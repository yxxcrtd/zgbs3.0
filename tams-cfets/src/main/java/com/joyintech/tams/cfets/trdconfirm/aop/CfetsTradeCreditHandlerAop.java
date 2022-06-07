package com.joyintech.tams.cfets.trdconfirm.aop;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
//@Aspect
public class CfetsTradeCreditHandlerAop {

    @Autowired
    private TradeCommonService tradeCommonService;

    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doCompleteMethod(..))")
    protected void completePoint() {

    }

    /**
     * 处理回售操作的占用和释放
     */
    @After("completePoint()")
    public void creditOccupyOrReleaseMethod(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        try {
            Object[] args = joinPoint.getArgs();
            OperationType operationType = (OperationType) args[1];
            
            // add by etliu 20210129 start
            // 选择的成交编号存在对应的报价，则不影响风险
            Map<String,Object> mapTrd= JsonUtils.object2Map(args[0]);
            if (mapTrd.get("fTradeId") != null) {
                String fTradeId = (String) mapTrd.get("fTradeId");
                if (StringUtil.isNotBlank(fTradeId) && StringUtil.equals(DictCodeList.是否_是.getValue(),
                    tradeCommonService.checkExecIdHasQuote(fTradeId))) {
                    return;
                }
            }
            // add by etliu 20210129 end
            CfetsTrdBondInvestTradeDto pageInputDto = (CfetsTrdBondInvestTradeDto) args[0];
            TrdBondInvestTradeDto inputDto = new TrdBondInvestTradeDto();
            BeanUtil.copyPropertiesIgnoreNull(inputDto, pageInputDto);
            if (operationType.equals(OperationType.REVOKE)||operationType.equals(OperationType.DELETE)) {
                tradeCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_撤销.getValue(),DictCodeListRsk.授信占用类别_实占.getValue(),"doComplete");
            }else {
                tradeCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_初始.getValue(), DictCodeListRsk.授信占用类别_实占.getValue(),"doComplete");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
