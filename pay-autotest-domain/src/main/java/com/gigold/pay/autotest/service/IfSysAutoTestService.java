/**
 * Title: IfSysAutoTestService.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.service;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gigold.pay.autotest.bo.*;
import com.gigold.pay.autotest.datamaker.GlobalVal;
import com.gigold.pay.autotest.dataorigin.DataOriginBase;
import com.gigold.pay.autotest.jrn.JrnGeneratorService;
import com.gigold.pay.autotest.util.MD5Uitl;
import com.gigold.pay.framework.service.AbstractService;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gigold.pay.autotest.httpclient.HttpClientService;
import com.gigold.pay.framework.core.Domain;
import com.gigold.pay.framework.util.common.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//import javax.xml.crypto.Data;

/**
 * Title: IfSysAutoTestService<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月5日下午4:56:57
 *
 */
@Service
public class IfSysAutoTestService extends AbstractService {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Autowired
	HttpClientService httpClientService;
	@Autowired
	IfSysMockService ifSysMockService;
	@Autowired
	IfSysSQLCallBackService ifSysSQLCallBackService;
	@Autowired
	IfSysReferService ifSysReferService;
	@Autowired
	InterFaceService interFaceService;
	@Autowired
	IfSysAssertRuleService ifSysAssertRuleService;
	@Autowired
	InterFaceSysService interFaceSysService;
	@Autowired
	JrnGeneratorService jrnGrneratorService;
	@Autowired
	private RetrunCodeService retrunCodeService;

	public void writeBackContent(IfSysMock mock) {
		String testResulte;
		List<IfSysAssertRule> assertRules = ifSysAssertRuleService.getAssertRulesByMock(mock);

		// 获得真实返回json
		JSONObject jsonObject;
		String responseJson = mock.getRealResponseJson();
		try {
			jsonObject = JSONObject.fromObject(responseJson);
		} catch (Exception e) {
			jsonObject = new JSONObject();
		}
		// 初始化两个信息
		String relRspCode = String.valueOf(jsonObject.get("rspCd"));
		String relRspInfo = String.valueOf(jsonObject.get("rspInf"));

		// 开始断言
		if(assertRules.size()>0){
			// 规则断言模式
			testResulte = assertMock(mock,assertRules);
		} else if(mock.getRspCode().equals("NOCODE")){
			// 网络状态码断言模式
			String responseHeaders = mock.getRealResponseHead();
			Map<String,String> headers = strHeadToMap(responseHeaders);
			if(headers.containsKey("[StatusCode]")&&"200".equals(headers.get("[StatusCode]"))&&!headers.containsKey("API-Error-Code")){
				testResulte = "1";
			}else {
				testResulte = "0";
			}
			relRspCode = headers.get("[StatusCode]");
			relRspInfo = headers.get("[Status]");

			if(headers.containsKey("API-Error-Code"))relRspCode="API-Error-Code:"+headers.get("API-Error-Code");
			if(headers.containsKey("API-Error-Message"))relRspInfo="API-Error-Message:"+headers.get("API-Error-Message");
			// 记录返回码历史
		}else{
			// 返回码断言模式

			// 1-正常 0-失败 -1-请求或响应存在其他异常
			if (relRspCode.equals(mock.getRspCode())) {// 返回码与预期一致
				testResulte ="1";
			} else if (StringUtil.isNotEmpty(relRspCode)&&(!relRspCode.equals("null"))) {// 返回码与预期不一致,但不为空
				testResulte ="0";
			} else { // 返回码与预期不一致,或为空,或为其他
				testResulte="-1";
			}
			relRspInfo = String.valueOf(jsonObject.get("rspInf"));

		}

		// 记录返回码历史
		retrunCodeService.recordOneHis(mock.getIfId(),relRspCode,relRspInfo);
		mock.setTestResult(testResulte);
		mock.setRealRspCode(relRspCode);
		ifSysMockService.writeBackRealRsp(mock);

	}

