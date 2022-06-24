package com.joyintech.tams.svreport.chb.prodpersonissue.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.component.svrreport.chb.contant.SendDataCheckType;
import com.joyintech.component.svrreport.chb.dto.ChbPrdIssueSubDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.svreport.chbsd.prdissuesub.service.SdProdIssueBatchService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.svreport.chb.prodpersonissue.dto.ProdInfo;
import com.joyintech.tams.svreport.chb.prodpersonissue.dto.ProdPersonIssue001Dto;
import com.joyintech.tams.svreport.chb.prodpersonissue.service.ProdPersonIssue001Service;
import com.joyintech.tams.svreport.chb.prodpersonissue.service.ProdPersonIssueMService;


/**
 * 个人发行登记
 *
 * @author Administrator
 * @version 1.0 2019年1月22日
 * @see ProdPersonIssue001Controller
 * @since 1.0
 */
@Controller
@PageId("SvrProdPersonIssue001")
@RequestMapping("/chb/submitmanagement/prodpersonissue/ProdPersonIssue001")
public class ProdPersonIssue001Controller extends BaseController {
	/**
     * 001Service
     */
    @Autowired
    private ProdPersonIssue001Service prodPersonIssue001Service;
    @Autowired
    private ProdPersonIssueMService prodPersonIssueMService;
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SdProdIssueBatchService sdProdIssueBatchService;
	  /**
     * 初始化
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
    	Map<String, Object> map = RequestUtil.getPageParam();
		if (StringUtil.isNotEmpty(map.get("submitStatus"))) {
			model.addAttribute("submitStatus", map.get("submitStatus").toString().trim());
			if (StringUtil.isNotEmpty(map.get("status"))) {
				model.addAttribute("status", map.get("status").toString().trim());
			}
			if(StringUtil.isNotEmpty(map.get("submitFeedbackStatus"))){
            	model.addAttribute("submitFeedbackStatus", map.get("submitFeedbackStatus").toString().trim());
            }
			model.addAttribute("searchDataStart", map.get("searchDataStart"));
			model.addAttribute("searchDataEnd", map.get("searchDataEnd"));
			model.addAttribute("isMain", "01");
		}
		model.addAttribute("SVR_REPORT_CODE", DictCodeListExt2.报表名称_产品发行登记.getValue());
		model.addAttribute("FILE_HANDLE_STATUS", DictCodeListExt3.中债一二三期文件处理状态_未处理.getValue());
    	  super.setSelectOptionToModel(model, 
          		DictCode.报送状态,
          		DictCode.报送反馈状态,
          		DictCode.报送数据状态);
        return super.init(model);
    }
    /**
    *
    * 查询功能
    * <功能详细描述>
    * @return 存储了查询结果的list
    * @see
    * @since 1.0
    */
   @ResponseBody
   @RequestMapping(SVReportGlobalConstant.SEARCH_PAGE)
   public Page<ProdPersonIssue001Dto> search(){
       Map<String, Object> param = RequestUtil.getPageParam();
       return prodPersonIssueMService.searchForPage(param);
   }
   /**
   *
   * 导出功能
   * <功能详细描述>
   * @return 结果
   * @see
   * @since 1.0
   */
  @RequestMapping(value = ACTION_EXPORT)
  public ResponseEntity<byte[]> export() {
	  Map<String, Object> pageParam = RequestUtil.getPageParam();
	  if(pageParam.containsKey("keyList")){
	 		List<ProdPersonIssue001Dto>  dataList=prodPersonIssueMService.searchForExport(pageParam);
	 		  File outFile = prodPersonIssue001Service.makeFile(
	 	             "/com/joyintech/tams/svreport/chb/prodpersonissue/conf/export_conf.xml", dataList);
	 	         return ExportUtil.export(
	 	             this.getMessage("产品发行登记") + GlobalConstant.OPEN_PARENTHESIS
	 	                                  + DateUtil.format(DateUtil.parse(DateUtil.getSysDate())) + GlobalConstant.CLOSE_PARENTHESIS
	 	                                  + GlobalConstant.POINT + FileNameUtil.getFileExtName(outFile),
	 	             outFile, true);
	 	         
	 	}else{
	 		  pageParam.put("export_method_flg", "export");
	 		  return prodPersonIssue001Service.export(this.getPageId(), pageParam);
	 	}
//      if (pageParam.containsKey("expKeys")){
//      	pageParam.put("export_method_flg", "exportSelect");
//      } else {
//      	pageParam.put("export_method_flg", "export");
//      }
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
	@ResponseBody
	@RequestMapping(value = "/doManual",method = RequestMethod.POST)
	public AjaxResult doUpdate(@RequestBody Map<String, Object> map) {
		prodPersonIssueMService.updateSumStaus(map);
		return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
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
	@ResponseBody
	@RequestMapping(value = "/cancelMannul", method = RequestMethod.POST)
	public AjaxResult cancelMannul(@RequestBody Map<String, Object> map) {
		String result = prodPersonIssueMService.cancelMannul(map);
		if("fail".equals(result)){
			AjaxResult ajaxresult =  new AjaxResult("有未报送的数据",MessageType.错误消息);
			ajaxresult.setStatuCode(HttpStatus.OK.value());
			return ajaxresult;
		}
		return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
	}
	
	@ResponseBody
    @RequestMapping("reportCheck")
    public Map<String,Object> reportCheck() {
        return prodPersonIssueMService.reportCheck(RequestUtil.getPageParam());
    }

	/**start  中债一二期直连代码  add  by  wangzehui  20201010*/

	/**
	 * 中债直连报送
	 *
	 * @param map
	 *            传入需要报送的主键
	 * @return 反馈信息
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping(value = "/submitChb", method = RequestMethod.POST)
	public AjaxResult doSubmit(@RequestBody Map<String, Object> param) {

		List<ProdPersonIssue001Dto> list=new ArrayList<ProdPersonIssue001Dto>();
		// 选择行数据时
		if (param.containsKey("keyList")) {
			String keyList = param.get("keyList").toString();
			try {
				list = JsonUtils.json2List(keyList,
						ProdPersonIssue001Dto.class);
			}
			catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}else{
			Page<ProdPersonIssue001Dto> page = prodPersonIssueMService.searchForPage(RequestUtil.getPageParam());
			if(page!=null&&page.getRoot()!=null&&page.getRoot().size()>0){
				list = page.getRoot();
			}
			//查询数据
		}

		//报文拼接：2、进行数据结构转化，把[1]中取得的List，转化为报文拼接组件中调用方法的数据结构类
		List<ChbPrdIssueSubDto> dataList = new ArrayList<ChbPrdIssueSubDto>();
		for(ProdPersonIssue001Dto sdDto:list){
			ChbPrdIssueSubDto target = new ChbPrdIssueSubDto();
			BeanUtil.copyPropertiesIgnoreNull(target, sdDto);
			dataList.add(target);
		}

		SendDataCheckType sendDataCheckType = SendDataCheckType.报送;
		if (StringUtil.equals(param.get("opertionType"), "reSubmitChb")){
			sendDataCheckType = SendDataCheckType.重新报送;
		}
		sdProdIssueBatchService.sendBatchCommon(dataList, sendDataCheckType);

		return new AjaxResult(this.getMessage("REPORT_COMMON_I_0018"));
	}

	/**end  中债一二期直连代码  add  by  wangzehui  2020100910*/
}
