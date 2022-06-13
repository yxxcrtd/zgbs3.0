package com.joyintech.tams.valuation.tradecostmanagement.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.BokBooksetDateMapper;
import com.joyintech.tams.common.base.entity.BokBooksetDate;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.accounadjustmanage.assetbook.service.ValAssetBook001Service;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import com.joyintech.tams.valuation.tradecostmanagement.dto.TradeCostManagement001Dto;
import com.joyintech.tams.valuation.tradecostmanagement.dto.TradeCostManagement002Dto;
import com.joyintech.tams.valuation.tradecostmanagement.service.TradeCostManagement002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 交易费用管理 新建
 *
 * @Author wangXiaoYong
 * @create 2019/11/19
 */
@Controller
@PageId("TradeCostManagement002")
@RequestMapping("/valuation/tradecostmanagement/TradeCostManagement002")
public class TradeCostManagement002Controller extends BaseController {

    @Autowired
    private TradeCostManagement002Service tradeCostManagement002Service;

    @Autowired
    private ValAssetBook001Service valAssetBook001Service;

    /**
     * 账套日期Mapper
     */
    @Autowired
    private BokBooksetDateMapper bokBooksetDateMapper;

    @Autowired
    private DBUtil dbUtil;

    /**
     * 页面初始化
     */
    @Override
    public String init(Model model) {
        //一次性费用类型 B05佣金  B13账户维护费  B99其他费用  B06交易手续费  B08结算手续费
        //币种 登记托管机构
        //是否待摊
        List<SelectOptionDto> feeTypeOptions = new ArrayList<SelectOptionDto>();
        SelectOptionDto dto1 = new SelectOptionDto();
        dto1.setId(DictCodeListExt1.一次性费用类型_佣金.getValue());
        dto1.setText(DictCodeListExt1.一次性费用类型_佣金.getCodeName());
        feeTypeOptions.add(dto1);
        SelectOptionDto dto2 = new SelectOptionDto();
        dto2.setId(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
        dto2.setText(DictCodeListExt1.一次性费用类型_交易手续费.getCodeName());
        feeTypeOptions.add(dto2);
        SelectOptionDto dto3 = new SelectOptionDto();
        dto3.setId(DictCodeListExt1.一次性费用类型_结算手续费.getValue());
        dto3.setText(DictCodeListExt1.一次性费用类型_结算手续费.getCodeName());
        feeTypeOptions.add(dto3);
        SelectOptionDto dto4 = new SelectOptionDto();
        dto4.setId(DictCodeListExt1.一次性费用类型_账户维护费.getValue());
        dto4.setText(DictCodeListExt1.一次性费用类型_账户维护费.getCodeName());
        feeTypeOptions.add(dto4);
        SelectOptionDto dto5 = new SelectOptionDto();
        dto5.setId(DictCodeListExt1.一次性费用类型_其他费用.getValue());
        dto5.setText(DictCodeListExt1.一次性费用类型_其他费用.getCodeName());
        feeTypeOptions.add(dto5);
        model.addAttribute("FEE_TYPE", JsonUtils.objectArray2Json(feeTypeOptions.toArray()));
        setSelectOptionToModel(model, DictCode.币种, DictCode.是否);
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        model.addAllAttributes(pageParam);
        model.addAttribute("isPrepaid", "N");//是否待摊默认为否
        model.addAttribute("FEE_TYPE_B08", DictCodeListExt1.一次性费用类型_结算手续费.getValue());
        model.addAttribute("FEE_TYPE_B13", DictCodeListExt1.一次性费用类型_账户维护费.getValue());
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);
        if (StringUtil.isNotEmpty(workflowId)) {
            TradeCostManagement002Dto entity =
                    tradeCostManagement002Service.getBussinessEntityByWorkflowId(workflowId);
            Map<String, Object> map = BeanUtil.object2Map(entity);
            model.addAllAttributes(map);
        }
        return super.init(model);
    }

