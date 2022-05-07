/*
 * 文件名：SysInstRegBankCode001Service.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：刘二通
 * 修改时间：2018年5月18日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.tams.report.system.instregbankcode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joyintech.tams.constant.DictCode;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.base.entity.SysInstitution;
import com.joyintech.system.institution.dto.SysInstitutionDto;
import com.joyintech.system.institution.service.InstitutionService;
import com.joyintech.tams.common.base.dao.ChbOrgRegistBankCodeMapper;
import com.joyintech.tams.common.base.entity.ChbOrgRegistBankCode;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.report.common.util.ReportGlobalConstant;
import com.joyintech.tams.report.submitmaintenance.equityinfo.validate.EquityInfoValid;
import com.joyintech.tams.report.system.instregbankcode.dao.SysInstRegBankCode001Mapper;
import com.joyintech.tams.report.system.instregbankcode.dto.SysInstRegBankCode001Dto;
import com.joyintech.tams.report.submitmaintenance.transformrule.dto.ChbTransformRuleObjDto;
import com.joyintech.tams.report.submitmaintenance.transformrule.dto.TransformRule001Dto;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tools.collectors.JoyinCollectors;

/**
 * 机构登记银行代码管理画面用service
 *
 * @author 刘二通
 * @version 1.0 2018年5月18日
 * @see SysInstRegBankCode001Service
 * @since 1.0
 */
@Service
public class SysInstRegBankCode001Service extends CommonExportService<SysInstRegBankCode001Dto> {

    /**
     * 机构管理service
     */
    @Autowired(required = false)
    private InstitutionService institutionService;
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * check
     */
    @Autowired
    private EquityInfoValid equityInfoValid;

    /**
     * SysInstRegBankCode001Mapper
     */
    @Autowired
    private SysInstRegBankCode001Mapper sysInstRegBankCode001Mapper;

    /**
     * ChbOrgRegistBankCodeMapper
     */
    @Autowired
    private ChbOrgRegistBankCodeMapper chbOrgRegistBankCodeMapper;

    /**
     * 产品登记编码信息列表
     *
     * @param param 查询参数
     * @return 产品登记编码信息列表
     */
    public Page<SysInstRegBankCode001Dto> searchForPage(Map<String, Object> param) {
        String orgCode = (String)param.get("orgCode");
        List<String> orgCodeList = StringUtil.splitToList(GlobalConstant.COMMA, orgCode);
        if (StringUtil.isBlank(orgCode)) {
            orgCodeList = CommonUtil.getOrgCodeListForSearch();
        }
        param.put("orgCodeList", orgCodeList);
        int pagenum = Integer.parseInt(param.get(GlobalConstant.PARAM_PAGE).toString());
	    int legth = Integer.parseInt(param.get(GlobalConstant.PARAM_LENGTH).toString());
        Page<SysInstRegBankCode001Dto> page = super.queryForPage(
        	pagenum,legth,()->sysInstRegBankCode001Mapper.queryForPage(param));
        this.formatSelectResult(page.getRoot());
        return page;
    }

