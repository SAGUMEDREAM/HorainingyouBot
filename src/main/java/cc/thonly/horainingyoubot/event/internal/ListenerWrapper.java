package cc.thonly.horainingyoubot.event.internal;

public record ListenerWrapper(
        EventPriority priority,
        BotEventListener<?> listener
) {}
