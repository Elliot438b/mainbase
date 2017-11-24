package algorithms.search.support;

import java.util.Iterator;

@SuppressWarnings("unchecked")
public class ResizeArrayStack<Item> implements Iterable<Item> {
    /**
     * 定义一个存放栈的数组
     * 
     * @注意 该数组要始终保持空间充足以供栈的使用，否则会造成栈溢出。
     */
    private Item[] target = (Item[]) new Object[1];
    private int top = 0;// 栈顶指针，永远指向最新元素的下一位

    // 判断栈是否为空
    public boolean isEmpty() {
        return top == 0;
    }

    // 返回栈的大小，如果插入一个元素，top就加1的话，当前top的值就是栈的大小。
    public int size() {
        return top;
    }

    /**
     * 调整数组大小，以适应不断变化的栈。
     * 
     * @supply 数组的大小不能预先设定过大，那会造成空间的浪费，影响程序性能
     * @param max
     */
    public void resize(int max) {
        Item[] temp = (Item[]) new Object[max];
        for (int i = 0; i < top; i++) {
            temp[i] = target[i];
        }
        target = temp;
    }

    /**
     * @step1 如果没有多余的空间，就给数组扩容
     * @step2 空间充足，不断压入新元素
     * @param i
     */
    public void push(Item i) {
        // 如果没有多余的空间，会将数组长度加倍，以支持栈充足的空间，栈永远不会溢出。
        if (top == target.length)
            resize(2 * target.length);// 扩充一倍
        target[top++] = i;// 在top位置插入新元素，然后让top向上移动一位
    }

    /**
     * 弹出一个元素，当弹出元素较多，数组空间有大约四分之三的空间空闲，则针对数组空间进行相应的减容
     * 
     * @return
     */
    public Item pop() {
        Item item = target[--top];// top是栈顶指针，没有对应对象，需要减一位为最新对象
        // 弹出的元素已被赋值给item，然而内存中弹出元素的位置还有值，但已不属于栈，需要手动置为null，以供GC收回。
        target[top] = null;
        // 如果栈空间仅为数组大小的四分之一，则给数组减容一半，这样可以始终保持数组空间使用率不低于四分之一。
        if (top > 0 && top == target.length / 4)
            resize(target.length / 2);
        return item;
    }

    public static void main(String[] args) {
        ResizeArrayStack<TestFun> clients = new ResizeArrayStack<TestFun>();
        for (int i = 0; i < 5; i++) {
            clients.push(new TestFun());
        }
        System.out.println("clients.size() = " + clients.size());
        // a.testST();
        for (TestFun c : clients) {
            System.out.println(c);
        }
    }

    @Override
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            private int i = top;

            @Override
            public boolean hasNext() {
                return i > 0;
            }

            @Override
            public Item next() {
                // top 没有值，减一位为最新的值。这里是用来迭代的方法，与弹出不同，不涉及事务性（增删改）操作
                return target[--i];
            }

        };
    }
}
