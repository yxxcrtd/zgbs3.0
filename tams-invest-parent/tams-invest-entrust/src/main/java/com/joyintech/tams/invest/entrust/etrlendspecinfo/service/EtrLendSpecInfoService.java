/*
 * 文件名：EtrLendSpecInfo001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrlendspecinfo.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
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
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.entrust.etrlendspecinfo.dto.EtrLendSpecInfoDto;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;

/**
 * 拆解委托画面端service 〈功能详细描述〉
 *
 * @author xiexinting
 * @version 1.0 2018年9月12日
 * @see EtrLendSpecInfoService
 * @since 1.0
 */
@Service
public class EtrLendSpecInfoService extends CommonExportService<EtrLendSpecInfoDto> implements BaseProcessService<EtrLendSpecInfoDto> {

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
    *
    */
   @Autowired
   private EtrCommonService etrCommonService;
    
    /**
     * 拆借委托分页查询 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrLendSpecInfoDto> selectForPage(Map<String, Object> param) {
        if(StringUtil.isNotEmpty((String)param.get("entrustDate"))){
            List<String> list = StringUtil.splitToList(" ",(String)param.get("entrustDate"));
            param.put("startDate", list.get(0));
            param.put("endDate", list.get(2));
        }
        if(StringUtil.isNotEmpty(param.get("type6"))){
            List<String> type6List = StringUtil.splitToList(",",(String)param.get("type6"));
            param.put("type6List", type6List);
        }
        if(StringUtil.isNotEmpty(param.get("ccy"))){
            List<String> ccyList = StringUtil.splitToList(",",(String)param.get("ccy"));
            param.put("ccyList", ccyList);
        }
        if(StringUtil.isNotEmpty(param.get("ps"))){
            List<String> psList = StringUtil.splitToList(",",(String)param.get("ps"));
            param.put("psList", psList);
        }
        if(StringUtil.isNotEmpty(param.get("entrustStatus"))){
            List<String> entrustStatusList = StringUtil.splitToList(",",(String)param.get("entrustStatus"));
            // 去除空字符串
            entrustStatusList = entrustStatusList.stream().filter(i->!i.isEmpty()).collect(Collectors.toList());
            param.put("entrustStatusList", entrustStatusList);
        }
        if(StringUtil.isNotEmpty(param.get("tradeMarket"))){
            List<String> entrustStatusList = StringUtil.splitToList(",",(String)param.get("tradeMarket"));
            param.put("tradeMarketList", entrustStatusList);
        }
        param.put("lendin", DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        param.put("lendout", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        param.put("loginUser", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/findPage", param,
            EtrLendSpecInfoDto.class);

    }

    /**
     * 查询页面组合代码联想文本框 <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getentrustId(String entrustId) {
        User user = SecurityUtil.getLoginUser();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserName());
        map.put("param", entrustId);
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrLendSpecInfo/getentrustId" , map,
                                       SuggestDto.class);
    }

    /**
     * 新建页面组合代码联想下拉框 <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getentrustIdAdd(Map<String, Object> map) {
        String loginUser = SecurityUtil.getLoginUser().getUserName();
        map.put("loginUser", loginUser);
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
            + "/EtrLendSpecInfo/getentrustIdAdd" , map,
            SelectOptionDto.class);
    }

    /**
     * 撤回委托
     * <功能详细描述>
     * @param entity
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrLendSpecInfoDto entity) {
        return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/recall",entity,
            boolean.class);
    }

    /**
     * 关闭委托
     * <功能详细描述>
     * @param entrustId
     * @param closeWay
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId, String closeWay) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/close?entrustId="+entrustId+"&closeWay?="+closeWay,
            boolean.class);
    }

    /**
     * 删除委托
     * <功能详细描述>
     * @param entrustId
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/delete?entrustId="+entrustId,
            boolean.class);
    }

    /**
     * 校验方法
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrLendSpecInfoDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/check", dto,
            ValidMessage.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(EtrLendSpecInfoDto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate = DateUtil.platDateFromCache();
        dto.setEntrustDate(sysDate);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/doComplete",dto,
            Integer.class);
        doDeleteFlow(dto.getWorkflowId());
    }
   /**
    *
    * 流程发起时调用
    */
    @Override
    public void doInsert(EtrLendSpecInfoDto dto, OperationType type){
        if ("submit".equals(dto.getOperateType())) {
            doInsertFlow(dto);
        }
        dto.setOperateType(type.toString());
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/doInsert",dto,
            Integer.class);
    }
    private void doInsertFlow(EtrLendSpecInfoDto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            BeanUtil.copyProperties(etrEntrust,dto);
            etrEntrust.setPs(DictCodeList.交易方向_买入.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            etrEntrust.setEntrustStatus(BussinessDictCodeList.委托状态_流程中.getValue());
            etrEntrust.setFinprodType2(StringUtil.equals(dto.getPs(),DictCodeList.回购拆借方向_拆入.getValue())?DictCodeListExt1.金融产品类型_投管用_拆入.getValue():DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
            if(dto.getPrice()!=null){
                BigDecimal num = new BigDecimal(100);
                etrEntrust.setPrice(dto.getPrice().divide(num));
            }
            BeanUtil.copyProperties(etrEntrustAdd,dto);
            BeanUtil.copyProperties(etrEntrust,dto.getEtrInvestPersonInfo());
            etrEntrustAdd.setBusiPs(dto.getPs());
            etrEntrustAdd.setMinAmt(dto.getMinAmt());
            etrEntrustAdd.setVdate(dto.getVdateAdd());
            etrEntrustAdd.setMdate(dto.getMdateAdd());
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_拆借.getValue());
            etrEntrustAdd.setTermType (dto.getTermType ());
            etrEntrustAdd.setSettleSpreed (dto.getSettleSpreed ());
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            entity.setWorkFlowId(dto.getWorkflowId());
            restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
                Boolean.class);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
    }
    private void doDeleteFlow(String workflowId) {
        Map<String,Object> param = new HashMap<>();
        param.put("workflowId",workflowId);
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doDeleteEtrFlow", param,
            Boolean.class);

    }
    /**
     *
     * 流程修改时调用
     */
    @Override
    public void doUpdate(EtrLendSpecInfoDto dto){
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/doInsert",dto,
            Integer.class);
        if (!StringUtil.equals("revoke", dto.getOperateType()) && !StringUtil.equals("delete", dto.getOperateType()) ) {
            // 先删除流程中数据
            doDeleteFlow(dto.getWorkflowId());
            // 重新insert
            doInsertFlow(dto);
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(EtrLendSpecInfoDto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(dto.getWorkflowId());
                newEntity.setStatus(dto.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(dto));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     *
     * 流程作废时调用
     */
//    @Override
//    public void doDelete(String workflowId){
//        restTemplate.getForObject(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/doDelete?workflowId="+workflowId,
//            Integer.class);
//    }
    /**
     * 修改/查看反显查询
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrLendSpecInfoDto selectByKey(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/selectByKey?entrustId="+entrustId,
            EtrLendSpecInfoDto.class);
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
        exportParam.config("/com/joyintech/tams/invest/entrust/etrlendspecinfo/conf/export_conf.xml")
            .dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<EtrLendSpecInfoDto> selectPageData(Map<String, Object> selectParam) {
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
     * @param
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

        Map<String, Object> cash = restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/getProAvailableCash",param,
            Map.class);

         return cash;
    }

    /**
     *
     * 查询文件上传详情
     * <功能详细描述>
     * @param businessId String
     * @return List<SysUploadFileInfo>
     * @see
     * @since 1.0
     */
    public List<SysUploadFileInfo> selectFileByBusinessId(String businessId){
        return restTemplate.getForList(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrLendSpecInfo/selectFileByBusinessId?businessId="+businessId,
            SysUploadFileInfo.class);
    }
    /**
     *
     * 判断是否为节假日
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public AjaxResult judgeIsHday(Map<String, Object> param){
        String mdateAdd = (String)param.get("mdateAdd");
        String calId = (String)param.get("calId");
        boolean result = dateCalService.isDealCalendarHoliday(mdateAdd,calId);
        AjaxResult msg =new AjaxResult();
        msg.setMessage("到期日为节假日");
        msg.setMessageType(MessageType.错误消息);
        return result == true?msg:null;
    }

    /**
     * 是否为节假日 是：日期顺延  否：不做处理
     * @param param
     * @return
     */
    public Date isHoliday(Map<String, Object> param){
        Date mdateAdd = DateUtil.parse(param.get("mdateAdd").toString());
        String calId = (String)param.get("calId");
        for (int count = 0 ; count < 10 ; count ++){
            boolean result = dateCalService.isDealCalendarHoliday(mdateAdd,calId);
            if (result){
                mdateAdd=DateUtil.addDays(mdateAdd,1);
            }
        }
        return mdateAdd;
    }
    /**
     *
     * 查询授权额度
     * <功能详细描述>
     * @param
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectLimitAmt(EtrLendSpecInfoDto dto){
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", DictCodeList.平安资产类型_拆借.getValue());
        map.put("assetSecType", dto.getAssetType1());
        map.put("tradeMarket", dto.getTradeMarket());
        map.put("ps",dto.getPs());
        return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/common/SelectLimitAmt/selectLimitAmt", map,
            BigDecimal.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrLendSpecInfoDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
                if (dto.isImportFlow()) { // 只有在多对多组合提交时，才为true，供弹出用户选择，选择用户备用，让需要上级审批的流程走到这个人这里，不需要的直接结束
                    Map<String, Object> param = new HashMap<>();
                    param.put(BussinessConstant.是否需要上级审批, true);
                    return param;
                }
                BigDecimal amount = dto.getEntrustAmt();
                if (amount == null) {
                    amount = dto.getEntrustNum();
                }
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amount, LoginUserUtil.getUserName(),
                        BussinessDictCodeList.平安资产类型_拆借.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                        , null, dto.getWorkflowId()
                        );
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
    
 // add by gaopeng 20191219 多组合多资产导入 start
    /**
     * 获取委托列表数据
     *
     * @return
     */
    public EtrLendSpecInfoDto getEntrustListData(String tempList) {
        EtrLendSpecInfoDto dto = new EtrLendSpecInfoDto();
        List<EtrLendSpecInfoDto> etrEntrustDtos = new ArrayList<>();
        JSONArray objects = JSONArray.parseArray(tempList);
        for (int i = 0; i < objects.size(); i++) {
            EtrLendSpecInfoDto etrLendSpecInfoDto = new EtrLendSpecInfoDto();
            JSONArray data = JSONArray.parseArray(((JSONArray) objects.get(i)).toJSONString());
            //根据模板中固定的下标来赋值
            etrLendSpecInfoDto.setIndexId(data.get(0).toString());
            //委托开始日
            etrLendSpecInfoDto.setVdate(DateUtil.parse(data.get(1).toString()));
            //委托结束日
            etrLendSpecInfoDto.setMdate(DateUtil.parse(data.get(2).toString()));
            //组合代码
            etrLendSpecInfoDto.setPortfolioId(data.get(3).toString().trim());
            //交易方向
            etrLendSpecInfoDto.setPs(data.get(4).toString().split(":")[0]);
            //拆借类型
            etrLendSpecInfoDto.setAssetType1(data.get(5).toString().split(":")[0]);
            //币种
            etrLendSpecInfoDto.setCcy(data.get(6).toString().split(":")[0]);
            //委托金额
            etrLendSpecInfoDto.setEntrustAmt(new BigDecimal((data.get(7).toString())));
            //利率
            // add by yushilong 利率为空转换错误问题 20200708 start
            if(StringUtil.isNotBlank(data.get(8).toString())) {
                etrLendSpecInfoDto.setPrice(new BigDecimal((data.get(8).toString())));
            }
            //清算速度
            etrLendSpecInfoDto.setSettleSpreed(Long.valueOf (data.get (12).toString ().split(":")[0]));
            //起息日  清算速度=T+1  起息日=指令开始日+1
            if (StringUtil.equals(DictCodeList.清算速度_T_1.getCodeName(),data.get(12).toString().split(":")[1])){
                etrLendSpecInfoDto.setVdateAdd(DateUtil.addDays(DateUtil.parse(data.get(1).toString()), 1));
            }//若 清算速度=T+0  起息日=指令开始日
            if (StringUtil.equals(DictCodeList.清算速度_T_0.getCodeName(),data.get(12).toString().split(":")[1])){
                etrLendSpecInfoDto.setVdateAdd(DateUtil.parse(data.get(1).toString()));
            }
            //期限
            if(StringUtil.isNotBlank(data.get(9).toString())) {
            etrLendSpecInfoDto.setTermDays(Long.parseLong(data.get(9).toString()));
            }
            //期限品种
            etrLendSpecInfoDto.setTermType(this.getTermType(etrLendSpecInfoDto.getVdateAdd(),etrLendSpecInfoDto.getTermDays()));
            //交易日历
            etrLendSpecInfoDto.setCalendarId(data.get(10).toString().split(":")[0]);
            //交易日历文本
            etrLendSpecInfoDto.setCalendarIdName(data.get(10).toString().split(":")[1]);
            //到期日
            if(StringUtil.isNotBlank(data.get(9).toString())) {
            	etrLendSpecInfoDto.setMdateAdd(DateUtil.addDays(etrLendSpecInfoDto.getVdateAdd(),etrLendSpecInfoDto.getTermDays().intValue()));
            }
            for (int count = 0 ; count < 10 ; count++ ){
                Boolean IsHoliday = dateCalService.isDealCalendarHoliday(etrLendSpecInfoDto.getMdateAdd(),etrLendSpecInfoDto.getCalendarId());
                if (IsHoliday){
                    etrLendSpecInfoDto.setMdateAdd(DateUtil.addDays(etrLendSpecInfoDto.getMdateAdd(),1));
                }
            }
            Map<String, Object> param = new HashMap<>();
            param.put("initValue", data.get(10).toString());
            param.put("page", 1);
            param.put("length", 20);
            Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/selectCalendarId",
                param, SelectOptionDto.class);
            if (!selectOptionDtoPage.getRoot().isEmpty()) {
                etrLendSpecInfoDto.setCalendarIdName(selectOptionDtoPage.getRoot().get(0).getText());
            }
            etrLendSpecInfoDto.setEtrInvestPersonInfo(new BussEtrInvestPersonInfoDto());
            // 投资经理
            etrLendSpecInfoDto.getEtrInvestPersonInfo().setInvManagerId(etrLendSpecInfoDto.getCreateUser());
            // 投资处室
            etrLendSpecInfoDto.getEtrInvestPersonInfo().setRoom(etrLendSpecInfoDto.getCreateDept());
            //最低成交金额
            if(StringUtil.isNotBlank(data.get(13).toString())) {
            etrLendSpecInfoDto.setMinAmt(new BigDecimal((data.get(13).toString())));
            }
            // 备注
            if (StringUtil.isNotBlank(data.get(14).toString())){
                etrLendSpecInfoDto.setRemark(data.get(14).toString());
            }

            //交易场所
            etrLendSpecInfoDto.setTradeMarket (data.get (11).toString ().split(":")[0]);
            param = new HashMap<>();
            param.put("portfolioId", etrLendSpecInfoDto.getPortfolioId());
            param.put("ccy", etrLendSpecInfoDto.getCcy());
            param.put("vdate", etrLendSpecInfoDto.getVdate());
            param.put("calId", etrLendSpecInfoDto.getCalendarId());
            param.put("chlId", etrLendSpecInfoDto.getChlId());
            Map<String, Object> cashMap = this.getProAvailableCash(param);
            // 指令可用现金
            if (cashMap != null && cashMap.get("cashT") != null) {
                etrLendSpecInfoDto.setProAvailableCash(
                    new BigDecimal(cashMap.get("cashT").toString()));
            }
            else {
                etrLendSpecInfoDto.setProAvailableCash(BigDecimal.ZERO);
            }
            etrEntrustDtos.add(etrLendSpecInfoDto);
        }
        dto.setSelectedItem(etrEntrustDtos);
        return dto;
    }

    /**
     * 根据 期限 获取 期限品种
     * @param vdateAdd
     * @param tremDays
     * @return
     */
    public String getTermType(Date vdateAdd, Long tremDays){
        if(StringUtil.isNotEmpty(tremDays) && tremDays <= 21){
            //期限品种小于21天
            if(tremDays == 1){
                return "IBO001";
            }
            if(tremDays > 1 && tremDays <= 7){
                return "IBO007";
            }
            if(tremDays > 7 && tremDays <= 14){
                return "IBO014";
            }
            if(tremDays > 14 && tremDays <=21){
                return "IBO021";
            }
        }
        if (StringUtil.isNotEmpty(vdateAdd) && StringUtil.isNotEmpty(tremDays)){
            Date date = DateUtil.addMonths(vdateAdd, 1);
            Long days = DateUtil.getBetweenDayNumber(vdateAdd,date);
            if (tremDays <= days){
                return "IBO1M";
            }
            date = DateUtil.addMonths(vdateAdd, 2);
            days = DateUtil.getBetweenDayNumber(vdateAdd,date);
            if (tremDays <= days){
                return "IBO2M";
            }
            date = DateUtil.addMonths(vdateAdd, 3);
            days = DateUtil.getBetweenDayNumber(vdateAdd,date);
            if (tremDays <= days){
                return "IBO3M";
            }
            date = DateUtil.addMonths(vdateAdd, 4);
            days = DateUtil.getBetweenDayNumber(vdateAdd,date);
            if (tremDays <= days){
                return "IBO4M";
            }
            date = DateUtil.addMonths(vdateAdd, 6);
            days = DateUtil.getBetweenDayNumber(vdateAdd,date);
            if (tremDays <= days){
                return "IBO6M";
            }
            date = DateUtil.addMonths(vdateAdd, 9);
            days = DateUtil.getBetweenDayNumber(vdateAdd,date);
            if (tremDays <= days){
                return "IBO9M";
            }
            date = DateUtil.addMonths(vdateAdd, 12);
            days = DateUtil.getBetweenDayNumber(vdateAdd,date);
            if (tremDays <= days){
                return "IBO1Y";
            }
        }
        return null;
    }

    /**
     * 验证导入的数据
     * @param
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
            List<String> rowData = objects.get(i);
                // 委托开始日日期非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(1).toString(), "EtrLendSpecInfo001.vdate", lineNo));
                // 委托开始日日期格式check
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsDate(rowData.get(1).toString(), "EtrLendSpecInfo001.vdate", lineNo));
                // 委托结束日日期非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(2).toString(), "EtrLendSpecInfo001.mdate", lineNo));
                // 委托结束日日期格式check
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsDate(rowData.get(2).toString(), "EtrLendSpecInfo001.mdate", lineNo));
                /**** 委托开始日与终止日必须是平台日 作者：mrbbp@qq.com 2020/6/20 14:20****/
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsPlateDate(rowData.get(1).toString(),
                        rowData.get(2).toString(),lineNo));

