package com.joyintech.tams.invest.portfolio.ptlpositionanalysis.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis003Dto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组合持仓分析报表
 *
 * @author nixiukuan
 * @version 1.0 2019年12月05日
 * @see PtlPositionAnalysis002Service
 * @since 1.0
 */
@Service
public class PtlPositionAnalysis002Service extends CommonExportService<PtlPositionAnalysis003Dto> {

    /**
     * 交易对手
     */
    @Autowired
    MstCounterPartyMapper mstCounterPartyMapper;

    @Autowired
    MstCustomerInfoMapper mstCustomerInfoMapper;

    /**
     * 组合持仓明细查询
     *
     * @param map 画面参数
     * @return 调用微服务
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<PtlPositionAnalysis003Dto> selectForPage(Map<String, Object> map) {
        String searchDate = map.get("searchDate").toString(); // 查询日期
        List<PtlPositionAnalysis003Dto> pageList = new ArrayList<>();
        String displayType = map.get("displayType").toString();
        Map<String,Object> newMap = new HashMap<>(map);
        newMap.remove("targetDbMap");
        Page<PtlPositionAnalysis003Dto> list = restTemplate.postForPage(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/secReportPage", newMap, PtlPositionAnalysis003Dto.class);
        //设置页面显示长度的初始值，这个正常情况下在map里会有设置
        int pageSize = 20;
        int total = 0;
        if(map.get("length")!=null) {
        	pageSize = Integer.parseInt((String)map.get("length"));
        }
        // 根据下钻层级重新组装树机构类型
        String level = map.get("deepLevel").toString();
        String parentId = map.get("parentId").toString();
        String pass = map.get("pass").toString();
        if (parentId.equals("root")) {
            List<PtlPositionAnalysis003Dto> remainList = this.selectRemain(map);
            BigDecimal dzhCash = BigDecimal.ZERO;
            BigDecimal dzhLx = BigDecimal.ZERO;
            Map<String, BigDecimal> dzhMap = new HashMap<String, BigDecimal>();
            Map<String, BigDecimal> dzhLxMap = new HashMap<String, BigDecimal>();
            if(!StringUtil.isNotEmpty(map.get("finprodMarketId"))) {
                if (displayType.equals(DictCodeListExt3.展示方式_多组合汇总.getValue())){
                    //多组合汇总汇总剩余现金数据
                    if (remainList != null){
                        for (PtlPositionAnalysis003Dto ptlPositionAnalysis003Dto : remainList) {
                            dzhCash = dzhCash.add(ptlPositionAnalysis003Dto.getPosValue() == null? BigDecimal.ZERO: ptlPositionAnalysis003Dto.getPosValue());
                            dzhLx =  dzhLx.add(ptlPositionAnalysis003Dto.getTdyIntincexp() == null? BigDecimal.ZERO: ptlPositionAnalysis003Dto.getTdyIntincexp());
                        }
                    }
                }else{
                    if (remainList != null){
                        for (PtlPositionAnalysis003Dto ptlPositionAnalysis003Dto : remainList) {
                            //同一个组合的剩余现金汇总  存入map
                            if (ptlPositionAnalysis003Dto.getPosValue() != null){
                                dzhMap.put(ptlPositionAnalysis003Dto.getPortfolioId(), dzhMap.get(ptlPositionAnalysis003Dto.getPortfolioId()) == null?ptlPositionAnalysis003Dto.getPosValue() : dzhMap.get(ptlPositionAnalysis003Dto.getPortfolioId()).add(ptlPositionAnalysis003Dto.getPosValue()));
                                dzhLxMap.put(ptlPositionAnalysis003Dto.getPortfolioId(), dzhLxMap.get(ptlPositionAnalysis003Dto.getPortfolioId()) == null?ptlPositionAnalysis003Dto.getTdyIntincexp() : dzhLxMap.get(ptlPositionAnalysis003Dto.getPortfolioId()).add(ptlPositionAnalysis003Dto.getTdyIntincexp()));
                            }
                        }
                    }
                }
            }else{
                dzhCash = null;
                dzhLx = null;
                dzhMap = null;
                dzhLxMap = null;
            }

            if (level.equals(DictCodeListExt3.下钻层级_资产一级分类.getValue())) {
                if (displayType.equals(DictCodeListExt3.展示方式_多组合汇总.getValue())) {
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist = list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPinanAssetType));
                    if (groupBylist.size() > 0) {
                        // map遍历
                        groupBylist.forEach((k, v) -> {
                            // 拼接一级分类
                            PtlPositionAnalysis003Dto typeName = new PtlPositionAnalysis003Dto();
                            typeName.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, v.get(0).getPinanAssetType()));
                            typeName.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, v.get(0).getPinanAssetType()));
                            typeName.setParentInfo("root");
                            typeName.setChildInfo(v.get(0).getPinanAssetType());
                            typeName.setHasChildren("01");
                            typeName.setPosCost(v.get(0).getPosCost());
                            typeName.setPosValue(v.get(0).getPosValue());
                            typeName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            typeName.setPrin(v.get(0).getPrin());
                            pageList.add(typeName);
                        });

                        if(pageList != null && pageList.size()>0){
                            //多组合剩余现金处理
                            this.allPortfolio(pageList,dzhCash,dzhLx);
                        }
                    }
                } else {
                    // 单组合汇总
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist = list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPortfolioId));
                    if (groupBylist.size() > 0) {
                        // map遍历
                        groupBylist.forEach((k, v) -> {
                            // 拼接组合名称
                            PtlPositionAnalysis003Dto portfolioName = new PtlPositionAnalysis003Dto();
                            portfolioName.setTypeName(v.get(0).getPortfolioId());
                            portfolioName.setPortfolioId(v.get(0).getPortfolioId());
                            portfolioName.setParentInfo("root");
                            portfolioName.setChildInfo(v.get(0).getPortfolioId());
                            portfolioName.setHasChildren("02");
                            portfolioName.setBelongInstitutionNameNew(StringUtils.isNotEmpty(v.get(0).getBelongInstitutionNameNew()) ? String.valueOf(CacheUtil.getAppCacheInstitutionCodeName(v.get(0).getBelongInstitutionNameNew())) : v.get(0).getBelongInstitutionNameNew());
                            portfolioName.setPosCost(v.get(0).getPosCost());
                            portfolioName.setPosValue(v.get(0).getPosValue());
                            portfolioName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(portfolioName);

                            // 拼接一级分类[根据组合代码查询对应下面的所有一级分类名称]
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "01", displayType, "", pass,map);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto oneType = new PtlPositionAnalysis003Dto();
                                    oneType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, entity.getPinanAssetType()));
                                    oneType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, entity.getPinanAssetType()));
                                    oneType.setParentInfo(v.get(0).getPortfolioId());
                                    oneType.setChildInfo(entity.getPortfolioId() + entity.getPinanAssetType());
                                    oneType.setHasChildren("01");
                                    oneType.setPosCost(entity.getPosCost());
                                    oneType.setPosValue(entity.getPosValue());
                                    oneType.setTdyIntincexp(entity.getTdyIntincexp());
                                    pageList.add(oneType);
                                }
                            }
                        });
                        if(pageList != null && pageList.size()>0){
                            //单组合剩余现金处理
                            this.onePortfolio(pageList,dzhMap,dzhLxMap);
                        }
                    }
                }

                list.setRoot(pageList);
                total = pageList.size()/pageSize;
                if(pageList.size()%pageSize>0){
                    total++;
                }
                list.setTotal(total);
                return list;
            } else if (level.equals(DictCodeListExt3.下钻层级_资产二级分类.getValue())) {
                if (displayType.equals(DictCodeListExt3.展示方式_多组合汇总.getValue())) {
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist = list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPinanAssetType));
                    if (groupBylist.size() > 0) {
                        // map遍历
                        groupBylist.forEach((k, v) -> {
                            // 拼接一级分类
                            PtlPositionAnalysis003Dto typeName = new PtlPositionAnalysis003Dto();
                            typeName.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, v.get(0).getPinanAssetType()));
                            typeName.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, v.get(0).getPinanAssetType()));
                            typeName.setParentInfo("root");
                            typeName.setChildInfo(v.get(0).getPinanAssetType());
                            typeName.setHasChildren("02");
                            typeName.setPosCost(v.get(0).getPosCost());
                            typeName.setPosValue(v.get(0).getPosValue());
                            typeName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(typeName);

                            // 二级分类
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType("", searchDate, "02", displayType, v.get(0).getPinanAssetType(), pass,map);
                            //根据parent查询出下一行的汇总额
                           // List<PtlPositionAnalysis003Dto> selectSumByParentpList = selectSumByParentp("", searchDate, "02", displayType, v.get(0).getPinanAssetType(), pass);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto twoType = new PtlPositionAnalysis003Dto();
                                    twoType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类, entity.getCustomAssetTypeTwo()));
                                    twoType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类, entity.getCustomAssetTypeTwo()));
                                    twoType.setParentInfo(entity.getPinanAssetType());
                                    twoType.setChildInfo(entity.getPinanAssetType() + entity.getCustomAssetTypeTwo());
                                    twoType.setHasChildren("01");
                                    twoType.setPosCost(entity.getPosCost());
                                    twoType.setPosValue(entity.getPosValue());
                                    twoType.setTdyIntincexp(entity.getTdyIntincexp());
                                    pageList.add(twoType);
                                }
                            }
                        });

                        if(pageList != null && pageList.size()>0){
                            //多组合剩余现金处理
                            this.allPortfolio(pageList,dzhCash,dzhLx);
                        }
                    }
                } else {
                    // 单组合汇总
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist = list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPortfolioId));
                    if (groupBylist.size() > 0) {
                        // map遍历
                        groupBylist.forEach((k, v) -> {
                            // 拼接组合名称
                            PtlPositionAnalysis003Dto portfolioName = new PtlPositionAnalysis003Dto();
                            portfolioName.setTypeName(v.get(0).getPortfolioId());
                            portfolioName.setPortfolioId(v.get(0).getPortfolioId());
                            portfolioName.setParentInfo("root");
                            portfolioName.setChildInfo(v.get(0).getPortfolioId());
                            portfolioName.setHasChildren("02");
                            portfolioName.setBelongInstitutionNameNew(StringUtils.isNotEmpty(v.get(0).getBelongInstitutionNameNew()) ? String.valueOf(CacheUtil.getAppCacheInstitutionCodeName(v.get(0).getBelongInstitutionNameNew())) : v.get(0).getBelongInstitutionNameNew());
                            portfolioName.setPosCost(v.get(0).getPosCost());
                            portfolioName.setPosValue(v.get(0).getPosValue());
                            portfolioName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(portfolioName);

                            // 拼接一级分类[根据组合代码查询对应下面的所有一级分类名称]
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "01", displayType, "", pass,map);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto oneType = new PtlPositionAnalysis003Dto();
                                    oneType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, entity.getPinanAssetType()));
                                    oneType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, entity.getPinanAssetType()));
                                    oneType.setParentInfo(entity.getPortfolioId());
                                    oneType.setChildInfo(entity.getPortfolioId() + entity.getPinanAssetType());
                                    oneType.setHasChildren("02");
                                    oneType.setPosCost(entity.getPosCost());
                                    oneType.setPosValue(entity.getPosValue());
                                    oneType.setTdyIntincexp(entity.getTdyIntincexp());
                                    pageList.add(oneType);
                                    //二级分类
                                    List<PtlPositionAnalysis003Dto> twoList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "02", displayType, entity.getPinanAssetType(), pass,map);
                                    if (!StringUtil.isEmptyList(twoList) && twoList.size() > 0) {
                                        for (PtlPositionAnalysis003Dto twoEntity : twoList) {
                                            PtlPositionAnalysis003Dto twoType = new PtlPositionAnalysis003Dto();
                                            twoType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类, twoEntity.getCustomAssetTypeTwo()));
                                            twoType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类, twoEntity.getCustomAssetTypeTwo()));
                                            twoType.setParentInfo(twoEntity.getPortfolioId() + twoEntity.getPinanAssetType());
                                            twoType.setChildInfo(twoEntity.getPortfolioId() + twoEntity.getPinanAssetType() + twoEntity.getCustomAssetTypeTwo());
                                            twoType.setHasChildren("01");
                                            twoType.setPosCost(twoEntity.getPosCost());
                                            twoType.setPosValue(twoEntity.getPosValue());
                                            twoType.setTdyIntincexp(twoEntity.getTdyIntincexp());
                                            pageList.add(twoType);
                                        }
                                    }
                                }
                            }
                        });

                        if(pageList != null && pageList.size()>0){
                            //单组合剩余现金处理
                            this.onePortfolio(pageList,dzhMap,dzhLxMap);
                        }
                    }
                }

                list.setRoot(pageList);
                total = pageList.size()/pageSize;
                if(pageList.size()%pageSize>0){
                    total++;
                }
                list.setTotal(total);
//                list.setTotal(pageList.size());
                return list;
            } else if (level.equals(DictCodeListExt3.下钻层级_资产三级分类.getValue())) {
                if (displayType.equals(DictCodeListExt3.展示方式_多组合汇总.getValue())) {
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist = list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPinanAssetType));
                    if (groupBylist.size() > 0) {
                        // map遍历
                        groupBylist.forEach((k, v) -> {
                            // 拼接一级分类
                            PtlPositionAnalysis003Dto typeName = new PtlPositionAnalysis003Dto();
                            typeName.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, v.get(0).getPinanAssetType()));
                            typeName.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, v.get(0).getPinanAssetType()));
                            typeName.setParentInfo("root");
                            typeName.setChildInfo(v.get(0).getPinanAssetType());
                            typeName.setHasChildren("02");
                            typeName.setPosCost(v.get(0).getPosCost());
                            typeName.setPosValue(v.get(0).getPosValue());
                            typeName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(typeName);

                            // 二级分类
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType("", searchDate, "02", displayType, v.get(0).getPinanAssetType(), pass,map);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto twoType = new PtlPositionAnalysis003Dto();
                                    twoType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                            entity.getCustomAssetTypeTwo()));
                                    twoType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                            entity.getCustomAssetTypeTwo()));
                                    twoType.setParentInfo(entity.getPinanAssetType());
                                    twoType.setChildInfo(entity.getPinanAssetType() + entity.getCustomAssetTypeTwo());
                                    twoType.setHasChildren("02");
                                    twoType.setPosCost(entity.getPosCost());
                                    twoType.setPosValue(entity.getPosValue());
                                    twoType.setTdyIntincexp(entity.getTdyIntincexp());
                                    pageList.add(twoType);
                                    //三级分类
                                    List<PtlPositionAnalysis003Dto> typeThreeList = getFinpordType("", searchDate, "03", displayType, v.get(0).getPinanAssetType(), pass,map);
                                    if (!StringUtil.isEmptyList(typeThreeList) && typeThreeList.size() > 0) {
                                        for (PtlPositionAnalysis003Dto threeEntity : typeThreeList) {
                                            PtlPositionAnalysis003Dto threeType = new PtlPositionAnalysis003Dto();
                                            threeType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                    threeEntity.getCustomAssetTypeThree()));
                                            threeType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                    threeEntity.getCustomAssetTypeThree()));
                                            threeType.setParentInfo(threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo());
                                            threeType.setChildInfo(threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo() + threeEntity.getCustomAssetTypeThree());
                                            threeType.setHasChildren("01");
                                            threeType.setPosCost(threeEntity.getPosCost());
                                            threeType.setPosValue(threeEntity.getPosValue());
                                            threeType.setTdyIntincexp(threeEntity.getTdyIntincexp());
                                            pageList.add(threeType);
                                        }
                                    }
                                }
                            }

                        });

                        if(pageList != null && pageList.size()>0){
                            //多组合剩余现金处理
                            this.allPortfolio(pageList,dzhCash,dzhLx);
                        }
                    }
                } else {
                    //单组合汇总
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist =
                            list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPortfolioId));
                    if (groupBylist.size() > 0) {

                        //map遍历
                        groupBylist.forEach((k, v) -> {

                            // 拼接组合名称
                            PtlPositionAnalysis003Dto portfolioName = new PtlPositionAnalysis003Dto();
                            portfolioName.setTypeName(
                                    v.get(0).getPortfolioId());
                            portfolioName.setPortfolioId(v.get(0).getPortfolioId());
                            portfolioName.setParentInfo("root");
                            portfolioName.setChildInfo(v.get(0).getPortfolioId());
                            portfolioName.setHasChildren("02");
                            portfolioName.setBelongInstitutionNameNew(StringUtils.isNotEmpty(v.get(0).getBelongInstitutionNameNew()) ? String.valueOf(CacheUtil.getAppCacheInstitutionCodeName(v.get(0).getBelongInstitutionNameNew())) : v.get(0).getBelongInstitutionNameNew());
                            portfolioName.setPosCost(v.get(0).getPosCost());
                            portfolioName.setPosValue(v.get(0).getPosValue());
                            portfolioName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(portfolioName);
                            //拼接一级分类[根据组合代码查询对应下面的所有一级分类名称]
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "01", displayType, "", pass,map);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto oneType = new PtlPositionAnalysis003Dto();
                                    oneType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                            entity.getPinanAssetType()));
                                    oneType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                            entity.getPinanAssetType()));
                                    oneType.setParentInfo(v.get(0).getPortfolioId());
                                    oneType.setChildInfo(entity.getPortfolioId() + entity.getPinanAssetType());
                                    oneType.setHasChildren("02");
                                    oneType.setPosCost(entity.getPosCost());
                                    oneType.setPosValue(entity.getPosValue());
                                    oneType.setTdyIntincexp(entity.getTdyIntincexp());
                                    pageList.add(oneType);
                                    //二级分类
                                    List<PtlPositionAnalysis003Dto> typeTwoList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "02", displayType, entity.getPinanAssetType(), pass,map);
                                    if (!StringUtil.isEmptyList(typeTwoList) && typeTwoList.size() > 0) {
                                        for (PtlPositionAnalysis003Dto twoEntity : typeTwoList) {
                                            PtlPositionAnalysis003Dto twoType = new PtlPositionAnalysis003Dto();
                                            twoType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                                    twoEntity.getCustomAssetTypeTwo()));
                                            twoType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                                    twoEntity.getCustomAssetTypeTwo()));
                                            twoType.setParentInfo(v.get(0).getPortfolioId() + twoEntity.getPinanAssetType());
                                            twoType.setChildInfo(v.get(0).getPortfolioId() + twoEntity.getPinanAssetType() + twoEntity.getCustomAssetTypeTwo());
                                            twoType.setHasChildren("02");
                                            twoType.setPosCost(twoEntity.getPosCost());
                                            twoType.setPosValue(twoEntity.getPosValue());
                                            twoType.setTdyIntincexp(twoEntity.getTdyIntincexp());
                                            pageList.add(twoType);
                                            //三级分类
                                            List<PtlPositionAnalysis003Dto> typeThreeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "03", displayType, twoEntity.getPinanAssetType(), pass,map);
                                            if (!StringUtil.isEmptyList(typeThreeList) && typeThreeList.size() > 0) {
                                                for (PtlPositionAnalysis003Dto threeEntity : typeThreeList) {
                                                    PtlPositionAnalysis003Dto threeType = new PtlPositionAnalysis003Dto();
                                                    threeType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                            threeEntity.getCustomAssetTypeThree()));
                                                    threeType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                            threeEntity.getCustomAssetTypeThree()));
                                                    threeType.setParentInfo(v.get(0).getPortfolioId() + threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo());
                                                    threeType.setChildInfo(v.get(0).getPortfolioId() + threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo() + threeEntity.getCustomAssetTypeThree());
                                                    threeType.setHasChildren("01");
                                                    threeType.setPosCost(threeEntity.getPosCost());
                                                    threeType.setPosValue(threeEntity.getPosValue());
                                                    threeType.setTdyIntincexp(threeEntity.getTdyIntincexp());
                                                    pageList.add(threeType);
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        });

                        if(pageList != null && pageList.size()>0){
                            //单组合剩余现金处理
                            this.onePortfolio(pageList,dzhMap,dzhLxMap);
                        }
                    }

                }

                List<PtlPositionAnalysis003Dto> pageNewList = new ArrayList<>();
                pageNewList = pageList.stream().collect(Collectors
                        .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> {
                            // 根据firstName和secondName进行去重
                            return o.getParentInfo() + "," + o.getChildInfo();
                        }))), ArrayList::new));
                list.setRoot(pageNewList);
                total = pageNewList.size()/pageSize;
                if(pageNewList.size()%pageSize>0){
                    total++;
                }
                list.setTotal(total);
//                list.setTotal(pageNewList.size());
                return list;
            } else if (level.equals(DictCodeListExt3.下钻层级_资产四级分类.getValue())) {
                if (displayType.equals(DictCodeListExt3.展示方式_多组合汇总.getValue())) {
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist =
                            list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPinanAssetType));
                    if (groupBylist.size() > 0) {
                        //map遍历
                        groupBylist.forEach((k, v) -> {

                            //拼接一级分类
                            PtlPositionAnalysis003Dto typeName = new PtlPositionAnalysis003Dto();
                            typeName.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                    v.get(0).getPinanAssetType()));
                            typeName.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                    v.get(0).getPinanAssetType()));
                            typeName.setParentInfo("root");
                            typeName.setChildInfo(v.get(0).getPinanAssetType());
                            typeName.setHasChildren("02");
                            typeName.setPosCost(v.get(0).getPosCost());
                            typeName.setPosValue(v.get(0).getPosValue());
                            typeName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(typeName);
                            //二级分类
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType("", searchDate, "02", displayType, v.get(0).getPinanAssetType(), pass,map);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto twoType = new PtlPositionAnalysis003Dto();
                                    twoType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                            entity.getCustomAssetTypeTwo()));
                                    twoType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                            entity.getCustomAssetTypeTwo()));
                                    twoType.setParentInfo(entity.getPinanAssetType());
                                    twoType.setChildInfo(entity.getPinanAssetType() + entity.getCustomAssetTypeTwo());
                                    twoType.setHasChildren("02");
                                    twoType.setPosCost(entity.getPosCost());
                                    twoType.setPosValue(entity.getPosValue());
                                    twoType.setTdyIntincexp(entity.getTdyIntincexp());
                                    pageList.add(twoType);
                                    //三级分类
                                    List<PtlPositionAnalysis003Dto> typeThreeList = getFinpordType("", searchDate, "03", displayType, v.get(0).getPinanAssetType(), pass,map);
                                    if (!StringUtil.isEmptyList(typeThreeList) && typeThreeList.size() > 0) {
                                        for (PtlPositionAnalysis003Dto threeEntity : typeThreeList) {
                                            PtlPositionAnalysis003Dto threeType = new PtlPositionAnalysis003Dto();
                                            threeType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                    threeEntity.getCustomAssetTypeThree()));
                                            threeType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                    threeEntity.getCustomAssetTypeThree()));
                                            threeType.setParentInfo(threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo());
                                            threeType.setChildInfo(threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo() + threeEntity.getCustomAssetTypeThree());
                                            threeType.setHasChildren("02");
                                            threeType.setPosCost(threeEntity.getPosCost());
                                            threeType.setPosValue(threeEntity.getPosValue());
                                            threeType.setTdyIntincexp(threeEntity.getTdyIntincexp());
                                            pageList.add(threeType);

                                            //四级分类
                                            List<PtlPositionAnalysis003Dto> fourTypeList = getFinpordType("", searchDate, "04", displayType, v.get(0).getPinanAssetType(), pass,map);
                                            if (!StringUtil.isEmptyList(fourTypeList) && fourTypeList.size() > 0) {
                                                for (PtlPositionAnalysis003Dto fourEntity : fourTypeList) {
                                                    PtlPositionAnalysis003Dto fourType = new PtlPositionAnalysis003Dto();
                                                    fourType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,
                                                            fourEntity.getCustomAssetTypeFour()));
                                                    fourType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,
                                                            fourEntity.getCustomAssetTypeFour()));
                                                    fourType.setParentInfo(fourEntity.getPinanAssetType() + fourEntity.getCustomAssetTypeTwo() + fourEntity.getCustomAssetTypeThree());
                                                    fourType.setChildInfo(fourEntity.getPinanAssetType() + fourEntity.getCustomAssetTypeTwo() + fourEntity.getCustomAssetTypeThree() + fourEntity.getCustomAssetTypeFour());
                                                    fourType.setHasChildren("01");
                                                    fourType.setPosCost(fourEntity.getPosCost());
                                                    fourType.setPosValue(fourEntity.getPosValue());
                                                    fourType.setTdyIntincexp(fourEntity.getTdyIntincexp());
                                                    pageList.add(fourType);

                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        });

                        if(pageList != null && pageList.size()>0){
                            //多组合剩余现金处理
                            this.allPortfolio(pageList,dzhCash,dzhLx);
                        }
                    }
                } else {
                    //单组合汇总
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist =
                            list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPortfolioId));
                    if (groupBylist.size() > 0) {

                        //map遍历
                        groupBylist.forEach((k, v) -> {

                            // 拼接组合名称
                            PtlPositionAnalysis003Dto portfolioName = new PtlPositionAnalysis003Dto();
                            portfolioName.setTypeName(
                                    v.get(0).getPortfolioId());
                            portfolioName.setPortfolioId(v.get(0).getPortfolioId());
                            portfolioName.setParentInfo("root");
                            portfolioName.setChildInfo(v.get(0).getPortfolioId());
                            portfolioName.setHasChildren("02");
                            portfolioName.setBelongInstitutionNameNew(StringUtils.isNotEmpty(v.get(0).getBelongInstitutionNameNew()) ? String.valueOf(CacheUtil.getAppCacheInstitutionCodeName(v.get(0).getBelongInstitutionNameNew())) : v.get(0).getBelongInstitutionNameNew());
                            portfolioName.setPosCost(v.get(0).getPosCost());
                            portfolioName.setPosValue(v.get(0).getPosValue());
                            portfolioName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(portfolioName);
                            //拼接一级分类[根据组合代码查询对应下面的所有一级分类名称]
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "01", displayType, "", pass,map);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto oneType = new PtlPositionAnalysis003Dto();
                                    oneType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                            entity.getPinanAssetType()));
                                    oneType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                            entity.getPinanAssetType()));
                                    oneType.setParentInfo(v.get(0).getPortfolioId());
                                    oneType.setChildInfo(entity.getPortfolioId() + entity.getPinanAssetType());
                                    oneType.setHasChildren("02");
                                    oneType.setPosCost(entity.getPosCost());
                                    oneType.setPosValue(entity.getPosValue());
                                    oneType.setTdyIntincexp(entity.getTdyIntincexp());
                                    pageList.add(oneType);
                                    //二级分类
                                    List<PtlPositionAnalysis003Dto> typeTwoList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "02", displayType, entity.getPinanAssetType(), pass,map);
                                    if (!StringUtil.isEmptyList(typeTwoList) && typeTwoList.size() > 0) {
                                        for (PtlPositionAnalysis003Dto twoEntity : typeTwoList) {
                                            PtlPositionAnalysis003Dto twoType = new PtlPositionAnalysis003Dto();
                                            twoType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                                    twoEntity.getCustomAssetTypeTwo()));
                                            twoType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                                    twoEntity.getCustomAssetTypeTwo()));
                                            twoType.setParentInfo(v.get(0).getPortfolioId() + twoEntity.getPinanAssetType());
                                            twoType.setChildInfo(v.get(0).getPortfolioId() + twoEntity.getPinanAssetType() + twoEntity.getCustomAssetTypeTwo());
                                            twoType.setHasChildren("02");

                                            twoType.setPosCost(twoEntity.getPosCost());
                                            twoType.setPosValue(twoEntity.getPosValue());
                                            twoType.setTdyIntincexp(twoEntity.getTdyIntincexp());
                                            pageList.add(twoType);
                                            //三级分类
                                            List<PtlPositionAnalysis003Dto> typeThreeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "03", displayType, twoEntity.getPinanAssetType(), pass,map);
                                            if (!StringUtil.isEmptyList(typeThreeList) && typeThreeList.size() > 0) {
                                                for (PtlPositionAnalysis003Dto threeEntity : typeThreeList) {
                                                    PtlPositionAnalysis003Dto threeType = new PtlPositionAnalysis003Dto();
                                                    threeType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                            threeEntity.getCustomAssetTypeThree()));
                                                    threeType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                            threeEntity.getCustomAssetTypeThree()));
                                                    threeType.setParentInfo(v.get(0).getPortfolioId() + threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo());
                                                    threeType.setChildInfo(v.get(0).getPortfolioId() + threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo() + threeEntity.getCustomAssetTypeThree());
                                                    threeType.setHasChildren("02");
                                                    threeType.setPosCost(threeEntity.getPosCost());
                                                    threeType.setPosValue(threeEntity.getPosValue());
                                                    threeType.setTdyIntincexp(threeEntity.getTdyIntincexp());
                                                    pageList.add(threeType);

                                                    //四级分类
                                                    List<PtlPositionAnalysis003Dto> fourTypeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "04", displayType, threeEntity.getPinanAssetType(), pass,map);
                                                    if (!StringUtil.isEmptyList(fourTypeList) && fourTypeList.size() > 0) {
                                                        for (PtlPositionAnalysis003Dto fourEntity : fourTypeList) {
                                                            PtlPositionAnalysis003Dto fourType = new PtlPositionAnalysis003Dto();
                                                            fourType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,
                                                                    fourEntity.getCustomAssetTypeFour()));
                                                            fourType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,
                                                                    fourEntity.getCustomAssetTypeFour()));
                                                            fourType.setParentInfo(v.get(0).getPortfolioId() + fourEntity.getPinanAssetType() + fourEntity.getCustomAssetTypeTwo() + fourEntity.getCustomAssetTypeThree());
                                                            fourType.setChildInfo(v.get(0).getPortfolioId() + fourEntity.getPinanAssetType() + fourEntity.getCustomAssetTypeTwo() + fourEntity.getCustomAssetTypeThree() + fourEntity.getCustomAssetTypeFour());
                                                            fourType.setHasChildren("01");

                                                            fourType.setPosCost(fourEntity.getPosCost());
                                                            fourType.setPosValue(fourEntity.getPosValue());
                                                            fourType.setTdyIntincexp(fourEntity.getTdyIntincexp());
                                                            pageList.add(fourType);

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        });

                        if(pageList != null && pageList.size()>0){
                            //单组合剩余现金处理
                            this.onePortfolio(pageList,dzhMap,dzhLxMap);
                        }
                    }
                }

                List<PtlPositionAnalysis003Dto> pageNewList = new ArrayList<>();
                pageNewList = pageList.stream().collect(Collectors
                        .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> {
                            // 根据firstName和secondName进行去重
                            return o.getParentInfo() + "," + o.getChildInfo();
                        }))), ArrayList::new));
                list.setRoot(pageNewList);
                total = pageNewList.size()/pageSize;
                if(pageNewList.size()%pageSize>0){
                    total++;
                }
                list.setTotal(total);
//                list.setTotal(pageNewList.size());
                return list;
            } else if (level.equals(DictCodeListExt3.下钻层级_资产五级分类.getValue())) {
                if (displayType.equals(DictCodeListExt3.展示方式_多组合汇总.getValue())) {
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist =
                            list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPinanAssetType));
                    if (groupBylist.size() > 0) {
                        //map遍历
                        groupBylist.forEach((k, v) -> {

                            //拼接一级分类
                            PtlPositionAnalysis003Dto typeName = new PtlPositionAnalysis003Dto();
                            typeName.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                    v.get(0).getPinanAssetType()));
                            typeName.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                    v.get(0).getPinanAssetType()));
                            typeName.setParentInfo("root");
                            typeName.setChildInfo(v.get(0).getPinanAssetType());
                            typeName.setHasChildren("02");
                            typeName.setPosCost(v.get(0).getPosCost());
                            typeName.setPosValue(v.get(0).getPosValue());
                            typeName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(typeName);
                            //二级分类
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType("", searchDate, "02", displayType, v.get(0).getPinanAssetType(), pass,map);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto twoType = new PtlPositionAnalysis003Dto();
                                    twoType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                            entity.getCustomAssetTypeTwo()));
                                    twoType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                            entity.getCustomAssetTypeTwo()));
                                    twoType.setParentInfo(entity.getPinanAssetType());
                                    twoType.setChildInfo(entity.getPinanAssetType() + entity.getCustomAssetTypeTwo());
                                    twoType.setHasChildren("02");
                                    twoType.setPosCost(entity.getPosCost());
                                    twoType.setPosValue(entity.getPosValue());
                                    twoType.setTdyIntincexp(entity.getTdyIntincexp());

                                    pageList.add(twoType);
                                    //三级分类
                                    List<PtlPositionAnalysis003Dto> typeThreeList = getFinpordType("", searchDate, "03", displayType, v.get(0).getPinanAssetType(), pass,map);
                                    if (!StringUtil.isEmptyList(typeThreeList) && typeThreeList.size() > 0) {
                                        for (PtlPositionAnalysis003Dto threeEntity : typeThreeList) {
                                            PtlPositionAnalysis003Dto threeType = new PtlPositionAnalysis003Dto();
                                            threeType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                    threeEntity.getCustomAssetTypeThree()));
                                            threeType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                    threeEntity.getCustomAssetTypeThree()));
                                            threeType.setParentInfo(threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo());
                                            threeType.setChildInfo(threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo() + threeEntity.getCustomAssetTypeThree());
                                            threeType.setHasChildren("02");
                                            threeType.setPosCost(threeEntity.getPosCost());
                                            threeType.setPosValue(threeEntity.getPosValue());
                                            threeType.setTdyIntincexp(threeEntity.getTdyIntincexp());
                                            pageList.add(threeType);
                                            //四级分类
                                            List<PtlPositionAnalysis003Dto> fourTypeList = getFinpordType("", searchDate, "04", displayType, v.get(0).getPinanAssetType(), pass,map);
                                            if (!StringUtil.isEmptyList(fourTypeList) && fourTypeList.size() > 0) {
                                                for (PtlPositionAnalysis003Dto fourEntity : fourTypeList) {
                                                    PtlPositionAnalysis003Dto fourType = new PtlPositionAnalysis003Dto();
                                                    fourType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,
                                                            fourEntity.getCustomAssetTypeFour()));
                                                    fourType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,
                                                            fourEntity.getCustomAssetTypeFour()));
                                                    fourType.setParentInfo(fourEntity.getPinanAssetType() + fourEntity.getCustomAssetTypeTwo() + fourEntity.getCustomAssetTypeThree());
                                                    fourType.setChildInfo(fourEntity.getPinanAssetType() + fourEntity.getCustomAssetTypeTwo() + fourEntity.getCustomAssetTypeThree() + fourEntity.getCustomAssetTypeFour());
                                                    fourType.setHasChildren("02");

                                                    fourType.setPosCost(fourEntity.getPosCost());
                                                    fourType.setPosValue(fourEntity.getPosValue());
                                                    fourType.setTdyIntincexp(fourEntity.getTdyIntincexp());
                                                    pageList.add(fourType);

                                                    //五级分类

                                                    List<PtlPositionAnalysis003Dto> fiveTypeList = getFinpordType("", searchDate, "05", displayType, v.get(0).getPinanAssetType(), pass,map);
                                                    if (!StringUtil.isEmptyList(fiveTypeList) && fiveTypeList.size() > 0) {
                                                        for (PtlPositionAnalysis003Dto fiveEntity : fiveTypeList) {
                                                            PtlPositionAnalysis003Dto fiveType = new PtlPositionAnalysis003Dto();
                                                            //edit by yangxin at 20220725 start 查询条件/查询结果，对应的资产一级、二级、三级、四级分类，对应债券需要按照现有分类调整，一级默认债券，二级取前台一级分类，三级取前台二级分类，四级取前台三级分类，五级分类默认为空
                                                            if (!StringUtil.equals(entity.getPinanAssetType(),DictCodeList.平安资产类型_债券.getValue())) {
                                                                fiveType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产五级分类,
                                                                        fiveEntity.getCustomAssetTypeFive()));
                                                                fiveType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产五级分类,
                                                                        fiveEntity.getCustomAssetTypeFive()));
                                                            }
                                                            //edit by yangxin at 20220725 end 查询条件/查询结果，对应的资产一级、二级、三级、四级分类，对应债券需要按照现有分类调整，一级默认债券，二级取前台一级分类，三级取前台二级分类，四级取前台三级分类，五级分类默认为空
                                                            fiveType.setParentInfo(fiveEntity.getPinanAssetType() + fiveEntity.getCustomAssetTypeTwo() + fiveEntity.getCustomAssetTypeThree() + fiveEntity.getCustomAssetTypeFour());
                                                            fiveType.setChildInfo(fiveEntity.getPinanAssetType() + fiveEntity.getCustomAssetTypeTwo() + fiveEntity.getCustomAssetTypeThree() + fiveEntity.getCustomAssetTypeFour() + fiveEntity.getCustomAssetTypeFive());
                                                            fiveType.setHasChildren("01");
                                                            fiveType.setPosCost(fiveEntity.getPosCost());
                                                            fiveType.setPosValue(fiveEntity.getPosValue());
                                                            fiveType.setTdyIntincexp(fiveEntity.getTdyIntincexp());
                                                            pageList.add(fiveType);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        });

                        if(pageList != null && pageList.size()>0){
                            //多组合剩余现金处理
                            this.allPortfolio(pageList,dzhCash,dzhLx);
                        }
                    }
                } else {
                    //单组合汇总
                    Map<String, List<PtlPositionAnalysis003Dto>> groupBylist =
                            list.getRoot().stream().collect(Collectors.groupingBy(PtlPositionAnalysis003Dto::getPortfolioId));
                    if (groupBylist.size() > 0) {

                        //map遍历
                        groupBylist.forEach((k, v) -> {

                            // 拼接组合名称
                            PtlPositionAnalysis003Dto portfolioName = new PtlPositionAnalysis003Dto();
                            portfolioName.setTypeName(
                                    v.get(0).getPortfolioId());
                            portfolioName.setPortfolioId(v.get(0).getPortfolioId());
                            portfolioName.setParentInfo("root");
                            portfolioName.setChildInfo(v.get(0).getPortfolioId());
                            portfolioName.setHasChildren("02");
                            portfolioName.setBelongInstitutionNameNew(StringUtils.isNotEmpty(v.get(0).getBelongInstitutionNameNew()) ? String.valueOf(CacheUtil.getAppCacheInstitutionCodeName(v.get(0).getBelongInstitutionNameNew())) : v.get(0).getBelongInstitutionNameNew());
                            portfolioName.setPosCost(v.get(0).getPosCost());
                            portfolioName.setPosValue(v.get(0).getPosValue());
                            portfolioName.setTdyIntincexp(v.get(0).getTdyIntincexp());
                            pageList.add(portfolioName);
                            //拼接一级分类[根据组合代码查询对应下面的所有一级分类名称]
                            List<PtlPositionAnalysis003Dto> finpordTypeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "01", displayType, "", pass,map);
                            if (!StringUtil.isEmptyList(finpordTypeList) && finpordTypeList.size() > 0) {
                                for (PtlPositionAnalysis003Dto entity : finpordTypeList) {
                                    PtlPositionAnalysis003Dto oneType = new PtlPositionAnalysis003Dto();
                                    oneType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                            entity.getPinanAssetType()));
                                    oneType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,
                                            entity.getPinanAssetType()));
                                    oneType.setParentInfo(v.get(0).getPortfolioId());
                                    oneType.setChildInfo(entity.getPortfolioId() + entity.getPinanAssetType());
                                    oneType.setHasChildren("02");
                                    oneType.setPosCost(entity.getPosCost());
                                    oneType.setPosValue(entity.getPosValue());
                                    oneType.setTdyIntincexp(entity.getTdyIntincexp());
                                    pageList.add(oneType);
                                    //二级分类
                                    List<PtlPositionAnalysis003Dto> typeTwoList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "02", displayType, entity.getPinanAssetType(), pass,map);
                                    if (!StringUtil.isEmptyList(typeTwoList) && typeTwoList.size() > 0) {
                                        for (PtlPositionAnalysis003Dto twoEntity : typeTwoList) {
                                            PtlPositionAnalysis003Dto twoType = new PtlPositionAnalysis003Dto();
                                            twoType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                                    twoEntity.getCustomAssetTypeTwo()));
                                            twoType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,
                                                    twoEntity.getCustomAssetTypeTwo()));
                                            twoType.setParentInfo(v.get(0).getPortfolioId() + twoEntity.getPinanAssetType());
                                            twoType.setChildInfo(v.get(0).getPortfolioId() + twoEntity.getPinanAssetType() + twoEntity.getCustomAssetTypeTwo());
                                            twoType.setHasChildren("02");

                                            twoType.setPosCost(twoEntity.getPosCost());
                                            twoType.setPosValue(twoEntity.getPosValue());
                                            twoType.setTdyIntincexp(twoEntity.getTdyIntincexp());
                                            pageList.add(twoType);
                                            //三级分类
                                            List<PtlPositionAnalysis003Dto> typeThreeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "03", displayType, twoEntity.getPinanAssetType(), pass,map);
                                            if (!StringUtil.isEmptyList(typeThreeList) && typeThreeList.size() > 0) {
                                                for (PtlPositionAnalysis003Dto threeEntity : typeThreeList) {
                                                    PtlPositionAnalysis003Dto threeType = new PtlPositionAnalysis003Dto();
                                                    threeType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                            threeEntity.getCustomAssetTypeThree()));
                                                    threeType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,
                                                            threeEntity.getCustomAssetTypeThree()));
                                                    threeType.setParentInfo(v.get(0).getPortfolioId() + threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo());
                                                    threeType.setChildInfo(v.get(0).getPortfolioId() + threeEntity.getPinanAssetType() + threeEntity.getCustomAssetTypeTwo() + threeEntity.getCustomAssetTypeThree());
                                                    threeType.setHasChildren("02");
                                                    threeType.setPosCost(threeEntity.getPosCost());
                                                    threeType.setPosValue(threeEntity.getPosValue());
                                                    threeType.setTdyIntincexp(threeEntity.getTdyIntincexp());
                                                    pageList.add(threeType);

                                                    //四级分类
                                                    List<PtlPositionAnalysis003Dto> fourTypeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "04", displayType, threeEntity.getPinanAssetType(), pass,map);
                                                    if (!StringUtil.isEmptyList(fourTypeList) && fourTypeList.size() > 0) {
                                                        for (PtlPositionAnalysis003Dto fourEntity : fourTypeList) {
                                                            PtlPositionAnalysis003Dto fourType = new PtlPositionAnalysis003Dto();
                                                            fourType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,
                                                                    fourEntity.getCustomAssetTypeFour()));
                                                            fourType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,
                                                                    fourEntity.getCustomAssetTypeFour()));
                                                            fourType.setParentInfo(v.get(0).getPortfolioId() + fourEntity.getPinanAssetType() + fourEntity.getCustomAssetTypeTwo() + fourEntity.getCustomAssetTypeThree());
                                                            fourType.setChildInfo(v.get(0).getPortfolioId() + fourEntity.getPinanAssetType() + fourEntity.getCustomAssetTypeTwo() + fourEntity.getCustomAssetTypeThree() + fourEntity.getCustomAssetTypeFour());
                                                            fourType.setHasChildren("02");
                                                            fourType.setPosCost(fourEntity.getPosCost());
                                                            fourType.setPosValue(fourEntity.getPosValue());
                                                            fourType.setTdyIntincexp(fourEntity.getTdyIntincexp());
                                                            pageList.add(fourType);
                                                            //五级分类

                                                            List<PtlPositionAnalysis003Dto> fiveTypeList = getFinpordType(v.get(0).getPortfolioId(), searchDate, "05", displayType, fourEntity.getPinanAssetType(), pass,map);
                                                            if (!StringUtil.isEmptyList(fiveTypeList) && fiveTypeList.size() > 0) {
                                                                for (PtlPositionAnalysis003Dto fiveEntity : fiveTypeList) {
                                                                    PtlPositionAnalysis003Dto fiveType = new PtlPositionAnalysis003Dto();
                                                                    //edit by yangxin at 20220725 start 查询条件/查询结果，对应的资产一级、二级、三级、四级分类，对应债券需要按照现有分类调整，一级默认债券，二级取前台一级分类，三级取前台二级分类，四级取前台三级分类，五级分类默认为空
                                                                    if (!StringUtil.equals(entity.getPinanAssetType(),DictCodeList.平安资产类型_债券.getValue())) {
                                                                        fiveType.setPortfolioId(SysDataUtil.getNameFromDataDict(DictCode.资产五级分类,
                                                                                fourEntity.getCustomAssetTypeFive()));
                                                                        fiveType.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.资产五级分类,
                                                                                fourEntity.getCustomAssetTypeFive()));
                                                                    }
                                                                    //edit by yangxin at 20220725 end 查询条件/查询结果，对应的资产一级、二级、三级、四级分类，对应债券需要按照现有分类调整，一级默认债券，二级取前台一级分类，三级取前台二级分类，四级取前台三级分类，五级分类默认为空
                                                                    fiveType.setParentInfo(v.get(0).getPortfolioId() + fiveEntity.getPinanAssetType() + fiveEntity.getCustomAssetTypeTwo() + fiveEntity.getCustomAssetTypeThree() + fiveEntity.getCustomAssetTypeFour());
                                                                    fiveType.setChildInfo(v.get(0).getPortfolioId() + fiveEntity.getPinanAssetType() + fiveEntity.getCustomAssetTypeTwo() + fiveEntity.getCustomAssetTypeThree() + fiveEntity.getCustomAssetTypeFour() + fiveEntity.getCustomAssetTypeFive());
                                                                    fiveType.setHasChildren("02");

                                                                    fiveType.setPosCost(fiveEntity.getPosCost());
                                                                    fiveType.setPosValue(fiveEntity.getPosValue());
                                                                    fiveType.setTdyIntincexp(fiveEntity.getTdyIntincexp());
                                                                    pageList.add(fiveType);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        });

                        if(pageList != null && pageList.size()>0){
                            //单组合剩余现金处理
                            this.onePortfolio(pageList,dzhMap,dzhLxMap);
                        }
                    }

                }

                List<PtlPositionAnalysis003Dto> pageNewList = new ArrayList<>();
                pageNewList = pageList.stream().collect(Collectors
                        .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> {
                            // 根据firstName和secondName进行去重
                            return o.getParentInfo() + "," + o.getChildInfo();
                        }))), ArrayList::new));
                list.setRoot(pageNewList);
                total = pageNewList.size()/pageSize;
                if(pageNewList.size()%pageSize>0){
                    total++;
                }
                list.setTotal(total);
//                list.setTotal(pageNewList.size());
                return list;
            }
        }

        // 仅在根节点 但组合时才会 展示 机构名称
        if (parentId.equals("root")) {

            if (displayType.equals(DictCodeListExt3.展示方式_单组合汇总.getValue())) {

                list.getRoot().forEach(item -> {
                    item.setBelongInstitutionNameNew(item.getBelongInstitutionName());
                });
            }
        }
        BigDecimal bg = BigDecimal.ZERO;
        if(map.get("sumVal1") != null) {
            bg = (BigDecimal)map.get("sumVal1");
        }else {
            bg = getSumPrice(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), searchDate, pass);
        }
        final BigDecimal sumVal1 = bg;
        Map<String,AssetTargetDb> targetDbMap1 = new HashMap<>();
        if(map.get("targetDbMap") != null) {
            targetDbMap1 = (Map)map.get("targetDbMap"); ;
        }else {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("date", DateUtil.format(DateUtil.addDay(DateUtil.parse(searchDate), -1)));//查询日-1
            map1.put("pass", pass);
            List<AssetTargetDb> assetTargetList = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getAllYesterdayTarget", map1, AssetTargetDb.class);
            if (CollectionUtils.isNotEmpty(assetTargetList)) {
                for(AssetTargetDb db : assetTargetList) {
                    targetDbMap1.put(db.getFinprodId(),db);
                }
            }
        }
        Map<String,AssetTargetDb> targetDbMap = targetDbMap1;
        Map<String,MstCustomerInfo> customerMap = new HashMap<>();
        Map<String,MstCounterParty> counterMap = new HashMap<>();
        List<String> counterIds = new ArrayList<>();
        List<String> customerIds = new ArrayList<>();
        list.getRoot().forEach(e->{
            if(StringUtil.isNotEmpty(e.getCounterId())) {
                counterIds.add(e.getCounterId());
            }
            if(StringUtil.isNotEmpty(e.getIssueParty())) {
                customerIds.add(e.getIssueParty());
            }
        });
        if(CollectionUtils.isNotEmpty(customerIds)) {
            List<MstCustomerInfo> customerList = mstCustomerInfoMapper.getAllCustomerName(customerIds);
            if(CollectionUtils.isNotEmpty(customerList)) {
                customerList.forEach(e->customerMap.put(e.getCustomerId(),e));
            }
        }
        if(CollectionUtils.isNotEmpty(counterIds)) {
            List<MstCounterParty> counterList = mstCounterPartyMapper.getAllCounterName(counterIds);
            if(CollectionUtils.isNotEmpty(counterList)) {
                counterList.forEach(e->counterMap.put(e.getCounterId(),e));
            }
        }
        list.getRoot().forEach(item -> {
            String typeMsg = item.getTypeName();//分类
            if (StringUtil.isNotEmpty(typeMsg) && typeMsg.equals("child")) {
                //是否为导出，为导出显示分类名称
                if (map.get("export") == null) {
                    item.setTypeName("");  //子节点不显示分类名称
                } else {
                    item.setTypeName(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,item.getPinanAssetType()));
                }
                if (displayType.equals(DictCodeListExt3.展示方式_单组合汇总.getValue())) {
                    item.setPortfolioId("");
                }
                item.setPinanAssetType(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型,item.getPinanAssetType()));
                item.setCustomAssetTypeTwo(SysDataUtil.getNameFromDataDict(DictCode.资产二级分类,item.getCustomAssetTypeTwo()));
                item.setCustomAssetTypeThree(SysDataUtil.getNameFromDataDict(DictCode.资产三级分类,item.getCustomAssetTypeThree()));
                item.setCustomAssetTypeFour(SysDataUtil.getNameFromDataDict(DictCode.资产四级分类,item.getCustomAssetTypeFour()));
                item.setCustomAssetTypeFive(SysDataUtil.getNameFromDataDict(DictCode.资产五级分类,item.getCustomAssetTypeFive()));
                //企业性质
                item.setCompanyNature(SysDataUtil.getNameFromDataDict(DictCode.企业性质,item.getCompanyNature()));
                //所属行业
                item.setIndustry(SysDataUtil.getNameFromDataDict(DictCode.机构所属行业一级分类,item.getIndustry()));
                //行业二级分类
                item.setIndustry2(SysDataUtil.getNameFromDataDict(DictCode.机构所属行业二级分类,item.getIndustry2()));
                //是否国企
                item.setIsSoe(SysDataUtil.getNameFromDataDict(DictCode.是否,item.getIsSoe()));
                //是否上市公司
                item.setIsListedCompany(SysDataUtil.getNameFromDataDict(DictCode.是否,item.getIsListedCompany()));
                //是否城投债
                item.setIsCityBond(SysDataUtil.getNameFromDataDict(DictCode.是否,item.getIsCityBond()));
                //债券评级[只有债券资产才显示相关信息，其他资产显示为空]
                if (StringUtil.isNotEmpty(item.getPinanAssetType()) && item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_债券.getCodeName())) {
                    // 估计收益率 保留4位小数
                    item.setBpyield(item.getBpyield() == null ? new BigDecimal(0) : item.getBpyield().setScale(4, BigDecimal.ROUND_HALF_UP));
                    //所属地区
                    item.setCityBondLev(SysDataUtil.getNameFromDataDict(DictCode.城投债级别,item.getCityBondLev()));
                    //持仓占发行总额比% =持仓券面总额/发行券面总额;  持仓占债券资产规模比%=（持仓市值+应计利息）/Σ(每只债券持仓市值+每只债券应计利息)
                    if (StringUtil.isNotEmpty(item.getBshareFaceValue()) && StringUtil.isNotEmpty(item.getIssueFaceValue()) && item.getIssueFaceValue().compareTo(new BigDecimal(0)) != 0) {
                        item.setPositionTotalIssuance(item.getBshareFaceValue().multiply(new BigDecimal(100)).divide(item.getIssueFaceValue(), 2, BigDecimal.ROUND_HALF_UP));//持仓占发行总额比%
                    }
                    //根据查询日期查询当前所有债券的持仓市值和应计利息进行汇总
                    BigDecimal sumVal = sumVal1;
                    if (sumVal.compareTo(new BigDecimal(0)) != 0) {
                        item.setPositionBondIssuance(item.getPosValue() == null ? new BigDecimal(0) : item.getPosValue().add(item.getTdyIntincexp() == null ? new BigDecimal(0) : item.getTdyIntincexp()).multiply(new BigDecimal(100)).divide(sumVal, 2, BigDecimal.ROUND_HALF_UP));//持仓占债券资产规模比%
                    }
                    //*****债券评级相关******
                    //查询外部主体评级信息
                    if (StringUtil.isNotEmpty(item.getmGradeResult())) {
                        item.setmGradeResult(SysDataUtil.getNameFromDataDict(DictCode.评级结果, item.getmGradeResult()));//主体评级结果
                    }
                    if (StringUtil.isNotEmpty(item.getmGradeOrg())) {
                        item.setmGradeOrg(SysDataUtil.getNameFromDataDict(DictCode.评级机构, item.getmGradeOrg()));// 主体评级结构
                    }
                    // 查询外部债项评级相关信息
                    if (StringUtil.isNotEmpty(item.getbGradeResult())) {
                        item.setbGradeResult(SysDataUtil.getNameFromDataDict(DictCode.评级结果, item.getbGradeResult()));//债项评级结果
                    }
                    if (StringUtil.isNotEmpty(item.getbGradeOrg())) {
                        item.setbGradeOrg(SysDataUtil.getNameFromDataDict(DictCode.评级机构, item.getbGradeOrg()));// 债项评级结构
                    }
                    //查询内部主体评级信息
                    if (StringUtil.isNotEmpty(item.getmGradeResultIn())) {
                        item.setmGradeResultIn(SysDataUtil.getNameFromDataDict(DictCode.评级结果, item.getmGradeResultIn()));//主体评级结果
                    }
                    if (StringUtil.isNotEmpty(item.getmGradeOrgIn())) {
                        item.setmGradeOrgIn(SysDataUtil.getNameFromDataDict(DictCode.评级机构, item.getmGradeOrgIn()));// 主体评级结构
                    }
                    // 查询内部债项评级相关信息
                    if (StringUtil.isNotEmpty(item.getbGradeResultIn())) {
                        item.setbGradeResultIn(SysDataUtil.getNameFromDataDict(DictCode.评级结果, item.getbGradeResultIn()));//债项评级结果
                    }
                    if (StringUtil.isNotEmpty(item.getbGradeOrgIn())) {
                        item.setbGradeOrgIn(SysDataUtil.getNameFromDataDict(DictCode.评级机构, item.getbGradeOrgIn()));// 债项评级结构
                    }
                } else {//其他场景下剩余行权期限为空
                    item.setLastExPeriod(null);
                }
                //股票持仓占流通股本比%=持仓数量/流通股本；持仓占总股本比%==持仓数量/总股本
                if (StringUtil.isNotEmpty(item.getPinanAssetType()) && item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_股票.getCodeName())) {
                    if (StringUtil.isNotEmpty(item.getcShares()) && item.getcShares().compareTo(new BigDecimal(0)) != 0) {
                        item.setcSharesPercentage(item.getPosAmount() == null ? new BigDecimal(0) : item.getPosAmount().multiply(new BigDecimal(100)).divide(item.getcShares(), 2, BigDecimal.ROUND_HALF_UP));//股票持仓占流通股本比%
                    }
                    if (StringUtil.isNotEmpty(item.getsShares()) && item.getsShares().compareTo(new BigDecimal(0)) != 0) {
                        item.setsSharesPercentage(item.getPosAmount() == null ? new BigDecimal(0) : item.getPosAmount().multiply(new BigDecimal(100)).divide(item.getsShares(), 2, BigDecimal.ROUND_HALF_UP));//持仓占总股本比%
                    }
                }
                //是否永续
                item.setIsSus(SysDataUtil.getNameFromDataDict(DictCode.是否, item.getIsSus()));
                //是否含权债
                item.setIsRight(SysDataUtil.getNameFromDataDict(DictCode.是否, item.getIsRight()));
                //收益类型
                item.setProfitType(SysDataUtil.getNameFromDataDict(DictCode.收益类型, item.getProfitType()));
                //交易场所
                item.setTradeMarket(SysDataUtil.getNameFromDataDict(DictCode.交易场所, item.getTradeMarket()));
                //名义本金 对于利率互换，显示为名义本金；其他类资产显示为空
                if (!item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue()) && !item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_信用保护凭证.getCodeName())) {
                    item.setPrin(null);
                }
                //根据交易对手id 获取名称
                if (StringUtil.isNotEmpty(item.getCounterId())) {
                    //只有交易型的资产：拆借、质押式回购、买断式回购、债券借贷、利率互换、外汇远期、外汇掉期才显示交易对手，取交易记录上的交易对手；其他资产显示为空
                    if (item.getPinanAssetType().equals(DictCodeList.平安资产类型_拆借.getCodeName()) || item.getPinanAssetType().equals(DictCodeList.平安资产类型_质押式回购.getCodeName()) || item.getPinanAssetType().equals(DictCodeList.平安资产类型_买断式回购.getCodeName()) || item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_债券借贷.getCodeName()) || item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_利率互换.getCodeName()) || item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_远期.getCodeName()) || item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_掉期.getCodeName())) {
                        MstCounterParty mstCounterParty = counterMap.get(item.getCounterId());
                        item.setCounterId(mstCounterParty == null ? "" : mstCounterParty.getCounterName());
                    } else {
                        item.setCounterId("");
                    }
                }
                //发行主体
                if (StringUtil.isNotEmpty(item.getIssueParty())) {
                    MstCustomerInfo mstCustomerInfo = customerMap.get(item.getIssueParty());
                    item.setIssueParty(mstCustomerInfo == null ? "" : mstCustomerInfo.getCustomerName());
                }
                //债券显示为估价净价,非货币基金和净值型计划显示为单位净值,  股票显示为收盘价,  其他资产显示为空
                if (item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_债券.getCodeName()) || item.getPinanAssetType().equals(DictCodeList.基金类资产类型_非货币基金.getCodeName()) || item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_股票.getCodeName())) {
                    //公允价格处理
                    if (StringUtil.isNotEmpty(item.getPinanAssetType()) && item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_债券.getCodeName())) {
                        // 公允价格 = round（估值明细.公允价值/估值明细.数量，6）
                        if (StringUtil.isNotEmpty(item.getFairePrice()) && StringUtil.isNotEmpty(item.getPosAmount()) && item.getPosAmount().compareTo(new BigDecimal(0)) != 0) {
                            item.setFairePrice(item.getFairePrice().divide(item.getPosAmount(), 6, BigDecimal.ROUND_HALF_UP));
                        }
                    } else if (StringUtil.isNotEmpty(item.getPinanAssetType()) && item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_股票.getCodeName())) {
                        //公允价格 = round（估值明细.公允价值/估值明细.数量，2）
                        if (StringUtil.isNotEmpty(item.getFairePrice()) && StringUtil.isNotEmpty(item.getPosAmount()) && item.getPosAmount().compareTo(new BigDecimal(0)) != 0) {
                            item.setFairePrice(item.getFairePrice().divide(item.getPosAmount(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    } else {
                        //公允价格 = round（估值明细.公允价值/估值明细.数量，4）
                        if (StringUtil.isNotEmpty(item.getFairePrice()) && StringUtil.isNotEmpty(item.getPosAmount()) && item.getPosAmount().compareTo(new BigDecimal(0)) != 0) {
                            item.setFairePrice(item.getFairePrice().divide(item.getPosAmount(), 4, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                    //价格涨跌=价格涨跌 = 公允价格 - 昨日持仓.公允价格;价格涨跌幅=价格涨跌/ 昨日持仓.公允价格
                    BigDecimal yesterdayFairePrice = new BigDecimal(0);//昨日持仓公允价格
                    yesterdayFairePrice = getFairePrice(item.getPinanAssetType(), item.getFinprodId(), searchDate, pass,targetDbMap);
                    item.setPriceFluctuation(item.getFairePrice() == null ? new BigDecimal(0) : item.getFairePrice().subtract(yesterdayFairePrice));
                    if (yesterdayFairePrice.compareTo(new BigDecimal(0)) != 0) {
                        item.setPriceFluctuationPercentage(item.getPriceFluctuation().multiply(new BigDecimal(100)).divide(yesterdayFairePrice, 2, BigDecimal.ROUND_HALF_UP));
                    } else {
                        item.setPriceFluctuationPercentage(new BigDecimal(0));
                    }
                } else {
                    item.setPriceFluctuation(null);
                    item.setPriceFluctuationPercentage(null);
                    item.setFairePrice(null);

                    // 将二级法人拦截器中的值赋给新的参数值
                    // item.setBelongInstitutionNameNew(item.getBelongInstitutionName());
                }
            }
            // 关闭拦截器生成的机构信息
            item.setBelongInstitutionName(null);
            item.setBelongInstitutionCode(null);
        });

        return list;
    }


    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.dataSource((String) exportParam.getSelectParam().get("tablelist"));
        exportParam.config(
                "/com/joyintech/tams/invest/portfolio/ptlpositionanalysis/conf/export_report_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PtlPositionAnalysis003Dto> selectPageData(Map<String, Object> map) {
        String searchDate = map.get("searchDate").toString();//查询日期
        map.put("parentId", "root");
        List<PtlPositionAnalysis003Dto> pageList = new ArrayList<>();
        String level = map.get("deepLevel").toString();
        String displayType = map.get("displayType").toString();
        map.put("export", "Y");
        //剔除之前导出文件中包含的数据
        int thisPage = (int)map.get("page");
        int pageSize = (int)map.get("length");
        int popNum = (thisPage-1)*pageSize; 
        //全部查出
        map.put("page", 1);
        map.put("length", Integer.toString(pageSize));
        Page<PtlPositionAnalysis003Dto> list = selectForPage(map);

        Map<String, String> institutionNameMap = new HashMap<>();
        list.getRoot().forEach(name -> {
            if ("root".equals(name.getParentInfo())) {
                institutionNameMap.put(name.getTypeName(), name.getBelongInstitutionNameNew());
            }
        });
        String pass = map.get("pass").toString();
        BigDecimal sumVal1 = getSumPrice(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), searchDate, pass);
        map.put("sumVal1",sumVal1);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("date", DateUtil.format(DateUtil.addDay(DateUtil.parse(searchDate), -1)));//查询日-1
        map1.put("pass", pass);
        List<AssetTargetDb> assetTargetList = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getAllYesterdayTarget", map1, AssetTargetDb.class);
        Map<String,AssetTargetDb> targetDbMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(assetTargetList)) {
            assetTargetList.forEach(e->targetDbMap.put(e.getFinprodId(),e));
            map.put("targetDbMap",targetDbMap);
        }
        for(PtlPositionAnalysis003Dto item: list.getRoot()) {
          map.put("parentId", item.getChildInfo());
          if (item.getHasChildren().equals("01")) {
        	//获取父层的组合代码
        	String portfolioId = null;
        	for(PtlPositionAnalysis003Dto ptlPositionAnalysis003Dto: list.getRoot()) {
        		if(StringUtil.equals(ptlPositionAnalysis003Dto.getChildInfo(),item.getParentInfo())){
        			portfolioId = ptlPositionAnalysis003Dto.getPortfolioId();
        		}
        	}
        	
        	//子查询中并未调用queryForPage包装分页，该结果为全部查询结果，不需要循环
          	Page<PtlPositionAnalysis003Dto> ptlPositionAnalysis003DtoPage = selectForPage(map);
          	for (PtlPositionAnalysis003Dto entity : ptlPositionAnalysis003DtoPage.getRoot()) {
          		if (displayType.equals(DictCodeListExt3.展示方式_单组合汇总.getValue())) {
          			entity.setPortfolioId(portfolioId);
          		}
                for (String key : institutionNameMap.keySet()) {
                	//资产一级分类 不止F00-F09,还会出现F1的情形，考虑到后续可能的扩展，此处去掉0
                    if (entity.getParentInfo().startsWith(key + "F0") || entity.getParentInfo().startsWith(key + "F1")) { // todo 没有完全相等的条件
                        entity.setBelongInstitutionNameNew(institutionNameMap.get(key) != null ? institutionNameMap.get(key).toString() : "");
                    }
                }
				if(popNum>0) {
					popNum--;
                }
                else {
					pageList.add(entity);
                }
            }
          }
        };
        
        //计算总页数
        int total = pageList.size()/pageSize;
        if(pageList.size()%pageSize>0) {
        	total = total+1;
        }
        list.setTotal(total);
        list.setRoot(pageList);
        return list;
    }

    /**
     * 查询债券相关最优评级结果
     */
    private List<BusiFinProductRatingDto> getBestProductRating(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/FinProductRating/getBestProductRating", map, BusiFinProductRatingDto.class);
    }

