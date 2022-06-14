package com.joyintech.tams.cfets.manualsettle.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.service.trade.buyoutrepo.dao.TrdOutRightRepoTradeMapper;
import com.joyintech.tams.business.common.dto.CfetsBondPortfolioCashRequest;
import com.joyintech.tams.business.common.dto.CfetsBondPositionRequest;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.PositionRulePtlData;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.trd.repo.dao.CfetsIbuQuoteRepo004Mapper;
import com.joyintech.tams.cfets.manualsettle.dao.CfetsManualSettleMapper;
import com.joyintech.tams.cfets.manualsettle.dto.*;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbClosePosEtrRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbClosePositionRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPosPortfolioCashRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPosPortfolioSecRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPositionRequest;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoAutoRKService;
import com.joyintech.tams.cfets.trdquery.dao.CfetsTrdQueryMapper;
import com.joyintech.tams.cfets.trdquery.dto.CfetsPledgeInfoResponse;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdRepoTrade002Dto;
import com.joyintech.tams.cfets.trdquery.service.CfetsTrdBondInvestTrade002Service;
import com.joyintech.tams.cfets.trdquery.service.CfetsTrdFirstBondInvestTrade002Service;
import com.joyintech.tams.cfets.trdquery.service.CfetsTrdRepoTrade002Service;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.impl.BaseTradeProductDealCommServiceImpl;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 手工交收
 * @author sjj
 * @version 1.0 2021年11月12日
 * @see CfetsManualSettleService
 * @since 1.0
 */
@Service("CfetsManualSettleService")
public class CfetsManualSettleService extends BaseService<CfetsManualSettleDto> implements BaseProcessService<CfetsManualSettleDto> {

    private Logger logger = LoggerFactory.getLogger(CfetsManualSettleService.class);

    @Autowired
    private CfetsManualSettleMapper cfetsManualSettleMapper;

    @Autowired
    private IPlatformDate platformDateImpl;

    @Autowired
    private TrdBankManualSettleFlowMapper trdBankManualSettleFlowMapper;

    @Autowired
    private TrdBankManualSetDetailMapper trdBankManualSetDetailMapper;

    @Autowired
    private CfetsTrdQueryMapper cfetsTrdQueryMapper;

    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;

    @Autowired
    private CfetsIbuQuoteRepo004Mapper cfetsIbuQuoteRepo004Mapper;

    @Autowired
    private CfetsTrdBondInvestTrade002Service cfetsTrdBondInvestTrade002Service;

    @Autowired
    private CfetsTrdFirstBondInvestTrade002Service cfetsTrdFirstBondInvestTrade002Service;

    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;

    @Autowired
    private CfetsEtrIbRepoAutoRKService cfetsEtrIbRepoAutoRKService;
    @Autowired(required = false)
    private BaseTradeProductDealCommServiceImpl baseTradeProductDealCommServiceImpl;
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    @Autowired
    private TrdProductTempDealTotalMapper trdProductTempDealTotalMapper;
    @Autowired
    BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;
    @Autowired
    private RegFinProductDealMapper regFinProductDealMapper;

    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    @Autowired
    private PtlCashPositionFlowMapper ptlCashPositionFlowMapper;

    /**
     * 业务编号取得共通
     */
    @Autowired
    private BaseGetBussinessId baseGetBussinessIdimpl;

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private TrdOutRightRepoTradeMapper trdOutRightRepoTradeMapper;

    @Autowired
    private CfetsTrdRepoTrade002Service cfetsTrdRepoTrade002Service;

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;


