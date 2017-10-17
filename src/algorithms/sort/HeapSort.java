package algorithms.sort;

public class HeapSort extends Sort {
	public int[] sort(int[] array) {
		int size = array.length;
		for (int k = size / 2 - 1; k >= 0; k--) {// 从最后一个父节点往回遍历所有父节点，直到根节点
			// sink(array, k);
			swim(array, k);
		}
		while (size > 1) {
			swap(array, 0, --size);// 交换堆顶和最后一个元素，即每次将剩余元素中的最大者放到最后面
			// sink(array, 0);
			swim(array, 0);
		}
		return array;
	}

	/**
	 * 大顶堆 下沉操作目标： 沉到叶节点（最底了） 或者 沉到比两个子节点均大的父节点位置
	 * 
	 * @param array
	 *            “水里”
	 * @param k
	 *            指定位置的节点进行下沉操作
	 */
	private void sink(int[] array, int k) {
		int size = array.length;
		while (k <= size / 2 - 1) {// 父节点进，如果不满足说明已经是叶节点，则已达成目标1
			int i = 2 * k + 1;
			int fatherNode = array[k];// 对应两个子节点为左孩array[i]，右孩array[i + 1]
			if (i < size && array[i] < array[i + 1])
				i++;
			// 到这里，我们不确定左孩大还是右孩大，但是array[i]肯定是大孩
			if (fatherNode > array[i]) {
				break;// 父节点比大孩还大，已达成目标2
			} else {// 否则父节点和大孩交换位置。
				swap(array, i, k);
			}
		}
	}

	/**
	 * 小顶堆 上浮，当指定节点比父节点还小的时候，要互换位置，将小的移到上层。
	 * 
	 * @param array
	 * @param k
	 *            (k + 1) / 2： 为指定节点的父节点位置
	 */
	private void swim(int[] array, int k) {
		while (k > 0 && array[(k + 1) / 2] < array[k]) {
			swap(array, (k + 1) / 2, k);
			k = (k + 1) / 2;
		}
	}

}
