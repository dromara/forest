package com.dtflys.forest.utils;

public class ANSIUtil {

    private final static String OS_NAME = System.getProperty("os.name");

    private final static boolean isWindows = OS_NAME.startsWith("Win");

    private final static boolean isMac = OS_NAME.startsWith("Mac");

    private final static boolean isUnix = isMac || OS_NAME.contains("nix") || OS_NAME.contains("nux") || OS_NAME.contains("aix");
    
    private final static boolean isSupportColor = isWindows || isMac || (!ReflectUtils.isAndroid() && !isUnix);

    public final static int COLOR_CHARS_LENGTH = isSupportColor ? 8 : 0;

    public final static String COLOR_RESET = isSupportColor ? "\u001B[0m" : "";

    public final static String COLOR_BLACK = isSupportColor ? "\u001B[30m" : "";

    public final static String COLOR_RED = isSupportColor ? "\u001B[31m" : "";

    public final static String COLOR_GREEN = isSupportColor ? "\u001B[32m" : "";

    public final static String COLOR_YELLOW = isSupportColor ? "\u001B[33m" : "";

    public final static String COLOR_BLUE = isSupportColor ? "\u001B[34m" : "";

    public final static String COLOR_PURPLE = isSupportColor ? "\u001B[35m" : "";

    public final static String COLOR_CYAN = isSupportColor ? "\u001B[36m" : "";

    public final static String COLOR_GRAY = isSupportColor ? "\u001B[37m" : "";

    public final static String COLOR_END = isSupportColor ? "\u001B[0m" : "";


    public static String colorRed(String text) {
        return COLOR_RED + text + COLOR_END;
    }

    public static String colorGreen(String text) {
        return COLOR_GREEN + text + COLOR_END;
    }

    public static String colorYellow(String text) {
        return COLOR_YELLOW + text + COLOR_END;
    }

    public static String colorBlue(String text) {
        return COLOR_BLUE + text + COLOR_END;
    }

    public static String colorPurple(String text) {
        return COLOR_PURPLE + text + COLOR_END;
    }

    public static String colorCyan(String text) {
        return COLOR_CYAN + text + COLOR_END;
    }

    public static String colorGray(String text) {
        return COLOR_GRAY + text + COLOR_END;
    }

}
