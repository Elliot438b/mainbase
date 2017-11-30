## 适配器模式
我们的笔记本电脑的充电线上通常都会有一个大砖块似得东西，这个东西是用来将家用额定电压220V转换成笔记本适用的电压，它叫做变压器，也叫做适配器。

今天要研究的是适配器模式，名副其实，中心思想也是建立一个适配器，将两个不同的接口或者应用连接起来，让他们能够协作。这种协作有时并不是主动的，就是他们彼此都不认识，只是通过适配器建立了合作通道而已。

> 适配器模式，将一个类的接口转换成客户希望的另外一个接口。Adapter模式使得原本由于接口不兼容而不能一起工作的那些类可以一起工作。

我们举一个例子写代码：
> 一个班级的成绩需要按照分数从大到小排名次。老师都是从成绩类Grade中的getRankList方法获得结果的，但是实现getRankList的算法过时了，或者说效率较低，而这时候另外我们有效率较高的算法QuickSort，但是由于双方的接口定义不同，而又不想直接更改getRankList方法，同时QuickSort是一个通用接口，更不会随意修改。

这时候就该适配器登场了，首先看客户端如何调用：

```
package pattern.adapter;

public class Main {

    public static void main(String[] args) {
        Grade g = new Grade();
        int[] grades = { 78, 75, 91, 81, 67, 32, 60, 59, 100, 74, 75 };
        g.getRankList(grades);
    }

}

```

Grade类的内部内容为：

```
package pattern.adapter;

public class Grade {

    public int[] getRankList(int[] index) {
        System.out.println("效率较低的算法");
        return index;
    }
}

```
此时，我们希望在不改变原有接口getRankList和QuickSort的前提下，新增一个Adapter类继承自Grade类，

```
package pattern.adapter;

import algorithms.sort.QuickSort;
import algorithms.sort.Sort;

public class GradeAdapter extends Grade {
    private Sort s = new QuickSort();

    @Override
    public int[] getRankList(int[] index) {
        index = s.sort(index);
        s.show(index);
        return index;
    }

}

```
在Adapter类中，我们首先定义了一个类属性QuickSort的实例，然后重写了getRankList方法，改为调用了QuickSort的算法。

原结果：
> 效率较低的算法

现结果：
> 100
91
81
78
75
75
74
67
60
59
32
数组长度：11，执行交换次数：18

通过上面的代码例子可以看到，Adapter的最佳使用时机不是在软件从无到有的设计阶段，而是在软件的维护阶段，当出现双方都不太容易修改的时候使用适配器模式适配，符合“开闭原则”，对扩展开放，对修改关闭。

> 如果能实现预防接口不同的问题，不匹配的问题就不会发生，在有效的接口不统一问题发生时，及时重构，问题不至于扩大；只有碰到无法改变原有设计和代码的情况时，才考虑适配。

> 事后控制不如事中控制，事中控制不如事前控制。

适配器模式是好模式，但如果不注意它的使用场合而盲目使用，就是本末倒置了。

参考资料：《大话设计模式》程杰著。