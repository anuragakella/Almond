package com.almondlang.almond;

import com.almondlang.almondtypes.AlmondType;

public class Token {
    public int line, col;
    public String  tokenString;
    public AlmondType tokenValue;
    public TokenType tokenType;
    public Token(int l, int c, String s, AlmondType a, TokenType t) {
        this.line = l;
        this.col = c;
        this.tokenString = s;
        this.tokenValue = a;
        this.tokenType = t;
    }
    public String toString() {
        return "["+ tokenString + " : " + tokenValue + " : " + tokenType + "]";
    }
}
