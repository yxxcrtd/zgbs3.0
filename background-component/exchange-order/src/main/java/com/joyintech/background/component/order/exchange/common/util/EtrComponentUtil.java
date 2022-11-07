/*
 * 文件名：EtrComponentUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.background.component.order.exchange.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.background.component.common.pack.common.base.intf.IDictCodeList;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.common.pack.common.util.ArithmeticUtil;
import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.background.component.common.pack.common.util.SpringUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;

/**
 * 投资指令组件Util
 * 
 * @author zhangrui1
 * @version 1.0 2020年7月9日
 * @see EtrComponentUtil
 * @since 1.0
 */
public class EtrComponentUtil {

    private EtrComponentUtil() {
        // private the constructor
    }

    /**
     * 设置数据操作信息
     * 
     * @param entity 数据
     * @param user 用户信息
     */
    public static final <V extends BaseEntity> void setBaseInfo(V entity, User user) {
        Date dt = DateUtil.now();
        // 创建部门
        if (entity.getCreateDept() == null || entity.getCreateDept().isEmpty()) {
            entity.setCreateDept(user.getDeptCode());
        }
        // 更新人
        if (entity.getUpdateUser() == null || entity.getUpdateUser().isEmpty()) {
            entity.setUpdateUser(user.getUserName());
        }
        // 创建人
        if (entity.getCreateUser() == null || entity.getCreateUser().isEmpty()) {
            entity.setCreateUser(entity.getUpdateUser());
        }
        // 更新时间
        entity.setUpdateTime(dt);
        // 创建时间
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(entity.getUpdateTime());
        }
    }

    /**
     * 金额类型是否为空
     * 
     * @param amt 金额
     * @param isZeroValid 0是否有效，true:有效，否则，无效。0为无效时，判定为空
     * @return true:金额为空
     */
    public static final boolean isAmtEmpty(BigDecimal amt, boolean isZeroValid) {
        return amt == null || (!isZeroValid && BigDecimal.ZERO.compareTo(amt) == 0);
    }

    /**
     * 取得Message
     *
     * @param messageCode messageCode
     * @param args 参数
     * @return message
     */
    public static final String getMessage(String messageCode, String... args) {
        return SpringUtil.getTextValue(messageCode, args);
    }

    /**
     * 判断num2是否能整除num1
     * 
     * @param num1 被除数
     * @param num2 除数
     * @return true:结果为整数
     */
    public static final boolean isNumberExactDivission(BigDecimal num1, BigDecimal num2) {
        if (isAmtEmpty(num1, true) || isAmtEmpty(num2, false)) {
            return false;
        }
        try {
            ArithmeticUtil.divide(num1, num2, 0, RoundingMode.UNNECESSARY);
            return true;
        }
        catch (ArithmeticException e) {
            return false;
        }
    }

    /**
     * 比较大小，null当最小值<br>
     * num1 > num2，则返回 1<br>
     * num1 = num2，则返回 0<br>
     * num1 < num2，则返回-1<br>
     * 
     * @param num1 参数1
     * @param num2 参数2
     */
    public static final int compareNumber(BigDecimal num1, BigDecimal num2) {
        if (num1 == null) {
            return num2 == null ? 0 : -1;
        }
        if (num2 == null) {
            return 1;
        }
        return num1.compareTo(num2);
    }

    /**
     * 全闭区间
     */
    public static final int RANGE_MODE_CL_CL = 0;

    /**
     * 半闭半开区间
     */
    public static final int RANGE_MODE_CL_OP = 1;

    /**
     * 半开半闭区间
     */
    public static final int RANGE_MODE_OP_CL = 2;

    /**
     * 全开区间
     */
    public static final int RANGE_MODE_OP_OP = 3;

    /**
     * 目标金额是否在给定区间内
     * 
     * @param num 目标金额
     * @param limitDn 区间下限
     * @param limitUp 区间上限
     * @param rangeMode 区间闭合类型<br>
     *            0:全闭区间、1:半闭半开区间、2:半开半闭区间、3:全开区间
     * @return true:目标金额在给定区间内
     */
    public static final boolean isNumberInRange(BigDecimal num, BigDecimal limitDn,
                                                BigDecimal limitUp, int rangeMode) {
        // 与最大值比较
        int cmp1 = limitUp == null ? -1 : compareNumber(num, limitUp);
        // 与最小值比较
        int cmp2 = limitDn == null ? 1 : compareNumber(num, limitDn);
        switch (rangeMode) {
            // 全闭区间
            case RANGE_MODE_CL_CL:
                return cmp1 <= 0 && cmp2 >= 0;
            // 半闭半开区间
            case RANGE_MODE_CL_OP:
                return cmp1 < 0 && cmp2 >= 0;
            // 半开半闭区间
            case RANGE_MODE_OP_CL:
                return cmp1 <= 0 && cmp2 > 0;
            // 全开区间
            case RANGE_MODE_OP_OP:
            default:
                return cmp1 < 0 && cmp2 > 0;
        }
    }

    /**
     * 目标金额是否在给定区间内<br>
     * 全开区间
     * 
     * @param num 目标金额
     * @param limitDn 区间下限
     * @param limitUp 区间上限
     * @return true:目标金额在给定区间内
     */
    public static final boolean isNumberInRange(BigDecimal num, BigDecimal limitDn,
                                                BigDecimal limitUp) {
        return isNumberInRange(num, limitDn, limitUp, RANGE_MODE_OP_OP);
    }

    /**
     * 目标金额是否在给定区间内<br>
     * 全开区间
     * 
     * @param num 目标金额
     * @param arr 区间
     * @param rangeMode 区间闭合类型
     * @return true:目标金额在给定区间内
     */
    public static final boolean isNumberInRange(BigDecimal num, BigDecimal[] arr, int rangeMode) {
        if (arr == null || arr.length == 0) {
            return true;
        }
        if (arr.length == 1) {
            return isNumberInRange(num, arr[0], null, rangeMode);
        }
        return isNumberInRange(num, arr[0], arr[1], rangeMode);
    }

    public static <K, V> Map<K, V> putIfAbsent(Map<K, V> map, K key, V defaultV,
                                               Consumer<V> consumer) {
        if (map == null) {
            map = new HashMap<>();
        }
        V v = map.getOrDefault(key, defaultV);
        if (consumer != null) {
            consumer.accept(v);
        }
        map.put(key, v);
        return map;
    }

    /**
     * 判断是否为指定数据字典
     * 
     * @param type 目标字符串
     * @param dict 数据字典
     * @return true:是
     */
    public static final boolean isType(String type, IDictCodeList dict) {
        return StringUtil.equals(type, dict.getValue());
    }
}
