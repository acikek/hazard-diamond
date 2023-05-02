package com.acikek.hdiamond.core.quadrant;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.EnumUtils;

public interface HazardQuadrant {

    record Texture(int u, int v, int width, int height) {

        public static Texture numeral(int row, int index) {
            return new Texture(64 + index * 12, 14 * row, 12, 14);
        }
    }

    Texture getTexture();

    static <E extends Enum<E>> E fromJson(JsonElement element, Class<E> clazz) {
        JsonPrimitive primitive = element != null
                ? element.getAsJsonPrimitive()
                : null;
        if (primitive == null || primitive.isNumber()) {
            var list = EnumUtils.getEnumList(clazz);
            int index = primitive != null
                    ? primitive.getAsInt()
                    : 0;
            if (index < 0 || index >= list.size()) {
                throw new JsonParseException("index is out of range (0-" + (list.size() - 1) + ")");
            }
            return list.get(index);
        }
        if (primitive.isString()) {
            String str = primitive.getAsString();
            var result = EnumUtils.getEnumIgnoreCase(clazz, str);
            if (result == null) {
                throw new JsonParseException("unrecognized quadrant id '" + str + "'");
            }
            return result;
        }
        throw new JsonParseException("hazard quadrant must be a string or its number ordinal");
    }
}
