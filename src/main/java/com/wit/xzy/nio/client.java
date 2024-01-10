package com.wit.xzy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @Author ZongYou
 **/
public class client {
    public static void main(String[] args) throws IOException {
//        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
//        ByteBuffer hello = Charset.defaultCharset().encode("hello");
//        byteBuffer.put(hello);
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",9090));
        socketChannel.write(Charset.defaultCharset().encode("hello"));
        System.out.println("waiting...");
    }
}
