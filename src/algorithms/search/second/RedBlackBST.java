package algorithms.search.second;

/**
 * 红黑树
 * 
 * @author Evsward
 * @RedBlackTree 重写put,remove,提高BST效率。
 * @BST 其他接口直接复用BST
 * @param <Key>
 * @param <Value>
 */
public class RedBlackBST<Key extends Comparable<Key>, Value> extends BST<Key, Value> {

    private final static boolean RED = true;
    private final static boolean BLACK = false;

    private Node root;

    /**
     * 红黑树结点
     * 
     * @notice 继承自BST的TreeNode，以期能够复用BST的公共方法。
     * @author Evsward
     *
     */
    private class Node extends TreeNode {
        private Node left, right;// 其左子右子结点
        private boolean color;// 指向该结点的链接的颜色，红或黑

        public Node(Key key, Value val, int size, boolean color) {
            super(key, val, size);// 这三个属性直接复用父类即可，没有区别
            this.color = color;
        }
    }

    /**
     * 判断某结点的链接是否为红
     * 
     * @param n
     *            某结点
     * @return
     */
    private boolean isRed(Node n) {
        if (n == null)
            return false;// 如果是空结点，则为空链接，空链接默认为黑链接
        return n.color == true;
    }

    /**
     * 左旋转操作：右链接和根结点的互换。
     * 
     * @param x
     *            根结点，若其右链接为红同时左链接为黑，则左旋转
     * @return 左旋转后的树根结点
     */
    private Node rotateLeft(Node x) {
        Node right = x.right;
        if (!isRed(right))// 若右链接为黑，则原样返回。
            return x;
        x.right = right.left;// x.left不变，右链接改为指向原右子的左子
        right.left = x;// right.right不变，原来的right.left已经被根要走为其右子，那么现在right.left改为原根结点。
        right.color = x.color;// 其他color均不变，只修改right和x的互换颜色。
        x.color = RED;
        // 注意不要忘记right和x的size问题，也要互换。
        right.size = x.size;
        x.size = x.left.size + x.right.size + 1;
        return right;
    }

    /**
     * 右旋转操作：左链接和根结点的互换。
     * 
     * @param x
     *            根结点，若其左链接为红，且左链接的左链接也为红，则右旋转
     * @return 右旋转后的树根结点
     */
    private Node rotateRight(Node x) {
        Node left = x.left;
        if (!isRed(left))// 若左链接的颜色不为红，则原样返回。
            return x;
        // 链接修改
        x.left = left.right;
        left.right = x;
        // 颜色修改
        left.color = x.color;
        x.color = RED;
        // 大小修改
        left.size = x.size;
        x.size = x.right.size + x.left.size + 1;

        return left;
    }

    /**
     * 颜色转换
     * 
     * @param x
     *            根结点
     */
    private void flipColor(Node x) {
        // 若结点为黑且其左链接和右链接均为红，则颜色转换【插入操作】
        if (!isRed(x) && isRed(x.left) && isRed(x.right)) {
            x.left.color = BLACK;
            x.right.color = BLACK;
            x.color = RED;
        }
        // 若结点为红且其左链接和右链接均为黑，则颜色转换【删除操作】
        if (isRed(x) && !isRed(x.left) && !isRed(x.right)) {
            x.left.color = RED;
            x.right.color = RED;
            x.color = BLACK;
        }
    }

    public void put(Key key, Value val) {
        root = put(root, key, val);
        root.color = BLACK;// 树的根结点的链接特例为黑。
    }

    /**
     * 红黑树插入键的自我实现
     * 
     * @param x
     *            在红黑树某结点为根结点的子树中插入键
     * @param key
     * @param val
     * @return 插入键调整以后的树
     */
    private Node put(Node x, Key key, Value val) {
        // 插入操作
        if (x == null)
            return new Node(key, val, 1, RED);
        // 比较操作
        int comp = key.compareTo(x.key);
        if (comp < 0)
            put(x.left, key, val);// 插入到左子树
        else if (comp > 0)
            put(x.right, key, val);// 插入到右子树
        else// 修改当前结点的值
            x.value = val;
        // 修复操作【这一步是与BST不同的，其他的步骤均一致】
        if (isRed(x.right) && !isRed(x.left))
            rotateLeft(x);
        if (isRed(x.left) && isRed(x.left.left))
            rotateRight(x);
        if (isRed(x.left) && isRed(x.right))// 临时4-结点（包含三个键）
            flipColor(x);
        // 调整size
        x.size = size(x.left) + size(x.right) + 1;
        return x;

    }

