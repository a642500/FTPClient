package me.toxz.ftp.util;

/**
 * Created by Carlos on 2015/10/26.
 */
public class Log {

    public static void i(String tag, String message) {
        System.out.println(tag + ": " + message);
    }

    public static void i(String tag, String message, Throwable throwable) {
        i(tag, message);
        System.err.println(throwable.getMessage());
    }
}
