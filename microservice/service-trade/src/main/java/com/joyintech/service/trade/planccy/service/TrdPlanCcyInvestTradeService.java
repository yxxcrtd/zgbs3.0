/*
 * 文件名：TrdPlanCcyInvestTradeService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.trade.planccy.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.common.service.TrdAvailableCashQueryService;
import com.joyintech.service.trade.planccy.dao.TrdPlanCcyInvestTradeMapper;
import com.joyintech.service.trade.planccy.dto.TrdPlanCcyInvestTradeDto;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.RepTradeMapper;
import com.joyintech.tams.common.base.dao.WflPaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.WflStlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.RepTrade;
import com.joyintech.tams.common.base.entity.RepTradeKey;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.WflPaTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflPaTrdProductDealKey;
import com.joyintech.tams.common.base.entity.WflStlPayOrders;
import com.joyintech.tams.common.base.entity.WflStlPayOrdersKey;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAdd;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAddKey;
import com.joyintech.tams.common.base.entity.WflTrdProductDealKey;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2019年1月7日
 * @see TrdPlanCcyInvestTradeService
 * @since 1.0
 */
@Service
public class TrdPlanCcyInvestTradeService extends BaseService<TrdPlanCcyInvestTradeDto> {
    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 投资委托组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    /**
     * 组合可用持仓和实际持仓查询更新组件
     */
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    /**
     * 自定义mapper
     */
    @Autowired
    private TrdPlanCcyInvestTradeMapper trdPlanCcyInvestTradeMapper;

    /**
     * 金融产品交易业务流程表
     */
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    /**
     * 金融产品附属业务流程表
     */
    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    /**
     * 金融产品交易扩展业务流程表
     */
    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    /**
     * 划款指令业务流程表
     */
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    /**
     * 组合交易表
     */
    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    /**
     * 金融产品交易扩展
     */
    @Autowired
    private PaTrdProductDealMapper PaTrdProductDealMapper;

    /**
     * 平台日获取接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 交易报送信息表
     */
    @Autowired
    private RepTradeMapper repTradeMapper;

    /**
     * 金融产品交易组件
     */
    @Autowired(required = false)
    private BaseTradeProductDealService baseTradeProductDealService;
    
    @Autowired(required = false)
    private TrdAvailableCashQueryService trdAvailableCashQueryService;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by renping 20190205 end

