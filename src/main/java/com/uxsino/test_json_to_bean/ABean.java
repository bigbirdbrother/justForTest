package com.uxsino.test_json_to_bean;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ABean{
    private A[] aArr;

    @Override
    public String toString() {
        return "ABean{" +
                "aArr=" + Arrays.toString(aArr) +
                '}';
    }

    public A[] getaArr() {
        return aArr;
    }

    public void setaArr(A[] aArr) {
        this.aArr = (A[]) aArr;
    }


    public static void main(String[] args) {
        try {
            ABean aBean = JSON.parseObject(FileUtils.readFileToString(new File("D:\\justForTest\\src\\main\\java\\com\\uxsino\\test_json_to_bean\\test.json"), "utf-8"), ABean.class);
            System.out.println(aBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

