package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.service.GroupManagerImpl;

public interface JPlugin {
    void onInitialize();

    String getPluginId();

    default String getPluginDescription() {
        return "";
    }

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
