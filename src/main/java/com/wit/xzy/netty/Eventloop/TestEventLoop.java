package com.wit.xzy.netty.Eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @Author ZongYou
 **/
public class TestEventLoop {
    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();//可以管理 io 普通任务 定时任务
    }

}
