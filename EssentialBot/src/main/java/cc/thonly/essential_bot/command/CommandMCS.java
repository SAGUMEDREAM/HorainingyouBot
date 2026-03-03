package cc.thonly.essential_bot.command;

import br.com.azalim.mcserverping.MCPing;
import br.com.azalim.mcserverping.MCPingOptions;
import br.com.azalim.mcserverping.MCPingResponse;
import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Command
public class CommandMCS implements CommandEntrypoint {

    @Autowired
    MarkdownImageFactory markdownImageFactory;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("mcs")
                        .withAliasName("mcstatus")
                        .withArguments("#{ip_address} #{port}")
                        .withDefaultArgument("port", 25565)
                        .withExecutor((bot, event, args) -> {
                            String host = args.getString("ip_address");
                            int port = args.getInt("port");

                            if (host == null || host.isBlank()) {
                                MsgTool.reply(bot, event, "请输入服务器地址");
                                return;
                            }

                            List<String> mdList = new ArrayList<>();
                            mdList.add("## 服务器状态\n");
                            try {
                                MCPingResponse response = MCPing.getPing(MCPingOptions.builder().hostname(host).port(port).build());
                                mdList.add("![服务器图标](%s)\n".formatted(response.getFavicon()));
                                mdList.add("主机地址：%s\n".formatted(response.getHostname()));
                                mdList.add("端口号：%s\n".formatted(response.getPort()));
                                mdList.add("玩家数量：%s/%s\n".formatted(response.getPlayers().getOnline(), response.getPlayers().getMax()));
                                mdList.add("版本：%s(%s)\n".formatted(response.getVersion().getName(), response.getVersion().getProtocol()));
                                mdList.add("描述：%s\n".formatted(response.getDescription().getStrippedText()));
                            } catch (IOException e) {
                                mdList.add("解析失败");
                                log.error("Error: ", e);
                            }
                            MsgTool.reply(bot, event, MsgTool.img(this.markdownImageFactory.render(String.join("\n", mdList)).get()));
                        })
        );
    }
}