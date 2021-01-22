package com.almondlang.syntax;

import com.almondlang.almond.Token;
import com.almondlang.almondtypes.AlmondType;

import java.util.ArrayList;

// The AST node classes
// they form the nodes of the ASt
// Expressions only, they are sub nodes for statement nodes
public abstract class Expression {
    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitBinaryExpression(Binary exp);
        R visitUnaryExpression(Unary exp);
        R visitLiteralExpression(Literal exp);
        R visitGroupExpression(Group exp);
        R visitPoundExpression(Pound pound);
        R visitVariableExpression(VariableExpression variableExpression);
        R visitAssignmentExpression(AssignmentExpression assignmentExpression);
        R visitLogicalExpression(LogicalExpression logicalExpression);
        R visitFunctionCallExpression(FunctionCallExpression functionCallExpression);
    }
    public static class Binary extends Expression {
        public final Expression left;
        public final Expression right;
        public final Token op;

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }

        public Binary(Expression left, Token op, Expression right) {
            this.left = left;
            this.right = right;
            this.op = op;
        }
    }

    public static class Group extends Expression {
        public final Expression expression;

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupExpression(this);
        }

        public Group(Expression expression) {
            this.expression = expression;
        }
    }

    public static class Unary extends Expression {
        public final Token op;
        public final Expression right;

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }

        public Unary(Token op, Expression right) {
            this.right = right;
            this.op = op;
        }
    }

    public static class Literal extends Expression {
        public final AlmondType literal;

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }

        public Literal(AlmondType literal) {
            this.literal = literal;
        }
    }

    public static class Pound extends Expression {
        public final Expression right;

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPoundExpression(this);
        }

        public Pound(Expression right) {
            this.right = right;
        }
    }

    public static class VariableExpression extends Expression {
        public final Token name;

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpression(this);
        }

        public VariableExpression(Token name) {
            this.name = name;
        }
    }

    public static class AssignmentExpression extends Expression {
        public final Token name;
        public final Expression value;

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignmentExpression(this);
        }

        public AssignmentExpression(Token name, Expression value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class LogicalExpression extends Expression {
        public final Token op;
        public final Expression left, right;

        public LogicalExpression(Token op, Expression left, Expression right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpression(this);
        }
    }

    public static class FunctionCallExpression extends Expression {
        public final Expression callee;
        public final Token paren;
        public final ArrayList<Expression> params;

        public FunctionCallExpression(Expression callee, Token paren, ArrayList<Expression> params) {
            this.callee = callee;
            this.paren = paren;
            this.params = params;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionCallExpression(this);
        }
    }
}


