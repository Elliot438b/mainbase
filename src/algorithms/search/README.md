> 首先保证这一篇分析查找算法的文章，气质与大部分搜索引擎搜索到的文章不同，主要体现在代码上面，会更加高级，结合到很多之前研究过的内容，例如设计模式，泛型等。这也与我的上一篇[面向程序员编程——精研排序算法](http://www.cnblogs.com/Evsward/p/sort.html)的气质也不尽相同。

> 关键字：查找算法，索引，泛型，二分查找树，红黑树，散列表，API设计，日志设计，测试设计，重构

> 查找是在大量的信息中寻找一个特定的信息元素，在计算机应用中，查找是常用的基本运算。

当今世纪，IT界最重要的词就是“数据！数据！数据！”，高效检索这些信息的能力是处理他们的重要前提。数据结构我们采用的是符号表，也叫索引和字典，算法就是下面将要研究的各种查找算法。

## 查找的数据结构
描述一张抽象的表格，我们会将value存入其中，然后按照指定的key来搜索并获取这些信息。符号表也叫索引，类似于书本后面列出的每个术语对应的页码（术语为key，页码为value），同时也被称为字典，类似于按照字母排序检索单词的释义（单词是key，发音释义是value）。

- 索引，数据库术语，我们在数据库中查找一张有大量记录的表时，
    - 第一种方式是全表查询，取出每一条记录依次对比，这将耗费大量数据库系统资源，占用大量磁盘I/O操作；
    - 第二种方式则是在表中建立索引，每次查询时，先到索引中检索匹配的索引值，也就是键key，找到以后直接取出对应的值value（rowid），快速取出表内记录。
    - 数据库中关于索引也有展开的内容，这里不做详细介绍。（未来如果我遇到这方面的需求，抑或是我对数据库索引莫名提起了兴趣，我会新开一篇文章来研究。）

- 下文将要介绍到实现高效符号表的三种数据类型：
    
        二分查找树、红黑树、散列表。


- 符号表是一种存储键值对的数据结构，我们将所有这些需要被检索的数据放在这个集合中。
    - 符号表的键值对为key和value，也叫键和数据元素，大部分时候键指的都是主键（primary key），有的也包含次主键（secondary key）。

    - 符号表支持两种操作：

    操作 | 释义
    ---|---
    插入（put） | 将一组新的键值对存入表中
    查找（get） | 根据给定的键得到相应的值

- 符号表的分类

    - 静态表：只做查找操作的符号表。即该符号表没有被修改增删等事务性操作，是静态的，不变的。
    - 动态表：查找时插入不存在的数据元素，或从表中删除已经存在的元素。即操作过程中，该符号表可能是变化的，是动态的。

- 符号表的基础API设计

    符号表[symbol table, 缩写ST]，也有称为查找表[search table, 缩写ST]，但是意思都是一样的，所以，之后下文出现的无论符号表、索引还是字典，指的都是同一个东西，不要混淆。
    
    public class ST<Key, Value>

return | function | 释义
---|---|---
.|ST()|构造函数，创建一个索引
void| put(Key key, Value val) | 将键值存入表中（若值为空则删除键）
Value|get(Key key) | 获取键对应的值（若key不存在返回null）
void |remove(Key key) | 从表中强制删除键值对
boolean|containsKey(Key key) | 判断键在表中是否存在对应的值
boolean |isEmpty() | 判断表是否为空
int |size() | 获得表的键值对数量
Iterable<Key> |keys() | 获得表中所有键的集合（可迭代的）

## 查找的算法分析

下面进入算法分析阶段，这次的研究我将一改以往简码的作风，我将遵循上面api的设计，完成一个在真实实践中也可用的API架构，因此除去算法本身，代码中也会有很多实用方法。

### 程序架构
利用这一次研究查找算法的机会，在研究具体算法内容之前，结合前面研究过的知识，我已经不满足于[面向程序员编程——精研排序算法](http://www.cnblogs.com/Evsward/p/sort.html)时的程序架构了（人总要学会慢慢长大...），这一次我要对系统进行重新架构。
#### 第一版
首先建一个package search，然后按照上面的API创建一个ST基类：

```
package algorithms.search;

/**
 * 符号表类
 * 
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class ST<Key, Value> {
    /**
     * ST类并不去亲自实现SFunction接口
     * 
     * @design “合成复用原则”
     * @member 保存接口实现类的对象为符号表类的成员对象
     */
    SFunction<Key, Value> sf;

    /**
     * 构造器创建符号表对象
     * 
     * @param sf
     *            指定接口的具体实现类（即各种查找算法）
     */
    public ST(SFunction<Key, Value> sf) {
        this.sf = sf;
    }

    /**
     * 采用延时删除，将key的值置为null
     * 
     * @param key
     *            将要删除的key的值
     */
    public void delete(Key key) {
        sf.put(key, null);
    }

    /**
     * 判断表内是否含有某key（value为null，key存在也算）
     * 
     * @param key
     * @return
     */
    public boolean containsKey(Key key) {
        @SuppressWarnings("rawtypes")
        List list = (ArrayList) keySet();
        list.contains(key);
        return list.contains(key);
    }

    /**
     * 判断表是否为空
     * 
     * @return
     */
    public boolean isEmpty() {
        return sf.size() == 0;
    }

    public void put(Key key, Value val) {
        sf.put(key, val);
    }

    public int size() {
        return sf.size();
    }

    public Iterable<Key> keySet() {
        return sf.keySet();
    }

    public Value get(Key key) {
        return sf.get(key);
    }

    // 即时删除
    public void remove(Key key) {
        sf.remove(key);
    }
}

```
- 针对ST类的几点说明：
1. 泛型，ST为一个泛型类，它是类型泛化的，在使用时指定具体类型。对于泛型的内容，不了解的朋友可以转到[“大师的小玩具——泛型精解”](http://www.cnblogs.com/Evsward/p/genericity.html)，查询“泛型类”相关的知识。
2. 符号表的两种删除算法
    1. 延迟删除，也就是先将键对应的值置为空，然后在某个时候删除所有值为空的键。API中对应的是delete方法。
    2. 即时删除，立刻从表中删除指定的键值对。API中对应的是remove方法。
3. 由于delete、containsKey和isEmpty方法均通过调用SFunction中的方法来实现，因此他们不必放入SFunction接口中去。
4. key不重复，我们遵循主键唯一的原则，不考虑次主键的情况。
5. 不允许key为null，不允许值为null。 
6. 既然要以key为条件去查找相应的值，就要做好等价性工作，也就是复写equals()方法。另外也可以考虑让key实现Comparable接口，除了有等价性以外，还可以比较大小。最后注意要使用不可变的数据类型作为key，以保证表的一致性。

下面，创建一个泛型接口SFunction\<Key, Value\>，用来定义查找算法必须要实现的方法，代码如下：

```
package algorithms.search;

/**
 * 查找算法的泛型接口，定义必须要实现的方法
 * 
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public interface SFunction<Key, Value> {

    public int size();// 获取表的长度

    public Value get(Key key);// 查找某key的值

    public void put(Key key, Value val);// 插入

    public Iterable<Key> keySet();// 迭代表内所有key

    public void remove(Key key);// 强制删除一个key以及它的节点
}

```
- 针对SFunction接口的几点说明：
1. 此接口为泛型接口，参数类型依然是泛化的，不了解的朋友可以转到[“大师的小玩具——泛型精解”](http://www.cnblogs.com/Evsward/p/genericity.html)，查询“泛型接口”相关的知识。
2. 此接口声明的方法为所有继承于ST类的子类必须实现的方法，每个子类有着自己不同的实现方式。

下面，我们再创建一个DemoSearch算法来实现SFunction接口。代码如下：

```
package algorithms.search;

/**
 * 查找算法接口的实现类：Demo查找
 * 
 * @notice 在实现一个泛型接口的时候，要指定参数的类型
 * @author Evsward
 *
 */
public class DemoSearch implements SFunction<String, String> {

    @Override
    public void put(String key, String val) {
        // TODO Auto-generated method stub

    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void remove(Key key) {
        // TODO Auto-generated method stub

    }
    
    @Override
    public Iterable<String> keys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String get(String key) {
         
        return "demo-test";
    }

}

```
~~（这段话已被丢弃）说明：除了必须复写的接口的方法以外，实现类必须指定参数类型代替泛型，否则报错。~~

最后再创建一个客户端，用来调用search方法：

```
package algorithms.search;

public class Client {
    public static void main(String[] args) {
        ST st;
        // error: Cannot instantiate the type SFunction
        st = new ST<String, String>(new SFunction());
        // warning: Type safety: The constructor ST(SFunction) belongs to the
        // raw type ST. References to generic type ST<Key,Value> should be
        // parameterized
        st = new ST(new DemoSearch());
        st.get("key");
    }
}

```
分析：

1. 接口本身只是方法的声明，无法创建一个接口的实例，但是它可以当做参数去传递，赋值的时候一定是它的实现类的实例。所以main方法中第二行代码报错，无法通过编译。
2. 上面那种方式不可行，我们来传入具体的实现类的实例，然而创建时如果不指定具体参数类型，会有warning出来，但是其实在算法接口的实现类DemoSearch中已经指定具体参数类型了(*class DemoSearch implements SFunction\<String, String\>*)，我们并不想在客户端调用的时候再次指定，这显得很麻烦，而且对于我们甄别其他warning的时候会产生迷惑。

####  @deprecated ~~第二版（第二版涉及实现泛型接口或者继承泛型基类需要指定具体参数类型的言论均被丢弃）~~

~~（这段话已被丢弃）由于泛型擦除的变通机制，我们无法继承一个未指定具体类型的泛型类或者实现一个未指定具体类型的泛型接口。（这段话已被丢弃）~~ 虽然我最终也没有找到超越第一版的更好的版本，但是我们再一次加强了对java泛型的理解。所以，我们在第一版的基础之上，

> 在此约定，每个算法具体实现类的泛型均被指定为\<String, String\>。

然后将算法实现类配置到配置文件中
> \<sf\>algorithms.search.DemoSearch\<\/sf\>

客户端代码改为：

```
package algorithms.search;

import tools.XMLUtil;

public class Client {
    public static void main(String[] args) {
        Object oSf = XMLUtil.getBean("sf");// 注入对象
        @SuppressWarnings("unchecked")
        ST<String, String> st = new ST<String, String>((SFunction<String, String>) oSf);
        System.out.println(st);
        System.out.println(st.get(""));
    }
}

```
> 执行结果：

    algorithms.search.ST@4e25154f
    demo-test

之后的算法实现类只需要：
1. 创建一个类，实现SFunction\<String, String\>接口，实现接口具体方法，填入自己算法内容。
2. 修改config.xml中的类名为当前实现类，客户端代码不用改，可以直接执行测试结果。

#### 关于数据结构：
我们发现上面的架构代码中并未出现具体的实现符号表的数据结构，例如数组、链表等。原因是每种算法依赖的数据结构不同，这些算法是直接操作于这些具体数据结构之上的（正如数据结构和算法水乳交融的关系），因此当有新的XXXSearch被创建的时候，它就会在类中引出自己需要的数据结构。上面被丢弃的第二版将泛型强制指定为\<String, String\>，这对系统架构中要求灵活的数据结构造成非常大的伤害，这也是第二版被丢弃的原因之一。


#### 最终架构（后面已被更改）
上面的“第二版”已被丢弃，留下的原因就是可以记录自己思考的过程，虽然没有结果，但是对设计模式，对泛型都加深了理解。那么最终架构是什么呢？由于泛型的特殊性，我们无法对其做多层继承设计，所以最终架构就是没有架构，每个算法都是一个独立的类，只能要求我自己在这些独立的算法类中去对比上面的API去依次实现。

### 顺序查找

又叫线性查找，是最基本的查找技术，遍历表中的每一条记录，根据关键字逐个比较，直到找到关键字相等的元素，如果遍历结束仍未找到，则代表不存在该关键字。
> 顺序查找不依赖表中的key是否有序，因此顺序查找中实现符号表的数据结构可以采用单链表结构，每个结点存储一个键值对，其中key是无序的。

- 码前准备：
    1. 我们要创建一个独立的实现顺序查找的符号表类SequentialSearchST。
    2. 在类中，要实现一个单链表结构。
    3. 操作单链表去具体实现基础API中的方法。

- 代码阶段：

```
package algorithms.search.second;

import java.util.ArrayList;
import java.util.List;

import algorithms.search.SFunction;

/**
 * 无序链表的顺序查找，大部分方法采用遍历链表的方式实现
 * 
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class SequentialSearchST<Key, Value> {
    private Node first;

    private class Node {
        Key key;
        Value val;
        Node next;

        public Node(Key key, Value val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    public void put(Key key, Value val) {
        // 遍历链表
        for (Node x = first; x != null; x = x.next) {
            // 找到key则替换value
            if (key.equals(x.key)) {
                x.val = val;
                return;
            }
        }
        // 如果未找到key，则新增
        first = new Node(key, val, first);
    }

    public int size() {
        int count = 0;
        for (Node x = first; x != null; x = x.next) {
            count++;
        }
        return count;
    }

    /**
     * 取出表中所有的键并存入可迭代的集合中
     * 
     * @return 可以遍历的都是Iterable类型，这里用List
     */
    public Iterable<Key> keySet() {
        List<Key> list = new ArrayList<Key>();
        for (Node x = first; x != null; x = x.next) {
            list.add(x.key);
        }
        return list;
    }

    public Value get(Key key) {
        // 遍历链表
        for (Node x = first; x != null; x = x.next) {
            // 找到key则替换value
            if (key.equals(x.key)) {
                return x.val;
            }
        }
        return null;// 未找到则返回null
    }

    /**
     * 永久删除
     * 
     * @param key
     */
    @SuppressWarnings("rawtypes")
    public void remove(Key key) {
        List list = (ArrayList) keySet();
        list.contains(key);
        if (list.contains(key)) {
            if (key.equals(first.key)) {// 删除表头结点
                first = first.next;
                return;
            }
            // 遍历链表
            for (Node x = first; x != null; x = x.next) {
                Node next = x.next;
                if (key.equals(next.key) && next.next == null) {// 删除表尾结点
                    x.next = null;
                    return;
                }
                if (key.equals(next.key)) {
                    x.next = x.next.next;// 删除表中结点
                    return;
                }
            }
        }
    }

    /**
     * delete, containKeys, isEmpty()方法均定义在ST中。
     */
}
```
请仔细观察代码，我将其中的方法名改为与Map一致，这样可以将Map作为参照物，来测试我们新写的符号表算法，下面是客户端测试代码：

```
package algorithms.search.second;

import java.util.Random;

public class Client {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SequentialSearchST<Integer, String> sst = new SequentialSearchST<Integer, String>();
        // Map<Integer, String> sst = new HashMap<Integer, String>();
        if (sst.isEmpty()) {
            sst.put(3, "fan");
            System.out.println("sst.size() = " + sst.size());
        }
        if (!sst.containsKey(17)) {
            sst.put(17, "lamp");
            System.out.println("sst.size() = " + sst.size());
        }
        System.out.println("sst.get(20) = " + sst.get(20));
        sst.put(20, "computer");
        System.out.println("sst.get(20) = " + sst.get(20));
        sst.remove(20);
        System.out.println("sst.get(345) = " + sst.get(345));
        Random rand = new Random();
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 10000; i++) {
            sst.put(rand.nextInt(), String.valueOf(abc.charAt(rand.nextInt(abc.length())))
                    + String.valueOf(abc.charAt(rand.nextInt(abc.length()))));
        }
        sst.put(123, "gg");
        sst.remove(3);
        sst.remove(123);
        sst.remove(17);
        System.out.println("-----输出集合全部内容-----");
        int a = 0;
        for (int k : sst.keySet()) {
            a++;
            sst.get(k);
        }
        int keyR = sst.keySet().iterator().next();
        System.out.println("next-key: " + keyR + " next-val: " + sst.get(keyR));
        System.out.println("0-" + a + "..." + "sst.size() = " + sst.size());
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) + "ms");
    }
}
```
客户端测试写了很多行，覆盖了我们符号表中所有的方法，同时也有大量的数据操作来测试性能。先看一下刚写的SequentialSearchST的输出：

    sst.size() = 1
    sst.size() = 2
    sst.get(20) = null
    sst.get(20) = computer
    sst.get(345) = null
    -----输出集合全部内容-----
    next-key: 1727216285 next-val: Pf
    0-10000...sst.size() = 10000
    总耗时：640ms

输出全部按预期正确，注意最后的总耗时是640ms。接下来是见证奇迹的时刻，我们将sst的对象类型改为Map，

> Map\<Integer, String\> sst = new HashMap\<Integer, String\>();

再看一下输出情况：

    sst.size() = 1
    sst.size() = 2
    sst.get(20) = null
    sst.get(20) = computer
    sst.get(345) = null
    -----输出集合全部内容-----
    next-key: 817524922 next-val: Hk
    0-10000...sst.size() = 10000
    总耗时：21ms

#### 21ms!!! 

这就是算法和数据结构的差异带来的性能差异。顺序查找因为有大量的遍历操作，并且它采用的单链表是一个内部类，每次要针对它进行操作，所以它的速度想想也不会太快。相较之下，jdk中的Map的性能提升不是一点半点，但是不用着急，后面会慢慢介绍到Map的实现方式，解答“为什么它这么快？”

### 二分查找

玩个游戏，我背着你写下一个100以内的整数，你如何用最快速的方式猜出这个数是几？这个游戏我想大家小时候都接触过，方法是先猜50，问结果比50大还是小，小的话再猜25，以此类推。这就是二分查找的主要思想。
> @deprecated ~~（这段话已被丢弃）二分查找，也称折半查找，它属于有序表查找，所以前提必须是key有序，如果将数组的下标当做key的话，数组的结构天生就是key有序，因此实现符号表的数据结构采用数组。~~

上面被丢弃的原因是我没有想到如果key只是数组的下标的话，无形中就是强制约束了key的类型只能为整型，其他类型无法被作为key，这对我们程序的限制非常大，我们用了泛型就希望他们是类型泛化的，而不是被强制成一种类型不能变动。

> 因此，我们将采用两个数组，一个数组用来存放key，一个数组用来存放value，两个数组为平行结构，通过相等的下标关联，也即实现了key-value的关联关系。

- 码前准备：
    1. 创建一个实现二分查找的符号表类BinarySearchST。
    2. 在类中创建两个数组作为存储容器，一个存储key，一个存储value。
    3. 我们需要一个动态调整数组大小的方法。
    4. 操作数组去实现API中的基础方法。

- 代码阶段：

经过上面的分析，我们发现代码阶段的第一个难点其实在于动态调整数组大小，我们都知道数组的大小在创建时就被限定，无法改变其大小，这也是为什么实际工作中我们愿意使用List来替代数组的原因之一。经过查阅资料，找到了一个动态调整数组大小的下压栈。

#### 动态调整数组栈
- 栈，首先栈的特性是LIFO，也叫下压栈，下推栈，把栈想象成一个奶瓶，无论它正放还是倒放，栈就是从瓶口往里挨个塞硬币，往外取的时候后进去的先取出来。注意top指针永远是在瓶口，永远指的是最新的元素（即下标最大的元素）的后面，压入时按照元素下标顺序来讲，top的值是越来越大的，取出时top的值是越来越小的。关于下推栈，在[大师的小玩具——泛型精解](http://www.cnblogs.com/Evsward/p/genericity.html)中搜索“下推栈”即可找到，当时我们是采用单链表泛型的方式实现的。


```
graph TB

subgraph top
el3-->el2
el2-->el1
end
```
这一次我们要实现数组的动态调整，因此采用泛型数组的方式实现下推栈。这里面要注意数组的大小一定要始终满足栈的空间，否则就会造成栈溢出，同时又要随时监控如果栈变小到一定程度，就要对数组进行减容操作，否则造成空间浪费。下面是动态调整数组栈：

```
package algorithms.search.second;

@SuppressWarnings("unchecked")
public class ResizeArrayStack<Item> {
    /**
     * 定义一个存放栈的数组
     * 
     * @注意 该数组要始终保持空间充足以供栈的使用，否则会造成栈溢出。
     */
    private Item[] target = (Item[]) new Object[1];
    private int top = 0;// 栈顶指针，永远指向最新元素的下一位

    // 判断栈是否为空
    public boolean isEmpty() {
        return top == 0;
    }

    // 返回栈的大小，如果插入一个元素，top就加1的话，当前top的值就是栈的大小。
    public int size() {
        return top;
    }

    /**
     * 调整数组大小，以适应不断变化的栈。
     * 
     * @supply 数组的大小不能预先设定过大，那会造成空间的浪费，影响程序性能
     * @param max
     */
    public void resize(int max) {
        Item[] temp = (Item[]) new Object[max];
        for (int i = 0; i < top; i++) {
            temp[i] = target[i];
        }
        target = temp;
    }

    /**
     * @step1 如果没有多余的空间，就给数组扩容
     * @step2 空间充足，不断压入新元素
     * @param i
     */
    public void push(Item i) {
        // 如果没有多余的空间，会将数组长度加倍，以支持栈充足的空间，栈永远不会溢出。
        if (top == target.length)
            resize(2 * target.length);// 扩充一倍
        target[top++] = i;// 在top位置插入新元素，然后让top向上移动一位
    }

    /**
     * 弹出一个元素，当弹出元素较多，数组空间有大约四分之三的空间空闲，则针对数组空间进行相应的减容
     * 
     * @return
     */
    public Item pop() {
        Item item = target[--top];// top是栈顶指针，没有对应对象，需要减一位为最新对象
        // 弹出的元素已被赋值给item，然而内存中弹出元素的位置还有值，但已不属于栈，需要手动置为null，以供GC收回。
        target[top] = null;
        // 如果栈空间仅为数组大小的四分之一，则给数组减容一半，这样可以始终保持数组空间使用率不低于四分之一。
        if (top > 0 && top == target.length / 4)
            resize(target.length / 2);
        return item;
    }

    public static void main(String[] args) {
        ResizeArrayStack<Client> clients = new ResizeArrayStack<Client>();
        for (int i = 0; i < 5; i++) {
            clients.push(new Client());
        }
        System.out.println("clients.size() = " + clients.size());
        Client a = clients.pop();
        System.out.println("clients.size() = " + clients.size());
        a.testST();
    }
}

```
> 输出

    clients.size() = 5
    clients.size() = 4
    sst.size() = 1
    sst.size() = 2
    sst.get(20) = null
    sst.get(20) = computer
    sst.get(345) = null
    -----输出集合全部内容-----
    next-key: 1294486824 next-val: zV
    0-10000...sst.size() = 10000
    总耗时：21ms（客户端这还是Map呢，没改）

输出正确。

#### 加入迭代
集合类数据元素的基本操作之一就是可以使用foreach语句迭代遍历并处理集合中的每个元素。加入迭代的方式就是实现Iterable接口，不了解Iterable接口与泛型联用的朋友可以转到[“大师的小玩具——泛型精解”](http://www.cnblogs.com/Evsward/p/genericity.html)，查询“Iterable”相关的知识。下面对ResizeArrayStack作一下改造，加入迭代。

这里我不完整地粘贴出代码了，只是展示变化的部分。

```
package algorithms.search.second;

import java.util.Iterator;

@SuppressWarnings("unchecked")
public class ResizeArrayStack<Item> implements Iterable<Item> {
    ...
    ...

    public static void main(String[] args) {
        ResizeArrayStack<Client> clients = new ResizeArrayStack<Client>();
        for (int i = 0; i < 5; i++) {
            clients.push(new Client());
        }
        System.out.println("clients.size() = " + clients.size());
        // a.testST();
        for (Client c : clients) {
            System.out.println(c);
        }
    }

    @Override
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            private int i = top;

            @Override
            public boolean hasNext() {
                return i > 0;
            }

            @Override
            public Item next() {
                // top 没有值，减一位为最新的值。这里是用来迭代的方法，与弹出不同，不涉及事务性（增删改）操作
                return target[--i];
            }

        };
    }
}

```
> 输出

    clients.size() = 5
    algorithms.search.second.Client@15db9742
    algorithms.search.second.Client@6d06d69c
    algorithms.search.second.Client@7852e922
    algorithms.search.second.Client@4e25154f
    algorithms.search.second.Client@70dea4e

在原ResizeArrayStack的基础上，让它实现Iterable接口，然后重写Iterator，返回一个匿名内部类，实现hasNext和next方法。注意，next方法只是用来遍历迭代的方法，与栈弹出不同，不会修改数据内容。这样一来，我们就可以方便的在客户端直接通过foreach语句迭代遍历栈内元素了。

## （**  强力插入补充）更改架构（随时重构）
在编写上面“加入迭代”的代码的时候，发现
> public class ResizeArrayStack\<Item\> implements Iterable\<Item\> { ...

- 翻回上面的架构第二版内容，我知道我理解错了，人就是不断学习去进步的，因此我也不打算删除前面的内容，留下我的思考、推翻、决定和再次推翻自己的学习过程。

- 既然可以泛型继承，它也跟擦除机制没什么关系，那么现在可以对上面第二版的架构稍作修改，然后再次启用了。（●>∀<●）

- 修改内容：

大部分修改依然重用了第二版的原始代码，这里不再重复粘贴展示，只是在SFunction接口中新加入了一个remove方法，更改了containKey方法名和keySet方法名，用来与Map的API保持一致。

### 二分查找继续

在上面的“二分查找”部分，我们了解了二分查找的分治思想，要求Key有序，确立了基于二分查找算法的符号表的数据结构：

> 二分查找的数据结构：采用一对平行可变数组（实现方式参照ResizeArrayStack），分别存储key和value。

二分查找算法的代码阶段：

```
public class BinarySearchST<Key extends Comparable<Key>, Value> implements SFunction<Key, Value> {...
```
- 思考：这里除了重用改后架构实现了SFunction以外，对泛型中Key设定了边界，让Key必须满足是可比较的，以在接下来的方法中能够对Key进行排序操作。这一个操作，让我重新理解了泛型的边界，泛型有了边界以后，就规定了类型参数不能超过这个边界，从以上代码来看，非常有用。这个打破了我在[大师的小玩具——泛型精解](http://www.cnblogs.com/Evsward/p/genericity.html)中搜索“泛型的边界”的偏见，也使我重新认识到了
    1. 泛型可继承的特性
    2. 再结合泛型边界

- java的泛型也没我想的那么难用。

```
package algorithms.search.second;

import java.util.ArrayList;
import java.util.List;

import algorithms.search.SFunction;

/**
 * 一对平行的可变长度数组，参照ResizeArrayStack
 * 
 * @author Evsward
 * @notice 二分查找为Key有序：所以Key的泛型的边界为Comparable，可比较的，否则无法判断是否有序
 * @param <Key>
 * @param <Value>
 */
public class BinarySearchST<Key extends Comparable<Key>, Value> implements SFunction<Key, Value> {

    private Key[] keys;
    private Value[] values;
    private int top;// keys顶端指针，不存值，等于数组长度的大小，keys[top-1]为最长处的元素。

    /**
     * 做一个通用类，可以自动先将数据排序后再进行二分查找
     * 
     * @target 构造函数时强制Key有序，同时Value的值也要跟随变化，保证key-value的映射。
     * 
     * @卡壳处 无法根据Key数组创建一个等长的Value数组，因为泛型数组无法被创建。
     * @error Cannot create a generic array of Value
     */
    public BinarySearchST() {
        // 必须加一个无参构造器，否则客户端测试报错
        // 通过配置文件反射创建对象没有初始化构造器参数。
        resize(1);
    }

    public BinarySearchST(int capacity) {
        if (capacity < 1)// 如果设置的不符合标准，则初始化为1
            capacity = 1;
        resize(capacity);
        // Value valueTemp[] = new Value[keyTemp.length];// error
    }

    /**
     * 同时调整Key和Value数组大小，以适应伸缩的键值对空间。
     * 
     * @supply1 数组的大小不能预先设定过大，那会造成空间的浪费，影响程序性能
     * @supply2 数组也不能设定小了，会造成键值对丢失，一定要足够键值对数据使用。
     * @param max
     */
    @SuppressWarnings("unchecked")
    public void resize(int max) {
        Key[] tempKeys = (Key[]) new Comparable[max];
        Value[] tempValues = (Value[]) new Object[max];
        for (int i = 0; i < top; i++) {
            tempKeys[i] = keys[i];
            tempValues[i] = values[i];
        }
        keys = tempKeys;
        values = tempValues;
    }

    public int size() {
        return top;
    }

    /**
     * 二分查找：在有序keys【从小到大排序】中找到key的下标
     * 
     * @notice 实际上同时也是二分排序
     * @param key
     * @return
     */
    public int getIndex(Key key) {
        int low = 0;
        int high = size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comp = key.compareTo(keys[mid]);
            if (comp > 0) // key比mid大，则只比较后一半
                low = mid + 1;
            else if (comp < 0) // key比mid小，则只比较前一半
                high = mid - 1;
            else
                return mid;
        }
        return low;// 当low=high+1的时候，依然没有找到相等的mid，就返回low【ceiling】。
    }

    /**
     * 查找到键则更新值，否则就创建一个新键值对
     */
    public void put(Key key, Value val) {
        int keyIndex = getIndex(key);
        if (keyIndex < top && keys[keyIndex].compareTo(key) == 0) {
            // 存在键下标且值相等，更新value
            values[keyIndex] = val;
            return;
        }
        // 如果数组空间不足以新增键值对元素，则扩充一倍，保证数组的利用率始终在25%以上。
        if (keys.length == size())
            resize(2 * keys.length);
        for (int j = top; j > keyIndex; j--) {
            // 插入，后面元素都往右窜一位，把keyIndex的位置空出来。
            keys[j] = keys[j - 1];
            values[j] = values[j - 1];
        }
        keys[keyIndex] = key;
        values[keyIndex] = val;
        top++;// 每次插入，指针向右移动一位。
    }

    @Override
    public Iterable<Key> keySet() {
        List<Key> list = new ArrayList<Key>();
        for (int i = 0; i < size(); i++) {
            list.add(keys[i]);
        }
        return list;
    }

    @Override
    public Value get(Key key) {
        int keyIndex = getIndex(key);
        if (keyIndex < top && key.compareTo(keys[keyIndex]) == 0) {
            // 如果keys中存在key的下标且值也相等，则返回values相同下标的值。
            return values[keyIndex];
        }
        return null;
    }

    @Override
    public void remove(Key key) {
        int keyIndex = getIndex(key);
        if (keyIndex == top - 1)// 删除表尾键值对数据
            top--;
        if (keyIndex < top - 1 && keys[keyIndex].compareTo(key) == 0) {// 删除表头或表中键值对数据
            // 存在键下标且值相等
            for (int j = keyIndex; j < top - 1; j++) {// 注意这里循环的是数组下标，最大不能超过表尾数据（上面已处理删除表尾）
                // 删除，后面元素都往左窜一位，把keyIndex的位置占上去
                keys[j] = keys[j + 1];// 循环若能够到表尾[top-1]数据，j+1溢出。
                values[j] = values[j + 1];
            }
            top--;
            // 监测：如果键值对的空间等于数组的四分之一，则将数组减容至一半，保证数组的利用率始终在25%以上。
            if (size() == keys.length / 4) {
                resize(keys.length / 2);
            }
        }
    }

    /**
     * 以下是一些除了基本符号表ST类以外的二分查找特有的方法。
     */
    public Key min() {
        return keys[0];
    }

    public Key max() {
        return keys[top - 1];// 注意这里不是key.length-1，而是top-1，为什么？自己想一想
    }

    /**
     * 返回排名为k的Key
     * 
     * @notice 排名是从1开始的，数组下标是从0开始的
     * @param k
     * @return
     */
    public Key select(int k) {
        if (k > top || k < 0)// 越界（指的是键值对数据空间越界）
            return null;
        return keys[k - 1];
    }

    /**
     * 当key不在keys中时，向上取整获得相近的keys中的元素。
     * 
     * @param key
     *            要检索的key
     * @return
     */
    public Key ceiling(Key key) {
        int i = getIndex(key);
        return keys[i];
    }

    /**
     * 操作同上，只是向下取整
     * 
     * @param key
     * @return
     */
    public Key floor(Key key) {
        int i = getIndex(key);
        return keys[i - 1];
    }

    /**
     * delete, containKeys, isEmpty()方法均定义在ST中。
     */
}

```
输出：

    algorithms.search.ST@15db9742
    sst.size() = 1
    sst.size() = 2
    sst.get(20) = null
    sst.get(20) = computer
    -----输出集合全部内容-----
    next-key: 125 next-val: Xd
    0-10000...sst.size() = 10000
    总耗时：68ms

我们还记得SequentialSearchST的总耗时是多少吗？我向上翻了一下是640ms，jdk中的Map是21ms，而我们的BinarySearchST是68ms。程序的优化在稳步前进中。下面我们来分析和总结一下二分查找算法。

#### 1. 二分查找为什么这么快？
二分查找的关键算法在于以上代码中的getIndex方法。
> getIndex():通过给定的一个key值来判断其在一个从小到大有序排列的键值对数组中的位置，如果恰好找到那个位置则返回，如果没找到，则向上取整，返回与他临近的稍大一点的key的下标。

这个方法的意义如上所说，具体实现方法采用的是二分分治的思想，每一次强制取键值对数组中间的key来与参数key比较，若参数key大，则与后一半进行比较，由于数组本身是从小到大有序排列，所以可以直接确定参数key比前一半都要大，递归执行这一操作。

#### 2. keys和top的关系？
我猜想可能有朋友不太理解以上代码中的keys、values和top的关系。keys和values只是对象数组，他们提供空间，而top指的是键值对数据的顶端指针，键值对数据的大小一定是小于等于对象数组的，由于数组在创建以后大小的不可变性，所以我们根据ResizeArrayStack的思想自己实现了resize方法来动态调整数组的大小以供键值对数据操作。可以类比为keys和values是舞台，键值对是舞台上面排排站的小朋友，而top指针则是小朋友队伍后面的那个老师，她并不是小朋友队伍的，但她永远在报数数最大的小朋友的后边。

#### 3. BinarySearchST的软肋
这里是BinarySearchST的软肋，也是二分查找的软肋，就是要求符号表本身是有序的，但是对于静态表（不需要增删改）来说，初始化时就排序是值得的，但是更多时候，表有序并不是常见情况，因此如果想让我们的BinarySearchST在数据类型泛化的基础上进一步扩大它的使用范围，我们要在BinarySearchST中实现一个排序的算法，可以参照[面向程序员编程——精研排序算法](http://www.cnblogs.com/Evsward/p/sort.html)。但是正如以上代码构造器的注释所说，同时有两个泛型数组的情况下，是很难实现的，因为
> 无法根据Key数组创建一个等长的Value数组，因为泛型数组无法被初始化创建。// Value valueTemp[] = new Value[keyTemp.length];// error

#### 4. 无心插柳柳成荫

当时我之所以想的是在构造函数里面实现排序，是因为我想初始化的时候就去把整个静态表插入进来，然后此时反思放在构造函数里面并不合适。因为构造函数只是构造了一个空的数组空间，我们还未向里面存入任何键值对数据，所以排序方法应该在插入的时候去为整个键值对数据做排序，（删除的时候不会破坏排序）。

> 《算法》也是建议在构造函数中实现符号表排序，将BinarySearchST\<Key extends Comparable\<Key\>, Value\>改为BinarySearchST\<Item\>，而Item是一个具体的键值对类，包含键值属性。

然而，我的代码可能与《算法》中的展示代码类似，但是我的思想却是与它不同，参照上面的“keys和top的关系？”，《算法》是将keys直接看作键值对，top就是数组的长度。而我是继承了ResizeArrayStack的思想，分割开了空间和数据的概念。这就使keys和values数组变为了空间的概念，而随着第一对键值对数据的插入，我们就开始了getIndex排序，二分查找的同时也在二分排序，这就是这段代码的神奇之处。也是我无心插柳柳成荫的幸事。

下面我们用客户端来测试一下，看是否支持无序插入，有序输出，以验证我以上的所有想法。首先改一改客户端测试脚本：

```
...
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 8; i++) {
            sst.put(rand.nextInt(), String.valueOf(abc.charAt(rand.nextInt(abc.length())))
                    + String.valueOf(abc.charAt(rand.nextInt(abc.length()))));
        }
...
```
- 只截取了一部分代码，让数据变短一点，这一次我们不用它来测性能。
- 然后下面去通过keySet输出所有的key，看看key是否有序。
 
```
        for (int k : sst.keySet()) {
            a++;
            System.out.println(k);
        }
```
- 最终输出结果：

        class: algorithms.search.second.BinarySearchST
        sst.size() = 1
        sst.size() = 2
        sst.get(20) = null
        sst.get(20) = computer
        -----输出集合全部内容-----
        -1750658048
        -1135712693
        -594342805
        -102767779
        3
        17
        123
        448486962
        792653902
        1785770411
        1858702680
        next-key: -1750658048 next-val: Zi
        0-11...sst.size() = 11
        总耗时：28ms

可以看到，key的输出是按照大小排列的。所以我们的BinarySearchST已具备通用性，也就是说当你用Map的时候，可以替换为BinarySearchST，一样的使用方式。不过，BinarySearchST是线程不安全的。这个以后有机会研究多线程的时候再说。

#### 其他
以上代码中我在注释里面写得非常详细，如果朋友们有任何问题，可以随时留言，我会不吝解答。再对话一下看过《大话数据结构》的朋友们，如果你们只是想体会一下查找算法的核心思想，看完那本书就OK了，但是如果你想真的建立一个算法程序的概念，请你来这里，或者读一下《算法》，我们一起交流。

## 重构系统（集成测试+日志）

#### 客户端测试

本文到这里，已经经历了一次架构重构，业务上面已经实现了SequentialSearchST和BinarySearchST两种查找算法。但是我发现在客户端测试时还处于非常低级与混乱的状态（恐怕读者们也忍了我好久），而且关键问题是我们的客户端测试脚本似乎无法完全覆盖我们的系统。

- 系统测试包括；
    - 功能测试：主要用来验证我们的方法是否满足各种业务情况，包括方法，参数，构造器等等是否能够按照我们预期那样稳定运行，这是程序完整的前提。
    - 性能测试：为了测试我们的算法是否高效。

> 系统测试是保障系统健壮性的最有利途径。

闲言少叙，我已经将客户端测试脚本改了一版：

```
package algorithms.search;

import java.util.Random;

import tools.XMLUtil;

public class Client {
    @SuppressWarnings("unchecked")
    public void testFun() {
        ST<Integer, String> sst;
        Object oSf = XMLUtil.getBean("sf");
        sst = new ST<Integer, String>((SFunction<Integer, String>) oSf);
        System.out.println("-----功能测试-----");
        if (sst.isEmpty()) {
            sst.put(3, "fan");
            System.out.println("sst.put(3, " + sst.get(3) + ") --- sst.size() = " + sst.size());
        }
        sst.put(77, "eclipse");
        sst.put(23, "idea");
        sst.put(60, "cup");
        sst.put(56, "plane");
        System.out.println("sst.put 77,23,60,56 --- sst.size() = " + sst.size());
        if (!sst.containsKey(1)) {
            sst.put(1, "lamp");
            System.out.println("sst.put(1, " + sst.get(1) + ") --- sst.size() = " + sst.size());
        }
        sst.put(20, "computer");
        System.out.println("sst.put(20, " + sst.get(20) + ") --- sst.size() = " + sst.size());
        sst.delete(20);
        System.out.println("sst.delete(20) --- sst.size() still= " + sst.size());
        System.out.println("-----①遍历当前集合【观察输出顺序】-----");
        for (int k : sst.keySet()) {
            System.out.println(k + "..." + sst.get(k));
        }
        System.out.println("-----②测试表头中尾删除-----");
        sst.remove(20);// 【有序表中删除，顺序表头删除】
        System.out.println("sst.remove(20)...【有序表中删除，顺序表头删除】");
        System.out.println("sst.get(20) = " + sst.get(20) + " --- sst.size() = " + sst.size());
        sst.remove(1);// 【有序表头删除，顺序表中删除】
        System.out.println("sst.remove(1)...【有序表头删除，顺序表中删除】");
        System.out.println("sst.get(1) = " + sst.get(1) + " --- sst.size() = " + sst.size());
        sst.remove(77);// 【有序表尾删除】，顺序表中删除
        System.out.println("sst.remove(77)...【有序表尾删除】，顺序表中删除");
        System.out.println("sst.get(77) = " + sst.get(77) + " --- sst.size() = " + sst.size());
        sst.remove(3);// 有序表中删除，【顺序表尾删除】
        System.out.println("sst.remove(3)...有序表中删除，【顺序表尾删除】");
        System.out.println("sst.get(3) = " + sst.get(3) + " --- sst.size() = " + sst.size());
        System.out.println("-----③遍历当前集合-----");
        for (int k : sst.keySet()) {
            System.out.println(k + "..." + sst.get(k));
        }

        System.out.println("-----性能测试-----");
        long start = System.currentTimeMillis();
        Random rand = new Random();
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 10000; i++) {
            sst.put(rand.nextInt(10000), String.valueOf(abc.charAt(rand.nextInt(abc.length())))
                    + String.valueOf(abc.charAt(rand.nextInt(abc.length()))));
        }
        int a = 0;
        for (int k : sst.keySet()) {
            a++;
            sst.get(k);
        }
        System.out.println("0-" + a + "..." + "sst.size() = " + sst.size());
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) + "ms");
    }

    public static void main(String[] args) {
        new Client().testFun();
    }
}

```
我会依次在config.xml中替换SequentialSearchST和BinarySearchST，观察他们的输出。

    class: algorithms.search.second.SequentialSearchST
    -----功能测试-----
    sst.put(3, fan) --- sst.size() = 1
    sst.put 77,23,60,56 --- sst.size() = 5
    sst.put(1, lamp) --- sst.size() = 6
    sst.put(20, computer) --- sst.size() = 7
    sst.delete(20) --- sst.size() still= 7
    -----①遍历当前集合【观察输出顺序】-----
    20...null
    1...lamp
    56...plane
    60...cup
    23...idea
    77...eclipse
    3...fan
    -----②测试表头中尾删除-----
    sst.remove(20)...【有序表中删除，顺序表头删除】
    sst.get(20) = null --- sst.size() = 7
    sst.remove(1)...【有序表头删除，顺序表中删除】
    sst.get(1) = null --- sst.size() = 6
    sst.remove(77)...【有序表尾删除】，顺序表中删除
    sst.get(77) = null --- sst.size() = 5
    sst.remove(3)...有序表中删除，【顺序表尾删除】
    sst.get(3) = null --- sst.size() = 4
    -----③遍历当前集合-----
    20...null
    56...plane
    60...cup
    23...idea
    -----性能测试-----
    0-6358...sst.size() = 6358
    总耗时：368ms
    
我是一个分割线

    class: algorithms.search.second.BinarySearchST
    -----功能测试-----
    sst.put(3, fan) --- sst.size() = 1
    sst.put 77,23,60,56 --- sst.size() = 5
    sst.put(1, lamp) --- sst.size() = 6
    sst.put(20, computer) --- sst.size() = 7
    sst.delete(20) --- sst.size() still= 7
    -----①遍历当前集合【观察输出顺序】-----
    1...lamp
    3...fan
    20...null
    23...idea
    56...plane
    60...cup
    77...eclipse
    -----②测试表头中尾删除-----
    sst.remove(20)...【有序表中删除，顺序表头删除】
    sst.get(20) = null --- sst.size() = 6
    sst.remove(1)...【有序表头删除，顺序表中删除】
    sst.get(1) = null --- sst.size() = 5
    sst.remove(77)...【有序表尾删除】，顺序表中删除
    sst.get(77) = null --- sst.size() = 4
    sst.remove(3)...有序表中删除，【顺序表尾删除】
    sst.get(3) = null --- sst.size() = 3
    -----③遍历当前集合-----
    23...idea
    56...plane
    60...cup
    -----性能测试-----
    0-6310...sst.size() = 6310
    总耗时：48ms

总结：
这个版本经过我多次调试，可用性已经很高，代码中覆盖了多个测试用例，具体我不在这里详细列出。依然可以观察到他们的运行速度，

> 顺序查找是368ms，二分查找是48ms。

这里由于我测试的机器不同，以及每次测试脚本的更改，这个时间的数值可能不同，但是我们只要将他们结对对比，结果依然是有参考意义的。

#### 集成日志系统

以上代码虽完整，但是每次输出均只能从控制台复制出来，无法统一管理，而且代码中充斥大量的syso显得特别混乱，日志级别也没有得到有效控制，因此要继承日志系统。

目前主流java的日志系统是jcl+log4j。
- jcl是Commons-logging，由apache提供的日志接口类工具。
- log4j是一套日志解决方案，也是apache提供的日志实现类工具。

经过我的调查，log4j已经除了最新版的2.9.1，而且log4j2.x貌似已经完全集成了接口和实现，这样一来，我们可以尝试只采用最新的log4j2.9.1架构我们自己的日志系统。

- 首先去官网下载最新的apache-log4j-2.9.1-bin.zip。解压缩出来一大堆的包，根据api我写了一个helloworld，需要引入：

    
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger; 
- 经过尝试，发现引入log4j-api-2.9.1.jar可以成功导入这两个包，然而在运行时报错：
> ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...

- 我又引入了log4j-core-2.9.1.jar，再次运行继续报错：
> ERROR StatusLogger No log4j2 configuration file found. Using default configuration: logging only errors to the console. Set system property 'log4j2.debug' to show Log4j2 internal initialization logging.

- 添加log4j2的配置文件:


```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <File name="LogFile" fileName="output.log" bufferSize="1"
            advertiseURI="./" advertise="true">
            <PatternLayout
                pattern="%d{YYYY/MM/dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n" />
        </File>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout
                pattern="%d{HH:mm:ss} %-5level %logger{36} - %msg%n" />
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console" />
            <AppenderRef ref="LogFile" />
        </Root>
    </Loggers>
</Configuration>
```
根据我的想法，以上配置描述了：
- 控制台输出时间加具体信息。
- 日志输出到工程根目录output.log文件里，输出规则改为具体的日期加时间。
- 日志级别均为info。

然后新创建一个客户端VIPClient，继承进去日志系统，只需要将原来的syso替换为logger.info即可。替换完以后进行测试输出：

    class: algorithms.search.second.BinarySearchST
    14:44:20 INFO  algorithms.search.VIPClient - -----功能测试-----
    14:44:20 INFO  algorithms.search.VIPClient - sst.put(3, fan) --- sst.size() = 1
    ...
    ...
此时再去工程根目录下查看output.log:

    2017/11/09 14:44:20 [main] INFO  algorithms.search.VIPClient - -----功能测试-----
    2017/11/09 14:44:20 [main] INFO  algorithms.search.VIPClient - sst.put(3, fan) --- sst.size() = 1
    2017/11/09 14:44:20 [main] INFO  algorithms.search.VIPClient - sst.put 77,23,60,56 --- sst.size() = 5
    ...
    ...

集成日志成功。

#### 集成单元测试
我们集成一个简单的单元测试，不再使用main方法去测试了。很简单，只要把main方法删除，新增一个test方法：

```
    @Test(timeout = 1000)
    public void test() {
        new VIPClient().testFun();
    }
```
增加注解Test，同时设定timeout的时间为1s，也就是说我们最长允许系统阻塞pending时间为1s。

之后测试，随时在JUnit界面点击rerun test即可触发单元测试，不必再进入main方法界面去右键点击运行了，再加上我们上面集成的日志备份系统，非常方便。

接下来的开发流程只需要：
- 创建一个实现SFunction接口的算法实现类，实现接口方法。
- 修改config.xml的sf值为当前实现类。
- 点击Junit界面rerun test，查看控制台输出。
- 需要的话，可以去根目录下查看output.log历史日志备份。

✧ (≖ ‿ ≖)✧

### 二叉查找树

上面介绍了使用单链表实现的顺序查找和使用数组实现的二分查找。他们各有所侧重，顺序查找在插入上非常灵活，而二分查找在查询上非常高效，换句话说，一个适合静态表，一个适合动态表。但是顺序查找在查询时要使用大量遍历，二分查找在插入时也有大量操作，这都是他们各自的劣势。这一部分介绍的二叉查找树，是结合了以上两种查找方式的优势，同时又最大化地避开了他们的劣势。

> 二叉查找树，是计算机科学中最重要的算法之一。

顾名思义，这个算法依赖的数据结构不是链表也不是数组，而是二叉树。每个结点含有左右两个链接（也就是左右子结点），链接可以指向空或者其他结点。同时每个结点还存储了一个键值对。

> 定义：一棵二叉查找树（BST, short of "Binary Search Tree"），是一棵二叉树，其中每个结点都含有一个Comparable的键（以及相关联的值）且每个结点的键都大于其左子树中的任意节点的键而小于右子树的任意结点的键。

- 二叉查找树的画法：键会标在结点上，对应的值写在旁边，除空结点（空结点没有结点）只表示为向下的一条线段以外，每个结点的链接都指向它下方的结点。

- 码前准备
    - 创建一个类型泛化的二叉查找树BST，实现SFunction接口。
    - 遵循以上画法的约定，创建一个内部树结点类。
    - 实现SFunction的方法。


## 重构：添加一个针对有序符号表API的扩展接口

```
package algorithms.search;

/**
 * 查找算法的泛型接口，定义必须要实现的方法
 * 
 * @notice 针对有序列表的扩展接口
 * @author Evsward
 * @param <Key>
 * @param <Value>
 */
public interface SFunctionSorted<Key, Value> {

    /**
     * 获取最小键
     * 
     * @return
     */
    public Key min();

    /**
     * 获取最大键
     * 
     * @return
     */
    public Key max();

    /**
     * 获取k位置的键【一般指有序列表中】
     * 
     * @return
     */
    public Key select(int k);

    /**
     * 获取键对应符号表中向上取整的键
     * 
     * @return
     */
    public Key ceiling(Key key);

    /**
     * 获取键对应符号表中向下取整的键
     * 
     * @return
     */
    public Key floor(Key key);

    /**
     * 重合这部分方法，用来测试
     */
    public void put(Key key, Value val);// 插入

    public Iterable<Key> keySet();// 迭代表内所有key

    public Value get(Key key);// 查找某key的值
}


```
#### SFunctionSorted接口说明：
- 该接口的方法仅为有序表实现，例如二分查找，而表无序的顺序查找不可实现该接口。
- 由于java一个类可以实现多个接口，改造BinarySearchST让其在实现SFunction的基础上再实现SFunctionSorted接口。
- 我们可以看到，SFunctionSorted接口和SFunction接口有重合的方法，这些重合的方法是为了测试方便。

#### 测试支持
接下来为实现了SFunctionSorted接口的类添加测试。我们将VIPClient改造了一下，去掉了main方法，直接在方法名上执行测试，增加了一个新测试方法。

```
package algorithms.search;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import tools.XMLUtil;

public class VIPClient {
    private static final Logger logger = LogManager.getLogger();

    @SuppressWarnings("unchecked")
    @Test
    public void testST() {... // 代码同上，这里不重复粘贴。
    }

    @SuppressWarnings("unchecked")
    @Test
    /**
     * 由于有序符号表实现了两个接口，SFunction相关的通过testST可以测试，这里仅测试与SFunctionSorted的方法。
     */
    public void testSST() {
        SST<Integer, String> sst;
        Object oSf = XMLUtil.getBean("ssf");
        sst = new SST<Integer, String>((SFunctionSorted<Integer, String>) oSf);
        sst.put(3, "fan");
        sst.put(77, "eclipse");
        sst.put(23, "idea");
        sst.put(60, "cup");
        sst.put(56, "plane");
        sst.put(1, "lamp");
        sst.put(20, "computer");
        logger.info("-----①遍历当前集合【观察输出顺序】-----");
        for (int k : sst.keySet()) {
            logger.info(k + "..." + sst.get(k));
        }
        logger.info("-----②有序表特有功能测试-----");
        logger.info("sst.ceiling(59) = " + sst.ceiling(59));
        logger.info("sst.floor(59) = " + sst.floor(59));
        logger.info("sst.min() = " + sst.min());
        logger.info("sst.max() = " + sst.max());
        logger.info("sst.select(3) = " + sst.select(3));
    }
}

```
同时，为config.xml增加字段：

```
    <sf>algorithms.search.second.BinarySearchST</sf>
    <ssf>algorithms.search.second.BinarySearchST</ssf>
```
我也对log格式进行了调整，增加了方法名，以便于区分以上两个测试方法。

举例说明：
- 如果要测试无序表顺序查找SequentialSearchST，将config.xml中sf值改为顺序查找的类名，然后在testST方法上执行Junit测试。
- 如果要测试有序表二分查找BinarySearchST的符号表基础API方法，依然先改config，然后在testST方法上执行。
- 如果要测试有序表的有序特有的方法，则需要在config中修改ssf的值，如上面引用所示，然后在testSST方法上执行Junit测试。


#### 加入断言
如果每一次都要查看日志，一行行比对方法输出结果信息的话，那实在很费力，相同的事情做多遍，为了快速验证我们的代码是否通过测试，可以引入断言机制。

```
assertTrue(sst.ceiling(59) == 60);
```
就像这样，但是要求VIPClient导入对应的包

```
import static org.junit.Assert.*;
```
然后就可以正常使用了，我们只要在输出的每一个位置，设定好预计输出的正确的值交给断言去判断，如果错误会中断测试。在testST和testSST的方法体结尾处添加一行

```
logger.info("测试成功！");
```
每次执行Junit，只要看结尾是否有“测试成功”的字样即可，看到了就代表测试通过，不比再依次比对输出结果。如果看不到，则可以去看是哪一行断言出了问题，也能准确定位错误的位置。



#### 批量化测试
我发现，每一次去修改config.xml中的类名，以此来决定执行的是哪一个实现类，这样非常麻烦，况且又新增了有序表的实现类配置。能否每次只是增加一个新的实现类，一次配置，不用修改。因此有了：
- config.xml

```
<?xml version="1.0"?>
<config>
    <!-- 符号表实现类 SequentialSearchST BinarySearchST BST -->
    <sf1>algorithms.search.second.SequentialSearchST</sf1>
    <sf2>algorithms.search.second.BinarySearchST</sf2>
    <sf3>algorithms.search.second.BST</sf3>
    <!-- 有序符号表实现类 BinarySearchST BST -->
    <ssf1>algorithms.search.second.BinarySearchST</ssf1>
    <ssf2>algorithms.search.second.BST</ssf2>
</config> 
```
每一次新增一个实现类只要在config.xml中按照需要添加到相应的位置。

- XMLUtil
改造getBean，将tagName封装为参数
- VIPClient
    - 封装testST和testSST方法，将tagName封装为参数
    - 建立批量测试方法，testSTBatch和testSSTBatch，调用testST和testSST方法并输入参数，并将@Test注释移到这两个方法上面。

- log4j2.xml
由于批量化测试，再输出那么多debu内容在控制台就很没意义了，于是将原来的info输出全部改为debug，控制台只输出info级别，而备份日志文件输出debug级别，这样一来当你要调试的时候，可以去查日志文件，同时这些日志也能被有效保存下来，而控制台日志方面，只需要在两个Batch方法内加入info级别的日志即可。

```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <File name="LogFile" fileName="output.log" bufferSize="1"
            advertiseURI="./" advertise="true">
            <ThresholdFilter level="debug" onMatch="ACCEPT"
                onMismatch="DENY" />
            <PatternLayout
                pattern="%d{YYYY/MM/dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n" />
        </File>
        <!--这个会打印出所有的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档 -->
        <RollingFile name="RollingFile" fileName="logs/mainbase.log"
            filePattern="log/$${date:yyyy-MM}/app-%d{MM-dd-yyyy}-%i.log.gz">
            <PatternLayout
                pattern="%d{yyyy-MM-dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n" />
            <SizeBasedTriggeringPolicy size="1MB" />
        </RollingFile>
        <Console name="Console" target="SYSTEM_OUT">
            <ThresholdFilter level="info" onMatch="ACCEPT"
                onMismatch="DENY" />
            <PatternLayout pattern="%d{HH:mm:ss}[%M]: %msg%n" />
        </Console>
    </Appenders>
    <Loggers>
        <Root level="debug">
            <AppenderRef ref="LogFile" />
            <AppenderRef ref="Console" />
        </Root>
    </Loggers>
</Configuration>
```
以后每次测试只需要：
- 执行testSTBatch Junit，观察控制台输出，是否有“批量测试成功”字样，如果有则通过测试，没有则具体查看日志输出文件，再去调试。
- 执行testSStBatch Junit，流程同上。


### 二叉查找树继续

- 代码阶段


```
package algorithms.search.second;

import java.util.LinkedList;

import algorithms.search.SFunction;
import algorithms.search.SFunctionSorted;

/**
 * 二叉查找树，默认都是从小到大，从左到右排序
 * 
 * @notice 二叉查找树将用到大量递归，每个公有方法都对应着一个用来递归的私有方法
 * @see 每棵树由其根结点代表
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class BST<Key extends Comparable<Key>, Value> implements SFunction<Key, Value>, SFunctionSorted<Key, Value> {
    private TreeNode root;// 定义一个根节点，代表了整个BST。

    private class TreeNode {
        private Key key;
        private Value value;
        private TreeNode leftChild;// 左链接：小于该结点的所有键组成的二叉查找树
        private TreeNode rightChild;// 右链接：大于该结点的所有键组成的二叉查找树
        private int size;// 以该结点为根的子树的结点总数

        // 构造函数创建一个根节点，不包含左子右子。
        public TreeNode(Key key, Value value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(TreeNode node) {
        if (node == null)
            return 0;
        return node.size;
    }

    @Override
    public void put(Key key, Value val) {
        // 注意将内存root的对象作为接收，否则对root并没有做实际修改。
        root = put(root, key, val);
    }

    /**
     * 递归函数：向根节点为x的树中插入key-value
     * 
     * @理解递归 把递归当作一个黑盒方法，而不要跳进这个里边
     * @核心算法
     * @param x
     * @param key
     * @param val
     * @return 插入key-value的新树
     */
    private TreeNode put(TreeNode x, Key key, Value val) {
        if (x == null)// 若x为空，则新建一个key-value结点。
            return new TreeNode(key, val, 1);// 初始化长度只为1。
        int comp = key.compareTo(x.key);
        if (comp < 0)
            x.leftChild = put(x.leftChild, key, val);
        else if (comp > 0)
            x.rightChild = put(x.rightChild, key, val);
        else if (comp == 0)// 如果树x已有key，则更新val值。
            x.value = val;
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    @Override
    public Iterable<Key> keySet() {
        return keySet(root);
    }

    private Iterable<Key> keySet(TreeNode x) {
        // TODO
        if (x == null)
            return null;
        LinkedList<Key> list = new LinkedList<Key>();
        Iterable<Key> leftKeySet = keySet(x.leftChild);
        Iterable<Key> rightKeySet = keySet(x.rightChild);
        if (leftKeySet != null) {
            for (Key k : leftKeySet) {// 按照顺序，先add左边小的
                list.add(k);
            }
        }
        list.add(x.key);// 按照顺序，再add中间的根节点
        if (rightKeySet != null) {
            for (Key k : rightKeySet) {// 按照顺序，最后add右边大的
                list.add(k);
            }
        }
        return list;
    }

    @Override
    public Value get(Key key) {
        return get(root, key);
    }

    /**
     * 设置递归方法：在结点node中查找条件key
     * 
     * @核心算法
     * @param node
     * @param key
     * @return
     */
    private Value get(TreeNode node, Key key) {
        if (node == null)
            return null;// 递归调用最终node为空，未命中
        int comp = key.compareTo(node.key);
        if (comp < 0)
            return get(node.leftChild, key);
        else if (comp > 0)
            return get(node.rightChild, key);
        return node.value;// 递归调用最终命中
    }

    @Override
    public void remove(Key key) {
        // 注意将内存root的对象作为接收，否则对root并没有做实际修改。
        root = remove(root, key);
    }

    /**
     * 强制删除树x中key的键值对
     * 
     * @param x
     * @param key
     * @return
     */
    private TreeNode remove(TreeNode x, Key key) {
        if (x == null)// 若x为空，返回空
            return null;
        int comp = key.compareTo(x.key);
        if (comp < 0)
            // 从左子树中删除key并返回删除后的左子树
            // 这里不能直接返回，要执行下面的size重置。
            x.leftChild = remove(x.leftChild, key);
        else if (comp > 0)
            // 从右子树中删除key并返回删除后的右子树
            // 这里不能直接返回，要执行下面的size重置。
            x.rightChild = remove(x.rightChild, key);
        else {// 命中，删除
            if (x.leftChild == null && x.rightChild == null)// 说明树x只有一个结点
                return null;
            else if (x.rightChild == null)// 表尾删除
                x = x.leftChild;
            else if (x.leftChild == null)// 表头删除
                x = x.rightChild;
            else {// 表中删除，越过根节点x，重构二叉树
                  // 这是二叉树，不是数组，x.rightChild是右子树的根结点
                  // 要找出右子树的最小结点需要调用方法min(TreeNode x)
                TreeNode t = x;
                x = min(t.rightChild);// x置为右子树中的最小值，替换待删除x
                x.rightChild = deleteMin(t.rightChild);// 右子树为删除最小值（即当前x）以后的树
                x.leftChild = t.leftChild;// 左子树均不变
            }
        }
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    public void deleteMin() {
        // 注意将内存root的对象作为接收，否则对root并没有做实际修改。
        root = deleteMin(root);
    }

    /**
     * 删除树x中最小的键对应的键值对
     * 
     * @param x
     * @return 删除以后的树
     */
    private TreeNode deleteMin(TreeNode x) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;
        if (x.leftChild == null)
            return x.rightChild;// 越过x，返回x.rightChild。x被删除
        x.leftChild = deleteMin(x.leftChild);// 否则在左子树中继续查找
        // ******注意处理size的问题，不要忘记******
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    public void deletMax() {
        // 注意将内存root的对象作为接收，否则对root并没有做实际修改。
        root = deleteMax(root);
    }

    /**
     * 删除树x中最大的键对应的键值对
     * 
     * @param x
     * @return 删除以后的树
     */
    private TreeNode deleteMax(TreeNode x) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;
        if (x.rightChild == null)
            return x.leftChild;// 越过x，返回x.leftChild。x被删除
        x.rightChild = deleteMax(x.rightChild);// 否则在右子树中继续查找
        // ******注意处理size的问题，不要忘记******
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    /**
     * 实现针对有序列表的扩展接口的方法。
     */

    private TreeNode min(TreeNode x) {
        // 当结点没有左结点的时候，它就是最小的
        if (x.leftChild == null)
            return x;
        return min(x.leftChild);
    }

    @Override
    public Key min() {
        return min(root).key;
    }

    private TreeNode max(TreeNode x) {
        // 当结点没有右结点的时候，它就是最小的
        if (x.rightChild == null)
            return x;
        return max(x.rightChild);
    }

    @Override
    public Key max() {
        return max(root).key;
    }

    @Override
    public Key select(int k) {
        // int a = 0;
        // for (Key key : keySet()) {
        // if (a++ == k)
        // return key;
        // }
        // return null;
        return selectNode(root, k).key;
    }

    /**
     * 获取在树x中排名为t的结点
     * 
     * @notice 位置是从0开始，排名是从1开始。
     * @param x
     * @param t
     * @return
     */
    private TreeNode selectNode(TreeNode x, int t) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;
        if (x.leftChild.size > t && t > 0)// 左子树的size大于t，则继续在左子树中找
            return selectNode(x.leftChild, t);
        else if (x.leftChild.size == t)// 左子树的size与t相等，说明左子树根就是排名t的结点
            return x.leftChild;
        else if (x.leftChild.size < t && t < x.size)// t比左子树的size大，且小于根节点的总size
            // 其实就是rightChild的范围，在右子树中寻找，排名为右子树中的排名，所以要减去左子树的size
            return selectNode(x.rightChild, t - x.leftChild.size - 1);// -1是因为要减去根结点
        else if (t == x.size)// 排名恰好等于结点的总size，说明排名为t的结点为最大结点，即有序表中的最后结点
            return max(x);
        else// 其他情况为t越界，返回null
            return null;
    }

    public int getRank(Key key) {
        return getRank(root, key);
    }

    /**
     * 获取key在树x中的排名，即位置+1，位置是从0开始，排名是从1开始。
     * 
     * @param x
     * @param key
     * @return
     */
    public int getRank(TreeNode x, Key key) {
        if (x == null)// 一定要先判断null，避免空值异常
            return 0;
        int comp = key.compareTo(x.key);
        if (comp > 0)
            return getRank(x.rightChild, key) + x.leftChild.size + 1;
        else if (comp < 0)
            return getRank(x.leftChild, key);
        else
            return x.leftChild.size;
    }

    @Override
    public Key ceiling(Key key) {
        TreeNode x = ceiling(root, key);// 最终也没找到比它大的，这个key放在表里面是最大的
        if (x == null)
            return null;
        return x.key;
    }

    /**
     * 向上取整，寻找与key相邻但比它大的key
     * 
     * @param x
     * @param key
     * @return
     */
    private TreeNode ceiling(TreeNode x, Key key) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;// 递归调用最终node为空，未找到符合条件的key
        int comp = key.compareTo(x.key);
        if (comp == 0)// 相等即返回，不必取整
            return x;
        else if (comp > 0)// 比根节点大，则ceiling一定继续在右子树里面找
            return ceiling(x.rightChild, key);
        else if (comp < 0) {// 比根节点小，则在左子树中尝试寻找比它大的结点作为ceiling
            TreeNode a = ceiling(x.leftChild, key);
            if (a != null)// 找到了就返回
                return a;
        }
        return x;// 没找到就说明只有根节点比它大，则返回根节点
    }

    @Override
    public Key floor(Key key) {
        TreeNode x = floor(root, key);
        if (x == null)// 最终也没找到比它小的，这个key放在表里面是最小的
            return null;
        return x.key;
    }

    /**
     * 向下取整，寻找与key相邻但比它小的key
     * 
     * @param x
     * @param key
     * @return
     */
    private TreeNode floor(TreeNode x, Key key) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;// 递归调用最终node为空，未找到符合条件的值
        int comp = key.compareTo(x.key);
        if (comp == 0)// 若相等，则没必要取整，直接返回即可
            return x;
        else if (comp < 0)// 如果比根节点小，说明floor一定在左子树
            return floor(x.leftChild, key);
        else if (comp > 0) {// 如果大于根节点
            TreeNode a = floor(x.rightChild, key);// 先查找右子树中是否有比它小的值floor
            if (a != null)// 找到了则返回
                return a;
        }
        return x;// 否则，最终只有根节店key比它小，作为floor返回。
    }

}

```
在config.xml中的sf和ssf分别加入BST。然后执行两个批量测试，输出结果为：


    14:48:45[testSTBatch]: ------开始批量测试------
    14:48:45[getBean]: class: algorithms.search.second.SequentialSearchST
    14:48:46[testST]: 总耗时：357ms
    14:48:46[getBean]: class: algorithms.search.second.BinarySearchST
    14:48:46[testST]: 总耗时：40ms
    14:48:46[getBean]: class: algorithms.search.second.BST
    14:48:46[testST]: 总耗时：20ms
    14:48:46[testSTBatch]: ------批量测试成功！------
    
我是有序表测试的分割线


    14:49:11[testSSTBatch]: ------开始批量测试------
    14:49:11[getBean]: class: algorithms.search.second.BinarySearchST
    14:49:11[getBean]: class: algorithms.search.second.BST
    14:49:11[testSSTBatch]: ------批量测试成功！------


通过结果可以看出，我们新增的BST已经通过了符号表基础API以及有序符号表API的功能测试，而在性能测试方面，BST遥遥领先为20ms，二分查找为40ms，而顺序查找为357ms。关于二分查找树的个方法的具体实现，我在代码中已经有了详细的注释，如有任何问题，欢迎随时留言，一起讨论。

### 红黑树

二叉查找树只是要求二叉树的左子树所有结点必须小于根结点，同时右子树所有结点必须大于根结点。它可以是普通二叉树，可以是完全二叉树，也可以是满二叉树，并不限制二叉树的结构。殊不知，相同的由二叉查找树实现的符号表，结构不同，效率也不同，二叉查找树层数越多，比较次数也就越多，所以二叉查找树是不稳定的，最坏情况可能效率并不高。

> 理想情况下，我们希望在二叉查找树的基础上，保证在一棵含有N个几点的数中，树高为log2N，是完全二叉树的结构。

但是在动态表中，不断维持完美二叉树的代价会很高，我们希望找到一种结构能够在尽可能减小这个代价的前提下保证实现符号表API的操作均能在对数时间内完成的数据结构。

> 注意：这种结构是树，可以不是二叉树。

#### 2-3查找树

学习一种结构，它也是一个树，但是它包含两种结点类型：2-结点和3-结点。

- 2-结点：与二叉查找树相似，它有一个键，同时有两个子结点链接，左子树的所有结点必须小于根结点，右子树所有结点必须大于根结点。
- 3-结点：根结点含有两个键，同时它有三个子结点链接，左子树的所有结点必须小于根结点，中子树的所有结点必须在根结点的两个键之间，右子树的所有结点必须大于根结点。
- 结点为空的为空链接。

这种结构叫做2-3查找树，内部包含2-结点和3-结点混搭。如下图所示：


![image](https://github.com/evsward/mainbase/blob/master/resource/image/search/2-3tree.png?raw=true)

> 一种完美平衡的2-3查找树中的所有空链接到根结点的距离都应该是相同的。以下所有2-3查找树均指的是这种完美平衡的2-3查找树。

- 问：为什么是2-3查找树？
我们的目标是让所有空链接到根结点的距离相同，那么就不能有多余的单个或几个不满的结点被挤落到下一层中。2-3查找树能够实现这一目标的核心所在就是他们之间的相互转化，不仅是同一层的2结点和3结点之间的互换，同时也可以子结点与父结点进行互换，通过这种转换，能够始终保持2-3查找树是一个类似与“满二叉树”（被填得饱满）的样子。

> 想出这种结构的人真是大师，为了时刻保持数据能够化为“满树”，这种方式真的很巧妙。

- 介绍一下2-3结点的转换
    - 首先2结点转换为3结点：
        - 找到（插入操作）大于当前根结点，同时小于右子结点的键，放入当前根结点，然后建立一个中子树，放入在当前根结点中两个键之间的键。
        - 找到（插入操作）大于左子结点，同时小于当前根结点的键，放入当前根结点，然后建立一个中子树，放入在当前根结点中两个键之间的键。
    - 3结点转换为2结点：
        - 3结点的根结点中含有两个键，把他们分解为左右两个2结点的根结点的键。
        - 原来3结点的左子结点不变，为左2结点的左子结点。
        - 原来3结点的右子结点不变，为右2结点的右子结点。
        - 原来3结点的中子结点有两种情况：
            - 该中子结点为2结点，则将该结点设为左2结点的右子结点，同时找到（插入操作）一个大于该结点并小于右2结点的键作为右2结点的左子结点；
            - 该中介结点为3结点，则将该结点的左键分给左2结点作右子结点，将右键分给右2结点作左子结点。
    - 3结点的“满树”如何增加元素？这就需要将新元素加到一个3结点，将其变为4结点，然后按照上面3结点转为两个2结点的方式将4结点转为3个2结点。

- 以上这些操作应该可以覆盖2-3查找树在实现符号表API过程中的所有操作了

- 注意：无论2-3树如何操作，2结点与3结点如何转换，都不会破坏2-3树的“满树”形态，都不会影响2-3树的全局有序性

- 2-3树的有序性：左子树所有结点均小于根结点，右子树所有结点均大于根节点，中子树如果有的话，必须在其根结点的2个键之间。
- 2-3树的平衡性：全树的任意空链接到根结点的距离都是相等的。

#### 红黑二叉查找树
根据上面介绍的巧妙的2-3查找树，我们要在程序中找到一种可行的数据结构来实现它，这种数据结构就是红黑二叉查找树。

- 转换2-3查找树为红黑树：基本思想是用标准的二叉查找树（完全由2-结点构成）和一些额外的信息（替换3-结点）来表示2-3树。
- 红黑树的两种链接：
    - 红链接（约定为左斜）：
        - 正面思维是从2-3树角度来讲，将一个3-结点的根结点的左右2个键用一条红链接连起来，调整一下层次结构，让原中子数跟随左键作右子树也好，跟随右键作左子树也行，构成一个红黑树。
        - 逆向思维也就是从红黑树角度来讲，是将2个2结点通过一条红链接连接他们的根结点为一个3结点的根结点的两个键，调整一下结构，原左键的右子树和右键的左子树可以合并非给3-结点的中子树，这样就构成了一个3-结点。
    - 黑链接：2-3树中的普通链接。
    
- 通过以上介绍的红黑树和2-3树的转化，优点是我们可以直接针对2-3树结构使用二叉查找树的所有方法。

- 红黑树的完整定义：
    - 红链接均为左链接（左斜）；
    - 没有任何一个结点同时和两条红链接相连；
    - 该树是完美黑色平衡的，即任意空链接到根结点的路径上的黑链接数量相同。（原因是黑链接压根就没有改动，2-3树本身的特性就是空链接到根结点距离相同）

- 红黑树就是二叉查找树和2-3树的纽带。这让我们可以结合双方优势：既有二叉查找树简洁高效的查找方法，又有2-3树中高效的平衡插入算法。

#### 红黑树结构的码前准备：

- 设置一个color的布尔变量用来表示链接颜色，true为红色，false为黑色，约定所有空链接为黑色，为了代码清晰，我们设定两个final静态的布尔RED和BLACK作为color变量的值。
- 设置一个私有方法isRed，用来判断一个结点与他的父结点之间的颜色。
- 红黑树中，当我们提到一个结点的颜色的时候，代表的就是其父结点指向该结的链接的颜色。

#### 红黑树实现基础API：
由于红黑树属于二叉查找树，所以符号表相关API不必重复写，可以复用二叉查找树的方法即可。而我们要做的是用代码实现红黑树与2-3树的转换。

- 红黑树的旋转操作（保证红黑树完整定义的基本操作）：
    - 左旋转（rotateLeft）：传入一条指向红黑树中某结点的链接，假设该链接为右红链接，则调整该树，并返回一个指向包含同一组键的子树且其左链接为红色的根结点的链接。
    - 右旋转（rotateRight）：传入一条指向红黑树中某结点的链接，假设该链接为左红链接，则调整该树，并返回一个指向包含同一组键的子树且其右链接为红色的根结点的链接。
    - 在插入新的键时，我们可以使用旋转操作保证2-3树和红黑树之间的一一对应关系，因为旋转操作可以保持红黑树的两个重要性质：有序性和完美平衡性。
    - 在插入新的键时，该新键一定是红链接进来。
- 保证没有右红链接（保证红黑树完整定义的第一条）

    当在一个2-结点插入一个新键大于老键，这个新键必然是一个右红链接，这时候需要使用上面的左旋转（rotateLeft）将其调整过来。

问：为什么插入新的键，一定是红链接？
答：根据红黑树的完整定义，任意空链接到根结点的路径上的黑链接数量相同。如果我们插入的新键不采用红链接而是黑链接，那么必然导致新键为根结点的空链接到根节点的路径上的黑链接数量增加了一个，就不能保持完美黑色平衡了。因此只有新键采用红链接，才不会打破这个完美黑色平衡。

- 保证不存在两条连续的红链接（保证红黑树完整定义的第二条）

    当在一个3-结点（一个根结点，一条左红链接指向其左子结点）插入一个新键时，必然会出现一个结点同时和两条红链接相连的情况。
    - 新键最大，右红链接指向新键，当前【根节点】同时和两条红链接相连，此时，将这两条红链接的颜色变为黑。此时等于所有空链接到根节点的路径上的黑链接数量都增加了1，这时要将当前根结点的链接颜色由黑变红（下面会仔细分析），等于路径数量减去1，与前面抵消，最终还是保证了树的黑色平衡性。。
    - 新键最小，【左子结点】通过红链接指向一个新键，左子结点同时和两条红链接相连，此时，将左子结点右旋转（rotateRight），改其左链接为右链接，调整树结构，此时原左子结点成为新树的根结点，然后与上面操作相同，将从其出发指向两个子结点的红链接改为黑链接即可。
    - 新键置于中间，左子结点发出一条右红链接指向新键，左旋转（rotateLeft）以后，将新键变为左子结点，原左子结点变为其左子结点。然后与上面操作相同，将当前左子结点右旋转（rotateRight），剩余步骤与上面操作相同。
    
    上面三种操作都用到了将两条红链接变黑，父结点的黑链接变红的操作，我们将其封装为一个方法flipColor来负责这个操作。所以上面三种情况变成了：
    - 新键最大，flipColor
    - 新键最小，rotateRight->flipColor
    - 新键在中间，rotateLeft->rotateRight->flipColor

- 红链接的向上传递

    flipColor的操作中根结点会由黑变红，2-3树的说法，相当于把根结点送入了父结点，这意味着在父结点新插入了一个键，如果父结点是红链接（即3-结点），那么仍需要按照在3-结点中插入新键的方式去调整，直到遇到父结点为树的根结点或者是一个黑链接（即2-结点为止）。这个过程就是红链接在树中的向上传递。

- 左旋转和右旋转
    
    我们不是要保证红链接一直为左链接吗？为什么还要有右旋转，其实是这样的，上面讲过了3-结点的新键插入，一般来讲，右旋转的操作之后一定会跟着一个颜色转换，这样就可以保证我们的右红链接不存在了。而左右旋转的使用时机在这里再总结一番：
    - 左旋转：结点的右子结点为红链接，而左子结点为黑链接时，使用左旋转。
    - 右旋转：结点的左子结点为红链接，并且它的左子结点也是红链接时，也即此时左子结点同时与两个红链接相连的时候，对左子结点使用右旋转。
    - 如果左右子结点均为红链接，则使用flipColor。
    - 不会出现根结点的右子结点为红链接，同时它的右子结点也为红链接的情况。因为在这种情况出现之前，就已经有一个结点被左旋转了。
    
    如下图所示：

![image](https://github.com/evsward/mainbase/blob/master/resource/image/search/RBTreeOp.png?raw=true)

#### 开始写代码吧
