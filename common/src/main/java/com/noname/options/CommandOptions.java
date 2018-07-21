package com.noname.options;

import java.util.*;

public class CommandOptions<K extends Enum<K> & IOption> {
    public static final String DELIMITER = ",";
    private final EnumMap<K, List<Object>> map;
    private final Class<K> clz;

    public CommandOptions(String[] args, Class<K> clz) {
        this.map = new EnumMap<>(clz);
        this.clz = clz;
        parseArgs(args);
    }

    //  {"-tcpPort", "9123", "-tcpAllowOthers", "-baseDir", "/tmp"};
    private void parseArgs(String[] args) {

        boolean prevIsKey = false;
        final Map<String, K> kMap = new HashMap<>();
        for (K key : clz.getEnumConstants()) {
            final String name = key.getName();
            kMap.put(name, key);
        }
        for (int i = 0; i < args.length; i++) {
            final String value = args[i];
            final boolean isKey = isKey(value);
            if (isKey && value.length() < 2) {
                throw new IllegalArgumentException("There is space after '-' symbol");
            }
            if (isKey && !prevIsKey) { //key
                prevIsKey = true;
            } else {
                final K key = kMap.get(args[i - 1]);
                if (key == null) {
                    throw new IllegalArgumentException(String.format("Unknown arg %s", args[i - 1]));
                }
                if (isKey) {
                    put(key, "true");
                    prevIsKey = true;
                } else { //value
                    put(key, value);
                    prevIsKey = false;
                }
            }
        }
    }


    public <T> List<T> getValues(K key, Class<T> clz) {
        final List<Object> objects = map.get(key);
        List<T> res = new ArrayList<>();
        for (Object object : objects) {
            res.add(clz.cast(object));
        }
        return Collections.unmodifiableList(res);
    }

    public <T> T getValue(K key, Class<T> clz) {
        final List<Object> objects = map.get(key);
        return clz.cast(objects.get(0));
    }


    private void put(K key, String value) {
        map.compute(key, (k, oldValue) -> {
           // List<Object> splitList = split(value).stream().map(key::convert).collect(Collectors.toList());
            //Object valObj = key.convert(value);
            final List<String> split = split(value);
            final List<Object> objList = new ArrayList<>();
            for (String str : split) {
                objList.add(key.convert(str));
            }
            if (oldValue == null) {
                return new ArrayList<>(objList);
            } else {
                oldValue.addAll(objList);
                return oldValue;
            }
        });

    }

    private List<String> split(String value) {
       return Arrays.asList(value.split(DELIMITER));
    }

    private static boolean isKey(String a) {
        return a.charAt(0) == '-';
    }
}
