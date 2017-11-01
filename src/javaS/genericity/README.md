> 又写了一篇很长的文章，原以为泛型的内容并没有这么多，但是后来我发现我以为错了。
> 关键字：泛型，Iterable接口，斐波那契数列，匿名内部类，可变参数列表
> 一般类和方法，要么只能使用基础类型，要么是自定义的类。如果要编写可以应用于多种类型的代码，这种刻板的限制会对代码的束缚很大。

- Java中，当你将一个基类作为一个方法的参数传入的时候，所有该基类的子类均可以作为参数，然而private构造器或者final修饰的类是不可被继承的，再加上Java的单继承特性，这种看上去的灵活性也有他的限制以及性能损耗。
- 如果参数为一个接口，要比继承好的多，任何实现了该接口的类都能满足该方法，而一个类是可以实现多个接口的。可是，反过来想我们必须实现该接口的所有方法才可以作为参数，这也是一种限制。

## 泛型

泛型实现了“参数化类型”的概念。

泛型的使用：

```
package javaS.genericity;

import algorithms.sort.QuickSort;

public class OldHolder {
    private Object a;

    public OldHolder(Object a) {
        this.a = a;
    }

    public void setA(Object a) {
        this.a = a;
    }

    public Object getA() {
        return a;
    }

    public static void main(String[] args) {
        OldHolder old = new OldHolder(new QuickSort());// 将OldHolder中的属性a赋值为QuickSort的实例
        old.setA(1);// 将a赋值为整型数字1
        old.setA("this is a value");// 将a赋值为String类型字符串
        System.out.println(old.getA());
    }
}

```
> this is a value

可以看到，OldHolder类中的属性a被设置为Object类型。我们都知道在Java中，所有类型都是Object的子类，所以这里将a定义为Object，在使用时a先后被赋值为类实例，整型，字符串，可见作者并不确定a到底是用来干嘛的。这时候，引入泛型的表现就是：

```
package javaS.genericity;

import algorithms.sort.QuickSort;

public class NewHolder<T> {

    private T a;

    public NewHolder(T a) {
        this.a = a;
    }

    public void setA(T a) {
        this.a = a;
    }

    public T getA() {
        return a;
    }

    public static void main(String[] args) {
        NewHolder<Integer> n = new NewHolder<Integer>(100);// 使用的时候将泛型定义为整型，那么只能限制设置a为整型值
        n.setA(1);// 将a赋值为整型数字1
        System.out.println(n.getA());
    }

}

```
> 1

我们在定义类的时候，不确定到底要使用什么类型的参数，就可以使用泛型来定义，等具体调用该类的时候，我们再指定类型来替换泛型。这就是泛型最简单的应用。

### 元组泛型
直接上代码：

```
package javaS.genericity;

public class TwoTuple<A, B> {
    public final A a;
    public final B b;

    public TwoTuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return a + "__&&&__" + b;
    }

    public static void main(String[] args) {
        TwoTuple<Integer, Integer> two = new TwoTuple<Integer, Integer>(110, 211);
        System.out.println(two);
    }
}

```
> 110\_\_&&&\_\_211

我们可以看出，类的泛型不仅仅可以定义一个，还可以定义两个，其实还可以定义三个，甚至更多，一次性定义多个对象不再困难。下面利用继承实现一个三个泛型的例子：

```
package javaS.genericity;

public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
    public final C c;

    public ThreeTuple(A a, B b, C c) {
        super(a, b);
        this.c = c;
    }

    @Override
    public String toString() {
        return a + "__&&&__" + b + "__&&&__" + c;
    }

    public static void main(String[] args) {
        ThreeTuple<Integer, Integer, Integer> three = new ThreeTuple<Integer, Integer, Integer>(110, 211, 985);
        System.out.println(three);
    }
}

```
> 110\_\_&&&\_\_211\_\_&&&\_\_985

### 栈类
下面用LinkList，链表实现一个下推栈。
> 下推栈就是把一个瓶子扣过来，往里面塞硬币，最先塞进去的硬币排在最顶，最新塞进去的硬币排在瓶口。

下面是代码：

