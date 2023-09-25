package me.nort721.secureidentity.utils;

import java.util.UUID;

public class StringUtils {

    public static boolean containsNumbers(String str) {
        int count = 0;
        for (char c : str.toCharArray())
            if (Character.isDigit(c))
                count++;
        return count > 0;
    }

    public static boolean containsSymbols(String str) {
        int count = 0;
        for (char c : str.toCharArray())
            if (!Character.isDigit(c) && !Character.isLetter(c))
                count++;
        return count > 0;
    }

    public static boolean containsLowerCase(String str) {
        int count = 0;
        for (char c : str.toCharArray())
            if (Character.isLowerCase(c))
                count++;
        return count > 0;
    }

    public static boolean containsUpperCase(String str) {
        int count = 0;
        for (char c : str.toCharArray())
            if (Character.isUpperCase(c))
                count++;
        return count > 0;
    }

    /**
     * Untrim a uuid string (Credit to Rowin for providing that method)
     * @param uuid a uuid string
     * @return The untrimmed version of that uuid
     */
    public static String untrimUUID(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(20, "-");
        sb.insert(16, "-");
        sb.insert(12, "-");
        sb.insert(8, "-");
        return sb.toString();
    }
}
