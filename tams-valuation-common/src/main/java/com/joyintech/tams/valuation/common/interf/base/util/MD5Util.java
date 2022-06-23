package com.joyintech.tams.valuation.common.interf.base.util;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MD5Util {

	/**
	 * 日志
	 */
//	private static Logger logger = Logger.getLogger(MD5Util.class);
	private static Logger logger = LoggerFactory.getLogger(MD5Util.class);

    public static String string2MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (Exception e) {
//            System.out.println(e.toString());
            logger.error("",e);
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++ ) {
            byteArray[i] = (byte)charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++ ) {
            int val = ((int)md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String convertMD5(String inStr) {
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++ ) {
            a[i] = (char)(a[i] ^ 't');
        }
        String s = new String(a);
        return s;
    }
}
