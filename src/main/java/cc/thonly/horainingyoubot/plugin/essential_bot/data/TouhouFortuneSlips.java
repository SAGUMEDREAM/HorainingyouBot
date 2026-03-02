package cc.thonly.horainingyoubot.plugin.essential_bot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Data
@Getter
@ToString
public class TouhouFortuneSlips {
    List<Item> slips;

    @AllArgsConstructor
    @Data
    @Getter
    @ToString
    public static class Item {
        String id;
        List<String> content;
        String sign;
    }
}
