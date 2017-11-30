## 桥接模式
> 桥接模式是一种很实用的结构型设计模式,它是将抽象部分与它的实现部分分离，使他们都可以独立地变化。

首先介绍一个标准的桥接模式的使用场景：
如果我想买汽车Car，我就要去4S店咨询。例如我们来到了一汽大众 FAWVolkswagen，我们需要咨询一汽大众旗下的车型 scanTypes()。接着我们去了长安福特，除了车型还想问问价格 askPrice()，还要问一下车险 askInsurance()。走出长安福特，我们又进到了广汽丰田，由于事先我们已经了解过车型，我们只想咨询一下价格 askPrice()，还要问一下车险 askInsurance()。代码如下：

```
package pattern.bridge;

public abstract class Car {}

public class ChanganFord extends Car{
    public void scanTypes() {}
    public void askPrice() {}
    public void askInsurance() {}
}

public class FAWVolkswagen extends Car{
    public void scanTypes() {}
}

public class GACToyota extends Car{
    public void askPrice() {}
    public void askInsurance() {}
}

```
观察以上代码，我们是否发现了什么问题？每个4s店类里面的方法似乎有重复的而我们多次重复写下相同的代码，当我们需要新增一个4S店或者新增修改一个操作方法，那么工程量是巨大的。

我们是否可以将这些方法抽象出来，4s店类本身是一个维度，可以有一汽大众，可以是长安福特，也可以是广汽丰田；同时询问车型、价格、车险是第二个维度。桥接模式就是让这两个维度可以建立独立继承关系，然后在中间建立一个抽象关联，这个抽象关联就像一个桥连接两个独立继承结构，将类之间的静态继承关系改为对象的组合关系，使系统更加灵活。

首先，我们将上面的例子拆分出两个维度：

1. 选择进入哪一家4S店。（大众、福特、丰田，后续还可以新增，以及修改或者删除现有内容）
2. 进了4S店说什么，也叫话术套路。（问车型、价格、保险，后续还可以新增，以及修改或者删除现有内容）

下面我们尝试修改一下上面的程序：

#### 一、定义一个话术接口
定义一个话术接口，声明一些话术方法的并集。
```
package pattern.bridge.taolu;

public interface SpeakIn4S {

    public void scanTypes();

    public void askPrice();

    public void askInsurance();

}

```
#### 二、创建两个话术实现类
创建两个话术类是为了日后可以选择，丰富我们程序的目标群众。先来个为北方人量身定做的。
```
package pattern.bridge.taolu;

/**
 * 4S店操作的具体话术套路：东北版
 * 
 * @author Evsward
 *
 */
public class NorthTaolu implements SpeakIn4S {

    @Override
    public void scanTypes() {
        System.out.println("你这都有啥车型？");
    }

    @Override
    public void askPrice() {
        System.out.println("我认识人，能便宜点不？");
    }

    @Override
    public void askInsurance() {
        System.out.println("是全保不？");
    }

}

```
再来个普通版的。
```
package pattern.bridge.taolu;

/**
 * 4S店操作的具体话术套路：普通版
 * 
 * @author Evsward
 *
 */
public class CommonTaolu implements SpeakIn4S {

    @Override
    public void scanTypes() {
        System.out.println("你家共有几种车？");
    }

    @Override
    public void askPrice() {
        System.out.println("能不能再给些优惠？");
    }

    @Override
    public void askInsurance() {
        System.out.println("保险都包括哪些内容？");
    }

}

```
#### 三、修改4S店基类
修改4S店基类，抽象问话方法。
```
package pattern.bridge;

import pattern.bridge.taolu.SpeakIn4S;

public abstract class SSSS {
    protected SpeakIn4S ops;// 通过对象的组合关系，将4S店类与问话操作关联起来。

    public void setOps(SpeakIn4S ops) {
        this.ops = ops;
    }

    public abstract void ask();// 增加一个用于调用具体操作的抽象方法。
}

```
#### 四、修改4S店子类
修改4S店子类，通过调用话术接口实现类的具体方法来具体其问话方法，有点绕，直接看代码吧。

- 一汽大众
```
package pattern.bridge;

public class FAWVolkswagen extends SSSS {

    @Override
    public void ask() {
        ops.scanTypes();
    }

}

```
- 长安福特

```
package pattern.bridge;

public class ChanganFord extends SSSS {

    @Override
    public void ask() {
        ops.scanTypes();
        ops.askPrice();
        ops.askInsurance();
    }
}

```
- 广汽丰田

```
package pattern.bridge;

public class GACToyota extends SSSS {

    @Override
    public void ask() {
        ops.askPrice();
        ops.askInsurance();
    }

}

```
#### 五，客户来操作

```
package pattern.bridge;

import pattern.bridge.taolu.CommonTaolu;
import pattern.bridge.taolu.SpeakIn4S;

public class Client {
    static void askACar() {
        // 声明两个维度
        SSSS c;
        SpeakIn4S taolu;
        // 第一个维度：选择进入哪一家4S店。
        c = new GACToyota();
        // 第二个维度：进了4S店说什么。
        taolu = new CommonTaolu();
        // 两个维度相互关联上。
        c.setOps(taolu);
        c.ask();// 具体实施
    }

    public static void main(String[] args) {
        askACar();
    }
}

```
    能不能再给些优惠？
    保险都包括哪些内容？

#### 六、客户端进一步解耦

