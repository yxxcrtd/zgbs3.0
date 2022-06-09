/*
 * 文件名：CheckLibUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2019年3月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common;

import com.joyintech.background.component.common.check.dto.CheckElement;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 基本检查方法苦，实现了多个数据检查方法<br>
 * ChbCommonCheckUtil中通过反射调用此校验配置类，确保此类能够被ChbCommonCheckUtil访问到即可。
 * @author chenxc
 * @version 1.7 2019年4月29日
 * @see ChbBaseCheckLib
 * @since 1.0
 */
public class ChbBaseCheckLib {
    //	  public static Map<String,List<ChbTransformRuleObjDto>> objFiledsMap;
//	  public static Map<String,TransformRule001Dto> spzMap;
    public static final String PATTERN_ALL = "^[\\u4e00-\\u9fef-\\u3400-\\u4db5-\\u20000-\\u2a6d6-\\u2a700-\\u2b734-\\u2b740-\\u2b81d-\\u2b820-\\u2cea1-\\u2ceb0-\\u2ebe0-\\uf900-\\ufad9\\ue815-\\ue86f\\u00b7\\u0020-\\u0026-\\u0028-\\u007B-\\u007D-\\u007E-\\u2000-\\u2040-\\u2160-\\u216B-\\u3000-\\u301F-\\uFF00-\\uFF65-\\uFFE0-\\uFFEF]{0,}$";
    public static final String PATTERN_COMMON="^[\\u0020-\\u0026-\\u0028-\\u002F-\\u003A-\\u003F\\u0040\\u005B-\\u005F\\u0060\\u007B\\u007D\\u007E\\u0030-\\u0039-\\u0041-\\u005A-\\u0061-\\u007A]{0,}$";
    public static final String PATTERN_NOCHINESE="^[\\u0020-\\u0026-\\u0028-\\u007B-\\u007D-\\u007E-\\u2000-\\u2040-\\u2160-\\u216B-\\u3000-\\u301F-\\uFF00-\\uFF65-\\uFFE0-\\uFFEF]{0,}$";
    //public static final String PATTERN_NOSYMBOL="(((\\d+%)|(\\d+%-\\d+%)):[\\u4e00-\\u9fef-\\u3400-\\u4db5-\\u20000-\\u2a6d6-\\u2a700-\\u2b734-\\u2b740-\\u2b81d-\\u2b820-\\u2cea1-\\u2ceb0-\\u2ebe0-\\uf900-\\ufad9\\ue815-\\ue86f\\u00b7\\u0020-\\u0026-\\u0028-\\u007B-\\u007D-\\u007E-\\u2000-\\u2040-\\u2160-\\u216B-\\u3000-\\u301F-\\uFF00-\\uFF65-\\uFFE0-\\uFFEF&&[^\\u0025\\u003A-\\u003B\\u002D]]+;)*(((\\d+%)|(\\d+%-\\d+%)):[\\u4e00-\\u9fef-\\u3400-\\u4db5-\\u20000-\\u2a6d6-\\u2a700-\\u2b734-\\u2b740-\\u2b81d-\\u2b820-\\u2cea1-\\u2ceb0-\\u2ebe0-\\uf900-\\ufad9\\ue815-\\ue86f\\u00b7\\u0020-\\u0026-\\u0028-\\u007B-\\u007D-\\u007E-\\u2000-\\u2040-\\u2160-\\u216B-\\u3000-\\u301F-\\uFF00-\\uFF65-\\uFFE0-\\uFFEF&&[^\\u0025\\u003A-\\u003B\\u002D]]+)";
    public static final String PATTERN_NOSYMBOL="(((\\d+%)|(\\d+%-\\d+%)):[\\u4e00-\\u9fef-\\u3400-\\u4db5-\\u20000-\\u2a6d6-\\u2a700-\\u2b734-\\u2b740-\\u2b81d-\\u2b820-\\u2cea1-\\u2ceb0-\\u2ebe0-\\uf900-\\ufad9\\ue815-\\ue86f\\u00b7\\u0020-\\u0026-\\u0028-\\u007B-\\u007D-\\u007E-\\u2000-\\u2040-\\u2160-\\u216B-\\u3000-\\u301F-\\uFF00-\\uFF65-\\uFFE0-\\uFFEF&&[^\\u0025\\u003A-\\u003B\\u002D]]+;)";

