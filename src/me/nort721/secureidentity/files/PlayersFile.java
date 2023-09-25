package me.nort721.secureidentity.files;

import me.nort721.secureidentity.SecureIdentity;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayersFile {

    private final File file;
    private final FileConfiguration data;

    public PlayersFile(SecureIdentity plugin, String dataFileName) {
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
     * Get the saved uuid for that specific username
     * @param username the players username
     * @return returns the uuid thats saved for that username if there is one, otherwise returns null
     */
    public UUID getCorrectUUID(String username) {
        String uuidStr = data.getString(username + ".VerifiedUUID", null);
        if (uuidStr == null) return null;
        return UUID.fromString(uuidStr);
    }

    /**
     * Save a uuid with a username
     * @param username the username
     * @param verifiedUUID the verified uuid
     */
    public void saveCorrectUUID(String username, UUID verifiedUUID) {
        data.set(username + ".VerifiedUUID", verifiedUUID.toString());
    }
}
