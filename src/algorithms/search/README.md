> 首先保证这一篇分析查找算法的文章，气质与大部分搜索引擎搜索到的文章不同，主要体现在代码上面，会更加高级，结合到很多之前研究过的内容，例如设计模式，泛型等。这也与我的上一篇[面向程序员编程——精研排序算法](http://www.cnblogs.com/Evsward/p/sort.html)的气质也不尽相同。

> 关键字：查找算法，索引，泛型，二分查找树，红黑树，散列表，API设计，日志设计，测试设计

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
     * delete, containKeys, isEmpty();方法均定义在ST中。
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
     * 返回下标为k的Key
     * 
     * @param k
     * @return
     */
    public Key select(int k) {
        if (k >= top || k < 0)// 越界（指的是键值对数据空间越界）
            return null;
        return keys[k];
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

### 二叉查找树继续

- 代码阶段


