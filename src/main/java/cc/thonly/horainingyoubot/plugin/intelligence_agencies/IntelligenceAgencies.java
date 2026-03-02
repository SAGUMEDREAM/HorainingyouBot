package cc.thonly.horainingyoubot.plugin.intelligence_agencies;

import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.core.CoreEvent;
import cc.thonly.horainingyoubot.core.JPlugin;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.event.internal.EventResult;
import cc.thonly.horainingyoubot.plugin.intelligence_agencies.data.CollectCache;
import cc.thonly.horainingyoubot.plugin.intelligence_agencies.repository.CollectCacheRepository;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.DeferTask;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.horainingyoubot.util.StringSimilarity;
import cc.thonly.horainingyoubot.util.TxtConfig;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.response.GroupInfoResp;
import com.mikuac.shiro.dto.action.response.MsgResp;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.JsonNode;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class IntelligenceAgencies implements JPlugin {

    @Autowired
    Commands commands;

    @Autowired
    UserManagerImpl userManager;

    @Autowired
    CollectCacheRepository collectCacheRepository;


    final Path keywordPath = Path.of("./data/keyword.txt");
    final Path targetGroup = Path.of("./data/forward_target_group.txt");
    final Path blacklistGroup = Path.of("./data/forward_blacklist_group.txt");
    final List<Long> groupIds = new ArrayList<>();
    final List<Long> blacklistGroups = new ArrayList<>();
    final List<String> keywords = new ArrayList<>();

    @Override
    public void onInitialize() {
        this.reloadConfig();
        this.registerCommands(this.commands);
        CoreEvent.RECEIVE_ANY.register(event -> {
            this.handleMessage(event.getBot(), event.getEvent());
            return EventResult.PASS;
        });
        CoreEvent.RECEIVE_ANY_REPLY.register(event -> {
            Bot bot = event.getBot();
            AnyMessageEvent anyMessageEvent = event.getEvent();
            User user = this.userManager.getOrCreate(anyMessageEvent);
            if (!user.hasPermissionLevel(2)) {
                return EventResult.PASS;
            }
            if (!(anyMessageEvent.getMessage().contains("/加入宣发列表") || anyMessageEvent.getMessage().contains("/加入列表"))) {
                return EventResult.PASS;
            }
            Integer replyMessageId = MsgTool.getReplyMessageId(bot, anyMessageEvent);
            if (replyMessageId == null) {
                return EventResult.PASS;
            }
            ActionData<MsgResp> msg = bot.getMsg(replyMessageId);
            MsgResp data = msg.getData();
            List<ArrayMsg> arrayMsg = data.getArrayMsg();
            ActionList<GroupInfoResp> groupList = bot.getGroupList();
            List<GroupInfoResp> groupInfoResps = groupList.getData();
            float nextTime = 1.0f;
            List<Runnable> actions = new ArrayList<>();
            for (GroupInfoResp groupInfoResp : groupInfoResps) {
                Long groupId = groupInfoResp.getGroupId();
                if (this.blacklistGroups.contains(groupId)) {
                    continue;
                }
                float finalNextTime = nextTime;
                actions.add(() -> DeferTask.create(DeferTask.getId(), finalNextTime, () -> {
                    bot.sendGroupMsg(groupId, arrayMsg, false);
                }));
                nextTime += 3.0F;
            }
            for (Runnable action : actions) {
                action.run();
            }
            bot.sendMsg(anyMessageEvent, "已加入宣发列表", false);
            return EventResult.PASS;
        });
    }

    public void reloadConfig() {
        this.keywords.clear();
        this.groupIds.clear();
        this.blacklistGroups.clear();
        TxtConfig.createIfNoExist(this.keywordPath);
        TxtConfig.read(this.keywordPath, this.keywords::add);
        TxtConfig.createIfNoExist(this.targetGroup);
        TxtConfig.read(this.targetGroup, line -> {
            line = line.trim();
            if (!line.isEmpty()) {
                this.groupIds.add(Long.parseLong(line));
            }
        });
        TxtConfig.createIfNoExist(this.blacklistGroup);
        TxtConfig.read(this.blacklistGroup, line -> {
            line = line.trim();
            if (!line.isEmpty()) {
                this.blacklistGroups.add(Long.parseLong(line));
            }
        });
    }

    @Override
    public void registerCommands(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("添加宣发黑名单")
                        .withAliasName("添加广播黑名单")
                        .withPermissionLevel(2)
                        .withArguments("#{group_id}")
                        .withExecutor((bot, event, args) -> {
                            String groupId = args.getString("group_id");
                            if (groupId == null) {
                                return;
                            }
                            TxtConfig.writeLine(this.blacklistGroup, groupId);
                            this.reloadConfig();
                            bot.sendMsg(event, "已将%s加入到广播黑名单中".formatted(groupId), false);
                        })
        );
        commands.registerCommand(
                CommandNode.createRoot("清空宣发缓存")
                        .withPermissionLevel(2)
                        .withExecutor((bot, event, args) -> {
                            long count = this.collectCacheRepository.count();
                            this.collectCacheRepository.deleteAll();
                            bot.sendMsg(event, "已清空%s条缓存".formatted(count), false);
                        })
        );
    }

    protected void handleMessage(Bot bot, AnyMessageEvent event) {
        if (event.getUserId() == bot.getSelfId()) {
            return;
        }
        if (this.groupIds.isEmpty()) {
            return;
        }
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        if (len(arrayMsg) <= 35) {
            return;
        }
        String listCQ = MsgTool.toListCQ(arrayMsg);
        if (!(shouldCollect(listCQ) && !hasCache(listCQ))) {
            return;
        }
        long totalCount = this.collectCacheRepository.count();
        if (totalCount >= 50) {
            CollectCache latest = this.collectCacheRepository
                    .findTopByOrderByTimeAsc();
            if (latest != null) {
                this.collectCacheRepository.delete(latest);
            }
        }
        CollectCache collectCache = new CollectCache();
        collectCache.setCqcode(listCQ);
        collectCache.setTime(LocalDateTime.now());
        for (Long groupId : this.groupIds) {
            bot.sendGroupMsg(groupId, listCQ, false);
        }
        this.collectCacheRepository.save(collectCache);
    }

    public int len(List<ArrayMsg> arrayMsgs) {
        StringBuilder sb = new StringBuilder();
        arrayMsgs.stream()
                .filter(item -> item.getType() == MsgTypeEnum.text)
                .forEach(item -> {
                    JsonNode text = item.getData().get("text");
                    if (text == null) {
                        return;
                    }
                    if (text.isString()) {
                        sb.append(text.asString());
                    }
                });
        return sb.length();
    }

    public boolean hasCache(String cqcode) {
        List<CollectCache> all = this.collectCacheRepository.findAll();
        for (CollectCache collectCache : all) {
            String cachedCqcode = collectCache.getCqcode();
            if (Objects.equals(cqcode, cachedCqcode)) {
                return true;
            }
            if (StringSimilarity.compareTwoStrings(cqcode, cachedCqcode) >= 0.67) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldCollect(String cqcode) {
        for (String keyword : this.keywords) {
            if (cqcode.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getPluginId() {
        return "intelligence_agencies";
    }
}
