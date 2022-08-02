/*
 * 文件名：EtrFundService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年10月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrfund.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.dao.EntrustCommonPersonSelectMapper;
import com.joyintech.service.entrust.etrboutrepo.dto.EtrBoutRepoDto;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.service.entrust.etrfund.dao.EtrFundMapper;
import com.joyintech.service.entrust.etrfund.dto.EtrFundDto;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 基金委托service
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年10月8日
 * @see EtrFundService
 * @since 1.0
 */
@Service("backEtrFundService")
public class EtrFundService extends BaseService<EtrFundDto> {
    /**
     * 自定义mappper
     */
    @Autowired
    private EtrFundMapper etrFundMapper;
    
    /**
     * 委托人员信息
     */
    @Autowired
    private EntrustCommonPersonSelectMapper entrustCommonPersonSelectMapper;
    
    /**
     * 平台日期
     */
    @Autowired(required=false)
    private IPlatformDate platformDateImpl;
    
    /**
     * 附件信息共通Service
     */
    @Autowired(required=false)
    private MstFileCommonService mstFileCommonService;
    
    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;
    
    /**
     * 组合可用持仓和实际持仓查询更新组件接口
     */
    @Autowired(required=false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;
    
    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired(required=false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    
    /**
     * 委托自动分发
     */
    @Autowired(required=false)
    private BusiAutoEtrDispService busiAutoEtrDispService;
    /**
     * 委托指令共同
     */
    @Autowired(required=false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired(required = false)
    private EtrCommandDispatcherService etrCommandDispatcherService;

    /**
     * 使用给定的检索分页数据。返回类型是Service的泛型。
     * 
     * @param length int
     * @param page int
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<EtrFundDto> selectForPage(int page, int length,
                                                  Map<String, Object> pageParam)
    {
        String assetType1 = (String)pageParam.get("assetType1");
        if(StringUtil.isNotEmpty(assetType1)){
            pageParam.put("assetType1List", StringUtil.splitToList(GlobalConstant.COMMA,assetType1));
        }
    	// 新增资产二、三、四级分类 王贵奇 2019/7/29 开始
        String assetType10 = (String)pageParam.get("assetType10");
        if(StringUtil.isNotEmpty(assetType10)){
            pageParam.put("assetType10List", StringUtil.splitToList(GlobalConstant.COMMA,assetType10));
        } 
        String assetType11 = (String)pageParam.get("assetType11");
        if(StringUtil.isNotEmpty(assetType11)){
            pageParam.put("assetType11List", StringUtil.splitToList(GlobalConstant.COMMA,assetType11));
        }
    	// 新增资产二、三、四级分类 王贵奇 2019/7/29 结束
        String ps = (String)pageParam.get("ps");
        if(StringUtil.isNotEmpty(ps)){
            pageParam.put("psList", StringUtil.splitToList(GlobalConstant.COMMA,ps));
        }
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
        
        pageParam.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        pageParam.put("endDate", DateUtil.parse((String)pageParam.get("endDate")));
        pageParam.put("startDate", DateUtil.parse((String)pageParam.get("startDate")));


        // add by etliu 20210501 银行间场内场外拆分 start
        pageParam.put("defaultTradeMarketList", PageMenuSplitUtil.getTradeMarketList((String) pageParam.get("pageId")));
        // add by etliu 20210501 银行间场内场外拆分 end
        pageParam.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());
        return super.queryForPage(page, length, () -> etrFundMapper.select(pageParam));
    }
    
    /**
     * 
     * 组合代码联想文本框 
     * <功能详细描述>
     * @param param Map<String, Object>
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getPortfolioId(Map<String, Object> param){
        param.put("portType", DictCodeListExt1.组合类型_投资类组合.getValue());
        param.put("portType1", DictCodeListExt1.组合类型_子组合.getValue());
        return etrFundMapper.getPortfolioId(param);
    }
    
    /**
     * 
     * 资产代码联想文本框
     * <功能详细描述>
     * @param finprodId String
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodMarketId(Map<String, Object> param){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", param.get("finprodId"));
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_基金.getValue());

        // add by etliu 20210501 银行间场内场外拆分 start
        map.put("defaultTradeMarketList", PageMenuSplitUtil.getTradeMarketList((String) param.get("pageId")));
        // add by etliu 20210501 银行间场内场外拆分 end

        return etrFundMapper.getFinprodMarketId(map);
    }
    
    /**
     * 关闭、撤销、删除委托画面校验 
     * <功能详细描述>
     * @param dto EtrFundDto
     * @return List<ValidMessage>
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrFundDto dto) {
        String entrustId = dto.getEntrustId();
        String delegateOperationType = dto.getEntrustOperationType();
        String delegateCloseWay = dto.getCloseWay();
        List<String> traderList = new ArrayList<>();
        traderList.add(dto.getTrader());
        return baseEtrInvestInfoService.checkDelegateOperation(entrustId, delegateOperationType,
            traderList, delegateCloseWay);
    }
    
    /**
     * 
     * 删除委托
     * 调用委托基础组件“删除委托”
     * @param entrustId String
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        if(baseEtrInvestInfoService != null){
            return  baseEtrInvestInfoService.deleteDelegate(entrustId);
        }
        else{
            return false;
        }
    }
    
    /**
     * 
     * 撤回委托
     * 调用基础组件“撤回委托”
     * @param entrustId String
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(String entrustId){
        //投资委托信息dto
        BaseEtrInvestInfoDto dto = new BaseEtrInvestInfoDto();
        //委托指令
        EtrEntrust etrEntrust = new EtrEntrust();
        etrEntrust.setEntrustId(entrustId);
        dto.setEtrEntrust(etrEntrust);
        if(baseEtrInvestInfoService != null){
            return baseEtrInvestInfoService.recallDelegate(dto);
        }
        else{
            return false;
        }
    }
    
    /**
     * 
     * 关闭委托
     * 调用基础组件“关闭委托”
     * @param entrustId String
     * @param closeWay String
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId,String closeWay){
        if(baseEtrInvestInfoService != null){
            return baseEtrInvestInfoService.closeDelegate(entrustId, closeWay);
        }
        else{
            return false;
        }
    }
    
    /**
     * 新建页面资产代码联想 <功能详细描述>
     * 
     * @param param 用户输入值
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSelectFinprodMarketId(int page,int length,Map<String, Object> param) {
        param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        param.put("profitType1", DictCodeList.收益类型_货币型.getValue());
        param.put("profitType2", DictCodeList.收益类型_净值型.getValue());
//        param.put("tradeMarket", DictCodeListExt1.交易场所_OTC场外.getValue());
        param.put("TRM_FUND_TYPE_01", DictCodeListExt2.交易所基金类型_ETF.getValue());
        param.put("TRM_FUND_TYPE_03", DictCodeListExt2.交易所基金类型_分级基金.getValue());
        param.put("TRM_FUND_TYPE_04", DictCodeListExt2.交易所基金类型_封闭式基金.getValue());
        param.put("TRM_FUND_TYPE_05", DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue());
        param.put("GRA_FUND_TYPE_M", DictCodeListExt2.分级基金_母基金.getValue());
        param.put("FUND_ASSET_TYPE_F02", DictCodeList.基金类资产类型_非货币基金.getValue());
        param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        param.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        param.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        param.put("PS_T06", DictCodeList.交易方向_赎回.getValue());

        // add by etliu 20210501 银行间场内场外拆分 start
        param.put("defaultTradeMarketList", PageMenuSplitUtil.getTradeMarketList((String) param.get("parentPageId")));
        // add by etliu 20210501 银行间场内场外拆分 end
        param.put("sysdate", platformDateImpl.getSysDate());
        Page<SelectOptionDto> pagelist = this.queryForPageForSelectReturn(page, length,() -> etrFundMapper.getSelectFinprodMarketId(param));
        List<SelectOptionDto> list = pagelist.getRoot();
        for(SelectOptionDto dto :list){
            if(dto.getCol().equals(DictCodeList.收益类型_货币型.getValue())){
                dto.setCol(DictCodeList.基金类资产类型_货币基金.getValue());
            }
            if(dto.getCol().equals(DictCodeList.收益类型_净值型.getValue())){
                dto.setCol(DictCodeList.基金类资产类型_非货币基金.getValue());
            }
        }
        return pagelist; 
    }

    /**
     * 新建页面组合代码联想下拉框 <功能详细描述>
     * 
     * @param map Map<String, Object>
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSelectPortfolioId(int page,int length,Map<String, Object> map) {
        map.put("portType", DictCodeListExt1.组合类型_投资类组合.getValue());
        map.put("portType1", DictCodeListExt1.组合类型_子组合.getValue());
        return this.queryForPageForSelectReturn(page, length,() -> etrFundMapper.getSelectPortfolioId(map));
    }
    
    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity EtrFundDto
     * @throws IllegalAccessException IllegalAccessException
     * @throws InvocationTargetException InvocationTargetException
     * @see
     * @since 1.0
     */
    public void doInsert(EtrFundDto entity)
        throws IllegalAccessException,
        InvocationTargetException {
        //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(entity.getFileArray());
        mstFileInputParameter.setBussinessId(entity.getWorkflowId());
        if(mstFileCommonService != null){
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }
    
    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity EtrFundDto
     * @throws IllegalAccessException IllegalAccessException
     * @throws InvocationTargetException InvocationTargetException
     * @see
     * @since 1.0
     */
    public void doDelete(EtrFundDto entity)
        throws IllegalAccessException,
        InvocationTargetException {
        //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(entity.getFileArray());
        mstFileInputParameter.setBussinessId(entity.getEntrustId());
        if(mstFileCommonService != null){
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }
    
    /**
     * 实现docomplement方法
     * @param entity EtrBrowDepoInfoDto
     * @throws InvocationTargetException IllegalAccessException
     * @throws IllegalAccessException InvocationTargetException
     */
    public void doComplete(EtrFundDto entity)
        throws IllegalAccessException,
        InvocationTargetException {
        // 投资委托信息dto
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        String type = entity.getOperateType();
        String isPushMsg = DictCodeList.是否_否.getValue();
        if (StringUtil.equals(type, "NEW")) {
            // 委托指令Entity
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, entity);
            BeanUtil.copyProperties(etrEntrust, entity.getBussEtrInvestPersonInfoDto());
            //设置委托状态
            etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            if(platformDateImpl != null){
                etrEntrust.setEntrustDate(platformDateImpl.getSysDate()); 
            }
            // 委托指令附属表Entity
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_基金.getValue());
            BeanUtil.copyProperties(etrEntrustAdd, entity);
            //设置资产一级类型：资产一级分类=资产类型        
            //etrEntrustAdd.setAssetType1(entity.getAssetType1());
            //设置起息日到期日
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            // List<委托指令回购质押品Entity>

            // List<委托指令分发Entity>
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            /*List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,entity.getBussEtrInvestPersonInfoDto().getTrader());
            for(String trader:traderList){
                EtrDistribute etrDistribute = new EtrDistribute();
                //设置接收状态
                etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                //设置指定交易员指定交易员 = 指定交易员       
                etrDistribute.setTrader(trader);
                //设置委托代码
                etrDistribute.setEntrustId(entity.getEntrustId());
                //
                etrDistributeList.add(etrDistribute);
            }*/
            // List<委托完成状况表Entity>
            List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
           /* EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            BeanUtil.copyProperties(etrFinishStatus, entity);
            etrFinishStatusList.add(etrFinishStatus);*/

            baseEtrInvestInfoDto.setEtrEntrust(etrEntrust);
            baseEtrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);
            if(busiAutoEtrDispService != null){
                //检测是否设置交易员
                if(!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)){
                    //跳出自动分发
                    baseEtrInvestInfoDto.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                }
                else{
                    //查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if(busiAutoEtrDispService.checkEntrustTime(etrEntrust)){
                        //自动分发
                        baseEtrInvestInfoDto.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            baseEtrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);

            // 新建委托
            if (baseEtrInvestInfoService != null) {
                baseEtrInvestInfoService.newDelegate(baseEtrInvestInfoDto);
            }
            //文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getEntrustId());
            if(mstFileCommonService != null){
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        }
        else {
            // 委托指令Entity
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, entity);
            etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            if(platformDateImpl != null){
                etrEntrust.setEntrustDate(platformDateImpl.getSysDate()); 
            }
            // 委托指令附属表Entity
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            BeanUtil.copyProperties(etrEntrustAdd, entity);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_基金.getValue());
            BeanUtil.copyProperties(etrEntrust, entity.getBussEtrInvestPersonInfoDto());
            //设置起息日到期日
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            // List<委托指令回购质押品Entity>

            // List<委托指令分发Entity>
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,entity.getBussEtrInvestPersonInfoDto().getTrader());
            for(String trader:traderList){
                EtrDistribute etrDistribute = new EtrDistribute();
                //设置接收状态
                etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                //设置指定交易员指定交易员 = 指定交易员       
                etrDistribute.setTrader(trader);
                //设置委托代码
                etrDistribute.setEntrustId(entity.getEntrustId());
                //
                etrDistributeList.add(etrDistribute);
            }
            
            // List<委托完成状况表Entity>
            List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
            /*EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            BeanUtil.copyProperties(etrFinishStatus, entity);
            etrFinishStatusList.add(etrFinishStatus);*/

            baseEtrInvestInfoDto.setEtrEntrust(etrEntrust);
            baseEtrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);
            if(busiAutoEtrDispService != null){
                //检测是否设置交易员
                if(!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)){
                    //跳出自动分发
                    baseEtrInvestInfoDto.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                }
                else{
                    //查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if(busiAutoEtrDispService.checkEntrustTime(etrEntrust)){
                        //自动分发
                        baseEtrInvestInfoDto.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            baseEtrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);

            // 修改委托
            if (baseEtrInvestInfoService != null) {
                baseEtrInvestInfoService.updateDelegate(baseEtrInvestInfoDto);
            }
            
            //上传文件修改更新
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setBussinessId(entity.getEntrustId());
            mstFileInputParameter.setFileList(entity.getFileArray());
            if(mstFileCommonService != null){
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        }
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", entity.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }

//        if(baseEtrCashPositionCommService!=null){
//            baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
//        }
    }
    
