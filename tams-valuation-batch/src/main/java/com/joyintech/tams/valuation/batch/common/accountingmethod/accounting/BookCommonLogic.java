/*
 * 文件名：BookCommonLogic.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统 修改人：wangzhiang 修改时间：2017年1月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.accountingmethod.accounting;

import com.joyintech.batch.core.dto.DimensionDto;
import com.joyintech.batch.core.dto.SysBatchExecInfoDto;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.dto.AssetImpairmentProvisionReaderResult;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dao.AssetIntdateCarryoverMapper;
import com.joyintech.tams.valuation.batch.common.accounting.entity.PrdCashEntity;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodNewMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.definiton.NodeType;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountEntry;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SceneConfigEntry;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.ValBooksetTodoDto;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.vatdraw.dto.PrdCashDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 记账公共类
 *
 * @author qiangchengxi
 * @version 1.0 2017年1月3日
 * @see BookCommonLogic
 * @since 1.0
 */
@Service
public class BookCommonLogic {

    /**
     * AccountingMethodMapper
     */
    @Autowired
    private AccountingMethodMapper accountingMethodMapper;

    /**
     * AccountingMethodMapper
     */
    @Autowired
    private AccountingMethodNewMapper accountingMethodNewMapper;

    @Autowired
    private AssetIntdateCarryoverMapper assetIntdateCarryoverMapper;
    
    @Autowired
    private BookAccountCommonService bookAccountCommonService;

    @Autowired
    private BookCommonLogicNew bookCommonLogicNew;
//    /**
//     * 1
//     */
//    private static final String I1 = "1";
//
//    /**
//     * 2
//     */
//    private static final String I2 = "2";
//
//    /**
//     * 3
//     */
//    private static final String I3 = "3";
//
//    /**
//     * 4
//     */
//    private static final String I4 = "4";
    

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 加载记账场景配置信息
     *
     * @param accountSceneCodes 本批次批处理任务记账时需要使用的全部会计场景代码
     * @param booksetId 账套代码
     * @return TreeNode
     * @see
     * @since 1.0
     */
    public TreeNode loadAccountSceneConfig(List<String> accountSceneCodes, String booksetId) {
        TreeNode result = new TreeNode();
        // 节点类型设置为“根节点”
        result.setNodeType(NodeType.根节点.getCode());
        // 根据账套代码获取节点序号
        String nodeNo = this.findNodeNoByBooksetId(booksetId);
        result.setNodeNo(nodeNo);
        // 创建孩子节点列表，并赋值。
        List<TreeNode> children = new ArrayList<>();
        result.setChildrenNodes(children);
        // 判断参数列表是否为空
        if (accountSceneCodes == null || accountSceneCodes.isEmpty()
            || StringUtil.isEmpty(booksetId)) {
            // 为空，直接返回result。
            return result;
        }

        // 不为空，下记处理继续。
        // 根据参数列表查询会计场景配置信息列表，返回结果使用SceneConfigEntry接收。
        // 调用方法getSceneConfigByNodeId获取会计场景分支配置信息列表。
        List<SceneConfigEntry> sceneConfigs = this.getSceneConfigByNodeId(accountSceneCodes,
            booksetId);

        // 4 根据参数列表查询会计场景与科目配置关系信息列表，返回结果使用AccountEntry接收。
        // 4.1 调用方法getSceneSubjectConfigByNodeId获取场景科目关系信息列表。
        List<AccountEntry> accountEntrys = this.getSceneSubjectConfigByNodeId(accountSceneCodes,
            booksetId);

        // 4.2 组装数据MAP供后续使用。
        HashMap<String, List<AccountEntry>> accountEntryMap = new HashMap<String, List<AccountEntry>>();
        if (accountEntrys != null && !accountEntrys.isEmpty()) {
            for (AccountEntry accountEntry : accountEntrys) {
                // 取出场景分支节点
                String sceneNodeId = accountEntry.getSceneNodeId();
                // 赋值给accountEntryMap
                List<AccountEntry> list = accountEntryMap.get(sceneNodeId);
                if (list == null) {
                    list = new ArrayList<AccountEntry>();
                }
                list.add(accountEntry);
                accountEntryMap.put(sceneNodeId, list);
            }
            // 5 为步骤1中定义的result变量添加子节点。调用方法fitTreeBySceneConfigEntrys。
            fitTreeBySceneConfigEntrys(result, accountEntryMap, sceneConfigs);
        }
        return result;
    }

    /**
     * findNodeNoByBooksetId
     *
     * @param booksetId booksetId
     * @return NodeNo
     * @see
     * @since 1.0
     */
    private String findNodeNoByBooksetId(String booksetId) {
        // 考虑后期使用缓存
        // String nodeNo = (String)BatchCache.getValue("NodeNo_" + booksetId);
        // if (nodeNo == null) {
        String nodeNo = accountingMethodMapper.findNodeNoByBooksetId(booksetId);
        // }
        return nodeNo;
    }

