package algorithms.sort;

public class QuickSort extends Sort {
	public int[] sort(int[] array) {
		return quickSort(array, 0, array.length - 1);
	}

	// 分割的方法
	private int partition(int[] array, int left, int right) {
		int pivot = array[left];// 定义基准数
		int pivotIndex = left;// 保存基准数的位置

		while (left < right) {// 直到中间相遇为止
			while (left < right && array[right] <= pivot)// 在右侧找到第一个比基准数大的
				right--;
			while (left < right && array[left] >= pivot)// 在左侧找到第一个比基准数小的
				left++;
			swap(array, left, right);// 互换上面找到的第一个比基准数大的和第一个比基准数小的位置
		}
		swap(array, pivotIndex, left);// 最后交换基准数到中央位置。
		return left;
	}

	// 用于递归的方法
	private int[] quickSort(int[] array, int left, int right) {
		if (left >= right)// 递归的终止条件，这是必要的。
			return array;
		int pivotIndex = partition(array, left, right);// 初次分割
		quickSort(array, left, pivotIndex - 1);// 快速排序基准数左边的数组
		quickSort(array, pivotIndex + 1, right);// 快速排序基准数右边的数组
		return array;
	}
}
