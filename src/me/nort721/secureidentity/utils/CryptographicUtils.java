package me.nort721.secureidentity.utils;

import me.nort721.secureidentity.SecureIdentity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptographicUtils {

    private static final String STATIC_SALT = "XJRwu46w@OYIoJlLB2Uo^Mvd@P1Z0ta2NpsYy";

    /**
     * Hash a string
     * @param str the string
     * @param algorithm the hash algorithm
     * @return Returns a hashed string
     */
    public static String hashString(String str, String algorithm) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
            byte[] hash = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
            return toHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            SecureIdentity.getInstance().getLogger().info(SecureIdentity.getInstance().getDescription().getName() + " the hash algorithm that have been provided does not exists");
        }
        return null;
    }

    /**
     * Converts a byte array to a string
     * @param bytes the byte array
     * @return Returns the converted string
     */
    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Secure a password
     * @param password the plain text password
     * @param dynamic_salt the generated dynamic salt
     * @return Secured password hash
     */
    public static String securePassword(String password, String dynamic_salt) {
        String static_salt_hash = hashString(STATIC_SALT, ConfigUtils.getStringFromConfig("2FA_Password_Security.Static salt.hash algorithm"));

        String salted_password = static_salt_hash + password + dynamic_salt;

        for (int i = 0; i < ConfigUtils.getIntFromConfig("2FA_Password_Security.Passwords.hash layers"); i++)
            salted_password = hashString(salted_password, ConfigUtils.getStringFromConfig("2FA_Password_Security.Passwords.hash algorithm"));

        return salted_password;
    }
}
