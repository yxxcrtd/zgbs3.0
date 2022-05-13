package com.joyintech.tams.invest.asset.plan.planProfit.validator;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.asset.plan.planProfit.dto.FinPlanProfitAssetDto;
import com.joyintech.tams.invest.asset.plan.planProfit.service.FinPlanProfitAssetService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FinPlanProfitAssetValidator implements BeanValidator {
    /**
     * 计划类资产（净值型）Service
     */
    @Autowired
    private FinPlanProfitAssetService finPlanProfitAssetService;

    /**
     * 各种业务共通组件Service
     */
    @Autowired
    private CommonBusinessService commonBusinessService;

	@Autowired
	private FinProductMapper finProductMapper;

	@Autowired
	private InvDataCheckService chbInvDataCheckService;
	@Autowired
	private IPlatformDate iPlatformDate;

    @Override
    public List<ValidMessage> valid(Object value) {
        FinPlanProfitAssetDto dto = (FinPlanProfitAssetDto) value;
        List<ValidMessage> errorList = new ArrayList<>();

        if (!StringUtil.equals(dto.getOperateType(), "delete")) {
            if(StringUtil.isNotEmpty(dto.getBussRepAssetMngProdInfo()) && StringUtil.equals(dto.getBussRepAssetMngProdInfo().getRepAssetType(),"1705")) {
                if (StringUtil.isEmpty(dto.getBussRepAssetMngProdInfo().getRepFinancialComissue()) || dto.getBussRepAssetMngProdInfo().getRepFinancialComissue() == "") {
                    errorList.add(new ValidMessage("bussRepAssetMngProdInfo.repFinancialComissue",
                            "请输入是否由金融资产投资公司发行。"));
                }
            }
        }
        if (StringUtil.isNotEmpty(dto.getPageModel())) {
	        if(dto.getVdate()!=null && dto.getMdate()!=null && dto.getVdate().compareTo(dto.getMdate()) >= 0){
	            errorList.add(new ValidMessage("vdate",getMessage("BUSS_COM_E_0035",getMessage("FinPlanProfitAsset002.vdate"),
	                getMessage("FinPlanProfitAsset002.mdate"))));
	        }
			if (dto.getMdate()!=null && dto.getMdate().compareTo(iPlatformDate.getSysDate()) < 0) {
				errorList.add(new ValidMessage("mdate",
						"终止日期不能小于平台当前日期。"));
			}
	        if (!GlobalConstant.DETAIL_PAGE_MODEL_EDIT.equals(dto.getPageModel())) {
				//非母计划  买断利率必填
				if (StringUtil.equals(dto.getParentPlan(), DictCodeList.是否_否.getValue())) {
					if (null == dto.getBuyOutRate()) {
						errorList.add(new ValidMessage("buyOutRate",
								getMessage("COMMON_E_0057", getMessage("FinPlanProfitAsset002.buyOutRate"))));
					}
					if (null == dto.getIssueAmt()) {
						errorList.add(new ValidMessage("issueAmt",
								getMessage("COMMON_E_0057", getMessage("FinPlanProfitAsset002.issueAmt"))));
					}
				}
			}
	        if (WorkFlowUtil.isNotFromToDoList()) {
	            if (StringUtil.equals(dto.getOperateType(), "delete")) {
	                //if (dto.getSelectedItem() != null || dto.getSelectedItem().size() > 0) {
	                //    for(FinPlanProfitAssetDto dto1:dto.getSelectedItem()) {
	                //        if (commonBusinessService.isTransInAsset(dto1.getFinprodId())>0){
	                //            errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0043")));
	                //        }
	                //    }
	                //}
            }
            else{
                if(StringUtil.equals(dto.getCreditFlag(), DictCodeList.是否_是.getValue())
                    && (dto.getActualCreditProvid() == null || StringUtil.equals(dto.getActualCreditProvid(), ""))
					&&"02".equals(CacheUtil.getSysParam(BussinessConstant.CEMODEL))){
                    errorList.add(
                        new ValidMessage("actualCreditProvid",this.getMessage("FW_E_0006", getMessage("FinPlanAssetC002.actualCreditProvid"))));
                }
                if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                    if (finPlanProfitAssetService.selectFinprodMarketId(dto.getFinprodMarketId()) > 0) {
						errorList.add(new ValidMessage("finprodMarketId",getMessage("FIN_ASSET_E_0007", dto.getFinprodMarketId())));
                    }
					//     附件上传改为非必填 by zhuyongkang 20191010 start
//                    if(dto.getFileCommonList().size()==0){
//                        errorList.add(new ValidMessage(getMessage("FIN_ASSET_E_0003")));
//                    }
                }
            }

	        } else {
	            if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
	                if (finPlanProfitAssetService.selectFinprodMarketId(dto.getFinprodMarketId()) > 0) {
						errorList.add(new ValidMessage("finprodMarketId", getMessage("FIN_ASSET_E_0007", dto.getFinprodMarketId())));
	                }
	            }
	        }
			if (StringUtil.equals(dto.getOperateType(), "save") ||StringUtil.equals(dto.getOperateType(), "submit")){
				if(StringUtil.isNotEmpty(dto.getFinPayInterestStdInfoDto())){
					for(FinCashPlan finCashPlan : dto.getFinPayInterestStdInfoDto().getFinCashPlanList()){
						//计息开始日=到期日 或者 计息开始日等于计息结束日都不给提交
						if(finCashPlan.getVdate().compareTo(dto.getMdate())==0 ||
								finCashPlan.getVdate().compareTo(finCashPlan.getMdate())==0){
							errorList.add(new ValidMessage("存在计息开始日等于到期日/计息开始日等于计息结束日的付息记录!"));
							break;
						}
					}
				}
			}
        }
		if (StringUtil.equals(dto.getOperateType(), "delete")) {
			if (dto.getSelectedItem() != null && dto.getSelectedItem().size() > 0) {
				for(FinPlanProfitAssetDto dto1:dto.getSelectedItem()) {
					if (commonBusinessService.isTransInAsset(dto1.getFinprodId())>0){
						errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0043")));
					}

					// 校验子计划有没有交易
					List<String> subProd = finProductMapper.getSubProd(dto1.getFinprodId());
					if(!StringUtil.isEmptyList(subProd)){
						for (String subProdId: subProd) {
							if (commonBusinessService.isTransInAsset(subProdId)>0){
								errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0061",subProdId)));
							}
						}
					}

				}
			}
		}
		// 是否展示资产四五级分类
		if (StringUtil.equals(CacheUtil.getSysParam("IS_SHOW_TYPE"), "01") &&  (StringUtil.equals(dto.getOperateType(), "save") ||  StringUtil.equals(dto.getOperateType(), "submit"))) {
			if (StringUtil.isEmpty(dto.getType11())) {
				errorList.add(new ValidMessage("type11",
						getMessage("COMMON_E_0057", getMessage("FinPlanProfitAsset002.type11"))));
			}
			if (StringUtil.isEmpty(dto.getType12())) {
				errorList.add(new ValidMessage("type12",
						getMessage("COMMON_E_0057", getMessage("FinPlanProfitAsset002.type12"))));
			}
		}
		// 根据二级分类的值判断是否投向金融资产投资公司发行的私募资产管理产品是必填还是选填 by 赵东东 2019.10.16
		if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_公募计划.getValue())) {
			// 编辑时BussRepAssetMngProdInfo为空，不check by 赵东东 2019.10.16
			if(StringUtil.isNotEmpty(dto.getBussRepAssetMngProdInfo())) {
			if(StringUtil.isEmpty(dto.getBussRepAssetMngProdInfo().getRepPrivateprdFlag())) {
				errorList.add(new ValidMessage("BussRepAssetMngProdInfo.repPrivateprdFlag",
						getMessage("FIN_ASSET_E_0066", getMessage("FinPlanNetAsset002.type"))));
			}
		}
		}

		if (dto.getOperateType() != null && !StringUtil.equals(dto.getOperateType(), "delete")) {
			errorList = chbInvDataCheckService.chbCheckCommon(errorList, value, null, dto.getRepAssetType());
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
