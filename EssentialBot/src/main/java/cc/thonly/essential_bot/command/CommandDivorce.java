package cc.thonly.essential_bot.command;

import cc.thonly.essential_bot.data.WifeData;
import cc.thonly.essential_bot.service.WifeServiceImpl;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Command
public class CommandDivorce implements CommandEntrypoint {

    @Autowired
    UserManagerImpl userManager;

    @Autowired
    WifeServiceImpl wifeService;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("离婚")
                        .withArguments("#{target}")
                        .withExecutor((bot, event, args) -> {
                            Optional<User> targetOptional = args.getUser("target");
                            if (targetOptional.isEmpty()) {
                                return;
                            }
                            User user = this.userManager.getOrCreate(event);
                            User target = targetOptional.get();
                            WifeData wifeData = this.wifeService.getOrCreate(user);

                            List<Long> wifeList = wifeData.getWifeList();

                            if (!wifeList.contains(target.getUserId())) {
                                MsgTool.reply(bot, event, "ta不是你老婆哦！");
                                return;
                            }

                            wifeList.remove(target.getUserId());
                            this.wifeService.save(wifeData);

                            MsgTool.reply(bot, event, "渣男！你竟敢把ta甩了！");
                        })
        );

    }
}