> 关键字：设计模式，代理模式，proxy，保护代理，虚拟代理，远程代理，缓冲代理，智能引用代理

## 代理模式

> 代理模式：给某一个对象提供一个代理或占位符，并由代理对象来控制对原对象的访问。

说白了，就是当你不能直接访问一个对象时，通过一个代理对象来间接访问，这种方式就叫做代理模式。

### 应用场景
代理模式是一种比较常见的结构型设计模式，按照不同的场景，又可以分为保护代理，远程代理，虚拟代理，缓冲代理和智能引用代理。
- 保护代理，就是为原对象设置不同的访问权限，处于被保护的状态，不可直接访问，用户在访问时根据自己不同的权限来访问。
- 远程代理，在本地创建一个远程对象的本地代理，通过访问该本地代理实现访问远程对象的目的，也叫大使（Ambassador）
- 虚拟代理，虚拟的意思是假的，当要创建一个复杂巨大的对象时，先创建一个小的代理对象，当具体使用时再创建真实对象，有点“懒加载”的意思。
- 缓冲代理，为针对原对象的某种操作提供一个临时的缓冲代理，用来分担访问原对象的压力，以便多客户端快速共享这些资源。
- 智能引用代理，相当于对原对象的一种功能扩展，在访问原对象时，加入了新功能，例如统计访问次数等。

### UML类图分析
通过上面的定义介绍，我想我们对代理模式已经有了初步的认识，心中已经有了一种架构图的出现：

