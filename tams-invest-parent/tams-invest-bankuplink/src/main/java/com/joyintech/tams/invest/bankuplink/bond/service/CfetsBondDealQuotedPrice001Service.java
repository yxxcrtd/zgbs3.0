/*
 * 文件名：CfetsBondDealQuotedPrice001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.bond.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.joyintech.framework.JoyinRestTemplate;
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
import com.joyintech.framework.web.socket.SocketService;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteImportPreviewDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.QuoteBatchResult;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.bankuplink.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;

/**
 * CFETS现券买卖报价
 * 
 * @author zhangliang
 * @version 1.0 2020年5月12日
 * @see CfetsBondDealQuotedPrice001Service
 * @since 1.0
 */
@Service
public class CfetsBondDealQuotedPrice001Service extends CommonExportService<CfetsBondDealEntrustOrderDto>  {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 外汇交易中心行情接口地址
     */
    @Value("${MarketInfoInterf.cmdsUrl}")
    private String MarketInfoInterf_cmdsUrl;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private BaseGetBussinessId bussinessIdGetter;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private SocketService socket;

    // 是否需要重复校验（重复校验未经过测试）
    @Value("${application.banklink.duplicatecheck:false}")
    private boolean _needDuplicateCheck;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;
    
    @Autowired
    private CfetsBondDealQuotedPrice002Service cfetsBondDealQuotedPrice002Service;
    
    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 产品评级共通
     */
    @Autowired(required = false)
    private BusiProductRatingService busiProductRatingService;

    /**
     * 根据查询条件查询委托列表
     * 
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public List<CfetsBondDealEntrustOrderDto> queryEntrustOrderList(Map<String, Object> param) {
        // 多选下拉框值处理
        // 投资经理
//        Object invManagerIdObj = param.get("invManagerId");
//        if (invManagerIdObj != null) {
//            param.put("invManagerIdList",
//                StringUtil.splitToList(GlobalConstant.COMMA, (String)invManagerIdObj));
//        }
//        else {
//            param.put("invManagerIdList", new ArrayList<>());
//        }
        if(StringUtil.isNotEmpty(param.get("invManagerIdList"))){
        	param.put("invManagerIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)param.get("invManagerIdList")));
        }
        if(StringUtil.isNotEmpty(param.get("finprodIdList"))){
        	param.put("finprodIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)param.get("finprodIdList")));
        }
        if(StringUtil.isNotEmpty(param.get("portfolioIdList"))){
        	param.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)param.get("portfolioIdList")));
        }
//        // 组合代码
//        Object portfolioIdObj = param.get("portfolioId");
//        if (portfolioIdObj != null) {
//            param.put("portfolioIdList",
//                    StringUtil.splitToList(GlobalConstant.COMMA, (String)portfolioIdObj));
//            param.put("portfolioId",null);
//        }
//        else {
//            param.put("portfolioIdList", new ArrayList<>());
//        }
        Object entrustIdObj = param.get("entrustId");
        if (entrustIdObj != null) {
            param.put("entrustIdMultiple",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)entrustIdObj));
            param.put("entrustId",null);
        }
        else {
            param.put("entrustIdMultiple", new ArrayList<>());
        }
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", LoginUserUtil.getUserName());
        String keywords = "";
		if(param.containsKey("keywords")){
			keywords =(String) param.get("keywords");
			param.remove("keywords");
		}
        List<CfetsBondDealEntrustOrderDto> cfetsBondDealEntrustOrderDtoList =  restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/CfetsBondDealQuotedPrice/queryEntrustOrderList",
            param, CfetsBondDealEntrustOrderDto.class);
        if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(cfetsBondDealEntrustOrderDtoList)){
        	cfetsBondDealEntrustOrderDtoList = (List<CfetsBondDealEntrustOrderDto>) getKeyWordsList(cfetsBondDealEntrustOrderDtoList,keywords,(String) param.get("keycols"));
         }
        return cfetsBondDealEntrustOrderDtoList;
        
    }
    /**
     * 根据查询条件查询委托列表
     * 
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public Page<CfetsBondDealEntrustOrderDto> queryEntrustOrderPage(Map<String, Object> param) {
        // 多选下拉框值处理
        // 投资经理
    	if(StringUtil.isNotEmpty(param.get("invManagerIdList"))){
        	param.put("invManagerIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)param.get("invManagerIdList")));
        }
        if(StringUtil.isNotEmpty(param.get("finprodIdList"))){
        	param.put("finprodIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)param.get("finprodIdList")));
        }
        if(StringUtil.isNotEmpty(param.get("portfolioIdList"))){
        	param.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)param.get("portfolioIdList")));
        }
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", LoginUserUtil.getUserName());
        return restTemplate.postForPage(ServiceApplication.BANKUPLINK_SERVICE_NAME
            + "/CfetsBondDealQuotedPrice/queryEntrustOrderPage",
            param, CfetsBondDealEntrustOrderDto.class);
    }

    @Override
	protected void buildExportParam(ExportParam exportParam) {
		exportParam.config("/com/joyintech/tams/invest/bankuplink/bond/conf/export_conf_entrustOrder.xml")
				.dataSource(DataSourceType.MASERT);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<CfetsBondDealEntrustOrderDto> selectPageData(Map<String, Object> selectParam) {
        Page<CfetsBondDealEntrustOrderDto> page = queryEntrustOrderPage(selectParam);
        // 导出特殊处理
//        if (page != null && !StringUtil.isEmptyList(page.getRoot())) {
//            List<CfetsBondDealEntrustOrderDto> list = page.getRoot();
//            for (CfetsBondDealEntrustOrderDto dto : list) {
//                if (StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_净价.getValue())) {
//                    dto.setMatureYield(null);
//                    dto.setExerYield(null);
//                }
//                else if (StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_到期收益率.getValue())) {
//                    dto.setPrice(null);
//                    dto.setExerYield(null);
//                }
//                else if (StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_行权收益率.getValue())) {
//                    dto.setPrice(null);
//                    dto.setMatureYield(null);
//                }
//            }
//            page.setRoot(list);
//        }
        return page;
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
        return restTemplate.postForPage(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/CfetsBondDealQuotedPrice/queryCfetsQuoteIdList",
            param, SuggestDto.class);
    }

    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @param param 入参
     * @return Page<BondQuoteInfoDto>
     */
    public List<BondQuoteInfoDto> getDayOfferTableList(Map<String, Object> param) {
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
		List<BondQuoteInfoDto> res = restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                + "/CfetsBondDealQuotedPrice/getDayOfferTableList",param, BondQuoteInfoDto.class);