    /**
     * 根据指定的规则，判断数据是否为空
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean isNotEmpty(Map<String, String> data, CheckElement rule) {
        String srcValue = data.get(rule.getSrcproperty());
        return (srcValue != null && srcValue.trim().length() > 0);
    }

    /**
     * 根据指定的规则，判断数据是长度是否超过指定的长度
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean lessThanLength(Map<String, String> data, CheckElement rule) {
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        int length = Integer.parseInt(rule.getObjvalue());
        return (srcValue.length() < length);
    }

    /**
     * 根据指定的规则，判断数据字符长度是否超过指定的长度 ，默认GBK编码
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean lessThanCharLength(Map<String, String> data, CheckElement rule){
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        //add by wm condition表中无nullable字段，并且当字段比较为0时，返回true start
        if(StringUtil.isEmpty(srcValue)  && StringUtil.isEmpty(rule.getNullable()) && StringUtil.equals("0", rule.getObjvalue()) )
            return true;
        //add by wm condition表中无nullable字段，并且当字段比较为0时，返回true end
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        int length = Integer.parseInt(rule.getObjvalue());
        try {
            byte[] b_gbk = srcValue.getBytes("GBK");
            return (b_gbk.length < length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据指定的规则，判断数据字符长度是否超过指定的长度 ，默认UTF-8编码
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean lessThanCharLengthUTF8(Map<String, String> data, CheckElement rule){
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        //add by wm condition表中无nullable字段，并且当字段比较为0时，返回true start
        if(StringUtil.isEmpty(srcValue)  && StringUtil.isEmpty(rule.getNullable()) && StringUtil.equals("0", rule.getObjvalue()) )
            return true;
        //add by wm condition表中无nullable字段，并且当字段比较为0时，返回true end
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        int length = Integer.parseInt(rule.getObjvalue());
        try {
            byte[] b_gbk = srcValue.getBytes("utf-8");
            return (b_gbk.length < length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据指定的规则，判断数据指定的值与目标值大小
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean compareValue(Map<String, String> data, CheckElement rule) {
        boolean r = false;
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;

        String objValue = rule.getObjvalue();
        String compareTo = rule.getCompareto();
        // add by zhangliang 20201023 判断符号如果是!=的场合 start
        // 此处判断是为了处理condition里比较值是空的场合，对于！=也是成立的
        if (StringUtil.equals(compareTo, "!=") && StringUtil.isEmpty(srcValue)){
            return true;
        }
        // add by zhangliang 20201023 判断符号如果是!=的场合 end

        if ( StringUtil.isEmpty(srcValue) )
            return false;
        switch (compareTo) {
            case ">":
                r = (srcValue.compareTo(objValue) > 0);
                break;
            case "<":
                r = (srcValue.compareTo(objValue) < 0);
                break;
            case ">=":
                r = (srcValue.compareTo(objValue) >= 0);
                break;
            case "<=":
                r = (srcValue.compareTo(objValue) <= 0);
                break;
            case "==":
                r = (srcValue.equals(objValue));
                break;
            case "!=":
                r = (!srcValue.equals(objValue));
                break;
            default:
        }
        return r;

    }

    /**
     * 根据指定的规则，判断数据指定的值与目标属性大小
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean compareProperty(Map<String, String> data, CheckElement rule) {
        boolean r = false;
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;

        String compareTo = rule.getCompareto();
        // add by zhangliang 20201023 判断符号如果是!=的场合 start
        // 此处判断是为了处理condition里比较值是空的场合，对于！=也是成立的
        if (StringUtil.equals(compareTo, "!=") && StringUtil.isEmpty(srcValue)){
            return true;
        }
        // add by zhangliang 20201023 判断符号如果是!=的场合 end
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        String objValue = data.get(rule.getObjvalue());
        if ( objValue==null ) objValue="";

        switch (compareTo) {
            case ">":
                r = (srcValue.compareTo(objValue) > 0);
                break;
            case "<":
                r = (srcValue.compareTo(objValue) < 0);
                break;
            case ">=":
                r = (srcValue.compareTo(objValue) >= 0);
                break;
            case "<=":
                r = (srcValue.compareTo(objValue) <= 0);
                break;
            case "==":
                r = (srcValue.equals(objValue));
                break;
            case "!=":
                r = (!srcValue.equals(objValue));
                break;
            default:
        }
        return r;

    }

    /**
     * 根据指定的规则，判断数据指定的值与目标值大小
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean compareValueAsNumber(Map<String, String> data, CheckElement rule) {
        boolean r = false;
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;

        String compareTo = rule.getCompareto();
        // add by zhangliang 20201023 判断符号如果是!=的场合 start
        // 此处判断是为了处理condition里比较值是空的场合，对于！=也是成立的
        if (StringUtil.equals(compareTo, "!=") && StringUtil.isEmpty(srcValue)){
            return true;
        }
        // add by zhangliang 20201023 判断符号如果是!=的场合 end

        if ( StringUtil.isEmpty(srcValue) )
            return false;
        String objValue = rule.getObjvalue();

        if (  !isNumeric(srcValue)  || !isNumeric(objValue) ) {
            return false;
        }

        BigDecimal s = new BigDecimal(srcValue);
        BigDecimal o = new BigDecimal(objValue);

        switch (compareTo) {
            case ">":
                r = (s.compareTo(o) > 0);
                break;
            case "<":
                r = (s.compareTo(o) < 0);
                break;
            case ">=":
                r = (s.compareTo(o) >= 0);
                break;
            case "<=":
                r = (s.compareTo(o) <= 0);
                break;
            case "==":
                r = (s.compareTo(o)==0);
                break;
            case "!=":
                r = (s.compareTo(o)!=0);
                break;
            default:
        }
        return r;

    }

    /**
     * 根据指定的规则，判断数据指定的值与目标属性大小
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean comparePropertyAsNumber(Map<String, String> data, CheckElement rule) {
        boolean r = false;
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;

        String compareTo = rule.getCompareto();
        // add by zhangliang 20201023 判断符号如果是!=的场合 start
        // 此处判断是为了处理condition里比较值是空的场合，对于！=也是成立的
        if (StringUtil.equals(compareTo, "!=") && StringUtil.isEmpty(srcValue)){
            return true;
        }
        // add by zhangliang 20201023 判断符号如果是!=的场合 end

        if ( StringUtil.isEmpty(srcValue) )
            return false;
        String objValue = data.get(rule.getObjvalue());
        if ( objValue==null ) objValue="";

        if (  !isNumeric(srcValue)  || !isNumeric(objValue) ) {
            return false;
        }

        BigDecimal s = new BigDecimal(srcValue);
        BigDecimal o = new BigDecimal(objValue);

        switch (compareTo) {
            case ">":
                r = (s.compareTo(o)  > 0);
                break;
            case "<":
                r = (s.compareTo(o)  < 0);
                break;
            case ">=":
                r = (s.compareTo(o)  >= 0);
                break;
            case "<=":
                r = (s.compareTo(o)  <= 0);
                break;
            case "==":
                r = (s.compareTo(o) ==0 );
                break;
            case "!=":
                r = (s.compareTo(o)!=0);
                break;
            default:
        }
        return r;

    }

    /**
     * 根据指定的规则，判断指定的值是否在数据字典枚举类型范围内
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean inDatadict(Map<String, String> data, CheckElement rule) throws IOException {
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        // add by zhangmingming 数据字典增加多选情况 20191224 start
        if(StringUtil.equals(rule.getCompareto(),"<")){
            List<String> srcValueList = Arrays.asList(srcValue.split(","));
            // 可以多选 但是不能重复
            if(srcValueList.size() != new HashSet<String>(srcValueList).size()){
                return false;
            }
            int flag = 0;
            for (String value : srcValueList) {
                String objValue = rule.getObjvalue();
                DataDict dto = CacheUtil.getDictCode(objValue);
                if (null != dto) {
                    List<? extends DataDictItem> items = dto.getItems();
                    for (DataDictItem item : items) {
                        if (value.equals(item.getItemCode()))  flag++;
                    }
                }
            }
            if(flag == srcValueList.size())  return true;

            return false;
            // add by zhangmingming 数据字典增加多选情况 20191224 end
        }else {
            String objValue = rule.getObjvalue();
            DataDict dto = CacheUtil.getDictCode(objValue);
            if (null != dto) {
                List<? extends DataDictItem> items = dto.getItems();

                for (DataDictItem item : items) {
                    if (srcValue.equals(item.getItemCode()))
                        return true;
                }
            }
            return false;
        }

    }

    /**
     * 根据指定的规则，判断指定的值是否在数据字典枚举类型范围内
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean inDatadictMulti(Map<String, String> data, CheckElement rule) {
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtils.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtils.isEmpty(srcValue) )
            return false;
        if(StringUtils.equals(rule.getCompareto(),"<")){
            List<String> srcValueList = Arrays.asList(srcValue.split(","));
            // 可以多选 但是不能重复
            if(srcValueList.size() != new HashSet<String>(srcValueList).size()){
                return false;
            }

            int flag = 0;
            Set<String> objValueSet = new HashSet<>(Arrays.asList(rule.getObjvalue().split(",")));
            for (String objValue : objValueSet){
                DataDict dto = CacheUtil.getDictCode(objValue);
                if (null != dto) {
                    List<? extends DataDictItem> items = dto.getItems();
                    for (DataDictItem item : items) {
                        for (String value : srcValueList) {
                            if (value.equals(item.getItemCode()))  flag++;
                        }

                        if(flag == srcValueList.size())  return true;
                    }

                    if(flag == srcValueList.size())  return true;
                }
            }

            if(flag == srcValueList.size())  return true;

            return false;
        }else {
            List<String> objValueList = Arrays.asList(rule.getObjvalue().split(","));

            for (String objValue : objValueList){
                DataDict dto = CacheUtil.getDictCode(objValue);
                if (null != dto) {
                    List<? extends DataDictItem> items = dto.getItems();
                    for (DataDictItem item : items) {
                        if (srcValue.equals(item.getItemCode()))
                            return true;
                    }
                }
            }

            return false;
        }

    }

    /**
     * 根据指定的规则，判断日期数据是否是指定的格式
     * @param data 被检查的数据
     * @param rule 检查规则实体，年:小写，月:大写，日:小写，时:大写,分:小写，秒:小写
     * @return
     * @see
     * @since 1.0
     */
    public static boolean isValidDate(Map<String, String> data, CheckElement rule) {
        boolean ret = false;
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        String objValue = rule.getObjvalue();

        SimpleDateFormat sdf = new SimpleDateFormat(objValue);
        try {
            sdf.setLenient(false);
            sdf.parse(srcValue);
            ret = true;
        } catch (ParseException e) {
            //e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    /**
     * 根据指定的规则，判断数据指定的属性的值是否是数字
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean isNumber(Map<String, String> data, CheckElement rule) {

        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;

        String objValue = rule.getObjvalue();
        if ( !StringUtil.isEmpty( objValue ) ) { //如果对格式要求不为空
            //格式如：10,5
            String [] objFormat = objValue.split(",");
            int allbit = Integer.parseInt(objFormat[0]);
            int decbit = Integer.parseInt(objFormat[1]);
            //"^[-+]?[1-9]{5}[.][0-9]{2}$";
            String format = "";
            if ( decbit>0)
                format = "^([-+]?[0-9]{1,"+(allbit-decbit)+"})?[.][0-9]{"+decbit+"}$";
            else
                format = "^([-+]?[0-9]{1,"+(allbit-decbit)+"})?[.]?[0-9]{"+decbit+"}$";

            return srcValue.matches(format);
        } else {
            return isNumeric(srcValue);
        }
    }

    /**
     * 根据指定的规则，判断数据指定的属性的值是否是否是特殊格式的字符串，格式使用正则表达式规则
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean isCustomizedString(Map<String, String> data, CheckElement rule) {

        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;

        String format = rule.getObjvalue();
        try {
            return srcValue.matches(format);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 根据指定的规则，判断数据指定的属性的值是否是否是特殊格式的字符串，格式使用正则表达式规则
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean isCustomizedStringForWhitelist(Map<String, String> data, CheckElement rule) {

        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue))
            return false;

        String format = rule.getObjvalue();
        boolean ff;
        try {
            ff= srcValue.matches(format);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
//        //白名单验证不通过后继续检查有没有存在生僻字库中
//        if(!ff){
//            if(objFiledsMap==null){
//                TransformRuleMapper transformRuleMapper =
//                    SpringContextUtil.getBean(TransformRuleMapper.class);
//                TransformRuleObjMapper transformRuleObjMapper =
//                    SpringContextUtil.getBean(TransformRuleObjMapper.class);
//                Map<String, Object> paramap=new HashMap<String, Object>();
//                paramap.put("statusList", Arrays.asList("E"));
//                List<TransformRule001Dto> allList=transformRuleMapper.selectAllData(paramap);
//                spzMap=allList.stream().collect(
//                    Collectors.toMap(x ->x.getTransType()+x.getOrgField(),part->part,(k1,k2)->k1));
//                List<ChbTransformRuleObjDto> allRuleObjList=transformRuleObjMapper.selectAllDataByTableId("CHB_EQUDET");
//                objFiledsMap=allRuleObjList.stream().collect(
//                    JoyinCollectors.groupingBy(
//                        x -> x.getInfoProperty() ));
//            }
//            List<ChbTransformRuleObjDto> objFileds=null;
//            if(objFiledsMap!=null){
//                 objFileds=objFiledsMap.get(rule.getSrcproperty());
//            }
//            //对于白名单检查不过的且在规则对象中配置了的数据继续检查
//            if(!StringUtil.isEmptyList(objFileds)){
//                String result=srcValue;//对原始值分解成单个
//                if(objFileds.get(0).getObjTableFiled().equals("CUST_NAME")){
//                    result= ChbSensitiveInfoUtil.handle汉字变形(result);
//                }
//                for(ChbTransformRuleObjDto objfiled:objFileds){
//                    //对于一个字段有2个类型的以前一次的转换后结果 重新转换
//                    char[] chars=  result.toCharArray();
//                    result="";
//                    for(char cc:chars){
//                        String key=objfiled.getTransType()+cc;
//                        //仅对单个字符不能通过白名单校验的数据继续检测
//                        if(findPatern(format, ""+cc)){
//                            if(spzMap.containsKey(key)){
//                                ff = true;
//                                result+=spzMap.get(key).getTargetField();;
//                            }else{
//                                TransformRuleMapper transformRuleMapper =
//                                    SpringContextUtil.getBean(TransformRuleMapper.class);
//                                TransformRule001Dto bb=new  TransformRule001Dto();
//                                bb.setOrgField(""+cc);
//                                bb.setTransType("02");
//                                bb.setDataStatus(DictCodeListExt1.维护数据状态_待处理.getValue());
//                                bb.setIsModify("01");
//                                if(transformRuleMapper.getTransFieldCount(bb)<=0){
//                                    transformRuleMapper.insertTransField(bb);
//                                }
//                                ff = false;
//                                result+=cc;
//                            }
//                        }else{
//                            result+=cc;
//                        }
//                    }
//                }
//                if(ff){
//                    data.put(objFileds.get(0).getObjTableFiled(), result);
//                }
//            }
//
//        }
        return ff;
    }
    public static boolean findPatern(String parternid, String value) {
        boolean flag = true;
        Pattern pattern = Pattern.compile(parternid);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            flag = false;
        }
        return flag;
    }
//    /**
//     * 根据指定的规则，执行逻辑语句
//     * @param data 被检查的数据
//     * @param rule 检查规则实体
//     * @return
//     * @see
//     * @since 1.0
//     */
//    @SuppressWarnings("unused")
//    private static boolean isCondition2(Map<String, String> data, CheckElement rule) {
////        if (rule.getCondition() == null)
////            return true;
////        String express = rule.getCondition();
//        String express = "";
//        JexlEngine jexl = new JexlEngine();
//        Expression e = jexl.createExpression(express);
//        JexlContext jc = new MapContext();
//
//        if (data != null) {
//            for (String key : data.keySet()) {
//                Object o = data.get(key);
//                if (isNumeric(o.toString())) {
//                    o = new BigDecimal(o.toString());
//                }
//                jc.set(key, o);
//            }
//        }
//
//        Object o = e.evaluate(jc);
//        if (o == null) {
//            return false;
//        }
//        else if (o instanceof Boolean) {
//            return (Boolean)o;
//        }
//        else {
//            return false;
//        }
//
//    }

    /**
     * 判断指定的字符串是否是数字
     * @param str
     * @return
     * @see
     * @since 1.0
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().equals(""))
            return false;
        if (str.endsWith("."))  //不想调换复杂的正则表达式，对于[3.]这种数据也认为是合法的数字
            str = str.substring(0,str.length()-1);
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

//    /**
//     * 判断指定的字符串是否是数字
//     * @param str
//     * @return
//     * @see
//     * @since 1.0
//     */
//    public static boolean isNumeric2(String str) {
//        if (str == null || str.trim().equals(""))
//            return false;
//        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
//
//    }
    /**
     * 判断是否在整个白名单中
     * @param str
     * @return
     */
    public static boolean inWhitelistAll(String str){
        return str.matches(PATTERN_ALL);
    }
    /**
     * 判断是否在常用白名单中
     * @param str
     * @return
     */
    public static boolean inWhitelistCommon(String str){
        return str.matches(PATTERN_COMMON);
    }
    /**
     * 判断是否在非中文白名单中
     * @param str
     * @return
     */
    public static boolean inWhitelistNoChines(String str){
        return str.matches(PATTERN_NOCHINESE);
    }
    /**
     * 判断是否在非符号白名单中
     * @param str
     * @return
     */
    public static boolean inWhitelistNoSymbol(String str){
        return str.matches(PATTERN_NOSYMBOL);
    }
    // etliu 20191115 add start
    /**
     * 根据指定的规则，判断数据的长度是否是定长
     * 允许【固定14位】，或者【固定14位或者15位】
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return 检查结果
     * @see
     * @since 1.0
     */
    public static boolean fixedLength(Map<String, String> data, CheckElement rule) {
        String srcValue = data.get(rule.getSrcproperty());
        if (StringUtil.isEmpty(srcValue) && rule.allowNullable()) {
            // 传入数据为空，且允许为空
            return true;
        }
        if (StringUtil.isEmpty(srcValue)) {
            return false;
        }
        List<String> lengthList = StringUtil.splitToList(GlobalConstant.COMMA, rule.getObjvalue());
        for (String lengthStr : lengthList) {
            if (srcValue.length() == Integer.parseInt(lengthStr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据指定的规则，判断数据指定的属性的值是否是数字
     * 对于小数位数的check，只要小数位数小于规定长度，都认为符合要求
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return 检查结果
     * @see
     * @since 1.0
     */
    public static boolean isNumber2(Map<String, String> data, CheckElement rule) {

        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;

        String objValue = rule.getObjvalue();
        if ( !StringUtil.isEmpty( objValue ) ) { //如果对格式要求不为空
            //格式如：10,5
            String [] objFormat = objValue.split(",");
            int allbit = Integer.parseInt(objFormat[0]);
            int decbit = Integer.parseInt(objFormat[1]);
            //"^[-+]?[1-9]{5}[.][0-9]{2}$";
            String format = "";
            if ( decbit>0 && StringUtil.contains(srcValue, GlobalConstant.POINT))
                format = "^([-+])?([0-9]{1,"+(allbit-decbit)+"})?[.][0-9]{1,"+decbit+"}$";
            else
                format = "^([-+])?([0-9]{1,"+(allbit-decbit)+"})$";

            return srcValue.matches(format);
        } else {
            return isNumeric(srcValue);
        }
    }
    /**
     * 根据指定的规则，判断日期数据是否是指定的格式
     * ※严格判断是否符合日期格式
     * @param data 被检查的数据
     * @param rule 检查规则实体，年:小写，月:大写，日:小写，时:大写,分:小写，秒:小写
     * @return
     * @see
     * @since 1.0
     */
    public static boolean isValidDate2(Map<String, String> data, CheckElement rule) {
        boolean ret = false;
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        String objValue = rule.getObjvalue();

        SimpleDateFormat sdf = new SimpleDateFormat(objValue);
        try {
            sdf.setLenient(false);
            sdf.parse(srcValue);
            ret = true;
        } catch (ParseException e) {
            //e.printStackTrace();
            ret = false;
        }

        // ※严格判断是否符合日期格式
        if (ret && DateUtil.isDateStr(srcValue, objValue)
                && srcValue.length() == objValue.length()) {
            ret = true;
        }
        else {
            ret = false;
        }

        return ret;
    }
    // etliu 20191115 add end

    /**
     * 判断是否是字符数组中
     * @param data 被检查的数据
     * @param rule 检查规则实体，年:小写，月:大写，日:小写，时:大写,分:小写，秒:小写
     * @return boolean
     */
    public  static boolean inArray(Map<String,String> data, CheckElement rule) {
        String srcValue = data.get(rule.getSrcproperty());
        String compareto = rule.getCompareto();
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;

        // add by zhangliang 20201023 判断符号如果是!=的场合 start
        // 此处判断是为了处理condition里比较值是空的场合，对于！=也是成立的
        if (StringUtil.equals(compareto, "!=") && StringUtil.isEmpty(srcValue)){
            return true;
        }
        // add by zhangliang 20201023 判断符号如果是!=的场合 end

        if ( StringUtil.isEmpty(srcValue) )
            return false;
        String objValue = rule.getObjvalue();
        List<String> objValueList = Arrays.asList(objValue.split(","));
        if(null != objValueList){
            if("==".equals(compareto)){
                if(objValueList.contains(srcValue))
                    return true;
            }else if("!=".equals(compareto)){
                if(!objValueList.contains(srcValue))
                    return true;
            }

        }

        return false;
    }

    /**
     * 求数组中最大的值
     * @param data  被检查的数据
     * @param rule  检查规则实体
     * @return
     */
    public static boolean inArrayMax(Map<String, String> data, CheckElement rule){
        //传入进来的数据值
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        //给的固定值
        String objValue = rule.getObjvalue();
        List<String> srcValueList = Arrays.asList(srcValue.split(","));
        String max= Collections.max(srcValueList);
        if(StringUtil.equals(max, objValue)){
            return true;
        }
        return  false;
    }


    /**
     * 求数组中最小的值是否等于固定值
     * @param data  被检查的数据
     * @param rule  检查规则实体
     * @return
     */
    public static boolean inArrayMin(Map<String, String> data, CheckElement rule){
        //传入进来的数据值
        String srcValue = data.get(rule.getSrcproperty());
        if ( StringUtil.isEmpty(srcValue)  && rule.allowNullable() )  //传入数据为空，且允许为空
            return true;
        if ( StringUtil.isEmpty(srcValue) )
            return false;
        //给的固定值
        String objValue = rule.getObjvalue();
        List<String> srcValueList = Arrays.asList(srcValue.split(","));
        String min= Collections.min(srcValueList);
        if(StringUtil.equals(min, objValue)){
            return true;
        }
        return  false;
    }

    /**
     * 根据指定的规则，判断数据指定的值与目标值大小
     * @param data 被检查的数据
     * @param rule 检查规则实体
     * @return
     * @see
     * @since 1.0
     */
    public static boolean compareValueSysPara(Map<String, String> data, CheckElement rule) {
        boolean result = false;

        String srcValue = rule.getSrcproperty();
        if (StringUtil.isNotEmpty(srcValue)){
            String sysParam = CacheUtil.getSysParam(srcValue);
            if (StringUtil.isNotEmpty(sysParam)){
                String objValue = rule.getObjvalue();
                String compareTo = rule.getCompareto();
                if (StringUtil.equals(compareTo, "!=") && !StringUtil.equals(sysParam, objValue)){
                    result = true;
                }
                if (StringUtil.equals(compareTo, "==") && StringUtil.equals(sysParam, objValue)){
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * 投资资产种类及比例特殊判断方法
     * @param data  被检查的数据
     * @param rule  检查规则实体
     * @return 检查结果
     */
    public static boolean invAssetTypeAndPercent(Map<String, String> data, CheckElement rule){
        //传入进来的数据值
        String srcValue = data.get(rule.getSrcproperty());
        //投资资产种类及比例要素的判断
        boolean  flag=true;
        String  regex="^(100|[1-9]?\\d(\\.\\d\\d?)?)%$|0$";
        if(StringUtil.isNotEmpty(srcValue)){
            //对于投资资产种类及比例要素按照";"切割成各个单元
            List<String>  invTaypeRatList=Arrays.asList(srcValue.split(GlobalConstant.SEMICOLON));
            //循环list，对于单元进行处理
            for(String invTaype:invTaypeRatList){
                //按照":"对于单元进行切割
                List<String>  invTaypePatten=Arrays.asList(invTaype.split(GlobalConstant.COLON));
                //单元切割后，不是2个部分。则格式不通过
                if(invTaypePatten!=null&&invTaypePatten.size()!=2){
                    flag=false;
                    break;
                }else{
                    //单元切割后是2个部分，获取数值和文字
                    //数值
                    if(invTaypePatten!=null){
                        String  pattenOne=invTaypePatten.get(0);
                        if(pattenOne.contains(GlobalConstant.SPLIT)){
                            //表示为区间
                            List<String>  pattenOneSection=Arrays.asList(pattenOne.split(GlobalConstant.SPLIT));
                            //校验区间
                            if(!pattenOneSection.get(0).matches(regex)||
                                    !pattenOneSection.get(1).matches(regex)){
                                flag=false;
                                break;
                            }
                        }else{
                            //不是区间，单个数字百分号
                            if(!pattenOne.matches(regex)){
                                flag=false;
                                break;
                            }
                        }
                        //文字
                        //String  pattenTwo=invTaypePatten.get(1);
                    }
                }
            }
        }
        return flag;
    }

}