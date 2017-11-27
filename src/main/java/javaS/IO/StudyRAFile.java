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
        RandomAccessFile raf = new RandomAccessFile(root + "/access", "rw");
    }
}
