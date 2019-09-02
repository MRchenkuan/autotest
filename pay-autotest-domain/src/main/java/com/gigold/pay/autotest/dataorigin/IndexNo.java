package com.gigold.pay.autotest.dataorigin;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/1/29.
 */
public class IndexNo implements DataOriginMaker{
    private static final String replaceHolder = "#{CONST-INDEX-23}";

    @Override
    public String process(String requestStr, Map<String, String> replacedStrs) {
        // 替换23位唯一索引
        if(requestStr.contains(replaceHolder)){ // 存在则替换
            if(replacedStrs.containsKey(replaceHolder)){// 若整个会话中已经存过当前值则直接用
                requestStr = requestStr.replace(replaceHolder,replacedStrs.get(replaceHolder) );
            }else{// 否则新取一个,然后刷新列表
                Format format = new SimpleDateFormat("yyyyMMddm");
                String _str = format.format(new Date())+String.valueOf(System.currentTimeMillis());
                requestStr = requestStr.replace(replaceHolder,_str);
                replacedStrs.put(replaceHolder,_str);
            }
        }
        return requestStr;
    }
}
