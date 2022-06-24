/*
 * 文件名：TrdConBond003Service.java
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
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.system.base.dao.SysUploadFileInfoMapper;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
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
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.ExchangeTransMatchCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.trade.conbond.dto.TrdConBond003Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 可转债申购确认画面Service
 *
 * @author wangpeng
 * @version 1.0 2019年11月14日
 * @see TrdConBond003Service
 * @since 1.0
 */
@Service
public class TrdConBond003Service extends BaseService<TrdConBond003Dto> implements BaseProcessService<TrdConBond003Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdConBond003Service.class);

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

    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private MapperDelegateService mapperDelegateService;

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
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
    

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;
    
    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    
    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;



    @Autowired
    private FinProductAddMapper finProductAddMapper;
    
    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    
    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
    
    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by renping 20190205 end


    @Autowired
    private ExchangeTransMatchCommonService exchangeTransMatchCommonService;

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
    public void prepareEntity(TrdConBond003Dto dto) {
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
        trdFinProductDealTotal.setbTradeId(dto.getbTradeId());//设置前置交易编号
        trdFinProductDealTotal.setTotleTradeId(dto.getBusiCode()); // 交易编号
        trdFinProductDealTotal.setPortfolioId(dto.getActPortfolioId()); // 组合信息
        trdFinProductDealTotal.setTradeDate(dto.getTradeDate()); // 画面确认日期=交易日期
//        trdFinProductDealTotal.setSettleDate(dto.getSettleDate()); // 画面缴款日期= 交割日
        if(StringUtil.isNotEmpty(dto.getRefundAmt())){
        	trdFinProductDealTotal.setSettleDate(dto.getRefundDate());	//画面.缴款日期/退款日期 ：交割日
        }else{
        	trdFinProductDealTotal.setSettleDate(dto.getSettleDate());//	画面.缴款日期/退款日期 ：交割日
        }
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(dto.getTradeAmt(), new BigDecimal(100),2)); // 	画面.申购券面总额/100 ：交易份额 
        trdFinProductDealTotal.setCpriceAmt(dto.getWinlotteryAmt()); // 设计变更	中签券面总额 ：净价总额
        trdFinProductDealTotal.setFpriceAmt(dto.getWinlotteryAmt()); // 设计变更	中签券面总额 ：全价总额
        trdFinProductDealTotal.setPrinAmt(dto.getWinlotteryAmt()); // 设计变更	中签券面总额 ：交易本金
        trdFinProductDealTotal.setIntAmt(BigDecimal.ZERO); // 	0 ：利息总额
//        trdFinProductDealTotal.setTradeAmt(dto.getTradeAmt()); // 画面.申购券面总额：交易金额
        trdFinProductDealTotal.setTradeAmt(ArithmeticUtil.subtract(dto.getSupplementaryAmt(), dto.getRefundAmt()));//	画面.补缴款金额 - 画面.退款金额：交易金额
        trdFinProductDealTotal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); // 	数据字典.交易市场.一级市场：交易市场
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_申购确认.getValue()); // 	数据字典.交易方向.申购：交易方向
    	trdFinProductDealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue()); // 债券买入：投管业务类型
    	trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 债券买入：投管业务类型
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
//        if(StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(), dto.getPurchType())){
//        	trdDealTotalAdd.setPurCfmDate(dto.getTradeDate()); // 	画面.确认日期 ：预计申购确认日
//        	trdDealTotalAdd.setPurPayDate(dto.getSettleDate()); // 	画面.缴款日期 ：预计申购缴款日
//        }
        trdDealTotalAdd.setPurAmt(dto.getTradeAmt());//设计变更	画面.申购券面总额：申购申请份额
        dto.setTrdDealTotalAdd(trdDealTotalAdd);
        // 金融产品交易汇总附属表Entity 构造结束----------------------------------------------------------
        
        // 金融产品交易汇总扩展表Entity 构造开始----------------------------------------------------------
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(dto.getBusiCode());// 交易编号
        paTrdFinProductDealTotal.setPaBussType(trdFinProductDealTotal.getTgBusiType()); // 平安业务类型
        paTrdFinProductDealTotal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        
        dto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        // 金融产品交易汇总附属表Entity 构造结束----------------------------------------------------------
        
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        // 金融产品交易表Entity 构造开始----------------------------------------------------------
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
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
        trdProductDeal.setTradeDate(dto.getTradeDate()); // 		画面.确认日期 ：金融产品交易.交易日
        trdProductDeal.setSettleDate(trdFinProductDealTotal.getSettleDate());//		画面.缴款日期/退款日期 ：金融产品交易.交割日
        //100 ：金融产品交易.单位面值  注：100是债券券面，如存在定义的静态常量，请使用该
        trdProductDeal.setParValue(new BigDecimal(100));
        //	100 ：金融产品交易.单位净价
        trdProductDeal.setUnitCprice(new BigDecimal(100));
        //	100 ：金融产品交易.单位全价
        trdProductDeal.setUnitFprice(new BigDecimal(100));
        //		画面.中签券面总额/100 ：金融产品交易.交易份额 
        trdProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getWinlotteryAmt(), new BigDecimal(100),2));
        //		画面.中签券面总额：金融产品交易.交易本金
        trdProductDeal.setPrinAmt(dto.getWinlotteryAmt());
        // 		nvl（补缴款金额，0）：金融产品交易.交易金额
        if(StringUtil.isNotEmpty(dto.getSupplementaryAmt())){
        	trdProductDeal.setTradeAmt(dto.getSupplementaryAmt());
        }else{
        	trdProductDeal.setTradeAmt(BigDecimal.ZERO);
        }
        // 		画面.中签券面总额：金融产品交易.净价总额
        trdProductDeal.setCpriceAmt(dto.getWinlotteryAmt()); 
        // 		画面.中签券面总额：金融产品交易.全价总额
        trdProductDeal.setFpriceAmt(dto.getWinlotteryAmt()); 
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
        trdProductDeal.setPs(DictCodeList.交易方向_申购确认.getValue());
    	trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue()); // 债券买入：投管业务类型
        trdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        trdProductDealList.add(trdProductDeal);
        dto.setTrdProductDealList(trdProductDealList);
        // 金融产品交易表Entity 构造结束----------------------------------------------------------
        
        List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
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
        trdProductDealAddList.add(trdProductDealAdd);
        dto.setTrdProductDealAddList(trdProductDealAddList);
    	// 金融产品交易附属表Entity 构造结束----------------------------------------------------------
    	
        List<PaTrdProductDeal> paTrdProductDealList = new ArrayList<>();
    	// 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(trdProductDeal.getTradeId()); // 交易编号
        // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        paTrdProductDealList.add(paTrdProductDeal);
        dto.setPaTrdProductDealList(paTrdProductDealList);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
    	
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
    	
        //退款金额大于零
