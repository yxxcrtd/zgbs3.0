/*
 * 文件名：CfetsRepoDealQuotedPrice001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.repo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.controller.FileUploadController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.bankuplink.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.tams.invest.bankuplink.repo.dto.RepoQuoteImportPreviewDto;
import com.joyintech.tams.invest.bankuplink.repo.dto.RepoQuoteInfoDto;
import com.joyintech.tams.invest.bankuplink.repo.service.CfetsRepoDealQuotedPrice001Service;
import com.joyintech.tams.invest.bankuplink.repo.service.CfetsRepoDealQuotedPrice002Service;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;

/**
 * CFETS现券买卖报价
 * 
 * @author zhangliang
 * @version 1.0 2020年5月7日
 * @see CfetsRepoDealQuotedPrice001Controller
 * @since 1.0
 */
@Controller
@PageId("CfetsRepoDealQuotedPrice001")
@RequestMapping("/invest/bankuplink/repo/CfetsRepoDealQuotedPrice001")
public class CfetsRepoDealQuotedPrice001Controller extends BaseController {
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsRepoDealQuotedPrice001Service cfetsRepoDealQuotedPrice001Service;
    @Autowired
    private CfetsRepoDealQuotedPrice002Service cfetsRepoDealQuotedPrice002Service;
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;
    
    @Autowired
    private CfetsRepoDealQuotedPrice002Controller cfetsRepo002Controller;
    
    @Autowired
    private BaseGetBussinessId bussGetBussinessIdService;
    
    @Autowired
	private IPlatformDate iPlatformDate;
    
