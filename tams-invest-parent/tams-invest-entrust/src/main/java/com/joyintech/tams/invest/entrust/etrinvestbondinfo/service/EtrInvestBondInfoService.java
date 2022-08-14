/*
 * 文件名：EtrInvestBondInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrinvestbondinfo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.calculation.CalculationException;
import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.AllocationType;
import com.joyintech.calculation.bonds.constant.Basis;
import com.joyintech.calculation.bonds.constant.CurrIntCalMethod;
import com.joyintech.calculation.bonds.constant.DiscountScheme;
import com.joyintech.calculation.bonds.constant.FlowCashScheme;
import com.joyintech.calculation.bonds.constant.IntFrequency;
import com.joyintech.calculation.bonds.constant.IntRateType;
import com.joyintech.calculation.bonds.constant.NextIntCalMethod;
import com.joyintech.calculation.bonds.constant.SecIntSchCalMethod;
import com.joyintech.calculation.bonds.constant.Variety;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.PriceYieldInParameter;
import com.joyintech.calculation.shell.param.PriceYieldOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.shell.param.YieldPriceInParameter;
import com.joyintech.calculation.shell.param.YieldPriceOutParameter;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.StlSecAcctInfoMapper;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.StlSecAcctInfo;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.FinCashPlanDto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.SecInfoModelDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrInvestPersonInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;


/**
 * 债券投资委托画面端service 〈功能详细描述〉
 *
 * @author xiexinting
 * @version 1.0 2018年9月10日
 * @see EtrInvestBondInfoService
 * @since 1.0
 */
