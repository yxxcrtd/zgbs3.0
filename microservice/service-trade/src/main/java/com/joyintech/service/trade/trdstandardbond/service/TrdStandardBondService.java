/*
 * 文件名：TrdRepoTradeService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：陈志力
 * 修改时间：2019年1月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.trade.trdstandardbond.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.trdstandardbond.dao.TrdStandardBondMapper;
import com.joyintech.service.trade.trdstandardbond.dto.TrdStandardBondInDto;
import com.joyintech.service.trade.trdstandardbond.dto.TrdStandardBondOutDto;
import com.joyintech.service.trade.trdstandardbond.dto.TrdStandardBondSearchDto;
import com.joyintech.tams.common.base.dao.TrdStasecFlowDetailMapper;
import com.joyintech.tams.common.base.dao.TrdStasecFlowMapper;
import com.joyintech.tams.common.base.entity.TrdStasecFlow;
import com.joyintech.tams.common.base.entity.TrdStasecFlowDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tools.collectors.JoyinCollectors;


/**
 * 标准券微服务Service
 */
@Service
public class TrdStandardBondService extends BaseService<TrdStandardBondSearchDto> {

    @Autowired
    private TrdStandardBondMapper trdStandardBondMapper;

    @Autowired
    private BaseGetBussinessId baseGetBussinessId;

    @Autowired
    private TrdStasecFlowMapper trdStasecFlowMapper;

    @Autowired
    private TrdStasecFlowDetailMapper trdStasecFlowDetailMapper;

    @Autowired
    private IPlatformDate platformDateImpl;
    
    @Autowired(required=false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    /**
     * 组合可用持仓和实际持仓查询更新组件接口
     */
    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl;

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoServiceImpl;
    /**
     * 标准券查询画面，查询标准券信息列表
     *
     * @param page   page
     * @param length length
     * @param pageParam  参数
     * @return Page<TrdStandardBondDto>
     * @throws Exception 异常
     * @see
     * @since 1.0selectForPage
     */
    public Page<TrdStandardBondSearchDto> selectSearchDataList(int page, int length,
                                                               Map<String, Object> pageParam)
    {
        this.setParam(pageParam);
        Page<TrdStandardBondSearchDto> pageResult = this.queryForPage(page, length,
                () -> this.trdStandardBondMapper.selectSearchDataList(pageParam));
        pageParam.put("FINPROD_TYPE_TG_F02",DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        List<TrdStandardBondSearchDto> resultList = this.trdStandardBondMapper.selectSearchDataList(pageParam);
        // 质押券总额度合计
        BigDecimal inTotalPrinAmtSum = BigDecimal.ZERO;
        // 标准券额度合计
        BigDecimal stdrdPrinAmtSum = BigDecimal.ZERO;
        // 已质押标准券总额度合计
        BigDecimal pledgeStdrdPrinAmtSum = BigDecimal.ZERO;
        // 可使用标准券总额度合计
        BigDecimal avaStrdPrinAmtSum = BigDecimal.ZERO;
        for (TrdStandardBondSearchDto dto : resultList) {
            inTotalPrinAmtSum = ArithmeticUtil.doAdd(inTotalPrinAmtSum, dto.getInTotalPrinAmt());
            stdrdPrinAmtSum = ArithmeticUtil.doAdd(stdrdPrinAmtSum, dto.getStdrdPrinAmt());
        }
        for (TrdStandardBondSearchDto dto : resultList) {
            BigDecimal pledgeStdrdPrinAmt = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(dto.getInTotalPrinAmt(), dto.getPledgePrinAmt()),
                    inTotalPrinAmtSum, 10, RoundingMode.HALF_UP);
            pledgeStdrdPrinAmtSum = ArithmeticUtil.doAdd(pledgeStdrdPrinAmtSum, pledgeStdrdPrinAmt);
            avaStrdPrinAmtSum = ArithmeticUtil.doAdd(avaStrdPrinAmtSum,
                    ArithmeticUtil.subtract(dto.getStdrdPrinAmt(), pledgeStdrdPrinAmt));
        }
        for (TrdStandardBondSearchDto dto : pageResult.getRoot()) {
            dto.setInTotalPrinAmtSum(inTotalPrinAmtSum);
            dto.setStdrdPrinAmtSum(stdrdPrinAmtSum);
            dto.setPledgeStdrdPrinAmtSum(pledgeStdrdPrinAmtSum);
            dto.setAvaStrdPrinAmtSum(avaStrdPrinAmtSum);
        }
        return pageResult;
    }

    /**
     * 检索参数设置：
     * 标准券查询画面，查询标准券信息列表
     * 质押券入库画面，查询可质押债券列表
     * 质押券出库画面，查询待出库信息
     *
     * @param pageParam
     */
    private void setParam(Map<String, Object> pageParam) {
        pageParam.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        pageParam.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        pageParam.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());

        pageParam.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
        pageParam.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
        pageParam.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
        pageParam.put("POSITION_TYPE_05", DictCodeList.持仓类型_融入.getValue());
        pageParam.put("POSITION_TYPE_06", DictCodeList.持仓类型_融出.getValue());
        pageParam.put("TARGET_BACK_TYPE_H01", DictCodeList.回购类型_质押式协议回购.getValue());
        pageParam.put("TARGET_BACK_TYPE_H02", DictCodeList.回购类型_质押式竞价回购.getValue());
        pageParam.put("LIBRARY_DIRECTION_01", DictCodeList.出入库方向_入库.getValue());
        pageParam.put("LIBRARY_DIRECTION_02", DictCodeList.出入库方向_出库.getValue());

        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());

        pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
    }

    /**
     * 根据组合代码查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getSecManageAcctId(Map<String, Object> param) {
        param.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        return this.queryForSuggest(() -> this.trdStandardBondMapper.getSecManageAcctId(param),
                null);
    }

    /**
     * 查询债券代码：下拉框
     *
     * @param param 画面参数
     * @return 债券代码：下拉框
     */
    public List<SuggestDto> getBondCode(Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return this.queryForSuggest(() -> this.trdStandardBondMapper.getBondCode(param),
                null);
    }

    /**
     * 质押券入库画面，查询可质押债券列表
     *
     * @param pageParam 参数
     * @return List<TrdStandardBondDto>
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<TrdStandardBondInDto> selectAvaPledgeBondList(Map<String, Object> pageParam)
    {
        this.setParam(pageParam);
        return this.trdStandardBondMapper.selectAvaPledgeBondList(pageParam);
    }

    /**
     * 质押券出库画面，查询待出库信息
     *
     * @param pageParam 参数
     * @return List<TrdStandardBondDto>
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<TrdStandardBondOutDto> selectOutListData(Map<String, Object> pageParam) {
        this.setParam(pageParam);

        List<TrdStandardBondOutDto> resultList = this.trdStandardBondMapper.selectOutListData(pageParam);
        // 竞价回购入出库合计券面总额合计
        BigDecimal bidInOutPrinAmtSum = BigDecimal.ZERO;
        // 标准券额度合计
        BigDecimal inOutStdrdPrinAmtSum = BigDecimal.ZERO;
        // 已质押标准券总额度合计
        BigDecimal pledgeStdrdPrinAmtSum = BigDecimal.ZERO;
        // 可使用标准券总额度合计
        BigDecimal avaStrdPrinAmtSum = BigDecimal.ZERO;
        for (TrdStandardBondOutDto dto : resultList) {
            bidInOutPrinAmtSum = ArithmeticUtil.doAdd(bidInOutPrinAmtSum, dto.getBidInOutPrinAmt());
            inOutStdrdPrinAmtSum = ArithmeticUtil.doAdd(inOutStdrdPrinAmtSum, dto.getInOutStdrdPrinAmt());
        }
        for (TrdStandardBondOutDto dto : resultList) {
            // 已质押标准券总额度
            BigDecimal pledgeStdrdPrinAmt = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(dto.getInOutStdrdPrinAmt(), dto.getPledgePrinAmt()),
                    inOutStdrdPrinAmtSum, 10, RoundingMode.HALF_UP);
            // 可使用标准券额度
            BigDecimal avaStrdPrinAmt = ArithmeticUtil.subtract(dto.getInOutStdrdPrinAmt(), pledgeStdrdPrinAmt);
            // 已竞价质押券面总额
            BigDecimal pledgePrinAmt = ArithmeticUtil.divide(pledgeStdrdPrinAmt,
                    dto.getDiscountRate(), 10, RoundingMode.HALF_UP);
            // 可出库券面总额
            BigDecimal avaOutPrinAmt = ArithmeticUtil.subtract(dto.getBidInOutPrinAmt(), pledgePrinAmt);
            // 根据需求确认结果，可出库券面总额直接设置为入出库合计，保存时校验出库后留存标准券是否够用，由用户手动调配各债券出库份额
            avaOutPrinAmt = dto.getBidInOutPrinAmt();
            // 可使用标准券总额度
            BigDecimal outStrdPrinAmt = ArithmeticUtil.divide
                    (ArithmeticUtil.multiply(avaOutPrinAmt, dto.getDiscountRate()),
                            new BigDecimal(100), 10, RoundingMode.HALF_UP);

            dto.setAvaOutPrinAmt(avaOutPrinAmt);
            dto.setAvaOutPrinAmtInit(avaOutPrinAmt);
            dto.setOutPrinAmt(avaOutPrinAmt);
            dto.setOutStrdPrinAmt(outStrdPrinAmt);
            dto.setPledgeStdrdPrinAmt(pledgeStdrdPrinAmt);
            dto.setAvaStrdPrinAmt(avaStrdPrinAmt);

            pledgeStdrdPrinAmtSum = ArithmeticUtil.doAdd(pledgeStdrdPrinAmtSum, pledgeStdrdPrinAmt);
            avaStrdPrinAmtSum = ArithmeticUtil.doAdd(avaStrdPrinAmtSum, avaStrdPrinAmt);
        }
        for (TrdStandardBondOutDto dto : resultList) {
            dto.setInOutStdrdPrinAmtSum(inOutStdrdPrinAmtSum);
            dto.setPledgeStdrdPrinAmtSum(pledgeStdrdPrinAmtSum);
            dto.setAvaStrdPrinAmtSum(avaStrdPrinAmtSum);
        }
        return resultList;
    }

    /**
     * 质押券入库画面，保存处理
     *
     * @param dto 参数
     * @return saveCount
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Long inSave(TrdStandardBondInDto dto) throws Exception {
        Long saveCount = 0L;
        Date sysDate = platformDateImpl.getSysDate();

        if (!StringUtil.isEmptyList(dto.getPledgeBondList())) {
            // 可质押券信息（含投资目的）
            Map<String, List<TrdStandardBondInDto>> avaMap = this.getAvaWithInvAim(dto)
                    .stream().collect(JoyinCollectors.groupingBy(x -> x.getPortfolioId() + "," + x.getSecAcctId() + "," + x.getBondCode()));
            avaMap.entrySet().stream().forEach(entry -> {
                Collections.sort(entry.getValue(), new Comparator<TrdStandardBondInDto>() {
                    @Override
                    public int compare(TrdStandardBondInDto o1, TrdStandardBondInDto o2) {
                        return o1.getInvAimSort().compareTo(o2.getInvAimSort());
                    }
                });
            });
            List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
            List<EtrSecPositionFlowDto> etrCashList = new ArrayList<EtrSecPositionFlowDto>();
            PtlSecPositionFlowDto resultDto;
            TrdStasecFlowDetail tsfd;

            int tradeIdIndex = 0;
            Map<String, List<TrdStandardBondInDto>> dtoMap = dto.getPledgeBondList().stream().collect(JoyinCollectors.groupingBy(x -> x.getPortfolioId() + "," + x.getSecAcctId()));
            for (Map.Entry<String, List<TrdStandardBondInDto>> entry : dtoMap.entrySet()) {
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setSeqNo("9999");
                String seqNo = baseGetBussinessId.buildBussinessCode(parameter);
                tradeIdIndex++;
                TrdStasecFlow tsf = new TrdStasecFlow();
                tsf.setSeqno(seqNo);
                tsf.setPortfolioId(entry.getValue().get(0).getPortfolioId());
                tsf.setCdate(sysDate);
                tsf.setPs(DictCodeList.出入库方向_入库.getValue());
                tsf.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                tsf.setRemark("质押券入库");

                saveCount += trdStasecFlowMapper.insert(tsf);

                for (TrdStandardBondInDto tsb : entry.getValue()) {
                    tsfd = new TrdStasecFlowDetail();
                    tsfd.setSeqno(seqNo);
                    tsfd.setFinprodId(tsb.getFinProdId());
                    tsfd.setBranch(dto.getBranch());
                    tsfd.setAmt(ArithmeticUtil.divide(tsb.getPledgePrinAmt(), new BigDecimal("100"), 4));
                    saveCount += trdStasecFlowDetailMapper.insert(tsfd);
                    // 标准券出入库头寸处理
                    EtrSecPositionFlowDto ret = new EtrSecPositionFlowDto();
                    ret.setSeqno(IDGenerator.generate());
                    String secAcctId = entry.getValue().get(0).getSecAcctId() ;
                    if (StringUtil.isEmpty(secAcctId)) {
                        secAcctId = "999999";
                    }
                    //设置 证券管理账户/通道代码，无通道无证券管理账户的存999999
                    ret.setSecAcctId(secAcctId);
                    // 投资目的
                    ret.setInvAim(String.valueOf(tsb.getInvAimSort()));
//                    ret.setTradeId(inList.get(0).getSecAcctId());
                    ret.setTradeId(seqNo);
                    ret.setFinprodId(tsb.getFinProdId());
                    ret.setPortfolioId(tsb.getPortfolioId());
                    ret.setBranch(0l);
                    ret.setCdate(platformDateImpl.getSysDate());
                    ret.setCcy(tsb.getCcy());
                    ret.setAmount(ArithmeticUtil.divide(tsb.getPledgePrinAmt(), new BigDecimal("100"), 4));
                    etrCashList.add(ret);
                    // 待分配投资目的的质押券面总额
                    BigDecimal pledgePrin = tsb.getPledgePrinAmt();
                    if (avaMap.get(tsb.getPortfolioId() + "," + tsb.getSecAcctId() + "," + tsb.getBondCode()) == null) {
                        continue;
                    }
                    for (TrdStandardBondInDto avaBond : avaMap.get(tsb.getPortfolioId() + "," + tsb.getSecAcctId() + "," + tsb.getBondCode())) {
                        resultDto = new PtlSecPositionFlowDto();

                        // 当前投资目的可分配券面总额
                        BigDecimal avaPrin = avaBond.getAvaPledgePrinAmt();
                        // 当前投资目的决定分配的券面总额
                        BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                        // 组合代码 = 入库列表.组合代码
                        resultDto.setPortfolioId(tsb.getPortfolioId());
                        // 证券管理账户/通道代码 = nvl（nvl（入库列表.证券托管账户，入库列表.通道代码），999999）
                        resultDto.setSecAcctId(StringUtil.isEmpty(tsb.getSecAcctId()) ? "999999" : tsb.getSecAcctId());
                        // 金融产品代码 = 入库列表.金融产品代码
                        resultDto.setFinprodId(tsb.getFinProdId());
                        // 分支序号 = 系统固定参数，默认为0
                        resultDto.setBranch(dto.getBranch());
                        // 持仓类型
                        resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                        // 日期 = 平台日
                        resultDto.setCdate(sysDate);
                        // 数量 = 入库列表.质押券面总额/100
                        resultDto.setAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                        // 币种 = 入库列表.币种
                        resultDto.setCcy(tsb.getCcy());
                        // 交易编号 = 入出库编号
                        resultDto.setTradeId(seqNo);
                        // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                        resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                        if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
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
            if (!StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
                PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
                ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
                if (ptlPositionHoldDto != null) {
                    if (ptlPositionHoldDto.getPtlSecPositionFlows() != null
                            && !ptlPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
                        //入库时已经往持仓流水表中插入了持仓数据，但是seqNo与出入库详细表中的seqNo不同，所以插入一条seqNo相同的，并同时插入一条金额相反的seqNo不同的
                        List<PtlSecPositionFlowDto> negateFlows = new ArrayList<PtlSecPositionFlowDto>();
                        for (PtlSecPositionFlowDto ptlFlow :ptlPositionHoldDto.getPtlSecPositionFlows()){
                            //生成金额相反，seqNo不同的数据
                            PtlSecPositionFlowDto negateFlow = new PtlSecPositionFlowDto();
                            BeanUtil.copyProperties(negateFlow,ptlFlow);
                            negateFlow.setAmount(negateFlow.getAmount().negate());
                            negateFlow.setTradeId(IDGenerator.generate());
                            negateFlows.add(negateFlow);
                        }
                        if(negateFlows.size()>0){
                            ptlPositionHoldDto.getPtlSecPositionFlows().addAll(negateFlows);
                        }  
                        for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
                            basePtlSecPositionInfoServiceImpl.inertSecPositionFlow(ptlSecPositionFlow);
                        }
                    }
                    if (ptlPositionHoldDto.getPtlCashPositionFlows() != null
                            && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
                        for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
                            basePtlCashPositionInfoServiceImpl.insertCashPositionFlow(ptlCashPositionFlow);
                        }
                    }
                }
            }
            // 标准券处理 委托那边统计持仓时，用的就是出入库的表 + 标准券的指令持仓，出入库管理已经影响了出入库的表，所以不能再影响标准券的指令持仓，否则重复计算了，这个注释掉 by qiangchengxi 20200302
            //入库交易提交时，指令可用持仓减少，入库交易生效时生成一笔反向的抵消掉
            if (baseEtrCashPositionCommService != null && etrCashList.size() > 0) {
                for (EtrSecPositionFlowDto ppf : etrCashList){
                    ppf.setSeqno(IDGenerator.generate());
                    ppf.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                }
//                baseEtrCashPositionCommService.StandardBondExecute(etrCashList,DictCodeList.出入库方向_入库);
                EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
                // 持仓流水
                etrPositionHoldDto.setPtlSecPositionFlows(etrCashList);
                EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
            }
        }
        return saveCount;
    }

    /**
     * 质押券入库画面，保存处理
     *
     * @param dto 参数
     * @return saveCount
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Long inSubmit(TrdStandardBondInDto dto) throws Exception {
        Long saveCount = 0L;
        Date sysDate = platformDateImpl.getSysDate();

        if (!StringUtil.isEmptyList(dto.getPledgeBondList())) {
            // 可质押券信息（含投资目的）
            Map<String, List<TrdStandardBondInDto>> avaMap = this.getAvaWithInvAim(dto)
                    .stream().collect(JoyinCollectors.groupingBy(x -> x.getPortfolioId() + "," + x.getSecAcctId() + "," + x.getBondCode()));
            avaMap.entrySet().stream().forEach(entry -> {
                Collections.sort(entry.getValue(), new Comparator<TrdStandardBondInDto>() {
                    @Override
                    public int compare(TrdStandardBondInDto o1, TrdStandardBondInDto o2) {
                        return o1.getInvAimSort().compareTo(o2.getInvAimSort());
                    }
                });
            });
            List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
            List<EtrSecPositionFlowDto> etrCashList = new ArrayList<EtrSecPositionFlowDto>();
            PtlSecPositionFlowDto resultDto;
//            TrdStasecFlowDetail tsfd;

            Map<String, List<TrdStandardBondInDto>> dtoMap = dto.getPledgeBondList().stream().collect(JoyinCollectors.groupingBy(x -> x.getPortfolioId() + "," + x.getSecAcctId()));
            for (Map.Entry<String, List<TrdStandardBondInDto>> entry : dtoMap.entrySet()) {
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setSeqNo("9999");
                String seqNo = baseGetBussinessId.buildBussinessCode(parameter);
                for (TrdStandardBondInDto tsb : entry.getValue()) {
                    // 标准券出入库头寸处理
                    EtrSecPositionFlowDto ret = new EtrSecPositionFlowDto();
                    ret.setSeqno(IDGenerator.generate());
                    String secAcctId = entry.getValue().get(0).getSecAcctId() ;
                    if (StringUtil.isEmpty(secAcctId)) {
                        secAcctId = "999999";
                    }
                    //设置 证券管理账户/通道代码，无通道无证券管理账户的存999999
                    ret.setSecAcctId(secAcctId);
                    // 投资目的
                    ret.setInvAim(String.valueOf(tsb.getInvAimSort()));
                    ret.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
//                    ret.setFinprodId(XN_STANDARD_BOND_PRODID);
                    ret.setSeqno(IDGenerator.generate());
                    ret.setTradeId(seqNo);
                    ret.setFinprodId(tsb.getFinProdId());
                    ret.setPortfolioId(tsb.getPortfolioId());
                    ret.setBranch(0l);
                    ret.setCdate(platformDateImpl.getSysDate());
                    ret.setCcy(tsb.getCcy());
                    ret.setAmount(ArithmeticUtil.divide(tsb.getPledgePrinAmt(), new BigDecimal("100"), 4).negate());
                    //回退时金额取反
                    if (dto.getIsBack()){
                        ret.setAmount(ret.getAmount().negate());    
                    }
                    etrCashList.add(ret);
                    // 待分配投资目的的质押券面总额
                    BigDecimal pledgePrin = tsb.getPledgePrinAmt();
                    if (avaMap.get(tsb.getPortfolioId() + "," + tsb.getSecAcctId() + "," + tsb.getBondCode()) == null) {
                        continue;
                    }
                    for (TrdStandardBondInDto avaBond : avaMap.get(tsb.getPortfolioId() + "," + tsb.getSecAcctId() + "," + tsb.getBondCode())) {
                        resultDto = new PtlSecPositionFlowDto();

                        // 当前投资目的可分配券面总额
                        BigDecimal avaPrin = avaBond.getAvaPledgePrinAmt();
                        // 当前投资目的决定分配的券面总额
                        BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                        // 组合代码 = 入库列表.组合代码
                        resultDto.setPortfolioId(tsb.getPortfolioId());
                        // 证券管理账户/通道代码 = nvl（nvl（入库列表.证券托管账户，入库列表.通道代码），999999）
                        resultDto.setSecAcctId(StringUtil.isEmpty(tsb.getSecAcctId()) ? "999999" : tsb.getSecAcctId());
                        // 金融产品代码 = 入库列表.金融产品代码
                        resultDto.setFinprodId(tsb.getFinProdId());
                        // 分支序号 = 系统固定参数，默认为0
                        resultDto.setBranch(dto.getBranch());
                        // 持仓类型
                        resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                        // 日期 = 平台日
                        resultDto.setCdate(sysDate);
                        // 数量 = 入库列表.质押券面总额/100
                        resultDto.setAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                        // 币种 = 入库列表.币种
                        resultDto.setCcy(tsb.getCcy());
                        // 交易编号 = 入出库编号
                        resultDto.setTradeId(seqNo);
                        // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                        resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());
                        //回退时金额取反
                        if (dto.getIsBack()){
                            resultDto.setAmount(resultDto.getAmount().negate());    
                        }
                        if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
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
            if (!StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
                PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
                ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
                if (ptlPositionHoldDto != null) {
                    if (ptlPositionHoldDto.getPtlSecPositionFlows() != null
                            && !ptlPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
                        for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
                            basePtlSecPositionInfoServiceImpl.inertSecPositionFlow(ptlSecPositionFlow);
                        }
                    }
                }
            }
            //入库交易提交时，指令可用持仓减少，入库交易生效时生成一笔反向的抵消掉
            if (baseEtrCashPositionCommService != null && etrCashList.size() > 0) {
//                baseEtrCashPositionCommService.StandardBondExecute(etrCashList,DictCodeList.出入库方向_入库);
                EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
                // 持仓流水
                etrPositionHoldDto.setPtlSecPositionFlows(etrCashList);
                EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
            }
        }
        return saveCount;
    }

    /**
     * 质押券出库画面，保存处理
     *
     * @param dto 参数
     * @return saveCount
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Long outSave(TrdStandardBondOutDto dto) {
        Long saveCount = 0L;
        Date sysDate = platformDateImpl.getSysDate();

        if (!StringUtil.isEmptyList(dto.getOutBondList())) {
            // 可出库质押券信息（含投资目的）
            Map<String, List<TrdStandardBondOutDto>> avaMap = this.getAvaOutWithInvAim(dto).stream().collect(JoyinCollectors.groupingBy(x -> x.getPortfolioId() + "," + x.getSecAcctId() + "," + x.getBondCode()));
            avaMap.entrySet().stream().forEach(entry -> {
                Collections.sort(entry.getValue(), new Comparator<TrdStandardBondOutDto>() {
                    @Override
                    public int compare(TrdStandardBondOutDto o1, TrdStandardBondOutDto o2) {
                        return o2.getInvAimSort().compareTo(o1.getInvAimSort());
                    }
                });
            });

            List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
            List<EtrSecPositionFlowDto> etrCashList = new ArrayList<EtrSecPositionFlowDto>();
            PtlSecPositionFlowDto resultDto;
            TrdStasecFlowDetail tsfd;

            Map<String, List<TrdStandardBondOutDto>> dtoMap = dto.getOutBondList().stream().collect(JoyinCollectors.groupingBy(x -> x.getPortfolioId() + "," + x.getSecAcctId()));
            for (Map.Entry<String, List<TrdStandardBondOutDto>> entry : dtoMap.entrySet()) {
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setSeqNo("9999");
                String seqNo = baseGetBussinessId.buildBussinessCode(parameter);

                TrdStasecFlow tsf = new TrdStasecFlow();
                tsf.setSeqno(seqNo);
                tsf.setPortfolioId(entry.getValue().get(0).getPortfolioId());
                tsf.setCdate(sysDate);
                tsf.setPs(DictCodeList.出入库方向_出库.getValue());
                tsf.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                tsf.setRemark("质押券出库");

                saveCount += trdStasecFlowMapper.insert(tsf);

                for (TrdStandardBondOutDto tsb : entry.getValue()) {
                    tsfd = new TrdStasecFlowDetail();
                    tsfd.setSeqno(seqNo);
                    tsfd.setFinprodId(tsb.getFinProdId());
                    tsfd.setBranch(dto.getBranch());
                    tsfd.setAmt(ArithmeticUtil.divide(tsb.getOutPrinAmt(), new BigDecimal("100"), 4));
                    saveCount += trdStasecFlowDetailMapper.insert(tsfd);
                    // 标准券出入库头寸处理
                    EtrSecPositionFlowDto ret = new EtrSecPositionFlowDto();
                    ret.setSeqno(IDGenerator.generate());
                    String secAcctId = entry.getValue().get(0).getSecAcctId() ;
                    if (StringUtil.isEmpty(secAcctId)) {
                        secAcctId = "999999";
                    }
                    //设置 证券管理账户/通道代码，无通道无证券管理账户的存999999
                    ret.setSecAcctId(secAcctId);
                    // 投资目的
                    ret.setInvAim(String.valueOf(tsb.getInvAimSort()));
//                    ret.setTradeId(inList.get(0).getSecAcctId());
                    ret.setTradeId(seqNo);
                    ret.setCcy(tsb.getCcy());
                    ret.setFinprodId(tsb.getFinProdId());
                    ret.setPortfolioId(entry.getValue().get(0).getPortfolioId());
                    ret.setBranch(0l);
                    ret.setCdate(platformDateImpl.getSysDate());
                    ret.setAmount(ArithmeticUtil.divide(tsb.getPledgePrinAmt(), new BigDecimal("100"), 4));
                    etrCashList.add(ret);
                    // 待分配投资目的的出库券面总额
                    BigDecimal outPrinAmt = tsb.getOutPrinAmt();

                    for (TrdStandardBondOutDto avaBond : avaMap.get(tsb.getPortfolioId() + "," + tsb.getSecAcctId() + "," + tsb.getBondCode())) {
                        resultDto = new PtlSecPositionFlowDto();
                        // 当前组合代码、证券账号下，可出库券面总额
//                        BigDecimal bidInOutPrinAmt = tsb.getBidInOutPrinAmt();
                        BigDecimal bidInOutPrinAmt = avaBond.getBidInOutPrinAmt();
                        // 当前投资目的决定分配的出库券面总额
                        BigDecimal prin = outPrinAmt.compareTo(bidInOutPrinAmt) > 0 ? bidInOutPrinAmt : outPrinAmt;

                        // 组合代码 = 入库列表.组合代码
                        resultDto.setPortfolioId(tsb.getPortfolioId());
                        // 证券管理账户/通道代码 = nvl（nvl（入库列表.证券托管账户，入库列表.通道代码），999999）
                        resultDto.setSecAcctId(StringUtil.isEmpty(tsb.getSecAcctId()) ? "999999" : tsb.getSecAcctId());
                        // 金融产品代码 = 入库列表.金融产品代码
                        resultDto.setFinprodId(tsb.getFinProdId());
                        // 分支序号 = 系统固定参数，默认为0
                        resultDto.setBranch(dto.getBranch());
                        // 持仓类型
                        resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                        // 日期 = 平台日
                        resultDto.setCdate(sysDate);
                        // 数量 = 出库列表.出库券面总额/100
                        resultDto.setAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4).negate());
                        // 币种 = 出库列表.币种
                        resultDto.setCcy(tsb.getCcy());
                        // 交易编号 = 入出库编号
                        resultDto.setTradeId(seqNo);
                        // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的反向顺序，依次释放对应投资目的债券可用持仓
                        resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                        if (bidInOutPrinAmt.compareTo(BigDecimal.ZERO) > 0) {
                            lstPtlSecPositionFlowDto.add(resultDto);
                        }
                        // 分配完毕后,停止可质押券信息（含投资目的）的循环
                        outPrinAmt = outPrinAmt.subtract(prin);
                        if (outPrinAmt.compareTo(BigDecimal.ZERO) <= 0) {
                            break;
                        }
                    }
                }
            }
            if (!StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
                PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
                ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
                restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                        ptlPositionHoldDto, Boolean.class);
            }
            // 标准券处理 委托那边统计持仓时，用的就是出入库的表 + 标准券的指令持仓，出入库管理已经影响了出入库的表，所以不能再影响标准券的指令持仓，否则重复计算了，这个注释掉 by qiangchengxi 20200302
//            if (baseEtrCashPositionCommService != null && etrCashList.size() > 0) {
//                baseEtrCashPositionCommService.StandardBondExecute(etrCashList,DictCodeList.出入库方向_出库);
//            }
        }
        return saveCount;
    }

    /**
     * 根据质押债券入库画面.质押债券，查询对应可用质押券信息
     *
     * @param dto dto
     * @return List<TrdStandardBondInDto>
     */
    private List<TrdStandardBondInDto> getAvaWithInvAim(TrdStandardBondInDto dto) {
        Map<String, Object> param = new HashMap<>();

        param.put("BRANCH_0", dto.getBranch());
        param.put("isPreciseSearch", true);
        param.put("isWithInvAim", true);

        List<String> portfolioIdList = new ArrayList<>();
        List<String> bondCodeList = new ArrayList<>();
        List<String> tradePlaceList = new ArrayList<>();
        List<String> secAcctIdList = new ArrayList<>();

        for (TrdStandardBondInDto tsbi : dto.getPledgeBondList()) {
            portfolioIdList.add(tsbi.getPortfolioId());
            bondCodeList.add(tsbi.getFinProdId());
            tradePlaceList.add(tsbi.getTradePlace());
            secAcctIdList.add(tsbi.getSecAcctId());
        }
        // 组合代码
        param.put("portfolioIdList", portfolioIdList);
        // 债券对应金融产品代码
        param.put("bondCodeList", bondCodeList);
        // 交易场所
        param.put("tradePlaceList", tradePlaceList);
        // 证券账户
        param.put("secAcctIdList", secAcctIdList);

        param.put("searchDate", platformDateImpl.getSysDateStr());
        this.setParam(param);
        return this.trdStandardBondMapper.selectAvaPledgeBondList(param);
    }

    /**
     * 根据质押债券出库画面.出库债券列表，查询对应可出库质押券信息
     *
     * @param dto dto
     * @return List<TrdStandardBondOutDto>
     */
    private List<TrdStandardBondOutDto> getAvaOutWithInvAim(TrdStandardBondOutDto dto) {
        Map<String, Object> param = new HashMap<>();

        param.put("BRANCH_0", dto.getBranch());
        param.put("isPreciseSearch", true);
        param.put("isWithInvAim", true);

        List<String> portfolioIdList = new ArrayList<>();
        List<String> bondCodeList = new ArrayList<>();
        List<String> tradePlaceList = new ArrayList<>();
        List<String> secAcctIdList = new ArrayList<>();

        for (TrdStandardBondOutDto tsb : dto.getOutBondList()) {
            portfolioIdList.add(tsb.getPortfolioId());
            bondCodeList.add(tsb.getFinProdId());
            tradePlaceList.add(tsb.getTradePlace());
            secAcctIdList.add(tsb.getSecAcctId());
        }
        // 组合代码
        param.put("portfolioIdList", portfolioIdList);
        // 债券对应金融产品代码
        param.put("bondCodeList", bondCodeList);
        // 交易场所
        param.put("tradePlaceList", tradePlaceList);
        // 证券账户
        param.put("secAcctIdList", secAcctIdList);

        param.put("searchDate", platformDateImpl.getSysDateStr());
        this.setParam(param);
        return this.trdStandardBondMapper.selectOutListData(param);
    }

}
