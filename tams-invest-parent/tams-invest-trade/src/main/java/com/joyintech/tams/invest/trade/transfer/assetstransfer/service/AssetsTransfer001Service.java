package com.joyintech.tams.invest.trade.transfer.assetstransfer.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.trade.transfer.assetstransfer.dto.AssetsTransfer001Dto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * 资产转仓Service
 * @author liuwei
 * @version 1.0 2019年12月10日
 * @see AssetsTransfer001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AssetsTransfer001Service extends BaseService<AssetsTransfer001Dto> {

    /**
     * 获取组合代码联想文本框
     * @return 组合代码列表
     *
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String,Object> paramMap){
        return simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/AssetsTransfer/getPortfolioId" ,paramMap,
                SelectOptionDto.class);
    }

    /**
     * 获取组合结构数据
     * @return 画面元素
     * @throws Exception Exception
     * @since 1.0
     */
    public List<TreeNode> getPortfolioIdTree(String portfolioId,Date mdate)
             {
        List<TreeNode> result = new ArrayList<TreeNode>();
        List<TreeNode> treeProList = new ArrayList<TreeNode>();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("portfolioId",portfolioId);
        paramMap.put("mdate", DateUtil.format(mdate));
        paramMap.put("userId", LoginUserUtil.getUserName());
        treeProList = simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/AssetsTransfer/getPortfolioIdTree" ,paramMap,
                TreeNode.class);
        List<DataDictItem> profitList = (List<DataDictItem>) SysDataUtil.getDataDictItem(DictCode.收益类型.getValue());
        if (profitList.size() > 0) {
            for (DataDictItem map : profitList) {
                if ("02".equals(map.getItemCode())) {
                    continue;
                }
                TreeNode treeDto = new TreeNode();
                treeDto.setId(map.getItemCode());
                treeDto.setName(map.getItemValue());
                treeDto.setpId("");
                treeDto.setOpen(true);
                result.add(treeDto);
            }
        }
        result.addAll(treeProList);
        return result;
    }

    /**
     * 获取待转仓列表
     *
     * @return 组合代码列表
     * @throws Exception Exception
     * @since 1.0
     */
    public Page<AssetsTransfer001Dto> findUnTransPage(Map<String,Object> paramMap){
        return simpleRestTemplate.postForPage(
                ServiceApplication.TRADE_SERVICE_NAME + "/AssetsTransfer/findUnTransPage" ,paramMap,
                AssetsTransfer001Dto.class);
    }

    /**
     * 获取已转仓列表
     *
     * @return 组合代码列表
     * @throws Exception Exception
     * @since 1.0
     */
    public Page<AssetsTransfer001Dto> findTransPage(Map<String,Object> paramMap){
        return simpleRestTemplate.postForPage(
                ServiceApplication.TRADE_SERVICE_NAME + "/AssetsTransfer/findTransPage" ,paramMap,
                AssetsTransfer001Dto.class);
    }

    /**
     * 获取资产代码联想下拉框
     * @return 资产代码列表
     *
     * @since 1.0
     */
    public List<SelectOptionDto> getFinprodId(Map<String,Object> paramMap){
        return simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/AssetsTransfer/getFinprodId" ,paramMap,
                SelectOptionDto.class);
    }

  /**
     * 获取业务编号联想文本框
     * @return 业务编号列表
     *
     * @since 1.0
     */
    public List<SuggestDto> getTradeId(String param){
    	if(param==null||"".equals(param)){
			return new ArrayList<SuggestDto>();
		}
    	Map<String,Object> paramMap = RequestUtil.getPageParam();
    	paramMap.put("tradeId", param);
    	List<SuggestDto> list = simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/AssetsTransfer/getTradeId" ,paramMap,
                SuggestDto.class);
    	return list;
    }
    
    public Page<SelectOptionDto> getFinprodMarketId(Map<String, Object> paramMap) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/AssetsTransfer/getFinprodMarketId" , paramMap,
                SelectOptionDto.class);
    }
}