//        if(StringUtil.isNotEmpty(dto.getRefundAmt())&&dto.getRefundAmt().compareTo(BigDecimal.ZERO)>0){
//	        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
//	        TrdProductDeal trdProductDeal1 = new TrdProductDeal();
//	        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal1, dto);
//	        trdProductDeal1.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//	        if(!StringUtil.isNotEmpty(trdProductDeal1.getTradeId())){
//	        	if (baseGetBusinessId != null) {
//	    			BussinessCodeParameter codeParameter = new BussinessCodeParameter();
//	    			codeParameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_可转债申购交易));
//	    			trdProductDeal1.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
//	    		}
//	    		else {
//	    			trdProductDeal1.setTradeId(IDGenerator.generate());
//	    		}
//	        }
//	        trdProductDeal1.setTradeDate(dto.getRefundDate()); // 	画面.退款日期 ：金融产品交易.交割日
//	        trdProductDeal1.setSettleDate(dto.getRefundDate());//	画面.退款日期：金融产品交易.交易日
//	        //100 ：金融产品交易.单位面值  注：100是债券券面，如存在定义的静态常量，请使用该
//	        trdProductDeal1.setParValue(new BigDecimal(100));
//	        //	100 ：金融产品交易.单位净价
//	        trdProductDeal1.setUnitCprice(new BigDecimal(100));
//	        //	100 ：金融产品交易.单位全价
//	        trdProductDeal1.setUnitFprice(new BigDecimal(100));
//	        //	画面.申购券面总额/100 ：金融产品交易.交易份额 
//	        trdProductDeal1.setShareAmt(ArithmeticUtil.divide(dto.getWinlotteryAmt(), new BigDecimal(100),2));
//	        //		画面.申购券面总额 ：金融产品交易.交易本金
//	        trdProductDeal1.setPrinAmt(dto.getWinlotteryAmt());
//	        // 		画面.退款金额：金融产品交易.交易金额
//	        trdProductDeal1.setTradeAmt(dto.getRefundAmt());
//	        // 	画面.申购券面总额 ：净价总额
//	        trdProductDeal1.setCpriceAmt(dto.getWinlotteryAmt()); 
//	        // 	画面.申购券面总额 ：全价总额
//	        trdProductDeal1.setFpriceAmt(dto.getWinlotteryAmt()); 
//	        //	0 ：金融产品交易.利息总额
//	        trdProductDeal1.setIntAmt(BigDecimal.ZERO);
//	        //	0 ：金融产品交易.随交易支付费用
//	        trdProductDeal1.setFeeAmtPay(BigDecimal.ZERO);
//	        //	0 ：金融产品交易.未随交易支付费用
//	        trdProductDeal1.setFeeAmtUnpay(BigDecimal.ZERO);
//	        //	0 ：金融产品交易.总交易费用
//	        trdProductDeal1.setFeeAmt(BigDecimal.ZERO);
//	        //	数据字典.交易市场.一级市场：金融产品交易.交易市场
//	        trdProductDeal1.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); 
//	        //	自动生成.汇总交易编号：金融产品交易.汇总交易编号
//	        trdProductDeal1.setTotleTradeId(dto.getBusiCode()); // 	
//        	trdProductDeal1.setTgBusiType(DictCodeListExt2.平安业务类型_保证金回款.getValue()); // 保证金划款：投管业务类型
//        	trdProductDeal1.setPs(DictCodeListExt1.交易方向_保证金回款.getValue());//
//	        trdProductDeal1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
//	        trdProductDealList.add(trdProductDeal1);
//	        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------
//	        
//	        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
//	        TrdProductDealAdd trdProductDealAdd1 = new TrdProductDealAdd();
//	        trdProductDealAdd1.setTradeId(trdProductDeal1.getTradeId()); // 交易编号
//	        trdProductDealAdd1.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
//	        trdProductDealAdd1.setEntrustId(dto.getEntrustId()); // 委托代码
//	        trdProductDealAdd1.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
//	        trdProductDealAdd1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
//	        trdProductDealAdd1.setPurchDate(dto.getPurchDate()); // 申购日期
//	        trdProductDealAdd1.setPurchModle(dto.getPurchModle()); // 申购方式
//	        trdProductDealAdd1.setPurchType(dto.getPurchType()); // 申购类型 
//	        trdProductDealAdd1.setDepositAmt(dto.getDepositAmt()); // 保证金金额
//	        trdProductDealAddList.add(trdProductDealAdd1);
//        	// 金融产品交易附属表Entity 构造结束----------------------------------------------------------
//        	
//        	// 金融产品交易扩展Entity 构造开始----------------------------------------------------------
//            PaTrdProductDeal paTrdProductDeal1 = new PaTrdProductDeal();
//            paTrdProductDeal1.setTradeId(trdProductDeal1.getTradeId()); // 交易编号
//            // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
//            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), trdProductDeal1.getPs())) {
//                paTrdProductDeal1.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
//            } else if (StringUtil.equals(DictCodeListExt1.交易方向_保证金划款.getValue(), trdProductDeal1.getPs())) {
//                paTrdProductDeal1.setPaBussType(DictCodeListExt2.平安业务类型_保证金划款.getValue());
//            }
//            paTrdProductDeal1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
//            paTrdProductDealList.add(paTrdProductDeal1);
//            // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
//        	
//    		// 组合交易表Entity 构造开始----------------------------------------------------------
//            PtlTrade ptlTrade1 = new PtlTrade();
//            ptlTrade1.setPortfolioId(dto.getPortfolioId()); // 组合代码
//            ptlTrade1.setBusiNo(trdProductDeal1.getTradeId()); // 业务编号
//            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
//            ptlTrade1.setBusiId(dto.getFinprodId()); // 业务明细代码
//            ptlTrade1.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 业务类型
//            ptlTrade1.setInvAim(dto.getInvAim()); // 投资目的
//            ptlTrade1.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
//            ptlTrade1.setSettleDate(dto.getSettleDate()); // 实际发生日
//            ptlTrade1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
//            ptlTrades.add(ptlTrade1);
//            if (StringUtil.isNotEmpty(dto.getActPortfolioId()) && !StringUtil.equals(dto.getPortfolioId(), dto.getActPortfolioId())) {
//                PtlTrade ptlTrade11 = new PtlTrade();
//                BeanUtil.copyPropertiesIgnoreNull(ptlTrade11, ptlTrade1);
//                ptlTrade11.setPortfolioId(dto.getActPortfolioId());
//                ptlTrades.add(ptlTrade11);
//            }
//            // 组合交易表Entity 构造结束----------------------------------------------------------
//        	
//        }
        
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
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *退款到账使用
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity2(TrdConBond003Dto dto) {
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
        trdFinProductDealTotal.setbTradeId(dto.getbTradeId());//设置前置交易编号
        trdFinProductDealTotal.setTotleTradeId(dto.getBusiCode()); // 交易编号
        trdFinProductDealTotal.setPortfolioId(dto.getActPortfolioId()); // 组合信息
        if(StringUtil.isNotEmpty(dto.getActTradeDate())){
        	trdFinProductDealTotal.setTradeDate(dto.getActTradeDate()); // 		实际到账日：交易日
            trdFinProductDealTotal.setSettleDate(dto.getActTradeDate()); // 实际到账日= 交割日
        }else{
        	trdFinProductDealTotal.setTradeDate(dto.getRefundDate());	//画面.缴款日期/退款日期 ：交易日
        	trdFinProductDealTotal.setSettleDate(dto.getRefundDate());	//画面.缴款日期/退款日期 ：交割日
        }
        /*if(StringUtil.isNotEmpty(dto.getRefundAmt())){
        	trdFinProductDealTotal.setSettleDate(dto.getRefundDate());	//画面.缴款日期/退款日期 ：交割日
        }else{
        	trdFinProductDealTotal.setSettleDate(dto.getSettleDate());//	画面.缴款日期/退款日期 ：交割日
        }*/
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(dto.getTradeAmt(), new BigDecimal(100),2)); // 	画面.申购券面总额/100 ：交易份额 
        trdFinProductDealTotal.setCpriceAmt(dto.getWinlotteryAmt()); // 	画面.申购券面总额 ：净价总额
        trdFinProductDealTotal.setFpriceAmt(dto.getWinlotteryAmt()); // 	画面.申购券面总额 ：全价总额
        trdFinProductDealTotal.setPrinAmt(dto.getWinlotteryAmt()); // 	画面.申购券面总额 ：交易本金
        trdFinProductDealTotal.setIntAmt(BigDecimal.ZERO); // 	0 ：利息总额
