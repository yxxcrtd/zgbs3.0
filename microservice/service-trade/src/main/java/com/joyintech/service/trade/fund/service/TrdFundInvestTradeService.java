/*
 * 文件名：TrdFundInvestTradeService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：xiexinting 修改时间：2018年10月19日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.fund.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.joyintech.service.trade.common.service.TrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.fund.dao.TrdFundInvestTradeMapper;
import com.joyintech.service.trade.fund.dto.TrdFundInvestTradeDto;
import com.joyintech.service.trade.fund.dto.WorkFlowDto;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.RepTradeMapper;
import com.joyintech.tams.common.base.dao.StlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.StlSecAcctInfoMapper;
import com.joyintech.tams.common.base.dao.TrdDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.dao.TrdTradeFeeDetailMapper;
import com.joyintech.tams.common.base.dao.WflPaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.WflStlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.WflTrdDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.WflTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.RepTrade;
import com.joyintech.tams.common.base.entity.RepTradeKey;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.StlSecAcctInfo;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.WflPaTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflPaTrdProductDealKey;
import com.joyintech.tams.common.base.entity.WflStlPayOrders;
import com.joyintech.tams.common.base.entity.WflStlPayOrdersKey;
import com.joyintech.tams.common.base.entity.WflTrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.WflTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAdd;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAddKey;
import com.joyintech.tams.common.base.entity.WflTrdProductDealKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.component.business.common.BaseDealCommService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;

/**
 * 基金投资交易微服务端service 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年10月19日
 * @see TrdFundInvestTradeService
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdFundInvestTradeService extends BaseService<TrdFundInvestTradeDto> {

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
    private TrdFundInvestTradeMapper trdFundInvestTradeMapper;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    /**
     * 提供金融产品交易共通业务逻辑处理
     */
    @Autowired
    private BaseDealCommService baseDealCommServiceImpl;

    /**
     * 划款指令表
     */
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    // 调用投管接口 add by renping 20190205 end

    /**
     * 金融产品交易业务流程表
     */
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

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
    private PaTrdProductDealMapper paTrdProductDealMapper;

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

    // 场内基金改造 add by renping 20191117 start
    /**
     * StlSecAcctInfoMapper
     */
    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired
    private IPlatformDate platformDateImpl;
    // 场内基金改造 add by renping 20191117 end

    @Autowired(required = false)
    private TrdAvailableCashQueryService trdAvailableCashQueryService;
    /**
     * 分页查询
     * 
     * @param page
     * @param length
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<TrdFundInvestTradeDto> selectForPage(int page, int length, Map<String, Object> pageParam)
    {
        pageParam.put("VALID_STATUS_01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("VALID_STATUS_02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("VALID_STATUS_03", DictCodeListExt1.交易流程状态_撤销.getValue());
        pageParam.put("YES_NO_NO", DictCodeList.是否_否.getValue());
        pageParam.put("TRADESTATUS_NEW", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("TRADESTATUS_REVOKE", DictCodeListExt1.交易状态_撤销.getValue());
        pageParam.put("COMMON_BUSINESS_TYPE_TRD02", DictCodeListExt1.金融产品类型_基金.getValue());
        pageParam.put("PS24", DictCodeList.交易方向_赎回到账.getValue());
        // 场内基金改造 add by renping 20191117 start
        pageParam.put("PST19", DictCodeList.交易方向_赎回确认.getValue());
        pageParam.put("PST05", DictCodeList.交易方向_申购确认.getValue());
        pageParam.put("TRM_FUND_TYPE05", DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue());
        // 场内基金改造 add by renping 20191117 end
        List<String> psList = new ArrayList<>();
        if (StringUtil.isNotEmpty(pageParam.get("ps"))) {
            psList.add((String)pageParam.get("ps"));
        }
        else {
            psList.add(DictCodeList.交易方向_申购.getValue());
            psList.add(DictCodeList.交易方向_申购确认.getValue());
            psList.add(DictCodeList.交易方向_赎回.getValue());
            psList.add(DictCodeList.交易方向_赎回确认.getValue());
            psList.add(DictCodeList.交易方向_赎回到账.getValue());
            psList.add(DictCodeList.交易方向_买入.getValue());
            psList.add(DictCodeList.交易方向_卖出.getValue());
            psList.add(DictCodeListExt2.交易方向_转换申请.getValue());
            psList.add(DictCodeListExt2.交易方向_转换确认.getValue());
            // 场内基金改造 add by renping 20191117 start
            psList.add(DictCodeList.交易方向_认购确认.getValue());
            psList.add(DictCodeListExt2.交易方向_认购.getValue());
            // 场内基金改造 add by renping 20191117 end
        }
        pageParam.put("psList", psList);

        // add by etliu 20210501 银行间场内场外拆分 start
        pageParam.put("defaultTradeMarketList", PageMenuSplitUtil.getTradeMarketList((String) pageParam.get("pageId")));
        // add by etliu 20210501 银行间场内场外拆分 end

        Page<TrdFundInvestTradeDto> pageDto = this.queryForPage(page, length,
            () -> trdFundInvestTradeMapper.selectDeal(pageParam));
        List<TrdFundInvestTradeDto> listDto = pageDto.getRoot();
        for (TrdFundInvestTradeDto tfitDto : listDto) {
            // 场内基金改造 modify by renping 20191117 start
            if ((StringUtil.equals(tfitDto.getPs(), DictCodeListExt2.交易方向_认购.getValue())
                 || StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_申购.getValue())
                 // 场内基金改造 modify by renping 20191117 end
                 || StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_赎回.getValue())
                 || StringUtil.equals(tfitDto.getPs(), DictCodeListExt2.交易方向_转换申请.getValue())
                 || StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_赎回确认.getValue()))
                && StringUtil.equals(tfitDto.getValidStatus(), DictCodeListExt1.交易流程状态_有效.getValue())) {
                Map<String, Object> param = new HashMap<>();
                param.put("bTradeId", tfitDto.getTradeId());
                param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
                param.put("yes", DictCodeList.是否_是.getValue());
                int flg1 = trdFundInvestTradeMapper.selectConfirmInfo(param);
                int flg2 = trdFundInvestTradeMapper.selectWflConfirmInfo(tfitDto.getTradeId());
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
     * 资产代码联想下拉框
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
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
//        if (StringUtil.isNotEmpty(param)&&!StringUtil.isNotEmpty(param.get("tradeDateVdate"))){
//            param.put("tradeDateVdate", platformDateImpl.getSysDateStr());
//        }

        // add by etliu 20210501 银行间场内场外拆分 start
        param.put("defaultTradeMarketList", PageMenuSplitUtil.getTradeMarketList((String) param.get("parentPageId")));
        // add by etliu 20210501 银行间场内场外拆分 end

        return this.queryForPageForSelectReturn(page, length, () -> trdFundInvestTradeMapper.getFinprodId(param));
    }

    // 资产推荐方 新增 zhuyongkang 20191023 end
    /**
     * 构建推荐方下拉框
     *
     * @param param 参数
     * @return 下拉框查询结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetRecommandOrg(int page, int length, Map<String, Object> param) {
        param.put("counterType", DictCodeListExt1.交易对手类型_分行.getValue());
        return this.queryForPageForSelectReturn(page, length, () -> trdFundInvestTradeMapper.getAssetRecommandOrg(param));
    }

    // 资产推荐方 新增 zhuyongkang 20191023 end
    /**
     * 提交流程启动时调用方法
     * 
     * @param entity
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @see
     * @since 1.0
     */
    public void doInsert(TrdFundInvestTradeDto entity) {
        if (entity.getOperateType().equals("submit")) {
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                Map<String, Object> param = new HashMap<>();
                param.put("tradeId", entity.getbTradeId());
                if(entity.getTotleTradeId() != null && entity.getTotleTradeId().length() > 31){
                    param.put("totleTradeId", entity.getTotleTradeId().substring(0,32));//针对多组合赎回到账   通道交易
                }
                Map<String, Object> map = selectAccountTrdId(param);
                if (map == null) {
                    map = selectAccountTrdIdinwfl(entity.getbTradeId());
                }
                if (map != null) {
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
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
            // 调用授信共同 sjj end

            // 场内基金改造 modify by renping 20191117 start
            // <!-- 通道交易 新增 强成西 2019/03/26 start -->
            // if (DictCodeList.交易方向_买入.getValue().equals(entity.getPs())
            // || DictCodeList.交易方向_申购.getValue().equals(entity.getPs())) {
            // entity.setTradeAmt(ArithmeticUtil.doAdd(entity.getTradeAmt(), entity.getChannelAmt()));
            // }
            // else {
            // entity.setTradeAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getChannelAmt()));
            // }
            if (entity.getCollectType() != null
                && StringUtil.equals(entity.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())) {
                entity.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getFeeAmtPay(), entity.getChannelAmt()));
            }
            else if (StringUtil.isNotEmpty(entity.getChlId())) {
                entity.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmtUnpay(), entity.getChannelAmt()));
            }
            entity.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmtPay(), entity.getFeeAmtUnpay()));

            // <!-- 通道交易 新增 强成西 2019/03/26 end -->
            // 佣金
            if (entity.getIsPayWithTrade() != null
                && StringUtil.equals(entity.getIsPayWithTrade(), DictCodeList.是否_否.getValue())) {
                entity.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmtUnpay(), entity.getCommissionFee()));
            }
            else {
                entity.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getFeeAmtPay(), entity.getCommissionFee()));
            }
            entity.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getCommissionFee()));
            // 场内基金改造 modify by renping 20191117 end

            if (entity.getTrdFinProductDealTotal() != null) {
                WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, entity.getTrdFinProductDealTotal());
                wflTrdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
                wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
                wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

                if (entity.getDealTotalAdd() != null) {
                    WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, entity.getDealTotalAdd());
                    wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
                    wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
                }

                if (entity.getTrdProductDeal() != null) {
                    WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity.getTrdProductDeal());
                    if (StringUtils.isNotBlank(entity.getfTradeId())) {
                        wflTrdProductDeal.setfTradeId(entity.getfTradeId());
                    }
                    wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                    wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflTrdProductDealMapper.insert(wflTrdProductDeal);
                }
            }

            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
            if (StringUtils.isNotBlank(entity.getfTradeId())) {
                wflTrdProductDeal.setfTradeId(entity.getfTradeId());
            }
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            // if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue()) || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())) {
            // wflTrdProductDeal.setTradeAmt(null);
            // }
            wflTrdProductDealMapper.insert(wflTrdProductDeal);

            if (entity.getTrdProductDealAdds() != null) {
                entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                    WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trdProductDealAdd);
                    wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                    wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
                    wflTrdProductDealAdd.setRedCfmDate(entity.getRedCfmDate());
                    // 不是转换申购确认，设置推荐方
                    if (entity.getTrdProductDeal() == null || !wflTrdProductDealAdd.getTradeId().equals(entity.getTrdProductDeal().getTradeId())) {
                        // 推荐方 by zhuyongkang 20191023 start
                        wflTrdProductDealAdd.setAssetRecommandOrg(entity.getAssetRecommandOrg());
                        // 推荐方 by zhuyongkang 20191023 start
                    }
                    wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
                });
            }

            entity.getPaTrdProductDeals().forEach(paTrdProductDeal -> {
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
                wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
                // 划款指令
//                if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())
//                    || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                    if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                        wflStlPayOrdersMapper.insert(wflStlPayOrders);
                    }
//                }
            });

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
            if (!DictCodeListExt2.交易方向_转换申请.getValue().equals(entity.getPs()) && !DictCodeListExt2.交易方向_转换确认.getValue().equals(entity.getPs())) {
                List<BaseTradeProductDealDto> dtoList = baseTradeProductDealService.select(idList);
                for (BaseTradeProductDealDto btpdd : dtoList) {
                    处理交易表流程数据(entity, btpdd);
                }
            }
        }
    }

    private void 处理交易表流程数据(TrdFundInvestTradeDto entity, BaseTradeProductDealDto btpdd) {
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
        wtpda.setWorkflowId(entity.getWorkflowId());
        wtpda.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wtpda.setRedCfmDate(entity.getRedCfmDate());
        wtpda.setPortfolioId(entity.getActPortfolioId());
        // 推荐方 by zhuyongkang 20191023 start
        wtpda.setAssetRecommandOrg(entity.getAssetRecommandOrg());
        // 推荐方 by zhuyongkang 20191023 start
        BeanUtil.copyPropertiesIgnoreNull(wptpd, paTrdProductDealMapper.selectByPrimaryKey(entity.getTradeId()));
        wptpd.setTradeId(wtpd.getTradeId());
        wptpd.setWorkflowId(entity.getWorkflowId());
        wptpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        if (btpdd.getStlPayOrdersList() != null && btpdd.getStlPayOrdersList().size() > 0) {
            BeanUtil.copyPropertiesIgnoreNull(wspo, btpdd.getStlPayOrdersList().get(0));
            wspo.setTradeId(wtpd.getTradeId());
            wspo.setWorkflowId(entity.getWorkflowId());
            wspo.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wspo.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
            wflStlPayOrdersMapper.insert(wspo);
        }
        wflTrdProductDealMapper.insert(wtpd);
        wflTrdProductDealAddMapper.insert(wtpda);
        wflPaTrdProductDealMapper.insert(wptpd);
    }

    /**
     * 流程结束时调用方法
     * 
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public void doComplete(TrdFundInvestTradeDto entity) {
        if (entity.getOperateType().equals("submit")) {
            String tradeId = entity.getTradeId();
            // 删除业务流程表
            wflTrdFinProductDealTotalMapper.deleteByWorkflowId(entity.getWorkflowId());
            wflTrdDealTotalAddMapper.deleteByWorkflowId(entity.getWorkflowId());
            wflTrdProductDealMapper.deleteByWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAddMapper.deleteByWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDealMapper.deleteByWorkflowId(entity.getWorkflowId());
            if (!StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                || !StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                trdFundInvestTradeMapper.deleteWflStlPayOrders(tradeId);
            }
            // 插入组合交易表
            if (entity.getPtlTrade() != null && !entity.getPtlTrade().isEmpty()) {
                entity.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insert(ptlTrade));
            }

            if (entity.getTrdFinProductDealTotal() != null) {
                trdFinProductDealTotalMapper.insertSelective(entity.getTrdFinProductDealTotal());

                if (entity.getDealTotalAdd() != null) {
                    trdDealTotalAddMapper.insertSelective(entity.getDealTotalAdd());
                }

                if (entity.getTrdProductDeal() != null) {
                    trdProductDealMapper.insert(entity.getTrdProductDeal());
                }

                if (entity.getTrdTradeFeeDetailList() != null) {
                    entity.getTrdTradeFeeDetailList().forEach(trdTradeFeeDetail -> trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail));
                }

                if (mstFileCommonService != null) {
                    // 文件
                    MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
                    mstFileInputParameter.setFileList(entity.getFileArray());
                    mstFileInputParameter.setBussinessId(entity.getTotleTradeId());
                    mstFileCommonService.updateBusinessId(mstFileInputParameter);
                }

                trdProductDealMapper.insert(entity);

                entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                    trdProductDealAdd.setRedCfmDate(entity.getRedCfmDate());
                    // 不是转换申购确认，设置推荐方
                    if (entity.getTrdProductDeal() == null || !trdProductDealAdd.getTradeId().equals(entity.getTrdProductDeal().getTradeId())) {
                        // 推荐方 by zhuyongkang 20191023 start
                        trdProductDealAdd.setAssetRecommandOrg(entity.getAssetRecommandOrg());
                        // 推荐方 by zhuyongkang 20191023 start
                    }
                    trdProductDealAddMapper.insert(trdProductDealAdd);
                });
            }
            else {
                // 文件
                if (mstFileCommonService != null) {
                    MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
                    mstFileInputParameter.setFileList(entity.getFileArray());
                    mstFileInputParameter.setBussinessId(entity.getTradeId());
                    mstFileCommonService.updateBusinessId(mstFileInputParameter);
                }
            }

            // 插入金融产品交易扩展表
            entity.getPaTrdProductDeals().forEach(paTrdProductDeal -> {
                paTrdProductDealMapper.insert(paTrdProductDeal);
            });


            Map<String, Object> map = new HashMap<>();
            map.put("product", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
            map.put("portfolioId", entity.getPortfolioId());
            // 插入交易报送表
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_认购确认.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                String prodCode = trdFundInvestTradeMapper.selProdCode(map);
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
                    if (!StringUtil.equals(entity.getCcy(), DictCodeList.币种_人民币_CNY.getValue())) {
                        map.put("ccy", entity.getCcy());
                        map.put("cny", DictCodeList.币种_人民币_CNY.getValue());
                        map.put("tradDate", entity.getTradeDate());
                        BigDecimal num = trdFundInvestTradeMapper.selCloPrice(map);
                        if (num != null) {
                            cloPrice = num;
                        }
                    }
                    repentity.setRepMoneyCny(entity.getPrinAmt().multiply(cloPrice));
                    repentity.setRepCount(entity.getShareAmt());
                    repentity.setRepMoneyFlowType(entity.getPs().equals(DictCodeList.交易方向_认购确认.getValue())
                                                  || entity.getPs().equals(DictCodeList.交易方向_申购确认.getValue())
                                                  || entity.getPs().equals(
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
            trdFundInvestTradeMapper.updateRep(map);

            entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                // 一体化对应etliu start
                // 所有交易生效后调用的共通方法
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getActPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                if (entity.getTradeId().equals(trdProductDealAdd.getTradeId())) {
                    inputDto.setTrdProductDeal(entity);
                }
                else {
                    inputDto.setTrdProductDeal(entity.getTrdProductDeal());
                }
                // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                inputDto.setTrdProductDealAdd(trdProductDealAdd);
                //通道费用
                inputDto.setTrdTradeFeeDetail(entity.getTrdTradeFeeDetail());
                // <!-- 通道交易 新增 强成西 2019/03/26 end -->
                // 场内基金改造 add by renping 20191117 start
                if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                    inputDto.setStlPayOrders(entity.getBussTrdTransferOrder().get(0));
                }
                List<TrdTradeFeeDetail> TrdTradeFeeDetailList = new ArrayList<>();
               for(TrdTradeFeeDetail trdTradeFeeDetail:entity.getTrdTradeFeeDetailList()){
            	   //非通道费用
            	   if(!StringUtil.equals(DictCodeListExt1.一次性费用类型_通道费.getValue(), trdTradeFeeDetail.getFeeType())){
            		   TrdTradeFeeDetailList.add(trdTradeFeeDetail);
            	   }
               }
                
                inputDto.setListTrdTradeFeeDetail(TrdTradeFeeDetailList);
                // 场内基金改造 add by renping 20191117 end

                if (entity.getTrdFinProductDealTotal() == null ||
                        StringUtil.equals(entity.getPs(), DictCodeListExt2.交易方向_转换赎回申请.getValue())
                        || StringUtil.equals(entity.getPs(), DictCodeListExt2.交易方向_转换赎回确认.getValue())
                        || StringUtil.equals(entity.getPs(), DictCodeListExt2.交易方向_转换申购确认.getValue())
                ) {
                    // 反向交易增加 start by qiangchengxi 2019年3月12日
                    AllTradeCommonUtil.setDeals(inputDto, entity, entity.getTrdProductDealAdds().get(0), entity.getPaTrdProductDeals().get(0),
                            entity.getBussTrdTransferOrder(), entity.getPtlTrade(), entity.getActPortfolioId(),TrdTradeFeeDetailList);
                    // 反向交易增加 end by qiangchengxi 2019年3月12日
                }
                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    inputDto.setActPortfolioId(entity.getActPortfolioId());
                }
                if (entity.getBussTrdTransferOrder() != null && !entity.getBussTrdTransferOrder().isEmpty()) {
                    StlPayOrders order = new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(order, entity.getBussTrdTransferOrder().get(0));
                    inputDto.setStlPayOrders(order);
                }
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                // 一体化对应etliu end

                // 调用投管接口 add by renping 20190205 start
                if (!(StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(), entity.getType6())
                    && (StringUtil.equals(DictCodeListExt2.交易方向_认购.getValue(), entity.getPs())
                        || StringUtil.equals(DictCodeList.交易方向_申购.getValue(), entity.getPs())
                        || StringUtil.equals(DictCodeList.交易方向_赎回.getValue(), entity.getPs())))
                    && !(StringUtil.equals(DictCodeList.基金类资产类型_非货币基金.getValue(), entity.getType6())
                        && (StringUtil.equals(DictCodeList.交易方向_赎回.getValue(), entity.getPs())))
                    && !StringUtil.equals(DictCodeList.交易方向_赎回到账.getValue(), entity.getPs())) {
                    InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                    if (StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(),
                        entity.getType6())) {
                        investInterfCommDto.setPageId("TrdFundInvestTrade002.02");
                    }
                    else {
                        investInterfCommDto.setPageId("TrdFundInvestTrade002.01");
                    }
                    investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                    investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                    investInterfCommDto.setOtherTypeForConvert(entity.getType6());
                    investInterfCommDto.setTrdProductDeal(inputDto.getTrdProductDeal());
                    investInterfCommDto.setTrdProductDealAdd(inputDto.getTrdProductDealAdd());
                    investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                    investInterfCommDto.setTradeFeeDetaillist(inputDto.getListTrdTradeFeeDetail());
                    investInterfCommService.doInvestInterf(investInterfCommDto);
                }
                // 调用投管接口 add by renping 20190205 end

            });
        }
        else if (StringUtil.equals(entity.getOperateType(), "REVOKE")
                && !DictCodeListExt2.交易方向_转换申请.getValue().equals(entity.getPs())
                && !DictCodeListExt2.交易方向_转换确认.getValue().equals(entity.getPs())) {

            PaTrdProductDeal ptpd = paTrdProductDealMapper.selectByPrimaryKey(entity.getTradeId());
            if (ptpd != null) {
                ptpd.setTradeId(ptpd.getTradeId() + "R");
                paTrdProductDealMapper.insert(ptpd);
            }

            trdFundInvestTradeMapper.insertPtlTradeByBusiNo(entity.getTradeId(), entity.getUpdateUser());

            String Tid = entity.getTradeId();
            String workflowId = entity.getWorkflowId();
            String workflowStatus = DictCodeListExt1.画面数据状态_流程中.getValue();
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                Map<String, Object> accountId;
                accountId = selectAccountTrdIdinwfl(Tid);
                if (accountId == null) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("tradeId", Tid);
                    if(entity.getTotleTradeId() != null && entity.getTotleTradeId().length() > 31){
                        param.put("totleTradeId", entity.getTotleTradeId().substring(0,32));//针对多组合赎回到账   通道交易
                    }
                    accountId = selectAccountTrdId(param);
                }
                if (accountId != null) {
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
                    trdFundInvestTradeMapper.deleteWflStlPayOrders(accountTradId);
                }
            }
            trdFundInvestTradeMapper.deleteWflStlPayOrdersByWfl(workflowId);
            trdFundInvestTradeMapper.deleteWflTrdProductDealByWfl(workflowId);
            trdFundInvestTradeMapper.deleteWflTrdProductDealAddByWfl(workflowId);
            trdFundInvestTradeMapper.deleteWflPaTrdProductDealByWfl(workflowId);
            RepTradeKey repTradeKey = new RepTradeKey();
            repTradeKey.setRepBankTradeCode(Tid);
            repTradeKey.setRepMoneyFlowType(entity.getPs().equals(DictCodeList.交易方向_申购确认.getValue()) || entity.getPs().equals(
                DictCodeList.交易方向_买入.getValue()) ? DictCodeListExt2.资金流动类型_资产买入.getValue() : DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
            repTradeMapper.deleteByPrimaryKey(repTradeKey);
        }
    }

    /**
     * 代办中修改时流程
     * 
     * @param entity
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    public void doUpdate(TrdFundInvestTradeDto entity) {
        if (entity.getOperateType().equals("submit")) {
            this.doDelete(entity.getWorkflowId());
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                Map<String, Object> map = new HashMap<>();
                Map<String, Object> param = new HashMap<>();
                param.put("tradeId", entity.getbTradeId());
                map = selectAccountTrdId(param);
                if (map == null) {
                    map = selectAccountTrdIdinwfl(entity.getbTradeId());
                }
                if (map != null) {
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
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
            // 调用授信共同 sjj end

            if (entity.getTrdFinProductDealTotal() != null) {
                WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, entity.getTrdFinProductDealTotal());
                wflTrdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
                wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
                wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

                if (entity.getDealTotalAdd() != null) {
                    WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, entity.getDealTotalAdd());
                    wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
                    wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
                }
//               del by xr 只有公墓基金转换申请时total不为null，但若只有此时插入流程表会导致流程交易退回在插入查询不到
                //故修改成在total之后插入
//                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
//                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, TrdProductDeal);
//                if (StringUtils.isNotBlank(entity.getfTradeId())) {
//                    wflTrdProductDeal.setfTradeId(entity.getfTradeId());
//                }
//                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
//                wflTrdProductDealMapper.insert(wflTrdProductDeal);
            }

                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
                if (StringUtils.isNotBlank(entity.getfTradeId())) {
                    wflTrdProductDeal.setfTradeId(entity.getfTradeId());
                }
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                // if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue()) || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())) {
                // wflTrdProductDeal.setTradeAmt(null);
                // }
                wflTrdProductDealMapper.insert(wflTrdProductDeal);

            entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trdProductDealAdd);
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
                wflTrdProductDealAdd.setRedCfmDate(entity.getRedCfmDate());
                // 不是转换申购确认，设置推荐方
                if (entity.getTrdProductDeal() == null || !wflTrdProductDealAdd.getTradeId().equals(entity.getTrdProductDeal().getTradeId())) {
                    // 推荐方 by zhuyongkang 20191023 start
                    wflTrdProductDealAdd.setAssetRecommandOrg(entity.getAssetRecommandOrg());
                    // 推荐方 by zhuyongkang 20191023 start
                }
                wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
            });

            entity.getPaTrdProductDeals().forEach(paTrdProductDeal -> {
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
                wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
                // 划款指令
                if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())
                        || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())
                ||StringUtil.equals(entity.getPs(),DictCodeList.交易方向_认购.getValue())) {
                    if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                        wflStlPayOrdersMapper.insert(wflStlPayOrders);
                    }
                }
            });

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
                wtpda.setWorkflowId(entity.getWorkflowId());
                wtpda.setPortfolioId(entity.getActPortfolioId());
                wtpda.setRedCfmDate(entity.getRedCfmDate());
                // 推荐方 by zhuyongkang 20191023 start
                wtpda.setAssetRecommandOrg(entity.getAssetRecommandOrg());
                // 推荐方 by zhuyongkang 20191023 start
                wtpda.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());

                BeanUtil.copyPropertiesIgnoreNull(wptpd, paTrdProductDealMapper.selectByPrimaryKey(entity.getTradeId()));
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
    }

    /**
     * 流程作废时删除业务流程表
     * 
     * @param workflowId
     * @see
     * @since 1.0
     */
    public void doDelete(String workflowId) {
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdDealTotalAddMapper.deleteByWorkflowId(workflowId);
        trdFundInvestTradeMapper.deleteWflTrdProductDealByWfl(workflowId);
        trdFundInvestTradeMapper.deleteWflTrdProductDealAddByWfl(workflowId);
        trdFundInvestTradeMapper.deleteWflPaTrdProductDealByWfl(workflowId);
        trdFundInvestTradeMapper.deleteWflStlPayOrdersByWfl(workflowId);
    }

    /**
     * 插入业务流程交易到账撤销信息
     * 
     * @param entity TrdFundInvestTradeDto
     * @see
     * @since 1.0
     */
    public void revokeAccount(TrdFundInvestTradeDto entity) {
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
            wtpda.setWorkflowId(entity.getWorkflowId());
            // 推荐方 by zhuyongkang 20191023 start
            wtpda.setAssetRecommandOrg(entity.getAssetRecommandOrg());
            // 推荐方 by zhuyongkang 20191023 start
            wtpda.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wtpda.setRedCfmDate(entity.getRedCfmDate());
            BeanUtil.copyPropertiesIgnoreNull(wptpd, paTrdProductDealMapper.selectByPrimaryKey(entity.getTradeId()));
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
     * 查询组合可用现金
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
        Map<String, Object> result = new HashMap<>();
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        dto.setAcctType(acctTypeList);
        dto.setPortfolioId((String)param.get("portfolioId"));
        dto.setCcy((String)param.get("ccy"));
        BigDecimal cashT3 = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
        result.put("cashT3", cashT3);

        acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
        //查询新表可用现金
        PtlCashPositionFlowDto dtoNew = new PtlCashPositionFlowDto();
        BeanUtil.copyPropertiesIgnoreNull(dtoNew,dto);
        dtoNew.setCdate(vdate);
        dtoNew.setHodingType(DictCodeListExt3.资产类型_汇总可用现金.getValue());
        dtoNew.setDateType(0);
        BigDecimal cashT0_new = basePtlCashPositionInfoService.queryTrdAvailableCashNew(dtoNew);
        cashT = com.joyintech.calculation.util.ArithmeticUtil.doAdd(cashT, cashT0_new);
        result.put("cashT", cashT);
        result.put("cashT", cashT);
        if (StringUtil.isNotEmpty(param.get("calId"))) {
            PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
            Date vdate1 = DateUtil.parse((String)param.get("vdate1"));
            dto2.setCdate(vdate1);
            dto2.setAcctType(acctTypeList);
            dto2.setPortfolioId((String)param.get("portfolioId"));
            dto2.setCcy((String)param.get("ccy"));
            BigDecimal cashT1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto2);
            dtoNew.setDateType(1);
            BigDecimal cashT1_new = basePtlCashPositionInfoService.queryTrdAvailableCashNew(dtoNew);
            cashT1 = com.joyintech.calculation.util.ArithmeticUtil.doAdd(cashT1, cashT1_new);
            result.put("cashT1", cashT1);
        }

        // 通道交易 新增 强成西 2019/03/26 start
        if (StringUtil.isNotEmpty(param.get("chlId"))) {
            dto.setNeedAcctType(false);
            dto.setChlFinprodId((String)param.get("chlId"));
            dto.setAcctType(null);
            result.put("T_2", basePtlCashPositionInfoService.queryUsableCashPosition(dto));
        }
        // 通道交易 新增 强成西 2019/03/26 end

        // 场内基金改造 add by renping 20191117 start
        if (StringUtil.isNotEmpty(param.get("secAcctId"))) {
            BigDecimal cashS = BigDecimal.ZERO;
            // 查询账户可用现金
            StlSecAcctInfo secAcct = stlSecAcctInfoMapper.selectByPrimaryKey((String)param.get("secAcctId"));
            if (secAcct != null && StringUtil.isNotEmpty(secAcct.getTrdAcctId())) {
                dto.setAcctType(null);
                dto.setAcctId(secAcct.getTrdAcctId());
                cashS = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            }
            result.put("cashS", cashS);
        }
        // 场内基金改造 add by renping 20191117 end
        return result;
    }

    /**
     * 查询组合可用持仓
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPtlSecPosition(PtlSecPositionFlowDto dto) {
        if (basePtlSecPositionInfoService != null) {
            dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
            if (StringUtil.isNotEmpty(dto.getFinprodId())) {
                return basePtlSecPositionInfoService.queryUsableSecPosition(dto);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取交易dto
     * 
     * @param tradId
     * @return
     * @see
     * @since 1.0
     */
    public List<BaseTradeProductDealDto> getFinDto(String tradId) {
        List<String> tidList = new ArrayList<>();
        tidList.add(tradId);
        // 场内基金改造 add by renping 20191117 start
        // 实时申赎的时候，申购确认赎回确认赎回到账需要一起撤销
//        Map<String, Object> param = new HashMap<>();
//        param.put("bTradeId", tradId);
//        param.put("yes", DictCodeList.是否_是.getValue());
//        param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
//        param.put("TRM_FUND_TYPE05", DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue());
//        List<String> tradeList = trdFundInvestTradeMapper.selectFollowTradeInfo(param);
//        if (!StringUtil.isEmptyList(tradeList)) {
//            tidList.addAll(tradeList);
//        }
            // 场内基金改造 add by renping 20191117 end
        List<BaseTradeProductDealDto> result = baseTradeProductDealService.select(tidList);
        if (result == null || result.isEmpty()) {
            return baseTradeProductDealService.selectTotalDeal(tidList);
        }
        return result;
    }

    /**
     * 查看反显数据
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public TrdFundInvestTradeDto getTrdFundDto(Map<String, Object> map) {
        // 场内基金改造 add by renping 20191117 start
        map.put("FEE_TYPE_C", DictCodeListExt1.一次性费用类型_佣金.getValue());
        map.put("FEE_TYPE_B18", DictCodeListExt1.一次性费用类型_认购费.getValue());
        map.put("FEE_TYPE_B15", DictCodeListExt1.一次性费用类型_赎回费.getValue());
        map.put("FEE_TYPE_B14", DictCodeListExt1.一次性费用类型_申购费.getValue());
        map.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        map.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        // 场内基金改造 add by renping 20191117 end
        return trdFundInvestTradeMapper.getTrdFundDto(map);
    }

    /**
     * 查询业务流程表中交易到账数据
     * 
     * @param tradId
     * @return
     * @see
     * @since 1.0
     */
    public TrdFundInvestTradeDto getTrdFundDtoForAccount(String tradId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tradId", tradId);
        return trdFundInvestTradeMapper.getTrdFundDtoForAccount(map);
    }

    /**
     * 查询业务流程表中划款指令
     * 
     * @param bizCode
     * @return
     * @see
     * @since 1.0
     */
    public List<BussTrdTransferOrderBase> selectOrdersByBizCode(String bizCode) {
        return trdFundInvestTradeMapper.selectOrdersByBizCode(bizCode, DictCodeListExt1.收支方向_支出.getValue());
    }

    /**
     * 查询单位净值
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPrice(Map<String, Object> map) {
    	BigDecimal price = null;
    	if(StringUtil.isNotEmpty(map.get("tradeId"))) {
    		price = trdFundInvestTradeMapper.getPriceByTradeId(map.get("tradeId").toString());
    	}
    	if(price == null && StringUtil.isNotEmpty(map.get("workflowId"))) {
    		price = trdFundInvestTradeMapper.getPriceByTradeId(map.get("workflowId").toString());
    	}
    	if(price==null) {
    		price = trdFundInvestTradeMapper.getPrice(map);
    	}
        return price;
    }

    /**
     * 查询待结转份额
     * 
     * @param map
     * @return
     */
    public BigDecimal getcarryAmt(Map<String, Object> map) {
        //待结转份额 = （上日的头寸+ 最新公布日的前台计算的收益- 当天红利再投）的方式
        BigDecimal carryAmt = trdFundInvestTradeMapper.getcarryAmt(map);
        map.put("finprodType", "F06_");
        BigDecimal lastNewWfsy = trdFundInvestTradeMapper.getcarryAmtNew(map);
        BigDecimal carryAmtNew =BigDecimal.ZERO;
        if(StringUtil.isNotEmpty(map.get("shuhuishareAmt"))){
            carryAmtNew = new BigDecimal(map.get("shuhuishareAmt").toString());
            carryAmtNew = ArithmeticUtil.round(ArithmeticUtil.multiply(carryAmtNew,lastNewWfsy==null?BigDecimal.ZERO:lastNewWfsy),2, RoundingMode.HALF_UP);
        }
        if(carryAmt != null){
            carryAmt = carryAmt.add(carryAmtNew);
        }else{
            carryAmt = BigDecimal.ZERO;
        }
        //待结转份额若小于0，则按0处理
        if(carryAmt.compareTo(BigDecimal.ZERO) <= 0){
            carryAmt =BigDecimal.ZERO;
        }
        return carryAmt;
    }

    /**
     * 查询投资委托DTO
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
                    已完成额度 = ArithmeticUtil.doAdd(已完成额度, etrFinishStatus.getFinishAmt());
                }

                resultDto.getEtrEntrust().setEntrustAmt(
                    ArithmeticUtil.subtract(resultDto.getEtrEntrust().getEntrustAmt(), 已完成额度));
            }
        }
        return resultDto;
    }

    /**
     * 查询资产代码的交易方式
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getPurSpeed(Map<String, Object> param) {
        return trdFundInvestTradeMapper.getPurSpeed(param);
    }

    /**
     * 插入交易到账信息
     * 
     * @param entity
     * @see
     * @since 1.0
     */
    public void doInsertRedemption(TrdFundInvestTradeDto entity) {
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflTrdProductDeal.setWorkflowId(null);
        wflTrdProductDealMapper.insert(wflTrdProductDeal);
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdds().get(0));
        wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
        wflTrdProductDealAdd.setRedCfmDate(entity.getRedCfmDate());
        // 推荐方 by zhuyongkang 20191023 start
        wflTrdProductDealAdd.setAssetRecommandOrg(entity.getAssetRecommandOrg());
        // 推荐方 by zhuyongkang 20191023 start
        wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeals().get(0));
        wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
        if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
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
     * 查询到账交易是否托管匹配
     * 
     * @param tradeId
     * @return
     * @see
     * @since 1.0
     */
    public Integer selRedemption(String tradeId) {
        return trdFundInvestTradeMapper.selRedemption(tradeId);
    }

    /**
     * 查询撤销数据是否有后继交易
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public boolean revokeCheck(TrdFundInvestTradeDto dto) {
        Map<String, Object> param = new HashMap<>();
        param.put("bTradeId", dto.getTradeId());
        param.put("yes", DictCodeList.是否_是.getValue());
        param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
        // add 赎回到账交易只需要check流程中数据 20191031 start
        int result = 0;
        String ps = dto.getPs();
        if (!StringUtil.equals(ps, DictCodeList.交易方向_赎回确认.getValue())) {
            result = trdFundInvestTradeMapper.selectConfirmInfo(param);
        }
        // add 赎回到账交易只需要check流程中数据 20191031 end
        //add by yzg 20200424 赎回确认不验证赎回到账 start
        if(StringUtil.equals(ps, DictCodeList.交易方向_赎回确认.getValue())) {
        	param.put("ps", DictCodeList.交易方向_赎回到账.getValue());
        }
        //add by yzg 20200424 赎回确认不验证赎回到账 end
        int result2 = trdFundInvestTradeMapper.selectConfirmInfoWfl(param);
        return result + result2 == 0 ? true : false;
    }

    /**
     * 根据赎回确认交易编号查询赎回到账交易编号
     * 
     * @param map 参数
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> selectAccountTrdId(Map<String, Object> map) {
        return trdFundInvestTradeMapper.selectAccountTrdId(map);
    }

    /**
     * 根据赎回确认交易编号查询赎回到账交易编号
     * 
     * @param tradId
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> selectAccountTrdIdinwfl(String tradId) {
        return trdFundInvestTradeMapper.selectAccountTrdIdinwfl(tradId);
    }

    /**
     * 查询委托可用金额
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> selectEntrustAmt(Map<String, Object> map) {
        map.put("pssg", DictCodeList.交易方向_申购.getValue());
        map.put("pssh", DictCodeList.交易方向_赎回.getValue());
        List<TrdFundInvestTradeDto> dtoList = trdFundInvestTradeMapper.selectEntrustAmt(map);
        Map<String, Object> result = new HashMap<>();
        BigDecimal finishAmt = BigDecimal.ZERO;
        BigDecimal prinAmt = BigDecimal.ZERO;
        for (TrdFundInvestTradeDto dto : dtoList) {
            result.put("ENTRUST_AMT", dto.getEntrustAmt());
            result.put("ENTRUST_NUM", dto.getEntrustNum());
            finishAmt = ArithmeticUtil.doAdd(finishAmt, dto.getFinishAmt());
            prinAmt = ArithmeticUtil.doAdd(prinAmt, dto.getPrinAmt());
        }
        result.put("FINISH_AMT", finishAmt);
        result.put("PRIN_AMT", prinAmt);
        return result;
    }

    /**
     * 查询资产管理人
     * 
     * @return
     * @see
     * @since 1.0
     */
    public String selectManeger(String issuer) {
        if (StringUtil.isNotEmpty(issuer)) {
            return trdFundInvestTradeMapper.selectManeger(issuer);
        }
        else {
            return null;
        }
    }
    
    /**
     * 通过资产代码查询资产管理人
     * 
     * @return
     * @see
     * @since 1.0
     */
	public String selectFManeger(String finprodId) {
		if(StringUtil.isNotEmpty(finprodId)){
			return trdFundInvestTradeMapper.selectFManeger(finprodId);
		}
		else{
			return null;
		}
	}
    /**
     * 查询是否有后续交易新建流程
     * 
     * @return
     * @see
     * @since 1.0
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    public List<WorkFlowDto> selectTradeAfter(Map<String, Object> param) {
        return trdFundInvestTradeMapper.selectTradeAfter(param);
    }

    /**
     * 投管接口（到账确认信息输入接口）
     * 
     * @param entity 参数
     * @return 处理状态
     * @see
     * @since 1.0
     */
    public String do赎回确认到账生成(TrdFundInvestTradeDto entity) {
        // 查询是否有流程中的赎回确认交易
        Map<String, Object> param = new HashMap<>();
        param.put("psT24", DictCodeList.交易方向_赎回到账.getValue());
        param.put("tradeId", entity.getTradeId());
        TrdFundInvestTradeDto dto = trdFundInvestTradeMapper.selectWflDealOfRecv(param);
        if (dto != null) {
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            baseTradeProductDealDtos.add(baseTradeProductDealDto);

            // 交易表
            WflTrdProductDealKey dealKey = new WflTrdProductDealKey();
            dealKey.setTradeId(dto.getTradeId());
            dealKey.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            WflTrdProductDeal wflTrdProductDeal = wflTrdProductDealMapper.selectByPrimaryKey(
                dealKey);
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, wflTrdProductDeal);
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdProductDeal.setStatus(DictCodeList.数据状态_新建.getValue());
            trdProductDealMapper.insert(trdProductDeal);
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
            
            // 交易附加表
            WflTrdProductDealAddKey addKey = new WflTrdProductDealAddKey();
            BeanUtil.copyPropertiesIgnoreNull(addKey, dealKey);
            WflTrdProductDealAdd wflTrdProductDealAdd = wflTrdProductDealAddMapper.selectByPrimaryKey(
                addKey);
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, wflTrdProductDealAdd);
            trdProductDealAdd.setStatus(DictCodeList.数据状态_新建.getValue());
            dto.setActPortfolioId(trdProductDealAdd.getPortfolioId());
            dto.setPortfolioId(
                AllTradeCommonUtil.exchangePortfolio(trdProductDealAdd.getPortfolioId()));
            trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
            trdProductDealAddMapper.insert(trdProductDealAdd);
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
            
            // 组合交易表
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            PtlTrade ptlTrade = new PtlTrade();
            ptlTrade.setPortfolioId(dto.getPortfolioId());
            ptlTrade.setBusiNo(dto.getTradeId());
            ptlTrade.setBusiId(dto.getFinprodId());
            ptlTradeList.add(ptlTrade);
            ptlTradeMapper.insert(ptlTrade);
            if (!StringUtil.equals(dto.getActPortfolioId(), dto.getPortfolioId())) {
                ptlTrade = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(ptlTrade, ptlTradeList.get(0));
                ptlTrade.setPortfolioId(dto.getActPortfolioId());
                ptlTradeList.add(ptlTrade);
                ptlTradeMapper.insert(ptlTrade);
            }
            
            // 指令表
            List<WflStlPayOrders> wflStlPayOrdersList = trdFundInvestTradeMapper.selectOrdersByTradeId(dto.getTradeId());
            List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
            if (!StringUtil.isEmptyList(wflStlPayOrdersList)) {
                for (WflStlPayOrders item : wflStlPayOrdersList) {
                    StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, item);
                    stlPayOrdersList.add(stlPayOrdersDto);
                    stlPayOrdersMapper.insert(stlPayOrdersDto);
                    wflStlPayOrdersMapper.deleteByPrimaryKey(item);
                }
            }
            baseTradeProductDealDto.setStlPayOrdersList(stlPayOrdersList);
            
            // 扩展表
            WflPaTrdProductDealKey paKey = new WflPaTrdProductDealKey();
            BeanUtil.copyPropertiesIgnoreNull(paKey, dealKey);
            WflPaTrdProductDeal wflPaTrdProductDeal = wflPaTrdProductDealMapper.selectByPrimaryKey(
                paKey);
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, wflPaTrdProductDeal);
            paTrdProductDealMapper.insert(paTrdProductDeal);

            // 金融产品交易共通业务逻辑处理:登录持仓流水与现金流水等
            finProductTradeDto.setNeedSum(true);
            baseDealCommServiceImpl.execute(finProductTradeDto);

            if (!StringUtil.equals(dto.getActPortfolioId(), dto.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getActPortfolioId()));
                baseDealCommServiceImpl.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getPortfolioId()));
            }

            wflTrdProductDealMapper.deleteByPrimaryKey(dealKey);
            wflTrdProductDealAddMapper.deleteByPrimaryKey(addKey);
            wflPaTrdProductDealMapper.deleteByPrimaryKey(paKey);
        }
        return DictCodeList.是否_是.getValue();
    }

    /**
     * 确认页面组合分配列表查询
     *
     * @param param 参数
     * @return 处理状态
     * @see
     * @since 1.0
     */
    public List<BussTrdTransfenpeiDto> selectTrdTransfenpeiList(Map<String, Object> param) {

        param.put("FEE_TYPE_C", DictCodeListExt1.一次性费用类型_佣金.getValue());
        param.put("FEE_TYPE_B18", DictCodeListExt1.一次性费用类型_认购费.getValue());
        param.put("FEE_TYPE_B15", DictCodeListExt1.一次性费用类型_赎回费.getValue());
        param.put("FEE_TYPE_B14", DictCodeListExt1.一次性费用类型_申购费.getValue());
        param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        param.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        List<BussTrdTransfenpeiDto> dtoList = trdFundInvestTradeMapper.selectTrdTransfenpeiList(param);
        String ps = (String)param.get("ps");
        BigDecimal unitCprice = new BigDecimal((String)param.get("unitCprice"));
        for(BussTrdTransfenpeiDto feipeiDto : dtoList){
            PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
            Date vdate = DateUtil.parse((String)param.get("vdate"));
            dto.setCdate(vdate);
            List<String> acctTypeList = new ArrayList<>();
            acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
            dto.setAcctType(acctTypeList);
            dto.setPortfolioId(feipeiDto.getPortfolioId());
            dto.setCcy((String)param.get("ccy"));
            BigDecimal cashT3 = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            feipeiDto.setTgSurpluscash(cashT3);//托管户剩余现金

            acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            feipeiDto.setBussTrdAvailableCashQueryavalibleCash(cashT);//组合T日可用现金

            if (StringUtil.isNotEmpty(param.get("calId"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date vdate1 = DateUtil.parse((String)param.get("vdate1"));
                dto2.setCdate(vdate1);
                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId(feipeiDto.getPortfolioId());
                dto2.setCcy((String)param.get("ccy"));
                BigDecimal cashT1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto2);
                feipeiDto.setBussTrdAvailableCashQueryavalibleCash1(cashT1);//组合T+1日可用现金
            }

            // 通道交易 新增 强成西 2019/03/26 start
            if (StringUtil.isNotEmpty(param.get("chlId"))) {
                dto.setNeedAcctType(false);
                dto.setChlFinprodId((String)param.get("chlId"));
                dto.setAcctType(null);
                feipeiDto.setChannelAvailableCash(basePtlCashPositionInfoService.queryUsableCashPosition(dto));//通道可用现金
            }
            // 通道交易 新增 强成西 2019/03/26 end

            // 场内基金改造 add by renping 20191117 start
            if (StringUtil.isNotEmpty(param.get("secAcctId"))) {
                BigDecimal cashS = BigDecimal.ZERO;
                // 查询账户可用现金
                StlSecAcctInfo secAcct = stlSecAcctInfoMapper.selectByPrimaryKey((String)param.get("secAcctId"));
                if (secAcct != null && StringUtil.isNotEmpty(secAcct.getTrdAcctId())) {
                    dto.setAcctType(null);
                    dto.setAcctId(secAcct.getTrdAcctId());
                    cashS = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
                }
                feipeiDto.setTradeMarketAccAvailableCash(cashS);//交易所资金账户可用现金
            }
            // 场内基金改造 add by renping 20191117 end
            if(StringUtil.equals(ps, DictCodeList.交易方向_申购确认.getValue())){
                feipeiDto.setShareAmt(com.joyintech.framework.util.ArithmeticUtil.divide(feipeiDto.getTradeAmt(), unitCprice, 2));
            }else if(StringUtil.equals(ps, DictCodeList.交易方向_赎回确认.getValue())){
                feipeiDto.setTradeAmt(com.joyintech.framework.util.ArithmeticUtil.multiply(feipeiDto.getShareAmt(), unitCprice));
            }

            //组合可用现金
            PtlSecPositionFlowDto flowDto = new PtlSecPositionFlowDto();
            BigDecimal assetAvailablePosition = BigDecimal.ZERO;
            if (basePtlSecPositionInfoService != null) {
                flowDto.setPortfolioId(feipeiDto.getPortfolioId());
                flowDto.setFinprodId((String)param.get("finprodId"));
                flowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
                flowDto.setCdate(DateUtil.parse((String)param.get("vdate")));
                if(StringUtil.isNotEmpty(param.get("secAcctId"))){
                    flowDto.setSecAcctId((String)param.get("secAcctId"));
                }else{
                    flowDto.setSecAcctId((String)param.get("chlId"));
                }
                if (StringUtil.isNotEmpty(param.get("finprodId"))) {
                    assetAvailablePosition =  basePtlSecPositionInfoService.queryUsableSecPosition(flowDto);
                }
            }
            feipeiDto.setAssetsavailablepositions(assetAvailablePosition);

            if (!StringUtil.isNotEmpty(feipeiDto.getbTradeId())) {
                feipeiDto.setbTradeId(feipeiDto.getTradeId());
            }
        }

        return dtoList;
    }

    public Map<String, Object> getSpeedByFinprodId(Map<String, Object> param) {
        // 场内基金改造 add by renping 20191117 end
        return trdFundInvestTradeMapper.getSpeedByFinprodId(param);
    }

}
