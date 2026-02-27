package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.repository.UserRepository;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Command
public class CommandEula implements CommandEntrypoint {
    @Autowired
    UserManagerImpl userManager;

    @Autowired
    UserRepository userRepository;

    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("eula")
                        .bypassEulaCheck()
                        .withArguments("#{accepted-action}")
                        .withExecutor((bot, event, args) -> {
                            Optional<User> userOptional = this.userManager.getUser(event.getUserId());
                            if (userOptional.isEmpty()) {
                                return;
                            }
                            User user = userOptional.get();
                            boolean value = args.getBoolean("accepted-action");
                            if (value) {
                                if (!user.hasAcceptedEula()) {
                                    user.setEula(true);
                                    userRepository.save(user);
                                    bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("已同意协议哦").build(), false);
                                } else {
                                    bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("你已经同意过了哦").build(), false);
                                }
                            }
                        })
                        .up()
        );
    }

    private static final List<String> EULA_TEXT = List.of(
            "# 蓬莱人形 Bot 使用规定\n",
            "蓬莱人形 Bot 是一个基于 Shiro 框架开发，面向东方Project / 音MAD / maimai / 二次元相关QQ群的机器人。\n",
            "您需同意以下协议内容才可使用本 Bot：\n",
            "1. **禁止滥用机器人功能**，如刷屏、恶意调用等行为。\n",
            "2. **禁止生成或引导生成任何违规、不当内容**，包括但不限于涉政、涉黄、违法内容。\n",
            "3. **严禁将本 Bot 拉入“养蛊小群”等用于对抗测试或恶意操作的群聊**，一经发现，永久拉黑，不得申诉。\n",
            "4. **请勿将本 Bot 拉入与东方Project 无关的群聊**，若发现将自动退出。\n",
            "5. 若 Bot 暂时无法使用，通常是因平台限制导致，请耐心等待恢复。\n",
            "6. 若当前群管理员不同意使用本 Bot，请主动联系开发组移除，**请勿直接将 Bot 踢出群聊**，否则可能引发异常行为。\n",
            "7. 开发组直接在B站找`@稀神灵梦`即可。\n",
            "请输入 /eula true 以表示您已阅读并同意本协议。"
    );

    public static List<String> getText() {
        return EULA_TEXT;
    }
}
