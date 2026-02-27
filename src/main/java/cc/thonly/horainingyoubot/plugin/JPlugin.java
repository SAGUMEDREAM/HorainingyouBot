package cc.thonly.horainingyoubot.plugin;

import cc.thonly.horainingyoubot.core.SpringContextHolder;
import cc.thonly.horainingyoubot.service.GroupManagerImpl;

public interface JPlugin {
    void onInitialize();

    String getPluginId();

    static <T> T getBean(Class<T> bean) {
        return SpringContextHolder.getBean(bean);
    }

    static GroupManagerImpl getGroupManger() {
        return SpringContextHolder.getBean(GroupManagerImpl.class);
    }

    static GroupManagerImpl getUserManger() {
        return SpringContextHolder.getBean(GroupManagerImpl.class);
    }

}
