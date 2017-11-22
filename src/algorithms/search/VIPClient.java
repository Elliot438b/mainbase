package algorithms.search;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import tools.XMLUtil;

public class VIPClient {
    private static final Logger logger = LogManager.getLogger();

    @Test
    public void testSTBatch() {
        logger.info("------开始批量测试------");
        testST("sf1");
        testST("sf2");
        testST("sf3");
        testST("sf4");
        testST("sf5");
        testST("sf6");
        testST("map");
        logger.info("------批量测试成功！------");
    }

    @Test
    public void testSSTBatch() {
        logger.info("------开始批量测试------");
        testSST("ssf1");
        testSST("ssf2");
        testSST("ssf3");
        logger.info("------批量测试成功！------");
    }

    @SuppressWarnings("unchecked")
    public void testST(String tagName) {
        if (tagName.equals("map")) {
            Map<Integer, String> sst = new HashMap<Integer, String>();
            logger.info("class: " + sst.getClass().getName());
            logger.debug("-----功能测试-----");
            if (sst.isEmpty()) {
                sst.put(3, "fan");
                logger.debug("sst.put(3, " + sst.get(3) + ") --- sst.size() = " + sst.size());
            }
            assertTrue(sst.size() == 1);
            sst.put(77, "eclipse");
            sst.put(32, "idea");
            sst.put(65, "cup");
            sst.put(256, "plane");
            logger.debug("sst.put 77,32,65,256 --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 5);
            if (!sst.containsKey(1)) {
                sst.put(1, "lamp");
                logger.debug("sst.put(1, " + sst.get(1) + ") --- sst.size() = " + sst.size());
            }
            assertTrue(sst.size() == 6);
            sst.put(20, "computer");
            logger.debug("sst.put(20, " + sst.get(20) + ") --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 7);
            sst.put(20, "book");
            assertTrue(sst.size() == 7);
            logger.debug("-----①遍历当前集合【观察输出顺序】-----");
            for (int k : sst.keySet()) {
                logger.debug(k + "..." + sst.get(k));
            }
            logger.debug("-----②测试表头中尾删除-----");
            sst.remove(20);// 【有序表中删除，顺序表头删除】
            logger.debug("sst.remove(20)...【有序表中删除，顺序表头删除】");
            logger.debug("sst.get(20) = " + sst.get(20) + " --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 6);
            sst.remove(1);// 【有序表头删除，顺序表中删除】
            logger.debug("sst.remove(1)...【有序表头删除，顺序表中删除】");
            logger.debug("sst.get(1) = " + sst.get(1) + " --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 5);
            sst.remove(77);// 【有序表尾删除】，顺序表中删除
            logger.debug("sst.remove(77)...【有序表尾删除】，顺序表中删除");
            logger.debug("sst.get(77) = " + sst.get(77) + " --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 4);
            sst.remove(3);// 有序表中删除，【顺序表尾删除】
            logger.debug("sst.remove(3)...有序表中删除，【顺序表尾删除】");
            logger.debug("sst.get(3) = " + sst.get(3) + " --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 3);
            logger.debug("-----③遍历当前集合-----");
            for (int k : sst.keySet()) {
                logger.debug(k + "..." + sst.get(k));
            }

            logger.debug("-----性能测试-----");
            long start = System.currentTimeMillis();
            Random rand = new Random();
            String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            for (int i = 0; i < 10000; i++) {
                sst.put(rand.nextInt(10000), String.valueOf(abc.charAt(rand.nextInt(abc.length())))
                        + String.valueOf(abc.charAt(rand.nextInt(abc.length()))));
            }
            int a = 0;
            // 多次查询，测试算法的查询效率
            for (int k : sst.keySet()) {
                a++;
                sst.get(k);
            }
            for (int k : sst.keySet()) {
                sst.get(k);
            }
            for (int k : sst.keySet()) {
                sst.get(k);
            }
            for (int k : sst.keySet()) {
                sst.get(k);
            }
            logger.debug("0-" + a + "..." + "sst.size() = " + sst.size());
            long end = System.currentTimeMillis();
            logger.info("总耗时：" + (end - start) + "ms");
            logger.debug("测试成功！");
        } else {
            ST<Integer, String> sst;
            Object oSf = XMLUtil.getBean(tagName);
            sst = new ST<Integer, String>((SFunction<Integer, String>) oSf);
            logger.debug("-----功能测试-----");
            if (sst.isEmpty()) {
                sst.put(3, "fan");
                logger.debug("sst.put(3, " + sst.get(3) + ") --- sst.size() = " + sst.size());
            }
            assertTrue(sst.size() == 1);
            sst.put(77, "eclipse");
            sst.put(32, "idea");
            sst.put(65, "cup");
            sst.put(256, "plane");
            logger.debug("sst.put 77,32,65,256 --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 5);
            if (!sst.containsKey(1)) {
                sst.put(1, "lamp");
                logger.debug("sst.put(1, " + sst.get(1) + ") --- sst.size() = " + sst.size());
            }
            assertTrue(sst.size() == 6);
            sst.put(20, "computer");
            logger.debug("sst.put(20, " + sst.get(20) + ") --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 7);
            sst.delete(20);
            logger.debug("sst.delete(20) --- sst.size() still= " + sst.size());
            sst.put(20, "book");
            assertTrue(sst.size() == 7);
            logger.debug("-----①遍历当前集合【观察输出顺序】-----");
            for (int k : sst.keySet()) {
                logger.debug(k + "..." + sst.get(k));
            }
            logger.debug("-----②测试表头中尾删除-----");
            sst.remove(20);// 【有序表中删除，顺序表头删除】
            logger.debug("sst.remove(20)...【有序表中删除，顺序表头删除】");
            logger.debug("sst.get(20) = " + sst.get(20) + " --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 6);
            sst.remove(1);// 【有序表头删除，顺序表中删除】
            logger.debug("sst.remove(1)...【有序表头删除，顺序表中删除】");
            logger.debug("sst.get(1) = " + sst.get(1) + " --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 5);
            sst.remove(77);// 【有序表尾删除】，顺序表中删除
            logger.debug("sst.remove(77)...【有序表尾删除】，顺序表中删除");
            logger.debug("sst.get(77) = " + sst.get(77) + " --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 4);
            sst.put(3, "fasssn");
            sst.put(3, "fassxxsn");
            sst.remove(3);// 有序表中删除，【顺序表尾删除】
            logger.debug("sst.remove(3)...有序表中删除，【顺序表尾删除】");
            logger.debug("sst.get(3) = " + sst.get(3) + " --- sst.size() = " + sst.size());
            assertTrue(sst.size() == 3);
            logger.debug("-----③遍历当前集合-----");
            for (int k : sst.keySet()) {
                logger.debug(k + "..." + sst.get(k));
            }

            logger.debug("-----性能测试-----");
            long start = System.currentTimeMillis();
            Random rand = new Random();
            String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            for (int i = 0; i < 10000; i++) {
                sst.put(rand.nextInt(10000), String.valueOf(abc.charAt(rand.nextInt(abc.length())))
                        + String.valueOf(abc.charAt(rand.nextInt(abc.length()))));
            }
            int a = 0;
            // 多次查询，测试算法的查询效率
            for (int k : sst.keySet()) {
                a++;
                sst.get(k);
            }
            for (int c : sst.keySet()) {
                sst.get(c);
            }
            for (int b : sst.keySet()) {
                sst.get(b);
            }
            for (int v : sst.keySet()) {
                sst.get(v);
            }
            for (int c : sst.keySet()) {
                sst.get(c);
            }
            for (int b : sst.keySet()) {
                sst.get(b);
            }
            for (int v : sst.keySet()) {
                sst.get(v);
            }
            for (int c : sst.keySet()) {
                sst.get(c);
            }
            for (int b : sst.keySet()) {
                sst.get(b);
            }
            for (int v : sst.keySet()) {
                sst.get(v);
            }
            logger.debug("0-" + a + "..." + "sst.size() = " + sst.size());
            long end = System.currentTimeMillis();
            logger.info("总耗时：" + (end - start) + "ms");
            logger.debug("测试成功！");

        }

    }

    @SuppressWarnings("unchecked")
    /**
     * 由于有序符号表实现了两个接口，SFunction相关的通过testST可以测试，这里仅测试SFunctionSorted的方法。
     */
    public void testSST(String tagName) {
        SST<Integer, String> sst;
        Object oSf = XMLUtil.getBean(tagName);
        sst = new SST<Integer, String>((SFunctionSorted<Integer, String>) oSf);
        sst.put(3, "fan");
        sst.put(77, "eclipse");
        sst.put(32, "idea");
        sst.put(65, "cup");
        sst.put(256, "plane");
        sst.put(1, "lamp");
        sst.put(20, "computer");
        logger.debug("-----①遍历当前集合【观察输出顺序】-----");
        for (int k : sst.keySet()) {
            logger.debug(k + "..." + sst.get(k));
        }
        logger.debug("-----②有序表特有功能测试-----");
        logger.debug("sst.ceiling(59) = " + sst.ceiling(59));
        assertTrue(sst.ceiling(59) == 65);
        logger.debug("sst.floor(59) = " + sst.floor(59));
        assertTrue(sst.floor(59) == 32);
        logger.debug("sst.min() = " + sst.min());
        assertTrue(sst.min() == 1);
        logger.debug("sst.max() = " + sst.max());
        assertTrue(sst.max() == 256);
        logger.debug("sst.select(1)...【有序查询排名第一的key】 = " + sst.select(1));
        assertTrue(sst.select(1) == 1);
        logger.debug("sst.select(3)...【有序查询排名中间的key】 = " + sst.select(3));
        assertTrue(sst.select(3) == 20);
        logger.debug("sst.select(7)...【有序查询排名最后的key】 = " + sst.select(7));
        assertTrue(sst.select(7) == 256);
        logger.debug("测试成功！");
    }
}
