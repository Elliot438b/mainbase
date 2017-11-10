package algorithms.search.second;

import java.util.ArrayList;
import java.util.List;

import algorithms.search.SFunction;
import algorithms.search.SFunctionSorted;

/**
 * 一对平行的可变长度数组，参照ResizeArrayStack
 * 
 * @author Evsward
 * @notice 二分查找为Key有序：所以Key的泛型的边界为Comparable，可比较的，否则无法判断是否有序
 * @param <Key>
 * @param <Value>
 */
public class BinarySearchST<Key extends Comparable<Key>, Value>
        implements SFunction<Key, Value>, SFunctionSorted<Key, Value> {

    private Key[] keys;
    private Value[] values;
    private int top;// keys顶端指针，不存值，等于数组长度的大小，keys[top-1]为最长处的元素。

    /**
     * 做一个通用类，可以自动先将数据排序后再进行二分查找
     * 
     * @target 构造函数时强制Key有序，同时Value的值也要跟随变化，保证key-value的映射。
     * 
     * @卡壳处 无法根据Key数组创建一个等长的Value数组，因为泛型数组无法被创建。
     * @error Cannot create a generic array of Value
     */
    public BinarySearchST() {
        // 必须加一个无参构造器，否则客户端测试报错
        // 通过配置文件反射创建对象没有初始化构造器参数。
        resize(1);
    }

    public BinarySearchST(int capacity) {
        if (capacity < 1)// 如果设置的不符合标准，则初始化为1
            capacity = 1;
        resize(capacity);
        // Value valueTemp[] = new Value[keyTemp.length];// error
    }

    /**
     * 同时调整Key和Value数组大小，以适应伸缩的键值对空间。
     * 
     * @supply1 数组的大小不能预先设定过大，那会造成空间的浪费，影响程序性能
     * @supply2 数组也不能设定小了，会造成键值对丢失，一定要足够键值对数据使用。
     * @param max
     */
    @SuppressWarnings("unchecked")
    public void resize(int max) {
        Key[] tempKeys = (Key[]) new Comparable[max];
        Value[] tempValues = (Value[]) new Object[max];
        for (int i = 0; i < top; i++) {
            tempKeys[i] = keys[i];
            tempValues[i] = values[i];
        }
        keys = tempKeys;
        values = tempValues;
    }

    public int size() {
        return top;
    }

    /**
     * 二分查找：在有序keys【从小到大排序】中找到key的下标
     * 
     * @notice 实际上同时也是二分排序
     * @param key
     * @return
     */
    public int getIndex(Key key) {
        int low = 0;
        int high = size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comp = key.compareTo(keys[mid]);
            if (comp > 0) // key比mid大，则只比较后一半
                low = mid + 1;
            else if (comp < 0) // key比mid小，则只比较前一半
                high = mid - 1;
            else
                return mid;
        }
        return low;// 当low=high+1的时候，依然没有找到相等的mid，就返回low【ceiling】。
    }

    /**
     * 查找到键则更新值，否则就创建一个新键值对
     */
    public void put(Key key, Value val) {
        int keyIndex = getIndex(key);
        if (keyIndex < top && keys[keyIndex].compareTo(key) == 0) {
            // 存在键下标且值相等，更新value
            values[keyIndex] = val;
            return;
        }
        // 如果数组空间不足以新增键值对元素，则扩充一倍，保证数组的利用率始终在25%以上。
        if (keys.length == size())
            resize(2 * keys.length);
        for (int j = top; j > keyIndex; j--) {
            // 插入，后面元素都往右窜一位，把keyIndex的位置空出来。
            keys[j] = keys[j - 1];
            values[j] = values[j - 1];
        }
        keys[keyIndex] = key;
        values[keyIndex] = val;
        top++;// 每次插入，指针向右移动一位。
    }

    @Override
    public Iterable<Key> keySet() {
        List<Key> list = new ArrayList<Key>();
        for (int i = 0; i < size(); i++) {
            list.add(keys[i]);
        }
        return list;
    }

    @Override
    public Value get(Key key) {
        int keyIndex = getIndex(key);
        if (keyIndex < top && key.compareTo(keys[keyIndex]) == 0) {
            // 如果keys中存在key的下标且值也相等，则返回values相同下标的值。
            return values[keyIndex];
        }
        return null;
    }

    @Override
    public void remove(Key key) {
        int keyIndex = getIndex(key);
        if (keyIndex == top - 1)// 删除表尾键值对数据
            top--;
        if (keyIndex < top - 1 && keys[keyIndex].compareTo(key) == 0) {// 删除表头或表中键值对数据
            // 存在键下标且值相等
            for (int j = keyIndex; j < top - 1; j++) {// 注意这里循环的是数组下标，最大不能超过表尾数据（上面已处理删除表尾）
                // 删除，后面元素都往左窜一位，把keyIndex的位置占上去
                keys[j] = keys[j + 1];// 循环若能够到表尾[top-1]数据，j+1溢出。
                values[j] = values[j + 1];
            }
            top--;
            // 监测：如果键值对的空间等于数组的四分之一，则将数组减容至一半，保证数组的利用率始终在25%以上。
            if (size() == keys.length / 4) {
                resize(keys.length / 2);
            }
        }
    }

    /**
     * 实现针对有序列表的扩展接口的方法。
     */
    public Key min() {
        return keys[0];
    }

    public Key max() {
        return keys[top - 1];// 注意这里不是key.length-1，而是top-1，为什么？自己想一想
    }

    /**
     * 返回排名为k的Key
     * 
     * @notice 排名是从1开始的，数组下标是从0开始的
     * @param k
     * @return
     */
    public Key select(int k) {
        if (k > top || k < 0)// 越界（指的是键值对数据空间越界）
            return null;
        return keys[k - 1];
    }

    /**
     * 当key不在keys中时，向上取整获得相近的keys中的元素。
     * 
     * @param key
     *            要检索的key
     * @return
     */
    public Key ceiling(Key key) {
        int i = getIndex(key);
        return keys[i];
    }

    /**
     * 操作同上，只是向下取整
     * 
     * @param key
     * @return
     */
    public Key floor(Key key) {
        int i = getIndex(key);
        return keys[i - 1];
    }

    /**
     * delete, containKeys, isEmpty();方法均定义在ST中。
     */
}
