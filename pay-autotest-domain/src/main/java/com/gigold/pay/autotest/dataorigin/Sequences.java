package com.gigold.pay.autotest.dataorigin;


import com.gigold.pay.autotest.datamaker.DBconnector;
import com.gigold.pay.framework.util.common.StringUtil;

import java.sql.ResultSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenkuan
 * on 16/1/29.
 */
public class Sequences implements DataOriginMaker{
    private static final String replaceHolder = "(#\\{CONST-SEQUENCE(-\\d+)?})";

    public static String getNo() throws Exception {
        return getNoByLength(0);
    }

    public static String getNoByLength(int length){
        DBconnector dBconnector = null;
        String firstStr="";
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("select * from T_UI_LAST_SEQUENCE WHERE LENGTH="+String.valueOf(length)+" limit 1");
            if(rs.next()){
                firstStr = rs.getString("NO");
            }else {
                firstStr = ("00000000"+"00000000"+"00000000"+"1").substring(25-length);
                dBconnector.updata("replace into T_UI_LAST_SEQUENCE set NO='"+firstStr+"',LENGTH="+length);
            }

        } catch (Exception ignore) {
            ignore.printStackTrace();
        } finally {
            if(dBconnector!=null)
            dBconnector.close();
        }
        return firstStr;
    }

    public static String renewNo() throws Exception {
        return renewNo(0);
    }

    public static String renewNo(int length){
        String no = "";
        DBconnector dBconnector = null;
        long _phone_nill,_phone_mid,_phone_head ;
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("select NO,LENGTH from T_UI_LAST_SEQUENCE WHERE LENGTH="+String.valueOf(length)+" limit 1");
            if (rs.next()) {
                no = rs.getString("NO");
                System.out.println(no);
                no = "00000000"+"00000000"+"00000000"+no;
                no = no.substring(no.length()-24);
                String phone_head = no.substring(0, 8);
                String phone_mid = no.substring(8, 16);
                String phone_nill = no.substring(16, 24);
                _phone_nill = Long.parseLong(phone_nill);
                _phone_mid = Long.parseLong(phone_mid);
                _phone_head = Long.parseLong(phone_head);


                _phone_nill++;// 尾端自增
                if(_phone_nill==100000000){
                    _phone_nill = 0;
                    _phone_mid++; // 中段进位
                    if(_phone_mid==100000000){
                        _phone_mid=0;
                        _phone_head++; // 首端进位
                        if(_phone_head==100000000){
                            _phone_head=0; // 首端进位
                        }
                    }
                }


                // 向左补足
                // 尾端
                String _nill = "00000000" + String.valueOf(_phone_nill);
                _nill = _nill.substring(_nill.length() - 8);
                // 中段
                String _mid = "00000000" + String.valueOf(_phone_mid);
                _mid = _mid.substring(_mid.length() - 8);
                // 首段
                String _head = "00000000" + String.valueOf(_phone_head);
                _head = _head.substring(_head.length() - 8);


                if(length<=0){
                    // 去掉左0
                    if("00000000".equals(_head)){

                        if("00000000".equals(_mid)){
                            no = String.valueOf(Long.parseLong(_nill));
                        }else{
                            no = String.valueOf(Long.parseLong(_mid))+_nill;
                        }
                    }else {
                        if("00000000".equals(_mid)){
                            no = String.valueOf(Long.parseLong(_head))+"00000000"+_nill;
                        }else{
                            no =_head+_mid+_nill;
                        }

                    }
                }else{
                    // 补足左0
                    no = _head+_mid+_nill;
                    no = no.substring(no.length()-length);
                }

                dBconnector.updata("update T_UI_LAST_SEQUENCE a set a.NO='" + no + "' where a.LENGTH=" + length);

            }else{
                dBconnector.updata("replace into T_UI_LAST_SEQUENCE set NO='1',LENGTH="+length);
            }


        } catch (Exception ignore) {
            ignore.printStackTrace();
        } finally {
            if(dBconnector!=null)
            dBconnector.close();
        }
        return getNoByLength(length);
    }


    @Override
    public String process(String requestStr, Map<String, String> replacedStrs) {
        // 替换任意位唯一索引 - 定长
        Pattern pattern = Pattern.compile(replaceHolder);
        Matcher matcher = pattern.matcher(requestStr);
        while (matcher.find()){ // 存在则替换
            String realHolder = matcher.group(1);
            String length = matcher.group(2);

            if(StringUtil.isEmpty(length)){
                // 未有明确长度
                length = "0";
            }else{
                // 有明确长度
                length = length.substring(1);
            }

            int length_int=0;
            if(StringUtil.isNotEmpty(length)) length_int = Integer.parseInt(length);

            if(replacedStrs.containsKey(realHolder)){// 若整个会话中已经存过当前值则直接用
                requestStr = requestStr.replace(realHolder,replacedStrs.get(realHolder) );
            }else{// 否则新取一个,然后刷新列表
                String no = getNoByLength(length_int);
                if(StringUtil.isNotEmpty(no)){
                    requestStr = requestStr.replace(realHolder,no);
                    renewNo(length_int);
                    replacedStrs.put(realHolder,no);
                }
            }
        }
        return requestStr;
    }
}
