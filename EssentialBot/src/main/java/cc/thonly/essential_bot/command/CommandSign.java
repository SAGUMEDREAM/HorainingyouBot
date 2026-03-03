package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.essential_bot.view.custom.SignResult;
import cc.thonly.essential_bot.view.custom.SignView;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CommandSign implements CommandEntrypoint {

    @Autowired
    UserManagerImpl userManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("sign")
                        .withAliasName("签到")
                        .withExecutor((bot, event, args) -> {
                            User user = this.userManager.getOrCreate(event);
                            SignView signView = user.getView(SignView::new);
                            SignResult result = signView.sign();
                            if (result.alreadySigned) {
                                bot.sendMsg(event, "你今天已经签到过了哦，别忘了明天再来！", false);
                                return;
                            }
                            String msg = "签到成功！🎉\n";
                            msg += "基础奖励：" + result.baseReward + " 円\n";
                            msg += "暴击倍率：×" + result.criticalRate + "\n";
                            msg += "最终奖励：" + result.finalReward + " 円\n";
                            bot.sendMsg(event, msg, false);
                        })
        );
    }
}
