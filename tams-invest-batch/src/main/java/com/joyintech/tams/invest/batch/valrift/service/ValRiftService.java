/*
 * 文件名：ValRiftService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年1月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.batch.valrift.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.PtlSecPierceValutionDetailMapper;
import com.joyintech.tams.common.base.dao.PtlSecPierceValutionMapper;
import com.joyintech.tams.common.base.dao.RskPreOccupyInfoMapper;
import com.joyintech.tams.common.base.entity.RskPreOccupyInfo;
import com.joyintech.tams.invest.batch.valrift.dao.ValRiftMapper;
import com.joyintech.tams.invest.batch.valrift.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 *  估值穿透
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年1月25日
 * @see ValRiftService
 * @since 1.0
 */
@Service
public class ValRiftService {

    @Autowired
    private ValRiftMapper valRiftMapper;
    
    @Autowired
    private PtlSecPierceValutionMapper ptlSecPierceValutionMapper;
    @Autowired
    private RskPreOccupyInfoMapper rskPreOccupyInfoMapper;
    @Autowired
    private PtlSecPierceValutionDetailMapper ptlSecPierceValutionDetailMapper;
    
    /**
     * 穿透处理
     * <功能详细描述>
     * @param currentDate
     * @param productIdList
     * @see
     * @since 1.0
     */
    public void run(Date currentDate, List<String> productIdList){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        paramMap.put("cdate", sdf.format(currentDate));
        paramMap.put("productIdList", productIdList);
        //穿透组合
        List<PtlSecValutionDto> ptlList = selectPtlIdForTask(paramMap);
        for(PtlSecValutionDto ptl : ptlList){
            paramMap.put("portfolioId", ptl.getPortfolioId());
            List<PtlSecPierceValutionDto> result = new ArrayList<PtlSecPierceValutionDto>();
            List<PtlSecPierceValutionDetailDto> detailResult = new ArrayList<PtlSecPierceValutionDetailDto>();
            //组合估值资产
            List<PtlSecValutionDto> ptlValList = selectPtlValAsset(paramMap);
            for(PtlSecValutionDto ptlVal : ptlValList){
                String portfolioId = StringUtil.isNotEmpty(ptlVal.getFinprodMarketId()) ? ptlVal.getFinprodMarketId() :
                        ptlVal.getPortfolioId();
                paramMap.put("cdate", DateUtil.format(currentDate));
                paramMap.put("portfolioId", portfolioId);
                List<ValtblAssetSubDto> positionList = valRiftMapper.selectValtblAssetSub(paramMap);//底层资产
                //剩余现金 finprodId = REMAININGCASH_+"成本科目号"
                List<ValtblAssetSubDto> cashList =  valRiftMapper.selectValtblAssetSubCash(paramMap);
                positionList.addAll(valRiftMapper.selectValtblAssetSubCash(paramMap));
                //回购利息是否是汇总的
                boolean hgSum = false;
                if (positionList != null && !positionList.isEmpty()) {
                    hgSum =true;
                    /*过滤回购利息取汇总*/
                    positionList = positionList.stream().filter(x->(!StringUtil.startsWith(x.getSubCode(),"120410") && !StringUtil.startsWith(x.getSubCode(),"2231"))).collect(Collectors.toList());
                    //回购利息汇总情况下查询，科目代码<=8 优先3级科目 没有就2级 再1级科目
                    positionList.addAll(valRiftMapper.selectValtblAssetSubHg(paramMap));

                    // 新建计划类标的信息 生成组合 市场代码作为组合代码
                    List<ValtblAssetSubDto> leafAssetPlan = dealRiftByPtlId(portfolioId, currentDate, ptlVal.getShareAmt(),
                        positionList);
                    if (null != leafAssetPlan && !leafAssetPlan.isEmpty()) {
                        for (ValtblAssetSubDto valtblAssetSubDto : leafAssetPlan) {
                            PtlSecPierceValutionDto ptlSecPierceValutionDto = new PtlSecPierceValutionDto();
                            ptlSecPierceValutionDto.setPortfolioId(ptlVal.getPortfolioId());
                            ptlSecPierceValutionDto.setCdate(currentDate);
                            ptlSecPierceValutionDto.setFinprodId(valtblAssetSubDto.getFinprodId());
                            ptlSecPierceValutionDto.setSubCode(valtblAssetSubDto.getSubCode());
                            ptlSecPierceValutionDto.setPositionRatio(valtblAssetSubDto.getPositionRatio());
                            ptlSecPierceValutionDto.setShareAmt(valtblAssetSubDto.getSecurityQuantity());
                            ptlSecPierceValutionDto.setDscCostAmt(valtblAssetSubDto.getSecurityCost());
                            //应计利息为空
                            if(StringUtil.startsWith(valtblAssetSubDto.getSubCode(),"1204")
                            || StringUtil.startsWith(valtblAssetSubDto.getSubCode(),"1203")//基金红利
                            || StringUtil.startsWith(valtblAssetSubDto.getSubCode(),"2231")//回购利息 add by lpf
                            ){
                                ptlSecPierceValutionDto.setTdyIntincexp(valtblAssetSubDto.getSecurityCost());
                            }else{
                                ptlSecPierceValutionDto.setTdyIntincexp(BigDecimal.ZERO);
                            }
                            //应计利息为空
                            ptlSecPierceValutionDto.setMarketValue(valtblAssetSubDto.getSmv());
                            result.add(ptlSecPierceValutionDto);
                            //add by lpf 资管计划
                            PtlSecPierceValutionDetailDto detail = new PtlSecPierceValutionDetailDto();
                            BeanUtil.copyPropertiesIgnoreNull(detail,ptlSecPierceValutionDto);
                            detail.setZgplanId(portfolioId);
                            detailResult.add(detail);
                        }
                    }
                }
                else {
                    PtlSecPierceValutionDto ptlSecPierceValutionDto = new PtlSecPierceValutionDto();
                    BeanUtil.copyPropertiesIgnoreNull(ptlSecPierceValutionDto, ptlVal);
                    result.add(ptlSecPierceValutionDto);
                    //add by lpf 资管计划
                    PtlSecPierceValutionDetailDto detail = new PtlSecPierceValutionDetailDto();
                    BeanUtil.copyPropertiesIgnoreNull(detail,ptlSecPierceValutionDto);
                    detailResult.add(detail);
                }
                //按占比处理回购利息
                if(hgSum){
                    genHgInit(result);
                }
            }
            //上农商个性化_对redis预占进行释放  add by zhangpei 20210120 start
            freedAmPreRskOccupy(ptlValList,currentDate);
            //上农商个性化_对redis预占进行释放  add by zhangpei 20210120 end
            //计算穿透后指标---modify by 裴家俊 2019/04/03---start--------
            List<PtlSecPierceValutionDto> resultRep = calValIndex(result);
            //add by lpf 增加资管计划维度明细
            List<PtlSecPierceValutionDetailDto> resultDetailRep = calValIndexDetail(detailResult);
            //删除当日跑批数据
            deleteValRift(currentDate,ptl);
            //入库
            writeValRift(resultRep);
            //入库 资管计划维度
            writeDetailValRift(resultDetailRep);
            //计算穿透后指标---modify by 裴家俊 2019/04/03---end--------
        }
    }

