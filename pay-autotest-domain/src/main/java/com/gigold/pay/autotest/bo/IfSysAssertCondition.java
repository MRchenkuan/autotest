package com.gigold.pay.autotest.bo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/6/23.
 */
@Component
@Scope("prototype")
public class IfSysAssertCondition {
    private List<Map> asType,asClass;

    public List<Map> getAsClass() {
        return asClass;
    }

    public void setAsClass(List<Map> asClass) {
        this.asClass = asClass;
    }

    public List<Map> getAsType() {
        return asType;
    }

    public void setAsType(List<Map> asType) {
        this.asType = asType;
    }
}
