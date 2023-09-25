package me.nort721.secureidentity.files;

import me.nort721.secureidentity.SecureIdentity;
import me.nort721.secureidentity.utils.ConfigUtils;
import me.nort721.secureidentity.utils.CryptographicUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AltsDataFile {

    private static final String IP_STATIC_SALT = "dHWzDn#F%P!3Md-8FRduYzQvb4FL7z$rk";

    private final File file;
    private final FileConfiguration data;

    public AltsDataFile(SecureIdentity plugin, String dataFileName) {
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
    private void updateFile() {
        try {
            data.save(file);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /***
     * If player ip is saved, add player name to name list, if not add ip and then add name
     *
     * @param ip address of the player
     * @param name name of the player
     */
    public void addName(String ip, String name) {
        List<String> names = data.getStringList(CryptographicUtils.hashString(IP_STATIC_SALT + ip, ConfigUtils.getStringFromConfig("Anti_Alts.Data Security.Hash algorithm")) + ".accounts");
        if (names == null) {
            names = new ArrayList<>();
        }
        names.add(name);
        data.set(CryptographicUtils.hashString(IP_STATIC_SALT + ip, ConfigUtils.getStringFromConfig("Anti_Alts.Data Security.Hash algorithm")) + ".accounts", names);
        updateFile();
    }

    /***
     * Set the name list
     *
     * @param ip list owner
     * @param list the list
     */
    public void setAccountsList(String ip, List<String> list) {
        data.set(CryptographicUtils.hashString(IP_STATIC_SALT + ip, ConfigUtils.getStringFromConfig("Anti_Alts.Data Security.Hash algorithm")) + ".accounts", list);
    }

    /***
     * Get all accounts under a certain ip
     *
     * @param ip the ip we want to check
     * @return a list of all player names
     */
    public List<String> getAccounts(String ip) {
        return data.getStringList(CryptographicUtils.hashString(IP_STATIC_SALT + ip, ConfigUtils.getStringFromConfig("Anti_Alts.Data Security.Hash algorithm")) + ".accounts");
    }

    /***
     * Check if player name is already saved under a certain ip
     *
     * @param ip player address
     * @param name the name
     * @return True if the name is already under that address otherwise false
     */
    public boolean isAlreadySaved(String ip, String name) {
        List<String> names = data.getStringList(CryptographicUtils.hashString(IP_STATIC_SALT + ip, ConfigUtils.getStringFromConfig("Anti_Alts.Data Security.Hash algorithm")) + ".accounts");
        if (names == null) return false;
        return names.contains(name);
    }
}
