/*
 * 文件名：BasePtlSecPositionInfoServiceImpl.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：huwei
 * 修改时间：2018年8月28日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.component.base.group.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.PtlSecPositionDetailMapper;
import com.joyintech.tams.common.base.dao.PtlSecPositionFlowMapper;
import com.joyintech.tams.common.base.dao.PtlSecPositionMapper;
import com.joyintech.tams.common.base.entity.PtlSecPosition;
import com.joyintech.tams.common.base.entity.PtlSecPositionDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlSecPositionInfoMapper;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;

/**
 * 组合可用持仓和实际持仓查询更新组件实现类
 *
 * @author huwei
 * @version 1.0 2018年9月31日
 * @see BasePtlSecPositionInfoServiceImpl
 * @since 1.0
 */
@Service
public class BasePtlSecPositionInfoServiceImpl implements BasePtlSecPositionInfoService {

    /**
     * 组合可用持仓和实际持仓查询更新组件Mapper
     */
    @Autowired
    private BasePtlSecPositionInfoMapper basePtlSecPositionInfoMapper;

    /**
     * 组合可用持仓和实际持仓查询更新组件系统生成Mapper
     */
    @Autowired
    private PtlSecPositionMapper ptlSecPositionMapper;

    /**
     * 组合持仓流水Mapper
     */
    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    /**
     * 业务编号取得共通
     */
    @Autowired
    private BaseGetBussinessId baseGetBussinessIdimpl;

