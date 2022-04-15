package com.joyintech.tams.exchange.common.enums;


import java.util.Objects;

/**
 * 最简单的枚举类，即只含code的枚举
 * 如若枚举值和实际值不同，请务必重写 {@code getCode} 方法
 * <br>实现此接口可使用{@link com.swwan.research.common.utils.base.EnumUtil}中的方法
 *
 * @author swwan
 * @date 2022/2/15 - 22:17
 */
public interface ICodeEnum<T> {

    /**
     * 获取枚举值
     *
     * @return 枚举值
     */
    T getCode();

    /**
     * 对比当前枚举对象和传入的枚举值是否一致（String类型会忽略大小写）
     * <br>当前枚举值是否匹配远端传入的值（比如：数据库的字段值、RPC传入的参数值）
     *
     * @param enumCode 枚举值
     * @return 是否匹配
     */
    default boolean codeEquals(T enumCode) {
        if (enumCode == null) {
            return false;
        }
        if (enumCode instanceof String) {
            return ((String) enumCode).equalsIgnoreCase((String) getCode());
        }
        return Objects.equals(this.getCode(), enumCode);
    }


    /**
     * 对比当前枚举对象和传入的枚举值是否一致（String类型会忽略大小写）
     * <br>当前枚举值是否匹配远端传入的值（比如：数据库的字段值、RPC传入的参数值）
     *
     * @param enumCode 枚举值
     * @return 是否匹配
     */
    default boolean codeNotEquals(T enumCode) {
        return !codeEquals(enumCode);
    }

    /**
     * 对比两个枚举对象是否完全相同（==）
     *
     * @param anotherEnum 枚举对象
     * @return 是否相同
     */
    @SuppressWarnings("all")
    default boolean equals(ICodeEnum<T> anotherEnum) {
        return this == anotherEnum;
    }
}
