/**
 * Title: IfSysMockService.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.service;

import com.gigold.pay.autotest.bo.IfSysCallBackDB;
import com.gigold.pay.autotest.bo.IfSysSQLCallBack;
import com.gigold.pay.autotest.bo.IfSysSQLHistory;
import com.gigold.pay.autotest.dao.IfSysSQLCallBackDAO;
import com.gigold.pay.autotest.dao.IfSysMockDAO;
import com.gigold.pay.autotest.datamaker.DBconnector;
import com.gigold.pay.framework.core.Domain;
import com.gigold.pay.framework.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;

/**
 * 回调sql服务
 */
@Service
public class IfSysSQLCallBackService extends AbstractService {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	@Autowired
	IfSysMockDAO ifSysMockDao ;

	@Autowired
	IfSysSQLCallBackDAO ifSysSQLCallBackDAO ;

	/**
	 * 根据mockid查询回调sql
	 * @return
	 */
	public IfSysSQLCallBack getIfSysSQLCallBackByMockId(int mockid){

		IfSysSQLCallBack ifSysSQLCallBack = null;
		try {
			ifSysSQLCallBack = ifSysSQLCallBackDAO.getIfSysSQLCallBackByMockId(mockid);
		}catch (Exception e){
			e.printStackTrace();
		}
		return ifSysSQLCallBack;
	}

	/**
	 * 根据id查询回调sql
	 * @return
	 */
	public IfSysSQLCallBack getIfSysSQLCallBackById(int id){
		IfSysSQLCallBack ifSysSQLCallBack = null;
		try {
			ifSysSQLCallBack = ifSysSQLCallBackDAO.getIfSysSQLCallBackById(id);
		}catch (Exception e){
			e.printStackTrace();
		}
		return ifSysSQLCallBack;
	}

	/**
	 * 更新回调sql
	 * @return
	 */
	public IfSysSQLCallBack updateIfSysSQLCallBack(IfSysSQLCallBack sqlCallBack){
		IfSysSQLCallBack ifSysSQLCallBack = null;
		try {
			int mockid = sqlCallBack.getMockid();
			IfSysSQLCallBack ifSysSQLCallBack1 = ifSysSQLCallBackDAO.getIfSysSQLCallBackByMockId(mockid);
			// 如果存在
			if(ifSysSQLCallBack1!=null){
				// 取出ID,初始化给 sqlCallBack
				int sqlid = ifSysSQLCallBack1.getId();
				sqlCallBack.setId(sqlid);
			}

			// 更新
			ifSysSQLCallBackDAO.updateIfSysSQLCallBack(sqlCallBack);
			ifSysSQLCallBack = ifSysSQLCallBackDAO.getIfSysSQLCallBackById(sqlCallBack.getId());


		}catch (Exception e){
			e.printStackTrace();
		}
		return ifSysSQLCallBack;
	}

