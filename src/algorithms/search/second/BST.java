package algorithms.search.second;

import java.util.LinkedList;

import algorithms.search.SFunction;
import algorithms.search.SFunctionSorted;

/**
 * 二叉查找树，默认都是从小到大，从左到右排序
 * 
 * @notice 二叉查找树将用到大量递归，每个公有方法都对应着一个用来递归的私有方法
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class BST<Key extends Comparable<Key>, Value> implements SFunction<Key, Value>, SFunctionSorted<Key, Value> {
    private TreeNode root;// 定义一个根节点，代表了整个BST。

    private class TreeNode {
        private Key key;
        private Value value;
        private TreeNode leftChild;// 左链接：小于该结点的所有键组成的二叉查找树
        private TreeNode rightChild;// 右链接：大于该结点的所有键组成的二叉查找树
        private int size;// 以该结点为根的子树的结点总数

        // 构造函数创建一个根节点，不包含左子右子。
        public TreeNode(Key key, Value value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(TreeNode node) {
        if (node == null)
            return 0;
        return node.size;
    }

    @Override
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }

    /**
     * 递归函数：向根节点为x的树中插入key-value
     * 
     * @理解递归 把递归当作一个黑盒方法，而不要跳进这个里边
     * @核心算法
     * @param x
     * @param key
     * @param val
     * @return 插入key-value的新树
     */
    private TreeNode put(TreeNode x, Key key, Value val) {
        if (x == null)// 若x为空，则新建一个key-value结点。
            return new TreeNode(key, val, 1);// 初始化长度只为1。
        int comp = key.compareTo(x.key);
        if (comp < 0)
            x.leftChild = put(x.leftChild, key, val);
        if (comp > 0)
            x.rightChild = put(x.rightChild, key, val);
        if (comp == 0)// 如果树x已有key，则更新val值。
            x.value = val;
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    @Override
    public Iterable<Key> keySet() {
        return keySet(root);
    }

    private Iterable<Key> keySet(TreeNode x) {
        // TODO
        LinkedList<Key> list = new LinkedList<Key>();
        Iterable<Key> leftKeySet = keySet(x.leftChild);
        Iterable<Key> rightKeySet = keySet(x.rightChild);
        for (Key k : leftKeySet) {// 按照顺序，先add左边小的
            list.add(k);
        }
        list.add(x.key);// 按照顺序，再add中间的根节点
        for (Key k : rightKeySet) {// 按照顺序，最后add右边大的
            list.add(k);
        }
        return list;
    }

    @Override
    public Value get(Key key) {
        return get(root, key);
    }

    /**
     * 设置递归方法：在结点node中查找条件key
     * 
     * @核心算法
     * @param node
     * @param key
     * @return
     */
    private Value get(TreeNode node, Key key) {
        if (node == null)
            return null;// 递归调用最终node为空，未命中
        int comp = key.compareTo(node.key);
        if (comp < 0)
            return get(node.leftChild, key);
        if (comp > 0)
            return get(node.rightChild, key);
        return node.value;// 递归调用最终命中
    }

    @Override
    public void remove(Key key) {
        remove(root, key);
    }

    private TreeNode remove(TreeNode x, Key key) {
        // TODO
        if (x == null)// 若x为空，返回空
            return null;
        int comp = key.compareTo(x.key);
        if (comp < 0)
            x.leftChild = remove(x.leftChild, key);
        if (comp > 0)
            x.rightChild = remove(x.rightChild, key);
        if (comp == 0)// 命中，删除
            // x.value = val;
            x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    /**
     * 实现针对有序列表的扩展接口的方法。
     */

    private TreeNode min(TreeNode x) {
        if (x.leftChild == null)
            return x;
        return min(x.leftChild);
    }

    @Override
    public Key min() {
        return min(root).key;
    }

    private TreeNode max(TreeNode x) {
        if (x.rightChild == null)
            return x;
        return max(x.rightChild);
    }

    @Override
    public Key max() {
        return max(root).key;
    }

    @Override
    public Key select(int k) {
        // TODO
        int a = 0;
        for (Key key : keySet()) {
            if (a++ == k)
                return key;
        }
        return null;
    }

    @Override
    public Key ceiling(Key key) {
        TreeNode x = ceiling(root, key);// 最终也没找到比它大的，这个key放在表里面是最大的
        if (x == null)
            return null;
        return x.key;
    }

    /**
     * 向上取整，寻找与key相邻但比它大的key
     * 
     * @param x
     * @param key
     * @return
     */
    private TreeNode ceiling(TreeNode x, Key key) {
        if (x == null)
            return null;// 递归调用最终node为空，未找到符合条件的key
        int comp = key.compareTo(x.key);
        if (comp == 0)// 相等即返回，不必取整
            return x;
        if (comp > 0)// 比根节点大，则ceiling一定继续在右子树里面找
            return ceiling(x.rightChild, key);
        if (comp < 0) {// 比根节点小，则在左子树中尝试寻找比它大的结点作为ceiling
            TreeNode a = ceiling(x.leftChild, key);
            if (a != null)// 找到了就返回
                return a;
        }
        return x;// 没找到就说明只有根节点比它大，则返回根节点
    }

    @Override
    public Key floor(Key key) {
        TreeNode x = floor(root, key);
        if (x == null)// 最终也没找到比它小的，这个key放在表里面是最小的
            return null;
        return x.key;
    }

    /**
     * 向下取整，寻找与key相邻但比它小的key
     * 
     * @param x
     * @param key
     * @return
     */
    private TreeNode floor(TreeNode x, Key key) {
        if (x == null)
            return null;// 递归调用最终node为空，未找到符合条件的值
        int comp = key.compareTo(x.key);
        if (comp == 0)// 若相等，则没必要取整，直接返回即可
            return x;
        if (comp < 0)// 如果比根节点小，说明floor一定在左子树
            return floor(x.leftChild, key);
        if (comp > 0) {// 如果大于根节点
            TreeNode a = floor(x.rightChild, key);// 先查找右子树中是否有比它小的值floor
            if (a != null)// 找到了则返回
                return a;
        }
        return x;// 否则，最终只有根节店key比它小，作为floor返回。
    }

}
