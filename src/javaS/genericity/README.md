> 掌握Java的泛型，这一篇文章足够了。

> 关键字：泛型，Iterable接口，斐波那契数列，匿名内部类，枚举，反射，可变参数列表，Set

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

#### 1. Iterable接口
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

Java容器中，所有的Collection子类会实现Iteratable接口以实现foreach功能，Iteratable接口的实现又依赖于实现了Iterator的内部类，有的容器类会有多个实现Iterator接口的内部类，通过返回不同的迭代器实现不同的迭代方式。如以上代码中JuiceIterator，这种迭代器可以做很多出来（例如前序迭代器，反向迭代器，随机迭代器等等），根据业务需要返回给类使用。

- override 方法iterator，可以让实现类默认拥有迭代的功能
- 也可以定义多个Iterator，除了返给override iterator方法以外，我们还可以通过类的对象去显式地调用其他Iterator。

一句话总结就是，对于迭代器，我们只是将其当做工具，希望它每一次都是从头开始迭代，不要与上一次迭代发生关系，并且工具可以有很多种，让我们随意挑选。

#### 2. 斐波那契数列
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
注意：基本类型无法作为泛型的类型参数，所以请先转为对应的对象类型再作为参数传入。
> 递归，好像你站在两面相对的镜子中间随意看向其中一面镜子，由于两面镜子之间相互的反射作用，你会发现镜子中有无数个自己，这就是递归。——程杰《大话数据结构》

- 编写一个实现了Iterable的斐波那契生成器

这里我们不会去重写这个类，因为上面那个类也很有教学意义，我们只好采用创建一个适配器来做这件事情，关于适配器模式的知识请移步[促和谐好干部——适配器模式](http://www.cnblogs.com/Evsward/p/adapter.html)。

代码如下：

```
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

#### 1. 参数类型推断
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

#### 2. 可变参数列表
> 可变参数列表就是参数的个数并不确定，用...来表示

实例代码如下：

```
package javaS.genericity.methodS;

import java.util.ArrayList;...

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
        // 参数列表中也可以互相不是同一类型（但是这时编译器会报一个警告，告诉你参数不安全）
        // 因为编译器会将他们转为Object对象
        System.out.println(makeList(1, "this", 4));
    }
}

```
> 输出

    [1, 2, 4]
    [a, b, c]
    [algorithms.sort.QuickSort@15db9742, algorithms.sort.SelectSort@6d06d69c, algorithms.sort.InsertSort@7852e922]
    [1, this, 4]

我们可以看到，由于泛型的使用，配合可变参数列表，我们可以将任意类型的元素随意组合传入该方法转换成一个List，泛型的这个特性很强大。

#### 3. 基于Generator的泛型方法
> 实例：将Generator创建的新元素填充进一个Collection的泛型方法。

这个例子里面，由于我们对元素的类型并不预知，也可以说使用了泛型方法以后，我们能够支持多种类型的参数，这样该方法的功能更加强大。

开始代码：

```
package javaS.genericity.methodS;

import java.util.ArrayList;...

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

```
> Lemon.....0
Orange.....1
Orange.....2
Pear.....3
Grape.....4
1
1
2
3
5
8
13
21
34
55

我们传入了Juice集合的实例，以及Juice生成器（生成器中实现了Iterable接口）的实例，我们也可以传入整型对象集合的实例，以及斐波那契生成器（生成器中实现了Iterable接口）的实例。我们可以分别获得一个可迭代的元素类型为Juice和一个元素类型为Integer的Collection对象。

### 通用Generator
> 为任意带有默认构造器的类创建一个生成器。

生成器上面提过很多次，属于对象创建模式的范畴。我们下面要写的通用Generator可以为任意类创建一个生成器，只要该类满足：
- 显式编写了无参构造器
- 该类为public的

这个类有了生成器，就可以利用他不断快速方便地创建对象。代码如下：

```
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