    /**
     * 获取动态列结果
     *
     * @param map
     * @return 查询结果集
     * @throws Exception 异常
     */
    public String getDataTableCols(Map<String, Object> map) {
        // 1.获取当前用户查询显示的列名称
        List<TargetDetail> retList = new ArrayList<>();
        String userId = com.joyintech.tams.systemtools.util.CommonUtil.getUserName();
        List<Map<String, Object>> colsList = new ArrayList<>();
        retList = restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/getDataTableCols", userId, TargetDetail.class);
        // 1.获取指标明细中对应指标列的名称和类型
        List<TargetDetail> columnMaps = restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/getTableColumn", userId, TargetDetail.class);
        String jsonStr = "";
        if (retList == null || retList.size() == 0) {
            // 加载库中默认列
            retList = restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/getDefaultTableCols", userId, TargetDetail.class);
        }

        if (map.get("displayType") != null && map.get("displayType").equals("02")) {
            // 多组合查询列表前面添加分类列
            Map<String, Object> typeCol = new HashMap<>();
            typeCol.put("field", "typeName");//字段代码
            typeCol.put("fieldMsg", "typeName");
            typeCol.put("title", "分类");//字段名称
            typeCol.put("sortable", false);//字段是否排序
            typeCol.put("type", "String");//字段类型
            colsList.add(typeCol);
        }

        if (map.get("displayType") != null && map.get("displayType").equals("01")) {
            // 单组合查询列表前面加组合列
            Map<String, Object> portfolioIdCol = new HashMap<>();
            portfolioIdCol.put("field", "portfolioId");//字段代码
            portfolioIdCol.put("fieldMsg", "portfolioId");
            portfolioIdCol.put("title", "分类");//字段名称
            portfolioIdCol.put("sortable", false);//字段是否排序
            portfolioIdCol.put("type", "String");//字段类型
            colsList.add(portfolioIdCol);
        }

        if (retList != null && retList.size() > 0) {
            Map<String, Object> colsMap;
            boolean sortable = false;
            for (TargetDetail targetDetail : retList) {
                colsMap = new HashMap<>();
                colsMap.put("field", targetDetail.getTaregetCode());//字段代码
                colsMap.put("fieldMsg", camelCaseName(targetDetail.getTaregetCode()));//字段代码转驼峰格式
                colsMap.put("title", targetDetail.getTaregetName());//字段名称
                colsMap.put("sortable", sortable);//字段是否排序
                List<TargetDetail> filedMsg = columnMaps.stream().filter(item -> item.getTaregetCode().equals(targetDetail.getTaregetCode())).collect(Collectors.toList());
                if (filedMsg != null && filedMsg.size() > 0 && filedMsg.get(0).getTaregetTypeCode().equals("NUMBER")) {
                    colsMap.put("type", "number");//字段类型
                } else if (filedMsg != null && filedMsg.size() > 0 && filedMsg.get(0).getTaregetTypeCode().equals("DATE")) {
                    colsMap.put("type", "date");//字段类型
                } else {
                    colsMap.put("type", "String");//字段类型
                }

                //判断组合是否已存在，不存在时添加
                if (map.get("displayType") != null && map.get("displayType").equals("01")) {
                    if (!targetDetail.getTaregetCode().equals("PORTFOLIO_ID")) {
                        colsList.add(colsMap);
                    }
                } else {
                    colsList.add(colsMap);
                }
            }
        }

        if (map.get("displayType") != null && map.get("displayType").equals("02")) {
            if (MultilevelDataAuthorityUtil.isEnabled()) {
                Map<String, Object> belongInstitutionCol = new HashMap<>();
                belongInstitutionCol.put("field", "belongInstitutionNameNew");//字段代码
                belongInstitutionCol.put("fieldMsg", "belongInstitutionNameNew");
                belongInstitutionCol.put("title", "机构名称");//字段名称
                belongInstitutionCol.put("sortable", false);//字段是否排序
                belongInstitutionCol.put("type", "String");//字段类型
                colsList.add(belongInstitutionCol);
            }
        }

        if (map.get("displayType") != null && map.get("displayType").equals("01")) {
            // 银行模式不展示 TISHKBS-1171
            if (MultilevelDataAuthorityUtil.isEnabled()) {
                Map<String, Object> belongInstitutionCol = new HashMap<>();
                belongInstitutionCol.put("field", "belongInstitutionNameNew");//字段代码
                belongInstitutionCol.put("fieldMsg", "belongInstitutionNameNew");
                belongInstitutionCol.put("title", "机构名称");//字段名称
                belongInstitutionCol.put("sortable", false);//字段是否排序
                belongInstitutionCol.put("type", "String");//字段类型
                colsList.add(belongInstitutionCol);
            }
        }
        jsonStr = JsonUtils.object2Json(colsList);
        return jsonStr;
    }

