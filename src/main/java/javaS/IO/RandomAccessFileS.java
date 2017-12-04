package javaS.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * RandomAccessFile：有一个指针seek，对文件任意位置操作的类
 * 
 * 完整JUnit执行顺序：/@BeforeClass –> /@Before –> /@Test –> /@After –> /@AfterClass
 * 
 * /@BeforeClass和/@AfterClass只执行一次，且必须为static void
 * 
 * 定义完整测试流程：先初始化一个空白文件，然后添加两行数据Jhon和Jack，然后在他俩中间插入Hudson，最后读出该文件数据，加入断言，验证结果
 * 
 * RandomAccessFile的大多数功能有nio存储映射文件所取代。
 * 
 * @author Evsward
 *
 */
public class RandomAccessFileS extends IOBaseS {
    @Before
    public void testWrite2RAFile() throws IOException {
        FileS.initEV(root + "access");// 首先清空access文件。
        RandomAccessFile raf = new RandomAccessFile(root + "access", "rw");// rw是采用读写的方式打开文件
        logger.info(raf.getFilePointer());
        Student Jhon = new Student(1001, "Jhon", 26, 1.85d);
        Student Jack = new Student(1002, "Jack", 25, 1.75d);
        Jhon.write(raf);// 写入文件以后，指针到当前文本结尾
        // 当前seek是从seek(raf.length)开始的
        logger.info(raf.getFilePointer());
        Jack.write(raf);// 继续写入，指针继续移动到末尾，相当于追加
        assertTrue(raf.length() == raf.getFilePointer());
        raf.close();
    }

    @Ignore
    @Test
    /**
     * 追加内容
     */
    public void testWriteAppend2RAFile() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(root + "access", "rw");// rw是采用读写的方式打开文件
        Student Mason = new Student(1003, "Mason", 26, 1.82d);// 这里的“Mason”比上面的两条数据多一位字符
        // 追加内容要先调整seek的位置到raf.length，然后开始追加内容
        raf.seek(raf.length());
        Mason.write(raf);
        logger.info(raf.length());
        raf.close();
    }

    @After
    public void testReadRAFile() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(root + "access", "r");
        // 获取raf时，seek就是在文件开始位置
        logger.info(raf.length());
        Student Lily = new Student();
        Lily.read(raf);
        logger.info(Lily);
        Lily.read(raf);
        logger.info(Lily);
        // 读入次数是有限的，一定要预先知道最多读几次，否则会报EOFException。
        Lily.read(raf);
        logger.info(Lily);
        raf.close();
        /**
         * 输出： 16:14:30[testReadRAFile]: id:1001 name:Jhon age:26 height:1.85
         */
    }

    @Test
    public void insert() {
        Student Hudson = new Student(1005, "Hudson", 45, 1.76d);
        try {
            insert(root + "access", 26, Hudson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在RandomAccessFile指定位置插入数据，先将位置后面的数据放入缓冲区，插入数据以后再将其写回来。
     * 
     * @param file
     *            能找到该文件的路径，是字符串类型
     * @param position
     *            其实外部调用的时候能找到这个位置比较难，因为不确定数据长度是多少，弄不好就会将数据拆分引起混乱。
     * @param content
     */
    public void insert(String file, long position, Student s) throws IOException {
        /**
         * 创建一个临时文件
         * 
         * 在使用完以后就将其删除
         */
        File tempFile = File.createTempFile("temp", null);
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream("temp");
        /**
         * 将插入位置后面的数据缓存到临时文件
         */
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        logger.info("raf.length() = " + raf.length());// 文件大小
        assertTrue(raf.getFilePointer() == 0);// 从0开始
        raf.seek(position);
        byte[] buffer = new byte[20];
        while (raf.read(buffer) > -1) {
            fos.write(buffer);
        }
        assertTrue(raf.length() == raf.getFilePointer());// 此时应该是执行到文件结尾，所以指针应该与文件大小相等
        raf.seek(position);
        assertTrue(position == raf.getFilePointer());// 重新指定位置以后，此时指针应该与position相等
        /**
         * 向RandomAccessFile写入插入内容
         */
        s.write(raf);
        /**
         * 从临时文件中写回缓存数据到RandomAccessFile
         */
        FileInputStream fis = new FileInputStream("temp");
        while (fis.read(buffer) > -1) {
            raf.write(buffer);
        }
        fos.close();
        fis.close();
        raf.close();
        tempFile.delete();// 删除临时文件tempFile
    }

    /**
     * 输出： 17:10:31[testWrite2RAFile]: 0 17:10:31[testWrite2RAFile]: 26
     * 17:10:31[testWrite2RAFile]: 52 17:10:31[testReadRAFile]: 94
     * 17:10:31[testReadRAFile]: id:1001 name:Jhon age:26 height:1.85
     * 17:10:31[testReadRAFile]: id:1005 name:Hudson age:45 height:1.76
     * 17:10:31[testReadRAFile]: id:1002 name:Jack age:25 height:1.75
     */
}