```
> 输出

    Orange.....0
    Orange.....1
    Orange.....2
    ------------
    gen-01-Orange.....3
    gen-01-Orange.....4
    gen-01-Orange.....5
    gen-02-Orange.....6
    gen-02-Orange.....7
    gen-02-Orange.....8

通过输出，以及代码中的注释，我们可以对比出来使用生成器创建对象是非常方便的，而且该生成器是基于泛型的，对于类的类型的处理非常灵活。

> 生成器Generator，类似于设计模式中的工厂模式，符合依赖倒转原则，里氏代换原则以及单一指责原则，避免了用new的方式去创建对象，解耦了对象和类之间的依赖关系。

当我们在工程实践中遇到需要多次创建类的对象的时候，可以采用BasicGenerator和Generator的结构，多多熟悉使用，增加我们的程序的灵活性。

#### 1. 简化元组类
我们这一节主要描述的是泛型方法，上面我们提过尽量使用泛型方法，而不是泛型类，那么之前研究到的所有泛型类均有机会被泛型方法所改造。元组类就是可以优化的一种。代码如下：

```
package javaS.genericity.methodS;

import java.util.ArrayList;...

public class Tuple {
    public static <A, B> TwoTuple<A, B> twoTuple(A a, B b) {
        return new TwoTuple<A, B>(a, b);
    }

    public static <A, B, C> ThreeTuple<A, B, C> twoTuple(A a, B b, C c) {
        return new ThreeTuple<A, B, C>(a, b, c);
    }

    public static void main(String[] args) {
        // 原来的方式
        TwoTuple<Sort, List<Integer>> two = new TwoTuple<Sort, List<Integer>>(new QuickSort(),
                new ArrayList<Integer>());
        // 现在的方式
        two = Tuple.twoTuple(new QuickSort(), new ArrayList<Integer>());
    }
}

```
通过直接调用方法去创建元组的对象。

#### 2. Set容器中泛型的使用

Set容器包含很多内置函数用来解决一些数学问题，加入了泛型以后，可以处理更多类型的参数。直接代码里面注释说：

```
package javaS.genericity.methodS;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Set通过使用泛型方法来封装Set的自有方法。
 * 
 * @author Evsward
 *
 */
public class SetSupply {
    // 求集合A、B的并集
    public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<T>(setA);// 不要直接操作setA，请保持setA的纯真
        result.addAll(setB);
        return result;
    }

    // 求集合A、B的交集
    public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<T>(setA);
        result.retainAll(setB);
        return result;
    }

    // 求集合A、B的差集
    public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<T>(setA);
        result.removeAll(setB);
        return result;
    }

    // 求集合A、B的并集-集合A、B的交集
    public static <T> Set<T> complement(Set<T> setA, Set<T> setB) {
        return difference(union(setA, setB), intersection(setA, setB));
    }

    public static void main(String[] args) {
        Set<Colors> setA = EnumSet.range(Colors.Red, Colors.Orange);
        Set<Colors> setB = EnumSet.range(Colors.Black, Colors.Blue);
        System.out.println(SetSupply.union(setA, setB));
        System.out.println(SetSupply.intersection(setA, setB));
        System.out.println(SetSupply.difference(setA, setB));
        System.out.println(SetSupply.complement(setA, setB));
    }
}

```
不要直接操作参数传过来的集合本身，我们复制了一份set出来作为结果集合。

- Set容器中泛型的使用，利用枚举做一个实例

```
package javaS.genericity.methodS;

public enum Colors {
    Red, Green, Black, White, Gray, Blue, Yellow, Purple, Pink, Orange
}

```
> 输出

    [Green, Gray, Blue, White, Pink, Purple, Red, Black, Orange, Yellow]
    [Gray, Blue, White, Black]
    [Green, Pink, Purple, Red, Orange, Yellow]
    [Green, Pink, Purple, Red, Orange, Yellow]
    
接下来，利用上面这个功能，我们来检查一下在jdk中Collection和Map的方法的差异：

```
package javaS.genericity.methodS;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MethodDif {
    static Set<String> methods(Class<?> type) {
        Set<String> methodSets = new TreeSet<String>();
        for (Method m : type.getMethods())
            methodSets.add(m.getName());
        return methodSets;
    }

    static void interfaces(Class<?> type) {
        System.out.println("Interfaces in " + type.getSimpleName() + ": ");
        List<String> result = new ArrayList<String>();
        for (Class<?> c : type.getInterfaces())
            result.add(c.getSimpleName());
        System.out.println(result);
    }

    static Set<String> objectMethods = methods(Object.class);// 比较之前要先把根类Object的方法去除。

    static {
        objectMethods.add("clone");
    }

    static void difference(Class<?> setA, Class<?> setB) {
        System.out.println(setA.getSimpleName() + "  " + setB.getSimpleName() + ", adds: ");
        Set<String> comp = SetSupply.difference(methods(setA), methods(setB));
        comp.removeAll(objectMethods);
        System.out.println(comp);
        interfaces(setA);
    }

    public static void main(String[] args) {
        // System.out.println("Collection: " + methods(Collection.class));
        // interfaces(Collection.class);
        // difference(Set.class, Collection.class);
        difference(Set.class, HashSet.class);
        difference(HashSet.class, Set.class);
    }

}

