package com.joyintech.service.chb3.submitmanagement.investorsholdinforegist.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.systemtools.service.ServiceInstitutionService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.ChbBatchParaMapper;
import com.joyintech.tams.common.base.entity.ChbBatchPara;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.report.submitmanagement.investorsholdinforegist.dto.InvestorsHoldInfoRegist001Dto;
import com.joyintech.tams.report.submitmanagement.investorsholdinforegist.service.InvestorsHoldInfoRegist001Service;
import com.joyintech.tams.systemtools.service.ServiceInstitutionService;

/**
 * 投资者持有信息登记服务
 *
 * @author zhangzhongliang
 * @version 1.0 2019年4月19日
 * @see InvestorsHoldInfoRegistController
 * @since 1.0
 */
@RestController
@RequestMapping("/InvestorsHoldInfoRegist")
public class InvestorsHoldInfoRegistController{

	/**
	 * 投资者持有数量信息登记
	 *
	 */
	@Autowired
	private InvestorsHoldInfoRegist001Service investorsHoldInfoRegist001Service;
	
	/**
	 * 机构管理service
	 */
	@Autowired(required = false)
	private ServiceInstitutionService institutionService;

    @Autowired
    private ChbBatchParaMapper chbBatchParaMapper;
    
	/**
	 *
	 * 机构代码下拉框取得值
	 *
	 * @return 下拉框值
	 * @see
	 * @since 1.0
	 */
	@PostMapping("/getInstitutionCode")
	public List<SuggestDto> getInstitutionCode(@RequestBody(required=false) String param) {
		return institutionService.getInstitutionCode(param);
	}
	
	/**
	 *
	 * 业务画面查询事件
	 *
	 * @return
	 * @see
	 * @since 1.0
	 */
	@PostMapping("findPage/{page}/{length}")
	public Page<InvestorsHoldInfoRegist001Dto> selectAllData(@RequestBody Map<String, Object> param) {
		Page<InvestorsHoldInfoRegist001Dto> page = investorsHoldInfoRegist001Service
				.selectForPage(param);
		return page;
	}

	@PostMapping("queryForHoldInfoRegist")
	public List<InvestorsHoldInfoRegist001Dto> queryForHoldInfoRegist(@RequestBody Map<String, Object> param) {
		return investorsHoldInfoRegist001Service.queryForHoldInfoRegist(param);
	}

	/**
	 *
	 * 导入
	 *
	 * @param dataList
	 *            文件
	 * @return 导入数据
	 * @see
	 * @since 1.0
	 */
	@PostMapping("importData")
	@SuppressWarnings("unchecked")
	public List<ExcelValidDto> importData(@RequestBody Map<String, Object> param) {
		List<List<String>> dataList = (List<List<String>>) param.get("dataList");
		String orgCode =  null!=param.get("orgCode")?param.get("orgCode").toString():"";
		return this.investorsHoldInfoRegist001Service.importData(dataList,orgCode);
	}

	/**
	 *
	 * 业务画面导出查询
	 *
	 * @return
	 * @see
	 * @since 1.0
	 */
	@PostMapping("selectAllList")
	public List<InvestorsHoldInfoRegist001Dto> selectAllList(@RequestBody Map<String, Object> pageParam) {;
		return investorsHoldInfoRegist001Service.selectAllList(pageParam);
	}

	/**
	 * 报送检查
	 *
	 * @return 检查结果
	 * @see
	 * @since 1.0
	 */
	@PostMapping("sendBeforeCheck")
	public List<ExcelValidDto> sendBeforeCheck(@RequestBody Map<String, Object> pageParam) {
		return investorsHoldInfoRegist001Service.sendBeforeCheck(pageParam,false);
	}

