package com.joyintech.tams.invest.trade.transfer.trdrolloverpledgedrepo.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.BussCoreCalculateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.calculator.CalendarUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.interf.service.InterfComponentService;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.tams.invest.trade.transfer.trdrolloverpledgedrepo.dto.FinDealPledgeDto;
import com.joyintech.tams.invest.trade.transfer.trdrolloverpledgedrepo.dto.TrdRolloverPledgedRepo001Dto;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class TrdRolloverPledgedRepo001Service extends BaseService<TrdRolloverPledgedRepo001Dto> implements BaseProcessService<TrdRolloverPledgedRepo001Dto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdRolloverPledgedRepo001Service.class);

    /**
     * 共同
     */
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;
    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
    @Autowired
    private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private BussCoreCalculateService bussCoreCalculateService;
    /*@Autowired
    private BaseGetBussinessId baseGetBussinessId;*/
    /**
     * 平台日期组件接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private InterfComponentService interfComponentService;

    /**
     * 共同
     */
    /*@Autowired
    private TrdProductDealMapper trdProductDealMapper;*/
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    /*@Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;*/
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    /*@Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;*/
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;
    /*@Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;*/
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;
    /**
     * bussCommonMapperDelegateService
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * 查询组合信息
     * @param param 参数
     * @return 组合信息
     */
    public List<SelectOptionDto> selectPortfolioId(Map<String, Object> param) {
        return simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/trade/transfer/trdrolloverpledgedrepo/TrdRolloverPledgedRepo/selectPortfolioId",
                param, SelectOptionDto.class);
    }

    /**
     * 查询质押券信息
     * @param param 参数
     * @return 质押券信息
     */
    public List<FinDealPledgeDto> selectPledgedRepoRolloverList(Map<String, Object> param) {
        return simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/trade/transfer/trdrolloverpledgedrepo/TrdRolloverPledgedRepo/selectPledgedRepoRolloverList",
                param, FinDealPledgeDto.class);
    }

    /**
     *
     * 设置列表数据
     * @param param 参数
     * @return 列表数据
     */
    public TrdRolloverPledgedRepo001Dto setListInfoDto(Map<String, Object> param) throws IOException {
        TrdRolloverPledgedRepo001Dto listInfoDto = new TrdRolloverPledgedRepo001Dto();
        List<FinDealPledgeDto> finDealPledgeList = new ArrayList<>();
        TrdRolloverPledgedRepo001Dto dto = BeanUtil.mapToObject(param, TrdRolloverPledgedRepo001Dto.class);
        List<FinDealPledgeDto> list = new ArrayList<>();

        if (StringUtil.isNotEmpty(param.get("pledgedRepoRolloverList"))) {
            list = JsonUtils.json2List(JsonUtils.object2Json(param.get("pledgedRepoRolloverList")), FinDealPledgeDto.class);
            dto.setPledgedRepoRolloverList(list);
        }
        if (StringUtil.isNotEmpty(dto)) {
            //生成交易编号
            if (!StringUtil.isNotEmpty(listInfoDto.getTradeId())) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                if (StringUtil.equals(dto.getPs(),
                        DictCodeList.回购拆借方向_正回购.getValue())) {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                } else {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                }
                listInfoDto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            }
            listInfoDto.setPortfolioId(dto.getPortfolioId());
            listInfoDto.setPortfolioName(dto.getPortfolioName());
            listInfoDto.setTradeDate(dto.getTradeDate());
            listInfoDto.setRolloverPrinAmt(dto.getRolloverPrinAmt());
            listInfoDto.setRebackPrice(dto.getRebackPrice());
            listInfoDto.setVdate(dto.getVdate());
            listInfoDto.setMdate(dto.getMdate());
            listInfoDto.setRolloverRepoIntAmt(dto.getRolloverRepoIntAmt());
            listInfoDto.setRolloverTradeAmt(dto.getRolloverTradeAmt());
            if (StringUtil.isNotEmpty(dto.getPs()) && StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())
                    && StringUtil.equals(dto.getRepoType(), DictCodeList.回购类型_质押式协议回购.getValue())) {
                listInfoDto.setPledgedRepoRolloverList(dto.getPledgedRepoRolloverList());
            }
            else if(StringUtil.isNotEmpty(dto.getPs())&&StringUtil.equals(dto.getPs(),DictCodeList.回购拆借方向_逆回购.getValue())){
                finDealPledgeList = dto.getPledgedRepoRolloverList();
                //非最后一条数据
                if (dto.getAvaRolloverAmt().compareTo(dto.getRolloverPrinAmt()) > 0) {
                    if (!StringUtil.isEmptyList(finDealPledgeList) && finDealPledgeList.size() > 0) {
                        for (FinDealPledgeDto fdpDto : finDealPledgeList) {
                            fdpDto.setDesiPledgePrin(ArithmeticUtil.divide(ArithmeticUtil.multiply(fdpDto.getPledgePrin(), dto.getRolloverPrinAmt()), dto.getPrinAmt(), 0, RoundingMode.HALF_UP));
                        }
                    }
                } else {
                    Map<String, BigDecimal> alParam = new HashMap<>();
                    //画面转仓列表
                    for (TrdRolloverPledgedRepo001Dto tspr : dto.getPledgedRepoInfoList()) {
                        for (FinDealPledgeDto tab : tspr.getPledgedRepoRolloverList()) {
                            if (StringUtil.isNotEmpty(alParam.get(tab.getPledgeFinprodId()))) {
                                alParam.put(tab.getPledgeFinprodId(), ArithmeticUtil.doAdd(tab.getDesiPledgePrin(), alParam.get(tab.getPledgeFinprodId())));
                            } else {
                                alParam.put(tab.getPledgeFinprodId(), tab.getDesiPledgePrin());
                            }
                        }
                    }
                    if (!StringUtil.isEmptyList(finDealPledgeList) && finDealPledgeList.size() > 0) {
                        for (FinDealPledgeDto fdpDto : finDealPledgeList) {
                            fdpDto.setDesiPledgePrin(ArithmeticUtil.doSub(fdpDto.getPledgePrin(), alParam.get(fdpDto.getPledgeFinprodId())));
                        }
                    }
                }
                listInfoDto.setPledgedRepoRolloverList(finDealPledgeList);
            }
        }
        return listInfoDto;
    }
    /**
     * 工作流doInsert
     *
     * @param entity        entity
     * @param operationType operationType
     */
    @Override
    public void doInsert(TrdRolloverPledgedRepo001Dto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            this.handleBussFlowData(entity);
        }
    }
    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdRolloverPledgedRepo001Dto entity) {
        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }
        if (StringUtil.isNotEmpty(entity.getPledgedRepoInfoList())) {
            for (TrdRolloverPledgedRepo001Dto input : entity.getPledgedRepoInfoList()) {
                input.setWorkflowId(entity.getWorkflowId());
                input.setSplitTradeId(entity.getTradeId());
                input.setInvTransPortf(entity.getInvTransPortf());

                TrdRolloverPledgedRepo001Dto output = new TrdRolloverPledgedRepo001Dto();
                BeanUtil.copyPropertiesIgnoreNull(output,input);
                //转出产品组合登录
                // 构造Entity
                this.prepareEntity(input,entity);

                // 调用授信共同 sjj start
                TrdProductDeal TrdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, input.getTrdProductDeal());
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setRepoType(entity.getRepoType());
                inputDto.setTrdProductDeal(TrdProductDeal);
                AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
                // 调用授信共同 sjj end

                // 金融产品交易业务流程信息
                input.getWflTrdProductDeal().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                // 金融产品交易附属业务流程表
                input.getWflTrdProductDealAdd().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                // 金融产品交易扩展业务流程表
                input.getWflPaTrdProductDeal().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                // 金融产品交易业务流程其他信息
                input.getWflTrdProductDealOth().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

                // 批量插入
                wflTrdProductDealMapper.insertSelective(input.getWflTrdProductDeal());
                wflTrdProductDealAddMapper.insertSelective(input.getWflTrdProductDealAdd());
                wflPaTrdProductDealMapper.insertSelective(input.getWflPaTrdProductDeal());
                wflTrdProductDealOthMapper.insertSelective(input.getWflTrdProductDealOth());

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                if (!StringUtil.isEmptyList(input.getBussTrdTransferOrder())) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                            input.getBussTrdTransferOrder().get(0));
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                }

                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                finProductTradeDto.setAssetType2(entity.getRepoType());
                BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                baseTradeProductDealDto.setTrdProductDeal(input.getTrdProductDeal());
                baseTradeProductDealDto.setTrdProductDealAdd(input.getTrdProductDealAdd());

                //金融产品交易信息DTO.划款指令表Entity -> 划款指令表
                StlPayOrdersDto stlPayOrder = null;
                List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
                if (!StringUtil.isEmptyList(input.getBussTrdTransferOrder())) {
                    for (BussTrdTransferOrderDto bttod : input.getBussTrdTransferOrder()) {
                        stlPayOrder = new StlPayOrdersDto();
                        BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, bttod);
                        stlPayOrders.add(stlPayOrder);
                    }
                }
                if (!StringUtil.isEmptyList(stlPayOrders)) {
                    baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
                }
                finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

                baseTradeProductDealCommService.execute(finProductTradeDto);
                if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !input.getActPortfolioId().equals(input.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(input.getActPortfolioId()));
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(input.getPortfolioId()));
                }
                // 7.4写入回购类型进金融产品分类表
                finProductTypeMapper.insertSelective(input.getFinProductType());
                // 写入质押品的组合持仓流水
                this.ptlSecPositionFlowLogic(input, false);

                //转入产品组合登录
                // 构造Entity
                this.prepareEntity1(output,entity);

                // 调用授信共同 sjj start
                TrdProductDeal TrdProductDeal1 = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal1, output.getTrdProductDeal());
                AllTradeCommonInputDto outputDto = new AllTradeCommonInputDto();
                outputDto.setRepoType(entity.getRepoType());
                outputDto.setTrdProductDeal(TrdProductDeal1);
                AllTradeCommonUtil.handleBeforeTradeComplete(outputDto, false);
                // 调用授信共同 sjj end

                // 金融产品交易业务流程信息
                output.getWflTrdProductDeal().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                // 金融产品交易附属业务流程表
                output.getWflTrdProductDealAdd().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                // 金融产品交易扩展业务流程表
                output.getWflPaTrdProductDeal().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                // 金融产品交易业务流程其他信息
                output.getWflTrdProductDealOth().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

                // 批量插入
                wflTrdProductDealMapper.insertSelective(output.getWflTrdProductDeal());
                wflTrdProductDealAddMapper.insertSelective(output.getWflTrdProductDealAdd());
                wflPaTrdProductDealMapper.insertSelective(output.getWflPaTrdProductDeal());
                wflTrdProductDealOthMapper.insertSelective(output.getWflTrdProductDealOth());

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                if (!StringUtil.isEmptyList(output.getBussTrdTransferOrder())) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                            output.getBussTrdTransferOrder().get(0));
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                }

                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
                FinProductTradeDto finProductTradeDto1 = new FinProductTradeDto();
                finProductTradeDto1.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                finProductTradeDto1.setAssetType2(entity.getRepoType());
                BaseTradeProductDealDto baseTradeProductDealDto1 = new BaseTradeProductDealDto();
                baseTradeProductDealDto1.setTrdProductDeal(output.getTrdProductDeal());
                baseTradeProductDealDto1.setTrdProductDealAdd(output.getTrdProductDealAdd());

                //金融产品交易信息DTO.划款指令表Entity -> 划款指令表
                StlPayOrdersDto stlPayOrder1 = null;
                List<StlPayOrdersDto> stlPayOrders1 = new ArrayList<>();
                if (!StringUtil.isEmptyList(output.getBussTrdTransferOrder())) {
                    for (BussTrdTransferOrderDto bttod : output.getBussTrdTransferOrder()) {
                        stlPayOrder1 = new StlPayOrdersDto();
                        BeanUtil.copyPropertiesIgnoreNull(stlPayOrder1, bttod);
                        stlPayOrders1.add(stlPayOrder1);
                    }
                }
                if (!StringUtil.isEmptyList(stlPayOrders1)) {
                    baseTradeProductDealDto1.setStlPayOrdersList(stlPayOrders1);
                }
                finProductTradeDto1.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto1));

                baseTradeProductDealCommService.execute(finProductTradeDto1);
                if (StringUtil.isNotEmpty(output.getActPortfolioId()) && !output.getActPortfolioId().equals(output.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto2 -> baseTradeProductDealDto2.getTrdProductDealAdd().setPortfolioId(output.getActPortfolioId()));
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto2 -> baseTradeProductDealDto2.getTrdProductDealAdd().setPortfolioId(output.getPortfolioId()));
                }

                // 7.3 写入资产信息进交易类金融产品表
                finTradeProductMapper.insertSelective(output.getFinTradeProduct());
                // 7.4写入回购类型进金融产品分类表
                finProductTypeMapper.insertSelective(output.getFinProductType());
                // 写入质押品的组合持仓流水
                this.ptlSecPositionFlowLogic(output, false);
            }
        }
    }
    /**
     * 写入质押品的组合持仓流水
     *
     * @param input    画面参数Dto
     * @param isNegate 是否冲销
     */
    private boolean ptlSecPositionFlowLogic(TrdRolloverPledgedRepo001Dto input, boolean isNegate) {
        List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
        // 冲销时（撤销）
        if (isNegate) {
            simpleRestTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/updatePledgeBondForRevoke/" + input.getTradeId(),
                    DictCodeList.持仓类型_质押.getValue(), Long.class);
        } else {
            // List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
            // 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
            if (!StringUtil.isEmptyList(input.getPledgedRepoRolloverList())) {
                // 可质押券信息（含投资目的）
                Map<String, List<PledgeBondInfoDto>> avaMap = this.selectAvaPledgeWithInvAim(input)
                        .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
                avaMap.entrySet().stream().forEach(entry -> {
                    Collections.sort(entry.getValue(), (o1, o2) -> o1.getInvAimSort().compareTo(o2.getInvAimSort()));
                });
                PtlSecPositionFlowDto resultDto;
                for (FinDealPledgeDto pledgeBond : input.getPledgedRepoRolloverList()) {
                    // 待分配投资目的的质押券面总额
                    BigDecimal pledgePrin = pledgeBond.getDesiPledgePrin();
                    if(CollectionUtils.isNotEmpty(avaMap.get(pledgeBond.getFinprodAbbr()))){
                        for (PledgeBondInfoDto avaBond : avaMap.get(pledgeBond.getFinprodAbbr())) {
                            resultDto = new PtlSecPositionFlowDto();
                            // 当前投资目的可分配券面总额
                            BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                            // 当前投资目的决定分配的券面总额
                            BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                            // 组合代码 = 画面.组合代码
                            resultDto.setPortfolioId(input.getPortfolioId());
                            // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
                            resultDto.setSecAcctId(StringUtil.isEmpty(input.getTrdProductDeal().getSecManageAcctId()) ?
                                    (StringUtil.isEmpty(input.getChlId()) ? "999999": input.getChlId()) :
                                    input.getSecAcctId());
                            // 金融产品代码 = 输出质押品DTO.金融产品代码
                            resultDto.setFinprodId(pledgeBond.getFinprodId());
                            // 分支序号 = 系统固定参数，默认为0
                            resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                            // 持仓类型
                            resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                            // 日期 = 金融产品交易Entity.交易日
                            resultDto.setCdate(input.getTrdProductDeal().getTradeDate());
                            // 数量 = 输出质押品DTO.质押数量
                            resultDto.setAmount(com.joyintech.framework.util.ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                            // 币种 = 金融产品交易Entity.币种
                            resultDto.setCcy(input.getTrdProductDeal().getCcy());
                            // 交易编号 = 金融产品交易Entity.交易编号
                            resultDto.setTradeId(input.getTrdProductDeal().getTradeId());
                            // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                            resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                            if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
                                if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
                                    PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
                                    BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
                                    subResultDto.setPortfolioId(input.getActPortfolioId());
                                    lstPtlSecPositionFlowDto.add(subResultDto);
                                }
                                lstPtlSecPositionFlowDto.add(resultDto);
                            }
                            // 分配完毕后,停止可质押券信息（含投资目的）的循环
                            pledgePrin = pledgePrin.subtract(prin);
                            if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
            return true;
        } else {
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
            return simpleRestTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                    ptlPositionHoldDto, Boolean.class);
        }
    }
    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(TrdRolloverPledgedRepo001Dto input) {
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getPortfolioId());
        param.put("ps", input.getPs());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        param.put("secAcctId", input.getSecAcctId());
        param.put("chlId", input.getChlId());
        return simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectAvaPledgeBondDetailList",
                param, PledgeBondInfoDto.class);
    }
    private void prepareEntity(TrdRolloverPledgedRepo001Dto input,TrdRolloverPledgedRepo001Dto entity) {
        input.setActPortfolioId(input.getPortfolioId());
        input.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(input.getPortfolioId()));
        //根据原始交易编号查询拆分前投资过度组合信息

        // 金融产品交易Entity 构造开始-----------------------------------------------------------------------
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        //新增：系统生成的交易编号
        trdProductDeal.setTradeId(input.getTradeId());
        //数据字典.质押式回购
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        trdProductDeal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        //金融产品代码 此金融产品代码为投资交易录入时生成的金融产品代码，不重新生成
        trdProductDeal.setFinprodId(entity.getFinprodId());
        // 金融产品类型（投管分类）
        String finprodType2;
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            finprodType2 = DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
            input.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
        } else {
            finprodType2 = DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
            input.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
        }
        input.setFinprodType2(finprodType2);

        trdProductDeal.setFinprodType2(finprodType2);
        // 交易日 = 画面.交易日
        trdProductDeal.setTradeDate(entity.getTradeDate());
        // 起息日 = 画面.起息日
        trdProductDeal.setVdate(entity.getVdate());
        // 到期日 = 画面.到期日
        trdProductDeal.setMdate(entity.getMdate());
        // 交割日 = 画面.交易日
        trdProductDeal.setSettleDate(entity.getTradeDate());
        // 交割日 = 画面.币种
        trdProductDeal.setCcy(entity.getCcy());
        // 交易份额 = 画面.本金金额
        trdProductDeal.setShareAmt(input.getRolloverPrinAmt());
        // 交易本金 = 画面.本金金额
        trdProductDeal.setPrinAmt(input.getRolloverPrinAmt());
        // 利息金额 = 画面.回购利息
        trdProductDeal.setIntAmt(input.getRolloverRepoIntAmt());
        // 交易金额 = 画面.本金金额
        trdProductDeal.setTradeAmt(input.getRolloverTradeAmt());
        // 交易对手 = 转入产品组合.组合代码
        trdProductDeal.setCounterId(input.getPortfolioId());
        //交易方向 = 正回购转仓时：卖出 逆回购转仓时：买入
        String remark = "";
        trdProductDeal.setPs(DictCodeList.交易方向_卖出.getValue());
        if (DictCodeList.回购拆借方向_逆回购.getValue().equals(input.getPs())) {
            remark = "质押式逆回购转出";
        }
        else{
            remark = "质押式正回购转出";
        }
        // 交易状态 = 交易状态_初始
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 工作流代码
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        //备注
        trdProductDeal.setRemark(remark);
        //会否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_否.getValue());
        //清算速度
        trdProductDeal.setSettleSpeed(0l);
        //是否撤销 默认否
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        //是否结清 默认是
        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        //交易场所
        trdProductDeal.setTradeMarket(entity.getTradeMarket());
        //交易员
        trdProductDeal.setTrader(entity.getTrader());
        //证券托管户
        trdProductDeal.setSecManageAcctId(entity.getSecAcctId());
        //通道代码
        trdProductDeal.setChlId(entity.getChlId());

        trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());

        //备注
        input.setRemark(remark);

        input.setTrdProductDeal(trdProductDeal);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, input.getTrdProductDeal());
        input.setWflTrdProductDeal(wflTrdProductDeal);
        // 金融产品交易Entity 构造结束-----------------------------------------------------------------------

        // 金融产品交易附属表Entity 构造开始-----------------------------------------------------------------------
        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        //交易编号 = 画面.交易编号
        trdProductDealAdd.setTradeId(input.getTradeId());
        //投资组合代码 = 待转出产品组合.组合代码
        trdProductDealAdd.setPortfolioId(entity.getInvTransPortf());
        //是否有结算指令	= 数据字典.是否    默认为否
        // TODO
        // 生成方式 = 数据字典缓存.生成方式.画面录入
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        // 工作流代码
        trdProductDealAdd.setWorkflowId(entity.getWorkflowId());

        input.setTrdProductDealAdd(trdProductDealAdd);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, input.getTrdProductDealAdd());
        input.setWflTrdProductDealAdd(wflTrdProductDealAdd);
        // 金融产品交易附属表Entity 构造结束-----------------------------------------------------------------------

        // 组合交易表Entity 构造开始-----------------------------------------------------------------------
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        // 组合代码 = 转出产品组合.组合代码
        ptlTrade.setPortfolioId(entity.getInvTransPortf());
        // 业务编号 = 业务编号 ※2生成的交易编号
        ptlTrade.setBusiNo(input.getTradeId());
        //业务表 = 画面.业务表  默认为金融产品交易表
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 业务明细代码 = 画面.金融产品代码
        ptlTrade.setBusiId(input.getTradeId());
        // 业务类型 = 数据字典缓存.共通业务类型.回购投资交易
        ptlTrade.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 证券托管户代码 = 画面.证券托管账户
        ptlTrade.setSecManageAcctId(entity.getSecAcctId());
        // 实际发生日 = 画面.交易日
        ptlTrade.setSettleDate(entity.getTradeDate());
        // 工作流代码
        ptlTrade.setWorkflowId(input.getWorkflowId());
        ptlTrades.add(ptlTrade);
        if (!input.getPortfolioId().equals(input.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
            // 组合代码 = 画面.组合代码
            ptlTrade1.setPortfolioId(input.getActPortfolioId());
            ptlTrades.add(ptlTrade1);
        }
        input.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束-----------------------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (StringUtil.isBlank(input.getIsGuarantee())) {
            // 查询组合是否保本
            PaPtlPortfolio paPtlPortfolio = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                    PaPtlPortfolioMapper.class, input.getPortfolioId(), PaPtlPortfolio.class);
            if (paPtlPortfolio != null) {
                input.setIsGuarantee(paPtlPortfolio.getIsGuarantee());
            } else {
                input.setIsGuarantee(DictCodeList.是否_否.getValue());
            }
        }
        if (bussTrdTransferOrderService != null) {
            if (StringUtil.isEmptyList(input.getBussTrdTransferOrder())) {
                // 后台自动生成划款指令
                BussSettleOrderParameterDto orderParameterDto = new BussSettleOrderParameterDto();
                // 是否汇总交易 = 是否_否
                orderParameterDto.setIsTotalTrade(DictCodeList.是否_否.getValue());

                // 金融产品交易Entity = 上述构造的金融产品交易Entity
                TrdProductDeal orderParameterDeal = new TrdProductDeal();
                // 交易日期 = 画面.交易日
                orderParameterDeal.setTradeDate(entity.getTradeDate());
                // 交易方向 = 交易方向_买入
                orderParameterDeal.setPs(trdProductDeal.getPs());
                // 币种 = 画面.币种
                orderParameterDeal.setCcy(entity.getCcy());
                // 交易金额 = 画面.本金金额
                orderParameterDeal.setTradeAmt(input.getRolloverTradeAmt());
                // 交易对手 = 画面.交易对手
                orderParameterDeal.setCounterId(trdProductDeal.getCounterId());
                // 交易市场 chumxing for huaxing 修复划款指令账户类型问题 2021-01-26
                orderParameterDeal.setTradeMarket(trdProductDeal.getTradeMarket());
                orderParameterDto.setDeal(orderParameterDeal);

                // 拆借类型 = null
                orderParameterDto.setBankLendingType(null);
                // 组合代码 = 画面.组合代码
                orderParameterDto.setPortfolioId(trdProductDealAdd.getPortfolioId());

                BussTrdTransferOrderDto resultOrderDto = bussTrdTransferOrderService.generateOrder(orderParameterDto);
                if (resultOrderDto != null) {
                    input.setBussTrdTransferOrder(Arrays.asList(resultOrderDto));
                }
            }
            if (!StringUtil.isEmptyList(input.getBussTrdTransferOrder())) {
                BussTrdTransferOrderDto bttod = input.getBussTrdTransferOrder().get(0);
                // 交易编号 = 业务编号 ※2生成的交易编号
                bttod.setTradeId(input.getTradeId());
                // 指令状态 = 指令状态_新建
                bttod.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                // 工作流代码
                bttod.setWorkflowId(input.getWorkflowId());
                input.setBussTrdTransferOrder(bussTrdTransferOrderService.save(input.getBussTrdTransferOrder()));
            }
        }
        // 划款指令表Entity 构造结束----------------------------------------------------------

        // 金融产品分类Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finProductType.setFinprodId(input.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finProductType.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 分类6 = 画面.回购类型
        finProductType.setType6(entity.getRepoType());
        // 分类7 = 画面.回购类型
        finProductType.setType7(entity.getPs());
        // 分类8 = 画面.回购类型
        finProductType.setType8(DictCodeList.平安资产类型_质押式回购.getValue());
        // 工作流代码
        finProductType.setWorkflowId(entity.getWorkflowId());
        input.setFinProductType(finProductType);
        // 金融产品分类Entity 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        // 交易编号 = 金融产品交易Entity.交易编号
        paTrdProductDeal.setTradeId(input.getTradeId());
        // 平安业务类型 = 数据字典缓存.平安业务类型（按交易方向取对应值）
        if (DictCodeList.回购拆借方向_正回购.getValue().equals(input.getPs())) {
            // 交易方向为正回购时，取数据字典缓存.平安业务类型.正回购
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_正回购.getValue());
        } else {
            // 交易方向为逆回购时，取数据字典缓存.平安业务类型.逆回购
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_逆回购.getValue());
        }
        paTrdProductDeal.setDeptCode(entity.getDeptCode());
        // 回购/拆借方向 = 画面.交易方向
        paTrdProductDeal.setBackLendDirection(entity.getPs());
        // 工作流代码
        paTrdProductDeal.setWorkflowId(input.getWorkflowId());

        WflPaTrdProductDeal wflPaTrdProductDeal =new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal,paTrdProductDeal);
        input.setWflPaTrdProductDeal(wflPaTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        // 金融产品交易业务流程其他信息Entity 构造开始----------------------------------------------------------
        WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
        // 交易编号 = 金融产品交易Entity.交易编号
        wflTrdProductDealOth.setTradeId(input.getTradeId());
        // 计息基础 = 画面.计息基础
        wflTrdProductDealOth.setBasis(entity.getBasis());
        // 利率 = 画面.回购利率/100
        wflTrdProductDealOth.setRate(com.joyintech.framework.util.ArithmeticUtil.divide(entity.getRebackPrice(), new BigDecimal("100"), 8));
        // 期限(天) = 画面.期限(天)
        wflTrdProductDealOth.setTerm(entity.getTerm());
        // 期限品种 = 画面.期限品种
        wflTrdProductDealOth.setTermType(entity.getTermTypeReback());
        // 到期金额
        wflTrdProductDealOth.setmTradeAmt(input.getRolloverTradeAmt());
        // 回购类型 = 画面.回购类型
        wflTrdProductDealOth.setRepoType(input.getRepoType());
        // 数据状态 = 数据字典缓存.数据状态.新建
        wflTrdProductDealOth.setStatus(DictCodeList.数据状态_新建.getValue());
        // 工作流代码
        wflTrdProductDealOth.setWorkflowId(input.getWorkflowId());
        input.setWflTrdProductDealOth(wflTrdProductDealOth);
        // 金融产品交易业务流程其他信息Entity 构造结束----------------------------------------------------------
    }
    private void prepareEntity1(TrdRolloverPledgedRepo001Dto output,TrdRolloverPledgedRepo001Dto entity) {
        output.setActPortfolioId(output.getPortfolioId());
        output.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(output.getPortfolioId()));
        //生成交易编号
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(output.getPs(),
                DictCodeList.回购拆借方向_正回购.getValue())) {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
        } else {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
        }
        output.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        // 金融产品交易Entity 构造开始-----------------------------------------------------------------------
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        //新增：系统生成的交易编号
        trdProductDeal.setTradeId(output.getTradeId());
        //数据字典.质押式回购
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        trdProductDeal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        //金融产品代码 此金融产品代码为投资交易录入时生成的金融产品代码，不重新生成
        trdProductDeal.setFinprodId(output.getTradeId());
        // 金融产品类型（投管分类）
        String finprodType2;
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
        if (StringUtil.equals(output.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            finprodType2 = DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
            output.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
        } else {
            finprodType2 = DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
            output.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
        }
        output.setFinprodType2(finprodType2);

        trdProductDeal.setFinprodType2(finprodType2);
        // 交易日 = 画面.交易日
        trdProductDeal.setTradeDate(entity.getTradeDate());
        // 起息日 = 画面.起息日
        trdProductDeal.setVdate(entity.getVdate());
        // 到期日 = 画面.到期日
        trdProductDeal.setMdate(entity.getMdate());
        // 交割日 = 画面.交易日
        trdProductDeal.setSettleDate(entity.getTradeDate());
        // 交割日 = 画面.币种
        trdProductDeal.setCcy(entity.getCcy());
        // 交易份额 = 画面.本金金额
        trdProductDeal.setShareAmt(output.getRolloverPrinAmt());
        // 交易本金 = 画面.本金金额
        trdProductDeal.setPrinAmt(output.getRolloverPrinAmt());
        // 交易金额 = 画面.本金金额
        trdProductDeal.setTradeAmt(output.getRolloverTradeAmt());
        // 利息金额 = 画面.回购利息
        trdProductDeal.setIntAmt(output.getRolloverRepoIntAmt());
        // 交易对手 = 转出产品组合.组合代码
        trdProductDeal.setCounterId(entity.getInvTransPortf());
        //交易方向 = 正回购转仓时：卖出 逆回购转仓时：买入
        String remark = "";
        if (DictCodeList.回购拆借方向_逆回购.getValue().equals(output.getPs())) {
            remark = "质押式逆回购转入";
        }
        else{
            remark = "质押式正回购转入";
        }
        trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
        // 交易状态 = 交易状态_初始
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        //拆分前交易编号
        trdProductDeal.setSplitTradeId(output.getSplitTradeId());
        // 工作流代码
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        //备注
        trdProductDeal.setRemark(remark);
        //会否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_否.getValue());
        //清算速度
        trdProductDeal.setSettleSpeed(0l);
        //是否撤销 默认否
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        //是否结清 默认是
        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        //交易场所
        trdProductDeal.setTradeMarket(entity.getTradeMarket());
        //交易员
        trdProductDeal.setTrader(entity.getTrader());
        //证券托管户
        trdProductDeal.setSecManageAcctId(entity.getSecAcctId());
        //通道代码
        trdProductDeal.setChlId(entity.getChlId());
        trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());

        output.setRemark(remark);

        output.setTrdProductDeal(trdProductDeal);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, output.getTrdProductDeal());
        output.setWflTrdProductDeal(wflTrdProductDeal);
        // 金融产品交易Entity 构造结束-----------------------------------------------------------------------

        // 金融产品交易附属表Entity 构造开始-----------------------------------------------------------------------
        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        //交易编号 = 画面.交易编号
        trdProductDealAdd.setTradeId(output.getTradeId());
        //投资组合代码 = 待转出产品组合.组合代码
        trdProductDealAdd.setPortfolioId(output.getPortfolioId());
        //是否有结算指令	= 数据字典.是否    默认为否
        // TODO
        // 生成方式 = 数据字典缓存.生成方式.画面录入
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        // 工作流代码
        trdProductDealAdd.setWorkflowId(entity.getWorkflowId());

        output.setTrdProductDealAdd(trdProductDealAdd);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, output.getTrdProductDealAdd());
        output.setWflTrdProductDealAdd(wflTrdProductDealAdd);
        // 金融产品交易附属表Entity 构造结束-----------------------------------------------------------------------

        // 交易类金融产品Entity 构造开始----------------------------------------------------------
        FinTradeProductDto finTradeProductDto = new FinTradeProductDto();

        finTradeProductDto.setFinprodId(output.getTradeId());
        finTradeProductDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 金融产品类型（投管分类）
        String finprodType22;
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
        if (StringUtil.equals(output.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            finprodType22 = DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
            finTradeProductDto.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
        } else {
            finprodType22 = DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
            finTradeProductDto.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
        }
        finTradeProductDto.setFinprodType2(finprodType22);
        //通道代码							画面.通道代码
        finTradeProductDto.setChlAgrtId(entity.getChlId());
        //名义本金							列表.交易本金
        finTradeProductDto.setPrin(output.getRolloverPrinAmt());
        //本金币种							画面.币种
        finTradeProductDto.setCcy(entity.getCcy());
        //起息日							画面.起息日
        finTradeProductDto.setVdate(entity.getVdate());
        //到期日							画面.到期日
        finTradeProductDto.setMdate(entity.getMdate());
        //期限(天)							画面.期限
        finTradeProductDto.setTerm(entity.getTerm());
        //利率类型							固定利率
        finTradeProductDto.setIntType(DictCodeList.利率类型_固定利率.getValue());
        //固定利率							画面.回购利率
        finTradeProductDto.setIntRate(ArithmeticUtil.divide(entity.getRebackPrice(),100));
        //计息基础							画面.计息基础
        finTradeProductDto.setBasis(entity.getBasis());
        //到期本金							列表.本金金额
        finTradeProductDto.setmPrinAmt(output.getRolloverPrinAmt());
        BigDecimal basis = BigDecimal.ZERO;
        if(StringUtil.equals(entity.getBasis(),DictCodeList.计息基础_A_360.getValue())){
            basis=new BigDecimal(1.0/360);
        }
        else{
            basis=new BigDecimal(1.0/365);
        }
        //到期利息		到期本金*（到期日-起息日）*利率/计息基础
        finTradeProductDto.setmIntAmt(ArithmeticUtil.round(ArithmeticUtil.multiply(finTradeProductDto.getmPrinAmt(),finTradeProductDto.getIntRate(), CalendarUtil.calendarDays(entity.getVdate(),entity.getMdate()),basis),2,RoundingMode.HALF_UP));
        //到期金额							列表.到期结算金额
        finTradeProductDto.setmTradeAmt(ArithmeticUtil.doAdd(finTradeProductDto.getmIntAmt(),finTradeProductDto.getmPrinAmt()));
        //本金收益特征							NULL
        //原金融产品代码							初始化时待转产品.金融产品代码
        finTradeProductDto.setoFinprodId(entity.getFinprodId());
        //交易场所							画面.交易场所
        finTradeProductDto.setTradeMarket(entity.getTradeMarket());
        //交易日历							画面.交易日历
        finTradeProductDto.setCalendarId(entity.getCalendarId());
        //期限品种							画面.期限品质
        finTradeProductDto.setTermType(entity.getTermTypeReback());
        //交易对手							转出产品组合.组合代码
        finTradeProductDto.setCounterId(entity.getPortfolioId());

        finTradeProductDto.setActCapDays(Long.valueOf(entity.getActCapDays()));

        output.setFinTradeProduct(finTradeProductDto);
        // 交易类金融产品Entity 构造结束----------------------------------------------------------

        // 组合交易表Entity 构造开始-----------------------------------------------------------------------
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        // 组合代码 = 转出产品组合.组合代码
        ptlTrade.setPortfolioId(output.getPortfolioId());
        // 业务编号 = 业务编号 ※2生成的交易编号
        ptlTrade.setBusiNo(output.getTradeId());
        //业务表 = 画面.业务表  默认为金融产品交易表
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 业务明细代码 = 画面.金融产品代码
        ptlTrade.setBusiId(output.getTradeId());
        // 业务类型 = 数据字典缓存.共通业务类型.回购投资交易
        ptlTrade.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 证券托管户代码 = 画面.证券托管账户
        ptlTrade.setSecManageAcctId(entity.getSecAcctId());
        // 实际发生日 = 画面.交易日
        ptlTrade.setSettleDate(entity.getTradeDate());
        // 工作流代码
        ptlTrade.setWorkflowId(output.getWorkflowId());
        ptlTrades.add(ptlTrade);
        if (!output.getPortfolioId().equals(output.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
            // 组合代码 = 画面.组合代码
            ptlTrade1.setPortfolioId(output.getActPortfolioId());
            ptlTrades.add(ptlTrade1);
        }
        output.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束-----------------------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (StringUtil.isBlank(output.getIsGuarantee())) {
            // 查询组合是否保本
            PaPtlPortfolio paPtlPortfolio = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                    PaPtlPortfolioMapper.class, output.getPortfolioId(), PaPtlPortfolio.class);
            if (paPtlPortfolio != null) {
                output.setIsGuarantee(paPtlPortfolio.getIsGuarantee());
            } else {
                output.setIsGuarantee(DictCodeList.是否_否.getValue());
            }
        }
        if (bussTrdTransferOrderService != null) {
            if (StringUtil.isEmptyList(output.getBussTrdTransferOrder())) {
                // 后台自动生成划款指令
                BussSettleOrderParameterDto orderParameterDto = new BussSettleOrderParameterDto();
                // 是否汇总交易 = 是否_否
                orderParameterDto.setIsTotalTrade(DictCodeList.是否_否.getValue());

                // 金融产品交易Entity = 上述构造的金融产品交易Entity
                TrdProductDeal orderParameterDeal = new TrdProductDeal();
                // 交易日期 = 画面.交易日
                orderParameterDeal.setTradeDate(entity.getTradeDate());
                // 交易方向 = 交易方向_买入
                orderParameterDeal.setPs(trdProductDeal.getPs());
                // 币种 = 画面.币种
                orderParameterDeal.setCcy(entity.getCcy());
                // 交易金额 = 画面.本金金额
                /**取交易金额 zhangnn for hxbank 2020/5/19 start **/
                orderParameterDeal.setTradeAmt(output.getRolloverTradeAmt());
                /**取交易金额 zhangnn for hxbank 2020/5/19 end **/
                // 交易对手 = 画面.交易对手
                orderParameterDeal.setCounterId(trdProductDeal.getCounterId());
                // 交易市场 chumxing for huaxing 修复划款指令账户类型问题 2021-01-26
                orderParameterDeal.setTradeMarket(trdProductDeal.getTradeMarket());
                orderParameterDto.setDeal(orderParameterDeal);

                // 拆借类型 = null
                orderParameterDto.setBankLendingType(null);
                // 组合代码 = 画面.组合代码
                orderParameterDto.setPortfolioId(trdProductDealAdd.getPortfolioId());

                BussTrdTransferOrderDto resultOrderDto = bussTrdTransferOrderService.generateOrder(orderParameterDto);
                if (resultOrderDto != null) {
                    output.setBussTrdTransferOrder(Arrays.asList(resultOrderDto));
                }
            }
            if (!StringUtil.isEmptyList(output.getBussTrdTransferOrder())) {
                BussTrdTransferOrderDto bttod = output.getBussTrdTransferOrder().get(0);
                // 交易编号 = 业务编号 ※2生成的交易编号
                bttod.setTradeId(output.getTradeId());
                // 指令状态 = 指令状态_新建
                bttod.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                // 工作流代码
                bttod.setWorkflowId(output.getWorkflowId());
                output.setBussTrdTransferOrder(bussTrdTransferOrderService.save(output.getBussTrdTransferOrder()));
            }
        }
        // 划款指令表Entity 构造结束----------------------------------------------------------

        // 金融产品分类Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finProductType.setFinprodId(output.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finProductType.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 分类6 = 画面.回购类型
        finProductType.setType6(entity.getRepoType());
        // 分类7 = 画面.回购类型
        finProductType.setType7(entity.getPs());
        // 分类8 = 画面.回购类型
        finProductType.setType8(DictCodeList.平安资产类型_质押式回购.getValue());
        // 工作流代码
        finProductType.setWorkflowId(entity.getWorkflowId());
        output.setFinProductType(finProductType);
        // 金融产品分类Entity 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        // 交易编号 = 金融产品交易Entity.交易编号
        paTrdProductDeal.setTradeId(output.getTradeId());
        // 平安业务类型 = 数据字典缓存.平安业务类型（按交易方向取对应值）
        if (DictCodeList.回购拆借方向_正回购.getValue().equals(output.getPs())) {
            // 交易方向为正回购时，取数据字典缓存.平安业务类型.正回购
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_正回购.getValue());
        } else {
            // 交易方向为逆回购时，取数据字典缓存.平安业务类型.逆回购
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_逆回购.getValue());
        }
        paTrdProductDeal.setDeptCode(entity.getDeptCode());
        // 回购/拆借方向 = 画面.交易方向
        paTrdProductDeal.setBackLendDirection(entity.getPs());
        // 工作流代码
        paTrdProductDeal.setWorkflowId(output.getWorkflowId());

        WflPaTrdProductDeal wflPaTrdProductDeal =new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal,paTrdProductDeal);
        output.setWflPaTrdProductDeal(wflPaTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        // 金融产品交易业务流程其他信息Entity 构造开始----------------------------------------------------------
        WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
        // 交易编号 = 金融产品交易Entity.交易编号
        wflTrdProductDealOth.setTradeId(output.getTradeId());
        // 计息基础 = 画面.计息基础
        wflTrdProductDealOth.setBasis(entity.getBasis());
        // 利率 = 画面.回购利率/100
        wflTrdProductDealOth.setRate(com.joyintech.framework.util.ArithmeticUtil.divide(entity.getRebackPrice(), new BigDecimal("100"), 8));
        // 期限(天) = 画面.期限(天)
        wflTrdProductDealOth.setTerm(entity.getTerm());
        // 期限品种 = 画面.期限品种
        wflTrdProductDealOth.setTermType(entity.getTermTypeReback());
        // 到期金额
        wflTrdProductDealOth.setmTradeAmt(output.getRolloverTradeAmt());
        // 回购类型 = 画面.回购类型
        wflTrdProductDealOth.setRepoType(output.getRepoType());
        // 数据状态 = 数据字典缓存.数据状态.新建
        wflTrdProductDealOth.setStatus(DictCodeList.数据状态_新建.getValue());
        // 工作流代码
        wflTrdProductDealOth.setWorkflowId(output.getWorkflowId());
        output.setWflTrdProductDealOth(wflTrdProductDealOth);
        // 金融产品交易业务流程其他信息Entity 构造结束----------------------------------------------------------
    }

    @Override
    public void doComplete(TrdRolloverPledgedRepo001Dto dto, OperationType operationType) {
        String workflowId ="";
        //删除FIN_TRADE_PRODUCT 和 FIN_PRODUCT_TYPE 表
        deleteFinProductInfo(dto.getWorkflowId());
        for(TrdRolloverPledgedRepo001Dto input:dto.getPledgedRepoInfoList()){
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                input.setWorkflowId(dto.getWorkflowId());
                input.setSplitTradeId(dto.getTradeId());
                input.setInvTransPortf(dto.getInvTransPortf());
                setBaseInfo(input,dto);
                TrdRolloverPledgedRepo001Dto output = new TrdRolloverPledgedRepo001Dto();
                BeanUtil.copyPropertiesIgnoreNull(output,input);
                // 构造Entity
                this.prepareEntity1(output,dto);

                this.prepareEntity(input,dto);

                // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
                input.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                input.getTrdProductDeal().setrTradeId(output.getTrdProductDeal().getTradeId());

                // 金融产品交易扩展表Entity -> 金融产品交易扩展表
                PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, input.getWflPaTrdProductDeal());

                if(input.getPtlTrade().size()>0){
                    input.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
                }
                paTrdProductDealMapper.insertSelective(paTrdProductDeal);

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
                StlPayOrdersDto stlPayOrder = null;
                List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
                if (!StringUtil.isEmptyList(input.getBussTrdTransferOrder())) {
                    for (BussTrdTransferOrderDto bttod : input.getBussTrdTransferOrder()) {
                        stlPayOrder = new StlPayOrdersDto();
                        BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, bttod);
                        stlPayOrders.add(stlPayOrder);
                    }
                }

                // OPCIS表Entity TODO

                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());

                //List<BaseTradeProductDealDto> lstPostion = new ArrayList<>();

                BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                // 金融产品交易
                baseTradeProductDealDto.setTrdProductDeal(input.getTrdProductDeal());
                // 金融产品附属
                baseTradeProductDealDto.setTrdProductDealAdd(input.getTrdProductDealAdd());
                // 划款指令
                if (stlPayOrders != null) {
                    baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
                }
                finProductTradeDto.setAssetType2(dto.getRepoType());
                finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

                // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
                baseTradeProductDealCommService.execute(finProductTradeDto);

                // 7.4写入回购类型进金融产品分类表
                finProductTypeMapper.insertSelective(input.getFinProductType());

                // 7.5写入现金流相关信息表
                this.cashFlowLogic(input);

                // 7.7写入交易信息进交易报送信息表，为中债报送用
                this.dealInfoCbReportLogic_7_7(input);

                // 发送Murex接口处理
                //this.sendMurex(input, false);

                // 一体化对应etliu start
                // 所有交易生效后调用的共通方法
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(input.getInvTransPortf());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(input.getTrdProductDeal());
                // 通道交易 新增 强成西 2019/03/26 start
                inputDto.setTrdProductDealAdd(input.getTrdProductDealAdd());
