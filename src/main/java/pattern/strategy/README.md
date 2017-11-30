## 策略模式

> 策略模式是指对一系列的算法定义，并将每一个算法封装起来，而且使它们还可以相互替换。策略模式让算法独立于使用它的客户而独立变化。

我们正好可以使用策略模式加适配器模式来将查找算法的调用重新架构。

### 分角色
1. 环境类Context，直接暴露给客户端使用的类。
2. 策略类Strategy，是一个抽象类，用于统筹策略
3. 具体策略类，继承自Strategy，有自己的实现方法。

### 应用
为了学习策略模式，我们将查找算法中的[BST和RedBlackBST两个类](http://www.cnblogs.com/Evsward/p/search.html)作为研究素材，研究设计模式肯定是不能修改原有代码，也就是说BST和RedBlackBST不会做任何修改，在此基础上，我们使用[适配器模式](http://www.cnblogs.com/Evsward/p/adapter.html)将其封装取出
- BSTAdapter
- RedBlackBSTAdapter

下面展示BSTAdapter的代码，RedBlackAdapter与它相似。

```
package pattern.strategy;

import algorithms.search.ST;
import algorithms.search.STImpl.BST;

public class BSTAdapter extends Strategy {
    @Override
    public void algorithm() {
        logger.info(this.getClass().getName());
        ST<Integer, String> st;
        st = new ST<Integer, String>(new BST<Integer, String>());
        testST(st);
    }

}

```
然后贴上Strategy的代码，省略了一些与模式无关的内容。

```
public abstract class Strategy {
    protected static final Logger logger = LogManager.getLogger();

    public abstract void algorithm();// 核心抽象方法

    protected void testST(ST<Integer, String> sst) {...}
}
```
最后是Context的代码
```
package pattern.strategy;

public class Context {
    private Strategy strategy;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void testSTStrategy() {
        strategy.algorithm();
    }
}

```
客户端的调用方法
```
package pattern.strategy;

import org.junit.Test;

public class StrategyClient {

    @Test
    public void testStrategy() {
        Context context = new Context();
        context.setStrategy(new RedBlackBSTAdapter());// 运行时指定具体类型
        context.testSTStrategy();
        context.setStrategy(new BSTAdapter());// 运行时指定具体类型
        context.testSTStrategy();
    }
}

```
输出：


    11:22:50[algorithm]: pattern.strategy.RedBlackBSTAdapter
    11:22:50[testST]: 总耗时：69ms
    11:22:50[algorithm]: pattern.strategy.BSTAdapter
    11:22:51[testST]: 总耗时：88ms


下面给出类图

![image](https://github.com/evsward/mainbase/blob/master/resource/image/patterns/Strategy/strategy.png?raw=true)

https://github.com/evsward/mainbase/blob/master/resource/image/Strategy/strategy.png?raw=true
### 策略模式总结
1. 我们上面使用的策略模式和适配器模式的联通使得完全没有对BST和RedBlackBST做任何改变即可将它们套用到新的架构之中，所以策略模式是对“开闭原则”的完美实现。
2. 策略模式提供了管理相关的算法族的办法。策略类的等级结构定义了一个算法或行为族，恰当使用继承可以把公共的代码移到抽象策略类中，从而避免重复的代码。

缺点：

以上展示的策略模式仍旧处于初级阶段，具体算法均需要通过继承来实现，可以作为研究学习使用，仍然存在一些问题，例如对具体策略类的管理，如果使用场景不当，可能每次都要新建一个具体策略类，因此我们这里将其与适配器模式联用有效地避免了这一点。