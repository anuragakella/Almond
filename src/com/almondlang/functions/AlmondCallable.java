package com.almondlang.functions;

import com.almondlang.almond.Interpreter;

import java.util.ArrayList;

public interface AlmondCallable {
    int arity();
    Object call(Interpreter interpreter, ArrayList<Object> args);
}
