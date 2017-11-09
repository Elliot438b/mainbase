package algorithms.search;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import tools.XMLUtil;

public class VIPClient {
    private static final Logger logger = LogManager.getLogger();

    @SuppressWarnings("unchecked")
    @Test
    public void testST() {
        ST<Integer, String> sst;
        Object oSf = XMLUtil.getBean("sf");
        sst = new ST<Integer, String>((SFunction<Integer, String>) oSf);
        logger.info("-----功能测试-----");
        if (sst.isEmpty()) {
            sst.put(3, "fan");
            logger.info("sst.put(3, " + sst.get(3) + ") --- sst.size() = " + sst.size());
        }
        sst.put(77, "eclipse");
        sst.put(23, "idea");
        sst.put(60, "cup");
        sst.put(56, "plane");
        logger.info("sst.put 77,23,60,56 --- sst.size() = " + sst.size());
        if (!sst.containsKey(1)) {
            sst.put(1, "lamp");
            logger.info("sst.put(1, " + sst.get(1) + ") --- sst.size() = " + sst.size());
        }
        sst.put(20, "computer");
        logger.info("sst.put(20, " + sst.get(20) + ") --- sst.size() = " + sst.size());
        sst.delete(20);
        logger.info("sst.delete(20) --- sst.size() still= " + sst.size());
        logger.info("-----①遍历当前集合【观察输出顺序】-----");
        for (int k : sst.keySet()) {
            logger.info(k + "..." + sst.get(k));
        }
        logger.info("-----②测试表头中尾删除-----");
        sst.remove(20);// 【有序表中删除，顺序表头删除】
        logger.info("sst.remove(20)...【有序表中删除，顺序表头删除】");
        logger.info("sst.get(20) = " + sst.get(20) + " --- sst.size() = " + sst.size());
        sst.remove(1);// 【有序表头删除，顺序表中删除】
        logger.info("sst.remove(1)...【有序表头删除，顺序表中删除】");
        logger.info("sst.get(1) = " + sst.get(1) + " --- sst.size() = " + sst.size());
        sst.remove(77);// 【有序表尾删除】，顺序表中删除
        logger.info("sst.remove(77)...【有序表尾删除】，顺序表中删除");
        logger.info("sst.get(77) = " + sst.get(77) + " --- sst.size() = " + sst.size());
        sst.remove(3);// 有序表中删除，【顺序表尾删除】
        logger.info("sst.remove(3)...有序表中删除，【顺序表尾删除】");
        logger.info("sst.get(3) = " + sst.get(3) + " --- sst.size() = " + sst.size());
        logger.info("-----③遍历当前集合-----");
        for (int k : sst.keySet()) {
            logger.info(k + "..." + sst.get(k));
        }

        logger.info("-----性能测试-----");
        long start = System.currentTimeMillis();
        Random rand = new Random();
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 10000; i++) {
            sst.put(rand.nextInt(10000), String.valueOf(abc.charAt(rand.nextInt(abc.length())))
                    + String.valueOf(abc.charAt(rand.nextInt(abc.length()))));
        }
        int a = 0;
        for (int k : sst.keySet()) {
            a++;
            sst.get(k);
        }
        logger.info("0-" + a + "..." + "sst.size() = " + sst.size());
        long end = System.currentTimeMillis();
        logger.info("总耗时：" + (end - start) + "ms");

    }

    @SuppressWarnings("unchecked")
    @Test
    /**
     * 由于有序符号表实现了两个接口，SFunction相关的通过testST可以测试，这里仅测试与SFunctionSorted的方法。
     */
    public void testSST() {
        SST<Integer, String> sst;
        Object oSf = XMLUtil.getBean("ssf");
        sst = new SST<Integer, String>((SFunctionSorted<Integer, String>) oSf);
        sst.put(3, "fan");
        sst.put(77, "eclipse");
        sst.put(23, "idea");
        sst.put(60, "cup");
        sst.put(56, "plane");
        sst.put(1, "lamp");
        sst.put(20, "computer");
        logger.info("-----①遍历当前集合【观察输出顺序】-----");
        for (int k : sst.keySet()) {
            logger.info(k + "..." + sst.get(k));
        }
        logger.info("-----②有序表特有功能测试-----");
        logger.info("sst.ceiling(59) = " + sst.ceiling(59));
        logger.info("sst.floor(59) = " + sst.floor(59));
        logger.info("sst.min() = " + sst.min());
        logger.info("sst.max() = " + sst.max());
        logger.info("sst.select(3) = " + sst.select(3));
    }
}
