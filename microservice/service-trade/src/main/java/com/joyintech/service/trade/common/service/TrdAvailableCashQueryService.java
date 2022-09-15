/*
 * 文件名：TrdAvailableCashQueryService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月11日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.trade.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlCashPositionInfoMapper;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlSecPositionInfoMapper;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.common.dao.TrdAvailableCashQueryMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;

@Service
public class TrdAvailableCashQueryService {

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired
    private EtrCommonCashPositionDealService etrCommonCashPositionDealService;
    @Autowired
    private BasePtlCashPositionInfoMapper basePtlCashPositionInfoMapper;

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    /**
     * 组合可用持仓和实际持仓查询更新组件Mapper
     */
    @Autowired
    private BasePtlSecPositionInfoMapper basePtlSecPositionInfoMapper;

    @Autowired
    private TrdAvailableCashQueryMapper trdAvailableCashQueryMapper;

    public Map<String, BigDecimal> getAvailableCash(Map<String, Object> param) {
        Map<String, BigDecimal> ret = new HashMap<>();
        Date tradeDate = null;
        if(param.get("tradeDate") instanceof Long ){
            tradeDate = new Date((Long)param.get("tradeDate"));
        }else {
            tradeDate = DateUtil.parse((String)param.get("tradeDate"));
        }
        Date tradeDateNext = DateUtil.parse((String)param.get("tradeDateNext"));
        if (tradeDateNext == null) {
            tradeDateNext = DateUtil.addDay(tradeDate, 1);
        }
        // 账户类型
        List<String> acctTypeList = new ArrayList<String>();
        String acctTypeListStr = (String)param.get("acctTypeList");
        if (StringUtil.isNotBlank(acctTypeListStr)) {
            acctTypeList = StringUtil.splitToList(GlobalConstant.COMMA, acctTypeListStr);
        }
        // 可用现金
        if (basePtlCashPositionInfoService != null) {
            PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
            dto.setPortfolioId((String)param.get("portfolioId"));
            dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
            dto.setCdate(tradeDate);
            dto.setCcy((String)param.get("ccy"));
            dto.setAcctType(acctTypeList);
            dto.setAcctId((String)param.get("acctId"));
            /**modify by sunhui 20210720 重庆银行：中标结果维护，缴款日特殊处理现金   start**/
            BigDecimal T_0 = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            dto.setCdate(tradeDateNext);
            BigDecimal T_1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            //如果是中标缴款进来,需要补上中标维护的占用头寸:不需要再判断维护明细中的是否占用头寸了，因为如果有就有，如果没有就是0，前后不变
            if(StringUtil.isNotEmpty(param.get("bondFirstSign")) &&
               StringUtil.isNotEmpty(param.get("entrustId")) &&
               StringUtil.equals("01",param.get("bondFirstSign"))){
                EntrustCommonDto entrustCommonDto = etrCommonCashPositionDealService.findEntrust((String)param.get("entrustId"));
                String acctId = etrCommonCashPositionDealService.select组合托管户(entrustCommonDto.getEtrEntrust().getPortfolioId(),
                        entrustCommonDto.getEtrEntrust().getCcy(),
                        DictCodeListExt2.资金账户类型_托管户.getValue());
                PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
                ptlCashPositionFlowDto.setPortfolioId((String)param.get("portfolioId"));
                ptlCashPositionFlowDto.setCcy(entrustCommonDto.getEtrEntrust().getCcy());
                ptlCashPositionFlowDto.setCdate(entrustCommonDto.getEtrEntrust().getVdate());
                ptlCashPositionFlowDto.setAcctId(acctId);
                ptlCashPositionFlowDto.setOrderId(entrustCommonDto.getEtrEntrust().getEntrustId());
                ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
                List<String> acctTypeList2 = new ArrayList<String>();
                acctTypeList2.add(DictCodeListExt2.资金账户类型_托管户.getValue());
                ptlCashPositionFlowDto.setAcctType(acctTypeList2);
                //根据ptlCashPositionFlowDto查询出冻结的持仓，取反+T_0作为新的T_0
                ptlCashPositionFlowDto.setIsFrozenPositionFlag(BussinessDictCodeList.是否_是.getValue());
                BigDecimal isFrozenPosition = basePtlCashPositionInfoMapper.selectCashPositionFlow(ptlCashPositionFlowDto);
                T_0 = ArithmeticUtil.doAdd(T_0,ArithmeticUtil.multiply(isFrozenPosition,new BigDecimal("-1")));
                T_1 = ArithmeticUtil.doAdd(T_1,ArithmeticUtil.multiply(isFrozenPosition,new BigDecimal("-1")));
            }
            ret.put("T_0", T_0);
            ret.put("T_1", T_1);
            /**modify by sunhui 20210720 重庆银行：中标结果维护，【缴款日特殊处理现金   end**/

            // 通道交易 新增 强成西 2019/03/26 start
            if (StringUtil.isNotEmpty(param.get("chlId"))) {
                dto.setCdate(tradeDate);
                dto.setAcctType(null);
                dto.setChlFinprodId((String) param.get("chlId"));
                ret.put("T_2", basePtlCashPositionInfoService.queryUsableCashPosition(dto));
            }
            // 通道交易 新增 强成西 2019/03/26 end

            //查询新表可用现金
            PtlCashPositionFlowDto dtoNew = new PtlCashPositionFlowDto();
            dtoNew.setHodingType(DictCodeListExt3.资产类型_汇总可用现金.getValue());
            dtoNew.setCdate(tradeDate);
            dtoNew.setPortfolioId((String)param.get("portfolioId"));
            dtoNew.setCcy((String)param.get("ccy"));
            dtoNew.setDateType(0);
            BigDecimal cashT0_new = basePtlCashPositionInfoService.queryTrdAvailableCashNew(dtoNew);
            BigDecimal cashT= new BigDecimal(ret.get("T_0").toString());
            if(StringUtil.isNotEmpty(cashT0_new)){
                ret.put("T_0",  ArithmeticUtil.doAdd(cashT, cashT0_new));// 组合T日可用
            }
            dtoNew.setDateType(1);
            BigDecimal cashT1_new = basePtlCashPositionInfoService.queryTrdAvailableCashNew(dtoNew);
            BigDecimal cashT1= new BigDecimal(ret.get("T_1").toString());
            if(StringUtil.isNotEmpty(cashT1_new)){
                ret.put("T_1",  ArithmeticUtil.doAdd(cashT1, cashT1_new));// 组合T+1日可用
            }

        }
        // 可用持仓
        if (basePtlSecPositionInfoService != null) {
            PtlSecPositionFlowDto dto = new PtlSecPositionFlowDto();
            dto.setPortfolioId((String)param.get("portfolioId"));
            dto.setCdate(tradeDate);
            dto.setCcy((String)param.get("ccy"));
            dto.setAcctType(acctTypeList);
            dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
            if (StringUtil.isNotEmpty((String) param.get("chlId"))) {
                dto.setChannel(true);
                dto.setSecAcctId((String) param.get("chlId"));
            } else {
                dto.setSecAcctId((String) param.get("secAcctId"));
            }
            // 一体化对应etliu start
            dto.setInvAim((String)param.get("invAim"));
            // 一体化对应etliu end
            if (param.get("finprodId") == null
                || StringUtil.isBlank((String)param.get("finprodId"))) {
                ret.put("position", null);
            }
            else {
                dto.setFinprodId((String)param.get("finprodId"));
                BigDecimal result = basePtlSecPositionInfoService.queryUsableSecPosition(dto);
                if (param.get("multiParam") != null) {
                    result = ArithmeticUtil.multiply(result, new BigDecimal((Integer) param.get("multiParam")));
                }
                ret.put("position", result);
            }
        }
        return ret;
    }

    public BigDecimal getSecManageAvailableCash(Map<String, Object> param) {
        List<String> secManager = trdAvailableCashQueryMapper.selectSecManager(param);
        PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
        dto.setPortfolioId((String)param.get("portfolioId"));
        // add by jiangpeng on 20210510 start YHZGCPB20210115001 通道增值税缴纳
        dto.setChlFinprodId((String)param.get("channelId"));
        // add by jiangpeng on 20210510 end YHZGCPB20210115001 通道增值税缴纳
        dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        dto.setCdate(DateUtil.parse((String)param.get("tradeDate")));
        dto.setCcy((String)param.get("ccy"));
        // 账户类型
        List<String> acctTypeList = new ArrayList<String>();
        String acctTypeListStr = (String)param.get("acctType");
        if (StringUtil.isNotBlank(acctTypeListStr)) {
            acctTypeList = StringUtil.splitToList(GlobalConstant.COMMA, acctTypeListStr);
        }
        //add by wangpeng 20190830 查询证券账户可用现金
        String acctId = (String)param.get("acctId");
        if(StringUtil.isNotEmpty(acctId)){
        	dto.setAcctId(acctId);
        }
        //add end wangpeng 20190830
        dto.setAcctType(acctTypeList);
        BigDecimal ret = BigDecimal.ZERO;
        for (String secNo : secManager) {
            dto.setSeqno(secNo);
            ret = ArithmeticUtil.doAdd(basePtlCashPositionInfoService.queryUsableCashPosition(dto), ret);
            //查询新表可用现金
            PtlCashPositionFlowDto dtoNew = new PtlCashPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(dtoNew, dto);
            dtoNew.setHodingType(DictCodeListExt3.资产类型_汇总可用现金.getValue());
            dtoNew.setDateType(0);
            ret = ArithmeticUtil.doAdd(basePtlCashPositionInfoService.queryTrdAvailableCashNew(dtoNew), ret);
        }
        return ret;
    }

    /**
     * 本方券可用持仓：实际 - 质押 +可用（流水表）-融出 +预占用 OWN_POSITION
     * 融入券可用持仓：融入-卖空-卖空可用（流水表） INTEGRATE_POSITION
     * 融入券卖空可用持仓：卖空 SHOWHANDS_POSITION
     */
    public Map<String, Object> getShowHandNum(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        if (basePtlSecPositionInfoService != null) {
            Date tradeDate = DateUtil.parse((String)param.get("tradeDate"));
            PtlSecPositionFlowDto dto = new PtlSecPositionFlowDto();
            dto.setPortfolioId((String) param.get("portfolioId"));
            dto.setCdate(tradeDate);
            dto.setCcy((String) param.get("ccy"));
            dto.setHodingType(DictCodeListExt1.持仓类型_卖空.getValue());
            // 一体化对应etliu start
            dto.setInvAim((String) param.get("invAim"));
            // 一体化对应etliu end
            String invAim = dto.getInvAim();
            dto.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            if (StringUtil.isNotEmpty((String) param.get("chlId"))) {
                dto.setChannel(true);
                dto.setSecAcctId((String) param.get("chlId"));
            } else {
                dto.setChannel(false);
                dto.setSecAcctId((String) param.get("secAcctId"));
            }
            if (param.get("finprodId") == null
                    || StringUtil.isBlank((String) param.get("finprodId"))) {
                ret.put("position", null);
            } else {
                dto.setFinprodId((String) param.get("finprodId"));
                BigDecimal 卖空 = basePtlSecPositionInfoService.queryOtherSecPosition(dto);
                ret.put("SHOWHANDS_POSITION", ArithmeticUtil.multiply(卖空, new BigDecimal(100)));
                dto.setInvAim(invAim);
                // 本方券可用持仓：实际 - 质押 +可用（流水表）-融出 +预占用 OWN_POSITION
                dto.setHodingType(DictCodeList.持仓类型_实际.getValue());
                BigDecimal 实际 = basePtlSecPositionInfoService.queryOtherSecPosition(dto);
                dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
                BigDecimal 可用持仓 = basePtlSecPositionInfoMapper.selectSecPositionFlow(dto);
                dto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                BigDecimal 质押 = basePtlSecPositionInfoService.queryOtherSecPosition(dto);
                dto.setInvAim("999999");
                dto.setHodingType(DictCodeList.持仓类型_融出.getValue());
                BigDecimal 融出 = basePtlSecPositionInfoService.queryOtherSecPosition(dto);
                dto.setInvAim(invAim);
                dto.setHodingType(DictCodeListExt1.持仓类型_预占用.getValue());
                BigDecimal 预占用 = basePtlSecPositionInfoService.queryOtherSecPosition(dto);
                ret.put("OWN_POSITION", ArithmeticUtil.multiply(ArithmeticUtil.doAdd(实际, (质押 == null ? BigDecimal.ZERO : 质押.negate()),
                        可用持仓, (融出 == null ? BigDecimal.ZERO : 融出.negate()), 预占用), new BigDecimal(100)));

                // 融入券可用持仓：融入-卖空-卖空可用（流水表） INTEGRATE_POSITION
                dto.setInvAim("999999");
                dto.setHodingType(DictCodeList.持仓类型_融入.getValue());
                BigDecimal 融入 = basePtlSecPositionInfoService.queryOtherSecPosition(dto);
                dto.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
                dto.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                BigDecimal 卖空可用 = basePtlSecPositionInfoMapper.selectSecPositionFlow(dto);
                ret.put("INTEGRATE_POSITION", ArithmeticUtil.multiply(ArithmeticUtil.doAdd(融入, (卖空 == null ? BigDecimal.ZERO : 卖空.negate()),
                        (卖空可用 == null ? BigDecimal.ZERO : 卖空可用.negate())), new BigDecimal(100)));
                dto.setInvAim(invAim);
            }
        }
        return ret;
    }
    
    /**
     * 获得资产维度的持仓
     * @param map 资产代码，日期
     * @return 持仓
     */
    public Map<String, Object> getFinAvaPosition(Map<String, Object> map) {
        map.put("hodingType03", DictCodeList.持仓类型_实际.getValue());
        Map<String, Object> resultMap=trdAvailableCashQueryMapper.getFinAvaPosition(map);
        if(StringUtil.isNotEmpty(map.get("isContainWfl")) && StringUtil.equals(map.get("isContainWfl").toString(),DictCodeListExt1.是或否_是.getValue())){
            //针对，计划类利率型资产买入时，在流程中不存表ptl_sec_position_flow，因此，流程中的持仓取自表wfl_trd_product_deal
            BigDecimal 可用持仓 = trdAvailableCashQueryMapper.getFinAvaPositionFlow(map);
            resultMap.put("POSITIONFLOW", 可用持仓);
        }
        return resultMap;
    }

    /**
     * 获得资产维度的持仓
     * @param map 资产代码，日期
     * @return 持仓
     */
    public BigDecimal getSecPosition(Map<String, Object> map) {
        map.put("hodingType03", DictCodeList.持仓类型_实际.getValue());
        return trdAvailableCashQueryMapper.getSecPosition(map);
    }
}
