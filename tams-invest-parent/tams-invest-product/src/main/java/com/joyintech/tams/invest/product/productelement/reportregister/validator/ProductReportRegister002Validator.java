package com.joyintech.tams.invest.product.productelement.reportregister.validator;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.calculator.RepCheckUtil;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.tams.invest.product.productelement.reportregister.dto.ProductReportRegister002Dto;
import com.joyintech.tams.invest.product.productelement.reportregister.service.ProductReportRegister002Service;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 产品收集删除修改验证
 *
 * @author yaozhenguo
 * @version 1.0 2019年10月11日
 * @see ProductReportRegister002Validator
 * @since 1.0
 */
@Component
public class ProductReportRegister002Validator implements BeanValidator {

	@Autowired
	private ProductReportRegister002Service productReportRegister002Service;
	  /**
     * 工作流CommonServic
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

	@Autowired
	private InvDataCheckService chbInvDataCheckService;

	@Override
	public List<ValidMessage> valid(Object value) {
		ProductReportRegister002Dto entity = (ProductReportRegister002Dto) value;
		List<ValidMessage> validList = new ArrayList<ValidMessage>();
		//OPEX-597 新建行内识别码输入类似#@#￥的字符，可以保存成功，却无法查看详细信息 guyunfei 20191224 start
		if(StringUtil.isNotEmpty(entity.getRepFinprodInsId())){
			String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
			Pattern pattern = Pattern.compile(regEx);
			Matcher m = pattern.matcher(entity.getRepFinprodInsId());
			if(m.find()){
				validList.add(new ValidMessage("repFinprodInsId","行内识别码不应包含特殊字符"));
			}
		}
		//OPEX-597 新建行内识别码输入类似#@#￥的字符，可以保存成功，却无法查看详细信息 guyunfei 20191224 end
		if (StringUtil.equals(entity.getIsShortTerm(), DictCodeList.是否_中债_是.getValue())) {
			if (entity.getShortTerm() == null || StringUtils.isEmpty(entity.getShortTerm().toString())) {
				validList.add(new ValidMessage("shortTerm",
						this.getMessage("FIN_PROD_E_0001", this.getMessage("BussRepFinProductInfo.shortTerm"))));
			}
			if (StringUtils.isEmpty(entity.getIsFreeRedem())) {
				validList.add(new ValidMessage("isFreeRedem",
						this.getMessage("FIN_PROD_E_0001", this.getMessage("BussRepFinProductInfo.isFreeRedem"))));
			}
		}
		// 增信标识
		if (StringUtil.equals(entity.getRepCreditFlag(), DictCodeListExt2.有无_中债_有.getValue())) {
			// 增信机构类型必填
			if (StringUtils.isEmpty(entity.getRepCreditType())) {
				validList.add(new ValidMessage("repCreditType",
						this.getMessage("FIN_PROD_E_0001", this.getMessage("BussRepFinProductInfo.repCreditType"))));
			}
			// 增信形式必填
			if (StringUtils.isEmpty(entity.getRepCreditMode())) {
				validList.add(new ValidMessage("repCreditMode",
						this.getMessage("FIN_PROD_E_0001", this.getMessage("BussRepFinProductInfo.repCreditMode"))));
			}
		}
		// 运作模式
		if (StringUtil.equals(entity.getRepOperateMode(), DictCodeListExt2.产品运作模式_开放式净值型.getValue())
				|| StringUtil.equals(entity.getRepOperateMode(), DictCodeListExt2.产品运作模式_开放式非净值型.getValue())) {
			if (StringUtils.isEmpty(entity.getIsShortTerm())) {
				validList.add(new ValidMessage("isShortTerm",
						this.getMessage("FIN_PROD_E_0001", this.getMessage("BussRepFinProductInfo.isShortTerm"))));
			}
		}

		if (!StringUtil.isNotEmpty(entity.getIsFhxgcp())
        		&& !StringUtil.equals("01", CacheUtil.getSysParam("SVR_IS_SUBSIDIARY"))) {
            validList.add(new ValidMessage("isFhxgcp", getMessage("请输入新老产品标记")));
        }
		/****缺少“是否养老理财”字段，与新建输入页面字段不一致   修改方案为将此字段注释 挪到产品新建界面   leipeng 20210430***/
//        if (!StringUtil.isNotEmpty(entity.getIsYllccp())) {
//            validList.add(new ValidMessage("isYllccp", getMessage("请输入是否养老理财")));
//        }

