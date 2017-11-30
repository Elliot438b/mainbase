package javaS.IO;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.PushbackReader;

import org.junit.Test;

public class PushBackS extends IOBaseS {
    @Test
    public void testPushbackInputStream() throws IOException {
        String content = "Superman VS Batman";
        // 构造参数为一个字节数组
        ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
        // 构造参数为一个InputStream对象。
        PushbackInputStream pbis = new PushbackInputStream(bais);
        pbis.unread("Ssdfasdf".getBytes(), 0, 1);// 将S推到源字符串的最前方
        // pr.unread('S');// 这里的'S'是按照整型值操作
        int n;
        String str = "";
        while ((n = pbis.read()) != -1) {
            str += (char) n;
            // pbis.unread(n);将刚读出来的字符再推回去，就会死循环。
        }
        logger.info(str);
        pbis.close();
        bais.close();
        /**
         * 输出：
         * 
         * 12:32:48[testPushBackInputStream]: SSuperman VS Batman
         */
    }

    @Test
    /**
     * PushbackInputStream的字符流版本
     */
    public void testPushbackReader() throws IOException {
        // 构造参数为Reader对象，使用字符数组读取
        PushbackReader pr = new PushbackReader(new CharArrayReader("go go Gan.".toCharArray()));
        pr.unread("Ssdfasdf".toCharArray(), 0, 1);// 将S推到源字符串的最前方
        // pr.unread('S');// 这里的'S'是按照整型值操作
        int n;
        String str = "";
        while ((n = pr.read()) != -1) {
            str += (char) n;
            // pr.unread(n);将刚读出来的字符再推回去，就会死循环。
        }
        logger.info(str);
        pr.close();
        /**
         * 输出：
         * 
         * 12:45:55[testPushbackReader]: Sgo go Gan.
         */
    }

}
