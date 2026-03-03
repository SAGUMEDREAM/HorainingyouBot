package cc.thonly.horainingyoubot.util;

import java.util.List;
import java.util.UUID;

public class Mth {
    public static <T> T getRandomElement(List<T> elements) {
        if (elements.isEmpty()) {
            return null;
        }
        var index = Math.floor(Math.random() * elements.size());
        return elements.get((int) index);
    }

    public static <T> T getRandomElement(T[] elements) {
        if (elements.length == 0) {
            return null;
        }
        var index = Math.floor(Math.random() * elements.length);
        return elements[((int) index)];
    }

    public static int getRandomNum(int min, int max) {
        return (int) (Math.floor(Math.random() * (max - min)) + min);
    }

    public static double getRandomNum(double min, double max) {
        return Math.floor(Math.random() * (max - min)) + min;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
