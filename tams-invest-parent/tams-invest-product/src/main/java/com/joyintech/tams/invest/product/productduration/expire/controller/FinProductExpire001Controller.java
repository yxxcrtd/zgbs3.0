/*
 * 文件名：FinProductExpire001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.product.productduration.expire.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.BaseDictCode;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.product.productduration.expire.dto.FinProductExpire001Dto;
import com.joyintech.tams.invest.product.productduration.expire.dto.FinProductExpire002Dto;
import com.joyintech.tams.invest.product.productduration.expire.service.FinProductExpire001Service;
import com.joyintech.tams.invest.product.productduration.expire.service.FinProductExpire002Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 产品到期查询
 * 
 * @author zhangliang
 * @version 1.0 2019年1月7日
 * @see FinProductExpire001Controller
 * @since 1.0
 */
@Controller
@PageId(value = "FinProductExpire001")
@RequestMapping("/invest/product/productduration/expire/FinProductExpire001")
public class FinProductExpire001Controller extends BaseController {



    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductExpire001Service finProductExpire001Service;

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductExpire002Service finProductExpire002Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.有效状态);
        // 因业务需求需要下拉值少显示一个值，故在Controller端初始化时候处理
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.有效状态, null);
        if (selectDto != null && !StringUtil.isEmptyList(selectDto.getOptions())) {
            List<SelectOptionDto> optionLastVersion = selectDto.getOptions();
            List<SelectOptionDto> options = new ArrayList<SelectOptionDto>();
//            if(CollectionUtils.isNotEmpty(optionLastVersion)) {
//            	// 去除待处理的下拉值
//                for(SelectOptionDto selectOptionDto : optionLastVersion) {
//                	if(!StringUtils.equalsIgnoreCase(selectOptionDto.getId(), "02")) {
//                		options.add(selectOptionDto);
//                	}
//                }
//            }
            if(CollectionUtils.isNotEmpty(optionLastVersion)) {
            	// 去除待处理的下拉值
                for(SelectOptionDto selectOptionDto : optionLastVersion) {
                	if (StringUtil.equals(DictCodeListExt1.有效状态_有效.getValue(), selectOptionDto.getId())
                            || StringUtil.equals(DictCodeListExt1.有效状态_撤销.getValue(), selectOptionDto.getId())
                            || StringUtil.equals(DictCodeListExt1.有效状态_流程中.getValue(), selectOptionDto.getId())) {
                		options.add(selectOptionDto);
                     }
                }
            }
            if(CollectionUtils.isNotEmpty(options)) {
            if ((DictCode.有效状态).getValue().equals(BaseDictCode.数据状态.getValue())) {
                for (int index = options.size() - 1; index >= 0; index-- ) {
                    SelectOptionDto option = options.get(index);
                    if (StringUtil.equals(option.getId(), BaseDictCodeList.数据状态_新建.getValue())
                        || StringUtil.equals(option.getId(), BaseDictCodeList.数据状态_删除中.getValue())
                        	|| StringUtil.equals(option.getId(),BaseDictCodeList.数据状态_修改中.getValue())) {
                        options.remove(option);
                    }
                }
            }
            model.addAttribute(DictCode.有效状态.getValue(), JsonUtils.objectArray2Json(options.toArray()));
           }
        }

        Map<String, Object> map = RequestUtil.getPageParam();
        if(StringUtil.isNotEmpty(map.get("remType"))) {
            model.addAttribute("finprodId", (String)map.get("finprodId"));

            model.addAttribute("remType", map.get("remType"));
//            String quaryDate = (String)map.get("mdate");
//            model.addAttribute("quaryDate", quaryDate+" - "+ DateUtil.addDay(quaryDate, Integer.parseInt((String)map.get("days"))));
        }

        return super.init(model);
    }

    /**
     * 查询产品成立信息
     * 
     * @return 产品成立信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("selectForPage")
    public Page<FinProductExpire001Dto> selectForPage() {
        return finProductExpire001Service.selectForPage(RequestUtil.getPageParam());
    }

    /**
     * 导出信息excel
     * 
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("export")
    public ResponseEntity<byte[]> export(){
        return finProductExpire001Service.export(this.getPageId(),
            RequestUtil.getParameters(getRequest()));
    }

    /**
     * check
     * 
     * @param finProductExpire001Dto check对象
     * @param br 验证信息
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@Valid @RequestBody FinProductExpire001Dto finProductExpire001Dto,
                            BindingResult br) {
        if (br != null && br.hasErrors()) {
            return new AjaxResult(br);
        }

		AjaxResult result = new AjaxResult();
		List<ValidMessage> errorList = new ArrayList<ValidMessage>();
		if (!DictCodeListExt1.有效状态_有效.getValue().equals(finProductExpire001Dto.getEffectStatus())) {
			errorList.add(new ValidMessage("只有有效的交易才能撤销!"));
		}
		

		if (StringUtil.isEmptyList(errorList)) {
			if (StringUtil.equalsIgnoreCase(BussinessConstant.REVOKE,
					finProductExpire001Dto.getOperateType())) {
                result.setData(this.getMessage("请确认是否撤销?"));
            }
			return result;
		}
        return result;
    }

    /**
     * 撤销
     * 
     * @param inputDto 画面输入参数dto
     * @return 撤销结果
     * @throws WorkFlowException 异常信息
     * @see
     * @since 1.0
     */
    @RequestMapping("doRevoke")
    @ResponseBody
    AjaxResult doRevoke(@RequestBody FinProductExpire002Dto inputDto) throws WorkFlowException {
    	/**流程备注，取不到FinprodMarketId add by chendijun 2019/08/19 start**/
    	inputDto.setCommentMarketId(inputDto.getFinprodMarketId());
    	/**流程备注，取不到FinprodMarketId add by chendijun 2019/08/19 end**/
        ProcessParameter<FinProductExpire002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<FinProductExpire002Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setPageId("FinProductExpire002");
        processParameter.setOperationType(OperationType.REVOKE);
        inputDto.setTradeId(inputDto.getBusiNo());
        workFlowParameter.setPageId("FinProductExpire002");
        workFlowParameter.setElementId(inputDto.getElementId());
        workFlowParameter.setBusinessCode((entity) -> entity.getBusiNo());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_收益型产品赎回到期管理.getValue());

        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("FinProductExpire001.table.busiNo") + GlobalConstant.OPEN_PARENTHESIS
                                                                            + entity.getBusiNo()
                                                                            + GlobalConstant.CLOSE_PARENTHESIS,
            getMessage("FinProductExpire001.table.prodMarketId") + GlobalConstant.OPEN_PARENTHESIS
                                                                                                                + entity.getCommentMarketId()
                                                                                                                + GlobalConstant.CLOSE_PARENTHESIS,
            getMessage(
                "FinProductExpire001.table.prinamt") + GlobalConstant.OPEN_PARENTHESIS + entity.getPrinamt() + GlobalConstant.CLOSE_PARENTHESIS));
        //update by leipeng 20190628 【收益型产品赎回/到期】撤销，撤销交易被审核人退回到代办，这笔交易还可以继续撤销，提交时就报系统异常。
        processParameter.setWorkFlowParameter(workFlowParameter);

        String msg = finProductExpire002Service.start(processParameter, inputDto);

        return new AjaxResult(msg);
    }

}