    /**add by lpf
     * 按占比拆分回购利息
     * @param result
     */
    private void genHgInit(List<PtlSecPierceValutionDto> result) {
        //计算回购成本汇总
        BigDecimal dscCostAmtMrSum = BigDecimal.ZERO;
        BigDecimal dscCostAmtMcSum = BigDecimal.ZERO;
        //买入返售成本科目list
        List<PtlSecPierceValutionDto> mrfsListTemp = new ArrayList<>();
        //回购成本科目List
        List<PtlSecPierceValutionDto> hgListTemp = new ArrayList<>();

        for(PtlSecPierceValutionDto dto:result){
            //买入返售
            if(StringUtil.startsWith(dto.getSubCode(),"1202")){
                dscCostAmtMrSum = ArithmeticUtil.doAdd(dscCostAmtMrSum,dto.getDscCostAmt());
                mrfsListTemp.add(dto);
            }
            //回购
            if(StringUtil.startsWith(dto.getSubCode(),"2202")){
                dscCostAmtMcSum = ArithmeticUtil.doAdd(dscCostAmtMcSum,dto.getDscCostAmt());
                hgListTemp.add(dto);
            }
        }
        //买入返售利息Dto
        PtlSecPierceValutionDto mrfsdto = null;
        List<PtlSecPierceValutionDto> mrfsList = result.stream().filter(x->StringUtil.startsWith(x.getSubCode(),"120410")).collect(Collectors.toList());
        if(!StringUtil.isEmptyList(mrfsList) && mrfsList.size()>0){
            mrfsdto = mrfsList.get(0);
        }
        //回购dto
        PtlSecPierceValutionDto hgdto = null;
        List<PtlSecPierceValutionDto> hgList = result.stream().filter(x->StringUtil.startsWith(x.getSubCode(),"2231")).collect(Collectors.toList());
        if(!StringUtil.isEmptyList(hgList) && hgList.size()>0){
            hgdto = hgList.get(0);
        }

        //循环成本科目计算利息
        //买入返售
        if(StringUtil.isNotEmpty(mrfsdto)){
            //数量
            BigDecimal shareAmt = BigDecimal.ZERO;
            //摊销总成本
            BigDecimal dscCostAmt = BigDecimal.ZERO;
            //市值
            BigDecimal marketValue = BigDecimal.ZERO;
            //利息
            BigDecimal tdyIntincexp = BigDecimal.ZERO;
            for(int i =0;i<mrfsListTemp.size();i++){
                PtlSecPierceValutionDto intDto = new PtlSecPierceValutionDto();
                //买入返售
                BeanUtil.copyPropertiesIgnoreNull(intDto,mrfsListTemp.get(i));
                //最后一条倒减
                if(i==mrfsListTemp.size()-1){
                    intDto.setShareAmt(ArithmeticUtil.subtract(mrfsdto.getShareAmt(),shareAmt));
                    intDto.setDscCostAmt(ArithmeticUtil.subtract(mrfsdto.getDscCostAmt(),dscCostAmt));
                    intDto.setMarketValue(ArithmeticUtil.subtract(mrfsdto.getMarketValue(),marketValue));
                    intDto.setTdyIntincexp(ArithmeticUtil.subtract(mrfsdto.getTdyIntincexp(),tdyIntincexp));
                }
                else{
                    intDto.setShareAmt(ArithmeticUtil.multiply(mrfsdto.getShareAmt(),ArithmeticUtil.divide(mrfsListTemp.get(i).getDscCostAmt(),dscCostAmtMrSum,9,RoundingMode.HALF_UP)));
                    intDto.setDscCostAmt(ArithmeticUtil.multiply(mrfsdto.getDscCostAmt(),ArithmeticUtil.divide(mrfsListTemp.get(i).getDscCostAmt(),dscCostAmtMrSum,9, RoundingMode.HALF_UP)));
                    intDto.setMarketValue(ArithmeticUtil.multiply(mrfsdto.getMarketValue(),ArithmeticUtil.divide(mrfsListTemp.get(i).getDscCostAmt(),dscCostAmtMrSum,9,RoundingMode.HALF_UP)));
                    intDto.setTdyIntincexp(ArithmeticUtil.multiply(mrfsdto.getTdyIntincexp(),ArithmeticUtil.divide(mrfsListTemp.get(i).getDscCostAmt(),dscCostAmtMrSum,9,RoundingMode.HALF_UP)));

                    shareAmt = ArithmeticUtil.doAdd(shareAmt,intDto.getShareAmt());
                    dscCostAmt = ArithmeticUtil.doAdd(dscCostAmt,intDto.getDscCostAmt());
                    marketValue = ArithmeticUtil.doAdd(marketValue,intDto.getMarketValue());
                    tdyIntincexp = ArithmeticUtil.doAdd(tdyIntincexp,intDto.getTdyIntincexp());
                }
                //利息计入成本
                if(StringUtil.isNotEmpty(intDto.getTdyIntincexp())){
                    mrfsListTemp.get(i).setTdyIntincexp(ArithmeticUtil.doAdd(mrfsListTemp.get(i).getTdyIntincexp(),intDto.getTdyIntincexp()));
                }
                else{
                    mrfsListTemp.get(i).setTdyIntincexp(ArithmeticUtil.doAdd(mrfsListTemp.get(i).getTdyIntincexp(),intDto.getDscCostAmt()));
                }
            }
        }
        //回购
        if(StringUtil.isNotEmpty(hgdto)){
            //数量
            BigDecimal shareAmt = BigDecimal.ZERO;
            //摊销总成本
            BigDecimal dscCostAmt = BigDecimal.ZERO;
            //市值
            BigDecimal marketValue = BigDecimal.ZERO;
            //利息
            BigDecimal tdyIntincexp = BigDecimal.ZERO;
            for(int i =0;i<hgListTemp.size();i++){
                PtlSecPierceValutionDto intDto = new PtlSecPierceValutionDto();
                //买入返售
                BeanUtil.copyPropertiesIgnoreNull(intDto,hgListTemp.get(i));
                //最后一条倒减
                if(i==hgListTemp.size()-1){
                    intDto.setShareAmt(ArithmeticUtil.subtract(hgdto.getShareAmt(),shareAmt));
                    intDto.setDscCostAmt(ArithmeticUtil.subtract(hgdto.getDscCostAmt(),dscCostAmt));
                    intDto.setMarketValue(ArithmeticUtil.subtract(hgdto.getMarketValue(),marketValue));
                    intDto.setTdyIntincexp(ArithmeticUtil.subtract(hgdto.getTdyIntincexp(),tdyIntincexp));
                }
                else{
                    intDto.setShareAmt(ArithmeticUtil.multiply(hgdto.getShareAmt(),ArithmeticUtil.divide(hgListTemp.get(i).getShareAmt(),dscCostAmtMcSum,9,RoundingMode.HALF_UP)));
                    intDto.setDscCostAmt(ArithmeticUtil.multiply(hgdto.getDscCostAmt(),ArithmeticUtil.divide(hgListTemp.get(i).getDscCostAmt(),dscCostAmtMcSum,9,RoundingMode.HALF_UP)));
                    intDto.setMarketValue(ArithmeticUtil.multiply(hgdto.getMarketValue(),ArithmeticUtil.divide(hgListTemp.get(i).getDscCostAmt(),dscCostAmtMcSum,9,RoundingMode.HALF_UP)));
                    intDto.setTdyIntincexp(ArithmeticUtil.multiply(hgdto.getTdyIntincexp(),ArithmeticUtil.divide(hgListTemp.get(i).getDscCostAmt(),dscCostAmtMcSum,9,RoundingMode.HALF_UP)));

                    shareAmt = ArithmeticUtil.doAdd(shareAmt,intDto.getShareAmt());
                    dscCostAmt = ArithmeticUtil.doAdd(dscCostAmt,intDto.getDscCostAmt());
                    marketValue = ArithmeticUtil.doAdd(marketValue,intDto.getMarketValue());
                    tdyIntincexp = ArithmeticUtil.doAdd(tdyIntincexp,intDto.getTdyIntincexp());
                }

                //利息计入成本
                if(StringUtil.isNotEmpty(intDto.getTdyIntincexp())){
                    hgListTemp.get(i).setTdyIntincexp(ArithmeticUtil.doAdd(hgListTemp.get(i).getTdyIntincexp(),intDto.getTdyIntincexp()));
                }
                else{
                    hgListTemp.get(i).setTdyIntincexp(ArithmeticUtil.doAdd(hgListTemp.get(i).getTdyIntincexp(),intDto.getDscCostAmt()));
                }
            }
        }
        //过滤汇总利息
        List<PtlSecPierceValutionDto> list = result.stream().filter(x->(!StringUtil.startsWith(x.getSubCode(),"120410")&&!StringUtil.startsWith(x.getSubCode(),"2231"))).collect(Collectors.toList());

        result.clear();

        result.addAll(list);
    }

