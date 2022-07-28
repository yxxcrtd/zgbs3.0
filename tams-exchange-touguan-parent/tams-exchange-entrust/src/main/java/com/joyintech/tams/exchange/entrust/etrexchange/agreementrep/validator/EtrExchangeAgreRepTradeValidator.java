package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.validator;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinExercisePlanMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinExercisePlanDto;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto.EtrExchangeAgreRepTrade001Dto;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.service.EtrExchangeAgreRepTrade001Service;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class EtrExchangeAgreRepTradeValidator implements EtrCommonValidator {

    @Autowired
    private IPlatformDate iPlatformDate;
    @Autowired
    private FinProductMapper finProductMapper;
    @Autowired
    private FinExercisePlanMapper finExercisePlanMapper;

    @Autowired
    private EtrExchangeAgreRepTrade001Service etrExchangeAgreRepTrade001Service;


    @Override
    public List<ValidMessage> bussValid(Object value) {
        List<ValidMessage> errorList = new ArrayList<>();
        EtrExchangeAgreRepTrade001Dto dto = (EtrExchangeAgreRepTrade001Dto) value;

        //交易方向Map
        Map<String, DictCodeListExt3> psMap = new HashMap<>();
        psMap.put(DictCodeListExt3.交易方向_提前购回.getValue(), DictCodeListExt3.交易方向_提前购回);
        psMap.put(DictCodeListExt3.交易方向_到期购回.getValue(), DictCodeListExt3.交易方向_到期购回);
//                put(DictCodeListExt3.交易方向_中途换券.getValue(),DictCodeListExt3.交易方向_中途换券);
        psMap.put(DictCodeListExt3.交易方向_解除质押.getValue(), DictCodeListExt3.交易方向_解除质押);
        psMap.put(DictCodeListExt3.交易方向_到期续做.getValue(), DictCodeListExt3.交易方向_到期续做);

        if (StringUtil.equals(DictCodeListExt3.交易方向_首期.getValue(), dto.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_正回购.getValue(), dto.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_逆回购.getValue(), dto.getPs())) {
            if (dto.getTradeAmt() != null && dto.getRepSecNum() != null
                    && dto.getTradeAmt().compareTo(dto.getRepSecNum().multiply(BigDecimal.valueOf(100))) > 0) {
                errorList.add(new ValidMessage("回购金额不能超过质押券数量！"));
            }
        }

        // modify by etliu 20220721 start
        // 3.0.6协议回购+风险升级 XYHGFXSJ-355
        //【协议回购】上交所正回购解除质押指令，撤回后，2.撤回后指令修改提交时，提示【 回购期限不能超过质押债券的存续期限！】不应该有此提示
//        FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
        FinProduct finProduct;
        if (StringUtil.equals(DictCodeListExt3.交易方向_首期.getValue(), dto.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_正回购.getValue(), dto.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_逆回购.getValue(), dto.getPs())) {
            finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
            if (StringUtil.isNotEmpty(finProduct) && DateUtil.compareTo(DateUtil.parse(dto.getExpireSettleDate()), finProduct.getMdate()) > 0) {
                errorList.add(new ValidMessage("term", "回购期限不能超过质押债券的存续期限！"));
            }
        }
        // modify by etliu 20220721 end

        if (StringUtil.equals(DictCodeListExt3.交易方向_中途换券.getValue(), dto.getPs())
                && StringUtil.isNotBlank(dto.getNewfinprodId())
                && dto.getExpireSettleDate() != null) {
            finProduct = finProductMapper.selectByPrimaryKey(dto.getNewfinprodId());
            if (StringUtil.isNotEmpty(finProduct) && DateUtil.compareTo(DateUtil.parse(dto.getExpireSettleDate()), finProduct.getMdate()) > 0) {
                errorList.add(new ValidMessage("term", "回购期限不能超过新质押券的存续期限！"));
            }
        }


        if (StringUtil.equals(DictCodeListExt3.交易方向_首期.getValue(), dto.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_到期续做.getValue(), dto.getPs())) {
            Date sysDate = iPlatformDate.getSysDate();
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", dto.getFinprodId());
            params.put("sysDate", sysDate);
            FinExercisePlanDto finExercisePlanDto = finExercisePlanMapper.selectDateByFinprodId(params);
            if (Objects.nonNull(finExercisePlanDto) && Objects.nonNull(finExercisePlanDto.getExerciseDate())) {
                Date exerciseDate = finExercisePlanDto.getExerciseDate();
                if (DateUtil.compareTo(exerciseDate, DateUtil.parse(dto.getVdate())) <= 0) {
                    errorList.add(new ValidMessage("", "行权计划中的赎回日期必须大于协议回购到期结算日！"));
                }
            }

            finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
            if(Objects.nonNull(finProduct) && Objects.nonNull(finProduct.getMdate())){
                if (DateUtil.compareTo(DateUtil.parse(dto.getExpireSettleDate()), DateUtil.parse(dto.getMdate())) <= 0) {
                    errorList.add(new ValidMessage("", "质押券的到期日必须大于协议回购到期结算日！"));
                }
            }
        }

        //判断提前购回、到期购回、到期续作、解除质押需要判断是否重复下了指令
        if (WorkFlowUtil.isNotFromToDoList()) {
            if (StringUtil.isNotEmpty(psMap.get(dto.getPs())) && !StringUtil.equals(dto.getRedoType(), "1")
                    && StringUtil.isBlank(dto.getEntrustId())) {
                if (etrExchangeAgreRepTrade001Service.isAlreadyDoEntrust(dto.getPs(),
                        StringUtil.isNotEmpty(dto.getIntiTradeId()) ? dto.getIntiTradeId() : dto.getTradeId())) {
                    errorList.add(new ValidMessage("", psMap.get(dto.getPs()).getCodeName() + "不能重复做！"));
                }
            }
        }
        return errorList;
    }
}