    /**
     * 分页查询 <功能详细描述>
     * 
     * @param page
     * @param length
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<TrdPlanCcyInvestTradeDto> selectForPage(int page, int length,
                                                     Map<String, Object> pageParam)
    {
        pageParam.put("VALID_STATUS_01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("VALID_STATUS_02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("VALID_STATUS_03", DictCodeListExt1.交易流程状态_撤销.getValue());
        pageParam.put("YES_NO_NO", DictCodeList.是否_否.getValue());
        pageParam.put("TRADESTATUS_NEW", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("TRADESTATUS_REVOKE", DictCodeListExt1.交易状态_撤销.getValue());
        pageParam.put("COMMON_BUSINESS_TYPE_TRD02", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        pageParam.put("PROFIT_TYPE", DictCodeList.收益类型_货币型.getValue());
        pageParam.put("PS24", DictCodeList.交易方向_赎回到账.getValue());
        List<String> psList = new ArrayList<>();
        if(StringUtil.isNotEmpty(pageParam.get("type9"))){
            pageParam.put("type9List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type9")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("type10"))){
            pageParam.put("type10List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type10")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("type11"))){
            pageParam.put("type11List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type11")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("portfolioId"))){
            pageParam.put("portfolioId",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("portfolioId")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("ps"))){
            psList.add((String)pageParam.get("ps"));
        }else{
            psList.add(DictCodeList.交易方向_买入.getValue());
            psList.add(DictCodeList.交易方向_卖出.getValue());
            psList.add(DictCodeList.交易方向_申购.getValue());
            psList.add(DictCodeList.交易方向_申购确认.getValue());
            psList.add(DictCodeList.交易方向_赎回.getValue());
            psList.add(DictCodeList.交易方向_赎回确认.getValue());
            psList.add(DictCodeList.交易方向_赎回到账.getValue());
        }
        pageParam.put("psList", psList);
        Page<TrdPlanCcyInvestTradeDto> pageDto = this.queryForPage(page, length,
            () -> trdPlanCcyInvestTradeMapper.selectDeal(pageParam));
        List<TrdPlanCcyInvestTradeDto> listDto = pageDto.getRoot();
        for (TrdPlanCcyInvestTradeDto tfitDto : listDto) {
            if ((StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_申购.getValue())
                 || StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_赎回.getValue())||StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_赎回确认.getValue()))
                && StringUtil.equals(tfitDto.getValidStatus(),
                    DictCodeListExt1.交易流程状态_有效.getValue())) {
                Map<String, Object> param = new HashMap<>();
                param.put("bTradeId", tfitDto.getTradeId());
                param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
                param.put("yes", DictCodeList.是否_是.getValue());
                int flg1 = trdPlanCcyInvestTradeMapper.selectConfirmInfo(param);
                int flg2 = trdPlanCcyInvestTradeMapper.selectWflConfirmInfo(tfitDto.getTradeId());
                if (flg1 == 0 && flg2 == 0) {
                    tfitDto.setFlg("01");
                }
                else {
                    tfitDto.setFlg("02");
                }
            }
            else {
                tfitDto.setFlg("02");
            }
        }
        return pageDto;
    }

    /**
     * 资产代码联想下拉框 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFinprodId(Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        param.put("PROFIT_TYPE", DictCodeList.收益类型_货币型.getValue());
        return trdPlanCcyInvestTradeMapper.getFinprodId(param);
    }

    /**
     * 提交流程启动时调用方法 <功能详细描述>
     * 
     * @param entity
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @see
     * @since 1.0
     */
    public void doInsert(TrdPlanCcyInvestTradeDto entity)
        throws IllegalAccessException, InvocationTargetException {
        if (entity.getOperateType().equals("submit")) {
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                Map<String, Object> map = new HashMap<>();
                Map<String, Object> param = new HashMap<>();
                param.put("tradeId", entity.getbTradeId());
                map = selectAccountTrdId(param);
                if(map == null){
                    map = selectAccountTrdIdinwfl(entity.getbTradeId());
                }
                if(map != null){
                String tradId = (String)map.get("TRADID");
                WflTrdProductDealKey key1 = new WflTrdProductDealKey();
                key1.setTradeId(tradId);
                key1.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflTrdProductDealMapper.deleteByPrimaryKey(key1);
                WflTrdProductDealAddKey key2 = new WflTrdProductDealAddKey();
                key2.setTradeId(tradId);
                key2.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflTrdProductDealAddMapper.deleteByPrimaryKey(key2);
                WflPaTrdProductDealKey key3 = new WflPaTrdProductDealKey();
                key3.setTradeId(tradId);
                key3.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflPaTrdProductDealMapper.deleteByPrimaryKey(key3);
                WflStlPayOrdersKey key4 = new WflStlPayOrdersKey();
                key4.setOrderId(entity.getBussTrdTransferOrder().get(0).getOrderId());
                key4.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflStlPayOrdersMapper.deleteByPrimaryKey(key4);
                }
            }
            
            // 调用授信共同 sjj start
            TrdProductDeal TrdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
            inputDto.setTrdProductDeal(TrdProductDeal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
            // 调用授信共同 sjj end
            
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
//            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
//                wflTrdProductDeal.setTradeAmt(null);
//            }
            wflTrdProductDealMapper.insert(wflTrdProductDeal);
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAdd.setRedCfmDate(entity.getRedCfmDate());
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
            wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
            // 划款指令
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                if (entity.getBussTrdTransferOrder() != null
                    && entity.getBussTrdTransferOrder().size() > 0) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                        entity.getBussTrdTransferOrder().get(0));
                    wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflStlPayOrdersMapper.insert(wflStlPayOrders);
                }
            }
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getWorkflowId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
        else if (entity.getOperateType().equals("save")) {
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getWorkflowId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
        else if (entity.getOperateType().equals("REVOKE")) {
            List<String> idList = new ArrayList<>();
            idList.add(entity.getTradeId());
            // 调用金融产品交易组件查询交易信息
            List<BaseTradeProductDealDto> dtoList = baseTradeProductDealService.select(idList);
            for (BaseTradeProductDealDto btpdd : dtoList) {
                TrdProductDeal tpddto = btpdd.getTrdProductDeal();
                tpddto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                tpddto.setpTradeId(entity.getTradeId());
                tpddto.setCancelRemark(entity.getCancelRemark());
                tpddto.setTradeId(entity.getTradeId() + "R");
                tpddto.setWorkflowId(entity.getWorkflowId());
                WflTrdProductDeal wtpd = new WflTrdProductDeal();
                WflTrdProductDealAdd wtpda = new WflTrdProductDealAdd();
                WflPaTrdProductDeal wptpd = new WflPaTrdProductDeal();
                WflStlPayOrders wspo = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(wtpd, tpddto);
                wtpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                BeanUtil.copyPropertiesIgnoreNull(wtpda, btpdd.getTrdProductDealAdd());
                wtpda.setTradeId(wtpd.getTradeId());
                wtpda.setRedCfmDate(entity.getRedCfmDate());
                wtpda.setWorkflowId(entity.getWorkflowId());
                wtpda.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wtpda.setPortfolioId(entity.getActPortfolioId());
                BeanUtil.copyPropertiesIgnoreNull(wptpd,
                    PaTrdProductDealMapper.selectByPrimaryKey(entity.getTradeId()));
                wptpd.setTradeId(wtpd.getTradeId());
                wptpd.setWorkflowId(entity.getWorkflowId());
                wptpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                if (btpdd.getStlPayOrdersList() != null
                    && btpdd.getStlPayOrdersList().size() > 0) {
                    BeanUtil.copyPropertiesIgnoreNull(wspo, btpdd.getStlPayOrdersList().get(0));
                    wspo.setTradeId(wtpd.getTradeId());
                    wspo.setWorkflowId(entity.getWorkflowId());
                    wspo.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflStlPayOrdersMapper.insert(wspo);
                }
                wflTrdProductDealMapper.insert(wtpd);
                wflTrdProductDealAddMapper.insert(wtpda);
                wflPaTrdProductDealMapper.insert(wptpd);
            }
        }
    }

    /**
     * 流程结束时调用方法 <功能详细描述>
     * 
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public void doComplete(TrdPlanCcyInvestTradeDto entity) {
        if (entity.getOperateType().equals("submit")) {
            String tradId = entity.getTradeId();
            // 删除业务流程表
            trdPlanCcyInvestTradeMapper.deleteWflTrdProductDeal(tradId);
            trdPlanCcyInvestTradeMapper.deleteWflTrdProductDealAdd(tradId);
            trdPlanCcyInvestTradeMapper.deleteWflPaTrdProductDeal(tradId);
            if (!StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
                trdPlanCcyInvestTradeMapper.deleteWflStlPayOrders(tradId);
            }
            // 调用授信共同 sjj start
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
//            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
            inputDto.setTrdProductDeal(trdProductDeal);
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
            inputDto.setTrdTradeFeeDetail(entity.getTrdTradeFeeDetail());
            
            // 插入组合交易表
            if(CollectionUtils.isNotEmpty(entity.getPtlTrade())) {
                entity.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insert(ptlTrade));
            }
            // 插入金融产品交易扩展表
            PaTrdProductDealMapper.insert(entity.getPaTrdProductDeal());
            Map<String, Object> map = new HashMap<>();
            map.put("product", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
            map.put("portfolioId", entity.getPortfolioId());
            // 插入交易报送表
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                String prodCode = trdPlanCcyInvestTradeMapper.selProdCode(map);
                if (StringUtil.isNotEmpty(prodCode)) {
                    RepTrade repentity = new RepTrade();
                    repentity.setRepFinprodRegId(prodCode);
                    repentity.setRepBankTradeCode(entity.getTradeId());
                    repentity.setRepBankAssetCode(entity.getFinprodId());
                    repentity.setRepCcy(entity.getCcy());
                    repentity.setRepOccurMoney(entity.getPrinAmt());
                    repentity.setRepIssuePartyId(entity.getIssuePartyId());
                    repentity.setRepTradeComponent(entity.getCounterId());
                    BigDecimal cloPrice = new BigDecimal(1);
                    if (!StringUtil.equals(entity.getCcy(),
                        DictCodeList.币种_人民币_CNY.getValue())) {
                        map.put("ccy", entity.getCcy());
                        map.put("cny", DictCodeList.币种_人民币_CNY.getValue());
                        map.put("tradDate", entity.getTradeDate());
                        BigDecimal num = trdPlanCcyInvestTradeMapper.selCloPrice(map);
                        if(num != null){
                            cloPrice = num;
                        }
                    }
                    repentity.setRepMoneyCny(entity.getPrinAmt().multiply(cloPrice));
                    repentity.setRepCount(entity.getShareAmt());
                    repentity.setRepMoneyFlowType(entity.getPs().equals(
                        DictCodeList.交易方向_申购确认.getValue())||entity.getPs().equals(
                            DictCodeList.交易方向_买入.getValue()) ? DictCodeListExt2.资金流动类型_资产买入.getValue() : DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
                    repentity.setRepTradeDate(entity.getTradeDate());
                    repentity.setRepTradeComponent(entity.getCounterTrader());
                    repentity.setRepSendStatus(DictCodeList.是否_是.getValue());
                    if (iPlatformDate != null) {
                        Date sysdate = iPlatformDate.getSysDate();
                        repentity.setRepStatusDate(sysdate);
                    }
                    repentity.setRepRemark(entity.getRemark());
                    // 调交易报送组件
                    repTradeMapper.insert(repentity);
                }
            }
            map.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
            map.put("finprodId", entity.getFinprodId());
            trdPlanCcyInvestTradeMapper.updateRep(map);
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getTradeId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
            // 反向交易增加 start by qiangchengxi 2019年3月12日
            AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, entity.getTrdProductDealAdd(), entity.getPaTrdProductDeal(),
                    entity.getBussTrdTransferOrder(), entity.getPtlTrade(), entity.getActPortfolioId());
            // 反向交易增加 end by qiangchengxi 2019年3月12日

            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            if (entity.getBussTrdTransferOrder() != null && !entity.getBussTrdTransferOrder().isEmpty()) {
                StlPayOrders order = new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(order, entity.getBussTrdTransferOrder().get(0));
                inputDto.setStlPayOrders(order);
            }
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 调用授信共同 sjj end

            // 调用投管接口 add by renping 20190205 start
            if (!(StringUtil.equals(DictCodeList.交易方向_申购.getValue(), entity.getPs())
                  || StringUtil.equals(DictCodeList.交易方向_赎回.getValue(), entity.getPs())
                  || StringUtil.equals(DictCodeList.交易方向_赎回到账.getValue(), entity.getPs()))) {
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdPlanCcyInvestTrade002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setTrdProductDeal(inputDto.getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(inputDto.getTrdProductDealAdd());
                investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                if (entity.getTrdTradeFeeDetail() != null) {
                    investInterfCommDto.setTradeFeeDetaillist(
                        Arrays.asList(entity.getTrdTradeFeeDetail()));
                }
                investInterfCommDto.setOtherTypeForConvert(
                    DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
                investInterfCommService.doInvestInterf(investInterfCommDto);
            }
            // 调用投管接口 add by renping 20190205 end
        }
        else if (StringUtil.equals(entity.getOperateType(), "REVOKE")) {
            PaTrdProductDeal ptpd = PaTrdProductDealMapper.selectByPrimaryKey(entity.getTradeId());
            ptpd.setTradeId(ptpd.getTradeId() + "R");
            PaTrdProductDealMapper.insert(ptpd);
            String Tid = entity.getTradeId();
            String workflowId = entity.getWorkflowId();
            String workflowStatus = DictCodeListExt1.画面数据状态_流程中.getValue();
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
                Map<String, Object> accountId = new HashMap<>();
                accountId =selectAccountTrdIdinwfl(Tid);
                if(accountId == null){
                    Map<String, Object> param = new HashMap<>();
                    param.put("tradeId", Tid);
                    accountId = selectAccountTrdId(param);
                }
                if(accountId != null){
                String accountTradId = (String)accountId.get("TRADID");
                WflTrdProductDealKey key1 = new WflTrdProductDealKey();
                key1.setTradeId(accountTradId);
                key1.setWorkflowStatus(workflowStatus);
                wflTrdProductDealMapper.deleteByPrimaryKey(key1);
                WflTrdProductDealAddKey key2 = new WflTrdProductDealAddKey();
                key2.setTradeId(accountTradId);
                key2.setWorkflowStatus(workflowStatus);
                wflTrdProductDealAddMapper.deleteByPrimaryKey(key2);
                WflPaTrdProductDealKey key3 = new WflPaTrdProductDealKey();
                key3.setTradeId(accountTradId);
                key3.setWorkflowStatus(workflowStatus);
                wflPaTrdProductDealMapper.deleteByPrimaryKey(key3);
                trdPlanCcyInvestTradeMapper.deleteWflStlPayOrders(accountTradId);
                }
            }
            trdPlanCcyInvestTradeMapper.deleteWflStlPayOrdersByWfl(workflowId);
            trdPlanCcyInvestTradeMapper.deleteWflTrdProductDealByWfl(workflowId);
            trdPlanCcyInvestTradeMapper.deleteWflTrdProductDealAddByWfl(workflowId);
            trdPlanCcyInvestTradeMapper.deleteWflPaTrdProductDealByWfl(workflowId);
            RepTradeKey repTradeKey = new RepTradeKey();
            repTradeKey.setRepBankTradeCode(Tid);
            repTradeKey.setRepMoneyFlowType(entity.getPs().equals(
                DictCodeList.交易方向_申购确认.getValue()) || entity.getPs().equals(
                    DictCodeList.交易方向_买入.getValue()) ? DictCodeListExt2.资金流动类型_资产买入.getValue() : DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
            repTradeMapper.deleteByPrimaryKey(repTradeKey);
        }
    }

    @Autowired
    private TradeProductDealCommService tradeProductDealCommService;
    /**
     * 代办中修改时流程 <功能详细描述>
     * 
     * @param entity
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    public void doUpdate(TrdPlanCcyInvestTradeDto entity)
        throws IllegalAccessException, InvocationTargetException {
        tradeProductDealCommService.deleteWflData(entity.getWorkflowId());
        if (entity.getOperateType().equals("submit")) {
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                Map<String, Object> map = new HashMap<>();
                Map<String, Object> param = new HashMap<>();
                param.put("tradeId", entity.getbTradeId());
                map = selectAccountTrdId(param);
                if(map == null){
                    map = selectAccountTrdIdinwfl(entity.getbTradeId());
                }
                if(map!=null){
                String tradId = (String)map.get("TRADID");
                WflTrdProductDealKey key1 = new WflTrdProductDealKey();
                key1.setTradeId(tradId);
                key1.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflTrdProductDealMapper.deleteByPrimaryKey(key1);
                WflTrdProductDealAddKey key2 = new WflTrdProductDealAddKey();
                key2.setTradeId(tradId);
                key2.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflTrdProductDealAddMapper.deleteByPrimaryKey(key2);
                WflPaTrdProductDealKey key3 = new WflPaTrdProductDealKey();
                key3.setTradeId(tradId);
                key3.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflPaTrdProductDealMapper.deleteByPrimaryKey(key3);
                WflStlPayOrdersKey key4 = new WflStlPayOrdersKey();
                key4.setOrderId(entity.getBussTrdTransferOrder().get(0).getOrderId());
                key4.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflStlPayOrdersMapper.deleteByPrimaryKey(key4);
                }
            }
            
            // 调用授信共同 sjj start
            TrdProductDeal TrdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
            inputDto.setTrdProductDeal(TrdProductDeal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
            // 调用授信共同 sjj end
            
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
//            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
//                wflTrdProductDeal.setTradeAmt(null);
//            }
            wflTrdProductDealMapper.insert(wflTrdProductDeal);
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAdd.setRedCfmDate(entity.getRedCfmDate());
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                if (entity.getBussTrdTransferOrder() != null
                    && entity.getBussTrdTransferOrder().size() > 0) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                        entity.getBussTrdTransferOrder().get(0));
                    wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflStlPayOrdersMapper.insert(wflStlPayOrders);
                }
            }
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getWorkflowId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
        else if (entity.getOperateType().equals("save")) {
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getWorkflowId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
        else if (entity.getOperateType().equals("REVOKE")) {
            List<String> idList = new ArrayList<>();
            idList.add(entity.getTradeId());
            // 调用金融产品交易组件查询交易信息
            List<BaseTradeProductDealDto> dtoList = baseTradeProductDealService.select(idList);
            for (BaseTradeProductDealDto btpdd : dtoList) {
                TrdProductDeal tpddto = btpdd.getTrdProductDeal();
                tpddto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                tpddto.setpTradeId(entity.getTradeId());
                tpddto.setCancelRemark(entity.getCancelRemark());
                tpddto.setTradeId(entity.getTradeId() + "R");
                tpddto.setWorkflowId(entity.getWorkflowId());
                WflTrdProductDeal wtpd = new WflTrdProductDeal();
                WflTrdProductDealAdd wtpda = new WflTrdProductDealAdd();
                WflPaTrdProductDeal wptpd = new WflPaTrdProductDeal();
                WflStlPayOrders wspo = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(wtpd, tpddto);
                wtpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                BeanUtil.copyPropertiesIgnoreNull(wtpda, btpdd.getTrdProductDealAdd());
                wtpda.setTradeId(wtpd.getTradeId());
                wtpda.setRedCfmDate(entity.getRedCfmDate());
                wtpda.setWorkflowId(entity.getWorkflowId());
                wtpda.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                BeanUtil.copyPropertiesIgnoreNull(wptpd,
                    PaTrdProductDealMapper.selectByPrimaryKey(entity.getTradeId()));
                wptpd.setTradeId(wtpd.getTradeId());
                wptpd.setWorkflowId(entity.getWorkflowId());
                wptpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                if (btpdd.getStlPayOrdersList() != null
                    && btpdd.getStlPayOrdersList().size() > 0) {
                    BeanUtil.copyPropertiesIgnoreNull(wspo, btpdd.getStlPayOrdersList().get(0));
                    wspo.setTradeId(wtpd.getTradeId());
                    wspo.setWorkflowId(entity.getWorkflowId());
                    wspo.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflStlPayOrdersMapper.insert(wspo);
                }
                wflTrdProductDealMapper.insert(wtpd);
                wflTrdProductDealAddMapper.insert(wtpda);
                wflPaTrdProductDealMapper.insert(wptpd);
            }
        }
    }

    /**
     * 流程作废时删除业务流程表 <功能详细描述>
     * 
     * @param workflowId
     * @see
     * @since 1.0
     */
    public void doDelete(String workflowId) {
        trdPlanCcyInvestTradeMapper.deleteWflTrdProductDealByWfl(workflowId);
        trdPlanCcyInvestTradeMapper.deleteWflTrdProductDealAddByWfl(workflowId);
        trdPlanCcyInvestTradeMapper.deleteWflPaTrdProductDealByWfl(workflowId);
        trdPlanCcyInvestTradeMapper.deleteWflStlPayOrdersByWfl(workflowId);
    }

    /**
     * 交易到账撤销流程生效 <功能详细描述>
     * 
     * @param entity
     * @see
     * @since 1.0
     */