//                inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);
                // 通道交易 新增 强成西 2019/03/26 end
                inputDto.setRepoType(input.getRepoType());
                inputDto.setStlPayOrders(stlPayOrder);
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                // 一体化对应etliu end

                //转入产品组合登陆------------------------------------------------------------------------------------------------------------
                // 构造Entity

                // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
                output.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

                // 金融产品交易扩展表Entity -> 金融产品交易扩展表
                PaTrdProductDeal paTrdProductDeal1 = new PaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal1, output.getWflPaTrdProductDeal());

                if(output.getPtlTrade().size()>0){
                    output.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
                }
                paTrdProductDealMapper.insertSelective(paTrdProductDeal1);

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
                StlPayOrdersDto stlPayOrder1 = null;
                List<StlPayOrdersDto> stlPayOrders1 = new ArrayList<>();
                if (!StringUtil.isEmptyList(output.getBussTrdTransferOrder())) {
                    for (BussTrdTransferOrderDto bttod : output.getBussTrdTransferOrder()) {
                        stlPayOrder1 = new StlPayOrdersDto();
                        BeanUtil.copyPropertiesIgnoreNull(stlPayOrder1, bttod);
                        stlPayOrders1.add(stlPayOrder1);
                    }
                }

                // OPCIS表Entity TODO

                FinProductTradeDto finProductTradeDto1 = new FinProductTradeDto();
                finProductTradeDto1.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());

                //List<BaseTradeProductDealDto> lstPostion1 = new ArrayList<>();

                BaseTradeProductDealDto baseTradeProductDealDto1 = new BaseTradeProductDealDto();
                // 金融产品交易
                baseTradeProductDealDto1.setTrdProductDeal(output.getTrdProductDeal());
                // 金融产品附属
                baseTradeProductDealDto1.setTrdProductDealAdd(output.getTrdProductDealAdd());
                // 划款指令
                if (stlPayOrders1 != null) {
                    baseTradeProductDealDto1.setStlPayOrdersList(stlPayOrders1);
                }
                finProductTradeDto1.setAssetType2(dto.getRepoType());
                finProductTradeDto1.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto1));

                // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
                baseTradeProductDealCommService.execute(finProductTradeDto1);
                // 7.3 写入资产信息进交易类金融产品表
                finTradeProductMapper.insertSelective(output.getFinTradeProduct());
                // 7.4 写入资产信息进交易类金融产品附属表