    /**
     * 格式化查询结果
     *
     * @param dataList 查询结果列表
     * @see
     * @since 1.0
     */
    private void formatSelectResult(List<SysInstRegBankCode001Dto> dataList) {
        if (StringUtil.isEmptyList(dataList)) {
            return;
        }

        // 取得系统中所有机构
        List<SuggestDto> institutionList = institutionService.getAllInstitutionCode(null);
/*
        List<SysInstitutionDto> sysInstitutionDtoList = institutionService.selectAllList(null);
*/
        Map<String, String> institutionMap = new HashMap<String, String>();
/*
        Map<String, String> instituteMap = new HashMap<String, String>();
*/
        if (!StringUtil.isEmptyList(institutionList)) {
            institutionMap = institutionList.stream().collect(
                JoyinCollectors.toMap(x -> x.getCode(), x -> x.getName()));
        }
        /*if (!StringUtil.isEmptyList(sysInstitutionDtoList)) {
        	instituteMap = sysInstitutionDtoList.stream().collect(
        			JoyinCollectors.toMap(x -> x.getInstitutionCode(), x -> x.getInsitituteCode()));
        }*/
        for (SysInstRegBankCode001Dto dataDto : dataList) {
        	if ("1".equals(dataDto.getIsFinSubCompany())) {
        		dataDto.setIsFinSubCompany("是");
        	} else if ("2".equals(dataDto.getIsFinSubCompany())) {
        		dataDto.setIsFinSubCompany("否");
        	}
            if (institutionMap.containsKey(dataDto.getOrgCode())) {
                dataDto.setOrgName(institutionMap.get(dataDto.getOrgCode()));
            }


            if(!StringUtil.isEmpty(dataDto.getBelogCodeCBRC())){
                dataDto.setBelogCodeCBRC(dataDto.getBelogCodeCBRC().trim()+" "+SvrDataUtil.getNameFromDataDict(DictCode.监管机构, dataDto.getBelogCodeCBRC()));
            }
          /*  if (instituteMap.containsKey(dataDto.getOrgCode())) {
            	dataDto.setInsitituteCode(instituteMap.get(dataDto.getOrgCode()));
            }*/
        }
    }

    /**
     * 导入文件之前检查文件中的数据在数据库中是否已经存在 如果已经存在，需要提示用户进行确认是否覆盖数据库
     *
     * @param fileName 导入的文件名名
     * @return 检查结果
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirmMsgForImportData(List<List<String>> dataList) {
        AjaxResult webResult = new AjaxResult();

        if (!StringUtil.isEmptyList(dataList)) {
            if (null != dataList) {
                List<SysInstRegBankCode001Dto> entities = new ArrayList<>(dataList.size());
                List<ExcelValidDto> checkResult = convertToEntity(dataList, entities);
                if (checkResult.isEmpty() && CollectionUtils.isNotEmpty(entities)) {
                    int dataCount = sysInstRegBankCode001Mapper.selectDataCount(entities);
                    if (dataCount > 0) {
                        webResult.setMessage(this.getMessage("REPORT_ASSET_C_0003"), MessageType.确认消息);
                    }
                    else {
                        webResult.setData(ReportGlobalConstant.NO_INFO_SHOWOUT);
                    }
                }
                else {
                    webResult.setData(ReportGlobalConstant.NO_INFO_SHOWOUT);
                }
            }
            else {
                webResult.setData(ReportGlobalConstant.NO_INFO_SHOWOUT);
            }
        }

        return webResult;
    }

    /**
     * excel数据转为java对象
     * <p>
     * 将excel导入的数据，按行转为Exchangerate001Dto对象，同时验证各字段值
     * </p>
     *
     * @param dataList excel导入的数据
     * @param entities 转化后的列表
     * @return 验证信息
     */
    private List<ExcelValidDto> convertToEntity(List<List<String>> dataList,
                                                List<SysInstRegBankCode001Dto> entities) {
        List<ExcelValidDto> checkResult = new ArrayList<>();

        // 取得系统中所有机构
        List<SuggestDto> institutionList = institutionService.getInstitutionCode(null);
        Map<String, String> institutionMap = new HashMap<String, String>();
        if (!StringUtil.isEmptyList(institutionList)) {
            institutionMap = institutionList.stream().collect(
                JoyinCollectors.toMap(x -> x.getCode(), x -> x.getName()));
        }

        for (int i = 0; i < dataList.size(); i++ ) {
            List<String> list = dataList.get(i);
            if (null == list || list.size() != 6) {
                checkResult.add(new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i + 1),
                    this.getMessage("REPORT_COMMON_FILE_E_0001", "导入行内容")));
                continue;
            }

            SysInstRegBankCode001Dto entity = new SysInstRegBankCode001Dto();

            // 验证消息
            List<String> messageList = new ArrayList<>();

            // 机构代码
            String orgCode = list.get(0);
            if (StringUtil.isEmpty(orgCode)) {
                messageList.add(this.getMessage("REPORT_COMMON_E_0057", "机构代码"));
            }
            else if (!institutionMap.containsKey(orgCode)) {
                messageList.add(this.getMessage("REPORT_E_0015", "机构代码"));
            }

