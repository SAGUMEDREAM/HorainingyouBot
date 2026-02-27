package cc.thonly.horainingyoubot.data;

import cc.thonly.horainingyoubot.data.db.User;

public record PermissionLevel(int level) {
    public static final PermissionLevel GUEST = new PermissionLevel(0);
    public static final PermissionLevel USER = new PermissionLevel(1);
    public static final PermissionLevel MODERATOR = new PermissionLevel(2);
    public static final PermissionLevel ADMIN = new PermissionLevel(3);
    public static final PermissionLevel OWNER = new PermissionLevel(4);
    public static final PermissionLevel EXECUTOR = new PermissionLevel(4);

    public boolean matches(User user) {
        return user.getPermissionLevel() >= this.level;
    }

}
