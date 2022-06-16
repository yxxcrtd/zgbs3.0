/*
 * 文件名：EtruibBondInfo003Service.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月21日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etr.bond.service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.AllocationType;
import com.joyintech.calculation.bonds.constant.Basis;
import com.joyintech.calculation.bonds.constant.CurrIntCalMethod;
import com.joyintech.calculation.bonds.constant.DiscountScheme;
import com.joyintech.calculation.bonds.constant.FlowCashScheme;
import com.joyintech.calculation.bonds.constant.IntFrequency;
import com.joyintech.calculation.bonds.constant.IntRateType;
import com.joyintech.calculation.bonds.constant.NextIntCalMethod;
import com.joyintech.calculation.bonds.constant.SecIntSchCalMethod;
import com.joyintech.calculation.bonds.constant.Variety;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.PriceYieldInParameter;
import com.joyintech.calculation.shell.param.PriceYieldOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.shell.param.YieldPriceInParameter;
import com.joyintech.calculation.shell.param.YieldPriceOutParameter;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.controller.EntrustCommonBaseController;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfo002Mapper;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrCalcAmountRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrCalcAmountResponse;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbPosPortfolioRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbPositionRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.common.base.dao.EtrEntrustBackupMapper;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.WflEtrEntrustAddKey;
import com.joyintech.tams.common.base.entity.WflEtrEntrustKey;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.FinCashPlanDto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.SecInfoModelDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 多组合债券指令新建
 *
 * @author 王波
 * @date 2021年01月21日
 * @see CfetsEtribBondInfo003Service
 * @since 1.0
 */
@Service
public class CfetsEtribBondInfo003Service extends CommonExportService<CfetsEtribBondInfo003Dto> implements BaseProcessService<CfetsEtribBondInfo003Dto> {

    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired
    private EtrCommonService etrCommonService;

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;
    @Autowired(required = false)
    private EtrCommandDispatcherService etrCommandDispatcherService;
    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;
    @Autowired
    private CfetsEtrIbBondinfo002Mapper cfetsEtrIbBondinfo002Mapper;
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;
    @Autowired(required = false)
    private SysWflBusinessListMapper sysWflBusinessListMapper;
    /**
     * 指令废弃表
     */
    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;
    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;
    @Autowired
    private CfetsEtrIbBondinfoService cfetsEtrIbBondinfoService;
    @Autowired
    private CfetsEtrIbBondinfo002Service cfetsEtrIbBondinfo002Service;

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;

    @Autowired
    private IPlatformDate pPlatformDate;

