package com.almondlang.tools;

import com.almondlang.almond.Almond;
import com.almondlang.errors.AlmondRuntimeException;
import com.almondlang.errors.UndefinedVariableException;
import com.almondlang.errors.VariableRedefinedException;


// messaging class helps print errors and alerts to the console
public class Messages {
    public static void reportError(int line, int col, String source, String msg) {
        System.out.println(ConsoleColors.ANSI_RED + "line " + line + ":" + col + " Error: " + msg + " (" + source + ")" + ConsoleColors.ANSI_RESET);
    }
    public static void handleError(AlmondRuntimeException exception) {
        System.out.println(ConsoleColors.ANSI_RED + "[" + exception.op.line + ":" + exception.op.col + "] Error: " + exception.getMessage() + ConsoleColors.ANSI_RESET);
        Almond.hadRuntimeException = true;
    }
    public static void handleError(VariableRedefinedException exception) {
        System.out.println(ConsoleColors.ANSI_RED + "Error: " + exception.getMessage() + ConsoleColors.ANSI_RESET);
        Almond.hadRuntimeException = true;
    }
    public static void handleError(UndefinedVariableException exception) {
        System.out.println(ConsoleColors.ANSI_RED + "Error: " + exception.getMessage() + ": " + exception.name + ConsoleColors.ANSI_RESET);
        Almond.hadRuntimeException = true;
    }
    public static void handleError(RuntimeException exception) {
        System.out.println(ConsoleColors.ANSI_RED + "Error: " + exception.getMessage() + ConsoleColors.ANSI_RESET);
        Almond.hadRuntimeException = true;
    }
}