		// 产品期次
		if (StringUtil.isNotEmpty(entity.getRepFinprodStage())) {
			if (RepCheckUtil.checkByteLength(entity.getRepFinprodStage().toString(), 6)) {
				validList.add(new ValidMessage("repFinprodStage",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repFinprodStage"))));
			}
		}
		// 起点销售金额
		if (!StringUtil.isNotEmpty(entity.getRepSaleAmtStr())) {
			validList.add(new ValidMessage("repSaleAmtStr",
					this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repSaleAmtStr"))));
		}
		// 销售手续费
		if (!StringUtil.isNotEmpty(entity.getRepSaleFeeRate())) {
			validList.add(new ValidMessage("repSaleFeeRate",
					this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repSaleFeeRate"))));
		}
		// 托管费率
		if (!StringUtil.isNotEmpty(entity.getRepTruFeeRate())) {
			validList.add(new ValidMessage("repTruFeeRate",
					this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repTruFeeRate"))));
		}
		// 业绩比较基准 update by yzg 改为选填 20191218
		if (StringUtil.isNotEmpty(entity.getRepYield())) {
			/*validList.add(new ValidMessage("repYield",
					this.getMessage("FW_E_0002", this.getMessage("ProductReportRegister001.repJizhun"))));
		}else {*/
			//业绩比较基准后台校验
			Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,5})?$");
			Matcher match=pattern.matcher(entity.getRepYield());
			if(!match.matches()){
				validList.add(new ValidMessage("repYield",
						this.getMessage("业绩比较基准:格式不正确", this.getMessage("ProductReportRegister001.repJizhun"))));
			}
		}
		// 计划募集金额
		if (!StringUtil.isNotEmpty(entity.getRepRaiseAmtPlan())) {
			validList.add(new ValidMessage("repRaiseAmtPlan",
					this.getMessage("FW_E_0002", this.getMessage("BussFinProductSaleParam.repRaiseAmtPlan"))));
		}
		// 行内识别码验证唯一
		if (StringUtil.equals(entity.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
			if (StringUtil.isNotEmpty(entity.getRepFinprodInsId())) {
				int i = productReportRegister002Service.selsectByFinprodInsId(entity.getRepFinprodInsId());
				if (i > 0) {
					validList.add(new ValidMessage("repFinprodInsId",
							this.getMessage("ProductReportRegister001.repFinprodInsId.repeat")));
				}
			}

			 //add by yujiaping 2020-11-26 ZGBS-22 增加流程中的校验  行内识别码唯一 start
            WflValidInputParameter inputParam = new WflValidInputParameter();
            inputParam.setPageId("ProductReportRegister002");
            inputParam.setBusinessDataPk1("F16_"+entity.getRepFinprodInsId());
            if(mstWorkflowCommonService.isInWflProcess(inputParam)){
            	validList.add(new ValidMessage("行内识别码已存在流程中，不可重复。"));
            }
            //add by yujiaping 2020-11-26 ZGBS-22 增加流程中的校验  行内识别码唯一 end
		}
		// 画面.募集开始日>画面.募集结束日
        if (StringUtil.isNotEmpty(entity.getRepRaisePstrdate())
            && StringUtil.isNotEmpty(entity.getRepRaisePenddate())) {
            if (DateUtil.compareDate(entity.getRepRaisePstrdate(), entity.getRepRaisePenddate()) > 0) {
                validList.add(new ValidMessage("repRaisePstrdate",
                    getMessage("BUSS_COM_E_0003", getMessage("ProductReportRegister001.repRaisePstrdate"),
                        getMessage("ProductReportRegister001.repRaisePenddate"))));
            }
        }

		// 发行机构代码
		if (StringUtil.isNotEmpty(entity.getRepIssuePartyId())) {
			if (RepCheckUtil.checkByteLength(entity.getRepIssuePartyId(), 6)) {
				validList.add(new ValidMessage("repIssuePartyId",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repIssuePartyId"))));
			}
		}
		// 产品审批人身份证号
		if (StringUtil.isNotEmpty(entity.getRepApproverId())) {
			if (RepCheckUtil.checkByteLength(entity.getRepApproverId(), 30)) {
				validList.add(new ValidMessage("repApproverId",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repApproverId"))));
			}
		}
		// 产品审批人姓名
		if (StringUtil.isNotEmpty(entity.getRepApproverName())) {
			if (RepCheckUtil.checkByteLength(entity.getRepApproverName(), 200)) {
				validList.add(new ValidMessage("repApproverName",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repApproverName"))));
			}
		}
		// 产品设计人身份证号
		if (StringUtil.isNotEmpty(entity.getRepDesignerId())) {
			if (RepCheckUtil.checkByteLength(entity.getRepDesignerId(), 30)) {
				validList.add(new ValidMessage("repDesignerId",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repDesignerId"))));
			}
		}
		// 产品设计人姓名
		if (StringUtil.isNotEmpty(entity.getRepDesignerName())) {
			if (RepCheckUtil.checkByteLength(entity.getRepDesignerName(), 200)) {
				validList.add(new ValidMessage("repDesignerName",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repDesignerName"))));
			}
		}
		// 投资经理身份证号
		if (StringUtil.isNotEmpty(entity.getRepManagerId())) {
			if (RepCheckUtil.checkByteLength(entity.getRepManagerId(), 30)) {
				validList.add(new ValidMessage("repManagerId",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repManagerId"))));
			}
		}
		// 投资经理姓名
		if (StringUtil.isNotEmpty(entity.getRepManagerName())) {
			if (RepCheckUtil.checkByteLength(entity.getRepManagerName(), 200)) {
				validList.add(new ValidMessage("repManagerName",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repManagerName"))));
			}
		}
		// 业务联络人姓名
		if (StringUtil.isNotEmpty(entity.getRepLiaisonName())) {
			if (RepCheckUtil.checkByteLength(entity.getRepLiaisonName(), 32)) {
				validList.add(new ValidMessage("repLiaisonName",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repLiaisonName"))));
			}
		}
		// 业务联络人座机
//		if (StringUtil.isNotEmpty(entity.getRepLiaisonTel())) {
//			if (RepCheckUtil.checkByteLength(entity.getRepLiaisonTel(), 30)) {
//				validList.add(new ValidMessage("repLiaisonTel",
//						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repLiaisonTel"))));
//			}
//		}
		// 业务联络人手机
        if (StringUtil.isNotEmpty(entity.getRepLiaisonMobile())) {
            if(!RepCheckUtil.checkNum11(entity.getRepLiaisonMobile())){
            	validList.add(new ValidMessage("repLiaisonMobile",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repLiaisonMobile"))));
            }
            else if (RepCheckUtil.checkByteLength(entity.getRepLiaisonMobile(), 11)) {
            	validList.add(new ValidMessage("repLiaisonMobile",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repLiaisonMobile"))));
			}
		}
		// 业务联络人邮箱
		if (StringUtil.isNotEmpty(entity.getRepLiaisonEmail())) {
			if (RepCheckUtil.checkByteLength(entity.getRepLiaisonMobile(), 50)) {
				validList.add(new ValidMessage("repLiaisonEmail",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repLiaisonEmail"))));
			}
		}
		// 境外托管机构名称
		if (StringUtil.isNotEmpty(entity.getRepOutDepName())) {
			if (RepCheckUtil.checkByteLength(entity.getRepOutDepName(), 200)) {
				validList.add(new ValidMessage("repOutDepName",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repOutDepName"))));
			}
		}
//		// 实际管理人名称
//		if (StringUtil.isNotEmpty(entity.getRepMname())) {
//			if (RepCheckUtil.checkByteLength(entity.getRepMname(), 120)) {
//				validList.add(new ValidMessage("repMname",
//						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repMname"))));
//			}
//		}
		// 境内托管机构名称
		if (StringUtil.isNotEmpty(entity.getRepInDepName())) {
			if (RepCheckUtil.checkByteLength(entity.getRepInDepName(), 120)) {
				validList.add(new ValidMessage("repInDepName",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repInDepName"))));
			}
			// “当境内托管机构名称为其他时,备注要素不可为空”
			else if (StringUtil.equals(entity.getRepInDepName(), "99") && StringUtil.isEmpty(entity.getRepRemark())) {
				validList.add(new ValidMessage("repInDepName", getMessage("BussRepFinProductInfo.repInDepNamAndRepRemark")));
			}
		}
//		if (StringUtil.isNotEmpty(entity.getRepCoopName())) {
//			if (RepCheckUtil.checkByteLength(entity.getRepCoopName(), 120)) {
//				validList.add(new ValidMessage("repCoopName",
//						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repCoopName"))));
//			}
//		}
		// 投资资产种类及比例
		if (StringUtil.isNotEmpty(entity.getRepInvTypeRat())) {
			if (RepCheckUtil.checkByteLength(entity.getRepInvTypeRat(), 300)) {
				validList.add(new ValidMessage("repInvTypeRat",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repInvTypeRat"))));
			}else if(entity.getRepInvTypeRat().contains("（") || entity.getRepInvTypeRat().contains("）") ||
					entity.getRepInvTypeRat().contains("：") || entity.getRepInvTypeRat().contains("；")) {
				validList.add(new ValidMessage("repInvTypeRat", this.getMessage("FW_E_0002",
                        this.getMessage("BussRepFinProductInfo.repInvTypeRat"))));
            }
		}
		if (StringUtil.isNotEmpty(entity.getRepRemark())) {
			if (RepCheckUtil.checkByteLength(entity.getRepRemark(), 256)) {
				validList.add(new ValidMessage("repRemark",
						this.getMessage("FW_E_0002", this.getMessage("BussRepFinProductInfo.repRemark"))));
			}
		}
		// 境内托管机构代码
		if (StringUtil.isNotEmpty(entity.getRepInDepCode())) {
			// 规则： CXXXXX（XXXXX 为数字）
			Pattern pattern = Pattern.compile("^[C]{1}[0-9]{5}$");
			Matcher matcher = pattern.matcher(entity.getRepInDepCode());
			if (!matcher.matches()) {
				validList.add(new ValidMessage("repInDepCode",
						getMessage("FW_E_0002", getMessage("BussRepFinProductInfo.repInDepCode"))));
			}
		}

		// modify by zhangliang 20190226 start
		// 资金投向地区
		if (StringUtil.isNotEmpty(entity.getRepInvestArea())) {
			if (StringUtil.equals(entity.getRepInvestArea(), DictCodeListExt2.资金投向地区_境外.getValue())
					|| StringUtil.equals(entity.getRepInvestArea(), DictCodeListExt2.资金投向地区_境内和境外.getValue())) {
				// 产品投资国家或地区
				if (StringUtil.isEmpty(entity.getRepInvestCountry())) {
					validList.add(new ValidMessage("repInvestCountry",
							getMessage("BUSS_COM_E_0038", getMessage("BussRepFinProductInfo.repInvestCountry"))));
				} else {
					if (StringUtil.equals(entity.getRepInvestArea(),
							DictCodeListExt2.资金投向地区_境外.getValue()) && StringUtil.indexOf(entity.getRepInvestCountry(), DictCodeListExt2.所属国家或地区_中国.getValue()) > -1) {
						validList.add(new ValidMessage("repInvestCountry", getMessage("REP_E_00011")));
					}
				}
				// 境外托管机构国别
				if (StringUtil.isEmpty(entity.getRepOutDepCtry())) {
					validList.add(new ValidMessage("repOutDepCtry",
							getMessage("BUSS_COM_E_0038", getMessage("BussRepFinProductInfo.repOutDepCtry"))));
				} else {
					if (StringUtil.equals(entity.getRepInvestArea(), DictCodeListExt2.资金投向地区_境外.getValue()) && StringUtil.indexOf(entity.getRepOutDepCtry(), DictCodeListExt2.所属国家或地区_中国.getValue()) > -1) {
						validList.add(new ValidMessage("repOutDepCtry", getMessage("REP_E_0008")));
					}
				}
				// 境外托管机构名称
				if (StringUtil.isEmpty(entity.getRepOutDepName())) {
					validList.add(new ValidMessage("repOutDepName",
							getMessage("BUSS_COM_E_0038", getMessage("BussRepFinProductInfo.repOutDepName"))));
				}
			}
			if (StringUtil.equals(entity.getRepInvestArea(), DictCodeListExt2.资金投向地区_境内.getValue())
					|| StringUtil.equals(entity.getRepInvestArea(), DictCodeListExt2.资金投向地区_境内和境外.getValue())) {
				// 理财业务服务模式
				if (StringUtil.isEmpty(entity.getRepFinanSerModel())) {
					validList.add(new ValidMessage("repFinanSerModel",
							getMessage("BUSS_COM_E_0038", getMessage("BussRepFinProductInfo.repFinanSerModel"))));
				}
				if (StringUtil.isEmpty(entity.getRepInDepName())) {
					validList.add(new ValidMessage("repInDepName",
							getMessage("BUSS_COM_E_0038", getMessage("BussRepFinProductInfo.repInDepName"))));
				}
			}
		}
		// 产品管理模式
		if (StringUtil.isNotEmpty(entity.getRepPmmode())) {
			// 实际管理人名称
			if (StringUtil.equals(entity.getRepPmmode(), DictCodeListExt1.产品管理模式_资产管理公司为实际管理人.getValue())
					|| StringUtil.equals(entity.getRepPmmode(), DictCodeListExt1.产品管理模式_其他机构为实际管理人.getValue())) {
				if (StringUtil.isEmpty(entity.getRepMname())) {
					validList.add(new ValidMessage("repMname",
							getMessage("BUSS_COM_E_0038", getMessage("BussRepFinProductInfo.repMname"))));
				}
			}
		}

		// 起点销售金额
//		if (StringUtil.isNotEmpty(entity.getRepSaleAmtStr())) {
//			if (entity.getRepSaleAmtStr().compareTo(BigDecimal.ZERO) < 0) {
//				validList.add(new ValidMessage("repSaleAmtStr",
//						getMessage("BUSS_COM_E_0004", getMessage("BussRepFinProductInfo.repSaleAmtStr"), "0")));
//			}
//		}
		// 合作模式
		if (StringUtil.isNotEmpty(entity.getRepContMode())) {
			// 合作机构名称
			if (!StringUtil.equals(entity.getRepContMode(), DictCodeListExt2.合作模式_独立运作.getValue())) {
				if (StringUtil.isEmpty(entity.getRepCoopName())) {
					validList.add(new ValidMessage("repCoopName",
							getMessage("BUSS_COM_E_0038", getMessage("BussRepFinProductInfo.repCoopName"))));
				}
			}
		}

		// 是否金融同业专属
		if (StringUtil.isEmpty(entity.getExcFinSector())) {
			validList.add(new ValidMessage("excFinSector",
					getMessage("BUSS_COM_E_0038", getMessage("BussRepFinProductInfo.excFinSector"))));
		}
		//是否现金管理类 产品运作模式开放式必填
        if (!StringUtil.isNotEmpty(entity.getRepIsCashManage())
        		&& (StringUtil.equals(entity.getRepOperateMode(), "03") || StringUtil.equals(entity.getRepOperateMode(),"04") )) {
            validList.add(new ValidMessage("请输入是否现金管理类"));
        }

		//发行机构代码必须与银行代码相同
		if (!StringUtil.isNotEmpty(entity.getRepIssuePartyId())
				&& StringUtil.equals(entity.getRepIssuePartyId(), CommonUtil.selectBankCode())) {
			validList.add(new ValidMessage("发行机构代码必须与银行代码相同。"));
		}

		 validList = chbInvDataCheckService.chbCheckCommon(validList, entity, null, null);

		return validList;
	}

}
