package com.almondlang.functions;

import com.almondlang.almond.Interpreter;
import com.almondlang.almondtypes.AlmondNone;
import com.almondlang.store.Store;
import com.almondlang.syntax.Statement;

import java.util.ArrayList;


// generic function method that represents an almond function
// all user defined functions are 'compiled' into this class before execution
public class AlmondFunction implements AlmondCallable {
    private final Statement.FunctionDeclarationStatement declarationStatement;
    private final Store capture;
    private int arity = 0;

    public AlmondFunction(Statement.FunctionDeclarationStatement declarationStatement, Store capture) {
        this.declarationStatement = declarationStatement;
        this.arity = declarationStatement.params.size();
        this.capture = capture;
    }

    @Override
    public int arity() {
        return arity;
    }

    @Override
    public Object call(Interpreter interpreter, ArrayList<Object> args) {
        Store localStore = new Store(capture);
        for(int i = 0; i < declarationStatement.params.size(); i++) {
            localStore.define(declarationStatement.params.get(i).tokenString, args.get(i));
        }
        try {
            interpreter.executeBlock(declarationStatement.body, localStore);
        } catch (ReturnValue val) {
            return val.val;
        }
        return new AlmondNone();
    }
    public String toString(){
        return "AlmondFunction:" + declarationStatement.name.tokenString;
    }
}