    /**
     * 迭代组装树节点
     *
     * @param treeNode 树节点
     * @param accountEntryMap 场景科目关系信息MAP
     * @param sceneConfigEntrys 会计场景配置信息
     * @see
     * @since 1.0
     */
    private void fitTreeBySceneConfigEntrys(TreeNode treeNode,
                                            Map<String, List<AccountEntry>> accountEntryMap,
                                            List<SceneConfigEntry> sceneConfigEntrys) {
        // 1 会计场景配置信息不为空时，给树节点配置子节点信息。
        if (sceneConfigEntrys != null && sceneConfigEntrys.size() > 0) {
            // 1.1 取该树节点的节点序号作为检索条件
            String nodeNo = treeNode.getNodeNo();
            // 1.2 遍历会计场景配置信息列表，获取子节点，并从列表中删除该条记录。
            Iterator<SceneConfigEntry> iter = sceneConfigEntrys.iterator();
            while (iter.hasNext()) {
                SceneConfigEntry sceneConfig = iter.next();
                String fSceneNodeId = sceneConfig.getfSceneNodeId();
                if (nodeNo.equals(fSceneNodeId)) {
                    TreeNode child = new TreeNode();
                    child.setNodeNo(sceneConfig.getSceneNodeId());
                    child.setNodeType(NodeType.普通节点.getCode());
                    child.setFatherNode(treeNode);
                    child.setNodeValue(sceneConfig.getNodeValue());
                    child.setAccountEntrys(accountEntryMap.get(sceneConfig.getSceneNodeId()));
                    treeNode.setChildrenRange(sceneConfig.getNodeId());
                    List<TreeNode> children = treeNode.getChildrenNodes();
                    if (children == null) {
                        children = new ArrayList<>();
                        treeNode.setChildrenNodes(children);
                    }
                    children.add(child);
                    iter.remove();
                }
            }
            // 1.3 如果会计场景配置信息不为空时，迭代调用
            if (sceneConfigEntrys != null && sceneConfigEntrys.size() > 0) {
                // 1.3.1 去子节点列表
                List<TreeNode> children = treeNode.getChildrenNodes();
                // 1.3.2 遍历子节点列表，迭代调用
                if (children != null && !children.isEmpty()) {
                    for (TreeNode child : children) {
                        fitTreeBySceneConfigEntrys(child, accountEntryMap, sceneConfigEntrys);
                    }
                }
            }
        }
    }

    /**
     * 根据会计场景代码获取某账套下的场景分支配置信息
     *
     * @param accountSceneCodes 本批次批处理任务记账时需要使用的全部会计场景代码
     * @param booksetId 账套代码
     * @return 会计场景配置信息
     * @see
     * @since 1.0
     */
    public List<AccountEntry> getSceneSubjectConfigByNodeId(List<String> accountSceneCodes,
                                                            String booksetId) {
        // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
        // 若按照内部科目编码分录，则需要映射分录对应科目信息
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            // 调用新的service查询会计分录
            return bookCommonLogicNew.getSceneSubjectConfigByNodeId(accountSceneCodes, booksetId);
        }
        else{
            if (accountSceneCodes == null || accountSceneCodes.isEmpty()
                    || StringUtil.isEmpty(booksetId)) {
                return null;
            }
            return accountingMethodMapper.getSceneSubjectConfigByNodeId(booksetId, accountSceneCodes,
                    GlobalConstant.ACCOUNTEVENTS);
        }
        // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end
    }

    /**
     * 根据会计场景代码 批量查询会计场景 配置信息
     *
     * @param accountSceneCodes 本批次批处理任务记账时需要使用的全部会计场景代码
     * @param booksetId 账套代码
     * @return 会计场景配置信息
     * @see
     * @since 1.0
     */
    public List<SceneConfigEntry> getSceneConfigByNodeId(List<String> accountSceneCodes,
                                                         String booksetId) {
        if (accountSceneCodes == null || accountSceneCodes.isEmpty()
            || StringUtil.isEmpty(booksetId)) {
            return null;
        }
        return accountingMethodMapper.getSceneConfigByNodeId(booksetId, accountSceneCodes,
            GlobalConstant.ACCOUNTEVENTS);
    }

    /**
     * 获取跑批范围信息
     *
     * @param taskId 任务代码
     * @return 跑批范围信息list
     * @see
     * @since 1.0
     */
    public List<BokBatchTaskTodo> getTaskTodoScopeInfo(Map<String, Object> params) {

        // 获取范围信息数据
        List<BokBatchTaskTodo> reList = accountingMethodMapper.getTaskTodoScopeInfo(params);

        return reList;
    }

    /**
     * 获取账套信息
     *
     * @param taskId 任务代码
     * @return 跑批范围信息list
     * @see
     * @since 1.0
     */
    public List<BokBookset> getBooksetInfoByParam(Map<String, Object> params) {

        // 获取范围信息数据
        List<BokBookset> bokBookset = accountingMethodMapper.getBooksetInfoByParam(params);

        return bokBookset;
    }
    
    /**
     * 获取跑批范围中，包含的场景配置信息 （返回值：map<账套代码，场景配置信息>）
     *
     * @param listBatchTaskTodo 跑批范围信息list（批处理待处理任务表）
     * @param taskId 会计场景代码
     * @return map<账套代码，场景配置信息>
     * @see
     * @since 1.0
     */
    public Map<String, TreeNode> commonForGetTreeNodeMap(Set<String> bookSetIds,
                                                         String taskId) {
        List<String> matchTypeList = new ArrayList<String>();
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_会计场景.getValue());
        // 获取任务代码对应的场景代码
        List<BokMatchRlat> valMatchRlatList = accountingMethodMapper.selectValMatchRlatBySource(
            matchTypeList, taskId, null);
        // 获取会计场景记账代码
        List<String> accountSceneCodes = new ArrayList<String>();
        if (valMatchRlatList != null && !valMatchRlatList.isEmpty()) {
            valMatchRlatList.forEach(item -> {
                accountSceneCodes.add(item.getTarget());
            });
        }
        else {
            return null;
        }
