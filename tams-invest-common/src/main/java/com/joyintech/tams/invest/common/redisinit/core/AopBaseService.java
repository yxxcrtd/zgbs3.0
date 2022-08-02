/*
 * 文件名：AopBaseService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年4月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.redisinit.core;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constantrds.BussinessConstantRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.redisinit.dto.IRiskRds;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 基础抽象类组件Service
 * @author 裴家俊
 * @version 1.0 2020年4月18日
 * @see AopBaseService
 * @since 1.0
 */
public abstract class AopBaseService<V extends IRiskRds>  {

    private Logger logger = LoggerFactory.getLogger(AopBaseService.class);

    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private AssetInfoRedisService assetInfoSrc;

    @Autowired
    private FinProductMapper finProductMapper;

    public interface IInfCallback<V>{
        ClientData receive(Map<String,Object> t, V v) throws IOException, InstantiationException;
    }

    /**
     * 处理调用后台风险接口的中间处理过程
     * @param joinPoint  切面参数
     * @param v
     * @param iInfRds  要调用的方法
     * @throws BusinessException
     */
    public  void completePeriodCommon(JoinPoint joinPoint,V v,IInfCallback<V> iInfRds) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        OperationType operationType = (OperationType) args[1];
        Map<String,Object> mapTrd= JsonUtils.object2Map(args[0]);
        //在redis里查询资产类型
        String assetType=StringUtil.EMPTY;
        if(StringUtil.isNotEmpty(mapTrd.get(Const.finprodId))){
            Map<String, IAssetInfo> mapAsset = assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(), Arrays.asList(mapTrd.get(Const.finprodId).toString()));
            if(StringUtil.isNotEmpty(mapAsset)&&StringUtil.isNotEmpty(mapAsset.values())) {
                for (IAssetInfo iAst : mapAsset.values()) {
                    assetType = StringUtil.isNotEmpty(iAst)?iAst.getAssetType():assetType;
                }
            }
            if (!StringUtil.isNotEmpty(mapTrd.get("mDate")) && !StringUtil.isNotEmpty(mapTrd.get("mdate"))) {
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(mapTrd.get(Const.finprodId).toString());
                mapTrd.put("mdate", finProduct.getMdate());
            }
        }
        v.setAssetType(assetType);
        revokeCompleteRiskCommon(mapTrd,joinPoint,en->{
            //把各实现类中自己封装的实体再赋值进全量map集合中
            if(StringUtil.isNotEmpty(en)) {
                mapTrd.putAll(JsonUtils.object2Map(en));
            }
            //调用实占或释放接口
            try {
                //设置接口代码，实占RSK002,实占释放RSK003
                v.setInfCode(operationType.equals(OperationType.REVOKE)?BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue():BussinessDictCodeListRds.接口系统ID_实占接口.getValue());
                ClientData result = iInfRds.receive(mapTrd, v);
                if(StringUtil.isNotEmpty(result)&&StringUtil.isNotEmpty(result.getHead())&&StringUtil.isNotEmpty(result.getHead().get(BussinessConstantRds.std400msgt))){
                    if(StringUtil.equals(result.getHead().get(BussinessConstantRds.std400msgt).toString(),BussinessConstantRds.E)&&StringUtil.isNotEmpty(result.getHead().get(BussinessConstantRds.stdrtninfo))){
                        throw new BusinessException(BussinessConstantRds.MSG+result.getHead().get(BussinessConstantRds.stdrtninfo).toString());
                    }
                }
            } catch (Exception e) {
                logger.error("===completePeriodCommon error===", e);
                throw new BusinessException(e);
            }
        });
    }

    /**
     * 调用共通处理逻辑获取基本要素
     * @param mapTrd
     * @param joinPoint
     * @param var
     * @throws BusinessException
     */
    public void revokeCompleteRiskCommon(Map<String,Object> mapTrd, JoinPoint joinPoint, Consumer<Object> var) throws BusinessException {
        try {
            //调用实体构造，在各实现类中
            assembleCalEntity(mapTrd,joinPoint);
        } catch (BusinessException e) {
            throw e;
        }
        //设置每个交易品种过来的类型(分配的明细列表)，各实现类可重载
        Object obj=setEachAstType(mapTrd, joinPoint);
        //有些把资产按组合维度来分配的，所以产生多笔，比如债券，项目类等
        if(StringUtil.isNotEmpty(obj)){
            if(obj instanceof String[] ){
                String[] arrObj = (String[]) obj;
                if(arrObj.length>0){
                    Arrays.asList(arrObj).forEach(var);
                }else{
                    var.accept(null);
                }
             }
             else if(obj instanceof List){
                 List<?> listMap =(List<?>)obj;
                 if(StringUtil.isEmptyList(listMap)) {
                     var.accept(null); 
                 } 
                 else {
                     listMap.forEach(var);
                 }
             } else{
                 var.accept(null);
             }
        }else {
            //比如回购，拆借等都是单笔
            var.accept(null);
        }
    }



    /**
     * 赋值交易方向与资产类型
     * @param mapTrd
     * @param ps
     * @param finType
     */
    public void setBasicTradeInfo(Map<String,Object> mapTrd,String ps ,String finType){
        mapTrd.put(BussinessConstantRds.ps, ps);
        if(!StringUtil.isNotEmpty(mapTrd.get(BussinessConstantRds.finprodType2))){
            mapTrd.put(BussinessConstantRds.finprodType2, finType);
        }
    }

    /**
     * 设置每个交易品种过来的类型(分配的明细列表)
     * @param mapTrd
     * @param joinPoint
     */
    protected Object setEachAstType(Map<String,Object> mapTrd,JoinPoint joinPoint){
        //这里是默认的实体key,针对存续期的
        return mapTrd.get("finAssetIntInfoList");
    }

    protected abstract void assembleCalEntity(Map<String,Object> mapTrd,JoinPoint joinPoint) throws BusinessException;

}