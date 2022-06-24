/*
 * 文件名：FinDepositTarget002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.asset.stock.controller;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.RepAssetEquity;
import com.joyintech.tams.component.business.report.asset.project.BussRepEquityInfoService;
import com.joyintech.tams.component.business.report.asset.project.dto.BussRepEquityInfoDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.asset.fund.dto.FinFundTargetDto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.asset.stock.dto.FinStockInfoDto;
import com.joyintech.tams.invest.asset.stock.service.FinStockInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 股票新建
 * @author zhangnian
 * @version 1.0 2018年9月5日
 * @see FinStockInfo002Controller
 * @since 1.0
 */
@Controller
@PageId("FinStockInfo002")
@RequestMapping("/invest/asset/stock/FinStockInfo002")
public class FinStockInfo002Controller extends BaseController {

    @Autowired(required = false)
    private FinStockInfoService finStockInfoService;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired
    private BussRepEquityInfoService bussRepEquityInfoService;

    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.交易场所,DictCode.币种,
        DictCode.是否沪港通或深港通,DictCode.上市板,DictCode.是否,DictCode.股票类型};

    /**
     * 初始化数据字典下拉框
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        //标的金融产品代码
        String finprodId = this.getParam("finprodId");
        String workflowId = getParam("workflowId");
        model.addAttribute("finprodId", finprodId);
        model.addAttribute("workflowId", workflowId);
        model.addAttribute("type8",DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId))
        //流程中的页面
            {
            FinStockInfoDto finStockInfoDto = finStockInfoService.getBussinessEntityByWorkflowId(workflowId);
            //commonBusinessService.setTradeMarketId(finStockInfoDto,model);
            model.addAttribute("entity", finStockInfoDto);
                model.addAttribute("businessId", workflowId);
            model.addAttribute("gp",DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        }
        else if(WorkFlowUtil.isNotFromToDoList()&& StringUtil.isNotEmpty(finprodId)) {//查看和编辑页面
            //获取资产是否存在交易
            model.addAttribute("isTransInAsset", commonBusinessService.isTransInAsset(finprodId)!=0?true:false);
            //根据主键获取业务实体--标的金融产品基本信息

            FinStockInfoDto dto = finStockInfoService.selectByFinprodId(finprodId);
            dto.setRealFinProductId(finprodId);
            //commonBusinessService.setTradeMarketId(dto,model);
            //modify by yjp ZGBSEI-138 香港联合交易所得标的查看和编辑页面也变成.SZ 20210806 START
            if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())){
                dto.setTradeMarketId(".SH");
            }else if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
            	dto.setTradeMarketId(".SZ");
            }else if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_香港联合交易所.getValue())){
                dto.setTradeMarketId(".HK");
            }
            //modify by yjp ZGBSEI-138 香港联合交易所得标的查看和编辑页面也变成.SZ 20210806 END
            if(null != dto){
                model.addAttribute("businessId", dto.getFinprodId());
              //  model.addAttribute("businessId", dto.getFinprodId().substring(0,dto.getFinprodId().indexOf(".")));
            }
           /* if(null !=dto.getFinprodMarketId()){
            	dto.setFinprodMarketId(dto.getFinprodMarketId().substring(0,dto.getFinprodMarketId().indexOf(".")));
            }*/
            model.addAttribute("entity", dto);
            model.addAttribute("gp",DictCodeListExt2.共通业务类型_股票信息.getValue());
        }
        else if(WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(finprodId))//新建页面（不是工作流和不是从资产代码查询）
        {
            FinStockInfoDto dto = new FinStockInfoDto();
           // dto.setFinprodId(IDGenerator.generate());
            //dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            dto.setIsMarginFinprod(DictCodeList.是否_否.getValue());
//            dto.setShOrZh(DictCodeListExt2.是否沪港通或深港通_否.getValue());
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());

            // add by etliu 20211111 TISHKS-125 start
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            // add by etliu 20211111 end

            //commonBusinessService.setTradeMarketId(dto,model);
            model.addAttribute("entity", dto);
            model.addAttribute("gp",DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        }

        // 平台日期
        model.addAttribute("iPlatformDate", DateUtil.getSysDate());
        //取系统参数
        model.addAttribute("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
        model.addAttribute("YES_OR_NO_Y",DictCodeList.是否_是.getValue());
    return super.init(model);
    }

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 业务共同
     */
    @Autowired
    private CommonBusinessService commonBusinessService;


    /**
     * check--保存
     *
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody
                                @Validated({
        ValidateGroupEdit.class} )
        FinStockInfoDto dto, BindingResult result) {
    	AjaxResult ajax= commonCheck(result, dto.getOperateType());
        if(ajax.getErrorList()!=null&&!ajax.getErrorList().isEmpty()){
        	return ajax;
        }
        String finprodId = dto.getRealFinProductId()!=null?dto.getRealFinProductId():dto.getFinprodId();
        if(commonBusinessService.isTransInAsset(finprodId)>0){
        	ajax = new AjaxResult();
            ajax.setMessageType(MessageType.确认消息);
            ajax.setData(getMessage("FIN_ASSET_STOCK_1"));
        	return ajax;
        }
        return ajax;
    }
    /**
     * 新建时根据市场代码查询wind接口基金信息
     * @param finprodMarketId 市场代码
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getInterfFundDto")
    public AjaxResult getInterfFundDto(@RequestParam("finprodMarketId") String finprodMarketId,
                                       @RequestParam("tradeMarketId") String tradeMarketId) throws ParseException{

        AjaxResult ar = new AjaxResult();
FinStockInfoDto dto = finStockInfoService.getInterfFundDto(finprodMarketId + tradeMarketId);
        if (StringUtil.isNotEmpty(dto)) {
            dto.setFinprodMarketId(finprodMarketId);
            ar.setData(dto);
        }else {
            List<ValidMessage> errList = new ArrayList<ValidMessage>();
            errList.add(new ValidMessage(this.getMessage("COMMON_E_0008",this.getMessage("FinStockInfo001.finprodMarketId"))));
            ar.setData(errList);
        }
        return ar;
    }

    /**
     * 新建时根据市场代码查询wind接口股票信息
     * @param finprodMarketId 市场代码
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getInterfStockDto")
    public AjaxResult getInterfStockDto(@RequestParam("finprodMarketId") String finprodMarketId,
                                       @RequestParam("tradeMarketId") String tradeMarketId) throws ParseException{

        AjaxResult ar = new AjaxResult();
        //add by yjp 20210813 ZGBSEI-220 start
        String finprodId = commonBusinessService.getFinProdId(finprodMarketId+tradeMarketId, BussinessDictCodeList.金融产品类型_股票.getValue(),StringUtil.EMPTY);
        FinStockInfoDto dto = finStockInfoService.selectByFinprodId(finprodId);
        BussRepEquityInfoDto bussRepEquityInfoDto = bussRepEquityInfoService.getBussRepEquityInfoByProductId(finprodId);
        if(bussRepEquityInfoDto!=null){
//            RepAssetEquity repAssetEquity = new RepAssetEquity();
//            BeanUtil.copyPropertiesIgnoreNull(repAssetEquity, bussRepEquityInfoDto);
            dto.setRepAssetEquity(bussRepEquityInfoDto);
        }
        if (StringUtil.isNotEmpty(dto)) {
            dto.setRealFinProductId(finprodId);
            dto.setFinprodMarketId(finprodMarketId);
            dto.setFinprodName(dto.getFinprodAbbr());
            if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())){
                dto.setTradeMarketId(".SH");
            }else if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                dto.setTradeMarketId(".SZ");
            }else if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_香港联合交易所.getValue())){
                dto.setTradeMarketId(".HK");
            }
            ar.setData(dto);
        }else {
            //add by yjp 20210813 ZGBSEI-220 end
            dto = finStockInfoService.getInterfStockDto(finprodMarketId + tradeMarketId);
            if (StringUtil.isNotEmpty(dto)) {
                dto.setFinprodMarketId(finprodMarketId);
                ar.setData(dto);
            } else {
                List<ValidMessage> errList = new ArrayList<ValidMessage>();
                errList.add(new ValidMessage(this.getMessage("COMMON_E_0008", this.getMessage("FinStockInfo001.finprodMarketId"))));
                ar.setData(errList);
            }
        }
        return ar;
    }


    /**
     * 保存
     *
     * @param dto
     * @param br
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody
                               @Validated({
        ValidateGroupEdit.class})
                                   FinStockInfoDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        //dto.setFinprodMarketId(dto.getFinprodMarketId()+"."+dto.getTradeMarketId());
        ProcessParameter<FinStockInfoDto> processParameter = new ProcessParameter<FinStockInfoDto>();
        WorkFlowParameter<FinStockInfoDto> params = new WorkFlowParameter<FinStockInfoDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
//        //文件信息
//        if (CollectionUtils.isNotEmpty(dto.getFileArray())
//                && StringUtil.isNotEmpty(dto.getFinprodId())) {
//            // 将附件和当前业务对象绑定（更新附件的业务代码）
//            fileAttachmentProcess.updateBussinessNo(
//                    dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
//                            Collectors.toList()),
//                    dto.getFinprodId());
//        }
        params.setBusinessType(DictCodeListExt2.共通业务类型_股票信息.getValue());
        //修改时为画面所选记录的金融产品代码。新建时调用各种业务共通组件.获取金融产品代码
//      if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
//            if (commonBusinessService != null) {
//                //设置金融产品代码
//               // dto.setFinprodMarketId(dto.getFinprodMarketId()+dto.getTradeMarketId());}//先走check逻辑，再走保存逻辑，所以这步可以放到check中
//                dto.setFinprodId(commonBusinessService.getFinProdId(dto.getFinprodMarketId(), dto.getFinprodType2(), null));
                params.setBusinessCode(dto.getFinprodMarketId()+dto.getTradeMarketId());
                // 业务说明：资产代码 + "(" + 资产代码 + ")" + 资产名称 + "(" + 资产名称 + ")"
                params.setComment(entity -> getMessage("FinStockInfo002.finprodMarketId") + "(" + entity.getFinprodMarketId()+entity.getTradeMarketId() + ")" + "-"
                        + getMessage("FinStockInfo002.finprodAbbr") + "(" + entity.getFinprodName() + ")");
//           }
//       }
//      else{
//          params.setBusinessCode(dto.getFinprodMarketId());
//          // 业务说明：资产代码 + "(" + 资产代码 + ")" + 资产名称 + "(" + 资产名称 + ")"
//          params.setComment(entity -> getMessage("FinStockInfo002.finprodMarketId") + "(" + entity.getFinprodMarketId() + ")" + "-"
//                  + getMessage("FinStockInfo002.finprodAbbr") + "(" + entity.getFinprodName() + ")");
//      }
        // 设置流程业务说明, entity即传入的dto

        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<FinStockInfoDto> parameter = new ProcessParameter<FinStockInfoDto>();
        parameter.setWorkFlowParameter(params);
        // 系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        //封装标的类金融产品基础组件需要的DTO
        dto = finStockInfoService.encapBaseFinProductBaseInfoList(dto);
        String msg = finStockInfoService.start(parameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 存款机构
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCustomerName")
    public List<SuggestDto> getCustomerName(){
        String param = super.getRequest().getParameter("param");
        List<SuggestDto> temp = finStockInfoService.getCustomerName(param);
        return temp;
    }
    /**
     * 交易日历
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCalendarId")
    public AjaxResult getCalendarId(@RequestParam("tradeMarket") String tradeMarket) {
        AjaxResult ar = new AjaxResult();
        if(tradeMarket.equals("03")){
            tradeMarket = "04";
        }
        else if(tradeMarket.equals("04")){
            tradeMarket = "05";
        }else{
            tradeMarket = "13";
        }
        List<SuggestDto> temp =  finStockInfoService.getCalendarId(tradeMarket);
        ar.setData(temp);
        return ar;
    }

    /**
     * 沪港通深港通
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCalendarId2")
    public AjaxResult getCalendarId2(@RequestParam("tradeMarket") String tradeMarket) throws ParseException{
        AjaxResult ar = new AjaxResult();
        if(tradeMarket.equals("01")){
            tradeMarket = "13";
        }
        else if(tradeMarket.equals("02")){
            tradeMarket = "14";
        }else{
            tradeMarket = "15";
        }
        List<SuggestDto> temp =  finStockInfoService.getCalendarId(tradeMarket);
        ar.setData(temp);
        return ar;
    }

    /**
     * 沪港通&深港通交易日历
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getShOrZhCalendarId")
    public AjaxResult getShOrZhCalendarId() {
        AjaxResult ar = new AjaxResult();
        List<SuggestDto> temp =  finStockInfoService.getShOrZhCalendarId();
        ar.setData(temp);
        return ar;
    }
}