//        // 循环跑批范围，获取跑批范围里的账套id（（利用set去重））
//        Set<String> bookSetIds = new HashSet<String>();
//        listBatchTaskTodo.forEach(item -> {
//            if (StringUtil.isNotEmpty(item.getBooksetId())) {
//                bookSetIds.add(item.getBooksetId());
//            }
//        });
        //查询帐套信息
        List<BokBookset> bokBooksets = accountingMethodMapper.selectBookSetIdInfo(bookSetIds);
        // 账套代码对应的配张信息
        Map<String, TreeNode> treeNodeMap = new HashMap<String, TreeNode>();
        // 模板账套代码对应的配张信息
        Map<String, TreeNode> treeNodeMapTmpl = new HashMap<String, TreeNode>();
        for (BokBookset bookSetId : bokBooksets) {
            TreeNode node = treeNodeMapTmpl.get(bookSetId.getBooksetIdTmpl());
            if (node == null) {
                // 获取每个账套对应的 记账场景配置信息
                node = this.loadAccountSceneConfig(accountSceneCodes,
                    bookSetId.getBooksetIdTmpl());
                treeNodeMapTmpl.put(bookSetId.getBooksetIdTmpl(), node);
            }
            treeNodeMap.put(bookSetId.getBooksetId(), node);

        }
        return treeNodeMap;
    }

    /**
     * 交易类场景元素赋值
     *
     * @param param param 交易信息结果DTO
     * @param dto dto 金融产品交易信息DTO
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    public Map<String, Object> transactionElementAssign(FinProductDealInfoResult param,
                                                        FinProductDealInfoComDTO dto) {

        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();

        // 若reader传递的参数为空，不进行后续处理
        if (param == null || dto == null) {
            return accountSceneMap;
        }

        // 记账场景要素map塞值
        // 账套代码 -> 交易类账务处理结果.账套代码
        accountSceneMap.put(BookConstant.BOOKSET_ID, param.getBooksetId());
        //ADD JINMAODONG 20190803 场景分支增加币种
        //币种
        accountSceneMap.put(BookConstant.CCY, dto.getCcy());
        // 发生日 -> 交易类账务处理结果.发生日
        accountSceneMap.put(BookConstant.HAPPEN_DATE, param.getHappenDate());
        // 入账日 -> 交易类账务处理结果.入账日
        accountSceneMap.put(BookConstant.BOOK_DATE, param.getBookDate());
        // 账套日期 -> 交易明细.账套代码
        accountSceneMap.put(BookConstant.BOOKSET_DATE, dto.getBooksetDate());
        // 任务代码 -> 交易明细.任务代码
        accountSceneMap.put(BookConstant.TASK_ID, dto.getTaskId());
        // 任务优先级 -> 交易明细.任务优先级
        accountSceneMap.put(BookConstant.TASK_PRI, dto.getTaskPri());
        // 凭证类型 -> 【日终:01】
        accountSceneMap.put(BookConstant.BOOK_TYPE, DictCodeList.凭证类型_日终.getValue());
        // 本位币 -> 交易明细.本位币
        accountSceneMap.put(BookConstant.B_CCY, dto.getStandardCcy());
        // 投资目的 -> 交易明细.投资目的
        accountSceneMap.put(BookConstant.INV_AIM, dto.getInvAim());
        // 交易类型->交易明细.交易方向
        accountSceneMap.put(BookConstant.PS, dto.getPs());
        // 发生数量->交易明细.交易份额
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, dto.getShareAmt());
        // 交易对手类型->交易明细.交易对手类型
//        accountSceneMap.put(BookConstant.COUNTER_TYPE, dto.getCounterType());
        // 一体化：交易对手类型改成是否外部交易
        accountSceneMap.put(BookConstant.COUNTER_TYPE, dto.getIsOutTrade());
        // 是否通道 ->交易明细.通道代码 == null ？否：是
        accountSceneMap.put(BookConstant.IS_CHL,
            dto.getChlId() == null ? DictCodeList.是否_是.getValue() : DictCodeList.是否_否.getValue());
        // 交易场所 ->交易明细.交易场所
        accountSceneMap.put(BookConstant.COUNTER_PLACE, dto.getTradeMarket());
        // 账务明细代码1->交易类账务处理结果.账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,
            getValBokDetailId(dto.getFinprodId(), param.getBokDetailId(), null,dto.getCcy()));
        // 账务明细代码2 ->交易明细.本方资金账户 == null? Null:调用账务明细代码共通（交易明细.本方资金账户,“现金类”)
        if (dto.getMyAcctNo() != null) {
            accountSceneMap.put(BookConstant.BOK_DETAIL_ID2,
                getValBokDetailId(dto.getMyAcctNo(), null, dto.getAcctType(),dto.getCcy()));
        }
        // 证券管理户代码 ->交易明细.证券管理户代码
        accountSceneMap.put(BookConstant.SEC_MANAGE_ACCT, dto.getSecManageAcctId());
        // 单位面值 ->交易明细.单位面值
        accountSceneMap.put(BookConstant.PAR_VALUE, dto.getParValue());
        // 是否累计余额->否
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_否.getValue());
        // 分支序号 - 交易明细.分支序号
        accountSceneMap.put(BookConstant.BRANCH, dto.getBranch());
        // 是否结清 - 交易明细.是否结清
        accountSceneMap.put(BookConstant.IS_CLEAN, dto.getIsClean());
        // 资金账户类型 - 交易明细.资金账户类型
        accountSceneMap.put(BookConstant.ACCT_TYPE, dto.getAcctType());
        // 交易编号
        accountSceneMap.put(BookConstant.TRADE_ID, dto.getTradeId());
        // 冲正标识 - （往账务明细（当前表））默认塞入正常
        accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());

        return accountSceneMap;
    }

    /**
     * 计提类场景元素赋值
     *
     * @param param 计提基础信息取得结果DTO
     * @param dto 计提基础信息DTO
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    public Map<String, Object> accrualElementAssign(DrawBaseInfoSelectComResult param,
                                                    DrawBaseInfoSelectComDTO dto) {

        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();

        // 若reader传递的参数为空，不进行后续处理
        if (param == null || dto == null) {
            return accountSceneMap;
        }

        // 记账场景要素map塞值
        // 账套代码 -> 交易类账务处理结果.账套代码
        accountSceneMap.put(BookConstant.BOOKSET_ID, param.getBooksetId());
        // 发生日 -> 交易类账务处理结果.发生日
        accountSceneMap.put(BookConstant.HAPPEN_DATE, param.getHappenDate());
        // 入账日 -> 交易类账务处理结果.入账日
        accountSceneMap.put(BookConstant.BOOK_DATE, param.getBookDate());
        // 账套日期 -> 交易明细.账套代码
        accountSceneMap.put(BookConstant.BOOKSET_DATE, dto.getBooksetDate());
        // 任务代码 -> 交易明细.任务代码
        accountSceneMap.put(BookConstant.TASK_ID, dto.getTaskId());
        // 任务优先级 -> 交易明细.任务优先级
        accountSceneMap.put(BookConstant.TASK_PRI, dto.getTaskPri());
        // 凭证类型 -> 【日终:01】
        accountSceneMap.put(BookConstant.BOOK_TYPE, DictCodeList.凭证类型_日终.getValue());
        //ADD JINMAODONG 20190803 场景分支增加币种
        //币种
        accountSceneMap.put(BookConstant.CCY, dto.getoCcy());
        // 本位币 -> 交易明细.本位币
        accountSceneMap.put(BookConstant.B_CCY, dto.getStandardCcy());
        // 投资目的 -> 交易明细.投资目的
        accountSceneMap.put(BookConstant.INV_AIM, param.getInvAim());
        // 账务明细代码1->交易类账务处理结果.账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,
            getValBokDetailId(dto.getBusiId(), param.getBokDetailId(), null,dto.getoCcy()));
        // 交易类型 -> null
        accountSceneMap.put(BookConstant.DEALTYPE, null);
        // 发生数量 - 交易明细.数量余额
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, dto.getNumAmtHappenDate());
        // 是否累计余额
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        // 交易编号 - > (计提类生成的账户明细，不需要塞交易编号)
        accountSceneMap.put(BookConstant.TRADE_ID, null);
        // 冲正标识 - （往账务明细（当前表））默认塞入正常
        accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());
        // 是否0入账
        accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT, DictCodeList.是否_是.getValue().equals(
            dto.getIsBackValue()) ? DictCodeList.是否_是.getValue() : null);

        return accountSceneMap;
    }
    
    /**
     * 设置增值税场景元素代码
     * 
     * @param param reader返回结果
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> taxElementAssign(PrdCashDto param) {

        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();

        // 若reader传递的参数为空，不进行后续处理
        if (param == null) {
            return accountSceneMap;
        }
        Date happenDate = param.getHappenDate();
        String taskId = param.getTaskId();
        // 记账场景要素map塞值
        // 账套代码 -> 计提Reader结果.账套代码
        accountSceneMap.put(BookConstant.BOOKSET_ID, param.getBooksetId());
        // 发生日 -> 计提Reader结果.发生日
        accountSceneMap.put(BookConstant.HAPPEN_DATE, happenDate);
        // 入账日 -> 计提Reader结果.入账日
        accountSceneMap.put(BookConstant.BOOK_DATE, param.getBookDate());
        // 账套日期 -> 计提Reader结果.账套代码
        accountSceneMap.put(BookConstant.BOOKSET_DATE, param.getBooksetDate());
        // 任务代码 -> 计提Reader结果.任务代码
        accountSceneMap.put(BookConstant.TASK_ID, taskId);
        // 任务优先级 -> 计提Reader结果.任务优先级
        accountSceneMap.put(BookConstant.TASK_PRI, param.getTaskPri());
        // 凭证类型 -> 【日终:01】
        accountSceneMap.put(BookConstant.BOOK_TYPE, DictCodeList.凭证类型_日终.getValue());
        // 本位币 -> 计提Reader结果.本位币
        accountSceneMap.put(BookConstant.B_CCY, param.getStandardCcy());
        // 投资目的 -> null
        accountSceneMap.put(BookConstant.INV_AIM, null);
        // 账务明细代码1->计提Reader结果.账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID1, this.getValBokDetailId(param.getBusiId(), param.getBokDetailId(),param.getCashType2(),param.getStandardCcy()));
        // 交易类型 -> null
        accountSceneMap.put(BookConstant.DEALTYPE, null);
        // 发生数量 - 计提Reader结果.数量余额
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, null);
        // 是否累计余额
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        // 交易编号 - > (计提类生成的账户明细，不需要塞交易编号)
        accountSceneMap.put(BookConstant.TRADE_ID, null);
        /**modify by wujie on 20191106 start
        // 是否0入账
        accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT, DictCodeList.是否_是.getValue());
        **/
        // 是否0入账
        accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT, DictCodeList.是否_否.getValue());
        /**modify by wujie on 20191106 end**/
        return accountSceneMap;
    }

    /**
     * 交易类场景元素赋值（TA固定管理费和浮动管理费） - [参考拆借计提类场景元素赋值]
     *
     * @param taskId 任务代码
     * @param happenDate 发生日
     * @param acctEntity 待计提金融产品现金流Dto
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    public Map<String, Object> accrualElementAssignForTaFee(String taskId,
                                                                      Date happenDate,
                                                                      PrdCashEntity acctEntity) {

        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();

        // 记账场景要素map塞值
        // 记账场景要素map塞值
        // 账套代码 -> 交易类账务处理结果.账套代码
        accountSceneMap.put(BookConstant.BOOKSET_ID, acctEntity.getBooksetId());
        // 发生日 -> 批处理处理日---即发生日
        accountSceneMap.put(BookConstant.HAPPEN_DATE, happenDate);
        // 入账日 -> 交易类账务处理结果.入账日
        accountSceneMap.put(BookConstant.BOOK_DATE, acctEntity.getBookDate());
        // 账套日期 -> 交易类账务处理结果.账套日期
        accountSceneMap.put(BookConstant.BOOKSET_DATE, acctEntity.getBooksetDate());
        // 任务代码 ->批处理信息.任务代码
        accountSceneMap.put(BookConstant.TASK_ID, taskId);
        // 任务优先级 -> 交易明细.任务优先级
        accountSceneMap.put(BookConstant.TASK_PRI, acctEntity.getTaskPri());
        // 凭证类型 -> 【日终:01】
        accountSceneMap.put(BookConstant.BOOK_TYPE, DictCodeList.凭证类型_日终.getValue());
        // 本位币 -> 交易类账务处理结果.本位币
        accountSceneMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        // 投资目的 -> null
        accountSceneMap.put(BookConstant.INV_AIM, null);

        // 账务明细代码1->交易类账务处理结果.账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,
            getValBokDetailId(acctEntity.getFinprodId(), acctEntity.getBokDetailId(), acctEntity.getFinprodType(),acctEntity.getStandardCcy()));

        // 交易类型 -> null
        accountSceneMap.put(BookConstant.DEALTYPE, null);
        // 发生数量 - 交易明细.数量余额
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, null);
        // 是否累计余额
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        // 交易编号 - > (计提类生成的账户明细，不需要塞交易编号)
        accountSceneMap.put(BookConstant.TRADE_ID, null);
        // 冲正标识 - （往账务明细（当前表））默认塞入正常
        accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());

        return accountSceneMap;
    }

    /**
     * 组装账务明细(资金帐户/现金类)
     *
     * @param busiId
     * @param bokDetailId
     * @return
     * @see
     * @since 1.0
     */
    public BokDetailId getValBokDetailId(String busiId, String bokDetailId,
                                            String busiDetailType,String ccy) {
        BokDetailId valBokDetailId = new BokDetailId();
        valBokDetailId.setBusiId(busiId);
        valBokDetailId.setBusiDetailType(busiDetailType);
        /**ADD BY WUJIE ON 20190805 START**/
        valBokDetailId.setCcy(ccy);
        /**ADD BY WUJIE ON 20190805 END**/
        if (bokDetailId == null) {
            valBokDetailId.setBokDetailId(bookAccountCommonService.genMd5BokDetailId(valBokDetailId));
        }
        else {
            valBokDetailId.setBokDetailId(bokDetailId);
        }
        return valBokDetailId;
    }
    
    /**
     * 组装账务明细(资产类型+投资目的+资产代码)
     * 
     * @param busiId 资产代码
     * @param bokDetailId 账务明细代码
     * @param busiDetailType 资产类型
     * @param invAim 投资目的
     * @return 账务明细代码
     * @see
     * @since 1.0
     */
     public BokDetailId getValBokDetailIdWithInvAim(String busiId, String bokDetailId,
                                             String busiDetailType,String invAim) {
         BokDetailId valBokDetailId = new BokDetailId();
         valBokDetailId.setBusiId(busiId); //资产代码
         valBokDetailId.setBusiDetailType(busiDetailType); //资产类型
         valBokDetailId.setInvAim(invAim); //投资目的
         if (bokDetailId == null) {
             valBokDetailId.setBokDetailId(bookAccountCommonService.genMd5BokDetailId(valBokDetailId));
         }
         else {
             valBokDetailId.setBokDetailId(bokDetailId);
         }
         return valBokDetailId;
     }
