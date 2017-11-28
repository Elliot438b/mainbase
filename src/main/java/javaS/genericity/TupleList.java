package javaS.genericity;

import java.util.*;

import javaS.genericity.interfaceS.JuiceGenerator;
import javaS.genericity.interfaceS.Orange;

public class TupleList<A, B, C> extends ArrayList<ThreeTuple<A, B, C>> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        TupleList<Integer, Orange, IceCream> tl = new TupleList<Integer, Orange, IceCream>();
        ThreeTuple<Integer, Orange, IceCream> tt1 = new ThreeTuple<Integer, Orange, IceCream>(12, new Orange(),
                IceCream.generator().next());// 我们创建对象的时候全都在复用以前的代码，这很好。
        ThreeTuple<Integer, Orange, IceCream> tt2 = new ThreeTuple<Integer, Orange, IceCream>(3,
                (Orange) new JuiceGenerator().next(), IceCream.generator().next());// 我们创建对象的时候全都在复用以前的代码，这很好。
        tl.add(tt1);
        tl.add(tt2);
        for (ThreeTuple<Integer, Orange, IceCream> a : tl)
            System.out.println(a);
    }
}
