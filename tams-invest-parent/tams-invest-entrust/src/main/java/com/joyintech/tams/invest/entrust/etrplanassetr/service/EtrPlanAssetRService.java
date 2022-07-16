/*
 * 文件名：EtrPlanAssetR001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangjun
 * 修改时间：2018年9月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrplanassetr.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.entrust.etrplanassetn.dto.EtrPlanAssetNDto;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BussChannelCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrInvestPersonInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 计划类利率委托service
 * 〈功能详细描述〉
 * @author zhangjun
 * @version 1.0 2018年9月25日
 * @see EtrPlanAssetRService
 * @since 1.0
 */
@Service
public class EtrPlanAssetRService extends CommonExportService<EtrPlanAssetRDto> implements BaseProcessService<EtrPlanAssetRDto> {
    /**
     * 通道共通
     */
    @Autowired
    private BussChannelCommonService bussChannelCommonService;
    /**
     * 委托共通
     */
    @Autowired
    private EtrCommonService etrCommonService;
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
     *
     */
    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 计划类利率委托查询
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrPlanAssetRDto> selectForPage(Map<String, Object> param) {
        if (StringUtil.isNotEmpty((String)param.get("entrustDate"))) {
            List<String> dateList = StringUtil.splitToList(" ", (String)param.get("entrustDate"));
            param.put("startDate", dateList.get(0));
            param.put("endDate", dateList.get(2));
        }
        if(StringUtil.isNotEmpty(param.get("entrustStatus"))){
            List<String> entrustStatusList = StringUtil.splitToList(",",(String)param.get("entrustStatus"));
            param.put("entrustStatusList", entrustStatusList);
        }
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        Page<EtrPlanAssetRDto> page = restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/findPage", param,
                EtrPlanAssetRDto.class);
        for(EtrPlanAssetRDto dto:page.getRoot()){
            if(StringUtil.isNotEmpty(dto.getNextUser())){
                String sysUsers = userList.stream().filter(x-> Arrays.asList(dto.getNextUser().split(",")).contains(x.getUserName()))
                        .map(SysUser::getRealName)
                        .collect(Collectors.joining(",","",""));
                dto.setNextUser(sysUsers);
            }
        }
        return page;
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
                        + "/EtrPlanAssetR/getentrustId" , map,
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
    public List<SuggestDto> getfinprodId(String param) {
        return restTemplate.getForList(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetR/getfinprodId?finprodId=" + param,
                SuggestDto.class);
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
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/delete?entrustId="+entrustId,
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
    public EtrPlanAssetRDto selectByKey(String entrustId,String finprodId,String portfolioId) {
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
                        + "/EtrPlanAssetR/selectByKey?entrustId=" + entrustId+"&finprodId="+finprodId+"&portfolioId="+portfolioId,
                EtrPlanAssetRDto.class);
    }

