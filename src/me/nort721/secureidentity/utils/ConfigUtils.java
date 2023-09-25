package me.nort721.secureidentity.utils;

import me.nort721.secureidentity.SecureIdentity;
import org.bukkit.ChatColor;

public class ConfigUtils {

    public static String getMessageFromConfig(String path) {
        return ChatColor.translateAlternateColorCodes('&', SecureIdentity.getInstance().getConfig().getString(path));
    }

    public static String getStringFromConfig(String path) {
        return SecureIdentity.getInstance().getConfig().getString(path);
    }

    public static int getIntFromConfig(String path) {
        return SecureIdentity.getInstance().getConfig().getInt(path);
    }
}
