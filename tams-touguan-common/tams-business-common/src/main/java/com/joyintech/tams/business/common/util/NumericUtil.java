package com.joyintech.tams.business.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kchen
 */
public class NumericUtil {

    /**
     * 校验字符串是否为数字 小数 不为负值
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[+]?[0-9]+(\\.[0-9]+)?");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
