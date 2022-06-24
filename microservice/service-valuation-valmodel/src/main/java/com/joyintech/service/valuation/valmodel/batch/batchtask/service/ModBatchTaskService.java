package com.joyintech.service.valuation.valmodel.batch.batchtask.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dao.SysBatchGrpMapper;
import com.joyintech.framework.base.dao.SysBatchgrpExecInfoMapper;
import com.joyintech.framework.base.entity.*;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.valuation.valmodel.batch.batchtask.dao.ModBatchTaskMapper;
import com.joyintech.service.valuation.valmodel.batch.batchtask.dto.*;
import com.joyintech.tams.common.base.dao.ModBatchLogsMapper;
import com.joyintech.tams.common.base.dao.ModValBatchParamsMapper;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.common.base.entity.ModBatchLogsExample;
import com.joyintech.tams.common.base.entity.ModValBatchParams;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.batch.batchTasks.ModBatchLogDetailDTO;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 王波
 * @date 2021-07-13
 * @see ModBatchTaskService
 * @since 1.0
 */
@Service
public class ModBatchTaskService extends BaseService {

    @Autowired
    private ModBatchTaskMapper modBatchTaskMapper;

    @Autowired
    private SysBatchGrpMapper batchGroupMapper;

//    @Autowired(required = false)
//    private ProductService productService;

//    @Autowired
//    private SysBatchPrdGrpMapper batchProductGroupMapper;

    @Autowired
    private SysBatchgrpExecInfoMapper sysBatchgrpExecInfoMapper;

    @Autowired
    private ModBatchLogsMapper modBatchLogsMapper;

    public List<BatchTaskDto> getBatchTask(Map<String, Object> params) {
        return modBatchTaskMapper.getBatchTask(params);
    }

    public void batchInsertBatchGroup(List<SysBatchJob> jobList) {

        modBatchTaskMapper.batchInsertBatchGroup(jobList);
    }

    public void batchInsertProdGroup(List<SysBatchGrpPrdlink> products) {
        modBatchTaskMapper.batchInsertProdGroup(products);
    }

    /**
     * 模型批处理固定的批处理分组编号
     */
    private final static String VAL_MODEL_BATCH_GROUP = "valmodel000000000000000000000001";


    /**
     * 读取资产信息
     * @param params
     * @return
     */
    public Page<SelectOptionDto> getAssetInfo(Map<String, Object> params, int page, int length) {
        params.put("page", page);
        params.put("length", length);

        Page<SelectOptionDto> assetInfoPages = new Page<>();
//        String isImpairment = (String)params.get("isImpairment");
//        String lossType =(String)params.get("lossType");
//        if (isImpairment != null && "true".equals(isImpairment)) {
//              if(DictCodeListExt3.减值方案类型_毕马威.getValue().equals(lossType)){
//                  assetInfoPages = super.queryForPage(page, length, () -> modBatchTaskMapper.getLossKpmgAssetInfo(params));
//              }else{
//                  assetInfoPages = super.queryForPage(page, length, () -> modBatchTaskMapper.getLossPwcAssetInfo(params));
//              }
//        } else {
              assetInfoPages=super.queryForPage(page, length, () -> modBatchTaskMapper.getValAssetInfo(params));
//        }
        return assetInfoPages;
    }

    /**
     * 主画面分页查询
     * @param params
     * @param page
     * @param length
     * @return
     */
    public Page<BatchExecutingStatusDto> findBatchExeDetail(Map<String, Object> params, int page, int length) {
        return super.queryForPage(page, length, ()->modBatchTaskMapper.findBatchExeDetail(params));
    }

    public List<BatchAssetInfoDto> getAllAssetInfo() {
        //TODO
//        Map<String, Object> requestMaps = new HashMap<>();
//        List<BatchAssetInfoDto> assetInfos = restTemplate.postForList(ServiceApplication.CUSTOMER_INTERF_NAME
//                        + "/interf/ModelCallOnInvest/getAllAssetInfo"
//                , requestMaps, BatchAssetInfoDto.class);
        /**
         * 不带参数查所有模型法要跑的资产
         */
        List<BatchAssetInfoDto> assetInfos= modBatchTaskMapper.getAllAssetInfo(null);

        return assetInfos;
    }

