package cc.thonly.horainingyoubot.util;

import java.util.*;

public class StringSimilarity {
    public static double compareTwoStrings(String first, String second) {
        if (first == null) first = "";
        if (second == null) second = "";

        // 移除所有空白
        first = first.replaceAll("\\s+", "");
        second = second.replaceAll("\\s+", "");

        if (first.equals(second)) return 1.0; // 完全相同
        if (first.length() < 2 || second.length() < 2) return 0.0; // 长度小于2直接返回0

        // 统计 first 的 bigram 出现次数
        Map<String, Integer> firstBigrams = new HashMap<>();
        for (int i = 0; i < first.length() - 1; i++) {
            String bigram = first.substring(i, i + 2);
            firstBigrams.put(bigram, firstBigrams.getOrDefault(bigram, 0) + 1);
        }

        // 计算交集大小
        int intersectionSize = 0;
        for (int i = 0; i < second.length() - 1; i++) {
            String bigram = second.substring(i, i + 2);
            int count = firstBigrams.getOrDefault(bigram, 0);
            if (count > 0) {
                firstBigrams.put(bigram, count - 1);
                intersectionSize++;
            }
        }

        return 2.0 * intersectionSize / (first.length() + second.length() - 2);
    }
}
