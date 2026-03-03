package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.essential_bot.EssentialBot;
import cc.thonly.essential_bot.data.TouhouFortuneSlips;
import cc.thonly.essential_bot.view.custom.JRRPView;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import com.google.gson.Gson;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.model.ArrayMsg;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

@Command
public class CommandJrrp implements CommandEntrypoint {
    private TouhouFortuneSlips cachedContent = null;
    private final Map<String, List<String>> fortuneMap = new HashMap<>();
    private final Random random = new Random();
    @Autowired
    UserManagerImpl userManager;

    @Override
    public void registerCommand(Commands commands) {
        this.loadFortunes();

        commands.registerCommand(
                CommandNode.createRoot("jrrp")
                        .withAliasName("今日人品")
                        .withExecutor((bot, event, args) -> {

                            User user = this.userManager.getOrCreate(event);
                            JRRPView view = user.getView(JRRPView::new);

                            if (!view.isToday()) {
                                int value = this.random.nextInt(140) + 1;
                                String fortune = getLuckMessage(value);
                                view.setTodayResult(fortune, value);
                                this.userManager.save(user);
                            }

                            List<ArrayMsg> msg =ArrayMsgUtils.builder()
                                    .reply(event.getMessageId())
                                    .text(getTimeMessage())
                                    .at(event.getUserId())
                                    .text("\n" +
                                            "你今天的运气值是: " + (int)(view.getValue() / 1.4) + "\n" +
                                            "抽到的御神签是：\n" +
                                            view.getFortune())
                                    .build();

                            bot.sendMsg(event, msg, false);
                        })
        );
    }

    private String getLuckMessage(int luck) {
        if (luck <= 14) return randomOf("大凶");
        if (luck <= 28) return randomOf("凶");
        if (luck <= 42) return randomOf("小凶");
        if (luck <= 56) return randomOf("空");
        if (luck <= 70) return randomOf("末吉");
        if (luck <= 84) return randomOf("吉");
        if (luck <= 98) return randomOf("小吉");
        if (luck <= 112) return randomOf("中吉");
        if (luck <= 126) return randomOf("大吉");
        return randomOf("纯粹");
    }

    private String randomOf(String type) {
        List<String> list = fortuneMap.get(type);
        if (list == null || list.isEmpty()) return "（签文缺失）";
        return list.get(random.nextInt(list.size()));
    }

    private String getTimeMessage() {
        int hour = LocalTime.now().getHour();
        if (hour >= 6 && hour < 12) return "早上好！";
        if (hour < 14) return "中午好！";
        if (hour < 18) return "下午好！";
        return "晚上好！";
    }

    private void loadFortunes() {
        TouhouFortuneSlips data = getListFromJsonFile();
        if (data == null) return;

        for (TouhouFortuneSlips.Item slip : data.getSlips()) {
            List<String> content = slip.getContent();
            String text = String.join("\n", content);

            String first = content.get(0);

            addIfMatch("大凶", first, text);
            addIfMatch("凶", first, text);
            addIfMatch("小凶", first, text);
            addIfMatch("空", first, text);
            addIfMatch("末吉", first, text);
            addIfMatch("吉", first, text);
            addIfMatch("小吉", first, text);
            addIfMatch("中吉", first, text);
            addIfMatch("大吉", first, text);
            addIfMatch("纯粹", first, text);
        }
    }

    private void addIfMatch(String key, String text, String full) {
        if (!text.contains(key)) return;
        this.fortuneMap.computeIfAbsent(key, k -> new ArrayList<>()).add(full);
    }

    public TouhouFortuneSlips getListFromJsonFile() {
        if (this.cachedContent != null) return this.cachedContent;

        try (InputStream is = EssentialBot.class.getResourceAsStream(
                "/static/assets/Touhou_Fortune_Slips.json")) {

            if (is == null) throw new RuntimeException("签文文件不存在");

            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            this.cachedContent = new Gson().fromJson(json, TouhouFortuneSlips.class);
            return this.cachedContent;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
