package cc.thonly.horainingyoubot.command;

import cc.thonly.horainingyoubot.service.GroupManagerImpl;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Component
public class Commands {
    private final Map<String, CommandNode> root2Node = new HashMap<>(128);

    @Autowired
    UserManagerImpl userManager;
    @Autowired
    GroupManagerImpl groupManager;

    public void registerCommand(CommandNode node) {
        if (!node.isRoot()) {
            log.error("{} is not a root command node", node);
            return;
        }
        this.root2Node.put(node.getName(), node);
    }

    public void registerCommand(CommandEntrypoint entrypoint) {
        entrypoint.registerCommand(this);
    }

    public void drops() {
        this.root2Node.clear();
    }

    public Map<String, CommandNode> getRoot2Node() {
        return Map.copyOf(this.root2Node);
    }

    @Nullable
    public CommandSession parseForCommand(Bot bot, GroupMessageEvent event) {
        List<ArrayMsg> arrayMsgs = event.getArrayMsg();
        if (arrayMsgs == null || arrayMsgs.isEmpty()) {
            System.out.println("1");
            return null;
        }
        arrayMsgs = normalizeAtBotFirst(bot, arrayMsgs);

        ArrayMsg first = arrayMsgs.getFirst();
        if (!isText(first)) {
            return null;
        }

        String fullText = getText(first);
        if (fullText == null || fullText.isBlank()) {
            return null;
        }

        List<String> tokens = splitTokens(fullText);
        if (tokens.isEmpty()) {
            return null;
        }

        int index = 0;

        String rootName = normalizeRoot(tokens.get(index));
        CommandNode current = this.root2Node.get(rootName);

        if (current == null) {
            return null;
        }

        index++;

        // 匹配子命令路径
        while (index < tokens.size()) {
            String token = tokens.get(index);
            CommandNode next = findChildByName(current, token);

            if (next == null) {
                break;
            }

            current = next;
            index++;
        }

        if (!current.hasExecutor()) {
            return null;
        }

        // ==============================
        // 构建参数消息流
        // ==============================

        List<ArrayMsg> argStream = new ArrayList<>();

        // ① 文本中剩余token作为参数
        for (int i = index; i < tokens.size(); i++) {
            argStream.add(wrapText(tokens.get(i)));
        }

        // ② 后续消息段全部作为参数（图片 / at 等）
        for (int i = 1; i < arrayMsgs.size(); i++) {
            argStream.add(arrayMsgs.get(i));
        }

        // ==============================
        // 参数绑定
        // ==============================

        Map<String, ArrayMsg> argMap = new LinkedHashMap<>();
        List<String> argNames = current.getArguments();

        for (int i = 0; i < argNames.size(); i++) {
            if (i >= argStream.size()) {
                break;
            }
            argMap.put(argNames.get(i), argStream.get(i));
        }

        Map<String, Object> defaultArguments = current.getDefaultArguemnts();
        for (var mapEntry : defaultArguments.entrySet()) {
            String key = mapEntry.getKey();
            Object value = mapEntry.getValue();
            if (argNames.contains(key) && !argMap.containsKey(key)) {
                List<ArrayMsg> build = ArrayMsgUtils.builder()
                        .text(String.valueOf(value))
                        .build();
                if (!build.isEmpty()) {
                    argMap.put(key, build.getFirst());
                }
            }
        }


        return new CommandSession(current, new CommandArgs(this.userManager, this.groupManager, argMap), event.getRawMessage());
    }

    private List<ArrayMsg> normalizeAtBotFirst(Bot bot, List<ArrayMsg> arrayMsgs) {
        int size = arrayMsgs.size();
        if (size == 0) {
            return arrayMsgs;
        }
        ArrayMsg first = arrayMsgs.getFirst();
        if (Objects.equals(first.getType(), MsgTypeEnum.at)) {
            long target = first.getLongData("qq");
            if (bot.getSelfId() == target) {
                List<ArrayMsg> news = new ArrayList<>(arrayMsgs);
                news.removeFirst();
                return news;
            }
        }
        return arrayMsgs;
    }

    private String normalizeRoot(String token) {
        if (token == null || token.isEmpty()) {
            return token;
        }

        char first = token.charAt(0);

        if (first == '/' || first == '.') {
            return token.substring(1);
        }

        return token;
    }


    private List<String> splitTokens(String input) {
        List<String> tokens = new ArrayList<>();
        if (input == null || input.isBlank()) {
            return tokens;
        }

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // 转义字符
            if (c == '\\' && i + 1 < input.length()) {
                char next = input.charAt(i + 1);
                switch (next) {
                    case 'n' -> current.append('\n');
                    case 't' -> current.append('\t');
                    case 'r' -> current.append('\r');
                    case '"' -> current.append('"');
                    case '\'' -> current.append('\'');
                    case '\\' -> current.append('\\');
                    default -> current.append(next);
                }
                i++;
                continue;
            }

            // 进入/退出引号
            if (c == '"' || c == '\'') {
                if (!inQuotes) {
                    inQuotes = true;
                    quoteChar = c;
                    continue;
                } else if (quoteChar == c) {
                    inQuotes = false;
                    continue;
                }
            }

            // 空白分隔（仅在非引号内）
            if (Character.isWhitespace(c) && !inQuotes) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(c);
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    private CommandNode findChildByName(CommandNode node, String name) {
        for (CommandNode child : node.getChildren()) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    private ArrayMsg wrapText(String text) {
        ArrayMsg arrayMsg = new ArrayMsg();
        arrayMsg.setType(MsgTypeEnum.text);
        HashMap<String, String> map = new HashMap<>();
        map.put("text", text);
        arrayMsg.setData(map);
        return arrayMsg;
    }

    private static String getText(ArrayMsg msg) {
        if (!isText(msg)) {
            return "";
        }
        JsonNode data = msg.getData();
        JsonNode text = data.get("text");
        return text.isString() ? text.stringValue() : "";
    }

    private static boolean isText(ArrayMsg msg) {
        return Objects.equals(MsgTypeEnum.text, msg.getType());
    }
}
