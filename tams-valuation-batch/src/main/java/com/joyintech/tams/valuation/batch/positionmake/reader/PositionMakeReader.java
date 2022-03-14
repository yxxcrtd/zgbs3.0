package com.joyintech.tams.valuation.batch.positionmake.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokStatFeePosition;
import com.joyintech.tams.valuation.batch.positionmake.dto.PositionMakeReaderResult;
import com.joyintech.tams.valuation.batch.positionmake.service.PositionMakeReaderNewService;
import com.joyintech.tams.valuation.batch.positionmake.service.PositionMakeReaderService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 头寸生成批处理 - reader
 * 
 * @author Administrator
 */
@Service
@Scope("prototype")
public class PositionMakeReader extends BaseReader<PositionMakeReaderResult> {

    @Autowired
    private PositionMakeReaderService positionMakeReaderService;

    @Autowired
    private PositionMakeReaderNewService positionMakeReaderNewService;

    @SuppressWarnings("unchecked")
	@Override
    public List<PositionMakeReaderResult> createList() {

        // 获取task传递参数
        Map<String, Object> params = this.getParams();
        if (MapUtils.isEmpty(params)) {
            return new ArrayList<>();
        }
        // modify by weipeng on 20220312 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        List<PositionMakeReaderResult> result = new ArrayList<PositionMakeReaderResult>();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
        // 若按照内部科目编码分录，则需要映射分录对应科目信息.
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            result = positionMakeReaderNewService.execute(params);
        }
        else{
            result = positionMakeReaderService.execute(params);
        }
        // modify by weipeng on 20220312 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

        return result;
    }

}
