package algorithms.search;

/**
 * 符号表类（特指有序表 Sorted Search Table, SST）
 * 
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class SST<Key, Value> {
    /**
     * ST类并不去亲自实现SFunction接口
     * 
     * @design “合成复用原则”
     * @member 保存接口实现类的对象为符号表类的成员对象
     * @note 增加一个有序表的算法方法接口对象，作为有序符号表的成员对象
     */
    SFunctionSorted<Key, Value> ssf;

    /**
     * 构造器创建符号表对象
     * 
     * @param sf
     *            指定接口的具体实现类（即各种查找算法）
     */
    public SST(SFunctionSorted<Key, Value> ssf) {
        this.ssf = ssf;
    }

    public Key min() {
        return ssf.min();
    }

    public Key max() {
        return ssf.max();
    }

    public Key select(int k) {
        return ssf.select(k);
    }

    public Key ceiling(Key key) {
        return ssf.ceiling(key);
    }

    public Key floor(Key key) {
        return ssf.floor(key);
    }

    public void put(Key key, Value val) {
        ssf.put(key, val);
    }

    public Iterable<Key> keySet() {
        return ssf.keySet();
    }

    public Value get(Key key) {
        return ssf.get(key);
    }
}
