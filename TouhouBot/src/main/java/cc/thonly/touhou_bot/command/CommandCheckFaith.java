package cc.thonly.touhou_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.touhou_bot.view.custom.FaithView;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CommandCheckFaith implements CommandEntrypoint {

    @Autowired
    private UserManagerImpl userManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("查询信仰")
                        .withAliasName("checkfaith", "faith")
                        .withExecutor((bot, event, args) -> {
                            User user = this.userManager.getOrCreate(event);
                            CustomData customData = user.getCustomData();
                            FaithView faith = new FaithView(user, customData);

                            StringBuilder result = new StringBuilder();
                            result.append(MsgTool.createMessage()
                                    .at(event.getUserId())
                                    .text("你的当前信仰度为 " + faith.getValue() + " 点\n" +
                                            "信仰等级为 " + faith.getLevel() + " 级\n" +
                                            "已参拜 " + faith.getCount() + " 次。")
                                    .build()
                            );

                            MsgTool.reply(bot, event, result.toString());
                        })
        );
    }
}