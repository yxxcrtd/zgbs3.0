package com.joyintech.service.valuation.basicdata.asset.issue.service;


import com.google.common.collect.Lists;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.valuation.basicdata.asset.issue.dao.FinProductIssueMapper;
import com.joyintech.service.valuation.basicdata.asset.issue.dto.FinProductIssueDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 债券标的信息
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinProductIssueService
 * @since 1.0
 */
@Service(value = "finProductIssueService")
public class FinProductIssueService extends BaseService<FinProductIssueDto> {

    @Autowired
    private FinProductIssueMapper finProductIssueMapper;

    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 分页
     *
     * @param page
     * @param length
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<FinProductIssueDto> selectForPage(int page, int length, Map<String, Object> pageParam)
            throws Exception {
        List<String> tradeMarketList = Lists.newArrayList();
        tradeMarketList.add(DictCodeListExt1.交易场所_银行间.getValue());
        tradeMarketList.add(DictCodeListExt1.交易场所_商业银行柜台.getValue());
        //平台日
        pageParam.put("iPlatformDate", DateUtil.format(iPlatformDate.getSysDate()));
        //金融产品类型
        pageParam.put("finProdType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        //交易场所
        pageParam.put("tradeMarketList", tradeMarketList);
        Page<FinProductIssueDto> pageDto = super.queryForPage(page, length, () -> finProductIssueMapper.queryForPage(pageParam));
        return pageDto;
    }
}
