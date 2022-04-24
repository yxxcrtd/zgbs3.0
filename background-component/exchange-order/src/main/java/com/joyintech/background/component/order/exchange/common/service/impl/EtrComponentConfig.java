/*
 * 文件名：EtrComponentConfig.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.background.component.order.exchange.common.service.impl;

import com.joyintech.background.component.order.exchange.business.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;

import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.service.EtrComponentCommService;
import com.joyintech.background.component.order.exchange.common.service.EtrSubComponentService;
import com.joyintech.background.component.order.exchange.database.dao.CmpComponentCommMapper;

/**
 * 实例化组件
 * 
 * @author zhangrui1
 * @version 1.0 2020年7月11日
 * @see EtrComponentConfig
 * @since 1.0
 */
@Configuration
public class EtrComponentConfig {

    @Autowired
    private CmpComponentCommMapper commonMapper;

    @Autowired
    private Validator validator;


    @Bean
    public EtrComponentCommService<EtrSecRepoBondDto> etrSecRepoBondComm(EtrSubComponentService<EtrSecRepoBondDto> etrSecRepoBondService) {
        return EtrComponentFactory.put(EtrSecRepoBondDto.class, getService(etrSecRepoBondService));
    }

    @Bean
    public EtrComponentCommService<EtrSecRepoRepoDto> etrSecRepoRepoComm(EtrSubComponentService<EtrSecRepoRepoDto> etrSecRepoRepoService) {
        return EtrComponentFactory.put(EtrSecRepoRepoDto.class, getService(etrSecRepoRepoService));
    }

    @Bean
    public EtrComponentCommService<EtrStockBiddingDto> etrStockBiddingComm(EtrSubComponentService<EtrStockBiddingDto> stockBiddingService) {
        return EtrComponentFactory.put(EtrStockBiddingDto.class, getService(stockBiddingService));
    }

    @Bean
    public EtrComponentCommService<EtrFundBiddingDto> fundBiddingComm(EtrSubComponentService<EtrFundBiddingDto> fundBiddingService) {
        return EtrComponentFactory.put(EtrFundBiddingDto.class, getService(fundBiddingService));
    }

    @Bean
    public EtrComponentCommService<EtrBlockTradeBondDto> blockTradeBondComm(EtrSubComponentService<EtrBlockTradeBondDto> blockTradeBondService) {
        return EtrComponentFactory.put(EtrBlockTradeBondDto.class,
            getService(blockTradeBondService));
    }

    @Bean
    public EtrComponentCommService<EtrBlockTradeFundDto> bolckTradeFundComm(EtrSubComponentService<EtrBlockTradeFundDto> bolckTradeFundService) {
        return EtrComponentFactory.put(EtrBlockTradeFundDto.class,
            getService(bolckTradeFundService));
    }

    @Bean
    public EtrComponentCommService<EtrBlockTradeStockDto> blockTradeStockComm(EtrSubComponentService<EtrBlockTradeStockDto> blockTradeStockService) {
        return EtrComponentFactory.put(EtrBlockTradeStockDto.class,
            getService(blockTradeStockService));
    }

    @Bean
    public EtrComponentCommService<EtrBlockTradePledgeRepoDto> blockTradePledgeRepoComm(EtrSubComponentService<EtrBlockTradePledgeRepoDto> blockTradePledgeRepoService) {
        return EtrComponentFactory.put(EtrBlockTradePledgeRepoDto.class,
            getService(blockTradePledgeRepoService));
    }

    @Bean
    public EtrComponentCommService<EtrBondBiddingDto> bondBiddingComm(EtrSubComponentService<EtrBondBiddingDto> bondBiddingService) {
        return EtrComponentFactory.put(EtrBondBiddingDto.class, getService(bondBiddingService));
    }

    @Bean
    public EtrComponentCommService<EtrBondFxdIncmPlatSHDto> bondFxdIncmPlatSHComm(EtrSubComponentService<EtrBondFxdIncmPlatSHDto> bondFxdIncmPlatSHService) {
        return EtrComponentFactory.put(EtrBondFxdIncmPlatSHDto.class,
            getService(bondFxdIncmPlatSHService));
    }

    @Bean
    public EtrComponentCommService<EtrBondRepoBiddingDto> bondRepoBiddingComm(EtrSubComponentService<EtrBondRepoBiddingDto> bondRepoBiddingService) {
        return EtrComponentFactory.put(EtrBondRepoBiddingDto.class,
            getService(bondRepoBiddingService));
    }

