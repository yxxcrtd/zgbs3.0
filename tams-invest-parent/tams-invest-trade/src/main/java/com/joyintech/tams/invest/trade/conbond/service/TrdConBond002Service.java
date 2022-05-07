/*
 * 文件名：TrdConBond002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangpeng
 * 修改时间：2019年11月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.conbond.service;


import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.system.base.dao.SysUploadFileInfoMapper;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.entrust.dto.BaseEtrFinishAmtDto;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.trade.conbond.dto.TrdConBond003Dto;
import com.joyintech.tams.invest.trade.conbond.dto.TrdConBondDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.ExchangeTransMatchCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.POSITION_TYPE_ETR;

/**
 * 可转债申购画面Service
 *
 * @author wangpeng
 * @version 1.0 2019年11月14日
 * @see TrdConBond002Service
 * @since 1.0
 */
@Service
public class TrdConBond002Service extends BaseService<TrdConBondDto> implements BaseProcessService<TrdConBondDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdConBond002Service.class);

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
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by renping 20190205 end
    
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;


    /**
     * MultiApproveTradeCheckService
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;
    @Autowired(required = false)
    private SysUploadFileInfoMapper sysUploadFileInfoMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;
    
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    
    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
    
    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    
    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    
    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;

    @Autowired
    private ExchangeTransMatchCommonService exchangeTransMatchCommonService;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    @Autowired
    private CommonPositionEngineService commonPositionEngineService;

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

    /**
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(TrdConBondDto dto) {
    	 dto.setActPortfolioId(dto.getPortfolioId());
         dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));	
    	// 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        dto.setBranch(branchLong);
        // 是否结清 = 数据字典缓存.是否.是
        dto.setIsClean(DictCodeList.是否_是.getValue());
        dto.setIsCancel(DictCodeList.是否_否.getValue());

        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 本方交易员
            dto.setTrader(personInfo.getTrader());
            // 交易对手
            dto.setCounterId(personInfo.getCounterId());
            // 是否外部交易
//            dto.setIsOutTrade(personInfo.getIsOutTrade());
            if(StringUtil.equals(DictCodeList.是否_否.getValue(), personInfo.getIsOutTrade())){
            	dto.setIsOutTrade(personInfo.getIsOutTrade());
            }else{
            	dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            }
            // 对手方交易员
            dto.setCounterTrader(personInfo.getCounterTrader());
            // 联系方式
            dto.setCounterContact(personInfo.getContactInfo());
        }

        
        // 金融产品交易汇总表Entity 构造开始----------------------------------------------------------
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, dto);
        trdFinProductDealTotal.setPortfolioId(dto.getActPortfolioId());//存入实际组合信息
        trdFinProductDealTotal.setTotleTradeId(dto.getBusiCode()); // 交易编号
//        trdFinProductDealTotal.setPortfolioId(dto.getPortfolioId()); // 组合信息
        trdFinProductDealTotal.setTradeDate(dto.getTradeDate()); // 画面确认日期=交易日期
        trdFinProductDealTotal.setSettleDate(dto.getSettleDate()); // 画面缴款日期= 交割日
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(dto.getTradeAmt(), new BigDecimal(100),2)); // 	画面.申购券面总额/100 ：交易份额 
        trdFinProductDealTotal.setCpriceAmt(dto.getTradeAmt()); // 	画面.申购券面总额 ：净价总额
        trdFinProductDealTotal.setFpriceAmt(dto.getTradeAmt()); // 	画面.申购券面总额 ：全价总额
        trdFinProductDealTotal.setPrinAmt(dto.getTradeAmt()); // 	画面.申购券面总额 ：交易本金
        trdFinProductDealTotal.setIntAmt(BigDecimal.ZERO); // 	0 ：利息总额
        trdFinProductDealTotal.setTradeAmt(dto.getTradeAmt()); // 画面.申购券面总额：交易金额
        trdFinProductDealTotal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); // 	数据字典.交易市场.一级市场：交易市场
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_申购.getValue()); // 	数据字典.交易方向.申购：交易方向
        if(StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(), dto.getPurchType())){
        	trdFinProductDealTotal.setTgBusiType(DictCodeListExt2.平安业务类型_保证金划款.getValue()); // 保证金划款：投管业务类型
        	trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 保证金划款：投管业务类型
        	trdFinProductDealTotal.setTradeAmt(dto.getDepositAmt()); // 	画面.保证金金额：交易金额
        	trdFinProductDealTotal.setTradeDate(dto.getPurchDate()); // 	画面.申购日期 ：交易日
            trdFinProductDealTotal.setSettleDate(dto.getPurchDate()); // 	画面.申购日期 ：交割日
        }else{
        	trdFinProductDealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue()); // 债券买入：投管业务类型
        	trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 债券买入：投管业务类型
        }
        dto.setTrdFinProductDealTotal(trdFinProductDealTotal);
        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------

        // 金融产品交易汇总附属表Entity 构造开始----------------------------------------------------------
    	TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(dto.getBusiCode()); // 交易编号
        trdDealTotalAdd.setPurchDate(dto.getPurchDate()); // 申购日期
        trdDealTotalAdd.setPurchModle(dto.getPurchModle()); // 申购方式
        trdDealTotalAdd.setPurchType(dto.getPurchType()); // 申购类型 
        trdDealTotalAdd.setDepositAmt(dto.getDepositAmt()); // 保证金金额
        trdDealTotalAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdDealTotalAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        if(StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(), dto.getPurchType())){
        	trdDealTotalAdd.setPurCfmDate(dto.getTradeDate()); // 	画面.确认日期 ：预计申购确认日
        	trdDealTotalAdd.setPurPayDate(dto.getSettleDate()); // 	画面.缴款日期 ：预计申购缴款日
        }
        dto.setTrdDealTotalAdd(trdDealTotalAdd);
        // 金融产品交易汇总附属表Entity 构造结束----------------------------------------------------------
        
        // 金融产品交易汇总扩展表Entity 构造开始----------------------------------------------------------
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(dto.getBusiCode());// 交易编号
        paTrdFinProductDealTotal.setPaBussType(trdFinProductDealTotal.getTgBusiType()); // 平安业务类型
        paTrdFinProductDealTotal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        
        dto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        // 金融产品交易汇总附属表Entity 构造结束----------------------------------------------------------
        
        //优先配售或保证金不为0
        if(StringUtil.equals(DictCodeListExt2.申购类型_优先配售.getValue(), dto.getPurchType())||(StringUtil.isNotEmpty(dto.getDepositAmt())&&dto.getDepositAmt().compareTo(BigDecimal.ZERO)>0)){
        	
	        
	        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
	        
	        TrdProductDeal trdProductDeal = new TrdProductDeal();
	        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
	        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//	        trdProductDeal.setTradeId("");//交易编号
	        if(!StringUtil.isNotEmpty(trdProductDeal.getTradeId())){
	        	if (baseGetBusinessId != null) {
	    			BussinessCodeParameter codeParameter = new BussinessCodeParameter();
	    			codeParameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_可转债申购交易));
	    			trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
	    		}
	    		else {
	    			trdProductDeal.setTradeId(IDGenerator.generate());
	    		}
	        }
	        trdProductDeal.setTradeDate(dto.getTradeDate()); // 	画面.缴款日期 ：金融产品交易.交割日
	        trdProductDeal.setSettleDate(dto.getSettleDate());//	画面.确认日期 ：金融产品交易.交易日
	        //100 ：金融产品交易.单位面值  注：100是债券券面，如存在定义的静态常量，请使用该
	        trdProductDeal.setParValue(new BigDecimal(100));
	        //	100 ：金融产品交易.单位净价
	        trdProductDeal.setUnitCprice(new BigDecimal(100));
	        //	100 ：金融产品交易.单位全价
	        trdProductDeal.setUnitFprice(new BigDecimal(100));
	        //	画面.申购券面总额/100 ：金融产品交易.交易份额 
	        trdProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getTradeAmt(), new BigDecimal(100),2));
	        //		画面.申购券面总额 ：金融产品交易.交易本金
	        trdProductDeal.setPrinAmt(dto.getTradeAmt());
	        // 		画面.申购券面总额：金融产品交易.交易金额
	        trdProductDeal.setTradeAmt(dto.getTradeAmt());
	        // 	画面.申购券面总额 ：净价总额
	        trdProductDeal.setCpriceAmt(dto.getTradeAmt()); 
	        // 	画面.申购券面总额 ：全价总额
	        trdProductDeal.setFpriceAmt(dto.getTradeAmt()); 
	        //	0 ：金融产品交易.利息总额
	        trdProductDeal.setIntAmt(BigDecimal.ZERO);
	        //	0 ：金融产品交易.随交易支付费用
	        trdProductDeal.setFeeAmtPay(BigDecimal.ZERO);
	        //	0 ：金融产品交易.未随交易支付费用
	        trdProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
	        //	0 ：金融产品交易.总交易费用
	        trdProductDeal.setFeeAmt(BigDecimal.ZERO);
	        //	数据字典.交易市场.一级市场：金融产品交易.交易市场
	        trdProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); 
	        //	自动生成.汇总交易编号：金融产品交易.汇总交易编号
	        trdProductDeal.setTotleTradeId(dto.getBusiCode()); // 	
	        //	数据字典.交易方向.买入：交易方向
	        trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
	        if(StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(), dto.getPurchType())){
	        	trdProductDeal.setTgBusiType(DictCodeListExt2.平安业务类型_保证金划款.getValue()); // 保证金划款：投管业务类型
	        	trdProductDeal.setTradeAmt(dto.getDepositAmt()); // 	画面.保证金金额：交易金额
	        	trdProductDeal.setTradeDate(dto.getPurchDate()); // 	画面.申购日期 ：交易日
	        	trdProductDeal.setSettleDate(dto.getPurchDate()); // 	画面.申购日期 ：交割日
	        	trdProductDeal.setPs(DictCodeListExt1.交易方向_保证金划款.getValue());//
	        }else{
	        	trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue()); // 债券买入：投管业务类型
	        }
	        trdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
	        dto.setTrdProductDeal(trdProductDeal);
	        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------
	        
	        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
	        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
	        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId()); // 交易编号
	        trdProductDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
	        trdProductDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
	        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
	        trdProductDealAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
	        trdProductDealAdd.setPurchDate(dto.getPurchDate()); // 申购日期
	        trdProductDealAdd.setPurchModle(dto.getPurchModle()); // 申购方式
	        trdProductDealAdd.setPurchType(dto.getPurchType()); // 申购类型 
	        trdProductDealAdd.setDepositAmt(dto.getDepositAmt()); // 保证金金额
	        if(StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(), dto.getPurchType())){
	        	trdProductDealAdd.setPurCfmDate(dto.getTradeDate()); // 	画面.确认日期 ：预计申购确认日
	        	trdProductDealAdd.setPurPayDate(dto.getSettleDate()); // 	画面.缴款日期 ：预计申购缴款日
	        }
        	dto.setTrdProductDealAdd(trdProductDealAdd);
        	// 金融产品交易附属表Entity 构造结束----------------------------------------------------------
        	
        	// 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            paTrdProductDeal.setTradeId(trdProductDeal.getTradeId()); // 交易编号
            // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), trdProductDeal.getPs())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
            } else if (StringUtil.equals(DictCodeListExt1.交易方向_保证金划款.getValue(), trdProductDeal.getPs())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_保证金划款.getValue());
            }
            paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
            dto.setPaTrdProductDeal(paTrdProductDeal);
            // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
        	
        	//优先配售时生成组合交易信息。
//        	if(StringUtil.equals(DictCodeListExt2.申购类型_优先配售.getValue(), dto.getPurchType())){
            // 组合交易表Entity 构造开始----------------------------------------------------------
                List<PtlTrade> ptlTrades = new ArrayList<>();
                PtlTrade ptlTrade = new PtlTrade();
                ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
                ptlTrade.setBusiNo(trdProductDeal.getTradeId()); // 业务编号
                ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
                ptlTrade.setBusiId(dto.getFinprodId()); // 业务明细代码
                ptlTrade.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 业务类型
                ptlTrade.setInvAim(dto.getInvAim()); // 投资目的
                ptlTrade.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
                ptlTrade.setSettleDate(dto.getSettleDate()); // 实际发生日
                ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
                ptlTrades.add(ptlTrade);

                if (StringUtil.isNotEmpty(dto.getActPortfolioId()) && !StringUtil.equals(dto.getPortfolioId(), dto.getActPortfolioId())) {
                    PtlTrade ptlTrade1 = new PtlTrade();
                    BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
                    ptlTrade1.setPortfolioId(dto.getActPortfolioId());
                    ptlTrades.add(ptlTrade1);
                }

                dto.setPtlTrade(ptlTrades);
                // 组合交易表Entity 构造结束----------------------------------------------------------
//        	}
        	
        }
        
        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (bussTrdTransferOrderService != null) {
            // 调用划款指令共通业务组件的保存处理，根据输出结果加工形成金融产品交易DTO的要素数据
            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getBusiCode()); // 交易编号
                dto.getBussTrdTransferOrder().get(0).setOrderStatus(
                        DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
                dto.getBussTrdTransferOrder().get(0).setWorkflowId(dto.getWorkflowId()); // 工作流代码
                dto.setBussTrdTransferOrder(
                        bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            }
        }
        // 划款指令表Entity 构造结束----------------------------------------------------------
        
        //存回实际组合信息  流程中查看
        dto.setPortfolioId(dto.getActPortfolioId());

    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdConBondDto entity) {
//        entity.setActPortfolioId(entity.getPortfolioId());
//        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
    	if (CollectionUtils.isNotEmpty(entity.getUploadFileList()) && StringUtil.isNotEmpty(entity.getBusiCode())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getBusiCode());
        }
    	if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())&&StringUtil.isNotEmpty(entity.getBussTrdTransferOrder().get(0))) {
            entity.getBussTrdTransferOrder().get(0).setWorkflowId(entity.getWorkflowId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

//        this.prepareEntity(entity);

        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/doInsert", entity, Integer.class);
        

        if(StringUtil.isNotEmpty(entity.getTrdProductDeal())){
	        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
	        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
	        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
	        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
	        baseTradeProductDealDto.setTrdProductDeal(entity.getTrdProductDeal());
	        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
	        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
	            baseTradeProductDealDto.setStlPayOrdersList(
	                    Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
	        }
	        //        委托代码不为空设置委托编号
	        if(StringUtil.isNotEmpty(entity.getEntrustId())){
	            baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
	        }
	        baseTradeProductDealDto.setPurchModle(entity.getPurchModle());
	        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
	        
	        	
	        entity.setActPortfolioId(entity.getPortfolioId());
	        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
	        baseTradeProductDealCommService.execute(finProductTradeDto);
	        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
	            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
	                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
	            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
	            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
	                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
	        }
        }
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    /*private void deleteBussFlowData(String workflowId) {
    	// 金融产品交易信息DTO.金融产品交易Entity -> 	金融产品交易汇总表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_FIN_PRODUCT_DEAL_TOTAL", "WORKFLOW_ID",
                workflowId);
        
     // 金融产品交易信息DTO.金融产品交易Entity -> 	金融产品交易汇总附属表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_DEAL_TOTAL_ADD", "WORKFLOW_ID",
                workflowId);
    	
    	// 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
                workflowId);

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
                "WORKFLOW_ID", workflowId);

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
                "WORKFLOW_ID", workflowId);

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID",
                workflowId);
    }*/

    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