//                /**** 交易场所和期限品种的check****/
//                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                        etrCommonService.checkMarketQx(rowData.get(11).toString(),rowData.get(9).toString(),lineNo));



                // 组合代码非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(3).toString(), "EtrLendSpecInfo001.portfolioId", lineNo));

                 /**** 交易场所和清算速度的check****/
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkMarketQs(rowData.get(11).toString(),rowData.get(12).toString(),lineNo));

                // 组合代码check
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkPortfolioIdExist(rowData.get(3).toString(), "EtrLendSpecInfo001.portfolioId", lineNo));

//                // modify by yushilong 起息日到期日期限为空则 不进行相关校验 20200708 start
//                /**** 起息日与到期日check****/
//                if (StringUtil.isNotEmpty(rowData.get(9).toString()) &&
//                    StringUtil.isNotEmpty(rowData.get(10).toString()) &&
//                    StringUtil.isNotEmpty(rowData.get(11).toString())) {
//                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                            etrCommonService.checkQDQ(rowData.get(9).toString(),rowData.get(10).toString(),rowData.get(11).toString(),lineNo));
//                }
//                // modify by yushilong 起息日到期日期限为空则 不进行相关校验 20200708 end

                // 交易方向非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(4).toString(), "EtrLendSpecInfo001.ps", lineNo));

