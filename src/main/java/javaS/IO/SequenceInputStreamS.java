package javaS.IO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import org.junit.Test;

public class SequenceInputStreamS extends IOBaseS {

    /**
     * 合并两个读入的字节流
     * 
     * @throws IOException
     */
    @Test
    public void testSequenceInputStream() throws IOException {
        // buffer的空间要设定为2的次方才能有效分割，否则会出现某汉字被中途分割显示不完整的情况，
        int bufferSize = 16;
        InputStream is1 = new FileInputStream(root + "UME.txt");
        InputStream is2 = new FileInputStream(root + "HongXing.txt");
        SequenceInputStream sis = new SequenceInputStream(is1, is2);// 构造参数必须为InputStream
        byte[] buffer = new byte[bufferSize];
        while (sis.read(buffer, 0, bufferSize) != -1) {
            // 开始读合并后的数据流，这里可以针对这些数据流做任何操作(读写到任何文件或者打印到控制台)
            String str = new String(buffer, 0, bufferSize);
            logger.info(str);// 打印到控制台
        }
        is1.close();
        is2.close();
        sis.close();
    }

    @Test
    public void testMergeEnumInputStream() throws IOException {
        // 实际上它可以合并不同类型数据，然而如果是对象流的话，读取时涉及反序列化工作，要找准与其他数据的分割点，比较麻烦。
        InputStream is1 = new FileInputStream(root + "UME.txt");
        InputStream is2 = new FileInputStream(root + "HongXing.txt");
        InputStream is3 = new FileInputStream(root + "HuaYi.txt");
        ArrayList<InputStream> list = new ArrayList<InputStream>();
        list.add(is1);
        list.add(is2);
        list.add(is3);
        Iterator<InputStream> it = list.iterator();
        SequenceInputStream sis = new SequenceInputStream(new Enumeration<InputStream>() {
            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override
            public InputStream nextElement() {
                return it.next();
            }
        });
        int bufferSize = 32;
        byte[] buffer = new byte[bufferSize];
        while (sis.read(buffer, 0, bufferSize) != -1) {
            // 开始读合并后的数据流，这里可以针对这些数据流做任何操作(读写到任何文件或者打印到控制台)
            String str = new String(buffer, 0, bufferSize);
            logger.info(str);// 打印到控制台
        }
        is1.close();
        is2.close();
        is3.close();
        sis.close();
    }
}
