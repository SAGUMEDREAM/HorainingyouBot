package cc.thonly.horainingyoubot.util;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

@Slf4j
public class ValueAssert {
    public static boolean test(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean test(Runnable action, Runnable callback) {
        try {
            action.run();
        } catch (Exception e) {
            callback.run();
            return false;
        }
        return true;
    }

    public static boolean isNull(@Nullable Object... objs) {
        for (Object obj : objs) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean notNull(Object... objs) {
        for (Object obj : objs) {
            if (obj == null) {
                return false;
            }
        }
        return false;
    }
}
