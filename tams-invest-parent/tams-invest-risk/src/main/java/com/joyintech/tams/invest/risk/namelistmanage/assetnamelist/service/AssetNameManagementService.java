package com.joyintech.tams.invest.risk.namelistmanage.assetnamelist.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.RskAccessListDetail;
import com.joyintech.tams.common.base.entity.WflRskAccessList;
import com.joyintech.tams.common.base.entity.WflRskAccessListDetail;
import com.joyintech.tams.common.base.entity.WflRskAccessListLog;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.risk.namelistmanage.assetnamelist.dto.AssetNameManagement004Dto;
import com.joyintech.tams.invest.risk.namelistmanage.assetnamelist.dto.RskAccessListDetailDto;
import com.joyintech.tams.invest.risk.namelistmanage.assetnamelist.dto.RskAccessListDto;
import com.joyintech.tams.invest.risk.namelistmanage.assetnamelist.dto.RskAccessListLogDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 名单管理--service
 * @author 王壮壮
 * @version 1.0 2019年07月22日
 * @see AssetNameManagementService
 * @since 1.0
 */
@Service
public class AssetNameManagementService extends CommonExportService<RskAccessListDto>
    implements BaseProcessService<RskAccessListDto> {

    @Autowired
    private MapperDelegateService mapperDelegateService;

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param pageParam pageParam
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<RskAccessListDto> selectForPage(Map<String, Object> pageParam) {
        if (pageParam.get("listId") != null) {
            pageParam.put("listIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)pageParam.get("listId")));
        }
        return restTemplate.postForPage(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagement/findPage",
            pageParam, RskAccessListDto.class);
    }

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param pageParam pageParam
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<RskAccessListDetailDto> selectForDetailPage(Map<String, Object> pageParam) throws IOException {
        Page<RskAccessListDetailDto> resultPage = selectPageResult(pageParam);
        //用户操作要映射到查询中  充当临时表
//        for(RskAccessListDetailDto rskAccessListDetailDto : resultPage.getRoot() ){
//            for(RskAccessListDetailDto rskAccessListDetailDto2 : handleRskAccessListDetailList){
//                if(StringUtil.equals(rskAccessListDetailDto.getListDetailName(),rskAccessListDetailDto2.getOldlistDetailName())
//                &&StringUtil.isNotEmpty(rskAccessListDetailDto2.getListDetailId())){
//                    rskAccessListDetailDto.setListDetailId(rskAccessListDetailDto2.getListDetailId());
//                    break;
//                }
//            }
//        }
        return resultPage;
    }

    public Page<RskAccessListDetailDto> selectPageResult(Map<String, Object> pageParam){
        return restTemplate.postForPage(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagementDetail/findPage",
            pageParam, RskAccessListDetailDto.class);
    }
    @Override
    public void doInsert(RskAccessListDto entity, OperationType operationType) {
            this.handleBussFlowData(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(RskAccessListDto entity) {
            // 先删除流程中数据
            this.doDelete(entity.getWorkflowId());
            // 重新insert
            this.handleBussFlowData(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        // 1.删除各业务流程信息
        // 名单
        mapperDelegateService.deleteBySpecificKey("WFL_RSK_ACCESS_LIST", "WORKFLOW_ID",
            workflowId);
        // 名单明细
        mapperDelegateService.deleteBySpecificKey("WFL_RSK_ACCESS_LIST_DETAIL", "WORKFLOW_ID",
            workflowId);
        // 名单履历
        mapperDelegateService.deleteBySpecificKey("WFL_RSK_ACCESS_LIST_LOG", "WORKFLOW_ID",
            workflowId);
    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(RskAccessListDto entity) {
        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }
        this.prepareEntity(entity);
        insertBussFlow(entity);
    }

    private void insertBussFlow(RskAccessListDto dto){
        restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagement/insertBussFlow", dto,
            Integer.class);
    }
    /**
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(RskAccessListDto dto) {
        //名单明细业务流程dto
        //查找未处理的名单明细
        List<RskAccessListDetailDto> allrskAccessListDetailList = new ArrayList<>();
        List<WflRskAccessListDetail> wflRskAccessListDetailList = new ArrayList<>();
        List<WflRskAccessListLog> wflRskAccessListLogList = new ArrayList<>();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("handleRskAccessListDetailList",dto.getRskAccessListDetailList());
        paramMap.put("listId",dto.getListId());
        List<RskAccessListDetailDto> unhandleRskAccessListDetailDtoList = getAdjPriview(paramMap);
        allrskAccessListDetailList.addAll(dto.getRskAccessListDetailList());
        allrskAccessListDetailList.addAll(unhandleRskAccessListDetailDtoList);
        //名单生效日、失效日
        RskAccessListDto rskAccessListDto = selectRskAccessList(paramMap);
        dto.setEffdate(rskAccessListDto.getEffdate());
        dto.setDeldate(rskAccessListDto.getDeldate());
        dto.setListType(rskAccessListDto.getListType());
        dto.setListDetailType(rskAccessListDto.getListDetailType());
        dto.setListLevel(rskAccessListDto.getListLevel());
        //名单业务流程表
        WflRskAccessList wflRskAccessList = new WflRskAccessList();
        BeanUtil.copyPropertiesIgnoreNull(wflRskAccessList,dto);
        wflRskAccessList.setWorkflowStatus(DictCodeListExt1.名单生效状态_生效流程中.getValue());
        for(RskAccessListDetailDto item : allrskAccessListDetailList){
            WflRskAccessListLog wflRskAccessListLog = new WflRskAccessListLog();
            if(!StringUtil.equals(item.getOperatePs(), DictCodeListExt2.名单操作方向_移出名单.getValue())){
                WflRskAccessListDetail wflRskAccessListDetail = new WflRskAccessListDetail();
                BeanUtil.copyPropertiesIgnoreNull(wflRskAccessListDetail,item);
                wflRskAccessListDetail.setWorkflowId(dto.getWorkflowId());
                wflRskAccessListDetail.setListId(dto.getListId());
                wflRskAccessListDetail.setListDate(DateUtil.platDateFromCache());
                wflRskAccessListDetail.setListDetailType(dto.getListDetailType());
                /*wflRskAccessListDetail.setOperateType(dto.getOperateStyle());*/
                wflRskAccessListDetail.setWorkflowStatus(DictCodeListExt1.名单生效状态_生效流程中.getValue());
                wflRskAccessListDetail.setOperateType(dto.getOperateStyle());
                wflRskAccessListDetailList.add(wflRskAccessListDetail);
            }
            BeanUtil.copyPropertiesIgnoreNull(wflRskAccessListLog,item);
            wflRskAccessListLog.setWorkflowId(dto.getWorkflowId());
            wflRskAccessListLog.setListId(dto.getListId());
            wflRskAccessListLog.setListDate(DateUtil.platDateFromCache());
            wflRskAccessListLog.setListDetailType(dto.getListDetailType());
            /*wflRskAccessListLog.setOperateType(dto.getOperateStyle());*/
            wflRskAccessListLog.setWorkflowStatus(DictCodeListExt1.名单生效状态_生效流程中.getValue());
            wflRskAccessListLog.setOperateType(dto.getOperateStyle());
            wflRskAccessListLogList.add(wflRskAccessListLog);
        }
        dto.setWflRskAccessList(wflRskAccessList);
        dto.setWflRskAccessListDetail(wflRskAccessListDetailList);
        dto.setWflRskAccessListLog(wflRskAccessListLogList);
    }
    /**
     * 工作流（留痕操作结束的时候执行的方法）
     */
    @Override
    public void doComplete(RskAccessListDto entity, OperationType type) {
        String action = StringUtil.EMPTY;
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        }
        else if (OperationType.NEW.equals(type)) {
            action = "insert";
            //处理业务dto
            List<RskAccessListDetailDto> allrskAccessListDetailList = new ArrayList<>();
            List<RskAccessListDetailDto> rskAccessListDetailDtoList = new ArrayList<>();
            List<RskAccessListLogDto> rskAccessListLogDtoList = new ArrayList<>();
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("handleRskAccessListDetailList",entity.getRskAccessListDetailList());
            paramMap.put("listId",entity.getListId());
            //名单生效日、失效日
            RskAccessListDto rskAccessListDto = selectRskAccessList(paramMap);
            entity.setEffdate(rskAccessListDto.getEffdate());
            entity.setDeldate(rskAccessListDto.getDeldate());
            entity.setListType(rskAccessListDto.getListType());
            entity.setListDetailType(rskAccessListDto.getListDetailType());
            //当天历史履历
            List<RskAccessListLogDto>  dayHisAssetLogList =  selectDayHisAssetLog(paramMap);
            List<RskAccessListDetailDto> unhandleRskAccessListDetailDtoList = getAdjPriview(paramMap);
            allrskAccessListDetailList.addAll(entity.getRskAccessListDetailList());
            allrskAccessListDetailList.addAll(unhandleRskAccessListDetailDtoList);
            rskAccessListDetailDtoList.addAll(entity.getRskAccessListDetailList());
            for(RskAccessListDetailDto item : allrskAccessListDetailList){
                RskAccessListLogDto rskAccessListLogDto = new RskAccessListLogDto();
                BeanUtil.copyPropertiesIgnoreNull(rskAccessListLogDto,item);
                /*rskAccessListLogDto.setOperateType(entity.getOperateStyle());*/
                rskAccessListLogDto.setListId(entity.getListId());
                rskAccessListLogDto.setListDate(DateUtil.platDateFromCache());
                rskAccessListLogDto.setListDetailType(entity.getListDetailType());
                rskAccessListLogDtoList.add(rskAccessListLogDto);
            }
            //审批中的履历和当日历史履历比较
            for(RskAccessListLogDto rskAccessListLogDto:rskAccessListLogDtoList){
                for(RskAccessListLogDto dayHisAssetLogDto : dayHisAssetLogList){
                    if(StringUtil.equals(rskAccessListLogDto.getListDetailName(),dayHisAssetLogDto.getListDetailName())
                            &&StringUtil.equals(rskAccessListLogDto.getOperatePs(),DictCodeListExt2.名单操作方向_移出名单.getValue())
                            &&StringUtil.equals(dayHisAssetLogDto.getOperatePs(),DictCodeListExt2.名单操作方向_移入名单.getValue())){
                        rskAccessListLogDto.setOperatePs(DictCodeListExt2.名单操作方向_不变.getValue());
                        break;
                    }
                    if(StringUtil.equals(rskAccessListLogDto.getListDetailName(),dayHisAssetLogDto.getListDetailName())
                            &&StringUtil.equals(rskAccessListLogDto.getOperatePs(),DictCodeListExt2.名单操作方向_移出名单.getValue())
                            &&StringUtil.equals(dayHisAssetLogDto.getOperatePs(),DictCodeListExt2.名单操作方向_移入名单.getValue())){
                        rskAccessListLogDto.setOperatePs(DictCodeListExt2.名单操作方向_不变.getValue());
                        break;
                    }
                }
            }
            entity.setRskAccessListDetailList(rskAccessListDetailDtoList);
            entity.setRskAccessListLogDtoList(rskAccessListLogDtoList);
        }
        else {
            action = "update";
        }
        //删除业务流程表
        this.doDelete(entity.getWorkflowId());
        restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagement/" + action, entity,
            Integer.class);

    }

    public List<RskAccessListDetailDto> getAdjPriview(Map<String, Object> param){
        //未经处理的名单明细
        List<RskAccessListDetailDto> unhandleRskAccessListDetailDtoList = getUnhandleRskAccessListDetailDtoList(param);
        return unhandleRskAccessListDetailDtoList;
    }

    /**
     *  获取未处理的名单明细
     * @param param
     * @return
     */
    public List<RskAccessListDetailDto> getUnhandleRskAccessListDetailDtoList(Map<String, Object> param){
        return restTemplate.postForList(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagementDetail/getUnhandleRskAccessListDetailDtoList",
            param, RskAccessListDetailDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
            "/com/joyintech/tams/invest/risk/namelistmanage/assetnamelist/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    /**
     * 导出模板
     */
    @Override
    public Page<RskAccessListDto> selectPageData(Map<String, Object> selectParam) {
        Page<RskAccessListDto> page = new Page<>();
        List<RskAccessListDto> list = new ArrayList<>();
        page.setRoot(list);
        return page;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param fileName
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public AssetNameManagement004Dto importData(String fileName,String inputType,String listId)
            throws FileNotFoundException, InvocationTargetException, IllegalAccessException {
        //记录错误信息
        AssetNameManagement004Dto detailDto = new AssetNameManagement004Dto();
        //新增条数
        int addNum = 0;
        //修改条数
        int editNum = 0;
        //删除条数
        int deleteNum = 0;
        List<RskAccessListDetailDto> list = new ArrayList<RskAccessListDetailDto>();
        // 检查文件名是否为空
        if (StringUtil.isNotEmpty(fileName)) {
           /*ExcelUtil excelUtil = ExcelUtil.build(FileNameUtil.getUploadFilePath(fileName),
            null);*/
           ExcelUtil excelUtil = ExcelUtil.build(fileName,
                null);
            List<List<String>> dataList = excelUtil.readToList(0, 0, 0);
            //检查读出信息是否为空
            if (null != dataList) {
                //定义一个List封装表里的所有的资产名称
                List<String> nameList=new ArrayList<>();
                //查询表中所有数据
                Map<String, Object> param=new HashMap<>();
                param.put("listDetailType",DictCodeListExt2.名单主体类型_资产.getValue());
                param.put("listId",listId);
                List<RskAccessListDetail> entityList= restTemplate.postForList(
                    ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagement/getEntityList",
                    param, RskAccessListDetail.class);

                for(int i=1; i<dataList.size(); i++){
                    if(StringUtil.isNotEmpty(dataList.get(i))){
                        RskAccessListDetailDto dto=new RskAccessListDetailDto();

                        if(StringUtil.isNotEmpty(dataList.get(i).get(1))){
                            String finprodName=dataList.get(i).get(1);
                            //把表里的资产名称封装进去
                            nameList.add(finprodName);
                            dto.setListDetailName(finprodName);
                            dto.setOldlistDetailName("");
                            param.put("finprodName", finprodName);
                           int count= restTemplate.postForObject(
                                ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagement/getRskEntityByName",
                                param, int.class);
                           if(count < 1){
                               addNum+=1;
                               dto.setOperateType(DictCodeListExt2.名单操作方式_批量.getValue());
                               dto.setOperatePs(DictCodeListExt2.名单操作方向_移入名单.getValue());
                               dto.setListId(listId);
                               dto.setListDetailType(DictCodeListExt2.名单主体类型_资产.getValue());
                           }else{
                               //不变的暂不显示
                               //editNum+=1;
                              // dto.setOperateType(DictCodeListExt2.名单操作方式_批量.getValue());
                               // dto.setOperatePs(DictCodeListExt2.名单操作方向_不变.getValue());
                               //dto.setListId(listId);
                               //dto.setListDetailType(DictCodeListExt2.名单主体类型_资产.getValue());
                               continue;
                           }
                        }
                        if(StringUtil.isNotEmpty(dataList.get(i).get(0))){
                            dto.setListDetailId(dataList.get(i).get(0));
                        }

                        list.add(dto);
                    }

                }
                //全量导入,把库里有，表里没有的数据状态变更为删除
                if(inputType.equals("01")){
                    for (RskAccessListDetail rskAccessListDetail : entityList) {
                        RskAccessListDetailDto dto=new RskAccessListDetailDto();
                        if(!nameList.contains(rskAccessListDetail.getListDetailName())){
                            deleteNum+=1;
                            BeanUtil.copyProperties(dto, rskAccessListDetail);
                            dto.setOldlistDetailName(rskAccessListDetail.getListDetailName());
                            dto.setOperateType(DictCodeListExt2.名单操作方式_批量.getValue());
                            dto.setOperatePs(DictCodeListExt2.名单操作方向_移出名单.getValue());
                            dto.setListId(listId);
                            dto.setListDetailType(DictCodeListExt2.名单主体类型_资产.getValue());
                            list.add(dto);

                        }

                    }
                }
                detailDto.setList(list);
                detailDto.setAddNum(addNum);
                detailDto.setEditNum(editNum);
                detailDto.setDeleteNum(deleteNum);
            }
        }
        return detailDto;
    }

    /**
     * 双击查看
     * @param paramMap
     * @return
     */
    public List<RskAccessListDetailDto> selectAssetListDetail(Map<String,Object> paramMap){
        paramMap.put("pageId","AssetNameManagement003");
        return restTemplate.postForList(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagementDetail/selectAssetListDetail",
            paramMap, RskAccessListDetailDto.class);
    }

    /**
     * 双击查看履历更新
     * @param paramMap
     * @return
     */
    public List<RskAccessListLogDto> selectAssetListDetailLog(Map<String,Object> paramMap){
        paramMap.put("pageId","AssetNameManagement003");
        return restTemplate.postForList(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagementDetail/selectAssetListDetailLog",
            paramMap, RskAccessListLogDto.class);
    }

    /**
     * 双击查看履历明细
     * @param paramMap
     * @return
     */
    public List<RskAccessListLogDto> selectNameListLogDetail(Map<String,Object> paramMap){
        return restTemplate.postForList(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagementDetail/selectNameListLogDetail",
            paramMap, RskAccessListLogDto.class);
    }

    /**
     * 当日历史履历
     * @param paramMap
     * @return
     */
    public List<RskAccessListLogDto> selectDayHisAssetLog(Map<String,Object> paramMap){
        return restTemplate.postForList(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagementDetail/selectDayHisAssetLog",
            paramMap, RskAccessListLogDto.class);
    }

    /**
     * 查看名单
     * @param paramMap
     * @return
     */
    public RskAccessListDto selectRskAccessList(Map<String,Object> paramMap){
        return restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagement/selectRskAccessList",
            paramMap, RskAccessListDto.class);
    }

    /**
     * 模糊下拉框 多选
     * @param param
     * @return
     */
    public List<SelectOptionDto> selectListId(Map<String, Object> param){
        return restTemplate.postForList(
                ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagement/selectListId",
                param, SelectOptionDto.class);
    }

    /**
     * 非一个体系的名单明细
     * @param param
     * @return
     */
    public List<RskAccessListDetailDto> getOtherRskAccessListDetailDtoList(Map<String, Object> param){
        return restTemplate.postForList(
                ServiceApplication.RISK_SERVICE_NAME + "/AssetNameManagementDetail/getOtherRskAccessListDetailDtoList",
                param, RskAccessListDetailDto.class);
    }

}
