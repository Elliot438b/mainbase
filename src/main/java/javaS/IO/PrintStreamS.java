package javaS.IO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.Test;

public class PrintStreamS extends IOBaseS {

    /**
     * 打印流的使用非常类似于FileWriter，但是它支持更多的方法，同时也有着丰富的构造方法。
     */
    @Test
    public void testPrintStream() throws IOException {
        FileS.initEV(root + "HongXing.txt");
        // PrintStream p = new PrintStream(root + "HongXing.txt");
        // PrintStream p = new PrintStream(new File(root + "HongXing.txt"));
        // PrintStream p = new PrintStream(new FileOutputStream(root +
        // "HongXing.txt"), true, "UTF-8");
        PrintStream p = System.out;// 数据源切换到控制台，标准输出，相当于System.out.xxx();
        p.append("海上升明月");
        p.println("润物细无声");
        p.print("当春乃发生");
        p.write("无敌心头好".getBytes());
        p.flush();// 刷入内存数据到数据源
        System.out.write("asdfas".getBytes());
        p.close();

        /**
         * 输出：
         * 
         * 海上升明月润物细无声
         * 
         * 当春乃发生无敌心头好
         */
    }

    /**
     * PrintWriter与PrintStream的两点区别：
     * 
     * write方法一个是写入字节，一个是写入字符。
     * 
     * PrintWriter无法作为标准输出到控制台。
     * 
     * 一般来讲，使用PrintStream多一些。
     */
    @Test // 如果忘记写该注解，执行JUnit会报错initializationError
    public void testPrintWriter() throws IOException {
        FileS.initEV(root + "HongXing.txt");
        // PrintWriter p = new PrintWriter(root + "HongXing.txt");
        // PrintWriter p = new PrintWriter(new File(root + "HongXing.txt"));
        // 第二个参数为autoflush，如果为true的话，println、printf和format会自动执行flush。
        PrintWriter p = new PrintWriter(new FileOutputStream(root + "HongXing.txt"), true);
        p.append("海上升明月");
        p.println("润物细无声");
        p.print("当春乃发生");
        p.write("无敌心头好");// 这是与PrintStream唯一区别了
        p.flush();// PrintWriter也支持刷入操作
        p.close();
    }
}
