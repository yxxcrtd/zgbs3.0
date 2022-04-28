package com.joyintech.tams.valuation.batch.common.cache.service;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBookset;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.common.base.entity.BokSubjectNatureGroup;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.batch.common.accounting.dao.AccountingCommonMapper;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodNewMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountEntry;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SceneConfigEntry;
import com.joyintech.tams.valuation.batch.common.cache.dao.BatchCacheMapper;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.BokSchemeConfigDto;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dao.ProfitAndLossAccountingMapper;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingSubject;
import com.joyintech.tams.valuation.batch.vatdraw.dto.ValTaxCalConfigDto;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 可变缓存
 * @author 何阳阳
 * @date 2022/3/4
 */
@Service
public class ChangingCacheService implements InitializingBean, DisposableBean {

    /**
     * 缓存超时时间（120分钟）
     */
    @Value("${batch.valuation.cache.timeout:7200000}")
    private long cacheTimeout = 120 * 60 * 1000;

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ChangingCacheService.class);

    /**
     * 账套缓存
     */
    private Cache<String, BokBookset> bookSetCache;

    /**
     * 账套配置表缓存
     */
    private Cache<BookSetExtCacheKey, BokBooksetExt> bookSetExtCache;
    
    /**
     * 账套科目表缓存
     */
    private Cache<String, List<BokSubject>> bokSubjectCache;

    private static final String BOK_SUBJECT = "BOK_SUBJECT";

    /**
     * 会计场景配置信息缓存
     */
    private Cache<String, SceneConfigEntry> nodeNoCache;

    /**
     * 账套科目表缓存-只缓存是否生成四级科目为是的
     */
    private Cache<String, List<SceneConfigEntry>> sceneConfigCache;
    
    /**
     * 会计分录配置信息缓存
     */
    private Cache<String, List<AccountEntry>> sceneSubjectConfigCache;

    /**
     * 账套方案配置缓存
     */
    private Cache<BokSchemeConfigCacheKey, List<BokSchemeConfigDto>> bokSchemeConfigCache;

    /**
     * 账套分层子结构缓存
     */
    private Cache<String, List<StayDrawSubprdEntity>> subBokAcctInfoCache;
    
    /**
     * 账套分层子结构缓存 全量
     */
    private Cache<String, List<StayDrawSubprdEntity>> subBokAcctInfoAllCache;

    private static final String BOK_ACCT_LAYERING_INFO = "BOK_ACCT_LAYERING_INFO";

    /**
     * 科目性质分组信息缓存
     */
    private Cache<String, List<BokSubjectNatureGroup>> valSubjectNatureListCache;

    /**
     * 税额计提分类配置缓存-目前此缓存只有在增值税计提批处理中有使用
     */
    private Cache<String, List<ValTaxCalConfigDto>> valTaxCalConfigCache;

    @Autowired
    private AccountingCommonMapper accountingCommonMapper;

    @Autowired
    private AccountingMethodMapper accountingMethodMapper;

    @Autowired
    private AccountingMethodNewMapper accountingMethodNewMapper;

    @Autowired
    private BatchCacheMapper batchCacheMapper;

    @Autowired
    private ProfitAndLossAccountingMapper profitAndLossAccountingMapper;
    
    /**
     * 缓存刷新线程池
     */
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(20,
            new BasicThreadFactory.Builder().namingPattern("ChangingCacheThread-%d").daemon(true).build());
    
    /**
     * 获取账套信息
     * @param booksetIdList 账套代码List
     * @return 账套信息
     */
    public List<BokBookset> getValBokBookset(Collection<String> booksetIdList) {
        List<BokBookset> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(booksetIdList)) {
            booksetIdList.forEach(booksetId -> {
                try {
                    BokBookset bokBookset = bookSetCache.get(booksetId, () -> {
                        logNoCache("BOK_BOOKSET", booksetId);
                        Map<String, Object> param = new HashMap<>();
                        param.put("BOOKSETS", Arrays.asList(booksetId));
                        List<BokBookset> tmpList = accountingMethodMapper.getBooksetInfoByParam(param);
                        if(!tmpList.isEmpty()) {
                            return tmpList.get(0);
                        }
                        return new BokBookset();
                    });
                    if(null != bokBookset && StringUtils.isNotEmpty(bokBookset.getBooksetId())) {
                        result.add(bokBookset);
                    }
                } catch (ExecutionException e) {
                    handelException(e);
                }
            });
        }
        return result;
    }
    
    /**
     * 获取账套配置信息
     * @param booksetId 账套代码
     * @param paramCodeList 参数代码List
     * @return 账套配置信息List
     */
    public List<BokBooksetExt> getValBokBooksetExt(String booksetId, List<String> paramCodeList) {
        List<BokBooksetExt> result = new ArrayList<>();
        if(StringUtils.isNotEmpty(booksetId) && CollectionUtils.isNotEmpty(paramCodeList)) {
            paramCodeList.forEach(paramCode -> {
                try {
                    BookSetExtCacheKey cacheKey = new BookSetExtCacheKey(booksetId, paramCode);
                    BokBooksetExt bokBooksetExt = bookSetExtCache.get(cacheKey,
                            () -> {
                                logNoCache("BOK_BOOKSET_EXT", cacheKey);
                                BokBooksetExt ext = accountingCommonMapper.getBokBooksetExt(booksetId, paramCode);
                                if(ext == null) {
                                    ext = new BokBooksetExt();
                                }
                                return ext;
                            }
                    );
                    if(null != bokBooksetExt && StringUtils.isNotEmpty(bokBooksetExt.getBooksetId())) {
                        result.add(bokBooksetExt);
                    }
                } catch (Exception e) {
                    handelException(e);
                }
            });
        }
        return result;
    }

    /**
     * 获取账套分层子结构信息
     * @param params Map<String, Object>
     * @return 账套分层子结构信息List
     */
    public List<StayDrawSubprdEntity> getValSubBokAcctInfo(Map<String, Object> params) {
        List<StayDrawSubprdEntity> result = new ArrayList<>();
        String booksetId = (String) params.get("booksetId");
        String lev = (String) params.get("lev");
        if (StringUtils.isNotEmpty(booksetId)) {
            try {
                List<StayDrawSubprdEntity> subBokAccInfo = subBokAcctInfoCache.get(booksetId,
                        () -> {
                            logNoCache("BOK_ACCT_LAYERING_INFO", booksetId);
                            List<StayDrawSubprdEntity> ext = accountingCommonMapper.querySubBokAcctInfo(params);
                            if (ext.size() == 0) {
                                ext = new ArrayList<>();
                            }
                            return (List<StayDrawSubprdEntity>) ext;
                        }
                );
                if (null != subBokAccInfo && subBokAccInfo.size() > 0) {
                    if(StringUtils.isNotEmpty(lev)){
                        //按层级like匹配过滤
                        List<StayDrawSubprdEntity> resultFilter= subBokAccInfo.stream()
                                .filter((StayDrawSubprdEntity s) -> (like(s.getLev(), lev+"-%")==true) || StringUtil.equals(s.getLev(), lev))
                                .collect(Collectors.toList());
                        result.addAll(resultFilter);
                    }
                    else{
                        result.addAll(subBokAccInfo);
                    }
                }
            } catch (Exception e) {
            	handelException(e);
            }
        }
        else {
            //不传账套ID则获取全量
            try {
                List<StayDrawSubprdEntity> subBokAccInfo = (List<StayDrawSubprdEntity>) subBokAcctInfoAllCache.get(BOK_ACCT_LAYERING_INFO,
                        () -> {
                            List<StayDrawSubprdEntity> all = accountingCommonMapper.queryAllSubBokAcctInfo();
                            if (all.size() == 0) {
                                all = new ArrayList<>();
                            }
                            return all;
                        }
                );
                if (null != subBokAccInfo && subBokAccInfo.size() > 0) {
                    result.addAll(subBokAccInfo);
                }
            } catch (Exception e) {
            	handelException(e);
            }

        }
        return result;
    }
    
    /**
     * 获取帐套方案配置信息
     * @param booksetId 账套代码
     * @param schemeType 配置方案类型
     * @param configId 配置方案代码
     * @return 帐套方案配置信息
     */
    public List<BokSchemeConfigDto> getValBokSchemeConfig(final String booksetId, final String schemeType, final String configId) {
        if(StringUtils.isNotEmpty(booksetId) && StringUtils.isNotEmpty(schemeType)) {
            try {
                BokSchemeConfigCacheKey cacheKey = new BokSchemeConfigCacheKey(booksetId, schemeType);
                List<BokSchemeConfigDto> list = bokSchemeConfigCache.get(cacheKey,
                        () -> {
                            logNoCache("BOK_SCHEME_CONFIG", cacheKey);
                            return batchCacheMapper.selectBokSchemeConfig(booksetId, schemeType);
                        }
                );
                if(StringUtils.isNotEmpty(configId)) {
                    return list.stream().filter(item -> StringUtils.equals(item.getConfigId(), configId)).collect(Collectors.toList());
                }
                return list;
            } catch (Exception e) {
                handelException(e);
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * 获取会计场景配置信息
     * @param boksetId 账套代码
     * @return 会计场景配置信息
     */
    public SceneConfigEntry getBokSceneconfig(String boksetId) {
        if(StringUtils.isNotEmpty(boksetId)) {
            try {
                return nodeNoCache.get(boksetId, () -> batchCacheMapper.findOneBokSceneconfig(boksetId));
            } catch (ExecutionException e) {
                handelException(e);
            }
        }
        return null;
    }
    
    /**
     * 获取账套科目信息-是否生成四级科目为是的
     * @param booksetId 账套代码
     * @param accountSceneCodes 本批次批处理任务记账时需要使用的全部会计场景代码
     * @return 账套科目信息
     */
    public List<SceneConfigEntry> getBokSceneconfigWithNode(String booksetId, List<String> accountSceneCodes) {
        if(StringUtils.isNotEmpty(booksetId)) {
            try {
                List<SceneConfigEntry> result = sceneConfigCache.get(booksetId, () -> {
                    logNoCache("BOK_SCENECONFIG", booksetId);
                    return batchCacheMapper.findOneSceneConfigWithNodeId(booksetId, GlobalConstant.ACCOUNTEVENTS);
                });
                if(CollectionUtils.isNotEmpty(result)) {
                    if(CollectionUtils.isNotEmpty(accountSceneCodes)) {
                        return result.stream().filter(item -> accountSceneCodes.contains(item.getAccountSceneCode())).collect(Collectors.toList());
                    }
                    return result;
                }
            } catch (ExecutionException e) {
                handelException(e);
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * 获取会计分录配置信息
     * @param booksetId 账套代码
     * @param accountSceneCodes 本批次批处理任务记账时需要使用的全部会计场景代码
     * @return 账套科目信息
     */
    public List<AccountEntry> getSceneSubjectConfigWithNode(String booksetId, List<String> accountSceneCodes) {
        if(StringUtils.isNotEmpty(booksetId)) {
            try {
                List<AccountEntry> result = sceneSubjectConfigCache.get(booksetId, () -> {
                    logNoCache("BOK_SCENESUBJECT", booksetId);
                    return batchCacheMapper.findOneSceneSubjectConfigByNodeId(booksetId, GlobalConstant.ACCOUNTEVENTS);
                });
                if(CollectionUtils.isNotEmpty(result)) {
                    if(CollectionUtils.isNotEmpty(accountSceneCodes)) {
                        return result.stream().filter(item -> accountSceneCodes.contains(item.getNodeValue())).collect(Collectors.toList());
                    }
                    return result;
                }
            } catch (ExecutionException e) {
                handelException(e);
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * 获取科目性质分组信息
     * @param groupId 分组代码
     * @return 科目性质分组信息
     */
    public List<String> getValSubjectNatureList(String groupId) {
        if(StringUtils.isNotEmpty(groupId)) {
            try {
                List<BokSubjectNatureGroup> result = valSubjectNatureListCache.get(groupId, () -> {
                    logNoCache("BOK_SUBJECT_NATURE_GROUP", groupId);
                    return batchCacheMapper.getValSubjectNatureList(groupId,null);
                });
                if(CollectionUtils.isNotEmpty(result)) {
                    return result.stream().map(BokSubjectNatureGroup::getBaseSubjectNature).collect(Collectors.toList());
                }
            } catch (ExecutionException e) {
                handelException(e);
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * 获取税额计提分类配置信息
     * @param booksetId 账套代码
     * @param schemeType 配置方案类型
     * @return 税额计提分类配置信息
     */
    public List<ValTaxCalConfigDto> getValTaxCalConfig(String booksetId, String schemeType) {
        if(StringUtils.isNotEmpty(booksetId) && StringUtils.isNotEmpty(schemeType)) {
            try {
                List<ValTaxCalConfigDto> result = valTaxCalConfigCache.get(booksetId, () -> {
                    logNoCache("TAX_BOK_CONFIG", schemeType);
                    return batchCacheMapper.queryOneTaxCalConfig(booksetId, schemeType);
                });
                if(CollectionUtils.isNotEmpty(result)) {
                    return result.stream().filter(item -> StringUtils.equals(schemeType, item.getSchemeType())).collect(Collectors.toList());
                }
            } catch (ExecutionException e) {
                handelException(e);
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * 获取科目信息
     * @param params Map<String, Object>
     * @return 科目信息List
     */
    public List<BokSubject> getSubjectInfoList(Map<String, Object> params) {
        //所属账套
        String booksetId = (String) params.get("booksetId");
        //科目号
        String subjectNo = (String) params.get("subjectNo");
        //所属账套模板
        String booksetIdTmpl=null;
        List<String> bookIdList=new ArrayList<>();
        bookIdList.add(booksetId);
        List<BokBookset> booksetList = getValBokBookset(bookIdList);
        if(CollectionUtils.isNotEmpty(booksetList)){
            booksetIdTmpl=booksetList.get(0).getBooksetIdTmpl();
        }
        if(StringUtils.isNotEmpty(booksetIdTmpl)) {
            try {
                List<BokSubject> result = bokSubjectCache.get(BOK_SUBJECT, () -> {
                    logNoCache("BOK_SUBJECT", subjectNo);
                    return accountingCommonMapper.getSubjectInfo(params);
                });
                if(CollectionUtils.isNotEmpty(result)) {
                    String finalBooksetIdTmpl = booksetIdTmpl;
                    List<BokSubject> subjects=new ArrayList<>();
                    subjects= result.stream().filter(item -> StringUtils.equals(finalBooksetIdTmpl, item.getBooksetId())).collect(Collectors.toList());
                    if(StringUtils.isNotEmpty(subjectNo)){
                        subjects= subjects.stream().filter(item -> StringUtils.equals(subjectNo, item.getSubjectNo())).collect(Collectors.toList());
                    }
                    return subjects;
                }
            } catch (ExecutionException e) {
                handelException(e);
            }
            //不传账套模板的情况，保持原有逻辑按账套和科目号查询数据库
        }
        else{
            if(StringUtils.isNotEmpty(booksetId)) {
                return accountingCommonMapper.getSubjectInfo(params);
            }

        }
        return new ArrayList<>();
    }


    /**
     * 获取科目信息
     * @param params Map<String, Object>
     * @return 科目信息List
     */
    public List<BokSubject> getSubjectByInternalCode(Map<String, Object> params) {
        //所属账套
        String booksetId = (String) params.get("booksetId");
        //科目号
        String subjectNo = (String) params.get("subjectNo");
        //内部科目编码
        List<String> listSubjectInternalCode = (List<String>) params.get("listSubjectInternalCode");
        //是否核算项
        String isAcc= (String) params.get("isAcc");
        //所属账套模板
        String booksetIdTmpl=null;
        String booksetType=null;
        List<String> bookIdList=new ArrayList<>();
        bookIdList.add(booksetId);
        List<BokBookset> booksetList = getValBokBookset(bookIdList);
        if(CollectionUtils.isNotEmpty(booksetList)){
            booksetIdTmpl=booksetList.get(0).getBooksetIdTmpl();
            booksetType=booksetList.get(0).getBooksetType();
            //账套模板本身的模板账套为空
            if(booksetType.equals("01")){
                booksetIdTmpl=booksetId;
            }
        }
        if(StringUtils.isNotEmpty(booksetIdTmpl)) {
            try {
                List<BokSubject> result = bokSubjectCache.get(BOK_SUBJECT, () -> {
                    logNoCache("BOK_SUBJECT", subjectNo);
                    return accountingMethodNewMapper.getSubjectByInternalCode(params);
                });
                if(CollectionUtils.isNotEmpty(result)) {
                    String finalBooksetIdTmpl = booksetIdTmpl;
                    List<BokSubject> subjects=new ArrayList<>();
                    subjects= result.stream().filter(item -> StringUtils.equals(finalBooksetIdTmpl, item.getBooksetId())).collect(Collectors.toList());
                    if(StringUtils.isNotEmpty(subjectNo)){
                        subjects= subjects.stream().filter(item -> StringUtils.equals(subjectNo, item.getSubjectNo())).collect(Collectors.toList());
                    }
                    //根据是否核算项过滤
                    if(CollectionUtils.isNotEmpty(listSubjectInternalCode)){
                        subjects= subjects.stream().filter(item -> listSubjectInternalCode.contains(item.getSubjectInternalCode())).collect(Collectors.toList());
                    }
                    if(StringUtils.isNotEmpty(isAcc)){
                        subjects= subjects.stream().filter(item -> StringUtils.equals(isAcc, item.getIsAcc())).collect(Collectors.toList());
                    }
                    return subjects;
                }
            } catch (ExecutionException e) {
                handelException(e);
            }
            //不传账套模板的情况，保持原有逻辑按账套和科目号查询数据库
        }
        else{
            if(StringUtils.isNotEmpty(booksetId)) {
                accountingMethodNewMapper.getSubjectByInternalCode(params);
            }

        }
        return new ArrayList<>();
    }
    
    /**
     * 获取损益结转科目
     * @param booksetId 账套代码
     * @param subjectTypeList 科目类型
     * @param baseSubjectNatureList 基础科目性质
     * @return 损益结转科目List
     */
    public List<ProfitAndLossAccountingSubject> selectPalSubject(String booksetId, List<String> subjectTypeList, List<String> baseSubjectNatureList) {
        //所属账套模板
        String booksetIdTmpl=null;
        List<String> bookIdList=new ArrayList<>();
        bookIdList.add(booksetId);
        List<BokBookset> booksetList = getValBokBookset(bookIdList);
        if(CollectionUtils.isNotEmpty(booksetList)){
            booksetIdTmpl=booksetList.get(0).getBooksetIdTmpl();
        }
        if(StringUtils.isNotEmpty(booksetIdTmpl) ) {
            try {
                List<BokSubject> result = bokSubjectCache.get(BOK_SUBJECT, () -> {
                    logNoCache("BOK_SUBJECT", BOK_SUBJECT);
                    return new ArrayList<>();
                });
                if(CollectionUtils.isNotEmpty(result)) {
                    List<BokSubject> subjects=new ArrayList<>();
                    List<ProfitAndLossAccountingSubject> profitAndLossAccountingSubjects=new ArrayList<>();
                    //所有科目根据账套模板过滤
                    String finalBooksetIdTmpl = booksetIdTmpl;
                    subjects=result.stream().filter(item -> StringUtils.equals(finalBooksetIdTmpl, item.getBooksetId())).collect(Collectors.toList());
                    //根据科目类型过滤
                    if(CollectionUtils.isNotEmpty(subjectTypeList)){
                        subjects= subjects.stream().filter(item -> subjectTypeList.contains(item.getSubjectType())).collect(Collectors.toList());
                    }
                    //根据科目基础性质过滤
                    if(CollectionUtils.isNotEmpty(baseSubjectNatureList)){
                        subjects= subjects.stream().filter(item -> baseSubjectNatureList.contains(item.getBaseSubjectNature())).collect(Collectors.toList());
                    }
                    //转为损益结转的科目实体类
                    if(CollectionUtils.isNotEmpty(subjects)){
                        subjects.stream().forEach(s->profitAndLossAccountingSubjects.add(new ProfitAndLossAccountingSubject(s.getSubjectNo(),s.getBaseSubjectNature())));
                    }
                    return  profitAndLossAccountingSubjects;
                }
                else {
                	return profitAndLossAccountingMapper.selectPalSubject(booksetId, subjectTypeList, baseSubjectNatureList);
                }
            } catch (ExecutionException e) {
                handelException(e);
            }
        }
        else{
            List<ProfitAndLossAccountingSubject> list= profitAndLossAccountingMapper.selectPalSubject(
                    booksetId, subjectTypeList, baseSubjectNatureList);
            return list;

        }
        return new ArrayList<>();
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    	// 账套信息缓存-缓存中保持最大2000
        bookSetCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(2000).build();
        // 账套配置信息缓存-缓存中保持最大100000
        bookSetExtCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(100000).build();
        //分层子结构缓存-缓存中最大保持2000
        subBokAcctInfoCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(2000).build();
        //分层子结构缓存 全量-缓存中最大保持2000
        subBokAcctInfoAllCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(2000).build();
        //账套方案配置缓存-缓存中最大保持5000
        bokSchemeConfigCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(5000).build();
        //会计场景配置信息缓存-缓存中最大保持100
        nodeNoCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(100).build();
        //账套科目表缓存-只缓存是否生成四级科目为是的-缓存中最大保持40000
        sceneConfigCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(40000).build();
        //会计分录配置信息缓存-缓存中最大保持100000
        sceneSubjectConfigCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(100000).build();
        //科目性质分组缓存-缓存中最大保持1000
        valSubjectNatureListCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(1000).build();
        //税额计提分类配置缓存-缓存中最大保持150000
        valTaxCalConfigCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(150000).build();
        //账套科目表缓存-缓存中最大保持10000
        bokSubjectCache= CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.MILLISECONDS)
                .maximumSize(10000).build();
        
        // 账套信息缓存-每5分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadBookSet, 1000, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 账套配置信息缓存-每5分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadBookSetExt, 1000, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 账套科目表缓存-每100分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadBokSubject, 2000, 100 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 分层子结构缓存-每5分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadSubBokAcctInfo, 2000, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 账套方案配置缓存-每5分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadBokSchemeConfig, 3000, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 会计场景配置信息缓存-每100分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadBokSceneConfig, 3000, 100 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 账套科目表缓存-只缓存是否生成四级科目为是的-每100分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadBokSceneConfigWithNode, 4000, 100 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 会计分录配置信息缓存-每100分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadSceneSubjectConfig, 4000, 100 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 税额计提分类配置缓存-每100分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadValSubjectNatureList, 5000, 100 * 60 * 1000, TimeUnit.MILLISECONDS);
        // 税额计提分类配置缓存-每100分钟刷新一次
        executorService.scheduleWithFixedDelay(this::loadValTaxCalConfig, 5000, 100 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    // 账套配置缓存
    private void loadBookSet() {
        long start = System.currentTimeMillis();
        logger.info("账套缓存{}加载中......", "BOOK_SET");
        int count = 0;
        try {
            List<BokBookset> bookSetList = batchCacheMapper.getAllBokBookset();
            count = bookSetList.size();
            bookSetCache.putAll(bookSetList.stream()
                    .collect(Collectors.toMap(BokBookset::getBooksetId, item -> item)));
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========账套缓存{}加载完毕，共{}条配置信息，共耗时{}秒===========", "BOOK_SET", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    // 账套配置缓存
    private void loadBookSetExt() {
        long start = System.currentTimeMillis();
        logger.info("账套配置缓存{}加载中......", "BOOK_SET_EXT");
        int count = 0;
        try {
            List<BokBooksetExt> bookSetExtList = accountingCommonMapper.getAllBokBooksetExt();
            count = bookSetExtList.size();
            bookSetExtCache.putAll(bookSetExtList.stream()
                    .collect(Collectors.toMap(item -> new BookSetExtCacheKey(item.getBooksetId(), item.getParamCode()), item -> item)));
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========账套配置缓存{}加载完毕，共{}条配置信息，共耗时{}秒===========", "BOOK_SET_EXT", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    // 账套科目缓存
    private void loadBokSubject() {
        long start = System.currentTimeMillis();
        logger.info("账套科目缓存{}加载中......", BOK_SUBJECT);
        int count = 0;
        try {
            List<BokSubject> bokSubjectList = accountingCommonMapper.getAllBokSubject();
            count = bokSubjectList.size();
            bokSubjectCache.put(BOK_SUBJECT, bokSubjectList);
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========账套科目缓存{}加载完毕，共{}条，耗时{}秒===========", "BOK_SUBJECT", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    /**
     * 方案配置缓存
     */
    private void loadBokSchemeConfig() {
        long start = System.currentTimeMillis();
        logger.info("账套方案配置缓存{}加载中......", "BOK_SCHEME_CONFIG");
        int count = 0;
        try {
            List<BokSchemeConfigDto> bokSchemeConfigDtoList = batchCacheMapper.selectAllBokSchemeConfig();
            count = bokSchemeConfigDtoList.size();
            bokSchemeConfigCache.putAll(bokSchemeConfigDtoList.stream()
                    .collect(Collectors.groupingBy(item -> new BokSchemeConfigCacheKey(item.getBooksetId(), item.getSchemeType()))));
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========账套方案配置缓存{}加载完毕，共{}条，耗时{}秒===========", "BOK_SCHEME_CONFIG", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    private void loadBokSceneConfig() {
        long start = System.currentTimeMillis();
        logger.info("场景分支配置缓存{}加载中......", "BOK_SCENECONFIG");
        int count = 0;
        try {
            List<SceneConfigEntry> nodeList = batchCacheMapper.findAllBokSceneconfig();
            count = nodeList.size();
            nodeNoCache.putAll(nodeList.stream()
                    .collect(Collectors.toMap(SceneConfigEntry::getBooksetId, item -> item)));
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========场景分支配置缓存{}加载完毕，共{}条，耗时{}秒===========", "BOK_SCENECONFIG", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    private void loadBokSceneConfigWithNode() {
        long start = System.currentTimeMillis();
        logger.info("场景分支配置缓存{}加载中......", "BOK_SCENECONFIG");
        int count = 0;
        try {
            List<SceneConfigEntry> configList = batchCacheMapper.findAllSceneConfigWithNodeId(GlobalConstant.ACCOUNTEVENTS);
            count = configList.size();
            sceneConfigCache.putAll(configList.stream()
                    .collect(Collectors.groupingBy(SceneConfigEntry::getBooksetId, Collectors.toList())));
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========场景分支配置缓存{}加载完毕，共{}条，耗时{}秒===========", "BOK_SCENECONFIG", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    private void loadSceneSubjectConfig() {
        long start = System.currentTimeMillis();
        logger.info("场景科目关系缓存{}加载中......", "BOK_SCENESUBJECT");
        int count = 0;
        try {
            List<AccountEntry> configList = batchCacheMapper.findAllSceneSubjectConfigByNodeId(GlobalConstant.ACCOUNTEVENTS);
            count = configList.size();
            sceneSubjectConfigCache.putAll(configList.stream()
                    .collect(Collectors.groupingBy(AccountEntry::getBooksetId, Collectors.toList())));
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========场景科目关系缓存{}加载完毕，共{}条，耗时{}秒===========", "BOK_SCENESUBJECT", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    private void handelException(Exception e) {
        if(e instanceof ExecutionException) {
            logger.error("缓存获取失败", e);
        }
        else {
            logger.error("缓存更新失败，等待下次更新", e);
        }
    }

    private void logNoCache(String cacheType, Object key) {
        logger.warn("{}缓存未命中{}，从数据库中获取数据", cacheType, key);
    }

    // 账套分层子结构缓存
    private void loadSubBokAcctInfo() {
        long start = System.currentTimeMillis();
        logger.info("账套分层子结构缓存{}加载中......", "BOK_ACCT_LAYERING_INFO");
        int count = 0;
        try {
            List<StayDrawSubprdEntity> subBokAcctInfoList = accountingCommonMapper.queryAllSubBokAcctInfo();
            count = subBokAcctInfoList.size();
            //按账户ID分组放一份
            Map<String, List<StayDrawSubprdEntity>> group = subBokAcctInfoList.stream().collect(Collectors.groupingBy(StayDrawSubprdEntity::getBooksetId));
            group.forEach((key, value) -> {
                subBokAcctInfoCache.put(key, value);
            });
            //全量放一份
            subBokAcctInfoAllCache.put(BOK_ACCT_LAYERING_INFO, subBokAcctInfoList);
        } catch (Exception e) {
            logger.error("缓存更新失败，等待下次更新", e);
        } finally {
            logger.info("===========账套分层子结构缓存{}加载完毕，共{}条配置信息，共耗时{}秒===========", "BOK_ACCT_LAYERING_INFO", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    /**
     * 科目性质分组表
     */
    private void loadValSubjectNatureList() {
        long start = System.currentTimeMillis();
        logger.info("科目性质分组表{}加载中......", "BOK_SUBJECT_NATURE_GROUP");
        int count = 0;
        try {
            List<BokSubjectNatureGroup> configList = batchCacheMapper.getValSubjectNatureList(null ,null);
            count = configList.size();
            valSubjectNatureListCache.putAll(configList.stream()
                    .collect(Collectors.groupingBy(BokSubjectNatureGroup::getGroupId, Collectors.toList())));
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========科目性质分组表缓存{}加载完毕，共{}条，耗时{}秒===========", "BOK_SUBJECT_NATURE_GROUP", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    /**
     * 税额计提分类配置
     */
    private void loadValTaxCalConfig() {
        long start = System.currentTimeMillis();
        logger.info("税额计提分类配置表{}加载中......", "TAX_BOK_CONFIG");
        int count = 0;
        try {
            List<ValTaxCalConfigDto> configList = batchCacheMapper.queryAllTaxCalConfig();
            count = configList.size();
            valTaxCalConfigCache.putAll(configList.stream()
                    .collect(Collectors.groupingBy(ValTaxCalConfigDto::getBooksetId, Collectors.toList())));
        }
        catch (Exception e) {
            handelException(e);
        }
        finally {
            logger.info("===========税额计提分类配置表缓存{}加载完毕，共{}条，耗时{}秒===========", "TAX_BOK_CONFIG", count, (System.currentTimeMillis() - start) / 1000);
        }
    }

    /**
     * 账套配置表信息缓存KEY
     * @author 何阳阳
     * @date 2022-03-04
     */
    static class BookSetExtCacheKey implements Serializable {
        private String booksetId;
        private String paramCode;

        public BookSetExtCacheKey(String booksetId, String paramCode) {
            this.booksetId = booksetId;
            this.paramCode = paramCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BookSetExtCacheKey that = (BookSetExtCacheKey) o;
            return Objects.equal(booksetId, that.booksetId) && Objects.equal(paramCode, that.paramCode);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(booksetId, paramCode);
        }

        @Override
        public String toString() {
            return "BookSetExtCacheKey{" +
                    "booksetId='" + booksetId + '\'' +
                    ", paramCode='" + paramCode + '\'' +
                    '}';
        }
    }

    /**
     * 账套配置表信息缓存KEY
     * @author 何阳阳
     * @date 2022-03-14
     */
    static class BokSchemeConfigCacheKey implements Serializable {
        private String booksetId;
        private String schemeType;

        public BokSchemeConfigCacheKey(String booksetId, String schemeType) {
            this.booksetId = booksetId;
            this.schemeType = schemeType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BokSchemeConfigCacheKey that = (BokSchemeConfigCacheKey) o;
            return Objects.equal(booksetId, that.booksetId) && Objects.equal(schemeType, that.schemeType);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(booksetId, schemeType);
        }

        @Override
        public String toString() {
            return "BokSchemeConfigCacheKey{" +
                    "booksetId='" + booksetId + '\'' +
                    ", schemeType='" + schemeType + '\'' +
                    '}';
        }
    }

    /**
     * 账套科目信息缓存KEY
     * @author 陈卓
     * @date 2022-03-04
     */
    static class BokSubjectCacheKey implements Serializable {
        private String booksetId;
        private String subjectNo;

        public BokSubjectCacheKey(String booksetId, String subjectNo) {
            this.booksetId = booksetId;
            this.subjectNo = subjectNo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BokSubjectCacheKey that = (BokSubjectCacheKey) o;
            return Objects.equal(booksetId, that.booksetId) && Objects.equal(subjectNo, that.subjectNo);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(booksetId, subjectNo);
        }
    }

    /**
     * 模糊匹配方法
     * @param toBeCompare
     * @param by
     * @return
     */
    public static boolean like(String toBeCompare, String by){
        if(by != null){
            if(toBeCompare != null){
                if(by.startsWith("%") && by.endsWith("%")){
                    int index = toBeCompare.toLowerCase().indexOf(by.replace("%","").toLowerCase());
                    if(index < 0){
                        return false;
                    } else {
                        return true;
                    }
                } else if(by.startsWith("%")){
                    return toBeCompare.endsWith(by.replace("%",""));
                } else if(by.endsWith("%")){
                    return toBeCompare.startsWith(by.replace("%",""));
                } else {
                    return toBeCompare.equals(by.replace("%",""));
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
