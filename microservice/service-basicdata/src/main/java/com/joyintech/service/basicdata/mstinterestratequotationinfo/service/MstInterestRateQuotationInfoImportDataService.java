package com.joyintech.service.basicdata.mstinterestratequotationinfo.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.basicdata.mstcoinfundquotationinfo.dto.MstCoinFundQuotationInfo001Dto;
import com.joyintech.service.basicdata.mstinterestratequotationinfo.dao.MstInterestRateQuotationInfoMapper;
import com.joyintech.service.basicdata.mstinterestratequotationinfo.dto.MstInterestRateQuotationInfoDto;
import com.joyintech.tams.common.base.dao.MstIntRateInfoMapper;
import com.joyintech.tams.common.base.entity.MstExchargeQuote;
import com.joyintech.tams.common.base.entity.MstIntRateInfo;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/***hsgexinghua add by zhangxing1 20200225
 * @auther: 张星1
 * @date: 2020 年 02 月 25
 * @description:
 */
@Service
@Transactional
public class MstInterestRateQuotationInfoImportDataService   extends CommonExportService<MstInterestRateQuotationInfoDto> {

    /**
     * 注入Mapper类自己创建的
     */
    @Autowired
    private MstInterestRateQuotationInfoMapper mstInterestRateQuotationInfoMapper;

    /**
     * 注入继承的Mapper
     */
    @Autowired
    private MstIntRateInfoMapper mstIntRateInfoMapper;
    
    /**
     * 当在做导入前的check就是判断业务数据是否需要覆盖的时候在webresult的data中塞入这个值可以不会弹框两次
     */
    private static final String NO_INFO_SHOWOUT = "noInfoShowOut";


    /**
     * 导入文件
     * <p>
     * 解析excel文件并进行验证
     * </p>
     *

     * @return 导入结果
     */
    public List<ExcelValidDto> importData(List<List<String>> dataList, List<List<String>> tempList,String userName, String orgCode, String deptCode) {
        if (!StringUtil.isEmptyList(dataList)) {
            List<String> headList = new ArrayList<String>();
            headList.add("发布日");
            headList.add("利率名称");
            headList.add("币种");
            headList.add("利率值%");
            headList.add("生效日");
            List<ExcelValidDto> tempCheckResult = ExcelUtil.checkExcelTemp(tempList, headList);
            if (!StringUtil.isEmptyList(tempCheckResult)) {
                return tempCheckResult;
            }
            if (null != dataList) {                 
                    List<MstIntRateInfo> entities = new ArrayList<>(dataList.size());
                    List<ExcelValidDto> checkResult= convertToEntity(dataList, entities,userName,deptCode);
                    if(checkResult.isEmpty()) {
    	                for(MstIntRateInfo dto : entities){
    	                	mstIntRateInfoMapper.insert(dto);
    	                }
                    }
                    return checkResult;
            }
        }else{
        	List<ExcelValidDto> checkResult = new ArrayList<>();
        	ExcelValidDto excelValidDto = new ExcelValidDto();
        	String errorMsg = "模板为空，请输入数据！";
        	excelValidDto.setErrorInfo(errorMsg);
        	checkResult.add(excelValidDto);
        	return checkResult;
        }
        return null;
    }