//                // modify by yushilong 起息日到期日期限为空则 不进行相关校验 20200708 start
//                /**** 起息日与指令开始check****/
//                if (StringUtil.isNotEmpty(rowData.get(9).toString())) {
//                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                            etrCommonService.checkQ(rowData.get(1).toString(),rowData.get(9).toString(),lineNo));
//                }
//                // modify by yushilong 起息日到期日期限为空则 不进行相关校验 20200708 end

                //拆借类型非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(5).toString(), "EtrLendSpecInfo002.type6", lineNo));
                // 拆借类型是特殊目的载体时 交易方向只能为拆入
                if (rowData.get(5).toString().contains(DictCodeList.拆借类型_特殊目的载体.getValue())
                    &&rowData.get(4).toString().contains(DictCodeList.回购拆借方向_拆出.getValue())) {
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        getMessage("ETR_LSSMTM_001", lineNo));
                }
                // 币种非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(6).toString(), "EtrBrowDepoInfo001.ccy", lineNo));
                // 委托金额(元)非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(7).toString(), "EtrLendSpecInfo001.entrustAmt", lineNo));
                // 清算速度非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService.checkIsBlank(
                        rowData.get(12).toString(), "EtrLendSpecInfo001.settleSpreed", lineNo));
            // 委托金额(元)是数值