//    private void doRevokeInsert(TrdConBondDto entity) {
//        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
//        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
//                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
//                        + entity.getBusiCode(),
//                BaseTradeProductDealDto.class);
//
//        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
//            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
//                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
//                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
//                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
//                // 交易状态=“撤销”
//                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
//                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
//                wflTrdProductDeal.setpTradeId( baseDto.getTrdProductDeal().getTradeId());
//                // 撤销说明=画面.撤销说明
//                wflTrdProductDeal.setCancelRemark(entity.getRevokeReason());
//                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
//                wflTrdProductDeal.setTradeId(baseDto.getTrdProductDeal().getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
//                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
//
//                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
//                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
//                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
//                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
//                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
//                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
//                wflTrdProductDealAdd.setPortfolioId(baseDto.getActPortfolioId());
//
//                // 批量插入
//                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
//                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
//
//                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
//                    for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
//                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
//                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
//                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
//
//                        String orderId;
//                        if (baseGetBusinessId != null) {
//                            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
//                            bussinessCodeParameter.setSeqNo("9998");
//                            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
//                        } else {
//                            orderId = IDGenerator.generate();
//                        }
//                        wflStlPayOrders.setOrderId(orderId);
//                        wflStlPayOrders.setTradeId(wflTrdProductDeal.getTradeId());
//                        wflStlPayOrders.setpTradeId(stlPayOrdersDto.getOrderId());
//                        wflStlPayOrders.setProdId(wflTrdProductDeal.getTradeId());
//
//                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
//                    }
//                }
//            }
//        }
//    }


