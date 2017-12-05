package tools;

import java.util.Arrays;
import java.util.regex.Matcher;
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

    @Test
    /**
     * 简单的正则表达式的热身测试
     */
    public void testRegExpWarmUp() {
        String[] data = { "a.txt", "+", "12345", "8", "-2", "-2123", "+010" };
        // 正则表达式中的正整数匹配为\d，在java中要多加一个\用来区分特殊字符。
        String regExp = "\\d";// 一位数的正整数字符【只匹配一个】"8"
        regExp = "\\d+";// 正数数字【加号+代表一个或者多个】"12345", "8"
        regExp = "-?\\d+";// 数字(包括正数和负数)"12345", "8", "-2", "-2123"
        regExp = "-\\d+";// 所有负数【只匹配以一个字符‘-’开头的】"-2", "-2123"
        regExp = "-\\d";// 只有一位数的负数【只匹配以一个字符‘-’开头的，同时只有一位整数的字符串】"-2"
        // 所以一个字符a从无到有的过程为：a?（一个或者没有） -> a（只有一个） -> a+（一个或者多个）
        // 如果要取正则表达式的关键字字符，用\\声明
        regExp = "\\+";// 内容为加号的字符串 "+"
        regExp = "\\+\\d+";// 以加号开头接整数的字符串 "+010"
        // 用括号分组，整体相当于一个单独的字符
        regExp = "(\\+|-)\\d+";// 以一个加号或者一个负号开头的数字 "-2", "-2123", "+010"
        regExp = "(\\+|-)?\\d+";// 以一个加号或者一个负号开头或者没有符号的数字 "12345", "8"，"-2",
                                // "-2123", "+010"
        for (String s : data) {
            if (Pattern.matches(regExp, s))
                logger.info(s);
        }
    }

    @Test
    /**
     * 测试String方法中的正则表达式
     */
    public void testStringFRegex() {
        String regex = " ";// 按空格来划分字符串
        regex = "\\W+";// 正则选择出非单词字符，split过滤一遍以后剩下纯单词，删除其他符号
        regex = "g\\W+";// 字母n后面跟着非单词字符，也就是‘n后面有空格字符’,split以后就去掉了n和这个空格
        String preface = "I wish I had this book when I started programming... I recommend this book to every student as well as beginner and intermediate Java programmer.";
        String[] a = preface.split(regex);
        logger.info(a.length + " " + Arrays.toString(a));

        logger.info(preface.replaceFirst("p\\w+", "heyhey"));// 将第一个p打头的单词改为heyhey
        logger.info(preface.replaceAll("p\\w+", "heyhey"));// 将全部p打头的单词改为heyhey
    }

    @Test
    public void testRegex() {
        Pattern p = Pattern.compile("[a-z]{3}");
        Matcher m = p.matcher("asd");
        logger.info(m.matches() + " " + m.end() + " " + m.start() + " " + m.group());
    }
}
