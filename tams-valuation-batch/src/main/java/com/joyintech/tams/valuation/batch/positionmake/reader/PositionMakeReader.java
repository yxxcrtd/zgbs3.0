package com.joyintech.tams.valuation.batch.positionmake.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.positionmake.dao.PositionMakeWithoutDispatchMapper;
import com.joyintech.tams.valuation.batch.positionmake.dto.PositionMakeReaderResult;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private PositionMakeWithoutDispatchMapper positionMakeWithoutDispatchMapper;

    @SuppressWarnings("unchecked")
	@Override
    public List<PositionMakeReaderResult> createList() {

        // 获取task传递参数
        if (MapUtils.isEmpty(this.getParams())) {
            return new ArrayList<>();
        }
        // modify by weipeng on 20220312 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        return positionMakeWithoutDispatchMapper.getBooksets( this.getParams());
    }

}
