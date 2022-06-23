/*
 * 文件名：TrdBondInvestTrade001Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：Zane.Y 修改时间：2018年9月19日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.pretrdbondtrade.service;

import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.isNotFomartSec;
import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.toLocalDate;
import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.toSecInfoModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.Basis;
import com.joyintech.calculation.bonds.constant.CurrIntCalMethod;
import com.joyintech.calculation.bonds.constant.DiscountScheme;
import com.joyintech.calculation.bonds.constant.FlowCashScheme;
import com.joyintech.calculation.bonds.constant.IntRateType;
import com.joyintech.calculation.bonds.constant.NextIntCalMethod;
import com.joyintech.calculation.bonds.constant.Variety;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.PriceYieldInParameter;
import com.joyintech.calculation.shell.param.PriceYieldOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.shell.param.YieldPriceInParameter;
import com.joyintech.calculation.shell.param.YieldPriceOutParameter;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinDealPledgePre;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.InterfTradSecdeal;
import com.joyintech.tams.common.base.entity.StlSecAcctInfo;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAddPre;
import com.joyintech.tams.common.base.entity.TrdProductDealPre;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.WflTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAddPre;
import com.joyintech.tams.common.base.entity.WflTrdProductDealPre;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussTransDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.common.TrdTransfenpeiDto;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.PreTrdCommonDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.PreTrdCommonBaseService;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.bond.dto.SecCpriceInfoDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeCSTPDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.conbond.dto.TrdConBondDto;
import com.joyintech.tams.invest.trade.pretrdbondtrade.dto.PreTrdBondInvestTrade002Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 债券投资交易新建画面Service
 *
 * @author etliu
 * @version 1.0 2018年11月29日
 * @see PreTrdBondInvestTrade002Service
 * @since 1.0
 */
@Service
public class PreTrdBondInvestTrade002Service extends BaseService<PreTrdBondInvestTrade002Dto> implements BaseProcessService<PreTrdBondInvestTrade002Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(PreTrdBondInvestTrade002Service.class);

    @Autowired
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    @Autowired
    private DateCalService dateCalService;
    @Autowired
    private TradeCommonService tradeCommonService;




    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 费用查询
     */
    @Autowired(required = false)
    private BaseTradeFeeCommService baseTradeFeeCommService;

    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private MapperDelegateService mapperDelegateService;
    
    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    
    
    @Autowired
    private TrdFinProductDealTotalMapper TrdFinProductDealTotalMapper;
    
    @Autowired
    private PreTrdCommonBaseService preTrdCommonBaseService;
    
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoService;
    
    @Autowired
    private WflTrdProductDealAddPreMapper wflTrdProductDealAddPreMapper;
    
    @Autowired
    private WflTrdProductDealPreMapper wflTrdProductDealPreMapper;

    /**
     * MultiApproveTradeCheckService
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;


    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;


    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;


    @Autowired
    private InterfTradSecdealMapper interfTradSecdealMapper;


    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。 {@inheritDoc}
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
     * @param
     * @see
     * @since 1.0
     */
    public void prepareEntity(PreTrdBondInvestTrade002Dto input) {
    	
    	//组合信息处理
    	input.setTrdTransfenpeiDto(input.getBussTransfenpei().getTrdTransfenpeiDtoList());


    	//构建金融产品交易汇总业务流程Dto add by kongzhifang  ---start----
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal,input);
        wflTrdFinProductDealTotal.setFinprodId(wflTrdFinProductDealTotal.getFinprodId());
        wflTrdFinProductDealTotal.setBusiType(DictCodeListExt1.共通业务类型_债券交易预录入.getValue());
        wflTrdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        wflTrdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        wflTrdFinProductDealTotal.setBranch(new Long(0));
        wflTrdFinProductDealTotal.setSettleDate(input.getTradeDate());
        if (StringUtil.equals(input.getSettleSpeedText(),DictCodeList.清算速度_T_0.getValue())){
            wflTrdFinProductDealTotal.setSettleSpeed(new Long(0));
        }else if (StringUtil.equals(input.getSettleSpeedText(),DictCodeList.清算速度_T_1.getValue())){
            wflTrdFinProductDealTotal.setSettleSpeed(new Long(1));
        }
        if (StringUtil.isNotEmpty(input.getTradeDate())&&StringUtil.isNotEmpty(input.getEff_time())){
            wflTrdFinProductDealTotal.setEffTime(DateUtil.parse(DateUtil.format(input.getTradeDate()) + " "+ input.getEff_time() + ":00"));
        }
        wflTrdFinProductDealTotal.setTradeMarket(input.getTradeMarket());
        wflTrdFinProductDealTotal.setTradePlat(DictCodeListExt1.交易场所_银行间.getValue());
        wflTrdFinProductDealTotal.setTradeMarketMode(input.getTradeMarketMode());
        wflTrdFinProductDealTotal.setUnitInt(input.getUnitInt());
        wflTrdFinProductDealTotal.setUnitFprice(input.getUnitFprice());
        wflTrdFinProductDealTotal.setUnitCprice(input.getUnitCprice());
        wflTrdFinProductDealTotal.setParValue(trdBondInvestTrade002Service.getUnitPrice(input.getFinprodId(), input.getSettleDate()));
        wflTrdFinProductDealTotal.setCpriceAmt(input.getCpriceAmt());
        wflTrdFinProductDealTotal.setTradeAmt(input.getFpriceAmt());
        wflTrdFinProductDealTotal.setShareAmt(input.getTradeAmt().divide(new BigDecimal(100)));
        wflTrdFinProductDealTotal.setPrinAmt(input.getCpriceAmt());
        wflTrdFinProductDealTotal.setFpriceAmt(input.getFpriceAmt());
        wflTrdFinProductDealTotal.setIntAmt(input.getIntAmt());
        wflTrdFinProductDealTotal.setFeeAmtPay(BigDecimal.ZERO);
        wflTrdFinProductDealTotal.setFeeAmt(BigDecimal.ZERO);
        wflTrdFinProductDealTotal.setFeeAmtUnpay(BigDecimal.ZERO);
        if(StringUtil.isNotEmpty(input.getYield())) {
        	wflTrdFinProductDealTotal.setYield(input.getYield().divide(new BigDecimal(100)));
        }
        if(StringUtil.isNotEmpty(input.getExerYield())) {
        	wflTrdFinProductDealTotal.setExerYield(input.getExerYield().divide(new BigDecimal(100)));
        }
        wflTrdFinProductDealTotal.setSecManageAcctId(input.getSecAcctId());
        wflTrdFinProductDealTotal.setIsOutTrade(DictCodeListExt1.是否_是.getValue());
        wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdFinProductDealTotal.setIsCancel(DictCodeListExt1.是否_否.getValue());
        wflTrdFinProductDealTotal.setIsClean(DictCodeListExt1.是否_是.getValue());
        wflTrdFinProductDealTotal.setWorkflowId(input.getWorkflowId());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        input.setWflTrdFinProductDealTotal(wflTrdFinProductDealTotal);
        //构建金融产品交易汇总业务流程Dto add by kongzhifang  ---end----
        //构建金融产品交易汇总Dto add by kongzhifang  ---start----
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal,wflTrdFinProductDealTotal);
        input.setTrdFinProductDealTotal(trdFinProductDealTotal);
        //构建金融产品交易汇总Dto add by kongzhifang  ---end----

        //List<金融产品交易（预录入）业务流程dto>
        List<WflTrdProductDealPre> wflTrdProductDealPreList = new ArrayList<>();
        input.setWflTrdProductDealPreList(wflTrdProductDealPreList);
        //List<金融产品交易（预录入）业务流程附属表dto>
        List<WflTrdProductDealAddPre> wflTrdProductDealAddPreList = new ArrayList<>();
        input.setWflTrdProductDealAddPreList(wflTrdProductDealAddPreList);

        //List<金融产品交易（预录入）dto>
        List<TrdProductDealPre> trdProductDealPreList = new ArrayList<>();
        input.setTrdProductDealPreList(trdProductDealPreList);
        //List<金融产品交易（预录入）附属表dto>
        List<TrdProductDealAddPre> trdProductDealAddPreList = new ArrayList<>();
        input.setTrdProductDealAddPreList(trdProductDealAddPreList);


        if (!StringUtil.isEmptyList(input.getBussTransfenpei().getTrdTransfenpeiDtoList())){
            for (TrdTransfenpeiDto trdTransfenpeiDto : input.getBussTransfenpei().getTrdTransfenpeiDtoList()){
                String tradeId = "";
                if (baseGetBusinessId != null) {
                    BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                    
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券类资产交易);
                   
                    tradeId = baseGetBusinessId.buildBussinessCode(codeParameter);
                } else {
                    tradeId = IDGenerator.generate();
                }
                //金融产品交易（预录入）业务流程dto
                WflTrdProductDealPre wflTrdProductDealPre = new WflTrdProductDealPre();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealPre,wflTrdFinProductDealTotal);
                wflTrdProductDealPre.setTradeId(tradeId);
                wflTrdProductDealPre.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                wflTrdProductDealPre.setCpriceAmt(trdTransfenpeiDto.getCpriceAmt());
                wflTrdProductDealPre.setShareAmt(trdTransfenpeiDto.getTradeAmt().divide(new BigDecimal(100)));
                wflTrdProductDealPre.setPrinAmt(trdTransfenpeiDto.getCpriceAmt());
                wflTrdProductDealPre.setIntAmt(trdTransfenpeiDto.getIntAmt());
                wflTrdProductDealPre.setFpriceAmt(trdTransfenpeiDto.getFpriceAmt());
                wflTrdProductDealPre.setTradeAmt(trdTransfenpeiDto.getFpriceAmt());
               
                //预录入交易回购质押品
