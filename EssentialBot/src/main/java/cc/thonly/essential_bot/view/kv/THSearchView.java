package cc.thonly.essential_bot.view.kv;

import cc.thonly.essential_bot.data.THSearchResult;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

public class THSearchView {

    private static final Gson GSON = new Gson();

    private final JsonElement jsonElement;

    public THSearchView(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
        this.initData();
    }

    private void initData() {
        if (this.jsonElement instanceof JsonObject body) {
            if (!body.has("timestamp")) {
                body.addProperty("timestamp", 0L);
            }
            if (!body.has("results")) {
                body.add("results", new JsonArray());
            }
        } else {
            throw new IllegalStateException("THSearchView requires JsonObject");
        }
    }

    private JsonObject obj() {
        return (JsonObject) this.jsonElement;
    }


    public long getTimestamp() {
        return this.obj().get("timestamp").getAsLong();
    }

    public void setTimestamp(long timestamp) {
        this.obj().addProperty("timestamp", timestamp);
    }

    public void setResult(List<THSearchResult> results) {
        JsonArray array = new JsonArray();

        for (THSearchResult r : results) {
            JsonElement element = GSON.toJsonTree(r);
            array.add(element);
        }

        obj().add("results", array);
    }

    public List<THSearchResult> getResults() {
        List<THSearchResult> list = new ArrayList<>();

        JsonArray array = obj().getAsJsonArray("results");
        if (array == null) {
            return list;
        }

        for (JsonElement element : array) {
            THSearchResult result = GSON.fromJson(element, THSearchResult.class);
            list.add(result);
        }

        return list;
    }
}