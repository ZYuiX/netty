package com.wit.xzy.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ZongYou
 **/
@Slf4j
public class server {
    public static void main(String[] args) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(15);
        //1.开启服务器并监听端口
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);//关闭阻塞式编程，开启非阻塞式编程。
        channel.bind(new InetSocketAddress(9090));
        //2.连接集合
        List<SocketChannel> channelList = new ArrayList<>();
        while (true){
            //3.accpet()阻塞直到客户端发送请求
            log.info("等待连接....");
            SocketChannel sc = channel.accept();//切换成非阻塞式编程，如果没有建立连接，accept()不会阻塞，sc==null；
            if(sc!=null){
                log.info("连接成功....{}",sc);
                sc.configureBlocking(false);//非阻塞模式
                channelList.add(sc);//将该channel加入集合
            }
            for(SocketChannel mychannel:channelList){
                //4.接收客户端发送的数据
                log.info("before read.....");
                int read = mychannel.read(byteBuffer);//非阻塞，线程会继续运行，如果没有读到数据，read返回0;
                if(read>0){
                    byteBuffer.flip();//切换为读状态
                    while (byteBuffer.hasRemaining()){
                        System.out.println(byteBuffer.get());
                    }
                    byteBuffer.clear();
                    log.info("after read.....");
                }
            }
        }
    }
}
