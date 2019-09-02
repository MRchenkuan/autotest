/**
 * Title: IfSysMockHistory.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.bo;

import com.gigold.pay.framework.core.Domain;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Title: IfSysMockHistory<br/>
 * Description: <br/>
 * Company: gigold
 * @author xiebin
 *
 */
@Component
@Scope("prototype")
public class IfSysSQLHistory extends Domain {
	private int id,mockid,sqlid,order;
	private String resulte,exceptions,sql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMockid() {
		return mockid;
	}

	public void setMockid(int mockid) {
		this.mockid = mockid;
	}

	public int getSqlid() {
		return sqlid;
	}

	public void setSqlid(int sqlid) {
		this.sqlid = sqlid;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getResulte() {
		return resulte;
	}

	public void setResulte(String resulte) {
		this.resulte = resulte;
	}

	public String getExceptions() {
		return exceptions;
	}

	public void setExceptions(String exceptions) {
		this.exceptions = exceptions;
	}
}
