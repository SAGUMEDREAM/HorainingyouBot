package cc.thonly.otomad_helper.command;

import cc.thonly.horainingyoubot.browser.MarkdownImage;
import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.BilibiliApi;
import cc.thonly.horainingyoubot.util.BilibiliFormatter;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
@Slf4j
@Command
public class CommandBilibili implements CommandEntrypoint {

    @Autowired
    MarkdownImageFactory markdownImageFactory;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("b站解析")
                        .withAliasName("B站解析")
                        .withAliasName("bili")
                        .withArguments("#{bvid|avid}")
                        .withExecutor((bot, event, args) -> {
                            String videoId = args.getString("bvid|avid"); // 直接拿这个整体参数
                            if (videoId == null || videoId.isEmpty()) {
                                bot.sendMsg(event, "No video ID provided", false);
                                return;
                            }
                            MsgTool.reply(bot, event, "正在获取中，请稍后...");
                            try {
                                var data = BilibiliApi.getVideoInfo(videoId);
                                String markdown = BilibiliFormatter.formatVideoMarkdown(data);

                                long aid = data.get("aid").asLong();
                                long cid = data.get("cid").asLong();

                                var playData = BilibiliApi.getVideoPlayUrl(aid, cid);
                                StringBuilder urls = new StringBuilder();
                                for (var durl : playData.get("durl")) {
                                    urls.append(durl.get("url").asText()).append("\n");
                                }

                                MarkdownImage markdownImage = this.markdownImageFactory.render(markdown);
                                MsgTool.reply(bot, event, ArrayMsgUtils.builder().img(markdownImage.get()).build());
                            } catch (Exception e) {
                                log.error("Failed to parse Bilibili video: {}", e.getMessage(), e);
                                bot.sendMsg(event, "解析 B站 视频失败", false);
                            }
                        })
                        .up()
                        .addNode("下载")
                        .withExecutor((bot, event, args) -> {
                            String videoId = args.getString("bvid|avid");
                            if (videoId == null || videoId.isEmpty()) {
                                bot.sendMsg(event, "No video ID provided", false);
                                return;
                            }
                            MsgTool.reply(bot, event, "正在获取中，请稍后...");

                            Path videoTemp = null;
                            Path coverTemp = null;
                            try {
                                var data = BilibiliApi.getVideoInfo(videoId);
                                long aid = data.get("aid").asLong();
                                long cid = data.get("cid").asLong();

                                var playData = BilibiliApi.getVideoPlayUrl(aid, cid);
                                String videoUrl = playData.get("durl").get(0).get("url").asText();

                                String coverUrl = data.get("pic").asText();

                                videoTemp = Files.createTempFile("bili_video_", ".mp4");
                                downloadFile(videoUrl, videoTemp);

                                coverTemp = Files.createTempFile("bili_cover_", ".jpg");
                                downloadFile(coverUrl, coverTemp);

                                bot.sendMsg(event, ArrayMsgUtils.builder()
                                        .video(videoTemp.toAbsolutePath().toString(), coverTemp.toAbsolutePath().toString())
                                        .build(), false);

                            } catch (Exception e) {
                                log.error("Failed to download Bilibili video: {}", e.getMessage(), e);
                                bot.sendMsg(event, "下载 B站 视频失败", false);
                            } finally {
                                try {
                                    if (videoTemp != null) Files.deleteIfExists(videoTemp);
                                    if (coverTemp != null) Files.deleteIfExists(coverTemp);
                                } catch (IOException ignored) {}
                            }
                        })
                        .withAliasName("download")
                        .withArguments("#{bvid|avid}")
                        .up()
        );
    }

    private void downloadFile(String urlStr, Path target) throws IOException {
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (InputStream in = conn.getInputStream(); OutputStream out = Files.newOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }
        }
    }
}