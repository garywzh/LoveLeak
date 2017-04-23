package xyz.garywzh.loveleak.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by garywzh on 2017/4/23.
 */

public class ParseUtil {

    private static final String PATTERN_WEB_URL = "LiveLeak-dot-com-(\\w{3}_\\d+)";
    private static final Pattern pattern = Pattern.compile(PATTERN_WEB_URL);

    public static String parseWebUrl(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
