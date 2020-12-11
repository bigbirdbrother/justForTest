package com.uxsino.iterator_test;

import java.util.ArrayList;
import java.util.Iterator;

public class Test {
    public static void main(String[] args) {
        ArrayList<Object> list = new ArrayList<>();
        list.add("5");
        list.add("342");
        list.add("sdfdsa");
        System.out.println("source:-------------------------------");
        System.out.println(list);
        System.out.println("--------------------------------------");
        Iterator<Object> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().equals("5")){
                it.remove();
            }
        }
        System.out.println(list);

    }
}
