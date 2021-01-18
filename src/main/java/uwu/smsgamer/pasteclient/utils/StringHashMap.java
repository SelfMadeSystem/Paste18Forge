package uwu.smsgamer.pasteclient.utils;

import java.util.*;

public class StringHashMap<T> extends LinkedHashMap<T, String> {
    public StringHashMap() {
    }

    public StringHashMap(Object... objects) {
        T t = null;
        boolean sw = true;
        for (Object object : objects) {
            if (!sw) {
                put(t, object.toString());
                sw = true;
            } else {
                t = (T) object;
                sw = false;
            }
        }
    }

    public HashMap<String, T> getReversedMap() {
        HashMap<String, T> map = new HashMap<>();
        for (Map.Entry<T, String> e : entrySet()) {
            map.put(e.getValue(), e.getKey());
        }
        return map;
    }

    public static <T>StringHashMap<T> reverse(Object... objects) {
        StringHashMap<T> hashMap = new StringHashMap<>();
        String s = null;
        boolean sw = true;
        for (Object object : objects) {
            if (!sw) {
                hashMap.put((T) object, s);
                sw = true;
            } else {
                s = (String) object;
                sw = false;
            }
        }
        return hashMap;
    }
}
