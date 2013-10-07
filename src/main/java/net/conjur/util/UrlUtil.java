package net.conjur.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 */
public class UrlUtil {
    public static String urlEncode(String s){
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