```
通过反射获得我们要比较的目标类所包含的方法名，并存入集合，然后调用之前写的那段比较Set的方法，进行查看两个类之间的方法区别。这里应用到了Java的反射机制。
### 反射与泛型

现在，Class类是泛型的，例如，String.class 实际上是Class\<String\>类的唯一的对象，类型参数十分有用，这是因为它允许Class\<T\>方法的返回类型更加具有针对性，参照JDK中Class\<T\>源码可以看到这些类型参数。

> java.lang.Class<T> 1.0

- T newInstance() 返回默认构造器构造的一个新实例。免除了类型转换。
- T cast(Object obj) 如果obj为null或有可能转换成类型T，则返回obj；否则抛出BadCastException异常。
- T[] getEnumConstants() 如果T是枚举类型，则返回所有值组成的数组，否则返回null。
- Class<? super T> getSuperclass() 返回这个类的超类（由于java是单继承，每个子类最多只有一个父类）如果T不是一个类或T是Object类（Object类已经是根类了，它没有超类），则返回null。
- Constructor<T> getConstructor(Class... parameterTypes)
- Constructor<T> getDeclaredConstructor(Class... parameterTypes) 这两个方法均返回一个Constructor<T>对象，Constructor类也已经变成泛型，以便它建立出来的实例与newInstance方法有一个正确的返回类型。

> java.lang.reflect.Constructor<T> 1.1

- T newInstance(Object... parameters) 返回一个指定参数构造的新实例。
### 匿名内部类

前面的代码例子中，实现Iterable接口，返回迭代器的部分，已经引用到了匿名内部类的特性，没看到的同学可以去到那里再看一眼，然后回到这里继续分析，泛型在匿名内部类中的应用。前面我们有了Juice基类，可以输出每一次输出对象的id，同时它也有自己的JuiceGenerator，用来随机迭代输出众多子类对象。我们可以继续使用他们，同时再新创建一个冰激凌类，果汁搭配冰淇淋，听上去就要拉肚子的配方。下面看代码：

```
package javaS.genericity;

import javaS.genericity.interfaceS.Generator;

/**
 * 泛型在匿名内部类中的应用
 * 
 * @author Evsward
 *
 */
public class IceCream {
    private static long counter = 0;
    private final long id = counter++;

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
}

```
随时记住复用我们之前的代码，

```
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

```
> 输出

    IceCream.....1 matches Peach.....0
    IceCream.....1 matches Orange.....1
    IceCream.....2 matches Grape.....2
    IceCream.....0 matches Orange.....3
    IceCream.....1 matches Grape.....4
    IceCream.....0 matches Lemon.....5

IceCream的构造器被指定为private，所以强制只能使用Generator来创建对象。我们复用了Container创建集合对象，复用了Generators的fill方法，将Generator创建出来的对象填充到一个集合中区。

### 创建复杂模型
我们来尝试创建一个复杂的容器，

```
package javaS.genericity;

import java.util.*;

public class TupleList<A, B, C> extends ArrayList<ThreeTuple<A, B, C>> {}

```
> 体内的恶魔在苏醒...

但是貌似我们没有用过多代码就得到了一个安全稳定的负载结构，下面在Client端进行使用该集合：

```
package javaS.genericity;

import javaS.genericity.interfaceS.JuiceGenerator;
import javaS.genericity.interfaceS.Orange;

public class Client {
    public static void main(String[] args) {
        TupleList<Integer, Orange, IceCream> tl = new TupleList<Integer, Orange, IceCream>();
        // 我们创建对象的时候全都在复用以前的代码，这很好。
        ThreeTuple tt1 = new ThreeTuple(12, new Orange(), IceCream.generator().next());
        // hmmm，这很好。
        ThreeTuple tt2 = new ThreeTuple(3, new JuiceGenerator().next(), IceCream.generator().next());
        tl.add(tt1);
        tl.add(tt2);
        for (ThreeTuple<Integer, Orange, IceCream> a : tl)
            System.out.println(a);
    }
}