    /**
     * 组合持仓融入融出明细系统生成Mapper
     */
    @Autowired
    private PtlSecPositionDetailMapper ptlSecPositionDetailMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal queryUsableSecPosition(PtlSecPositionFlowDto positionQueryDto) {
        Assert.isTrue(positionQueryDto != null, "查询dto不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getPortfolioId()), "组合代码不能为空");
        Assert.notNull(positionQueryDto.getCdate(), "日期不能为null");
        String holdingType = positionQueryDto.getHodingType();

        positionQueryDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        BigDecimal 可用持仓 = basePtlSecPositionInfoMapper.selectSecPositionFlow(positionQueryDto);

        positionQueryDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        BigDecimal 实际持仓 = queryOtherSecPosition(positionQueryDto);

        positionQueryDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
        BigDecimal 质押持仓 = queryOtherSecPosition(positionQueryDto);

        String invAim = positionQueryDto.getInvAim();
        positionQueryDto.setInvAim("999999");
        positionQueryDto.setHodingType(DictCodeList.持仓类型_融入.getValue());
        BigDecimal 融入持仓 = queryOtherSecPosition(positionQueryDto);

        positionQueryDto.setHodingType(DictCodeList.持仓类型_融出.getValue());
        BigDecimal 融出持仓 = queryOtherSecPosition(positionQueryDto);
        positionQueryDto.setInvAim(invAim);
        BigDecimal 融出持仓带投资目的 = null;
        if (!"999999".equals(invAim) ) {//&& StringUtil.isNotEmpty(invAim) 剔除该条件
            融出持仓带投资目的 = queryOtherSecPosition(positionQueryDto);
        }
        // - 卖空-卖空可用（流水表）+预占用
        positionQueryDto.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
        positionQueryDto.setHodingType(DictCodeListExt1.持仓类型_卖空.getValue());
        BigDecimal 卖空 = queryOtherSecPosition(positionQueryDto);

        positionQueryDto.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
        BigDecimal 卖空可用 = basePtlSecPositionInfoMapper.selectSecPositionFlow(positionQueryDto);

        positionQueryDto.setInvAim(invAim);
        positionQueryDto.setHodingType(holdingType);
        positionQueryDto.setHodingType(DictCodeListExt1.持仓类型_预占用.getValue());
        BigDecimal 预占用 = basePtlSecPositionInfoMapper.selectSecPositionFlow(positionQueryDto);
        // return ArithmeticUtil.subtract(ArithmeticUtil.doAdd(可用持仓, 实际持仓), 质押持仓);

        BigDecimal result = ArithmeticUtil.doAdd(可用持仓, 实际持仓, 融入持仓, 预占用);
        result = ArithmeticUtil.subtract(ArithmeticUtil.subtract(result, 质押持仓), 融出持仓);
        result = ArithmeticUtil.subtract(ArithmeticUtil.subtract(result, 卖空), 卖空可用);
        if (融出持仓带投资目的 != null) {
            result = ArithmeticUtil.subtract(result, 融出持仓带投资目的);
        }
        // result = ArithmeticUtil.subtract(result, 融出持仓);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal queryOtherSecPosition(PtlSecPositionFlowDto positionQueryDtoParam) {
        Assert.isTrue(positionQueryDtoParam != null, "查询dto不能为null");

        PtlSecPositionFlowDto positionQueryDto = new PtlSecPositionFlowDto();
        BeanUtil.copyPropertiesIgnoreNull(positionQueryDto, positionQueryDtoParam);

        Assert.isTrue(positionQueryDto != null, "查询dto不能为null");
        if (StringUtil.isNotEmpty(positionQueryDto.getPortfolioId())) {
            Assert.isTrue(StringUtil.isNotEmpty(positionQueryDto.getPortfolioId()), "组合代码不能为空");
            Assert.notNull(positionQueryDto.getCdate(), "日期不能为null");
            Assert.notNull(positionQueryDto.getHodingType(), "持仓类型不能为null，支持：持仓类型.在途、持仓类型.实际、持仓类型.质押");

            // 查询最大日期
            Date maxCdate = basePtlSecPositionInfoMapper.selectSecPositionMaxCdate(positionQueryDto);
            positionQueryDto.setCdate(maxCdate);
            if (maxCdate == null) {
                return BigDecimal.ZERO;
            }
            return basePtlSecPositionInfoMapper.selectSecPosition(positionQueryDto);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inertSecPositionFlow(PtlSecPositionFlowDto positionFlowDto) {
        Assert.notNull(positionFlowDto, "参数不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getPortfolioId()), "组合代码不能为空");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getFinprodId()), "金融产品代码不能为空");
        Assert.notNull(positionFlowDto.getBranch(), "分支序号不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getHodingType()), "持仓类型不能为空");
        Assert.notNull(positionFlowDto.getCdate(), "日期不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getCcy()), "币种不能为空");
        Assert.notNull(positionFlowDto.getAmount(), "数量不能为null");
        Assert.isTrue(StringUtil.isNotEmpty(positionFlowDto.getTradeId()), "交易编号不能为空");

        // 2.1.1 调用业务编号共通组件获取流水号
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setSeqNo("9999");
        String seqNo = baseGetBussinessIdimpl.buildBussinessCode(parameter);

        // 2.1.2 组合持仓流水数据插入
        positionFlowDto.setSeqno(seqNo);
        ptlSecPositionFlowMapper.insert(positionFlowDto);

        // 2.2 输入参数.持仓类型 IN (数据字典缓存.持仓类型.实际, 数据字典缓存.持仓类型.在途)
        if (DictCodeList.持仓类型_实际.getValue().equals(positionFlowDto.getHodingType())
            || DictCodeList.持仓类型_在途.getValue().equals(positionFlowDto.getHodingType())
            || DictCodeList.持仓类型_质押.getValue().equals(positionFlowDto.getHodingType())
            || DictCodeList.持仓类型_融入.getValue().equals(positionFlowDto.getHodingType())
            || DictCodeListExt1.持仓类型_卖空.getValue().equals(positionFlowDto.getHodingType())
            || DictCodeList.持仓类型_融出.getValue().equals(positionFlowDto.getHodingType())
            //增加持仓类型_场内上市前冻结 modify by fuyongsheng 20220111 start
            || DictCodeListExt3.持仓类型_场内上市前冻结.getValue().equals(positionFlowDto.getHodingType())
            //增加持仓类型_场内上市前冻结 modify by fuyongsheng 20220111 end
        ) {
            PtlSecPosition position = getPtlSecPosition(positionFlowDto);
            BigDecimal amount = basePtlSecPositionInfoMapper.selectSecPosition(positionFlowDto);
            if (amount == null) {
                // 如果当天的持仓数据没有，取当天之前最近一天的数据
                // 查询最近的那一天
                Date maxCdate = basePtlSecPositionInfoMapper.selectSecPositionMaxCdate(positionFlowDto);
                if (maxCdate == null) {
                    // modify by etliu 汇总表更新并发问题解决 20210511 start
                    try {
                        ptlSecPositionMapper.insertSelective(position);
                    } catch (Exception e) {
                        basePtlSecPositionInfoMapper.updateByPrimaryKeySelectiveForFlow(position);
                    }
                    // modify by etliu 汇总表更新并发问题解决 20210511 end
                }
                else {
                    PtlSecPositionFlowDto tempPositionFlowDto = new PtlSecPositionFlowDto();
                    BeanUtils.copyProperties(positionFlowDto, tempPositionFlowDto);
                    tempPositionFlowDto.setCdate(maxCdate);

                    BigDecimal tempAmount = basePtlSecPositionInfoMapper.selectSecPosition(tempPositionFlowDto);
                    // modify by etliu 汇总表更新并发问题解决 20210511 start
                    BigDecimal flowAmount = position.getAmount();
                    position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), tempAmount));
                    try {
                        ptlSecPositionMapper.insertSelective(position);
                    } catch (Exception e) {
                        position.setAmount(flowAmount);
                        basePtlSecPositionInfoMapper.updateByPrimaryKeySelectiveForFlow(position);
                    }
                    // modify by etliu 汇总表更新并发问题解决 20210511 end
                }
            }
            else {
                // modify by etliu 汇总表更新并发问题解决 20210511 start
//                position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), amount));
//                ptlSecPositionMapper.updateByPrimaryKeySelective(position);
                basePtlSecPositionInfoMapper.updateByPrimaryKeySelectiveForFlow(position);
                // modify by etliu 汇总表更新并发问题解决 20210511 end

            }

            // 2.2.2 检索组合持仓的最大日期
            String maxCdate = basePtlSecPositionInfoMapper.selectMaxCdate(positionFlowDto);
            String cDate = DateUtil.format(positionFlowDto.getCdate());
            // 2.2.3 上面检索出的最大日期 <> 输入参数.日期，将输入参数.日期到2.2.2检索出来的最大日期的数据全部更新
            if (maxCdate != null && !maxCdate.equals(cDate)) {
                String minDate = maxCdate.compareTo(cDate) > 0 ? DateUtil.addDay(cDate, 1) : maxCdate;
                String maxDate = maxCdate.compareTo(cDate) < 0 ? DateUtil.addDay(cDate, -1) : maxCdate;
                Map<String, Object> params = BeanUtil.object2Map(positionFlowDto);
                params.put("minDate", minDate);
                params.put("maxDate", maxDate);
                basePtlSecPositionInfoMapper.updatePosition(params);
            }
            // 2.2.4 if 参数.持仓类型=数据字典缓存.持仓类型.实际
            if (DictCodeList.持仓类型_实际.getValue().equals(positionFlowDto.getHodingType())) {
                // 输入参数.前置交易编号不为null或者空的时候，输入参数.前置交易编号。否则，输入参数.交易编号
                positionFlowDto.setTradeId(StringUtil.isNotEmpty(
                    positionFlowDto.getbTradeId()) ? positionFlowDto.getbTradeId() : positionFlowDto.getTradeId());
                positionFlowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
                deleteSecPosition(positionFlowDto);
            }
            else if (DictCodeListExt1.持仓类型_卖空.getValue().equals(positionFlowDto.getHodingType())) {
                positionFlowDto.setTradeId(StringUtil.isNotEmpty(
                        positionFlowDto.getbTradeId()) ? positionFlowDto.getbTradeId() : positionFlowDto.getTradeId());
                positionFlowDto.setHodingType(DictCodeListExt1.持仓类型_卖空可用.getValue());
                deleteSecPosition(positionFlowDto);
            }
        }
        //华兴银行：组合持仓融入融出明细共通 added by liuwei 2020/1/9 S
        if (DictCodeList.持仓类型_融入.getValue().equals(positionFlowDto.getHodingType())
                || DictCodeList.持仓类型_融出.getValue().equals(positionFlowDto.getHodingType())) {
            PtlSecPositionDetail position = getPtlSecPositionDetail(positionFlowDto);
            BigDecimal amount = basePtlSecPositionInfoMapper.selectSecPositionDetail(positionFlowDto);
            if (amount == null) {
                // 如果当天的持仓数据没有，取当天之前最近一天的数据
                // 查询最近的那一天
                Date maxCdate = basePtlSecPositionInfoMapper.selectSecPositionDetailMaxCdate(positionFlowDto);
                if (maxCdate == null) {
                    ptlSecPositionDetailMapper.insertSelective(position);
                }
                else {
                    PtlSecPositionFlowDto tempPositionFlowDto = new PtlSecPositionFlowDto();
                    BeanUtils.copyProperties(positionFlowDto, tempPositionFlowDto);
                    tempPositionFlowDto.setCdate(maxCdate);
                    BigDecimal tempAmount = basePtlSecPositionInfoMapper.selectSecPositionDetail(tempPositionFlowDto);
                    position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), tempAmount));
                    ptlSecPositionDetailMapper.insertSelective(position);
                }
            } else {
                position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), amount));
                ptlSecPositionDetailMapper.updateByPrimaryKeySelective(position);
            }
            // 2.2.2 检索组合持仓的最大日期
            String maxCdate = basePtlSecPositionInfoMapper.selectMaxCdateDetail(positionFlowDto);
            String cDate = DateUtil.format(positionFlowDto.getCdate());
            // 2.2.3 上面检索出的最大日期 <> 输入参数.日期，将输入参数.日期到2.2.2检索出来的最大日期的数据全部更新
            if (maxCdate != null && !maxCdate.equals(cDate)) {
                String minDate = maxCdate.compareTo(cDate) > 0 ? DateUtil.addDay(cDate, 1) : maxCdate;
                String maxDate = maxCdate.compareTo(cDate) < 0 ? DateUtil.addDay(cDate, -1) : maxCdate;
                Map<String, Object> params = BeanUtil.object2Map(positionFlowDto);
                params.put("minDate", minDate);
                params.put("maxDate", maxDate);
                basePtlSecPositionInfoMapper.updatePositionDetail(params);
            }
