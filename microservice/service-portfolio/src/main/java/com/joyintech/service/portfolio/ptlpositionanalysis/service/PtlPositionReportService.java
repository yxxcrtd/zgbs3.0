
 
package com.joyintech.service.portfolio.ptlpositionanalysis.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.portfolio.ptlpositionanalysis.dao.PtlPositionAnalysisMapper;
import com.joyintech.service.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis003Dto;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 组合持仓分析
 * @author wanghb
 * @version 1.0 2019年1月25日
 * @see PtlPositionReportService
 * @since 1.0
 */
@Service
public class PtlPositionReportService extends BaseService<PtlPositionAnalysis003Dto>{

    @Autowired
    private PtlPositionAnalysisMapper ptlPositionAnalysisMapper;

    /**
     * 组合持仓分析报表查询
     * @param page 第几页
     * @param length 长度
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<PtlPositionAnalysis003Dto> secReportPage(int page, int length, Map<String, Object> pageParam) {
        if(StringUtil.isNotEmpty(pageParam.get("portfolioId"))){
            pageParam.put("portfolioIdList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("portfolioId")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("profitType"))){
            pageParam.put("profitTypeList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("profitType")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("prodManager"))){
            pageParam.put("prodManagerList", StringUtil.splitToList(null, pageParam.get("prodManager").toString()));
        }
        if(StringUtil.isNotEmpty(pageParam.get("finprodMarketId"))){
            pageParam.put("finprodMarketIdList", StringUtil.splitToList(null, pageParam.get("finprodMarketId").toString()));
        }
        // 下钻层级
        if(StringUtil.isNotEmpty(pageParam.get("deepLevel"))){
            pageParam.put("deepLevel", pageParam.get("deepLevel").toString());
        }
        //展示方式01：单组合汇总；02 多组合汇总
        if(StringUtil.isNotEmpty(pageParam.get("displayType"))){
            pageParam.put("displayType", pageParam.get("displayType").toString());
        }
        //modify by mashukai 组合持仓分析 上市流通结转持仓后如果持仓市值和应记利息都为0时不展示该条数据 ZGBSSE-238 start
        List<PtlPositionAnalysis003Dto> newResultList = new ArrayList<PtlPositionAnalysis003Dto>();
        // 多级法人改造 add by hxy 20200702
        List<PtlPositionAnalysis003Dto> resultList = ptlPositionAnalysisMapper.secReportPage(pageParam);
        if (StringUtil.isNotEmpty(pageParam.get("parentId")) && !StringUtil.equals(pageParam.get("parentId"), "root")) {
            resultList.forEach(list->{
                list.setTypeName("child");
            });
        }
        Page<PtlPositionAnalysis003Dto> pagelist = new Page<>();
        
        //过滤查询结果中金融产品类型是债券并且持仓市值和应记利息都为0的数据
        for (PtlPositionAnalysis003Dto ptlPositionAnalysis003Dto : resultList) {
            if (!(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(ptlPositionAnalysis003Dto.getPinanAssetType())
                    && ptlPositionAnalysis003Dto.getPosValue().compareTo(BigDecimal.ZERO) == 0
                    && ptlPositionAnalysis003Dto.getTdyIntincexp().compareTo(BigDecimal.ZERO) == 0)){
                newResultList.add(ptlPositionAnalysis003Dto);       
            }
        }
        pagelist.setRoot(newResultList);
        int total = newResultList.size()/length;
        if(newResultList.size()%length>0) {
            total++;
        }
        pagelist.setTotal(total);
        return pagelist;
    }
    //modify by mashukai 组合持仓分析 上市流通结转持仓后如果持仓市值和应记利息都为0时不展示该条数据  ZGBSSE-238 end
}