    /**
     * 编辑、双击查看页面详情 <功能详细描述>
     * 
     * @param entrustId String
     * @param finprodId String
     * @param portfolioId String
     * @return EtrFundDto
     * @see
     * @since 1.0
     */
    public EtrFundDto selectByKey(String entrustId, String finprodId, String portfolioId) {
        if(finprodId.equals("null")){
            finprodId = null;
        }
        EtrFundDto etrFundDto = etrFundMapper.selectByKey(entrustId,finprodId);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("entrustId", entrustId);
        List<String> traderList = entrustCommonPersonSelectMapper.getTraderList(map);
        String trader = "";
        for(int i=0; i<traderList.size(); i++){
            trader = trader +","+ traderList.get(i);
        }
        etrFundDto.getBussEtrInvestPersonInfoDto().setTrader(trader);
        return etrFundDto;
    }
    
    /**
     * 根据金融产品代码获取委托金额 <功能详细描述>
     * 
     * @param finprodId 金融产品代码
     * @return long
     * @see
     * @since 1.0
     */
    public BigDecimal selectEntrustAmt(String finprodId) {
        List<String> statusList = new ArrayList<String>();
        statusList.add(DictCodeList.委托状态_待接收.getValue());
        statusList.add(DictCodeListExt3.委托状态_待分发.getValue());
        statusList.add(DictCodeList.委托状态_已接收.getValue());
        statusList.add(DictCodeList.委托状态_已完成.getValue());
        statusList.add(DictCodeList.委托状态_已撤回.getValue());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("statusList", statusList);
        
        map.put("ps", DictCodeList.交易方向_申购.getValue());
        return etrFundMapper.selectEntrustAmt(map);
    }