    /**
     * 校验数据
     *
     * @param dto 参数
     * @return 校验结果
     * @see
     * @since 1.0
     */
    @RequestMapping("doCheck")
    @ResponseBody
    public AjaxResult doCheck(@RequestBody TradeCostManagement001Dto dto) {
        BokBooksetDate bokBooksetDate = bokBooksetDateMapper.selectByPrimaryKey(
                dto.getBookSetId());
        // 若当前交易的支付日小于账套日期，则提示调整账套日期后才可修改
        if (bokBooksetDate != null && DateUtil.compareDate(DateUtil.parse(dto.getPayDate()),
                bokBooksetDate.getBooksetDate()) < 0) {
            return new AjaxResult("交易支付日期不能小于账套日期，请调整账套日期后重新提交！", MessageType.错误消息);
        }
        return new AjaxResult();
    }

    @ResponseBody
    @RequestMapping("/save")
    public AjaxResult save(@RequestBody TradeCostManagement002Dto dto) {
        if (DictCodeListExt1.一次性费用类型_账户维护费.getValue().equals(dto.getFeeType())
                && !StringUtil.isNotEmpty(dto.getRegistOrg())) {
            dto.setRegistOrg(DictCodeList.登记托管机构_其他.getValue());
        }

        // 启动流程
        ProcessParameter<TradeCostManagement002Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TradeCostManagement002Dto> params = new WorkFlowParameter<>();

        //设置主键，即【即TRD_VOUCH_DEAL（手工凭证交易表）中的TRADE_ID（交易编号）】
        dto.setTradeId(dbUtil.getSequenceIndexData(DataIndexSequenceType.交易编号));

        // 业务代码（凭证流水号）
        params.setBusinessCode(dto.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeListExt3.共通业务类型_交易费用管理.getValue());
        // 页面id
        params.setPageId(this.getPageId());
        // 元素id
        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));

        //设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("交易费用管理新建：",
                entity.getTradeId(),//交易编号
                entity.getPayDate(),//支付日期
                entity.getBookSetId()//账套代码
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = tradeCostManagement002Service.start(proParam, dto);
        } catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);

//        tradeCostManagement002Service.save(dto);
//        AjaxResult result = new AjaxResult("新建成功");
//        return result;
    }

    /**
     * 检索账套代码(只取理财账套)
     *
     * @return 联想框数据
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getBookSetId2")
    public List<SuggestDto> getBookSetId2()
             {
        return valAssetBook001Service.getBooksetId2(RequestUtil.getPageParam());
    }


    /**
     * @return 联想框数据
     */
    @ResponseBody
    @RequestMapping("/getRegistOrg")
    public List<SelectOptionDto> getRegiestOrg() {
        String feeType = this.getParam("feeType");
            List<SelectOptionDto> list = new ArrayList<SelectOptionDto>();
        if (DictCodeListExt1.一次性费用类型_结算手续费.getValue().equals(feeType)) {
//            SelectOptionDto dto = new SelectOptionDto();
//            dto.setId(DictCodeList.登记托管机构_中国证券登记结算有限责任公司.getValue());
//            dto.setText(DictCodeList.登记托管机构_中国证券登记结算有限责任公司.getCodeName());
            SelectOptionDto dto;
            dto = new SelectOptionDto();
            dto.setId(DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue());
            dto.setText(DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getCodeName());
            list.add(dto);
            dto = new SelectOptionDto();
            dto.setId(DictCodeList.登记托管机构_上海清算所.getValue());
            dto.setText(DictCodeList.登记托管机构_上海清算所.getCodeName());
            list.add(dto);
            return list;
        }
        if (DictCodeListExt1.一次性费用类型_账户维护费.getValue().equals(feeType)) {
            SelectOptionDto dto;
            dto = new SelectOptionDto();
            dto.setId(DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue());
            dto.setText(DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getCodeName());
            list.add(dto);
            dto = new SelectOptionDto();
            dto.setId(DictCodeList.登记托管机构_上海清算所.getValue());
            dto.setText(DictCodeList.登记托管机构_上海清算所.getCodeName());
            list.add(dto);
            return list;
        }
        SelectOptionDto dto = new SelectOptionDto();
        list.add(dto);
        return list;
    }

}