    @Value("${application.rocketmq.topic.repo:trcms_pfms_tbank_repo}")
    private String repoTopic;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, BussinessDictCode.回购拆借方向, BussinessDictCode.交易方向, BussinessDictCode.清算速度);
        model.addAttribute("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
        model.addAttribute("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        model.addAttribute("unSendStatus", BussinessDictCodeList.银行间上行发送状态_待发送.getValue());
        model.addAttribute("quoteTypeDialog", BussinessDictCodeList.交易类型_对话报价.getValue());
        model.addAttribute("ps_pay_name", BussinessDictCodeList.交易方向_买入.getCodeName());
        model.addAttribute("ps_sell_name", BussinessDictCodeList.交易方向_卖出.getCodeName());
        model.addAttribute("tradeStatusPending", BussinessDictCodeList.银行间上行报价状态_待回复.getValue());
        model.addAttribute("quoteRoleTypeEnquiry", BussinessDictCodeList.银行间上行本方角色_询价方.getValue());
        model.addAttribute("quoteRoleTypeReply", BussinessDictCodeList.银行间上行本方角色_回复方.getValue());
        // 可否无委托对话报价
        model.addAttribute("ibuFlag_startDialogueQuoteWithNoEntrust", CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"));
        model.addAttribute("sendStatus_fail", BussinessDictCodeList.银行间上行发送状态_失败.getCodeName());
        model.addAttribute("sendStatus_unsend", BussinessDictCodeList.银行间上行发送状态_待发送.getCodeName());
        model.addAttribute("sendStatus_send", BussinessDictCodeList.银行间上行发送状态_已发送.getCodeName());
        model.addAttribute("quoteStatus_added", BussinessDictCodeList.银行间上行报价状态_新增中.getCodeName());
        model.addAttribute("loginTraderId", cfetsQuotedCommonService.queryLoginTraderId(RequestUtil.getPageParam()));
        this.setSelectOptionToModel(model, BussinessDictCode.匹配状态,BussinessDictCode.交易类型_CSTP);
        // 定义topic
        model.addAttribute("repoTopic", repoTopic);
        return super.init(model);
    }

    /**
     * 根据查询符合条件的委托列表
     * 
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryEntrustOrderList")
    public List<CfetsRepoDealQuotedPrice001Dto> queryEntrustOrderList() {
        return cfetsRepoDealQuotedPrice001Service.queryEntrustOrderList(
            RequestUtil.getPageParam());
    }

    /**
     * 质押券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/getDayOfferTableList")
    public List<RepoQuoteInfoDto> getDayOfferTableList() {
        return cfetsRepoDealQuotedPrice001Service.getDayOfferTableList(RequestUtil.getPageParam());
    }

    /**
     * 质押券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     *
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/getDayOfferTableListByPage")
    public Page<RepoQuoteInfoDto> getDayOfferTableListByPage() {
        return cfetsRepoDealQuotedPrice001Service.getDayOfferTableListByPage(RequestUtil.getPageParam());
    }

    /**
     * 质押券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/getOrderOfferTableList")
    public List<RepoQuoteInfoDto> getOrderOfferTableList() {
        return cfetsRepoDealQuotedPrice001Service.getOrderOfferTableList(
            RequestUtil.getPageParam());
    }
    /**
     * 查询报价编号列表
     * 
     * @return 报价编号列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryCfetsQuoteIdList")
    public Page<SuggestDto> queryCfetsQuoteIdList() {
        return cfetsRepoDealQuotedPrice001Service.queryCfetsQuoteIdList(
            RequestUtil.getPageParam());
    }
    
    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/getCstpQryTableList")
    public List<CstpSelectDto> getCstpQryTableList() {
        return cfetsRepoDealQuotedPrice001Service.getCstpQryTableList(
            RequestUtil.getPageParam());
    }
    
    /**
     * 刷新
     * 
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("timeOutQuoteRefreshRepo")
    public AjaxResult timeOutQuoteRefreshRepo(@RequestBody BondQuoteInfoDto bondQuoteInfoDto) {
        cfetsRepoDealQuotedPrice001Service.timeOutQuoteRefreshRepo(bondQuoteInfoDto.getQuoteId());
        return new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
    }
    
    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @param parameter 页面参数
     * @see
     * @since 1.0
     */
	@RequestMapping(ACTION_EXPORT)
	public ResponseEntity<byte[]> export() throws Exception {
//		return cfetsRepoDealQuotedPrice001Service.export(this.getPageId(), RequestUtil.getPageParam());
		Map<String, Object> param = RequestUtil.getPageParam();
		if (null != param.get("exportType") && StringUtil.equals(param.get("exportType").toString(), "cstp")) {
			return cfetsRepoDealQuotedPrice001Service.export(this.getPageId(), RequestUtil.getPageParam());
		} else {
			return cfetsRepoDealQuotedPrice002Service.export(this.getPageId() + "_02", RequestUtil.getPageParam());
		}
	}
	
	/**
     * 文件上传公共画面
     * 
     * @param model Model
     * @return 画面路径
     */
    @RequestMapping("framework/fileUpload/init")
    private String uploadInit(Model model) {
        // 画面ID
        String pageId = StringUtil.trim(RequestUtil.getParam(GlobalConstant.PAGE_ID));
        // 上传文件控件的类型
        String type = StringUtil.trim(
            StringUtil.defaultString(RequestUtil.getParam("type"), "drag"));
        // 支持文件类型
        String exts = getExtFile();
        String legend = StringUtil.EMPTY;
        boolean hideTpl = Boolean.valueOf(StringUtil.trim(
            StringUtil.defaultString(RequestUtil.getParam("hideTpl"), Boolean.FALSE.toString())));
        switch (type) {
            case "singleImage":
                legend = "普通图片上传";
                exts = "jpg|jpeg|png|gif|bmp";
                break;
            case "mutiImage":
                legend = "上传多张图片";
                exts = "jpg|jpeg|png|gif|bmp";
                break;
            case "allowFile":
                legend = "只允许上传指定类型的文件";
                break;
            case "allowSize":
                legend = "设定文件大小限制";
                break;
            case "drag":
                legend = "拖拽上传";
                break;
            case "list":
                legend = "多文件列表";
                break;
            default:
                break;
        }
        model.addAttribute("type", type);
        model.addAttribute("hideTpl", hideTpl);
        model.addAttribute("legend", legend);
        model.addAttribute("exts", exts);
        model.addAttribute("currentPageId", pageId);
        model.addAttribute("sendStatus_fail", BussinessDictCodeList.银行间上行发送状态_失败.getCodeName());
        model.addAttribute("sendStatus_unsend", BussinessDictCodeList.银行间上行发送状态_待发送.getCodeName());
        model.addAttribute("sendStatus_sent", BussinessDictCodeList.银行间上行发送状态_已发送.getCodeName());

        return "invest/bankuplink/repo/fileupload";
    }
    
    /**
     * 获取支持文件的类型
     * 
     * @return 支持文件类型
     */
    private String getExtFile() {
        return StringUtil.trim(StringUtil.defaultString(RequestUtil.getParam("exts"), "xls|xlsx"));
    }
    
    /**
     * 模板文件下载
     * 
     * @return 将系统所有模板文件进行打包下载
     * @throws IOException 异常
     */
    @RequestMapping("framework/fileUpload/downloadTemplate")
    public ResponseEntity<byte[]> download() throws IOException{
        // 支持文件类型
        String exts = getExtFile();
        String pageId = RequestUtil.getParam(GlobalConstant.PAGE_ID);
        TemplateName tempName = getTemplateFileNameByPageId(pageId, exts);
        // 如果当前画面的模板存在，则下载对应模板
        if (null != tempName) {
            File tplFile = new File(FileNameUtil.getTplFile(tempName.getFileName()));
            String targetFileName = tempName.getOutFileName() + GlobalConstant.POINT
                                    + FileNameUtil.getFileExtName(tempName.getFileName());
            InputStream in = null;
            if (!tplFile.exists()) {
                in = FileUploadController.class.getResourceAsStream("/tpl/" + tplFile.getName());
            }
            else {
                in = new FileInputStream(tplFile);
            }
            return ExportUtil.export(targetFileName, in);
        }
        
        return null;
    }
    
    /**
     * 根据画面ID获取对应的模板名
     * 
     * @param pageId 画面ID
     * @param exts 模板后缀
     * @return 模板文件和文件名
     * @throws IOException 异常
     */
    private TemplateName getTemplateFileNameByPageId(String pageId, String exts)
        throws IOException {
        if (null != pageId) {
            List<String> extList = StringUtil.splitToList(GlobalConstant.VERTICAL_LINE, exts);
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resource = resolver.getResources("classpath*:page_tpl.properties");
            for (Resource res : resource) {
                Properties properties = PropertiesLoaderUtils.loadProperties(res);
                for (Object key : properties.keySet()) {
                    String keyStr = key.toString();
                    String template = properties.getProperty(keyStr);
                    String[] templateNames = StringUtil.split(StringUtil.trim(template),
                        GlobalConstant.COMMA);
                    for (String tpl : templateNames) {
                        String[] fileNames = StringUtil.split(tpl, GlobalConstant.VERTICAL_LINE);
                        if (StringUtil.equals(keyStr, pageId) && fileNames.length == 2) {
                            if (StringUtil.isEmpty(exts)
                                || extList.contains(FileNameUtil.getFileExtName(fileNames[1]))) {
                                return new TemplateName(pageId, fileNames[0], fileNames[1]);
                            }
                        }
                    }
                }
            }
        }
        return new TemplateName(pageId, SpringContextUtil.getTextValue(pageId),
            pageId + GlobalConstant.FILE_POSTFIX_XLSX);
    }
    
    class TemplateName {

        /**
         * 输出文件名称
         */
        private String outFileName;

        /**
         * 模板名对应的画面ID
         */
        private String pageId;

        /**
         * 模板文件名
         */
        private String fileName;

        /**
         * @param pageId 画面ID
         * @param outFileName 导出文件名
         * @param fileName 模板文件名
         */
        TemplateName(String pageId, String outFileName, String fileName) {
            this.outFileName = outFileName;
            this.pageId = pageId;
            this.fileName = fileName;
        }

        /**
         * 获取 outFileName
         * 
         * @return outFileName.
         */
        public String getOutFileName() {
            return outFileName;
        }

        /**
         * 设置 outFileName
         * 
         * @param outFileName outFileName
         */
        public void setOutFileName(String outFileName) {
            this.outFileName = outFileName;
        }

        /**
         * 获取 pageId
         * 
         * @return pageId.
         */
        public String getPageId() {
            return pageId;
        }

        /**
         * 设置 pageId
         * 
         * @param pageId pageId
         */
        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

        /**
         * 获取 fileName
         * 
         * @return fileName.
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * 设置 fileName
         * 
         * @param fileName fileName
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
	
	/**
     * 报价导入时的check
     *
     * @param fileName String
     * @return List<BondQuoteImportPreviewDto>
     * @throws Exception 
     */
    @ResponseBody
    @RequestMapping("importCheck")
    public List<RepoQuoteImportPreviewDto> importCheck(String fileName) throws Exception {
        List<RepoQuoteImportPreviewDto> checkList = cfetsRepoDealQuotedPrice001Service.importCheck(fileName);
        return checkList;
    }
    
    /**
     * 报价导入
     *
     * @param dataRows List<BondQuoteImportPreviewDto>
     * @return AjaxResult 处理结果
     * @throws Exception 异常
     */
    @SuppressWarnings("null")
	@ResponseBody
    @RequestMapping("importFileCheck")
    public AjaxResult importFileCheck(@RequestBody List<RepoQuoteImportPreviewDto> dataRows,BindingResult result)
        throws Exception {
        List<ValidMessage> messages = new ArrayList<>();
        AjaxResult ajaxResult = new AjaxResult();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < dataRows.size(); i++ ) {
        	if(StringUtil.equals(dataRows.get(i).getQuoteType(), BussinessDictCodeList.交易类型_对话报价.getValue())){
        		CfetsRepoDealQuotedPrice001Dto cfetsBondDealDialogueQuoteDto = new CfetsRepoDealQuotedPrice001Dto();
            	BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto, dataRows.get(i));
//            	cfetsBondDealDialogueQuoteDto.setAvaNum(dataRows.get(i).getEntrustAmt());
//            	cfetsBondDealDialogueQuoteDto.setEntrustAmt(dataRows.get(i).getEntrustAmt().multiply(new BigDecimal(10000)));
            	cfetsBondDealDialogueQuoteDto.setQuotaPageModel("new");
            	cfetsBondDealDialogueQuoteDto.setOperatorType("submit");
				/*
				 * List<ValidMessage> resultList =
				 * cfetsDialogueValidator.valid(cfetsBondDealDialogueQuoteDto); if
				 * (!StringUtil.isEmptyList(resultList)) { StringBuffer message = new
				 * StringBuffer(); message.append(getMessage("CFETS_COMMON_001",
				 * String.valueOf(i + 1))); for (ValidMessage validMessage : resultList) {
				 * message.append(GlobalConstant.HTML_NEWLINE);
				 * message.append(validMessage.getMessage()); } messages.add(new
				 * ValidMessage(message.toString())); }
				 */
            	AjaxResult ajaxResult1 = cfetsRepo002Controller.check(cfetsBondDealDialogueQuoteDto, result);
            	if(StringUtil.isNotEmpty(ajaxResult1.getData())){
            		if(ajaxResult1.getData().toString().contains("<")){
            			sb.append(getMessage("CFETS_COMMON_001", String.valueOf(i + 1)));
                		sb.append(GlobalConstant.HTML_NEWLINE);
                		sb.append(ajaxResult1.getData().toString());
            		}else if(i == dataRows.size()-1){
            			sb.append(ajaxResult1.getData().toString());
            		}
                }
        	}
        }
        if (messages.size() > 0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        if (sb.length() > 0){
            ajaxResult.setData(sb);
        }else{
        	String msg = getMessage("SysCommon.submit");
        	ajaxResult.setData(getMessage("COMMON_C_0001", msg));
        }
        return ajaxResult;
    }

    /**
     * 根据查询符合条件的委托列表
     *
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/selectEtrReByQuoteId")
    public List<PtlAllocationDto> selectEtrReByQuoteId(@RequestBody Map<String, Object> param) {
        return cfetsRepoDealQuotedPrice001Service.selectEtrReByQuoteId(param);
    }

    @ResponseBody
	@RequestMapping("save")
	public AjaxResult save(@RequestBody RepoQuoteImportPreviewDto allDto, BindingResult br,HttpServletRequest requestq) throws Exception {
    	String msg = null;
    	if (br.hasErrors()) {
			return new AjaxResult(br);
		}
    	List<RepoQuoteImportPreviewDto> dataRows = allDto.getRepoQuoteImportPreviewDtoList();
    	List<RepoQuoteImportPreviewDto> dataList = new ArrayList<RepoQuoteImportPreviewDto>();
        //报价导入  首次净价和登记托管机构 未处理
        Map<String,Object> pledgeMap= new HashMap<>();
    	//将质押券添加到list中
    	for(RepoQuoteImportPreviewDto dto:dataRows) {
    		if(StringUtils.isNotEmpty(dto.getEntrustId())) {
    			List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = new ArrayList<CfetsRepoDealQuotedPrice003Dto>();
    			for(RepoQuoteImportPreviewDto pledgeDto:dataRows) {
    				if(dto.getEntrustId().equals(pledgeDto.getEntrustId()) || dto.getEntrustId().equals(pledgeDto.getParentEntrustId())) {
    					CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
    					cfetsRepoDealQuotedPrice003Dto.setBondCode(pledgeDto.getBondCode());
    					cfetsRepoDealQuotedPrice003Dto.setBondAbbrName(pledgeDto.getBondAbbrName());
    					cfetsRepoDealQuotedPrice003Dto.setPledgePrin(pledgeDto.getPledgePrin().multiply(new BigDecimal(10000)));
    					cfetsRepoDealQuotedPrice003Dto.setPledgeRate(pledgeDto.getPledgeRate());
    					cfetsRepoDealQuotedPrice003Dto.setFinprodId(pledgeDto.getFinprodId());
                        //处理托管机构和首次净价
                        if(pledgeMap.containsKey(pledgeDto.getFinprodId())){
                            CfetsRepoDealQuotedPrice003Dto o = (CfetsRepoDealQuotedPrice003Dto)pledgeMap.get(pledgeDto.getFinprodId());
                            cfetsRepoDealQuotedPrice003Dto.setNetPrice(o.getNetPrice());
                            cfetsRepoDealQuotedPrice003Dto.setRepRegistOrg(o.getRepRegistOrg());
                        }else{
                            CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto1 = cfetsRepoDealQuotedPrice001Service.queryPledgeBondElementForImport(pledgeDto.getFinprodId());
                            if(null!=cfetsRepoDealQuotedPrice003Dto1){
                                pledgeMap.put(pledgeDto.getFinprodId(),cfetsRepoDealQuotedPrice003Dto1);
                                cfetsRepoDealQuotedPrice003Dto.setNetPrice(cfetsRepoDealQuotedPrice003Dto1.getNetPrice());
                                cfetsRepoDealQuotedPrice003Dto.setRepRegistOrg(cfetsRepoDealQuotedPrice003Dto1.getRepRegistOrg());
                            }
                        }
    					pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
    				}
    			}
    			if(pledgeBondList.size()>0) {
    				dto.setPledgeBondList(pledgeBondList);
    			}
    			dataList.add(dto);
    		}
    	}
    	
    	//设置流程需要信息
    	List<CfetsRepoDealQuotedPrice001Dto> cfetsRepoDealQuotedPrice001DtoList = new ArrayList<>();
    	for(RepoQuoteImportPreviewDto repoQuoteImportPreviewDto : dataList){
        	if(StringUtil.equals(repoQuoteImportPreviewDto.getQuoteType(), BussinessDictCodeList.交易类型_对话报价.getValue())){
        		CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
            	BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice001Dto, repoQuoteImportPreviewDto);
            	cfetsRepoDealQuotedPrice001Dto.setQuotaPageModel("new");
            	cfetsRepoDealQuotedPrice001Dto.setOperatorType("submit");
            	cfetsRepoDealQuotedPrice001Dto.setPageId("CfetsRepoDealQuotedPrice002");
            	cfetsRepoDealQuotedPrice001Dto.setElementId("CfetsRepoDealQuotedPrice002_btn_submit");
            	cfetsRepoDealQuotedPrice001Dto.setSettleDate(iPlatformDate.getSysDate());
        		if (!StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getQuoteId())) {
        			BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        			codeParameter.setSeqNo("IBU_003");
        			String code = bussGetBussinessIdService != null
        					? bussGetBussinessIdService.buildBussinessCode(codeParameter) : IDGenerator.generate();
        					cfetsRepoDealQuotedPrice001Dto.setQuoteId(code);
        		}
        		cfetsRepoDealQuotedPrice001Dto.setQuoteType(DictCodeListExt3.报价系统区分类型_质押式回购对话报价.getValue());

        		cfetsRepoDealQuotedPrice001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
        		cfetsRepoDealQuotedPrice001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
        		cfetsRepoDealQuotedPrice001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
        		cfetsRepoDealQuotedPrice001Dto.setCreateTime(new Date());// 创建时间
        		cfetsRepoDealQuotedPrice001DtoList.add(cfetsRepoDealQuotedPrice001Dto);
        	}
        }
    	
    	//计算页面联动元素
    	for(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto : cfetsRepoDealQuotedPrice001DtoList) {
    		long actDays;
    		//计算结算日
    		if(BussinessDictCodeList.清算速度_T加0.getValue().equals(cfetsRepoDealQuotedPrice001Dto.getSettleSpreed())) {
    			Date date = iPlatformDate.getSysDate();
    			Map<String, Object> params = new HashMap<String, Object>();
    			Date tradeDate = DateUtil.addDay(date,Integer.parseInt(cfetsRepoDealQuotedPrice001Dto.getTerm()+""));
    			params.put("tradeDate", DateUtil.format(tradeDate));
    			String mDate = cfetsQuotedCommonService.getWorkDay(params);
    			cfetsRepoDealQuotedPrice001Dto.setvSettleDate(date);
    			cfetsRepoDealQuotedPrice001Dto.setmSettleDate(DateUtil.parse(mDate));
    			actDays = DateUtil.getBetweenDayNumber(date,DateUtil.parse(mDate));
    		}else {
    			Date date = iPlatformDate.getSysDate();
    			Map<String, Object> params = new HashMap<String, Object>();
    			Date tradeDate = DateUtil.addDay(date,1);
    			params.put("tradeDate", DateUtil.format(tradeDate));
    			String vDate = cfetsQuotedCommonService.getWorkDay(params);
    			cfetsRepoDealQuotedPrice001Dto.setvSettleDate(DateUtil.parse(vDate));
    			tradeDate = DateUtil.addDay(tradeDate,Integer.parseInt(cfetsRepoDealQuotedPrice001Dto.getTerm()+""));
    			params.put("tradeDate", DateUtil.format(tradeDate));
    			String mDate = cfetsQuotedCommonService.getWorkDay(params);
    			cfetsRepoDealQuotedPrice001Dto.setmSettleDate(DateUtil.parse(mDate));
    			actDays = DateUtil.getBetweenDayNumber(DateUtil.parse(vDate),DateUtil.parse(mDate));
    		}
    		//计算券面总额合计、质押券折算总金额
    		List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = cfetsRepoDealQuotedPrice001Dto.getPledgeBondList();
    		BigDecimal pledgePrinTotal = new BigDecimal(0);
    		BigDecimal shareCtAmt = new BigDecimal(0);
    		for(CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : pledgeBondList) {
    			BigDecimal pledgePrin = cfetsRepoDealQuotedPrice003Dto.getPledgePrin();
    			BigDecimal pledgeRate = cfetsRepoDealQuotedPrice003Dto.getPledgeRate();
    			pledgePrinTotal = pledgePrinTotal.add(pledgePrin);
    			shareCtAmt = shareCtAmt.add(pledgePrin.multiply(pledgeRate).divide(new BigDecimal(100)));
    		}
    		cfetsRepoDealQuotedPrice001Dto.setPledgePrinTotal(pledgePrinTotal.divide(new BigDecimal(10000)));
    		cfetsRepoDealQuotedPrice001Dto.setShareCtAmt(shareCtAmt);
    		//实际占款天数、设置回购利息、到期结算金额
    		cfetsRepoDealQuotedPrice001Dto.setActDays(actDays);
    		if(cfetsRepoDealQuotedPrice001Dto.getDealAmt()==null) {
    			cfetsRepoDealQuotedPrice001Dto.setDealAmt(shareCtAmt);
    		}
    		BigDecimal mIntAmt = cfetsRepoDealQuotedPrice001Dto.getDealAmt().multiply(cfetsRepoDealQuotedPrice001Dto.getPrice()).multiply(new BigDecimal(actDays)).divide(new BigDecimal(36500),2, RoundingMode.HALF_UP);
    		cfetsRepoDealQuotedPrice001Dto.setmIntAmt(mIntAmt);
    		cfetsRepoDealQuotedPrice001Dto.setmTradeAmt(mIntAmt.add(cfetsRepoDealQuotedPrice001Dto.getDealAmt()));
    	}
    	
    	if(!StringUtil.isEmptyList(cfetsRepoDealQuotedPrice001DtoList)){
    		ProcessParameter<CfetsRepoDealQuotedPrice001Dto> processParameter = new ProcessParameter<CfetsRepoDealQuotedPrice001Dto>();
    		WorkFlowParameter<CfetsRepoDealQuotedPrice001Dto> params =  new WorkFlowParameter<CfetsRepoDealQuotedPrice001Dto>();
    		params.setPageId(cfetsRepoDealQuotedPrice001DtoList.get(0).getPageId());
    		params.setElementId(cfetsRepoDealQuotedPrice001DtoList.get(0).getElementId());
    		params.setBusinessType(DictCodeListExt3.共通业务类型_银行间上行.getValue());
    		params.setBusinessCode(CfetsRepoDealQuotedPrice001Dto::getQuoteId);
    		params.setComment(entity -> getMessage("对话报价编号") + "(" + entity.getQuoteId() + ")-"
    				+ "业务类型" + "(" + DictCodeListExt3.报价系统区分类型_质押式回购对话报价.getCodeName() + ")-" + "平台日" + "("
    				+ DateUtil.format(iPlatformDate.getSysDate()) + ")");
    		processParameter.setWorkFlowParameter(params);
//    		boolean flag = true;
//    		if(flag) {
//    			return null;
//    		}
    		try {
        		msg = cfetsRepoDealQuotedPrice002Service.start(processParameter, cfetsRepoDealQuotedPrice001DtoList.toArray(new CfetsRepoDealQuotedPrice001Dto[cfetsRepoDealQuotedPrice001DtoList.size()]));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
    	}
		return new AjaxResult(msg);
	}
}
