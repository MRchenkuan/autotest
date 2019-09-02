/**
 * Title: IfSysMockReqaDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.rspDto;

import com.gigold.pay.autotest.bo.IfSysSQLCallBack;
import com.gigold.pay.framework.core.ResponseDto;

import java.util.Map;

/**
 * Title: IfSysMockReqaDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * @author xiebin
 * @date 2015年11月30日上午11:39:51
 *
 */
public class IfSysCallBackSQLRspDto extends ResponseDto {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private IfSysSQLCallBack ifSysSQLCallBack;
	private Map dBOptions;
	private int mockId;
	private String sql,desc;

	public Map getdBOptions() {
		return dBOptions;
	}

	public void setdBOptions(Map dBOptions) {
		this.dBOptions = dBOptions;
	}

	public int getMockId() {
		return mockId;
	}

	public void setMockId(int mockId) {
		this.mockId = mockId;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public IfSysSQLCallBack getIfSysSQLCallBack() {
		return ifSysSQLCallBack;
	}

	public void setIfSysSQLCallBack(IfSysSQLCallBack ifSysSQLCallBack) {
		this.ifSysSQLCallBack = ifSysSQLCallBack;
	}
}