@Service
public class EtrInvestBondInfoService extends CommonExportService<EtrInvestBondInfoDto> implements BaseProcessService<EtrInvestBondInfoDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(EtrInvestBondInfoService.class);

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;

    /**
     * 共通Mapper的通用代理方案
     */
    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    
    /**
     *
     */
    @Autowired
    private EtrCommonService etrCommonService;

    /**
     * 债券投资委托查询 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrInvestBondInfoDto> selectForPage(Map<String, Object> param) {
//        if (StringUtil.isNotEmpty((String)param.get("entrustDate"))) {
//            List<String> dateList = StringUtil.splitToList(" ", (String)param.get("entrustDate"));
//            param.put("startDate", dateList.get(0));
//            param.put("endDate", dateList.get(2));
//        }
		/*更改债券以及分类为资产二级分类 王贵奇 2019/7/26
		 * String bondType =(String)param.get("bondType");
		 * if(StringUtil.isNotEmpty(bondType)){ List<String> bondTypeList =
		 * StringUtil.splitToList(",", bondType); param.put("bondTypeList",
		 * bondTypeList); }
		 */
        //	新增债券二、三、四级分类 	2019/7/26	王贵奇	开始
        if (StringUtil.isNotEmpty(param.get("type9"))) {
            param.put("type9List", StringUtil.splitToList(",", param.get("type9").toString()));
        }
        if (StringUtil.isNotEmpty(param.get("type10"))) {
        	param.put("type10List", StringUtil.splitToList(",", param.get("type10").toString()));
        }
        if (StringUtil.isNotEmpty(param.get("type11"))) {
        	param.put("type11List", StringUtil.splitToList(",", param.get("type11").toString()));
        }
        //	新增债券二、三、四级分类	2019/7/26	王贵奇	结束
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        //配置单位 edit by yeming 20210730 start
        Page<EtrInvestBondInfoDto> page = restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/findPage", param,
                EtrInvestBondInfoDto.class);
        for(EtrInvestBondInfoDto dto:page.getRoot()){
            if(dto.getEntrustNum() != null) {
                dto.setEntrustNum(dto.getEntrustNum().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
            if(dto.getEntrustOwnNum() != null) {
                dto.setEntrustOwnNum(dto.getEntrustOwnNum().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
            if(dto.getEntrustIntegrateNum() != null) {
                dto.setEntrustIntegrateNum(dto.getEntrustIntegrateNum().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
        }
        return page;
        //配置单位 edit by yeming 20210730 end
    }

    /**
     * 获取组合代码的联想文本框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getentrustId(String param) {
        User user = SecurityUtil.getLoginUser();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserName());
        map.put("param", param);
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrInvestBondInfo/getentrustId" , map,
            SuggestDto.class);
    }

    /**
     * 获取资产代码的联想文本框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getfinprodId(String param, String pageId) {
        return restTemplate.getForList(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrInvestBondInfo/getfinprodId?finprodId=" + param + "&pageId="+pageId,
            SuggestDto.class);

    }

    /**
     * 获取新建界面的组合代码联想下拉框框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getentrustIdAdd(Map<String, Object> param) {
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrInvestBondInfo/getentrustIdAdd"
                                       , param,
            SelectOptionDto.class);
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrInvestBondInfo/getfinprodIdForAdd"
                                       , param,
            SelectOptionDto.class);

    }
    /**
     * 判断是否含权债
     *
     * @param finprodId 金融产品代码
     * @see
     * @since 1.0
     */
    public boolean isRight(String finprodId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodId", finprodId);

        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isRight", paramMap,
                Boolean.class);
    }
    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrInvestBondInfoDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/check", dto,
            ValidMessage.class);
    }

    /**
     * 工作流完成方法
     */
    @Override
    public void doComplete(EtrInvestBondInfoDto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate = DateUtil.platDateFromCache();
        dto.setEntrustDate(sysDate);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/doComplete",dto,
            Integer.class);
    }
    /**
     *
     * 流程发起时调用
     */
     @Override
     public void doInsert(EtrInvestBondInfoDto dto, OperationType type){
         // OperationType枚举值与dto的operateType不同,后续逻辑使用的是dto的operateType,所以不能覆盖
         // ZGZGST-1502:fuyongsheng 20210825 delete start
         // dto.setOperateType(type.toString());
         // ZGZGST-1502:fuyongsheng 20210825 delete end
         restTemplate.postForObject(
             ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/doInsert",dto,
             Integer.class);
     }

     /**
      *
      * 流程修改时调用
      */
     @Override
     public void doUpdate(EtrInvestBondInfoDto dto){
         restTemplate.postForObject(
             ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/doInsert",dto,
             Integer.class);
     }

    @Override
    public void doBack(EtrInvestBondInfoDto entity) {
        // ZGZGST-1557:退回工作流 fuyongsheng add start
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/doBack", entity,
                Integer.class);
        // ZGZGST-1557:退回工作流 fuyongsheng add end
    }

    /**
     *
     * 删除投资委托
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/delete?entrustId="+entrustId,
            boolean.class);
    }
    /**
     * 修改/双击查看详情查询信息 <功能详细描述>
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrInvestBondInfoDto selectByKey(String entrustId,String finprodId,String portfolioId) {
        if(!StringUtil.isNotEmpty(entrustId)){
            entrustId="";
        }
        if(!StringUtil.isNotEmpty(finprodId)){
            finprodId="";
        }
        if(!StringUtil.isNotEmpty(portfolioId)){
            portfolioId="";
        }
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                                         + "/EtrInvestBondInfo/selectByKey?entrustId=" + entrustId+"&finprodId="+finprodId+"&portfolioId="+portfolioId,
            EtrInvestBondInfoDto.class);
    }


    /**
     * 撤回委托 <功能详细描述>
     *
     * @param dto 实体类
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrInvestBondInfoDto dto) {
        return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/recall", dto,
            boolean.class);
    }
    /**
     * 关闭委托
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId,String closeWay) {
        return restTemplate.getForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/close?entrustId="+entrustId+"&closeWay="+closeWay,
            boolean.class);
    }

    /**
     * 导出
     *
     * @param exportParam 导出信息
     * @see
     * @since 1.0
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/entrust/etrinvestbondinfo/conf/export_conf.xml")
            .dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<EtrInvestBondInfoDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName,"transactioncom")){
            cell.setCellValue(value+"%");
        }
    }
    /**
     * 可用面值计算
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getAvailableNum(String finprodId) {
        Date sysDate = DateUtil.platDateFromCache();
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("sysdate", DateUtil.format(sysDate));
        return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/getAvailableNum",map,
            BigDecimal.class);
    }

    /**
     * 组合可用现金查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate",DateUtil.format(sysDate));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(sysDate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(sysDate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate)), 1, holidayList, "1");
            param.put("sysDateT1",DateUtil.format(tradeDay));
        }
        Map<String, Object> cash = etrCommonService.getProAvailableCash(param);
//                restTemplate.postForObject(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/getProAvailableCash",param,
//            Map.class);
         return cash;
    }

    /**
     * 组合可用持仓查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getAssetAvailablePosition(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        Date sysDate_1 = null;
        param.put("sysDate",DateUtil.format(sysDate));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(sysDate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(sysDate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate)), 1, holidayList, "1");
            sysDate_1 = tradeDay;
        }
        EtrSecPositionDto dto1= new EtrSecPositionDto();
        dto1.setCdate(sysDate);
        dto1.setPortfolioId((String)param.get("portfolioId"));
        dto1.setFinprodId((String)param.get("finprodId"));
        dto1.setCcy((String)param.get("ccy"));
        dto1.setSecAcctId((String)param.get("secAcctId"));
        if(StringUtil.isEmpty(dto1.getSecAcctId())){
            dto1.setSecAcctId((String)param.get("chlId"));
        }
        dto1.setIsContain9((String)param.get("isContain9"));
        dto1.setInvAim((String)param.get("invAim"));
        dto1.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
//        var data = {portfolioId:portfolioId1,calId:$('#calendarId').val(),finprodId:$('#finprodIdhid').val(),ccy:$('#ccy').val(), secAcctId: $("#secManageAcctId").val(), chlId: $('#chlFinprodId').val()}
//        // 一体化对应etliu start
//        data.invAim = $('#invAim').val();
        Map<String,Object>ret = new HashMap<>();
        ret.put("position",etrCommonService.getAssetAvailablePosition(dto1));
        if(StringUtil.isNotEmpty(calId)&&sysDate_1!=null){
            dto1.setCdate(sysDate_1);
            ret.put("positionT1",etrCommonService.getAssetAvailablePosition(dto1));
        }
        return ret;
//        return restTemplate.postForObject(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/getAssetAvailablePosition",param,
//            Map.class);
    }
    /**
     *
     * 查询授权额度
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectLimitAmt(EtrInvestBondInfoDto dto){
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", DictCodeList.平安资产类型_债券.getValue());
        map.put("assetSecType", dto.getAssetType1());
        map.put("tradeMarket", dto.getTradeMarket());
        map.put("ps",dto.getPs());
        return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/common/SelectLimitAmt/selectLimitAmt", map,
            BigDecimal.class);
    }
    
    /**
     * 
     * 净价、行权收益率、到期收益率联动
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    public void calcAmt(EtrInvestBondInfoDto dto) {
//        if(platformDate!=null){
//            dto.setSettleDate(platformDate.getSysDate());
//        }
        if (dto == null || dto.getVdate() == null || dto.getMdate() == null 
            || DateUtil.compareDate(dto.getVdate(), dto.getMdate()) != 0) {
            EtrInvestBondInfoDto nullDto = new EtrInvestBondInfoDto();
            try {
            	nullDto.setPrice(dto.getPrice());
            	nullDto.setExerYield(dto.getExerYield());
            	nullDto.setMatureYield(dto.getMatureYield());
                BeanUtil.copyProperties(dto, nullDto);
            }
            catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            return;
        }
        dto.setSettleDate(dto.getVdate());
        
        /*if(StringUtil.equals(DictCodeList.清算速度_T_1.getValue(), dto.getSettleSpreed())){
        	List<String> holiday = dateCalService.getDealCalendarHoliday(dto.getVdate(),
        			DateUtil.addDays(dto.getVdate(), 31),dto.getCalendarId());
        	Date tradingDay = dateCalService.calculationTradingDay(dto.getVdate(), 1, holiday, "1");
        	dto.setSettleDate(tradingDay);
        }*/
        //update by zhangnian 20200212  start  债券二级指令改造
        if(StringUtil.isNotEmpty(dto.getSettleSpreed())&&!StringUtils.equals(dto.getSettleSpreed(),"null")){
            if(Integer.parseInt(dto.getSettleSpreed())!=0){
                List<String> holiday = dateCalService.getDealCalendarHoliday(dto.getVdate(),
                        DateUtil.addDays(dto.getVdate(), 31),dto.getCalendarId());
                Date tradingDay = dateCalService.calculationTradingDay(dto.getVdate(), Integer.parseInt(dto.getSettleSpreed()), holiday, "1");
                dto.setSettleDate(tradingDay);
            }
        }
        //update by zhangnian 20200212  end
        if (DateUtil.compareDate(dto.getSettleDate(), dto.getFinvdate()) < 0) {
            dto.setSettleDate(dto.getFinvdate());
        }

        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = true;
        if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
            && !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket())) {
            // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量
            this.getUnitIntFromValution(dto);
            isJiaoYiSuo = false;
        }

//        dto.setPrinAmt(dto.getTradeAmt());

        final int SCALE = 12;

        //到期收益率
        dto.setMatureYield(ArithmeticUtil.divide(dto.getMatureYield(), 100, 14));
        //行权收益率
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), 100, 14));

        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = null;
        PriceYieldInParameter priceYieldInParameter = null;
        YieldPriceOutParameter yieldPriceOutParameter = null;
        PriceYieldOutParameter priceYieldOutParameter = null;

        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        tempParamMap.put("finprodId", dto.getFinprodId());
        tempParamMap.put("ps", dto.getPs());
        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
