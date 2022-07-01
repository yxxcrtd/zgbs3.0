package com.joyintech.service.bankuplink.cfets.service;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.cfets.constant.CfetsConstant;
import com.joyintech.service.bankuplink.cfets.dao.QuoteIdConverMapper;
import com.joyintech.service.bankuplink.cfets.dto.QuoteIdConverDto;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报价编号和客户参考编号转化
 * @author wangyang1
 * @version 1.0 2021年10月28日
 * @see QuoteIdConverService
 * @since 1.0
 */
@Service
public class QuoteIdConverService {

    @Autowired
    private QuoteIdConverMapper quoteIdConverMapper;

    @Autowired
    private BaseGetBussinessId bussGetBussinessIdService;

    @Autowired
    private IPlatformDate iPlatformDate;
    /**
     * 报价订单报文开头
     */
    private static final String OD = "OD";

    /**
     * API用户ID
     */
    private static final String API_USER_ID = "api_user_id";

    public String getClOrdIdByQuoteIdConver(Date quoteDate, String quoteId){
         Map<String,Object> paramMap = new HashMap<>();
         paramMap.put("quoteDate",DateUtil.format(quoteDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
         paramMap.put("quoteId",quoteId);
         List<QuoteIdConverDto> list = quoteIdConverMapper.selectQuoteIdConver(paramMap);
         if (!CollectionUtils.isNotEmpty(list)){
             String apiUserId = CacheUtil.getSysParam(API_USER_ID);
             BussinessCodeParameter codeParameter = new BussinessCodeParameter();
             codeParameter.setSeqNo("IBU_999");
             String code = bussGetBussinessIdService.buildBussinessCode(codeParameter);
             if(StringUtil.isNotEmpty(apiUserId) && StringUtil.isNotEmpty(code)){
                  String clOrdId = OD+apiUserId+code;
                  QuoteIdConverDto quoteIdConverDto = new QuoteIdConverDto();
                  quoteIdConverDto.setClOrdId(clOrdId);
                  quoteIdConverDto.setQuoteDate(quoteDate);
                  quoteIdConverDto.setQuoteId(quoteId);
                  int flag = quoteIdConverMapper.insertQuoteIdConver(quoteIdConverDto);
                  if (flag >= 1){
                      return clOrdId;
                  }
                 return null;
             }
             return null;
         }
         return list.get(0).getClOrdId();
     }

    public String getQuoteIdByQuoteIdConver(Map<String,Object> param){
        String clOrdId = StringUtil.isNotEmpty(param.get("clOrdID"))?param.get("clOrdID").toString():null;
		if(!StringUtil.isNotEmpty(clOrdId)){
			return null;
		}
        Map<String,Object> paramMap = new HashMap<>();
        Date transactTime = null;
        if (null != param.get("transactTime")) {
            try {
                transactTime = DateUtil.parseDate((String)param.get("transactTime"),
                        CfetsConstant.DATETIME_FORMAT);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        paramMap.put("quoteDate",StringUtil.isNotEmpty(transactTime)?DateUtil.format(transactTime,DateUtil.DEFAULT_DATE_FORMAT_EN):iPlatformDate.getSysDateStr());
        paramMap.put("clOrdId",clOrdId); 
        List<QuoteIdConverDto> list = quoteIdConverMapper.selectQuoteIdConver(paramMap);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0).getQuoteId();
        }
        return clOrdId;
    }
}
