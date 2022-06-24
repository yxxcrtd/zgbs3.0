package com.joyintech.tams.exchange.service.trade.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.joyintech.background.component.common.pack.common.base.BaseService;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.tams.exchange.common.bean.OthCodeBean;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.joyintech.tams.exchange.common.constant.EntrustProp.*;
import static com.joyintech.tams.exchange.common.constant.OthCodeType.*;

/**
 * 投管接口
 */
@Component
public class InvestInterfaceHelper extends BaseService {
    public static final String DECLARATION_TYPE = "othCodeType";
    public static final String FINPRODID = "finprodId";
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(InvestInterfaceHelper.class);
    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;
    @Autowired(required = false)
    @Qualifier("loadBalancedRestTemplate")
    protected JoyinRestTemplate restTemplate;
    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;


    /**
     * 查询带交易市场标识后缀的申报代码
     *
     * @param finprodId   资产代码
     * @param entrustProp 委托属性
     * @return 申报代码
     */
    public String queryDeclarationCodeWithMarketSuffix(String finprodId, String entrustProp, String finprodMarketId) {
        String declarationCode = queryDeclarationCode(finprodId, entrustProp);
        if (StrUtil.isEmpty(declarationCode) || StrUtil.isEmpty(finprodMarketId)) {
            return StrUtil.EMPTY;
        }
        return StockCodeUtils.formatStockCode(declarationCode.trim(), StockCodeUtils.getMarket(finprodMarketId));

    }

    /**
     * 查询申报代码
     *
     * @param finprodId   资产代码
     * @param entrustProp 委托属性
     * @return 申报代码
     */
    public String queryDeclarationCode(String finprodId, String entrustProp) {
        String declarationType = convertEntrustProp2DeclarationType(entrustProp);
        if (StrUtil.isEmpty(finprodId) || StrUtil.isEmpty(declarationType)) {
            return StrUtil.EMPTY;
        }

        MapBuilder<String, Object> builder = MapUtil.builder();
        Map<String, Object> map = builder.put(FINPRODID, finprodId)
                .put(DECLARATION_TYPE, declarationType)
                .build();
        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.selectForInterf;

        List<OthCodeBean> list = postForList(url, map, OthCodeBean.class);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0).getOthCode();
        }
        return StrUtil.EMPTY;
    }

    private String convertEntrustProp2DeclarationType(String entrustProp) {
        if (StrUtil.isEmpty(entrustProp)) {
            return StrUtil.EMPTY;
        }
        String declarationType = StrUtil.EMPTY;
        if (HS.value.equals(entrustProp)) {
            declarationType = HSDM.value;
        } else if (ZYCRK.value.equals(entrustProp)) {
            declarationType = ZYDM.value;
        } else if (CHANGE_TO_STOCK.value.equals(entrustProp)) {
            declarationType = KZZZGDM.value;
        } else if (PS.value.equals(entrustProp)) {
            declarationType = PZDM.value;
        } else if (ALLOTMENT.value.equals(entrustProp)) {
            declarationType = PGDM.value;
        }
        return declarationType;
    }

    private Map postForMap(String url, Map<String, Object> paramMap) {

        try {
            logger.debug("调用投管接口 => 【url = {}】, 【paramMap = {}】", url, paramMap);
            if (isSingle) {
                return simpleRestTemplate.postForObject(url, paramMap, Map.class);
            }
            return restTemplate.postForObject(url, paramMap, Map.class);
        } catch (Exception e) {
            logger.error("调用投管接口失败 => 【url = {}】, 【paramMap = {}】, 【exception = {}】", url, paramMap, ExceptionUtil.getMessage(e));
            return MapUtil.empty();
        }
    }

    private <T> List<T> postForList(String url, Map<String, Object> paramMap, Class<T> beanClass) {

        try {
            logger.debug("调用投管接口 => 【url = {}】, 【paramMap = {}】, 【beanClass = {}】", url, paramMap, beanClass);
            if (isSingle) {
                return simpleRestTemplate.postForList(url, paramMap, beanClass);
            }
            return restTemplate.postForList(url, paramMap, beanClass);
        } catch (Exception e) {
            logger.error("调用投管接口失败 => 【url = {}】, 【paramMap = {}】, 【exception = {}】", url, paramMap, ExceptionUtil.getMessage(e));
            return ListUtil.empty();
        }
    }

}
