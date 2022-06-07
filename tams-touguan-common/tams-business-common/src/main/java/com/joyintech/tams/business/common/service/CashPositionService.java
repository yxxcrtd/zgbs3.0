package com.joyintech.tams.business.common.service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CashPositionFlowDto;
import com.joyintech.tams.business.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CashPositionService {

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;
    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    public BigDecimal queryUsableCashPosition(PtlCashPositionFlowDto positionQueryDto) {
        Assert.isTrue(positionQueryDto != null, "查询dto不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getPortfolioId()), "组合代码不能为空");
        Assert.notNull(positionQueryDto.getCdate(), "日期不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getCcy()), "币种不能为空");
        String holdingType = positionQueryDto.getHodingType();
        positionQueryDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        positionQueryDto.setNeedAcctType(true);
        BigDecimal 可用现金 = joyinbusinessCommonMapper.selectCashPositionFlow(positionQueryDto);
        positionQueryDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        BigDecimal 实际现金 = queryOtherCashPosition(positionQueryDto);
        positionQueryDto.setHodingType(holdingType);
        return ArithmeticUtil.doAdd(可用现金, 实际现金);
    }

    public BigDecimal queryOtherCashPosition(PtlCashPositionFlowDto positionQueryDtoParam) {
        PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
        BeanUtil.copyPropertiesIgnoreNull(positionQueryDto, positionQueryDtoParam);

        Assert.isTrue(positionQueryDto != null, "查询dto不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getPortfolioId()), "组合代码不能为空");
        Assert.notNull(positionQueryDto.getCdate(), "日期不能为null");
        Assert.notNull(positionQueryDto.getHodingType(), "持仓类型不能为null，支持：持仓类型.在途、持仓类型.实际");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getCcy()), "币种不能为空");
        positionQueryDto.setNeedAcctType(true);

        // 查询最大CDATE
        Date maxCdate = joyinbusinessCommonMapper.selectCashPositionMaxCdate(positionQueryDto);
        positionQueryDto.setCdate(maxCdate);

        return joyinbusinessCommonMapper.selectCashPositionNew(positionQueryDto);
    }
    
    public BigDecimal trdQryCommandAvailableCash(Map<String, Object> param){
        BigDecimal cashT = BigDecimal.ZERO;
        PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
        positionQueryDto.setPortfolioId((String)param.get("portfolioId"));
        positionQueryDto.setCcy((String)param.get("ccy"));
        positionQueryDto.setCdate((Date) param.get("vdate"));
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        positionQueryDto.setAcctType(acctTypeList);
        // 实际现金（账户现金）
        cashT = ArithmeticUtil.doAdd(cashT, queryUsableCashPosition(positionQueryDto));
        return cashT;
    }
    /**
     * 查询指令可用现金
     *
     * @param dto
     * @return
     */
    public BigDecimal etrQryCommandAvailableCash(Map<String, Object> param){
        BigDecimal cashT = BigDecimal.ZERO;
        
        CashPositionFlowDto dto = new CashPositionFlowDto();
        List<String> acctTypeList = new ArrayList<>();
        dto.setAcctType(acctTypeList);

        dto.setCdate((Date) param.get("vdate"));
        dto.setPortfolioId((String)param.get("portfolioId"));
        dto.setCcy((String)param.get("ccy"));

        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        BigDecimal cashT3 = qryCommandAvailableCash(dto);
        cashT = ArithmeticUtil.doAdd(cashT, cashT3 == null ? BigDecimal.ZERO : cashT3);

        acctTypeList.clear();
        acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        BigDecimal cashT4 = qryCommandAvailableCash(dto);
        cashT = ArithmeticUtil.doAdd(cashT, cashT4 == null ? BigDecimal.ZERO : cashT4);
        
        return cashT;
    }

    /**
     * 按账户类型依次查持仓类型_委托指令可用、持仓类型_委托指令预增、持仓类型_人工调整
     * @param dto
     * @return
     */
    private BigDecimal qryCommandAvailableCash(CashPositionFlowDto dto) {
        BigDecimal val = BigDecimal.ZERO;
        dto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        BigDecimal availableCash = joyinbusinessCommonMapper.selectCommandAvailableCash(dto);
        
        dto.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
        BigDecimal advanceCash =  joyinbusinessCommonMapper.selectCommandAvailableCash(dto);
        
        dto.setHodingType(DictCodeListExt1.持仓类型_人工调整.getValue());
        BigDecimal 人工调整流水 = joyinbusinessCommonMapper.selectManualCashFlow(dto);
        
        if (availableCash != null) {
            val = ArithmeticUtil.doAdd(val, availableCash);
        }
        if (advanceCash != null) {
            val = ArithmeticUtil.doAdd(val, advanceCash);
        }
        if (availableCash == null && advanceCash == null){
            return null;
        }
        return ArithmeticUtil.doAdd(val, 人工调整流水);
    }
}
