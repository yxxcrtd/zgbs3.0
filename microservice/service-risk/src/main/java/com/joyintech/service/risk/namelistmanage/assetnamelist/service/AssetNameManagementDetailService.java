package com.joyintech.service.risk.namelistmanage.assetnamelist.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.risk.namelistmanage.assetnamelist.dao.AssetNameManagementDetailMapper;
import com.joyintech.service.risk.namelistmanage.assetnamelist.dao.AssetNameManagementMapper;
import com.joyintech.service.risk.namelistmanage.assetnamelist.dto.RskAccessListDetailDto;
import com.joyintech.service.risk.namelistmanage.assetnamelist.dto.RskAccessListLogDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 名单管理--资产名单明细管理
 * @author 张年
 * @version 1.0 2019年07月11日
 * @see AssetNameManagementService
 * @since 1.0
 */
@Service("backAssetNameManagementDetailService")
public class AssetNameManagementDetailService extends BaseService<RskAccessListDetailDto> {

    @Autowired
    private AssetNameManagementMapper assetNameManagementMapper;

    @Autowired
    private AssetNameManagementDetailMapper assetNameManagementDetailMapper;

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 分页
     * @param page
     * @param length
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<RskAccessListDetailDto> selectForPage(int page, int length, Map<String, Object> pageParam) throws IOException
    {
        List<RskAccessListDetailDto> handleRskAccessListDetailList = new ArrayList<>();
        if(pageParam.containsKey("rskAccessListLogDtoList")){
            handleRskAccessListDetailList= JsonUtils.json2List(pageParam.get("rskAccessListLogDtoList").toString(),RskAccessListDetailDto.class);
            pageParam.put("rskAccessListLogDtoList",handleRskAccessListDetailList);
        }
        pageParam.put("sysDate",iPlatformDate.getSysDate());
        Page<RskAccessListDetailDto> pageDto = super.queryForPage(page, length, () -> assetNameManagementMapper.queryDetailForPage(pageParam));
        return pageDto;
    }

    public List<RskAccessListDetailDto> getUnhandleRskAccessListDetailDtoList(Map<String, Object> param){
        List<Map<String,Object>> handleRskAccessListDetailList = (List<Map<String,Object>>)param.get("handleRskAccessListDetailList");
        if(null!=handleRskAccessListDetailList){
            List<Map<String,Object>> taglist = handleRskAccessListDetailList.stream()
                    .filter(map ->(StringUtil.isNotEmpty(map.get("oldlistDetailName"))))
                    .collect(Collectors.toList());
            param.put("handleRskAccessListDetailList",taglist);
        }
        return assetNameManagementDetailMapper.getUnhandleRskAccessListDetailDtoList(param);
    }

    /**
     * 双击名单查看明细
     * @param map
     * @return
     */
    public List<RskAccessListDetailDto> selectAssetListDetail(Map<String,Object> map){
        //delete by yangwenjun 20201208 ZGBS-68
//        if(StringUtil.isNotEmpty(map.get("workflowId"))){
//            return assetNameManagementDetailMapper.selectAssetListDetailInworkflow(map);
//        }
        return assetNameManagementDetailMapper.selectAssetListDetailValid(map);
    }

    /**
     * 双击查看履历更新
     * @param param
     * @return
     */
    public List<RskAccessListLogDto> selectAssetListDetailLog(Map<String, Object> param){
        return assetNameManagementDetailMapper.selectAssetListDetailLog(param);
    }

    /**
     * 双击查看履历明细
     * @param param
     * @return
     */
    public List<RskAccessListLogDto> selectNameListLogDetail(Map<String, Object> param){
        return assetNameManagementDetailMapper.selectNameListLogDetail(param);
    }

    /**
     * 当日历史履历
     * @param param
     * @return
     */
    public List<RskAccessListLogDto> selectDayHisAssetLog(Map<String, Object> param){
        return assetNameManagementDetailMapper.selectDayHisAssetLog(param);
    }

    /**
     * get名单总条数
     * @param param
     * @return
     */
    public int getNameDetailCount(Map<String, Object> param){
        return assetNameManagementDetailMapper.getNameDetailCount(param);
    }

    /**
     * 非一个体系的名单明细
     * @param param
     * @return
     */
    public List<RskAccessListDetailDto> getOtherRskAccessListDetailDtoList(Map<String, Object> param){
        List<Map<String,Object>> otherRskAccessListDetailList = (List<Map<String,Object>>)param.get("otherRskAccessListDetailList");
        List<Map<String,Object>> Taglist1 = otherRskAccessListDetailList.stream()
                .filter(map ->(StringUtil.isNotEmpty(map.get("listDetailName"))))
                .collect(Collectors.toList());
        List<Map<String,Object>> Taglist2 = otherRskAccessListDetailList.stream()
                .filter(map ->(StringUtil.isNotEmpty(map.get("listDetailId"))))
                .collect(Collectors.toList());
        param.put("otherRskAccessListDetailList1",Taglist1);
        param.put("otherRskAccessListDetailList2",Taglist2);
        return assetNameManagementDetailMapper.getOtherRskAccessListDetailDtoList(param);
    }
}