    /**
     * 撤回委托 <功能详细描述>
     *
     * @param dto 实体类
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrPlanAssetRDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/recall", dto,
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
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/close?entrustId="+entrustId+"&closeWay="+closeWay,
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
        exportParam.config("/com/joyintech/tams/invest/entrust/etrplanassetr/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<EtrPlanAssetRDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName,"transactioncom")){
            cell.setCellValue(value+"%");
        }
    }
    /**
     * 获取新建界面的组合代码联想下拉框框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getentrustIdAdd(Map<String, Object> map) {
        map.put("userId", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetR/getentrustIdAdd"
                , map,
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
    public Page<SelectOptionDto> getfinprodIdForAdd(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetR/getentrustIdForAdd"
                , map,
                SelectOptionDto.class);
    }

    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrPlanAssetRDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/check", dto,
                ValidMessage.class);
    }
    /**
     * 修改/双击查看详情查询信息 <功能详细描述>
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrPlanAssetRDto selectByKey(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetR/selectByKey?entrustId=" + entrustId,
                EtrPlanAssetRDto.class);
    }


    /**
     * 工作流
     */
    @Override
    public void doComplete(EtrPlanAssetRDto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate = DateUtil.platDateFromCache();
        dto.setEntrustDate(sysDate);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/doComplete", dto,
                Integer.class);

    }
    /**
     *
     * 流程发起时调用
     */
    @Override
    public void doInsert(EtrPlanAssetRDto dto, OperationType type){
        //dto.setOperateType(type.toString());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/doInsert",dto,
                Integer.class);
    }

    /**
     *
     * 流程修改时调用
     */
    @Override
    public void doUpdate(EtrPlanAssetRDto dto){
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/doUpdate",dto,
                Integer.class);
    }

    @Override
    public void doBack(EtrPlanAssetRDto dto) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/doBack",dto,
                Integer.class);
    }

    /**
     * 通过资产代码获取资产类型
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public String searchType(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetR/searchType?finprodId=" + finprodId,
                String.class);
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
        Date vdate = DateUtil.parse((String)param.get("vdate"));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
            param.put("vdate1",DateUtil.format(tradeDay));
        }
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/getProAvailableCash",param,
                Map.class);
    }

    /**
     * 组合可用持仓查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getAssetAvailablePosition(EtrSecPositionDto dto) {
        Date sysDate = DateUtil.platDateFromCache();
        dto.setCdate(DateUtil.parse(DateUtil.format(sysDate)));
        dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
//        if (StringUtil.isNotEmpty(dto.getSecAcctId())) {
//            dto.setChannel(true);
//        }
        // modify by etliu 20211129 start
        // TISHKS-465: 组合T日可用现金、组合T+1日可用现金为交易可用，应为指令可用
//        return restTemplate.postForObject(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetR/getAssetAvailablePosition",dto,
//            BigDecimal.class);
        return etrCommonService.getAssetAvailablePosition(dto);
        // modify by etliu 20211129 end
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
    public BigDecimal selectLimitAmt(EtrPlanAssetRDto dto){
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        map.put("assetSecType", dto.getAssetType1());
        map.put("tradeMarket", dto.getTradeMarket());
        map.put("ps",dto.getPs());
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/SelectLimitAmt/selectLimitAmt", map,
                BigDecimal.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        EtrPlanAssetRDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            BigDecimal amt = dto.getEntrustAmt();
            if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0) {
                amt = dto.getEntrustNum();
            }
            try {
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amt, LoginUserUtil.getUserName(),
                        BussinessDictCodeList.平安资产类型_计划类资产_利率型.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                        // 资产类型改为授权类型 add by renping 2019-08-06  start
                        , dto.getFinprodId(), dto.getWorkflowId());
                // add by zhangrui1 风险审批修改 start
                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                // add by zhangrui1 风险审批修改 end
                return commonBusinessService.是否需要上级审批_委托用(params);
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
//        return null;
    }


    /**
     *
     * @param finprodId
     * @return
     */
    public FinProduct getFinProductInfo(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetR/getFinProductInfo?finprodId=" + finprodId,
                FinProduct.class);
    }

    // 多对多指令新增 add by zhnagpuyu 2020/1/2 start
    /**
     * 导入数据文件
     *
     * @param fileName 导入的文件名
     * @return 导入结果，包含验证内容
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
    public List<EtrPlanAssetRDto> getEntrustListData(String tempList) {
        List<EtrPlanAssetRDto> etrPlanAssetRDtoList = new ArrayList<>();
        JSONArray objects = JSONArray.parseArray(tempList);
        for (int i = 0; i < objects.size(); i++ ) {
            EtrPlanAssetRDto dto = new EtrPlanAssetRDto();
            JSONArray rowData = JSONArray.parseArray(((JSONArray)objects.get(i)).toJSONString());
            // 根据模板中固定的下标来赋值
            // 序号
            dto.setIndexId(rowData.get(0).toString().trim());
            // 委托开始日
            dto.setVdate(DateUtil.parse(rowData.get(1).toString().trim()));
            // 委托终止日
            dto.setMdate(DateUtil.parse(rowData.get(2).toString().trim()));
            // 组合代码
            dto.setPortfolioId(rowData.get(3).toString().trim());
            // 市场代码
            dto.setFinprodMarketId(rowData.get(4).toString().trim());
            // 交易方向
            dto.setPs(rowData.get(5).toString().trim().split(":")[0]);
            // 委托本金
            if (StringUtil.isNotEmpty(rowData.get(6).toString())) {
                dto.setEntrustNum(
                        BigDecimal.valueOf(Double.valueOf(rowData.get(6).toString().trim())));
            }
            else {
                dto.setEntrustNum(null);
            }
            // 委托金额
            if (StringUtil.isNotEmpty(rowData.get(7).toString())) {
                dto.setEntrustAmt(
                        BigDecimal.valueOf(Double.valueOf(rowData.get(7).toString().trim())));
            }
            else {
                dto.setEntrustAmt(null);
            }
            // 投资目的
            dto.setInvAim(rowData.get(8).toString().trim().split(":")[0]);
            // 获取备注
            dto.setRemark(rowData.get(9).toString().trim());
            // 获取交易对手
            dto.setCounterId(rowData.get(10).toString().trim());
            // 获取交易对手名称
            dto.setCounterIdName(getCounterName(rowData.get(10).toString().trim()));
            // 投资经理
            dto.setEtrInvestPersonInfo(new EtrInvestPersonInfoDto());
            dto.getEtrInvestPersonInfo().setInvManagerId(dto.getCreateUser());
            // 获取交易对手
            dto.getEtrInvestPersonInfo().setCounterId(rowData.get(10).toString().trim());
            // 投资处室
            dto.getEtrInvestPersonInfo().setRoom(dto.getCreateDept());
            // 根据市场代码查出联动值
            Map<String, Object> map = new HashMap<>();
            map.put("initValue", rowData.get(4).toString());
            map.put("page", 1);
            map.put("length", 20);
            Page<SelectOptionDto> finprodIds = getfinprodIdForAdd(map);
            SelectOptionDto selectOptionDto = finprodIds.getRoot().get(0);
            JSONObject jsonObject = JSONObject.parseObject(
                    JSONObject.toJSONString(selectOptionDto.getObject()));
            if (jsonObject.containsKey("calendarId")) {
                dto.setCalendarId(jsonObject.get("calendarId").toString());
            }
            // 资产名称
            if (jsonObject.containsKey("finprodName")) {
                dto.setFinprodName(jsonObject.get("finprodName").toString());
            }
            // 币种
            dto.setCcy(jsonObject.get("ccy").toString());
            // 资产二级分类
            dto.setAssetType1(jsonObject.get("assetType1").toString());
            // 资产三级分类
            dto.setAssetType10(jsonObject.get("assetType10").toString());
            // 资产四级分类
            dto.setAssetType11(jsonObject.get("assetType11").toString());
            // 资产代码
            dto.setFinprodId(jsonObject.get("finprodId").toString());
            // 通道
            if (StringUtil.isNotBlank(dto.getFinprodId())) {
                Map<String, Object> param = new HashMap<>();
                param = new HashMap<>();
                param.put("finprodId", dto.getFinprodId());
                param.put("page", 1);
                param.put("length", 20);
                List<SelectOptionDto> selectOptionDtoPage = restTemplate.postForList(
                        ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getChannelIdAndCollectTypeForAsset",
                        param, SelectOptionDto.class);
                if (!selectOptionDtoPage.isEmpty()
                        && StringUtil.isNotEmpty(selectOptionDtoPage.get(0))) {
                    dto.setChlIdName(selectOptionDtoPage.get(0).getText());
                }
            }
            // 指令可用现金
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                Map<String, Object> param = new HashMap<>();
                param.put("finprodId", jsonObject.get("finprodId").toString());
                param.put("portfolioId", dto.getPortfolioId());
                param.put("ccy", dto.getCcy());
                param.put("vdate", DateUtil.format(dto.getVdate()));
                Map<String, Object> proAvailableCash = getProAvailableCash(param);
                if (proAvailableCash != null && proAvailableCash.get("cashT") != null) {
                    dto.setEtrAvailableCash(
                            BigDecimal.valueOf(Double.valueOf(proAvailableCash.get("cashT").toString())));
                    //组合可用现金
                    dto.setProAvailableCash(BigDecimal.valueOf(Double.valueOf(proAvailableCash.get("cashT").toString())));
                }
                // 托管户可用现金
                if (proAvailableCash!=null&&proAvailableCash.containsKey("cashTG")
                        && proAvailableCash.get("cashTG") != null) {
                    dto.setAccAvailableCash(
                            BigDecimal.valueOf(Double.valueOf(proAvailableCash.get("cashTG").toString())));
                }
            }
            // 指令可用持仓
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                EtrSecPositionDto etrSecPositionDto = new EtrSecPositionDto();
                Date sysDate = DateUtil.platDateFromCache();
                etrSecPositionDto.setCdate(DateUtil.parse(DateUtil.format(sysDate)));
                etrSecPositionDto.setFinprodId(jsonObject.get("finprodId").toString());
                etrSecPositionDto.setPortfolioId(dto.getPortfolioId());
                etrSecPositionDto.setCcy(dto.getCcy());
                BigDecimal assetAvailablePosition = etrCommonService.getAssetAvailablePosition(etrSecPositionDto);
                dto.setAssetAvailablePosition(BigDecimal.valueOf(Double.valueOf(assetAvailablePosition.toString())));
            }
            etrPlanAssetRDtoList.add(dto);
        }
        return etrPlanAssetRDtoList;
    }

    /**
     * 查询交易对手名称
     *
     * @return
     */
    public String getCounterName(String counterId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrBondFirstLBidding/getCounterName?counterId="
                        + counterId,
                String.class);
    }

    /**
     * 验证导入的数据
     *
     * @param tempList 导入的数据
     * @return
     */
    public AjaxResult checkImportData(String fileName) {
        List<List<String>> objects = ExcelUtil.getTempList(fileName);
        int totalData = 0;
        // 导入数据超过100条做报错信息的check
        for (int i = 2; i < objects.size(); i++ ) {
            List<String> rowData = objects.get(i);
            for (int j = 0; j <= 10; j++ ) {
                if (StringUtil.isNotEmpty(rowData.get(j))) {
                    totalData++ ;
                    break;
                }
            }
        }
        if (totalData > 99) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", getMessage("ETR_E_0021"));
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        }
        else {
            String confirmMsg1 = "";
            JSONArray jsonArrays = new JSONArray();
            // 失败条数
            int errorNum = 0;
            for (int i = 1; i < objects.size(); i++ ) {
                String confirmMsg = "";
                List<String> rowData = objects.get(i);
                String lineNo = String.valueOf(i + 1);
                String indexId = rowData.get(0).toString();
                if (StringUtil.isBlank(indexId)) {
                    confirmMsg = "第" + (i + 1) + "行序号不能为空";
                    //TISHKS-1390:导入模板中“序号”为空，为提示导入失败 start
                    confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, confirmMsg);
                    errorNum++ ;
                    continue;
                    //TISHKS-1390:导入模板中“序号”为空，为提示导入失败 end
                }
                // 委托开始日非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(1).toString(),
                                "EtrPlanAssetR002.vdate", lineNo));
                // 委托开始日格式check
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsDate(rowData.get(1).toString(),
                                "EtrPlanAssetR002.vdate", lineNo));
                // 委托终止日非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(2).toString(),
                                "EtrPlanAssetR002.mdate", lineNo));
                // 委托终止日格式check
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsDate(rowData.get(2).toString(),
                                "EtrPlanAssetR002.mdate", lineNo));
                // 委托开始、终止日是否是平台日check
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        EtrCommonService.checkIsPlateDate(rowData.get(1).toString(), rowData.get(2).toString(),lineNo));
                // 组合代码非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(3).toString(),
                                "EtrPlanAssetR002.portfolioId", lineNo));
                // 组合代码是否存在
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkPortfolioIdExist(rowData.get(3).toString(),
                                "EtrPlanAssetR002.portfolioId", lineNo));
                // 资产代码非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(4).toString(),
                                "EtrPlanAssetR002.finprodId", lineNo));
                if (StringUtil.isNotEmpty(rowData.get(4).toString())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("search", rowData.get(4).toString());
                    map.put("page", 1);
                    map.put("length", 20);
                    Page<SelectOptionDto> finprodIds = getfinprodIdForAdd(map);
                    if (finprodIds.getRoot().isEmpty()) {
                        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(
                                "ETR_E_0017", getMessage("EtrPlanAssetR002.finprodId"), lineNo));
                    }
                }
                // 交易方向非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(5).toString(), "EtrPlanAssetR002.ps",
                                lineNo));
                // 根据交易方向来chec委托本金和委托金额
                String[] s2 = rowData.get(5).toString().split(":");
                if (StringUtil.equals(s2[0], DictCodeList.交易方向_买入.getValue())) {
                    // 委托金额
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsBlank(rowData.get(7).toString(),
                                    "EtrPlanAssetR002.entrustAmt", lineNo));
                    // 交易方向为买入时,委托本金为空check
                    if (StringUtil.isNotBlank(rowData.get(6).toString())) {
                        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(
                                "ETR_E_0029", getMessage("EtrPlanAssetR002.ps"), lineNo));
                    }
                }
                else {
                    // 委托本金
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsBlank(rowData.get(6).toString(),
                                    "EtrPlanAssetR002.entrustNum", lineNo));
                    // 交易方向为卖出时，委托金额为空check
                    if (StringUtil.isNotBlank(rowData.get(7).toString())) {
                        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(
                                "ETR_E_0030", getMessage("EtrPlanAssetR002.ps"), lineNo));
                    }
                }
                // 投资目的非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(8).toString(),
                                "EtrInvestBondInfo002.invAim", lineNo));
                // TISHKS-220:导入模板删除通道字段 fuyongsheng 20211105 edit start
                // 通道
