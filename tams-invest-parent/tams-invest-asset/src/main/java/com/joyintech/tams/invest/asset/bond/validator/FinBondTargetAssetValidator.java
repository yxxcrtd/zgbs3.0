/*
 * 文件名：FinBondTargetAssetValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.asset.bond.validator;

import com.google.common.cache.Cache;
import com.joyintech.framework.base.intf.Page;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.report.asset.bond.dto.BussFinBondRepInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.asset.bond.dto.FinBondTargetAssetDto;
import com.joyintech.tams.invest.asset.bond.service.FinBondTargetAssetService;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 债券标的信息业务验证
 * @author zhangnian
 * @version 1.0 2018年9月20日
 * @see FinBondTargetAssetValidator
 * @since 1.0
 */
@Component
public class FinBondTargetAssetValidator implements BeanValidator{

    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private FinBondTargetAssetService finBondTargetAssetService;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired
    private InvDataCheckService chbInvDataCheckService;

    @Override
    public List<ValidMessage> valid(Object value) {
        FinBondTargetAssetDto dto = (FinBondTargetAssetDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        //选择永续时特殊条款必输
        String isSus =  dto.getIsSus();
        /*String finprodMarketId  =  dto.getFinprodMarketId();*/
        /*String pattern = "[A-Za-z].*[0-9]|[0-9].*[A-Za-z]";
        if(!Pattern.matches(pattern, finprodMarketId)){
            resultList.add(new ValidMessage("finprodMarketId",
                getMessage("BUSS_COM_E_0044", getMessage("FinBondTargetAsset002.finprodId"))));
        }*/
        if (StringUtil.equals(dto.getOperateType(), "save")||StringUtil.equals(dto.getOperateType(), "submit")) {
            if(dto.getMdate()!=null &&DateUtil.compareDate(dto.getMdate(),DateUtil.platDateFromCache())<0){
                resultList.add(new ValidMessage("mdate",
                        getMessage("BUSS_COM_E_0004",getMessage("FinDepositTarget002.mdate"),getMessage("RskSupervisionPortfPoint002.platDate"))));
            }
            // 资产必须有还本计划
            if (!DictCodeList.息票品种_零息.getValue().equals(dto.getFinInterestFactorDto().getCouponSpecies())
                && !DictCodeList.息票品种_贴现.getValue().equals(dto.getFinInterestFactorDto().getCouponSpecies())
                && ((dto.getBussFinRepaymentDto() == null || dto.getBussFinRepaymentDto().getFinCashPlanList() == null
                     || dto.getBussFinRepaymentDto().getFinCashPlanList().size() == 0))) {
                resultList.add(new ValidMessage(getMessage("还本计划不能为空!")));
            }
            //含权债
            if (DictCodeList.是否_是.getValue().equals(dto.getIsRight())) {
            	if (dto.getBusiFinExercisePlanDto() == null
            			|| dto.getBusiFinExercisePlanDto().getFinExercisePlanList() == null
            			|| dto.getBusiFinExercisePlanDto().getFinExercisePlanList().size() == 0) {
            		resultList.add(new ValidMessage(getMessage("含权债的行权计划不能为空!")));
            	}else{
            	    for(FinExercisePlan finExercisePlan : dto.getBusiFinExercisePlanDto().getFinExercisePlanList()){
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
//                if(dto.getTradePlat () == null || dto.getTradePlat () == ""){
//                    resultList.add(new ValidMessage(getMessage("允许交易的平台不能为空")));
//                }
            }
            // 获取对应画面的page
            Page page = CacheUtil.getPage("FinBondTargetAsset002");
            // 获取page中SYS_ELEMENT_CONTROL表配置的是否展示or隐藏画面元素。如果隐藏则跳过判断，如果展示则进入判断逻辑
            // modify by etliu 20211223 start
            // SYS_ELEMENT_CONTROL表没有配置，认为是显示，需要做check
//            if (null != page && null != page.getElementcontrol("bussRepBondInfo") && page.getElementcontrol("bussRepBondInfo").getIsShow()) {
            if (null != page && ((null != page.getElementcontrol("bussRepBondInfo") && page.getElementcontrol("bussRepBondInfo").getIsShow())
                || page.getElementcontrol("bussRepBondInfo") == null)) {
                if(StringUtil.isNotEmpty(dto.getRepAssetType())
                        &&StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_地方政府债.getValue())){
                    if(dto.getBussFinBondRepInfoDto()!=null && !StringUtil.isNotEmpty(dto.getBussFinBondRepInfoDto().getBankSecZxzq())){
                        resultList.add(new ValidMessage("bussFinBondRepInfoDto.bankSecZxzq",
                                getMessage("是否专项债券数据不能为空。")));
                    }
                }
                if(StringUtil.isNotEmpty(dto.getType12())
                        &&StringUtil.equals(dto.getType12(), DictCodeListExt3.行内资产四级分类_可转债.getValue())){
                    if(dto.getBussFinBondRepInfoDto()!=null && !StringUtil.isNotEmpty(dto.getBussFinBondRepInfoDto().getBankSecKzzq())){
                        resultList.add(new ValidMessage("bussFinBondRepInfoDto.bankSecKzzq",
                                getMessage("是否商业银行可转债数据不能为空。")));
                    }
                }
            }
        }
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()){
                for(FinBondTargetAssetDto entity : dto.getSelectedItem()){
                    //只要存在债券交易就不可以删除债券资料，否则债券查询页面查询不到资产信息并报错，暂时修改债券资产，后续再考虑是否一并修改
                    //if(commonBusinessService.isTransInAsset(entity.getFinprodId())!=0){
                    if(commonBusinessService.isTransInAssetTwo(entity.getFinprodId())!=0){
                        resultList.add(new ValidMessage("",
                            getMessage("资产存在交易,不可删除!")));
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
                if(finBondTargetAssetService.selectCountByfinprodId(DictCodeList.平安资产类型_债券.getValue() +
                        "_" + dto.getFinprodMarketId())>0){
                    resultList.add(new ValidMessage("finprodMarketId",
                            getMessage("BUSS_COM_E_0006", getMessage("FinBondTargetAsset002.finprodId"),"")));
                }
            }
        }
        if(StringUtil.equals(isSus, DictCodeList.是否_是.getValue())){
            // 特殊条款已删除，相关check也删掉
          if(StringUtil.isEmpty(dto.getSustainableRemark())){
              resultList.add(new ValidMessage("sustainableRemark",
                  getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.sustainableRemark"))));
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
        if(StringUtil.equals(dto.getType4(), DictCodeList.WIND一级分类_含债券一级分类等_金融债.getValue())
            ||StringUtil.equals(dto.getType4(), DictCodeList.WIND一级分类_含债券一级分类等_短期融资券.getValue())
            ||StringUtil.equals(dto.getType4(), DictCodeList.WIND一级分类_含债券一级分类等_中期票据.getValue())
            ||StringUtil.equals(dto.getType4(), DictCodeList.WIND一级分类_含债券一级分类等_企业债.getValue())
            ||StringUtil.equals(dto.getType4(), DictCodeList.WIND一级分类_含债券一级分类等_公司债.getValue())
            ||StringUtil.equals(dto.getType4(), DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue())){
            if(StringUtil.isEmpty(dto.getType5())){
                resultList.add(new ValidMessage("type5",
                    getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.type5"))));
            }
        }
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
        	if(StringUtil.isEmpty(dto.getType12())) {
        		resultList.add(new ValidMessage("type12",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.type12"))));
        	}
        }
        if(StringUtil.equals(dto.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())
        ||StringUtil.equals(dto.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue())){
            if(StringUtil.isEmpty(dto.getConFinprodMarketId())){
                resultList.add(new ValidMessage("conFinprodMarketId",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.finProductConvertible.conFinprodId"))));
            }
//            if(StringUtil.isEmpty(dto.getFinProductConvertible().getConFinprodId())) {
//                resultList.add(new ValidMessage("conFinprodMarketId",
//                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.finProductConvertible.conFinprodId"))));
//            }
            if(StringUtil.isEmpty(dto.getFinProductConvertible().getConShareId())) {
                resultList.add(new ValidMessage("finProductConvertible.conShareId",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.finProductConvertible.conShareId"))));
            }
            if(null==dto.getFinProductConvertible().getConVdate()) {
                resultList.add(new ValidMessage("finProductConvertible.conVdate",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.finProductConvertible.conVdate"))));
            }
            if(null==dto.getFinProductConvertible().getConPrice()) {
                resultList.add(new ValidMessage("finProductConvertible.conPrice",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.finProductConvertible.conPrice"))));
            }
            if(null==dto.getFinProductConvertible().getResalePrice()) {
                resultList.add(new ValidMessage("finProductConvertible.resalePrice",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.finProductConvertible.resalePrice"))));
            }
            if(null==dto.getFinProductConvertible().getRedeemPrice()) {
                resultList.add(new ValidMessage("finProductConvertible.redeemPrice",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.finProductConvertible.redeemPrice"))));
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
                if(finBondTargetAssetService.selectCountByOFinprodId(dto.getoFinprodId(),dto.getFinprodId())>0){
                    resultList.add(new ValidMessage("oFinprodId",
                            getMessage("FIN_ASSET_E_0103")));
                }

            }

            BussFinBondRepInfoDto bussFinBondRepInfoDto = dto.getBussFinBondRepInfoDto();

            //ZGBSSE-558   【中债直连】债券标的新建提交时，必填项校验不正确 leipeng 20210611 改提示已经改动到CHB_DATACHECK_RULE_INV中
            //ZGBSS-1388 wyn 20210406 债券标的-权益类资产，新建、修改时不该提示“登记托管机构为99的时候,登记托管机构说明不能为空”
//            if (BussinessDictCodeList.行内资产二级分类_同业存单.getValue().equals(dto.getType9())
//                    || BussinessDictCodeList.行内资产二级分类_债券.getValue().equals(dto.getType9())) {
//                if (StringUtil.equals("99", dto.getRegistOrg())) {
//                    // 如果为99  则必须不能为空
//                    if (bussFinBondRepInfoDto != null) {
//                        if (StringUtil.isEmpty(bussFinBondRepInfoDto.getRepRegistorgComment())) {
//                            resultList.add(new ValidMessage(this.getMessage("FIN_ASSET_E_0101")));
//                        }
//                    }
//                } else {
//                    // 如果不为99  说明则必须为空
//                    if (bussFinBondRepInfoDto != null) {
//                        if (StringUtil.isNotEmpty(bussFinBondRepInfoDto.getRepRegistorgComment())) {
//                            resultList.add(new ValidMessage(this.getMessage("FIN_ASSET_E_0102")));
//                        }
//                    }
//                }
//            }

        }

        if (dto.getOperateType() != null && !StringUtil.equals(dto.getOperateType(), "delete")) {
            resultList = chbInvDataCheckService.chbCheckCommon(resultList, dto, "FinBondTargetAsset002", dto.getRepAssetType());
        }
        //ZGBSSE-588  【中债直连】债券标的新建提交时，具体类别选择99其他债券，备注check重复
//        if((StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW) || StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT))&&dto.getBussFinBondRepInfoDto().getRepSectype().equals("99")){
//            if(StringUtil.isEmpty(dto.getBussFinBondRepInfoDto().getRepRemark())){
//                resultList.add(new ValidMessage("当中债报送信息中具体类别为其他债券时，备注必填。"));
//            }
//        }
        return resultList;
    }
}
