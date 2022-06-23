package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealWriteDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StlDownDataDealWriteBaseDto;
import com.joyintech.tams.settle.common.dto.StlDownDataDealAccountInfoDto;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author meifaxin
 * @date 2021/12/09
 * @see StlDataDealSzdTzxxProcessService
 * @since 1.0
 */
@Service
public class StlDataDealSzdTzxxProcessService {

    @Autowired
    private StlDataDealProcessCommonService stlDataDealProcessCommonService;

    /**
     * 生成上中登TZXX数据清算批处理step2 processor的结果Dto
     *
     * @param stlDownDataDealOperatorDto StlDownDataDealOperatorDto
     */
    public StlDownDataDealWriteDto generatorStlDownDataDealSzdTzxxWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        if (stlDownDataDealOperatorDto != null && stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto() != null &&
                stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSzdTzxxList() != null &&
                stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSzdTzxxList().size() > 0) {
            List<MstFundBonusNotice> mstFundBonusNoticeArrayList = new ArrayList<MstFundBonusNotice>();
            for (SzdTzxx szdTzxx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSzdTzxxList()) {
                MstFundBonusNotice mstFundBonusNotice = new MstFundBonusNotice();
                // GNS-871: 修改字段赋值 modify by fuyongsheng 20220216 start
                // mstFundBonusNotice.setFundId(szdTzxx.getZqzh()+".SH");
                mstFundBonusNotice.setFundId(szdTzxx.getZqdm()+".SH");
                mstFundBonusNotice.setRegDate(szdTzxx.getRq1());
                // mstFundBonusNotice.setDelDate(DateUtil.addDay(szdTzxx.getRq1(),1));
                // mstFundBonusNotice.setDevidentDate(DateUtil.addDay(szdTzxx.getRq1(),2));
                mstFundBonusNotice.setDelDate(nextWorkDay(szdTzxx.getRq1())); // +1工作日
                mstFundBonusNotice.setDevidentDate(nextWorkDay(nextWorkDay(szdTzxx.getRq1()))); // +2工作日
                // mstFundBonusNotice.setUnitBonusUnitValue(szdTzxx.getJe2());
                mstFundBonusNotice.setUnitBonusUnitValue(new BigDecimal(szdTzxx.getJg2()));
                // GNS-871: 修改字段赋值 modify by fuyongsheng 20220216 end
                mstFundBonusNoticeArrayList.add(mstFundBonusNotice);
            }
            stlDownDataDealWriteDto.setMstFundBonusNoticeList(mstFundBonusNoticeArrayList);
        }

        return stlDownDataDealWriteDto;
    }

    /**
     * 取下一个工作日
     * @param parseDate
     * @return
     */
    private Date nextWorkDay(Date parseDate) {
        String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
        return stlDataDealProcessCommonService.getNextWorkDay(parseDate, calendarId);
    }
}
