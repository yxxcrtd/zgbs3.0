package com.joyintech.tams.invest.period.trdsecoverdueduration.service;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.period.trdsecoverdueduration.dto.TrdSecOverdueDuration001Dto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName TrdSecOverdueDurationService
 * @Description: TODO
 * @Author wangxuelei
 * @Date 2019/12/4 0004
 * @Version V1.0
 **/
@Service
public class TrdSecOverdueDuration001Service extends CommonExportService<TrdSecOverdueDuration001Dto> {
    @Autowired
    private ProgressBarService progressBarService;
    /** @MethodName: findPage
    * @Description: TODO逾期未结清和已结清
    * @Param: [params]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.period.trdsecoverdueduration.dto.TrdSecOverdueDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/5 0005
    **/
    public Page<TrdSecOverdueDuration001Dto> selectPageData(Map<String, Object> params) {
        Page<TrdSecOverdueDuration001Dto> retPage = new Page<>();
        setParams(params);
        if(StringUtil.isNotEmpty((String)params.get("caseType"))){
            List<String> dateList =StringUtil.splitToList(",", (String)params.get("caseType"));
            params.put("caseTypeList",dateList);
        }
        //未结清
        if(StringUtil.equals(params.get("exportType").toString(),DictCodeList.是否_否.getValue())){
        	if(StringUtil.isNotEmpty((String)params.get("finprodType2"))){
                List<String> dateList =StringUtil.splitToList(",", (String)params.get("finprodType2"));
                params.put("finprodType2List",dateList);
            }
            retPage= restTemplate.postForPage(
                    ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/theoryFindPage", params,
                    TrdSecOverdueDuration001Dto.class);
            if(CollectionUtils.isNotEmpty(retPage.getRoot())){
                for (TrdSecOverdueDuration001Dto dto:retPage.getRoot()) {
                    //列表中理论本息总额 = 上述查询.理论本息总额+理论罚息总额
                    setTheoryAmtByDto(dto, dto.getTheoryPenaltyInt());
                    //未付罚息总额 = 理论罚息总额-实收罚息总额
                    setTheoryPenaltyIntByDto(dto);
                    //实收本息总额 = 实收本息总额+实收罚息总额
                    setFpriceAmtByDto(dto,dto.getPenaltyIntamt());
                    setBranchFromBranchType(dto);
                    setpenaltyBaseTypeByPage(dto);
                    setFxYieldByPage(dto);
                    setFxMdateByPage(dto);
                }
            }
        }else{//已结清
        	//此次转为投管业务类型
        	if(StringUtil.isNotEmpty((String)params.get("finprodType2"))){
                List<String> dateList =StringUtil.splitToList(",", (String)params.get("finprodType2"));
                List<String> resultList = new ArrayList<String>();
                for(String data :dateList){
                	String result = null;
            		if (StringUtil.equals(data, DictCodeList.平安资产类型_债券.getValue())) {
            			result=DictCodeListExt1.金融产品类型_投管用_债券.getValue();
            		} else if (StringUtil.equals(data, DictCodeList.平安资产类型_存款.getValue())) {
            			result=DictCodeListExt1.金融产品类型_投管用_存款.getValue();
            		} else if (StringUtil.equals(data, DictCodeList.平安资产类型_项目类资产.getValue())) {
            			result=DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue();
            		} else if (StringUtil.equals(data, DictCodeList.平安资产类型_计划类资产_利率型.getValue())) {
            			result=DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue();
            		}
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    else if (StringUtil.equals(data, DictCodeListExt3.平安资产类型_权益类资产.getValue())) {
                        result=DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue();
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 end
            		resultList.add(result);
                }
                params.put("finprodType2List",resultList);
            }
            retPage= restTemplate.postForPage(
                    ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/realityFindPage", params,
                    TrdSecOverdueDuration001Dto.class);
            if(CollectionUtils.isNotEmpty(retPage.getRoot())) {
                for (TrdSecOverdueDuration001Dto dto : retPage.getRoot()) {
                    //列表中理论本息总额 = 上述查询.理论本息总额+实收罚息总额
                    setTheoryAmtByDto(dto,dto.getPenaltyIntamt());
                    //已结清 不存在未支付罚息信息，处理为零
                    dto.setTheoryPenaltyInt(BigDecimal.ZERO);
                }
            }
        }
        return retPage;
    }

    private void setTheoryAmtByDto(TrdSecOverdueDuration001Dto dto ,BigDecimal... datas){
        BigDecimal theoryAmt = dto.getTheoryAmt();
        if(datas.length>0){
            for (BigDecimal data:datas) {
                theoryAmt  = ArithmeticUtil.doAdd(theoryAmt,data);
            }
        }
        dto.setTheoryAmt(theoryAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
    private void setFpriceAmtByDto(TrdSecOverdueDuration001Dto dto ,BigDecimal... datas){
        BigDecimal fpriceAmt = dto.getFpriceAmt();
        if(datas.length>0){
            for (BigDecimal data:datas) {
                fpriceAmt  = ArithmeticUtil.doAdd(fpriceAmt,data);
            }
        }
        dto.setFpriceAmt(fpriceAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    private void setTheoryPenaltyIntByDto(TrdSecOverdueDuration001Dto dto){
        BigDecimal theoryPenaltyInt = dto.getTheoryPenaltyInt();
        BigDecimal subtract = ArithmeticUtil.subtract(theoryPenaltyInt, dto.getPenaltyIntamt());
        dto.setTheoryPenaltyInt(subtract.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    private void setParams(Map<String, Object> params){
        params.put("rec_type_01", DictCodeListExt3.应收款类型_逾期未收本金.getValue());
        params.put("rec_type_02", DictCodeListExt3.应收款类型_逾期未收利息.getValue());
        params.put("rec_type_03", DictCodeListExt3.应收款类型_还本未收利息.getValue());
        params.put("position_type_03", DictCodeList.持仓类型_实际.getValue());
        params.put("BRANCH_TYPE_D01",DictCodeList.分支类型_应收本金.getValue());
        params.put("BRANCH_TYPE_D02",DictCodeList.分支类型_应收利息.getValue());
        params.put("PS_T09",DictCodeList.交易方向_收息.getValue());
        params.put("PS_T08",DictCodeList.交易方向_还本.getValue());
        params.put("PS_T07",DictCodeList.交易方向_到期.getValue());
        params.put("PS_T54",DictCodeListExt3.交易方向_投资端_提前终止.getValue());
        params.put("PINAN_ASSET_TYPE_F01", DictCodeList.平安资产类型_债券.getValue());
        params.put("PINAN_ASSET_TYPE_F03", DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        params.put("PINAN_ASSET_TYPE_F06", DictCodeList.平安资产类型_项目类资产.getValue());
        params.put("PINAN_ASSET_TYPE_F05", DictCodeList.平安资产类型_存款.getValue());
        // add by etliu 20210421 权益类资产相关代码合并 start
        params.put("PINAN_ASSET_TYPE_F19", DictCodeListExt3.平安资产类型_权益类资产.getValue());
        // add by etliu 20210421 权益类资产相关代码合并 end
        params.put("CASH_TYPE_2_C01", DictCodeListExt1.现金流二级类型_本金.getValue());
        params.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
    }

    /** @MethodName: recePositionDetailFindPage
    * @Description: TODO 持仓明细
    * @Param: [params]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.period.trdsecoverdueduration.dto.TrdSecOverdueDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/6 0006
    **/
    public Page<TrdSecOverdueDuration001Dto> recePositionDetailFindPage(Map<String, Object> params) {
        setParams(params);
        Page<TrdSecOverdueDuration001Dto> retPage= restTemplate.postForPage(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/recePositionDetailFindPage", params,
                TrdSecOverdueDuration001Dto.class);
        if(CollectionUtils.isNotEmpty(retPage.getRoot())){
            for (TrdSecOverdueDuration001Dto dto:retPage.getRoot()) {
                setTheoryAmtByDto(dto,dto.getTheoryPenaltyInt());
                //实收本息总额 = 实收本息总额+实收罚息总额
                setFpriceAmtByDto(dto,dto.getPenaltyIntamt());
                //未支付罚息总额= 理论罚息总额-实收罚息总额
                BigDecimal theoryPenaltyInt = ArithmeticUtil.subtract(dto.getTheoryPenaltyInt(), dto.getPenaltyIntamt());
                if(BigDecimal.ZERO.compareTo(theoryPenaltyInt)>0){
                	theoryPenaltyInt=BigDecimal.ZERO;
                }
                dto.setTheoryPenaltyInt(theoryPenaltyInt);
                
            }
            //ZGBSS-1324 【全流程测试-应收款查询】应收款转仓撤销后，原应收款的持仓明细展示问题
            retPage.setRoot(retPage.getRoot().stream().filter(item->item.getTheoryAmt().compareTo(BigDecimal.ZERO) != 0
                     || item.getTheoryInt().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList()));
        }
        return retPage;
    }
    /** @MethodName: findRecePositionDetailByPortfolioId
    * @Description: TODO 子组合持仓明细
    * @Param: [params]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.period.trdsecoverdueduration.dto.TrdSecOverdueDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/12 0012
    **/
    public Page<TrdSecOverdueDuration001Dto> findRecePositionDetailByPortfolioId(Map<String, Object> params) {
        setParams(params);
        Page<TrdSecOverdueDuration001Dto> retPage= restTemplate.postForPage(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/findRecePositionDetailByPortfolioId", params,
                TrdSecOverdueDuration001Dto.class);
        if(CollectionUtils.isNotEmpty(retPage.getRoot())){
            for (TrdSecOverdueDuration001Dto dto:retPage.getRoot()) {
                setTheoryAmtByDto(dto,dto.getTheoryPenaltyInt());
                //实收本息总额 = 实收本息总额+实收罚息总额
                setFpriceAmtByDto(dto,dto.getPenaltyIntamt());
              //未支付罚息总额= 理论罚息总额-实收罚息总额
                BigDecimal theoryPenaltyInt = ArithmeticUtil.subtract(dto.getTheoryPenaltyInt(), dto.getPenaltyIntamt());
                if(BigDecimal.ZERO.compareTo(theoryPenaltyInt)>0){
                	theoryPenaltyInt=BigDecimal.ZERO;
                }
                dto.setTheoryPenaltyInt(theoryPenaltyInt);
            }
        }
        return retPage;
    }
    /** @MethodName: intertstDetailFindPage
    * @Description: TODO 收息明细信息
    * @Param: [params]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.period.trdsecoverdueduration.dto.TrdSecOverdueDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/6 0006
    **/
    public Page<TrdSecOverdueDuration001Dto> intertstDetailFindPage(Map<String, Object> params) {
        setParams(params);
        Page<TrdSecOverdueDuration001Dto> retPage= restTemplate.postForPage(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/intertstDetailFindPage", params,
                TrdSecOverdueDuration001Dto.class);
        if(CollectionUtils.isNotEmpty(retPage.getRoot())){
            for (TrdSecOverdueDuration001Dto dto:retPage.getRoot()) {
                if(StringUtil.isNotBlank(dto.getTradeStatus())&&StringUtil.equals(dto.getTradeStatus(),DictCodeListExt1.交易流程状态_有效.getCodeName())){
                    dto.setIsRevoke(DictCodeList.是否_是.getValue());
                    break;
                }
            }
            for (TrdSecOverdueDuration001Dto dto:retPage.getRoot()) {
                if(StringUtil.isNotBlank(dto.getIsRevoke())&&StringUtil.equals(dto.getIsRevoke(),DictCodeList.是否_是.getValue())){
                    dto.setResidueAmount(dto.getResidueAmount());
                }else{
                	dto.setResidueAmount(null);
                }
            }
        }
        return retPage;
    }


    private static void setBranchFromBranchType(TrdSecOverdueDuration001Dto dto){
        if(StringUtil.isNoneBlank(dto.getBranchType())){
            List<String> list = new ArrayList<>();
            List<String> list2 = new ArrayList<>();
            String[] split = dto.getBranchType().split(",");
            String[] split2 = dto.getBranchAll().split(",");
            if(split.length>0){
                TreeSet<String> hset = new TreeSet<String>(Arrays.asList(split));
                Iterator i = hset.iterator();
                while(i.hasNext()){
                    list.add(i.next().toString());
                }
            }
            if(split2.length>0){
                TreeSet<String> hset = new TreeSet<String> (Arrays.asList(split2));
                Iterator i = hset.iterator();
                while(i.hasNext()){
                    list2.add(i.next().toString());
                }
            }
            if(list.size()>0&&list2.size()>0){
                for (int i=0;i<list.size();i++) {
                    if(StringUtil.equals(list.get(i), DictCodeList.分支类型_应收本金.getValue())){
                        dto.setBjBranch(Long.valueOf(list2.get(i)));
                    }else if(StringUtil.equals(list.get(i), DictCodeList.分支类型_应收利息.getValue())){
                        dto.setLiBranch(Long.valueOf(list2.get(i)));
                    }
                }
            }
        }
    }

    private static void setpenaltyBaseTypeByPage(TrdSecOverdueDuration001Dto dto){
        List<String> list = new ArrayList<>();
        if(StringUtil.isNoneBlank(dto.getPenaltyBaseType())) {
            String[] split = dto.getPenaltyBaseType().split(",");
            if (split.length > 0) {
                TreeSet<String> hset = new TreeSet<String>(Arrays.asList(split));
                Iterator i = hset.iterator();
                while (i.hasNext()) {
                    list.add(i.next().toString());
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list)){
            dto.setPenaltyBaseType(list.get(0));
        }
    }

    private static void setFxYieldByPage(TrdSecOverdueDuration001Dto dto){
        List<String> list = new ArrayList<>();
        if(StringUtil.isNoneBlank(dto.getFxYield())) {
            String[] split = dto.getFxYield().split(",");
            if (split.length > 0) {
                TreeSet<String> hset = new TreeSet<String>(Arrays.asList(split));
                Iterator i = hset.iterator();
                while (i.hasNext()) {
                    list.add(i.next().toString());
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list)){
            dto.setFxYield(list.get(0));
        }
    }
    private static void setFxMdateByPage(TrdSecOverdueDuration001Dto dto){
        List<String> list = new ArrayList<>();
        if(StringUtil.isNoneBlank(dto.getFxmdateStr())) {
            String[] split = dto.getFxmdateStr().split(",");
            if (split.length > 0) {
                TreeSet<String> hset = new TreeSet<String>(Arrays.asList(split));
                Iterator i = hset.iterator();
                while (i.hasNext()) {
                    list.add(i.next().toString());
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list)){
            dto.setFxmdate(DateUtil.parse(list.get(0)));
        }
    }
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        Map<String, Object> params = exportParam.getSelectParam();
        String exportXmlName = "";
        if(StringUtil.equals(params.get("exportType").toString(),DictCodeList.是否_否.getValue())){//未结清
            exportXmlName= "/com/joyintech/tams/invest/period/trdsecoverdueduration/config/export_conf_no.xml";
        }else{//已结清
            exportXmlName="/com/joyintech/tams/invest/period/trdsecoverdueduration/config/export_conf_yes.xml";
        }
        exportParam.config(
               exportXmlName).dataSource(
                DataSourceType.MASERT);
    }


    /**
     * 获取资产代码
     * <功能详细描述>
     * @return 资产代码
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodMarketId() {
        Map<String, Object> params = RequestUtil.getPageParam();
        if(params.containsKey("finprodType2")&&StringUtil.isNotBlank(params.get("finprodType2").toString())){
            List<String> list = new ArrayList<>();
            String[] finprodType2s = params.get("finprodType2").toString().split(",");
            for (String str:finprodType2s) {
                list.add(str);
            }
            params.put("finprodTypeList",list);
        }
        return restTemplate.postForPage(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/getFinprodMarketId",
                params, SelectOptionDto.class);
    }
}