    /**
     * 手工交收交收check
     * @param cfetsRequest
     * @return message
     */
    public AjaxResult checkManualSettleSave(CfetsManualSettleRequest cfetsRequest, BindingResult br) {
        AjaxResult ajaxResult = new AjaxResult();
        List<CfetsManualSettleDto> returnList = new ArrayList<CfetsManualSettleDto>();
        if(StringUtil.equals(cfetsRequest.getSelectedItem().get(0).getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())){
            for(CfetsManualSettleDto cfetsDto : cfetsRequest.getSelectedItem()) {
                //是否存在流程中的数据
                Boolean isInwflFlag = this.isInwfl(cfetsDto.getTradeId());
                //判断勾选记录交收状态是否为“待交收”，
                //①交易方向=买入 AND 业务类型 = 数据字典.FINPROD_TYPE_TG.债券 AND 清算状态！=“清算成功”
                //②交易方向 IN （到期，提前终止，逾期返售） AND 业务类型 = 数据字典.FINPROD_TYPE_TG.债券质押式正回购 AND 清算状态！=“清算成功”
                //③交易方向=到期 AND 业务类型  IN（ 数据字典.FINPROD_TYPE_TG.债券买断式正回购，债券借贷融入） AND 清算状态！=“清算成功”
                //④交易方向=买入 AND 业务类型 IN（数据字典.FINPROD_TYPE_TG.债券买断式逆回购 ，数据字典.FINPROD_TYPE_TG.债券质押式逆回购 )AND 清算状态！=“清算成功”
                //④交易方向=买入 AND 业务类型 IN（数据字典.FINPROD_TYPE_TG.债券买断式逆回购 ，数据字典.FINPROD_TYPE_TG.债券质押式逆回购 )AND 清算状态！=“清算成功”
                //①交易方向=买入 AND 业务类型 = 数据字典.FINPROD_TYPE_TG.一级投标债券 AND 清算状态！=“清算成功”
                if(!isInwflFlag
                   && StringUtil.equals(cfetsDto.getSettleStatus(), DictCodeListExt3.交收状态_未交收.getValue())
                   && StringUtil.equals(cfetsDto.getSettleDate(),DateUtil.format(platformDateImpl.getSysDate()))
                   && ((StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_买入.getValue()) && StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && StringUtil.equals(cfetsDto.getCleanStatus(), DictCodeListExt3.清算状态_清算成功.getValue()))
                   || ((StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_到期.getValue()) || StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_提前终止.getValue()) || StringUtil.equals(cfetsDto.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue()))
                      && StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue()) && StringUtil.equals(cfetsDto.getCleanStatus(), DictCodeListExt3.清算状态_清算成功.getValue()))
                   || (StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_到期.getValue()) && (StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券买断式正回购.getValue())
                       || StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券借贷融入.getValue())) && StringUtil.equals(cfetsDto.getCleanStatus(), DictCodeListExt3.清算状态_清算成功.getValue()))
                   || (StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_买入.getValue()) && (StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())
                      || StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) && StringUtil.equals(cfetsDto.getCleanStatus(), DictCodeListExt3.清算状态_清算成功.getValue()))
                   || (StringUtil.equals(cfetsDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券存续期交易.getValue()) ||StringUtil.equals(cfetsDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券行权交易.getValue())
                      || (StringUtil.equals(cfetsDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_现券交易.getValue()) && !StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_买入.getValue()))
                      || (StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue()) && StringUtil.equals(cfetsDto.getPs(), DictCodeListExt3.交易方向_正回购.getValue()))
                      || (StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue()) && StringUtil.equals(cfetsDto.getPs(), DictCodeListExt3.交易方向_逆回购.getValue()))
                      || (StringUtil.equals(cfetsDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券行权交易.getValue()) && StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))
                        )
                   || (StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())
                        && StringUtil.equals(cfetsDto.getCleanStatus(), DictCodeListExt3.清算状态_清算成功.getValue()))
                )){
                    returnList.add(cfetsDto);
                }
            }
        }else if(StringUtil.equals(cfetsRequest.getSelectedItem().get(0).getButtonType(), DictCodeListExt3.交收操作类型_交收失败.getValue())){
            for(CfetsManualSettleDto cfetsDto : cfetsRequest.getSelectedItem()) {
                if(StringUtil.equals(cfetsDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_债券一级申购.getValue())){
                    ajaxResult.setErrorList(Arrays.asList(new ValidMessage("一级申购不允许进行交收失败!")));
                    return ajaxResult;
                }
                if(StringUtil.equals(cfetsDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_债券一级投标.getValue())){
                    ajaxResult.setErrorList(Arrays.asList(new ValidMessage("一级投标不允许进行交收失败!")));
                    return ajaxResult;
                }
                //是否存在流程中的数据
                Boolean isInwflFlag = this.isInwfl(cfetsDto.getTradeId());
                //判断勾选记录交收状态是否为“待交收”
                if(!isInwflFlag && (StringUtil.equals(cfetsDto.getSettleDate(),DateUtil.format(platformDateImpl.getSysDate())))
                    &&(StringUtil.equals(cfetsDto.getSettleStatus(),DictCodeListExt3.交收状态_未交收.getValue())
                        &&(StringUtil.equals(cfetsDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_现券交易.getValue())
                            ||StringUtil.equals(cfetsDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_债券行权交易.getValue())
                            || StringUtil.equals(cfetsDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_质押式回购首期.getValue())
                        || StringUtil.equals(cfetsDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_质押式回购到期.getValue())
                            ))){
                    returnList.add(cfetsDto);
                }
            }

        }else if(StringUtil.equals(cfetsRequest.getSelectedItem().get(0).getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue())){
            for(CfetsManualSettleDto cfetsDto : cfetsRequest.getSelectedItem()) {
                //是否存在流程中的数据
                Boolean isInwflFlag = this.isInwfl(cfetsDto.getTradeId());
                if(StringUtil.equals(cfetsDto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && StringUtil.equals(cfetsDto.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())
                        && StringUtil.equals(cfetsDto.getCleanStatus(), DictCodeListExt3.清算状态_清算成功.getValue())){
                    FinProduct finProduct = finProductMapper.selectByPrimaryKey(cfetsDto.getFinprodId());
                    // 上市流通日不为空,则已经做过了上市流通 此时存在后续交易 交收回溯不可进行
                    if(StringUtil.isNotEmpty(finProduct.getCirculationDate())){
                        ajaxResult.setErrorList(Arrays.asList(new ValidMessage("存在后续交易，不允许进行交收回溯，请重新选择!")));
                        return ajaxResult;
                    }
                }
                //判断勾选记录交收状态是否为“已交收”
                if(!isInwflFlag && StringUtil.equals(cfetsDto.getSettleDate(),DateUtil.format(platformDateImpl.getSysDate()))
                        && StringUtil.equals(cfetsDto.getSettleStatus(),DictCodeListExt3.交收状态_已交收.getValue())){
                    returnList.add(cfetsDto);
                }
            }
        }

        ajaxResult.setData(returnList);

        //查询页面、按钮是否走流程
        Map<String, String> param = new HashMap<String, String>();
        param.put("pageId","CfetsManualSettle");
        param.put("element_id",cfetsRequest.getSelectedItem().get(0).getElementId());

        ajaxResult.setMessage(this.isNeedWorkflow(param));

        return ajaxResult;
    }

    /**
     * 判断选择的记录是否存在流程中
     * @param tradeId
     * @return Boolean
     */
    private Boolean isInwfl(String tradeId) {
        Boolean returnMsg =false;
        WflValidInputParameter wflValidInputParameter  = new WflValidInputParameter();
        wflValidInputParameter.setPageId("CfetsManualSettle");
        wflValidInputParameter.setBusinessDataPk1(tradeId);
        //查询流程中数据"01"
        wflValidInputParameter.setSeachMode("01");
        List<String> result = cfetsManualSettleMapper.getBusinessData(wflValidInputParameter);
        wflValidInputParameter.setPageId("CfetsManualSettleUpdate");
        List<String> resultu = cfetsManualSettleMapper.getBusinessData(wflValidInputParameter);
        if(!StringUtil.isEmptyList(result) || !StringUtil.isEmptyList(resultu)){
            returnMsg = true;//存在流程中数据
        }
        return returnMsg;
    }

    /**
     * 构建银行间手工交收信息
     * @param cfetsManualSettleDto
     * @return List<CfetsManualSettleInsertDto>
     */
    public List<CfetsManualSettleInsertDto> handleSettleData(CfetsManualSettleDto cfetsManualSettleDto) {

        List<CfetsManualSettleInsertDto> empList = new ArrayList<CfetsManualSettleInsertDto>();
        //手工交收入库dto
        CfetsManualSettleInsertDto cfetsManualSettleInsertDto = new CfetsManualSettleInsertDto();
        //银行间手工交收信息Entity
        TrdBankManualSettleInfo trdBankManualSettleInfo = this.createTrdBankManualSettleInfo(cfetsManualSettleDto);
        //银行间手工交收流水信息Entity
        TrdBankManualSettleFlow trdBankManualSettleFlow = this.createTrdBankManualSettleFlow(cfetsManualSettleDto,trdBankManualSettleInfo);
        //调用【成交确认明细查询】
        List<TrdBankManualSetDetail> listTrdBnakSetDetails = new ArrayList<TrdBankManualSetDetail>();
        List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleDto.getTradeId(),null);
        if (CollectionUtils.isEmpty(listTemp)) {
            listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleDto.getTradeId());
        }
        for(CfetsManualSettleDealInfo cfetsManualSettleDealInfo : listTemp) {
            //银行间手工交收明细Entiy
            TrdBankManualSetDetail trdBankManualSetDetail = new TrdBankManualSetDetail();
            //交收流水序号=调用【共通】生成
            trdBankManualSetDetail.setSeqNo(IDGenerator.generate());
            //交收编号=银行间手工交收信息Entiy.交收编号
            trdBankManualSetDetail.setSettleSeqNo(trdBankManualSettleInfo.getSettleSeqNo());
            //交收日期=平台日
            trdBankManualSetDetail.setSettleDate(platformDateImpl.getSysDate());
            //原业务编号=成交确认明细.交易编号
            trdBankManualSetDetail.setTradeId(cfetsManualSettleDealInfo.getTradeId());
            //金融产品代码=画面.资产代码
            trdBankManualSetDetail.setFinprodId(cfetsManualSettleDto.getFinprodId());
            //金融产品简称=画面.资产简称
            trdBankManualSetDetail.setFinprodAbbr(cfetsManualSettleDto.getFinprodAbbr());
            //组合代码=成交确认明细.投资组合代码
            trdBankManualSetDetail.setPortfolioId(cfetsManualSettleDealInfo.getPortfolioId());
            //交割数量=成交确认明细.交易份额
            trdBankManualSetDetail.setSettleNum(cfetsManualSettleDealInfo.getShareAmt());
            //交割金额=成交确认明细.交易金额
            trdBankManualSetDetail.setSettleAmt(cfetsManualSettleDealInfo.getTradeAmt());
            //业务类别=画面.投管业务类型
            trdBankManualSetDetail.setBusiType(cfetsManualSettleDto.getTgBusiType());

            //交收状态=数据字典.交收状态.已交收
            if(StringUtil.equals(cfetsManualSettleDto.getButtonType(),DictCodeListExt3.交收操作类型_交收.getValue())
                    || StringUtil.equals(cfetsManualSettleDto.getButtonType(),DictCodeListExt3.交收操作类型_交收修改.getValue())){
                trdBankManualSetDetail.setSettleStatus(DictCodeListExt3.交收状态_已交收.getValue());
            }else if(StringUtil.equals(cfetsManualSettleDto.getButtonType(),DictCodeListExt3.交收操作类型_交收失败.getValue())){
                trdBankManualSetDetail.setSettleStatus(DictCodeListExt3.交收状态_交收失败.getValue());
            }else if(StringUtil.equals(cfetsManualSettleDto.getButtonType(),DictCodeListExt3.交收操作类型_回溯.getValue())){
                trdBankManualSetDetail.setSettleStatus(DictCodeListExt3.交收状态_未交收.getValue());
            }
            listTrdBnakSetDetails.add(trdBankManualSetDetail);
        }

        cfetsManualSettleInsertDto.setTrdBankManualSettleInfo(trdBankManualSettleInfo);
        cfetsManualSettleInsertDto.setTrdBankManualSettleFlow(trdBankManualSettleFlow);
        cfetsManualSettleInsertDto.setListTrdBnakSetDetails(listTrdBnakSetDetails);
        cfetsManualSettleInsertDto.setCcy(cfetsManualSettleDto.getCcy());
        cfetsManualSettleInsertDto.setPs(cfetsManualSettleDto.getPs());
        cfetsManualSettleInsertDto.setTradeId(cfetsManualSettleDto.getTradeId());
        empList.add(cfetsManualSettleInsertDto);
        return empList;
    }

    /**
     * 构建银行间手工交收信息Entity
     * @param cfetsManualSettleDto
     * @param
     * @return
     */
    private TrdBankManualSettleInfo createTrdBankManualSettleInfo(CfetsManualSettleDto cfetsManualSettleDto) {
        TrdBankManualSettleInfo trdBankManualSettleInfo = new TrdBankManualSettleInfo();
        String buttonType = cfetsManualSettleDto.getButtonType();
        //首次交收、交收失败、首次修改（回溯后修改）
        if(StringUtil.isBlank(cfetsManualSettleDto.getSettleSeqNo())) {
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeListExt3.共通业务类型_手工交收);
            String settleSeqNo = autoPeriodCommonService.buildBussinessCode(parameter);
            //交收编号
            trdBankManualSettleInfo.setSettleSeqNo(settleSeqNo);
            //原业务编号
            trdBankManualSettleInfo.setTradeId(cfetsManualSettleDto.getTradeId());
            //金融产品代码
            trdBankManualSettleInfo.setFinprodId(cfetsManualSettleDto.getFinprodId());
            //交收时间=平台日
            trdBankManualSettleInfo.setSettleDate(platformDateImpl.getSysDate());
            //交收数量
            trdBankManualSettleInfo.setSettleNum(cfetsManualSettleDto.getSettleNum());
            //交收金额
            if(StringUtil.equals(cfetsManualSettleDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_现券交易.getValue())
                    && StringUtil.equals(cfetsManualSettleDto.getPs(),DictCodeList.交易方向_买入.getValue())){
            }else{
                trdBankManualSettleInfo.setSettleAmt(cfetsManualSettleDto.getSettleAmt());
            }
            //业务类别
            trdBankManualSettleInfo.setBusiType(cfetsManualSettleDto.getTgBusiType());
            //交收状态
            if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收.getValue()) ||StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收修改.getValue())){
                trdBankManualSettleInfo.setSettleStatus(DictCodeListExt3.交收状态_已交收.getValue());
            }else if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收失败.getValue())){
                trdBankManualSettleInfo.setSettleStatus(DictCodeListExt3.交收状态_交收失败.getValue());
                trdBankManualSettleInfo.setRemark(cfetsManualSettleDto.getRemark());
            }
        }else{
            //回溯、再次交收（回溯后交收）、首次修改（回溯后修改）
            trdBankManualSettleInfo.setSettleSeqNo(cfetsManualSettleDto.getSettleSeqNo());
            trdBankManualSettleInfo.setFinprodId(cfetsManualSettleDto.getFinprodId());
            trdBankManualSettleInfo.setSettleAmt(cfetsManualSettleDto.getSettleAmt());
            trdBankManualSettleInfo.setTradeId(cfetsManualSettleDto.getTradeId());
            //交收状态
            trdBankManualSettleInfo.setSettleStatus(DictCodeListExt3.交收状态_未交收.getValue());
            if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收.getValue())  ||StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收修改.getValue())){
                trdBankManualSettleInfo.setSettleStatus(DictCodeListExt3.交收状态_已交收.getValue());
            }else if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收失败.getValue())){
                trdBankManualSettleInfo.setSettleStatus(DictCodeListExt3.交收状态_交收失败.getValue());
                trdBankManualSettleInfo.setRemark(cfetsManualSettleDto.getRemark());
            }
        }
        return trdBankManualSettleInfo;
    }

    /**
     * 银行间手工交收流水信息Entity
     * @param cfetsManualSettleDto
     * @param trdBankManualSettleInfo 交收信息表
     * @param
     * @return
     */
    private TrdBankManualSettleFlow createTrdBankManualSettleFlow(CfetsManualSettleDto cfetsManualSettleDto,TrdBankManualSettleInfo trdBankManualSettleInfo) {
        TrdBankManualSettleFlow trdBankManualSettleFlow = new TrdBankManualSettleFlow();
        String buttonType = cfetsManualSettleDto.getButtonType();
        //交收流水编号=调用【共通】生成
        trdBankManualSettleFlow.setSeqNo(IDGenerator.generate());
        //交收编号=银行间手工交收信息Entiy.交收编号
        trdBankManualSettleFlow.setSettleSeqNo(trdBankManualSettleInfo.getSettleSeqNo());
        //交收时间=当前时间
        trdBankManualSettleFlow.setSettleTime(new Date());
        //交收数量=画面.交收数量
        trdBankManualSettleFlow.setSettleNum(cfetsManualSettleDto.getSettleNum());
        //交收金额=画面.交收金额
        if(StringUtil.equals(cfetsManualSettleDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_现券交易.getValue())
                && StringUtil.equals(cfetsManualSettleDto.getPs(),DictCodeList.交易方向_买入.getValue())){
        }else{
            trdBankManualSettleFlow.setSettleAmt(cfetsManualSettleDto.getSettleAmt());
        }
        //金融产品代码=画面.资产代码
        trdBankManualSettleFlow.setFinprodId(cfetsManualSettleDto.getFinprodId());
        //金融产品代码=画面.资产简称
        trdBankManualSettleFlow.setFinprodAbbr(cfetsManualSettleDto.getFinprodAbbr());
        //操作类型=交收
        if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收修改.getValue())){
            buttonType = DictCodeListExt3.交收操作类型_交收.getValue();
        }
        trdBankManualSettleFlow.setOperType(buttonType);

        return trdBankManualSettleFlow;
    }
    /**
     * 构建债券发送头寸信息
     * @param cfetsManualSettleRequest
     * @return
     */
    private void sendPositionMethod(CfetsManualSettleInsertDto cfetsManualSettleRequest,String buttonType) {
        //组装组合金额信息
        List<PositionRulePtlData> ptlList=new ArrayList<>();
        TrdBankManualSettleInfo trdBankManualSettleInfo = cfetsManualSettleRequest.getTrdBankManualSettleInfo();
        if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_回溯.getValue()) || StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收.getValue()) ||
               StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收修改.getValue())|| StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收失败.getValue())){
            List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId(),null);
            if (CollectionUtils.isEmpty(listTemp)) {
                listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
            }
            for(CfetsManualSettleDealInfo dto : listTemp) {
                PositionRulePtlData positionRulePtlData = new PositionRulePtlData();
                positionRulePtlData.setPortfolioId(dto.getPortfolioId());
                positionRulePtlData.setTradeId(dto.getTradeId());
                positionRulePtlData.setEtrId(dto.getEntrustId());
                if(StringUtil.isNotEmpty(dto.getPortfolioId())){
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                    map.put("ccy", dto.getCcy());
                    String cashAcctId = this.qryPortfolioCashAcctId(map);
                    if (StringUtil.isNotEmpty(cashAcctId)) {
                        positionRulePtlData.setCashAcctId(cashAcctId); // 托管户
                    }
                }
                if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("finprodId", dto.getFinprodId());
                    String secAcctId = this.qryPortfolioSecAcctId(map);
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                    }
                }
                positionRulePtlData.setSecAmount(dto.getShareAmt());
                positionRulePtlData.setCashAmount(dto.getTradeAmt());
                positionRulePtlData.setInvAim(dto.getInvAim());
                positionRulePtlData.setOwnSecAmount(dto.getOurShareAmt() == null? BigDecimal.ZERO : ArithmeticUtil.multiply(dto.getOurShareAmt(),new BigDecimal(100)));
                positionRulePtlData.setIntegrateSecAmount(dto.getLendinShareAmt() == null? BigDecimal.ZERO : ArithmeticUtil.multiply(dto.getLendinShareAmt(),new BigDecimal(100)));
                ptlList.add(positionRulePtlData);
            }

            CfetsManualSettlePositionDto req = new CfetsManualSettlePositionDto();
            req.setInterfaceType("Bond001");
            //指令或交易标识	ETR:指令,TRD:交易
            req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
            //用户名	当前操作用户
            req.setUserId(SecurityUtil.getLoginUser().getUserName());
            //汇总交易编号	单交易时传交易编号
            if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_回溯.getValue())){
                req.setbTotalTradeId(trdBankManualSettleInfo.getSettleSeqNo());
            }else {
                req.setbTotalTradeId(trdBankManualSettleInfo.getTradeId());
            }
            //汇总交易编号
            req.setTotalTradeId(trdBankManualSettleInfo.getSettleSeqNo());
            req.setWorkFlowId(trdBankManualSettleInfo.getWorkflowId());
            //流程状态	流程节点 ，提交 审批通过
            req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
            //余额校验	Y：需校验   N：不需校验
            String balanceCheck="N";
            req.setBalanceCheck(balanceCheck);
            // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
            req.setBusiType(DictCodeListExt3.头寸业务类型_现券交易.getValue());
            if(StringUtil.isNotEmpty(cfetsManualSettleRequest.getPs())){
                if(StringUtil.equals(cfetsManualSettleRequest.getPs(),BussinessDictCodeList.交易方向_行权回售.getValue())
                    ||StringUtil.equals(cfetsManualSettleRequest.getPs(),BussinessDictCodeList.交易方向_行权赎回.getValue())){
                    req.setBusiType(DictCodeListExt3.头寸业务类型_债券行权交易.getValue());
                }else if(StringUtil.equals(cfetsManualSettleRequest.getPs(),BussinessDictCodeList.交易方向_到期.getValue())
                        ||StringUtil.equals(cfetsManualSettleRequest.getPs(),BussinessDictCodeList.交易方向_还本.getValue())
                        ||StringUtil.equals(cfetsManualSettleRequest.getPs(),BussinessDictCodeList.交易方向_收息.getValue())){
                    req.setBusiType(DictCodeListExt3.头寸业务类型_债券存续期交易.getValue());
                }
            }
            //交易场所
            req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
            //清算速度
            if(!StringUtil.isEmptyList(listTemp) && StringUtil.isNotEmpty(listTemp.get(0).getSettleSpeed())){
                req.setSettleSpeed(listTemp.get(0).getSettleSpeed().toString());
                if(listTemp.get(0).getSettleSpeed().compareTo(new Long(0))==0){
                    req.setSettleSpeed("01");
                }else if(listTemp.get(0).getSettleSpeed().compareTo(new Long(1))==0){
                    req.setSettleSpeed("02");
                }else if(listTemp.get(0).getSettleSpeed().compareTo(new Long(2))==0){
                    req.setSettleSpeed("03");
                }
            }else{
                req.setSettleSpeed("01");
            }
            //业务动作BUSI_ACT 09:交收确认 10:交收失败
            req.setBusiAct("09");
            if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收失败.getValue())){
                req.setBusiAct("10");
            }else if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_回溯.getValue())){
                req.setBusiAct("20");
            }
            //交易方向
            req.setPs(cfetsManualSettleRequest.getPs());
            //新建 修改 关闭
            req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setCdate(platformDateImpl.getSysDate());
            req.setCcy(cfetsManualSettleRequest.getCcy());
            req.setFinprodId(trdBankManualSettleInfo.getFinprodId());
            req.setPtlList(ptlList);
            PositionHttpRequest.send(req);
        }
    }
    /**
     * 构建一级申购发送头寸信息
     * @param cfetsManualSettleRequest
     * @return
     */
    private void sendBidPositionMethod(CfetsManualSettleInsertDto cfetsManualSettleRequest,String buttonType) {
        //组装组合金额信息
        List<CfetsBondPortfolioCashRequest> ptlList = new ArrayList<>();
        TrdBankManualSettleInfo trdBankManualSettleInfo = cfetsManualSettleRequest.getTrdBankManualSettleInfo();
        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) || StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) ||
                StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收修改.getValue()) || StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId(), null);
            if (CollectionUtils.isEmpty(listTemp)) {
                listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
            }
            for (CfetsManualSettleDealInfo dto : listTemp) {
                CfetsBondPortfolioCashRequest positionRulePtlData = new CfetsBondPortfolioCashRequest();
                positionRulePtlData.setPortfolioId(dto.getPortfolioId());
                positionRulePtlData.setTradeId(dto.getTradeId());
                positionRulePtlData.setEtrId(dto.getEntrustId());
                if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                    map.put("ccy", dto.getCcy());
                    String cashAcctId = this.qryPortfolioCashAcctId(map);
                    if (StringUtil.isNotEmpty(cashAcctId)) {
                        positionRulePtlData.setCashAcctId(cashAcctId); // 托管户
                    }
                }
                if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("finprodId", dto.getFinprodId());
                    String secAcctId = this.qryPortfolioSecAcctId(map);
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                    }
                }
                positionRulePtlData.setSecAmount(ArithmeticUtil.multiply(dto.getShareAmt(),new BigDecimal(100)));
                positionRulePtlData.setCashAmount(dto.getTradeAmt());
                positionRulePtlData.setOwnSecAmount(ArithmeticUtil.multiply(dto.getShareAmt(),new BigDecimal(100)).toPlainString());
                positionRulePtlData.setInvAim(dto.getInvAim());
                positionRulePtlData.setEtrId(dto.getEntrustId());
                ptlList.add(positionRulePtlData);
            }

            CfetsBondPositionRequest req = new CfetsBondPositionRequest();
            req.setInterfaceType("Bond001");
            //指令或交易标识	ETR:指令,TRD:交易
            req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
            //用户名	当前操作用户
            req.setUserId(SecurityUtil.getLoginUser().getUserName());
            //汇总交易编号	单交易时传交易编号
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue())) {
                req.setbTotalTradeId(trdBankManualSettleInfo.getSettleSeqNo());
            } else {
                req.setbTotalTradeId(trdBankManualSettleInfo.getTradeId());
            }
            //汇总交易编号
            req.setTotalTradeId(trdBankManualSettleInfo.getSettleSeqNo());
            req.setWorkFlowId(trdBankManualSettleInfo.getWorkflowId());
            //流程状态	流程节点 ，提交 审批通过
            req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
            //余额校验	Y：需校验   N：不需校验
            String balanceCheck = "N";
            req.setBalanceCheck(balanceCheck);
            //  必填,参数字典 BUSI_TYPE_POSITION
            req.setBusiType(DictCodeListExt3.头寸业务类型_债券一级申购.getValue());
            //交易场所
            req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
            //业务动作 BUSI_ACT 09:交收确认 10:交收失败
            req.setBusiAct(DictCodeListExt3.业务动作_交收确认.getValue());
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
                req.setBusiAct(DictCodeListExt3.业务动作_交收失败.getValue());
            } else if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue())) {
                req.setBusiAct(DictCodeListExt3.业务动作_交收回溯.getValue());
            }
            //交易方向
            req.setPs(cfetsManualSettleRequest.getPs());
            // 系统动作 新建 修改 关闭
            req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setCdate(platformDateImpl.getSysDate());
            req.setCcy(cfetsManualSettleRequest.getCcy());
            req.setFinprodId(trdBankManualSettleInfo.getFinprodId());
            req.setPtlList(ptlList);
            PositionHttpRequest.send(req);
        }
    }
    /**
     * 构建一级投标确认发送头寸信息
     * @param cfetsManualSettleRequest
     * @return
     */
    private void sendBidFirmPositionMethod(CfetsManualSettleInsertDto cfetsManualSettleRequest, String buttonType) {
        //组装组合金额信息
        List<CfetsBondPortfolioCashRequest> ptlList = new ArrayList<>();
        TrdBankManualSettleInfo trdBankManualSettleInfo = cfetsManualSettleRequest.getTrdBankManualSettleInfo();
        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) || StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) ||
                StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收修改.getValue()) || StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId(), null);
            if (CollectionUtils.isEmpty(listTemp)) {
                listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
            }
            String tgBusiType = null;
            for (CfetsManualSettleDealInfo dto : listTemp) {
                CfetsBondPortfolioCashRequest positionRulePtlData = new CfetsBondPortfolioCashRequest();
                positionRulePtlData.setPortfolioId(dto.getPortfolioId());
                positionRulePtlData.setTradeId(dto.getTradeId());
                positionRulePtlData.setEtrId(dto.getEntrustId());
                if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                    map.put("ccy", dto.getCcy());
                    String cashAcctId = this.qryPortfolioCashAcctId(map);
                    if (StringUtil.isNotEmpty(cashAcctId)) {
                        positionRulePtlData.setCashAcctId(cashAcctId); // 托管户
                    }
                }
                if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("finprodId", dto.getFinprodId());
                    String secAcctId = this.qryPortfolioSecAcctId(map);
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                    }
                }
                positionRulePtlData.setSecAmount(ArithmeticUtil.multiply(dto.getShareAmt(),new BigDecimal(100)));
                positionRulePtlData.setCashAmount(dto.getTradeAmt());
                positionRulePtlData.setOwnSecAmount(ArithmeticUtil.multiply(dto.getShareAmt(),new BigDecimal(100)).toPlainString());
                positionRulePtlData.setInvAim(dto.getInvAim());
                positionRulePtlData.setEtrId(dto.getEntrustId());
                ptlList.add(positionRulePtlData);
                tgBusiType = dto.getTgBusiType();
            }

            CfetsBondPositionRequest req = new CfetsBondPositionRequest();
            req.setInterfaceType("Bond001");
            //指令或交易标识	ETR:指令,TRD:交易
            req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
            //用户名	当前操作用户
            req.setUserId(SecurityUtil.getLoginUser().getUserName());
            //汇总交易编号	单交易时传交易编号
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue())) {
                req.setbTotalTradeId(trdBankManualSettleInfo.getSettleSeqNo());
            } else {
                req.setbTotalTradeId(trdBankManualSettleInfo.getTradeId());
            }
            //汇总交易编号
            req.setTotalTradeId(trdBankManualSettleInfo.getSettleSeqNo());
            req.setWorkFlowId(trdBankManualSettleInfo.getWorkflowId());
            //流程状态	流程节点 ，提交 审批通过
            req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
            //余额校验	Y：需校验   N：不需校验
            String balanceCheck = "N";
            req.setBalanceCheck(balanceCheck);
            //  必填,参数字典 BUSI_TYPE_POSITION
            req.setBusiType(DictCodeListExt3.头寸业务类型_债券一级投标.getValue());
            //交易场所
            req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
            //业务动作 BUSI_ACT 09:交收确认 10:交收失败
            req.setBusiAct(DictCodeListExt3.业务动作_交收确认.getValue());
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
                req.setBusiAct(DictCodeListExt3.业务动作_交收失败.getValue());
            } else if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue())) {
                req.setBusiAct(DictCodeListExt3.业务动作_交收回溯.getValue());
            }
            req.setPurchModle(DictCodeListExt4.头寸申购方式_自主投标.getValue());
            if (StringUtil.equals(tgBusiType, DictCodeListExt4.平安业务类型_债券分销买入确认.getValue())) {
                req.setPurchModle(DictCodeListExt4.头寸申购方式_分销买入.getValue());
            }
            //交易方向
            req.setPs(cfetsManualSettleRequest.getPs());
            // 系统动作 新建 修改 关闭
            req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setCdate(platformDateImpl.getSysDate());
            req.setCcy(cfetsManualSettleRequest.getCcy());
            req.setFinprodId(trdBankManualSettleInfo.getFinprodId());
            req.setPtlList(ptlList);
            PositionHttpRequest.send(req);
        }
    }
    /**
     * 构建回购发送头寸信息
     * @param cfetsManualSettleRequest
     * @return
     */
    private void sendRepoPositionMethod(CfetsManualSettleInsertDto cfetsManualSettleRequest,String buttonType) {
        //组装组合金额信息
        List<CfetsEtrIbRepoPosPortfolioCashRequest> ptlCashList = new ArrayList<>();
        List<CfetsEtrIbRepoPosPortfolioSecRequest> ptlSecList = new ArrayList<>();

        TrdBankManualSettleInfo trdBankManualSettleInfo = cfetsManualSettleRequest.getTrdBankManualSettleInfo();
        if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_回溯.getValue()) || StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收.getValue())
           ||StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收修改.getValue()) || StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收失败.getValue())){
            List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId(),null);
            if (CollectionUtils.isEmpty(listTemp)) {
                listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
            }
            for(CfetsManualSettleDealInfo dto : listTemp) {
                CfetsEtrIbRepoPosPortfolioCashRequest posport = new CfetsEtrIbRepoPosPortfolioCashRequest();
                posport.setPortfolioId(dto.getPortfolioId());
                posport.setEtrId(dto.getEntrustId());
                posport.setTradeId(dto.getTradeId());
                if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                    map.put("ccy", dto.getCcy());
                    String cashAcctId = this.qryPortfolioCashAcctId(map);
                    if (StringUtil.isNotEmpty(cashAcctId)) {
                        posport.setCashAcctId(cashAcctId); // 托管户
                    }
                }
                posport.setCashAmount(dto.getTradeAmt());
                ptlCashList.add(posport);
                List<PledgeBondInfoDto> pledgeBondList = cfetsManualSettleMapper.getPledgeListByTradeId(dto.getFinprodId());
                if (StringUtil.isNotEmpty(pledgeBondList)) {
                    for (PledgeBondInfoDto regFinPledge : pledgeBondList) {
                        CfetsEtrIbRepoPosPortfolioSecRequest secport = new CfetsEtrIbRepoPosPortfolioSecRequest();
                        secport.setPortfolioId(dto.getPortfolioId());
                        secport.setEtrId(dto.getEntrustId());
                        secport.setTradeId(dto.getTradeId());
                        secport.setFinprodId(regFinPledge.getFinprodId());
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("portfolioId", dto.getPortfolioId());
                        map.put("finprodId", regFinPledge.getFinprodId());
                        String secAcctId = this.qryPortfolioSecAcctId(map);
                        if (StringUtil.isNotEmpty(secAcctId)) {
                            secport.setSecAcctId(secAcctId); // 证券账户
                        }
                        secport.setSecAmount(ArithmeticUtil.multiply(regFinPledge.getPledgePrin(),new BigDecimal("100")));
                        secport.setOwnSecAmount(ArithmeticUtil.multiply(regFinPledge.getPledgePrin(),new BigDecimal("100")));
                        secport.setIntegrateSecAmount(BigDecimal.ZERO);
                        ptlSecList.add(secport);
                    }
                }
            }
            //调用头寸
            CfetsEtrIbRepoPositionRequest req = new CfetsEtrIbRepoPositionRequest();
            req.setInterfaceType("REPO001");
            req.setUserId(SecurityUtil.getLoginUser().getUserName());
            if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_回溯.getValue())){
                req.setbTotalTradeId(trdBankManualSettleInfo.getSettleSeqNo());
            }else {
                req.setbTotalTradeId(trdBankManualSettleInfo.getTradeId());
            }
            req.setTotalTradeId(trdBankManualSettleInfo.getSettleSeqNo());
            req.setWorkFlowId(trdBankManualSettleInfo.getWorkflowId());
            req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
            req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
            req.setRepoPledgeModel(CacheUtil.getSysParam("repo_pledge_model"));
            //余额校验 Y：需校验 N：不需校验
            String balanceCheck="N";
            req.setBalanceCheck(balanceCheck);
            req.setBusiType(cfetsManualSettleRequest.getListTrdBnakSetDetails().get(0).getBusiType());
            req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
            //清算速度
            if(!StringUtil.isEmptyList(listTemp) && StringUtil.isNotEmpty(listTemp.get(0).getSettleSpeed())){
                req.setSettleSpeed(listTemp.get(0).getSettleSpeed().toString());
                if(listTemp.get(0).getSettleSpeed().compareTo(new Long(0))==0){
                    req.setSettleSpeed("01");
                }else if(listTemp.get(0).getSettleSpeed().compareTo(new Long(1))==0){
                    req.setSettleSpeed("02");
                }else if(listTemp.get(0).getSettleSpeed().compareTo(new Long(2))==0){
                    req.setSettleSpeed("03");
                }
            }else{
                req.setSettleSpeed("01");
            }
            req.setBusiAct(DictCodeListExt3.业务动作_交收确认.getValue());
            //业务动作BUSI_ACT 09:交收确认 10:交收失败
            req.setBusiAct(DictCodeListExt3.业务动作_交收确认.getValue());
            if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_交收失败.getValue())){
                req.setBusiAct(DictCodeListExt3.业务动作_交收失败.getValue());
            }else if(StringUtil.equals(buttonType,DictCodeListExt3.交收操作类型_回溯.getValue())){
                req.setBusiAct("20");
            }
            String finprodType2=listTemp.get(0).getFinprodType2();
            if(StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())){
                req.setPs(DictCodeList.回购拆借方向_正回购.getValue());
            }else if(StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())){
                req.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
            }
            req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setCdate(platformDateImpl.getSysDate());
            req.setCcy(cfetsManualSettleRequest.getCcy());
            req.setPtlCashList(ptlCashList);
            req.setPtlSecList(ptlSecList);
            PositionHttpRequest.send(req);
        }
    }

    /**
     * 查询证券账户
     * @param param
     * @return
     */
    public String qryPortfolioSecAcctId(Map<String, Object> param) {
        return cfetsManualSettleMapper.qryPortfolioSecAcctId(param);
    }

    /**
     * 查询托管户
     * @param param
     * @return
     */
    public String qryPortfolioCashAcctId(Map<String, Object> param) {
        return cfetsManualSettleMapper.qryPortfolioCashAcctId(param);
    }

    @Override
    public void doBack(CfetsManualSettleDto entity) {
        if (StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())) {
            if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                if (!(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())
                        || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                )) {
                    //处理业务数据
                    List<CfetsManualSettleInsertDto> insertDtoLists = handleSettleData(entity);
                    //插入银行间手工交收相关表
                    for (CfetsManualSettleInsertDto cfetsManualSettleInsertDto : insertDtoLists) {
                        deletePosition(cfetsManualSettleInsertDto);
                    }
                }
            } else if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
            } else {
                //处理业务数据
                List<CfetsManualSettleInsertDto> insertDtoLists = handleSettleData(entity);
                //插入银行间手工交收相关表
                for (CfetsManualSettleInsertDto cfetsManualSettleInsertDto : insertDtoLists) {
                    this.insertOldRepoPosition(cfetsManualSettleInsertDto, entity.getButtonType(), entity.getSettleAmt(), entity.getFinprodType2(), true, entity.getTgBusiType(), true);
                }
            }

        }

    }

    private void deletePosition(CfetsManualSettleInsertDto cfetsManualSettleRequest) {
        //设置登记时的成交编号
        List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId(),null);
        if (CollectionUtils.isEmpty(listTemp)) {
            listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
        }
        for (CfetsManualSettleDealInfo item : listTemp) {
            AllTradeCommonUtil.deletePosition(null, item.getTradeId());
        }
    }

    @Override
    public void doUpdate(CfetsManualSettleDto entity) {
        //处理业务数据
        List<CfetsManualSettleInsertDto> insertDtoLists = handleSettleData(entity);
        //插入银行间手工交收相关表
        for (CfetsManualSettleInsertDto cfetsManualSettleInsertDto : insertDtoLists) {
            //发送头寸
            if ((StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())
                    || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue())
                    || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收失败.getValue())
                    || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收修改.getValue())) &&
                    StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {

                if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    if (!(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())
                            || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())
                            || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_行权_回售.getValue()))) {
                        //插入旧头寸
                        BigDecimal settleAmt = cfetsManualSettleInsertDto.getListTrdBnakSetDetails().stream().map(TrdBankManualSetDetail::getSettleAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                        this.insertOldPosition(cfetsManualSettleInsertDto, entity.getButtonType(), settleAmt, true);

                    }
                } else if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
                } else {
                    //对回购数据进行处理
                    if ((StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())
                            || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue()))) {
                        this.insertOldRepoPosition(cfetsManualSettleInsertDto, entity.getButtonType(), entity.getSettleAmt(), entity.getFinprodType2(), true, entity.getTgBusiType(), false);
                    }
                }
            }
        }
    }

    @Override
    public void doInsert(CfetsManualSettleDto entity, OperationType type) {
        //处理业务数据
        List<CfetsManualSettleInsertDto> insertDtoLists = handleSettleData(entity);
        //插入银行间手工交收相关表
        for (CfetsManualSettleInsertDto cfetsManualSettleInsertDto : insertDtoLists) {
            //发送头寸
            if ((StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())
                    || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue())
                    || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收失败.getValue())
                    || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收修改.getValue())) &&
                    StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {

                if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    if (!(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())
                            || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())
                            || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_行权_回售.getValue()))) {
                        //插入旧头寸
                        BigDecimal settleAmt = cfetsManualSettleInsertDto.getListTrdBnakSetDetails().stream().map(TrdBankManualSetDetail::getSettleAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                        this.insertOldPosition(cfetsManualSettleInsertDto, entity.getButtonType(), settleAmt, true);

                    }
                } else if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
                } else {
                    //对回购数据进行处理
                    if ((StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())
                            || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue())) ) {
                        this.insertOldRepoPosition(cfetsManualSettleInsertDto, entity.getButtonType(), entity.getSettleAmt(), entity.getFinprodType2(), true, entity.getTgBusiType(),false);
                    }
                }
            }
        }
    }


    @Override
    public void doComplete(CfetsManualSettleDto entity, OperationType type) {
        try {
            //更新交易表,将临时表数据复制到交易表
            this.updateDealData(entity);
            //处理业务数据
            List<CfetsManualSettleInsertDto> insertDtoLists = handleSettleData(entity);
            //插入银行间手工交收相关表
            for(CfetsManualSettleInsertDto cfetsManualSettleInsertDto : insertDtoLists) {
                //存在交收编号，说明是做过交收后又回溯，则做更新动作
                cfetsManualSettleMapper.mergeInfo(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo());
                //新增交收流水
                trdBankManualSettleFlowMapper.insertSelective(cfetsManualSettleInsertDto.getTrdBankManualSettleFlow());
                List<String> portfolioIds = new ArrayList<>();
                //新增交收明细
                for(TrdBankManualSetDetail trdBankManualSetDetail:cfetsManualSettleInsertDto.getListTrdBnakSetDetails()){
                    trdBankManualSetDetailMapper.insertSelective(trdBankManualSetDetail);
                    portfolioIds.add(trdBankManualSetDetail.getPortfolioId());
                }

                //发送头寸
                if(StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())
                        || StringUtil.equals(entity.getButtonType(),DictCodeListExt3.交收操作类型_回溯.getValue())
                        || StringUtil.equals(entity.getButtonType(),DictCodeListExt3.交收操作类型_交收失败.getValue())
                        ||StringUtil.equals(entity.getButtonType(),DictCodeListExt3.交收操作类型_交收修改.getValue())) {
                    if(StringUtil.equals(entity.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                        if ((StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())
                                || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue())
                                || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收失败.getValue()))
                                && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                            //插入旧头寸
                            BigDecimal settleAmt = cfetsManualSettleInsertDto.getListTrdBnakSetDetails().stream().map(TrdBankManualSetDetail::getSettleAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                            this.insertOldPosition(cfetsManualSettleInsertDto, entity.getButtonType(), settleAmt, false);
                        }
                        //对债券数据发送头寸
                        this.sendPositionMethod(cfetsManualSettleInsertDto,entity.getButtonType());
                    } else if (StringUtil.equals(entity.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())) {
                        // 一级申购发送头寸报文
                        this.sendBidPositionMethod(cfetsManualSettleInsertDto,entity.getButtonType());
                        this.insertOldBidPosition(cfetsManualSettleInsertDto, entity.getButtonType(), entity.getSettleAmt(),
                                entity.getFinprodType2(), false, entity.getTgBusiType(), false);
                    } else if (StringUtil.equals(entity.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
                        // 一级申购发送头寸报文
                        this.sendBidFirmPositionMethod(cfetsManualSettleInsertDto, entity.getButtonType());
                        this.insertOldBidPosition(cfetsManualSettleInsertDto, entity.getButtonType(), entity.getSettleAmt(),
                                entity.getFinprodType2(), false, entity.getTgBusiType(), false);
                    }else {
                        sendRepoPositionMethod(cfetsManualSettleInsertDto,entity.getButtonType());
                       //对回购数据进行处理
                        if ((StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())
                                || StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue())) ) {
                            this.insertOldRepoPosition(cfetsManualSettleInsertDto, entity.getButtonType(), entity.getSettleAmt(), entity.getFinprodType2(), false, entity.getTgBusiType(), false);
                        }
                    }
                }
                // 质押券库自动入库
                if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())) {
                    cfetsEtrIbRepoAutoRKService.autoInLibrary(SecurityUtil.getLoginUser().getUserName(),
                            cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getFinprodId(), portfolioIds, entity.getFinprodType2(), entity.getPs());
                }
                //交收失败后，自动撤销原交易、交易登记、行权登记数据，关闭对应指令
                if(StringUtil.equals(entity.getButtonType(),DictCodeListExt3.交收操作类型_交收失败.getValue())) {
                    String settleRemark = DictCodeListExt3.交收状态_交收失败.getCodeName();
                    String totleTradeId = cfetsManualSettleMapper.selectRegTotleTradeIdByTotleTradeId(entity.getTradeId());
                    if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                        CfetsTrdBondInvestTradeDto revokeEntity = this.getTrdBondInvestTradeDto(entity.getTradeId());
                        revokeEntity.setTgBusiType(entity.getTgBusiType());
                        cfetsTrdBondInvestTrade002Service.doCompleteRevoke(revokeEntity, settleRemark, StringUtil.isNotEmpty(totleTradeId));
                    } else if (StringUtil.equals(entity.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
                        CfetsTrdBondInvestTradeDto revokeEntity = this.getTrdBondInvestTradeDto(entity.getTradeId());
                        revokeEntity.setTgBusiType(entity.getTgBusiType());
                        cfetsTrdFirstBondInvestTrade002Service.doCompleteBidRevoke(revokeEntity, settleRemark, StringUtil.isNotEmpty(totleTradeId));
                    }else {
                        CfetsTrdRepoTrade002Dto revokeEntity = new CfetsTrdRepoTrade002Dto();
                        revokeEntity.setTotleTradeId(entity.getTradeId());
                        revokeEntity.setTgBusiType(entity.getTgBusiType());
                        revokeEntity.setPs(entity.getPs());
                        // 登记编号存在时,为登记,反之为报价
                        cfetsTrdRepoTrade002Service.doRevokeComplete(revokeEntity, settleRemark, StringUtil.isNotEmpty(totleTradeId));
                    }
                    //关闭对应指令
                    List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(entity.getTradeId(), "Y");
                    if (CollectionUtils.isEmpty(listTemp)) {
                        listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(entity.getTradeId());
                    }
                    List<String> entrustIdList = new ArrayList<>();
                    for(CfetsManualSettleDealInfo info:listTemp){
                        if(StringUtil.isNotEmpty(info.getEntrustId())){
                            entrustIdList.add(info.getEntrustId());
                        }
                    }
                    //List<String> entrustIdList = listTemp.stream().map(CfetsManualSettleDealInfo::getEntrustId).collect(Collectors.toList());
                    Map<String,Object> param = new HashMap<String,Object>();
                    param.put("entrustIds",entrustIdList);
                    // 行权赎回无指令，此处未考虑出现报错
                    if(StringUtil.isNotEmpty(entrustIdList.size()) && entrustIdList.size()>0
                            && cfetsManualSettleMapper.countWflEtr(param) <= 0){
                        for(CfetsManualSettleDealInfo dealInfo : listTemp){
                            this.closeDelegateMethod(dealInfo.getEntrustId(),"","交收失败");
                        }
                        SpringContextUtil.getApplicationContext().getBean(CfetsManualSettleService.class).closeDelegate(listTemp,entity.getRemark(),entity.getTgBusiType());
                    }
                }
            }
        } catch (PositionException e) {
            logger.error("交收失败-头寸处理异常",e);
            WorkFlowUtil.showMessage("头寸处理异常,请检查头寸日期", MessageType.错误消息);
            throw new PositionException(e);
        } catch(Exception e) {
            //e.printStackTrace();
            logger.error("交收失败",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新交易表数据
     * 处理逻辑：
     * 因为待交收数据来源不止一个，所以这里只处理temp表中存在数据的记录。
     * 交收成功：复制temp表数据到deal表中，如果是回溯之后交收则更新deal表的撤销状态为N
     * 交收回溯：更新deal表的数据为撤销状态Y
     * 交收失败：未回溯的交收失败则直接复制temp表数据到deal表，并更新状态为撤销Y,回溯的交收失败则更新状态为撤销Y即可
     */
    private void updateDealData(CfetsManualSettleDto entity) {
        TrdFinProductDealTotal tempTotal = trdProductTempDealTotalMapper.selectByPrimaryKey(entity.getTradeId());
        if(Objects.isNull(tempTotal)) return;
        TrdFinProductDealTotal total = trdFinProductDealTotalMapper.selectByPrimaryKey(entity.getTradeId());
        if(StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue())) {
            if(Objects.nonNull(total)) {
                autoPeriodCommonService.updateDealDataCancel(entity.getTradeId(),DictCodeList.是否_否.getValue());
            }else {
                autoPeriodCommonService.insertDealDataFromTemp(entity.getTradeId());
            }
        }
        if(StringUtil.equals(entity.getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue())) {
            autoPeriodCommonService.updateDealDataCancel(entity.getTradeId(),DictCodeList.是否_是.getValue());
        }
        if(StringUtil.equals(entity.getButtonType(),DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            if(Objects.isNull(total)) {
                autoPeriodCommonService.insertDealDataFromTemp(entity.getTradeId());
            }
            autoPeriodCommonService.updateDealDataCancel(entity.getTradeId(),DictCodeList.是否_是.getValue());
        }
    }

    /**
     * 查询页面、按钮是否走流程
     * @param param
     * @return
     */
    public String isNeedWorkflow(Map<String, String> param) {
        return joyinbusinessCommonService.isNeedWorkflow(param);
    }
    /**
     * 债券投资交易新建画面查看模式查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public CfetsTrdBondInvestTradeDto getTrdBondInvestTradeDto(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", tradeId);
        params.put("B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        params.put("Y", DictCodeList.是否_是.getValue());
        CfetsTrdBondInvestTradeDto resultDto = cfetsManualSettleMapper.getTrdBondInvestTradeDto(params);
        if (resultDto != null) {
            if (resultDto.getSettleSpeed() != null) {
                resultDto.setSettleSpeedStr(String.valueOf(resultDto.getSettleSpeed()));
            }
           /* else if(resultDto.getSettleSpeed() == 1L){无用的判断
                resultDto.setSettleSpeedStr("1");
            }else if(resultDto.getSettleSpeed() == 2L){
                resultDto.setSettleSpeedStr("2");
            }else{
                resultDto.setSettleSpeedStr(resultDto.getSettleSpeed().toString());
            }*/
            // 到期收益率
            if (resultDto.getYield() != null) {
                resultDto.setYield(ArithmeticUtil.multiply(resultDto.getYield(), BigDecimal.valueOf(100)));
            }
            // 行权收益率
            if (resultDto.getExerYield() != null) {
                resultDto.setExerYield(ArithmeticUtil.multiply(resultDto.getExerYield(), BigDecimal.valueOf(100)));
            }
            // 交易手续费
            TrdTradeFeeTotalKey trdTradeFeeTotalKey = new TrdTradeFeeTotalKey();
            trdTradeFeeTotalKey.setTotleTradeId(resultDto.getTotleTradeId());
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
            TrdTradeFeeTotal trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setProcedureAmt(trdTradeFeeTotal.getFeeAmt());
            }

            // 结算费服务费
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
            trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setSettleServiceAmt(trdTradeFeeTotal.getFeeAmt());
            }
        }
        return resultDto;
    }

    /**
     * 委托状态设置为“已关闭”
     * @param entrustId 委托代码
     * @see
     * @since 1.0
     */
    public boolean closeDelegateMethod(String entrustId,String closeWay,String settleRemark){
        EtrEntrust ee = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (ee != null) {
            // 调用投资委托组件的更新委托状态接口，将委托状态设置为“已关闭”
            String tempEntrustStatu = ee.getEntrustStatus();// 获取该委托原始状态，防止后续更改成关闭
            boolean flag = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeList.委托状态_已关闭.getValue());
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                //插入旧头寸
                if (baseEtrCashPositionCommService != null && DictCodeList.委托状态_已接收.getValue().equals(tempEntrustStatu)) {
                    baseEtrCashPositionCommService.entrustCloseExecute(entrustId,DictCodeList.是否_是.getValue());
                }
            }
            String remark = ee.getRemark() ==null?settleRemark:ee.getRemark().concat(settleRemark);
            EtrEntrust record =new EtrEntrust();
            record.setEntrustId(entrustId);
            int ii= etrEntrustMapper.updateByPrimaryKeySelective(record);
            // 判断关闭委托是否完成
            if (flag) {
                return true;
            }
        }
        return false;
    }

    /**
     * 委托状态设置为“已关闭”
     * @param listTemp
     * @see
     * @since 1.0
     */
    public CfetsEtribBondInfo003Dto closeDelegate(List<CfetsManualSettleDealInfo> listTemp, String remark, String tgBusiType) throws Exception {
        List<String> entrustIdList = listTemp.stream().map(CfetsManualSettleDealInfo::getEntrustId).collect(Collectors.toList());
        List<CfetsEtribBondInfo003Dto> dtoList = new ArrayList<>();
        // ZGZGST-2330:委托关闭判断是否有完成额度，分情况处理 fuyongsheng 20210919 edit start
        if(CollectionUtils.isNotEmpty(entrustIdList)){
            Map<String, BigDecimal> etrFinishMap = EntrustCompleteUtil.getEtrFinishAmtList(entrustIdList);
            //委托没有做过交易的，和撤销相同逻辑，删除预占
            List<CfetsManualSettleDealInfo> recallEntrustIdList = new ArrayList<>();
            //委托已有完成额度，做过交易，走关闭完成的逻辑，更新预占
            List<String> closeEntrustIdList = new ArrayList<>();
            for(CfetsManualSettleDealInfo etrDto : listTemp) {
                if(StringUtil.isNotEmpty(etrFinishMap) && StringUtil.isNotEmpty(etrFinishMap.get(etrDto.getEntrustId()))
                        && BigDecimal.ZERO.compareTo(etrFinishMap.get(etrDto.getEntrustId()))<0) {
                    closeEntrustIdList.add(etrDto.getEntrustId());
                }
                else if(entrustIdList.contains(etrDto.getEntrustId())){
                    recallEntrustIdList.add(etrDto);
                }
                CfetsEtribBondInfo003Dto tradeDto = new CfetsEtribBondInfo003Dto();
                BeanUtil.copyPropertiesIgnoreNull(tradeDto,etrDto);
                dtoList.add(tradeDto);
            }
/*
            //没有做过交易，直接关闭的
            if(!StringUtil.isEmptyList(recallEntrustIdList)) {
                // add by yushilong 批量撤销预占 start
                EntrustRdsCommonAop.doListPreOccupy(recallEntrustIdList, DictCodeList.平安资产类型_债券.getValue(),
                        BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
                // add by yushilong 批量撤销预占 end
            }

            //已经有委托完成度的
            if(!StringUtil.isEmptyList(closeEntrustIdList)) {
                EntrustCompleteUtil.doComplete(closeEntrustIdList);
            }*/

        }

        //发送头寸，批量关闭
        this.closeDelegatePositionMethod(listTemp,tgBusiType);
        //更新操作日志
        this.closeDelegatOprHisMethod(listTemp,remark);

        CfetsEtribBondInfo003Dto tradeDto = new CfetsEtribBondInfo003Dto();
        if (!StringUtil.isEmptyList(dtoList)){
            String finprodType2 = dtoList.get(0).getFinprodType2();
            if (StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                || StringUtil.equals(finprodType2,DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())){
                tradeDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            }else if (StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())){
                tradeDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
                tradeDto.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
            }else if (StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())){
                tradeDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
                tradeDto.setPs(DictCodeList.回购拆借方向_正回购.getValue());
            }
        }
        tradeDto.setSelectedItem(dtoList);
        return tradeDto;
    }

    /**
     * 更新操作日志
     * @param dtolist
     * @param remark
     * @see
     * @since 1.0
     */
    public void closeDelegatOprHisMethod(List<CfetsManualSettleDealInfo> dtolist,String remark){
        if(dtolist != null && dtolist.size()>0) {
            for (CfetsManualSettleDealInfo entity : dtolist) {
                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq=new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entity.getEntrustId());
                oprHisReq.setOperType(DictCodeListExt3.操作类型_关闭.getValue());
                oprHisReq.setBusiDes(remark);
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
            }
        }
    }

    public void closeDelegatePositionMethod(List<CfetsManualSettleDealInfo> dtolist,String tgBusiType){
        CfetsEtrIbClosePositionRequest req=new CfetsEtrIbClosePositionRequest();
        List<CfetsEtrIbClosePosEtrRequest> etrList=new ArrayList<>();
        for(CfetsManualSettleDealInfo t: dtolist){
            CfetsEtrIbClosePosEtrRequest closeItem=new CfetsEtrIbClosePosEtrRequest();
            String busiTypePosition=DictCodeListExt3.头寸业务类型_现券交易.getValue();
            if(StringUtil.isNotEmpty(t.getPs())
                    && StringUtil.equals(t.getPs(),BussinessDictCodeList.交易方向_行权回售.getValue())){
                busiTypePosition=DictCodeListExt3.头寸业务类型_债券行权交易.getValue(); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
            }else if(StringUtil.isNotEmpty(tgBusiType) && StringUtil.equals(tgBusiType,"05") || StringUtil.equals(tgBusiType,"06")) {
                busiTypePosition = tgBusiType;
            }

            closeItem.setBusiType(busiTypePosition);
            closeItem.setPs(t.getPs());
            closeItem.setCloseId(t.getEntrustId());
            closeItem.setSystemAct(DictCodeListExt3.系统动作_关闭.getValue());
            closeItem.setBusiAct(DictCodeListExt3.业务动作_指令手动关闭.getValue());
            etrList.add(closeItem);
        }
        req.setData(etrList);
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
        req.setTotalTradeId(IDGenerator.generate());
        req.setModelType(DictCodeListExt1.子系统代码_委托.getValue());
        PositionHttpRequest.batchClose(req);
    }

    /**
     * 交收确认时,重新组装插入旧头寸
     * @param cfetsManualSettleRequest
     * @param buttonType
     */
    public void insertOldPosition(CfetsManualSettleInsertDto cfetsManualSettleRequest, String buttonType,
                                  BigDecimal tradeAmt, boolean isFlowStart) {
        //设置登记时的成交编号
        List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId(),null);
        if (CollectionUtils.isEmpty(listTemp)) {
            listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
        }

        //交易份额 处理
        listTemp.stream().forEach(item -> {
            if (item.getShareAmt() != null) {
                item.setShareAmt(ArithmeticUtil.divide(item.getShareAmt(), new BigDecimal("100"), 0));
            }
        });
        //  处理组合可用持仓、可用现金
        if ((StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) &&
                (StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_卖出.getValue())
                        || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_行权_回售.getValue()))
        ) {

            //交收失败 生效 处理 可用
            List<String> orderIds = new ArrayList<>();
            //登记
            listTemp.stream().forEach(item -> {
                if (StringUtil.isNotEmpty(cfetsManualSettleMapper.selectRegTradeIdBytradeId(item.getTradeId()))) {
                    orderIds.add(cfetsManualSettleMapper.selectRegTradeIdBytradeId(item.getTradeId()));
                }
            });
            //对话，请求
            listTemp.stream().forEach(item -> {
                if (StringUtil.isNotEmpty(cfetsManualSettleMapper.selectIbTradeIdBytradeId(item.getTradeId()))) {
                    orderIds.add(cfetsManualSettleMapper.selectIbTradeIdBytradeId(item.getTradeId()));
                }
            });

            if (CollectionUtils.isNotEmpty(orderIds)) {
                orderIds.stream().forEach( orderId ->{
                    AllTradeCommonUtil.deleteRegPosition(null, orderId);
                });
                //baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(orderIds);
            }
            return;
        }

        if (!isFlowStart) {
            if (StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                    || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_到期.getValue())
                    || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
            ) {
                //登记、对话，请求 交收、交收回溯处理可用
                this.updatePtlPosition(listTemp, buttonType);
            }

        }

        TrdBankManualSettleInfo trdBankManualSettleInfo = cfetsManualSettleRequest.getTrdBankManualSettleInfo();
        StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
        if (trdBankManualSettleInfo != null) {
            List<StlPayOrdersDto> list = cfetsManualSettleMapper.getStlPayOrdersByTradeId(cfetsManualSettleRequest.getTradeId());
            stlPayOrdersDto = list.get(0);
        }
        //按照交易金额排序
        listTemp = listTemp.stream().sorted(Comparator.comparing(CfetsManualSettleDealInfo::getTradeAmt).reversed()).collect(Collectors.toList());
        BigDecimal orderAmt = stlPayOrdersDto.getSettleAmt();
        int index = 0;

        for (CfetsManualSettleDealInfo item : listTemp) {
            index++;
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) && !isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            }
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) && !isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            }
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) && isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            }

            finProductTradeDto.setNeedSum(true);
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            // 金融产品交易
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, item);
            if (StringUtil.isNotBlank(trdProductDeal.getfTradeId())) {
                baseTradeProductDealDto.setfTradeId(trdProductDeal.getfTradeId());
            }
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
            // 金融产品附属
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, item);
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
            baseTradeProductDealDto.setEntrustId(item.getEntrustId());
            //划款指令
            List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
            if (trdBankManualSettleInfo != null) {
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, stlPayOrdersDto);

                String acctId = EtrCommonCashPositionDealService.select组合托管户(item.getPortfolioId(), item.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                stlPayOrder.setMyAcctNo(acctId);

                BigDecimal settleAmt = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(item.getTradeAmt(), stlPayOrdersDto.getSettleAmt()), tradeAmt, 2);
                if (index == listTemp.size()) {
                    settleAmt = new BigDecimal(String.valueOf(orderAmt));
                }
                orderAmt = ArithmeticUtil.subtract(orderAmt, settleAmt);
                stlPayOrder.setSettleAmt(settleAmt);
                stlPayOrder.setCcy(item.getCcy());
                stlPayOrder.setOrderId(item.getTradeId());

                stlPayOrders.add(stlPayOrder);
            } else {
                stlPayOrders = cfetsManualSettleMapper.getStlPayOrdersByTradeId(item.getTradeId());
            }
            if (CollectionUtils.isNotEmpty(stlPayOrders)
                    && !DictCodeListExt1.投资目的_卖空.getValue().equals(trdProductDeal.getInvAim())) {
                baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
            }

            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
            baseTradeProductDealCommServiceImpl.insertPosition(finProductTradeDto);
        }


    }


    /**
     * 登记、对话，请求 交收、交收回溯处理可用
     * @param listTemp
     * @param buttonType
     */
    private void updatePtlPosition(List<CfetsManualSettleDealInfo> listTemp, String buttonType) {
        List<String> orderIdList = new ArrayList<>();

        //登记
        listTemp.stream().forEach(item -> {
            String tradeId = cfetsManualSettleMapper.selectRegTradeIdBytradeId(item.getTradeId());
            if(StringUtil.isNotEmpty(tradeId)) {
                orderIdList.add(tradeId);
            }
        });

        //对话，请求
        listTemp.stream().forEach(item -> {
            if (StringUtil.isNotEmpty(cfetsManualSettleMapper.selectIbTradeIdBytradeId(item.getTradeId()))) {
                orderIdList.add(cfetsManualSettleMapper.selectIbTradeIdBytradeId(item.getTradeId()));
            }
        });

        List<String> orderIds = orderIdList.stream().distinct().collect(Collectors.toList());

        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue())) {
            if (CollectionUtils.isNotEmpty(orderIds)) {
                AllTradeCommonUtil.deleteRegPosition(null, orderIds.get(0));
            }
        }
        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue())) {
            if (CollectionUtils.isNotEmpty(orderIds)) {
                List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
                List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();

                //报价
                orderIds.stream().forEach( orderId ->{
                    //查询报价信息 报价多组合会有多条数据
                    List<PtlFlowsDto> dealList = cfetsManualSettleMapper.selectIbQuoteBytradeId(orderId);
                    dealList.stream().forEach( quoteDeal ->{
                        PtlFlowsDto deal = new PtlFlowsDto();
                        if (quoteDeal != null) {
                            BeanUtil.copyPropertiesIgnoreNull(deal, quoteDeal);
                        }
                        if (StringUtil.isNotEmpty(deal.getTradeId())) {
                            String acctId = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                            //证券管理户代码
                            String secManageAcctId = "";
                            if (listTemp.size() > 0){
                                secManageAcctId = listTemp.get(0).getSecManageAcctId();
                            }
                            if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_卖出.getValue())
                                    || StringUtil.equals(deal.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                            ) {
                                ptlSecPositionFlows.add(positionDeal(deal, secManageAcctId));
                            }
                            if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue())) {
                                ptlCashPositionFlows.add(cashDeal(deal, acctId));
                            }
                        }
                    });

                });

                //登记
                orderIds.stream().forEach(orderId -> {
                    PtlFlowsDto deal = new PtlFlowsDto();
                    //查询登记信息
                    RegFinProductDeal regDeal = regFinProductDealMapper.selectByPrimaryKey(orderId);
                    if (regDeal != null) {
                        BeanUtil.copyPropertiesIgnoreNull(deal, regDeal);
                    }

                    if (StringUtil.isNotEmpty(deal.getTradeId())) {
                        String acctId = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                        //证券管理户代码
                        String secManageAcctId = "";
                        if (listTemp.size() > 0){
                            secManageAcctId = listTemp.get(0).getSecManageAcctId();
                        }
                        if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_卖出.getValue())
                                || StringUtil.equals(deal.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        ) {
                            ptlSecPositionFlows.add(positionDeal(deal, secManageAcctId));
                        }
                        if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue())) {
                            ptlCashPositionFlows.add(cashDeal(deal, acctId));
                        }
                    }

                });

                updatePtlFlows(ptlSecPositionFlows, ptlCashPositionFlows);
            }
        }
    }

    /**
     * 更新组合流水
     * @param ptlSecPositionFlows
     * @param ptlCashPositionFlows
     */
    private void updatePtlFlows(List<PtlSecPositionFlowDto> ptlSecPositionFlows, List<PtlCashPositionFlowDto> ptlCashPositionFlows) {
        if (CollectionUtils.isNotEmpty(ptlSecPositionFlows)) {
            ptlSecPositionFlows.stream().forEach(x -> {
                //调用业务编号共通组件获取流水号
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setSeqNo("9999");
                String seqNo = baseGetBussinessIdimpl.buildBussinessCode(parameter);
                x.setSeqno(seqNo);
                ptlSecPositionFlowMapper.insert(x);
            });
        }

        if (CollectionUtils.isNotEmpty(ptlCashPositionFlows)) {
            ptlCashPositionFlows.stream().forEach(x -> {
                //调用业务编号共通组件获取流水号
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setSeqNo("9999");
                String seqNo = baseGetBussinessIdimpl.buildBussinessCode(parameter);
                x.setSeqno(seqNo);
                ptlCashPositionFlowMapper.insert(x);
            });
        }
    }

    /**
     * 组装组合现金流水
     * @param deal
     * @param acctId
     * @return
     */
    private PtlCashPositionFlowDto cashDeal(PtlFlowsDto deal, String acctId) {
        PtlCashPositionFlowDto resultDto = new PtlCashPositionFlowDto();
        // 输入参数.组合代码
        resultDto.setPortfolioId(deal.getPortfolioId());
        // 划款指令Entity.本方账户
        resultDto.setAcctId(StringUtil.isEmpty(acctId) ? "999999" : acctId);
        // 输入参数.持仓类型
        resultDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        // 划款指令Entity.币种
        resultDto.setCcy(deal.getCcy());
        // 划款指令Entity.指令编号
        resultDto.setOrderId(deal.getTradeId());
        // 流程id
        resultDto.setEntrustId(deal.getEntrustId());
        resultDto.setCreateUser(deal.getCreateUser());
        resultDto.setCreateDept(deal.getCreateDept());
        resultDto.setUpdateUser(deal.getCreateUser());
        resultDto.setCdate(deal.getSettleDate());
        resultDto.setAmount(ArithmeticUtil.multiply(deal.getTradeAmt(), new BigDecimal(-1)));
        resultDto.setIsExistNew(DictCodeList.是否_是.getValue());

        return resultDto;

    }

    /**
     * 组装组合持仓流水
     * @param deal
     * @param acctId
     * @return
     */
    private PtlSecPositionFlowDto positionDeal(PtlFlowsDto deal, String acctId) {
        PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
        resultDto.setPortfolioId(deal.getPortfolioId());

        resultDto.setSecAcctId(StringUtil.isEmpty(acctId) ? "999999" : acctId);
        resultDto.setFinprodId(deal.getFinprodId());
        resultDto.setBranch(0L);
        resultDto.setInvAim(StringUtil.isEmpty(deal.getInvAim()) ? "999999" : deal.getInvAim());
        resultDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        resultDto.setCcy(deal.getCcy());
        resultDto.setTradeId(deal.getTradeId());
        resultDto.setCreateUser(deal.getCreateUser());
        resultDto.setCreateDept(deal.getCreateDept());
        resultDto.setUpdateUser(deal.getCreateUser());
        resultDto.setCdate(deal.getSettleDate());
        resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
        resultDto.setIsExistNew(DictCodeList.是否_是.getValue());
        return  resultDto;
    }

    /**
     * 交收确认时,重新组装质押式回购插入旧头寸
     * @author jiangpeng
     * @date 2022/3/14 10:23
     * @param cfetsManualSettleRequest
     * @param buttonType
     * @param tradeAmt
     * @param finprodType2 资产类型
     * @param isFlowStart 是否流程发起
     */
    public void insertOldRepoPosition(CfetsManualSettleInsertDto cfetsManualSettleRequest,
                                      String buttonType, BigDecimal tradeAmt, String finprodType2,
                                      boolean isFlowStart,String busiType,boolean isBack) {
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            logger.info("是否处理旧头寸标识为[否]不处理旧头寸");
            return;
        }
        if (!StringUtil.equals(busiType, DictCodeListExt3.头寸业务类型_质押式回购到期.getValue()) && isFlowStart) {
            logger.info("首期流程发起时不处理头寸");
            return;
        }
        logger.info("cfetsManualSettleRequest.getTradeId():" + cfetsManualSettleRequest.getTradeId());
        //设置登记时的成交编号
        List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId(),null);
        if (CollectionUtils.isEmpty(listTemp)) {
            listTemp = cfetsManualSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
        }
        // 正回购 交收失败 指令质押券可用持仓增加 组合质押券质押持仓减少
        // 正回购到期 交收 指令质押券可用持仓增加 组合质押券质押持仓减少 回溯反向处理
        if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
                && !isFlowStart && !isBack
                && ((StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())
                && StringUtil.equals(busiType, DictCodeListExt3.头寸业务类型_质押式回购首期.getValue()))
                || ((StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue())
                || StringUtil.equals(DictCodeListExt3.交收操作类型_回溯.getValue(), buttonType))
                && StringUtil.equals(busiType, DictCodeListExt3.头寸业务类型_质押式回购到期.getValue())))) {
            this.settleFailPosition(listTemp, busiType, StringUtil.equals(DictCodeListExt3.交收操作类型_回溯.getValue(), buttonType));
        }
        if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
                && StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            return;
        }
        StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
        List<StlPayOrdersDto> list = cfetsManualSettleMapper.getStlPayOrdersByTradeId(cfetsManualSettleRequest.getTradeId());
        // 首期只会按汇总编号生成一条汇总的划款指令
        if (CollectionUtils.isNotEmpty(list)) {
            stlPayOrdersDto = list.get(0);
        }
        BigDecimal orderAmt = stlPayOrdersDto.getSettleAmt();
        logger.info("orderAmt:" + orderAmt);
        listTemp = listTemp.stream().sorted((x, y) -> y.getTradeAmt().compareTo(x.getTradeAmt())).collect(Collectors.toList());
        int index = 0;
        List<String> orderIds = new ArrayList<>();
        for (CfetsManualSettleDealInfo item : listTemp) {
            index++;
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) && !isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            }
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) && !isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            }
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) && (isFlowStart || isBack)) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            }
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, item);
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            //表格质押券data
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("busiCode", item.getFinprodId());
            pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            pageParam.put("platDate", DateUtil.format(trdProductDeal.getTradeDate()));
            List<CfetsPledgeInfoResponse> cfetsPledgeInfoList = cfetsTrdQueryMapper.findRepoPledgeInfoList(pageParam);
            List<PledgeBondInfoDto> pledgeBondInfoDtoList = new ArrayList<>();
            cfetsPledgeInfoList.forEach(info -> {
                PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
                pledgeBondInfoDto.setBondCode(info.getFinprodMarketId());
                // 质押券面已按比例折算,且已万元转化 此处做还原操作
                pledgeBondInfoDto.setPledgePrin(ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(new BigDecimal(1000000), info.getPledgePrin()), info.getPledgeRate(), 4));
                pledgeBondInfoDto.setFinprodId(info.getFinprodId());
                pledgeBondInfoDtoList.add(pledgeBondInfoDto);
            });
            BussRepoSecDto bussRepoSecDto = new BussRepoSecDto();
            bussRepoSecDto.setPledgeBondList(pledgeBondInfoDtoList);
            //划款指令
            baseTradeProductDealDto.setBussRepoSec(bussRepoSecDto);
            List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
            if (StringUtil.equals(busiType, DictCodeListExt3.头寸业务类型_质押式回购到期.getValue())) {
                // 到期会按交易编号生成单独的划款指令
                stlPayOrders = cfetsManualSettleMapper.getStlPayOrdersByTradeId(item.getTradeId());
                logger.info("交收处理头寸,到期划款指令:" + JsonUtils.object2Json(stlPayOrders));
            } else {
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, stlPayOrdersDto);
                if (StringUtil.isEmpty(stlPayOrder.getMyAcctNo())) {
                    String acctId = EtrCommonCashPositionDealService.select组合托管户(item.getPortfolioId(), item.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                    stlPayOrder.setMyAcctNo(acctId);
                }
                BigDecimal settleAmt = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(item.getTradeAmt(), stlPayOrdersDto.getSettleAmt()), tradeAmt, 2);
                if (index == listTemp.size() && orderAmt != null) {
                    settleAmt = new BigDecimal(String.valueOf(orderAmt));
                }
                orderAmt = ArithmeticUtil.subtract(orderAmt, settleAmt);
                stlPayOrder.setSettleAmt(settleAmt);
                String orderId = cfetsManualSettleMapper.selectRegTradeIdBytradeId(item.getTradeId());
                if (StringUtil.isNotEmpty(orderId)) {
                    orderIds.add(orderId);
                }
                orderId = cfetsManualSettleMapper.selectQuoteIdBytradeId(item.getTradeId());
                if (StringUtil.isNotEmpty(orderId)) {
                    orderIds.add(orderId);
                }
                stlPayOrders.add(stlPayOrder);
                logger.info("交收处理头寸,首期期划款指令:"  + JsonUtils.object2Json(stlPayOrder));
            }
            if (StringUtil.isEmpty(baseTradeProductDealDto.getEntrustId())) {
                baseTradeProductDealDto.setEntrustId(cfetsManualSettleMapper.qryEntrustId(item.getFinprodId()));
            }
            if(isBack) {
                trdProductDeal.setPrinAmt(trdProductDeal.getPrinAmt() == null ? null : trdProductDeal.getPrinAmt().negate());
                trdProductDeal.setShareAmt(trdProductDeal.getShareAmt() == null ? null : trdProductDeal.getShareAmt().negate());
                trdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt() == null ? null : trdProductDeal.getTradeAmt().negate());
                stlPayOrders.forEach(ordersDto ->
                        ordersDto.setSettleAmt(ordersDto.getSettleAmt().negate())
                );
            }
            // 划款指令
            baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
            // 金融产品交易
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
            // 金融产品附属
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, item);
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

            baseTradeProductDealDto.setEntrustId(item.getEntrustId());

            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
            finProductTradeDto.setAssetType2(DictCodeList.回购类型_质押式回购.getValue());
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommServiceImpl.insertPosition(finProductTradeDto);
        }
        if (CollectionUtils.isNotEmpty(orderIds) && !isBack && isFlowStart) {
            baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(orderIds);
        }
    }
    /**
     * 更新质押position持仓
     * @param bizCode
     * @param holdingType
     * @param tradeId
     * @return
     */
    public long updatePledgeBondForRevoke(String bizCode, String holdingType, String tradeId, boolean isRevoke, Date settleDate) {
        if (StringUtil.isEmpty(tradeId)) {
            tradeId = bizCode;
        }
        Map<String, Object> param = setParams(bizCode, holdingType);
        List<PtlSecPositionFlow> ptlSecPositionFlows;
        ptlSecPositionFlows = 更新持仓(param, tradeId, isRevoke, settleDate);
        return ptlSecPositionFlows.size();
    }

    private static Map<String, Object> setParams(String bizCode, String holdingType) {
        Map<String, Object> param = new HashMap<>();
        param.put("bizCode", bizCode);
        if (StringUtil.isNotEmpty(holdingType) && !"null".equals(holdingType)) {
            param.put("POSITION_TYPE_ROLLIN", holdingType);
            param.put("POSITION_TYPE_ROLLOUT", holdingType);
        } else {
            param.put("POSITION_TYPE_ROLLIN", DictCodeList.持仓类型_融入.getValue());
            param.put("POSITION_TYPE_ROLLOUT", DictCodeList.持仓类型_融出.getValue());
        }
        return param;
    }

    private List<PtlSecPositionFlow> 更新持仓(Map<String, Object> param, String tradeId, boolean isRevoke, Date settleDate) {
        List<PtlSecPositionFlow> ptlSecPositionFlows = trdOutRightRepoTradeMapper.selectPtlSecPositionFlow(param);
        for (PtlSecPositionFlow flow : ptlSecPositionFlows) {
            PtlSecPosition position = new PtlSecPosition();
            if(StringUtil.isNotEmpty(settleDate)){
                flow.setCdate(settleDate);
            }
            BeanUtil.copyPropertiesIgnoreNull(position, flow);
            if (!isRevoke) {
                flow.setAmount(flow.getAmount().negate());
            }
            flow.setSeqno(IDGenerator.generate());
            flow.setTradeId(tradeId);
            position.setAmount(flow.getAmount().negate());
            ptlSecPositionFlowMapper.insert(flow);
            trdOutRightRepoTradeMapper.updatePosition(position);
        }
        return ptlSecPositionFlows;
    }

    /**
     * 交收失败时处理头寸信息
     *
     * @param listTemp
     * @author jiangpeng
     * @date 2022/3/11 15:51
     */
    private void settleFailPosition(List<CfetsManualSettleDealInfo> listTemp, String busiType, boolean isRevoke) {
        // 标记委托编号,避免重复操作
//        List<String> entrustIdList = new ArrayList<>();
//        for (CfetsManualSettleDealInfo deal : listTemp) {
//            if (!entrustIdList.contains(deal.getEntrustId())) {
//                cfetsRepoQuotedPrice004Service.revokeEtrSecPosition(deal.getEntrustId(), deal.getSettleDate(), isRevoke, deal.getTradeId());
//                entrustIdList.add(deal.getEntrustId());
//            }
//        }
        listTemp.forEach(deal -> {
                    String regId = cfetsManualSettleMapper.selectRegTradeIdBytradeId(deal.getFinprodId());
                    if (StringUtil.isEmpty(regId)) {
                        regId = cfetsManualSettleMapper.selectQuoteIdBytradeId(deal.getFinprodId());
                    }
                    this.updatePledgeBondForRevoke(regId, DictCodeList.持仓类型_质押.getValue(), deal.getTradeId(), isRevoke, deal.getSettleDate());
                }
        );
    }


    public void insertOldBidPosition(CfetsManualSettleInsertDto cfetsManualSettleRequest,
                                      String buttonType, BigDecimal tradeAmt, String finprodType2,
                                      boolean isFlowStart,String busiType,boolean isBack) {
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            return;
        }
        Set<String> totleTradeIds = new HashSet<>();
        //设置登记时的成交编号
        List<CfetsManualSettleDealInfo> listTemp = cfetsManualSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId(),null);
        for (CfetsManualSettleDealInfo deal : listTemp) {
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
                totleTradeIds.add(deal.getTradeId());
            } else {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue())) {
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                }else{
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                }
                finProductTradeDto.setAssetType2("ISSUE_CLASS_01");
                finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
                BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                baseTradeProductDealDto.setEntrustId(deal.getEntrustId());
                baseTradeProductDealDto.setStage("03");
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, deal);
                trdProductDeal.setFinprodId(deal.getFinprodId());
                trdProductDeal.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
                trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
                trdProductDeal.setSettleDate(deal.getTradeDate());
                trdProductDeal.setPrinAmt(deal.getShareAmt());
                trdProductDeal.setTradeDate(deal.getTradeDate());
                trdProductDeal.setSettleDate(deal.getSettleDate());
                trdProductDeal.setSecManageAcctId(deal.getSecManageAcctId());
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
                TrdProductDealAdd add = new TrdProductDealAdd();
                add.setPortfolioId(deal.getPortfolioId());
                add.setTradeId(deal.getTradeId());
                add.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                add.setEntrustId(deal.getEntrustId());
                baseTradeProductDealDto.setTrdProductDealAdd(add);
                //处理指令完成情况
                List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(deal.getFinprodId());
                String acctId = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), finProduct.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                stlPayOrder.setMyAcctNo(acctId);
                stlPayOrder.setOrderId(deal.getTotleTradeId());
                stlPayOrder.setCcy(finProduct.getCcy());
                stlPayOrder.setSettleAmt(deal.getTradeAmt());
                stlPayOrder.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                stlPayOrder.setPs(DictCodeListExt1.收支方向_支出.getValue());
                stlPayOrder.setSettleDate(deal.getTradeDate());
                stlPayOrder.setpTradeId(deal.getTotleTradeId());
                stlPayOrders.add(stlPayOrder);
                baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
                finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            }
        }
        // 一级投标 一级申购不可做交收失败
//        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
//            // 一级申购成交时可用现金 交收失败时需要冲销
//            baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(new ArrayList<String>(totleTradeIds));
//            // 一级申购成交时可用持仓 交收失败时需要冲销
//            for (String totleTradeId : totleTradeIds) {
//                baseAssetCashPositionCommMapper.deletePositionByTradeId(totleTradeId);
//                List<PtlSecPositionFlow> list = baseAssetCashPositionCommMapper.selectAllPosition(totleTradeId);
//                list.forEach(flow -> {
//                    PtlSecPositionFlowDto flowDto = new PtlSecPositionFlowDto();
//                    BeanUtil.copyPropertiesIgnoreNull(flowDto, flow);
//                    flowDto.setAmount(flow.getAmount().negate());
//                    flowDto.setOffsetId(flow.getTradeId());
//                    flowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
//                    flowDto.setTradeId(totleTradeId + "R");
//                    basePtlSecPositionInfoService.inertSecPositionFlow(flowDto);
//                });
//            }
//        }

    }
}