	/**
	 * 规则断言方法
	 * @param mock 实际用例
	 * @param assertRules 断言规则
	 * @return 断言结果
     */
	private String assertMock(IfSysMock mock, List<IfSysAssertRule> assertRules) {
		try {
			for (IfSysAssertRule assertRule : assertRules) {
				String asClass = assertRule.getAsClass();
				String asObj = assertRule.getAsObj();
				String condition = assertRule.getAsType();
				String asValue = assertRule.getAsValue();

				Map<String, String> realHeaders = strHeadToMap(mock.getRealResponseHead());
				// 获得真实返回json
				JSONObject jsonObject;
				String responseJson = mock.getRealResponseJson();
				try {
					jsonObject = JSONObject.fromObject(responseJson);
				} catch (Exception e) {
					jsonObject = new JSONObject();
				}
				// 初始化两个信息
				String relRspCode = String.valueOf(jsonObject.get("rspCd"));
				String relRspInfo = String.valueOf(jsonObject.get("rspInf"));

				// 获取左值
				String leftVal;
				switch (asClass) {
					case "HEADERS":
						leftVal = realHeaders.get(asObj);
						break;
					case "STATUSCODE":
						leftVal = realHeaders.get("[StatusCode]");
						break;
					case "JSON":
						leftVal = gatJsonValByPath(responseJson, asObj);
						break;
					case "RSPCODE":
						leftVal = relRspCode;
						break;
					case "TEXT":
						leftVal = responseJson;
						break;
					default:
						leftVal = relRspCode;
						break;
				}

				// 获取右值 ,目前只做全值和正则判断
				String rightVal = asValue;
				Pattern pattern = Pattern.compile(rightVal);
				Matcher matcher = pattern.matcher(leftVal);
				// 获取判定条件,并得出结论
				switch (condition) {
					case "INCLUDE":// 包含
						if (!matcher.find()) {
							return "0";
						}
						break;
					case "EXCLUDE":// 不包含
						if (matcher.find()) {
							return "0";
						}
						break;
					case "ISEMPTY":// 为空
						if (StringUtil.isNotEmpty(rightVal)) {
							return "0";
						}
						break;
					case "NOTEMPTY":// 不为空
						if (StringUtil.isEmpty(rightVal)) {
							return "0";
						}
						break;
					case "=":// 严格相等
						boolean isFloatEqule;
						try {
							isFloatEqule = Float.parseFloat(leftVal) == Float.parseFloat(rightVal);
						} catch (Exception e) {
							isFloatEqule = false;
						}
						if (!StringUtil.equals(leftVal, rightVal) && !isFloatEqule) {
							return "0";
						}
						break;
					case ">":
						try {
							if (Float.parseFloat(leftVal) > Float.parseFloat(rightVal)) {
								continue;
							} else {
								return "0";
							}
						} catch (Exception e) {
							return "0";
						}
					case "<":
						try {
							if (Float.parseFloat(leftVal) < Float.parseFloat(rightVal)) {
								continue;
							} else {
								return "0";
							}
						} catch (Exception e) {
							return "0";
						}
					default:
						return "0";
				}
			}
			return "1";
		}catch (Exception e){
			e.printStackTrace();
			return "0";
		}
	}


