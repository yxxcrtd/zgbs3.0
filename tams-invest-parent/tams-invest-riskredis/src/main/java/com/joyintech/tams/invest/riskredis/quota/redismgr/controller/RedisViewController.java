/*
 * 文件名：RedisViewController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年9月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.riskredis.quota.redismgr.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.annotation.SubPageId;
import com.joyintech.framework.base.BaseDictCode;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.intf.*;
import com.joyintech.framework.base.service.DealDictCodeDataService;
import com.joyintech.framework.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.ButtonPurview;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.security.data.DataAuthorityService;
import com.joyintech.framework.security.data.GridColumnAuthInfo;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.core.data.RedisHash;
import com.joyintech.redis.view.controller.RedisBaseController;
import com.joyintech.redis.view.interf.RedisSearch;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.riskinfrds.base.service.RedisBaseService;
import com.joyintech.tams.invest.common.riskinfrds.calendar.service.CalendarService;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.EventMap;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEvent;
import com.joyintech.tams.invest.common.riskinfrds.event.service.RskEventService;
import com.joyintech.tams.invest.common.riskinfrds.indic.service.IndicatorService;
import com.joyintech.tams.invest.common.riskinfrds.indic.service.IndicatorTrendService;
import com.joyintech.tams.invest.common.riskinfrds.info.service.*;
import com.joyintech.tams.invest.common.riskinfrds.occupy.service.OccupyRelParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamValService;
import com.joyintech.tams.invest.common.riskinfrds.prod.service.PtlIndicatorService;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.service.RskSupervisionIndicator001Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * redis管理画面
 * 
 * @author zhangrui1
 * @version 1.0 2019年9月9日
 * @see RedisViewController
 * @since 1.0
 */
@Controller
@RequestMapping("/invest/riskredis/quota/redismgr/RedisView")
@PageId("RedisView")
public class RedisViewController extends RedisBaseController {

    public static final String RDS_KEY = "rdsKey";

    public static final String RDS_FIELD = "rdsField";

    /**
     * 风险前置服务
     */
    @Autowired
    private RskEventService eventSrc;

    /**
     * 指标信息Redis读写服务
     */
    @Autowired
    private IndicatorService indSrc;

    /**
     * 预占/实占参数信息服务
     */
    @Autowired
    private OccupyRelParamService ocpSrc;

    /**
     * 指标参数信息Redis读写信息服务
     */
    @Autowired
    private IndicatorParamService paraSrc;

    /**
     * 指标参数值读写服务
     */
    @Autowired
    private IndicatorParamValService valSrc;

    /**
     * 指标关联组合信息读写服务
     */
    @Autowired
    private PtlIndicatorService ptlSrc;

    /**
     * 产品基本信息更新Redis服务
     */
    @Autowired
    private ProdInfoRedisService prodInfoSrc;

    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private AssetInfoRedisService assetInfoSrc;
    
    /**
     * 资产索引
     */
    @Autowired
    private AssetIndexService assetIndexSrc;
    
    /**
     * 汇率行情
     */
    @Autowired
    private ExchargeInfoService exchargeSrc;
    
    /**
     * 基金估值行情
     */
    @Autowired
    private FundValuationService fundValSrc;
    
    /**
     * 节假日信息
     */
    @Autowired
    private CalendarService calendarSrc;

    // add by zhangrui1 start
    /**
     * 资产实时规模信息
     */
    @Autowired
    private ProdAssetAmountService prodAssetAmtSrc;
    // add by zhangrui1 end
    // add by zhangrui1 20200814 start
    @Autowired
    private IndicatorTrendService indTrendSrc;
    // add by zhangrui1 20200814 end

