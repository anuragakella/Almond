package com.almondlang.almond;

import com.almondlang.almondtypes.AlmondBoolean;
import com.almondlang.almondtypes.AlmondNone;
import com.almondlang.syntax.*;
import com.almondlang.tools.Messages;

import java.util.ArrayList;
import java.util.List;

//Parses scanned tokens into an abstract syntax tree and then dumps each 'statement' into an arraylist
public class Parser {
    //parse error, thrown if it comes across any weird tokens
    private static class ParseError extends RuntimeException {}

    // main list that will store all of the execurable 'statements'
    private final ArrayList<Token> tokenList;
    private int currentTok;

    // constructor
    public Parser(ArrayList<Token> tokenList){
        this.tokenList = tokenList;
        this.currentTok = 0;
    }

    // top down parsing, the parser runs through the methods recursively: follows language's grammar
    public ArrayList<Statement> parse() {
        ArrayList<Statement> statements = new ArrayList<>();
        while (!tokensEnd()) {
            Statement s = declaration();
            if (s == null) {
                continue;
            }
            statements.add(s);
        }
        return statements;
    }

    // declaration statements
    private Statement declaration() {
        try{
            if(match(TokenType.VAR)) return variableDeclaration();
            return statement();
        } catch (ParseError e) {
            // sync method, it it finds any weird tokens it 'syncs' by looping through until it finds a legal start token
            sync();
            System.out.println(e.getMessage());
            return null;
        }
    }



    // declaring variables
    private Statement variableDeclaration() {
        Token name;
        if(match(TokenType.IDENTIFIER)) name = getOffset(-1);
        else throw parseError(getOffset(-1), "you need to name a variable after using 'var'");

        Expression init = null;
        if(match(TokenType.EQUAL)) {
            init = expression();
        }
        return new Statement.VariableDeclStatement(init, name);
    }

    // statements, functions, if... etc
    private Statement statement() {
        if(match(TokenType.FUNCTION)) return function("function");
        if(match(TokenType.IF)) return ifStatement();
        if(match(TokenType.WHILE)) return whileStatement();
        if(match(TokenType.FOR)) return forStatement();
        if(match(TokenType.PRINT)) return printStatement();
        if(match(TokenType.RETURN)) return returnStatement();
        return expressionStatement();
    }


    private Statement.FunctionDeclarationStatement function(String type) {
        // it runs this method when it finds a function keyword
        // then it looks for the name and creates an ArrayList of arguments
        // then comes the function body
        // then packages it in a function statement and sends it off into the statements list
        if(!match(TokenType.IDENTIFIER)) throw parseError(getOffset(-1), "Expected a" + type + " name");
        Token name = getOffset(-1), paren;
        ArrayList<Token> params = new ArrayList<>();
        ArrayList<Statement> funcBlock = new ArrayList<>();
        if(match(TokenType.LEFT_PAREN)) {
            // if it matches a right paren right away, the args are empty.
            // so no need to parse args
            if(!match(TokenType.RIGHT_PAREN)){
                do {
                    if(!match(TokenType.IDENTIFIER)) throw parseError(peek(), "Expected a parameter name");
                    Token p = getOffset(-1);
                    params.add(p);
                } while (match(TokenType.COMMA));
                if (params.size() >= 255) {
                    Messages.reportError(peek().line, peek().col, "Parser", "Can't have more than 255 parameters.");
                }
                if (!match(TokenType.RIGHT_PAREN)) throw parseError(peek(), "Missing a ')' after a function header");
            }
            if (!match(TokenType.COLON)) throw parseError(peek(), "Missing a ':' after a function header");
            if (!match(TokenType.NEWLINE)) throw parseError(peek(), "Missing a newline after a function header");
            while(!tokensEnd()){
                if(match(TokenType.END)) break;
                Statement s = statement();
                if(s == null) continue;
                funcBlock.add(s);
            }
            if(tokensEnd()) throw parseError(getOffset(-1), "'end' Expected after function block");
            // newline after headers and end keywords is a must!
            if(!match(TokenType.NEWLINE)) throw parseError(getOffset(-1), "newline expected after 'end'");
        }
        return new Statement.FunctionDeclarationStatement(name, params, funcBlock);
    }

