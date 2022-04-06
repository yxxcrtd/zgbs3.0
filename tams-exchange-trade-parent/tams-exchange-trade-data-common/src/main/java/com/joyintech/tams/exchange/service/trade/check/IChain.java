package com.joyintech.tams.exchange.service.trade.check;

/**
 * 责任链接口
 *
 * @author lifangpeng
 * @date 2022/4/1 14:11
 */
public interface IChain<T extends ChainBean> {

    /**
     * 下一个责任链
     *
     * @param nextChain 下一个责任链
     */
    void nextChain(AbstractChain nextChain);

    /**
     * 执行具体的责任链功能逻辑
     *
     * @param chainBean 责任链数据传输实体
     */
    void doChain(T chainBean);

    /**
     * 执行责任链
     *
     * @param chainBean 责任链数据传输实体
     */
    void execute(T chainBean);
}