    private String pageId;
    private String subPageId;
    private com.joyintech.framework.base.intf.Page page;
    private String pageModel;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    /**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisWriter;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(
            required = false
    )
    private DataAuthorityService dataAuthorityService;
    @Autowired(
            required = false
    )
    private DealDictCodeDataService dealDictCodeDataService;

    @Autowired(required = false)
    private RskSupervisionIndicator001Service rskSupervisionIndicator001Service;

    @ModelAttribute
    protected void initCommonParams() {
        if (StringUtils.isEmpty(this.pageId)) {
            PageId anno = (PageId)this.getClass().getAnnotation(PageId.class);
            if (null != anno) {
                this.pageId = anno.value();
            }
        }

        if (StringUtils.isEmpty(this.subPageId)) {
            SubPageId anno = (SubPageId)this.getClass().getAnnotation(SubPageId.class);
            if (null != anno) {
                this.subPageId = anno.value();
            } else {
                this.subPageId = "999999";
            }
        }
        this.page = CacheUtil.getPage(this.pageId);

        // add by etliu 20210925 start
        if (page != null) {
            this.request.setAttribute("localNeedWorkflow", page.getIsNeedWorkflow());
        }
        // add by etliu 20210925 end

        this.pageModel = StringUtil.getOrElse(this.getParam("pageModel"), "0");
        this.request.setAttribute("pageId", this.pageId);
        this.request.setAttribute("pageModelReference", "2");
        this.request.setAttribute("pageModelEdit", "1");
        this.request.setAttribute("pageModelNew", "0");
        this.request.setAttribute("dateSelectBtnCtr", CacheUtil.getSysParam("date_select_btn_ctr"));
        this.request.setAttribute("dateSelectAllowSingle", CacheUtil.getSysParam("date_select_allow_single"));
        this.request.setAttribute("filterAreaShow", CacheUtil.getSysParam("filter_area_show"));
        this.request.setAttribute("pageModel", this.pageModel);
        this.request.setAttribute("showBtnIcon", CacheUtil.getSysParam("is_show_btn_icon"));
        String INPUT_SELECT_IGNORECASE = CacheUtil.getSysParam("input_select_ignorecase");
        if (StringUtil.isEmpty(INPUT_SELECT_IGNORECASE)) {
            INPUT_SELECT_IGNORECASE = "03";
        }

        this.request.setAttribute("input_select_ignorecase", INPUT_SELECT_IGNORECASE);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("页面ID: {}", this.pageId);
        }

        User user = SecurityUtil.getLoginUser();
        if (null == user) {
            this.request.setAttribute("ElementsControl", "[]");
        } else {
            List<String> elementShowIds = new ArrayList();
            List<ElementControl> elementRequiredIds = new ArrayList();
            List<String> elementHoldIds = new ArrayList();
            if (null != this.page) {
                Collection<ElementControl> elements = this.page.getElementcontrols();
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(elements)) {
                    Iterator var7 = elements.iterator();

                    while(var7.hasNext()) {
                        ElementControl ele = (ElementControl)var7.next();
                        if (!ele.getIsShow()) {
                            elementShowIds.add(ele.getElementId());
                        } else {
                            elementRequiredIds.add(ele);
                        }

                        if (ele.getIsHold()) {
                            elementHoldIds.add(ele.getElementId());
                        }
                    }
                }

                this.request.setAttribute("ElementsControl", JsonUtils.object2Json(elementShowIds));
                this.request.setAttribute("ElementsRequired", JsonUtils.object2Json(elementRequiredIds));
                this.request.setAttribute("ElementsHold", JsonUtils.object2Json(elementHoldIds));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @RequestMapping("init")
    public String init(Model model) {
        this.setSelectOptionToModel(model, new BussinessDictCodeRds[] {BussinessDictCodeRds.共通业务类型,
                BussinessDictCodeRds.REDIS模块
        });
        //查询指标参数
        List<SelectOptionDto> mapZbcs = rskSupervisionIndicator001Service.getIndicatorParamList();
        model.addAttribute("INDICATOR_PARAM",mapZbcs);
        model.addAttribute("REDIS_MODEL_PARAMVAL", BussinessDictCodeListRds.REDIS模块_指标参数值.getValue());
        model.addAttribute("REDIS_MODEL_EVENT", RedisKeyEnum.风险事件配置.key());
        model.addAttribute("REDIS_MODEL_PARAM_CONFIG", RedisKeyEnum.指标参数配置.key());
        Assert.notNull(this.page, "[" + this.pageId + "]对应的当前画面在数据库中未配置， 请重写getPagePath或init方法");
        return this.page.getFile();
    }

    @ResponseBody
    @RequestMapping("selectForPage")
    public Page<?> selectForPage() {
        Map<String, Object> param = RequestUtil.getPageParam();
        String key = (String)param.get(RDS_KEY);
        if (StringUtil.isEmpty(key)) {
            return new Page<>();
        }
        String fields = (String)param.get(RDS_FIELD);
        String[] keys = StringUtil.split(key, GlobalConstant.COMMA);
        RedisBaseService<?> baseService = getRedisBaseService(key);
        List<String> fldList = new ArrayList<>();
        if (StringUtil.contains(fields, GlobalConstant.COMMA)) {
            fldList = StringUtil.splitToList(GlobalConstant.COMMA, fields);
        }
        else if (null != baseService && !isRedisKey(key, RedisKeyEnum.风险事件配置)) {
            fldList = baseService.allHKeys(key, fields);
        }
        else {
            for (String k : keys) {
                fldList.addAll(valSrc.allHKeys(k, fields));
            }
        }
        if (StringUtil.isEmptyList(fldList) && StringUtil.isNotEmpty(fields)) {
            fldList.add(fields);
        }
        List<String> fieldList = fldList;
        if (baseService != null) {
            if (isRedisKey(key, RedisKeyEnum.风险事件配置)) {
                if (StringUtil.isEmptyList(fieldList)) {
                    return selectForPage(() -> changeToStrMap(
                        changeEventMap(baseService.readAll(Arrays.asList(keys)))));
                }
                return selectForPage(() -> changeToEventMapWithCond(fieldList));
            }
            else {
                if (StringUtil.isEmptyList(fieldList)) {
                    return selectForPage(
                        () -> changeToStrMap(baseService.readAll(Arrays.asList(keys))));
                }
                Map<String, Map<String, String>> listMap = new HashMap<>();
                listMap.put(key, changeToStrMap2(baseService.getMapFromRedis(key, fieldList)));
                return selectForPage(() -> listMap);
            }
        }
        if (!StringUtil.isEmptyList(fieldList)) {
            Map<String, Map<String, IValue<Object>>> paraValList = new HashMap<>();
            for (String k : keys) {
                Map<String, IValue<Object>> map = new HashMap<>();
                if (!StringUtil.contains(fields, GlobalConstant.COMMA)) {
                    fieldList.forEach(f -> map.put(f, null));
                }
                else {
                    map.put(fields, null);
                }
                paraValList.put(k, map);
            }
            return selectForPage(() -> changeToNumMap(valSrc.getParamVal(paraValList)));
        }
        return selectForPage(() -> changeToNumMap(valSrc.readAll(Arrays.asList(keys))));
    }

    //重载父类中的方法
    public <T> Page<RedisHash<String, String, T>> selectForPage(RedisSearch<T> search) {
        List<RedisHash<String, String, T>> resList = this.mapToList(search.doSearch());
        if (StringUtil.isEmptyList(resList)) {
            return new Page();
        } else {
            sort(resList);
            String pageNumStr = RequestUtil.getParam("page");
            pageNumStr = StringUtil.getOrElse(pageNumStr, "1");
            int pageNum = Integer.parseInt(pageNumStr);
            String pageSizeStr = RequestUtil.getParam("length");
            pageSizeStr = StringUtil.getOrElse(pageSizeStr, "20");
            int pageSize = Integer.parseInt(pageSizeStr);
            com.github.pagehelper.Page<RedisHash<String, String, T>> page = new com.github.pagehelper.Page(pageNum, pageSize);
            int size = resList.size();
            page.setTotal((long)size);
            int start = (pageNum - 1) * pageSize;
            if(size<start){
              start=0;
            }
            int end = start + pageSize;
            page.addAll(resList.subList(start, end >= size ? size : end));
            return new Page(page);
        }
    }

    private Map<String, Map<String, String>> changeToEventMapWithCond(List<String> fieldList) {
        Map<String, Map<String, String>> listMap = new HashMap<>();
        EventMap map = eventSrc.getInfoFromRedis(RedisKeyEnum.风险事件配置.key(), EventMap.ALL);
        Map<String, IEvent> eMap = map.getEventMap();
        if (!CollectionUtils.isEmpty(eMap)) {
            Map<String, String> resMap = new HashMap<>();
            eMap.entrySet().forEach(en -> {
                String field = en.getKey();
                if (fieldList.contains(field)) {
                    resMap.put(field, JsonUtils.object2Json(en.getValue()));
                }
                else {
                    fieldList.forEach(f -> {
                        if (StringUtil.contains(field, f)) {
                            resMap.put(field, JsonUtils.object2Json(en.getValue()));
                        }
                    });
                }
            });
            listMap.put(RedisKeyEnum.风险事件配置.key(), resMap);
        }
        return listMap;
    }

    private <T> Map<String, String> changeToStrMap2(Map<String, T> map) {
        Map<String, String> resMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(map)) {
            map.entrySet().forEach(en -> {
                String field = en.getKey();
                T obj = en.getValue();
                if (obj != null) {
                    // modify by etliu 20211115 start
                    // GZYHTZJY-368：【产品问题】【风险管理-指标管理】页面：1、选择框错位了；2、预警线、禁止线、审批线，显示错乱。
                    // 框架调整，导致画面显示异常，需要业务代码自行处理
//                    resMap.put(field, JsonUtils.object2Json(obj));
                    String tempStr = JsonUtils.object2Json(obj);
                    tempStr = StringUtil.replaceAll(tempStr, ">", "&gt;");
                    tempStr = StringUtil.replaceAll(tempStr, "<", "&lt;");
                    resMap.put(field, tempStr);
                    // modify by etliu 20211115 end
                }
            });
        }
        return resMap;
    }

    public <T> Map<String, Map<String, String>> changeToStrMap(Map<String, Map<String, T>> map) {
        Map<String, Map<String, String>> resMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(map)) {
            map.entrySet().forEach(en -> {
                String key = en.getKey();
                Map<String, String> vMap = new HashMap<>();
                Map<String, T> value = en.getValue();
                if (!CollectionUtils.isEmpty(value)) {
                    value.entrySet().forEach(mp -> {
                        String field = mp.getKey();
                        T obj = mp.getValue();
                        if (obj != null) {
                            vMap.put(field, JsonUtils.object2Json(obj));
                        }
                    });
                    resMap.put(key, vMap);
                }
            });
        }
        return resMap;
    }
    //----zxjjpei 更改类型------------------
    public Map<String, Map<String, Object>> changeToNumMap(Map<String, Map<String, IValue<Object>>> map) {
        Map<String, Map<String, Object>> resMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(map)) {
            map.entrySet().forEach(en -> {
                String key = en.getKey();
                Map<String, Object> vMap = new HashMap<>();
                Map<String, IValue<Object>> value = en.getValue();
                if (!CollectionUtils.isEmpty(value)) {
                    value.entrySet().forEach(mp -> {
                        String field = mp.getKey();
                        IValue<Object> obj = mp.getValue();
                        if (obj != null) {
                            vMap.put(field, obj.value());
                        }
                    });
                    resMap.put(key, vMap);
                }
            });
        }
        return resMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, IEvent>> changeEventMap(Map<String, ?> map) {
        Map<String, EventMap> eveMap = (Map<String, EventMap>)map.get(RedisKeyEnum.风险事件配置.key());
        Map<String, Map<String, IEvent>> eventMp = new HashMap<>();
        if (!CollectionUtils.isEmpty(eveMap)) {
            EventMap entity = eveMap.get(EventMap.ALL);
            eventMp.put(RedisKeyEnum.风险事件配置.key(), entity.getEventMap());
        }
        return eventMp;
    }

    public interface KeyReader<T> {
        List<T> doReader(T var1);
    }

    @ResponseBody
    @RequestMapping("queryItemInfo")
    public Page<SelectOptionDto> queryItemInfo() {
        Map<String, Object> param = RequestUtil.getPageParam();
        String key = (String)param.get("search");
        return queryKeyInfo(key, k -> valSrc.allKeys(k));
    }

    /**
     * 获取redis中key
     * @param k
     * @param reader
     * @return
     */
    private Page<SelectOptionDto> queryKeyInfo(String k, KeyReader<String> reader) {
        StringBuilder builder = new StringBuilder("*");
        if (!StringUtil.isEmpty(k)) {
            builder.append(k).append("*");
        }
        Set<String> keySet=new HashSet<>();
        List<String> keys = reader.doReader(builder.toString());
        keys.forEach(key->{
            keySet.add(key);
        });
        List<SelectOptionDto> dtoList = new ArrayList();
        keySet.forEach((key) -> {
            SelectOptionDto dto = new SelectOptionDto();
            dto.setId(key);
            dto.setText(key);
            dtoList.add(dto);
        });
        return toPage(dtoList);
    }

