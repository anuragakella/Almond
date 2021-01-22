package com.almondlang.almond;

import com.almondlang.almondtypes.AlmondDouble;
import com.almondlang.almondtypes.AlmondInt;
import com.almondlang.almondtypes.AlmondString;
import com.almondlang.almondtypes.AlmondType;

import static com.almondlang.tools.Messages.reportError;
import java.util.ArrayList;

public class Scanner {
    String src;
    private int previousChars = 0;
    private int current = 0, tokStart = 0, line = 1;
    ArrayList<Token> tokenList;
    public Scanner(String src) {
        this.src = src;
        tokenList = new ArrayList<>();
    }

    public ArrayList<Token> getTokenList() {
        return this.tokenList;
    }

    public void start() {
        while (!sourceEnd()) {
            tokStart = current;
            scan();
        }
        addToken(line, current - 1, "", null, TokenType.ENDFILE);
    }

    // uses a huge switch case to scan string 'pieces' into Token objects
    // each token has location and type info
    public void scan() {
        char chr = next();
        switch (chr)
        {
            case ' ':
            case '\r':
            case '\t':
                break;
            case ';': addToken(line, current - 1 - previousChars, TokenType.SEMICOLON); break;
            case '#': addToken(line, current - 1 - previousChars, TokenType.POUND); break;
            case '(': addToken(line, current - 1 - previousChars, TokenType.LEFT_PAREN); break;
            case ')': addToken(line, current - 1 - previousChars, TokenType.RIGHT_PAREN); break;
            case ':': addToken(line, current - 1 - previousChars, TokenType.COLON); break;
            case ',': addToken(line, current - 1 - previousChars, TokenType.COMMA); break;
            case '.': addToken(line, current - 1 - previousChars, TokenType.DOT); break;
            case '+': addToken(line, current - 1 - previousChars, peekCheck('=')?TokenType.PLUS_EQUAL:TokenType.PLUS); break;
            case '-': addToken(line, current - 1 - previousChars, peekCheck('=')?TokenType.MINUS_EQUAL:TokenType.MINUS); break;
            case '*': addToken(line, current - 1 - previousChars, peekCheck('=')?TokenType.STAR_EQUAL:TokenType.STAR); break;
            case '/':
                if (peekCheck('/')) {
                    while (peek() != '\n' && !sourceEnd()) next();
                    line++;
                    addToken(line, current - 1, TokenType.NEWLINE); line++; previousChars=current-1;
                    previousChars = current;
                    next();
                }
                else addToken(line, current - 1 - previousChars, peekCheck('=')?TokenType.SLASH_EQUAL:TokenType.SLASH);
                break;
            case '!': addToken(line, current - 1 - previousChars, peekCheck('=')?TokenType.BANG_EQUAL:TokenType.BANG);break;
            case '=': addToken(line, current - 1 - previousChars, peekCheck('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case '<': addToken(line, current - 1 - previousChars, peekCheck('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '>': addToken(line, current - 1 - previousChars, peekCheck('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case '"': string(); break;
            case '\n': addToken(line, current - 1, TokenType.NEWLINE); line++; previousChars=current-1; break;
            default:
                if (isAlpha(chr)) identifier();
                else if (isDigit(chr)) number();
                else {
                    reportError(line, current,  "Scanner", "Unexpected Character");
                }
                break;
        }
    }


    // helper methods that help parse the string
    private void string() {
        while (peek() != '\"' && !sourceEnd()) {
            if(src.charAt(current) == '\n') line++;
            next();
        }
        if(sourceEnd()) reportError(line, current, "Scanner", "Unterminated String");
        next();
        String  s = src.substring(tokStart + 1, current - 1);
        addToken(line, current - 1 - previousChars, s, new AlmondString(s), TokenType.STRING);
    }

    private void identifier() {
        while (isAlpha(peek()) || isDigit(peek())) next();
        String  s= src.substring(tokStart, current);
        if(checkKeyword(s)) return;
        addToken(line, current - 1 - previousChars, s, null, TokenType.IDENTIFIER);
    }


    // a simple trie that is traversed with switch cases to find keywords
    // could've used a hashmap, but this was interesting
    private boolean checkKeyword(String s) {
        if(s.length() <= 1) return false;
        s = s.toLowerCase();
        char c = s.charAt(0);
        switch (c) {
            case 'a': return matchKeyword(s, "and", TokenType.AND);
            case 'c': return matchKeyword(s, "class", TokenType.CLASS);
            case 'e': {
                switch (s.charAt(1)) {
                    case 'l': return matchKeyword(s, "else", TokenType.ELSE);
                    case 'n': return matchKeyword(s, "end", TokenType.END);
                }
            }
            case 'f': {
                switch (s.charAt(1)) {
                    case 'a': return matchKeyword(s, "false", TokenType.FALSE);
                    case 'u': return matchKeyword(s, "function", TokenType.FUNCTION);
                    case 'o': return matchKeyword(s, "for", TokenType.FOR);
                }
            } break;
            case 'i': return matchKeyword(s, "if", TokenType.IF);
            case 'n': return matchKeyword(s, "none", TokenType.NONE);
            case 'o': return matchKeyword(s, "or", TokenType.OR);
            case 'p': return matchKeyword(s, "print", TokenType.PRINT);
            case 'r': return matchKeyword(s, "return", TokenType.RETURN);
            case 't': {
                switch (s.charAt(1)) {
                    case 'h': return matchKeyword(s, "this", TokenType.THIS);
                    case 'r': return matchKeyword(s, "true", TokenType.TRUE);
                }
            } break;
            case 'v': return matchKeyword(s, "var", TokenType.VAR);
            case 'w': return matchKeyword(s, "while", TokenType.WHILE);
            default:
                return false;
        }
        return false;
    }

    // checks if a token is a numer
    // returns an AlmondDouble or a AlmnodInt object during runtime
    private void number() {
        boolean isInt = true;
        while (isDigit(peek())) next();
        if (peek() == '.' && isDigit(peekOffset(1))) {
            isInt = false;
            next();
        } else {

        }
        while (isDigit(peek())) next();
        String str = src.substring(tokStart, current);
        if (isInt) {
            int num = Integer.parseInt(str);
            addToken(line, current - 1 - previousChars, str, new AlmondInt(num), TokenType.NUMBER);
        } else {
            double num = Double.parseDouble(str);
            addToken(line, current - 1 - previousChars, str, new AlmondDouble(num), TokenType.NUMBER);
        }
    }

    // checks if it a chat is alphabetic
    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    // self explanatory
    private boolean isDigit(char c){
        return (c >= '0' && c <= '9');
    }

    // adds a token to the scan list
    // overloaded for different token types
    private void addToken(int line, int col, TokenType tokenType) {
        tokenList.add(new Token(line, col, src.substring(tokStart, current), null, tokenType));
    }

    private void addToken(int line, int col, String tstring, AlmondType tokenValue, TokenType tokenType) {
        tokenList.add(new Token(line, col, tstring, tokenValue, tokenType));
    }

    // helper methods
    private char peek() {
        if(sourceEnd()) return '\0';
        return src.charAt(current);
    }

    public char next() {
        current++;
        return src.charAt(current-1);
    }

    private boolean sourceEnd() {
        if(current >= src.length())
            return true;
        return false;
    }

    private char peekOffset(int n) {
        if (sourceEnd())
            return '\0';
        return src.charAt(current + n);
    }

    private boolean peekCheck(char c) {
        if (sourceEnd())
            return false;
        if (peek() == c) {
            next();
            return true;
        }
        return false;
    }


//    AND, CLASS, ELSE, FALSE, FUNCTION, FOR, IF, NONE, OR,
//    PRINT, RETURN, THIS, TRUE, VAR, WHILE, END



    private boolean matchKeyword(String s, String cmp, TokenType t) {
        if(s.compareTo(cmp) == 0) {
            addToken(line, current - 1 - previousChars, s, null, t);
            return true;
        }
        return false;
    }
    }
