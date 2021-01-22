package com.almondlang.errors;

public class VariableRedefinedException extends RuntimeException{
    public String name;
    public VariableRedefinedException(String name, String message) {
        super(message);
        this.name = name;
    }
}