//        trdFinProductDealTotal.setTradeAmt(dto.getTradeAmt()); // 画面.申购券面总额：交易金额
        trdFinProductDealTotal.setTradeAmt( dto.getRefundAmt());//		退款金额：交易金额
        trdFinProductDealTotal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); // 	数据字典.交易市场.一级市场：交易市场
        trdFinProductDealTotal.setPs(DictCodeListExt3.交易方向_退款到账.getValue()); // 	数据字典.交易方向.退款到账：交易方向
    	trdFinProductDealTotal.setTgBusiType(DictCodeListExt2.平安业务类型_保证金回款.getValue()); // 债券买入：投管业务类型
    	trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 债券买入：投管业务类型
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
//        if(StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(), dto.getPurchType())){
//        	trdDealTotalAdd.setPurCfmDate(dto.getTradeDate()); // 	画面.确认日期 ：预计申购确认日
//        	trdDealTotalAdd.setPurPayDate(dto.getSettleDate()); // 	画面.缴款日期 ：预计申购缴款日
//        }
        trdDealTotalAdd.setPurAmt(dto.getTradeAmt());//	画面.申购申请份额：申购申请份额
        dto.setTrdDealTotalAdd(trdDealTotalAdd);
        // 金融产品交易汇总附属表Entity 构造结束----------------------------------------------------------
        
        // 金融产品交易汇总扩展表Entity 构造开始----------------------------------------------------------
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(dto.getBusiCode());// 交易编号
        paTrdFinProductDealTotal.setPaBussType(trdFinProductDealTotal.getTgBusiType()); // 平安业务类型
        paTrdFinProductDealTotal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        
        dto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        // 金融产品交易汇总附属表Entity 构造结束----------------------------------------------------------
        
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        dto.setTrdProductDealList(trdProductDealList);
        // 金融产品交易表Entity 构造开始----------------------------------------------------------
        // 金融产品交易表Entity 构造结束----------------------------------------------------------
        
        List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
        dto.setTrdProductDealAddList(trdProductDealAddList);
        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
    	// 金融产品交易附属表Entity 构造结束----------------------------------------------------------
    	
        List<PaTrdProductDeal> paTrdProductDealList = new ArrayList<>();
        dto.setPaTrdProductDealList(paTrdProductDealList);
    	// 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
    	
		// 组合交易表Entity 构造开始----------------------------------------------------------
        List<PtlTrade> ptlTrades = new ArrayList<>();
        dto.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束----------------------------------------------------------
    	
        //退款金额大于零
//        if(StringUtil.isNotEmpty(dto.getRefundAmt())&&dto.getRefundAmt().compareTo(BigDecimal.ZERO)>0){
	        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
	        TrdProductDeal trdProductDeal1 = new TrdProductDeal();
	        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal1, dto);
	        trdProductDeal1.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
	        if(!StringUtil.isNotEmpty(trdProductDeal1.getTradeId())){
	        	if (baseGetBusinessId != null) {
	    			BussinessCodeParameter codeParameter = new BussinessCodeParameter();
	    			codeParameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_可转债申购交易));
	    			trdProductDeal1.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
	    		}
	    		else {
	    			trdProductDeal1.setTradeId(IDGenerator.generate());
	    		}
	        }
	        trdProductDeal1.setTradeDate(dto.getRefundDate()); // 	画面.退款日期 ：金融产品交易.交割日
	        trdProductDeal1.setSettleDate(dto.getRefundDate());//	画面.退款日期：金融产品交易.交易日
	        //100 ：金融产品交易.单位面值  注：100是债券券面，如存在定义的静态常量，请使用该
	        trdProductDeal1.setParValue(new BigDecimal(100));
	        //	100 ：金融产品交易.单位净价
	        trdProductDeal1.setUnitCprice(new BigDecimal(100));
	        //	100 ：金融产品交易.单位全价
	        trdProductDeal1.setUnitFprice(new BigDecimal(100));
	        //	画面.申购券面总额/100 ：金融产品交易.交易份额 
	        trdProductDeal1.setShareAmt(ArithmeticUtil.divide(dto.getWinlotteryAmt(), new BigDecimal(100),2));
	        //		画面.申购券面总额 ：金融产品交易.交易本金
	        trdProductDeal1.setPrinAmt(dto.getWinlotteryAmt());
	        // 		画面.退款金额：金融产品交易.交易金额
	        trdProductDeal1.setTradeAmt(dto.getRefundAmt());
	        // 	画面.申购券面总额 ：净价总额
	        trdProductDeal1.setCpriceAmt(dto.getWinlotteryAmt()); 
	        // 	画面.申购券面总额 ：全价总额
	        trdProductDeal1.setFpriceAmt(dto.getWinlotteryAmt()); 
	        //	0 ：金融产品交易.利息总额
	        trdProductDeal1.setIntAmt(BigDecimal.ZERO);
	        //	0 ：金融产品交易.随交易支付费用
	        trdProductDeal1.setFeeAmtPay(BigDecimal.ZERO);
	        //	0 ：金融产品交易.未随交易支付费用
	        trdProductDeal1.setFeeAmtUnpay(BigDecimal.ZERO);
	        //	0 ：金融产品交易.总交易费用
	        trdProductDeal1.setFeeAmt(BigDecimal.ZERO);
	        //	数据字典.交易市场.一级市场：金融产品交易.交易市场
	        trdProductDeal1.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); 
	        //	自动生成.汇总交易编号：金融产品交易.汇总交易编号
	        trdProductDeal1.setTotleTradeId(dto.getBusiCode()); // 	
        	trdProductDeal1.setTgBusiType(DictCodeListExt2.平安业务类型_保证金回款.getValue()); // 保证金划款：投管业务类型
        	trdProductDeal1.setPs(DictCodeListExt1.交易方向_保证金回款.getValue());//
	        trdProductDeal1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
	        trdProductDealList.add(trdProductDeal1);
	        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------
	        
	        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
	        TrdProductDealAdd trdProductDealAdd1 = new TrdProductDealAdd();
	        trdProductDealAdd1.setTradeId(trdProductDeal1.getTradeId()); // 交易编号
	        trdProductDealAdd1.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
	        trdProductDealAdd1.setEntrustId(dto.getEntrustId()); // 委托代码
	        trdProductDealAdd1.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
	        trdProductDealAdd1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
	        trdProductDealAdd1.setPurchDate(dto.getPurchDate()); // 申购日期
	        trdProductDealAdd1.setPurchModle(dto.getPurchModle()); // 申购方式
	        trdProductDealAdd1.setPurchType(dto.getPurchType()); // 申购类型 
	        trdProductDealAdd1.setDepositAmt(dto.getDepositAmt()); // 保证金金额
	        trdProductDealAddList.add(trdProductDealAdd1);
        	// 金融产品交易附属表Entity 构造结束----------------------------------------------------------
        	
        	// 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            PaTrdProductDeal paTrdProductDeal1 = new PaTrdProductDeal();
            paTrdProductDeal1.setTradeId(trdProductDeal1.getTradeId()); // 交易编号
            // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
