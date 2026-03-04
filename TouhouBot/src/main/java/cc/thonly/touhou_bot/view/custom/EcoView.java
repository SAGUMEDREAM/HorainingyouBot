package cc.thonly.touhou_bot.view.custom;

import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;

public class EcoView {

    public static final String KEY_MONEY = "economic.money";

    private final User user;
    private final CustomData data;

    public EcoView(User user, CustomData customData) {
        this.user = user;
        this.data = customData;
        this.initData();
    }

    private void initData() {
        if (!this.data.has(KEY_MONEY)) {
            this.data.put(KEY_MONEY, 0.0);
        }
    }

    public synchronized void addBalance(double value) {
        this.data.put(KEY_MONEY, this.getBalance() + value);
    }

    public synchronized void deductBalance(double value) {
        this.data.put(KEY_MONEY, this.getBalance() - value);
    }

    public synchronized boolean hasBalance(double value) {
        return this.getBalance() >= value;
    }

    public void setBalance(double value) {
        data.put(KEY_MONEY, value);
    }

    public double getBalance() {
        Double v = this.data.get(KEY_MONEY, Double.class);
        return v == null ? 0.0 : v;
    }
}