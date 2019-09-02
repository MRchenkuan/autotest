package com.gigold.pay.autotest.dao;

import com.gigold.pay.autotest.bo.TestList;

import java.util.List;

/**
 * Created by chenkuan
 * on 16/6/29.
 */
public interface IfSysTestListDAO {
    /**
     * 获取测试清单
     */
    TestList getTestListById(int listId);

    /**
     * 更新测试清单
     */
    void updateTestListString(TestList testList);

    List<TestList> getAllTestList();
}
