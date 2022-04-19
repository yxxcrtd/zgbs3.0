package com.joyintech.tams.exchange.common.enums;

/**
 * 如若枚举描述和实际值不同，请务必重写 {@code getDescription} 方法
 * <p/>
 * 枚举定义规范：
 * <br>1.枚举名切记大写
 * <br>2.描述尽量清晰
 * <br>3.不要出现错误拼写
 *
 * @param <T> 枚举值的泛型
 * @author swwan
 * @date 2022/2/15 - 22:17
 */
public interface IEnum<T> extends ICodeEnum<T> {

    /**
     * 获取枚举描述
     *
     * @return 枚举描述
     */
    String getDescription();
}