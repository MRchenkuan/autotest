package com.gigold.pay.autotest.dataorigin;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/1/29.
 */
public class NowData implements DataOriginMaker{
    private static final String replaceHolder = "#{CONST-NOW-DATA}";

    @Override
    public String process(String requestStr, Map<String, String> replacedStrs) {
        // 替换当前日期
        if(requestStr.contains(replaceHolder)){
            Format format = new SimpleDateFormat("yyyy-MM-dd");
            requestStr = requestStr.replace(replaceHolder,format.format(new Date()));
        }
        return requestStr;
    }
}
