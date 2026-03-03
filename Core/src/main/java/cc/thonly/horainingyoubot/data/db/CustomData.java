package cc.thonly.horainingyoubot.data.db;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CustomData {

    @JsonIgnore
    private final Map<String, Object> data = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.data;
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        this.data.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = this.data.get(key);
        if (value == null) return null;
        return type.cast(value);
    }

    public Object getAny(String key) {
        return this.data.get(key);
    }

    public boolean has(String key) {
        return this.data.containsKey(key);
    }

    public void put(String key, Object value) {
        this.data.put(key, value);
    }

    public void remove(String key) {
        this.data.remove(key);
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}