	/**
	 * 根据mockid查询mock sql
	 * @param mockId 用例ID
	 * @return 返回用例sql
	 */
	public List<IfSysSQLCallBack> getMockSQLByMockId(int mockId){
		List<IfSysSQLCallBack> list = null;
		try {
			list = ifSysSQLCallBackDAO.getMockSQLByMockId(mockId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 执行sql命令
	 * @param sqlObj sql对象
	 * @return
     */
	public Map<String, Object> excuteSql(IfSysSQLCallBack sqlObj) {
		Map<String,Object> resulte = new HashMap<>();
		List<List> rs = new ArrayList<>();
		String sql = sqlObj.getRealSql();

		try{
			// 获取数据库ID
			int dbId = sqlObj.getDbId();
			IfSysCallBackDB ifSysCallBackDB = ifSysSQLCallBackDAO.getCallBackDbById(dbId);

			// 动态创建连接
			String name = "com.mysql.jdbc.Driver";
			String host = ifSysCallBackDB.getDbHost();
			int port = ifSysCallBackDB.getDbPort();
			String username = ifSysCallBackDB.getDbUsername();
			String password = ifSysCallBackDB.getDbPassword();
			String dbname = ifSysCallBackDB.getDbName();
			String url = "jdbc:mysql://"+host+":"+port+"/"+dbname+"?characterEncoding=utf8";
			Connection conn = null;


			// 动态加载mysql驱动
			Class.forName(name);
			// 为了节省资源,尝试从连接池读取
			if(DBconnector.stmtPool.containsKey(url)){
				//如果创建过则直接获取
				conn = DBconnector.stmtPool.get(url);
			}else{
				// 如果么有创建过,则重新创建
				conn = DriverManager.getConnection(url, username, password);
				DBconnector.stmtPool.put(url,conn);
			}

			// 执行sql
			Statement stmt = conn.createStatement();
			try{
				ResultSet _rs = stmt.executeQuery(sql);
				// 构建二维数组
				while (_rs.next()){
					ResultSetMetaData mtdt = _rs.getMetaData();
					List<String> cnameList = new ArrayList<>();
					List<String> valueList = new ArrayList<>();
					for(int i=1;i<=mtdt.getColumnCount();i++){
						String cname = mtdt.getColumnLabel(i);
						String value = _rs.getString(cname);
						cnameList.add(cname);
						valueList.add(value);
					}
					// 在第一行的时候加入cnamelist
					if(_rs.getRow()==1)rs.add(cnameList);
					rs.add(valueList);
				}
				stmt.close();
			}catch (Exception e){
				e.printStackTrace();
				stmt.close();
			}

			System.out.println(ifSysCallBackDB.getDbName());
			resulte.put("Exception","ok");
			resulte.put("rs",rs);
		}catch (Exception e){
			e.getStackTrace();
			String exception = "";
			Throwable cause = e.getCause();
			if(cause==null)exception="ok";
			if(cause!=null)exception=cause.getMessage();
			resulte.put("Exception",exception);
			resulte.put("rs",new ArrayList<>());
		}
		return resulte;
	}


	/**
	 * 取一对新老sql查询历史
	 * @param sqlObj sql对象
	 * @return 返回数据对
	 */
	public Map<String,IfSysSQLHistory> getAPairOfSQLHistoryBySql(IfSysSQLCallBack sqlObj) {
		int sqlid = sqlObj.getId();
		int mockid = sqlObj.getMockid();
		Map<String,IfSysSQLHistory> aPairOfHis = new HashMap<>();

		IfSysSQLHistory newHis=new IfSysSQLHistory(),oldHis=new IfSysSQLHistory();
		if(sqlid>0&&mockid>0){
			// 查询新结果
			newHis = ifSysSQLCallBackDAO.getSQLHistory(sqlid,mockid,2);
			// 查询老结果
			oldHis = ifSysSQLCallBackDAO.getSQLHistory(sqlid,mockid,1);
		}


		// 初始化
		if (newHis==null){
			newHis = new IfSysSQLHistory();
			newHis.setSqlid(sqlid);
			newHis.setMockid(mockid);
		}

		// 初始化
		if (oldHis==null){
			oldHis = new IfSysSQLHistory();
			oldHis.setSqlid(sqlid);
			oldHis.setMockid(mockid);
		}

		aPairOfHis.put("old",oldHis);
		aPairOfHis.put("new",newHis);

		return aPairOfHis;
	}

	/**
	 * 回写正常的sql执行结果
	 * @param his IfSysSQLHistory对象
	 */
	public boolean saveSQLResulte(IfSysSQLHistory his) {
		int count;
		boolean flag = false;
		try {
			count = ifSysSQLCallBackDAO.saveSQLResulte(his);
			if(count>0){
				flag = true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 根据项目ID获取项目下的DB简介
	 * @param mockId
	 * @return
     */
	public Map<String,String> getDBInfoListByMockId(int mockId) {
		List<Map> dbList = new ArrayList<>();
		Map<String,String> dbMap = new HashMap<>();
		try {
			dbList = ifSysSQLCallBackDAO.getDBInfoList(mockId);
			dbMap = new HashMap<>();
			for(Map db:dbList){
				dbMap.put(String.valueOf(db.get("id")),String.valueOf(db.get("dbDesc")));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return dbMap;
	}
}
