package algorithms.stack;

import ioutil.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Stack<Item> implements Iterable<Item> {
    private int SIZE;
    private Node first;// 栈顶

    public Stack() {// 初始化成员变量
        SIZE = 0;
        first = null;
    }

    private class Node {
        private Item item;
        private Node next;
    }

    // 栈：往first位置插入新元素
    public void push(Item item) {
        Node temp = first;
        first = new Node();
        first.item = item;
        first.next = temp;
        SIZE++;
    }

    // 栈：从first位置取出新元素，满足LIFO，后进先出。
    public Item pop() {
        if (isEmpty()) throw new RuntimeException("Stack underflow");
        Item item = first.item;
        first = first.next;
        SIZE--;
        return item;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public int size() {
        return this.SIZE;
    }

    @Override
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            Node node = first;

            @Override
            public boolean hasNext() {
                return first != null;
            }

            @Override
            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();
                Item item = node.item;
                node = node.next;
                return item;
            }
        };
    }

    public static void main(String[] args){
        Stack<String> stack = new Stack<>();
        stack.push("heyheyhey");
        stack.push("howau");
        stack.push("231");
        StdOut.println(stack.SIZE);
        StdOut.println(stack.pop());
    }
}
