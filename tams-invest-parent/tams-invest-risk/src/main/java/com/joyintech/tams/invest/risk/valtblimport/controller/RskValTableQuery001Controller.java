package com.joyintech.tams.invest.risk.valtblimport.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.invest.risk.valtblimport.dto.RskValtblAssetSubDto;
import com.joyintech.tams.invest.risk.valtblimport.dto.RskValtblAssetSumDto;
import com.joyintech.tams.invest.risk.valtblimport.service.RskValTableQueryService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 *   估值表查询
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年12月27日
 * @see RskValTableQuery001Controller
 * @since 1.0
 */
@Controller
@PageId("RskValTableQuery001")
@RequestMapping("/invest/risk/valtblimport/RskValTableQuery001")
public class RskValTableQuery001Controller extends BaseController {

    @Autowired
    private IPlatformDate platformDate;

    @Autowired
    private RskValTableQueryService rskValTableQueryService;
    @Override
    public String init(Model model){
        model.addAttribute("initDate",DateUtil.format(DateUtil.addDay(platformDate.getSysDate(),-1)));
        return super.init(model);
    }


    /**
     * 初始化查询
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<RskValtblAssetSumDto> selectAll() {
        return rskValTableQueryService.selectForPage(RequestUtil.getParameters(getRequest()));
    }

    /**
     * 详情查看初始化
     * @param model
     * @return
     */
    @RequestMapping(value = "/detailedInit")
    public String detaile(Model model) {
        String paramJsonStr = this.getParam("paramJsonStr");
        model.addAttribute("paramJsonStr", paramJsonStr);
        return "invest/risk/valtblimport/RskValTableQueryDetail";
    }

    /**
     * 查看估值表明细
     * <功能详细描述>
     * @param dto
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @RequestMapping(value = "/selectRskValTableDetaile")
    @ResponseBody
    public List<RskValtblAssetSubDto> selectRskValTableDetaile(@RequestBody RskValtblAssetSumDto dto)
            throws ParseException {
        Map<String,Object> param = new HashedMap();
        param.put("ptlId",dto.getPtlId());
        param.put("valDate", DateUtil.format(dto.getValDate()));
        return  rskValTableQueryService.selectRskValTableDetaile(param);
    }

    /**
     * 导出
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
             {
        Map<String, Object> param = RequestUtil.getParameters(getRequest());
        return rskValTableQueryService.export(this.getPageId(),param);
    }
}
