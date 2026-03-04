package cc.thonly.touhou_bot.view.custom;

import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;

import java.time.LocalDate;

public class JRRPView {
    public static final String KEY_DATE = "jrrp.date";
    public static final String KEY_FORTUNE = "jrrp.fortune";
    public static final String KEY_VALUE = "jrrp.value";

    private final User user;
    private final CustomData data;

    public JRRPView(User user, CustomData customData) {
        this.user = user;
        this.data = customData;
    }

    public boolean isToday() {
        String today = LocalDate.now().toString();
        String stored = data.get(KEY_DATE, String.class);
        return today.equals(stored);
    }

    public void setTodayResult(String fortune, int value) {
        data.put(KEY_DATE, LocalDate.now().toString());
        data.put(KEY_FORTUNE, fortune);
        data.put(KEY_VALUE, value);
    }

    public String getFortune() {
        return data.get(KEY_FORTUNE, String.class);
    }

    public int getValue() {
        Integer v = data.get(KEY_VALUE, Integer.class);
        return v == null ? 0 : v;
    }
}