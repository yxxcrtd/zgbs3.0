/*
 * 文件名：EtrPlanAssetNService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年10月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrplanassetn.service;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;

import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrplanassetc.dto.EtrPlanAssetCDto;
import com.joyintech.tams.invest.entrust.etrplanassetn.dto.EtrPlanAssetNDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 计划类净值委托service 〈功能详细描述〉
 *
 * @author xiexinting
 * @version 1.0 2018年10月8日
 * @see EtrPlanAssetNService
 * @since 1.0
 */
@Service
public class EtrPlanAssetNService extends CommonExportService<EtrPlanAssetNDto> implements BaseProcessService<EtrPlanAssetNDto> {

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
     * 委托共通
     */
    @Autowired
    private EtrCommonService etrCommonService;
    /**
     * 计划类净值委托分页查询 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrPlanAssetNDto> selectForPage(Map<String, Object> param) {
        if (StringUtil.isNotEmpty((String)param.get("entrustDate"))) {
            List<String> dateList = StringUtil.splitToList(" ", (String)param.get("entrustDate"));
            param.put("startDate", dateList.get(0));
            param.put("endDate", dateList.get(2));
        }
        if(StringUtil.isNotEmpty((String)param.get("assetType1"))){
            List<String> assetType1List = StringUtil.splitToList(",", (String)param.get("assetType1"));
            param.put("assetType1List", assetType1List);
        }
        /*添加三四级分类 zhangpuyu 2019/7/31 start*/
        if(StringUtil.isNotEmpty((String)param.get("assetType10"))){
            List<String> assetType10List = StringUtil.splitToList(",", (String)param.get("assetType10"));
            param.put("assetType10List", assetType10List);
        }
        if(StringUtil.isNotEmpty((String)param.get("assetType11"))){
            List<String> assetType11List = StringUtil.splitToList(",", (String)param.get("assetType11"));
            param.put("assetType11List", assetType11List);
        }
        /*添加三四级分类 zhangpuyu 2019/7/31 end*/
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        Page<EtrPlanAssetNDto> page = restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/findPage", param,
                EtrPlanAssetNDto.class);
        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        for(EtrPlanAssetNDto dto:page.getRoot()){
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
     * 组合代码联想文本框 <功能详细描述>
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
                        + "/EtrPlanAssetN/getentrustId" , map,
                SuggestDto.class);
    }

    /**
     * 资产代码联想文本框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getfinprodId(String param) {
        return restTemplate.getForList(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetN/getfinprodId?finprodId=" + param,
                SuggestDto.class);
    }

    /**
     * 删除委托 <功能详细描述>
     *
     * @param entrustId
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetN/delete?entrustId=" + entrustId,
                boolean.class);
    }

    /**
     * 关闭委托 <功能详细描述>
     *
     * @param entrustId
     * @param closeWay
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId, String closeWay) {
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/close?entrustId=" + entrustId
                        + "&closeWay=" + closeWay,
                boolean.class);
    }

    /**
     * 撤回委托 <功能详细描述>
     *
     * @param entity
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrPlanAssetNDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/recall", dto, boolean.class);
    }

    /**
     * 工作流完成
     */
    @Override
    public void doComplete(EtrPlanAssetNDto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate = DateUtil.platDateFromCache();
        dto.setEntrustDate(sysDate);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/doComplete", dto,
                Integer.class);
    }

    /**
     *
     * 流程发起时调用
     */
    @Override
    public void doInsert(EtrPlanAssetNDto dto, OperationType type){
        // OperationType枚举值与dto的operateType不同,后续逻辑使用的是dto的operateType,所以不能覆盖
        // ZGZGST-1464:fuyongsheng 20210825 delete start
        // dto.setOperateType(type.toString());
        // ZGZGST-1464:fuyongsheng 20210825 delete end
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/doInsert",dto,
                Integer.class);
    }

    /**
     *
     * 流程修改时调用
     */
    @Override
    public void doUpdate(EtrPlanAssetNDto dto){
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/doUpdate",dto,
                Integer.class);
    }

    @Override
    public void doBack(EtrPlanAssetNDto dto) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/doBack",dto,
                Integer.class);
    }

    /**
     * 新建页面组合代码联想下拉框 <功能详细描述>
     *
     * @param search
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getentrustIdAdd(Map<String, Object> map) {
        map.put("userId", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetN/getentrustIdAdd" ,map,
                SelectOptionDto.class);
    }

    /**
     * 新建页面资产代码联想下拉框 <功能详细描述>
     *
     * @param search
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                + "/EtrPlanAssetN/getfinprodIdForAdd",map,SelectOptionDto.class);
    }

    public Page<SelectOptionDto> getfinprodIdForAdd1(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                + "/EtrPlanAssetN/getfinprodIdForAdd1",map,SelectOptionDto.class);
    }

    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrPlanAssetNDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/check", dto,
                ValidMessage.class);
    }

    /**
     * 根据主键查询修改/查看信息 <功能详细描述>
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrPlanAssetNDto selectByKey(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetN/selectByKey?entrustId=" + entrustId,
                EtrPlanAssetNDto.class);
    }

    /**
     * 通过资产代码带出资产类型 <功能详细描述>
     *
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> searchType(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrPlanAssetN/searchType?finprodId=" + finprodId,
                Map.class);
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
        exportParam.config("/com/joyintech/tams/invest/entrust/etrplanassetn/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<EtrPlanAssetNDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName,"transactioncom")){
            cell.setCellValue(value+"%");
        }
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
            param.put("sysDateT1",DateUtil.format(tradeDay));
        }
        return etrCommonService.getProAvailableCash(param);
//        return restTemplate.postForObject(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/getProAvailableCash",param,
//            Map.class);
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
        return etrCommonService.getAssetAvailablePosition(dto);
//        return restTemplate.postForObject(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/getAssetAvailablePosition",dto,
//            BigDecimal.class);
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
    public BigDecimal selectLimitAmt(EtrPlanAssetNDto dto){
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", DictCodeList.平安资产类型_计划类资产_净值型.getValue());
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
        EtrPlanAssetNDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
                BigDecimal amount = dto.getEntrustAmt();
                if (amount == null) {
                    amount = dto.getEntrustNum();
                }
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amount, LoginUserUtil.getUserName(),
                        BussinessDictCodeList.平安资产类型_计划类资产_净值型.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                        , dto.getFinprodId(), entity.getWorkflowId());
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
     * 获取委托列表数据
     * @return
     */
    public List<EtrPlanAssetNDto> getEntrustNListData(String tempList){
        List<EtrPlanAssetNDto> etrPlanAssetNDtoList=new ArrayList<>();
        JSONArray objects = JSONArray.parseArray(tempList);
        for (int i = 0; i < objects.size(); i++) {
            EtrPlanAssetNDto dto = new EtrPlanAssetNDto();
            JSONArray data = JSONArray.parseArray(((JSONArray) objects.get(i)).toJSONString());
            //根据模板中固定的下标来复制
            dto.setIndexId(data.get(0).toString().trim());
            //委托开始日
            dto.setVdate(DateUtil.parse(data.get(1).toString().trim()));
            //委托结束日
            dto.setMdate(DateUtil.parse(data.get(2).toString().trim()));
            //组合代码
            dto.setPortfolioId(data.get(3).toString().trim());
            //资产/市场代码
            dto.setFinprodMarketId(data.get(4).toString().trim());
            //交易方向
            dto.setPs(data.get(5).toString().trim());
            String Ps = data.get(5).toString().trim();
            dto.setPs(Ps.split(":")[0]);
            dto.setPsName(Ps.split(":")[1]);
            //委托金额
            if(StringUtil.isNotEmpty(data.get(6))){
                dto.setEntrustAmt(BigDecimal.valueOf(Double.valueOf(data.get(6).toString())));
            }
            //委托份额
            if(StringUtil.isNotEmpty(data.get(7))){
                dto.setEntrustNum(BigDecimal.valueOf(Double.valueOf(data.get(7).toString())));
            }
            // ZGZGST-1835:根据导入文件交易对手名称查询Id fuyongsheng 20210901 start
            //交易对手
            if (StringUtil.isNotBlank(data.get(8).toString())) {
                Map<String, Object> map = new HashMap<>();
                map.put("initValue", data.get(8).toString().trim());
                map.put("page", 1);
                map.put("length", 20);
                map.put("counterType", null);
                Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                        ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterId",
                        map, SelectOptionDto.class);
                if (!selectOptionDtoPage.getRoot().isEmpty()) {
                    SelectOptionDto counterInfo = selectOptionDtoPage.getRoot().get(0);
                    dto.setCounterId(counterInfo.getId());
                    dto.setCounterIdName(counterInfo.getText());
                }
            }
            // ZGZGST-1835:根据导入文件交易对手名称查询Id fuyongsheng 20210901 start
            //获取投资经理
            dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
            //所属部门
            dto.setRoom(LoginUserUtil.getDeptCode());
            //备注
            dto.setRemark(data.get(9).toString());
            //根据资产代码获取联动信息
            Map<String, Object> map = new HashMap<String,Object>();
            map.put("search", data.get(4).toString());
            map.put("page", 1);
            map.put("length", 20);
            Page<SelectOptionDto> page = getfinprodIdForAdd(map);
            SelectOptionDto selectOptionDto = page.getRoot().get(0);
            Map<String, Object> m = (Map<String, Object>) selectOptionDto.getObject();
            dto.setAssetType1(m.get("assetType1").toString());
            dto.setAssetType10(m.get("assetType10").toString());
            dto.setAssetType11(m.get("assetType11").toString());
            dto.setFinprodName(m.get("finprodName").toString());
            dto.setCcy(m.get("ccy").toString());
            dto.setFinprodId(m.get("finprodId").toString());
            //指令可用现金查询
            String vdate =DateUtil.format(dto.getVdate(),"yyyyMMdd");
            Map<String, Object> param = new HashMap<>();
            param.put("finprodId", m.get("finprodId").toString());
            param.put("portfolioId", dto.getPortfolioId());
            param.put("ccy", dto.getCcy());
            param.put("vdate",vdate);
            Map<String, Object> proAvailableCash = getProAvailableCash(param);
            if(StringUtil.isNotEmpty(proAvailableCash.get("cashT"))){
                dto.setProAvailableCash(BigDecimal.valueOf(Double.valueOf(proAvailableCash.get("cashT").toString())));
            } else {
                dto.setProAvailableCash(BigDecimal.ZERO);
            }
            //指令可用持仓查询
            map.put("finprodId", dto.getFinprodId());
            EtrSecPositionDto etrSecPositionDto=new EtrSecPositionDto();
            etrSecPositionDto.setCcy(dto.getCcy());
            etrSecPositionDto.setFinprodId(dto.getFinprodId());
            etrSecPositionDto.setPortfolioId(dto.getPortfolioId());
            if(StringUtil.isNotEmpty(getAssetAvailablePosition(etrSecPositionDto))){
                dto.setAssetAvailablePosition(getAssetAvailablePosition(etrSecPositionDto));
            }
            etrPlanAssetNDtoList.add(dto);
        }
        return etrPlanAssetNDtoList;
    }
    /**
     * 查询交易对手名称
     * @return
     */
    public String getCounterName(String counterId){
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/getCounterName?counterId="+counterId,
                String.class);
    }
    /**
     * 验证导入的数据
     *
     * @param tempList 导入的数据
     * @return
     */
    @Validated
    public AjaxResult checkImportData(String fileName) {
        String changeLineMark = "<br/>";
        List<List<String>> objects=ExcelUtil.getTempList(fileName);
        int totalData = 0;
        for (int i = 2; i < objects.size(); i++) {
            List<String> rowData = objects.get(i);
            for (int j = 0; j <= 8; j++) {
                if (StringUtil.isNotEmpty(rowData.get(j))) {
                    totalData++;
                    break;
                }
            }
        }
        //导入数据<100条时
        if (totalData <101) {
            String confirmMsg0 = "";
            JSONArray jsonArrays = new JSONArray();
            // 失败条数
            int errorNum = 0;
            for (int i = 1; i < objects.size(); i++) {
                String confirmmsg = "";
                List<String> rowData = objects.get(i);
                // excel的行号
                String lineNo = String.valueOf(i + 1);
                boolean isEntrust = false;
                for (int j = 0; j <= 8; j++) {
                    if (StringUtil.isNotEmpty(rowData.get(j))) {
                        isEntrust = true;
                        break;
                    }
                }
                if (isEntrust) {
                    //委托开始日调用共通check非空和日期格式是否正确
                    String message = etrCommonService.checkIsBlank(rowData.get(1).toString(), "EtrPlanAssetN002.vdate", lineNo);
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);
                    message = etrCommonService.checkIsDate(rowData.get(1).toString(), "EtrPlanAssetN002.vdate", lineNo);
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);

                    //委托结束日调用共通check非空和日期格式是否正确
                    message = etrCommonService.checkIsBlank(rowData.get(2).toString(), "EtrPlanAssetN002.mdate", lineNo);
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);
                    message = etrCommonService.checkIsDate(rowData.get(2).toString(), "EtrPlanAssetN002.mdate", lineNo);
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);

                    // 委托开始、终止日是否是平台日check
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg,
                            EtrCommonService.checkIsPlateDate(rowData.get(1).toString(), rowData.get(2).toString(),lineNo));

                    //组合代码check
                    message = etrCommonService.checkIsBlank(rowData.get(3).toString(), "EtrPlanAssetN002.portfolioId", lineNo);
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);
                    message = etrCommonService.checkPortfolioIdExist(rowData.get(3).toString(), "EtrPlanAssetN002.portfolioId", lineNo);
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);

                    //资产代码check
                    message = etrCommonService.checkIsBlank(rowData.get(4).toString(), "EtrPlanAssetN002.finprodId", lineNo);
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);
                    if (StringUtil.isNotEmpty(rowData.get(4).toString())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("search", rowData.get(4).toString());
                        map.put("page", 1);
                        map.put("length", 20);
                        Page<SelectOptionDto> finprodIds = getfinprodIdForAdd1(map);
                        if (finprodIds.getRoot().isEmpty()) {
                            confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0017", getMessage("EtrPlanAssetN002.finprodId"), lineNo));
                        }
                    }
                    //交易方向
                    message = etrCommonService.checkIsBlank(rowData.get(5).toString(), "EtrPlanAssetN002.ps", lineNo);
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);

                    // 对委托金额与委托份额的check
                    Map<String, Object> map33 = new HashMap<String,Object>();
                    map33.put("search", rowData.get(4).toString());
                    map33.put("page", 1);
                    map33.put("length", 20);
                    Page<SelectOptionDto> page = getfinprodIdForAdd1(map33);
                    //查到的时候才对委托金额与委托份额的check
                    if(!page.getRoot().isEmpty()){
                        SelectOptionDto selectOptionDto = page.getRoot().get(0);
                        Map<String, Object> m = (Map<String, Object>) selectOptionDto.getObject();
                        //当二级分类为公募计划，且交易方向为卖出或赎回时，委托金额为空，委托份额必填。
                        if (StringUtil.equals((String)m.get("assetType2"),
                                DictCodeList.计划类资产二级分类_公募计划.getValue())) {
                            String[] s = rowData.get(5).toString().split(":");
                            if (StringUtil.equals(s[0], DictCodeList.交易方向_卖出.getValue())
                                    || StringUtil.equals(s[0], DictCodeList.交易方向_赎回.getValue())) {
                                //委托金额
                                if (StringUtil.isNotBlank(rowData.get(6).toString())) {
                                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0102",
                                            lineNo));
                                }
                                //委托份额
                                message = etrCommonService.checkIsBlank(rowData.get(7).toString(), "EtrPlanAssetN002.entrustNum", lineNo);
                                confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);

                            }
                            //当交易方向为买入或申购时，委托份额为空，委托金额必填。
                            else{
                                //委托份额
                                if (StringUtil.isNotBlank(rowData.get(7).toString())) {
                                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0100",
                                            lineNo));
                                }
                                //委托金额
                                message = etrCommonService.checkIsBlank(rowData.get(6).toString(), "EtrPlanAssetN002.entrustAmt", lineNo);
                                confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);
                            }
                        }
                        //当二级分类不为公募计划，且交易方向为卖出或赎回时，委托金额与委托份额必填且只能填一个。
                        else{
                            String[] s2 = rowData.get(5).toString().split(":");
                            if(StringUtil.equals(s2[0], DictCodeList.交易方向_卖出.getValue())
                                    || StringUtil.equals(s2[0], DictCodeList.交易方向_赎回.getValue())){
                                if ((StringUtil.isBlank(rowData.get(6).toString())
                                        && StringUtil.isBlank(rowData.get(7).toString()))
                                        || (StringUtil.isNotBlank(rowData.get(6).toString())
                                        && StringUtil.isNotBlank(rowData.get(7).toString()))) {
                                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0020",
                                            getMessage("EtrPlanAssetN002.entrustAmt"), getMessage("EtrPlanAssetN002.entrustNum"), lineNo));
                                }
                            }
                            else{
                                //委托份额
                                if (StringUtil.isNotBlank(rowData.get(7).toString())) {
                                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0100",
                                            lineNo));
                                }
                                //委托金额
                                message = etrCommonService.checkIsBlank(rowData.get(6).toString(), "EtrPlanAssetN002.entrustAmt", lineNo);
                                confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);
                            }

                        }
                    }
                   /* //当交易方向为买入与申购时对委托份额与金额的check
                    String[] s = rowData.get(5).toString().split(":");
                    if (StringUtil.equals(s[0], DictCodeList.交易方向_买入.getValue())
                            || StringUtil.equals(s[0], DictCodeList.交易方向_申购.getValue())) {
                        //委托份额
                        if (StringUtil.isNotBlank(rowData.get(7).toString())) {
                            confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0100",
                                     lineNo));
                        }
                        //委托金额
                        message = etrCommonService.checkIsBlank(rowData.get(6).toString(), "EtrPlanAssetN002.entrustAmt", lineNo);
                        confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);
                    } else{
                        if ((StringUtil.isBlank(rowData.get(6).toString())
                                && StringUtil.isBlank(rowData.get(7).toString()))
                                || (StringUtil.isNotBlank(rowData.get(6).toString())
                                && StringUtil.isNotBlank(rowData.get(7).toString()))) {
                            confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0020",
                                    getMessage("EtrPlanAssetN002.entrustAmt"), getMessage("EtrPlanAssetN002.entrustNum"), lineNo));
                        }
                    }*/

                    //交易对手check
                    confirmmsg = checkCounter(confirmmsg, rowData, lineNo);
                }
                if (StringUtil.isBlank(confirmmsg)) {
                    jsonArrays.add(rowData);
                } else {
                    confirmMsg0 = etrCommonService.StringAddByBr(confirmMsg0, confirmmsg);
                    if (isEntrust) {
                        errorNum++;
                    }
                }
            }

            confirmMsg0 = etrCommonService.StringAddByBr(confirmMsg0,
                    getMessage("ETR_ST_009", String.valueOf(errorNum)));
            Map<String, Object> resultMap = new HashMap<>();
            if (StringUtil.isNotBlank(confirmMsg0)) {
                confirmMsg0 += changeLineMark + getMessage("ETR_ST_010");
                resultMap.put("confirmMsg", confirmMsg0);
            }
            resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        }
        //导入数据>100条时
        else {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", getMessage("ETR_E_0021"));
            AjaxResult errorMsg = new AjaxResult();
            errorMsg.setData(resultMap);
            return errorMsg;
        }
    }

    private String checkCounter(String confirmmsg, List<String> rowData, String lineNo) {
        String counterStr = rowData.get(8).toString();
        String ps = rowData.get(5).toString().split(":")[0];
        String message = etrCommonService.checkIsBlank(counterStr, "EtrPlanAssetN003.counterId", lineNo);
        confirmmsg = etrCommonService.StringAddByBr(confirmmsg, message);

        //交易对手是否符合交易方向的check
        if (StringUtil.isBlank(counterStr)) {
            return confirmmsg;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("initValue", counterStr);
        map.put("page", 1);
        map.put("length", 20);
        map.put("counterType", DictCodeListExt1.交易对手类型_内部交易对手.getValue());
        Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME +
                "/common/EntrustCommonPersonSelect/getCounterId", map, SelectOptionDto.class);

        if (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())){
            if (DictCodeList.是否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL))) {
                if (!selectOptionDtoPage.getRoot().isEmpty()) {
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0101", getMessage("EtrPlanAssetN003.counterId"), lineNo));
                }
            } else {
                if (selectOptionDtoPage.getRoot().isEmpty()) {
                    confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0103", getMessage("EtrPlanAssetN003.counterId"), lineNo));
                }
            }
        }
        if (StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
            if (selectOptionDtoPage.getRoot().isEmpty()) {
                confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0103", getMessage("EtrPlanAssetN003.counterId"), lineNo));
            }
        }
        // 申购与赎回时外部交易对手
        if (StringUtil.equals(ps, DictCodeList.交易方向_申购.getValue())
                || StringUtil.equals(ps, DictCodeList.交易方向_赎回.getValue())) {
            map.put("counterType", DictCodeListExt1.交易对手类型_市场机构.getValue());
            selectOptionDtoPage = restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME +
                    "/common/EntrustCommonPersonSelect/getCounterId", map, SelectOptionDto.class);
            // BSXQHD-2257
            //【计划类净值型指令】多对多指令导入，交易对手一直报必填报错
            // 要输入 组织机构代码或统一社会信用代码  进行 市场机构交易对手 校验。
            if (selectOptionDtoPage.getRoot().isEmpty()) {
                confirmmsg = etrCommonService.StringAddByBr(confirmmsg, getMessage("ETR_E_0103", getMessage("EtrPlanAssetN003.counterId"), lineNo));
            }
        }
        return confirmmsg;
    }

    /**
     * check 数字非空及格式
     * @param confirmMsg 错误信息
     * @param indexId 序号
     * @param data 数据
     * @param labelName 字段名称
     * @return
     */
    private String checkIsDouble(String confirmMsg, String indexId, String data, String labelName) {
        //判断是否为空
        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(data, labelName, indexId));
        //判断是否是数值
        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsNumber(data, labelName, indexId));
        //判断是否大于0
        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsGtZero(data, labelName, indexId));
        return confirmMsg;
    }

    public double getValue(Map map) {
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrPlanAssetN/getValue",map,
                double.class);
    }
}