```
package javaS.genericity;

public class LinkedStack<T> {
    private static class Node<U> {
        U item;
        Node<U> next;

        Node() {
            item = null;
            next = null;
        }

        Node(U item, Node<U> next) {
            this.item = item;
            this.next = next;
        }

        boolean end() {
            return item == null && next == null;
        }

    }

    /**
     * top是倒过来的瓶口位置。top|..|..|..，在最左侧
     */
    private Node<T> top = new Node<T>();// 结尾哨兵sentinel，空结点，item和next均为null

    public void push(T item) {
        top = new Node<T>(item, top);
    }

    public T pop() {
        T result = top.item;
        if (!top.end())// top的item和next均为空的时候，说明top碰到了末端哨兵，下推栈已空。
            top = top.next;
        return result;
    }

    public static void main(String[] args) {
        LinkedStack<String> lss = new LinkedStack<String>();
        for (String s : "This is a value".split(" ")) {
            lss.push(s);
        }
        String s;
        while ((s = lss.pop()) != null) {
            System.out.println(s);
        }
    }
}

```
> value
a
is
This

- 首先创建一个静态类用来描述链表的结点。
- 构建LinkedStack时创建了末端哨兵top。
- 每次push，都会将top向左移动一位，将新结点插入top的右侧。
- 每次pop，都是取出top的item，然后将top右移一位，直到top碰到末端哨兵。

末端哨兵的功能：链表都会设置一个哨兵，因为链表是“邻居依赖”，因此设置一个哨兵作为边界，当链表触碰到哨兵时即停止前进，否则会有越界异常。因此单链表通常会有一个末端哨兵，而双链表则需要两头均设置一个哨兵，左哨兵和右哨兵。

### 泛型应用：代码训练 RandomList

准备：
- 一个持有特定类型的List容器
- 一个从该容器中随机获取该特定类型元素的方法

代码如下：

```
package javaS.genericity;

import java.util.*;

public class RandomList<T> {
    private List<T> storage = new ArrayList<T>();
    // new Random(long seed); Random是个伪随机，通过seed随机规则来生成随机数。
    // seed可以理解为random生成随机数的规则，如果seed相同，那么random生成的随机数也肯定一样。
    // 如果不指定seed，则每次生成随机数的seed不同，那么random每一次执行生成的随机数也不同，真的随机了。
    private Random random = new Random();

    public void add(T item) {
        storage.add(item);
    }

    public T select() {
        return storage.get(random.nextInt(storage.size()));
    }

    public static void main(String[] args) {
        RandomList<String> rlist = new RandomList<String>();
        for (String a : "this is a value".split(" ")) {
            rlist.add(a);
        }
        for (int i = 0; i < rlist.storage.size(); i++) {
            System.out.println(rlist.select());
        }
    }
}

```
> a
a
is
this

在使用RandomList时，指定其泛型的具体类型，然后随机取出元素。

### 泛型接口
使用泛型来作为接口，我们采用一个生成器，它的功能是生产对象，类似与工厂模式，但是工厂模式一般是需要参数的，参数是有继承关系的，而我们采用泛型，生成器无需额外信息就知道如何创建对象。生成器一般有且只有一个方法，就是生产对象。我们还是实现一个随机生成对象的例子。
代码如下，首先我们先创建一个继承关系的父类和其子类们：

```
package javaS.genericity;

public class Juice {
    private static long counter = 0;
    private final long id = counter++;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "....." + id;
    }

}

```
定义一个基类，可以统计每一次通过该基类创建子类实例的次数，重写toString方法来输出自增id。然后定义其子类：

```
package javaS.genericity;

public class Apple extends Juice {}
public class Orange extends Juice {}
public class PineApple extends Juice {}
public class Pear extends Juice {}
public class Peach extends Juice {}

```
我们定义了5个子类，然后开始我们的重头戏，创建生成器Generator：

```
package javaS.genericity.interfaceS;

public interface Generator<T> {
    T next();
}

```
上面这个类很有用，把它放到你的工具类里面，日后随时会用到。
```
package javaS.genericity;

import java.util.Random;

public class JuiceGenerator implements Generator<Juice> {

    private Class[] types = { Apple.class, Pear.class, Peach.class, PineApple.class, Orange.class };
    private Random random = new Random();

    @Override
    public Juice next() {
        try {
            return (Juice) types[random.nextInt(types.length)].newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        JuiceGenerator gen = new JuiceGenerator();
        for (int i = 0; i < 5; i++)
            System.out.println(gen.next());
    }
}

```
> Peach.....0
PineApple.....1
Apple.....2
Orange.....3
Orange.....4

