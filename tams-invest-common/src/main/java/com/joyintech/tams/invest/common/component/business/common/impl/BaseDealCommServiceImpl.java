package com.joyintech.tams.invest.common.component.business.common.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.EtrFinishStatusMapper;
import com.joyintech.tams.common.base.dao.InterfOpicsTradRepoMapper;
import com.joyintech.tams.common.base.dao.InterfTradSecdealMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddPreMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealPreMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.EtrFinishStatusKey;
import com.joyintech.tams.common.base.entity.InterfOpicsTradRepo;
import com.joyintech.tams.common.base.entity.InterfTradSecdeal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAddPre;
import com.joyintech.tams.common.base.entity.TrdProductDealPre;
import com.joyintech.tams.common.base.entity.TrdProductDealPreKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.CommonResult;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.business.common.BaseAssetCashPositionCommService;
import com.joyintech.tams.invest.common.component.business.common.BaseDealCommService;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioCashPositionsFlowInputDto;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioPositionsFlowInputDto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealRevokeDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.component.business.period.service.impl
 * @Description: 金融产品交易共通 实现类
 * @date Date : 2018年09月27日 15:31
 */
@Service
public class BaseDealCommServiceImpl extends BaseService implements BaseDealCommService {

    /**
     * 业务共通组件.资产头寸现金持仓共通组件
     */
    @Autowired
    private BaseAssetCashPositionCommService assetPositionsCashCommServiceImpl;

    @Autowired
    private BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;

    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    /**
     * 持仓现金执行共通
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    @Autowired
    private InterfTradSecdealMapper interfTradSecdealMapper;
    @Autowired
    private InterfOpicsTradRepoMapper interfOpicsTradRepoMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    @Autowired
    private EtrFinishStatusMapper etrFinishStatusMapper;

    /**
     * BussCommonMapperDelegateService
     */
    @Autowired
    private TrdProductDealPreMapper trdProductDealPreMapper;
    @Autowired
    private TrdProductDealAddPreMapper trdProductDealAddPreMapper;

