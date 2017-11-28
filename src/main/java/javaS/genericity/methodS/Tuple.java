package javaS.genericity.methodS;

import java.util.ArrayList;
import java.util.List;

import algorithms.sort.QuickSort;
import algorithms.sort.Sort;
import javaS.genericity.ThreeTuple;
import javaS.genericity.TwoTuple;

public class Tuple {
    public static <A, B> TwoTuple<A, B> twoTuple(A a, B b) {
        return new TwoTuple<A, B>(a, b);
    }

    public static <A, B, C> ThreeTuple<A, B, C> twoTuple(A a, B b, C c) {
        return new ThreeTuple<A, B, C>(a, b, c);
    }

    public static void main(String[] args) {
        // 原来的方式
        @SuppressWarnings("unused")
        TwoTuple<Sort, List<Integer>> two = new TwoTuple<Sort, List<Integer>>(new QuickSort(),
                new ArrayList<Integer>());
        // 现在的方式
        two = Tuple.twoTuple(new QuickSort(), new ArrayList<Integer>());
    }
}
