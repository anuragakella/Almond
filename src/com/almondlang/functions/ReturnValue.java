package com.almondlang.functions;

// 'error' class for return statements
// not an error but does the job
// cuts the currently running 'statement' or block and throws an error
public class ReturnValue extends RuntimeException{
    Object val;
    public ReturnValue(Object val) {
        super(null, null, false, false);
        this.val = val;
    }
}