//            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), trdProductDeal1.getPs())) {
//                paTrdProductDeal1.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
//            } else if (StringUtil.equals(DictCodeListExt1.交易方向_保证金划款.getValue(), trdProductDeal1.getPs())) {
                paTrdProductDeal1.setPaBussType(DictCodeListExt2.平安业务类型_保证金回款.getValue());
//            }
            paTrdProductDeal1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
            paTrdProductDealList.add(paTrdProductDeal1);
            // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
        	
    		// 组合交易表Entity 构造开始----------------------------------------------------------
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getPortfolioId()); // 组合代码
            ptlTrade1.setBusiNo(trdProductDeal1.getTradeId()); // 业务编号
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
            ptlTrade1.setBusiId(dto.getFinprodId()); // 业务明细代码
            ptlTrade1.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue()); // 业务类型
            ptlTrade1.setInvAim(dto.getInvAim()); // 投资目的
            ptlTrade1.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
            ptlTrade1.setSettleDate(dto.getSettleDate()); // 实际发生日
            ptlTrade1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
            ptlTrades.add(ptlTrade1);
            if (StringUtil.isNotEmpty(dto.getActPortfolioId()) && !StringUtil.equals(dto.getPortfolioId(), dto.getActPortfolioId())) {
                PtlTrade ptlTrade11 = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(ptlTrade11, ptlTrade1);
                ptlTrade11.setPortfolioId(dto.getActPortfolioId());
                ptlTrades.add(ptlTrade11);
            }
            // 组合交易表Entity 构造结束----------------------------------------------------------
        	
//        }
        
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
    private void handleBussFlowData(TrdConBond003Dto entity) {
//        entity.setActPortfolioId(entity.getPortfolioId());
//        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())&&StringUtil.isNotEmpty(entity.getBussTrdTransferOrder().get(0))) {
            entity.getBussTrdTransferOrder().get(0).setWorkflowId(entity.getWorkflowId());
        }
        
    	if (CollectionUtils.isNotEmpty(entity.getUploadFileList()) && StringUtil.isNotEmpty(entity.getBusiCode())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getBusiCode());
        }
        if (!StringUtil.equals("submit", entity.getOperateType())) {
//        	 this.prepareEntity(entity);
        	return;
//        	 if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())){
//        		 this.prepareEntity(entity);
//             }else{
//            	 this.prepareEntity2(entity);
//             }
        }
        // 调用授信共同 sjj start
        TrdProductDeal TrdProductDeal = new TrdProductDeal();
        //提交占用授信
        if (StringUtil.equals("submit", entity.getOperateType())) {
	        if(!StringUtil.isEmptyList(entity.getTrdProductDealList())){
	        	for(TrdProductDeal trd:entity.getTrdProductDealList()){
	        		if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), trd.getPs())){
	        			BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity.getTrdFinProductDealTotal());
	        	        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
	        	        TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
	        	        TrdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
	        	        TrdProductDeal.setTradeId(entity.getBusiCode());
	        	        inputDto.setTrdProductDeal(TrdProductDeal);
	        	        AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
	        		}
	        	}
	        }
        }
        // 调用授信共同 sjj end
        
        if(StringUtil.equals(DictCodeListExt3.交易方向_退款到账.getValue(), entity.getPs())){
        	Map<String,Object> map = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getBusicode/"+ entity.getbTradeId(),
        			Map.class);
        	deleteBussFlowData(map,entity);
        }

        // 金融产品交易信息汇总DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, entity.getTrdFinProductDealTotal());
        wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        
        // 金融产品交易汇总附属信息DTO.金融产品交易汇总附属Entity -> 金融产品交易业务汇总附属流程表
        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, entity.getTrdDealTotalAdd());
