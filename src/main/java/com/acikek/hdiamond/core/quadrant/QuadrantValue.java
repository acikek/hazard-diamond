package com.acikek.hdiamond.core.quadrant;

import com.acikek.hdiamond.core.section.QuadrantSection;

public class QuadrantValue<E extends Enum<E> & QuadrantSection<E>> {

    private final Class<E> enumClass;
    private E value;

    public QuadrantValue(Class<E> enumClass, E value) {
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

    public boolean isEmpty() {
        return value.ordinal() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuadrantValue<?> that = (QuadrantValue<?>) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public QuadrantValue<E> copy() {
        return new QuadrantValue<>(enumClass, value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
