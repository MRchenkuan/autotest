/**
 * Title: IfSysAutoTest.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.scripte.service;

import java.util.*;

import com.alibaba.dubbo.config.annotation.Service;
import com.gigold.pay.autotest.bo.*;
import com.gigold.pay.autotest.dao.InterFaceDao;
import com.gigold.pay.autotest.datamaker.GlobalVal;
import com.gigold.pay.autotest.email.MailSenderService;
import com.gigold.pay.autotest.service.*;
import com.gigold.pay.autotest.threadpool.IfsysCheckThreadPool;
import com.gigold.pay.framework.base.SpringContextHolder;
import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.Domain;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.service.AbstractService;

/**
 * Title: IfSysAutoTest<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月15日上午9:40:02
 *
 */
public class IfSysAutoTest extends AbstractService {
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private IfsysCheckThreadPool ifsysCheckThreadPool;
	private MailSenderService mailSenderService;
	private IfSysMockService ifSysMockService;
	private IfSysStuffService ifSysStuffService;
	private IfSysMockHistoryService ifSysMockHistoryService;
	private InterFaceService interFaceService;
	private InterFaceDao interFaceDao;
	private IfSysAutoTestService ifSysAutoTestService;
	private InterFaceSysService interFaceSysService;
	private IfSysDataAnalyseService ifSysDataAnalyseService;


	public IfSysAutoTest(){
		ifsysCheckThreadPool = (IfsysCheckThreadPool) SpringContextHolder.getBean(IfsysCheckThreadPool.class);
		mailSenderService = (MailSenderService) SpringContextHolder.getBean(MailSenderService.class);
		ifSysMockService = (IfSysMockService) SpringContextHolder.getBean(IfSysMockService.class);
		ifSysStuffService = (IfSysStuffService) SpringContextHolder.getBean(IfSysStuffService.class);
		ifSysMockHistoryService = (IfSysMockHistoryService) SpringContextHolder.getBean(IfSysMockHistoryService.class);
		interFaceService = (InterFaceService) SpringContextHolder.getBean(InterFaceService.class);
		interFaceDao = (InterFaceDao) SpringContextHolder.getBean(InterFaceDao.class);
		ifSysAutoTestService = (IfSysAutoTestService) SpringContextHolder.getBean(IfSysAutoTestService.class);
		interFaceSysService = (InterFaceSysService) SpringContextHolder.getBean(InterFaceSysService.class);
		ifSysDataAnalyseService = (IfSysDataAnalyseService) SpringContextHolder.getBean(IfSysDataAnalyseService.class);


	}
	public void work() {
		debug("Quartz的任务调度！！！");
		autoTest();
		System.out.println("call work");
		debug("一次的任务调度！！！");
	}
	
	public void autoTest() {
		if(!"YES".equals(GlobalVal.get("test.run")))return;
		ifsysCheckThreadPool.execute();
	}


