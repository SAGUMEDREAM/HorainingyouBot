package cc.thonly.essential_bot.view.custom;

public class SignResult {

    public final boolean success;
    public final boolean alreadySigned;

    public final int baseReward;
    public final double criticalRate;
    public final int finalReward;

    private SignResult(boolean success,
                       boolean alreadySigned,
                       int baseReward,
                       double criticalRate,
                       int finalReward) {

        this.success = success;
        this.alreadySigned = alreadySigned;
        this.baseReward = baseReward;
        this.criticalRate = criticalRate;
        this.finalReward = finalReward;
    }

    public static SignResult success(int base, double critical, int finalReward) {
        return new SignResult(true, false, base, critical, finalReward);
    }

    public static SignResult alreadySigned() {
        return new SignResult(false, true, 0, 0, 0);
    }
}
