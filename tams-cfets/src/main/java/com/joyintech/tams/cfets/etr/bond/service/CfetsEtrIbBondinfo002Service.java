/*
 * 文件名：EtrIbBondinfo002Service.java
 */

package com.joyintech.tams.cfets.etr.bond.service;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.entrust.common.controller.EntrustCommonBaseController;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfo002Mapper;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondinfo002Dto;
import com.joyintech.tams.common.base.dao.WflEtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 债券投资委托微服务端service 〈功能详细描述〉
 */
@Service("CfetsEtrIbBondinfo002Service")
@Transactional(rollbackFor = Exception.class)
public class CfetsEtrIbBondinfo002Service extends BaseService<CfetsEtrIbBondinfo002Dto>  implements BaseProcessService<CfetsEtrIbBondinfo002Dto> {

    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;
    @Autowired
    private  CfetsEtrIbBondinfo002Mapper cfetsEtrIbBondinfo002Mapper;

    /**
     * 委托指令流程中Mapper
     */
    @Autowired
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    /**
     * 委托指令附属表流程中Mapper
     */
    @Autowired
    private WflEtrEntrustAddMapper wflEtrEntrustAddMapper;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;
    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;
    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;
    /**
     *  投资委托基础组件调用
     */
    @Autowired(required = false)
    private EntrustCommonBaseController entrustCommonBaseController;

    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private EtrCommandDispatcherService etrCommandDispatcherService;
    /**
     * 获取新建界面的资产代码联想下拉框框 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
  /*  public Page<SelectOptionDto> getfinprodIdForAdd(int page, int length,Map<String, Object> param) {
        param.put("assestType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_05",DictCodeListExt1.交易场所_机构间私募系统.getValue());
        param.put("TRADE_PLACE_09",DictCodeListExt1.交易场所_OTC场外.getValue());
        param.put("TRADE_PLACE_10",DictCodeListExt1.交易场所_境外交易场所.getValue());
        return this.queryForPageForSelectReturn(page, length,()->etrIbBondinfo002Mapper.getfinprodIdForAdd(param));
    }*/

    /**
     * 工作流完成方法
     */
    @Override
    @Transactional
    @DTXTransaction
    public void doComplete(CfetsEtrIbBondinfo002Dto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate = DateUtil.platDateFromCache();
        dto.setEntrustDate(sysDate);
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
       // basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        //WFlowCommonService.doCompleteFlow(dto.getWorkflowId());
        try {
            doCompleteMethod(dto, dto.getOperateType());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //发送数据到b45
//        Map<String, Object> param = new HashMap<String, Object>();
//        param.put("entrustId", dto.getEntrustId());
//        String ps = dto.getPs();
//
//        if (DictCodeList.交易方向_买入.getValue().equals(ps)
//                || DictCodeList.交易方向_卖出.getValue().equals(ps)) {
//            sendService.sendToB45(param, send005Service);
//        }
//        if (DictCodeList.交易方向_行权_回售.getValue().equals(ps)) {
//            sendService.sendToB45(param, send007Service);
//        }
    }

    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
    }

    private void doDeleteFlow(String workflowId) {
        Map<String, Object> param = new HashMap<>();
        param.put("workflowId", workflowId);
        entrustCommonBaseController.doDeleteEtrFlow(param);
    }

    /**
     * 流程发起时调用
     */
    @Override
    public void doInsert(CfetsEtrIbBondinfo002Dto dto, OperationType type) {
        //TODO 调用头寸接口

        WFlowCommonService.doInsertFlow(dto.getOperateType(), initBaseEtrInvestInfoDto(dto));

        doInsertMethod(dto, dto.getOperateType());
    }

