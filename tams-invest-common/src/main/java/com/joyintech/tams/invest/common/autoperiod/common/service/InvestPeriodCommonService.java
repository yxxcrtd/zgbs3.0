package com.joyintech.tams.invest.common.autoperiod.common.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.common.dao.PeriodCommonMapper;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrade;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdplanintduration.TrdPlanInterestDurationDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;

/**
 * @program: tams-invest-parent
 * @description:
 * @author: 强成西
 * @create: 2018-11-06 13:59
 **/
@Service
public class InvestPeriodCommonService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(InvestPeriodCommonService.class);

    public final String 金融产品交易业务流程表 = "WFL_TRD_PRODUCT_DEAL";

    public final String 金融产品交易附属业务流程表 = "WFL_TRD_PRODUCT_DEAL_ADD";

    public final String 金融产品交易扩展业务流程表 = "WFL_PA_TRD_PRODUCT_DEAL";

    public final String 划款指令业务流程表 = "WFL_STL_PAY_ORDERS";
    public final String 金融产品交易汇总业务流程 = "WFL_TRD_FIN_PRODUCT_DEAL_TOTAL";
    public final String 金融产品交易汇总扩展业务流程= "WFL_PA_TRD_FIN_DEAL_TOTAL";

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private PeriodCommonMapper periodCommonMapper;

    @Autowired
    private RepTradeMapper repTradeMapper;

    @Autowired
    private IPlatformDate platformDateImpl;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;


    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    /**
     * 金融产品类型表Mapper
     */
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    // 调用投管接口 add by renping 20190205 end
    
    /**
     * 金融产品表Mapper
     */
    @Autowired
    private FinProductMapper finProductMapper;
    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    public void revoke后续交易相关表(String bizCode, String updateUser, String revokeReason, String type6, String actPortfolio) {
    	// 现金分红场景
        Map<String, Object> map = new HashMap<>();
        map.put("isCancle", DictCodeList.是否_否.getValue());
        map.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
        map.put("tableId", 金融产品交易汇总业务流程);
        map.put("tradeId", bizCode);
        String flowTradeId = periodCommonMapper.findFollowDeal(map);
        if (StringUtil.isNotEmpty(flowTradeId)) {
            // 未确认直接删除流程变数据即可
            deleteWflByTotleTradeId(flowTradeId);
	   } else {
	            map.put("tableId", "TRD_FIN_PRODUCT_DEAL_TOTAL");
	            flowTradeId = periodCommonMapper.findFollowDeal(map);
	            // 登录反向交易
                if(StringUtil.isNotEmpty(flowTradeId)){
                    revoke汇总和交易相关表(flowTradeId, updateUser, revokeReason, null, actPortfolio);
                }
	   }
        
    }
    /**
     * 删除流程中数据
     * 
     * @param tradeId 交易代码
     */
    public void deleteWflByTotleTradeId(String tradeId) {
		Map<String, Object> map = new HashMap<>();
		map.put("tradeId", tradeId);
		map.put("isTotle", "01");
        String[] tableTotalArr = { 金融产品交易汇总业务流程,金融产品交易汇总扩展业务流程,};
        for (String tableId : tableTotalArr) {
            map.put("tableId", tableId);
            periodCommonMapper.deleteWflByTradeId(map);
        }
        map.put("tableId", 金融产品交易业务流程表);
		 List<String>subTradeId = periodCommonMapper.findSubTradeId(map);
		 if(subTradeId!=null) {
			 map.put("isTotle", "02");
			 subTradeId.forEach(tradeIdTmp->{
				 map.put("tradeId", tradeIdTmp);
				 String[] tableArr = { 金融产品交易扩展业务流程表, 金融产品交易附属业务流程表,
	                     划款指令业务流程表, 金融产品交易业务流程表 };
			   for (String tableId : tableArr) {
			            map.put("tableId", tableId);
			            periodCommonMapper.deleteWflByTradeId(map);
			   }
			 });
		 }
		 else {
			 map.put("isTotle", "02");
			 String[] tableArr = { 金融产品交易扩展业务流程表, 金融产品交易附属业务流程表,
                     划款指令业务流程表, 金融产品交易业务流程表 };
			 for (String tableId : tableArr) {
		            map.put("tableId", tableId);
		            periodCommonMapper.deleteWflByTradeId(map);
	            }
		 }
    }
    public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser, String revokeReason, String type6,
                                                 String actPortfolio) {
        List<String> subBizCodes = periodCommonMapper.findSubTotalTradeId(bizCode);
        if (subBizCodes != null && !subBizCodes.isEmpty()) {
            for (String subBizCode : subBizCodes) {
                revoke汇总和交易相关表(subBizCode, updateUser, true, revokeReason,type6, actPortfolio);
            }
        }
        return revoke汇总和交易相关表(bizCode, updateUser, true, revokeReason,type6, actPortfolio);
    }
    public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser, boolean isTotal, String revokeReason, String type6,
                                                 String actPortfolio) {
        return revoke汇总和交易相关表(bizCode, updateUser, true, revokeReason,type6, actPortfolio,true);
    }
    public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser, boolean isTotal, String revokeReason, String type6,
                                                 String actPortfolio,boolean commonFlag) {
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
            if (trdFinProductDealTotal != null) {
                // 原数据 是否撤销 => 是
                trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());

                //多级法人改造 add by xr 20200711
                if(MultilevelDataAuthorityUtil.isEnabled()){
                    // 机构代码
                    trdFinProductDealTotal.setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(updateUser));
                }
                //多级法人改造 add by xr  20200711
                trdFinProductDealTotal.setUpdateTime(updateTime);
                trdFinProductDealTotal.setUpdateUser(updateUser);
                if(StringUtil.isNotEmpty(revokeReason)){
                    trdFinProductDealTotal.setRemark((trdFinProductDealTotal.getRemark()==null?"":trdFinProductDealTotal.getRemark())+revokeReason);
                }
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
                if (paTrdFinProductDealTotal != null) {
                    paTrdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
                    paTrdFinProductDealTotal.setUpdateTime(updateTime);
                    paTrdFinProductDealTotal.setUpdateUser(updateUser);
                    paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
                }

                TrdDealTotalAdd trdDealTotalAdd = trdDealTotalAddMapper.selectByPrimaryKey(bizCode);
                /** add by yanya on 20191116 修复债券存续期撤销取不到值报空错误**/
                if (trdDealTotalAdd != null) {
                    trdDealTotalAdd.setTotleTradeId(newTotalTradeId);
                    trdDealTotalAdd.setUpdateTime(updateTime);
                    trdDealTotalAdd.setUpdateUser(updateUser);
                    trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
                }
            }
        }
        // 划款指令
        List<StlPayOrders> ordersTotal = periodCommonMapper.findOrdersByOrderId(bizCode);
        revokeStlPayOrders(ordersTotal);
        // 费用明细
        periodCommonMapper.inertRevokeFeeDetailByTradeId(bizCode, updateUser);
        // 费用汇总
        periodCommonMapper.inertRevokeFeeTotalByTradeId(bizCode, updateUser);
        // 金融产品交易Entity
        // 增加无汇总交易的场景 无汇总交易使用交易编号关联
        List<TrdProductDeal> trdProductDeals = periodCommonMapper.findDealByTotalId(map);

     // 一体化对应etliu start
        Map<String, String> portfolioIdMap = new HashMap<String, String>();
     // 一体化对应etliu end

        trdProductDeals.forEach(trdProductDeal1 -> {
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtils.copyProperties(trdProductDeal1, trdProductDeal);
            trdProductDeal.setIsCancel(DictCodeList.是否_是.getValue());  // 原数据 是否撤销 => 是
            trdProductDeal.setCancelDate(iPlatformDate.getSysDate());//原数据增加撤销日期
            trdProductDealMapper.updateByPrimaryKey(trdProductDeal);
            // 交易状态为撤销
            String oldTradeId = trdProductDeal.getTradeId();
            String newTradeId = trdProductDeal.getTradeId() + "R";
            trdProductDeal1.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue()); // 调用估值接口时，状态是撤销
            trdProductDeal1.setpTradeId(oldTradeId);
            trdProductDeal1.setTradeId(newTradeId);
//            预期交易的汇总交易编号需要加R
            if(StringUtil.isNotEmpty(trdProductDeal.getTotleTradeId())&&!StringUtil.endsWith(trdProductDeal.getTotleTradeId(),"R")
            &&StringUtil.equals(trdProductDeal.getTgBusiType(), DictCodeListExt3.平安业务类型_已到期资产转让.getValue())
            ){
                trdProductDeal.setTotleTradeId(trdProductDeal.getTotleTradeId()+"R");
            }
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
            //update zhangweiqi 2020-02-13 场外期权 汇总已经处理  begin
            if(!StringUtil.equals(trdProductDeal.getBusiType(), DictCodeListExt3.业务类型_场外期权.getValue())){
                List<StlPayOrders> orders = periodCommonMapper.findOrdersByOrderId(oldTradeId);
                // modify by etliu 20220219 start
                // GNS-926: 【广农商场内清算】深交所竞价首期解析SJSMX1文件；重复跑批时，投管交易撤销批处理报错
                // 前面的处理已经做了划款指令的撤销，这里如果出现主键冲突，直接忽略
                try {
                    revokeStlPayOrders(orders);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // modify by etliu 20220219 end
            }
          //update zhangweiqi 2020-02-13 场外期权 汇总已经处理  end
            
            periodCommonMapper.insertRevokePaTrdProductDeal(oldTradeId, updateUser);
            periodCommonMapper.insertRevokeTrdProductDealAdd(oldTradeId, updateUser);

            TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(oldTradeId);
            // 组合交易
            if (trdProductDealAdd != null && StringUtil.isNotEmpty(trdProductDealAdd.getPortfolioId())) {
                PtlTrade deal = new PtlTrade();
                deal.setBusiNo(oldTradeId);
                deal.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                deal.setPortfolioId(trdProductDealAdd.getPortfolioId());
                periodCommonMapper.insertRevokePtlTrade(deal, updateUser);
                if (StringUtil.isNotEmpty(actPortfolio) && !trdProductDealAdd.getPortfolioId().equals(actPortfolio)) {
                    deal.setPortfolioId(actPortfolio);
                    periodCommonMapper.insertRevokePtlTrade(deal, updateUser);
                }

                // 一体化对应etliu start
                portfolioIdMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
             // 一体化对应etliu end
            }

            // modify by etliu 20220219 start
            // GNS-926: 【广农商场内清算】深交所竞价首期解析SJSMX1文件；重复跑批时，投管交易撤销批处理报错
            // 前面的处理已经做了费用明细的撤销，这里如果出现主键冲突，直接忽略
            try {
                // 费用明细
                periodCommonMapper.inertRevokeFeeDetailByTradeId(oldTradeId, updateUser);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            // modify by etliu 20220219 end
            // 删除交易报送
            periodCommonMapper.deleteRepTradeByTradeId(oldTradeId);

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
        if (!StringUtil.isEmptyList(trdProductDeals)&&commonFlag) {
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

            List<TrdProductDeal> trdProductDeals1 = periodCommonMapper.findDealByTotalIdForInterf(map);
            List<TrdProductDealAdd> trdProductDealAdds = periodCommonMapper.findDealAddByTradeIds(trdProductDeals1);
            List<PtlTrade> ptlTrades = periodCommonMapper.findPtlTradeByTradeIds(trdProductDeals1);

            // 所有交易生效后调用的共通方法
            for (TrdProductDeal baseDto : trdProductDeals1) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(portfolioIdMap.get("portfolioId"));
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                子组合估值调用
                inputDto.setActPortfolioId(actPortfolio);
                baseDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                inputDto.setTrdProductDeal(baseDto);
                inputDto.setTrdProductDealAdd(getAdd(trdProductDealAdds, baseDto.getTradeId()));
                inputDto.setRepoType(type6);
                if (ordersTotal != null && !ordersTotal.isEmpty()) {
                    inputDto.setStlPayOrders(ordersTotal.get(0));
                }
                List<PtlTrade> ptlTradeList = new ArrayList<>();
                for (PtlTrade ptlTrade : ptlTrades) {
                    if (baseDto.getTradeId().equals(ptlTrade.getBusiNo())) {
                        ptlTradeList.add(ptlTrade);
                    }
                }
                inputDto.setPtlTradeList(ptlTradeList);
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

                // 调用投管接口 add by renping 20190205 start
                if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue(), finprodType2)) {
                    FinProductTypeKey key = new FinProductTypeKey();
                    key.setFinprodId(baseDto.getFinprodId());
                    key.setBranch(0L);
                    FinProductType finProductType =finProductTypeMapper.selectByPrimaryKey(key);
                        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                    if (StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(),
                        finProductType.getType6())) {
                        investInterfCommDto.setPageId("TrdFundInvestTrade002.02");
                    }
                    else {
                        investInterfCommDto.setPageId("TrdFundInvestTrade002.01");
                    }
                    investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                    investInterfCommDto.setPortfolioId(actPortfolio);
                    investInterfCommDto.setTrdProductDeal(
                        inputDto.getTrdProductDeal());
                    investInterfCommDto.setTrdProductDealAdd(
                        inputDto.getTrdProductDealAdd());
                    investInterfCommDto.setTrdProductDealAdd(
                        inputDto.getTrdProductDealAdd());
                    investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                    // TODO 撤销这个，费用目前没有给，后续再看会不会出问题
                    investInterfCommService.doInvestInterf(investInterfCommDto);
                }
                // 调用投管接口 add by renping 20190205 end
                
                //删除授信占用
                if (null != baseDto.getTradeId() && !"".equals(baseDto.getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(baseDto.getTradeId());
                }
            }
        }
        // 一体化对应etliu end
        
        return trdFinProductDealTotal;
    }

    public TrdProductDealAdd getAdd(List<TrdProductDealAdd> trdProductDealAdds, String tradeId) {
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

    public BigDecimal find最低收盘价(String ccy, String tradeDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("ccy", ccy);
        params.put("tradeDate", tradeDate);
        return periodCommonMapper.findLatestClosingPrice(params);
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
        
        if(baseTradeInfoDto.getTrdDealTotalAdd()!=null){
        	baseTradeInfoDto.getTrdDealTotalAdd().setUpdateUser(updateUser);
        	baseTradeInfoDto.getTrdDealTotalAdd().setUpdateTime(updateTime);
            trdDealTotalAddMapper.insertSelective( baseTradeInfoDto.getTrdDealTotalAdd());
        }

        if (baseTradeInfoDto.getTrdProductDeal() != null) {
            baseTradeInfoDto.getTrdProductDeal().setUpdateUser(updateUser);
            baseTradeInfoDto.getTrdProductDeal().setUpdateTime(updateTime);

            if(StringUtil.equals(baseTradeInfoDto.getTrdProductDeal().getIsClean() , DictCodeList.是否_是.getValue())){
            	Map<String, Object> param = new HashMap<>();
                param.put("finprodId", baseTradeInfoDto.getTrdProductDeal().getFinprodId());
                param.put("invAim", baseTradeInfoDto.getTrdProductDeal().getInvAim());
                param.put("mDate", baseTradeInfoDto.getTrdProductDeal().getMdate());
                param.put("holdingType", DictCodeList.持仓类型_实际.getValue());
                param.put("portfolioId", baseTradeInfoDto.getTrdProductDealAdd().getPortfolioId());
                String maxScAcctNo = periodCommonMapper.findMaxSecAcctNo(param);
                if (maxScAcctNo != null && maxScAcctNo.equals(baseTradeInfoDto.getTrdProductDeal().getSecManageAcctId())) {
                    baseTradeInfoDto.getTrdProductDeal().setIsClean(DictCodeList.是否_是.getValue());
                }
            }
            String chlid = baseTradeInfoDto.getTrdProductDeal().getChlId();
            this.checkChlId(baseTradeInfoDto.getTrdProductDeal(), null);
            //入库时改变了busitype 项目类 计划利率 存款不改变，调用估值时判断使用
            if(StringUtil.equals(baseTradeInfoDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_项目类资产存续期.getValue())
 	     			||StringUtil.equals(baseTradeInfoDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue())
 	     			||StringUtil.equals(baseTradeInfoDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
 	     			||StringUtil.equals(baseTradeInfoDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
 	     			||StringUtil.equals(baseTradeInfoDto.getTrdProductDeal().getBusiType(), DictCodeList.业务类型_逾期交易.getValue())
	     			||StringUtil.equals(baseTradeInfoDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())){
            	TrdProductDeal trdProductDeal = new TrdProductDeal();
                BeanUtils.copyProperties(baseTradeInfoDto.getTrdProductDeal() , trdProductDeal);
                trdProductDealMapper.insertSelective(trdProductDeal);
            }else{
            	trdProductDealMapper.insertSelective(baseTradeInfoDto.getTrdProductDeal());
            }
            baseTradeInfoDto.getTrdProductDeal().setChlId(chlid);
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
            baseTradeInfoDto.getStlPayOrders().setCreateTime(updateTime);
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

                if(StringUtil.equals(baseTrade.getTrdProductDeal().getIsClean() , DictCodeList.是否_是.getValue())){
                	Map<String, Object> param = new HashMap<>();
                    param.put("finprodId", baseTrade.getTrdProductDeal().getFinprodId());
                    param.put("invAim", baseTrade.getTrdProductDeal().getInvAim());
                    param.put("mDate", baseTrade.getTrdProductDeal().getMdate());
                    param.put("holdingType", DictCodeList.持仓类型_实际.getValue());
                    param.put("portfolioId", baseTrade.getTrdProductDealAdd().getPortfolioId());
                    String maxScAcctNo = periodCommonMapper.findMaxSecAcctNo(param);
                    if (maxScAcctNo != null && maxScAcctNo.equals(baseTrade.getTrdProductDeal().getSecManageAcctId())) {
                        baseTrade.getTrdProductDeal().setIsClean(DictCodeList.是否_是.getValue());
                    }
                }
                
                String chlid = baseTrade.getTrdProductDeal().getChlId();
                this.checkChlId(baseTrade.getTrdProductDeal(), null);
                //入库时改变了busitype 项目类 计划利率 存款不改变，调用估值时判断使用
                if(StringUtil.equals(baseTrade.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_项目类资产存续期.getValue())
	 	     			||StringUtil.equals(baseTrade.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue())
	 	     			||StringUtil.equals(baseTrade.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
	 	     			||StringUtil.equals(baseTrade.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
	 	     			||StringUtil.equals(baseTrade.getTrdProductDeal().getBusiType(), DictCodeList.业务类型_逾期交易.getValue())
    	     			||StringUtil.equals(baseTrade.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())){
                	TrdProductDeal trdProductDeal = new TrdProductDeal();
                    BeanUtils.copyProperties(baseTrade.getTrdProductDeal() , trdProductDeal);
                    trdProductDealMapper.insertSelective(trdProductDeal);
                }else{
                	trdProductDealMapper.insertSelective(baseTrade.getTrdProductDeal());
                }
                baseTrade.getTrdProductDeal().setChlId(chlid);
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
        if (baseTotalTradeInfoDto.getStlPayOrders() != null) {
            baseTotalTradeInfoDto.getStlPayOrders().setUpdateUser(updateUser);
            baseTotalTradeInfoDto.getStlPayOrders().setUpdateTime(updateTime);
            baseTotalTradeInfoDto.getStlPayOrders().setCreateTime(updateTime);
            stlPayOrdersMapper.insertSelective(baseTotalTradeInfoDto.getStlPayOrders());
        }
        List<BaseTradeInfoDto> baseTradeInfoDtos = baseTotalTradeInfoDto.getBaseTradeInfoDtoList();
        baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
            doInsertBaseTradeInfo(baseTradeInfoDto,updateUser,updateTime,baseTotalTradeInfoDto.isNeedStlPayOrders());
            
//        还本不带息信息
          if(baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList() != null &&  baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0){
              baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(item->{
//                  不入划款指令
                  doInsertBaseTradeInfo(item,updateUser,updateTime,false);
              });
          }
//        罚息信息
          if(baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList() != null &&  baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().size()>0){
        	  baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().forEach(item->{
//                不入划款指令
        		  doInsertBaseTradeInfo(item,updateUser,updateTime,false);
        	  });
          }
//            处理存续期子组合交易
            if(baseTradeInfoDto.getBaseTradeInfoDtoList() != null &&  baseTradeInfoDto.getBaseTradeInfoDtoList().size()>0){
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(item->{
//                    不入划款指令
                    doInsertBaseTradeInfo(item,updateUser,updateTime,false);
//                  子组合还本不带息信息
                    if(item.getRepaymentUnpayInterestBaseTradeInfoDtoList() != null &&  item.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0){
                    	item.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(item1->{
//                            不入划款指令
                            doInsertBaseTradeInfo(item1,updateUser,updateTime,false);
                        });
                    }
//                  子组合罚息信息
                    if(item.getPenaltyIntBaseTradeInfoDtoList() != null &&  item.getPenaltyIntBaseTradeInfoDtoList().size()>0){
                    	item.getPenaltyIntBaseTradeInfoDtoList().forEach(item1->{
//                          不入划款指令
                  		  doInsertBaseTradeInfo(item1,updateUser,updateTime,false);
                  	  });
                    }
                });
            }
            
        });
        deleteByWorkflowId(workflowId);
    }

    public String doInsert(TrdPlanInterestDurationDto durationDto) {
        BaseTotalTradeInfoDto baseTotalTradeInfoDto = durationDto.getBaseTotalTradeInfoDto();
        if (baseTotalTradeInfoDto.getTrdFinProductDealTotal() != null
                && DictCodeList.交易方向_现金分红.getValue().equals(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getPs())) {
            if(StringUtil.isNotEmpty(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getChlId())){
                String payType = periodCommonMapper.getPayType(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getChlId());
                // 现金分红时，如果有通道，并且通道的划款方式是一次性划款，直接生成有效的现金分红数据
                if (DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType)) {
                    do生成有效数据(durationDto);
                    return DictCodeListExt1.交易流程状态_有效.getValue();
                }
            }
//            通道有值时不为券商资金账户
//            处理和赎回到账一样，现金分红直接生效，不需要在来账分拣进行处理。
            else if (!StringUtil.isEmptyList(baseTotalTradeInfoDto.getBaseTradeInfoDtoList())
                    // add by jiangpeng on 20210524 TISHKBS-2189 逾期债券转仓报空指针
                    && StringUtil.isNotEmpty(baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getTrdProductDeal())
                    // add by jiangpeng on 20210524 TISHKBS-2189 逾期债券转仓报空指针
                    && baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getStlPayOrders()!=null){
                String myAcctNo = baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getStlPayOrders().getMyAcctNo();
                Map<String,Object> paraMap = new HashMap<>();
                paraMap.put("acctNo",myAcctNo);
                paraMap.put("acctType",DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                paraMap.put("portfolioId",baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getStlPayOrders().getPortfolioId());
                paraMap.put("ccy",baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getStlPayOrders().getCcy());
                if(periodCommonMapper.countAccNoNum(paraMap)>0){
                    do生成有效数据(durationDto);
                    return DictCodeListExt1.交易流程状态_有效.getValue();
                }
            }

        }
        //基金支持多种分红方式特殊处理
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.FUND_DEAL_TYPE),"02")){
            if(baseTotalTradeInfoDto !=null){
                if (baseTotalTradeInfoDto.getBaseTradeInfoDtoList() != null
                        // add by jiangpeng on 20210524 TISHKBS-2189 逾期债券转仓报空指针
                        && StringUtil.isNotEmpty(baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getTrdProductDeal())
                        // add by jiangpeng on 20210524 TISHKBS-2189 逾期债券转仓报空指针
                        && StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue(),baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getTrdProductDeal().getFinprodType2())
                        && StringUtil.equals(DictCodeList.交易方向_现金分红.getValue(),baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getTrdProductDeal().getPs())
                        && StringUtil.isNotEmpty(baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getTrdProductDeal().getSecManageAcctId())
                        && baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getStlPayOrders()!=null) {
                    String myAcctNo = baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getStlPayOrders().getMyAcctNo();
                    Map<String,Object> paraMap = new HashMap<>();
                    paraMap.put("acctNo",myAcctNo);
                    paraMap.put("acctType",DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                    paraMap.put("portfolioId",baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getStlPayOrders().getPortfolioId());
                    paraMap.put("ccy",baseTotalTradeInfoDto.getBaseTradeInfoDtoList().get(0).getStlPayOrders().getCcy());
                    if(periodCommonMapper.countAccNoNum(paraMap)>0){
                        do生成有效数据(durationDto);
                        return DictCodeListExt1.交易流程状态_有效.getValue();
                    }
                }
            }
        }
        insertWflData(baseTotalTradeInfoDto);
        return DictCodeListExt1.交易流程状态_流程中.getValue();
    }

    public String insertWflData(BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtils.copyProperties(baseTotalTradeInfoDto.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal);
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtils.copyProperties(baseTotalTradeInfoDto.getPaTrdFinProductDealTotal(), wflPaTrdFinDealTotal);
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);

        if(baseTotalTradeInfoDto.getTrdDealTotalAdd()!=null){
            WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
            BeanUtils.copyProperties(baseTotalTradeInfoDto.getTrdDealTotalAdd(),wflTrdDealTotalAdd);
            wflTrdDealTotalAdd.setWorkflowId(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId());
            wflTrdDealTotalAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
        }
        if(baseTotalTradeInfoDto.getStlPayOrders()!= null){
            WflStlPayOrders orders = new WflStlPayOrders();
            BeanUtils.copyProperties(baseTotalTradeInfoDto.getStlPayOrders(), orders);
            orders.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            orders.setWorkflowId(wflTrdFinProductDealTotal.getWorkflowId());
            wflStlPayOrdersMapper.insertSelective(orders);
        }
        baseTotalTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
            insertBaseTotalTadeInfo(baseTradeInfoDto,baseTotalTradeInfoDto.isNeedStlPayOrders(), wflTrdFinProductDealTotal.getWorkflowId());
            //还本不带息信息
            if(baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList()!= null && baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0 ){
                baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto->{
                    insertBaseTotalTadeInfo(subBaseTradeInfoDto,false, wflTrdFinProductDealTotal.getWorkflowId());
                });
            }
            
			//罚息信息
			if (baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList() != null
					&& baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().size() > 0) {
				baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto -> {
					insertBaseTotalTadeInfo(subBaseTradeInfoDto, false, wflTrdFinProductDealTotal.getWorkflowId());
				});
			}
			//子组合信息
            if(baseTradeInfoDto.getBaseTradeInfoDtoList()!= null && baseTradeInfoDto.getBaseTradeInfoDtoList().size()>0 ){
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto->{
                    insertBaseTotalTadeInfo(subBaseTradeInfoDto,false, wflTrdFinProductDealTotal.getWorkflowId());
                  //子组合还本不带息信息
                    if(subBaseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList()!= null && subBaseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0 ){
                    	subBaseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto1->{
                            insertBaseTotalTadeInfo(subBaseTradeInfoDto1,false, wflTrdFinProductDealTotal.getWorkflowId());
                        });
                    }
                  //子组合罚息信息
        			if (subBaseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList() != null
        					&& subBaseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().size() > 0) {
        				subBaseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto1 -> {
        					insertBaseTotalTadeInfo(subBaseTradeInfoDto1, false, wflTrdFinProductDealTotal.getWorkflowId());
        				});
        			}
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
        
        if(baseTradeInfoDto.getTrdDealTotalAdd()!=null){
            WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
            BeanUtils.copyProperties(baseTradeInfoDto.getTrdDealTotalAdd(),wflTrdDealTotalAdd);
            wflTrdDealTotalAdd.setWorkflowId(baseTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId());
            wflTrdDealTotalAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
        }

        if (baseTradeInfoDto.getTrdProductDeal() != null) {
            WflTrdProductDeal deal = new WflTrdProductDeal();
            BeanUtils.copyProperties(baseTradeInfoDto.getTrdProductDeal(), deal);
            deal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            deal.setWorkflowId(workflowId);
            String chlid = baseTradeInfoDto.getTrdProductDeal().getChlId();
            this.checkChlId(null, deal);
            wflTrdProductDealMapper.insert(deal);
            deal.setChlId(chlid);
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
            String chlid = baseTradeInfoDto.getBaseTrade().getTrdProductDeal().getChlId();
            this.checkChlId(null,deal1);
            wflTrdProductDealMapper.insert(deal1);
            deal1.setChlId(chlid);
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
    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinDealTotalMapper;

    private void do生成有效数据(TrdPlanInterestDurationDto durationDto) {
        BaseTotalTradeInfoDto baseTotalTradeInfoDto = durationDto.getBaseTotalTradeInfoDto();
        if (baseTotalTradeInfoDto.getTrdFinProductDealTotal() != null) {
            trdFinProductDealTotalMapper.insertSelective(baseTotalTradeInfoDto.getTrdFinProductDealTotal());
        }

        if (baseTotalTradeInfoDto.getPaTrdFinProductDealTotal() != null) {
            paTrdFinDealTotalMapper.insertSelective(baseTotalTradeInfoDto.getPaTrdFinProductDealTotal());
        }

        if (baseTotalTradeInfoDto.getBaseTradeInfoDtoList() != null && !baseTotalTradeInfoDto.getBaseTradeInfoDtoList().isEmpty()) {
            baseTotalTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
                if (baseTradeInfoDto.getTrdFinProductDealTotal() != null) {
                    trdFinProductDealTotalMapper.insertSelective(baseTradeInfoDto.getTrdFinProductDealTotal());
                }
                if (baseTradeInfoDto.getPaTrdFinProductDealTotal() != null) {
                    paTrdFinDealTotalMapper.insertSelective(baseTradeInfoDto.getPaTrdFinProductDealTotal());
                }

                if (baseTradeInfoDto.getTrdProductDeal() != null) {
                    trdProductDealMapper.insertSelective(baseTradeInfoDto.getTrdProductDeal());
                }

                if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
                    paTrdProductDealMapper.insertSelective(baseTradeInfoDto.getPaTrdProductDeal());
                }

                if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
                    trdProductDealAddMapper.insertSelective(baseTradeInfoDto.getTrdProductDealAdd());
                }

                if (baseTotalTradeInfoDto.isNeedStlPayOrders() && baseTradeInfoDto.getStlPayOrders() != null) {
                    stlPayOrdersMapper.insertSelective(baseTradeInfoDto.getStlPayOrders());
                }

                if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
                    trdProductDealMapper.insertSelective(baseTradeInfoDto.getBaseTrade().getTrdProductDeal());
                }

                if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal() != null) {
                    paTrdProductDealMapper.insertSelective(baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal());
                }

                if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd() != null) {
                    trdProductDealAddMapper.insertSelective(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd());
                }
