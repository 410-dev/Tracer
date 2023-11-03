package me.hysong.tracer.backend;

import me.hysong.libhycore.CoreDate;

public class Records {
    public static StringBuilder sb = new StringBuilder();

    public static void add(String s) {
//        Get stack trace
        String stackTrace = Thread.currentThread().getStackTrace()[2].toString();
        String time = CoreDate.timestamp();
        String formatted = String.format("[%s] %s: %s\n", time, stackTrace, s);
        sb.append(formatted);
    }

    public static String get() {
        return sb.toString();
    }
}
