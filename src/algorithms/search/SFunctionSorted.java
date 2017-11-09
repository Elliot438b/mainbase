package algorithms.search;

/**
 * 查找算法的泛型接口，定义必须要实现的方法
 * 
 * @see 实现该接口只为了了解有序表的一些操作，我们仅在代码本身去测试，并不提供类似ST的基类
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
}
