package com.joyintech.tams.exchange.service.trade.check.chain;

import com.joyintech.tams.exchange.common.constant.joyin.DictCode;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.AbstractChain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 共通检查
 *
 * @author lifangpeng
 * @date 2022/4/1 14:18
 */
@Component
public class CommonCheckChain extends AbstractChain<CheckChainBean> {

    @Override
    public void doChain(CheckChainBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        if (StringUtils.isBlank(instruction.getEntrustId())) {
            checkRsp.put("entrustId", "组合代码不能为空");
        }
        if (StringUtils.isBlank(instruction.getPortfolioId())) {
            checkRsp.put("portfolioId", "组合代码不能为空");
        }
        if (StringUtils.isBlank(instruction.getPortfolioName())) {
            checkRsp.put("portfolioName", "组合名称不能为空");
        }
        if (StringUtils.isBlank(instruction.getFinprodId())) {
            checkRsp.put("finprodId", "金融产品代码不能为空");
        }
        if (StringUtils.isBlank(instruction.getFinprodAbbr())) {
            checkRsp.put("finprodAbbr", "金融产品简称不能为空");
        }
        if (StringUtils.isBlank(instruction.getFinprodName())) {
            checkRsp.put("finprodName", "金融产品全称不能为空");
        }
        if (StringUtils.isBlank(instruction.getFinprodType2())) {
            checkRsp.put("finprodType2", "资产类型不能为空");
        }
        if (StringUtils.isBlank(instruction.getAcctNo())) {
            checkRsp.put("acctNo", "网点账号不能为空");
        }
        if (StringUtils.isBlank(instruction.getCcy())) {
            checkRsp.put("ccy", "币种不能为空");
        }
        if (StringUtils.isBlank(instruction.getBusiAcctNo())) {
            checkRsp.put("busiAcctNo", "资金账号不能为空");
        }
        if (StringUtils.isBlank(instruction.getAcctName())) {
            checkRsp.put("acctName", "资金账户名不能为空");
        }
        if (StringUtils.isBlank(instruction.getSecAcctId())) {
            checkRsp.put("secAcctId", "证券账户代码不能为空");
        }
        if (StringUtils.isBlank(instruction.getSecAcctName())) {
            checkRsp.put("secAcctName", "证券账户名称不能为空");
        }
        if (StringUtils.isBlank(instruction.getFinprodMarketId())) {
            checkRsp.put("finprodMarketId", "市场代码不能为空");
        }
        if (StringUtils.isBlank(instruction.getPs())) {
            checkRsp.put("ps", "交易方向不能为空");
        }
        if (null == DictCodeListExt2.from(DictCode.交易方向, instruction.getBusiTypeTg())) {
            checkRsp.put("ps", "交易方向取值不合法");
        }
        if (Objects.isNull(instruction.getTradeDate())) {
            checkRsp.put("tradeDate", "委托日期不能为空");
        }
        if (Objects.isNull(instruction.getVdate())) {
            checkRsp.put("vdate", "委托开始日不能为空");
        }
        if (Objects.isNull(instruction.getMdate())) {
            checkRsp.put("mdate", "委托结束日不能为空");
        }
        if (StringUtils.isBlank(instruction.getEntrustStatus())) {
            checkRsp.put("entrustStatus", "委托状态不能为空");
        }
        if (StringUtils.isBlank(instruction.getPriceMode())) {
            checkRsp.put("priceMode", "限价模式不能为空");
        }
        if (StringUtils.isBlank(instruction.getTradeId())) {
            checkRsp.put("tradeId", "交易员编号不能为空");
        }
        if (StringUtils.isBlank(instruction.getTraderName())) {
            checkRsp.put("traderName", "投资经理不能为空");
        }
        if (StringUtils.isBlank(instruction.getAssetType())) {
            checkRsp.put("assetType", "资产类型不能为空");
        }
        if (StringUtils.isBlank(instruction.getTradersInfo())) {
            checkRsp.put("tradersInfo", "指令分发交易员不能为空");
        }
        if (StringUtils.isBlank(instruction.getEntrustBussType())) {
            checkRsp.put("entrustBussType", "指令业务类型不能为空");
        }
        if (StringUtils.isBlank(instruction.getEntrustClass())) {
            checkRsp.put("entrustClass", "委托指令类别不能为空");
        }
        if (StringUtils.isBlank(instruction.getBusiTypeTg())) {
            checkRsp.put("busiTypeTg", "投管业务类型不能为空");
        }
        if (null == DictCodeListExt3.from(DictCode.投管业务类型, instruction.getBusiTypeTg())) {
            checkRsp.put("busiTypeTg", "投管业务类型取值不合法");
        }
        if (CollectionUtils.isEmpty(chainBean.getDetailList())) {
            checkRsp.put("instructionDetail", "指令组合明细不能为空");
        }
    }

}
