package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.browser.MarkdownImage;
import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.essential_bot.view.custom.EcoView;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Command
public class CommandInfo implements CommandEntrypoint {
    @Autowired
    UserManagerImpl userManager;

    @Autowired
    MarkdownImageFactory markdownImageFactory;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("info")
                        .withAliasName("查询")
                        .withExecutor((bot, event, args) -> {
                            User user = this.userManager.getOrCreate(event);
                            EcoView ecoView = user.getView(EcoView::new);
                            List<String> mdList = List.of(
                                    "## 用户信息\n",
                                    "* 用户名: %s\n".formatted(user.getUsername()),
                                    "* 用户ID: %s\n".formatted(String.valueOf(user.getUserId()).replace("L", "")),
                                    "* 用户权限等级: %s\n".formatted(user.getPermissionLevel()),
                                    "* 用户余额: %s\n".formatted(ecoView.getBalance())
                            );
                            MarkdownImage markdownImage = this.markdownImageFactory.render(mdList);
                            MsgTool.reply(bot, event, ArrayMsgUtils.builder().img(markdownImage.get()).build());
                        })
        );
    }
}
