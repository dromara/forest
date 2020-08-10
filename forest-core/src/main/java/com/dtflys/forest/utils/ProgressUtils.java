package com.dtflys.forest.utils;

import com.dtflys.forest.http.ForestRequest;

public class ProgressUtils {

    public static void printProgressBar(ForestProgress progress) {
        ForestRequest request = progress.getRequest();
//        System.out.println("Download \"" + request.getFilename() + "\"");
//        System.out.println();
        Object barLen = request.getAttachment("barLen");
        if (barLen != null) {
            printToPre(109);
        }
        String bar = buildBar(progress);
        System.out.print(bar);
        request.addAttachment("barLen", bar.length());
        if (progress.isDone()) {
            System.out.println("\nFile \"" + request.getFilename() + "\" Download Completed.");
        }
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
        barBuilder.append(" [");
        for (int i = 0; i <= 100; i++) {
            if (i > 0 && i <= rate) {
                barBuilder.append("=");
            } else {
                barBuilder.append(" ");
            }
        }
        barBuilder.append("]");
        return barBuilder.toString();
    }

    // 将光标后移 num 位
    private static void printToPre(int num) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < num; i++){
            builder.append("\b");
        }
        System.out.print(builder.toString());
    }

    // 在光标位开始 即索引在第一个 - 时 开始输出num个 >
    private static void  printEnd(int num) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < num; i++ ) {
            builder.append(">");
        }
        System.out.print(builder.toString());
    }
}