    /**
     * 根据金融产品代码获取发行规模 <功能详细描述>
     * 
     * @param finprodId String
     * @return long
     * @see
     * @since 1.0
     */
    public BigDecimal selectIssueAmt(String finprodId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        return etrFundMapper.selectIssueAmt(map);

    }
    
    /**
     * 组合可用现金查询
     * <功能详细描述>
     * @param param PtlCashPositionFlowDto
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        /*BigDecimal result=null;
        if(basePtlCashPositionInfoService!=null){
            result = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
        }
        return result;*/
        if (basePtlCashPositionInfoService != null) {
            PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
            Date vdate = DateUtil.parse((String)param.get("vdate"));
            Map<String, Object> result = new HashMap<>();
            dto.setCdate(vdate);
            List<String> acctTypeList = new ArrayList<>();
            acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
            dto.setAcctType(acctTypeList);
            dto.setPortfolioId((String)param.get("portfolioId"));
            dto.setCcy((String)param.get("ccy"));
            BigDecimal cashTG = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            result.put("cashTG", cashTG);
            acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            result.put("cashT", cashT);
            if (StringUtil.isNotEmpty(param.get("vdate1"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date vdate1 = DateUtil.parse((String)param.get("vdate1"));
                dto2.setCdate(vdate1);
                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId((String)param.get("portfolioId"));
                dto2.setCcy((String)param.get("ccy"));
                BigDecimal cashT1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto2);
                result.put("cashT1", cashT1);
            }
            // ZGZGST-1552:增加资金账户可用现金查询 fuyongsheng 20210827 add start
            acctTypeList.clear();
            acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            BigDecimal cashT4 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
            result.put("cashT4", cashT4);
            // ZGZGST-1552:增加资金账户可用现金查询 fuyongsheng 20210827 add end
            return result;
        }
        else {
            return null;
        }
    }

