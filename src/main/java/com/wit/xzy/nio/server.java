package com.wit.xzy.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author ZongYou
 **/
@Slf4j
public class server {
    public static void main(String[] args) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(15);

        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        //1.创建selector
        Selector selector = Selector.open();
        //2.将selector与channel关联。SelectionKey就是将来事件发生后，通过这个key可以知道事件和是那个channel的事件
        SelectionKey ssckey = channel.register(selector, 0, null);
        ssckey.interestOps(SelectionKey.OP_ACCEPT);//指定selector关注的事件类型

        channel.bind(new InetSocketAddress(9090));
        log.info("register key:{}",ssckey);
        while (true){
            //3.select()，没有事件发生时阻塞，有时间发生回复运行。
            //select()在事件未处理时，他不会阻塞(如果客户端连接以后，不做任何操作，那么服务端就会重复监测是否有accept()事件，可以用cancel()取消该事件。)
            // 事件发生要么处理要么取消，不能置之不理
            selector.select();
            //4.处理事件，selectedKeys()内部包含了所有发生的时间。
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                log.info("key:{}",key);
                /*ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                SocketChannel sc = serverSocketChannel.accept();
                log.info("{}",sc);*/
                //key.cancel();

            }

        }
    }
}
