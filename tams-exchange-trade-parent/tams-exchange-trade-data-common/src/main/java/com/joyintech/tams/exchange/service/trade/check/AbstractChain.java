package com.joyintech.tams.exchange.service.trade.check;

/**
 * 抽象责任链
 *
 * @author lifangpeng
 * @date 2022/4/1 14:11
 */
public abstract class AbstractChain<V extends ChainBean> implements IChain<V> {

    private AbstractChain nextChain;

    @Override
    public void nextChain(AbstractChain nextChain) {
        this.nextChain = nextChain;
    }

    @Override
    public void execute(V chainBean) {
        if (chainBean.isInterrupt()) {
            return;
        }
        this.doChain(chainBean);
        if (this.nextChain != null) {
            this.nextChain.execute(chainBean);
        }
    }
}
