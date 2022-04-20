package com.joyintech.tams.business.common.util;

import com.joyintech.framework.util.ArithmeticUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jiangpeng
 * @date 2022年04月19日 16:35
 */
public class BigDecimalUtil {
    /**
     * 数值比较 a<b 返回-1,a=b返回0,a>b返回1
     *
     * @param a     空值默认为0
     * @param b     空值默认为0
     * @param scale 比较精度,默认精度为小数点后4位,四舍五入
     * @return int
     * @author jiangpeng
     * @date 2022年04月19日 16:35
     */
    public static int ACompareToB(BigDecimal a, BigDecimal b, int scale) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        if (scale == 0) {
            scale = 4;
        }
        return ArithmeticUtil.round(a, scale, RoundingMode.HALF_UP).compareTo(ArithmeticUtil.round(b, scale, RoundingMode.HALF_UP));
    }
}
