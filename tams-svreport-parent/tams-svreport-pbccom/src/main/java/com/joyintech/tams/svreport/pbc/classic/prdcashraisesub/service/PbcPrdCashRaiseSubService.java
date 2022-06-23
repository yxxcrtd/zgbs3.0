package com.joyintech.tams.svreport.pbc.classic.prdcashraisesub.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.svreport.base.service.ChbTypeMappingService;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.DeadlineUtil;
import com.joyintech.tams.report.interf.common.constant.DateUtil;
import com.joyintech.tams.svreport.pbc.classic.prdcashraisesub.dao.PbcProdRaiseSubMapper;
import com.joyintech.tams.svreport.pbc.classic.prdcashraisesub.dto.PbcProdRaiseSub;
import com.joyintech.tams.svreport.pbc.common.service.PbcClassicsSubService;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
 * 功能:人行单产品报表-产品资金募集信息
 * @since 1.7.0
 * @version 1.7.0
 * @作者：weiyuqiang
 * @创建时间：2019年6月11日
 * @修改记录：
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PbcPrdCashRaiseSubService extends PbcClassicsSubService<PbcProdRaiseSub>{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PbcProdRaiseSubMapper pbcProdRaiseSubMapper;
	
	@Autowired
    private ChbTypeMappingService chbTypeMappingService;
	
	@Autowired
	private SqlSessionTemplate template;
	
	/**
     * 数据批量一次性insert条数(单条数据字段较多)
     */
    @Value("${application.reportInsertLongCount}")
    private int reportInsertLongCount;

    /**
     * 数据批量一次查询或者删除条数
     */
    @Value("${application.reportSelectOrDeleteCount}")
    private int reportSelectOrDeleteCount;
	
    /**
     * 功能:人行单产品报表-产品资金募集信息批处理入口
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年6月12日
     * @param executeDate
     * @param prodIdList 
     * @see com.joyintech.tams.svreport.pbc.common.service.PbcClassicsSubService#genReport(java.util.Date, java.util.List)
     */
	@Override
	public void genReport(Date executeDate, List<String> prodIdList) {
//		// 每个月的一号生成报表
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(executeDate);
//        // 每月1日生成上月的数据
//        // 如果走修改消息池进入则自动进入批处理
//        if ( calendar.get(Calendar.DATE) != 1 ) {  
//            return;
//        }
        genRegistReport(executeDate,prodIdList);
	}
	
	/**
	 * 功能:人行单产品报表-产品资金募集信息批处理
	 * @since 1.7.0
	 * @version 1.7.0
	 * @author：weiyuqiang 2019年6月12日
	 * @param executeDate 批处理日期
	 * @param prodIdList 产品list
	 * void
	 */
	public void genRegistReport(Date executeDate, List<String> prodIdList){
		// 根据月末日期查询产品头寸
		List<PbcProdRaiseSub> prdCashRaiseList = new ArrayList<>();
		String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE);
        // 获取系统参数：非净值型产品赎回交易取值
        String pbcProdRaising = SvrDataUtil.getSysParam(SVReportGlobalConstant.PBC_PROD_RAISING);
        // 分批处理数据,100个产品查询一次
        if (!StringUtil.isEmptyList(prodIdList)){
	        for (int i = 0;i < prodIdList.size();){
	        	int end = i + 100;
	        	if (end > prodIdList.size()){
	        		end = prodIdList.size();
	        	}
	        	prdCashRaiseList.addAll(pbcProdRaiseSubMapper.selectPrdCashRaise(bankCode,DateUtil.getLastDayOfLayMonth(executeDate),prodIdList.subList(i, end),pbcProdRaising));
	            i = end;
	        }
        } else {
        	prdCashRaiseList.addAll(pbcProdRaiseSubMapper.selectPrdCashRaise(bankCode,DateUtil.getLastDayOfLayMonth(executeDate),null,pbcProdRaising));
        }
        // 查询已出的登记数据
        List<PbcProdRaiseSub> prdCashRaiseAllList = pbcProdRaiseSubMapper.selectPrdCashRaiseRegist(DateUtil.getLastDayOfLayMonth(executeDate));
        // 已登记数据
        List<PbcProdRaiseSub> prdCashRaiseRegList = prdCashRaiseAllList.stream().filter(item->{
            return !DictCodeListExt2.报送状态_未报送.getValue().equals(item.getSendStatus());
        }).collect(Collectors.toList());
        // 未登记数据
        List<PbcProdRaiseSub> prdCashRaiseNoRegList = prdCashRaiseAllList.stream().filter(item->{
            return DictCodeListExt2.报送状态_未报送.getValue().equals(item.getSendStatus());
        }).collect(Collectors.toList());
        // 根据传入产品list判断是按照指定产品出报表还是全部产品出
        if (!StringUtil.isEmptyList(prodIdList)){      
			// 单产品出报表
			for (String prodid : prodIdList){
				// 按产品分头寸
				List<PbcProdRaiseSub> posProdList = prdCashRaiseList.stream().filter(item->{
					return prodid.equals(item.getProdId());
				}).collect(Collectors.toList());
                List<PbcProdRaiseSub> prdCashRaiseRegistList = prdCashRaiseRegList.stream().filter(item->{
                    return prodid.equals(item.getProdId());
                }).collect(Collectors.toList());
                List<PbcProdRaiseSub> prdCashRaiseNoRegistList = prdCashRaiseNoRegList.stream().filter(item->{
                    return prodid.equals(item.getProdId());
                }).collect(Collectors.toList());
	            Map<String,List<PbcProdRaiseSub>> posProdAllMap = posProdList.stream().collect(
	                    Collectors.groupingBy(PbcProdRaiseSub::getPbcProdidAndOrgCode,Collectors.toList()));
                Map<String,List<PbcProdRaiseSub>> posProdRegAllMap = prdCashRaiseRegistList.stream().collect(
                        Collectors.groupingBy(PbcProdRaiseSub::getPbcProdidAndOrgCode,Collectors.toList()));
                Map<String,List<PbcProdRaiseSub>> posProdRegNoAllMap = prdCashRaiseNoRegistList.stream().collect(
                        Collectors.groupingBy(PbcProdRaiseSub::getPbcProdidAndOrgCode,Collectors.toList()));
                // 记录已经统计过得单产品(带机构)
                Map<String,String> prodMap = new HashMap<>();
	            // 根据查询出的数据分组进行出单产品报表
	            for (String key : posProdAllMap.keySet()){
                    if (!prodMap.containsKey(key)){
                        prodMap.put(key, key);
                    } else {
                        // 已经出过报表则跳过
                        continue;
                    }
	                List<PbcProdRaiseSub> prdRaiseRegList = new ArrayList<>();
	                if (posProdRegAllMap.containsKey(key)){
	                    prdRaiseRegList = posProdRegAllMap.get(key);
	                }
	                genSingleProdRegistReport(executeDate, key, posProdAllMap.get(key),prdRaiseRegList);
	            }
                // 根据查询出的登记数据分组进行出单产品报表
                for (String key : posProdRegAllMap.keySet()){
                    if (!prodMap.containsKey(key)){
                        prodMap.put(key, key);
                    } else {
                        // 已经出过报表则跳过
                        continue;
                    }
                    List<PbcProdRaiseSub> prdRaiseRegList = new ArrayList<>();
                    if (posProdRegAllMap.containsKey(key)){
                        prdRaiseRegList = posProdRegAllMap.get(key);
                    }
                    genSingleProdRegistReport(executeDate, key, posProdAllMap.get(key),prdRaiseRegList);
                }
                // 根据查询出的登记数据分组进行出单产品报表
                for (String key : posProdRegNoAllMap.keySet()){
                    if (!prodMap.containsKey(key)){
                        prodMap.put(key, key);
                    } else {
                        // 已经出过报表则跳过
                        continue;
                    }
                    List<PbcProdRaiseSub> prdRaiseRegList = new ArrayList<>();
                    if (posProdRegNoAllMap.containsKey(key)){
                        prdRaiseRegList = posProdRegNoAllMap.get(key);
                        prdRaiseRegList.forEach(item->{
                            item.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
                        });
                    }
                    genSingleProdRegistReport(executeDate, key, posProdAllMap.get(key),prdRaiseRegList);
                }
			}
        } else{
			Map<String,List<PbcProdRaiseSub>> posProdAllMap = prdCashRaiseList.stream().collect(
					Collectors.groupingBy(PbcProdRaiseSub::getPbcProdidAndOrgCode,Collectors.toList()));
            Map<String,List<PbcProdRaiseSub>> posProdRegAllMap = prdCashRaiseRegList.stream().collect(
                    Collectors.groupingBy(PbcProdRaiseSub::getPbcProdidAndOrgCode,Collectors.toList()));
            Map<String,List<PbcProdRaiseSub>> posProdRegNoAllMap = prdCashRaiseNoRegList.stream().collect(
                    Collectors.groupingBy(PbcProdRaiseSub::getPbcProdidAndOrgCode,Collectors.toList()));
            // 记录已经统计过得单产品(带机构)
            Map<String,String> prodMap = new HashMap<>();
			// 根据查询出的数据分组进行出单产品报表
			for (String key : posProdAllMap.keySet()){
                if (!prodMap.containsKey(key)){
                    prodMap.put(key, key);
                } else {
                    // 已经出过报表则跳过
                    continue;
                }
                List<PbcProdRaiseSub> prdRaiseRegList = new ArrayList<>();
                if (posProdRegAllMap.containsKey(key)){
                    prdRaiseRegList = posProdRegAllMap.get(key);
                }
				genSingleProdRegistReport(executeDate, key, posProdAllMap.get(key),prdRaiseRegList);
			}
            // 根据查询出的登记数据分组进行出单产品报表
            for (String key : posProdRegAllMap.keySet()){
                if (!prodMap.containsKey(key)){
                    prodMap.put(key, key);
                } else {
                    // 已经出过报表则跳过
                    continue;
                }
                List<PbcProdRaiseSub> prdRaiseRegList = new ArrayList<>();
                if (posProdRegAllMap.containsKey(key)){
                    prdRaiseRegList = posProdRegAllMap.get(key);
                }
                genSingleProdRegistReport(executeDate, key, posProdAllMap.get(key),prdRaiseRegList);
            }
            // 根据查询出的登记数据分组进行出单产品报表
            for (String key : posProdRegNoAllMap.keySet()){
                if (!prodMap.containsKey(key)){
                    prodMap.put(key, key);
                } else {
                    // 已经出过报表则跳过
                    continue;
                }
                List<PbcProdRaiseSub> prdRaiseRegList = new ArrayList<>();
                if (posProdRegNoAllMap.containsKey(key)){
                    prdRaiseRegList = posProdRegNoAllMap.get(key);
                    prdRaiseRegList.forEach(item->{
                        item.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
                    });
                }
                genSingleProdRegistReport(executeDate, key, posProdAllMap.get(key),prdRaiseRegList);
            }
        }
	}
	
	/**
	 * 功能:人行单产品报表-产品资金募集信息批处理-生成单一产品的产品资金募集信息
	 * @since 1.7.0
	 * @version 1.7.0
	 * @author：weiyuqiang 2019年6月12日
	 * @param executeDate 跑批日
	 * @param prodid 产品代码
	 * @param prdCashRaiseList 跑批日当天的所有产品的产品头寸信息
	 * @param prdCashRaiseRegList 当月已登记的单产品信息
	 * void
	 */
	public void genSingleProdRegistReport(Date executeDate, String prodid
			,List<PbcProdRaiseSub> prdCashRaiseList, List<PbcProdRaiseSub> prdCashRaiseRegList){
		// 新增列表
		List<PbcProdRaiseSub> insertList = new ArrayList<>();
		// 删除列表，月报默认全删全插
		List<PbcProdRaiseSub> deleteList = new ArrayList<>();
        // key：产品代码、地区代码、客户类型、币种代码 value:产品资金信息
        Map<String,PbcProdRaiseSub> resultMap = new HashMap<String,PbcProdRaiseSub>();
        // 人行汇率开关
        String exchangeSource = CacheUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        // 汇率map
        Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();
        // md5加密key
        String key = "joyin";
        // 存放已登记的单产品报表信息,按产品代码、地区、客户一级类型、币种划分组
        Map<String,PbcProdRaiseSub> prdCashRaiseRegMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(prdCashRaiseRegList)){
            prdCashRaiseRegMap = prdCashRaiseRegList.stream().collect(Collectors.toMap(p->p.getUniqueKey(), p->p,(key1,key2)->key2));
        }
        // 存放要入库的单产品报表信息,按产品代码、地区、客户一级类型、币种划分组
        Map<String,PbcProdRaiseSub> prdCashRaiseMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(prdCashRaiseList)){
            prdCashRaiseMap = prdCashRaiseList.stream().collect(Collectors.toMap(p->p.getUniqueKey(), p->p,(key1,key2)->key2));
        }
        if (CollectionUtils.isNotEmpty(prdCashRaiseList)){
            // 如果重新生成数据则重新计算
            for (PbcProdRaiseSub sub : prdCashRaiseList){
                // 先映射在分组
                // 地区代码映射到一级分类
                if (StringUtil.isNotEmpty(sub.getBuyPlace())){
                    String buyPlace = sub.getBuyPlace();
                    // 通过销售区域获取上级省
                    // 县级往市级映射
                    if (SvrDataUtil.getNameFromDataDict(DictCode.行政区划_县, buyPlace) != null){
                        buyPlace = SvrDataUtil.getParentCodeFromDataDict(DictCode.行政区划_县, buyPlace);
                    }
                    // 市级往省级映射
                    if (SvrDataUtil.getNameFromDataDict(DictCode.行政区划_市, buyPlace) != null){
                        buyPlace = SvrDataUtil.getParentCodeFromDataDict(DictCode.行政区划_市, buyPlace);
                    }
                    sub.setBuyPlace(buyPlace);
                }
                // 根据客户二级类型映射成客户一级类型
                if (StringUtil.isNotEmpty(sub.getCustomerType1())){
                    String customerType1 = chbTypeMappingService.getMappingValue("PBC_RAISING_INVESTOR_TYPE", sub.getCustomerType1());
                    sub.setCustomerType1(customerType1);
                }
                // 共通数据处理
                // key：产品代码、地区代码、客户类型、币种代码,汇总数据
                String keyStringSub = sub.getRaisingDate() + "|" + sub.getPbcProdId() + "|" + sub.getBuyPlace() + "|"
                        + sub.getCustomerType1() + "|" + sub.getCcy() + "|";
                String encodeStrSub=DigestUtils.md5Hex( keyStringSub + key);
                if (!DictCodeListExt1.币种_中债_人民币.getValue().equals(sub.getCcy())){
                    // 当期申购金额折人民币
                    sub.setRaisingAmtsumCny(ChbCcyConvertUtil.
                            exchangeMoney(sub.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), 
                                    sub.getRaisingDate(), sub.getRaisingAmtsum(), rateMap, exchangeSource));
                    // 当期兑付/赎回金额折人民币
                    sub.setRedamtCny(ChbCcyConvertUtil.
                            exchangeMoney(sub.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), 
                                    sub.getRaisingDate(), sub.getRedamt(), rateMap, exchangeSource));
                    // 期末产品金额折人民币
                    sub.setEndamtsumCny(ChbCcyConvertUtil.
                            exchangeMoney(sub.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), 
                                    sub.getRaisingDate(), sub.getEndamtsum(), rateMap, exchangeSource));
                }
                if (resultMap.containsKey(encodeStrSub)){
                    // 已存在的数据进行汇总处理
                    PbcProdRaiseSub LastSub = resultMap.get(encodeStrSub);
                    // 当期申购金额计算累计值
                    BigDecimal raisingAmtsum = LastSub.getRaisingAmtsum() == null?BigDecimal.ZERO:LastSub.getRaisingAmtsum();
                    LastSub.setRaisingAmtsum(raisingAmtsum.add(sub.getRaisingAmtsum()==null?BigDecimal.ZERO:sub.getRaisingAmtsum()));
                    // 当期申购金额折人民币累计值
                    BigDecimal raisingAmtsumCny = LastSub.getRaisingAmtsumCny() == null?BigDecimal.ZERO:LastSub.getRaisingAmtsumCny();
                    LastSub.setRaisingAmtsumCny(raisingAmtsumCny.add(sub.getRaisingAmtsumCny()==null?BigDecimal.ZERO:sub.getRaisingAmtsumCny()));
                    // 当期申购份额累计值
                    BigDecimal raisingQty = LastSub.getRaisingQty() == null?BigDecimal.ZERO:LastSub.getRaisingQty();
                    LastSub.setRaisingQty(raisingQty.add(sub.getRaisingQty()==null?BigDecimal.ZERO:sub.getRaisingQty()));
                    // 当期兑付/赎回金额累计值
                    BigDecimal redamt = LastSub.getRedamt() == null?BigDecimal.ZERO:LastSub.getRedamt();
                    LastSub.setRedamt(redamt.add(sub.getRedamt()==null?BigDecimal.ZERO:sub.getRedamt()));
                    // 当期兑付/赎回金额折人民币累计值
                    BigDecimal redamtCny = LastSub.getRedamtCny() == null?BigDecimal.ZERO:LastSub.getRedamtCny();
                    LastSub.setRedamtCny(redamtCny.add(sub.getRedamtCny()==null?BigDecimal.ZERO:sub.getRedamtCny()));
                    // 当期兑付/赎回份额累计值
                    BigDecimal redqty = LastSub.getRedqty() == null?BigDecimal.ZERO:LastSub.getRedqty();
                    LastSub.setRedqty(redqty.add(sub.getRedqty()==null?BigDecimal.ZERO:sub.getRedqty()));
                    // 期末产品金额累计值
                    BigDecimal endamtsum = LastSub.getEndamtsum() == null?BigDecimal.ZERO:LastSub.getEndamtsum();
                    LastSub.setEndamtsum(endamtsum.add(sub.getEndamtsum()==null?BigDecimal.ZERO:sub.getEndamtsum()));
                    // 期末产品金额折人民币累计值
                    BigDecimal endamtsumCny = LastSub.getEndamtsumCny() == null?BigDecimal.ZERO:LastSub.getEndamtsumCny();
                    LastSub.setEndamtsumCny(endamtsumCny.add(sub.getEndamtsumCny()==null?BigDecimal.ZERO:sub.getEndamtsumCny()));
                    // 期末产品份额累计值
                    BigDecimal endqty = LastSub.getEndqty() == null?BigDecimal.ZERO:LastSub.getEndqty();
                    LastSub.setEndqty(endqty.add(sub.getEndqty()==null?BigDecimal.ZERO:sub.getEndqty()));
                } else {
                    // 登记序列
                    sub.setRegistSeqno(IDGenerator.generate(36));
                    if (StringUtil.isNotEmpty(sub.getYieldCcy())&&
                    		!DictCodeListExt1.币种_中债_人民币.getValue().equals(sub.getYieldCcy())){
                        // 开放式净值型产品期末净值折人民币
                        if (sub.getProdValue() != null){
                            sub.setProdValueCny(ChbCcyConvertUtil.
                                    exchangeMoney(sub.getYieldCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), 
                                            sub.getRaisingDate(), sub.getProdValue(), rateMap, exchangeSource));
                        }
                        // 开放式净值型产品期末累计净值折人民币
                        if (sub.getUnitValueTotal() != null){
                            sub.setUnitValueTotalCny(ChbCcyConvertUtil.
                                    exchangeMoney(sub.getYieldCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), 
                                            sub.getRaisingDate(), sub.getUnitValueTotal(), rateMap, exchangeSource));
                        }
                    }
                    // 报送状态
                    sub.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
                    // 数据日期
                    sub.setStatusDate(executeDate);
                    // 报送截止日生成
                    // 当前批次报送时点集合
                    Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
                    List<Date> dateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_产品资金募集信息.getValue(),
                            DictCodeListExt2.报送数据状态_新建.getValue(),dateMap, DateUtil.getFirstDay(executeDate),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
                    if (!StringUtil.isEmptyList(dateList)) {
                        sub.setPbcSubmitDeadline(dateList.get(0));
                        sub.setSubmitDeadline(dateList.get(1));
                    }
                    // 创建人
                    sub.setCreateUser(CommonUtil.getUserName());
                    // 更新人
                    sub.setUpdateUser(CommonUtil.getUserName());
                    // 创建部门
                    sub.setCreateDept(CommonUtil.getDeptCode());
                    // 机构代码
                    if (StringUtil.isEmpty(sub.getOrgCode())){
                        sub.setOrgCode(CommonUtil.getOrgCode());
                    }
                    // 计入带入库列表
                    resultMap.put(encodeStrSub, sub);
                }
            }
        }
        // 是否重新生成登记数据
        boolean isReg = true;
        // 要登记的数据
        List<PbcProdRaiseSub> prdCashRaiseListToReg = new ArrayList<>(resultMap.values());
        // 数据的状态:默认新建
        String status = DictCodeListExt2.报送数据状态_新建.getValue();
        // 如果有已报送的数据
        if (!CollectionUtils.isEmpty(prdCashRaiseRegList)){
            // 没有要入库的数据，则生成删除未报送
            if (CollectionUtils.isEmpty(prdCashRaiseListToReg)){
                // 如果登记数据是删除已报送则不生成,单产品的维度删除状态都一致
                if(DictCodeListExt2.报送数据状态_删除.getValue()
                        .equals(prdCashRaiseRegList.get(0).getStatus())){
                    isReg = false;
                    deleteList.addAll(prdCashRaiseRegList);
                } else {
                    // 删除未报送改的数据根据已报送数据生成
                    for (PbcProdRaiseSub v: prdCashRaiseRegList){
                        v.setRegistSeqno(IDGenerator.generate(36));
                        v.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
                        v.setSubmitFeedbackStatus(null);  
                        v.setSubmitTime(null);
                        v.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
                    }
                    insertList.addAll(prdCashRaiseRegList);
                    deleteList.addAll(prdCashRaiseRegList);
                    isReg = false;
                }
            } else {
                // 判断数据是否发生变化，发生变化则生活才能修改未报送
                // 如果登记数据是删除已报送则生成新建未报送
                if(DictCodeListExt2.报送数据状态_删除.getValue()
                        .equals(prdCashRaiseRegList.get(0).getStatus())){
                    status = DictCodeListExt2.报送数据状态_新建.getValue();
                } else {
                    if (!compareData(prdCashRaiseMap, prdCashRaiseRegMap)){
                        status = DictCodeListExt2.报送数据状态_修改.getValue();
                    } else { // 否则不生成报表
                        deleteList.addAll(prdCashRaiseList);
                        isReg = false;
                    }
                }
            }
        } else { // 没有已报送的数据
            // 没有头寸数据则不生成报表
            if (CollectionUtils.isEmpty(prdCashRaiseList)){
                deleteList.addAll(prdCashRaiseListToReg);
                isReg = false;
            } else{
                // 没有已报送的数据则生成新建数据
                status = DictCodeListExt2.报送数据状态_新建.getValue();
            }
        }
        if (isReg){
            for (PbcProdRaiseSub sub : prdCashRaiseListToReg){
            	String  jsbankStr=SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_JSBK_IS_ROUND);
                // 数据状态
                sub.setStatus(status);
                // 修改开放式净值型产品期末净值、开放式净值型产品期末净值折人民币、开放式净值型产品期末累计净值、开放式净值型产品期末累计净值折人民币保留五位小数
                if(sub.getProdValue()!= null){
                	//江苏银行个性化保留小数位
                    if(StringUtil.isNotEmpty(jsbankStr)&&jsbankStr.equals("01")){
                        sub.setProdValue(sub.getProdValue().setScale(5,BigDecimal.ROUND_DOWN));
                    }else{
                        sub.setProdValue(sub.getProdValue().setScale(5,BigDecimal.ROUND_HALF_UP));
                    }
                }
                if(sub.getProdValueCny()!=null){
                	 if(StringUtil.isNotEmpty(jsbankStr)&&jsbankStr.equals("01")){
                         sub.setProdValueCny(sub.getProdValueCny().setScale(5,BigDecimal.ROUND_DOWN));
                	 }else{
                         sub.setProdValueCny(sub.getProdValueCny().setScale(5,BigDecimal.ROUND_HALF_UP));
                	 }
                }
                if(sub.getUnitValueTotal()!=null){
                	if(StringUtil.isNotEmpty(jsbankStr)&&jsbankStr.equals("01")){
                        sub.setUnitValueTotal(sub.getUnitValueTotal().setScale(5,BigDecimal.ROUND_DOWN));
                	}else{
                        sub.setUnitValueTotal(sub.getUnitValueTotal().setScale(5,BigDecimal.ROUND_HALF_UP));
                	}
                }
                if(sub.getUnitValueTotalCny()!=null){
                	if(StringUtil.isNotEmpty(jsbankStr)&&jsbankStr.equals("01")){
                        sub.setUnitValueTotalCny(sub.getUnitValueTotalCny().setScale(5,BigDecimal.ROUND_DOWN));
                	}else{
                        sub.setUnitValueTotalCny(sub.getUnitValueTotalCny().setScale(5,BigDecimal.ROUND_HALF_UP));
                	}
                }
            }
            insertList.addAll(prdCashRaiseListToReg);
            deleteList.addAll(prdCashRaiseListToReg);
        }
        
        //当期申购金额、当期申购份额、当期兑付/赎回金额、当期兑付/赎回份额、期末产品金额、期末产品份额字段同时为空或为0时，应该不生成pbc_prod_raising_sub表的数据 start 20200514
        Iterator<PbcProdRaiseSub> iterator = insertList.iterator();
        while(iterator.hasNext()){
        	PbcProdRaiseSub pbcProdRaiseSub = (PbcProdRaiseSub) iterator.next();
        	//当期申购金额
        	BigDecimal raisingAmtsum = pbcProdRaiseSub.getRaisingAmtsum();
        	//当期申购份额
        	BigDecimal raisingQty = pbcProdRaiseSub.getRaisingQty();
        	//当期兑付/赎回金额
        	BigDecimal redamt = pbcProdRaiseSub.getRedamt();
        	//当期兑付/赎回份额
        	BigDecimal redqty = pbcProdRaiseSub.getRedqty();
        	//期末产品金额
        	BigDecimal endamtsum = pbcProdRaiseSub.getEndamtsum();
        	//期末产品份额
        	BigDecimal endqty = pbcProdRaiseSub.getEndqty();
        	if(raisingAmtsum == null && raisingQty == null && redamt == null && redqty == null && endamtsum == null && endqty == null){
        			iterator.remove();
        	}
        	if(raisingAmtsum.compareTo(BigDecimal.ZERO) == 0 && raisingQty.compareTo(BigDecimal.ZERO) == 0 &&
        			redamt.compareTo(BigDecimal.ZERO) == 0 && redqty.compareTo(BigDecimal.ZERO) == 0 && 
        			endamtsum.compareTo(BigDecimal.ZERO) == 0 && endqty.compareTo(BigDecimal.ZERO) == 0){
    			iterator.remove();
        	}
        }
        //当期申购金额、当期申购份额、当期兑付/赎回金额、当期兑付/赎回份额、期末产品金额、期末产品份额字段同时为空或为0时，应该不生成pbc_prod_raising_sub表的数据 start 20200514

        // 数据入库
        // 先删后插
		intoDatabase(insertList,deleteList);
	}
	

	/**
	 * 功能:批量入库
	 * @since 1.7.0
	 * @version 1.7.0
	 * @author：weiyuqiang 2019年6月11日
	 * @param insertlist
	 * @param deletelist 
	 * void
	 */
	private void intoDatabase(List<PbcProdRaiseSub> insertlist, List<PbcProdRaiseSub> deletelist) {
		if (CollectionUtils.isNotEmpty(deletelist)) {
		    // 插入前先删除未报送的登记数据
		    logger.debug("人行产品资金募集信息删除未报送数据：删除开始----------");
		    // 数据批量一次删除条数,如果参数设置过大则强制定位50
            int batchDeleteCount = reportSelectOrDeleteCount;
            // 每批最后一个的下标
            int batchDeleteLastIndex = batchDeleteCount;            
            for(int index = 0; index < deletelist.size();){
                if(batchDeleteLastIndex > deletelist.size()-1){
                    batchDeleteLastIndex = deletelist.size();
                    pbcProdRaiseSubMapper.deleteBatch(deletelist.subList(index, batchDeleteLastIndex));
                    break;//数据插入完毕,退出循环
                }else{
                	pbcProdRaiseSubMapper.deleteBatch(deletelist.subList(index, batchDeleteLastIndex));
                    index = batchDeleteLastIndex;//设置下一批下标
                    batchDeleteLastIndex = index + batchDeleteCount;                     
                }
            }
            logger.debug("人行产品资金募集信息删除未报送数据：删除结束----------");
            logger.debug("人行产品资金募集信息插入数据：插入开始----------");
			int count = 0;
			SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
	    	try{
	    		PbcProdRaiseSubMapper batchMapper =  session.getMapper(PbcProdRaiseSubMapper.class);
	    		for(PbcProdRaiseSub dto : insertlist){
	    			batchMapper.insert(dto);
	    			count++;
	    			if(count%1000==0){
	    				session.commit();
	    	            // 清理缓存，防止溢出
	    	            session.clearCache();
	    			}
	    		}
	    		session.commit();
	            // 清理缓存，防止溢出
	            session.clearCache();
	    	} catch(Exception e) {
	    		logger.error(e.getMessage(),e);
	    		session.rollback();
	    		throw new BusinessException(e);
	    	} finally {
	    		session.close();
	    	}
            logger.debug("人行产品资金募集信息插入数据：插入结束----------");
		}
	}

    /**
     * 功能:判断俩个登记数据是否一致
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年6月17日
     * @param prdRaiseMap
     * @param prdRaiseOrgMap
     * @return 
     * boolean
     */
    public boolean compareData(Map<String,PbcProdRaiseSub> prdRaiseMap,Map<String,PbcProdRaiseSub> prdRaiseOrgMap){
        // 俩者只要有一个为空则认为不相等
        if (prdRaiseMap == null || prdRaiseOrgMap == null){
            return false;
        }
        // 先判断生成的数据大小是否一致
        if (prdRaiseMap.keySet().size() != prdRaiseOrgMap.keySet().size()){
            return false;
        }
        // 判断数据是否相等
        for (String key : prdRaiseMap.keySet()){
            if (!prdRaiseOrgMap.containsKey(key)){
                return false;
            } 
            PbcProdRaiseSub compare1 = genDto(prdRaiseMap.get(key));
            PbcProdRaiseSub compare2 = genDto(prdRaiseOrgMap.get(key));
            if(!JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
                return false;
            }
        }
        return true;
    }

    /**
     * 功能:根据源数据获取比较的dto
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年6月17日
     * @param orgDto
     * @return 
     * PbcProdRaiseSub
     */
    private PbcProdRaiseSub genDto(PbcProdRaiseSub orgDto) { 
        PbcProdRaiseSub sub = new PbcProdRaiseSub();
        sub.setRaisingDate(orgDto.getRaisingDate());
        sub.setPbcProdId(orgDto.getPbcProdId());
        sub.setBuyPlace(orgDto.getBuyPlace());
        sub.setCustomerType1(orgDto.getCustomerType1());
        sub.setCcy(orgDto.getCcy());
        sub.setRaisingAmtsum((orgDto.getRaisingAmtsum()==null?BigDecimal.ZERO:orgDto.getRaisingAmtsum()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setRaisingAmtsumCny((orgDto.getRaisingAmtsumCny()==null?BigDecimal.ZERO:orgDto.getRaisingAmtsumCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setRaisingQty((orgDto.getRaisingQty()==null?BigDecimal.ZERO:orgDto.getRaisingQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setRedamt((orgDto.getRedamt()==null?BigDecimal.ZERO:orgDto.getRedamt()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setRedamtCny((orgDto.getRedamtCny()==null?BigDecimal.ZERO:orgDto.getRedamtCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setRedqty((orgDto.getRedqty()==null?BigDecimal.ZERO:orgDto.getRedqty()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setEndamtsum((orgDto.getEndamtsum()==null?BigDecimal.ZERO:orgDto.getEndamtsum()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setEndamtsumCny((orgDto.getEndamtsumCny()==null?BigDecimal.ZERO:orgDto.getEndamtsumCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setEndqty((orgDto.getEndqty()==null?BigDecimal.ZERO:orgDto.getEndqty()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setProdValue((orgDto.getProdValue()==null?BigDecimal.ZERO:orgDto.getProdValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setProdValueCny((orgDto.getProdValueCny()==null?BigDecimal.ZERO:orgDto.getProdValueCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setUnitValueTotal((orgDto.getUnitValueTotal()==null?BigDecimal.ZERO:orgDto.getUnitValueTotal()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setUnitValueTotalCny((orgDto.getUnitValueTotalCny()==null?BigDecimal.ZERO:orgDto.getUnitValueTotalCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setProdHighYield((orgDto.getUnitValueTotalCny()==null?BigDecimal.ZERO:orgDto.getUnitValueTotalCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setProdLowYield((orgDto.getProdLowYield()==null?BigDecimal.ZERO:orgDto.getProdLowYield()).setScale(2, BigDecimal.ROUND_HALF_UP));
        return sub;
    }
}