    @Bean
    public EtrComponentCommService<EtrBondRepurchaseDto> bondRepurchaseComm(EtrSubComponentService<EtrBondRepurchaseDto> bondRepurchaseService) {
        return EtrComponentFactory.put(EtrBondRepurchaseDto.class,
            getService(bondRepurchaseService));
    }

    @Bean
    public EtrComponentCommService<EtrBondRightDto> bondRightComm(EtrSubComponentService<EtrBondRightDto> bondRightService) {
        return EtrComponentFactory.put(EtrBondRightDto.class, getService(bondRightService));
    }

    @Bean
    public EtrComponentCommService<EtrBondToStockDto> bondToStockComm(EtrSubComponentService<EtrBondToStockDto> bondToStockService) {
        return EtrComponentFactory.put(EtrBondToStockDto.class, getService(bondToStockService));
    }

    @Bean
    public EtrComponentCommService<EtrConvBondApplyDto> convBondApplyComm(EtrSubComponentService<EtrConvBondApplyDto> convBondApplyService) {
        return EtrComponentFactory.put(EtrConvBondApplyDto.class,
            getService(convBondApplyService));
    }

    @Bean
    public EtrComponentCommService<EtrStandardBondInOutDto> standardBondInOutComm(EtrSubComponentService<EtrStandardBondInOutDto> standardBondInOutService) {
        return EtrComponentFactory.put(EtrStandardBondInOutDto.class,
            getService(standardBondInOutService));
    }

    @Bean
    public EtrComponentCommService<EtrStockNewDto> stockNewComm(EtrSubComponentService<EtrStockNewDto> stockNewService) {
        return EtrComponentFactory.put(EtrStockNewDto.class, getService(stockNewService));
    }

    @Bean
    public EtrComponentCommService<EtrStockRightDto> stockRightComm(EtrSubComponentService<EtrStockRightDto> stockRightService) {
        return EtrComponentFactory.put(EtrStockRightDto.class, getService(stockRightService));
    }
    @Bean
    public EtrComponentCommService<AgreRepFristIssueDto>agreRepFristIssueComm(EtrSubComponentService<AgreRepFristIssueDto> agreRepFristIssueService) {
        return EtrComponentFactory.put(AgreRepFristIssueDto.class, getService(agreRepFristIssueService));
    }
    @Bean
    public EtrComponentCommService<AgreRepAdvBuyDto> agreRepAdvBuyDtoComm(EtrSubComponentService<AgreRepAdvBuyDto> agreRepAdvBuyService) {
        return EtrComponentFactory.put(AgreRepAdvBuyDto.class, getService(agreRepAdvBuyService));
    }
    @Bean
    public EtrComponentCommService<AgreRepExpireDto> agreRepExpireComm(EtrSubComponentService<AgreRepExpireDto> agreRepExpireService) {
        return EtrComponentFactory.put(AgreRepExpireDto.class, getService(agreRepExpireService));
    }
    @Bean
    public EtrComponentCommService<AgreRepExpireUnBuyDto> agreRepExpireUnBuyComm(EtrSubComponentService<AgreRepExpireUnBuyDto> agreRepExpireUnBuyService) {
        return EtrComponentFactory.put(AgreRepExpireUnBuyDto.class, getService(agreRepExpireUnBuyService));
    }
    @Bean
    public EtrComponentCommService<AgreRepHwayChgSecDto> agreRepHwayChgSecComm(EtrSubComponentService<AgreRepHwayChgSecDto> agreRepHwayChgSecService) {
        return EtrComponentFactory.put(AgreRepHwayChgSecDto.class, getService(agreRepHwayChgSecService));
    }
    @Bean
    public EtrComponentCommService<AgreRepOpenSecDto> agreRepOpenSecComm(EtrSubComponentService<AgreRepOpenSecDto> agreRepOpenSecService) {
        return EtrComponentFactory.put(AgreRepOpenSecDto.class, getService(agreRepOpenSecService));
    }
    @Bean
    public EtrComponentCommService<EtrFutureBiddingDto> futureBiddingComm(EtrSubComponentService<EtrFutureBiddingDto> etrFutureBiddingService) {
        return EtrComponentFactory.put(EtrFutureBiddingDto.class, getService(etrFutureBiddingService));
    }

    private <V extends EtrBaseDto> EtrComponentCommService<V> getService(EtrSubComponentService<V> subService) {
        EtrComponentCommServiceImpl<V> service = new EtrComponentCommServiceImpl<>();
        service.setSubComponentService(subService);
        service.setCommonMapper(commonMapper);
        service.setValidator(validator);
        return service;
    }
}
