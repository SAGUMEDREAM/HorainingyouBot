package cc.thonly.horainingyoubot.converter;

import cc.thonly.horainingyoubot.data.db.CustomData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CustomDataConverter implements AttributeConverter<CustomData, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CustomData attribute) {
        try {
            if (attribute == null) return "{}";
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return "{}";
        }
    }

    @Override
    public CustomData convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) {
                return new CustomData();
            }
            return mapper.readValue(dbData, CustomData.class);
        } catch (Exception e) {
            return new CustomData();
        }
    }
}