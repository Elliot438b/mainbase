package javaS.genericity.interfaceS;

/**
 * 用泛型接口实现斐波那契数列
 * 
 * @注意 实现泛型接口的时候就要指定泛型的具体类型了
 * @author Evsward
 *
 */
public class Fibonacci implements Generator<Integer> {
    int count;// 定义一个自加器，用来当做斐波那契数列的线性增加的个数

    @Override
    public Integer next() {
        return fib(count++);// 斐波那契数列的线性增加个数
    }

    /**
     * 递归调用自己，每次的结果为前两个数之和
     * 
     * @param n
     * @return
     */
    private int fib(int n) {
        if (n < 2)
            return 1;
        return fib(n - 2) + fib(n - 1);
    }

    public static void main(String[] args) {
        Fibonacci fib = new Fibonacci();
        for (int i = 0; i < 10; i++)
            System.out.println(fib.next());
    }
}
