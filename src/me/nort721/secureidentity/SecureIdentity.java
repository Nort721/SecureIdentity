package me.nort721.secureidentity;

import lombok.Getter;
import me.nort721.secureidentity.commands.CommandsManager;
import me.nort721.secureidentity.files.AltsDataFile;
import me.nort721.secureidentity.files.PasswordsFile;
import me.nort721.secureidentity.files.PlayersFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SecureIdentity extends JavaPlugin {

    @Getter private String consolePrefix;

    @Getter private AltsDataFile altsDataFile;
    @Getter private PasswordsFile passwordsFile;
    @Getter private PlayersFile playersFile;

    public static SecureIdentity getInstance() {
        return getPlugin(SecureIdentity.class);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        consolePrefix = ChatColor.RED + "[" + getDescription().getName() + "] " + ChatColor.RESET;

        saveDefaultConfig();

        playersFile = new PlayersFile(this, "players.yml");
        passwordsFile = new PasswordsFile(this, "passwords.yml");
        altsDataFile = new AltsDataFile(this, "altsData.yml");

        Bukkit.getPluginManager().registerEvents(new PlayerLoginListener(), this);
        Bukkit.getPluginManager().registerEvents(new Player2FAListener(), this);

        getCommand("SecureIdentity").setExecutor(new CommandsManager());

        getServer().getConsoleSender().sendMessage(consolePrefix + "has been enabled");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getServer().getConsoleSender().sendMessage(consolePrefix + "has been disabled");
    }
}