//        wflTrdDealTotalAdd.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, entity.getPaTrdFinProductDealTotal());
        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        
        // 批量插入
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
        wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        if(StringUtil.isNotEmpty(entity.getTrdProductDealList())){
        	entity.getTrdProductDealList().forEach(trdProductDeal -> {
        		WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        		BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trdProductDeal);
        		wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        		wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);});
            
        }

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        if(StringUtil.isNotEmpty(entity.getTrdProductDealAddList())){
        	entity.getTrdProductDealAddList().forEach(trdProductDealAdd -> {
        		WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        		BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trdProductDealAdd);
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        	});
        }
        
     // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        if(StringUtil.isNotEmpty(entity.getPaTrdProductDealList())){
        	entity.getPaTrdProductDealList().forEach(paTrdProductDeal -> {
        		 WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                 BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
                 wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                 wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
        	});
        }

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                    entity.getBussTrdTransferOrder().get(0));
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
        }

        if(StringUtil.equals("submit", entity.getOperateType())&&StringUtil.isNotEmpty(entity.getTrdProductDeal())){
	        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
	        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
	        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
	        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
	        baseTradeProductDealDto.setTrdProductDeal(getTrdProductDeal(entity));
	        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
	        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
	            baseTradeProductDealDto.setStlPayOrdersList(
	                    Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
	        }
	        //        委托代码不为空设置委托编号
	        if(StringUtil.isNotEmpty(entity.getEntrustId())){
	            baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
	        }
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
    private void deleteBussFlowData(Map<String,Object> map,TrdConBond003Dto entity) {
//    	// 金融产品交易信息DTO.金融产品交易Entity -> 	金融产品交易汇总表
//        mapperDelegateService.deleteBySpecificKey("WFL_TRD_FIN_PRODUCT_DEAL_TOTAL", "WORKFLOW_ID",
//                workflowId);
//        
//     // 金融产品交易信息DTO.金融产品交易Entity -> 	金融产品交易汇总附属表
//        mapperDelegateService.deleteBySpecificKey("WFL_TRD_DEAL_TOTAL_ADD", "WORKFLOW_ID",
//                workflowId);
//    	
//    	// 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
//        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
//                workflowId);
//
//        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
//        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
//                "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
//        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
//                "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//        mapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID",
//                workflowId);
    	String tradeId = (String) map.get("TRADEID");
    	String busiCode = (String) map.get("BUSICODE");
    	WflTrdFinProductDealTotalKey key= new WflTrdFinProductDealTotalKey();
    	key.setTotleTradeId(busiCode);
    	key.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
    	wflTrdFinProductDealTotalMapper.deleteByPrimaryKey(key);
    	
    	wflTrdDealTotalAddMapper.deleteByPrimaryKey(busiCode);
    	
    	/*WflTrdFinProductDealTotalKey key6= new WflTrdFinProductDealTotalKey();
    	key.setTotleTradeId(busiCode);
    	key.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
    	wflTrdFinProductDealTotalMapper.deleteByPrimaryKey(key);*/
    	
    	WflPaTrdFinDealTotalKey key7= new WflPaTrdFinDealTotalKey();
    	key7.setTotleTradeId(busiCode);
    	key7.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
    	wflPaTrdFinDealTotalMapper.deleteByPrimaryKey(key7);
    	
    	WflTrdProductDealKey key1 = new WflTrdProductDealKey();
        key1.setTradeId(tradeId);
        key1.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflTrdProductDealMapper.deleteByPrimaryKey(key1);
        WflTrdProductDealAddKey key2 = new WflTrdProductDealAddKey();
        key2.setTradeId(tradeId);
        key2.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflTrdProductDealAddMapper.deleteByPrimaryKey(key2);
        WflPaTrdProductDealKey key3 = new WflPaTrdProductDealKey();
        key3.setTradeId(tradeId);
        key3.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflPaTrdProductDealMapper.deleteByPrimaryKey(key3);
        WflStlPayOrdersKey key4 = new WflStlPayOrdersKey();
        key4.setOrderId(entity.getBussTrdTransferOrder().get(0).getOrderId());
        key4.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflStlPayOrdersMapper.deleteByPrimaryKey(key4);
    }

    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
//    private void doRevokeInsert(TrdConBond003Dto entity) {
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


    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
//    private void doCompleteRevoke(TrdConBond003Dto entity) {
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
    public void doInsert(TrdConBond003Dto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
        	setWorkflowId(entity, entity.getWorkflowId());
        	this.handleBussFlowData(entity);
        }
//        // 撤销交易
        else {
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            entity.setOperateType(operationType.toString());
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/doInsert",
                entity, Integer.class);
        
        }
    }
    
    private void setWorkflowId(TrdConBond003Dto entity, String workflowId) {
    	entity.getTrdFinProductDealTotal().setWorkflowId(workflowId);;//汇总交易
    	entity.getTrdDealTotalAdd().setWorkflowId(workflowId);//汇总交易附属
    	entity.getPaTrdFinProductDealTotal().setWorkflowId(workflowId);//汇总扩展
		if(StringUtil.isNotEmpty(entity.getTrdProductDealList())){
			entity.getTrdProductDealList().forEach(trdProductDeal ->
				trdProductDeal.setWorkflowId(workflowId));//交易表信息
			entity.getTrdProductDealAddList().forEach(trdProductDealAdd ->
				trdProductDealAdd.setWorkflowId(workflowId));//交易表附属
			entity.getTrdProductDealList().forEach(paTrdProductDeal ->
				paTrdProductDeal.setWorkflowId(workflowId));//交易表扩展
//			entity.getPaTrdProductDeal().setWorkflowId(workflowId);//交易表扩展
//			entity.getTrdProductDealAdd().setWorkflowId(workflowId);//交易表附属
			entity.getBussTrdTransferOrder().get(0).setWorkflowId(workflowId); // 指令
//			baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getPtlTrade()//组合交易
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdConBond003Dto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType())
                && !StringUtil.equals("delete", entity.getOperateType())) {
//           	 this.prepareEntity(entity);
        	if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())){
           		 this.prepareEntity(entity);
            }else{
            	this.prepareEntity2(entity);
            }
        	ProcessMark newEntity;
        	try {
        		newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdConBond003Dto.class, true);
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
    }

    @Override
    public void doComplete(TrdConBond003Dto entity, OperationType operationType) {

        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
        	entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
//            this.prepareEntity(entity);

            // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
            // 金融产品交易汇总信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总表
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, entity.getTrdFinProductDealTotal());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            
            
            // 金融产品交易信息DTO.金融产品交易汇总附属表Entity -> 金融产品交易汇总附属表
            TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, entity.getTrdDealTotalAdd());
            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
            
            // 金融产品交易信息DTO.金融产品交易汇总扩展表Entity -> 金融产品交易汇总附属表
            PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, entity.getPaTrdFinProductDealTotal());
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
            
            // 金融产品交易信息DTO.金融产品交易表Entity -> 金融产品交易表
            if(StringUtil.isNotEmpty(entity.getTrdProductDealList())){
            	entity.getTrdProductDealList().forEach(trdProductDeal -> {
                	trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                	trdProductDealMapper.insertSelective(trdProductDeal);});
            }
            
            // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
            if(StringUtil.isNotEmpty(entity.getTrdProductDealAddList())){
            	entity.getTrdProductDealAddList().forEach(trdProductDealAdd -> 
                	trdProductDealAddMapper.insertSelective(trdProductDealAdd));
            }

            // 金融产品交易扩展表Entity -> 金融产品交易扩展表
            if(StringUtil.isNotEmpty(entity.getPaTrdProductDealList())){
            	entity.getPaTrdProductDealList().forEach(patrdProductDeal -> 
            	paTrdProductDealMapper.insertSelective(patrdProductDeal));
            }
            
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
            StlPayOrdersDto stlPayOrders = null;
            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                        entity.getBussTrdTransferOrder().get(0));
                //清算指令金额为零，该清算指令不需要入库
                if(BigDecimal.ZERO.compareTo(stlPayOrders.getSettleAmt())!=0){
                	//申购确认时，该指令是退款，不需要入库
                	if((StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())
                			&&StringUtil.isNotEmpty(entity.getRefundAmt())&&BigDecimal.ZERO.compareTo(entity.getRefundAmt())<0)){
                		
                	}else{
                		stlPayOrdersMapper.insertSelective(stlPayOrders);
                	}
                }
            }
            
            // 组合交易表Entity -> 组合交易信息表
            List<PtlTrade> ptlTrades = entity.getPtlTrade();
            if (ptlTrades != null) {
                ptlTrades.forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
            }
            
            FinProductTradeDto finProductTradeDto = genFinProductTradeDto(entity, operationType,stlPayOrders);
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            
            finProductTradeDto.setNeedSum(true);
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

//            // 7.2删除原业务流程表中的交易记录
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
            

            doDelete(entity.getWorkflowId());
            // 所有交易生效后调用的共通方法
            for (TrdProductDeal trdProductDeal : entity.getTrdProductDealList()) {
            	AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                inputDto.setTrdProductDeal(trdProductDeal);
//                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            	for (TrdProductDealAdd trdProductDealAdds : entity.getTrdProductDealAddList()) {
                    if (StringUtil.equals(trdProductDeal.getTradeId(), trdProductDealAdds.getTradeId())) {
                    	inputDto.setProdid(trdProductDealAdds.getPortfolioId());
                        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                        inputDto.setTrdProductDealAdd(trdProductDealAdds);
                    }
                }
                List<PtlTrade> ptlTradeList = new ArrayList<>();
                for(PtlTrade ptlTrade : entity.getPtlTrade()){
                	if (StringUtil.equals(trdProductDeal.getTradeId(), ptlTrade.getBusiNo())) {
                		ptlTradeList.add(ptlTrade);
                	}
                }