    /**
     * 转换为驼峰
     *
     * @param underscoreName
     * @return
     */
    public static String camelCaseName(String underscoreName) {

        //修改久期特殊处理
        if (underscoreName.equals("MDURATION")) {
            return "mdurationAmend";
        }
        if (underscoreName.equals("M_DURATION")) {
            return "mduration";
        }
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(Character.toLowerCase(ch));
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * 昨日公允价格获取
     * 资产代码，查询日期
     *
     * @return
     * @paramFAIRE_PRICE
     */
    public BigDecimal getFairePrice(String finprodType, String finprodId, String searchDate, String pass,Map<String,AssetTargetDb> targetDbMap) {
        BigDecimal fairePrice = new BigDecimal(0); //昨日公允价格
        AssetTargetDb assetTargetDb = targetDbMap.get(finprodId);
        if (assetTargetDb == null) {
            return new BigDecimal(0);
        }
        if (StringUtil.isNotEmpty(finprodType) && finprodType.equals(DictCodeListExt1.金融产品类型_投管用_债券.getCodeName())) {
            // 公允价格 = round（估值明细.公允价值/估值明细.数量，6）
            if (StringUtil.isNotEmpty(assetTargetDb.getFairePrice()) && StringUtil.isNotEmpty(assetTargetDb.getPosAmount()) && assetTargetDb.getPosAmount().compareTo(new BigDecimal(0)) != 0) {
                fairePrice = assetTargetDb.getFairePrice().divide(assetTargetDb.getPosAmount(), 6, BigDecimal.ROUND_HALF_UP);
            }
        } else if (StringUtil.isNotEmpty(finprodType) && finprodType.equals(DictCodeListExt1.金融产品类型_投管用_股票.getCodeName())) {
            //公允价格 = round（估值明细.公允价值/估值明细.数量，2）
            if (StringUtil.isNotEmpty(finprodType) && StringUtil.isNotEmpty(assetTargetDb.getPosAmount()) && assetTargetDb.getPosAmount().compareTo(new BigDecimal(0)) != 0) {
                fairePrice = assetTargetDb.getFairePrice().divide(assetTargetDb.getPosAmount(), 2, BigDecimal.ROUND_HALF_UP);
            }
        } else {
            //公允价格 = round（估值明细.公允价值/估值明细.数量，4）
            if (StringUtil.isNotEmpty(assetTargetDb.getFairePrice()) && StringUtil.isNotEmpty(assetTargetDb.getPosAmount()) && assetTargetDb.getPosAmount().compareTo(new BigDecimal(0)) != 0) {
                fairePrice = assetTargetDb.getFairePrice().divide(assetTargetDb.getPosAmount(), 4, BigDecimal.ROUND_HALF_UP);
            }
        }
        return fairePrice;
    }

    /**
     * 查询当前时间的持仓市值和应计利息汇总值
     * 资产类型为债券，查询日期
     *
     * @return
     * @paramFAIRE_PRICE
     */
    public BigDecimal getSumPrice(String finprodType, String searchDate, String pass) {
        BigDecimal sumPrice = new BigDecimal(0);
        Map<String, Object> map = new HashMap<>();
        map.put("finprodType", finprodType);
        map.put("date", searchDate);
        map.put("pass", pass);
        ResponseEntity<AssetTargetDb> assetTargetDbResponseEntity = restTemplate.postForEntity(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSumPrice", map, AssetTargetDb.class);
        if (assetTargetDbResponseEntity != null && assetTargetDbResponseEntity.getBody() != null) {
            sumPrice = assetTargetDbResponseEntity.getBody().getFairePrice() == null ? new BigDecimal(0) : assetTargetDbResponseEntity.getBody().getFairePrice();
        }
        return sumPrice;
    }

    /**
     * 成本收益率计算
     * 资产代码，查询日期
     *
     * @param
     * @return
     */

    public BigDecimal calculationCostRate(String finprodId, String searchDate, Map<String, Object> pageParam, String portfolioId, String pass) {
        String displayType = pageParam.get("displayType").toString();
        BigDecimal yesterDayPosAmount = new BigDecimal(0); //昨日持仓数量
        BigDecimal yesterDaycostRate = new BigDecimal(0); //昨日综合成本收益率
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("date", searchDate);
        map.put("pass", pass);
        //根据资产代码查询相关所有的交易信息
        if (displayType.equals(DictCodeListExt3.展示方式_多组合汇总.getValue())) {
            if (StringUtil.isNotEmpty(pageParam.get("portfolioId"))) {
                map.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, (String) pageParam.get("portfolioId")));
            }
        } else {
            map.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        List<TrdProductDeal> trdProductDeallist = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getTradeListByFinprodId",
                map, TrdProductDeal.class);

        // 根据资产代码查询昨日指标信息的持仓数量和综合成本收益率
        map.put("date", DateUtil.format(DateUtil.addDay(DateUtil.parse(searchDate), -1)));//查询日-1
        ResponseEntity<AssetTargetDb> assetTargetDbResponseEntity = restTemplate.postForEntity(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getYesterdayTarget",
                map, AssetTargetDb.class);

        if (assetTargetDbResponseEntity != null && assetTargetDbResponseEntity.getBody() != null) {
            yesterDayPosAmount = assetTargetDbResponseEntity.getBody().getPosAmount() == null ? new BigDecimal(0) : assetTargetDbResponseEntity.getBody().getPosAmount();
            yesterDaycostRate = assetTargetDbResponseEntity.getBody().getCostRate() == null ? new BigDecimal(0) : assetTargetDbResponseEntity.getBody().getCostRate();
        }
        if (!StringUtil.isEmptyList(trdProductDeallist)) {
            for (TrdProductDeal trdProductDeal : trdProductDeallist) {
                if (trdProductDeal.getPs().equals(DictCodeList.交易方向_买入.getValue())) {

                    if (yesterDayPosAmount.add(trdProductDeal.getShareAmt() == null ? new BigDecimal(0) : trdProductDeal.getShareAmt()).compareTo(new BigDecimal(0)) != 0) {
                        yesterDaycostRate = (yesterDayPosAmount.multiply(yesterDaycostRate).add(trdProductDeal.getShareAmt() == null ? new BigDecimal(0) : trdProductDeal.getShareAmt().multiply(trdProductDeal.getYield() == null ? new BigDecimal(0) : trdProductDeal.getYield()))).divide(yesterDayPosAmount.add(trdProductDeal.getShareAmt() == null ? new BigDecimal(0) : trdProductDeal.getShareAmt()), 4);
                    }
                    yesterDayPosAmount = yesterDayPosAmount.add(trdProductDeal.getShareAmt() == null ? new BigDecimal(0) : trdProductDeal.getShareAmt());

                }

                if (trdProductDeal.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {

                  /*  if (yesterDayPosAmount.subtract(trdProductDeal.getShareAmt() == null ? new BigDecimal(0) : trdProductDeal.getShareAmt()).compareTo(new BigDecimal(0)) != 0) {
                        yesterDaycostRate = (yesterDayPosAmount.multiply(yesterDaycostRate).add((yesterDayPosAmount.subtract(trdProductDeal.getShareAmt() == null ? new BigDecimal(0) : trdProductDeal.getShareAmt())).multiply(yesterDaycostRate))).divide(yesterDayPosAmount.subtract(trdProductDeal.getShareAmt()== null ? new BigDecimal(0) : trdProductDeal.getShareAmt()),4);
                    }*/
                    yesterDayPosAmount = yesterDayPosAmount.subtract(trdProductDeal.getTradeAmt() == null ? new BigDecimal(0) : trdProductDeal.getTradeAmt());
                }
            }

        }
        return yesterDaycostRate.multiply(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 剩余期限计算
     * 资产代码、起息日、查询日期、分支序号
     *
     * @param
     * @return
     */
    public BigDecimal calculationSurplusDays(String finprodId, String vdate, String mdate, String searchDate, Long branch) {
        BigDecimal surplusDays = new BigDecimal(0);// 剩余期限
        //根据资产代码、起息日、分支序号 查询对应的计息基础
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("effDate", vdate);
        map.put("branch", branch);
        ResponseEntity<FinCashCalcRule> finCashCalcRuleResponseEntity = restTemplate.postForEntity(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getBasisByFinprodId",
                map, FinCashCalcRule.class);
        if (finCashCalcRuleResponseEntity != null && finCashCalcRuleResponseEntity.getBody() != null) {
            FinCashCalcRule finCashCalcRule = finCashCalcRuleResponseEntity.getBody();
            if (finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_365.getValue()) || finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_365F.getValue())) {
                //变量.剩余期限 = round（（参数.到期日 - 参数.查询日）/365，2）
                surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(mdate))).divide(new BigDecimal(365), 2, BigDecimal.ROUND_HALF_UP);
            } else if (finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_360.getValue()) || finCashCalcRule.getBasis().equals(DictCodeList.计息基础_30_360.getValue())) {
                //变量.剩余期限 = round（（参数.到期日 - 参数.查询日）/360，2）
                surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(mdate))).divide(new BigDecimal(360), 2, BigDecimal.ROUND_HALF_UP);
            } else if (finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A_平均分配.getValue()) || finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A_按实际天数分配.getValue()) || finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A.getValue())) {
                //if 参数.到期日.年份 = 参数.查询日.年份  变量.剩余期限 = round（（参数.到期日 - 参数.查询日）/（参数.查询日的年的12月31-参数.查询日的年的1月1+1），2）

                if (DateUtil.getYear(mdate) == DateUtil.getYear(searchDate)) {

                    surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(mdate))).divide(new BigDecimal(DateUtil.getDayOfYear(searchDate)), 2, BigDecimal.ROUND_HALF_UP);
                }
            } else {
                if (DateUtil.getYear(mdate) == DateUtil.getYear(searchDate)) {

                    surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(mdate))).divide(new BigDecimal(DateUtil.getDayOfYear(searchDate)), 2, BigDecimal.ROUND_HALF_UP);
                }
            }
        }
        return surplusDays;
    }

    /**
     * 剩余行权期限计算
     * 资产代码、起息日\下一行权日期、查询日期\分支序号
     *
     * @param
     * @return
     */
    public BigDecimal calculationExPeriodDays(String finprodId, String date, String searchDate, Long branch, String lastExPeriodDate) {
        BigDecimal surplusDays = new BigDecimal(0);// 剩余行权期限
        //根据资产代码、起息日、分支序号 查询对应的计息基础
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("effDate", date);
        map.put("branch", branch);
        ResponseEntity<FinCashCalcRule> finCashCalcRuleResponseEntity = restTemplate.postForEntity(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getBasisByFinprodId",
                map, FinCashCalcRule.class);
        if (finCashCalcRuleResponseEntity != null && finCashCalcRuleResponseEntity.getBody() != null) {
            FinCashCalcRule finCashCalcRule = finCashCalcRuleResponseEntity.getBody();
            if (finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_365.getValue()) || finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_365F.getValue())) {
                //变量.剩余行权期限 = round（（参数.下一行权日期 - 参数.查询日）/365，2）
                surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(lastExPeriodDate))).divide(new BigDecimal(365), 2, BigDecimal.ROUND_HALF_UP);
            } else if (finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_360.getValue()) || finCashCalcRule.getBasis().equals(DictCodeList.计息基础_30_360.getValue())) {
                //变量.剩余行权期限 = round（（参数.下一行权日期 - 参数.查询日）/360，2）
                surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(lastExPeriodDate))).divide(new BigDecimal(360), 2, BigDecimal.ROUND_HALF_UP);
            } else if (finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A_平均分配.getValue()) || finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A_按实际天数分配.getValue()) || finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A.getValue())) {
                //if 参数.到期日.年份 = 参数.查询日.年份  变量.剩余期限 = round（（参数.到期日 - 参数.查询日）/（参数.查询日的年的12月31-参数.查询日的年的1月1+1），2）

                if (DateUtil.getYear(lastExPeriodDate) == DateUtil.getYear(searchDate)) {

                    surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(lastExPeriodDate))).divide(new BigDecimal(DateUtil.getDayOfYear(searchDate)), 2, BigDecimal.ROUND_HALF_UP);
                }
            } else {
                if (DateUtil.getYear(lastExPeriodDate) == DateUtil.getYear(searchDate)) {

                    surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(lastExPeriodDate))).divide(new BigDecimal(DateUtil.getDayOfYear(searchDate)), 2, BigDecimal.ROUND_HALF_UP);
                }
            }
        }
        return surplusDays;
    }

    /**
     * 根据groupby 条件获取对象下的分类
     * 组合代码（可选项，多组合汇总时不添加）、查询日期、groupby 查询条件参数【searchCondition】
     *
     * @param
     * @return
     */
    public List<PtlPositionAnalysis003Dto> getFinpordType(String portfolioId, String searchDate, String searchCondition, String displayType, String type, String pass,Map<String, Object> param) {

        List<PtlPositionAnalysis003Dto> typelist = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isNotEmpty(portfolioId)) {
            map.put("portfolioId", portfolioId);
        }
        map.put("searchDate", searchDate);
        map.put("searchCondition", searchCondition);
        map.put("displayType", displayType);
        map.put("type", type);
        map.put("pass", pass);
        if(StringUtil.isNotEmpty(param.get("finprodMarketId"))){
            map.put("finprodMarketIdList", StringUtil.splitToList(null, param.get("finprodMarketId").toString()));
        }
        typelist = restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/getFinpordType",
                map, PtlPositionAnalysis003Dto.class);
        return typelist;
    }

    /**
     * 票面利率计算
     * 资产代码、查询日期、分支序号
     *
     * @param
     * @return
     */
    public BigDecimal calculationYield(String finprodId, String searchDate, Long branch) {
        BigDecimal yield = new BigDecimal(0);// 票面利率
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("effDate", searchDate);
        map.put("branch", branch);
        ResponseEntity<FinCashCalcRule> finCashCalcRuleResponseEntity = restTemplate.postForEntity(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getYieldByFinprodId",
                map, FinCashCalcRule.class);
        if (finCashCalcRuleResponseEntity != null && finCashCalcRuleResponseEntity.getBody() != null) {
            FinCashCalcRule finCashCalcRule = finCashCalcRuleResponseEntity.getBody();
            yield = finCashCalcRule.getYield();
        }
        return yield.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    //获取俩个时间相差的天数
    public long getDay(Date date1, Date date2) {
        // 获取相差的天数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        long timeInMillis1 = calendar.getTimeInMillis();
        calendar.setTime(date2);
        long timeInMillis2 = calendar.getTimeInMillis();
        long betweenDays = (timeInMillis2 - timeInMillis1) / (1000L * 3600L * 24L);
        return betweenDays;

    }

    /**
     * 下一行权日查询
     * 资产代码、查询日、金融产品类型、行权方式\含权摊销日
     *
     * @param
     * @return
     */
    public List<FinExercisePlan> calculationSurplusExercise(String finprodId, String seachDate, String finprodType, String execMode, String exDate) {
        String nextExDate = "";//下一行权日
        if (exDate.equals(DictCodeList.含权债摊销日_最近赎回行权日.getValue())) {
            execMode = DictCodeList.行权方式_赎回.getValue();
        }
        if (exDate.equals(DictCodeList.含权债摊销日_最近回售行权日.getValue())) {
            execMode = DictCodeList.行权方式_回售.getValue();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("exerciseDate", seachDate);
        map.put("finprodtype", finprodType);
        //   map.put("exerciseType",execMode);
        List<FinExercisePlan> finExercisePlanList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getExerciseDate",
                map, FinExercisePlan.class);

        return finExercisePlanList;

    }


    /**
     * 剩余金额的信息
     * @param map 是否穿透
     * @param map 查询条件日期
     * @return
     */
    public List<PtlPositionAnalysis003Dto> selectRemain(Map<String,Object> map ){
        List<PtlPositionAnalysis003Dto> typelist = simpleRestTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/selectRemain",
                map, PtlPositionAnalysis003Dto.class);
        return typelist;
    }

    /**
     * 单组合剩余现金处理
     * @param pageList 查询结果List
     * @param dzhMap 单组合剩余现金Map
     */
    public void onePortfolio(List<PtlPositionAnalysis003Dto> pageList,Map<String, BigDecimal> dzhMap,Map<String, BigDecimal> dzhLxMap){
        List<PtlPositionAnalysis003Dto> addList = new ArrayList<PtlPositionAnalysis003Dto>();
        if (dzhMap != null && dzhLxMap != null){
            pageList.forEach(item->{
                //当一级节点且有剩余现金时
                if (StringUtil.equals(item.getParentInfo(),"root") && dzhMap.containsKey(item.getPortfolioId())){
                    //一级节点总持仓增加剩余现金
                    item.setPosValue(item.getPosValue().add(dzhMap.get(item.getPortfolioId())));
                    item.setPosCost(item.getPosCost().add(dzhMap.get(item.getPortfolioId())));
                    item.setTdyIntincexp(item.getTdyIntincexp().add(dzhLxMap.get(item.getPortfolioId())));
                    PtlPositionAnalysis003Dto addDto = new PtlPositionAnalysis003Dto();
                    addDto.setParentInfo(item.getPortfolioId());
                    addDto.setPortfolioId("剩余金额");
                    addDto.setTypeName("剩余金额");
                    addDto.setHasChildren("01");
                    addDto.setChildInfo("SYJE");
                    addDto.setTdyIntincexp(dzhLxMap.get(item.getPortfolioId()));
                    addDto.setPosValue(dzhMap.get(item.getPortfolioId()));
                    addDto.setPosCost(dzhMap.get(item.getPortfolioId()));
                    addList.add(addDto);
                }
            });
            pageList.addAll(addList);
        }
    }


    /**
     * 多组合组合剩余现金处理
     * @param pageList 查询结果List
     * @param dzhCash 多组合汇总剩余现金
     */
    public void allPortfolio(List<PtlPositionAnalysis003Dto> pageList, BigDecimal dzhCash, BigDecimal dzhLx){
        if (dzhCash != null && dzhLx != null){
            List<PtlPositionAnalysis003Dto> addList = new ArrayList<PtlPositionAnalysis003Dto>();
            //生成汇总剩余现金子节点数据
            PtlPositionAnalysis003Dto addDto = new PtlPositionAnalysis003Dto();
            addDto.setParentInfo("root");
            addDto.setPortfolioId("剩余金额");
            addDto.setTypeName("剩余金额");
            addDto.setHasChildren("01");
            addDto.setChildInfo("SYJE");
            addDto.setPosValue(dzhCash);
            addDto.setPosCost(dzhCash);
            addDto.setTdyIntincexp(dzhLx);
            addList.add(addDto);
            pageList.addAll(addList);
        }
    }

}
