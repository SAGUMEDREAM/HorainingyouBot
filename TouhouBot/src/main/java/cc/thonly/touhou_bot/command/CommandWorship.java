package cc.thonly.touhou_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.touhou_bot.view.custom.EcoView;
import cc.thonly.touhou_bot.view.custom.FaithView;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CommandWorship implements CommandEntrypoint {

    private static final int COST = 140;
    private static final int REQUIRED_FAITH = 100;

    @Autowired
    private UserManagerImpl userManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("参拜神社")
                        .withAliasName("参拜", "worship", "visit")
                        .withExecutor((bot, event, args) -> {
                            User user = this.userManager.getOrCreate(event);
                            CustomData customData = user.getCustomData();

                            EcoView eco = new EcoView(user, customData);
                            FaithView faith = new FaithView(user, customData);

                            if (eco.getBalance() >= COST) {
                                eco.deductBalance(COST);

                                int gain = (int) (Math.random() * 31 + 10);
                                faith.setValue(faith.getValue() + gain);
                                faith.setLevel(checkLevelUp(faith));
                                faith.setCount(faith.getCount() + 1);

                                StringBuilder result = new StringBuilder();
                                result.append(MsgTool.createMessage()
                                        .at(event.getUserId())
                                        .text("参拜成功！信仰值增加了 " + gain + " 点。")
                                        .build()
                                );

                                if (faith.getValue() == 0) {
                                    result.append("\n你的信仰等级已升级为 ").append(faith.getLevel()).append(" 级");
                                }

                                MsgTool.reply(bot, event, result.toString());
                                this.userManager.save(user);
                            } else {
                                MsgTool.reply(bot, event, MsgTool.createMessage()
                                        .at(event.getUserId())
                                        .text("参拜神社需要 " + COST + " 円!")
                                        .build());
                            }
                        })
        );
    }

    /**
     * 检查是否升级，并返回当前等级
     */
    private int checkLevelUp(FaithView faith) {
        int levelUp = faith.getLevel();
        while (faith.getValue() >= REQUIRED_FAITH) {
            levelUp++;
            faith.setValue(faith.getValue() - REQUIRED_FAITH);
        }
        faith.setLevel(levelUp);
        return levelUp;
    }
}