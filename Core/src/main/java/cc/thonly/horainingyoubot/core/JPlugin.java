package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.event.internal.BotEventListener;
import cc.thonly.horainingyoubot.event.internal.EventKey;
import cc.thonly.horainingyoubot.event.internal.EventPriority;
import cc.thonly.horainingyoubot.event.internal.EventResult;
import cc.thonly.horainingyoubot.service.GroupManagerImpl;

import java.util.List;

public interface JPlugin {
    void onInitialize();

    String getPluginId();

    default Meta getMeta() {
        return null;
    }

    default boolean hasMeta() {
        return this.getMeta() != null;
    }

    default void registerCommands(Commands commands) {

    }

    default void registerEvents() {

    }

    default <T> void registerEvent(EventKey<T> key, BotEventListener<T> listener) {
        key.register(listener);
    }

    default <T> void registerEvent(EventKey<T> key, EventPriority priority, BotEventListener<T> listener) {
        key.register(listener);
    }

    default <T> EventResult postEvent(EventKey<T> key, T listener) {
        return key.post(listener);
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

    interface Meta {
        String getDescription();
        String getSourceCodeUrl();
        List<String> getAuthor();
        Long getVersion();
    }
}
