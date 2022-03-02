package com.joyintech.tams.constant;

import com.joyintech.framework.base.BaseDictCode;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典明细枚举
 *
 * @author chenzhuo
 * @version 1.0 2016年11月12日
 * @see FinanceOrgEnum
 * @since 1.0
 */
public enum FinanceOrgEnum implements BaseDictCodeList {
    金融机构简称_工商银行(DictCode.金融机构简称,"A001","工商银行"),
    金融机构简称_农业银行(DictCode.金融机构简称,"A002","农业银行"),
    金融机构简称_中国银行(DictCode.金融机构简称,"A003","中国银行"),
    金融机构简称_建设银行(DictCode.金融机构简称,"A004","建设银行"),
    金融机构简称_交通银行(DictCode.金融机构简称,"A005","交通银行"),
    金融机构简称_邮储银行(DictCode.金融机构简称,"B001","邮储银行"),
    金融机构简称_招商银行(DictCode.金融机构简称,"B002","招商银行"),
    金融机构简称_浦发银行(DictCode.金融机构简称,"B003","浦发银行"),
    金融机构简称_中信银行(DictCode.金融机构简称,"B004","中信银行"),
    金融机构简称_中国光大银行(DictCode.金融机构简称,"B005","中国光大银行"),
    金融机构简称_华夏银行(DictCode.金融机构简称,"B006","华夏银行"),
    金融机构简称_中国民生银行(DictCode.金融机构简称,"B007","中国民生银行"),
    金融机构简称_广发银行(DictCode.金融机构简称,"B008","广发银行"),
    金融机构简称_兴业银行(DictCode.金融机构简称,"B009","兴业银行"),
    金融机构简称_平安银行(DictCode.金融机构简称,"B010","平安银行"),
    金融机构简称_浙商银行(DictCode.金融机构简称,"B011","浙商银行"),
    金融机构简称_恒丰银行(DictCode.金融机构简称,"B012","恒丰银行"),
    金融机构简称_渤海银行(DictCode.金融机构简称,"B013","渤海银行"),
    金融机构简称_其他银行(DictCode.金融机构简称,"Z999","其他银行"),
    ;
    /**
     * 对应于数据字典code
     */
    private String value;

    /**
     * 数据字典枚举
     */
    private DictCode dictCode;

    /**
     * 明细名称
     */
    private String codeName;

    FinanceOrgEnum(DictCode dictCode, String value, String codeName) {
        this.dictCode = dictCode;
        this.value = value;
        this.setCodeName(codeName);
    }

    /**
     * 获得数据字典详细类
     *
     * @param value 数据字典值
     * @param dictCode 数据字典DictCode
     * @return DictCodeList
     * @see
     * @since 1.0
     */
    public static FinanceOrgEnum from(DictCode dictCode, String value) {
        FinanceOrgEnum[] values = FinanceOrgEnum.values();
        for (FinanceOrgEnum code : values) {
            if (StringUtil.equals(value, code.getValue()) && code.getDictCode() == dictCode) {
                return code;
            }
        }
        return null;
    }

    /**
     * 转换成DICTCODELIST
     * @param dictCodeListExt2
     * @return
     */
    public static Map<String,Object> fromDictCodeList(FinanceOrgEnum dictCodeListExt2){
        Map<String,Object> retMap=new HashMap<String,Object>();
        retMap.put("codeName",dictCodeListExt2.codeName);
        retMap.put("value",dictCodeListExt2.value);
        retMap.put("dictCode",dictCodeListExt2.dictCode);
        return retMap;
    }
    /**
     * 获取数据字典明细List
     *
     * @param dictCode DictCode
     * @return List&lt;SelectOptionDto&gt;
     * @see
     * @since 1.0
     */
    public static List<SelectOptionDto> fromDictCode(DictCode dictCode) {
        List<SelectOptionDto> result = new ArrayList<>();
        FinanceOrgEnum[] values = FinanceOrgEnum.values();
        for (FinanceOrgEnum code : values) {
            if (code.getDictCode() == dictCode) {
                SelectOptionDto option = new SelectOptionDto();
                option.setId(code.getValue());
                option.setText(code.getCodeName());
                result.add(option);
            }
        }
        return result;
    }

    /**
     * 获取数据字典明细valueList
     *
     * @param dictCode DictCode
     * @return List&lt;SelectOptionDto&gt;
     * @see
     * @since 1.0
     */
    public static List<String> valuesfromDictCode(DictCode dictCode) {
        List<String> result = new ArrayList<>();
        FinanceOrgEnum[] values = FinanceOrgEnum.values();
        for (FinanceOrgEnum code : values) {
            if (code.getDictCode() == dictCode) {
                result.add(code.getValue());
            }
        }
        return result;
    }

    /**
     * 获取 DictCode
     *
     * @return value.
     */
    @Override
    public DictCode getDictCode() {
        return dictCode;
    }

    /**
     * 获取 value
     *
     * @return value.
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 设置 value
     *
     * @param value value
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取 codeName
     *
     * @return codeName.
     */
    @Override
    public String getCodeName() {
        return codeName;
    }

    /**
     * 获取 带有引号的名称
     *
     * @return 带有引号的名称.
     */
    public String getQuotesCodeName() {
        return "“" + codeName + "”";
    }

    /**
     * 获取Integer value
     *
     * @return Integer value
     */
    public Integer getIntegerCodeName() {
        Integer result = null;
        try {
            result = Integer.parseInt(codeName);
        }
        catch (Exception e) {
        }
        return result;
    }

    /**
     * 获取Integer value
     *
     * @return Integer value
     */
    public BigDecimal getBigDecimalCodeName() {
        BigDecimal result = null;
        try {
            result = new BigDecimal(codeName);
        }
        catch (Exception e) {
        }
        return result;
    }

    /**
     * 设置 codeName
     *
     * @param codeName codeName
     */
    @Override
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    /**
     * 根据金融机构名称获取对应金融机构简称数据字典
     * @param financeOrgName
     * @return
     */
    public static FinanceOrgEnum getEnumByName(String financeOrgName) {
        List<String> result = new ArrayList<>();
        FinanceOrgEnum[] values = FinanceOrgEnum.values();
        for (FinanceOrgEnum code : values) {
            if (code.getValue().indexOf(financeOrgName) > 0) {
                return code;
            }
        }
        return FinanceOrgEnum.金融机构简称_其他银行;
    }
}
