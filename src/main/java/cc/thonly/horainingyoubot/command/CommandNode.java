package cc.thonly.horainingyoubot.command;

import cc.thonly.horainingyoubot.data.PermissionLevel;
import cc.thonly.horainingyoubot.data.db.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@SuppressWarnings("ALL")
public final class CommandNode {
    private static final Pattern ARG_PATTERN = Pattern.compile("#\\{([^}]+)}");
    private final String name;
    private final boolean root;
    private CommandExecutor executor;
    private CommandNode prevNode;
    private int permissionLevel = -1;
    private int length = -1;
    private boolean eulaNoCheck = false;
    private final List<CommandNode> subNodes = new ArrayList<>();
    private final List<String> arguemnts = new ArrayList<>();
    private Map<String, Object> defaultArguemnts = new LinkedHashMap<>();

    private CommandNode(String name) {
        this.name = name;
        this.root = false;
    }

    private CommandNode(String name, boolean root) {
        this.name = name;
        this.root = root;
    }

    public CommandNode addNode(String name) {
        CommandNode node = new CommandNode(name, false);
        node.prevNode = this;
        this.assertNotRepeat(node);
        this.subNodes.add(node);
        return node;
    }

    public CommandNode addNode(String name, CommandExecutor executor) {
        return addNode(name).withExecutor(executor);
    }

    public CommandNode addNode(String name, Consumer<CommandNode> function) {
        CommandNode node = addNode(name);
        function.accept(node);
        return this;
    }

    public CommandNode addNode(String name, CommandExecutor executor, Consumer<CommandNode> function) {
        CommandNode node = addNode(name);
        node.withExecutor(executor);
        function.accept(node);
        return this;
    }

    public CommandNode up() {
        return this.prevNode == null ? this : this.prevNode;
    }

    public void assertNotRepeat(CommandNode node) {
        for (CommandNode n : this.subNodes) {
            if (n.name.equals(node.name)) {
                log.warn("Duplicate command: {}", node.name);
                return;
            }
        }
    }

    public CommandNode withArgumentName(String argumentName) {
        this.arguemnts.add(argumentName);
        return this;
    }

    public CommandNode withArguments(String arguments) {
        this.arguemnts.clear();

        if (arguments == null || arguments.isBlank()) {
            return this;
        }

        Matcher matcher = ARG_PATTERN.matcher(arguments);

        while (matcher.find()) {
            this.arguemnts.add(matcher.group(1));
        }

        return this;
    }

    public CommandNode bypassEulaCheck() {
        this.eulaNoCheck = true;
        return this;
    }

    public CommandNode withDefaultArgument(String arguments, Object defaultValue) {
        this.defaultArguemnts.put(arguments, defaultValue);
        return this;
    }

    public CommandNode withPermissionLevel(int level) {
        this.permissionLevel = level;
        return this;
    }

    public CommandNode withPermissionLevel(PermissionLevel level) {
        this.permissionLevel = level.level();
        return this;
    }

    public CommandNode withExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public Map<String, Object> getDefaultArguemnts() {
        return this.defaultArguemnts;
    }

    public CommandExecutor getExecutor() {
        return this.executor;
    }

    public List<String> iterator() {
        CommandNode root = this;
        while (root.prevNode != null && !root.isRoot()) {
            root = root.prevNode;
        }

        List<String> result = new ArrayList<>();
        this.buildPaths(root, root.getName(), result);
        return result;
    }

    private void buildPaths(CommandNode node, String path, List<String> result) {
//        if (node.hasExecutor()) {
            StringBuilder cmd = new StringBuilder(path);

            if (!node.arguemnts.isEmpty()) {
                for (String arg : node.arguemnts) {
                    cmd.append(" #{").append(arg).append("}");
                }
            }

            result.add(cmd.toString());
//        }

        // 遍历子节点
        for (CommandNode child : node.subNodes) {
            this.buildPaths(child, path + " " + child.name, result);
        }
    }

    public int length() {
        if (this.length != -1) {
            return this.length;
        }

        int len = 0;
        CommandNode node = this;

        while (node != null && !node.root) {
            len++;
            node = node.prevNode;
        }

        this.length = len;
        return len;
    }

    public boolean hasPermissionLevel(User user) {
        return user.getPermissionLevel() >= this.permissionLevel;
    }

    public boolean hasExecutor() {
        return this.executor != null;
    }

    public boolean isRoot() {
        return this.root;
    }

    public boolean isEulaNoCheck() {
        return this.eulaNoCheck;
    }

    public String getName() {
        return this.name;
    }

    public CommandNode setPrevNode(CommandNode prevNode) {
        if (this.root) return this;
        this.prevNode = prevNode;
        return this;
    }

    public List<String> getArguments() {
        return List.copyOf(this.arguemnts);
    }

    public List<CommandNode> getChildren() {
        return List.copyOf(this.subNodes);
    }

    public CommandNode getPrevNode() {
        return this.prevNode;
    }

    public static CommandNode createRoot(String name) {
        return new CommandNode(name, true);
    }

    public static CommandNode createNode(String name) {
        return new CommandNode(name, false);
    }

}
