package algorithms.search;

import java.util.Random;

import tools.XMLUtil;

public class Client {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ST<Integer, String> sst;
        Object oSf = XMLUtil.getBean("sf");
        sst = new ST<Integer, String>((SFunction<Integer, String>) oSf);
        System.out.println(sst);
        if (sst.isEmpty()) {
            sst.put(3, "fan");
            System.out.println("sst.size() = " + sst.size());
        }
        if (!sst.containsKey(17)) {
            sst.put(17, "lamp");
            System.out.println("sst.size() = " + sst.size());
        }
        System.out.println("sst.get(20) = " + sst.get(20));
        sst.put(20, "computer");
        System.out.println("sst.get(20) = " + sst.get(20));
        sst.remove(20);
        Random rand = new Random();
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 10000; i++) {
            sst.put(125 + i, String.valueOf(abc.charAt(rand.nextInt(abc.length())))
                    + String.valueOf(abc.charAt(rand.nextInt(abc.length()))));
        }
        sst.put(123, "gg");
        sst.remove(3);
        sst.remove(123);
        sst.remove(17);
        System.out.println("-----输出集合全部内容-----");
        int a = 0;
        for (int k : sst.keySet()) {
            a++;
            sst.get(k);
        }
        int keyR = sst.keySet().iterator().next();
        System.out.println("next-key: " + keyR + " next-val: " + sst.get(keyR));
        System.out.println("0-" + a + "..." + "sst.size() = " + sst.size());
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) + "ms");
    }
}
