package com.wit.xzy.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author ZongYou
 **/
@Slf4j
public class server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //1.创建selector
        Selector selector = Selector.open();
        //2.将selector与channel关联。SelectionKey就是将来事件发生后，通过这个key可以知道事件和是那个channel的事件
        //selector对象内部有一个集合，当有channel要注册到selector时，就会把这个channel放入selector内部的集合中
        //将ssc加入到selector内部集合。
        SelectionKey ssckey = ssc.register(selector, 0, null);
        ssckey.interestOps(SelectionKey.OP_ACCEPT);//指定selector关注的事件类型
        ssc.bind(new InetSocketAddress(9090));
        log.info("register key:{}",ssckey);
        while (true){
            //3.select()，没有事件发生时阻塞，有时间发生回复运行。
            //select()在事件未处理时，他不会阻塞(如果客户端连接以后，不做任何操作，那么服务端就会重复监测是否有accept()事件，可以用cancel()取消该事件。)
            // 事件发生要么处理要么取消，不能置之不理
            selector.select();//监听到服务端的连接后，出发accept，会将ssc加入到selectedKeys()的内部set集合中。但是selectedKeys()不会主动删除这个ssckey
            //4.处理事件，selectedKeys()内部包含了所有发生的时间。
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                //一定要处理key，要从selectedKeys()中删除，
                // 否则下次有新的channel注册，触发事件，从迭代器迭代获取key时，会从集合头部获取上一个事件的key，而这个事件并没有触发，继续
                //SocketChannel sc = channel.accept();这个地方就会报错。因此要主动移除selectedKeys()的key
                //此例子中
                iterator.remove();//主动移除
                log.info("key:{}",key);
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    SelectionKey register = sc.register(selector, 0, null);
                    SelectionKey selectionKey = register.interestOps(SelectionKey.OP_READ);
                    log.info("{}",sc);
                }else  if(key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel)key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(6);
                        int read = channel.read(byteBuffer);//客户端正常断开，read的返回值是-1
                        if(read==-1){
                            key.cancel();
                        }else {
                            byteBuffer.flip();
                            System.out.println(Charset.defaultCharset().decode(byteBuffer));

                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        key.cancel();//客户端异常断开，因此需要将key取消(从selector的key集合中删除)
                    }

                }


            }

        }
    }
}

//selector对象内部有一个集合，当有channel要注册到selector时，就会把这个channel放入selector内部的集合中
//selectedKeys() public abstract Set<SelectionKey> selectedKeys();可以看到selectedKeys()是一个set集合，
// 每当有事件发生，selector就会向selectedKeys集合中填充对应channel。
