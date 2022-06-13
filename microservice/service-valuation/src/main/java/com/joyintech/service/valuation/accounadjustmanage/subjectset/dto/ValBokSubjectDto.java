/*
 * 文件名：ValBokSubjectDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年5月17日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.accounadjustmanage.subjectset.dto;

import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.service.valuation.accounadjustmanage.subjectset.validator.ValBokSubjectDtoValidator;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.valuation.common.utils.ExpressionUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@ValidLogic(targetClass = ValBokSubjectDtoValidator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class ValBokSubjectDto extends BokSubject implements ITableRow {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -1852967864201289335L;
    /**
     * 内部科目编码
     */
    private String subjectInternalCode;
    /**
     * "辅助核算表达式（存储FEL表达式，如交易场所为银行间，发行方式为封闭式则生成表达式TRADE_MARKET='01'&&ISSUE_TYPE='01'
     * 如果是记账科目，必填。"
     */
    private String subjectHandFelCode;

    /**
     * "辅助核算表达式中文 用于展示
     */
    private String subjectHandFelCodeName;

    private static final Set<String> sets = new TreeSet<String>();
    static {
        // &&和|| 转换为单字符@和#替代 方便判断
        sets.add("&&");
        sets.add("||");
        sets.add("@");
        sets.add("#");
        sets.add("!");
        sets.add("+");
        sets.add("-");
        sets.add("*");
        sets.add("/");
        sets.add("%");
        sets.add(">");
        sets.add("=");
        sets.add("<");
        sets.add(" ");
        sets.add("(");
        sets.add(")");
    }

    public String getSubjectHandFelCodeName() {
        StringBuilder sb = new StringBuilder();
        if(subjectHandFelCode!=null){
            Map<String, String> map = new HashMap<String, String>();
            Map<String, String> mapKey = new HashMap<String, String>();
            Map<String, String> mapVal = new HashMap<String, String>();
            // 表达式list
            List<String> explist = ExpressionUtil.split(subjectHandFelCode);
            for (int i=0;i<explist.size();i++) {
                String str=explist.get(i);
                if(CacheUtil.getDictCode(str)!=null){
                    String keyCn= CacheUtil.getDictCode(str).getDictCnname();
                    if (null != keyCn) {
                        sb.append(keyCn);
                    } else {
                        sb.append(str);
                    }
                }else{
                    //value值必须要用‘’，往前找key，转换value值为中文
                    if(str.contains("'")){
                        String key=null;
                        for (int j=0;j<i;j++) {
                            String substr=explist.get(j);
                            if(sets.contains(substr)){
                                continue;
                            }
                            key=substr;
                        }
                        str=getDictValue(key,str.replaceAll("'",""));
                    }
                    sb.append(str);
                }
            }
        }
        return sb.toString();
    }

    public void setSubjectHandFelCodeName(String subjectHandFelCodeName) {
        this.subjectHandFelCodeName = subjectHandFelCodeName;
    }
    /**
     * 币种
     */
    private String ccy;


    /**
     * 内部科目编码
     */
    @NotEmpty(fieldName = "ValSubjectSet001.subjectInternalCode", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "ValSubjectSet001.subjectInternalCode", max = 32, groups = {
            ValidateGroupEdit.class})

    public String getSubjectInternalCode() {
        return subjectInternalCode;
    }



    /**
     * 辅助核算表达式
     */
    @NotEmpty(fieldName = "ValSubjectSet001.subjectHandFelCode", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "ValSubjectSet001.subjectHandFelCode", max = 500, groups = {
            ValidateGroupEdit.class})

    public String getSubjectHandFelCode() {
        return subjectHandFelCode;
    }


    /**
     * 币种
     */
    @NotEmpty(fieldName = "ValSubjectSet001.ccy", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "ValSubjectSet001.ccy", max = 10, groups = {
            ValidateGroupEdit.class})
    @Override
    public String getCcy() {
        return ccy;
    }

    @Override
    public void setSubjectInternalCode(String subjectInternalCode) {
        this.subjectInternalCode = subjectInternalCode;
    }

    @Override
    public void setSubjectHandFelCode(String subjectHandFelCode) {
        this.subjectHandFelCode = subjectHandFelCode;
    }

    @Override
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }


    /**
     * 证券大类
     */
    private String bokBondType;
    /**
     * 证券分类
     */
    private String bokBondType2;
    /**
     * 交易市场
     */
    private String bokTrdMarket;
    /**
     * 发行方式
     */
    private String bokIssueType;
    /**
     * 交易对手类型
     */
    private String bokCpyType;
    /**
     * 账户类型
     */
    private String bokAccountType;
    /**
     * 费用类型
     */
    private String bokFeeType;
    /**
     * 交易属性
     */
    private String bokTrdProp;
    /**
     * 税费类型
     */
    private String bokTaxType;
    /**
     * 交易对手
     */
    private String bokCpy;
    /**
     * 渠道
     */
    private String bokChannel;

    public String getBokChannel() {
        return bokChannel;
    }

    public void setBokChannel(String bokChannel) {
        this.bokChannel = bokChannel;
    }

    public String getBokBondType() {
        return bokBondType;
    }

    public void setBokBondType(String bokBondType) {
        this.bokBondType = bokBondType;
    }

    public String getBokBondType2() {
        return bokBondType2;
    }

    public void setBokBondType2(String bokBondType2) {
        this.bokBondType2 = bokBondType2;
    }

    public String getBokTrdMarket() {
        return bokTrdMarket;
    }

    public void setBokTrdMarket(String bokTrdMarket) {
        this.bokTrdMarket = bokTrdMarket;
    }

    public String getBokIssueType() {
        return bokIssueType;
    }

    public void setBokIssueType(String bokIssueType) {
        this.bokIssueType = bokIssueType;
    }

    public String getBokCpyType() {
        return bokCpyType;
    }

    public void setBokCpyType(String bokCpyType) {
        this.bokCpyType = bokCpyType;
    }

    public String getBokAccountType() {
        return bokAccountType;
    }

    public void setBokAccountType(String bokAccountType) {
        this.bokAccountType = bokAccountType;
    }

    public String getBokFeeType() {
        return bokFeeType;
    }

    public void setBokFeeType(String bokFeeType) {
        this.bokFeeType = bokFeeType;
    }

    public String getBokTrdProp() {
        return bokTrdProp;
    }

    public void setBokTrdProp(String bokTrdProp) {
        this.bokTrdProp = bokTrdProp;
    }

    public String getBokTaxType() {
        return bokTaxType;
    }

    public void setBokTaxType(String bokTaxType) {
        this.bokTaxType = bokTaxType;
    }

    public String getBokCpy() {
        return bokCpy;
    }

    public void setBokCpy(String bokCpy) {
        this.bokCpy = bokCpy;
    }

    /**
     * 余额方向
     */
    @SuppressWarnings("unused")
    private String balFlagName;

    /**
     * 余额方向
     */
    @SuppressWarnings("unused")
    private String subjectTypeName;

    /**
     * 是否核算数量
     */
    @SuppressWarnings("unused")
    private String accQuaFlagName;

    /**
     * 是否计息
     */
    @SuppressWarnings("unused")
    private String accIntFlagName;

    /**
     * 是否允许透支
     */
    @SuppressWarnings("unused")
    private String overdrawnFlagName;


    /**
     * 自动生成子科目Code
     */
    private String genDetsubFlagName;

    /**
     * 画面模式
     */
    private String pageModel;

    /**
     * 画面模式
     */
    private String operateType;

    /**
     * 选中的数据
     */
    private transient List<ValBokSubjectDto> selectedItem;

    /**
     * @author  科目辅助项JSON
     * @description
     * @date 2022-1-20 11:24
     */
    private String handjson;

    public String getHandjson() {
        return handjson;
    }

    public void setHandjson(String handjson) {
        this.handjson = handjson;
    }

    private List<ValSubjectHandDto> subjectHands;

    public List<ValSubjectHandDto> getSubjectHands() {
        return subjectHands;
    }

    public void setSubjectHands(List<ValSubjectHandDto> subjectHands) {
        this.subjectHands = subjectHands;
    }

    /**
     * 账套代码
     */
    @Length(fieldName = "ValSubjectSet001.booksetId", max = 20, groups = {ValidateGroupEdit.class})
    @Override
    public String getBooksetId() {
        return super.getBooksetId();
    }

    /**
     * 科目号
     */
    @Length(fieldName = "ValSubjectSet001.subjectNo", max = 20, groups = {ValidateGroupEdit.class})
    @Override
    public String getSubjectNo() {
        return super.getSubjectNo();
    }

    /**
     * 科目名称
     */
    @NotEmpty(fieldName = "ValSubjectSet001.subjectName", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "ValSubjectSet001.subjectName", max = 200, groups = {
        ValidateGroupEdit.class})
    @Override
    public String getSubjectName() {
        return super.getSubjectName();
    }

    /**
     * 上级科目号
     */
    @Length(fieldName = "ValSubjectSet001.fsubjectId", max = 20, groups = {
        ValidateGroupEdit.class})
    @Override
    public String getFsubjectId() {
        return super.getFsubjectId();
    }

    /**
     * 余额方向
     */
    @Length(fieldName = "ValSubjectSet001.balFlag", max = 10, groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "ValSubjectSet001.balFlag", groups = {ValidateGroupEdit.class})
    @Override
    public String getBalFlag() {
        return super.getBalFlag();
    }

    /**
     * 科目类型
     */
    @NotEmpty(fieldName = "ValSubjectSet001.subjectType", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "ValSubjectSet001.subjectType", max = 10, groups = {
        ValidateGroupEdit.class})
    @Override
    public String getSubjectType() {
        return super.getSubjectType();
    }

    /**
     * 科目级别
     */
    @NotEmpty(fieldName = "ValSubjectSet001.subjectLevel", groups = {ValidateGroupEdit.class})
    @Override
    public Long getSubjectLevel() {
        return super.getSubjectLevel();
    }

    /**
     * 是否核算数量
     */
    @Length(fieldName = "ValSubjectSet001.accQuaFlag", max = 10, groups = {
        ValidateGroupEdit.class})
    @Override
    public String getAccQuaFlag() {
        return super.getAccQuaFlag();
    }

    /**
     * 是否计息
     */
    @Length(fieldName = "ValSubjectSet001.accIntFlag", max = 10, groups = {
        ValidateGroupEdit.class})
    @Override
    public String getAccIntFlag() {
        return super.getAccIntFlag();
    }

    /**
     * 是否允许透支
     */
    @Length(fieldName = "ValSubjectSet001.overdrawnFlag", max = 10, groups = {
        ValidateGroupEdit.class})
    @Override
    public String getOverdrawnFlag() {
        return super.getOverdrawnFlag();
    }

    /**
     * 获取 自动生成子科目
     * 
     * @return genDetsubFlag.
     */
    @Length(fieldName = "ValSubjectSet001.genDetsubFlag", max = 10, groups = {
            ValidateGroupEdit.class})
    @Override
    public String getGenDetsubFlag() {
        return super.getGenDetsubFlag();
    }



    public String getGenDetsubFlagName() {
        return genDetsubFlagName;
    }

    public void setGenDetsubFlagName(String genDetsubFlagName) {
        this.genDetsubFlagName = genDetsubFlagName;
    }

    /**
     * 基础科目性质
     */
    @Length(fieldName = "ValSubjectSet001.baseSubjectNature", max = 100, groups = {
        ValidateGroupEdit.class})
    @Override
    public String getBaseSubjectNature() {
        return super.getBaseSubjectNature();
    }

    /**
     * 扩展科目性质1
     */
    // @Length(fieldName = "ValSubjectSet001.subjectNature1", max = 100, groups = {
    // ValidateGroupEdit.class})
    // @Override
    // public String getSubjectNature1() {
    // return super.getSubjectNature1();
    // }

    /**
     * 扩展科目性质2
     */
    // @Length(fieldName = "ValSubjectSet001.subjectNature2", max = 100, groups = {
    // ValidateGroupEdit.class})
    // @Override
    // public String getSubjectNature2() {
    // return super.getSubjectNature2();
    // }

    /**
     * 扩展科目性质3
     */
    // @Length(fieldName = "ValSubjectSet001.subjectNature3", max = 100, groups = {
    // ValidateGroupEdit.class})
    // @Override
    // public String getSubjectNature3() {
    // return super.getSubjectNature3();
    // }

    /**
     * 扩展科目性质4
     */
    // @Length(fieldName = "ValSubjectSet001.subjectNature4", max = 100, groups = {
    // ValidateGroupEdit.class})
    // @Override
    // public String getSubjectNature4() {
    // return super.getSubjectNature4();
    // }

    /**
     * 扩展科目性质5
     */
    // @Length(fieldName = "ValSubjectSet001.subjectNature5", max = 100, groups = {
    // ValidateGroupEdit.class})
    // @Override
    // public String getSubjectNature5() {
    // return super.getSubjectNature5();
    // }

    /**
     * 扩展科目性质6
     */
    // @Length(fieldName = "ValSubjectSet001.subjectNature6", max = 100, groups = {
    // ValidateGroupEdit.class})
    // @Override
    // public String getSubjectNature6() {
    // return super.getSubjectNature6();
    // }

    public List<ValBokSubjectDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<ValBokSubjectDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public String getBalFlagName() {
        return this.balFlagName;
    }

    public void setBalFlagName(String balFlagName) {
        this.balFlagName = balFlagName;
    }

    public String getSubjectTypeName() {
        return this.subjectTypeName;
    }

    public void setSubjectTypeName(String subjectTypeName) {
        this.subjectTypeName = subjectTypeName;
    }

    public String getAccQuaFlagName() {
        return this.accQuaFlagName;
    }

    public void setAccQuaFlagName(String accQuaFlagName) {
        this.accQuaFlagName = accQuaFlagName;
    }

    public String getAccIntFlagName() {
        return this.accIntFlagName;
    }

    public void setAccIntFlagName(String accIntFlagName) {
        this.accIntFlagName = accIntFlagName;
    }

    public String getOverdrawnFlagName() {
        return this.overdrawnFlagName;
    }

    public void setOverdrawnFlagName(String overdrawnFlagName) {
        this.overdrawnFlagName = overdrawnFlagName;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    private static String getDictValue(String key,String value){
        List<? extends DataDictItem > dataDictItemList = SysDataUtil.getDataDictItem(key);
        if(CollectionUtils.isNotEmpty(dataDictItemList)){
            return (String)dataDictItemList.stream().filter((item) -> {
                return StringUtil.equals(item.getItemCode(), value);
            }).map(DataDictItem::getItemValue).findFirst().orElse((String)null);
        }else{
            return value;
        }
    }

    /*add by sunshuaishuai 辅助核算项 20220406 start*/
    /**
     * 辅助核算项type1
     */
    private String type1;

    /**
     * 辅助核算项type2
     */
    private String type2;

    /**
     * 辅助核算项type3
     */
    private String type3;

    /**
     * 辅助核算项type4
     */
    private String type4;

    /**
     * 辅助核算项type5
     */
    private String type5;

    /**
     * 辅助核算项type6
     */
    private String type6;

    /**
     * 辅助核算项type7
     */
    private String type7;

    /**
     * 辅助核算项type8
     */
    private String type8;

    /**
     * 辅助核算项type9
     */
    private String type9;

    /**
     * 默认
     */
    private String typeDefault;

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getType3() {
        return type3;
    }

    public void setType3(String type3) {
        this.type3 = type3;
    }

    public String getType4() {
        return type4;
    }

    public void setType4(String type4) {
        this.type4 = type4;
    }

    public String getType5() {
        return type5;
    }

    public void setType5(String type5) {
        this.type5 = type5;
    }

    public String getType6() {
        return type6;
    }

    public void setType6(String type6) {
        this.type6 = type6;
    }

    public String getType7() {
        return type7;
    }

    public void setType7(String type7) {
        this.type7 = type7;
    }

    public String getType8() {
        return type8;
    }

    public void setType8(String type8) {
        this.type8 = type8;
    }

    public String getType9() {
        return type9;
    }

    public void setType9(String type9) {
        this.type9 = type9;
    }

    public String getTypeDefault() {
        return typeDefault;
    }

    public void setTypeDefault(String typeDefault) {
        this.typeDefault = typeDefault;
    }

    /*add by sunshuaishuai 辅助核算项 20220406 end*/

}
