package com.almondlang.errors;

import com.almondlang.almond.Token;

public class AlmondRuntimeException extends RuntimeException {
    public Token op;
    public AlmondRuntimeException(Token op, String s) {
        super(s);
        this.op = op;
    }
}
