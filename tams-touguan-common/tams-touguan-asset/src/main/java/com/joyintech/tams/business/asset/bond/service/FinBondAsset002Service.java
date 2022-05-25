/*
 * 文件名：FinAssetRateAdj001Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：zhangnian 修改时间：2018年10月11日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.business.asset.bond.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.asset.bond.dao.FinBondAssetMapper;
import com.joyintech.tams.business.asset.bond.dto.FinBondAssetSaveRequest;
import com.joyintech.tams.common.base.dao.FinCashCalcRuleMapper;
import com.joyintech.tams.common.base.dao.FinCashPlanMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.business.asset.bond.dto.FinAssetRateAdjRequest;
import com.joyintech.tams.business.asset.bond.dto.FinAssetRateAdjResponse;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.asset.dto.RateAdjDto;
import com.joyintech.tams.invest.common.asset.service.RateAdjService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利率调整 〈功能详细描述〉
 *
 * @author wangyang1
 * @version 1.0 2022年4月19日
 * @see FinBondAsset002Service
 * @since 1.0
 */
@Service
public class FinBondAsset002Service extends BaseService<FinAssetRateAdjResponse> implements BaseProcessService<FinAssetRateAdjResponse> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(FinBondAsset002Service.class);

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private RateAdjService rateAdjService;

    @Autowired
    private FinBondAssetMapper finBondAssetMapper;

    @Autowired
    private FinCashCalcRuleMapper finCashCalcRuleMapper;

    @Autowired
    private FinCashPlanMapper finCashPlanMapper;

    @Autowired
    private FinBondAssetService finBondAssetService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 初始化信息--前台分页 <功能详细描述>
     *
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    public List<FinAssetRateAdjResponse> getfinAssetRateAdjByProductId(Map<String, Object> paramMap) {
        paramMap.put("FINPROD_TYPE_TG_F28", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
        List<FinAssetRateAdjResponse> finAssetRateAdjList = finBondAssetMapper.getfinAssetRateAdjByProductId(paramMap);
        for (FinAssetRateAdjResponse entity : finAssetRateAdjList) {
            String updateFlg = StringUtil.EMPTY;
            if (finBondAssetService.isTransInAssetTwo(entity.getFinprodId())!=0 &&
                    DateUtil.compareDate(entity.getEffDate(), iPlatformDate.getSysDate()) == 0) {
                updateFlg = "02";
            }else if ( DateUtil.compareDate(entity.getEffDate(), iPlatformDate.getSysDate()) == -1) {
                updateFlg = "02";
            } else {
                updateFlg = "01";
            }
            entity.setUpdateFlg(updateFlg);
        }
        return finAssetRateAdjList;
    }

    public Page<FinAssetRateAdjResponse> selectForPage(FinAssetRateAdjRequest request) {
        Map<String,Object> pageParam = BeanUtil.object2Map(request);
        pageParam.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        pageParam.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        pageParam.put("cashType2", DictCodeListExt1.现金流二级类型_利息.getValue());
        Page<FinAssetRateAdjResponse> pageList = restTemplate.postForPage(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinAssetRateAdj/findPage", pageParam, FinAssetRateAdjResponse.class);
        for (FinAssetRateAdjResponse entity : pageList.getRoot()) {
            String updateFlg = StringUtil.EMPTY;
            if (StringUtil.equals(entity.getIsInitial(), DictCodeList.是否_是.getValue())) {
                updateFlg = "02";
            }
            else if (DateUtil.compareDate(entity.getEffDate(), iPlatformDate.getSysDate()) == -1) {
                updateFlg = "02";
            }
            else {
                updateFlg = "01";
            }
            entity.setUpdateFlg(updateFlg);
        }
        return pageList;
    }

    public FinAssetRateAdjResponse encapFinAssetRateAdjDto(FinAssetRateAdjResponse dto) {
        // 重新查询计算规则、计算规则扩展初始化信息
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
            param.put("finprodId", dto.getFinprodId());
        }else if (CollectionUtils.isNotEmpty(dto.getSelectedItem())) {
            param.put("finprodId", dto.getSelectedItem().get(0).getFinprodId());
        }
        param.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        param.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        param.put("cashType2", DictCodeListExt1.现金流二级类型_利息.getValue());
        List<FinAssetRateAdjResponse> finAssetRateAdjList = selectInitAssetRateAdj(param);
        String cashId = finAssetRateAdjList.get(0).getCashId();
        String calcType = finAssetRateAdjList.get(0).getCalcType();
        String baseType = finAssetRateAdjList.get(0).getBaseType();
        String baseDateType = finAssetRateAdjList.get(0).getBaseDateType();
        String basis = finAssetRateAdjList.get(0).getBasis();
        String marketBasis = finAssetRateAdjList.get(0).getMarketBasis();
        BigDecimal issueAmt = finAssetRateAdjList.get(0).getIssueAmt();
        Long branch = finAssetRateAdjList.get(0).getBranch();
        String finprodType2 =  finAssetRateAdjList.get(0).getFinprodType2();
        BigDecimal investRate = finAssetRateAdjList.get(0).getInvestRate();
        List<PaFinCashCalcRule> paFinCashCalcRuleList = new ArrayList<PaFinCashCalcRule>();
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<FinCashCalcRule>();
        dto.getSelectedItem().forEach(entity -> {
            // 生效利率
            BigDecimal yield = entity.getYield();
            PaFinCashCalcRule paFinCashCalcRule = new PaFinCashCalcRule();
            FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
            BeanUtil.copyPropertiesIgnoreNull(finCashCalcRule, entity);
            finCashCalcRule.setFinprodType2(finprodType2);
            finCashCalcRule.setCashId(cashId);
            finCashCalcRule.setCalcType(calcType);
            finCashCalcRule.setBaseType(baseType);
            finCashCalcRule.setBaseDateType(baseDateType);
            finCashCalcRule.setBasis(basis);
            finCashCalcRule.setMarketBasis(marketBasis);
            if (!StringUtil.isNotEmpty(entity.getIsInitial())){
                entity.setIsInitial(DictCodeList.是否_否.getValue());
            }
            finCashCalcRule.setBranch(branch);
            finCashCalcRule.setYield(yield);
            paFinCashCalcRule.setCashId(cashId);
            paFinCashCalcRule.setEffDate(entity.getEffDate());
            paFinCashCalcRule.setInvestRate(investRate);
            paFinCashCalcRule.setLoadRate(yield);
            paFinCashCalcRuleList.add(paFinCashCalcRule);
            finCashCalcRuleList.add(finCashCalcRule);
        });
        dto.setIssueAmt(issueAmt);
        dto.setCashId(cashId);
        dto.setBasis(basis);
        dto.setPaFinCashCalcRuleList(paFinCashCalcRuleList);
        dto.setFinCashCalcRuleList(finCashCalcRuleList);
        return dto;
    }

    /**
     * 封装计算规则信息(通道)
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private FinAssetRateAdjResponse encapFinAssetRateAdjDtoForChannel(FinAssetRateAdjResponse dto) {
        Long branch =StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE))?0:Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        // 重新查询计算规则、初始化信息
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<FinCashCalcRule>();
        List<PaFinCashCalcRule> paFinCashCalcRuleList = new ArrayList<PaFinCashCalcRule>();
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
            param.put("finprodId", dto.getFinprodId());
        }else if (CollectionUtils.isNotEmpty(dto.getSelectedItem())) {
            param.put("finprodId", dto.getSelectedItem().get(0).getFinprodId());
        }
        param.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
        List<FinAssetRateAdjResponse> finAssetRateAdjList = selectInitAssetRateAdj(param);
        String cashId = finAssetRateAdjList.get(0).getCashId();
        String calcType = finAssetRateAdjList.get(0).getCalcType();
        String baseType = finAssetRateAdjList.get(0).getBaseType();
        String baseDateType = finAssetRateAdjList.get(0).getBaseDateType();
        String basis = finAssetRateAdjList.get(0).getBasis();
        String marketBasis = finAssetRateAdjList.get(0).getMarketBasis();
        BigDecimal issueAmt = finAssetRateAdjList.get(0).getIssueAmt();
        String finprodType2 =  finAssetRateAdjList.get(0).getFinprodType2();
        BigDecimal investRate = finAssetRateAdjList.get(0).getInvestRate();
		for (FinAssetRateAdjResponse entity : dto.getSelectedItem()) {
			for (FinAssetRateAdjResponse fin : finAssetRateAdjList) {
				if (fin.getFeeId().equals(entity.getFeeId())) {
					cashId = fin.getCashId();
					calcType = fin.getCalcType();
					baseType = fin.getBaseType();
					baseDateType = fin.getBaseDateType();
					basis = fin.getBasis();
					issueAmt = fin.getIssueAmt();
					finprodType2 = fin.getFinprodType2();
					investRate = fin.getInvestRate();
				}
			}
            // 生效利率
            BigDecimal yield = ArithmeticUtil.round(entity.getYield(), 14, RoundingMode.HALF_UP);
            PaFinCashCalcRule paFinCashCalcRule = new PaFinCashCalcRule();
            FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
            BeanUtil.copyPropertiesIgnoreNull(finCashCalcRule, entity);
            finCashCalcRule.setFinprodId(entity.getFinprodId());
            finCashCalcRule.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
            finCashCalcRule.setFinprodType2(finprodType2);
            finCashCalcRule.setCashId(cashId);
            finCashCalcRule.setCalcType(calcType);
            finCashCalcRule.setBaseType(baseType);
            finCashCalcRule.setBaseDateType(baseDateType);
            finCashCalcRule.setBasis(basis);
            finCashCalcRule.setMarketBasis(marketBasis);
            if (!StringUtil.isNotEmpty(entity.getIsInitial())){
                entity.setIsInitial(DictCodeList.是否_否.getValue());
            }
            finCashCalcRule.setBranch(branch);
            finCashCalcRule.setYield(yield);
            paFinCashCalcRule.setCashId(cashId);
            paFinCashCalcRule.setEffDate(entity.getEffDate());
            paFinCashCalcRule.setInvestRate(investRate);
            paFinCashCalcRule.setLoadRate(yield);
            paFinCashCalcRuleList.add(paFinCashCalcRule);
            finCashCalcRuleList.add(finCashCalcRule);
        }
        dto.setIssueAmt(issueAmt);
        dto.setCashId(cashId);
        dto.setBasis(basis);
        dto.setPaFinCashCalcRuleList(paFinCashCalcRuleList);
        dto.setFinCashCalcRuleList(finCashCalcRuleList);
        return dto;
    }

    @Override
    public void doInsert(FinAssetRateAdjResponse entity, OperationType type){
        finBondAssetService.updateCfetsOprHis(entity.getFinprodMarketId(), DictCodeListExt3.操作类型_利率调整.getValue());
    }

    @Override
    public void doUpdate(FinAssetRateAdjResponse entity) {
        finBondAssetService.updateCfetsOprHis(entity.getFinprodMarketId(), DictCodeListExt3.操作类型_利率调整.getValue());
    }

    @Override
    public void doBack(FinAssetRateAdjResponse entity) {
        finBondAssetService.updateCfetsOprHis(entity.getFinprodMarketId(), DictCodeListExt3.操作类型_利率调整退回.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(FinAssetRateAdjResponse entity, OperationType type) {
        String action = StringUtil.EMPTY;
        //通道
        if(StringUtil.equals(entity.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_通道.getValue())){
            entity = encapFinAssetRateAdjDtoForChannel(entity);
        }else{
         // 重新计算现金流计划
            entity = encapFinAssetRateAdjDto(entity);
            if (null != entity.getSelectedItem() && !entity.getSelectedItem().isEmpty()) {
                try {
                    RateAdjDto rateAdjDto = new RateAdjDto();
                    BeanUtil.copyPropertiesIgnoreNull(rateAdjDto, entity);
                    rateAdjDto.setFinCashCalcRuleList(entity.getFinCashCalcRuleList());
                    rateAdjDto.setNowSystemDate(iPlatformDate.getSysDate());
                    //与批处理调用用一个逻辑
                    rateAdjDto = rateAdjService.againCalCashPlan(rateAdjDto);
                    if (rateAdjDto != null) {
                        entity.setFinCashPlanList(rateAdjDto.getFinCashPlanList());
                    }
                    //entity = againCalCashPlan(entity);
                }
                catch (ParseException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }
        //针对还本不付息资产，还本时，计划应付利息应等于区间还本未付利息
        entity =encapFinCashPlanIntAmt(entity);
        insert(entity);
        if (StringUtil.isNotEmpty(entity.getFinprodMarketId())) {
            finBondAssetService.updateCfetsOprHis(entity.getFinprodMarketId(), DictCodeListExt3.操作类型_利率调整.getValue());
        }else{
            finBondAssetService.updateCfetsOprHis(entity.getSelectedItem().get(0).getFinprodMarketId(), DictCodeListExt3.操作类型_利率调整.getValue());
        }
    }

    public void insert(FinAssetRateAdjResponse dto){
        //根据cashId删除计算规则、计算规则扩展
        FinCashCalcRuleKey finCashCalcRuleKey = new FinCashCalcRuleKey();
        //新增计算规则、计算规则扩展
        dto.getFinCashCalcRuleList().forEach(entity ->{
            finCashCalcRuleKey.setCashId(entity.getCashId());
            finBondAssetMapper.deleteCashCalcRuleByCashId(finCashCalcRuleKey);
        });
        dto.getFinCashCalcRuleList().forEach(entity ->{
            finCashCalcRuleMapper.insert(entity);
        });
        //付息计划
        if(null!=dto.getFinCashPlanList()&&!dto.getFinCashPlanList().isEmpty()){
            dto.getFinCashPlanList().forEach(entity->{
                FinCashPlanKey key = new FinCashPlanKey();
                key.setCashId(entity.getCashId());
                key.setMdate(entity.getMdate());
                finCashPlanMapper.deleteByPrimaryKey(key);
            });
            dto.getFinCashPlanList().forEach(entity->{
                finCashPlanMapper.insert(entity);
            });
        }
    }



    private FinAssetRateAdjResponse encapFinCashPlanIntAmt(FinAssetRateAdjResponse entity){
        if(!StringUtil.isEmptyList(entity.getFinCashPlanList()) && entity.getFinCashPlanList().size()>0){
            for(FinCashPlan plan:entity.getFinCashPlanList()){
                if(StringUtil.isNotEmpty(plan.getIsPayInt()) && StringUtil.equals(DictCodeList.是否_否.getValue(), plan.getIsPayInt())){//业务场景，是还本不带息的
                    plan.setIntAmt(plan.getRepayWithoutInt());
                }
            }
        }
        return entity;
    }

    public List<FinAssetRateAdjResponse> selectInitAssetRateAdj(Map<String,Object> param){
        return finBondAssetMapper.selectInitAssetRateAdj(param);
    }

    public AssetCommonInfoDto getMdateByFinproId(String finprodId){
        return finBondAssetMapper.getMdateByFinproId(finprodId);
    }

    public AssetCommonInfoDto getAssetInfoToRateAdj(@RequestBody Map<String, Object> param){
        return finBondAssetMapper.getAssetInfoToRateAdj(param);
    }

}