//    /**
//     * 撤销doComplete
//     *
//     * @param entity 参数
//     * @see
//     * @since 1.0
//     */
//    private void doCompleteRevoke(TrdConBondDto entity) {
//        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
//        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
//                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
//                        + entity.getBusiCode(),
//                BaseTradeProductDealDto.class);
//
//        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
//        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
//            /*for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
//                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
//                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
//                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
//                PaTrdProductDeal paTrdProductDeal = paTrdProductDealMapper.selectByPrimaryKey(baseDto.getTrdProductDeal().getTradeId());
//                paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
//                paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
//                paTrdProductDeal.setCreateTime(new Date());
//                paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
//                paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
//                paTrdProductDealMapper.insertSelective(paTrdProductDeal);
//            }*/
//            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
//            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);
//
//            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
//            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
//            baseTradeProductDealCommService.execute(finProductTradeDto);
//            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
//                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
//                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId())
//                );
//                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
//                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
//                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
//            }
//
////            if (entity.getPtlTrade() != null) {
////                entity.getPtlTrade().forEach(ptlTrade -> {
////                    ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
////                    ptlTradeMapper.insertSelective(ptlTrade);
////                });
////            }
//
//        }
//
//        // 删除原业务流程记录
//        this.deleteBussFlowData(entity.getWorkflowId());
//
//
//        basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getBusiCode());
//
//        // 调用基础组件.【交易报送组件】删除交易报送信息
//        restTemplate.postForObject(
//                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
//                Arrays.asList(entity.getBusiCode()), Boolean.class);
//
//    }


    @Override
    public void doInsert(TrdConBondDto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
        	setWorkflowId(entity, entity.getWorkflowId());
        	this.handleBussFlowData(entity);
        }
        // 撤销交易
        else {
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            entity.setOperateType(operationType.toString());
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/doInsert",
                entity, Integer.class);
        
        }
    }

    private void setWorkflowId(TrdConBondDto entity, String workflowId) {
    	entity.getTrdFinProductDealTotal().setWorkflowId(workflowId);;//汇总交易
    	entity.getTrdDealTotalAdd().setWorkflowId(workflowId);//汇总交易附属
    	entity.getPaTrdFinProductDealTotal().setWorkflowId(workflowId);//汇总扩展
		if(StringUtil.isNotEmpty(entity.getTrdProductDeal())){
			entity.getTrdProductDeal().setWorkflowId(workflowId);//交易表信息
			entity.getPaTrdProductDeal().setWorkflowId(workflowId);//交易表扩展
			entity.getTrdProductDealAdd().setWorkflowId(workflowId);//交易表附属
			entity.getBussTrdTransferOrder().get(0).setWorkflowId(workflowId); // 指令
//			baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getPtlTrade()//组合交易
		}
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdConBondDto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType())
                && !StringUtil.equals("delete", entity.getOperateType())) {
        	this.prepareEntity(entity);
        	ProcessMark newEntity;
        	try {
        		newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdConBondDto.class, true);
        		// 工作流ID
        		newEntity.setWorkflowId(entity.getWorkflowId());
        		newEntity.setStatus(entity.getStatus());
        		newEntity.setBusinessData(JsonUtils.object2Json(entity));
        		// 更新业务表数据
        		// 操作留痕表
        		WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
        	}
        	catch (Exception e) {
                logger.error(e.getMessage(),e);
        	}
        	// 先删除流程中数据
