package javaS.IO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

/**
 * 研究对象序列化（跨平台，跨网络的基础）
 * 
 * 内存 -> 磁盘/网络
 * 
 * Java对象 -> 二进制文件
 * 
 * @author Evsward
 *
 */
public class SerialS extends IOBaseS {
    /**
     * 测试序列化对象存储结构
     */
    @Test
    public void testWriteSerialObject() throws IOException {
        FileS.initEV(root + "/access");// 先将access文件清空。
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(root + "/access"));
        Student Lu = new Student(2001, "Luxa", 31, 1.81d);
        // 可以写入不同的序列化对象数据，但要记录写入顺序
        oos.writeObject(Lu);
        oos.close();
        /**
         * access内容：由于写入的是一个二进制文件，所以打开是乱码
         * 
         * ¬í^@^Esr^@^PjavaS.IO.Student Ç.2<95>×³^?^B^@^DI^@^CageD^@^FheightJ^@^BidL^@^Dnamet^@^RLjava/lang/String;xp^@^@^@^_?üõÂ<8f>\(ö^@^@^@^@^@^@^GÑt^@^DLuxa
         */
    }

    /**
     * 读取二进制文件（序列号文件）
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void testReadSerialObject() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(root + "/access"));
        // 可以读取不同的对象的数据，但是要按照写入顺序去读取。
        Student s = (Student) ois.readObject();
        logger.info(s);
        ois.close();
        /**
         * ①输出：
         * 
         * 10:24:08[testReadSerialObject]: id:2001 name:Luxa age:31 height:1.81
         * 
         * ②若height属性变量被声明为transient，则该变量在序列化过程中不会被写入，为初始值。输出为：
         * 
         * 10:29:34[testReadSerialObject]: id:2001 name:Luxa age:31 height:0.0
         */
    }

}
