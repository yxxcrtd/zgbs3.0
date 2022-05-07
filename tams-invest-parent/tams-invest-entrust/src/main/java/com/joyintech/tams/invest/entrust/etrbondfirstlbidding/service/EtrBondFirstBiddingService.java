/*
 * 文件名：EtrBondFirstBiddingService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrbondfirstlbidding.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.common.base.entity.EtrEntrustBidPrice;
import com.joyintech.tams.component.base.entrust.impl.dto.TrdCommandDispatcher001Dto;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrbondfirstlbidding.dto.EtrBondFirstLBiddingDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrInvestPersonInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 债券一级投标委托处理详细 〈功能详细描述〉
 *
 * @author yangjm
 * @version 1.0 2019年5月6日
 * @see EtrBondFirstBiddingService
 * @since 1.0
 */
@Service
public class EtrBondFirstBiddingService extends CommonExportService<EtrBondFirstLBiddingDto> implements BaseProcessService<EtrBondFirstLBiddingDto> {

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
     * 委托共通
     */
    @Autowired
    private EtrCommonService etrCommonService;

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
                        + "/EtrBondFirstLBidding/getentrustId", map,
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
                        + "/EtrBondFirstLBidding/getfinprodId?finprodId=" + param + "&pageId=" + pageId,
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
        param.put("userIsNoOrOn", "no");
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrBondFirstLBidding/getentrustIdAdd"
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
                        + "/EtrBondFirstLBidding/getfinprodIdForAdd"
                , param,
                SelectOptionDto.class);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(EtrBondFirstLBiddingDto dto, OperationType operationType) {

        dto.setOperateType(operationType.toString());
//        Date sysDate = DateUtil.platDateFromCache();
//        dto.setEntrustDate(sysDate);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        if (dto.getBidPriceList() != null && !dto.getBidPriceList().isEmpty()) {
            for (EtrEntrustBidPrice ePrice : dto.getBidPriceList()) {
                ePrice.setEntrustId(dto.getEntrustId());
            }
        }
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/doComplete", dto,
                Integer.class);


    }

    @Override
    public void doInsert(EtrBondFirstLBiddingDto entity, OperationType type) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/doInsert", entity,
                Integer.class);
    }

    @Override
    public void doUpdate(EtrBondFirstLBiddingDto entity) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/doUpdate", entity,
                Integer.class);
    }

    @Override
    public void doBack(EtrBondFirstLBiddingDto entity) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/doBack", entity,
                Integer.class);
    }

    /**
     * 分页查询债券一级投标委托信息
     * <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrBondFirstLBiddingDto> selectForPage(Map<String, Object> param) {
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        //委托日期区间查询
        if (StringUtil.isNotEmpty((String) param.get("entrustDate"))) {
            List<String> dateList = StringUtil.splitToList(" ", (String) param.get("entrustDate"));
            param.put("startDate", dateList.get(0));
            param.put("endDate", dateList.get(2));
        }
        //投标日期区间查询
        if (StringUtil.isNotEmpty((String) param.get("bidDate"))) {
            List<String> dateList = StringUtil.splitToList(" ", (String) param.get("bidDate"));
            param.put("bidStartDate", dateList.get(0));
            param.put("bidEndDate", dateList.get(2));
        }
        String bondType = (String) param.get("bondType");
        if (StringUtil.isNotEmpty(bondType)) {
            List<String> bondTypeList = StringUtil.splitToList(",", bondType);
            param.put("bondTypeList", bondTypeList);
        }
        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        //配置单位 edit by yeming 20210730 start
        Page<EtrBondFirstLBiddingDto> page = restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/findPage",
                param, EtrBondFirstLBiddingDto.class);
        for(EtrBondFirstLBiddingDto dto:page.getRoot()){
            if(dto.getBidPriceTotal() != null) {
                dto.setBidPriceTotal(dto.getBidPriceTotal().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
            if(dto.getWinPriceTotal() != null) {
                dto.setWinPriceTotal(dto.getWinPriceTotal().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
            if(StringUtil.isNotEmpty(dto.getNextUser())){
                String sysUsers = userList.stream().filter(x-> Arrays.asList(dto.getNextUser().split(",")).contains(x.getUserName()))
                        .map(SysUser::getRealName)
                        .collect(Collectors.joining(",","",""));
                dto.setNextUser(sysUsers);
            }
        }
        return page;
        //配置单位 edit by yeming 20210730 end
    }


    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrBondFirstLBiddingDto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
                BigDecimal amount = dto.getEntrustNum();
                if (amount == null) {
                    amount = dto.getEntrustAmt();
                }
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amount, LoginUserUtil.getUserName(),
                        BussinessDictCodeList.平安资产类型_债券.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                        , dto.getFinprodId(), entity.getWorkflowId()
                );
                return commonBusinessService.是否需要上级审批_委托用(params);
            } catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        } else {
            return null;
        }
    }

    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrBondFirstLBiddingDto dto) {
        dto.setCloseWay(BussinessDictCodeList.委托关闭方式__人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/check", dto,
                ValidMessage.class);
    }

    /**
     * 组合可用现金查询
     * <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate", DateUtil.format(sysDate));
        String calId = (String) param.get("calId");
        if (StringUtil.isNotEmpty(calId)) {
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(sysDate)), DateUtil.addDay(DateUtil.parse(DateUtil.format(sysDate)), 15), calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate)), 1, holidayList, "1");
            param.put("sysDateT1", DateUtil.format(tradeDay));
        }
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/getProAvailableCash", param,
                Map.class);
    }

    /**
     * 删除投资委托
     * <功能详细描述>
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/delete?entrustId=" + entrustId,
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
    public EtrBondFirstLBiddingDto selectByKey(String entrustId, String finprodId, String portfolioId) {
        if (!StringUtil.isNotEmpty(entrustId)) {
            entrustId = "";
        }
        if (!StringUtil.isNotEmpty(finprodId)) {
            finprodId = "";
        }
        if (!StringUtil.isNotEmpty(portfolioId)) {
            portfolioId = "";
        }
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrBondFirstLBidding/selectByKey?entrustId=" + entrustId + "&finprodId=" + finprodId + "&portfolioId=" + portfolioId,
                EtrBondFirstLBiddingDto.class);
    }


    /**
     * 撤回委托 <功能详细描述>
     *
     * @param dto 实体类
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrBondFirstLBiddingDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/recall", dto,
                boolean.class);
    }

    /**
     * 关闭委托
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId, String closeWay) {
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/close?entrustId=" + entrustId + "&closeWay=" + closeWay,
                boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<EtrBondFirstLBiddingDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
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
        exportParam.config("/com/joyintech/tams/invest/entrust/etrbondfirstlbidding/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }

    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if (StringUtil.equals(propName, "transactioncom")) {
            cell.setCellValue(value + "%");
        }
    }

    /**
     * 查询授权额度
     * <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectLimitAmt(EtrBondFirstLBiddingDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", BussinessDictCodeList.平安资产类型_一级投标债券.getValue());
        map.put("assetSecType", dto.getAssetType1());
        map.put("tradeMarket", dto.getTradeMarket());
        map.put("ps", BussinessDictCodeList.交易方向_买入.getValue());
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/SelectLimitAmt/selectLimitAmt", map,
                BigDecimal.class);
    }

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
    public List<EtrBondFirstLBiddingDto> getEntrustListData(String tempList) {
        List<EtrBondFirstLBiddingDto> etrBondFirstLBiddingDtoList = new ArrayList<>();
        JSONArray objects = JSONArray.parseArray(tempList);
        for (int i = 0; i < objects.size(); i++) {
            EtrBondFirstLBiddingDto dto = new EtrBondFirstLBiddingDto();
            JSONArray rowData = JSONArray.parseArray(((JSONArray) objects.get(i)).toJSONString());
            //根据模板中固定的下标来赋值
            dto.setIndexId(rowData.get(0).toString().trim());
            dto.setVdate(DateUtil.parse(rowData.get(1).toString().trim()));
            dto.setMdate(DateUtil.parse(rowData.get(2).toString().trim()));
            dto.setPortfolioId(rowData.get(3).toString().trim());
            dto.setFinprodMarketId(rowData.get(4).toString().trim());
            //根据资产代码查出联动值
            Map<String, Object> map = new HashMap<>();
            map.put("search", rowData.get(4).toString());
            map.put("page", 1);
            map.put("length", 20);
            Page<SelectOptionDto> finprodIds = getfinprodIdForAdd(map);
            SelectOptionDto selectOptionDto = finprodIds.getRoot().get(0);
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(selectOptionDto.getObject()));
            dto.setTradeMarket(jsonObject.get("tradeMarket").toString());
            dto.setCcy(jsonObject.get("ccy").toString());
            dto.setAssetType1(jsonObject.get("assetType1").toString());
            dto.setAssetType10(jsonObject.get("assetType10").toString());
            dto.setAssetType11(jsonObject.get("assetType11").toString());
            long lt = Long.parseLong(jsonObject.get("biddate").toString());
            Date date = new Date(lt);
//            dto.setEntrustDate(date);
            dto.setEntrustDate(DateUtil.parse(rowData.get(12).toString().trim()));
            dto.setFinprodId(jsonObject.get("finprodId").toString());
            //缴款方式
            String etrPayType = rowData.get(5).toString().trim();
            dto.setEtrPayType(etrPayType.split(":")[0]);
            //投资目的
            String invAim = rowData.get(6).toString().trim();
            dto.setInvAim(invAim.split(":")[0]);
            //交易对手
            selectOptionDto = getCounterInfo(rowData.get(7).toString());
            dto.setCounterId(selectOptionDto == null ? "":selectOptionDto.getId());
            dto.setCounterIdName(selectOptionDto == null ? "":selectOptionDto.getText());
            dto.setBidPrice(rowData.get(8).toString().trim());
            dto.setBidPrinAmt(BigDecimal.valueOf(Double.valueOf(rowData.get(9).toString().trim())));
            dto.setDealAmount(BigDecimal.valueOf(Double.valueOf(rowData.get(10).toString().trim())));
            // 主承销商
            selectOptionDto = getMainConsigneeInfo(rowData.get(11).toString().trim());
            dto.setMainConsignee(selectOptionDto == null ? "":selectOptionDto.getId());
            //指令可用现金
            Map<String, Object> param = new HashMap<>();
            param.put("finprodId", jsonObject.get("finprodId").toString());
            param.put("portfolioId", dto.getPortfolioId());
            param.put("ccy", dto.getCcy());
            param.put("vdate", dto.getVdate());
//            Map<String, Object> proAvailableCash = getProAvailableCash(param);
            Map<String, Object> proAvailableCash = etrCommonService.getProAvailableCash(param);
            dto.setEtrAvailableCash(BigDecimal.valueOf(Double.valueOf(proAvailableCash.get("cashT").toString())));
            //指令可用持仓
            EtrSecPositionDto etrSecPositionDto = new EtrSecPositionDto();
            Date sysDate = DateUtil.platDateFromCache();
            etrSecPositionDto.setCdate(DateUtil.parse(DateUtil.format(sysDate)));
            etrSecPositionDto.setFinprodId(jsonObject.get("finprodId").toString());
            etrSecPositionDto.setPortfolioId(dto.getPortfolioId());
            etrSecPositionDto.setCcy(dto.getCcy());
            BigDecimal assetAvailablePosition = etrCommonService.getAssetAvailablePosition(etrSecPositionDto);
            dto.setEtrAvailablePosition(BigDecimal.valueOf(Double.valueOf(assetAvailablePosition.toString())));
            dto.setEtrInvestPersonInfo(new EtrInvestPersonInfoDto());
            // 投资经理
            dto.getEtrInvestPersonInfo().setInvManagerId(dto.getCreateUser());
            // 投资处室
            dto.getEtrInvestPersonInfo().setRoom(dto.getCreateDept());
            etrBondFirstLBiddingDtoList.add(dto);
        }
        return etrBondFirstLBiddingDtoList;
    }

    /**
     * 查询交易对手信息
     *
     * @return
     */
    private SelectOptionDto getCounterInfo(String initValue) {
        SelectOptionDto selectOptionDto = null;
        if(StringUtil.isNotEmpty(initValue)){
            Map<String, Object> map = new HashMap<>();
            map.put("initValue", initValue);
            map.put("page", 1);
            map.put("length", 20);
            map.put("counterType", null);
            Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterId",
                    map, SelectOptionDto.class);
            if (!selectOptionDtoPage.getRoot().isEmpty()) {
                selectOptionDto = selectOptionDtoPage.getRoot().get(0);
            }
        }
        return selectOptionDto;
    }

    /**
     * 查询主承销商信息
     *
     * @return
     */
    private SelectOptionDto getMainConsigneeInfo(String initValue) {
        SelectOptionDto selectOptionDto = null;
        if(StringUtil.isNotEmpty(initValue)){
            Map<String, Object> map = new HashMap<>();
            map.put("initValue", initValue);
            map.put("page", 1);
            map.put("length", 20);
            Page<SelectOptionDto> selectOptionDtoPage = getMainConsignee(map);
            if (!selectOptionDtoPage.getRoot().isEmpty()) {
                selectOptionDto = selectOptionDtoPage.getRoot().get(0);
            }
        }
        return selectOptionDto;
    }

    /**
     * 查询交易对手名称
     *
     * @return
     */
    public String getCounterName(String counterName) {
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/isCounterName?counterName=" + counterName,
                String.class);
    }

    /**
     * 验证导入的数据
     *
     * @param fileName 导入的数据
     * @return
     */
    public AjaxResult checkImportData(String fileName) {
        try {
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
            for (int i = 1; i < objects.size(); i++) {
                String confirmMsg = "";
                List<String> rowData = objects.get(i);
                // excel的行号
                String lineNo = String.valueOf(i + 1);

                //委托开始日调用共通check非空和是否正确
                String message = etrCommonService.checkIsBlank(rowData.get(1).toString(), "EtrBondFirstLBidding002.vdate", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                message = etrCommonService.checkIsDate(rowData.get(1).toString(), "EtrBondFirstLBidding002.vdate", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);

                //投标日期调用共通check非空和是否正确
                message = etrCommonService.checkIsBlank(rowData.get(12).toString(), "EtrBondFirstLBidding002.bidDate", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                message = etrCommonService.checkIsDate(rowData.get(12).toString(), "EtrBondFirstLBidding002.bidDate", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);

                //委托结束日
                message = etrCommonService.checkIsBlank(rowData.get(2).toString(), "EtrBondFirstLBidding002.mdate", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                message = etrCommonService.checkIsDate(rowData.get(2).toString(), "EtrBondFirstLBidding002.mdate", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);

                // 委托开始、终止日是否是平台日check
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        EtrCommonService.checkIsPlateDate(rowData.get(1).toString(), rowData.get(2).toString(), lineNo));

                //组合代码
                message = etrCommonService.checkIsBlank(rowData.get(3).toString(), "EtrBondFirstLBidding002.portfolioId", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                message = etrCommonService.checkPortfolioIdExist(rowData.get(3).toString(), "EtrBondFirstLBidding002.portfolioId", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);

                //资产代码
                message = etrCommonService.checkIsBlank(rowData.get(4).toString(), "EtrBondFirstLBidding002.finprodId", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                if (StringUtil.isNotEmpty(rowData.get(4).toString())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("search", rowData.get(4).toString());
                    map.put("page", 1);
                    map.put("length", 20);
                    Page<SelectOptionDto> finprodIds = getfinprodIdForAdd(map);
                    if (finprodIds.getRoot().isEmpty()) {
                        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage("ETR_E_0017", getMessage("EtrBondFirstLBidding002.finprodId"), lineNo));
                    }
                }

                //缴款方式
                message = etrCommonService.checkIsBlank(rowData.get(5).toString(), "EtrBondFirstLBidding002.payType", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);

                //投资目的
                message = etrCommonService.checkIsBlank(rowData.get(6).toString(), "EtrBondFirstLBidding002.invAim", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);

                //交易对手
                message = etrCommonService.checkIsBlank(rowData.get(7).toString(), "EtrInvestPersonInfo002.counterId", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                if (StringUtil.isNotBlank(rowData.get(7).toString())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("search", rowData.get(7).toString());
                    map.put("page", 1);
                    map.put("length", 20);
                    map.put("counterType", DictCodeListExt1.交易对手类型_市场机构.getValue());
                    Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME +
                            "/common/EntrustCommonPersonSelect/getCounterId", map, SelectOptionDto.class);
                    if (selectOptionDtoPage.getRoot().isEmpty()) {
                        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage("ETR_E_0017", getMessage("EtrInvestPersonInfo002.counterId"), lineNo));
                    }
                }
                //投标标位
                confirmMsg = checkBidPrice(confirmMsg, lineNo, rowData.get(8).toString(), "EtrBondFirstLBidding001.bidPrice");
                //投标券面总额
                confirmMsg = checkIsDouble(confirmMsg, lineNo, rowData.get(9).toString(), "EtrBondFirstLBidding001.bidPriceTotal");
                //最低成交金额
                confirmMsg = checkIsDouble(confirmMsg, lineNo, rowData.get(10).toString(), "EtrBondFirstLBidding003.dealAmount");
//            主承销商
                message = etrCommonService.checkIsBlank(rowData.get(11).toString(), "EtrBondFirstLBidding002.mainConsignee", lineNo);
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, message);
                if (StringUtil.isBlank(confirmMsg)) {
                    jsonArrays.add(rowData);
                } else {
                    totalMsg = etrCommonService.StringAddByBr(totalMsg, confirmMsg);
                    errorNum++;
                }
            }
            totalMsg = etrCommonService.StringAddByBr(totalMsg, getMessage("ETR_C_0001", String.valueOf(errorNum)));
            Map<String, Object> resultMap = new HashMap<>();
            if (StringUtil.isNotBlank(totalMsg)) {

                resultMap.put("confirmMsg", totalMsg);
            }
            resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        }catch (IndexOutOfBoundsException e){
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", "文件格式不正确");
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
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
        //判断是否大于0
        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsGtZero(data, labelName, indexId));
        return confirmMsg;
    }
    /**
     * check 投标标位
     * @param confirmMsg 错误信息
     * @param indexId 序号
     * @param data 数据
     * @param labelName 字段名称
     * @return
     */
    private String checkBidPrice(String confirmMsg, String indexId, String data, String labelName) {
        //判断是否为空
        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(data, labelName, indexId));
        //判断是否是数值
        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsNumber(data, labelName, indexId));
        //判断是否数值大于0小于等于100
        confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsGtZeroLtHundred(data, labelName, indexId));
        return confirmMsg;
    }

    /**
     * 验证债券是否截标
     * @return
     */
    public Integer checkIsBidClose(EtrBondFirstLBiddingDto dto){
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/checkIsBidClose", dto,
                Integer.class);
    }
    /**
     * 获取新建界面的主承销商下拉框
     *
     * @param param 画面参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getMainConsignee(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrBondFirstLBidding/getMainConsignee"
                , param,
                SelectOptionDto.class);
    }

    /**
     * 根据委托代码查询一投标债的交易人员的信息
     * @param
     * @return
     */
    public List<TrdCommandDispatcher001Dto> getTraders(TrdCommandDispatcher001Dto dto) {
        return	restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdCommandDispatcher001/getTraders",dto,TrdCommandDispatcher001Dto.class);

    }
}