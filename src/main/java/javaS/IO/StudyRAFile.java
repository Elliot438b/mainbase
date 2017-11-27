package javaS.IO;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * RandomAccessFile：有一个指针seek，对文件任意位置操作的类
 * 
 * 完整JUnit执行顺序：/@BeforeClass –> /@Before –> /@Test –> /@After –> /@AfterClass
 * 
 * /@BeforeClass和/@AfterClass只执行一次，且必须为static void
 * 
 * 定义完整测试流程：先初始化一个空白文件，然后添加两行数据Jhon和Jack，然后在他俩中间插入Hudson，最后读出该文件数据，验证结果
 * 
 * @author Evsward
 *
 */
public class StudyRAFile extends StudyIOBase {
    @BeforeClass
    public static void initEV() throws IOException {
        File f = new File(root + "/access");
        if (f.exists())
            f.delete();
        f.createNewFile();
    }

    @Before
    public void testWrite2RAFile() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(root + "/access", "rw");// rw是采用读写的方式打开文件
        logger.info(raf.length());
        Student Jhon = new Student(1001, "Jhon", 26, 1.85d);
        Student Jack = new Student(1002, "Jack", 25, 1.75d);
        Jhon.write(raf);// 写入文件以后，指针到当前文本结尾
        // 当前seek是从seek(raf.length)开始的
        logger.info(raf.length());
        Jack.write(raf);// 继续写入，指针继续移动到末尾，相当于追加
        logger.info(raf.length());
        raf.close();
    }

    @Ignore
    @Test
    /**
     * 追加内容
     */
    public void testWriteAppend2RAFile() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(root + "/access", "rw");// rw是采用读写的方式打开文件
        Student Mason = new Student(1003, "Mason", 26, 1.82d);// 这里的“Mason”比上面的两条数据多一位字符
        // 追加内容要先调整seek的位置到raf.length，然后开始追加内容
        raf.seek(raf.length());
        Mason.write(raf);
        logger.info(raf.length());
        raf.close();
    }

    @After
    public void testReadRAFile() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(root + "/access", "r");
        // 获取raf时，seek就是在文件开始位置
        logger.info(raf.length());
        Student Lily = new Student();
        Lily.read(raf);
        logger.info(Lily);
        Lily.read(raf);
        logger.info(Lily);
        Lily.read(raf);
        logger.info(Lily);
        Lily.read(raf);
        logger.info(Lily);
        Lily.read(raf);
        logger.info(Lily);
        Lily.read(raf);
        logger.info(Lily);
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
            Hudson.insert(root + "/access", 26, Hudson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出： 17:10:31[testWrite2RAFile]: 0 17:10:31[testWrite2RAFile]: 26
     * 17:10:31[testWrite2RAFile]: 52 17:10:31[testReadRAFile]: 94
     * 17:10:31[testReadRAFile]: id:1001 name:Jhon age:26 height:1.85
     * 17:10:31[testReadRAFile]: id:1005 name:Hudson age:45 height:1.76
     * 17:10:31[testReadRAFile]: id:1002 name:Jack age:25 height:1.75
     */
}
