package algorithms.search.second;

import java.util.ArrayList;
import java.util.List;

/**
 * 无序链表的顺序查找，大部分方法采用遍历链表的方式实现
 * 
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class SequentialSearchST<Key, Value> {
    private Node first;

    private class Node {
        Key key;
        Value val;
        Node next;

        public Node(Key key, Value val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    public void put(Key key, Value val) {
        // 遍历链表
        for (Node x = first; x != null; x = x.next) {
            // 找到key则替换value
            if (key.equals(x.key)) {
                x.val = val;
                return;
            }
        }
        // 如果未找到key，则新增
        first = new Node(key, val, first);
    }

    public int size() {
        int count = 0;
        for (Node x = first; x != null; x = x.next) {
            count++;
        }
        return count;
    }

    /**
     * 取出表中所有的键并存入可迭代的集合中
     * 
     * @return 可以遍历的都是Iterable类型，这里用List
     */
    public Iterable<Key> keys() {
        List<Key> list = new ArrayList<Key>();
        for (Node x = first; x != null; x = x.next) {
            list.add(x.key);
        }
        return list;
    }

    public Value get(Key key) {
        // 遍历链表
        for (Node x = first; x != null; x = x.next) {
            // 找到key则替换value
            if (key.equals(x.key)) {
                return x.val;
            }
        }
        return null;// 未找到则返回null
    }

    public void hardDelete(Key key) {
        if (contains(key)) {
            if (key.equals(first.key)) {// 删除表头结点
                first = first.next;
                return;
            }
            // 遍历链表
            for (Node x = first; x != null; x = x.next) {
                if (key.equals(x.next.key)) {
                    if (x.next.next == null) {// 删除表尾结点
                        x.next = null;
                        return;
                    }
                    x.next = x.next.next;// 删除表中结点
                }
            }
        }
    }

    /**
     * 下面的方法是固定的，不需要改动。
     */
    public void delete(Key key) {
        if (contains(key)) {
            put(key, null);
        }
    }

    public boolean contains(Key key) {
        return get(key) != null;
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
