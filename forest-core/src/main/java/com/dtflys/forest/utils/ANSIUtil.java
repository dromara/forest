package com.dtflys.forest.utils;

public class ANSIUtil {

    private final static String OS_NAME = System.getProperty("os.name");

    private final static boolean isWindows = OS_NAME.startsWith("Win");

    private final static boolean isMac = OS_NAME.startsWith("Mac");

    private final static boolean isUnix = isMac || OS_NAME.contains("nix") || OS_NAME.contains("nux") || OS_NAME.contains("aix");

    public final static int COLOR_CHARS_LENGTH = isUnix ? 8 : 0;

    public final static String COLOR_RESET = isUnix ? "\u001B[0m" : "";

    public final static String COLOR_BLACK = isUnix ? "\u001B[30m" : "";

    public final static String COLOR_RED = isUnix ? "\u001B[31m" : "";

    public final static String COLOR_GREEN = isUnix ? "\u001B[32m" : "";

    public final static String COLOR_YELLOW = isUnix ? "\u001B[33m" : "";

    public final static String COLOR_BLUE = isUnix ? "\u001B[34m" : "";

    public final static String COLOR_PURPLE = isUnix ? "\u001B[35m" : "";

    public final static String COLOR_CYAN = isUnix ? "\u001B[36m" : "";

    public final static String COLOR_GRAY = isUnix ? "\u001B[37m" : "";

    public final static String COLOR_END = isUnix ? "\u001B[0m" : "";


    public static String colorRed(String text) {
        if (isUnix) {
            return "\u001B[31m" + text + "\u001B[0m";
        }
        return text;
    }

    public static String colorGreen(String text) {
        if (isUnix) {
            return "\u001B[32m" + text + "\u001B[0m";
        }
        return text;
    }

    public static String colorYellow(String text) {
        if (isUnix) {
            return "\u001B[33m" + text + "\u001B[0m";
        }
        return text;
    }

    public static String colorBlue(String text) {
        if (isUnix) {
            return "\u001B[34m" + text + "\u001B[0m";
        }
        return text;
    }

    public static String colorPurple(String text) {
        if (isUnix) {
            return "\u001B[35m" + text + "\u001B[0m";
        }
        return text;
    }

    public static String colorCyan(String text) {
        if (isUnix) {
            return "\u001B[36m" + text + "\u001B[0m";
        }
        return text;
    }

    public static String colorGray(String text) {
        if (isUnix) {
            return "\u001B[37m" + text + "\u001B[0m";
        }
        return text;
    }

}
