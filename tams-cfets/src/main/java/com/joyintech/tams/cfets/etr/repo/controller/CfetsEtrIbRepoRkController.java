package com.joyintech.tams.cfets.etr.repo.controller;

import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.repo.dto.*;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoRKService;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoinfoService;
import com.joyintech.tams.common.base.entity.EtrSecStockConfig;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.workflow.engine.constant.OperationType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉 资产系列管理
 * 
 * @author wl
 * @version 1.0 2021年2月7日
 * @see CfetsEtrIbRepoRkController
 * @since 1.0
 */

@Controller
@PageId("CfetsEtrIbRepoRk")
@RequestMapping("/cfets/etrib/cfetsEtrIbRepoRk")
@Api(tags = "银行间回购质押券库")
public class CfetsEtrIbRepoRkController extends BaseController {
	
	@Autowired
    private CfetsEtrIbRepoRKService cfetsEtrIbRepoRKService;
	
	
	@Autowired
    private CfetsEtrIbRepoinfoService cfetsEtrIbRepoinfoService;
	
    
    /**
     * BaseGetBusinessId
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;
	
    /**
     * 组合树
     * @return
     */
    @PostMapping("/getInterGroup")
    @ResponseBody
    @ApiOperation(value = "组合树", notes = "组合树")
    public Message<List<CfetsTreePortfolioResponse>> getInterGroup(@RequestBody CfetsTreePortfolioRequest cfetsTreePortfolioRequest){
    	try{
    	    Map<String,Object> map = BeanUtil.object2Map(cfetsTreePortfolioRequest);
//            if(StringUtil.equals("select", getParam("pledgeStockId"))){
//                map.put("pledgeStockId", "");
//            }else{
//                map.put("pledgeStockId", getParam("pledgeStockId"));
//            }
            map.put("portfolioId", getParam("portfolioId"));
            List<CfetsTreePortfolioResponse> treeList = cfetsEtrIbRepoinfoService.getTreeNodeList(map);
            return new Message<List<CfetsTreePortfolioResponse>>().success().setData(treeList);
        }catch (Exception e){
    	    e.printStackTrace();
            return new Message<List<CfetsTreePortfolioResponse>>().fail();
        }
    }

    @PostMapping("getPledgeStockId")
    @ResponseBody
    @ApiOperation(value = "质押库：列表", notes = "质押库：列表")
    public Message<List<SelectOptionDto>> getPledgeStockId(@RequestBody CfetsSearchPledgeStockRequest params) {
        try {
            List<SelectOptionDto> page = cfetsEtrIbRepoinfoService.getPledgeStockId(params);
            return new Message<List<SelectOptionDto>>().success().setData(page);
        } catch (Exception e) {
            return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
        }

    }
    
    /**
     * 
     * <一句话功能简述>
     * 页面查询
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @PostMapping("findPage")
    @ApiOperation(value = "质押券库设置查询", notes = "质押券库设置查询")
    public Message<Collection<CfetsEtrSecStockConfig>> findPage(@RequestBody CfetsEtrRepoPledgeAssetDetailRequest cfetsEtrRepoPledgeAssetDetailRequest) {
        try{
            if (StringUtil.isNotEmpty(cfetsEtrRepoPledgeAssetDetailRequest.getPledgeStockId())) {
                return new Message<Collection<CfetsEtrSecStockConfig>>().success().setData(cfetsEtrIbRepoinfoService.getAssetDetailBySeqno(cfetsEtrRepoPledgeAssetDetailRequest));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Message<Collection<CfetsEtrSecStockConfig>>().fail();
    }
    
    /**
     * 删除
     * @param dto
     * @return
     */
    @ResponseBody
    @PostMapping(ACTION_DELETE)
    @ApiOperation(value = "删除质押券库", notes = "删除质押券库")
    public Message<AjaxResult> delete(@RequestBody CfetsEtrIbRepoRkDto dto, BindingResult br) {
        try{
            if (br.hasErrors()) {
                return new Message<AjaxResult>().setData(new AjaxResult(br));
            }
            cfetsEtrIbRepoRKService.doComplete(dto,OperationType.DELETE);
            return new Message<AjaxResult>().success().setData(new AjaxResult("删除成功"));
        }catch (Exception e){
            e.printStackTrace();
            return new Message<AjaxResult>().fail().setData(new AjaxResult("删除失败"));
        }
    }

