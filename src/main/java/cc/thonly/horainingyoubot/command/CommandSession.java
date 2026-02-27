package cc.thonly.horainingyoubot.command;

public class CommandSession {
    final CommandNode node;
    final CommandArgs arguments;
    final String raw;

    public CommandSession(CommandNode node,
                          CommandArgs arguments,
                          String raw) {
        this.node = node;
        this.arguments = arguments;
        this.raw = raw;
        if (arguments != null) {
            arguments.setRaw(raw);
        }
    }

    public CommandNode node() {
        return this.node;
    }

    public CommandArgs arguments() {
        return this.arguments;
    }

    public String raw() {
        return this.raw;
    }
}
