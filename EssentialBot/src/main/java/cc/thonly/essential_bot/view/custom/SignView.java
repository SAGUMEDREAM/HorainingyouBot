package cc.thonly.essential_bot.view.custom;

import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;

public class SignView {

    public static final String KEY_TIMESTAMP = "sign_system.timestamp";

    private final User user;
    private final CustomData data;


    public SignView(User user, CustomData customData) {
        this.user = user;
        this.data = customData;
        this.initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (!this.data.has(KEY_TIMESTAMP)) {
            this.data.put(KEY_TIMESTAMP, 0L); // 从未签到
        }
    }

    /**
     * 执行签到
     */
    public SignResult sign() {

        long lastTimestamp = getTimestamp();

        LocalDate today = LocalDate.now();
        LocalDate lastDate = Instant.ofEpochMilli(lastTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (today.equals(lastDate)) {
            return SignResult.alreadySigned();
        }

        long now = System.currentTimeMillis();
        this.data.put(KEY_TIMESTAMP, now);

        int base = ThreadLocalRandom.current().nextInt(200, 301);

        double critical = ThreadLocalRandom.current().nextDouble(0.9, 1.9);
        critical = Math.round(critical * 100.0) / 100.0;

        int finalReward = (int) Math.floor(base * critical);

        EcoView ecoView = this.user.getView(EcoView::new);
        ecoView.addBalance(finalReward);

        return SignResult.success(base, critical, finalReward);
    }

    private long getTimestamp() {
        Long ts = this.data.get(KEY_TIMESTAMP, Long.class);
        return ts == null ? 0L : ts;
    }

}