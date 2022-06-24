package com.joyintech.service.basicdata.mstbondquotationinfo.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.basicdata.mstbondquotationinfo.dao.MstBondQuotationInfoMapper;
import com.joyintech.service.basicdata.mstbondquotationinfo.dto.MstBondQuotationInfoDto;
import com.joyintech.service.basicdata.mstcoinfundquotationinfo.dto.MstCoinFundQuotationInfo001Dto;
import com.joyintech.service.basicdata.mstinterestratequotationinfo.dto.MstInterestRateQuotationInfoDto;
import com.joyintech.tams.common.base.dao.MstBondValuationInfoMapper;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.common.base.entity.MstExchargeQuote;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**债卷类行情信息导入
 * @auther: 张星1
 * @date: 2020 年 02 月 24
 * @description:
 */
@Service
public class MstBondQuotationInfoImportDataService extends CommonExportService<MstBondQuotationInfoDto> {
    /**
     * 定义mapper(mine)
     */
    @Autowired
    private MstBondQuotationInfoMapper mstBondQuotationInfoMapper;

    /**
     * 定义mapper(系统生成)
     */
    @Autowired
    private MstBondValuationInfoMapper mstBondValuationInfoMapper;
    
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
            headList.add("资产代码");
            headList.add("债券简称");
            headList.add("估值来源");
            headList.add("估值日");
            headList.add("剩余期限");

            headList.add("市价全价");
            headList.add("市价净价");
            headList.add("市场久期");
            headList.add("市场凸性");
            headList.add("基点价值");

