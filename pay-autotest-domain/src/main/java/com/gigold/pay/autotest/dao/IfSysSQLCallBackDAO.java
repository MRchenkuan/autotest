package com.gigold.pay.autotest.dao;

import com.gigold.pay.autotest.bo.IfSysCallBackDB;
import com.gigold.pay.autotest.bo.IfSysSQLCallBack;
import com.gigold.pay.autotest.bo.IfSysSQLHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/3/24.
 */
public interface IfSysSQLCallBackDAO {
    /**
     * 根据mockid查询回调sql
     * @return
     */
    public IfSysSQLCallBack getIfSysSQLCallBackByMockId(int mockid);

    /**
     * 根据id查询回调sql
     * @return
     */
    public IfSysSQLCallBack getIfSysSQLCallBackById(int id);

    /**
     * 更新回调sql
     * @return
     */
    public void updateIfSysSQLCallBack(IfSysSQLCallBack sqlCallBack);

    /**
     * 获取接口下的 mockid
     * @param mockId 接口ID
     * @return 返回sql列表
     */
    public List<IfSysSQLCallBack> getMockSQLByMockId(int mockId);


    /**
     * 执行mock的sql的方法
     * @param sql sql
     * @return 返回
     */
    List<Map> excuteSql(@Param("paramSQL") String sql);

    /**
     * 根据mockid, sqlid,order 查询新老SQL执行结果
     * @param sqlid sqlid
     * @param mockid mockid
     * @param order 新老结果,1为老,2为新
     * @return 返回结果
     */
    IfSysSQLHistory getSQLHistory(@Param("sqlid") int sqlid, @Param("mockid") int mockid, @Param("order") int order);

    /**
     * 保存sql执行结果
     * @param his 结果对象
     * @return 返回保存结果
     */
    int saveSQLResulte(IfSysSQLHistory his);

    /**
     * 根据dbid获取db链接的具体信息
     * @param dbId
     * @return
     */
    IfSysCallBackDB getCallBackDbById(int dbId);

    /**
     * 根据项目id获取项目下的db简介
     * @param mockId
     * @return
     */
    List<Map> getDBInfoList(int mockId);
}
