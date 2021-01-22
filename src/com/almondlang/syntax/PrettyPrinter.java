package com.almondlang.syntax;


import com.almondlang.almond.Token;
import com.almondlang.almondtypes.AlmondNone;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;

// simple method that prints a flattened AST for the parsed statements
public class PrettyPrinter implements Expression.Visitor<String>, Statement.Visitor<String> {

    public String print(Statement statement) {
        if(statement == null) return null;
        return statement.accept(this);
    }

    public String print(Expression expression) {
        if(expression == null) return null;
        return expression.accept(this);
    }

    public void prettyprint(ArrayList<Statement> statements) {
        for (Statement statement : statements)
            System.out.println(print(statement));
    }

    @Override
    public String visitBinaryExpression(Expression.Binary exp) {
        return "(be " + exp.left.accept(this) + exp.op.tokenString + exp.right.accept(this) + ")";
    }

    @Override
    public String visitUnaryExpression(Expression.Unary exp) {
        return "(ue " + exp.op.tokenString + exp.right.accept(this) + ")";
    }

    @Override
    public String visitLiteralExpression(Expression.Literal exp) {
        String data;
        data = "" + exp.literal.getData();
        if(exp.literal instanceof AlmondNone) data = "none";
        return "(lit " + data + ")";
    }

    @Override
    public String visitGroupExpression(Expression.Group exp) {
        return "(ge " + exp.expression.accept(this) + ")";
    }

    @Override
    public String visitPoundExpression(Expression.Pound pound) {
        return "(typeof " + print(pound.right) + ")" ;
    }

    @Override
    public String visitVariableExpression(Expression.VariableExpression variableExpression) {
        return "(varexp " + variableExpression.name.tokenString + ")";
    }

    @Override
    public String visitAssignmentExpression(Expression.AssignmentExpression assignmentExpression) {
        return "(varassgn " + assignmentExpression.name.tokenString + " " + print(assignmentExpression.value) + ")";
    }

    @Override
    public String visitLogicalExpression(Expression.LogicalExpression logicalExpression) {
        return "(logical " + print(logicalExpression.left) + " " + logicalExpression.op.tokenString + " " + print(logicalExpression.right) + ")";
    }

    @Override
    public String visitFunctionCallExpression(Expression.FunctionCallExpression functionCallExpression) {
        String s = "(functioncall " + print(functionCallExpression.callee) + " ";
        for(Expression exp : functionCallExpression.params) {
            s += print(exp) + ", ";
        }
        return s + ")";
    }

    @Override
    public String visitExpressionStatement(Statement.ExpressionStatement statement) {
        return "(exp " + print(statement.expression) + ")";
    }

    @Override
    public String visitPrintStatement(Statement.Print statement) {
        return "(print " + print(statement.expression) + ")";
    }

    @Override
    public String visitVariableStatement(Statement.VariableDeclStatement variableDeclStatement) {
        return "(vardecl " + variableDeclStatement.name.tokenString + " " +  print(variableDeclStatement.initializer) + ")";
    }

    @Override
    public String visitBlockStatement(Statement.Block block) {
        String st =  "(block \n";
        for(Statement s : block.statements) {
            st += print(s) + "\n";
        }
        st += "end)";
        return st;
    }

    @Override
    public String visitIfStatement(Statement.IfStatement ifStatement) {
        return "(ifstmt " + print(ifStatement.expression) + "\n" + print(ifStatement.ifStatement) + "\nelsestmt\n  " + print(ifStatement.elseStatement) + "\nend)" ;
    }

    @Override
    public String visitIfBlockStatement(Statement.IfBlockStatement ifBlockStatement) {
        return "(ifblock " + print(ifBlockStatement.expression) + "\n" + print(ifBlockStatement.ifBlock) + "\nelsestmt\n  " + print(ifBlockStatement.elseBlock) + "\nend)" ;
    }

    @Override
    public String  visitWhileStatement(Statement.WhileStatement whileStatement) {
        return "(while " + print(whileStatement.condition) + "\n" + print(whileStatement.whileBlock) + "\nend)";
    }

    @Override
    public String visitWhileSingleStatement(Statement.WhileSingleStatement whileSingleStatement) {
        return "(while " + print(whileSingleStatement.condition) + "\n" + print(whileSingleStatement.whileStatement) + "\nend)";
    }

    @Override
    public String visitFunctionDeclarationStatement(Statement.FunctionDeclarationStatement functionDeclarationStatement) {
        String s = "(functiondecl ";
        for(Token exp : functionDeclarationStatement.params) {
            s += exp.tokenString + ", ";
        }
        s += "\n";
        for(Statement st : functionDeclarationStatement.body) {
            s += "\t" + print(st) + "\n";
        }
        return s + ")";
    }

    @Override
    public String visitReturnStatement(Statement.ReturnStatement returnStatement) {
        return "(returnstmt " + print(returnStatement.returnExpression) + ")";
    }
}
