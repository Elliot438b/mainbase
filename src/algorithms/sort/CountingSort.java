package algorithms.sort;

public class CountingSort extends Sort {

    @Override
    protected int[] sort(int[] array) {
        countingSort(array);
        return array;
    }

    /*
     * 计数排序
     * 
     * @example [1,0,2,0,3,1,1,2,8]
     * 最大值是8，建立一个计数数组a[]统计原数组中每个元素出现的次数，长度为9(因为是从0到8)
     * 
     * @开始计数：第一个统计0的次数为2，则a[0]=2;第二个统计1的次数为3，则a[1]=3;第三个按照数组下标以此类推，最终获得一个统计数组。
     * 
     * @开始排序：因为按照统计数组的下标，已经是有顺序的，只要循环输出每个重复的数就可以了。
     */
    private void countingSort(int[] array) {
        int max = max(array);
        // 开始计数
        int[] count = new int[max + 1];
        for (int a : array) {
            count[a]++;
        }

        // 输出回原数组
        int k = 0;
        for (int i = 0; i < count.length; i++) {
            for (int j = 0; j < count[i]; j++) {
                array[k++] = i;
            }
        }
    }
}