/**
 * Title: IfSysMockReqaDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.reqDto;


import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;

/**
 * Title: IfSysMockReqaDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * @author xiebin
 * @date 2015年11月30日上午11:39:51
 *
 */
public class IfSysMockDelReqDto extends RequestDto {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	//主键
	private int id;
	//接口ID
	private int ifId;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the ifId
	 */
	public int getIfId() {
		return ifId;
	}
	/**
	 * @param ifId the ifId to set
	 */
	public void setIfId(int ifId) {
		this.ifId = ifId;
	}

	@Override
	public boolean validate() throws OtherExceptionCollect {
		return true;
	}
}
