package com.framgia.tungvd.soundcloud.util;

public class UsefulFunc {
    public static String convertProgressToTime(long progress) {
        long hour = progress / 3600;
        long second = progress % 60;
        long minute = (progress - hour * 3600) / 60;
        String sec = second / 10 != 0 ?
                String.valueOf(second) : new StringBuilder("0").append(second).toString();
        String min = minute / 10 != 0 ?
                String.valueOf(minute) : new StringBuilder("0").append(minute).toString();
        String hou = hour / 10 != 0 ?
                String.valueOf(hour) : new StringBuilder("0").append(hour).toString();
        String txt = "";
        if (hour == 0) {
            txt = String.format("%s:%s", min, sec);
        } else {
            txt = String.format("%s:%s:%s", hou, min, sec);
        }
        return txt;
    }
}