//                finTradeProductAddMapper.insertSelective(input.getFinTradeProductAdd());
                // 7.4写入回购类型进金融产品分类表
                finProductTypeMapper.insertSelective(output.getFinProductType());

                // 7.5写入现金流相关信息表
                this.cashFlowLogic(output);

                // 7.6写入回购质押品信息
                if(StringUtil.isNotEmpty(output.getPledgedRepoRolloverList())){
                    for(FinDealPledgeDto finDealPledgeDto: output.getPledgedRepoRolloverList()){
                        FinDealPledge finDealPledge = new FinDealPledge();
                        // 金融产品代码 = 金融产品交易Entity.交易编号
                        finDealPledge.setFinprodId(output.getTradeId());
                        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
                        finDealPledge.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                        // 质押金融产品代码 = 输出质押品DTO.金融产品代码
                        finDealPledge.setPledgeFinprodId(finDealPledgeDto.getPledgeFinprodId());
                        // 质押面值 = 输出质押品DTO.质押面值
                        finDealPledge.setPledgeFaceValue(new BigDecimal("100"));
                        // 质押数量 = 输出质押品DTO.质押数量
                        finDealPledge.setPledgeNumber(com.joyintech.framework.util.ArithmeticUtil.divide(finDealPledgeDto.getDesiPledgePrin(), new BigDecimal("100"), 4));
                        // 质押率 = 输出质押品DTO.质押率
                        finDealPledge.setPledgeRatio(com.joyintech.framework.util.ArithmeticUtil.divide(finDealPledgeDto.getPledgeRatio(), new BigDecimal("100"), 4));
                        // 质押金额 = 输出质押品DTO.质押金额
                        finDealPledge.setPledgeAmt(com.joyintech.framework.util.ArithmeticUtil.multiply(finDealPledgeDto.getDesiPledgePrin(), finDealPledge.getPledgeRatio()));
                        finDealPledgeMapper.insertSelective(finDealPledge);
                    }
                }
                // 7.7写入交易信息进交易报送信息表，为中债报送用
                this.dealInfoCbReportLogic_7_7(output);

                // 发送Murex接口处理
                //this.sendMurex(output, false);

                // 一体化对应etliu start
                // 所有交易生效后调用的共通方法
                AllTradeCommonInputDto outputDto = new AllTradeCommonInputDto();
                outputDto.setProdid(output.getPortfolioId());
                outputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                outputDto.setTrdProductDeal(output.getTrdProductDeal());
                // 通道交易 新增 强成西 2019/03/26 start
                outputDto.setTrdProductDealAdd(output.getTrdProductDealAdd());
