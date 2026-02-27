package cc.thonly.horainingyoubot.event.internal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private final Map<Class<?>, List<ListenerWrapper>> listeners = new ConcurrentHashMap<>();

    protected EventBus() {

    }

    public <E> void register(
            Class<E> eventType,
            EventPriority priority,
            BotEventListener<E> listener
    ) {
        this.listeners
                .computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(new ListenerWrapper(priority, listener));

        this.listeners.get(eventType)
                .sort(Comparator.comparing(ListenerWrapper::priority));
    }

    public <E> void registerType(Class<E> eventType) {
        this.listeners.computeIfAbsent(eventType, k -> new ArrayList<>());
    }

    public <E> EventResult post(E event) {
        List<ListenerWrapper> list = this.listeners.get(event.getClass());
        if (list == null) {
            return EventResult.PASS;
        }

        for (ListenerWrapper wrapper : list) {
            @SuppressWarnings("unchecked")
            BotEventListener<E> listener = (BotEventListener<E>) wrapper.listener();

            EventResult result = listener.onEvent(event);

            if (result == EventResult.BLOCKING) {
                return EventResult.BLOCKING;
            }
        }

        return EventResult.PASS;
    }

    public void clear() {
        List<Class<?>> busKeys = new ArrayList<>();
        for (var mapEntry : this.listeners.entrySet()) {
            Class<?> key = mapEntry.getKey();
            busKeys.add(key);
        }
        this.listeners.clear();
        for (Class<?> busKey : busKeys) {
            this.listeners.put(busKey, new ArrayList<>());
        }
    }
}