//    public void revokeAccountDoComplet(TrdPlanCcyInvestTradeDto entity) {
//        Map<String, Object> map = trdPlanCcyInvestTradeMapper.selectAccountTrdId(
//            entity.getbTradeId());
//        String tradId = (String)map.get("TRADID");
//    }

    /**
     * 插入业务流程交易到账撤销信息 <功能详细描述>
     * 
     * @param entity TrdPlanCcyInvestTradeDto
     * @see
     * @since 1.0
     */
    public void revokeAccount(TrdPlanCcyInvestTradeDto entity) {
        List<String> idList = new ArrayList<>();
        idList.add(entity.getTradeId());
        // 调用金融产品交易组件查询交易信息
        List<BaseTradeProductDealDto> dtoList = baseTradeProductDealService.select(idList);
        for (BaseTradeProductDealDto btpdd : dtoList) {
            TrdProductDeal tpddto = btpdd.getTrdProductDeal();
            tpddto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            tpddto.setpTradeId(entity.getTradeId());
            tpddto.setCancelRemark(entity.getCancelRemark());
            tpddto.setTradeId(entity.getTradeId() + "R");
            tpddto.setWorkflowId(entity.getWorkflowId());
            WflTrdProductDeal wtpd = new WflTrdProductDeal();
            WflTrdProductDealAdd wtpda = new WflTrdProductDealAdd();
            WflPaTrdProductDeal wptpd = new WflPaTrdProductDeal();
            WflStlPayOrders wspo = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wtpd, tpddto);
            wtpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            BeanUtil.copyPropertiesIgnoreNull(wtpda, btpdd.getTrdProductDealAdd());
            wtpda.setTradeId(wtpd.getTradeId());
            wtpda.setRedCfmDate(entity.getRedCfmDate());
            wtpda.setWorkflowId(entity.getWorkflowId());
            wtpda.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            BeanUtil.copyPropertiesIgnoreNull(wptpd,
                PaTrdProductDealMapper.selectByPrimaryKey(entity.getTradeId()));
            wptpd.setTradeId(wtpd.getTradeId());
            wptpd.setWorkflowId(entity.getWorkflowId());
            wptpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            if (btpdd.getStlPayOrdersList() != null && btpdd.getStlPayOrdersList().size() > 0) {
                BeanUtil.copyPropertiesIgnoreNull(wspo, btpdd.getStlPayOrdersList().get(0));
                wspo.setTradeId(wtpd.getTradeId());
                wspo.setWorkflowId(entity.getWorkflowId());
                wspo.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflStlPayOrdersMapper.insert(wspo);
            }
            wflTrdProductDealMapper.insert(wtpd);
            wflTrdProductDealAddMapper.insert(wtpda);
            wflPaTrdProductDealMapper.insert(wptpd);
        }
    }

    /**
     * 查询组合可用现金 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
        Date vdate = DateUtil.parse((String)param.get("vdate"));
        dto.setCdate(vdate);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        dto.setAcctType(acctTypeList);
        dto.setPortfolioId((String)param.get("portfolioId"));
        dto.setCcy((String)param.get("ccy"));
        BigDecimal cashT2 = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("cashT2", cashT2);

        acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
        result.put("cashT", cashT);
        if (StringUtil.isNotEmpty(param.get("calId"))) {
            PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
            Date vdate1 = DateUtil.parse((String)param.get("vdate1"));
            dto2.setCdate(vdate1);
            dto2.setAcctType(acctTypeList);
            dto2.setPortfolioId((String)param.get("portfolioId"));
            dto2.setCcy((String)param.get("ccy"));
            BigDecimal cashT1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto2);
            result.put("cashT1", cashT1);
        }
        // 通道交易 新增 强成西 2019/03/26 start
        if (StringUtil.isNotEmpty(param.get("chlId"))) {
            dto.setNeedAcctType(false);
            dto.setChlFinprodId((String)param.get("chlId"));
            dto.setAcctType(null);
            result.put("cashTD", basePtlCashPositionInfoService.queryUsableCashPosition(dto));
        }
        // 通道交易 新增 强成西 2019/03/26 end+
        return result;
    }

    /**
     * 查询组合可用持仓 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPtlSecPosition(PtlSecPositionFlowDto dto) {
        return basePtlSecPositionInfoService.queryUsableSecPosition(dto);
    }

    /**
     * 获取交易dto <功能详细描述>
     * 
     * @param tradId
     * @return
     * @see
     * @since 1.0
     */
    public List<BaseTradeProductDealDto> getFinDto(String tradId) {
        List<String> tidList = new ArrayList<>();
        tidList.add(tradId);
        return baseTradeProductDealService.select(tidList);
    }

    /**
     * 查看反显数据 <功能详细描述>
     * 
     * @param tradId
     * @return
     * @see
     * @since 1.0
     */
    public TrdPlanCcyInvestTradeDto getTrdFundDto(String tradId,String feeType) {
        Map<String, Object> map = new HashMap<>();
        map.put("tradId", tradId);
        //YUCHUBS-647 交易查询时费用为空 guyunfei 20190910 start
        map.put("feeType",feeType);
        //YUCHUBS-647 交易查询时费用为空 guyunfei 20190910 end
        return trdPlanCcyInvestTradeMapper.getTrdFundDto(map);
    }

    /**
     * 查询业务流程表中交易到账数据 <功能详细描述>
     * 
     * @param tradId
     * @return
     * @see
     * @since 1.0
     */
    public TrdPlanCcyInvestTradeDto getTrdFundDtoForAccount(String tradId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tradId", tradId);
        return trdPlanCcyInvestTradeMapper.getTrdFundDtoForAccount(map);
    }

    /**
     * 查询业务流程表中划款指令 <功能详细描述>
     * 
     * @param bizCode
     * @return
     * @see
     * @since 1.0
     */
    public List<BussTrdTransferOrderBase> selectOrdersByBizCode(String bizCode) {
        return trdPlanCcyInvestTradeMapper.selectOrdersByBizCode(bizCode,
            DictCodeListExt1.收支方向_支出.getValue());
    }

    /**
     * 查询单位净值 <功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPrice(Map<String, Object> map) {
        return trdPlanCcyInvestTradeMapper.getPrice(map);
    }

    /**
     * 查询投资委托DTO <功能详细描述>
     * 
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEtrustDto(String entrustId) {
        if (baseEtrInvestInfoService == null) {
            return new BaseEtrInvestInfoDto();
    }
        BaseEtrInvestInfoDto resultDto = baseEtrInvestInfoService.queryActiveMandate(entrustId);
        if (resultDto != null && resultDto.getEtrEntrust() != null) {
            // 委托数量中扣除已完成额度
            if (!StringUtil.isEmptyList(resultDto.getEtrFinishStatusList())) {
                BigDecimal 已完成额度 = BigDecimal.ZERO;
                for (EtrFinishStatus etrFinishStatus : resultDto.getEtrFinishStatusList()) {
                    已完成额度 = ArithmeticUtil.doAdd(已完成额度,etrFinishStatus.getFinishAmt());
                }

                resultDto.getEtrEntrust().setEntrustAmt(
                        ArithmeticUtil.subtract(resultDto.getEtrEntrust().getEntrustAmt(), 已完成额度));
                if(StringUtil.isNotEmpty(resultDto.getEtrEntrust().getPs()) && 
                        StringUtil.equals(resultDto.getEtrEntrust().getPs(),DictCodeList.交易方向_赎回.getValue())){
                    resultDto.getEtrEntrust().setEntrustNum(
                            ArithmeticUtil.subtract(resultDto.getEtrEntrust().getEntrustNum(), 已完成额度));    
                }
            }
        }
        return resultDto;
    }

    /**
     * 查询资产代码的交易方式 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getPurSpeed(Map<String, Object> param) {
        return trdPlanCcyInvestTradeMapper.getPurSpeed(param);
    }

    /**
     * 插入交易到账信息 <功能详细描述>
     * 
     * @param entity
     * @see
     * @since 1.0
     */
    public void doInsertRedemption(TrdPlanCcyInvestTradeDto entity) {
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflTrdProductDeal.setWorkflowId(null);
        wflTrdProductDealMapper.insert(wflTrdProductDeal);
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
        wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        wflTrdProductDealAdd.setRedCfmDate(entity.getRedCfmDate());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
        wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
        wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
        if (entity.getBussTrdTransferOrder() != null
            && entity.getBussTrdTransferOrder().size() > 0) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                entity.getBussTrdTransferOrder().get(0));
            wflStlPayOrders.setTradeId(entity.getTradeId());
            wflStlPayOrders.setSettleDate(entity.getRedCfmDate());
            wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wflStlPayOrdersMapper.insert(wflStlPayOrders);
        }
     // 文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(entity.getFileArray());
        mstFileInputParameter.setBussinessId(entity.getTradeId());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }

    /**
     * 查询到账交易是否托管匹配 <功能详细描述>
     * 
     * @param tradeId
     * @return
     * @see
     * @since 1.0
     */
    public Integer selRedemption(String tradeId) {
        return trdPlanCcyInvestTradeMapper.selRedemption(tradeId);
    }

    /**
     * 查询撤销数据是否有后继交易 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public boolean revokeCheck(TrdPlanCcyInvestTradeDto dto) {
        Map<String, Object> param = new HashMap<>();
        param.put("bTradeId", dto.getTradeId());
        param.put("yes", DictCodeList.是否_是.getValue());
        param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
        int result = trdPlanCcyInvestTradeMapper.selectConfirmInfo(param);
        int result2 = trdPlanCcyInvestTradeMapper.selectConfirmInfoWfl(param);
        return result+result2 == 0 ? true : false;
    }

    /**
     * 根据赎回确认交易编号查询赎回到账交易编号 <功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> selectAccountTrdId(Map<String, Object> map) {
        return trdPlanCcyInvestTradeMapper.selectAccountTrdId(map);
    }
    
    /**
     * 根据赎回确认交易编号查询赎回到账交易编号 <功能详细描述>
     * 
     * @param tradId
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> selectAccountTrdIdinwfl(String tradId) {
        return trdPlanCcyInvestTradeMapper.selectAccountTrdIdinwfl(tradId);
    }
    
    /**
     * 
     * 查询委托可用金额
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> selectEntrustAmt(Map<String, Object> map){
        map.put("pssg", DictCodeList.交易方向_申购.getValue());
        map.put("pssh", DictCodeList.交易方向_赎回.getValue());
        List<TrdPlanCcyInvestTradeDto> dtoList = trdPlanCcyInvestTradeMapper.selectEntrustAmt(map);
        Map<String,Object> result = new HashMap<>();
        BigDecimal finishAmt = BigDecimal.ZERO;
        BigDecimal prinAmt = BigDecimal.ZERO;
        for(TrdPlanCcyInvestTradeDto dto:dtoList){
        	result.put("ENTRUST_AMT", dto.getEntrustAmt());
        	result.put("ENTRUST_NUM", dto.getEntrustNum());
        	finishAmt =ArithmeticUtil.doAdd(finishAmt,dto.getFinishAmt());
        	prinAmt =ArithmeticUtil.doAdd(prinAmt,dto.getPrinAmt());
        }
        result.put("FINISH_AMT", finishAmt);
        result.put("PRIN_AMT", prinAmt);
        return result;
//        return trdPlanCcyInvestTradeMapper.selectEntrustAmt(map);
    }

    /**
     * 多组合交易查询
     * @param param
     * @return
     */
    public List<TrdPlanCcyInvestTradeDto> selectAllTrade(Map<String, Object> param) {
    	return trdPlanCcyInvestTradeMapper.selectAllTrade(param);
    }
    
    /**
     * 点击确认按钮的校验
     * @param param
     * @return
     */
	public boolean isCheckConfirm(Map<String, Object> param) {
		boolean flag = false;
		List<TrdPlanCcyInvestTradeDto> listDto = trdPlanCcyInvestTradeMapper.selectAllTrade(param);
		for (TrdPlanCcyInvestTradeDto tfitDto : listDto) {
            if ((StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_申购.getValue())
                 || StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_赎回.getValue())||StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_赎回确认.getValue()))
                && StringUtil.equals(tfitDto.getValidStatus(),
                    DictCodeListExt1.交易流程状态_有效.getValue())) {
                Map<String, Object> param2 = new HashMap<>();
                param2.put("bTradeId", tfitDto.getTradeId());
                param2.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
                param2.put("yes", DictCodeList.是否_是.getValue());
                int flg1 = trdPlanCcyInvestTradeMapper.selectConfirmInfo(param2);
                int flg2 = trdPlanCcyInvestTradeMapper.selectWflConfirmInfo(tfitDto.getTradeId());
                if (flg1 == 0 && flg2 == 0) {
//                    tfitDto.setFlg("01");
                }
                else {
//                    tfitDto.setFlg("02");
                    flag = true;
                    break;
                }
            }
            else {
//                tfitDto.setFlg("02");
                flag = true;
                break;
            }
        }
		
		return flag;
	}

	/**
	 * 确认页面组合分配列表查询
	 * @param param
	 * @return
	 */
	public List<BussTrdTransfenpeiDto> selectTrdTransfenpeiList(Map<String, Object> param) {
		Map<String, BigDecimal> resultMap = new HashMap<>();
		BigDecimal unitCprice = new BigDecimal((String)param.get("unitCprice"));
		String ps = (String)param.get("ps");
		List<BussTrdTransfenpeiDto> dtoList = trdPlanCcyInvestTradeMapper.selectTrdTransfenpeiList(param);
		for(BussTrdTransfenpeiDto dto : dtoList){
			param.put("portfolioId", dto.getPortfolioId());
			resultMap = trdAvailableCashQueryService.getAvailableCash(param);
			if(StringUtil.equals(ps, DictCodeList.交易方向_申购确认.getValue())){
				dto.setShareAmt(ArithmeticUtil.divide(dto.getTradeAmt(), unitCprice, 2));
			}else if(StringUtil.equals(ps, DictCodeList.交易方向_赎回确认.getValue())){
				dto.setTradeAmt(ArithmeticUtil.divide(dto.getShareAmt(), unitCprice, 2));
			}
			dto.setBussTrdAvailableCashQueryavalibleCash(resultMap.get("T_0"));
			dto.setBussTrdAvailableCashQueryavalibleCash1(resultMap.get("T_1"));
			dto.setAssetsavailablepositions(resultMap.get("position"));
		}
		return dtoList;
	}
	
	public BigDecimal getDoEntrustMoneyByEntrustId(Map<String, Object> param) {
		String ps = (String)param.get("ps");
		BigDecimal entrustAmt = BigDecimal.ZERO;
		BigDecimal entrustNum = BigDecimal.ZERO;
		BigDecimal resultAmt = BigDecimal.ZERO;
		Map<String, BigDecimal> resultMap = trdPlanCcyInvestTradeMapper.getEntrustAmtByEntrustId(param);
		if(resultMap!=null){
			entrustAmt = resultMap.get("ENTRUSTAMT");
			entrustNum = resultMap.get("ENTRUSTNUM");
		}
		
		if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), ps) 
				|| StringUtil.equals(DictCodeList.交易方向_申购.getValue(), ps)){
			List<BigDecimal> wflPrinAmts = trdPlanCcyInvestTradeMapper.getFinPrintAmt(param);
	    	BigDecimal wflPrinAmt = BigDecimal.ZERO;
	    	for(BigDecimal temp: wflPrinAmts) {
	    		wflPrinAmt = wflPrinAmt.add(temp);
	    	}
			resultAmt = entrustAmt.subtract(wflPrinAmt);
		}else if(StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), ps)  
				|| StringUtil.equals(DictCodeList.交易方向_赎回.getValue(), ps)){
			List<BigDecimal> wflShareAmts = trdPlanCcyInvestTradeMapper.getFinShareAmt(param);
	    	BigDecimal wflShareAmt = BigDecimal.ZERO;
	    	for(BigDecimal temp: wflShareAmts) {
	    		wflShareAmt = wflShareAmt.add(temp);
	    	}
			resultAmt = entrustNum.subtract(wflShareAmt);
		}
		
		return resultAmt;
	}
}
