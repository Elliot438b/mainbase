package algorithms.search;

import java.util.List;

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
     *            将要删除的key
     */
    public void delete(Key key) {
        sf.put(key, null);
    }

    /**
     * 判断表内是否含有某key（value为null，key存在也算）
     * 
     * @param key
     * @return
     */
    public boolean containsKey(Key key) {
        @SuppressWarnings("rawtypes")
        List list = (List) keySet();
        list.contains(key);
        return list.contains(key);
    }

    /**
     * 判断表是否为空
     * 
     * @return
     */
    public boolean isEmpty() {
        return sf.size() == 0;
    }

    /**
     * 以下方法均在接口实现类中实现
     */

    public void put(Key key, Value val) {
        sf.put(key, val);
    }

    public int size() {
        return sf.size();
    }

    public Iterable<Key> keySet() {
        return sf.keySet();
    }

    public Value get(Key key) {
        return sf.get(key);
    }

    /**
     * 即时删除（与延时删除相对应的） 直接删掉某key
     * 
     * @param key
     */
    public void remove(Key key) {
        sf.remove(key);
    }
}
