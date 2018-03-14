package algorithms.graph;

import ioutil.StdOut;

import java.util.Stack;

public class DirectedCycle {
    private boolean[] marked;// 以顶点为索引，值代表了该顶点是否标记过（是否可达）
    private Stack<Integer> cycle; // 用来存储有向环顶点。
    // *****重点理解这里start****
    private int[] edgeTo;// edgeTo[0]=1代表顶点1->0, to 0的顶点为1。
    // *****重点理解这里end****
    private boolean[] onStack;// 顶点为索引，值为该顶点是否参与dfs递归，参与为true

    public DirectedCycle(Digraph digraph) {
        // 初始化成员变量
        marked = new boolean[digraph.V()];
        onStack = new boolean[digraph.V()];
        edgeTo = new int[digraph.V()];
        cycle = null;
        // 检查是否有环
        for (int v = 0; v < digraph.V(); v++) {
            dfs(digraph, v);
        }
    }

    private void dfs(Digraph digraph, int v) {
        onStack[v] = true;// 递归开始，顶点上栈
        marked[v] = true;
        for (int w : digraph.adj(v)) {// 遍历一条边，v-> w
            // 终止条件：找到有向环
            if (hasCycle()) return;
            // 使用onStack标志位来记录有效路径上的点，如果w在栈上，说明w在前面当了出发点，
            if (!marked[w]) {
                edgeTo[w] = v;// to w的顶点为v
                dfs(digraph, w);
            } else if (onStack[w]) {// 如果指到了已标记的顶点，且该顶点递归栈上。（栈上都是出发点，而找到了已标记的顶点是终点，说明出发点和终点相同了。）
                cycle = new Stack<Integer>();
                for (int x = v; x != w; x = edgeTo[x]) {//起点在第一次循环中已经push了，不要重复
                    cycle.push(x);// 将由v出发，w结束的环上中间的结点遍历push到cycle中。
                }
                cycle.push(w);// push终点
            }
        }
        onStack[v] = false;// 当递归开始结算退出时，顶点下栈。
    }

    public boolean hasCycle() {
        return cycle != null;
    }

    public Iterable<Integer> cycle() {
        return cycle;
    }

    public static void main(String[] args) {
        Digraph d = new Digraph(6);
        d.addEdge(0, 1);
        d.addEdge(1, 2);
        d.addEdge(2, 3);
        d.addEdge(5, 0);
        StdOut.println(d);
        DirectedCycle directedCycle = new DirectedCycle(d);
        if (directedCycle.hasCycle()) {
            for (int a : directedCycle.cycle()) {
                StdOut.println(a);
            }
        } else {
            StdOut.println("DAG");
        }
    }
}
