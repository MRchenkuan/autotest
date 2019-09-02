package com.gigold.pay.autotest.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.gigold.pay.autotest.bo.IfSysMockResponse;
import com.gigold.pay.autotest.service.SSLClient;
import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.framework.util.common.StringUtil;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.gigold.pay.framework.core.Domain;

/**
 * 
 * Title: HttpClientService<br/>
 * Description: 调用汇添富接口<br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年11月10日下午4:54:14
 *
 */
@Service
public class HttpClientService extends AbstractService{
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private static String CHARSET="UTF-8";

	/**
	 * Description: 设置超时<br/>
	 */
	public void setTimeOut(HttpClient httpclient) {
		// 请求超时
		int CONNECT_TIMEOUT = 30 * 1000;
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
		// 读取超时
		int SO_TIMEOUT = 30 * 1000;
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);

	}

	/**
	 * 设置默认 通用 请求头
	 * @param httpRequest 请求
	 */
	public void setHeader(HttpRequest httpRequest, Map<String,String> headMap) {
		httpRequest.setHeader("accept", "*/*");
		httpRequest.setHeader("connection", "Keep-Alive");
		httpRequest.setHeader("Content-Type", "application/json");
		for(String head:headMap.keySet()){
			httpRequest.setHeader(head,headMap.get(head));
		}
	}

	/**
	 * 请求 POST 发送
	 * @param url 请求地址
	 * @param postData 请求包体
	 * @param cookieStore cookie信息
	 * @param extraHeader 额外的包头信息
	 * @return 返回结果
	 * @throws Exception
	 */
	public IfSysMockResponse httpPost(String url, String postData, CookieStore cookieStore, Map<String,String> extraHeader) throws Exception{

		String responseData = "";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		List<Cookie> clist= cookieStore.getCookies();
		for(Cookie c:clist ){
			BasicClientCookie bc=(BasicClientCookie)c;
			bc.setPath("/");
		}
		//设置cookies
		httpclient.setCookieStore(cookieStore);
		HttpPost httppost = new HttpPost(url);
		// 设置超时
		setTimeOut(httpclient);
		// 设置请求头
		setHeader(httppost,extraHeader);


		try {
			setRequestParams(httppost, postData);
		} catch (UnsupportedEncodingException e1) {
			debug("请求参数中存在非法字符");
			e1.printStackTrace();
		}

		IfSysMockResponse ifSysMockResponse = new IfSysMockResponse();

		try {
//			HttpResponse response = httpclient.execute(httppost);
			// 扩展安全的https
			HttpResponse response = doHttpRequest(httpclient,url,httppost);
			if(null!=response) {
				/* 读返回数据 */
				responseData = EntityUtils.toString(response.getEntity(), CHARSET);
				/* 读取返回头 */
				Header[] headers = response.getAllHeaders();
				/* 入库返回消息 */
				ifSysMockResponse.setResponseStr(responseData);
				/* 读返回返回状态行 */
				StatusLine statusLine = response.getStatusLine();
				/* 入库返回状态行 */
				ifSysMockResponse.setStatus(statusLine);
				// 入库返回头
				ifSysMockResponse.setHeaders(arrHeadToMap(headers));
			}
		} catch (Exception e) {
			e.printStackTrace();
			debug("服务器响应失败");
		}finally {
			// 获取所有请求头
			Header[] requestHeaders = httppost.getAllHeaders();
			// 记录请求头
			ifSysMockResponse.setRequestHeaders(arrHeadToMap(requestHeaders));
			// 释放资源
			httppost.abort();
		}
		return ifSysMockResponse;
	}


	/**
	 * 请求 GET 发送
	 * @param url 请求地址
	 * @param cookieStore cookie信息
	 * @param extraHeader 额外的包头信息
	 * @return 返回结果
	 * @throws Exception
	 */
	public IfSysMockResponse httpGet(String url, CookieStore cookieStore, Map<String,String> extraHeader) throws Exception{
		String responseData;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		List<Cookie> clist= cookieStore.getCookies();
		for(Cookie c:clist ){
			BasicClientCookie bc=(BasicClientCookie)c;
			bc.setPath("/");
		}
		//设置cookies
		httpclient.setCookieStore(cookieStore);
		HttpGet httpGet = new HttpGet(url);
		// 设置超时
		setTimeOut(httpclient);
		// 设置请求头
		setHeader(httpGet,extraHeader);

		IfSysMockResponse ifSysMockResponse = new IfSysMockResponse();

		try {
//			HttpResponse response = httpclient.execute(httpGet);
			HttpResponse response = doHttpRequest(httpclient,url,httpGet);
			if(null!=response){
				/* 读返回数据 */
				responseData = EntityUtils.toString(response.getEntity(), CHARSET);
				/* 读取返回头 */
				Header[] headers = response.getAllHeaders();
				/* 入库返回消息 */
				ifSysMockResponse.setResponseStr(responseData);
				/* 读返回返回状态行 */
				StatusLine statusLine = response.getStatusLine();
				/* 入库返回状态行 */
				ifSysMockResponse.setStatus(statusLine);
				// 入库返回头
				ifSysMockResponse.setHeaders(arrHeadToMap(headers));
			}

		} catch (Exception e) {
			e.printStackTrace();
			debug("服务器响应失败");
		}finally {
			// 获取所有请求头
			Header[] requestHeaders = httpGet.getAllHeaders();
			// 记录请求头
			ifSysMockResponse.setRequestHeaders(arrHeadToMap(requestHeaders));
			// 释放资源
			httpGet.abort();
		}

		return ifSysMockResponse;
	}


	/**
	 * 请求 GET 发送
	 * @param url 请求地址
	 * @param cookieStore cookie信息
	 * @param extraHeader 额外的包头信息
	 * @return 返回结果
	 * @throws Exception
	 */
	public IfSysMockResponse httpPut(String url, String postData,  CookieStore cookieStore, Map<String,String> extraHeader) throws Exception{
		String responseData;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		List<Cookie> clist= cookieStore.getCookies();
		for(Cookie c:clist ){
			BasicClientCookie bc=(BasicClientCookie)c;
			bc.setPath("/");
		}
		//设置cookies
		httpclient.setCookieStore(cookieStore);
		HttpPut httpPut = new HttpPut(url);
		// 设置超时
		setTimeOut(httpclient);
		// 设置请求头
		setHeader(httpPut,extraHeader);
		try {
			setRequestParams(httpPut, postData);
		} catch (UnsupportedEncodingException e1) {
			debug("请求参数中存在非法字符");
			e1.printStackTrace();
		}
		IfSysMockResponse ifSysMockResponse = new IfSysMockResponse();

		try {
//			HttpResponse response = httpclient.execute(httpPut);
			HttpResponse response = doHttpRequest(httpclient,url,httpPut);

			if(null!=response){

				/* 读返回数据 */
				responseData = EntityUtils.toString(response.getEntity(), CHARSET);
				/* 读取返回头 */
				Header[] headers = response.getAllHeaders();
				/* 入库返回消息 */
				ifSysMockResponse.setResponseStr(responseData);
				/* 读返回返回状态行 */
				StatusLine statusLine = response.getStatusLine();
				/* 入库返回状态行 */
				ifSysMockResponse.setStatus(statusLine);
				// 入库返回头
				ifSysMockResponse.setHeaders(arrHeadToMap(headers));
			}
		} catch (Exception e) {
			e.printStackTrace();
			debug("服务器响应失败");
		}finally {
			// 获取所有请求头
			Header[] requestHeaders = httpPut.getAllHeaders();
			// 记录请求头
			ifSysMockResponse.setRequestHeaders(arrHeadToMap(requestHeaders));
			// 释放资源
			httpPut.abort();
		}

		return ifSysMockResponse;
	}
	

	/**
	 * 设置参数和请求头
	 * @param httppost 请求对象
	 * @param requestData 请求参数
	 * @throws UnsupportedEncodingException
	 */
	public void setRequestParams(HttpPost httppost, String requestData) throws UnsupportedEncodingException {

		StringEntity entity = new StringEntity(requestData, CHARSET);// 解决中文乱码问题
		entity.setContentEncoding(CHARSET);
		httppost.setEntity(entity);
	}

	/**
	 * 设置参数和请求头
	 * @param httpreq 请求对象
	 * @param requestData 请求参数
	 * @throws UnsupportedEncodingException
     */
	public void setRequestParams(HttpPut httpreq, String requestData) throws UnsupportedEncodingException {

		StringEntity entity = new StringEntity(requestData, CHARSET);// 解决中文乱码问题
		entity.setContentEncoding(CHARSET);
		httpreq.setEntity(entity);
	}

	/**
	 * 将数组类型的head转换为Map类型
	 * @param headers 数组类型head
	 * @return map类型head
     */
	public static Map<String, String> arrHeadToMap(Header [] headers){
		Map<String,String> _headrs = new HashMap<>();
		if (headers==null || headers.length<=0)return _headrs;
		for (Header header : headers) {
			_headrs.put(header.getName(), header.getValue());
		}
		return _headrs;
	}


	/**
	 * 进行 带ssl的http 请求
	 * @param httpClient 普通http客户端
	 * @param url 请求地址
     * @return 返回response
     */
	public HttpResponse doHttpRequest(DefaultHttpClient httpClient,String url ,HttpUriRequest httpUriRequest){
		HttpResponse response = null;
		DefaultHttpClient httpsClient;
		// 判断发送类型
		if(StringUtil.isEmpty(url)){
			return null;
		}
		String protocol = url.trim().indexOf("https://")==0?"https":"http";
		try{
			httpsClient = new SSLClient();
			// 设置cookie
			if(httpClient.getCookieStore()!=null)httpsClient.setCookieStore(httpClient.getCookieStore());
			// 设置超时
			setTimeOut(httpsClient);
			// 判断发送类型
			switch (protocol) {
				case "http":
					response = httpClient.execute(httpUriRequest);
					break;
				case "https":
					response = httpsClient.execute(httpUriRequest);
					break;
				default:
					throw new Exception("无法判断请求是https还是http");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return response;
	}

}
