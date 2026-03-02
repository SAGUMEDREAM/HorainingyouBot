package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;

import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Command
public class CommandPing implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("ping")
                        .withArguments("#{address}")
                        .withExecutor((bot, event, args) -> {
                            String address = args.getString("address");
                            if (address == null || address.isBlank()) {
                                MsgTool.reply(bot, event, "请输入有效地址");
                                return;
                            }
                            try {
                                List<String> delays = ping(address, 3, 2000);

                                if (delays.isEmpty()) {
                                    MsgTool.reply(bot, event, "无法获取延迟");
                                    return;
                                }

                                String result = delays.stream()
                                        .map(d -> d + " ms")
                                        .reduce((a, b) -> a + ", " + b)
                                        .orElse("无法获取延迟");

                                MsgTool.reply(bot, event, result);
                            } catch (Exception e) {
                                MsgTool.reply(bot, event, "Ping 失败: " + e.getMessage());
                            }
                        })
        );

    }

    public static List<String> ping(String host, int count, int timeout) {
        List<String> delays = new ArrayList<>();
        Proxy proxyToUse = null;

        try {
            System.setProperty("java.net.useSystemProxies", "true");
            List<Proxy> proxies = ProxySelector.getDefault().select(new URI("http://" + host));

            for (Proxy proxy : proxies) {
                if (proxy.address() instanceof InetSocketAddress) {
                    proxyToUse = proxy;
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        for (int i = 0; i < count; i++) {
            long start = System.currentTimeMillis();
            try (Socket socket = (proxyToUse != null && proxyToUse.type() == Proxy.Type.SOCKS ?
                    new Socket(proxyToUse) : new Socket())) {

                socket.connect(new InetSocketAddress(host, 443), timeout);
                long end = System.currentTimeMillis();
                delays.add(String.valueOf(end - start));
            } catch (Exception e) {
                delays.add("-");
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}
        }

        while (delays.size() < count) delays.add("-");

        return delays;
    }
}