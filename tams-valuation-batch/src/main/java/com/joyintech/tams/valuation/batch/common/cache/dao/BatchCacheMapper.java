package com.joyintech.tams.valuation.batch.common.cache.dao;

import java.util.List;

import com.joyintech.tams.common.base.entity.BokBookset;
import com.joyintech.tams.common.base.entity.BokMatchRlat;
import com.joyintech.tams.common.base.entity.BokSubjectNatureGroup;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountEntry;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SceneConfigEntry;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.BokSchemeConfigDto;
import com.joyintech.tams.valuation.batch.vatdraw.dto.ValTaxCalConfigDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.valuation.batch.common.cache.service.entity.SysParaDto;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.ValBokBussDetsubMem;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.ValBokSubjectDetailMem;

/**
 * 基础mapper
 * 
 * @author qiangchengxi
 * @version 1.0 2016年12月17日
 * @see BatchCacheMapper
 * @since 1.0
 */
@Repository(value = "valBatchCacheMapper")
public interface BatchCacheMapper {

    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    List<ValBokSubjectDetailMem> findAllDetailSubject();
    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    List<ValBokBussDetsubMem> findAllBussDetsubKey();
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    List<SysParaDto> selectAllSysParam();

    List<BokMatchRlat> findAllBokMatchRlat();

    /**
     *
     * 账套全量查询
     * @return 账套表DTO
     * @see
     * @since 1.0
     */
    List<BokBookset> getAllBokBookset();

    List<BokSchemeConfigDto> selectAllBokSchemeConfig();

    List<BokSchemeConfigDto> selectBokSchemeConfig(@Param("booksetId") String booksetId, @Param("schemeType") String schemeType);

    List<SceneConfigEntry> findAllBokSceneconfig();

    SceneConfigEntry findOneBokSceneconfig(@Param("booksetId") String booksetId);

    List<SceneConfigEntry> findAllSceneConfigWithNodeId(@Param("ACCOUNTEVENTS") String ACCOUNTEVENTS);

    List<SceneConfigEntry> findOneSceneConfigWithNodeId(@Param("booksetId") String booksetId, @Param("ACCOUNTEVENTS") String ACCOUNTEVENTS);

    List<AccountEntry> findAllSceneSubjectConfigByNodeId(@Param("ACCOUNTEVENTS") String ACCOUNTEVENTS);

    List<AccountEntry> findOneSceneSubjectConfigByNodeId(@Param("booksetId") String booksetId, @Param("ACCOUNTEVENTS") String ACCOUNTEVENTS);

    List<BokSubjectNatureGroup> getValSubjectNatureList(@Param("groupId") String groupId, @Param("acctStandardId") String acctStandardId);

    List<ValTaxCalConfigDto> queryAllTaxCalConfig();

    List<ValTaxCalConfigDto> queryOneTaxCalConfig(@Param("booksetId") String booksetId, @Param("schemeType") String schemeType);
    
    
}