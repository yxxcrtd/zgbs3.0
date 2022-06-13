package com.joyintech.tams.valuation.batch.ptlCashFlow.service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义现金流账务处理
 */
@Service
@Scope("prototype")
public class PtlCashFlowNewService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 标的类共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;


    /**
     * process 数据处理
     *
     * @param param 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param.get(GlobalConstant.READER_RESULT);

        // 若reader传递的参数或交易明细为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListFinProductDealInfoComDTO() == null || acctEntity.getListFinProductDealInfoComDTO().isEmpty()) {
            return null;
        }

        // 定义此process的返回变量
        BookGenerateResult rtnResult = new BookGenerateResult();

        // 账务明细若为空赋初始值
        if (rtnResult.getValBokBookdetailBatch() == null) {
            rtnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");

        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景

        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 交易基础信息列表



        for(FinProductDealInfoComDTO dealDto:listFinProductDealInfoComDTONow) {
//            // 变量 - 交易信息（标的交易信息）
//            TradeInfoDto dealInfoForTarget = this.getTargetInfo(dealDto.getFinprodId(),dealDto.getFinprodType(),dealDto.getBranch());
//            // 保证获取的标的交易信息不为空
//            if (dealInfoForTarget == null) {
//                dealInfoForTarget = new TradeInfoDto();
//            }

            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放交易金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            if (DictCodeList.交易方向_收取.getValue().equals(dealDto.getPs())) {
                // 组装记账元素
                // 初始化定义 - 记账场景要素管理
                // 设置账务引擎参数
                ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
                // ad.会计场景代码 = 资金收款-其他收入到账
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_其他收入到账.getValue());


                // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
                accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealDto);
                accountSceneMap.put("TRADE_MARKET", dealDto.getTradeMarket());
                // 金融产品类型+交易方向+"交易登账"
                String finprodTypeName  = "";
                if(dealDto.getFinprodType()!=null){
                    finprodTypeName = DictCodeList.from(DictCode.金融产品类型, dealDto.getFinprodType()).getCodeName();
                }
                String psName = DictCodeList.from(DictCode.交易方向, dealDto.getPs()).getCodeName();
                accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "交易登账");

                //accountSceneMap.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());// 分类1 - 变量.交易信息.分类1
                //accountSceneMap.put(BookConstant.TYPE_2, dealInfoForTarget.getType2());// 分类2 - 变量.交易信息.分类2
                //登记托管机构                    <-      变量.登记托管机构
                //accountSceneMap.put(BookConstant.REGIST_ORG, registOrg);
                accountSceneMap.put(BookConstant.REGIST_ORG, dealDto.getRegistOrg());

                accountAmountMap.put(BookConstant.DEALAMT, dealDto.getTradeAmt());//交易明细.交易金额

                accountCurrencyMap.put(BookConstant.DEALAMT, dealDto.getCcy());

            }else if(DictCodeList.交易方向_支付.getValue().equals(dealDto.getPs())){
                // 组装记账元素
                // 初始化定义 - 记账场景要素管理
                // 设置账务引擎参数
                ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
                // ad.会计场景代码 = 资金收款-其他收入到账
                ad.setTableId(DictCodeListTableId.会计场景代码_资金划款_其他支出划款.getValue());


                // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
                accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealDto);
                accountSceneMap.put("TRADE_MARKET", dealDto.getTradeMarket());
                // 金融产品类型+交易方向+"交易登账"
                String finprodTypeName  = "";
                if(dealDto.getFinprodType()!=null){
                    finprodTypeName = DictCodeList.from(DictCode.金融产品类型, dealDto.getFinprodType()).getCodeName();
                }         
                String psName = DictCodeList.from(DictCode.交易方向, dealDto.getPs()).getCodeName();
                accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "交易登账");

                //accountSceneMap.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());// 分类1 - 变量.交易信息.分类1
                //accountSceneMap.put(BookConstant.TYPE_2, dealInfoForTarget.getType2());// 分类2 - 变量.交易信息.分类2
                //登记托管机构                    <-      变量.登记托管机构
                //accountSceneMap.put(BookConstant.REGIST_ORG, registOrg);
                accountSceneMap.put(BookConstant.REGIST_ORG, dealDto.getRegistOrg());

                accountAmountMap.put(BookConstant.DEALAMT, dealDto.getTradeAmt());//交易明细.交易金额

                accountCurrencyMap.put(BookConstant.DEALAMT, dealDto.getCcy());


            }

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

        }

        return rtnResult;
    }


//    /**
//     * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
//     *
//     * @param finprodId 金融产品代码【必输项】
//     * @param finprodType 金融产品类型【必输项】
//     * @param branch 分支序号
//     * @return ValFinProductDeal 标的查询
//     * @see
//     * @since 1.0
//     */
//    private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
//        // 定义返回值 - 标的交易信息
//        TradeInfoDto tradeInfoDtoForReturn = null;
//
//        // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
//        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(finprodId, finprodType, branch);
//
//        // 当查询结果不为空
//        if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
//            // 默认取第一条标的信息
//            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
//        }
//
//        return tradeInfoDtoForReturn;
//    }

}
