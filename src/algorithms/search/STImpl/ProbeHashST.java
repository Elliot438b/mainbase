package algorithms.search.STImpl;

import algorithms.search.SFunction;

public class ProbeHashST<Key, Value> implements SFunction<Key, Value> {

    private Key[] keys;// 键数组
    private Value[] vals;// 值数组

    private int N;// 符号表中键值对的总数
    private int M;// 散列表大小

    @SuppressWarnings("unchecked")
    public ProbeHashST() {
        this.M = 16;// 初始化将散列表长度设置为16
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
        // 32位整型值，去掉标志位留31位使用
        return (key.hashCode() & 0x7fffffff) % M;
    }

    /**
     * @notice 复制于BinarySearchST
     */
    @SuppressWarnings("unchecked")
    public void resize(int max) {
        Key[] tempKeys = (Key[]) new Comparable[max];
        Value[] tempValues = (Value[]) new Object[max];
        for (int i = 0; i < N; i++) {
            tempKeys[i] = keys[i];
            tempValues[i] = vals[i];
        }
        keys = tempKeys;
        vals = tempValues;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Value get(Key key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void put(Key key, Value val) {
        // TODO Auto-generated method stub

    }

    @Override
    public Iterable<Key> keySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(Key key) {
        // TODO Auto-generated method stub

    }

}
