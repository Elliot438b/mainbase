/*
 * @lc app=leetcode.cn id=9 lang=cpp
 *
 * [9] 回文数
 */
#define INT_MAX 2147483647
#define INT_MIN (-INT_MAX - 1)
// @lc code=start
class Solution
{
public:
    bool isPalindrome(int x)
    {
        if (x < 0 || x % 10 == 0 && x != 0)
        {
            return false;
        }
        int rev(0);
        while (x > rev)
        {
            int pop = x % 10;
            if (rev > INT_MAX / 10 || (rev == INT_MAX / 10 && pop > 7))
                return 0;
            if (rev < INT_MIN / 10 || (rev == INT_MIN / 10 && pop < -8))
                return 0;
            rev = rev * 10 + pop;
            x /= 10;
        }
        return rev == x || x == rev / 10;
    }
};
// @lc code=end
