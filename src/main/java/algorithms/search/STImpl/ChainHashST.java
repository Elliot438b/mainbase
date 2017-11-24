package algorithms.search.STImpl;

import java.util.ArrayList;
import java.util.List;

import algorithms.search.SFunction;

/**
 * 基于拉链法的散列表
 * 
 * @notice 处理碰撞 将散列值相同的key（但实际上key是不同的）存入一个链表，而整个散列表容器是一个链表数组
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class ChainHashST<Key, Value> implements SFunction<Key, Value> {

    private int N;// 键值对总数（等于散列表上每个单链表的元素个数之和）
    private int M;// 散列表长度（也就是M条链表，只不过有的链表只有一个元素，有的有多个元素）
    private SequentialSearchST<Key, Value>[] st; // 单链表数组

    public ChainHashST() {
        this(997);// 初始化定义一个素数为链表数组长度
    }

    @SuppressWarnings("unchecked")
    /**
     * 构建一个长度为M的链表数组，并且为链表数组的每一个位置开辟内存空间。
     * 
     * @param M
     */
    public ChainHashST(int M) {
        this.M = M;
        // 注意：new SequentialSearchST<Key, Value>[M]这样是报错的，因为java不允许泛型数组。
        st = new SequentialSearchST[M];
        for (int i = 0; i < M; i++) {
            st[i] = new SequentialSearchST<Key, Value>();
        }
    }

    /**
     * 获得散列值
     * 
     * @param key
     * @return
     */
    private int hash(Key key) {
        // 32位整型值，去掉标志位留31位使用
        return (key.hashCode() & 0x7fffffff) % M;
    }

    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < M; i++) {
            size += st[i].size();
        }
        N = size;
        return N;
    }

    @Override
    public Value get(Key key) {
        return st[hash(key)].get(key);
    }

    @Override
    public void put(Key key, Value val) {
        st[hash(key)].put(key, val);
    }

    @Override
    public Iterable<Key> keySet() {
        List<Key> result = new ArrayList<Key>();
        for (int i = 0; i < M; i++) {
            result.addAll((List<Key>) st[i].keySet());
        }
        return result;
    }

    @Override
    public void remove(Key key) {
        for (Key k : keySet()) {
            if (key.equals(k)) {
                // st[hash(k)]取的是链表数组下标为当前键的哈希值的链表元素，该链表包含那些哈希相等但是key不同的键
                // 这里是按照key删除，key不会重复
                st[hash(k)].remove(key);
            }
        }
    }

}