```
> 输出

    12__&&&__Orange.....0__&&&__IceCream.....0
    3__&&&__Peach.....1__&&&__IceCream.....1

我们是在虐自己么？这种复杂集合模型有什么用呢，下面来构建一个实例。

```
package javaS.genericity;

import javaS.genericity.interfaceS.Generator;
import javaS.genericity.methodS.BasicGenerator;

/**
 * 举个栗子：线程池中有线程组，线程组中有三个元素。
 * 
 * @author Evsward
 *
 */
public class ThreadPoolSG {
    private TupleList<Integer, Thread, String> threadPool = new TupleList<Integer, Thread, String>();

    private int countId;

    // 使用生成器来生成线程组
    private class ThreadTupleGenerator implements Generator {

        @Override
        public ThreeTuple<Integer, Thread, String> next() {
            return new ThreeTuple(countId++, new Thread(), "xx" + countId);
        }

    }

    /**
     * 外部只提供该方法生成指定数量的线程池
     * 
     * @param n
     *            指定线程池的大小
     * @return
     */
    public TupleList<Integer, Thread, String> getThreadPool(int n) {
        // 先清空
        for (int i = 0; i < threadPool.size(); i++)
            threadPool.remove(i);
        for (int i = 0; i < n; i++)
            threadPool.add(new ThreadTupleGenerator().next());
        return threadPool;
    }

    public static void main(String[] args) {
        ThreadPoolSG tpsg = new ThreadPoolSG();
        for (ThreeTuple<Integer, Thread, String> t : tpsg.getThreadPool(5))
            System.out.println(t);
    }
}

```
> 输出

    0__&&&__Thread[Thread-0,5,main]__&&&__xx1
    1__&&&__Thread[Thread-1,5,main]__&&&__xx2
    2__&&&__Thread[Thread-2,5,main]__&&&__xx3
    3__&&&__Thread[Thread-3,5,main]__&&&__xx4
    4__&&&__Thread[Thread-4,5,main]__&&&__xx5

我写这个线程池只是为了举例，以上代码并不具备实践意义，关于java多线程的基础知识我会另开一篇博文专门研究，这里知识为了说明复杂模型的应用场景，大家可以自由发挥。

> 总结：使用泛型元组可以轻松构建复杂集合模型，且他们是类型安全可管理的。

---
- 看到这里，我想您已经很累了，后面的内容为选看，如果想了解和使用泛型，成为一个中级泛型程序员，那么以上内容已经足够，下面的内容可以定义为“提高篇”。

> 你看上了一个姑娘，目之所及之处全是各种美好，相处起来却发现她也有自己的小缺点啊。任何事不要停留在幻想，去理性接受一个人的弱点，就像你钦羡她的成功一样。

---

### 泛型的擦除——变个魔术

```
package javaS.genericity;

import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        List a1 = new ArrayList<String>();
        List a2 = new ArrayList<Integer>();
        System.out.println(a1 == a2);
        Class c1 = new ArrayList<String>().getClass();
        Class c2 = new ArrayList<Integer>().getClass();
        System.out.println(c1 == c2);
    }
}

```
让我来猜猜输出的情况，
> 输出：false
true

为什么是这样，我们很容易将ArrayList\<String\>和ArrayList\<Integer\>理解为不同的类型，为什么结果他们是相同的类型？我们进一步测试：

```
package javaS.genericity;

import java.util.*;

class Hey<YYYY, B, C, D> {
}

public class Client {
    public static void main(String[] args) {
        Class c1 = new ArrayList<String>().getClass();
        Class c2 = new ArrayList<Integer>().getClass();
        Class c5 = new HashSet<Integer>().getClass();
        Class c6 = new HashMap<Integer, String>().getClass();
        Class c7 = new Hey<Integer, String, Integer, String>().getClass();
        System.out.println(Arrays.toString(c1.getTypeParameters()));
        System.out.println(Arrays.toString(c2.getTypeParameters()));
        System.out.println(Arrays.toString(c5.getTypeParameters()));
        System.out.println(Arrays.toString(c6.getTypeParameters()));
        System.out.println(Arrays.toString(c7.getTypeParameters()));
    }
}

