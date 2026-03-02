package cc.thonly.horainingyoubot.converter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class JsonElementConverter implements AttributeConverter<JsonElement, String> {

    private static final Gson GSON = new Gson();

    @Override
    public String convertToDatabaseColumn(JsonElement attribute) {
        if (attribute == null || attribute.isJsonNull()) {
            return "{}";
        }
        return GSON.toJson(attribute);
    }

    @Override
    public JsonElement convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) {
                return GSON.fromJson("{}", JsonElement.class);
            }
            return GSON.fromJson(dbData, JsonElement.class);
        } catch (Exception e) {
            return GSON.fromJson("{}", JsonElement.class);
        }
    }
}