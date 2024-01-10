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
        channel.bind(new InetSocketAddress(9090));
        //2.连接集合
        List<SocketChannel> channelList = new ArrayList<>();
        while (true){
            //3.accpet()阻塞直到客户端发送请求
            log.info("等待连接....");
            SocketChannel sc = channel.accept();//
            log.info("连接成功....{}",sc);
            channelList.add(sc);//将该channel加入集合
            for(SocketChannel mychannel:channelList){
                //4.接收客户端发送的数据
                log.info("before read.....");
                mychannel.read(byteBuffer);//使用read()方法写入数据时，也是一种阻塞式写入，读取数据时，如果没有数据可读，通道的读取操作可能会阻塞
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
