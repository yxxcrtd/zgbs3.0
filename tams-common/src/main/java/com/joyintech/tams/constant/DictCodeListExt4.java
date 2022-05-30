package com.joyintech.tams.constant;

import com.joyintech.framework.base.BaseDictCodeList;
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
 * @see DictCodeListExt4
 * @since 1.0
 */
public enum DictCodeListExt4 implements BaseDictCodeList {

    会计分录类型_按科目分录(DictCode.会计分录类型,"01","按科目分录"),
    会计分录类型_按内部科目编码分录(DictCode.会计分录类型,"02","按内部科目编码分录"),
    //20220413 add by gwl start
    发行阶段_待发行(DictCode.发行阶段,"01","待发行"),
    发行阶段_已发行(DictCode.发行阶段,"02","已发行"),
    发行阶段_已上市(DictCode.发行阶段,"03","已上市"),
    新债_发行方式_报价发行(DictCode.新债_发行方式,"01","报价发行"),
    新债_发行方式_招标发行(DictCode.新债_发行方式,"02","招标发行"),
    新债_发行方式_簿记建档(DictCode.新债_发行方式,"03","簿记建档"),
    招标方式_单一价格招标(DictCode.招标方式,"01","单一价格招标"),
    招标方式_数量招标(DictCode.招标方式,"02","数量招标"),
    招标方式_荷兰式招标(DictCode.招标方式,"03","荷兰式招标"),
    招标方式_混合式招标(DictCode.招标方式,"04","混合式招标"),
    招标标的_利率(DictCode.招标标的,"01","利率(%)"),
    招标标的_价格(DictCode.招标标的,"02","价格(元)"),
    招标标的_利差(DictCode.招标标的,"03","利差(BP)"),
    发行类型_首发(DictCode.发行类型,"01","首发"),
    发行类型_增发(DictCode.发行类型,"02","增发"),

    基准利率精度_1(DictCode.基准利率精度,"1","1"),
    基准利率精度_2(DictCode.基准利率精度,"2","2"),
    基准利率精度_3(DictCode.基准利率精度,"3","3"),
    基准利率精度_4(DictCode.基准利率精度,"4","4"),
    交收结果_正常交收(DictCode.交收结果,"01","正常交收"),
    交收结果_部分交收(DictCode.交收结果,"02","部分交收"),
    交收结果_交收失败(DictCode.交收结果,"03","交收失败"),
    交收结果_未交收(DictCode.交收结果,"04","未交收"),
    交收结果_交收通知(DictCode.交收结果,"05","交收通知"),
    //20220413 add by gwl end
    行内资产三级分类_金融债(DictCode.行内资产三级分类,"69","金融债"),
    行内资产三级分类_可转债(DictCode.行内资产三级分类,"70","可转债"),
    行内资产三级分类_可交换债(DictCode.行内资产三级分类,"71","可交换债"),
    行内资产三级分类_同业存单(DictCode.行内资产三级分类,"72","同业存单"),

    行内资产四级分类_一般地方政府债(DictCode.行内资产四级分类,"170","一般地方政府债"),
    行内资产四级分类_地方政府专项债(DictCode.行内资产四级分类,"171","地方政府专项债"),
    行内资产四级分类_政府支持机构债(DictCode.行内资产四级分类,"145","政府支持机构债"),
    行内资产四级分类_商业银行普通金融债(DictCode.行内资产四级分类,"146","商业银行普通金融债"),
    行内资产四级分类_商业银行次级债(DictCode.行内资产四级分类,"147","商业银行次级债"),
    行内资产四级分类_证券公司金融债(DictCode.行内资产四级分类,"148","证券公司金融债"),
    行内资产四级分类_保险公司金融债(DictCode.行内资产四级分类,"149","保险公司金融债"),
    行内资产四级分类_证券公司短期融资券(DictCode.行内资产四级分类,"150","证券公司短期融资券"),
    行内资产四级分类_其他金融机构债(DictCode.行内资产四级分类,"151","其他金融机构债"),
    行内资产四级分类_一般企业债(DictCode.行内资产四级分类,"152","一般企业债"),
    行内资产四级分类_集合企业债(DictCode.行内资产四级分类,"153","集合企业债"),
    行内资产四级分类_短期融资券(DictCode.行内资产四级分类,"154","短期融资券"),
    行内资产四级分类_超短期融资券(DictCode.行内资产四级分类,"155","超短期融资券"),
    行内资产四级分类_中期票据(DictCode.行内资产四级分类,"156","中期票据"),
    行内资产四级分类_项目收益债(DictCode.行内资产四级分类,"157","项目收益债"),
    行内资产四级分类_项目收益票据(DictCode.行内资产四级分类,"158","项目收益票据"),
    行内资产四级分类_集合票据(DictCode.行内资产四级分类,"159","集合票据"),
    行内资产四级分类_定向工具(DictCode.行内资产四级分类,"160","定向工具"),
    行内资产四级分类_其他企业债务融资工具(DictCode.行内资产四级分类,"161","其他企业债务融资工具"),
    行内资产四级分类_国际开发机构债(DictCode.行内资产四级分类,"162","国际开发机构债"),
    行内资产四级分类_外国政府债(DictCode.行内资产四级分类,"163","外国政府债"),
    行内资产四级分类_外国金融机构债(DictCode.行内资产四级分类,"164","外国金融机构债"),
    行内资产四级分类_外国公司债(DictCode.行内资产四级分类,"165","外国公司债"),
    行内资产四级分类_外国其他债券(DictCode.行内资产四级分类,"166","外国其他债券"),
    行内资产四级分类_同业存单(DictCode.行内资产四级分类,"167","同业存单"),
    行内资产四级分类_定向可转债(DictCode.行内资产四级分类,"168","定向可转债"),
    行内资产四级分类_分离交易的可转债(DictCode.行内资产四级分类,"169","分离交易的可转债"),

