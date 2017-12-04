package javaS.IO;

import java.util.Arrays;
import java.util.Collection;

public class PPrint {
    public static String pFormat(Collection<?> c) {// 泛型方法
        if (c.size() == 0)
            return "[]";
        StringBuilder result = new StringBuilder("[");
        for (Object elem : c) {
            if (c.size() != 1) {
                result.append("\n");
            }
            result.append(elem);
        }
        if (c.size() != 1) {
            result.append("\n");
        }
        result.append("]");
        return result.toString();
    }

    /**
     * 打印一个可视化的集合
     * 
     * @param c
     */
    public static void pprint(Collection<?> c) {
        System.out.println(pFormat(c));
    }

    /**
     * 打印一个可视化的数组
     * 
     * @param c
     */
    public static void pprint(Object[] c) {
        System.out.println(pFormat(Arrays.asList(c)));
    }
}
