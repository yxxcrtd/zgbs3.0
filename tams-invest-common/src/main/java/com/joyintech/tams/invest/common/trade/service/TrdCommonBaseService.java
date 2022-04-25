/*
 * 文件名：TrdCommonBaseService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.trade.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.TrdCommonParam;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.dto.mock.trade.MockTradeInfoDto;
import com.joyintech.tams.invest.common.dto.mock.trade.MockTrdProductDealDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrade;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.trade.dto.OrderInfoDto;
import com.joyintech.tams.invest.common.trade.dto.PortfolioCalculateDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.TrdCommonBaseMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 交易基础组件调用Service
 *
 * @author 刘二通
 * @version 1.0 2018年11月2日
 * @see TrdCommonBaseService
 * @since 1.0
 */
@Service
public class TrdCommonBaseService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(TrdCommonBaseService.class);

    /**
     * TrdCommonBaseMapper
     */
    @Autowired
    private TrdCommonBaseMapper trdCommonBaseMapper;
    @Autowired
    private FinCashInfoMapper finCashInfoMapper;
    @Autowired
    private FinCashPayRuleMapper finCashPayRuleMapper;
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;

    @Autowired
    private StlFinProductAcctMapper stlFinProductAcctMapper;
    @Autowired
    private PrdSaleParamMapper prdSaleParamMapper;
    @Autowired
    private FinProductAddMapper finProductAddMapper;
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;
    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;
    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
    @Autowired
    private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;
    @Autowired
    private RepDurationMapper repDurationMapper;
    /**
     * 汇总流程附加表
     */
    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    /**
     * 汇总附加表
     */
    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
    /**
     * 实物交易表
     */
    @Autowired
    private TrdFinProductBarterMapper trdFinProductBarterMapper;
    /**
     * 汇总实物交易表
     */
    @Autowired
    private TrdFinProductBarterTotalMapper trdFinProductBarterTotalMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;
    /**
     * 查询产品登记编码
     *
     * @param portfolioId 组合代码
     * @return 产品登记编码
     * @see
     * @since 1.0
     */
    public String selectProdRegistCode(String portfolioId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("FINPROD_TYPE_TG_F16", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        paramMap.put("portfolioId", portfolioId);

        return trdCommonBaseMapper.selectProdRegistCode(paramMap);
    }

    /**
     * 根据工作流代码查询金融产品交易附属业务流程
     *
     * @param workflowId 工作流代码
     * @return 金融产品交易附属业务流程信息
     * @see
     * @since 1.0
     */
    public WflTrdProductDealAdd selectWflTrdProductDealAdd(String workflowId) {
        List<WflTrdProductDealAdd> dataList = trdCommonBaseMapper.selectWflTrdProductDealAdd(
                workflowId);
        if (!StringUtil.isEmptyList(dataList)) {
            return dataList.get(0);
        }
        return null;
    }

    /**
     * 根据工作流代码查询指令编号列表
     *
     * @param workflowId 工作流代码
     * @return 指令编号列表
     * @see
     * @since 1.0
     */
    public List<WflStlPayOrders> selectOrderIdList(String workflowId) {
        return trdCommonBaseMapper.selectOrderIdList(workflowId);
    }

    /**
     * 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
     *
     * @param finprodId 金融产品代码
     * @return 成功/失败
     * @see
     * @since 1.0
     */
    public boolean updateRepAssetDebt(String finprodId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        paramMap.put("finprodId", finprodId);
        trdCommonBaseMapper.updateRepAssetDebt(paramMap);

        return true;
    }

    /**
     * 检索已质押标准券额度
     *
     * @param portfolioId  组合代码
     * @param secAcctId    证券账户
     * @param finProdId    金融产品代码（债券代码）
     * @param searchDate   检索日期（yyyy-MM-dd）
     * @param isWithInvAim 检索项目是否包含检索投资目的
     * @return 已质押标准券信息Dto列表
     * @see
     * @since 1.0
     */
    public List<TrdStaSecInfoDto> selectPledgeStandardPrinAmt(String portfolioId, String secAcctId, String finProdId, String searchDate, Boolean isWithInvAim) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("portfolioId", portfolioId);
        paramMap.put("secAcctId", secAcctId);
        paramMap.put("finProdId", finProdId);
        paramMap.put("searchDate", searchDate);
        paramMap.put("isWithInvAim", isWithInvAim);

        paramMap.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        paramMap.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        paramMap.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());

        paramMap.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
        paramMap.put("TARGET_BACK_TYPE_H02", DictCodeList.回购类型_质押式竞价回购.getValue());

        paramMap.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());

        return trdCommonBaseMapper.selectPledgeStandardPrinAmt(paramMap);
    }
    

	/**
     * 根据工作流代码查询金融产品交易附属业务流程
     * @param workflowId 工作流代码
     * @return 金融产品交易附属业务流程信息
     * @see
     * @since 1.0
     */
    public AllTradeCommonInputDto selectWflTrdProductDeal(String workflowId) {
    	Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("workflowId", workflowId);
        paramMap.put("finprodtype", DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue());
        List<AllTradeCommonInputDto> dataList = trdCommonBaseMapper.selectWflTrdProductDeal(paramMap);
        if (!StringUtil.isEmptyList(dataList)) {
            return dataList.get(0);
        }
        return null;
    }

    // 2.0自动生成反向交易 bug修改 start
    /**
     * 查询系统参数
     *
     * @param systemCode 系统代码
     * @param paraCode 参数code
     *
     * @return 系统参数
     * @param systemCode
     * @param paraCode
     */
    public String selectParam(String systemCode, String paraCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("systemCode", systemCode);
        param.put("paraCode", paraCode);
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        String flag = trdCommonBaseMapper.selectParam(
                param);
        DynamicDataSourceHolder.clearDataSource();
        return flag;
    }
    // 2.0自动生成反向交易 bug修改 end

    /**
     * 指令接收交易录单根据委托代码查询流程中已录入金额
     * @param map 查询参数
     * @return BigDecimal
     * @author wangguiqi
     * @since 1.0
     */
    public BigDecimal queryWflPrinAmt(Map<String,Object> map) {
    	String finProductType = (String) map.get("type");
    	String entrustId = (String) map.get("entrustId");
    	if(StringUtil.isEmpty(finProductType)||StringUtil.isEmpty(entrustId)){
    		return BigDecimal.ZERO;
    	}
    	if(StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(), finProductType)
    			||StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), finProductType)
    			||StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), finProductType)
    				){
    		//债券类流程取用shareAmt
    		map.put("shareAmt", "shareAmt");
    	}
    	map.put("TRADE_STATUS", DictCodeListExt1.交易状态_初始.getValue());
    	map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
    	ArrayList<BigDecimal> wflPrinAmts = trdCommonBaseMapper.queryWflPrinAmt(map);
    	BigDecimal wflPrinAmt = BigDecimal.ZERO;
    	for(BigDecimal temp: wflPrinAmts) {
    	    if(StringUtil.isNotEmpty(temp)){
                wflPrinAmt = wflPrinAmt.add(temp);
            }
    	}
		return wflPrinAmt;
	};
    /**
     * 指令接收交易录单根据委托代码查询委托金额
     * @param entrustId String
     * @return BigDecimal
     * @author wangguiqi
     * @since 1.0
     */
    public BigDecimal queryEntrustAmtByEntrustId(String entrustId) {
    	BigDecimal wflPrinAmt = trdCommonBaseMapper.queryEntrustAmtByEntrustId(entrustId);
		return wflPrinAmt;
	};

    /**
     * 入库所有的属性
     * @param trdCommonWflParam 参数
     */
    public void insertAutoClassifyBatchWfl(TrdCommonWflParam trdCommonWflParam) {
        if(trdCommonWflParam != null){
//            划款指令
            if(trdCommonWflParam.getWflStlPayOrdersDtos()!= null){
                trdCommonWflParam.getWflStlPayOrdersDtos().forEach(item->wflStlPayOrdersMapper.insert(item));
            }
//            交易扩展
            if(trdCommonWflParam.getWflDealAdds()!= null){
                trdCommonWflParam.getWflDealAdds().forEach(item->wflTrdProductDealAddMapper.insert(item));
            }
//            交易附属
            if(trdCommonWflParam.getWflPaDeals()!= null){
                trdCommonWflParam.getWflPaDeals().forEach(item->wflPaTrdProductDealMapper.insert(item));
            }
//            交易
            if(trdCommonWflParam.getWflTrdProductDeals()!= null){
                trdCommonWflParam.getWflTrdProductDeals().forEach(item->wflTrdProductDealMapper.insert(item));
            }
//            交易汇总
            if(trdCommonWflParam.getWflTrdFinProductDealTotal()!= null){
                wflTrdFinProductDealTotalMapper.insert(trdCommonWflParam.getWflTrdFinProductDealTotal());
            }
//            交易汇总附属
            if(trdCommonWflParam.getWflPaTrdFinDealTotal()!= null){
                wflPaTrdFinDealTotalMapper.insert(trdCommonWflParam.getWflPaTrdFinDealTotal());
            }
//            交易其他
            if(trdCommonWflParam.getWflTrdProductDealOth()!=null){
                trdCommonWflParam.getWflTrdProductDealOth().forEach(item->wflTrdProductDealOthMapper.insert(item));
            }
        }
    }

    /**
     * 入库所有的属性（流程中）
     * @param trdCommonParam 参数
     */
    public void insertAutoClassifyBatch(TrdCommonParam trdCommonParam) {
        if(trdCommonParam != null){
//            划款指令
            if(trdCommonParam.getStlPayOrdersDtos()!= null){
                trdCommonParam.getStlPayOrdersDtos().forEach(item->stlPayOrdersMapper.insert(item));
            }
//            交易扩展
            if(trdCommonParam.getDealAdds()!= null){
                trdCommonParam.getDealAdds().forEach(item-> trdProductDealAddMapper.insert(item));
            }
//            交易附属
            if(trdCommonParam.getPaDeals()!= null){
                trdCommonParam.getPaDeals().forEach(item->paTrdProductDealMapper.insert(item));
            }
//            交易
            if(trdCommonParam.getTrdProductDeals()!= null){
                trdCommonParam.getTrdProductDeals().forEach(item->trdProductDealMapper.insert(item));
            }
//            组合交易
            if(trdCommonParam.getPtlTrades()!= null){
                trdCommonParam.getPtlTrades().forEach(item->ptlTradeMapper.insert(item));
            }
//            交易汇总
            if(trdCommonParam.getTrdFinProductDealTotal()!= null){
                trdFinProductDealTotalMapper.insert(trdCommonParam.getTrdFinProductDealTotal());
            }
//            交易汇总附属
            if(trdCommonParam.getPaTrdFinProductDealTotal()!= null){
                paTrdFinProductDealTotalMapper.insert(trdCommonParam.getPaTrdFinProductDealTotal());
            }
//            产品类型
//            if(trdCommonParam.getFinProductType()!=null){
//                finProductTypeMapper.insert(trdCommonParam.getFinProductType());
//            }
//            交易类产品类型
            if(trdCommonParam.getFinProductType()!=null){
                finProductTypeMapper.insert(trdCommonParam.getFinProductType());
            }
//            交易类金融产品
            if(trdCommonParam.getFinTradeProduct()!=null){
                finTradeProductMapper.insert(trdCommonParam.getFinTradeProduct());
            }
//            交易类金融产品附加
            if(trdCommonParam.getFinTradeProductAdd()!=null){
                finTradeProductAddMapper.insert(trdCommonParam.getFinTradeProductAdd());
            }
            //            账户
            if(trdCommonParam.getStlFinProductAccts()!=null){
                trdCommonParam.getStlFinProductAccts().forEach(item->stlFinProductAcctMapper.insert(item));
            }
//            费用
            if(trdCommonParam.getTrdTradeFeeDetails()!=null){
                trdCommonParam.getTrdTradeFeeDetails().forEach(item->trdTradeFeeDetailMapper.insert(item));
            }
        }
    }
    public String doInsert(BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtils.copyProperties(baseTotalTradeInfoDto.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal);
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflTrdFinProductDealTotalMapper.insert(wflTrdFinProductDealTotal);

        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtils.copyProperties(baseTotalTradeInfoDto.getPaTrdFinProductDealTotal(), wflPaTrdFinDealTotal);
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflPaTrdFinDealTotalMapper.insert(wflPaTrdFinDealTotal);

        if(baseTotalTradeInfoDto.getTrdDealTotalAdd()!=null){
            WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
            BeanUtils.copyProperties(baseTotalTradeInfoDto.getTrdDealTotalAdd(),wflTrdDealTotalAdd);
            wflTrdDealTotalAdd.setWorkflowId(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId());
            wflTrdDealTotalAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdDealTotalAddMapper.insert(wflTrdDealTotalAdd);
        }

        baseTotalTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
            insertBaseTotalTadeInfo(baseTradeInfoDto,baseTotalTradeInfoDto.isNeedStlPayOrders(), wflTrdFinProductDealTotal.getWorkflowId());
            if(baseTradeInfoDto.getBaseTradeInfoDtoList()!= null && baseTradeInfoDto.getBaseTradeInfoDtoList().size()>0 ){
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto->{
                    insertBaseTotalTadeInfo(subBaseTradeInfoDto,false, wflTrdFinProductDealTotal.getWorkflowId());
                });
            }
        });
        return DictCodeListExt1.交易流程状态_流程中.getValue();
    }
    private void insertBaseTotalTadeInfo(BaseTradeInfoDto baseTradeInfoDto, boolean isNeedStlPayOrders, String workflowId){
        if (baseTradeInfoDto.getTrdFinProductDealTotal() != null) {
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal1 = new WflTrdFinProductDealTotal();
            BeanUtils.copyProperties(baseTradeInfoDto.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal1);
            wflTrdFinProductDealTotal1.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdFinProductDealTotal1.setWorkflowId(workflowId);
            wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal1);
        }
        if (baseTradeInfoDto.getPaTrdFinProductDealTotal() != null) {
            WflPaTrdFinDealTotal wflPaTrdFinDealTotal1 = new WflPaTrdFinDealTotal();
            BeanUtils.copyProperties(baseTradeInfoDto.getPaTrdFinProductDealTotal(), wflPaTrdFinDealTotal1);
            wflPaTrdFinDealTotal1.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflPaTrdFinDealTotal1.setWorkflowId(workflowId);
            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal1);
        }

        if (baseTradeInfoDto.getTrdProductDeal() != null) {
            WflTrdProductDeal deal = new WflTrdProductDeal();
            BeanUtils.copyProperties(baseTradeInfoDto.getTrdProductDeal(), deal);
            deal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            deal.setWorkflowId(workflowId);
            wflTrdProductDealMapper.insertSelective(deal);
        }

        if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtils.copyProperties(baseTradeInfoDto.getPaTrdProductDeal(), wflPaTrdProductDeal);
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflPaTrdProductDeal.setWorkflowId(workflowId);
            wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
        }

        if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtils.copyProperties(baseTradeInfoDto.getTrdProductDealAdd(), wflTrdProductDealAdd);
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdProductDealAdd.setWorkflowId(workflowId);
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        }

        if (isNeedStlPayOrders && baseTradeInfoDto.getStlPayOrders() != null) {
            WflStlPayOrders orders = new WflStlPayOrders();
            BeanUtils.copyProperties(baseTradeInfoDto.getStlPayOrders(), orders);
            orders.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            orders.setWorkflowId(workflowId);
            wflStlPayOrdersMapper.insertSelective(orders);
        }

        if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
            WflTrdProductDeal deal1 = new WflTrdProductDeal();
            BeanUtils.copyProperties(baseTradeInfoDto.getBaseTrade().getTrdProductDeal(), deal1);
            deal1.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            deal1.setWorkflowId(workflowId);
            wflTrdProductDealMapper.insertSelective(deal1);
        }

        if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal() != null) {
            WflPaTrdProductDeal deal1 = new WflPaTrdProductDeal();
            BeanUtils.copyProperties(baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal(), deal1);
            deal1.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            deal1.setWorkflowId(workflowId);
            wflPaTrdProductDealMapper.insertSelective(deal1);
        }

        if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd() != null) {
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtils.copyProperties(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), wflTrdProductDealAdd);
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdProductDealAdd.setWorkflowId(workflowId);
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        }
    }
    /**
     * 维护交易入表（汇总及交易）
     * @param baseTotalTradeInfoDto 交易信息dto
     * @param updateUser 更新人
     * @param updateTime 更新事件
     * @param workflowId 工作流代码
     */
    public void doInsertAndDelete(BaseTotalTradeInfoDto baseTotalTradeInfoDto, String updateUser, Date updateTime,
                                  String workflowId) {
        baseTotalTradeInfoDto.getTrdFinProductDealTotal().setUpdateUser(updateUser);
        baseTotalTradeInfoDto.getTrdFinProductDealTotal().setUpdateTime(updateTime);
        trdFinProductDealTotalMapper.insertSelective(baseTotalTradeInfoDto.getTrdFinProductDealTotal());

        baseTotalTradeInfoDto.getPaTrdFinProductDealTotal().setUpdateUser(updateUser);
        baseTotalTradeInfoDto.getPaTrdFinProductDealTotal().setUpdateTime(updateTime);
        paTrdFinProductDealTotalMapper.insertSelective(baseTotalTradeInfoDto.getPaTrdFinProductDealTotal());
        if(baseTotalTradeInfoDto.getTrdTradeFeeDetail()!=null) {
            baseTotalTradeInfoDto.getTrdTradeFeeDetail().setUpdateUser(updateUser);
            baseTotalTradeInfoDto.getTrdTradeFeeDetail().setUpdateTime(updateTime);
            trdTradeFeeDetailMapper.insertSelective(baseTotalTradeInfoDto.getTrdTradeFeeDetail());
        }
        if(baseTotalTradeInfoDto.getTrdDealTotalAdd()!=null){
            baseTotalTradeInfoDto.getTrdDealTotalAdd().setUpdateUser(updateUser);
            baseTotalTradeInfoDto.getTrdDealTotalAdd().setUpdateTime(updateTime);
            trdDealTotalAddMapper.insertSelective( baseTotalTradeInfoDto.getTrdDealTotalAdd());
        }
        if(baseTotalTradeInfoDto.getTrdFinProductBarterTotal() != null){
            trdFinProductBarterTotalMapper.insertSelective(baseTotalTradeInfoDto.getTrdFinProductBarterTotal());
        }
        List<BaseTradeInfoDto> baseTradeInfoDtos = baseTotalTradeInfoDto.getBaseTradeInfoDtoList();
        baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
            doInsertBaseTradeInfo(baseTradeInfoDto,updateUser,updateTime,baseTotalTradeInfoDto.isNeedStlPayOrders());
//            处理存续期子组合交易
            if(baseTradeInfoDto.getBaseTradeInfoDtoList() != null &&  baseTradeInfoDto.getBaseTradeInfoDtoList().size()>0){
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(item->{
//                    不入划款指令
                    doInsertBaseTradeInfo(item,updateUser,updateTime,false);
                });
            }
        });
        deleteByWorkflowId(workflowId);
    }
    public void deleteByWorkflowId(String workflowId) {
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflPaTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);
        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
        wflTrdDealTotalAddMapper.deleteByWorkflowId(workflowId);
    }
    /**
     * 维护交易入表
     * @param baseTradeInfoDto 交易信息
     * @param updateUser 更新人
     * @param updateTime 更新事件
     * @param needStlPayOrders 是否维护划款指令入表
     */
    private void doInsertBaseTradeInfo(BaseTradeInfoDto baseTradeInfoDto, String updateUser, Date updateTime,boolean needStlPayOrders){
        if (baseTradeInfoDto.getTrdFinProductDealTotal() != null) {
            baseTradeInfoDto.getTrdFinProductDealTotal().setUpdateUser(updateUser);
            baseTradeInfoDto.getTrdFinProductDealTotal().setUpdateTime(updateTime);
            trdFinProductDealTotalMapper.insertSelective(baseTradeInfoDto.getTrdFinProductDealTotal());
        }
        if (baseTradeInfoDto.getPaTrdFinProductDealTotal() != null) {
            baseTradeInfoDto.getPaTrdFinProductDealTotal().setUpdateUser(updateUser);
            baseTradeInfoDto.getPaTrdFinProductDealTotal().setUpdateTime(updateTime);
            paTrdFinProductDealTotalMapper.insertSelective(baseTradeInfoDto.getPaTrdFinProductDealTotal());
        }
        if(baseTradeInfoDto.getTrdFinProductBarter() != null){
            trdFinProductBarterMapper.insertSelective(baseTradeInfoDto.getTrdFinProductBarter());
        }
        if (baseTradeInfoDto.getTrdProductDeal() != null) {
            baseTradeInfoDto.getTrdProductDeal().setUpdateUser(updateUser);
            baseTradeInfoDto.getTrdProductDeal().setUpdateTime(updateTime);
            trdProductDealMapper.insertSelective(baseTradeInfoDto.getTrdProductDeal());
        }

        if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
            baseTradeInfoDto.getTrdProductDealAdd().setUpdateUser(updateUser);
            baseTradeInfoDto.getTrdProductDealAdd().setUpdateTime(updateTime);
            trdProductDealAddMapper.insertSelective(baseTradeInfoDto.getTrdProductDealAdd());
        }

        if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
            baseTradeInfoDto.getPaTrdProductDeal().setUpdateUser(updateUser);
            baseTradeInfoDto.getPaTrdProductDeal().setUpdateTime(updateTime);
            paTrdProductDealMapper.insertSelective(baseTradeInfoDto.getPaTrdProductDeal());
        }

        if (baseTradeInfoDto.getTrdTradeFeeDetail() != null) {
            for (TrdTradeFeeDetail detail : baseTradeInfoDto.getTrdTradeFeeDetail()) {
                detail.setUpdateUser(updateUser);
                detail.setUpdateTime(updateTime);
                trdTradeFeeDetailMapper.insertSelective(detail);
            }
        }

        if (needStlPayOrders && baseTradeInfoDto.getStlPayOrders() != null) {
            baseTradeInfoDto.getStlPayOrders().setUpdateUser(updateUser);
            baseTradeInfoDto.getStlPayOrders().setUpdateTime(updateTime);
            stlPayOrdersMapper.insertSelective(baseTradeInfoDto.getStlPayOrders());
        }

        if (CollectionUtils.isNotEmpty(baseTradeInfoDto.getPtlTrade())) {
            baseTradeInfoDto.getPtlTrade().forEach(ptlTrade -> {
                ptlTrade.setUpdateUser(updateUser);
                ptlTrade.setUpdateTime(updateTime);
                ptlTradeMapper.insertSelective(ptlTrade);
            });
        }
        BaseTrade baseTrade = baseTradeInfoDto.getBaseTrade();
        if (baseTrade != null) {
            if (baseTrade.getTrdProductDeal() != null) {
                baseTrade.getTrdProductDeal().setUpdateUser(updateUser);
                baseTrade.getTrdProductDeal().setUpdateTime(updateTime);
                trdProductDealMapper.insertSelective(baseTrade.getTrdProductDeal());
            }
            if (baseTrade.getPaTrdProductDeal() != null) {
                baseTrade.getPaTrdProductDeal().setUpdateUser(updateUser);
                baseTrade.getPaTrdProductDeal().setUpdateTime(updateTime);
                paTrdProductDealMapper.insertSelective(baseTrade.getPaTrdProductDeal());
            }
            if (baseTrade.getTrdProductDealAdd() != null) {
                baseTrade.getTrdProductDealAdd().setUpdateUser(updateUser);
                baseTrade.getTrdProductDealAdd().setUpdateTime(updateTime);
                trdProductDealAddMapper.insertSelective(baseTrade.getTrdProductDealAdd());
            }

            if (CollectionUtils.isNotEmpty(baseTrade.getPtlTrade())) {
                baseTrade.getPtlTrade().forEach(ptlTrade -> {
                    ptlTrade.setUpdateUser(updateUser);
                    ptlTrade.setUpdateTime(updateTime);
                    ptlTradeMapper.insertSelective(ptlTrade);
                });
            }
        }
    }
    public static void setWorkflowId(BaseTotalTradeInfoDto baseTotalTradeInfoDto, String workflowId) {
        if (baseTotalTradeInfoDto != null
                && baseTotalTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId() == null) {
            baseTotalTradeInfoDto.getTrdFinProductDealTotal().setWorkflowId(workflowId);
            baseTotalTradeInfoDto.getPaTrdFinProductDealTotal().setWorkflowId(workflowId);
            if(baseTotalTradeInfoDto.getTrdDealTotalAdd() != null ){
                baseTotalTradeInfoDto.getTrdDealTotalAdd().setWorkflowId(workflowId);
            }
            baseTotalTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
                setWorkflowIdToBaseTradeInfoDto(baseTradeInfoDto,workflowId);
                if(baseTradeInfoDto.getBaseTradeInfoDtoList()!= null && baseTradeInfoDto.getBaseTradeInfoDtoList().size()>0){
                    baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto->{
                        setWorkflowIdToBaseTradeInfoDto(subBaseTradeInfoDto,workflowId);
                    });
                }
            });
        }
    }
    /**
     *  给dto设置工作流代码
     * @param baseTradeInfoDto dto
     * @param workflowId 工作流代码
     */
    private static void setWorkflowIdToBaseTradeInfoDto(BaseTradeInfoDto baseTradeInfoDto ,String workflowId){
        if(baseTradeInfoDto.getTrdFinProductDealTotal()!=null) {
            baseTradeInfoDto.getTrdFinProductDealTotal().setWorkflowId(workflowId);
        }
        if(baseTradeInfoDto.getPaTrdFinProductDealTotal()!=null) {
            baseTradeInfoDto.getPaTrdFinProductDealTotal().setWorkflowId(workflowId);
        }
        if(baseTradeInfoDto.getStlPayOrders()!=null) {
            baseTradeInfoDto.getStlPayOrders().setWorkflowId(workflowId);
        }

        if (baseTradeInfoDto.getTrdProductDeal() != null) {
            baseTradeInfoDto.getTrdProductDeal().setWorkflowId(workflowId);
        }
        if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
            baseTradeInfoDto.getTrdProductDealAdd().setWorkflowId(workflowId);
        }
        if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
            baseTradeInfoDto.getPaTrdProductDeal().setWorkflowId(workflowId);
        }
        if (baseTradeInfoDto.getBaseTrade() != null) {
            if (baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
                baseTradeInfoDto.getBaseTrade().getTrdProductDeal().setWorkflowId(workflowId);
            }
            if (baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd() != null) {
                baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd().setWorkflowId(workflowId);
            }
            if (baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal() != null) {
                baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal().setWorkflowId(workflowId);
            }
        }
    }
    public List<BaseTradeProductDealDto> findEffectiveDeal(String busiCode,DictCodeListExt1 交易流程状态) {
        return findEffectiveDeal(busiCode,交易流程状态,false);
    }
    public List<BaseTradeProductDealDto> findEffectiveDeal(String busiCode, DictCodeListExt1 交易流程状态, boolean isTqzq) {
        Map<String,Object>map = new HashMap<>();
        map.put("statusValid", 交易流程状态.getValue());
        map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
        map.put("bizCode", busiCode);
        map.put("YON_Y", BussinessDictCodeList.是否_是.getValue());
        if(isTqzq){
            map.put("isTqzq", BussinessDictCodeList.是否_是.getValue());
        }
        else{
            map.put("isTqzq", "");
        }
        List<BaseTradeProductDealDto> result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdProjectDuration/findEffectiveDeal",
                map, BaseTradeProductDealDto.class);
        if(CollectionUtils.isEmpty(result)) {
            result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findEffectiveDeal",
                    map, BaseTradeProductDealDto.class);
        }
        return result;
    }
    public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser, String revokeReason, String type6,
                                                 String actPortfolio) {
        List<String> subBizCodes = trdCommonBaseMapper.findSubTotalTradeId(bizCode);
        if (subBizCodes != null && !subBizCodes.isEmpty()) {
            for (String subBizCode : subBizCodes) {
                revoke汇总和交易相关表(subBizCode, updateUser, true, revokeReason,type6, actPortfolio);
            }
        }
        return revoke汇总和交易相关表(bizCode, updateUser, true, revokeReason,type6, actPortfolio);
    }
    public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser, boolean isTotal, String revokeReason, String type6,
                                                 String actPortfolio) {
        // 用于获取关键信息的map
        Map<String, Object> map = new HashMap<>();
        // 是否汇总的参数区分
        if (isTotal) {
            map.put("totleTradeId", bizCode);
        }
        else {
            map.put("tradeId", bizCode);
        }
        Date updateTime = new Date();
        TrdFinProductDealTotal trdFinProductDealTotal = null;
        if (isTotal) {
            // 金融产品交易汇总
            trdFinProductDealTotal = trdFinProductDealTotalMapper.selectByPrimaryKey(bizCode);
            // 原数据 是否撤销 => 是
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());

            trdFinProductDealTotal.setUpdateTime(updateTime);
            trdFinProductDealTotal.setUpdateUser(updateUser);
            trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);
            // 撤销交易状态为撤销
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            String newTotalTradeId = trdFinProductDealTotal.getTotleTradeId() + "R";
            trdFinProductDealTotal.setpTradeId(trdFinProductDealTotal.getTotleTradeId());
            trdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
            if (StringUtil.isNotEmpty(trdFinProductDealTotal.getpTotleTradeId())) {
                trdFinProductDealTotal.setpTotleTradeId(trdFinProductDealTotal.getpTotleTradeId() + "R");
            }
            // 是否撤销入否
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

            PaTrdFinProductDealTotal paTrdFinProductDealTotal = paTrdFinProductDealTotalMapper.selectByPrimaryKey(bizCode);
            paTrdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
            paTrdFinProductDealTotal.setUpdateTime(updateTime);
            paTrdFinProductDealTotal.setUpdateUser(updateUser);
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);

            TrdDealTotalAdd trdDealTotalAdd = trdDealTotalAddMapper.selectByPrimaryKey(bizCode);
            /** add by yanya on 20191116 修复债券存续期撤销取不到值报空错误**/
            if(trdDealTotalAdd != null) {
                trdDealTotalAdd.setTotleTradeId(newTotalTradeId);
                trdDealTotalAdd.setUpdateTime(updateTime);
                trdDealTotalAdd.setUpdateUser(updateUser);
                trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
            }

        }
        // 划款指令
        List<StlPayOrders> ordersTotal = trdCommonBaseMapper.findOrdersByOrderId(bizCode);
        revokeStlPayOrders(ordersTotal);
        // 费用明细
        trdCommonBaseMapper.inertRevokeFeeDetailByTradeId(bizCode, updateUser);
        // 费用汇总
        trdCommonBaseMapper.inertRevokeFeeTotalByTradeId(bizCode, updateUser);
        // 金融产品交易Entity
        // 增加无汇总交易的场景 无汇总交易使用交易编号关联
        List<TrdProductDeal> trdProductDeals = trdCommonBaseMapper.findDealByTotalId(map);

        // 一体化对应etliu start
        Map<String, String> portfolioIdMap = new HashMap<String, String>();
        // 一体化对应etliu end

        trdProductDeals.forEach(trdProductDeal1 -> {
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtils.copyProperties(trdProductDeal1, trdProductDeal);
            trdProductDeal.setIsCancel(DictCodeList.是否_是.getValue());
            // 原数据 是否撤销 => 是
            trdProductDealMapper.updateByPrimaryKey(trdProductDeal);
            // 交易状态为撤销
            String oldTradeId = trdProductDeal.getTradeId();
            String newTradeId = trdProductDeal.getTradeId() + "R";
            String oldTotleTradeId = trdProductDeal.getTotleTradeId();
            String newTotleTradeId = trdProductDeal.getTotleTradeId() + "R";
            trdProductDeal1.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue()); // 调用估值接口时，状态是撤销
            trdProductDeal1.setpTradeId(oldTradeId);
            trdProductDeal1.setTradeId(newTradeId);
            trdProductDeal.setTotleTradeId(newTotleTradeId);
            trdProductDeal.setpTradeId(oldTradeId);
            trdProductDeal.setTradeId(newTradeId);
            //更新存续期交易撤销创建时间和更新时间 add by lishuping 20190911 start
            trdProductDeal.setCreateTime(new Date());
            trdProductDeal.setUpdateTime(new Date());
            //更新存续期交易撤销创建时间和更新时间 add by lishuping 20190911 end
            trdProductDeal.setCancelRemark(revokeReason);
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            trdProductDealMapper.insertSelective(trdProductDeal);

            // 划款指令
            List<StlPayOrders> orders = trdCommonBaseMapper.findOrdersByOrderId(oldTradeId);
            revokeStlPayOrders(orders);

            trdCommonBaseMapper.insertRevokePaTrdProductDeal(oldTradeId, updateUser);
            trdCommonBaseMapper.insertRevokeTrdProductDealAdd(oldTradeId, updateUser);

            TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(oldTradeId);
            // 组合交易
            if (trdProductDealAdd != null && StringUtil.isNotEmpty(trdProductDealAdd.getPortfolioId())) {
                PtlTrade deal = new PtlTrade();
                deal.setBusiNo(oldTradeId);
                deal.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                deal.setPortfolioId(trdProductDealAdd.getPortfolioId());
                trdCommonBaseMapper.insertRevokePtlTrade(deal, updateUser);
                if (StringUtil.isNotEmpty(actPortfolio) && !trdProductDealAdd.getPortfolioId().equals(actPortfolio)) {
                    deal.setPortfolioId(actPortfolio);
                    trdCommonBaseMapper.insertRevokePtlTrade(deal, updateUser);
                }

                // 一体化对应etliu start
                portfolioIdMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
                // 一体化对应etliu end
            }

            // 费用明细
            trdCommonBaseMapper.inertRevokeFeeDetailByTradeId(oldTradeId, updateUser);
            // 删除交易报送
            trdCommonBaseMapper.deleteRepTradeByTradeId(oldTradeId);

//            // 一体化对应etliu start
//            // 所有交易生效后调用的共通方法
//            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            inputDto.setProdid(trdProductDealAdd.getPortfolioId());
//            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//            inputDto.setTrdProductDeal(trdProductDeal);
//            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//            // 一体化对应etliu end
        });

        // 一体化对应etliu start
        if (!StringUtil.isEmptyList(trdProductDeals)) {
            // 拆入到期 调用lendMdatDelete估值接口
            String finprodType2 = trdProductDeals.get(0).getFinprodType2();
            if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆入.getValue(), finprodType2)
                    || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue(), finprodType2)
                    || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue(), finprodType2)
                    || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), finprodType2)
                    || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), finprodType2)
                    || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue(), finprodType2)
            ) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setTotleTradeId(bizCode);
                AllTradeCommonUtil.lendMdatDelete(inputDto);
                return trdFinProductDealTotal;
            }
            if (isTotal) {
                map.put("totleTradeId", bizCode+'R');
            }
            else {
                map.put("tradeId", bizCode+'R');
            }