下面引入Iterable接口，

#### Iterable接口
Iterable是一个接口，任何实现该接口的类都具备可迭代的功能，支持for each循环迭代。

注意：
- 实现了Iterable接口，会要求实现其方法返回一个Iterator的实例。
- 实现了该接口的类一定要在内部建立一个私有内部类去实现Iterator接口，并在上一层中返回该Iterator的实例。
- 注意总结区分Iterable和Iterator接口。

```
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
    private Class[] types = { Apple.class, Grape.class, Orange.class, Peach.class, Pear.class };
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

```
> Orange.....0
Peach.....1
Orange.....2
Pear.....3
Peach.....4

问题：为什么不直接实现Iterator？

通过代码观察可以发现，实现Iterable接口的方法每一次返回一个新的Iterator，而直接实现Iterator就需要设置当前迭代位置，这个当前迭代位置的值在该实现类作为参数传来传去的时候，会始终作为该实现类的私有属性存在内存里，那么依赖该属性的hasNext方法和next方法的结果将变得不可预知，这将为我们的程序造成困惑。

一句话总结就是，对于迭代器，我们只是将其当做工具，希望它每一次都是从头开始迭代，不要与上一次迭代发生关系。

#### 斐波那契数列
斐波那契数列就是从第2个数开始，每个数的大小为前两个数字之和。这里也通过泛型接口实现，依然实现我们上面的基本生成器Generator类。代码如下：

```
package javaS.genericity.interfaceS;

/**
 * 用泛型接口实现斐波那契数列
 * 
 * @注意 实现泛型接口的时候就要指定泛型的具体类型了
 * @author Evsward
 *
 */
public class Fibonacci implements Generator<Integer> {
    private int count;// 定义一个自加器，用来当做斐波那契数列的线性增加的个数

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

```
注意：基本类型无法作为泛型的类型参数。

- 编写一个实现了Iterable的斐波那契生成器

