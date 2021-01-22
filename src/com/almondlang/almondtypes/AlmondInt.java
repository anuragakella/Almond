package com.almondlang.almondtypes;

// wrapper classes for java data types
// Almond<Type>
// the user almost always interacts with AlmondTypes and not java types
public class AlmondInt implements AlmondType {
    public int data;
    @Override
    public String typeOf() {
        return getClass().getSimpleName();
    }
    @Override
    public Object getData() {
        return data;
    }

    public AlmondInt(int d) {
        this.data = d;
    }
    public AlmondInt(double d) {
        this.data = (int)d;
    }


    public String toString() {
        return data + "";
    }
}
