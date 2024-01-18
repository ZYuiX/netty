package com.wit.xzy.nio.Mthread;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @Author ZongYou
 **/
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey sscKey = ssc.register(boss, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(9090));


        //1.初始化一个worker
        Worker worker = new Worker("Worker-0");
        //worker.register();移到下面

        while (true){
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.info("connect.....{}",sc.getRemoteAddress());
                    //2.关联 selector
                    log.info("befor register.....{}",sc.getRemoteAddress());
                    worker.register(sc);//这段代码运行在worker-0线程上。初始selector，启动Worker-0

                    log.info("after register.....{}",sc.getRemoteAddress());

                }

            }


        }

    }
    /**
     要在两个线程之间传递数据，且让两段代码在指定位置执行，可以使用消息队列
    */
     static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean start = false;
        private ConcurrentLinkedDeque<Runnable>queue = new ConcurrentLinkedDeque<>();
        public Worker(String name) {
            this.name = name;
        }
        //初始化线程与selector；
        public  void register(SocketChannel sc) throws IOException {
            if(!start){
                thread =  new Thread(this,name);
                selector = Selector.open();
                thread.start();
                start=true;
            }
            //将要执行的任务加入队列
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);//这个代码运行在主线程也就是boss线程
                }catch (ClosedChannelException e){
                    e.printStackTrace();
                }
            });

        }


        @Override
        public void run() {
            while (true){
                try {
                    selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if(key.isReadable()){
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel =(SocketChannel) key.channel();
                            log.info("read...{}",channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
