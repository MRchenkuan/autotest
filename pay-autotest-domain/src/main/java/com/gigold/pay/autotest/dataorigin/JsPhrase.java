package com.gigold.pay.autotest.dataorigin;

import javax.script.ScriptEngineManager;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;   /**  * 直接调用js代码  */

/**
 * Created by chenkuan
 * on 16/8/24.
 */
public class JsPhrase implements DataOriginMaker{
    private static final String replaceHolder = "(#\\{EXEC-JS\\{(.*)}}#)";

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("javascript");

    public JsPhrase(){
        try {
            //扩充data.format方法
            engine.eval("Date.prototype.format = function(fmt){var o = { \"M+\" : this.getMonth()+1,\"d+\" : this.getDate(),\"H+\" : this.getHours(),\"m+\" : this.getMinutes(),\"s+\" : this.getSeconds(),\"q+\" : Math.floor((this.getMonth()+3)/3),\"S\": this.getMilliseconds()};if(/(y+)/.test(fmt)) fmt=fmt.replace(RegExp.$1, (this.getFullYear()+\"\").substr(4 - RegExp.$1.length));for(var k in o) if(new RegExp(\"(\"+ k +\")\").test(fmt))fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : ((\"00\"+ o[k]).substr((\"\"+ o[k]).length)));return fmt;}");
        }catch(ScriptException e){
            e.printStackTrace();
        }
    }

    // 计算结果
    public String getJsCodeResulte(String jsCode){
        // 防止js注入: 左右括号
        if(jsCode.matches("[\\s;]*}(.*)\\{[\\s;]*")){
            return jsCode;
        }
        engine.put("code",jsCode);

        try{
            Object rs = engine.eval("eval(code) + \"\"");
            return String.valueOf(rs);
        }catch(ScriptException e){
            e.printStackTrace();
            return jsCode;
        }
    }


    @Override
    public String process(String requestStr, Map<String, String> replacedStrs) {
        // 替换时间序列
        Pattern pattern = Pattern.compile(replaceHolder);
        Matcher matcher = pattern.matcher(requestStr);
        while(matcher.find()) { // 存在则替换
            String placeHolder = matcher.group(1);
            String jsCode = matcher.group(2);
            String rs = getJsCodeResulte(jsCode);
            requestStr = requestStr.replace(placeHolder,rs);
        }
        return requestStr;
    }

    public static void main(String[]args){

    }
}

