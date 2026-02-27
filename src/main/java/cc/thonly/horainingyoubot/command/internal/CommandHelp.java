package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.browser.MarkdownImage;
import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgUtil;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Command
public class CommandHelp implements CommandEntrypoint {

    @Autowired
    Commands commands;

    @Autowired
    MarkdownImageFactory markdownImageFactory;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("help")
                        .withArguments("#{command}")
                        .withExecutor((bot, event, args) -> {
                            String command = args.getString("command");
                            if (command == null || command.isBlank()) {
                                MsgUtil.reply(bot, event, ArrayMsgUtils.builder().text("请输入命令名称").build());
                                return;
                            }

                            List<String> split = Arrays.stream(command.split(" "))
                                    .filter(s -> !s.isBlank())
                                    .toList();
                            if (split.isEmpty()) {
                                MsgUtil.reply(bot, event, ArrayMsgUtils.builder().text("命令无效").build());
                                return;
                            }

                            List<String> cmdResults = new ArrayList<>();
                            Map<String, CommandNode> root2Node = this.commands.getRoot2Node();
                            CommandNode root = root2Node.get(split.get(0));
                            if (root == null) {
                                MsgUtil.reply(bot, event, ArrayMsgUtils.builder().text("找不到命令: " + split.get(0)).build());
                                return;
                            }

                            this.buildPaths(root, cmdResults);

                            List<String> filtered = cmdResults.stream()
                                    .filter(cmdPath -> {
                                        List<String> pathParts = Arrays.stream(cmdPath.split(" "))
                                                .filter(s -> !s.startsWith("#{"))
                                                .toList();
                                        if (pathParts.size() < split.size()) return false;

                                        for (int i = 0; i < split.size(); i++) {
                                            if (!pathParts.get(i).equalsIgnoreCase(split.get(i))) {
                                                return false;
                                            }
                                        }
                                        return true;
                                    })
                                    .toList();

                            if (filtered.isEmpty()) {
                                MsgUtil.reply(bot, event, ArrayMsgUtils.builder().text("没有匹配的用法").build());
                                return;
                            }

                            List<String> mdTexts = new ArrayList<>();
                            mdTexts.add(command + " 的用法：");
                            for (String cmd : filtered) {
                                mdTexts.add("* " + cmd);
                            }

                            MarkdownImage render = this.markdownImageFactory.render(mdTexts);
                            MsgUtil.reply(bot, event, ArrayMsgUtils.builder().img(render.get()).build());
                        })
        );
    }

    private void buildPaths(CommandNode node, List<String> result) {
        buildPaths(node, "", result);
    }

    private void buildPaths(CommandNode node, String prefix, List<String> result) {
        // 累积当前节点路径
        StringBuilder path = new StringBuilder(prefix);
        if (!prefix.isEmpty()) {
            path.append(" "); // 父路径非空，补空格
        }
        path.append(node.getName());

        // 添加节点参数
        for (String arg : node.getArguments()) {
            path.append(" #{").append(arg).append("}");
        }

        // 当前节点路径加入结果
        result.add(path.toString().trim());

        // 递归子节点
        for (CommandNode child : node.getChildren()) {
            buildPaths(child, path.toString(), result);
        }
    }
}