    /**
     * 判断是否含权债
     *
     * @param finprodId 金融产品代码
     * @see
     * @since 1.0
     */
    public boolean isRight(String finprodId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodId", finprodId);

        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isRight", paramMap,
                Boolean.class);
    }

    public PtlPortfolio findPortfolio(String counterId) {
        return ptlPortfolioMapper.selectByPrimaryKey(counterId);
    }
    /**
     * 获取组合分配相关数据
     *
     * @param param
     * @return
     */
    public Map<String, Object> getPortfolioAllocationValues(Map<String, Object> param) {
        // 获取平台日
        Date tradeDate = DateUtil.platDateFromCache();
        // 交易日历
        String calId = StringUtil.isNotEmpty(param.get("calId"))?(String) param.get("calId"):BussinessDictCodeList.wind交易场所_银行间.getValue();
        Date tradeDateNext = DateUtil.addDay(tradeDate, 1);
        if (StringUtil.isNotEmpty(calId)) {
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(tradeDate)), DateUtil.addDay(DateUtil.parse(DateUtil.format(tradeDate)), 15), calId);
            tradeDateNext = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(tradeDate)), 1, holidayList, "1");
        }
        Map<String, Object> result = new HashMap<>();

        if(!StringUtil.isNotEmpty(param.get("finprodId"))){
            /**
             * 当单选组合时，使用组合的币种
             */
            PtlPortfolio ptlPortfolio = findPortfolio(param.get("portfolioId") + "");
            if(ptlPortfolio != null && StringUtil.isNotEmpty(ptlPortfolio.getCcy())){
                param.put("ccy", ptlPortfolio.getCcy());
            }
        }
        // 组合信息没有币种 导致入参币种为空 取不到现金  现如果为空 默认人民币
        if(!StringUtil.isNotEmpty(param.get("ccy"))){
            param.put("ccy",DictCodeList.币种_人民币_CNY.getValue());
        }
        // 可用现金
        Map<String, Object> cash = etrCommonService.getProAvailableCash(param);
        //组合T日可用现金
        result.put("proAvailableCash",cash.get("cashT"));
        //组合T+1日可用现金
        result.put("proAvailableCashT1", cash.get("cashT1"));
        //托管户T日可用现金
        result.put("secManageAvailablePosition",cash.get("cashT3"));
        //托管户T+1日可用现金
        result.put("secManageAvailablePositionT1", cash.get("cashTT1"));
        //通道可用现金
        if (StringUtil.isNotEmpty(param.get("chlId"))) {
        	//通道T日可用现金
        	result.put("channelAvailable", cash.get("cashT2"));
        	//通道T+1日可用现金
            result.put("channelAvailableT1",cash.get("cashT21"));
        }

        if (StringUtil.isNotEmpty((String)param.get("finprodId"))) {
            // 资产可用券面总额
            EtrSecPositionDto etrSecPositionDto= new EtrSecPositionDto();
            etrSecPositionDto.setCdate(tradeDate);
            etrSecPositionDto.setFinprodId((String)param.get("finprodId"));
            etrSecPositionDto.setPortfolioId((String)param.get("portfolioId"));

            etrSecPositionDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());

            etrSecPositionDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            result.put("assetAvailableEntrustAmt", etrCommonService.getAssetAvailablePosition(etrSecPositionDto).divide(new BigDecimal(100)));
            // 资产持仓券面总额
            PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
            ptlSecPositionFlowDto.setCdate(tradeDate);
            ptlSecPositionFlowDto.setPortfolioId((String)param.get("portfolioId"));
            ptlSecPositionFlowDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
            ptlSecPositionFlowDto.setFinprodId((String)param.get("finprodId"));
            ptlSecPositionFlowDto.setEntrust(true);
            BigDecimal amount03 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto).divide(new BigDecimal(100));
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_融入.getValue());
            BigDecimal amount05 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto).divide(new BigDecimal(100));
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_融出.getValue());
            BigDecimal amount06 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto).divide(new BigDecimal(100));
            BigDecimal assetPositionEntrustAmt = ArithmeticUtil.doAdd(amount03, amount05);
            assetPositionEntrustAmt = ArithmeticUtil.subtract(assetPositionEntrustAmt, amount06);
            result.put("assetPositionEntrustAmt", assetPositionEntrustAmt);
        } else {
            result.put("assetAvailableEntrustAmt", BigDecimal.ZERO);
            result.put("assetPositionEntrustAmt", BigDecimal.ZERO);
        }
        return result;
    }
    /**
     * 设置流程业务说明
     */
    @Override
    public String generateTitle(CfetsEtribBondInfo003Dto entity, OperationType type) {
        return  "指令日期"+ ":" + DateUtil.format(entity.getEtrDate())+"-"+
                "资产简称" + ":" + (entity.getFinprodAbbr()==null? "": entity.getFinprodAbbr()) + "-"+
                "券面总额"+ ":" +  ArithmeticUtil.divide(entity.getTradeAmt(),new BigDecimal(10000),2)+"万元";
    }
    /**
     *
     * 流程发起时调用
     */
    @Override
    public void doInsert(CfetsEtribBondInfo003Dto entity, OperationType type){
        if ("submit".equals(entity.getOperateType())) {
            if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos())) {
               // List<CfetsEtrIbPosPortfolioRequest> ptlList=new ArrayList<>();
                for (CfetsEtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
                    WFlowCommonService.insertEtrEntrustRe(dto.getEntrustId(),entity.getTotalEtrustId());
                    dto.setWorkflowId(entity.getWorkflowId());
                    doInsertFlowMethod(entity.getOperateType(), initBaseEtrInvestInfoDto(dto));
                }
                //发送头寸
                //sendPositionMethod(entity,entity.getSubmitFlag());
                sendPositionMethod(entity, StringUtil.equals(entity.getSystemAct(), DictCodeListExt3.系统动作_新建.getValue()),null);

            }
        }
        //无附件上传，删除
       // doInsertMethod(entity, entity.getOperateType());
        for (CfetsEtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
            //更新操作记录日志
            CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
            oprHisReq.setEntrustId(dto.getEntrustId());
            if (StringUtil.equals(type.name(), "NEW")) {
                oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
            } else {
                oprHisReq.setOperType(DictCodeListExt3.操作类型_修改.getValue());
            }
            oprHisReq.setBusiDes(entity.getRemark());
            joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        }
    }

    @Override
    public void doUpdate(CfetsEtribBondInfo003Dto entity) {
        if("save".equals(entity.getOperateType())){
            return;
        }
        String confirmOk = RequestUtil.getParam("confirmOk");
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            boolean isSubmit = true;
            SysWflBusinessList sysBus=sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
            if(null!=sysBus && StringUtil.equals(sysBus.getProcessState(),DictCodeList.流程状态_已退回.getValue())){
                isSubmit = false;
            }
            if("submit".equals(entity.getOperateType()) && isSubmit) {
                cfetsEtrIbBondinfoService.packageEquityForSubmit(entity);
                List<CfetsEtribBondInfo003Dto> dataList = new ArrayList<>();
                for (CfetsEtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
                    if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("portfolioId", dto.getPortfolioId());
                        map.put("finprodId", dto.getFinprodId());
                        String secAcctId = cfetsEtrIbBondinfo002Service.qryPortfolioSecAcctId(map);
                        if (StringUtil.isNotEmpty(secAcctId)) {
                            dto.setSecAcctId(secAcctId); // 证券账户
                        }
                    }
                    if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                        dataList.add(dto);
                    }
                }
                EntrustRdsCommonAop.riskListToChildren(entity.getRiskTrialRetDto(), entity.getEtrInvestBondInfoDtos(),(dto, list) -> dto.setRiskTrialRetDto(list));
            }

            WFlowCommonService.deleteFlow(entity.getWorkflowId());
            for (CfetsEtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
                dto.setWorkflowId(entity.getWorkflowId());
                BaseEtrInvestInfoDto base = initBaseEtrInvestInfoDto(dto);
                WFlowCommonService.insertFlow(base);
                if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                    //插入旧头寸
                    if(baseEtrCashPositionCommService != null && StringUtil.isNotEmpty(base.getEtrEntrust().getEntrustId())){
                        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
                        entrustCommonDto.setEtrEntrust(base.getEtrEntrust());
                        entrustCommonDto.setEtrEntrustAdd(base.getEtrEntrustAdd());
                        entrustCommonDto.setIsExistNew(DictCodeList.是否_是.getValue());
                        baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
                    }
                }
            }
            //发送头寸
            //entity.setSoftSkipMode("Y");
            try {
                sendPositionMethod(entity, isSubmit,confirmOk);
            }catch (Exception e) {
                Message message = PositionUtil.ajaxErrorReturn(e);
                BaseMap bm = (BaseMap) message.getData();
                String code = (String) bm.get("code");
                StringBuffer messageStr = (StringBuffer) bm.get("message");
                if (StringUtil.isEmpty(confirmOk) && StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                }
                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.错误消息);
                }
                throw new PositionException(messageStr.toString());
            }
        }
    }

    @Override
    public void doBack(CfetsEtribBondInfo003Dto entity) {
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos()))  {
            for (CfetsEtribBondInfo003Dto dto: entity.getEtrInvestBondInfoDtos()) {
                if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                    //插入旧头寸
                    AllTradeCommonUtil.deletePosition(null, dto.getEntrustId());
                }
            }
        }
    }

    /**
     * 修改指令回退时,从废弃表将数据取出重新插入
     *
     * @param entrustId 指令代码
     * @author jiangpeng
     * @date 2022/3/24 9:01
     */
    public void backReRoll(String entrustId) {
        //从废弃表中获取，转移到指令表
        etrEntrustBackupMapper.insertEtrFromQuery(entrustId);
        etrEntrustBackupMapper.insertEtrAddFromQuery(entrustId);
        // 删除废弃表
        etrEntrustBackupMapper.deleteByPrimaryKey(entrustId);
        etrEntrustBackupMapper.deleteAddByPrimaryKey(entrustId);
    }

    private void doInsertFlow(CfetsEtribBondInfo003Dto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            //BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().divide(new BigDecimal(100)));
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            //etrEntrustAdd.setOurEntNum(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            //etrEntrustAdd.setLendinEntNum(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            etrEntrustAdd.setFullPrice(dto.getUnitFprice());
           /* if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }*/
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
                    Boolean.class);
            if(baseEtrCashPositionCommService!=null){
                baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(CfetsEtribBondInfo003Dto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/check", dto,
                ValidMessage.class);
    }

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param parameters 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAcctId(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId",
                parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String)parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String)parameters.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     *
     * 净价、行权收益率、到期收益率联动
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    public void calcAmt(CfetsEtribBondInfo003Dto dto) {
        if(dto == null || StringUtil.isEmpty(dto.getFinprodId())){
            return;
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            dto.setSettleSpeed(String.valueOf(0));
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            dto.setSettleSpeed(String.valueOf(1));
        }
        if (dto == null || dto.getEtrDate() == null ) {
            CfetsEtribBondInfo003Dto nullDto = new CfetsEtribBondInfo003Dto();
            try {
                nullDto.setPrice(dto.getPrice());
                nullDto.setExerYield(dto.getExerYield());
                nullDto.setMatureYield(dto.getMatureYield());
                BeanUtil.copyProperties(dto, nullDto);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        dto.setSettleDate(dto.getEtrDate());
        dto.setVdate(dto.getEtrDate());
        dto.setMdate(dto.getEtrDate());
        if(StringUtil.isNotEmpty(dto.getSettleSpeed())){
            if(Integer.parseInt(dto.getSettleSpeed())!=0){
                List<String> holiday = dateCalService.getDealCalendarHoliday(dto.getVdate(),
                        DateUtil.addDays(dto.getVdate(), 31),dto.getCalendarId());
                Date tradingDay = dateCalService.calculationTradingDay(dto.getVdate(), Integer.parseInt(dto.getSettleSpeed()), holiday, "1");
                dto.setSettleDate(tradingDay);
            }
        }

        if (DateUtil.compareDate(dto.getSettleDate(), dto.getFinvdate()) < 0) {
            dto.setSettleDate(dto.getFinvdate());
        }

        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = true;
        if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
                && !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket())) {
            // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量
            this.getUnitIntFromValution(dto);
            isJiaoYiSuo = false;
        }

        final int SCALE = 12;

        //到期收益率
        //dto.setMatureYield(ArithmeticUtil.divide(dto.getMatureYield(), 100, 14));
        //行权收益率
        //dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), 100, 14));

        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = null;
        PriceYieldInParameter priceYieldInParameter = null;
        YieldPriceOutParameter yieldPriceOutParameter = null;
        PriceYieldOutParameter priceYieldOutParameter = null;

        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        tempParamMap.put("finprodId", dto.getFinprodId());
        tempParamMap.put("ps", dto.getPs());
        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
//         List<BondsIntReset> bondsIntResetList = transactionBond001Mapper.getInvRateQuo(dto);
        List<FinCashCalcRule> finCashCalcRuleList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectBondsIntResetList",
                tempParamMap, FinCashCalcRule.class);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinCashPlan",
                tempParamMap, FinCashPlanDto.class);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                    DictCodeListExt1.现金流一级类型_利息.getValue())
                    && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);

                intSchList.add(item);
            }
        }

        // d.获取债券基本资料信息
        SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel",
                tempParamMap, SecInfoModelDto.class);
        FinCashCalcRule finCashCalcRule = joyinbusinessCommonMapper.selectBasisByFinprodId(dto.getFinprodId(), pPlatformDate.getSysDate());
        if (StringUtil.isNotEmpty(finCashCalcRule) && StringUtil.isNotEmpty(finCashCalcRule.getMarketBasis())) {
            secInfoModelDto.setBasis(finCashCalcRule.getMarketBasis());
        }
        SecInfoModel secInfoModel = this.toSecInfoModel(secInfoModelDto);
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 付息债的计息基础使用A_365F
            if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
                secInfoModel.setBasis(Basis.A_365F);
            }
        }

        // e.获取债券的还本计划信息
        List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                    DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(
                        StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }

        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(
                    BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            //多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        yieldPriceInParameter = new YieldPriceInParameter();
        yieldPriceInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        priceYieldInParameter = new PriceYieldInParameter();
        priceYieldInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        priceYieldInParameter.setSecInfoModel(secInfoModel);
        priceYieldInParameter.setSecIntScheduleList(invScheduleList);
        priceYieldInParameter.setSecPrinScheduleList(secPrinScheduleList);
        priceYieldInParameter.setPaperIrResetList(paperIrResetList);
        yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
            if (isNotFomartSec(intSchList, secInfoModelDto)) {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                priceYieldInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
            } else {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
            }
        }
        yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());
        priceYieldInParameter.setCalMethod(secInfoModel.getCalMethod());
        // 后续区间利息计算方案
        if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
        } else {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
        }
        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        // 交易方式为“一级市场”时，单位应计利息=应计利息总额=0
        if (StringUtil.equals(dto.getTradeMarketMode(),
                DictCodeListExt1.交易市场_一级市场.getValue())) {
            dto.setIntAmt(BigDecimal.ZERO);
            dto.setUnitInt(accurInt);
        }
        // 交易方式为“二级市场”时，单位应计利息 =e取得的百元面值应计利息/100*a取得的当前债券面额
        else if (StringUtil.equals(dto.getTradeMarketMode(),
                DictCodeListExt1.交易市场_二级市场.getValue())) {

            //交易所债券特殊处理start----------------------------------------------------------------
            Date tempSettleDate = dto.getSettleDate();
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())) {
                    tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
                }
            }
            //交易所债券特殊处理end----------------------------------------------------------------

            // a.当前债券面额取得（根据还本计划计算）
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("finprodId", dto.getFinprodId());
//            paraMap.put("settleDate", DateUtil.format(dto.getSettleDate()));
            paraMap.put("settleDate", DateUtil.format(tempSettleDate));
            BigDecimal curSecAmt = restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCurSecAmt",
                    paraMap, BigDecimal.class);

            AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
            // 计算日
