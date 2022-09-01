package com.joyintech.tams.invest.basicdata.mstcommissionset.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.tams.common.base.entity.TrdFeeConfigDetail;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.CreditLines;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.basicdata.mstcommissionset.dto.MstCommissionSetDto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 交易所交易费用设置
 * @author 裴家俊
 * @version 1.0 2019年3月22日
 * @see MstCommissionSetService
 * @since 1.0
 */
@Service
public class MstCommissionSetService extends CommonExportService<MstCommissionSetDto> implements BaseProcessService<MstCommissionSetDto>{

    //add by hudongyang 2020-08-03
    @Autowired
    private MstCommissionSetService mstCommissionSetService;
	
	@Override
	public void doComplete(MstCommissionSetDto entity, OperationType type) {
		 String action = StringUtil.EMPTY;
		 if (OperationType.DELETE.equals(type)) {
	            action = "delete";
	        }
	        else if (OperationType.NEW.equals(type)) {
	            action = "insert";
	        }
	        else {
	            action = "update";
	        }
	        restTemplate.postForObject(
	            ServiceApplication.BASICDATA_SERVICE_NAME + "/MstCommissionSet/" + action, entity,
	            Integer.class);
	}
	
    /**
     * 查询界面询事件
     * @param map 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<MstCommissionSetDto> selectForPage(Map<String, Object> map){
        //add by hudongyang 2020-08-04 start
        if (StringUtil.isNotEmpty(map.get("tradeType"))) {
            map.put("tradeType", StringUtil.splitToList(null, map.get("tradeType").toString()));
        }else{
            map.remove("tradeType");
        }
        //add by hudongyang 2020-08-04 end

        Page<MstCommissionSetDto> page = restTemplate.postForPage(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstCommissionSet/findPage",
                map, MstCommissionSetDto.class);
        List<MstCommissionSetDto> dtoList = page.getRoot();
        for (MstCommissionSetDto dto : dtoList){
            if (StringUtil.equals(dto.getChargeType(), BussinessDictCodeList.计费类型_固定费率.getValue())){
                dto.setFeeRate(ArithmeticUtil.round(dto.getChargeValue(),8, RoundingMode.HALF_UP).toString()+"‰");
            }else {
                dto.setFeeRate(ArithmeticUtil.round(dto.getChargeValue(),2,RoundingMode.HALF_UP).toString()+"元");
            }
        }
        return page;
    }

    /**
     * 根据主键查询交易所交易费用设置
     * @param batchSeq 流水号
     * @return 佣金信息
     */
    public MstCommissionSetDto getBussinessEntityBySeqNo(String batchSeq) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("batchSeq", batchSeq);
        return restTemplate.postForObject(
            ServiceApplication.BASICDATA_SERVICE_NAME + "/MstCommissionSet/getBussinessEntityBySeqNo",
            map, MstCommissionSetDto.class);
    }
    
	 /**
     * 证券账户联想文本框service层实现
      * @param map 检索参数
      * @return SuggestDto
      */
    public List<SelectOptionDto> getSecAcctName(Map<String, Object> map) {
        return restTemplate.postForList(
            ServiceApplication.BASICDATA_SERVICE_NAME + "/MstCommissionSet/getSecAcctName",
            map, SelectOptionDto.class);
    }
    
    /**
     * 新建场合时查询同一证券同一交易方向同一交易品种是否重复
     * @param
     */
    public int searchRepeatBySecPs(Map<String, Object> map){
    	 return restTemplate.postForObject(
    	            ServiceApplication.BASICDATA_SERVICE_NAME + "/MstCommissionSet/searchRepeatBySecPs",map,
    	             Integer.class);
    }
    
    /**
     * 删除事件
     * @param dto 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public int delete(MstCommissionSetDto dto){
        return restTemplate.postForObject(
                ServiceApplication.RISK_SERVICE_NAME + "/MstCommissionSet/delete",
                dto, Integer.class);
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/invest/basicdata/mstcommissionset/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }
    /**
     * 设置日期格式
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(("effDate".equals(propName)) && value != null){
            //add by hudongyang 2020-08-04 导出日期报错 start
//            cell.setCellValue(DateUtil.format((Date)value));

            if(value instanceof Date){
                cell.setCellValue(DateUtil.format((Date)value));
            }else if (value instanceof Long){
                cell.setCellValue(DateUtil.format(new Date((Long) value)));
            }
            //add by hudongyang 2020-08-04 导出日期报错 end
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<MstCommissionSetDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 修改或查看界面
     *
     * @param paraCode 参数代码
     * @return 结果
     * @see
     * @since 1.0
     */
    public List<MstCommissionSetDto> selectInfo(String paraCode) {
        return restTemplate.getForList(
                ServiceApplication.RISK_SERVICE_NAME + "/RskSupervisionIndicatorParam/selectInfo?paraCode="+
                paraCode, MstCommissionSetDto.class);
    }

    /**
     * 查询参数代码
     *
     * @param paraCode 参数代码
     * @return 结果
     * @see
     * @since 1.0
     */
    public int selectParaCode(String paraCode) {
        return restTemplate.getForObject(
                ServiceApplication.RISK_SERVICE_NAME + "/RskSupervisionIndicatorParam/selectParaCode?paraCode="+paraCode,
                Integer.class);
    }

    /**
     * 是否在监督指标里配置
     *
     * @param paraCode 参数代码
     * @return 结果
     * @see
     * @since 1.0
     */
    public int isConfigure(String paraCode) {
        return restTemplate.getForObject(
                ServiceApplication.RISK_SERVICE_NAME + "/RskSupervisionIndicatorParam/isConfigure?paraCode="+paraCode,
                Integer.class);
    }

    /**
     * add by hudongyang
     * @param pageParam
     * @return
     */
    public Page<TrdFeeConfigDetail> select(Map<String, Object> pageParam) {
        String workflowId=(String)pageParam.get("workflowId");
        Page<TrdFeeConfigDetail> page = new Page<>();
        if(StringUtil.isNotEmpty(workflowId)){
            MstCommissionSetDto dto = this.getBussinessEntityByWorkflowId(
                    workflowId);
            List<TrdFeeConfigDetail> list = new ArrayList<TrdFeeConfigDetail>();
            if(dto.getTrdFeeConfigDetailList() !=  null){
                list=dto.getTrdFeeConfigDetailList();
            }
            page.setRoot(list);
            page.setRecords(list.size());
        }
        else{
            if(pageParam.get("batchSeq") != null && !"".equals((String)pageParam.get("batchSeq"))){
                MstCommissionSetDto entity = mstCommissionSetService.getBussinessEntityBySeqNo((String)pageParam.get("batchSeq"));
                List<TrdFeeConfigDetail> list = new ArrayList<TrdFeeConfigDetail>();
                if(entity != null){
                    if(entity.getTrdFeeConfigDetailList() != null){
                        list=entity.getTrdFeeConfigDetailList();
                    }
                    page.setRoot(list);
                    page.setRecords(list.size());
                }
            }
        }
        return page;
    }

    /**
     * add by hudongyang
     * @param param
     * @return
     */
    public List<SelectOptionDto> getMstCustomerInfo(Map<String, Object> param) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstCommissionSet/getMstCustomerInfo",
                param, SelectOptionDto.class);
    }


    /**
     * add by hudongyang
     * @param param
     * @return
     */
    public Page<SelectOptionDto> getMstAcctInfo(Map<String, Object> param) {
        return restTemplate.postForPage(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstCaptialAccount/getAcctInfo",
                param, SelectOptionDto.class);
    }
}
