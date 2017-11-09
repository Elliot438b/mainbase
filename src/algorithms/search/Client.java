package algorithms.search;

import java.util.Random;

import tools.XMLUtil;

/**
 * 已更换最新架构，请转到VIPClient;
 * 
 * @author Evsward
 * @param <Key>
 * @param <Value>
 */
public class Client {
    @SuppressWarnings("unchecked")
    public void testFun() {
        ST<Integer, String> sst;
        Object oSf = XMLUtil.getBean("sf");
        sst = new ST<Integer, String>((SFunction<Integer, String>) oSf);
        System.out.println("-----功能测试-----");
        if (sst.isEmpty()) {
            sst.put(3, "fan");
            System.out.println("sst.put(3, " + sst.get(3) + ") --- sst.size() = " + sst.size());
        }
        sst.put(77, "eclipse");
        sst.put(23, "idea");
        sst.put(60, "cup");
        sst.put(56, "plane");
        System.out.println("sst.put 77,23,60,56 --- sst.size() = " + sst.size());
        if (!sst.containsKey(1)) {
            sst.put(1, "lamp");
            System.out.println("sst.put(1, " + sst.get(1) + ") --- sst.size() = " + sst.size());
        }
        sst.put(20, "computer");
        System.out.println("sst.put(20, " + sst.get(20) + ") --- sst.size() = " + sst.size());
        sst.delete(20);
        System.out.println("sst.delete(20) --- sst.size() still= " + sst.size());
        System.out.println("-----①遍历当前集合【观察输出顺序】-----");
        for (int k : sst.keySet()) {
            System.out.println(k + "..." + sst.get(k));
        }
        System.out.println("-----②测试表头中尾删除-----");
        sst.remove(20);// 【有序表中删除，顺序表头删除】
        System.out.println("sst.remove(20)...【有序表中删除，顺序表头删除】");
        System.out.println("sst.get(20) = " + sst.get(20) + " --- sst.size() = " + sst.size());
        sst.remove(1);// 【有序表头删除，顺序表中删除】
        System.out.println("sst.remove(1)...【有序表头删除，顺序表中删除】");
        System.out.println("sst.get(1) = " + sst.get(1) + " --- sst.size() = " + sst.size());
        sst.remove(77);// 【有序表尾删除】，顺序表中删除
        System.out.println("sst.remove(77)...【有序表尾删除】，顺序表中删除");
        System.out.println("sst.get(77) = " + sst.get(77) + " --- sst.size() = " + sst.size());
        sst.remove(3);// 有序表中删除，【顺序表尾删除】
        System.out.println("sst.remove(3)...有序表中删除，【顺序表尾删除】");
        System.out.println("sst.get(3) = " + sst.get(3) + " --- sst.size() = " + sst.size());
        System.out.println("-----③遍历当前集合-----");
        for (int k : sst.keySet()) {
            System.out.println(k + "..." + sst.get(k));
        }

        System.out.println("-----性能测试-----");
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
        System.out.println("0-" + a + "..." + "sst.size() = " + sst.size());
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) + "ms");
    }

    public static void main(String[] args) {
        new Client().testFun();
    }
}
