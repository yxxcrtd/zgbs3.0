/*
 * 文件名：RskAuthorityManageService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.risk.rskauthoritymanage.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.risk.rskauthoritymanage.dto.RskAuthorityManageDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年11月30日
 * @see RskAuthorityManageService
 * @since 1.0
 */
@Service
public class RskAuthorityManageService  extends CommonExportService<RskAuthorityManageDto>
    implements BaseProcessService<RskAuthorityManageDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(RskAuthorityManageService.class);

    /**
     * rskAuthorityManageService
     */
    @Autowired
    private RskAuthorityManageService rskAuthorityManageService;
    /**
     * 业务标号生成共通
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;
    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(RskAuthorityManageDto entity, OperationType type) {
        String action = StringUtil.EMPTY;
        // entity.setSystemCode("SYS");
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        }
        else if (OperationType.NEW.equals(type)) {
            List<RskAuthorityManageDto> list = getListDto(entity);
            List<RskAuthorityManageDto> listDto = new ArrayList<RskAuthorityManageDto>();
            for(RskAuthorityManageDto d:list ){
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共通业务类型_授权管理);
                String code = baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(
                    param) : IDGenerator.generate();
                d.setSeqNo(code);
                listDto.add(d);
            }
            entity.setSelectedItem(listDto);
            action = "insert";
        }
        else {
            List<RskAuthorityManageDto> list = getListDto(entity);
            for(RskAuthorityManageDto d:list ){
                d.setSeqNo(entity.getSeqNo());
            }
            entity = null;
            entity = list.get(0);
            action = "update";
        }
        restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME + "/RskAuthorityManage/" + action, entity,
            Integer.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getUser(Map<String, Object> pageParam) {
        return restTemplate.postForPage(
            ServiceApplication.RISK_SERVICE_NAME +
            "/RskAuthorityManage/getUser",pageParam,
            SelectOptionDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<RskAuthorityManageDto> selectForPage(Map<String, Object> pageParam) {
        return restTemplate.postForPage(
            ServiceApplication.RISK_SERVICE_NAME +
            "/RskAuthorityManage/findPage",
            pageParam, RskAuthorityManageDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public RskAuthorityManageDto selectBySecNo(RskAuthorityManageDto dto) {
        return restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME + "/RskAuthorityManage/selectBySecNo",
            dto, RskAuthorityManageDto.class);
    }

    /**
     * 检查是否有流程中的资产交易数据
     * @param rsklist
     * @return
     * @see
     * @since 1.0
     */
    public int selectAssetTrade(List<RskAuthorityManageDto> rsklist) {
    	int rsknum =0;
        Map<String, Object> param = new HashMap<String, Object>();
        if(rsklist!=null && rsklist.size()>0){
            for (RskAuthorityManageDto e : rsklist) {
            	param.put("ASSET_TYPE", e.getAssetType());
            	param.put("ASSET_SEC_TYPE", e.getAssetSecType());
                param.put("RISK_TYPE", DictCodeListExt2.配置类型_授权分类_校验用.getValue());
            	rsknum +=  restTemplate.postForObject(
            	            ServiceApplication.RISK_SERVICE_NAME + "/RskAuthorityManage/selectAssetTrade",
            	            param, Integer.class);
            }
        }
        return rsknum;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<RskAuthorityManageDto> select(Map<String, Object> pageParam) {
        String workflowId=(String)pageParam.get("workflowId");
        Page<RskAuthorityManageDto> page = new Page<>();
        //进入代办之后数据需要用workflowId找值，数据此时并没有加到数据库里面
        if(StringUtil.isNotEmpty(workflowId)){
            RskAuthorityManageDto dto = this.getBussinessEntityByWorkflowId(
                workflowId);
            List<RskAuthorityManageDto> list = new ArrayList<RskAuthorityManageDto>();
            if(dto.getSelectedItem() != null){
                list=dto.getSelectedItem();
            }
            else{
                list.add(dto); 
            }
            page.setRoot(list);
            page.setRecords(list.size());
        }
        else{
            RskAuthorityManageDto dto = new RskAuthorityManageDto();
            if(pageParam.get("seqNo") != null && !"".equals((String)pageParam.get("seqNo"))){
                dto.setSeqNo((String)pageParam.get("seqNo"));
                RskAuthorityManageDto entity = rskAuthorityManageService.selectBySecNo(dto);
                List<RskAuthorityManageDto> list = new ArrayList<RskAuthorityManageDto>();
                if(entity != null){
                    if(entity.getSelectedItem() != null){
                        list=entity.getSelectedItem();
                    }
                    else{
                        list.add(entity); 
                    }
                    page.setRoot(list);
                    page.setRecords(list.size());
                }
            }
        }
        return page;
    }
    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
            "/com/joyintech/tams/invest/risk/rskauthoritymanage/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RskAuthorityManageDto> selectPageData(Map<String, Object> selectParam) {
        try {
            return selectForPage(selectParam);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public List<RskAuthorityManageDto> selectByDto(RskAuthorityManageDto entity) {
        return restTemplate.postForList(
            ServiceApplication.RISK_SERVICE_NAME +
            "/RskAuthorityManage/selectByDto",entity,
            RskAuthorityManageDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public List<RskAuthorityManageDto> getListDto(RskAuthorityManageDto dto) {
        List<String> userNames = StringUtil.splitToList(GlobalConstant.COMMA,dto.getUserName());
        List<RskAuthorityManageDto> list = dto.getSelectedItem();
        List<RskAuthorityManageDto> listDto = new ArrayList<RskAuthorityManageDto>();
        if(list != null && list.size()>0){
            for(RskAuthorityManageDto e:list){
                for(String userName:userNames){
                    RskAuthorityManageDto temp = new RskAuthorityManageDto();
                    BeanUtil.copyPropertiesIgnoreNull(temp, e);
                    temp.setRolePos(dto.getRolePos());
                    temp.setUserName(userName);
                    listDto.add(temp);
                }
            }
        }
        return listDto;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<RskAuthorityManageDto> getDtoByUserName(Map<String,Object> map) {
        return restTemplate.postForList(
            ServiceApplication.RISK_SERVICE_NAME +
            "/RskAuthorityManage/getDtoByUserName",map,
            RskAuthorityManageDto.class);
    }
    /**
     * 根据用户名查询真实的名字
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String selectRealNameByUserName(String userName){
        return restTemplate.getForObject(
            ServiceApplication.RISK_SERVICE_NAME +
            "/RskAuthorityManage/selectRealNameByUserName?userName="+userName,
            String.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    public boolean checkEdit(RskAuthorityManageDto dto) {
        List<RskAuthorityManageDto> newListDto = dto.getSelectedItem();
        boolean flag = false;
      //判断数据库中用户的授权是否重复
        List<String> userNames = StringUtil.splitToList(GlobalConstant.COMMA,dto.getUserName());
        if(userNames != null && userNames.size()>0 && newListDto != null && newListDto.size()>0){
            for(String userName:userNames){
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("userName", userName);
                map.put("rolePos", dto.getRolePos());
                List<RskAuthorityManageDto> lDto = rskAuthorityManageService.getDtoByUserName(map);
                if(lDto != null && lDto.size()>0){
                    for(int i=0;i < lDto.size();i++){
                        if(flag){
                            break;
                        }
                        for(int j=0;j<newListDto.size();j++){
                            if(newListDto.get(j).getAssetType() != null 
                                && !StringUtil.equals("", newListDto.get(j).getAssetType())){
                              //如果数据库里的数据与页面数据重复
                                flag = checkDtoIsConflict(lDto.get(i),newListDto.get(j)) &&(
                                    dto.getSeqNo() ==null ||!StringUtil.equals(lDto.get(i).getSeqNo(), dto.getSeqNo()));
                                if(flag){
                                    break;
                                }
                            }
                        }
                    }
                }
                /*if(flag){
                    String realName = rskAuthorityManageService.selectRealNameByUserName(userName);
                    //message
                    //resultList.add(new ValidMessage(this.getMessage("RSK_W_0028",getMessage("RskAuthorityManage001.userName")+":"+realName)));
                }*/
            }
        }
        return flag;
    }
    /**
     * 检查list的合法性
     * <功能详细描述>
     * @param tempList 待检查list
     * @return check结果
     * @see 
     * @since 1.0
     */
    public boolean checkList(List<RskAuthorityManageDto> tempList) {
        boolean flag = false;
        if(tempList.size()<2){
            return false;
        }
        else{
            for(int i = 0;i<tempList.size();i++){
                for(int j = i+1;j<tempList.size();j++){
                    if(checkDtoIsConflict(tempList.get(i),tempList.get(j))){
                        return true;
                    };
                }
            }
        }
        return flag;
    }
    /**
     * 
     * check数据知否重复
     * <功能详细描述>
     * @param dto1
     * @param dto2
     * @return
     * @see
     * @since 1.0
     */
    public boolean checkDtoIsConflict(RskAuthorityManageDto dto1,RskAuthorityManageDto dto2){
        //如果是修改，且是自身seqNo,则不为重复
        if(StringUtil.isNotEmpty(dto1.getSeqNo()) && StringUtil.isNotEmpty(dto2.getSeqNo()) && dto1.getSeqNo().equals(dto2.getSeqNo())){
            return false;
        }
        //第一级返回false再判断第二级，第二级返回false再判断第三级
        if(isNotSame(dto1.getRolePos(), dto2.getRolePos())||isNotSame(dto1.getUserName(), dto2.getUserName())
            ||isNotSame(dto1.getAssetType(), dto2.getAssetType())||isNotSame(dto1.getAssetSecType(),dto2.getAssetSecType())||isNotSame(dto1.getTradeMarket(),dto2.getTradeMarket())
            ||isNotSame(dto1.getTradeDirection(),dto2.getTradeDirection())){
            return false;
        }
        return true;
    }
    
    public boolean isNotSame(String str1,String str2){
        //如果Str1与str2不相等且str1和str2都不为空则返回true
        return !StringUtil.equals(str1, str2)&&StringUtil.isNotEmpty(str1)&&StringUtil.isNotEmpty(str2);
    }
    
    /**
     * 
     * 根据pageId查询所有流程中的数据逐条进行对比
     * <功能详细描述>
     * @param parameter
     * @return
     * @see
     * @since 1.0
     */
    public List<RskAuthorityManageDto> getBusinessData(WflValidInputParameter parameter){
        return restTemplate.postForList(
            ServiceApplication.RISK_SERVICE_NAME +
            "/RskAuthorityManage/getBusinessData",parameter,
            RskAuthorityManageDto.class);
    }
}
