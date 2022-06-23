/*
 * 文件名：TrdSpotexInvestTrade002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangguoxian
 * 修改时间：2019年7月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.spotex.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.trade.spotex.dto.TrdSpotexInvestTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 外汇即期交易新建画面Service
 * 
 * @author wangguoxian
 * @version 1.0 2019年7月8日
 * @see TrdSpotexInvestTrade002Service
 * @since 1.0
 */
@Service
public class TrdSpotexInvestTrade002Service extends BaseService<TrdSpotexInvestTradeDto> implements BaseProcessService<TrdSpotexInvestTradeDto> {

    /**
     * DateCalService
     */
    @Autowired(required = false)
    private DateCalService dateCalService;

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    
    /**
     * jixu接口
     */
    @Autowired
    private ValuationInterfService valuationInterfService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    @Override
    public void doComplete(TrdSpotexInvestTradeDto entity, OperationType operationType) {
        //设置母组合代码和子组合代码--范双虎
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        
        this.prepareEntity(entity);

        // 登录各业务表，包括金融产品交易、金融产品交易扩展、金融产品附属、交易类金融产品、交易费用明细表、组合交易表、划款指令及附件信息
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());

        // 金融产品交易信息DTO.交易费用明细表Entity -> 交易费用明细表
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        BeanUtil.copyPropertiesIgnoreNull(trdTradeFeeDetail, entity.getTrdTradeFeeDetail());

        // 金融产品交易信息DTO.金融产品交易扩展表Entity -> 金融产品交易扩展表
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getPaTrdProductDeal());

        // 金融产品交易信息DTO.交易类金融产品表Entity -> 交易类金融产品表
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        BeanUtil.copyPropertiesIgnoreNull(finTradeProduct, entity.getFinTradeProduct());

        // 金融产品分类
        FinProductType finProductType = entity.getFinProductType();
        
        // add by wangpeng 获取估值分类123 start
        ValItemValueGenUtil.genTypeForFinProductType(finTradeProduct, finProductType);
        // add by wangpeng end

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
        List<StlPayOrdersDto> stlPayOrders = entity.getStlPayOrdersList();

        if (!operationType.equals(OperationType.REVOKE)
            && !operationType.equals(OperationType.DELETE)) {
            // 组合交易表Entity -> 组合交易信息表
            PtlTrade ptlTrade = entity.getPtlTrade();
            List<PtlTrade>ptlList = new ArrayList<>();
            ptlList.add(ptlTrade);

            finTradeProductMapper.insertSelective(finTradeProduct);
            finProductTypeMapper.insertSelective(finProductType);
            paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            ptlTradeMapper.insertSelective(ptlTrade);
            if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getPortfolioId(),
                    entity.getActPortfolioId())) {
                PtlTrade actPtlTrade = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, entity.getPtlTrade());
                actPtlTrade.setPortfolioId(entity.getActPortfolioId());
                ptlList.add(actPtlTrade);
                ptlTradeMapper.insertSelective(actPtlTrade);
            }

            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
            List<TrdTradeFeeDetail> feeDetailList = new ArrayList<TrdTradeFeeDetail>();
            feeDetailList.add(trdTradeFeeDetail);
            baseTradeProductDealDto.setTrdTradeFeeDetailList(feeDetailList);
            // 划款指令
            if (!StringUtil.isEmptyList(stlPayOrders)) {
                baseTradeProductDealDto.setStlPayOrdersList(entity.getStlPayOrdersList());
            }
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);
            
            //子组合插入流水
            if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getActPortfolioId(),
                    entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
            // 7.2删除原业务流程表中的交易记录
            String workflowId = entity.getWorkflowId();
            if (StringUtil.isBlank(workflowId)) {
                WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
                wflTrdProductDealKey.setTradeId(entity.getTradeId());
                wflTrdProductDealKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                    WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

                if (wflTrdProductDeal != null) {
                    workflowId = wflTrdProductDeal.getWorkflowId();
                }
            }
            this.deleteBussFlowData(workflowId);

            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

            /*trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
            trdProductDeal.setBusiType(DictCodeListExt2.共通业务类型_外汇即期投资交易.getValue());
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);*/

         // 所有交易生效后调用的共通方法
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            inputDto.setPtlTradeList(ptlList);
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
          
