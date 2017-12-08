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
