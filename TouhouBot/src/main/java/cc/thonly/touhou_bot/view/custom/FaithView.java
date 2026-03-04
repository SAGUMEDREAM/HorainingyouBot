package cc.thonly.touhou_bot.view.custom;

import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;

public class FaithView {
    public static final String KEY_FAITH_LEVEL = "faith.faith_level";
    public static final String KEY_FAITH_VALUE = "faith.faith_value";
    public static final String KEY_FAITH_COUNT = "faith.faith_count";
    private final User user;
    private final CustomData data;

    public FaithView(User user, CustomData customData) {
        this.user = user;
        this.data = customData;
        this.init();
    }

    public void init() {
        if (this.data.has(KEY_FAITH_LEVEL)) {
            this.data.put(KEY_FAITH_LEVEL, 0);
        }
        if (this.data.has(KEY_FAITH_VALUE)) {
            this.data.put(KEY_FAITH_VALUE, 0);
        }
    }

    public void setCount(int value) {
        this.data.set(KEY_FAITH_COUNT, value);
    }

    public int getCount() {
        return this.data.get(KEY_FAITH_COUNT, Integer.class);
    }

    public void setValue(int value) {
        this.data.set(KEY_FAITH_VALUE, value);
    }

    public int getValue() {
        return this.data.get(KEY_FAITH_VALUE, Integer.class);
    }

    public void setLevel(int value) {
        this.data.set(KEY_FAITH_VALUE, value);
    }

    public int getLevel() {
        return this.data.get(KEY_FAITH_VALUE, Integer.class);
    }
}