	/**
	 * 发送详情邮件到每个人
	 */
	public void sendMailOfDetail() {
		if(!"YES".equals(GlobalVal.get("test.sendmail")))return;

		// 返回所有测试过的结果
		List<IfSysMock> resulteMocks = ifSysMockService.filterMocksByFailed();
		// 根据用例 - 步骤对应表
		if(resulteMocks.isEmpty()){
			System.out.print("没有查询到错误的结果集");
		}else {
			// 1.测试结果按接口分类
			Map<String,Object> StepsMap = new HashMap<>();// 每个用例的步骤表
			Map<String,List<IfSysMock>> rstItfces = new TreeMap<>();
			for(IfSysMock ifSysMock:resulteMocks){
				// 首先,判断结果分类中是否已经初始化过了,若没有则初始化
				String key  = String.valueOf(ifSysMock.getIfId());
				String mockId  = String.valueOf(ifSysMock.getId());
				if(!rstItfces.containsKey(key)){
					rstItfces.put(key,new ArrayList<IfSysMock>()); // 键值格式为{"12":[1,2,3,4]}
				}
				// 然后,初始化每个用例的步骤表 键值格式为 {"186":[1,2,3,4]}
				List<IfSysMock> Steps = new ArrayList<>();
				ifSysAutoTestService.invokerOrder(Steps,Integer.parseInt(mockId));
				Collections.reverse(Steps);//步骤反序
				StepsMap.put(mockId,Steps);
				// 最后,增加mock
				rstItfces.get(key).add(ifSysMock);
			}


			// 2.分发收件人
			Map<String,List<List<IfSysMock>>> observers = new HashMap<>();
			for(String ifId:rstItfces.keySet()){
				// 每个接口的测试结果集 [1,2,3,4]
				List<IfSysMock> eachMockSet = rstItfces.get(ifId);

				// 获取接口的关注者
				List<IfSysMock> emailObjs = ifSysMockService.getInterfaceFollowShipById(Integer.parseInt(ifId));

				// 将接口结果集添加到收件人observers
				int i = 1;
				for(IfSysMock emailObj:emailObjs){ // 遍历单个接口的测试结果集,拿到测试结果
					String email = emailObj.getEmail(); // chenkuan@qq.com
					String Uname = emailObj.getUsername(); // chenkuan@qq.com
					String key = email+"::"+Uname;
					if(!observers.containsKey(key)){
						observers.put(key,new ArrayList<List<IfSysMock>>());//二维数组,每组为一个接口
					}
					observers.get(key).add(eachMockSet);// 对测试结果所包含的发件人去重,然后将相同的发件人所对应的mock所对应的接口进行关联
					if(i++ == emailObjs.size()){// 如果到最后,则进行一次排序
						Collections.sort(observers.get(key), new Comparator<List<IfSysMock>>() {
							@Override
							public int compare(List<IfSysMock> o1, List<IfSysMock> o2) {
								return o1.get(0).getIfId() - o2.get(0).getIfId();
							}
						});
					}
				}
			}


			// 3.发件
			for(String emailNuname:observers.keySet()){
				// 获取每个用户的结果
				List<List<IfSysMock>> ifOfmockSetList = observers.get(emailNuname);
				//收件人地址和姓名
				String email = emailNuname.split("::")[0].trim();
				String userName = emailNuname.split("::")[1].trim();
				// 设置收件人地址
				List<String> addressTo = new ArrayList<>();
				addressTo.add(email);
				mailSenderService.setTo(addressTo);
				mailSenderService.setSubject(GlobalVal.get("sys.env.title")+" 接口测试报告");
				mailSenderService.setTemplateName("mail.vm");// 设置的邮件模板
				// 发送结果
				Map<String,Object> model = new HashMap<>();
				model.put("ifOfmockSetList", ifOfmockSetList);
				model.put("userName", userName);
				model.put("StepsMap", StepsMap); //每个用例的步骤表  {332=[], 159=[1,2], 330=[1,2]}

				//if(email.equals("chenkuan@gigold.com")||email.equals("chenhl@gigold.com")||email.equals("liuzg@gigold.com")||email.equals("xiebin@gigold.com"))
				mailSenderService.sendWithTemplateForHTML(model);
			}
			System.out.println("邮件发送成功！");
		}
	}


	/**
	 * 发送总览邮件到所有人
	 */
	public void sendMailOfTotle(){
		if(!"YES".equals(GlobalVal.get("test.sendmail")))return;

		int jnrCount = 15;

		// 获得数据
		Map<String,Object> model = ifSysDataAnalyseService.getAnalyse(jnrCount);

		// 发送邮件
		String[] copyList = SystemPropertyConfigure.getProperty("mail.default.observer").split(",");
		List<String> copyTo = java.util.Arrays.asList(copyList);
		mailSenderService.setTo(copyTo);
		mailSenderService.setSubject(GlobalVal.get("sys.env.title")+" 接口测试报告");
		mailSenderService.setTemplateName("copyMail.vm");// 设置的邮件模板
		mailSenderService.sendWithTemplateForHTML(model);
		System.out.println("邮件发送成功！");
	}

}
