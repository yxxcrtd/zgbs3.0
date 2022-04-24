/*
 * 文件名：FinBondTargetAssetValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.business.asset.bond.service;

import com.joyintech.framework.base.intf.Page;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dto.BondFinExercisePlanDto;
import com.joyintech.tams.business.asset.bond.dto.FinBondAssetSaveRequest;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.report.asset.bond.dto.BussFinBondRepInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 债券标的信息业务验证
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAssetValidatorService
 * @since 1.0
 */

@Service
public class FinBondAssetValidatorService implements BeanValidator{

    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private IPlatformDate iPlatformDate;
    @Autowired
    private FinBondAssetService finBondAssetService;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired
    private InvDataCheckService chbInvDataCheckService;

    @Override
    public List<ValidMessage> valid(Object value) {
        FinBondAssetSaveRequest dto = (FinBondAssetSaveRequest)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        //选择永续时特殊条款必输
        String isSus =  dto.getIsSus();
        if (StringUtil.equals(dto.getOperateType(), "submit")) {
            if(dto.getMdate()!=null &&DateUtil.compareDate(dto.getMdate(),DateUtil.platDateFromCache())<0){
                resultList.add(new ValidMessage("mdate",
                        getMessage("BUSS_COM_E_0004",getMessage("FinDepositTarget002.mdate"),getMessage("RskSupervisionPortfPoint002.platDate"))));
            }
            // 资产必须有还本计划
            if (DictCodeList.息票品种_附息.getValue().equals(dto.getFinInterestFactorDto().getCouponSpecies())
                    && DictCodeList.息票品种_利随本清.getValue().equals(dto.getFinInterestFactorDto().getCouponSpecies())) {
                if((dto.getFinInterestFactorDto().getCalcType() ==null)) {
                    resultList.add(new ValidMessage(getMessage("息票品种为附息或利随本清时，利率类型不能为空!")));
                }else{
//                    利率类型为浮动利率时，基准利率精度、基准利率、系数、利差、基准利率调整规则、首次利率确定日为空
                    if (StringUtil.equals(dto.getFinInterestFactorDto().getCalcType(), DictCodeList.利率类型_浮动利率.getValue())){
                        if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getBenchmarkIdAccuracy())){
                            resultList.add(new ValidMessage(getMessage("利率类型为浮动利率时，基准利率精度不能为空!")));
                        }
                        if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getBasicRate())){
                            resultList.add(new ValidMessage(getMessage("利率类型为浮动利率时，基准利率不能为空!")));
                        }
                        if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getCoefficient())){
                            resultList.add(new ValidMessage(getMessage("利率类型为浮动利率时，系数不能为空!")));
                        }
                        if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getSpreadRate())){
                            resultList.add(new ValidMessage(getMessage("利率类型为浮动利率时，利差不能为空!")));
                        }
                        if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getResetType())){
                            resultList.add(new ValidMessage(getMessage("利率类型为浮动利率时，基准利率调整规则不能为空!")));
                        }else{
                            //基准利率调整规则为基准利率变动后固定间隔生效时，延后时间和延后时间单位为空
                            if (StringUtil.equals(dto.getFinInterestFactorDto().getResetType(),DictCodeList.重置类型_基准利率调整规则_基准利率变动后固定间隔生效.getValue())){
                                if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getObserveBefDay())){
                                    resultList.add(new ValidMessage(getMessage("基准利率调整规则为基准利率变动后固定间隔生效时，延后时间不能为空。")));
                                }
                                if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getObserveBefUnit())){
                                    resultList.add(new ValidMessage(getMessage("基准利率调整规则为基准利率变动后固定间隔生效时，延后时间单位不能为空。")));
                                }
                            }
                        }
                        if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getFirstConfirmDate())){
                            resultList.add(new ValidMessage(getMessage("利率类型为浮动利率时，首次利率确定日不能为空!")));
                        }
                    }
                }

                if((dto.getFinInterestFactorDto().getYield() ==null)) {
                    resultList.add(new ValidMessage(getMessage("息票品种为附息或利随本清时，初始利率不能为空!")));
                }

                if((dto.getInvRepayList() ==null)) {
                    resultList.add(new ValidMessage(getMessage("息票品种为附息或利随本清时，还本计划不能为空!")));
                }

            }

            if (DictCodeList.息票品种_零息.getValue().equals(dto.getFinInterestFactorDto().getCouponSpecies())
                && DictCodeList.息票品种_贴现.getValue().equals(dto.getFinInterestFactorDto().getCouponSpecies())){
                if((dto.getFinInterestFactorDto().getYield() ==null)) {
                    resultList.add(new ValidMessage(getMessage("息票品种为附息或利随本清时，初始利率不能为空!")));
                }
                if((dto.getInvRepayList() ==null)) {
                    resultList.add(new ValidMessage(getMessage("还本计划不能为空!")));
                }
            }
            //含权债
            if (DictCodeList.是否_是.getValue().equals(dto.getIsRight())) {
            	if ( dto.getFinExercisePlanList() == null
            			|| dto.getFinExercisePlanList().size() == 0) {
            		resultList.add(new ValidMessage(getMessage("含权债的行权计划不能为空!")));
            	}else{
            	    for(BondFinExercisePlanDto finExercisePlan : dto.getFinExercisePlanList()){
                        if (finExercisePlan.getExercisePrice() == null) {
                            resultList.add(new ValidMessage(getMessage("行权计划中的行权价（净价）不能为空!")));
                            continue;
                        }
                    }
                }
            }
            if(dto.getTradeMarket ().equals ("03") || dto.getTradeMarket ().equals ("04")){
                if(dto.getIsNetprice () == null || dto.getIsNetprice () == ""){
                    resultList.add(new ValidMessage(getMessage("是否净价交易不能为空")));
                }
                if(dto.getTradePlat () == null || dto.getTradePlat () == ""){
                    resultList.add(new ValidMessage(getMessage("允许交易的平台不能为空")));
                }
            }
            // 获取对应画面的page
            Page page = CacheUtil.getPage("FinBondTargetAsset002");
            // 获取page中SYS_ELEMENT_CONTROL表配置的是否展示or隐藏画面元素。如果隐藏则跳过判断，如果展示则进入判断逻辑
            // SYS_ELEMENT_CONTROL表没有配置，认为是显示，需要做check
            if (null != page && ((null != page.getElementcontrol("bussRepBondInfo") && page.getElementcontrol("bussRepBondInfo").getIsShow())
                || page.getElementcontrol("bussRepBondInfo") == null)) {
                if(StringUtil.isNotEmpty(dto.getRepAssetType())
                        &&StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_地方政府债.getValue())){
                    if(dto.getBussFinBondRepInfoDto()!=null && !StringUtil.isNotEmpty(dto.getBussFinBondRepInfoDto().getBankSecZxzq())){
                        resultList.add(new ValidMessage("bussFinBondRepInfoDto.bankSecZxzq",
                                getMessage("是否专项债券数据不能为空。")));
                    }
                }
            }
        }
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()){
                for(FinBondAssetSaveRequest entity : dto.getSelectedItem()){
                    //只要存在债券交易就不可以删除债券资料，否则债券查询页面查询不到资产信息并报错，暂时修改债券资产，后续再考虑是否一并修改
                    if(commonBusinessService.isTransInAssetTwo(entity.getFinprodId())!=0){
                        resultList.add(new ValidMessage("",
                            getMessage("资产存在交易,不可删除!")));
                        break;
                    }
                }
                for(FinBondAssetSaveRequest entity : dto.getSelectedItem()) {
                    WflValidInputParameter parameter = new WflValidInputParameter();
                    parameter.setPageId("FinBondTargetAsset002");
                    parameter.setBusinessDataPk2(dto.getFinprodId());
                    if (mstWorkflowCommonService.isInWflProcess(parameter)) {
                        resultList.add(new ValidMessage("", getMessage("资产存在流程中数据,不可删除!")));
                        break;
                    }
                }
                for(FinBondAssetSaveRequest entity : dto.getSelectedItem()) {
                    if(finBondAssetService.selectCountByfinprodId(entity.getFinprodId())==0){
                        resultList.add(new ValidMessage("",
                                getMessage("该数据已被删除!")));
                        break;
                    }
                }
            }
        }
        else{
            if(StringUtil.equals(dto.getCreditFlag(), DictCodeList.是否_是.getValue())
                && (dto.getActualCreditProvid() == null || StringUtil.equals(dto.getActualCreditProvid(), ""))){
                resultList.add(
                    new ValidMessage("actualCreditProvid",this.getMessage("FW_E_0006", getMessage("FinPlanAssetC002.actualCreditProvid"))));
            }
        }
        //校验金融产品代码是否重复
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            if(StringUtil.isNotEmpty(dto.getFinprodMarketId())){
                if(finBondAssetService.selectCountByfinprodId(DictCodeList.平安资产类型_债券.getValue() +
                        "_" + dto.getFinprodMarketId())>0){
                    resultList.add(new ValidMessage("finprodMarketId",
                            getMessage("BUSS_COM_E_0006", getMessage("FinBondTargetAsset002.finprodId"),"")));
                }
            }
        }
        if(null!=dto.getVdate()&&null!=dto.getMdate()){
            if(DateUtil.compareDate(dto.getVdate(), dto.getMdate())!=-1){
                resultList.add(new ValidMessage("vdate",
                    getMessage("BUSS_COM_E_0035", getMessage("FinBondTargetAsset002.vdate"),getMessage("FinBondTargetAsset002.mdate"))));
            }
            if(StringUtil.isNotEmpty(dto.getFinprodId())){
                Date maxTradeDate = commonBusinessService.getMaxTradeDate(dto.getFinprodId());
                if(null!=maxTradeDate){
                    if(DateUtil.compareDate(dto.getMdate(), maxTradeDate) < 0){
                        resultList.add(new ValidMessage("mdate",
                            getMessage("BUSS_COM_E_0004", getMessage("FinDepositTarget002.mdate"), "最大交易日:" + DateUtil.format(maxTradeDate))));
                    }
                }
            }
        }
        //二级分类非空check
        if(StringUtil.isNotEmpty(dto.getFinprodMarketId()) && dto.getFinprodMarketId().indexOf(".")==0){
            resultList.add(new ValidMessage("finprodMarketId",
                getMessage("BUSS_COM_E_0038", getMessage("FinBondTargetAsset002.finprodId"))));
        }

        //是否城投债 为是时，城投债级别必填
        if (StringUtil.equals(dto.getIsCityBond(), DictCodeList.是否_是.getValue()) && StringUtil.isEmpty(dto.getCityBondLev())){
            resultList.add(new ValidMessage("cityBondLev",
                    getMessage("BUSS_COM_E_0038", getMessage("FinBondTargetAsset002.cityBondLev"))));
        }
        //是否展示资产四五级分类
        if(StringUtil.equals(CacheUtil.getSysParam("IS_SHOW_TYPE"),"01") &&  (StringUtil.equals(dto.getOperateType(), "save") ||  StringUtil.equals(dto.getOperateType(), "submit"))) {
        	if(StringUtil.isEmpty(dto.getType11())) {
        		resultList.add(new ValidMessage("type11",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.type11"))));
        	}
        }
        // 如果是新增或者修改 则校验 一级市场投标债券  不在流程中 校验pk2
        // modify by wj 20200819 BS-757 【投资交易-新发行债权标的】一级市场投标债券可以加挂到两个债券标的上
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW) || StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)) {
            if(StringUtil.isNotEmpty(dto.getoFinprodId())) {
                // 选择的时候 已校验 已被别的债券占用的一级市场投标债券不会展示 所以只需要过滤流程中的判断。
                WflValidInputParameter parameter = new WflValidInputParameter();
                parameter.setPageId("FinBondTargetAsset002");
                parameter.setBusinessDataPk2(dto.getoFinprodId());
                if (mstWorkflowCommonService.isInWflProcess(parameter)) {
                    resultList.add(new ValidMessage("oFinprodId",this.getMessage("FIN_ASSET_E_0100")));
                }
                // 有些一级市场投标债券 是从快录进来的。。导致报错。校验是否被别的债券占用。
                // BS-757
                //【投资交易-新发行债权标的】一级市场投标债券可以加挂到两个债券标的上
                if(finBondAssetService.selectCountByOFinprodId(dto.getoFinprodId(),dto.getFinprodId())>0){
                    resultList.add(new ValidMessage("oFinprodId",
                            getMessage("FIN_ASSET_E_0103")));
                }
            }
            BussFinBondRepInfoDto bussFinBondRepInfoDto = dto.getBussFinBondRepInfoDto();
        }
        if (dto.getOperateType() != null && !StringUtil.equals(dto.getOperateType(), "delete")) {
            resultList = chbInvDataCheckService.chbCheckCommon(resultList, dto, "FinBondTargetAsset002", dto.getRepAssetType());
        }
        return resultList;
    }
}