//            List<TrdProductDeal> trdProductDeals1 = trdCommonBaseMapper.findDealByTotalIdForInterf(map);
//            List<TrdProductDealAdd> trdProductDealAdds = trdCommonBaseMapper.findDealAddByTradeIds(trdProductDeals1);
//            List<PtlTrade> ptlTrades = trdCommonBaseMapper.findPtlTradeByTradeIds(trdProductDeals1);
//
//            // 所有交易生效后调用的共通方法
//            for (TrdProductDeal baseDto : trdProductDeals1) {
//                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//                inputDto.setProdid(portfolioIdMap.get("portfolioId"));
//                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//
//                baseDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
//                inputDto.setTrdProductDeal(baseDto);
//                inputDto.setTrdProductDealAdd(getAdd(trdProductDealAdds, baseDto.getTradeId()));
//                inputDto.setRepoType(type6);
//                List<PtlTrade> ptlTradeList = new ArrayList<>();
//                String actPortfolioId = null;
//                for (PtlTrade ptlTrade : ptlTrades) {
//                    if (baseDto.getTradeId().equals(ptlTrade.getBusiNo())) {
//                        if (!inputDto.getTrdProductDealAdd().getPortfolioId().equals(ptlTrade.getPortfolioId())) {
//                            actPortfolioId = ptlTrade.getPortfolioId();
//                        }
//                        ptlTradeList.add(ptlTrade);
//                    }
//                }
//                if (actPortfolio != null) {
//                    inputDto.setActPortfolioId(actPortfolioId);
//                }
//                inputDto.setPtlTradeList(ptlTradeList);
//                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//            }
        }
        // 一体化对应etliu end

        return trdFinProductDealTotal;
    }
    private TrdProductDealAdd getAdd(List<TrdProductDealAdd> trdProductDealAdds, String tradeId) {
        for (TrdProductDealAdd dealAdd : trdProductDealAdds) {
            if (tradeId.equals(dealAdd.getTradeId())) {
                return dealAdd;
            }
        }
        return null;
    }
    private void revokeStlPayOrders(List<StlPayOrders> orders) {
        if (orders != null) {
            orders.forEach(order -> {
                        order.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        order.setpTradeId(order.getOrderId());
                        order.setTradeId(order.getTradeId() + "R");
                        order.setOrderId(order.getOrderId() + "R");
                        order.setUpdateTime(DateUtil.now());
                        stlPayOrdersMapper.insertSelective(order);
                    }
            );
        }
    }

    /**
     * 根据业务编号查询交易表信息
     * @param busiNo 业务编号
     * @return
     */
    public List<PtlTrade> findPtlTradeByBusiNo(String busiNo) {
        return trdCommonBaseMapper.findPtlTradeByBusiNo(busiNo);
    }

    /**
     * 校验指令是否已经转交易到流程中
     * @param entrustId
     * @return
     */
    public List<WflTrdProductDealAdd> checkEtrToTrdInFlow(String entrustId) {
        return trdCommonBaseMapper.checkEtrToTrdInFlow(entrustId);
    }

    /**
     * 根据交易编号查询另一笔未撤销的转仓交易信息
     * add by hpp 2020-09-17
     * @param tradeId
     * @return
     */
    public TrdProductDeal selectZCDeal(String tradeId){
        return trdCommonBaseMapper.selectZCDeal(tradeId);
    }

    /**
     * 根据交易编号查询另一笔未撤销的转仓交易信息
     * add by hpp 2020-09-17
     * @param tradeId
     * @return
     */
    public TrdProductDeal selectZCDealByTid(String tradeId,boolean hastotle){
        if(hastotle) {
            return trdCommonBaseMapper.selectZCDeal(tradeId);
        }else{
           return trdCommonBaseMapper.selectZCDealByTradeId(tradeId);
        }
    }

    /**
     * 根据交易编号查询该交易是否已被撤销
     *
     * @param tradeId 交易编号
     * @return 该交易是否已被撤销
     * @see
     * @since 1.0
     */
    public Boolean isAlreadyRevoked(String tradeId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tradeId", tradeId);
        paramMap.put("YES_NO_YES", DictCodeList.是否_是.getValue());
        paramMap.put("TRADE_STATUS_03", DictCodeListExt1.交易流程状态_撤销.getValue());
        return trdCommonBaseMapper.selectRevokedDataCount(paramMap) > 0;

    }
	
	public List<WflTrdProductDeal> findDealByBtradeId(String tradeId) {
        return trdCommonBaseMapper.findDealByBtradeId(tradeId);
    }

    /**
     * 撤销交易时，判断清算的状态是否是清算撤销，是才能撤，对于多组合交易，查询每个单组合交易的清算状态
     * @param tradeId
     * @return
     */
    public boolean isCanRevoke(String tradeId){
        Map<String, Object> map = new HashMap<>();
        map.put("JYZT_CS", DictCodeListExt1.交易状态_初始.getValue());
        map.put("SF_S", DictCodeListExt1.是否_是.getValue());
        map.put("tradeIdL",tradeId);
        return trdCommonBaseMapper.isCanRevoke(map) > 0;
    }

    /**
     * 模拟试算
     * 金融产品交易主方法查询
     */
    public MockTradeInfoDto selectTradeinfodeal(Map<String, Object> map) {
    	MockTradeInfoDto allList = new MockTradeInfoDto();
    	//资产端交易列表
    	List<MockTrdProductDealDto> listz = selectTradedealz(map);
    	//负债端交易列表
    	List<MockTrdProductDealDto> listf = selectTradedealf(map);
    	//资产端T+N交易列表
    	List<MockTrdProductDealDto> listztn = selectTradedealztn(map);
    	//负债端T+N交易列表
    	List<MockTrdProductDealDto> listftn = selectTradedealftn(map);
    	List<MockTrdProductDealDto> listfy = selectFyTradedeal(map);
    	allList.setAssetTradeList(listz);
    	allList.setLiabilitiesTradeList(listf);
    	allList.setAssetTdayPlusNdayTradeList(listztn);
    	allList.setLiabilitiesTdayPlusNdayTradeList(listftn);
    	//费用交易列表
    	allList.setFeeTradeList(listfy);
        return allList;
    }

    /**
     * 模拟试算
     * 金融产品交易查询
     */
    public List<MockTrdProductDealDto> selectTradedealz(Map<String, Object> map) {
    	map.put("psList", "T01,T02,T03,T04,T05,T06,T07,T08,T09,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,T37,T38,T39,T40,T41,T42,T43,T44,T45,T46,T47,T48,T49,T50,T51,T52,T53,T54,T55,T56,T58,T59,T60,T61,T62,T63,T64,T65,T66,T98,T99");
    	map.put("psList", StringUtil.splitToList(GlobalConstant.COMMA,(String)map.get("psList")));
    	map.put("isyesorno", "N");
    	return trdCommonBaseMapper.selectTradedeal(map);
    }

    /**
     * 模拟试算
     * 金融产品交易查询
     */
    public List<MockTrdProductDealDto> selectTradedealf(Map<String, Object> map) {
    	map.put("psList", "C1,C10,C14,C15,C16,C17,C18,C2,C20,C3,C4,C5,C6,C7,C8,C9");
    	map.put("psList", StringUtil.splitToList(GlobalConstant.COMMA,(String)map.get("psList")));
    	map.put("isyesorno", "N");
        return trdCommonBaseMapper.selectTradedeal(map);
    }

    /**
     * 模拟试算
     * 金融产品交易查询
     */
    public List<MockTrdProductDealDto> selectTradedealztn(Map<String, Object> map) {
    	map.put("psList", "T01,T02,T03,T04,T05,T06,T07,T08,T09,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,T37,T38,T39,T40,T41,T42,T43,T44,T45,T46,T47,T48,T49,T50,T51,T52,T53,T54,T55,T56,T58,T59,T60,T61,T62,T63,T64,T65,T66,T98,T99");
    	map.put("psList", StringUtil.splitToList(GlobalConstant.COMMA,(String)map.get("psList")));
    	map.put("isyesorno", "Y");
        return trdCommonBaseMapper.selectTradedeal(map);
    }

    /**
     * 模拟试算
     * 金融产品交易查询
     */
    public List<MockTrdProductDealDto> selectTradedealftn(Map<String, Object> map) {
    	map.put("psList", "C1,C10,C14,C15,C16,C17,C18,C2,C20,C3,C4,C5,C6,C7,C8,C9");
    	map.put("psList", StringUtil.splitToList(GlobalConstant.COMMA,(String)map.get("psList")));
    	map.put("isyesorno", "Y");
        return trdCommonBaseMapper.selectTradedeal(map);
    }

    /**
     * 模拟试算
     * 费用交易列表查询
     */
    public List<MockTrdProductDealDto> selectFyTradedeal(Map<String, Object> map) {
        return trdCommonBaseMapper.selectFyTradedeal(map);
    }

    public boolean checkRevokeRTrade(String tradeIdL) {
        Map<String, Object> map = new HashMap<>();
        map.put("JYZT_CS", DictCodeListExt1.交易状态_初始.getValue());
        map.put("SF_S", DictCodeListExt1.是否_是.getValue());
        map.put("tradeIdL",tradeIdL);
        return trdCommonBaseMapper.checkRevokeRTrade(map)>0;
    }

    /**
     * check预录入交易是否存在有效的真实交易
     * @param tradeId
     * @return
     */
    public boolean checkRevokeBTrade(String tradeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("JYZT_CS", DictCodeListExt1.交易状态_初始.getValue());
        map.put("SF_S", DictCodeListExt1.是否_是.getValue());
        map.put("tradeIdL",tradeId);
        return trdCommonBaseMapper.checkRevokeBTrade(map)>0;
    }

    // add by etliu 20210421 权益类资产相关代码合并 start
    /**
     * 根据资产代码查询流程中已录入金额
     * @param map 查询参数
     * @return BigDecimal
     * @author chaowei
     * @since 1.0
     */
    public BigDecimal queryWflPrinAmtByAssetId(Map<String,Object> map) {
        String finprod_id = (String) map.get("finprod_id");
        if(StringUtil.isEmpty(finprod_id)){
            return BigDecimal.ZERO;
        }
        map.put("TRADE_STATUS", DictCodeListExt1.交易状态_初始.getValue());
        map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        BigDecimal wflPrinAmt = trdCommonBaseMapper.queryWflPrinAmtByAssetId(map);
        return wflPrinAmt;
    };
    // add by etliu 20210421 权益类资产相关代码合并 end


    /**
     * 通过组合代码查询金融产品标的
     * @param portfolioId
     * @return
     */
    public FinProduct queryFinProductByPortfolidId(String portfolioId){

        return trdCommonBaseMapper.queryFinProductByPortfolidId(portfolioId);
    }

    /**
     * 百元券面共通查询方法
     *
     * @param finprodId 标的代码
     * @param happenDate 发生日
     * @return faceValue 处理后的百元券面
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BigDecimal getFaceValue(String finprodId, Date happenDate) {
        BigDecimal faceValue = new BigDecimal("100");
        BigDecimal payedFaceValue = new BigDecimal("0");
        // 增加查询类型Dto
        Map<String, Object> paMap = new HashMap<>();
        paMap.put("finprodId", finprodId);// 标的代码
        paMap.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());// 现金流一级类型
        paMap.put("branch", 0L);
        List<FinCashPlan> listDto = trdCommonBaseMapper.getTargetCashPlanList(paMap);

        if(null!=listDto){
            for (FinCashPlan plan : listDto) {
                // 计息结束日 <= 发生日
                if (DateUtil.compareDate(plan.getMdate(), happenDate) <= 0) {
                    payedFaceValue = ArithmeticUtil.doAdd(payedFaceValue, plan.getPrinAmt());
                }
            }
        }
        faceValue = ArithmeticUtil.subtract(faceValue, payedFaceValue);
        return faceValue;
    }

    /**
     * 根据组合ID查询母组合ID
     * @param portfolioId
     * @return
     */
    public String selectParentPortfolioIdByPortfolioId(String portfolioId) {
        return trdCommonBaseMapper.selectParentPortfolioIdByPortfolioId(portfolioId);
    }

    /**
     * 根据市场代码查询金融产品代码
     *
     * @param finprodMarketId 市场代码
     * @return 金融产品代码
     * @see
     * @since 1.0
     */
    public String queryFinprodIdByFinprodMarketId(String finprodMarketId, String finprodType2) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodMarketId", finprodMarketId);
        param.put("finprodType2", finprodType2);
        String finprodId = trdCommonBaseMapper.queryFinprodIdByFinprodMarketId(param);
        if (StringUtil.isBlank(finprodId)) {
            if (StringUtil.startsWith(finprodMarketId, finprodType2)) {
                finprodId = finprodMarketId;
            }
            else {
                finprodId = finprodType2 + "_" + finprodMarketId;
            }
        }

        return finprodId;
    }

    // add by etliu 20220116 start
    /**
     * 根据首期成交编号查询协议回购首期金融产品代码
     * @param portfolioId 组合代码
     * @param firstFTradeId 首期成交编号
     * @return 协议回购首期金融产品代码
     */
    public String selectHgsqFinprodId(String portfolioId, String firstFTradeId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("portfolioId", portfolioId);
        paramMap.put("firstFTradeId", firstFTradeId);

        return trdCommonBaseMapper.selectHgsqFinprodId(paramMap);
    }
    // add by etliu 20220116 end


    /**
     * 查询组合计算分配信息
     * @param paraMap
     * @return
     */
    public List<PortfolioCalculateDto> queryPortfolioCalculate (Map<String,Object> paraMap) {
        logger.debug("【查询组合计算分配信息】paraMap: {}", JSON.toJSONString(paraMap));
        List<PortfolioCalculateDto> portfolioDtos = new ArrayList<>();
        // 成交编号
        String dealNo = (String) paraMap.get("dealNo");
        // 关联交易编号
        String fTradeId = (String) paraMap.get("fTradeId");
        if(!StringUtil.isEmpty(dealNo)){
            // 查询关联交易编号
            fTradeId = trdCommonBaseMapper.selectFTradeId(dealNo);
        }
        if(StringUtil.isEmpty(fTradeId)){
            return portfolioDtos;
        }
        // 查询下单对应的委托编号信息
        InfContractDetailfile infContractDetailfile = trdCommonBaseMapper.selectInfContractDetailfile(fTradeId);
        // 根据委托编号查询下单信息
        OrderInfoDto orderInfoDto = trdCommonBaseMapper.selectOrderInfo(infContractDetailfile.getOrderId());
        // ZGTFCP-2165:查询下单信息,查不到睡一秒再查,循环3次，下个版本优化 modify by fuyongsheng 20220330 start
        if(null == orderInfoDto){
            try {
                for(int i=0; i<10; i++){
                    logger.debug("【查询下单信息为空】OrderId: {}", infContractDetailfile.getOrderId());
                    Thread.sleep(1000L);
                    orderInfoDto = trdCommonBaseMapper.selectOrderInfo(infContractDetailfile.getOrderId());
                    if(orderInfoDto != null){
                        break;
                    }
                }
            } catch (InterruptedException e) {
            }
            if(orderInfoDto == null){
                return new ArrayList<>();
            }
        }
        // ZGTFCP-2165:查询下单信息,查不到睡一秒再查,循环3次，下个版本优化 modify by fuyongsheng 20220330 end
        // 查询指令信息
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(orderInfoDto.getInstructionId());
        if(etrEntrust == null){
            return new ArrayList<>();
        }
        // ZGTFCP-1560:计算标准券面额 modify by fuyongsheng 20220325 start
        //查询当日折算率
        BigDecimal converseRadio = trdCommonBaseMapper.queryConverseRadio(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
        if(converseRadio==null) {
            converseRadio = BigDecimal.ONE;
        }
        // 标准券券面总额=质押数量*标准折算率*面值
        BigDecimal 百元券面 = this.getFaceValue(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
        if (百元券面 == null) {
            百元券面 = BigDecimal.valueOf(100);
        }
        BigDecimal hundred = BigDecimal.valueOf(100);
        // ZGTFCP-1560:计算标准券面额 modify by fuyongsheng 20220325 end
        if(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue().equals(etrEntrust.getEntrustClass())){ // 多组合指令
            List<EtrEntrustDetail> etrEntrustDetails = trdCommonBaseMapper.selectEtrEntrustDetailByEntrustId(etrEntrust.getEntrustId());
            if(org.springframework.util.CollectionUtils.isEmpty(etrEntrustDetails)){
                return new ArrayList<>();
            }
            for(EtrEntrustDetail etrEntrustDetail : etrEntrustDetails){
                PortfolioCalculateDto portfolioDto = new PortfolioCalculateDto();
                // 组合代码
                portfolioDto.setPortfolioId(etrEntrustDetail.getPortfolioId());
                // 资金账户
                portfolioDto.setCashAcctId(orderInfoDto.getAccountId());
                // 证券账户
                portfolioDto.setSecAcctId(orderInfoDto.getSecAcctId());
                // 下单编号
                portfolioDto.setOrderId(infContractDetailfile.getOrderId());
                BigDecimal ratio = new BigDecimal(1);
                if(DictCodeListExt3.目标类型_数量.getValue().equals(etrEntrustDetail.getTargetType())){
                    // 指令券面总额
                    if(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(etrEntrust.getFinprodType2())){
                        portfolioDto.setEtrSecAmount(ArithmeticUtil.multiply(etrEntrustDetail.getEntrustNum(), new BigDecimal(100)));
                    }else{
                        portfolioDto.setEtrSecAmount(ArithmeticUtil.multiply(etrEntrustDetail.getEntrustNum()));
                    }
                    ratio = ArithmeticUtil.divide(etrEntrustDetail.getEntrustNum(), etrEntrust.getEntrustNum(), 8, RoundingMode.HALF_UP);
                }else{
                    // 指令券面总额
                    portfolioDto.setEtrSecAmount(etrEntrustDetail.getEntrustAmt());
                    ratio = ArithmeticUtil.divide(etrEntrustDetail.getEntrustAmt(), etrEntrust.getEntrustAmt(), 8, RoundingMode.HALF_UP);
                }
                // 交易金额
                portfolioDto.setCashAmount(ArithmeticUtil.multiply(infContractDetailfile.getTradeAmt(), ratio));
                if(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(etrEntrust.getFinprodType2())){
                    // 券面总额
                    portfolioDto.setSecAmount(ArithmeticUtil.multiply(infContractDetailfile.getShareAmt(), new BigDecimal(100), ratio));
                    // 下单券面总额
                    portfolioDto.setOrdSecAmount(ArithmeticUtil.multiply(orderInfoDto.getEntrustVolume(), new BigDecimal(100), ratio));
                }else{
                    // 券面总额
                    portfolioDto.setSecAmount(ArithmeticUtil.multiply(infContractDetailfile.getShareAmt(), ratio));
                    // 下单券面总额
                    portfolioDto.setOrdSecAmount(ArithmeticUtil.multiply(orderInfoDto.getEntrustVolume(), ratio));
                }
                // ZGCPCN-126:更改本方券总额赋值 modify by fuyongsheng 20220420 start
                // 本方券面总额
                portfolioDto.setOwnSecAmount(portfolioDto.getSecAmount());
                // ZGCPCN-126:更改本方券总额赋值 modify by fuyongsheng 20220420 end
                // 标准券券面总额
                BigDecimal standardSecAmount = ArithmeticUtil.multiply(infContractDetailfile.getShareAmt().multiply(converseRadio)
                        .multiply(百元券面), ratio);
                BigDecimal standardSecAmountTemp = ArithmeticUtil.divide(standardSecAmount, new BigDecimal(100),0, RoundingMode.DOWN);
                portfolioDto.setStandardSecAmount(ArithmeticUtil.multiply(standardSecAmountTemp, new BigDecimal(100)));
                // 融入券券面总额
                portfolioDto.setIntegrateSecAmount(BigDecimal.ZERO);
                portfolioDtos.add(portfolioDto);
            }
        }else{ // 单组合指令
            PortfolioCalculateDto portfolioDto = new PortfolioCalculateDto();
            // 组合代码
            portfolioDto.setPortfolioId(etrEntrust.getPortfolioId());
            // 资金账户
            portfolioDto.setCashAcctId(orderInfoDto.getAccountId());
            // 证券账户
            portfolioDto.setSecAcctId(orderInfoDto.getSecAcctId());
            // 下单编号
            portfolioDto.setOrderId(infContractDetailfile.getOrderId());
            // 交易金额
            portfolioDto.setCashAmount(infContractDetailfile.getTradeAmt());
            if(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(etrEntrust.getFinprodType2())){
                // 指令券面总额
                portfolioDto.setEtrSecAmount(ArithmeticUtil.multiply(etrEntrust.getEntrustNum(), new BigDecimal(100)));
                // 下单券面总额
                portfolioDto.setOrdSecAmount(ArithmeticUtil.multiply(orderInfoDto.getEntrustVolume(), new BigDecimal(100)));
                // 券面总额
                portfolioDto.setSecAmount(ArithmeticUtil.multiply(infContractDetailfile.getShareAmt(), new BigDecimal(100)));
                // 本方券面总额
                portfolioDto.setOwnSecAmount(ArithmeticUtil.multiply(infContractDetailfile.getShareAmt(), new BigDecimal(100)));
            }else{
                // 指令券面总额
                portfolioDto.setEtrSecAmount(ArithmeticUtil.multiply(etrEntrust.getEntrustNum()));
                // 下单券面总额
                portfolioDto.setOrdSecAmount(ArithmeticUtil.multiply(orderInfoDto.getEntrustVolume()));
                // 券面总额
                portfolioDto.setSecAmount(ArithmeticUtil.multiply(infContractDetailfile.getShareAmt()));
                // 本方券面总额
                portfolioDto.setOwnSecAmount(ArithmeticUtil.multiply(infContractDetailfile.getShareAmt()));
            }
            // 标准券券面总额
            // ZGTFCP-1560: 修改标准券面额赋值 modify by fuyongsheng 20220327 start
            // 标准券券面总额
            BigDecimal standardSecAmount = ArithmeticUtil.multiply(infContractDetailfile.getShareAmt().multiply(converseRadio)
                    .multiply(百元券面));
            BigDecimal standardSecAmountTemp = ArithmeticUtil.divide(standardSecAmount, new BigDecimal(100),0, RoundingMode.DOWN);
            portfolioDto.setStandardSecAmount(ArithmeticUtil.multiply(standardSecAmountTemp, new BigDecimal(100)));
            // 融入券券面总额
            portfolioDto.setIntegrateSecAmount(BigDecimal.ZERO);
            portfolioDtos.add(portfolioDto);
        }
        logger.debug("【组合计算分配结果】portfolioDtos: {}", JSON.toJSONString(portfolioDtos));
        return portfolioDtos;
    }

    public PtlCapacctInfo getPtlCapAcctInfo(Map<String, Object> map){
        return  trdCommonBaseMapper.getPtlCapAcctInfo(map);
    }

    public List<FinPledgeDispDetailTemp> getFinPledgeDispDetailTemps(Map<String, Object> map){
        return  trdCommonBaseMapper.getFinPledgeDispDetailTemps(map);
    }

    /**
     * 查询首期质押券信息
     * @param portfolioId
     * @param firstFTradeId
     * @return
     */
    public FinDealPledge getSqFinDealPledge(String portfolioId, String firstFTradeId){
        String hgsqFinprodId = selectHgsqFinprodId(portfolioId, firstFTradeId);
        if (StringUtil.isNotBlank(hgsqFinprodId)) {
            // 质押券信息
            FinDealPledgeKey finDealPledgeKey = new FinDealPledgeKey();
            finDealPledgeKey.setFinprodId(hgsqFinprodId);
            finDealPledgeKey.setBranch(0L);
            return trdCommonBaseMapper.selectFinDealPledgeByPrimaryKey(finDealPledgeKey);
        }
        return null;
    }

}
