package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.google.gson.Gson;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Command
public class CommandBaiduImage implements CommandEntrypoint {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("百度搜图")
                        .withArguments("#{keyword}")
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            if (keyword == null || keyword.isEmpty()) {
                                return;
                            }
                            bot.sendMsg(event, "搜索中，请稍后...", false);
                            String apiUrl = "https://api.suyanw.cn/api/baidu_image_search.php?msg=" + keyword + "&type=json";

                            try {
                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(apiUrl))
                                        .build();

                                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                                if (response.statusCode() == 200) {
                                    String responseBody = response.body();
                                    JsonObject result = gson.fromJson(responseBody, JsonObject.class);
                                    JsonArray data = result.getAsJsonArray("data");

                                    List<List<ArrayMsg>> output = new ArrayList<>();;

                                    for (int i = 0; i < Math.min(data.size(), 8); i++) {
                                        List<ArrayMsg> list = new ArrayList<>();
                                        JsonObject obj = data.get(i).getAsJsonObject();
//                                        System.out.println(obj.toString());
                                        String imageUrl = obj.get("imageurl").getAsString();
                                        String fromURL = obj.get("FromURL").getAsString();
                                        String fromURLHost = obj.get("fromURLHost").getAsString();

                                        boolean isValid = !fromURLHost.contains("cpc.people")
                                                && !fromURL.contains("cpc.people")
                                                && !fromURL.contains("gov")
                                                && !fromURL.contains("xjp")
                                                && !fromURLHost.contains("gov")
                                                && !fromURLHost.contains("xjp")
                                                && !fromURL.contains("cnr.cn")
                                                && !fromURLHost.contains("cnr.cn")
                                                && !fromURL.contains("news")
                                                && !fromURLHost.contains("news")
                                                && !fromURL.contains("people.com")
                                                && !fromURLHost.contains("people.com")
                                                && !fromURL.contains("people");

                                        if (isValid) {
                                            list.add(MsgTool.img2Byte(imageUrl));
                                            list.add(MsgTool.text(String.format("%s\n来源:%s\n", imageUrl, fromURL)));
                                            output.add(list);
                                        }
                                    }

                                    if (output.isEmpty()) {
                                        bot.sendMsg(event, List.of(MsgTool.reply(event), MsgTool.text("没有找到有效的图片")), false);
                                        return;
                                    }
                                    List<String> list = output.stream().map(MsgTool::toListCQ).toList();
                                    List<Map<String, Object>> maps = ShiroUtils.generateForwardMsg(event.getSelfId(), "", list);
                                    bot.sendForwardMsg(event, maps);
                                } else {
                                    bot.sendMsg(event, List.of(MsgTool.reply(event), MsgTool.text("获取失败")), false);
                                }
                            } catch (Exception e) {
                                log.error("Error fetching images", e);
                                bot.sendMsg(event, List.of(MsgTool.reply(event), MsgTool.text("获取失败")), false);
                            }
                        })
        );

    }
}