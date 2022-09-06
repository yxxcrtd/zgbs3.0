package com.joyintech.tams.business.common.service;

import com.codingapi.txlcn.tc.core.TxTransactionInfo;
import com.codingapi.txlcn.tc.support.listener.TransactionListener;
import com.joyintech.tams.position.servive.PositionRequestHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 头寸引擎回退报文
 *
 * @author jiangpeng
 * @date 2022年09月06日 15:45
 */
@Service("positionRollService")
public class PositionRollService implements TransactionListener {

    /**
     * 头寸引擎
     */
    @Autowired
    private PositionRequestHandle positionRequestHandle;

    @Override
    public void onTransactionBegin(TxTransactionInfo txTransactionInfo) {

    }

    @Override
    public void onTransactionError(TxTransactionInfo txTransactionInfo) {
        // 头寸引擎发送回滚报文
        positionRequestHandle.threadRollAll();
    }

    @Override
    public void onTransactionClean(String groupId, String unitId, int transactionState) {

    }
}