	/**
	 * 中债直连报送
	 *
	 * @param map
	 *            传入需要报送的主键
	 * @return 反馈信息
	 * @throws Exception 
	 * @see
	 * @since 1.0
	 */
	@PostMapping("submitChb")
	public void doSubmit(@RequestBody Map<String, Object> map) {
		Map<String, List<InvestorsHoldInfoRegist001Dto>> registMap = investorsHoldInfoRegist001Service
				.getSubmitList(map);
		Iterator<Map.Entry<String, List<InvestorsHoldInfoRegist001Dto>>> entries = registMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, List<InvestorsHoldInfoRegist001Dto>> entry = entries.next();
			List<InvestorsHoldInfoRegist001Dto> registList = entry.getValue();
			investorsHoldInfoRegist001Service.sendInvestorsHoldCHB(registList, false);
		}
	}

	/**
	 * 手工处理功能
	 *
	 * @return 提示信息
	 * @param map
	 *            条件map
	 * @seea
	 * @since 1.0
	 */
	@PostMapping("doUpdate")
	public void doUpdate(@RequestBody Map<String, Object> map) {
		//add by liubin
		if(map.get("flag") != null && "01".equals(map.get("flag"))){
			//处理全部数据
			investorsHoldInfoRegist001Service.updateSumStausAll(map);
		}else{
			investorsHoldInfoRegist001Service.updateSumStaus(map);
		}	
	}

	/**
	 * 取消手工处理功能
	 *
	 * @return 提示信息
	 * @param map
	 *            条件map
	 * @seea
	 * @since 1.0
	 */
	@PostMapping("cancelMannulAll")
	public void cancelMannulAll(@RequestBody Map<String, Object> map) {
			investorsHoldInfoRegist001Service.cancelMannulAll(map);
	}
	/**
	 * 取消手工处理功能
	 *
	 * @return 提示信息
	 * @param map
	 *            条件map
	 * @seea
	 * @since 1.0
	 */
	@PostMapping("cancelMannul")
	public void cancelMannul(@RequestBody Map<String, Object> map) {
			investorsHoldInfoRegist001Service.cancelMannul(map);
	}
	/**
	 * checkIsNotSubmit
	 *
	 * @return 提示信息
	 * @param map
	 *            条件map
	 * @seea
	 * @since 1.0
	 */
	@PostMapping("checkIsNotSubmit")
	public List<ValidMessage> checkIsNotSubmit(@RequestBody Map<String, Object> map) {
			return investorsHoldInfoRegist001Service.checkIsNotSubmit(map);
	}
	/**
	 * 重新报送
	 *
	 * @param map
	 *            传入需要报送的主键
	 * @return 反馈信息
	 * @see
	 * @since 1.0
	 */
	@PostMapping("submitCbAgain")
	public void doSubmitAgain(@RequestBody Map<String, Object> map) {
		Map<String, List<InvestorsHoldInfoRegist001Dto>> registMap = investorsHoldInfoRegist001Service
				.getSubmitList(map);
		Iterator<Map.Entry<String, List<InvestorsHoldInfoRegist001Dto>>> entries = registMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, List<InvestorsHoldInfoRegist001Dto>> entry = entries.next();
			List<InvestorsHoldInfoRegist001Dto> registList = entry.getValue();
			investorsHoldInfoRegist001Service.sendInvestorsHoldCHB(registList, false);
		}
	}
	
	@PostMapping("selectAllData")
	public List<InvestorsHoldInfoRegist001Dto> selectAllDataExport(@RequestBody Map<String,Object> map){
		return investorsHoldInfoRegist001Service.selectAllData(map);
	}
	
    @PostMapping("checkIfInBatchByTaskBeanName")
    public boolean checkIfInBatchByTaskBeanName(@RequestBody String beanname) {
    	return investorsHoldInfoRegist001Service.checkIfInBatchByTaskBeanName(beanname);
    }
    
    @PostMapping("insert")
    public void insert(@RequestBody ChbBatchPara record){
    	chbBatchParaMapper.insert(record);
    }
}