    private Statement forStatement() {
        // parses a for loop
        // looks for a left paren, then parses an initializer,
        // then the condition and increment exp
        // and then finally, the body
        // packaged into a for statement in the end
        if(!match(TokenType.LEFT_PAREN)) throw parseError(getOffset(-1), "'(' Expected after 'for'");
        Statement initializer;
        // initializer can be a new var or just a re assinging exp for an existing var
        if (match(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (match(TokenType.VAR)) {
            initializer = variableDeclaration();
        } else {
            initializer = new Statement.ExpressionStatement(expression());
        }
        // if there's no init or condition or incrmeent, the loops still parses into an infinte loop and ignores the conditions
        if(!match(TokenType.SEMICOLON)) throw parseError(getOffset(-1), "';' Expected after loop initializer");
        Expression condition = null;
        if(!match(TokenType.SEMICOLON)) {
            condition = expression();
        }
        if(!match(TokenType.SEMICOLON)) throw parseError(getOffset(-1), "';' Expected after loop condition");
        Expression incr = null;
        if(!match(TokenType.RIGHT_PAREN)){
            incr = expression();
        }
        if(!match(TokenType.RIGHT_PAREN)) throw parseError(getOffset(-1), "')' Expected after for loop header");
        if(!match(TokenType.COLON)) throw parseError(getOffset(-1), "':' Expected after for loop header");
        if(!match(TokenType.NEWLINE)) throw parseError(getOffset(-1), "newline Expected after for loop header");
        ArrayList<Statement> forBlock = new ArrayList<>();
        while(!tokensEnd()){
            if(match(TokenType.END)) break;
            Statement s = statement();
            if (s == null) continue;
            forBlock.add(s);
        }
        if(tokensEnd()) throw parseError(getOffset(-1), "'end' Expected after while block");
        if(!match(TokenType.NEWLINE)) throw parseError(getOffset(-1), "newline expected after 'end'");
        // de sugaring
        // the for loop is a wrapper around the while loop backend
        // it's parsed into a while loop and sent off as a while loop
        // the interpreter can't tell the difference between a for and a while loop
        // in fact, it doesn't even know that a for loop exists!
        forBlock.add(new Statement.ExpressionStatement(incr));
        Statement whileBlock = new Statement.WhileStatement(condition, new Statement.Block(forBlock));
        ArrayList<Statement> newBlock = new ArrayList<>();
        newBlock.add(initializer);
        newBlock.add(whileBlock);
        return new Statement.Block(newBlock);
    }

    // while loop
    private Statement whileStatement() {
        // parses the while loop
        // simple, looks for ( then a condition and then a )
        // then a block which in almond, unofficialy is : ..... end (instead of { ... } in C-like languages)
        if(!match(TokenType.LEFT_PAREN)) throw parseError(getOffset(-1), "'(' Expected after 'while'");
        Expression e = expression();
        if(!match(TokenType.RIGHT_PAREN)) throw parseError(getOffset(-1), "')' Expected after a while condition");
        if(match(TokenType.COLON) && match(TokenType.NEWLINE)) {
            ArrayList<Statement> whileBlock = new ArrayList<>();
            while(!tokensEnd()){
                if(match(TokenType.END)) break;
                Statement s = statement();
                if (s == null) continue;
                whileBlock.add(s);
            }
            if(tokensEnd()) throw parseError(getOffset(-1), "'end' Expected after while block");
            if(!match(TokenType.NEWLINE)) throw parseError(getOffset(-1), "newline expected after 'end'");
            return new Statement.WhileStatement(e, new Statement.Block(whileBlock));
        }
        Statement whileStmt = statement();
        return new Statement.WhileSingleStatement(e, whileStmt);
    }

    // if else
    private Statement ifStatement() {
        // if else in almond:
        // if and then a condition and a statement without a block <- the short if else
        // if and a colon and a block <- long if
        // if you use an else after an if block you dont have to end the block with an end keyword
        // use an else instead
        if(!match(TokenType.LEFT_PAREN)) throw parseError(getOffset(-1), "'(' Expected after 'if'");
        Expression e = expression();
        if(!match(TokenType.RIGHT_PAREN)) throw parseError(getOffset(-1), "')' Expected after if condition");
        if(match(TokenType.COLON) && match(TokenType.NEWLINE)) {
            ArrayList<Statement> ifblock = new ArrayList<>();
            ArrayList<Statement> elseArr = new ArrayList<>();
            Statement.Block elseB = null;
            while(!tokensEnd()){
                Statement s = statement();
                if (s == null) continue;
                ifblock.add(s);
                if(match(TokenType.END)) break;
                if(match(TokenType.ELSE)) {
                    elseArr = elseBlock();
                    elseB = new Statement.Block(elseArr);
                    break;
                }
            }
            if(tokensEnd()) throw parseError(getOffset(-1), "'end' Expected after if/else block");
            if(!match(TokenType.NEWLINE)) throw parseError(getOffset(-1), "newline expected after 'end'");
            return new Statement.IfBlockStatement(e, new Statement.Block(ifblock), elseB);
        }
        if(peek().tokenType == TokenType.NEWLINE) next();
        Statement ifBranch = statement();
        Statement elseBranch = null;
        if(match(TokenType.ELSE)) {
            if(peek().tokenType == TokenType.NEWLINE) next();
            elseBranch = statement();
        }
        return new Statement.IfStatement(e, ifBranch, elseBranch);
    }

    private ArrayList<Statement> elseBlock() {
        // parses the else block
        ArrayList<Statement> elseBlock = new ArrayList<>();
        if(!(match(TokenType.COLON) && match(TokenType.NEWLINE))) throw parseError(getOffset(-1), ": and newline expected after 'else'");
        while (!match(TokenType.END) && !tokensEnd()) {
            Statement s2 = statement();
            if (s2 == null) continue;
            elseBlock.add(s2);
        }
        if(tokensEnd()) throw parseError(getOffset(-1), "'end' Expected after else block");
        return elseBlock;
    }

    private Statement printStatement() {
        // print statement
        // a 'statement' not a native function like it is in most languages
        // but a print() function also works, which I implemented later after functions were implemented
        Expression e = expression();
        if (match(TokenType.NEWLINE))
            return new Statement.Print(e);
        throw parseError(getOffset(-1), "you can't run 2 statements on one line");
    }

    // return statements, throws a custom 'error' object
    // the custom object just delivers the value to the caller
    // no errors are reported
    private Statement returnStatement() {
        Expression e = expression();
        if(match(TokenType.NEWLINE)) return new Statement.ReturnStatement(e, getOffset(-1));
        throw parseError(getOffset(-1), "you can't run 2 statements on one line");
    }

    private Statement expressionStatement() {
        Expression e = expression();
        if(e == null) {
            next();
            return null;
        }
        if(match(TokenType.NEWLINE)) return new Statement.ExpressionStatement(e);
        throw parseError(getOffset(-1), "you can't run 2 statements on one line");
    }


    // these methods top down parse an expression
    // top down parsing - so the level of precedence is bottom up (high-bottom to low-up)
    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        Expression e = or();
        if(match(TokenType.EQUAL)) {
            Token eq = getOffset(-1);
            Expression val = assignment();
            if (e instanceof Expression.VariableExpression) {
                Token name = ((Expression.VariableExpression) e).name;
                return new Expression.AssignmentExpression(name, val);
            }
            Messages.reportError(peek().line, peek().col, peek().tokenString, "invalid assignment expression");
        }
        // shortcut assignment operators
        // creates a new binary expression node for the RHS and adds the LHS
        else if(match(TokenType.PLUS_EQUAL)) return shortAssignExp("+", TokenType.PLUS, e);
        else if(match(TokenType.MINUS_EQUAL)) return shortAssignExp("-", TokenType.MINUS, e);
        else if(match(TokenType.SLASH_EQUAL)) return shortAssignExp("/", TokenType.SLASH, e);
        else if(match(TokenType.STAR_EQUAL)) return shortAssignExp("*", TokenType.STAR, e);
        return e;
    }

    // shortcut helper method
    private Expression shortAssignExp(String s, TokenType type, Expression e) {
        Token eq = getOffset(-1);
        Expression val = assignment();
        if (e instanceof Expression.VariableExpression) {
            Token name = ((Expression.VariableExpression) e).name;
            return new Expression.AssignmentExpression(name, new Expression.Binary(e, new Token(eq.line, eq.col, s, null, type) ,val));
        }
        Messages.reportError(peek().line, peek().col, peek().tokenString, "invalid assignment expression");
        return e;
    }

    // or - logical operator
    // print a or b
    private Expression or() {
        Expression e = and();
        while (match(TokenType.OR)) {
            Token op = getOffset(-1);
            Expression right = and();
            e = new Expression.LogicalExpression(op, e, right);
        }
        return e;
    }

    // and operator
    private Expression and() {
        Expression e = equality();
        while (match(TokenType.AND)) {
            Token op = getOffset(-1);
            Expression right = equality();
            e = new Expression.LogicalExpression(op, e, right);
        }
        return e;
    }

    private Expression equality() {
        Expression e = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = getOffset(-1);
            Expression right = comparison();
            e = new Expression.Binary(e, operator, right);
        }
        return e;
    }

