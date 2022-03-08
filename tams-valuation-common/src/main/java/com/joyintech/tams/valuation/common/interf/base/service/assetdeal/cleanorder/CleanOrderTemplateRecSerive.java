/*
 * 文件名：CleanOrderTemplateRecSerive.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chendijun
 * 修改时间：2018年8月17日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.cleanorder;

import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import org.jasypt.commons.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValCleanOrder;
import org.jasypt.commons.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 结算指令接口 模板处理Service(VAL_PRD_PERIOD_DEAL_C) 〈功能详细描述〉
 * 
 * @author chendijun
 * @version 1.0 2018年8月17日
 * @see CleanOrderTemplateRecSerive
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CleanOrderTemplateRecSerive {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    /**
     * 接口共通方法
     */
    @Autowired
    private InterfCommonService interfCommonService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public CleanOrderCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                          TemplateOrderInfoReaderResult acctEntity) throws Exception{
        // 初始化 模板处理返回共通结果
        CleanOrderCommonResult result = new CleanOrderCommonResult();
        Map<String,Object> params = acctEntity.getParams();
        List<ValCleanOrder> listValCleanOrder = new ArrayList<ValCleanOrder>();

        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();

        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValCleanOrder = returnResult.getT(ValCleanOrder.class);
        //设置读取数据数量
        result.setDataNum(listValCleanOrder.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态, DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/
        
        // 3、根据有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValCleanOrder(listValCleanOrder,
            acctEntity.getResovleDate(),params,returnResult.getValInterflog());

        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
            "ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 入账账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
            "ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);

        // 结算指令
        List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
        result.setListValOrders(listValOrders);
        // 结算指令扩展表
        List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrdersExt(listValOrdersExt);

        // 添加日志
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("booksetIds", (List<String>)map.get("booksetIds"));
        result.setParamMap(paramMap);
        // 返回处理结果数据
        return result;
    }
    @SuppressWarnings({"unchecked"})
    private Map<String, Object> handleValCleanOrder(List<ValCleanOrder> listValCleanOrder,
            Date resovleDate,Map<String,Object> params,List<ValInterflog> valInterflog) throws Exception{
		List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
		String valModel = (String)params.get(InterfCommonConstant.接口模式);
        // 初始化返回结果
        Map<String, Object> resultMap = new HashMap<>();
        // 入账信息表
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        // 结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        // 结算指令扩展表
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        List<String> booksetIds = new ArrayList<>();
        for (ValCleanOrder dto : listValCleanOrder) {
        	if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
            dto.setCreateTime(resovleDate);// 用于去重操作
            // T1: 结算指令表
            StlOrders ValOrders = this.getValStlOrders(dto);
            listValOrders.add(ValOrders);
            // T2: 结算指令扩展表
            StlOrdersExt ValOrdersExt = this.getValStlOrdersExt(dto,ValOrders);
            listValOrdersExt.add(ValOrdersExt);
            // T3: 账务明细代码表
            BokDetailId valBokDetailId = this.getValBokDetailId(dto);
            listValBokDetailId.add(valBokDetailId);
            // T6: 账务明细代码表
            BokDetailId valBokDetailIdT6 = this.getValBokDetailIdT6(dto);
            listValBokDetailId.add(valBokDetailIdT6);
            // T4: 入账信息表
            BokDealAccounting valBokDealAccounting = this.getValDealAccounting(dto,
                valBokDetailId,ValOrders);
            listValDealAccounting.add(valBokDealAccounting);;
            // T5: 入账账务明细表
            BokDealAccountingDetail valBokDealAccountingDetail = this.getValBokDealAccountingDetail(
                dto, valBokDetailId,ValOrders);
            listValDealAccountingDetail.add(valBokDealAccountingDetail);
            booksetIds.add(valBokDealAccountingDetail.getBooksetId()); 
        }
        resultMap.put("listValOrders", listValOrders);
        resultMap.put("listValOrdersExt", listValOrdersExt);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        if(booksetIds != null && booksetIds.size()>0){
        	resultMap.put("booksetIds", booksetIds);
        }
        return resultMap;
    }

    /**
     * T1: 结算指令表
     * 
     * @param dto ValCleanOrder
     * @return StlOrders
     * @see
     * @since 1.0
     */
    private StlOrders getValStlOrders(ValCleanOrder dto) throws Exception {
        StlOrders stlOrders = new StlOrders();

        //业务类型 = A.业务类型
        stlOrders.setBusiType(dto.getBusiType());
        //指令类型 = A.指令类型
        /**update by yinya on 2022-01-08 上游投管传交易相关属性，由估值自动映射指令类型**/
        //stlOrders.setOrderType(dto.getOrderType());
        if(CommonUtils.isNotEmpty(dto.getOrderType())){
            stlOrders.setOrderType(dto.getOrderType());
        }else{
            //定义 变量.交易对应指令集合 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询交易需要生成的指令SHEET
            ProductDealDto deal = new ProductDealDto();
            deal.setFinprodType(dto.getFinprodType());
            deal.setBusiType(dto.getBusiType());
            deal.setPs(dto.getTradePs());
            deal.setInvAim(dto.getInvAim());
            //02为接口默认使用的映射类型
            deal.setMappingType("02");
            //匹配IS_BOOK为N的指令映射
            deal.setIsTrade(DictCodeList.是否_否.getValue());
            List<StlOrdersMapping> somlist = interfCommonService.queryStlOrdersMapping(deal);
//            List<StlOrdersMapping> stlList =new ArrayList<StlOrdersMapping>();
//            if(somlist==null || somlist.size()==0){
//               throw new Exception(
//                       "指令编号："+dto.getOrderId()
//                       +" 金融产品类型："+dto.getFinprodType()
//                       +" 业务类型："+dto.getBusiType()
//                       +" 交易方向："+dto.getTradePs()
//                       +" 投资目的："+dto.getInvAim()
//                       +"未匹配指令类型,请检查结算指令与交易映射关系表");
//            }
//            //去重处理
//            somlist.stream(). collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getOrderType()+f.getMappingType()))), ArrayList::new))
//                    .forEach(f->stlList.add(f));
//
//            if(stlList.size()>1){
//                throw new Exception(
//                                "指令编号："+dto.getOrderId()
//                                +" 金融产品类型："+deal.getFinprodType()
//                                +" 业务类型："+dto.getBusiType()
//                                +" 交易方向："+dto.getTradePs()
//                                +" 投资目的："+dto.getInvAim()
//                                +" 匹配多个指令类型，请检查结算指令与交易映射关系表");
//            }
            stlOrders.setOrderType(somlist.get(0).getOrderType());

        }
        //指令编号 = interfCommonService.getTradeid(A.指令类型,A.结算日期，“B")
        stlOrders.setOrderId(interfCommonService.getTradeid(dto.getOrderType(), dto.getSettleDate(), "B"));

        //结算日期 = A.结算日期
        stlOrders.setSettleDate(dto.getSettleDate());
        //交易编号 = A.交易编号
        stlOrders.setTradeId(dto.getTradeId());
        //本方资金账户 = A.本方资金账户
        stlOrders.setMyAcctNo(dto.getMyAcctNo());
        //收付方向 = A.收付方向
        stlOrders.setPs(dto.getPs());
        //对方资金账户 = A.对方资金账户
        stlOrders.setCounterAcctNo(dto.getCounterAcctNo());
        //结算金额 = A.结算金额
        stlOrders.setSettleAmt(dto.getSettleAmt());
        //币种 = A.币种
        stlOrders.setCcy(dto.getCcy());
        /**add 2018/11/14 start**/
        //结算状态 = 接口传入，若未提供默认成功 数据字典：结算状态.结算成功
        stlOrders.setSettleStatus(dto.getSettleStatus()==null? DictCodeList.成功失败标志_成功.getValue():dto.getSettleStatus());
        //指令状态 = A.数据动作标志 新增 --> 01:初始 撤销 --> 03:撤销
        stlOrders.setOrderStatus(dto.getDataActionSign());
        //关联系统指令编号 = A.指令编号
        stlOrders.setfOrderId(dto.getOrderId());
        //原指令编号 = 指令状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销指令交易对应的原指令编号查询SHEET
        //           交易状态为非撤销时：原交易编号为空
        if(DictCodeListExt1.交易状态_撤销.getValue().equals(dto.getDataActionSign())) {
            stlOrders.setpOrderId(interfCommonService.queryPOrderId(stlOrders));
        } 

        /**add 2018/11/14 start**/
        //创建时间 = 固定值:系统时间
        stlOrders.setCreateTime(dto.getCreateTime());
        
        return stlOrders;
    }

    /**
     * T2: 结算指令扩展表 <功能详细描述>
     * 
     * @param dto ValCleanOrder
     * @return StlOrdersExt
     * @see
     * @since 1.0
     */
    private StlOrdersExt getValStlOrdersExt(ValCleanOrder dto, StlOrders valOrders) {
        StlOrdersExt stlOrdersExt = new StlOrdersExt();
        //指令编号 = A.指令编号
        stlOrdersExt.setOrderId(valOrders.getOrderId());
        //是否随交易出账 = 固定值:N 否
        stlOrdersExt.setIsTrade(DictCodeList.是否_否.getValue());
        //创建时间 = 系统时间
        stlOrdersExt.setCreateTime(dto.getCreateTime());

        return stlOrdersExt;
    }

    /**
     * T3: 账务明细代码表
     * 
     * @param dto ValCleanOrder
     * @return BokDetailId
     * @see
     * @since 1.0
     */
    private BokDetailId getValBokDetailId(ValCleanOrder dto) {
        BokDetailId bokDetailId = new BokDetailId();
        //账套代码 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码)SHEET
        ValBooksetInfoDto booksetInfoDto = interfCommonService.getBooksetInfoByProdId(dto.getProdId());
        if(bokDetailId != null) {
            bokDetailId.setBooksetId(booksetInfoDto.getBooksetId());
        }
        
        //业务明细代码 = A.本方资金账户
        bokDetailId.setBusiId(dto.getMyAcctNo());
        
        /** modify by wujie on 20181210 start remark:查询账户类型方法变更
        //业务明细类型 = 根据资金帐户查询对应的资金账户类型
        StlFinProductAcct stlFinProductAcct = interfCommonService.queryValFinProductAcct(null, null, null, dto.getMyAcctNo());
        if(stlFinProductAcct != null) {            
            bokDetailId.setBusiDetailType(stlFinProductAcct.getAcctType());
        }
        **/
        BokBooksetCapacctInfo bokBooksetCapacctInfo =
        		interfCommonService.queryBokBooksetCapacctInfo(bokDetailId.getBooksetId(), dto.getMyAcctNo(),dto.getCcy(),null);
        if(bokBooksetCapacctInfo != null){
        	bokDetailId.setBusiDetailType(bokBooksetCapacctInfo.getAcctType());
        }
        /**modify by wujie on 20181210 end**/

        // 币种 = A.本方资金账户币种
        bokDetailId.setCcy(dto.getCcy());

        //账务明细代码 = MD5加密(A.本方资金账户+本方资金账户类型)
        bokDetailId.setBokDetailId(interfCommonService.genMd5BokDetailId(bokDetailId));
        //投资目的 = 默认为空
        //证券管理户代码 = 默认为空
        //交易编号 = A.交易编号
        bokDetailId.setTradeId(dto.getTradeId());
        //创建时间 = 固定值:系统时间
        bokDetailId.setCreateTime(dto.getCreateTime());
        /** add by lby on 20201228 start **/
        //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
        BokDetailId bokDetailIdTail = interfCommonService.settingDetailSubjectNoTaiL(dto.getMyAcctNo(),bokDetailId.getBusiDetailType(),0L,null,bokDetailId.getBooksetId());
        //T3.账务明细代码.四级科目尾号 = dto.四级科目尾号
        bokDetailId.setDetailSubjectNoTail(bokDetailIdTail.getDetailSubjectNoTail());
        //T3.账务明细代码.四级科目尾号名称 = dto.四级科目尾号名称
        bokDetailId.setDetailSubnameTail(bokDetailIdTail.getDetailSubnameTail());
        /** add by lby on 20201228 end **/
        return bokDetailId;
    }
    /**
     * T6: 账务明细代码表
     *
     * @param dto ValCleanOrder
     * @return BokDetailId
     * @see
     * @author lby
     * @date 2020-12-28
     * @since 1.0
     */
    private BokDetailId getValBokDetailIdT6(ValCleanOrder dto) {
        BokDetailId bokDetailId = new BokDetailId();
        //账套代码 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码)SHEET
        ValBooksetInfoDto booksetInfoDto = interfCommonService.getBooksetInfoByProdId(dto.getProdId());
        if(bokDetailId != null) {
            bokDetailId.setBooksetId(booksetInfoDto.getBooksetId());
        }

        //业务明细代码 = A.对方资金账户
        bokDetailId.setBusiId(dto.getCounterAcctNo());

        BokBooksetCapacctInfo bokBooksetCapacctInfo =
                interfCommonService.queryBokBooksetCapacctInfo(bokDetailId.getBooksetId(), dto.getMyAcctNo(),dto.getCcy(),null);
        if(bokBooksetCapacctInfo != null){
            bokDetailId.setBusiDetailType(bokBooksetCapacctInfo.getAcctType());
        }

        // 币种 = A.币种
        bokDetailId.setCcy(dto.getCcy());

        //账务明细代码 = MD5加密(A.对方资金账户+T6.本方资金账户类型,A.币种)
        bokDetailId.setBokDetailId(interfCommonService.genMd5BokDetailId(bokDetailId));
        //投资目的 = 默认为空
        //证券管理户代码 = 默认为空
        //交易编号 = A.交易编号
        bokDetailId.setTradeId(dto.getTradeId());
        //创建时间 = 固定值:系统时间
        bokDetailId.setCreateTime(dto.getCreateTime());

        //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
        BokDetailId bokDetailIdTail = interfCommonService.settingDetailSubjectNoTaiL(dto.getCounterAcctNo(),bokDetailId.getBusiDetailType(),0L,null,bokDetailId.getBooksetId());
        //T6.账务明细代码.四级科目尾号 = dto.四级科目尾号
        bokDetailId.setDetailSubjectNoTail(bokDetailIdTail.getDetailSubjectNoTail());
        //T6.账务明细代码.四级科目尾号名称 = dto.四级科目尾号名称
        bokDetailId.setDetailSubnameTail(bokDetailIdTail.getDetailSubnameTail());

        return bokDetailId;
    }
    /**
     * T4: 入账信息表
     *
     * @param dto ValCleanOrder
     * @param bokDetailId BokDetailId
     * @return
     * @see
     * @since 1.0
     */
    private BokDealAccounting getValDealAccounting(ValCleanOrder dto, BokDetailId bokDetailId, StlOrders valOrders) {
        BokDealAccounting bokDealAccounting = new BokDealAccounting();
        //账套代码 = T3.账套代码
        bokDealAccounting.setBooksetId(bokDetailId.getBooksetId());
        //业务编号 = A.指令编号
        bokDealAccounting.setBusiNo(valOrders.getOrderId());
        //业务表 = 固定值:数据字典：业务表.结算指令表
        bokDealAccounting.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
        //业务类型 = A.业务类型
//        bokDealAccounting.setBusiType(dto.getBusiType());
        //录入日 = A.结算日期
        bokDealAccounting.setBooksetDate(dto.getSettleDate());
        //入账日 = A.结算日期
        bokDealAccounting.setBookDate(dto.getSettleDate());
        //实际发生日 = A.结算日期
        bokDealAccounting.setHappenDate(dto.getSettleDate());
        //账务明细代码 = T3.账务明细代码
//        bokDealAccounting.setBokDetailId(bokDetailId.getBokDetailId());
        //是否已入账 = 数据字典：是否.否
        bokDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
        //入账凭证编号 = 默认为空
        //创建时间 = 系统时间
        bokDealAccounting.setCreateTime(dto.getCreateTime());
        
        return bokDealAccounting;
    }

    private BokDealAccountingDetail getValBokDealAccountingDetail(ValCleanOrder dto,
                                                                  BokDetailId bokDetailId, StlOrders valOrders) {
        BokDealAccountingDetail bokDealAccountingDetail = new BokDealAccountingDetail();
        //账套代码 = T3.账套代码
        bokDealAccountingDetail.setBooksetId(bokDetailId.getBooksetId());
        //业务编号 = A.指令编号
        bokDealAccountingDetail.setBusiNo(valOrders.getOrderId());
        //业务表 = 固定值:数据字典：业务表.结算指令表
        bokDealAccountingDetail.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
        //业务类型 = A.业务类型
        bokDealAccountingDetail.setBusiType(dto.getBusiType());
        //账务明细代码 = T3.账务明细代码
        bokDealAccountingDetail.setBokDetailId(bokDetailId.getBokDetailId());
        //创建时间 = 系统时间
        bokDealAccountingDetail.setCreateTime(dto.getCreateTime());

        return bokDealAccountingDetail;
    }
}
