package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.core.StartupRunner;
import cc.thonly.horainingyoubot.data.PermissionLevel;
import cc.thonly.horainingyoubot.util.MsgUtil;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CommandReload implements CommandEntrypoint {
    @Autowired
    StartupRunner runner;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("reload-all")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .withExecutor((bot, event, args) -> {
                            long start = System.currentTimeMillis();
                            this.runner.run();
                            long end = System.currentTimeMillis();
                            long elapsed = end - start;
                            MsgUtil.reply(bot, event, "重载成功，一共花费+ %s ms".formatted(elapsed));
                        })
        );
    }
}