//                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                    etrCommonService.checkIsNumber(rowData.get(7).toString(), "EtrLendSpecInfo001.entrustAmt", lineNo));
                // 委托金额(元)是大于0
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsGtZero(rowData.get(7).toString(), "EtrLendSpecInfo001.entrustAmt", lineNo));
                // 利率（%）是数值
//                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                    etrCommonService.checkIsNumber(rowData.get(8).toString(), "EtrLendSpecInfo001.price", lineNo));


                /** 利率 大于 0 **/
                if (StringUtil.isNotBlank(rowData.get(8))){
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsGtZero(rowData.get(8).toString(), "EtrLendSpecInfo001.price", lineNo));
                }

                // modify by yushilong 起息日到期日期限为空则 不进行相关校验 20200708 start
                // 起息日日期非空
                /*confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(9).toString(), "EtrLendSpecInfo002.vdateAdd", lineNo));*/
                // 起息日日期格式check
//                if (StringUtil.isNotEmpty(rowData.get(9).toString())) {
//                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                            etrCommonService.checkIsDate(rowData.get(9).toString(), "EtrLendSpecInfo002.vdateAdd", lineNo));
//                }
                // 到息日日期非空
                /*confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                        etrCommonService.checkIsBlank(rowData.get(10).toString(), "EtrLendSpecInfo002.mdateAdd", lineNo));*/
