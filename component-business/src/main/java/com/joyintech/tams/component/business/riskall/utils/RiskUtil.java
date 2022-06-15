package com.joyintech.tams.component.business.riskall.utils;


import com.google.common.collect.Lists;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;

import java.util.List;

/**
 * 风险处理
 */
public class RiskUtil {
    //预占类型
    public static List<String> preList = Lists.newArrayList(DictCodeListRsk.风险占用类型_预占.getValue(), DictCodeListRsk.风险占用类型_预占释放.getValue(),
            DictCodeListRsk.风险占用类型_预占释放再预占.getValue());
    //实占类型
    public static List<String> list = Lists.newArrayList(DictCodeListRsk.风险占用类型_实占.getValue(), DictCodeListRsk.风险占用类型_实占释放.getValue(),
            DictCodeListRsk.风险占用类型_实占释放再实占.getValue());

    /**
     * 根据配置组装返回的指标入参对象
     *
     * @param dtl
     * @return
     */
    public static InterfRiskRdsDto getInterfRiskRdsDto(RiskNodeConfigDtl dtl, Boolean isCheck) {
        InterfRiskRdsDto riskRdsDto = new InterfRiskRdsDto();
        if (preList.contains(dtl.getIndicatorOccupy())) {
            riskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue());
            riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
        }
        if (list.contains(dtl.getIndicatorOccupy())) {
            riskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
            riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
        }
        if (isCheck) {
            riskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_风险试算批量接口.getValue());
        }
        return riskRdsDto;
    }

    /**
     * 根据配置组装返回的授信对象
     *
     * @param dtl
     * @param businessDto
     * @return
     */
    public static CreditLinesInputDto getCreditLinesInputDto(RiskNodeConfigDtl dtl, Object obj) {
        CreditLinesInputDto inputDto = new CreditLinesInputDto();
        inputDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        if (preList.contains(dtl.getCreditOccupy())) {
            inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            if (StringUtil.equals(DictCodeListRsk.风险占用类型_预占释放.getValue(), dtl.getCreditOccupy())) {
                inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            }
        }
        if (list.contains(dtl.getCreditOccupy())) {
            inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            if (StringUtil.equals(DictCodeListRsk.风险占用类型_实占释放.getValue(), dtl.getCreditOccupy())) {
                inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            }
        }
        // 处理预占编号
        Object preOccupyCode = CommonUtil.findValue(JsonUtils.object2Map(obj),dtl.getCreditPreNode());
        if (StringUtil.isNotEmpty(preOccupyCode)){
            inputDto.setEntrustId(preOccupyCode.toString());
        }
        return inputDto;
    }

}