        // 债项评级
        for(BondQuoteInfoDto dto : res ){
            Map<String,Object> zxparam = new HashMap<String,Object>();
            zxparam.put("oCode", dto.getFinprodMarketId());
            zxparam.put("oType", DictCodeList.评级类型_债项评级.getValue());
            //查询评级类型是债项评级的数据
            List<BusiFinProductRatingDto> zxList = busiProductRatingService.getZxBestRatingByMarketId(zxparam);
            if(zxList != null && zxList.size()>0){
                //只取第一条数据
                dto.setGrade(zxList.get(0).getGradeResult());
            }
        }
         if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(res)){
        	 res = (List<BondQuoteInfoDto>) getKeyWordsList(res,keywords,(String) param.get("keycols"));
         }
        return res;
    }

    public Page<BondQuoteInfoDto> getDayOfferTableListByPage(Map<String, Object> param) {
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
        List<BondQuoteInfoDto> list = simpleRestTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                + "/CfetsBondDealQuotedPrice/getDayOfferTableList",param, BondQuoteInfoDto.class);
        if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(list)){
            list = (List<BondQuoteInfoDto>) getKeyWordsList(list,keywords,(String) param.get("keycols"));
        }
        int pageNumber = Integer.parseInt(StringUtil.getOrElse(param.get("page"), "1"));
        int pageSize = Integer.parseInt(StringUtil.getOrElse(param.get("length"), "20"));
        // 封装Page对象
        Page<BondQuoteInfoDto> page = new Page<>();
        page.setCode(0);
        page.setRecords(list.size());
        page.setPage(pageNumber+1);
        int total = list.size()%pageSize==0?list.size()/pageSize:list.size()/pageSize+1;
        page.setTotal(total);
        //截取返回集合
        List<BondQuoteInfoDto> resultList = list.stream().sorted(Comparator.comparing(BondQuoteInfoDto::getQuoteId)).skip((pageNumber-1)*pageSize).limit(pageSize).collect(Collectors.toList());
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
    public List<BondQuoteInfoDto> getOrderOfferTableList(Map<String, Object> param) {
        if (!StringUtil.isNotEmpty(param.get("entrustId"))) {
            return new ArrayList<BondQuoteInfoDto>();
        }
        return restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/CfetsBondDealQuotedPrice/getOrderOfferTableList",
            param, BondQuoteInfoDto.class);
    }

    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @param param 入参
     * @return Page<BondQuoteInfoDto>
     */
    public List<BondQuoteInfoDto> getMarketOfferTableList(Map<String, Object> param) {
        return restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/CfetsBondDealQuotedPrice/getMarketOfferTableList",
            param, BondQuoteInfoDto.class);
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
		
		List<CstpSelectDto> list =restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/BankUpCSTPSelect/getCstpQryTableList",
            param, CstpSelectDto.class);
         if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(list)){
 			list = (List<CstpSelectDto>) getKeyWordsList(list,keywords,(String) param.get("keycols"));
         }
         
        // add by etliu 20210319 银行间上下行多组合 start
        // 现券买卖成交回报中券面总额合计字段改为券面总额，字段单位调整为万元。
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
    /**
     * 撤回
     * 
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    public boolean doReCall(BondQuoteInfoDto bondQuoteInfoDto) {
        if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
            BussinessDictCodeList.银行间上行本方角色_询价方.getValue())) {
            return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                              + "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteRevoke",
                bondQuoteInfoDto, boolean.class);
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
            BussinessDictCodeList.银行间上行本方角色_做市方.getValue())) {
            return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                              + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteRevokeReply",
                bondQuoteInfoDto, boolean.class);
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
            BussinessDictCodeList.银行间上行本方角色_非做市方.getValue())) {
            return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                              + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteRevoke",
                bondQuoteInfoDto, boolean.class);
        }
        return false;
    }

    /**
     * 回退
     * 
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    public boolean doBack(BondQuoteInfoDto bondQuoteInfoDto) {
        if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
            BussinessDictCodeList.报价方式_对话报价.getValue())) {
            return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                              + "/CfetsBondDealQuotedPrice/bondDialogueQuoteRestore",
                bondQuoteInfoDto, boolean.class);
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
            BussinessDictCodeList.报价方式_请求报价.getValue())) {
            return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                              + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteRestore",
                bondQuoteInfoDto, boolean.class);
        }
        return false;
    }

    /**
     * 删除
     * 
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    public boolean doDel(BondQuoteInfoDto bondQuoteInfoDto) {
        if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
            BussinessDictCodeList.报价方式_对话报价.getValue())) {
            return restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                             + "/CfetsBondDealQuotedPrice/deleteBondDialogueQuote?quoteId="
                                             + bondQuoteInfoDto.getQuoteId(),
                boolean.class);
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
            BussinessDictCodeList.报价方式_请求报价.getValue())) {
            return restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                             + "/CfetsBondDealQuotedPrice/deleteBondRequestQuote?quoteId="
                                             + bondQuoteInfoDto.getQuoteId(),
                boolean.class);
        }
        return false;
    }
    
    /**
     * 超时刷新机制
     * 
     * @param quoteId 报价编号
     * @see
     * @since 1.0
     */
    public void timeOutQuoteRefreshBond(String quoteId){
        restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
            + "/CfetsBondDealQuotedPrice/timeOutQuoteRefreshBond?quoteId=" + quoteId, Object.class);
    }

    /**
     * 现卷报价导入excel check
     *
     * @param flieName String 文件名称
     * @return List<BondQuoteImportPreviewDto> 检验结果
     * @throws Exception
     */
    public List<BondQuoteImportPreviewDto> importCheck(String flieName)
        throws Exception {
        List<BondQuoteImportPreviewDto> validationList = new ArrayList<>();

        if (StringUtil.isNotEmpty(flieName)) {
            ExcelUtil excelUtil = ExcelUtil.build(FileNameUtil.getUploadFilePath(flieName), null);
            List<List<String>> dataList = excelUtil.readToList(0, 0, 0);

            if (CollectionUtils.isNotEmpty(dataList) && dataList.size() > 1) {
                // 查询已接收的委托指令且指令的接收交易员为登录用户，且指令的完成度<100%
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("entrustStatus", "unComplete");
                List<CfetsBondDealEntrustOrderDto> entrustList = this.queryEntrustOrderList(param);

                // Excel中的汉字已经转换为编码（不能转换的除外）
                List<CfetsBondDealEntrustOrderDto> cbdeoList = compositeCbdeoList(dataList,
                    entrustList);

                // region 设置序号rowNumber并得到转换后的对象
                AtomicInteger rowNumber = new AtomicInteger(0);
                validationList.addAll(cbdeoList.stream().map(p -> {
                    BondQuoteImportPreviewDto previewDto = new BondQuoteImportPreviewDto();
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
                        ResponseEntity<String> respEntity = restTemplate.exchange(
                            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/bondQuoteListDuplicateCheck",
                            HttpMethod.POST, entity, respType);

                        if (respEntity != null) {
                            log.debug(respEntity.getBody());
                            validationList = JSONObject.parseObject(respEntity.getBody(),
                                new TypeReference<List<BondQuoteImportPreviewDto>>() {
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
                Map<String, BigDecimal> avaNumMap = new HashMap<>();
                validationList.forEach(p -> {
                    CfetsBondDealEntrustOrderDto cfetsBondDealEntrustOrderDto = null;
                    
                    // 判断委托代码是否为空
                    if (StringUtils.isBlank(p.getEntrustId())) {
                        setValidationMsg(p, "entrustId",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_007")));
                    }
                    else {
                        // 判断是否是已接收的委托指令且指令的接收交易员为登录用户，且指令的完成度<100%
                        if (entrustList.isEmpty()) {
                            setValidationMsg(p, "entrustId",
                                getMessage("QUOTE_UPLOAD_031", p.getEntrustId()));
                        }
                        else {
                            List<CfetsBondDealEntrustOrderDto> filterList = entrustList.stream().filter(
                                e -> StringUtil.equals(e.getEntrustId(),
                                    p.getEntrustId())).collect(Collectors.toList());
                            if (StringUtil.isEmptyList(filterList)) {
                                setValidationMsg(p, "entrustId",
                                    getMessage("QUOTE_UPLOAD_031", p.getEntrustId()));
                            }
                            else {
                                cfetsBondDealEntrustOrderDto = filterList.get(0);
                            }
                        }
                    }

                    // 判断组合代码需要验证是否与委托指令的组合代码一致
                    if (!StringUtil.isBlank(p.getPortfolioId())
                        && cfetsBondDealEntrustOrderDto != null) {
                        if (!StringUtil.equals(p.getPortfolioId(),
                            cfetsBondDealEntrustOrderDto.getPortfolioId())) {
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
                    }
                    else {
                        if (!BussinessDictCodeList.报价方式_对话报价.getValue().equals(p.getQuoteType())
                            && !BussinessDictCodeList.报价方式_请求报价.getValue().equals(
                                p.getQuoteType())) {
                            setValidationMsg(p, "quoteTypeStr",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_011")));
                        }
                    }

                    // 债券代码
                    if (StringUtils.isBlank(p.getFinprodMarketId())) {
                        setValidationMsg(p, "finprodMarketId",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_012")));
                    }
                    else {
                        if (cfetsBondDealEntrustOrderDto != null
                            && !StringUtil.equals(p.getFinprodMarketId(),
                                cfetsBondDealEntrustOrderDto.getFinprodMarketId())) {
                            setValidationMsg(p, "finprodMarketId", getMessage("QUOTE_UPLOAD_032",
                                this.getMessage("QUOTE_UPLOAD_012"), p.getFinprodMarketId()));
                        }
                        // Modified by JIAOXUJIN on 2020-06-15
                        else if (cfetsBondDealEntrustOrderDto != null) {
                            p.setFinprodId(cfetsBondDealEntrustOrderDto.getFinprodId());
                        }
                    }
                    
                    //债券代码和简称都不为空校验是否匹配 add by yzg 20210119 start
                    if(!StringUtil.isBlank(p.getFinprodId())) {
                    	FinProduct finProduct = finProductMapper.selectByPrimaryKey(p.getFinprodId());
                    	if(StringUtil.isNotEmpty(finProduct)) {
                    		if(!StringUtil.isBlank(p.getFinprodAbbr()) && !StringUtil.equals(finProduct.getFinprodAbbr(), p.getFinprodAbbr())) {
                    			setValidationMsg(p, "finprodMarketId",
                                        getMessage("QUOTE_UPLOAD_041", p.getFinprodMarketId(),p.getFinprodAbbr()));
                    		}
                    	}else {
                    		setValidationMsg(p, "finprodMarketId",
                                    getMessage("QUOTE_UPLOAD_037", p.getFinprodId()));
                    	}
                    }
                    //债券代码和简称都不为空校验是否匹配 add by yzg 20210119 end
                    // 交易方向
                    if (StringUtils.isBlank(p.getPs())) {
                        setValidationMsg(p, "psStr",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_014")));
                    }
                    else {
                        if (!BussinessDictCodeList.交易方向_买入.getValue().equals(p.getPs())
                            && !BussinessDictCodeList.交易方向_卖出.getValue().equals(p.getPs())) {
                            setValidationMsg(p, "psStr",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_014")));
                        }
                        else {
                            if (cfetsBondDealEntrustOrderDto != null
                                && !StringUtil.equals(p.getPs(),
                                    cfetsBondDealEntrustOrderDto.getPs())) {
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
                        if (cfetsBondDealEntrustOrderDto != null) {
                            if (!StringUtil.equals(p.getMySideAbbr(),
                                cfetsBondDealEntrustOrderDto.getMySideAbbr())) {
                                setValidationMsg(p, "mySideAbbr", getMessage("QUOTE_UPLOAD_033"));
                            }
                            else {
                                p.setMySide(cfetsBondDealEntrustOrderDto.getMySide());
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

                    // 券面(万)
                    if (p.getEntrustAmt() == null) {
                        setValidationMsg(p, "entrustAmt",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_019")));
                    }
                    else {
                        // 检索券面是否超委托的剩余可交易额度，超过则需要提示
                        if (cfetsBondDealEntrustOrderDto != null
                            && (cfetsBondDealEntrustOrderDto.getAvaNum() == null
                                || p.getEntrustAmt().compareTo(ArithmeticUtil.subtract(cfetsBondDealEntrustOrderDto.getAvaNum(),
                                    avaNumMap.get(p.getEntrustId()) != null ? avaNumMap.get(
                                        p.getEntrustId()) : BigDecimal.ZERO)) > 0)) {
                            // 如果开关是硬阻挡则在错误信息提示，如果开关是软阻挡在预警信息提示
                            // 是否硬阻挡
                            boolean _isHard = true;
                            if (StringUtil.equals(
                                CacheUtil.getSysParam("ibuFlag_bondOverEntrustAvaAmt"), "02")) {
                                _isHard = false;
                            }
                            setValidationMsg(p, "entrustAmt",
                                getMessage("QUOTE_UPLOAD_027", p.getEntrustId()), _isHard);
                        }
                        else {
                            // 券面需要为十万的整数倍（接口规则要求）
                        	if (p.getEntrustAmt().compareTo(BigDecimal.ZERO) <= 0) {
                        		setValidationMsg(p, "entrustAmt", "券面(万)必须大于0！");
                            }
                            if (StringUtil.equals(BussinessDictCodeList.报价方式_对话报价.getValue(),p.getQuoteType())) {
                                String numStr = ArithmeticUtil.multiply(p.getEntrustAmt(),BigDecimal.valueOf(10)).toPlainString();
                                // Modified by JIAOXUJIN on 2020-06-10
                                if (Double.parseDouble(numStr) % 1.0d > 0) {
                                    setValidationMsg(p, "entrustAmt",
                                            getMessage("QUOTE_UPLOAD_038_2", p.getEntrustAmt()));
                                }
                            }else {
                                String numStr = p.getEntrustAmt().toPlainString();
                                // Modified by JIAOXUJIN on 2020-06-10
                                if (Double.parseDouble(numStr) % 10.0d > 0) {
                                    setValidationMsg(p, "entrustAmt",
                                            getMessage("QUOTE_UPLOAD_038", p.getEntrustAmt()));
                                }
                            }
                        }
                        if (StringUtil.isNotEmpty(p.getEntrustId())) {
                            if (avaNumMap.containsKey(p.getEntrustId())) {
                                avaNumMap.put(p.getEntrustId(), ArithmeticUtil.doAdd(
                                    avaNumMap.get(p.getEntrustId()),
                                    p.getEntrustAmt() != null ? p.getEntrustAmt() : BigDecimal.ZERO));
                            }
                            else {
                                avaNumMap.put(p.getEntrustId(),
                                    p.getEntrustAmt() != null ? p.getEntrustAmt() : BigDecimal.ZERO);
                            }
                        }
                    }

                    // 净价、到期%、行权%
                    if (StringUtil.equals(BussinessDictCodeList.报价方式_对话报价.getValue(),
                        p.getQuoteType())) {
                        // 含权债
                        int count = 0;
                        //资产支持证券
                        if(cfetsBondDealEntrustOrderDto != null
                                && StringUtil.equals(cfetsBondDealEntrustOrderDto.getAssetTypeOne(), "B13")){
                        	if(p.getPrice() != null) {
                        		count++ ;
                                p.setCalculateField(BussinessDictCodeList.计算选择方式_净价.getValue());
                            }else {
                            	setValidationMsg(p, "price",
                                 getMessage("QUOTE_UPLOAD_042",
                                 getMessage("QUOTE_UPLOAD_020")));
                            }
                        }
                        //资产支持含权债
                        else if (cfetsBondDealEntrustOrderDto != null
                            && StringUtil.equals(cfetsBondDealEntrustOrderDto.getIsRight(), "Y")) {
                            if (p.getPrice() != null) {
                                count++ ;
                                p.setCalculateField(BussinessDictCodeList.计算选择方式_净价.getValue());
                            }
                            if (p.getMatureYield() != null) {
                                count++ ;
                                p.setCalculateField(BussinessDictCodeList.计算选择方式_到期收益率.getValue());
                            }
                            if (p.getExerYield() != null) {
                                count++ ;
                                p.setCalculateField(BussinessDictCodeList.计算选择方式_行权收益率.getValue());
                            }
                            if (count != 1) {
                                setValidationMsg(p, "exerYield", getMessage("QUOTE_UPLOAD_039"));
                            }
                        }
                        // 非含权债
                        else {
                            if (p.getPrice() != null) {
                                count++ ;
                                p.setCalculateField(BussinessDictCodeList.计算选择方式_净价.getValue());
                            }
                            if (p.getMatureYield() != null) {
                                count++ ;
                                p.setCalculateField(BussinessDictCodeList.计算选择方式_到期收益率.getValue());
                            }
                            if (count != 1) {
                                setValidationMsg(p, "price", getMessage("QUOTE_UPLOAD_040"));
                            }
                        }

                        if (count == 1) {
                            if (cfetsBondDealEntrustOrderDto != null) {
                                // 是否硬阻挡
                                boolean _isBondOverEntrustPriceHard = true;
                                if (StringUtil.equals(
                                    CacheUtil.getSysParam("ibuFlag_bondOverEntrustPrice"), "02")) {
                                    _isBondOverEntrustPriceHard = false;
                                }
                                if (StringUtil.equals(cfetsBondDealEntrustOrderDto.getPs(),
                                    BussinessDictCodeList.交易方向_买入.getValue())) {
                                    if (p.getPrice() != null
                                        && cfetsBondDealEntrustOrderDto.getPrice() != null) {
                                        if (p.getPrice().compareTo(
                                            cfetsBondDealEntrustOrderDto.getPrice()) > 0) {
                                            setValidationMsg(p, "price",
                                                getMessage("CFETS_COMMON_E_014"),
                                                _isBondOverEntrustPriceHard);
                                        }
                                    }
                                    else if (cfetsBondDealEntrustOrderDto.getMatureYield() != null
                                             && p.getMatureYield() != null) {
                                        if (p.getMatureYield().compareTo(
                                            cfetsBondDealEntrustOrderDto.getMatureYield()) < 0) {
                                            setValidationMsg(p, "matureYield",
                                                getMessage("CFETS_COMMON_E_015"),
                                                _isBondOverEntrustPriceHard);
                                        }
                                    }
                                    else if (cfetsBondDealEntrustOrderDto.getExerYield() != null
                                             && p.getExerYield() != null) {
                                        if (p.getExerYield().compareTo(
                                            cfetsBondDealEntrustOrderDto.getExerYield()) < 0) {
                                            setValidationMsg(p, "exerYield",
                                                getMessage("CFETS_COMMON_E_016"),
                                                _isBondOverEntrustPriceHard);
                                        }
                                    }
                                }
                                else if (StringUtil.equals(cfetsBondDealEntrustOrderDto.getPs(),
                                    BussinessDictCodeList.交易方向_卖出.getValue())) {
                                    if (cfetsBondDealEntrustOrderDto.getPrice() != null
                                        && p.getPrice() != null) {
                                        if (p.getPrice().compareTo(
                                            cfetsBondDealEntrustOrderDto.getPrice()) < 0) {
                                            setValidationMsg(p, "price",
                                                getMessage("CFETS_COMMON_E_017"),
                                                _isBondOverEntrustPriceHard);
                                        }
                                    }
                                    else if (cfetsBondDealEntrustOrderDto.getMatureYield() != null
                                             && p.getMatureYield() != null) {
                                        if (p.getMatureYield().compareTo(
                                            cfetsBondDealEntrustOrderDto.getMatureYield()) > 0) {
                                            setValidationMsg(p, "matureYield",
                                                getMessage("CFETS_COMMON_E_018"),
                                                _isBondOverEntrustPriceHard);
                                        }
                                    }
                                    else if (cfetsBondDealEntrustOrderDto.getExerYield() != null
                                             && p.getExerYield() != null) {
                                        if (p.getExerYield().compareTo(
                                            cfetsBondDealEntrustOrderDto.getExerYield()) > 0) {
                                            setValidationMsg(p, "exerYield",
                                                getMessage("CFETS_COMMON_E_019"),
                                                _isBondOverEntrustPriceHard);
                                        }
                                    }
                                }
                            }
                        }
                        //金额计算
                        if(StringUtil.isNotEmpty(p.getEntrustId()) && cfetsBondDealEntrustOrderDto!= null && StringUtil.isNotEmpty(p.getFinprodId())){
                        	CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.queryEntrustInfoByEntrustId(p.getEntrustId());
                            cfetsBondDealDialogueQuoteDto.setFinprodId("F01"+"_"+p.getFinprodMarketId());
                            if(StringUtil.isNotEmpty(p.getEntrustAmt())) {
                            	cfetsBondDealDialogueQuoteDto.setAvaNum(p.getEntrustAmt().multiply(new BigDecimal(10000)));
                            }else {
                            	cfetsBondDealDialogueQuoteDto.setAvaNum(new BigDecimal(0));
                            }
                            cfetsBondDealDialogueQuoteDto.setSettleDate(iPlatformDate.getSysDate());
                            if(p.getPrice() != null){
                            	cfetsBondDealDialogueQuoteDto.setPrice(p.getPrice());
                            	//cfetsBondDealDialogueQuoteDto.setCalculateField(BussinessDictCodeList.计算选择方式_净价.getValue());
                            } else {
                            	cfetsBondDealDialogueQuoteDto.setPrice(new BigDecimal(0.0000));
                            }
                            if(p.getMatureYield() != null){
                            	cfetsBondDealDialogueQuoteDto.setMatureYield(p.getMatureYield());
                            	//cfetsBondDealDialogueQuoteDto.setCalculateField(BussinessDictCodeList.计算选择方式_到期收益率.getValue());
                            } else {
                            	cfetsBondDealDialogueQuoteDto.setMatureYield(new BigDecimal(0.0000));
                            }
                            if (p.getExerYield() != null) {
                            	cfetsBondDealDialogueQuoteDto.setExerYield(p.getExerYield());
                            	//cfetsBondDealDialogueQuoteDto.setCalculateField(BussinessDictCodeList.计算选择方式_行权收益率.getValue());
							} else {
								cfetsBondDealDialogueQuoteDto.setExerYield(null);
							}
                            cfetsBondDealDialogueQuoteDto.setCalculateField(p.getCalculateField());
                            AjaxResult ajaxResult = cfetsBondDealQuotedPrice002Service.calcAmt(cfetsBondDealDialogueQuoteDto);
                            if(StringUtil.isNotEmpty(ajaxResult.getData())){
                            	CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto1 = (CfetsBondDealDialogueQuoteDto) ajaxResult.getData();
                            	cfetsBondDealDialogueQuoteDto1.setCounterTrader(p.getCounterTrader());
                            	cfetsBondDealDialogueQuoteDto1.setCounterTraderName(p.getCounterTraderName());
                            	if(StringUtil.isNotEmpty(p.getMySide())) {
                            		cfetsBondDealDialogueQuoteDto1.setMySide(p.getMySide());
                            	}
                            	if(StringUtil.isNotEmpty(p.getMySideTrader())) {
                            		cfetsBondDealDialogueQuoteDto1.setMySideTrader(p.getMySideTrader());
                            	}
                            	if(StringUtil.isNotEmpty(p.getMySideTraderName())) {
                            		cfetsBondDealDialogueQuoteDto1.setMySideTraderName(p.getMySideTraderName());
                            	}
                            	cfetsBondDealDialogueQuoteDto1.setPortfolioId(p.getPortfolioId());
                            	cfetsBondDealDialogueQuoteDto1.setPortfolioName(p.getPortfolioName());
                            	cfetsBondDealDialogueQuoteDto1.setFinprodAbbr(p.getFinprodAbbr());
                            	cfetsBondDealDialogueQuoteDto1.setSettleSpreed(p.getSettleSpreed());
                            	cfetsBondDealDialogueQuoteDto1.setSettleMethod(p.getSettleMethod());
                            	cfetsBondDealDialogueQuoteDto1.setSettleType(p.getSettleType());
                            	cfetsBondDealDialogueQuoteDto1.setCounterId(p.getCounterId());
                            	cfetsBondDealDialogueQuoteDto1.setCounterName(p.getCounterName());
                            	BeanUtil.copyPropertiesIgnoreNull(p, cfetsBondDealDialogueQuoteDto1);
                            	if(!StringUtil.equals(cfetsBondDealEntrustOrderDto.getIsRight(), "Y")){
                            		p.setExerYield(null);
                            	}
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

                    // 结算方式
                    if (StringUtils.isNotBlank(p.getSettleMethod())) {
                        if (!BussinessDictCodeList.结算方式_见券付款.getValue().equals(p.getSettleMethod())
                            && !BussinessDictCodeList.结算方式_见款付券.getValue().equals(
                                p.getSettleMethod())
                            && !BussinessDictCodeList.结算方式_券款对付.getValue().equals(
                                p.getSettleMethod())) {
                            setValidationMsg(p, "settleMethodStr",
                                getMessage("QUOTE_UPLOAD_029", getMessage("QUOTE_UPLOAD_024")));
                        }
                    }
                    else {
                        setValidationMsg(p, "settleMethodStr",
                            getMessage("QUOTE_UPLOAD_026", getMessage("QUOTE_UPLOAD_024")));
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
                });
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
     * 在预览数据的顶部插入一条错误消息数据
     * 
     * @param validationList List<BondQuoteImportPreviewDto>
     * @param errMessage String
     */
    private void setErrorMsgOnTop(List<BondQuoteImportPreviewDto> validationList,
                                  String errMessage) {
        // 改变原有列表中的rowNumber属性
        if (CollectionUtils.isNotEmpty(validationList)) {
            validationList.forEach(p -> {
                p.setRowNumber(p.getRowNumber() + 1);
            });
        }

        BondQuoteImportPreviewDto validDto = new BondQuoteImportPreviewDto();
        validDto.setValidationErrorMsg(errMessage);
        validDto.setRowNumber(1);
        validationList.add(0, validDto);
    }

    private void setValidationMsg(BondQuoteImportPreviewDto dto, String fieldName,
                                  String message) {
        setValidationMsg(dto, fieldName, message, true);
    }

    private void setValidationMsg(BondQuoteImportPreviewDto dto, String fieldName, String message,
                                  boolean isHard) {
        dto.addValidFieldName(fieldName);
        if (isHard) {
            if (StringUtils.isNotBlank(dto.getValidationErrorMsg())) {
                dto.setValidationErrorMsg(dto.getValidationErrorMsg() + "\n" + message);
            }
            else {
                dto.setValidationErrorMsg(message);
            }
        }
        else {
            if (StringUtils.isNotBlank(dto.getValidationWarnMsg())) {
                dto.setValidationWarnMsg(dto.getValidationWarnMsg() + "\n" + message);
            }
            else {
                dto.setValidationWarnMsg(message);
            }
        }
    }

    /**
     * 现卷报价导入excel功能
     *
     * @param dataRows List<BondQuoteImportPreviewDto>
     * @return List<String> 错误消息
     * @throws Exception
     */
    public List<String> importData(List<BondQuoteImportPreviewDto> dataRows)
        throws Exception {
        // 记录错误信息
        List<String> errorMsgList = new ArrayList<String>();

        if (CollectionUtils.isNotEmpty(dataRows)) {
            dataRows.forEach(x -> {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                if (StringUtil.equals(x.getQuoteType(),
                    BussinessDictCodeList.报价方式_对话报价.getValue())) {
                    codeParameter.setSeqNo("IBU_001");
                }
                else {
                    codeParameter.setSeqNo("IBU_002");
                }
                x.setQuoteId(bussinessIdGetter.buildBussinessCode(codeParameter));
            });

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("inputList", dataRows);
            paramMap.put("userInfo", SecurityUtil.getLoginUser());
            String requestJson = JSON.toJSONString(paramMap);
            log.debug("requestJson is: {}", requestJson);

            QuoteBatchResult batchResult = new QuoteBatchResult();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
            HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
            ParameterizedTypeReference<String> respType = new ParameterizedTypeReference<String>() {
            };

            try {
                // 调用组件新增发送，要么返回全部成功，要么全部失败。
                ResponseEntity<String> respEntity = restTemplate.exchange(
                    ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/batchNewBondDialogueQuote/"
                                                                          + String.valueOf(
                                                                              _needDuplicateCheck),
                    HttpMethod.POST, entity, respType);

                if (respEntity != null) {
                    log.debug(respEntity.getBody());
                    batchResult = JSONObject.parseObject(respEntity.getBody(),
                        QuoteBatchResult.class);

                    if (batchResult.getSuccessAccout() <= 0) {
                        errorMsgList.addAll(batchResult.getErrMsgList());
                    }
                    else {
                        int rowNumber = 0;
                        int rowSize = dataRows.size();
                        User curentUser = SecurityUtil.getLoginUser();
                        
                        // 异步线程：逐笔报价进行调用发送接口
                        AtomicBoolean isAllSuccess = new AtomicBoolean(true);
                        AtomicBoolean isSendFinish = new AtomicBoolean(false);
                        AtomicInteger sendCount = new AtomicInteger(0);

                        for (BondQuoteImportPreviewDto dto : dataRows) {
                            rowNumber++;
                            dto.setRowNumber(rowNumber);

                            // 逐条异步发送 (搞不定，有时restTemplate就是请求不到微服务端)
                                new SendBondQuoteTask(restTemplate, dto, isAllSuccess,
                                    isSendFinish, sendCount, curentUser, rowSize).run();
                        }
                    }
                }
                else {
                    errorMsgList.add(getMessage("QUOTE_UPLOAD_002"));
                }
            }
            catch (RestClientException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                errorMsgList.add(e.getMessage());
            }
        }
        else {
            errorMsgList.add(getMessage("QUOTE_UPLOAD_001"));
        }

        if (CollectionUtils.isNotEmpty(errorMsgList)) {
            return errorMsgList;
        }

        return null;
    }

    /**
     * 调用微服务端现劵报价发送方法<br />
     * 发送成功返回true，发送失败返回false
     */
    private boolean sendBondQuote(CfetsBondDealEntrustOrderDto dto, 
                                  JoyinRestTemplate restTemplate, User user) {
        boolean result = true;

        String requestJson = JSON.toJSONString(dto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));

        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ParameterizedTypeReference<String> respType = new ParameterizedTypeReference<String>() {};
        ResponseEntity<String> respEntity = restTemplate.exchange(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + 
            "/CfetsBondDealQuotedPrice/convertToQuoteResultDto/" + 
            String.valueOf(_needDuplicateCheck), HttpMethod.POST, entity, respType);

        if (respEntity != null) {
            String quoteResultDtoJson = respEntity.getBody();

            // 将这个json传到微服务端接口
            log.debug("quoteResultDtoJson is: " + quoteResultDtoJson);

            // 调用微服务端现劵报价发送方法
            // 发送成功返回true，发送失败返回false
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("quoteResultDtoJson", quoteResultDtoJson);
            paramMap.put("userInfo", user);

            HttpEntity<String> gatewayEntity = new HttpEntity<String>(JSON.toJSONString(paramMap), headers);
            ParameterizedTypeReference<Boolean> gatewayRespType = new ParameterizedTypeReference<Boolean>() {};
            ResponseEntity<Boolean> gatewayRespEntity = restTemplate.exchange(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + 
                "/CfetsBondDealQuotedPrice/sendGateWayHandle", HttpMethod.POST, gatewayEntity, gatewayRespType);
            if (gatewayRespEntity != null) {
                result = Boolean.valueOf(gatewayRespEntity.getBody());
            }
        }
        else {
            log.warn("respEntity in sendBondQuote() is empty.");
            result = false;
        }
        return result;
    }

    /**
     * 将Excel中的数据解析组装成List<CfetsBondDealEntrustOrderDto>
     */
    private List<CfetsBondDealEntrustOrderDto> compositeCbdeoList(List<List<String>> dataList,
                                                                  List<CfetsBondDealEntrustOrderDto> entrustList) {
        List<CfetsBondDealEntrustOrderDto> cbdeoList = new ArrayList<>();
        CfetsBondDealEntrustOrderDto itemDto;

        for (List<String> rowList : dataList.subList(1, dataList.size())) {
            itemDto = new CfetsBondDealEntrustOrderDto();

            itemDto.setEntrustId(StringUtils.stripToNull(rowList.get(0)));
            itemDto.setInvManagerName(StringUtils.stripToNull(rowList.get(1)));
            itemDto.setPortfolioId(StringUtils.stripToNull(rowList.get(2)));
            itemDto.setPortfolioName(StringUtils.stripToNull(rowList.get(3)));

            if (StringUtils.isNotBlank(rowList.get(4))) {
                if (BussinessDictCodeList.报价方式_对话报价.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(4)))) {
                    itemDto.setQuoteType(BussinessDictCodeList.报价方式_对话报价.getValue());
                }
                else if (BussinessDictCodeList.报价方式_请求报价.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(4)))) {
                    itemDto.setQuoteType(BussinessDictCodeList.报价方式_请求报价.getValue());
                }
                else {
                    itemDto.setQuoteType(StringUtils.stripToNull(rowList.get(4)));
                }
            }

            itemDto.setFinprodMarketId(StringUtils.stripToNull(rowList.get(5)));
            itemDto.setFinprodAbbr(StringUtils.stripToNull(rowList.get(6)));

            if (StringUtils.isNotBlank(rowList.get(7))) {
                if (BussinessDictCodeList.交易方向_买入.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(7)))) {
                    itemDto.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                }
                else if (BussinessDictCodeList.交易方向_卖出.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(7)))) {
                    itemDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                }
                else {
                    itemDto.setPs(StringUtils.stripToNull(rowList.get(7)));
                }
            }

            itemDto.setMySideAbbr(StringUtils.stripToNull(rowList.get(8)));
            itemDto.setMySideTraderName(StringUtils.stripToNull(rowList.get(9)));
            itemDto.setCounterName(StringUtils.stripToNull(rowList.get(10)));
            itemDto.setCounterTraderName(StringUtils.stripToNull(rowList.get(11)));
            itemDto.setEntrustAmt(getDecimal(rowList.get(12)));
            if (!StringUtil.equals(itemDto.getQuoteType(),
                BussinessDictCodeList.报价方式_请求报价.getValue())) {
                itemDto.setPrice(getDecimal(rowList.get(13)));
                itemDto.setMatureYield(getDecimal(rowList.get(14)));
                itemDto.setExerYield(getDecimal(rowList.get(15)));
            }

            if (StringUtils.isNotBlank(rowList.get(16))) {
                if (BussinessDictCodeList.清算速度_T加0.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(16)))) {
                    itemDto.setSettleSpreed(BussinessDictCodeList.清算速度_T加0.getValue());
                }
                else if (BussinessDictCodeList.清算速度_T加1.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(16)))) {
                    itemDto.setSettleSpreed(BussinessDictCodeList.清算速度_T加1.getValue());
                }
                else {
                    itemDto.setSettleSpreed(StringUtils.stripToNull(rowList.get(16)));
                }
            }
            if (StringUtils.isNotBlank(rowList.get(17))) {
                if (BussinessDictCodeList.结算方式_见券付款.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(17)))) {
                    itemDto.setSettleMethod(BussinessDictCodeList.结算方式_见券付款.getValue());
                }
                else if (BussinessDictCodeList.结算方式_见款付券.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(17)))) {
                    itemDto.setSettleMethod(BussinessDictCodeList.结算方式_见款付券.getValue());
                }
                else if (BussinessDictCodeList.结算方式_券款对付.getCodeName().equals(
                    StringUtils.stripToNull(rowList.get(17)))) {
                    itemDto.setSettleMethod(BussinessDictCodeList.结算方式_券款对付.getValue());
                }
                else {
                    itemDto.setSettleMethod(StringUtils.stripToNull(rowList.get(17)));
                }
            }
            if (StringUtils.isNotBlank(rowList.get(18))) {
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
            itemDto.setSendStatus(BussinessDictCodeList.银行间上行发送状态_待发送.getValue());

            cbdeoList.add(itemDto);
        }

        return cbdeoList;
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
     * 发送现卷报价任务类<br />
     * Added by JIAOXUJIN on 2020-06-18
     */
    class SendBondQuoteTask implements Runnable {
        JoyinRestTemplate restTemplate;
        CfetsBondDealEntrustOrderDto dto;
        AtomicBoolean isAllSuccess;
        AtomicBoolean isSendFinish;
        AtomicInteger sendCount;
        User curentUser;
        int rows;

        public SendBondQuoteTask(JoyinRestTemplate restTemplate, CfetsBondDealEntrustOrderDto dto,
                                 AtomicBoolean isAllSuccess, AtomicBoolean isSendFinish,
                                 AtomicInteger sendCount, User curentUser, int rows) {
            this.restTemplate = restTemplate;
            this.dto = dto;
            this.isAllSuccess = isAllSuccess;
            this.isSendFinish = isSendFinish;
            this.sendCount = sendCount;
            this.curentUser = curentUser;
            this.rows = rows;
        }

        @Override
        public void run() {
            if (isAllSuccess.get()) {
                boolean sendResult = sendBondQuote(dto, restTemplate, curentUser);
                isAllSuccess.set(sendResult);

                // 将发送结果推送到界面，提示具体的行数错误
                socket.sendToUser(curentUser.getUserName(),
                    "/bondQuoteImport/sendResult",
                    "{\"rowNumber\": " + dto.getRowNumber() +", \"sendResult\": " + sendResult + "}");

                // 发送时，如果出现接口发送异常，后续报价不继续发送
                // 全部发送完成后，推送消息到界面，告知发送完成，关闭导入预览界面
                if (!isSendFinish.get() && (sendCount.addAndGet(1) == rows || !sendResult)) {
                    isSendFinish.set(true);
                    socket.sendToUser(curentUser.getUserName(),
                        "/bondQuoteImport/sendFinish",
                        "{\"sendCount\": " + sendCount.get() + "}");
                }
            }
        }
    }


    /**
     * 成交查询
     *
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public List<CstpSelectDto> gencjcxTable(Map<String, Object> param) {
        if(param.get("portfolioId") != null && StringUtil.isNotEmpty(param.get("portfolioId").toString())){
            String[] portfolioId = param.get("portfolioId").toString().split(",");
            param.replace("portfolioId", portfolioId);
        }else{
            param.replace("portfolioId", null);
        }
        if(param.get("matchStatus") != null && StringUtil.isNotEmpty(param.get("matchStatus").toString())){
            String[] matchStatus = param.get("matchStatus").toString().split(",");
            param.replace("matchStatus", matchStatus);
        }else{
            param.replace("matchStatus", null);
        }
        if(param.get("tradeDateBegin") != null && !param.get("tradeDateBegin").toString().isEmpty()){
            param.replace("tradeDateBegin",param.get("tradeDateBegin").toString().replace("-", ""));
            param.replace("tradeDateEnd",param.get("tradeDateEnd").toString().replace("-", ""));
        }else{
            param.replace("tradeDateBegin",null);
            param.replace("tradeDateEnd",null);
        }
        param.put("IS_CANCEL", BussinessDictCodeList.是否_否.getValue());
        param.put("MATCH", BussinessDictCodeList.匹配状态_已匹配.getValue());
        param.put("UNMATCH", BussinessDictCodeList.匹配状态_未匹配.getValue());
        String keywords = "";
        if(param.containsKey("keywords")){
            keywords =(String) param.get("keywords");
            param.remove("keywords");
        }
        param.put("loginUserId", LoginUserUtil.getUserName());
        List<CstpSelectDto>list = restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/getCstpQryTableList",
                param, CstpSelectDto.class);
        if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(list)){
            list = (List<CstpSelectDto>) getKeyWordsList(list,keywords,(String) param.get("keycols"));
        }
        return list;
    }


    /**
     * 查询债券行情信息
     * @param pageParam
     * @return
     */
    public Map<String, Object> findQuotations(Map<String, Object> pageParam) {
        String secCode = (String) pageParam.get("seccode");
        String url = MarketInfoInterf_cmdsUrl+"/rest/markinfo/sec/getMarkinfoBySecCode?seccode=" + secCode;
        return getMarketInfo(url);
    }

    /**
     * 请求市场行情数据接口
     * @param url
     * @return
     */
    public Map<String,Object> getMarketInfo(String url){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000);
        factory.setReadTimeout(1000);
        RestTemplate template = new RestTemplate(factory);
        try {
            Map<String, Object> quoteMap = template.getForObject(url, Map.class);
            return quoteMap;
        } catch (Exception e) {
            return null;
        }
    }

}
