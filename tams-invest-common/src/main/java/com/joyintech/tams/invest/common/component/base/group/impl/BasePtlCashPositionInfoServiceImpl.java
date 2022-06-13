/*
 * 文件名：BasePtlCashPositionInfoServiceImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.component.base.group.impl;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.PtlCashPositionFlowMapper;
import com.joyintech.tams.common.base.dao.PtlCashPositionMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.PtlCashPosition;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlCashPositionInfoMapper;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 组合可用现金和实际现金查询更新组件实现类
 *
 * @author fanshuanghu
 * @version 1.0 2018年9月3日
 * @see BasePtlCashPositionInfoServiceImpl
 * @since 1.0
 */
@Component
public class BasePtlCashPositionInfoServiceImpl implements BasePtlCashPositionInfoService {

    /**
     * 组合可用持仓和实际持仓查询更新组件Mapper
     */
    @Autowired
    private BasePtlCashPositionInfoMapper basePtlCashPositionInfoMapper;

    /**
     * 组合可用持仓和实际持仓查询更新组件系统生成Mapper
     */
    @Autowired
    private PtlCashPositionMapper ptlCashPositionMapper;

    /**
     * 组合持仓流水Mapper
     */
    @Autowired
    private PtlCashPositionFlowMapper ptlCashPositionFlowMapper;

    /**
     * 业务编号取得共通
     */
    @Autowired
    private BaseGetBussinessId baseGetBussinessIdimpl;

    /**
     * 资产信息Mapper
     */
    @Autowired
    private FinProductMapper finProductMapper;
    
