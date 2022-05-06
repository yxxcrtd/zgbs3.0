/*
 * 文件名：TrdConBondService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangpeng
 * 修改时间：2019年11月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.conbond.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.conbond.dao.TrdConBondMapper;
import com.joyintech.service.trade.conbond.dto.TrdConBondDto;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;

/**
 * 可转债交易Service
 *
 * @author wangpeng
 * @version 1.0 2019年11月14日
 * @see TrdConBondService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class TrdConBondService extends BaseService {

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    @Autowired
    private TrdConBondMapper trdConBondMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;


    /**
     * 金融产品交易业务流程表
     */
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    /**
     * 金融产品附属业务流程表
     */
    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    /**
     * 金融产品交易扩展业务流程表
     */
    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    /**
     * 划款指令业务流程表
     */
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    /**
     * 金融产品交易组件
     */
    @Autowired(required = false)
    private BaseTradeProductDealService baseTradeProductDealService;


    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private MapperDelegateService mapperDelegateService;


    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private PositionRequestTrdConBondService positionRequestService;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    /**
     * 分页查询
     *
     * @param page      页编号
     * @param length    页条数
     * @param pageParam 画面参数
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<TrdConBondDto> selectForPage(int page, int length,
                                             Map<String, Object> pageParam)
    {
        pageParam.put("VALID_STATUS_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("VALID_STATUS_02", DictCodeListExt1.有效状态_待处理.getValue());
        pageParam.put("VALID_STATUS_03", DictCodeListExt1.有效状态_撤销.getValue());
//        pageParam.put("YES_NO_YES", DictCodeList.是否_是.getValue());
//        pageParam.put("YES_NO_NO", DictCodeList.是否_否.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 一体化对应etliu start
        List<String> psList = new ArrayList<String>();
        psList.add(DictCodeList.交易方向_申购.getValue());
        psList.add(DictCodeList.交易方向_申购确认.getValue());
        psList.add(DictCodeListExt3.交易方向_退款到账.getValue());
        pageParam.put("psList", psList);
        // 一体化对应etliu end
//        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        pageParam.put("TRD62", DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
        pageParam.put("PURCH_TYPE_02", DictCodeListExt2.申购类型_普通申购.getValue());
        pageParam.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        pageParam.put("PS_T58", DictCodeListExt3.交易方向_退款到账.getValue());
        Page<TrdConBondDto> pageDto = this.queryForPage(page, length,
                () -> trdConBondMapper.selectDeal(pageParam));
        List<TrdConBondDto> listDto = pageDto.getRoot();
        for (TrdConBondDto tfitDto : listDto) {
            if (StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_申购.getValue())
                    && StringUtil.equals(tfitDto.getValidStatus(), DictCodeListExt1.交易流程状态_有效.getValue())) {
                Map<String, Object> param = new HashMap<>();
                param.put("bTradeId", tfitDto.getBusiCode());
                param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
                param.put("yes", DictCodeList.是否_是.getValue());
                int flg1 = trdConBondMapper.selectConfirmInfo(param);
                int flg2 = trdConBondMapper.selectWflConfirmInfo(tfitDto.getBusiCode());
                if (flg1 == 0 && flg2 == 0 && !StringUtil.equals(DictCodeListExt2.申购类型_优先配售.getValue(), tfitDto.getPurchType())) {
                    tfitDto.setFlg("01");
                } else {
                    tfitDto.setFlg("02");
                }
            }
            else if(StringUtil.equals(tfitDto.getPs(), DictCodeList.交易方向_申购确认.getValue())
                    && StringUtil.equals(tfitDto.getValidStatus(), DictCodeListExt1.交易流程状态_有效.getValue())){
                Map<String, Object> param = new HashMap<>();
                param.put("bTradeId", tfitDto.getBusiCode());
                param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
                param.put("yes", DictCodeList.是否_是.getValue());
                int flg1 = trdConBondMapper.selectConfirmInfo(param);
                int flg2 = trdConBondMapper.selectWflConfirmInfo(tfitDto.getBusiCode());
                if (flg1 == 0 && flg2 == 0 ) {
                    TrdConBondDto trdConBondDto=this.getTrdConBondDto(tfitDto.getBusiCode());
                    if(StringUtil.isNotEmpty(trdConBondDto.getRefundAmt())&&BigDecimal.ZERO.compareTo(trdConBondDto.getRefundAmt())<0){
                        tfitDto.setFlg("01");
                    }else {
                        tfitDto.setFlg("02");
                    }
                } else {
                    tfitDto.setFlg("02");
                }
            }

            else {
                tfitDto.setFlg("02");
            }
        }
        return pageDto;
    }



    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getSecManageAcctId(Map<String, Object> param) {
        return this.queryForSuggest(() -> trdConBondMapper.getSecManageAcctId(param),
                null);
    }

    /**
     * 资产代码：下拉框
     *
     * @param
     * @return 资产代码：下拉框
     * @throws Exception
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Page<SelectOptionDto> getFinprodId(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//        List<SelectOptionDto> resultList = trdConBondMapper.getFinprodId(param);
        param.put("TYPE6_B14", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        param.put("TYPE6_B15", DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue());
        param.put("TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        return this.queryForPage(page, length,
                () -> trdConBondMapper.getFinprodId(param));

    }


    /**
     * 债券投资交易新建画面查看模式查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public TrdConBondDto getTrdConBondDto(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", tradeId);
        params.put("B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        params.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        params.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        params.put("PS_T51", DictCodeListExt1.交易方向_保证金回款.getValue());
        params.put("TRD62", DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
        params.put("PURCH_TYPE_02", DictCodeListExt2.申购类型_普通申购.getValue());
        params.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        params.put("PS_T58", DictCodeListExt3.交易方向_退款到账.getValue());
        TrdConBondDto resultDto = trdConBondMapper.getTrdConBondDto(params);
        return resultDto;
    }

    public BigDecimal getTongDaoCash(Map<String, Object> map) {
        map.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
        map.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
        return trdConBondMapper.getTongDaoCash(map);
    }

    /**
     * 提交流程启动时调用方法 <功能详细描述>
     *
     * @param entity
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @see
     * @since 1.0
     */
    public void doInsert(TrdConBondDto entity)
            throws IllegalAccessException,
            InvocationTargetException {
        if (entity.getOperateType().equals("submit")) {
            if (StringUtil.isNotEmpty(entity.getTrdProductDeal())&&StringUtil.equals(DictCodeList.交易方向_买入.getValue(), entity.getTrdProductDeal().getPs())) {
                // 调用授信共同 sjj start
                TrdProductDeal TrdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity.getTrdProductDeal());
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                TrdProductDeal.setTradeId(entity.getBusiCode());//设置为汇总交易编号
                inputDto.setTrdProductDeal(TrdProductDeal);
                AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
                // 调用授信共同 sjj end
            }


            this.doDelete(entity.getWorkflowId());
            // 金融产品交易信息汇总DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, entity.getTrdFinProductDealTotal());
            if(!StringUtil.isNotEmpty(wflTrdFinProductDealTotal.getbTradeId())){
                wflTrdFinProductDealTotal.setbTradeId(entity.getbTradeId());
            }
            wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

            // 金融产品交易汇总附属信息DTO.金融产品交易汇总附属Entity -> 金融产品交易业务汇总附属流程表
            WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, entity.getTrdDealTotalAdd());
