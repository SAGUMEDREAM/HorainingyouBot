package cc.thonly.horainingyoubot.event.internal;

import java.util.Objects;

public enum EventResult {
    PASS,
    CONTINUE,
    BLOCKING,
    ;
    public boolean is(EventResult other) {
        return Objects.equals(this, other);
    }
}