    private static final List<String> 认购确认_申购确认_赎回确认_除权登记_红利再投 = Arrays.asList(
            DictCodeList.交易方向_认购确认.getValue(), DictCodeList.交易方向_申购确认.getValue(),
            DictCodeList.交易方向_赎回确认.getValue(), DictCodeList.交易方向_除权登记.getValue(), DictCodeList.交易方向_红利再投.getValue());
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CommonResult execute(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto == null) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        //处理CSTP下行数据
        if(finProductTradeDto.getBaseTradeProductDealDtos() != null && !finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()
                && finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal() != null &&
                StringUtils.isNotBlank(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getfTradeId())){
            if(StringUtils.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(),DictCodeList.共通业务类型_债券类资产交易.getValue())){
                updateInterfForZhaiJuan(finProductTradeDto);
            }else if(StringUtils.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(),DictCodeList.共通业务类型_质押式回购交易.getValue())){
                updateInterfForZhiYaShi(finProductTradeDto);
            }
        }
        String workflowStatus = finProductTradeDto.getWorkflowStatus();
        if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(workflowStatus)) {
            return deal流程中(finProductTradeDto);
        }
        else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(workflowStatus)) {
            return deal有效(finProductTradeDto);
        }
        else if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(workflowStatus)) {
            return deal撤销(finProductTradeDto);
        }
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    private CommonResult deal撤销(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        if (!finProductTradeDto.isNeedSum()) {
            // 2.调用基础组件.【金融产品交易组件.撤销交易金融产品交易信息】，撤销金融产品交易信息
            List<BaseTradeProductDealRevokeDto> baseTradeProductDealRevokeDtos = new ArrayList<>();
            for (BaseTradeProductDealDto baseTradeProductDealDto : finProductTradeDto.getBaseTradeProductDealDtos()) {
                BaseTradeProductDealRevokeDto dto = new BaseTradeProductDealRevokeDto();
                dto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
                dto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
                dto.setStlPayOrdersList(baseTradeProductDealDto.getStlPayOrdersList());
                dto.setTrdFinProductBarterList(baseTradeProductDealDto.getTrdFinProductBarterList());
                dto.setTrdTradeFeeDetailList(baseTradeProductDealDto.getTrdTradeFeeDetailList());
                dto.setMstFileInputParameter(baseTradeProductDealDto.getMstFileInputParameter());
                dto.setMstFileOutputParameter(baseTradeProductDealDto.getMstFileOutputParameter());
                dto.setCancelRemark(baseTradeProductDealDto.getTrdProductDeal().getCancelRemark());
                dto.setpTradeId(baseTradeProductDealDto.getTrdProductDeal().getTradeId());
                dto.setActPortfolioId(baseTradeProductDealDto.getActPortfolioId());
                baseTradeProductDealRevokeDtos.add(dto);
            }
            Boolean result = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/revokeTradeInfo",
                baseTradeProductDealRevokeDtos, Boolean.class);
            if (!result) {
                return new CommonResult(CommonResult.FAILD, "处理失败");
            }
        }

        /*----------------------zhangliang----add------2019/02/23-------------start--------------------*/
        BaseTradeProductDealDto baseTradeDto = finProductTradeDto.getBaseTradeProductDealDtos().get(0);
        if (baseTradeDto.getTrdProductDeal() != null
        			&& StringUtil.equals(baseTradeDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_买断式回购交易.getValue())){
        	if (baseTradeDto.getSecAmount() != null){
        		baseTradeDto.getTrdProductDeal().setShareAmt(baseTradeDto.getSecAmount());
        	}
        }
        /*----------------------zhangliang----add------2019/02/23-------------end--------------------*/
        
        // 3.调用【组合可用持仓与实际持仓查询更新组件】，写入类型为[实际]的组合持仓流水
        if (!insertPosition(finProductTradeDto)) {
            return new CommonResult(CommonResult.FAILD, "处理失败");
        }
        // 调用基础组件.【投资委托组件.取消完成委托】，撤销委托完成状况信息(投资委托场合才执行此步)
        finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), true);
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    private CommonResult deal有效(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }

        /*----------------------裴家俊-----add------2018/11/23-------------start--------------*/
        BaseTradeProductDealDto baseTradeDto=finProductTradeDto.getBaseTradeProductDealDtos().get(0);
        Map<String, Object> vDateNato = baseAssetCashPositionCommMapper.selectVDateNato(baseTradeDto.getTrdProductDeal().getFinprodId());
        String profitType = vDateNato == null ? null : (String)vDateNato.get("PROFIT_TYPE");//获取收益类型
        List<StlPayOrdersDto> globalStlOrderList=new ArrayList<StlPayOrdersDto>();
        if(认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())
        		&& DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
        		认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
        		DictCodeList.收益类型_净值型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
        		认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
                DictCodeList.收益类型_货币型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())){
        			globalStlOrderList=baseTradeDto.getStlPayOrdersList();
        			baseTradeDto.setStlPayOrdersList(new ArrayList<StlPayOrdersDto>());
        }
        /*-------------------裴家俊------add------2018/11/23-------------end--------------------*/
        if (!finProductTradeDto.isNeedSum()) {
            // 2.调用基础组件.【金融产品交易组件.新建金融产品交易信息】,写入金融产品交易信息
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertTradeInfo",
                    finProductTradeDto.getBaseTradeProductDealDtos(), Boolean.class);
        }
        /*----------------------裴家俊----add------2018/11/23-------------start--------------------*/
        if(认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())
        		&& DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
        		认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
        		DictCodeList.收益类型_净值型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
        		认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
                DictCodeList.收益类型_货币型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())){
        		baseTradeDto.setStlPayOrdersList(globalStlOrderList);
        }
        /*----------------------裴家俊----add------2018/11/23-------------end--------------------*/
        
        // 3.写入类型为[实际]的组合持仓流水与组合现金流水
        insertPosition(finProductTradeDto);

        /*----------------------zhangliang----add------2019/02/23-------------start--------------------*/
        BigDecimal shareAmt = baseTradeDto.getTrdProductDeal().getShareAmt();
        if (baseTradeDto.getTrdProductDeal() != null
        		&& StringUtil.equals(baseTradeDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_买断式回购交易.getValue())){
        	if (baseTradeDto.getSecAmount() != null){
        		baseTradeDto.getTrdProductDeal().setShareAmt(baseTradeDto.getSecAmount());
        	}
        }
        /*----------------------zhangliang----add------2019/02/23-------------end--------------------*/
        
        // 3.调用基础组件.【投资委托组件.完成委托】，写入委托完成状况信息(投资委托场合才执行此步)
        finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), false);

        /*----------------------zhangliang----add------2019/02/23-------------start--------------------*/
        baseTradeDto.getTrdProductDeal().setShareAmt(shareAmt);
        /*----------------------zhangliang----add------2019/02/23-------------end--------------------*/
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    private Boolean finishEntrust(List<BaseTradeProductDealDto> BaseTradeProductDealDtoList, boolean isCancel) {
        List<EtrFinishStatusDto> baseEtrInvestInfoDtos = new ArrayList<>();
        for (BaseTradeProductDealDto baseTradeProductDealDto : BaseTradeProductDealDtoList) {
            if(StringUtil.isNotBlank(baseTradeProductDealDto.getPreTradeId())){
                TrdProductDealPreKey key = new TrdProductDealPreKey();
                key.setTradeId(baseTradeProductDealDto.getPreTradeId());
                key.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                TrdProductDealPre trdProductDealPre = trdProductDealPreMapper.selectByPrimaryKey(key);
                TrdProductDealAddPre trdProductDealAddPre = trdProductDealAddPreMapper.selectByPrimaryKey(baseTradeProductDealDto.getPreTradeId());
                EtrFinishStatusKey etrKey = new EtrFinishStatusKey();
                etrKey.setEntrustId(trdProductDealAddPre.getEntrustId());
                etrKey.setTradeId(baseTradeProductDealDto.getPreTradeId());
                EtrFinishStatus etrFinishStatus = etrFinishStatusMapper.selectByPrimaryKey(etrKey);
                if(trdProductDealPre!=null && StringUtils.isNotEmpty(trdProductDealAddPre.getEntrustId())){
                    if (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            || StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                            || StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                        EtrFinishStatusDto status = new EtrFinishStatusDto();
                        status.setEntrustId(trdProductDealAddPre.getEntrustId());
                        status.setTradeId(baseTradeProductDealDto.getTrdProductDealAdd().getTradeId());
                        status.setFinishDate(baseTradeProductDealDto.getTrdProductDeal().getTradeDate());
                        status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                        status.setEntFinishAmt(etrFinishStatus != null ? etrFinishStatus.getEntFinishAmt() : BigDecimal.ZERO);
                        status.setTrader(baseTradeProductDealDto.getTrdProductDeal().getTrader());
                        if(StringUtil.isNotBlank(baseTradeProductDealDto.getfTradeId())){
                            status.setCstpTradeId(baseTradeProductDealDto.getfTradeId());
                        }
                        baseEtrInvestInfoDtos.add(status);
                        continue;
                    }
                }
            }
            if (StringUtil.isEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId())) {
                continue;
            }
            //可转债申购不需要更新委托完成度，service已经更新过委托状态
            if(StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                    &&baseTradeProductDealDto.getTrdFinProductDealTotal()!=null
            		&&(StringUtil.equals(DictCodeList.交易方向_申购.getValue(),baseTradeProductDealDto.getTrdFinProductDealTotal().getPs())||StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(),baseTradeProductDealDto.getTrdFinProductDealTotal().getPs()))){
            	 continue;
            }
            // 外汇远期掉期到期时不需要更新委托完成度（到期copy的买入，所以委托代码也有值了） add by renping 20191125 start
            if ((StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(),
                DictCodeListExt1.金融产品类型_投管用_远期.getValue())
                 || StringUtil.equals(
                     baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(),
                     DictCodeListExt1.金融产品类型_投管用_掉期.getValue()))
                && (StringUtil.equals(DictCodeList.交易方向_到期.getValue(),
                    baseTradeProductDealDto.getTrdProductDeal().getPs()))) {
                continue;
            }
            // 外汇远期掉期到期时不需要更新委托完成度（到期copy的买入，所以委托代码也有值了） add by renping 20191125 end

            EtrFinishStatusDto status = new EtrFinishStatusDto();
            status.setEntrustId(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
            status.setTradeId(baseTradeProductDealDto.getTrdProductDealAdd().getTradeId());
            status.setFinishDate(baseTradeProductDealDto.getTrdProductDeal().getTradeDate());
            if (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
            		|| StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
            		|| StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())) {
//                status.setFinishAmt(new BigDecimal(baseTradeProductDealDto.getTrdProductDeal().getShareAmt().intValue() * 100));
                status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
            }else if(StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())||
                     StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())||
                    StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())){
                    EtrEntrust etr =etrEntrustMapper.selectByPrimaryKey(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
            	//委托是金额 更新金额
            	if(StringUtil.isNotEmpty(etr)&&StringUtil.isNotEmpty(etr.getEntrustAmt())){
            		status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getTradeAmt());
            	}
            	//委托是数量 更新数量
            	else{
            		status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
            	}
            }
            else {
                status.setFinishAmt(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal().getPrinAmt())?baseTradeProductDealDto.getTrdProductDeal().getPrinAmt(): StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal().getShareAmt())?baseTradeProductDealDto.getTrdProductDeal().getShareAmt():baseTradeProductDealDto.getTrdProductDeal().getTradeAmt());
            }
            status.setTrader(baseTradeProductDealDto.getTrdProductDeal().getTrader());
            if(StringUtil.isNotBlank(baseTradeProductDealDto.getfTradeId())){
                status.setCstpTradeId(baseTradeProductDealDto.getfTradeId());
            }
            baseEtrInvestInfoDtos.add(status);
        }
        if (!baseEtrInvestInfoDtos.isEmpty()) {
            if (isCancel) {
                return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/cancelEntrust", baseEtrInvestInfoDtos, Boolean.class);
            } else {
                return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/finishEntrust", baseEtrInvestInfoDtos, Boolean.class);
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 处理流程中状态
     *
     * @return CommonResult
     * @param finProductTradeDto
     */
    private CommonResult deal流程中(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        // 1.调用【组合可用持仓与实际持仓查询更新组件】与【组合可用现金与实际现金查询更新组件】执行持仓与现金的插入操作

        /*----------------------zhangliang----add------2019/02/23-------------start--------------------*/
        BaseTradeProductDealDto baseTradeDto = finProductTradeDto.getBaseTradeProductDealDtos().get(0);
        if (baseTradeDto.getTrdProductDeal() != null
        		&& StringUtil.equals(baseTradeDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_买断式回购交易.getValue())){
        	if (baseTradeDto.getSecAmount() != null){
        		baseTradeDto.getTrdProductDeal().setShareAmt(baseTradeDto.getSecAmount());
        	}
        }
        /*----------------------zhangliang----add------2019/02/23-------------end--------------------*/

        if (!insertPosition(finProductTradeDto)) {
            return new CommonResult(CommonResult.FAILD, "处理失败");
        }

        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    /**
     * 更新债券CSTP的交易
     */
    @DataSource(DataSourceType.OTHER)
    @DTXTransaction
    public void updateInterfForZhaiJuan(FinProductTradeDto finProductTradeDto){
        TrdProductDeal trdProductDeal = finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal();
        InterfTradSecdeal interfTradSecdeal = interfTradSecdealMapper.selectByPrimaryKey(trdProductDeal.getfTradeId());
        InterfTradSecdeal record = new InterfTradSecdeal();
        if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            if(interfTradSecdeal!=null){
                record.setDealno(trdProductDeal.getfTradeId());
                BigDecimal prinamt = interfTradSecdeal.getPrinamt();
                BigDecimal subtract = ArithmeticUtil.doAdd(prinamt, finProductTradeDto.getTotalFaceValue());
                if(subtract.compareTo(BigDecimal.ZERO)<0||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                    record.setPrinamt(BigDecimal.ZERO);
                }else{
                    record.setPrinamt(subtract);
                }
                interfTradSecdealMapper.updateByPrimaryKeySelective(record);
            }
        }
        else if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            if(interfTradSecdeal!=null){
                record.setDealno(trdProductDeal.getfTradeId());
                BigDecimal prinamt = interfTradSecdeal.getPrinamt();
                BigDecimal subtract = ArithmeticUtil.subtract(prinamt, finProductTradeDto.getTotalFaceValue());
               if(subtract.compareTo(BigDecimal.ZERO)<0||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                   record.setPrinamt(BigDecimal.ZERO);
               }else{
                   record.setPrinamt(subtract);
               }
               interfTradSecdealMapper.updateByPrimaryKeySelective(record);
            }
        }
        else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(finProductTradeDto.getWorkflowStatus())) {

        }
    }

    /**
     * 更新CSTP质押式交易
     * @param finProductTradeDto
     */
    @DataSource(DataSourceType.OTHER)
    @DTXTransaction
    public void updateInterfForZhiYaShi(FinProductTradeDto finProductTradeDto){
        TrdProductDeal trdProductDeal = finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal();
        InterfOpicsTradRepo interfOpicsTradRepo = interfOpicsTradRepoMapper.selectByPrimaryKey(trdProductDeal.getfTradeId());
        InterfOpicsTradRepo record = new InterfOpicsTradRepo();
        if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            if(interfOpicsTradRepo!=null){
                record.setDealno(trdProductDeal.getfTradeId());
                record.setTradeid(trdProductDeal.getfTradeId());
                BigDecimal prinamt = interfOpicsTradRepo.getRepoamt();
                BigDecimal subtract = ArithmeticUtil.doAdd(prinamt, trdProductDeal.getPrinAmt());
                if(subtract.compareTo(BigDecimal.ZERO)<0||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                    record.setRepoamt(BigDecimal.ZERO);
                }else{
                    record.setRepoamt(subtract);
                }
                interfOpicsTradRepoMapper.updateByPrimaryKeySelective(record);
            }
        }
        else if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            if(interfOpicsTradRepo!=null){
                record.setDealno(trdProductDeal.getfTradeId());
                record.setTradeid(trdProductDeal.getfTradeId());
                BigDecimal prinamt = interfOpicsTradRepo.getRepoamt();
                BigDecimal subtract = ArithmeticUtil.subtract(prinamt, trdProductDeal.getPrinAmt());
                if(subtract.compareTo(BigDecimal.ZERO)<0||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                    record.setRepoamt(BigDecimal.ZERO);
                }else{
                    record.setRepoamt(subtract);
                }
                interfOpicsTradRepoMapper.updateByPrimaryKeySelective(record);
            }
        }
        else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
        }
    }

    /**
     * @description: 2.写入类型为[实际]的组合持仓流水与组合现金流水
     * @param: [BaseTradeProductDealDtoList, 持仓类型]
     * @return: void
     * @date: 2018/9/29
     */
    @Override
    public Boolean insertPosition(FinProductTradeDto finProductTradeDto) {
        DictCodeList 持仓类型 = null;
        boolean needNegative = false;
        if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            持仓类型 = DictCodeList.持仓类型_实际;
            needNegative = true;
        }
        else if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            持仓类型 = DictCodeList.持仓类型_可用;
            needNegative = false;
        }
        else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            持仓类型 = DictCodeList.持仓类型_实际;
            needNegative = false;
        }
        if (持仓类型 == null) {
            return false;
        }

        List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
        List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();
        for (BaseTradeProductDealDto baseTradeProductDealDto : finProductTradeDto.getBaseTradeProductDealDtos()) {
            PortfolioPositionsFlowInputDto inputDto = new PortfolioPositionsFlowInputDto();
            inputDto.setPositionType(持仓类型.getValue());
            inputDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
//            增加附属表dto
            inputDto.setTrdFinProductBarterList(baseTradeProductDealDto.getTrdFinProductBarterList());
            inputDto.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
            inputDto.setBonusMode(baseTradeProductDealDto.getTrdProductDealAdd().getPayType());
            inputDto.setAssetType2(finProductTradeDto.getAssetType2());
            List<PtlSecPositionFlowDto> ptlSecPositionFlowDtoss = assetPositionsCashCommServiceImpl.positionDeal(inputDto);
            ptlSecPositionFlows.addAll(ptlSecPositionFlowDtoss);

            if (baseTradeProductDealDto.getStlPayOrdersList() != null && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
                PortfolioCashPositionsFlowInputDto cashInputDto = new PortfolioCashPositionsFlowInputDto();
                cashInputDto.setPositionType(持仓类型.getValue());
                cashInputDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
                cashInputDto.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
                cashInputDto.setBonusMode(baseTradeProductDealDto.getTrdProductDealAdd().getPayType());
                cashInputDto.setAssetType2(finProductTradeDto.getAssetType2());
                cashInputDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList());
                cashInputDto.setIeFlag(finProductTradeDto.getIeFlag());
                List<PtlCashPositionFlowDto> resultList = assetPositionsCashCommServiceImpl.cashDeal(cashInputDto);
                ptlCashPositionFlows.addAll(resultList);
            }
            //        现金与持仓执行共同
            if(baseEtrCashPositionCommService != null && !finProductTradeDto.isNeedExecuteEtrPosition()){
                baseTradeProductDealDto.setBussRepoSec(finProductTradeDto.getBussRepoSec());
                baseTradeProductDealDto.setAssetType2(finProductTradeDto.getAssetType2());
                baseEtrCashPositionCommService.executeEtrCashPosition(baseTradeProductDealDto, 持仓类型, needNegative,finProductTradeDto.getIeFlag(),finProductTradeDto.getIsOverdueDeal());
            }
        }
        if (needNegative) {
            if (ptlCashPositionFlows != null && !ptlCashPositionFlows.isEmpty()) {
                for (PtlCashPositionFlowDto ptlCashPosition : ptlCashPositionFlows) {
                    if (ptlCashPosition.getAmount() != null) {
                        ptlCashPosition.setAmount(ptlCashPosition.getAmount().negate());
                    }
                    if (ptlCashPosition.getOrderId().lastIndexOf('R') != ptlCashPosition.getOrderId().length() - 1) {
                        ptlCashPosition.setOrderId(ptlCashPosition.getOrderId() + "R");
                    }
                }
            }
            if (ptlSecPositionFlows != null && !ptlSecPositionFlows.isEmpty()) {
                for (PtlSecPositionFlowDto ptlSecPosition : ptlSecPositionFlows) {
                    if (ptlSecPosition.getAmount() != null) {
                        ptlSecPosition.setAmount(ptlSecPosition.getAmount().negate());
                    }
                    if (ptlSecPosition.getTradeId().lastIndexOf('R') != ptlSecPosition.getTradeId().length() - 1) {
                        ptlSecPosition.setTradeId(ptlSecPosition.getTradeId() + "R");
                    }
                }
            }
        }
        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
        ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
        //不是逾期交易，更新持仓流水
        if(!(StringUtil.isNotEmpty(finProductTradeDto.getIsOverdueDeal())&&StringUtil.equals(DictCodeList.是否_是.getValue(), finProductTradeDto.getIsOverdueDeal()))){
        	if (ptlPositionHoldDto.getPtlSecPositionFlows() != null && !ptlPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
                for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
                    basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
                }
            }
        }
        if (ptlPositionHoldDto.getPtlCashPositionFlows() != null && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
            for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
                basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlow);
            }
        }
        return Boolean.TRUE;
    }
}
