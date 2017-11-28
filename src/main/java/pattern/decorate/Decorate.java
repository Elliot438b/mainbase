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
