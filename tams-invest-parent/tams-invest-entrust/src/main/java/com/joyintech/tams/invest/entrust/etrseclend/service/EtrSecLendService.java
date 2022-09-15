/*
 * 文件名：EtrInvestBondInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrseclend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.entrust.etrlendspecinfo.dto.EtrLendSpecInfoDto;
import com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 债券投资委托画面端service 〈功能详细描述〉
 *
 * @author wangzhuangzhuang
 * @version 1.0 2019年7月5日
 * @see EtrSecLendService
 * @since 1.0
 */
@Service
public class EtrSecLendService extends CommonExportService<EtrSecLendDto> implements BaseProcessService<EtrSecLendDto> {
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
     * 质押券组件
     */
    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 债券投资委托查询 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrSecLendDto> selectForPage(Map<String, Object> param) {
        //委托日期
        if (StringUtil.isNotEmpty((String)param.get("entrustDate"))) {
            List<String> dateList = StringUtil.splitToList(" ", (String)param.get("entrustDate"));
            param.put("startDate", dateList.get(0));
            param.put("endDate", dateList.get(2));
        }
        //组合代码
        String portfolioId = (String)param.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            param.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
        //标的债券
        String finprodId = (String)param.get("finprodId");
        if(StringUtil.isNotEmpty(finprodId)){
            param.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA,finprodId));
        }
        //币种
        String ccy = (String)param.get("ccy");
        if(StringUtil.isNotEmpty(ccy)){
            param.put("ccyList", StringUtil.splitToList(GlobalConstant.COMMA,ccy));
        }
        //交易场所
        String tradeMarket = (String)param.get("tradeMarket");
        if(StringUtil.isNotEmpty(tradeMarket)){
            param.put("tradeMarketList", StringUtil.splitToList(GlobalConstant.COMMA,tradeMarket));
        }
        //有效状态
        String entrustStatus = (String)param.get("entrustStatus");
        if(StringUtil.isNotEmpty(entrustStatus)){
            // 去除空字符串值
            List<String> entrustStatusList = StringUtil.splitToList(GlobalConstant.COMMA, entrustStatus);
            entrustStatusList = entrustStatusList.stream().filter(i->!i.isEmpty()).collect(Collectors.toList());
            param.put("entrustStatusList", entrustStatusList);
        }
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
//        配置单位 edit by yeming 20210802 start
        Page<EtrSecLendDto> page = restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/findPage", param,
                EtrSecLendDto.class);
        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        for(EtrSecLendDto dto:page.getRoot()){
            if(dto.getTotalAmt() != null) {
                dto.setTotalAmt(dto.getTotalAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
            if(StringUtil.isNotEmpty(dto.getNextUser())){
                String sysUsers = userList.stream().filter(x-> Arrays.asList(dto.getNextUser().split(",")).contains(x.getUserName()))
                        .map(SysUser::getRealName)
                        .collect(Collectors.joining(",","",""));
                dto.setNextUser(sysUsers);
            }
        }
        return page;
//        return restTemplate.postForPage(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/findPage", param,
//            EtrSecLendDto.class);
        //配置单位 edit by yeming 20210802 end
    }
    /**
     * 资产代码：下拉框
     *
     * @param parameters 画面参数
     * @return 资产代码：下拉框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> parameters) {
        return restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/getFinprodId", parameters, SelectOptionDto.class);
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
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/getSecManageAcctId",
                parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String) parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String) parameters.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrSecLendDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/check", dto,
                ValidMessage.class);
    }

    /**
     * 工作流完成方法
     */
    @Override
    public void doComplete(EtrSecLendDto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate = DateUtil.platDateFromCache();
        dto.setEntrustDate(sysDate);
        deleteWflEntity(dto);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/doComplete",dto,
                Integer.class);
        //新增或修改质押券信息
//         if(bussRepoSecService != null){
//                bussRepoSecService.insert(dto.getBussRepoSec(),dto.getEntrustId());
//         }
    }
    /**
     *
     * 流程发起时调用
     */
    @Override
    public void doInsert(EtrSecLendDto dto, OperationType type){
        dto.setOperateType(type.toString());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/doInsert",dto,
                Integer.class);
        //新增或修改质押券信息
        if(bussRepoSecService != null && dto.getBussRepoSec() != null){
            bussRepoSecService.deletePledgeBond(dto.getEntrustId(), dto.getBussRepoSec().getPageFrom());
            bussRepoSecService.insert(dto.getBussRepoSec(),dto.getEntrustId());
        }
    }

    @Override
    public void doBack(EtrSecLendDto entity) {
        doBackFlow(entity);
        //deleteWflEntity(entity);
    }



    /**
     * 删除流程中数据和流水
     * @param dto
     */
    public void deleteWflEntity(EtrSecLendDto dto) {
        restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME+"/EtrSecLend/doDeleteFlow",
                dto, Void.class);
    }

    /**
     *
     * 流程修改时调用
     */
    @Override
    public void doUpdate(EtrSecLendDto dto){
        deleteWflEntity(dto);
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/doInsert",dto,
                Integer.class);

        //新增或修改质押券信息
        if(bussRepoSecService != null && dto.getBussRepoSec() != null){
            bussRepoSecService.deletePledgeBond(dto.getEntrustId(), dto.getBussRepoSec().getPageFrom());
            bussRepoSecService.insert(dto.getBussRepoSec(),dto.getEntrustId());
        }
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
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/delete?entrustId="+entrustId,
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
    public EtrSecLendDto selectByKey(String entrustId,String finprodId,String portfolioId) {
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
                        + "/EtrSecLend/selectByKey?entrustId=" + entrustId+"&finprodId="+finprodId+"&portfolioId="+portfolioId,
                EtrSecLendDto.class);
    }


    /**
     * 撤回委托 <功能详细描述>
     *
     * @param dto 实体类
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrSecLendDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/recall", dto,
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
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/close?entrustId="+entrustId+"&closeWay="+closeWay,
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
        exportParam.config("/com/joyintech/tams/invest/entrust/etrseclend/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<EtrSecLendDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName,"transactioncom")){
            cell.setCellValue(new BigDecimal(100).multiply(StringUtil.isNotEmpty(value)?new BigDecimal(value.toString()):BigDecimal.ZERO)+"%");
        }
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
        param.put("sysDate",DateUtil.format(sysDate));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(sysDate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(sysDate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate)), 1, holidayList, "1");
            param.put("sysDateT1",DateUtil.format(tradeDay));
        }
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrSecLend/getAssetAvailablePosition",param,
                Map.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        EtrSecLendDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getTotalAmt()/*.multiply(new BigDecimal(10000))*/, LoginUserUtil.getUserName(),
                        DictCodeListExt2.平安资产类型_债券借贷.getValue(), null, dto.getTradeMarket(), dto.getBusiPs()
                        // 资产类型改为授权类型 add by renping 2019-08-06  start
                        , null, dto.getWorkflowId()
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
     * 退回更新流程,删除流水
     * @param dto
     */
    public void doBackFlow(EtrSecLendDto dto) {
        restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME+"/EtrSecLend/doBackFlow",
                dto, Void.class);
    }
}
