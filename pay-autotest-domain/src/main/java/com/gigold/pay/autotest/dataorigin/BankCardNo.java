package com.gigold.pay.autotest.dataorigin;

import com.gigold.pay.autotest.datamaker.DBconnector;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/6/21.
 */
public class BankCardNo implements DataOriginMaker {
    private static String replaceHolder="#{CONST-BANK-CARD-NO}";

    public String getData() {
        try{
            return getUnusedNo();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "default";
    }

    public String renewThisOrigin() {
        String phonenum = "";
        DBconnector dBconnector = null;
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("select * from T_UI_LAST_BANKCARD_NO limit 1");
            while (rs.next()) {
                phonenum = rs.getString("NUMBER");
            }
            String phone_head = phonenum.substring(0, 2);
            String phone_mid = phonenum.substring(2, 10);
            String phone_nill = phonenum.substring(10, 18);

            long _phone_nill = Long.parseLong(phone_nill);
            long _phone_mid = Long.parseLong(phone_mid);
            int _phone_head = Integer.parseInt(phone_head);

            _phone_nill++;// 尾端自增
            if(_phone_nill==99999999){
                _phone_nill = 0;
                _phone_mid++; // 中段进位
                if(_phone_mid==99999999){
                    _phone_head++; // 尾端进位
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
            String _head = "0000" + String.valueOf(_phone_head);
            _head = _head.substring(_head.length() - 2);

            dBconnector.updata("update T_UI_LAST_BANKCARD_NO a set a.NUMBER='" + _head+_mid+_nill + "' where a.id=1");
        } catch (Exception ignore) {

        } finally {
            if(dBconnector!=null)
                dBconnector.close();
        }
        return getUnusedNo();
    }

    @Override
    public String process(String requestStr, Map<String, String> replacedStrs) {
        if(replacedStrs.containsKey(replaceHolder)){
            // 取历史
            requestStr = requestStr.replace(replaceHolder,replacedStrs.get(replaceHolder) );
        }else{
            // 取新值
            String data = getData();
            // 替换
            requestStr = requestStr.replace(replaceHolder,data );
            // 更新
            renewThisOrigin();
            // 缓存
            replacedStrs.put(replaceHolder,data);
        }
        return requestStr;
    }

    public static String getUnusedNo(){
        DBconnector dBconnector = null;
        try {
            dBconnector=new DBconnector();
            ResultSet rs = dBconnector.query("select * from T_UI_LAST_BANKCARD_NO limit 1");
            rs.next();
            return rs.getString("NUMBER");
        } catch (Exception ignore) {

        } finally {
            if(dBconnector!=null)
            dBconnector.close();
        }

        return null;
    }

    public static List<String> getAvalidNo() throws Exception {
        List<String> list = new ArrayList<>();
        DBconnector dBconnector = new DBconnector();
        try {
            ResultSet rs = dBconnector.query("select * from T_UI_AVL_BANKCARD_NO order by id desc limit 100");
            while (rs.next()) {
                list.add(rs.getString("NUMBER"));
            }
            return list;
        } catch (Exception ignore) {

        } finally {
            dBconnector.close();
        }
        return list;
    }


    public static void addToAvalidList(String No,String useage) throws Exception {
        DBconnector dBconnector = new DBconnector();
        try {
            dBconnector.insert("insert into T_UI_AVL_BANKCARD_NO set NUMBER='" + No + "',useage='"+useage+"'");
        } finally {
            dBconnector.close();
        }

    }
}
