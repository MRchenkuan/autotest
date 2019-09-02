package com.gigold.pay.autotest.controller;



import com.gigold.pay.autotest.bo.*;
import com.gigold.pay.autotest.dao.IfSysMockHistoryDAO;
import com.gigold.pay.autotest.dao.IfSysTestListDAO;
import com.gigold.pay.autotest.dao.InterFaceDao;
import com.gigold.pay.autotest.datamaker.*;
import com.gigold.pay.autotest.email.MailSenderService;
import com.gigold.pay.autotest.service.*;

import com.gigold.pay.scripte.service.IfSysAutoTest;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:spring/*Beans.xml"})
public class DebugApi  {
    @Autowired
    private IfSysReferService ifSysReferService;
    @Autowired
    private IfSysSQLCallBackService ifSysSQLCallBackService;
    @Autowired
    private IfSysAutoTestService ifSysAutoTestService;
    @Autowired
    private IfSysMockService ifSysMockService;
    @Autowired
    private IfSysAutoTest ifSysAutoTest;
    @Autowired
    private IfSysDataAnalyseService ifSysDataAnalyseService;
    @Autowired
    private InterFaceService interFaceService;
    @Autowired
    private IfSysTestStatuService ifSysTestStatuService;
    @Autowired
    private RetrunCodeService retrunCodeService;
    @Autowired
    private IfSysAssertRuleService ifSysAssertRuleService;
    @Autowired
    private IfSysMockHistoryService ifSysMockHistoryService;
    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private InterFaceDao interFaceDao;
    @Autowired
    private IfSysTestListService ifSysTestListService;
    @Autowired
    private IfSysMockHistoryDAO ifSysMockHistoryDAO;

    @Test
    public void updataReferFields() throws Exception {
//        List<IfSysMockHistory> a = ifSysMockHistoryService.getNewestReslutOf(15,47);
//        System.out.println(a);
//        try {
//        ifSysSQLCallBackService.getDBInfoList(1);
//            IfSysSQLCallBack ifSysSQLCallBack  =new IfSysSQLCallBack();
//            ifSysSQLCallBack.setDesc("测试2");
//            ifSysSQLCallBack.setMockid(302);
//            ifSysSQLCallBack.setDbId(1);
//            ifSysSQLCallBack.setSql("select * from T_IF_INTERFACE where id=67");
//
//            ifSysSQLCallBackService.updateIfSysSQLCallBack(ifSysSQLCallBack);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(interFaceService.getAllIfSys());
//        ifSysAutoTestService.testMock(1560);

//        System.out.println(DateSequences.renewDate("month"));
//        List<IfSysMock> ifSysMockList = ifSysMockService.getAllValidMocks();
//        for(IfSysMock ifSysMock :ifSysMockList ){
//            System.out.println(ifSysMock.getId());
//            System.out.println(ifSysMock.getCaseName());
//            System.out.println(ifSysMock.getIfId());
//            System.out.println(ifSysMock.getIfName());
//            System.out.println(ifSysMock.getDsname());
//            System.out.println(ifSysMock.getSysName());
//        }

//        retrunCodeService.getIfRspCodeHis(80);

//    ifSysDataAnalyseService.getAnalyse(15);

//        TestStatus testStatus = new TestStatus();
//        testStatus.setProDesc("正在进行中...");
//        testStatus.setProValue("1");

//        System.out.println(ifSysTestStatuService.getTestStatus().getProDesc());
//        System.out.println(ifSysTestStatuService.updateTestStatus(testStatus));

//        IfSysAssertCondition ifSysAssertCondition = ifSysAssertRuleService.getConditions();
//        System.out.println(ifSysAssertCondition);
//        int i=1000;
//        while (i-->0){

            ifSysAutoTestService.testMock(2263);
        System.out.println("ok");
//        }
//        int[] arr = {523,559,570,575,576,589,590,598,601,602,604,608,610,611,612,613,614,724};
//        ifSysDataAnalyseService.getTestResulteByIfList(arr,15);
//        ifSysTestListService.getIfIdArraryByListId(0);
//        PageInfo<InterFaceInfo> interFaceInfo = ifSysTestListService.getIfsOfAllTestList(0);

//        List<InterFaceInfo> list = interFaceService.getAllInterfaceBeTest();
//        System.out.println(list);

//        List<String> jnrList = ifSysMockHistoryDAO.getRecentJNRListOf(15);
//        System.out.println(jnrList);
//        IfSysAssertRule ifSysAssertRule1 = new IfSysAssertRule();
//        IfSysAssertRule ifSysAssertRule2 = new IfSysAssertRule();
//        IfSysAssertRule ifSysAssertRule3 = new IfSysAssertRule();
//
//        ifSysAssertRule1.setMockId(1527);
//        ifSysAssertRule1.setAsClass("TEXT");
//        ifSysAssertRule1.setAsType("EXCLUDE");
//        ifSysAssertRule1.setAsValue("dddd");
//
//        ifSysAssertRule2.setMockId(1527);
//        ifSysAssertRule2.setAsClass("TEXT");
//        ifSysAssertRule2.setAsType("EXCLUDE");
//        ifSysAssertRule2.setAsValue("fdsaaa");
//
//        ifSysAssertRule3.setMockId(1527);
//        ifSysAssertRule3.setAsClass("TEXT");
//        ifSysAssertRule3.setAsType("EXCLUDE");
//        ifSysAssertRule3.setAsValue("fdsa");
//
//        List<IfSysAssertRule> ifSysAssertRules = new ArrayList<>();
//        ifSysAssertRules.add(ifSysAssertRule1);
//        ifSysAssertRules.add(ifSysAssertRule2);
//        ifSysAssertRules.add(ifSysAssertRule3);
//
//        ifSysAssertRules = ifSysAssertRuleService.updateAssertRules(ifSysAssertRules);
//
//        System.out.println(ifSysAssertRules);

//        IfSysMock ifSysMock = new IfSysMock();
//        ifSysMock.setId(302);
//        ifSysAssertRuleService.getAssertRulesByMock(ifSysMock);
//        System.out.println(Sequences.renewNo(1));
    }

}