//            accurIntCalInParameter.setCalDate(this.toLocalDate(dto.getSettleDate()));
            accurIntCalInParameter.setCalDate(this.toLocalDate(tempSettleDate));
            // 债券基本资料
            accurIntCalInParameter.setSecInfoModel(secInfoModel);
            // 付息计划
            accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
            // 利率重置信息
            accurIntCalInParameter.setPaperIrResetList(paperIrResetList);

            //交易所债券特殊处理start----------------------------------------------------------------
            AccurIntCalOutParameter out = null;
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                Map<String, BigDecimal> intMap = new HashMap<>();
                if(null != invScheduleList && !invScheduleList.isEmpty()) {
                    for(SecIntSchedule intSche : invScheduleList) {
                        intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                    }
                }

                if(intMap.containsKey(accurIntCalInParameter.getCalDate().toString()) &&
                        !((Variety.贴现.equals(secInfoModel.getBondsType())) || Variety.零息.equals(secInfoModel.getBondsType()))) {
                    out = new AccurIntCalOutParameter();
                    out.setInterest(intMap.get(accurIntCalInParameter.getCalDate().toString()).multiply(new BigDecimal("100")
                    ).divide(curSecAmt, 12, RoundingMode.HALF_UP));
                    accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                    AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                    if (accurIntCalInParameter.getPaperIrResetList() != null && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                        accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                    }
                    out = calculator2.calculate(accurIntCalInParameter);
                }
