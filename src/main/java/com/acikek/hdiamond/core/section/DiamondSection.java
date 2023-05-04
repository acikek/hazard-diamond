package com.acikek.hdiamond.core.section;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.EnumUtils;

public interface DiamondSection<E extends Enum<E>> {

    record Texture(int u, int v, int width, int height) {

        public static Texture numeral(int row, int index) {
            return new Texture(64 + index * 12, 14 * row, 12, 14);
        }
    }

    E getValue();

    Texture getTexture();

    String getType();

    default MutableText getText(String suffix) {
        return Text.translatable(getType() + "." + suffix);
    }

    default MutableText getSpecificText(String suffix) {
        return getText(getValue().name().toLowerCase() + "." + suffix);
    }

    default MutableText getTitle() {
        return getSpecificText("name");
    }

    default MutableText getDescription() {
        return getSpecificText("description");
    }

    static <E extends Enum<E>> E quadrantsFromJson(JsonElement element, Class<E> clazz) {
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