    private void writeDetailValRift(List<PtlSecPierceValutionDetailDto> resultDetailRep) {
        for(PtlSecPierceValutionDetailDto ptlSecPierceValutionDetailDto : resultDetailRep){
            ptlSecPierceValutionDetailMapper.insertSelective(ptlSecPierceValutionDetailDto);
        }
    }


    /**
     * 释放自主管理预占数据
     */
    private void freedAmPreRskOccupy(List<PtlSecValutionDto> ptlValList,Date currentDate){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        for (PtlSecValutionDto ptlVal : ptlValList) {
            String portfolioId = StringUtil.isNotEmpty(ptlVal.getFinprodMarketId()) ? ptlVal.getFinprodMarketId()
                    : ptlVal.getPortfolioId();
            paramMap.put("cdate", DateUtil.format(currentDate));
            paramMap.put("portfolioId", portfolioId);
            List<String> uuidList=valRiftMapper.selectFreedAmPreRskOccupy(paramMap);
            if(!StringUtil.isEmptyList(uuidList)){
                for(String uuid:uuidList){
                    RskPreOccupyInfo info=new RskPreOccupyInfo();
                    info.setUuid(uuid);
                    info.setOccupyDate(currentDate);
                    rskPreOccupyInfoMapper.updateByPrimaryKeySelective(info);
                }
            }
        }
    }
    /**
     * 计算穿透后指标
     * <功能详细描述>
     * @param result
     * @see
     * @since 1.0
     */
    private List<PtlSecPierceValutionDto> calValIndex(List<PtlSecPierceValutionDto> result){
    	List<PtlSecPierceValutionDto> resultRep = new ArrayList<PtlSecPierceValutionDto>();
        Map<String,PtlSecPierceValutionDto> mapAsset = new HashMap<String,PtlSecPierceValutionDto>();
        Collections.sort(result, Comparator.comparing(o -> (StringUtil.defaultString(o.getSubCode(),""))));
        for(PtlSecPierceValutionDto bean : result){
            if(StringUtil.isNotEmpty(bean.getFinprodId())){
                //add by lpf 剩余现金处理 start
                if(StringUtil.startsWith(bean.getFinprodId(),"REMAININGCASH")){
                    if(mapAsset.containsKey(bean.getFinprodId())){
                        PtlSecPierceValutionDto ptlSecPierceValutionDto  = mapAsset.get(bean.getFinprodId());
                        //剩余现金
                        if(!StringUtil.startsWith(bean.getSubCode(),"120401") &&!StringUtil.startsWith(bean.getSubCode(),"120402")
                                &&!StringUtil.startsWith(bean.getSubCode(),"120403")){
                            //数量
                            if(StringUtil.isNotEmpty(bean.getShareAmt())){
                                bean.setShareAmt(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getShareAmt(),ptlSecPierceValutionDto.getPositionRatio()),
                                        ArithmeticUtil.multiply(bean.getShareAmt(),bean.getPositionRatio())));
                            }
                            else{
                                bean.setShareAmt(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getShareAmt(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                            //摊销总成本
                            if(StringUtil.isNotEmpty(bean.getDscCostAmt())){
                                bean.setDscCostAmt(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getDscCostAmt(),ptlSecPierceValutionDto.getPositionRatio()),
                                        ArithmeticUtil.multiply(bean.getDscCostAmt(),bean.getPositionRatio())));
                            }
                            else{
                                bean.setDscCostAmt(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getDscCostAmt(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                            //市值
                            if(StringUtil.isNotEmpty(bean.getMarketValue())){
                                bean.setMarketValue(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getMarketValue(),ptlSecPierceValutionDto.getPositionRatio()),
                                        ArithmeticUtil.multiply(bean.getMarketValue(),bean.getPositionRatio())));
                            }
                            else{
                                bean.setMarketValue(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getMarketValue(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                        }
                        //利息
                        else{
                            //数量
                            if(StringUtil.isNotEmpty(ptlSecPierceValutionDto.getShareAmt())){
                                bean.setShareAmt(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getShareAmt(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                            //摊销总成本
                            if(StringUtil.isNotEmpty(ptlSecPierceValutionDto.getDscCostAmt())){
                                bean.setDscCostAmt(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getDscCostAmt(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                            //市值
                            if(StringUtil.isNotEmpty(ptlSecPierceValutionDto.getMarketValue())){
                                bean.setMarketValue(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getMarketValue(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                            //利息
                            if(StringUtil.isNotEmpty(bean.getTdyIntincexp())){
                                bean.setTdyIntincexp(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getTdyIntincexp(),ptlSecPierceValutionDto.getPositionRatio()),
                                        ArithmeticUtil.multiply(bean.getTdyIntincexp(),bean.getPositionRatio())));
                            }
                            else{
                                bean.setTdyIntincexp(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getTdyIntincexp(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                        }
                        bean.setPositionRatio(BigDecimal.ONE);//重置持仓比例
                        mapAsset.put(bean.getFinprodId(), bean);
                    }
                    else{
                        //初始化数据，防止只有一条数据
                        if (bean.getMarketValue() != null){
                            bean.setMarketValue(ArithmeticUtil.multiply(bean.getMarketValue(),bean.getPositionRatio()));
                        }
                        if (bean.getTdyIntincexp() != null){
                            bean.setTdyIntincexp(ArithmeticUtil.multiply(bean.getTdyIntincexp(),bean.getPositionRatio()));
                        }
                        if (bean.getShareAmt() != null){
                            bean.setShareAmt(ArithmeticUtil.multiply(bean.getShareAmt(),bean.getPositionRatio()));
                        }
                        if (bean.getDscCostAmt() != null){
                            bean.setDscCostAmt(ArithmeticUtil.multiply(bean.getDscCostAmt(),bean.getPositionRatio()));
                        }
                        bean.setPositionRatio(BigDecimal.ONE);//重置持仓比例
                        mapAsset.put(bean.getFinprodId(), bean);
                    }
                }
                ////add by lpf 剩余现金处理 end
                else{
                    if(mapAsset.containsKey(bean.getFinprodId())){
                        PtlSecPierceValutionDto ptlSecPierceValutionDto  = mapAsset.get(bean.getFinprodId());
                        //数量
                        if(StringUtil.isNotEmpty(ptlSecPierceValutionDto.getShareAmt())){
                            if(StringUtil.isNotEmpty(bean.getShareAmt())){
                                bean.setShareAmt(ArithmeticUtil.doAdd(2,RoundingMode.HALF_UP,ArithmeticUtil.multiply(ptlSecPierceValutionDto.getShareAmt(),ptlSecPierceValutionDto.getPositionRatio()),
                                        ArithmeticUtil.multiply(bean.getShareAmt(),bean.getPositionRatio())));
                            }else{
                                //update by xr for TISHKBS-405
                                bean.setShareAmt(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getShareAmt(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                        }else{
                            if(StringUtil.isNotEmpty(bean.getShareAmt())){
                                bean.setShareAmt(ArithmeticUtil.multiply(bean.getShareAmt(),bean.getPositionRatio()));
                            }
                        }
                        //摊销总成本
                        if(StringUtil.isNotEmpty(ptlSecPierceValutionDto.getDscCostAmt())){
                            if (StringUtil.isNotEmpty(bean.getDscCostAmt()) && !StringUtil.startsWith(bean.getSubCode(),"1204")
                                    && !StringUtil.startsWith(bean.getSubCode(),"1203")){
                                    bean.setDscCostAmt(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getDscCostAmt(),ptlSecPierceValutionDto.getPositionRatio()),
                                            ArithmeticUtil.multiply(bean.getDscCostAmt(),bean.getPositionRatio())));
                            }else{
                                bean.setDscCostAmt(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getDscCostAmt(),ptlSecPierceValutionDto.getPositionRatio()));
                            }
                        }else{
                            if (StringUtil.isNotEmpty(bean.getDscCostAmt()) && !StringUtil.startsWith(bean.getSubCode(),"1204")
                                    && !StringUtil.startsWith(bean.getSubCode(),"1203")){
                                //新增的数据摊销总成本不为零时计算后存入bean中，后续的比例会统一置为 1
                                bean.setDscCostAmt(ArithmeticUtil.multiply(bean.getDscCostAmt(),bean.getPositionRatio()));
                            }
                        }
                        //利息
                        if(StringUtil.isNotEmpty(ptlSecPierceValutionDto.getTdyIntincexp())){
                            if(StringUtil.isNotEmpty(bean.getTdyIntincexp()) && (StringUtil.startsWith(bean.getSubCode(),"1204")
                                    || StringUtil.startsWith(bean.getSubCode(),"1203") //基金
                                    || StringUtil.isEmpty(bean.getSubCode()) //直投
                            )){
                                bean.setTdyIntincexp(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getTdyIntincexp(),ptlSecPierceValutionDto.getPositionRatio()),
                                        ArithmeticUtil.multiply(bean.getTdyIntincexp(),bean.getPositionRatio())));
                            }else{
                                    bean.setTdyIntincexp(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getTdyIntincexp(),ptlSecPierceValutionDto.getPositionRatio()));
                                }
                        }else{
                            if(StringUtil.isNotEmpty(bean.getTdyIntincexp()) && (StringUtil.startsWith(bean.getSubCode(),"1204")
                                    || StringUtil.startsWith(bean.getSubCode(),"1203") //基金
                                    || StringUtil.isEmpty(bean.getSubCode()) //直投
                            )){
                                bean.setTdyIntincexp(ArithmeticUtil.multiply(bean.getTdyIntincexp(),bean.getPositionRatio()));
                            }
                        }
                        //add by zhagnnian 20191014 end
                        //市值
                        if(StringUtil.isNotEmpty(ptlSecPierceValutionDto.getMarketValue())){
                             if(StringUtil.isNotEmpty(bean.getMarketValue())){
                                bean.setMarketValue(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getMarketValue(),ptlSecPierceValutionDto.getPositionRatio()),
                                        ArithmeticUtil.multiply(bean.getMarketValue(),bean.getPositionRatio())));
                             }else{
                                 bean.setMarketValue(ArithmeticUtil.multiply(ptlSecPierceValutionDto.getMarketValue(),ptlSecPierceValutionDto.getPositionRatio()));
                             }
                        }else{
                            if(StringUtil.isNotEmpty(bean.getMarketValue())){
                                bean.setMarketValue(ArithmeticUtil.multiply(bean.getMarketValue(),bean.getPositionRatio()));
                            }
                        }
                        bean.setPositionRatio(new BigDecimal(1));//重置持仓比例
                        mapAsset.put(bean.getFinprodId(), bean);
                    }else{
                        //市值单独计算
                        // 市值= 摊销总成本+isnull(公允价值变动,当日应计利息)
                        /*bean.setMarketValue(ArithmeticUtil.doAdd(ArithmeticUtil.doAdd(null!=bean.getDscCostAmt()?bean.getDscCostAmt():BigDecimal.ZERO,null!=bean.getTdyFloatIngpl()?bean.getTdyFloatIngpl():BigDecimal.ZERO),null!=bean.getTdyIntincexp()?bean.getTdyIntincexp():BigDecimal.ZERO));*/
                        mapAsset.put(bean.getFinprodId(), bean);
                    }
                }

            }
        }
        if (mapAsset.get("REMAININGCASH_BJ")!= null && mapAsset.get("REMAININGCASH_LX")!= null){
            //处理结束后将利息放入现金数据中
            PtlSecPierceValutionDto bjDto = mapAsset.get("REMAININGCASH_BJ");
            PtlSecPierceValutionDto lxDto = mapAsset.get("REMAININGCASH_LX");
            bjDto.setMarketValue(ArithmeticUtil.multiply(2,RoundingMode.HALF_UP,bjDto.getMarketValue(),bjDto.getPositionRatio()));
            bjDto.setTdyIntincexp(ArithmeticUtil.multiply(2,RoundingMode.HALF_UP,lxDto.getTdyIntincexp(),lxDto.getPositionRatio()));
            bjDto.setFinprodId("REMAININGCASH");
            mapAsset.put("REMAININGCASH",bjDto);
            mapAsset.remove("REMAININGCASH_LX");
            mapAsset.remove("REMAININGCASH_BJ");
        }

        if(mapAsset.keySet() != null &&  mapAsset.keySet().size()> 0){
            PtlSecPierceValutionDto cashDto = new PtlSecPierceValutionDto();
            if (mapAsset.get("REMAININGCASH") == null){
                String portfolioId = null;
                Date cDate = null;
                for (String s : mapAsset.keySet()) {
                    if(StringUtil.isEmpty(portfolioId)){
                        portfolioId = mapAsset.get(s).getPortfolioId();
                    }
                    if(cDate == null){
                        cDate = mapAsset.get(s).getCdate();
                    }
                }
                cashDto.setPortfolioId(portfolioId);
                cashDto.setCdate(cDate);
                cashDto.setFinprodId("REMAININGCASH");
            } else{
                cashDto = mapAsset.get("REMAININGCASH");
            }

            //初始化数据
            cashDto.setTdyIntincexp(cashDto.getTdyIntincexp() == null ? BigDecimal.ZERO : cashDto.getTdyIntincexp());
            cashDto.setMarketValue(cashDto.getMarketValue() == null ? BigDecimal.ZERO : cashDto.getMarketValue());

            List<String> idList = new ArrayList<>();
            //现金数据相加
            PtlSecPierceValutionDto finalCashDto = cashDto;
            mapAsset.keySet().forEach(item->{
                //如果资产代码中包含：则为现金数据
                if(StringUtil.indexOf(mapAsset.get(item).getFinprodId(),":")>0){
                    idList.add(mapAsset.get(item).getFinprodId());
                    PtlSecPierceValutionDto itemDto = mapAsset.get(item);
                    finalCashDto.setTdyIntincexp(finalCashDto.getTdyIntincexp().add(itemDto.getTdyIntincexp() == null?BigDecimal.ZERO : itemDto.getTdyIntincexp()));
                    finalCashDto.setMarketValue(finalCashDto.getMarketValue().add(itemDto.getMarketValue() == null?BigDecimal.ZERO : itemDto.getMarketValue()));
                }
            });
            mapAsset.put("REMAININGCASH",finalCashDto);

            //移除已经合并的数据
            idList.forEach(item->{
                mapAsset.remove(item);
            });
        }

        //-----add by 裴家俊  2019/04/03----重新构造避免重复金融产品代码---start----
    	for (Entry<String, PtlSecPierceValutionDto> entry : mapAsset.entrySet()) {
			if (entry.getValue().getShareAmt() != null
					) {
				entry.getValue().setShareAmt(ArithmeticUtil.multiply(2,RoundingMode.HALF_UP,entry.getValue().getShareAmt()
						,entry.getValue().getPositionRatio()));
			}
			if (entry.getValue().getDscCostAmt() != null ) {
				entry.getValue().setDscCostAmt(ArithmeticUtil.multiply(2,RoundingMode.HALF_UP,entry.getValue().getDscCostAmt()
						,entry.getValue().getPositionRatio()));
			}
            //市值单独计算
            // 市值= 摊销总成本+isnull(公允价值变动,当日应计利息)
			if (entry.getValue().getMarketValue() == null) {
				entry.getValue().setMarketValue(ArithmeticUtil.multiply(2,RoundingMode.HALF_UP,
				        ArithmeticUtil.doAdd(
				                null!=entry.getValue().getDscCostAmt()?entry.getValue().getDscCostAmt():BigDecimal.ZERO
                                ,null!=entry.getValue().getTdyFloatIngpl()?entry.getValue().getTdyFloatIngpl():BigDecimal.ZERO
                                ,null!=entry.getValue().getTdyIntincexp()?entry.getValue().getTdyIntincexp():BigDecimal.ZERO)
						,entry.getValue().getPositionRatio()));
			}else{
                entry.getValue().setMarketValue(ArithmeticUtil.multiply(2,RoundingMode.HALF_UP,entry.getValue().getMarketValue()
                        ,entry.getValue().getPositionRatio()));
            }
			if (entry.getValue().getTdyIntincexp() != null ) {
				entry.getValue().setTdyIntincexp(ArithmeticUtil.multiply(2,RoundingMode.HALF_UP,entry.getValue().getTdyIntincexp()
						,entry.getValue().getPositionRatio()));
			}
			resultRep.add(entry.getValue());
		}
    	return resultRep;
    	//-----add by 裴家俊  2019/04/03----重新构造避免重复金融产品代码---end----
    }

    /**
     * 计算穿透后指标 增加资管计划的维度
     * <功能详细描述>
     * @param result
     * @see
     * @since 1.0
     */
    private List<PtlSecPierceValutionDetailDto> calValIndexDetail(List<PtlSecPierceValutionDetailDto> result){
    	List<PtlSecPierceValutionDetailDto> resultRep = new ArrayList<PtlSecPierceValutionDetailDto>();
        Map<String,PtlSecPierceValutionDetailDto> mapAsset = new HashMap<String,PtlSecPierceValutionDetailDto>();
        Collections.sort(result, new Comparator<PtlSecPierceValutionDetailDto>() {
            @Override
            public int compare(PtlSecPierceValutionDetailDto o1, PtlSecPierceValutionDetailDto o2) {
                return (StringUtil.isEmpty(o1.getSubCode()) ? "":o1.getSubCode()).compareTo(
                        (StringUtil.isEmpty(o2.getSubCode())  ? "":o2.getSubCode())
                );
            }
        });
        for(PtlSecPierceValutionDetailDto bean : result){
            if(StringUtil.isNotEmpty(bean.getFinprodId())){
                    if(mapAsset.containsKey(bean.getFinprodId()+bean.getZgplanId())){
                        PtlSecPierceValutionDetailDto ptlSecPierceValutionDetailDto  = (PtlSecPierceValutionDetailDto)mapAsset.get(bean.getFinprodId()+bean.getZgplanId());
                        //数量
                        if(StringUtil.isNotEmpty(ptlSecPierceValutionDetailDto.getShareAmt())){
                            if(StringUtil.isNotEmpty(bean.getShareAmt())){
                                bean.setShareAmt(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getShareAmt(),ptlSecPierceValutionDetailDto.getPositionRatio()),
                                        ArithmeticUtil.multiply(bean.getShareAmt(),bean.getPositionRatio())));
                            }else{
								//update by xr for TISHKBS-405
                                bean.setShareAmt(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getShareAmt(),ptlSecPierceValutionDetailDto.getPositionRatio()));
                            }
                        }
                        //摊销总成本
                        if(StringUtil.isNotEmpty(ptlSecPierceValutionDetailDto.getDscCostAmt())
                                && StringUtil.isNotEmpty(bean.getDscCostAmt())){
                            if(!StringUtil.startsWith(ptlSecPierceValutionDetailDto.getSubCode(),"1204")
                            && !StringUtil.startsWith(ptlSecPierceValutionDetailDto.getSubCode(),"1203")
                            ){
                                if(!StringUtil.startsWith(bean.getSubCode(),"1204")
                                && !StringUtil.startsWith(bean.getSubCode(),"1203")
                                ){
                                    bean.setDscCostAmt(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getDscCostAmt(),ptlSecPierceValutionDetailDto.getPositionRatio()),
                                            ArithmeticUtil.multiply(bean.getDscCostAmt(),bean.getPositionRatio())));
                                }else{
									//update by xr for TISHKBS-405
                                    bean.setDscCostAmt(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getDscCostAmt(),ptlSecPierceValutionDetailDto.getPositionRatio()));
                                }
                            }
                        }
                        //add by zhagnnian 20191014 start
                        //利息
                        if(StringUtil.isNotEmpty(ptlSecPierceValutionDetailDto.getTdyIntincexp())
                                ){
                            if(StringUtil.isNotEmpty(bean.getTdyIntincexp())){

                                if(StringUtil.startsWith(bean.getSubCode(),"1204")
                                || StringUtil.startsWith(bean.getSubCode(),"1203") //基金
                                        || StringUtil.isEmpty(bean.getSubCode()) //直投
                                ){
                                    bean.setTdyIntincexp(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getTdyIntincexp(),ptlSecPierceValutionDetailDto.getPositionRatio()),
                                            ArithmeticUtil.multiply(bean.getTdyIntincexp(),bean.getPositionRatio())));
                                }else{
									//update by xr for TISHKBS-405
                                    bean.setTdyIntincexp(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getTdyIntincexp(),ptlSecPierceValutionDetailDto.getPositionRatio()));
                                }
                            }else{
									//update by xr for TISHKBS-405
                                    bean.setTdyIntincexp(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getTdyIntincexp(),ptlSecPierceValutionDetailDto.getPositionRatio()));
                            }
                        }
                        //add by zhagnnian 20191014 end
                        //市值
                        if(StringUtil.isNotEmpty(ptlSecPierceValutionDetailDto.getMarketValue())
                                && StringUtil.isNotEmpty(bean.getMarketValue())){
                            if(!StringUtil.startsWith(ptlSecPierceValutionDetailDto.getSubCode(),"1204")
                            && !StringUtil.startsWith(ptlSecPierceValutionDetailDto.getSubCode(),"1203")
                            ){
                                if(!StringUtil.startsWith(bean.getSubCode(),"1204")
                                && !StringUtil.startsWith(bean.getSubCode(),"1203")
                                ){
                                    bean.setMarketValue(ArithmeticUtil.doAdd(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getMarketValue(),ptlSecPierceValutionDetailDto.getPositionRatio()),
                                            ArithmeticUtil.multiply(bean.getMarketValue(),bean.getPositionRatio())));
                                }else{
									//update by xr for TISHKBS-405
                                    bean.setMarketValue(ArithmeticUtil.multiply(ptlSecPierceValutionDetailDto.getMarketValue(),ptlSecPierceValutionDetailDto.getPositionRatio()));
                                }
                            }
                        }
                        bean.setZgplanId(ptlSecPierceValutionDetailDto.getZgplanId());
                        String zgplanId = ptlSecPierceValutionDetailDto.getZgplanId() == null?bean.getZgplanId():ptlSecPierceValutionDetailDto.getZgplanId();
                        bean.setZgplanId(zgplanId);
                        bean.setPositionRatio(new BigDecimal(1));//重置持仓比例
                        mapAsset.put(bean.getFinprodId()+bean.getZgplanId(), bean);
                }else{
                    //市值单独计算
                    // 市值= 摊销总成本+isnull(公允价值变动,当日应计利息)
                    /*bean.setMarketValue(ArithmeticUtil.doAdd(ArithmeticUtil.doAdd(null!=bean.getDscCostAmt()?bean.getDscCostAmt():BigDecimal.ZERO,null!=bean.getTdyFloatIngpl()?bean.getTdyFloatIngpl():BigDecimal.ZERO),null!=bean.getTdyIntincexp()?bean.getTdyIntincexp():BigDecimal.ZERO));*/
                    mapAsset.put(bean.getFinprodId()+bean.getZgplanId(), bean);
                }
            }
        }
        //-----add by 裴家俊  2019/04/03----重新构造避免重复金融产品代码---start----
    	for (Entry<String, PtlSecPierceValutionDetailDto> entry : mapAsset.entrySet()) {
			if (entry.getValue().getShareAmt() != null
					) {
				entry.getValue().setShareAmt(ArithmeticUtil.multiply(entry.getValue().getShareAmt()
						,entry.getValue().getPositionRatio()));
			}
			if (entry.getValue().getDscCostAmt() != null ) {
				entry.getValue().setDscCostAmt(ArithmeticUtil.multiply(entry.getValue().getDscCostAmt()
						,entry.getValue().getPositionRatio()));
			}
            //市值单独计算
            // 市值= 摊销总成本+isnull(公允价值变动,当日应计利息)
			if (entry.getValue().getMarketValue() == null) {
				entry.getValue().setMarketValue(ArithmeticUtil.multiply(ArithmeticUtil.doAdd(ArithmeticUtil.doAdd(null!=entry.getValue().getDscCostAmt()?entry.getValue().getDscCostAmt():BigDecimal.ZERO,null!=entry.getValue().getTdyFloatIngpl()?entry.getValue().getTdyFloatIngpl():BigDecimal.ZERO),null!=entry.getValue().getTdyIntincexp()?entry.getValue().getTdyIntincexp():BigDecimal.ZERO)
						,entry.getValue().getPositionRatio()));
			}else{
                entry.getValue().setMarketValue(ArithmeticUtil.multiply(entry.getValue().getMarketValue()
                        ,entry.getValue().getPositionRatio()));
            }
			if (entry.getValue().getTdyIntincexp() != null ) {
				entry.getValue().setTdyIntincexp(ArithmeticUtil.multiply(entry.getValue().getTdyIntincexp()
						,entry.getValue().getPositionRatio()));
			}
			resultRep.add(entry.getValue());
		}
    	return resultRep;
    	//-----add by 裴家俊  2019/04/03----重新构造避免重复金融产品代码---end----
    }
    
    /**
     * 处理穿透底层资产---add by 裴家俊---2019/04/03-----
     * <功能详细描述>
     * @param portfolioId
     * @param vdate
     * @param shareAmt 理财产品数量
     * @param positionList
     * @see
     * @since 1.0
     */
    private List<ValtblAssetSubDto> dealRiftByPtlId(String portfolioId, Date vdate, BigDecimal shareAmt, List<ValtblAssetSubDto> positionList){
        SubPtlNode node = new SubPtlNode(portfolioId);
        ValtblAssetSubDto valAssetEntry=new ValtblAssetSubDto();
        valAssetEntry.setFinprodMarketId(portfolioId);
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("cdate", DateUtil.format(vdate));
        paramMap.put("portfolioId", node.getCode());
        //实收资本
        BigDecimal valCapital = valRiftMapper.selectValCapital(paramMap);
        List<ValtblAssetSubDto> leafAssetPlan = new ArrayList<>();
        if(null!=valCapital&&valCapital!= BigDecimal.ZERO){
            node.setPositionRatio(ArithmeticUtil.divide(ArithmeticUtil.multiply(node.getPositionRatio(),StringUtil.isNotEmpty(shareAmt)?shareAmt:BigDecimal.ZERO), valCapital));
        }
        
        if(null!=positionList&&!positionList.isEmpty()){
            for(ValtblAssetSubDto valtblAssetSubDto : positionList){
                valtblAssetSubDto.setPositionRatio(node.getPositionRatio());
                if(StringUtil.isEmpty(valtblAssetSubDto.getFinprodId())){
                    valtblAssetSubDto.setFinprodId(Long.toString(node.getNextVirtAssetObjectId()));
                }
                valtblAssetSubDto.setPtlId(portfolioId);
                leafAssetPlan.add(valtblAssetSubDto); // 添加到List中等待处理
            }
        }
        return leafAssetPlan;
    }
    
    /**
     * 获取参加任务的组合数
     * <功能详细描述>
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    public List<PtlSecValutionDto> selectPtlIdForTask(Map<String,Object> paramMap){
        return valRiftMapper.selectPtlIdForTask(paramMap);
    }
    
    /**
     * 获取组合估值资产
     * <功能详细描述>
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    public List<PtlSecValutionDto> selectPtlValAsset(Map<String,Object> paramMap){
        return valRiftMapper.selectPtlValAsset(paramMap);
    }
    
    /**
     * 保存穿透后的估值数据
     * <功能详细描述>
     * @param result
     * @see
     * @since 1.0
     */
    private void  writeValRift(List<PtlSecPierceValutionDto> result){
        for(PtlSecPierceValutionDto ptlSecPierceValutionDto : result){
            ptlSecPierceValutionMapper.insertSelective(ptlSecPierceValutionDto);
        }
    }
    
    /**
     * 删除已跑批的穿透数据
     * <功能详细描述>
     * @see
     * @since 1.0
     */
    private void deleteValRift(Date currentDate,PtlSecValutionDto ptl){
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("portfolioId", ptl.getPortfolioId());
        paramMap.put("cdate", currentDate);
        valRiftMapper.deleteValRift(paramMap);
        //add by lpf 增加资管计划维度明细
        valRiftMapper.deleteDetailValRift(paramMap);
    }
    /**
     * 查询指定日期范围导入估值表的产品
     *
     * @param supDate
     * @param productIdList
     * @return
     */
    public List<Map<String,Object>> selectImportValTableProdList(Date supDate, List<String> productIdList,String deptCode) {
        //参数
        Map<String,Object> param = new HashMap<>();
        //系统参数配置 指定日期范围内天数 联社：一个月以内，16天可以  杭联：1个月2次 默认16天
        String importValDay = StringUtil.defaultString(CacheUtil.getSysParam("MAX_SUB_VALDATE"),"16");
        //计算最小日期
        Date dateMin = DateUtil.addDay(supDate,-1*Integer.valueOf(importValDay));

        param.put("dateMin",dateMin);
        param.put("dateMax",supDate);
        param.put("deptCode", deptCode);
        List<Map<String,Object>> resultList = new ArrayList<>();
        // update by caiqun 2021-09-24 DataHandleUtil在估值代码里，不能使用，并且sql已对语句过长做了处理 start
        param.put("productIdList",productIdList);
        //查询产品对应最小跑批日期
        resultList.addAll(valRiftMapper.selectImportValTableProdList(param));
        //防止语句过长
        /*if(productIdList == null || productIdList.size()<800) {
            param.put("productIdList",productIdList);
            //查询产品对应最小跑批日期
            resultList.addAll(valRiftMapper.selectImportValTableProdList(param));
        }
        else{
            //分组查询防止语句过长
            List<List<String>> totalList = DataHandleUtil.splitList(productIdList,800);
            for (List<String> evrylist : totalList) {
                param.put("productIdList",evrylist);
                //查询产品对应最小跑批日期
                List<Map<String,Object>> evryResList = valRiftMapper.selectImportValTableProdList(param);
                if(StringUtil.isNotEmpty(evryResList)){
                    resultList.addAll(evryResList);
                }
            }
        }*/
        // update by caiqun 2021-09-24 DataHandleUtil在估值代码里，不能使用，并且sql已对语句过长做了处理 end
        return resultList;
    }
}
