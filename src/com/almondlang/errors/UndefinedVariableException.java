package com.almondlang.errors;

import com.almondlang.almond.Token;

public class UndefinedVariableException extends RuntimeException{
    public String name;
    public UndefinedVariableException(String name, String message) {
        super(message);
        this.name = name;
    }
}
