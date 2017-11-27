package javaS.IO;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

/**
 * RandomAccessFile：有一个指针seek，对文件任意位置操作的类
 * 
 * @author Evsward
 *
 */
public class StudyRAFile extends StudyIOBase {
    @Test
    public void testWrite2RAFile() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(root + "/access", "rw");// rw是采用读写的方式打开文件
        Student Jhon = new Student(1001, "Jhon", 26, 1.85d);
        Student Jack = new Student(1002, "Jack", 25, 1.75d);
        Jhon.write(raf);// 写入文件以后，指针到当前文本结尾
        // 当前seek是从seek(raf.length)开始的
        Jack.write(raf);// 继续写入，指针继续移动到末尾，相当于追加
        raf.close();
    }

    @Test
    public void testReadRAFile() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(root + "/access", "r");
        Student Lily = new Student();
        raf.seek(0);// 调整指针从第一行开始读，所以读的前提是你必须知道数据的位置，而这个位置与数据的内容并无任何键关联。
        Lily.read(raf);
        logger.info(Lily);
        Lily.read(raf);
        logger.info(Lily);
        raf.close();
        /**
         * 输出： 16:14:30[testReadRAFile]: id:1001 name:Jhon age:26 height:1.85
         */
    }
    
    
}
