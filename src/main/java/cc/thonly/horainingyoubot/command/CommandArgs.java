package cc.thonly.horainingyoubot.command;

import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.GroupManagerImpl;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("WrapperTypeMayBePrimitive")
@Setter
@Getter
public class CommandArgs {

    private final Map<String, ArrayMsg> argMap;
    private String raw = "";

    private final UserManagerImpl userManager;
    private final GroupManagerImpl groupManager;

    public CommandArgs(UserManagerImpl userManager,
                       GroupManagerImpl groupManager,
                       Map<String, ArrayMsg> argMap) {
        this.argMap = new LinkedHashMap<>(argMap);
        this.userManager = userManager;
        this.groupManager = groupManager;
    }

    public void setRaw(String raw) {
        this.raw = raw == null ? "" : raw;
    }

    public Optional<User> getUser(String name) {
        return resolveById(
                this.getArrayMsg(name),
                this.userManager::getUser,
                this.userManager::forceCreateUser
        );
    }

    public Optional<Group> getGroup(String name) {
        return resolveById(
                this.getArrayMsg(name),
                this.groupManager::getGroup,
                this.groupManager::forceCreateGroup
        );
    }

    public ArrayMsg getArrayMsg(String name) {
        return this.argMap.get(name);
    }

    public boolean getBoolean(String name) {
        String string = this.getString(name);
        if (string == null) {
            return false;
        }
        if (string.equalsIgnoreCase("true")) {
            return true;
        }
        if (string.equalsIgnoreCase("false")) {
            return false;
        }
        return false;
    }

    public String getString(String name) {
        ArrayMsg msg = this.argMap.get(name);
        if (msg == null) return null;

        if (msg.getType() == MsgTypeEnum.text) {
            return msg.getStringData("text");
        }

        return msg.getData() != null ? msg.getData().toString() : null;
    }

    public String getString(String name, String defaultValue) {
        String value = getString(name);
        return value != null ? value : defaultValue;
    }

    private <T> Optional<T> resolveById(
            ArrayMsg msg,
            Function<Long, Optional<T>> getter,
            Function<Long, T> creator
    ) {
        if (msg == null) return Optional.empty();

        Long id = null;

        if (msg.getType() == MsgTypeEnum.at) {
            id = msg.getLongData("qq");
        }

        if (msg.getType() == MsgTypeEnum.text) {
            id = parseLongLoose(msg.getStringData("text"));
        }

        if (id == null) return Optional.empty();

        Long finalId = id;
        return getter.apply(id)
                .or(() -> Optional.ofNullable(creator.apply(finalId)));
    }

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public int getInt(String name, int defaultValue) {
        String value = getString(name);
        Integer parsed = parseIntSafe(value);
        return parsed != null ? parsed : defaultValue;
    }

    public long getLong(String name) {
        return getLong(name, 0L);
    }

    public long getLong(String name, long defaultValue) {
        String value = getString(name);
        Long parsed = parseLongLoose(value);
        return parsed != null ? parsed : defaultValue;
    }

    public float getFloat(String name) {
        return getFloat(name, 0f);
    }

    public float getFloat(String name, float defaultValue) {
        String value = getString(name);
        Float parsed = parseFloatSafe(value);
        return parsed != null ? parsed : defaultValue;
    }

    public double getDouble(String name) {
        return getDouble(name, 0d);
    }

    public double getDouble(String name, double defaultValue) {
        String value = getString(name);
        Double parsed = parseDoubleSafe(value);
        return parsed != null ? parsed : defaultValue;
    }

    public int size() {
        return this.argMap.size();
    }

    private static String normalizeNumber(String s) {
        if (s == null) return null;

        s = s.trim().toLowerCase();

        // 去后缀
        if (s.endsWith("f") || s.endsWith("d") || s.endsWith("l")) {
            s = s.substring(0, s.length() - 1);
        }

        // 去千分位
        s = s.replace(",", "");

        // 去正号
        if (s.startsWith("+")) {
            s = s.substring(1);
        }

        return s;
    }

    private static Integer parseIntSafe(String s) {
        try {
            s = normalizeNumber(s);
            if (s == null) return null;
            Double d = Double.parseDouble(s);
            return d.intValue();
        } catch (Exception e) {
            return null;
        }
    }

    private static Float parseFloatSafe(String s) {
        try {
            s = normalizeNumber(s);
            if (s == null) return null;
            return Float.parseFloat(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static Double parseDoubleSafe(String s) {
        try {
            s = normalizeNumber(s);
            if (s == null) return null;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long parseLongLoose(String input) {
        try {
            String s = normalizeNumber(input);
            if (s == null) return null;
            Double d = Double.parseDouble(s);
            return d.longValue();
        } catch (Exception e) {
            return null;
        }
    }
}
