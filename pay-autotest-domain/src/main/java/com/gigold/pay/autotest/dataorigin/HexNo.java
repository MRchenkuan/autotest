package com.gigold.pay.autotest.dataorigin;

import com.gigold.pay.autotest.datamaker.DBconnector;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/1/29.
 */
public class HexNo implements DataOriginMaker {

    private static final String replaceHolder = "#{CONST-HEX-6}";

    public static List<String> getUnUsedNo() {
        List<String> list = new ArrayList<>();
        DBconnector dBconnector = null;
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("select * from T_UI_AVL_OPER_ACCT where stat='Y' limit 100");
            while (rs.next()) {
                list.add(rs.getString("operacct"));
            }
            return list;
        } catch (Exception ignore) {

        } finally {
            if(dBconnector!=null)
            dBconnector.close();
        }
        return list;
    }

    public static String getLastHexNo(){
        String hexNo = "";
        DBconnector dBconnector=null;
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("select * from T_UI_LAST_HEX_NO limit 1");
            while (rs.next()) {
                hexNo = rs.getString("no");
            }
            return hexNo;
        } catch (Exception ignore) {

        } finally {
            if(null!=dBconnector){
                dBconnector.close();
            }
        }

        return hexNo;
    }


    public static void renewNo(){
        String hexNo = "";
        DBconnector dBconnector = null;
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("select * from T_UI_LAST_HEX_NO limit 1");
            while (rs.next()) {
                hexNo = rs.getString("no");
            }
            dBconnector.updata("UPDATE T_UI_LAST_HEX_NO SET no ='"+StrIncrease(hexNo)+"'");
            dBconnector.updata("insert into T_UI_AVL_OPER_ACCT(operacct) values('"+hexNo+"')");
        } catch (Exception ignore) {

        } finally {
            if(null != dBconnector)
            dBconnector.close();
        }
    }



    /**
     * 设置号码用途
     * @param no 号码
     * @param useage 用途
     * @throws Exception
     */
    public static void disableNo(String no , String useage) throws Exception {
        DBconnector dBconnector = new DBconnector();
        try {
            dBconnector.insert("UPDATE T_UI_AVL_OPER_ACCT SET stat='N', useage='"+useage+"' WHERE operacct='" + no + "'");
        } finally {
            dBconnector.close();
        }

    }

    public static void disableNo(String no) throws Exception {
        disableNo(no,"unset");
    }

    // 字符串增长工具
    public static String StrIncrease(String str) {
        str = str.toUpperCase();
        char[] chars = str.toCharArray();
        chars[chars.length-1]++;
        for(int i=chars.length-1;i>=0; i--){
            byte nowchar = (byte) chars[i];
            // 跳过符号段
            if(nowchar>57&&nowchar<65)chars[i]='A';
            // 进位
            if(nowchar>90){
                chars[i]='0';// 当前位置零
                chars[i-1]++; // 进位加1
            }
        }
        str = String.valueOf(chars);
        return str;
    }


    @Override
    public String process(String requestStr, Map<String, String> replacedStrs) {

        if(replacedStrs.containsKey(replaceHolder)){
            // 取历史
            requestStr = requestStr.replace(replaceHolder,replacedStrs.get(replaceHolder) );
        }else{
            // 取新值
            String data = getLastHexNo();
            // 替换
            requestStr = requestStr.replace(replaceHolder,data );
            // 更新
            renewNo();
            // 缓存
            replacedStrs.put(replaceHolder,data);
        }
        return requestStr;
    }
}
