package com.almondlang.almond;

import com.almondlang.functions.AlmondFunction;
import com.almondlang.functions.AlmondNatives;
import com.almondlang.almondtypes.*;
import com.almondlang.store.Store;
import com.almondlang.errors.AlmondRuntimeException;
import com.almondlang.errors.UndefinedVariableException;
import com.almondlang.errors.VariableRedefinedException;
import com.almondlang.functions.AlmondCallable;
import com.almondlang.functions.ReturnValue;
import com.almondlang.syntax.Expression;
import com.almondlang.syntax.Statement;
import com.almondlang.tools.Messages;

import java.util.ArrayList;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Object> {
    ArrayList<Statement> statements;
    public Store globalStore = new Store();
    private Store environment = globalStore;
    boolean isRepl;

    public Interpreter(ArrayList<Statement> statements, boolean replMode) {
        globalStore.define("time", new AlmondNatives.Time());
        globalStore.define("print", new AlmondNatives.Print());
        this.statements = statements;
        this.isRepl = replMode;
    }

    public void interpret() {
        try {
            for (Statement statement : statements) {
                run(statement);
            }
        } catch (AlmondRuntimeException err) {
            Messages.handleError(err);
        } catch (VariableRedefinedException err) {
            Messages.handleError(err);
        }catch (UndefinedVariableException err){
            Messages.handleError(err);
        }catch (RuntimeException err) {
            Messages.handleError(err);
        }
    }

    private void run(Statement statement) {
        Object o = statement.accept(this);
        if(this.isRepl)
            System.out.println(stringFormat(o));
    }

    private String stringFormat(Object data) {
        if(data instanceof AlmondNone || data == null) return "none";
        return data.toString();
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary exp) {
        Object e_l = evaluate(exp.left);
        Object e_r = evaluate(exp.right);
        boolean d = false;
        char c = '0';
        // 0 int int
        // 1 int double
        // 2 double int
        // 3 double double
        if (e_l instanceof AlmondInt && e_r instanceof AlmondInt) c = '0';
        else if (e_l instanceof AlmondDouble || e_r instanceof AlmondDouble) {
            if (e_l instanceof AlmondInt) c = '1';
            else if (e_r instanceof AlmondInt) c = '2';
        }
        else c = '3';
        switch (exp.op.tokenType) {
            case PLUS:
                if(e_l instanceof AlmondString) {
                    if(e_r instanceof AlmondInt) return new AlmondString(((AlmondString) e_l).data + ((AlmondInt) e_r).data);
                    else if(e_r instanceof AlmondDouble) return new AlmondString(((AlmondString) e_l).data + ((AlmondDouble) e_r).data);
                } else if(e_r instanceof AlmondString) {
                    if(e_l instanceof AlmondInt) return new AlmondString(((AlmondString) e_r).data + ((AlmondInt) e_l).data);
                    else if(e_l instanceof AlmondDouble) return new AlmondString(((AlmondDouble) e_l).data + ((AlmondString) e_r).data);
                }
                if (e_r instanceof AlmondString && e_l instanceof AlmondString) {
                    return new AlmondString(((AlmondString) e_l).data + ((AlmondString) e_r).data);
                } else {
                    validateBOperands(exp.op, e_l, e_r);
                    switch (c) {
                        case '0':
                            return new AlmondInt(((AlmondInt) e_l).data + ((AlmondInt) e_r).data);
                        case '1':
                            return new AlmondDouble(((AlmondInt) e_l).data + ((AlmondDouble) e_r).data);
                        case '2':
                            return new AlmondDouble(((AlmondDouble) e_l).data + ((AlmondInt) e_r).data);
                        case '3':
                            return new AlmondDouble(((AlmondDouble) e_l).data + ((AlmondDouble) e_r).data);
                    }
                } break;
            case MINUS:
                validateBOperands(exp.op, e_l, e_r);
                switch (c) {
                    case '0': return new AlmondInt(((AlmondInt) e_l).data - ((AlmondInt) e_r).data);
                    case '1': return new AlmondDouble(((AlmondInt) e_l).data - ((AlmondDouble) e_r).data);
                    case '2': return new AlmondDouble(((AlmondDouble) e_l).data - ((AlmondInt) e_r).data);
                    case '3': return new AlmondDouble(((AlmondDouble) e_l).data - ((AlmondDouble) e_r).data);
                }
            case STAR:
                validateBOperands(exp.op, e_l, e_r);
                switch (c) {
                    case '0': return new AlmondInt(((AlmondInt) e_l).data * ((AlmondInt) e_r).data);
                    case '1': return new AlmondDouble(((AlmondInt) e_l).data * ((AlmondDouble) e_r).data);
                    case '2': return new AlmondDouble(((AlmondDouble) e_l).data * ((AlmondInt) e_r).data);
                    case '3': return new AlmondDouble(((AlmondDouble) e_l).data * ((AlmondDouble) e_r).data);
                }
            case SLASH:
                validateBOperands(exp.op, e_l, e_r);
                try {
                    switch (c) {
                        case '0': return new AlmondInt(((AlmondInt) e_l).data / ((AlmondInt) e_r).data);
                        case '1': return new AlmondDouble(((AlmondInt) e_l).data / ((AlmondDouble) e_r).data);
                        case '2': return new AlmondDouble(((AlmondDouble) e_l).data / ((AlmondInt) e_r).data);
                        case '3': return new AlmondDouble(((AlmondDouble) e_l).data / ((AlmondDouble) e_r).data);
                    }
                } catch (ArithmeticException e) {
                    Messages.handleError(new AlmondRuntimeException(exp.op, e.getMessage()));
                    return null;
                }
            case GREATER:
                validateBOperands(exp.op, e_l, e_r);
                switch (c) {
                    case '0': return new AlmondBoolean(((AlmondInt) e_l).data > ((AlmondInt) e_r).data);
                    case '1': return new AlmondBoolean(((AlmondInt) e_l).data > ((AlmondDouble) e_r).data);
                    case '2': return new AlmondBoolean(((AlmondDouble) e_l).data > ((AlmondInt) e_r).data);
                    case '3': return new AlmondBoolean(((AlmondDouble) e_l).data > ((AlmondDouble) e_r).data);
                }
            case LESS:
                validateBOperands(exp.op, e_l, e_r);
                switch (c) {
                    case '0': return new AlmondBoolean(((AlmondInt) e_l).data < ((AlmondInt) e_r).data);
                    case '1': return new AlmondBoolean(((AlmondInt) e_l).data < ((AlmondDouble) e_r).data);
                    case '2': return new AlmondBoolean(((AlmondDouble) e_l).data < ((AlmondInt) e_r).data);
                    case '3': return new AlmondBoolean(((AlmondDouble) e_l).data < ((AlmondDouble) e_r).data);
                }
            case GREATER_EQUAL:
                validateBOperands(exp.op, e_l, e_r);
                switch (c) {
                    case '0': return new AlmondBoolean(((AlmondInt) e_l).data >= ((AlmondInt) e_r).data);
                    case '1': return new AlmondBoolean(((AlmondInt) e_l).data >= ((AlmondDouble) e_r).data);
                    case '2': return new AlmondBoolean(((AlmondDouble) e_l).data >= ((AlmondInt) e_r).data);
                    case '3': return new AlmondBoolean(((AlmondDouble) e_l).data >= ((AlmondDouble) e_r).data);
                }
            case LESS_EQUAL:
                validateBOperands(exp.op, e_l, e_r);
                switch (c) {
                    case '0': return new AlmondBoolean(((AlmondInt) e_l).data <= ((AlmondInt) e_r).data);
                    case '1': return new AlmondBoolean(((AlmondInt) e_l).data <= ((AlmondDouble) e_r).data);
                    case '2': return new AlmondBoolean(((AlmondDouble) e_l).data <= ((AlmondInt) e_r).data);
                    case '3': return new AlmondBoolean(((AlmondDouble) e_l).data <= ((AlmondDouble) e_r).data);
                }
            case EQUAL_EQUAL:
                switch (c) {
                    case '0': return new AlmondBoolean(((AlmondInt) e_l).data == ((AlmondInt) e_r).data);
                    case '1': return new AlmondBoolean(((AlmondInt) e_l).data == ((AlmondDouble) e_r).data);
                    case '2': return new AlmondBoolean(((AlmondDouble) e_l).data == ((AlmondInt) e_r).data);
                    case '3': return new AlmondBoolean(((AlmondDouble) e_l).data == ((AlmondDouble) e_r).data);
                }
            case BANG_EQUAL:
                switch (c) {
                    case '0': return new AlmondBoolean(((AlmondInt) e_l).data != ((AlmondInt) e_r).data);
                    case '1': return new AlmondBoolean(((AlmondInt) e_l).data != ((AlmondDouble) e_r).data);
                    case '2': return new AlmondBoolean(((AlmondDouble) e_l).data != ((AlmondInt) e_r).data);
                    case '3': return new AlmondBoolean(((AlmondDouble) e_l).data != ((AlmondDouble) e_r).data);
                }
        }
        return null;
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary exp) {
        Object e = evaluate(exp.right);
        switch (exp.op.tokenType) {
            case MINUS:
                if (e instanceof AlmondInt){
                    return -((AlmondInt)e).data;
                } else if ( e instanceof  AlmondDouble) {
                    return -((AlmondDouble)e).data;
                }
            case BANG:
                if (e instanceof AlmondBoolean){
                    return !((AlmondBoolean)e).data;
                }
        }
        return null;
    }

    @Override
    public Object visitLiteralExpression(Expression.Literal exp) {
        return exp.literal;
    }

    @Override
    public Object visitGroupExpression(Expression.Group exp) {
        return evaluate(exp.expression);
    }

    @Override
    public Object visitPoundExpression(Expression.Pound pound) {
        Object e = evaluate(pound.right);
        if(e instanceof AlmondString) return ((AlmondString) e).typeOf();
        else if(e instanceof AlmondDouble) return ((AlmondDouble) e).typeOf();
        else if(e instanceof AlmondInt) return ((AlmondInt) e).typeOf();
        else if(e instanceof AlmondBoolean) return ((AlmondBoolean) e).typeOf();
        else return e.getClass().getSimpleName();
    }

    @Override
    public Object visitVariableExpression(Expression.VariableExpression variableExpression) {
        return globalStore.get(variableExpression.name.tokenString);
    }

    @Override
    public Object visitAssignmentExpression(Expression.AssignmentExpression assignmentExpression) {
        globalStore.reAssign(assignmentExpression.name.tokenString, evaluate(assignmentExpression.value));
        return null;
    }

    @Override
    public Object visitLogicalExpression(Expression.LogicalExpression logicalExpression) {
        switch (logicalExpression.op.tokenType) {
            case AND:
                return new AlmondBoolean(((AlmondBoolean)evaluate(logicalExpression.left)).data && ((AlmondBoolean)evaluate(logicalExpression.right)).data);
            case OR:
                return new AlmondBoolean(((AlmondBoolean)evaluate(logicalExpression.left)).data || ((AlmondBoolean)evaluate(logicalExpression.right)).data);
        }
        return null;
    }



    public Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    public Object evaluate(Statement statement) {
        return statement.accept(this);
    }

    private void validateBOperands(Token token, Object left, Object right){
        if((left instanceof AlmondDouble && right instanceof AlmondDouble) || (left instanceof AlmondInt && right instanceof AlmondInt)
            || (left instanceof AlmondDouble && right instanceof AlmondInt) || (left instanceof AlmondDouble && right instanceof AlmondInt) ||
                left instanceof AlmondDouble || left instanceof AlmondInt || left instanceof AlmondDouble || left instanceof AlmondInt
        ) return;
        throw new AlmondRuntimeException(token, "operands must be numbers expected: (AlmondInt, AlmondDouble): got: " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
    }
    private void validateUOperands(Token token, Object right){
        if((right instanceof AlmondDouble) || (right instanceof AlmondInt)) return;
        throw new AlmondRuntimeException(token, "operands must be numbers expected: (AlmondInt, AlmondDouble): got: " + right.getClass().getSimpleName());
    }

    @Override
    public Object visitExpressionStatement(Statement.ExpressionStatement statement) {
        Object o = evaluate(statement.expression);
        if(isRepl) return o;
        return null;
    }

    @Override
    public Object visitPrintStatement(Statement.Print statement) {
        Object val = evaluate(statement.expression);
        System.out.println(stringFormat(val));
        return null;
    }

    @Override
    public Object visitVariableStatement(Statement.VariableDeclStatement variableDeclStatement) {
        Object val = null;
        if(variableDeclStatement.initializer != null) {
            val = evaluate(variableDeclStatement.initializer);
        }
        globalStore.define(variableDeclStatement.name.tokenString, val);
        return null;
    }

    public void executeBlock(ArrayList<Statement> statements, Store localStore) {
        Store previous = this.globalStore;
        this.globalStore = localStore;
        try {
            for(Statement statement : statements) {
                evaluate(statement);
            }
        } finally {
            this.globalStore = previous;
        }
    }

    @Override
    public Object visitBlockStatement(Statement.Block block) {
        executeBlock(block.statements, new Store(globalStore));
        return null;
    }

    @Override
    public Object visitIfBlockStatement(Statement.IfBlockStatement ifBlockStatement) {
        if(((AlmondBoolean)evaluate(ifBlockStatement.expression)).data) {
            evaluate(ifBlockStatement.ifBlock);
        } else if(ifBlockStatement.elseBlock != null) {
            evaluate(ifBlockStatement.elseBlock);
        };
        return null;
    }

    @Override
    public Object visitWhileStatement(Statement.WhileStatement whileStatement) {
        while(((AlmondBoolean)evaluate(whileStatement.condition)).data){
            evaluate(whileStatement.whileBlock);
        }
        return null;
    }

    @Override
    public Object visitWhileSingleStatement(Statement.WhileSingleStatement whileSingleStatement) {
        while(((AlmondBoolean)evaluate(whileSingleStatement.condition)).data){
            evaluate(whileSingleStatement.whileStatement);
        }
        return null;
    }

    @Override
    public Object visitIfStatement(Statement.IfStatement ifStatement) {
        if(((AlmondBoolean)evaluate(ifStatement.expression)).data) {
            evaluate(ifStatement.ifStatement);
        } else if(ifStatement.elseStatement != null) {
            evaluate(ifStatement.elseStatement);
        }
        return null;
    }

    @Override
    public Object visitFunctionDeclarationStatement(Statement.FunctionDeclarationStatement functionDeclarationStatement) {
        AlmondFunction func = new AlmondFunction(functionDeclarationStatement, globalStore);
        globalStore.define(functionDeclarationStatement.name.tokenString, func);
        return null;
    }

    @Override
    public Object visitReturnStatement(Statement.ReturnStatement returnStatement) {
        Object val = returnStatement.returnExpression == null ? null : evaluate(returnStatement.returnExpression);
        throw new ReturnValue(val);
    }

    @Override
    public Object visitFunctionCallExpression(Expression.FunctionCallExpression functionCallExpression) {
        Object callee = evaluate(functionCallExpression.callee);

        ArrayList<Object> args = new ArrayList<>();
        for (Expression arg : functionCallExpression.params) {
            args.add(evaluate(arg));
        }
        if(!(callee instanceof AlmondCallable)) throw new AlmondRuntimeException(functionCallExpression.paren, "can't call non callable");
        AlmondCallable func = (AlmondCallable)callee;
        if(args.size() != func.arity()) {
            throw new AlmondRuntimeException(functionCallExpression.paren, "Expected " + func.arity() + " arguments but got " + args.size() + " arguments");
        }
        return func.call(this, args);
    }
}
