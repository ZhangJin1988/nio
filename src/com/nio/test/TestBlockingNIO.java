package com.nio.test;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author zhangjin
 * @since 2017/5/25
 * 一 使用NIO 完成网络通信的三个核心
 *  1 通道 Channel  负责连接
 *          java.nio.channels.Chnnel 接口:
 *                  |-- SocketChannel
 *                  |-- ServerSocketChannel
 *                  |-- DatagramChannel
 *
 *                  |-- Pipe.SinkChannel
 *                  |-- Pipe.SourceChannel
 *  2 缓冲区 Buffer 负责数据的读取
 *  3 选择器 Selector 是 SelectableChannel 的多路复用器 用于监控 SelectableChannel 的IO 状况
 */
public class TestBlockingNIO {


    @Test
    public void client() throws IOException {

        //1 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);

        //2.分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //3,读取本地文件 并且发送到服务端

        while (inChannel.read(buf)!=-1){
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        //4 关闭通道
        sChannel.close();
        inChannel.close();
    }

    @Test
    public void server() throws IOException {

        //1 获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);

        //2 绑定连接
        ssChannel.bind(new InetSocketAddress(9898));

        //3 获取客户端连接的通道
        SocketChannel sChannel = ssChannel.accept();

        //4 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //5 接收客户端的数据 并保存到本地

        while (sChannel.read(buf)!=-1){
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        //6关闭连接
        sChannel.close();
        outChannel.close();
        ssChannel.close();
    }
}