//                if (StringUtil.isNotBlank(rowData.get(9).toString())) {
//                    Map<String, Object> param = new HashMap<>();
//                    param.put("initValue", rowData.get(9).toString());
//                    param.put("page", 1);
//                    param.put("length", 20);
//                    Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
//                        ServiceApplication.COMMON_SERVICE_NAME + "/common/BussChannelCommonService/getChannelId",
//                        param, SelectOptionDto.class);
//                    if (selectOptionDtoPage.getRoot().isEmpty()) {
//                        etrCommonService.StringAddByBr(confirmMsg, getMessage("ETR_E_0017",
//                            getMessage("invest.channelId"), lineNo));
//                    }
//                }
                // TISHKS-220:导入模板删除通道字段 fuyongsheng 20211105 edit end
                // 交易对手  TISHKS-1631 无需填写交易对手代码
                /*confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(10).toString(),
                                "EtrPlanAssetN003.counterId", lineNo));
                if (StringUtil.isNotBlank(rowData.get(10).toString())) {
                    if (StringUtil.equals(s2[0], DictCodeList.交易方向_赎回.getValue())
                            || StringUtil.equals(s2[0], DictCodeList.交易方向_买入.getValue())
                            || StringUtil.equals(s2[0], DictCodeList.交易方向_申购.getValue())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("search", rowData.get(10).toString());
                        map.put("page", 1);
                        map.put("length", 20);
                        map.put("counterType", DictCodeListExt1.交易对手类型_市场机构.getValue());
                        Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterId",
                                map, SelectOptionDto.class);
                        if (selectOptionDtoPage.getRoot().isEmpty()) {
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                    getMessage("ETR_E_0017",
                                            getMessage("EtrInvestPersonInfo002.counterId"), lineNo));
                        }
                    }
                }*/

                if (StringUtil.isBlank(confirmMsg)) {
                    jsonArrays.add(rowData);
                }
                else {
                    confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, confirmMsg);
                    errorNum++ ;
                }
            }
            confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1,
                    getMessage("ETR_C_0001", String.valueOf(errorNum)));
            Map<String, Object> resultMap = new HashMap<>();
            if (StringUtil.isNotBlank(confirmMsg1)) {
                resultMap.put("confirmMsg", confirmMsg1);
            }
            resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        }
    }

    /**
     * check 数字非空及格式
     *
     * @param confirmMsg 错误信息
     * @param indexId 序号
     * @param data 数据
     * @param labelName 字段名称
     * @return
     */
    private String checkIsDouble(String confirmMsg, String indexId, String data,
                                 String labelName) {
        String message = etrCommonService.checkIsBlank(data, labelName, indexId);
        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
        if (StringUtil.isNotBlank(data)) {
            try {
                Double.parseDouble(data);
            }
            catch (NumberFormatException e) {
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        getMessage("ETR_E_0016", getMessage(labelName), indexId));
            }
        }
        return confirmMsg;
    }
    // 多对多指令新增 add by zhnagpuyu 2020/1/2 end
}
