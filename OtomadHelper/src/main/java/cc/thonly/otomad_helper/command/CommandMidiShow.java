package cc.thonly.otomad_helper.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.otomad_helper.OtomadHelper;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Command
public class CommandMidiShow implements CommandEntrypoint {
    private static final int OUTPUT_LENGTH = 8;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("midi搜索")
                        .withAliasName("MIDI搜索")
                        .withArguments("#{keyword}")
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            if (keyword == null) {
                                return;
                            }

                            try {
                                this.searchMidiAndSend(bot, event, keyword);
                            } catch (Exception e) {
                                MsgTool.reply(bot, event, "搜索失败");
                                log.error("Error: ", e);
                            }
                        })
        );
    }

    @SuppressWarnings("DataFlowIssue")
    private void searchMidiAndSend(Bot bot, AnyMessageEvent event, String keyword) throws IOException {
        String url = "https://www.midishow.com/search/result?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        Request request = new Request.Builder()
                .url(url)
                .header("Referer", "https://www.midishow.com/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                MsgTool.reply(bot, event, "网络错误，MIDI搜索失败");
                return;
            }

            Document doc = Jsoup.parse(response.body().string());
            Elements results = doc.select("#search-result > div");

            if (results.isEmpty() || results.getFirst().attr("data-key").isEmpty()) {
                MsgTool.reply(bot, event, "没有找到相关MIDI……");
                return;
            }

            List<String> list = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            builder.append("MIDI 搜索结果:\n");

            for (int i = 0; i < Math.min(results.size(), OUTPUT_LENGTH); i++) {
                Element result = results.get(i);
                String title = result.selectFirst(".text-hover-primary").text().trim();
                String uploader = result.selectFirst(".avatar-img").attr("alt").trim();
                String duration = result.selectFirst("[title=\"乐曲时长\"]").text().trim();
                String trackCount = result.selectFirst("[title=\"音轨数量\"]").text().trim();
                String key = result.attr("data-key").trim();

                builder.append(String.format(
                        "标题: %s\n上传用户: %s\n乐曲时长: %s\n音轨数量: %s\n详细链接: https://www.midishow.com/midi/%s.html\n",
                        title, uploader, duration, trackCount, key
                ));
                list.add(builder.toString());
                builder.setLength(0);
            }

            if (results.size() > OUTPUT_LENGTH) {
                int remaining = results.size() - OUTPUT_LENGTH;
                builder.append(String.format("剩余 %d 个结果未展示...\n", remaining));
                list.add(builder.toString());
            }
            bot.sendForwardMsg(event, MsgTool.createForwardMsg(bot.getSelfId(), "蓬莱人形Bot", list));
        }
    }
}