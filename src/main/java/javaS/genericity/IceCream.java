package javaS.genericity;

import java.util.List;
import java.util.Queue;
import java.util.Random;

import javaS.genericity.interfaceS.Generator;
import javaS.genericity.interfaceS.Juice;
import javaS.genericity.interfaceS.JuiceGenerator;
import javaS.genericity.methodS.Container;
import javaS.genericity.methodS.Generators;

/**
 * 泛型在匿名内部类中的应用
 * 
 * @author Evsward
 *
 */
public class IceCream {
    private static long counter = 0;
    private final long id = counter++;

    //构造器是private的，那么外部无法使用new来创建对象，必须使用Generator来创建。
    private IceCream() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "....." + id;
    }

    /**
     * 这里通过一个匿名内部类返回一个Generator
     * 
     * @return
     */
    public static Generator<IceCream> generator() {
        return new Generator<IceCream>() {
            public IceCream next() {
                return new IceCream();
            }
        };
    }

    // 随意输出一个结果，让IceCream与Juice建立一个联系。
    public static void match(IceCream i, Juice j) {
        System.out.println(i + " matches " + j);
    }

    public static void main(String[] args) {
        Random random = new Random();
        // 随时记住复用我们之前写好的工具类
        Queue<Juice> drinks = Container.queue();// 创建一个集合用来存果汁
        List<IceCream> ices = Container.list();// 创建一个集合用来存冰激凌
        // 两种方式的generator。
        Generators.fill(drinks, new JuiceGenerator(), 6);
        Generators.fill(ices, IceCream.generator(), 3);
        // 输出结果
        for (Juice j : drinks)
            match(ices.get(random.nextInt(ices.size())), j);
    }
}
