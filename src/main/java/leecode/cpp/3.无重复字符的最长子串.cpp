/*
 * @lc app=leetcode.cn id=3 lang=cpp
 *
 * [3] 无重复字符的最长子串
 */
#include <string>
#include <iostream>
#include <unordered_map>

using namespace std;
// @lc code=start
class Solution
{
public:
    int lengthOfLongestSubstring(string s)
    {
        int start(0), end(0), length(0), result(0);
        int sSize = s.length();
        while (end < sSize)
        {
            char tmp = s[end];
            for (int i = start; i < end; i++)
            {
                if (s[i] == tmp)
                {
                    start = i + 1;
                    length = end - start;
                    break;
                }
            }

            end++;
            length++;
            result = max(length, result);
        }
        return result;
    }
};
// @lc code=end
int main()
{
    Solution S;
    cout << S.lengthOfLongestSubstring("aabcakas") << endl;
    cout << "end" << endl;
}