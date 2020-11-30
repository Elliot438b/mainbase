/*
 * @lc app=leetcode.cn id=13 lang=cpp
 *
 * [13] 罗马数字转整数
 */

#include <string>

using namespace std;

// @lc code=start
class Solution
{
public:
    int romanToInt(string s)
    {
        int sum(0);
        for (int i(0); i < s.length(); i++)
        {
            int num = change(s[i]);
            if (i != (s.length() - 1))
            {
                if (change(s[i + 1]) > num)
                {
                    num = -num;
                }
            }
            sum += num;
        }
        return sum;
    }

    int change(char a)
    {
        switch (a)
        {
        case 'I':
            return 1;
            break;
        case 'V':
            return 5;
            break;
        case 'X':
            return 10;
            break;
        case 'L':
            return 50;
            break;
        case 'C':
            return 100;
            break;
        case 'D':
            return 500;
            break;
        case 'M':
            return 1000;
            break;
        }
        return 0;
    }
};
// @lc code=end
