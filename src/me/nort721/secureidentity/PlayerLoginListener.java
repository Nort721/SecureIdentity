package me.nort721.secureidentity;

import me.nort721.secureidentity.files.AltsDataFile;
import me.nort721.secureidentity.utils.ConfigUtils;
import me.nort721.secureidentity.utils.MojangUtils;
import me.nort721.secureidentity.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class PlayerLoginListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        String username = e.getName();

        if (SecureIdentity.getInstance().getPlayersFile().getCorrectUUID(username) == null) {
            if (!isUUIDSpoofing(username, uuid)) {
                SecureIdentity.getInstance().getPlayersFile().saveCorrectUUID(username, uuid);
                SecureIdentity.getInstance().getPlayersFile().updateFile();
            } else {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
                return;
            }
        } else {
            if (!SecureIdentity.getInstance().getPlayersFile().getCorrectUUID(username).equals(uuid)) {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
                return;
            }
        }

        AltsDataFile dataFile = SecureIdentity.getInstance().getAltsDataFile();
        if (!dataFile.isAlreadySaved(e.getAddress().getHostAddress(), e.getName())) {

            dataFile.addName(e.getAddress().getHostAddress(), e.getName());

            if (dataFile.getAccounts(e.getAddress().getHostAddress()).size() > ConfigUtils.getIntFromConfig("Anti_Alts.On-login.Max accounts per ip")) {

                String kickMessage = ConfigUtils.getMessageFromConfig("Anti_Alts.On-login.Block-login reason");
                kickMessage = kickMessage.replaceAll("%player%", e.getName());
                kickMessage = kickMessage.replaceAll("%host%", e.getAddress().getHostAddress());

                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
                e.setKickMessage(kickMessage);

                String command = ConfigUtils.getStringFromConfig("Anti_Alts.On-login.Command");
                if (command.length() == 0) return;
                command = command.replaceAll("%player%", e.getName());
                command = command.replaceAll("%host%", e.getAddress().getHostAddress());

                final String cmd = command;
                Bukkit.getServer().getScheduler().runTask(SecureIdentity.getInstance(), new Runnable() {
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    }
                });
            }
        }
    }

    /**
     * Checks if the uuid is spoofed or not
     * @param username the username of the player
     * @param uuid the provided uuid from the player
     * @return True if the uuid is different then the mojang verified one, otherwise False
     */
    private boolean isUUIDSpoofing(String username, UUID uuid) {
        String fixedUUID = MojangUtils.getUUIDFromUsername(username);
        if (fixedUUID.equals("invalid name") || fixedUUID.equals("error")) return true;
        return !uuid.equals(UUID.fromString(StringUtils.untrimUUID(fixedUUID)));
    }
}