```
package pattern.bridge;

import pattern.bridge.taolu.SpeakIn4S;
import tools.XMLUtil;

public class Client {
    static void askACar() {
        SSSS c;
        SpeakIn4S taolu;
        // 第一个维度：选择进入哪一家4S店。
        c = (SSSS) XMLUtil.getBean("ssss");
        // 第二个维度：进了4S店说什么。
        taolu = (SpeakIn4S) XMLUtil.getBean("taolu");
        // 两个维度相互关联上。
        c.setOps(taolu);
        c.ask();
    }

    public static void main(String[] args) {
        askACar();
    }
}

```
我们将创建对象的方法从new改为了读取配置文件，这个XMLUtil是复用的之前写下的工具类，改了改它给他加了参数，现在是：

```
package tools;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/**
 * XML文件读取工具类
 * 
 * @author Evsward
 *
 */
public class XMLUtil {
    /**
     * 通过xml文件里的tagName搜索，获得其内部的值
     * 
     * @param tagName
     * @return
     */
    public static Object getBean(String tagName) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("resource/config.xml"));

            NodeList nl = doc.getElementsByTagName(tagName);
            Node n = nl.item(0).getFirstChild();
            String className = n.getNodeValue();

            Object c = Class.forName(className).newInstance();
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

```
配置文件的内容也贴出来看一下：

```
<?xml version="1.0"?>
<config>
    <builder>pattern.builder.IPhone</builder>
    <ssss>pattern.bridge.GACToyota</ssss>
    <taolu>pattern.bridge.taolu.CommonTaolu</taolu>
</config> 
```
注意XMLUtil在获得tag的值以后要通过反射来创建类的对象，这时候如果不写入完整的包名，编译器将找不到这个类，无法创建成功。
> 这种配置方法在Spring等框架中十分常见，有时我们好像在Spring的配置文件中只写了类名而没写包名能够正常运行，那是因为xml的结构发生了变化，肯定有一个tag表明了包的路径，下面配置的类名均默认在此包下面。

### 桥接模式与设计原则的关系
- 桥接模式是非常常见，且立竿见影的设计模式，继续拿上面的例子来说，当我们要增加一种新的话术支持的时候，只需要新增一个SpeakIn4S接口的实现类即可，在使用的时候，也仅是更改配置文件的内容，所有的原有代码完全不用修改，这符合了“开闭原则”。

- 接着，我们原有4S店的实现类还要去实现具体的话术内容，这就违反了“单一职责原则”，通过桥接模式将这两个维度的东西拆分以后，符合了“单一职责原则”。

- 使用配置文件在运行时注入其子类的对象，这符合“依赖倒转原则”和“里氏代换原则”。

- 在第一个维度4S店里面，我们将话术类作为其成员对象，这是对象的组合关系，而不是静态继承，这符合“合成复用原则”。


### 桥接模式与适配器模式联用
适配器模式比较简单，我们在前面的[促和谐好干部——适配器模式](http://www.cnblogs.com/Evsward/p/adapter.html)中简单介绍了一下。桥接模式一般是用在软件的设计阶段，而当软件运营一段时间，在不改变原程序结构的基础上要增加适配一些不可改变的第三方接口时，适配器模式就出来了，所以，桥接模式和适配器模式经常一起联用。

接着上面的例子来说：我们的程序需要接入一个第三方话术服务，第三方话术服务的代码如下：


```
package pattern.bridge.thirdparty;

public class SmartTalking {
    public void sayHello() {
        System.out.println("hello,");
    }

    public void askCar() {
        System.out.println("how many kinds of cars do you have?");
    }

    public void askMoney() {
        System.out.println("How much is it?");
    }

    public void askMoney2() {
        System.out.println("Do you have any discount?");
    }

    public void askAfterSale() {
        System.out.println("What about the insurance?");
    }
}

```

由于是第三方公司提供，他们肯定不会修改接口来适应我们的程序，我们也不想修改我们原有的代码。这时候我们可以新建一个Adapter类来适配：

```
package pattern.bridge.taolu;

import pattern.bridge.thirdparty.SmartTalking;

public class SmartTalkingAdapter implements SpeakIn4S {
    private SmartTalking st = new SmartTalking();

    @Override
    public void scanTypes() {
        st.askCar();
    }

    @Override
    public void askPrice() {
        st.askMoney2();
    }

    @Override
    public void askInsurance() {
        st.askAfterSale();
    }

}

```
接下来，我们修改一下配置文件中的话术为新加入的这个适配类。

```
<taolu>pattern.bridge.taolu.SmartTalkingAdapter</taolu>
```
客户端执行结果变为：

    Do you have any discount?
    What about the insurance?

适配成功。

### 总结

桥接模式是JVM和JDBC等模块设计的核心模式，当你遇到多维度问题的程序时，可以采用桥接模式来降低系统的复杂度，增加代码的复用性。桥接模式将多维度问题拆分，是每个维度的内容沿着自己的维度独立变化，分离了抽象和实现，提高了系统的扩展性。

然而，桥接模式依赖的多维度关系需要事先准确的抽象出来，这对于程序员来讲，是一个挑战，需要经验积累。

### 参考资料
- 《四人帮GOF的设计模式》

### 源码请转到 [Evsward的Github](https://github.com/evsward/mainbase/tree/master/src/pattern/bridge)

### 更多内容请关注 [醒者呆的博客园](http://www.cnblogs.com/Evsward/)

