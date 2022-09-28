package com.joyintech.service.trade.transfer.assetstransfer.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.transfer.assetstransfer.dao.AssetsTransferMapper;
import com.joyintech.service.trade.transfer.assetstransfer.dto.AssetsTransferDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 资产转仓一览Service
 * @author liuwei
 * @version 1.0 2019年12月11日
 * @see AssetsTransferService
 * @since 1.0
 */
@Service("backAssetsTransferService")
@Transactional(rollbackFor=Exception.class)
public class AssetsTransferService extends BaseService<AssetsTransferDto> {

    /**
     * 注入资产转仓一览mapper
     */
    @Autowired
    private AssetsTransferMapper assetsTransferMapper;

    /**
     * 获取组合代码联想文本框
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String,Object> map){
        map.put("PORTFOLIO_TYPE_01", DictCodeListExt1.组合类型_投资类组合.getValue());
        return assetsTransferMapper.getPortfolioId(map);
    }

    /**
     * 获取组合代码联想文本框
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    public List<TreeNode> getPortfolioIdTree(Map<String,Object> map){
        map.put("PORTFOLIO_TYPE_01", DictCodeListExt1.组合类型_投资类组合.getValue());
        if(StringUtil.isNotEmpty(map.get("portfolioId"))){
            map.put("portfolioIdList",StringUtil.splitToList(GlobalConstant.COMMA,(String)map.get("portfolioId")));
        }
        return assetsTransferMapper.getPortfolioIdTree(map);
    }

    /**
     * 查询待转仓交易
     *
     * @param pageParam
     * @return
     * @throws Exception
     */
    public Page<AssetsTransferDto> findUnTransPage(int page, int length, Map<String, Object> pageParam){
        setParam(pageParam);
        Page<AssetsTransferDto> result = super.queryForPage(page,length,() -> assetsTransferMapper.findUnTransPage(pageParam));
        if(result != null && !StringUtil.isEmptyList(result.getRoot())){
        	List<AssetsTransferDto> newRootList = new ArrayList<AssetsTransferDto>();
            result.getRoot().forEach(assetsTransferDto -> {
                if(!StringUtil.equals(assetsTransferDto.getAssetType(),DictCodeList.平安资产类型_买断式回购.getValue())) {
                    Map<String, Object> map = toMap(assetsTransferDto, pageParam);
                    //在途的持仓
                    BigDecimal flowAmt = assetsTransferMapper.selectFlowAmt(map);
                    //处于流程中的持仓（但是未记录在在持仓流水中）
                    BigDecimal wflAmt = assetsTransferMapper.selectWflAmt(map);
                    if (StringUtil.equals(assetsTransferDto.getAssetType(), DictCodeList.平安资产类型_债券.getValue())) {
                    	//2020-10-14 判断当前持仓与流程中持仓之和，小于等于0则不在待转仓列表显示
                    	//债券在途持仓数量为交易份额，交易份额为券面总额/100,所以在这里需要查出来*100
                    	BigDecimal flowAmt1 = ArithmeticUtil.multiply(flowAmt,new BigDecimal(100));
                    	BigDecimal newAmount = ArithmeticUtil.doAdd(assetsTransferDto.getAmount(), flowAmt1, wflAmt);
                    	if(BigDecimal.ZERO.compareTo(newAmount) < 0) {
                    		//assetsTransferDto.setAmount(newAmount);
                    		newRootList.add(assetsTransferDto);
                    	}
                    } else if (StringUtil.equals(assetsTransferDto.getAssetType(), DictCodeList.平安资产类型_计划类资产_利率型.getValue()) ||
                            StringUtil.equals(assetsTransferDto.getAssetType(), DictCodeList.平安资产类型_存款.getValue()) ||
                            StringUtil.equals(assetsTransferDto.getAssetType(), DictCodeList.平安资产类型_项目类资产.getValue()) ||
                            StringUtil.equals(assetsTransferDto.getAssetType(), DictCodeList.平安资产类型_质押式回购.getValue()) ||
                            StringUtil.equals(assetsTransferDto.getAssetType(), DictCodeList.平安资产类型_拆借.getValue())) {
                    	
                     	//2020-10-14 判断当前持仓与流程中持仓之和，小于等于0则不在待转仓列表显示
                    	//modify by yujiaping 20210319 转仓交易在流程中，本金金额不应扣减 start
                    	BigDecimal newPrintAmt = ArithmeticUtil.doAdd(assetsTransferDto.getPrintAmt(), flowAmt, wflAmt);                                      			
                    	if(BigDecimal.ZERO.compareTo(newPrintAmt) < 0) {    
                    		newRootList.add(assetsTransferDto);
                      		}                   	
                    	}
                       else {
                    	//2020-10-14 判断当前持仓与流程中持仓之和，小于等于0则不在待转仓列表显示
                    	BigDecimal newPosAmt = ArithmeticUtil.doAdd(assetsTransferDto.getPosAmt(), flowAmt, wflAmt);
                    	if(BigDecimal.ZERO.compareTo(newPosAmt) < 0) {                   		
                    		newRootList.add(assetsTransferDto);
                    	}
                    }
                    //modify by yujiaping 20210319 转仓交易在流程中，本金金额不应扣减 end
                }else {
                	newRootList.add(assetsTransferDto);
                }
            });
            
            //重新设置分页数据，去除了持仓为0数据
        	result.setRoot(newRootList);
        }
        return result;
    }