	/**
	 * 自动化测试核心代码 测试用例之间的依赖
	 * @param interFaceInfo 每页接口用例信息
	 */
	public void autoTest(InterFaceInfo interFaceInfo) {
		// 获取接口访问的完整地址
		String url = getAddressUrl(interFaceInfo.getAddressUrl(), interFaceInfo.getIfUrl());
		String ifType = interFaceInfo.getMethod();
		// 调用接口所有的测试用例
		for (IfSysMock mock : interFaceInfo.getMockList()) {
			//判断用例是否有被依赖
			List<IfSysRefer> listRef=ifSysReferService.getReferByRefMockId(mock.getId());
			//如果用例被其他用例依赖了 则进入下一次循环
			if(listRef!=null&&listRef.size()!=0){
				continue;
			}
			// 设置接口访问的完整地址
			mock.setAddressUrl(url);
			// 设置接口访问方式
			if(ifType!=null)mock.setIfType(ifType.trim().toUpperCase());
			// 设置接口访问的主机地址
			mock.setSysUrl(interFaceInfo.getAddressUrl());
			// 1、获取该测试用例调用时依赖的其他用例的调用列表
			List<IfSysMock> invokerOrderList = new ArrayList<IfSysMock>();
			// 第一位放入目标接口
			invokerOrderList.add(mock);
			// 然后加入依赖用例
			invokerOrder(invokerOrderList, mock.getId());
			// 存放依赖的cookies
			CookieStore cookieStore=new BasicCookieStore();
			// 存放依赖的header
			TreeMap<String, String> headerStore = new TreeMap<>();
			// 按照调用序号依次调用被依赖测试用例,设置需要回写历史记录
			invokRefCase(invokerOrderList,cookieStore,headerStore);
		}

	}

