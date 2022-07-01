package com.joyintech.tams.invest.common.component.business.common.impl;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.PtlSecPositionFlowMapper;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.common.base.entity.TrdFinProductBarter;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.business.common.BaseAssetCashPositionCommService;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioCashPositionsFlowInputDto;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioPositionsFlowInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

// import org.apache.shiro.util.Assert;

/**
 *
 **/
@Service
public class BaseAssetCashPositionCommServiceImpl implements BaseAssetCashPositionCommService {

    /**
     * @see BaseAssetCashPositionCommMapper
     */
    @Autowired
    private BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;
    /**
     * 组合持仓流水Mapper
     */
    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    private static final List<String> 正回购_逆回购 = Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),
        DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue(),
        DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());

    private static final List<String> 拆入_拆出 = Arrays.asList(DictCodeListExt1.金融产品类型_投管用_拆入.getValue(),
        DictCodeListExt1.金融产品类型_投管用_拆出.getValue(), DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue());

    private static final List<String> 还本_到期_收息 = Arrays.asList(DictCodeList.交易方向_还本.getValue(), DictCodeList.交易方向_到期.getValue(),
        DictCodeList.交易方向_收息.getValue());

    // 一体化对应etliu
    // private static final List<String> 卖出_行权_到期 = Arrays.asList(DictCodeList.交易方向_卖出.getValue(),
    // DictCodeList.交易方向_行权.getValue(), DictCodeList.交易方向_到期.getValue());
    private static final List<String> 卖出_行权_到期 = Arrays.asList(DictCodeList.交易方向_卖出.getValue(),
        DictCodeList.交易方向_行权_回售.getValue(), DictCodeList.交易方向_行权_赎回.getValue(), DictCodeList.交易方向_到期.getValue());

    private static final List<String> 还本_卖出_到期 = Arrays.asList(DictCodeList.交易方向_还本.getValue(), DictCodeList.交易方向_到期.getValue(),
        DictCodeList.交易方向_卖出.getValue());

    // 一体化对应etliu start
    // private static final List<String> 还本_到期_赎回 = Arrays.asList(DictCodeList.交易方向_还本.getValue(),
    // DictCodeList.交易方向_到期.getValue(), DictCodeList.交易方向_赎回.getValue());
    private static final List<String> 还本_到期_赎回_卖出 = Arrays.asList(DictCodeList.交易方向_还本.getValue(),
        DictCodeList.交易方向_到期.getValue(), DictCodeList.交易方向_赎回.getValue(), DictCodeList.交易方向_卖出.getValue());
    // 一体化对应etliu end

    private static final List<String> 产品赎回_现金分红_付费 = Arrays.asList(DictCodeList.交易方向_产品赎回.getValue(),
        DictCodeList.交易方向_产品现金分红.getValue(), DictCodeList.交易方向_产品付费.getValue());

    // 一体化对应etliu
    // private static final List<String> 还本_行权_收息_到期_卖出 = Arrays.asList(DictCodeList.交易方向_还本.getValue(),
    // DictCodeList.交易方向_行权.getValue(), DictCodeList.交易方向_收息.getValue(),
    // DictCodeList.交易方向_到期.getValue(), DictCodeList.交易方向_卖出.getValue());
    private static final List<String> 还本_行权_收息_到期_卖出 = Arrays.asList(DictCodeList.交易方向_还本.getValue(),
        DictCodeList.交易方向_行权_回售.getValue(), DictCodeList.交易方向_行权_赎回.getValue(), DictCodeList.交易方向_收息.getValue(),
        DictCodeList.交易方向_到期.getValue(), DictCodeList.交易方向_卖出.getValue());

    private static final List<String> 还本_收息_到期_卖出 = Arrays.asList(DictCodeList.交易方向_还本.getValue(),
        DictCodeList.交易方向_收息.getValue(), DictCodeList.交易方向_到期.getValue(), DictCodeList.交易方向_卖出.getValue());

    private static final List<String> 产品认购_申购 = Arrays.asList(DictCodeList.交易方向_产品认购.getValue(),
        DictCodeList.交易方向_产品申购.getValue());

    private static final List<String> 赎回到账_现金分红 = Arrays.asList(DictCodeList.交易方向_赎回到账.getValue(),
        DictCodeList.交易方向_现金分红.getValue());

    @Override
    public List<PtlSecPositionFlowDto> positionDeal(PortfolioPositionsFlowInputDto inputDto) {
        // Assert.notNull(inputDto, "参数不能为null");
        // Assert.notNull(inputDto.getTrdProductDeal(), "金融产品交易Entity不能为null");
        // Assert.isTrue(StringUtil.isNotEmpty(inputDto.getTrdProductDeal().getFinprodId()), "金融产品代码不能为空");
        // Assert.isTrue(StringUtil.isNotEmpty(inputDto.getPositionType()), "持仓类型不能为空");
        // 1、起息日、收益类型取得(计划类利率型/项目类资产)
        Map<String, Object> vDateNato = baseAssetCashPositionCommMapper.selectVDateNato(
            inputDto.getTrdProductDeal().getFinprodId());
        TrdProductDeal deal = inputDto.getTrdProductDeal();
        TrdProductDealAdd dealAdd = inputDto.getTrdProductDealAdd();
        // 起息日
        Date vDate = vDateNato == null ? null : (Date)vDateNato.get("VDATE");
        /**
         * 收益类型
         */
        String profitType = vDateNato == null ? null : (String)vDateNato.get("PROFIT_TYPE");
        // 2、资产二级类型
        String assetType2 = StringUtil.isNotEmpty(
            inputDto.getAssetType2()) ? inputDto.getAssetType2() : baseAssetCashPositionCommMapper.selectFundType(
                inputDto.getTrdProductDeal().getFinprodId());
        String stage = inputDto.getStage();
        List<PtlSecPositionFlowDto> result = new ArrayList<>();
        PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
        resultDto.setPortfolioId(inputDto.getPortfolioId());
        String secAcctId = deal.getSecManageAcctId();
        if ((StringUtil.isEmpty(secAcctId) || "999999".equals(secAcctId)) && StringUtil.isNotEmpty(deal.getChlId())) {
            secAcctId = deal.getChlId();
        }
        resultDto.setSecAcctId(StringUtil.isEmpty(secAcctId) ? "999999" : secAcctId);
        resultDto.setFinprodId(deal.getFinprodId());
        resultDto.setBranch(0L);
        resultDto.setInvAim(StringUtil.isEmpty(deal.getInvAim()) ? "999999" : deal.getInvAim());
        resultDto.setHodingType(inputDto.getPositionType());
        resultDto.setCcy(deal.getCcy());
        resultDto.setTradeId(deal.getTradeId());
        resultDto.setCreateUser(deal.getCreateUser());
        resultDto.setCreateDept(deal.getCreateDept());
        resultDto.setUpdateUser(deal.getCreateUser());
        /*
         * 金融产品交易Entity.金融产品类型（投管分类）in (金融产品类型_投管用_理财产品,金融产品类型_投管用_理财子产品,金融产品类型_投管用_理财产品分层) and 金融产品交易Entity.交易方向 in
         * (交易方向_产品赎回,交易方向_产品到期、交易方向_份额强减、交易方向_产品提前赎回、交易方向_产品违约赎回、交易方向_提前终止)
         */
        if ((DictCodeListExt1.金融产品类型_投管用_理财产品.getValue().equals(deal.getFinprodType2())
             || DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue().equals(deal.getFinprodType2())
             || DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue().equals(deal.getFinprodType2()))
            && (DictCodeList.交易方向_产品赎回.getValue().equals(deal.getPs()) || DictCodeList.交易方向_产品到期.getValue().equals(deal.getPs())
                || DictCodeList.交易方向_份额强减.getValue().equals(deal.getPs())
                || DictCodeList.交易方向_产品提前赎回.getValue().equals(deal.getPs())
                || DictCodeList.交易方向_产品违约赎回.getValue().equals(deal.getPs())
                || DictCodeList.交易方向_提前终止.getValue().equals(deal.getPs()))) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            if (DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue().equals(deal.getFinprodType2())) {
                resultDto.setpFinprodId(StringUtil.defaultString(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()).concat(
                    GlobalConstant.UNDER_LINE).concat(StringUtil.defaultString(inputDto.getPortfolioId())));
            }
            result.add(resultDto);
            return result;
        }
        /*
         * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）
         */
        if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
            && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()) && 卖出_行权_到期.contains(deal.getPs())) {
            resultDto.setCdate(StringUtil.isNotEmpty(deal.getPayDate()) ? deal.getPayDate() : deal.getTradeDate());// modify by jiangpeng on 20210524 ZGBSSE-226
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));

            if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
                && DictCodeListExt1.投资目的_卖空.getValue().equals(deal.getInvAim())) {
                PtlSecPositionFlowDto resultDto1 = new PtlSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(resultDto1, resultDto);
                resultDto1.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                if (DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())) {
                    resultDto1.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
                }
                else {
                    resultDto1.setHodingType(DictCodeListExt1.持仓类型_卖空.getValue());
                }
                resultDto1.setAmount(resultDto1.getAmount().negate());
                result.add(resultDto1);
            }
            else {
                result.add(resultDto);
            }
            return result;
        }
        //add by yujiaping 2021-1-6 解决ZGBS-139 计划类净值型卖出交易提交后（流程中），没有占用资产可用持仓 start
        else if ((DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                && (DictCodeList.收益类型_净值型.getValue().equals(profitType)))
              && (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
                /*|| DictCodeList.交易方向_赎回.getValue().equals(deal.getPs())*/)) {
                 resultDto.setCdate(deal.getTradeDate());
                 // ZGBS-11 计划类净值型赎回交易 交易金额/交易份额 二者只用填一项,当只填交易金额时,amount值为空报错
                 resultDto.setAmount(
                         ArithmeticUtil.multiply(StringUtil.isNotEmpty(deal.getShareAmt()) ? deal.getShareAmt() : deal.getTradeAmt(), new BigDecimal(-1)));
                 result.add(resultDto);
        }
        //add by yujiaping  2021-1-6 解决ZGBS-139 计划类净值型卖出交易提交后（流程中），没有占用资产可用持仓 end
        /*
         * 1 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“卖出”,“行权”,“到期”））
         *
         * 2 输入金融产品交易Entity.金融产品类型（投管分类）in (“正回购”,“逆回购”) and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“到期”）
         *
         * 3 输入金融产品交易Entity.金融产品类型（投管分类）in （“存款”）and 金融产品交易Entity.交易方向 in （“卖出”,“到期”）
         *
         * 4 输入金融产品交易Entity.金融产品类型（投管分类）in （“存款”）and 金融产品交易Entity.交易方向 in （“提前支取”）and 资产二级类型(共通逻辑获取)=“存放同业”
         *
         * 5 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and 金融产品交易Entity.交易方向 in （“赎回确认”）
         *
         * 6 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“赎回确认”）and 收益类型(共通逻辑获取) = “净值型”
         *
         * 7 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“赎回”，“还本”，“到期”）and 收益类型(共通逻辑获取) = “利率型”
         *
         * 8 输入金融产品交易Entity.金融产品类型（投管分类）in （“项目类资产”）and 金融产品交易Entity.交易方向 in （“还本”，“到期”）
         *
         */
        else if ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                  && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()) && 卖出_行权_到期.contains(deal.getPs()))

                 || (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                     && (DictCodeList.存款类资产类型_同业借款.getValue().equals(assetType2)
                         || DictCodeList.存款类资产类型_存放同业.getValue().equals(assetType2))
                     && 还本_卖出_到期.contains(deal.getPs()))

                 || (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                     && DictCodeList.存款类资产类型_存放同业.getValue().equals(assetType2)
                     && DictCodeList.交易方向_提前支取.getValue().equals(deal.getPs()))

                 || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                     // 一体化对应etliu start
                     // && DictCodeList.收益类型_收益型.getValue().equals(profitType) && 还本_到期_赎回.contains(deal.getPs()))
                     && DictCodeList.收益类型_收益型.getValue().equals(profitType) && 还本_到期_赎回_卖出.contains(deal.getPs()))
                 // 一体化对应etliu end

                 || (DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(deal.getFinprodType2())
                     && 还本_卖出_到期.contains(deal.getPs()))) {
            if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                && BigDecimal.ZERO.compareTo(deal.getShareAmt()) == 0) {
                return result;
            }
            resultDto.setCdate(deal.getTradeDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));

            if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
                && DictCodeListExt1.投资目的_卖空.getValue().equals(deal.getInvAim())) {
                PtlSecPositionFlowDto resultDto1 = new PtlSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(resultDto1, resultDto);
                resultDto1.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                if (DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())) {
                    resultDto1.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
                }
                else {
                    resultDto1.setHodingType(DictCodeListExt1.持仓类型_卖空.getValue());
                }
                resultDto1.setAmount(resultDto1.getAmount().negate());
                result.add(resultDto1);
            }
            else {
                result.add(resultDto);
            }
            return result;
        }
        else if ((正回购_逆回购.contains(deal.getFinprodType2()) && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                  && DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            /**** 回购释放头寸处理*****chenlei1*add***start **/
            if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2()))
                && (DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))
                && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)) { // DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(deal.getPs()) ||
                Map<String, Object> rsmap = new HashMap<String, Object>();
                rsmap.put("tradeId", deal.getFinprodId());
                rsmap.put("finprodType2", deal.getFinprodType2());
                Map<String, Object> statusMap = baseAssetCashPositionCommMapper.selectTradeReleaseStatus(rsmap);
                String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
                String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");
                if(deal.getTradeId().endsWith("R")){//是否撤销交易
                    if (DictCodeList.是否_否.getValue().equals(isManualRelease)) {//IF 是否手工释放 = 否
                        result.add(resultDto);
                    }else {
                        return result;
                    }
                }else{//到期确认交易
                    if (DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)) {
                        return result;
                    }else {//IF  未释放
                        result.add(resultDto);
                    }
                }
            }
            else {
                result.add(resultDto);
            }
            /**** 回购释放头寸处理*****chenlei1*add***end **/
            return result;
        }

        /*
         * 1 输入金融产品交易Entity.金融产品类型（投管分类）in (“正回购”,“逆回购”) and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“到期”）
         *
         */
        else if ((正回购_逆回购.contains(deal.getFinprodType2()) && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                  && DictCodeList.交易方向_到期.getValue().equals(deal.getPs())) ) {
            resultDto.setCdate(deal.getMdate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            /**** 回购释放头寸处理*****chenlei1*add***start **/
            if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2()))
                && (DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))
                && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)) {       // DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(deal.getPs()) ||
                Map<String, Object> rsmap = new HashMap<String, Object>();
                rsmap.put("tradeId", deal.getFinprodId());
                rsmap.put("finprodType2", deal.getFinprodType2());
                Map<String, Object> statusMap = baseAssetCashPositionCommMapper.selectTradeReleaseStatus(rsmap);
                String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
                String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");
                if(deal.getTradeId().endsWith("R")){//是否撤销交易
                    if (DictCodeList.是否_否.getValue().equals(isManualRelease)) {//IF 是否手工释放 = 否
                    	 result.add(resultDto);
                    }else {
                    	return result;
                    }
                }else{//到期确认交易
                    if (DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)) {
                        return result;
                    }else {//IF  未释放
                        result.add(resultDto);
                    }
                }

            }
            else {
                result.add(resultDto);
            }
            /**** 回购释放头寸处理*****chenlei1*add***end **/
            return result;
        }
        /*
         * 2 输入金融产品交易Entity.金融产品类型（投管分类）in (“拆入”,“拆出”) and 金融产品交易Entity.交易方向 in （“到期”）
         *
         */
        else if (拆入_拆出.contains(deal.getFinprodType2()) &&
        		(DictCodeList.交易方向_到期.getValue().equals(deal.getPs()) || DictCodeList.交易方向_卖出.getValue().equals(deal.getPs()) ) ) {
              resultDto.setCdate(deal.getSettleDate());
              resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
              /**** 回购释放头寸处理*****chenlei1*add***start **/
              if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2()))
                  && (DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))
                  && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)) {       // DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(deal.getPs()) ||
                  Map<String, Object> rsmap = new HashMap<String, Object>();
                  rsmap.put("tradeId", deal.getFinprodId());
                  rsmap.put("finprodType2", deal.getFinprodType2());
                  Map<String, Object> statusMap = baseAssetCashPositionCommMapper.selectTradeReleaseStatus(rsmap);
                  String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
                  String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");
                  if(deal.getTradeId().endsWith("R")){//是否撤销交易
                      if (DictCodeList.是否_否.getValue().equals(isManualRelease)) {//IF 是否手工释放 = 否
                           result.add(resultDto);
                      }else {
                          return result;
                      }
                  }else{//到期确认交易
                      if (DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)) {
                          return result;
                      }else {//IF  未释放
                          result.add(resultDto);
                      }
                  }

              }
              else {
                  result.add(resultDto);
              }
              /**** 回购释放头寸处理*****chenlei1*add***end **/
              return result;
          }
        /*
         * 1 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and 金融产品交易Entity.交易方向 in （“赎回确认”）
         *
         * 2 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“赎回确认”）and 收益类型(共通逻辑获取) = “净值型”
         *
         */
        else if ((DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
                  || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                      && DictCodeList.收益类型_净值型.getValue().equals(profitType))
                  || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                      && DictCodeList.收益类型_货币型.getValue().equals(profitType))
                // add by etliu 20210421 权益类资产相关代码合并 start
                || (DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2())))
                // add by etliu 20210421 权益类资产相关代码合并 end
                 && (DictCodeList.交易方向_赎回确认.getValue().equals(deal.getPs()) || DictCodeListExt2.交易方向_转换赎回确认.getValue().equals(deal.getPs()))
                || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                        && DictCodeList.收益类型_货币型.getValue().equals(profitType) && DictCodeList.交易方向_赎回.getValue().equals(deal.getPs()))
                //add by chenjiaxing 20210909 计划类资产净值型赎回交易可用持仓错误 start
                || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                && DictCodeList.收益类型_净值型.getValue().equals(profitType) && DictCodeList.交易方向_赎回.getValue().equals(deal.getPs()))
            //add by chenjiaxing 20210909 计划类资产净值型赎回交易可用持仓错误 end
        ) {
            if (!DictCodeList.交易方向_赎回确认.getValue().equals(deal.getPs()) && !DictCodeList.交易方向_赎回.getValue().equals(deal.getPs())){
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                result.add(resultDto);
            }
            if (inputDto.getPositionType().equals(DictCodeList.持仓类型_可用.getValue()) && DictCodeList.交易方向_赎回.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(ArithmeticUtil.multiply(StringUtil.isNotEmpty(deal.getShareAmt()) ? deal.getShareAmt() : deal.getTradeAmt(), new BigDecimal(-1)));
                result.add(resultDto);
            }
            if (inputDto.getPositionType().equals(DictCodeList.持仓类型_实际.getValue()) &&
                    DictCodeList.交易方向_赎回.getValue().equals(deal.getPs()) && !"N".equals(deal.getIsCancel())) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
                resultDto.setAmount(ArithmeticUtil.multiply(StringUtil.isNotEmpty(deal.getShareAmt()) ? deal.getShareAmt() : deal.getTradeAmt(), new BigDecimal(-1)));
                result.add(resultDto);
            }
            if (/*!(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                        && DictCodeList.收益类型_净值型.getValue().equals(profitType))
                    &&*/ inputDto.getPositionType().equals(DictCodeList.持仓类型_实际.getValue())
                    && DictCodeList.交易方向_赎回确认.getValue().equals(deal.getPs())) {
                Map<String, Object> params = new HashMap<>();
                params.put("finprodId",deal.getbTradeId());
                params.put("holdingType",DictCodeList.持仓类型_在途.getValue());
                List<PtlSecPositionFlow> list = ptlSecPositionFlowMapper.selectSecFlow(params);
                if(list.isEmpty()) {
                    resultDto.setCdate(deal.getTradeDate());
                    resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                    result.add(resultDto);
                    PtlSecPositionFlowDto resultDtozt = new PtlSecPositionFlowDto();
                    BeanUtils.copyProperties(resultDto, resultDtozt);
                    resultDtozt.setAmount(deal.getShareAmt());
                    resultDtozt.setHodingType(DictCodeList.持仓类型_可用.getValue());
                    result.add(resultDtozt);
                }else{
                    resultDto.setCdate(deal.getTradeDate());
                    resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                    result.add(resultDto);
                    PtlSecPositionFlowDto resultDtozt = new PtlSecPositionFlowDto();
                    BeanUtils.copyProperties(resultDto, resultDtozt);
                    resultDtozt.setAmount(list.get(0).getAmount());
                    resultDtozt.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    result.add(resultDtozt);
                }
                //抵消赎回提交时的可用持仓
                // modify by jiangpeng on 20210526 ZGBSSE-418 计划类净值型提交时没有可用持仓
                if(!StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue(),deal.getFinprodType2())) {
                    PtlSecPositionFlowDto resultDtoky = new PtlSecPositionFlowDto();
                    BeanUtils.copyProperties(resultDto, resultDtoky);
                    resultDtoky.setHodingType(DictCodeList.持仓类型_可用.getValue());
                    resultDtoky.setAmount(resultDtoky.getAmount().negate());
                    result.add(resultDtoky);
                }
            }
            return result;
        }
        // -----------------add by 裴家俊 20190628------债券借贷----------start-------------
        /*
         * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券借贷融出 and 金融产品交易Entity.交易方向 in （“买入”）
         */
        else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue().equals(deal.getFinprodType2())
                 && DictCodeList.交易方向_到期.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getMdate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            result.add(resultDto);
            return result;
        }
        /*
         * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券借贷融入 and 金融产品交易Entity.交易方向 in （“到期”）
         */
        else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(deal.getFinprodType2())
                 && DictCodeList.交易方向_到期.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getMdate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            result.add(resultDto);
            return result;
        }
        // 利率互换买入移动到实际持仓时，提前终止和到期统计到可用持仓 renping 2019-08-29 start
        else if (DictCodeListExt1.金融产品类型_投管用_利率互换.getValue().equals(deal.getFinprodType2())
                 && !DictCodeList.交易方向_买入.getValue().equals(deal.getPs()) && deal.getShareAmt() != null) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            result.add(resultDto);
            return result;
        }
        // 利率互换买入移动到实际持仓时，提前终止和到期统计到可用持仓 renping 2019-08-29 start

        // add by wangpeng 20190724 start
        /*
         * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).股票 and 金融产品交易Entity.交易方向 in （卖出）
         */
        else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(deal.getFinprodType2())
                 && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            result.add(resultDto);
            return result;
        }
        else if (StringUtils.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_基金.getValue())
                 && (StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强减.getValue())
                     || StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强增.getValue()))) {
            if (StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强减.getValue())) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.subtract(BigDecimal.ZERO, deal.getShareAmt()));
                result.add(resultDto);
                return result;
            }
        }

        // add by wangpeng 20190724 end
        // add by wangpeng 20190724 start
        /*
         * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型_投管用_债券 and 金融产品交易Entity.交易方向 in （债转股）
         */
        else if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                 && DictCodeListExt3.交易方向_债转股.getValue().equals(deal.getPs())
                 && DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())) {
            resultDto.setCdate(deal.getTradeDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            result.add(resultDto);
            return result;
        }

        //-----------------add by 裴家俊 20190628----债券借贷--------end--------------------------

        //add by  liutf 20200108 start
        /*
         * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型_投管用_期权 and 金融产品交易Entity.
         * 期权存续期交易
         */
        else if (DictCodeListExt1.金融产品类型_投管用_期权.getValue().equals(deal.getFinprodType2())) {
            resultDto.setCdate(deal.getTradeDate());
            //期权存续期交易
            if(StringUtil.isNotEmpty(deal.getPsPayment())){
                resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            }else{
                //期权交易
                resultDto.setAmount(deal.getShareAmt());
            }
            resultDto.setTradeId(deal.getTotleTradeId());
            result.add(resultDto);
            return result;
        }
        //add by  liutf 20200108 end

        //add by mashukai 20210316 计划类货币型资产卖出交易提交时交易可用持仓改变 start
        if ((DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
            && DictCodeList.收益类型_货币型.getValue().equals(profitType))
                && DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())
                && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getTradeDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            result.add(resultDto);
        }
        //add by mashukai 20210316 计划类货币型资产卖出交易提交时交易可用持仓改变 end

        if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
                && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getTradeDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
            result.add(resultDto);
        }else if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
                && DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())
                && DictCodeList.交易方向_赎回.getValue().equals(deal.getPs())) {
            PtlSecPositionFlowDto resultDtozt = new PtlSecPositionFlowDto();
            BeanUtils.copyProperties(resultDto, resultDtozt);
            resultDtozt.setCdate(deal.getTradeDate());
            resultDtozt.setAmount(ArithmeticUtil.multiply(com.joyintech.framework.util.StringUtil.isNotEmpty(
                    deal.getShareAmt()) ? deal.getShareAmt() : deal.getPrinAmt(), new BigDecimal(-1)));
//            resultDtozt.setHodingType(DictCodeList.持仓类型_在途.getValue());
            if (DictCodeList.持仓类型_实际.getValue().equals(resultDto.getHodingType())) {
            	resultDtozt.setHodingType(DictCodeList.持仓类型_在途.getValue());
            	resultDtozt.setAmount(ArithmeticUtil.multiply(resultDtozt.getAmount(), new BigDecimal(-1)));
            }
            result.add(resultDtozt);
            return result;
        }

        if (StringUtils.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_基金.getValue())
                && (StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强减.getValue())
                || StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强增.getValue()))) {
            resultDto.setTradeId(deal.getTotleTradeId());
            if (StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强减.getValue())) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.subtract(BigDecimal.ZERO, deal.getShareAmt()));
                result.add(resultDto);
                return result;
            }
            else if (StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强增.getValue())) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getShareAmt());
                result.add(resultDto);
                return result;
            }
        }

        if (DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(deal.getFinprodType2())
                && ("ISSUE_CLASS_01".equals(assetType2) || "ISSUE_CLASS_02".equals(assetType2))// 发行方式 标志是否为一级投标
                && StringUtil.equals(DictCodeList.交易方向_买入.getValue(), deal.getPs())) {
            // 成交 增加在途持仓
            if ("02".equals(stage)) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getShareAmt());
                resultDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                result.add(resultDto);
                return result;
            }
            // 交收 减少在途持仓 增加实际持仓
            if ("03".equals(stage)) {
                resultDto.setCdate(deal.getSettleDate());
                PtlSecPositionFlowDto flowDto = new PtlSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(flowDto, resultDto);
                flowDto.setAmount(deal.getShareAmt().negate());
                flowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                result.add(flowDto);
                PtlSecPositionFlowDto flowDto1 = new PtlSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(flowDto1, resultDto);
                flowDto1.setAmount(deal.getShareAmt());
                flowDto1.setHodingType(DictCodeList.持仓类型_实际.getValue());
                result.add(flowDto1);
                return result;
            }
        }

        if (DictCodeList.持仓类型_实际.getValue().equals(inputDto.getPositionType())) {

            /*
             * 金融产品交易Entity.金融产品类型（投管分类）in (金融产品类型_投管用_理财产品,金融产品类型_投管用_理财子产品,金融产品类型_投管用_理财产品分层) and 金融产品交易Entity.交易方向 in
             * (交易方向_产品申购,交易方向_产品认购、交易方向_产品红利转投、交易方向_份额强增)
             */
            if ((DictCodeListExt1.金融产品类型_投管用_理财产品.getValue().equals(deal.getFinprodType2())
                 || DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue().equals(deal.getFinprodType2())
                 || DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue().equals(deal.getFinprodType2()))
                && (产品认购_申购.contains(deal.getPs()) || DictCodeList.交易方向_产品红利转投.getValue().equals(deal.getPs())
                    || DictCodeList.交易方向_份额强增.getValue().equals(deal.getPs()))) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getShareAmt());
                if (DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue().equals(deal.getFinprodType2())) {
                    resultDto.setpFinprodId(StringUtil.defaultString(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()).concat(
                        GlobalConstant.UNDER_LINE).concat(StringUtil.defaultString(inputDto.getPortfolioId())));
                }
                result.add(resultDto);
                return result;
            }
            //期权交易卖出
            else if (DictCodeListExt1.金融产品类型_投管用_期权.getValue().equals(deal.getFinprodType2())
                    &&(DictCodeList.交易方向_卖出.getValue().equals(deal.getPs()))){
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(ArithmeticUtil.multiply(deal.getPrinAmt(), new BigDecimal(-1)));
                resultDto.setTradeId(deal.getTotleTradeId());
                result.add(resultDto);
                return result;
            }

            /*
             * 1 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）
             *
             * 2 输入金融产品交易Entity.金融产品类型（投管分类）in (“正回购”,“逆回购”) and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）
             */
            if ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                 || 正回购_逆回购.contains(deal.getFinprodType2()))
                && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())

                || (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2()))
                   && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                   && DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())

            /*
             * //需求变更 保证金回款方向为退款到账，此时不需要持仓入库 申购确认时，已变更持仓 ||(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())) &&
             * DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()) && DictCodeListExt1.交易方向_保证金回款.getValue().equals(deal.getPs())
             */

            ) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getShareAmt());
                if (DictCodeListExt1.投资目的_卖空.getValue().equals(deal.getInvAim())) {
                    resultDto.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                    if (DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())) {
                        resultDto.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
                    }
                    else {
                        resultDto.setHodingType(DictCodeListExt1.持仓类型_卖空.getValue());
                    }
                    resultDto.setAmount(deal.getShareAmt().negate());
                }
                result.add(resultDto);
                return result;
            }

            /*
             * 1 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）
             *
             * 2 输入金融产品交易Entity.金融产品类型（投管分类）in (“正回购”,“逆回购”) and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）
             *
             * 3 输入金融产品交易Entity.金融产品类型（投管分类）in （“存款”） and 金融产品交易Entity.交易方向 in （“买入”）
             *
             * 4 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金类 and 金融产品交易Entity.交易方向 in （“申购确认”）
             *
             * 5 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“申购确认”）
             *
             */
            else if (((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                       || 正回购_逆回购.contains(deal.getFinprodType2()))
                      && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                      && (DictCodeList.交易方向_买入.getValue().equals(deal.getPs()) || DictCodeListExt3.交易方向_配债.getValue().equals(deal.getPs())))

                     || (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                         && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))

                     || ((DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
                          || DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    || DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2()))
                    // add by etliu 20210421 权益类资产相关代码合并 end
                         // 场内基金改造 modify by renping 20191117 start
                         && (DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
                             || DictCodeList.交易方向_认购确认.getValue().equals(deal.getPs())
                            || DictCodeListExt2.交易方向_转换申购确认.getValue().equals(deal.getPs())))
                     // 场内基金改造 modify by renping 20191117 end

                     || ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2()))
                         && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                         && DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs()))

            /*
             * //需求变更 保证金回款方向为退款到账，此时不需要持仓入库 申购确认时，已变更持仓 ||((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())) &&
             * !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()) && DictCodeListExt1.交易方向_保证金回款.getValue().equals(deal.getPs()))
             */

            ) {
                resultDto.setCdate(deal.getTradeDate());
                // 可转债申购确认需要设置成结算日期
                if ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2()))
                        && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                        && dealAdd != null && StringUtil.equals(DictCodeListExt2.申购方式_网上申购.getValue(), dealAdd.getPurchModle())
                        && DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())) {
                	resultDto.setCdate(deal.getSettleDate());
                }
                // 日间成交回报不应该这么处理，应该是将成交数据明细的成交数量放到Dto的交易份额里面，该处代码复旧。
