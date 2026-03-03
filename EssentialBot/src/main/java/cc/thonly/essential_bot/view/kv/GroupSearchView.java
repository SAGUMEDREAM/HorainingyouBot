package cc.thonly.essential_bot.view.kv;

import cc.thonly.essential_bot.data.GroupSearchCache;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupSearchView {

    private static final Gson GSON = new Gson();
    private final JsonObject body;

    public GroupSearchView(JsonElement jsonElement) {
        if (!(jsonElement instanceof JsonObject obj)) {
            throw new IllegalStateException("GroupSearchView requires JsonObject");
        }
        this.body = obj;
        initData();
    }

    private void initData() {
        if (!body.has("timestamp")) {
            body.addProperty("timestamp", 0L);
        }
        if (!body.has("data")) {
            body.add("data", GSON.toJsonTree(new ArrayList<GroupSearchCache.Item>()));
        }
    }

    public long getTimestamp() {
        return body.get("timestamp").getAsLong();
    }

    public void setTimestamp(long ts) {
        body.addProperty("timestamp", ts);
    }

    public List<GroupSearchCache.Item> getData() {
        Type type = new TypeToken<List<GroupSearchCache.Item>>() {}.getType();
        return GSON.fromJson(body.get("data"), type);
    }

    public void setData(List<GroupSearchCache.Item> list) {
        body.add("data", GSON.toJsonTree(list));
    }
}