```
> [E]
[E]
[E]
[K, V]
[YYYY, B, C, D]

jdk官方文档对Class.getTypeParameters()的解释是：
>Returns an array of TypeVariable objects that represent the type variables declared by the generic declaration represented by this GenericDeclaration object

意思就是这个方法将返回一个TypeVariable对象数组，表示有泛型声明所声明的类型参数。然而无论你泛型指定的类型是Integer也好，String也罢，输出的结果都是无含义的占位符而已，也就是说，
> 在泛型代码内部，无法获得任何有关泛型参数类型的信息。

所以ArrayList\<String\>和ArrayList\<Integer\>在运行上是相同的类型。这两种形式都被擦除成他们的原生类型，即List。

### 探索泛型的底层原理，类型的擦除机制。

Java对对象的控制要大过C++，对象只能调用自己事先声明过的方法，对于陌生的方法，编译器并不会自动识别。例如：

```
package javaS.genericity;

import algorithms.sort.QuickSort;

public class NewHolder<T> {

    private T a;

    public NewHolder(T a) {
        this.a = a;
    }

    public void aMethod(int[] arr) {
        a.sort(arr);// 报错了！！！The method sort() is undefined for the type T
        // 除非改成强制转为指定类型。那我们的NewHolder<T>类是否就失去了泛型的意义？
        ((NewHolder<QuickSort>) a).sort(arr);
    }

    public static void main(String[] args) {
        int[] a = { 12, 5, 66, 23 };
        // 使用的时候将泛型定义为整型，那么只能限制设置a为整型值
        NewHolder<QuickSort> n = new NewHolder<QuickSort>(new QuickSort());
        n.aMethod(a);// 我们期望能执行new QuickSort().sort(array)
    }

}

```
以上代码在Java中编译都不会通过，但有意思的是在C++中是很正常的存在，NewHolder在自己的aMethod方法中用泛型T的对象来调用跟它完全陌生的sort方法，它怎么知道T的对象认识sort方法呢？如果NewHolder在使用时被指定为其他类型，例如NewHolder\<Integer\>，那么这个T的对象就是一个整型类型，无论如何Integer里面也不会有我们在QuickSort中自定义的sort方法的。Java之于C++是青出于蓝，泛型的思想也是源于C++，但是在java里落地生根，泛型的机制发生了变化，最大的变化就是Java的编译器需要预先指定泛型类的边界，以便告知编译器只能接受这个边界的类型，超越这个边界的类型在该泛型类中不予支持。

> 泛型的边界，重用extends关键字。


```
package javaS.genericity;

import algorithms.sort.QuickSort;
import algorithms.sort.Sort;

public class NewHolder<T extends Sort> {

    private T a;

    public NewHolder(T a) {
        this.a = a;
    }

    public void aMethod(int[] arr) {
        a.sort(arr);// 编译通过
    }

    /**
     * 如果想要通过泛型类的持有属性a来调用Sort中的其他方法，需要定义泛型类自己的方法来包含Sort的方法。
     * 
     * @provide 对外提供方法来调用Sort的内部方法
     * @param arr
     */
    public void show(int[] arr) {
        a.show(arr);// 编译通过
    }

    public static void main(String[] args) {
        int[] a = { 12, 5, 66, 23 };
        QuickSort q = new QuickSort();
        NewHolder<QuickSort> n = new NewHolder<QuickSort>(q);// 使用的时候将泛型定义为整型，那么只能限制设置a为整型值
        n.aMethod(a);// 我们期望能执行new QuickSort().sort(array)
        q.show(a);
        System.out.println("--------------------我是方法作用域的分界线--------------------");
        n.show(a);
    }

}

