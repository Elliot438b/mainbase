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

末端哨兵的功能：链表都会设置一个哨兵，因为链表是“邻居依赖”，因此设置一个哨兵作为边界，当链表触碰到哨兵时即停止前进，否则会有溢出异常。因此单链表通常会有一个末端哨兵，而双链表则需要两头均设置一个哨兵，左哨兵和右哨兵。