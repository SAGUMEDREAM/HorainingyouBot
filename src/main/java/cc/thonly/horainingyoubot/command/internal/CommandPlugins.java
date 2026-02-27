package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.core.JPlugin;
import cc.thonly.horainingyoubot.core.JPluginLoader;
import cc.thonly.horainingyoubot.util.MsgUtil;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

@Command
public class CommandPlugins implements CommandEntrypoint {

    @Autowired
    JPluginLoader jPluginLoader;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("plugins")
                        .withExecutor((bot, event, args) -> {
                            StringBuilder md = new StringBuilder();
                            Set<Map.Entry<String, JPlugin>> entries = this.jPluginLoader.entries();

                            md.append("## 已加载插件 (").append(entries.size()).append(")\n\n");

                            int i = 0;
                            for (Map.Entry<String, JPlugin> entry : entries) {
                                String name = entry.getKey();
                                JPlugin plugin = entry.getValue();
                                Class<? extends JPlugin> clazz = plugin.getClass();

                                String className = clazz.getName();
                                String jarPath = clazz.getProtectionDomain()
                                        .getCodeSource()
                                        .getLocation()
                                        .getPath();

                                md.append("### ").append(++i).append(". ").append(name).append("\n");
                                md.append("- Class: `").append(className).append("`\n");
                                md.append("- Description: `").append(plugin.getPluginDescription()).append("`\n");
//                                md.append("- Jar: `").append(jarPath).append("`\n\n");
                            }

                            MsgUtil.reply(bot, event, ArrayMsgUtils.builder().text(md.toString()).build());
                        })
        );
    }
}
