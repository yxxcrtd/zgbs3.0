package com.joyintech.tams.valuation.common.utils;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.common.NumberUtil;
import com.greenpineyu.fel.context.MapContext;
import com.greenpineyu.fel.function.CommonFunction;

import java.util.Map;

//import com.joyintech.dsp.core.constant.DateConstant;

/**
 * java表达式工具(强类型)
 *
 * @author Zim
 * @time 2016年6月21日 下午1:13:36
 */
public class FelAnalysisUtil {

    private final static FelEngine FEL = new FelEngineImpl();

    /**
     * 表达式计算
     *
     * @param expression
     * @param env        无参数赋值null
     * @return
     * @
     */
    public static Object eval(String expression, Map<String, Object> env) {
        try {
            if (null == env) return FEL.eval(expression);
            return FEL.eval(expression, new MapContext(env));
        } catch (Exception e) {
            throw new RuntimeException(expression + "表达式执行异常", e);
        }
    }

    /**
     * 调用静态函数或实例化对象的函数
     *
     * @param expression
     * @param env        无参数赋值null
     * @return
     * @
     */
    public static Object evalMethod(String expression, Map<String, Object> env) {
        if (null == env) return FelEngine.instance.eval(expression);
        return FelEngine.instance.eval(expression, new MapContext(env));
    }

    public static int intValue(String expression, Map<String, Object> env) {
        return NumberUtil.toInteger(eval(expression, env));
    }

    public static long longValue(String expression, Map<String, Object> env) {
        return NumberUtil.toLong(eval(expression, env));
    }

    public static double doubleValue(String expression, Map<String, Object> env) {
        return NumberUtil.toDouble(eval(expression, env));
    }

    public static boolean booleanValue(String expression, Map<String, Object> env) {
        return (Boolean) eval(expression, env);
    }

    public static String stringValue(String expression, Map<String, Object> env) {
        return (String) eval(expression, env);
    }

    public static Object[] objectValue(String expression, Map<String, Object> env) {
        return (Object[]) eval(expression, env);
    }

    public static void registerFunction(CommonFunction function) {
        FEL.addFun(function);
    }
}