//        	this.doDelete(entity.getWorkflowId());
        	// 重新insert
        	this.handleBussFlowData(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            TrdConBondDto entity = getBussinessEntityByWorkflowId(workflowId);
            logger.info("sendDelete() called with parameters => 【entity = {}】", JsonUtils.object2Json(entity));
            String busiType = commonPositionEngineService.getPositionBusiType(entity.getFinprodType2(), entity.getPs(), null);
            PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(), POSITION_TYPE_ETR, busiType, entity.getPs());
        }
        baseTradeProductDealCommService.delete(workflowId);
    }

    @Override
    public void doComplete(TrdConBondDto entity, OperationType operationType) {
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
        	
        	 entity.setActPortfolioId(entity.getPortfolioId());
             entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
//             this.prepareEntity(entity);

             // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
             // 金融产品交易汇总信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总表
             TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
             BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, entity.getTrdFinProductDealTotal());
             trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            // trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
             
             
             // 金融产品交易信息DTO.金融产品交易汇总附属表Entity -> 金融产品交易汇总附属表
             TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
             BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, entity.getTrdDealTotalAdd());
             trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
             
             // 金融产品交易信息DTO.金融产品交易汇总扩展表Entity -> 金融产品交易汇总附属表
             PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
             BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, entity.getPaTrdFinProductDealTotal());
             paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
             
             // 金融产品交易信息DTO.金融产品交易表Entity -> 金融产品交易表
             TrdProductDeal trdProductDeal = null;
             if(StringUtil.isNotEmpty(entity.getTrdProductDeal())){
             	trdProductDeal =new TrdProductDeal();
             	BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity.getTrdProductDeal());
                 trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                 entity.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
             }
             
             // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
             TrdProductDealAdd trdProductDealAdd =  null;
             if(StringUtil.isNotEmpty(entity.getTrdProductDealAdd())){
             	trdProductDealAdd = new TrdProductDealAdd();
             	BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());
             }

             // 金融产品交易扩展表Entity -> 金融产品交易扩展表
             PaTrdProductDeal paTrdProductDeal =  null;
             if(StringUtil.isNotEmpty(entity.getPaTrdProductDeal())){
             	paTrdProductDeal = new PaTrdProductDeal();
                 BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getPaTrdProductDeal());
             }
             
             // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
             StlPayOrdersDto stlPayOrders = null;
             if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                 stlPayOrders = new StlPayOrdersDto();
                 BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                         entity.getBussTrdTransferOrder().get(0));
             }
        	
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
            baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);//汇总交易表 更新委托时获取真实交易方向
            // 组合交易表Entity -> 组合交易信息表
            List<PtlTrade> ptlTrades = entity.getPtlTrade();
            if (ptlTrades != null) {
                ptlTrades.forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
            }
            //交易扩展
            if (paTrdProductDeal != null) {
            	paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            }
            
            // 划款指令
            if (stlPayOrders != null) {
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
            }
            baseTradeProductDealDto.setPurchModle(entity.getPurchModle());
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            //        委托代码不为空处理委托代码
            if(StringUtil.isNotEmpty(entity.getEntrustId())){
                baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
             // 调用投资委托组件的更新委托状态接口，将委托状态设置为“已完成”

//                etrInvestInfoServiceImpl.updateDelegateStatus(entity.getEntrustId(),
//                    DictCodeList.委托状态_已完成.getValue());

                List<EtrFinishStatusDto> baseEtrInvestInfoDtos = new ArrayList<>();
                EtrFinishStatusDto etrInvestInfoDto = new EtrFinishStatusDto();
                etrInvestInfoDto.setEntrustId(entity.getEntrustId());
                etrInvestInfoDto.setTradeId(entity.getBusiCode());
                etrInvestInfoDto.setFinishDate(entity.getTradeDate());
                etrInvestInfoDto.setFinishAmt(ArithmeticUtil.divide(entity.getTradeAmt(), new BigDecimal(100), 2));
                etrInvestInfoDto.setTrader(entity.getTrader());
                baseEtrInvestInfoDtos.add(etrInvestInfoDto);

                etrInvestInfoServiceImpl.completeDelegate(baseEtrInvestInfoDtos);

            }
            if(StringUtil.isNotEmpty(entity.getTrdProductDeal())){
            	// 调用业务组件.【金融产品交易共通.交易产品接口】, insert
                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
                baseTradeProductDealCommService.execute(finProductTradeDto);
                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }
            } 
            else {
                trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            }
            
