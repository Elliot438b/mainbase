package algorithms.sort;

public class MergeSort extends Sort {

    private int[] temp;

    @Override
    protected int[] sort(int[] array) {
        temp = new int[array.length];// 新建一个与原数组长度相同的空的辅助数组
        mergeSort(array, 0, array.length - 1);
        return array;
    }

    /**
     * 一到递归别迷糊：用于递归的方法MergeSort，而非merge
     * 
     * @param array
     * @param left
     * @param right
     */
    private void mergeSort(int[] array, int left, int right) {
        if (left >= right)// 已经分治到最细化，说明排序已结束
            return;
        int mid = (right + left) / 2;// 手动安排那个两情相悦的位置，强制为中间。ㄟ(◑‿◐ )ㄏ
        mergeSort(array, left, mid);// 左半部分递归分治
        mergeSort(array, mid + 1, right);// 右半部分递归分治
        merge(array, left, mid, right);// 强制安排两情相悦，就要付出代价：去插手merge他们的感情。( ͡° ͜ʖ°)✧
    }

    /**
     * 通过辅助数组，合并两个子数组为一个数组，并排序。
     * 
     * @param array
     *            原数组
     * @param left
     *            左子数组 [left, mid]；
     * @param mid
     *            那个被强制的两情相悦的位置。(ಠ .̫.̫ ಠ)
     * @param right
     *            右子数组 [mid+1, right]
     */
    private void merge(int[] array, int left, int mid, int right) {
        for (int k = left; k <= right; k++) {// 将区间[left,right]复制到temp数组中，这是强硬合并，并没有温柔的捋顺。
            temp[k] = array[k];
        }
        int i = left;
        int j = mid + 1;
        for (int k = left; k <= right; k++) {// 通过判断，将辅助数组temp中的值按照大小归并回原数组array
            if (i > mid)// 第三步：亲戚要和蔼，左半边用尽，则取右半边元素
                array[k] = temp[j++];// 右侧元素取出一个以后，要移动指针到其右侧下一个元素了。
            else if (j > right)// 第四步：与第三步同步，工作要顺利，右半边用尽，则取左半边元素
                array[k] = temp[i++];// 同样的，左侧元素取出一个以后，要移动指针到其右侧下一个元素了。
            else if (array[j] > temp[i])// 第一步：性格要和谐，右半边当前元素大于左半边当前元素，取右半边元素（从大到小排序）
                array[k] = temp[j++];// 右侧元素取出一个以后，要移动指针到其右侧下一个元素了。
            else// 第二步：与第一步同步，三观要一致，左半边当前元素大于右半边当前元素，取左半边元素（从大到小排序）
                array[k] = temp[i++];// 同样的，左侧元素取出一个以后，要移动指针到其右侧下一个元素了。
        }
    }
}