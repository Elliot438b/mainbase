package algorithms.sort;

public class MergeSort extends Sort {

	private int[] temp;

	@Override
	protected int[] sort(int[] array) {
		temp = new int[array.length];// 新建一个与原数组长度相同的空的辅助数组
		divide(array, 0, array.length - 1);
		return array;
	}

	private void divide(int[] array, int left, int right) {
		if (left >= right)// 分治到最细化
			return;
		int mid = (right + left) / 2;
		divide(array, left, mid);// 左半部分递归分治
		divide(array, mid + 1, right);// 右半部分递归分治
		merge(array, left, mid, right);
	}

	private void merge(int[] array, int left, int mid, int right) {
		// 左子数组 [left, mid]； 右子数组 [mid+1, right]
		int i = left;
		int j = mid + 1;
		for (int k = left; k <= right; k++) {// 将left至right复制到temp数组中
			temp[k] = array[k];
		}
		for (int k = left; k <= right; k++) {// 通过判断，将辅助数组中的值按照大小归并回原数组
			if (i > mid)// 左半边用尽，则取右半边元素
				array[k] = temp[j++];
			else if (j > right)// 右半边用尽，则取左半边元素
				array[k] = temp[i++];
			else if (array[j] > temp[i])// 右半边当前元素大于左半边当前元素，取右半边元素（从大到小排序）
				array[k] = temp[j++];
			else// 左半边当前元素大于右半边当前元素，取左半边元素（从大到小排序）
				array[k] = temp[i++];
		}
	}
}