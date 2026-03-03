package cc.thonly.horainingyoubot.event.internal;

public class EventKey<E> {

    private final Class<E> type;

    EventKey(Class<E> type) {
        this.type = type;
    }

    public void register(EventPriority priority, BotEventListener<E> listener) {
        EventBusFactory.register(type, priority, listener);
    }

    public void register(BotEventListener<E> listener) {
        register(EventPriority.NORMAL, listener);
    }

    public EventResult post(E event) {
        return EventBusFactory.post(event);
    }

    public Class<E> type() {
        return type;
    }
}