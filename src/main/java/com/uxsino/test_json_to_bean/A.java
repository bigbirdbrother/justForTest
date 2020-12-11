package com.uxsino.test_json_to_bean;

import java.util.Arrays;
import java.util.HashMap;

public class A {
    private String a;
    private String b;
    private HashMap<String,String>[] vlist;
    private HashMap<String,String> llist;

    public HashMap<String, String> getLlist() {
        return llist;
    }

    public void setLlist(HashMap<String, String> llist) {
        this.llist = llist;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }


    @Override
    public String toString() {
        return "A{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", vlist=" + Arrays.toString(vlist) +
                '}';
    }

    public HashMap<String, String>[] getVlist() {
        return vlist;
    }

    public void setVlist(HashMap<String, String>[] vlist) {
        this.vlist = vlist;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
}
