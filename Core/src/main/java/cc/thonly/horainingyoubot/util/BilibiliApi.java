package cc.thonly.horainingyoubot.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class BilibiliApi {
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode getVideoInfo(String videoId) throws IOException {
        String api = videoId.matches("^\\d+$") ?
                "https://api.bilibili.com/x/web-interface/view?aid=" + videoId :
                "https://api.bilibili.com/x/web-interface/view?bvid=" + videoId;

        Request request = new Request.Builder()
                .url(api)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return mapper.readTree(response.body().string()).get("data");
        }
    }

    public static JsonNode getVideoPlayUrl(long aid, long cid) throws IOException {
        String api2 = String.format(
                "https://api.bilibili.com/x/player/playurl?avid=%d&cid=%d&qn=1&type=&otype=json&platform=html5&high_quality=1",
                aid, cid
        );

        Request request = new Request.Builder().header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                .header("Referer", "https://www.bilibili.com/").url(api2).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return mapper.readTree(response.body().string()).get("data");
        }
    }
}
