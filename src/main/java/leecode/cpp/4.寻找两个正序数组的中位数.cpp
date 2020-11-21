/*
 * @lc app=leetcode.cn id=4 lang=cpp
 *
 * [4] 寻找两个正序数组的中位数
 */
#include <vector>
#include <iostream>

using namespace std;

// @lc code=start
class Solution
{
public:
    double findMedianSortedArrays(vector<int> &nums1, vector<int> &nums2)
    {
        int start1(nums1[0]), start2(nums2[0]);
        int m = nums1.size();
        int n = nums2.size();
        vector<int> result(m + n);
        result[0] = max(nums1[0], nums2[0]);
        for (int i = 0; i < m + n; i++)
        {
            for (int j = 0; j < max(m, n); j++)
            {
                result[i] = min(min(nums1[j], nums2[j]), result[i]);
                result[i + 1] = max(nums1[j], nums2[j]);
                result[i + 2] = max(nums1[j], result[i]);
            }
        }
        int k = result.size();
        for (int h = 0; h < k; h++)
        {
            cout << result[h] << endl;
        }
        if (k % 2 == 1)
        {
            return result[k / 2];
        }
        else
        {
            int a = result[k / 2 - 1];
            int b = result[k / 2];
            return (a + b) / 2;
        }
    }
};
// @lc code=end
int main()
{
    vector<int> nums1 = {1, 3};
    vector<int> nums2 = {2};
    Solution S;
    cout << "End: " << S.findMedianSortedArrays(nums1, nums2) << endl;
}