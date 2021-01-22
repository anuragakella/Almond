package com.almondlang.almond;

public enum TokenType {
    // single-char
    LEFT_PAREN, RIGHT_PAREN, COLON,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, BANG, GREATER, LESS, EQUAL, POUND,

    // multi-char
    BANG_EQUAL,
    EQUAL_EQUAL,
    GREATER_EQUAL,
    LESS_EQUAL,
    PLUS_EQUAL,
    MINUS_EQUAL,
    SLASH_EQUAL,
    STAR_EQUAL,


    // literals.
    IDENTIFIER, STRING, NUMBER,

    // keywords
    AND, CLASS, ELSE, FALSE, FUNCTION, FOR, IF, NONE, OR,
    PRINT, RETURN, THIS, TRUE, VAR, WHILE, END,

    NEWLINE,
    ENDFILE
};