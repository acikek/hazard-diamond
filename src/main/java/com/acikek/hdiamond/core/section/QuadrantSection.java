package com.acikek.hdiamond.core.section;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.EnumUtils;

public interface QuadrantSection<E extends Enum<E>> extends DiamondSection<E> {

    Formatting getTypeColor();

    Pair<Integer, Integer> getScreenOffsets();

    private MutableText getTypeName() {
        return getText("type").formatted(getTypeColor());
    }

    @Override
    default MutableText getTitle() {
        return getTypeName()
                .append(Text.literal(" - ").formatted(Formatting.GRAY))
                .append(DiamondSection.super.getTitle().formatted(getLevelColor()));
    }

    default MutableText getSymbol() {
        return Text.literal(String.valueOf(getValue().ordinal())).formatted(getTypeColor());
    }

    default Formatting getLevelColor() {
        return switch (getValue().ordinal()) {
            case 0, 1 -> Formatting.GREEN;
            case 2, 3 -> Formatting.YELLOW;
            case 4 -> Formatting.RED;
            default -> Formatting.WHITE;
        };
    }

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
