package com.joyintech.service.valuation.lossmodel.batchtask.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dao.SysBatchGrpMapper;
import com.joyintech.framework.base.dao.SysBatchPrdGrpMapper;
import com.joyintech.framework.base.dao.SysBatchgrpExecInfoMapper;
import com.joyintech.framework.base.dto.Product;
import com.joyintech.framework.base.entity.*;
import com.joyintech.framework.base.service.ProductService;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.valuation.lossmodel.assetimpairmentmanagement.dao.AssetImpairmentManagement001Mapper;
import com.joyintech.service.valuation.lossmodel.batchtask.dao.ModImpairementBatchTaskMapper;
import com.joyintech.service.valuation.lossmodel.batchtask.dto.*;
import com.joyintech.system.batch.batchmonitor.dto.BatchExecutingStatusDto;
import com.joyintech.tams.common.base.dao.ModBatchLogsMapper;
import com.joyintech.tams.common.base.dao.ModValBatchParamsMapper;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.common.base.entity.ModBatchLogsExample;
import com.joyintech.tams.common.base.entity.ModValBatchParams;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.batch.batchTasks.ModBatchLogDetailDTO;
import com.joyintech.tams.valuation.common.dto.modelDto.BatchAssetInfoDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 王波
 * @date 2021-07-27
 * @see ModImpairementBatchTaskService
 * @since 1.0
 */
@Service
public class ModImpairementBatchTaskService extends BaseService {
    /**
     * 减值模型批处理固定的批处理分组编号
     */
    private final static String LOSS_MODEL_BATCH_GROUP = "valloss0000000000000000000000001";

    @Autowired
    private ModImpairementBatchTaskMapper modImpairementBatchTaskMapper;

    @Autowired
    private SysBatchGrpMapper batchGroupMapper;

    @Autowired(required = false)
    private ProductService productService;

    @Autowired
    private SysBatchPrdGrpMapper batchProductGroupMapper;

    @Autowired
    private SysBatchgrpExecInfoMapper batchExecuteDetailMapper;


    @Autowired
    private SqlSessionTemplate template;
    @Autowired
    private SysBatchgrpExecInfoMapper sysBatchgrpExecInfoMapper;
    @Autowired
    private ModBatchLogsMapper modBatchLogsMapper;

    @Autowired
    private AssetImpairmentManagement001Mapper assetImpairmentManagement001Mapper;

    public List<BatchTaskDto> getBatchTask(Map<String, Object> params) {
        return modImpairementBatchTaskMapper.getBatchTask(params);
    }

    public void batchInsertBatchGroup(List<SysBatchJob> jobList) {

        modImpairementBatchTaskMapper.batchInsertBatchGroup(jobList);
    }

    public void batchInsertProdGroup(List<SysBatchGrpPrdlink> products) {
        modImpairementBatchTaskMapper.batchInsertProdGroup(products);
    }

//    /**
//     * 读取资产信息
//     * @param params
//     * @return
//     */
//    public Page<SelectOptionDto> getAssetInfo(Map<String, Object> params, int page, int length) {
//        params.put("page", page);
//        params.put("length", length);
//        params.put("isImpairment", true);
//        Page<BatchAssetInfoDto> assetInfoPages = new Page<>();
//
//        Boolean isImpairment = (Boolean)params.get("isImpairment");
//        if (isImpairment != null && isImpairment) {
//            assetInfoPages= super.queryForPage(page, length, () -> assetImpairmentManagement001Mapper.getAssetInfoImpairment(params));
//        } else {
//            List<String> finprodTypes = new ArrayList<>(2);
//            finprodTypes.add(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
//            finprodTypes.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//            params.put("finprodTypes", finprodTypes);
//            assetInfoPages= super.queryForPage(page, length, () -> assetImpairmentManagement001Mapper.getAssetInfo(params));
//        }
//
//
//
//        Page<SelectOptionDto> res = new Page<>();
//        BeanUtils.copyProperties(assetInfoPages, res);
//        if (CollectionUtils.isNotEmpty(assetInfoPages.getRoot())) {
//            List<SelectOptionDto> root = new ArrayList<>(assetInfoPages.getRoot().size());
//            for (BatchAssetInfoDto batchAssetInfoDto : assetInfoPages.getRoot()) {
//                SelectOptionDto select = new SelectOptionDto();
//                select.setId(batchAssetInfoDto.getFinprodId());
//                select.setText(batchAssetInfoDto.getFinprodName());
//                root.add(select);
//            }
//            res.setRoot(root);
//        }
//        return res;
//    }