//         List<BondsIntReset> bondsIntResetList = transactionBond001Mapper.getInvRateQuo(dto);
        List<FinCashCalcRule> finCashCalcRuleList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectBondsIntResetList",
            tempParamMap, FinCashCalcRule.class);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinCashPlan",
            tempParamMap, FinCashPlanDto.class);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                DictCodeListExt1.现金流一级类型_利息.getValue())
                && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);
                
                intSchList.add(item);
            }
        }

        // d.获取债券基本资料信息
        SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel",
            tempParamMap, SecInfoModelDto.class);
        SecInfoModel secInfoModel = this.toSecInfoModel(secInfoModelDto);
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 付息债的计息基础使用A_365F
            if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
                secInfoModel.setBasis(Basis.A_365F);
            }
        }

        // e.获取债券的还本计划信息
        List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(
                    StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }

//        List<PaperIrReset> paperIrResetList = bondsIntResetList.stream().map(item -> {
//            PaperIrReset ret = new PaperIrReset();
//            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
//            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getBaseRateEffDate()));
//            ret.setParRate(item.getParRate());
//            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getParRateEffDate()));
//            return ret;
//        }).collect(Collectors.toList());
//        if (paperIrResetList != null && paperIrResetList.size() > 0) {
//            paperIrResetList.get(0).setBaseRateEffDate(
//                BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
//        }

        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(
                BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
        	//多重利率则计算按浮动利率算
        	secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        yieldPriceInParameter = new YieldPriceInParameter();
        yieldPriceInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        priceYieldInParameter = new PriceYieldInParameter();
        priceYieldInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        priceYieldInParameter.setSecInfoModel(secInfoModel);
        priceYieldInParameter.setSecIntScheduleList(invScheduleList);
        priceYieldInParameter.setSecPrinScheduleList(secPrinScheduleList);
        priceYieldInParameter.setPaperIrResetList(paperIrResetList);
        yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
        	if (isNotFomartSec(intSchList, secInfoModelDto)) {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                priceYieldInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
        	} else {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        	}
        }
        yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());
        priceYieldInParameter.setCalMethod(secInfoModel.getCalMethod());
        // 后续区间利息计算方案
        if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
        } else {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
        }
        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        // 交易方式为“一级市场”时，单位应计利息=应计利息总额=0
        if (StringUtil.equals(dto.getTradeMarketMode(),
            DictCodeListExt1.交易市场_一级市场.getValue())) {
            dto.setIntAmt(BigDecimal.ZERO);
            dto.setUnitInt(accurInt);
        }
        // 交易方式为“二级市场”时，单位应计利息 =e取得的百元面值应计利息/100*a取得的当前债券面额
        else if (StringUtil.equals(dto.getTradeMarketMode(),
            DictCodeListExt1.交易市场_二级市场.getValue())) {
            // a.当前债券面额取得（根据还本计划计算）
            // BigDecimal curSecAmt = transactionBond001Mapper.getCurSecAmt(dto);
            // accurInt = ArithmeticUtil.round(BondsCalculation.calAccurInt(dto.getSettDate(),
            // bondsInfo,
            // bondsScheduleList, bondsIntResetList), SCALE, RoundingMode.HALF_UP);
            // 单位应计利息
            // if (dto.getUnitInt() == null) {
            // dto.setUnitInt(ArithmeticUtil.doMul(
            // ArithmeticUtil.doDiv(accurInt, new BigDecimal(100), SCALE), curSecAmt));
            // }
            // // 应计利息总额 = 单位应计利息 * 券面总额/100
            // dto.setIntAmt(ArithmeticUtil.doDiv(ArithmeticUtil.doMul(accurInt, dto.getPrinAmt()),
            // new BigDecimal(100), SCALE));
            
          //交易所债券特殊处理start----------------------------------------------------------------
            Date tempSettleDate = dto.getSettleDate();
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                // 付息债、贴现债、零息债，交割日+1
//                if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())
//                    || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(), secInfoModelDto.getBondsType())
//                    || StringUtil.equals(DictCodeList.息票品种_零息.getValue(), secInfoModelDto.getBondsType())) {
                if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())) {
                    tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
                }
//                }
            }
          //交易所债券特殊处理end----------------------------------------------------------------

            // a.当前债券面额取得（根据还本计划计算）
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("finprodId", dto.getFinprodId());
//            paraMap.put("settleDate", DateUtil.format(dto.getSettleDate()));
            paraMap.put("settleDate", DateUtil.format(tempSettleDate));
            BigDecimal curSecAmt = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCurSecAmt",
                                                              paraMap, BigDecimal.class);

            AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
            // 计算日
//            accurIntCalInParameter.setCalDate(this.toLocalDate(dto.getSettleDate()));
            accurIntCalInParameter.setCalDate(this.toLocalDate(tempSettleDate));
            // 债券基本资料
            accurIntCalInParameter.setSecInfoModel(secInfoModel);
            // 付息计划
            accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
            // 利率重置信息
            accurIntCalInParameter.setPaperIrResetList(paperIrResetList);
            
            //交易所债券特殊处理start----------------------------------------------------------------
            AccurIntCalOutParameter out = null;
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                // 付息债、贴现债、零息债，交割日+1
//                if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())
//                    || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(), secInfoModelDto.getBondsType())
//                    || StringUtil.equals(DictCodeList.息票品种_零息.getValue(), secInfoModelDto.getBondsType())) {
                    // 付息计划截止日对应的利息
                    Map<String, BigDecimal> intMap = new HashMap<>();
                    if(null != invScheduleList && !invScheduleList.isEmpty()) {
                        for(SecIntSchedule intSche : invScheduleList) {
                            intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                        }
                    }
                    
                    if(intMap.containsKey(accurIntCalInParameter.getCalDate().toString()) && 
                            !((Variety.贴现.equals(secInfoModel.getBondsType())) || Variety.零息.equals(secInfoModel.getBondsType()))) {
                        out = new AccurIntCalOutParameter();
                        out.setInterest(intMap.get(accurIntCalInParameter.getCalDate().toString()).multiply(new BigDecimal("100")
                            ).divide(curSecAmt, 12, RoundingMode.HALF_UP));
                        accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                        AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                        if (accurIntCalInParameter.getPaperIrResetList() != null && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                            accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                        }
                        out = calculator2.calculate(accurIntCalInParameter);
                    }
//                }
            }
            //交易所债券特殊处理end----------------------------------------------------------------
            
            if (out != null) {
                accurInt = ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
            }
            else {
                // 从计算工厂中创建应计利息计算器
                AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
                // 计算利息
                AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
                accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
            }

            // 单位应计利息
            if (dto.getUnitInt() == null) {
                if (isJiaoYiSuo) {
                    dto.setUnitInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")), RoundingMode.HALF_UP));
                }
                else {
                    dto.setUnitInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_BANK")), RoundingMode.HALF_UP));
                }
            }
//            // 应计利息总额 = 单位应计利息 * 券面总额/100
//            dto.setIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitInt(), dto.getPrinAmt()),
//                new BigDecimal(100), SCALE));
        }
        try {
            // 单位净价
            if (StringUtil.equals(dto.getCalflag(), "price")) {
                // 单位全价 = 单位净价 + 单位应计利息
                dto.setUnitFprice(ArithmeticUtil.doAdd(dto.getPrice(), dto.getUnitInt()));
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getEntrustNum()),
                    new BigDecimal(100), SCALE));
