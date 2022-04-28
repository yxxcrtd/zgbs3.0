/*
 * 文件名：BatchCacheService.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：杨智
 * 修改时间：2017年6月13日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.cache.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.joyintech.tams.common.base.entity.BokMatchRlat;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.service.BatchCacheService;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.tams.valuation.batch.common.cache.dao.BatchCacheMapper;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.SysParaDto;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.ValBokBussDetsubMem;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.ValBokSubjectDetailMem;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;


/**
 * 批处理缓存用service  【方法注释，使用投管该方法】
 *
 * @author 强成西
 * @version 1.0 2017年6月13日
 * @see ValuationBatchCacheService
 * @since 1.0
 */
@Service
public class ValuationBatchCacheService extends BatchCacheService {

    @Value("${batch.properties.maxSqlNum}")
    public int maxSqlNum;

    private static final String SYS_CODE_ = "SYS_CODE_";

    /**
     * BatchCacheMapper
     */
    @Autowired
    private BatchCacheMapper batchCacheMapper;

    @Autowired
    private AccountingMethodMapper accountingMethodMapper;

    /**
     * 获取所有明细科目的key
     *
     * @return set
     * @see
     * @since 1.0
     */
    public Set<ValBokSubjectDetailMem> findAllDetailSubjectKey() {
        List<ValBokSubjectDetailMem> keyList = batchCacheMapper.findAllDetailSubject();
        if (keyList == null) {
            keyList = new ArrayList<ValBokSubjectDetailMem>();
        }
        return Collections.synchronizedSet(new HashSet<>(keyList));
    }

    /**
     * 获取所有业务明细科目的key
     *
     * @return set
     * @see
     * @since 1.0
     */
    public Set<ValBokBussDetsubMem> findAllBussDetsubKey() {
        List<ValBokBussDetsubMem> keyList = batchCacheMapper.findAllBussDetsubKey();
        if (keyList == null) {
            keyList = new ArrayList<ValBokBussDetsubMem>();
        }
        return Collections.synchronizedSet(new HashSet<>(keyList));
    }

    /**
     * 获取所有业务明细科目的key
     *
     * @return set
     * @see
     * @since 1.0
     */
    public Map<String, Object> findAllSysParam() {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        List<SysParaDto> paraList = batchCacheMapper.selectAllSysParam();
        DynamicDataSourceHolder.clearDataSource();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (CollectionUtils.isNotEmpty(paraList)) {
            for (SysParaDto sysPara : paraList) {
                resultMap.put(sysPara.getParaCode(), sysPara.getParamValue());
                // sysPara 
                if (resultMap.get(SYS_CODE_ + sysPara.getSystemCode()) == null) {
                    Map<String, String> map = new HashMap<>();
                    map.put(sysPara.getParaCode(), sysPara.getParamValue());
                    resultMap.put(SYS_CODE_ + sysPara.getSystemCode(), map);
                } else {
                    ((Map<String, String>) resultMap.get(SYS_CODE_ + sysPara.getSystemCode())).put(sysPara.getParaCode(), sysPara.getParamValue());
                }
            }
        }
        return resultMap;
    }

    public List<BokMatchRlat> findAllBokMatchRlat() {
        return batchCacheMapper.findAllBokMatchRlat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putCache() {
        CacheUtil.putAppCache(GlobalConstant.DETAIL_SUBJECT, findAllDetailSubjectKey());
        CacheUtil.putAppCache(GlobalConstant.BUSS_DETSUB, findAllBussDetsubKey());
        CacheUtil.putAppCache(GlobalConstant.SYS_PARA_KEY, findAllSysParam());
        CacheUtil.putAppCache(GlobalConstant.MAX_SQL_NUM, maxSqlNum);
        CacheUtil.putAppCache(GlobalConstant.BOK_MATCH_RLAT_CACHE_KEY, findAllBokMatchRlat());
    }

    public List<BokMatchRlat> getBokMatchRlatFromCache(List<String> matchTypeList, String source, String acctStandard) {
        List<BokMatchRlat> bokMatchRlatList = (List<BokMatchRlat>) CacheUtil.getAppCache(GlobalConstant.BOK_MATCH_RLAT_CACHE_KEY, List.class);
        //缓存中为空的时候，从数据库获取
        if(CollectionUtils.isEmpty(bokMatchRlatList)){
            bokMatchRlatList=accountingMethodMapper.selectValMatchRlatBySource(matchTypeList, source, acctStandard);
        }
        return bokMatchRlatList.stream().filter(item -> {
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(source) && !org.apache.commons.lang3.StringUtils.equals(source, item.getSource())) {
                return false;
            }
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(acctStandard) && !org.apache.commons.lang3.StringUtils.equals(acctStandard, item.getAcctStandard())) {
                return false;
            }
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(matchTypeList) && !matchTypeList.contains(item.getMatchType())) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

}
