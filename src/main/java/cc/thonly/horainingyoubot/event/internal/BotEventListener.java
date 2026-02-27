package cc.thonly.horainingyoubot.event.internal;

public interface BotEventListener<E> {
    EventResult onEvent(E event);
}
