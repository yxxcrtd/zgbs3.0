/*
 * 文件名：BatchCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年7月13日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.common.base.entity.TrsSubjectRelation;
import com.joyintech.tams.valuation.common.batch.bookcommon.service.BookCommonBatchService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.SubjectBalance;
import com.joyintech.tams.valuation.common.batch.finproduct.service.AccountingCommonBatchService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.dao.DataTransplantCommonMapper;
import com.joyintech.tams.valuation.common.dao.SubjectBalanceCollectCommonMapper;
import com.joyintech.tams.valuation.common.interf.base.dto.BalanceTransplantDto;

/**
 * 数据移植共通方法 〈功能详细描述〉
 * 
 * @author chengzhong
 * @version 1.0 2018年11月08日
 * @see DataTransplantCommonService
 * @since 1.0
 */
@Service
public class DataTransplantCommonService {
    private static final Logger logger = LoggerFactory.getLogger(DataTransplantCommonService.class);
    @Autowired
    private DataTransplantCommonMapper dataTransplantCommonMapper;

    /**
     * 科目余额汇总Mapper
     */
    @Autowired
    private SubjectBalanceCollectCommonMapper subjectBalanceCollectMapper;

    /**
     * 批处理账务相关公用方法
     */
    @Autowired
    private AccountingCommonBatchService accountingCommonBatchService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonBatchService bookCommonBatchService;

    /**
     * round(amt/(1+rate),2)*rate
     */
    public static final String TAX_CALC_FORMULA_2 = "FORMULA_2";

    /**
     * amt/(1+rate)*rate
     */
    public static final String TAX_CALC_FORMULA_1 = "FORMULA_1";

    /**
     * 获取账务明细代码
     * 
     * @param busiDetailType 业务明细类型
     * @param busiId 业务明细代码
     * @param branchNo 分支序号
     * @param invAim 投资目的
     * @return 账务明细代码
     * @see
     * @since 1.0
     */
    public String getBookDetailId(String busiDetailType, String busiId, Long branchNo,
                                  String invAim,String ccy) {

        // 账务明细信息
        BokDetailId bokDetailIdVo = null;

        // 分情况获取
        if (DictCodeListExt1.金融产品类型_债券.getValue().equals(busiDetailType)) {
            // 组装账务明细(资产类型+投资目的+资产代码)
            bokDetailIdVo = bookCommonBatchService.getValBokDetailIdWithInvAim(busiId, null,
                busiDetailType, invAim);

        }
        else if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(busiDetailType)) {
            // 组装账务明细(资产类型+分支序号+资产代码)
            bokDetailIdVo = bookCommonBatchService.getValBokDetailIdWithBranchNo(busiId, null,
                busiDetailType, branchNo);

        }
        else {
            // 组装账务明细(资产类型+资产代码)
            bokDetailIdVo = bookCommonBatchService.getValBokDetailId(busiId, null, busiDetailType,ccy);
        }

        // 账务明细代码
        String bookDetailid = null;
        if (bokDetailIdVo != null) {
            bookDetailid = bokDetailIdVo.getBokDetailId();
        }

