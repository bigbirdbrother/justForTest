package com.uxsino;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EntrySetTest {
    public static void main(String[] args) {
        Map<String, String> sb = new HashMap<>();
        sb.put("张三","23");
        sb.put("李四","24");
        Set<Map.Entry<String, String>> entries = sb.entrySet();
        Iterator<Map.Entry<String, String>> it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> man = it.next();
            System.out.println(man.getKey()+"="+man.getValue());
        }


    }
}
