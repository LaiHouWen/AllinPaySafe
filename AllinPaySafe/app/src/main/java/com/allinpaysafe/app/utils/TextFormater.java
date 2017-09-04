package com.allinpaysafe.app.utils;

public class TextFormater {
    public static String dataSizeFormat(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.2f GB", (float) size / gb);
        }
        else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        }
        else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        }
        else {
            return String.format("%d B", size);
        }
    }

    public static String[] dataSizeFormatArray(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        String[] result =new String[2];


        if (size >= gb) {
            result[0] = String.format("%.2f", (float) size / gb);
            result[1] = "GB";
            return result;
        }
        else if (size >= mb) {
            float f = (float) size / mb;
            result[0] = String.format(f > 100 ? "%.0f" : "%.1f", f);
            result[1] = "MB";
            return result;
        }
        else if (size >= kb) {
            float f = (float) size / kb;
            result[0] = String.format(f > 100 ? "%.0f" : "%.1f", f);
            result[1] = "KB";
            return result;
        }
        else {
            result[0] = String.format("%d", size);
            result[1] = "B";
            return result;
        }
    }
}
