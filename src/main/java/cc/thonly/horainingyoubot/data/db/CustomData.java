package cc.thonly.horainingyoubot.data.db;

import java.util.*;

public class CustomData {
    private final Map<String, Object> data = new HashMap<>();

    public void put(String key, Object value) {
        this.data.put(key, value);
    }

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

    public void remove(String key) {
        this.data.remove(key);
    }
}
