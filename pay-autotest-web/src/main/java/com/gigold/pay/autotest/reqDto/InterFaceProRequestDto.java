/**
 * Title: QueryDemoRequestDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.reqDto;

import com.gigold.pay.autotest.bo.InterFacePro;
import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;

public class InterFaceProRequestDto extends RequestDto {
    private InterFacePro interFacePro;

    /**
     * @return the interFacePro
     */
    public InterFacePro getInterFacePro() {
        return interFacePro;
    }

    /**
     * @param interFacePro the interFacePro to set
     */
    public void setInterFacePro(InterFacePro interFacePro) {
        this.interFacePro = interFacePro;
    }


    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
