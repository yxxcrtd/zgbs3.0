package com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.validator;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.dto.ExchangeTransMatch002Dto;
import com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.dto.ExchangeTransMatch003Dto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExchangeTransMatchValidator implements BeanValidator {
    @Override
    public List<ValidMessage> valid(Object entity) {
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
        ExchangeTransMatch002Dto dealDto = (ExchangeTransMatch002Dto) entity;
        if(StringUtil.equals(dealDto.getElementId(),"transaction_macth")) {

            ExchangeTransMatch003Dto entrustDto = dealDto.getEntrustList().get(0);
            //比较左右的资产类型
            if(!StringUtil.equals(dealDto.getSecType(),entrustDto.getSecType())) {
                errorList.add(new ValidMessage("组成交数据的资产类型和委托或交易的资产类型不一致!"));
            }

            // ZGTFCP-1133:增加协议回购匹配数量校验 modify by fuyongsheng 20220317 start
//            if(!StringUtil.isEmptyList(dealDto.getEntrustList()) && dealDto.getEntrustList().size()>1
//            && (StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) || StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
//            && StringUtil.equals(dealDto.getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())) {
//                errorList.add(new ValidMessage("协议回购时只能选择一条指令进行匹配!"));
//            }
            if((StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) || StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
                    && StringUtil.equals(dealDto.getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())){
                if(!StringUtil.isEmptyList(dealDto.getEntrustList()) && dealDto.getEntrustList().size()>1){
                    errorList.add(new ValidMessage("协议回购时只能选择一条指令进行匹配!"));
                }else if(!StringUtil.isEmptyList(dealDto.getEntrustList()) && dealDto.getEntrustList().size()==1){
                    if(dealDto.getRemainNum().compareTo(dealDto.getEntrustList().get(0).getEntrustAmt())>0){
                        errorList.add(new ValidMessage("不符合协议回购匹配规则!"));
                    }
                }
            }
            // ZGTFCP-1133:增加协议回购匹配数量校验 modify by fuyongsheng 20220317 end

            //比较基本信息
            String ps = dealDto.getPs();
            if(StringUtil.equals(dealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
            && StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                ps = DictCodeList.回购拆借方向_正回购.getValue();
            }
            else if (StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
            && StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                ps = DictCodeList.回购拆借方向_逆回购.getValue();
            }
            if(StringUtil.equals(ps,DictCodeList.交易方向_到期.getValue())) {
                ps = DictCodeListExt3.交易方向_到期购回.getValue();
            }
            else if (StringUtil.equals(ps,DictCodeListExt3.交易方向_投资端_提前终止.getValue())) {
                ps = DictCodeListExt3.交易方向_提前购回.getValue();
            }

            // GNS-902:特殊场景不加日期条件查询指令 modify by fuyongsheng 20220224 start
            if(com.joyintech.framework.util.StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(),ps)
                    || (com.joyintech.framework.util.StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), dealDto.getFinprodType2())
                    && com.joyintech.framework.util.StringUtil.equals(DictCodeList.交易方向_申购.getValue(), ps)
            )
                    || (com.joyintech.framework.util.StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), dealDto.getFinprodType2())
                    && com.joyintech.framework.util.StringUtil.equals(DictCodeList.交易方向_申购.getValue(), ps)
            )
                    || com.joyintech.framework.util.StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(), ps)
                    || com.joyintech.framework.util.StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(), ps)
            ){
                if(!StringUtil.equals(dealDto.getSecAcctId(),entrustDto.getSecAcctId())
                        || !StringUtil.equals(dealDto.getAcctNo(),entrustDto.getAcctNo())
                        || !StringUtil.equals(dealDto.getFinprodId(),entrustDto.getFinprodId())
                        || !StringUtil.equals(ps,entrustDto.getPs())
                        // || !StringUtil.equals(dealDto.getTradePlat(),entrustDto.getTradePlat())
                ) {
                    errorList.add(new ValidMessage("成交数据的基本信息和委托或交易的基本信息不一致"));
                }
            }else{
                if(!StringUtil.equals(dealDto.getSecAcctId(),entrustDto.getSecAcctId())
                        || !StringUtil.equals(dealDto.getAcctNo(),entrustDto.getAcctNo())
                        || !StringUtil.equals(dealDto.getFinprodId(),entrustDto.getFinprodId())
                        || !StringUtil.equals(ps,entrustDto.getPs())
                        || !StringUtil.equals(DateUtil.format(dealDto.getTradeDate()),DateUtil.format(entrustDto.getEntrustDate()))
                        // || !StringUtil.equals(dealDto.getTradePlat(),entrustDto.getTradePlat())
                ) {
                    errorList.add(new ValidMessage("成交数据的基本信息和委托或交易的基本信息不一致"));
                }
            }
            // GNS-902:特殊场景不加日期条件查询指令 modify by fuyongsheng 20220224 end
        }
        else if (StringUtil.equals(dealDto.getElementId(),"transaction_record")) {
            if(!StringUtil.equals(dealDto.getMatchStatus(), DictCodeListExt3.下行交易匹配状态_未匹配.getValue())
            && !StringUtil.equals(dealDto.getMatchStatus(), DictCodeListExt3.下行交易匹配状态_部分匹配.getValue())) {
                errorList.add(new ValidMessage("请选择未匹配或者部分匹配的数据"));
            }
            if(((StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
                    && StringUtil.equals(dealDto.getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue()))
               || (StringUtil.equals(dealDto.getPs(),DictCodeList.交易方向_申购.getValue()))) {
                errorList.add(new ValidMessage("暂不支持此类业务成交录单"));
            }



        }

        return errorList;
    }
}
