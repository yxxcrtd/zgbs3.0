package com.joyintech.tams.exchange.service.trade.processor.account;

import com.joyintech.tams.exchange.common.bean.PositionQueryBean;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.TradeType;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.datacommon.db.service.PositionBeanService;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.transaction.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: think
 * @Description: 账户相关处理
 * @Date: Created in 11:08 2020/11/9
 */
@Component
public class AccountPositionHelper {
    private static final Logger logger = LoggerFactory.getLogger(AccountPositionHelper.class);

    @Autowired
    private PositionBeanService positionBeanService;
    @Autowired
    private TransactionHelper transactionHelper;
    @Value("${position.flag:false}")
    private boolean positionCheckFlag;

    public boolean isPositionCheckFlag() {
        return positionCheckFlag;
    }

    public void setPositionCheckFlag(boolean positionCheckFlag) {
        this.positionCheckFlag = positionCheckFlag;
    }

    public void positionCache(TradeAgent tradeAgent, String accountId) {
        try {
            if (!positionCheckFlag) return;
            List<PositionBean> positionUpdateList = new ArrayList<>();
            List<PositionBean> positionBeanList = tradeAgent.reqAccountPosition(new PositionQueryBean(accountId, null, 0, null));
            if (Objects.nonNull(positionBeanList)) {
                List<PositionBean> positionBeanDbList = positionBeanService.findAll();
                Map<String, PositionBean> positionBeanDbMap = positionBeanDbList.stream().collect(Collectors.toMap(PositionBean::getStockCode, positionBean -> positionBean));
                Set<String> positionDbSet = new HashSet<>();
                positionBeanList.forEach(positionBean -> {
                    PositionBean positionBeanDb = positionBeanDbMap.get(positionBean.getStockCode());
                    //比对持仓信息
                    if (Objects.nonNull(positionBeanDb)) {
                        if (positionBean.getEnableVolume() != positionBeanDb.getEnableVolume()) {
                            positionBeanDb.setEnableVolume(positionBean.getEnableVolume());
                            positionUpdateList.add(positionBeanDb);
                        }
                        if (positionBean.getHoldVolume() != positionBeanDb.getHoldVolume()) {
                            positionBeanDb.setHoldVolume(positionBean.getHoldVolume());
                            positionUpdateList.add(positionBeanDb);
                        }
                        positionDbSet.add(positionBeanDb.getId());
                    } else {
                        positionUpdateList.add(positionBean);
                    }
                });
                logger.info("需要更新的持仓信息， {}", positionUpdateList.size());
                positionBeanService.asyncUpdate(positionUpdateList);
                List<PositionBean> positionBeanDeleteList = positionBeanDbList.stream().filter(positionBean -> !positionDbSet.contains(positionBean.getId())).collect(Collectors.toList());
                logger.info("需要清除的持仓信息， {}", positionBeanDeleteList.size());
                if (positionBeanDeleteList.size() > 0) positionBeanService.asyncDel(positionBeanDeleteList);
            }
        } catch (Exception e) {
            logger.warn("账户持仓信息缓存失败。", e);
        }
    }

