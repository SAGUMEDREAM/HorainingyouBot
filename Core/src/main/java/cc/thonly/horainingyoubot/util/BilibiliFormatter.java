package cc.thonly.horainingyoubot.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BilibiliFormatter {

    public static String formatVideoMarkdown(JsonNode data) {
        StringBuilder sb = new StringBuilder();
        long aid = data.get("aid").asLong();
        String title = data.get("title").asText();
        sb.append("## ").append(title).append(" (AV").append(aid).append(")\n");

        sb.append("![Cover](").append(data.get("pic").asText()).append(")\n\n");
        sb.append("Uploader: ").append(data.get("owner").get("name").asText()).append("\n\n");

        sb.append("Category: ").append(data.get("tname").asText())
                .append("-").append(data.get("tname_v2").asText()).append("\n\n");

        long pubdate = data.get("pubdate").asLong();
        sb.append("Upload Time: ").append(formatUnixTime(pubdate)).append("\n\n");

        sb.append("Description:\n");
        if (data.has("desc_v2")) {
            for (JsonNode obj : data.get("desc_v2")) {
                sb.append("> ").append(obj.get("raw_text").asText()).append("\n");
            }
        }

        JsonNode stat = data.get("stat");
        sb.append("| Views | Danmaku | Likes | Coins | Favorites | Shares |\n");
        sb.append("|-------|---------|-------|-------|-----------|--------|\n");
        sb.append("| ")
                .append(stat.get("view").asText()).append(" | ")
                .append(stat.get("danmaku").asText()).append(" | ")
                .append(stat.get("like").asText()).append(" | ")
                .append(stat.get("coin").asText()).append(" | ")
                .append(stat.get("favorite").asText()).append(" | ")
                .append(stat.get("share").asText()).append(" |\n");

        return sb.toString();
    }

    private static String formatUnixTime(long timestamp) {
        return Instant.ofEpochSecond(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}