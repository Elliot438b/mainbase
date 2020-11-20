/*
 * @lc app=leetcode.cn id=2 lang=cpp
 *
 * [2] 两数相加
 */

#include <iostream>
#include <string>

using namespace std;
// @lc code=start
/**
 * Definition for singly-linked list.
 */
struct ListNode
{
    int val;
    ListNode *next;
    ListNode() : val(0), next(nullptr) {}
    ListNode(int x) : val(x), next(nullptr) {}
    ListNode(int x, ListNode *next) : val(x), next(next) {}
};

class Solution
{
public:
    ListNode *addTwoNumbers(ListNode *l1, ListNode *l2)
    {
        ListNode *head = new ListNode(-1);
        ListNode *node = head;
        int carry = 0;

        while (l1 || l2)
        {
            int val1 = 0, val2 = 0;

            if (l1)
            {
                val1 = l1->val;
                l1 = l1->next;
            }
            if (l2)
            {
                val2 = l2->val;
                l2 = l2->next;
            }

            int sum = val1 + val2 + carry;
            carry = sum / 10;
            node->next = new ListNode(sum % 10);
            node = node->next;
        }
        if (carry)
        {
            node->next = new ListNode(carry);
        }
        return head->next;
    }
};
// @lc code=end

int main()
{
    cout << "Hello, world!" << endl;
    ListNode *l1 = new ListNode(2, new ListNode(4, new ListNode(3)));
    ListNode *l2 = new ListNode(5, new ListNode(6, new ListNode(4)));
    Solution S;
    ListNode *result = S.addTwoNumbers(l1, l2);
    while (result != nullptr)
    {
        cout << result->val << endl;
        result = result->next;
    }
}