//                }
            }
            //交易所债券特殊处理end----------------------------------------------------------------

            if (out != null) {
                accurInt = ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
            }
            else {
                // 从计算工厂中创建应计利息计算器
                AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
                // 计算利息
                AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
                accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
            }

            // 单位应计利息
            if (dto.getUnitInt() == null) {
                if (isJiaoYiSuo) {
                    dto.setUnitInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")), RoundingMode.HALF_UP));
                }
                else {
                    dto.setUnitInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_BANK")), RoundingMode.HALF_UP));
                }
            }
        }
        try {
            // 单位净价
            if (StringUtil.equals(dto.getCalflag(), "price")) {
                // 单位全价 = 单位净价 + 单位应计利息
                dto.setUnitFprice(ArithmeticUtil.doAdd(dto.getPrice(), dto.getUnitInt()));
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getEntrustNum()),
                        new BigDecimal(100), SCALE));
//                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
            }
//            // 全价总额
//            else if (StringUtil.equals(dto.getCalculateField(), "fpriceAmt")) {
//                // 单位全价 = 全价总额 * 100 / 券面总额
//                dto.setUnitFprice(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getFpriceAmt(), new BigDecimal(100)),
//                    dto.getPrinAmt(), SCALE));
//                // 单位净价 = 单位全价 - 单位应计利息
//                dto.setUnitCprice(ArithmeticUtil.doSub(dto.getUnitFprice(), dto.getUnitInt()));
//                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
//            }
            switch (dto.getCalflag()) {
                // 单位净价
                case "price":
                    // 全价总额
                case "fpriceAmt":
                    // 全价总额==>到期收益率
                    yieldPriceInParameter.setFullPrice(
                            dto.getUnitFprice()/* dto.getFpriceAmt() */);
                    yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                            yieldPriceInParameter);
                    dto.setMatureYield(yieldPriceOutParameter.getMatYield());
                    if (secInfoModelDto.getNextExeDate() != null) {
                        // 全价总额==>行权收益率
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                    yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getExeYield());
                        }
                        else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 到期收益率
                case "matureYield":
                    // 到期收益率==>全价总额
                    priceYieldInParameter.setMatYield(dto.getMatureYield());
                    priceYieldOutParameter = priceByYieldCalculator.calculate(
                            priceYieldInParameter);
                    // dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                    dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                    // 全价总额==>行权收益率
                    if (secInfoModelDto.getNextExeDate() != null) {
                        yieldPriceInParameter.setFullPrice(
                                dto.getUnitFprice()/* dto.getFpriceAmt() */);
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                    yieldPriceInParameter);
                           // dto.setExerYield(yieldPriceOutParameter.getMatYield());
                            dto.setExerYield(yieldPriceOutParameter.getExeYield());
                        }
                        else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 行权收益率
                case "exerYield":
                    // 行权收益率==>全价总额
                    if (secInfoModelDto.getNextExeDate() != null) {
//                        priceYieldInParameter.setMatYield(dto.getMatureYield());
                        priceYieldInParameter.setMatYield(dto.getExerYield());//行权收益
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        priceYieldOutParameter = priceByYieldCalculator.calculate(
                                priceYieldInParameter);
                        dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                        dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                        // 全价总额==>到期收益率
                        yieldPriceInParameter.setFullPrice(dto.getFpriceAmt());
                        yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                yieldPriceInParameter);
                        dto.setMatureYield(yieldPriceOutParameter.getMatYield());
                    }
                    break;
                default:
                    break;
            }
            // 到期收益率 或 行权收益率
            if (StringUtil.equals(dto.getCalflag(), "matureYield")
                    || StringUtil.equals(dto.getCalflag(), "exerYield")) {
                // 单位全价 = 全价总额 * 100 / 券面总额
                // dto.setUnitFprice(ArithmeticUtil.doDiv(
                // ArithmeticUtil.doMul(dto.getFpriceAmt(), new BigDecimal(100)),
                // dto.getPrinAmt(), 12));
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getEntrustNum()),
                        new BigDecimal(100), SCALE));
                // 单位净价 = 单位全价 - 单位应计利息
                dto.setPrice(com.joyintech.calculation.util.ArithmeticUtil.doSub(dto.getUnitFprice(), dto.getUnitInt()));
                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

     /*   if (dto.getMatureYield() != null) {
            dto.setMatureYield(ArithmeticUtil.multiply(dto.getMatureYield(), new BigDecimal(100)));
        }
        if (dto.getExerYield() != null) {
            dto.setExerYield(ArithmeticUtil.multiply(dto.getExerYield(), new BigDecimal(100)));
        }*/
    }

    /**
     * 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量，
     * 如取不到估值核算中前一个自然日的数据，计算方式见算法文档；
     * 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT
     * （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
     *
     * @param dto 参数
     * @return 应计利息
     * @see
     * @since 1.0
     */
    private void getUnitIntFromValution(CfetsEtribBondInfo003Dto dto) {
        // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量，如取不到估值核算中前一个自然日的数据，计算方式见算法文档；
        // 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT  （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
      /*    Map<String, Object> paraMapForIntAmt = new HashMap<String, Object>();
        paraMapForIntAmt.put("settleDate", DateUtil.format(dto.getSettleDate()));
        paraMapForIntAmt.put("portfolioId", dto.getPortfolioId());
        paraMapForIntAmt.put("finprodId", dto.getFinprodId());
        paraMapForIntAmt.put("invAim", dto.getInvAim());
        paraMapForIntAmt.put("secAcctId", dto.getChlId());
        BigDecimal 应计利息 = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getIntAmtFromValution",
                paraMapForIntAmt, BigDecimal.class);
      if (应计利息 != null) {
            dto.setUnitInt(应计利息);
        }*/
    }

    /**
     * 转换数据
     *
     * @param secInfoModelDto 债券基本信息SecInfoModelDto
     * @return 债券基本信息SecInfoModel
     * @see
     * @since 1.0
     */
    private SecInfoModel toSecInfoModel(SecInfoModelDto secInfoModelDto) {
        SecInfoModel secInfoModel = new SecInfoModel();

        secInfoModel.setStartDate(toLocalDate(secInfoModelDto.getStartDate()));
        secInfoModel.setEndDate(toLocalDate(secInfoModelDto.getEndDate()));
        secInfoModel.setFirstPayDate(toLocalDate(secInfoModelDto.getFirstPayDate()));
        // 利率类型
        if (StringUtil.equals(DictCodeList.利率类型_固定利率.getValue(),
                secInfoModelDto.getIntRateType())) {
            secInfoModel.setIntRateType(IntRateType.固定利率);
        }
        else {
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }

        if (StringUtil.isNotBlank(secInfoModelDto.getBasis())) {
            String basis = StringUtil.splitToList(GlobalConstant.COMMA, secInfoModelDto.getBasis()).get(0);
            DictCodeList basisDict = DictCodeList.from(DictCode.计息基础, basis);
            if (basisDict != null){
                switch (basisDict) {
                    case 计息基础_30_360:
                        secInfoModel.setBasis(Basis._30_360);
                        break;
                    case 计息基础_A_360:
                        secInfoModel.setBasis(Basis.A_360);
                        break;
                    case 计息基础_A_365:
                        secInfoModel.setBasis(Basis.A_365);
                        break;
                    case 计息基础_A_365F:
                        secInfoModel.setBasis(Basis.A_365F);
                        break;
                    case 计息基础_A_A:
                    case 计息基础_A_A_平均分配:
                    case 计息基础_A_A_按实际天数分配:
                        secInfoModel.setBasis(Basis.A_A);
                        break;
                }
                if (basisDict.equals(DictCodeList.计息基础_A_A_平均分配)) {
                    secInfoModel.setAllocationType(AllocationType.平均分配);
                }
                else {
                    secInfoModel.setAllocationType(AllocationType.按实际天数分配);
                }
            }else {
                DictCodeListExt3 basisDict3 = DictCodeListExt3.from(DictCode.计息基础, basis);
                if (basisDict3 != null){
                    switch (basisDict3) {
                        case 计息基础_30U_360:
                            secInfoModel.setBasis(Basis._30U_360);
                            break;
                    }

                }
            }
        }
        DictCodeList bondsTypeDict = DictCodeList.from(DictCode.息票品种,
                secInfoModelDto.getBondsType());
        switch (bondsTypeDict) {
            case 息票品种_零息:
                secInfoModel.setBondsType(Variety.零息);
                break;
            case 息票品种_利随本清:
                secInfoModel.setBondsType(Variety.利随本清);
                break;
            case 息票品种_贴现:
                secInfoModel.setBondsType(Variety.贴现);
                break;
            case 息票品种_附息:
                secInfoModel.setBondsType(Variety.附息);
                break;
        }

        // 一体化对应etliu start
//        if (StringUtil.equals(DictCodeList.推算方法_起息日向后.getValue(),
        if (StringUtil.equals(DictCodeList.推算方式_起息日向后.getValue(),
                secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.起息日向后);
        }
//        else if (StringUtil.equals(DictCodeList.推算方法_到期日向前.getValue(),
        else if (StringUtil.equals(DictCodeList.推算方式_到期日向前.getValue(),
                secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.到期日向前);
        }
        else {
            secInfoModel.setCalMethod(SecIntSchCalMethod.首次付息日);
        }

//        if (StringUtil.equals(DictCodeList.支付频率_付息频率_月付.getValue(),
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.月付);
        }
