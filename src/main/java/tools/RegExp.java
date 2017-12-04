package tools;

import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Regular expression 正则表达式
 * 
 * @author Evsward
 *
 */
public class RegExp {
    private final static Logger logger = LogManager.getLogger();
    private String[] data = { "a.txt", "car", "12345", "-2" };

    @Test
    public void testRegExp() {
        String regExp = "\\d+";// 挑出正数数字
//        regExp = "-?\\d+";
        for (String s : data) {
            if (Pattern.matches(regExp, s))
                logger.info(s);
        }

    }
}
