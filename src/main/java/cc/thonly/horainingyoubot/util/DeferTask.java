package cc.thonly.horainingyoubot.util;

import cc.thonly.horainingyoubot.HorainingyouBotApplication;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
public class DeferTask {
    public static final List<DeferTask> TASKS = new ArrayList<>();
    private final int serverId;
    private float leftTime;
    private Runnable action;

    public DeferTask(int serverId, float leftTime, Runnable action) {
        this.serverId = serverId;
        this.leftTime = leftTime;
        this.action = action;
        TASKS.add(this);
    }

    public static synchronized DeferTask create(int server, float second, Runnable action) {
        return new DeferTask(server, second, action);
    }

    public static synchronized void when(int server, BooleanPredicate predicate, float intervalSeconds, Runnable action, Runnable elseAction) {
        create(server, intervalSeconds, () -> {
            if (predicate.get()) {
                action.run();
            } else {
                elseAction.run();
                when(server, predicate, intervalSeconds, action, elseAction);
            }
        });
    }

    public static synchronized void repeat(int server, int times, float intervalSeconds, Runnable action) {
        if (times <= 0) return;
        create(server, intervalSeconds, () -> {
            action.run();
            repeat(server, times - 1, intervalSeconds, action);
        });
    }


    public static synchronized void tick(int serverId) {
        Set<DeferTask> collect = TASKS.stream().filter(t -> t.serverId == serverId).collect(Collectors.toSet());
        for (var task : collect) {
            task.tick();
        }
    }

    public static synchronized int getId() {
        return HorainingyouBotApplication.START_ID;
    }

    public void stop() {
        TASKS.remove(this);
    }

    public synchronized boolean tick() {
        if (--this.leftTime <= 0) {
            this.action.run();
            TASKS.remove(this);
            return true;
        }
        return false;
    }

    public interface BooleanPredicate {
        Boolean get();
    }
}
