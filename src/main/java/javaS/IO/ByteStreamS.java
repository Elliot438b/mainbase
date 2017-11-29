package javaS.IO;

import java.io.BufferedOutputStream;
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
 * 出入的主语是“内存”，出内存就是写入文件，入内存就是读取文件
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
     * 缓冲区处理流：BufferedInputStream，BufferedOutputStream，BufferedReader,BufferedWriter,
     * 一次性写入，降低占用IO的频率
     * 避免每次和硬盘打交道，提高数据访问的效率。
     */
    @Test
    public void testWrite2BufferedOutputStream() throws IOException {
        // OutputStream为基类
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