//            wflTrdDealTotalAdd.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, entity.getPaTrdFinProductDealTotal());
            wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());


            // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            if (StringUtil.isNotEmpty(entity.getTrdProductDeal())) {
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity.getTrdProductDeal());
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            }

            // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            if (StringUtil.isNotEmpty(entity.getTrdProductDealAdd())) {
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
            }

            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            if(StringUtil.isNotEmpty(entity.getPaTrdProductDeal())){
                BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
                wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            }

            // 批量插入
            wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
            wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
            if (StringUtil.isNotEmpty(entity.getTrdProductDeal())) {
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
            }
            if (StringUtil.isNotEmpty(entity.getTrdProductDealAdd())) {
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
            }
            if(StringUtil.isNotEmpty( entity.getPaTrdProductDeal())){
                wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
            }
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                        entity.getBussTrdTransferOrder().get(0));
                wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
            }
        } else if (entity.getOperateType().equals("save")) {
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getWorkflowId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        } else if (entity.getOperateType().equals("REVOKE")) {
            doRevokeDeal(entity);
        }

        if (!entity.getOperateType().equals(OperationType.REVOKE)) {
            //对接头寸引擎
            String decisionAct = DictCodeListExt3.系统动作_新建.getValue();
            String systemAct = DictCodeListExt3.系统动作_新建.getValue();
            if (StringUtil.equals(entity.getOperateType(), "modify")) {
                systemAct = DictCodeListExt3.系统动作_新建.getValue();
                decisionAct = DictCodeListExt3.系统动作_修改.getValue();
            }else {
                SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                    systemAct = 系统动作_新建.getValue();
                    decisionAct = 系统动作_修改.getValue();
                }
            }
            positionRequestService.sendTrdConBondDto(entity,decisionAct,systemAct);
        }else{
            positionRequestService.sendRevoke(entity);
        }
    }

    public void doRevokeDeal(TrdConBondDto trdConBondDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("bizCode", trdConBondDto.getBusiCode());
        params.put("workflowId", trdConBondDto.getWorkflowId());
        params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
        params.put("user", trdConBondDto.getCreateUser());
        params.put("dept", trdConBondDto.getCreateDept() == null ? "dept" : trdConBondDto.getCreateDept());
        params.put("time", trdConBondDto.getCreateTime());
        params.put("revokeReason", trdConBondDto.getRevokeReason());
        // 汇总交易表
        trdConBondMapper.insertWflTotle(params);
//        // 汇总扩展表
        trdConBondMapper.insertWflPaTotal(params);

        trdConBondMapper.insertWflTradeTotalAdd(params);
        params.put("ORDER_STATUS_03", DictCodeListExt1.指令状态_撤销.getValue());
        // 划款指令表
        trdConBondMapper.insertWflPayOrders(params);
        // 修改参数
        params.put("totleTradeId", params.get("bizCode"));
        List<TrdProductDeal> deals = trdConBondMapper.findDealByTotalId(params);
        if (!deals.isEmpty()) {
            params.put("deals", deals);
            // 交易表
            trdConBondMapper.insertWflTrade(params);
//            // 交易扩展表
            trdConBondMapper.insertWflPaTrade(params);
            // 交易附属表
            trdConBondMapper.insertWflTradeAdd(params);
            // 划款指令表
            trdConBondMapper.insertWflPayOrdersOfDeal(params);
        }
        if(StringUtil.equals(trdConBondDto.getPs(), DictCodeList.交易方向_申购确认.getValue())&&StringUtil.isNotEmpty(trdConBondDto.getRefundAmt())&&BigDecimal.ZERO.compareTo(trdConBondDto.getRefundAmt())<0){
            //申购确认撤销时，撤销退款到账
            String bizcode = trdConBondMapper.getTradeId(trdConBondDto.getBusiCode());
            if(StringUtil.isNotEmpty(bizcode)){
                params.put("bizCode", bizcode);

                trdConBondMapper.insertWflTotle(params);
//              // 汇总扩展表
                trdConBondMapper.insertWflPaTotal(params);

                params.put("ORDER_STATUS_03", DictCodeListExt1.指令状态_撤销.getValue());
                // 划款指令表
                trdConBondMapper.insertWflPayOrders(params);
                // 修改参数
                params.put("totleTradeId", bizcode);
                deals = trdConBondMapper.findDealByTotalId(params);
                if (!deals.isEmpty()) {
                    params.put("deals", deals);
                    // 交易表
                    trdConBondMapper.insertWflTrade(params);
                    //                  // 交易扩展表
                    trdConBondMapper.insertWflPaTrade(params);
                    // 交易附属表
                    trdConBondMapper.insertWflTradeAdd(params);
                    // 划款指令表
                    trdConBondMapper.insertWflPayOrdersOfDeal(params);

                }
            }
        }
    }

    public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser,
                                                 String revokeReason, String actPortfolio) {
        // 用于获取关键信息的map
        Map<String, Object> map = new HashMap<>();
        // 是否汇总的参数区分
        map.put("totleTradeId", bizCode);
        Date updateTime = new Date();
        TrdFinProductDealTotal trdFinProductDealTotal = null;

        // 金融产品交易汇总
        trdFinProductDealTotal = trdFinProductDealTotalMapper.selectByPrimaryKey(bizCode);
        // 原数据 是否撤销 => 是
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());
        trdFinProductDealTotal.setUpdateTime(updateTime);
        trdFinProductDealTotal.setUpdateUser(updateUser);
        trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);
        // 撤销交易状态为撤销
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
        String newTotalTradeId = trdFinProductDealTotal.getTotleTradeId() + "R";
        trdFinProductDealTotal.setpTradeId(trdFinProductDealTotal.getTotleTradeId());
        trdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
        if (StringUtil.isNotEmpty(trdFinProductDealTotal.getpTotleTradeId())) {
            trdFinProductDealTotal.setpTotleTradeId(trdFinProductDealTotal.getpTotleTradeId() + "R");
        }
        // 是否撤销入否
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

        PaTrdFinProductDealTotal paTrdFinProductDealTotal = paTrdFinProductDealTotalMapper
                .selectByPrimaryKey(bizCode);
        if(StringUtil.isNotEmpty(paTrdFinProductDealTotal)) {
            paTrdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
            paTrdFinProductDealTotal.setUpdateTime(updateTime);
            paTrdFinProductDealTotal.setUpdateUser(updateUser);
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
        }

        TrdDealTotalAdd trdDealTotalAdd = trdDealTotalAddMapper.selectByPrimaryKey(bizCode);
        if(StringUtil.isNotEmpty(trdDealTotalAdd)) {
            trdDealTotalAdd.setTotleTradeId(newTotalTradeId);
            trdDealTotalAdd.setUpdateTime(updateTime);
            trdDealTotalAdd.setUpdateUser(updateUser);
            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
        }

        // 划款指令
        List<StlPayOrders> ordersTotal = trdConBondMapper.findOrdersByOrderId(bizCode);
        if (ordersTotal != null) {
            ordersTotal.forEach(order -> {
                        order.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        order.setpTradeId(order.getOrderId());
                        order.setTradeId(order.getTradeId() + "R");
                        order.setOrderId(order.getOrderId() + "R");
                        stlPayOrdersMapper.insertSelective(order);
                    }
            );
        }

        // 金融产品交易Entity
        // 增加无汇总交易的场景 无汇总交易使用交易编号关联
        List<TrdProductDeal> trdProductDeals = trdConBondMapper.findDealByTotalId(map);

        // 一体化对应etliu start
        Map<String, String> portfolioIdMap = new HashMap<String, String>();
        // 一体化对应etliu end

        trdProductDeals.forEach(trdProductDeal1 -> {
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtils.copyProperties(trdProductDeal1, trdProductDeal);
            trdProductDeal.setIsCancel(DictCodeList.是否_是.getValue());
            // 原数据 是否撤销 => 是
            trdProductDealMapper.updateByPrimaryKey(trdProductDeal);
            // 交易状态为撤销
            String oldTradeId = trdProductDeal.getTradeId();
            String newTradeId = trdProductDeal.getTradeId() + "R";
            trdProductDeal1.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue()); // 调用估值接口时，状态是撤销
            trdProductDeal1.setpTradeId(oldTradeId);
            trdProductDeal1.setTradeId(newTradeId);

            trdProductDeal.setpTradeId(oldTradeId);
            trdProductDeal.setTradeId(newTradeId);
            trdProductDeal.setCreateTime(new Date());
            trdProductDeal.setUpdateTime(new Date());
            trdProductDeal.setCancelRemark(revokeReason);
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            trdProductDealMapper.insertSelective(trdProductDeal);
            // 划款指令
//			List<StlPayOrders> orders = trdConBondMapper.findOrdersByOrderId(oldTradeId);
//			revokeStlPayOrders(orders);

            trdConBondMapper.insertRevokePaTrdProductDeal(oldTradeId, updateUser);
            trdConBondMapper.insertRevokeTrdProductDealAdd(oldTradeId, updateUser);

            TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(oldTradeId);
            // 组合交易
            if (trdProductDealAdd != null && StringUtil.isNotEmpty(trdProductDealAdd.getPortfolioId())) {
                PtlTrade deal = new PtlTrade();
                deal.setBusiNo(oldTradeId);
                deal.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                deal.setPortfolioId(trdProductDealAdd.getPortfolioId());
                trdConBondMapper.insertRevokePtlTrade(deal, updateUser);
                if (StringUtil.isNotEmpty(actPortfolio) && !trdProductDealAdd.getPortfolioId().equals(actPortfolio)) {
                    deal.setPortfolioId(actPortfolio);
                    trdConBondMapper.insertRevokePtlTrade(deal, updateUser);
                }

                // 一体化对应etliu start
                portfolioIdMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
                // 一体化对应etliu end
            }

            // 费用明细
//			trdConBondMapper.inertRevokeFeeDetailByTradeId(oldTradeId, updateUser);
//
//			// 删除交易报送
//			trdConBondMapper.deleteRepTradeByTradeId(oldTradeId);

        });

        // 一体化对应etliu start
        if (!StringUtil.isEmptyList(trdProductDeals)) {

            List<TrdProductDeal> trdProductDeals1 = trdConBondMapper.findDealByTotalIdForInterf(map);
            List<TrdProductDealAdd> trdProductDealAdds = trdConBondMapper.findDealAddByTradeIds(trdProductDeals1);
            List<PtlTrade> ptlTradeList = trdConBondMapper.findPtlTradeByTradeIds(trdProductDeals1);
            // 所有交易生效后调用的共通方法
            for (TrdProductDeal baseDto : trdProductDeals1) {
                if(StringUtil.isEmpty(baseDto.getpTradeId())){
                    continue;
                }
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(portfolioIdMap.get("portfolioId"));
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());

                baseDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                inputDto.setTrdProductDeal(baseDto);
                inputDto.setTrdProductDealAdd(getAdd(trdProductDealAdds, baseDto.getTradeId()));
                inputDto.setPtlTradeList(getPtlTrade(ptlTradeList, baseDto.getTradeId()));
                if (ordersTotal != null && !ordersTotal.isEmpty()) {
                    StlPayOrders order = new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(order, ordersTotal.get(0));
                    inputDto.setStlPayOrders(order);
                }
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }
        }
        // 一体化对应etliu end

        if(StringUtil.equals(trdFinProductDealTotal.getPs(), DictCodeList.交易方向_申购确认.getValue())){
            String tradeId= trdConBondMapper.getTradeId(bizCode);
            if(StringUtil.isNotEmpty(tradeId)){
                revoke汇总和交易相关表(tradeId,updateUser,revokeReason,actPortfolio);
            }
        }

        return trdFinProductDealTotal;
    }

    private TrdProductDealAdd getAdd(List<TrdProductDealAdd> trdProductDealAdds, String tradeId) {
        for (TrdProductDealAdd dealAdd : trdProductDealAdds) {
            if (tradeId.equals(dealAdd.getTradeId())) {
                return dealAdd;
            }
        }
        return null;
    }

    private List<PtlTrade> getPtlTrade(List<PtlTrade> trdProductDealAdds, String tradeId) {
        List<PtlTrade> ptlTradeList = new ArrayList<>();
        for (PtlTrade ptlTrade : trdProductDealAdds) {
            if (tradeId.equals(ptlTrade.getBusiNo())) {
                ptlTradeList.add(ptlTrade);
            }
        }
        return ptlTradeList;
    }

    /**
     * 流程结束时调用方法 <功能详细描述>
     *
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public void doComplete(TrdConBondDto entity) {
        if (entity.getOperateType().equals("submit")) {

//            if (StringUtil.equals(DictCodeList.交易方向_申购.getValue(), entity.getPs())) {
//                doDelete(entity.getWorkflowId());
//                // 文件
//                MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
//                mstFileInputParameter.setFileList(entity.getFileArray());
//                mstFileInputParameter.setBussinessId(entity.getBusiCode());
//                if (mstFileCommonService != null) {
//                    mstFileCommonService.updateBusinessId(mstFileInputParameter);
//                }
//
//                // 一体化对应etliu start
//                // 所有交易生效后调用的共通方法
//                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//                inputDto.setProdid(entity.getPortfolioId());
//                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                inputDto.setTrdProductDeal(entity.getTrdProductDeal());
//                // <!-- 通道交易 新增 强成西 2019/03/26 start -->
//                inputDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
//                // <!-- 通道交易 新增 强成西 2019/03/26 end -->
//
//                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
//                StlPayOrdersDto stlPayOrders = null;
//                if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
//                    stlPayOrders = new StlPayOrdersDto();
//                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
//                            entity.getBussTrdTransferOrder().get(0));
//                }
//                // 反向交易增加 start by qiangchengxi 2019年3月12日
//                if (StringUtil.isNotEmpty(entity.getTrdProductDeal())) {
//                    AllTradeCommonUtil.setDeals(inputDto, entity.getTrdProductDeal(), entity.getTrdProductDealAdd(), entity.getPaTrdProductDeal(),
//                            Arrays.asList(stlPayOrders), entity.getPtlTrade(), entity.getActPortfolioId());
//                }
//
//                // 反向交易增加 end by qiangchengxi 2019年3月12日
//
//                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
//                    inputDto.setActPortfolioId(entity.getActPortfolioId());
//                }
//                if (StringUtil.isNotEmpty(entity.getTrdProductDeal())) {
//                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//                }
//                // 一体化对应etliu end
//
//            } else if (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())) {
//                doDelete(entity.getWorkflowId());
//                // 所有交易生效后调用的共通方法
//                for (TrdProductDeal trdProductDeal : entity.getTrdProductDealList()) {
//                	AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//                    trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
//                    inputDto.setTrdProductDeal(trdProductDeal);
////                    TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
//                	for (TrdProductDealAdd trdProductDealAdds : entity.getTrdProductDealAddList()) {
//                        if (StringUtil.equals(trdProductDeal.getTradeId(), trdProductDealAdds.getTradeId())) {
//                        	inputDto.setProdid(trdProductDealAdds.getPortfolioId());
//                            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                            inputDto.setTrdProductDealAdd(trdProductDealAdds);
//                        }
//                    }
//                    List<PtlTrade> ptlTradeList = new ArrayList<>();
//                    for(PtlTrade ptlTrade : entity.getPtlTrade()){
//                    	if (StringUtil.equals(trdProductDeal.getTradeId(), ptlTrade.getBusiNo())) {
//                    		ptlTradeList.add(ptlTrade);
//                    	}
//                    }
////                    PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
////                    for(PaTrdProductDeal paTrdProductDeals : entity.getPaTrdProductDealList()){
////                    	if (StringUtil.equals(trdProductDeal.getTradeId(), paTrdProductDeals.getTradeId())) {
////                    		paTrdProductDeal = paTrdProductDeals;
////                    	}
////                    }
//                    inputDto.setTrdProductDeal(trdProductDeal);
////                    inputDto.setTrdProductDealAdd(trdProductDealAdd);
//                    inputDto.setPtlTradeList(ptlTradeList);
//                    inputDto.setActPortfolioId(entity.getActPortfolioId());
////                    AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, trdProductDealAdd, paTrdProductDeal,
////                                null, ptlTradeList, entity.getActPortfolioId());
//                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//
//                }
//            }

        } else if (StringUtil.equals(entity.getOperateType(), "REVOKE")) {
            doDelete(entity.getWorkflowId());
            revoke汇总和交易相关表(entity.getBusiCode(), entity.getUpdateUser(), entity.getRevokeReason(), entity.getActPortfolioId());
        }
    }

    /**
     * 代办中修改时流程 <功能详细描述>
     *
     * @param entity
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    public void doUpdate(TrdConBondDto entity)
            throws IllegalAccessException,
            InvocationTargetException {
        if (entity.getOperateType().equals("submit")) {

            // 调用授信共同 sjj start
            TrdProductDeal TrdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
            inputDto.setTrdProductDeal(TrdProductDeal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
            // 调用授信共同 sjj end

            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            // if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue()) || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())) {
            // wflTrdProductDeal.setTradeAmt(null);
            // }
            wflTrdProductDealMapper.insert(wflTrdProductDeal);
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
            wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
            wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
                    wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflStlPayOrdersMapper.insert(wflStlPayOrders);
                }
            }
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getWorkflowId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        } else if (entity.getOperateType().equals("save")) {
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getWorkflowId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        } else if (entity.getOperateType().equals("REVOKE")) {
            List<String> idList = new ArrayList<>();
            idList.add(entity.getBusiCode());
            // 调用金融产品交易组件查询交易信息
            List<BaseTradeProductDealDto> dtoList = baseTradeProductDealService.select(idList);
            for (BaseTradeProductDealDto btpdd : dtoList) {
                TrdProductDeal tpddto = btpdd.getTrdProductDeal();
                tpddto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                tpddto.setpTradeId(entity.getBusiCode());
                tpddto.setCancelRemark(entity.getRemark());
                tpddto.setTradeId(entity.getBusiCode() + "R");
                tpddto.setWorkflowId(entity.getWorkflowId());
                WflTrdProductDeal wtpd = new WflTrdProductDeal();
                WflTrdProductDealAdd wtpda = new WflTrdProductDealAdd();
                WflPaTrdProductDeal wptpd = new WflPaTrdProductDeal();
                WflStlPayOrders wspo = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(wtpd, tpddto);
                wtpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                BeanUtil.copyPropertiesIgnoreNull(wtpda, btpdd.getTrdProductDealAdd());
                wtpda.setTradeId(wtpd.getTradeId());
                wtpda.setWorkflowId(entity.getWorkflowId());
                wtpda.setPortfolioId(entity.getActPortfolioId());
                wtpda.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());

                BeanUtil.copyPropertiesIgnoreNull(wptpd, paTrdProductDealMapper.selectByPrimaryKey(entity.getBusiCode()));
                wptpd.setTradeId(wtpd.getTradeId());
                wptpd.setWorkflowId(entity.getWorkflowId());
                wptpd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                if (btpdd.getStlPayOrdersList() != null && btpdd.getStlPayOrdersList().size() > 0) {
                    BeanUtil.copyPropertiesIgnoreNull(wspo, btpdd.getStlPayOrdersList().get(0));
                    wspo.setTradeId(wtpd.getTradeId());
                    wspo.setWorkflowId(entity.getWorkflowId());
                    wspo.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
                    wflStlPayOrdersMapper.insert(wspo);
                }
                wflTrdProductDealMapper.insert(wtpd);
                wflTrdProductDealAddMapper.insert(wtpda);
                wflPaTrdProductDealMapper.insert(wptpd);
            }
        }
        //头寸接口引擎
        String decisionAct = DictCodeListExt3.系统动作_修改.getValue();
        String systemAct = DictCodeListExt3.系统动作_修改.getValue();
        positionRequestService.sendTrdConBondDto(entity,decisionAct,systemAct);
    }

    /**
     * 流程作废时删除业务流程表 <功能详细描述>
     *
     * @param workflowId
     * @see
     * @since 1.0
     */
    public void doDelete(String workflowId) {
//        trdFundInvestTradeMapper.deleteWflTrdProductDealByWfl(workflowId);
//        trdFundInvestTradeMapper.deleteWflTrdProductDealAddByWfl(workflowId);
//        trdFundInvestTradeMapper.deleteWflPaTrdProductDealByWfl(workflowId);
//        trdFundInvestTradeMapper.deleteWflStlPayOrdersByWfl(workflowId);


        // 金融产品交易信息DTO.金融产品交易Entity -> 	金融产品交易汇总表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_FIN_PRODUCT_DEAL_TOTAL", "WORKFLOW_ID",
                workflowId);

        // 金融产品交易信息DTO.金融产品交易Entity -> 	金融产品交易汇总附属表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_DEAL_TOTAL_ADD", "WORKFLOW_ID",
                workflowId);

        // 金融产品交易信息DTO.金融产品交易Entity -> 	金融产品交易汇总扩展表
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_FIN_DEAL_TOTAL", "WORKFLOW_ID",
                workflowId);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
                workflowId);

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
                "WORKFLOW_ID", workflowId);

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
                "WORKFLOW_ID", workflowId);

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID",
                workflowId);
        //头寸引擎对接
        positionRequestService.sendDelete(workflowId);
    }

    public List<StlPayOrders> getStlPayOrders(Map<String, Object> param) {
        // 划款指令
        String bizCode = (String) param.get("busiCode");
        List<StlPayOrders> ordersTotal = trdConBondMapper.findOrdersByOrderId(bizCode);
        return ordersTotal;
    }

    /**
     * 查询撤销数据是否有后继交易 <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public boolean revokeCheck(TrdConBondDto dto) {
        Map<String, Object> param = new HashMap<>();
        param.put("bTradeId", dto.getBusiCode());
        param.put("yes", DictCodeList.是否_是.getValue());
        param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
        int result = 0;
        result = trdConBondMapper.selectConfirmInfo(param);
        int result2 = trdConBondMapper.selectConfirmInfoWfl(param);
        if(StringUtil.isNotEmpty(dto.getPs())&&StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), dto.getPs())){
            return result2 == 0?true:false;
        }
        return result + result2 == 0 ? true : false;
    }

    /**
     * 查询撤销数据是否有撤销流程中数据
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public boolean revokeCheckWfl(TrdConBondDto dto) {
        Map<String, Object> param = new HashMap<>();
        param.put("bTradeId", dto.getBusiCode());
        param.put("yes", DictCodeList.是否_是.getValue());
        param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
        int result = 0;
        result = trdConBondMapper.revokeCheckWfl(param);
        return result == 0 ? true : false;
    }



    public TrdConBondDto getTrdConBondDtoForAccount(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", tradeId);
        params.put("B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        params.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        params.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        params.put("PS_T51", DictCodeListExt1.交易方向_保证金回款.getValue());
        params.put("TRD62", DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
        params.put("PURCH_TYPE_02", DictCodeListExt2.申购类型_普通申购.getValue());
        params.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        TrdConBondDto resultDto = trdConBondMapper.getTrdConBondDtoForAccount(params);
        return resultDto;
    }



    /**
     * 根据交易id找寻后续退款到账交易id
     * @param tradeId
     * @return
     */
    public Map<String, Object> getBusicode(String tradeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("bTradeId", tradeId);
        map.put("PS_T51", DictCodeListExt1.交易方向_保证金回款.getValue());
        return trdConBondMapper.getBusicode(map);
    }



    public TrdConBondDto getTrdConBondDtoForTradeId(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", tradeId);
        params.put("B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        params.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        params.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        params.put("PS_T51", DictCodeListExt1.交易方向_保证金回款.getValue());
        params.put("TRD62", DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
        params.put("PURCH_TYPE_02", DictCodeListExt2.申购类型_普通申购.getValue());
        params.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        TrdConBondDto resultDto = trdConBondMapper.getTrdConBondDtoForTradeId(params);
        return resultDto;
    }

    /**
     * 获取生效退款到账交易编号
     */
    public String  getTradeId(String busiCode) {
        return trdConBondMapper.getTradeId(busiCode);
    }


    /**
     * 获取退款到账交易编号(包括流程中)
     */
    public String  getRefundTradeId(String busiCode) {
        String tradeId= trdConBondMapper.getTradeId(busiCode);
        if(StringUtil.isNotEmpty(tradeId)){
            return tradeId;
        }else{
            Map<String, Object> map = this.getBusicode(busiCode);
            if(map != null && StringUtil.isNotEmpty(map.get("BUSICODE"))){
                return (String)map.get("BUSICODE");
            }
            return null;
        }
    }

    public List<StlPayOrders> getTrdProductDeals(Map<String, Object> param) {
        return trdConBondMapper.getTrdProductDeals(param);
    }
}