    /**
     * 批量提交
     *
     * @param RepoInfoDto EtrInvestRepoInfoDto
     * @param br msg
     */
    @ResponseBody
    @PostMapping(PAGE_SAVE)
    @ApiOperation(value = "质押券库保存", notes = "自动入库提交")
    public Message<AjaxResult> save(@RequestBody CfetsEtrIbRepoRkDto RepoInfoDto, BindingResult br) {
        try {
            if (br.hasErrors()) {
                return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
            }
            // 临时主键
            // 业务代码生成参数
            //前台默认
            //        BussinessCodeParameter parameter = new BussinessCodeParameter();
            //        parameter.setSeqNo("0666");
            //        RepoInfoDto.setPledgeStockId(baseGetBusinessId.buildBussinessCode(parameter));
//            String generatedId = IDGenerator.generate();
            List<EtrSecStockConfig> etrSecStockConfigList = new ArrayList<>();
            for (CfetsEtrSecStockConfig cfetsEtrSecStockConfig : RepoInfoDto.getCfetsEtrSecStockConfig()) {
                if (StringUtil.isEmptyList(cfetsEtrSecStockConfig.getWindtype())) {
                    EtrSecStockConfig etrSecStockConfig = new EtrSecStockConfig();
                    etrSecStockConfig.setPledgeStockId(RepoInfoDto.getPledgeStockId());
                    etrSecStockConfig.setConfigId(IDGenerator.generate());
                    etrSecStockConfig.setIsSecBond(cfetsEtrSecStockConfig.getIsSecBond());
                    etrSecStockConfig.setIsPpb(cfetsEtrSecStockConfig.getIsPpb());
                    etrSecStockConfig.setIsRight(cfetsEtrSecStockConfig.getIsRight());
                    etrSecStockConfig.setmGradeResult(cfetsEtrSecStockConfig.getmGradeResult());
                    etrSecStockConfig.setSecGradeResult(cfetsEtrSecStockConfig.getSecGradeResult());
                    etrSecStockConfig.setWindtypeone(null);
                    etrSecStockConfig.setWindtypetwo(null);
                    etrSecStockConfigList.add(etrSecStockConfig);
                } else {
                    for (int i = 0; i < cfetsEtrSecStockConfig.getWindtype().size(); i++) {
                        EtrSecStockConfig etrSecStockConfig = new EtrSecStockConfig();
                        etrSecStockConfig.setPledgeStockId(RepoInfoDto.getPledgeStockId());
                        etrSecStockConfig.setConfigId(IDGenerator.generate());
                        etrSecStockConfig.setIsSecBond(cfetsEtrSecStockConfig.getIsSecBond());
                        etrSecStockConfig.setIsPpb(cfetsEtrSecStockConfig.getIsPpb());
                        etrSecStockConfig.setIsRight(cfetsEtrSecStockConfig.getIsRight());
                        etrSecStockConfig.setmGradeResult(cfetsEtrSecStockConfig.getmGradeResult());
                        etrSecStockConfig.setSecGradeResult(cfetsEtrSecStockConfig.getSecGradeResult());
                        etrSecStockConfig.setWindtypeone(cfetsEtrSecStockConfig.getWindtype().get(i).get(0));
                        etrSecStockConfig.setWindtypetwo(cfetsEtrSecStockConfig.getWindtype().get(i).get(1));
                        etrSecStockConfig.setWindtypethree(cfetsEtrSecStockConfig.getWindtype().get(i).get(2));
                        etrSecStockConfigList.add(etrSecStockConfig);
                    }
                }

            }
            RepoInfoDto.setEtrSecStockConfig(etrSecStockConfigList);
//            for (int i = 0; i < RepoInfoDto.getEtrSecStockConfig().size(); i++) {
//                RepoInfoDto.getEtrSecStockConfig().get(i).setConfigId(IDGenerator.generate());
//                RepoInfoDto.getEtrSecStockConfig().get(i).setPledgeStockId(RepoInfoDto.getPledgeStockId());
//            }
            for (int i = 0; i < RepoInfoDto.getPtlSecStock().size(); i++) {
                RepoInfoDto.getPtlSecStock().get(i).setPledgeStockName(RepoInfoDto.getPledgeStockName());
                RepoInfoDto.getPtlSecStock().get(i).setPledgeStockId(RepoInfoDto.getPledgeStockId());
            }
            cfetsEtrIbRepoRKService.doComplete(RepoInfoDto, OperationType.NEW);
            return new Message<AjaxResult>().success().setData(new AjaxResult("保存成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return new Message<AjaxResult>().fail().setData(new AjaxResult("删除失败"));
        }
    }

}
