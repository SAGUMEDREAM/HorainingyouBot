package cc.thonly.horainingyoubot.event.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBusFactory {

    private static final EventBus BUS = new EventBus();
    private static final Map<Class<?>, EventKey<?>> KEYS = new ConcurrentHashMap<>();

    public static <E> void register(
            Class<E> type,
            EventPriority priority,
            BotEventListener<E> listener
    ) {
        BUS.register(type, priority, listener);
    }

    public static <E> void registerType(Class<E> type) {
        BUS.registerType(type);
    }

    public static <E> EventResult post(E event) {
        return BUS.post(event);
    }

    public static void clear() {
        BUS.clear();
    }

    public static EventBus bus() {
        return BUS;
    }

    @SuppressWarnings("unchecked")
    public static <E> EventKey<E> key(Class<E> type) {
        return (EventKey<E>) KEYS.computeIfAbsent(type, t -> {
            BUS.registerType(type);
            return new EventKey<>(type);
        });
    }
}