package com.joyintech.tams.exchange.common.utils;

/**
 * @author swwan
 * @date 2022/4/13 - 13:41
 */

import cn.hutool.core.util.ObjectUtil;
import com.joyintech.tams.exchange.common.enums.ICodeEnum;
import com.joyintech.tams.exchange.common.enums.IEnum;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

/**
 * 枚举工具类 {@link cn.hutool.core.util.EnumUtil}
 *
 * @author swwan
 * @date 2022/1/15 - 23:22
 */
@SuppressWarnings("all")
public class EnumUtil {

    /**
     * 获取枚举对象
     *
     * @param enumClass 枚举类
     * @param code      枚举值
     * @return 枚举对象 {@link IEnum}<{@link T}>
     */
    public static <E extends Enum<E>, T> IEnum<T> getEnumByCode(Class<E> enumClass, T code) {
        if (null == code) {
            return null;
        }
        EnumSet<E> es = EnumSet.allOf(enumClass);
        for (E anEnum : es) {
            if (anEnum instanceof IEnum) {
                if (((IEnum<T>) anEnum).codeEquals(code)) {
                    return (IEnum<T>) anEnum;
                }
            }
        }
        return null;
    }

    /**
     * 根据枚举值获取对应的枚举描述
     *
     * @param enums 枚举对象数组
     * @param code  枚举值
     * @return 枚举描述
     */
    public static <E extends Enum<E>, T> String getDescriptionByCode(Class<E> enumClass, T code) {
        IEnum<T> clazz = getEnumByCode(enumClass, code);
        if (null == clazz) {
            return null;
        }
        return clazz.getDescription();
    }

    /**
     * 判断枚举值是否在给出的枚举对象中
     *
     * @param code  枚举值
     * @param enums 枚举对象
     * @return
     */
    public static <T> boolean in(T code, ICodeEnum<T>... enums) {
        if (ObjectUtil.isNull(code) || ObjectUtil.isNull(enums)) {
            return false;
        }
        Optional<ICodeEnum<T>> any = Arrays.stream(enums).filter(item -> item.codeEquals(code)).findAny();
        return any.isPresent();
    }

    /**
     * 判断枚举值是否在给出的枚举对象中
     *
     * @param code  枚举值
     * @param enums 枚举对象
     * @return
     */
    public static <T> boolean notIn(T code, ICodeEnum<T>... enums) {
        return !in(code, enums);
    }

    public static void main(String[] args) {
        boolean in1 = EnumUtil.in("01", EtfEnum.ETF_BOND, EtfEnum.ETF_GOLD, EtfEnum.ETF_CURRENCY);
        boolean in2 = EnumUtil.in("01", EtfEnum.ETF_BOND, EtfEnum.ETF_GOLD, EtfEnum.ETF_CURRENCY, EtfEnum.ETF_SINGLE_MARKET);
        System.out.println(in1 + ":" + in2);
    }


    public enum EtfEnum implements ICodeEnum<String> {
        ETF_SINGLE_MARKET("01"),
        ETF_CROSS_MARKET("02"),
        ETF_CROSS_BORDER("03"),
        ETF_BOND("04"),
        ETF_GOLD("05"),
        ETF_CURRENCY("06");

        public final String code;

        EtfEnum(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }
}
