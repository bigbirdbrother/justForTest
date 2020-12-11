package com.uxsino;

import com.uxsino.entity.Dog;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.net.URL;

public class EhcacheTest {
    public static void main(String[] args) {
        //1. 加载配置文件
        URL url = EhcacheTest.class.getClassLoader().getResource("ehcache.xml");

        //2. 创建CacheManager缓存管理器
        CacheManager cacheManager = CacheManager.create(url);
        //3. 获取缓存
        Cache ct = cacheManager.getCache("CacheTest");

        //4. 打包数据,封装到Element中, (key, value)
        Element e = new Element("towDog", new Dog("towDog", 2));
        Element e2 = new Element(null, new Dog("null", 3));
        Element e3 = new Element("", new Dog("empty", 2));
        //5. 将Element压入缓存
        ct.put(e);
        ct.put(e2);
        ct.put(e3);

        //6. 通过get(key)获取缓存中的内容
        //缓存get方法不消耗对象
        Element element = ct.get("towDog");

        //getObjectKey()获取key
        Object objectKey = element.getObjectKey();
        System.out.println("ok="+objectKey);
        //getObjectValue()获取value
        Object objectValue = element.getObjectValue();
        System.out.println("ov="+objectValue);

        System.out.println("get(towDog)="+ct.get("towDog"));
        System.out.println("get(unExist)="+ct.get("unExist"));
        System.out.println("get(null)="+ct.get(null));
        System.out.println("get（“”）="+ct.get(""));

        System.out.println("------------------------------------------");
        System.out.println("get(towDog)="+ct.get("towDog"));
        //remove()可以删除Element,
        boolean removeFlag = ct.remove("towDog");
        System.out.println("isRemove="+removeFlag);
        System.out.println("get(towDog)="+ct.get("towDog"));

        System.out.println("------------------------------------------");
        removeFlag = ct.remove("towDog");
        System.out.println("isRemoveAgain="+removeFlag);

        /**
         * ok=towDog
         * ov=Dog(name=towDog, id=2)
         * get(towDog)=[ key = towDog, value=Dog(name=towDog, id=2), version=1, hitCount=2, CreationTime = 1599202799338, LastAccessTime = 1599202799352 ]
         * get(unExist)=null
         * get(null)=null
         * get（“”）=[ key = , value=Dog(name=empty, id=2), version=1, hitCount=1, CreationTime = 1599202799338, LastAccessTime = 1599202799353 ]
         * ------------------------------------------
         * get(towDog)=[ key = towDog, value=Dog(name=towDog, id=2), version=1, hitCount=3, CreationTime = 1599202799338, LastAccessTime = 1599202799354 ]
         * isRemove=true
         * get(towDog)=null
         * ------------------------------------------
         * isRemoveAgain=false
         */


    }
}
