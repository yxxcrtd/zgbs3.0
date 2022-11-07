/*
 * 文件名：PtlPositionAnalysis001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2018年10月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.portfolio.ptlpositionanalysis.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis001Dto;
import com.joyintech.tams.invest.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis002Dto;
import com.joyintech.tams.invest.portfolio.ptlpositionanalysis.service.PtlPositionAnalysis001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 组合持仓分析
 * @author wanghb
 * @version 1.0 2018年10月30日
 * @see PtlPositionAnalysis001Controller
 * @since 1.0
 */
@Controller
@PageId("PtlPositionAnalysis001")
@RequestMapping("/invest/portfolio/ptlpositionanalysis/PtlPositionAnalysis001")
public class PtlPositionAnalysis001Controller extends BaseController {
    @Autowired
    private PtlPositionAnalysis001Service ptlPositionAnalysis001Service;
    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;
    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.平安资产类型);
        // 平台日期
        model.addAttribute("platDate", iPlatformDate.getSysDateStr());
        model.addAttribute("AUTHORITY_TYPE", CacheUtil.getSysParam("AUTHORITY_TYPE"));

        return super.init(model);
    }
    /**
     * 组合持仓明细查询
     * @return 查询结果集
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<PtlPositionAnalysis001Dto> selectAll() {
        Map<String,Object> map = RequestUtil.getParameters(getRequest());
        Page<PtlPositionAnalysis001Dto> page = ptlPositionAnalysis001Service.selectForPage(map);
        return page;
    }
    /**
     * 组合持仓汇总查询
     * @return 查询结果集
     */
   /* @ResponseBody
    @RequestMapping("findPageForAsset")
    public Page<PtlPositionAnalysis002Dto> selectAllAsset()
            throws Exception {
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<PtlPositionAnalysis002Dto> page = ptlPositionAnalysis001Service.selectAllAsset(map);
        return page;
    }*/
    /**
     * 组合持仓汇总查询
     * @return 查询结果集
     */
    @ResponseBody
    @RequestMapping("findPageForAssetList")
    public List<PtlPositionAnalysis002Dto> selectAllAssetList(){
        Map<String,Object> map = RequestUtil.getPageParam();
        List<PtlPositionAnalysis002Dto> list = ptlPositionAnalysis001Service.selectAllAssetList(map);

        if (StringUtil.equals(map.get("sex"),"02")){
            List<PtlPositionAnalysis002Dto> cashList =  ptlPositionAnalysis001Service.selectRemain(map);
            if(cashList != null && cashList.size() > 0){
                for (PtlPositionAnalysis002Dto ptlPositionAnalysis002Dto : list) {
                    if (StringUtil.equals(ptlPositionAnalysis002Dto.getFinprodType2(),"现金")){
                        ptlPositionAnalysis002Dto.setTdyIntincexp(BigDecimal.ZERO);
                        ptlPositionAnalysis002Dto.setDscCostAmt(BigDecimal.ZERO);
                        ptlPositionAnalysis002Dto.setMarketValue(BigDecimal.ZERO);
                        cashList.forEach(item->{
                            ptlPositionAnalysis002Dto.setMarketValue(ptlPositionAnalysis002Dto.getMarketValue().add(item.getMarketValue()));
                            ptlPositionAnalysis002Dto.setTdyIntincexp(ptlPositionAnalysis002Dto.getTdyIntincexp().add(item.getTdyIntincexp()));
                            ptlPositionAnalysis002Dto.setDscCostAmt(ptlPositionAnalysis002Dto.getMarketValue());
                        });
                    }
                }
            }else{
                //穿透后剩余现金无数据则去除
                int cashIndex = -1;
                for (PtlPositionAnalysis002Dto ptlPositionAnalysis002Dto : list) {
                    if (StringUtil.equals(ptlPositionAnalysis002Dto.getFinprodType2(),"现金")){
                        cashIndex = list.indexOf(ptlPositionAnalysis002Dto);
                    }
                }
                if (cashIndex >= 0){
                    list.remove(cashIndex);
                }
            }
        }
        // start  modify by wj 2020-04-16 饼图展示数据 无现金数据。重新计算并赋值。
        countSumAndCalPercent(list);

        return list==null?new ArrayList<PtlPositionAnalysis002Dto>():list;
    }
    
    /**
     * 饼图展示数据 无现金数据。重新计算并赋值。
     * （市值 + 利息）* 100  / 总额   ===  百分比
     * @param list
     * @return
     */
    private void countSumAndCalPercent(List<PtlPositionAnalysis002Dto> list) {
		
        BigDecimal sum = BigDecimal.ZERO;

    	if(!StringUtil.isEmptyList(list)){
    		for (PtlPositionAnalysis002Dto temp : list) {
				if(temp.getMarketValue()!=null){
					sum = sum.add(temp.getMarketValue());
				}
				if(temp.getTdyIntincexp()!=null){
					sum = sum.add(temp.getTdyIntincexp());
				}
			}
    		
    		//总额不能为0
    		if(BigDecimal.ZERO.compareTo(sum)!=0){
    			for (PtlPositionAnalysis002Dto temp : list) {
    				BigDecimal marketAndTdyIntince = BigDecimal.ZERO;
    				if(temp.getMarketValue()!=null){
    					marketAndTdyIntince = marketAndTdyIntince.add(temp.getMarketValue());
    				}
    				if(temp.getTdyIntincexp()!=null){
    					marketAndTdyIntince = marketAndTdyIntince.add(temp.getTdyIntincexp());
    				}
    				temp.setAmtRation(marketAndTdyIntince.multiply(new BigDecimal(100)).divide(sum,6, BigDecimal.ROUND_HALF_UP));
				}
    		}
    	}
	}
	/**
     * 导出信息excel
     * @return excel
     * @see
     * @since 1.0
     */

   /* @RequestMapping("/export")*/
    public ResponseEntity<byte[]> export(){
        return ptlPositionAnalysis001Service.export(this.getPageId(),
                RequestUtil.getParameters(getRequest()));
    }
    /**
     * 导出信息excel
     * @return excel
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> exportNew() {
//        List<PtlPositionAnalysis001Dto> dtoList = new ArrayList<PtlPositionAnalysis001Dto>();
//        dtoList= ptlPositionAnalysis001Service.selectAllList(RequestUtil.getParameters(getRequest()));
//        File outFile = ptlPositionAnalysis001Service.makeFile(
//            "/com/joyintech/tams/invest/portfolio/ptlpositionanalysis/conf/export_conf.xml", dtoList);
//        return ExportUtil.export(
//            this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
//                                 + DateUtil.format(new Date()) + SystemGlobalConstant.CLOSE_PARENTHESIS
//                                 + SystemGlobalConstant.POINT + FileNameUtil.getFileExtName(outFile),
//            outFile, true);
        return ptlPositionAnalysis001Service.export(this.getPageId(),
            RequestUtil.getParameters(getRequest()),"searchDate");
    }

    /**
     * 资产代码的下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getPortFolioInfo")
    public List<SelectOptionDto> getPortFolioInfo(){
        Map<String,Object> map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = ptlPositionAnalysis001Service.getPortFolioInfo(map);
        return temp;
    }

    /**
     * 委外的下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getPortFolioInfoWW")
    public List<SelectOptionDto> getPortFolioInfoWW(){
        Map<String,Object> map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = ptlPositionAnalysis001Service.getPortFolioInfoWW(map);
        return temp;
    }
}