            headList.add("利差久期");
            headList.add("利差凸性");
            headList.add("利率久期");
            headList.add("利率凸性");
            headList.add("市场收益率%");
            headList.add("导入日");
            List<ExcelValidDto> tempCheckResult = ExcelUtil.checkExcelTemp(tempList, headList);
            if (!StringUtil.isEmptyList(tempCheckResult)) {
                return tempCheckResult;
            }
            if (null != dataList) {                  
                List<MstBondValuationInfo> entities = new ArrayList<>(dataList.size());
                List<ExcelValidDto> checkResult= convertToEntity(dataList, entities,userName,deptCode);
                if(checkResult.isEmpty()) {
	                for(MstBondValuationInfo dto : entities){
	                	mstBondValuationInfoMapper.insert(dto);
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
    private List<ExcelValidDto> convertToEntity(List<List<String>> dataList,List<MstBondValuationInfo> entities,String userName,String deptCode) {
        List<ExcelValidDto> checkResult = new ArrayList<>();
        Date nowDate = new Date();
        for (int i = 0; i < dataList.size(); i++ ) {
        	//是否正常标志
        	boolean flag = true;
            List<String> list = dataList.get(i);         
            // 验证消息
            List<String> message = new ArrayList<>();
            Map<String,Object> param = new HashMap<String,Object>();
            //利率名称
            String valueSource = "";

            //*****资产代码secId
            if(list.get(0) == null || list.get(0).trim().equals("")){
            	flag = false;
            	message.add("资产代码不能为空");
            }else{
            	param.put("secId", list.get(0));
                param.put("prodType2", "F01");
                String result = mstBondQuotationInfoMapper.checkSecId(param);
                if(StringUtils.isEmpty(result)){
                	flag = false;
                	message.add("不存在该债券代码");
                }
            } 
            //债券简称与资产代码验证
            if(flag && StringUtil.isNotEmpty(list.get(1))){
            	param.put("secId", list.get(0));
                param.put("secName", list.get(1));
                param.put("prodType2", "F01");
                String result = mstBondQuotationInfoMapper.checkSecName(param);
                if(StringUtils.isEmpty(result)){
                	flag = false;
                	message.add("债券简称与资产代码不匹配");
                }
            }
            //*****估值来源valueSource
            if(list.get(2) == null || list.get(2).trim().equals("")){
            	flag = false;
            	message.add("估值来源不能为空");
            }else{
            	param.put("VALUATION_SOURCE", DictCode.估值来源.getValue());
                param.put("valueSourceName", list.get(2));
                MstBondQuotationInfoDto dto =    mstBondQuotationInfoMapper.getvalueSourceCode(param);
                if(dto != null){
                	valueSource = dto.getValueSource();
                }else{
                	flag = false;
                    message.add("估值来源错误");
                }
            }
            //*****估值日valueDateStr
            if(list.get(3) == null || list.get(3).trim().equals("")){
            	flag = false;
            	message.add("估值日期不能为空");
            }else{
            	if (!checkDate(list.get(3))) {
            		flag = false;
                	message.add("估值日期格式不正确");
                } 
            }
            //剩余期限lastPeriod
            if(list.get(4) != null && !list.get(4).trim().equals("")){
            	if(!checkFigure(list.get(4))){
            		flag = false;
                	message.add("剩余期限不是数字");
            	}
            }
            //*****市价全价price
            if(list.get(5) == null || list.get(5).trim().equals("")){
            	flag = false;
            	message.add("市价全价不能为空");
            }else{
            	if(!checkFigure(list.get(5))){
            		flag = false;
                	message.add("市价全价不是数字");
            	}
            }
           //*****市场净价netprice
            if(list.get(6) == null || list.get(6).trim().equals("")){
            	flag = false;
            	message.add("市场净价不能为空");
            }else{
            	if(!checkFigure(list.get(6))){
            		flag = false;
                	message.add("市场净价不是数字");
            	}
            }
            //市场久期mDuration
            if(list.get(7) != null && !list.get(7).trim().equals("")){
            	if(!checkFigure(list.get(7))){
            		flag = false;
                	message.add("市场久期不是数字");
            	}
            }
            //市场凸性mConvexity
            if(list.get(8) != null && !list.get(8).trim().equals("")){
            	if(!checkFigure(list.get(8))){
            		flag = false;
                	message.add("市场凸性不是数字");
            	}
            }
            //基点价值bpvalue
            if(list.get(9) != null && !list.get(9).trim().equals("")){
            	if(!checkFigure(list.get(9))){
            		flag = false;
                	message.add("基点价值不是数字");
            	}
            }
            //利差久期sduration
            if(list.get(10) != null && !list.get(10).trim().equals("")){
            	if(!checkFigure(list.get(10))){
            		flag = false;
                	message.add("利差久期不是数字");
            	}
            }
            //利差凸性scnvxty
            if(list.get(11) != null && !list.get(11).trim().equals("")){
            	if(!checkFigure(list.get(11))){
            		flag = false;
                	message.add("利差凸性不是数字");
            	}
            }
            //利率久期interestDuration
            if(list.get(12) != null && !list.get(12).trim().equals("")){
            	if(!checkFigure(list.get(12))){
            		flag = false;
                	message.add("利率久期不是数字");
            	}
            }
            //利率凸性interestCnvxty
            if(list.get(13) != null && !list.get(13).trim().equals("")){
            	if(!checkFigure(list.get(13))){
            		flag = false;
                	message.add("利率凸性不是数字");
            	}
            }
            //市场收益率%bbpyield
            if(list.get(14) != null && !list.get(14).trim().equals("")){
            	if(!checkFigure(list.get(14))){
            		flag = false;
                	message.add("市场收益率不是数字");
            	}
            }            
            // 导入日
            if(StringUtil.isNotEmpty(list.get(15))){
                if(!checkDate(list.get(15))){
                	flag = false;
                	message.add("导入日期格式不正确");
                }
            }
            
            //主键校验
            if(flag){
            	param.put("secId",list.get(0));
                param.put("valueDate", DateUtil.parse(list.get(3)));
                param.put("valueSource",valueSource);
                String  result = mstBondQuotationInfoMapper.getRegistExcelList(param);
            	if(StringUtils.isNotEmpty(result)){
            		flag = false;
            		message.add("存在和资产代码、估值日和估值来源相同的数据");
            	}                    	
            }

            if(flag){
            	MstBondValuationInfo entity = new MstBondValuationInfo();
            	//*****资产代码secId
                entity.setSecId(list.get(0));
                //*****估值来源valueSource
                entity.setValueSource(valueSource);
                //*****估值日valueDateStr
                entity.setValueDate(DateUtil.parse(list.get(3)));
                //剩余期限lastPeriod
                if(StringUtil.isNotEmpty(list.get(4))){
                    BigDecimal lastPeriod = new BigDecimal(list.get(4));
                    entity.setLastPeriod(lastPeriod);
                }
                //*****市价全价price
                if(StringUtil.isNotEmpty(list.get(5))){
                    BigDecimal price = new BigDecimal(list.get(5));
                    entity.setPrice(price);
                }
                //*****市场净价netprice
                if(StringUtil.isNotEmpty(list.get(6))){
                    BigDecimal netprice= new BigDecimal(list.get(6));
                    entity.setNetprice(netprice);
                }
                //市场久期mDuration
                if(StringUtil.isNotEmpty(list.get(7))){
                    BigDecimal mDuration= new BigDecimal(list.get(7));
                    entity.setmDuration(mDuration);
                }
                //市场凸性mConvexity
                if(StringUtil.isNotEmpty(list.get(8))){
                    BigDecimal mConvexity= new BigDecimal(list.get(8));
                    entity.setmConvexity(mConvexity);
                }
                //基点价值bpvalue
                if(StringUtil.isNotEmpty(list.get(9))){
                    BigDecimal bpvalue= new BigDecimal(list.get(9));
                    entity.setBpvalue(bpvalue);
                }
                //利差久期sduration
                if(StringUtil.isNotEmpty(list.get(10))){
                    BigDecimal sduration= new BigDecimal(list.get(10));
                    entity.setSduration(sduration);
                }
               //利差凸性scnvxty
                if(StringUtil.isNotEmpty(list.get(11))){
                    BigDecimal scnvxty= new BigDecimal(list.get(11));
                    entity.setScnvxty(scnvxty);
                }
               //利率久期interestDuration
                if(StringUtil.isNotEmpty(list.get(12))){
                    BigDecimal interestDuration= new BigDecimal(list.get(12));
                    entity.setInterestDuration(interestDuration);
                }
                //利率凸性interestCnvxty
                if(StringUtil.isNotEmpty(list.get(13))){
                    BigDecimal interestCnvxty= new BigDecimal(list.get(13));
                    entity.setInterestCnvxty(interestCnvxty);
                }
                //市场收益率%bbpyield
                if(StringUtil.isNotEmpty(list.get(14))){
                    BigDecimal bpyield= new BigDecimal(list.get(14));
                    entity.setBpyield(bpyield.divide(new BigDecimal(100)));
                }
                // 导入日
                if(StringUtil.isNotEmpty(list.get(15))){
                	entity.setCreateTime(DateUtil.parse(list.get(15)));                    
                }else {
                    // 默认导入日= 录入日 =平台日
                    entity.setCreateTime(nowDate);
                }
                entity.setInputType(DictCodeListExt1.录入方式_系统导入.getValue());             
                entity.setCreateUser(userName);
                entity.setCreateDept(deptCode);
                Date sysDate = DateUtil.platDateFromCache();// 获取平台日期
                entity.setUpdateUser(userName);
                entity.setUpdateTime(nowDate); 
                entities.add(entity);
            }

            if (CollectionUtils.isNotEmpty(message)) {
                checkResult.add(new ExcelValidDto(list.get(0), String.valueOf(i+2),
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
    	//判断excel表中资产代码、估值日和估值来源是否冲突
    	for (int i = 0; i < dataList.size(); i++ ) {
    		for (int j = i+1; j < dataList.size(); j++ ) {
    			if(StringUtils.equals(dataList.get(i).get(0), dataList.get(j).get(0)) &&
    					StringUtils.equals(dataList.get(i).get(2), dataList.get(j).get(2)) &&
    					StringUtils.equals(dataList.get(i).get(3), dataList.get(j).get(3))){
    				ValidMessage validMessage = new ValidMessage("第"+(i+2)+"行资产代码、估值日和估值来源与"+"第"+(j+2)+"行资产代码、估值日和估值来源相同");
                    errorList.add(validMessage);
    			}
    		}
    	}

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