    /**
     * 组合可用持仓查询
     * <功能详细描述>
     * @param dto PtlSecPositionFlowDto
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    public BigDecimal getAssetAvailablePosition(PtlSecPositionFlowDto dto) {
        BigDecimal result = null;
        if(basePtlSecPositionInfoService!=null){
            result = basePtlSecPositionInfoService.queryUsableSecPosition(dto);
        }
        return result;
    }
    
    /**
     * 
     * 查询单位净值
     * <功能详细描述>
     * @param map Map<String,Object>
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    public BigDecimal getPrice(Map<String,Object> map){
        map.put("sysDate", DateUtil.parse((String)map.get("sysDate")));
        return etrFundMapper.getPrice(map);
    }
    
    /**
     * 新建页面通道下拉框
     * @param page
     * @param length
     * @param map
     * @return
     */
    public Page<SelectOptionDto> getSelectTongDao(int page,int length,Map<String, Object> map) {
        return super.queryForPageForSelectReturn(page, length,() -> etrFundMapper.getSelectTongDao(map));
    }
    /**
     * 通道可用现金查询
     * @param map
     * @return
     */
    
    public BigDecimal getTongDaoCash(Map<String, Object> map){
        map.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
        map.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());

        map.put("platDate",platformDateImpl.getSysDate());
    	return etrFundMapper.getTongDaoCash(map);
    }

    
    /**
     * 
     * 查询资产管理人
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String selectManeger(String issuer){
        if(StringUtil.isNotEmpty(issuer)){
            return etrFundMapper.selectManeger(issuer);
        }else{
            return null;
        }
    }

    public List<String> findSameNameCounterId(Map<String, Object> map) {
        return etrFundMapper.findSameNameCounterId(map);
    }

    public BaseEtrInvestInfoDto initBaseDto(EtrFundDto dto){
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();

        try {
            // etrEntrust
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getBussEtrInvestPersonInfoDto());
            //设置委托状态
            etrEntrust.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            if(platformDateImpl != null){
                etrEntrust.setEntrustDate(platformDateImpl.getSysDate());
            }
            entity.setEtrEntrust(etrEntrust);

            // etrEntrustAdd
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_基金.getValue());
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            //设置起息日到期日
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            entity.setEtrEntrustAdd(etrEntrustAdd);

            // 添加流程编号
            entity.setWorkFlowId(dto.getWorkflowId());
        } catch (IllegalAccessException | InvocationTargetException e) {
            // 对象转换赋值失败
            throw new RuntimeException("对象转换赋值失败", e);
        }
        return entity;
    }
}
