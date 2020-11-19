/*
 * @lc app=leetcode.cn id=1 lang=cpp
 *
 * [1] 两数之和
 */
#include <vector>
#include <iostream>
#include <string>
#include <unordered_map>

using namespace std;
// @lc code=start
class Solution
{
public:
    vector<int> twoSum(vector<int> &nums, int target)
    {
        int n = nums.size();
        for (int i = 0; i < n; ++i)
        {
            for (int j = i + 1; j < n; ++j)
            {
                if (nums[i] + nums[j] == target)
                {
                    return {i, j};
                }
            }
        }
        return {};
    }

    vector<int> twoSum2(vector<int> &nums, int target)
    {
        unordered_map<int, int> hashtable;
        int n = nums.size();
        for (int i = 0; i < n; ++i)
        {
            auto it = hashtable.find(target - nums[i]);
            if (it != hashtable.end())
            {
                return {it->second, i};
            }
            hashtable[nums[i]] = i;
        }
        return {};
    }
};
// @lc code=end

int main()
{
    vector<int> nums = {8, 3, 0, 4, 11, 2};
    short int target = 5;
    Solution S;
    vector<int> result = S.twoSum2(nums, target);
    cout << "hello, world!" << endl;
    cout << result[0] << endl;
    cout << result[1] << endl;
}