//            // jixu接口
	          List<StlPayOrders> stlPayOrdersList = new ArrayList<StlPayOrders>();
	          if(entity.getStlPayOrdersList()!=null) {
	              stlPayOrdersList.addAll(entity.getStlPayOrdersList());
	          }
//	          TrdCommDto trdCommDto = new TrdCommDto();
//	          trdCommDto.setFinTradeProduct(entity.getFinTradeProduct());
//	          trdCommDto.setStlPayOrdersList(stlPayOrdersList);
//	          trdCommDto.setTrdProductDeal(trdProductDeal);
//	          trdCommDto.setPtlTradeList(ptlList);
//	          if(StringUtil.isNotEmpty(trdProductDealAdd) && StringUtil.isNotEmpty(entity.getPortfolioId())
//	              && !StringUtil.equals(entity.getPortfolioId(), trdProductDealAdd.getPortfolioId())) {
//	              trdProductDealAdd.setPortfolioId(entity.getPortfolioId());
//	          }
//	          trdCommDto.setTrdProductDealAdd(trdProductDealAdd);
//              trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_ADD);
//	          valuationInterfService.runValInvest2Jixu(trdCommDto);

	        // 调用投管接口 add by wqd 20190211 start
	        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
	        investInterfCommDto.setPageId("TrdSpotexInvestTrade002");
	        investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
	        investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
	        investInterfCommDto.setTrdProductDeal(trdProductDeal);
            investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
            investInterfCommDto.setPaTrdProductDeal(paTrdProductDeal);
            investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
            investInterfCommDto.setTradeFeeDetaillist(baseTradeProductDealDto.getTrdTradeFeeDetailList());
            investInterfCommDto.setOtherTypeForConvert(finTradeProduct.getExcPs());
            investInterfCommService.doInvestInterf(investInterfCommDto);
	        // 调用投管接口 add by wqd 20190211 end
        }
        // 撤销
        else {
            this.doCompleteRevoke(entity);
        }
        //        调用接口  add by zhangpuyu 2019/12/6 start
        try {
            List<String> tradeIds = new ArrayList<>();
            tradeIds.add(entity.getTradeId());
            restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                    Map.class);
        }catch (Exception e){

        }
        //        调用接口  add by zhangpuyu 2019/12/6 end
    }

    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(TrdSpotexInvestTradeDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                                                                                            + entity.getTradeId(),
            BaseTradeProductDealDto.class);
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());

                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(),
                        PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId()
                                                + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdProductDeal.setCreateTime(new Date());
                    paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                    paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
//                    bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class, paTrdProductDeal);
                }
                
//                // jixu 接口 add by qmh start
                List<StlPayOrders> stlPayOrdersList = new ArrayList<StlPayOrders>();
                if(entity.getStlPayOrdersList()!=null) {
                    stlPayOrdersList.addAll(entity.getStlPayOrdersList());
                }
