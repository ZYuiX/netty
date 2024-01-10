package com.wit.xzy.bio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author ZongYou
 **/
public class BioServer {
    public static void main(String[] args) throws FileNotFoundException {
        // channel = new RandomAccessFile("文件名","r")
        int corePoolSize = 5;//核心线程数量
        int MaxPoolSize = 10;//最大线程数量
        long KeepAliveTime = 100;//
        TimeUnit unit = TimeUnit.MINUTES;
        BlockingQueue<Runnable>blockingQueue = new ArrayBlockingQueue<Runnable>(10);//阻塞队列
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,MaxPoolSize,KeepAliveTime,unit,blockingQueue);
        //1.创建服务器去监听一个端口
        try (ServerSocket serverSocket = new ServerSocket(8090)){
            System.out.println("服务器启动了.....");
            while (true){
                System.out.println("线程信息"+Thread.currentThread().getId()
                        +"线程名字"+Thread.currentThread().getName());
                //阻塞等待客户端连接
                Socket socket = serverSocket.accept();//监听端口
                System.out.println("等待客户端连接....");
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        handle(socket);
                    }
                });

            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void handle(Socket socket){
        try {
            byte[] bytes = new byte[1024];
            System.out.println("线程信息"+Thread.currentThread().getId()
                    +"线程名字"+Thread.currentThread().getName());
            //通过Socket获取输入流
            InputStream inputStream = socket.getInputStream();
            //循环读取客户端发送的数据
            while (true){
                int read = inputStream.read(bytes);
                if(read!=-1){
                    System.out.println(new String(bytes,0,read));//输出客户端发送的数据
                }else {
                    break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("关闭连接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
