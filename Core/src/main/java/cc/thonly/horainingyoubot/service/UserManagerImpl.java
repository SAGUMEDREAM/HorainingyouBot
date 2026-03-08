package cc.thonly.horainingyoubot.service;

import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.repository.UserRepository;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.StrangerInfoResp;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import com.mikuac.shiro.dto.event.request.RequestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class UserManagerImpl {
    @Autowired
    UserRepository userRepository;

    public synchronized Optional<User> getUser(long userId) {
        return this.userRepository.findById(userId);
    }

    public synchronized User createUser(AnyMessageEvent event) {
        Long userId = event.getUserId();
        GroupMessageEvent.GroupSender sender = event.getSender();

        User user = new User(
                userId,
                sender.getNickname(),
                MsgTool.getUserAvatar(userId),
                false,
                false,
                1,
                new ArrayList<>(),
                new CustomData()
        );

        return this.userRepository.save(user);
    }

    public synchronized User forceCreateUser(User user) {
        if (user.getUserId() == -1L) {
            return null;
        }
        this.save(user);
        return user;
    }

    public synchronized User forceCreateUser(long userId) {
        return this.forceCreateUser(
                new User(userId,
                        String.valueOf(userId),
                        MsgTool.getUserAvatar(userId),
                        false,
                        false,
                        1,
                        new ArrayList<>(),
                        new CustomData()
                )
        );
    }

    public synchronized User forceCreateUser(Bot bot, long userId) {
        ActionData<StrangerInfoResp> strangerInfo = bot.getStrangerInfo(userId, false);
        if (strangerInfo.getRetCode() != 0) {
            return this.forceCreateUser(
                    new User(userId,
                            String.valueOf(userId),
                            MsgTool.getUserAvatar(userId),
                            false,
                            false,
                            1,
                            new ArrayList<>(),
                            new CustomData()
                    )
            );
        }
        StrangerInfoResp data = strangerInfo.getData();
        return this.forceCreateUser(
                new User(userId,
                        data.getNickname(),
                        MsgTool.getUserAvatar(userId),
                        false,
                        false,
                        1,
                        new ArrayList<>(),
                        new CustomData()
                )
        );
    }

    public synchronized void save(User user) {
        this.userRepository.save(user);
    }

    public synchronized User getOrCreate(AnyMessageEvent event) {
        Long userId = event.getUserId();

        Optional<User> byId = this.userRepository.findById(userId);
        if (byId.isEmpty()) {
            return this.createUser(event);
        }
        User user = byId.get();
        boolean changed = false;
        if (Objects.equals(event.getSender().getNickname(), user.getUsername())) {
            user.setUsername(event.getSender().getNickname());
            changed = true;
        }
        if (changed) {
            this.userRepository.save(user);
        } else {
            return user;
        }
        return this.userRepository.findById(userId)
                .orElseGet(() -> this.createUser(event));
    }

    public synchronized User getOrCreate(Bot bot, Long userId) {
        return this.userRepository.findById(userId)
                .orElseGet(() -> this.forceCreateUser(bot, userId));
    }

    public synchronized User getOrCreate(Long userId) {
        return this.userRepository.findById(userId)
                .orElseGet(() -> this.forceCreateUser(userId));
    }

    public synchronized User getOrCreate(RequestEvent event) {
        Long userId = event.getUserId();

        return this.userRepository.findById(userId)
                .orElseGet(() -> this.forceCreateUser(userId));
    }

    public synchronized User getOrCreate(PokeNoticeEvent event) {
        Long userId = event.getUserId();

        return this.userRepository.findById(userId)
                .orElseGet(() -> this.forceCreateUser(userId));
    }

    public synchronized List<User> findByPredicate(Predicate<User> predicate) {
        return this.userRepository.findAll().stream().filter(predicate).toList();
    }

    public synchronized boolean hasUser(long userId) {
        return this.userRepository.existsById(userId);
    }
}
