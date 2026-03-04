package cc.thonly.horainingyoubot.service;

import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.repository.GroupRepository;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class GroupManagerImpl {
    @Autowired
    GroupRepository groupRepository;

    public synchronized Optional<Group> getGroup(long groupId) {
        return this.groupRepository.findById(groupId);
    }

    public synchronized Group createGroup(AnyMessageEvent event) {
        Long groupId = event.getGroupId();
        if (groupId == null) {
            return null;
        }
        Group group = new Group(groupId,
                false,
                new ArrayList<>(),
                new CustomData()
        );
        this.groupRepository.save(group);
        return group;
    }

    public synchronized Group forceCreateGroup(Group group) {
        Long groupId = group.getGroupId();
        if (groupId == null) {
            return null;
        }
        this.save(group);
        return group;
    }

    public synchronized Group forceCreateGroup(long userId) {
        return this.forceCreateGroup(new Group(userId, false, new ArrayList<>(), new CustomData()));
    }

    public synchronized Group getOrCreate(AnyMessageEvent event) {
        Long groupId = event.getGroupId();
        if (groupId == null) {
            return null;
        }

        return this.groupRepository.findById(groupId)
                .orElseGet(() -> this.createGroup(event));
    }

    public synchronized List<Group> findByPredicate(Predicate<Group> predicate) {
        return this.groupRepository.findAll().stream().filter(predicate).toList();
    }


    public synchronized void save(Group group) {
        this.groupRepository.save(group);
    }
}
