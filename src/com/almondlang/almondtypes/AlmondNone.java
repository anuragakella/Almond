package com.almondlang.almondtypes;

// wrapper classes for java data types
// Almond<Type>
// the user almost always interacts with AlmondTypes and not java types
public class AlmondNone implements AlmondType {
    @Override
    public Object getData() {
        return null;
    }
    @Override
    public String typeOf() {
        return getClass().getSimpleName();
    }
    public AlmondNone() {
    }

    public String toString() {
        return "none";
    }
}
