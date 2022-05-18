package com.joyintech.service.trade.entrustmsgbymq.service;

import java.math.BigDecimal;
import java.util.Date;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.service.trade.entrustmsgbymq.dto.PortfolioTradeMessage;
import com.joyintech.tams.common.base.dao.InfContractPortfolioDetailMapper;
import com.joyintech.tams.common.base.entity.InfContractPortfolioDetail;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.rabbitmq.entity.SimpleRabbitMessage;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.entrustmsgbymq.dto.OrderFinishAmtMessage;
import com.joyintech.service.trade.entrustmsgbymq.dto.TradeMessage;
import com.joyintech.tams.common.base.dao.EtrFinishStatusMapper;
import com.joyintech.tams.common.base.dao.InfContractDetailfileMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.EtrFinishStatusKey;
import com.joyintech.tams.common.base.entity.InfContractDetailfile;
import com.rabbitmq.client.Channel;

/**
 * 
 * 委托成交消息主推接口Service
 * 〈功能详细描述〉
 * @author tanxaun
 * @version 1.0 2020年5月11日
 * @see EntrustMsgbyMQService
 * @since 1.0
 */
@Lazy(false)
@Service
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class EntrustMsgbyMQService extends BaseService<InfContractDetailfile>{
	
	private static final Logger logger = LoggerFactory.getLogger(EntrustMsgbyMQService.class);

    /**
     * 委托成交消息主推接口Mapper
     */
    @Autowired
    private InfContractDetailfileMapper mapper;

	/**
	 * 委托成交消息主推接口Mapper
	 */
	@Autowired
	private InfContractPortfolioDetailMapper infContractPortfolioDetailMapper;

    /**
     * 成交信息预清算Service
     */
    @Autowired
    private TradePreClearingService tradePreClearingService;

	/**
	 * 委托完成状况表Mapper
	 */
	@Autowired
	private EtrFinishStatusMapper etrFinishStatusMapper;

	@RabbitListener(queues = "queue.exchange.trade")
    public void processFanout(SimpleRabbitMessage context, Message message, Channel channel) throws Exception {
		String tradeMessageStr = (String) context.getMessage();
    	try {
			TradeMessage tradeMessage = JSON.parseObject(tradeMessageStr, TradeMessage.class);
			if(tradeMessage != null){
				logger.info("【成交消息接收】getTradeMsgFromMQ entrustId {}", tradeMessage.getEntrustId());
				this.insertEntrustMsg(tradeMessage);
			}else{
				logger.info("【成交消息接收】the message get from MQ is null");
			}

		}catch (Exception e){
			logger.error("成交消息接收失败-错误信息:{}" + e.getMessage());
			logger.error("成交消息接收失败-错误信息:{}" + e);
			e.printStackTrace();
			logger.error("成交消息接收失败-错误信息:{},消息内容:{}", e.getLocalizedMessage(), tradeMessageStr);
			//throw new Exception("成交消息接收失败");
		}
    }
    
    /**
     * 委托成交消息入库
     * 
     * @param tradeMessage
     * @return 
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public void insertEntrustMsg(TradeMessage tradeMessage)
        throws Exception {
    	InfContractDetailfile infContractDetailfile = new InfContractDetailfile();
        infContractDetailfile.setSeqno(IDGenerator.generate());
    	infContractDetailfile.setEntrustId(tradeMessage.getEntrustId());
    	infContractDetailfile.setOrderId(tradeMessage.getOrderId());
    	infContractDetailfile.setOrderNo(tradeMessage.getEntrustNo());
    	infContractDetailfile.setDealNo(tradeMessage.getDealNo());
    	infContractDetailfile.setPortfolioId(tradeMessage.getPortfolioId());
    	infContractDetailfile.setAcctNo(tradeMessage.getAcctNo());
    	infContractDetailfile.setBondId(tradeMessage.getSecAcctId());
    	infContractDetailfile.setFinprodId(tradeMessage.getFinprodId());
    	infContractDetailfile.setPs(tradeMessage.getPS());
    	infContractDetailfile.setTradeMarket(tradeMessage.getTradeMarket());
    	infContractDetailfile.setTradePlat(tradeMessage.getTradePlat());
    	infContractDetailfile.setFinprodMarketId(tradeMessage.getFinprodMarketId());
    	if(tradeMessage.getDealVolume() != null){
    		infContractDetailfile.setShareAmt(BigDecimal.valueOf(tradeMessage.getDealVolume()));
    	}
    	if(tradeMessage.getDealPrice() != null){
    		infContractDetailfile.setUnitCprice(BigDecimal.valueOf(tradeMessage.getDealPrice()));
    	}
    	if(tradeMessage.getFullPrice() != null){
    		infContractDetailfile.setUnitFprice(BigDecimal.valueOf(tradeMessage.getFullPrice()));
    	}
    	if(tradeMessage.getDealBalance() != null){
    		infContractDetailfile.setTradeAmt(BigDecimal.valueOf(tradeMessage.getDealBalance()));
    	}
    	infContractDetailfile.setTradeDate(DateUtil.parse(tradeMessage.getTradingDay()));
    	infContractDetailfile.setVdate(DateUtil.parse(tradeMessage.getValueDate()));
    	infContractDetailfile.setMdate(DateUtil.parse(tradeMessage.getMaturityDate()));
    	infContractDetailfile.setvTradeId(tradeMessage.getTrdMatchID());
    	infContractDetailfile.setCcy(tradeMessage.getCcy());

		// 手工交收判断 add by yufei 20201022 start
		if(StringUtil.isNotEmpty(tradeMessage.getSettleDay()) && StringUtil.isNotEmpty(tradeMessage.getBusiType())){
			infContractDetailfile.setSettleDay(DateUtil.parse(tradeMessage.getSettleDay()));
			infContractDetailfile.setBusiType(tradeMessage.getBusiType());
		}
//		// 手工交收判断 add by yufei 20201022 end
//		// 期货 交易保证金  add by lijie 20201113 start
//    	infContractDetailfile.setDealMargin(tradeMessage.getDealMargin());
//    	// 期货 交易保证金 add by lijie 20201113 end

		//折算比例
		if(StringUtil.isNotEmpty(tradeMessage.getPledgeRatio())) {
			infContractDetailfile.setScale(tradeMessage.getPledgeRatio());
		}

		// 交易对手
		if (StringUtil.isNotEmpty(tradeMessage.getCounterId())){
			infContractDetailfile.setCounterId(tradeMessage.getCounterId());
		}

    	mapper.insertSelective(infContractDetailfile);

		// 下单成交组合明细数据入库 add by fuyongsheng 20220511 start
		if(CollectionUtils.isNotEmpty(tradeMessage.getPortfolioTradeMessages())){
			for(PortfolioTradeMessage portfolioMessage : tradeMessage.getPortfolioTradeMessages()){
				InfContractPortfolioDetail detail = new InfContractPortfolioDetail();
				detail.setSeqno(infContractDetailfile.getSeqno());
				detail.setPortfolioId(portfolioMessage.getPortfolioId());
				detail.setPortfolioName(portfolioMessage.getPortfolioName());
				detail.setShareAmt(portfolioMessage.getTradeNum());
				detail.setTradeAmt(portfolioMessage.getTradeAmt());
				detail.setEntrustAmt(portfolioMessage.getEntrustAmt());
				detail.setEntrustNum(portfolioMessage.getEntrustNum());
				detail.setTargetType(portfolioMessage.getTargetType());
				detail.setCreateUser("system");// 创建用户
				detail.setCreateDept("dept");// 创建部门
				detail.setCreateTime(new Date());// 创建时间
				detail.setUpdateUser("system");// 更新用户
				detail.setUpdateTime(new Date());// 更新时间
				infContractPortfolioDetailMapper.insertSelective(detail);
			}
		}
		// 下单成交组合明细数据入库 add by fuyongsheng 20220511 end

		tradePreClearingService.tradePreClearing(infContractDetailfile);
    }


	@RabbitListener(queues = "queue.exchange.progress")
	public void updateOrderFinishAmt(SimpleRabbitMessage context, Message message, Channel channel) throws Exception {
		String tradeMessageStr = (String) context.getMessage();
		try {
			OrderFinishAmtMessage tradeMessage = JSON.parseObject(tradeMessageStr, OrderFinishAmtMessage.class);
			if (tradeMessage != null) {
				logger.info("【执行进度接收】getTradeMsgFromMQ entrustId {}", tradeMessage.getEntrustId());
				this.insertEntrustMsg(tradeMessage);
			} else {
				logger.info("【执行进度接收】the message get from MQ is null");
			}
		}catch (Exception e){
            logger.error("成交消息接收失败-错误信息:{}" + e.getMessage());
            logger.error("成交消息接收失败-错误信息:{}" + e);
            e.printStackTrace();
            logger.error("执行进度接收失败-错误信息:{},消息内容:{}", e.getLocalizedMessage(), tradeMessageStr);
            //throw new Exception("执行进度消息接收失败");
		}
	}

	/***
	 *  更新指令订单完成额度
	 * @param orderFinishAmtMessage
	 * @throws Exception
	 */
	public void insertEntrustMsg(OrderFinishAmtMessage orderFinishAmtMessage)
			throws Exception {
		EtrFinishStatusKey key = new EtrFinishStatusKey();
		key.setEntrustId(orderFinishAmtMessage.getEntrustId());
		key.setTradeId(orderFinishAmtMessage.getEntrustId());
		EtrFinishStatus etrFinishStatus1 = etrFinishStatusMapper.selectByPrimaryKey(key);
		if(etrFinishStatus1!=null){
			//add by meifaxin 20220226  防止同一指令多笔交易-交易金额小的覆盖金额大的记录 start
			BigDecimal tradedBalanceTemp = orderFinishAmtMessage.getTradedBalance() !=null?new BigDecimal(orderFinishAmtMessage.getTradedBalance().toString()): BigDecimal.ZERO;
            if(etrFinishStatus1.getEntFinishAmt()!=null&&tradedBalanceTemp.compareTo(etrFinishStatus1.getEntFinishAmt())==-1){
                 return;
			}
			//add by meifaxin 20220226  防止同一指令多笔交易-交易金额小的覆盖金额大的记录 end
			etrFinishStatusMapper.deleteByPrimaryKey(key);
		}

		etrFinishStatus1 = new EtrFinishStatus();

		etrFinishStatus1.setEntrustId(orderFinishAmtMessage.getEntrustId());
		etrFinishStatus1.setTradeId(orderFinishAmtMessage.getEntrustId());
		etrFinishStatus1.setFinishDate(new Date());

		BigDecimal tradedBalance = orderFinishAmtMessage.getTradedBalance() !=null?new BigDecimal(orderFinishAmtMessage.getTradedBalance().toString()): BigDecimal.ZERO;

		BigDecimal orderFinishAmt = orderFinishAmtMessage.getFrozenBalance() !=null?new BigDecimal(orderFinishAmtMessage.getFrozenBalance().toString()): BigDecimal.ZERO;

		etrFinishStatus1.setOrderFinishAmt(tradedBalance.add(orderFinishAmt));

//		EtrEntrust etrEntrust = this.entrustMsgbyMQMapper.queryEntrustNumAndAmt(orderFinishAmtMessage.getEntrustId());
//		if(etrEntrust!=null && StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(),etrEntrust.getPs())) {
//			//配股下单成功后，指令列表中的成交进度应该是0，因为日间没有更新头寸。
//			etrFinishStatus1.setEntFinishAmt(BigDecimal.ZERO);
//		} else {
//			etrFinishStatus1.setEntFinishAmt(tradedBalance);
//		}
		etrFinishStatus1.setEntFinishAmt(tradedBalance);
		etrFinishStatus1.setFinishAmt(etrFinishStatus1.getOrderFinishAmt());
		etrFinishStatus1.setCreateUser("exchange");
		etrFinishStatus1.setCreateTime(new Date());
		etrFinishStatus1.setUpdateUser("exchange");
		etrFinishStatus1.setUpdateTime(new Date());
		etrFinishStatusMapper.insertSelective(etrFinishStatus1);

		//更新配股指令的状态
		tradePreClearingService.updateEntrust(orderFinishAmtMessage.getEntrustId(), orderFinishAmt);

	}


	@RabbitListener(queues = "queue.exchange.manualSettleTrade")
	public void processManualSettleTrade(SimpleRabbitMessage context, Message message, Channel channel) throws Exception {
		String tradeMessageStr = (String) context.getMessage();
		try {
			TradeMessage tradeMessage = JSON.parseObject(tradeMessageStr, TradeMessage.class);
			if(tradeMessage != null){
				logger.info("【手工交收消息接收】getTradeMsgFromMQ tradeMessage {}", tradeMessageStr);
				this.insertEntrustMsg(tradeMessage);
			}else{
				logger.info("【手工交收消息接收】the message get from MQ is null");
			}

		}catch (Exception e){
			logger.error("手工交收消息接收失败-错误信息:{}" + e.getMessage());
			logger.error("手工交收消息接收失败-错误信息:{}" + e);
			e.printStackTrace();
			logger.error("手工交收消息接收失败-错误信息:{},消息内容:{}", e.getLocalizedMessage(), tradeMessageStr);
			//throw new Exception("成交消息接收失败");
		}
	}

}
