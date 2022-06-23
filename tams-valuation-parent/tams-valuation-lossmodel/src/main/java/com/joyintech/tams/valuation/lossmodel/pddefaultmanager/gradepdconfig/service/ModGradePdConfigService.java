/*
 * 文件名：ModGradePdConfigService.java
 * 版权：Copyright 2021-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中邮理财资产管理系统V1.0
 * 修改人：yuzj
 * 修改时间：2021年6月23日
 * 修改内容：新建
 * 系统名称：中邮理财资产管理系统V1.0
 */

package com.joyintech.tams.valuation.lossmodel.pddefaultmanager.gradepdconfig.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import com.joyintech.tams.systemtools.util.ExcelUtil;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.lossmodel.pddefaultmanager.gradepdconfig.dto.ModGradePdConfigDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
//import com.joyintech.tams.invest.common.util.ExcelUtil;
//import com.joyintech.tams.model.common.constant.ServiceApplication;
//import com.joyintech.tams.model.impairment.lgddefultmanager.lgdspecialconfig.dto.LgdSpecialConfigDto;
//import com.joyintech.tams.model.impairment.pddefaultmanager.gradepdconfig.dto.ModGradePdConfigDto;


/**
 * @Description:评级PD维护Service
 * @auther: yuzj
 * @date: 2021/7/1
 * @param:
 * @return:
 */
@Service
public class ModGradePdConfigService extends  CommonExportService<ModGradePdConfigDto> implements BaseProcessService<ModGradePdConfigDto> {
    private static final String TMP_DIR = "/tpl/";


    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(ModGradePdConfigService.class);

    /**
     * @Description:列表分页获取数据
     * @auther: yuzj
     * @date: 2021/7/1
     * @param:
     * @return:
     */
    public Page<ModGradePdConfigDto> selectForPage(Map<String, Object> pageParam) throws Exception {
        Page<ModGradePdConfigDto> pageList = restTemplate.postForPage(ServiceApplication.LOSS_MODEL_SERVICE_NAME+"/ModGradePdConfig/findPage", pageParam, ModGradePdConfigDto.class);
        return pageList;
    }