    /**
     * 资产信息Mapper
     */
    @Autowired
    private FinProductAddMapper finProductAddMapper;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal queryUsableCashPosition(PtlCashPositionFlowDto positionQueryDto) {
        Assert.isTrue(positionQueryDto != null, "查询dto不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getPortfolioId()), "组合代码不能为空");
        Assert.notNull(positionQueryDto.getCdate(), "日期不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getCcy()), "币种不能为空");
        String holdingType = positionQueryDto.getHodingType();
        positionQueryDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        positionQueryDto.setNeedAcctType(true);
        BigDecimal 可用现金 = basePtlCashPositionInfoMapper.selectCashPositionFlow(positionQueryDto);
        positionQueryDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        BigDecimal 实际现金 = queryOtherCashPosition(positionQueryDto);
        positionQueryDto.setHodingType(holdingType);
        return ArithmeticUtil.doAdd(可用现金, 实际现金);
    }

    /**
     * 查询账户可取余额
     */
    @Override
    public BigDecimal queryUsableCashPositionOther(PtlCashPositionFlowDto positionQueryDto) {
        Assert.isTrue(positionQueryDto != null, "查询dto不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getPortfolioId()), "组合代码不能为空");
        Assert.notNull(positionQueryDto.getCdate(), "日期不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getCcy()), "币种不能为空");
        String holdingType = positionQueryDto.getHodingType();
        positionQueryDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        positionQueryDto.setNeedAcctType(true);
        BigDecimal 可用现金 = basePtlCashPositionInfoMapper.selectCashPositionFlow(positionQueryDto);
        positionQueryDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        BigDecimal 实际现金 = queryOtherCashPosition(positionQueryDto);
        positionQueryDto.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
        BigDecimal 不可支取现金 = queryOtherCashPosition(positionQueryDto);
        不可支取现金 = 不可支取现金 == null ? BigDecimal.ZERO : 不可支取现金;
        不可支取现金 = 不可支取现金.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : 不可支取现金;
        positionQueryDto.setHodingType(holdingType);
        return ArithmeticUtil.subtract(ArithmeticUtil.doAdd(可用现金, 实际现金), 不可支取现金);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
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
        Date maxCdate = basePtlCashPositionInfoMapper.selectCashPositionMaxCdate(positionQueryDto);
        positionQueryDto.setCdate(maxCdate);

        return basePtlCashPositionInfoMapper.selectCashPositionNew(positionQueryDto);
    }

    /**BaseTradeProductDealCommService
     * {@inheritDoc}
     */
    @Override
    public boolean insertCashPositionFlow(PtlCashPositionFlowDto positionFlowDto) {
        Assert.notNull(positionFlowDto, "参数不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getPortfolioId()), "组合代码不能为空");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getAcctId()), "账号不能为空");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getHodingType()), "持仓类型不能为空");
        Assert.notNull(positionFlowDto.getCdate(), "日期不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getCcy()), "币种不能为空");
        Assert.notNull(positionFlowDto.getAmount(), "数量不能为null");

        // 2.1.1 调用业务编号共通组件获取流水号
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setSeqNo("9999");
        String seqNo = baseGetBussinessIdimpl.buildBussinessCode(parameter);

        // 2.1.2 组合持仓流水数据插入
        positionFlowDto.setSeqno(seqNo);
        ptlCashPositionFlowMapper.insert(positionFlowDto);

        // 2.2 输入参数.持仓类型 IN (数据字典缓存.持仓类型.实际, 数据字典缓存.持仓类型.在途)
        if (DictCodeList.持仓类型_实际.getValue().equals(positionFlowDto.getHodingType())
                || DictCodeList.持仓类型_在途.getValue().equals(positionFlowDto.getHodingType())
                || DictCodeListExt1.持仓类型_不可支取.getValue().equals(positionFlowDto.getHodingType())) {
            PtlCashPosition position = new PtlCashPosition();
            BeanUtils.copyProperties(positionFlowDto, position);
            positionFlowDto.setNeedAcctType(false);
            BigDecimal amount = basePtlCashPositionInfoMapper.selectCashPosition(positionFlowDto);
            if (amount == null) {
                // 如果当天的持仓数据没有，取当天之前最近一天的数据
                // 查询最近的那一天
                Date maxCdate = basePtlCashPositionInfoMapper.selectCashPositionMaxCdate(positionFlowDto);
                if (maxCdate == null) {
                    // modify by etliu 汇总表更新并发问题解决 20210511 start
                    try {
                        ptlCashPositionMapper.insert(position);
                    } catch (Exception e) {
                        basePtlCashPositionInfoMapper.updateByPrimaryKeySelectiveForFlow(position);
                    }
                    // modify by etliu 汇总表更新并发问题解决 20210511 end
                }
                else {
                    PtlCashPositionFlowDto tempPositionFlowDto = new PtlCashPositionFlowDto();
                    BeanUtils.copyProperties(positionFlowDto, tempPositionFlowDto);
                    tempPositionFlowDto.setCdate(maxCdate);

                    BigDecimal tempAmount = basePtlCashPositionInfoMapper.selectCashPosition(tempPositionFlowDto);
                    // modify by etliu 汇总表更新并发问题解决 20210511 start
                    BigDecimal flowAmount = position.getAmount();
                    position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), tempAmount));
                    try {
                        ptlCashPositionMapper.insert(position);
                    } catch (Exception e) {
                        position.setAmount(flowAmount);
                        basePtlCashPositionInfoMapper.updateByPrimaryKeySelectiveForFlow(position);
                    }
                    // modify by etliu 汇总表更新并发问题解决 20210511 end
                }
            } else {
                // modify by etliu 汇总表更新并发问题解决 20210511 start
//                position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), amount));
//                ptlCashPositionMapper.updateByPrimaryKeySelective(position);
                basePtlCashPositionInfoMapper.updateByPrimaryKeySelectiveForFlow(position);
                // modify by etliu 汇总表更新并发问题解决 20210511 end

            }

            // 2.2.2 检索组合持仓的最大日期
            String maxCdate = basePtlCashPositionInfoMapper.selectMaxCdate(positionFlowDto);
            String cDate = DateUtil.format(positionFlowDto.getCdate());
            // 2.2.3 上面检索出的最大日期 <> 输入参数.日期，将输入参数.日期到2.2.2检索出来的最大日期的数据全部更新
            if (maxCdate != null && !maxCdate.equals(cDate)) {
                String minDate = maxCdate.compareTo(cDate) > 0 ? DateUtil.addDay(cDate, 1) : maxCdate;
                String maxDate = maxCdate.compareTo(cDate) < 0 ? DateUtil.addDay(cDate, -1) : maxCdate;
                Map<String, Object> params = BeanUtil.object2Map(positionFlowDto);
                params.put("minDate", minDate);
                params.put("maxDate", maxDate);
                basePtlCashPositionInfoMapper.updatePosition(params);
            }
            // 2.2.4 if 输入参数.持仓类型 = 数据字典缓存.持仓类型.实际
            if (DictCodeList.持仓类型_实际.getValue().equals(positionFlowDto.getHodingType())
                    && !DictCodeList.是否_否.getValue().equals(positionFlowDto.getIsDeleteAble())) {
                positionFlowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
                deleteCashPosition(positionFlowDto);
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteCashPosition(PtlCashPositionFlowDto positionQueryDto) {
        int count = basePtlCashPositionInfoMapper.deletePtlCashPositionFlow(positionQueryDto);
        return count>0;
    }
    private PtlCashPositionFlowDto getPtlCashPosition(PtlCashPositionFlowDto positionFlowDto) {
        PtlCashPositionFlowDto position = new PtlCashPositionFlowDto();
        position.setAmount(positionFlowDto.getAmount());
        position.setCcy(positionFlowDto.getCcy());
        position.setPortfolioId(positionFlowDto.getPortfolioId()); 
        position.setFinprodId(positionFlowDto.getFinprodId()); 
        position.setHodingType(positionFlowDto.getHodingType());
        position.setCdate(positionFlowDto.getCdate());
        position.setStatus(positionFlowDto.getStatus());
        position.setCreateTime(positionFlowDto.getCreateTime());
        position.setUpdateUser(positionFlowDto.getUpdateUser());
        position.setUpdateTime(positionFlowDto.getUpdateTime());
        position.setWorkflowId(positionFlowDto.getWorkflowId());
        position.setCreateUser(positionFlowDto.getCreateUser());
        position.setCreateDept(positionFlowDto.getCreateDept());
        return position;
    }
    
    /**
     * 获取下一个工作日
     * @param tradeMarketId
     * @param cdate
     * @param finprodId
     * @return
     */
    public Date selectNextWorkDay(String tradeMarketId, Date cdate, String finprodId) {
    	Map<String,Object> map = new HashMap<String,Object>();
    	//资产交易不传的话，通过资产信息获取
    	if (StringUtil.isEmpty(tradeMarketId)) {
    		FinProduct fin = finProductMapper.selectByPrimaryKey(finprodId);
    		tradeMarketId = fin.getCalendarId();
    	}
    	map.put("tradeMarketId", tradeMarketId);
    	map.put("cdate", cdate);
    	Date rstDate = basePtlCashPositionInfoMapper.selectNextWorkDay(map);
    	return rstDate == null ? cdate : rstDate;
    }
    
    /**
     * 查询资产附加信息，查询产品是否纳入净额结算
     * @param finprodId
     * @return
     */
    public FinProductAdd getFinProductAdd(String finprodId) {
    	return finProductAddMapper.selectByPrimaryKey(finprodId);
    }
    @Override
    public BigDecimal queryTrdAvailableCashNew(PtlCashPositionFlowDto positionQueryDto) {
        Assert.isTrue(positionQueryDto != null, "查询dto不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getPortfolioId()), "组合代码不能为空");
        Assert.notNull(positionQueryDto.getCdate(), "日期不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getCcy()), "币种不能为空");
        return basePtlCashPositionInfoMapper.queryTrdAvailableCashNew(positionQueryDto);
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal queryActCashPosition(PtlCashPositionFlowDto positionQueryDto) {
        Assert.isTrue(positionQueryDto != null, "查询dto不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getPortfolioId()), "组合代码不能为空");
        Assert.notNull(positionQueryDto.getCdate(), "日期不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getCcy()), "币种不能为空");
        String holdingType = positionQueryDto.getHodingType();
        positionQueryDto.setNeedAcctType(true);
        positionQueryDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        BigDecimal 实际现金 = queryOtherCashPosition(positionQueryDto);
        positionQueryDto.setHodingType(holdingType);
        return 实际现金;
    }
}