//				if (正回购_逆回购.contains(deal.getFinprodType2()) && (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(deal.getTradeMarket()) || DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(deal.getTradeMarket())) && (DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))) {
//					resultDto.setAmount(deal.getTradeAmt());
//				} else {
//					resultDto.setAmount(deal.getShareAmt());
//				}
                resultDto.setAmount(deal.getShareAmt());
                if (DictCodeListExt1.投资目的_卖空.getValue().equals(deal.getInvAim())) {
                    resultDto.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                    if (DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())) {
                        resultDto.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
                    }
                    else {
                        resultDto.setHodingType(DictCodeListExt1.持仓类型_卖空.getValue());
                    }
                    resultDto.setAmount(deal.getShareAmt().negate());
                }
                result.add(resultDto);
                // 结算数据下行，日终成交回报、质押式竞价回购、增加组合的标准券持仓登陆处理 start by chenzhuo
                if ((StringUtils.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), deal.getFinprodType2()))
                	   && (StringUtils.equals(DictCodeList.回购类型_质押式竞价回购.getValue(), assetType2))
                       && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                       && (DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))) {
                	PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                	ptlSecPositionFlowDto.setSeqno(IDGenerator.generate(20));
                	ptlSecPositionFlowDto.setPortfolioId(inputDto.getPortfolioId());
                	ptlSecPositionFlowDto.setSecAcctId(deal.getSecManageAcctId());
                	ptlSecPositionFlowDto.setFinprodId(BussinessConstant.XN_STANDARD_BOND_PRODID);
                	ptlSecPositionFlowDto.setBranch(0L);
                	ptlSecPositionFlowDto.setInvAim("999999");
                	ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
                	ptlSecPositionFlowDto.setCdate(deal.getTradeDate());
                	ptlSecPositionFlowDto.setCcy(deal.getCcy());
                	ptlSecPositionFlowDto.setAmount(ArithmeticUtil.divide(deal.getPrinAmt(), new BigDecimal(100), 2).negate());
                	ptlSecPositionFlowDto.setTradeId(deal.getTradeId());
                	result.add(ptlSecPositionFlowDto);
                }
                // 日终成交回报、质押式竞价回购、增加组合的标准券持仓登陆处理 end by chenzhuo
                return result;
            }

            /*
             * 1 输入金融产品交易Entity.金融产品类型（投管分类）in （“拆入”，“拆出”） and 金融产品交易Entity.交易方向 in （“买入”）
             */
            else if ((拆入_拆出.contains(deal.getFinprodType2()) && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))) {
                resultDto.setCdate(deal.getVdate());
                resultDto.setAmount(deal.getShareAmt());
                result.add(resultDto);
                return result;
            }

            /*
             * 1 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and 金融产品交易Entity.交易方向 in （“赎回”）
             *
             * 2 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“赎回”）and 收益类型(共通逻辑获取) = “净值型”
             *
             */
            else if (/*(DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
                      && DictCodeList.交易方向_赎回.getValue().equals(deal.getPs()))

                     ||*/ (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                         && DictCodeList.收益类型_净值型.getValue().equals(profitType)
                         && DictCodeList.交易方向_赎回.getValue().equals(deal.getPs()))
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    || (DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2())
                    && DictCodeList.交易方向_赎回.getValue().equals(deal.getPs()))
                    // add by etliu 20210421 权益类资产相关代码合并 end
                     || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                         && DictCodeList.收益类型_货币型.getValue().equals(profitType)
                         && DictCodeList.交易方向_赎回.getValue().equals(deal.getPs()))) {
                PtlSecPositionFlowDto resultDtozt = new PtlSecPositionFlowDto();
                BeanUtils.copyProperties(resultDto, resultDtozt);
                resultDtozt.setCdate(deal.getTradeDate());
                resultDtozt.setAmount(com.joyintech.framework.util.StringUtil.isNotEmpty(
                    deal.getShareAmt()) ? deal.getShareAmt() : deal.getPrinAmt());
                resultDtozt.setHodingType(DictCodeList.持仓类型_在途.getValue());
                result.add(resultDtozt);
                return result;

            }

            /*
             * ------------------裴家俊------------------add----------2018/11/23---------------------- 1 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and
             * 金融产品交易Entity.交易方向 in （“除权登记”）and 资产二级类型(共通逻辑获取)=“非货币基金”
             *
             * 2 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“除权登记”）and 收益类型(共通逻辑获取) = “净值型”
             *
             */
            /*
             * else if (((DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2()) && DictCodeList.基金类资产类型_非货币基金.getValue().equals(assetType2) &&
             * DictCodeList.交易方向_除权登记.getValue().equals(deal.getPs())) || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2()) &&
             * DictCodeList.收益类型_净值型.getValue().equals(profitType))) && DictCodeList.交易方向_除权登记.getValue().equals(deal.getPs())) {
             * if(DictCodeList.分红方式_红利再投.getValue().equals(inputDto.getBonusMode())){ PtlSecPositionFlowDto resultDtozt = new PtlSecPositionFlowDto();
             * BeanUtils.copyProperties(resultDto,resultDtozt); resultDtozt.setAmount(deal.getShareAmt()); resultDtozt.setCdate(deal.getTradeDate());
             * resultDtozt.setHodingType(DictCodeList.持仓类型_在途.getValue()); result.add(resultDtozt); return result; } }
             */

            /*
             * ------------------裴家俊------------------update----------2018/11/23---------------------- 1 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and
             * 金融产品交易Entity.交易方向 in （“红利再投”）and 资产二级类型(共通逻辑获取)=“非货币基金”
             *
             * 2 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“红利再投”）and 收益类型(共通逻辑获取) = “净值型”
             *
             */
            else if (((DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
                       && DictCodeList.交易方向_红利再投.getValue().equals(deal.getPs()))
                      || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                          && DictCodeList.收益类型_净值型.getValue().equals(profitType))
                      || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                          && DictCodeList.收益类型_货币型.getValue().equals(profitType)))
                     && DictCodeList.交易方向_红利再投.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getShareAmt());

                /*
                 * PtlSecPositionFlowDto resultDtozt = new PtlSecPositionFlowDto(); BeanUtils.copyProperties(resultDtozt, resultDto);
                 * resultDto.setCdate(deal.getTradeDate()); resultDtozt.setHodingType(DictCodeList.持仓类型_在途.getValue()); result.add(resultDtozt);
                 */

                /*
                 * PtlSecPositionFlowDto resultDtozt1 = new PtlSecPositionFlowDto(); BeanUtils.copyProperties(resultDto,resultDtozt1);
                 * resultDtozt1.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                 * resultDtozt1.setHodingType(DictCodeList.持仓类型_在途.getValue()); result.add(resultDtozt1);
                 */
                result.add(resultDto);
                return result;
            }

            /*
             * 1 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“申购”）and 收益类型(共通逻辑获取) = “利率型”
             *
             * 2 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).项目类 and 金融产品交易Entity.交易方向 in （“买入”）
             *
             */
            else if (((DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                       && DictCodeList.收益类型_收益型.getValue().equals(profitType))
                      // 一体化对应etliu start
                      // && DictCodeList.交易方向_申购.getValue().equals(deal.getPs()))
                      && (DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
                          || DictCodeList.交易方向_买入.getValue().equals(deal.getPs())))
                     // 一体化对应etliu end

                     || (DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(deal.getFinprodType2())
                         && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))) {
                if (DateUtil.compareDate(vDate, deal.getTradeDate()) != -1) {
                    resultDto.setCdate(vDate);
                }
                else {
                    resultDto.setCdate(deal.getTradeDate());
                }
                resultDto.setAmount(deal.getShareAmt());
                result.add(resultDto);

                if (DateUtil.compareDate(vDate, deal.getTradeDate()) >= 1) {
                    PtlSecPositionFlowDto resultDtozt = new PtlSecPositionFlowDto();
                    BeanUtils.copyProperties(resultDto, resultDtozt);
                    resultDtozt.setCdate(deal.getTradeDate());
                    resultDtozt.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    result.add(resultDtozt);

                    PtlSecPositionFlowDto resultDtozt1 = new PtlSecPositionFlowDto();
                    BeanUtils.copyProperties(resultDto, resultDtozt1);
                    resultDtozt1.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                    resultDtozt1.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    resultDtozt1.setCdate(vDate);
                    result.add(resultDtozt1);
                }
                return result;
            }

            // 增加基金类资产的持仓处理 qiangchengxi 2019年2月26日 start
            else if (((DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                       && (DictCodeList.收益类型_货币型.getValue().equals(profitType)
                           || DictCodeList.收益类型_净值型.getValue().equals(profitType)))
					// add by etliu 20210421 权益类资产相关代码合并 start
					||DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2())
					// add by etliu 20210421 权益类资产相关代码合并 end
                      || DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2()))
                     && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                if (inputDto.getPositionType().equals(DictCodeList.持仓类型_实际.getValue())) {
                    resultDto.setCdate(deal.getTradeDate());
                    resultDto.setAmount(deal.getShareAmt());
                    result.add(resultDto);
                }
            }
            else if (((DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                       && (DictCodeList.收益类型_货币型.getValue().equals(profitType)
                          /* || DictCodeList.收益类型_净值型.getValue().equals(profitType)*/))
                      /*|| DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())*/)
                     && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                result.add(resultDto);
            }
            // 增加基金类资产的持仓处理 qiangchengxi 2019年2月26日 end
            // -----------------add by 裴家俊 20190628------债券借贷----------start-------------
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券借贷融入 and 金融产品交易Entity.交易方向 in （“买入”）
             */
            else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(deal.getFinprodType2())
                     && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getVdate());
                resultDto.setAmount(deal.getShareAmt());
                // 融入到期现金交割，本方券和融入券的买入
                if (DictCodeListExt1.投资目的_卖空.getValue().equals(deal.getInvAim())) {
                    resultDto.setCdate(deal.getSettleDate());
                    resultDto.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                    if (DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())) {
                        resultDto.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
                    }
                    else {
                        resultDto.setHodingType(DictCodeListExt1.持仓类型_卖空.getValue());
                    }
                    resultDto.setAmount(deal.getShareAmt().negate());
                }
                result.add(resultDto);
                return result;
            }
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券借贷融出 and 金融产品交易Entity.交易方向 in （“买入”）
             */
            else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue().equals(deal.getFinprodType2())
                     && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getVdate());
                resultDto.setAmount(deal.getShareAmt());
                result.add(resultDto);
                return result;
            }
            // 融出到期现金交割，本方券的卖出
            else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue().equals(deal.getFinprodType2())
                    && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getShareAmt().negate());
                if (DictCodeListExt1.投资目的_卖空.getValue().equals(deal.getInvAim())) {
                    resultDto.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                    if (DictCodeList.持仓类型_可用.getValue().equals(inputDto.getPositionType())) {
                        resultDto.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
                    }
                    else {
                        resultDto.setHodingType(DictCodeListExt1.持仓类型_卖空.getValue());
                    }
                    resultDto.setAmount(deal.getShareAmt());
                }
                result.add(resultDto);
                return result;
            }
            // -----------------add by 裴家俊 20190628----债券借贷--------end--------------------------
            /*
             * 增加 股票除权登记 持仓处理 潘彬2019-7-18 start 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).股票 and 金融产品交易Entity.交易方向 in （除权登记）
             */
            else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(deal.getFinprodType2())
                     && DictCodeList.交易方向_除权登记.getValue().equals(deal.getPs()) && deal.getShareAmt() != null
                     && deal.getShareAmt().compareTo(new BigDecimal(0)) > 0) {
                // modify by etliu 20220219 start
                // GNS-875：【广农商场内清算】股票现金分红，权益登记日日终上交所解析JSMX03文件；深交所解析SJSJG文件后，成交数据明细表、各交易表和头寸表数据登录不正确
/*                resultDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getShareAmt());
                result.add(resultDto);*/
                // modify by etliu 20220219 end

            }
            // 增加 股票除权登记 持仓处理 潘彬2019-7-18 end

            /*
             * 增加 股票红利再投 持仓处理 潘彬2019-7-18 start 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).股票 and 金融产品交易Entity.交易方向 in （除权登记）
             */
            else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(deal.getFinprodType2())
                     && DictCodeList.交易方向_红利再投.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(deal.getShareAmt());
                result.add(resultDto);

                // GNS-932:股票红利再投,非工作流数据无需登录在途持仓 modify by fuyongsheng 20220225 start
                if(!inputDto.isNotWorkflow()){
                    PtlSecPositionFlowDto ztFlowDto = new PtlSecPositionFlowDto();
                    // 输入参数.组合代码
                    ztFlowDto.setPortfolioId(resultDto.getPortfolioId());
                    // 证券管理账户/通道代码
                    ztFlowDto.setSecAcctId(resultDto.getSecAcctId());
                    ztFlowDto.setFinprodId(resultDto.getFinprodId());
                    ztFlowDto.setBranch(0L);
                    ztFlowDto.setInvAim(StringUtil.isEmpty(deal.getInvAim()) ? "999999" : deal.getInvAim());
                    // 持仓类型-在途
                    ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // 金融产品交易Entity.结算日期
                    ztFlowDto.setCdate(deal.getSettleDate());
                    // 划款指令Entity.结算金额*-1
                    ztFlowDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                    // 金融产品交易Entity.币种
                    ztFlowDto.setCcy(deal.getCcy());
                    // 金融产品交易Entity.交易编号
                    ztFlowDto.setTradeId(deal.getTradeId());
                    ztFlowDto.setCreateUser(deal.getCreateUser());
                    ztFlowDto.setCreateDept(deal.getCreateDept());
                    ztFlowDto.setUpdateUser(deal.getCreateUser());
                    result.add(ztFlowDto);
                }
                // GNS-932:股票红利再投，非工作流数据无需登录在途持仓 modify by fuyongsheng 20220225 end
            }
            // 增加 股票红利再投 持仓处理 潘彬2019-7-18 end

            // add by wangpeng 20190724 start
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).股票 and 金融产品交易Entity.交易方向 in （买入/配股/增发（配售））
             */
            else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(deal.getFinprodType2())
                     && (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                         || DictCodeListExt2.交易方向_配股.getValue().equals(deal.getPs())
                         || DictCodeListExt2.交易方向_增发.getValue().equals(deal.getPs())
                         || DictCodeListExt2.交易方向_新股中签.getValue().equals(deal.getPs()))
                     // GZYHTZJY-1016:股票的申购确认只处理持仓,不处理现金 modify by fuyongsheng 20220104 start
                         || DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
                     // GZYHTZJY-1016:股票的申购确认只处理持仓,不处理现金 modify by fuyongsheng 20220104 end
            ) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getShareAmt());
                result.add(resultDto);
                return result;
            }

            // add by wangpeng 20190724 end

            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).外汇远期 add by qmh start
             */
            // 外汇远期持仓暂定不需要计算 delete by renping 2019-08-29 start
            // else if (DictCodeListExt1.金融产品类型_投管用_远期.getValue().equals(deal.getFinprodType2())) {
            // resultDto.setCdate(deal.getSettleDate());
            // resultDto.setAmount(deal.getPrinAmt());
            // result.add(resultDto);
            // return result;
            // }
            // 外汇远期持仓暂定不需要计算 delete by renping 2019-08-29 end
            // end
            // 利率互换买入移动到实际持仓时 renping 2019-08-29 start
            else if (DictCodeListExt1.金融产品类型_投管用_利率互换.getValue().equals(deal.getFinprodType2())
                     && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(deal.getPrinAmt());
                result.add(resultDto);
                return result;
            }