```
> 输出：

    66
    23
    12
    5
    数组长度：4，执行交换次数：5
    --------------------我是方法作用域的分界线--------------------
    66
    23
    12
    5
    数组长度：4，执行交换次数：5

- 我们通过\<T extends Sort\>的方式指定了泛型的边界，该泛型只接受Sort类型，当然了Sort的子类也都在这个范畴，因此合情合理可以调用Sort的内部方法了。
- 我们正常的通过Sort的对象去调用其方法，也可以在泛型类中通过持有的泛型属性来调用，只要再定义一个泛型类自己的方法即可。

泛型中关于类型的擦除，如果没有指定泛型的边界，擦除机制会将该泛型的所有类型都擦掉，最终泛型只沦落为一个占位符而已，就像上面提到过的那样。而如果定义了它的边界，擦除机制会将参数类型擦除到这个边界，就好像在类的声明中用Sort替换了T一样。

问题：为什么不直接用\<Sort\>代替\<T extends Sort\>啊？非要搞出个泛型T，还要定义他的边界，其实效果不就跟直接将泛型指定为Sort来的方便吗？甚至，都不要搞泛型了，直接把持有对象属性T a改为Sort a不就完事了，泛型在这里真是多此一举啊。

解答：只有当你希望使用的类型参数比某个具体类型（以及他的所有子类型）更加“泛化”时——也就是说，当你希望代码能够跨多个类工作时，使用泛型才有所帮助。因此，使用泛型边界通常要比直接类替换更加复杂。举个弱弱的例子，如果某个类有一个返回T的方法，那么泛型就有所帮助，因为它会返回确切的类型，而不是基类。（好吧，这也算数？？那我们平时总使用的List list = new ArrayList(); 如何解释啊？当然了，肯定会有希望获得具体类型对象而不是基类对象的场景，例如我只想使用子类特有的方法。。但是这样的话又会重蹈找不到方法的覆辙吧。）

> 这个回答我自己都不满意。往下分析再看看吧，Java作者肯定比我聪明，会有更好的解答的，否则设计个泛型的边界有何用？

#### 1. 请给我一个合理的解释？迁移兼容性

首先重定向上面的疑虑，泛型边界不是Java作者专门设计的什么牛逼的语言特性，而是一个折中方案，整个折中方案的核心就是擦除机制，java爸爸们为了能让我们使用上新款“泛型”特性，也是做足了里子面子，想出擦除这么一个折中办法。

下面用三寸不烂之舌谈谈历史问题，

如果从Java诞生之时，就有泛型，那么泛型一定不会用类型擦除的方式实现，而是使用具体化，使类型参数保持为第一类实体，因此你就能够在类型参数上执行基于类型的语言操作和反射操作。

> 擦除减少了泛型的泛化行，泛型在Java中仍旧是有用的，只不过没有设想的那么有用，原因就是擦除。

#### 2. 泛型是java在5.0以后加入的特性。
Java SE5之前，有大量的非泛型类库，之后我们热爱泛型的工程师们开始了泛化之路，但是之前的非泛化类库如何升级为泛化呢？这就需要“迁移兼容性”，将非泛化类库变为泛型时，不会破坏依赖于它的代码和应用程序。（任何一个有良心的组织或公司，都会让他们的系统向旧机兼容，ps3好像就不能玩耍ps4的游戏呢，幸好iphone5s还能安装IOS11）这个良心目标定下了以后，众多“最强大脑”们认为擦除是唯一可行的解决方案。通过允许非泛型代码与泛型代码共存，擦除使得这种向着泛型的迁移成为可能。

#### 3. 擦除的问题

由于上一节提到的那个崇高的动机，擦除的代价是显著的：
> 泛型不能用于显式地引用运行时类型的操作之中（只能处于类型声明阶段），例如转型、instanceof和new表达式。因为所有关于参数的类型信息都丢失了，无论何时，当你在编写泛型代码时，必须时刻提醒自己，你只是看起来好像拥有有关参数的类型信息而已。

例如：

```
class Fool<T>  {
    T a;
}
```
看上去你在创建一个Fool的实例，

```
Fool<Joke> f = new Fool<Joke>();
```
泛型语法也在强烈暗示：在整个类的各个地方，类型T都在被替换。但是事实并非如此，无论何时，当你在编写整个类的代码时，必须提醒自己：
> No, it's just an Object!

擦除和迁移兼容性意味着，泛型的使用并不是强制的，所以我们经常可以在使用那种没有指定泛型的子类时，经常会有warning出来，（我知道有些人对代码下面的黄色波浪线也很难容忍）这时通过一个注解：
> @SuppressWarings("unchecked")

可以不让这个warning出来扰乱你，但是作者仍旧好心提醒，请把这个注解放在离你warning代码最近的位置，而不是整个类上，这样可以避免忽略掉其他真正的问题。

- 而当你希望泛型参数不仅仅是个Object，就需要管理泛型的边界。

上面提到的那个疑虑，不使用泛型直接使用类替换和使用泛型边界这两种效果是一样的，我们可以通过跟踪这两种代码在编译期间的字节码来对比发现，确实如我们所料字节码是相同的。

擦除在方法体中移除了类型信息，所以在运行时的问题就是边界：即对象进入和离开方法的地点，这正是编译器在编译期执行对传入值的类型检查并插入传出值的转型代码（因为边界让泛型转型为具体类型）的地点。
> 边界就是发生动作的地方。

#### 4. 擦除的补偿
前面强调过，泛型不能显式地引用运行时类型的操作之中，换句话来讲，就是运行时，泛型一定被具体类型替代。任何试图运行泛型操作的举动都将被编译器视为违法。
例如，

```
// T并没有指定具体类型，在编译器那里就是个占位符，没有任何类型的特征以及类型操作的能力
// 所以这一行代码直接报错。
T t = new T();
```
要想创建类的实例，首先要确定类的类型，而泛型恰恰类型已被擦除。
>
```
// 判断arg是否是B的实例，还是那句话，要确定类的实例，首先这个类得“是个真的类”，泛型不是真的类
// 所以这一行直接报错。
if(arg instanceof B){}
```
综上所述，在java中，所有关于具体类型的操作，泛型都是做不了的。

问：擦除这么大代价，我要是想实现上面那些功能，该如何补偿？

```
package javaS.genericity;

