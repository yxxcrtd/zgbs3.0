package com.joyintech.tams.business.common.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dao.EtrCommonTableInfoQueryMapper;
import com.joyintech.tams.business.common.dto.CommonSearchInfoRequest;
import com.joyintech.tams.business.common.dto.CommonSearchQueryWorkflowRequest;
import com.joyintech.tams.business.common.dto.CommonSearchQueryWorkflowResponse;
import com.joyintech.tams.business.common.dto.CommonSearchRskInfoResponse;
import com.joyintech.tams.business.common.dto.CommonSearchOperateListRequest;
import com.joyintech.tams.business.common.dto.CommonSearchInfoOperateListResponse;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 公共方法service
 */
@Service("etrCommonTableService")
public class EtrCommonTableService extends BaseService {

    @Autowired
    private EtrCommonTableInfoQueryMapper etrCommonTableInfoQueryMapper;
    
    /**
     * 指令风险试算查询
     *
     * @param dto
     * @return Page<FieldEtrDispatcherRskInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<CommonSearchRskInfoResponse> getCommonSearchTabRskInfo(CommonSearchInfoRequest dto) {

        //dto转成map
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        List<String> entrustIdList = StringUtil.splitToList(",",(String)pageParam.get("entrustId"));
        String  busiTypePosition = (String)pageParam.get("busiTypePosition");
        //质押式回购的风险试算信息处理
        if (!DictCodeListExt3.头寸业务类型_质押式回购首期.getValue().equals(busiTypePosition) && !DictCodeListExt3.头寸业务类型_债券一级投标.getValue().equals(busiTypePosition) ) {
            pageParam.put("NOT_NULL_FLAG", "Y");
        }
        if(StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1){
            return new Page<CommonSearchRskInfoResponse>();
        }else{
            String entrustId = dto.getEntrustId();
            List<String> entrustIds = new ArrayList<>();
            if (StringUtil.equals(dto.getPs(), DictCodeListExt1.交易方向_债转股.getValue())){
                entrustId = entrustId+ DictCodeList.交易方向_卖出.getValue();
                entrustIds.add(entrustId);
                entrustId = dto.getEntrustId()+ DictCodeList.交易方向_买入.getValue();
                entrustIds.add(entrustId);
                pageParam.put("entrustIdList",entrustIds);
            }else{
                entrustIds.add(entrustId);
                pageParam.put("entrustIdList",entrustIds);
            }
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                    () -> etrCommonTableInfoQueryMapper.getCommonSearchTabRskInfo(pageParam));
        }
    }


    /**
     * 操作记录查询
     *
     * @param dto
     * @return Page<FieldEtrInfoOperateListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<CommonSearchInfoOperateListResponse> getCommonSearchTabInfoQueryOperate(CommonSearchOperateListRequest dto) {

        //dto转成map
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        List<String> entrustIdList = StringUtil.splitToList(",",(String)pageParam.get("entrustId"));
        if(StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1){
            return new Page<CommonSearchInfoOperateListResponse>();
        }else{
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                    () -> etrCommonTableInfoQueryMapper.getCommonSearchTabInfoQueryOperate(pageParam));
        }
    }

    /**
     * 审批记录分页查询
     * @param dto 入数
     * @return Page<CfetsTrdQueryTradeInfoListResponse>
     */
    public Page<CommonSearchQueryWorkflowResponse> queryAllWorkFlowInfoList(CommonSearchQueryWorkflowRequest dto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        List<String> totalEntrustIdList=etrCommonTableInfoQueryMapper.queryTotalEntrustIdList(pageParam);
        pageParam.put("totalEntrustIdList",totalEntrustIdList);
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> etrCommonTableInfoQueryMapper.queryAllWorkFlowInfoList(pageParam));
    }
}