    /**
     * 持仓检查
     * @param orderInterface    订单对象
     * @param entrustVolume 委托数量
     * @param buyUnit   交易最小单位
     * @return  检查结果
     */
    public boolean checkLooseStocksByPosition(OrderInterface orderInterface, long entrustVolume, int buyUnit) {
        if (positionCheckFlag && EntrustBs.SELL.value.equals(orderInterface.getEntrustBs())
                && EntrustUtils.needPositionCheck(orderInterface.getEntrustProp(), orderInterface.getAssetType())) {
            PositionBean position = positionBeanService.getPositionBean(orderInterface.getAccountId(), orderInterface.getStockCode());
            if (Objects.isNull(position)) {
                orderInterface.setErrorCode(ErrorCode.ACCOUNT_POSITION_NOT_STOCK.value);
                orderInterface.setErrorMsg(ErrorCode.ACCOUNT_POSITION_NOT_STOCK.errorMsg);
                return false;
            }
            long positionEnable = position.getEnableVolume() - position.getSellFrozenVolume();
            if (positionEnable < entrustVolume) {
                orderInterface.setErrorCode(ErrorCode.ACCOUNT_POSITION_NOT_ENABLE.value);
                orderInterface.setErrorMsg(ErrorCode.ACCOUNT_POSITION_NOT_ENABLE.errorMsg + ", 可用持仓" + positionEnable + "; 委托数量：" + entrustVolume);
                return false;
            } else {
                if (buyUnit != 0 && positionEnable >= 0
                        && !(entrustVolume % buyUnit == 0
                        || (positionEnable - entrustVolume) % buyUnit == 0)) {
                    orderInterface.setErrorCode(ErrorCode.ENTRUST_NUM_ERROR.value);
                    orderInterface.setErrorMsg(ErrorCode.ENTRUST_NUM_ERROR.errorMsg + ", 不满足卖出最小单位, 持仓" + positionEnable + "; 委托数量：" + entrustVolume);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean frozenPositionEnable(OrderInterface orderInterface, long frozenVolume) {
        // 卖出冻结数量
        if (positionCheckFlag
                && EntrustUtils.needPositionCheck(orderInterface.getEntrustProp(), orderInterface.getAssetType())
                && EntrustBs.SELL.value.equals(orderInterface.getEntrustBs())) {
            PositionBean positionBean = positionBeanService.getPositionBean(orderInterface.getAccountId(), orderInterface.getStockCode());
            if (Objects.isNull(positionBean)) return true;
            transactionHelper.addPositionBean(positionBean);
            if (positionBean.getEnableVolume() < (positionBean.getSellFrozenVolume() + frozenVolume)) {
                orderInterface.setErrorCode(ErrorCode.ACCOUNT_POSITION_NOT_ENABLE.value);
                orderInterface.setErrorMsg(ErrorCode.ACCOUNT_POSITION_NOT_ENABLE.errorMsg);
                return false;
            }
            positionBean.setSellFrozenVolume(positionBean.getSellFrozenVolume() + frozenVolume);
            logger.info("卖出冻结账户持仓；HoldVolume {} EnableVolume {} frozenVolume {}", positionBean.getHoldVolume(), positionBean.getEnableVolume(), positionBean.getSellFrozenVolume());
            return true;
        }
        return true;
    }

    public void unFrozenPositionEnable(OrderInterface orderInterface, long frozenVolume) {
        if (positionCheckFlag && EntrustBs.SELL.value.equals(orderInterface.getEntrustBs())
                && EntrustUtils.needPositionCheck(orderInterface.getEntrustProp(), orderInterface.getAssetType())) {
            PositionBean positionBean = positionBeanService.getPositionBean(orderInterface.getAccountId(), orderInterface.getStockCode());
            if (Objects.isNull(positionBean)) return;
            transactionHelper.addPositionBean(positionBean);
            positionBean.setSellFrozenVolume(positionBean.getSellFrozenVolume() - frozenVolume);
            logger.info("卖出回退账户持仓冻结；HoldVolume {} EnableVolume {} frozenVolume {}", positionBean.getHoldVolume(), positionBean.getEnableVolume(), positionBean.getSellFrozenVolume());
        }
    }

    public void handlePositionByTrade(EntrustInterface entrustInterface, int tradeType, long tradeVolume) {
        if (positionCheckFlag
                && EntrustUtils.needPositionCheck(entrustInterface.getEntrustProp(), entrustInterface.getAssetType()) && TradeType.NORMAL.value == tradeType) {
            PositionBean positionBean = positionBeanService.getPositionBean(entrustInterface.getAccountId(), entrustInterface.getStockCode());
            if (EntrustBs.SELL.value.equals(entrustInterface.getEntrustBs())) {
                if (Objects.isNull(positionBean)) return;
                //卖出减少可用
                positionBean.setEnableVolume(positionBean.getEnableVolume() - tradeVolume);
                positionBean.setHoldVolume(positionBean.getHoldVolume() - tradeVolume);
            } else {
                // 买入增加今仓
                if (Objects.isNull(positionBean)) {
                    positionBean = new PositionBean();
                    positionBean.setStockCode(entrustInterface.getStockCode());
                    positionBean.setAccountId(entrustInterface.getAccountId());
                    positionBean.setMarket(entrustInterface.getMarket());
                    positionBean.setFinanceAccount(entrustInterface.getFinanceAccount());
                    positionBean.setStockName(entrustInterface.getStockName());
                }
                positionBean.setTodayVolume(positionBean.getTodayVolume() + tradeVolume);
                positionBean.setHoldVolume(positionBean.getHoldVolume() + positionBean.getTodayVolume());
            }
            transactionHelper.addPositionBean(positionBean);
            logger.info("成交后更新账户持仓；HoldVolume {} EnableVolume {} frozenVolume {} todayVolume {}", positionBean.getHoldVolume(), positionBean.getEnableVolume(), positionBean.getSellFrozenVolume(), positionBean.getTodayVolume());
        }
    }

    public void clearPosition() {
        if (!positionCheckFlag) return;
        logger.info("账户持仓清算开始...");
        List<PositionBean> positionBeanDeleteList = new ArrayList<>();
        List<PositionBean> positionBeanUpdateList = new ArrayList<>();
        List<PositionBean> positionBeanList = positionBeanService.findAll();
        positionBeanList.forEach(positionBean -> {
            positionBean.setEnableVolume(positionBean.getEnableVolume() + positionBean.getTodayVolume());
            positionBean.setTodayVolume(0);
            positionBean.setSellFrozenVolume(0);
            if (positionBean.getEnableVolume() == 0) {
                positionBeanDeleteList.add(positionBean);
            } else {
                positionBeanUpdateList.add(positionBean);
            }
        });
        logger.info("账户持仓清算结束，positionBeanUpdateList {} positionBeanDeleteList {}", positionBeanUpdateList.size(), positionBeanDeleteList.size());
        if (positionBeanUpdateList.size() > 0) positionBeanService.asyncUpdate(positionBeanUpdateList);
        if (positionBeanDeleteList.size() > 0) positionBeanService.asyncDel(positionBeanDeleteList);
    }
}