//                if (StringUtil.isNotEmpty(rowData.get(10).toString())) {
//                    // 到息日日期格式check
//                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                            etrCommonService.checkIsDate(rowData.get(10).toString(), "EtrLendSpecInfo002.mdateAdd", lineNo));
//                }
                //期限非空
                /*confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(11).toString(), "EtrLendSpecInfo002.term", lineNo));*/
                /** 期限 大于 0 **/
                if (StringUtil.isNotEmpty(rowData.get(9))) {
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsGtZero(rowData.get(9).toString(), "EtrLendSpecInfo002.term", lineNo));
                }
                // modify by yushilong 起息日到期日期限为空则 不进行相关校验 20200708 end
                //交易日历非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(10).toString(), "EtrLendSpecInfo002.calendarId", lineNo));

                // 交易场所非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(11).toString(), "EtrLendSpecInfo001.tradeMarket", lineNo));


                //最低成交金额是数值
//                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                    etrCommonService.checkIsNumber(rowData.get(13).toString(), "EtrLendSpecInfo002.minAmt", lineNo));
                //最低成交金额是大于0
                if (StringUtil.isNotBlank(rowData.get(13))){
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsGtZero(rowData.get(13).toString(), "EtrLendSpecInfo002.minAmt", lineNo));
                }

                if (StringUtil.isBlank(confirmMsg)) {
                    jsonArrays.add(rowData);
                } else {
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
    // add by gaopeng 20191219 多组合多资产导入 end

    // add by zhangrui1 start
    /**
     * {@inheritDoc}
     */
    @Override
    public void doBack(EtrLendSpecInfoDto entity) {
        // do nothing just for aspect
    	//产品化_所有投资指令预占需求_拆借类资产指令 add by longpeng 20201119 start
    	AllTradeCommonUtil.deletePosition(null, entity.getEntrustId());
    	//产品化_所有投资指令预占需求_拆借类资产指令 add by longpeng 20201119 end
    }
    // add by zhangrui1 end
    
}
