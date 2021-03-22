/*
 * @lc app=leetcode.cn id=20 lang=cpp
 *
 * [20] 有效的括号
 */

#include <string>
#include <vector>
#include <iostream>
#include <unordered_map>
#include <stack>
#include <stdio.h>
using namespace std;
// @lc code=start
class Solution
{
public:
    bool isValid(string s)
    {
        int n = s.size();
        if (n % 2 == 1)
        {
            return false;
        }

        unordered_map<char, char> pairs{
            {')', '('}, {']', '['}, {'}', '{'}};
        stack<char> stk;
        for (char ch : s)
        {
            if (pairs.count(ch))
            {
                if (stk.empty() || stk.top() != pairs[ch])
                {
                    return false;
                }
                stk.pop();
            }
            else
            {
                stk.push(ch);
            }
        }
        return stk.empty();
    }
};
// @lc code=end
int main()
{
    string s = "{()}[";
    stack<char> stk;
    unordered_map<char, char> pairs{
        {')', '('}, {']', '['}, {'}', '{'}};
    Solution S;
    bool ret = S.isValid(s);
    cout << s << endl;
    cout << ret << endl;
    cout << pairs[']'] << endl;
    cout << pairs.count(']') << endl;
    cout << stk.empty() << endl;
}