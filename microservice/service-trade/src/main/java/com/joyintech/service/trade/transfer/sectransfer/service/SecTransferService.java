package com.joyintech.service.trade.transfer.sectransfer.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.transfer.common.dto.BondValuationDto;
import com.joyintech.service.trade.transfer.sectransfer.dao.SecTransferMapper;
import com.joyintech.service.trade.transfer.sectransfer.dto.SecTransfer001Dto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeList.估值来源_中债登_CBC;
import static com.joyintech.tams.constant.DictCodeList.估值来源_中证登_CSD;
import static com.joyintech.tams.constant.DictCodeListExt1.交易场所_上海证券交易所;
import static com.joyintech.tams.constant.DictCodeListExt1.交易场所_深圳证券交易所;

/**
 * 债券交易拆分
 *
 * @author zcw
 * @version 1.0 2019年11月21日
 * @see SecTransferService
 * @since 1.0
 */
@Service("backSecTransferService")
@Transactional(rollbackFor = Exception.class)
public class SecTransferService extends BaseService<SecTransfer001Dto> {

    /**
     * 注入存款类交易拆分mapper
     */
    @Autowired
    private SecTransferMapper secTransferMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    @Autowired
    private FinProductMapper finProductMapper;
    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    /**
     * 插入业务表
     *
     * @return
     * @see
     * @since 1.0
     */
    public String doComplete(SecTransfer001Dto secResolute001Dto) {
        String updateUser = secResolute001Dto.getUpdateUser();
        Date updateTime = new Date();
        List<BaseTradeInfoDto> baseTradeInfoDtos = secResolute001Dto.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList();
        if (baseTradeInfoDtos != null && !baseTradeInfoDtos.isEmpty()) {
            baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
                if (baseTradeInfoDto.getTrdProductDeal() != null) {
                    baseTradeInfoDto.getTrdProductDeal().setUpdateUser(updateUser);
                    baseTradeInfoDto.getTrdProductDeal().setUpdateTime(updateTime);
                    trdProductDealMapper.insertSelective(baseTradeInfoDto.getTrdProductDeal());
                }

                if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
                    baseTradeInfoDto.getTrdProductDealAdd().setUpdateUser(updateUser);
                    baseTradeInfoDto.getTrdProductDealAdd().setUpdateTime(updateTime);
                    trdProductDealAddMapper.insertSelective(baseTradeInfoDto.getTrdProductDealAdd());
                }

                if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
                    baseTradeInfoDto.getPaTrdProductDeal().setUpdateUser(updateUser);
                    baseTradeInfoDto.getPaTrdProductDeal().setUpdateTime(updateTime);
                    paTrdProductDealMapper.insertSelective(baseTradeInfoDto.getPaTrdProductDeal());
                }

