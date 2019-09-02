/**
 * Title: IfSysMockRspDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.rspDto;

import com.gigold.pay.autotest.bo.IfSysMock;
import com.gigold.pay.framework.core.ResponseDto;

import java.util.List;

/**
 * Title: IfSysMockRspDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月2日下午1:36:56
 *
 */
public class IfSysMockInfoRspDto extends ResponseDto {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private IfSysMock mock;
	private List mockFieldReferList;
	private List mockReferList;
	private List referTo;
	private List relayOn;

	public List getReferTo() {
		return referTo;
	}

	public void setReferTo(List referTo) {
		this.referTo = referTo;
	}

	public List getRelayOn() {
		return relayOn;
	}

	public void setRelayOn(List relayOn) {
		this.relayOn = relayOn;
	}

	public List getMockFieldReferList() {
		return mockFieldReferList;
	}

	public void setMockFieldReferList(List mockFieldReferList) {
		this.mockFieldReferList = mockFieldReferList;
	}

	public List getMockReferList() {
		return mockReferList;
	}

	public void setMockReferList(List mockReferList) {
		this.mockReferList = mockReferList;
	}

	public IfSysMock getMock() {
		return mock;
	}

	public void setMock(IfSysMock mock) {
		this.mock = mock;
	}
}
