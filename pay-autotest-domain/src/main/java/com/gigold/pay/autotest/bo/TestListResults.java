package com.gigold.pay.autotest.bo;

import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/7/19.
 */
public class TestListResults {
    int ifCount,mockCount,mockPassCount,ifPassCount,mockFaildCount,ifFaildCount;
    float ifPassRate,mockPassRate;
    List<String> jnrList;
    List<Map> interfaceInfo;
    List<Map> moduleInfo;
    List<Map> userInfo;
    int[] ifIdList;
    List<List<List<String>>> dates;

    public int[] getIfIdList() {
        return ifIdList;
    }

    public void setIfIdList(int[] ifIdList) {
        this.ifIdList = ifIdList;
    }

    public int getMockPassCount() {
        return mockPassCount;
    }

    public void setMockPassCount(int mockPassCount) {
        this.mockPassCount = mockPassCount;
    }

    public int getIfPassCount() {
        return ifPassCount;
    }

    public void setIfPassCount(int ifPassCount) {
        this.ifPassCount = ifPassCount;
    }

    public int getMockFaildCount() {
        return mockFaildCount;
    }

    public void setMockFaildCount(int mockFaildCount) {
        this.mockFaildCount = mockFaildCount;
    }

    public int getIfFaildCount() {
        return ifFaildCount;
    }

    public void setIfFaildCount(int ifFaildCount) {
        this.ifFaildCount = ifFaildCount;
    }

    public int getIfCount() {
        return ifCount;
    }

    public void setIfCount(int ifCount) {
        this.ifCount = ifCount;
    }

    public int getMockCount() {
        return mockCount;
    }

    public void setMockCount(int mockCount) {
        this.mockCount = mockCount;
    }

    public float getIfPassRate() {
        return ifPassRate;
    }

    public void setIfPassRate(float ifPassRate) {
        this.ifPassRate = ifPassRate;
    }

    public float getMockPassRate() {
        return mockPassRate;
    }

    public void setMockPassRate(float mockPassRate) {
        this.mockPassRate = mockPassRate;
    }

    public List<String> getJnrList() {
        return jnrList;
    }

    public void setJnrList(List<String> jnrList) {
        this.jnrList = jnrList;
    }

    public List<Map> getInterfaceInfo() {
        return interfaceInfo;
    }

    public void setInterfaceInfo(List<Map> interfaceInfo) {
        this.interfaceInfo = interfaceInfo;
    }

    public List<Map> getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(List<Map> moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    public List<Map> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<Map> userInfo) {
        this.userInfo = userInfo;
    }

    public List<List<List<String>>> getDates() {
        return dates;
    }

    public void setDates(List<List<List<String>>> dates) {
        this.dates = dates;
    }
}
