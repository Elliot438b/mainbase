package javaS.IO;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.junit.Test;

public class LineNumberReaderS extends IOBaseS {

    @Test
    public void testLineNumberReader() throws IOException {
        FileReader fr = new FileReader(root + "UME.txt");
        // 构造参数为Reader
        LineNumberReader lnr = new LineNumberReader(fr);
        lnr.setLineNumber(1);// 设置行号从2开始。
        String str;
        while ((str = lnr.readLine()) != null) {
            // 核心方法：lnr.getLineNumber()，获得行号
            logger.info("行号：" + lnr.getLineNumber() + " 内容：" + str);
        }
        fr.close();
        lnr.close();
        /**
         * 输出：
         * 
         * 12:11:27[testLineNumberReader]: 行号：2 内容：举杯邀明月
         * 
         * 12:11:27[testLineNumberReader]: 行号：3 内容：床前明月光
         */
    }
}
