package com.joyintech.tams.svreport.interf.dis.repfeetrdinterf.service;

import java.text.ParseException;
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
import com.joyintech.tams.svreport.interf.dis.repfeetrdinterf.dao.DisRepFeeTrdMapper;
import com.joyintech.tams.svreport.interf.dis.repfeetrdinterf.dto.DisRepFeeTrd;

/**
 * 费用交易表接口
 * 
 * @author dongxiangyang
 */
@Service
public class DisRepFeeTrdInterfService extends FileImpService {

    @Autowired
    private DisRepFeeTrdMapper repFeeTrdMapper;

    /**
     * 长sql最大一次插入或者更新条数
     */
    @Value("${application.reportInsertLongCount}")
    public int reportInsertLongCount;

    @Override
    public String importDB(List<Map<String, String>> listData, InterfFile interfFile, String date) {
        if(CollectionUtils.isEmpty(listData)){
            return "success";
        }
        // 数据检查
        checkData(listData, interfFile.getFilename(), InterfDefine.产品费用交易接口数据.getCode(),
            InterfDefine.数据检查类型_产品费用交易接口.getCode(), "TRADE_ID",
            new String[]{"TRADE_ID"});
        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(
            InterfDefine.产品费用交易接口数据.getCode());
        if (interfConfig == null) {
            logger.info("CHB_INTERF_CONFIG表中无配置项！");
            return "fail";
        }
        String saveFlag = interfConfig.getContentType();
        return saveData(listData, saveFlag, interfFile.getDataSource());
    }

    /**
     * 费用交易信息入库 
     * listData 费用交易list 
     * saveFlag 入库标识
     * {@inheritDoc}
     */
    @Override
    public String saveData(List<Map<String, String>> listData, String saveFlag,
                           String dateSource) {
        if (StringUtil.isEmptyList(listData)) {
            logger.info("saveData方法参数listData为空。");
            return "success";
        }
        // 全部删除

        List<DisRepFeeTrd> list = new ArrayList<>();
        // 循环参数
        DisRepFeeTrd cashidDto = null;
        for (Map<String, String> map : listData) {
        	 DisRepFeeTrd repFeeTrdDto = new DisRepFeeTrd();
        	try{
                  repFeeTrdDto.setBusiId(map.get("TRADE_ID"));
//                  repFeeTrdDto.setCashType2(map.get("FEE_SECTYPE"));// 取【费用二级类型】映射到信披的 TODO
                  repFeeTrdDto.setCashType2(map.get("CASH_TYPE_2"));// 取【现金流二级类型】  zhangyf 20191123
                  repFeeTrdDto.setProdId(map.get("PROD_ID"));
                  cashidDto = repFeeTrdMapper.selectCashIdByProdidAndCashType2(repFeeTrdDto);
                  repFeeTrdDto.setFeeId(cashidDto == null ? "999999" : cashidDto.getCashId());// 根据产品代码+费用二级类型取【产品费率收益率表】中的现金流代码（任意一条）
                  repFeeTrdDto.setPaidFee(DisInterfDecimalUtil.str2BigDecimal(map.get("AMT")));
                  repFeeTrdDto.setPayDate(DateUtil.parse(map.get("PAY_DATE")));
                  repFeeTrdDto.setInputType("0");
                  repFeeTrdDto.setInputUser("interf");
                  repFeeTrdDto.setStatus(map.get("STATUS"));
                  repFeeTrdDto.setAdapterId("0");
                  repFeeTrdDto.setCreateUser("interf");
                  repFeeTrdDto.setCreateDept(map.get("CREATE_DEPT"));
                  repFeeTrdDto.setUpdateUser("interf");
        	}
        	catch(Exception  e){
           		logger.error("解析数据错误：",e.getMessage(),e);
           		return  "fail";
           	}
            try {
                if (StringUtil.isNotEmpty(map.get("CREATE_TIME"))) {
                    repFeeTrdDto.setCreateTime(DateUtil.parseDate(map.get("CREATE_TIME"),
                        new String[] {DateUtil.DEFAULT_DATETIME_FORMAT_ODS}));
                }
                else {
                    repFeeTrdDto.setCreateTime(new Date());
                }
                if (StringUtil.isNotEmpty(map.get("UPDATE_TIME"))) {
                    repFeeTrdDto.setUpdateTime(DateUtil.parseDate(map.get("UPDATE_TIME"),
                        new String[] {DateUtil.DEFAULT_DATETIME_FORMAT_ODS}));
                }
                else {
                    repFeeTrdDto.setUpdateTime(new Date());
                }
            }
            catch (ParseException e) {
                logger.error("transMap2Dto方法转换CreateTime或UpdateTime异常");
                logger.error(e.getMessage(),e);
                return  "fail";
            }
            
            // 删除
//            repFeeTrdMapper.deleteByPaydate(repFeeTrdDto);
            if (list.size() < reportInsertLongCount) {
                list.add(repFeeTrdDto);
            }
            else if (list.size() >= reportInsertLongCount) {
                repFeeTrdMapper.mergeRepFeeTrdInfo(list);
                list.clear();
                list.add(repFeeTrdDto);
            }
        }
        // 如果循环结束，小于最大值
        if (CollectionUtils.isNotEmpty(list)) {
        	repFeeTrdMapper.mergeRepFeeTrdInfo(list);
        }
        return "success";
    }

}
