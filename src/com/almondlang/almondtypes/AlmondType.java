package com.almondlang.almondtypes;

// wrapper classes for java data types
// Almond<Type>
// the user almost always interacts with AlmondTypes and not java types
public interface AlmondType {
    public Object getData();
    public String typeOf();
}

