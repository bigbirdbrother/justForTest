package no_lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main2 {
    //队列长度
    private static final int QUEUE_LENGTH = 1024;
    private static final int PRO_NUM = 100;
    private static final int CUM_NUM = 10;
    private static final int DATA = 10000;
    private static LinkedBlockingQueue<Integer> queue;
    //private static CountDownLatch countDownLatch;

    public static void main(String[] args) {
        queue = new LinkedBlockingQueue<Integer>(QUEUE_LENGTH);
        //countDownLatch=new CountDownLatch(QUEUE_LENGTH);
        List<Thread> proThread=new ArrayList<Thread>();
        List<Thread> cusThread=new ArrayList<Thread>();
        long begin=System.currentTimeMillis();
        for(int i=0;i<PRO_NUM;i++){
            proThread.add( new Thread(new Producer(begin)));
        }

        for(int i=0;i<CUM_NUM;i++){
            cusThread.add(new Thread(new Customer(begin)));
        }

        for (Thread t:cusThread){
            t.start();
        }

        for (Thread t:proThread){
            t.start();
        }


    }

    static class Producer implements Runnable{
        long begin=0L;

        public void setBegin(long begin) {
            this.begin = begin;
        }

        public Producer(long begin){
            this.begin=begin;
        }
        @Override
        public void run() {
            int i = 1;
            while (i++<=DATA){
                try {
                    queue.put(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Customer implements Runnable{
        long begin=0L;

        public void setBegin(long begin) {
            this.begin = begin;
        }

        public Customer(long begin){
            this.begin=begin;
        }
        @Override
        public void run() {
            while (true){
                Integer o = null;
                try {
                    o = (Integer) queue.take();
                    if (o % DATA == 0) {
                        System.out.println(Thread.currentThread().getName() + "阻塞队列消费" + o + "当前时间" + ((double) (System.currentTimeMillis() - begin)) / 1000 + "s");
                    }
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
            }
        }
    }


}