//                for (PledgeBondInfoDto pledgeBondInfo : input.getPledgeBondListDetailed()){
//                    if (StringUtil.equals(pledgeBondInfoDto.getPortfolioId(),pledgeBondInfo.getPortfolioId())){
//                        FinDealPledgePre finDealPledgePre = new FinDealPledgePre();
//                        finDealPledgePre.setTradeId(tradeId);
//                        finDealPledgePre.setPledgeFinprodId(pledgeBondInfo.getBondCode());
//                        finDealPledgePre.setPledgeFaceValue(new BigDecimal(100));
//                        finDealPledgePre.setPledgeAmt(pledgeBondInfo.getPledgePrin());
//                        finDealPledgePre.setPledgeNumber(ArithmeticUtil.divide(pledgeBondInfo.getPledgePrin(),new BigDecimal(100),2));
//                        finDealPledgePre.setPledgeRatio(ArithmeticUtil.divide(pledgeBondInfo.getPledgeRatio(),new BigDecimal(100),2));
//                        finDealPledgePreList.add(finDealPledgePre);
//                        wflTrdProductDealPre.setFinprodId(pledgeBondInfo.getFinprodId());
//                    }
//                }
                //金融产品交易（预录入）业务流程附属表dto
                WflTrdProductDealAddPre wflTrdProductDealAddPre = new WflTrdProductDealAddPre();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAddPre,wflTrdProductDealPre);
                wflTrdProductDealAddPre.setTradeId(tradeId);
                wflTrdProductDealAddPre.setPortfolioId(trdTransfenpeiDto.getPortfolioId());
                wflTrdProductDealAddPre.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
                wflTrdProductDealAddPre.setEntrustId(trdTransfenpeiDto.getEntrustId());
                wflTrdProductDealAddPre.setRegDateAmt(trdTransfenpeiDto.getDoEntrustMoney());
                wflTrdProductDealAddPre.setWorkflowId(input.getWorkflowId());
                wflTrdProductDealAddPre.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                //金融产品交易（预录入）业务流程其他信息dto
//                WflTrdProductDealOthPre wflTrdProductDealOthPre = new WflTrdProductDealOthPre();
//                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealOthPre,wflTrdProductDealPre);
//                wflTrdProductDealOthPre.setTradeId(tradeId);
//                wflTrdProductDealOthPre.setPledgeFinprodId(wflTrdProductDealPre.getFinprodId());
//                wflTrdProductDealOthPre.setPledgeFinprodType(wflTrdProductDealPre.getFinprodType2());
//                wflTrdProductDealOthPre.setBasis(input.getBasis());
//                wflTrdProductDealOthPre.setRate(ArithmeticUtil.divide(pledgeBondInfoDto.getRebackPrice(), new BigDecimal("100"), 8));
//                wflTrdProductDealOthPre.setTerm(input.getRepoTerm());
//                wflTrdProductDealOthPre.setmTradeAmt(input.getTradeAmt());
//                wflTrdProductDealOthPre.setTermType(input.getTermType());
//                wflTrdProductDealOthPre.setWorkflowId(input.getWorkflowId());
//                wflTrdProductDealOthPre.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                //金融产品交易（预录入）dto
                TrdProductDealPre trdProductDealPre = new TrdProductDealPre();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealPre,wflTrdProductDealPre);
                trdProductDealPre.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                //金融产品交易（预录入）附属表dto
                TrdProductDealAddPre trdProductDealAddPre = new TrdProductDealAddPre();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealAddPre,wflTrdProductDealAddPre);
                //构建list集合
                wflTrdProductDealPreList.add(wflTrdProductDealPre);
                wflTrdProductDealAddPreList.add(wflTrdProductDealAddPre);
//                wflTrdProductDealOthPreList.add(wflTrdProductDealOthPre);
                trdProductDealPreList.add(trdProductDealPre);
                trdProductDealAddPreList.add(trdProductDealAddPre);
            }
        }
    }

    public PreTrdBondInvestTrade002Dto getPreTrdBondInvestTradeDto(String tradeId) {
        Map<String,Object> params = new HashMap<>();
        params.put("tradeId",tradeId);
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/PreTrdBondInvestTrade/getPreTrdBondInvestTradeDto",params, PreTrdBondInvestTrade002Dto.class);
    }
    

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(PreTrdBondInvestTrade002Dto entity) {

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }
     // 构建金融产品交易汇总业务流程Dto insert
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = entity.getWflTrdFinProductDealTotal();
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
        //List<金融产品交易（预录入）业务流程dto>
        List<WflTrdProductDealPre> wflTrdProductDealPreList = entity.getWflTrdProductDealPreList();
        mapperDelegateService.batchInsertSelective(WflTrdProductDealPreMapper.class,wflTrdProductDealPreList);
        //List<金融产品交易（预录入）业务流程附属表dto>
        List<WflTrdProductDealAddPre> wflTrdProductDealAddPreList = entity.getWflTrdProductDealAddPreList();
        mapperDelegateService.batchInsertSelective(WflTrdProductDealAddPreMapper.class,wflTrdProductDealAddPreList);
        //List<金融产品交易（预录入）业务流程其他信息dto>
        //预录入
