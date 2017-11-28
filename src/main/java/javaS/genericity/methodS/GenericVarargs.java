package javaS.genericity.methodS;

import java.util.ArrayList;
import java.util.List;

import algorithms.sort.InsertSort;
import algorithms.sort.QuickSort;
import algorithms.sort.SelectSort;

public class GenericVarargs {
    /**
     * 通过泛型自己实现java.util.Array.asList()
     * 
     * @param args
     *            可变参数列表，数量并不确定
     * @return 将参数中不定数量的元素变成一个List
     */
    @SafeVarargs
    public static <T> List<T> makeList(T... args) {
        List<T> result = new ArrayList<T>();
        for (T item : args)
            result.add(item);
        return result;
    }

    public static void main(String[] args) {
        System.out.println(makeList(1, 2, 4));
        System.out.println(makeList('a', 'b', 'c'));
        System.out.println(makeList(new QuickSort(), new SelectSort(), new InsertSort()));
        System.out.println(makeList(1, "this", 4));// 参数列表中也可以互相不是同一类型，因为编译器会将他们转为Object对象
    }
}
