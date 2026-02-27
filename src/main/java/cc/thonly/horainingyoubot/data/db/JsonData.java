package cc.thonly.horainingyoubot.data.db;

import cc.thonly.horainingyoubot.converter.JsonElementConverter;
import cc.thonly.horainingyoubot.repository.JsonDataRepository;
import com.google.gson.JsonElement;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "json_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonData {
    @Id
    private Long id = -1L;
    @Convert(converter = JsonElementConverter.class)
    private JsonElement internalElement;

    public <T> T asEditor(EditorFactory<T> factory) {
        return factory.accept(this.internalElement);
    }

    public void save(JsonDataRepository repository) {
        repository.save(this);
    }

    public interface EditorFactory<T> {
        T accept(JsonElement element);
    }
}
