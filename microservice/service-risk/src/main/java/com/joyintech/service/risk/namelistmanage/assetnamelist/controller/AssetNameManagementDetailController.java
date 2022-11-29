package com.joyintech.service.risk.namelistmanage.assetnamelist.controller;


import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.risk.namelistmanage.assetnamelist.dto.RskAccessListDetailDto;
import com.joyintech.service.risk.namelistmanage.assetnamelist.dto.RskAccessListLogDto;
import com.joyintech.service.risk.namelistmanage.assetnamelist.service.AssetNameManagementDetailService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/AssetNameManagementDetail")
public class AssetNameManagementDetailController {

    @Autowired
    private AssetNameManagementDetailService assetNameManagementDetailService;

    @Autowired
    private IPlatformDate iPlatformDate;
    /**
     * 查询
     * @param param
     * @param page
     * @param length
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<RskAccessListDetailDto> selectAll(@RequestBody Map<String, Object> param,
                                                  @PathVariable("page") int page, @PathVariable("length") int length) throws IOException {
        return assetNameManagementDetailService.selectForPage(page, length, param);
    }

    /**
     *  查询未处理的名单明细
     * @param param
     * @return
     */
    @PostMapping("getUnhandleRskAccessListDetailDtoList")
    public List<RskAccessListDetailDto> getUnhandleRskAccessListDetailDtoList(@RequestBody Map<String, Object> param){
        return assetNameManagementDetailService.getUnhandleRskAccessListDetailDtoList(param);
    }


    /**
     * 查询(双击)
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("selectAssetListDetail")
    public List<RskAccessListDetailDto> selectAssetListDetail(@RequestBody Map<String, Object> param) {
        List<RskAccessListDetailDto> sd=assetNameManagementDetailService.selectAssetListDetail(param);
        return sd;
    }

    /**
     * 查询履历更新(双击)
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0selectAssetListDetailLog
     */
    @PostMapping("selectAssetListDetailLog")
    public List<RskAccessListLogDto> selectAssetListDetailLog(@RequestBody Map<String, Object> param) {
        return assetNameManagementDetailService.selectAssetListDetailLog(param);
    }

    /**
     * 查询履历明细(双击)
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("selectNameListLogDetail")
    public List<RskAccessListLogDto> selectNameListLogDetail(@RequestBody Map<String, Object> param) {
        param.put("listDate", DateUtil.parse(param.get("listDate").toString()));
        return assetNameManagementDetailService.selectNameListLogDetail(param);
    }


    /**
     * 当日历史履历
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("selectDayHisAssetLog")
    public List<RskAccessListLogDto> selectDayHisAssetLog(@RequestBody Map<String, Object> param) {
        param.put("listDate", iPlatformDate.getSysDate());
        return assetNameManagementDetailService.selectDayHisAssetLog(param);
    }

    /**
     * get名单总条数
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("getNameDetailCount")
    public int getNameDetailCount(@RequestBody Map<String, Object> param) {
        return assetNameManagementDetailService.getNameDetailCount(param);
    }

    /**
     *  非一个体系的名单明细
     * @param param
     * @return
     */
    @PostMapping("getOtherRskAccessListDetailDtoList")
    public List<RskAccessListDetailDto> getOtherRskAccessListDetailDtoList(@RequestBody Map<String, Object> param){
        return assetNameManagementDetailService.getOtherRskAccessListDetailDtoList(param);
    }

}