这里我们不会去重写这个类，因为上面那个类也很有教学意义，我们只好采用创建一个适配器来做这件事情，关于适配器模式的知识请移步[促和谐好干部——适配器模式](http://www.cnblogs.com/Evsward/p/adapter.html)。

代码如下：

```
package javaS.genericity.interfaceS;

import java.util.Iterator;

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

```
> 1
1
2
3
5
8
13
21
34
55
89
144
233
377
610
987
1597
2584

上面这段代码有几个要点：
1. 这里通过继承原有类Fibonacci来实现适配器模式，代码中直接复用基类的斐波那契数列的实现return FibonacciGeneratorAdapter.this.next();
2. 通过构造器来控制迭代次数，我们在构造函数中设置了迭代次数，并且在迭代器的hasNext方法中将该次数作为结束的判断。
3. 这一次并没有使用新建一个私有内部类来实现Iterator接口，而是采用了匿名内部类的方式，匿名内部类省去了我们对私有内部类的声明，匿名就是不需要为这个私有内部类取名字，直接返回的就是Iterator的匿名实现。注意，匿名内部类的结尾，大括号后面要有“;”。

### 泛型方法
上面介绍了泛型的简单定义，元组的使用，又介绍了泛型接口，这些都是针对整个类的，现在要介绍的是可以作用与单个方法上的泛型方法。
> 泛型方法的使用就是在方法的返回值前面加上泛型参数即可。
下面用一段代码简单说明：

```
package javaS.genericity.methodS;

public class GenericMethods {
    public <T> void f(T x) {
        System.out.println(x.getClass().getName());
    }

    public static void main(String[] args) {
        GenericMethods gm = new GenericMethods();
        gm.f(1000000000000L);
        gm.f(23);
        gm.f("3");
        gm.f(3.4);
        gm.f('a');
        gm.f(gm);
    }
}

```
> 输出：

    java.lang.Long
    java.lang.Integer
    java.lang.String
    java.lang.Double
    java.lang.Character
    javaS.genericity.methodS.GenericMethods

学到了泛型方法，Java作者给出的建议是，
> 无论何时，只要你能做到，你就应该尽量使用泛型方法。意思就是在泛型类和泛型方法之间，要选择泛型方法，因为这样会定位更加精准，可读性更高。

泛型方法与泛型类的区别，以及优势：
- 泛型类必须在类创建的时候就指定好具体类型来代替泛型
- 泛型方法不需要指定具体类型，就像调用正常方法那样去掉用就好了，因为编译器会为我们找出参数的类型，就像上面代码的输出，它甚至可以接受类对象本身作为其参数，如上面代码输出的最后一行。
> 这称为参数类型推断（type argument inference）
- 当调用f(n)泛型方法时，传入了基本类型作为参数,
> 自动打包机制，介入将基本类型包装为对应的对象，如“java.lang.Integer”

这个自动打包机制甚至代替了我们很多时候的手写工作，好处众多，那就从现在开始，每次写代码的时候，将泛型方法放在你的优先级最高。

#### 参数类型推断
上面介绍了那个Generator类，提到它不仅仅是学习中的教材，还是最佳的实践，应该将它放在你的工具类中，日后遇到对象创建工作均可以使用到它。下面，我要再写一段也很有用的代码：

```
package javaS.genericity.methodS;

import java.util.*;

public class Container {
    public static <K, V> Map<K, V> map() {
        return new HashMap<K, V>();
    }

    public static <T> List<T> list() {
        return new ArrayList<T>();
    }

    public static <T> LinkedList<T> lList() {
        return new LinkedList<T>();
    }

    public static <T> Set<T> set() {
        return new HashSet<T>();
    }

    public static <T> Queue<T> queue() {
        return new LinkedList<T>();
    }
}

```
没学过泛型之前，看到上面的代码，会有种不明觉厉的感觉，
> “能写出类似以上代码的人，应该就是架构师吧”曾经弱弱的我如是想。

问：以上代码有什么用处呢？

当我们要创建一个容器的时候，先写个复杂一点的：
> Map<Animal, List<? extends Pet>> pet = new HashMap<Animal, List<? extends Pet>>();

这行代码很常见吧，参数类型那么长还要写两遍，会不会感觉有点烦，现在我们只要：
> Map<Animal, List<? extends Pet>> pet = Container.map();

就可以了，参数类型只需要定义一遍，化解了还要再抄一遍参数类型的烦恼。

---
@deprecated
> 类型推断只对已知参数类型有效，例如你可以传一个1，编译器会认出它是整型Integer对象，就会用Integer去代替原参数位置的泛型类型，但你不能寄希望于传入一个泛型参数让编译器去猜，它会报错的。( ∙̆ .̯ ∙̆ ) 

所以接着上面的代码写：
> f(Container.map()); // 编译器会不理你的。

然而，若你直接指明类型：
> f(Container.<Animal, List<Pet>>map()); // 这样是可以的。

---
以上内容已过时了，我使用的是jdk1.8，测试发现编译器并未报错：

```
public static void main(String[] args) {
        GenericMethods gm = new GenericMethods();
        Map pet = Container.map();
        gm.f(Container.map());
        gm.f(Container.<Sort, List<Grade>>map());
    }
```
> 输出：

    java.util.HashMap
    java.util.HashMap

可以看出无论是未指定参数类型的Container.map()，还是显式地指定参数类型的，参数类型推断机制都发挥了效用。或许，未指定参数类型的map在之后可以指定，但是并不建议这么用。虽然编译器未报错，但这么写规规矩矩地不好么？
> Map<Sort, List<Grade>> map = Container.map();

#### 可变参数列表
> 可变参数列表就是参数的个数并不确定，用...来表示

实例代码如下：

```
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

```
> 输出

    [1, 2, 4]
    [a, b, c]
    [algorithms.sort.QuickSort@15db9742, algorithms.sort.SelectSort@6d06d69c, algorithms.sort.InsertSort@7852e922]
    [1, this, 4]

我们可以看到，由于泛型的使用，配合可变参数列表，我们可以将任意类型的元素随意组合传入该方法转换成一个List，泛型的这个特性很强大。