    /**
     * selectSum
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<ModGradePdConfigDto> selectSum(Map<String, Object> pageParam) {
        return restTemplate.postForPage(ServiceApplication.LOSS_MODEL_SERVICE_NAME+"/ValBalanceStatementQuery001/selectSum", pageParam, ModGradePdConfigDto.class);
    }


    /**
     * 根据主键查询
     * @param
     * @return ModGradePdConfigDto 实体类
     */
    public ModGradePdConfigDto selectByPrimaryKey(ModGradePdConfigDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME +"/ModGradePdConfig/selectByPrimaryKey",
                dto, ModGradePdConfigDto.class);
    }



    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public ModGradePdConfigDto selectByKey(ModGradePdConfigDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME +"/ModGradePdConfig/selectByKey",
                dto,ModGradePdConfigDto.class);
    }
    /**
     * 查询需要导出的数据
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public List<ModGradePdConfigDto> selectExportList(ModGradePdConfigDto dto) {
        List<ModGradePdConfigDto> listAll =
                restTemplate.postForList(ServiceApplication.LOSS_MODEL_SERVICE_NAME+"/ModGradePdConfig/selectExportList",dto,ModGradePdConfigDto.class);
        return listAll;
    }


    /**
     * @Description:工作流，处理数据方法
     * @auther: yuzj
     * @date: 2021/7/1
     * @param: type 操作类型 NEW新增，MODIFY修改，DELETE删除，REVOKE撤销
     * @return:
     */
    @Override
    public void doComplete(ModGradePdConfigDto entity, OperationType type) {
        String action = StringUtil.EMPTY;
        entity.setPdConfigType(DictCodeListExt3.评级PD类型_债券_其他债权.getValue());
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        } else if (OperationType.NEW.equals(type)) {
            action = "insert";
        } else {
            action = "update";
        }
        restTemplate.postForObject(ServiceApplication.LOSS_MODEL_SERVICE_NAME+"/ModGradePdConfig/"+ action,
                entity, Integer.class);

    }


    /**
     * 根据Map获取评级PD信息
     * @return
     */
    public List<ModGradePdConfigDto> selectByMap(Map<String,Object> params){
        List<ModGradePdConfigDto> modGradePdConfigDtoList = restTemplate.getForList(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME + "/ModGradePdConfig/selectByMap"
                        + params,
                ModGradePdConfigDto.class);
        return  modGradePdConfigDtoList;
    }


    /**
     * @Description:判断是否重复
     * @auther: yuzj
     * @date: 2021/7/1
     * @param:
     * @return:
     */
    public Boolean getRepeatCheck(ModGradePdConfigDto dto){
        return restTemplate.postForObject(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME +"/ModGradePdConfig/getRepeatCheck",
                dto,Boolean.class);
    }



    /**
     * 导入数据文件之前检查数据
     *
     * @param fileName 导入的文件名
     * @return 处理结果
     */
    public AjaxResult checkGradePdImportData(String fileName)  throws Exception{
        AjaxResult webResult = new AjaxResult();
        // 防止在进行导入前check时进行了两次的弹框
        webResult.setData("noInfoShowOut");
        //校验文件模板格式
        //check 表头
        List<String> list = Arrays.asList("年份","评级结果","前瞻前年度边际PD%");
//        List<com.joyintech.tams.systemtools.dto.ExcelValidDto> excelValidDtos = com.joyintech.tams.systemtools.util.ExcelUtil.checkExcelTemp(fileName, list);
        String localPath = getclasspath();
        List<List<String>> dataList = ExcelUtil.getTempList(fileName);
//        List<List<String>> dataList = getDataList(fileName,localPath,0);
        //判断模板是否正确(取第一列数据进行判断)
        Boolean isTemplate=true;
        if(dataList.size()>0 && dataList.get(0).size()==3){
            for(int i=0; i<dataList.get(0).size(); i++){
               if(!dataList.get(0).get(i).equals(list.get(i))){
                   isTemplate=false;
                   break;
               }
            }
        }else{
            isTemplate=false;
        }
        if(isTemplate){
            //数据
            if(CollectionUtils.isEmpty(dataList) && dataList.size()<=1){
                webResult.setMessage("导入的是空文件，请重新选择文件导入！",MessageType.错误消息);
                return webResult;
            }

            //1.校验字段必填项
            for(int m=1; m<dataList.size(); m++){
                if(StringUtils.isNotEmpty(check1(dataList.get(m),m))){
                    webResult.setMessage(check1(dataList.get(m),m),MessageType.错误消息);
                    return webResult;
                }
            }

            //1.校验字段合法性
            for(int m=1; m<dataList.size(); m++){
                if(StringUtils.isNotEmpty(check2(dataList.get(m),m))){
                    webResult.setMessage(check2(dataList.get(m),m),MessageType.错误消息);
                    return webResult;
                }
            }




            //检查数据重复性(年份、评级结果)，显示出所有重复的数据
            String messageStr="";
            List<Integer> intOutList = new ArrayList<>();
            for(int m=1; m<dataList.size(); m++){
                List<Integer> intList = new ArrayList<>();
                intList.add(m);
                if(!intOutList.contains(m)){
                    for(int n=m+1; n<dataList.size(); n++ ){
                        if(dataList.get(m).get(0).equals(dataList.get(n).get(0))
                                && dataList.get(m).get(1).equals(dataList.get(n).get(1))){
                            intList.add(n);
                            intOutList.add(m);
                            intOutList.add(n);
                            //errorMsgList.add(new ValidMessage("模板内数据存在重复数据，请调整！第"+m+"行、第"+n+"行" ));
                        }
                    }
                    if(intList.size()>1){
                        StringBuilder builder = new  StringBuilder();
                        for(Integer num:intList){
                            builder.append("第").append(num+1).append("行、");
                        }
                        messageStr = messageStr+builder.substring(0,builder.length()-1)+"<br/>";
                    }
                }


            }
            if(messageStr.indexOf("行")>0){
                webResult.setMessage("模板内数据存在重复数据，请调整！<br/>"+messageStr,MessageType.错误消息);
                return webResult;
            }

            //校验导入数据库中是否存在重复
            List<JoyinMessage> joyinMessages= new ArrayList<>();
            String messages = "";

            for(int m=1; m<dataList.size(); m++){
                ModGradePdConfigDto dto= new ModGradePdConfigDto();
                dto.setVouchYear(dataList.get(m).get(0));
                dto.setGradeResult(dataList.get(m).get(1).split(":")[0]);
                boolean  flag = restTemplate.postForObject(
                        ServiceApplication.LOSS_MODEL_SERVICE_NAME + "/ModGradePdConfig/getRepeatCheck",dto,
                        Boolean.class);
                if(flag==true){
                    /*joyinMessages.add(new JoyinMessage("【"+dataList.get(m).get(0)+"】、【"+
                            dataList.get(m).get(1)+"】、【"+dataList.get(m).get(2)+"】"
                            ,MessageType.提示消息));*/
                    messages=messages+"【"+dataList.get(m).get(0)+"】、【"+
                            dataList.get(m).get(1).split(":")[0]+"】、【"+dataList.get(m).get(2)+"】<br/>";
                }
            }

            if(StringUtils.isNotEmpty(messages)){
                joyinMessages.add(new JoyinMessage("导入数据与库中有重复，是否继续？",MessageType.确认消息));
                webResult.setJoyinMessages(joyinMessages);
                webResult.setData("重复数据如下：<br/>"+messages.substring(0,messages.length()-1));
                return webResult;
            }

        }else{
            webResult.setMessage("数据读入失败，导入模板不一致",MessageType.错误消息);
            return webResult;
        }
        return webResult;
    }

    /**
     * @Description:excel导入功能
     * @auther: yuzj
     * @date: 2021/7/1
     * @param: type (年份、评级结果)重复是否覆盖数据，0否，1是
     * @return:
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ExcelValidDto> importFile(String fileName, String type) throws Exception {
        //数据
        List<List<String>> dataList = ExcelUtil.getTempList(fileName);
//        String localPath = getclasspath();
//        List<List<String>> dataList = getDataList(fileName,localPath,1);
//        List<List<String>> dataList1 = com.joyintech.tams.systemtools.util.ExcelUtil.getDataList(flieName);
        //数据入库
        if(dataList.size()>0){
            List<ModGradePdConfigDto> dtoList = new ArrayList<>();
            for(int i=1; i<dataList.size(); i++){
                ModGradePdConfigDto dto= new ModGradePdConfigDto();
                dto.setVouchYear(dataList.get(i).get(0));
                dto.setGradeResult((dataList.get(i).get(1).split(":"))[0]);//模板格式为 03:AAA-，保存只要 03，所以此处处理
                dto.setForwardYearPd(new BigDecimal(dataList.get(i).get(2)));
                dto.setPdConfigType(DictCodeListExt3.评级PD类型_债券_其他债权.getValue());
                dtoList.add(dto);
            }
            this.saveModGradePdConfigDtoList(dtoList,type);
        }
        return null;
    }

    /**
     *
     * 插入导入的excle到数据库
     * @param type 判断是否有重复数据， 1重复更新 2重复忽略
     * @return int
     * @see
     * @since 1.0
     */
    public void saveModGradePdConfigDtoList(List<ModGradePdConfigDto> dtoList,String type){
        if(StringUtils.isNotEmpty(type) && "1".equals(type)){
            restTemplate.postForObject(
                    ServiceApplication.LOSS_MODEL_SERVICE_NAME + "/ModGradePdConfig/saveCoverModGradePdConfigDtoList",dtoList,
                    Integer.class);
        }else{
            restTemplate.postForObject(
                    ServiceApplication.LOSS_MODEL_SERVICE_NAME + "/ModGradePdConfig/saveModGradePdConfigDtoList",dtoList,
                    Integer.class);
        }

    }


    /**
     *
     * 判断行导入字段是否为空
     * <功能详细描述>
     * @param strList 列数据
     * @return int
     * @see
     * @since 1.0
     */
    public String check1(List<String> strList,int row){
        String errorStr="";
        for(int i=0; i<strList.size(); i++){
            //1.校验数据必填项
            if(StringUtils.isEmpty(strList.get(i))){
                errorStr="导入数据不完整，存在必填项缺失，请调整！";
//                errorStr="导入数据不完整，存在必填项缺失，请调整！行{"+(row+1)+"}列{"+(i+1)+"}";
                return errorStr;
            }

        }
        return  errorStr;
    }

    /**
     *
     * 判断行导入字段是否具有合法性
     * <功能详细描述>
     * @param strList 列数据
     * @return int
     * @see
     * @since 1.0
     */
    public String check2(List<String> strList,int row){
        String errorStr="";
        BigDecimal minDec = new BigDecimal (0);
        BigDecimal maxDec = new BigDecimal (100);
//        for(int i=0; i<strList.size(); i++){
            //2.校验数据合法性
            //校验前瞻前年度边际PD%（小数位后可保留四位，区间为（0，100】，左开右闭）
            if(isNumber(strList.get(2))){
                if(new BigDecimal(strList.get(2)).compareTo(maxDec) >0 || new BigDecimal(strList.get(2)).compareTo(minDec) <=0){
                   errorStr = "导入模板[前瞻前年度边际PD%]数据格式有误！前瞻前年度边际PD%区间应为(0，100]!";
//                    errorStr = "导入模板[前瞻前年度边际PD%]数据格式有误！前瞻前年度边际PD%区间应为(0，100]!"+"行{"+(row+1)+"}列{"+3+"}";
                   return errorStr;
                }
            }else{
                errorStr = "导入模板[前瞻前年度边际PD%]数据格式有误！";
                //errorStr = "导入模板[前瞻前年度边际PD%]数据格式有误！"+"行{"+(row+1)+"}列{"+3+"}";
                return errorStr;
            }
//        }
        return  errorStr;
    }

    /**
     * @Description: 校验是否是数字或者小数
     * @auther: yuzj
     * @date: 2021/7/9
     * @param:
     * @return:
     */
    private boolean isNumber(String str){
        if(StringUtils.isEmpty(str)){
            return false;
        }
        String reg = "\\d+(\\.\\d+)?";
        return str.matches(reg);
    }




    /**
     * 获取导入数据
     * @param fileName 文件路径
     * @param localPath 根路径（windows如D:\ linux为空）
     * @param row 从第几行读取数据
     * @return 列表数据
     */
    public   List<List<String>> getDataList(String fileName,String localPath, int row){
        List<List<String>> dataList = null;
        try {
            String filePath1 = FileNameUtil.getUploadFilePath(fileName);
            boolean bo = isWindowsOS();
            //是否是windows系统，待优化
            if(bo){
                filePath1=localPath+filePath1.substring(22,filePath1.length());
            }else{
                filePath1.substring(22,filePath1.length());
            }
//            String filePath=filePath1.substring(22,filePath1.length());
            dataList = ExcelUtil.readToList(filePath1, null, null, 0, row, 0);
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(),e1);
        }
        return dataList;
    }


    /*
     * @return true---是Windows操作系统
     */
    public static  boolean isWindowsOS(){
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if(osName.toLowerCase().indexOf("windows")>-1){
            isWindowsOS = true;
        }
        return isWindowsOS;
    }
    public  String getclasspath() throws Exception {
        try {
            String strclassname = getClass().getName();
            String strpackagename = "";
            if (getClass().getPackage() != null) {
                strpackagename = getClass().getPackage().getName();

            }
            String strclassfilename = "";

            if (!"".equals(strpackagename)) {
                strclassfilename = strclassname.substring(strpackagename.length() + 1,
                        strclassname.length());
            } else {
                strclassfilename = strclassname;

            }
            getClass().getResource(strclassfilename + ".class");
            String strurl = getClass().getResource(strclassfilename + ".class").toString();
            strurl = strurl.substring(strurl.indexOf('/') + 1, strurl.lastIndexOf('/'));
            //返回当前类的路径，并且处理路径中的空格，因为在路径中出现的空格如果不处理的话，
            //在访问时就会从空格处断开，那么也就取不到完整的信息了，这个问题在web开发中尤其要注意
            String str = strurl.replaceAll("%20", " ");
            boolean bo = isWindowsOS();
            if(bo){
                int index=str.lastIndexOf(":");
                str=str.substring(0,index)+":\\";
            }else{
                str="";
            }
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * 根据配置流水号和配置类型查找是批处理调用过
     * @return
     */
    public int checkBySeqNoAndType(Map<String, Object> map) {
        int count=0;
        count = restTemplate.postForObject(ServiceApplication.LOSS_MODEL_SERVICE_NAME
                + "/ModGradePdConfig/checkBySeqNoAndType",map, Integer.class);
        return count;
    }



}
