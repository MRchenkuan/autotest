package com.gigold.pay.autotest.bo;

import org.apache.http.Header;
import org.apache.http.StatusLine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/3/24.
 */
public class IfSysMockResponse {
    private String responseStr =""; // 返回报文
    private Map<String,String> headers = new HashMap<>(); // 返回头
    private Map<String,String> requestHeaders=new HashMap<>(); // 请求头
    private StatusLine status;// 状态码等消息

    public StatusLine getStatus() {
        return status;
    }

    public void setStatus(StatusLine statusLine) {
        this.status = statusLine;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
