package javaS.genericity.methodS;

import java.util.ArrayList;
import java.util.Collection;

import javaS.genericity.interfaceS.FibonacciGeneratorAdapter;
import javaS.genericity.interfaceS.Generator;
import javaS.genericity.interfaceS.Juice;
import javaS.genericity.interfaceS.JuiceGenerator;

public class Generators {
    /**
     * 将Generator生成的next元素填充进一个Collection中。
     * 
     * @param col
     *            目标Collection
     * @param gen
     *            元素生成器
     * @param n
     *            生成器工作的次数
     * @return
     */
    public static <T> Collection<T> fill(Collection<T> col, Generator<T> gen, int n) {
        for (int i = 0; i < n; i++)
            col.add(gen.next());
        return col;
    }

    public static void main(String[] args) {
        // 使用时要指定具体类型
        Collection<Juice> colJuice = Generators.fill(new ArrayList<Juice>(), new JuiceGenerator(), 5);
        Collection<Integer> fibonacci = Generators.fill(new ArrayList<Integer>(), new FibonacciGeneratorAdapter(), 10);
        for (Juice j : colJuice)
            System.out.println(j);
        for (Integer i : fibonacci)
            System.out.println(i);
    }
}