	/**
	 * 单个用例测试执行
	 * @param mockid 待执行测试的测试用例
     */
	public boolean testMock(int mockid) {
		try {
			/* 获取被测mock */
			IfSysMock mock = new IfSysMock();
			mock.setId(mockid);
			mock =	ifSysMockService.getMockInfoById(mock);

			/* 初始化测试数据 */
			InterFaceInfo interFaceInfo = interFaceService.getInterFaceById(mock.getIfId());
			String url = getAddressUrl(interFaceInfo.getAddressUrl(), interFaceInfo.getIfUrl());
			String ifType = interFaceInfo.getMethod();
			// 确定真实地址
			mock.setAddressUrl(url);
			// 确定主机地址
			mock.setSysUrl(interFaceInfo.getAddressUrl());
			// 确定接口发送方式
			if(ifType!=null)mock.setIfType(ifType.trim().toUpperCase());
			/* 构建测试列表 */
			List<IfSysMock> invokerOrderList = new ArrayList<IfSysMock>();
			// 第一位放入目标接口
			invokerOrderList.add(mock);
			// 然后加入依赖用例
			invokerOrder(invokerOrderList, mock.getId());
			// 存放依赖的cookies
			CookieStore cookieStore=new BasicCookieStore();
			// 存放依赖的header
			TreeMap<String, String> headerStore = new TreeMap<>();
			// 按照调用序号依次调用被依赖测试用例,并设置不需要回写历史记录
			invokRefCase(invokerOrderList,cookieStore,headerStore);
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * 按调用序号依次调用被依赖测试用例
	 * @param invokerOrderList 用例调用列表
	 * @param cookieStore 用来维持cookie的变量
     */
	public void invokRefCase(List<IfSysMock> invokerOrderList,CookieStore cookieStore,TreeMap<String,String> headerStore) {
		/**
		 * 初始化持续变量
		 */
		Map<Integer,String> allRespMap = new HashMap<>();// 临时变量,存放各个依赖用例的包体返回
		Map<Integer,Map<String,String>> allHeadMap = new HashMap<>();// 临时变量,存放各个依赖用例的头部
		Map<String,String> replacedStrs = new HashMap<>();// 已替换变量,存放占位符替换的变量
		Map<String, String> responseHead = null; // 接口返回头信息
		Map<String, String> extraHeaders = new TreeMap<>();// 获取额外头部
		String md5="";
		int mockId = 0;
		for (int i = invokerOrderList.size() - 1; i >= 0; i--) {
			String responseJson = ""; // 接口返回字符串
			StatusLine responseStatus; // 接口返回状态
			String responseStatusStr=""; // 接口返回状态字符串
			int responseStatusCode=0; // 接口返回状态码
			String realddressUrl = ""; // 真实请求url
			String postData = ""; // 请求报文
			IfSysMock refmock = null;

			try {
				refmock = invokerOrderList.get(i);
				mockId = refmock.getId();
				// 替换请求报文
				postData = refmock.getRequestJson();
				/**
				 * 请求组装
				 */
				// 替换占位符
				if (StringUtil.isNotEmpty(postData)) {
					postData = replaceHolder(postData, mockId, allRespMap, allHeadMap, replacedStrs);
				} else {
					postData = "";
				}
				refmock.setRealRequestJson(postData);

				try {
					md5 = MD5Uitl.ecodeByMD5(postData, GlobalVal.get("sys.md5key"));
				}catch (Exception e){
					e.printStackTrace();
				}

				/**
				 * 头部组装
				 */

				String reqHead = refmock.getRequestHead();
				// 判断头部是否存在
				if (StringUtil.isNotEmpty(reqHead)) {
					// 替换额外头部
					reqHead = replaceHolder(reqHead, mockId, allRespMap, allHeadMap, replacedStrs);
				}
				// 重组额外头部
				if (StringUtil.isNotEmpty(reqHead)) {
					extraHeaders = strHeadToMap(reqHead);
				}
				// 合并: 外部头部, 额外头部,移除空头部
				headerStore.put("md5",md5);// 加入签名
				headerStore.putAll(extraHeaders);// 合入外部头部
				extraHeaders = headerStore;

				/**
				 * 地址组装
				 */
				// 替换占位符
				realddressUrl = refmock.getRequestPath();
				if (StringUtil.isBlank(realddressUrl)) {
					// 若真实地址不存在则用接口地址
					realddressUrl = refmock.getAddressUrl();
				} else {
					// 若存在则直接使用,先要替换占位符
					realddressUrl = replaceHolder(realddressUrl, mockId, allRespMap, allHeadMap, replacedStrs);
					realddressUrl = getAddressUrl(refmock.getSysUrl(), realddressUrl);
				}
				// 回写真实请求地址
				refmock.setRealRequestPath(realddressUrl);


				/**
				 * 发送请求
				 */
				// 确认请求方式并发送请求
				String method = refmock.getIfType();
				IfSysMockResponse ifSysMockResponse = new IfSysMockResponse();
				if(method==null){method = "";}// 防止异常
				long timeMillis = System.currentTimeMillis();
				try{
					switch (method){
						case "POST"	:	ifSysMockResponse = httpClientService.httpPost(realddressUrl, postData,cookieStore,extraHeaders);break;
						case "GET"	:	ifSysMockResponse = httpClientService.httpGet(realddressUrl,cookieStore,extraHeaders);break;
						case "PUT"	:	ifSysMockResponse = httpClientService.httpPut(realddressUrl, postData,cookieStore,extraHeaders);break;
						default		:	ifSysMockResponse = httpClientService.httpPost(realddressUrl, postData,cookieStore,extraHeaders);break;
					}
				}catch (Exception e){
					// 发送目标接口异常
					new Exception("发送目标接口执行失败/超时").getStackTrace();
					String _responsStr = ifSysMockResponse.getResponseStr();
					if(StringUtil.isEmpty(_responsStr))_responsStr="";
					ifSysMockResponse.setResponseStr("目标接口异常或超时:\n"+_responsStr+"响应耗时:"+String.valueOf(System.currentTimeMillis()-timeMillis)+" ms");
				}


				// 处理返回为空的情况
				if(ifSysMockResponse==null)throw new Exception("请求返回null");
				responseJson = ifSysMockResponse.getResponseStr();
				responseHead = ifSysMockResponse.getHeaders();
				responseStatus = ifSysMockResponse.getStatus();
				// 设置服务器返回状态行
				if(responseStatus==null){
					responseStatusStr="服务器没有响应";
				}else{
					responseStatusStr=responseStatus.toString();
					responseStatusCode = responseStatus.getStatusCode();
				}
				if(StringUtil.isEmpty(responseStatusStr))responseStatusStr="服务器没有响应";
				// 返回头中加入http状态信息
				responseHead.put("[Status]",responseStatusStr);
				responseHead.put("[StatusCode]",String.valueOf(responseStatusCode));
				responseHead.put("[响应耗时]",String.valueOf(System.currentTimeMillis()-timeMillis)+" ms");

				// 拿到真实请求头
				Map<String,String> reqHeaders = ifSysMockResponse.getRequestHeaders();
				List<Cookie> cks = cookieStore.getCookies();
				// 请求头中加入cookie
				if(cks!=null&&cks.size()>0)
					reqHeaders.put("[Cookie]",stringfiyCookiesList(cks));
				reqHeaders.put("[Method]",method);
				// 回写真实请求头
				refmock.setRealRequestHead(mapHeadToStr(reqHeaders));

				// 记录当次请求结果
				allRespMap.put(mockId,responseJson);// 返回json
				allHeadMap.put(mockId,responseHead);// 返回头

			}catch (Exception e){
				e.printStackTrace();
			}finally {
				// 回写真实返回json
				if(responseJson!=null)refmock.setRealResponseJson(responseJson);
				// 回写真实返回头
				if(responseHead!=null){refmock.setRealResponseHead(mapHeadToStr(responseHead));}
				// 记录其他测试信息
				writeBackContent(refmock);
			}

			/**
			 * 回调组装
			 */
			// 若是最后一个,并在回调sql则执行回调
			if(i==0){
				// 查询当条mock的sql
				List<IfSysSQLCallBack> sqlObjs = ifSysSQLCallBackService.getMockSQLByMockId(mockId);
				for(IfSysSQLCallBack sqlObj : sqlObjs){
					String sql = sqlObj.getSql();
					if(StringUtil.isEmpty(sql))continue;
					// 替换占位符
					if(StringUtil.isNotEmpty(sql)){
						sql = replaceHolder(sql,mockId,allRespMap,allHeadMap, replacedStrs);
					}else{
						sql="";
					}
					// 执行当条mock的sql
					sqlObj.setRealSql(sql);
					Map<String,Object> rs = ifSysSQLCallBackService.excuteSql(sqlObj);
					List rslist=null;
					// 0.根据sqlid和mockid查询当前mock对应的sql结果,若不存在,则新增
					Map<String,IfSysSQLHistory> aPairOfifSysSQLHistory  = ifSysSQLCallBackService.getAPairOfSQLHistoryBySql(sqlObj);
					IfSysSQLHistory latestHis =  aPairOfifSysSQLHistory.get("new");
					IfSysSQLHistory oldestHis =  aPairOfifSysSQLHistory.get("old");
					// 1.将原先为2的设为1
					latestHis.setOrder(1);
					// 2.将原先为1设为2,并替换为当前结果,储存正常信息
					oldestHis.setOrder(2);
					if("ok".equals(String.valueOf(rs.get("Exception")))){
						// 拿到执行结果
						rslist = (List) rs.get("rs");
						// rslist 序列化然后储存
						JSONArray rsJson = JSONArray.fromObject(rslist);
						oldestHis.setExceptions("ok");
						oldestHis.setResulte(rsJson.toString());
					}else{
						oldestHis.setExceptions(String.valueOf(rs.get("Exception")));
						oldestHis.setResulte("");
					}

					// 回写当条sql的结果
					oldestHis.setSql(sql);
					// 回写正常信息
					ifSysSQLCallBackService.saveSQLResulte(latestHis);
					ifSysSQLCallBackService.saveSQLResulte(oldestHis);
				}
			}

		}
	}

	/**
	 * 替换接口占位符依赖,支持报文替代和包头替代
	 * @param requestStr 原始请求参数
	 * @param mockid 目标用例
	 * @param allRespMap 依赖用例的所有返回<用例id,用例返回字符串>
	 * @param allHeadMap 依赖用例的所有头部返回<用例id,用例头部列表>
	 * @param replacedStrs 依赖用例的所有返回<占位符,占位符取值>
     * @return 替换后的字符串
     */
	public String replaceHolder(String requestStr,int mockid,Map<Integer,String> allRespMap,Map<Integer,Map<String,String>> allHeadMap,Map<String,String> replacedStrs){
		try {
			// 1.获取当前接口所依赖的所有字段,
			List<IfSysFeildRefer> referFields=ifSysReferService.queryReferFields(mockid);
			for(IfSysFeildRefer referField :referFields){
				//2.根据返回字段,替换当前报文; 别名|mockid|feild 依次遍历 allRespMap
				int nowMockId = referField.getRef_mock_id(); // 当前用例数据的id
				String path = referField.getRef_feild(); // 当前用例数据所依赖的域
				String type = referField.getType();
				// 根据每一个依赖的用例,在临时变量中查询出记录的返回的 json
				String backJson = allRespMap.get(nowMockId);
				// 根据每一个依赖的用例,在临时变量中查询出记录的返回的 Head
				Map<String,String> backHead = allHeadMap.get(nowMockId);


				// 得到返回字段
				String backHeadField ="";
				String backField = "";

				if(StringUtil.isNotEmpty(backJson))// 空校验
					backField = gatJsonValByPath(backJson,path);
				if(backHead!=null) // 空校验
					backHeadField = backHead.get(path);

				// 分别替换包头和包体依赖
				if(requestStr.contains(referField.getAlias())){
					// 引用字段
					if("BODY".equalsIgnoreCase(type)){
						if(StringUtil.isNotEmpty(backField))
						requestStr = requestStr.replace(referField.getAlias() ,backField);
					}

					// 引用包头
					if("HEAD".equalsIgnoreCase(type)){
						if(StringUtil.isNotEmpty(backHeadField))
						requestStr = requestStr.replace(referField.getAlias() ,backHeadField);
					}
				}


			}

			/**
			 * 替换数据源如:唯一手机号, 唯一邮箱号, 唯一身份证号, 当前日期 等
			 */
			requestStr = DataOriginBase.fillDate(requestStr,replacedStrs);


		} catch (Exception e) {
			System.out.println("出错的mockId为:"+mockid);
			e.printStackTrace();
		}
		return requestStr.trim();
	}



	/**
	 * 获取该测试用例调用时依赖的其他用例的调用列表
	 * @param invokerOrderList 接口调用列表
	 * @param mockId 目标接口ID
     */
	public void invokerOrder(List<IfSysMock> invokerOrderList, int mockId) {
		// 获取被测用例依赖其他用例的列表
		List<IfSysRefer> referList = ifSysReferService.getReferList(mockId);
		// 如果有依赖 遍历依赖 列表
		for (int i = referList.size() - 1; i >= 0; i--) {
			IfSysRefer refer = referList.get(i);
			// 获取被依赖的用例数据
			IfSysMock mock = ifSysMockService.getReferByIfId(refer.getRefMockId());
			if(mock!=null){
				String url = getAddressUrl(mock.getAddressUrl(), mock.getIfURL());
				mock.setSysUrl(mock.getAddressUrl());
				mock.setAddressUrl(url);
				invokerOrderList.add(mock);
			}
			
			// 如果被依赖测试用例还依赖其他测试用例
			invokerOrder(invokerOrderList, refer.getRefMockId());
		}
	}


	/**
	 * 获取接口完整地址
	 * @param url 系统地址
	 * @param action 接口path
     * @return 返回完整地址
     */
	public String getAddressUrl(String url, String action) {
		String addressUrl = "";
		if (StringUtil.isNotBlank(url) && StringUtil.isNotBlank(action)) {
			url=url.trim();
			action=action.trim();
			if (url.endsWith("/")) {
				if(action.startsWith("/")){
					addressUrl=url+action.substring(1);
				}else{
					addressUrl = url + action;
				}
				
			} else {
				if(action.startsWith("/")){
					addressUrl=url+action;
				}else{
					addressUrl = url + "/" + action;
				}
				
				
			}
		}
		return addressUrl;
	}

	public static String getHeadValuebyKey(String headString ,String key){
		String[] headsArr = headString.split("\n");
		for(String head:headsArr){
			String _key = head.substring(0,head.indexOf(":"));
			String _val = head.substring(head.indexOf(":")+1);
			_key=_key.trim();key=key.trim();
			if(StringUtil.isNotEmpty(_key)&&_key.equals(key)){
				return _val;
			}
		}
		return "";
	}

	/**
	 * 根据表达式 从json字符串中取值
	 * @param jsonString json字符串
	 * @param field 查找表达式
     * @return 返回字符串
     */
	public static String gatJsonValByPath(String jsonString, String field){
		JSONObject json;
		// 判断传入字符串是否为空
		if(jsonString==null||jsonString.isEmpty()||field.isEmpty())return "";
		try {
			json = JSONObject.fromObject(jsonString);
		} catch (Exception e) {
			json = new JSONObject();
		}

		// 替换数组[]为.
		field = field.replaceAll("\\[",".");
		field = field.replaceAll("]\\.",".");
		field = field.replaceAll("]",".");
		field = field.replaceAll("\\.$","");
		// 逐级查找path对应的值
		String[] path = field.split("\\.");
		for(int i = 0; i<path.length;i++){
			if(json!=null && !json.isEmpty() && json.get(path[i]) instanceof JSONArray){
				JSONArray jsonArr = JSONArray.fromObject(json.get(path[i]));
				if(i>=path.length-1){
					// 若下一个位置在path[]中已经超标,
					// 则当前jsonArray对象已经是最后的位置,
					// 直接返回即可
					return jsonArr.toString();
				}else{
					int idxOfJsonArr = Integer.parseInt(path[i+1]); //path的下一个位置转为整型就是所需值的下标
					json=jsonArr.getJSONObject(idxOfJsonArr);
					i++;
				}
			}else{
				if(i>=path.length-1){
					// 判断是否有值
					if(json!=null && json.containsKey(path[i])){
						return json.get(path[i]).toString();
					}else{
						return "";
					}
				}

				if(json!=null && json.containsKey(path[i])){
					json = JSONObject.fromObject(json.get(path[i]));
				}else{
					return "";
				}
			}
		}
		return json.toString();
	}

	/**
	 * 头信息转换成字符串
	 * @param headers map类型的头信息
	 * @return 字符串类型的头
     */
	public static String mapHeadToStr(Map<String,String> headers){

		if(headers!=null){
			// 排序
			TreeMap<String,String>_headers = new TreeMap<>(headers);
			String headStr = "";
			for(String ahead : _headers.keySet()){
				headStr += ahead+":"+_headers.get(ahead)+"\n";
			}
			return headStr;
		}else {
			return "";
		}
	}

	/**
	 * 头信息转 Map
	 * @param headers 字符串类型头信息
	 * @return map 类型头信息
     */
	public static Map<String,String> strHeadToMap(String headers){
		Map<String,String> headMap = new TreeMap<>();
		if(StringUtil.isNotEmpty(headers)){
			String[] headArr = headers.trim().split("\n");
			for(String aheader:headArr){
				String key,val = "";
				int seqposition = 0;
				if(aheader.indexOf(":")>0 && aheader.indexOf(":")<aheader.length()-1 ){
					seqposition = aheader.indexOf(":");
					key = aheader.substring(0,seqposition);
					val = aheader.substring(seqposition+1);
				}else{
					key = aheader;
					val = "";
				}
				headMap.put(key,val);
			}
		}
		return headMap;
	}

	public static String stringfiyCookiesList(List<Cookie> cookieList){
		String cksstr = "";
		if(cookieList==null||cookieList.size()<=0)return cksstr;

		for(Cookie cookie : cookieList){
			cksstr +="; "+cookie.getName()+"="+cookie.getValue();
		}
		return cksstr.substring(1);
	}
}
