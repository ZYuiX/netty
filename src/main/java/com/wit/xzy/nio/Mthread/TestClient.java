package com.wit.xzy.nio.Mthread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @Author ZongYou
 **/
public class TestClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",9090));
        sc.write(Charset.defaultCharset().encode("2137893127390"));
        System.in.read();
    }
}
