package algorithms.sort;

public class HeapSort extends Sort {

    /**
     * 下沉操作，让k的值array[k]下沉到合适位置，往下走就要跟子节点比较
     * k位置的数值打破了大顶堆有序状态，说明其比子节点还小，这时就要将k与较大的子节点互换位置
     * （不用考虑比父节点大的问题，因为循环到检查父节点的时候，依旧可以采用其比子节点小的逻辑） 
     *            7 
     *           / \ 
     *         6    3 
     *        / \   / \ 
     *      4   5 1   2
     * 
     * @param array
     * @param k
     *            目标位置
     * @param right
     *            区间[k,right]
     */
    private void sink(int[] array, int k, int right) {
        // 循环终止条件1：左子并不存在,说明k目前已为叶节点,无处可沉
        while (2 * k + 1 <= right) {
            int bigChildIndex = 2 * k + 1;// left child index:2 * k + 1，right
                                          // child index:2 * k + 2
            // 如果有右子节点,且右子大于左子
            if (2 * k + 2 <= right && array[2 * k + 1] < array[2 * k + 2])
                bigChildIndex = 2 * k + 2;
            if (array[k] > array[bigChildIndex])
                // 循环终止条件2：k的值所处位置已堆有序,无处可沉,也就是说比他的子节点（一个或者两个子节点）都大
                break;
            swap(array, k, bigChildIndex);// 下沉，交换k和bigChildIndex
            k = bigChildIndex;// 位置k已经换到了bigChildIndex
        }
    }

    /**
     * 上浮操作：让目标位置的值上浮到合适位置，往上走就要跟父节点比较
     * k位置的数值打破了小顶堆有序状态，说明其比父节点还小，这时就要将k与其父节点互换位置
     * （不用考虑比子节点大的问题，因为循环到检查子节点的时候，依旧可以采用其比父节点小的逻辑）
     * 相对与下沉操作，上浮操作比较简略的原因是k只需要与一个父节点比较大小，而下沉操作则需要跟一个或两个子节点比较大小，多出的是这部分逻辑 
     *          1 
     *         / \
     *       2    5 
     *      / \   / \ 
     *    4   3 6   7
     * 
     * @param array
     * @param k
     *            区间[0,k]
     */
    private void swim(int[] array, int k) {
        if (k == 0)
            return;// k的位置已经是根节点了，不需要再上浮了。
        // @@@@
        // 终止条件:k不断往父节点一层层地爬或许能爬到根节点（k==0），或许中途k找到了比父节点大的位置，根据小顶堆规则，它就已经堆有序。
        while (k > 0 && array[k] < array[(k - 1) / 2]) {// k的父节点：(k - 1) / 2
            swap(array, k, (k - 1) / 2);// 上浮
            k = (k - 1) / 2;// k换到了它的父节点的位置
        }
    }

    /**
     * 堆排序：下沉堆排序 注意：通过下沉操作可以得到大顶堆也可以得到小顶堆，这里只采用一种情况来介绍。
     * 
     * @param array
     * @return 从小到大排序
     */
    @SuppressWarnings("unused")
    private int[] sinkSort(int[] array) {
        int maxIndex = array.length - 1;// 数组array，区间为 [0,maxIndex]
        // 构造堆
        int lastParentIndex = (maxIndex - 1) / 2;// 最后一个父节点位置
        // @@@@如果使用下沉操作，一定要从最后一个父节点开始往根节点倒序检查，才能保证最大值被送到根节点@@@@
        for (int i = lastParentIndex; i >= 0; i--) {// 区间[0,lastParentIndex]为当前数组的全部父节点所在
            sink(array, i, maxIndex);// 区间[lastParentIndex,maxIndex]，从最后一个父节点开始检查，下沉操作，调整堆有序
        }
        System.out.println("the max one is " + array[0]);
        // 获得排序（注意：堆有序！=堆排序，堆有序只能保证根节点是最值，而不能保证子节点及树枝节点同级间的大小顺序）
        while (maxIndex > 0) {
            swap(array, 0, maxIndex--);// 取出最大值
            sink(array, 0, maxIndex);// 修复堆
        }
        return array;
    }

    /**
     * 堆有序：通过上浮操作，使堆有序
     * 
     * @param array
     * @param len
     *            整理[0,len]区间的堆有序
     */
    private void headAdjustBySwim(int[] array, int len) {
        // @@@@如果使用上浮操作，一定要从最后一个叶节点开始，到根节点位置检查，才能保证最小值被送到根节点@@@@
        for (int i = len; i > 0; i--) {// i不需要检查=0的情况，因为根节点没有父节点了。
            swim(array, i);// 区间[0,i]，从最后一个叶节点开始检查，上浮操作，调整堆有序
        }
    }

    /**
     * 堆排序：上浮堆排序 注意：通过上浮操作可以得到大顶堆也可以得到小顶堆，这里只采用一种情况来介绍。
     * 
     * @param array
     * @return 从大到小排序
     */
    private int[] swimSort(int[] array) {
        int maxIndex = array.length - 1;// 数组array，区间为 [0,maxIndex]
        headAdjustBySwim(array, maxIndex);
        System.out.println("the min one is " + array[0]);
        // 获得排序（注意：堆有序！=堆排序，堆有序只能保证根节点是最值，而不能保证子节点及树枝节点同级间的大小顺序）
        while (maxIndex > 0) {
            swap(array, 0, maxIndex--);// 取出最小值
            headAdjustBySwim(array, maxIndex);
        }
        return array;
    }

    @Override
    public int[] sort(int[] array) {
        return swimSort(array);
    }

}