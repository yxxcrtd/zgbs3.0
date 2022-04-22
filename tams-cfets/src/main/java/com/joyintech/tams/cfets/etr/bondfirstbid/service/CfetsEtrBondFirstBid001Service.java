package com.joyintech.tams.cfets.etr.bondfirstbid.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bondfirstbid.dao.CfetsEtrBondFirstBid001Mapper;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindEtrInfoRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindEtrInfoDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindIssueInfoDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindIssueInfoRequest;
import com.joyintech.tams.common.base.entity.SppiImportData;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一级投标指令
 *
 * @Author: kchen
 * @Date: 2022/4/19 14:52
 */
@Service("CfetsEtrBondFirstBid001Service")
public class CfetsEtrBondFirstBid001Service extends BaseService {

    @Autowired
    private CfetsEtrBondFirstBid001Mapper cfetsEtrBondFirstBid001Mapper;

    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;

    /**
     * 指令信息查询
     *
     * @param dto
     * @return
     */
    public Page<CfetsEtrIBondFirstBidFindEtrInfoDto> findEtrInfoPage(CfetsEtrIBondFirstBidFindEtrInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADE_MARKET_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        pageParam.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());

        List<String> statusList = dto.getEntrustStatus();
        if (!StringUtil.isEmptyList(statusList) && statusList.contains("all")) {
            dto.setEntrustStatus(null);
            pageParam.put("entrustStatus", null);
        }
        //组合
        String portfolioId = (String) pageParam.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        //资产类型
        String finprodId = (String) pageParam.get("finprodId");
        if (StringUtil.isNotEmpty(finprodId)) {
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA, finprodId));
        }
        //投资经理
        String invManagerId = (String) pageParam.get("invManagerId");
        if (StringUtil.isNotEmpty(invManagerId)) {
            pageParam.put("invManagerIdList", StringUtil.splitToList(GlobalConstant.COMMA, invManagerId));
        }
        if (StringUtil.isNotEmpty(dto.getSearch())) {
            pageParam.put("search_isNumberFlag", BaseMap.isNumeric(dto.getSearch()));
        }
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();

        if (StringUtils.isNotEmpty(userId)) {
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsEtrBondFirstBid001Mapper.findEtrInfoPage(pageParam));
        } else {
            return null;
        }

    }

    /**
     * 查询SPPI测试结果
     *
     * @param secId
     * @return
     */
    public SppiImportData queryAssetIsSppiTest(String secId) {
        if (!StringUtil.isNotEmpty(secId)) {
            return null;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("finprodId", secId);
        return cfetsetrIbBondinfoMapper.queryAssetIsSppiTest(param);
    }

    /**
     * 查询待发行信息分页数据
     *
     * @param dto
     * @return
     */
    public Page<CfetsEtrIBondFirstBidFindIssueInfoDto> findIssueInfoPage(CfetsEtrIBondFirstBidFindIssueInfoRequest dto) {
        return null;
    }

    /**
     * 查询业务模式
     *
     * @param portfolioId
     * @param secId
     * @return
     */
    public String findInvAim(String portfolioId, String secId) {
        Map<String, Object> param = new HashMap<>();
        param.put("finprodId", secId);
        param.put("portfolioId", portfolioId);
        param.put("platDate", DateUtil.platDateFromCache());
        String invAim = cfetsEtrBondFirstBid001Mapper.findInvAimInEtr(param);
        return invAim;
    }
}
