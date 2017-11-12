package algorithms.search.second;

/**
 * 红黑树
 * 
 * @author Evsward
 * @notice 红黑树也是二分查找树，直接继承BST，不用再去实现那两个接口了。
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
     * @notice 继承自BST的TreeNode，以期能够使用BST的公共方法。
     * @author Evsward
     *
     */
    private class Node extends TreeNode {
        private Key key;
        private Value val;
        private Node left, right;// 其左子右子结点
        private boolean color;// 指向该结点的链接的颜色，红或黑
        private int size;// 以该结点为根的树的结点的总数

        public Node(Key key, Value val, int size, boolean color) {
            super(key, val, size);
            this.key = key;
            this.val = val;
            this.size = size;
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
     *            根结点，判断其右链接是否为红，若为红，则左旋转
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
     *            根结点，判断其左链接是否为红，若为红，则右旋转
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
     *            结点
     */
    private void flipColor(Node x) {
        // 必须x颜色为黑，同时x的左右链接均为红色
        if (!isRed(x) && isRed(x.left) && isRed(x.right)) {
            x.left.color = BLACK;
            x.right.color = BLACK;
            x.color = RED;
        }
    }

}
