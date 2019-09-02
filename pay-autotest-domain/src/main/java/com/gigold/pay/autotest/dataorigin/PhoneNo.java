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
public class PhoneNo implements DataOriginMaker{

    private static final String replaceHolder = "#{CONST-FRESH-PHONE-NO}";


    public static String getUnusedPhoneNo(){
        DBconnector dBconnector = null;
        String no = "";
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("select * from T_UI_LAST_PHONE_NO limit 1");
            rs.next();
            no = rs.getString("NUMBER");
        } catch (Exception ignore) {

        } finally {
            if(dBconnector!=null)
            dBconnector.close();
        }

        return no;
    }

    public static List<String> getAvalidPhoneNo() throws Exception {
        List<String> list = new ArrayList<>();
        DBconnector dBconnector = new DBconnector();
        try {
            ResultSet rs = dBconnector.query("select * from T_UI_AVL_PHONE_NO order by id desc limit 100");
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

    public static void renewPhone(){
        String phonenum = "";
        DBconnector dBconnector = null;
        try {
            dBconnector = new DBconnector();
            ResultSet rs = dBconnector.query("select * from T_UI_LAST_PHONE_NO limit 1");
            while (rs.next()) {
                phonenum = rs.getString("NUMBER");
            }
            String phone_head = phonenum.substring(0, 3);
            String phone_nill = phonenum.substring(3, 11);
            long _phone_nill = Long.parseLong(phone_nill);
            _phone_nill++;
            // 向左补足
            String filledString = "00000000" + String.valueOf(_phone_nill);
            filledString = phone_head + filledString.substring(filledString.length() - 8);
            dBconnector.updata("update T_UI_LAST_PHONE_NO a set a.NUMBER='" + filledString + "' where a.id=1");
        } catch (Exception ignore) {

        } finally {
            if(dBconnector!=null)
            dBconnector.close();
        }
    }

    public static void addToAvalidList(String PhoneNo) throws Exception {
        DBconnector dBconnector = new DBconnector();
        try {
            dBconnector.insert("insert into T_UI_AVL_PHONE_NO set NUMBER='" + PhoneNo + "'");
        } finally {
            dBconnector.close();
        }

    }

    public static void addToAvalidList(String PhoneNo,String useage) throws Exception {
        DBconnector dBconnector = new DBconnector();
        try {
            dBconnector.insert("insert into T_UI_AVL_PHONE_NO set NUMBER='" + PhoneNo + "',useage='"+useage+"'");
        } finally {
            dBconnector.close();
        }

    }

    @Override
    public String process(String requestStr, Map<String, String> replacedStrs) {
        if(replacedStrs.containsKey(replaceHolder)){
            requestStr = requestStr.replace(replaceHolder,replacedStrs.get(replaceHolder) );
        }else {
            String unUsedNo = getUnusedPhoneNo();
            if(unUsedNo!=null)requestStr = requestStr.replace(replaceHolder, unUsedNo);
            renewPhone();
            replacedStrs.put(replaceHolder,unUsedNo);

        }
        return requestStr;
    }
}
