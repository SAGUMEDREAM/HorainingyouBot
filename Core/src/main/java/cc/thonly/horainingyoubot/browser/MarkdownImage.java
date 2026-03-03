package cc.thonly.horainingyoubot.browser;

public class MarkdownImage {
    private final String rawInput;
    private final byte[] bytes;

    MarkdownImage(String rawInput, byte[] bytes) {
        this.rawInput = rawInput;
        this.bytes = bytes;
    }

    public byte[] get() {
        return this.bytes;
    }
}
