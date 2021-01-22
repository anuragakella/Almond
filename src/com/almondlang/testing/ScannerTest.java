package com.almondlang.testing;

import com.almondlang.almondtypes.AlmondInt;
import com.almondlang.almond.Scanner;
import com.almondlang.almond.Token;
import com.almondlang.almond.TokenType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

// test class that unit tests the scanner class
public class ScannerTest {
    @Test
    public void shouldScanNumbers() throws Exception {
        String oup = "[99 : AlmondInt(99) : NUMBER] " + "[ : null : ENDFILE]";
        Scanner s = new Scanner("99");
        s.start();
        ArrayList<Token> tokens = s.getTokenList();
        String tstr = tokens.get(0) + " " + tokens.get(1);
        Assert.assertEquals(oup, tstr);
    }
    @Test
    public void shouldScanIdentifier() throws Exception {
        String oup = "[test : null : IDENTIFIER] " + "[ : null : ENDFILE]";
        Scanner s = new Scanner("test");
        s.start();
        ArrayList<Token> tokens = s.getTokenList();
        String tstr = tokens.get(0) + " " + tokens.get(1);
        Assert.assertEquals(oup, tstr);
    }

    @Test
    public void shouldScanKeyword() throws Exception {
        String oup = "[for : null : FOR] " + "[ : null : ENDFILE]";
        Scanner s = new Scanner("for");
        s.start();
        ArrayList<Token> tokens = s.getTokenList();
        String tstr = tokens.get(0) + " " + tokens.get(1);
        Assert.assertEquals(oup, tstr);
    }

    @Test
    public void shouldScanString() throws Exception {
        String oup = "[str : AlmondString(str) : STRING] " + "[ : null : ENDFILE]";
        Scanner s = new Scanner("\"str\"");
        s.start();
        ArrayList<Token> tokens = s.getTokenList();
        String tstr = tokens.get(0) + " " + tokens.get(1);
        Assert.assertEquals(oup, tstr);
    }
}
