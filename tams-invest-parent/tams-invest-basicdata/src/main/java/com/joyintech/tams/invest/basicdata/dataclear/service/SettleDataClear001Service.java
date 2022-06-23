/*
 * 文件名：SettleDataClear001Service.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年08月10日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.invest.basicdata.dataclear.service;

import com.google.gson.JsonObject;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.batch.batchstart.service.SysBatchStart001Service;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.basicdata.dataclear.dto.SettleBatchResultDto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 王波
 * @date 2020年08月10日
 * @see SettleDataClear001Service
 * @since 1.0
 */
@Service
public class SettleDataClear001Service extends BaseService {

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextpath.single:false}")
    private boolean isSingle;


    @Autowired
    private SysBatchStart001Service sysBatchStart001Service;

    /**
     * 查询树列表
     * @param params 参数
     * @return 树列表
     */
    public List<TreeNode> getInterGroup(Map<String, Object> params) {
        List<TreeNode> list = new ArrayList<>();

        TreeNode secNode = new TreeNode();
        secNode.setId(DictCodeListExt1.证券品种_债券.getValue());
        secNode.setName(DictCodeListExt1.证券品种_债券.getCodeName() + "交易");
        list.add(secNode);

        TreeNode sharesNode = new TreeNode();
        sharesNode.setId(DictCodeListExt1.证券品种_股票.getValue());
        sharesNode.setName(DictCodeListExt1.证券品种_股票.getCodeName() + "交易");
        list.add(sharesNode);

        TreeNode fundNode = new TreeNode();
        fundNode.setId(DictCodeListExt1.证券品种_基金.getValue());
        fundNode.setName(DictCodeListExt1.证券品种_基金.getCodeName() + "交易");
        list.add(fundNode);

        TreeNode repoNode = new TreeNode();
        repoNode.setId(DictCodeListExt1.证券品种_回购.getValue());
        repoNode.setName(DictCodeListExt1.证券品种_回购.getCodeName() + "交易");
        list.add(repoNode);

        return list;
    }

    public Page<SettleBatchResultDto> getInterActionLog(Map<String, Object> pageParam) {
        if(isSingle) {
            return simpleRestTemplate.postForPage(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataClear/findLogPage",
                    pageParam, SettleBatchResultDto.class);
        }
        else {
            return restTemplate.postForPage(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataClear/findLogPage",
                    pageParam, SettleBatchResultDto.class);
        }

    }

    /**
     * 执行按钮处理
     * @param pageParam 画面参数
     * @return 处理结果
     */
    public String execute(Map<String, Object> pageParam) {
        JsonObject json = new JsonObject();
        int isCheck = 0;

        // 选中的数据文件
        String datas = (String)pageParam.get("datas");

        String portfolioId = (String)pageParam.get("portfolioId");

        Date dealDate = DateUtil.parse(pageParam.get("execDate").toString());

        String[] portfolioIdArray = null;
        if(StringUtil.isNotEmpty(portfolioId)) {
            portfolioIdArray = StringUtil.split(portfolioId, GlobalConstant.COMMA);
        }
        if (StringUtil.isNotEmpty(datas)) {
            List<SettleBatchPara> settleBatchParas = new ArrayList<>();
            String[] dataArr = datas.split(GlobalConstant.COMMA);
            for (String secType: dataArr) {
                if(null != portfolioIdArray && portfolioIdArray.length > 0) {
                    for (String tempId : portfolioIdArray) {
                        SettleBatchPara settleBatchPara = new SettleBatchPara();
                        settleBatchPara.setDealDate(dealDate);
                        settleBatchPara.setPortfolioId(tempId);
                        settleBatchPara.setSecType(secType);
                        settleBatchPara.setSeqno(IDGenerator.generate());
                        settleBatchParas.add(settleBatchPara);
                    }
                }
                else {
                    SettleBatchPara settleBatchPara = new SettleBatchPara();
                    settleBatchPara.setDealDate(dealDate);
                    settleBatchPara.setSecType(secType);
                    settleBatchPara.setSeqno(IDGenerator.generate());
                    settleBatchParas.add(settleBatchPara);
                }
            }

            if (CollectionUtils.isNotEmpty(settleBatchParas)) {
                if (checkIsBatchRunning() != 0) {
                    json.addProperty("isCheck", isCheck);
                    json.addProperty("checkMsg", "正在进行批处理, 请稍后重试!");
                    return json.toString();
                }
                if(isSingle) {
                    simpleRestTemplate.postForObject(
                            ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataClear/insertSettleBatchParas",
                            settleBatchParas, Integer.class);
                }
                else {
                    restTemplate.postForObject(
                            ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataClear/insertSettleBatchParas",
                            settleBatchParas, Integer.class);
                }

                executeBatch(dealDate);
            } else {
                json.addProperty("checkMsg", "请选择导入数据文件!");
                return json.toString();
            }
        }
        isCheck = 1;
        json.addProperty("isCheck", isCheck);
        return json.toString();
    }

    /**
     * 执行批处理
     * @param dealDate
     */
    private void executeBatch(Date dealDate) {
        String startDate = DateUtil.format(dealDate);
        String endDate = DateUtil.format(dealDate);
        String batchGroupType = GlobalConstant.BATCH_EXECUTE_TYPE_SINGLE;
        String prodGroupType = GlobalConstant.BATCH_EXECUTE_TYPE_GROUP;
        // 50011-数据清算,50013-投管交易撤销,50012-投管交易汇总
        // “投管交易撤销” 待交易汇总测试结束后开放 TODO
        sysBatchStart001Service.executeBatch("50011,50012,50013"/*"50011,50013,50012"*/,
                StringUtil.EMPTY, startDate, endDate, batchGroupType, prodGroupType);
    }

    /**
     * 检查批处理是否正在运行
     * @return
     */
    private int checkIsBatchRunning() {
        if(isSingle) {
            return simpleRestTemplate.postForObject(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataClear/checkIsBatchRunning",
                    null, Integer.class);
        }
        else {
            return restTemplate.postForObject(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataClear/checkIsBatchRunning",
                    null, Integer.class);
        }

    }
}
