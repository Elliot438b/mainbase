package pattern.strategy;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algorithms.search.ST;

public abstract class Strategy {
    protected static final Logger logger = LogManager.getLogger();

    public abstract void algorithm();// 核心抽象方法

    protected void testST(ST<Integer, String> sst) {
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