//
//    /**
//     * 生成MD5账务明细代码
//     *
//     * @param valBokDetailId 账务明细代码生成参数
//     * @param busiDetailType 类型
//     * @return String 账务明细代码
//     * @see
//     * @since 1.0
//     */
//    public String genMd5BokDetailId(BokDetailId valBokDetailId) {
//        String tempBokDetailId = null;
//        // 判断分支: 除了债券，均为业务明细代码 + 业务明细类型
//        if (DictCodeListExt1.金融产品类型_债券.getValue().equals(valBokDetailId.getBusiDetailType())) {
//            // 债券
//            // 账务明细代码 = 业务明细代码 + 业务明细类型 + 投资目的 + 证券管理户代码
//            tempBokDetailId = I1 + ValStringUtil.getNotNullStr(valBokDetailId.getBusiId()) + I2
//                              + ValStringUtil.getNotNullStr(valBokDetailId.getBusiDetailType())
//                              + I3 + ValStringUtil.getNotNullStr(valBokDetailId.getInvAim()) + I4
//                              + ValStringUtil.getNotNullStr(valBokDetailId.getSecManageAcctId());
//        }
//        else {
//            // 账务明细代码 = 业务明细代码 + 业务明细类型
//            tempBokDetailId = I1 + ValStringUtil.getNotNullStr(valBokDetailId.getBusiId()) + I2
//                              + ValStringUtil.getNotNullStr(valBokDetailId.getBusiDetailType());
//        }
//        // MD5转换
//        String md5BokDetailId = MD5Util.string2MD5(tempBokDetailId);
//        valBokDetailId.setBokDetailId(md5BokDetailId);
//        return md5BokDetailId;
//    }

    /**
     * 根据交易排序
     *
     * @param dealInfo
     * @return
     * @see
     * @since 1.0
     */
    public FinProductDealInfoResult sortDealInfoByPs(FinProductDealInfoResult dealInfo) {
        if (dealInfo == null) {
            return dealInfo;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("acctStandard", dealInfo.getAcctStandard());
        param.put("busiType", dealInfo.getBusiType());
        // 获取排序配置
        List<BokTradeOrder> valTradeOrderList = accountingMethodMapper.selectValTradeOrder(param);
        if (valTradeOrderList == null || valTradeOrderList.isEmpty()) {
            return dealInfo;
        }
        // 交易顺序转Map
        Map<String, Long> orderMap = valTradeOrderList.stream()
                .collect(JoyinCollectors.toMap(BokTradeOrder::getPs, BokTradeOrder::getPsOrder));
        // 按交易日排序
        Comparator<FinProductDealInfoComDTO> bySettleDate = Comparator
                .comparing(FinProductDealInfoComDTO::getSettleDate, Comparator.nullsFirst(Comparator.naturalOrder()));
        // 日志记录SET
        Set<String> notConfigedPs = new HashSet<>();
        // 按交易方向排序
        Comparator<FinProductDealInfoComDTO> byPs = Comparator.comparing(FinProductDealInfoComDTO::getPs, (s1, s2) -> {
            // 如果没有配置则放到最后处理
            if (!orderMap.containsKey(s1)) {
                notConfigedPs.add(s1);
                return 1;
            }
            if (!orderMap.containsKey(s2)) {
                notConfigedPs.add(s2);
                return -1;
            }
            return orderMap.get(s1).compareTo(orderMap.get(s2));
        });
        // 按是否结清排序
        Comparator<FinProductDealInfoComDTO> byIsClean = Comparator.comparing(FinProductDealInfoComDTO::getIsClean,
                Comparator.nullsFirst(Comparator.naturalOrder()));
        // 按交易金额排序
        Comparator<FinProductDealInfoComDTO> byTradeAmt = Comparator.comparing(FinProductDealInfoComDTO::getTradeAmt,
                Comparator.nullsFirst(Comparator.naturalOrder()));
        // 交易金额排序方式
        if (ValDictCodeList.排序类型_降序.getValue().equals(CacheUtil.getSysParam("TRADEAMT_SORT_TYPE"))) {
            byTradeAmt = byTradeAmt.reversed();
        }
        // 根据优先级依次排序  交易日>交易方向>是否结清>交易金额
        Collections.sort(dealInfo.getListFinProductDealInfoComDTO(),
                bySettleDate.thenComparing(byPs.thenComparing(byIsClean.thenComparing(byTradeAmt))));
        logger.warn("存在未配置顺序的交易方向:BUSI_TYPE[" + dealInfo.getBusiType() + "]PS"+notConfigedPs);

        return dealInfo;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dateMap
     * @return
     * @see
     * @since 1.0
     */
//    private List<Entry<Date, List<FinProductDealInfoComDTO>>> sortDealBySettleDate(Map<Date, List<FinProductDealInfoComDTO>> dateMap) {
//        List<Map.Entry<Date, List<FinProductDealInfoComDTO>>> list = new ArrayList<Map.Entry<Date, List<FinProductDealInfoComDTO>>>(dateMap.entrySet());
//        Collections.sort(list, new Comparator<Map.Entry<Date, List<FinProductDealInfoComDTO>>>() {
//
//            public int compare(Map.Entry<Date, List<FinProductDealInfoComDTO>> o1,
//                               Map.Entry<Date, List<FinProductDealInfoComDTO>> o2) {
//                return DateUtil.compareDate(o1.getKey(),o2.getKey());
//            }
//        });
//        return list;
//    }

    /**
     * 获取纬度值
     * <一句话功能简述>
     * <功能详细描述>
     * @param dimensionName
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
//    private String getDimensionValue(String dimensionName, List<DimensionDto> dimList) {
//        String dimensionValue = "";
//        if (StringUtil.isEmpty(dimensionName) || dimList == null) {
//            return dimensionValue;
//        }
//        for (DimensionDto item : dimList) {
//            if (dimensionName.equals(item.getDimensionName())) {
//                dimensionValue = item.getDimensionValue();
//                break;
//            }
//        }
//        return dimensionValue;
//    }
    
    public Map<String, String> getDimensionValue(SysBatchExecInfo batchExecuteInfo) {
        List<DimensionDto> dimList = ((SysBatchExecInfoDto)batchExecuteInfo).getDimensionDtoList();
        Map<String,String> dimensionMap = new HashMap<>();
        if (dimList == null || dimList.isEmpty()) {
            return dimensionMap;
        }
        dimList.forEach(x-> {
            dimensionMap.put(x.getDimensionName(), x.getDimensionValue());
        });
//        logger.error(batchExecuteInfo.getTaskId() + "  " + dimensionMap.toString());
        return dimensionMap;
    }
    

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listValDealAccounting
     * @see
     * @since 1.0
     */
    public void insertValDealAccounting(List<BokDealAccounting> listValDealAccounting) {
        if (listValDealAccounting == null || listValDealAccounting.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<BokDealAccounting>> list = DataHandleUtil.splitList(listValDealAccounting);
        for (List<BokDealAccounting> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                assetIntdateCarryoverMapper.insertValDealAccounting(listInsert);
            }
        }
//        List<BokDealAccounting> listInsert = new ArrayList<>();
//        listValDealAccounting.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                assetIntdateCarryoverMapper.insertValDealAccounting(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            assetIntdateCarryoverMapper.insertValDealAccounting(listInsert);
//        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listValDealAccountingDetail
     * @see
     * @since 1.0
     */
    public void insertValDealAccountingDetail(List<BokDealAccountingDetail> listValDealAccountingDetail) {
        if (listValDealAccountingDetail == null || listValDealAccountingDetail.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<BokDealAccountingDetail>> list = DataHandleUtil.splitList(listValDealAccountingDetail);
        for (List<BokDealAccountingDetail> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                assetIntdateCarryoverMapper.insertValDealAccountingDetail(listInsert);
            }
        }
//        List<BokDealAccountingDetail> listInsert = new ArrayList<>();
//        listValDealAccountingDetail.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                assetIntdateCarryoverMapper.insertValDealAccountingDetail(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            assetIntdateCarryoverMapper.insertValDealAccountingDetail(listInsert);
//        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listValFinProductDeal
     * @see
     * @since 1.0
     */
    public void insertValFinProductDeal(List<TrdProductDeal> listValFinProductDeal) {
        if (listValFinProductDeal == null || listValFinProductDeal.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<TrdProductDeal>> list = DataHandleUtil.splitList(listValFinProductDeal);
        for (List<TrdProductDeal> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                assetIntdateCarryoverMapper.insertValFinProductDeal(listInsert);
            }
        }
//        List<TrdProductDeal> listInsert = new ArrayList<>();
//        listValFinProductDeal.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                assetIntdateCarryoverMapper.insertValFinProductDeal(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            assetIntdateCarryoverMapper.insertValFinProductDeal(listInsert);
//        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listValFinProductDealExt
     * @see
     * @since 1.0
     */
    //一体化问题12:表名变更(金融产品交易扩展表(TRD_PRODUCT_DEAL_EXT) -> 金融产品交易附属表(TRD_PRODUCT_DEAL_ADD))
    public void insertValFinProductDealExt(List<TrdProductDealAdd> listValFinProductDealExt) {
        if (listValFinProductDealExt == null || listValFinProductDealExt.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<TrdProductDealAdd>> list = DataHandleUtil.splitList(listValFinProductDealExt);
        for (List<TrdProductDealAdd> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                assetIntdateCarryoverMapper.insertValFinProductDealExt(listInsert);
            }
        }
//        List<TrdProductDealExt> listInsert = new ArrayList<>();
//        listValFinProductDealExt.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                assetIntdateCarryoverMapper.insertValFinProductDealExt(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            assetIntdateCarryoverMapper.insertValFinProductDealExt(listInsert);
//        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listValTradeFinProduct
     * @see
     * @since 1.0
     */
    public void insertValTradeFinProduct(List<FinTradeProduct> listValTradeFinProduct) {
        if (listValTradeFinProduct == null || listValTradeFinProduct.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<FinTradeProduct>> list = DataHandleUtil.splitList(listValTradeFinProduct);
        for (List<FinTradeProduct> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                assetIntdateCarryoverMapper.insertValTradeFinProduct(listInsert);
            }
        }
//        List<FinTradeProduct> listInsert = new ArrayList<>();
//        listValTradeFinProduct.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                assetIntdateCarryoverMapper.insertValTradeFinProduct(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            assetIntdateCarryoverMapper.insertValTradeFinProduct(listInsert);
//        }
    }
    
    /**
     * 交易类场景元素赋值（产品通道） - [参考交易类场景元素赋值]
     *
     * @param taskId 任务代码
     * @param happenDate 发生日
     * @param acctEntity 待计提金融产品现金流Dto
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    public Map<String, Object> accrualElementAssignForPass(ValBooksetTodoDto acctEntity) {

        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();

        // 记账场景要素map塞值
        // 记账场景要素map塞值
        // 账套代码 -> 交易类账务处理结果.账套代码
        accountSceneMap.put(BookConstant.BOOKSET_ID, acctEntity.getBooksetId());
        // 发生日 -> 批处理处理日---即发生日
        accountSceneMap.put(BookConstant.HAPPEN_DATE, acctEntity.getHappenDate());
        // 入账日 -> 交易类账务处理结果.入账日
        accountSceneMap.put(BookConstant.BOOK_DATE, acctEntity.getBookDate());
        // 账套日期 -> 交易类账务处理结果.账套日期
        accountSceneMap.put(BookConstant.BOOKSET_DATE, acctEntity.getBooksetDate());
        // 任务代码 ->批处理信息.任务代码
        accountSceneMap.put(BookConstant.TASK_ID, acctEntity.getTaskId());
        // 任务优先级 -> 交易明细.任务优先级
//        accountSceneMap.put(BookConstant.TASK_PRI, acctEntity.getTaskPri());
        // 凭证类型 -> 【日终:01】
        accountSceneMap.put(BookConstant.BOOK_TYPE, DictCodeList.凭证类型_日终.getValue());
        // 本位币 -> 交易类账务处理结果.本位币
        accountSceneMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        // 投资目的 -> null
        accountSceneMap.put(BookConstant.INV_AIM, null);

        // 账务明细代码1->交易类账务处理结果.账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,
            getValBokDetailId(acctEntity.getFinprodId(), acctEntity.getBokDetailId(), acctEntity.getFinprodType(),acctEntity.getStandardCcy()));

        // 交易类型 -> null
        accountSceneMap.put(BookConstant.DEALTYPE, null);
        // 发生数量 - 交易明细.数量余额
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, null);
        // 是否累计余额
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        // 交易编号 - > (计提类生成的账户明细，不需要塞交易编号)
        accountSceneMap.put(BookConstant.TRADE_ID, null);
        // 冲正标识 - （往账务明细（当前表））默认塞入正常
        accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());

        return accountSceneMap;
    }
    
	/**
	 * 资产减值计提 - [参看设计【详细设计-共用场景元素赋值】计提场景元素]
	 *
	 * @param params 
	 * @param acctEntity 资产减值计提Dto
	 * @return Map<String, Object>
	 * @see
	 * @since 1.0
	 */
	public Map<String, Object> accrualElementAssignForTaFeeLoss(AssetImpairmentProvisionReaderResult acctEntity,
			Map<String, Object> params) {

		// 初始化定义 - 存放该笔记账的场景要素 map定义
		Map<String, Object> accountSceneMap = new HashMap<>();

		// 若reader传递的参数为空，不进行后续处理
		if (acctEntity == null) {
			return accountSceneMap;
		}

		// 记账场景要素map塞值
		// 账套代码 -> 交易类账务处理结果.账套代码
		accountSceneMap.put(BookConstant.BOOKSET_ID, acctEntity.getBooksetId());
		// 发生日 -> 交易类账务处理结果.发生日
		accountSceneMap.put(BookConstant.HAPPEN_DATE, acctEntity.getHappenDate());
		// 入账日 -> 交易类账务处理结果.入账日
		accountSceneMap.put(BookConstant.BOOK_DATE, acctEntity.getBookDate());
		// 账套日期 -> 交易明细.账套代码
		accountSceneMap.put(BookConstant.BOOKSET_DATE, acctEntity.getBooksetDate());
		// 任务代码 -> 交易明细.任务代码
		accountSceneMap.put(BookConstant.TASK_ID, params.get("taskId"));
		// 任务优先级 -> 交易明细.任务优先级
		accountSceneMap.put(BookConstant.TASK_PRI, params.get("taskPri"));
		// 凭证类型 -> 【日终:01】
		accountSceneMap.put(BookConstant.BOOK_TYPE, DictCodeList.凭证类型_日终.getValue());
		// ADD JINMAODONG 20190803 场景分支增加币种
		// 币种
		accountSceneMap.put(BookConstant.CCY, params.get("oCcy"));
		// 本位币 -> 交易明细.本位币
		accountSceneMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
		// 投资目的 -> 交易明细.投资目的
		accountSceneMap.put(BookConstant.INV_AIM, acctEntity.getInvAim());
		// 账务明细代码1->交易类账务处理结果.账务明细代码
		accountSceneMap.put(BookConstant.BOK_DETAIL_ID1, getValBokDetailId(acctEntity.getBusiId(),
				acctEntity.getBokDetailId(), null, (String) params.get("oCcy")));
		// 交易类型 -> null
		accountSceneMap.put(BookConstant.DEALTYPE, null);
		// 发生数量 - 交易明细.数量余额
		accountSceneMap.put(BookConstant.HAPPEN_NUMBER, null);
		// 是否累计余额
		accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
		// 交易编号 - > (计提类生成的账户明细，不需要塞交易编号)
		accountSceneMap.put(BookConstant.TRADE_ID, null);
		// 冲正标识 - （往账务明细（当前表））默认塞入正常
		accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());
		// 是否0入账
		accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT, null);

		return accountSceneMap;
	}
    /**
     * 查询账务明细代码
     */
	public List<BokDetailId> queryBokDetailIdList(Map<String, Object> map) {
		return bookAccountCommonService.queryBokDetailIdList(map);
	}
	
	/**
     * 账务明细代码表 批量入库
     * @param listValTradeFinProduct
     * @see
     * @since 1.0
     */
    public void insertBokDetailId(List<BokDetailId> listBokDetailId) {
        if (listBokDetailId == null || listBokDetailId.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<BokDetailId>> list = DataHandleUtil.splitList(listBokDetailId);
        for (List<BokDetailId> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                assetIntdateCarryoverMapper.insertBokDetailId(listInsert);
            }
        }
    }
    
    /**
     * 根据账套代码、业务明细代码、业务明细类型查询账务明细代码
     * @param booksetId
     * @param busiId
     * @param busiDetailType
     * @return
     */
    public BokDetailId selectBokdetailId(String booksetId, String busiId, String busiDetailType){
    	return accountingMethodMapper.selectBokDetailId(booksetId, busiId, busiDetailType);
    }
    
    /**   * 根据账套代码、交易日期、业务明细类型查询账务明细代码
     * @param booksetId
     * @param busiId
     * @param busiDetailType
     * @return
             */
    public BokDetailId selectBokdetailIdYT(String booksetId, String tradeDate, String busiDetailType){
        return accountingMethodMapper.selectBokDetailIdYT(booksetId, tradeDate, busiDetailType);
    }
    
    /**
     * 查询批处理待执行任务中的账套代码List
     * @param params 参数
     * @return 账套代码List
     * @see
     * @since 1.0
     */
    public List<String> getBatchExcuteProdList(Map<String, Object> params){
    	return accountingMethodMapper.getBatchExcuteProdList(params);
    }
    
    /**
     * 查询金融交易表
     * @param params 参数
     * @return PaTrdProductDeal
     * @see
     * @since 1.0
     */
    public PaTrdProductDeal getPaTrdProductDeal(Map<String, Object> params) {
    	return accountingMethodMapper.getPaTrdProductDeal(params);
    }
}
