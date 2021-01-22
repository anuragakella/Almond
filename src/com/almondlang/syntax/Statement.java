package com.almondlang.syntax;


import com.almondlang.almond.Token;

import java.util.ArrayList;

// statement nodes
// they are the nodes that get 'executed' by the interpreter
// they hold smaller statements or expressions or other values
public abstract class Statement {
    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        <R> R visitExpressionStatement(ExpressionStatement statement);
        <R> R visitPrintStatement(Print statement);
        <R> R visitVariableStatement(VariableDeclStatement variableDeclStatement);
        <R> R visitBlockStatement(Block block);
        <R> R visitIfStatement(IfStatement ifStatement);
        <R> R visitIfBlockStatement(IfBlockStatement ifBlockStatement);
        <R> R visitWhileStatement(WhileStatement whileStatement);
        <R> R visitWhileSingleStatement(WhileSingleStatement whileSingleStatement);
        <R> R visitFunctionDeclarationStatement(FunctionDeclarationStatement functionDeclarationStatement);
        <R> R visitReturnStatement(ReturnStatement returnStatement);
    }

    public static class ExpressionStatement extends Statement {
        public ExpressionStatement(Expression expression) {
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }

        public final Expression expression;
    }

    public static class Print extends Statement {
        public Print(Expression expression) {
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStatement(this);
        }

        public final Expression expression;
    }

    public static class VariableDeclStatement extends Statement {

        public VariableDeclStatement(Expression initializer, Token name) {
            this.initializer = initializer;
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableStatement(this);
        }

        public final Expression initializer;
        public final Token name;
    }

    public static class Block extends Statement {
        public Block(ArrayList<Statement> statements) {
            this.statements = statements;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }

        public final ArrayList<Statement> statements;
    }

    public static class IfStatement extends Statement {
        public IfStatement(Expression expression, Statement ifStatement, Statement elseStatement) {
            this.expression = expression;
            this.ifStatement = ifStatement;
            this.elseStatement = elseStatement;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }

        public final Expression expression;
        public final Statement ifStatement;
        public final Statement elseStatement;
    }

    public static class IfBlockStatement extends Statement {
        public IfBlockStatement(Expression expression, Block ifBlock, Block elseBlock) {
            this.expression = expression;
            this.ifBlock = ifBlock;
            this.elseBlock = elseBlock;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfBlockStatement(this);
        }

        public final Expression expression;
        public final Block ifBlock;
        public final Block elseBlock;
    }

    public static class WhileStatement extends Statement {

        public WhileStatement(Expression condition, Block whileBlock) {
            this.condition = condition;
            this.whileBlock = whileBlock;

        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStatement(this);
        }

        public final Expression condition;
        public final Block whileBlock;
    }

    public static class WhileSingleStatement extends Statement {

        public WhileSingleStatement(Expression condition, Statement whileStatement) {
            this.condition = condition;
            this.whileStatement = whileStatement;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileSingleStatement(this);
        }

        public final Expression condition;
        public final Statement whileStatement;
    }

    public static class FunctionDeclarationStatement extends Statement {


        public FunctionDeclarationStatement(Token name, ArrayList<Token> params, ArrayList<Statement> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionDeclarationStatement(this);
        }

        public final Token name;
        public final ArrayList<Token> params;
        public final ArrayList<Statement> body;
    }

    public static class ReturnStatement extends Statement {


        public ReturnStatement(Expression returnExpression, Token locationToken) {
            this.returnExpression = returnExpression;
            this.locationToken = locationToken;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStatement(this);
        }

        public final Expression returnExpression;
        public final Token locationToken;
    }

}