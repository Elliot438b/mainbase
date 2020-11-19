package leecode.twosum;

public class Solution {
	public int[] twoSum(int[] nums, int target) {
		int[] result = new int[2];
		for (int i = 0; i < nums.length; i++) {
			int minus = target - nums[i];
			for (int j = i + 1; j < nums.length; j++) {
				if (nums[j] == minus) {
					result[0] = i;
					result[1] = j;
					return result;
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		Solution s = new Solution();
		int[] nums = { 2, 7, 11, 15 };
//		int[] nums = { 3, 2, 4 };
//		int target = 6;
		int target = 9;
		System.out.println(s.twoSum(nums, target)[0]);
		System.out.println(s.twoSum(nums, target)[1]);
	}
}
