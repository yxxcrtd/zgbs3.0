package com.joyintech.tams.exchange.service.trade.check.business;

import com.joyintech.tams.exchange.common.constant.joyin.DictCode;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt1;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.CheckBean;
import com.joyintech.tams.exchange.service.trade.check.business.ICheck;
import com.joyintech.tams.exchange.service.trade.management.service.CommonManagement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * @author lifangpeng
 * @date 2022/6/17 9:32
 */
public abstract class AbstractCheck implements ICheck {
    @Autowired
    private CommonManagement commonManagement;


    @Override
    public void check(CheckBean chainBean) {
        //公共信息检查
        doCommonCheck(chainBean);

        //具体业务信息检查
        doBusinessCheck(chainBean);
    }

    protected void doBusinessCheck(CheckBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        if (CollectionUtils.isEmpty(chainBean.getDetailList())) {
            checkRsp.put("instructionDetail", "指令组合明细不能为空");
        }
    }


    private void doCommonCheck(CheckBean chainBean) {
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
            checkRsp.put("finprodMarketId", "证券代码不能为空");
        }
        if (StringUtils.isBlank(instruction.getTradeMarket())) {
            checkRsp.put("tradeMarket", "交易场所不能为空");
        } else if (null == DictCodeListExt1.from(DictCode.交易场所, instruction.getTradeMarket())) {
            checkRsp.put("tradeMarket", "交易场所取值不合法:" + instruction.getTradeMarket());
        }
        if (StringUtils.isBlank(instruction.getPs())) {
            checkRsp.put("ps", "交易方向不能为空");
        } else if (null == DictCodeListExt2.from(DictCode.交易方向, instruction.getPs())) {
            checkRsp.put("ps", "交易方向取值不合法:" + instruction.getPs());
        }
        if (Objects.isNull(instruction.getTradeDate())) {
            checkRsp.put("tradeDate", "委托日期不能为空");
        }
        if (Objects.isNull(instruction.getVdate())) {
            checkRsp.put("vdate", "委托开始日不能为空");
        } else if (instruction.getVdate().compareTo(commonManagement.getPlatDate()) < 0) {
            //交易日期不得早于平台日
            checkRsp.put("vdate", "委托开始日不得早于平台日");
        }
        if (Objects.isNull(instruction.getMdate())) {
            checkRsp.put("mdate", "委托结束日不能为空");
        }
        if (StringUtils.isBlank(instruction.getEntrustStatus())) {
            checkRsp.put("entrustStatus", "委托状态不能为空");
        }
        if (StringUtils.isBlank(instruction.getPriceMode())) {
            checkRsp.put("priceMode", "限价模式不能为空");
        } else if (null == DictCodeListExt2.from(DictCode.价格模式, instruction.getPriceMode())) {
            checkRsp.put("priceMode", "限价模式取值不合法:" + instruction.getPriceMode());
        }
        if (Objects.isNull(instruction.getValueDate())) {
            checkRsp.put("valueDate", "起息日不能为空");
        }
        if (Objects.isNull(instruction.getValueDate2())) {
            checkRsp.put("valueDate2", "起息日2不能为空");
        }
        if (Objects.isNull(instruction.getMaturityDate())) {
            checkRsp.put("maturityDate", "到期日不能为空");
        }
        if (Objects.isNull(instruction.getMaturityDate2())) {
            checkRsp.put("maturityDate2", "到期日2不能为空");
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
        if (StringUtils.isBlank(instruction.getEntrustBussType())) {
            checkRsp.put("entrustBussType", "指令业务类型不能为空");
        }
        if (StringUtils.isBlank(instruction.getEntrustClass())) {
            checkRsp.put("entrustClass", "委托指令类别不能为空");
        } else if (null == DictCodeListExt3.from(DictCode.投资指令类型, instruction.getEntrustClass())) {
            checkRsp.put("entrustClass", "委托指令类别取值不合法:" + instruction.getEntrustClass());
        }
        if (StringUtils.isBlank(instruction.getBusiTypeTg())) {
            checkRsp.put("busiTypeTg", "投管业务类型不能为空");
        } else if (null == DictCodeListExt3.from(DictCode.投管业务类型, instruction.getBusiTypeTg())) {
            checkRsp.put("busiTypeTg", "投管业务类型取值不合法:" + instruction.getBusiTypeTg());
        }
        if (StringUtils.isBlank(instruction.getTargetType())) {
            checkRsp.put("targetType", "目标类型不能为空");
        } else if (null == DictCodeListExt3.from(DictCode.目标类型, instruction.getTargetType())) {
            checkRsp.put("targetType", "目标类型取值不合法:" + instruction.getTargetType());
        }

        if (DictCodeListExt3.目标类型_数量.getValue().equals(instruction.getTargetType())) {
            if (instruction.getEntrustNum() == 0) {
                checkRsp.put("entrustNum", "数量单指令数量不能为0");
            }
        } else if (DictCodeListExt3.目标类型_金额.getValue().equals(instruction.getTargetType())) {
            if (null == instruction.getEntrustAmt() || instruction.getEntrustAmt().compareTo(BigDecimal.ZERO) <= 0) {
                checkRsp.put("entrustAmt", "金额单指令金额不能为空或0");
            }
        }

        if (DictCodeListExt2.价格模式_指定价格.getValue().equals(instruction.getPriceMode())) {
            if (null == instruction.getEntrustPrice() || instruction.getEntrustPrice().compareTo(BigDecimal.ZERO) <= 0) {
                checkRsp.put("entrustPrice", "指定价格指令价格不能为空或0");
            }
        } else if (DictCodeListExt2.价格模式_限价指令.getValue().equals(instruction.getPriceMode())) {
            if (null == instruction.getEntrustPrice() || instruction.getEntrustPrice().compareTo(BigDecimal.ZERO) <= 0) {
                checkRsp.put("entrustPrice", "限价指令价格不能为空或0");
            }
        }

    }
}