//                PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
//                for(PaTrdProductDeal paTrdProductDeals : entity.getPaTrdProductDealList()){
//                	if (StringUtil.equals(trdProductDeal.getTradeId(), paTrdProductDeals.getTradeId())) {
//                		paTrdProductDeal = paTrdProductDeals;
//                	}
//                }
                inputDto.setTrdProductDeal(trdProductDeal);
//                inputDto.setTrdProductDealAdd(trdProductDealAdd);
                inputDto.setPtlTradeList(ptlTradeList);
                inputDto.setActPortfolioId(entity.getActPortfolioId());
                if(!StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())){
                	inputDto.setStlPayOrders(stlPayOrders);
                }else if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())&&!StringUtil.isNotEmpty(entity.getRefundAmt())){
                	inputDto.setStlPayOrders(stlPayOrders);
                }else if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())&&StringUtil.isNotEmpty(entity.getRefundAmt())&&entity.getRefundAmt().compareTo(BigDecimal.ZERO)<0){
                	inputDto.setStlPayOrders(stlPayOrders);
                }
//                AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, trdProductDealAdd, paTrdProductDeal,
//                            null, ptlTradeList, entity.getActPortfolioId());
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

                // 调用投管接口 add by renping 20190205 start
                if (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())) {
                    InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                    investInterfCommDto.setPageId("TrdConBond003");
                    investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                    investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                    investInterfCommDto.setTrdProductDeal(trdProductDeal);
                    investInterfCommDto.setTrdProductDealAdd(inputDto.getTrdProductDealAdd());
                    investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                    investInterfCommDto.setOtherTypeForConvert(DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
                    investInterfCommService.doInvestInterf(investInterfCommDto);
                }
                // 调用投管接口 add by renping 20190205 end
            }
            //退款金额大于零  生成退款到账 交易
            if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())&&StringUtil.isNotEmpty(entity.getRefundAmt())&&entity.getRefundAmt().compareTo(BigDecimal.ZERO)>0){
            	doInsertRedemption(entity);
            }

            //成交录单---调用共通
            if(StringUtil.isNotEmpty(entity.getDealSqeId())) {
                exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),trdFinProductDealTotal.getTotleTradeId(),OperationType.NEW);
            }
            
        }
        // 撤销
        else {
//            this.doCompleteRevoke(entity);

            entity.setOperateType(operationType.toString());
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            
            Map<String,Object> map = new HashMap<>();
            map.put("busiCode", entity.getBusiCode());
            List<StlPayOrders> result = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getStlPayOrders",
                    map, StlPayOrders.class);
            StlPayOrdersDto stlPayOrders = null;
            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                        entity.getBussTrdTransferOrder().get(0));
            }
            else if (StringUtil.isEmptyList(result)) {
                //获取退款到账清算指令
            	String refundTradeId = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getRefundTradeId/"+ entity.getBusiCode(),
            			String.class);//获取退款到账交易编号
                Map<String,Object> refundMap = new HashMap<>();
                refundMap.put("busiCode", refundTradeId);
                result = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getStlPayOrders",
                        refundMap, StlPayOrders.class);
                stlPayOrders = null;
                if (!StringUtil.isEmptyList(result)) {
                    stlPayOrders = new StlPayOrdersDto();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                    		result.get(0));
                }
            	
            }else{
            	stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                		result.get(0));
            }

            //场内债券申购确认交易页面撤销时报错,因为没有留痕表数据
            if(!StringUtil.isNotEmpty(entity.getTrdFinProductDealTotal())) {
                this.prepareEntity(entity);
            }
            List<TrdProductDeal> productDealList = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getTrdProductDeals",
                    map, TrdProductDeal.class);
            entity.setTrdProductDealList(productDealList);
            //交易方向为申购确认
        	TrdConBond003Dto dto = new TrdConBond003Dto();
        	BeanUtil.copyPropertiesIgnoreNull(dto, entity);
        	finProductTradeDto= this.genFinProductTradeDto(dto, operationType, stlPayOrders);
            finProductTradeDto.setNeedSum(true);
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getBusiCode()+"R");

            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(finProductTradeDto);
            entity.setUpdateUser(finProductTradeDto.getUpdateUser());
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
            //释放授信额度
            isNeedOccupy(entity);

            // 调用投管接口 add by renping 20190205 start
            if (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())) {
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdConBond003");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setTrdProductDeal(
                    finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(
                    finProductTradeDto.getBaseTradeProductDealDtos().get(
                        0).getTrdProductDealAdd());
                investInterfCommDto.setStlPayOrders(stlPayOrders);
                investInterfCommService.doInvestInterf(investInterfCommDto);
            }
            // 调用投管接口 add by renping 20190205 end

            String tradeId = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getTradeId/"+ entity.getBusiCode(),
        			String.class);//获取退款到账交易编号
            if(StringUtil.isNotEmpty(tradeId)){
            	TrdConBond003Dto  refunddto = this.getTrdConBond003Dto(tradeId);
            	this.prepareEntity2(refunddto);
//            	List<BussTrdTransferOrderBase> bttob = bussSettleOrderCommService.selectOrdersByBizCode((String)accountTradId.get("TRADID"));
                map.put("busiCode", refunddto.getBusiCode());
                result = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getStlPayOrders",
                        map, StlPayOrders.class);
                stlPayOrders = null;
                if (!StringUtil.isEmptyList(result)) {
                    stlPayOrders = new StlPayOrdersDto();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                    		result.get(0));
                }
            	
                refunddto.setActPortfolioId(refunddto.getPortfolioId());
                refunddto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(refunddto.getPortfolioId()));
                finProductTradeDto= this.genFinProductTradeDto(refunddto, operationType, stlPayOrders);
                finProductTradeDto.setNeedSum(true);
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                //basRiskMathCommService.insert(refunddto.getRiskTrialRetDto(), refunddto.getBusiCode()+"R");
                if (refunddto.getActPortfolioId() != null && !refunddto.getActPortfolioId().equals(refunddto.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                        finProductTrade.setActPortfolioId(refunddto.getActPortfolioId());
                    });
                }
                baseTradeProductDealCommService.execute(finProductTradeDto);
                refunddto.setUpdateUser(finProductTradeDto.getUpdateUser());
                if (StringUtil.isNotEmpty(refunddto.getActPortfolioId()) && !refunddto.getActPortfolioId().equals(refunddto.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(refunddto.getActPortfolioId()));
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(refunddto.getPortfolioId()));
                }
            	
            }
            
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/doComplete",
                    entity,Integer.class);

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
     * 可转债申购确认新建画面查看模式查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 可转债交易信息
     * @see
     * @since 1.0
     */
    public TrdConBond003Dto getTrdConBond003Dto(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getTrdConBondDto/"
                        + tradeId,
                null, TrdConBond003Dto.class);
    }


    /**
     * 插入交易到账信息 <功能详细描述>
     * 
     * @param entity
     * @see
     * @since 1.0
     */
    public void doInsertRedemption(TrdConBond003Dto entity) {
//        UploadFile file = null;
//        List<SysUploadFileInfo> syfiList = entity.getFileArray();
//        for (SysUploadFileInfo sufi : syfiList) {
//            file = fileAttachmentProcess.fetch(sufi.getSeqNo());
//            String generateId = IDGenerator.generate();
//            file.setFileNo(generateId);
//            fileAttachmentProcess.upload(file);
//            sufi.setSeqNo(generateId);
//        }
        TrdConBond003Dto redEntity = new TrdConBond003Dto();
        BeanUtil.copyPropertiesIgnoreNull(redEntity, entity);
        redEntity.setbTradeId(entity.getBusiCode());
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_可转债申购交易));
            redEntity.setBusiCode(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            redEntity.setBusiCode(IDGenerator.generate());
        }
        redEntity.setPs(DictCodeListExt3.交易方向_退款到账.getValue());
        String actportfolioId = entity.getActPortfolioId();
        redEntity.setPortfolioId(actportfolioId);
        //批量审批时  实际到款日为平台日
        prepareEntity2(redEntity);
        redEntity.getTrdFinProductDealTotal().setTradeDate(entity.getTrdFinProductDealTotal().getTradeDate());
        redEntity.getTrdFinProductDealTotal().setSettleDate(entity.getTrdFinProductDealTotal().getSettleDate());
        if(StringUtil.isNotEmpty(actportfolioId)) {
            redEntity.setActPortfolioId(actportfolioId);
        }
        
        
        if (StringUtil.isNotEmpty(entity.getChlId())) {
            FinProductAdd payType = finProductAddMapper.selectByPrimaryKey(entity.getChlId());
            if ((payType != null
                 && DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType.getPayType()))
                ) {
                doComplete(redEntity, OperationType.NEW);
                return;
            }
        }
        
     // 金融产品交易信息汇总DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, redEntity.getTrdFinProductDealTotal());
        wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        
        // 金融产品交易汇总附属信息DTO.金融产品交易汇总附属Entity -> 金融产品交易业务汇总附属流程表
        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, redEntity.getTrdDealTotalAdd());
