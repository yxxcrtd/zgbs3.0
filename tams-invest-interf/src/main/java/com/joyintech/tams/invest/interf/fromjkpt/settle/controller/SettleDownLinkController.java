package com.joyintech.tams.invest.interf.fromjkpt.settle.controller;

import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.invest.common.service.BatchManualExcuteService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

/**
 *  结算文件解析完，启动批处理
 * @author zznn
 * @date 2021/10/25
 */
@RestController("settleDownLink_dtx")
@RequestMapping("/settleDownLink")
public class SettleDownLinkController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private BatchManualExcuteService batchManualExcuteService;

    @Transactional
    @DTXTransaction
    @PostMapping("execute")
    public void execute(@RequestBody Map<String,Object> param) {
        try{
            logger.debug("接口平台调用批处理开始");
            String isRecon = CacheUtil.getSysParam("IS_RECON");
            if(StringUtil.equals(isRecon,"01")){
                // modify by fuyongsheng 20211201 接收清算文件消息后新增批处理 start
                // param.put("beanNames", Arrays.asList("investDealRevokeTask","stlDownDataDealTask","stlDownDataGatherTask"));
                param.put("beanNames", Arrays.asList("investDealRevokeTask","stlDownDataDealTask","stlDownDataGatherTask","stlSettleTransDataGatherTask","stlDownDurationDataGatherTask"));
                // modify by fuyongsheng 20211201 接收清算文件消息后新增批处理 end
                if(!param.containsKey("execDate")){
                    param.put("execDate", DateUtil.format(iPlatformDate.getSysDate(),"yyyyMMdd"));
                } else {
                    param.put("execDate", DateUtil.format(DateUtil.parse(
                            param.get("execDate").toString()),"yyyyMMdd"));
                }
                batchManualExcuteService.execute(param);
                logger.debug("接口平台调用批处理成功");
            }else{
                logger.debug("系统参数是否有对账流程IS_RECON非01,不用启动批处理");
            }
        }catch (Exception e){
            logger.error("接口平台调用批处理失败!"+e.getMessage());
        }
    }

}

