/*
 * 文件名：TrdBondInvestTradeService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年6月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.bondlend.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.bondlend.dao.TrdBondLendInvestTradeMapper;
import com.joyintech.service.trade.bondlend.dto.FinCashPlanDto;
import com.joyintech.service.trade.bondlend.dto.SecInfoModelDto;
import com.joyintech.service.trade.bondlend.dto.TrdBondLendInvestTradeDto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;

/**
 * 债券借贷交易Service
 *
 * @author 裴家俊
 * @version 1.0 2019年6月11日
 * @see TrdBondLendInvestTradeService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class TrdBondLendInvestTradeService extends BaseService {

    @Autowired
    private TrdBondLendInvestTradeMapper trdBondLendInvestTradeMapper;

    @Autowired
    private IPlatformDate pPlatformDate;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * FinProductMapper
     */
    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    /**
     * 分页查询
     *
     * @param page 页编号
     * @param length 页条数
     * @param pageParam 画面参数
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<TrdBondLendInvestTradeDto> selectForPage(int page, int length,
                                                         Map<String, Object> pageParam)
    {
        pageParam.put("VALID_STATUS_01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("VALID_STATUS_02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("VALID_STATUS_03", DictCodeListExt1.交易流程状态_撤销.getValue());
        pageParam.put("YES_NO_YES", DictCodeList.是否_是.getValue());
        pageParam.put("YES_NO_NO", DictCodeList.是否_否.getValue());
        pageParam.put("FINPROD_TYPE_TG_F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        pageParam.put("FINPROD_TYPE_TG_F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        //pageParam.put("BRANCH_0", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        
        pageParam.put("psList", DictCodeList.交易方向_买入.getValue());

        return this.queryForPage(page, length,
            () -> trdBondLendInvestTradeMapper.selectDeal(pageParam));
    }

    /**
     * 查询投资委托信息
     *
     * @param entrustId 委托代码
     * @return 投资委托信息
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEntrustInfo(String entrustId) {
        if (baseEtrInvestInfoService == null) {
            return new BaseEtrInvestInfoDto();
        }
        BaseEtrInvestInfoDto resultDto = baseEtrInvestInfoService.queryActiveMandate(entrustId);
        if (resultDto != null && resultDto.getEtrEntrust() != null) {
            // 委托数量中扣除已完成额度
            if (!StringUtil.isEmptyList(resultDto.getEtrFinishStatusList())) {
                BigDecimal 已完成额度 = BigDecimal.ZERO;
                for (EtrFinishStatus etrFinishStatus : resultDto.getEtrFinishStatusList()) {
                    已完成额度 = ArithmeticUtil.doAdd(已完成额度,etrFinishStatus.getFinishAmt());
                }

                resultDto.getEtrEntrust().setEntrustAmt(
                    ArithmeticUtil.subtract(resultDto.getEtrEntrust().getEntrustAmt(), 已完成额度));
            }

            resultDto.getEtrEntrust().setPrice(ArithmeticUtil.multiply(
                resultDto.getEtrEntrust().getPrice(), BigDecimal.valueOf(100)));
            resultDto.getEtrEntrust().setEntrustAmt(ArithmeticUtil.divide(
                resultDto.getEtrEntrust().getEntrustAmt(), BigDecimal.valueOf(10000),4));
            if(resultDto.getEtrEntrust().getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())){
                resultDto.getEtrEntrust().setPs(DictCodeList.回购拆借方向_融入.getValue());
            }
            else{
                resultDto.getEtrEntrust().setPs(DictCodeList.回购拆借方向_融出.getValue());
            }
            // 查询金融产品名称
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(resultDto.getEtrEntrust().getFinprodId());
            if (finProduct != null) {
                resultDto.setFinprodName(finProduct.getFinprodName());
            }
        }
        if (resultDto != null && resultDto.getEtrEntrustAdd() != null) {
            // 清算速度
            if (resultDto.getEtrEntrustAdd().getSettleSpreed() == null
                || resultDto.getEtrEntrustAdd().getSettleSpreed() == 0L) {
                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_0.getValue());
            }
            else {
                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_1.getValue());
            }
        }

        return resultDto;
    }

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getSecManageAcctId(Map<String, Object> param) {
        return this.queryForSuggest(() -> trdBondLendInvestTradeMapper.getSecManageAcctId(param),
            null);
    }

    public Map<String, Object> getAssetAvailablePosition(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        param.put("hodingType",DictCodeListExt3.资产类型_债券借贷本方券可用持仓.getValue());
        BigDecimal position=  trdBondLendInvestTradeMapper.getAssetAvailablePosition(param);
        result.put("position", position);
        return result;
    }



    
    
    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getChannel(Map<String, Object> param) {
        return this.queryForSuggest(() -> trdBondLendInvestTradeMapper.getChannel(param),
            null);
    }
    
    
    public List<String> getChannelInfo(Map<String, Object> param) {
        return this.queryForSuggest(() -> trdBondLendInvestTradeMapper.getChannelInfo(param),
            null);
    }

    /**
     * 资产代码：下拉框
     *
     * @param params 画面参数
     * @return 资产代码：下拉框
     * @throws Exception
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Page<SelectOptionDto> getFinprodId(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("FINPROD_MARKET",DictCodeListExt1.交易场所_银行间.getValue());
        param.put("FINPROD_WIND_MARKET",DictCodeListExt1.交易场所_WIND_银行间.getValue());
        return this.queryForPage(page, length,
            () -> trdBondLendInvestTradeMapper.getFinprodId(param));

    }

    /**
     * 债券投资交易新建画面查看模式查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public TrdBondLendInvestTradeDto getTrdBondLendInvestTradeDto(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", tradeId);
        params.put("B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        params.put("Y", DictCodeList.是否_是.getValue());
        TrdBondLendInvestTradeDto resultDto = trdBondLendInvestTradeMapper.getTrdBondLendInvestTradeDto(params);
        if (resultDto != null) {
            // 清算速度
            if (resultDto.getSettleSpeed() == null || resultDto.getSettleSpeed() == 0L) {
                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_0.getValue());
            }
            else {
                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_1.getValue());
            }
            // 到期收益率
            if (resultDto.getYield() != null) {
                resultDto.setYield(ArithmeticUtil.multiply(resultDto.getYield(), BigDecimal.valueOf(100)));
            }
            // 行权收益率
            if (resultDto.getExerYield() != null) {
                resultDto.setExerYield(ArithmeticUtil.multiply(resultDto.getExerYield(), BigDecimal.valueOf(100)));
            }
        }
        return resultDto;
    }

    /**
     * 查询债券基本信息
     *
     * @param finprodId 金融产品代码
     * @return 债券基本信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public SecInfoModelDto getSecInfoModel(String finprodId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("sysDate", pPlatformDate.getSysDate());
        map.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        return trdBondLendInvestTradeMapper.getSecInfoModel(map);
    }

    /**
     * 获取债券付息计划
     *
     * @param finprodId 金融产品代码
     * @return 债券付息计划
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<FinCashPlanDto> getFinCashPlan(String finprodId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        return trdBondLendInvestTradeMapper.getFinCashPlan(map);
    }

	public List<SelectOptionDto> getPortfolioId(Map<String, Object> param) {
        return trdBondLendInvestTradeMapper.getPortfolioId(param);
	}

    /**
     * 取得证券托管户的登记托管机构
     * 
     * @param param Map<String, Object>
     * @return 证券托管户的登记托管机构
     * @see
     * @since 1.0
     */
    public String getRegistOrg(Map<String, Object> param) {
        return trdBondLendInvestTradeMapper.getRegistOrg(param);
    }

    /**
     * 查询产品类账户信息
     * @param portfolioId 组合ID
     * @return 清算账户DTO
     */
    public List<StlCapacctInfo> getProAccountInfo(String portfolioId, String ccy,String capacctType) {
        return trdBondLendInvestTradeMapper.selectProdCapacctInfoList(portfolioId, ccy,capacctType);
    }
    /**
     * 查询交易类账户信息
     * @param secManageAcctId 证券管理户ID
     * @return 清算账户DTO
     */
    public List<StlCapacctInfo> getDealAccountInfo(String secManageAcctId, String ccy) {
        return trdBondLendInvestTradeMapper.selectDealCapacctInfoList(secManageAcctId, ccy);
    }

    /**
     * 查询交易对手账户信息
     * @param secManageAcctId 证券管理户ID
     * @return 清算账户DTO
     */
    public List<StlCapacctInfo> getCounterCapacctInfo(String counterId) {
        return trdBondLendInvestTradeMapper.selectCounterCapacctInfoList(counterId, DictCodeListExt1.资金账户类型_交易对手账户.getValue());
    }
}