//        wflTrdDealTotalAdd.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        
     // 金融产品交易汇总附属信息DTO.金融产品交易汇总附属Entity -> 金融产品交易业务汇总附属流程表
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, redEntity.getPaTrdFinProductDealTotal());
//        wflTrdDealTotalAdd.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        
        // 批量插入
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
        wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);

        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, redEntity.getTrdProductDealList().get(0));
        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflTrdProductDeal.setWorkflowId(null);
        wflTrdProductDealMapper.insert(wflTrdProductDeal);
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, redEntity.getTrdProductDealAddList().get(0));
        wflTrdProductDealAdd.setWorkflowId(redEntity.getWorkflowId());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflTrdProductDealAdd.setPortfolioId(redEntity.getActPortfolioId());
//		wflTrdProductDealAdd.setRedCfmDate(entity.getRedCfmDate());
        //        推荐方 by zhuyongkang 20191023 start
//        wflTrdProductDealAdd.setAssetRecommandOrg(entity.getAssetRecommandOrg());
        //        推荐方 by zhuyongkang 20191023 start
        wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, redEntity.getPaTrdProductDealList().get(0));
        wflPaTrdProductDeal.setWorkflowId(redEntity.getWorkflowId());
        wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
        if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
            wflStlPayOrders.setTradeId(wflTrdProductDeal.getTradeId());
            wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wflStlPayOrdersMapper.insert(wflStlPayOrders);
        }
        // 文件
//        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
//        mstFileInputParameter.setFileList(entity.getFileArray());
//        mstFileInputParameter.setBussinessId(entity.getTradeId());
//        if (mstFileCommonService != null) {
//            mstFileCommonService.updateBusinessId(mstFileInputParameter);
//        }
    
    }



    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdConBond003Dto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
        	 try {
//               return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getTradeAmt(), LoginUserUtil.getUserName(),
//                       DictCodeList.平安资产类型_债券.getValue(), dto.getWindtypeone(), dto.getTradeMarket(), dto.getPs()
//                       // 资产类型改为授权类型 add by renping 2019-08-06  start
//                       , dto.getFinprodId()
//                       // 资产类型改为授权类型 add by renping 2019-08-06  end
//               );
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

    private void isNeedOccupy(TrdConBond003Dto entity){
        
        TrdProductDeal TrdProductDeal = new TrdProductDeal();
        if(!StringUtil.isEmptyList(entity.getTrdProductDealList())){
        	for(TrdProductDeal trd:entity.getTrdProductDealList()){
        		if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), trd.getPs())){
        			BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity.getTrdFinProductDealTotal());
        	        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        	        TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
        	        TrdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
        	        TrdProductDeal.setTradeId(entity.getBusiCode());
        	        inputDto.setTrdProductDeal(TrdProductDeal);
        	        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        	        ceLinesUpdate.isNeedOccupyCre(inputDto, true, true, true);
        		}
        	}
        }

    
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
    public void doBack(TrdConBond003Dto entity) {
    //  根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
        //卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//        if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
//        }
    	isNeedOccupy(entity);
    }
    @Override
    public void doRevoke(TrdConBond003Dto entity) {
    //  根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
        //卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//        if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
//        }
    	isNeedOccupy(entity);
    }

    public FinProductTradeDto genFinProductTradeDto(TrdConBond003Dto trdConBond003Dto, OperationType operationType,StlPayOrdersDto stlPayOrders) {
        // 5.构造调用组件的输入参数DTO
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        //      委托代码不为空处理委托代码
        if(StringUtil.isNotEmpty(trdConBond003Dto.getEntrustId())){
            baseTradeProductDealDto.setEntrustId(trdConBond003Dto.getEntrustId());
        }
        /**add by taoran 20200303 普通申购确认设置 申购券面 - 中签券面(更新实时现金) start*/
        //      申购类型不为空处理委托代码
        if(StringUtil.isNotEmpty(trdConBond003Dto.getPurchType())){
            baseTradeProductDealDto.setPurchType(trdConBond003Dto.getPurchType());
        }
        //普通申购确认设置 申购券面 - 中签券面
        if(DictCodeListExt2.申购类型_普通申购.getValue()
        		.equals(trdConBond003Dto.getPurchType())) {
        	baseTradeProductDealDto.setSecAmount(ArithmeticUtil.doSub(trdConBond003Dto.getTradeAmt(), trdConBond003Dto.getWinlotteryAmt()));
        }
        /**add by taoran 20200303 普通申购确认设置 申购券面 - 中签券面(更新实时现金) end*/
        baseTradeProductDealDtos.add(baseTradeProductDealDto);

        TrdProductDeal trdProductDeal = null;
        //申购确认
        if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), trdConBond003Dto.getPs())){
        	trdProductDeal = getTrdProductDeal(trdConBond003Dto);
        }else{
        	//退款到账
        	trdProductDeal = trdConBond003Dto.getTrdProductDealList().get(0);
        }
        
        // 划款指令
        if (stlPayOrders != null) {
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
        }
        TrdProductDealAdd add = new TrdProductDealAdd();
        add.setPortfolioId(trdConBond003Dto.getPortfolioId());
        
        add.setPurchModle(trdConBond003Dto.getPurchModle());

        //      申购确认不需要更新委托完成度
