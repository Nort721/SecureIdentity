package me.nort721.secureidentity.utils;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;

public class MojangUtils {

    /**
     * Gets the UUID of a username from mojang servers
     * @param username the username
     * @return Returns the uuid from mojang servers if it exists, otherwise returns an error string
     */
    public static String getUUIDFromUsername(String username) {
        String url = "https://api.mojang.com/users/profiles/minecraft/"+username;
        try {
            String UUIDJson = IOUtils.toString(new URL(url));
            if(UUIDJson.isEmpty()) return "invalid name";
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            return UUIDObject.get("id").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return "error";
    }
}
