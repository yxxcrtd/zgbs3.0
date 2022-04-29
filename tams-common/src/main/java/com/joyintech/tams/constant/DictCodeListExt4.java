package com.joyintech.tams.constant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;

/**
 * 数据字典明细枚举
 *
 * @author chenzhuo
 * @version 1.0 2016年11月12日
 * @see DictCodeListExt4
 * @since 1.0
 */
public enum DictCodeListExt4 implements BaseDictCodeList {
    详情日志切面开关_关闭(DictCode.详情日志切面开关,"01","关闭"),
    详情日志切面开关_开启(DictCode.详情日志切面开关,"02","开启"),
    会计分录类型_按科目分录(DictCode.会计分录类型,"01","按科目分录"),
    会计分录类型_按内部科目编码分录(DictCode.会计分录类型,"02","按内部科目编码分录"),

    /** add by weipeng on 20220410：现金管理类产品改造升级 start **/
    利息收入备抵科目_利息收入备抵(DictCode.增值税缴纳利息收入备抵科目,"01","利息收入增值税备抵"),
    利息收入备抵科目_投资收益_利息收入备抵(DictCode.增值税缴纳利息收入备抵科目,"02","投资收益_利息收入_增值税备抵"),
    其他综合收益计算的平准金科目_计入申赎(DictCode.其他综合收益计算的平准金科目,"01","计入其他综合收益-申赎"),
    其他综合收益计算的平准金科目_计入损益平准金(DictCode.其他综合收益计算的平准金科目,"02","计入损益平准金-其他综合收益"),
    /** add by weipeng on 20220411：现金管理类产品改造升级 end **/

    利率互换估值模型_DCF现金流折现(DictCode.利率互换估值模型,"01","01"),
    利率互换估值模型_应收应付轧差(DictCode.利率互换估值模型,"02","02"),

    /** add by weipeng on 20220426：股票相关变更 start **/
    股票持仓类型_新股(DictCode.股票持仓类型,"01","新股"),
    股票持仓类型_增发(DictCode.股票持仓类型,"02","增发"),
    股票持仓类型_流通(DictCode.股票持仓类型,"03","流通"),
    股票持仓类型_限售(DictCode.股票持仓类型,"04","限售"),
    交易方向_新股申购(DictCode.交易方向,"T101","新股申购"),
    交易方向_股票拆分交易(DictCode.交易方向,"T102","股票拆分交易"),
    交易方向_股票合并交易(DictCode.交易方向,"T103","股票合并交易"),
    交易方向_红利补税(DictCode.交易方向,"T104","红利补税"),
    交易方向_零碎股转现金(DictCode.交易方向,"T105","零碎股转现金"),
    配置类型_股票交易类型_TO_交易方向(DictCode.配置类型, "STOCK_TRADE_TYPE_TO_PS", "股票交易类型_TO_交易方向"),
    /** add by weipeng on 20220426：股票相关变更 end **/

    /** add by zhangzewei on 20220428：股票相关变更 start **/
    股票交易类型_买入_竞价(DictCode.股票交易类型,"01","买入（竞价）"),
    股票交易类型_买入_大宗(DictCode.股票交易类型,"02","买入（大宗）"),
    股票交易类型_卖出(DictCode.股票交易类型,"03","卖出"),
    股票交易类型_要约收购(DictCode.股票交易类型,"04","要约收购"),
    股票交易类型_港股通_现金收购(DictCode.股票交易类型,"05","港股通-现金收购"),
    股票交易类型_要约出售(DictCode.股票交易类型,"06","要约出售"),
    股票交易类型_配股(DictCode.股票交易类型,"07","配股"),
    股票交易类型_增发_配售(DictCode.股票交易类型,"08","增发（配售）"),
    股票交易类型_新股申购(DictCode.股票交易类型,"09","新股申购"),
    股票交易类型_新股中签(DictCode.股票交易类型,"10","新股中签"),
    股票交易类型_现金分红(DictCode.股票交易类型,"11","现金分红"),
    股票交易类型_送股(DictCode.股票交易类型,"12","送股"),
    股票交易类型_分红且送股(DictCode.股票交易类型,"13","分红且送股"),
    股票交易类型_红利补税(DictCode.股票交易类型,"14","红利补税"),
    股票交易类型_零碎股转现金(DictCode.股票交易类型,"15","零碎股转现金"),
    股票交易发行方式_新股申购(DictCode.股票交易发行方式,"01","未上市-增发"),
    股票交易发行方式_股票拆分交易(DictCode.股票交易发行方式,"02","未上市-其他"),
    股票交易发行方式_股票合并交易(DictCode.股票交易发行方式,"03","未上市-限售"),
    股票交易发行方式_红利补税(DictCode.股票交易发行方式,"04","未上市-新股"),
    股票交易发行方式零碎股转现金(DictCode.股票交易发行方式,"05","已上市"),
    /** add by zhangzewei on 20220428：股票相关变更 end **/
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

    DictCodeListExt4(DictCode dictCode, String value, String codeName) {
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
    public static DictCodeListExt4 from(DictCode dictCode, String value) {
        DictCodeListExt4[] values = DictCodeListExt4.values();
        for (DictCodeListExt4 code : values) {
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
    public static Map<String,Object> fromDictCodeList(DictCodeListExt4 dictCodeListExt2){
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
        DictCodeListExt4[] values = DictCodeListExt4.values();
        for (DictCodeListExt4 code : values) {
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
        DictCodeListExt4[] values = DictCodeListExt4.values();
        for (DictCodeListExt4 code : values) {
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

}
