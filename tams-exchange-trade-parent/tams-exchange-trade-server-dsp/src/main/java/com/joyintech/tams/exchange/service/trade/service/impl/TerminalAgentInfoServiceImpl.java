package com.joyintech.tams.exchange.service.trade.service.impl;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfo;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfoExample;
import com.joyintech.tams.exchange.service.trade.dao.mapper.TerminalAgentInfoMapper;
import com.joyintech.tams.exchange.service.trade.service.TerminalAgentInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lifangpeng
 * @date 2021/10/12
 */
@Service
public class TerminalAgentInfoServiceImpl implements TerminalAgentInfoService {
    @Autowired
    private TerminalAgentInfoMapper terminalAgentInfoMapper;

    @Override
    public List<TerminalAgentInfo> selectByExample(TerminalAgentInfoExample example) {
        return terminalAgentInfoMapper.selectByExample(example);
    }

    @Override
    public List<TerminalAgentInfo> select(String agentType, Integer functionCode, String entrustProp,
                                          Integer quoteType, String market) {
        TerminalAgentInfoExample example = new TerminalAgentInfoExample();
        TerminalAgentInfoExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(1);
        if (!Strings.isNullOrEmpty(agentType)) {
            criteria.andAgentTypeEqualTo(agentType);
        }
        if (null != functionCode) {
            criteria.andFunctionCodeEqualTo(functionCode);
        }
        if (!Strings.isNullOrEmpty(entrustProp)) {
            criteria.andEntrustPropEqualTo(entrustProp);
        }
        if (null != quoteType) {
            criteria.andQuoteTypeEqualTo(quoteType);
        }
        if (!Strings.isNullOrEmpty(market)) {
            criteria.andMarketEqualTo(market);
        }

        return selectByExample(example);
    }

    @Override
    public List<TerminalAgentInfo> select(String agentType, Integer functionCode) {
        return select(agentType, functionCode, null, null, null);
    }

    @Override
    public TerminalAgentInfo selectOne(String agentType, Integer functionCode) {
        List<TerminalAgentInfo> infoList = select(agentType, functionCode, null, null, null);
        if (CollectionUtils.isEmpty(infoList)) {
            throw new RuntimeException("未找到接口配置信息，请确认");
        } else if (infoList.size() > 1) {
            throw new RuntimeException("找多条接口配置信息，请确认");
        }
        return infoList.get(0);
    }

    @Override
    public TerminalAgentInfo selectOne(String agentType, Integer functionCode, String entrustProp) {
        List<TerminalAgentInfo> infoList = select(agentType, functionCode, entrustProp, null, null);
        if (CollectionUtils.isEmpty(infoList)) {
            throw new RuntimeException("未找到接口配置信息，请确认");
        } else if (infoList.size() > 1) {
            throw new RuntimeException("找多条接口配置信息，请确认");
        }
        return infoList.get(0);
    }

    @Override
    public TerminalAgentInfo selectOne(String agentType, Integer functionCode, Integer quoteType, String market) {
        List<TerminalAgentInfo> infoList = select(agentType, functionCode, null, quoteType, market);
        if (CollectionUtils.isEmpty(infoList)) {
            throw new RuntimeException("未找到接口配置信息，请确认");
        } else if (infoList.size() > 1) {
            throw new RuntimeException("找多条接口配置信息，请确认");
        }
        return infoList.get(0);
    }

}
