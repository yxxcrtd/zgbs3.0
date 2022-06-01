package com.joyintech.tams.invest.basicdata.mstmarketmanagement.service;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.select.SelectOptionDto;
import org.springframework.stereotype.Service;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.tams.invest.basicdata.mstmarketmanagement.dto.MstMarketManagementDto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
@Service
public class MstMarketManagement001Service extends CommonExportService<MstMarketManagementDto> 
implements BaseProcessService<MstMarketManagementDto>{
	 /**
     * 分页查询
     * 
     * @param pageParam 传入的查询条件
     * @return Page<MstInterestRateQuotationInfoDto> page对象
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
	  public Page<MstMarketManagementDto> selectForPage(Map<String, Object> pageParam){
/*	        pageParam.put("customerType", DictCodeList.客户类型_法人.getValue());
*/	        return restTemplate.postForPage(
	            ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketManagement/selectForPage",
	            pageParam, MstMarketManagementDto.class);
	    }

	@Override
	public void doComplete(MstMarketManagementDto entity, OperationType type) {
		restTemplate.postForObject(
		ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketManagement/doComplete",entity, Integer.class);
	}

	 /**
     * 
     * 客户名称联想文本框service层实现
     * <功能详细描述>
     * @param map 联想文本框查询信息
     * @return 查询结果
     * @see
     * @since 1.0
     */

	public List<SuggestDto> selectFinprodName(Map<String, Object> param) {
		 return restTemplate.postForList(
		            ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketManagement/selectFinprodName",
		            param, SuggestDto.class);
	}

	 

	/*public Page<SelectOptionDto> getGradeResult(Map<String, Object> param) {
	        return restTemplate.postForPage(
	        		 ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketManagement/getGradeResult",
	                 param,SelectOptionDto.class);
		}
	 */
	/**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/invest/basicdata/mstmarketmanagement/conf/export_conf.xml")
        .dataSource(DataSourceType.MASERT);
    }
    
    @Override
    public Page<MstMarketManagementDto> selectPageData(Map<String, Object> selectParam) {
    	return selectForPage(selectParam);
    }

    public Page<SelectOptionDto> getCodeName(Map<String, Object> param) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketManagement/getCodeName",
				param, SelectOptionDto.class);
    }

    public MstMarketManagementDto selectDetail(MstMarketManagementDto entity) {
		return restTemplate.postForObject(
		ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketManagement/selectDetail",
				entity, MstMarketManagementDto.class);
    }
}
