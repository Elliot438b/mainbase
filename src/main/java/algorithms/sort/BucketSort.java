package algorithms.sort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BucketSort extends Sort {
    private List<List<Integer>> buckets = new ArrayList<List<Integer>>();

    private int optimizeDivisor = 0;

    private int bucketNum = 100;// 这个桶个数要提前定义
    private int divisor = 1;

    private int f(int n) {
        return n / divisor;
    }

    public int[] sort(int[] array) {
        bucketSort(array);
        System.out.println("divisor=" + divisor + ", 桶排序优化程度：" + optimizeDivisor);
        return array;
    }

    private void bucketSort(int[] arr) {
        divisor = max(arr) / bucketNum + 1;
        for (int i = 0; i < bucketNum; i++) {
            buckets.add(new LinkedList<Integer>());
        }

        for (int a : arr) {
            buckets.get(f(a)).add(a);
        }
        int k = arr.length - 1;
        for (int i = 0; i < bucketNum; i++) {
            if (!buckets.get(i).isEmpty()) {
                optimizeDivisor++;
                List<Integer> list = buckets.get(i);
                int[] bucket = new int[list.size()];
                for (int j = 0; j < list.size(); j++) {
                    bucket[j] = list.get(j);
                }
                Sort quickSort = new QuickSort();
                bucket = quickSort.sort(bucket);// 如果是从小到大排序，那就正序插入，反之从大到小则倒序插入
                for (int j = bucket.length - 1; j >= 0; j--) {
                    arr[k--] = bucket[j];
                }
            }
        }
    }

}