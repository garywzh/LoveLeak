package xyz.garywzh.loveleak.util;

/**
 * Created by garywzh on 2016/9/21.
 */

public class TextStyleUtil {

    public static String clearStyle(String s) {
        return s.replace("&quot;", "\"")
            .replace("<span class=\"highlight\">", "")
            .replace("</span>", "");
    }
}
