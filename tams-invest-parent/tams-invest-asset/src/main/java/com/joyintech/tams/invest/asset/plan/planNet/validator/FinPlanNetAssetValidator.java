package com.joyintech.tams.invest.asset.plan.planNet.validator;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.asset.plan.planNet.dto.FinPlanNetAssetDto;
import com.joyintech.tams.invest.asset.plan.planNet.service.FinPlanNetAssetService;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FinPlanNetAssetValidator implements BeanValidator {
    /**
     * 计划类资产（净值型）Service
     */
    @Autowired
    private FinPlanNetAssetService finPlanNetAssetService;

    /**
     * 各种业务共通组件Service
     */
    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private InvDataCheckService chbInvDataCheckService;

    @Override
    public List<ValidMessage> valid(Object value) {
        FinPlanNetAssetDto dto = (FinPlanNetAssetDto) value;
        List<ValidMessage> errorList = new ArrayList<>();

        if (!StringUtil.equals(dto.getOperateType(), "delete")) {
            if(StringUtil.isNotEmpty(dto.getBussRepAssetMngProdInfo()) && StringUtil.equals(dto.getBussRepAssetMngProdInfo().getRepAssetType(),"1705")) {
                if (StringUtil.isEmpty(dto.getBussRepAssetMngProdInfo().getRepFinancialComissue()) || dto.getBussRepAssetMngProdInfo().getRepFinancialComissue() == "") {
                    errorList.add(new ValidMessage("bussRepAssetMngProdInfo.repFinancialComissue",
                            "请输入是否由金融资产投资公司发行。"));
                }
            }
        }
        //新建页面 资产投资性质 必填校验
        if(dto.getInvestmentAttributes() == null &&
                (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit"))){
            errorList.add(new ValidMessage("investmentAttributes",getMessage("BUSS_COM_E_0035",getMessage("FinPlanNetAsset002.investmentAttributes"),
                    getMessage("FinPlanNetAsset002.investmentAttributes"))));
        }

        if(dto.getVdate()!=null && dto.getMdate()!=null && dto.getVdate().compareTo(dto.getMdate()) >= 0){
            errorList.add(new ValidMessage("vdate",getMessage("BUSS_COM_E_0035",getMessage("FinPlanNetAsset002.vDate"),
                getMessage("FinPlanNetAsset002.mDate"))));
        }

        //add by wyn TISHKBS-1969 【计划类净值型标的】新建：“资产投资性质”必填项为空时，提交未校验 20210409
        if((dto.getInvestmentAttributes() == null || dto.getInvestmentAttributes().length() <= 0) &&
                (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit"))){
            errorList.add(new ValidMessage("investmentAttributes",getMessage("请输入资产投资性质")));
        }

        //modify by mashukai ZGBS-32 计划类净值型标的，点击删除按钮，提示请输入发行金额 20201126 start
        //add by mashukai FIX BUG TISHKBS-1465 新增发行金额必填校验 20201110 start
        if(dto.getIssueAmt() == null &&
                (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit"))){
            errorList.add(new ValidMessage("issueAmt",getMessage("请输入发行金额",getMessage("FinPlanNetAsset002.issueAmt"),
                    getMessage("FinPlanNetAsset002.issueAmt"))));
        }
        //add by mashukai FIX BUG TISHKBS-1465 新增发行金额必填校验 20201110 end
        //modify by mashukai ZGBS-32 计划类净值型标的，点击删除按钮，提示请输入发行金额 20201126 end
        if (WorkFlowUtil.isNotFromToDoList()) {
            if (StringUtil.equals(dto.getOperateType(), "delete")) {
                if(dto.getSelectedItem()!=null&&dto.getSelectedItem().size()>0){
                    for(FinPlanNetAssetDto dto1:dto.getSelectedItem()) {
                        if (commonBusinessService.isTransInAsset(dto1.getFinprodId())>0){
                            errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0043")));
                        }
                    }
                }
            }
            else if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
                if(StringUtil.isNotEmpty(dto.getFinprodMarketId())&& finPlanNetAssetService.selectFinprodMarketId(dto.getFinprodMarketId())>0){
                    errorList.add(new ValidMessage("finprodMarketId", getMessage("FIN_ASSET_E_0007", dto.getFinprodMarketId())));
                }
            //     附件上传改为非必填 by zhuyongkang 20191010 start
             /*   if(dto.getFileCommonList().size()==0){
                    errorList.add(new ValidMessage(getMessage("FIN_ASSET_E_0003")));
                }*/
            }
        }
        else{
            if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
                if(finPlanNetAssetService.selectFinprodMarketId(dto.getFinprodMarketId())>0){
                    errorList.add(new ValidMessage("finprodMarketId", getMessage("FIN_ASSET_E_0007", dto.getFinprodMarketId())));
                }
            }
            if(StringUtil.equals(dto.getCreditFlag(), DictCodeList.是否_是.getValue())
                && (dto.getActualCreditProvid() == null || StringUtil.equals(dto.getActualCreditProvid(), ""))){
                errorList.add(
                    new ValidMessage("actualCreditProvid",this.getMessage("FW_E_0006", getMessage("FinPlanAssetC002.actualCreditProvid"))));
            }
        }
		// 是否展示资产四五级分类
		if (StringUtil.equals(CacheUtil.getSysParam("IS_SHOW_TYPE"), "01") &&  (StringUtil.equals(dto.getOperateType(), "save") ||  StringUtil.equals(dto.getOperateType(), "submit"))) {
			if (StringUtil.isEmpty(dto.getType11())) {
				errorList.add(new ValidMessage("type11",
						getMessage("COMMON_E_0057", getMessage("FinPlanNetAsset002.type11"))));
			}
			if (StringUtil.isEmpty(dto.getType12())) {
				errorList.add(new ValidMessage("type12",
						getMessage("COMMON_E_0057", getMessage("FinPlanNetAsset002.type12"))));
			}
		}
	    // 根据二级分类的值判断是否投向金融资产投资公司发行的私募资产管理产品是必填还是选填 by 赵东东 2019.10.11
		if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_公募计划.getValue())) {
			// 编辑时BussRepAssetMngProdInfo为空，不check by 赵东东 2019.10.14
			if(StringUtil.isNotEmpty(dto.getBussRepAssetMngProdInfo())) {
			if(StringUtil.isEmpty(dto.getBussRepAssetMngProdInfo().getRepPrivateprdFlag())) {
				errorList.add(new ValidMessage("BussRepAssetMngProdInfo.repPrivateprdFlag",
						getMessage("FIN_ASSET_E_0066", getMessage("FinPlanNetAsset002.type"))));
			}
		}
		}
		//注释掉多余的check by 赵东东 2019.10.15 start
		// 增加法审编号和合同号的必填check by 赵东东 2019.10.14 start
//		if(StringUtil.isEmpty(dto.getTrialNo())) {
//		  errorList.add(new ValidMessage(getMessage("FIN_ASSET_E_0067")));
//		}
//	    if(StringUtil.isEmpty(dto.getContractNo())) {
//			errorList.add(new ValidMessage(getMessage("FIN_ASSET_E_0068")));
//		}
		// 增加法审编号和合同号的必填check by 赵东东 2019.10.14 end
		//注释掉多余的check by 赵东东 2019.10.15 end

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