//        else if (StringUtil.equals(DictCodeList.支付频率_付息频率_季付.getValue(),
        else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.季付);
        }
//        else if (StringUtil.equals(DictCodeList.支付频率_付息频率_半年付.getValue(),
        else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
                // 一体化对应etliu end
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.半年付);
        }
        else {
            secInfoModel.setYearIntRate(IntFrequency.年付);
        }

        secInfoModel.setIssuePrice(secInfoModelDto.getIssuePrice());

        return secInfoModel;
    }

    public boolean isNotFomartSec(List<FinCashPlanDto> secIntScheduleList, SecInfoModelDto secInfoModelDto) {
        // 根据【实际起始日】排序付息计划
        Collections.sort(secIntScheduleList, new Comparator<FinCashPlanDto>() {
            @Override
            public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                return o1.getVdateUnadjust().compareTo(o2.getVdateUnadjust());
            }
        });
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 1);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 3);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 6);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addYear(she.getVdateUnadjust(), 1);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 转换数据
     *
     * @param date 日期
     * @return LocalDate
     * @see
     * @since 1.0
     */
    private LocalDate toLocalDate(Date date) {
        if (date != null) {
            return LocalDate.parse(DateUtil.format(date));
        }
        return null;
    }

    @Override
    public void doComplete(CfetsEtribBondInfo003Dto entity, OperationType type) {
        entity.setOperateType(type.toString());
        entity.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        if(StringUtil.isNotEmpty(entity.getSettleSpeed())){
            if(Integer.parseInt(entity.getSettleSpeed())!=0){
                List<String> holiday = dateCalService.getDealCalendarHoliday(entity.getVdate(),
                        DateUtil.addDays(entity.getVdate(), 31),entity.getCalendarId());
                Date tradingDay = dateCalService.calculationTradingDay(entity.getVdate(), Integer.parseInt(entity.getSettleSpeed()), holiday, "1");
                entity.setSettleDate(tradingDay);
                // 每个组合设置交割日
                if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos())) {
                    for (int i = 0; i < entity.getEtrInvestBondInfoDtos().size(); i++) {
                        entity.getEtrInvestBondInfoDtos().get(i).setSettleDate(tradingDay);
                    }
                }
            }
        }
        for (CfetsEtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
            basRiskMathCommService.insert(entity.getRiskTrialRetDto(), dto.getEntrustId());
        }


        if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos()))  {
            for (CfetsEtribBondInfo003Dto dto: entity.getEtrInvestBondInfoDtos()) {
                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", dto.getEntrustId());
                doDeleteEtrFlowMethod(param);
            }
        }
        WFlowCommonService.doCompleteFlow(entity.getWorkflowId());
        for (CfetsEtribBondInfo003Dto dto: entity.getEtrInvestBondInfoDtos()) {
            try {
                doCompleteMethod(dto, entity.getOperateType());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("新建指令失败", e);
            }
        }
    }

    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
    }

    private void doDeleteFlow(String workflowId) {
        CfetsEtribBondInfo003Dto entity = getBussinessEntityByWorkflowId(workflowId);
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos()))  {
            for (CfetsEtribBondInfo003Dto dto: entity.getEtrInvestBondInfoDtos()) {
                if(StringUtil.isNotEmpty(dto.getEntrustId())) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("entrustId", dto.getEntrustId());
                    doDeleteEtrFlowMethod(param);
                    //从废弃表中获取，转移到指令表
                    etrEntrustBackupMapper.insertEtrFromQuery(dto.getEntrustId());
                    etrEntrustBackupMapper.insertEtrAddFromQuery(dto.getEntrustId());
                    // 删除废弃表
                    etrEntrustBackupMapper.deleteByPrimaryKey(dto.getEntrustId());
                    etrEntrustBackupMapper.deleteAddByPrimaryKey(dto.getEntrustId());
                }

            }
        }
        //发送头寸
      //  PositionHttpRequest.deleteMethod(entity.getTotalEtrustId(),DictCodeListExt1.子系统代码_委托.getValue(),"Bond001");
        String busiType =DictCodeListExt3.头寸业务类型_现券交易.getValue();
        if(StringUtil.isNotEmpty(entity.getPs())
                && StringUtil.equals(entity.getPs(),BussinessDictCodeList.交易方向_行权回售.getValue())){
            busiType =DictCodeListExt3.头寸业务类型_债券行权交易.getValue(); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
        }
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(),DictCodeListExt1.子系统代码_委托.getValue(),busiType,entity.getPs());
        }
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        CfetsEtribBondInfo003Dto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
            	if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH_IS_TOTAL), "Y")){
           		 	return getEtrLimitSwitch(entity.getWorkflowId(), dto, commonBusinessService);
            	}else{
//           		BussinessConstant.是否需要上级审批
	           		List<CfetsEtribBondInfo003Dto> etribRepoInfo003DtoList =dto.getEtrInvestBondInfoDtos();
	           		Map<String, Object> result = new HashMap<>();
	           		for(CfetsEtribBondInfo003Dto CfetsEtribBondInfo003Dto:etribRepoInfo003DtoList){
	           			result=getEtrLimitSwitch(entity.getWorkflowId(), CfetsEtribBondInfo003Dto, commonBusinessService);
	           			if((boolean)result.get(BussinessConstant.是否需要上级审批)==true){
	           				return result;
	           			}
	           		}
	           		return result;
           		}
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
    }
    
    private Map<String, Object> getEtrLimitSwitch(String workflowId, CfetsEtribBondInfo003Dto dto,
			CommonBusinessService commonBusinessService) throws WorkFlowException {
    	if (dto.isImportFlow()) {
            Map<String, Object> param = new HashMap<>();
            param.put(BussinessConstant.是否需要上级审批, true);
            return param;
        }
        BigDecimal amount = dto.getEntrustNum();
        if (amount == null) {
            amount = dto.getEntrustAmt();
        }
        Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getEntrustNum(), LoginUserUtil.getUserName(),
                BussinessDictCodeList.平安资产类型_债券.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                , dto.getFinprodId(), workflowId
        );
        // add by zhangrui1 风险审批修改 start
        params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
        // add by zhangrui1 风险审批修改 end
        return commonBusinessService.是否需要上级审批_委托用(params);
	}
    public boolean doDeleteEtrFlowMethod(Map<String,Object> param){
        String entrustId = (String)param.get("entrustId");
        WflEtrEntrustKey wflEtrEntrustKey = new WflEtrEntrustKey();
        wflEtrEntrustKey.setEntrustId(entrustId);
        wflEtrEntrustKey.setWorkflowStatus(DictCodeListExt3.委托状态_流程中.getValue());

        WflEtrEntrustAddKey wflEtrEntrustAddKey = new WflEtrEntrustAddKey();
        wflEtrEntrustAddKey.setEntrustId(entrustId);
        wflEtrEntrustAddKey.setWorkflowStatus(DictCodeListExt3.委托状态_流程中.getValue());

        cfetsEtrIbBondinfo002Mapper.deleteWflEtrEntrustAddByEntrustId(entrustId);
        cfetsEtrIbBondinfo002Mapper.deleteWflEtrEntrustByEntrustId(entrustId);
        return true;
    }
    /**
     * 流程结束后操作
     *
     * @param dto           实体类
     * @param operationType 操作类型
     */
    public void doCompleteMethod(CfetsEtribBondInfo003Dto dto, String operationType) throws IllegalAccessException, InvocationTargetException {
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
//            由于dto.getEtrInvestPersonInfo()中InvManagerId为null，因此审批后的业务表中无项目经理，故隐藏掉这行代码
//            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().divide(new BigDecimal(100)));
            etrEntrust.setInvAim(dto.getBusinessModel());
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setOurEntNum(ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            etrEntrustAdd.setLendinEntNum(ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            // add by yushilon 全价入库 20200710 start
            etrEntrustAdd.setFullPrice(dto.getUnitFprice());
            // add by yushilon 全价入库 20200710 end
           /* if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }*/
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 0);
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 1);
            }
            String busiTypePosition=DictCodeListExt3.头寸业务类型_现券交易.getValue();
            if(StringUtil.isNotEmpty(dto.getPs())
                    && StringUtil.equals(dto.getPs(),BussinessDictCodeList.交易方向_行权回售.getValue())){
                busiTypePosition=DictCodeListExt3.头寸业务类型_债券行权交易.getValue(); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
            }
            etrEntrustAdd.setBusiTypePosition(busiTypePosition);
            //指令下达时间
            if(StringUtil.isNotEmpty(dto.getTransmitTime())){
                etrEntrustAdd.setTransmitTime(dto.getTransmitTime());
            }else{
                etrEntrustAdd.setTransmitTime(new Date());
            }
            //默认证券账户
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())
                && !StringUtil.isNotEmpty(dto.getSecAcctId())
            ) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    etrEntrustAdd.setSecAcctId(secAcctId); // 证券账户
                }
            }
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            /*if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {

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
            }*/
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
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
        } else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            dto.setRecStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
