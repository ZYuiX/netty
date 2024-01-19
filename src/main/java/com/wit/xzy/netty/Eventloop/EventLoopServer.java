package com.wit.xzy.netty.Eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @Author ZongYou
 **/
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {

        //NioEventLoopGroup Worker = new NioEventLoopGroup();//默认为核心线程数*2
        //分工细化 boss worker
        //BOSS只负责accept时间，Worker只负责socketChannel上的读写时间

        EventLoopGroup group = new DefaultEventLoopGroup();//创建一个额外的普通时间，可以将一些耗时较长的事件交给这个EventLoopGroup处理
        new ServerBootstrap()
                //          bossEventLoopGroup            WorkerEventLoopGroup
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void  channelRead(ChannelHandlerContext ctx, Object msg){
                                        ByteBuf bf = (ByteBuf) msg;
                                        log.info(Thread.currentThread().getName()+" - "+bf.toString(Charset.defaultCharset()));
                                        ctx.fireChannelRead(msg);//这个方法可以将msg传递给下一个handler
                                    }
                                }).addLast(group,"handler2",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void  channelRead(ChannelHandlerContext ctx, Object msg){
                                        ByteBuf bf = (ByteBuf) msg;
                                        log.info(Thread.currentThread().getName()+" - "+bf.toString(Charset.defaultCharset()));
                                    }
                                });
                            }
                        })
                .bind(9080);
    }
    }
