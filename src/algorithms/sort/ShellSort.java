package algorithms.sort;

public class ShellSort extends Sort {

	@Override
	protected int[] sort(int[] array) {
		int lastStep = 0;// 控制循环次数，保存上一个step，避免重复
		for (int d = 2; d < array.length; d++) {
			int step = array.length / d;
			if (lastStep != step) {
				lastStep = step;
				System.out.println("step: " + step);// 监控step，shellSort执行次数
				shellSort(array, step);
			} else {
				continue;
			}
		}
		return array;
	}

	private void shellSort(int[] array, int step) {
		for (int i = 0; i < array.length - step; i++) {
			if (array[i] < array[i + step]) {
				swap(array, i, i + step);
			}
		}
	}

}