//            else if (StringUtils.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_基金.getValue())
//                     && (StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强减.getValue())
//                         || StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强增.getValue()))) {
//                if (StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强减.getValue())) {
//                    resultDto.setCdate(deal.getSettleDate());
//                    resultDto.setAmount(ArithmeticUtil.subtract(BigDecimal.ZERO, deal.getShareAmt()));
//                    result.add(resultDto);
//                    return result;
//                }
//                else if (StringUtils.equals(deal.getPs(), DictCodeList.交易方向_份额强增.getValue())) {
//                    resultDto.setCdate(deal.getSettleDate());
//                    resultDto.setAmount(deal.getShareAmt());
//                    result.add(resultDto);
//                    return result;
//                }
//            }
            // 利率互换买入移动到实际持仓时 renping 2019-08-29 end
            // 债转股交易 dingzhipeng 2019-11-22 start
            else if (StringUtils.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                     && StringUtils.equals(deal.getPs(), DictCodeListExt3.交易方向_债转股.getValue())) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                result.add(resultDto);
                PtlSecPositionFlowDto othResult = new PtlSecPositionFlowDto();
                // 实物交易存储了股票信息
                TrdFinProductBarter barterbDeal = inputDto.getTrdFinProductBarterList().get(0);
                BeanUtil.copyPropertiesIgnoreNull(othResult, resultDto);
                othResult.setFinprodId(barterbDeal.getFinprodId());
                othResult.setAmount(barterbDeal.getShareAmt());
                result.add(othResult);
                return result;
            }
            // 债转股交易 dingzhipeng 2019-11-22 end
            // 活期存放结息 交易方向为红利再投 结息方式为结转为本金 start
            else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                     && DictCodeListExt3.存款类资产类型_活期存放.getValue().equals(assetType2)
                     && DictCodeList.交易方向_红利再投.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(deal.getShareAmt());
                result.add(resultDto);
                return result;
            }
            // 活期存放结息 交易方向为红利再投 结息方式为结转为本金 end
            // 活期存放交易 交易方向为卖出 start
            else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                     && DictCodeListExt3.存款类资产类型_活期存放.getValue().equals(assetType2)
                     && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(deal.getShareAmt().negate());
                result.add(resultDto);
                return result;
            }
            // 活期存放 交易方向为卖出 end
            // 场内-信用保护凭证 start
            else if (DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue().equals(deal.getFinprodType2())
                    && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())) {
                resultDto.setCdate(deal.getTradeDate());
                if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                    resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                }
                if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs()) || DictCodeList.交易方向_申购.getValue().equals(deal.getPs())) {
                    resultDto.setAmount(deal.getShareAmt());
                }
                result.add(resultDto);
                return result;
            }
            // 场内-信用保护凭证 end
            // 场内-一级投标债 start
            else if (DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(deal.getFinprodType2())
                    && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())) {
                resultDto.setCdate(deal.getTradeDate());
                if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                    resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
                }
                if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                    resultDto.setAmount(deal.getShareAmt());
                }
                result.add(resultDto);
                // 前置交易编号不为空,说明是交收结果数据,需要反冲交收通知头寸
                if(StringUtil.isNotEmpty(deal.getbTradeId())){
                    List<TrdProductDeal> trdProductDeals = baseAssetCashPositionCommMapper.selectTrdProductDealByTotleTradeId(
                            deal.getbTradeId());
                    if(CollectionUtils.isNotEmpty(trdProductDeals)){
                        List<PtlSecPositionFlowDto> bPtlSecPositionFlows = baseAssetCashPositionCommMapper.selectAllPositionByTotle(
                                trdProductDeals.get(0).getTradeId());
                        for(PtlSecPositionFlowDto bPtlSecPositionFlow : bPtlSecPositionFlows){
                            bPtlSecPositionFlow.setCdate(deal.getTradeDate());
                            bPtlSecPositionFlow.setAmount(ArithmeticUtil.multiply(bPtlSecPositionFlow.getAmount(), new BigDecimal(-1)));
                            result.add(bPtlSecPositionFlow);
                        }
                    }
                }
                return result;
            }
            // 场内-一级投标债 end
        }
        return result;
    }

    @Override
    public List<PtlCashPositionFlowDto> cashDeal(PortfolioCashPositionsFlowInputDto inputDto) {
        // Assert.notNull(inputDto, "参数不能为null");
        // Assert.notNull(inputDto.getTrdProductDeal(), "金融产品交易Entity不能为null");
        // Assert.isTrue(StringUtil.isNotEmpty(inputDto.getTrdProductDeal().getFinprodId()), "金融产品代码不能为空");
        // Assert.isTrue(StringUtil.isNotEmpty(inputDto.getPositionType()), "持仓类型不能为空");
        // Assert.notEmpty(inputDto.getStlPayOrders(), "划款指令不能为空");
        List<PtlCashPositionFlowDto> result = new ArrayList<>();
        List<StlPayOrdersDto> stlPayOrders = inputDto.getStlPayOrders();
        String portfolioId = inputDto.getPortfolioId();
        TrdProductDeal deal = inputDto.getTrdProductDeal();
        TrdProductDealAdd dealAdd = inputDto.getTrdProductDealAdd();
        for (StlPayOrdersDto stlPayOrder : stlPayOrders) {
            if (DictCodeListExt1.指令类型_银证转账_银转证.getValue().equals(stlPayOrder.getOrderType())) {
                portfolioId = baseAssetCashPositionCommMapper.selectPortfolioIdByOrderId(stlPayOrder.getOrderId());
            }
            // 1、账户类型---add---裴家俊---2018/12/10------------
            List<String> accountTypes = baseAssetCashPositionCommMapper.selectAccountType(portfolioId, stlPayOrder.getMyAcctNo());

            // 2、起息日、收益类型取得(计划类利率型/项目类资产)
            Map<String, Object> vDateNato = baseAssetCashPositionCommMapper.selectVDateNato(
                inputDto.getTrdProductDeal().getFinprodId());

            // 起息日
            Date vDate = vDateNato == null ? null : (Date)vDateNato.get("VDATE");
            // 收益类型
            String profitType = vDateNato == null ? null : (String)vDateNato.get("PROFIT_TYPE");

            // 3、资产二级类型
            String assetType2 = StringUtil.isNotEmpty(
                inputDto.getAssetType2()) ? inputDto.getAssetType2() : baseAssetCashPositionCommMapper.selectFundType(
                    inputDto.getTrdProductDeal().getFinprodId());
            String stage = inputDto.getStage();
            PtlCashPositionFlowDto resultDto = new PtlCashPositionFlowDto();
            // 输入参数.组合代码
            resultDto.setPortfolioId(portfolioId);
            // 划款指令Entity.本方账户
            resultDto.setAcctId(stlPayOrder.getMyAcctNo());
            // 输入参数.持仓类型
            resultDto.setHodingType(inputDto.getPositionType());
            // 划款指令Entity.币种
            resultDto.setCcy(stlPayOrder.getCcy());
            // 划款指令Entity.指令编号
            resultDto.setOrderId(stlPayOrder.getOrderId());
            // 流程id
            resultDto.setWorkflowId(stlPayOrder.getWorkflowId());
            // 指令代码
            if (StringUtil.isNotEmpty(dealAdd)) {
                resultDto.setEntrustId(dealAdd.getEntrustId());
            }
            // 金融产品交易Entity.金融产品代码
            String chlId = null;
            if (StringUtil.isNotEmpty(deal.getChlId())) {
                String payType = baseAssetCashPositionCommMapper.getPayType(deal.getChlId(), stlPayOrder.getMyAcctNo());
                if (DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType)) { // 一次性时，从通道划款，记录通道代码
                    resultDto.setChlFinprodId(deal.getChlId());
                    chlId = deal.getChlId();
                }
            }
            resultDto.setCreateUser(deal.getCreateUser());
            resultDto.setCreateDept(deal.getCreateDept());
            resultDto.setUpdateUser(deal.getCreateUser());
            // “可用”或“实际”
            /*
             * 金融产品交易Entity.金融产品类型（投管分类）in (金融产品类型_投管用_理财产品,金融产品类型_投管用_理财子产品,金融产品类型_投管用_理财产品分层) and 金融产品交易Entity.交易方向 in
             * (交易方向_产品赎回,交易方向_产品到期、交易方向_份额强减、交易方向_产品提前赎回、交易方向_产品违约赎回、交易方向_提前终止)
             */
            if ((DictCodeListExt1.金融产品类型_投管用_理财产品.getValue().equals(deal.getFinprodType2())
                 || DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue().equals(deal.getFinprodType2())
                 || DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue().equals(deal.getFinprodType2()))
                && (DictCodeList.交易方向_产品赎回.getValue().equals(deal.getPs())
                    || DictCodeList.交易方向_产品到期.getValue().equals(deal.getPs())
                    || DictCodeList.交易方向_份额强减.getValue().equals(deal.getPs())
                    || DictCodeList.交易方向_产品提前赎回.getValue().equals(deal.getPs())
                    || DictCodeList.交易方向_产品违约赎回.getValue().equals(deal.getPs())
                    || DictCodeList.交易方向_产品现金分红.getValue().equals(deal.getPs())
                    || DictCodeList.交易方向_中间付息.getValue().equals(deal.getPs())
                    || DictCodeList.交易方向_提前终止.getValue().equals(deal.getPs()))) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                result.add(resultDto);
                return result;
            }

            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）and 金融产品交易Entity.交易平台
             * in （“场内交易”） and 输入划款指令Entity.账户类型=数据字典.账户类型.托管账户
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）and 金融产品交易Entity.交易平台
             * ！=“场内交易”
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).逆回购 and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）and
             * 资产二级类型(共通逻辑获取)=“质押式协议”
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).逆回购 and 资产二级类型(共通逻辑获取)=“质押式竞价” and 金融产品交易Entity.交易方向 in （“买入”） and 划款指令Entity.账户类型=数据字典.账户类型.托管账户
             *
             * 日期 金融产品交易Entity.交割日 数量 划款指令Entity.结算金额*-1
             */
            if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                && ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                     && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()))
                    || (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                        && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                        && accountTypes.contains(DictCodeListExt1.资金账户类型_产品托管户.getValue()))
                    // 非银行间券商和一次性通道为交易日
                    || (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                        && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                        && !accountTypes.contains(DictCodeListExt1.资金账户类型_券商资金账户.getValue()) && StringUtil.isEmpty(chlId))
                    || ((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2()))
                        && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                        && (DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)
                                ||DictCodeList.回购类型_质押式回购.getValue().equals(assetType2))
                        && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))
                    || ((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2()))
                        /* && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()) */
                        && DictCodeList.回购类型_质押式竞价回购.getValue().equals(assetType2)
                        && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                        && accountTypes.contains(DictCodeListExt1.资金账户类型_产品托管户.getValue()))
                    || ((DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(deal.getFinprodType2()))
                        && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                        && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())))) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).拆出 and 金融产品交易Entity.交易方向 in （“买入”）
             *
             * 日期 金融产品交易Entity.起息日 数量 划款指令Entity.结算金额*-1
             *
             */
            else if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                     && DictCodeListExt1.金融产品类型_投管用_拆出.getValue().equals(deal.getFinprodType2())) {
                resultDto.setCdate(deal.getVdate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).拆入 and 金融产品交易Entity.交易方向 in （“到期”）
             *
             *
             */
            else if (DictCodeList.交易方向_到期.getValue().equals(deal.getPs())
                     && ((DictCodeListExt1.金融产品类型_投管用_拆入.getValue().equals(deal.getFinprodType2())
                          || DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue().equals(deal.getFinprodType2())))) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).正回购 and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 金融产品交易Entity.交易方向 in （“到期”）
             *
             * 日期 金融产品交易Entity.到期日 数量 划款指令Entity.结算金额*-1
             *
             */
            else if (DictCodeList.交易方向_到期.getValue().equals(deal.getPs())
                     && (((DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(deal.getFinprodType2())
                              || DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2()))
                             && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())))) {
                resultDto.setCdate(deal.getMdate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）and 金融产品交易Entity.交易平台
             * in （“场内交易”） and 输入划款指令Entity.账户类型=数据字典.账户类型.交易所资金账户
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).逆回购 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）and
             * 资产二级类型(共通逻辑获取)=“质押式竞价” and 输入划款指令Entity.账户类型=数据字典.账户类型.交易所资金账户
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).逆回购 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）and
             * 资产二级类型(共通逻辑获取)=“质押式协议”
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).正回购 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“到期”）
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).存款类 and 金融产品交易Entity.交易方向 in （“买入”）
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“申购”）and 收益类型(共通逻辑获取) = “利率型”
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).项目类 and 金融产品交易Entity.交易方向 in （“买入”）
             *
             * 日期 金融产品交易Entity.交易日 数量 划款指令Entity.结算金额*-1
             */
            else if ((DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                      && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                      && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                      && !DictCodeList.交易平台_外汇交易中心.getValue().equals(deal.getTradePlat()) /* 等于场内 ===> 不等于银行间 */
                      // 一次性通道确认日为交易日
                      && StringUtil.isNotEmpty(chlId))
                     || (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2())
                         && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                         && DictCodeList.回购类型_质押式竞价回购.getValue().equals(assetType2)
                         && accountTypes.contains(DictCodeListExt1.资金账户类型_券商资金账户.getValue())
                         && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))
                     || (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2())
                         && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                         && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)
                         && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))
                     || (DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(deal.getFinprodType2())
                         && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                         && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))

                     || (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                         && DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2()))
                     // 一体化对应etliu start
                     // || (DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
                     || ((DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
                          || DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))
                         // 一体化对应etliu end
                         && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                         && DictCodeList.收益类型_收益型.getValue().equals(profitType))

            ) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            else if(DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                    && DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(deal.getFinprodType2())){
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            else if ((DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                      && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                      && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                      && !DictCodeList.交易平台_外汇交易中心.getValue().equals(deal.getTradePlat()) /* 等于场内 ===> 不等于银行间 */
                      && accountTypes.contains(DictCodeListExt1.资金账户类型_券商资金账户.getValue()))) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            else if ((DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(deal.getFinprodType2())
                      && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                      && DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            else if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2())
                      && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                      && DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))) {
                if ((StringUtil.isNotEmpty(inputDto.getIeFlag())
                        && inputDto.getIeFlag().equals(DictCodeListExt1.收支方向_收入.getValue()))
                        ||
                        (StringUtil.isNotEmpty(stlPayOrder.getPs()) && stlPayOrder.getPs().equals(DictCodeListExt1.收支方向_收入.getValue()))
                ) {
                    if (!DictCodeList.持仓类型_实际.getValue().equals(inputDto.getPositionType())) {
                        return result;
                    }// 到期续做场景，收支方向为收入，流程中不出流水
                }
                else {// 正常场景及正回购收支方向为支出的
                    resultDto.setCdate(deal.getSettleDate());
                    resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }
            }
            else if (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2())
                      && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                      && DictCodeList.交易方向_到期.getValue().equals(deal.getPs())
                    && (
                    (StringUtil.isNotEmpty(inputDto.getIeFlag()) && inputDto.getIeFlag().equals(DictCodeListExt1.收支方向_支出.getValue()))
                    ||
                    (StringUtil.isNotEmpty(stlPayOrder.getPs()) && stlPayOrder.getPs().equals(DictCodeListExt1.收支方向_支出.getValue()))
                 )
            ) {
                resultDto.setCdate(deal.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }

            /*------------裴家俊---------------add-------------2018/11/23---------------
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金类 and 金融产品交易Entity.交易方向 in （“申购”）
             *
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“申购”）and 收益类型(共通逻辑获取) = “净值型”
             *
             */
            // 场内基金改造 modify by renping 20191117 start
            else if ((((DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
                        || DictCodeListExt2.交易方向_认购.getValue().equals(deal.getPs()))
                       // 场内基金改造 modify by renping 20191117 end
                       && DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2()))
                      || (DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
                          && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                          && DictCodeList.收益类型_净值型.getValue().equals(profitType))
					  // add by etliu 20210421 权益类资产相关代码合并 start
                      || (DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
                        && DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2())
                        )
					  // add by etliu 20210421 权益类资产相关代码合并 end
                      || (DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
                          && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                          && DictCodeList.收益类型_货币型.getValue().equals(profitType)))
                     || (DictCodeListExt1.交易方向_保证金划款.getValue().equals(deal.getPs())
                         && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2()))) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);

                if (inputDto.getPositionType().equals(DictCodeList.持仓类型_实际.getValue())) {
                    PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                    // 输入参数.组合代码
                    ztFlowDto.setPortfolioId(portfolioId);
                    // 划款指令Entity.本方账户
                    ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                    // 输入参数.持仓类型
                    ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // 日期
                    ztFlowDto.setCdate(deal.getTradeDate());
                    // 划款指令Entity.币种
                    ztFlowDto.setCcy(stlPayOrder.getCcy());
                    // 划款指令Entity.指令编号
                    ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                    // 金融产品交易Entity.金融产品代码
                    ztFlowDto.setChlFinprodId(chlId);
                    ztFlowDto.setCreateUser(deal.getCreateUser());
                    ztFlowDto.setCreateDept(deal.getCreateDept());
                    ztFlowDto.setUpdateUser(deal.getCreateUser());
                    ztFlowDto.setAmount(stlPayOrder.getSettleAmt());
                    ztFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(ztFlowDto);
                }
                return result;
            }
            else if (
                    // GZYHTZJY-1001:债券的申购只处理现金,不处理持仓;申购确认只处理持仓,不处理现金 modify by fuyongsheng 20220104 start
                    // (DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
                    (DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
                    // GZYHTZJY-1001:债券的申购只处理现金,不处理持仓;申购确认只处理持仓,不处理现金 modify by fuyongsheng 20220104 end
                      && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2()))) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                if (BigDecimal.ZERO.compareTo(resultDto.getAmount()) != 0
                    && StringUtil.equals(stlPayOrder.getPs(), DictCodeListExt1.收付方向_支出.getValue())) {// 需求变更，出钱，入流水，否则不需要出实际流水
                    result.add(resultDto);
                }
                if (inputDto.getPositionType().equals(DictCodeList.持仓类型_实际.getValue())) {
                    PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                    // 输入参数.组合代码
                    ztFlowDto.setPortfolioId(portfolioId);
                    // 划款指令Entity.本方账户
                    ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                    // 输入参数.持仓类型
                    ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // 日期
                    ztFlowDto.setCdate(deal.getTradeDate());
                    // 划款指令Entity.币种
                    ztFlowDto.setCcy(stlPayOrder.getCcy());
                    // 划款指令Entity.指令编号
                    ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                    // 金融产品交易Entity.金融产品代码
                    ztFlowDto.setChlFinprodId(chlId);
                    ztFlowDto.setCreateUser(deal.getCreateUser());
                    ztFlowDto.setCreateDept(deal.getCreateDept());
                    ztFlowDto.setUpdateUser(deal.getCreateUser());
                    ztFlowDto.setAmount(ArithmeticUtil.multiply(deal.getTradeAmt(), new BigDecimal(-1)));// 存为min(保证金,中签券面)
                    ztFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(ztFlowDto);
                }
                return result;
            }
            else if ((DictCodeListExt1.交易方向_保证金回款.getValue().equals(deal.getPs())
                      && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2()))) {
                resultDto.setCdate(deal.getTradeDate());
                resultDto.setAmount(stlPayOrder.getSettleAmt());
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                // if(BigDecimal.ZERO.compareTo(resultDto.getAmount())!=0){//流水不等于零时，存入流水
                result.add(resultDto);
                // }
                if (inputDto.getPositionType().equals(DictCodeList.持仓类型_实际.getValue())) {
                    PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                    // 输入参数.组合代码
                    ztFlowDto.setPortfolioId(portfolioId);
                    // 划款指令Entity.本方账户
                    ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                    // 输入参数.持仓类型
                    ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // 日期
                    ztFlowDto.setCdate(deal.getTradeDate());
                    // 划款指令Entity.币种
                    ztFlowDto.setCcy(stlPayOrder.getCcy());
                    // 划款指令Entity.指令编号
                    ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                    // 金融产品交易Entity.金融产品代码
                    ztFlowDto.setChlFinprodId(chlId);
                    ztFlowDto.setCreateUser(deal.getCreateUser());
                    ztFlowDto.setCreateDept(deal.getCreateDept());
                    ztFlowDto.setUpdateUser(deal.getCreateUser());
                    ztFlowDto.setAmount(ArithmeticUtil.multiply(deal.getTradeAmt(), new BigDecimal(-1)));// 存为退款金额
                    ztFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(ztFlowDto);
                }
                return result;
            }
            /*
             * 输入金融产品交易Entity.金融产品类型(投管分类) = 数据字典缓存.金融产品类型（投管分类).理财产品 and 金融产品交易Entity.交易方向 in （“产品赎回”，“产品分红”） 输入金融产品交易Entity.业务类型 in （“自定义现金流”）and
             * 金融产品交易Entity.交易方向 in （“支付”）
             *
             * 日期 划款指令Entity.划款日
             *
             * 数量 划款指令Entity.结算金额*-1
             */
            else if ((产品赎回_现金分红_付费.contains(deal.getPs())
                      && DictCodeListExt1.金融产品类型_投管用_理财产品.getValue().equals(deal.getFinprodType2()))
                     || (DictCodeList.共通业务类型_自定义现金流.getValue().equals(deal.getBusiType())
                         && DictCodeList.交易方向_支付.getValue().equals(deal.getPs()))) {
                resultDto.setCdate(stlPayOrder.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                if ((DictCodeList.共通业务类型_自定义现金流.getValue().equals(deal.getBusiType())
                     && DictCodeList.交易方向_支付.getValue().equals(deal.getPs()))) {
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                }
                result.add(resultDto);
                return result;
            }

            // 通道增加
            else if (DictCodeList.交易方向_付费.getValue().equals(deal.getPs())
                     && DictCodeListExt1.一次性费用类型_通道费.getValue().equals(deal.getFinprodType2())) {
                resultDto.setCdate(stlPayOrder.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                result.add(resultDto);

                // 取得子组合分配比例，分配费用到子组合
                List<Map<String, Object>> 子组合继承比例List = AllTradeCommonUtil.getSubPortfolioDispercent(resultDto.getPortfolioId());
                if (子组合继承比例List != null) {
                    BigDecimal 合计 = BigDecimal.ZERO;
                    for (int i = 0; i < 子组合继承比例List.size(); i++ ) {
                        PtlCashPositionFlowDto ptlCashPositionFlow1 = new PtlCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(ptlCashPositionFlow1, resultDto);
                        result.add(ptlCashPositionFlow1);

                        ptlCashPositionFlow1.setPortfolioId((String)子组合继承比例List.get(i).get("PORTFOLIO_ID"));
                        if (i == 子组合继承比例List.size() - 1) {
                            ptlCashPositionFlow1.setAmount(ArithmeticUtil.subtract(resultDto.getAmount(), 合计));
                        }
                        else {
                            BigDecimal amount = ArithmeticUtil.round(
                                ArithmeticUtil.multiply(resultDto.getAmount(), (BigDecimal)子组合继承比例List.get(i).get("DIS_PERCENT")),
                                2, RoundingMode.HALF_UP);
                            合计 = ArithmeticUtil.doAdd(合计, amount);
                            ptlCashPositionFlow1.setAmount(amount);
                        }
                    }
                }
                return result;
            }

            // 输入划款指令Entity.指令类型 in （“银证转账”）
            else if (DictCodeListExt1.指令类型_银证转账_银转证.getValue().equals(stlPayOrder.getOrderType())
                     || DictCodeListExt1.指令类型_银证转账_证转银.getValue().equals(stlPayOrder.getOrderType())) {
                resultDto.setCdate(stlPayOrder.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);

                PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                // 输入参数.组合代码
                ztFlowDto.setPortfolioId(portfolioId);
                // 划款指令Entity.本方账户
                ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                // 输入参数.持仓类型
                ztFlowDto.setHodingType(inputDto.getPositionType());
                // 划款指令Entity.币种
                ztFlowDto.setCcy(stlPayOrder.getCcy());
                // 划款指令Entity.指令编号
                ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                // 金融产品交易Entity.金融产品代码
                ztFlowDto.setChlFinprodId(chlId);
                ztFlowDto.setCreateUser(deal.getCreateUser());
                ztFlowDto.setCreateDept(deal.getCreateDept());
                ztFlowDto.setUpdateUser(deal.getCreateUser());
                // ztFlowDto.setAcctId(stlPayOrder.getCounterAcctNo());
                ztFlowDto.setCdate(stlPayOrder.getSettleDate());
                ztFlowDto.setAmount(stlPayOrder.getSettleAmt());
                ztFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(ztFlowDto);
                return result;
            }
            // 增加基金类资产的持仓处理 qiangchengxi 2019年2月26日 start
            else if (((DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                       && (DictCodeList.收益类型_货币型.getValue().equals(profitType)
                           || DictCodeList.收益类型_净值型.getValue().equals(profitType)))
                      || DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
					  // add by etliu 20210421 权益类资产相关代码合并 start
					  || DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2()))
					  // add by etliu 20210421 权益类资产相关代码合并 end
                     && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                resultDto.setCdate(stlPayOrder.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            // 增加基金类资产的持仓处理 qiangchengxi 2019年2月26日 end
            // 增加债券借贷到期现金处理 裴家俊 2019年7月6日 start---
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券借贷融入 and 金融产品交易Entity.交易方向 in （“到期”）
             */
            else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(deal.getFinprodType2())
                     && DictCodeList.交易方向_到期.getValue().equals(deal.getPs())) {
                resultDto.setCdate(stlPayOrder.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }

            // 增加债券借贷到期现金处理 裴家俊 2019年7月6日 end---

            // add by wangpeng 20190724 start
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).股票 and 金融产品交易Entity.交易方向 in （买入/配股/增发（配售））
             */
            else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(deal.getFinprodType2())
                     && (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                         || DictCodeListExt2.交易方向_配股.getValue().equals(deal.getPs())
                         || DictCodeListExt2.交易方向_增发.getValue().equals(deal.getPs())
                         || DictCodeListExt2.交易方向_新股中签.getValue().equals(deal.getPs()))
                         // GZYHTZJY-1016:股票的申购只处理现金,不处理持仓;申购确认只处理持仓,不处理现金 modify fuyongsheng 20220104 start
                         || DictCodeList.交易方向_申购.getValue().equals(deal.getPs())) {
                         // GZYHTZJY-1016:股票的申购只处理现金,不处理持仓;申购确认只处理持仓,不处理现金 modify fuyongsheng 20220104 end
                resultDto.setCdate(stlPayOrder.getSettleDate());
                resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                result.add(resultDto);
                return result;
            }
            // add by wangpeng 20190724 end

            //add by  liutf 20200108 start
            /*
             * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型_投管用_期权 and
             * 收付方向 = 支付 金融产品交易Entity.
             */
            else if (DictCodeListExt1.金融产品类型_投管用_期权.getValue().equals(deal.getFinprodType2())) {
                resultDto.setCdate(deal.getPayDate());
                if(StringUtil.equals(DictCodeListExt1.收付方向_支出.getValue(), deal.getPsPayment()) || StringUtil.equals(DictCodeList.交易方向_买入.getValue(), deal.getPs())){
                    resultDto.setAmount(ArithmeticUtil.multiply(deal.getTradeAmt(), new BigDecimal(-1)));
                }
                else if(StringUtil.equals(DictCodeList.收付方向_收入.getValue(), deal.getPsPayment()) || StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), deal.getPs())){
                    resultDto.setAmount(deal.getTradeAmt());
                }
                result.add(resultDto);
                return result;
            }
            //add by  liutf 20200108 end
            // add by jiangpeng on 20220428 新增一级申购业务处理 start
            else if (DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(deal.getFinprodType2())
                    && ("ISSUE_CLASS_01".equals(assetType2) || "ISSUE_CLASS_02".equals(assetType2))// 发行方式 标志是否为一级投标
                    && StringUtil.equals(DictCodeList.交易方向_买入.getValue(), deal.getPs())) {
                resultDto.setCdate(stlPayOrder.getSettleDate());
                resultDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
                // 交收 增加可用金额 减少实际
                if ("03".equals(stage)) {
                    PtlCashPositionFlowDto flowDto1 = new PtlCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(flowDto1, resultDto);
                    flowDto1.setAmount(stlPayOrder.getSettleAmt());
                    flowDto1.setHodingType(DictCodeList.持仓类型_可用.getValue());
                    result.add(flowDto1);
                    PtlCashPositionFlowDto flowDto2 = new PtlCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(flowDto2, resultDto);
                    flowDto2.setAmount(stlPayOrder.getSettleAmt().negate());
                    flowDto2.setHodingType(DictCodeList.持仓类型_实际.getValue());
                    flowDto2.setIsDeleteAble(DictCodeList.是否_否.getValue());
                    result.add(flowDto2);
                } else {
                    // 登记/成交 扣减可用金额
                    resultDto.setAmount(stlPayOrder.getSettleAmt().negate());
                    result.add(resultDto);
                }
                return result;
            }
            // add by jiangpeng on 20220428 新增一级申购业务处理 end

            // 2 只插入“实际”
            if (DictCodeList.持仓类型_实际.getValue().equals(inputDto.getPositionType())) {

                /*
                 * 金融产品交易Entity.金融产品类型（投管分类）in (金融产品类型_投管用_理财产品,金融产品类型_投管用_理财子产品,金融产品类型_投管用_理财产品分层) and 金融产品交易Entity.交易方向 in
                 * (交易方向_产品申购,交易方向_产品认购、交易方向_产品红利转投、交易方向_份额强增)
                 */
                if ((DictCodeListExt1.金融产品类型_投管用_理财产品.getValue().equals(deal.getFinprodType2())
                     || DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue().equals(deal.getFinprodType2())
                     || DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue().equals(deal.getFinprodType2()))
                    && (产品认购_申购.contains(deal.getPs()) || DictCodeList.交易方向_产品红利转投.getValue().equals(deal.getPs())
                        || DictCodeList.交易方向_份额强增.getValue().equals(deal.getPs()))) {
                    resultDto.setCdate(deal.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    result.add(resultDto);
                    return result;
                }
                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“卖出”,“行权”,“到期”,“收息”,“还本”）
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“卖出”,“到期”,“收息”,“还本”））
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).正回购 and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).正回购 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）and
                 * 资产二级类型(共通逻辑获取)=“质押式竞价”
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).存款 and 金融产品交易Entity.交易方向 in （“收息”，“到期”） and 资产二级类型(共通逻辑获取)=“同业借款”
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).存款 and 金融产品交易Entity.交易方向 in （“提前支取”，“到期”） and 资产二级类型(共通逻辑获取)=“存放同业”
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“赎回”，还本、“收息”，“到期”） and 收益类型(共通逻辑获取) = “利率型”
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).项目类 and 金融产品交易Entity.交易方向 in （“卖出”,“收息”，“还本”，“到期”）
                 */
                if ((还本_行权_收息_到期_卖出.contains(deal.getPs())
                     && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                     && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()))
                    || (还本_收息_到期_卖出.contains(deal.getPs())
                        && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                        && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()))
                    || (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2())
                        && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                        && (DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)
                        || DictCodeList.回购类型_质押式回购.getValue().equals(assetType2))
                        && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))
                    || (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2())
                        /* && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()) */
                        && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                        && DictCodeList.回购类型_质押式竞价回购.getValue().equals(assetType2))
                    || (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(deal.getFinprodType2())
                        // && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()) // 现在只有银行间的交易，去掉这个判断
                        && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))
                    || (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                        && (DictCodeList.交易方向_收息.getValue().equals(deal.getPs()) || 还本_卖出_到期.contains(deal.getPs()))
                        && DictCodeList.存款类资产类型_同业借款.getValue().equals(assetType2))

                    || (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                        && (DictCodeList.交易方向_提前支取.getValue().equals(deal.getPs()) || 还本_收息_到期_卖出.contains(deal.getPs()))
                        && DictCodeList.存款类资产类型_存放同业.getValue().equals(assetType2))

                    || (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                        && DictCodeList.收益类型_收益型.getValue().equals(profitType) && 还本_到期_收息.contains(deal.getPs()))

                    || (DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(deal.getFinprodType2())
                        && 还本_到期_收息.contains(deal.getPs()))) {
                    resultDto.setCdate(deal.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }
                /*-------------裴家俊-----------delete-------------2018/11/23-----------------
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金类 and 金融产品交易Entity.交易方向 in （“申购”）
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“申购”）and 收益类型(共通逻辑获取) = “净值型”
                 *
                 */
                /*
                 * else if (((DictCodeList.交易方向_申购.getValue().equals(deal.getPs()) && DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())) ||
                 * (DictCodeList.交易方向_申购.getValue().equals(deal.getPs()) && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2()) &&
                 * DictCodeList.收益类型_净值型.getValue().equals(profitType)))) { resultDto.setCdate(deal.getTradeDate());
                 * resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1))); result.add(resultDto);
                 *
                 * PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto(); // 输入参数.组合代码 ztFlowDto.setPortfolioId(portfolioId); // 划款指令Entity.本方账户
                 * ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo()); // 输入参数.持仓类型 ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue()); // 日期
                 * ztFlowDto.setCdate(deal.getTradeDate()); // 划款指令Entity.币种 ztFlowDto.setCcy(stlPayOrder.getCcy()); // 划款指令Entity.指令编号
                 * ztFlowDto.setOrderId(stlPayOrder.getOrderId()); // 金融产品交易Entity.金融产品代码 ztFlowDto.setChlFinprodId(chlId);
                 * ztFlowDto.setCreateUser(deal.getCreateUser()); ztFlowDto.setCreateDept(deal.getCreateDept()); ztFlowDto.setUpdateUser(deal.getCreateUser());
                 * ztFlowDto.setAmount(stlPayOrder.getSettleAmt()); result.add(ztFlowDto); return result; }
                 */
                // 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).拆入 and 金融产品交易Entity.交易方向 in （“买入”）
                else if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                         && (DictCodeListExt1.金融产品类型_投管用_拆入.getValue().equals(deal.getFinprodType2())
                             || DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue().equals(deal.getFinprodType2()))) {
                    resultDto.setCdate(deal.getVdate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }

                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).拆出 and 金融产品交易Entity.交易方向 in （“到期”）
                 */
                else if (DictCodeList.交易方向_到期.getValue().equals(deal.getPs())
                    && (DictCodeListExt1.金融产品类型_投管用_拆出.getValue().equals(deal.getFinprodType2()))){
                   resultDto.setCdate(deal.getSettleDate());
                   resultDto.setAmount(stlPayOrder.getSettleAmt());
                   resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                   /**** 回购释放头寸处理*****chenlei1*add***start **/
                   if ((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2()))
                       && (DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))
                       && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)) {
                       Map<String, Object> rsmap = new HashMap<String, Object>();
                       rsmap.put("tradeId", deal.getFinprodId());
                       rsmap.put("finprodType2", deal.getFinprodType2());
                       Map<String, Object> statusMap = baseAssetCashPositionCommMapper.selectTradeReleaseStatus(rsmap);
                       String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
                       String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");
                       if(deal.getTradeId().endsWith("R")){//是否撤销交易
                           if (DictCodeList.是否_否.getValue().equals(isManualRelease)) {//IF 是否手工释放 = 否
                                result.add(resultDto);
                           }else {
                               return result;
                           }
                       }else{//到期确认交易
                           if (DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)) {
                               return result;
                           }else {//IF  未释放
                               result.add(resultDto);
                           }
                       }
                   }
                   else {
                       result.add(resultDto);
                   }
                   /**** 回购释放头寸处理*****chenlei1*add***end **/
                   return result;
               }
                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).逆回购 and 金融产品交易Entity.交易场所 = 数据字典缓存.交易场所.银行间 金融产品交易Entity.交易方向 in （“到期”）
                 */
                else if (DictCodeList.交易方向_到期.getValue().equals(deal.getPs())
                         && (((DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(deal.getFinprodType2())
                                  || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2()))
                                 && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())))) {
                    resultDto.setCdate(deal.getMdate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    /**** 回购释放头寸处理*****chenlei1*add***start **/
                    if ((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2()))
                        && (DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))
                        && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)) {
                        Map<String, Object> rsmap = new HashMap<String, Object>();
                        rsmap.put("tradeId", deal.getFinprodId());
                        rsmap.put("finprodType2", deal.getFinprodType2());
                        Map<String, Object> statusMap = baseAssetCashPositionCommMapper.selectTradeReleaseStatus(rsmap);
                        String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
                        String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");
                        if(deal.getTradeId().endsWith("R")){//是否撤销交易
                            if (DictCodeList.是否_否.getValue().equals(isManualRelease)) {//IF 是否手工释放 = 否
                            	 result.add(resultDto);
                            }else {
                            	return result;
                            }
                        }else{//到期确认交易
                            if (DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)) {
                                return result;
                            }else {//IF  未释放
                                result.add(resultDto);
                            }
                        }
                    }
                    else {
                        result.add(resultDto);
                    }
                    /**** 回购释放头寸处理*****chenlei1*add***end **/
                    return result;
                }

                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金类 and 金融产品交易Entity.交易方向 in （“申购确认”）
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“申购确认”）and 收益类型(共通逻辑获取) = “净值型”
                 *
                 */
                else if ((DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
                          && DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2()))
                         // 场内基金改造 add by renping 20191209 start
                         || (DictCodeList.交易方向_认购确认.getValue().equals(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2()))
                         // 场内基金改造 add by renping 20191209 end
                         || (DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                             && DictCodeList.收益类型_净值型.getValue().equals(profitType))
					     // add by etliu 20210421 权益类资产相关代码合并 start
                         || (DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
                             && DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2()))
					     // add by etliu 20210421 权益类资产相关代码合并 end
                         || (DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                             && DictCodeList.收益类型_货币型.getValue().equals(profitType))) {
                    PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                    // 输入参数.组合代码
                    ztFlowDto.setPortfolioId(portfolioId);
                    // 划款指令Entity.本方账户
                    ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                    // 输入参数.持仓类型
                    ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // 划款指令Entity.币种
                    ztFlowDto.setCcy(stlPayOrder.getCcy());
                    // 划款指令Entity.指令编号
                    ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                    // 金融产品交易Entity.金融产品代码
                    ztFlowDto.setChlFinprodId(chlId);
                    ztFlowDto.setCreateUser(deal.getCreateUser());
                    ztFlowDto.setCreateDept(deal.getCreateDept());
                    ztFlowDto.setUpdateUser(deal.getCreateUser());
                    // ztFlowDto.setAcctId(stlPayOrder.getCounterAcctNo());
                    ztFlowDto.setCdate(deal.getTradeDate());
                    ztFlowDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                    ztFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(ztFlowDto);

                    return result;
                }

                /*------------------裴家俊-------------------------update-------------2018/11/23---------------
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“赎回”）and 收益类型(共通逻辑获取) = “利率型”
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).项目类 and 金融产品交易Entity.交易方向 in （“卖出”）
                 *
                 */
                // 一体化对应etliu start
                // else if ((DictCodeList.交易方向_赎回.getValue().equals(deal.getPs())
                else if (((DictCodeList.交易方向_赎回.getValue().equals(deal.getPs())
                           || DictCodeList.交易方向_卖出.getValue().equals(deal.getPs()))
                          // 一体化对应etliu end
                          && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                          && DictCodeList.收益类型_收益型.getValue().equals(profitType))
                         || (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(deal.getFinprodType2()))) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);

                    // PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                    // // 输入参数.组合代码
                    // ztFlowDto.setPortfolioId(portfolioId);
                    // // 划款指令Entity.本方账户
                    // ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                    // // 输入参数.持仓类型
                    // ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // // 划款指令Entity.币种
                    // ztFlowDto.setCcy(stlPayOrder.getCcy());
                    // // 划款指令Entity.指令编号
                    // ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                    // // 金融产品交易Entity.金融产品代码
                    // ztFlowDto.setChlFinprodId(chlId);
                    // ztFlowDto.setCreateUser(deal.getCreateUser());
                    // ztFlowDto.setCreateDept(deal.getCreateDept());
                    // ztFlowDto.setUpdateUser(deal.getCreateUser());
                    // // ztFlowDto.setAcctId(stlPayOrder.getCounterAcctNo());
                    // ztFlowDto.setCdate(deal.getTradeDate());
                    // ztFlowDto.setAmount(stlPayOrder.getSettleAmt());
                    // ztFlowDto.setSplitType(DictCodeList.拆分标识_已拆分.getValue());
                    // result.add(ztFlowDto);
                    //
                    // PtlCashPositionFlowDto ztFlowDto1 = new PtlCashPositionFlowDto();
                    // // 输入参数.组合代码
                    // ztFlowDto1.setPortfolioId(portfolioId);
                    // // 划款指令Entity.本方账户
                    // ztFlowDto1.setAcctId(stlPayOrder.getMyAcctNo());
                    // // 输入参数.持仓类型
                    // ztFlowDto1.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // // 划款指令Entity.币种
                    // ztFlowDto1.setCcy(stlPayOrder.getCcy());
                    // // 划款指令Entity.指令编号
                    // ztFlowDto1.setOrderId(stlPayOrder.getOrderId());
                    // // 金融产品交易Entity.金融产品代码
                    // ztFlowDto1.setChlFinprodId(chlId);
                    // ztFlowDto1.setCreateUser(deal.getCreateUser());
                    // ztFlowDto1.setCreateDept(deal.getCreateDept());
                    // ztFlowDto1.setUpdateUser(deal.getCreateUser());
                    // // ztFlowDto1.setAcctId(stlPayOrder.getCounterAcctNo());
                    // ztFlowDto1.setCdate(stlPayOrder.getSettleDate());
                    // ztFlowDto1.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                    // ztFlowDto1.setSplitType(DictCodeList.拆分标识_已拆分.getValue());
                    // result.add(ztFlowDto1);
                    return result;
                }
                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and 金融产品交易Entity.交易方向 in （“赎回确认”）
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“赎回确认”,“除权登记”）and 收益类型(共通逻辑获取) = “净值型”
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and 金融产品交易Entity.交易方向 in （“除权登记”）and 资产二级类型(共通逻辑获取)=“非货币基金”
                 *
                 */
                else if ((DictCodeList.交易方向_赎回确认.getValue().equals(deal.getPs())
                          && DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2()))
                         || (DictCodeList.交易方向_赎回确认.getValue().equals(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2()))
						 // add by etliu 20210421 权益类资产相关代码合并 start
                         || (DictCodeList.交易方向_赎回确认.getValue().equals(deal.getPs())
                             && DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2()))
						 // add by etliu 20210421 权益类资产相关代码合并 end
                         || (DictCodeList.交易方向_除权登记.getValue().equals(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                             && DictCodeList.收益类型_净值型.getValue().equals(profitType))
                         || (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
                             /* && DictCodeList.基金类资产类型_非货币基金.getValue().equals(assetType2) */
                             && DictCodeList.交易方向_除权登记.getValue().equals(deal.getPs()))
                         || (DictCodeList.交易方向_除权登记.getValue().equals(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                             && DictCodeList.收益类型_货币型.getValue().equals(profitType))
						 // add by etliu 20210421 权益类资产相关代码合并 start
                         || (DictCodeList.交易方向_除权登记.getValue().equals(deal.getPs())
                             && DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2())
						 // add by etliu 20210421 权益类资产相关代码合并 end
							 )) {

                    PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                    // 输入参数.组合代码
                    ztFlowDto.setPortfolioId(portfolioId);
                    // 划款指令Entity.本方账户
                    ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                    // 输入参数.持仓类型
                    ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // 划款指令Entity.币种
                    ztFlowDto.setCcy(stlPayOrder.getCcy());
                    // 划款指令Entity.指令编号
                    ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                    // 金融产品交易Entity.金融产品代码
                    ztFlowDto.setChlFinprodId(chlId);
                    ztFlowDto.setCreateUser(deal.getCreateUser());
                    ztFlowDto.setCreateDept(deal.getCreateDept());
                    ztFlowDto.setUpdateUser(deal.getCreateUser());
                    // ztFlowDto.setAcctId(stlPayOrder.getCounterAcctNo());
                    ztFlowDto.setCdate(deal.getTradeDate());
                    ztFlowDto.setAmount(stlPayOrder.getSettleAmt());
                    ztFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(ztFlowDto);

                    return result;
                }
                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“红利再投”）and 收益类型(共通逻辑获取) = “净值型” 输入金融产品交易Entity.金融产品类型（投管分类）=
                 * 数据字典缓存.金融产品类型（投管分类).基金 and 金融产品交易Entity.交易方向 in （“红利再投”）and 资产二级类型(共通逻辑获取)=“非货币基金”
                 */
                else if ((DictCodeList.交易方向_红利再投.getValue().equals(deal.getPs())
                          && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2()))
                         || (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
                             // 红利再投货币基金不登录在途流水 modify by renping 20190909 start
                             && DictCodeList.基金类资产类型_非货币基金.getValue().equals(assetType2)
                             // 红利再投货币基金不登录在途流水 modify by renping 20190909 end
                             && DictCodeList.交易方向_红利再投.getValue().equals(deal.getPs()))) {

                    PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                    // 输入参数.组合代码
                    ztFlowDto.setPortfolioId(portfolioId);
                    // 划款指令Entity.本方账户
                    ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                    // 输入参数.持仓类型
                    ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                    // 划款指令Entity.币种
                    ztFlowDto.setCcy(stlPayOrder.getCcy());
                    // 划款指令Entity.指令编号
                    ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                    // 金融产品交易Entity.金融产品代码
                    ztFlowDto.setChlFinprodId(chlId);
                    ztFlowDto.setCreateUser(deal.getCreateUser());
                    ztFlowDto.setCreateDept(deal.getCreateDept());
                    ztFlowDto.setUpdateUser(deal.getCreateUser());
                    ztFlowDto.setCdate(deal.getTradeDate());
                    ztFlowDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                    ztFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(ztFlowDto);

                    return result;
                }

                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and 金融产品交易Entity.交易方向 in （“赎回到账”）
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).计划类 and 金融产品交易Entity.交易方向 in （“赎回到账”,“现金分红”）and 收益类型(共通逻辑获取) = “净值型”
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).基金 and 金融产品交易Entity.交易方向 in （“现金分红”）and 资产二级类型(共通逻辑获取)=“非货币基金”
                 *
                 */
                else if ((赎回到账_现金分红.contains(deal.getPs())
                          && DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2()))
                         || (赎回到账_现金分红.contains(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                             && DictCodeList.收益类型_净值型.getValue().equals(profitType))
						 // add by etliu 20210421 权益类资产相关代码合并 start
                         || (赎回到账_现金分红.contains(deal.getPs())
                             && DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2()))
						 // add by etliu 20210421 权益类资产相关代码合并 end
                         || (赎回到账_现金分红.contains(deal.getPs())
                             && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                             && DictCodeList.收益类型_货币型.getValue().equals(profitType))) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);

                    // GNS-949:只有流程中数据才走，结算下行无流程中数据,不走 modify by fuyongsheng 20220227 start
                    if(!inputDto.isNotWorkflow()){
                        PtlCashPositionFlowDto ztFlowDto1 = new PtlCashPositionFlowDto();
                        // 输入参数.组合代码
                        ztFlowDto1.setPortfolioId(portfolioId);
                        // 划款指令Entity.本方账户
                        ztFlowDto1.setAcctId(stlPayOrder.getMyAcctNo());
                        // 输入参数.持仓类型
                        ztFlowDto1.setHodingType(DictCodeList.持仓类型_在途.getValue());
                        // 划款指令Entity.币种
                        ztFlowDto1.setCcy(stlPayOrder.getCcy());
                        // 划款指令Entity.指令编号
                        ztFlowDto1.setOrderId(stlPayOrder.getOrderId());
                        // 金融产品交易Entity.金融产品代码
                        ztFlowDto1.setChlFinprodId(chlId);
                        ztFlowDto1.setCreateUser(deal.getCreateUser());
                        ztFlowDto1.setCreateDept(deal.getCreateDept());
                        ztFlowDto1.setUpdateUser(deal.getCreateUser());
                        // ztFlowDto1.setAcctId(stlPayOrder.getCounterAcctNo());
                        ztFlowDto1.setCdate(stlPayOrder.getSettleDate());
                        ztFlowDto1.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                        ztFlowDto1.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                        result.add(ztFlowDto1);
                    }
                    // GNS-949:只有流程中数据才走，结算下行无流程中数据,不走 modify by fuyongsheng 20220227 end
                    return result;
                }
                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“行权”）
                 */
                else if ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                          && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                          // 一体化对应etliu start
                          // && DictCodeList.交易方向_行权.getValue().equals(deal.getPs()))) {
                          && (DictCodeList.交易方向_行权_回售.getValue().equals(deal.getPs())
                              || DictCodeList.交易方向_行权_赎回.getValue().equals(deal.getPs())))) {
                    // 一体化对应etliu end
                	/// modify by etliu 20220228 start
                    // 广农商二阶段 GNS-975:【广农商场内清算】场内债券存续期-行权赎回交易生成时，处理相关头寸有误
//                    resultDto.setCdate(deal.getTradeDate());
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    // modify by etliu 20220228 end
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }
                // GNS-879:配债组合现金流水未登录 modify by fuyongsheng 20220220 start
                else if ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                        && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                        && DictCodeListExt3.交易方向_配债.getValue().equals(deal.getPs()))) {
                    resultDto.setCdate(deal.getTradeDate());
                    resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }
                // GNS-879:配债组合现金流水未登录 modify by fuyongsheng 20220220 end

                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).逆回购 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“到期”）
                 *
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).正回购 and 金融产品交易Entity.交易场所 ！= 数据字典缓存.交易场所.银行间 and 金融产品交易Entity.交易方向 in （“买入”）and
                 * 资产二级类型(共通逻辑获取)=“质押式协议”
                 */
                else if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2())
                          && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                          && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)
                          && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))
                /*
                 * 跟上面的判断重复 || (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(deal.getFinprodType2()) &&
                 * !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()) && DictCodeList.交易方向_买入.getValue().equals(deal.getPs()))
                 */
                ) {
                    resultDto.setCdate(deal.getTradeDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }
                else if ((DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(deal.getFinprodType2())
                          && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                          && DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))) {
                    resultDto.setCdate(deal.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    result.add(resultDto);
                    return result;
                }
                else if ((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2())
                          && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                          && DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))) {
                    if (StringUtil.isNotEmpty(inputDto.getIeFlag())
                        && inputDto.getIeFlag().equals(DictCodeListExt1.收支方向_支出.getValue())) {
                        return result;// 到期续做特殊场景，逆回购支出在流程中已出流水，实际不在出流水
                    }
                    else {
                        resultDto.setCdate(deal.getSettleDate());
                        resultDto.setAmount(stlPayOrder.getSettleAmt());
                        resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                        /**** 回购释放头寸处理*****chenlei1*add***start **/
                        if ((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2()))
                            && (DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))
                            && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)) {
                            Map<String, Object> rsmap = new HashMap<String, Object>();
                            rsmap.put("tradeId", deal.getFinprodId());
                            rsmap.put("finprodType2", deal.getFinprodType2());
                            Map<String, Object> statusMap = baseAssetCashPositionCommMapper.selectTradeReleaseStatus(rsmap);
                            String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
                            String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");
                            if(deal.getTradeId().endsWith("R")){//是否撤销交易
                                if (DictCodeList.是否_否.getValue().equals(isManualRelease)) {//IF 是否手工释放 = 否
                                	 result.add(resultDto);
                                }else {
                                	return result;
                                }
                            }else{//到期确认交易
                                if (DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)) {
                                    return result;
                                }else {//IF  未释放
                                    result.add(resultDto);
                                }
                            }
                        }
                        else {
                            result.add(resultDto);
                        }
                        /**** 回购释放头寸处理*****chenlei1*add***end **/
                        return result;
                    }
                }
                else if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2())
                         && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                         && DictCodeList.交易方向_到期.getValue().equals(deal.getPs()) && StringUtil.isNotEmpty(inputDto.getIeFlag())
                         && inputDto.getIeFlag().equals(DictCodeListExt1.收支方向_收入.getValue())) {// 到期续做特殊场景
                    resultDto.setCdate(deal.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }

                // 输入金融产品交易Entity.交易方向 in （“产品认购”，“产品申购”，“活期存款结息”，“自定义现金流入”）
                else if (产品认购_申购.contains(deal.getPs())
                         || (DictCodeList.共通业务类型_自定义现金流.getValue().equals(deal.getBusiType())
                             && DictCodeList.交易方向_收取.getValue().equals(deal.getPs()))
                         || DictCodeList.共通业务类型_活期结息.getValue().equals(deal.getBusiType())) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    if ((DictCodeList.共通业务类型_自定义现金流.getValue().equals(deal.getBusiType())
                         && DictCodeList.交易方向_收取.getValue().equals(deal.getPs()))) {
                        resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    }
                    result.add(resultDto);
                    return result;
                }

                // 增加基金类资产的持仓处理 qiangchengxi 2019年2月26日 start
                else if (((DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                           && (DictCodeList.收益类型_货币型.getValue().equals(profitType)
                               || DictCodeList.收益类型_净值型.getValue().equals(profitType)))
                          || DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
						  // add by etliu 20210421 权益类资产相关代码合并 start
                          || DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(deal.getFinprodType2()))
						  // add by etliu 20210421 权益类资产相关代码合并 end
                         && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }
                // 增加基金类资产的持仓处理 qiangchengxi 2019年2月26日 end
                // 增加债券借贷到期现金处理 裴家俊 2019年7月6日 start---
                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).债券借贷融出 and 金融产品交易Entity.交易方向 in （“到期”）
                 */
                else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue().equals(deal.getFinprodType2())
                         && DictCodeList.交易方向_到期.getValue().equals(deal.getPs())) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                    return result;
                }
                // 增加债券借贷到期现金处理 裴家俊 2019年7月6日 end---
                // add by wangpeng 20190724 start

                /*
                 * 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).股票 and 金融产品交易Entity.交易方向 in （卖出）
                 */
                else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(deal.getFinprodType2())
                         && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                }

                // add by wangpeng 20190724 end
                /*
                 * 增加 股票现金分红 现金处理 潘彬2019-7-18 start 输入金融产品交易Entity.金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管分类).股票 and 金融产品交易Entity.交易方向 in （现金分红）
                 */
                else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(deal.getFinprodType2())
                         && DictCodeList.交易方向_现金分红.getValue().equals(deal.getPs())) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);

                    // GNS-932:股票红利再投,非工作流数据无需登录在途持仓 modify by fuyongsheng 20220225 start
                    if(!inputDto.isNotWorkflow()){
                        PtlCashPositionFlowDto ztFlowDto = new PtlCashPositionFlowDto();
                        // 输入参数.组合代码
                        ztFlowDto.setPortfolioId(portfolioId);
                        // 划款指令Entity.本方账户
                        ztFlowDto.setAcctId(stlPayOrder.getMyAcctNo());
                        // 持仓类型-在途
                        ztFlowDto.setHodingType(DictCodeList.持仓类型_在途.getValue());
                        // 划款指令Entity.结算日期
                        ztFlowDto.setCdate(stlPayOrder.getSettleDate());
                        // 划款指令Entity.币种
                        ztFlowDto.setCcy(stlPayOrder.getCcy());
                        // 划款指令Entity.指令编号
                        ztFlowDto.setOrderId(stlPayOrder.getOrderId());
                        // 划款指令Entity.结算金额*-1
                        ztFlowDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                        ztFlowDto.setCreateUser(deal.getCreateUser());
                        ztFlowDto.setCreateDept(deal.getCreateDept());
                        ztFlowDto.setUpdateUser(deal.getCreateUser());
                        ztFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                        result.add(ztFlowDto);

                        PtlCashPositionFlowDto ztFlowDto1 = new PtlCashPositionFlowDto();
                        // 输入参数.组合代码
                        ztFlowDto1.setPortfolioId(portfolioId);
                        // 划款指令Entity.本方账户
                        ztFlowDto1.setAcctId(stlPayOrder.getMyAcctNo());
                        // 持仓类型-在途
                        ztFlowDto1.setHodingType(DictCodeList.持仓类型_在途.getValue());
                        // 划款指令Entity.结算日期
                        ztFlowDto1.setCdate(deal.getTradeDate());
                        // 划款指令Entity.币种
                        ztFlowDto1.setCcy(stlPayOrder.getCcy());
                        // 划款指令Entity.指令编号
                        ztFlowDto1.setOrderId(stlPayOrder.getOrderId());
                        ztFlowDto1.setAmount(stlPayOrder.getSettleAmt());
                        ztFlowDto1.setCreateUser(deal.getCreateUser());
                        ztFlowDto1.setCreateDept(deal.getCreateDept());
                        ztFlowDto1.setUpdateUser(deal.getCreateUser());
                        ztFlowDto1.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());

                        result.add(ztFlowDto1);
                    }
                    // GNS-932:股票红利再投,非工作流数据无需登录在途持仓 modify by fuyongsheng 20220225 end
                }
                // 增加 股票现金分红 现金处理 潘彬2019-7-18 end
                // 外汇或者其他需要的场景用 add by renping 2019年7月11日 start---
                else if (((DictCodeListExt1.金融产品类型_投管用_即期.getValue().equals(deal.getFinprodType2())
                           || DictCodeListExt1.金融产品类型_投管用_远期.getValue().equals(deal.getFinprodType2())
                           || DictCodeListExt1.金融产品类型_投管用_掉期.getValue().equals(deal.getFinprodType2()))
                          && DictCodeListExt1.收支方向_支出.getValue().equals(stlPayOrder.getPs()))
                         || (DictCodeListExt1.金融产品类型_投管用_利率互换.getValue().equals(deal.getFinprodType2())
                             && DictCodeListExt1.收支方向_支出.getValue().equals(stlPayOrder.getPs()))) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                }
                else if (((DictCodeListExt1.金融产品类型_投管用_即期.getValue().equals(deal.getFinprodType2())
                           || DictCodeListExt1.金融产品类型_投管用_远期.getValue().equals(deal.getFinprodType2())
                           || DictCodeListExt1.金融产品类型_投管用_掉期.getValue().equals(deal.getFinprodType2()))
                          && DictCodeListExt1.收支方向_收入.getValue().equals(stlPayOrder.getPs()))
                         || (DictCodeListExt1.金融产品类型_投管用_利率互换.getValue().equals(deal.getFinprodType2())
                             && DictCodeListExt1.收支方向_收入.getValue().equals(stlPayOrder.getPs()))) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                }
                // 外汇或者其他需要的场景用 add by renping 2019年7月11日 end---
                // 增加债券债转股 dingzhipeng 2019/11/22 开始
                else if (StringUtils.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                         && StringUtils.equals(deal.getPs(), DictCodeListExt3.交易方向_债转股.getValue())) {
                    resultDto.setCdate(stlPayOrder.getSettleDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
                    result.add(resultDto);
                }
                // 增加债券债转股 dingzhipeng 2019/11/22 结束
                // 活期存放结息 交易方向为收息到账 结息方式为结转为现金结息 start
                else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                         && DictCodeListExt3.存款类资产类型_活期存放.getValue().equals(assetType2)
                         && DictCodeList.交易方向_收息到账.getValue().equals(deal.getPs())) {
                    resultDto.setCdate(deal.getTradeDate());
                    // modify by wj 20200515 TISHKBS-384 活期存放中间结息，结息方式为现金结息，PTL_CASH_POSITION_FLOW存值是0
                    //resultDto.setAmount(deal.getShareAmt());
                    resultDto.setAmount(deal.getTradeAmt());
                    result.add(resultDto);
                    return result;
                }
                //活期存放结息 交易方向为红利再投 结息方式为结转为本金  end

                //活期存放结息 交易方向为收息到账 结息方式为结转为现金结息,撤销交易
                else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                        && DictCodeListExt3.存款类资产类型_活期存放.getValue().equals(assetType2)
                        && DictCodeList.交易方向_中间付息.getValue().equals(deal.getPs())) {
                    resultDto.setCdate(deal.getTradeDate());
                    resultDto.setAmount(deal.getTradeAmt());
                    result.add(resultDto);
                    return result;
                }

                // 增加期权存续期 liutf 2020/01/08开始
                else if(StringUtils.equals(deal.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_期权.getValue())&&
                        StringUtils.equals(deal.getPsPayment(), DictCodeListExt3.收付方向_收取.getValue())){
                    resultDto.setCdate(deal.getPayDate());
                    resultDto.setAmount(deal.getShareAmt());
                    result.add(resultDto);
                    return result;
                }


                // 活期存放 交易方向为卖出 start
                else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                         && DictCodeListExt3.存款类资产类型_活期存放.getValue().equals(assetType2)
                         && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                    resultDto.setCdate(deal.getTradeDate());
                    resultDto.setAmount(stlPayOrder.getSettleAmt());
                    result.add(resultDto);
                    return result;
                }
                // 活期存放 交易方向为卖出 end
                // 场内-信用保护凭证 start
                else if (DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue().equals(deal.getFinprodType2())
                        && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())) {
                    resultDto.setCdate(deal.getTradeDate());
                    if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                        resultDto.setAmount(stlPayOrder.getSettleAmt());
                    }
                    if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                        resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                    }
                    result.add(resultDto);
                    return result;
                }
                // 场内-信用保护凭证 end
                // 场内-一级投标债 start
                else if (DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(deal.getFinprodType2())
                        && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())) {
                    resultDto.setCdate(deal.getTradeDate());
                    if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                        resultDto.setAmount(stlPayOrder.getSettleAmt());
                    }
                    if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                        resultDto.setAmount(ArithmeticUtil.multiply(stlPayOrder.getSettleAmt(), new BigDecimal(-1)));
                    }
                    result.add(resultDto);
                    // 前置交易编号不为空,说明是交收结果数据,需要反冲交收通知头寸
                    if(StringUtil.isNotEmpty(deal.getbTradeId())){
                        List<TrdProductDeal> trdProductDeals = baseAssetCashPositionCommMapper.selectTrdProductDealByTotleTradeId(
                                deal.getbTradeId());
                        if(CollectionUtils.isNotEmpty(trdProductDeals)){
                            List<PtlCashPositionFlowDto> bPtlCashPositionFlows = baseAssetCashPositionCommMapper.selectAllCashPositionByTotle(
                                    trdProductDeals.get(0).getTradeId());
                            for(PtlCashPositionFlowDto bPtlCashPositionFlow : bPtlCashPositionFlows){
                                bPtlCashPositionFlow.setCdate(deal.getTradeDate());
                                bPtlCashPositionFlow.setAmount(ArithmeticUtil.multiply(bPtlCashPositionFlow.getAmount(), new BigDecimal(-1)));
                                result.add(bPtlCashPositionFlow);
                            }
                        }
                    }
                    return result;
                }
                // 场内-一级投标债 end
            }
        }
        return result;
    }
}