//        if(StringUtil.isNotEmpty(trdConBond003Dto.getEntrustId())){
//        	add.setEntrustId(trdConBond003Dto.getEntrustId());
//        }
        
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
        // 划款指令
        if (stlPayOrders != null) {
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
        }
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        return finProductTradeDto;
    }
    
    
    /**
     * 为调用共通组件组装的entity
     *
     * @param entity
     * @return
     */
    private TrdProductDeal getTrdProductDeal(TrdConBond003Dto dto) {
    	//申购确认
        if(StringUtil.equals(DictCodeListExt3.交易方向_退款到账.getValue(), dto.getPs())){
        	return dto.getTrdProductDealList().get(0);
        }
    	TrdProductDeal trdProductDeal = new TrdProductDeal();
    	BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto.getTrdProductDealList().get(0));
    	trdProductDeal.setTradeId(dto.getBusiCode());
    	trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//    	trdProductDeal.setPs(DictCodeListExt1.交易方向_保证金回款.getValue());//该方向为持仓流水更新交易方向。
//    	trdProductDeal.setTradeAmt(dto.getDepositAmt());//交易金额存为保证金金额。冲掉在途的保证金。
    	//退款
    	if(StringUtil.isNotEmpty(dto.getRefundAmt())&&dto.getRefundAmt().compareTo(BigDecimal.ZERO)>0){
    		//金额为min(画面.中签券面总额，画面.保证金金额) 最终金额不为0时录入 
    		if(trdProductDeal.getPrinAmt().compareTo(dto.getDepositAmt())>0){
	    		trdProductDeal.setTradeAmt(dto.getDepositAmt());
	    	}else{
	    		trdProductDeal.setTradeAmt(trdProductDeal.getPrinAmt());
	    	}
    	}else{
    		//补缴款时  保证金全部冲掉
    		trdProductDeal.setTradeAmt(dto.getDepositAmt());
    	}
    	//退款金额大于零
//        if(StringUtil.isNotEmpty(dto.getRefundAmt())&&dto.getRefundAmt().compareTo(BigDecimal.ZERO)>0){
//        	// 金融产品交易附属表Entity 构造开始----------------------------------------------------------
//	        trdProductDeal.setTradeDate(dto.getRefundDate()); // 	画面.退款日期 ：金融产品交易.交割日
//	        trdProductDeal.setSettleDate(dto.getRefundDate());//	画面.退款日期：金融产品交易.交易日
//	        //100 ：金融产品交易.单位面值  注：100是债券券面，如存在定义的静态常量，请使用该
//	        trdProductDeal.setParValue(new BigDecimal(100));
//	        //	100 ：金融产品交易.单位净价
//	        trdProductDeal.setUnitCprice(new BigDecimal(100));
//	        //	100 ：金融产品交易.单位全价
//	        trdProductDeal.setUnitFprice(new BigDecimal(100));
//	        //	画面.申购券面总额/100 ：金融产品交易.交易份额 
//	        trdProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getWinlotteryAmt(), new BigDecimal(100),2));
//	        //		画面.申购券面总额 ：金融产品交易.交易本金
//	        trdProductDeal.setPrinAmt(dto.getTradeAmt());
//	        // 	画面.申购券面总额 ：净价总额
//	        trdProductDeal.setCpriceAmt(dto.getWinlotteryAmt()); 
//	        // 	画面.申购券面总额 ：全价总额
//	        trdProductDeal.setFpriceAmt(dto.getWinlotteryAmt()); 
//	        //	0 ：金融产品交易.利息总额
//	        trdProductDeal.setIntAmt(BigDecimal.ZERO);
//	        //	0 ：金融产品交易.随交易支付费用
//	        trdProductDeal.setFeeAmtPay(BigDecimal.ZERO);
//	        //	0 ：金融产品交易.未随交易支付费用
//	        trdProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
//	        //	0 ：金融产品交易.总交易费用
//	        trdProductDeal.setFeeAmt(BigDecimal.ZERO);
//	        //	数据字典.交易市场.一级市场：金融产品交易.交易市场
//	        trdProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); 
//	        //	自动生成.汇总交易编号：金融产品交易.汇总交易编号
//	        trdProductDeal.setTotleTradeId(dto.getBusiCode()); // 	
//        	trdProductDeal.setBusiType(DictCodeListExt2.平安业务类型_保证金回款.getValue()); // 保证金划款：投管业务类型
//        	trdProductDeal.setTradeAmt(dto.getDepositAmt()); // 	画面.保证金金额：交易金额
//        	
//	        trdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
//        }else{
//            trdProductDeal.setTradeDate(dto.getTradeDate()); // 		画面.确认日期 ：金融产品交易.交易日
//            trdProductDeal.setSettleDate(dto.getSettleDate());//		画面.缴款日期/退款日期 ：金融产品交易.交割日
//            //100 ：金融产品交易.单位面值  注：100是债券券面，如存在定义的静态常量，请使用该
//            trdProductDeal.setParValue(new BigDecimal(100));
//            //	100 ：金融产品交易.单位净价
//            trdProductDeal.setUnitCprice(new BigDecimal(100));
//            //	100 ：金融产品交易.单位全价
//            trdProductDeal.setUnitFprice(new BigDecimal(100));
//            //		画面.中签券面总额/100 ：金融产品交易.交易份额 
//            trdProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getWinlotteryAmt(), new BigDecimal(100),2));
//            //		画面.中签券面总额：金融产品交易.交易本金
//            trdProductDeal.setPrinAmt(dto.getWinlotteryAmt());
//            // 		nvl（补缴款金额，0）：金融产品交易.交易金额
//            trdProductDeal.setCpriceAmt(dto.getWinlotteryAmt()); 
//            // 		画面.中签券面总额：金融产品交易.全价总额
//            trdProductDeal.setFpriceAmt(dto.getTradeAmt()); 
//            //	0 ：金融产品交易.利息总额
//            trdProductDeal.setIntAmt(BigDecimal.ZERO);
//            //	0 ：金融产品交易.随交易支付费用
//            trdProductDeal.setFeeAmtPay(BigDecimal.ZERO);
//            //	0 ：金融产品交易.未随交易支付费用
//            trdProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
//            //	0 ：金融产品交易.总交易费用
//            trdProductDeal.setFeeAmt(BigDecimal.ZERO);
//            //	数据字典.交易市场.一级市场：金融产品交易.交易市场
//            trdProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue()); 
//            //	自动生成.汇总交易编号：金融产品交易.汇总交易编号
//            trdProductDeal.setTotleTradeId(dto.getBusiCode()); // 	
//            trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());//
//        	trdProductDeal.setBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue()); // 债券买入：投管业务类型
//            trdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
//            dto.setTrdProductDeal(trdProductDeal);
//        }
        return trdProductDeal;
    }

	public TrdConBond003Dto getTrdConBondDtoForAccount(String tradeId) {
		return restTemplate.getForObject(
	            ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getTrdConBondDtoForAccount/" + tradeId,
	            TrdConBond003Dto.class);
	}

	public TrdConBond003Dto getTrdConBondDtoForTradeId(String tradeId) {
		return restTemplate.getForObject(
	            ServiceApplication.TRADE_SERVICE_NAME + "/TrdConBond/getTrdConBondDtoForTradeId/" + tradeId,
	            TrdConBond003Dto.class);
	}
}