    @ResponseBody
    @RequestMapping("queryField")
    public Page<SelectOptionDto> queryField() {
        Map<String, Object> param = RequestUtil.getPageParam();
        String field = (String)param.get("search");
        String key = (String)param.get(RDS_KEY);
        return queryItemInfo(field, k -> valSrc.allHKeys(key, k));
    }

    @ResponseBody
    @RequestMapping(value={"refresh/{isUpParamVal}","refresh"})
    public AjaxResult refresh(@RequestBody RedisHash<String, String, String> dto, @PathVariable(value="isUpParamVal",required=false) String isUpParamVal) {
        AjaxResult aj = new AjaxResult();
        fresh(dto.getKey(), dto.getField(),isUpParamVal);
        return aj;
    }

    private void fresh(String key, String filed,String isUpParamVal) {
        ImplCond cond = new ImplCond();
        cond.setDel(false);
        cond.setPlateDate(DateUtil.parse(DateUtil.getSysDate()));
        RedisBaseService<?> baseService = getRedisBaseService(key);
        if (baseService != null && !isRedisKey(key, RedisKeyEnum.预占实占信息)
            && !isRedisKey(key, RedisKeyEnum.指标趋势信息)) {
        	baseService.deleteInfoFromRedis(key, !StringUtil.isEmpty(filed) ? filed : null);
        }
        if (isRedisKey(key, RedisKeyEnum.指标信息配置)) {
        	if (!StringUtil.isEmpty(filed)) {
        	    cond.setIndicatorCodeArray(filed);
        	}
            redisWriter.dealRiskIndicator(cond);
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.指标参数配置)) {
            cond.setIsUpdParVal(false);
        	if (!StringUtil.isEmpty(filed)) {
                filed = StringUtil.contains(filed, GlobalConstant.COLON)
                        ? StringUtil.substring(filed, 0, StringUtil.indexOf(filed, GlobalConstant.COLON))
                        : filed;
                cond.setParamCodeArray(filed);
                cond.setIsUpdParVal(StringUtil.equals(isUpParamVal,BussinessDictCodeListRds.是否_是.getValue())?true:false);
        	}
            cond.setLoadOffset(false);
            redisWriter.dealRiskIndParam(cond);
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.组合关联指标配置)) {
        	if (!StringUtil.isEmpty(filed)) {
                cond.setPortfIdArray(filed);
        	}
            redisWriter.dealRiskPtlIndicator(cond);
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.预占实占信息)) {
            //
        }
        if (isRedisKey(key, RedisKeyEnum.风险事件配置)) {
            redisWriter.dealRiskEvent();
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.产品基本信息)) {
        	if (!StringUtil.isEmpty(filed)) {
        	    cond.setPortfIdArray(filed);
        	}
        	redisWriter.dealPortData(cond);
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.资产基本信息)) {
        	if (!StringUtil.isEmpty(filed)) {
        	    cond.setAssetIdArray(filed);
        	}
        	redisWriter.dealAssetData(cond);
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.资产代码索引)) {
        	if (!StringUtil.isEmpty(filed)) {
        	    cond.setParamCodeArray(filed);
        	}
        	redisWriter.dealAssetIndex(cond);
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.汇率行情信息)) {
        	if (!StringUtil.isEmpty(filed)) {
        	    List<String> fList = new ArrayList<>();
        	    fList.add(filed);
                cond.setCondList(fList);
        	}
        	redisWriter.dealExchargeInfo(cond);
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.基金估值行情信息)) {
        	if (!StringUtil.isEmpty(filed)) {
        	    List<String> fList = new ArrayList<>();
        	    fList.add(filed);
        	    cond.setCondList(fList);
        	}
        	redisWriter.dealFundValuation(cond);
            return;
        }
        if (isRedisKey(key, RedisKeyEnum.日历信息)) {
        	if (!StringUtil.isEmpty(filed)) {
        	    cond.setCalendarIdArray(filed);
        	}
        	redisWriter.dealCalendarData(cond);
            return;
        }
        // add by zhangrui1 start
        if (isRedisKey(key, RedisKeyEnum.资产实时规模信息)) {
            if (!StringUtil.isEmpty(filed)) {
                List<String> fList = new ArrayList<>();
                fList.add(filed);
                cond.setCondList(fList);
            }
            redisWriter.dealProdAssetAmount(cond);
            return;
        }
        // add by zhangrui1 end
        if (isRedisKey(key, RedisKeyEnum.预占实占信息)) {
            redisWriter.dealOccupyRelParamData();
            return;
        }
        cond.setParamCodeArray(StringUtil.substring(key, 0, StringUtil.indexOf(key, GlobalConstant.COLON)));
        cond.setIsUpdParVal(true);
        List<String> fList = new ArrayList<>();
        fList.add(filed);
        cond.setCondList(fList);
        cond.setLoadOffset(false);
        redisWriter.dealRiskIndParam(cond);
    }

    private RedisBaseService<?> getRedisBaseService(String key) {
        if (isRedisKey(key, RedisKeyEnum.指标信息配置)) {
            return getIndSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.指标参数配置)) {
            return getParaSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.组合关联指标配置)) {
            return getPtlSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.预占实占信息)) {
            return getOcpSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.风险事件配置)) {
            return getEventSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.资产基本信息)) {
            return getAssetInfoSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.产品基本信息)) {
            return getProdInfoSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.资产代码索引)) {
            return getAssetIndexSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.汇率行情信息)) {
            return getExchargeSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.基金估值行情信息)) {
            return getFundValSrc();
        }
        if (isRedisKey(key, RedisKeyEnum.日历信息)) {
            return getCalendarSrc();
        }
        // add by zhangrui1 start 
        if (isRedisKey(key, RedisKeyEnum.资产实时规模信息)) {
            return getProdAssetAmtSrc();
        }
        // add by zhangrui1 end
        // add by zhangrui1 start
        if (isRedisKey(key, RedisKeyEnum.指标趋势信息)) {
            return getProdAssetAmtSrc();
        }
        // add by zhangrui1 end
        return null;
    }

    private boolean isRedisKey(String key, RedisKeyEnum keyEnum) {
        return StringUtil.equals(key, keyEnum.key());
    }

    @ModelAttribute("purviewElements")
    private String addPurviewElements() {
        User user = SecurityUtil.getLoginUser();
        if (null == user) {
            return "[]";
        } else {
            Set<String> authElements = new HashSet(ButtonPurview.allPurviews());
            if (!StringUtil.equals("admin", user.getUserName()) && !user.isAdmin() && null != this.page) {
                Collection<PageElement> elements = this.page.getElements();
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(elements)) {
                    Iterator var4 = elements.iterator();

                    while(var4.hasNext()) {
                        PageElement ele = (PageElement)var4.next();
                        if (ele.isAuth() && !SecurityUtil.isPermitted(ele.getId())) {
                            if (StringUtil.endsWith(ele.getId(), ButtonPurview.EDIT.getValue())) {
                                authElements.remove(ButtonPurview.EDIT.getValue());
                            } else if (StringUtil.endsWith(ele.getId(), ButtonPurview.DELETE.getValue())) {
                                authElements.remove(ButtonPurview.DELETE.getValue());
                            }

                            if (StringUtil.endsWith(ele.getId(), ButtonPurview.SEARCH.getValue())) {
                                authElements.remove(ButtonPurview.SEARCH.getValue());
                            }

                            if (StringUtil.endsWith(ele.getId(), ButtonPurview.EXPORT.getValue())) {
                                authElements.remove(ButtonPurview.EXPORT.getValue());
                            }
                        }
                    }
                }
            }

            return JsonUtils.object2Json(authElements);
        }
    }

    @ModelAttribute("dataPurview")
    private String addDataPurview() {
        User user = SecurityUtil.getLoginUser();
        List<GridColumnAuthInfo> authElements = new ArrayList();
        if (null != user && !StringUtil.equals("admin", user.getUserName()) && !user.isAdmin() && StringUtil.equals(CacheUtil.getSysParam("data_authority"), DictCodeList.是否_是.getValue()) && null != this.dataAuthorityService) {
            authElements = this.dataAuthorityService.getGridColumn();
        }

        return JsonUtils.object2Json(authElements);
    }


    protected void setSelectOptionToModel(Model model, IDictCode... dictCodes) {
        IDictCode[] var3 = dictCodes;
        int var4 = dictCodes.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            IDictCode dictCode = var3[var5];
            this.setSelectOptionToModel(dictCode, model);
        }

    }

    private void setSelectOptionToModel(IDictCode dictCode, Model model) {
        SelectDto selectDto = this.getSelectDataFromDataDic(dictCode, (String)null);
        if (selectDto != null && !StringUtil.isEmptyList(selectDto.getOptions())) {
            List<SelectOptionDto> options = selectDto.getOptions();
            if (dictCode.equals(BaseDictCode.数据状态)) {
                for(int index = options.size() - 1; index >= 0; --index) {
                    SelectOptionDto option = (SelectOptionDto)options.get(index);
                    if (StringUtil.equals(option.getId(), BaseDictCodeList.数据状态_新建.getValue()) || StringUtil.equals(option.getId(), BaseDictCodeList.数据状态_删除中.getValue()) || StringUtil.equals(option.getId(), BaseDictCodeList.数据状态_修改中.getValue())) {
                        options.remove(option);
                    }
                }
            }

            model.addAttribute(dictCode.getValue(), JsonUtils.objectArray2Json(options.toArray()));
            model.addAttribute(dictCode.getValue() + "_default", selectDto.getInitValue());
        }

    }

    protected SelectDto getSelectDataFromDataDic(IDictCode dictCode, String likeStr) {
        DataDict dataDict = CacheUtil.getDictCode(dictCode.getValue());
        SelectDto select = new SelectDto();
        if (null == dataDict) {
            logger.info("数据字典缓存获取失败，请重启系统或手动更新缓存。");
        } else {
            Set<String> pageIds = dataDict.getPageIds();
            List<? extends DataDictItem> itemList = dataDict.getItems();
            String pageKey = this.getPageId() + "|" + this.getSubPageId();
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(pageIds) && pageIds.contains(pageKey)) {
                itemList = (List)itemList.stream().filter((item) -> {
                    return item.getPageIds().contains(pageKey);
                }).collect(Collectors.toList());
            }

            if (StringUtil.isNotEmpty(likeStr)) {
                itemList = (List)itemList.stream().filter((item) -> {
                    return item.getItemCode().indexOf(likeStr) != -1 || item.getItemValue().indexOf(likeStr) != -1;
                }).collect(Collectors.toList());
            }

            List<SelectOptionDto> options = new ArrayList(itemList.size());
            if (this.dealDictCodeDataService != null) {
                this.dealDictCodeDataService.getSelectDataFromDictCodeByKey(options, itemList, dataDict, select, pageKey);
            } else {
                itemList.forEach((item) -> {
                    SelectOptionDto option = new SelectOptionDto();
                    option.setId(item.getItemCode());
                    option.setText(item.getItemValue());
                    option.setCol(dataDict.getDictCode());
                    option.setParentId(item.getParentId());
                    if ("1".equals(item.getDefaultValue())) {
                        select.setInitValue(item.getItemCode());
                    }

                    options.add(option);
                });
                select.setOptions(options);
                select.setCode(dictCode.getValue());
                select.setName(dataDict.getDictCnname());
            }
        }

        return select;
    }

    protected String getParam(String paramName) {
        String result = this.request.getParameter(paramName)==null?"":this.request.getParameter(paramName);
//      result = StringUtil.delHTMLTag(result);
        result = StringUtil.trimHtmlTag(result);
        if (!StringUtil.strPos(paramName, "inputSql")) {
            result = StringUtil.StringFilter(result);
        }

        return result;
    }
    /**
     * 获取 eventSrc
     * 
     * @return eventSrc.
     */
    public RskEventService getEventSrc() {
        return eventSrc;
    }

    /**
     * 设置 eventSrc
     * 
     * @param eventSrc eventSrc
     */
    public void setEventSrc(RskEventService eventSrc) {
        this.eventSrc = eventSrc;
    }

    /**
     * 获取 indSrc
     * 
     * @return indSrc.
     */
    public IndicatorService getIndSrc() {
        return indSrc;
    }

    /**
     * 设置 indSrc
     * 
     * @param indSrc indSrc
     */
    public void setIndSrc(IndicatorService indSrc) {
        this.indSrc = indSrc;
    }

    /**
     * 获取 ocpSrc
     * 
     * @return ocpSrc.
     */
    public OccupyRelParamService getOcpSrc() {
        return ocpSrc;
    }

    /**
     * 设置 ocpSrc
     * 
     * @param ocpSrc ocpSrc
     */
    public void setOcpSrc(OccupyRelParamService ocpSrc) {
        this.ocpSrc = ocpSrc;
    }

    /**
     * 获取 paraSrc
     * 
     * @return paraSrc.
     */
    public IndicatorParamService getParaSrc() {
        return paraSrc;
    }

    /**
     * 设置 paraSrc
     * 
     * @param paraSrc paraSrc
     */
    public void setParaSrc(IndicatorParamService paraSrc) {
        this.paraSrc = paraSrc;
    }

    /**
     * 获取 valSrc
     * 
     * @return valSrc.
     */
    public IndicatorParamValService getValSrc() {
        return valSrc;
    }

    /**
     * 设置 valSrc
     * 
     * @param valSrc valSrc
     */
    public void setValSrc(IndicatorParamValService valSrc) {
        this.valSrc = valSrc;
    }

    /**
     * 获取 ptlSrc
     * 
     * @return ptlSrc.
     */
    public PtlIndicatorService getPtlSrc() {
        return ptlSrc;
    }

    /**
     * 设置 ptlSrc
     * 
     * @param ptlSrc ptlSrc
     */
    public void setPtlSrc(PtlIndicatorService ptlSrc) {
        this.ptlSrc = ptlSrc;
    }

    /**
     * 获取 prodInfoSrc
     * 
     * @return prodInfoSrc.
     */
    public ProdInfoRedisService getProdInfoSrc() {
        return prodInfoSrc;
    }

    /**
     * 设置 prodInfoSrc
     * 
     * @param prodInfoSrc prodInfoSrc
     */
    public void setProdInfoSrc(ProdInfoRedisService prodInfoSrc) {
        this.prodInfoSrc = prodInfoSrc;
    }

    /**
     * 获取 assetInfoSrc
     * 
     * @return assetInfoSrc.
     */
    public AssetInfoRedisService getAssetInfoSrc() {
        return assetInfoSrc;
    }

    /**
     * 设置 assetInfoSrc
     * 
     * @param assetInfoSrc assetInfoSrc
     */
    public void setAssetInfoSrc(AssetInfoRedisService assetInfoSrc) {
        this.assetInfoSrc = assetInfoSrc;
    }

    /**
     * 获取 assetIndexSrc
     * 
     * @return assetIndexSrc
     */
	public AssetIndexService getAssetIndexSrc() {
		return assetIndexSrc;
	}

    /**
     * 设置 assetIndexSrc
     * 
     * @param assetIndexSrc assetIndexSrc
     */
	public void setAssetIndexSrc(AssetIndexService assetIndexSrc) {
		this.assetIndexSrc = assetIndexSrc;
	}

	public ExchargeInfoService getExchargeSrc() {
		return exchargeSrc;
	}

	public void setExchargeSrc(ExchargeInfoService exchargeSrc) {
		this.exchargeSrc = exchargeSrc;
	}

	public FundValuationService getFundValSrc() {
		return fundValSrc;
	}

	public void setFundValSrc(FundValuationService fundValSrc) {
		this.fundValSrc = fundValSrc;
	}

	public CalendarService getCalendarSrc() {
		return calendarSrc;
	}

	public void setCalendarSrc(CalendarService calendarSrc) {
		this.calendarSrc = calendarSrc;
	}


    public String getPageId() {
        return this.pageId;
    }

    public String getSubPageId() {
        return this.subPageId;
    }
    // add by zhangrui1 start
	public ProdAssetAmountService getProdAssetAmtSrc() {
		return prodAssetAmtSrc;
	}

	public void setProdAssetAmtSrc(ProdAssetAmountService prodAssetAmtSrc) {
		this.prodAssetAmtSrc = prodAssetAmtSrc;
	}
    // add by zhangrui1 end
    // add by zhangrui1 20200814 start
    public IndicatorTrendService getIndTrendSrc() {
        return indTrendSrc;
    }

    public void setProdAssetAmtSrc(IndicatorTrendService indTrendSrc) {
        this.indTrendSrc = indTrendSrc;
    }
    // add by zhangrui1 20200814 end
}
