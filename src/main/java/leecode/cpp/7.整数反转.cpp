/*
 * @lc app=leetcode.cn id=7 lang=cpp
 *
 * [7] 整数反转
 */
#define INT_MAX 2147483647
#define INT_MIN (-INT_MAX - 1)
// @lc code=start
class Solution
{
public:
    int reverse(int x)
    {
        int rev(0);
        while (x != 0)
        {
            int pop = x % 10;
            if (rev > INT_MAX / 10 || (rev == INT_MAX / 10 && pop > 7))
                return 0;
            if (rev < INT_MIN / 10 || (rev == INT_MIN / 10 && pop < -8))
                return 0;
            rev = rev * 10 + pop;
            x /= 10;
        }
        return rev;
    }
};
// @lc code=end
