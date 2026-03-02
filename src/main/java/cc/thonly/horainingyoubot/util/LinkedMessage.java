package cc.thonly.horainingyoubot.util;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
public class LinkedMessage {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static ScheduledExecutorService TIMER =
            Executors.newScheduledThreadPool(1);

    private static final long DEFAULT_TIMEOUT = 15;
    private static final BiConsumer<Bot, Exception> NULLPTR_FEEDBACK = (nullptr, e) -> {
    };

    public static void reloadAndClear() {
        for (Session session : SESSIONS.values()) {
            try {
                session.cancel();
            } catch (Exception ignored) {
            }
        }
        SESSIONS.clear();
        TIMER.shutdownNow();
        TIMER = Executors.newScheduledThreadPool(1);
    }

    public static void start(Bot bot, AnyMessageEvent event, SessionHandler handler) {
        start(bot, event, handler, NULLPTR_FEEDBACK, "/取消");
    }

    public static void start(
            Bot bot,
            AnyMessageEvent event,
            SessionHandler handler,
            BiConsumer<Bot, Exception> timeoutFeedback,
            String... cancelCommands
    ) {
        String key = key(event);

        Session session = new Session(cancelCommands);
        Session old = SESSIONS.put(key, session);
        if (old != null) {
            old.cancel();
        }

        CompletableFuture.runAsync(() -> {
            try {
                handler.handle(new Context(event, session));
            } catch (Exception e) {
                timeoutFeedback.accept(bot, e);
            } finally {
                SESSIONS.remove(key);
            }
        });
    }

    public static void onMessage(AnyMessageEvent event) {
        Session session = SESSIONS.get(key(event));
//        System.out.println("当前: %s".formatted(session));
        if (session != null) {
            if (session.isCancelCommand(event.getMessage())) {
                session.cancel();
                SESSIONS.remove(key(event));
                return;
            }
//            System.out.println("开始传递 %s".formatted(event));
            session.complete(event);
        }
    }

    private static String key(AnyMessageEvent e) {
        String group = e.getGroupId() == null ? "private" : String.valueOf(e.getGroupId());
        return group + ":" + e.getUserId();
    }

    // =========================

    public interface SessionHandler {
        void handle(Context ctx) throws Exception;
    }

    // =========================


    public static class Context {

        private final AnyMessageEvent origin;
        private final Session session;

        Context(AnyMessageEvent origin, Session session) {
            this.origin = origin;
            this.session = session;
        }

        public AnyMessageEvent waitNext() {
            return waitNext(DEFAULT_TIMEOUT);
        }

        public AnyMessageEvent waitNext(long seconds) {
            try {
                return this.session.waitNext(seconds);
            } catch (TimeoutException e) {
                throw new RuntimeException("等待输入超时", e);
            } catch (CancellationException e) {
                throw new RuntimeException("会话已取消", e);
            }
        }

        public void cancel() {
            this.session.cancel();
        }

        public AnyMessageEvent getOrigin() {
            return origin;
        }
    }

    // =========================

    private static class Session {

        private final BlockingQueue<AnyMessageEvent> queue = new LinkedBlockingQueue<>();
        private final String[] cancelCommands;
        private volatile boolean cancelled = false;

        Session(String... cancelCommands) {
            this.cancelCommands = cancelCommands == null ? new String[0] : cancelCommands;
        }

        public AnyMessageEvent waitNext(long seconds) throws TimeoutException {
            if (this.cancelled) {
                throw new CancellationException("Session cleared");
            }

            try {
                AnyMessageEvent event = this.queue.poll(seconds, TimeUnit.SECONDS);
                if (event == null) {
                    throw new TimeoutException();
                }
                return event;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        private boolean isCancelCommand(String msg) {
            if (msg == null) return false;
            for (String cmd : this.cancelCommands) {
                if (msg.equalsIgnoreCase(cmd) || msg.contains(cmd)) {
                    return true;
                }
            }
            return false;
        }

        public void complete(AnyMessageEvent event) {
            if (!this.cancelled) {
                //noinspection ResultOfMethodCallIgnored
                this.queue.offer(event);
            }
        }

        public void cancel() {
            this.cancelled = true;
            this.queue.clear();
        }
    }
}