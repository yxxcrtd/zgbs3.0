package com.joyintech.tams.cfets.risk.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.risk.dto.CfetsEtrRiskcalDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubEntrustDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CalcPriceAndYieldResponse;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CfetsEtrRdsCommonService {

    /**
     * 指令信息
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private FinProductMapper finProductMapper;

    public String doRiskcalCheckMethod(Object obj){
        //参数不能为空
        if (obj instanceof CfetsTrdBondFirstSubInfoDto) {
            CfetsTrdBondFirstSubInfoDto dto = (CfetsTrdBondFirstSubInfoDto)obj;
            if (StringUtil.isEmptyList(dto.getEntrustDtos())) {
                return "组合分配列表不能为空";
            }
            List<String> messages = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getEntrustDtos())) {
                for (CfetsTrdBondFirstSubEntrustDto item : dto.getEntrustDtos()) {
                    if (!StringUtil.isNotEmpty(item.getPortfolioId())) {
                        messages.add("请输入组合名称。");
                    }
                    if (!StringUtil.isNotEmpty(item.getEntrustNum())) {
                        messages.add("请输入券面总额。");
                    }
                }
            }
            if (!StringUtil.isNotEmpty(dto.getFinprodId())) {
                messages.add("请输入资产代码。");
            }
            if (messages.size() > 0) {
                return StringUtil.joinString(messages, GlobalConstant.VERTICAL_LINE);
            }
        }
        return null;
    }

    /**
     *风险试算时，后台数据转换
     * @return
     */
    public Map<String, Object> doRiskcalPackageEquity(Object obj) {
        Map<String, Object> dataMap=new HashMap<>();
        if (obj instanceof CfetsTrdBondFirstSubInfoDto) {
            CfetsTrdBondFirstSubInfoDto dto = (CfetsTrdBondFirstSubInfoDto)obj;
            if (StringUtil.isNotEmpty(dto.getFinprodId())){
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
                if (!StringUtil.isNotEmpty(finProduct.getCirculationCode())){
                    return null;
                }
                if (StringUtil.equals( finProduct.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())){
                    dto.setFinprodId("F01_"+finProduct.getCirculationCode());
                }
            }
            //风险试算封装dto
            CfetsEtrRiskcalDto d = new CfetsEtrRiskcalDto();
            List<Map> bussTrdTransfenpeiDto = new ArrayList<>();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            for (CfetsTrdBondFirstSubEntrustDto pt : dto.getEntrustDtos()) {
                etrEntrust = etrEntrustMapper.selectByPrimaryKey(pt.getEntrustId());
                etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(pt.getEntrustId());
                bussTrdTransfenpeiDto.add(BeanUtil.object2Map(etrEntrust));
                bussTrdTransfenpeiDto.add(BeanUtil.object2Map(etrEntrustAdd));
                bussTrdTransfenpeiDto.add(BeanUtil.object2Map(pt));
            }
            BeanUtil.copyPropertiesIgnoreNull(d, dto);
            d.setBussTrdTransfenpeiDto(bussTrdTransfenpeiDto);
            d.setBusiType("02");
            d.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
            d.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            d.setVdate(dto.getTradeDate());
            d.setMdate(dto.getTradeDate());
            d.setCcy("CNY");
            d.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            dataMap = BeanUtil.object2Map(d);
        }
        return dataMap;
    }

    public String getKriStatusCnByKriStatus(String kriStatus){
        if(StringUtil.isNotEmpty(kriStatus)){
            if(StringUtil.equals(kriStatus,"0")){
                return "正常";
            }else if(StringUtil.equals(kriStatus,"1")){
                return "预警";
            }else  if(StringUtil.equals(kriStatus,"2")){
                return "审批";
            }else  if(StringUtil.equals(kriStatus,"3")){
                return "违规";
            }else  if(StringUtil.equals(kriStatus,"4")){
                return "异常";
            }
        }
        return null;
    }
}
