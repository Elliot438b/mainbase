package javaS.genericity.interfaceS;

import java.util.Iterator;
import java.util.Random;

/**
 * Generator, 对象生成器
 * 
 * @Iterable 实现了Iterable接口，该实现类就具备了可迭代的功能，支持for each迭代循环
 * 
 * @author Evsward
 *
 */
public class JuiceGenerator implements Generator<Juice>, Iterable<Juice> {
    // 存储所有果汁类的数组
    private Class[] types = { Lemon.class, Grape.class, Orange.class, Peach.class, Pear.class };
    private Random random = new Random();

    @Override
    public Juice next() {
        try {
            // 根据下标随机选择子类实例，并强制转型为基类对象
            return (Juice) types[random.nextInt(types.length)].newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 实现Iterator接口的私有内部类，外界无法直接访问
    private class JuiceIterator implements Iterator<Juice> {
        // 传入数据长度，作为默认迭代次数（也可以在上层类中定义次数）
        // 也可以将其理解为“末端哨兵”，用来判断何时停止
        private int count = types.length;

        @Override
        public boolean hasNext() {
            return count > 0;
        }

        @Override
        public Juice next() {
            count--;
            return JuiceGenerator.this.next();
        }

    }

    @Override
    public Iterator<Juice> iterator() {
        return new JuiceIterator();
    }

    public static void main(String[] args) {
        // JuiceGenerator实现了Iterable接口，所以它可以在循环中使用
        for (Juice j : new JuiceGenerator()) {
            System.out.println(j);
        }
    }
}