//                inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);
                // 通道交易 新增 强成西 2019/03/26 end
                outputDto.setRepoType(output.getRepoType());
                outputDto.setStlPayOrders(stlPayOrder1);
                AllTradeCommonUtil.handleAfterTradeComplete(outputDto);
            }

            // 7.2删除原业务流程表中的交易记录
            workflowId = input.getWorkflowId();
            if (StringUtil.isBlank(workflowId)) {
                WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
                wflTrdProductDealKey.setTradeId(input.getTradeId());
                wflTrdProductDealKey.setWorkflowStatus(
                        DictCodeListExt1.交易流程状态_流程中.getValue());
                WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                        WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

                if (wflTrdProductDeal != null) {
                    workflowId = wflTrdProductDeal.getWorkflowId();
                }
            }
        }
        this.doDelete(workflowId);
    }

    private void deleteFinProductInfo(String workflowId) {
        List<WflTrdProductDeal> dealList = simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME+ "/trade/transfer/common/TransferCommon/selectDealByworkflowId",
                workflowId, WflTrdProductDeal.class);
        if(!StringUtil.isEmptyList(dealList)){
            for (WflTrdProductDeal deal:dealList) {
                FinProductType type = new FinProductType();
                type.setFinprodId(deal.getTradeId());
                type.setBranch(deal.getBranch());
                finProductTypeMapper.deleteByPrimaryKey(type);

                FinTradeProductDto trade = new FinTradeProductDto();
                trade.setFinprodId(deal.getTradeId());
                trade.setBranch(deal.getBranch());
                finTradeProductMapper.deleteByPrimaryKey(trade);
            }
        }
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.deleteWflData(workflowId);
        deleteFinProductInfo(workflowId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdRolloverPledgedRepo001Dto dto) {
        if (!StringUtil.equals("revoke", dto.getOperateType())
                && !StringUtil.equals("delete", dto.getOperateType())) {
            // 先删除流程中数据
            this.doDelete(dto.getWorkflowId());
            ProcessMark newEntity = null;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdRolloverPledgedRepo001Dto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(dto.getWorkflowId());
                newEntity.setStatus(dto.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(dto));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            }
            catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            // 重新insert
            this.handleBussFlowData(dto);
        }
    }
    /**
     * 发送Murex接口处理
     *
     * @param entity 回购投资交易信息
     * @param revokeFlag 撤销标识
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unused")
    private void sendMurex(TrdRolloverPledgedRepo001Dto entity, boolean revokeFlag) {
        if (StringUtil.equals(entity.getIsGuarantee(), DictCodeList.是否_否.getValue())) {
            if (interfComponentService != null) {
                List<Map<String, Object>> sendData = new ArrayList<>();
                Map<String, Object> infoMap = new HashMap<>();

                if (StringUtil.equals(entity.getPs(),
                        DictCodeList.回购拆借方向_正回购.getValue())) {
                    infoMap.put("BUY_BACK_DIR", "borrow");// 回购方向
                }
                else if (StringUtil.equals(entity.getPs(),
                        DictCodeList.回购拆借方向_逆回购.getValue())) {
                    infoMap.put("BUY_BACK_DIR", "lend");// 回购方向
                }

                infoMap.put("EXPIRE_DATE", DateUtil.format(entity.getMdate()));// 到期日

                infoMap.put("INT_EFFECT_DATE", DateUtil.format(entity.getVdate()));// 起息日

                infoMap.put("BUY_BACK_INT_RATE", com.joyintech.framework.util.ArithmeticUtil.divide(entity.getRebackPrice(),
                        BigDecimal.valueOf(100), 8).toPlainString());// 回购利率

                infoMap.put("TRAN_AMT", entity.getRolloverPrinAmt().toPlainString());// 交易金额

                infoMap.put("SETTLE_CCY", entity.getCcy());// 结算币种

                infoMap.put("OUTER_TRAN_NO", entity.getTradeId());// 外部交易号

                infoMap.put("INVEST_GROUP", entity.getPortfolioId());// 投组

                infoMap.put("DEAL_DATE", DateUtil.format(entity.getTradeDate()));// 成交日

                infoMap.put("TRAN_OPERATION_TYPE", "A – NEW");// 交易操作类型
                if (revokeFlag) {
                    infoMap.put("TRAN_OPERATION_TYPE", "R - Reverse");// 交易操作类型
                }

                infoMap.put("REMARK", entity.getRemark());// 备注

                infoMap.put("EXPIRE_SETTLE_AMT", entity.getRolloverTradeAmt().toPlainString());// 到期结算金额

                sendData.add(infoMap);
                try {
                    interfComponentService.sendMurexRequest(InterfComponentService.OrderCaller.REPO002, sendData);
                }
                catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }
    }
    /**
     * 7.7写入交易信息进交易报送信息表，为中债报送用
     *
     * @param input 画面参数dto
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_7(TrdRolloverPledgedRepo001Dto input) {
        // 7.7写入交易信息进交易报送信息表，为中债报送用
        // 1)查询产品登记编码
        String prodRegistCode = simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/"
                        + input.getPortfolioId(),
                null, String.class);
        // 若产品登记编码不为空则继续执行2），反之操作结束
        if (StringUtil.isBlank(prodRegistCode)) {
            return;
        }
        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        // 产品登记编码 = 1）输出的产品登记编码
        repTrade.setRepFinprodRegId(prodRegistCode);
        // 行内交易编码 = 金融产品交易Entity.交易编号
        repTrade.setRepBankTradeCode(input.getTradeId());
        // 行内资产/负债编码 = 金融产品交易Entity.交易编号
        repTrade.setRepBankAssetCode(input.getTradeId());
        // 币种 = 画面.币种
        repTrade.setRepCcy(input.getCcy());
        // 发生金额 = 画面.本金金额
        repTrade.setRepOccurMoney(input.getRolloverPrinAmt());
        // 数量 = 画面.本金金额
        repTrade.setRepCount(input.getRolloverPrinAmt());
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(input.getCcy(),
                DateUtil.format(input.getTradeDate()));
        BigDecimal 折算人民币金额 = com.joyintech.framework.util.ArithmeticUtil.multiply(input.getRolloverPrinAmt(), 收盘价);
        // 折算人民币金额（元） = 画面.本金金额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        repTrade.setRepMoneyCny(折算人民币金额);

        // 资金流动类型 = 数据字典缓存.资金流动类型 ※根据交易方向判断，若正回购，即拆入和正回购首期，若逆回购，即为拆出和逆回购首期
        String 资金流动类型 = null;
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_同业拆入和卖出回购首期.getValue();
        } else if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_拆放同业和买入返售首期.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);
        // 资金流动类型说明 = "无"
        repTrade.setRepMoneyFlowTypeExp("无");
        // 交易日 = 画面.交易日
        repTrade.setRepTradeDate(input.getTradeDate());
        // 交易对手方 = 输出的人员信息结果DTO.交易对手
        repTrade.setRepTradeComponent(StringUtil.isNotEmpty(input.getTrdProductDeal().getCounterId())?input.getTrdProductDeal().getCounterId():"");
        // 成交编号/合同号 = 画面.银行间成交编号
        repTrade.setRepDelCode(input.getfTradeId());
        // 报送状态 = 数据字典缓存.是否.是
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
        // 数据日期 = 系统平台日期
        repTrade.setRepStatusDate(iPlatformDate.getSysDate());
        // 备注 = 画面.备注
        repTrade.setRepRemark(input.getRemark());

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
                Arrays.asList(repTrade), Boolean.class);

        // 7.8写入负债(同业拆入/卖出回购)报送表，为中债报送用
        // 资产负债报送信息Entity
        this.assetDebtCbReportLogic_7_8_1(input);

        // 负债(同业拆入/卖出回购)报送信息Entity
        this.assetDebtCbReportLogic_7_8_2(input);
    }

    /**
     * 调用资产负债信息报送组件
     * 写入资产负债报送信息
     *
     * @param input 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_8_1(TrdRolloverPledgedRepo001Dto input) {
        // 资产负债报送信息Entity
        RepAssetDept repAssetDept = new RepAssetDept();
        // 金融产品代码 = 金融产品交易Entity.交易编号
        repAssetDept.setRepFinprodId(input.getTradeId());
        // 市场代码 = 金融产品交易Entity.交易编号
        repAssetDept.setFinprodMarketId(input.getTradeId());
        // 发行机构代码 = 系统参数缓存.发行机构代码
        repAssetDept.setRepIssuePartyId(CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID));
        // 资产/负债类别
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
            repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_卖出回购.getValue());
        } else {
            repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_买入返售.getValue());
        }
        // 币种 = 画面.币种
        repAssetDept.setRepCcy(input.getCcy());
        // 报送状态 = 数据字典缓存.是否.是
        repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue());
        // 交易流通场所
        if (StringUtil.equals(input.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
            repAssetDept.setRepTradeMarket(DictCodeListExt2.交易流通场所_中债_银行间债券市场.getValue());
        } else if (StringUtil.equals(input.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            repAssetDept.setRepTradeMarket(DictCodeListExt2.交易流通场所_中债_上海证券交易所.getValue());
        } else {
            repAssetDept.setRepTradeMarket(DictCodeListExt2.交易流通场所_中债_深圳证券交易所.getValue());
        }
        // 调用资产负债信息报送组件
        simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepAssetDebt",
                Arrays.asList(repAssetDept), Boolean.class);
    }

    /**
     * 调用负债(同业拆入卖出回购)报送信息组件
     * 写入负债(同业拆入卖出回购)报送信息
     *
     * @param input 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_8_2(TrdRolloverPledgedRepo001Dto input) {
        // 拆入、正回购 -> 负债(拆入或正回购)类要素登记
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
            // 负债(同业拆入/卖出回购)报送信息Entity
            RepLiabilities repLiabilities = new RepLiabilities();
            // 金融产品代码 = 金融产品交易Entity.交易编号
            repLiabilities.setRepFinprodId(input.getTradeId());
            // 起息日 = 画面.起息日
            repLiabilities.setRepValueDate(input.getVdate());
            // 到期日 = 画面.到期日
            repLiabilities.setRepExpiringDate(input.getMdate());
            // 对手方 = 输出的人员信息结果DTO.交易对手
            repLiabilities.setRepCounterparty(input.getTrdProductDeal().getCounterId());
            // 年利率% = 画面.利率
            repLiabilities.setRepAnnualInterestRate(input.getRebackPrice());
            // 计息基础 = 画面.计息基础
            repLiabilities.setRepBasis(input.getBasis());
            // 备注 = 画面.备注
            repLiabilities.setRepRemark(input.getRemark());
            // 回购标的类别 = 回购标的类型_中债_债券
            repLiabilities.setRepTargetType(DictCodeListExt1.回购标的类型_质押式正回购.getValue());
            // 回购标的金额 = 本金金额
            repLiabilities.setRepBuyBackMoney(input.getRolloverPrinAmt());

            // 调用负债(同业拆入卖出回购)报送信息组件
            simpleRestTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepLiabilities",
                    Arrays.asList(repLiabilities), Boolean.class);
        }
        // 拆出、逆回购 -> 拆放同业及买入返售
        else {
            RepAssetInterLend repAssetInterLend = new RepAssetInterLend();
            // 金融产品代码 = 金融产品交易Entity.交易编号
            repAssetInterLend.setRepFinprodId(input.getTradeId());
            // 起息日 = 画面.起息日
            repAssetInterLend.setRepVdate(input.getVdate());
            // 到期日 = 画面.到期日
            repAssetInterLend.setRepMdate(input.getMdate());
            // 对手方 = 输出的人员信息结果DTO.交易对手
            repAssetInterLend.setRepTradeTarget(input.getTrdProductDeal().getCounterId());
            // 年利率% = 画面.利率
            repAssetInterLend.setRepRateYear(input.getRebackPrice());
            // 计息基础 = 画面.计息基础
            repAssetInterLend.setRepBasis(input.getBasis());
            // 备注 = 画面.备注
            repAssetInterLend.setRepRemark(input.getRemark());
            // 回购标的类别 = 回购标的类型_中债_债券
            repAssetInterLend.setRepTargetType(DictCodeListExt1.回购标的类型_质押式逆回购.getValue());
            // 回购标的金额 = 本金金额
            repAssetInterLend.setRepTargetAmt(input.getRolloverPrinAmt());

            // 调用负债(同业拆入卖出回购)报送信息组件
            simpleRestTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepDlRepoInfo",
                    Arrays.asList(repAssetInterLend), Boolean.class);
        }
    }
    /**
     * 设置基本信息
     * @param input input
     * @param dto dto
     */
    private void setBaseInfo(TrdRolloverPledgedRepo001Dto input, TrdRolloverPledgedRepo001Dto dto) {
        input.setVdate(dto.getVdate());
        input.setMdate(dto.getMdate());
        input.setCcy(dto.getCcy());
        input.setBasis(dto.getBasis());
        input.setRebackPrice(dto.getRebackPrice());
        input.setTradeDate(dto.getTradeDate());
        input.setPs(dto.getPs());
        input.setTradeMarket(dto.getTradeMarket());
    }

    /**
     * 写入现金流相关信息表
     *
     * @param input 画面参数Dto
     * @see
     * @since 1.0
     */
    private void cashFlowLogic(TrdRolloverPledgedRepo001Dto input) {
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);

        BaseFinProductCashInputDto inputDto = new BaseFinProductCashInputDto();

        // List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        // 现金流信息Entity-本金
        FinCashInfo finCashInfoPrin = new FinCashInfo();
        // 现金流代码 = 调用【各种业务共通组件.现金流代码生成】生成现金流代码。输入参数如下
        // 业务代码 = 金融产品交易Entity.交易编号
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.本金
        finCashInfoPrin.setCashId(commonBusinessService.generateCashFlowCode(input.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
                DictCodeListExt1.现金流二级类型_本金.getValue()));
        // 现金流一级类型 = 数据字典缓存.现金流一级类型.本金
        finCashInfoPrin.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.本金
        finCashInfoPrin.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        // 币种 = 画面.币种
        finCashInfoPrin.setCcy(input.getCcy());
        // 业务代码 = 金融产品交易Entity.交易编号
        finCashInfoPrin.setFinprodId(input.getTradeId());
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        finCashInfoPrin.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashInfoPrin.setBranch(branchLong);
        // 工作流代码
        finCashInfoPrin.setWorkflowId(input.getWorkflowId());
        finCashInfoList.add(finCashInfoPrin);

        // 现金流信息Entity-利息
        FinCashInfo finCashInfoInt = new FinCashInfo();
        // 现金流代码 = 调用【各种业务共通组件.现金流代码生成】生成现金流代码。输入参数如下
        // 业务代码 = 金融产品交易Entity.交易编号
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
        finCashInfoInt.setCashId(commonBusinessService.generateCashFlowCode(input.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
                DictCodeListExt1.现金流二级类型_利息.getValue()));
        // 现金流一级类型 = 数据字典缓存.现金流一级类型.利息
        finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
        finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        // 币种 = 画面.币种
        finCashInfoInt.setCcy(input.getCcy());
        // 业务代码 = 金融产品交易Entity.交易编号
        finCashInfoInt.setFinprodId(input.getTradeId());
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashInfoInt.setBranch(branchLong);
        // 工作流代码
        finCashInfoInt.setWorkflowId(input.getWorkflowId());
        finCashInfoList.add(finCashInfoInt);

        inputDto.setFinCashInfoList(finCashInfoList);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        // 现金流支付规则表Entity-利息
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPayRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashPayRule.setEffDate(input.getVdate());
        // 支付频率 = 数据字典缓存.支付频率(付息频率).到期支付

        // 一体化对应etliu start
