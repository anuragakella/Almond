package com.almondlang.almondtypes;

// wrapper classes for java data types
// Almond<Type>
// the user almost always interacts with AlmondTypes and not java types
public class AlmondBoolean implements AlmondType {
    public boolean data;
    @Override
    public String typeOf() {
        return getClass().getSimpleName();
    }
    @Override
    public Object getData() {
        return data;
    }

    public AlmondBoolean(boolean d) {
        this.data = d;
    }

    public String toString() {
        return data + "";
    }
}
