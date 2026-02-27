package cc.thonly.horainingyoubot.service;

import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.repository.UserRepository;
import cc.thonly.horainingyoubot.util.MsgUtil;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.request.RequestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class UserManagerImpl {
    @Autowired
    UserRepository userRepository;

    public Optional<User> getUser(long userId) {
        return this.userRepository.findById(userId);
    }

    public User createUser(AnyMessageEvent event) {
        Long userId = event.getUserId();
        GroupMessageEvent.GroupSender sender = event.getSender();

        User user = new User(
                userId,
                sender.getNickname(),
                MsgUtil.getUserAvatar(userId),
                false,
                false,
                1,
                new ArrayList<>(),
                new CustomData()
        );

        return this.userRepository.save(user);
    }

    public User forceCreateUser(User user) {
        if (user.getUserId() == -1L) {
            return null;
        }
        this.save(user);
        return user;
    }

    public User forceCreateUser(long userId) {
        return this.forceCreateUser(
                new User(userId,
                        String.valueOf(userId),
                        MsgUtil.getUserAvatar(userId),
                        false,
                        false,
                        1,
                        new ArrayList<>(),
                        new CustomData()
                )
        );
    }

    public void save(User user) {
        this.userRepository.save(user);
    }

    public User getOrCreate(AnyMessageEvent event) {
        Long userId = event.getUserId();

        return this.userRepository.findById(userId)
                .orElseGet(() -> this.createUser(event));
    }

    public User getOrCreate(RequestEvent event) {
        Long userId = event.getUserId();

        return this.userRepository.findById(userId)
                .orElseGet(() -> this.forceCreateUser(userId));
    }

    public List<User> findByPredicate(Predicate<User> predicate) {
        return this.userRepository.findAll().stream().filter(predicate).toList();
    }

    public boolean hasUser(long userId) {
        return this.userRepository.existsById(userId);
    }
}
