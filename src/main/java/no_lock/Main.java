package no_lock;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    //队列长度
    private static final int QUEUE_LENGTH = 1024;
    private static final int PRO_NUM = 100;
    private static final int CUM_NUM = 10;
    private static final int DATA = 10000;

    private static ConcurrentLoopQueue queue;
    //private static CountDownLatch countDownLatch;

    public static void main(String[] args) {
        queue = new ConcurrentLoopQueue(QUEUE_LENGTH);
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


        System.out.println("begin=="+begin);
        for (Thread t:cusThread){
            t.start();
        }
        for (Thread t:proThread){
            t.start();
        }

        System.out.println("startTime="+System.currentTimeMillis());
    }

    static class Producer implements Runnable{
        long begin=0L;
        public Producer(long begin){
            this.begin=begin;
        }
        @Override
        public void run() {
            long i = 1;
            while (i++<=DATA){
                queue.enqueue(i);
            }
        }
    }

    static class Customer implements Runnable{
        long begin=0L;
        public Customer(long begin){
            this.begin=begin;
        }
        @Override
        public void run() {
            while (true){
                Long o = (Long)queue.dequeue();
                if(o%DATA==0){
                    System.out.println(Thread.currentThread().getName()+"消费"+o+"当前时间"+((double)(System.currentTimeMillis()-begin))/1000+"s");
                }
            }
        }
    }

    public static class ConcurrentLoopQueue {
        private int capacity;
        private AtomicInteger head;
        private AtomicInteger tail;
        private volatile Object[] array;


        public  ConcurrentLoopQueue(int capacity) {
            this.capacity = capacity;
            array = new Object[capacity];
            head = new AtomicInteger(0);
            tail = new AtomicInteger(0);
        }


        public boolean enqueue(Object ele){
            boolean flag = false;
            int preTail = 0;
            int nextTail = 0;
            while(!flag){
                preTail = tail.get();
                while((preTail + 1) % capacity == head.get()){ //队列已满,自旋
                    //return false;
                }

                nextTail = (preTail + 1) % capacity;        //取模运算保证的是环形
                //通过CAS保证线程安全
                if(tail.compareAndSet(preTail,nextTail)){
                    array[preTail] = ele;
                    return true;
                }
            }
            return false;
        }

        public Object dequeue(){
            int preHead = 0;
            int nextHead = 0;
            while(true){
                preHead = head.get();
                while(preHead == tail.get()){ // 队列空,自旋
                    //return null;
                }
                nextHead = (preHead + 1) % capacity;        //取模运算保证的是环形
                //通过CAS保证线程安全
                if(array[preHead] != null && head.compareAndSet(preHead,nextHead)){
                    return array[preHead];
                }
            }
        }
    }
}