    @Autowired
    private SqlSessionTemplate template;

    public void insertBatchParams(List<ModValBatchParams> params) {
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            int count = 0;
            ModValBatchParamsMapper batchMapper = session.getMapper(ModValBatchParamsMapper.class);
            int size = params.size();
            for(int index = 0; index < size; index++){
                batchMapper.insert(params.get(index));
                count ++;
                // 每1000次提交
                if (count % 1000 == 0) {
                    session.commit();
                    // 清理缓存，防止溢出
                    session.clearCache();
                }
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        finally {
            session.close();
        }
    }

    /**
     * 检查是否有未执行的坡处理
     * @param finprodIds
     * @return
     */
    public boolean checkIsRunning(String finprodIds) {
        Map<String, Object> params = new HashMap<String, Object>(4);
        if (StringUtil.isNotBlank(finprodIds)) {
            List<String> finprodIdList = Arrays.asList(finprodIds.split(GlobalConstant.COMMA));
            params.put("finprodIdList", finprodIdList);
            //清理和确认是否有执行中的时候要使用任务组代码查
            params.put("BATCH_SEQ_NO", VAL_MODEL_BATCH_GROUP);
        }
        return modBatchTaskMapper.checkIsRunning(params) > 0;
    }

    /**
     * 跑批前清理掉之前的参数
     */
    public void cleanBatchParams(Map<String, Object> params) {
        //清理和确认是否有执行中的时候要使用任务组代码查
        params.put("BATCH_SEQ_NO", VAL_MODEL_BATCH_GROUP);
        modBatchTaskMapper.cleanBatchParams(params);
    }

    /**
     * 执行批处理
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    public boolean executeBatch(
                                Date startDate,
                                Date endDate,
                                String finprodIds) {

        SysBatchgrpExecInfo executeDetail = new SysBatchgrpExecInfo();
        executeDetail.setSystemCode(SysCodeUtil.getSysCode());
        executeDetail.setSeqNo(IDGenerator.generate());
        executeDetail.setBatchSeqNo(VAL_MODEL_BATCH_GROUP);
        executeDetail.setPrdGroupSeqNo(null);
        executeDetail.setStartDate(startDate);
        executeDetail.setEndDate(endDate);
        executeDetail.setStatus(BatchDictCodeList.批处理执行状态_未执行.getValue());
        executeDetail.setCreateUser(LoginUserUtil.getUserName());
        executeDetail.setUpdateUser(LoginUserUtil.getUserName());
        executeDetail.setDetailName(StringUtil.isBlank(RequestUtil.getParam("detailName")) ? (LoginUserUtil.getUserName() + "创建于"+ DateUtil.getCurrentDateTime()) : RequestUtil.getParam("detailName"));

        sysBatchgrpExecInfoMapper.insert(executeDetail);

        List<ModValBatchParams> params = new ArrayList<>();

        buildModParam(startDate, finprodIds, executeDetail, params);

        return true;
    }

    /**
     * 构间模型新增的批处理额外参数
     * @param startDate
     * @param finprodIds
     * @param executeDetail
     * @param params
     */
    private void buildModParam(Date startDate, String finprodIds, SysBatchgrpExecInfo executeDetail, List<ModValBatchParams> params) {
        if (StringUtil.isNotBlank(finprodIds)) {
            List<String> finprodIdList = Arrays.asList(finprodIds.split(GlobalConstant.COMMA));
            for (String finprodId : finprodIdList) {
                ModValBatchParams param = new ModValBatchParams();
                param.setSeqno(IDGenerator.generate());
                param.setExecDate(startDate);
                // 批处理执行序号
                param.setBatchExecSeqno(executeDetail.getSeqNo());

                // 资产代码
                param.setFinprodId(finprodId);

                param.setCreateUser(LoginUserUtil.getUserName() == null?"system":LoginUserUtil.getUserName());
                param.setUpdateUser(LoginUserUtil.getUserName() == null?"system":LoginUserUtil.getUserName());
                params.add(param);
            }
        } else {
            List<BatchAssetInfoDto> assetInfos = getAllAssetInfo();
            if (CollectionUtils.isNotEmpty(assetInfos)) {
                for (BatchAssetInfoDto assetInfo : assetInfos) {
                    ModValBatchParams param = new ModValBatchParams();
                    param.setSeqno(IDGenerator.generate());
                    param.setExecDate(startDate);
                    // 批处理执行序号
                    param.setBatchExecSeqno(executeDetail.getSeqNo());

                    // 资产代码
                    param.setFinprodId(assetInfo.getFinprodId());

                    param.setCreateUser(LoginUserUtil.getUserName() == null?"system":LoginUserUtil.getUserName());
                    param.setUpdateUser(LoginUserUtil.getUserName() == null?"system":LoginUserUtil.getUserName());
                    params.add(param);
                }
            }
        }

        insertBatchParams(params);
    }

    public List<ModBatchLogDetailDTO> findBatchExeTaskForExport(Map<String, Object> params) {
        return modBatchTaskMapper.selectLogsDetail(params);

//        String batchSeqno = (String)params.get("detailSeqNo");
//        String taskId = (String)params.get("taskId");
//        String execStatus = (String)params.get("execStatus");
//        ModBatchLogsExample example = new ModBatchLogsExample();
//        ModBatchLogsExample.Criteria criteria = example.createCriteria();
//
//        if (StringUtil.isNotBlank(batchSeqno)) {
//            criteria.andBatchExecSeqnoEqualTo(batchSeqno);
//        }
//        if (StringUtil.isNotBlank(taskId)) {
//            criteria.andTaskIdEqualTo(taskId);
//        }
//        if (StringUtil.isNotBlank(execStatus) && !StringUtil.equals("undefined", execStatus)) {
//            criteria.andExecStatusEqualTo(execStatus);
//        }
//
//        List<ModBatchLogs> logPage =  modBatchLogsMapper.selectByExampleWithBLOBs(example);
//        List<ModBatchLogDetailDTO> res = new ArrayList<>();
//        BeanUtil.copyPropertiesIgnoreNull(res, logPage);
//
//        if (CollectionUtils.isNotEmpty(logPage)) {
//            List<String> finProdIdList = new ArrayList<>(logPage.size());
//            for (ModBatchLogs modBatchLogs : logPage) {
//                finProdIdList.add(modBatchLogs.getFinprodId());
//                ModBatchLogDetailDTO modBatchLogDetailDTO = new ModBatchLogDetailDTO();
//                BeanUtil.copyPropertiesIgnoreNull(modBatchLogDetailDTO, modBatchLogs);
//                res.add(modBatchLogDetailDTO);
//            }
//            Map<String, Object> requestMap = new HashMap<>();
//            requestMap.put("finProdIdList", finProdIdList);
//            requestMap.put("isImpairment", true);
////            List<BatchAssetInfoDto> assetInfos = restTemplate.postForList(ServiceApplication.CUSTOMER_INTERF_NAME
////                            + "/interf/ModelCallOnInvest/getAllAssetInfo"
////                    , requestMap, BatchAssetInfoDto.class);
//            List<BatchAssetInfoDto> assetInfos = new ArrayList<>();
//            Boolean isImpairment = false;
//            if(StringUtil.isNotEmpty(params.get("isImpairment"))) {
//                isImpairment = Boolean.parseBoolean(params.get("isImpairment").toString());
//            }
//            if(isImpairment) {
//                params.put("execDate",params.get("execDate"));
//                assetInfos= modBatchTaskMapper.getAssetInfoImpairment(params);
//            }
//            else {
//                List<String> finprodTypes = new ArrayList<>(2);
//                finprodTypes.add(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
//                finprodTypes.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//                params.put("finprodTypes", finprodTypes);
//                if (!org.springframework.util.CollectionUtils.isEmpty(finProdIdList)) {
//                    params.put("finprodIdList", finProdIdList);
//                }
//                assetInfos= modBatchTaskMapper.getAssetInfo(params);
//            }
//            if (CollectionUtils.isNotEmpty(assetInfos)) {
//                Map<String, BatchAssetInfoDto> map = assetInfos.stream()
//                        .collect(Collectors.toMap(BatchAssetInfoDto::getFinprodId, Function.identity()));
//                res.forEach(x->x.setFinprodName(map.get(x.getFinprodId()) == null ? null:map.get(x.getFinprodId()).getFinprodName()));
//            }
//        }
//
//        return res;
    }

    public Page<ModBatchLogDetailDTO> findBatchExeTask(Map<String, Object> params, int page, int length) {
//        String batchSeqno = (String)params.get("detailSeqNo");
//        String taskId = (String)params.get("taskId");
//        String execStatus = (String)params.get("execStatus");
//        ModBatchLogsExample example = new ModBatchLogsExample();
//        ModBatchLogsExample.Criteria criteria = example.createCriteria();
//
//        if (StringUtil.isNotBlank(batchSeqno)) {
//            criteria.andBatchExecSeqnoEqualTo(batchSeqno);
//        }
//        if (StringUtil.isNotBlank(taskId)) {
//            criteria.andTaskIdEqualTo(taskId);
//        }
//        if (StringUtil.isNotBlank(execStatus) && !StringUtil.equals("undefined", execStatus)) {
//            criteria.andExecStatusEqualTo(execStatus);
//        }

        Page<ModBatchLogDetailDTO> logPage = super.queryForPage(page, length, () -> modBatchTaskMapper.selectLogsDetail(params));
//        Page<ModBatchLogDetailDTO> res = new Page<>();
//        BeanUtil.copyPropertiesIgnoreNull(res, logPage);
//        List<ModBatchLogDetailDTO> root = new ArrayList<>(logPage.getRoot().size());
//        if (CollectionUtils.isNotEmpty(logPage.getRoot())) {
//            List<String> finProdIdList = new ArrayList<>(logPage.getRoot().size());
//            for (ModBatchLogs modBatchLogs : logPage.getRoot()) {
//                finProdIdList.add(modBatchLogs.getFinprodId());
//                ModBatchLogDetailDTO modBatchLogDetailDTO = new ModBatchLogDetailDTO();
//                BeanUtil.copyPropertiesIgnoreNull(modBatchLogDetailDTO, modBatchLogs);
//                root.add(modBatchLogDetailDTO);
//            }
//            Map<String, Object> requestMaps = new HashMap<>();
//            requestMaps.put("finProdIdList", finProdIdList);
////            List<BatchAssetInfoDto> assetInfos = restTemplate.postForList(ServiceApplication.CUSTOMER_INTERF_NAME
////                            + "/interf/ModelCallOnInvest/getAllAssetInfo"
////                    , requestMaps, BatchAssetInfoDto.class);
//            List<BatchAssetInfoDto> assetInfos = new ArrayList<>();
//            Boolean isImpairment = false;
//            if(StringUtil.isNotEmpty(params.get("isImpairment"))) {
//                isImpairment = Boolean.parseBoolean(params.get("isImpairment").toString());
//            }
//            if(isImpairment) {
//                params.put("execDate",params.get("execDate"));
//                assetInfos= modBatchTaskMapper.getAssetInfoImpairment(params);
//            }
//            else {
//                List<String> finprodTypes = new ArrayList<>(2);
//                finprodTypes.add(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
//                finprodTypes.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//                params.put("finprodTypes", finprodTypes);
//                if (!org.springframework.util.CollectionUtils.isEmpty(finProdIdList)) {
//                    params.put("finprodIdList", finProdIdList);
//                }
//                assetInfos= modBatchTaskMapper.getAssetInfo(params);
//            }
//
//            if (CollectionUtils.isNotEmpty(assetInfos)) {
//                Map<String, BatchAssetInfoDto> map = assetInfos.stream()
//                        .collect(Collectors.toMap(BatchAssetInfoDto::getFinprodId, Function.identity()));
//                root.forEach(x->x.setFinprodName(map.get(x.getFinprodId()) == null ? null:map.get(x.getFinprodId()).getFinprodName()));
//            }
//        }
//        res.setRoot(root);
//        return res;
        return logPage;
    }
}
