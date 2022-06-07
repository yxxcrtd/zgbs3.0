package com.joyintech.tams.business.common.service;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.business.common.dao.CfgDataTableFlagMapper;
import com.joyintech.tams.business.common.dao.CfgDataTableMapper;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.entity.CfgDataTableShowFlag;
import com.joyintech.tams.constant.DictCodeList;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * 页面展示设置service
 */
@Service
public class CfgDataTableService {

    /**
     * 用户自定义设置
     */
    private static final int EFFERANGE_IS = 1;

    /**
     * 默认设置
     */
    private static final int EFFERANGE_NO = 0;


    /**
     * 默认设置用户ID
     */
    private static final String USER_ID = "COMM";


    @Autowired
    private CfgDataTableMapper cfgDataTableMapper;

    @Autowired
    private CfgDataTableFlagMapper cfgDataTableFlagMapper;

    /**
     * 查询页面展示设置
     *
     * @param pageCode
     * @return
     */
    public Map<String, List<CfgDataTableShow>> getByPageCode(String pageCode) {
        Map<String, List<CfgDataTableShow>> map = new HashMap<>();
        //获取登录用户的ID
        String userId = SecurityUtil.getLoginUser().getUserName();
        //获取页面的区域编码
        List<String> dtCodes = cfgDataTableMapper.getDtCodeByPageCode(pageCode);
        String spcdCode = "999999";
        if (null != dtCodes && dtCodes.size() > 0) {
            dtCodes.stream().forEach(dtCode -> {
                //先查用户自定义设置，没有再查询默认设置
                List<CfgDataTableShow> uDataList = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_IS, userId);
                if (uDataList != null && uDataList.size() < 1) {
                    List<CfgDataTableShow> dataList = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_NO, null);
                    map.put(dtCode, dataList);
                } else {
                    map.put(dtCode, uDataList);
                }
            });
        }
        return map;
    }

    /**
     * 页面展示设置
     *
     * @param dataTableShowList
     */
    @Transactional(rollbackFor = Exception.class)
    public void setDataTable(List<CfgDataTableShow> dataTableShowList) {
        if (null == dataTableShowList || dataTableShowList.size() <= 0) {
            return;
        }
        //获取登录用户的ID
        String userId = SecurityUtil.getLoginUser().getUserName();
        //先判断用户是否自定义过设置
        CfgDataTableShow dataTableShow = dataTableShowList.get(0);
        String pageCode = dataTableShow.getPageCode();
        String dtCode = dataTableShow.getDtCode();
        List<String> spcdCodes = new ArrayList<>();
        dataTableShowList.forEach(data -> {
            if (!spcdCodes.contains(data.getSpcdCode())) {
                spcdCodes.add(data.getSpcdCode());
            }
        });
        CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCodes(pageCode, dtCode, spcdCodes, userId);
        List<CfgDataTableShow> tempList = cfgDataTableMapper.getBySpcdCodesByIsHide(pageCode, dtCode, spcdCodes, EFFERANGE_NO, USER_ID, DictCodeList.是否_是.getValue());
        if (CollectionUtils.isNotEmpty(tempList)) {
            dataTableShowList.addAll(tempList);
        }
        if (null != flag) {
            //删除用户原有自定义设置
            cfgDataTableFlagMapper.deleteDataTables(pageCode, dtCode, spcdCodes, userId);
            //新增用户修改后设置
            for (int i = 0; i < dataTableShowList.size(); i++) {
                CfgDataTableShow data = dataTableShowList.get(i);
                data.setRangeId(userId);
                data.setEffeRange(1);
                data.setSeqNo(i + 1);
            }
            cfgDataTableMapper.insertCfgDataTableBatch(dataTableShowList);
            return;
        }
        //新增用户自定义设置
        for (int i = 0; i < dataTableShowList.size(); i++) {
            CfgDataTableShow data = dataTableShowList.get(i);
            data.setSeqNo(i + 1);
            data.setEffeRange(1);
            data.setRangeId(userId);
        }
        cfgDataTableMapper.insertCfgDataTableBatch(dataTableShowList);
        spcdCodes.forEach(spcdCode -> {
            CfgDataTableShowFlag showFlag = new CfgDataTableShowFlag();
            showFlag.setEffeRange(1);
            showFlag.setPageCode(pageCode);
            showFlag.setDtCode(dtCode);
            showFlag.setRangeId(userId);
            showFlag.setSpcdCode(spcdCode);
            showFlag.setUpdateTime(DateUtil.format(new Date(), DateUtil.DEFAULT_DATETIME_FORMAT));
            cfgDataTableFlagMapper.insertCfgDataTableFlag(showFlag);
        });
    }

    /**
     * 根据pageCode，dtCode和spcdCode查询页面展示设置
     *
     * @param pageCode
     * @param dtCode
     * @return
     */
    public List<CfgDataTableShow> getByPageCodeAndDtCode(String pageCode, String dtCode, String spcdCode) {
        List<CfgDataTableShow> list;
        //获取登录用户的ID
        String userId = SecurityUtil.getLoginUser().getUserName();
        //List<CfgDataTableShow> uDataList = cfgDataTableMapper.getByPageCode(pageCode, dtCode, EFFERANGE_IS, userId);
        CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCode, userId);
        if (null == flag) {
            list = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_NO, USER_ID);
        } else {
            list = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_IS, userId);
        }
        return list;
    }

    /**
     * 根据pageCode，dtCode和spcdCode查询页面展示设置
     *
     * @param pageCode
     * @param dtCode
     * @return
     */
    public List<CfgDataTableShow> getByPageCodeAndDtCodes(String pageCode, String dtCode, List<String> spcdCodes) {
        List<CfgDataTableShow> list;
        //获取登录用户的ID
        //区分投资目的和业务模式
//        String showInvaimOrBussinssmodel =CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
        String userId = SecurityUtil.getLoginUser().getUserName();
        CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCodes(pageCode, dtCode, spcdCodes, userId);
        if (null == flag) {
//            list = cfgDataTableMapper.getBySpcdCodesshowInvaim(pageCode, dtCode, spcdCodes, EFFERANGE_NO, USER_ID,showInvaimOrBussinssmodel);
            list = cfgDataTableMapper.getBySpcdCodesshowInvaim(pageCode, dtCode, spcdCodes, EFFERANGE_NO, USER_ID,null);
        } else {
//            list = cfgDataTableMapper.getBySpcdCodesshowInvaim(pageCode, dtCode, spcdCodes, EFFERANGE_IS, userId,showInvaimOrBussinssmodel);
            list = cfgDataTableMapper.getBySpcdCodesshowInvaim(pageCode, dtCode, spcdCodes, EFFERANGE_IS, userId,null);
        }
        return list;
    }
}
