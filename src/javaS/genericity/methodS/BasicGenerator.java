package javaS.genericity.methodS;

import javaS.genericity.interfaceS.Generator;
import javaS.genericity.interfaceS.Juice;
import javaS.genericity.interfaceS.Orange;

/**
 * 为任何类生成一个生成器
 *
 * @author Evsward
 *
 * @param <T>
 */
public class BasicGenerator<T> implements Generator<T> {

    private Class<T> type;

    public BasicGenerator() {
    }

    // 也可以直接显式调用此构造函数为对象类型创建一个默认生成器
    public BasicGenerator(Class<T> type) {
        this.type = type;
    }

    @Override
    public T next() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对外提供一个静态方法，通过给定对象类型create一个默认生成器
     * 
     * @param type
     *            想要生成的类型
     * @set type 类必须为public，必须具备构造器
     * 
     * @return 一个默认生成器
     */
    public static <T> Generator<T> create(Class<T> type) {
        return new BasicGenerator<T>(type);
    }

    public static void main(String[] args) {
        // 从前创造多个类的对象的做法：
        Juice orange01 = new Orange();
        System.out.println(orange01);
        Juice orange02 = new Orange();
        System.out.println(orange02);
        Juice orange03 = new Orange();
        System.out.println(orange03);
        System.out.println("------------");
        // 吃了药以后，额不是，有了生成器以后，只需要设定要几个对象就循环几次，对象就全部创建出来了。
        Generator<Orange> gen01 = BasicGenerator.create(Orange.class);
        Generator<Orange> gen02 = new BasicGenerator<Orange>(Orange.class);
        for (int i = 0; i < 3; i++)
            System.out.println("gen-01-" + gen01.next());
        for (int i = 0; i < 3; i++)
            System.out.println("gen-02-" + gen02.next());

    }
}
