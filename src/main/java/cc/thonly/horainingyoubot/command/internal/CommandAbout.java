package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.browser.MarkdownImage;
import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.util.MsgUtil;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandAbout implements CommandEntrypoint {
    private static final List<String> FEEDBACKS = List.of(
            "## 关于本项目",
            "* 网站：https://thonly.cc",
            "* 东方Project线下活动维基群：868256565",
            "* 中文东方社群信息聚合频道：589711336",
            "* 开发群：863842932",
            "* 开发/代码：稀神灵梦"
    );
    @Autowired
    MarkdownImageFactory factory;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("about")
                        .bypassEulaCheck()
                        .withExecutor((bot, event, args) -> {
                            MarkdownImage markdownImage = this.factory.render(FEEDBACKS);
                            MsgUtil.reply(bot, event, ArrayMsgUtils.builder().img(markdownImage.get()).build());
                        })
        );
    }
}
