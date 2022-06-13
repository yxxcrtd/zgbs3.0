package com.joyintech.tams.settle.batch.datadown.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.SettleTransParaMapper;
import com.joyintech.tams.common.base.entity.SettleTransPara;
import com.joyintech.tams.common.dbf.DBFRow;
import com.joyintech.tams.common.dbf.Field;
import com.joyintech.tams.settle.batch.datadown.dao.StlDataDownMapper;
import com.joyintech.tams.settle.batch.datadown.dto.BondSeatPtlMapping;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 何阳阳
 * @date 2020/7/7
 * @see StlDataDownService
 * @since 1.0
 */
@Service
public class StlDataDownService {

    @Autowired
    private StlDataDownMapper stlDataDownMapper;

    @Autowired
    private SettleTransParaMapper settleTransParaMapper;

    private static Map<String, List<BondSeatPtlMapping>> mappingMap;

    private static Object locker = new Object();

    public void loadAllBondSeatPtlMapping() {
        if(null == mappingMap) {
            synchronized(locker) {
                List<BondSeatPtlMapping> mappings = stlDataDownMapper.selectAllPtlNo();
                mappingMap = mappings.stream().collect(JoyinCollectors.groupingBy(BondSeatPtlMapping::getBondId));
            }
        }
    }

    public List<SettleTransPara> selectImportParam(Date importDate, String taskId) {
        return stlDataDownMapper.selectAllParam(importDate, taskId);
    }

    /**
     * 清理导入参数，无论此次批处理执行是否出现异常，导入参数都必须清除
     * @param param 需要被清理的导入参数列表
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void clearImportParam(List<SettleTransPara> param) {
        param.stream().map(SettleTransPara::getSeqno).forEach(seqno -> {
            settleTransParaMapper.deleteByPrimaryKey(seqno);
        });
    }

    /**
     * 证券账号+席位号 => 组合代码
     * <br>
     * @param data
     * @return
     */
    public List<String> createPortfolioId(DBFRow data) {
        /** jsmx ***/
        // 证券账号
        Field zqzhField = data.get("ZQZH");
        // 席位号
        Field xwhField = data.get("XWH1");

        /** sjsmx **/
        if(null == zqzhField) {
            zqzhField = data.get("MXZQZH");
        }
        if(null == xwhField) {
            xwhField = data.get("MXJYDY");
        }

        /** sjsjg **/
        if(null == zqzhField) {
            zqzhField = data.get("JGZQZH");
        }
        if(null == xwhField) {
            xwhField = data.get("JGTGDY");
        }

        /** sjsfx **/
        if(null == zqzhField) {
            zqzhField = data.get("FXZQZH");
        }
        if(null == xwhField) {
            xwhField = data.get("FXTGDY");
        }

        /** zqbd **/
        if(null == zqzhField) {
            zqzhField = data.get("ZQZH");
        }
        if(null == xwhField) {
            xwhField = data.get("XWH");
        }

        /** abcsj **/
        if(null == zqzhField) {
            zqzhField = data.get("ZQZH");
        }
        if(null == xwhField) {
            // TODO
        }

        /** zsmx **/
        if(null == zqzhField) {
            zqzhField = data.get("MXGDDM");
        }
        if(null == xwhField) {
            xwhField = data.get("MXXWDH");
        }

        if(null != zqzhField && null != xwhField) {
            List<BondSeatPtlMapping> mappingList = mappingMap.get(zqzhField.getValue());
            if(null != mappingList) {
                Stream<BondSeatPtlMapping> stream = mappingList.stream();
                if(null != xwhField) {
                    String xwh = (String)xwhField.getValue();
                    stream = stream.filter(item -> StringUtil.equals(item.getShSeatNo(),xwh));
                }
                return stream.map(BondSeatPtlMapping::getPortfolioId).collect(Collectors.toList());
            }
        }
        return null;
    }
}