//                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
            }
//            // 全价总额
//            else if (StringUtil.equals(dto.getCalculateField(), "fpriceAmt")) {
//                // 单位全价 = 全价总额 * 100 / 券面总额
//                dto.setUnitFprice(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getFpriceAmt(), new BigDecimal(100)),
//                    dto.getPrinAmt(), SCALE));
//                // 单位净价 = 单位全价 - 单位应计利息
//                dto.setUnitCprice(ArithmeticUtil.doSub(dto.getUnitFprice(), dto.getUnitInt()));
//                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
//            }
            switch (dto.getCalflag()) {
                // 单位净价
                case "price":
                    // 全价总额
                case "fpriceAmt":
                    // 全价总额==>到期收益率
                    yieldPriceInParameter.setFullPrice(
                        dto.getUnitFprice()/* dto.getFpriceAmt() */);
                    yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                        yieldPriceInParameter);
                    dto.setMatureYield(yieldPriceOutParameter.getMatYield());
                    if (secInfoModelDto.getNextExeDate() != null) {
                        // 全价总额==>行权收益率
                        yieldPriceInParameter.setNextExeDate(
                            toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                            secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getExeYield());
                        }
                        else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 到期收益率
                case "matureYield":
                    // 到期收益率==>全价总额
                    priceYieldInParameter.setMatYield(dto.getMatureYield());
                    priceYieldOutParameter = priceByYieldCalculator.calculate(
                        priceYieldInParameter);
                    // dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                    dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                    // 全价总额==>行权收益率
                    if (secInfoModelDto.getNextExeDate() != null) {
                        yieldPriceInParameter.setFullPrice(
                            dto.getUnitFprice()/* dto.getFpriceAmt() */);
                        yieldPriceInParameter.setNextExeDate(
                            toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                            secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getMatYield());
                        }
                        else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 行权收益率
                case "exerYield":
                    // 行权收益率==>全价总额
                    if (secInfoModelDto.getNextExeDate() != null) {
//                        priceYieldInParameter.setMatYield(dto.getMatureYield());
                        priceYieldInParameter.setMatYield(dto.getExerYield());//行权收益
                        yieldPriceInParameter.setNextExeDate(
                            toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                            secInfoModelDto.getNextExercisePrice());
                        priceYieldOutParameter = priceByYieldCalculator.calculate(
                            priceYieldInParameter);
                        dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                        dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                        // 全价总额==>到期收益率
                        yieldPriceInParameter.setFullPrice(dto.getFpriceAmt());
                        yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                            yieldPriceInParameter);
                        dto.setMatureYield(yieldPriceOutParameter.getMatYield());
                    }
                    break;
                default:
                    break;
            }
            // 到期收益率 或 行权收益率
            if (StringUtil.equals(dto.getCalflag(), "matureYield")
                || StringUtil.equals(dto.getCalflag(), "exerYield")) {
                // 单位全价 = 全价总额 * 100 / 券面总额
                // dto.setUnitFprice(ArithmeticUtil.doDiv(
                // ArithmeticUtil.doMul(dto.getFpriceAmt(), new BigDecimal(100)),
                // dto.getPrinAmt(), 12));
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getEntrustNum()),
                    new BigDecimal(100), SCALE));
                // 单位净价 = 单位全价 - 单位应计利息
                dto.setPrice(ArithmeticUtil.doSub(dto.getUnitFprice(), dto.getUnitInt()));
                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

        if (dto.getMatureYield() != null) {
            dto.setMatureYield(ArithmeticUtil.multiply(dto.getMatureYield(), new BigDecimal(100)));
        }
        if (dto.getExerYield() != null) {
            dto.setExerYield(ArithmeticUtil.multiply(dto.getExerYield(), new BigDecimal(100)));
        }
    }
    
    /**
     * 转换数据
     *
     * @param secInfoModelDto 债券基本信息SecInfoModelDto
     * @return 债券基本信息SecInfoModel
     * @see
     * @since 1.0
     */
    private SecInfoModel toSecInfoModel(SecInfoModelDto secInfoModelDto) {
        SecInfoModel secInfoModel = new SecInfoModel();

        secInfoModel.setStartDate(toLocalDate(secInfoModelDto.getStartDate()));
        secInfoModel.setEndDate(toLocalDate(secInfoModelDto.getEndDate()));
        secInfoModel.setFirstPayDate(toLocalDate(secInfoModelDto.getFirstPayDate()));
        // 利率类型
        if (StringUtil.equals(DictCodeList.利率类型_固定利率.getValue(),
            secInfoModelDto.getIntRateType())) {
            secInfoModel.setIntRateType(IntRateType.固定利率);
        }
        else {
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }

        if (StringUtil.isNotBlank(secInfoModelDto.getBasis())) {
            String basis = StringUtil.splitToList(GlobalConstant.COMMA, secInfoModelDto.getBasis()).get(0);
            DictCodeList basisDict = DictCodeList.from(DictCode.计息基础, basis);
            if (basisDict != null){
                switch (basisDict) {
                    case 计息基础_30_360:
                        secInfoModel.setBasis(Basis._30_360);
                        break;
                    case 计息基础_A_360:
                        secInfoModel.setBasis(Basis.A_360);
                        break;
                    case 计息基础_A_365:
                        secInfoModel.setBasis(Basis.A_365);
                        break;
                    case 计息基础_A_365F:
                        secInfoModel.setBasis(Basis.A_365F);
                        break;
                    case 计息基础_A_A:
                    case 计息基础_A_A_平均分配:
                    case 计息基础_A_A_按实际天数分配:
                        secInfoModel.setBasis(Basis.A_A);
                        break;
                }
                if (basisDict.equals(DictCodeList.计息基础_A_A_平均分配)) {
                    secInfoModel.setAllocationType(AllocationType.平均分配);
                }
                else {
                    secInfoModel.setAllocationType(AllocationType.按实际天数分配);
                }
            }else {
                DictCodeListExt3 basisDict3 = DictCodeListExt3.from(DictCode.计息基础, basis);
                if (basisDict3 != null && basisDict3.equals(DictCodeListExt3.计息基础_30U_360)){
                    secInfoModel.setBasis(Basis._30U_360);
                }
            }
        }
        DictCodeList bondsTypeDict = DictCodeList.from(DictCode.息票品种,
            secInfoModelDto.getBondsType());
        switch (bondsTypeDict) {
            case 息票品种_零息:
                secInfoModel.setBondsType(Variety.零息);
                break;
            case 息票品种_利随本清:
                secInfoModel.setBondsType(Variety.利随本清);
                break;
            case 息票品种_贴现:
                secInfoModel.setBondsType(Variety.贴现);
                break;
            case 息票品种_附息:
                secInfoModel.setBondsType(Variety.附息);
                break;
        }

        // 一体化对应etliu start
//        if (StringUtil.equals(DictCodeList.推算方法_起息日向后.getValue(),
        if (StringUtil.equals(DictCodeList.推算方式_起息日向后.getValue(),
            secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.起息日向后);
        }
//        else if (StringUtil.equals(DictCodeList.推算方法_到期日向前.getValue(),
        else if (StringUtil.equals(DictCodeList.推算方式_到期日向前.getValue(),
            secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.到期日向前);
        }
        else {
            secInfoModel.setCalMethod(SecIntSchCalMethod.首次付息日);
        }

//        if (StringUtil.equals(DictCodeList.支付频率_付息频率_月付.getValue(),
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
            secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.月付);
        }
