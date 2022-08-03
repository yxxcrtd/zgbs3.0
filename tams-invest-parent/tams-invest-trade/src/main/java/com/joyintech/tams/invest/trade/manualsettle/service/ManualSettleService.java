package com.joyintech.tams.invest.trade.manualsettle.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.trade.manualsettle.dto.ManualSettleDto;
import com.joyintech.tams.invest.trade.manualsettle.dto.ManualSettleFlowDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.流程节点_审批生效;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;

/**
 * @author zhangnian
 * @ClassName ManualSettleService
 * @description TODO
 * @date 2020/10/21 0021 16:42
 **/
@Service
public class ManualSettleService extends CommonExportService<ManualSettleDto>
        implements BaseProcessService<ManualSettleDto> {

    @Value("${application.micorservice.contextPath.tamsServer:http://127.0.0.1:8080/tams}")
    private String tamsServer;


    @Override
    public void doComplete(ManualSettleDto dto, OperationType operationType){
            //处理业务数据
            handleBusData(dto);
            //交收、回溯分别按有效、撤销状态处理持仓数据
            //交收失败
            if(StringUtil.equals(dto.getPageModel(), DictCodeListExt3.交收操作类型_交收.getValue())
                    ||StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_回溯.getValue())
                    ||StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_交收失败.getValue())){
                  settleCash(dto);
            }
        //信用保护凭证买入、信用保护凭证卖出的交收失败风险调用共通处理
        if(StringUtil.equals(dto.getPageModel(), DictCodeListExt3.交收操作类型_交收失败.getValue())
            &&(StringUtil.equals(dto.getBusiType(),"27") || StringUtil.equals(dto.getBusiType(),"28"))){
            String url = String.format("/interf/risk/riskTrial/doOccupy/exchangeTrd/%s/%s/%s", "ManualSettle001", 系统动作_新建.getValue(), 流程节点_审批生效.getValue());
            restTemplate.postForObject(tamsServer + url, dto, AjaxResult.class);
        }
    }

    /**
     *功能描述：交收头寸处理
     * @author zhangnian
     * @date 2020/10/22 0022
     * @param  * @param dto
     * @return void
    */
    public void settleCash(ManualSettleDto dto){
        restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/ManualSettle/settleCash", dto,
                Integer.class);
    }

    /**
     *功能描述 ：交收失败
     * @author zhangnian
     * @date 2020/10/22 0022
     * @param  * @param dto
     * @return void
    */
    public void recall(ManualSettleDto dto){
        restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/ManualSettle/recall", dto,
                Integer.class);
    }

    /**
     *功能描述:处理业务数据
     * @author zhangnian
     * @date 2020/10/22 0022
     * @param  * @param entity
     * @return void
    */
    public void handleBusData(ManualSettleDto entity){
        restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/ManualSettle/settle", entity,
                Integer.class);
    }

    @Override
    public void doInsert(ManualSettleDto entity, OperationType type) {

    }

    @Override
    public void doUpdate(ManualSettleDto entity) {

    }

    @Override
    public void doBack(ManualSettleDto entity) {

    }

    public Page<ManualSettleDto> selectForPage(Map<String, Object> param) {
        if (param.get("portfolioId") != null) {
            param.put("portfolioId", StringUtil.splitToList(",", (String)param.get("portfolioId")));
        }
        else {
            param.put("portfolioId", null);
        }
        param.put("userName", LoginUserUtil.getUserName());
        Page<ManualSettleDto> page = restTemplate.postForPage(
                ServiceApplication.TRADE_SERVICE_NAME + "/ManualSettle/findPage",
                param, ManualSettleDto.class);
        return page;
    }

    public List<ManualSettleFlowDto> queryForFlowPage(Map<String, Object> param) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/ManualSettle/queryForFlowPage", param,
                ManualSettleFlowDto.class);
    }

    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName,"tradeDate") || StringUtil.equals(propName,"settleDate")){
            if(value instanceof Date){
                cell.setCellValue(DateUtil.format((Date)value));
            }else if (value instanceof Long){
                cell.setCellValue(DateUtil.format(new Date((Long) value)));
            }
        }
    }
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/trade/manualsettle/config/export_conf.xml").dataSource(DataSourceType.MASERT);
    }

    @Override
    public Page<ManualSettleDto> selectPageData(Map<String, Object> selectParam) {
        if (selectParam.get("portfolioId") != null) {
            selectParam.put("portfolioId", StringUtil.splitToList(",", (String)selectParam.get("portfolioId")));
        }
        else {
            selectParam.put("portfolioId", null);
        }
        selectParam.put("userName", LoginUserUtil.getUserName());
        Page<ManualSettleDto> page = restTemplate.postForPage(
                ServiceApplication.TRADE_SERVICE_NAME + "/ManualSettle/findPageExport",
                selectParam, ManualSettleDto.class);
        return page;
    }
}

