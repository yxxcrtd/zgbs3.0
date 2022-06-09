package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.validator;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto.EtrExchangeAgreRepTrade001Dto;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.service.EtrExchangeAgreRepTrade001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EtrExchangeAgreRepTradeValidator implements EtrCommonValidator {

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private EtrExchangeAgreRepTrade001Service etrExchangeAgreRepTrade001Service;

    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;


    @Override
    public List<ValidMessage> bussValid(Object value) {
        List<ValidMessage> errorList = new ArrayList<>();
        EtrExchangeAgreRepTrade001Dto dto = (EtrExchangeAgreRepTrade001Dto)value;

        //交易方向Map
        Map<String,DictCodeListExt3> psMap = new HashMap<String,DictCodeListExt3>();
        psMap.put(DictCodeListExt3.交易方向_提前购回.getValue(),DictCodeListExt3.交易方向_提前购回);
        psMap.put(DictCodeListExt3.交易方向_到期购回.getValue(),DictCodeListExt3.交易方向_到期购回);
//                put(DictCodeListExt3.交易方向_中途换券.getValue(),DictCodeListExt3.交易方向_中途换券);
        psMap.put(DictCodeListExt3.交易方向_解除质押.getValue(),DictCodeListExt3.交易方向_解除质押);
        psMap.put(DictCodeListExt3.交易方向_到期续做.getValue(),DictCodeListExt3.交易方向_到期续做);


        FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
        if(StringUtil.isNotEmpty(finProduct) && DateUtil.compareTo(DateUtil.parse(dto.getExpireSettleDate()),finProduct.getMdate())>0) {
            errorList.add(new ValidMessage("term", "回购期限不能超过质押债券的存续期限！"));
        }

        if (StringUtil.equals(DictCodeListExt3.交易方向_中途换券.getValue(), dto.getPs())
                && StringUtil.isNotBlank(dto.getNewfinprodId())
                && dto.getExpireSettleDate() != null) {
            finProduct = finProductMapper.selectByPrimaryKey(dto.getNewfinprodId());
            if(StringUtil.isNotEmpty(finProduct) && DateUtil.compareTo(DateUtil.parse(dto.getExpireSettleDate()),finProduct.getMdate())>0) {
                errorList.add(new ValidMessage("term", "回购期限不能超过新质押券的存续期限！"));
            }
        }

//        //判断提前购回、到期购回、到期续作、中途换券、解除质押需要判断回购持仓
//        if(StringUtil.isNotEmpty(psMap.get(dto.getPs())) && !StringUtil.equals(dto.getRedoType(),"1")) {
//            Map<String, Object> param = new HashMap<String, Object>();
//            param.put("portfolioId", dto.getPortfolioId());
//            param.put("finprodId", StringUtil.isNotEmpty(dto.getIntiTradeId())?dto.getIntiTradeId():dto.getTradeId());
//            if (null != dto.getSecAcctId()) {
//                param.put("secAcctId", dto.getSecAcctId());
//            }
//            param.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
//            BigDecimal availableNum =  etrExchangeTradeCommon001Service.selectAvailableNum(param);
//            if(StringUtil.isNotEmpty(availableNum) && BigDecimal.ZERO.compareTo(availableNum)>=0) {
//                errorList.add(new ValidMessage("", psMap.get(dto.getPs()).getCodeName()+"不能重复做！"));
//            }
//        }

//        if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
//            if(StringUtil.equals(dto.getPs(),DictCodeListExt3.交易方向_到期续做.getValue())) {
//            Map<String, Object> queryMap = new HashMap<>();
//            queryMap.put("tradeId",dto.getTradeId() == null ? dto.getIntiTradeId() : dto.getTradeId());
//                EtrExchangeAgreRepTrade001Dto firstEtrAgreRepTradeDto = etrExchangeAgreRepTrade001Service.getEntrustInfoByTradeId(queryMap);
//                if(StringUtil.isNotEmpty(firstEtrAgreRepTradeDto)) {
//                    if(StringUtil.isNotEmpty(firstEtrAgreRepTradeDto.getTerm()) && firstEtrAgreRepTradeDto.getTerm().compareTo(dto.getTerm())>=0) {
//                        errorList.add(new ValidMessage("term", "回购期限必须大于原回购期限！"));
//                    }
//                }
//            }
//        }

        //判断提前购回、到期购回、到期续作、解除质押需要判断是否重复下了指令
        if (WorkFlowUtil.isNotFromToDoList()) {
            if(StringUtil.isNotEmpty(psMap.get(dto.getPs())) && !StringUtil.equals(dto.getRedoType(),"1")
                && StringUtil.isBlank(dto.getEntrustId())) {
                if (etrExchangeAgreRepTrade001Service.isAlreadyDoEntrust(dto.getPs(),
                        StringUtil.isNotEmpty(dto.getIntiTradeId()) ? dto.getIntiTradeId() : dto.getTradeId())) {
                    errorList.add(new ValidMessage("", psMap.get(dto.getPs()).getCodeName()+"不能重复做！"));
                }
            }
        }

        return errorList;
    }
}