//            由于dto.getEtrInvestPersonInfo()中InvManagerId为null，因此审批后的业务表中无项目经理，故隐藏掉这行代码
//            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().divide(new BigDecimal(100)));
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setOurEntNum(ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            etrEntrustAdd.setLendinEntNum(ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            etrEntrustAdd.setFullPrice(dto.getUnitFprice());
           /* if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }*/
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 0);
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 1);
            }
            String busiTypePosition=DictCodeListExt3.头寸业务类型_现券交易.getValue();
            if(StringUtil.isNotEmpty(dto.getPs())
                    && StringUtil.equals(dto.getPs(),BussinessDictCodeList.交易方向_行权回售.getValue())){
                busiTypePosition=DictCodeListExt3.头寸业务类型_债券行权交易.getValue(); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
            }
            etrEntrustAdd.setBusiTypePosition(busiTypePosition);
            //指令下达时间
            if(StringUtil.isNotEmpty(dto.getTransmitTime())){
                etrEntrustAdd.setTransmitTime(dto.getTransmitTime());
            }else{
                etrEntrustAdd.setTransmitTime(new Date());
            }
            //默认证券账户
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())
                    && !StringUtil.isNotEmpty(dto.getSecAcctId())
            ) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    etrEntrustAdd.setSecAcctId(secAcctId); // 证券账户
                }
            }
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo())
                    && StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
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
            }
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
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
//        if (baseEtrCashPositionCommService != null) {
//            baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
//        }
        //add by tanxuan on 20200514 指令分发推送信息到交易etrCommandDispatcherService系统
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", dto.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }

    }

    public BaseEtrInvestInfoDto initBaseEtrInvestInfoDto(CfetsEtribBondInfo003Dto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
            dto.setInvManagerId(LoginUserUtil.getUserName());
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            //BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().divide(new BigDecimal(100)));
            etrEntrust.setInvAim(dto.getBusinessModel());
            if (StringUtil.isEmpty(dto.getSecAcctId())) {
                //默认证券账户
                if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("finprodId", dto.getFinprodId());
                    String secAcctId = cfetsEtrIbBondinfo002Service.qryPortfolioSecAcctId(map);
                    if (com.joyintech.framework.util.StringUtil.isNotEmpty(secAcctId)) {
                        dto.setSecAcctId(secAcctId); // 证券账户
                    }
                }
            }
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setOurEntNum(ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            etrEntrustAdd.setLendinEntNum(ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            etrEntrustAdd.setFullPrice(dto.getUnitFprice());
          /*  if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }*/
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            etrEntrustAdd.setCalMode(dto.getCalculateField());
            //默认证券账户
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())
                    && !StringUtil.isNotEmpty(dto.getSecAcctId())
            ) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    dto.setSecAcctId(secAcctId); // 证券账户
                }
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            return entity;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 操作类型为submit时执行新增流程数据 & 持仓处理
     * @param operateType
     * @param dto
     */
    public  void doInsertFlowMethod(String operateType, BaseEtrInvestInfoDto dto){
        if ("submit".equals(operateType)) {
            if(StringUtil.isEmpty(dto.getWorkFlowId())){
                throw new RuntimeException("WorkFlowId初始化不能为空!");
            }
            WFlowCommonService.insertFlow(dto);
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                //插入旧头寸
                if(baseEtrCashPositionCommService != null){
                    EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
                    entrustCommonDto.setEtrEntrust(dto.getEtrEntrust());
                    entrustCommonDto.setEtrEntrustAdd(dto.getEtrEntrustAdd());
                    entrustCommonDto.setIsExistNew(DictCodeList.是否_是.getValue());
                    baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
                }
            }
        }
    }
    public String qryPortfolioSecAcctId(Map<String, Object> param) {
        return cfetsEtrIbBondinfo002Mapper.qryPortfolioSecAcctId(param);
    }
    public String qryPortfolioCashAcctId(Map<String, Object> param) {
        return cfetsEtrIbBondinfo002Mapper.qryPortfolioCashAcctId(param);
    }
    /**
     *
     * 净新建指令金额计算功能联动
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    public CfetsEtrCalcAmountResponse calAmountData(CfetsEtrCalcAmountRequest dto) {
        final int SCALE = 12;
        CfetsEtrCalcAmountResponse result=new CfetsEtrCalcAmountResponse();
        result.setPrice(dto.getPrice());
        dto.setUnitFprice(ArithmeticUtil.round(dto.getUnitFprice(),4,RoundingMode.HALF_UP));
        result.setUnitFprice(dto.getUnitFprice());
        if(StringUtil.isNotEmpty(dto.getPrice()) && StringUtil.isNotEmpty(dto.getUnitFprice())){
            //应计利息
            result.setUnitInt(ArithmeticUtil.subtract(result.getUnitFprice(),result.getPrice()));
        }
        //净价总额
        if(StringUtil.isNotEmpty(dto.getShareNum())){
            //券面总额
            BigDecimal entrustNum=dto.getShareNum()==null?BigDecimal.ZERO:dto.getShareNum();
            result.setPriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(result.getPrice(), entrustNum),
                    new BigDecimal(100), SCALE));
            //应计利息总额
            result.setUnitIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(result.getUnitInt(), entrustNum),
                    new BigDecimal(100), SCALE));
            //全价总额= 单位全价 * 券面总额/100
            result.setUnitFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(result.getUnitFprice(), entrustNum),
                    new BigDecimal(100), SCALE));
        }
        if(StringUtil.isNotEmpty(dto.getEntrustOwnNum())){
            //本方券券面总额
            BigDecimal entrustOwnNum=dto.getEntrustOwnNum()==null?BigDecimal.ZERO:dto.getEntrustOwnNum();
            //本方券应计利息总额
            result.setEntrustOwnInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(result.getUnitInt(), entrustOwnNum),
                    new BigDecimal(100), SCALE));
            //本方券全价总额
            result.setEntrustOwnUnitFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(result.getUnitFprice(), entrustOwnNum),
                    new BigDecimal(100), SCALE));
        }
        if(StringUtil.isNotEmpty(dto.getEntrustIntegrateNum())){
            //融入券/卖空购回券面总额
            BigDecimal entrustIntegrateNum=dto.getEntrustIntegrateNum()==null?BigDecimal.ZERO:dto.getEntrustIntegrateNum();
            //卖空购回应计利息总额
            result.setEntrustIntegrateInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(result.getUnitInt(), entrustIntegrateNum),
                    new BigDecimal(100), SCALE));
            //卖空购回全价总额
            result.setEntrustIntegrateUnitFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(result.getUnitFprice(), entrustIntegrateNum),
                    new BigDecimal(100), SCALE));
        }
        return result;
    }
    private  void sendPositionMethod(CfetsEtribBondInfo003Dto entity,boolean isSubmit,String confirmOk){
        List<CfetsEtrIbPosPortfolioRequest> ptlList=new ArrayList<>();
        for (CfetsEtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
            //调用头寸
            CfetsEtrIbPosPortfolioRequest posport=new CfetsEtrIbPosPortfolioRequest();
            posport.setPortfolioId(dto.getPortfolioId());
            posport.setEtrId(dto.getEntrustId());
            if(StringUtil.isNotEmpty(dto.getPortfolioId())){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                String cashAcctId = this.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    posport.setCashAcctId(cashAcctId); // 托管户
                }
            }
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(entity.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", entity.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    posport.setSecAcctId(secAcctId); // 证券账户
                }
            }
            posport.setSecAmount(dto.getEntrustNum());
            posport.setCashAmount(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getEntrustNum()),
                    new BigDecimal(100), 2));
            if(!StringUtil.isEmptyList(dto.getBussCombDistribtList())){
                posport.setInvAim(dto.getBussCombDistribtList().get(0).getBusinessModel());
            }
            posport.setIntegrateSecAmount(dto.getEntrustIntegrateNum());
            posport.setOwnSecAmount(dto.getEntrustOwnNum());
            ptlList.add(posport);
        }
        //调用头寸
        CfetsEtrIbPositionRequest req=new CfetsEtrIbPositionRequest();
        req.setInterfaceType("Bond001");
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
        req.setTotalTradeId(entity.getTotalEtrustId());
        req.setWorkFlowId(entity.getWorkflowId());
        req.setFlowStatus(DictCodeListExt3.流程节点_流程提交.getValue());
        req.setModelType(DictCodeListExt1.子系统代码_委托.getValue());
        String balanceCheck="Y";
        if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                || StringUtil.isNotEmpty(confirmOk)) {
            balanceCheck = DictCodeList.是否_否.getValue();
        }
        req.setBalanceCheck(balanceCheck);
        req.setBusiType(DictCodeListExt3.头寸业务类型_现券交易.getValue());
        if(StringUtil.isNotEmpty(entity.getPs())
                && StringUtil.equals(entity.getPs(),BussinessDictCodeList.交易方向_行权回售.getValue())){
            req.setBusiType(DictCodeListExt3.头寸业务类型_债券行权交易.getValue()); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
        }
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        req.setSettleSpeed(entity.getSettleSpeed());
        req.setBusiAct(DictCodeListExt3.业务动作_投资指令.getValue());
        req.setPs(entity.getPs());
        if(isSubmit){
            req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setSystemAct(entity.getSystemAct());
        }else{
            req.setDecisionAct(DictCodeListExt3.系统动作_修改.getValue());
            req.setSystemAct(entity.getSystemAct());
            req.setbTotalTradeId(entity.getTotalEtrustId());
        }
        req.setCdate(entity.getEtrDate());
        req.setCcy(entity.getCcy());
        req.setFinprodId(entity.getFinprodId());
        req.setPtlList(ptlList);
        PositionHttpRequest.send(req);
    }
}