//            }
        }
        //华兴银行：组合持仓融入融出明细共通 added by liuwei 2020/1/9 E
        return true;
    }

    private PtlSecPosition getPtlSecPosition(PtlSecPositionFlowDto positionFlowDto) {
        PtlSecPosition position = new PtlSecPosition();
        position.setAmount(positionFlowDto.getAmount());
        position.setCcy(positionFlowDto.getCcy());
        position.setpFinprodId(positionFlowDto.getpFinprodId());
        position.setPortfolioId(positionFlowDto.getPortfolioId());
        position.setSecAcctId(positionFlowDto.getSecAcctId());
        position.setFinprodId(positionFlowDto.getFinprodId());
        position.setBranch(positionFlowDto.getBranch());
        position.setInvAim(positionFlowDto.getInvAim());
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
     * {@inheritDoc}
     */
    @Override
    public int deleteSecPosition(PtlSecPositionFlowDto positionQueryDto) {
        return basePtlSecPositionInfoMapper.deletePtlSecPositionFlow(positionQueryDto);
    }

    //华兴银行：组合持仓融入融出明细共通 added by liuwei 2020/1/9 S
    private PtlSecPositionDetail getPtlSecPositionDetail(PtlSecPositionFlowDto positionFlowDto) {
        PtlSecPositionDetail position = new PtlSecPositionDetail();
        position.setAmount(positionFlowDto.getAmount());
        position.setCcy(positionFlowDto.getCcy());
        position.setPortfolioId(positionFlowDto.getPortfolioId());
        position.setSecAcctId(positionFlowDto.getSecAcctId());
        position.setFinprodId(positionFlowDto.getFinprodId());
        position.setBranch(positionFlowDto.getBranch());
        position.setHodingType(positionFlowDto.getHodingType());
        position.setCdate(positionFlowDto.getCdate());
        position.setStatus(positionFlowDto.getStatus());
        position.setTradeId(positionFlowDto.getTradeId());
        position.setCreateTime(positionFlowDto.getCreateTime());
        position.setUpdateUser(positionFlowDto.getUpdateUser());
        position.setUpdateTime(positionFlowDto.getUpdateTime());
        position.setWorkflowId(positionFlowDto.getWorkflowId());
        position.setCreateUser(positionFlowDto.getCreateUser());
        position.setCreateDept(positionFlowDto.getCreateDept());
        return position;
    }
    //华兴银行：组合持仓融入融出明细共通 added by liuwei 2020/1/9 E
}
