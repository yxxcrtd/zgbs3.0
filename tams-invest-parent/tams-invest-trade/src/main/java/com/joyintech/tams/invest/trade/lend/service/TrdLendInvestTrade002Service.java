/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.trade.lend.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseFinProductCashInputDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.interf.service.InterfComponentService;
import com.joyintech.tams.invest.interf.service.InterfComponentService.OrderCaller;
import com.joyintech.tams.invest.trade.lend.dto.TrdLendInvestTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrdLendInvestTrade002Service extends BaseService<TrdLendInvestTradeDto> implements BaseProcessService<TrdLendInvestTradeDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdLendInvestTrade002Service.class);

    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 平台日期组件接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 平安银行算法组件
     */
    @Autowired
    private BussCoreCalculateService bussCoreCalculateService;

    /**
     * DateCalService
     */
    @Autowired
    private DateCalService dateCalService;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private InterfComponentService interfComponentService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired(required = false)
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired(required = false)
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired(required = false)
    private PtlTradeMapper ptlTradeMapper;

    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired(required = false)
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired(required = false)
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired(required = false)
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired(required = false)
    private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;

    @Autowired(required = false)
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    
    @Autowired(required = false)
    private TrdProductDealMapper trdProductDealMapper;
    
    /**
     * 投管接口共通组件
     * add by cxq 20200212
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。 {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.MASERT)
    public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
        if (isMutiApproval) {
            // 业务编号
            String tradeId = entity.getTradeId();
            if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }

    public void prepareEntity(TrdLendInvestTradeDto dto) {
        // 金融产品交易Entity 构造开始----------------------------------------------------------
        // dto.setBusiType(DictCodeList.共通业务类型_拆借投资交易.getValue()); // 业务类型
        dto.setFinprodId(dto.getTradeId()); // 金融产品代码
        // 金融产品类型（投管分类）
        if (StringUtil.equals(dto.getBusiPs(), DictCodeList.回购拆借方向_拆入.getValue())) {
            if (StringUtil.equals(dto.getPaBussType(), DictCodeList.拆借类型_特殊目的载体.getValue())) {
                dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue());
            }
            else {
                dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
            }
        }
        else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        }
        // 金融产品类型（估值核算） TODO

        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        dto.setBranch(branchLong);

        dto.setCpriceAmt(dto.getFinTradeProduct().getPrin()); // 净价总额
        dto.setCcy(dto.getFinTradeProduct().getCcy()); // 交易币种
        dto.setPs(DictCodeList.交易方向_买入.getValue()); // 交易方向
        dto.setVdate(dto.getFinTradeProduct().getVdate()); // 起息日
        dto.setMdate(dto.getFinTradeProduct().getMdate()); // 到期日
        dto.setSettleDate(dto.getTradeDate()); // 交割日
        dto.setShareAmt(dto.getFinTradeProduct().getPrin()); // 交易份额
        dto.setPrinAmt(dto.getFinTradeProduct().getPrin()); // 交易本金
        dto.setTradeAmt(dto.getFinTradeProduct().getPrin()); // 交易金额

        // 利息总额
        // ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
        BigDecimal 利息总额 = bussCoreCalculateService.calc投资交易计算回购利息(dto.getPrinAmt(),
            ArithmeticUtil.divide(dto.getFinTradeProduct().getIntRate(), BigDecimal.valueOf(100)), dto.getVdate(), dto.getMdate(),
            dto.getFinTradeProduct().getBasis());
        dto.setIntAmt(利息总额);
        dto.getFinTradeProduct().setmPrinAmt(dto.getFinTradeProduct().getPrin());
        dto.getFinTradeProduct().setmIntAmt(利息总额);
        dto.getFinTradeProduct().setmTradeAmt(ArithmeticUtil.doAdd(dto.getPrinAmt(), dto.getIntAmt()));
        // dto.setTradeAmt(ArithmeticUtil.doAdd(dto.getPrinAmt(), dto.getIntAmt())); // 交易金额

        // dto.setFpriceAmt(
        // ArithmeticUtil.doAdd(dto.getFinTradeProduct().getPrin(), dto.getIntAmt()));
        dto.setIsClean(DictCodeList.是否_是.getValue()); // 是否结清
        dto.setIsCancel(DictCodeList.是否_否.getValue());

        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            dto.setTrader(personInfo.getTrader()); // 本方交易员
            dto.setCounterId(personInfo.getCounterId()); // 交易对手
            dto.setIsOutTrade(personInfo.getIsOutTrade()); // 是否外部交易
            dto.setCounterTrader(personInfo.getCounterTrader()); // 对手方交易员
            dto.setCounterContact(personInfo.getContactInfo()); // 联系方式
        }
        // 交易编号、交易日、备注 本身就在dto中
        // 金融产品交易Entity 构造结束----------------------------------------------------------

        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        // trdProductDealAdd.setProdId(dto.getTradeId()); // 理财产品代码
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
        trdProductDealAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setTrdProductDealAdd(trdProductDealAdd);
        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------

        // 组合交易表Entity 构造开始----------------------------------------------------------
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiNo(dto.getTradeId()); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getTradeId()); // 业务明细代码
        ptlTrade.setBusiType(DictCodeList.共通业务类型_拆借投资交易.getValue()); // 业务类型
        ptlTrade.setSettleDate(dto.getTradeDate()); // 实际发生日
        ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setPtlTrade(ptlTrade);
        // 组合交易表Entity 构造结束----------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (StringUtil.isBlank(dto.getIsGuarantee())) {
            // 查询组合是否保本
            PaPtlPortfolio paPtlPortfolio = bussCommonMapperDelegateService.selectBySinglePrimaryKey(PaPtlPortfolioMapper.class,
                dto.getPortfolioId(), PaPtlPortfolio.class);
            if (paPtlPortfolio != null) {
                dto.setIsGuarantee(paPtlPortfolio.getIsGuarantee());
            }
            else {
                dto.setIsGuarantee(DictCodeList.是否_否.getValue());
            }
        }
        if (bussTrdTransferOrderService != null) {
            if (StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                // 后台自动生成划款指令
                BussSettleOrderParameterDto orderParameterDto = new BussSettleOrderParameterDto();
                orderParameterDto.setIsTotalTrade(DictCodeList.是否_否.getValue());
                orderParameterDto.setBankLendingType(dto.getPaBussType());
                orderParameterDto.setPortfolioId(dto.getPortfolioId());

                TrdProductDeal orderParameterDeal = new TrdProductDeal();
                if (StringUtil.equals(dto.getBusiPs(), DictCodeList.回购拆借方向_拆入.getValue())) {
                    orderParameterDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
                }
                else {
                    orderParameterDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
                }
                orderParameterDeal.setTradeDate(dto.getTradeDate());
                orderParameterDeal.setPs(DictCodeList.交易方向_买入.getValue());
                orderParameterDeal.setCcy(dto.getFinTradeProduct().getCcy());
                orderParameterDeal.setTradeAmt(dto.getFinTradeProduct().getPrin());
                orderParameterDeal.setCounterId(personInfo.getCounterId());

                orderParameterDto.setDeal(orderParameterDeal);

                BussTrdTransferOrderDto resultOrderDto = bussTrdTransferOrderService.generateOrder(orderParameterDto);
                if (resultOrderDto != null) {
                    dto.setBussTrdTransferOrder(Arrays.asList(resultOrderDto));
                }
            }

            if (/*
                 * StringUtil.equals(dto.getIsGuarantee(), DictCodeList.是否_否.getValue()) &&
                 */!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTradeId()); // 交易编号
                dto.getBussTrdTransferOrder().get(0).setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
                dto.getBussTrdTransferOrder().get(0).setWorkflowId(dto.getWorkflowId()); // 工作流代码
                dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            }
        }
        // 划款指令表Entity 构造结束----------------------------------------------------------

        // 交易类金融产品Entity 构造开始----------------------------------------------------------
        dto.getFinTradeProduct().setFinprodId(dto.getTradeId()); // 金融产品代码
        dto.getFinTradeProduct().setBranch(branchLong); // 分支序号
        dto.getFinTradeProduct().setFinprodType2(dto.getFinprodType2()); // 金融产品类型（投管分类）
        dto.getFinTradeProduct().setIntType(DictCodeList.利率类型_固定利率.getValue()); // 利率类型
        dto.getFinTradeProduct().setIntRate(
            ArithmeticUtil.divide(dto.getFinTradeProduct().getIntRate(), BigDecimal.valueOf(100))); // 固定利率
        // 本金收益特征 = 拆出：保本，拆入不存
        if (DictCodeList.回购拆借方向_拆出.getValue().equals(dto.getBusiPs())) {
            // ※若画面联动1.7查询出的组合为保本
            dto.getFinTradeProduct().setCapiIncomeFeature(DictCodeListExt1.本金收益特征_保本.getValue());
        }
        dto.getFinTradeProduct().setCounterId(dto.getCounterId()); // 交易对手
        dto.getFinTradeProduct().setWorkflowId(dto.getWorkflowId()); // 工作流代码
        // 名义本金、名义币种、起息日、到期日、期限(天)、计息基础、交易日历、回购/拆借方向 本身就在dto中
        dto.getFinTradeProduct().setTermType(commonBusinessService.getRepTermType(
            DictCodeList.回购拆借方向_拆出.getValue().equals(
                dto.getPs()) ? DictCodeListExt1.金融产品类型_拆出.getValue() : DictCodeListExt1.金融产品类型_拆入.getValue(),
            dto.getFinTradeProduct().getTerm()));

        // 交易类金融产品Entity 构造结束----------------------------------------------------------

        // 金融产品分类Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(dto.getTradeId()); // 金融产品代码
        finProductType.setBranch(branchLong); // 分支序号
        finProductType.setType6(dto.getPaBussType()); // 分类6
        finProductType.setType7(dto.getBusiPs());
        finProductType.setType8(DictCodeList.平安资产类型_拆借.getValue());
        /* add by zhangpuyu 2019/8/13 start */
        FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
        finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
        finProductTypeConfig.setInputValue1(DictCodeList.平安资产类型_拆借.getValue());
        finProductTypeConfig.setInputValue2(dto.getBusiPs());
        finProductTypeConfig = restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
            finProductTypeConfig, FinProductTypeConfig.class);
        finProductType.setType9(finProductTypeConfig.getOutputValue2());
        finProductType.setType10(finProductTypeConfig.getOutputValue3());
        finProductType.setType11(finProductTypeConfig.getOutputValue4());
        finProductType.setType12(finProductTypeConfig.getOutputValue5());
        /* add by zhangpuyu 2019/8/13 end */
        finProductType.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setFinProductType(finProductType);
        // 金融产品分类Entity 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId()); // 交易编号
        // paTrdProductDeal.setPaBussType(dto.getPaBussType()); // 平安业务类型
        // 平安业务类型
        if (StringUtil.equals(dto.getBusiPs(), DictCodeList.回购拆借方向_拆入.getValue())) {
            if (StringUtil.equals(dto.getPaBussType(), DictCodeList.拆借类型_特殊目的载体.getValue())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_特拆拆入.getValue());
            }
            else {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆入.getValue());
            }
        }
        else {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆出.getValue());
        }
        paTrdProductDeal.setBackLendDirection(dto.getBusiPs());
        paTrdProductDeal.setDeptCode(personInfo.getTraderDept()); // 交易所属部门
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setPaTrdProductDeal(paTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        // 金融产品交易业务流程其他信息Entity 构造开始----------------------------------------------------------
        WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
        wflTrdProductDealOth.setTradeId(dto.getTradeId()); // 交易编号
        wflTrdProductDealOth.setBasis(dto.getFinTradeProduct().getBasis()); // 计息基础
        wflTrdProductDealOth.setRate(dto.getFinTradeProduct().getIntRate()); // 利率
        wflTrdProductDealOth.setTerm(dto.getFinTradeProduct().getTerm());// 期限(天)
        wflTrdProductDealOth.setmTradeAmt(ArithmeticUtil.doAdd(dto.getPrinAmt(), dto.getIntAmt()));// 到期金额
        wflTrdProductDealOth.setDlType(dto.getPaBussType());// 拆借类型
        wflTrdProductDealOth.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setWflTrdProductDealOth(wflTrdProductDealOth);
        // 金融产品交易业务流程其他信息Entity 构造结束----------------------------------------------------------

        // OPCIS表Entity 构造开始----------------------------------------------------------
        // TODO
        if (!StringUtil.equals(dto.getIsGuarantee(), DictCodeList.是否_是.getValue())) {

        }
        // OPCIS表Entity 构造结束----------------------------------------------------------
    }

    /**
     * 现金流相关逻辑
     *
     * @param dto 参数
     * @see
     * @since 1.0
     */
    private void cashFlowLogic(TrdLendInvestTradeDto dto) {
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);

        BaseFinProductCashInputDto inputDto = new BaseFinProductCashInputDto();

        // List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        // 现金流信息Entity-本金
        FinCashInfo finCashInfoPrin = new FinCashInfo();
        finCashInfoPrin.setCashId(commonBusinessService.generateCashFlowCode(dto.getTradeId(),
            DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong, DictCodeListExt1.现金流二级类型_本金.getValue()));
        finCashInfoPrin.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        finCashInfoPrin.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfoPrin.setCcy(dto.getCcy());
        finCashInfoPrin.setFinprodId(dto.getTradeId());
        finCashInfoPrin.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfoPrin.setBranch(branchLong);
        finCashInfoPrin.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashInfoList.add(finCashInfoPrin);

        // 现金流信息Entity-利息
        FinCashInfo finCashInfoInt = new FinCashInfo();
        finCashInfoInt.setCashId(commonBusinessService.generateCashFlowCode(dto.getTradeId(),
            DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong, DictCodeListExt1.现金流二级类型_利息.getValue()));
        finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        finCashInfoInt.setCcy(dto.getCcy());
        finCashInfoInt.setFinprodId(dto.getTradeId());
        finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfoInt.setBranch(branchLong);
        finCashInfoInt.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashInfoList.add(finCashInfoInt);

        inputDto.setFinCashInfoList(finCashInfoList);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        // 现金流支付规则表Entity-利息
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        finCashPayRule.setCashId(finCashInfoInt.getCashId());
        finCashPayRule.setEffDate(dto.getVdate());

        // 一体化对应etliu start
        // finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_到期支付.getValue());
        finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());
        // 一体化对应etliu end

        // 一体化对应etliu
        // finCashPayRule.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());

        finCashPayRule.setFirstPayDate(dto.getMdate());
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPayRule.setFinprodId(dto.getTradeId());
        finCashPayRule.setFinprodType(dto.getFinprodType());
        finCashPayRule.setFinprodType2(dto.getFinprodType2());
        finCashPayRule.setBranch(branchLong);
        finCashPayRule.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashPayRuleList.add(finCashPayRule);

        inputDto.setFinCashPayRuleList(finCashPayRuleList);

        // List<现金流计算规则表Entity>
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();

        // 现金流计算规则表Entity-利息
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        finCashCalcRule.setCashId(finCashInfoInt.getCashId());
        finCashCalcRule.setEffDate(dto.getVdate()); // 生效日期
        // 一体化对应etliu
        // finCashCalcRule.setRelaeseDate(dto.getVdate()); // 公布日期
        finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        finCashCalcRule.setBasis(dto.getFinTradeProduct().getBasis());
        finCashCalcRule.setYield(dto.getFinTradeProduct().getIntRate());
        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        finCashCalcRule.setFinprodId(dto.getTradeId()); // 金融产品代码
        finCashCalcRule.setFinprodType(dto.getFinprodType());
        finCashCalcRule.setFinprodType2(dto.getFinprodType2());
        finCashCalcRule.setBranch(branchLong);
        finCashCalcRule.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashCalcRuleList.add(finCashCalcRule);

        inputDto.setFinCashCalcRuleList(finCashCalcRuleList);

        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();

        // 现金流利率信息表Entity-利息
        FinRateInfo finRateInfo = new FinRateInfo();
        finRateInfo.setCashId(finCashInfoInt.getCashId()); // 现金流代码
        finRateInfo.setEffDate(dto.getVdate());
        // 一体化对应etliu
        // finRateInfo.setRateType(DictCodeList.利率类型_固定利率.getValue());
        finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
        finRateInfo.setBasis(dto.getFinTradeProduct().getBasis());
        finRateInfo.setRate(finCashCalcRule.getYield());
        finRateInfo.setFinprodId(dto.getTradeId()); // 金融产品代码
        finRateInfo.setFinprodType(dto.getFinprodType());
        finRateInfo.setFinprodType2(dto.getFinprodType2());
        finRateInfo.setBranch(branchLong);
        finRateInfo.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finRateInfoList.add(finRateInfo);

        inputDto.setFinRateInfoList(finRateInfoList);

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();

        // 现金流计划表Entity 到期还本
        FinCashPlan finCashPlan = new FinCashPlan();
        finCashPlan.setCashId(finCashInfoPrin.getCashId());
        finCashPlan.setCashNum(1L);
        finCashPlan.setCashType(DictCodeList.现金流类型_到期还本.getValue());
        finCashPlan.setVdateUnadjust(dto.getVdate());
        finCashPlan.setMdateUnadjust(dto.getMdate());
        finCashPlan.setPayDateUnadjust(dto.getMdate());
        finCashPlan.setVdate(dto.getVdate());
        finCashPlan.setMdate(dto.getMdate());
        finCashPlan.setPayDate(dto.getMdate());
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()) + 1);
        finCashPlan.setPrinAmt(dto.getPrinAmt());
        finCashPlan.setIntPrinAmt(dto.getPrinAmt());
        finCashPlan.setIntAmt(BigDecimal.ZERO);
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        finCashPlan.setCashAmt(dto.getPrinAmt());
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPlan.setFinprodId(dto.getTradeId());
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        finCashPlan.setFinprodType(dto.getFinprodType());
        finCashPlan.setFinprodType2(dto.getFinprodType2());
        finCashPlan.setBranch(branchLong);
        finCashPlan.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashPlanList.add(finCashPlan);

        // 现金流计划表Entity 到期付息
        finCashPlan = new FinCashPlan();
        finCashPlan.setCashId(finCashInfoInt.getCashId());
        finCashPlan.setCashNum(2L);
        finCashPlan.setCashType(DictCodeList.现金流类型_到期收息.getValue());
        finCashPlan.setVdateUnadjust(dto.getVdate());
        finCashPlan.setMdateUnadjust(dto.getMdate());
        finCashPlan.setPayDateUnadjust(dto.getMdate());
        finCashPlan.setVdate(dto.getVdate());
        finCashPlan.setMdate(dto.getMdate());
        finCashPlan.setPayDate(dto.getMdate());
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()) + 1);
        finCashPlan.setPrinAmt(BigDecimal.ZERO);
        finCashPlan.setIntPrinAmt(dto.getPrinAmt());

        // 计划支付利息
        // ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
        finCashPlan.setIntAmt(dto.getIntAmt());

        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        finCashPlan.setCashAmt(finCashPlan.getIntAmt());
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPlan.setFinprodId(dto.getTradeId());
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        finCashPlan.setFinprodType(dto.getFinprodType());
        finCashPlan.setFinprodType2(dto.getFinprodType2());
        finCashPlan.setBranch(branchLong);
        finCashPlan.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashPlanList.add(finCashPlan);

        inputDto.setFinCashPlanList(finCashPlanList);

        // 调用共通登录现金流相关数据
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertFinProductCashData",
            inputDto, Boolean.class);

        // bussCommonMapperDelegateService.insertAutoClassifyBatch(finCashInfoList, finCashPayRule,
        // finCashCalcRule, finRateInfo, finCashPlanList);
    }

    /**
     * 7.7写入交易信息进交易报送信息表，为中债报送用
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_7(TrdLendInvestTradeDto dto) {
        // 7.7写入交易信息进交易报送信息表，为中债报送用
        // 1)查询产品登记编码
        String prodRegistCode = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/" + dto.getPortfolioId(), null,
            String.class);

        if (StringUtil.isBlank(prodRegistCode)) {
            return;
        }

        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode); // 产品登记编码
        repTrade.setRepBankTradeCode(dto.getTradeId()); // 行内交易编码
        repTrade.setRepBankAssetCode(dto.getTradeId()); // 行内资产/负债编码
        repTrade.setRepCcy(dto.getCcy()); // 币种
        repTrade.setRepOccurMoney(dto.getPrinAmt()); // 发生金额

        // 折算人民币金额（元） = 画面.本金金额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(dto.getCcy(), DateUtil.format(dto.getTradeDate()));
        BigDecimal 折算人民币金额 = ArithmeticUtil.multiply(dto.getPrinAmt(), 收盘价);
        repTrade.setRepMoneyCny(折算人民币金额);

        // 资金流动类型: 数据字典缓存.资金流动类型 ※根据交易方向判断，若拆入，即拆入和正回购首期，若拆出，即为拆出和逆回购首期
        String 资金流动类型 = DictCodeListExt2.资金流动类型_同业拆入和卖出回购首期.getValue();
        if (StringUtil.equals(dto.getBusiPs(), DictCodeList.回购拆借方向_拆出.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_拆放同业和买入返售首期.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);
        repTrade.setRepDelCode(dto.getTradeId()); // 成交编号/合同号
        repTrade.setRepCount(dto.getPrinAmt()); // 数量

        repTrade.setRepTradeDate(dto.getTradeDate()); // 交易日
        repTrade.setRepTradeComponent(dto.getCounterId()); // 交易对手方
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        repTrade.setRepStatusDate(iPlatformDate.getSysDate()); // 数据日期
        repTrade.setRepRemark(dto.getRemark()); // 备注

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
            Arrays.asList(repTrade), Boolean.class);
    }

    /**
     * 调用资产负债信息报送组件 写入资产负债报送信息
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_8_1(TrdLendInvestTradeDto dto) {
        // 资产负债报送信息Entity
        RepAssetDept repAssetDept = new RepAssetDept();
        repAssetDept.setRepFinprodId(dto.getTradeId()); // 金融产品代码
        repAssetDept.setFinprodMarketId(dto.getTradeId()); // 市场代码
        // 发行机构代码
        String 发行机构代码 = CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID);
        repAssetDept.setRepIssuePartyId(发行机构代码);
        repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_同业拆入.getValue());
        // 资产/负债类别
        if (StringUtil.equals(dto.getPaBussType(), DictCodeList.拆借类型_普通拆借.getValue())
            && StringUtil.equals(dto.getPaTrdProductDeal().getBackLendDirection(), DictCodeList.回购拆借方向_拆出.getValue())) {
            repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_拆放同业.getValue());
        }
        repAssetDept.setRepCcy(dto.getCcy());// 币种
        repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        // 交易流通场所
        repAssetDept.setRepTradeMarket(DictCodeList.交易流通场所_全国银行间同业拆借市场.getValue());
        // 调用资产负债信息报送组件
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepAssetDebt",
            Arrays.asList(repAssetDept), Boolean.class);
    }

    /**
     * 调用负债(同业拆入卖出回购)报送信息组件 写入负债(同业拆入卖出回购)报送信息
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_8_2(TrdLendInvestTradeDto dto) {
        // 拆入、正回购 -> 负债(拆入或正回购)类要素登记
        if (StringUtil.equals(dto.getBusiPs(), DictCodeList.回购拆借方向_拆入.getValue())) {
            // 负债(同业拆入/卖出回购)报送信息Entity
            RepLiabilities repLiabilities = new RepLiabilities();
            repLiabilities.setRepFinprodId(dto.getTradeId()); // 金融产品代码
            repLiabilities.setRepValueDate(dto.getVdate()); // 起息日
            repLiabilities.setRepExpiringDate(dto.getMdate()); // 到期日
            repLiabilities.setRepCounterparty(dto.getCounterId()); // 对手方
            repLiabilities.setRepAnnualInterestRate(dto.getFinTradeProduct().getIntRate().multiply(new BigDecimal(100))); // 年利率%
            repLiabilities.setRepBasis(dto.getFinTradeProduct().getBasis()); // 计息基础
            repLiabilities.setRepRemark(dto.getRemark()); // 备注

            // 调用负债(同业拆入卖出回购)报送信息组件
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepLiabilities",
                Arrays.asList(repLiabilities), Boolean.class);
        }
        // 拆出、逆回购 -> 拆放同业及买入返售
        else {
            RepAssetInterLend repAssetInterLend = new RepAssetInterLend();
            repAssetInterLend.setRepFinprodId(dto.getTradeId()); // 金融产品代码
            repAssetInterLend.setRepVdate(dto.getVdate()); // 起息日
            repAssetInterLend.setRepMdate(dto.getMdate()); // 到期日
            repAssetInterLend.setRepTradeTarget(dto.getCounterId()); // 对手方
            repAssetInterLend.setRepRateYear(dto.getFinTradeProduct().getIntRate().multiply(new BigDecimal(100))); // 年利率%
            repAssetInterLend.setRepBasis(dto.getFinTradeProduct().getBasis()); // 计息基础
            repAssetInterLend.setRepRemark(dto.getRemark()); // 备注

            // 调用负债(拆放同业及买入返售)报送信息组件
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepDlRepoInfo",
                Arrays.asList(repAssetInterLend), Boolean.class);
        }
    }

    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(TrdLendInvestTradeDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId=" + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
                // 交易状态=“撤销”
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                wflTrdProductDeal.setpTradeId(entity.getTradeId());
                // 撤销说明=画面.撤销说明
                wflTrdProductDeal.setCancelRemark(entity.getRevokeReason());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                wflTrdProductDeal.setTradeId(entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, baseDto.getPaTrdProductDeal());
                wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                wflPaTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId());
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                // 转换
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
                // 批量插入
                // bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class, wflTrdProductDeal,
                // WflPaTrdProductDealMapper.class, wflPaTrdProductDeal, WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());

                        String orderId;
                        if (baseGetBusinessId != null) {
                            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                            bussinessCodeParameter.setSeqNo("9998");
                            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
                        }
                        else {
                            orderId = IDGenerator.generate();
                        }
                        wflStlPayOrders.setOrderId(orderId);
                        wflStlPayOrders.setTradeId(wflTrdProductDeal.getTradeId());
                        wflStlPayOrders.setpTradeId(stlPayOrdersDto.getOrderId());
                        wflStlPayOrders.setProdId(wflTrdProductDeal.getTradeId());
                        wflStlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        // bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class, wflStlPayOrders);
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                    }
                }
            }
        }
    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdLendInvestTradeDto entity) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (CollectionUtils.isNotEmpty(entity.getFileArray()) && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getFileArray().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        this.prepareEntity(entity);
        
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        // 一体化对应etliu start
        wflTrdProductDeal.setIntAmt(null);
        // 一体化对应etliu end
        //授信占用
        if (StringUtil.equals(entity.getBusiPs(), DictCodeList.回购拆借方向_拆出.getValue())){
            // 调用授信共同 sjj start
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, entity);
            AllTradeCommonInputDto commonInputDto = new AllTradeCommonInputDto();
            deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            deal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
            deal.setPs(DictCodeList.交易方向_买入.getValue());
            deal.setTgBusiType(DictCodeList.平安资产类型_拆借.getValue());
            deal.setPsPayment(entity.getBusiPs());
            commonInputDto.setTrdProductDeal(deal);
            AllTradeCommonUtil.handleBeforeTradeComplete(commonInputDto,false);
            // 调用授信共同 sjj end
        }
        
        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
        wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易业务流程其他信息Entity -> 金融产品交易业务流程其他信息表
        WflTrdProductDealOth wflTrdProductDealOth = entity.getWflTrdProductDealOth();
        wflTrdProductDealOth.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//        转换
        wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
        wflTrdProductDealOthMapper.insertSelective(wflTrdProductDealOth);

        finTradeProductMapper.deleteByPrimaryKey(entity.getFinTradeProduct());
        finTradeProductMapper.insertSelective(entity.getFinTradeProduct());

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
        }
        // OPCIS表Entity TODO

        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(entity);
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
        }
        //        委托代码不为空处理委托代码
        if(StringUtil.isNotEmpty(entity.getEntrustId())){
            baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
        }
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

        // 撤销时，子组合需要调估值共通
        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }
        baseTradeProductDealCommService.execute(finProductTradeDto);

        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID", workflowId);
        //
        // // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD", "WORKFLOW_ID", workflowId);
        //
        // // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL", "WORKFLOW_ID", workflowId);
        //
        // // 金融产品交易业务流程其他信息Entity -> 金融产品交易业务流程其他信息表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_OTH", "WORKFLOW_ID", workflowId);
        //
        // // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID", workflowId);
        baseTradeProductDealCommService.deleteWflData(workflowId);
        // opics TODO
    }

    @Override
    public void doInsert(TrdLendInvestTradeDto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            this.handleBussFlowData(entity);
        }
        // 撤销交易
        else {
            this.doRevokeInsert(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdLendInvestTradeDto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());

            // 重新insert
            this.handleBussFlowData(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        // OPCIS表Entity TODO

        baseTradeProductDealCommService.delete(workflowId);
    }

    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(TrdLendInvestTradeDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId=" + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(), PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(
                        paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdProductDeal.setCreateTime(new Date());
                    paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                    paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
                    // bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class, paTrdProductDeal);

                    PtlTrade ptlTrade = new PtlTrade();
                    ptlTrade.setPortfolioId(entity.getPortfolioId());
                    ptlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
                    ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                    ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade,
                        PtlTrade.class);
                    ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    // bussCommonMapperDelegateService.insertSelective(PtlTradeMapper.class, ptlTrade);
                    ptlTradeMapper.insertSelective(ptlTrade);
                    if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        PtlTrade actPtlTrade = new PtlTrade();
                        BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, ptlTrade);
                        actPtlTrade.setPortfolioId(entity.getActPortfolioId());
                        actPtlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
                        actPtlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, actPtlTrade,
                            PtlTrade.class);
                        actPtlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        // bussCommonMapperDelegateService.insertSelective(PtlTradeMapper.class, ptlTrade);
                        ptlTradeMapper.insertSelective(actPtlTrade);
                    }
                }
                // 调用投管接口 add by cxq 20190212 start
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdLendInvestTrade002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setRevokeReason(entity.getRevokeReason());
                investInterfCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
                investInterfCommDto.setFinTradeProduct(entity.getFinTradeProduct());
                PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                if (StringUtil.equals(entity.getBusiPs(), DictCodeList.回购拆借方向_拆入.getValue())) {
                    if (StringUtil.equals(entity.getPaBussType(), DictCodeList.拆借类型_特殊目的载体.getValue())) {
                        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_特拆拆入.getValue());
                    }
                    else {
                        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆入.getValue());
                    }
                }
                else {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆出.getValue());
                }
                investInterfCommDto.setPaTrdProductDeal(paTrdProductDeal);
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    investInterfCommDto.setStlPayOrders(
                    		baseDto.getStlPayOrdersList().get(0));
                }
                investInterfCommService.doInvestInterf(investInterfCommDto);
//                investInterfCommService.doSendTradeToInterf(entity.getTradeId()+"R" ,DictCodeList.平安资产类型_拆借.getValue());

             // 调用投管接口 add by cxq 20190212 end
            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);

            if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getPortfolioId()));
            }
            if (StringUtil.equals(entity.getBusiPs(), DictCodeList.回购拆借方向_拆出.getValue())) {
            	TrdProductDeal trdProductDeal = trdProductDealMapper.selectByPrimaryKey(entity.getTradeId());
//                TrdProductDeal trdProductDeal = new TrdProductDeal();
//                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
            	trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                trdProductDeal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
                trdProductDeal.setTgBusiType(DictCodeList.平安资产类型_拆借.getValue());
                trdProductDeal.setPsPayment(entity.getBusiPs());
                inputDto.setTrdProductDeal(trdProductDeal);
                AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
            }
            // // 一体化对应etliu start
            // // 所有交易生效后调用的共通方法
            // for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
            // AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            // inputDto.setProdid(baseDto.getTrdProductDealAdd().getPortfolioId());
            // inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            // inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());
            // AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // }
            // // 一体化对应etliu end

        }


        // 删除原业务流程记录
        this.deleteBussFlowData(entity.getWorkflowId());

        // 插入风险试算结果
        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

        // 调用基础组件.【交易报送组件】删除交易报送信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
            Arrays.asList(entity.getTradeId()), Boolean.class);

        // 调用资产负债信息报送组件 删除拆借报送信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepAssetDebt",
            Arrays.asList(entity.getTradeId()), Boolean.class);
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepLiabilities",
            Arrays.asList(entity.getTradeId()), Boolean.class);
    }

    @Override
    public void doComplete(TrdLendInvestTradeDto entity, OperationType operationType) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            this.prepareEntity(entity);

            // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
            // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
            trdProductDeal.setSettleSpeed(entity.getSettleSpreed());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            // 一体化对应etliu start
            trdProductDeal.setIntAmt(null);
            // 一体化对应etliu end

            // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());

            // 金融产品交易扩展表Entity -> 金融产品交易扩展表
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getPaTrdProductDeal());

            // 组合交易表Entity -> 组合交易信息表
            PtlTrade ptlTrade = entity.getPtlTrade();
            // 转换

            // 批量插入
            // bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class, paTrdProductDeal, PtlTradeMapper.class,ptlTrade);
            paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            ptlTradeList.add(ptlTrade);
            ptlTradeMapper.insertSelective(ptlTrade);

            if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                && !StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
                PtlTrade ptlTrade1 = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
                ptlTrade1.setPortfolioId(entity.getActPortfolioId());
                ptlTradeMapper.insertSelective(ptlTrade1);
                ptlTradeList.add(ptlTrade1);
            }
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
            StlPayOrdersDto stlPayOrders = null;
            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, entity.getBussTrdTransferOrder().get(0));

                // bussCommonMapperDelegateService.insertBatch(StlPayOrdersMapper.class, stlPayOrders);
            }

            // OPCIS表Entity TODO

            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
            // 划款指令
            if (stlPayOrders != null) {
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
            }
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            // 委托代码不为空处理委托代码
            if (StringUtil.isNotEmpty(entity.getEntrustId())) {
                baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
            }

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);

            if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getPortfolioId()));
            }

            // 7.2删除原业务流程表中的交易记录
            String workflowId = entity.getWorkflowId();
            if (StringUtil.isBlank(workflowId)) {
                WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
                wflTrdProductDealKey.setTradeId(entity.getTradeId());
                wflTrdProductDealKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                    WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

                if (wflTrdProductDeal != null) {
                    workflowId = wflTrdProductDeal.getWorkflowId();
                }
            }

            // 7.3写入资产信息进交易类金融产品表
            FinTradeProduct finTradeProduct = entity.getFinTradeProduct();
            finTradeProduct.setCapiIncomeFeature(DictCodeList.是否_是.getValue());
            // 7.5写入拆借类型进金融产品分类表
            FinProductType finProductType = entity.getFinProductType();

            // 一体化对应etliu start
            ValItemValueGenUtil.genTypeForFinProductType(finTradeProduct, finProductType);
            // 一体化对应etliu end

            finProductTypeMapper.insertSelective(finProductType);

            // 7.6写入现金流相关信息表
            this.cashFlowLogic(entity);

            // 7.7写入交易信息进交易报送信息表，为中债报送用
            this.dealInfoCbReportLogic_7_7(entity);

            // 插入风险试算结果
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

            // 7.8写入负债(同业拆入/卖出回购)报送表，为中债报送用
            // 资产负债报送信息Entity
            this.assetDebtCbReportLogic_7_8_1(entity);
            // 负债(同业拆入/卖出回购)报送信息Entity
            this.assetDebtCbReportLogic_7_8_2(entity);

            // 发送Murex接口处理
            // 平安个性化调用接口内容删除 delete by renping 20190926 start
            // this.sendMurex(entity, false);
            // 平安个性化调用接口内容删除 delete by renping 20190926 end

            // 一体化对应etliu start
            // 所有交易生效后调用的共通方法
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);
            inputDto.setTrdProductDealAdd(trdProductDealAdd);
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            inputDto.setPtlTradeList(ptlTradeList);
            if (stlPayOrders != null) {
                inputDto.setStlPayOrders(stlPayOrders);
            }
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 一体化对应etliu end
            // 调用投管接口 add by cxq 20190212 start
            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
            investInterfCommDto.setPageId("TrdLendInvestTrade002");
            investInterfCommDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
            investInterfCommDto.setTrdProductDeal(trdProductDeal);
            investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
            investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
            investInterfCommDto.setPaTrdProductDeal(paTrdProductDeal);//ArithmeticUtil
            finTradeProduct.setIntRate(ArithmeticUtil.multiply(finTradeProduct.getIntRate(),new BigDecimal(100)));
            investInterfCommDto.setFinTradeProduct(finTradeProduct);
            investInterfCommService.doInvestInterf(investInterfCommDto);

//            investInterfCommService.doSendTradeToInterf(entity.getTradeId() ,DictCodeList.平安资产类型_拆借.getValue());
            // 调用投管接口 add by cxq 20190212 end

            if (StringUtil.equals(entity.getBusiPs(), DictCodeList.回购拆借方向_拆入.getValue())){
                // 调用授信共同 sjj start
                TrdProductDeal deal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(deal, entity);
                AllTradeCommonInputDto commonInputDto = new AllTradeCommonInputDto();
                deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                deal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
                deal.setPs(DictCodeList.交易方向_卖出.getValue());
                deal.setTgBusiType(DictCodeList.平安资产类型_拆借.getValue());
                deal.setPsPayment(entity.getBusiPs());
                commonInputDto.setTrdProductDeal(deal);
                AllTradeCommonUtil.handleBeforeTradeComplete(commonInputDto,false);
                // 调用授信共同 sjj end
            }

            this.deleteBussFlowData(workflowId);
        }
        // 撤销
        else {
            this.doCompleteRevoke(entity);

            // 发送Murex接口处理
            // this.sendMurex(entity, true);
        }
     // 调接口 by 赵东东 20191206 start
        try {
        List<String> tradeIds = new ArrayList<String>();
        tradeIds.add(entity.getTradeId());
        restTemplate.postForObject(
        		ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
        		Map.class);
        }catch(Exception e) {
        	
        }
        // 调接口 by 赵东东 20191206  end
    }

    public TrdLendInvestTradeDto getTrdLendInvestTradeDto(String tradeId) {
        String BRANCH_0 = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdLendInvestTrade/getTrdLendInvestTradeDto/" + tradeId + "/" + BRANCH_0,
            null, TrdLendInvestTradeDto.class);
    }

    /**
     * 根据工作流代码查询交易编号
     *
     * @param workflowId 工作流代码
     * @return 交易编号
     * @see
     * @since 1.0
     */
    public String selectTradeIdByWorkflowId(String workflowId) {
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdLendInvestTrade/selectTradeIdByWorkflowId/" + workflowId, null,
            String.class);
    }

    /**
     * 检查到期日是否为节假日 到期日如果是节假日 要提醒
     *
     * @param inputDto 画面输入参数
     * @param result valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public AjaxResult checkMdate(TrdLendInvestTradeDto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }

        if (inputDto.getFinTradeProduct() == null || inputDto.getFinTradeProduct().getMdate() == null
            || StringUtil.isBlank(inputDto.getFinTradeProduct().getCalendarId())) {
            return null;
        }

        boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(inputDto.getFinTradeProduct().getMdate(),
            inputDto.getFinTradeProduct().getCalendarId());
        if (isDealCalendarHoliday) {
            AjaxResult wr = new AjaxResult();
            wr.setData(getMessage("BUSS_COM_E_0039", getMessage("TrdLendInvestTrade002.mdate")));
            return wr;
        }

        return null;
    }

    /**
     * 发送Murex接口处理
     * 
     * @param entity 拆借投资交易信息
     * @param revokeFlag 撤销标识
     * @see
     * @since 1.0
     */
    private void sendMurex(TrdLendInvestTradeDto entity, boolean revokeFlag) {
        if (StringUtil.equals(entity.getIsGuarantee(), DictCodeList.是否_否.getValue())) {
            if (interfComponentService != null) {
                List<Map<String, Object>> sendData = new ArrayList<>();
                Map<String, Object> infoMap = new HashMap<>();

                // borrow：拆入；lend: 拆出
                infoMap.put("TRAN_DIR", "borrow");// 拆入
                if (StringUtil.equals(DictCodeList.回购拆借方向_拆出.getValue(), entity.getPs())) {
                    infoMap.put("TRAN_DIR", "lend");// 买卖方向
                }

                infoMap.put("TRAN_AMT", entity.getFinTradeProduct().getPrin().toPlainString());// 交易金额

                infoMap.put("TRAN_CCY", entity.getFinTradeProduct().getCcy());// 交易货币

                infoMap.put("INT_EFFECT_DATE", DateUtil.format(entity.getFinTradeProduct().getVdate()));// 起息日

                infoMap.put("EXPIRE_DATE", DateUtil.format(entity.getFinTradeProduct().getMdate()));// 到期日

                infoMap.put("LEND_INT_RATE",
                    ArithmeticUtil.divide(entity.getFinTradeProduct().getIntRate(), BigDecimal.valueOf(100)).toPlainString());// 拆借利率

                if (StringUtil.equals(entity.getFinTradeProduct().getBasis(), DictCodeList.计息基础_A_360.getValue())) {
                    infoMap.put("INTEREST_RULE", "LIN ACT/360");// 计息规则
                }
                else if (StringUtil.equals(entity.getFinTradeProduct().getBasis(), DictCodeList.计息基础_A_365.getValue())) {
                    infoMap.put("INTEREST_RULE", "LIN ACT/365");// 计息规则
                }

                // infoMap.put("LEND_TYPE");//拆借类型
                // infoMap.put("INDEX");//指标
                // infoMap.put("METAL_PRICE");//贵金属价格
                // infoMap.put("REPAY_INTEREST_PERIOD");//付息周期

                infoMap.put("RATE_TYPE", "fixedRate");// 利率类型

                // infoMap.put("PRODUCT");//产品
                // infoMap.put("PURITY");//成色
                // infoMap.put("DELIVERY_ACCT");//交割账户

                infoMap.put("OUTER_TRAN_NO", entity.getTradeId());// 外部交易号

                infoMap.put("INVEST_GROUP", entity.getPortfolioId());// 投组

                if (entity.getTrdInvestPersonInfo() != null) {
                    if (entity.getTrdInvestPersonInfo().getCounterId() != null) {
                        MstCounterParty mstCounterParty = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                            MstCounterPartyMapper.class, entity.getTrdInvestPersonInfo().getCounterId(), MstCounterParty.class);
                        if (mstCounterParty != null) {
                            infoMap.put("COUNTER_PARTY_NAME", mstCounterParty.getCounterName());// 交易对手名称
                        }
                    }
                }

                infoMap.put("DEAL_DATE", entity.getTradeDateText());// 成交日

                // infoMap.put("UPPER_SYS");//上游系统
                // infoMap.put("CLEARING_MODE");//清算方式

                infoMap.put("TRAN_OPERATION_TYPE", "A – NEW");// 交易操作类型
                if (revokeFlag) {
                    infoMap.put("TRAN_OPERATION_TYPE", "R - Reverse");// 交易操作类型
                }

                // infoMap.put("EVENT_DATE");//事件日期

                infoMap.put("DEALER_ROLE", entity.getTrdInvestPersonInfo().getTrader());// 交易员

                infoMap.put("REMARK", entity.getRemark());// 备注

                // infoMap.put("OLD_COUNTER_PARTY");//原始交易对手
                // infoMap.put("GOLD_IMPORT_MODE");//黄金进口方式
                // infoMap.put("TRAN_TYPE");//交易类型
                // infoMap.put("PERSONAL_OLD_COUNTER_PARTY_BECIF_NO");//对公原始交易对手BECIF号
                // infoMap.put("ACCUMUL_NUM");//归集数目
                // infoMap.put("COMPANY_APPROVE_NO");//企业批复号
                // infoMap.put("SEC_ACCT_NAME");//资金账户名称
                // infoMap.put("SEC_OPEN_BRANCH");//资金开户行
                // infoMap.put("SEC_ACCT_NO");//资金账号
                // infoMap.put("SEC_OPEN_INTERBANK_BRANCH_ID");//资金开户行联行行号
                // infoMap.put("REQUEST_SEQ_NO");//请求流水号
                // infoMap.put("OLD_TRADER");//原始交易员
                // infoMap.put("PROD_PROPERTY");//产品属性

                sendData.add(infoMap);
                try {
                    interfComponentService.sendMurexRequest(OrderCaller.LNBR001, sendData);
                }
                catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId)
        throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdLendInvestTradeDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
//                return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getFinTradeProduct().getPrin(),
//                    LoginUserUtil.getUserName(), DictCodeList.平安资产类型_拆借.getValue(), dto.getPaBussType(), dto.getTradeMarket(),
//                    dto.getBusiPs()
//                    // 资产类型改为授权类型 add by renping 2019-08-06 start
//                    , null
//                // 资产类型改为授权类型 add by renping 2019-08-06 end
//                );
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getFinTradeProduct().getPrin(),LoginUserUtil.getUserName(),
                    DictCodeList.平安资产类型_拆借.getValue(), dto.getPaBussType(), dto.getTradeMarket(), dto.getBusiPs()
                    , null ,entity.getWorkflowId());
                
                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                params.put("entrustId", dto.getEntrustId());
                return commonBusinessService.是否需要上级审批(params);
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
        // return null;
    }

    @Override
    public void doBack(TrdLendInvestTradeDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
        if (StringUtil.equals(entity.getBusiPs(), DictCodeList.回购拆借方向_拆出.getValue())) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, entity);
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            deal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            deal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
            deal.setPs(DictCodeList.交易方向_买入.getValue());
            deal.setTgBusiType(DictCodeList.平安资产类型_拆借.getValue());
            deal.setPsPayment(entity.getBusiPs());
            inputDto.setTrdProductDeal(deal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
        }
    }

    @Override
    public void doRevoke(TrdLendInvestTradeDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
    }
}
