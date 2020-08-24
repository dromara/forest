package com.dtflys.forest.utils;

import com.dtflys.forest.http.ForestRequest;

public class ProgressUtils {

    public static void printProgressBar(ForestProgress progress) {
        ForestRequest request = progress.getRequest();
        if (progress.isBegin()) {
            System.out.println("Download \"" + request.getFilename() + "\"\n");
        }

        System.out.print("\033[200D\033[1A\033[K");
        String pre = stringToPre(200);
        String bar = buildBar(progress);
        System.out.print(pre + bar);
//        System.out.print("\033[1A");
        if (progress.isDone()) {
            System.out.println("\n\nFile \"" + request.getFilename() + "\" Download Completed.");
        }
//        System.out.print("\033[?25h");
    }

    private static String buildBar(ForestProgress progress) {
        int rate = (int) Math.round(progress.getRate() * 100);
        String percentage = rate + "%";
        StringBuilder barBuilder = new StringBuilder();
        barBuilder.append(" ");
        switch (percentage.length()) {
            case 2:
                barBuilder.append("  ");
                break;
            case 3:
                barBuilder.append(" ");
                break;
        }
        barBuilder.append(percentage);
        barBuilder.append("\u001b[34m");
        barBuilder.append("  ");
        for (int i = 0; i <= 100; i++) {
            if (i <= rate) {
                barBuilder.append("█");
            } else {
                barBuilder.append("-");
            }
        }
        barBuilder.append(" ");
        barBuilder.append("\u001b[0m");
        return barBuilder.toString();
    }

    // 将光标后移 num 位
    private static void printToPre(int num) {
        for (int i = 0; i < num; i++) {
            System.out.print("\b");
        }
    }

    private static String stringToPre(int num) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < num; i++){
            builder.append("\b");
        }
        return builder.toString();
    }

}
