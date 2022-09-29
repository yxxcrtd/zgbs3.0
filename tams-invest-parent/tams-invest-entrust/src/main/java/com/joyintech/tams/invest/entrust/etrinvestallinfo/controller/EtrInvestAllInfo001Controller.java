/*
 * 文件名：EtrInvestAllInfo001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrinvestallinfo.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.invest.entrust.etrinvestallinfo.dto.EtrInvestAllInfoDto;
import com.joyintech.tams.invest.entrust.etrinvestallinfo.service.EtrInvestAllInfoService;
import com.joyintech.tams.invest.entrust.etrrepo.dto.EtrRepoDto;

/**
 * 所有投资委托信息 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年9月7日
 * @see EtrInvestAllInfo001Controller
 * @since 1.0
 */
@Controller
@PageId("EtrInvestAllInfo001")
@RequestMapping("/invest/entrust/etrinvestallinfo/EtrInvestAllInfo001")
public class EtrInvestAllInfo001Controller extends BaseController {
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 所有投资委托信息service
     */
    @Autowired
    private EtrInvestAllInfoService etrInvestAllInfoService;

    /**
     * 页面初始化
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.委托状态);
        this.setSelectOptionToModel(model, DictCode.平安资产类型);
        this.setSelectOptionToModel(model, DictCode.交易场所);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        this.setSelectOptionToModel(model, DictCode.是否);
        List<SelectOptionDto> psList = makeAllFloatRate();
        model.addAttribute("psList", JsonUtils.objectArray2Json(psList.toArray()));
        model.addAttribute("F01", DictCodeList.平安资产类型_债券.getValue());
        model.addAttribute("F07", DictCodeList.平安资产类型_质押式回购.getValue());
        model.addAttribute("F02", DictCodeList.平安资产类型_基金.getValue());
        model.addAttribute("F05", DictCodeList.平安资产类型_存款.getValue());
        model.addAttribute("F08", DictCodeList.平安资产类型_拆借.getValue());
        model.addAttribute("F04", DictCodeList.平安资产类型_计划类资产_净值型.getValue());
        model.addAttribute("F03", DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        model.addAttribute("F06", DictCodeList.平安资产类型_项目类资产.getValue());
        model.addAttribute("F09", DictCodeList.平安资产类型_计划类资产_货币型.getValue());
        /* add by zhangpuyu 2910/10/18 修复委托查询买断式回购双击跳转失败 start */
        model.addAttribute("F10", DictCodeList.平安资产类型_买断式回购.getValue());
        /* add by zhangpuyu 2910/10/18 修复委托查询买断式回购双击跳转失败 end */
        model.addAttribute("F11", DictCodeListExt2.平安资产类型_股票.getValue());
        model.addAttribute("F12", DictCodeListExt2.平安资产类型_债券借贷.getValue());
        model.addAttribute("F13", DictCodeListExt2.平安资产类型_外汇即期.getValue());
        model.addAttribute("F14", DictCodeListExt2.平安资产类型_外汇远期.getValue());
        model.addAttribute("F15", DictCodeListExt2.平安资产类型_外汇掉期.getValue());
        model.addAttribute("F16", DictCodeListExt2.平安资产类型_利率互换.getValue());
        model.addAttribute("F17", DictCodeListExt2.平安资产类型_一级市场债.getValue());
        model.addAttribute("F36", DictCodeListExt1.金融产品类型_投管用_立项指令.getValue());
        model.addAttribute("F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        model.addAttribute("IRS_TYPE", DictCode.互换方式.getValue());
        model.addAttribute("PS_T04", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("PS_T03", DictCodeList.交易方向_行权_回售.getValue());

        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("F19", DictCodeListExt3.平安资产类型_权益类资产.getValue());


        model.addAttribute("ENTRUST_BUSS_TYPE_17", DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_18", DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_19", DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_20", DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_21", DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_22", DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue());

        model.addAttribute("ENTRUST_CREATE_SOURCE_02", DictCodeListExt3.指令创建来源_场内实时.getValue());
        model.addAttribute("ENTRUST_CLASS_01", DictCodeListExt3.投资指令类型_单组合单资产指令.getValue());
        model.addAttribute("ENTRUST_CLASS_02", DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());
        model.addAttribute("TRD75", DictCodeListExt3.共通业务类型_立项指令.getValue());

        String remotePathPrefix = CacheUtil.getSysParam("dateTextType");
        if (remotePathPrefix.equals("01")){
            model.addAttribute("sysDate", (iPlatformDate.getSysDateStr()+" - "+iPlatformDate.getSysDateStr()));
        }
        return super.init(model);
    }
    /**
     * 
     * 组交易方向下拉框值
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    private List<SelectOptionDto> makeAllFloatRate() {
        List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();

        SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        for (SelectOptionDto dto : selectDto1.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto2 = this.getSelectDataFromDataDic(DictCode.回购拆借方向, null);
        for (SelectOptionDto dto : selectDto2.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto3 = this.getSelectDataFromDataDic(DictCode.互换方式, null);
        for (SelectOptionDto dto : selectDto3.getOptions()) {
            listFloatRate.add(dto);
        }
        return listFloatRate;
    }
    /**
     * 
     * 获取组合代码联想文本框信息
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getentrustId")
    public List<SuggestDto> getentrustId(String param){
        List<SuggestDto> temp = etrInvestAllInfoService.getentrustId(param);
        return temp;
    }
    /**
     * 
     * 获取资产代码联想文本框信息
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getfinprodId")
    public List<SuggestDto> getfinprodId(String param){
        List<SuggestDto> temp = etrInvestAllInfoService.getfinprodId(param);
        return temp;
    }
    /**
     * 
     * 所有投资委托信息分页查询
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<EtrInvestAllInfoDto> selectAllMstCaptialAccount(){
        Page<EtrInvestAllInfoDto> page = etrInvestAllInfoService.selectForPage(
            RequestUtil.getPageParam());
        return page;
    }

    @ApiOperation(value = "获取债券基本信息", notes = "获取债券基本信息")
    @ResponseBody
    @PostMapping("/getBasicInfo")
    public Message<EtrInvestAllInfoDto> getBasicInfo(@RequestBody EtrInvestAllInfoDto request){
        String entrustId = request.getEntrustId();
        EtrInvestAllInfoDto resultDto = etrInvestAllInfoService.getById(entrustId);
        return new Message<EtrInvestAllInfoDto>().success().setData(resultDto);
    }

    /**
     * 文件导出 <功能详细描述>
     * 
     * @return 导出返回参数
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export(){
        return etrInvestAllInfoService.export(this.getPageId(),
            RequestUtil.getParameters(getRequest()));
    }
    /**
     * PB文件导出前数据检查 add by caoxue 2019-12-31
     * 检查回购交易是否为 质押式竞价回购
     * @return 返回检查结果
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    @RequestMapping("checkPBRepoType")
    @ResponseBody
    public EtrRepoDto checkPBRepoType(){
    	return etrInvestAllInfoService.checkPBRepoType(RequestUtil.getParameters(getRequest()));
    }
    /**
     * PB文件导出前数据检查 add by caoxue 2019-12-31
     * 检查是否已导出过指令
     * @return 返回检查结果
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    @RequestMapping("checkPBExportType")
    @ResponseBody
    public String checkPBExportType(){
		Page<EtrInvestAllInfoDto> page = etrInvestAllInfoService.selectForPage(RequestUtil.getPageParam());
		List<EtrInvestAllInfoDto> listData = page.getRoot();
		String exportMsg="";
		for(int i=0;i<listData.size();i++){
			EtrInvestAllInfoDto etrInvestAllInfoDto=listData.get(i);
			if(DictCodeList.是否_是.getValue().equals(etrInvestAllInfoDto.getPbExportstatus())){
				exportMsg+=listData.get(i).getEntrustId()+"，";
			}
		}
    	return StringUtil.trimHtmlTag(exportMsg);
    }
    /**
     * PB文件导出 add by caoxue 2019-12-31
     * 
     * @return 导出返回参数
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    @RequestMapping("exportPB")
    public ResponseEntity<byte[]> exportPB(){
    	List<EtrInvestAllInfoDto> listData = etrInvestAllInfoService.selectExportPB(
    	            RequestUtil.getPageParam());
    	 
         File outFile = etrInvestAllInfoService.makeFile(
             "/com/joyintech/tams/invest/entrust/etrinvestallinfo/conf/export_conf_pb.xml",
             listData);
         
         return ExportUtil.export(
             this.getMessage("EtrInvestAllInfoPB") + SystemGlobalConstant.OPEN_PARENTHESIS
                                  + DateUtil.format(new Date())
                                  + SystemGlobalConstant.CLOSE_PARENTHESIS
                                  + SystemGlobalConstant.POINT
                                  + FileNameUtil.getFileExtName(outFile),
             outFile, true);
    }

    /**
     * 导出pdf add by zengchengfa 20210730
     * @return
     * @throws Exception
     */
    @RequestMapping("exportPdf")
    public ResponseEntity<byte[]> exportPdf(){
        return etrInvestAllInfoService.exportPdf(this.getPageId(),RequestUtil.getPageParam());
    }
    
}
