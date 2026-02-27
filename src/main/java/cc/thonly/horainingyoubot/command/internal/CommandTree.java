package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.browser.MarkdownImage;
import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.util.MsgUtil;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Command
public class CommandTree implements CommandEntrypoint {
    @Autowired
    Commands commands;

    @Autowired
    MarkdownImageFactory markdownImageFactory;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("tree")
                        .withArguments("#{command}")
                        .withExecutor((bot, event, args) -> {
                            String command = args.getString("command");
                            if (command == null) {
                                return;
                            }
                            Map<String, CommandNode> root2Node = this.commands.getRoot2Node();
                            String[] split = command.split(" ");
                            if (split.length == 0) {
                                return;
                            }
                            CommandNode node = root2Node.get(split[0]);
                            List<String> iterator = node.iterator();
                            List<String> output = new ArrayList<>();
                            output.add("# 命令树");
                            for (String s : iterator) {
                                output.add("* " + s);
                            }
//                            System.out.println(output);
                            MarkdownImage render = this.markdownImageFactory.render(output);
                            MsgUtil.reply(bot, event, ArrayMsgUtils.builder().img(render.get()).build());
                        })
        );
    }
}
