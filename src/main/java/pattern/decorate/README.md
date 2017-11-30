## 装饰模式

> 装饰模式指的是在不必改变原类文件和使用继承的情况下，动态地扩展一个对象的功能。它是通过创建一个包装对象，也就是装饰来包裹真实的对象。

### 类图分析
我们先假设一个业务场景，有三种房子需要装修，分别是公寓，木屋和别墅，装修的方式有刷墙和摆满鲜花。那么应用装饰模式以后的类图结构如下所示：

![image](https://github.com/evsward/mainbase/blob/master/resource/image/patterns/Decorate/Decorate.png?raw=true)

这个结构似乎与[组合模式](http://www.cnblogs.com/Evsward/p/Composite.html)非常像，然而内部却大有不同。截止到Decorate部分，左上部分完全与组合模式相同，Decorate类是装饰类的核心类。

### 代码展示

```
package pattern.decorate;

/**
 * 装饰类基类，注意要继承构建类基类，同时关联一个基类对象
 * 
 * @author Evsward
 *
 */
public class Decorate extends House {
    protected House house;

    public Decorate() {
        // 给出一个默认值，防止house空值异常。
        this.house = new Cabin();
    }

    public Decorate(House house) {
        this.house = house;
    }

    @Override
    public void show() {
        house.show();
    }

}

```

这里面关键点为：
- Decorate类也继承了House抽象基类。
- 同时它还包含一个House对象的成员属性。
- 该属性在构造器中被初始化。
- 其复写的抽象方法并未实现任何具体内容，而是直接调用House对象的show方法。

然后请看Decorate的子类的实现方式，他们是如何具体的扩展构建类的。


```
package pattern.decorate;

public class GreenWallHouse extends Decorate {

    public GreenWallHouse(House h) {
        super(h);
    }

    private void painGreenOnWall() {
        logger.info("The wall is green now.");
    }

    @Override
    public void show() {
        super.show();
        painGreenOnWall();
    }

}

```

FlowerHouse也是同理。它们的关键点是：
- 必须创建构造函数，将House对象传入。
- 在实现抽象方法时，直接调用House对象的show方法，同时加入自己的“装饰”内容。

### 角色分析

装饰模式中重要的角色主要有两个，正是上面代码展示部分的那两个，他们可以总结为：
1. Decorate类，它继承自构建基类，并不作任何具体动作，却是架构中重要的一环。
2. ConcreteDecorate类，继承自Decorate类，作具体的扩展功能。

### 优势

装饰模式的架构平淡无奇，但是却可以不断套用，我们来看客户端的调用：


```
package pattern.decorate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Client {
    public final static Logger logger = LogManager.getLogger();

    @Test
    public void testDecorate() {
        House a = new Apartment();
        a.show();
        logger.info("---------------");
        House aPlus = new GreenWallHouse(a);
        aPlus.show();
        logger.info("---------------");
        /**
         * 因为他们都继承了House，是同一个基类，所以可以无限套用装饰类去循环。
         */
        House aPPlus = new GreenWallHouse(new FlowerHouse(a));
        aPPlus.show();
    }
}

```
输出：

    14:38:45[show]: This is my apartment, which is on the high floor.
    14:38:45[testDecorate]: ---------------
    14:38:45[show]: This is my apartment, which is on the high floor.
    14:38:45[painGreenOnWall]: The wall is green now.
    14:38:45[testDecorate]: ---------------
    14:38:45[show]: This is my apartment, which is on the high floor.
    14:38:45[decorateFlowerAround]: The room is full of flowers now.
    14:38:45[painGreenOnWall]: The wall is green now.

由于装饰模式中所有新增的类都是构建类的子类，并且他们每个类都声明了以构建类对象为参数的构造函数，因此，具体装饰类可以直接套用拓展，正如以上代码所示。

装饰模式，符合了面向对象设计原则“对修改关闭，对拓展开放”的原则。在原有代码完全不改动的情况下，可以有效拓展系统功能。并且通过不断的套用构造函数的方式，使得原始构建类得到了多层的功能拓展，这有效地代替了多继承。在JavaIO中，装饰模式得到了广泛使用。
