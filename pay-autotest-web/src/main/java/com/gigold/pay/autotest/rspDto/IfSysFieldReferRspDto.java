package com.gigold.pay.autotest.rspDto;

import com.gigold.pay.autotest.bo.IfSysFeildRefer;
import com.gigold.pay.framework.core.ResponseDto;


/**
 * Created by chenkuan
 * on 16/3/8.
 */
public class IfSysFieldReferRspDto extends ResponseDto {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    private IfSysFeildRefer feildRefer;


    public IfSysFeildRefer getRefer() {
        return feildRefer;
    }
    public void setRefer(IfSysFeildRefer feildRefer) {
        this.feildRefer = feildRefer;
    }
}
