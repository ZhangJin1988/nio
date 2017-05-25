package com.nio.test;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author zhangjin
 * @since 2017/5/25
 * 1 通道 Channel 用于源节点与目标节点的连接  在Java NIO 中负责缓冲区数据的传输 Channel本书不存储数据 因此需要配合缓冲区进行传输
 * <p>
 * 2 通道主要的实现类
 * java.nio.channels.Channel 接口 :
 * |-- FileChannel
 * |-- SocketChannel
 * |-- ServerSocketChannel
 * |-- DatagramChannel
 * 3 获取通道
 * 1， java针对支持通道的类提供了 getChannel()类
 * 本地IO
 * FileInputStream/FileOutputStream
 * RandomAccessFile
 * 网络IO
 * Socket
 * SeverSocket
 * DatagramSocket
 * 2，在JDK1.7 中 NIO。2 针对各个通道提供了静态方法 open()
 * 3 在JDK1.7中 NIO。2 的Files 工具类的newByteChannel()
 * <p>
 * <p>
 * 4 通道之间的数据传输
 * transferFrom()
 * transferTo()
 * <p>
 * <p>
 * 5 分散 Scatter 与 聚集 Gather
 * 分散读取 Scattering Reads 将通道中的数据分散到多个buffer中
 * 聚集写入 Gathering Writes 将多个缓冲区中的数据聚集到通道中
 * <p>
 * <p>
 * 6 字符集
 * 编码  字符 到 字节
 * 解码  字节 到 字符
 */
public class TestChannel {

    @Test
    public void test1() throws IOException {
        long start = System.currentTimeMillis();

        FileInputStream fis = null;
        FileOutputStream fos = null;

        //1 获取通道
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");

            //1 获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //2 分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            //3 将缓冲区中的数据存入缓冲区中
            while (inChannel.read(buf) != -1) {
                buf.flip();//切换读取模式
                //4 将缓冲区中的数据写入通道中
                outChannel.write(buf);
                buf.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (outChannel != null) {
                outChannel.close();
            }
            if (inChannel != null) {
                inChannel.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (fis != null) {
                fis.close();
            }
        }

        long end = System.currentTimeMillis();
        System.out.println(end - start);

    }

    //直接使用缓冲区完成文件的复制 (内存映射文件)

    @Test
    public void test2() throws IOException {

        long start = System.currentTimeMillis();
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
        //内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());

        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        //直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();

        long end = System.currentTimeMillis();
        System.out.println(end - start);

    }

    @Test
    public void test3() throws IOException {

        long start = System.currentTimeMillis();
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

//        inChannel.transferTo(0,inChannel.size(),outChannel);

        outChannel.transferFrom(inChannel, 0, inChannel.size());
        inChannel.close();
        outChannel.close();

    }

    //分散 和 聚集
    @Test
    public void test4() throws IOException {

        RandomAccessFile raf1 = new RandomAccessFile("1.txt", "rw");
        //1 获取通道
        FileChannel channel = raf1.getChannel();

        //2 分配指定大小的缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(1024);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);

        //3 分散读取

        ByteBuffer[] bufs = {buf1, buf2};
        channel.read(bufs);

        for (ByteBuffer byteBuffer : bufs) {
            byteBuffer.flip();
        }

        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

        //4 聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
        FileChannel ch = raf2.getChannel();

        ch.write(bufs);

    }

    @Test
    public void test5() {
        SortedMap<String, Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> entries = map.entrySet();
        for (Map.Entry entry : entries) {
            System.out.println(entry.getValue());

        }
    }

    @Test
    public void test6() throws CharacterCodingException {
        Charset cs1 = Charset.forName("UTF-8");

        //获取编码器
        CharsetEncoder ce = cs1.newEncoder();

        //获取解码器
        CharsetDecoder cd = cs1.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);

        cBuf.put("中国");
        cBuf.flip();



        //编码
        ByteBuffer bBuf = ce.encode(cBuf);

        for (int i = 0; i <6; i++) {
            System.out.println(bBuf.get());

        }

        //解码
        bBuf.flip();
        CharBuffer decode = cd.decode(bBuf);
        System.out.println(decode.toString());

    }

}
