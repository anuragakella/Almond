package com.almondlang.almondtypes;

// wrapper classes for java data types
// Almond<Type>
// the user almost always interacts with AlmondTypes and not java types
public class AlmondString implements AlmondType {
    public String data;
    @Override
    public String typeOf() {
        return getClass().getSimpleName();
    }
    @Override
    public Object getData() {
        return data;
    }

    public AlmondString(String d) {
        this.data = d;
    }

    public String toString() {
        return data + "";
    }
}
