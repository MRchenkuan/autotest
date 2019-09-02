package com.gigold.pay.autotest.reqDto;

import com.gigold.pay.autotest.bo.IfSysFeildRefer;
import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;

import java.util.List;

/**
 * Created by chenkuan
 * on 16/3/8.
 */
public class IfSysFieldReferListReqDto extends RequestDto {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private List<IfSysFeildRefer>  referList;

    public String validation(){
        return SysCode.SUCCESS;
    }

    public List<IfSysFeildRefer> getReferList() {
        return referList;
    }

    public void setReferList(List<IfSysFeildRefer> referList) {
        this.referList = referList;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