        return bookDetailid;
    }

    /**
     * 获取科目信息
     * 
     * @param bookSetId 账套代码,必输项
     * @param subjectNo 科目号,必输项
     * @return BokSubject 账务明细信息
     * @see
     * @since 1.0
     */
    public BokSubject findSubjectInfo(String bookSetId, String subjectNo) {
        return dataTransplantCommonMapper.findSubjectInfo(bookSetId, subjectNo);
    }

    /**
     * 科目余额叶子节点向上汇总处理
     * 
     * @param booksetId 账套代码
     * @param bookDate 入账日期
     * @param listBalancesForYest 昨日余额List
     * @param listBalancesForNow 当日余额List
     * @return 余额明细 LIST
     * @see
     * @since 1.0
     */
    public List<SubjectBalance> subjectBalanceCollect(String booksetId, Date bookDate,
                                                      List<SubjectBalance> listBalancesForYest,
                                                      List<SubjectBalance> listBalancesForNow) {
        // 1.昨日余额信息处理
        // 1.1 查询账套科目List
        List<BokSubject> listSubjects = subjectBalanceCollectMapper.querySubject(booksetId);
        // 循环 昨日余额 ： 昨日余额List
        for (SubjectBalance subjectBalance : listBalancesForYest) {
            // 循环 账套科目 ： 账套科目List
            for (BokSubject bokSubject : listSubjects) {
                // if A.科目级别="99" && 昨日余额.上级科目号=账套科目.科目号
                if (BookConstant.MAX_SUBJECT_LEVEL.equals(subjectBalance.getSubjectLevel())
                    && StringUtils.equals(subjectBalance.getFsubjectId(),
                        bokSubject.getSubjectNo())) {
                    // 昨日余额.科目性质 = 账套科目.科目性质
                    subjectBalance.setBaseSubjectNature(bokSubject.getBaseSubjectNature());
                    break;
                }
                // if A.科目级别 ！= "99" && 昨日余额.科目号=账套科目.科目号
                if (!BookConstant.MAX_SUBJECT_LEVEL.equals(subjectBalance.getSubjectLevel())
                    && StringUtils.equals(subjectBalance.getSubjectNo(),
                        bokSubject.getSubjectNo())) {
                    // 昨日余额.科目性质 = 账套科目.科目性质
                    subjectBalance.setBaseSubjectNature(bokSubject.getBaseSubjectNature());
                    break;
                }
            }
        }
        // 1.2 昨日余额Map数据封装
        // 将上述昨日余额信息的结果组装成 【变量.科目余额MAP】，KEY:科目号 + 币种，VALUE 对应科目号查询DTO
        Map<String, SubjectBalance> balancesMap = Maps.newHashMap();
        listBalancesForYest.forEach(item -> {
            balancesMap.put(item.getSubjectNo() + item.getOCcy(), item);
        });

        // 2. 科目号组装
        Map<String, BokSubject> subjectsMap = Maps.newHashMap();
        listSubjects.forEach(item -> {
            subjectsMap.put(item.getSubjectNo(), item);
        });

        // 4. 三级科目余额的汇总
        // 调用【科目余额汇总处理】方法，参照【详细设计-账务类共通方法设计.xlxs的【科目余额汇总处理】sheet】
        for (SubjectBalance item : listBalancesForNow) {
            // 变量.余额明细.场景代码 = ''(空字符串)
            item.setTableId(StringUtils.EMPTY);
            accountingCommonBatchService.collectSubjectBalance(item, item.getSubjectNo(),
                item.getSubjectLevel(), balancesMap, subjectsMap, DictCodeList.是否_是.getValue());
        }

        // 重新定义 余额明细 LIST
        List<SubjectBalance> listBalances = Lists.newArrayList();
        // 将科目余额Map转换为List
        for (String key : balancesMap.keySet()) {
        	SubjectBalance bal = new SubjectBalance();
        	BeanUtil.copyPropertiesIgnoreNull(bal, balancesMap.get(key));
            listBalances.add(bal);
        }
        listBalances.stream().filter(item -> item.getBookDate() == null).forEach(
            item -> item.setBookDate(bookDate));

        return listBalances;
    }

    /**
     * 四级科目信息生成<一句话功能简述> <功能详细描述>
     * 
     * @param booksetId
     * @param busiId
     * @param busiDetailType
     * @param branch
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, String> createDetailSubjectNoInfo(String booksetId, String busiId,
                                                         String busiDetailType, Long branch) {

        // 定义变量
        // 定义:变量-四级科目号 = null;
        String setailSubjectNo = null;
        // 定义:变量-四级科目号名称 = null;
        String setailSubjectName = null;
        // 定义:变量-四级科目尾号 = null;
        String detailSubjectNoTail = null;
        // 定义:变量-四级科目尾号名称 = null;
        String detailSubnameTail = null;

        // 按类型进行逻辑处理
        if (DictCodeListExt1.金融产品类型_债券.getValue().equals(busiDetailType)
            || DictCodeListExt1.金融产品类型_基金.getValue().equals(busiDetailType)
            || DictCodeListExt1.金融产品类型_存款.getValue().equals(busiDetailType)
            || DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(busiDetailType)
            || DictCodeListExt1.金融产品类型_外包服务费.getValue().equals(busiDetailType)
            || DictCodeListExt1.金融产品类型_代销费.getValue().equals(busiDetailType)
            || DictCodeListExt1.金融产品类型_交易费用.getValue().equals(busiDetailType)
            || DictCodeListExt1.金融产品类型_债券借贷融入.getValue().equals(busiDetailType)
            || DictCodeListExt1.金融产品类型_债券借贷融出.getValue().equals(busiDetailType)) {
            // 获取金融产品/投资组合对应的名称
            String finprodName = dataTransplantCommonMapper.findFinprodName(busiId,
                busiDetailType);
            // 变量-四级科目号 = 参数-业务明细代码;
            setailSubjectNo = busiId;
            // 变量-四级科目号名称 = 变量-金融产品简称;
            setailSubjectName = finprodName;
            // 变量-四级科目尾号 = 变量-四级科目号;
            detailSubjectNoTail = setailSubjectNo;
            // 变量-四级科目尾号名称 = 变量-四级科目号名称;
            detailSubnameTail = setailSubjectName;
        }
        else if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(busiDetailType)) {
            // 获取期限品种
            String termType = dataTransplantCommonMapper.findTermType(busiId, busiDetailType);
            // 期限品种汉字
            String termTypeName = StringUtils.isEmpty(
                termType) ? null : SysDataUtil.getNameFromDataDict(DictCode.期限品种_回购, termType);
            // 变量-四级科目号 = 变量-期限品种;
            setailSubjectNo = termType;
            // 变量-四级科目号名称 = 变量-期限品种汉字;
            setailSubjectName = termTypeName;
            // 变量-四级科目尾号 = 变量-四级科目号;
            detailSubjectNoTail = setailSubjectNo;
            // 变量-四级科目尾号名称 = 变量-四级科目号名称;
            detailSubnameTail = setailSubjectName;
        }
        else if (DictCodeListExt1.金融产品类型_拆入.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_拆出.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_同业借款.getValue().equals(busiDetailType)) {
            // 获取期限品种
            String termType = dataTransplantCommonMapper.findTermType(busiId, busiDetailType);
            // 期限品种汉字
            String termTypeName = StringUtils.isEmpty(
                termType) ? null : SysDataUtil.getNameFromDataDict(DictCode.期限品种_内部拆借, termType);
            // 变量-四级科目号 = 变量-期限品种;
            setailSubjectNo = termType;
            // 变量-四级科目号名称 = 变量-期限品种汉字;
            setailSubjectName = termTypeName;
            // 变量-四级科目尾号 = NULL;
            detailSubjectNoTail = null;
            // 变量-四级科目尾号名称 = NULL;
            detailSubnameTail = null;
        }
        else if (DictCodeListExt1.金融产品类型_理财产品.getValue().equals(busiDetailType)) {
            // 根据分层代码获取对应的四级科目号名称
            String termTypeName = dataTransplantCommonMapper.findDetailSubnameTail(booksetId,
                busiId, busiDetailType);
            // 变量-四级科目号 = 参数-业务明细代码;
            setailSubjectNo = busiId + GlobalConstant.UNDER_LINE + busiDetailType;
            // 变量-四级科目号名称 = 【调用sql返回数据：SQL3】.四级科目号名称;
            setailSubjectName = termTypeName;
            // 变量-四级科目尾号 = 变量-四级科目号;
            detailSubjectNoTail = setailSubjectNo;
            // 变量-四级科目尾号名称 = 变量-四级科目号名称;
            detailSubnameTail = setailSubjectName;
        }
        else if (DictCodeListExt1.金融产品类型_投资组合.getValue().equals(busiDetailType)) {
            // 根据分层代码获取对应的四级科目号名称
            String termTypeName = dataTransplantCommonMapper.findDetailSubnameTailBySubject(
                booksetId, busiId, busiDetailType);
            // 变量-四级科目号 = 参数-业务明细代码;
            setailSubjectNo = busiId + GlobalConstant.UNDER_LINE + busiDetailType;
            // 变量-四级科目号名称 = 【调用sql返回数据：SQL4】.四级科目号名称;
            setailSubjectName = termTypeName;
            // 变量-四级科目尾号 = 变量-四级科目号;
            detailSubjectNoTail = setailSubjectNo;
            // 变量-四级科目尾号名称 = 变量-四级科目号名称;
            detailSubnameTail = setailSubjectName;
        }
        else if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(busiDetailType)) {
            // 获取金融产品/投资组合对应的名称
            String finprodName = dataTransplantCommonMapper.findFinChildprodName(busiId,
                busiDetailType, branch);
            // 变量-四级科目号 = 参数-业务明细代码 + 参数-分支序号;
            setailSubjectNo = busiId + branch + GlobalConstant.UNDER_LINE + busiDetailType;
            // 变量-四级科目号名称 = 变量-金融产品简称 + 参数-分支序号;
            setailSubjectName = finprodName;
            // 变量-四级科目尾号 = 变量-四级科目号;
            detailSubjectNoTail = setailSubjectNo;
            // 变量-四级科目尾号名称 = 变量-四级科目号名称;
            detailSubnameTail = setailSubjectName;
        }
        else if (DictCodeListExt1.金融产品类型_固定管理费.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_浮动管理费.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_托管费.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_销售费.getValue().equals(busiDetailType)
                 || DictCodeListExt1.金融产品类型_税额.getValue().equals(busiDetailType)) {
            // 获取金融产品/投资组合对应的名称
            String finprodName = dataTransplantCommonMapper.findFinprodName(busiId,
                busiDetailType);
            // 变量-四级科目号 = 参数-业务明细代码;
            setailSubjectNo = busiId + GlobalConstant.UNDER_LINE + busiDetailType;
            // 变量-四级科目号名称 = 变量-金融产品简称;
            setailSubjectName = finprodName;

            // 变量-所挂产品类型 = 根据产品代码，获取产品类型
            String finprodType = dataTransplantCommonMapper.findFinprodType(busiId);
            // 变量-分层代码 = 获取对应的分层代码
//            String layeringId = dataTransplantCommonMapper.findLayeringId(booksetId, busiId,
//            		finprodType);

            // 不同产品类型，进行不同处理
            if (DictCodeListExt1.金融产品类型_理财产品.getValue().equals(finprodType)) {
                // 变量-四级科目尾号 = 参数-业务明细代码;
                detailSubjectNoTail = busiId + GlobalConstant.UNDER_LINE + finprodType;
                // 变量-四级科目尾号名称 = 根据分层代码获取对应的四级科目号名称
                detailSubnameTail = dataTransplantCommonMapper.findDetailSubnameTail(booksetId,
                		busiId, finprodType);
            }
            else if (DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)) {
                // 变量-四级科目尾号 = 参数-业务明细代码;
                detailSubjectNoTail = busiId + GlobalConstant.UNDER_LINE + finprodType;
                // 变量-四级科目尾号名称 = 根据分层代码获取对应的四级科目号名称
                detailSubnameTail = dataTransplantCommonMapper.findDetailSubnameTailBySubject(
                    booksetId, busiId, finprodType);
            }
            else if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)) {
                // 变量-四级科目尾号 = 参数-业务代码 + 参数-分支序号;
                detailSubjectNoTail = busiId + branch + GlobalConstant.UNDER_LINE + finprodType;
                // 变量-四级科目尾号名称 = 变量-金融产品简称 + 参数-分支序号;
                // 获取金融产品/投资组合对应的名称
                String layeringName = dataTransplantCommonMapper.findFinChildprodName(busiId,
                    finprodType, branch);
                detailSubnameTail = layeringName;
            }
            else {
                detailSubjectNoTail = null;
                detailSubnameTail = null;
            }
        }

        // 定义返回参数
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("DETAIL_SUBJECT_NO", setailSubjectNo);
        resultMap.put("DETAIL_SUBJECT_NAME", setailSubjectName);
        resultMap.put("DETAIL_SUBJECT_NO_TAIL", detailSubjectNoTail);
        resultMap.put("DETAIL_SUBNAME_TAIL", detailSubnameTail);

        return resultMap;
    }

    /**
     * 增值税计算共通
     * 
     * @param allIncome 含税收入
     * @param taxRate 税率
     * @param scale 小数位数
     * @param roundingMode 尾数处理方式
     * @param taxCalcFormula
     * @see
     * @since 1.0
     */
    public BigDecimal taxCalculate(BigDecimal allIncome, BigDecimal taxRate, int scale,
                                   RoundingMode roundingMode, String taxCalcFormula) {
        BigDecimal amount = BigDecimal.ZERO;
        if (allIncome != null && taxRate != null) {
            if (TAX_CALC_FORMULA_1.equals(taxCalcFormula)) {
                // 返回值 = 发生含税销售 * 税率 /(1+税率)
                amount = ArithmeticUtil.divide(ArithmeticUtil.multiply(allIncome, taxRate),
                    ArithmeticUtil.doAdd(scale, roundingMode, taxRate, new BigDecimal(1)), scale);
            }
            else {
                amount = ArithmeticUtil.multiply(
                    ArithmeticUtil.divide(allIncome,
                        ArithmeticUtil.doAdd(scale, roundingMode, taxRate, new BigDecimal(1)), 2),
                    taxRate).setScale(scale, roundingMode);
            }
        }
        return amount;
    }
    
    /**
     * 查询今日已入库的子科目余额信息
     * add by wujie on 20181210
     * @param booksetId
     * @param queryDate
     * @return
     */
    public List<SubjectBalance> queryTdayBalanceAready(String booksetId,Date queryDate){
    	return subjectBalanceCollectMapper.queryTdayBalanceAready(queryDate, booksetId);
    }

    /**
     * 判断账套表中是否存在该账套
     * 
     * @param booksetId
     * @return
     * @see
     * @since 1.0
     */
    public boolean checkBooksetId(String booksetId) {
        return dataTransplantCommonMapper.checkBooksetId(booksetId);
    }

    /**
     * 判断核算分层中是否存在该分层
     * 
     * @param layeringId
     * @return
     * @see
     * @since 1.0
     */
    public boolean checkLayering(@Param("layeringId") String layeringId) {
        return dataTransplantCommonMapper.checkLayering(layeringId);
    }

    /**
     * 判断对应的标的是否存在
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public boolean checkUnderlying(Map<String, Object> map) {
        return dataTransplantCommonMapper.checkUnderlying(map);
    }
    

    /**
     * 判断是否是成本类科目
     * 
     * @param subjectNo
     * @return
     * @see
     * @since 1.0
     */
    public boolean checkSubjectNature(@Param("subjectNo")String subjectNo){
    	return dataTransplantCommonMapper.checkSubjectNature(subjectNo);
    }
    
    /**
     *查询全量数据移植科目对应关系
     *add by wujie on 20181220
     */
    public List<TrsSubjectRelation> getTrsSubjectRelationAll(){
    	return dataTransplantCommonMapper.getTrsSubjectRelationAll();
    }
    
    /**
     * 转换后数据导入
     * add by wujie on 20181228
     */
    public void insertTrsBalanceTransplant(List<BalanceTransplantDto> balanceList){
//    	System.out.println("转换后数据大小："+balanceList.size());
    	logger.info("转换后数据大小："+balanceList.size());
        // 入库
        if (CollectionUtils.isNotEmpty(balanceList)) {
            // 定义一个固定大小插入存放集合
            List<BalanceTransplantDto> listInsert = new ArrayList<>();
            for (BalanceTransplantDto e : balanceList) {
                // 小于最大值
                if (listInsert.size() < 500) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= 500) {
                	dataTransplantCommonMapper.insertTrsBalanceTransplant(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
            	dataTransplantCommonMapper.insertTrsBalanceTransplant(listInsert);
            }
        }
    }
    
    public List<BalanceTransplantDto> queryBalanceTransplantList(String batchSeqno){
    	return dataTransplantCommonMapper.queryBalanceTransplantList(batchSeqno);
    }
}
