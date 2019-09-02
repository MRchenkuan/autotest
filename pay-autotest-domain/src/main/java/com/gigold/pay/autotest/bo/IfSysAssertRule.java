package com.gigold.pay.autotest.bo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by chenkuan
 * on 16/5/19.
 */
@Component
@Scope("prototype")
public class IfSysAssertRule {
    int id,mockId;
    String asValue,asObj,asClass,asType,desc,status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMockId() {
        return mockId;
    }

    public void setMockId(int mockId) {
        this.mockId = mockId;
    }

    public String getAsValue() {
        return asValue;
    }

    public void setAsValue(String asValue) {
        this.asValue = asValue;
    }

    public String getAsObj() {
        return asObj;
    }

    public void setAsObj(String asObj) {
        this.asObj = asObj;
    }

    public String getAsClass() {
        return asClass;
    }

    public void setAsClass(String asClass) {
        this.asClass = asClass;
    }

    public String getAsType() {
        return asType;
    }

    public void setAsType(String asType) {
        this.asType = asType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