            // 登记银行代码
            String registBankCode = list.get(2);
            Map<String,TransformRule001Dto> spzMap=new HashMap<String,TransformRule001Dto>();
            Map<String,List<ChbTransformRuleObjDto>> objFiledsMap=new HashMap<String,List<ChbTransformRuleObjDto>>();
            equityInfoValid.checkBankCodeNew(messageList, registBankCode,spzMap,objFiledsMap,null);
            // 总行机构代码
            String pbcBankCode = list.get(3);
            equityInfoValid.checkPbcBankCode(messageList, pbcBankCode);
            // 是否理财子公司
            String isFinSubCompany = list.get(4);
            if (StringUtil.isEmpty(isFinSubCompany)) {
                messageList.add(this.getMessage("REPORT_COMMON_E_0057", "是否理财子公司"));
            }else{
                if(!StringUtil.equals(isFinSubCompany,"1") && !StringUtil.equals(isFinSubCompany,"2") ){
                    messageList.add(this.getMessage("REPORT_COMMON_E_0112"));
                }
            }

            //社会信用代码
            String insitituteCode = list.get(5);
            if (StringUtil.isEmpty(insitituteCode)) {
                messageList.add(this.getMessage("REPORT_COMMON_E_0057", "社会信用代码"));
            }

            //TISHKBS-2372 社会信用代码要求只能是18位的数字或大写英文组合 add by wangyaning 20210625 start
            if (insitituteCode.length() != 18){
                messageList.add(this.getMessage("REPORT_COMMON_E_0056", "社会信用代码"));
            }
            else {
                Pattern p = Pattern.compile("[^A-Z 0-9]");
                Matcher m = p.matcher(insitituteCode);
                boolean match = m.find();
                if(match){
                    messageList.add(this.getMessage("REPORT_COMMON_E_0060", "社会信用代码"));
                }
            }
            //TISHKBS-2372 社会信用代码要求只能是18位的数字或大写英文组合 add by wangyaning 20210625 end

            /******所属银保监局不可为空**/
            String belogCodeCBRC = list.get(6);
            if (StringUtil.isEmpty(belogCodeCBRC)) {
                messageList.add(this.getMessage("REPORT_COMMON_E_0057", "所属银保监局"));
            }