    private Expression comparison() {
        Expression e = addsub();

        while (match(TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.GREATER)) {
            Token operator = getOffset(-1);
            Expression right = comparison();
            e = new Expression.Binary(e, operator, right);
        }

        return e;
    }

    private Expression addsub() {
        Expression e = muldiv();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = getOffset(-1);
            Expression right = comparison();
            e = new Expression.Binary(e, operator, right);
        }
        return e;
    }

    private Expression muldiv() {
        Expression e = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = getOffset(-1);
            Expression right = comparison();
            e = new Expression.Binary(e, operator, right);
        }
        return e;
    }

    private Expression unary() {
        if (match(TokenType.MINUS, TokenType.BANG)) {
            Token operator = getOffset(-1);
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }
        else if(match(TokenType.POUND) && match(TokenType.LEFT_PAREN)) {
            Expression right = expression();
            if (match(TokenType.RIGHT_PAREN)) {
                return new Expression.Pound(right);
            }
            else throw parseError(peek(), "u Missing a ')'");

        }
        return call();
    }

    // parses a function call
    // highest precedence
    private Expression call() {
        PrettyPrinter p = new PrettyPrinter();
        Expression e = literal();
        Token paren;
        ArrayList<Expression> args = new ArrayList<>();
        if(match(TokenType.LEFT_PAREN)) {
            do {
                Expression e2 = expression();
                if(e2 == null) continue;
                args.add(e2);
            } while (match(TokenType.COMMA));
            if(!match(TokenType.RIGHT_PAREN)) throw parseError(peek(), "Missing a ')' after a function call");
            paren = getOffset(-1);
            e = new Expression.FunctionCallExpression(e, paren, args);
        }
//        while (true) {
//             else break;
//        }
        return e;
    }

    // leaves of the syntax tree
    // literals, numbers true false values etc
    // if it comes across a token that it doesn't recoginize,
    // it just ignores it

    private Expression literal() {
        if(match(TokenType.FALSE)) return new Expression.Literal(new AlmondBoolean(false));
        if(match(TokenType.TRUE)) return new Expression.Literal(new AlmondBoolean(true));
        if(match(TokenType.NONE)) return new Expression.Literal(new AlmondNone());
        if(match(TokenType.NUMBER, TokenType.STRING)) return new Expression.Literal(getOffset(-1).tokenValue);
        if(match(TokenType.IDENTIFIER)) return new Expression.VariableExpression(getOffset(-1));
        if(match(TokenType.LEFT_PAREN)) {
            Expression e = expression();
            if (match(TokenType.RIGHT_PAREN)) return new Expression.Group(e);
            throw parseError(peek(), "Missing a ')'");
        }
        return null;
    }


    // helper methods that help with parsing
    private ParseError parseError(Token peek, String s) {
        Messages.reportError(peek().line, peek().col, "Parser", s);
        return new ParseError();
    }

    // reutns a token with the offset
    private Token getOffset(int n) {
        return tokenList.get(currentTok + n);
    }


    // currentTok points to the currently-being-parsed element
    // this method moves the pointer 1 token forward
    private Token next() {
        currentTok++;
        return tokenList.get(currentTok-1);
    }

    // checks for an ENDFILE token
    private boolean tokensEnd() {
        if(peek().tokenType == TokenType.ENDFILE) return true;
        return false;
    }


    // 'peeks' one token ahead without modifying the pointer
    private Token peek() {
        return tokenList.get(currentTok);
    }

    private boolean match(TokenType ...t){
        for (TokenType type : t) {
            if(!tokensEnd() && peek().tokenType == type) {
                next();
                return true;
            }
        }
        return false;
    }

    // sync method, described above
    private void sync() {
        Almond.hadParseError = true;
        while (!tokensEnd()) {
            if (getOffset(-1).tokenType == TokenType.NEWLINE || getOffset(-1).tokenType == TokenType.ENDFILE) return;

            switch (peek().tokenType) {
                case CLASS:
                case FUNCTION:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            next();
        }
    }


}
