/*
 * 文件名：TrdBondLendInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年06月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.bondlend.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.interf.service.InterfComponentService;
import com.joyintech.tams.invest.trade.bondlend.dto.TrdBondLendInvestTradeDto;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 债券借贷投资交易新建画面Service
 *
 * @author 裴家俊
 * @version 1.0 2019年06月25日
 * @see TrdBondLendInvestTrade002Service
 * @since 1.0
 */
@Service
public class TrdBondLendInvestTrade002Service extends BaseService<TrdBondLendInvestTradeDto> implements BaseProcessService<TrdBondLendInvestTradeDto> {

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * DateCalService
     */
    @Autowired(required = false)
    private DateCalService dateCalService;

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 共通Mapper的通用代理方案
     */

    /**
     * 共通组件
     */
    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

    /**
     * 添加质押券组件Service
     */
    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;


    @Autowired(required = false)
    private FinProductMapper finProductMapper;
/**
     * {@linkplain}
     */
    @Autowired(required = false)
    private InterfComponentService interfComponentService;

    /**
     * MultiApproveTradeCheckService
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    @Autowired
    private StlFinProductAcctMapper stlFinProductAcctMapper;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinDealTargetMapper finDealTargetMapper;

    @Autowired
    private RepAssetDeriMapper repAssetDeriMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.MASERT)
    public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
        if (isMutiApproval) {
            // 业务编号
            String tradeId = entity.getTradeId();
            if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }

    @Override
    public void doInsert(TrdBondLendInvestTradeDto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            this.handleBussFlowData(entity,false);
            // 写入质押品的组合持仓流水
            this.ptlPredgeSecPositionFlowLogic(entity, false);
            // add by jiangpeng on 20210428 start
            // ZGBSSE-122 实时头寸查询，2019-5-30，债券借贷融出，交易可用持仓
            if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), entity.getFinprodType2())) {
                ptlOutSecPositionFlowLogic(entity, false, false, false);
            }
            // add by jiangpeng on 20210428 end
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
    public void doUpdate(TrdBondLendInvestTradeDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
        if (!StringUtil.equals("revoke", entity.getOperateType())
            && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
           // this.doDelete(entity.getWorkflowId());
            // 重新insert
            this.handleBussFlowData(entity,true);
            // 写入质押品的组合持仓流水,债券借贷质押品的组合持仓流水在提交时就改变
            this.ptlPredgeSecPositionFlowLogic(entity, false);
            // add by jiangpeng on 20210428 start
            // ZGBSSE-122 实时头寸查询，2019-5-30，债券借贷融出，交易可用持仓
            if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), entity.getFinprodType2())) {
                ptlOutSecPositionFlowLogic(entity, false, false, false);
            }
            // add by jiangpeng on 20210428 end
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @DTXTransaction
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

    @Override
    public void doComplete(TrdBondLendInvestTradeDto entity, OperationType operationType) {
        //设置母组合代码和子组合代码--范双虎
//        entity.setActPortfolioId(entity.getPortfolioId());
//        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

        this.prepareEntity(entity);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity.getTrdProductDeal());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setRegistOrg(getRegisOrgInfo(entity.getFinprodId()));//登记托管机构

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展表
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getPaTrdProductDeal());

        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            //金融产品分类
            FinProductType finProductType=entity.getFinProductType();
            //交易类金融产品
            FinTradeProductDto finTradeProductDto=entity.getFinTradeProduct();
            // 组合交易信息表
            PtlTrade ptlTrade = entity.getPtlTrade();
            //金融产品账户信息
            // add by wangpeng 获取估值分类123 start
            FinTradeProduct finTradeProduct = new FinTradeProduct();
            BeanUtil.copyPropertiesIgnoreNull(finTradeProduct,finTradeProductDto);
            ValItemValueGenUtil.genTypeForFinProductType(finTradeProduct, finProductType);
            // add by wangpeng end
            // 批量插入
            ptlTradeMapper.insertSelective(ptlTrade);
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            ptlTradeList.add(ptlTrade);
            if(!StringUtils.isEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getPortfolioId(),
                    entity.getActPortfolioId())) {
                PtlTrade actPtlTrade = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, entity.getPtlTrade());
                //母组合金融产品交易
                actPtlTrade.setPortfolioId(entity.getPortfolioId());
                ptlTradeMapper.insertSelective(actPtlTrade);
                ptlTradeList.add(actPtlTrade);
            }

            // 7.2写入金融产品标的信息（债券借贷）
            FinDealTarget finDealTarget = this.finDealTargetLogic(entity.getTrdProductDeal(),entity.getFinprodId());

            // 7.6写入金融衍生品交易报送信息
            RepAssetDeri repAssetDeri  = this.insertMonetaryDerivative_7_6(entity.getTrdProductDeal());

            stlFinProductAcctMapper.insertSelective(entity.getStlFinProductAcct());
            finProductTypeMapper.insertSelective(finProductType);
            finTradeProductMapper.insertSelective(finTradeProduct);
            finDealTargetMapper.insertSelective(finDealTarget);
            repAssetDeriMapper.insertSelective(repAssetDeri);
            paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            finTradeProductAddMapper.insertSelective(entity.getFinTradeProductAdd());

            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
            baseTradeProductDealDto.setBussRepoSec(entity.getBussRepoSec());
            baseTradeProductDealDto.setSecLendFinprodId(entity.getFinprodId());
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            //        委托代码不为空处理委托代码
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(item->{
                String entrustId = StringUtil.isNotEmpty(entity.getEntrustId())?entity.getEntrustId():item.getTrdProductDealAdd()!=null?item.getTrdProductDealAdd().getEntrustId():null;
                if(StringUtil.isEmpty(item.getEntrustId())&&StringUtil.isNotEmpty(entrustId)){
                    item.setEntrustId(entrustId);
                }
            });

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入交易基本信息与[实际]的组合持仓流水及现金流水
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

            
//            // 写入质押品的组合持仓流水,债券借贷的质押品的组合持仓流水变更到提交时改变
//            this.ptlPredgeSecPositionFlowLogic(entity, false);

            // 写入融入券组合持仓流水
            this.ptlBlendSecPositionFlowLogic(entity, false);

            // 7.3写入现金流相关信息表
            BaseFinProductCashInputDto basedto = this.cashFlowLogic(entity);

            // 7.4写入债券借贷质押品信息
            bussRepoSecService.insert(entity.getBussRepoSec(), entity.getTradeId());

            // 7.5写入交易信息进交易报送信息表，为中债报送用
            this.dealInfoCbReportLogic_7_5(entity);

            //插入风险试算结果信息
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

            // 所有交易生效后调用的共通方法(入账信息)
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);
            inputDto.setTrdProductDealAdd(trdProductDealAdd);
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            inputDto.setPtlTradeList(ptlTradeList);
            if (entity.getBussTrdTransferOrder() != null && !entity.getBussTrdTransferOrder().isEmpty()) {
                StlPayOrders stlPayOrders = new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                        entity.getBussTrdTransferOrder().get(0));
                inputDto.setStlPayOrders(stlPayOrders);
            }
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 调用投管接口 add by cxq 20190219 start
            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
            investInterfCommDto.setPageId("TrdBondLendInvestTrade002");
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
            investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
            investInterfCommDto.setTrdProductDeal(trdProductDeal);
            investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
            investInterfCommDto.setFinTradeProduct(finTradeProduct);
            investInterfCommDto.setFinCashCalcRule(basedto.getFinCashCalcRuleList().get(0));
            investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
            investInterfCommDto.setPledgeBondListDetailed(entity.getBussRepoSec().getPledgeBondListDetailed());
            investInterfCommService.doInvestInterf(investInterfCommDto);
            // 调用投管接口 add by cxq 20190219 end
            // add by jiangpeng on 20210428 start
            // ZGBSSE-122 实时头寸查询，2019-5-30，债券借贷融出，交易可用持仓
            if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), entity.getFinprodType2())){
                ptlOutSecPositionFlowLogic(entity, false, false, true);
            }
            //ZGBSSE-260
            //【债券借贷交易】新建债券借贷融入交易，审核岗审批后，融入债券的实时资产头寸中，实际、指令、交易持仓不一致
            /*else{
                ptlOutSecPositionFlowLogic(entity, false, false, true);
            }*/
            // add by jiangpeng on 20210428 end
        }
        // 撤销
        else {
            this.doCompleteRevoke(entity);
        }