//        else if (StringUtil.equals(DictCodeList.支付频率_付息频率_季付.getValue(),
        else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
            secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.季付);
        }
//        else if (StringUtil.equals(DictCodeList.支付频率_付息频率_半年付.getValue(),
        else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
        // 一体化对应etliu end
            secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.半年付);
        }
        else {
            secInfoModel.setYearIntRate(IntFrequency.年付);
        }

        secInfoModel.setIssuePrice(secInfoModelDto.getIssuePrice());

        return secInfoModel;
    }
    
    /**
     * 转换数据
     *
     * @param date 日期
     * @return LocalDate
     * @see
     * @since 1.0
     */
    private LocalDate toLocalDate(Date date) {
        if (date != null) {
            return LocalDate.parse(DateUtil.format(date));
        }
        return null;
    }
    
    /**
     * 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量，
     * 如取不到估值核算中前一个自然日的数据，计算方式见算法文档；
     * 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT
     * （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
     *
     * @param dto 参数
     * @return 应计利息
     * @see
     * @since 1.0
     */
    private void getUnitIntFromValution(EtrInvestBondInfoDto dto) {
        // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量，如取不到估值核算中前一个自然日的数据，计算方式见算法文档；
        // 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT  （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
        Map<String, Object> paraMapForIntAmt = new HashMap<String, Object>();
        paraMapForIntAmt.put("settleDate", DateUtil.format(dto.getSettleDate()));
        paraMapForIntAmt.put("portfolioId", dto.getPortfolioId());
        paraMapForIntAmt.put("finprodId", dto.getFinprodId());
        paraMapForIntAmt.put("invAim", dto.getInvAim());
        paraMapForIntAmt.put("secAcctId", dto.getChlId());
        BigDecimal 应计利息 = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getIntAmtFromValution",
            paraMapForIntAmt, BigDecimal.class);
        if (应计利息 != null) {
            dto.setUnitInt(应计利息);
        }
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrInvestBondInfoDto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
                BigDecimal amount = dto.getEntrustNum();
                if (amount == null) {
                    amount = dto.getEntrustAmt();
                }
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getEntrustNum(), LoginUserUtil.getUserName(),
                        BussinessDictCodeList.平安资产类型_债券.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                        , dto.getFinprodId(), entity.getWorkflowId()
                        );
                return commonBusinessService.是否需要上级审批_委托用(params);
            } catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }else {
            return null;
        }
    }

    public boolean isNotFomartSec(List<FinCashPlanDto> secIntScheduleList, SecInfoModelDto secInfoModelDto) {
        // 根据【实际起始日】排序付息计划
        Collections.sort(secIntScheduleList, new Comparator<FinCashPlanDto>() {
            @Override
            public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                return o1.getVdateUnadjust().compareTo(o2.getVdateUnadjust());
            }
        });
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
        	for (FinCashPlanDto she : secIntScheduleList) {
        		Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 1);
        		if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
        			return false;
        		}
        	}
        }
        else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
            secInfoModelDto.getYearIntRate())) {
        	for (FinCashPlanDto she : secIntScheduleList) {
        		Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 3);
        		if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
        			return false;
        		}
        	}
        }
        else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
            secInfoModelDto.getYearIntRate())) {
        	for (FinCashPlanDto she : secIntScheduleList) {
        		Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 6);
        		if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
        			return false;
        		}
        	}
        }
        else {
        	for (FinCashPlanDto she : secIntScheduleList) {
        		Date mDate = DateUtil.addYear(she.getVdateUnadjust(), 1);
        		if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
        			return false;
        		}
        	}
        }
        return true;
    }

    /**
     * {@linkplain}
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    //获取交易所资金账户可用现金	王贵奇	2019/8/26
	public BigDecimal getSecManageAvailableCash(Map<String, Object> pageParam) {
		StlSecAcctInfo stlSecAcctInfo = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                StlSecAcctInfoMapper.class, (String)pageParam.get("secManageAcctId"), StlSecAcctInfo.class);
		Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("portfolioId", (String)pageParam.get("portfolioId"));
        paramMap.put("acctId", stlSecAcctInfo.getTrdAcctId());
        paramMap.put("calendarId", (String)pageParam.get("calendarId"));
		paramMap.put("tradeDate", iPlatformDate.getSysDateStr());
        paramMap.put("ccy", (String)pageParam.get("ccy"));
        //paramMap.put("acctType", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
//        BigDecimal secManageAcctId = bussTrdAvailableCashQueryService.getSecManageAvailableCash(paramMap);
        Map<String, Object> obj = etrCommonService.getProAvailableCash(paramMap);
		return (BigDecimal) obj.get("cashT4");
	}

    /**
     * 查询估值净价
     * @author wangguiqi
     */
	public BigDecimal getClosePriceByPara(Map<String, Object> paramMap) {
		paramMap.put("sysdate", iPlatformDate.getSysDateStr());
		return restTemplate.postForObject(
	            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/getClosePriceByPara", paramMap,
	            BigDecimal.class);
	}

    /**
     * 查询上一日估值净价
     *
     * @author jiangpeng
     */
    public BigDecimal getLastPriceByPara(Map<String, Object> paramMap) {
        if (StringUtil.isEmpty((String) paramMap.get("sysdate"))) {
            paramMap.put("sysdate", iPlatformDate.getSysDateStr());
        }
        String sysdate = (String) paramMap.get("sysdate");
        paramMap.put("sysdate", DateUtil.addDay(sysdate, -1));
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/getLastPriceByPara", paramMap,
                BigDecimal.class);
    }

    /**
     * 导入数据文件
     *
     * @param fileName 导入的文件名
     * @return 导入结果，包含验证内容，如果验证失败，返回message
     */
    public Map<String, Object> importData(String fileName) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("tempList", JSONArray.toJSONString(ExcelUtil.getTempList(fileName)));
        return param;
    }

    /**
     * 获取委托列表数据
     *
     * @return
     */
    public EtrInvestBondInfoDto getEntrustListData(String tempList) {
        EtrInvestBondInfoDto dto = new EtrInvestBondInfoDto();
        // 委托列表
        List<EtrInvestBondInfoDto> etrInvestBondInfoDtoList = new ArrayList<>();
        dto.setSelectedItem(etrInvestBondInfoDtoList);
        JSONArray objects = JSONArray.parseArray(tempList);
        for (int i = 0; i < objects.size(); i++ ) {
            JSONArray rowData = JSONArray.parseArray(((JSONArray)objects.get(i)).toJSONString());
            EtrInvestBondInfoDto etrInvestBondInfoDto = new EtrInvestBondInfoDto();
            etrInvestBondInfoDtoList.add(etrInvestBondInfoDto);
            // 根据模板中固定的下标来赋值
            etrInvestBondInfoDto.setIndexId(rowData.get(0).toString());
            // 委托开始日
            etrInvestBondInfoDto.setVdate(DateUtil.parse(rowData.get(1).toString()));
            // 委托终止日
            etrInvestBondInfoDto.setMdate(DateUtil.parse(rowData.get(2).toString()));
            // 组合代码
            etrInvestBondInfoDto.setPortfolioId(rowData.get(3).toString());
            // 资产代码
            etrInvestBondInfoDto.setFinprodMarketId(rowData.get(4).toString());
            // 交易方向
            etrInvestBondInfoDto.setPs(rowData.get(5).toString().split(":")[0]);
            // 币种
            etrInvestBondInfoDto.setCcy(rowData.get(6).toString().split(":")[0]);
            // 券面总额
            if (StringUtil.isNotBlank(rowData.get(7).toString())) {
                etrInvestBondInfoDto.setEntrustNum(new BigDecimal(rowData.get(7).toString()));
            }
            // 价格模式
            etrInvestBondInfoDto.setPriceModel(rowData.get(8).toString().split(":")[0]);
            // 净价
            if (StringUtil.isNotBlank(rowData.get(9).toString())) {
                etrInvestBondInfoDto.setPrice(new BigDecimal(rowData.get(9).toString()));
            }
            // 行权收益率（%）
            if (StringUtil.isNotBlank(rowData.get(10).toString())) {
                etrInvestBondInfoDto.setExerYield(new BigDecimal(rowData.get(10).toString()));
                etrInvestBondInfoDto.setExerYield(etrInvestBondInfoDto.getExerYield().setScale(2,RoundingMode.HALF_UP));
            }
            // 到期收益率（%）
            if (StringUtil.isNotBlank(rowData.get(11).toString())) {
                etrInvestBondInfoDto.setMatureYield(new BigDecimal(rowData.get(11).toString()));
                etrInvestBondInfoDto.setMatureYield(etrInvestBondInfoDto.getMatureYield().setScale(2,RoundingMode.HALF_UP));
            }
            // 最低成交金额
            if (StringUtil.isNotBlank(rowData.get(12).toString())) {
                etrInvestBondInfoDto.setMinAmt(new BigDecimal(rowData.get(12).toString()));
            }
            // 清算速度
            etrInvestBondInfoDto.setSettleSpreed(rowData.get(13).toString().split(":")[0]);
            // 投资目的
            etrInvestBondInfoDto.setInvAim(rowData.get(14).toString().split(":")[0]);
            // 交易场所
            if (StringUtil.isNotBlank(rowData.get(15).toString())) {
                etrInvestBondInfoDto.setTradeMarket(rowData.get(15).toString().split(":")[0]);
            }
            // 交易市场固定为二级市场
            etrInvestBondInfoDto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            // 交易平台
            if (StringUtil.isNotBlank(rowData.get(16).toString())) {
                etrInvestBondInfoDto.setTradePlat(rowData.get(16).toString().split(":")[0]);
            }
            Map<String, Object> param;
            Page<SelectOptionDto> selectOptionDtoPage;
            if (StringUtil.isNotBlank(etrInvestBondInfoDto.getFinprodMarketId())) {
                SelectOptionDto selectOptionDto = getBondInfoByFinprodMarketId(etrInvestBondInfoDto.getFinprodMarketId());
                if (selectOptionDto != null) {
                    JSONObject jsonObject = JSONObject.parseObject(
                        JSONObject.toJSONString(selectOptionDto.getObject()));
                    etrInvestBondInfoDto.setFinprodAbbr(etrInvestBondInfoDto.getFinprodMarketId()
                                                        + ":" + selectOptionDto.getText());
                    etrInvestBondInfoDto.setTradeMarket(jsonObject.get("tradeMarket").toString());
                    etrInvestBondInfoDto.setCalendarId(jsonObject.get("calendarId").toString());
                    etrInvestBondInfoDto.setAssetType1(jsonObject.get("assetType1").toString());
                    etrInvestBondInfoDto.setAssetType10(jsonObject.get("assetType10").toString());
                    etrInvestBondInfoDto.setAssetType11(jsonObject.get("assetType11").toString());
                    etrInvestBondInfoDto.setFinvdate(new Date((Long)jsonObject.get("finvdate")));
                    etrInvestBondInfoDto.setFinmdate(new Date((Long)jsonObject.get("finmdate")));
                    etrInvestBondInfoDto.setCcy(jsonObject.get("ccy").toString());
                    etrInvestBondInfoDto.setFinprodId(jsonObject.get("finprodId").toString());
                }
            }
            // 证券账户
            if (StringUtil.isNotBlank(rowData.get(17).toString())) {
                etrInvestBondInfoDto.setSecAcctId(rowData.get(17).toString());
                param = new HashMap<>();
                param.put("initValue", rowData.get(17).toString());
                param.put("portfolioId", etrInvestBondInfoDto.getPortfolioId());
                param.put("tradeMarket", etrInvestBondInfoDto.getTradeMarket());
                param.put("page", 1);
                param.put("length", 20);
                List<SelectOptionDto> selectOptionDtoList = this.getSecManageAcctId(param);
                if (!StringUtil.isEmptyList(selectOptionDtoList)) {
                    etrInvestBondInfoDto.setSecManageAcctIdName(
                        selectOptionDtoList.get(0).getText());
                }
            }
            // 通道
            if (StringUtil.isNotBlank(rowData.get(18).toString())) {
                etrInvestBondInfoDto.setChlId(rowData.get(18).toString());
                param = new HashMap<>();
                param.put("initValue", rowData.get(18).toString());
                param.put("page", 1);
                param.put("length", 20);
                selectOptionDtoPage = restTemplate.postForPage(
                    ServiceApplication.COMMON_SERVICE_NAME + "/common/BussChannelCommonService/getChannelId",
                    param, SelectOptionDto.class);
                if (!StringUtil.isEmptyList(selectOptionDtoPage.getRoot())) {
                    etrInvestBondInfoDto.setChlIdName(
                        selectOptionDtoPage.getRoot().get(0).getText());
                }
            }
            // 交易对手
            etrInvestBondInfoDto.setCounterId(rowData.get(19).toString());
            // 交易对手名称
            if (StringUtil.isNotBlank(rowData.get(19).toString())) {
                String counterName = getCounterName(rowData.get(19).toString());
                etrInvestBondInfoDto.setCounterIdName(counterName);
            }
            // 备注
            etrInvestBondInfoDto.setRemark(rowData.get(20).toString());
            param = new HashMap<>();
            param.put("portfolioId", etrInvestBondInfoDto.getPortfolioId());
            param.put("ccy", etrInvestBondInfoDto.getCcy());
            param.put("vdate", etrInvestBondInfoDto.getVdateText());
            param.put("calId", etrInvestBondInfoDto.getCalendarId());
            param.put("manageId", etrInvestBondInfoDto.getSecAcctId());
            param.put("chlId", etrInvestBondInfoDto.getChlId());
            Map<String, Object> cashMap = this.getProAvailableCash(param);

            // 指令可用现金
            if (cashMap != null) {
                etrInvestBondInfoDto.setProAvailableCash(
                    cashMap.get("cashT") == null ? BigDecimal.ZERO : new BigDecimal(
                        cashMap.get("cashT").toString()));
                etrInvestBondInfoDto.setChannelAvailableCash(
                    cashMap.get("cashT2") == null ? BigDecimal.ZERO : new BigDecimal(
                        cashMap.get("cashT2").toString()));
                etrInvestBondInfoDto.setAccAvailableCash(
                    cashMap.get("cashT3") == null ? BigDecimal.ZERO : new BigDecimal(
                        cashMap.get("cashT3").toString()));
            }
            if (StringUtil.isNotBlank(etrInvestBondInfoDto.getFinprodId())) {
                param = new HashMap<>();
                param.put("finprodId", etrInvestBondInfoDto.getFinprodId());
                param.put("portfolioId", etrInvestBondInfoDto.getPortfolioId());
                param.put("calId", etrInvestBondInfoDto.getCalendarId());
                param.put("ccy", etrInvestBondInfoDto.getCcy());
                param.put("secAcctId", etrInvestBondInfoDto.getSecAcctId());
                param.put("chlId", etrInvestBondInfoDto.getChlId());
                param.put("invAim", etrInvestBondInfoDto.getInvAim());
                // 指令可用持仓
                Map<String, Object> posMap = this.getAssetAvailablePosition(param);
                if (posMap != null) {
                    etrInvestBondInfoDto.setAssetAvailablePosition(
                        posMap.get("position") == null ? BigDecimal.ZERO : new BigDecimal(100).multiply(new BigDecimal(
                            posMap.get("position").toString())));
                }
                // 本方券券面总额
                etrInvestBondInfoDto.setEntrustOwnNum(etrInvestBondInfoDto.getEntrustNum());
                // 融入券券面总额
                etrInvestBondInfoDto.setEntrustIntegrateNum(BigDecimal.ZERO);
                etrInvestBondInfoDto.setEtrInvestPersonInfo(new EtrInvestPersonInfoDto());
                // 投资经理
                etrInvestBondInfoDto.getEtrInvestPersonInfo().setInvManagerId(dto.getCreateUser());
                // 投资处室
                etrInvestBondInfoDto.getEtrInvestPersonInfo().setRoom(dto.getCreateDept());
                // 交易对手
                etrInvestBondInfoDto.getEtrInvestPersonInfo().setCounterId(
                    etrInvestBondInfoDto.getCounterId());
                try {
                    // 计算全价
                    EtrInvestBondInfoDto temp = new EtrInvestBondInfoDto();
                    BeanUtil.copyPropertiesIgnoreNull(temp, etrInvestBondInfoDto);
                    if (temp.getPrice() != null) {
                        temp.setCalflag("price");
                    }
                    else if (temp.getExerYield() != null) {
                        temp.setCalflag("exerYield");
                    }
                    else if (temp.getMatureYield() != null) {
                        temp.setCalflag("matureYield");
                    }

                    if (StringUtil.equals(temp.getSettleSpreed(),
                            DictCodeList.清算速度_T_0.getValue())) {
                        temp.setSettleSpreed("0");
                    }
                    if (StringUtil.equals(temp.getSettleSpreed(),
                            DictCodeList.清算速度_T_1.getValue())) {
                        temp.setSettleSpreed("1");
                    }
                    if(StringUtil.isNotEmpty(temp.getCalflag())){
                        this.calcAmt(temp);
                        etrInvestBondInfoDto.setFpriceAmt(temp.getUnitFprice());
                        etrInvestBondInfoDto.setMatureYield(temp.getMatureYield());
                        etrInvestBondInfoDto.setMatureYield(etrInvestBondInfoDto.getMatureYield().setScale(2,RoundingMode.HALF_UP));
                    }
                }
                catch (Exception e) {
                    // 计算失败什么都不做
                }
            }
        }
        dto.setSelectedItem(etrInvestBondInfoDtoList);
        return dto;
    }

    /**
     * 验证导入的数据
     * @param tempList 导入的数据
     * @return
     */
    public AjaxResult checkImportData(String fileName) {
        List<List<String>> objects = ExcelUtil.getTempList(fileName);
        if (objects.size() - 1 > 100) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", getMessage("ETR_E_0021"));
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        }

        String totalMsg = "";
        JSONArray jsonArrays = new JSONArray();
        // 失败条数
        int errorNum = 0;
        for (int i = 1; i < objects.size(); i++ ) {
            String confirmMsg = "";
            // excel的行号
            String lineNo = String.valueOf(i + 1);
            List rowData = objects.get(i);
            // 委托开始日非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                rowData.get(1).toString(), "EtrInvestBondInfo004.vdate", lineNo));
            // 委托开始日格式check
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsDate(
                rowData.get(1).toString(), "EtrInvestBondInfo004.vdate", lineNo));
            // 委托终止日非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                rowData.get(2).toString(), "EtrInvestBondInfo004.mdate", lineNo));
            // 委托终止日格式check
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsDate(
                rowData.get(2).toString(), "EtrInvestBondInfo004.mdate", lineNo));
            // 委托开始、终止日是否是平台日check
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    EtrCommonService.checkIsPlateDate(rowData.get(1).toString(), rowData.get(2).toString(),lineNo));
            // 组合代码非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                rowData.get(3).toString(), "EtrInvestBondInfo004.portfolioId", lineNo));
            // 组合代码check
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                etrCommonService.checkPortfolioIdExist(rowData.get(3).toString(),
                    "EtrInvestBondInfo004.portfolioId", lineNo));
            // 资产代码(市场代码)
            String finprodMarketId = rowData.get(4).toString();
            // 交易方向
            String ps = rowData.get(5).toString();
            if (StringUtil.isNotBlank(ps)) {
                ps = ps.split(":")[0];
            }
            // 交易方向非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                etrCommonService.checkIsBlank(ps, "EtrInvestBondInfo004.ps", lineNo));
            // 交易方向是买入时check
            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), ps)) {
                if (StringUtil.isBlank(finprodMarketId)) {
                    // 交易场所非空
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(16).toString(),
                            "EtrInvestBondInfo004.tradeMarket", lineNo));
                    // 币种非空
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(6).toString(),
                            "EtrInvestBondInfo004.ccy", lineNo));
                }
                else {
                    // 资产代码(市场代码)存在check
                    if (this.getBondInfoByFinprodMarketId(finprodMarketId) == null) {
                        confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            getMessage("ETR_E_0017",
                                getMessage("EtrInvestBondInfo004.finprodId"), lineNo));
                    }
                }
            }
            // 交易方向不是买入时check
            else if (StringUtil.isNotBlank(ps)) {
                // 资产代码(市场代码)非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(finprodMarketId,
                        "EtrInvestBondInfo004.finprodId", lineNo));
                // 资产代码(市场代码)存在check
                if (this.getBondInfoByFinprodMarketId(finprodMarketId) == null) {
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(
                        "ETR_E_0017", getMessage("EtrInvestBondInfo004.finprodId"), lineNo));
                }
            }
            // 券面总额
            String entrustAmt = rowData.get(7).toString();
            // 券面总额非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                entrustAmt, "EtrInvestBondInfo004.entrustAmt", lineNo));
            // 券面总额是数值
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsNumber(
                entrustAmt, "EtrInvestBondInfo004.entrustAmt", lineNo));
            // 券面总额是大于0
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsGtZero(
                entrustAmt, "EtrInvestBondInfo004.entrustAmt", lineNo));
            // 价格模式非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                rowData.get(8).toString(), "EtrInvestBondInfo004.priceModel", lineNo));
            // 净价
            String price = rowData.get(9).toString();
            // 行权收益率（%）
            String exerYield = rowData.get(10).toString();
            // 到期收益率（%）
            String matureYield = rowData.get(11).toString();
            // 净价/行权收益率（%）/到期收益率（%）至少且只有一个非空
            if (rowData.get(8).toString().contains(DictCodeListExt2.价格模式_指定价格.getValue())
                || rowData.get(8).toString().contains(DictCodeListExt2.价格模式_限价指令.getValue())) {
                int inputNo = (StringUtil.isBlank(price) ? 0 : 1)
                              + (StringUtil.isBlank(exerYield) ? 0 : 1)
                              + (StringUtil.isBlank(matureYield) ? 0 : 1);
                if (inputNo != 1) {
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        getMessage("ETR_E_0024", lineNo));
                }
            }
            // 净价是数值
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                etrCommonService.checkIsNumber(price, "EtrInvestBondInfo004.price", lineNo));
            // 净价是大于0
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                etrCommonService.checkIsGtZero(price, "EtrInvestBondInfo004.price", lineNo));
            // 行权收益率（%）是数值
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsNumber(
                exerYield, "EtrInvestBondInfo004.exerYield", lineNo));
            // 行权收益率（%）是大于0
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsGtZero(
                exerYield, "EtrInvestBondInfo004.exerYield", lineNo));
            // 到期收益率（%）是数值
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsNumber(
                matureYield, "EtrInvestBondInfo004.matureYield", lineNo));
            // 到期收益率（%）是大于0
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsGtZero(
                matureYield, "EtrInvestBondInfo004.matureYield", lineNo));
            // 最低成交金额
            String minAmt = rowData.get(12).toString();
            // 最低成交金额是数值
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                etrCommonService.checkIsNumber(minAmt, "EtrInvestBondInfo004.minAmt", lineNo));
            // 最低成交金额是大于0
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                etrCommonService.checkIsGtZero(minAmt, "EtrInvestBondInfo004.minAmt", lineNo));
            // 清算速度非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                rowData.get(13).toString(), "EtrInvestBondInfo004.settleSpreed", lineNo));
            // 投资目的非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                rowData.get(14).toString(), "EtrInvestBondInfo004.invAim", lineNo));
            // 交易平台非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                rowData.get(16).toString(), "EtrInvestBondInfo004.tradePlat", lineNo));
            // 证券账户存在
            if (StringUtil.isNotBlank(rowData.get(3).toString())
                && StringUtil.isNotBlank(rowData.get(17).toString())) {
                Map<String, Object> param = new HashMap<>();
                param.put("initValue", rowData.get(17).toString());
                param.put("portfolioId", rowData.get(3).toString());
                if (StringUtil.isNotBlank(rowData.get(15).toString())) {
                    param.put("tradeMarket", rowData.get(15).toString().split(":")[0]);
                }
                param.put("page", 1);
                param.put("length", 20);
                List<SelectOptionDto> selectOptionDtoList = this.getSecManageAcctId(param);
                if (StringUtil.isEmptyList(selectOptionDtoList)) {
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(
                        "ETR_E_0017", getMessage("EtrInvestBondInfo004.secManageAcctId"), lineNo));
                }
            }
            // 通道存在
            if (StringUtil.isNotBlank(rowData.get(18).toString())) {
                Map<String, Object> param = new HashMap<>();
                param.put("initValue", rowData.get(18).toString());
                param.put("page", 1);
                param.put("length", 20);
                Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                    ServiceApplication.COMMON_SERVICE_NAME + "/common/BussChannelCommonService/getChannelId",
                    param, SelectOptionDto.class);
                if (selectOptionDtoPage.getRoot().isEmpty()) {
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        getMessage("ETR_E_0017", getMessage("invest.channelId"), lineNo));
                }
            }
            // 交易对手存在时check对手存在
            if (StringUtil.isNotBlank(rowData.get(19).toString())) {
                Map<String, Object> map = new HashMap<>();
                map.put("initValue", rowData.get(19).toString());
                map.put("page", 1);
                map.put("length", 20);
                Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterIdCfets",
                    map, SelectOptionDto.class);
                if (selectOptionDtoPage.getRoot().isEmpty()) {
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(
                        "ETR_E_0017", getMessage("etrInvestPersonInfo.counterId"), lineNo));
                }else {
					rowData.set(19, selectOptionDtoPage.getRoot().get(0).getId());
				}
            }
            if (StringUtil.isBlank(confirmMsg)) {
                jsonArrays.add(rowData);
            }
            else {
                totalMsg = etrCommonService.StringAddByBr(totalMsg, confirmMsg);
                errorNum++ ;
            }
        }
        totalMsg = etrCommonService.StringAddByBr(totalMsg,
            getMessage("ETR_C_0001", String.valueOf(errorNum)));
        Map<String, Object> resultMap = new HashMap<>();
        if (StringUtil.isNotBlank(totalMsg)) {
            resultMap.put("confirmMsg", totalMsg);
        }
        resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
        AjaxResult wr = new AjaxResult();
        wr.setData(resultMap);
        return wr;
    }

    /**
     * 根据市场代码查询债券资产信息
     *
     * @param parameters 画面参数
     * @return 债券资产信息
     * @see
     * @since 1.0
     */
    private SelectOptionDto getBondInfoByFinprodMarketId(String finprodMarketId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("initValue", finprodMarketId);
        param.put("page", 1);
        param.put("length", 20);
        Page<SelectOptionDto> selectOptionDtoPage = this.getfinprodIdForAdd(param);
        if (!StringUtil.isEmptyList(selectOptionDtoPage.getRoot())) {
            return selectOptionDtoPage.getRoot().get(0);
        }
        else {
            return null;
        }
    }
    /**
     * 查询证券托管户：联想框文本框
     *
     * @param parameters 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAcctId(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId",
            parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String)parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String)parameters.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     * 查询交易对手名称
     *
     * @return
     */
    public String getCounterName(String counterId) {
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/getCounterName?counterId=" + counterId,
                String.class);
    }

}
