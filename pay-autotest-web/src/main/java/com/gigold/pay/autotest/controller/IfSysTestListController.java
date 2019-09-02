/**
 * Title: IfSysMockController.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.controller;

import com.gigold.pay.autotest.bo.*;
import com.gigold.pay.autotest.reqDto.IfSysTestResultReqDto;
import com.gigold.pay.autotest.rspDto.IfSysTestResultRspDto;
import com.gigold.pay.autotest.service.IfSysDataAnalyseService;
import com.gigold.pay.autotest.service.IfSysTestListService;
import com.gigold.pay.autotest.service.InterFaceService;
import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title: IfSysMockController<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年11月30日上午11:37:39
 *
 */
@Controller
@RequestMapping("/autotest")
public class IfSysTestListController extends BaseController {

	@Autowired
	InterFaceService interFaceService;
	@Autowired
	IfSysTestListService ifSysTestListService;
	@Autowired
	IfSysDataAnalyseService ifSysDataAnalyseService;
	/**
	 * 获取可测接口的列表
	 */
	@RequestMapping("/getAllInterfaceBeTest.do")
	public @ResponseBody
	ResponseDto getAllInterfaceBeTest(){
		ResponseDto reDto = new ResponseDto();
		// 接口表映射
		Map<Integer,Map<Integer,String>> idIfMap = new HashMap<>();
		// 系统ID名字映射
		Map<Integer,String> idNameMap = new HashMap<>();
		// 返回列表
		List<Map> relist = new ArrayList<>();
		// 返回对象
		Map<String,Map> obj = new HashMap<>();

		List<InterFaceInfo> list = interFaceService.getAllInterfaceBeTest();

		for(InterFaceInfo faceInfo :list ){
			int ifSysId = faceInfo.getIfSysId();
			String ifSysName = faceInfo.getSysName();
			String ifName = faceInfo.getIfName();
			int ifId = faceInfo.getId();

			// 分类接口对象
			if(!idIfMap.containsKey(ifSysId)){
				idIfMap.put(ifSysId,new HashMap<Integer, String>());
			}
			// 系统ID 接口表映射
			idIfMap.get(ifSysId).put(ifId,ifName);

			// 系统ID名字映射
			idNameMap.put(ifSysId,ifSysName);

		}

		obj.put("ifList",idIfMap);
		obj.put("sysNames",idNameMap);

		relist.add(obj);

		reDto.setDataes(relist);
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}

	/**
	 * 获取用例的测试结果接口
	 */
	@RequestMapping("/getTestResultByTestList.do")
	public @ResponseBody
	IfSysTestResultRspDto getTestResultByTestList(@RequestBody IfSysTestResultReqDto dto){
		IfSysTestResultRspDto reDto = new IfSysTestResultRspDto();
		if(dto.getSize()<=0){
			reDto.setRspCd(SysCode.PARA_NULL);
			return reDto;
		}

		int hisCount = dto.getSize();

		if(hisCount<=0){
			reDto.setRspCd(SysCode.LIST_IS_NULL);
			return reDto;
		}

		if(hisCount>30){
			reDto.setRspCd(SysCode.SUCCESS);
			reDto.setRspInf(String.valueOf(hisCount)+"批数据,想卡死我吗?");
			return reDto;
		}

		try {
			int[] ifIdList = new int[1];
			if(dto.getIfId()>0){
				ifIdList[0] = dto.getIfId();
			}else{

				String[] ifIdArray = ifSysTestListService.getIfIdArraryByListId(dto.getListId());
				ifIdList = new int[ifIdArray.length];
				int i=0;
				for(String s:ifIdArray){
					ifIdList[i++] = Integer.parseInt(s);
				}
			}

			TestListResults testListResults= ifSysDataAnalyseService.getTestResulteByIfList(ifIdList,hisCount);
			reDto.setTestListResults(testListResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}
}
