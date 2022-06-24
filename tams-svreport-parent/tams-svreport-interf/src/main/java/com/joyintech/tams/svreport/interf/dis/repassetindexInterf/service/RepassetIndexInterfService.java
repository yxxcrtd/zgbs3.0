package com.joyintech.tams.svreport.interf.dis.repassetindexInterf.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.FileImpService;
import com.joyintech.tams.common.InterfDefine;
import com.joyintech.tams.common.InterfFile;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.svreport.interf.dis.common.DisInterfDecimalUtil;
import com.joyintech.tams.svreport.interf.dis.repassetindexInterf.dao.DisRepAssetIndexMapper;
import com.joyintech.tams.svreport.interf.dis.repassetindexInterf.dto.RepAssetIndex;
/**
 * 资产指标表接口
 * @author dongxiangyang
 *
 */
@Service
public class RepassetIndexInterfService extends FileImpService{

	@Autowired
	private DisRepAssetIndexMapper  repAssetIndexMapper;
	
	  /**
     * 长sql最大一次插入或者更新条数
     */
    @Value("${application.reportInsertLongCount}")
    public int reportInsertLongCount;
	/**
	 * 重写saveData方法
	 * 产品指标信息入库
	 * listData 费用交易list saveFlag 入库标识
	 */
	@Override
    public String saveData(List<Map<String, String>> listData, String saveFlag,
                           String dateSource) {
        if (StringUtil.isEmptyList(listData)) {
            logger.info("saveData方法参数listData为空。");
            return "success";
        }
        // 全部删除
        SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd");
        repAssetIndexMapper.delete(fo.format(getNowSysDate()));
        // 全部插入
        List<RepAssetIndex> list = new ArrayList<>();
        // 循环参数
        for (Map<String, String> map : listData) {
            RepAssetIndex dto = new RepAssetIndex();
            try{
            	// dto.setSeqNo(map.get("SEQ_NO"));
                dto.setBooksetId(map.get("BOOKSET_ID"));
                dto.setProdId(map.get("PROD_ID"));
                dto.setValueDate(DateUtil.parse(map.get("VALUE_DATE")));
                dto.setBusiId(map.get("ASSET_ID"));
                dto.setPosAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("POS_AMT")));
                dto.setTdyIntincexp(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_INTINCEXP")));
                // dto.setVdate(DateUtil.parse(map.get("VDATE")));
                dto.setMdate(DateUtil.parse(map.get("MDATE")));
                // dto.setTdyPosition(new BigDecimal(map.get("TDY_POSITION")));
                dto.setTdyCostAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_COST_AMT")));
                dto.setTdyFloatIngpl(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_FLOAT_INGPL")));
                dto.setEndDays1(DisInterfDecimalUtil.str2BigDecimal(map.get("END_DAYS")));
                // dto.setEndDays2(new BigDecimal(map.get("END_DAYS_2")));
                dto.setAlFlag(map.get("AL_FLAG"));
                dto.setStatus("E");
                dto.setCreateUser("interf");
                dto.setCreateDept(null);
                dto.setUpdateUser("interf");
            }
            catch(Exception  e){
           		logger.error("解析数据错误：",e.getMessage(),e);
           		return  "fail";
           	}
            try {
                if (StringUtil.isNotEmpty(map.get("CREATE_TIME"))) {
                    dto.setCreateTime(DateUtil.parseDate(map.get("CREATE_TIME"),
                        new String[] {DateUtil.DEFAULT_DATETIME_FORMAT_ODS}));
                }
                else {
                    dto.setCreateTime(new Date());
                }
                if (StringUtil.isNotEmpty(map.get("UPDATE_TIME"))) {
                    dto.setUpdateTime(DateUtil.parseDate(map.get("UPDATE_TIME"),
                        new String[] {DateUtil.DEFAULT_DATETIME_FORMAT_ODS}));
                }
                else {
                    dto.setUpdateTime(new Date());
                }
            }
            catch (ParseException e) {
                logger.error("transMap2Dto方法转换CreateTime或UpdateTime异常");
                logger.error(e.getMessage(),e);
                return  "fail";
            }
            dto.setInputType(null);
            dto.setInputUser(null);
            dto.setAdapterId("0");
            dto.setRateType(map.get("RATE_TYPE"));
            if (list.size() < reportInsertLongCount) {
                list.add(dto);
            }
            else if (list.size() >= reportInsertLongCount) {
                repAssetIndexMapper.insert(list);
                list.clear();
                list.add(dto);
            }
        } // 如果循环结束，小于最大值
        if (CollectionUtils.isNotEmpty(list)) {
            repAssetIndexMapper.insert(list);
        }
        return "success";
    }
	@Override
	public String importDB(List<Map<String, String>> listData, InterfFile interfFile, String date) {
        if(CollectionUtils.isEmpty(listData)){
            return "success";
        }
        // 数据检查
        checkData(listData, interfFile.getFilename(), InterfDefine.资产基础指标数据.getCode(),
            InterfDefine.数据检查类型_资产基础指标.getCode(), null,
            new String[]{"VALUE_DATE","ASSET_ID","PROD_ID"});
        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(
            InterfDefine.资产基础指标数据.getCode());
        if (interfConfig == null) {
            logger.info("CHB_INTERF_CONFIG表中无配置项！");
            return "fail";
        }
        String saveFlag = interfConfig.getContentType();
        return saveData(listData, saveFlag, interfFile.getDataSource());
    }

}
