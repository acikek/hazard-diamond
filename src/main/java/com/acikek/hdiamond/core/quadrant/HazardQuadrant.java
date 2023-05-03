package com.acikek.hdiamond.core.quadrant;

public class HazardQuadrant<E extends Enum<E>> {

    private final Class<E> enumClass;
    private E value;

    public HazardQuadrant(Class<E> enumClass, E value) {
        this.enumClass = enumClass;
        this.value = value;
    }

    public E get() {
        return value;
    }

    public E scroll() {
        int index = value.ordinal();
        E[] values = enumClass.getEnumConstants();
        value = index == values.length - 1 ? values[0] : values[index + 1];
        return value;
    }
}
