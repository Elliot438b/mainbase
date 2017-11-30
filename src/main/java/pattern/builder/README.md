## 建造者模式
建造者模式是最后一个创建型设计模式，也是研究对象的创建。
> 将一个复杂对象的创建与它的表示分离，使得同样的构建过程可以创建不同的表示。

**创建和表示是什么意思**？

表示就是外在，对象具体的样子，而内部构建过程是一种组装的概念，有点像套娃，同样的结构，但是却产生了不同大小的样子。

按照惯例，先讲故事。

假设要生产一部iPhone和一部ipod。我们要怎么做？

```
public class IPhone {
    private String camera;
    private String touchScreen;
    private String communication;
    //省略getter，setter方法。
```

```
public class Ipod {
    private String camera;
    private String touchScreen;
    //省略getter，setter方法。
```

```
public static void main(String[] args) {
    IPhone iPhone = new IPhone();
    Ipod ipod = new Ipod();
    iPhone.setCamera("1200 pixel");
    iPhone.setTouchScreen("retina");
    iPhone.setCommunication("TDMA");
    ipod.setCamera("800 pixel");
    ipod.setTouchScreen("NOVA");
}
```
我们知道同为apple旗舰产品的iPhone和ipod其实差不太多，只是iPhone的配置相对高一些，同时多了通讯模块。那么上面的代码是否显得臃肿，没有设计，因为都是在创建对象的时候去设置其属性，而且这两款产品的属性似乎差不多，好像可以套用一样的生产线。

> 建造者模式 = buildX() + construct() + (optional)ifX()

### 第一次建造
增加一个基类Apple产品类，包含并集的所有零件的属性。

```
public class Apple {
    private String camera;
    private String touchScreen;
    private String communication;
    //省略getter，setter方法。
```
增加一个abstract AppleBuilder类，包含Apple的成员对象。然后加入buildX方法，以及**钩子方法ifX用来判断某些只有一些子类特有的属性**。最后要加一个成员对象apple的获取方法。

注意：这个apple对象要定义为protected的原因是它的子类要直接使用这个对象。

```
public abstract class AppleBuilder {
    protected Apple apple = new Apple();

    public abstract void buildCamera();

    public abstract void buildTouchScreen();

    public abstract void buildCommunication();

    public boolean ifCommunication() {
        return false;
    }

    public Apple createApple() {
        return apple;
    }
}
```
然后修改IPhone类和Ipod类，让他们实现AppleBuild类，可以直接使用基类的apple对象，而不必自身再去定义Apple中已经定义好的属性们，只需实现AppleBuilder类的abstract funciton buildX们。同时，不要忘记，由于默认ifCommunication是false，所以IPhone类一定要重写改**钩子方法**，修改为return true的方式。

```
public class IPhone extends AppleBuilder {

    @Override
    public void buildCamera() {
        apple.setCamera("1200 pixel");
    }

    @Override
    public void buildTouchScreen() {
        apple.setTouchScreen("retina");
    }

    @Override
    public void buildCommunication() {
        apple.setCommunication("TDMA");
    }

    @Override
    public boolean ifCommunication() {
        return true;
    }

}
```
```
public class Ipod extends AppleBuilder {

    @Override
    public void buildCamera() {
        apple.setCamera("800 pixel");
    }

    @Override
    public void buildTouchScreen() {
        apple.setTouchScreen("NOVA");
    }

    @Override
    public void buildCommunication() {
        apple.setCommunication("none");
    }
}
```
最后，要加入关键的导演类，这里是车间类，用于真正的组装工作，对外提供装配方法construct();

```
public class Workshop {
    public static Apple construct(AppleBuilder ab) {
        ab.buildCamera();
        ab.buildTouchScreen();
        if (ab.ifCommunication()) {
            ab.buildCommunication();
        }
        Apple apple = ab.createApple();
        return apple;
    }
}
```
这样，就可以直接在客户端调用了。

```
public static void main(String[] args) {
    Apple apple = Workshop.construct(new IPhone());
    System.out.println("" + apple.getCamera());
}
```
客户端调用的时候，只需要新建一个workshop对象，然后调用其construct方法，传入具体的Apple产品子类即可获得一个Apple类。而new IPhone()我们可以使用配置文件的方式，这里给出代码。


```
public static void main(String[] args) {
    Apple apple = Workshop.construct((AppleBuilder) XMLUtil.getBean());
    System.out.println("" + apple.getCamera());
}
```
配置文件

```
<?xml version="1.0"?>
<config>
       <className>construction.IPhone</className>
</config> 
```
建立XMLUtil类

```
public class XMLUtil {
    public static Object getBean() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("E:\\Evsward\\workspace\\test\\src\\config.xml"));

            NodeList nl = doc.getElementsByTagName("className");
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
以上就是第一次建造的全部代码了。我们可以清晰地看到完整的建造者模式的结构。几个要注意的点是：
1. 目前的几个对象是可以抽象出来一个包含所有属性的对象，如此例中的Apple类。
2. 我们去掉了具体的对象类，而是直接采用Builder的方式，将每个对象具体的内容实现在其里面。
3. Builder要抽象出来一个基类，要包含上面的那个总对象以及该对象的对外获取方法。同时要注意设置该对象为protected，因为其子类Builder们要直接使用该对象，给该对象的属性赋值。
4. 最重要的导演类，此例中的车间类，只提供一个construct方法，我觉得设置为static更好，外部可以直接通过类来调用。该方法内部要去调用具体的buildX的顺序。

### 第二次建造
这一次建造致力于最大限度精简化，此次建造属于探索性建造，不一定用于生产环境。

去掉导演类，将construct方法移入AppleBuilder类中。然后客户端调用方式为

```
public static void main(String[] args) {
    Apple apple = AppleBuilder.construct((AppleBuilder) XMLUtil.getBean());
    System.out.println("" + apple.getCamera());
}
```
这样虽然精简结构了，但是会让代码变得不可读，AppleBuilder类的职责太多，不仅要创建对象，提供外部访问方法，还要声明各种buildX方法，以及可能出现的钩子方法，违背了“单一职责原则”。

所以当系统业务比较复杂的时候，不推荐省略导演类，完整的建造者模式会提高代码的可读性，以及更好的扩展。

### 适用场景
1. 当要创建的对象内部属性比较复杂，且与其他对象有公共的部分的时候。然后他们的内部属性结构一定要稳定。
2. 需要生成的对象属性可以变成buildX的形式，对属性赋值的顺序有要求。
> 3. 隔离复杂对象的创建和使用，并使得相同的创建过程可以创建不同的产品。

创建过程是在导演类中进行，这就与使用隔离开来。