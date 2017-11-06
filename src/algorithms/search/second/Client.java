package algorithms.search.second;

import java.util.Random;

public class Client {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SequentialSearchST<Integer, String> sst = new SequentialSearchST<Integer, String>();
        if (sst.isEmpty()) {
            sst.put(3, "fan");
            System.out.println("sst.size() = " + sst.size());
        }
        if (!sst.contains(17)) {
            sst.put(17, "lamp");
            System.out.println("sst.size() = " + sst.size());
        }
        System.out.println("sst.get(20) = " + sst.get(20));
        sst.put(20, "computer");
        System.out.println("sst.get(20) = " + sst.get(20));
        sst.hardDelete(20);
        System.out.println("sst.get(345) = " + sst.get(345));
        Random rand = new Random();
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 10000; i++) {
            sst.put(rand.nextInt(), String.valueOf(abc.charAt(rand.nextInt(abc.length())))
                    + String.valueOf(abc.charAt(rand.nextInt(abc.length()))));
        }
        sst.put(123, "gg");
        sst.hardDelete(3);
        sst.hardDelete(123);
        sst.hardDelete(17);
        System.out.println("-----输出集合全部内容-----");
        int a = 0;
        for (int k : sst.keys()) {
            System.out.println((a++) + "...." + sst.get(k));
        }
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) + "ms");
    }
}
