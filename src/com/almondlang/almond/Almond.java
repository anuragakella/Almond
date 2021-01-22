package com.almondlang.almond;

import com.almondlang.syntax.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

// The Almond Programming Language
public class Almond {
    static final String VERSION = "1.1.5";
    public static boolean hadRuntimeException = false, hadParseError = false;

    //check for args and start a repl or read a file
    public static void main(String args[]) throws IOException {
        if(args.length < 1) {
            startREPL();
        } if(args.length == 1) {
            startFILE(args[0]);
        }
    }

    // read the input file into a string and run almond
    private static void startFILE(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        runLang(new String(bytes, Charset.defaultCharset()), false);
    }

    // if it can't find any file args it starts a REPL
    private static void startREPL() throws IOException {
        System.out.println("Almond " + VERSION + " on " + System.getProperty("os.name"));
        System.out.println("Type 'quit' to exit");
        while (true) {
            InputStreamReader ir = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(ir);
            System.out.print("> ");
            String line = br.readLine();
            if(line.compareTo("quit") == 0)
                System.exit(0);
            line += '\n';
            runLang(line, true);
        }
    }

    // main run method that runs almond through a source string
    private static void runLang(String src, boolean repl) {
        // create a scanner and scan
        Scanner scanner = new Scanner(src);
        scanner.start();
        ArrayList<Token> tokenList = scanner.getTokenList();

        // pass the scanned tokens to a parser
        Parser parser = new Parser(tokenList);
        ArrayList<Statement> statements = parser.parse();

        // print debug (optional)
        debug(false, false, tokenList, statements);

        // if there are no parse errors, start executing code
        if(hadRuntimeException || hadParseError) return;
        Interpreter interpreter = new Interpreter(statements, repl);
        interpreter.interpret();
    }


    // debug methods to check for parser / scanner bugs
    private static void debug(boolean token, boolean tree, ArrayList<Token> tokenList, ArrayList<Statement> statements) {
        if(token)
            for(Token tok : tokenList) {
                System.out.println(tok);
            }
        if(tree)
            prettyPrint(statements);
    }
    private static void prettyPrint(ArrayList<Statement> statements) {
        PrettyPrinter p = new PrettyPrinter();
        p.prettyprint(statements);
    }
}
