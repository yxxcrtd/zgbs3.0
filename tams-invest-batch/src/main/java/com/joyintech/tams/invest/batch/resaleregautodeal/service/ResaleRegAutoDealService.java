package com.joyintech.tams.invest.batch.resaleregautodeal.service;

import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.resaleregautodeal.dao.ResaleRegAutoDealMapper;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 回售登记数据自动生成成交数据
 */
@Service
public class ResaleRegAutoDealService {

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextpath.single:false}")
    private boolean isSingle;

    @Autowired(required = false)
    private JoyinRestTemplate restTemplate;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    @Value("${application.micorservice.contextpath.tamsServer:http://tams-server}")
    private String tamsSerever;

    @Autowired
    private ResaleRegAutoDealMapper resaleRegAutoDealMapper;

    /**
     * 回售登记数据自动生成成交数据
     * @param currentDate
     */
    public void run(Date currentDate) throws BatchException {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("cdate", DateUtil.format(currentDate));
        //先删除生成的数据应该
        List<String> totleTradeIds = resaleRegAutoDealMapper.listTotleTradeId(paramMap);
        if(CollectionUtils.isNotEmpty(totleTradeIds)) {
            //撤回头寸
            List<String> totalList = resaleRegAutoDealMapper.getTotleTradeId(totleTradeIds);
            for(String total : totalList) {
                PositionHttpRequest.dorevoke(total, DictCodeListExt1.子系统代码_交易.getValue(), DictCodeListExt3.头寸业务类型_债券行权交易.getValue(), BussinessDictCodeList.交易方向_行权回售.getValue());
            }
            //删除已经生成的数据，用来支持重复跑
            resaleRegAutoDealMapper.deletePadeal(totleTradeIds);
            resaleRegAutoDealMapper.deleteDealAdd(totleTradeIds);
            resaleRegAutoDealMapper.deleteDeal(totleTradeIds);
            resaleRegAutoDealMapper.deletePaDealTotal(totleTradeIds);
            resaleRegAutoDealMapper.deleteDealTotal(totleTradeIds);
            resaleRegAutoDealMapper.deleteMatchInfoByPreTradeId(totleTradeIds);
            paramMap.put("totleTradeIds",totleTradeIds);
            //调用页面自动成交确认
            try{
                if(isSingle) {
                    simpleRestTemplate.postForObject(tamsSerever + "/cfets/common/CfetsIbuQuoteBatchCommon/resaleregautodeal", paramMap, Map.class);
                }
                else {
                    restTemplate.postForObject(tamsSerever + "/cfets/common/CfetsIbuQuoteBatchCommon/resaleregautodeal", paramMap, Map.class);
                }
            }catch (Exception e){
                throw new BatchException(e.getMessage());
            }
        }
    }
}
