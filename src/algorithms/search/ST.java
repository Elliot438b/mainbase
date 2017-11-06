package algorithms.search;

/**
 * 符号表类
 * 
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class ST<Key, Value> {
    /**
     * ST类并不去亲自实现SFunction接口
     * 
     * @design “合成复用原则”
     * @member 保存接口实现类的对象为符号表类的成员对象
     */
    SFunction<Key, Value> sf;

    /**
     * 构造器创建符号表对象
     * 
     * @param sf
     *            指定接口的具体实现类（即各种查找算法）
     */
    public ST(SFunction<Key, Value> sf) {
        this.sf = sf;
    }

    /**
     * 采用延时删除，将key的值置为null
     * 
     * @param key
     *            将要删除的key的值
     */
    public void delete(Key key) {
        sf.put(key, null);
    }

    /**
     * 判断表内是否含有某key
     * 
     * @param key
     * @return
     */
    public boolean contains(Key key) {
        return sf.get(key) != null;
    }

    /**
     * 判断表是否为空
     * 
     * @return
     */
    public boolean isEmpty() {
        return sf.size() == 0;
    }

    public void put(Key key, Value val) {
        sf.put(key, val);
    }

    public int size() {
        return sf.size();
    }

    public Iterable<Key> keys() {
        return sf.keys();
    }

    public Value get(Key key) {
        return sf.get(key);
    }
}
