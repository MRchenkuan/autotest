/**
 * Title: IfSysMockController.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.controller;

import com.gigold.pay.autotest.bo.*;
import com.gigold.pay.autotest.reqDto.IfSysAssertRuleReqDto;
import com.gigold.pay.autotest.rspDto.IfSysAssertRuleRspDto;
import com.gigold.pay.autotest.service.*;
import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
public class IfSysAssertRuleController extends BaseController {

	@Autowired
	IfSysAssertRuleService ifSysAssertRuleService;

	/**
	 * 获取mock的断言规则信息
	 */
	@RequestMapping("/getAssertRulesByMockId.do")
	public @ResponseBody
	IfSysAssertRuleRspDto getAssertRulesByMockId(@RequestBody Map<String,Object> dto){
		int mockid;
		IfSysAssertRuleRspDto reDto = new IfSysAssertRuleRspDto();
		try{
			IfSysAssertCondition ifSysAssertCondition= ifSysAssertRuleService.getConditions();
			reDto.setAssertCondition(ifSysAssertCondition);
		}catch (Exception e){
			reDto.setRspCd(SysCode.SYS_FAIL);
			e.printStackTrace();
		}
		if(dto.containsKey("mockId")){
			mockid = (int)dto.get("mockId");
			IfSysMock ifSysMock = new IfSysMock();
			ifSysMock.setId(mockid);
			List<IfSysAssertRule> rules = ifSysAssertRuleService.getAssertRulesByMock(ifSysMock);
			reDto.setAssertRules(rules);
			reDto.setRspCd(SysCode.SUCCESS);
		}else{
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("请传入mockId");
		}
		return reDto;
	}

	/**
	 * 获取mock的断言规则信息
	 */
	@RequestMapping("/deleteAssertRule.do")
	public @ResponseBody
	ResponseDto deleteAssertRule(@RequestBody Map<String,Integer> dto){
		int arId = dto.get("arId");

		IfSysAssertRuleRspDto reDto = new IfSysAssertRuleRspDto();
		boolean flag = false;

		try{
			flag = ifSysAssertRuleService.deleteAssertRule(arId);
		}catch (Exception e){
			reDto.setRspCd(SysCode.SYS_FAIL);
			e.printStackTrace();
		}

		if(flag){
			reDto.setRspCd(SysCode.SUCCESS);
		}else{
			reDto.setRspCd(SysCode.SYS_FAIL);
		}
		return reDto;
	}

	/**
	 * 新增/更新mock的断言规则信息
	 */
	@RequestMapping("/updateAssertRules.do")
	public @ResponseBody
	IfSysAssertRuleRspDto updateAssertRules(@RequestBody IfSysAssertRuleReqDto dto){
		IfSysAssertRuleRspDto reDto = new IfSysAssertRuleRspDto();
		List<IfSysAssertRule> ifSysAssertRules = dto.getAssertRules();
		try{
			ifSysAssertRules = ifSysAssertRuleService.updateAssertRules(ifSysAssertRules);
			if(ifSysAssertRules!=null)
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			e.printStackTrace();
			reDto.setRspCd(SysCode.SYS_FAIL);
		}
		reDto.setAssertRules(ifSysAssertRules);
		return reDto;
	}


}
