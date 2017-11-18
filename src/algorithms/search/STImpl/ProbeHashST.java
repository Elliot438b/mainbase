package algorithms.search.STImpl;

import java.util.ArrayList;
import java.util.List;

import algorithms.search.SFunction;

/**
 * 基于线性探测的符号表
 * 
 * @connect BinarySearchST，resize实现，平行数组，均十分类似，而且也可以对插入的键实现自动排序
 *          并且先进的是这里的Key并不需要继承Comparable接口，因为它不是靠比较大小，而是比较哈希值是否相等。
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class ProbeHashST<Key, Value> implements SFunction<Key, Value> {

    private Key[] keys;// 键数组
    private Value[] vals;// 值数组

    private int N;// 符号表中键值对的总数，N是很小于M的
    private int M;// 散列表大小，M有很多空元素

    @SuppressWarnings("unchecked")
    public ProbeHashST() {
        this.M = 1;// 初始化将散列表长度设置为16，定下来以后就不能变了
        keys = (Key[]) new Object[M];
        vals = (Value[]) new Object[M];
    }

    @SuppressWarnings("unchecked")
    public ProbeHashST(int Cap) {
        this.M = Cap;
        keys = (Key[]) new Object[M];
        vals = (Value[]) new Object[M];
    }

    /**
     * 获得散列值
     * 
     * @param key
     * @return
     */
    private int hash(Key key) {
        // 获取key的哈希值为一个32位整型值，去掉标志位留31位使用，通过除留余数法获得散列值
        return (key.hashCode() & 0x7fffffff) % M;
    }

    /**
     * 数组增容减容
     * 
     * @notice 复制于BinarySearchST 稍作调整。
     * @param 数组容量大小
     */
    public void resize(int cap) {
        // 先创建一个新的容量的空散列表
        ProbeHashST<Key, Value> t = new ProbeHashST<Key, Value>(cap);
        for (int i = 0; i < M; i++) {// 遍历当前的存储数据的散列表
            /**
             * 神奇之处在于每次resize，都会将数据重新按照新的长度计算哈希存入容器。
             * 
             * @性能 但是每次resize都会触发重新遍历的put，会消耗性能，但是随着样本规模增大，resize的次数变少，性能影响会越来越小
             */
            if (keys[i] != null)
                // 将原数据依次重新put到新容量的空散列表中（会按照新的空散列表进行hash算法，所以数据位置会发生变化）
                t.put(keys[i], vals[i]);
        }
        keys = t.keys;
        vals = t.vals;
        M = t.M;
    }

    @Override
    public int size() {
        return N;
    }

    @Override
    public Value get(Key key) {
        /**
         * 由于我们的插入机制，逆向思考在查找key的时候，是从hash(key)下标开始找，命中即找到，不等则继续往下找，遇到空了说明没有。
         */
        for (int i = hash(key); keys[i] != null; i = (i + 1) % M) {
            if (keys[i].equals(key)) {// 命中key，返回val
                return vals[i];
            }
            /**
             * 不等则继续循环
             */
        }
        /**
         * 循环终止，说明遇到空元素了，根据插入机制，那就代表整个数组都不含有该key
         */
        return null;
    }

    @Override
    public void put(Key key, Value val) {
        if (N >= M / 2)
            resize(2 * M);// M要保证至少内部N的占有率为25%到50%之间。（与BinarySearchST操作相同）
        int i;
        /**
         * @notice hash(key)是可以重复的，也就是碰撞位置，而key是不可重复的，是主键。
         * @具体探测方法: 初始化为传入key的散列值，探测其是否相等、为空或者不等，相等则更新值，为空则插入值，不等则继续查找。
         */
        for (i = hash(key); keys[i] != null; i = (i + 1) % M) {
            if (keys[i].equals(key)) {
                vals[i] = val;// 命中key，更新val，直接退出循环，退出方法。
                return;
            }
            /**
             * 不等则循环继续
             */
        }
        // 从循环出来只有一种情况，就是keys[i]==null,则在当前i位置添加键值对。
        keys[i] = key;
        vals[i] = val;
        N++;
    }

    @Override
    public Iterable<Key> keySet() {
        List<Key> list = new ArrayList<Key>();
        for (int i = 0; i < M; i++) {
            if (keys[i] != null) {// 剔除空元素
                list.add(keys[i]);
            }
        }
        return list;
    }

    @Override
    public void remove(Key key) {
        for (int i = hash(key); keys[i] != null; i = (i + 1) % M) {
            if (keys[i].equals(key)) {// 命中key，开始删除
                keys[i] = null;
                vals[i] = null;
                N--;
                if (N <= M / 4) {// 如果键值对数据比容器的25%小的时候，容器减容一倍。（与BinarySearchST操作相同）
                    resize(M / 2);
                }
                return;
            }
            /**
             * 不等则继续循环
             */
        }
        /**
         * 未命中key不做任何操作
         */
    }

}