            if (CollectionUtils.isNotEmpty(messageList)) {
                checkResult.add(new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i + 2),
                    StringUtil.joinString(messageList, "<br>")));
            }
            else {
                entity.setOrgCode(orgCode);
                entity.setRegistBankCode(registBankCode);
                entity.setCreateDept(orgCode);
                /*机构登记银行代码管理增加总行机构代码和是否理财子公司字段 add by gaojian 2019-06-19 begin*/
                entity.setPbcBankCode(pbcBankCode);
                entity.setIsFinSubCompany(isFinSubCompany);
                /*机构登记银行代码管理增加总行机构代码和是否理财子公司字段 add by gaojian 2019-06-19 end*/
                entity.setInsitituteCode(insitituteCode);
                /****新增所属银保监局字段   20220307 leipeng**/
                entity.setBelogCodeCBRC(belogCodeCBRC);
                /****新增所属银保监局字段   20220307 leipeng**/

                entities.add(entity);
            }
        }
        return checkResult;
    }

    /**
     *
     * @param dataList
     * @return
     */
    public List<ExcelValidDto> importData(List<List<String>> dataList) {
        if (!StringUtil.isEmptyList(dataList)) {
            if (null != dataList) {
                List<SysInstRegBankCode001Dto> entities = new ArrayList<>(dataList.size());
                List<ExcelValidDto> checkResult = convertToEntity(dataList, entities);
                if(StringUtil.isEmptyList(checkResult) && !StringUtil.isEmptyList(entities)) {
                    for (SysInstRegBankCode001Dto dataDto : entities) {
                        ChbOrgRegistBankCode chbOrgRegistBankCode = new ChbOrgRegistBankCode();
                        chbOrgRegistBankCode.setOrgCode(dataDto.getOrgCode());
                        chbOrgRegistBankCode.setRegistBankCode(dataDto.getRegistBankCode());
                        chbOrgRegistBankCode.setCreateDept(dataDto.getOrgCode());
                        chbOrgRegistBankCode.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
                        /*机构登记银行代码管理增加总行机构代码和是否理财子公司字段 add by gaojian 2019-06-05 begin*/
                        chbOrgRegistBankCode.setPbcBankCode(dataDto.getPbcBankCode());
                        chbOrgRegistBankCode.setIsFinSubCompany(dataDto.getIsFinSubCompany());
                        /*机构登记银行代码管理增加总行机构代码和是否理财子公司字段 add by gaojian 2019-06-05 end*/
                        chbOrgRegistBankCode.setInsitituteCode(dataDto.getInsitituteCode());

                        /****新增所属银保监局字段   20220307 leipeng**/
                        chbOrgRegistBankCode.setBelogCodeCBRC(dataDto.getBelogCodeCBRC());
                        /****新增所属银保监局字段   20220307 leipeng**/

                        try {
                            chbOrgRegistBankCodeMapper.insert(chbOrgRegistBankCode);
                        }
                        catch (Exception e) {
                            chbOrgRegistBankCodeMapper.updateByPrimaryKey(chbOrgRegistBankCode);
                        }
                    }
                }
                return checkResult;
            }
        }
        return null;
    }

    /**
     * 查询所有内容，不分页
     * <p>导出功能需要导出查询条件的所有内容</p>
     * @param param 画面查询参数
     * @return 查询结果列表
     */
    public List<SysInstRegBankCode001Dto> queryExportList(Map<String, Object> param) {
        String keys = (String)param.get("keys");
        param.put("keys", StringUtil.splitToList(GlobalConstant.COMMA, keys));

        String orgCode = (String)param.get("orgCode");
        List<String> orgCodeList = StringUtil.splitToList(GlobalConstant.COMMA, orgCode);
        if (StringUtil.isBlank(orgCode)) {
            orgCodeList = CommonUtil.getOrgCodeListForSearch();
        }
        param.put("orgCodeList", orgCodeList);

        List<SysInstRegBankCode001Dto> pageList = sysInstRegBankCode001Mapper.queryForPage(param);
        this.formatSelectResult(pageList);

        return pageList;
    }

    /**
     * 删除数据
     *
     * @param keyStr 主键字符串
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public void doDelete(String keyStr) {
        if (StringUtil.isNotEmpty(keyStr)) {
            List<String> keys = StringUtil.splitToList(GlobalConstant.COMMA, keyStr);
            keys.forEach(key -> {
                chbOrgRegistBankCodeMapper.deleteByPrimaryKey(key);
            });
        }
    }
    
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public List<SysInstitutionDto> selectAllInstitutionList(){
    	return institutionService.selectAllList(null);
    }
    
    
    public String[] getRegistCode(String orgCode){
        String issuePartyId = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID);
//        String issuePartyId = CommonUtil.selectBankCode();
        String  issuePartyName = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_NAME);
//        String  issuePartyName=CommonUtil.selectBankCodeName();
        // 发行机构代码取值
        if(null!=orgCode){
        	Map<String,Object> param = new HashMap<>();
        	List<String> orgCodeList = new ArrayList<>();
        	orgCodeList.add(orgCode);
        	param.put("orgCodeList", orgCodeList);
        	List<SysInstRegBankCode001Dto> list = sysInstRegBankCode001Mapper.queryForPage(param);
        	
        	if(null!=list&&list.size()>0){
        		issuePartyId = list.get(0).getRegistBankCode();
        		SysInstitution dtp = (SysInstitution) CommonUtil.queryCode(GlobalConstant.SYS_INSTITUTION_KEY,orgCode);

        		issuePartyName =  null!=dtp?dtp.getInstitutionName():issuePartyName;
        	}
        }
		return new String[]{issuePartyId,issuePartyName};
	}
    
}