package algorithms.search.second;

import algorithms.search.SFunction;
import algorithms.search.functions.ResizeArrayStack;

/**
 * 一对平行的可变长度数组ResizeArrayStack
 * 
 * @author Evsward
 * @notice 二分查找为Key有序：所以Key的泛型的边界为comparable，可比较的，否则无法判断是否有序
 * @param <Key>
 * @param <Value>
 */
public class BinarySearchST<Key extends Comparable<Key>, Value> implements SFunction<Key, Value> {
    private ResizeArrayStack<Key> keys;
    private ResizeArrayStack<Value> values;

    /**
     * @target 构造函数时强制Key有序，同时Value的值也要跟随变化，保证key-value的映射。
     * 
     * @卡壳处 无法根据Key数组创建一个等长的Value数组，因为泛型数组无法被创建。
     * @error Cannot create a generic array of Value
     */
    public BinarySearchST() {
        // Value a[] = new Value[10];// error
    }

    /**
     * @param key
     * @return key在有序Keys中的位置
     */
    private int rank(Key key) {
        int length = size();
        int low = 0;
        int high = length - 1;
        while (low < high) {
            int mid = (high - low) / 2 + low;
            int com = key.compareTo(keys.get(mid));
            if (com > 0) {// 返回一个正数，说明比mid值大。
                low = mid + 1;// 前一半不要了，直接跟后一半比较
            } else if (com < 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return 0;
    }

    @Override
    public void put(Key key, Value val) {
        keys.push(key);
        values.push(val);
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public Iterable<Key> keySet() {
        return keys;
    }

    @Override
    public Value get(Key key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(Key key) {
    }

}