    /**
     * 查询已转仓交易
     *
     * @param pageParam
     * @return
     * @throws Exception
     */
    public Page<AssetsTransferDto> findTransPage(int page, int length, Map<String, Object> pageParam){
        setParam(pageParam);
        return super.queryForPage(page,length,() -> assetsTransferMapper.findTransPage(pageParam));
    }

    /**
     * 添加查询参数
     *
     * @param pageParam
     * @return
     * @throws Exception
     */
    public void setParam(Map<String, Object> pageParam){
        if(StringUtil.isNotEmpty(pageParam.get("assetType"))){
            pageParam.put("assetTypeList",StringUtil.splitToList(GlobalConstant.COMMA,(String) pageParam.get("assetType")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("assetId"))){
            pageParam.put("assetIdList",StringUtil.splitToList(GlobalConstant.COMMA,(String) pageParam.get("assetId")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("portfolioId"))){
            pageParam.put("portfolioIdList",StringUtil.splitToList(GlobalConstant.COMMA,(String) pageParam.get("portfolioId")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("finprodId"))){
            pageParam.put("finprodIdList",StringUtil.splitToList(GlobalConstant.COMMA,(String) pageParam.get("finprodId")));
        }
        pageParam.put("YESORNO_Y",DictCodeListExt1.是否_是.getValue());
        pageParam.put("YESORNO_N",DictCodeListExt1.是否_否.getValue());
        pageParam.put("CASH_TYPE2_C02",DictCodeListExt1.现金流二级类型_利息.getValue());
        pageParam.put("PROFIT_TYPE_02",DictCodeList.收益类型_收益型.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F01",DictCodeList.平安资产类型_债券.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F02",DictCodeList.平安资产类型_基金.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F03",DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F04",DictCodeList.平安资产类型_计划类资产_净值型.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F05",DictCodeList.平安资产类型_存款.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F06",DictCodeList.平安资产类型_项目类资产.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F07",DictCodeList.平安资产类型_质押式回购.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F08",DictCodeList.平安资产类型_拆借.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F09",DictCodeList.平安资产类型_计划类资产_货币型.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F10",DictCodeList.平安资产类型_买断式回购.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F11",DictCodeListExt2.平安资产类型_股票.getValue());
        pageParam.put("PINAN_ASSET_TYPE_F12",DictCodeListExt2.平安资产类型_债券借贷.getValue());
        pageParam.put("TRADE_STATUS_01",DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("TRADE_STATUS_02",DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("TRADE_STATUS_03",DictCodeListExt1.交易流程状态_撤销.getValue());
        pageParam.put("STATUS_01",DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("STATUS_03",DictCodeListExt1.交易状态_撤销.getValue());
        pageParam.put("PORTFOLIO_TYPE_01", DictCodeListExt1.组合类型_投资类组合.getValue());
        pageParam.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
        pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
    }

    /**
     * 获取资产代码联想下拉框
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFinprodId(Map<String,Object> map){
        return assetsTransferMapper.getFinprodId(map);
    }

    /**
     * 转换成查询参数
     * @return map
     * @see
     * @since 1.0
     */
    public Map<String,Object> toMap(AssetsTransferDto assetsTransferDto,Map<String,Object> map){
        map.put("portfolioId",assetsTransferDto.getPortfolioId());
        if(StringUtil.isNotEmpty(assetsTransferDto.getChlId())){
            map.put("secAcctId",null);
        }
        else {
            map.put("secAcctId",assetsTransferDto.getSecAcctId());
        }
        map.put("finprodId",assetsTransferDto.getFinprodId());
        map.put("invAim",assetsTransferDto.getInvAim());
        map.put("ccy",assetsTransferDto.getCcy());
        map.put("chlId",assetsTransferDto.getChlId());
        return map;
    }

 /**
     * 获取业务编号联想文本框
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getTradeId(Map<String,Object> pageParam){
    	setParam(pageParam);
        return assetsTransferMapper.getTradeId(pageParam);
    }
	
    public Page<SelectOptionDto> getFinprodMarketId(Map<String, Object> map, int page, int length) {
//        苏州银行、九江银行需求
//        List<String> type8List = Arrays.asList(DictCodeList.平安资产类型_债券.getValue(),DictCodeList.平安资产类型_基金.getValue(),DictCodeList.平安资产类型_计划类资产_利率型.getValue()
//        ,DictCodeList.平安资产类型_计划类资产_净值型.getValue(),DictCodeList.平安资产类型_存款.getValue(),DictCodeList.平安资产类型_项目类资产.getValue()
//        ,DictCodeList.平安资产类型_质押式回购.getValue(),DictCodeList.平安资产类型_拆借.getValue(),DictCodeList.平安资产类型_买断式回购.getValue());
//        map.put("type8List", type8List);
//        // 多级法人改造 add by jingqiongqiong 20200615
//        if(MultilevelDataAuthorityUtil.isEnabled()) {
//            //当前用户所属机构代码
//            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
//            //右上角选择框为空时 存放本级机构及有权限的下级机构 选择时 存放所选机构
//            List<String> resourcesTreeInstCodeList =  LoginUserUtil.getTreeInstCode();
//            map.put("institutionCode",createInst);
//            map.put("resourcesTreeInstCodeList",resourcesTreeInstCodeList);
//        }
//        // 多级法人改造 add by jingqiongqiong 20200615
        return this.queryForPageForSelectReturn(page, length,()->assetsTransferMapper.getFinprodMarketId(map));
    }
}
