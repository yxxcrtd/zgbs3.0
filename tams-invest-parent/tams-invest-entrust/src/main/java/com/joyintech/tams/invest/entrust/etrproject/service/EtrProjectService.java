/*
 * 文件名：EtrProject001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrproject.service;

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
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.common.base.dao.WflTrdProductDealMapper;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BussChannelCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.invest.entrust.etrproject.dto.EtrProjectDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目类委托画面端service 〈功能详细描述〉
 *
 * @author xiexinting
 * @version 1.0 2018年10月11日
 * @see EtrProjectService
 * @since 1.0
 */
@Service
public class EtrProjectService extends CommonExportService<EtrProjectDto> implements BaseProcessService<EtrProjectDto> {

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
     * 委托组件
     */
    @Autowired
    private EtrCommonService etrCommonService;
    private BussChannelCommonService bussChannelCommonService;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    /**
     * 工作流完成
     */
    @Override
    public void doComplete(EtrProjectDto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate = DateUtil.platDateFromCache();
        dto.setEntrustDate(sysDate);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/doComplete", dto,
                Integer.class);
    }

    /**
     * 流程发起时调用
     */
    @Override
    public void doInsert(EtrProjectDto dto, OperationType type) {
        //流程交易表插入一条记录，用于在待办中显示交易方向
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        wflTrdProductDeal.setWorkflowId(dto.getWorkflowId());
        wflTrdProductDeal.setPs(dto.getPs());
        wflTrdProductDeal.setTradeId(dto.getWorkflowId());
        wflTrdProductDeal.setFinprodId(dto.getFinprodId());
        wflTrdProductDeal.setWorkflowStatus("A");
        wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);

        // OperationType枚举值与dto的operateType不同,后续逻辑使用的是dto的operateType,所以不能覆盖
        // ZGZGST-1479:fuyongsheng 20210825 delete start
        // dto.setOperateType(type.toString());
        // ZGZGST-1479:fuyongsheng 20210825 delete end
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/doInsert", dto,
                Integer.class);
    }

    /**
     * 流程修改时调用
     */
    @Override
    public void doUpdate(EtrProjectDto dto) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/doUpdate", dto,
                Integer.class);
    }

    @Override
    public void doBack(EtrProjectDto dto) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/doBack", dto,
                Integer.class);
    }

    /**
     * 项目类委托分页查询 <功能详细描述>
     *
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrProjectDto> selectForPage(Map<String, Object> param) {
        if (StringUtil.isNotEmpty((String) param.get("entrustDate"))) {
            List<String> dateList = StringUtil.splitToList(" ", (String) param.get("entrustDate"));
            param.put("startDate", dateList.get(0));
            param.put("endDate", dateList.get(2));
        }
        if (StringUtil.isNotEmpty((String) param.get("ccy"))) {
            List<String> dateList = StringUtil.splitToList(",", (String) param.get("ccy"));
            param.put("ccyList", dateList);
        }
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        Page<EtrProjectDto> page = restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/findPage", param,
                EtrProjectDto.class);
        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        for(EtrProjectDto dto:page.getRoot()){
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
                        + "/EtrProject/getentrustId", map,
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
                        + "/EtrProject/getfinprodId?finprodId=" + param,
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
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/delete?entrustId=" + entrustId,
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
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/close?entrustId=" + entrustId
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
    public boolean recallDelegate(EtrProjectDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/recall", dto, boolean.class);
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
        exportParam.config("/com/joyintech/tams/invest/entrust/etrproject/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    @Override
    public Page<EtrProjectDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if (StringUtil.equals(propName, "transactioncom")) {
            cell.setCellValue(value + "%");
        }
    }

    /**
     * 通过资产代码带出资产类型 <功能详细描述>
     *
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public String searchType(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrProject/searchType?finprodId=" + finprodId,
                String.class);
    }

    /**
     * 通过资产代码查询融资金额 <功能详细描述>
     *
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal searchIssueAmt(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrProject/searchIssueAmt?finprodId=" + finprodId,
                BigDecimal.class);
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
                        + "/EtrProject/getentrustIdAdd", map,
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
                        + "/EtrProject/getfinprodIdForAdd", map,
                SelectOptionDto.class);
    }

    /**
     * 查询修改/查看返显数据 <功能详细描述>
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrProjectDto selectByKey(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrProject/selectByKey?entrustId=" + entrustId,
                EtrProjectDto.class);
    }

    /**
     * 组合可用现金查询
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        Date vdate = DateUtil.parse((String) param.get("vdate"));
        String calId = (String) param.get("calId");
        if (StringUtil.isNotEmpty(calId)) {
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)), DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15), calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
            param.put("sysDateT1", DateUtil.format(tradeDay));
        }
        return etrCommonService.getProAvailableCash(param);
        /*return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/getProAvailableCash", param,
            Map.class);*/
    }

    /**
     * 组合可用持仓查询
     * <功能详细描述>
     *
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
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/getAssetAvailablePosition", dto,
//            BigDecimal.class);
        return etrCommonService.getAssetAvailablePosition(dto);
        // modify by etliu 20211129 end
    }

    /**
     * 校验方法
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrProjectDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/check", dto,
                ValidMessage.class);
    }

    /**
     * 查询授权额度
     * <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectLimitAmt(EtrProjectDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", DictCodeList.平安资产类型_项目类资产.getValue());
        map.put("assetSecType", dto.getAssetType1());
        map.put("tradeMarket", dto.getTradeMarket());
        map.put("ps", dto.getPs());
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/SelectLimitAmt/selectLimitAmt", map,
                BigDecimal.class);
    }


    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        EtrProjectDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {BigDecimal amount = dto.getEntrustAmt();
                if (amount == null) {
                    amount = dto.getEntrustNum();
                }
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amount, LoginUserUtil.getUserName(),
                        BussinessDictCodeList.平安资产类型_项目类资产.getValue(), dto.getAssetType1(), null, dto.getPs()
                        // 资产类型改为授权类型 add by renping 2019-08-06  start
                        , dto.getFinprodId(), dto.getWorkflowId()
                        // 资产类型改为授权类型 add by renping 2019-08-06  end
                );
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
    }

    /**
     * 获取委托列表数据
     * @return
     */
    public List<EtrProjectDto> getEtrDoubleProjectDtoListData(String tempList){
        List<EtrProjectDto> etrProjectDtoList=new ArrayList<>();
        JSONArray objects = JSONArray.parseArray(tempList);
        for (int i = 0; i < objects.size(); i++) {
            EtrProjectDto dto = new EtrProjectDto();
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
            dto.setEntrustAmt(BigDecimal.valueOf(Double.valueOf(data.get(6).toString().trim())));
            //本金金额
            dto.setPrinAmt(BigDecimal.valueOf(Double.valueOf(data.get(7).toString())));
            //投资目的
            dto.setInvAim(data.get(8).toString().trim().split(":")[0]);
            //交易对手
            dto.setCounterId(data.get(9).toString().trim());
            String counterName = getCounterName(data.get(9).toString().trim());
            dto.setCounterIdName(counterName);
            //获取投资经理
            dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
            //所属部门
            dto.setRoom(LoginUserUtil.getDeptCode());
            //清算速度
            dto.setSettleSpeed(data.get(10).toString());
            //分割清算速度
            String ss = data.get(10).toString().trim();
            dto.setSettleSpeed(ss.split(":")[1]);
            //净投资收益率
            dto.setInvestRate(BigDecimal.valueOf(Double.valueOf(data.get(11).toString())));
            //备注
            dto.setRemark(data.get(12).toString());
            //根据资产代码获取联动信息
            Map<String, Object> map = new HashMap<String,Object>();
            map.put("initValue", data.get(4).toString());
            map.put("page", 1);
            map.put("length", 20);
            map.put("chlId",dto.getChlId());
            Page<SelectOptionDto> page = getfinprodIdForAdd(map);
            SelectOptionDto selectOptionDto = page.getRoot().get(0);
            Map<String, Object> m = (Map<String, Object>) selectOptionDto.getObject();
            dto.setType9(m.get("type9").toString());
            dto.setType10(m.get("type10").toString());
            dto.setType11(m.get("type11").toString());
            dto.setFinprodName(m.get("finprodName").toString());
            dto.setCcy(m.get("ccy").toString());
            dto.setFinprodId(m.get("finprodId").toString());
            // 通道
            if (StringUtil.isNotBlank(dto.getFinprodId())) {
                Map<String, Object> param = new HashMap<>();
                param = new HashMap<>();
                param.put("finprodId", dto.getFinprodId());
                param.put("page", 1);
                param.put("length", 20);
                List<SelectOptionDto> selectOptionDtoList = restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getChannelIdAndCollectTypeForAsset",
                        param, SelectOptionDto.class);
                if (!selectOptionDtoList.isEmpty()&&StringUtil.isNotEmpty(selectOptionDtoList.get(0))) {
                    dto.setChlId(selectOptionDtoList.get(0).getId());
                    dto.setChlIdName(selectOptionDtoList.get(0).getText());
                }else {
                    dto.setChlIdName(null);
                    dto.setChlId(null);
                }
            }
            //指令可用现金查询
            String vdate =DateUtil.format(dto.getVdate(),"yyyyMMdd");
            Map<String, Object> param = new HashMap<>();
            param.put("finprodId", m.get("finprodId").toString());
            param.put("portfolioId", dto.getPortfolioId());
            param.put("ccy", dto.getCcy());
            param.put("vdate",vdate);
            Map<String, Object> proAvailableCash = getProAvailableCash(param);
            dto.setProAvailableCash(BigDecimal.valueOf(Double.valueOf(proAvailableCash.get("cashT").toString())));
            //指令可用持仓
            map.put("finprodId", dto.getFinprodId());

            // modify by etliu 20211129 start
//            PtlSecPositionFlowDto ptlSecPositionFlowDto=new PtlSecPositionFlowDto();
            EtrSecPositionDto ptlSecPositionFlowDto=new EtrSecPositionDto();
            // modify by etliu 20211129 end
            ptlSecPositionFlowDto.setCcy(dto.getCcy());
            ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
            ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
            dto.setAssetAvailablePosition(getAssetAvailablePosition(ptlSecPositionFlowDto));
            etrProjectDtoList.add(dto);
        }
        return etrProjectDtoList;
    }

    /**
     * 查询交易对手名称
     * @return
     */
    public String getCounterName(String counterId){
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/getCounterName?counterId="+counterId,
                String.class);
    }
    /**
     * 验证导入的数据
     *
     * @param tempList 导入的数据
     * @return
     */
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
                String confirmMsg = "";
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
                if(isEntrust) {
                    //委托开始日调用共通check非空和是否正确
                    String message = etrCommonService.checkIsBlank(rowData.get(1).toString(), "EtrProject002.vdate", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    message = etrCommonService.checkIsDate(rowData.get(1).toString(), "EtrProject002.vdate", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    //委托结束日
                    message = etrCommonService.checkIsBlank(rowData.get(2).toString(), "EtrProject002.mdate", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    message = etrCommonService.checkIsDate(rowData.get(2).toString(), "EtrPlanAssetN002.mdate", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    // 委托开始、终止日是否是平台日check
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            EtrCommonService.checkIsPlateDate(rowData.get(1).toString(), rowData.get(2).toString(),lineNo));
                    //组合代码
                    message = etrCommonService.checkIsBlank(rowData.get(3).toString(), "EtrProject002.portfolioId", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    message = etrCommonService.checkPortfolioIdExist(rowData.get(3).toString(), "EtrProject002.portfolioId", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    //资产代码
                    message = etrCommonService.checkIsBlank(rowData.get(4).toString(), "EtrProject002.finprodId", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    if (StringUtil.isNotEmpty(rowData.get(4).toString())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("search", rowData.get(4).toString());
                        map.put("page", 1);
                        map.put("length", 20);
                        Page<SelectOptionDto> finprodIds = getfinprodIdForAdd(map);
                        if (finprodIds.getRoot().isEmpty()) {
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage("ETR_E_0017", getMessage("EtrProject002.finprodId"), lineNo));
                        }
                    }
                    //交易方向
                    message = etrCommonService.checkIsBlank(rowData.get(5).toString(), "EtrProject002.ps", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    //委托金额
                    confirmMsg= checkIsDouble(confirmMsg,lineNo,rowData.get(6).toString(),"EtrProject002.entrustAmt");
                    //本金金额
                    confirmMsg = checkIsDouble(confirmMsg, lineNo, rowData.get(7).toString(), "EtrProject003.prinAmt");
                    //投资目的
                    message = etrCommonService.checkIsBlank(rowData.get(8).toString(), "EtrInvestBondInfo002.invAim", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    //交易对手
                    message = etrCommonService.checkIsBlank(rowData.get(9).toString(), "EtrProject003.counterId", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    //交易对手输入格式是否正确check
                    if (StringUtil.isNotBlank(rowData.get(9).toString())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("initValue", rowData.get(9).toString());
                        map.put("page", 1);
                        map.put("length", 20);
                        map.put("counterType", null);
                        Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterId",
                                map, SelectOptionDto.class);
                        if (selectOptionDtoPage.getRoot().isEmpty()) {
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(
                                    "ETR_E_0017", getMessage("etrInvestPersonInfo.counterId"), lineNo));
                        }
                    }
                    //交易方向为买入的时候对交易对手的check
                    if (StringUtil.isNotBlank(rowData.get(9).toString())) {
                        String[] s2 = rowData.get(5).toString().split(":");
                        if (StringUtil.equals(s2[0], DictCodeList.交易方向_买入.getValue())) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("initValue", rowData.get(9).toString());
                            map.put("page", 1);
                            map.put("length", 20);
                            map.put("counterType", DictCodeListExt1.交易对手类型_内部交易对手.getValue());
                            Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME +
                                    "/common/EntrustCommonPersonSelect/getCounterId", map, SelectOptionDto.class);
                            if (!selectOptionDtoPage.getRoot().isEmpty()) {
                                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage("ETR_E_0101", getMessage("EtrPlanAssetN003.counterId"), lineNo));
                            }

                        }
                    }
                    //清算速度
                    message = etrCommonService.checkIsBlank(rowData.get(10).toString(), "EtrProject003.settleSpeed", lineNo);
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                    //净投资收益率
                    confirmMsg = checkIsDouble(confirmMsg, lineNo, rowData.get(11).toString(), "EtrProject003.investRate");
                }
                if (StringUtil.isBlank(confirmMsg)) {
                    jsonArrays.add(rowData);
                }else {
                    confirmMsg0 = etrCommonService.StringAddByBr(confirmMsg0, confirmMsg);
                    if (isEntrust) {
                        errorNum++;
                    }
                }

            }
            confirmMsg0=etrCommonService.StringAddByBr(confirmMsg0,
                    getMessage("ETR_ST_009",String.valueOf(errorNum)));
            Map<String, Object> resultMap = new HashMap<>();
            if (StringUtil.isNotBlank(confirmMsg0)) {
                confirmMsg0 += changeLineMark + getMessage("ETR_ST_010");
                resultMap.put("confirmMsg", confirmMsg0);
            }
            resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        }else{
            //数据大于100条页面提示
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", getMessage("ETR_E_0021"));
            AjaxResult errorMsg = new AjaxResult();
            errorMsg.setData(resultMap);
            return errorMsg;
        }
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
        //判断是否大于0,如果不是数值就不用判断是否大于0。
        if(etrCommonService.checkIsNumber(data, labelName, indexId).equals("")){
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsGtZero(data, labelName, indexId));

        }
        return confirmMsg;
    }

    @Override
    public String generateTitle(EtrProjectDto entity, OperationType operationType) {
        return this.getMessage("EtrProject001") + "(" + entity.getPortfolioId() + ")"
                + "-" + this.getMessage("EtrProject001.ps") + "-" + "("
                + this.getMessage("EtrProject001.vdate") + ":" +DateUtil.format(entity.getVdate())
                + ")-(" + this.getMessage("EtrProject001.mdate") + ":"
                +DateUtil.format(entity.getMdate())+ ")-"
                + this.getMessage("EtrProject001.entrustAmt") + ":"
                + entity.getEntrustAmt();
    }
}
