package com.gigold.pay.autotest.dataorigin;


import com.gigold.pay.autotest.datamaker.DBconnector;
import com.gigold.pay.framework.util.common.StringUtil;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenkuan
 * on 16/1/29.
 */
public class DateSequences implements DataOriginMaker{
    static String replaceHolder="(#\\{CONST-DATE-([MYD])(-[myd])?})";

    public static Map<String,String> getDate(String type){
        DBconnector dBconnector = null;
        Map<String,String> dateMap = new HashMap<>();
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("SELECT `DATE` FROM `T_UI_LAST_DATE_SEQUENCE` where type='"+type+"' limit 1");
            rs.next();
            String[] dateArr = rs.getString("DATE").split("-");
            dateMap.put("y",dateArr[0]);
            dateMap.put("m",dateArr[1]);
            dateMap.put("d",dateArr[2]);
            dateMap.put("date",rs.getString("DATE"));
            return dateMap;
        } catch (Exception ignore) {
            ignore.printStackTrace();
        } finally {
            if(dBconnector!=null)
            dBconnector.close();
        }
        return dateMap;
    }

    public static Map<String,String> renewDate(String type) {
        DBconnector dBconnector = null;
        Map<String,String> dateMap = new HashMap<>();
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("SELECT `type`,`DATE` FROM `T_UI_LAST_DATE_SEQUENCE` where type='"+type+"' limit 1");
            rs.next();
            String dateStr = rs.getString("DATE");
            String[] dateArr = dateStr.split("-");
            int year = Integer.parseInt(dateArr[0]);
            int month = Integer.parseInt(dateArr[1]);
            int day = Integer.parseInt(dateArr[2]);
            switch (type){
                case "month":
                    if(++month>12){
                        year++;
                        month=1;
                    }
                    break;
                case "year":
                    year++;
                    break;
                case "day":
                    if(++day>30){
                        day=1;
                        if(++month>12){
                            year++;
                            month=1;
                        }
                    }
                    break;
                default:break;
            }

            String m = ("0"+String.valueOf(month)).substring(("0"+String.valueOf(month)).length()-2);
            String d = ("0"+String.valueOf(day)).substring(("0"+String.valueOf(day)).length()-2);
            dateMap.put("y",String.valueOf(year));
            dateMap.put("m",m);
            dateMap.put("d",d);
            dateMap.put("date",dateMap.get("y")+"-"+dateMap.get("m")+"-"+dateMap.get("d"));

            dBconnector.updata("update T_UI_LAST_DATE_SEQUENCE set `DATE`='"+dateMap.get("y")+"-"+dateMap.get("m")+"-"+dateMap.get("d")+"' WHERE type='"+type+"'");
            return dateMap;
        } catch (Exception ignore) {

        } finally {
            if(dBconnector!=null)
            dBconnector.close();
        }
        return null;
    }

    @Override
    public String process(String requestStr, Map<String, String> replacedStrs) {
        // 替换时间序列
        Pattern pattern = Pattern.compile(replaceHolder);
        Matcher matcher = pattern.matcher(requestStr);
        List<String> replaceType = new ArrayList<>();
        while(matcher.find()){ // 存在则替换
            String realHolder = matcher.group(1);
            String type = matcher.group(2);
            String val = matcher.group(3);// y m d
            if(StringUtil.isEmpty(val))val="";

            if(replacedStrs.containsKey(realHolder)){// 若整个会话中已经存过当前值则直接用
                requestStr = requestStr.replace(realHolder,replacedStrs.get(realHolder) );
            }else{// 否则新取一个,然后刷新列表
                String dateStr ="";
                Map<String,String> dateMap = null;

                // 按年月日获取日期
                String typeStr;
                switch (type){
                    case "Y":
                        typeStr = "year";
                        break;
                    case "M":
                        typeStr = "month";
                        break;
                    case "D":
                        typeStr = "day";
                        break;
                    default:
                        typeStr = "month";
                        break;
                }
                dateMap = getDate(typeStr);

                if(!replaceType.contains(typeStr)){
                    // 更新时间序列
                    renewDate(typeStr);
                    replaceType.add(typeStr);
                }


                // 取年或者月或者日
                if(dateMap!=null)
                    switch (val){
                        case "-y":
                            dateStr = dateMap.get("y");
                            break;
                        case "-m":
                            dateStr = dateMap.get("m");
                            dateStr = String.valueOf(Integer.parseInt(dateStr));
                            break;
                        case "-d":
                            dateStr = dateMap.get("d");
                            dateStr = String.valueOf(Integer.parseInt(dateStr));
                            break;
                        default:
                            dateStr = dateMap.get("date");
                            break;
                    }

                if(StringUtil.isNotEmpty(dateStr)){
                    requestStr = requestStr.replace(realHolder,dateStr);
                    replacedStrs.put(realHolder,dateStr);
                }
            }
        }
        return requestStr;
    }
}