//        finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_到期支付.getValue());
        finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());
        // 一体化对应etliu end

        // 一体化对应etliu
//        finCashPayRule.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());

        // 首次支付日 = 画面.到期日
        finCashPayRule.setFirstPayDate(input.getMdate());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPayRule.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPayRule.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPayRule.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashPayRule.setBranch(branchLong);
        // 工作流代码
        finCashPayRule.setWorkflowId(input.getWorkflowId());
        finCashPayRuleList.add(finCashPayRule);

        inputDto.setFinCashPayRuleList(finCashPayRuleList);

        // List<现金流计算规则表Entity>
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();

        // 现金流计算规则表Entity-利息
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashCalcRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashCalcRule.setEffDate(input.getVdate());
        // 公布日期 = 画面.起息日
        // 一体化对应etliu
//        finCashCalcRule.setRelaeseDate(dto.getVdate()); // 公布日期
        // 计算类型 = 数据字典缓存.利率类型.固定利率
        finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
        // 基数类型 = 数据字典缓存.基数类型.本金
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        // 基数日期类型 = 数据字典缓存.基数日期类型.当日
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        // 计算基础 = 画面.计息基础
        finCashCalcRule.setBasis(input.getBasis());
        // 生效利率 = 画面.回购利率/100
        finCashCalcRule.setYield(com.joyintech.framework.util.ArithmeticUtil.divide(input.getRebackPrice(), new BigDecimal("100"), 8));
        // 是否初始利率 = 数据字典缓存.是否.是
        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashCalcRule.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashCalcRule.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashCalcRule.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashCalcRule.setBranch(branchLong);
        // 工作流代码
        finCashCalcRule.setWorkflowId(input.getWorkflowId());
        finCashCalcRuleList.add(finCashCalcRule);

        inputDto.setFinCashCalcRuleList(finCashCalcRuleList);

        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();

        // 现金流利率信息表Entity-利息
        FinRateInfo finRateInfo = new FinRateInfo();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finRateInfo.setCashId(finCashInfoInt.getCashId());
        // 生效日 = 画面.起息日
        finRateInfo.setEffDate(input.getVdate());
        // 利率类型 = 数据字典缓存.利率类型.固定利率
        // 一体化对应etliu
