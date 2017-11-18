package algorithms.search.STImpl;

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

        /**
         * 以下两个set方法是必须的，因为要将每次set关联到基类的引用，以便于调用基类关于左右子的方法
         * 当然了这些方法肯定是与color无关的，跟color有关的都需要自己实现。
         */

        public void setLeft(Node left) {
            this.left = left;
            super.leftChild = left;// 关联到基类的引用
        }

        public void setRight(Node right) {
            this.right = right;
            super.rightChild = right;// 关联到基类的引用
        }

        @Override
        public String toString() {// 方便调试，可以直观看到树结构
            return "NODE key:" + this.key + " value:" + this.value + " size:" + this.size + " color:" + this.color
                    + " \n leftChild:" + this.left + " rightChild:" + this.right;
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
     * 左旋转操作：具体请看上方左旋示意图，有详细解释
     * 
     * @param h
     * @return 左旋转后的树根结点
     */
    private Node rotateLeft(Node h) {
        Node x = h.right;// 先将右子寄存
        h.setRight(x.left);
        x.setLeft(h);
        x.color = h.color;// 其他color均不变，只修改right和h互换颜色。
        h.color = RED;
        // 注意不要忘记right和h的size问题，也要互换。
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }

    /**
     * 右旋转操作：具体请看上方右旋示意图，有详细解释
     * 
     * @param h
     * @return 右旋转后的树根结点
     */
    private Node rotateRight(Node h) {
        Node x = h.left;// 寄存左子
        // 链接修改
        h.setLeft(x.right);// 断开链接，转移左子的右子
        x.setRight(h);
        // 颜色修改
        x.color = h.color;
        h.color = RED;
        // 大小修改
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;

        return x;
    }

    /**
     * 颜色转换，将双子改为黑，根改为红
     * 
     * @notice 插入操作时需要将红链接从下传到上。
     * @param x
     *            根结点
     */
    private void flipColor(Node x) {
        x.left.color = BLACK;
        x.right.color = BLACK;
        x.color = RED;
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
            return new Node(key, val, 1, RED);// 新键采用红链接，才不会打破红黑树的完美黑色平衡
        // 比较操作
        int comp = key.compareTo(x.key);
        if (comp < 0)
            x.setLeft(put(x.left, key, val));// 插入到左子树
        else if (comp > 0)
            x.setRight(put(x.right, key, val));// 插入到右子树
        else// 修改当前结点的值
            x.value = val;
        // 修复操作【这一步是与BST不同的，其他的步骤均一致】
        if (isRed(x.right) && !isRed(x.left))
            x = rotateLeft(x);
        if (isRed(x.left) && isRed(x.left.left))
            x = rotateRight(x);
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
            return new Node(key, val, 1, RED);// 新键采用红链接，才不会打破红黑树的完美黑色平衡
        // 比较操作
        int comp = key.compareTo(x.key);
        if (comp < 0)
            x.setLeft(put(x.left, key, val));// 插入到左子树
        else if (comp > 0)
            x.setRight(put(x.right, key, val));// 插入到右子树
        else// 修改当前结点的值
            x.value = val;
        // 修复操作【这一步是与BST不同的，其他的步骤均一致】
        if (isRed(x.right) && !isRed(x.left))
            x = rotateLeft(x);
        if (isRed(x.left) && isRed(x.left.left))
            x = rotateRight(x);
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
     * 局部构建，将结点x中双子改为红
     * 
     * @notice 删除的操作与插入是相反的，它需要将红链接从上传到下。
     * @param x
     * @return
     */
    private Node moveRed(Node x) {
        x.left.color = RED;
        x.right.color = RED;
        x.color = BLACK;
        return x;
    }

    /**
     * 局部红黑树的删除
     * 
     * @notice 最小键一定要与红色沾边（也就是3-结点键的一部分），否则删除一个2-结点会破坏红黑树的平衡
     *         （直接导致该2-结点被删除后接替的空链接到根节点路径上黑链接总数减一，与其他空链接不等)
     * @param x
     *            局部红黑树的根结点
     * @return 删除最小键且调整回红黑树的根结点
     */
    private Node deleteMin(Node x) {
        if (x.left == null)
            // x结点并无任何子结点，那么直接删除根结点，返回空树
            if (x.right == null)
            return null;
            // x结点还存在比它大的右子结点，那么删除根结点，返回右子结点
            else
            return x.right;
        // 首先x为根的局部树中，最小键肯定为x.left(x.left==null的情况上面已处理)。所以要对x.left是否与红沾边进行判断。
        // 在分析x.left结点的时候，局部树的范围是x.left,x.left.right和x.left.left三个结点，若想让x.left与红沾边，这三个结点任意一个为红链接即可满足。
        // 根据红黑树定义，初始情况下x.left.right不可能为红，所以只有判断当x.left和x.left.left都不为红时，对传入树进行调整。
        if (!isRed(x.left) && !isRed(x.left.left))
            x = moveRed(x);
        // 调整结束，最小键x.left已经与红沾边，开始删除
        x.setLeft(deleteMin(x.left));
        // 删除完毕，开始修复红黑树结构。
        return balance(x);
    }

    /**
     * 修复红黑树。
     * 
     * @param x
     * @return
     */
    private Node balance(Node x) {
        // 以下为三种属于2-3树而不属于红黑树的特殊情况
        if (isRed(x.right))
            x = rotateLeft(x);
        if (isRed(x.left) && isRed(x.left.left))
            x = rotateRight(x);
        if (isRed(x.left) && isRed(x.right))
            flipColor(x);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    public void deleteMax() {// 如果根结点的两个子结点均为黑，则需要将根结点置为红，以方便后续升4结点时不会破坏黑路径等值
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMax(root);
        // 如果树不为空，树的根结点的链接特例为黑。
        if (root.size > 0)
            root.color = BLACK;
    }

    private Node deleteMax(Node x) {
        if (x.right == null)
            // x结点并无任何子结点，那么直接删除根结点，返回空树
            if (x.left == null)
            return null;
            // x结点还存在比它小的左子结点，那么删除根结点，返回左子结点
            else
            return x.left;
        // 首先x为根的局部树中，最大键肯定为x.right(x.right==null的情况上面已处理)。所以要对x.right是否与红沾边进行判断。
        // 在分析x.right结点的时候，局部树的范围是x.right,x.right.right和x.right.left三个结点，若想让x.right与红沾边，这三个结点任意一个为红链接即可满足。
        // 根据红黑树定义，初始情况下x.right.right不可能为红，所以只有判断当x.right和x.right.left都不为红时，对传入树进行调整。
        if (!isRed(x.right) && !isRed(x.right.left))
            x = moveRed(x);
        // 调整结束，最大键x.right已经与红沾边，开始删除
        x.setRight(deleteMax(x.right));
        // 删除完毕，开始修复红黑树结构。
        return balance(x);
    }

    public void remove(Key key) {
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = remove(root, key);
        // 如果树不为空，树的根结点的链接特例为黑。
        if (root.size > 0)
            root.color = BLACK;
    }

    private Node remove(Node x, Key key) {
        if (key.compareTo(x.key) < 0) {
            if (!isRed(x.left) && !isRed(x.left.left))
                x = moveRed(x);
            x.setLeft(remove(x.left, key));
        } else {
            if (isRed(x.left))
                x = rotateRight(x);
            if (key.compareTo(x.key) == 0 && (x.right == null))
                return null;
            if (!isRed(x.right) && !isRed(x.right.left))
                x = moveRed(x);
            if (key.compareTo(x.key) == 0) {
                x.value = get(x.right, min(x.right).key);
                x.key = min(x.right).key;
                x.setRight(deleteMin(x.right));
            } else {
                x.setRight(remove(x.right, key));
            }
        }
        return balance(x);
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