    /**
     * 流程修改时调用
     */
    @Override
    public void doUpdate(CfetsEtrIbBondinfo002Dto dto) {
        WFlowCommonService.doUpdateFlow(dto.getOperateType(), initBaseEtrInvestInfoDto(dto));
        doInsertMethod(dto, dto.getOperateType());
        if (!StringUtil.equals("revoke", dto.getOperateType()) && !StringUtil.equals("delete", dto.getOperateType())) {
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(CfetsEtrIbBondinfo002Dto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(dto.getWorkflowId());
                newEntity.setStatus(dto.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(dto));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 工作流完成方法 <功能详细描述>
     *
     * @param dto
     * @param operationType
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    public void doCompleteMethod(CfetsEtrIbBondinfo002Dto dto, String operationType)
            throws IllegalAccessException, InvocationTargetException {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        entrustCommonDto.setEtrEntrust(etrEntrust);
        entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        //add by tanxuan on 20200514 指令分发推送MQ  参数定义
        String isPushMsg = DictCodeList.是否_否.getValue();
        if (StringUtil.equals(operationType, "NEW")) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            dto.setRecStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            //BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().multiply(new BigDecimal(100))); //前台单位万元
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            //etrEntrustAdd.setOurEntNum(ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            //etrEntrustAdd.setLendinEntNum(ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            // 全价入库
            // 单位全价 fuyongsheng 20210917 edit start
            etrEntrustAdd.setFullPrice(dto.getUnitFprice());
            // 单位全价 fuyongsheng 20210917 edit end
            if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getTrader())) {

                EtrDistribute etrDistribute = new EtrDistribute();
                // 设置接收状态
                etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                // 设置指定交易员指定交易员 = 指定交易员
                etrDistribute.setTrader(dto.getTrader());
                // 设置委托代码
                etrDistribute.setEntrustId(dto.getEntrustId());
                //
                etrDistributeList.add(etrDistribute);
                // entity.setEtrDistributeList(etrDistributeList);
            }

            /*  if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {

                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,
                    dto.getEtrInvestPersonInfo().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    // 设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    // 设置指定交易员指定交易员 = 指定交易员
                    etrDistribute.setTrader(trader);
                    // 设置委托代码
                    etrDistribute.setEntrustId(dto.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
                // entity.setEtrDistributeList(etrDistributeList);
            }*/
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                }
                else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        // 自动分发
                        entity.setEtrDistributeList(
                                busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            baseEtrInvestInfoService.newDelegate(entity);
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
        else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            dto.setRecStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            //BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().multiply(new BigDecimal(100))); //前台单位万元
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            // 全价入库
            // 单位全价 fuyongsheng 20210917 edit start
            etrEntrustAdd.setFullPrice(dto.getUnitFprice());
            // 单位全价 fuyongsheng 20210917 edit start
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            //etrEntrustAdd.setOurEntNum(ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            //etrEntrustAdd.setLendinEntNum(ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getTrader())) {

                EtrDistribute etrDistribute = new EtrDistribute();
                // 设置接收状态
                etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                // 设置指定交易员指定交易员 = 指定交易员
                etrDistribute.setTrader(dto.getTrader());
                // 设置委托代码
                etrDistribute.setEntrustId(dto.getEntrustId());
                //
                etrDistributeList.add(etrDistribute);
                // entity.setEtrDistributeList(etrDistributeList);
            }

/*            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,
                    dto.getEtrInvestPersonInfo().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    // 设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    // 设置指定交易员指定交易员 = 指定交易员
                    etrDistribute.setTrader(trader);
                    // 设置委托代码
                    etrDistribute.setEntrustId(dto.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
                // entity.setEtrDistributeList(etrDistributeList);
            }*/
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                }
                else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        // 自动分发
                        entity.setEtrDistributeList(
                                busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            baseEtrInvestInfoService.updateDelegate(entity);
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
//        if(baseEtrCashPositionCommService!=null){
//            baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
//        }
        // 指令分发推送信息到交易系统 TODO
        if(DictCodeList.是否_是.getValue().equals(isPushMsg)){
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", dto.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
    }
    /**
     * 流程启动时为附件绑定工作流代码 <功能详细描述>
     *
     * @param dto
     * @param operateType
     * @see
     * @since 1.0
     */
    public void doInsertMethod(CfetsEtrIbBondinfo002Dto dto, String operateType) {
        // 上传文件修改更新
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(dto.getWorkflowId());
        mstFileInputParameter.setFileList(dto.getFileArray());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }

    public BaseEtrInvestInfoDto initBaseEtrInvestInfoDto(CfetsEtrIbBondinfo002Dto dto) {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        try {
            EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
            dto.setInvManagerId(LoginUserUtil.getUserName());
            dto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().multiply(new BigDecimal(100))); //前台单位万元
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            //etrEntrustAdd.setOurEntNum(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            //etrEntrustAdd.setLendinEntNum(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            // 单位全价 fuyongsheng 20210917 edit start
            etrEntrustAdd.setFullPrice(dto.getUnitFprice());
            // 单位全价 fuyongsheng 20210917 edit end
            if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }

            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 1);
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entity;
    }
    // 指令授信 start
    public TrdProductDeal prepareDeal(CfetsEtrIbBondinfo002Dto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,entity);
        trdProductDeal.setTradeId(entity.getEntrustId());
        trdProductDeal.setpTradeId(entity.getEntrustId());
        trdProductDeal.setCounterId(null);
        trdProductDeal.setTradeAmt(entity.getPrinAmt());
        // 债券取券面/100
        trdProductDeal.setShareAmt(trdProductDeal.getPrinAmt().divide(new BigDecimal("100")));
        trdProductDeal.setPrinAmt(entity.getPrinAmt());
        return trdProductDeal;
    }

    public String qryPortfolioSecAcctId(Map<String, Object> param) {

        return cfetsEtrIbBondinfo002Mapper.qryPortfolioSecAcctId(param);
    }
}