    /**
     * 向2-3-4树插入一个键
     * 
     * @notice 在多进程可以同时访问同一棵树的应用中这个算法要优于2-3树。
     * @param x
     * @param key
     * @param val
     * @return
     */
    @SuppressWarnings("unused")
    private Node put234Tree(Node x, Key key, Value val) {
        if (isRed(x.left) && isRed(x.right))// 临时4-结点（包含三个键）
            flipColor(x);
        // 插入操作
        if (x == null)
            return new Node(key, val, 1, RED);
        // 比较操作
        int comp = key.compareTo(x.key);
        if (comp < 0)
            put(x.left, key, val);// 插入到左子树
        else if (comp > 0)
            put(x.right, key, val);// 插入到右子树
        else// 修改当前结点的值
            x.value = val;
        // 修复操作【这一步是与BST不同的，其他的步骤均一致】
        if (isRed(x.right) && !isRed(x.left))
            rotateLeft(x);
        if (isRed(x.left) && isRed(x.left.left))
            rotateRight(x);
        // 调整size
        x.size = size(x.left) + size(x.right) + 1;
        return x;

    }

    public void deleteMin() {
        // 如果根结点的两个子结点均为黑，则需要将根结点置为红，以方便后续升4结点时不会破坏黑路径等值
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMin(root);
        // 如果树不为空，树的根结点的链接特例为黑。
        if (root.size > 0)
            root.color = BLACK;
    }

    /**
     * 局部构建，将结点x中最小键调整为红
     * 
     * @param x
     * @return
     */
    private Node moveRedLeft(Node x) {
        flipColor(x);// 改x的双子为红
        if (isRed(x.right.left)) {// 如果右子的左链接为红
            x.right = rotateRight(x.right);
            // TODO
            x = rotateLeft(x);// ？？？
        }
        return x;
    }

    /**
     * 局部红黑树的删除
     * 
     * @notice 最小键一定要与红色沾边（也就是3-结点键的一部分），否则会破坏红黑树的结构
     * @question 为什么是左子？因为最小键一定是左子。
     * @step1 判断左子是否与红“沾边”：要么本身是红色，要么他的左子是红色。
     * @step2 如果左子不与红沾边，需要先调整moveRedLeft树让其与红沾边
     * @step3 左子满足与红沾边以后，开始递归删除。
     * @param x
     *            待删除最小键的树的根结点
     * @return
     */
    private Node deleteMin(Node x) {
        if (x.left == null)// 根结点x没有左子了，x已经是最小的了，那就删除根结点x，返回空树
            return null;
        // 当前结点的最小键为x.left，它如果不是3-结点key的一部分（大的那一个或者小的那一个，左子或左子左链接至少有一个为红）。则需要构建让其成为3结点的一部分。
        // 如果左子只是3-结点的一部分即可，为什么不是左子的右链接为红？因为如果局部树的范围扩大到左子的左右链接，那么最小键就变成了左子的左子，跟左子的右链接没关系。
        // 为什么还要管左子的左子，局部树为什么要扩大一级？因为如果左子的左子是红链接，这时候只因为左子是黑就去调整树，会将左子变成4-结点。
        // 为什么局部树不继续扩大一级？高深解答是：因为再扩大一级，对应的是监测5-结点，而5-结点是可以由3-结点+2-结点换过来，所以不必要规避这种情况。
        // 平民解答是： 第三级会在递归调用时检查。
        if (!isRed(x.left) && !isRed(x.left.left))
            x = moveRedLeft(x);
        // 调整结束，最小键x.left已经与红沾边，开始删除
        x.left = deleteMin(x.left);
        // 删除完毕，开始构建会红黑树结构。
        return balance(x);
    }

    /**
     * put插入的后五行代码，用来修复红黑树。
     * 
     * @param x
     * @return
     */
    private Node balance(Node x) {
        if (isRed(x.right))
            x = rotateLeft(x);
        if (isRed(x.right) && !isRed(x.left))
            rotateLeft(x);
        if (isRed(x.left) && isRed(x.left.left))
            rotateRight(x);
        if (isRed(x.left) && isRed(x.right))
            flipColor(x);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    public void deleteMax() {
        root = deleteMax(root);
    }

    private Node deleteMax(Node x) {
        // TODO
        return null;
    }

    public void remove(Key key) {
        root = remove(root, key);
    }

    private Node remove(Node x, Key key) {
        // TODO
        return null;
    }

    /**
     * 以下方法内部只需调用父类方法即可
     * 
     * @notice 因为要对红黑树的根root操作，所以复写下面方法是必要的。
     */
    public Value get(Key key) {
        return get(root, key);
    }

    public int size() {
        return size(root);
    }

    public Iterable<Key> keySet() {
        return keySet(root);
    }

    public Key min() {
        return min(root).key;
    }

    public Key max() {
        return max(root).key;
    }

    public Key select(int k) {
        return selectNode(root, k).key;
    }

    public int getRank(Key key) {
        return getRank(root, key);
    }

    public Key ceiling(Key key) {
        TreeNode x = ceiling(root, key);// 最终也没找到比它大的，这个key放在表里面是最大的
        if (x == null)
            return null;
        return x.key;
    }

    public Key floor(Key key) {
        TreeNode x = floor(root, key);
        if (x == null)// 最终也没找到比它小的，这个key放在表里面是最小的
            return null;
        return x.key;
    }

}
