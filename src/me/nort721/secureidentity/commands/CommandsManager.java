package me.nort721.secureidentity.commands;

import me.nort721.secureidentity.SecureIdentity;
import me.nort721.secureidentity.utils.ConfigUtils;
import me.nort721.secureidentity.utils.CryptographicUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CommandsManager implements CommandExecutor {

    public static ArrayList<Player> passwordPlayers = new ArrayList<>();
    public static ConcurrentHashMap<Player, Integer> freezedPlayers = new ConcurrentHashMap<>();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("reset")) {
                    // reset players password

                    return true;
                }
            }

            sender.sendMessage(ConfigUtils.getMessageFromConfig("prefix") + "you must be a other to use this command!");
            return true;
        }
        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("protect")) {
            if (passwordPlayers.contains(player)) {
                player.sendMessage(ConfigUtils.getMessageFromConfig("prefix") + ChatColor.GRAY + "you are already setting a password, please type the password in chat");
                return true;
            }
            passwordPlayers.add(player);
            player.sendMessage(ConfigUtils.getMessageFromConfig("prefix") + ChatColor.GRAY + "please enter your password in chat");
            return true;
        }

        if (args.length == 2) {
            if (freezedPlayers.containsKey(player)) {
                if (args[0].equalsIgnoreCase("login")) {
                    UUID uuid = player.getUniqueId();

                    String passwordHash = SecureIdentity.getInstance().getPasswordsFile().getPasswordHash(uuid);

                    String dynamic_salt = SecureIdentity.getInstance().getPasswordsFile().getPasswordDynamicSalt(uuid);
                    String inputHash = CryptographicUtils.securePassword(args[1], dynamic_salt);

                    if (passwordHash.equals(inputHash)) {
                        freezedPlayers.remove(player);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.removePotionEffect(PotionEffectType.SLOW);
                        player.removePotionEffect(PotionEffectType.SATURATION);
                    } else {
                        if (freezedPlayers.get(player) > 4) {
                            player.kickPlayer("Forgot your password?, contact an admin and ask him to reset it for you!");
                        } else {
                            player.sendMessage(ConfigUtils.getMessageFromConfig("prefix") + ChatColor.RED + ChatColor.BOLD + "incorrect password, please try again");
                            freezedPlayers.replace(player, freezedPlayers.get(player) + 1);
                        }
                    }

                    return true;
                }
            } else {
                player.sendMessage(ConfigUtils.getMessageFromConfig("prefix") + ChatColor.GRAY + "You are already logged in");
                return true;
            }
        }

        player.sendMessage(ConfigUtils.getMessageFromConfig("prefix") + "unknown command.");
        return true;
    }
}
