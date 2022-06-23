/*
 * 文件名：TrdLendDuration001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年9月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdlendduration.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.core.service.MstFileCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.TrdCommonDeleteDto;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdlendduration.dto.TrdLendDuration001Dto;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.constant.OperationType;

/**
 * 拆借(存续期)
 * 〈功能详细描述〉
 * @author dingzhipeng
 * @version 1.0 2018年9月15日
 * @see TrdLendDuration001Service
 * @since 1.0
 */
@Service
public class TrdLendDuration001Service extends CommonExportService<TrdLendDuration001Dto>
implements BaseProcessService<TrdLendDuration001Dto>{

    /**
     * 拆借处理dtoService
     */
    @Autowired
    private TrdLendDurationDealEntity dealEntityService;

    @Autowired
    private PeriodCommonService periodCommonService;

    /**
     * 业务共通组件.资产头寸现金持仓共通组件
     */
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private InvestInterfCommService investInterfCommService;

    /**
     * 查询组合代码
     * <功能详细描述>
     * @param map 参数
     * @return 结果值
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String, Object> map) {
        if(map.get("initValue") == null){
            map.put("userId", LoginUserUtil.getUserName());
        }
        return restTemplate.postForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdLendDuration/getPortfolioId",
            map, SelectOptionDto.class);
    }
    /**
     * 查询组合代码
     * <功能详细描述>
     * @param map 参数
     * @return 结果值
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCounterId(Map<String, Object> map) {
        return restTemplate.postForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdLendDuration/getCounterId",
            map, SelectOptionDto.class);
    }
    /**
     *画面查询
     * <功能详细描述>
     * @param map 参数
     * @return 返回结果值
     * @see
     * @since 1.0
     */
    public Page<TrdLendDuration001Dto> selectForPage(Map<String, Object> map) {
        String backLendDirection = (String)map.get("backLendDirection");
        if(StringUtil.isNotEmpty(backLendDirection)) {
            if(StringUtil.equals(backLendDirection, DictCodeList.回购拆借方向_拆入.getValue())) {
                map.put("finprodType",DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
            }
            else {
                map.put("finprodType", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
            }
        }
        map.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdLendDuration/selectForPage",
            map, TrdLendDuration001Dto.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/period/trdlendduration/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TrdLendDuration001Dto> selectPageData(Map<String, Object> selectParam) {
        selectParam.put("userName", LoginUserUtil.getUserName());
        return selectForPage(selectParam);
    }
    /**
     * 根据画面参数获取entity
     * <功能详细描述>
     * @param map 参数
     * @return 返回结果值
     * @see
     * @since 1.0
     */
    public TrdLendDuration001Dto getMyEntity(Map<String, Object> map) {
        return restTemplate.postForObject(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdLendDuration/getEntityByMap",
            map, TrdLendDuration001Dto.class);
    }
    @Override
    public void doUpdate(TrdLendDuration001Dto entity) {
        saveDataWhenStartWorkflow(entity, OperationType.NEW);
    }
    @Override
    public void doComplete(TrdLendDuration001Dto entity, OperationType operationType) {
        FinProductTradeDto tradeDto = dealEntityService.genFinProductTradeDto(entity);
        tradeDto.setNeedSum(true);
        if (OperationType.REVOKE == operationType) {
            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            entity.setOperateType(BussinessConstant.REVOKE);
        }
        else {
            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        }
        entity.setRepIssuePartyId(CacheUtil.getSysParam("issue_party_id"));

        baseTradeProductDealCommServiceImpl.execute(tradeDto);
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        restTemplate.postForObject(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdLendDuration/doComplete", entity,
            String.class);

        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            tradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> {
                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
                    });
            baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
            tradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }

        // 调用投管接口 add by renping 20200330 start
//        tradeDto.getBaseTradeProductDealDtos().forEach(item->{
//            if (OperationType.REVOKE == operationType) {
//                getInterfInputDto(entity, item, DictCodeListExt1.交易状态_撤销);
//            }
//            else {
//                getInterfInputDto(entity, item, DictCodeListExt1.交易状态_初始);
//            }
//        });

        String busiCode = entity.getBusiCode();
        if(OperationType.REVOKE == operationType) {
            if(!busiCode.endsWith("R")) {
                busiCode = busiCode + "R";
            }
        }

//        investInterfCommService.doSendTradeToInterf(busiCode,DictCodeList.共通业务类型_拆借存续期交易.getValue());

        // 调用投管接口 add by renping 20200330 end
    }

    // 调用投管接口 add by renping 20200330 start
    /**
     * 存续期调用接口
     * 
     * @param entity 画面DTO
     * @param baseTradeProductDealDto 参数
     * @param 交易状态 交易状态
     * @see
     * @since 1.0
     */
    private void getInterfInputDto(TrdLendDuration001Dto entity,
                                   BaseTradeProductDealDto baseTradeProductDealDto,
                                   DictCodeListExt1 交易状态) {
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdLendDuration002");
        investInterfCommDto.setTradeStatus(交易状态.getValue());
        investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
        investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
        if (baseTradeProductDealDto.getStlPayOrdersList() != null
                && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
            investInterfCommDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList().get(0));
        }
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        investInterfCommDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }
    // 调用投管接口 add by renping 20200330 end

    @Override
    public void doDelete(String workflowId) {
        deleteWflDataAndPosition(workflowId);
    }

    private void deleteWflDataAndPosition(String workflowId) {
//        TrdLendDuration001Dto entity = getBussinessEntityByWorkflowId(workflowId);
//        /*List<WflTrdProductDeal> wflTrdProductDeals = periodCommonService.findDealByWorkflowId(workflowId);*/
//        List<WflStlPayOrders> wflStlPayOrders = dealEntityService.gen划款指令流程(entity);
//        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
//        wflStlPayOrders.forEach(wflStlPayOrders1 -> {
//            StlPayOrdersDto orders = new StlPayOrdersDto();
//            BeanUtils.copyProperties(wflStlPayOrders1, orders);
//            stlPayOrdersList.add(orders);
//        });
//        List<PtlSecPositionFlowDto> ptlSecPositions = new ArrayList<>();
//        List<PtlCashPositionFlowDto> ptlCashPositions = new ArrayList<>();
//        TrdProductDeal trdProductDeal = dealEntityService.getTrdProductDeal(entity);
//        periodCommonService.gen可用持仓(entity.getPortfolioId(), stlPayOrdersList, ptlSecPositions,
//            ptlCashPositions, trdProductDeal);
//        TrdCommonDeleteDto trdCommonDeleteDto = new TrdCommonDeleteDto();
//        trdCommonDeleteDto.setWorkflowId(workflowId);
//        trdCommonDeleteDto.setPtlCashPositions(ptlCashPositions);
//        trdCommonDeleteDto.setPtlSecPositions(ptlSecPositions);
//
//        restTemplate.postForObject(
//            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/doDelete",
//            trdCommonDeleteDto, String.class);
        baseTradeProductDealCommServiceImpl.delete(workflowId);
    }
    /**
     * {@inheritDoc}
     */
//    @Override
//    public void doComplete(TrdLendDuration001Dto entity, OperationType type) {
//        //撤销
//        if(type.equals(OperationType.REVOKE)){
//
//        }
//        else{
//          //附件信息
//            if(null!=fileCommonService){
//                if(null!=entity.getFileCommonList()){
//                    FileCommonDto fileCommonDto = encapFileCommon(entity.getTotleTradeId(),entity.getFileCommonList());
//                    entity.setMstFileInputParameter(fileCommonDto.getMstFileInputParameter());
//                }
//            }
//
//        }
//    }
    /**
     * 封装附件信息
     * <一句话功能简述>
     * <功能详细描述>
     * @param busiId
     * @param list
     * @return
     * @see
     * @since 1.0
     */
    public FileCommonDto encapFileCommon(String busiId, List<FileCommonDto> list) {
        FileCommonDto fileCommonDto = new FileCommonDto();
        //文件上传输入参数
          MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
          List<SysUploadFileInfo> fileList  = new ArrayList<>();
          for(FileCommonDto filecCommonDto:list){
                //根据文件路径转换成对应文件
                SysUploadFileInfo file = new SysUploadFileInfo();
                file.setSeqNo(filecCommonDto.getFileNo());
                fileList.add(file);
          }
          mstFileInputParameter.setFileList(fileList);
          mstFileInputParameter.setBussinessId(busiId);
         fileCommonDto.setMstFileInputParameter(mstFileInputParameter);
          return fileCommonDto;
      }

    @Override
    public void doInsert(TrdLendDuration001Dto entity, OperationType type) {
        saveDataWhenStartWorkflow(entity, type);
    }
    private void saveDataWhenStartWorkflow(TrdLendDuration001Dto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
            && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                entity.getTotleTradeId());
        }
        if ("submit".equals(entity.getOperateType())) {
            deleteWflDataAndPosition(entity.getWorkflowId());
//            entity.setMode(type.toString());
            // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
            WflTrdFinProductDealTotal trdFinProductDealTotal = dealEntityService.gen汇总流程表(entity);
            trdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            List<WflStlPayOrders> stlPayOrdersDtos = dealEntityService.gen划款指令流程(entity);


            // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = dealEntityService.gen汇总扩展业务流程(entity,
                trdFinProductDealTotal);

            List<WflPaTrdProductDeal> wflPaTrdProductDeals = new ArrayList<>();
            
            List<WflTrdProductDealAdd> wflTrdProductDealAdds = new ArrayList<>();
            // 金融产品交易Entity -> 金融产品交易业务流程表
            List<WflTrdProductDeal> wflTrdProductDeals = dealEntityService.gen交易业务流程(entity, wflPaTrdProductDeals, wflTrdProductDealAdds);

            if(StringUtil.equals(OperationType.REVOKE.toString(), entity.getMode())) {
                wflTrdProductDeals.forEach(wflTrdProductDeal -> {
                    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
                    wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                });
                wflPaTrdProductDeals.forEach(wflTrdProductDeal -> {
                    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
                });
                wflTrdProductDealAdds.forEach(wflTrdProductDeal -> {
                    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
                });
                stlPayOrdersDtos.forEach(wflStlPayOrders -> {
                    wflStlPayOrders.setpTradeId(wflStlPayOrders.getTradeId());
                    wflStlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                });
                trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                trdFinProductDealTotal.setpTradeId(trdFinProductDealTotal.getTotleTradeId().replace("R", ""));
            }
            wflTrdProductDeals.forEach(wflTrdProductDeal -> wflTrdProductDealMapper.insertSelective(wflTrdProductDeal));
            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
            wflTrdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            stlPayOrdersDtos.forEach(wflStlPayOrders -> wflStlPayOrdersMapper.insertSelective(wflStlPayOrders));
            wflPaTrdProductDeals.forEach(wflPaTrdProductDeal -> wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal));
            wflTrdProductDealAdds.forEach(wflTrdProductDealAdd -> wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd));

            if (type != OperationType.REVOKE) {
                // 调用业务组件.【金融产品交易共通.交易产品接口】
                FinProductTradeDto tradeDto = dealEntityService.genFinProductTradeDto(entity);
                tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                baseTradeProductDealCommServiceImpl.execute(tradeDto);
                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    tradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                    baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
                    tradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        // 交易日期+到账日期导出格式化问题修改 modfy by yufei 20200715 start
        if (StringUtil.equals(propName, "vdate")
                || StringUtil.equals(propName, "mdate")
                || StringUtil.equals(propName, "tradeDate")
                || StringUtil.equals(propName, "settleDate")) {
            if(value instanceof Date){
                cell.setCellValue(DateUtil.format((Date)value));
            }else if (value instanceof Long){
                cell.setCellValue(DateUtil.format(new Date((Long) value)));
            }
        }
        // 交易日期+到账日期导出格式化问题修改 modfy by yufei 20200715 end
    }
    /**
     *
     * 查询业务编号
     * <功能详细描述>
     * @param map 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getBusiCode(Map<String, Object> map) {
        List<String>finProdTypeList = new ArrayList<>();
        finProdTypeList.add(DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        finProdTypeList.add(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue());
        // ZGZGST-1611:增加拆借存续期业务编号查询条件 fuyongsheng 20210907 add start
        finProdTypeList.add(DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        map.put("IS_CANCEL",DictCodeList.是否_否.getValue());
        map.put("finProdTypeList",finProdTypeList);
        map.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
        map.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
        map.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        map.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
        map.put("COMMON_BUSINESS_TYPETRD11", DictCodeList.共通业务类型_拆借存续期交易.getValue());
        map.put("PS_HBDZ", DictCodeList.交易方向_还本到账.getValue());
        map.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        map.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        map.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        map.put("COM_TYPE_TRD03", DictCodeList.业务类型_拆借交易.getValue());
        map.put("userName", LoginUserUtil.getUserName());
        // ZGZGST-1611:增加拆借存续期业务编号查询条件 fuyongsheng 20210907 add end
        return restTemplate.postForPage(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/getBusiCode",
                map, SelectOptionDto.class);
    }

    @Override
    public void doBack(TrdLendDuration001Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTotleTradeId());
    }

    @Override
    public void doRevoke(TrdLendDuration001Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTotleTradeId());
    }
}