    /**
     * excel数据转为java对象
     * <p> 将excel导入的数据，按行转为ProRegistCodeManage001Dto  FinProductValuation对象，同时验证各字段值</p>
     *
     * @param dataList excel导入的数据
     * @param entities 转化后的列表
     * @param box
     * @return 验证信息
     */
    private List<ExcelValidDto> convertToEntity(List<List<String>> dataList,List<MstIntRateInfo> entities,String userName,String deptCode) {
        List<ExcelValidDto> checkResult = new ArrayList<>();
        Date sysDate = DateUtil.platDateFromCache();// 获取平台日期
        Date nowDate = new Date();
        for (int i = 0; i < dataList.size(); i++ ) {
        	//是否正常标志
        	boolean flag = true;
        	String ccy = "";
            List<String> list = dataList.get(i);
            // 验证消息
            List<String> message = new ArrayList<>();
            Map<String,Object> param = new HashMap<String,Object>();
            //利率名称
            String intRateId = "";
            //发布日
            if(list.get(0) == null || list.get(0).trim().equals("")){
            	flag = false;
            	message.add("发布日期不能为空");
            }else{
            	if (!checkDate(list.get(0))) {
            		flag = false;
                	message.add("发布日期格式不正确");
                }else{
                	if(DateUtil.compareDate(DateUtil.parse(list.get(0)), DateUtil.platDateFromCache()) == -1){
                		flag = false;
                    	message.add("发布日应大于等于平台日期");
                	}
                }
            }           
            // 利率名称
            if(list.get(1) == null || list.get(1).trim().equals("")){
            	flag = false;
            	message.add("利率名称不能为空");
            }else{
                param.put("SECRATE_DATA", DictCode.利率行情.getValue());
                param.put("IntRateName", list.get(1));
                MstInterestRateQuotationInfoDto  dto =    mstInterestRateQuotationInfoMapper.getIntRateIdByName(param);
                if(dto != null){
                	intRateId = dto.getIntRateId();	
                }else{
                	flag = false;
                    message.add("利率名称错误");
                }
            }
            // 币种
            if(list.get(2) == null || list.get(2).trim().equals("")){
            	flag = false;
            	message.add("币种不能为空");
            }else{
            	if(list.get(2).length() < 2){
            		flag = false;
                	message.add("币种输入不正确");
            	}else{
                    param.put("ccy", list.get(2));
                    ccy =    mstInterestRateQuotationInfoMapper.checkCcy(param);
                    if(ccy == null){
                    	flag = false;
                    	message.add("币种输入不正确");
                    }
            	}
            }

            //利率值
            if(list.get(3) == null || list.get(3).trim().equals("")){
            	flag = false;
            	message.add("利率值不能为空");
            }else{
            	if(!checkFigure(list.get(3))){
            		flag = false;
                	message.add("利率值不是数字");
            	}
            }
            // 生效日
            if(list.get(4) == null || list.get(4).trim().equals("")){
            	flag = false;
            	message.add("生效日期不能为空");
            }else{
            	if (!checkDate(list.get(4))) {
            		flag = false;
                	message.add("生效日期格式不正确");
                } 
            }               
            //主键校验
            if(flag){
            	param.put("intRateId", intRateId);
            	param.put("effDate", DateUtil.parse(list.get(4)));
                String result  = mstInterestRateQuotationInfoMapper.getRegistExcelList(param);
            	if(StringUtils.isNotEmpty(result)){
            		flag = false;
            		message.add("存在和利率名称、生效日相同的数据");
            	}                    	
            }
                
            if(flag){
            	MstIntRateInfo entity = new MstIntRateInfo();
                //发布日
            	entity.setRelaeseDate(DateUtil.parse(list.get(0)));                        
                // 利率名称
            	entity.setIntRateId(intRateId);
                // 币种
                entity.setCcy(ccy);                         
                //利率值
            	entity.setIntRate(new BigDecimal(list.get(3)).divide(new BigDecimal(100)));
                // 生效日
            	entity.setEffDate(DateUtil.parse(list.get(4)));
            	//利率类型
            	entity.setIntRateType(DictCodeListExt1.基准类型_2_利率.getValue());
            	entity.setInputType(DictCodeListExt1.录入方式_系统导入.getValue());             
                entity.setCreateUser(userName);
                entity.setCreateDept(deptCode);               
                entity.setCreateTime(nowDate);
                entity.setUpdateUser(userName);
                entity.setUpdateTime(nowDate);   
                entities.add(entity);
            }

            if (CollectionUtils.isNotEmpty(message)) {
                checkResult.add(new ExcelValidDto(list.get(1), String.valueOf(i+2),
                        StringUtil.joinString(message, GlobalConstant.SEMICOLON)));
            }
        }
        return checkResult;
    }


    /**
     * 导入文件之前检查文件 导入的excel是否有重复数据
     *
     * param fileName 导入的文件名名
     * @return 检查结果
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirmMsgForImportData(List<List<String>> dataList) {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();        
    	//判断excel表中利率名称和生效日期是否冲突
    	for (int i = 0; i < dataList.size(); i++ ) {
    		for (int j = i+1; j < dataList.size(); j++ ) {
    			if(StringUtils.equals(dataList.get(i).get(1), dataList.get(j).get(1)) &&
    					StringUtils.equals(dataList.get(i).get(4), dataList.get(j).get(4))){
    				ValidMessage validMessage = new ValidMessage("第"+(i+2)+"行利率名称、生效日期与"+"第"+(j+2)+"行利率名称、生效日期相同");
                    errorList.add(validMessage);
    			}
    		}
    	}
    	//判断excel表中同一发布日不能存在重复的有效状态的数据
//    	for (int i = 0; i < dataList.size(); i++ ) {
//    		for (int j = i+1; j < dataList.size(); j++ ) {
//    			if(StringUtils.equals(dataList.get(i).get(0), dataList.get(j).get(0))){
//    				ValidMessage validMessage = new ValidMessage("第"+(i+2)+"行发布日与"+"第"+(j+2)+"行发布日相同");
//                    errorList.add(validMessage);
//    			}
//    		}
//    	}

        if(errorList.size() == 0){
        	webResult.setData(NO_INFO_SHOWOUT);
        }else{
        	webResult.setErrorList(errorList);
        }
        return webResult;
    }
    
    /**
     * 判断是否为数字
     *
     * @param 
     * @return 返回boolean
     */
    private boolean checkFigure(String figure) {
    	try{
    		new BigDecimal(figure);
    		return true;
    	}catch(Exception e){
    		return false;
    	}
    }
    
    /**
     * 日期格式校验
     *
     * @param 
     * @return 返回boolean
     */
    private boolean checkDate(String date) {
    	boolean flag = true;
    	if(!DateUtil.isDateStr(date, DateUtil.DEFAULT_OTHER_FORMAT,
                DateUtil.DEFAULT_ANOTHER_FORMAT,DateUtil.DEFAULT_DATE_FORMAT_EN)){
        	flag = false;
        }else{
        	date = date.replace("-", "").replace("/", "");
        	//年
        	int year = Integer.valueOf(date.substring(0, 4));
        	//月
        	int month = Integer.valueOf(date.substring(4, 6));
        	//日
        	int day = Integer.valueOf(date.substring(6));
        	if(month < 1 || month > 12){
        		flag = false;
        	}else{
        		if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
        			if(day < 1 || day > 31){
        				flag = false;
        			}
        		}else if(month == 4 || month == 6 || month == 9 || month == 11){
        			if(day < 1 || day > 30){
        				flag = false;
        			}
        		}else if(month == 2){
        			if((year % 4 == 0 && year % 100 != 0) ||year % 400 == 0){
        				if(day < 1 || day > 29){
            				flag = false;
            			}
        			}else{
        				if(day < 1 || day > 28){
            				flag = false;
            			}
        			}
        		}
        	}
        }
    	return flag;
    }

}