//                添加没有维护组合交易表的情况
                if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getPtlTrade() != null) {
                    baseTradeInfoDto.getBaseTrade().getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
                }
            });
        }

        String updateUser = durationDto.getUpdateUser();
        Date updateTime = new Date();

        List<BaseTradeInfoDto> baseTradeInfoDtos = durationDto.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList();
        if (baseTradeInfoDtos != null && !baseTradeInfoDtos.isEmpty()) {
            baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
                String portfolioId = baseTradeInfoDto.getTrdProductDealAdd() != null
                        ? baseTradeInfoDto.getTrdProductDealAdd().getPortfolioId()
                        : baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd().getPortfolioId();
                String 产品登记编码 = periodCommonMapper.selectProdRegistCode(portfolioId,
                        DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
                if (StringUtil.isNotEmpty(产品登记编码)) {
                    if (baseTradeInfoDto.getTrdProductDeal() != null) {
                        insertRep(durationDto, updateUser, updateTime, baseTradeInfoDto.getTrdProductDeal(), 产品登记编码);
                    }
                    if (baseTradeInfoDto.getBaseTrade() != null
                            && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
                        insertRep(durationDto, updateUser, updateTime,
                                baseTradeInfoDto.getBaseTrade().getTrdProductDeal(), 产品登记编码);
                    }
                }
//                                    一体化调用 etliu add start
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(portfolioId);
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(baseTradeInfoDto.getTrdProductDeal());
                inputDto.setTrdProductDealAdd(baseTradeInfoDto.getTrdProductDealAdd());
                inputDto.setPtlTradeList(baseTradeInfoDto.getPtlTrade());
                inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//                                  一体化调用 etliu add end

                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto1 -> {
                    AllTradeCommonInputDto inputDto1 = new AllTradeCommonInputDto();
                    inputDto1.setProdid(baseTradeInfoDto1.getTrdProductDealAdd().getPortfolioId());
                    inputDto1.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                    inputDto1.setTrdProductDeal(baseTradeInfoDto1.getTrdProductDeal());
                    inputDto1.setTrdProductDealAdd(baseTradeInfoDto1.getTrdProductDealAdd());
                    inputDto1.setPtlTradeList(baseTradeInfoDto1.getPtlTrade());
                    inputDto1.setStlPayOrders(baseTradeInfoDto1.getStlPayOrders());
                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto1);
                });
            });
        }
    }

    public void insertRep(TrdPlanInterestDurationDto durationDto, String updateUser, Date updateTime, TrdProductDeal deal,
                           String 产品登记编码) {
        List<RepTrade> repTrades = gen交易报送信息(durationDto, deal, 产品登记编码);
        repTrades.forEach(repTrade -> {
            repTrade.setCreateUser(durationDto.getCreateUser());
            repTrade.setCreateTime(durationDto.getCreateTime());
            repTrade.setCreateDept(durationDto.getCreateDept());
            repTrade.setUpdateUser(updateUser);
            repTrade.setUpdateTime(updateTime);
            repTradeMapper.insertSelective(repTrade);
        });
    }

    private List<RepTrade> gen交易报送信息(TrdPlanInterestDurationDto entity, TrdProductDeal deal, String 产品登记编码) {
        List<RepTrade> result = new ArrayList<>();
        // 产品登记编码
        Map<String, Object> params = new HashMap<>();
        BigDecimal 最低收盘价 = BigDecimal.ONE;
        if (!DictCodeList.币种_人民币_CNY.getValue().equals(deal.getCcy())) {
            最低收盘价 = find最低收盘价(deal.getCcy(), entity.getArrivalDate());
        }
        // 金融机构中文名(交易对手)
        String institutionName = periodCommonMapper.findInstitutionName(entity.getFinprodId());
        BigDecimal 最低收盘价F = 最低收盘价;
        // 折算人民币金额（元）
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(产品登记编码);
        repTrade.setRepIssuePartyId(entity.getIssuePartyId());
        repTrade.setRepBankTradeCode(deal.getTradeId());
        repTrade.setRepBankAssetCode(entity.getFinAssetInfo().getFinprodMarketId());
        repTrade.setRepCcy(deal.getCcy());
        repTrade.setRepCount(deal.getShareAmt());
        repTrade.setRepTradeDate(DateUtil.parse(entity.getArrivalDate()));
        repTrade.setRepDelCode(deal.getTradeId());
        repTrade.setRepRemark(entity.getRemark());
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
        repTrade.setRepStatusDate(platformDateImpl.getSysDate());
        repTrade.setRepUnitPriceNet(deal.getUnitCprice());
        repTrade.setRepUnitPriceFull(deal.getUnitFprice());
        repTrade.setRepTradeComponent(institutionName);
        if (DictCodeList.交易方向_到期.getValue().equals(deal.getStatus()) || DictCodeList.交易方向_还本.getValue().equals(deal.getStatus())) {
            repTrade.setRepOccurMoney(deal.getTradeAmt());
            repTrade.setRepMoneyCny(ArithmeticUtil.multiply(deal.getTradeAmt(), 最低收盘价F));
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
            if (DictCodeList.交易方向_到期.getValue().equals(deal.getStatus())) {
                repTrade.setRepMoneyFlowTypeExp("到期还本");
            }
            else if (DictCodeList.交易方向_还本.getValue().equals(deal.getStatus())) {
                repTrade.setRepMoneyFlowTypeExp("中间还本");
            }
            repTrade.setRepUnitPriceNet(new BigDecimal(100));
            repTrade.setRepUnitPriceFull(new BigDecimal(100));
            repTrade.setRepCount(deal.getTradeAmt());
        }
        else if (DictCodeList.交易方向_收息.getValue().equals(deal.getStatus())) {
            repTrade.setRepOccurMoney(deal.getPrinAmt());
            repTrade.setRepMoneyCny(ArithmeticUtil.multiply(deal.getPrinAmt(), 最低收盘价F));
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产所得收益.getValue());
            repTrade.setRepMoneyFlowTypeExp("利息收入");
        }
        result.add(repTrade);
        return result;
    }

    public void deleteByWorkflowId(String workflowId) {
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdDealTotalAddMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflPaTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);
        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
    }

    public void insertWflTable(Map<String, Object> params) {
        List<String> subBizCodes = periodCommonMapper.findSubTotalTradeId((String) params.get("bizCode"));
        if (subBizCodes != null && !subBizCodes.isEmpty()) {
            for (String subBizCode : subBizCodes) {
                Map<String, Object> params1 = new HashMap<>();
                params1.putAll(params);
                params1.put("bizCode", subBizCode);
                doRevokeDeal(params1);
            }
        }
        doRevokeDeal(params);
    }

    private void doRevokeDeal(Map<String, Object> params) {
        params.put("REVOKE_TRADE_STATUS", DictCodeListExt1.交易状态_撤销.getValue());
        // 汇总交易表
        periodCommonMapper.insertWflTotle(params);
        // 汇总扩展表
        periodCommonMapper.insertWflPaTotal(params);

        periodCommonMapper.insertWflTradeTotalAdd(params);
        params.put("ORDER_STATUS_03", DictCodeListExt1.指令状态_撤销.getValue());
        // 划款指令表
        periodCommonMapper.insertWflPayOrders(params);
        // 修改参数
        params.put("totleTradeId", params.get("bizCode"));
        List<TrdProductDeal> deals = periodCommonMapper.findDealByTotalId(params);
        if (!deals.isEmpty()) {
            params.put("deals", deals);
            // 交易表
            periodCommonMapper.insertWflTrade(params);
            // 交易扩展表
            periodCommonMapper.insertWflPaTrade(params);
            // 交易附属表
            periodCommonMapper.insertWflTradeAdd(params);
            // 非汇总指令时，撤销交易对应的划款指令 add by renping 2019-09-10 start
            // 划款指令表
            periodCommonMapper.insertWflPayOrdersOfDeal(params);
            // 非汇总指令时，撤销交易对应的划款指令 add by renping 2019-09-10 end
        }
    }

    /**
     * 根据组合信息按占比分配金额
     * @param preMap 待分配的金额 key值必须与dto字段名一致
     * @param paramMap 查询参数
     * @return 分配结果
     */
    public List<FinAssetIntInfo>distributeDataByPercent(Map<String,BigDecimal> preMap, Map<String,Object> paramMap, List<FinAssetIntInfo> portfolioListInfo){
        String subAconShareAmt = null;//基金折算
        if(StringUtil.isNotEmpty(paramMap.get("subAconShareAmt"))){
        	subAconShareAmt=paramMap.get("subAconShareAmt").toString();
        }
        //如果没有提供子组合持仓信息则查询子组合持仓信息
        if(portfolioListInfo==null || portfolioListInfo.size()==0){
            paramMap.put("POSITION_TYPE03", BussinessDictCodeList.持仓类型_实际.getValue());
//            处理日期
            paramMap.put("dealDate",DateUtil.parse((String) paramMap.get("dealDate")));
            if(!paramMap.containsKey("bizCode")){
                paramMap.put("bizCode","");
            }
            String bizCode = (String)paramMap.get("bizCode");
            if(bizCode!= null && bizCode.endsWith("R")){
                paramMap.put("bizCode",bizCode.substring(0,bizCode.length()-1));
                if(StringUtil.isNotEmpty(paramMap.get("workflowId"))){
                    paramMap.remove("workflowId");
                }
            }
            paramMap.put("YON_N", BussinessDictCodeList.是否_否.getValue());
            paramMap.put("ps", DictCodeList.交易方向_应收款项转出.getValue());
            portfolioListInfo = periodCommonMapper.getPortfolioListInfo(paramMap);//代办
            if(portfolioListInfo == null || portfolioListInfo.size()==0 && StringUtil.isNotEmpty(paramMap.get("workflowId"))){//流程中
                paramMap.remove("workflowId");
                portfolioListInfo = periodCommonMapper.getPortfolioListInfo(paramMap);
            }
            if(portfolioListInfo == null || portfolioListInfo.size()==0 && StringUtil.isNotEmpty(paramMap.get("bizCode"))){//生效
                paramMap.remove("bizCode");
                portfolioListInfo = periodCommonMapper.getPortfolioListInfo(paramMap);
            }
            //如果查询结果为空则直接返回null
            if(portfolioListInfo == null || portfolioListInfo.size()==0){
                return  new ArrayList<>();
            }
        }
//      按照持仓排序
        if(StringUtil.isBlank(subAconShareAmt)){
            portfolioListInfo.sort( Comparator.comparing(FinAssetIntInfo::getAmount));
        }else{
            portfolioListInfo.sort( Comparator.comparing(FinAssetIntInfo::getBconShareAmt));//折算后金额排序
        }
//        总持仓
        BigDecimal sumAmount = BigDecimal.ZERO;
        for(FinAssetIntInfo item:portfolioListInfo){
            sumAmount = ArithmeticUtil.doAdd(sumAmount,item.getAmount());
        }
//        初始化已分配
        Map<String,BigDecimal>sumMap = new HashMap<>();
        for(String key:preMap.keySet()){
            sumMap.put(key,BigDecimal.ZERO);
        }
        Class clzz = FinAssetIntInfo.class;
        for (int i = 0; i < portfolioListInfo.size(); i++) {
            FinAssetIntInfo temp = portfolioListInfo.get(i);
            try{
//              最后一个
                if (i == portfolioListInfo.size() - 1) {
                    for(String key:preMap.keySet()){
                        Field fieldT = clzz.getDeclaredField(key);
                        fieldT.setAccessible(true);
                        fieldT.set(temp,ArithmeticUtil.subtract(preMap.get(key),sumMap.get(key)));
                    }
                }
//        	    非最后一个
                else{
//                  计算占比
                    BigDecimal prop  = ArithmeticUtil.divide(temp.getAmount(),sumAmount,13, RoundingMode.HALF_UP);
//                    根据占比处理数值
                    for(String key:preMap.keySet()){
                        Field fieldT = clzz.getDeclaredField(key);
                        fieldT.setAccessible(true);
//                        计算这次需要的数值
                        BigDecimal thisNum = ArithmeticUtil.multiply( preMap.get(key),prop).setScale(12, RoundingMode.HALF_UP);
//        			处理已分配的钱
                        sumMap.put(key,ArithmeticUtil.doAdd(sumMap.get(key),thisNum));
//                   把数据存入dto
                        fieldT.set(temp,thisNum);
                    }
                }
            }
            catch (Exception e){
                logger.error(e.getMessage(),e);
            }
        }
        return portfolioListInfo;
    }
    
    
    
    /**
     * 项目类，计划类利率，存款，入库前过滤通道信息
     * TrdProductDeal 生效时传入，否则传空。WflTrdProductDeal 流程中传入，否则传空
     * @param trdProductDeal
     */
    private void checkChlId(TrdProductDeal trdProductDeal,WflTrdProductDeal wflTrdProductDeal){
        //modify by mashukai TISHKBS-1664 当计划类项目类资产通道投资模式开关为02时，通道代码不置位空 20201208 start
        if(trdProductDeal!=null){
            if((StringUtil.equals(trdProductDeal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                    ||StringUtil.equals(trdProductDeal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue()))
                    &&(StringUtil.equals(trdProductDeal.getBusiType(), DictCodeList.共通业务类型_项目类资产存续期.getValue())
                    ||StringUtil.equals(trdProductDeal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue())
                    ||StringUtil.equals(trdProductDeal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
                    ||StringUtil.equals(trdProductDeal.getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
                    ||StringUtil.equals(trdProductDeal.getBusiType(), DictCodeList.业务类型_逾期交易.getValue()))
                    && "01".equals(CacheUtil.getSysParam("CHL_MODEL_TYPE"))){
                trdProductDeal.setChlId(null);
            }else if(StringUtil.equals(trdProductDeal.getFinprodType2(),  DictCodeListExt1.金融产品类型_投管用_存款.getValue())){
                //01 借款利率 不需要处理通道信息,  否则置空
                if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
                    trdProductDeal.setChlId(null);
                }
            }
        }else if (wflTrdProductDeal!=null){
            if((StringUtil.equals(wflTrdProductDeal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                    ||StringUtil.equals(wflTrdProductDeal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue()))
                    &&(StringUtil.equals(wflTrdProductDeal.getBusiType(), DictCodeList.共通业务类型_项目类资产存续期.getValue())
                    ||StringUtil.equals(wflTrdProductDeal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue())
                    ||StringUtil.equals(wflTrdProductDeal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
                    ||StringUtil.equals(wflTrdProductDeal.getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
                    ||StringUtil.equals(wflTrdProductDeal.getBusiType(), DictCodeList.业务类型_逾期交易.getValue()))
                    && "01".equals(CacheUtil.getSysParam("CHL_MODEL_TYPE"))){
                wflTrdProductDeal.setChlId(null);
            }else if(StringUtil.equals(wflTrdProductDeal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())){
                //01 借款利率 不需要处理通道信息,  否则置空
                if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
                    wflTrdProductDeal.setChlId(null);
                }
            }
        }
        //modify by mashukai TISHKBS-1664 当计划类项目类资产通道投资模式开关为02时，通道代码不置位空 20201208 end
    }


    /**
     *
     * 撤銷相關交易表
     */
    public TrdFinProductDealTotal revokeReverseTradeTable(String bizCode, String updateUser, String revokeReason, String type6,
                                                 String actPortfolio,String tradeDealNoR) {
        List<String> subBizCodes = periodCommonMapper.findSubTotalTradeId(bizCode);
        if (subBizCodes != null && !subBizCodes.isEmpty()) {
            for (String subBizCode : subBizCodes) {
                revokeReverseTradeTable(subBizCode, updateUser, true, revokeReason,type6, actPortfolio,true,tradeDealNoR);
            }
        }
        return revokeReverseTradeTable(bizCode, updateUser, true, revokeReason,type6, actPortfolio,true,tradeDealNoR);
    }

    /**
     *
     * @param bizCode
     * @param updateUser
     * @param isTotal
     * @param revokeReason
     * @param type6
     * @param actPortfolio
     * @param commonFlag
     * @param tradeDealNoR  用于后续扩展  如果有某个交易 某字段有问题  可通过传此字段扩展
     * @return
     */
    public TrdFinProductDealTotal revokeReverseTradeTable(String bizCode, String updateUser, boolean isTotal, String revokeReason, String type6,
                                                 String actPortfolio,boolean commonFlag,String tradeDealNoR) {
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
            if (trdFinProductDealTotal != null) {
                // 原数据 是否撤销 => 是
                trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());

                //多级法人改造 add by xr 20200711
                if(MultilevelDataAuthorityUtil.isEnabled()){
                    // 机构代码
                    trdFinProductDealTotal.setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(updateUser));
                }
                //多级法人改造 add by xr  20200711
                trdFinProductDealTotal.setUpdateTime(updateTime);
                trdFinProductDealTotal.setUpdateUser(updateUser);
                trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);
                // 撤销交易状态为撤销
                trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                String newTotalTradeId = trdFinProductDealTotal.getTotleTradeId() + "R";
//                trdFinProductDealTotal.setpTradeId(trdFinProductDealTotal.getTotleTradeId());
                trdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
//                    trdFinProductDealTotal.setpTotleTradeId(trdFinProductDealTotal.getpTotleTradeId() + "R");
                trdFinProductDealTotal.setpTotleTradeId(trdFinProductDealTotal.getTotleTradeId() );


                // 是否撤销入否
                trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
                trdFinProductDealTotal.setRemark(revokeReason);
                trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

                PaTrdFinProductDealTotal paTrdFinProductDealTotal = paTrdFinProductDealTotalMapper.selectByPrimaryKey(bizCode);
                if (paTrdFinProductDealTotal != null) {
                    paTrdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
                    paTrdFinProductDealTotal.setUpdateTime(updateTime);
                    paTrdFinProductDealTotal.setUpdateUser(updateUser);
                    paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
                }

                TrdDealTotalAdd trdDealTotalAdd = trdDealTotalAddMapper.selectByPrimaryKey(bizCode);
                /** add by yanya on 20191116 修复债券存续期撤销取不到值报空错误**/
                if (trdDealTotalAdd != null) {
                    trdDealTotalAdd.setTotleTradeId(newTotalTradeId);
                    trdDealTotalAdd.setUpdateTime(updateTime);
                    trdDealTotalAdd.setUpdateUser(updateUser);
                    trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
                }
            }
        }
        // 划款指令
        List<StlPayOrders> ordersTotal = periodCommonMapper.findOrdersByOrderId(bizCode);
        revokeStlPayOrders(ordersTotal);
        // 费用明细
        periodCommonMapper.inertRevokeFeeDetailByTradeId(bizCode, updateUser);
        // 费用汇总
        periodCommonMapper.inertRevokeFeeTotalByTradeId(bizCode, updateUser);
        // 金融产品交易Entity
        // 增加无汇总交易的场景 无汇总交易使用交易编号关联
        List<TrdProductDeal> trdProductDeals = periodCommonMapper.findDealByTotalId(map);

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
            trdProductDeal1.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue()); // 调用估值接口时，状态是撤销
            trdProductDeal1.setpTradeId(oldTradeId);
            trdProductDeal1.setTradeId(newTradeId);

            trdProductDeal.setTotleTradeId(trdProductDeal.getTotleTradeId()+"R");

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
            //update zhangweiqi 2020-02-13 场外期权 汇总已经处理  begin
            if(!StringUtil.equals(trdProductDeal.getBusiType(), DictCodeListExt3.业务类型_场外期权.getValue())){
                List<StlPayOrders> orders = periodCommonMapper.findOrdersByOrderId(oldTradeId);
                revokeStlPayOrders(orders);
            }
            //update zhangweiqi 2020-02-13 场外期权 汇总已经处理  end

            periodCommonMapper.insertRevokePaTrdProductDeal(oldTradeId, updateUser);
            periodCommonMapper.insertRevokeTrdProductDealAdd(oldTradeId, updateUser);

            TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(oldTradeId);
            // 组合交易
            if (trdProductDealAdd != null && StringUtil.isNotEmpty(trdProductDealAdd.getPortfolioId())) {
                PtlTrade deal = new PtlTrade();
                deal.setBusiNo(oldTradeId);
                deal.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                deal.setPortfolioId(trdProductDealAdd.getPortfolioId());
                periodCommonMapper.insertRevokePtlTrade(deal, updateUser);
                if (StringUtil.isNotEmpty(actPortfolio) && !trdProductDealAdd.getPortfolioId().equals(actPortfolio)) {
                    deal.setPortfolioId(actPortfolio);
                    periodCommonMapper.insertRevokePtlTrade(deal, updateUser);
                }

                // 一体化对应etliu start
                portfolioIdMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
                // 一体化对应etliu end
            }

            // 费用明细
            periodCommonMapper.inertRevokeFeeDetailByTradeId(oldTradeId, updateUser);
            // 删除交易报送
            periodCommonMapper.deleteRepTradeByTradeId(oldTradeId);

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
        if (!StringUtil.isEmptyList(trdProductDeals)&&commonFlag) {
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

            List<TrdProductDeal> trdProductDeals1 = periodCommonMapper.findDealByTotalIdForInterf(map);
            List<TrdProductDealAdd> trdProductDealAdds = periodCommonMapper.findDealAddByTradeIds(trdProductDeals1);
            List<PtlTrade> ptlTrades = periodCommonMapper.findPtlTradeByTradeIds(trdProductDeals1);

            // 所有交易生效后调用的共通方法
            for (TrdProductDeal baseDto : trdProductDeals1) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(portfolioIdMap.get("portfolioId"));
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                子组合估值调用
                inputDto.setActPortfolioId(actPortfolio);
                baseDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                inputDto.setTrdProductDeal(baseDto);
                inputDto.setTrdProductDealAdd(getAdd(trdProductDealAdds, baseDto.getTradeId()));
                inputDto.setRepoType(type6);
                if (ordersTotal != null && !ordersTotal.isEmpty()) {
                    inputDto.setStlPayOrders(ordersTotal.get(0));
                }
                List<PtlTrade> ptlTradeList = new ArrayList<>();
                for (PtlTrade ptlTrade : ptlTrades) {
                    if (baseDto.getTradeId().equals(ptlTrade.getBusiNo())) {
                        ptlTradeList.add(ptlTrade);
                    }
                }
                inputDto.setPtlTradeList(ptlTradeList);
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

                // 调用投管接口 add by renping 20190205 start
                if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue(), finprodType2)) {
                    FinProductTypeKey key = new FinProductTypeKey();
                    key.setFinprodId(baseDto.getFinprodId());
                    key.setBranch(0L);
                    FinProductType finProductType =finProductTypeMapper.selectByPrimaryKey(key);
                    InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                    if (StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(),
                            finProductType.getType6())) {
                        investInterfCommDto.setPageId("TrdFundInvestTrade002.02");
                    }
                    else {
                        investInterfCommDto.setPageId("TrdFundInvestTrade002.01");
                    }
                    investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                    investInterfCommDto.setPortfolioId(actPortfolio);
                    investInterfCommDto.setTrdProductDeal(
                            inputDto.getTrdProductDeal());
                    investInterfCommDto.setTrdProductDealAdd(
                            inputDto.getTrdProductDealAdd());
                    investInterfCommDto.setTrdProductDealAdd(
                            inputDto.getTrdProductDealAdd());
                    investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                    // TODO 撤销这个，费用目前没有给，后续再看会不会出问题
                    investInterfCommService.doInvestInterf(investInterfCommDto);
                }
                // 调用投管接口 add by renping 20190205 end

                //删除授信占用
                if (null != baseDto.getTradeId() && !"".equals(baseDto.getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(baseDto.getTradeId());
                }
            }
        }
        // 一体化对应etliu end

        return trdFinProductDealTotal;
    }

    public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser, boolean isTotal, String revokeReason, String type6,
            String actPortfolio,boolean commonFlag, BigDecimal earlyWithdrawalAmt) {
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
    	String tradeId = "";
    	TrdFinProductDealTotal trdFinProductDealTotal = null;
    	if (isTotal) {
    		// 金融产品交易汇总
    		trdFinProductDealTotal = trdFinProductDealTotalMapper.selectByPrimaryKey(bizCode);
    		if (trdFinProductDealTotal != null) {
    			// 撤销tradeId
    			tradeId = trdFinProductDealTotal.getpTotleTradeId();
    			if (DictCodeListExt1.金融产品类型_存款.getValue().equals(trdFinProductDealTotal.getFinprodType2())){
    				tradeId = tradeId + "_" + trdFinProductDealTotal.getPortfolioId();
    			}
    			// 存款提前支取tradeId
    			if(StringUtil.isNotEmpty(earlyWithdrawalAmt)){
    				tradeId = trdFinProductDealTotal.getTotleTradeId() + "_" + trdFinProductDealTotal.getPortfolioId();
    			}
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
    			if (paTrdFinProductDealTotal != null) {
    				paTrdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
    				paTrdFinProductDealTotal.setUpdateTime(updateTime);
    				paTrdFinProductDealTotal.setUpdateUser(updateUser);
    				paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
    			}

    			TrdDealTotalAdd trdDealTotalAdd = trdDealTotalAddMapper.selectByPrimaryKey(bizCode);
    			/** add by yanya on 20191116 修复债券存续期撤销取不到值报空错误**/
    			if (trdDealTotalAdd != null) {
    				trdDealTotalAdd.setTotleTradeId(newTotalTradeId);
    				trdDealTotalAdd.setUpdateTime(updateTime);
    				trdDealTotalAdd.setUpdateUser(updateUser);
    				trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
    			}
    		}
    	}
    	// 划款指令
    	List<StlPayOrders> ordersTotal = periodCommonMapper.findOrdersByOrderId(bizCode);
    	revokeStlPayOrders(ordersTotal);
    	// 费用明细
    	periodCommonMapper.inertRevokeFeeDetailByTradeId(bizCode, updateUser);
    	// 费用汇总
    	periodCommonMapper.inertRevokeFeeTotalByTradeId(bizCode, updateUser);
    	// 金融产品交易Entity
    	// 增加无汇总交易的场景 无汇总交易使用交易编号关联
    	List<TrdProductDeal> trdProductDeals = periodCommonMapper.findDealByTotalId(map);

    	// 一体化对应etliu start
    	Map<String, String> portfolioIdMap = new HashMap<String, String>();
    	// 一体化对应etliu end

    	for (TrdProductDeal trdProductDeal1 : trdProductDeals) {
    		//授信占用
    		if (DictCodeList.交易方向_还本到账.getValue().equals(trdProductDeal1.getPs())){
    			// 调用授信共通
    			TrdProductDeal trdProductDeal = new TrdProductDeal();
    			BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trdProductDeal1);
    			AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
    			trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
    			trdProductDeal.setTradeId(tradeId);
    			trdProductDeal.setPs(DictCodeList.交易方向_还本.getValue());
    			inputDto.setTrdProductDeal(trdProductDeal);
    			AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
    		}
    		//授信占用 存款提前支取
    		if (StringUtil.isNotEmpty(earlyWithdrawalAmt)){
    			// 调用授信共通
    			TrdProductDeal trdProductDeal = new TrdProductDeal();
    			BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trdProductDeal1);
    			AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
    			trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
    			trdProductDeal.setTradeId(tradeId);
    			trdProductDeal.setPs(DictCodeList.交易方向_提前支取.getValue());
    			trdProductDeal.setPrinAmt(earlyWithdrawalAmt);
    			FinProduct finProduct = finProductMapper.selectByPrimaryKey(trdProductDeal.getFinprodId());
    			trdProductDeal.setCounterId(finProduct.getIssuer());
    			inputDto.setTrdProductDeal(trdProductDeal);
    			AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
    		}
    		if (DictCodeList.交易方向_到期.getValue().equals(trdProductDeal1.getPs())
    				&& DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(trdProductDeal1.getFinprodType2())) {
    			// 调用授信共通
    			TrdProductDeal trdProductDeal = new TrdProductDeal();
    			BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trdProductDeal1);
    			AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
    			trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
    			trdProductDeal.setTradeId(trdProductDeal.getTradeId());
    			trdProductDeal.setPs(DictCodeList.交易方向_还本.getValue());
    			trdProductDeal.setCounterId(trdFinProductDealTotal.getCounterId());
    			inputDto.setTrdProductDeal(trdProductDeal);
    			AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
    		}
    		TrdProductDeal trdProductDeal = new TrdProductDeal();
    		BeanUtils.copyProperties(trdProductDeal1, trdProductDeal);
    		trdProductDeal.setIsCancel(DictCodeList.是否_是.getValue());
    		// 原数据 是否撤销 => 是
    		trdProductDealMapper.updateByPrimaryKey(trdProductDeal);
    		// 交易状态为撤销
    		String oldTradeId = trdProductDeal.getTradeId();
    		String newTradeId = trdProductDeal.getTradeId() + "R";
    		trdProductDeal1.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue()); // 调用估值接口时，状态是撤销
    		trdProductDeal1.setpTradeId(oldTradeId);
    		trdProductDeal1.setTradeId(newTradeId);
    		//预期交易的汇总交易编号需要加R
    		if(StringUtil.isNotEmpty(trdProductDeal.getTotleTradeId())&&!StringUtil.endsWith(trdProductDeal.getTotleTradeId(),"R")
    				&&StringUtil.equals(trdProductDeal.getTgBusiType(), DictCodeListExt3.平安业务类型_已到期资产转让.getValue())
    				){
    			trdProductDeal.setTotleTradeId(trdProductDeal.getTotleTradeId()+"R");
    		}
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
    		//update zhangweiqi 2020-02-13 场外期权 汇总已经处理  begin
    		if(!StringUtil.equals(trdProductDeal.getBusiType(), DictCodeListExt3.业务类型_场外期权.getValue())){
    			// add by zhangliang 20200903 结算下行调试发现明细交易编号对应的划款指令被重复撤销了两次，上面已经调了一次revokeStlPayOrders 此处判断是否重复  start
    			List<StlPayOrders> orders = periodCommonMapper.findOrdersByOrderId(oldTradeId);
    			if (!StringUtil.isEmptyList(orders)){
    				List<String> orderList = ordersTotal.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
    				List<StlPayOrders> newOrders = orders.stream().filter(x -> !orderList.contains(x.getOrderId() + "R")).collect(Collectors.toList());
    				revokeStlPayOrders(newOrders);
    			}
    			// add by zhangliang 20200903 结算下行调试发现明细交易编号对应的划款指令被重复撤销了两次，上面已经调了一次revokeStlPayOrders 此处判断是否重复  end
    		}
    		//update zhangweiqi 2020-02-13 场外期权 汇总已经处理  end

    		periodCommonMapper.insertRevokePaTrdProductDeal(oldTradeId, updateUser);
    		periodCommonMapper.insertRevokeTrdProductDealAdd(oldTradeId, updateUser);

    		TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(oldTradeId);
    		// 组合交易
    		if (trdProductDealAdd != null && StringUtil.isNotEmpty(trdProductDealAdd.getPortfolioId())) {
    			PtlTrade deal = new PtlTrade();
    			deal.setBusiNo(oldTradeId);
    			deal.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
    			deal.setPortfolioId(trdProductDealAdd.getPortfolioId());
    			periodCommonMapper.insertRevokePtlTrade(deal, updateUser);
    			if (StringUtil.isNotEmpty(actPortfolio) && !trdProductDealAdd.getPortfolioId().equals(actPortfolio)) {
    				deal.setPortfolioId(actPortfolio);
    				periodCommonMapper.insertRevokePtlTrade(deal, updateUser);
    			}

    			// 一体化对应etliu start
    			portfolioIdMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
    			// 一体化对应etliu end
    		}

    		// 费用明细
    		// add by zhangliang 20200903 结算下行调试发现费用明细被重复撤销了两次，上面已经调了一次inertRevokeFeeDetailByTradeId 此处判断是否重复  start
    		if (!StringUtil.equals(oldTradeId, bizCode)){
    			periodCommonMapper.inertRevokeFeeDetailByTradeId(oldTradeId, updateUser);
    		}
    		// add by zhangliang 20200903 结算下行调试发现费用明细被重复撤销了两次，上面已经调了一次inertRevokeFeeDetailByTradeId 此处判断是否重复  end

    		// 删除交易报送
    		periodCommonMapper.deleteRepTradeByTradeId(oldTradeId);

    		//// 一体化对应etliu start
    		//// 所有交易生效后调用的共通方法
    		//AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
    		//inputDto.setProdid(trdProductDealAdd.getPortfolioId());
    		//inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
    		//inputDto.setTrdProductDeal(trdProductDeal);
    		//AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
    		//// 一体化对应etliu end
    	};

    	// 一体化对应etliu start
    	if (!StringUtil.isEmptyList(trdProductDeals)&&commonFlag) {
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

    		List<TrdProductDeal> trdProductDeals1 = periodCommonMapper.findDealByTotalIdForInterf(map);
    		List<TrdProductDealAdd> trdProductDealAdds = periodCommonMapper.findDealAddByTradeIds(trdProductDeals1);
    		List<PtlTrade> ptlTrades = periodCommonMapper.findPtlTradeByTradeIds(trdProductDeals1);

    		// 所有交易生效后调用的共通方法
    		for (TrdProductDeal baseDto : trdProductDeals1) {
    			AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
    			inputDto.setProdid(portfolioIdMap.get("portfolioId"));
    			inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
    			//子组合估值调用
    			inputDto.setActPortfolioId(actPortfolio);
    			baseDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
    			inputDto.setTrdProductDeal(baseDto);
    			inputDto.setTrdProductDealAdd(getAdd(trdProductDealAdds, baseDto.getTradeId()));
    			inputDto.setRepoType(type6);
    			if (ordersTotal != null && !ordersTotal.isEmpty()) {
    				inputDto.setStlPayOrders(ordersTotal.get(0));
    			}
    			List<PtlTrade> ptlTradeList = new ArrayList<>();
    			for (PtlTrade ptlTrade : ptlTrades) {
    				if (baseDto.getTradeId().equals(ptlTrade.getBusiNo())) {
    					ptlTradeList.add(ptlTrade);
    				}
    			}
    			inputDto.setPtlTradeList(ptlTradeList);
    			AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

    			// 调用投管接口 add by renping 20190205 start
    			if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue(), finprodType2)) {
    				FinProductTypeKey key = new FinProductTypeKey();
    				key.setFinprodId(baseDto.getFinprodId());
    				key.setBranch(0L);
    				FinProductType finProductType =finProductTypeMapper.selectByPrimaryKey(key);
    				InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
    				if (StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(),
    						finProductType.getType6())) {
    					investInterfCommDto.setPageId("TrdFundInvestTrade002.02");
    				}
    				else {
    					investInterfCommDto.setPageId("TrdFundInvestTrade002.01");
    				}
    				investInterfCommDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
    				investInterfCommDto.setPortfolioId(actPortfolio);
    				investInterfCommDto.setTrdProductDeal(
    						inputDto.getTrdProductDeal());
    				investInterfCommDto.setTrdProductDealAdd(
    						inputDto.getTrdProductDealAdd());
    				investInterfCommDto.setTrdProductDealAdd(
    						inputDto.getTrdProductDealAdd());
    				investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
    				// TODO 撤销这个，费用目前没有给，后续再看会不会出问题
    				investInterfCommService.doInvestInterf(investInterfCommDto);
    			}
    			// 调用投管接口 add by renping 20190205 end
    		}
    	}
    	// 一体化对应etliu end

    	return trdFinProductDealTotal;
    }	
}