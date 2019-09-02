/**
 * Title: IfSysMockController.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.controller;

import java.util.*;

import com.gigold.pay.autotest.bo.*;
import com.gigold.pay.autotest.datamaker.ConstField;
import com.gigold.pay.autotest.reqDto.*;
import com.gigold.pay.autotest.rspDto.*;
import com.gigold.pay.autotest.service.*;
import com.gigold.pay.autotest.util.Constant;
import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.scripte.service.IfSysAutoTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gigold.pay.framework.base.SpringContextHolder;
import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.core.exception.PendingException;
import com.gigold.pay.framework.web.BaseController;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpSession;

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
public class IfSysMockController extends BaseController {

	@Autowired
	IfSysMockService ifSysMockService;
	@Autowired
	InterFaceFieldService interFaceFieldService;
	@Autowired
	InterFaceService interFaceService;
	@Autowired
	InterFaceSysService interFaceSysService;
	@Autowired
	InterFaceProService interFaceProService;
	@Autowired
	private RetrunCodeService retrunCodeService;
	@Autowired
	IfSysReferService ifSysReferService;
	@Autowired
	IfSysAutoTestService ifSysAutoTestService;
	@Autowired
	IfSysAutoTest ifSysAutoTest;
	@Autowired
	IfSysSQLCallBackService ifSysSQLCallBackService;
	@Autowired
	IfSysDataAnalyseService ifSysDataAnalyseService;
	@Autowired
	IfSysTestStatuService ifSysTestStatuService;

	/**
	 * @return the retrunCodeService
	 */
	public RetrunCodeService getRetrunCodeService() {
		return retrunCodeService;
	}

	/**
	 * @param retrunCodeService the retrunCodeService to set
	 */
	public void setRetrunCodeService(RetrunCodeService retrunCodeService) {
		this.retrunCodeService = retrunCodeService;
	}

	/**
	 * @return the ifSysMockService
	 */
	public IfSysMockService getIfSysMockService() {
		return ifSysMockService;
	}

	/**
	 * @param ifSysMockService
	 *            the ifSysMockService to set
	 */
	public void setIfSysMockService(IfSysMockService ifSysMockService) {
		this.ifSysMockService = ifSysMockService;
	}

	/**
	 * @return the interFaceFieldService
	 */
	public InterFaceFieldService getInterFaceFieldService() {
		return interFaceFieldService;
	}

	/**
	 * @param interFaceFieldService
	 *            the interFaceFieldService to set
	 */
	public void setInterFaceFieldService(InterFaceFieldService interFaceFieldService) {
		this.interFaceFieldService = interFaceFieldService;
	}

	/**
	 * @return the interFaceService
	 */
	public InterFaceService getInterFaceService() {
		return interFaceService;
	}

	/**
	 * @param interFaceService
	 *            the interFaceService to set
	 */
	public void setInterFaceService(InterFaceService interFaceService) {
		this.interFaceService = interFaceService;
	}


	@RequestMapping("/getHistoricalReturnCode.do")
	public @ResponseBody
	ResponseDto getHistoricalReturnCode(@RequestBody Map<String,Integer> dto) {
		int ifId;
		ResponseDto reDto = new ResponseDto();
		if(dto.containsKey("ifId")){
			ifId= dto.get("ifId");
		}else{
			reDto.setRspCd(SysCode.SYS_FAIL);
			return reDto;
		}
		reDto.setDataes(retrunCodeService.getIfRspCodeHis(ifId));
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}

	/**
	 *
	 * Title: addIfSysMock<br/>
	 * Description: 新增接口测试数据<br/>
	 *
	 * @author xiebin
	 * @date 2015年12月4日下午1:20:56
	 *
	 * @param dto
	 * @return
	 */
	@RequestMapping("/addifsysmock.do")
	public @ResponseBody IfSysMockAddRspDto addIfSysMock(@RequestBody IfSysMockAddReqDto dto) {
		IfSysMockAddRspDto reDto = new IfSysMockAddRspDto();
		// 验证请求参数合法性
		String code = dto.validation();
		// 没有通过则返回对应的返回码
		if (!"00000".equals(code)) {
			reDto.setRspCd(code);
			return reDto;
		}
		IfSysMock ifSysMock = null;
		try {
			ifSysMock = createBO(dto, IfSysMock.class);
		} catch (PendingException e) {
			reDto.setRspCd(CodeItem.CREATE_BO_FAILURE);
			return reDto;
		}
		boolean flag = ifSysMockService.addIfSysMock(ifSysMock);
		if (flag) {
			reDto.setRspCd(SysCode.SUCCESS);
			reDto.setIfSysMock(ifSysMock);
		} else {
			reDto.setRspCd(CodeItem.FAILURE);
		}
		return reDto;
	}

	/**
	 *
	 * Title: deleteIfSysMockById<br/>
	 * Description: 根据ID删除测试数据<br/>
	 *
	 * @author xiebin
	 * @date 2015年12月4日下午1:22:36
	 *
	 * @param dto
	 * @return
	 */
	@RequestMapping("/deleteifsysmockbyid.do")
	public @ResponseBody ResponseDto deleteIfSysMockById(@RequestBody IfSysMockDelReqDto dto) {
		ResponseDto reDto = new ResponseDto();
		boolean flag = ifSysMockService.deleteIfSysMockById(dto.getId());
		if (flag) {
			reDto.setRspCd(SysCode.SUCCESS);
		} else {
			reDto.setRspCd(CodeItem.FAILURE);
		}
		return reDto;
	}

	/**
	 *
	 * Title: deleteIfSysMockByIfId<br/>
	 * Description: 根据接口ID删除测试数据<br/>
	 *
	 * @author xiebin
	 * @date 2015年12月4日下午1:21:34
	 *
	 * @param dto
	 * @return
	 */
	@RequestMapping("/deleteifsysmockbyifId.do")
	public @ResponseBody ResponseDto deleteIfSysMockByIfId(@RequestBody IfSysMockDelReqDto dto) {
		ResponseDto reDto = new ResponseDto();
		boolean flag = ifSysMockService.deleteIfSysMockByIfId(dto.getIfId());
		if (flag) {
			reDto.setRspCd(SysCode.SUCCESS);
		} else {
			reDto.setRspCd(CodeItem.FAILURE);
		}
		return reDto;
	}

	/**
	 *
	 * Title: updateIfSysMock<br/>
	 * Description: 修改测试数据<br/>
	 *
	 * @author xiebin
	 * @date 2015年12月4日下午1:21:54
	 *
	 * @param dto
	 * @return
	 */
	@RequestMapping("/updateifsysmock.do")
	public @ResponseBody IfSysMockAddRspDto updateIfSysMock(@RequestBody IfSysMockAddReqDto dto, HttpSession session) {
		IfSysMockAddRspDto reDto = new IfSysMockAddRspDto();
		// 验证请求参数合法性
		String code = dto.validation();
		// 没有通过则返回对应的返回码
		if (!"00000".equals(code)) {
			reDto.setRspCd(code);
			return reDto;
		}
		IfSysMock ifSysMock = null;
		try {
			ifSysMock = createBO(dto, IfSysMock.class);
			ifSysMock.setRequestPath(dto.getRequestPath()); // 设置用例path
			ifSysMock.setRequestHead(dto.getRequestHead()); // 设置head

		} catch (PendingException e) {
			reDto.setRspCd(CodeItem.CREATE_BO_FAILURE);
			return reDto;
		}

		// 获取用户信息
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		if(user!=null && user.getId()>0){
			ifSysMock.setCreateBy(user.getId());
		}else{
			reDto.setRspCd(CodeItem.USER_PASS_ERROR);
			return reDto;
		}

		boolean flag = ifSysMockService.updateIfSysMock(ifSysMock);

		if (flag) {
			ifSysMock=ifSysMockService.getMockInfoById(ifSysMock);
			reDto.setIfSysMock(ifSysMock);
			reDto.setRspCd(SysCode.SUCCESS);
		} else {
			reDto.setRspCd(CodeItem.FAILURE);
		}
		return reDto;
	}

	/**
	 * 分页获取系统下的接口信息
	 * @return 返回接口信息列表
	 */
	@RequestMapping("/getallifsys.do")
	public @ResponseBody IfSysMockRspDto getAllIfSys(@RequestBody IfSysMockPageDto dto) {
		IfSysMockRspDto reDto = new IfSysMockRspDto();
		int curPageNum = dto.getPageNum();
		PageInfo<InterFaceInfo> pageInfo = null;
		PageHelper.startPage(curPageNum, Integer.parseInt(SystemPropertyConfigure.getProperty("sys.pageSize")));
		InterFaceInfo interFaceInfo = null;
		try {
			interFaceInfo = createBO(dto, InterFaceInfo.class);
			// 若提交了接口Id则搜指定的ID
			if(dto.getIfId()>0){
				interFaceInfo.setId(dto.getIfId());
			}
		} catch (PendingException e) {
			debug("转换bo异常");
			e.printStackTrace();
		}


		List<InterFaceInfo> list = interFaceService.getAllIfSys(interFaceInfo);
		for(InterFaceInfo eachIfInfo : list){

			// 加入重组的 mocklist,去掉一些无用信息
			List<Map> mockidlist  = ifSysMockService.getInterfaceMocksById(eachIfInfo.getId());
			// mockIdList 根据 caseName排序
			try{
				Collections.sort(mockidlist, new Comparator<Map>() {
					@Override
					public int compare(Map o1, Map o2) {
						String caseNameO1 = String.valueOf(o1.get("caseName"));
						String caseNameO2 = String.valueOf(o2.get("caseName"));
						return caseNameO1.compareTo(caseNameO2);
					}
				});
			}catch(Exception ignore){
				System.out.println("排序异常");
			}

			// 加入返回码
			List<ReturnCode> returnCodeList = retrunCodeService.getReturnCodeByIfId(eachIfInfo.getId());
			eachIfInfo.setMockidList(mockidlist);
			eachIfInfo.setReturnCodeList(returnCodeList);
		}

		pageInfo = new PageInfo<>(list);
		reDto.setPageInfo(pageInfo);
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}

	/**
	 *
	 * Title: getIfSysMockByIfId<br/>
	 * Description: 根据接口ID获取测试数据信息 编辑页,新增页<br/>
	 *
	 * @author xiebin
	 * @date 2015年12月2日下午3:08:21
	 *
	 * @param dto
	 * @return
	 */
	@RequestMapping("/getifsysmockbyifid.do")
	public @ResponseBody IfStsMockRspListDto getIfSysMockByIfId(@RequestBody IfSysMockAddReqDto dto) {
		IfStsMockRspListDto reDto = new IfStsMockRspListDto();
		try {
			int ifId = dto.getIfId();
			// 获取接口基本信息
			InterFaceInfo interFaceInfo = interFaceService.getInterFaceById(ifId);
			if (interFaceInfo == null) {
				reDto.setRspCd(CodeItem.FAILURE);
				return reDto;
			}
			// 初始化测试数据
			//initMockData(interFaceInfo);
			// 获取接口测试数据
			List<IfSysMock> list = ifSysMockService.getMockInfoByIfId(ifId);
			interFaceInfo.setMockList(list);
			reDto.setRspCd(SysCode.SUCCESS);
			reDto.setInterFaceInfo(interFaceInfo);
		} catch (Exception e) {
			reDto.setRspCd(CodeItem.FAILURE);
		}
		return reDto;
	}

	/**
	 *
	 * Title: initMockData<br/>
	 * Description: 初始化测试数据 <br/>
	 *
	 * @author xiebin
	 * @date 2015年12月11日上午11:42:24
	 *
	 * @param interFaceInfo
	 */
	public void initMockData(InterFaceInfo interFaceInfo) {
		int ifId = interFaceInfo.getId();
		// 初始化测试数据
		List<ReturnCode> returnList = retrunCodeService.getReturnCodeByIfId(ifId);
		// 遍历返回码 更新测试数据
		for (ReturnCode rscdObj : returnList) {
			IfSysMock mock = (IfSysMock) SpringContextHolder.getBean(IfSysMock.class);
			mock.setIfId(ifId);
			mock.setRspCodeId(rscdObj.getId());
			//测试数据表中确认是否已经存在对应返回码的测试数据
			List<IfSysMock> ifMockList = ifSysMockService.getMockInfoByIfIdAndRspCdId(mock);
			if (ifMockList == null||ifMockList.size()==0) {
				// 获取接口请求字段的JSON展示字符串
				InterFaceField interFaceField = (InterFaceField) SpringContextHolder.getBean(InterFaceField.class);
				interFaceField.setIfId(ifId);
				interFaceField.setFieldFlag("1");
				String reqJson = interFaceFieldService.getJsonStr(interFaceField);
				// 获取接口响应字段的JSON展示字符串
				interFaceField.setFieldFlag("2");
				String rspJson = interFaceFieldService.getJsonStr(interFaceField);
				mock.setRequestJson(reqJson);
				mock.setResponseJson(rspJson);
				// 如果还没有测试数据 则默认添加一条
				ifSysMockService.addIfSysMock(mock);
			}
		}

	}


	/**
	 *
	 * Title: getmockbypage<br/>
	 * Description: 分页获取测试用例数据列表<br/>
	 * @author xiebin
	 * @date 2015年12月24日上午10:33:18
	 *
	 * @param dto
	 * @return
	 */
	@RequestMapping("/getmockbypage.do")
	public @ResponseBody IfSysMockPageRspDto getmockbypage(@RequestBody IfSysMockPageReqDto dto) {
		IfSysMockPageRspDto reDto = new IfSysMockPageRspDto();

		int pageSize = dto.getPageSize();
		PageHelper.startPage(dto.getPageNum(),pageSize>0?pageSize:5);

		IfSysMock ifSysMock=null;
		try {
			ifSysMock=createBO(dto, IfSysMock.class);
		} catch (PendingException e) {
			debug("转换bo异常");
			e.printStackTrace();
		}
		List<IfSysMock> list=ifSysMockService.queryMockByPage(ifSysMock);
		if(list!=null){
			PageInfo pageInfo=new PageInfo(list);
			reDto.setPageInfo(pageInfo);
			reDto.setRspCd(SysCode.SUCCESS);
		}else{
			reDto.setRspCd(CodeItem.FAILURE);
		}
		return reDto;

	}



	@RequestMapping("/getrspcdbyifid.do")
	public @ResponseBody RetrunCodeRspDto getReturnCodeByIfId(@RequestBody ReturnCodeReqDto dto) {
		debug("调用getReturnCodeByIfId");
		RetrunCodeRspDto rdto = new RetrunCodeRspDto();
		int ifId = dto.getIfId();
		if (ifId == 0) {
			rdto.setRspCd(CodeItem.IF_ID_FAILURE);
			return rdto;
		}
		List<ReturnCode> list = retrunCodeService.getReturnCodeByIfId(ifId);
		if (list != null) {
			rdto.setList(list);
			rdto.setRspCd(SysCode.SUCCESS);
		} else {
			rdto.setRspCd(CodeItem.FAILURE);
		}
		return rdto;
	}


	@RequestMapping("/getMockById.do")
	public @ResponseBody
	IfSysMockInfoRspDto getMockByMockId(@RequestBody IfSysMockAddReqDto dto) {

		IfSysMockInfoRspDto reDto = new IfSysMockInfoRspDto();
		int mockid = dto.getId();
		if (mockid == 0) {
			reDto.setRspCd(CodeItem.IF_ID_FAILURE);
			return reDto;
		}

		IfSysMock ifSysMock = new IfSysMock();
		ifSysMock.setId(mockid);
		ifSysMock = ifSysMockService.getMockInfoById(ifSysMock);

		// 获取依赖,字段依赖关系
		List<IfSysRefer> refers= ifSysReferService.getDeeplyReferList(mockid);
		List<IfSysRefer> relays = ifSysReferService.getDeeplyRelaysList(mockid);
		List<IfSysFeildRefer> fields = ifSysReferService.queryReferFields(mockid);
		// 获取sql
		// 获取报文头
		if (ifSysMock != null) {
			reDto.setMock(ifSysMock);
			reDto.setMockReferList(refers);
			reDto.setReferTo(refers);// 依赖
			reDto.setRelayOn(relays);// 引用
			reDto.setMockFieldReferList(fields);
			reDto.setRspCd(SysCode.SUCCESS);
		} else {
			reDto.setRspCd(CodeItem.FAILURE);
		}
		return reDto;
	}


	/**
	 * 增加字段依赖数据
	 * {
	 * 	referList:[
	 * 		{mockid:xxx,ref_mock_id:xxx,ref_feild:xxx,alias:xxx,status:xxx},
	 * 		{mockid:xxx,ref_mock_id:xxx,ref_feild:xxx,alias:xxx,status:xxx}
	 * 	]
	 * }
	 *
	 * @param dto
	 * @return
     */
	@RequestMapping("/addFieldRefer.do")
	public @ResponseBody
	IfSysFieldReferListRspDto addFieldRefer(@RequestBody IfSysFieldReferListReqDto dto) {
		IfSysFieldReferListRspDto reDto = new IfSysFieldReferListRspDto();
		// 验证请求参数合法性
		String code = dto.validation();
		// 没有通过则返回对应的返回码
		if (!"00000".equals(code)) {
			reDto.setRspCd(code);
			return reDto;
		}

		List<IfSysFeildRefer> ifSysFeildReferList = dto.getReferList();
		reDto.setList(ifSysFeildReferList);
		boolean flag = ifSysReferService.updataReferFields(ifSysFeildReferList);

		if (flag) {
			reDto.setRspCd(SysCode.SUCCESS);
		} else {
			reDto.setRspCd(CodeItem.FAILURE);
		}
		return reDto;
	}

	/**
	 * 查询字段依赖关系
	 * @param dto
	 * @return
     */
	@RequestMapping("/queryFieldRefer.do")
	public @ResponseBody
	IfSysFieldReferListRspDto queryFieldRefer(@RequestBody IfSysFieldReferReqDto dto) {
		IfSysFieldReferListRspDto reDto = new IfSysFieldReferListRspDto();
		// 验证请求参数合法性
		String code = dto.validation();
		// 没有通过则返回对应的返回码
		if (!"00000".equals(code)) {
			reDto.setRspCd(code);
			return reDto;
		}

		try {
			List<IfSysFeildRefer> ifSysFeildReferList = ifSysReferService.queryReferFields(Integer.parseInt(dto.getMockid()));
			reDto.setList(ifSysFeildReferList);
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			reDto.setRspCd(SysCode.SYS_FAIL);
		}
		return reDto;
	}

	/**
	 * 删除字段依赖关系
	 * @param dto
	 * @return
     */
	@RequestMapping("/deleteFieldRefer.do")
	public @ResponseBody
	IfSysFieldReferListRspDto deleteReferField(@RequestBody IfSysFieldReferReqDto dto) {
		IfSysFieldReferListRspDto reDto = new IfSysFieldReferListRspDto();
		// 验证请求参数合法性
		String code = dto.validation();
		// 没有通过则返回对应的返回码
		if (!"00000".equals(code)) {
			reDto.setRspCd(code);
			return reDto;
		}

		boolean flag = false;
		try {
			flag = ifSysReferService.deleteReferField(Integer.parseInt(dto.getId()));
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			reDto.setRspCd(SysCode.SYS_FAIL);
		}

		return reDto;
	}

	/**
	 * 获取用例常量域
     */
	@RequestMapping("/getConstFields.do")
	public @ResponseBody
	ResponseDto getConstFields(){
		ResponseDto reDto = new ResponseDto();
		try {
			reDto.setDataes(ConstField.getAllConstFields());
		} catch (Exception e) {
			e.printStackTrace();
		}
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}

	/**
	 * 获取接口信息
	 */
	@RequestMapping("/queryInterFaceById.do")
	public @ResponseBody
	IfSysInterFaceInfoRspDto getInterFaceInfo(@RequestBody IfSysInterFaceReqDto dto){

		IfSysInterFaceInfoRspDto reDto = new IfSysInterFaceInfoRspDto();


		// 获取接口信息
		InterFaceInfo interFaceInfo = interFaceService.getInterFaceById(dto.getIfId());
		if (interFaceInfo == null || interFaceInfo.getId() == 0) {
			reDto.setRspCd(CodeItem.FAILURE);
			return reDto;
		}


		// 获取所属系统信息
		InterFaceSysTem interFaceSysTem = (InterFaceSysTem) SpringContextHolder.getBean(InterFaceSysTem.class);
		interFaceSysTem.setId(interFaceInfo.getIfSysId());
		interFaceSysTem = interFaceSysService.getSysInfoById(interFaceSysTem);
		if (interFaceSysTem == null) {
			reDto.setRspCd(CodeItem.FAILURE);
			return reDto;
		}

		// 获取返回码列表
		List<ReturnCode> returnCodes = retrunCodeService.getReturnCodeByIfId(dto.getIfId());

		// 设置接口信息
		reDto.setInterFaceInfo(interFaceInfo);
		// 设置接口所属系统信息
		reDto.setSystem(interFaceSysTem);
		// 设置接口所属产品信息
		reDto.setReturnCodeList(returnCodes);

		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}

	/**
	 * 执行指定用例的接口
	 */
	@RequestMapping("/autotest.do")
	public @ResponseBody
	ResponseDto autotest(@RequestBody IfSysMockReqDto dto){
		ResponseDto reDto = new ResponseDto();
		boolean resulte = false;
		try {
			int mockid = dto.getMockId();
			resulte = ifSysAutoTestService.testMock(mockid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		reDto.setRspCd(resulte?SysCode.SUCCESS:SysCode.SYS_FAIL);
		return reDto;
	}

	/**
	 * 获取用例的测试结果接口
	 */
	@RequestMapping("/getAnalyse.do")
	public @ResponseBody
	ResponseDto getAnalyse(@RequestBody Map<String,Integer> dto){
		ResponseDto reDto = new ResponseDto();
		if(!dto.containsKey("size")){
			reDto.setRspCd(SysCode.PARA_NULL);
			return reDto;
		}

		int hisCount = dto.get("size");

		if(hisCount<=0){
			reDto.setRspCd(SysCode.LIST_IS_NULL);
			return reDto;
		}

		if(hisCount>30){
			reDto.setRspCd(SysCode.SUCCESS);
			reDto.setRspInf(String.valueOf(hisCount)+"批数据,想卡死我吗?");
			return reDto;
		}

		List<Map> resulte = new ArrayList<>();
		try {
			Map _resulte;
			if(dto.containsKey("ifId")){
				_resulte = ifSysDataAnalyseService.getAnalyse(hisCount,dto.get("ifId"));
			}else{
				_resulte = ifSysDataAnalyseService.getAnalyse(hisCount);
			}

			// 获得源数据
			Map initedDataSet;
			if(_resulte==null || _resulte.size()<=0){
				reDto.setRspCd(SysCode.LIST_IS_NULL);
				reDto.setRspInf("当前接口没有测试记录");
				return reDto;
			}


			initedDataSet = (Map) _resulte.get("initedDataSet");
			for(Object oneseire :initedDataSet.keySet()){
				// 重组数据
				Map allIfs = (Map)initedDataSet.get(oneseire);
				for(Object ifId : allIfs.keySet()){
					// 拿到接口信息
					Object eachIfRs = allIfs.get(ifId);
					// 拿到测试结果
					List ifTestData = (List) ((Map)eachIfRs).get("ifTestData");
					// 储存测试结果
					allIfs.put(ifId, ifTestData);
				}
				initedDataSet.put(oneseire,allIfs);
			}
			_resulte.put("initedDataSet",initedDataSet);

			resulte.add(_resulte);
			reDto.setDataes(resulte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}

	/**
	 * 更新回调sql的接口
	 */
	@RequestMapping("/updateCallBackSQL.do")
	public @ResponseBody
	ResponseDto updateCallBackSQL(@RequestBody IfSysCallBackSQLReqDto dto){
		IfSysCallBackSQLRspDto reDto = new IfSysCallBackSQLRspDto();
		int mockid = dto.getMockId();
		String sql = dto.getSql();
		String desc =dto.getDesc();
		int id = dto.getId();
		int dbId = dto.getDbId();

		// 输入参数的初始化
		IfSysSQLCallBack ifSysSQLCallBack = new IfSysSQLCallBack();
		ifSysSQLCallBack.setMockid(mockid);
		ifSysSQLCallBack.setSql(sql);
		ifSysSQLCallBack.setDesc(desc);
		ifSysSQLCallBack.setDbId(dbId);
		if(id>0)ifSysSQLCallBack.setId(id);

		try{
			IfSysSQLCallBack SQLCallBack = ifSysSQLCallBackService.updateIfSysSQLCallBack(ifSysSQLCallBack);
			reDto.setIfSysSQLCallBack(SQLCallBack);
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			e.getStackTrace();
			reDto.setRspCd(SysCode.SYS_FAIL);
		}

		reDto.setMockId(mockid);
		reDto.setSql(sql);
		reDto.setDesc(desc);

		return reDto;
	}

	/**
	 * 获取回调sql信息
	 */
	@RequestMapping("/getCallBackSQL.do")
	public @ResponseBody
	ResponseDto getCallBackSQL(@RequestBody IfSysCallBackSQLReqDto dto){
		IfSysCallBackSQLRspDto reDto = new IfSysCallBackSQLRspDto();
		int mockid = dto.getMockId();



		try{
			// 获取sql执行结果
			List<IfSysSQLCallBack> SQLCallBack = ifSysSQLCallBackService.getMockSQLByMockId(mockid);
			if(SQLCallBack.size()>0){
				reDto.setIfSysSQLCallBack(SQLCallBack.get(0));
			}

			// 获取db列表
			Map dbOptions = ifSysSQLCallBackService.getDBInfoListByMockId(mockid);
			reDto.setdBOptions(dbOptions);

			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			e.getStackTrace();
			reDto.setRspCd(SysCode.SYS_FAIL);
		}

		reDto.setMockId(mockid);

		return reDto;
	}

	/**
	 * 获取回调sql信息
	 */
	@RequestMapping("/getCallBackSQLResulte.do")
	public @ResponseBody
	ResponseDto getCallBackSQLResulte(@RequestBody Map<String,Object> dto){
		ResponseDto reDto = new ResponseDto();
		int mockid;
		if(dto.containsKey("mockId")){
			mockid = (int)dto.get("mockId");
		}else{
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("请传入mockId");
			return reDto;
		}

		List<IfSysSQLCallBack> sqlObjs = ifSysSQLCallBackService.getMockSQLByMockId(mockid);

		// TODO 取第一个,此处可能会有问题,需要从根本上解决返回多个的问题
		// TODO 取第一个,此处可能会有问题,需要从根本上解决返回多个的问题
		// TODO 取第一个,此处可能会有问题,需要从根本上解决返回多个的问题
		// TODO 取第一个,此处可能会有问题,需要从根本上解决返回多个的问题
		// TODO 取第一个,此处可能会有问题,需要从根本上解决返回多个的问题
		// TODO 取第一个,此处可能会有问题,需要从根本上解决返回多个的问题
		// TODO 取第一个,此处可能会有问题,需要从根本上解决返回多个的问题

		IfSysSQLCallBack sqlObj;
		Map<String,IfSysSQLHistory> aPairOfifSysSQLHistory;
		if(sqlObjs.size()>0){
			sqlObj = sqlObjs.get(0);
		}else {
			sqlObj = new IfSysSQLCallBack();
		}
		aPairOfifSysSQLHistory = ifSysSQLCallBackService.getAPairOfSQLHistoryBySql(sqlObj);
		List<Map> data = new ArrayList<>();
		data.add(aPairOfifSysSQLHistory);
		reDto.setDataes(data);
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}

	/**
	 * 获取回调sql信息
	 * ifSysMockService.getTestingProgress()
	 */
	@RequestMapping("/getTestingProgress.do")
	public @ResponseBody
	ResponseDto getTestingProgress(){
		ResponseDto reDto = new ResponseDto();
		TestStatus testStatus = ifSysTestStatuService.getTestStatus();
		List<Map> prog = new ArrayList<>();
		Map<String,String> progMap = new HashMap<>();
		if(StringUtil.equals(testStatus.getProValue(),"1")){
			int progres = ifSysMockService.getTestingProgress();
			progMap.put("progress",String.valueOf(progres));
		}else{
			progMap.put("progress","0");
		}
		progMap.put("state",testStatus.getProValue());
		progMap.put("info",testStatus.getProDesc());
		prog.add(progMap);
		reDto.setDataes(prog);
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}

	/**
	 * 修改接口自动测试状态
	 */
	@RequestMapping("/switchAutoTest.do")
	public @ResponseBody
	IfSysSwitchAutoTestDto switchAutoTest(@RequestBody Map<String,Object> dto){
		IfSysSwitchAutoTestDto reDto = new IfSysSwitchAutoTestDto();
		String testTag;
		int ifId;
		if(!dto.containsKey("ifId")) return reDto;
		ifId = (int)dto.get("ifId");
		InterFaceInfo interFaceInfo = interFaceService.getInterFaceById(ifId);
		if(dto.containsKey("testTag")){
			testTag = String.valueOf(dto.get("testTag"));
		}else{
			testTag = "Y".equals(interFaceInfo.getAutoTest())?"N":"Y";
		}
		interFaceInfo.setAutoTest(testTag);
		int count = interFaceService.updateInterfaceInfo(interFaceInfo);
		if(count>0){
			reDto.setRspCd(SysCode.SUCCESS);
			reDto.setIsAutoTest(testTag);
		}else{
			reDto.setRspCd(SysCode.SYS_FAIL);
		}
		return reDto;
	}

	/**
	 * 时间控制接口
	 */
	@RequestMapping("/timeControl.do")
	public @ResponseBody
	ResponseDto timeControl(@RequestBody Map<String,String> dto){
		ResponseDto reDto = new ResponseDto();
		String type,value;
		if(!dto.containsKey("type")) return reDto;
		type = dto.get("type");

		if(!dto.containsKey("value")) return reDto;
		value = dto.get("value");

		switch(type){
			case "delay":
				try{
					int ms = Integer.parseInt(value);
					reDto.setRspInf("成功设置延时"+ms+"毫秒");
					if(ms>30000){
						ms = 30000;// 限制最大延时不超过30秒
						reDto.setRspInf("延时最大限制为30秒");
					}
					Thread.sleep(ms);
					reDto.setRspCd(SysCode.SUCCESS);
				}catch (Exception e){
					reDto.setRspInf(e.getLocalizedMessage());
				};
				break;
			default:break;
		}
		return reDto;
	}
}
