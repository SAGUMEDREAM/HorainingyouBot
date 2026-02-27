package cc.thonly.horainingyoubot.data;

import cc.thonly.horainingyoubot.data.db.CustomData;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class CustomDataHolder {
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> moduleCache = new ConcurrentHashMap<>();
    private final CustomData data;

    public CustomDataHolder(CustomData data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T> T getModule(Class<T> type) {
        return (T) moduleCache.computeIfAbsent(type, this::createModule);
    }

    private Object createModule(Class<?> type) {
        try {
            Constructor<?> ctor = CONSTRUCTOR_CACHE.computeIfAbsent(type, t -> {
                try {
                    Constructor<?> c = t.getConstructor(CustomData.class);
                    c.setAccessible(true);
                    return c;
                } catch (Exception e) {
                    throw new RuntimeException("The module must have a (CustomData) constructor: " + t.getName(), e);
                }
            });

            return ctor.newInstance(this);

        } catch (Exception e) {
            throw new RuntimeException("Module creation failed: " + type.getName(), e);
        }
    }
}
