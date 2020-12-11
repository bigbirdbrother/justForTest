package com.uxsino;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;


public class testFileUtiles {
    //private static Logger LOGGER = LoggerFactory.getLogger(testFileUtiles.class);

    public static void main(String[] args) {
        DecimalFormat fomat = new DecimalFormat("###############0.0000#");// 最多保留几位小数，就用几个#，最少位就用0来确定
        DecimalFormat format = new DecimalFormat("###############0.####");// 最多保留几位小数，就用几个#，最少位就用0来确定
        Double bb = -4.99183553218834E-12;
        String s = fomat.format(bb);
        System.out.println(s);
    }

//    public static void main(String[] args) {
//        String privilegedUser = System.getProperty("privilegedUser");
//        String privilegedPassword = System.getProperty("privilegedPassword");
//        System.out.println(privilegedUser+"\t"+privilegedPassword);
//    }

    /**
     * test System.getProperty(String key)
     * the method can get the system message by the spasific key
     */
//    @Test
//    public void testSystemGetProperty() {
//        String[] keys = { "java.version", "java.vendor", "java.vendor.url", "java.home",
//                "java.vm.specification.version", "java.vm.specification.vendor", "java.vm.specification.name",
//                "java.vm.version", "java.vm.vendor", "java.vm.name", "java.specification.version",
//                "java.specification.vendor", "java.specification.name", "java.class.version", "java.class.path",
//                "java.library.path", "java.io.tmpdir", "java.compiler", "java.ext.dirs", "os.name", "os.arch",
//                "os.version", "file.separator", "path.separator", "line.separator", "user.name", "user.home",
//                "user.dir" };
//
//        for (String key : keys) {
//            String value = System.getProperty(key);
//            System.out.println(key + " : " + value);
//        }
//
//
//    }
//
//    @Test
//    public void testLog4j(){
//        LOGGER.debug("HAHA");
//        LOGGER.error("yousillyb");
//    }

    @Before
    public void before(){}

//    @Test

    @After
    public void after(){}


}
