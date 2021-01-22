package com.almondlang.almondtypes;

// wrapper classes for java data types
// Almond<Type>
// the user almost always interacts with AlmondTypes and not java types
public class AlmondDouble implements AlmondType {
    public double data;

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public String typeOf() {
        return getClass().getSimpleName();
    }

    public AlmondDouble(double d) {
        this.data = d;
    }
    public AlmondDouble(int d) {
        this.data = d;
    }

    public String toString() {
        return data + "";
    }
}
