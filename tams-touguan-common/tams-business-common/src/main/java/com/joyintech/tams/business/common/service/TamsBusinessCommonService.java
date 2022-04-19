package com.joyintech.tams.business.common.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dao.TamsBusinessCommonMapper;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 投管业务共通公共方法service
 */
@Service
public class TamsBusinessCommonService {

    /**
     * TamsBusinessCommonMapper
     */
    @Autowired
    private TamsBusinessCommonMapper tamsBusinessCommonMapper;

    /**
     * 查询组合托管户资金账户
     * @param portfolioId 组合代码
     * @return 组合托管户资金账户
     */
    public String selectProdTghCapacctNo(String portfolioId) {
        List<StlCapacctInfo> resultList = tamsBusinessCommonMapper.selectProdCapacctInfoList(portfolioId, "CNY", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        if (!StringUtil.isEmptyList(resultList)) {
            return resultList.get(0).getAcctNo();
        }

        return null;
    }
}