//            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/doComplete", entity,
//                    Integer.class);

            doDelete(entity.getWorkflowId());
//            // 文件
//            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
//            mstFileInputParameter.setFileList(entity.getFileArray());
//            mstFileInputParameter.setBussinessId(entity.getBusiCode());
//            if (mstFileCommonService != null) {
//                mstFileCommonService.updateBusinessId(mstFileInputParameter);
//            }

            // 一体化对应etliu start
            // 所有交易生效后调用的共通方法
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(entity.getTrdProductDeal());
            // <!-- 通道交易 新增 强成西 2019/03/26 start -->
            inputDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
            // <!-- 通道交易 新增 强成西 2019/03/26 end -->

            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
//            StlPayOrdersDto stlPayOrders = null;
//            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
//                stlPayOrders = new StlPayOrdersDto();
//                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
//                        entity.getBussTrdTransferOrder().get(0));
//            }
            // 反向交易增加 start by qiangchengxi 2019年3月12日
            if (StringUtil.isNotEmpty(entity.getTrdProductDeal())) {
                AllTradeCommonUtil.setDeals(inputDto, entity.getTrdProductDeal(), entity.getTrdProductDealAdd(), entity.getPaTrdProductDeal(),
                        Arrays.asList(stlPayOrders), entity.getPtlTrade(), entity.getActPortfolioId());
                inputDto.setStlPayOrders(stlPayOrders);
            }

            // 反向交易增加 end by qiangchengxi 2019年3月12日

            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            if (StringUtil.isNotEmpty(entity.getTrdProductDeal())) {
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }
            // 一体化对应etliu end

            // 调用投管接口 add by renping 20190205 start
            if (trdProductDeal != null) {
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdConBond002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setTrdProductDeal(trdProductDeal);
                investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
                investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                investInterfCommDto.setOtherTypeForConvert(
                    DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
                investInterfCommService.doInvestInterf(investInterfCommDto);
            }
            // 调用投管接口 add by renping 20190205 end

            // 7.2删除原业务流程表中的交易记录
//            String workflowId = entity.getWorkflowId();
//            if (StringUtil.isBlank(workflowId)) {
//                WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
//                wflTrdProductDealKey.setTradeId(entity.getTrdProductDeal().getTradeId());
//                wflTrdProductDealKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//                WflTrdProductDeal wflTrdProductDeal = wflTrdProductDealMapper.selectByPrimaryKey(wflTrdProductDealKey);
//
//                if (wflTrdProductDeal != null) {
//                    workflowId = wflTrdProductDeal.getWorkflowId();
//                }
//            }
//            this.deleteBussFlowData(workflowId);

            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getBusiCode());
            
            if(StringUtil.isNotEmpty(trdProductDeal)){
                if (trdProductDeal != null){
                    trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                    trdProductDeal.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
                    trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());//交易状态
                }
                // 发送Murex接口处理
                // this.sendMurex(entity, false);
                // 一体化对应etliu start
                // 所有交易生效后调用的共通方法
