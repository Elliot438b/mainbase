> 关键字：排序算法，时间复杂度，空间复杂度，对数函数


排序就是研究如何将一系列数据按照某种逻辑顺序重新排列的一门算法。在计算机早期，排序要占用大量计算资源是人们的共识，而今天随着机器性能的提高，以及排序算法的演进，排序已经非常搞笑，现在随处都会提起数据的重要性，而整理数据的第一步就是排序。
> 引用自[知乎](https://www.zhihu.com/question/66519221/answer/243013874)

> 很多东西的难度，是随着需求变化的。比如排序吧，10个数字，我可以给你人眼排序，
100个可以冒泡排序，学过c语言的大一学生，就能干，免费。100T的数字呢？你给我冒个泡试试？量变产生了质变，数据量的增大，让本来可用的算法变得不可用，因为你找不到100T这么大内存，n2复杂度的冒泡排序让排序时间变得不可接受。100T数据排序已经是各大公司炫耀技术的方式了。腾讯打破2016 Sort Benchmark 4项纪录，98.8秒完成100TB数据排序，现在你告诉我，排序这个事儿简不简单？

所以，排序是基础，每一名优秀的程序员都需要熟悉掌握，今天我来总结一下。

首先介绍几个基础概念：

- 对数函数
 
在数学中用log表示，

```math
log2^8 = 3
```
其中8是真数，2是对数的底，3是对数。我么都知道2的3次方等于8，对数函数相当于求2的几次方等于8？

对数函数的表示还有几个特殊情况，当底为10时，log可以表示为lg，同时省略底10
```math
lg100 = 2
```
称以无理数e（e=2.71828...）为底的对数称为自然对数（natural logarithm），并记为ln。

然后继续来说，如何计算堆排序的时间复杂度。
- 时间复杂度

时间复杂度是定性的描述了一段程序的运行时间，
> 算法中某个特定步骤的执行次数/对于总执行时间的估算成本，随着「问题规模」的增大时，增长的形式。

也就是说时间复杂度越高，它的执行时间一定越久，使用大写字母O来表示。

在长度为N的数组中，一个遍历就是O(N)，嵌套两个遍历就是 O(N^2)， 同样的嵌套三个遍历就是O(N^3 )，而若程序中有一个嵌套两个遍历，还要一个嵌套三个遍历，那就是O(N^2) + O(N^3) 当问题规模增大到无限大的时候，较小的分子一方可以忽略，按照数量级大的来，仍旧是O(N^3) 。而如果有二分，那就是O(log2^N) ，如果一个遍历嵌套一个二分，则是O(N*log2^N)。
- 空间复杂度

空间复杂度是指算法在执行过程中临时占用内存的量度，算法的效率要通过时间复杂度和空间复杂度共同定义。空间复杂度仍旧使用大写字母O来表示。一个算法的空间复杂度S(n)定义为该算法所耗费的存储空间，它也是问题规模n的函数。


下面进入代码阶段。

我们先创建一个java工程sort，然后创建一个抽象类Sort。代码如下：

```
package sort;

public abstract class Sort {
	protected int[] sort(int[] array) {
		return null;
	};

	/**
	 * 互换位置的方法
	 * @param array 要换位置的目标数组
	 * @param i 数组位置1
	 * @param j 数组位置2
	 * @return 换好位置以后的数组
	 */
	protected int[] swap(int[] array, int i, int j) {
		int t = array[i];
		array[i] = array[j];
		array[j] = t;
		return array;
	}
	
	protected void show(int[] array){
		for (int a : array) {
			System.out.println(a);
		}
		System.out.println("执行交换次数："+count);
	}
}

```
然后再创建一个客户端Client，用来调用算法。代码如下：

```
package sort;

public class Client {
	public static void main(String[] args) {
		int[] array = { 1, 12432, 534, 6, 4576, 47, 56, 8 };
		Sort s = new XXXSort();
		array = s.sort(array);
		s.show(array);
	}
}
```
其中XXXSort类就是一下我们要介绍的十种排序算法。
### 冒泡排序
简单来讲，就是从头拿到一个数与所有其他的数比较一番，遇到比他大的（或者比它小的）就互换位置，遍历一遍以后，第一个数已经换来换取换到了最适合它的位置，再比较第二个数，以此类推，一直到将所有的数均与其他数比较一番为止。

时间复杂度为：
```math
T(n) = O(n^2)
```
空间复杂度为：
```math
S(n) = O(1)
```
这里的空间复杂度只有在交换时的一个中转临时变量占用的内存空间，所以是O(1)。

具体实现代码如下：

```
package sort;

public class BubbleSort extends Sort {
	public int[] sort(int[] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length - 1; j++) {
				if (array[j] < array[j + 1]) {
					array = swap(array, j, j + 1);
				}
			}
		}
		return array;
	}
}

```
很简单，嵌套两层循环用来依次比较大小并互换位置。

### 选择排序
冒泡排序的优化，给每个位置选择最小值（或者最大值）。例如，第一个位置，遍历找出最小或者最大的一个放在这，然后是第二个位置，以此类推，找到这个值以后再进行交换，比起冒泡排序，降低了交换的次数，但是由于都是嵌套两层循环，时间复杂度相同，空间复杂度也为O(1)，原理同上。

```
package sort;

public class SelectSort extends Sort {
	public int[] sort(int[] array) {
		for (int i = 0; i < array.length - 1; i++) {// 控制交换的次数，最多交换n-1次。
			int maxIndex = i;
			for (int j = i + 1; j < array.length; j++) {
				if (array[j] > array[maxIndex]) {
					maxIndex = j;
				}
			}
			if (maxIndex != i) {// 找到当前位置后面最小值的位置，交换。
				swap(array, maxIndex, i);
			}
		}
		return array;
	}
}

```
选择排序执行的次数为：与下面的插入排序的计算方式相同，只不过选择排序是不断与当前位置后面的元素比较，而插入排序是不断地与当前元素前面的比。
### 插入排序
上面两种都是用交换位置的方式，而插入排序是用插入的方式。具体来说就是，例如打扑克牌摸牌阶段时的码牌动作，第一张摸过来，不动，第二张摸过来，跟第一张比较一下，如果比它大就插到第一张的前面，第三张摸过来，先跟第二张比较一下，如果比它大就再跟第一张牌比较，如果比它大就插到第一张牌的前面（前面再也没有牌了，不用比了，就是下面代码中的j>0）。而如果不比第一张牌大，就保留插入到第二张的前面，而此时第二张在它与刚摸到的这张牌比较完的时候就已经成为了第三个位置的牌了。以此类推，代码如下。

```
package sort;

public class InsertSort extends Sort {
	public int[] sort(int[] array) {
		// 从第二张牌开始比较
		for (int i = 1; i < array.length; i++) {
			int target = array[i];
			int j = i;
			// 如果比前一个大，就把前一个放到当前目标牌的位置，把前一个的位置空出来，然后继续跟更前一个比较，循环去找到最准确的目标位置
			while (j > 0 && target > array[j - 1]) {
				array[j] = array[j - 1];
				j--;
			}
			// 在目标位置的插入操作
			array[j] = target;
		}
		return array;
	}
}

```
插入排序执行的次数为：按照最坏的打算，也就是每一次跟前面的牌比较都是要比到第一位为止，也就是说本身数组就是一个有序数组，利用该排序更换排序顺序。假设数组的长度为N，第一层循环的第一次操作是执行1次，第二次操作是执行2次，直到第N-1次操作是执行N-1次，那么总次数为一个等差数列求和，即N*(N-1)/2，当问题规模扩大到无穷大时，小数量级的加减可以忽略，同时原分母2可以忽略不计，最终时间复杂度仍旧为O(N^2)，空间复杂度也为O(1)，原理同上。
### 快速排序
快速排序是最流行的排序算法，效率是比较高的。它是基于冒泡排序，但略比冒泡排序复杂，基本思想为分割法，将一个数组按照一个基准数分割，比基准数小的放基准数的右边，大的放在左边。这就需要定义两个坐标分别从数组的两头开始比较换位，最终在数组的中间位置相遇，然后在基准数的左边和右边再递归执行这个分割法即可，代码如下。

```
package sort;

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

```
快速排序法就是集合了冒泡、二分分治和递归的思想。

时间复杂度为：

```math
T(n) = O(n*log2^n)
```

快速排序使用了二分法，如果恰好每一次分割都正好将基准数摆在了中央位置，也就是恰好对半分，这时它的第二层嵌套次数为log2^N ，所以完整的时间复杂度为O(n*log2^n)。 而当每一次二分操作，有一侧只分到了一个元素，而另一侧是N-1个元素，那就是最坏的情况，即为第二层嵌套的次数仍为N，那么时间复杂度就是O(N^2)
快速排序的空间复杂度很高，因为要二分分治，会占用log2^N 的临时空间去操作，同时还有快排的递归是与数组的长度相同，所以最终快速排序的空间复杂度为：

```math
S(n) = O(n*log2^n)
```

切分要占用N个新的临时空间，排序比较又要占用log2^N ，所以所以完整的空间复杂度为O(n*log2^n)。

### 堆排序

- 先来介绍堆的定义

这里的堆指的是数据结构中的“二叉堆”。二叉堆一般是通过数组来表示，每个元素都要保证大于等于另两个特定位置的元素。转化成二叉树的结构就是一个完全二叉树，每个节点都要小于等于它的父节点，这种结构就叫做“大顶堆”，也叫“最大堆”，而反过来，每个节点都要大于等于它的父节点，这就是“小顶堆”，也叫“最小堆”。

- 再说一下堆的特性

在一个长度为N的堆中，位置k的节点的父节点的位置为k/2，而它的两个子节点的位置则分别为2k和2k+1，而该堆总共有N/2个父节点。
- 修复堆有序的操作

当堆结构中出现了一个打破有序状态的节点，若它是因为变得比他的父节点大而打破，那么就要通过将其互换到更高的位置来修复堆，这个过程叫做由下而上的堆有序化，也叫“上浮”。反过来，就是由上至下的堆有序化，也叫“下沉”。

- 堆排序的原理

根据堆的结构来看，就像一个三角形，根节点是唯一一层仅有一个数的节点，而同时它又必然是最大或者最小的一个。那么将该根节点取出来，然后修复堆，再取出修复后的根节点，以此类推，最终就会得到一个有序的数组。所以，修复堆和构建堆的工作是相似的。

- 堆排序的工作

堆排序要解决两个问题：

1. 将无序数组中构造出来一个堆
2. 堆有序操作，上浮或者下沉操作
- 利用图片来解释一下：
![image](https://github.com/evsward/mainbase/blob/master/resource/image/sort/Heap_sort.png?raw=true)
- 代码如下：

```
package sort;

public class HeapSort extends Sort {
	/**
	 * 大顶堆，下沉操作，与上浮操作二选一
	 * 
	 * @param array
	 * @param k
	 *            指定位置的数array[k]作为下沉target
	 * @param maxIndex
	 *            由于是按最大下标循环递减处理，所以maxIndex一定要以参数的形式传入sink方法
	 */
	private void sink(int[] array, int k, int maxIndex) {
		int target = array[k];
		int j = 2 * k + 1;// left child index
		while (j <= maxIndex) {// 循环终止条件1：左子并不存在,说明target目前位置已为叶节点,无处可沉
			if (j + 1 <= maxIndex && array[j] < array[j + 1])// 如果有右子节点,且右子大于左子
				j++;
			if (target > array[j])
				break;// 循环终止条件2：target目前位置已是处于堆有序,无处可沉,也就是说比他的子节点（一个或者两个子节点）都大
			swap(array, k, j);// 下沉
			k = j;// target的位置k已经换到了j
			j = 2 * k + 1;// target的位置k变了,重新找左子,继续循环
		}
	}

	/**
	 * 小顶堆，最后是从大到小排列 上浮操作
	 * 
	 * @param array
	 * @param k
	 * @param maxIndex
	 */
	private void swim(int[] array, int k, int maxIndex) {
		int target = array[k];
		int j = 2 * k + 1;
		while (j <= maxIndex) {
			if (j + 1 <= maxIndex && array[j] > array[j + 1])
				j++;
			if (target < array[j])
				break;
			swap(array, k, j);
			k = j;
			j = 2 * k + 1;
		}
	}

	@Override
	protected int[] sort(int[] array) {
		int maxIndex = array.length - 1;
		// 构造堆
		int lastParentIndex = (maxIndex - 1) / 2;// 最后一个父节点位置
		for (int i = 0; i <= lastParentIndex; i++) {// 从根节点开始循环所有父节点
			swim(array, i, maxIndex);
		}
		// 堆有序
		while (maxIndex > 0) {// 不断取出数组中最大或者最小的元素
			swap(array, 0, maxIndex);
			maxIndex--;
			swim(array, 0, maxIndex);
		}
		return array;
	}

}

```
根据以上代码总结一下：
我们使用的数组举例如:{1, 12432, 47, 534, 6, 4576, 47, 56, 8}

1. 将这些数组按原有顺序摆成完全二叉树的形式（注意二叉树，完全二叉树，满二叉树的定义，条件是逐渐苛刻的，完全二叉树必须每个节点都在满二叉树的轨迹上，而深度为N的满二叉树必须拥有2N-1个节点，不多不少。）

2. 将该二叉树转换成二叉堆，最大堆或者最小堆均可
3. 取出当前数组最大或者最小的元素，然后用最后一个位置的元素来作为根节点，打破了二叉堆的有序状态。
4. 堆有序修复
5. 重复3.4步，直到数组全部元素被取完为止

堆排序的时间复杂度为：

```math
T(n) = O(n*log2^n)
```
空间复杂度也为O(1)，原理同上。
### 希尔排序
希尔是个人，是希尔排序的发明者。

这是我觉得非常精巧的方案。

首先用图来表示一下希尔排序的中心思想：
![image](https://github.com/evsward/mainbase/blob/master/resource/image/sort/shell_sort.png?raw=true)

代码如下：

```
package sort;

public class ShellSort extends Sort {

	@Override
	protected int[] sort(int[] array) {
		int lastStep = 0;// 控制循环次数，保存上一个step，避免重复
		for (int d = 2; d < array.length; d++) {
			int step = array.length / d;
			if (lastStep != step) {
				lastStep = step;
			} else {
				break;
			}
			System.out.println(step);// 监控step，shellSort执行次数
			shellSort(array, step);
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


```
希尔排序的分析是复杂的，时间复杂度是所取增量的函数，这涉及一些数学上的难题。但是在大量实验的基础上推出当n在某个范围内时，时间复杂度可以达到O(n^1.3)，，空间复杂度也为O(1)，原理同上。
### 归并排序
归并排序的操作有些像快速排序，只不过归并排序每次都是强制从中间分割，递归分割至不可再分（即只有两个元素），将分割后的子数组进行排序，然后相邻的两个子数组进行合并，新建一个数组用来存储合并后的有序数组并重新赋值给原数组。
```
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
```
归并排序的空间复杂度很高，因为它建立了一个与原数组同样长度的辅助数组，同时要对原数组进行二分分治，所以空间复杂度为

```math
S(n) = O(n*log2^n)
```
时间复杂度比较低，与空间复杂度的计算方式差不多，也为O(n*log2^n) 。

归并排序是一种渐进最优的基于比较排序的算法。但是它的空间复杂度很高，同时也是不稳定的，当遇到最坏情况，也即每次比较都发生数据移动时，效率也不高。

### 

