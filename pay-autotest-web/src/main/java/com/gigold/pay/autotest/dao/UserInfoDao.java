package com.gigold.pay.autotest.dao;

import java.util.List;

import com.gigold.pay.autotest.bo.UserInfo;

public interface UserInfoDao {

    public UserInfo getUser(int id);

    public List<UserInfo> getList();

    public int addUser(UserInfo user);

    public UserInfo login(UserInfo user);

}
