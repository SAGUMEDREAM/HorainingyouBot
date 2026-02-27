package cc.thonly.horainingyoubot.data;

import cc.thonly.horainingyoubot.data.db.CustomData;

public abstract class CustomDataModule {

    protected final CustomData data;
    private final String namespace;

    protected CustomDataModule(CustomData data) {
        this.data = data;
        this.namespace = this.getClass().getName();
    }

    protected String key(String key) {
        return namespace + "." + key;
    }

    protected <T> T get(String key, Class<T> type) {
        return data.get(key(key), type);
    }

    protected void set(String key, Object value) {
        data.set(key(key), value);
    }

    protected boolean has(String key) {
        return data.has(key(key));
    }
}
