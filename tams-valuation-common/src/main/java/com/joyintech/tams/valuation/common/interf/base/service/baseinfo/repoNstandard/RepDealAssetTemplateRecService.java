/*
 * 文件名：DepositeAssetBaseInfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.baseinfo.repoNstandard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.FinDealPledge;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.RepoNStandardCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealAsset;

/**
 * 债券回购质押品信息模板处理Service(VAL_REP_DEAL_SEC)
 * 
 * @author leipeng
 * @version 1.0 2018年6月27日
 * @see RepDealAssetTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class RepDealAssetTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    

    /**
     * 债券回购质押品信息接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 债券回购质押品信息 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RepoNStandardCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                        TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
    	RepoNStandardCommonResult result = new RepoNStandardCommonResult();
        List<ValRepDealAsset> listValRepDealAsset = new ArrayList<ValRepDealAsset>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValRepDealAsset = returnResult.getT(ValRepDealAsset.class);

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValBotAstInfo(listValRepDealAsset);
        // 质押品信息List
        List<FinDealPledge> ListFinDealPledge = (List<FinDealPledge>)map.get(
            "listFinDealPledge");
        result.setListFinDealPledge(ListFinDealPledge);
       
        
        // 添加日志
        result.setValInterflog(returnResult.getValInterflog());
        // 返回处理结果数据
        return result;
    }

    public Map<String, Object> handleValBotAstInfo(List<ValRepDealAsset> listValRepDealAsset) {
        Map<String, Object> result = new HashMap<>();
        // 定义 债券回购质押品信息List
        List<FinDealPledge> listFinDealPledge = new ArrayList<FinDealPledge>();

        for (ValRepDealAsset dto : listValRepDealAsset) {
            // 债券回购质押品信息
        	FinDealPledge FinDealPledge = getFinDealPledge(dto);
            listFinDealPledge.add(FinDealPledge);
        }
        result.put("listFinDealPledge", listFinDealPledge);
        return result;
    }

    /**
     * T1 质押品信息
     * 
     * @param dto 01-基础信息
     * @param floatingResult 
     * @return 质押品信息
     * @see
     * @since 1.0
     */
    private FinDealPledge getFinDealPledge(ValRepDealAsset dto) {
    	FinDealPledge finDealPledge = new FinDealPledge();
        // 金融产品代码 = A.回购标的代码
        finDealPledge.setFinprodId(dto.getTradeId());
        // 分支序号 = 设置全局变量(默认为0）
        finDealPledge.setBranch(InterfCommonConstant.分支序号_默认);
        // 质押金融产品代码 = 债券代码
        finDealPledge.setPledgeFinprodId(dto.getAssetId());
        // 质押金额 = 质押金额
        finDealPledge.setPledgeAmt(dto.getPledgeAmt());
        // 质押面值 = 质押面值
        finDealPledge.setPledgeFaceValue(dto.getPledgeFaceValue());
        // 质押面值 = 质押面值
        finDealPledge.setPledgeNumber(dto.getPledgeNumber());
        // 质押面值 = 质押面值
        finDealPledge.setPledgeRatio(dto.getPledgeRatio());
        return finDealPledge;
    }

}
