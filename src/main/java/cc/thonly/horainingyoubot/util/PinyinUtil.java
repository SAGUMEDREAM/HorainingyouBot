package cc.thonly.horainingyoubot.util;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.List;

public class PinyinUtil {
    public static List<String> toPinyinList(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }

        for (char c : text.toCharArray()) {
            String pinyin = Pinyin.toPinyin(String.valueOf(c), "");
            result.add(pinyin);
        }
        return result;
    }

    public static String toPinyin(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Pinyin.toPinyin(text, " ");
    }

    public static String toPinyinNoSpace(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Pinyin.toPinyin(text, "");
    }
}
