package algorithms.graph;

import algorithms.bag.Bag;
import ioutil.StdOut;

/**
 * 基于深度优先搜索（Depth First Search）解答有向图顶点可达性问题。
 */
public class DigraphDFS {
    private boolean[] marked;// 是否标记过

    /**
     * 算法：在图中找到从某个顶点出发的所有顶点
     *
     * @param digraph
     * @param start
     */
    public DigraphDFS(Digraph digraph, int start) {
        marked = new boolean[digraph.V()];// 初始化marked数组
        dfs(digraph, start);
    }

    /**
     * 算法：在图中找到从某些顶点出发的所有顶点，这些顶点被作为一个集合传入。
     *
     * @param digraph
     * @param startSet
     */
    public DigraphDFS(Digraph digraph, Iterable<Integer> startSet) {
        marked = new boolean[digraph.V()];
        for (int w : startSet) {
            dfs(digraph, w);
        }
    }

    /**
     * 查询某个顶点是否被标记（是否可达，因为标记过就是可达的）
     *
     * @param v
     * @return
     */
    public boolean marked(int v) {
        return marked[v];
    }

    /**
     * 深度优先搜索核心算法，通过标记，在图中从v顶点出发找到有效路径
     * <p>
     * 返回的是通过标记形成的一条有效路径。
     *
     * @param digraph
     * @param v
     */
    private void dfs(Digraph digraph, int v) {
        marked[v] = true;// 标记起点可达。
        for (int w : digraph.adj(v)) {// 遍历v顶点可达的一级顶点。
            if (!marked[w]) dfs(digraph, w);// 如果发现w顶点未到达过，则继续从w开始dfs（即向前走了一步）
        }
    }

    public static void main(String[] args) {
        Digraph d = new Digraph(5);// 初始化五个顶点的图
        d.addEdge(0, 1);
        d.addEdge(1, 0);
        d.addEdge(2, 3);
        d.addEdge(0, 4);
        Bag<Integer> startSet = new Bag<>();
        startSet.add(2);
        DigraphDFS reachable = new DigraphDFS(d, startSet);
        for (int v = 0; v < d.V(); v++) {
            if (reachable.marked(v)) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }
        /**
         * 输出：
         *

         2
         3



         */
    }
}