//                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//                inputDto.setProdid(entity.getPortfolioId());
//                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                inputDto.setTrdProductDeal(trdProductDeal);
//
//                // 通道交易 新增 强成西 2019/03/26 start
//                inputDto.setTrdProductDealAdd(trdProductDealAdd);
//                // 通道交易 新增 强成西 2019/03/26 end
//
//                // 反向交易增加 start by qiangchengxi 2019年3月12日
//                AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, trdProductDealAdd, null, Arrays.asList(stlPayOrders), null,entity.getActPortfolioId());
//                // 反向交易增加 end by qiangchengxi 2019年3月12日
//                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                // 一体化对应etliu end
            }
            
            if(StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(), entity.getPurchType())){
            	TrdConBond003Dto confirmDto = new TrdConBond003Dto();
                BeanUtil.copyPropertiesIgnoreNull(confirmDto, entity);
                confirmDto.setbTradeId(entity.getTotleTradeId());
                ProcessParameter<TrdConBond003Dto> workFlowPatameter = getWorkFlowPatameter(confirmDto);
                confirmDto.setPortfolioId(entity.getActPortfolioId());//存入实际组合
                confirmDto.getTrdFinProductDealTotal().setPortfolioId(entity.getActPortfolioId());//存入实际组合
                try {
                    WorkFlowUtil.startProcess(workFlowPatameter.getWorkFlowParameter(), "", "", entity.getTrader(),
                        OperationType.NEW, false, confirmDto);
                }
                catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }

            //成交录单---调用共通
            if(StringUtil.isNotEmpty(entity.getDealSqeId())) {
                exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),trdFinProductDealTotal.getTotleTradeId(),OperationType.NEW);
            }
        }
        // 撤销
        else {
            entity.setOperateType(operationType.toString());
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            Map<String,Object>map = new HashMap<>();
            map.put("busiCode", entity.getBusiCode());
            List<StlPayOrders> result = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getStlPayOrders",
                    map, StlPayOrders.class);
            StlPayOrdersDto stlPayOrders = null;
            if (!StringUtil.isEmptyList(result)) {
                stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                		result.get(0));
            }
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            this.prepareEntity(entity);
//            stlPayOrders = new StlPayOrdersDto();
//            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
//                    entity.getBussTrdTransferOrder().get(0));
            baseTradeProductDealDto.setTrdProductDeal(entity.getTrdProductDeal()); // 金融产品交易
            baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd()); // 金融产品附属
            baseTradeProductDealDto.setTrdFinProductDealTotal(entity.getTrdFinProductDealTotal()); // 汇总交易表 更新委托时获取真实交易方向
            // 划款指令
            if (stlPayOrders != null) {
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
            }
            baseTradeProductDealDto.setPurchModle(entity.getPurchModle());
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            //        委托代码不为空处理委托代码
            if(StringUtil.isNotEmpty(entity.getEntrustId())){
                baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
//                etrInvestInfoServiceImpl.updateDelegateStatus(entity.getEntrustId(),
//                        DictCodeList.委托状态_已接收.getValue());
                etrInvestInfoServiceImpl.cancelCompleteDelegate(entity.getEntrustId(),entity.getBusiCode());
            }
            finProductTradeDto.setNeedSum(true);
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getBusiCode()+"R");

            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            if(StringUtil.isNotEmpty(entity.getTrdProductDeal())){
            	 baseTradeProductDealCommService.execute(finProductTradeDto);
                 entity.setUpdateUser(finProductTradeDto.getUpdateUser());
                 if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                     finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                             baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                     baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                     finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                             baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                 }
            }
            //释放授信
            if(StringUtil.equals(DictCodeListExt2.申购类型_优先配售.getValue(), entity.getPurchType())){
            	this.isNeedOccupy(entity, true, true, true);
            }
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/doComplete",
                    entity,Integer.class);
            // 调用投管接口 add by renping 20190205 start
            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
            investInterfCommDto.setPageId("TrdConBond002");
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
            investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
            investInterfCommDto.setTrdProductDeal(entity.getTrdProductDeal());
            investInterfCommDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
            investInterfCommDto.setStlPayOrders(stlPayOrders);
            investInterfCommService.doInvestInterf(investInterfCommDto);
            // 调用投管接口 add by renping 20190205 end

            //成交录单---调用共通
            exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),entity.getTotleTradeId(),OperationType.REVOKE);

        }
        //        调用接口  add by zhangpuyu 2019/12/6 start
        try {
            List<String> tradeIds = new ArrayList<>();
            tradeIds.add(entity.getBusiCode());
            restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                    Map.class);
        }catch (Exception e){

        }
        //        调用接口  add by zhangpuyu 2019/12/6 end
    }
    
    /**
     * 设置流程参数
     * 
     * @param dto
     * @return
     */
    public ProcessParameter<TrdConBond003Dto> getWorkFlowPatameter(TrdConBond003Dto dto) {
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
            dto.setPs(DictCodeList.交易方向_申购确认.getValue());
        }
        dto.setPortfolioId(dto.getActPortfolioId());//存入实际组合  发起流程
        dto.setbTradeId(dto.getBusiCode());
        dto.setWorkflowId(null);
        dto.setOperateType("save");
        //清除清算指令
        dto.setBussTrdTransferOrder(null);
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
                codeParameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_可转债申购交易));
            }
            dto.setBusiCode(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            dto.setBusiCode(IDGenerator.generate());
        }
        ProcessParameter<TrdConBond003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdConBond003Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId("TrdConBond003");
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        }
        else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId("TrdConBond003");
        workFlowParameter.setElementId("TrdConBond003_btn_save");
        workFlowParameter.setBusinessCode((entity) -> entity.getBusiCode());
        workFlowParameter.setServiceName("trdConBond003Service");
        workFlowParameter.setBusinessType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("TrdBondInvestTrade002.tradeId") + GlobalConstant.COLON + entity.getBusiCode(),
                getMessage("TrdBondInvestTrade002.finprodName") + GlobalConstant.COLON + entity.getFinprodName(),
                getMessage("TrdBondInvestTrade002.tradeDate") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));
        dto.setPortfolioId(dto.getActPortfolioId());
        return processParameter;
    }

    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public TrdConBondDto getEntrustInfo(String entrustId) {
    	BaseEtrInvestInfoDto baseDto=baseEtrInvestInfoService.queryActiveMandate(entrustId);
    	TrdConBondDto dto = new TrdConBondDto();
    	if(baseDto!=null){
    		dto.setTradeAmt(ArithmeticUtil.multiply(baseDto.getEtrEntrust().getEntrustNum(),new BigDecimal(100)));//申购券面总额
    		dto.setPortfolioId(baseDto.getEtrEntrust().getPortfolioId());//组合代码
    		dto.setPurchDate(baseDto.getEtrEntrust().getEntrustDate());//申购日期
    		dto.setPurchType(baseDto.getEtrEntrustAdd().getPurchType());//申购类型
    		dto.setPurchModle(baseDto.getEtrEntrustAdd().getPurchModle());//申购方式
    		dto.setInvAim(baseDto.getEtrEntrust().getInvAim());//投资目的
    		dto.setFinprodId(baseDto.getEtrEntrust().getFinprodId());//资产代码
    		dto.setSecManageAcctId(baseDto.getEtrEntrustAdd().getSecAcctId());//证券账户
    		dto.setChlId(baseDto.getEtrEntrust().getChlId());//通道
    		dto.setTradeMarket(baseDto.getEtrEntrust().getTradeMarket());//交易场所
//    		dto.setTrdInvestPersonInfo();
    		TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
    		trdInvestPersonInfo.setContactInfo(baseDto.getEtrEntrust().getContactInfo());
    		trdInvestPersonInfo.setCounterId(baseDto.getEtrEntrust().getCounterId());//交易对手
    		trdInvestPersonInfo.setCounterTrader(baseDto.getEtrEntrust().getCounterTrader());
//    		trdInvestPersonInfo.setTrader(baseDto.getEtrDistributeList().get);
    		if (baseDto.getEtrDistributeList() != null && baseDto.getEtrDistributeList().size() > 0) {
                trdInvestPersonInfo.setTrader(baseDto.getEtrDistributeList().get(0).getTrader());
            }
    		dto.setTrdInvestPersonInfo(trdInvestPersonInfo);
    		
    	}
    	return dto;
    }


    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public TrdConBondDto getRealEntrustInfo(String entrustId) {
        BaseEtrInvestInfoDto baseDto=baseEtrInvestInfoService.queryActiveMandate(entrustId);
        TrdConBondDto dto = new TrdConBondDto();
        if(baseDto!=null){

            BaseEtrFinishAmtDto etrFinishAmtDto = baseEtrInvestInfoService.selectEtrFinishAmtDtoByPrimaryKey(entrustId);

            if(etrFinishAmtDto!=null && etrFinishAmtDto.getSumFinishAmt()!=null&& BigDecimal.ZERO.compareTo(etrFinishAmtDto.getSumFinishAmt())<0  ){
                dto.setTradeAmt(ArithmeticUtil.multiply(ArithmeticUtil.subtract(baseDto.getEtrEntrust().getEntrustNum(),  etrFinishAmtDto.getSumFinishAmt()),new BigDecimal(100)));
            }else {
                dto.setTradeAmt(ArithmeticUtil.multiply(baseDto.getEtrEntrust().getEntrustNum(), new BigDecimal(100)));//申购券面总额
            }
            dto.setPortfolioId(baseDto.getEtrEntrust().getPortfolioId());//组合代码
            dto.setPurchDate(baseDto.getEtrEntrust().getEntrustDate());//申购日期
            dto.setPurchType(baseDto.getEtrEntrustAdd().getPurchType());//申购类型
            dto.setPurchModle(baseDto.getEtrEntrustAdd().getPurchModle());//申购方式
            dto.setInvAim(baseDto.getEtrEntrust().getInvAim());//投资目的
            dto.setFinprodId(baseDto.getEtrEntrust().getFinprodId());//资产代码
            dto.setSecManageAcctId(baseDto.getEtrEntrustAdd().getSecAcctId());//证券账户
            dto.setChlId(baseDto.getEtrEntrust().getChlId());//通道
            dto.setTradeMarket(baseDto.getEtrEntrust().getTradeMarket());//交易场所
//    		dto.setTrdInvestPersonInfo();
            TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
            trdInvestPersonInfo.setContactInfo(baseDto.getEtrEntrust().getContactInfo());
            trdInvestPersonInfo.setCounterId(baseDto.getEtrEntrust().getCounterId());//交易对手
            trdInvestPersonInfo.setCounterTrader(baseDto.getEtrEntrust().getCounterTrader());
//    		trdInvestPersonInfo.setTrader(baseDto.getEtrDistributeList().get);
            if (baseDto.getEtrDistributeList() != null && baseDto.getEtrDistributeList().size() > 0) {
                trdInvestPersonInfo.setTrader(baseDto.getEtrDistributeList().get(0).getTrader());
            }
            dto.setTrdInvestPersonInfo(trdInvestPersonInfo);

        }
        return dto;
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
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getSecManageAcctId",
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
     * 资产代码：下拉框
     *
     * @param parameters 画面参数
     * @return 资产代码：下拉框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> parameters) {
        return restTemplate.postForPage(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getFinprodId", parameters, SelectOptionDto.class);
    }
    

    /**
     * 可转债申购交易新建画面查看模式查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public TrdConBondDto getTrdConBondDto(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getTrdConBondDto/"
                        + tradeId,
                null, TrdConBondDto.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdConBondDto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getTradeAmt(), LoginUserUtil.getUserName(),
//                      DictCodeList.平安资产类型_债券.getValue(), dto.getWindtypeone(), dto.getTradeMarket(), dto.getPs()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  start
//                      , dto.getFinprodId()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  end
//              );
              Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getTradeAmt(), 
                  LoginUserUtil.getUserName(),DictCodeList.平安资产类型_债券.getValue(), dto.getWindtypeone(), dto.getTradeMarket(), dto.getPs()
                  , dto.getFinprodId() ,entity.getWorkflowId());
              params.put("riskTrialRetDto", dto.getRiskTrialRetDto()); 
              return commonBusinessService.是否需要上级审批(params);
          }catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        } else {
            return null;
        }
//        return null;
    }

    /**
     * 流程退回处理
     *
     * @param entity 画面参数
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public void doBack(TrdConBondDto entity) {
        //  根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
        //卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
        if (StringUtil.equals(entity.getPurchType(), DictCodeListExt2.申购类型_优先配售.getValue())) {
        	isNeedOccupy(entity, true, true, true);
        }

    }
    @Override
    public void doRevoke(TrdConBondDto entity) {
        //  根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
        //卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
        if (StringUtil.equals(entity.getPurchType(), DictCodeListExt2.申购类型_优先配售.getValue())) {
        	isNeedOccupy(entity, true, true, true);
        }

    }

    /**
     * 撤销/流程退回时额度占用、释放处理 Add by xkni 20190830
     *
     * @param entity          交易model
     * @param isNotBottomflag 是否是底层资产交易
     * @param isSave          是否入库
     * @param isdeleteflag    是否是作废删除
     * @return
     */
    private void isNeedOccupy(TrdConBondDto entity, boolean isNotBottomflag, boolean isSave, Boolean isdeleteflag) {
        // 所有交易生效后调用的共通方法(入账信息)  撤销后释放占用的授信额度  Add 20190828 by xkni
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(entity.getPortfolioId());
        inputDto.setProdFinprodType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
        TrdProductDeal TrdProductDeal = new TrdProductDeal();
        TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());//交易状态
        BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity.getTrdProductDeal());
        TrdProductDeal.setTradeId(entity.getBusiCode());
        inputDto.setTrdProductDeal(TrdProductDeal);
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        ceLinesUpdate.isNeedOccupyCre(inputDto, isNotBottomflag, isSave, isdeleteflag);
    }


    public BigDecimal getTongDaoCash(Map<String, Object> param) {
        param.put("platDate",iPlatformDate.getSysDate());
        BigDecimal bigDecimal = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getTongDaoCash",
                param, BigDecimal.class);
        return bigDecimal;
    }
    
    /**
     *@title: getProAvailableCash
     *@description: 组合可用现金查询
     *@author: junYin
     *@date: 2019/06/04 11:22
     *@param: * @param param 1
     *@return: java.util.Map<java.lang.String,java.lang.Object>
     *@throws:
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        if (basePtlCashPositionInfoService != null) {
            PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
            Date vdate = DateUtil.parse((String)param.get("vdate"));
            dto.setCdate(vdate);
            List<String> acctTypeList = new ArrayList<>();
            acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
            dto.setAcctType(acctTypeList);
            dto.setPortfolioId((String)param.get("portfolioId"));
            dto.setCcy((String)param.get("ccy"));
            BigDecimal cashTG = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            Map<String, Object> result = new HashMap<>();
            result.put("cashTG", cashTG);
            acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            result.put("cashT", cashT);
            if (StringUtil.isNotEmpty(param.get("calId"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date vdate1 = DateUtil.parse((String)param.get("vdate1"));
                dto2.setCdate(vdate1);
                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId((String)param.get("portfolioId"));
                dto2.setCcy((String)param.get("ccy"));
                BigDecimal cashT1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto2);
                result.put("cashT1", cashT1);
            }
            BigDecimal cashS = BigDecimal.ZERO;
            if (StringUtil.isNotEmpty(param.get("secAcctId"))) {
            	//查询账户可用现金
            	StlSecAcctInfo secAcct = stlSecAcctInfoMapper.selectByPrimaryKey((String)param.get("secAcctId"));
            	if(secAcct!=null&&StringUtil.isNotEmpty(secAcct.getTrdAcctId())){
            		dto.setAcctType(null);
            		dto.setAcctId(secAcct.getTrdAcctId());
            		cashS = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            	}
            	
            }
            result.put("cashS", cashS);
            BigDecimal cashC = BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(param.get("chlId"))){
            	cashC = getTongDaoCash(param);
            }
            result.put("cashC", cashC);
            return result;
        }
        else {
            return null;
        }
    }
    
    /**
     * 为调用授信 风险组装信息entity
     *
     * @param dto
     * @return
     */
    public TrdFinProductDealTotal getTrdFinProductDealTotal(TrdConBondDto dto) {
    	
    	TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 本方交易员
            dto.setTrader(personInfo.getTrader());
            // 交易对手
            dto.setCounterId(personInfo.getCounterId());
            // 是否外部交易
//            dto.setIsOutTrade(personInfo.getIsOutTrade());
            if(StringUtil.equals(DictCodeList.是否_否.getValue(), personInfo.getIsOutTrade())){
            	dto.setIsOutTrade(personInfo.getIsOutTrade());
            }else{
            	dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            }
            // 对手方交易员
            dto.setCounterTrader(personInfo.getCounterTrader());
            // 联系方式
            dto.setCounterContact(personInfo.getContactInfo());
        }
    	 // 金融产品交易汇总表Entity 构造开始----------------------------------------------------------
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, dto);
        trdFinProductDealTotal.setPortfolioId(dto.getActPortfolioId());//存入实际组合信息
        trdFinProductDealTotal.setTotleTradeId(dto.getBusiCode()); // 交易编号
