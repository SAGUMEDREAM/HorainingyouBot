package cc.thonly.horainingyoubot.util;

import com.mikuac.shiro.enums.ActionPath;

public class BotActionExtend {


    public static ActionPath createKey(String path) {
        return () -> path;
    }
}