import java.util.ArrayList;
import java.util.Arrays;

public class Client<B> {
    B b;

    public Client(Class<B> arg) {
        try {
            b = arg.newInstance();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Class c1 = new ArrayList<String>().getClass();
        Class c2 = new ArrayList<Integer>().getClass();
        Class c7 = new Client<String>(String.class).getClass();
        System.out.println(Arrays.toString(c1.getTypeParameters()));
        System.out.println(Arrays.toString(c2.getTypeParameters()));
        System.out.println(Arrays.toString(c7.getTypeParameters()));
    }
}


```
> [E]
[E]
[B]

可以采用工厂模式来创建泛型的实例，实际上在调用该工厂模式的时候，泛型已经被具体类型化了。

### 泛型数组
- 泛型数组一般使用ArrayList，例如我们常见的

```
public class Client<T> {
    List<T> array = new ArrayList<T>();// 看这里。。

    public void add(T x) {
        arr.add(x);
    }

    public static void main(String[] args) {
        Client<String> c = new Client<String>();
        c.add("hey,");
        c.add("yeah.");
        System.out.println(c.arr);
    
    }
}
```
> [hey,, yeah.]

这将使你获得数组的行为，以及由泛型提供的编译期的类型安全。

### 通配符？
#### 1. \<? extends SuperClass\>

```
package javaS.genericity;

import java.util.*;

import javaS.genericity.interfaceS.Juice;
import javaS.genericity.interfaceS.Lemon;

public class Client<T> {
    public static void main(String[] args) {
        List<? extends Juice> juice = new ArrayList<Lemon>();
        juice.add(null);// 实际上除了null以外，任何值都add不了，直接报错。。。
        Juice aha = juice.get(0);
        System.out.println(aha);
    }
}

```
> null

感觉自己是个逗逼。。。

我为什么要写出这样的代码，WTF。好吧，蓄力分析一波，List<? extends Juice> 可以读作“具有任何从Juice继承过来的类型的列表”。但是我们知道泛型是假的类型，juice不是什么真正的Juice子类的实例列表，那好，我们在new后面指定了具体类型Lemon，继承自Juice。气力已尽，总之，在实践中可以不用泛型，但不要写出这样的代码。

#### 2. \<? super Class\>

超类型通配符。

```
package javaS.genericity;

import java.util.*;

import javaS.genericity.interfaceS.Lemon;

public class Client<T> {
    public static void main(String[] args) {
        List<? super Juice> juice = new ArrayList<Juice>();
        juice.add(new Lemon());
        juice.add(new Orange());
        juice.add(new Peach());
        for (Object aha : juice)
            System.out.println(aha);
    }
}

```
    Lemon.....0
    Orange.....1
    Peach.....2


编译成功了，运行也成功了。但是? super Juice好像与Juice没有区别。“！！！我在做什么，我在哪里，我是谁！！！”

#### 3. \<?\>


```
package javaS.genericity;

import java.util.ArrayList;
import java.util.List;

public class Client<T> {
    public static void main(String[] args) {
        List<?> list = new ArrayList<String>();
        list.add("afd");//报错，无论如何也add不进去，任何对象都不行。
        
    }
}

```
我决定要放弃这场逗逼之旅了。。。

### 使用泛型的注意事项
#### 1. 任何基本类型都不能作为类型参数。
> 不能创建类似ArrayList\<int\>的东西。

#### 2. 一个类不能实现同一个泛型接口的两种变体。

```
package javaS.genericity;

import algorithms.sort.Sort;
import javaS.genericity.interfaceS.Juice;

interface ONE<A>{}
class TWO implements ONE<Sort>{}
public class Client<T> extends TWO implements ONE<Juice> {
// error: The interface ONE cannot be implemented more than once -
// with different arguments: ONE<Sort> and ONE<Juice>
}

```
#### 3. \@SuppressWarning注解
用于去除泛型类型参数的转型或instanceof时发出的警告。

#### 4. 重载泛型方法时
注意不同的泛型命名最终都会被擦除成相同的占位符，所以实际类型会被打回原始类型List而跟泛型内容毫无关系，所以不要写：

```
public class Client<A,B> {
    void f(List<A> list){}//报错
    void f(List<B> list){}//报错
}
```
> 报错信息：Erasure of method f(List\<B\>) is the same as another method in type Client\<A,B\>

是不是A和B不是真的类呢？我要是换个真的类是不是能好？

```
public class Client<String, Integer> {
    void f(List<String> list){}//报错
    void f(List<Integer> list){}//报错
}

```
> 报错信息：Erasure of method f(List\<B\>) is the same as another method in type Client\<A,B\>

兄弟你想多了。

总结：只要涉及到泛型参数的方法，不能重载。

#### 5. 不能创建参数化类型的数组

```
List<Integer>[] a = new ArrayList<Integer>[10];//error:
```
error:

    - Cannot create a generic array of ArrayList\<Integer\>
    - List is a raw type. References to generic type List\<E\> should be 
     parameterized

看到这里，我突然突发奇想，想知道列表数组的写法，于是写了一段以下代码：

```
package javaS.genericity;

import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        List[] a = new ArrayList[10];
        for (int i = 0; i < 5; i++) {
            List<Integer> list = new ArrayList<Integer>();
            list.add(6);
            a[i] = list;
        }
        for (List<Integer> k : a) {
            if (k == null)
                break;
            for (int c : k) {
                System.out.println(c);
            }
        }
    }
}

