package com.almondlang.store;

import com.almondlang.errors.UndefinedVariableException;
import com.almondlang.errors.VariableRedefinedException;

import java.util.HashMap;
import java.util.Map;

// the store class
// is a store for variables and functions
// aka environment
public class Store {
    Store parentEnv;

    public Store() {
        this.parentEnv = null;
    }

    public Store(Store parentEnv) {
        this.parentEnv = parentEnv;
    }

    public Map<String, Object> store = new HashMap<>();

    // define an new variable
    public void define(String name, Object value){
        if(store.containsKey(name))
            throw new VariableRedefinedException(name, "Variable already exists");
        store.put(name, value);
    }

    // assign variables with new values
    public void reAssign(String name, Object value) {
        if(store.containsKey(name)) {
            store.put(name, value);
            return;
        }
        if (parentEnv != null) {
            parentEnv.reAssign(name, value);
            return;
        }
        throw new UndefinedVariableException(name, "Undefined variable");
    }

    // get an existing variable
    public Object get(String name){
        if(store.containsKey(name))
            return store.get(name);
        if(parentEnv != null) return parentEnv.get(name);
        throw new UndefinedVariableException(name, "Undefined variable");
    }

}
