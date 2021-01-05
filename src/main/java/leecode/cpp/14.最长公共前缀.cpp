/*
 * @lc app=leetcode.cn id=14 lang=cpp
 *
 * [14] 最长公共前缀
 */
#include <vector>
#include <string>
#include <unordered_map>

using namespace std;
// @lc code=start
class Solution
{
public:
    string longestCommonPrefix(vector<string> &strs)
    {
        unordered_map<char, int> counter;
        for (int i = 0; i < strs.size(); i++)
        {
            for (int j = 0; j < strs[i].length(); j++)
            {
                
            }
        }
    }
};
// @lc code=end
