package javaS.IO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

/**
 * 字节流的学习
 * 
 * 基于字节I/O操作的基类：InputStream和OutputStream
 * 
 * 对应的缓存类：BufferedInputStream和BufferedOutputStream
 * 
 * 输入输出的主语是“内存”，内存输出写入文件，内存输入读取文件
 * 
 * @author Evsward
 *
 */
public class ByteStreamS extends IOBaseS {
    @Test
    /**
     * 使用输出流OutputStream.write，将内存中的内容写入设备文件（这里的设备文件为File：磁盘文件）
     */
    public void testWrite2OutputStream() throws IOException {
        OutputStream fos = new FileOutputStream(root+"/UME.txt");//找不到该文件会自动创建（包括路径）
        /**
         * 内容中的字符串内容content
         */
        String content = "哈哈哈\n嘿嘿";
        fos.write(content.getBytes());// 直接写入字节
        fos.close();// 操作完注意将流关闭

        /**
         * 文件后面追加内容，构造函数加第二个参数true
         */
        OutputStream fosadd = new FileOutputStream(root+"/UME.txt", true);
        fosadd.write(" 你好".getBytes());
        fosadd.close();
    }

    /**
     * 一次性写入，降低占用IO的频率
     */
    @Test
    public void testWrite2BufferedOutputStream() throws IOException {
        OutputStream fosaddOnce = new FileOutputStream(root+"/UME.txt");
        OutputStream bs = new BufferedOutputStream(fosaddOnce);
        bs.write("举杯邀明月".getBytes());
        bs.flush();// 每次flush会将内存中数据一齐刷入到外部文件中，但不会close该流。
        bs.write("床前明月光".getBytes());
        /**
         * close方法除了有关闭流的作用，在其关闭流之前也会执行一次flush。
         * 注意一定要先关闭BufferedOutputStream，再关闭FileOutputStream，从外到内打开，要从内到外关闭。
         */
        bs.close();
        fosaddOnce.close();// 两个流都要关闭
    }

    @Test
    /**
     * DataOutputStream，可以直接写入java基本类型数据（没有String），但写入以后是一个二进制文件的形式，不可以直接查看。
     * 
     * 文本文件是二进制文件的特殊形式，这是通过转储实现的，相关内容请转到 http://www.cnblogs.com/Evsward/p/huffman.html#二进制转储
     */
    public void testWrite2DataOutputStream() throws IOException {
        OutputStream fosaddOnce = new FileOutputStream(root+"/UME.txt");
        OutputStream bs = new BufferedOutputStream(fosaddOnce);
        DataOutputStream dos = new DataOutputStream(bs);
        dos.writeInt(22);
        dos.writeShort(1222222222);
        dos.writeLong(20L);
        dos.writeByte(3);
        dos.writeChar(42);
        dos.close();
        bs.close();
        fosaddOnce.close();
        /**
         * 终版：上面的close阶段要从内向外关闭三次，比较麻烦，下面直接采用只关闭一次的方法
         */
        DataOutputStream dosA = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(root+"/UME.txt")));
        dosA.writeInt(22);
        dosA.writeShort(65538);// DataOutputStream并不会检查数据是否越界，越界的数据按照二进制方式截取，只保留界限以内的数据。
        dosA.writeLong(20L);
        dosA.writeByte(3);
        dosA.writeChar(42);
        dosA.writeDouble(3.1415926);
        dosA.close();// 只关闭一次。
    }

    @Test
    /**
     * 通过DataInputStream读取二进制文件，一定要按照写入的顺序去读取java基本类型的文件内容，否则会出现乱码或者不准确的信息
     */
    public void testRead2DataInputStream() throws IOException {
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(root+"/UME.txt")));
        logger.info(dis.readInt());
        /**
         * 即使存入越界的树65538，也不会报错，因为超出部分不会被存入，存入的只是超出的部分。
         * short类型占据16位的空间，因此将65538转为二进制数，超出16位的部分自动截掉，只保留16为以内的数据，所以就变成了2。
         */
        logger.info(dis.readShort());
        logger.info(dis.readLong());
        logger.info(dis.readByte());
        logger.info(dis.readChar());
        logger.info(dis.readDouble());
        dis.close();
        /**
         * 输出：
         * 13:39:03[testDataInputStream]: 22 
         * 13:39:03[testDataInputStream]: 2 
         * 13:39:03[testDataInputStream]: 20 
         * 13:39:03[testDataInputStream]: 3 
         * 13:39:03[testDataInputStream]: * 
         * 13:39:03[testDataInputStream]: 3.1415926 
         */
    }

    @Test
    /**
     * 使用输入流读取InputStream.read，将设备文件（这里的File是磁盘文件）读到内存buffer中去。
     */
    public void testRead2InputStream() throws IOException {
        int bufferSize = 200;
        FileInputStream fis = new FileInputStream(root+"/UME.txt");
        byte buffer[] = new byte[bufferSize];
        int length;
        while ((length = fis.read(buffer, 0, bufferSize)) > -1) {
            String str = new String(buffer, 0, length);
            logger.info(str);
        }
        fis.close();// 操作完注意将流关闭
        /**
         * 输出：
         * 13:41:02[testInputStreamS]: 举杯邀明月床前明月光
         */
    }
}
