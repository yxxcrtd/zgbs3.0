/*
 * 文件名：PtlFundAllocation001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2019年1月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.portfolio.ptlfundallocation.service;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.dto.PtlFundAllocation001Dto;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.dto.PtlFundAllocation002Dto;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.dto.PtlFundAllocationImportDto;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.tams.common.base.dao.PtlCashPositionFlowMapper;
import com.joyintech.tams.common.base.entity.PtlCashPosition;
import com.joyintech.tams.common.base.entity.PtlCashPositionFlow;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.dto.PtlFundAllocationDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 资金划拨Service
 * @author fanshuanghu
 * @version 1.0 2019年1月29日
 * @see PtlFundAllocation001Service
 * @since 1.0
 */
@Service("PtlFundAllocation001Service")
@Transactional(rollbackFor=Exception.class)
public class PtlFundAllocation001Service extends CommonExportService<PtlFundAllocationDto>
    implements BaseProcessService<PtlFundAllocationDto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(PtlFundAllocation001Service.class);

    /**
     * 业务主键生成类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBussinessId;
    /**
     * 基础Mapper代理Service
     */
    @Autowired(required=false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 查询组合
     * @return 组合集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioList(Map<String,Object> map){
        User user = SecurityUtil.getLoginUser();
        map.put("userId", user.getUserName());
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getPortfolioList", map, SelectOptionDto.class);
    }

    /**
     * 获取账号
     * @return 账号下拉框集合
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getAcct(Map<String,Object> map){
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getAcct", map, SuggestDto.class);
    }

    /**
     * 分页查询
     * @return Page<MstInterestRateQuotationInfoDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<PtlFundAllocationDto> findPage(Map<String,Object> map){
        if(map.get("portfolioId") != null && StringUtil.isNotEmpty((String)map.get("portfolioId"))){
            map.replace("portfolioId", Arrays.asList(map.get("portfolioId").toString().split(",")));
        }else {
            map.replace("portfolioId", null);
        }
        if(map.get("dataDateStart") == null || StringUtil.isEmpty((String)map.get("dataDateStart"))){
            map.replace("dataDateStart", null);
        }
//      String[] psArr = {DictCodeListExt1.指令类型_银证转账_证转银.getValue(),DictCodeListExt1.指令类型_银证转账_银转证.getValue()};
        String[] psArr = {DictCodeList.交易方向_银证转账.getValue(),
            DictCodeList.交易方向_证银转账.getValue()};
        List<String> ps = Arrays.asList(psArr);
        map.put("ps", ps);

        // 划款类型转交易方向
        String payType = (String)map.get("payType");
        if (StringUtil.equals(DictCodeListExt1.指令类型_银证转账_银转证.getValue(), payType)) {
            payType = DictCodeList.交易方向_银证转账.getValue();
        }
        else if (StringUtil.equals(DictCodeListExt1.指令类型_银证转账_证转银.getValue(), payType)) {
            payType = DictCodeList.交易方向_证银转账.getValue();
        }
        map.put("payType", payType);

        map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());

        map.put("userId", LoginUserUtil.getUserName());

        // 有效状态转交易状态
        String dataStatus = (String) map.get("dataStatus");
        if (StringUtil.equals(DictCodeListExt1.画面数据状态_有效.getValue(), dataStatus)) {
            map.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
        }
        else if (StringUtil.equals(DictCodeListExt1.画面数据状态_撤销.getValue(), dataStatus)) {
            map.put("tradeStatus", DictCodeListExt1.交易状态_撤销.getValue());
        }

        return restTemplate.postForPage(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/findPage", map, PtlFundAllocationDto.class);
    }

    public PtlFundAllocationDto setEntity(PtlFundAllocationDto entity, OperationType type){

        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(entity.getPortfolioId());
        ptlTrade.setBusiNo(entity.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
        entity.setPtlTrade(ptlTrade);

        PtlCashPositionFlow ptlCashPositionFlow = new PtlCashPositionFlow();
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setSeqNo("9999");
        String seqno = baseGetBussinessId !=null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate();
        ptlCashPositionFlow.setSeqno(seqno);
        ptlCashPositionFlow.setPortfolioId(entity.getPortfolioId());
        ptlCashPositionFlow.setAcctId(entity.getMyAcctNo());
        ptlCashPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
        ptlCashPositionFlow.setAmount(entity.getSettleAmt());
//        if (DictCodeListExt1.收支方向_支出.getValue().equals(entity.getPs())) {
        ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(entity.getSettleAmt(),new BigDecimal("-1")));
//        }
        if (OperationType.REVOKE == type || OperationType.DELETE == type) {
            ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(ptlCashPositionFlow.getAmount(),new BigDecimal("-1")));
        }
        ptlCashPositionFlow.setOrderId(entity.getOrderId());
        ptlCashPositionFlow.setCdate(entity.getSettleDate());
        ptlCashPositionFlow.setCcy(entity.getCcy());
        ptlCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
        entity.setPtlCashPositionFlow(ptlCashPositionFlow);


        PtlCashPosition ptlCashPosition = new PtlCashPosition();
        ptlCashPosition.setPortfolioId(entity.getPortfolioId());
        ptlCashPosition.setAcctId(entity.getMyAcctNo());
        ptlCashPosition.setCcy(entity.getCcy());
        ptlCashPosition.setCdate(entity.getSettleDate());
        ptlCashPosition.setHodingType(DictCodeList.持仓类型_实际.getValue());
        ptlCashPosition.setAmount(ArithmeticUtil.multiply(entity.getSettleAmt(),new BigDecimal("-1")));
        entity.setPtlCashPosition(ptlCashPosition);

        entity.setTradeId(entity.getTradeId());

        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(PtlFundAllocationDto entity, OperationType type) {
        // TODO Auto-generated method stub
        this.setEntity(entity, type);
        entity.setOperateType(type.toString());
        restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doComplete", entity, Integer.class);
    }

    @Override
    public void doDelete(String workflowId) {
        PtlFundAllocationDto entity = this.getBussinessEntityByWorkflowId(workflowId);
        if (StringUtil.isNotEmpty(entity.getOrderId())) {
            restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doDeleteNew", entity, Integer.class);
        }
    }

    @Override
    public void doInsert(PtlFundAllocationDto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        this.setEntity(entity, type);
        restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doInsert", entity, String.class);
    }

    @Override
    public void doUpdate(PtlFundAllocationDto entity){
        if(bussCommonMapperDelegateService != null){
            restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doDelete", entity.getOrderId(), Integer.class);
            doInsert(entity, OperationType.NEW);
        }
    }


    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
            "/com/joyintech/tams/invest/portfolio/ptlfundallocation/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PtlFundAllocationDto> selectPageData(Map<String, Object> selectParam) {
        return findPage(selectParam);
    }

    /**
     * 设置单元格值
     * <p>
     * 如果单元格值需要特殊处理，比如数值计算等，可以实现该方法
     * </p>
     *
     * @param beanName sheet对应的JavaBean
     * @param cell 单元格
     * @param propName 属性名
     * @param value 单元格的值
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if("settleDate".equals(propName)){
            cell.setCellValue(DateUtil.format((Date)value, "yyyy-MM-dd"));
        }
    }
    /* 新增导入功能 add by liuwei 20200220 start */
    /**
     * 导入
     * @param fileName
     */
    public Map<String,Object> importData(String fileName) throws FileNotFoundException {
        Map<String,Object> result = new HashMap<>();
        // 检查文件名是否为空
        List<ValidMessage> errorMessage = new ArrayList<ValidMessage>();
        // 保存的实体
        List<PtlFundAllocationImportDto> importList = new ArrayList<>();
        result.put("errorList",errorMessage);
        result.put("data",importList);
        if (StringUtil.isNotEmpty(fileName)) {
            ExcelUtil excelUtil = ExcelUtil.build(fileName,null);
            List<List<String>> dataList = excelUtil.readToList(0, 0, 0);
            if (!StringUtil.isEmptyList(dataList) && dataList.size() >1) {
                // 查询必填项是否输入
                List<ValidMessage> emptyCheck = checkEmpty(dataList);
                if (!StringUtil.isEmptyList(emptyCheck)) {
                    errorMessage.addAll(emptyCheck);
                    return result;
                }
                for (int i = 1; i <= dataList.size() - 1; i++) {
                    List<String> datas = dataList.get(i);
                    String portfolioName = queryProdInfo(datas.get(2));
                    // 组合名称
                    if (StringUtil.isEmpty(portfolioName)) {
                        errorMessage.add(new ValidMessage("第"+i+"行：组合代码("+datas.get(2)+")不存在或已到期，请查证后导入！"));
                        continue;
                    }
                    // 收/付款账户:校验产品是否真实关联了该账户
                    Map<String,Object> param = new HashMap<>();
                    param.put("portfolioId",datas.get(2));
                    param.put("counterAcctNo",datas.get(1));
                    param.put("ccy",datas.get(4));
                    String counterAcctNoName = checkAccount(param);
                    if(StringUtil.isEmpty(counterAcctNoName)){
//                        errorMessage.add(new ValidMessage("第"+i+"行：组合代码("+datas.get(2)+")未关联账户("+datas.get(1)+")，请查证后导入！"));
                        errorMessage.add(new ValidMessage(datas.get(1)+"未上挂"+datas.get(2)+"的【组合创建】，请添加账户！"));
                        continue;
                    }
                    //将读取的数据封装到实体类
                    PtlFundAllocationImportDto importDto = new PtlFundAllocationImportDto();
                    if(StringUtil.equals(datas.get(0),"银证转账")){
                        importDto.setPayType(BussinessDictCodeList.指令类型_银证转账_银转证.getValue());
                    }
                    else if(StringUtil.equals(datas.get(0),"证银转账")){
                        importDto.setPayType(BussinessDictCodeList.指令类型_银证转账_证转银.getValue());
                    }
                    importDto.setCounterAcctNo(datas.get(1));
                    importDto.setPortfolioId(datas.get(2));
                    importDto.setSettleAmt(new BigDecimal(datas.get(3)));
                    importDto.setCcy(datas.get(4));
                    importDto.setPortfolioName(portfolioName);
                    importDto.setDate(DateUtil.parse(DateUtil.getSysDate()));
                    importDto.setCounterAcctNoName(counterAcctNoName);
                    // 获取组合托管户以及账户余额
                    Map<String,Object> account = this.getAccountInfo(importDto);
                    if(StringUtil.isNotEmpty(account)){
                        // 组合托管户
                        importDto.setMyAcctNo((String) account.get("MY_ACCT_NO"));
                        // 托管户名称
                        importDto.setMyAcctNoName((String) account.get("MY_ACCT_NO_NAME"));
                        // 托管户余额
                        BigDecimal amount = new BigDecimal((String)account.get("AMOUNT"));
                        importDto.setAmount(amount);
                    }
                    importList.add(importDto);
                }
            } else {
                errorMessage.add(new ValidMessage("导入数据为空，请检查文件！"));
            }
        }
        return result;
    }

    /**
     * 校验必输项
     */
    private List<ValidMessage> checkEmpty(List<List<String>> dataList){
        List<ValidMessage> result = new ArrayList<>();
        String payType = dataList.get(1).get(0);
        String counterAcctNo = dataList.get(1).get(1);
        String ccy = dataList.get(1).get(4);
        for (int i = 1; i <= dataList.size() - 1; i++) {
            List<String> datas = dataList.get(i);
            //0:划款类型
            if(StringUtil.isEmpty(datas.get(0))){
                result.add(new ValidMessage("第" + i + "行：划款类型不能为空！"));
            }
            else if(i > 1 && !StringUtil.equals(datas.get(0),payType)){
                result.add(new ValidMessage("同一划款类型才能导入成功，请核对信息！"));
            }
            //1:收/付款账户
            if(StringUtil.isEmpty(datas.get(1))){
                result.add(new ValidMessage("第" + i + "行：收/付款账户不能为空！"));
            }
            else if(i > 1 && !StringUtil.equals(datas.get(1),counterAcctNo)){
                result.add(new ValidMessage("同一收/付款账户才能导入成功，请核对信息！"));
            }
            //2:组合代码
            if(StringUtil.isEmpty(datas.get(2))){
                result.add(new ValidMessage("第" + i + "行：组合代码不能为空！"));
            }
            //3:划拨金额
            if(StringUtil.isEmpty(datas.get(3))){
                result.add(new ValidMessage("第" + i + "行：划拨金额不能为空！"));
            }
            //4:币种
            if(StringUtil.isEmpty(datas.get(4))){
                result.add(new ValidMessage("第" + i + "行：币种不能为空！"));
            }
            else if(i > 1 && !StringUtil.equals(datas.get(4),ccy)){
                result.add(new ValidMessage("同一币种才能导入成功，请核对信息！"));
            }
        }
        return result;
    }

    /**
     * 查询对应组合代码是否存在以及组合名称
     */
    private String queryProdInfo(String portfolioId){
        return simpleRestTemplate.getForObject(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/queryProdInfo?portfolioId=" + portfolioId,
                String.class);
    }

    /**
     * 校验产品是否真实关联了该账户
     */
    private String checkAccount(Map<String,Object> param){
        return simpleRestTemplate.postForObject(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/checkAccount", param,
                String.class);
    }

    /**
     * 查询组合托管户以及对应现金余额
     */
    public Map<String,Object> getAccountInfo(PtlFundAllocationImportDto dto){
        Map<String,Object> param = new HashMap<>();
        param.put("portfolioId",dto.getPortfolioId());
        param.put("ccy",dto.getCcy());
        param.put("cdate",DateUtil.format(dto.getDate()));
        param.put("payType",dto.getPayType());
        param.put("counterAcctNo",dto.getCounterAcctNo());
        return simpleRestTemplate.postForObject(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getAccountInfo", param,
                Map.class);
    }

    /**
     * 处理实体类
     */
    public void doImportSave(List<PtlFundAllocationImportDto> list, PtlFundAllocation002Dto dto) throws WorkFlowException {
        //生成交易编号
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setCommonBusinessType1(BussinessDictCodeList.共同业务类型_资金划拨管理);
        dto.setTradeId(baseGetBussinessId !=null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate());
        //处理组合明细并生成对应划款指令
        List<PtlFundAllocation001Dto> ptlFundAllocation001Dtos = new ArrayList<>();
        BigDecimal settleAmt = BigDecimal.ZERO;
        List<BussTrdTransferOrderDto> orderDtos = new ArrayList<>();
        for (PtlFundAllocationImportDto ptlFundAllocationImportDto : list){
            PtlFundAllocation001Dto ptlFundAllocation001Dto = new PtlFundAllocation001Dto();
            ptlFundAllocation001Dto.setPortfolioId(ptlFundAllocationImportDto.getPortfolioId());
            dto.setPayType(ptlFundAllocationImportDto.getPayType());
            dto.setSettleDate(ptlFundAllocationImportDto.getDate());
            ptlFundAllocation001Dto.setAcctNo(ptlFundAllocationImportDto.getMyAcctNo());
            if(StringUtil.equals(ptlFundAllocationImportDto.getPayType(),BussinessDictCodeList.指令类型_银证转账_银转证.getValue())){
                dto.setCounterAcctNo(ptlFundAllocationImportDto.getCounterAcctNo());
            }
            else if(StringUtil.equals(ptlFundAllocationImportDto.getPayType(),BussinessDictCodeList.指令类型_银证转账_证转银.getValue())){
                dto.setMyAcctNo(ptlFundAllocationImportDto.getCounterAcctNo());
            }
            dto.setCcy(ptlFundAllocationImportDto.getCcy());
            ptlFundAllocation001Dto.setSettleAmt(ptlFundAllocationImportDto.getSettleAmt());
            ptlFundAllocation001Dto.setAmount(ptlFundAllocationImportDto.getAmount());
            ptlFundAllocation001Dtos.add(ptlFundAllocation001Dto);
            //生成对应划款指令
            orderDtos.add(generateOrder(ptlFundAllocationImportDto));
            settleAmt = ArithmeticUtil.doAdd(settleAmt,ptlFundAllocationImportDto.getSettleAmt());
        };
        dto.setPtlFundAllocation001Dtos(ptlFundAllocation001Dtos);
        dto.setSettleAmt(settleAmt);
        dto.setBussTrdTransferOrder(orderDtos);
        dto.setOperateType("save");
        //启动流程
        ProcessParameter<PtlFundAllocation002Dto> processParameter = new ProcessParameter<PtlFundAllocation002Dto>();
        WorkFlowParameter<PtlFundAllocation002Dto> params = new WorkFlowParameter<PtlFundAllocation002Dto>();
        params.setPageId("PtlFundAllocation003");
        params.setElementId("PtlFundAllocation003_btn_save");
        params.setBusinessCode(item -> item.getTradeId());
        params.setBusinessType(BussinessDictCodeList.共同业务类型_资金划拨管理.getValue());
        params.setComment(
                entity -> "资金划拨多组合管理,划拨金额:"+dto.getSettleAmt()+",交易日期:("+DateUtil.format(dto.getSettleDate())+")");
        params.setServiceName("PtlFundAllocation003Service");
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.NEW);
        try {
            WorkFlowUtil.startProcess(processParameter.getWorkFlowParameter(),"","",SecurityUtil.getLoginUser().getUserName(),OperationType.NEW,false,dto);
        }catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    private BussTrdTransferOrderDto generateOrder(PtlFundAllocationImportDto ptlFundAllocationImportDto) {
        BussSettleOrderParameterDto param = new BussSettleOrderParameterDto();
        param.setPortfolioId(ptlFundAllocationImportDto.getPortfolioId());
        TrdProductDeal deal = new TrdProductDeal();
        deal.setFinprodType2(BussinessDictCodeList.共同业务类型_资金划拨管理.getValue());
        deal.setSettleDate(ptlFundAllocationImportDto.getDate());
        deal.setTradeDate(ptlFundAllocationImportDto.getDate());
        deal.setPs(ptlFundAllocationImportDto.getPayType());
        deal.setCcy(ptlFundAllocationImportDto.getCcy());
        deal.setTradeAmt(ptlFundAllocationImportDto.getSettleAmt());
        param.setDeal(deal);
        BussTrdTransferOrderDto orderDto = bussTrdTransferOrderService.generateOrder(param);
        List<SelectOptionDto> payAcctNoList = new ArrayList<>();
        SelectOptionDto payAcctNo = new SelectOptionDto();
        payAcctNo.setId(ptlFundAllocationImportDto.getCounterAcctNo());
        payAcctNo.setText(ptlFundAllocationImportDto.getCounterAcctNoName());
        payAcctNoList.add(payAcctNo);
        List<SelectOptionDto> myAcctNoList = new ArrayList<>();
        SelectOptionDto myAcctNo = new SelectOptionDto();
        myAcctNo.setId(ptlFundAllocationImportDto.getMyAcctNo());
        myAcctNo.setText(ptlFundAllocationImportDto.getMyAcctNoName());
        myAcctNoList.add(myAcctNo);
        if(StringUtil.equals(ptlFundAllocationImportDto.getPayType(),BussinessDictCodeList.指令类型_银证转账_银转证.getValue())){
            orderDto.setPayAcctNoList(myAcctNoList);
            orderDto.setRecAcctNoList(payAcctNoList);
        }
        else {
            orderDto.setPayAcctNoList(payAcctNoList);
            orderDto.setRecAcctNoList(myAcctNoList);
        }
        orderDto.setPayAcctNo(orderDto.getPayAcctNoList().get(0).getId());
        orderDto.setPayAcctName(orderDto.getPayAcctNoList().get(0).getText());
        orderDto.setRecAcctNo(orderDto.getRecAcctNoList().get(0).getId());
        orderDto.setRecAcctName(orderDto.getRecAcctNoList().get(0).getText());
        return orderDto;
    }

    /**
     * 处理实体类
     */
    public PtlFundAllocation002Dto dealData(List<PtlFundAllocationImportDto> list,PtlFundAllocation002Dto dto) throws WorkFlowException {
        //处理组合明细并生成对应划款指令
        List<PtlFundAllocation001Dto> ptlFundAllocation001Dtos = new ArrayList<>();
        BigDecimal settleAmt = BigDecimal.ZERO;
        List<BussTrdTransferOrderDto> orderDtos = new ArrayList<>();
        for (PtlFundAllocationImportDto ptlFundAllocationImportDto : list){
            PtlFundAllocation001Dto ptlFundAllocation001Dto = new PtlFundAllocation001Dto();
            ptlFundAllocation001Dto.setPortfolioId(ptlFundAllocationImportDto.getPortfolioId());
            dto.setPayType(ptlFundAllocationImportDto.getPayType());
            dto.setSettleDate(ptlFundAllocationImportDto.getDate());
            ptlFundAllocation001Dto.setAcctNo(ptlFundAllocationImportDto.getMyAcctNo());
            ptlFundAllocation001Dto.setAcctName(ptlFundAllocationImportDto.getMyAcctNoName());
            if(StringUtil.equals(ptlFundAllocationImportDto.getPayType(),BussinessDictCodeList.指令类型_银证转账_银转证.getValue())){
                dto.setCounterAcctNo(ptlFundAllocationImportDto.getCounterAcctNo());
            }
            else if(StringUtil.equals(ptlFundAllocationImportDto.getPayType(),BussinessDictCodeList.指令类型_银证转账_证转银.getValue())){
                dto.setMyAcctNo(ptlFundAllocationImportDto.getCounterAcctNo());
            }
            dto.setCcy(ptlFundAllocationImportDto.getCcy());
            ptlFundAllocation001Dto.setSettleAmt(ArithmeticUtil.round(ptlFundAllocationImportDto.getSettleAmt(),2, RoundingMode.HALF_UP));
            ptlFundAllocation001Dto.setAmount(ptlFundAllocationImportDto.getAmount());
            ptlFundAllocation001Dtos.add(ptlFundAllocation001Dto);
            //生成对应划款指令
//            orderDtos.add(generateOrder(ptlFundAllocationImportDto));
            settleAmt = ArithmeticUtil.doAdd(settleAmt,ptlFundAllocationImportDto.getSettleAmt());
        }
        dto.setPtlFundAllocation001Dtos(ptlFundAllocation001Dtos);
        dto.setSettleAmt(settleAmt);
//        dto.setBussTrdTransferOrder(orderDtos);
        return dto;
    }
    /* 新增导入功能 add by liuwei 20200220 end */
}