    /**
     * 主画面分页查询
     * @param params
     * @param page
     * @param length
     * @return
     */
    public Page<BatchExecutingStatusDto> findBatchExeDetail(Map<String, Object> params, int page, int length) {
        return super.queryForPage(page, length, ()->modImpairementBatchTaskMapper.findBatchExeDetail(params));
    }

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
    public boolean checkIsRunning(List<String> finprodIdList, Date execDate) {
        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put("finprodIdList", finprodIdList);
        //清理和确认是否有执行中的时候要使用任务组代码查
        params.put("BATCH_SEQ_NO", LOSS_MODEL_BATCH_GROUP);
        return modImpairementBatchTaskMapper.checkIsRunning(params) > 0;
    }

    /**
     * 跑批前清理掉之前的参数
     * @param params
     */
    public void cleanBatchParams(Map<String, Object> params) {
        //清理和确认是否有执行中的时候要使用任务组代码查
        params.put("BATCH_SEQ_NO", LOSS_MODEL_BATCH_GROUP);
        modImpairementBatchTaskMapper.cleanBatchParams(params);
    }

    /**
     * 执行批处理
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    public int executeBatch(Date startDate,
                                Date endDate,
                                List<String> finprodIdList) {
        SysBatchgrpExecInfo executeDetail = new SysBatchgrpExecInfo();
        executeDetail.setSystemCode(SysCodeUtil.getSysCode());
        executeDetail.setSeqNo(IDGenerator.generate());
        executeDetail.setBatchSeqNo(LOSS_MODEL_BATCH_GROUP);
        executeDetail.setPrdGroupSeqNo(null);
        executeDetail.setStartDate(startDate);
        executeDetail.setEndDate(endDate);
        executeDetail.setStatus(BatchDictCodeList.批处理执行状态_未执行.getValue());
        executeDetail.setCreateUser(LoginUserUtil.getUserName());
        executeDetail.setUpdateUser(LoginUserUtil.getUserName());
        executeDetail.setDetailName(StringUtil.isBlank(RequestUtil.getParam("detailName")) ? (LoginUserUtil.getUserName() + "创建于"+ DateUtil.getCurrentDateTime()) : RequestUtil.getParam("detailName"));

        sysBatchgrpExecInfoMapper.insert(executeDetail);

        List<ModValBatchParams> params = new ArrayList<>();

        buildModParam(startDate, finprodIdList, executeDetail, params);
        return 0;
    }


    /**
     * 构间模型新增的批处理额外参数
     * @param startDate
     * @param finprodIds
     * @param executeDetail
     * @param params
     */
    private void buildModParam(Date startDate, List<String> finprodIdList, SysBatchgrpExecInfo executeDetail, List<ModValBatchParams> params) {
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
        insertBatchParams(params);
    }

    public Page<ModBatchLogDetailDTO> findBatchExeTask(Map<String, Object> params, int page, int length) {
        Page<ModBatchLogDetailDTO> logPage = super.queryForPage(page, length, () -> modImpairementBatchTaskMapper.selectLogsDetail(params));
        return logPage;
    }

    public List<ModBatchLogDetailDTO> findBatchExeTaskForExport(Map<String, Object> params) {
        return modImpairementBatchTaskMapper.selectLogsDetail(params);
    }

    public List<String> getAssetInfoAll(Date execDate){
        String lossType = CacheUtil.getSysParam("LOSS_TYPE");
        if(DictCodeListExt3.减值方案类型_普华.getValue().equals(lossType)){
            return modImpairementBatchTaskMapper.getAssetInfoAllPH(execDate);
        }
        else{
            return modImpairementBatchTaskMapper.getAssetInfoAllBMW(execDate);
        }

    }
}
