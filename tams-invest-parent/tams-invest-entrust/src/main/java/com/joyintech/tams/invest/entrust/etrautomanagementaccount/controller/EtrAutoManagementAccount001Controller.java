package com.joyintech.tams.invest.entrust.etrautomanagementaccount.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrautomanagementaccount.dto.EtrAutoManagementAccountDto;
import com.joyintech.tams.invest.entrust.etrautomanagementaccount.service.EtrAutoManagementAccountService;
import com.joyintech.tams.invest.entrust.etrfund.controller.EtrFund001Controller;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


/**
 * 自主管理专户投资指令
 * 〈功能详细描述〉
 * @author yangqifeng
 * @version 1.0 20201222
 * @since 1.0
 */
@Controller
@PageId("EtrAutoManagementAccount001")
@RequestMapping("/invest/entrust/etrautomanagementaccount/EtrAutoManagementAccount001")
public class EtrAutoManagementAccount001Controller extends BaseController {

    @Autowired
    private EtrAutoManagementAccountService etrAutoManagementAccountService;
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向);
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        return super.init(model);
    }


    @ResponseBody
    @RequestMapping("findPage")
    public Page<EtrAutoManagementAccountDto> selectForPage(){
        Page<EtrAutoManagementAccountDto> page = etrAutoManagementAccountService.selectForPage(
                RequestUtil.getPageParam());
        return page;
    }

    @ResponseBody
    @RequestMapping("/getSelectPortfolioId")
    public Page<SelectOptionDto> getSelectPortfolioId()
            throws Exception {
        Map<String, Object> map = RequestUtil.getPageParam();
        map.put("planAssetSecType", DictCodeList.计划类资产二级分类_自主管理.getValue());
        //ZGZGST-1948: 修改查询自主专户代码传参 fuyongsheng 20210902 edit start
        Page<SelectOptionDto> temp = etrAutoManagementAccountService.getSelectPortfolioId(map);
        //ZGZGST-1948: 修改查询自主专户代码传参 fuyongsheng 20210902 edit end
        return temp;
    }

    @ResponseBody
    @RequestMapping("/getFinprodMarketId")
    public List<SuggestDto> getFinprodMarketId() {
        String finprodId = super.getRequest().getParameter("param");
        return etrAutoManagementAccountService.getFinprodMarketId(finprodId);
    }

    /**
     *
     * 删除、撤销、关闭委托check
     * <功能详细描述>
     * @param dto EtrFundDto
     * @param result BindingResult
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupDelete.class}) EtrAutoManagementAccountDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 关闭委托
     *
     * @param dto EtrFundDto
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("close")
    public AjaxResult closeDelegate(@RequestBody EtrAutoManagementAccountDto dto)
    // modify by yushilong start
            throws Exception {
        // modify by yushilong end
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage> ();
        List<EtrAutoManagementAccountDto> list = dto.getSelectedItem();
        // add by zhangrui1 批量委托关闭 start
        List<String> entrustIdList = new ArrayList<>();
        // add by zhangrui1 批量委托关闭 end
        for(EtrAutoManagementAccountDto etrFundDto:list){
            if(etrAutoManagementAccountService.closeDelegate(etrFundDto.getEntrustId(), etrFundDto.getCloseWay())){
                ajaxResult.setMessage(this.getMessage("ETR_X_003"));
                // add by yushilong 批量委托关闭 start
                entrustIdList.add(etrFundDto.getEntrustId()+etrFundDto.getPortfolioId());
                // add by yushilong 批量委托关闭 end
                List<EtrAutoManagementAccountDto> dtoList = etrAutoManagementAccountService.selectAllData(Collections.singletonList(etrFundDto.getEntrustId()));
                if (StringUtil.isEmptyList(dtoList)){
                    continue;
                }
                try {
                    EntrustRdsCommonAop.doListPreOccupy(dtoList,dtoList.get(0).getAssetType(),
                            BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(),
                            false, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                errorList.add(new ValidMessage(getMessage("组合代码:"+etrFundDto.getPortfolioId())+"指令关闭失败"));
                ajaxResult.setErrorList(errorList);
            }
        }
        return ajaxResult;
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
    public ResponseEntity<byte[]> export()
            throws Exception {
        Map<String, Object> param = RequestUtil.getParameters(getRequest());
        param.put("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return etrAutoManagementAccountService.export(this.getPageId(),param);
    }
}
