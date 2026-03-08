package cc.thonly.essential_bot.service;

import cc.thonly.essential_bot.data.WifeData;
import cc.thonly.essential_bot.repository.WifeRepository;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import com.mikuac.shiro.core.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WifeServiceImpl {

    @Autowired
    UserManagerImpl userManager;

    @Autowired
    WifeRepository wifeRepository;

    public boolean allowGetNewWife(User user) {
        WifeData wifeData = this.getOrCreate(user);
        if (!wifeData.getLastDrawDate().equals(LocalDate.now())) {
            wifeData.setTodayCount(0);
            this.wifeRepository.save(wifeData);
        }
        return wifeData.getTodayCount() <= 5;
    }

    public boolean addWife(User user, Long id) {
        WifeData wifeData = this.getOrCreate(user);
        List<Long> wifeList = wifeData.getWifeList();
        if (wifeList.contains(id)) {
            return false;
        }
        wifeList.add(id);
        int todayCount = wifeData.getTodayCount();
        wifeData.setTodayCount(++todayCount);
        this.save(wifeData);
        return true;
    }

    public WifeData getOrCreate(User user) {
        return this.wifeRepository.findById(user.getUserId())
                .orElseGet(() -> {
                    WifeData wifeData = this.createWifeData(user.getUserId());
                    this.wifeRepository.save(wifeData);
                    return wifeData;
                });
    }

    public WifeData save(WifeData data) {
        return this.wifeRepository.save(data);
    }

    private WifeData createWifeData(Long userId) {
        return new WifeData(userId, new ArrayList<>(), 0, LocalDate.now());
    }

}