                if (baseTradeInfoDto.getStlPayOrders() != null) {
                    baseTradeInfoDto.getStlPayOrders().setUpdateUser(updateUser);
                    baseTradeInfoDto.getStlPayOrders().setUpdateTime(updateTime);
                    stlPayOrdersMapper.insertSelective(baseTradeInfoDto.getStlPayOrders());
                }
            });
        }
        return Boolean.TRUE.toString();
    }

    /**
     * 查询组合托管户
     *
     * @return 组合托管户
     * @see
     * @since 1.0
     */
    public String getAccountInfo(Map<String, Object> map) {
        map.put("ACCT_TYPE", DictCodeListExt2.资金账户类型_托管户.getValue());
        return secTransferMapper.getAccountInfo(map);
    }

    /**
     * 查询组合券商资金账户
     *
     * @return 组合券商资金账户
     * @see
     * @since 1.0
     */
    public String getCounterAcctInfo(Map<String, Object> map) {
        map.put("acctType", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        return secTransferMapper.getCounterAcctInfo(map);
    }

    /**
     * @return 组合名称
     * @see
     * @since 1.0
     */
    public String selectPortfolioName(String portfolioId) {
        return secTransferMapper.selectPortfolioName(portfolioId);
    }

    /**
     * 查询托管划款指令
     * <功能详细描述>
     *
     * @param map 查询条件
     * @see
     * @since 1.0
     */
    public StlPayOrders selectStlPayOrder(String tradeId) {
        return secTransferMapper.selectStlPayOrder(tradeId);
    }
    //////

    /**
     * 由资产代码带出各种表单元素
     *
     * @param map 资产代码
     * @return 资产类型、计息基础等
     * @see
     * @since 1.
     */
    public SecTransfer001Dto selectTradeInfo(Map<String, Object> map) {
        map.put("FEE_TYPE_B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        map.put("FEE_TYPE_B06", DictCodeListExt1.一次性费用类型_交易手续费.getValue());
        map.put("FEE_TYPE_B08", DictCodeListExt1.一次性费用类型_结算手续费.getValue());
        map.put("FEE_TYPE_B05", DictCodeListExt1.一次性费用类型_佣金.getValue());
        map.put("Y", DictCodeListExt1.是否_是.getValue());
        map.put("hodingtype", DictCodeList.持仓类型_实际.getValue());
        SecTransfer001Dto dto = secTransferMapper.selectTradeInfo(map);
        return dto;
    }

    /**
     * 获取资产代码联想框集合
     *
     * @param map 资产代码入参
     * @return 联想文本框实体类
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFinprodMarketId(Map<String, Object> map) {
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return secTransferMapper.getFinprodMarketId(map);
    }

    /**
     * 查询已拆分金额
     *
     * @return 拆分前交易编号
     * @see
     * @since 1.0
     */
    public BigDecimal getDistAmt(String tradeId) {
        return secTransferMapper.getDistAmt(tradeId);
    }

    /**
     * 获取拆分信息—组合代码
     *
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String, Object> map) {
        List<SelectOptionDto> result = new ArrayList<>();
        map.put("portfolioType", DictCodeListExt1.组合类型_投资过渡组合.getValue());
        map.put("Y", DictCodeListExt1.是否_是.getValue());
        List<SelectOptionDto> list = secTransferMapper.getPortfolioId(map);
        for (SelectOptionDto dto : list) {
            PtlCashPositionFlowDto pcpdto = new PtlCashPositionFlowDto();
            Date sysdate = DateUtil.parse((String) map.get("tradeDate"));
            pcpdto.setCdate(sysdate);
            List<String> acctTypeList = new ArrayList<>();
            acctTypeList.add(BussinessDictCodeList.资金账户类型_托管账户.getValue());
            acctTypeList.add(BussinessDictCodeList.资金账户类型_交易所资金账户.getValue());
            pcpdto.setAcctType(acctTypeList);
            pcpdto.setPortfolioId(dto.getId());
            pcpdto.setCcy((String) map.get("ccy"));
            if (basePtlCashPositionInfoService != null) {
                BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(pcpdto);
                if (cashT.compareTo(new BigDecimal(0)) <= 0) continue;
                result.add(dto);
            }
        }
        return result;
    }

    // add by xuzhihao start
    public BigDecimal findUnitCprice(Map<String, Object> param) {

//        // 查询债券登记托管机构
//        FinProduct finProduct = finProductMapper.selectByPrimaryKey((String) param.get("finprodId"));
//        if (finProduct != null) {
//            if (StringUtil.equals(DictCodeList.登记托管机构_中债_中央国债登记结算有限责任公司.getValue(),
//                    finProduct.getRegistOrg())) {
//                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
//            }
//            else if (StringUtil.equals(DictCodeList.登记托管机构_中债_上海清算所.getValue(),
//                    finProduct.getRegistOrg())) {
//                param.put("VALUATION_SOURCE", DictCodeList.估值来源_上清所_SCH.getValue());
//            }
//            else if (StringUtil.equals(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue(),
//                    finProduct.getRegistOrg())) {
//                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中证登_CSD.getValue());
//            }
//            else {
//                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
//            }
//        }
//        else {
//            param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
//        }
//
//        //获取债券是否上市流通信息
//        param.put("NO",DictCodeList.是否_否.getValue());
//        param.put("TRADE_STATUS01",DictCodeListExt1.交易状态_初始.getValue());
//        FinProduct apperFinProduct = secTransferMapper.selectApperInfo(param);
//
//        //如果是转仓日当天进行的上市流通交易，查询净值时查询发行标的的净值
//        if (apperFinProduct != null && StringUtil.isNotEmpty(apperFinProduct.getFinprodId())){
//            param.put("finprodId",apperFinProduct.getFinprodId());
//        }

        // 查询上一估值日的净价
        return secTransferMapper.getLastNetPrice(param);
    }
    // add by xuzhihao end

    public int selectData(Map<String, Object> map) {
        map.put("tradeDate", new Date((Long) map.get("tradeDate")));
        return secTransferMapper.selectData(map);
    }

    public int selectYwmsData(Map<String, Object> map) {
        map.put("tradeDate", new Date((Long) map.get("tradeDate")));
        return secTransferMapper.selectYwmsData(map);
    }

    public int selectCpriceData(Map<String, Object> map) {
        map.put("tradeDate", new Date((Long) map.get("tradeDate")));
        return secTransferMapper.selectCpriceData(map);
    }

    public BondValuationDto findValuation(Map<String, Object> param) {
        List<BondValuationDto> dtoList = secTransferMapper.selectBondValuationInfo(param);
        return CollectionUtils.isNotEmpty(dtoList) ? dtoList.get(0) : null;
    }
}
