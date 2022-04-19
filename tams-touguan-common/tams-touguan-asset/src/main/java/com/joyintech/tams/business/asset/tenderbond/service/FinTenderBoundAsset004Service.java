package com.joyintech.tams.business.asset.tenderbond.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.business.asset.tenderbond.dao.FinTenderBondAssetMapper;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBoundAsset004Dto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBoundAsset004PayHSrcSecSchedulDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 债券市场服务类
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinTenderBondAsset003Service
 * @since 1.0
 */
@Service
public class FinTenderBoundAsset004Service extends BaseService {

    @Autowired
    private FinTenderBondAssetMapper finTenderBoundAssetMapper;

    public FinTenderBoundAsset004Dto getBoundMarketInfo(Map<String, Object> params) {
        FinTenderBoundAsset004Dto boundMarketInfo = finTenderBoundAssetMapper.getBoundMarketInfo(params);
        //付息计划列表
        if (Objects.nonNull(boundMarketInfo)) {
            String secId = boundMarketInfo.getSecId();
            Map<String, Object> param = new HashMap<>();
            param.put("secid", secId);
            List<FinTenderBoundAsset004PayHSrcSecSchedulDto> finTenderBoundAsset004PayHSrcSecSchedulDtos = finTenderBoundAssetMapper.selectWindBondSchedule(param);
            boundMarketInfo.setTenderBoundAsset004PayHSrcSecSchedulDtos(finTenderBoundAsset004PayHSrcSecSchedulDtos);
        }
        return boundMarketInfo;
    }

}