//        调用接口  add by zhuyongkang 20191127 start
        List<String> tradeIds = new ArrayList<>();
        tradeIds.add(entity.getTradeId());
        try {
            restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                    Map.class);
        }catch (Exception e){

        }
//        调用接口  add by zhuyongkang 20191127 end
    }

    /**
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(TrdBondLendInvestTradeDto dto) {
        //流程中不会塞子组合代码，这里直接塞成组合代码--范双虎
        //设置母组合代码和子组合代码--范双虎
        if(StringUtil.isEmpty(dto.getActPortfolioId())){
            dto.setActPortfolioId(dto.getPortfolioId());
            dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        }
        // 金融产品交易Entity 构造开始----------------------------------------------------------
        TrdProductDeal entry=new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(entry, dto);
        // 业务类型
        entry.setBusiType(DictCodeListExt2.共通业务类型_债券借贷交易.getValue());
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_融入.getValue())) {
            entry.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
            // 金融产品类型（估值核算）
            entry.setFinprodType(DictCodeListExt1.金融产品类型_债券借贷融入.getValue());
        } else {
            entry.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
            // 金融产品类型（估值核算）
            entry.setFinprodType(DictCodeListExt1.金融产品类型_债券借贷融出.getValue());
        }
        //金融产品代码
        entry.setFinprodId(entry.getTradeId());//是交易编号，不是标的代码
        // 金融产品类型（估值核算）
        // 分支序号
        entry.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 交易方向 = 数据字典缓存.交易方向.买入
        entry.setPs(DictCodeList.交易方向_买入.getValue());
        // 清算速度
        if (StringUtil.equals(DictCodeList.清算速度_T_0.getValue(), dto.getSettleSpeedStr())) {
            entry.setSettleSpeed(0L);
        } else {
            entry.setSettleSpeed(1L);
        }
        // 交易平台等同交易场所，银行间
        entry.setTradePlat(dto.getTradeMarket());
        //交割日等同于交易日期
        entry.setSettleDate(dto.getTradeDate());
        //交易份额=券面总额(万元)*10000
//        js里已转换
//        entry.setShareAmt(ArithmeticUtil.multiply(dto.getShareAmt(),new BigDecimal(10000)));
        //交易本金，券面 = 交易份额
        entry.setPrinAmt(entry.getShareAmt());
        //重置交易金额为0
        entry.setTradeAmt(BigDecimal.ZERO);
        //到期收益率
        entry.setYield( ArithmeticUtil.divide(dto.getmYield(), new BigDecimal(100), 8, RoundingMode.HALF_UP));
        // 是否结清 = 数据字典缓存.是否.是
        entry.setIsClean(DictCodeList.是否_是.getValue());
        //是否撤销= 数据字典缓存.是否.否
        entry.setIsCancel(DictCodeList.是否_否.getValue());
        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 本方交易员
            entry.setTrader(personInfo.getTrader());
            // 交易对手
            entry.setCounterId(personInfo.getCounterId());
            // 是否外部交易
            entry.setIsOutTrade(personInfo.getIsOutTrade());
            // 对手方交易员
            entry.setCounterTrader(personInfo.getCounterTrader());
            // 联系方式
            entry.setCounterContact(personInfo.getContactInfo());
        }
        dto.setTrdProductDeal(entry);
        // 交易编号、融产品代码、金融产品类型（投管分类）、证券管理户代码、关联系统交易编号、投资目的、交易日、
        // 起息日、到期日、交割日、单位净价、单位利息、单位全价、净价总额、交易场所、交易市场、交易平台、交易币种、交易方向、利息总额、全价总额、备注
        // 以上信息为画面上项目，不需要特殊设置
        // 金融产品交易Entity 构造结束----------------------------------------------------------

        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
        trdProductDealAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setTrdProductDealAdd(trdProductDealAdd);
        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------

        // 组合交易表Entity 构造开始----------------------------------------------------------
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiNo(dto.getTradeId()); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getTradeId()); // 业务明细代码
        ptlTrade.setBusiType(DictCodeListExt2.共通业务类型_债券借贷交易.getValue()); // 业务类型
        ptlTrade.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
        ptlTrade.setSettleDate(dto.getTradeDate()); // 实际发生日
        ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        //真实组合代码（子组合代码）--范双虎
        ptlTrade.setPortfolioId(dto.getActPortfolioId());
        dto.setPtlTrade(ptlTrade);
        // 组合交易表Entity 构造结束----------------------------------------------------------

        //金融产品账户信息Entity构造开始--------------------------------------------------------
        StlFinProductAcct stlFinProductAcct=new StlFinProductAcct();
        stlFinProductAcct.setFinprodId(dto.getTradeId());
        stlFinProductAcct.setFinprodType(dto.getFinprodType());
        stlFinProductAcct.setFinprodType2(dto.getFinprodType2());
        stlFinProductAcct.setAcctNo(dto.getAcctRivalId());
        stlFinProductAcct.setCcy(dto.getCcy());
        stlFinProductAcct.setAcctType(DictCodeListExt1.资金账户类型_交易对手账户.getValue());
        dto.setStlFinProductAcct(stlFinProductAcct);
        //金融产品账户信息Entity构造结束---------------------------

        // 交易类金融产品Entity 构造开始----------------------------------------------------
        FinTradeProductDto finTradeProduct = new FinTradeProductDto();
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finTradeProduct.setFinprodId(dto.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finTradeProduct.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券借贷融入/融出
        finTradeProduct.setFinprodType2(dto.getFinprodType2());
        // 名义本金 = 交易份额
        finTradeProduct.setPrin(entry.getShareAmt());
        // 名义币种 = 画面.币种
        finTradeProduct.setCcy(dto.getCcy());
        // 起息日 = 画面.首次结算日
        finTradeProduct.setVdate(dto.getVdate());
        // 到期日 = 画面.到期日结算
        finTradeProduct.setMdate(dto.getMdate());
        // 期限(天) = 画面.期限
        finTradeProduct.setTerm(dto.getTerm());
        // 利率类型 = 数据字典缓存.利率类型.固定利率
        finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
        // 固定利率 = 画面.借贷利率/100，等同于金融产品交易中的到期收益率
        finTradeProduct.setIntRate(entry.getYield());
        // 计息基础 = 固定A/365
        finTradeProduct.setBasis(DictCodeList.计息基础_A_365.getValue());
        // 到期利息 = 画面.借贷费用
        finTradeProduct.setmIntAmt(dto.getmTradeAmt());
        // 到期本金 = 本金(交易份额)
        finTradeProduct.setmPrinAmt(entry.getShareAmt());
        // 到期金额 = 画面.借贷费用 + 本金
        finTradeProduct.setmTradeAmt(ArithmeticUtil.doAdd(finTradeProduct.getmPrinAmt(),dto.getmTradeAmt()));
        // 本金收益特征 = 默认非保本
        finTradeProduct.setCapiIncomeFeature("02");
        //到期结算方式
        finTradeProduct.setmDeliveryType(dto.getmDeliveryType());
        //争议解决方式
        finTradeProduct.setConflictSolveWay(dto.getConflictSolveWay());
        // 交易场所 = 画面.交易场所
        finTradeProduct.setTradeMarket(dto.getTradeMarket());
        // 交易日历 = 画面.交易日历
        finTradeProduct.setCalendarId(dto.getCalendarId());
        // 期限品种 = 画面.期限品种
        finTradeProduct.setTermType(dto.getTermType());
        // 交易对手 = 画面.交易对手
        finTradeProduct.setCounterId(personInfo==null?null:personInfo.getCounterId());
        // 工作流代码
        finTradeProduct.setWorkflowId(dto.getWorkflowId());
        // 持有目的 中债新增 20200409
 		finTradeProduct.setHoldPurpose(dto.getHoldPurpose());
        dto.setFinTradeProduct(finTradeProduct);
        // 交易类金融产品Entity 构造结束----------------------------------------------------------

        // 交易类金融产品附属Entity 构造开始----------------------------------------------------------
        FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
        BeanUtil.copyPropertiesIgnoreNull(finTradeProductAdd, dto.getFinTradeProduct());
        if(StringUtils.isNotBlank(dto.getSupplyClause())){
            finTradeProductAdd.setSupplyClause(dto.getSupplyClause());//补充条款
        }
        if(dto.getBussRepoSec()!=null){
            if(StringUtils.isNotBlank(dto.getBussRepoSec().getExchangePledge())){
                finTradeProductAdd.setExchangePledge(dto.getBussRepoSec().getExchangePledge());//质押劵置换安排
            }
        }
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finTradeProductAdd.setFinprodId(dto.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finTradeProductAdd.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        //  金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finTradeProductAdd.setFinprodType2(dto.getFinprodType2());
        finTradeProductAdd.setFinprodType(dto.getFinprodType2());
        // 登记托管机构
        finTradeProductAdd.setRegistOrg(dto.getRegistOrg());
        dto.setFinTradeProductAdd(finTradeProductAdd);
        // 交易类金融产品附属Entity 构造结束----------------------------------------------------------

        // 金融产品分类Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finProductType.setFinprodId(dto.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finProductType.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_融出.getValue())) {
            finProductType.setType3(DictCodeListExt2.增值税资产分类_债券借贷融出.getValue());
        }
        // 分类6 = 画面.金融产品类型(投管)
        finProductType.setType6(dto.getFinprodType2());
        // 分类7 = 画面.方向
        finProductType.setType7(dto.getPs());
        // 分类8 = 画面.债券借贷类型(暂不存)
        finProductType.setType8(DictCodeListExt2.平安资产类型_债券借贷.getValue());
        /*添加五级分类映射 lishuping 2019/08/13 start*/
        FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
        finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
        finProductTypeConfig.setInputValue1(DictCodeListExt2.平安资产类型_债券借贷.getValue());
        finProductTypeConfig = restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                finProductTypeConfig, FinProductTypeConfig.class);
        finProductType.setType9(finProductTypeConfig.getOutputValue2());
        finProductType.setType10(finProductTypeConfig.getOutputValue3());
        finProductType.setType11(finProductTypeConfig.getOutputValue4());
        finProductType.setType12(finProductTypeConfig.getOutputValue5());
        /*添加五级分类映射 lishuping 2019/08/13 end*/
        // 工作流代码
        finProductType.setWorkflowId(dto.getWorkflowId());
        dto.setFinProductType(finProductType);
        // 金融产品分类Entity 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId()); // 交易编号
        // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
        if (StringUtil.equals(DictCodeList.回购拆借方向_融入.getValue(), dto.getPs())) {
            paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_债券借贷融入.getValue());
        }
        else if (StringUtil.equals(DictCodeList.回购拆借方向_融出.getValue(), dto.getPs())) {
            paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_债券借贷融出.getValue());
        }
        paTrdProductDeal.setBackLendDirection(dto.getPs());//交易方向
        paTrdProductDeal.setDeptCode(personInfo.getTraderDept()); // 交易所属部门
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setPaTrdProductDeal(paTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        // 金融产品交易业务流程其他信息Entity 构造开始----------------------------------------------------------
        WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
        // 交易编号 = 金融产品交易Entity.交易编号
        wflTrdProductDealOth.setTradeId(dto.getTradeId());
        // 计息基础 = A/365
        wflTrdProductDealOth.setBasis(DictCodeList.计息基础_A_365.getValue());
        // 利率 = 画面.借贷费率/100
        wflTrdProductDealOth.setRate(entry.getYield());
        // 期限(天) = 画面.期限(天)
        wflTrdProductDealOth.setTerm(dto.getTerm());
        // 期限品种 = 画面.期限品种
        wflTrdProductDealOth.setTermType(dto.getTermType());
        // 到期金额
        wflTrdProductDealOth.setmTradeAmt(dto.getmTradeAmt());
        //标的金融产品代码
        wflTrdProductDealOth.setPledgeFinprodId(dto.getFinprodId());
        //标的金融产品类型
        wflTrdProductDealOth.setPledgeFinprodType(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        //标的金融产品类型
        wflTrdProductDealOth.setPledgeFaceValue(entry.getShareAmt());
        // 数据状态 = 数据字典缓存.数据状态.新建
        wflTrdProductDealOth.setStatus(DictCodeList.数据状态_新建.getValue());
        // 工作流代码
        wflTrdProductDealOth.setWorkflowId(dto.getWorkflowId());
        dto.setWflTrdProductDealOth(wflTrdProductDealOth);
        // 金融产品交易业务流程其他信息Entity 构造结束----------------------------------------------------------
    }

    /**
     * 保存时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdBondLendInvestTradeDto entity, Boolean isUpdate) {
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

        // 调用授信共同 sjj start
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity.getTrdProductDeal());
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        inputDto.setTrdProductDeal(trdProductDeal);
        AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
        // 调用授信共同 sjj end

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity.getTrdProductDeal());
        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
        wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
        wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易Entity -> 金融产品交易其它业务流程表
        WflTrdProductDealOth wflPaTrdProductDealOth = new WflTrdProductDealOth();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDealOth, entity.getWflTrdProductDealOth());
        wflPaTrdProductDealOth.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        TrdCommonWflParam trdCommonWflParam = new TrdCommonWflParam();
        trdCommonWflParam.setWflTrdProductDeals(Arrays.asList(wflTrdProductDeal));
        trdCommonWflParam.setWflDealAdds(Arrays.asList(wflTrdProductDealAdd));
        trdCommonWflParam.setWflPaDeals(Arrays.asList(wflPaTrdProductDeal));
        trdCommonWflParam.setWflTrdProductDealOth(Arrays.asList(wflPaTrdProductDealOth));
        trdCommonWflParam.setWorkflowId(entity.getWorkflowId());
        trdCommonWflParam.setUpdate(isUpdate);

        // 批量插入
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondLendInvestTrade/insertBatch",
                trdCommonWflParam, String.class);

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        /*if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                entity.getBussTrdTransferOrder().get(0));
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

            bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class, wflStlPayOrders);
        }*/

        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(entity.getTrdProductDeal());
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
        baseTradeProductDealDto.setBussRepoSec(entity.getBussRepoSec());
        baseTradeProductDealDto.setSecLendFinprodId(entity.getFinprodId());
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            baseTradeProductDealDto.setStlPayOrdersList(
                Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
        }
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
        //设置母组合代码和子组合代码--范双虎
//        entity.setActPortfolioId(entity.getPortfolioId());
//        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        //        委托代码不为空处理委托代码
        finProductTradeDto.getBaseTradeProductDealDtos().forEach(item->{
            String entrustId = StringUtil.isNotEmpty(entity.getEntrustId())?entity.getEntrustId():item.getTrdProductDealAdd()!=null?item.getTrdProductDealAdd().getEntrustId():null;
            if(StringUtil.isEmpty(item.getEntrustId())&&StringUtil.isNotEmpty(entrustId)){
                item.setEntrustId(entrustId);
            }
        });

        // 撤销时，子组合需要调估值共通
        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }
        baseTradeProductDealCommService.execute(finProductTradeDto);
        //子组合插入流水
        if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getActPortfolioId(), entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
    }


    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondLendInvestTrade/deleteBussFlowData",
                workflowId, String.class);
    }
    /**
     * 写入金融产品标的信息
     *
     * @param input 画面参数Dto
     * @see
     * @since 1.0
     */
    private FinDealTarget finDealTargetLogic(TrdProductDeal input,String finprodId) {
        FinDealTarget finDealTarget = new FinDealTarget();
        //金融产品代码=交易编号
        finDealTarget.setFinprodId(input.getTradeId());
        //0，与估值系统保持一致
        finDealTarget.setBranch(input.getBranch());
        //标的券的金融产品代码
        finDealTarget.setPledgeFinprodId(finprodId);
        //标的券面总额
        finDealTarget.setPledgeFaceValue(input.getShareAmt());

        return finDealTarget;
    }

    /**
     * 写入现金流相关信息表
     *
     * @param input 画面参数Dto
     * @see
     * @since 1.0
     */
    private BaseFinProductCashInputDto cashFlowLogic(TrdBondLendInvestTradeDto input) {
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);

        BaseFinProductCashInputDto inputDto = new BaseFinProductCashInputDto();

        // List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        // 现金流信息Entity-本金
        FinCashInfo finCashInfoPrin = new FinCashInfo();
        // 现金流代码 = 调用【各种业务共通组件.现金流代码生成】生成现金流代码。输入参数如下
        // 业务代码 = 金融产品交易Entity.交易编号
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        // 现金流信息Entity-利息
        FinCashInfo finCashInfoInt = new FinCashInfo();
        // 现金流代码 = 调用【各种业务共通组件.现金流代码生成】生成现金流代码。输入参数如下
        // 业务代码 = 金融产品交易Entity.交易编号
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
        finCashInfoInt.setCashId(commonBusinessService.generateCashFlowCode(input.getTradeId(),
            DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
            DictCodeListExt1.现金流二级类型_利息.getValue()));
        // 现金流一级类型 = 数据字典缓存.现金流一级类型.利息
        finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
        finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        // 币种 = 画面.币种
        finCashInfoInt.setCcy(input.getCcy());
        // 业务代码 = 金融产品交易Entity.交易编号
        finCashInfoInt.setFinprodId(input.getTradeId());
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashInfoInt.setBranch(branchLong);
        // 工作流代码
        finCashInfoInt.setWorkflowId(input.getWorkflowId());
        finCashInfoList.add(finCashInfoInt);

        inputDto.setFinCashInfoList(finCashInfoList);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        // 现金流支付规则表Entity-利息
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPayRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashPayRule.setEffDate(input.getVdate());
        // 支付频率 = 数据字典缓存.支付频率(付息频率).到期支付

        finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());

        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());

        // 首次支付日 = 画面.到期日
        finCashPayRule.setFirstPayDate(input.getMdate());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPayRule.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPayRule.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPayRule.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashPayRule.setBranch(branchLong);
        // 工作流代码
        finCashPayRule.setWorkflowId(input.getWorkflowId());
        finCashPayRuleList.add(finCashPayRule);

        inputDto.setFinCashPayRuleList(finCashPayRuleList);

        // List<现金流计算规则表Entity>
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();

        // 现金流计算规则表Entity-利息
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashCalcRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashCalcRule.setEffDate(input.getVdate());
        // 计算类型 = 数据字典缓存.利率类型.固定利率
        finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
        // 基数类型 = 数据字典缓存.基数类型.本金
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        // 基数日期类型 = 数据字典缓存.基数日期类型.当日
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        // 计算基础 = 画面.计息基础
        finCashCalcRule.setBasis(DictCodeList.计息基础_A_365.getValue());
        // 生效利率 = 画面.借贷费率/100
        finCashCalcRule.setYield(ArithmeticUtil.divide(input.getmYield(), new BigDecimal(100), 8, RoundingMode.HALF_UP));
        // 是否初始利率 = 数据字典缓存.是否.是
        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashCalcRule.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashCalcRule.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashCalcRule.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashCalcRule.setBranch(branchLong);
        // 工作流代码
        finCashCalcRule.setWorkflowId(input.getWorkflowId());
        finCashCalcRuleList.add(finCashCalcRule);

        inputDto.setFinCashCalcRuleList(finCashCalcRuleList);

        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();

        // 现金流利率信息表Entity-利息
        FinRateInfo finRateInfo = new FinRateInfo();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finRateInfo.setCashId(finCashInfoInt.getCashId());
        // 生效日 = 画面.起息日
        finRateInfo.setEffDate(input.getVdate());
        // 利率类型 = 数据字典缓存.利率类型.固定利率
        // 一体化对应etliu
        finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
        // 计算基础 = 画面.计息基础
        finRateInfo.setBasis(DictCodeList.计息基础_A_365.getValue());
        // 初始利率 = 画面.回购利率/100
        finRateInfo.setRate(finCashCalcRule.getYield());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finRateInfo.setFinprodId(input.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finRateInfo.setBranch(branchLong);
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finRateInfo.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finRateInfo.setFinprodType2(input.getFinprodType2());
        // 工作流代码
        finRateInfo.setWorkflowId(input.getWorkflowId());
        finRateInfoList.add(finRateInfo);

        inputDto.setFinRateInfoList(finRateInfoList);

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();

        // 现金流计划表Entity 到期付息
        FinCashPlan finCashPlan = new FinCashPlan();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPlan.setCashId(finCashInfoInt.getCashId());
        // 现金流序号 = 2
        finCashPlan.setCashNum(2L);
        // 现金流类型 = 数据字典缓存.现金流类型.到期付息
        finCashPlan.setCashType(DictCodeList.现金流类型_到期收息.getValue());
        // 未调整的计算开始日 = 画面.起息日
        finCashPlan.setVdateUnadjust(input.getVdate());
        // 未调整的计算开始日 = 画面.到期日
        finCashPlan.setMdateUnadjust(input.getMdate());
        // 未调整的计划支付日 = 画面.到期日
        finCashPlan.setPayDateUnadjust(input.getMdate());
        // 调整后的计算开始日 = 画面.起息日
        finCashPlan.setVdate(input.getVdate());
        // 调整后的计算结束日 = 画面.到期日
        finCashPlan.setMdate(input.getMdate());
        // 调整后的计划支付日 = 画面.到期日
        finCashPlan.setPayDate(input.getMdate());
        // 计算周期天数 = 画面.到期日-画面.起息日
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(input.getVdate(), input.getMdate()));
        // 计划支付本金 = 0
        finCashPlan.setPrinAmt(BigDecimal.ZERO);
        // 计息本金 = 画面.券面总额
