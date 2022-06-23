/*
 * 文件名：CfetsBondDealQuotedPrice001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.repo.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.ExcelUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteImportPreviewDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.bankuplink.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.tams.invest.bankuplink.repo.dto.RepoQuoteImportPreviewDto;
import com.joyintech.tams.invest.bankuplink.repo.dto.RepoQuoteInfoDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;

/**
 * CFETS现券买卖报价
 * 
 * @author zhangliang
 * @version 1.0 2020年5月12日
 * @see CfetsRepoDealQuotedPrice001Service
 * @since 1.0
 */
@Service
public class CfetsRepoDealQuotedPrice001Service extends CommonExportService<CstpSelectDto> {
	
	private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;
    
    // 是否需要重复校验（重复校验未经过测试）
    @Value("${application.banklink.duplicatecheck:false}")
    private boolean _needDuplicateCheck;
    
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;
    
    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;

    @Autowired(required = false)
    private CommonBusinessService commonBusinessService;

    /**
     * 根据查询条件查询委托列表
     * 
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public List<CfetsRepoDealQuotedPrice001Dto> queryEntrustOrderList(Map<String, Object> param) {
        // 多选下拉框值处理
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", LoginUserUtil.getUserName());
        // 投资经理
        Object invManagerIdObj = param.get("invManagerId");
        if (invManagerIdObj != null) {
            param.put("invManagerIdList",
                StringUtil.splitToList(GlobalConstant.COMMA, (String)invManagerIdObj));
        }
        else {
            param.put("invManagerIdList", new ArrayList<>());
        }
        // 组合代码
        Object portfolioIdObj = param.get("portfolioId");
        if (portfolioIdObj != null) {
            param.put("portfolioIdList",
                StringUtil.splitToList(GlobalConstant.COMMA, (String)portfolioIdObj));
            param.put("portfolioId",null);
        }
        else {
            param.put("portfolioIdList", new ArrayList<>());
        }
        Object entrustIdObj = param.get("entrustId");
        if (entrustIdObj != null) {
            param.put("entrustIdMultiple",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)entrustIdObj));
            param.put("entrustId",null);
        }
        else {
            param.put("entrustIdMultiple", new ArrayList<>());
        }
        String keywords = "";
		if(param.containsKey("keywords")){
			keywords =(String) param.get("keywords");
			param.remove("keywords");
		}
		List<CfetsRepoDealQuotedPrice001Dto> list = restTemplate.postForList(
	            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/queryEntrustOrderList",
	            param, CfetsRepoDealQuotedPrice001Dto.class);
         if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(list)){
 			list = (List<CfetsRepoDealQuotedPrice001Dto>) getKeyWordsList(list,keywords,(String) param.get("keycols"));
         }
         return list;
    }
    

    
    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @param param 入参
     * @return Page<BondQuoteInfoDto>
     */
    public List<RepoQuoteInfoDto> getDayOfferTableList(Map<String, Object> param) {
        User user = SecurityUtil.getLoginUser();
        if (user == null) {
            return Collections.emptyList();
        }
        param.put("loginUserId", user.getUserName());
        //add by zznn 20210730 start
        //岗位信息 框架没有封装  单独处理
        //交易主管(交易处长03)、超级管理员能够查询出所有的报价信息
        Map<String,Object> paramUser = new HashMap<>();
        paramUser.put("userId",user.getUserName());
        paramUser.put("rolePos","03");
        boolean traDirector = commonBusinessService.isTraDirector(paramUser);
        param.put("isAdmin", user.isAdmin()||traDirector);
        //add by zznn 20210730 end
        // 待回复报价是否所有交易员可见（01：不允许，02：允许）
        param.put("unAnswerFlag", CacheUtil.getSysParam("ibuFlag_allTraderCanShowUnAnswerQuote"));
        
        String keywords = "";
		if(param.containsKey("keywords")){
			keywords =(String) param.get("keywords");
			param.remove("keywords");
		}
		List<RepoQuoteInfoDto> list =restTemplate.postForList(
	            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/getDayOfferTableList",
	            param, RepoQuoteInfoDto.class);
         if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(list)){
 			list = (List<RepoQuoteInfoDto>) getKeyWordsList(list,keywords,(String) param.get("keycols"));
         }
         return list;
    }

    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     *
     * @param param 入参
     * @return Page<BondQuoteInfoDto>
     */
    public Page<RepoQuoteInfoDto> getDayOfferTableListByPage(Map<String, Object> param) {
        User user = SecurityUtil.getLoginUser();
        if (user == null) {
            return new Page<>();
        }
        param.put("loginUserId", user.getUserName());
        //add by zznn 20210730 start
        //岗位信息 框架没有封装  单独处理
        //交易主管(交易处长03)、超级管理员能够查询出所有的报价信息
        Map<String,Object> paramUser = new HashMap<>();
        paramUser.put("userId",user.getUserName());
        paramUser.put("rolePos","03");
        boolean traDirector = commonBusinessService.isTraDirector(paramUser);
        param.put("isAdmin", user.isAdmin()||traDirector);
        //add by zznn 20210730 end
        // 待回复报价是否所有交易员可见（01：不允许，02：允许）
        param.put("unAnswerFlag", CacheUtil.getSysParam("ibuFlag_allTraderCanShowUnAnswerQuote"));

        String keywords = "";
        if(param.containsKey("keywords")){
            keywords =(String) param.get("keywords");
            param.remove("keywords");
        }
        List<RepoQuoteInfoDto> list =simpleRestTemplate.postForList(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/getDayOfferTableList",
                param, RepoQuoteInfoDto.class);
        if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(list)){
            list = (List<RepoQuoteInfoDto>) getKeyWordsList(list,keywords,(String) param.get("keycols"));
        }
        int pageNumber = Integer.parseInt(StringUtil.getOrElse(param.get("page"), "1"));
        int pageSize = Integer.parseInt(StringUtil.getOrElse(param.get("length"), "20"));

        // 封装Page对象
        Page<RepoQuoteInfoDto> page = new Page<>();
        page.setCode(0);
        page.setRecords(list.size());
        page.setPage(pageNumber+1);
        int total = list.size()%pageSize==0?list.size()/pageSize:list.size()/pageSize+1;
        page.setTotal(total);
        //截取返回集合
        List<RepoQuoteInfoDto> resultList = list.stream().sorted(Comparator.comparing(RepoQuoteInfoDto::getQuoteId)).skip((pageNumber-1)*pageSize).limit(pageSize).collect(Collectors.toList());
        page.setRoot(resultList);

        return page;
    }


    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @param param 入参
     * @return Page<BondQuoteInfoDto>
     */
    public List<RepoQuoteInfoDto> getOrderOfferTableList(Map<String, Object> param) {
        if (!StringUtil.isNotEmpty(param.get("entrustId"))){
            return new ArrayList<RepoQuoteInfoDto>();
        }
        return restTemplate.postForList(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/getOrderOfferTableList",
            param, RepoQuoteInfoDto.class);
    }
    
    /**
     * 根据查询条件查询报价编号
     * 
     * @param param 入参
     * @return 报价编号
     * @see
     * @since 1.0
     */
    public Page<SuggestDto> queryCfetsQuoteIdList(Map<String, Object> param) {
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", LoginUserUtil.getUserName());
        return restTemplate.postForPage(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/queryCfetsQuoteIdList",
            param, SuggestDto.class);
    }
    
    /**
     * 超时刷新机制
     * 
     * @param quoteId 报价编号
     * @see
     * @since 1.0
     */
    public void timeOutQuoteRefreshRepo(String quoteId){
        restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
            + "/CfetsRepoDealQuotedPrice001/timeOutQuoteRefreshRepo?quoteId=" + quoteId, Object.class);
    }
    
    
    /**
     * 
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @param param 入参
     * @return Page<BondQuoteInfoDto>
     */
    public List<CstpSelectDto> getCstpQryTableList(Map<String, Object> param) {
    	if(param.get("portfolioId") != null && StringUtil.isNotEmpty(param.get("portfolioId").toString())){
            String[] portfolioId = param.get("portfolioId").toString().split(",");
            param.replace("portfolioId", portfolioId);
        }else{
        	param.replace("portfolioId", null);
        }
        if(param.get("matchStatus") != null){
            if(param.get("matchStatus").toString().split(",").length > 1){
            	param.replace("matchStatus", null);
            }
        }
        if(param.get("tradeDateBegin") != null && !param.get("tradeDateBegin").toString().isEmpty()){
        	param.replace("tradeDateBegin",param.get("tradeDateBegin").toString().replace("-", ""));
        	param.replace("tradeDateEnd",param.get("tradeDateEnd").toString().replace("-", ""));
        }else{
        	param.replace("tradeDateBegin",null);
        	param.replace("tradeDateEnd",null);
        }
        param.put("IS_CANCEL",BussinessDictCodeList.是否_否.getValue());
        param.put("MATCH", BussinessDictCodeList.匹配状态_已匹配.getValue());
        param.put("UNMATCH", BussinessDictCodeList.匹配状态_未匹配.getValue());
        String keywords = "";
		if(param.containsKey("keywords")){
			keywords =(String) param.get("keywords");
			param.remove("keywords");
		}
		
	    // add by etliu 20210319 银行间上下行多组合 start
        param.put("loginUserId", LoginUserUtil.getUserName());
        // add by etliu 20210319 银行间上下行多组合 end
		
		List<CstpSelectDto>list = restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/BankUpCSTPSelect/getCstpQryTableList",
            param, CstpSelectDto.class);
		if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(list)){
 			list = (List<CstpSelectDto>) getKeyWordsList(list,keywords,(String) param.get("keycols"));
         }
		
        // add by etliu 20210319 银行间上下行多组合 start
        // 质押式回购成交回报中质押券面总额合计字段改为质押券券面总额，字段单位调整为万元。
        if (!StringUtil.isEmptyList(list)) {
            for (CstpSelectDto cstpSelectDto : list) {
                cstpSelectDto.setLastqty(ArithmeticUtil.divide(cstpSelectDto.getLastqty(),
                    BigDecimal.valueOf(10000), 2));
            }
        }
        // add by etliu 20210319 银行间上下行多组合 end
		
         return list;
    }
    
    
    private List<?> getKeyWordsList(List<?> list,String keywords,String keycols){
		if(StringUtil.isEmptyList(list)){
			return list;
		}
		if(!StringUtil.isNotEmpty(keywords)){
			return list;
		}
		if(!StringUtil.isNotEmpty(keycols)){
			return list;
		}
		List <String> keycolList = StringUtil.splitToList(GlobalConstant.COMMA,keycols);
		List<Object> containsKeyList = new ArrayList<>();
		for(Object cstpSelectDto:list){
			 Map<String, Object> map = JsonUtils.object2Map(cstpSelectDto);
			 for(String col:keycolList){
				 Object value = map.get(col);
				 if(StringUtil.isNotEmpty(value)&&value.toString().contains(keywords)){
					 containsKeyList.add(cstpSelectDto);
					 break;
				 }
			 }
		}
		return containsKeyList;
	}
    
    @Override
	protected void buildExportParam(ExportParam exportParam) {
		exportParam.config("/com/joyintech/tams/invest/bankuplink/bond/conf/export_conf2.xml")
				.dataSource(DataSourceType.MASERT);
	}
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<CstpSelectDto> selectPageData(Map<String, Object> selectParam) {
		Page<CstpSelectDto> page = new Page<CstpSelectDto>();
		// 导出特殊处理
		List<CstpSelectDto> list = getCstpQryTableList(selectParam);
		page.setRoot(list);
		return page;
	}
    
    /**
     * 逆回购报价是否占用同业额度
     * 
     * @param cfetsRepoDealQuotedPrice001Dto 报价信息
     * @return 错误信息
     * @see
     * @since 1.0
     */
    /*public String checkPledgeCreditAmtLimit(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        if (StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getPs())
            && StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getPortfolioId())
            && StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getMySide())
            && !StringUtil.isEmptyList(cfetsRepoDealQuotedPrice001Dto.getPledgeBondList())) {
            Map<String, Object> interMap = new HashMap<>();
            interMap.put("portfolioId", cfetsRepoDealQuotedPrice001Dto.getPortfolioId());
            interMap.put("partyId", cfetsRepoDealQuotedPrice001Dto.getMySide());
            String secAcctSource = restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/querySecAcctSourceByPtlIdPartyId", interMap, String.class);
            if (StringUtil.isNotEmpty(secAcctSource)) {
                // 10) 如果为逆回购，需要检查质押券发行人的R1额度。额度不足时，需要提示具体的剩余额度值。
                CreditAmtLimitDto creditAmtLimitDto=new CreditAmtLimitDto();
                creditAmtLimitDto.setPs(cfetsRepoDealQuotedPrice001Dto.getPs());
                creditAmtLimitDto.setSecAcctSource(secAcctSource);
                creditAmtLimitDto.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
               
                    
                List<PledgeBondInfoDto> pledgeList =new ArrayList<PledgeBondInfoDto>();
                for (CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : cfetsRepoDealQuotedPrice001Dto.getPledgeBondList()) {
                    PledgeBondInfoDto pledgeBondInfoDto=new PledgeBondInfoDto();
                    pledgeBondInfoDto.setBondCode(cfetsRepoDealQuotedPrice003Dto.getBondCode());
                    pledgeBondInfoDto.setPledgePrin(cfetsRepoDealQuotedPrice003Dto.getPledgePrin());
                    pledgeList.add(pledgeBondInfoDto);
                }
                creditAmtLimitDto.setPledgeBondList(pledgeList);
                return creditAmtLimitService.checkAmt(creditAmtLimitDto);   
            }
        }
        
        return null;
    }*/
	
	
	/**
     * 现卷报价导入excel check
     *
     * @param flieName String 文件名称
     * @return List<BondQuoteImportPreviewDto> 检验结果
     * @throws Exception
     */
    public List<RepoQuoteImportPreviewDto> importCheck(String flieName)
        throws Exception {
        List<RepoQuoteImportPreviewDto> validationList = new ArrayList<>();

        if (StringUtil.isNotEmpty(flieName)) {
            ExcelUtil excelUtil = ExcelUtil.build(FileNameUtil.getUploadFilePath(flieName), null);
            List<List<String>> dataList = excelUtil.readToList(0, 0, 0);

            if (CollectionUtils.isNotEmpty(dataList) && dataList.size() > 1) {
                // 查询已接收的委托指令且指令的接收交易员为登录用户，且指令的完成度<100%
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("entrustStatus", "unComplete");
                List<CfetsRepoDealQuotedPrice001Dto> entrustList = this.queryEntrustOrderList(param);

                // Excel中的汉字已经转换为编码（不能转换的除外）
                List<CfetsRepoDealQuotedPrice001Dto> cbdeoList = compositeCbdeoList(dataList,
                    entrustList);

                // region 设置序号rowNumber并得到转换后的对象
                AtomicInteger rowNumber = new AtomicInteger(0);
                validationList.addAll(cbdeoList.stream().map(p -> {
                	RepoQuoteImportPreviewDto previewDto = new RepoQuoteImportPreviewDto();
                    try {
                        BeanUtils.copyProperties(previewDto, p);
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        log.error(e.getMessage());
                    }
                    previewDto.setRowNumber(rowNumber.addAndGet(1));
                    return previewDto;
                }).collect(Collectors.toList()));
                // endRegion

                // region检查是否有重复数据
                if (_needDuplicateCheck) {
                    String requestJson = JSON.toJSONString(validationList);
                    log.debug("requestJson is: {}", requestJson);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
                    HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
                    ParameterizedTypeReference<String> respType = new ParameterizedTypeReference<String>() {
                    };

                    try {
                        ResponseEntity<String> respEntity = restTemplate.exchange(//////////////////////////////////////////
                            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/bondQuoteListDuplicateCheck",
                            HttpMethod.POST, entity, respType);

                        if (respEntity != null) {
                            log.debug(respEntity.getBody());
                            validationList = JSONObject.parseObject(respEntity.getBody(),
                                new TypeReference<List<RepoQuoteImportPreviewDto>>() {
                                });
                        }
                    }
                    catch (RestClientException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        setErrorMsgOnTop(validationList, e.getMessage());
                        return validationList;
                    }
                }
                // endRegion
                

                // region普通验证及业务逻辑验证
                Map<String, BigDecimal> pledgePrinMap = new HashMap<String, BigDecimal>();
                boolean pledgeFlag = false;
                String parentEntrustId = "";
                String portfolioId = "";
                String ps = "";
                String repRegistOrg = "";
                for(RepoQuoteImportPreviewDto p : validationList) {
                	CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = null;
                	if(pledgeFlag) {
                		if(StringUtils.isEmpty(p.getEntrustId())
                				&&StringUtils.isEmpty(p.getPortfolioId()) 
                				&&StringUtils.isEmpty(p.getPortfolioName()) 
                				&&StringUtils.isEmpty(p.getQuoteType()) 
                				&&StringUtils.isEmpty(p.getPsStr()) 
                				&&StringUtils.isEmpty(p.getMySideAbbr()) 
                				&&StringUtils.isEmpty(p.getMySideTraderName()) 
                				) {
                			/////////////质押券信息
                        	if(StringUtil.isEmpty(p.getBondCode())) {
                        		setValidationMsg(p, "bondCode",
                                        getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_057")));
                            }
                        	if (p.getPledgePrin() == null) {
                                setValidationMsg(p, "pledgePrin",
                                    getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_058")));
                            }
                        	if (p.getPledgeRate() == null) {
                                setValidationMsg(p, "pledgeRate",
                                    getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_059")));
                            }
                        	p.setParentEntrustId(parentEntrustId);
                        	if(StringUtil.isNotEmpty(p.getBondCode())){
                        		Map<String, Object> params = new HashMap<String, Object>();
                                params.put("portfolioId", portfolioId);
                                params.put("ps", ps);
                                params.put("bondCode", p.getBondCode());
                            	List<PledgeBondInfoDto> list = bussRepoSecService.selectAvaPledgeBondDetailList(params);
                            	if(list.isEmpty()) {
                            		setValidationMsg(p, "bondCode",
                                            getMessage("QUOTE_UPLOAD_060", p.getBondCode()));
                            	}else {
                            		PledgeBondInfoDto pledgeBondInfoDto = list.get(0);
                            		p.setFinprodId(pledgeBondInfoDto.getFinprodId());
                            		BigDecimal pledgePrinTotal = pledgePrinMap.get(pledgeBondInfoDto.getFinprodId());
                            		if(pledgePrinTotal!=null) {
                            			pledgePrinTotal = pledgePrinTotal.add(p.getPledgePrin());
                            			pledgePrinMap.put(pledgeBondInfoDto.getFinprodId(),pledgePrinTotal);
                            		}else {
                            			pledgePrinMap.put(pledgeBondInfoDto.getFinprodId(),p.getPledgePrin());
                            		}
                            		if(!StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue()) && (pledgeBondInfoDto.getAvaPledgePrin() == null || pledgeBondInfoDto.getAvaPledgePrin().compareTo(pledgePrinMap.get(pledgeBondInfoDto.getFinprodId()).multiply(new BigDecimal(10000)))==-1)) {
                            			setValidationMsg(p, "pledgePrin",getMessage("QUOTE_UPLOAD_061"));
                            		}
                            		if(!repRegistOrg.equals(pledgeBondInfoDto.getRepRegistOrg())) {
                            			setValidationMsg(p, "pledgePrin",getMessage("QUOTE_UPLOAD_062"));
                            		}
                            	}
                        	}
                        	
                        	continue;
                		}
                	}
                    
                    // 判断委托代码是否为空
                    if (StringUtils.isBlank(p.getEntrustId())) {
                        setValidationMsg(p, "entrustId",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_007")));
                    }
                    else {
                		pledgeFlag = true;
                		parentEntrustId = p.getEntrustId();
                        // 判断是否是已接收的委托指令且指令的接收交易员为登录用户，且指令的完成度<100%
                        if (entrustList.isEmpty()) {
                            setValidationMsg(p, "entrustId",
                                getMessage("QUOTE_UPLOAD_031", p.getEntrustId()));
                        }
                        else {
                            List<CfetsRepoDealQuotedPrice001Dto> filterList = entrustList.stream().filter(
                                e -> StringUtil.equals(e.getEntrustId(),
                                    p.getEntrustId())).collect(Collectors.toList());
                            if (StringUtil.isEmptyList(filterList)) {
                                setValidationMsg(p, "entrustId",
                                    getMessage("QUOTE_UPLOAD_031", p.getEntrustId()));
                            }
                            else {
                            	cfetsRepoDealQuotedPrice001Dto = filterList.get(0);
                            	portfolioId = cfetsRepoDealQuotedPrice001Dto.getPortfolioId();
                            	ps = cfetsRepoDealQuotedPrice001Dto.getPs();
                            }
                        }
                    }

                    // 判断组合代码需要验证是否与委托指令的组合代码一致
                    if (!StringUtil.isBlank(p.getPortfolioId())
                        && cfetsRepoDealQuotedPrice001Dto != null) {
                        if (!StringUtil.equals(p.getPortfolioId(),
                        		cfetsRepoDealQuotedPrice001Dto.getPortfolioId())) {
                            setValidationMsg(p, "portfolioId", getMessage("QUOTE_UPLOAD_032",
                                this.getMessage("QUOTE_UPLOAD_009"), p.getPortfolioId()));
                        }
                    }
                    //组合代码为空
                    if(StringUtil.isBlank(p.getPortfolioId())) {
                    	setValidationMsg(p, "portfolioId",
                                getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_009")));
                    }

                    // 报价方式
                    if (StringUtils.isBlank(p.getQuoteType())) {
                        // quoteTypeStr对应界面(invest\bankuplink\bond\fileupload.js)
                        // importResultTable绑定的属性名
                        setValidationMsg(p, "quoteTypeStr",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_011")));
                    }else {
                        if (!BussinessDictCodeList.报价方式_对话报价.getValue().equals(p.getQuoteType())) {
                            setValidationMsg(p, "quoteTypeStr",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_011")));
                        }
                    }
                    
                    // 本方方向
                    if (StringUtils.isBlank(p.getPs())) {
                        setValidationMsg(p, "psStr",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_014")));
                    }
                    else {
                        if (!BussinessDictCodeList.回购拆借方向_正回购.getValue().equals(p.getPs())
                            && !BussinessDictCodeList.回购拆借方向_逆回购.getValue().equals(p.getPs())) {
                            setValidationMsg(p, "psStr",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_014")));
                        }
                        else {
                            if (cfetsRepoDealQuotedPrice001Dto != null
                                && !StringUtil.equals(p.getPs(),
                                		cfetsRepoDealQuotedPrice001Dto.getPs())) {
                                setValidationMsg(p, "psStr",
                                    getMessage("QUOTE_UPLOAD_032",
                                        this.getMessage("QUOTE_UPLOAD_014"),
                                        SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向,
                                            p.getPs())));
                            }
                        }
                    }
                    
                    // 本方
                    // 导入的应该是本方机构名称
                    if (StringUtils.isBlank(p.getMySideAbbr())) {
                        setValidationMsg(p, "mySideAbbr",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_015")));
                    }
                    else {
                        if (cfetsRepoDealQuotedPrice001Dto != null) {
                            if (!StringUtil.equals(p.getMySideAbbr(),
                            		cfetsRepoDealQuotedPrice001Dto.getMySideAbbr())) {
                                setValidationMsg(p, "mySideAbbr", getMessage("QUOTE_UPLOAD_033"));
                            }
                            else {
                                p.setMySide(cfetsRepoDealQuotedPrice001Dto.getMySide());
                            }
                        }
                    }

                    // 本方交易员
                    if (StringUtils.isNotBlank(p.getMySideTraderName())) {
                        if (StringUtils.isNotBlank(p.getMySide())) {
                            Map<String, Object> mySideTraderMap = new HashMap<String, Object>();
                            mySideTraderMap.put("mySide", p.getMySide());
                            List<SelectOptionDto> mySideTraderList = cfetsQuotedCommonService.queryMySideTraderList(
                                mySideTraderMap);
                            if (!StringUtil.isEmptyList(mySideTraderList)) {
                                Optional<SelectOptionDto> opt = mySideTraderList.stream().filter(
                                    x -> StringUtils.equals(x.getText(),
                                        p.getMySideTraderName())).findFirst();
                                if (!opt.isPresent()) {
                                    setValidationMsg(p, "mySideTraderName",
                                        getMessage("QUOTE_UPLOAD_034", p.getMySideTraderName()));
                                }
                                else {
                                    p.setMySideTrader(opt.get().getId());
                                }
                            }
                            else {
                                setValidationMsg(p, "mySideTraderName",
                                    getMessage("QUOTE_UPLOAD_034", p.getMySideTraderName()));
                            }
                        }
                    }
                    else {
                        setValidationMsg(p, "mySideTraderName",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_016")));
                    }

                    // 对手方
                    if (StringUtil.isNotBlank(p.getCounterName())) {
                      // 查询银行间机构列表
                    	Map<String, Object> param1 = new HashMap<String, Object>();
                    	param1.put("counterName",p.getCounterName());
                    	List<SelectOptionDto> mySideList = cfetsQuotedCommonService.queryMySideList(param1);
                        if (!StringUtil.isEmptyList(mySideList)) {
                            Optional<SelectOptionDto> opt = mySideList.stream().filter(
                                x -> StringUtils.equals(x.getText(), p.getCounterName())).findFirst();
                            if (!opt.isPresent()) {
                                setValidationMsg(p, "counterId",
                                    getMessage("QUOTE_UPLOAD_035", p.getCounterName()));
                            }
                            else {
                                p.setCounterId(opt.get().getId());
                            }
                        }else {
                        	setValidationMsg(p, "counterId",
                                    getMessage("QUOTE_UPLOAD_035", p.getCounterName()));
                        }
                    }
                    else {
                        setValidationMsg(p, "counterName",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_017")));
                    }

                    // 对手方交易员
                    if (StringUtil.equals(BussinessDictCodeList.报价方式_对话报价.getValue(),
                        p.getQuoteType())) {
                        if (StringUtil.isEmpty(p.getCounterTraderName())) {
                            setValidationMsg(p, "counterTrader",
                                getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_018")));
                        }
                        else if (StringUtil.isNotBlank(p.getCounterId())) {
                            // 检查对手方交易员是否在对手方机构中
                            Map<String, Object> counterIdMap = new HashMap<String, Object>();
                            counterIdMap.put("counterId", p.getCounterId());
                            List<SelectOptionDto> counterIdList = cfetsQuotedCommonService.queryMySideTraderList(
                                counterIdMap);
                            if (!StringUtil.isEmptyList(counterIdList)) {
                                Optional<SelectOptionDto> opt = counterIdList.stream().filter(
                                    x -> StringUtils.equals(x.getText(),
                                        p.getCounterTraderName())).findFirst();
                                if (!opt.isPresent()) {
                                    setValidationMsg(p, "counterTrader",
                                        getMessage("QUOTE_UPLOAD_036", p.getCounterTraderName()));
                                }
                                else {
                                    p.setCounterTrader(opt.get().getId());
                                }
                            }
                            else {
                                setValidationMsg(p, "counterTrader",
                                    getMessage("QUOTE_UPLOAD_036", p.getCounterTraderName()));
                            }
                        }
                    }
                    
//                    // 回购方式
//                    if (StringUtils.isBlank(p.getRepoMethod())) {
//                        setValidationMsg(p, "repoMethod",
//                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_051")));
//                    }
//                    else {
//                        if (!"双边回购".equals(p.getRepoMethod())) {
//                            setValidationMsg(p, "repoMethod",
//                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_051")));
//                        }
//                    }
                    
                    // 回购期限
                    if (p.getTerm()==0l) {
                        setValidationMsg(p, "term",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_052")));
                    }
                    else {
                        if (cfetsRepoDealQuotedPrice001Dto != null && !(p.getTerm()==cfetsRepoDealQuotedPrice001Dto.getTerm())) {
                                setValidationMsg(p, "term",
                                    getMessage("QUOTE_UPLOAD_032",
                                        this.getMessage("QUOTE_UPLOAD_052")));
                            }
                    }
                    
                    // 回购利率
                    if (p.getPrice()==null) {
                        setValidationMsg(p, "price",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_053")));
                    }
                    else {
                        if (cfetsRepoDealQuotedPrice001Dto != null
                                && !(p.getPrice().compareTo(cfetsRepoDealQuotedPrice001Dto.getPrice())==0)) {
                                setValidationMsg(p, "price",
                                    getMessage("QUOTE_UPLOAD_032",
                                        this.getMessage("QUOTE_UPLOAD_053")));
                            }
                    }
                    
                    // 交易金额(元)
                    if (p.getDealAmt() == null && !BussinessDictCodeList.回购拆借方向_正回购.getValue().equals(p.getPs())) {
                        setValidationMsg(p, "dealAmt",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_054")));
                    }
                    
                    /////////////质押券信息
                    if(BussinessDictCodeList.回购拆借方向_正回购.getValue().equals(p.getPs())) {
                    	if(StringUtil.isEmpty(p.getBondCode())&&StringUtil.isEmpty(p.getBondAbbrName())) {
                    		setValidationMsg(p, "bondCode",
                                    getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_057")));
                        }
                    	if (p.getPledgePrin() == null) {
                            setValidationMsg(p, "pledgePrin",
                                getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_058")));
                        }
                    	if (p.getPledgeRate() == null) {
                            setValidationMsg(p, "pledgeRate",
                                getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_059")));
                        }
                    }
                	if(StringUtil.isNotEmpty(p.getBondCode())){
                		Map<String, Object> params = new HashMap<String, Object>();
                        params.put("portfolioId", portfolioId);
                        params.put("ps", ps);
                        params.put("bondCode", p.getBondCode());
                    	List<PledgeBondInfoDto> list = bussRepoSecService.selectAvaPledgeBondDetailList(params);
                    	if(list.isEmpty()) {
                    		setValidationMsg(p, "bondCode",
                                    getMessage("QUOTE_UPLOAD_060", p.getBondCode()));
                    	}else {
                    		PledgeBondInfoDto pledgeBondInfoDto = list.get(0);
                    		p.setFinprodId(pledgeBondInfoDto.getFinprodId());
                    		repRegistOrg = pledgeBondInfoDto.getRepRegistOrg();
                    		BigDecimal pledgePrinTotal = pledgePrinMap.get(pledgeBondInfoDto.getFinprodId());
                    		if(pledgePrinTotal!=null) {
                    			pledgePrinTotal = pledgePrinTotal.add(p.getPledgePrin());
                    			pledgePrinMap.put(pledgeBondInfoDto.getFinprodId(),pledgePrinTotal);
                    		}else {
                    			pledgePrinMap.put(pledgeBondInfoDto.getFinprodId(),p.getPledgePrin());
                    		}
                    		if(!StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())&&(pledgeBondInfoDto.getAvaPledgePrin() == null || pledgeBondInfoDto.getAvaPledgePrin().compareTo(pledgePrinMap.get(pledgeBondInfoDto.getFinprodId()).multiply(new BigDecimal(10000)))==-1)) {
                    			setValidationMsg(p, "pledgePrin",getMessage("QUOTE_UPLOAD_061"));
                    		}
                    	}
                	}
                    
                    
                    // 清算速度
                    if (StringUtils.isNotBlank(p.getSettleSpreed())) {
                        if (!BussinessDictCodeList.清算速度_T加0.getValue().equals(p.getSettleSpreed())
                            && !BussinessDictCodeList.清算速度_T加1.getValue().equals(
                                p.getSettleSpreed())) {
                            setValidationMsg(p, "settleSpreedStr",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_023")));
                        }
                    }
                    else {
                        setValidationMsg(p, "settleSpreedStr",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_023")));
                    }
                    
                    // 清算类型
                    if (StringUtils.isNotBlank(p.getSettleType())) {
                        if (!BussinessDictCodeList.清算类型_全额清算.getValue().equals(p.getSettleType())
                            && !BussinessDictCodeList.清算类型_净额清算.getValue().equals(
                                p.getSettleType())) {
                            setValidationMsg(p, "settleTypeStr",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_025")));
                        }
                    }
                    else {
                        setValidationMsg(p, "settleTypeStr",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_025")));
                    }
                    
                    // 首次结算方式
                    if (StringUtils.isNotBlank(p.getvDeliveryType())) {
                        if (!BussinessDictCodeList.结算方式_见券付款.getValue().equals(p.getvDeliveryType())
                            && !BussinessDictCodeList.结算方式_见款付券.getValue().equals(
                                p.getvDeliveryType())
                            && !BussinessDictCodeList.结算方式_券款对付.getValue().equals(
                                p.getvDeliveryType())) {
                            setValidationMsg(p, "vDeliveryType",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_055")));
                        }
                    }
                    else {
                        setValidationMsg(p, "vDeliveryType",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_055")));
                    }
                    
                    // 到期结算方式
                    if (StringUtils.isNotBlank(p.getmDeliveryType())) {
                        if (!BussinessDictCodeList.结算方式_见券付款.getValue().equals(p.getmDeliveryType())
                            && !BussinessDictCodeList.结算方式_见款付券.getValue().equals(
                                p.getmDeliveryType())
                            && !BussinessDictCodeList.结算方式_券款对付.getValue().equals(
                                p.getmDeliveryType())) {
                            setValidationMsg(p, "mDeliveryType",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_056")));
                        }
                    }
                    else {
                        setValidationMsg(p, "mDeliveryType",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_056")));
                    }

                }
                // endRegion
            }
            else {
                setErrorMsgOnTop(validationList, getMessage("QUOTE_UPLOAD_001"));
            }

            return validationList;
        }

        return null;
    }
    
    /**
     * 将Excel中的数据解析组装成List<CfetsBondDealEntrustOrderDto>
     */
    private List<CfetsRepoDealQuotedPrice001Dto> compositeCbdeoList(List<List<String>> dataList,
                                                                  List<CfetsRepoDealQuotedPrice001Dto> entrustList) {
        List<CfetsRepoDealQuotedPrice001Dto> cbdeoList = new ArrayList<>();
        CfetsRepoDealQuotedPrice001Dto itemDto;

        for (List<String> rowList : dataList.subList(1, dataList.size())) {
            itemDto = new CfetsRepoDealQuotedPrice001Dto();

            itemDto.setEntrustId(StringUtils.stripToNull(rowList.get(0)));
            itemDto.setInvManagerId(StringUtils.stripToNull(rowList.get(1)));
            itemDto.setPortfolioId(StringUtils.stripToNull(rowList.get(2)));
            itemDto.setPortfolioName(StringUtils.stripToNull(rowList.get(3)));
            if (StringUtils.isNotBlank(rowList.get(4))) {
                if (BussinessDictCodeList.报价方式_对话报价.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(4)))) {
                    itemDto.setQuoteType(BussinessDictCodeList.报价方式_对话报价.getValue());
                }else {
                    itemDto.setQuoteType(StringUtils.stripToNull(rowList.get(4)));
                }
            }
            if (StringUtils.isNotBlank(rowList.get(5))) {
                if (BussinessDictCodeList.回购拆借方向_正回购.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(5)))) {
                    itemDto.setPs(BussinessDictCodeList.回购拆借方向_正回购.getValue());
                }
                else if (BussinessDictCodeList.回购拆借方向_逆回购.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(5)))) {
                    itemDto.setPs(BussinessDictCodeList.回购拆借方向_逆回购.getValue());
                }
                else {
                    itemDto.setPs(StringUtils.stripToNull(rowList.get(5)));
                }
            }
            itemDto.setMySideAbbr(StringUtils.stripToNull(rowList.get(6)));
            itemDto.setMySideTraderName(StringUtils.stripToNull(rowList.get(7)));
            itemDto.setCounterName(StringUtils.stripToNull(rowList.get(8)));
            itemDto.setCounterTraderName(StringUtils.stripToNull(rowList.get(9)));
//            itemDto.setRepoMethod(StringUtils.stripToNull(rowList.get(10)));
            if(StringUtils.stripToNull(rowList.get(10))!=null) {
            	itemDto.setTerm(Long.parseLong(StringUtils.stripToNull(rowList.get(10))));
            }
            itemDto.setPrice(getDecimal(rowList.get(11)));
            //交易金额和质押券信息
            itemDto.setDealAmt(getDecimal(rowList.get(12)));
            itemDto.setBondCode(StringUtils.stripToNull(rowList.get(13)));
            itemDto.setBondAbbrName(StringUtils.stripToNull(rowList.get(14)));
            itemDto.setPledgePrin(getDecimal(rowList.get(15)));
            itemDto.setPledgeRate(getDecimal(rowList.get(16)));
            
            if (StringUtils.isNotBlank(rowList.get(17))) {
            	if (BussinessDictCodeList.清算速度_T加0.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(17)))) {
                    itemDto.setSettleSpreed(BussinessDictCodeList.清算速度_T加0.getValue());
                }
                else if (BussinessDictCodeList.清算速度_T加1.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(17)))) {
                    itemDto.setSettleSpreed(BussinessDictCodeList.清算速度_T加1.getValue());
                }
                else {
                    itemDto.setSettleSpreed(StringUtils.stripToNull(rowList.get(17)));
                }
            }
            if (StringUtils.isNotBlank(rowList.get(18))) {
            	itemDto.setSettleTypeStr(StringUtils.stripToNull(rowList.get(18)));
                if (BussinessDictCodeList.清算类型_全额清算.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(18)))) {
                    itemDto.setSettleType(BussinessDictCodeList.清算类型_全额清算.getValue());
                }
                else if (BussinessDictCodeList.清算类型_净额清算.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(18)))) {
                    itemDto.setSettleType(BussinessDictCodeList.清算类型_净额清算.getValue());
                }
                else {
                    itemDto.setSettleType(StringUtils.stripToNull(rowList.get(18)));
                }
            }
            if (StringUtils.isNotBlank(rowList.get(19))) {
            	itemDto.setvDeliveryTypeStr(StringUtils.stripToNull(rowList.get(19)));
                if (BussinessDictCodeList.结算方式_见券付款.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(19)))) {
                    itemDto.setvDeliveryType(BussinessDictCodeList.结算方式_见券付款.getValue());
                }
                else if (BussinessDictCodeList.结算方式_见款付券.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(19)))) {
                    itemDto.setvDeliveryType(BussinessDictCodeList.结算方式_见款付券.getValue());
                }
                else if (BussinessDictCodeList.结算方式_券款对付.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(19)))) {
                    itemDto.setvDeliveryType(BussinessDictCodeList.结算方式_券款对付.getValue());
                }
                else {
                    itemDto.setvDeliveryType(StringUtils.stripToNull(rowList.get(19)));
                }
            }
            if (StringUtils.isNotBlank(rowList.get(20))) {
            	itemDto.setmDeliveryTypeStr(StringUtils.stripToNull(rowList.get(20)));
                if (BussinessDictCodeList.结算方式_见券付款.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(20)))) {
                    itemDto.setmDeliveryType(BussinessDictCodeList.结算方式_见券付款.getValue());
                }
                else if (BussinessDictCodeList.结算方式_见款付券.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(20)))) {
                    itemDto.setmDeliveryType(BussinessDictCodeList.结算方式_见款付券.getValue());
                }
                else if (BussinessDictCodeList.结算方式_券款对付.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(20)))) {
                    itemDto.setmDeliveryType(BussinessDictCodeList.结算方式_券款对付.getValue());
                }
                else {
                    itemDto.setmDeliveryType(StringUtils.stripToNull(rowList.get(20)));
                }
            }
            itemDto.setSendStatus(BussinessDictCodeList.银行间上行发送状态_待发送.getValue());
            cbdeoList.add(itemDto);
        }

        return cbdeoList;
    }
    
    /**
     * 在预览数据的顶部插入一条错误消息数据
     * 
     * @param validationList List<BondQuoteImportPreviewDto>
     * @param errMessage String
     */
    private void setErrorMsgOnTop(List<RepoQuoteImportPreviewDto> validationList,
                                  String errMessage) {
        // 改变原有列表中的rowNumber属性
        if (CollectionUtils.isNotEmpty(validationList)) {
            validationList.forEach(p -> {
                p.setRowNumber(p.getRowNumber() + 1);
            });
        }

        RepoQuoteImportPreviewDto validDto = new RepoQuoteImportPreviewDto();
        validDto.setValidationErrorMsg(errMessage);
        validDto.setRowNumber(1);
        validationList.add(0, validDto);
    }
    
	private void setValidationMsg(RepoQuoteImportPreviewDto dto, String fieldName, String message) {
		setValidationMsg(dto, fieldName, message, true);
	}

	private void setValidationMsg(RepoQuoteImportPreviewDto dto, String fieldName, String message, boolean isHard) {
		dto.addValidFieldName(fieldName);
		if (isHard) {
			if (StringUtils.isNotBlank(dto.getValidationErrorMsg())) {
				dto.setValidationErrorMsg(dto.getValidationErrorMsg() + "\n" + message);
			} else {
				dto.setValidationErrorMsg(message);
			}
		} else {
			if (StringUtils.isNotBlank(dto.getValidationWarnMsg())) {
				dto.setValidationWarnMsg(dto.getValidationWarnMsg() + "\n" + message);
			} else {
				dto.setValidationWarnMsg(message);
			}
		}
	}   
	
	/**
     * 字符串转换为BigDecimal<br />
     * 若为空字符串返回null
     */
    private BigDecimal getDecimal(String data) {
        if (StringUtils.isNotBlank(data)) {
            return new BigDecimal(data.trim());
        }
        return null;
    }

    /**
     * 组合分配列表
     * @param map
     * @return
     */
    public List<PtlAllocationDto> selectEtrReByQuoteId(Map<String,Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/selectEtrReByQuoteId",
                map, PtlAllocationDto.class);
    }


    public List<CfetsRepoDealQuotedPrice001Dto> getQualifiedByentrustIds(Map<String,Object> map){
        return restTemplate.postForList(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/getQualifiedByentrustIds",
                map, CfetsRepoDealQuotedPrice001Dto.class);
    }

    public CfetsRepoDealQuotedPrice003Dto queryPledgeBondElementForImport(String finprodId) {
        Map<String, Object> param = new HashMap<>();
        param.put("finprodId",finprodId);
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("valueSourceCBC",BussinessDictCodeList.估值来源_中债登.getValue());
        return restTemplate.postForObject(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/queryPledgeBondElementForImport",
                param, CfetsRepoDealQuotedPrice003Dto.class);
    }
}
