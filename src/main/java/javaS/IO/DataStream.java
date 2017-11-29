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

public class DataStream extends IOBaseS {

    @Test
    /**
     * DataOutputStream，可以直接写入java基本类型数据（没有String），但写入以后是一个二进制文件的形式，不可以直接查看。
     * 
     * 文本文件是二进制文件的特殊形式，这是通过转储实现的，相关内容请转到
     * http://www.cnblogs.com/Evsward/p/huffman.html#二进制转储
     */
    public void testWrite2DataOutputStream() throws IOException {
        OutputStream fosaddOnce = new FileOutputStream(root + "/UME.txt");
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
         * 终版：上面的close阶段要从内向外关闭三次，比较麻烦，下面直接采用装饰模式标准写法，套接对象。
         * 套接对象：最里面的一定是节点流，它之外的无论几层都是处理流
         * FileOutputStream:属于节点流，其他节点流还包括管道和数组，剩下的都是处理流
         * BufferedOutputStream:缓冲技术（也属于处理流） DataOutputStream:处理流
         */
        DataOutputStream dosA = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(root + "/UME.txt")));
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
     * 通过DataInputStream处理流读取二进制文件，一定要按照写入的顺序去读取java基本类型的文件内容，否则会出现乱码或者不准确的信息
     */
    public void testRead2DataInputStream() throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(root + "/UME.txt")));
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
         * 输出： 13:39:03[testDataInputStream]: 22 13:39:03[testDataInputStream]:
         * 2 13:39:03[testDataInputStream]: 20 13:39:03[testDataInputStream]: 3
         * 13:39:03[testDataInputStream]: * 13:39:03[testDataInputStream]:
         * 3.1415926
         */
    }

}