//        trdFinProductDealTotal.setPortfolioId(dto.getPortfolioId()); // 组合信息
        trdFinProductDealTotal.setTradeDate(dto.getTradeDate()); // 画面确认日期=交易日期
        trdFinProductDealTotal.setSettleDate(dto.getSettleDate()); // 画面缴款日期= 交割日
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(dto.getTradeAmt(), new BigDecimal(100),2)); // 	画面.申购券面总额/100 ：交易份额 
        trdFinProductDealTotal.setCpriceAmt(dto.getTradeAmt()); // 	画面.申购券面总额 ：净价总额
        trdFinProductDealTotal.setFpriceAmt(dto.getTradeAmt()); // 	画面.申购券面总额 ：全价总额
        trdFinProductDealTotal.setPrinAmt(dto.getTradeAmt()); // 	画面.申购券面总额 ：交易本金
        trdFinProductDealTotal.setIntAmt(BigDecimal.ZERO); // 	0 ：利息总额
        trdFinProductDealTotal.setTradeAmt(dto.getTradeAmt()); // 画面.申购券面总额：交易金额
        trdFinProductDealTotal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); // 	数据字典.交易市场.一级市场：交易市场
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_申购.getValue()); // 	数据字典.交易方向.申购：交易方向
        if(StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(), dto.getPurchType())){
        	trdFinProductDealTotal.setTgBusiType(DictCodeListExt2.平安业务类型_保证金划款.getValue()); // 保证金划款：投管业务类型
        	trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 保证金划款：投管业务类型
        	trdFinProductDealTotal.setTradeAmt(dto.getDepositAmt()); // 	画面.保证金金额：交易金额
        	trdFinProductDealTotal.setTradeDate(dto.getPurchDate()); // 	画面.申购日期 ：交易日
            trdFinProductDealTotal.setSettleDate(dto.getPurchDate()); // 	画面.申购日期 ：交割日
        }else{
        	trdFinProductDealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue()); // 债券买入：投管业务类型
        	trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 债券买入：投管业务类型
        }
        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------
    	return trdFinProductDealTotal;
    }

}
