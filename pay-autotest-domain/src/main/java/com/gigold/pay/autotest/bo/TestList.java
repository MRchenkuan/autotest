package com.gigold.pay.autotest.bo;

import java.sql.Timestamp;

/**
 * Created by chenkuan
 * on 16/6/29.
 */
public class TestList {
    private int id;
    private String title,desc,excludeIdList,startTime,endTime,status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getExcludeIdList() {
        return excludeIdList;
    }

    public void setExcludeIdList(String excludeIdList) {
        this.excludeIdList = excludeIdList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