//        finCashPlan.setIntPrinAmt(ArithmeticUtil.multiply(input.getShareAmt(),new BigDecimal(10000)));
        finCashPlan.setIntPrinAmt(input.getShareAmt());
        // 计划支付利息 = 借贷费用
        finCashPlan.setIntAmt(input.getmTradeAmt());
        // 是否支付利息 数据字典缓存.是否.是
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        // 计划支付现金流 = 借贷费用
        finCashPlan.setCashAmt(finCashPlan.getIntAmt());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPlan.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPlan.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券借贷融入与融出
        finCashPlan.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 固定值，系统参数
        finCashPlan.setBranch(branchLong);
        // 支付方式 = 数据字典缓存.分红方式.现金分红
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        // 工作流代码
        finCashPlan.setWorkflowId(input.getWorkflowId());
        finCashPlanList.add(finCashPlan);

        inputDto.setFinCashPlanList(finCashPlanList);

        // 调用共通登录现金流相关数据
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/common/trdcommonbase/insertFinProductCashData",
            inputDto, Boolean.class);

		return inputDto;
    }
    /**
     * 7.5写入交易信息进交易报送信息表，为中债报送用
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_5(TrdBondLendInvestTradeDto dto) {
        // 1)查询产品登记编码
        String prodRegistCode = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/"
                                   + dto.getPortfolioId(),
            null, String.class);

        if (StringUtil.isBlank(prodRegistCode)) {
            return;
        }

        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode); // 产品登记编码
        repTrade.setRepBankTradeCode(dto.getTradeId()); // 行内交易编码
        // 行内资产/负债编码
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
        if (finProduct != null) {
            repTrade.setRepBankAssetCode(finProduct.getFinprodMarketId());
        }
        repTrade.setRepCcy(dto.getCcy()); // 币种
        repTrade.setRepOccurMoney(dto.getFpriceAmt()); // 发生金额

        // 折算人民币金额（元） = 画面.全价总额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(dto.getCcy(),
            DateUtil.format(dto.getTradeDate()));
        BigDecimal 折算人民币金额 = ArithmeticUtil.multiply(dto.getFpriceAmt(), 收盘价);
        repTrade.setRepMoneyCny(折算人民币金额);
        // 数量 = 画面.券面总额
//        repTrade.setRepCount(ArithmeticUtil.multiply(dto.getShareAmt(),new BigDecimal(10000)));
        repTrade.setRepCount((dto.getShareAmt()));

        // 资金流动类型: 数据字典缓存.资金流动类型 ※根据交易方向判断，若买入，即资产买入，若卖出，即为资产卖出或到期
        String 资金流动类型 = DictCodeListExt2.资金流动类型_资产卖出或到期.getValue();
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_融入.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_资产买入.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);

        repTrade.setRepTradeDate(dto.getTradeDate()); // 交易日
        repTrade.setRepTradeComponent(dto.getFinTradeProduct().getCounterId()); // 交易对手方
        repTrade.setRepUnitPriceFull(dto.getUnitFprice()); // 单位成交价格（全价）
        repTrade.setRepUnitPriceNet(dto.getUnitCprice()); // 单位成交价格（净价）
        repTrade.setRepExpireYield(dto.getmYield().multiply(new BigDecimal(100))); // 到期收益率%
        if (StringUtil.isNotEmpty(dto.getfTradeId())) {
            repTrade.setRepDelCode(dto.getfTradeId()); // 成交编号/合同号
        } else {
            repTrade.setRepDelCode(dto.getTradeId()); // 成交编号/合同号
        }
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        repTrade.setRepStatusDate(iPlatformDate.getSysDate()); // 数据日期
        repTrade.setRepRemark(dto.getRemark()); // 备注

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
            Arrays.asList(repTrade), Boolean.class);
    }

    /**
     * 7.6插入金融衍生品报送信息
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private RepAssetDeri insertMonetaryDerivative_7_6(TrdProductDeal dto) {
        RepAssetDeri repAssetDeri = new RepAssetDeri();
        repAssetDeri.setRepFinprodId(dto.getTradeId());
        repAssetDeri.setRepNomCapi(dto.getShareAmt());
        repAssetDeri.setRepDeriType(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return repAssetDeri;
    }

    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(TrdBondLendInvestTradeDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            TrdCommonWflParam trdCommonWflParam = new TrdCommonWflParam();
            trdCommonWflParam.setWflTrdProductDeals(new ArrayList<>());
            trdCommonWflParam.setWflDealAdds(new ArrayList<>());
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
                wflTrdProductDeal.setTradeId(entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                if(StringUtil.isNotEmpty(baseDto.getActPortfolioId())) {
                    wflTrdProductDealAdd.setPortfolioId(baseDto.getActPortfolioId());
                }
                trdCommonWflParam.getWflTrdProductDeals().add(wflTrdProductDeal);
                trdCommonWflParam.getWflDealAdds().add(wflTrdProductDealAdd);
            }
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondLendInvestTrade/doRevokeInsert",
                    trdCommonWflParam, String.class);
        }
    }

    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(TrdBondLendInvestTradeDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        // 提前查询判断  是否  撤销 并且是 有转仓交易 并且转仓交易方向ps为买入
        String reverseTradeId = "";
        TrdProductDeal reverseTrdProductDeal = trdCommonBaseService.selectZCDealByTid(entity.getTradeId(),false);
        if (reverseTrdProductDeal != null && StringUtil.equals(DictCodeList.交易方向_买入.getValue(), reverseTrdProductDeal.getPs())) {
            reverseTradeId = reverseTrdProductDeal.getTradeId();
        }

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                baseDto.setBussRepoSec(entity.getBussRepoSec());
                baseDto.setSecLendFinprodId(entity.getFinprodId());
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(),
                        PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdProductDeal.setCreateTime(new Date());
                    paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                    paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());

                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);

                    PtlTrade ptlTrade = new PtlTrade();
                    ptlTrade.setPortfolioId(entity.getPortfolioId());
                    ptlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
                    ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                    ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade, PtlTrade.class);
                    ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    ptlTradeMapper.insertSelective(ptlTrade);
                    if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        ptlTrade.setPortfolioId(entity.getActPortfolioId());
                        ptlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
                        PtlTrade ptlTrade1 = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade, PtlTrade.class);
                        ptlTrade1.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        ptlTradeMapper.insertSelective(ptlTrade1);
                    }
                }
                // 调用投管接口 add by cxq 20190219 start
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdBondLendInvestTrade002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setRevokeReason(entity.getRevokeReason());
                investInterfCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    investInterfCommDto.setStlPayOrders(baseDto.getStlPayOrdersList().get(0));
                }
                investInterfCommDto.setPledgeBondListDetailed(entity.getBussRepoSec().getPledgeBondListDetailed());
                investInterfCommService.doInvestInterf(investInterfCommDto);
                // 调用投管接口 add by cxq 20190219 end

                //删除授信占用
                if (null != baseDto.getTrdProductDeal().getTradeId() && !"".equals(baseDto.getTrdProductDeal().getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(baseDto.getTrdProductDeal().getTradeId());
                }
            }

            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);
            //        委托代码不为空处理委托代码
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(item->{
                String entrustId = StringUtil.isNotEmpty(entity.getEntrustId())?entity.getEntrustId():item.getTrdProductDealAdd()!=null?item.getTrdProductDealAdd().getEntrustId():null;
                if(StringUtil.isEmpty(item.getEntrustId())&&StringUtil.isNotEmpty(entrustId)){
                    item.setEntrustId(entrustId);
                }
            });

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

        // 撤销委托/交易时，删除借贷质押品信息
        // bussRepoSecService.deletePledgeBond(input.getTradeId(), input.getBussRepoSec().getPageFrom());

        // 写入质押品的组合持仓流水
        this.ptlPredgeSecPositionFlowLogic(entity, true);

        // 写入融入券组合持仓流水
        this.ptlBlendSecPositionFlowLogic(entity, true);

        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

        //调用基础组件.【交易报送组件】删除交易报送信息
        restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
            Arrays.asList(entity.getTradeId()), Boolean.class);

        if(StringUtil.isNotEmpty(reverseTradeId)){

            TrdProductDeal trdProductDeal = new TrdProductDeal();
            trdProductDeal.setTradeId(reverseTradeId);
            trdProductDeal.setCancelRemark(entity.getCancelRemark());
            commonBusinessService.revokeReverseTrade(trdProductDeal,false);

        }
    }


    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEntrustInfo(String entrustId) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                                          + "/TrdBondLendInvestTrade/getEntrustInfo/" + entrustId,
            null, BaseEtrInvestInfoDto.class);
    }

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param parameters 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAcctId(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId",
            parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String) parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String) parameters.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }


    /**
     * 通道：联想框文本框
     *
     * @param parameters 画面参数
     * @return 通道：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getChannel(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getChannel",
            parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     * 资产代码：下拉框
     *
     * @param parameters 画面参数
     * @return 资产代码：下拉框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> parameters) {
        return restTemplate.postForPage(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondLendInvestTrade/getFinprodId", parameters, SelectOptionDto.class);
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
            return DateUtil.format(tradingDay);
        }

        return null;
    }

    /**
     * 债券借贷投资交易新建画面查看模式查询债券借贷投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券借贷投资交易信息
     * @see
     * @since 1.0
     */
    public TrdBondLendInvestTradeDto getTrdBondLendInvestTradeDto(String tradeId) {
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondLendInvestTrade/getTrdBondLendInvestTradeDto/"
                                          + tradeId,
            null, TrdBondLendInvestTradeDto.class);
    }

    /**
     * 查询产品类账户
     * @param params 画面输入参数
     */
    public List<StlCapacctInfo> getProAccountInfo(Map<String, Object> params) {
        return restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondLendInvestTrade/getProAccountInfo",
            params, StlCapacctInfo.class);
    }

    /**
     * 查询交易类账户
     * @param params 画面输入参数
     */
    public List<StlCapacctInfo> getDealAccountInfo(Map<String, Object> params) {
       return restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondLendInvestTrade/getDealAccountInfo",
            params, StlCapacctInfo.class);
    }

    /**
     * 查询交易对手账户
     * @param params 画面输入参数
     */
    public List<StlCapacctInfo> getCounterCapacctInfo(Map<String, Object> params) {
        return restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondLendInvestTrade/getCounterCapacctInfo",
            params, StlCapacctInfo.class);
    }


    /**
     * 确认提醒
     * 检查交割日是否为节假日，交割日如果是节假日 要提醒
     * 交易方向为买入时校验交易所证券账户（券商存管账户）余额是否足够，如不足，提醒进行银证转账
     *
     * @param inputDto 画面输入参数
     * @param result valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirmInfo(TrdBondLendInvestTradeDto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }

        String confirmMsg = "";
        String changeLineMark = "<br/>";

        // 检查交易日是否为节假日，交易日如果是节假日 要提醒
        if (inputDto.getTradeDate() != null && StringUtil.isNotBlank(inputDto.getCalendarId())) {
            boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(
                inputDto.getTradeDate(), inputDto.getCalendarId());
            if (isDealCalendarHoliday) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("BUSS_COM_E_0039", getMessage("TrdBondInvestTrade002.tradeDate"));
            }
        }

        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMsg);
            return wr;
        }

        return null;
    }

	public List<SelectOptionDto> getPortfolioId(Map<String, Object> params) {
		return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getPortfolioId",
				params, SelectOptionDto.class);
	}



    public String getChannelInfo(String parameters) {
        String temp = null;
        Map<String, Object> map = new HashMap<>();
        map.put("chlId", parameters);
        List<String> tempList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getChannelInfo", map, String.class);
        if (!StringUtil.isEmptyList(tempList)) {
            temp = tempList.get(0);

        }
        return temp;
     }

    /**
     * 写入融入券的组合持仓流水
     * @param input    画面参数Dto
     * @param isNegate 是否冲销
     */
    private boolean ptlBlendSecPositionFlowLogic(TrdBondLendInvestTradeDto input, boolean isNegate) {
        List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
        // 冲销时（撤销）
        if (isNegate) {
            // 根据交易编号检索组合持仓流水（持仓类型为实际）
            List<PtlSecPositionFlow> lstReal = this.selectPtlSecPositionFlow(input.getTradeId(),false);
            if (!StringUtil.isEmptyList(lstReal)) {
                PtlSecPositionFlowDto resultDto;
                for (PtlSecPositionFlow ptlSecPositionFlow : lstReal) {
                    resultDto = new PtlSecPositionFlowDto();
                    // 组合代码
                    resultDto.setPortfolioId(ptlSecPositionFlow.getPortfolioId());
                    // 证券管理账户/通道代码
                    resultDto.setSecAcctId(ptlSecPositionFlow.getSecAcctId());
                    // 金融产品代码
                    resultDto.setFinprodId(ptlSecPositionFlow.getFinprodId());
                    // 分支序号
                    resultDto.setBranch(ptlSecPositionFlow.getBranch());
                    // 持仓类型
                    resultDto.setHodingType(ptlSecPositionFlow.getHodingType());
                    // 日期
                    resultDto.setCdate(ptlSecPositionFlow.getCdate());
                    // 数量
                    resultDto.setAmount(ptlSecPositionFlow.getAmount().negate());
                    // 币种
                    resultDto.setCcy(ptlSecPositionFlow.getCcy());
                    // 交易编号
                    resultDto.setTradeId(ptlSecPositionFlow.getTradeId());
                    // 投资目的
                    resultDto.setInvAim(ptlSecPositionFlow.getInvAim());

                    lstPtlSecPositionFlowDto.add(resultDto);
                }
            }
        } else {
            // 通过画面选择的标的债券进行融入融出

            if (DictCodeList.回购拆借方向_融出.getValue().equals(input.getPs())) {
                // 可融入券信息（含投资目的）
                Map<String, List<PledgeBondInfoDto>> avaMap = this.selectAvaPledgeWithInvAim(input, false)
                    .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
                avaMap.entrySet().stream().forEach(entry -> {
                    Collections.sort(entry.getValue(), new Comparator<PledgeBondInfoDto>() {
                        public int compare(PledgeBondInfoDto o1, PledgeBondInfoDto o2) {
                            return o1.getInvAimSort().compareTo(o2.getInvAimSort());
                        }
                    });
                });

                // 待分配投资目的的融入券面总额
                BigDecimal blendPrin = input.getTrdProductDeal().getShareAmt();
                FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, input.getFinprodId(), FinProduct.class);
                if (avaMap.size() > 0&&null!=avaMap.get(finProduct.getFinprodMarketId())){
                    for (PledgeBondInfoDto avaBond : avaMap.get(finProduct.getFinprodMarketId())) {
                        PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
                        // 当前投资目的可分配券面总额
                        BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                        // 当前投资目的决定分配的券面总额
                        BigDecimal prin = blendPrin.compareTo(avaPrin) > 0 ? avaPrin : blendPrin;

                        // 组合代码 = 画面.组合代码
                        resultDto.setPortfolioId(input.getPortfolioId());
                        // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
                        resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecManageAcctId()) ? "999999" : input.getSecManageAcctId());
                        // 金融产品代码 = 输出质押品DTO.金融产品代码
                        resultDto.setFinprodId(input.getFinprodId());
                        // 分支序号 = 系统固定参数，默认为0
                        resultDto.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                        // 持仓类型
                        resultDto.setHodingType(DictCodeList.回购拆借方向_融出.getValue());
                        // 日期 = 金融产品交易Entity.交易日
                        resultDto.setCdate(input.getTradeDate());
                        // 数量 = 画面.券面总额
                        resultDto.setAmount(com.joyintech.framework.util.ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                        // 币种 = 金融产品交易Entity.币种
                        resultDto.setCcy(input.getCcy());
                        // 交易编号 = 金融产品交易Entity.交易编号
                        resultDto.setTradeId(input.getTradeId());
                        // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                        resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                        if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
                            lstPtlSecPositionFlowDto.add(resultDto);
                            if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
                                PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
                                BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
                                subResultDto.setPortfolioId(input.getActPortfolioId());
                                lstPtlSecPositionFlowDto.add(subResultDto);
                            }
                        }
                        // 分配完毕后,停止可融入券信息（含投资目的）的循环
                        blendPrin = blendPrin.subtract(prin);
                        if (blendPrin.compareTo(BigDecimal.ZERO) <= 0) {
                            break;
                        }
                    }
                }
            }else{
                PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
                // 组合代码 = 画面.组合代码
                resultDto.setPortfolioId(input.getPortfolioId());
                // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
                resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecManageAcctId()) ? "999999" : input.getSecManageAcctId());
                // 金融产品代码 = 输出质押品DTO.金融产品代码
                resultDto.setFinprodId(input.getFinprodId());
                // 分支序号 = 系统固定参数，默认为0
                resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                // 持仓类型
                resultDto.setHodingType(DictCodeList.持仓类型_融入.getValue());
                // 日期 = 金融产品交易Entity.交易日
                resultDto.setCdate(input.getTradeDate());
                // 数量 = 输出券面总额
                resultDto.setAmount(com.joyintech.framework.util.ArithmeticUtil.divide(input.getTrdProductDeal().getShareAmt(), new BigDecimal("100"), 4));
                // 币种 = 金融产品交易Entity.币种
                resultDto.setCcy(input.getCcy());
                // 交易编号 = 金融产品交易Entity.交易编号
                resultDto.setTradeId(input.getTradeId());
                // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                resultDto.setInvAim("999999");
                lstPtlSecPositionFlowDto.add(resultDto);
                if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
                    PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
                    subResultDto.setPortfolioId(input.getActPortfolioId());
                    lstPtlSecPositionFlowDto.add(subResultDto);
                }
            }
        }
        if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
            return true;
        } else {
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
            return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                ptlPositionHoldDto, Boolean.class);
        }
    }

    /**
     * 写入质押品的组合持仓流水
     * @param input    画面参数Dto
     * @param isNegate 是否冲销
     */
    private boolean ptlPredgeSecPositionFlowLogic(TrdBondLendInvestTradeDto input, boolean isNegate) {
        List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
        // 冲销时（撤销）
        if (isNegate) {
            // 根据交易编号检索组合持仓流水（持仓类型为实际）
            List<PtlSecPositionFlow> lstReal = this.selectPtlSecPositionFlow(input.getTradeId(),true);
            if (!StringUtil.isEmptyList(lstReal)) {
                PtlSecPositionFlowDto resultDto;
                for (PtlSecPositionFlow ptlSecPositionFlow : lstReal) {
                    resultDto = new PtlSecPositionFlowDto();
                    // 组合代码
                    resultDto.setPortfolioId(ptlSecPositionFlow.getPortfolioId());
                    // 证券管理账户/通道代码
                    resultDto.setSecAcctId(ptlSecPositionFlow.getSecAcctId());
                    // 金融产品代码
                    resultDto.setFinprodId(ptlSecPositionFlow.getFinprodId());
                    // 分支序号
                    resultDto.setBranch(ptlSecPositionFlow.getBranch());
                    // 持仓类型
                    resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                    // 日期
                    resultDto.setCdate(ptlSecPositionFlow.getCdate());
                    // 数量
                    resultDto.setAmount(ptlSecPositionFlow.getAmount().negate());
                    // 币种
                    resultDto.setCcy(ptlSecPositionFlow.getCcy());
                    // 交易编号
                    resultDto.setTradeId(ptlSecPositionFlow.getTradeId());
                    // 投资目的
                    resultDto.setInvAim(ptlSecPositionFlow.getInvAim());

                    lstPtlSecPositionFlowDto.add(resultDto);
                }
            }
        } else {
            // List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
            // 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
            if (!StringUtil.isEmptyList(input.getBussRepoSec().getPledgeBondList())
                && DictCodeList.回购拆借方向_融入.getValue().equals(input.getPs())) {
                // 可质押券信息（含投资目的）
                Map<String, List<PledgeBondInfoDto>> avaMap = this.selectAvaPledgeWithInvAim(input,true)
                    .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
                    avaMap.entrySet().stream().forEach(entry -> {
                    Collections.sort(entry.getValue(), new Comparator<PledgeBondInfoDto>() {
                        public int compare(PledgeBondInfoDto o1, PledgeBondInfoDto o2) {
                            return o1.getInvAimSort().compareTo(o2.getInvAimSort());
                        }
                    });
                });

                for (PledgeBondInfoDto pledgeBond : input.getBussRepoSec().getPledgeBondList()) {
                    // 待分配投资目的的质押券面总额
                    BigDecimal pledgePrin = pledgeBond.getPledgePrin();
                    if(avaMap.get(pledgeBond.getBondCode())!=null) {
                        for (PledgeBondInfoDto avaBond : avaMap.get(pledgeBond.getBondCode())) {
                            PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();

                            // 当前投资目的可分配券面总额
                            BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                            // 当前投资目的决定分配的券面总额
                            BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                            // 组合代码 = 画面.组合代码
                            resultDto.setPortfolioId(input.getPortfolioId());
                            // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
                            resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecManageAcctId()) ? "999999" : input.getSecManageAcctId());
                            // 金融产品代码 = 输出质押品DTO.金融产品代码
                            resultDto.setFinprodId(avaBond.getFinprodId());
                            // 分支序号 = 系统固定参数，默认为0
                            resultDto.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                            // 持仓类型
                            resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                            // 日期 = 金融产品交易Entity.交易日
                            resultDto.setCdate(input.getTradeDate());
                            // 数量 = 输出质押品DTO.质押数量
                            resultDto.setAmount(com.joyintech.framework.util.ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                            // 币种 = 金融产品交易Entity.币种
                            resultDto.setCcy(input.getCcy());
                            // 交易编号 = 金融产品交易Entity.交易编号
                            resultDto.setTradeId(input.getTradeId());
                            // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                            resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                            if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
                                if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
                                    PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
                                    BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
                                    subResultDto.setPortfolioId(input.getActPortfolioId());
                                    lstPtlSecPositionFlowDto.add(subResultDto);
                                }
                                lstPtlSecPositionFlowDto.add(resultDto);
                            }
                            // 分配完毕后,停止可质押券信息（含投资目的）的循环
                            pledgePrin = pledgePrin.subtract(prin);
                            if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
            return true;
        } else {
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
            if (ptlPositionHoldDto != null) {
                if (ptlPositionHoldDto.getPtlSecPositionFlows() != null
                        && !ptlPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
                    for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
                        basePtlSecPositionInfoServiceImpl.inertSecPositionFlow(ptlSecPositionFlow);
                    }
                }
                if (ptlPositionHoldDto.getPtlCashPositionFlows() != null
                        && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
                    for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
                        basePtlCashPositionInfoServiceImpl.insertCashPositionFlow(ptlCashPositionFlow);
                    }
                }
            }
            return true;
        }
    }

    /**
     * 写入融出券的组合持仓流水
     * ZGBSSE-122 实时头寸查询，2019-5-30，债券借贷融出，交易可用持仓
     * @param input    画面参数Dto
     * @param isNegate 是否冲销
     */
    private boolean ptlOutSecPositionFlowLogic(TrdBondLendInvestTradeDto input, boolean isNegate, boolean isDoBack,boolean isDoComplete) {
        List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
        PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
        // 组合代码
        resultDto.setPortfolioId(input.getPortfolioId());
        // 证券管理账户/通道代码
        String secAcctId = input.getSecManageAcctId();
        if ((StringUtil.isEmpty(secAcctId) || "999999".equals(secAcctId)) && StringUtil.isNotEmpty(input.getChlId())) {
            secAcctId = input.getChlId();
        }
        resultDto.setSecAcctId(StringUtil.isEmpty(secAcctId) ? "999999" : secAcctId);
        // 金融产品代码
        resultDto.setFinprodId(input.getFinprodId());
        // 分支序号
        resultDto.setBranch(0L);
        // 持仓类型
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue(), input.getFinprodType2())) {
            resultDto.setHodingType(DictCodeListExt1.持仓类型_融入.getValue());
        } else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), input.getFinprodType2())) {
            if (isNegate) {
                resultDto.setHodingType(DictCodeListExt1.持仓类型_融出.getValue());
            } else {
                resultDto.setHodingType(DictCodeListExt1.持仓类型_借贷融出.getValue());
            }
        }
        // 日期
        resultDto.setCdate(input.getTradeDate());
        // 数量
        BigDecimal shareAmt = input.getShareAmt().divide(new BigDecimal(100));
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), input.getFinprodType2())) {
            resultDto.setAmount(isNegate ? shareAmt.negate() : shareAmt);// 撤销及退回 取反
        } else {
            resultDto.setAmount(isNegate ? shareAmt.negate() : shareAmt);
        }
        // 币种
        resultDto.setCcy(input.getCcy());
        // 交易编号
        resultDto.setTradeId(input.getTradeId());
        // 投资目的
        resultDto.setInvAim(StringUtil.isEmpty(input.getInvAim()) ? "999999" : input.getInvAim());
        // 融出交易流程最后一步
        if (!isNegate && (isDoComplete||isDoBack) && StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), input.getFinprodType2())) {
            // modify by jiangpeng on 20210506 start
            // ZGBSS-1150 【债券借贷存续期】：融出到期交易和撤销融入到期交易产生的错误
            // 融出交易生效时已入库持仓流水,此处不用在重复入库,只需冲销掉提交流程时入的流程中流水
//            PtlSecPositionFlowDto ptlSecPositionFlowDtoTemp = new PtlSecPositionFlowDto();
//            BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTemp, resultDto);
            resultDto.setAmount(resultDto.getAmount().negate());// 冲销掉流程提交时的持仓数据数据
//            ptlSecPositionFlowDtoTemp.setHodingType(DictCodeListExt1.持仓类型_融出.getValue());
//            lstPtlSecPositionFlowDto.add(ptlSecPositionFlowDtoTemp);
            // modify by jiangpeng on 20210506 start
        }
        lstPtlSecPositionFlowDto.add(resultDto);
        if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
            return true;
        } else {
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
            return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                    ptlPositionHoldDto, Boolean.class);
        }
    }

    /**
     * 写入融出券的组合持仓流水(持仓类型为借贷融入/借贷融出)
     * @author jiangpeng
     * ZGBSSE-122 实时头寸查询，2019-5-30，债券借贷融出，交易可用持仓
     * @param bizCode 交易编号
     * @return List<PtlSecPositionFlowDto>
     * @see
     * @since 1.0
     */
    public List<PtlSecPositionFlow> selectPtlSecPositionFlowForSL(String bizCode) {
        return restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/selectPtlSecPositionFlowForSL/" + bizCode,
                null, PtlSecPositionFlow.class);
    }

    /**
     * 组合可用持仓和实际持仓查询更新组件接口
     */
    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl;

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoServiceImpl;

    /**
     * 根据交易编号检索组合持仓流水（持仓类型为实际）
     *
     * @param bizCode 交易编号
     * @return List<PtlSecPositionFlowDto>
     * @see
     * @since 1.0
     */
    public List<PtlSecPositionFlow> selectPtlSecPositionFlow(String bizCode,boolean plegFlag) {
        if(plegFlag) {
            return restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/selectPtlSecPositionFlow/" + bizCode,
                null, PtlSecPositionFlow.class);
        }
        else{
            return restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/selectPtlSecPositionFlow/" + bizCode,
                null, PtlSecPositionFlow.class);
        }
    }

    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(TrdBondLendInvestTradeDto input,boolean pledge) {
        Map<String, Object> param = new HashMap<>();
        if(StringUtil.isNotEmpty(input.getActPortfolioId())) {//motify by jiangpeng on 20210402 ZGBSS-1114 新建保存债券借贷交易，报异常提醒。
            param.put("portfolioId", input.getActPortfolioId());
        }else{
            param.put("portfolioId", input.getPortfolioId());
        }
        param.put("ps", input.getPs());
        param.put("finprodId", input.getFinprodId());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        if(pledge) {//质押券逻辑
            return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectAvaPledgeBondDetailList",
                param, PledgeBondInfoDto.class);
        }else{//融入券逻辑
            return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussBlendSec/selectAvaBlendBondDetailList",
                param, PledgeBondInfoDto.class);
        }
    }

    /***
     * 获取当前标的登记托管机构
     * @param finprodId
     * @return
     */
    private String getRegisOrgInfo(String finprodId){
        FinProduct retFinBondInfo = restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME
                + "/FinBondTargetAsset/getBasicInfo?finprodId="
                + finprodId,
            FinProduct.class);

        return  StringUtil.isNotEmpty(retFinBondInfo)?retFinBondInfo.getRegistOrg():null;
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
        TrdBondLendInvestTradeDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
//
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), ArithmeticUtil.multiply(dto.getShareAmt(),new BigDecimal(10000)), LoginUserUtil.getUserName(),
//                      DictCodeListExt2.平安资产类型_债券借贷.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  start
//                      , dto.getFinprodId()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  end
//                      );
              Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getShareAmt(), LoginUserUtil.getUserName(),
                  DictCodeListExt2.平安资产类型_债券借贷.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
                  , dto.getFinprodId(),entity.getWorkflowId());
              params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doBack(TrdBondLendInvestTradeDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
        // add by jiangpeng on 20210428 start
        // ZGBSSE-122 实时头寸查询，2019-5-30，债券借贷融出，交易可用持仓
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), entity.getFinprodType2())) {
            ptlOutSecPositionFlowLogic(entity, false, true,false);
        }
        // add by jiangpeng on 20210428 end
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doRevoke(TrdBondLendInvestTradeDto entity) {
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }
}
