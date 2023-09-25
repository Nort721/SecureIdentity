package me.nort721.secureidentity;

import io.netty.util.internal.ThreadLocalRandom;
import me.nort721.secureidentity.commands.CommandsManager;
import me.nort721.secureidentity.utils.ConfigUtils;
import me.nort721.secureidentity.utils.CryptographicUtils;
import me.nort721.secureidentity.utils.StringUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Player2FAListener implements Listener {

    // <---------------------------> set password

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (CommandsManager.passwordPlayers.contains(player)) {
            String password = e.getMessage();

            if (!isStrongEnough(password)) {
                player.sendMessage(ConfigUtils.getMessageFromConfig("prefix") + ChatColor.GRAY + "password must be at least 6 characters long and have at least one upper case letter");
                return;
            }

            int max = ConfigUtils.getIntFromConfig("2FA_Password_Security.Passwords.dynamic_salt.size.max");
            int min = ConfigUtils.getIntFromConfig("2FA_Password_Security.Passwords.dynamic_salt.size.min");

            String dynamic_salt = RandomStringUtils.random(ThreadLocalRandom.current().nextInt(min, max), true, true);

            SecureIdentity.getInstance().getPasswordsFile().setPasswordHash(player.getUniqueId(), CryptographicUtils.securePassword(password, dynamic_salt), dynamic_salt);

            SecureIdentity.getInstance().getPasswordsFile().updateFile();

            CommandsManager.passwordPlayers.remove(player);

            player.sendMessage(ConfigUtils.getMessageFromConfig("prefix") + ChatColor.GRAY + "your password has been set");

            e.setCancelled(true);
        }

        if (CommandsManager.freezedPlayers.containsKey(player))
            e.setCancelled(true);
    }

    private boolean isStrongEnough(String password) {
        return password.length() < 6 || !StringUtils.containsUpperCase(password);
    }

    // <---------------------------> set password



    // <---------------------------> handle join

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        boolean isProtected = SecureIdentity.getInstance().getPasswordsFile().getPasswordHash(e.getPlayer().getUniqueId()) != null;
        if (isProtected) {
            CommandsManager.freezedPlayers.put(e.getPlayer(), 1);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000, 255));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000, 255));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1000, 255));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (CommandsManager.freezedPlayers.containsKey(e.getPlayer())) {
            CommandsManager.freezedPlayers.remove(e.getPlayer());
            e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            e.getPlayer().removePotionEffect(PotionEffectType.SATURATION);
        }
    }

    // <---------------------------> handle join



    // <---------------------------> freeze

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (CommandsManager.freezedPlayers.containsKey(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (CommandsManager.freezedPlayers.containsKey(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Player player = null;

        if (e.getDamager() instanceof Player)
            player = (Player) e.getDamager();

        if (e.getDamager() instanceof Projectile && ((Projectile)e.getDamager()).getShooter() instanceof Player)
            player = (Player) ((Projectile)e.getDamager()).getShooter();

        if (CommandsManager.freezedPlayers.containsKey(player))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            if (CommandsManager.freezedPlayers.containsKey(player))
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        if (CommandsManager.freezedPlayers.containsKey(e.getPlayer())) {
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ())
                e.setCancelled(true);
        }
    }

    // <---------------------------> freeze
}
