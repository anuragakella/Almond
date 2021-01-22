package com.almondlang.errors;

import com.almondlang.almond.Token;

public class FunctionNotCallable extends RuntimeException {
    public Token errtok;
    public FunctionNotCallable(Token errtok, String message) {
        super(message);
        this.errtok = errtok;
    }
}
