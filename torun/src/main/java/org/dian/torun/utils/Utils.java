package org.dian.torun.utils;

import java.util.Map;

/**
 * Created by BaiYa on 2014/5/25.
 */
public class Utils {

    public static String printMap(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            stringBuilder.append("| key=" + key + "; value=" + value);
        }
        return stringBuilder.toString();
    }

}