```
> 6
6
6
6
6

列表数组，意思就是一个数组中每个元素就是一个List对象，但是要注意，在具体给List赋值的时候，一定要指定类型了，获取这些List中的值的时候，也应该指定类型，否则将出现类型转换错误。所以这种写法并不灵活，且具有一定的类型错误的风险。

#### 6. 泛型类的静态上下文中类型变量无效
换句话说，就是不能再静态域或方法中引用类型变量。例如：

```
 private static T t;// 报错：Cannot make a static reference to the non-static type T
```


#### 7. 哦对了，还有一个逗比王中王
> class SelfBounded\<T extends SelfBounded\<T\>\> \{\}

写下这样的代码的人为什么不去当个科学家研究研究火箭啥的（你咋不上天）？

### 总结
请忽略“提高篇”中探索泛型底层实现的内容，我们客观评价一下泛型的优点。

- 不得不承认，泛型是我们需要的程序设计手段。使用泛型机制编写的程序代码要比那些杂乱地使用Object变量，然后再进行强制类型转换的代码具有更好的安全性和可读性。泛型对于集合类尤其有用，例如，ArrayList加泛型已经是我们习以为常的写法了。

- 泛型类、泛型方法、泛型接口在实践中都是很不错的应用，我们掌握了这一层就已经脱离了只会使用ArrayList\<String\>的初级泛型程序员而进化为可以自己去实现适合自己工作的泛型了。

- 通过学习泛型，我们巩固了迭代接口Iterable，Iterator，为自己积累了像Generator，Generators以及BasicGenerator这样的实用代码。我们还练习了并不常常敲的可变数量参数，以及斐波那契数列，匿名内部类，还有Set的使用，元组的使用，复杂模型的构建。收获是满满的。

- 我们深入了解了泛型的底层原理，体会了java作者们在设计泛型时的煎熬（一般人受不了），也体会了他们对泛型的期许。这使我成熟很多。

### 所有源码均在 [Evsward github](https://github.com/evsward/mainbase/tree/master/src/javaS/genericity)

### 参考资料

- *Core Java Volume I --- Fundamentals* (Ninth Edition)
- *Thinking in Java* (Fourth Edition)