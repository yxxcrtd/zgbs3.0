package com.joyintech.tams.invest.basicdata.standardcouponreconversionrate.service;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.invest.common.dto.InvAjaxResult;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.basicdata.standardcouponreconversionrate.dto.StandardCouponReConversionRateDto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

@Service
public class StandardCouponReConversionRateService extends CommonExportService<StandardCouponReConversionRateDto>
        implements BaseProcessService<StandardCouponReConversionRateDto>{


    /**
     * 使用给定的acctName检索分页数据。返回类型是Service的泛型。
     *
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<StandardCouponReConversionRateDto> selectForPage(Map<String, Object> pageParam){
//        if(StringUtil.isNotEmpty(pageParam.get("brokerId"))){
//            List<String> brokerIdList = StringUtil.splitToList(",",(String)pageParam.get("brokerId"));
//            pageParam.put("brokerIdList", brokerIdList);
//        }
//
//        if(StringUtil.isNotEmpty(pageParam.get("accountType"))){
//            List<String> accountTypeList = StringUtil.splitToList(",",(String)pageParam.get("accountType"));
//            pageParam.put("accountTypeList", accountTypeList);
//        }
        return restTemplate.postForPage(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/StandardCouponReConversionRate/selectForPage",
                pageParam, StandardCouponReConversionRateDto.class);
    }



    /**
     * 券商 <功能详细描述>
     * @return 结果
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getMstCustomerInfo(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/StandardCouponReConversionRate/getMstCustomerInfo", map,
                SelectOptionDto.class);
    }

    /**
     * 双击查看详细信息查询 修改页面信息查询
     *
     * @param
     *
     * @return MstSaleDepartmentDto
     * @see
     * @since 1.0
     */
    public StandardCouponReConversionRateDto selectByPrimaryKey(StandardCouponReConversionRateDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/StandardCouponReConversionRate/selectByPrimaryKey", dto,
                StandardCouponReConversionRateDto.class);
    }


    /**
     * 双击查看详细信息查询 修改页面信息查询
     *
     * @param
     *
     * @return MstSaleDepartmentDto
     * @see
     * @since 1.0
     */
    public List<StandardCouponReConversionRateDto> getList(StandardCouponReConversionRateDto dto) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/StandardCouponReConversionRate/getList", dto,
                StandardCouponReConversionRateDto.class);
    }


    /**
     * 双击查看详细信息查询 修改页面信息查询
     *
     * @param
     *
     * @return MstSaleDepartmentDto
     * @see
     * @since 1.0
     */
    public List<WflValidInputParameter> getBusinessData (WflValidInputParameter dto) {
        List<WflValidInputParameter> list = restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/StandardCouponReConversionRate/getBusinessData", dto,
                WflValidInputParameter.class);
        return list;
    }


    /**
     * 导出查询
     */
    @Override
    public Page<StandardCouponReConversionRateDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 文件导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/basicdata/standardcouponreconversionrate/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }

    @Override
    public void doComplete(StandardCouponReConversionRateDto entity,
                           OperationType type) {
        entity.setOperateType(type.toString());
        String action = StringUtil.EMPTY;
        if (entity.getOperateType().equals("DELETE")) {
            action = "delete";
        } else if (entity.getOperateType().equals("NEW")) {
            //生成序列号
            entity.setSeqNo(IDGenerator.generate(32));
            action = "insert";
        } else {
            action = "update";
        }
        restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/StandardCouponReConversionRate/" + action, entity, Integer.class);

    }


    /**
     * 查询组合资金账户
     * @return
     */
    public List<SelectOptionDto> getPtlSecAcctInfo(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME
                        + "/StandardCouponReConversionRate/getPtlSecAcctInfo", map,
                SelectOptionDto.class);
    }

    //add by chenjiaxing 标准券再折算率改造 20211019 start
    /**
     * 查询券商资产
     * @return
     */
    public List<SelectOptionDto> getFinprodIdInfo(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME
                        + "/StandardCouponReConversionRate/getFinprodIdInfo", map,
                SelectOptionDto.class);
    }
    //add by chenjiaxing 标准券再折算率改造 20211019 end

    public List<SelectOptionDto> brokerIdChange(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.BASICDATA_SERVICE_NAME
                        + "/StandardCouponReConversionRate/brokerIdChange", map,
                SelectOptionDto.class);
    }

    /**
     * 导入数据文件之前检查数据
     *
     * @param fileName 导入的文件名
     * @return 处理结果
     */
    public AjaxResult checkConfirmMsgForImportData(String fileName)  {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("dataList", ExcelUtil.getDataList1(fileName));
        param.put("orgCode", CommonUtil.getOrgCode());
        param.put("deptCode", CommonUtil.getDeptCode());
        return restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/StandardCouponReConversionRate/checkConfirmMsgForImportData",
                param,
                InvAjaxResult.class);
    }

    /**
     * 导入数据文件
     *
     * @param fileName 导入的文件名
     * @return 导入结果，包含验证内容，如果验证失败，返回message
     */
    public List<ExcelValidDto> importData(String fileName) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("dataList", ExcelUtil.getDataList1(fileName));
        param.put("tempList", ExcelUtil.getTempList1(fileName));
        param.put("userName", CommonUtil.getUserName());
        param.put("orgCode", CommonUtil.getOrgCode());
        param.put("deptCode", CommonUtil.getDeptCode());
//        restTemplate.postForList(
//                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstBondQuotationInfo/importData",
//                param,
//                ExcelValidDto.class);
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/StandardCouponReConversionRate/importData",
                param,
                ExcelValidDto.class);
    }
}
