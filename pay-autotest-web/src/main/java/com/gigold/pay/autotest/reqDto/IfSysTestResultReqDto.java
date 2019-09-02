/**
 * Title: IfSysMockRspDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.reqDto;


import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;

/**
 * Title: IfSysMockRspDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月2日下午1:36:56
 *
 */
public class IfSysTestResultReqDto extends RequestDto {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	int size,listId,ifId;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public int getIfId() {
		return ifId;
	}

	public void setIfId(int ifId) {
		this.ifId = ifId;
	}

	@Override
	public boolean validate() throws OtherExceptionCollect {
		return true;
	}
}
