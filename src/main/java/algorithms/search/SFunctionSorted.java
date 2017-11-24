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
