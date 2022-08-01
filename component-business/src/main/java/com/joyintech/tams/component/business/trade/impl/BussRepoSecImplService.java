/*
 * 文件名：TrdRepoTradeService003.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.component.business.trade.impl;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.EtrPledgeMapper;
import com.joyintech.tams.common.base.dao.FinDealPledgeMapper;
import com.joyintech.tams.common.base.entity.EtrPledge;
import com.joyintech.tams.common.base.entity.EtrSecPriority;
import com.joyintech.tams.common.base.entity.FinDealPledge;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.component.business.common.dao.CommonBusinessMapper;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 质押劵管理共通组件-实现Service
 */
@Service
public class BussRepoSecImplService implements BussRepoSecService {
    
    /** 日志 */
    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private JoyinRestTemplate restTemplate;

    @Autowired
    private EtrPledgeMapper etrPledgeMapper;

    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;

    /**
     * 代理类
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private CommonBusinessMapper commonBusinessMapper;

    /**
     * 债券代码：下拉框
     *
     * @param map 画面参数
     * @return 债券代码：下拉框
     */
    @Override
    public List<SelectOptionDto> getBondCode(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/getBondCode",
                map, SelectOptionDto.class);
    }

    /**
     * 通过entrustId/bizCode检索已质押券信息
     *
     * @param param 检索参数
     * @return List<BussRepoSecDto>
     */
    @Override
    public BussRepoSecDto selectBussRepoSec(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectBussRepoSec",
                param, BussRepoSecDto.class);
    }
    
    /**
     * 债券质押回购多组合新增
     * 张庚林
     * 20190723
     */
    @Override
    public PledgeBondInfoDto portfolioName(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/portfolioName",
                param, PledgeBondInfoDto.class);
    }
    
    /**
     * 根据画面条件检索可质押券信息
     *
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    @Override
    public List<PledgeBondInfoDto> selectAvaPledgeBondDetailList(Map<String, Object> params) {
        List<PledgeBondInfoDto> pledgeList = new ArrayList<PledgeBondInfoDto>();
        List<PledgeBondInfoDto> list = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectAvaPledgeBondDetailList",
            params, PledgeBondInfoDto.class);
        if(!StringUtil.isEmptyList(list)){
            try {
                // 获取质押列表数据，用于过滤可质押列表已经全额质押的债券 
                pledgeList = JsonUtils.json2List(String.valueOf(params.get("pledgeList")), PledgeBondInfoDto.class);
                if(!StringUtil.isEmptyList(pledgeList)){
                    for (int i = 0; i < pledgeList.size(); i++ ) {
                        PledgeBondInfoDto pledgeDto = pledgeList.get(i) ;
                        for (int j = 0; j < list.size(); j++ ) {
                            PledgeBondInfoDto avaPledgeDto = list.get(j) ;
                            if(StringUtil.isNotEmpty(avaPledgeDto.getAvaPledgePrin())){
                                if(StringUtil.equals(avaPledgeDto.getBondCode(), pledgeDto.getBondCode())
                                    &&avaPledgeDto.getAvaPledgePrin().compareTo(pledgeDto.getPledgePrin())==0){
                                    list.remove(avaPledgeDto) ;
                                }
                            }
                        }
                    }
                }
            }
            catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
        return list ;
    }
    
    /**
     * 根据画面条件检索可质押券信息
     *
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    @Override
    public Page<PledgeBondInfoDto> selectAvaPledgeBondDetailPage(Map<String, Object> params) {
        List<PledgeBondInfoDto> pledgeList = new ArrayList<PledgeBondInfoDto>();
        Page<PledgeBondInfoDto> list = restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectAvaPledgeBondDetailListPage",
            params, PledgeBondInfoDto.class);
        if(!StringUtil.isEmptyList(list.getRoot())){
            try {
                // 获取质押列表数据，用于过滤可质押列表已经全额质押的债券 
                pledgeList = JsonUtils.json2List(String.valueOf(params.get("pledgeList")), PledgeBondInfoDto.class);
                if(!StringUtil.isEmptyList(pledgeList)){
                    for (int i = 0; i < pledgeList.size(); i++ ) {
                        PledgeBondInfoDto pledgeDto = pledgeList.get(i) ;
                        for (int j = 0; j < list.getRoot().size(); j++ ) {
                            PledgeBondInfoDto avaPledgeDto = list.getRoot().get(j) ;
                            if(StringUtil.isNotEmpty(avaPledgeDto.getAvaPledgePrin())){
                                if(StringUtil.equals(avaPledgeDto.getBondCode(), pledgeDto.getBondCode())
                                    &&avaPledgeDto.getAvaPledgePrin().compareTo(pledgeDto.getAvaPledgePrin().multiply(new BigDecimal(10000)))==0){
                                    list.getRoot().remove(avaPledgeDto) ;
                                }
                            }
                        }
                    }
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(StringUtil.equals(params.get("isDzh"),"Y")&&!StringUtil.isEmptyList(list.getRoot())){
                //银行间上下行多组合  以万元为单位
                    list.getRoot().stream().forEach(avaPledgeDto -> {
                        avaPledgeDto.setPrin(ArithmeticUtil.divide(avaPledgeDto.getPrin(),new BigDecimal(10000),2));
                        avaPledgeDto.setPledgePrin(ArithmeticUtil.divide(avaPledgeDto.getPledgePrin(),new BigDecimal(10000),2));
                        avaPledgeDto.setAvaPledgePrin(ArithmeticUtil.divide(avaPledgeDto.getAvaPledgePrin(),new BigDecimal(10000),2));
                    });
            }
        }
        return list ;
    }

    /**
     * 7.6写入回购质押品信息
     *
     * @param bussRepoSec BussRepoSecDto
     * @param bizCode 委托代码/交易编号
     * @see
     * @since 1.0
     */
    @Override
    public int insert(BussRepoSecDto bussRepoSec, String bizCode) {

        if (bussRepoSec != null && !StringUtil.isEmptyList(bussRepoSec.getPledgeBondList())) {
            for (PledgeBondInfoDto pledgeBond : bussRepoSec.getPledgeBondList()) {
                // 回购委托新建
                if (PAGE_FROM_EtrRepo002.equals(bussRepoSec.getPageFrom())
                		||PAGE_FROM_EtrIbRepoinfo002.equals(bussRepoSec.getPageFrom())
                		||PAGE_FROM_EtribRepoInfo003.equals(bussRepoSec.getPageFrom())
                		) {
                    EtrPledge etrPledge = new EtrPledge();
                    // 委托代码 = 委托代码
                    etrPledge.setEntrustId(bizCode);
                    // 金融产品代码 = 输出质押品DTO.金融产品代码
                    etrPledge.setFinprodId(pledgeBond.getFinprodId());
                    // 质押面值 = 输出质押品DTO.质押面值
                    etrPledge.setPledgeFaceValue(new BigDecimal("100"));
                    // 质押数量 = 输出质押品DTO.质押数量
                    etrPledge.setPledgeNumber(ArithmeticUtil.divide(pledgeBond.getPledgePrin(), new BigDecimal("100"), 4));
                    // 质押率 = 输出质押品DTO.质押率
                    etrPledge.setPledgeRatio(ArithmeticUtil.divide(pledgeBond.getPledgeRatio(), new BigDecimal("100"), 4));
                    // 质押金额 = 输出质押品DTO.质押金额
                    etrPledge.setPledgeAmt(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), etrPledge.getPledgeRatio()));
                    etrPledgeMapper.insertSelective(etrPledge);
                }
                // 回购投资交易新建
                else if (PAGE_FROM_TrdRepoTrade002.equals(bussRepoSec.getPageFrom())||PAGE_FROM_TrdRepoTrade003.equals(bussRepoSec.getPageFrom())) {
                    FinDealPledge finDealPledge = new FinDealPledge();
                    // 金融产品代码 = 金融产品交易Entity.交易编号
                    finDealPledge.setFinprodId(bizCode);
                    // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
                    finDealPledge.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                    // 质押金融产品代码 = 输出质押品DTO.金融产品代码
                    if(StringUtils.isNoneBlank(pledgeBond.getFinprodId())){
                        finDealPledge.setPledgeFinprodId(pledgeBond.getFinprodId());
                    }else{
                        finDealPledge.setPledgeFinprodId(DictCodeList.平安资产类型_债券.getValue()+pledgeBond.getBondCode());
                    }
                    // 质押面值 = 输出质押品DTO.质押面值
                    finDealPledge.setPledgeFaceValue(new BigDecimal("100"));
                    // 质押数量 = 输出质押品DTO.质押数量
                    finDealPledge.setPledgeNumber(ArithmeticUtil.divide(pledgeBond.getPledgePrin(), new BigDecimal("100"), 4));
                    // 质押率 = 输出质押品DTO.质押率
                    finDealPledge.setPledgeRatio(ArithmeticUtil.divide(pledgeBond.getPledgeRatio(), new BigDecimal("100"), 4));
                    // 质押金额 = 输出质押品DTO.质押金额
                    finDealPledge.setPledgeAmt(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), finDealPledge.getPledgeRatio()));
                    finDealPledgeMapper.insertSelective(finDealPledge);
                }
            }
            return bussRepoSec.getPledgeBondList().size();
        }
        return 0;
    }

    /**
     * 撤销委托/回购交易时，删除回购质押品信息
     *
     * @param bizCode  委托代码/交易编号
     * @param pageFrom pageFrom
     * @return successCount
     * @see
     * @since 1.0
     */
    @Override
    public int deletePledgeBond(String bizCode, String pageFrom) {
        int successCount = 0;
        Map<String, Object> params = new HashMap<>();
        params.put("bizCode", bizCode);
        params.put("pageFrom", pageFrom);
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/deletePledgeBond",
                params, Integer.class);
    }

    /**
     * 根据组合代码查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
	@Override
	public List<SuggestDto> getSecManageAcctId(Map<String, Object> param) {
		return restTemplate.postForList(
	            ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getSecManageAcctId",
	            param, SuggestDto.class);
	}
	
	/**
     * 自动导入质押券
     *
     * @param param 检索参数
     * @return List<BussRepoSecDto>
     */
	@Autowired
    private BusiProductRatingService busiProductRatingService;
	
    @Override
    public List<PledgeBondInfoDto> selectAutoPledge(Map<String, String> param) {
        List<PledgeBondInfoDto> dataList = new ArrayList<PledgeBondInfoDto>() ;
        // 查询可质押债券信息
        // 设置证券账户/通道 代码
        String chlId = param.get("chlId") ;// 通道
        String securitiesCust = param.get("securitiesCust") ;// 证券账户
        param.put("secAcctId", StringUtil.isEmpty(chlId)?securitiesCust:chlId) ;
        List<PledgeBondInfoDto> pledgeList =  restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStandardBond/selectAutoPledge",
                param, PledgeBondInfoDto.class);
        if (pledgeList != null && pledgeList.size() > 0) {
            Map<String, Object> zxMap = new HashMap<String, Object>();
            for (PledgeBondInfoDto pledgeBondInfoDto : pledgeList) {
                // 通过市场代码，查询最优先的债项评级
                String gradeResult = "01" ;// 若无债项评级，默认为最高级
                zxMap.put("oCode", pledgeBondInfoDto.getBondCode()) ;
                List<BusiFinProductRatingDto> busiList = busiProductRatingService.getZxBestRatingByMarketId(zxMap);
                if(busiList.size() > 0){
                    gradeResult = busiList.get(0).getGradeResult() ;// 评级结果
                }
                pledgeBondInfoDto.setGradeResult(gradeResult);
            }
        }
        // 查询质押优先级设置数据，不为空且是否启用为Y，进行排序。排序字段不能为空，需统一处理
        EtrSecPriority etrSecPriority =  restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStandardBond/selectEtrSecPriDetailList",
            param, EtrSecPriority.class);
        if(StringUtil.isNotEmpty(etrSecPriority) 
            && StringUtil.equals(DictCodeList.是否_是.getValue(), etrSecPriority.getYesOrNo())){
            if(StringUtil.equals(etrSecPriority.getLastPeriodPri(),DictCodeListExt3.剩余期限_剩余期限短的优先质押.getValue())
                && StringUtil.equals(etrSecPriority.getSecGradePri(),DictCodeListExt3.债项评级_债项评级低的优先质押.getValue())){
//              if 变量.剩余期限优先级= 数据字典.剩余期限优先级.剩余期限短的优先质押 && 变量.债项评级优先级= 数据字典.债项评级优先级.债项评级低的优先质押                                                                                             
//              将上述结果按照质押优先级降序、剩余期限升序、债券评级降序、可质押券面总额降序排列 
                pledgeList.sort(Comparator.comparing(PledgeBondInfoDto::getPledgePri)
                    .thenComparing(PledgeBondInfoDto::getLastPeriod).reversed()
                    .thenComparing(PledgeBondInfoDto::getGradeResult).reversed()
                    .thenComparing(PledgeBondInfoDto::getAvaPledgePrin).reversed());
                
            }else if(StringUtil.equals(etrSecPriority.getLastPeriodPri(),DictCodeListExt3.剩余期限_剩余期限短的优先质押.getValue())
                && StringUtil.equals(etrSecPriority.getSecGradePri(),DictCodeListExt3.债项评级_债项评级高的优先质押.getValue())){
//              if 变量.剩余期限优先级= 数据字典.剩余期限优先级.剩余期限短的优先质押  && 变量.债项评级优先级= 数据字典.债项评级优先级.债项评级高的优先质押                                                                                             
//              将上述结果按照质押优先级降序、剩余期限升序、债券评级升序、可质押券面总额降序排列         
                pledgeList.sort(Comparator.comparing(PledgeBondInfoDto::getPledgePri)
                    .thenComparing(PledgeBondInfoDto::getLastPeriod).reversed()
                    .thenComparing(PledgeBondInfoDto::getGradeResult)
                    .thenComparing(PledgeBondInfoDto::getAvaPledgePrin).reversed());
                
            }else if(StringUtil.equals(etrSecPriority.getLastPeriodPri(),DictCodeListExt3.剩余期限_剩余期限长的优先质押.getValue())
                && StringUtil.equals(etrSecPriority.getSecGradePri(),DictCodeListExt3.债项评级_债项评级高的优先质押.getValue())){
//              if 变量.剩余期限优先级= 数据字典.剩余期限优先级.剩余期限长的优先质押 && 变量.债项评级优先级= 数据字典.债项评级优先级.债项评级高的优先质押                                                                                             
//              将上述结果按照质押优先级降序、剩余期限降序、债券评级升序、可质押券面总额降序排列              
                pledgeList.sort(Comparator.comparing(PledgeBondInfoDto::getPledgePri)
                    .thenComparing(PledgeBondInfoDto::getLastPeriod)
                    .thenComparing(PledgeBondInfoDto::getGradeResult)
                    .thenComparing(PledgeBondInfoDto::getAvaPledgePrin).reversed());
                
            }else if(StringUtil.equals(etrSecPriority.getLastPeriodPri(),DictCodeListExt3.剩余期限_剩余期限长的优先质押.getValue())
                && StringUtil.equals(etrSecPriority.getSecGradePri(),DictCodeListExt3.债项评级_债项评级低的优先质押.getValue())){
//              if 变量.剩余期限优先级= 数据字典.剩余期限优先级.剩余期限长的优先质押  && 变量.债项评级优先级= 数据字典.债项评级优先级.债项评级低的优先质押                                                                                             
//              将上述结果按照质押优先级降序、剩余期限降序、债券评级降序、可质押券面总额降序排列    
                pledgeList.sort(Comparator.comparing(PledgeBondInfoDto::getPledgePri)
                    .thenComparing(PledgeBondInfoDto::getLastPeriod)
                    .thenComparing(PledgeBondInfoDto::getGradeResult).reversed()
                    .thenComparing(PledgeBondInfoDto::getAvaPledgePrin).reversed());
            } else if(StringUtil.isEmpty(etrSecPriority.getLastPeriodPri())&& StringUtil.isEmpty(etrSecPriority.getSecGradePri()) ) {
                    pledgeList.sort(Comparator.comparing(PledgeBondInfoDto::getPledgePri).reversed()
                            .thenComparing(PledgeBondInfoDto::getAvaPledgePrin).reversed());
            }
            // 输出排序结果，用于测试排序
            pledgeList.stream().forEach(dto -> log.info(dto.getFinprodId()+"===" 
                + dto.getPledgePri()+"==="+dto.getLastPeriod()+"==="+dto.getGradeResult()+"==="+dto.getAvaPledgePrin()));
            
        }
        BigDecimal entrustAmt = new BigDecimal(param.get("entrustAmt")) ; // 委托金额
        BigDecimal pledgAmt = BigDecimal.ZERO ;// 累计质押金额
        for (PledgeBondInfoDto pledgeBondInfoDto : pledgeList) {
            pledgAmt = pledgAmt.add(pledgeBondInfoDto.getAvaPledgePrin()) ;
            dataList.add(pledgeBondInfoDto) ;
            if(pledgAmt.compareTo(entrustAmt) > -1 ){ // 累计质押金额大于等于 委托金额时，终止循环
                // 计算所需的部分质押额，使pledgAmt = entrustAmt
                BigDecimal pledgePrin = entrustAmt.add(pledgeBondInfoDto.getAvaPledgePrin()).subtract(pledgAmt) ;
                pledgeBondInfoDto.setPledgePrin(pledgePrin);
                break ;
            }
        }
        
        return dataList ;
    }
    
    @Override
    public int deleteAndInsert(String bizCode, String pageFrom, BussRepoSecDto bussRepoSec) {
        deletePledgeBond(bizCode, pageFrom);
        return insert(bussRepoSec, bizCode);
    }

    @Override
    public String querySecAcctId(Map<String, Object> params) {
        return this.commonBusinessMapper.querySecAcctId(params);
    }
}
