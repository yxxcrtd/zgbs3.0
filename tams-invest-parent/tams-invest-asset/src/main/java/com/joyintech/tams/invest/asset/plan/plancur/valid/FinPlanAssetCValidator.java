/*
 * 文件名：FinPlanAssetCValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.asset.plan.plancur.valid;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.asset.plan.plancur.dto.FinPlanAssetCDto;
import com.joyintech.tams.invest.asset.plan.plancur.service.FinPlanAssetCService;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author qiangchengxi
 * @version 1.0 2019年1月7日
 * @see FinPlanAssetCValidator
 * @since 1.0
 */
@Component
public class FinPlanAssetCValidator implements BeanValidator {
    /**
     * 各种业务共通组件Service
     */
    @Autowired
    private CommonBusinessService commonBusinessService;
    /**
     * finPlanAssetCService
     */
    @Autowired
    private FinPlanAssetCService finPlanAssetCService;

    @Autowired
    private InvDataCheckService chbInvDataCheckService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        FinPlanAssetCDto dto = (FinPlanAssetCDto) value;
        List<ValidMessage> errorList = new ArrayList<>();

        if (!StringUtil.equals(dto.getOperateType(), "delete")) {
            if(StringUtil.isNotEmpty(dto.getBussRepAssetMngProdInfo()) && StringUtil.equals(dto.getBussRepAssetMngProdInfo().getRepAssetType(),"1705")) {
                if (StringUtil.isEmpty(dto.getBussRepAssetMngProdInfo().getRepFinancialComissue()) || dto.getBussRepAssetMngProdInfo().getRepFinancialComissue() == "") {
                    errorList.add(new ValidMessage("bussRepAssetMngProdInfo.repFinancialComissue",
                            "请输入是否由金融资产投资公司发行。"));
                }
            }
        }

        if (WorkFlowUtil.isNotFromToDoList()) {
            if (StringUtil.equals(dto.getOperateType(), "delete")) {
                for(FinPlanAssetCDto e:dto.getSelectedItem()) {
                    if (commonBusinessService.isTransInAsset(e.getFinprodId())>0){
                        errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0043")));
                    }
                    if(finPlanAssetCService.getBasicInfoByFinprodId(e.getFinprodId()) == null){
                        errorList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0022")));
                    }
                }
            }
            else{
                if(dto.getVdate() != null && dto.getMdate() != null
                    && dto.getVdate().compareTo(dto.getMdate()) >= 0){
                    errorList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0035",
                        getMessage("FinPlanAssetC002.vDate") + DateUtil.format(dto.getVdate()),
                        getMessage("FinPlanAssetC002.mDate")+DateUtil.format(dto.getMdate()))));
                }
                if(dto.getBussFinIntPayDto() != null && dto.getBussFinIntPayDto().getPayCycle() != null
                    && StringUtil.equals(dto.getBussFinIntPayDto().getPayCycle(), DictCodeList.收益结转频率_按月结转.getValue())
                    && (dto.getIsCompoundInt() == null || StringUtil.equals(dto.getIsCompoundInt(), ""))){
                    errorList.add(
                        new ValidMessage("isCompoundInt",this.getMessage("FW_E_0006", getMessage("FinPlanAssetC002.isCompoundInt"))));
                }
                if(StringUtil.equals(dto.getCreditFlag(), DictCodeList.是否_是.getValue())
                    && (dto.getActualCreditProvid() == null || StringUtil.equals(dto.getActualCreditProvid(), ""))){
                    errorList.add(
                        new ValidMessage("actualCreditProvid",this.getMessage("FW_E_0006", getMessage("FinPlanAssetC002.actualCreditProvid"))));
                }
                /*增加资产代码重复校验 add by zhuyongkang 20191021 start*/
                if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                    if (finPlanAssetCService.selectFinprodMarketId(dto.getFinprodMarketId()) > 0) {
                        errorList.add(new ValidMessage("finprodMarketId", getMessage("FIN_ASSET_E_0007", dto.getFinprodMarketId())));
                    }
                }

            }
        }
        else {
            if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                if (finPlanAssetCService.selectFinprodMarketId(dto.getFinprodMarketId()) > 0) {
                    errorList.add(new ValidMessage("finprodMarketId", getMessage("FIN_ASSET_E_0007", dto.getFinprodMarketId())));
                }
            }
        }
        /*增加资产代码重复校验 add by zhuyongkang 20191021 end*/
		// 是否展示资产四五级分类
		if (StringUtil.equals(CacheUtil.getSysParam("IS_SHOW_TYPE"), "01") &&  (StringUtil.equals(dto.getOperateType(), "save") ||  StringUtil.equals(dto.getOperateType(), "submit"))) {
			if (StringUtil.isEmpty(dto.getType11())) {
				errorList.add(new ValidMessage("type11",
						getMessage("COMMON_E_0057", getMessage("FinPlanAssetC002.type11"))));
			}
			if (StringUtil.isEmpty(dto.getType12())) {
				errorList.add(new ValidMessage("type12",
						getMessage("COMMON_E_0057", getMessage("FinPlanAssetC002.type12"))));
			}
		}
//选公募计划时，是否投向金融资产投资公司发行的私募资产管理产品，必填 by zhuyongkang  20191010 start
		if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_公募计划.getValue())) {
            if (StringUtil.isNotEmpty(dto.getBussRepAssetMngProdInfo())) {
                if (StringUtil.isEmpty(dto.getBussRepAssetMngProdInfo().getRepPrivateprdFlag())) {
                    errorList.add(new ValidMessage("BussRepAssetMngProdInfo.repPrivateprdFlag",
                            getMessage("FW_E_0006", getMessage("BussRepAssetMngProdInfo.repPrivateprdFlag"))));
                }
            }
        }

        if (dto.getOperateType() != null && !StringUtil.equals(dto.getOperateType(), "delete") && StringUtil.isNotEmpty(dto.getBussRepAssetMngProdInfo())) {
            errorList = chbInvDataCheckService.chbCheckCommon(errorList, value, null, dto.getBussRepAssetMngProdInfo().getRepAssetType());
        }

        //同一数据行：开放开始日小于等于开放结束日，开放结束日小于等于开放确认日
        if (dto.getBussFinProductOpenPlanDto() != null) {
            List<FinOpenPlan> finOpenPlanListTemp = dto.getBussFinProductOpenPlanDto().getFinOpenPlanList();
            if (!StringUtil.isEmptyList(finOpenPlanListTemp)){
                for (FinOpenPlan finOpenPlan : finOpenPlanListTemp) {
                    if(!(finOpenPlan.getOpenDateStr().compareTo(finOpenPlan.getOpenDateEnd())<=0 && finOpenPlan.getOpenDateEnd().compareTo(finOpenPlan.getOpenVdate())<=0)){
                        errorList.add(new ValidMessage("开放计划中开放开始日必须小于等于开放结束日，开放结束日必须小于等于开放确认日"));
                        break;
                    }
                }
            }
        }

        return errorList;
    }

}