//        List<TrdTransfenpeiDto> trdTransfenpeiDtoList = entity.getTrdTransfenpeiDto();
//        mapperDelegateService.batchInsertSelective(FinDealPledgePreMapper.class,trdTransfenpeiDtoList);
        //处理组合及指令头寸
        PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
        preTrdCommonDto.setTrdProductDealPreList(entity.getTrdProductDealPreList());
        preTrdCommonDto.setTrdProductDealAddPreList(entity.getTrdProductDealAddPreList());
        preTrdCommonBaseService.excutePosition(preTrdCommonDto,false);
        //调用授信共通
        List<TrdProductDealPre> trdProductDealPreList = entity.getTrdProductDealPreList();
        if (!StringUtil.isEmptyList(trdProductDealPreList)){
            trdProductDealPreList.forEach(item ->{
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, item);
                trdProductDeal.setfTradeId(item.getTradeId());
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setTrdProductDeal(trdProductDeal);
                AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
            });
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
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_FIN_PRODUCT_DEAL_TOTAL", "WORKFLOW_ID", workflowId);
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_FIN_DEAL_TOTAL", "WORKFLOW_ID", workflowId);
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_DEAL_TOTAL_ADD", "WORKFLOW_ID", workflowId);
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID", workflowId);

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD", "WORKFLOW_ID", workflowId);

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL", "WORKFLOW_ID", workflowId);

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID", workflowId);
    }

    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(PreTrdBondInvestTrade002Dto entity) {
        Map<String, Object> params = new HashMap<>();
        params.put("bizCode", entity.getTotleTradeId());
        params.put("workflowId", entity.getWorkflowId());
        params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
        params.put("user", SecurityUtil.getLoginUser().getUserName());
        params.put("dept", SecurityUtil.getLoginUser().getDeptCode());
        params.put("revokeReason", entity.getRevokeReason());
        params.put("time", new Date());
        //撤销流程中
        preTrdCommonBaseService.insertWflTable(params);
    }

    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(PreTrdBondInvestTrade002Dto entity) {
    	Map<String, Object> params = new HashMap<>();
        params.put("workflowId", entity.getWorkflowId());
        params.put("workflowStatus",DictCodeListExt1.数据状态_删除.getValue());
        params.put("bizCode", entity.getTotleTradeId());
        params.put("user", SecurityUtil.getLoginUser().getUserName());
        params.put("dept", SecurityUtil.getLoginUser().getDeptCode());
        params.put("time", new Date());
        params.put("revokeReason", entity.getRevokeReason());

        preTrdCommonBaseService.doRevokeComplete(params);
    }

    @Override
    public void doDelete(String workflowId){
        preTrdCommonBaseService.deletePosition(workflowId);
        deleteWflByWorkflowId(workflowId);
    }
    
    /**
     * 撤销/流程退回时额度占用、释放处理 Add by xkni 20190830
     *
     * @param entity 交易model
     * @param isNotBottomflag 是否是底层资产交易
     * @param isSave 是否入库
     * @param isdeleteflag 是否是作废删除
     * @return
     */
    private void isNeedOccupy(PreTrdBondInvestTrade002Dto entity, boolean isNotBottomflag, boolean isSave, Boolean isdeleteflag) {
        // 所有交易生效后调用的共通方法(入账信息) 撤销后释放占用的授信额度 Add 20190828 by xkni
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(entity.getPortfolioId());
        inputDto.setProdFinprodType(DictCodeList.共通业务类型_基金类资产交易.getValue());
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());// 交易状态
        trdProductDeal.setPs(entity.getPs());// 交易方向
        if (StringUtils.isNotBlank(entity.getfTradeId())) {
            trdProductDeal.setfTradeId(entity.getfTradeId());
        }
        trdProductDeal.setTradeId(entity.getTotleTradeId());// 交易编号
        trdProductDeal.setFinprodId(entity.getFinprodId());// 金融产品代码
        trdProductDeal.setCreateDept(entity.getCreateDept());// 创建部门
        trdProductDeal.setCreateUser(entity.getCreateUser());// 创建人
        trdProductDeal.setCreateTime(entity.getCreateTime());// 创建时间
        trdProductDeal.setUpdateUser(entity.getUpdateUser());// 更新人
        trdProductDeal.setUpdateTime(entity.getUpdateTime());// 更新时间
        trdProductDeal.setTradeDate(entity.getTradeDate());// 交易日期
        trdProductDeal.setShareAmt(entity.getShareAmt());// 交易份额，债券数量等
        trdProductDeal.setTradeAmt(entity.getTradeAmt());// 交易金额
        trdProductDeal.setPrinAmt(entity.getPrinAmt());// 交易本金，债券券面等
        trdProductDeal.setFinprodType(entity.getFinprodType());// 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
        trdProductDeal.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