//                TrdCommDto trdCommDto = new TrdCommDto();
//                trdCommDto.setFinTradeProduct(entity.getFinTradeProduct());
////                trdCommDto.setStlFinProductAcctList(stlFinProductAcctList);
//                trdCommDto.setStlPayOrdersList(stlPayOrdersList);
//                trdCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
//                trdCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
//                trdCommDto.setPtlTradeList(baseDto.getPtlTrade());
//                trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_DELETE);
//                valuationInterfService.runValInvest2Jixu(trdCommDto);
//                // jixu 接口 add by qmh end
                // 调用投管接口 add by wqd 20190211 start
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdSpotexInvestTrade002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
                investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
                investInterfCommDto.setTradeFeeDetaillist(baseDto.getTrdTradeFeeDetailList());
                investInterfCommDto.setOtherTypeForConvert(entity.getFinTradeProduct()==null?"":entity.getFinTradeProduct().getExcPs());
                investInterfCommService.doInvestInterf(investInterfCommDto);
                // 调用投管接口 add by wqd 20190211 end
                
                //删除授信占用
                if (null != baseDto.getTrdProductDeal().getTradeId() && !"".equals(baseDto.getTrdProductDeal().getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(baseDto.getTrdProductDeal().getTradeId());
                }
            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);
            
            //子组合插入流水
            if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getActPortfolioId(),
                    entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }

        // 删除原业务流程记录
        this.deleteBussFlowData(entity.getWorkflowId());

        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doInsert(TrdSpotexInvestTradeDto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE)
            && !operationType.equals(OperationType.DELETE)) {
            this.handleBussFlowData(entity);

        }
        // 撤销交易
        else {
            this.doRevokeInsert(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdSpotexInvestTradeDto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType())
            && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            this.doDelete(entity.getWorkflowId());

            // 重新insert
            this.handleBussFlowData(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        
        //根据工作流代码查询交易相关信息
//        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
//            ServiceApplication.TRADE_SERVICE_NAME
//                    + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);
//        //删除授信占用
//        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
//            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
//        }
    }

    /**
     * 撤销交易
     * 
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(TrdSpotexInvestTradeDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                                                                                            + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
                // 交易状态=“撤销”
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                wflTrdProductDeal.setpTradeId(entity.getTradeId());
                // 撤销说明=画面.撤销说明
                wflTrdProductDeal.setCancelRemark(entity.getRevokeReason());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                wflTrdProductDeal.setTradeId(
                    entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd,
                    baseDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                if(StringUtil.isNotEmpty(baseDto.getActPortfolioId())) {
                    wflTrdProductDealAdd.setPortfolioId(baseDto.getActPortfolioId());
                }

                // 批量插入
//                bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,
//                    wflTrdProductDeal, WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());

                        String orderId;
                        if (baseGetBusinessId != null) {
                            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                            bussinessCodeParameter.setSeqNo("9998");
                            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
                        }
                        else {
                            orderId = IDGenerator.generate();
                        }
                        wflStlPayOrders.setOrderId(orderId);
                        wflStlPayOrders.setTradeId(wflTrdProductDeal.getTradeId());
                        wflStlPayOrders.setpTradeId(stlPayOrdersDto.getOrderId());
                        wflStlPayOrders.setProdId(wflTrdProductDeal.getTradeId());

//                        bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class,
//                            wflStlPayOrders);
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                    }
                }
            }
        }

    }

    /**
     * 提交时登录业务流程表
     * 
     * @param entity 画面参数Dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdSpotexInvestTradeDto entity) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
            && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                entity.getTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        this.prepareEntity(entity);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
        wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        //add by wangpeng 20190919 流程中 交易方向存入回购交易方向字段
        wflPaTrdProductDeal.setBackLendDirection(entity.getFinTradeProduct().getExcPs());
        //add end 

        WflTrdProductDealOth wflTrdProductDealOth = setWflTrdProductDealOth(entity);
        wflTrdProductDealOth.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdProductDealOth.setWorkflowId(entity.getWorkflowId());
        // 批量插入
//        bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,
//            wflTrdProductDeal, WflTrdProductDealAddMapper.class, wflTrdProductDealAdd,
//            WflPaTrdProductDealMapper.class, wflPaTrdProductDeal, WflTrdProductDealOthMapper.class,
//            wflTrdProductDealOth);
        wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
        wflTrdProductDealOthMapper.insertSelective(wflTrdProductDealOth);

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        /*WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
        if (!StringUtil.isEmptyList(entity.getStlPayOrdersList())) {
            for (StlPayOrdersDto stlPayOrdersDto : entity.getStlPayOrdersList()) {
                BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                if (wflStlPayOrders.getOrderId() != null) {
                    bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class,
                        wflStlPayOrders);
                }
            }
        }*/

        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        /*FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(entity);
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            baseTradeProductDealDto.setStlPayOrdersList(
                Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
        }
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
        baseTradeProductDealCommService.execute(finProductTradeDto);*/
    }

    /**
     * 根据交易编号获取交易详情信息
     * 
     * @param tradeId 交易编号
     * @return 交易详情
     * @see
     * @since 1.0
     */
    public TrdSpotexInvestTradeDto getDealInfo(String tradeId,String validStatus) {
        Map<String, Object> map = new HashedMap<>();
        map.put("tradeId", tradeId);
        map.put("validStatus", validStatus);
        map.put("userId", LoginUserUtil.getUserName());
        TrdSpotexInvestTradeDto result = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdSpotexInvestTrade/getDealInfo",map, TrdSpotexInvestTradeDto.class);
        TrdInvestPersonInfo002Dto personInfo = new TrdInvestPersonInfo002Dto();
        personInfo.setCounterId(result.getCounterId());
        personInfo.setTrader(result.getTrader());
        personInfo.setCounterTrader(result.getCounterTrader());
        personInfo.setContactInfo(result.getCounterContact());
        result.setTrdInvestPersonInfo(personInfo);
        return result;
    }

    /**
     * 根据委托代码查询委托详情
     * 
     * @param entrustId 委托代码
     * @return 委托详情
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEtrustDto(String entrustId) {
        return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdSpotexInvestTrade/getEtrustDto?entrustId="
                                         + entrustId,
            BaseEtrInvestInfoDto.class);
    }

    /**
     * 组合可用现金查询 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate", DateUtil.format(sysDate));
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdSpotexInvestTrade/getProAvailableCash",
            param, Map.class);
    }

    /**
     * 获取下一个工作日
     *
     * @param parameters 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    public String getNextWorkDay(Map<String, Object> parameters) {
        if (dateCalService != null) {
            Date tradeDate = DateUtil.parse((String)parameters.get("tradeDate"));
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate,
                DateUtil.addDays(tradeDate, 31), (String)parameters.get("calendarId"));
            Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
            String accountSpeed = (String)parameters.get("accountSpeed");
            if (StringUtil.equals(accountSpeed, DictCodeListExt2.清算速度_T_2.getValue())) {
                tradingDay = dateCalService.calculationTradingDay(tradingDay, 1, holiday, "1");
            }
            return DateUtil.format(tradingDay);
        }

        return null;
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
//        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
//            workflowId);
//
//        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
//            "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
//            "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易业务流程其他信息
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_OTH",
//            "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID",
//            workflowId);
        baseTradeProductDealCommService.deleteWflData(workflowId);
    }

    /**
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(TrdSpotexInvestTradeDto dto) {
        //流程中不会塞子组合代码，这里直接塞成组合代码--范双虎
        if(!StringUtil.isNotEmpty(dto.getActPortfolioId())) {
            dto.setActPortfolioId(dto.getPortfolioId());
        }

        // 业务类型
        dto.setBusiType(DictCodeListExt2.共通业务类型_外汇即期投资交易.getValue());
        // 金融产品代码
        dto.setFinprodId(dto.getTradeId());
        // 金融产品类型（估值核算）
        dto.setFinprodType(DictCodeListExt1.金融产品类型_即期.getValue());
        // 金融产品类型（投管用）
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        dto.setBranch(branchLong);
        // 单位全价
        dto.setUnitFprice(dto.getUnitCprice());
        // 单位面值
        if (dto.getCurPair().contains(DictCodeList.币种_日元_JPY.getValue())) {
            dto.setUnitInt(new BigDecimal(100));
        }
        else {
            dto.setUnitInt(new BigDecimal(1));
        }
        // 画面输入.交易方向，存入交易类金融产品表
        String excPs = dto.getPs();
        // 金融产品交易表.交易方向 "买入"
        dto.setPs(DictCodeList.交易方向_买入.getValue());
        // 全价总额
        dto.setFpriceAmt(dto.getTradeAmt());
        // 未随交易支付费用
        dto.setFeeAmtUnpay(dto.getFeeAmt());
        // 是否结清
        dto.setIsClean(DictCodeList.是否_是.getValue());
        // 是否撤销
        dto.setIsCancel(DictCodeList.是否_否.getValue());
        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 本方交易员
            dto.setTrader(personInfo.getTrader());
            // 交易对手
            dto.setCounterId(personInfo.getCounterId());
            // 是否外部交易
            dto.setIsOutTrade(personInfo.getIsOutTrade());
            // 对手方交易员
            dto.setCounterTrader(personInfo.getCounterTrader());
            // 联系方式
            dto.setCounterContact(personInfo.getContactInfo());
        }
        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        // 交易编号
        trdProductDealAdd.setTradeId(dto.getTradeId());
        // 投资组合代码
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        // 委托代码
        trdProductDealAdd.setEntrustId(dto.getEntrustId());
        // 生成方式
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        //add by 张年 20190909 start
        trdProductDealAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        //add by 张年 20190909 end
        // 外汇交易类型
        trdProductDealAdd.setExcDealType(dto.getExcDealType());
        // 货币对
        trdProductDealAdd.setCurPair(dto.getCurPair());
        // 折美元金额
        trdProductDealAdd.setUsdAmt(dto.getUsdAmt());
        // 我方角色
        trdProductDealAdd.setOurRole(dto.getOurRole());
        // 交易模式
        trdProductDealAdd.setDealMode(dto.getDealMode());
        dto.setTrdProductDealAdd(trdProductDealAdd);
        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------

        // 组合交易表Entity 构造开始----------------------------------------------------------
        PtlTrade ptlTrade = new PtlTrade();
        // 组合代码
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        // 业务编号
        ptlTrade.setBusiNo(dto.getTradeId());
        // 业务表
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 业务明细代码
        ptlTrade.setBusiId(dto.getFinprodId());
        // 业务类型
        ptlTrade.setBusiType(DictCodeListExt2.共通业务类型_外汇即期投资交易.getValue());
        // 实际发生日
        ptlTrade.setSettleDate(dto.getTradeDate());
        // 工作流代码
        ptlTrade.setWorkflowId(dto.getWorkflowId());
        dto.setPtlTrade(ptlTrade);
        // 组合交易表Entity 构造结束----------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            dto.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            for (BussTrdTransferOrderDto item : dto.getBussTrdTransferOrder()) {
                StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, item);
                stlPayOrdersDto.setTradeId(dto.getTradeId());
                list.add(stlPayOrdersDto);
            }
            dto.setStlPayOrdersList(list);
        }
        // 划款指令表Entity 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId()); // 交易编号
        // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
        paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_外汇即期.getValue());
        // 交易所属部门
        paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept()); // 交易所属部门
        // 工作流代码
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setPaTrdProductDeal(paTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        // 交易费用明细表Entity 构造开始---------------------------------------------------------------
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        // 交易编号
        trdTradeFeeDetail.setTradeId(dto.getTradeId());
        // 费用类型
        trdTradeFeeDetail.setFeeType(DictCodeList.费用品种类型_交易手续费.getValue());
        // 是否随交易支付
        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        // 币种
        if (StringUtil.equals(dto.getExcDealType(), DictCodeListExt2.外汇交易类型_人民币外汇交易.getValue())) {
            trdTradeFeeDetail.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        }
        else {
            trdTradeFeeDetail.setCcy(dto.getDealCur());
        }
        // 费用金额
        trdTradeFeeDetail.setFeeAmt(dto.getFeeAmt());
        dto.setTrdTradeFeeDetail(trdTradeFeeDetail);
        // 交易费用明细表Entity 构造结束---------------------------------------------------------------

        // 交易类金融产品Entity 构造开始---------------------------------------------------------------
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        // 金融产品代码
        finTradeProduct.setFinprodId(dto.getTradeId());
        // 分支序号
        finTradeProduct.setBranch(branchLong);
        // 金融产品类型（投管分类）
        finTradeProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
        // 名义本金
        finTradeProduct.setPrin(dto.getPrinAmt());
        // 本金币种
        finTradeProduct.setCcy(dto.getDealCur());
        // 起息日
        finTradeProduct.setVdate(dto.getTradeDate());
        // 到期日
        finTradeProduct.setMdate(dto.getSettleDate());
        // 到期单位净价
        finTradeProduct.setmUnitCprice(dto.getUnitCprice());
        // 到期单位全价
        finTradeProduct.setmUnitFprice(dto.getUnitCprice());
        // 到期本金
        finTradeProduct.setmPrinAmt(dto.getPrinAmt());
        // 到期金额
        finTradeProduct.setmTradeAmt(dto.getTradeAmt());
        // 交易日历
        finTradeProduct.setCalendarId(dto.getCalendarId());
        // 交易对手
        finTradeProduct.setCounterId(dto.getCounterId());
        // 外汇交易方向
        finTradeProduct.setExcPs(excPs);
        // 货币对
        finTradeProduct.setCurPair(dto.getCurPair());
        // 持有目的 中债新增 20200409
 		finTradeProduct.setHoldPurpose(dto.getHoldPurpose());
        dto.setFinTradeProduct(finTradeProduct);
        // 交易类金融产品Entity 构造结束---------------------------------------------------------------
        // 交易产品分类表Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        // 金融产品代码 交易编号、
        finProductType.setFinprodId(dto.getFinprodId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finProductType.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 分类1
        finProductType.setType8(DictCodeListExt2.平安资产类型_外汇即期.getValue());
        /*添加五级分类映射 lishuping 2019/08/13 start*/
        FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
        finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
        finProductTypeConfig.setInputValue1(DictCodeListExt2.平安资产类型_外汇即期.getValue());
        finProductTypeConfig = restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                finProductTypeConfig, FinProductTypeConfig.class);
        finProductType.setType9(finProductTypeConfig.getOutputValue2());
        finProductType.setType10(finProductTypeConfig.getOutputValue3());
        finProductType.setType11(finProductTypeConfig.getOutputValue4());
        finProductType.setType12(finProductTypeConfig.getOutputValue5());
        /*添加五级分类映射 lishuping 2019/08/13 end*/
        dto.setFinProductType(finProductType);
        // 交易产品分类表Entity 构造结束----------------------------------------------------------
    }

    /**
     * 构造金融产品交易业务流程其他信息
     * 
     * @param entity 画面Dto
     * @return 金融产品交易业务流程其他信息
     * @see
     * @since 1.0
     */
    private WflTrdProductDealOth setWflTrdProductDealOth(TrdSpotexInvestTradeDto entity) {
        WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
        // 交易编号
        wflTrdProductDealOth.setTradeId(entity.getTradeId());
        // 外汇交易方向
        wflTrdProductDealOth.setExcPs(entity.getFinTradeProduct().getExcPs());
        // 交易货币
        wflTrdProductDealOth.setDealCur(entity.getDealCur());
        return wflTrdProductDealOth;
    }

    /**
     * 将一笔外汇即期交易转化成两笔划款指令共通用的交易
     * 
     * @param dto 参数
     * @return 两个币种的两笔交易数据
     * @see
     * @since 1.0
     */
    public List<TrdProductDeal> getEntity(TrdSpotexInvestTradeDto dto) {
        List<TrdProductDeal> resultList = new ArrayList<>();
        if (StringUtil.isEmpty(dto.getCurPair()) || StringUtil.isEmpty(dto.getPs())
            || StringUtil.isEmpty(dto.getDealCur()) || StringUtil.isEmpty(dto.getCcy())
            || dto.getSettleDate() == null || dto.getPrinAmt() == null) {
            return resultList;
        }
        String[] arr = dto.getCurPair().split("/");
        // 买/卖的场合
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
            if (arr[0].equals(dto.getDealCur())) {
                resultList = setStlDealData(dto, DictCodeList.交易方向_卖出.getValue(),
                    DictCodeList.交易方向_买入.getValue());
            }
            else {
                resultList = setStlDealData(dto, DictCodeList.交易方向_买入.getValue(),
                    DictCodeList.交易方向_卖出.getValue());
            }
        }
        else {
            if (arr[0].equals(dto.getDealCur())) {
                resultList = setStlDealData(dto, DictCodeList.交易方向_买入.getValue(),
                    DictCodeList.交易方向_卖出.getValue());
            }
            else {
                resultList = setStlDealData(dto, DictCodeList.交易方向_卖出.getValue(),
                    DictCodeList.交易方向_买入.getValue());
            }

        }
        return resultList;
    }

    /**
     * 将一笔外汇即期交易转化成两笔划款指令共通用的交易
     * 
     * @param dto 参数
     * @param psDeal 交易货币交易方向
     * @param psCor 对应货币交易方向
     * @return 两个币种的两笔交易数据
     * @see
     * @since 1.0
     */
    private List<TrdProductDeal> setStlDealData(TrdSpotexInvestTradeDto dto, String psDeal,
                                                String psCor) {
        List<TrdProductDeal> resultList = new ArrayList<>();
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
        trdProductDeal.setPs(psDeal);
        trdProductDeal.setTradeDate(dto.getSettleDate());
        trdProductDeal.setTradeAmt(dto.getPrinAmt());
        trdProductDeal.setCcy(dto.getDealCur());
        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());
        trdProductDeal.setDeliveryType(dto.getDeliveryType());
        resultList.add(trdProductDeal);
        trdProductDeal = new TrdProductDeal();
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
        trdProductDeal.setPs(psCor);
        trdProductDeal.setTradeDate(dto.getSettleDate());
        trdProductDeal.setTradeAmt(dto.getTradeAmt());
        trdProductDeal.setCcy(dto.getCcy());
        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());
        trdProductDeal.setDeliveryType(dto.getDeliveryType());
        resultList.add(trdProductDeal);
        return resultList;
    }

    /**
     * 查询委托可用额度
     * @param entrustId 委托代码
     * @return 委托可用额度
     */
    public BigDecimal selAvailableEtrAmt(String entrustId){
        return restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdSpotexInvestTrade/selAvailableEtrAmt/"
                        + entrustId,
                BigDecimal.class);
    }

    /** 获取业务参数
     * @param entity 业务实体
     * @param taskId 任务代码
     * @return 参数
     * @throws WorkFlowException 工作流异常
     */
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdSpotexInvestTradeDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
                BigDecimal prinAmt = null;
                if (StringUtil.isNotEmpty(dto.getCurPair()) && dto.getCurPair().contains(DictCodeList.币种_人民币_CNY.getValue())) {
                    if (StringUtil.equals(dto.getDealCur(), DictCodeList.币种_人民币_CNY.getValue())) {
                        prinAmt = dto.getPrinAmt();
                    } else {
                        prinAmt = dto.getTradeAmt();
                    }
                } else {
                    CommonBusinessService commonBusinessService = SpringContextUtil
                            .getBean(CommonBusinessService.class);
                    if (commonBusinessService != null) {
                        prinAmt = commonBusinessService.calExchangeAmt(dto.getPrinAmt(),
                                DateUtil.addDay(dto.getTradeDate(), -1), dto.getDealCur(),
                                DictCodeList.币种_人民币_CNY.getValue());
                    }
                }
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), prinAmt, LoginUserUtil.getUserName(),
//              DictCodeListExt2.平安资产类型_外汇即期.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
//              // 资产类型改为授权类型 add by renping 2019-08-06  start
//              , null
//              // 资产类型改为授权类型 add by renping 2019-08-06  end
//              );
      Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), prinAmt,LoginUserUtil.getUserName(),
          DictCodeListExt2.平安资产类型_外汇即期.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
          , null,entity.getWorkflowId());
      params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
      params.put("prinAmt", dto.getPrinAmt());
      return commonBusinessService.是否需要上级审批(params);
  }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
    }

    @Override
    public void doBack(TrdSpotexInvestTradeDto entity) {
        //切面处理风险接口退回
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }
    
    @Override
    public void doRevoke(TrdSpotexInvestTradeDto entity) {
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }
}
