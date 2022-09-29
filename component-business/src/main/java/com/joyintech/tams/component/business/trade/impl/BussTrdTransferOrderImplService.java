/*
 * 文件名：BussTrdTransferOrderImplService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年09月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.component.business.trade.impl;

import static com.joyintech.tams.component.business.settle.BussSettleOrderCommService.genOrderSelect;

import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;

import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.dao.TrdCommonBaseMapper;
import com.joyintech.tams.invest.common.entrust.dao.EtrTrdCommonMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.StlCapacctInfoMapper;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.common.base.entity.StlCapacctInfoKey;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderResultDto;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

@Service
public class BussTrdTransferOrderImplService extends BaseService<BussTrdTransferOrderDto> implements BussTrdTransferOrderService {

    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
    private BussSettleOrderCommService bussSettleOrderCommService;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    @Autowired
    private StlCapacctInfoMapper stlCapacctInfoMapper;

    @Autowired
    private TrdCommonBaseMapper trdCommonBaseMapper;

    @Autowired
    private EtrTrdCommonMapper etrTrdCommonMapper;
    /**
     * 主键生成类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 共通Mapper的通用代理方案
     */

    /**
     * 列表的name属性
     */
    public static final String TABLE_NAME = "bussTrdTransferOrder";

    @Override
    public List<ValidMessage> check(List<BussTrdTransferOrderDto> dtoList) {
        List<ValidMessage> ret = new ArrayList<>();
        if (StringUtil.isEmptyList(dtoList)) {
            ret.add(new ValidMessage(TABLE_NAME,
                getMessage("FW_E_0006", getMessage("BussTrdTransferOrder"))));
        }
        else {
            for (BussTrdTransferOrderDto dataDto : dtoList) {
                // 是否发送指令
                if (StringUtil.isBlank(dataDto.getIsNeedSend())) {
                    ret.add(new ValidMessage(TABLE_NAME, getMessage("FW_E_0006",
                        getMessage("BussTrdTransferOrder.isNeedSend"))));
                    break;
                }
                else if ((DictCodeList.是否_是.getValue().equals(dataDto.getIsNeedSend())
                        || DictCodeList.是否_是.getCodeName().equals(dataDto.getIsNeedSend()))
                        && (StringUtil.isEmpty(dataDto.getRecAcctNo()) || StringUtil.isEmpty(dataDto.getPayAcctNo()))) {
                    ret.add(new ValidMessage("是否发送指令是：是，收付款账户都不能为空。"));
                    break;
                }

                if (StringUtil.equals(dataDto.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
                    // 付款账号
                    if (StringUtil.isBlank(dataDto.getPayAcctNo())) {
                        ret.add(new ValidMessage(TABLE_NAME, getMessage("FW_E_0006",
                            getMessage("BussTrdTransferOrder.payAcctNo"))));
                        break;
                    }
                }
                else {
                    // 收款账号
                    if (StringUtil.isBlank(dataDto.getRecAcctNo())) {
                        ret.add(new ValidMessage(TABLE_NAME, getMessage("FW_E_0006",
                            getMessage("BussTrdTransferOrder.recAcctNo"))));
                        break;
                    }
                }
            }

        }
        return ret;
    }

    @Override
    public List<BussTrdTransferOrderDto> save(List<BussTrdTransferOrderDto> dto) {
        if(CollectionUtils.isNotEmpty(dto)){
            return dto.stream().map(item -> {
                BussTrdTransferOrderDto orders = new BussTrdTransferOrderDto();
                BeanUtil.copyPropertiesIgnoreNull(orders, item);
                orders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                if (StringUtil.equals(item.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
                    orders.setMyAcctNo(item.getPayAcctNo());
                    orders.setCounterAcctNo(item.getRecAcctNo());
                }
                else {
                    orders.setMyAcctNo(item.getRecAcctNo());
                    orders.setCounterAcctNo(item.getPayAcctNo());
                }
                if (StringUtil.isNotEmpty(orders.getIsNeedSend())) {
                    if (DictCodeList.是否_是.getCodeName().equals(orders.getIsNeedSend())) {
                        orders.setIsNeedSend(DictCodeList.是否_是.getValue());
                    }
                    else if (DictCodeList.是否_否.getCodeName().equals(orders.getIsNeedSend())) {
                        orders.setIsNeedSend(DictCodeList.是否_否.getValue());
                    }
                }
                return orders;
            }).collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * 指令生成
     *
     * @param param 参数
     * @return 指令
     * @see
     * @since 1.0
     */
    @Override
    public BussTrdTransferOrderDto generateOrder(BussSettleOrderParameterDto param) {
        BussSettleOrderResultDto dto = bussSettleOrderCommService.generateSettleOrder(param);
        BussTrdTransferOrderDto ret = new BussTrdTransferOrderDto();
        if (dto == null) {
            return null;
        }
        BeanUtil.copyPropertiesIgnoreNull(ret, dto);
        genOrderSelect(dto, ret);
        ret.setPortfolioId(param.getPortfolioId());
        //返回传入的投资目的
        if(param.getDeal()!=null){
        	ret.setInvAim(param.getDeal().getInvAim());
        }
        String mstSaleFlag = CacheUtil.getSysParam("TRANSFER_MST_SALE_FLAG");
        //产品认申购的付款账户默认为募集户
        if (param.getDeal() != null
        		&& (DictCodeList.交易方向_产品认购.getValue().equals(param.getDeal().getPs())
        		|| DictCodeList.交易方向_产品申购.getValue().equals(param.getDeal().getPs())
        		|| DictCodeList.交易方向_份额强增.getValue().equals(param.getDeal().getPs()))) {
        	if (StringUtils.isNotEmpty(param.getPortfolioId())
        			&& StringUtils.isNotEmpty(param.getDeal().getCcy())) {
            	Map<String, Object> paramMap = new HashMap<String, Object>();
            	paramMap.put("portfolioId", param.getPortfolioId());
            	paramMap.put("ccy", param.getDeal().getCcy());
            	paramMap.put("capacctType", DictCodeListExt1.资金账户类型_产品募集户.getValue());
            	List<StlCapacctInfo> listMj = bussSettleOrderCommService.selectProdCapacctInfoList(paramMap);
            	if (listMj != null && listMj.size() > 0) {
            		ret.setPayAcctNo(listMj.get(0).getAcctNo());
            		ret.setPayAcctName(listMj.get(0).getAcctName());
            	}
        	}
        } else if (param.getDeal() != null
        		&& (DictCodeList.交易方向_份额强减.getValue().equals(param.getDeal().getPs())
        		|| DictCodeList.交易方向_产品到期.getValue().equals(param.getDeal().getPs())
        		|| DictCodeList.交易方向_提前终止.getValue().equals(param.getDeal().getPs())
        		|| DictCodeList.交易方向_产品赎回.getValue().equals(param.getDeal().getPs())
        		|| DictCodeList.交易方向_产品提前赎回.getValue().equals(param.getDeal().getPs()))){
        	if (StringUtils.isNotEmpty(param.getPortfolioId())
        			&& StringUtils.isNotEmpty(param.getDeal().getCcy())) {
            	Map<String, Object> paramMap = new HashMap<String, Object>();
            	paramMap.put("portfolioId", param.getPortfolioId());
            	paramMap.put("ccy", param.getDeal().getCcy());
            	paramMap.put("capacctType", DictCodeListExt1.资金账户类型_产品兑付户.getValue());
            	List<StlCapacctInfo> listMj = bussSettleOrderCommService.selectProdCapacctInfoList(paramMap);
            	if (listMj != null && listMj.size() > 0) {
            		ret.setRecAcctNo(listMj.get(0).getAcctNo());
            		ret.setRecAcctName(listMj.get(0).getAcctName());
            	}
        	}
        }
        else if(param.getDeal() != null && (DictCodeListExt3.交易方向_债转股.getValue().equals(param.getDeal().getPs()))
                &&DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(param.getDeal().getFinprodType2())){
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", param.getPortfolioId());
            paramMap.put("ccy", param.getDeal().getCcy());
            paramMap.put("chlId",param.getDeal().getChlId());
            paramMap.put("secManageAcctId",param.getDeal().getSecManageAcctId());
            List<StlCapacctInfo> listMj = bussSettleOrderCommService.getAcctNoByInfo(paramMap);
            if (listMj != null && listMj.size() > 0) {
                ret.setRecAcctNo(listMj.get(0).getAcctNo());
                ret.setRecAcctName(listMj.get(0).getAcctName());
            }
        }
        else if(param.getDeal() != null && (DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(param.getDeal().getPs()))
                &&(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(param.getDeal().getFinprodType2())||
                DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(param.getDeal().getFinprodType2()))){
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", param.getPortfolioId());
            paramMap.put("ccy", param.getDeal().getCcy());
            paramMap.put("counterId",param.getDeal().getCounterId());
            paramMap.put("acctType",DictCodeListExt1.资金账户类型_交易对手账户.getValue());
            List<StlCapacctInfo> listMj = bussSettleOrderCommService.selectCounterCapacctInfoList(paramMap);
            if (listMj != null && listMj.size() > 0) {
                if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(param.getDeal().getFinprodType2())){
                    ret.setRecAcctNo(listMj.get(0).getAcctNo());
                    ret.setRecAcctName(listMj.get(0).getAcctName());
                }
                else{
                    ret.setPayAcctNo(listMj.get(0).getAcctNo());
                    ret.setPayAcctName(listMj.get(0).getAcctName());
                }
            }
        } else if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(param.getDeal().getTradeMarket())
                || DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(param.getDeal().getTradeMarket()) ) {
                //|| "Y".equals(mstSaleFlag)
            // 券商户查询(转出账户)
            List<StlCapacctInfo> qsAcctNoOutList = etrTrdCommonMapper.getQsPtlAcctInfo(param.getSecManageAcctId(), param.getPortfolioId());
            // 券商户查询(转入账户)
            List<StlCapacctInfo> qsAcctNoInList = etrTrdCommonMapper.getQsPtlAcctInfo(param.getDeal().getSecManageAcctId(), param.getPortfolioId());
            if (CollectionUtils.isNotEmpty(qsAcctNoOutList)) {
                ret.setPayAcctNo(qsAcctNoOutList.get(0).getAcctNo());
                ret.setPayAcctName(qsAcctNoOutList.get(0).getAcctName());
            }
            if (CollectionUtils.isNotEmpty(qsAcctNoInList)) {
                ret.setRecAcctNo(qsAcctNoInList.get(0).getAcctNo());
                ret.setRecAcctName(qsAcctNoInList.get(0).getAcctName());
            }

        }

        // 根据组合代码获取组合名称
        PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(param.getPortfolioId());
        if (ptlPortfolio != null) {
            ret.setPortfolioName(ptlPortfolio.getPortfolioName());
        }
        else {
            ret.setPortfolioName(param.getPortfolioId());
        }

        // 根据收款账户查询划款性质
        String recAcctNo = ret.getRecAcctNo();
        if (StringUtil.isNotEmpty(recAcctNo) && StringUtil.isNotEmpty(ret.getCcy())){
            StlCapacctInfoKey stlCapacctInfoKey = new StlCapacctInfoKey();
            stlCapacctInfoKey.setAcctNo(recAcctNo);
            stlCapacctInfoKey.setCcy(ret.getCcy());
            StlCapacctInfo stlCapacctInfo = stlCapacctInfoMapper.selectByPrimaryKey(stlCapacctInfoKey);
            if (stlCapacctInfo != null){
                if (StringUtil.isNotEmpty(stlCapacctInfo.getInOutBank())){
                    ret.setInOutBank(SysDataUtil.getNameFromDataDict(DictCode.资产来源, stlCapacctInfo.getInOutBank()));
                }
                ret.setRecAcctPoint(stlCapacctInfo.getLargePayno());
            }
        }
        // 根据收付款账户设置是否需要发送字段
        String payAcctNo = ret.getPayAcctNo();
        if (StringUtil.isNotEmpty(payAcctNo)
                && StringUtil.isNotEmpty(recAcctNo)
                && "N".equals(param.getDeal().getIsOutTrade())) {
            //N 标识为转仓
            // 指令接口管理 要求显示转入方买入的那一条
            if (StringUtil.equals(payAcctNo, recAcctNo)) {
                ret.setIsNeedSend(DictCodeList.是否_是.getValue());
            } else {
                ret.setIsNeedSend(DictCodeList.是否_否.getValue());
            }
            // 转仓转入组合代码
            ret.setInPortfolioId(param.getDeal().getPortfolioId());
        }

        return ret;
    }

    /**
     * 查询所有账户列表
     *
     * @return 所有账户列表
     * @see
     * @since 1.0
     */
    @Override
    public List<SelectOptionDto> queryAcctList(Map<String, Object> paramMap) {
        if (paramMap == null) {
            paramMap = new HashMap<String, Object>();
        }
        return bussSettleOrderCommService.queryAcctList(paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StlCapacctInfo queryStlCapacctInfoByBusiAcctNo(String ccy, String busiAcctNo) {
        return bussSettleOrderCommService.queryStlCapacctInfoByBusiAcctNo(ccy, busiAcctNo);
    }
}
