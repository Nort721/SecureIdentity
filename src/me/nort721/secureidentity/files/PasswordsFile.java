package me.nort721.secureidentity.files;

import me.nort721.secureidentity.SecureIdentity;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PasswordsFile {

    private final File file;
    private final FileConfiguration data;

    public PasswordsFile(SecureIdentity plugin, String dataFileName) {
        System.out.println(plugin.getName() + " Loading "+dataFileName+"...");
        file = new File(plugin.getDataFolder(), dataFileName + ".yml");
        data = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        } else {
            try {
                data.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        updateFile();
    }

    /**
     * update the file
     */
    public void updateFile() {
        try {
            data.save(file);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Get the password hash of that specific uuid
     * @param uuid the players unique id
     * @return The password hash if there is one, otherwise null
     */
    public String getPasswordHash(UUID uuid) {
        return data.getString(uuid + ".hash");
    }

    /**
     * Get the password dynamic salt of that specific uuid
     * @param uuid the players unique id
     * @return The dynamic salt if there is one, otherwise null
     */
    public String getPasswordDynamicSalt(UUID uuid) {
        return data.getString(uuid + ".dynamic_slat");
    }

    /**
     * Set the password hash of that specific uuid
     * @param uuid the players unique id
     * @param hash the password hash
     */
    public void setPasswordHash(UUID uuid, String hash) {
        data.set(uuid + ".hash", hash);
    }

    /**
     * Set the password hash and the dynamic salt of that specific uuid
     * @param uuid the players unique id
     * @param hash the password hash
     * @param dynamic_salt the dynamic salt
     */
    public void setPasswordHash(UUID uuid, String hash, String dynamic_salt) {
        data.set(uuid + ".dynamic_slat", dynamic_salt);
        data.set(uuid + ".hash", hash);
    }
}