    WIND一级分类_含债券一级分类等_企业债务融资工具(DictCode.WIND一级分类_含债券一级分类等,"B20","企业债务融资工具"),
    WIND一级分类_含债券一级分类等_外国债券(DictCode.WIND一级分类_含债券一级分类等,"B21","外国债券"),
    WIND二级分类_含债券二级分类等_短期融资券(DictCode.WIND二级分类_含债券二级分类等,"21","短期融资券"),
    WIND二级分类_含债券二级分类等_项目收益债(DictCode.WIND二级分类_含债券二级分类等,"22","项目收益债"),
    WIND二级分类_含债券二级分类等_项目收益票据(DictCode.WIND二级分类_含债券二级分类等,"23","项目收益票据"),
    WIND二级分类_含债券二级分类等_其他企业债务融资工具(DictCode.WIND二级分类_含债券二级分类等,"24","其他企业债务融资工具"),
    WIND二级分类_含债券二级分类等_外国政府债(DictCode.WIND二级分类_含债券二级分类等,"25","外国政府债"),
    WIND二级分类_含债券二级分类等_外国金融机构债(DictCode.WIND二级分类_含债券二级分类等,"26","外国金融机构债"),
    WIND二级分类_含债券二级分类等_外国公司债(DictCode.WIND二级分类_含债券二级分类等,"27","外国公司债"),
    WIND二级分类_含债券二级分类等_外国其他债券(DictCode.WIND二级分类_含债券二级分类等,"28","外国其他债券"),
    平安业务类型_一级申购(DictCode.平安业务类型,"180","一级申购"),
    平安业务类型_债券分销买入确认(DictCode.平安业务类型,"202","债券分销买入确认"),

    结算方式_标的_实物结算(DictCode.结算方式_标的,"01","实物结算"),
    结算方式_标的_现金结算(DictCode.结算方式_标的,"02","现金结算"),

    中标方式_单标位中标(DictCode.中标方式,"01","单标位中标"),
    中标方式_多标位中标(DictCode.中标方式,"02","多标位中标"),


    头寸批处理类型_指令现金批处理(DictCode.头寸批处理类型,"01","指令现金日初批处理"),
    头寸批处理类型_指令持仓批处理(DictCode.头寸批处理类型,"02","指令持仓日初批处理"),
    头寸批处理类型_组合现金批处理(DictCode.头寸批处理类型,"03","组合现金日初批处理"),
    头寸批处理类型_组合持仓批处理(DictCode.头寸批处理类型,"04","组合持仓日初批处理"),
    头寸批处理类型_头寸日终批处理(DictCode.头寸批处理类型,"05","头寸日终批处理"),
    头寸批处理类型_同步数据(DictCode.头寸批处理类型,"06","同步数据批处理"),
    头寸批处理类型_标准券自动生成(DictCode.头寸批处理类型,"07","标准券自动生成"),
    投标方式_自主投标(DictCode.投标方式,"01","自主投标"),
    投标方式_代投代缴(DictCode.投标方式,"02","代投代缴"),
    投标方式_分销买入(DictCode.投标方式,"03","分销买入"),
    组合分配方式_平均分配(DictCode.组合分配方式,"139","平均分配"),
    组合分配方式_时间优先(DictCode.组合分配方式,"140","时间优先"),
    组合分配方式_数量优先(DictCode.组合分配方式,"141","数量优先"),
    组合分配方式_比例分配(DictCode.组合分配方式,"143","比例分配"),


    新债_担保方式_保证担保(DictCode.新债_担保方式,"01","保证担保"),
    新债_担保方式_不可撤销连带责任担保(DictCode.新债_担保方式,"02","不可撤销连带责任担保"),
    新债_担保方式_抵押担保(DictCode.新债_担保方式,"03","抵押担保"),
    新债_担保方式_反担保(DictCode.新债_担保方式,"04","反担保"),
    新债_担保方式_互相担保(DictCode.新债_担保方式,"05","互相担保"),
    新债_担保方式_豁免担保(DictCode.新债_担保方式,"06","豁免担保"),
    新债_担保方式_连带责任担保(DictCode.新债_担保方式,"07","连带责任担保"),
    新债_担保方式_违规担保(DictCode.新债_担保方式,"08","违规担保"),
    新债_担保方式_无偿担保(DictCode.新债_担保方式,"09","无偿担保"),
    新债_担保方式_信用担保(DictCode.新债_担保方式,"10","信用担保"),
    新债_担保方式_一般担保(DictCode.新债_担保方式,"01","一般担保"),
    新债_担保方式_质押担保(DictCode.新债_担保方式,"01","质押担保"),
    新债_担保方式_质押担保及抵押担保(DictCode.新债_担保方式,"01","质押担保及抵押担保"),

    共通业务类型_评级信息(DictCode.共通业务类型,"MST37","评级信息"),
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
