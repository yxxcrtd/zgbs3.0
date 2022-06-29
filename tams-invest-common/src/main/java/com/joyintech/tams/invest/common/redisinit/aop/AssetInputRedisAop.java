/*
 * 文件名：AssetInputRedisAop.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.common.redisinit.aop;

import java.util.*;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.riskinfrds.param.entity.IParam;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.dto.ImplParaRds;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.redisinit.service.RiskRefreshRedisService;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;

/**
 * 资产新增或修改时同步进redis逻辑aop
 *
 * @author 裴家俊
 * @version 1.0 2020年1月7日
 * @see AssetInputRedisAop
 * @since 1.0
 */
@Component
@Aspect
public class AssetInputRedisAop {
    
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(AssetInputRedisAop.class);

    /**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private RiskRefreshRedisService riskRefreshRedisService;

    private List<String> assetTypeList= Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券.getValue()
            , DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue(),DictCodeListExt1.金融产品类型_投管用_基金.getValue()
            ,DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue(),DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue()
            ,BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue(),DictCodeListExt1.金融产品类型_投管用_存款.getValue());

    private static Map<String,String> commonBusinessMap=new HashMap<String, String>(){
        {
            put("F01", DictCodeList.共通业务类型_债券类资产信息.getValue());
            put("F07", DictCodeList.共通业务类型_存款类资产信息.getValue());
            put("F17", DictCodeList.共通业务类型_计划类资产_净值型_信息.getValue());
            put("F06", DictCodeList.共通业务类型_基金类资产信息.getValue());
            put("F18", DictCodeList.共通业务类型_项目类资产信息.getValue());
            put("F19", DictCodeListExt3.共通业务类型_权益类资产信息.getValue());
            put("F32", BussinessDictCodeList.共同业务类型_一级投标债券类资产管理.getValue());
        }
    };
    /**
     * 平台日
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired(required = false)
    private IndicatorParamService indicatorParamService;

    @Pointcut("execution(* com.joyintech.tams.common.base.dao.FinProductMapper.insert(..))")
    protected void afterPoint() {

    }

    @After("afterPoint()")
    public void afterAssetMethod(JoinPoint joinPoint){
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        if (CommonUtil.isType(CacheUtil.getSysParam(RiskUtil.PAGE_REDIS_FLAG), BussinessDictCodeListRds.是否_是)) {
            Object[] args = joinPoint.getArgs();
            FinProduct finProduct = (FinProduct) args[0];
            //更新者为wind  默认为批处理数据   不需要刷新redis
            if(!StringUtil.equals(finProduct.getUpdateUser(),"wind")){
                //调用功能模块刷对应参数(目前只刷债券，新发行债券与项目类)
                if(assetTypeList.contains(finProduct.getFinprodType2())){
                    logger.info(finProduct.getFinprodType2()+"资产更新指标参数redis开始");
                    ImplParaRds implParaRds=new ImplParaRds();
                    implParaRds.setCommonBusinessType(commonBusinessMap.get(finProduct.getFinprodType2()));
                    try{
                        riskRefreshRedisService.refreshParamRedis(implParaRds);
                    }catch (Exception e){
                        //异常无需抛出，在内部处理掉
                        logger.error(finProduct.getFinprodType2()+"资产更新指标参数redis异常",e);
                    }
                }
                threadPoolTaskExecutor.execute(()->{
                    try {
                        Thread.sleep(6000);
                        logger.info("===========资产进入redis开始=============");
                        ImplCond cond = new ImplCond();
                        cond.setDel(false);
                        if (StringUtil.isNotEmpty(finProduct) && StringUtil.isNotEmpty(finProduct.getFinprodId())) {
                            cond.setAssetIdArray(finProduct.getFinprodId());
                            // 设置平台时间
                            cond.setPlateDate(iPlatformDate.getSysDate());
                            // 更新Redis
                            redisRriter.dealAssetData(cond);
                            // 更新指标参数值
                            if(indicatorParamService!=null) {
                                String[] keys = {RedisKeyEnum.指标参数配置.key()};
                                Map<String,Map<String, IParam>> params = indicatorParamService.readAll(Arrays.asList(keys));
                                ImplCond condp = new ImplCond();
                                condp.setPlateDate(iPlatformDate.getSysDate());
                                condp.setDel(false);
                                condp.setLoadOffset(true);
                                condp.setIsUpdParVal(true);
                                condp.setSource("asset");
                                condp.setCondList(Arrays.asList(finProduct.getFinprodId()));
                                Set<String> fields = new HashSet<>();
                                for(IParam iParam:params.get(RedisKeyEnum.指标参数配置.key()).values()) {
                                    if(iParam.getRedisFieldArr()!=null&&iParam.getRedisFieldArr().length==1&&StringUtil.equals(iParam.getRedisFieldArr()[0], "assetCode")) {
                                        String field = iParam.getField();
                                        field = StringUtil.contains(field, GlobalConstant.COLON)? StringUtil.substring(field, 0, StringUtil.indexOf(field, GlobalConstant.COLON)): field;
                                        fields.add(field);
                                    }
                                }
                                for(String field: fields) {
                                    condp.setParamCodeArray(field);
                                    redisRriter.dealRiskIndParam(condp);
                                }
                            }
                            logger.info("===========资产进入redis结束=============");
                        }
                    }catch (Exception e){
                        //异常无需抛出，在内部处理掉
                        logger.error("资产进入redis异常",e);
                    }
                });
            }
        }
    }
}
