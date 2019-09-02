package com.gigold.pay.autotest.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 用于计算模拟登录的权限信息
 */
public class HMACSHA1Util {
    /**
     * HMAC-SHA1签名
     *
     * @param message
     * @param key
     * @return
     */
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static final String CHARSET="UTF-8";
    public static final String  DIGTYPE="HmacSHA1";
    public static String getHmacSHA1(String message, String key) {
        String hmacSha1 = null;
        try {
            // url encode
            message = URLEncoder.encode(message, CHARSET);
            // hmac-sha1加密
            Mac mac = Mac.getInstance(DIGTYPE);
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(CHARSET), DIGTYPE);
            mac.init(spec);
            byte[] byteHMAC = mac.doFinal(message.getBytes());
            // base64 encode
            hmacSha1= getFormattedText(byteHMAC);
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException ignored) {
        }
        return hmacSha1;
    }



    /**
     * Takes the raw bytes from the digest and formats them correct.
     *
     * @param bytes
     *            the raw bytes from the digest.
     * @return the formatted bytes.
     */
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (byte aByte : bytes) {
            buf.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return buf.toString();
    }

    public static void main(String[] args){

//        // 运管
//        String appId = "backstage";
//        String appKey = "ebf6a7e1-e01e-4fa4-b106-79bc67247baf";
//        String operatorId = "2";
//        String areaId = "0";

//        // 1478 物管
//        String appId = "radiusManager";
//        String appKey = "2a0d8b8d-a0f5-4d03-bdc1-0582bc8cefa3";
//        String operatorId = "2";
//        String areaId = "279";

        // 1482 物管
        String appId = "radiusManager";
        String appKey = "2a0d8b8d-a0f5-4d03-bdc1-0582bc8cefa3";
        String operatorId = "2";
        String areaId = "286";


        long timestamp = new Date().getTime();
//        timestamp = timestamp - 60*24*3600;
        String action = "p_assistant"; // p_assistant 半径帮手审核 ; p_neighbor 友邻发布管理

        //半径管家后台
        StringBuilder ss = new StringBuilder();
        ss.append("appId=").append(appId);
        ss.append("&timestamp=").append(timestamp);
        ss.append("&operatorId=").append(operatorId);
        ss.append("&areaId=").append(areaId);
        ss.append("&action=").append(action);
        // 进行签名
        String signature = HMACSHA1Util.getHmacSHA1(ss.toString(), appKey);
        ss.append("&&signature=").append(signature);


        // 生成登录参数
        String queryString = "/authorization/authorization.do?"+ss.toString();

        System.out.println(queryString);
    }

}