//        TrdInvestPersonInfo002Dto personInfo = entity.getTrdInvestPersonInfo();
//        if (personInfo != null) {
//            // 交易对手
//            trdProductDeal.setCounterId(personInfo.getCounterId());// 交易对手编号
//        }
        if (!StringUtil.isNotEmpty(trdProductDeal.getShareAmt())) {
            // 交易份额 = 画面.券面总额/100
            trdProductDeal.setShareAmt(
                ArithmeticUtil.divide(trdProductDeal.getTradeAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        }
        inputDto.setTrdProductDeal(trdProductDeal);
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        ceLinesUpdate.isNeedOccupyCre(inputDto, isNotBottomflag, isSave, isdeleteflag);
    }

    @Override
    public void doInsert(PreTrdBondInvestTrade002Dto entity, OperationType operationType) {
        if (!BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            return;
        }
     // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            if (entity != null && StringUtil.equals("",entity.getWflTrdFinProductDealTotal().getWorkflowId())){
                entity.getWflTrdFinProductDealTotal().setWorkflowId(entity.getWorkflowId());
                entity.getWflTrdProductDealPreList().forEach(item ->{
                    item.setWorkflowId(entity.getWorkflowId());
                });
                entity.getWflTrdProductDealAddPreList().forEach(item -> {
                    item.setWorkflowId(entity.getWorkflowId());
                });
            }
            this.handleBussFlowData(entity);
        }// 撤销交易
        else {
            this.doRevokeInsert(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(PreTrdBondInvestTrade002Dto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            //删除组合及指令头寸
            preTrdCommonBaseService.deletePosition(entity.getWorkflowId());
            // 先删除流程中数据
            deleteWflByWorkflowId(entity.getWorkflowId());
            this.prepareEntity(entity);
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(PreTrdBondInvestTrade002Dto.class, true);
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
            // 重新insert
            this.handleBussFlowData(entity);
        }
    }

    

    /**
     * 更新CSTP的劵面总额
     * 
     * @param workflowId
     */
    @DataSource(DataSourceType.OTHER)
    @DTXTransaction
    public void uodateCstpMessage(String workflowId) {
        AllTradeCommonInputDto inputDto = restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectWflTrdProductDeal/" + workflowId,
            AllTradeCommonInputDto.class);
        // 处理cstp交易情况
        String fTradeId = inputDto.getTrdProductDeal().getfTradeId();
        if (StringUtils.isNotBlank(fTradeId)) {
            InterfTradSecdeal interfTradSecdeal = interfTradSecdealMapper.selectByPrimaryKey(fTradeId);
            if (interfTradSecdeal != null) {
                InterfTradSecdeal record = new InterfTradSecdeal();
                record.setDealno(fTradeId);
                BigDecimal prinamt = interfTradSecdeal.getPrinamt();
                BigDecimal subtract = ArithmeticUtil.doAdd(prinamt,
                    ArithmeticUtil.multiply(inputDto.getTrdProductDeal().getShareAmt(), new BigDecimal(100)));
                if (subtract.compareTo(BigDecimal.ZERO) == -1 || subtract.compareTo(BigDecimal.ZERO) == 0) {// 小于0的话
                    record.setPrinamt(BigDecimal.ZERO);
                }
                else {
                    record.setPrinamt(subtract);
                }
                interfTradSecdealMapper.updateByPrimaryKeySelective(record);
            }
        }
    }

    @Override
    public void doComplete(PreTrdBondInvestTrade002Dto entity, OperationType operationType) {
//        String tradeId = entity.getTotleTradeId();
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
        	TrdFinProductDealTotal TrdFinProductDealTotal = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(TrdFinProductDealTotal,entity.getTrdFinProductDealTotal());
            TrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
            // 是否撤销入否
            TrdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            TrdFinProductDealTotal.setBusinessModel(entity.getBusinessModel());
            //插入交易汇总表
            TrdFinProductDealTotalMapper.insertSelective(TrdFinProductDealTotal);

            //List<金融产品交易（预录入）dto>
            List<TrdProductDealPre> trdProductDealPreList = entity.getTrdProductDealPreList();
            mapperDelegateService.batchInsertSelective(TrdProductDealPreMapper.class,trdProductDealPreList);
            //List<金融产品交易（预录入）附属表dto>
            List<TrdProductDealAddPre> trdProductDealAddPreList = entity.getTrdProductDealAddPreList();
            mapperDelegateService.batchInsertSelective(TrdProductDealAddPreMapper.class,trdProductDealAddPreList);
            //预录入交易回购质押品
//            List<FinDealPledgePre> finDealPledgePreList = entity.getFinDealPledgePreList();
//            mapperDelegateService.batchInsertSelective(FinDealPledgePreMapper.class,finDealPledgePreList);
            //更新委托完成度
            PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
            preTrdCommonDto.setTrdProductDealPreList(entity.getTrdProductDealPreList());
            preTrdCommonDto.setTrdProductDealAddPreList(entity.getTrdProductDealAddPreList());
            preTrdCommonBaseService.updateEtrEntrustAmt(preTrdCommonDto,false);
        }
        // 撤销
        else {
            this.doCompleteRevoke(entity);
            //处理组合及指令头寸
            PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
            preTrdCommonDto.setTrdProductDealPreList(entity.getTrdProductDealPreList());
            preTrdCommonDto.setTrdProductDealAddPreList(entity.getTrdProductDealAddPreList());
            preTrdCommonBaseService.excutePosition(preTrdCommonDto,true);
            
          //更新委托完成度
            preTrdCommonBaseService.updateEtrEntrustAmt(preTrdCommonDto,true);
        }
       // 7.2 删除原业务流程表中的交易记录
        deleteWflByWorkflowId(entity.getWorkflowId());
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
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getEntrustInfo/" + entrustId, null,
            BaseEtrInvestInfoDto.class);
    }

    /**
     * 查询委托信息多组合
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public List<BaseEtrInvestInfoDto> getEntrustInfos(String entrustId) {
        List<BaseEtrInvestInfoDto> list = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getEntrustInfos/" + entrustId, null,
            BaseEtrInvestInfoDto.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (BaseEtrInvestInfoDto dto : list) {
                // 设置可委托金额
                dto.setDoEntrustMoney(
                    selectDoEntrustMoney(dto.getEtrEntrust().getEntrustId(), dto.getEtrEntrust().getPortfolioId()));
            }
        }
        return list;
    }

    public BigDecimal selectDoEntrustMoney(String entrustId, String portfolioId) {
        if (StringUtils.isNotBlank(entrustId) && StringUtils.isNotBlank(portfolioId)) {
            // 总的委托额度
            BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(entrustId);
            // 有效的和流程中的委托额度
            BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(entrustId, portfolioId, null);
            BigDecimal subtract = ArithmeticUtil.subtract(entrustNum, wflPrinAmt);
            return subtract;
        }
        return new BigDecimal(0);
    }

    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEntrustInfoTmp(String entrustId) {
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getEntrustInfo/" + entrustId, null,
            BaseEtrInvestInfoDto.class);
        Map<String, Object> map = new HashMap<>();
        map.put("oFinprodId", baseEtrInvestInfoDto.getEtrEntrust().getFinprodId());
        List<FinProduct> finProducts = restTemplate.postForList(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/Etrbondbiddingresult/findFinProductByOFinprodId", map, FinProduct.class);
        EtrEntrust etrEntrust = baseEtrInvestInfoDto.getEtrEntrust();
        if (finProducts != null && !finProducts.isEmpty()) {
            FinProduct finProduct = finProducts.get(0);
            etrEntrust.setFinprodId(finProduct.getFinprodId());
            baseEtrInvestInfoDto.setFinprodName(finProduct.getFinprodName());
        }
        etrEntrust.setTradeMarketMode("01");
        return baseEtrInvestInfoDto;
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
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId", parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String)parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String)parameters.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param parameters 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAccId(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId", parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String)parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String)parameters.get("portfolioId"));
                    // selectOptionDto.setId((String) parameters.get("portfolioId") + selectOptionDto.getId());//设置下拉框的id是组合代码加上债券账户的id
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }



    /**
     * 1.8计算单位面值
     *
     * @param finprodId 金融产品代码
     * @param settleDate 交割日
     * @return 单位面值
     * @see
     * @since 1.0
     */
    public BigDecimal getUnitPrice(String finprodId, Date settleDate) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("finprodId", finprodId);
        parameters.put("settleDate", DateUtil.format(settleDate));
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getUnitPrice", parameters,
            BigDecimal.class);
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
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/PreTrdBondInvestTrade/getFinprodId", parameters,
            SelectOptionDto.class);
    }
    /**
     * 资金账户下拉框
     * 
     * @param parameters
     * @return
     */
    public Page<SelectOptionDto> getacctNo(Map<String, Object> parameters) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/PreTrdBondInvestTrade/getacctNo", parameters,
            SelectOptionDto.class);
    }
    /**
     * 资金账户下拉框
     * 
     * @param parameters
     * @return
     */
    public Page<SelectOptionDto> getsecAcctId(Map<String, Object> parameters) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/PreTrdBondInvestTrade/getsecAcctId", parameters,
            SelectOptionDto.class);
    }
    /**
     * 
     * 
     * @param params
     * @return
     */
    public List<SelectOptionDto> getdeliveryType(Map<String, Object> params) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/PreTrdBondInvestTrade/getdeliveryType", params,
            SelectOptionDto.class);
    }
    /**
     * 
     * 
     * @param params
     * @return
     */
    public List<SelectOptionDto> getclearType(Map<String, Object> params) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/PreTrdBondInvestTrade/getclearType", params,
            SelectOptionDto.class);
    }
    // 资产推荐方 新增 zhuyongkang 20191023 start
    /**
     * 构建推荐方下拉框
     *
     * @param parameters 参数
     * @return 下拉框查询结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetRecommandOrg(Map<String, Object> parameters) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getAssetRecommandOrg",
            parameters, SelectOptionDto.class);
    }

    // 资产推荐方 新增 zhuyongkang 20191023 start
    

    /**
     * 判断某一天是否是债券行权日
     *
     * @param finprodId 金融产品代码
     * @param paramDate 判断日期
     * @return 判断结果
     * @see
     * @since 1.0
     */
    public boolean isExerciseDate(String finprodId, String paramDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodId", finprodId);
        paramMap.put("paramDate", paramDate);

        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isExerciseDate", paramMap,
            Boolean.class);
    }
 

 
    /**
     * 确认提醒 检查交割日是否为节假日，交割日如果是节假日 要提醒 交易方向为买入时校验交易所证券账户（券商存管账户）余额是否足够，如不足，提醒进行银证转账
     *
     * @param inputDto 画面输入参数
     * @param result valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirm(PreTrdBondInvestTrade002Dto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }

        String confirmMsg = "";
        String changeLineMark = "<br/>";

       

        // 检查交割日是否为节假日，交割日如果是节假日 要提醒
        if (inputDto.getSettleDate() != null && StringUtil.isNotBlank(inputDto.getCalendarId())) {
            boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(inputDto.getSettleDate(),
                inputDto.getCalendarId());
            if (isDealCalendarHoliday) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("BUSS_COM_E_0039", getMessage("TrdBondInvestTrade002.settleDate"));
            }
        }

        if(inputDto.getSettleSpeedText()==null) {
        	confirmMsg +="清算速度不能为空";
        }
        
        if(inputDto.getYield()==null) {
        	confirmMsg +="到期收益率不能为空";
        }
        // 买入交易需求要求添加组合T日现金剩余现金校验 add by yjm 2019-10-20 end



//        if (!StringUtil.equals(inputDto.getOperateType(), "revoke")) {
//            // 委托场合时
//            if (StringUtil.isNotEmpty(inputDto.getEntrustId())) {
//                Map<String,Object> map = new HashMap<>();
//                map.put("portfolioId",inputDto.getPortfolioId()) ;
//                map.put("tradeDate",inputDto.getTradeDate()) ;
//                map.put("acctTypeList","A01,A07") ;
//                map.put("ccy",inputDto.getCcy()) ;
//                map.put("tradeDate",inputDto.getTradeDate()) ;
//                map.put("finprodId",inputDto.getFinprodId()) ;
//                String dateId =  null;
//                dateId = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class,
//                        inputDto.getFinprodId(), FinProduct.class).getCalendarId();
//                List<String> holidayList = dateCalService.getDealCalendarHoliday(inputDto.getTradeDate(), DateUtil.addDay(inputDto.getTradeDate(), 15), dateId);
//                map.put("tradeDateNext", DateUtil.format(dateCalService.calculationTradingDay(inputDto.getTradeDate(), 1, holidayList, "1")));
//                BigDecimal position = (BigDecimal)bussTrdAvailableCashQueryService.getAvailableCash(map).get("position") ;
//
//                Map<String,Object> sinal = tradeCommonService.getSppiInfoByY1(map) ;
//                if(StringUtil.equals("2",sinal.get("result"))&&StringUtil.equals("Y1",inputDto.getBusinessModel())){
//                    if(position!=null){
//                        confirmMsg += changeLineMark;
//
//                        confirmMsg +="交易净价不满足使权不重大的购入价条件："+sinal.get("bidPriceCon")+"（中债接口接入或人工维护的使权不重大的购入价条件），业务模式由 “收取合同现金流”调整为“出售”，请进行资产重分类！";
//                    }else{
//                        confirmMsg += changeLineMark;
//
//                        confirmMsg +="交易净价不满足使权不重大的购入价条件："+sinal.get("bidPriceCon")+"（中债接口接入或人工维护的使权不重大的购入价条件），业务模式由 “收取合同现金流”调整为“出售”！";
//                    }
//                }
//            }
//        }

        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMsg);
            return wr;
        }

        return null;
    }


    
    /**
     * 根据成交编号查询cstp交易信息
     *
     * @param
     * @return cstp交易信息
     */
    public TrdBondInvestTradeCSTPDto querySecCstpInfoByNo(String fedealno) {
        return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/querySecCstpInfoByNo?fedealno=" + fedealno
                                         + "&loginUserName=" + LoginUserUtil.getUserName(),
            TrdBondInvestTradeCSTPDto.class);
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
    public void doBack(PreTrdBondInvestTrade002Dto entity) {
        // 对Cstp交易处理
//        uodateCstpMessage(entity.getWorkflowId());
        // 根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
        // 卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
        if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
            // 交易方向为买入时，退回动作后 释放原占用的额度 Add by xkni 20190830
//            isNeedOccupy(entity, true, true, true);
        }

    }
    @Override
    public void doRevoke(PreTrdBondInvestTrade002Dto entity) {
        // 对Cstp交易处理
//        uodateCstpMessage(entity.getWorkflowId());
        // 根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
        // 卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
        if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
            // 交易方向为买入时，退回动作后 释放原占用的额度 Add by xkni 20190830
//            isNeedOccupy(entity, true, true, true);
        }

    }

    public PreTrdBondInvestTrade002Dto getPreTrdBondTradeDtoFromEtr(String entrustId) {
    	PreTrdBondInvestTrade002Dto preTrdBondInvestTrade002Dto = new PreTrdBondInvestTrade002Dto();
        com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto etrInvestInfoDto = baseEtrInvestInfoService.queryActiveMandate(entrustId);
        if (!StringUtil.isNotEmpty(etrInvestInfoDto)){
            return preTrdBondInvestTrade002Dto;
        }
        List<com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto> etrInvestInfoDtoList = new ArrayList<>();
        
        if(StringUtil.isEmptyList(etrInvestInfoDto.getBaseEtrInvestInfoDtoList())){
            etrInvestInfoDtoList.add(etrInvestInfoDto);
        }else {
        	etrInvestInfoDtoList = etrInvestInfoDto.getBaseEtrInvestInfoDtoList();
        }
        preTrdBondInvestTrade002Dto.setPs(etrInvestInfoDtoList.get(0).getEtrEntrust().getPs());
        preTrdBondInvestTrade002Dto.setFinprodId(etrInvestInfoDtoList.get(0).getEtrEntrust().getFinprodId());
        preTrdBondInvestTrade002Dto.setType9(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getAssetType1());
        preTrdBondInvestTrade002Dto.setCalculateField(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getCalculateField());

        //清算速度
        if (Long.compare(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getSettleSpreed(), new Long(0)) == 0){
        	preTrdBondInvestTrade002Dto.setSettleSpeedText(DictCodeList.清算速度_T_0.getValue());
        }else if (Long.compare(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getSettleSpreed(), new Long(1)) == 0){
        	preTrdBondInvestTrade002Dto.setSettleSpeedText(DictCodeList.清算速度_T_1.getValue());
        }

        if(StringUtil.isNotEmpty(preTrdBondInvestTrade002Dto.getCalculateField())){
            if(StringUtil.equals(preTrdBondInvestTrade002Dto.getCalculateField(),"price")){
                preTrdBondInvestTrade002Dto.setUnitCprice(etrInvestInfoDtoList.get(0).getEtrEntrust().getPrice());
            }
            else if(StringUtil.equals(preTrdBondInvestTrade002Dto.getCalculateField(),"yield")){
                preTrdBondInvestTrade002Dto.setYield(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getMatureYield().multiply(new BigDecimal("100")));
            }
            else if(StringUtil.equals(preTrdBondInvestTrade002Dto.getCalculateField(),"exerYield")){
        	    preTrdBondInvestTrade002Dto.setExerYield(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getExerYield().multiply(new BigDecimal("100")));
            }
        }else{
            preTrdBondInvestTrade002Dto.setUnitCprice(etrInvestInfoDtoList.get(0).getEtrEntrust().getPrice());
        }
//        preTrdBondInvestTrade002Dto.setUnitCprice(etrInvestInfoDtoList.get(0).getEtrEntrust().getPrice());
        preTrdBondInvestTrade002Dto.setCounterId(etrInvestInfoDtoList.get(0).getEtrEntrust().getCounterId());
        preTrdBondInvestTrade002Dto.setCounterTrader(etrInvestInfoDtoList.get(0).getEtrEntrust().getCounterTrader());
//        preTrdBondInvestTrade002Dto.setYield(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getMatureYield());
        preTrdBondInvestTrade002Dto.setSecManageAcctId(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getSecAcctId());
//        if(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getExerYield()!=null) {
//        	preTrdBondInvestTrade002Dto.setExerYield(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getExerYield());	
//        }
        preTrdBondInvestTrade002Dto.setCalendarId(etrInvestInfoDtoList.get(0).getEtrEntrust().getCalendarId());
        //交易金额
        BigDecimal amount = new BigDecimal(0);
        //组合信息
        List<TrdTransfenpeiDto> list = new ArrayList<TrdTransfenpeiDto>();
        for (com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto baseEtrInvestInfoDto : etrInvestInfoDtoList){
        	TrdTransfenpeiDto trdTransfenpeiDto = new TrdTransfenpeiDto();
        	trdTransfenpeiDto.setEntrustId(baseEtrInvestInfoDto.getEtrEntrust().getEntrustId());
        	trdTransfenpeiDto.setPortfolioId(baseEtrInvestInfoDto.getEtrEntrust().getPortfolioId());
            BigDecimal entrustAurAmt = preTrdCommonBaseService.getEntrustSurAmt(baseEtrInvestInfoDto.getEtrEntrust().getEntrustId());
            trdTransfenpeiDto.setDoEntrustMoney(entrustAurAmt);
            BigDecimal tradeAmt = baseEtrInvestInfoDto.getEtrEntrust().getEntrustAmt()==null?baseEtrInvestInfoDto.getEtrEntrust().getEntrustNum().multiply(new BigDecimal(100)):baseEtrInvestInfoDto.getEtrEntrust().getEntrustAmt();
            trdTransfenpeiDto.setTradeAmt(tradeAmt);
            amount = ArithmeticUtil.doAdd(amount, trdTransfenpeiDto.getTradeAmt());
            trdTransfenpeiDto.setBussTrdAvailableCashQueryavalibleCash(baseEtrInvestInfoDto.getEtrEntrust().getProAvailableCash());
            //组合可用现金
            if (bussTrdAvailableCashQueryService != null) {
                Map<String, Object> param = new HashMap<>();

                Date tradeDate = DateUtil.platDateFromCache();
                String clandarID = "03"; 
                List<String> holidayList = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDay(tradeDate, 15), clandarID);
                param.put("calendarId",clandarID);
                param.put("tradeDate",DateUtil.format(tradeDate));
                param.put("ccy", BussinessDictCodeList.币种_人民币.getValue());
                param.put("portfolioId",trdTransfenpeiDto.getPortfolioId());
                param.put("acctTypeList", DictCodeListExt1.资金账户类型_产品托管户.getValue());
                param.put("tradeDateNext", DateUtil.format(dateCalService.calculationTradingDay(tradeDate, 1, holidayList, "1")));
                Map<String,Object> resultMap = bussTrdAvailableCashQueryService.getAvailableCash(param);
                trdTransfenpeiDto.setBussTrdAvailableCashQueryavalibleCash(new BigDecimal(String.valueOf(resultMap.get("T_0"))));
                trdTransfenpeiDto.setBussTrdAvailableCashQueryavalibleCash1(new BigDecimal(String.valueOf(resultMap.get("T_1"))));
            }
            list.add(trdTransfenpeiDto);
        }
        BussTransDto bussTransfenpei = new BussTransDto();

        bussTransfenpei.setTrdTransfenpeiDtoList(list);
        preTrdBondInvestTrade002Dto.setBussTransfenpei(bussTransfenpei);
        preTrdBondInvestTrade002Dto.setTradeAmt(amount);
        return preTrdBondInvestTrade002Dto;
    }
    /**
     * 查询债券cstp交易下拉框
     *
     * @param param 输入参数
     * @return
     */
    public Page<SelectOptionDto> querySecCstpInfoList(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/querySecCstpInfoList", param,
            SelectOptionDto.class);
    }

    

    public BigDecimal getTongDaoCash(Map<String, Object> param) {
        param.put("platDate",iPlatformDate.getSysDate());
        BigDecimal bigDecimal = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getTongDaoCash", param, BigDecimal.class);
        return bigDecimal;
    }

    public BigDecimal getBankCash(Map<String, Object> param) {
        String secManageAcctId = (String)param.get("secManageAcctId");
        // 查询网点账号
        StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(secManageAcctId);
        if (stlSecAcctInfo != null) {
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询现金余额(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", (String)param.get("portfolioId"));
            paramMap.put("acctId", stlSecAcctInfo.getTrdAcctId());
            paramMap.put("tradeDate", iPlatformDate.getSysDateStr());
            paramMap.put("ccy", (String)param.get("ccy"));
            paramMap.put("calendarId", (String)param.get("calendarId"));
            // paramMap.put("acctType", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            // paramMap.put("finprodId", inputDto.getFinprodId());

            BigDecimal bigDecimal = bussTrdAvailableCashQueryService.getSecManageAvailableCash(paramMap);

            return bigDecimal;
        }
        return BigDecimal.ZERO;
    }

    public Boolean isAllTradeAmtByFTradeId(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isAllTradeAmtByFTradeId",
            map, Boolean.class);
    }

    /**
     * 查询交易页面债券的市值净价和摊销净价（吉林银行产品化需求）
     * 
     * @param params
     * @return
     * @see
     * @since 1.0 malinlin 2019.12.2
     */
    public SecCpriceInfoDto getCprice(Map<String, Object> params) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCprice", params,
            SecCpriceInfoDto.class);
    }

    public static BussinessCodeParameter getBussinessCodeParameter(String ps) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), ps)) {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
        }
        else {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
        }
        return codeParameter;
    }

    private String deleteWflByWorkflowId(String workflowId) {
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealPreMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealAddPreMapper.deleteByWorkflowId(workflowId);
        return Boolean.TRUE.toString();
    }
    
    
    /**
     * 1.7计算按钮点击事件
     *
     * @param inputDto 画面参数
     * @return 计算结果
     * @see
     * @since 1.0
     */
    public AjaxResult calculateData(TrdBondInvestTradeDto inputDto) {
        AjaxResult ajaxResult = new AjaxResult();

        // 校验输入参数
        List<ValidMessage> errorList = this.validCalcAmt(inputDto);
        if (!StringUtil.isEmptyList(errorList)) {
            ajaxResult.setErrorList(errorList);
            return ajaxResult;
        }

        // 计算逻辑
        this.calcAmt(inputDto);
        ajaxResult.setData(inputDto);

        return ajaxResult;
    }
    
    /**
     * 1.7计算按钮点击事件
     *
     * @param dto 画面输入参数
     * @see
     * @since 1.0
     */
    public void calcAmt(TrdBondInvestTradeDto dto) {
        // 先计算融入券
        if (dto.getIntegrateTradeAmt() != null && dto.getIntegrateTradeAmt().compareTo(BigDecimal.ZERO) > 0) {
            dto.setPrinAmt(dto.getIntegrateTradeAmt());
            calc(dto, true);
        }
        else {
            dto.setIntegrateIntAmt(BigDecimal.ZERO);
            dto.setIntegrateCpriceAmt(BigDecimal.ZERO);
            dto.setIntegrateFpriceAmt(BigDecimal.ZERO);
        }
        dto.setPrinAmt(dto.getTradeAmt());
        calc(dto, false);
    }
    /**
     * 1.7计算按钮点击事件 检查输入参数是否合法
     *
     * @param dto 画面输入参数
     * @return 检查结果
     * @see
     * @since 1.0
     */
    private List<ValidMessage> validCalcAmt(TrdBondInvestTradeDto dto) {
        List<ValidMessage> validMessageList = new ArrayList<ValidMessage>();
        // 1.交割日
        if (dto.getSettleSpeed() == null) {
            ValidMessage validMessage = new ValidMessage("settleSpreed",
                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.settleSpreed")));
            validMessageList.add(validMessage);
        }
        // 2.资产代码
        if (StringUtil.isBlank(dto.getFinprodId())) {
            ValidMessage validMessage = new ValidMessage("finprodId",
                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.finprodId")));
            validMessageList.add(validMessage);
        }
        // 3.劵面总额
        if (dto.getTradeAmt() == null) {
            ValidMessage validMessage = new ValidMessage("tradeAmt",
                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.tradeAmt")));
            validMessageList.add(validMessage);
        }

        if (StringUtil.isBlank(dto.getCalculateField())) {
            ValidMessage validMessage = new ValidMessage(
                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.calculateField")));
            validMessageList.add(validMessage);
        }
        else {
            switch (dto.getCalculateField()) {
                case "unitCprice":
                    if (dto.getUnitCprice() == null) {
                        ValidMessage validMessage = new ValidMessage("unitCprice",
                            getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.unitCprice")));
                        validMessageList.add(validMessage);
                    }
                    break;
                case "fpriceAmt":
                    if (dto.getFpriceAmt() == null) {
                        ValidMessage validMessage = new ValidMessage("fpriceAmt",
                            getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.fpriceAmt")));
                        validMessageList.add(validMessage);
                    }
                    break;
                case "yield":
                    if (dto.getYield() == null) {
                        ValidMessage validMessage = new ValidMessage("yield",
                            getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.yield")));
                        validMessageList.add(validMessage);
                    }
                    break;
                case "exerYield":
                    if (dto.getExerYield() == null) {
                        ValidMessage validMessage = new ValidMessage("exerYield",
                            getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.exerYield")));
                        validMessageList.add(validMessage);
                    }
                    // 根据行权收益率反算其他值的情况，若下一行权日净价未维护，则提示错误消息
                    else {
                        try {
                            Map<String, Object> tempParamMap = new HashMap<String, Object>();
                            tempParamMap.put("finprodId", dto.getFinprodId());
                            tempParamMap.put("ps", dto.getPs());
                            // d.获取债券基本资料信息
                            SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
                                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel", tempParamMap,
                                SecInfoModelDto.class);

                            if (secInfoModelDto != null && secInfoModelDto.getNextExeDate() != null
                                && secInfoModelDto.getNextExercisePrice() == null) {
                                ValidMessage validMessage = new ValidMessage("exerYield", getMessage("TRD_E_0015"));
                                validMessageList.add(validMessage);
                            }
                        }
                        catch (Exception e) {
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        // 债券到期日<=交割日时
        if (StringUtil.isNotEmpty(dto.getSettleDate()) && StringUtil.isNotEmpty(dto.getMdate())) {
            if (DateUtil.compareDate(dto.getMdate(), dto.getSettleDate()) <= 0) {
                validMessageList.add(new ValidMessage("settleDate", "交割日期必须小于资产到期日"));
            }
        }
        // 画面.交割日<债券起息日
        if (StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_二级市场.getValue())
            && StringUtil.isNotEmpty(dto.getVdate()) && StringUtil.isNotEmpty(dto.getSettleDate())) {
            if (DateUtil.compareDate(dto.getSettleDate(), dto.getVdate()) < 0) {
                validMessageList.add(new ValidMessage("settleDate", this.getMessage("BUSS_COM_E_0004",
                    this.getMessage("TrdBondInvestTrade002.settleDate"), this.getMessage("TrdBondInvestTrade002.vdate"))));
            }
        }
        return validMessageList;
    }
    
    private void calc(TrdBondInvestTradeDto dto, boolean 计算融入券) {
        if (DateUtil.compareDate(dto.getSettleDate(), dto.getVdate()) < 0) {
            dto.setSettleDate(dto.getVdate());
        }

        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = true;
        if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
            && !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket())) {
            // // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量
            // this.getUnitIntFromValution(dto);
            isJiaoYiSuo = false;
        }

        final int SCALE = 12;

        dto.setYield(ArithmeticUtil.divide(dto.getYield(), 100, 14));
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), 100, 14));

        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = null;
        PriceYieldInParameter priceYieldInParameter = null;
        YieldPriceOutParameter yieldPriceOutParameter = null;
        PriceYieldOutParameter priceYieldOutParameter = null;

        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        tempParamMap.put("finprodId", dto.getFinprodId());

        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
        // List<BondsIntReset> bondsIntResetList = transactionBond001Mapper.getInvRateQuo(dto);
        List<FinCashCalcRule> finCashCalcRuleList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectBondsIntResetList", tempParamMap,
            FinCashCalcRule.class);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinCashPlan", tempParamMap, FinCashPlanDto.class);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_利息.getValue())
                && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);
                intSchList.add(item);
            }
        }

        // d.获取债券基本资料信息
        // 计算行权收益率，非行权时，取大于当日的行权日
        tempParamMap.put("ps", dto.getPs());
        SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel", tempParamMap, SecInfoModelDto.class);
        SecInfoModel secInfoModel = toSecInfoModel(secInfoModelDto);
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 付息债的计息基础使用A_365F
            if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
                secInfoModel.setBasis(Basis.A_365F);
            }
        }

        // e.获取债券的还本计划信息
        List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }


        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        yieldPriceInParameter = new YieldPriceInParameter();
        yieldPriceInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        priceYieldInParameter = new PriceYieldInParameter();
        priceYieldInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        priceYieldInParameter.setSecInfoModel(secInfoModel);
        priceYieldInParameter.setSecIntScheduleList(invScheduleList);
        priceYieldInParameter.setSecPrinScheduleList(secPrinScheduleList);
        priceYieldInParameter.setPaperIrResetList(paperIrResetList);
        yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
            if (isNotFomartSec(intSchList, secInfoModelDto)) {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                priceYieldInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
            }
            else {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
            }
        }
        yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());
        priceYieldInParameter.setCalMethod(secInfoModel.getCalMethod());
        // 后续区间利息计算方案
        if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
        }
        else {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
        }

        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        // 交易方式为“一级市场”时，单位应计利息=应计利息总额=0
        if (StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_一级市场.getValue())) {
            dto.setUnitInt(accurInt);
            dto.setIntAmt(BigDecimal.ZERO);
            if (计算融入券) {
                dto.setIntegrateIntAmt(BigDecimal.ZERO);
            }
        }
        // 交易方式为“二级市场”时，单位应计利息 =e取得的百元面值应计利息/100*a取得的当前债券面额
        else if (StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_二级市场.getValue())) {
            // a.当前债券面额取得（根据还本计划计算）

            // 交易所债券特殊处理start----------------------------------------------------------------
            Date tempSettleDate = dto.getSettleDate();
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                // 付息债、贴现债、零息债，交割日+1
     
                // 行权-回售 行权-赎回 交割日不加1
                if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                    && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())) {
                    tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
                }
                // add by 张年 20190909 end
                // }
            }
            // 交易所债券特殊处理end----------------------------------------------------------------

            // a.当前债券面额取得（根据还本计划计算）
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("finprodId", dto.getFinprodId());
            // paraMap.put("settleDate", DateUtil.format(dto.getSettleDate()));
            paraMap.put("settleDate", DateUtil.format(tempSettleDate));
            BigDecimal curSecAmt = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCurSecAmt", paraMap, BigDecimal.class);

            AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
            // 计算日
            // accurIntCalInParameter.setCalDate(this.toLocalDate(dto.getSettleDate()));
            accurIntCalInParameter.setCalDate(toLocalDate(tempSettleDate));
            if (paperIrResetList != null && paperIrResetList.size() > 1) {
                // 多重利率则计算按浮动利率算
                secInfoModel.setIntRateType(IntRateType.浮动利率);
            }
            // 债券基本资料
            accurIntCalInParameter.setSecInfoModel(secInfoModel);
            // 付息计划
            accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
            // 利率重置信息
            accurIntCalInParameter.setPaperIrResetList(paperIrResetList);

            // 交易所债券特殊处理start----------------------------------------------------------------
            AccurIntCalOutParameter out = null;
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                // 付息债、贴现债、零息债，交割日+1
                // if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())
                // || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(), secInfoModelDto.getBondsType())
                // || StringUtil.equals(DictCodeList.息票品种_零息.getValue(), secInfoModelDto.getBondsType())) {
                // 付息计划截止日对应的利息
                Map<String, BigDecimal> intMap = new HashMap<>();
                if (null != invScheduleList && !invScheduleList.isEmpty()) {
                    for (SecIntSchedule intSche : invScheduleList) {
                        intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                    }
                }

                if (intMap.containsKey(accurIntCalInParameter.getCalDate().toString())
                    && !((Variety.贴现.equals(secInfoModel.getBondsType())) || Variety.零息.equals(secInfoModel.getBondsType()))) {
                    out = new AccurIntCalOutParameter();
                    out.setInterest(
                        intMap.get(accurIntCalInParameter.getCalDate().toString()).multiply(new BigDecimal("100")).divide(
                            curSecAmt, 12, RoundingMode.HALF_UP));
                    accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                    AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                    if (accurIntCalInParameter.getPaperIrResetList() != null
                        && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                        accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                    }
                    out = calculator2.calculate(accurIntCalInParameter);
                }
                // }
            }
            // 交易所债券特殊处理end----------------------------------------------------------------

            if (out != null) {
                accurInt = ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
            }
            else {
                // 从计算工厂中创建应计利息计算器
                AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
                // 计算利息
                AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
                accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
            }

            // 单位应计利息
            if (dto.getUnitInt() == null) {
                if (isJiaoYiSuo) {
                    dto.setUnitInt(
                        ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")),
                            RoundingMode.HALF_UP));
                }
                else {
                    dto.setUnitInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                        Integer.parseInt(StringUtils.isEmpty(
                            CacheUtil.getSysParam("SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam("SEC_INT_SCALE_BANK")),
                        RoundingMode.HALF_UP));
                }
            }
            // 应计利息总额 = 单位应计利息 * 券面总额/100
            if (计算融入券) {
                dto.setIntegrateIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, dto.getPrinAmt()), 100, 2));
            }
            dto.setIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, dto.getPrinAmt()), 100, 2));

            // add by wl 交易方向选择行权时，应计利息及利息总额为0
            if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dto.getPs())
                 || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), dto.getPs()))) {
                dto.setUnitInt(BigDecimal.ZERO);
                dto.setIntAmt(BigDecimal.ZERO);
                if (计算融入券) {
                    dto.setIntegrateIntAmt(BigDecimal.ZERO);
                }
            }
        }
        try {
            // 单位净价
            if (StringUtil.equals(dto.getCalculateField(), "unitCprice")) {
                dto.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
                    new BigDecimal(100), 2));
                if (计算融入券) {
                    dto.setIntegrateCpriceAmt(dto.getCpriceAmt());
                }
                // 全价总额 = 净价总额+ 利息总额
                dto.setFpriceAmt(ArithmeticUtil.doAdd(dto.getIntAmt(), dto.getCpriceAmt()));
                if (计算融入券) {
                    dto.setIntegrateFpriceAmt(ArithmeticUtil.doAdd(dto.getIntAmt(), dto.getCpriceAmt()));
                }
                // round（全价总额/券面总额，10）*100
                // 全价 = 单位应计利息 + 单位净价 (保持横向的一致性)
                dto.setUnitFprice(ArithmeticUtil.doAdd(dto.getUnitInt(), dto.getUnitCprice()));
            }
            // 全价总额
            else if (StringUtil.equals(dto.getCalculateField(), "fpriceAmt")) {
                // 单位全价 = 全价总额 * 100 / 券面总额
                dto.setUnitFprice(
                    ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getFpriceAmt(), new BigDecimal(100)), dto.getPrinAmt(), 8));
                dto.setCpriceAmt(ArithmeticUtil.subtract(dto.getFpriceAmt(), dto.getIntAmt()));
                if (计算融入券) {
                    dto.setIntegrateCpriceAmt(dto.getCpriceAmt());
                }
                // round（净价总额/券面总额，10）*100
                // 单位净价 = 全价-单位应计利息 (保持横向的一致性)
                dto.setUnitCprice(ArithmeticUtil.subtract(dto.getUnitFprice(), dto.getUnitInt()));
            }
            switch (dto.getCalculateField()) {
                // 单位净价
                case "unitCprice":
                    // 全价总额
                case "fpriceAmt":
                    // 全价总额==>到期收益率
                    yieldPriceInParameter.setFullPrice(dto.getUnitFprice()/* dto.getFpriceAmt() */);
                    yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                    dto.setYield(yieldPriceOutParameter.getMatYield());
                    if (secInfoModelDto.getNextExeDate() != null) {
                        // 全价总额==>行权收益率
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getExeYield());
                        }
                        else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 到期收益率
                case "yield":
                    // 到期收益率==>全价总额
                    priceYieldInParameter.setMatYield(dto.getYield());
                    priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
                    // dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                    dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                    // 全价总额==>行权收益率
                    if (secInfoModelDto.getNextExeDate() != null) {
                        yieldPriceInParameter.setFullPrice(dto.getUnitFprice()/* dto.getFpriceAmt() */);
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getExeYield());
                        }
                        else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 行权收益率
                case "exerYield":
                    // 行权收益率==>全价总额
                    if (secInfoModelDto.getNextExeDate() != null) {
                        priceYieldInParameter.setMatYield(dto.getExerYield());
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
                        dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                        if (计算融入券) {
                            dto.setIntegrateFpriceAmt(priceYieldOutParameter.getFullPrice());
                        }
                        // 全价总额==>到期收益率
                        yieldPriceInParameter.setFullPrice(dto.getFpriceAmt());
                        yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                        dto.setYield(yieldPriceOutParameter.getMatYield());
                    }
                    break;
                default:
                    break;
            }
            // 到期收益率 或 行权收益率
            if (StringUtil.equals(dto.getCalculateField(), "yield") || StringUtil.equals(dto.getCalculateField(), "exerYield")) {
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getPrinAmt()),
                    new BigDecimal(100), 2));
                if (计算融入券) {
                    dto.setIntegrateFpriceAmt(dto.getFpriceAmt());
                }
                // 净价总额 = 全价总额 - 应计利息总额
                dto.setCpriceAmt(ArithmeticUtil.subtract(dto.getFpriceAmt(), dto.getIntAmt()));
                if (计算融入券) {
                    dto.setIntegrateCpriceAmt(dto.getCpriceAmt());
                }
                // 单位净价 = round（净价总额/券面总额，10）*100
                // 单位净价 = 全价 - 单位应计利息
                dto.setUnitCprice(ArithmeticUtil.subtract(dto.getUnitFprice(), dto.getUnitInt()));
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

        if (dto.getYield() != null) {
            dto.setYield(ArithmeticUtil.multiply(dto.getYield(), new BigDecimal(100)));
        }
        if (dto.getExerYield() != null) {
            dto.setExerYield(ArithmeticUtil.multiply(dto.getExerYield(), new BigDecimal(100)));
        }
        // --------add by 裴家俊 2019/03/28 费用查询----start---
        try {
            BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
            inputDto.setTradeDate(dto.getTradeDate());
            inputDto.setTradeMarket(dto.getTradeMarket());
            inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_债券.getValue());
            inputDto.setPs(dto.getPs());// 交易方向
            inputDto.setFpriceAmt(dto.getFpriceAmt());// 全价总额
            inputDto.setPrinAmt(dto.getPrinAmt());// 券面总额
            inputDto.setIsGrade("1");
            inputDto.setType6(dto.getType6());// 资产四级分类
            if (StringUtil.equals(BussinessDictCodeList.交易场所_银行间.getValue(), dto.getTradeMarket())) {
                // 登记托管机构用证券托管户的登记托管机构
                String registOrg = restTemplate.getForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getRegistOrg?secAcctId="
                                                             + dto.getSecManageAcctId(),
                    String.class);
                inputDto.setRegistOrg(registOrg);
                // 债券一级市场无交易手续费，结算手续费仅上清所有。
                if (StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_一级市场.getValue())) {
                    if (StringUtil.equals(registOrg, DictCodeList.登记托管机构_上海清算所.getValue())) {
                        inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_结算手续费.getValue()));
                    }
                }
                else {
                    inputDto.setFeeTypeList(
                        Arrays.asList(BussinessDictCodeList.费用类型_交易手续费.getValue(), BussinessDictCodeList.费用类型_结算手续费.getValue()));
                }
                if (!StringUtil.isEmptyList(inputDto.getFeeTypeList())) {
                    List<TrdTradeFeeDetail> tradeFeeList = baseTradeFeeCommService.innerBankFeeDeal(inputDto);
                    if (dto.getTradeFeeList() == null) {
                        dto.setTradeFeeList(tradeFeeList);
                    }
                    else {
                        dto.getTradeFeeList().addAll(tradeFeeList);
                    }
                }

            }
            if ((StringUtil.equals(BussinessDictCodeList.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
                 || StringUtil.equals(BussinessDictCodeList.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket()))
                && StringUtils.isNotBlank(dto.getSecManageAcctId())) {
                inputDto.setSecAcctId(dto.getSecManageAcctId());// 画面.证券账户
                inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_佣金.getValue()));
                List<TrdTradeFeeDetail> tradeFeeList = baseTradeFeeCommService.bourseFeeDeal(inputDto);
                if (StringUtil.equals(inputDto.getType6(), DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue())) {
                    inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_印花税.getValue()));
                    inputDto.setSecAcctId(null);
                    List<TrdTradeFeeDetail> tradeFeeListBO2 = baseTradeFeeCommService.bourseFeeDeal(inputDto);
                    tradeFeeList.addAll(tradeFeeListBO2);
                }
                if (dto.getTradeFeeList() == null) {
                    dto.setTradeFeeList(tradeFeeList);
                }
                else {
                    dto.getTradeFeeList().addAll(tradeFeeList);
                }
            }

        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        // --------add by 裴家俊 2019/03/28 费用查询----end---
    }
	
}
