/**
 * Title: QueryDemoRequestDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.reqDto;

import com.gigold.pay.autotest.bo.InterFaceSysTem;
import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;

public class InterFaceSysRequestDto extends RequestDto {
    /** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private InterFaceSysTem InterFaceSysTem;

    /**
     * @return the interFaceSysTem
     */
    public InterFaceSysTem getInterFaceSysTem() {
        return InterFaceSysTem;
    }

    /**
     * @param interFaceSysTem
     *            the interFaceSysTem to set
     */
    public void setInterFaceSysTem(InterFaceSysTem interFaceSysTem) {
        InterFaceSysTem = interFaceSysTem;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
