package javaS.genericity.interfaceS;

import java.util.Iterator;

/**
 * 实现了Iterable接口的斐波那契生成器
 * 
 * @author Evsward
 *
 */
public class FibonacciGeneratorAdapter extends Fibonacci implements Iterable<Integer> {

    private int num;

    public FibonacciGeneratorAdapter() {
    }

    public FibonacciGeneratorAdapter(int num) {
        this.num = num;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int n = num;// 当前迭代元素下标

            @Override
            public boolean hasNext() {
                return n > 0;// 边界
            }

            @Override
            public Integer next() {
                n--;// 控制次数
                // 无需改变，直接引用基类的next方法。
                return FibonacciGeneratorAdapter.this.next();
            }
        };// 匿名内部类，结尾要带“;”
    }

    public static void main(String[] args) {
        // 用构造器来设置迭代次数
        for (int a : new FibonacciGeneratorAdapter(18))
            System.out.println(a);
    }
}