//        finRateInfo.setRateType(DictCodeList.利率类型_固定利率.getValue());
        finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
        // 计算基础 = 画面.计息基础
        finRateInfo.setBasis(input.getBasis());
        // 初始利率 = 画面.回购利率/100
        finRateInfo.setRate(finCashCalcRule.getYield());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finRateInfo.setFinprodId(input.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finRateInfo.setBranch(branchLong);
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finRateInfo.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finRateInfo.setFinprodType2(input.getFinprodType2());
        // 工作流代码
        finRateInfo.setWorkflowId(input.getWorkflowId());
        finRateInfoList.add(finRateInfo);

        inputDto.setFinRateInfoList(finRateInfoList);

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();

        // 现金流计划表Entity 到期还本
        FinCashPlan finCashPlan = new FinCashPlan();
        // 现金流代码 = 现金流信息Entity-本金.现金流代码
        finCashPlan.setCashId(finCashInfoPrin.getCashId());
        // 现金流序号 = 1
        finCashPlan.setCashNum(1L);
        // 现金流类型 = 数据字典缓存.现金流类型.到期还本
        finCashPlan.setCashType(DictCodeList.现金流类型_到期还本.getValue());
        // 未调整的计算开始日 = 画面.起息日
        finCashPlan.setVdateUnadjust(input.getVdate());
        // 未调整的计算结束日 = 画面.到期日
        finCashPlan.setMdateUnadjust(input.getMdate());
        // 未调整的计划支付日 = 画面.到期日
        finCashPlan.setPayDateUnadjust(input.getMdate());
        // 调整后的计算开始日 = 画面.起息日
        finCashPlan.setVdate(input.getVdate());
        // 调整后的计算结束日 = 画面.到期日
        finCashPlan.setMdate(input.getMdate());
        // 调整后的计划支付日 = 画面.到期日
        finCashPlan.setPayDate(input.getMdate());
        // 计算周期天数 = 画面.到期日-画面.起息日
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(input.getVdate(), input.getMdate()));
        // 计划支付本金 = 画面.本金金额
        finCashPlan.setPrinAmt(input.getRolloverPrinAmt());
        // 计息本金 = 画面.本金金额
        finCashPlan.setIntPrinAmt(input.getRolloverPrinAmt());
        // 计划支付利息 = 0
        finCashPlan.setIntAmt(BigDecimal.ZERO);
        // 是否支付利息 = 数据字典缓存.是否.是
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        // 计划支付现金流 = 画面.本金金额
        finCashPlan.setCashAmt(input.getRolloverPrinAmt());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPlan.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPlan.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPlan.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 固定值，系统参数
        finCashPlan.setBranch(branchLong);
        // 支付方式 = 数据字典缓存.分红方式.现金分红
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        // 工作流代码
        finCashPlan.setWorkflowId(input.getWorkflowId());
        finCashPlanList.add(finCashPlan);

        // 现金流计划表Entity 到期付息
        finCashPlan = new FinCashPlan();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPlan.setCashId(finCashInfoInt.getCashId());
        // 现金流序号 = 2
        finCashPlan.setCashNum(2L);
        // 现金流类型 = 数据字典缓存.现金流类型.到期付息
        finCashPlan.setCashType(DictCodeList.现金流类型_到期收息.getValue());
        // 未调整的计算开始日 = 画面.起息日
        finCashPlan.setVdateUnadjust(input.getVdate());
        // 未调整的计算开始日 = 画面.到期日
        finCashPlan.setMdateUnadjust(input.getMdate());
        // 未调整的计划支付日 = 画面.到期日
        finCashPlan.setPayDateUnadjust(input.getMdate());
        // 调整后的计算开始日 = 画面.起息日
        finCashPlan.setVdate(input.getVdate());
        // 调整后的计算结束日 = 画面.到期日
        finCashPlan.setMdate(input.getMdate());
        // 调整后的计划支付日 = 画面.到期日
        finCashPlan.setPayDate(input.getMdate());
        // 计算周期天数 = 画面.到期日-画面.起息日
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(input.getVdate(), input.getMdate()));
        // 计划支付本金 = 0
        finCashPlan.setPrinAmt(BigDecimal.ZERO);
        // 计息本金 = 画面.本金金额
        finCashPlan.setIntPrinAmt(input.getRolloverPrinAmt());
        // 计划支付利息 = 回购利息 ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
        finCashPlan.setIntAmt(bussCoreCalculateService.calc投资交易计算回购利息(input.getRolloverPrinAmt(),
                com.joyintech.framework.util.ArithmeticUtil.divide(input.getRebackPrice(), new BigDecimal("100"), 8),
                input.getVdate(), input.getMdate(), input.getBasis()));
        // 是否支付利息 数据字典缓存.是否.是
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        // 计划支付现金流 = 回购利息
        finCashPlan.setCashAmt(finCashPlan.getIntAmt());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPlan.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPlan.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPlan.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 固定值，系统参数
        finCashPlan.setBranch(branchLong);
        // 支付方式 = 数据字典缓存.分红方式.现金分红
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        // 工作流代码
        finCashPlan.setWorkflowId(input.getWorkflowId());
        finCashPlanList.add(finCashPlan);

        inputDto.setFinCashPlanList(finCashPlanList);

        // 调用共通登录现金流相关数据
        simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/common/trdcommonbase/insertFinProductCashData",
                inputDto, Boolean.class);
    }

    @Override
    public void doBack(TrdRolloverPledgedRepo001Dto entity) {
        List<WflTrdProductDeal> trdProductDealList = simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME+ "/trade/transfer/common/TransferCommon/selectDealByworkflowId",
                entity.getWorkflowId(), WflTrdProductDeal.class);
        if(!StringUtil.isEmptyList(trdProductDealList)){
            for (WflTrdProductDeal deal: trdProductDealList) {
                AllTradeCommonUtil.deletePosition("",deal.getTradeId());
            }
        }
    }

    public TrdRolloverPledgedRepo001Dto getData(String tradeId) {
        Map<String,Object> param = new HashMap<>();
        if(StringUtil.isNotEmpty(tradeId)){
            param.put("tradeId",tradeId);
        }
        param.put("BRANCH_0",Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getTrdRepoTradeDto",
                param, TrdRolloverPledgedRepo001Dto.class);
    }
}
