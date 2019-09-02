package com.gigold.pay.autotest.util;


import net.sf.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chenkuan
 * on 16/7/4.
 */
public class MD5Uitl {
    /**
     * MD5加密方法
     * @param originstr
     * @return
     */
    public static String ecodeByMD5(String originstr,String key) {
        originstr = getPureStr(originstr);
        originstr += key;

        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    originstr.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (Exception e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 获取整理的json
     * @param postData
     * @return
     */
    public static String getPureStr(String postData){
        try{
//            JSONObject jsonObject = JSONObject.fromObject(postData);
//            return jsonObject.toString().trim();
            return postData;
        }catch (Exception e){
            e.printStackTrace();
            return postData;
        }
    }
}
