package com.nio.test;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @author zhangjin
 * @since 2017/5/25
 * 1 缓冲区  Buffer 在Java NIO中 负责数据的存取。 缓冲区 就是数组 用于存储不同数据类型的数据
 * 根据数据类型不同（Boolean类型除外） 提供了 相应类型的缓冲区
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * <p>
 * 上述缓冲区的管理方式 几乎一致 都是通过 allocate() 获取缓冲区
 * <p>
 * 2 缓冲区获取数据的两个核心方法
 * put():存入数据到缓冲区当中
 * get():获取缓冲区中的数据
 * <p>
 * 3 缓冲区的四个核心属性:
 * capacity : 容量 表示缓冲区中最大存储数据的容量 一旦声明不能改变
 * limit: 界限，表示缓冲区中可以操作数据的大小 limit 后数据不能进行读写
 * position : 位置 表示缓冲区正在操作的位置
 * position <= limit <= capacity
 *
 * mark : 标记 表示记录当前position的位置 通过reset（） 恢复到mark的位置
 *
 * 4 直接缓冲区 和 非直接缓冲区
 *
 * 非直接缓冲区 ： 通过allocate() 方法分配缓冲区 将缓冲区建立在JVM的内存中
 * 直接缓冲区 ： 通过allocateDirect() 方法分配直接缓冲区 将缓冲区简历在物理内存中 可以提高效率
 *
 *
 */
public class TestBuffer {

    @Test
    public void test1() {

        String str = "hello world";

        ByteBuffer buf = ByteBuffer.allocate(1024);

        System.out.println("--------allocate-------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        buf.put(str.getBytes());
        System.out.println("--------put-------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        buf.flip();
        System.out.println("--------flip-------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        System.out.println(new String(dst,0,dst.length));


        System.out.println("--------get-------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());



        //可重复读 rewind
        buf.rewind();


        System.out.println("--------rewind-------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        // 清空缓冲区  但是缓冲区中的数据依然存在 但是处于被遗忘的状态
        buf.clear();

        System.out.println("--------clear-------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        System.out.println((char) buf.get());







    }



    @Test
    public void test2(){
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);


        buf.put(str.getBytes());

        buf.flip();

        byte[] dst = new byte[buf.limit()];


        buf.get(dst,0,2);

        System.out.println(new String(dst,0,2));
        System.out.println(buf.position());

        //mark 标记一下

        buf.mark();

        buf.get(dst,2,2);
        System.out.println(new String(dst,2,2));
        System.out.println(buf.position());

        //恢复的mark的位置
        buf.reset();

        System.out.println(buf.position());


        //判断缓冲区是否还有剩余的
        if(buf.hasRemaining()){
            System.out.println(buf.remaining());
        }


    }


    @Test
    public void test3(){

        //直接缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    }

}
