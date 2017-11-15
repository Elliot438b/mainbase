package algorithms.search.STImpl;

import java.util.LinkedList;

import algorithms.search.SFunction;
import algorithms.search.SFunctionSorted;

/**
 * 二叉查找树，默认都是从小到大，从左到右排序
 * 
 * @notice 二叉查找树将用到大量递归，每个公有方法都对应着一个用来递归的私有方法
 * @see 每棵树由其根结点代表
 * @author Evsward
 *
 * @param <Key>
 * @param <Value>
 */
public class BST<Key extends Comparable<Key>, Value> implements SFunction<Key, Value>, SFunctionSorted<Key, Value> {
    private TreeNode root;// 定义一个根节点，代表了整个BST。

    protected class TreeNode {
        protected Key key;
        protected Value value;
        protected TreeNode leftChild;// 左链接：小于该结点的所有键组成的二叉查找树
        protected TreeNode rightChild;// 右链接：大于该结点的所有键组成的二叉查找树
        protected int size;// 以该结点为根的子树的结点总数

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

    protected int size(TreeNode node) {
        if (node == null)
            return 0;
        return node.size;
    }

    @Override
    public void put(Key key, Value val) {
        // 注意将内存root的对象作为接收，否则对root并没有做实际修改。
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
        else if (comp > 0)
            x.rightChild = put(x.rightChild, key, val);
        else if (comp == 0)// 如果树x已有key，则更新val值。
            x.value = val;
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    @Override
    public Iterable<Key> keySet() {
        return keySet(root);
    }

    protected Iterable<Key> keySet(TreeNode x) {
        if (x == null)
            return null;
        LinkedList<Key> list = new LinkedList<Key>();
        Iterable<Key> leftKeySet = keySet(x.leftChild);
        Iterable<Key> rightKeySet = keySet(x.rightChild);
        if (leftKeySet != null) {
            for (Key k : leftKeySet) {// 按照顺序，先add左边小的
                list.add(k);
            }
        }
        list.add(x.key);// 按照顺序，再add中间的根节点
        if (rightKeySet != null) {
            for (Key k : rightKeySet) {// 按照顺序，最后add右边大的
                list.add(k);
            }
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
    protected Value get(TreeNode node, Key key) {
        if (node == null)
            return null;// 递归调用最终node为空，未命中
        int comp = key.compareTo(node.key);
        if (comp < 0)
            return get(node.leftChild, key);
        else if (comp > 0)
            return get(node.rightChild, key);
        return node.value;// 递归调用最终命中
    }

    @Override
    public void remove(Key key) {
        // 注意将内存root的对象作为接收，否则对root并没有做实际修改。
        root = remove(root, key);
    }

    /**
     * 强制删除树x中key的键值对
     * 
     * @param x
     * @param key
     * @return
     */
    private TreeNode remove(TreeNode x, Key key) {
        if (x == null)// 若x为空，返回空
            return null;
        int comp = key.compareTo(x.key);
        if (comp < 0)
            // 从左子树中删除key并返回删除后的左子树
            // 这里不能直接返回，要执行下面的size重置。
            x.leftChild = remove(x.leftChild, key);
        else if (comp > 0)
            // 从右子树中删除key并返回删除后的右子树
            // 这里不能直接返回，要执行下面的size重置。
            x.rightChild = remove(x.rightChild, key);
        else {// 命中，删除
            if (x.leftChild == null && x.rightChild == null)// 说明树x只有一个结点
                return null;
            else if (x.rightChild == null)// 表尾删除
                x = x.leftChild;
            else if (x.leftChild == null)// 表头删除
                x = x.rightChild;
            else {// 表中删除，越过根节点x，重构二叉树
                  // 这是二叉树，不是数组，x.rightChild是右子树的根结点
                  // 要找出右子树的最小结点需要调用方法min(TreeNode x)
                TreeNode t = x;
                x = min(t.rightChild);// x置为右子树中的最小值，替换待删除x
                x.rightChild = deleteMin(t.rightChild);// 右子树为删除最小值（即当前x）以后的树
                x.leftChild = t.leftChild;// 左子树均不变
            }
        }
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    public void deleteMin() {
        // 注意将内存root的对象作为接收，否则对root并没有做实际修改。
        root = deleteMin(root);
    }

    /**
     * 删除树x中最小的键对应的键值对
     * 
     * @param x
     * @return 删除以后的树
     */
    private TreeNode deleteMin(TreeNode x) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;
        if (x.leftChild == null)
            return x.rightChild;// x.rightChild将x替换。x被删除
        x.leftChild = deleteMin(x.leftChild);// 否则在左子树中继续查找
        // ******注意处理size的问题，不要忘记******
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    public void deletMax() {
        // 注意将内存root的对象作为接收，否则对root并没有做实际修改。
        root = deleteMax(root);
    }

    /**
     * 删除树x中最大的键对应的键值对
     * 
     * @param x
     * @return 删除以后的树
     */
    private TreeNode deleteMax(TreeNode x) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;
        if (x.rightChild == null)
            return x.leftChild;// 越过x，返回x.leftChild。x被删除
        x.rightChild = deleteMax(x.rightChild);// 否则在右子树中继续查找
        // ******注意处理size的问题，不要忘记******
        x.size = size(x.leftChild) + size(x.rightChild) + 1;
        return x;
    }

    /**
     * 实现针对有序列表的扩展接口的方法。
     */

    protected TreeNode min(TreeNode x) {
        // 当结点没有左结点的时候，它就是最小的
        if (x.leftChild == null)
            return x;
        return min(x.leftChild);
    }

    @Override
    public Key min() {
        return min(root).key;
    }

    protected TreeNode max(TreeNode x) {
        // 当结点没有右结点的时候，它就是最小的
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
        // int a = 0;
        // for (Key key : keySet()) {
        // if (a++ == k)
        // return key;
        // }
        // return null;
        return selectNode(root, k).key;
    }

    /**
     * 获取在树x中排名为t的结点
     * 
     * @notice 位置是从0开始，排名是从1开始。
     * @param x
     * @param t
     * @return
     */
    protected TreeNode selectNode(TreeNode x, int t) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;
        if (x.leftChild.size > t && t > 0)// 左子树的size大于t，则继续在左子树中找
            return selectNode(x.leftChild, t);
        else if (x.leftChild.size == t)// 左子树的size与t相等，说明左子树的最大值就是排名t的结点
            return max(x.leftChild);
        else if (x.leftChild.size < t && t < x.size)// t比左子树的size大，且小于根节点的总size
            // 其实就是rightChild的范围，在右子树中寻找，排名为右子树中的排名，所以要减去左子树的size
            return selectNode(x.rightChild, t - x.leftChild.size - 1);// -1是因为要减去根结点
        else if (t == x.size)// 排名恰好等于结点的总size，说明排名为t的结点为最大结点，即有序表中的最后结点
            return max(x);
        else// 其他情况为t越界，返回null
            return null;
    }

    public int getRank(Key key) {
        return getRank(root, key);
    }

    /**
     * 获取key在树x中的排名，即位置+1，位置是从0开始，排名是从1开始。
     * 
     * @param x
     * @param key
     * @return
     */
    protected int getRank(TreeNode x, Key key) {
        if (x == null)// 一定要先判断null，避免空值异常
            return 0;
        int comp = key.compareTo(x.key);
        if (comp > 0)
            return getRank(x.rightChild, key) + x.leftChild.size + 1;
        else if (comp < 0)
            return getRank(x.leftChild, key);
        else
            return x.leftChild.size;
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
    protected TreeNode ceiling(TreeNode x, Key key) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;// 递归调用最终node为空，未找到符合条件的key
        int comp = key.compareTo(x.key);
        if (comp == 0)// 相等即返回，不必取整
            return x;
        else if (comp > 0)// 比根节点大，则ceiling一定继续在右子树里面找
            return ceiling(x.rightChild, key);
        else if (comp < 0) {// 比根节点小，则在左子树中尝试寻找比它大的结点作为ceiling
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
    protected TreeNode floor(TreeNode x, Key key) {
        if (x == null)// 一定要先判断null，避免空值异常
            return null;// 递归调用最终node为空，未找到符合条件的值
        int comp = key.compareTo(x.key);
        if (comp == 0)// 若相等，则没必要取整，直接返回即可
            return x;
        else if (comp < 0)// 如果比根节点小，说明floor一定在左子树
            return floor(x.leftChild, key);
        else if (comp > 0) {// 如果大于根节点
            TreeNode a = floor(x.rightChild, key);// 先查找右子树中是否有比它小的值floor
            if (a != null)// 找到了则返回
                return a;
        }
        return x;// 否则，最终只有根节店key比它小，作为floor返回。
    }

}
