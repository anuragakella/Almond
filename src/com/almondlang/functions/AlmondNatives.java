package com.almondlang.functions;

import com.almondlang.almond.Interpreter;
import com.almondlang.almondtypes.AlmondDouble;

import java.util.ArrayList;

// native functions, implemented in java but outputs to almond
// users can call these functions but cannot redefine them
public abstract class AlmondNatives {
    public static class Time implements AlmondCallable {

        @Override
        public int arity() {
            return 0;
        }

        @Override
        public Object call(Interpreter interpreter, ArrayList<Object> args) {
            return new AlmondDouble((double) System.currentTimeMillis() / 1000.0);
        }

        public String toString(){
            return "AlmondNative:time";
        }
    }
    public static class Print implements AlmondCallable {

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, ArrayList<Object> args) {
            System.out.println(args.get(0));
            return null;
        }

        public String toString(){
            return "AlmondNative:time";
        }
    }
}
