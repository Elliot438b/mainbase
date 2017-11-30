package javaS.IO;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

public class StringIOS extends IOBaseS {

    @Test
    public void testStringWriter() throws IOException {
        StringWriter sw = new StringWriter();
        sw.write("Hello");
        sw.append("A");
        sw.close();
        logger.info(sw);

        StringReader sr = new StringReader("Hello");
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = sr.read()) != -1) {
            sb.append((char) c);
        }
        logger.info(sb);
        /**
         * Output:
         * 
         * 12:56:47[testStringWriter]: HelloA
         * 
         * 12:56:47[testStringWriter]: Hello
         */
    }
}
