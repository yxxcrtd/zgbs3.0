package com.joyintech.tams.invest.portfolio.ptlcashflowgapanalysis.controller;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.portfolio.ptlcashflowgapanalysis.dto.PtlCashFlowGapAnalysis001Dto;
import com.joyintech.tams.invest.portfolio.ptlcashflowgapanalysis.service.PtlCashFlowGapAnalysis001Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 现金流缺口分析
 */
@Controller
@PageId("PtlCashFlowGapAnalysis001")
@RequestMapping("/invest/portfolio/ptlcashflowgapanalysis/PtlCashFlowGapAnalysis001")
public class PtlCashFlowGapAnalysis001Controller extends BaseController {
	
	@Autowired
	private PtlCashFlowGapAnalysis001Service ptlCashFlowGapAnalysis001Service;

    @Autowired(required=false)
    private IPlatformDate iPlatformDate;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        // 初始化参数字典
        this.fromDataDic(model);
        PtlCashFlowGapAnalysis001Dto dto = new PtlCashFlowGapAnalysis001Dto();
        dto.setDisplayAccuracy("01");
        dto.setCcy("CNY"); 
        model.addAttribute(GlobalConstant.ENTITY, dto);
        Date sysDate = DateUtil.platDateFromCache();
        String morenDate = DateUtil.format(sysDate);
        model.addAttribute("sysDate", morenDate);

        model.addAttribute("platDate", DateUtil.format(iPlatformDate.getSysDate()) + " - " + DateUtil.format(iPlatformDate.getSysDate()));
        return super.init(model);
    }

    /**
     * 初始化参数字典
     *
     * @param model
     * @author jiangpeng
     * @date 2022/8/5 15:53
     */
    private void fromDataDic(Model model) {
        this.setSelectOptionToModel(model, DictCode.金额显示精度, DictCode.币种, DictCode.平安业务类型, DictCode.交易方向, DictCode.组合系列);
        //添加数据字典
        SelectDto selectTgDto = this.getSelectDataFromDataDic(DictCode.金融产品类型_投管用, null);
        SelectDto selectPaDto = this.getSelectDataFromDataDic(DictCode.平安业务类型, null);
        if (selectPaDto != null && !StringUtil.isEmptyList(selectPaDto.getOptions())) {
            List<SelectOptionDto> options = selectPaDto.getOptions();
            for (int index = options.size() - 1; index >= 0; index--) {
                SelectOptionDto option = options.get(index);
                // 去除不需要的字典类型
                if (StringUtil.equals(option.getId(), DictCodeListExt1.平安业务类型_资金划拨银证转账.getValue())
                        || StringUtil.equals(option.getId(), DictCodeListExt1.平安业务类型_资金划拨证银转账.getValue())) {
                    selectTgDto.getOptions().add(option);
                }
            }
            List<SelectOptionDto> resultIptions = selectTgDto.getOptions();
            model.addAttribute("FINPROD_TYPE_TG", JsonUtils.objectArray2Json(resultIptions.toArray()));
        }
    }
    
    /**
     * 
     * 获得组合代码
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return ptlCashFlowGapAnalysis001Service.getPortfolioId(map);
    }

    /**
	 * 分页查询资金账户下拉框
	 * 
	 * @return
	 */
	@RequestMapping("getFundAccount")
	@ResponseBody
    public List<SelectOptionDto> getAssetAccount(){
		List<SelectOptionDto> fundAccountSelect = ptlCashFlowGapAnalysis001Service.getFundAccount(RequestUtil.getPageParam());
        return fundAccountSelect;
    }
    
	/**
     * 画面查询方法
     * @return 查询结果集
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<PtlCashFlowGapAnalysis001Dto> selectAll(){
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<PtlCashFlowGapAnalysis001Dto> page = ptlCashFlowGapAnalysis001Service.selectForPage(map);
        return page;
    }
    
    
    /**
     * 导出现金流明细信息excel
     *
     * @return excel
     * @see
     * @since 1.0
     */

    @RequestMapping("export")
    public ResponseEntity<byte[]> export() {
        return ptlCashFlowGapAnalysis001Service.export(this.getPageId(),RequestUtil.getParameters(getRequest()));
    }
     
    /**
     * 导出汇总行
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("/detailExport")
    public ResponseEntity<byte[]> detailExport(){
        Map<String,Object> param = RequestUtil.getPageParam(); 
        return ptlCashFlowGapAnalysis001Service.export(this.getPageId(),param);
        //return ptlCashFlowGapAnalysis001Service.detailExport(param);
    } 
}