![image](https://github.com/evsward/mainbase/blob/master/resource/image/patterns/Proxy/Proxy-one.png?raw=true)

Client无法直接访问Real，这时出现了一个Proxy类继承自Real，Client可以直接访问Proxy来使用Real的资源。看上去是这样，但是Proxy和Real不应该是继承的关系。

> 为什么Proxy不能简单的继承Real来达到代理模式的设计？

来看一下上面介绍过的代理模式的应用场景，我们希望通过代理模式的架构来给原对象创建一个代理类Proxy，它可以为原对象提供不同的访问权限，可以继承一部分原对象开放出来的接口，可以为原对象增添新功能，可以只是一个原对象的创建方法，作为原对象的一个过度，而这些，不能局限于一个基于强连接的直接继承关系。下面我用一段代码来说明Proxy不能简单继承Real的原因


```
package pattern.proxy;

public class Real {
    private long id;
    private String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

```

```
package pattern.proxy;

public class Proxy extends Real {

}

```

```
package pattern.proxy;

import org.junit.Test;

public class Client {
    private Proxy proxy = null;

    @Test
    public void testSimpleProxy() {
        proxy = new Proxy();
        proxy.getId();
        proxy.getPassword();// 问题出现了
    }
}

```
发现了没有，如果是保护代理的话，我们不希望Real类中的password属性被透明访问，只有拥有该权限的客户端才可以访问，其他都无法访问password字段内容。所以，我们需要一个更加灵活的Proxy和Real之间的关系，这个关系一定不是强继承的关系，经过这一番思考，我们发现了代理模式中的核心类：抽象代理类

![image](https://github.com/evsward/mainbase/blob/master/resource/image/patterns/Proxy/Proxy-two.png?raw=true)

### 代理模式的角色

现在来区分代理模式的角色：
- 抽象代理类，推荐使用抽象类的方式，声明了Real和Proxy的共同接口，这样用来在任何使用Real的地方都可以使用Proxy。客户端要针对该抽象代理类进行编程。
- 具体代理类，根据合成复用原则，它包含了Real的引用，从而可以在任何时候操作Real对象，同时它与Real都是继承自抽象代理类，并且与Real拥有相同的接口，可以随时替换Real。然后，它可以实现对Real的控制，可以编程创建和删除Real对象，通常来讲，Client通过具体代理类访问Real的接口时，要在具体代理类中预先执行多个补充方法，然后再对Real进行操作。
- 真实类，Real类，Client不能或者不想直接访问的类。它的对应于Proxy那个共同的接口有着真实的业务操作。

下面来看代码：

Real类改为继承Proxy抽象类，其他不变，Proxy抽象类改为
```
package pattern.proxy;

public abstract class Proxy {
    public abstract long getId();

    public abstract String getPassword();
}

```
增加一个同样继承于Proxy类的具体实现类，这里通过与Real建立组合关系将Real的对象作为SuperProxy的成员属性，为了满足多线程要求，这里采用了[饿汉单例模式](
http://www.cnblogs.com/Evsward/p/singleton.html#%E8%AF%B7%E5%8F%B2%E4%B8%8A%E6%9C%80%E7%89%9B%E7%A7%91%E5%AD%A6%E5%AE%B6%E5%A5%97%E8%B7%AF%E7%AC%AC%E4%BA%8C%E7%89%88)。

```
package pattern.proxy;

public class SuperProxy extends Proxy {
    private static Real real = new Real();// 直接采用饿汉单例

    /**
     * 要想操作Real，要先执行具体Proxy类中的一些其他方法，或许是创建Real对象，也或许是准备数据。
     */

    static {
        real.setId(12312l);
        real.setPassword("dontknow");
    }

    @Override
    public long getId() {
        return real.getId();
    }

    @Override
    public String getPassword() {
        return real.getPassword();
    }

}

```
客户端调用方法：

```
package pattern.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Client {
    private final static Logger logger = LogManager.getLogger();
    private Proxy proxy = null;

    @Test
    public void testSimpleProxy() {
        proxy = new SuperProxy();
        long id = proxy.getId();
        String pwd = proxy.getPassword();
        logger.info("id:" + id + " password:" + pwd);
    }
}

```
输出：20:35:10[testSimpleProxy][main]: id:12312 password:dontknow

### 业务实例（保护代理和智能引用代理）
> 业务需求：Client在访问Real的时候，要先进行身份验证并且记录访问次数。而同时，不可对Real内部进行修改。

这种情况下，我们可以通过代理模式增加代理类来实现，下面看代码：

首先定义一个代理抽象基类，加入共有抽象方法（此方法一定是已存在与RealSearcher的，因为RealSearcher内部不可改变）

```
package pattern.proxy.search;

public abstract class Searcher {
    public abstract String doSearch(String username, int sid);
}

```
将原RealSearcher改为继承于代理代理抽象基类，其他不必做任何修改。

```
package pattern.proxy.search;

import java.util.HashMap;
import java.util.Map;

public class RealSearcher extends Searcher {
    private Map<Integer, String> data = new HashMap<Integer, String>();

    RealSearcher() {// 模仿数据源，对象构造时初始化数据
        data.put(1001, "fridge");
        data.put(1002, "book");
        data.put(1003, "macrowave oven");
    }

    public String doSearch(String username, int sid) {
        return data.get(sid);
    }
}

```
此时，创建一个新的具体代理类，加入用户校验和访问次数功能。

```
package pattern.proxy.search;

public class ProxySearcher extends Searcher {

    private Searcher searcher = new RealSearcher();

    private int count;

    public String doSearch(String username, int sid) {
        if (validateUser(username)) {
            count++;
            return "times: " + count + " " + searcher.doSearch(username, sid);
        }
        return "Identity discrepancy";
    }

    private boolean validateUser(String username) {
        if ("jhon".equals(username))
            return true;
        return false;
    }
}

```
最后是客户端调用的方式，因为具体代理类和真实对象都是继承于代理抽象基类，因此可以创建抽象基类的不同子类的实例，同时他们都拥有原属于真实对象的查询方法。

```
package pattern.proxy.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Client {
    private final static Logger logger = LogManager.getLogger();

    @Test
    public void testSearcher() {
        Searcher searcher = new ProxySearcher();// 创建一个代理类对象，而不是RealSearcher
        logger.info(searcher.doSearch("jhon", 1002));
        logger.info(searcher.doSearch("jhon", 1001));
        logger.info(searcher.doSearch("jack", 1002));
        logger.info(searcher.doSearch("java", 1003));
    }
}

```
输出结果：

    11:06:03[testSearcher][main]: times: 1 book
    11:06:03[testSearcher][main]: times: 2 fridge
    11:06:03[testSearcher][main]: Identity discrepancy
    11:06:03[testSearcher][main]: Identity discrepancy

通过结果可以看出，我们只改变了真实查找类的继承关系即可实现增加用户验证和访问次数的功能。

### 其他代理场景

我们所有的代理模型都可以采用上面给出的类图结构，上面的代码实例介绍了保护代理和智能引用代理。下面再介绍一下其他的代理场景。
- 远程代理，上面简单介绍了一下，但其实远程代理在实际工作中有着广泛的应用，因为我们的程序往往需要远程主机的支持，或许是因为远程主机有着自己的服务接口，也或许远程主机有更加高效的环境，而远程代理可以将本地与远程主机直接的网络通信相关的操作封装起来，让我们本地的程序可以直接使用远程主机的好的性能或者功能。这部分由于模仿起来不好实现，因此如果未来在研究源码时遇到远程代理相关的内容会再详细介绍它在代码中具体的运用。
- 虚拟代理同样有着广泛的使用。在异步通信中，它可以显著解决由于对象本身复杂性，消耗大量系统资源或者网络原因造成的对象加载时间过久的问题，这时候可以通过多线程技术，使一个线程加载一个速度较快的小对象来代替原对象承接客户端新的操作，其他线程则继续加载原对象。
- 缓冲代理也是非常实用的模型，它可以建立一个临时内存空间存储那些操作较频繁的结果，使得更多的客户端可以直接共享这些结果而不必再去重复查找。其实我们在上面的代码中也无意间使用到了缓冲代理，只是没完全按照代理模式的架构去写，那就是RealSearcher的数据初始化部分，其实实际工作中数据都是来自于真实的数据源，例如数据库，或者网络通信，而我们这里相当于直接将数据源中的数据缓存在了本地程序中，当jvm启动的时候会在内存中创建这些数据，而jvm终止以后，这些数据也就没了，通过缓冲代理，客户端可以获得更快的访问速度，同时也减少了对真实数据源的访问次数。


### 代理模式总结
代理模式是非常常用的结构型设计模式，尤其是我们前面介绍过的保护代理，远程代理，虚拟代理，缓冲代理以及智能引用代理，本文介绍了他们的主旨思想，给出了代理模式的核心架构，解释了代理模式的原理，未来在其他内容的研究过程中，会碰到真实场景中的代理模式的应用，我会深入介绍。