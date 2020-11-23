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
        int m = nums1.size();
        int n = nums2.size();
        int pivot1 = (m + 1) / 2;
        int pivot2 = (n + 1) / 2;
        int mleft = nums1[pivot1];
        int mright = nums1[pivot1 + 1];
        int nleft = nums2[pivot2];
        int nrignt = nums2[pivot2 + 1];
        if (mright < nleft)
        {
            int temp(mright);
            mright = nleft;
            nleft = temp;
        }

        return 0;
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