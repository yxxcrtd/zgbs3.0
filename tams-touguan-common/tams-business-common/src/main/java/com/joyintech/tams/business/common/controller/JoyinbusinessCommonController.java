package com.joyintech.tams.business.common.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.user.service.UserService;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.CfetsSearchInfoRequest;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.dto.SecManageAcctIdRequestDto;
import com.joyintech.tams.business.common.entity.JoyinDataDictEntity;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.constant.DictCodeList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common/joyinbusinessCommon")
@Api(tags = "公共方法")
public class JoyinbusinessCommonController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(JoyinbusinessCommonController.class);


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private UserService userService;

    @PostMapping("/getSelectDataFromDataDic")
    @ApiOperation(value = "数据字典查询功能", notes = "数据字典查询功能")
    @ResponseBody
    public Message<SelectDto> getSelectDataFromDataDic(@RequestBody String dc) {
        try {

            JoyinDataDictEntity dictCode = new JoyinDataDictEntity();
            dictCode.setValue(dc);
            SelectDto selectDto = this.getSelectDataFromDataDic(dictCode, null);
            return new Message<SelectDto>().success().setData(selectDto);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<SelectDto>().fail().setMessage("查询异常");
        }
    }

    @PostMapping("/getSecManageAcctId")
    @ApiOperation(value = "证券账户列表查询", notes = "证券账户列表查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> getSecManageAcctId(@RequestBody SecManageAcctIdRequestDto secManageAcctIdRequestDto) {
        try {
            Page<SelectOptionDto> tempList = joyinbusinessCommonService.getSecManageAcctId(secManageAcctIdRequestDto);
            return new Message<Page<SelectOptionDto>>().success().setData(tempList);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @PostMapping("/getEntrustId")
    @ApiOperation(value = "指令代码列表查询", notes = "指令代码列表查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> getEntrustId(@RequestBody CfetsSearchInfoRequest entrustIdRequestDto) {
        try {
            Page<SelectOptionDto> tempList = joyinbusinessCommonService.getEntrustId(entrustIdRequestDto);
            return new Message<Page<SelectOptionDto>>().success().setData(tempList);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @PostMapping("/getfinprodIdForAdd")
    @ApiOperation(value = "债券简称列表查询", notes = "债券简称列表查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> getfinprodIdForAdd(@RequestBody CfetsSearchInfoRequest item) {
        try {
            Page<SelectOptionDto> dto = joyinbusinessCommonService.getfinprodIdForAdd(item);
            return new Message<Page<SelectOptionDto>>().success().setData(dto);
        } catch (Exception e) {
            logger.error("查询债券简称列表异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @PostMapping("/getfinprodIdForNewBond")
    @ApiOperation(value = "新债简称列表查询", notes = "新债简称列表查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> getfinprodIdForNewBond(@RequestBody CfetsSearchInfoRequest item) {
        try {
            Page<SelectOptionDto> dto = joyinbusinessCommonService.getfinprodIdForNewBond(item);
            return new Message<Page<SelectOptionDto>>().success().setData(dto);
        } catch (Exception e) {
            logger.error("查询新债简称列表异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @ResponseBody
    @RequestMapping("/getfinprodIdForSelect")
    @ApiOperation(value = "债券简称列表查询", notes = "债券简称列表查询")
    public Page<SelectOptionDto> getfinprodIdForSelect(CfetsSearchInfoRequest item) {
        try {
            if(!StringUtil.equals(item.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
                    && !StringUtil.equals(item.getPs(),DictCodeList.交易方向_行权_回售.getValue())

            ){
                item.setPs(null);
            }
           /* if (!"T03".equals(item.getPs())) {
                item.setPs(null);
            }*/
            PageMode page = new PageMode();
            page.setPageNo(item.getPage());
            page.setPageSize(item.getLength());
            item.setPageMode(page);
            return joyinbusinessCommonService.getfinprodIdForAdd(item);
        } catch (Exception e) {
            logger.error("查询债券简称列表异常", e);
            return null;
        }
    }

    @PostMapping("/selectPortfolioIdList")
    @ApiOperation(value = "组合列表查询", notes = "组合列表查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> selectPortfolioIdList(@RequestBody CfetsSearchInfoRequest item) {
        try {
            Page<SelectOptionDto> dto = joyinbusinessCommonService.selectPortfolioIdList(item);
            return new Message<Page<SelectOptionDto>>().success().setData(dto);
        } catch (Exception e) {
            logger.error("查询组合列表异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @RequestMapping("/selectPortfolioIdForSelect")
    @ApiOperation(value = "组合列表查询", notes = "组合列表查询")
    @ResponseBody
    public Page<SelectOptionDto> selectPortfolioIdForSelect(CfetsSearchInfoRequest item) {
        try {
            PageMode page = new PageMode();
            page.setPageNo(item.getPage());
            page.setPageSize(item.getLength());
            item.setPageMode(page);

            return joyinbusinessCommonService.selectPortfolioIdList(item);
        } catch (Exception e) {
            logger.error("查询组合列表异常", e);
            return null;
        }
    }

    @PostMapping("/getManagers")
    @ApiOperation(value = "投资经理查询", notes = "投资经理查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> getManagers(@RequestBody CfetsSearchInfoRequest item) {
        try {
            Page<SelectOptionDto> list = joyinbusinessCommonService.getManager(item);
            return new Message<Page<SelectOptionDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "更新操作记录", notes = "更新操作记录")
    @PostMapping("updateCfetsOprHis")
    public Message<AjaxResult> updateCfetsOprHis(@RequestBody CfetsOprHisInfoRequest dto) throws Exception {
        try {
            joyinbusinessCommonService.updateCfetsOprHis(dto);
            return new Message<AjaxResult>().success().setData(new AjaxResult("更新操作记录成功"));
        } catch (Exception e) {
            logger.error("更新操作记录异常", e);
            return new Message<AjaxResult>().fail().setMessage("更新操作记录异常");
        }
    }

    @PostMapping("/queryMySideList")
    @ApiOperation(value = "本方或对方查询", notes = "本方或对方查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> queryMySideList(@RequestBody CfetsSearchInfoRequest item) {
        try {
            Page<SelectOptionDto> list = joyinbusinessCommonService.queryMySideList(item);
            return new Message<Page<SelectOptionDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @RequestMapping("/queryMySideListForSelect")
    @ApiOperation(value = "本方或对方查询", notes = "本方或对方查询")
    @ResponseBody
    public Page<SelectOptionDto> queryMySideListForSelect(CfetsSearchInfoRequest item) {
        try {
            PageMode page = new PageMode();
            page.setPageNo(item.getPage());
            page.setPageSize(item.getLength());
            item.setPageMode(page);
            Page<SelectOptionDto> list = joyinbusinessCommonService.queryMySideList(item);
            return list;
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return null;
        }
    }


    @PostMapping("/queryMySideTraderList")
    @ApiOperation(value = "本方交易员或对手方交易员查询", notes = "本方交易员或对手方交易员查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> queryMySideTraderList(@RequestBody CfetsSearchInfoRequest item) {
        try {
            Page<SelectOptionDto> list = joyinbusinessCommonService.queryMySideTraderList(item);
            return new Message<Page<SelectOptionDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @RequestMapping("/queryMySideTraderListForSelect")
    @ApiOperation(value = "本方交易员或对手方交易员查询", notes = "本方交易员或对手方交易员查询")
    @ResponseBody
    public Page<SelectOptionDto> queryMySideTraderListForSelect(CfetsSearchInfoRequest item) {
        try {
            PageMode page = new PageMode();
            page.setPageNo(item.getPage());
            page.setPageSize(item.getLength());
            item.setPageMode(page);
            if (!StringUtil.isEmpty(item.getCounterId())) {//不为空时获取partyId
                CfetsSearchInfoRequest item1 = new CfetsSearchInfoRequest();
                PageMode page1 = new PageMode();
                item1.setInitValue(item.getCounterId());
                page1.setPageNo(1);
                page1.setPageSize(5);
                item1.setPageMode(page1);
                item1.setDealType("01");
                Page<SelectOptionDto> pagelist = joyinbusinessCommonService.queryMySideList(item1);
                if (!CollectionUtils.isEmpty(pagelist.getRoot())) {
                    item.setPartyId((String) pagelist.getRoot().get(0).getObject());
                }
            }
            Page<SelectOptionDto> list = joyinbusinessCommonService.queryMySideTraderList(item);
            return list;
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return null;
        }
    }

    @ApiOperation(value = "查询是否走流程", notes = "查询是否走流程Y 走  N 不走  pageId elementId 必传")
    @PostMapping("/isNeedWorkflow")
    public Message<String> isNeedWorkflow(@RequestBody Map<String, String> param) {
        try {
            String needWorkflow = joyinbusinessCommonService.isNeedWorkflow(param);
            return new Message<String>(needWorkflow).success();
        } catch (Exception e) {
            return new Message<String>().fail().setMessage("查询是否走流程失败");
        }
    }

    /**
     * 审批页面 根据id查询本方或对手方
     *
     * @param params
     * @return java.util.List<com.joyintech.framework.select.SelectOptionDto>
     * @author jiangpeng
     * @date 2022/1/14 14:25
     */
    @RequestMapping("queryMySideOrCount")
    @ResponseBody
    public List<SelectOptionDto> queryMySideOrCount(@RequestParam Map<String, Object> params) {
        CfetsSearchInfoRequest item = new CfetsSearchInfoRequest();
        item.setDealType((String) params.get("dealType"));
        item.setSearch((String) params.get("initValue"));
        try {
            return joyinbusinessCommonService.queryMySideOrCount(item);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return null;
        }
    }

    /**
     * 审批页面 根据id查询本方交易员或对手方交易员
     *
     * @param params
     * @return java.util.List<com.joyintech.framework.select.SelectOptionDto>
     * @author jiangpeng
     * @date 2022/1/14 14:25
     */
    @RequestMapping("queryMySideTraderOrCountTrader")
    @ResponseBody
    public List<SelectOptionDto> queryMySideTraderOrCountTrader(@RequestParam Map<String, Object> params) {
        CfetsSearchInfoRequest item = new CfetsSearchInfoRequest();
        String dealType = (String) params.get("dealType");//查询类型
        String counterId_Sel = (String) params.get("counterId_Sel");//交易对手方
        String counterId = (String) params.get("counterId");//交易对手方
        String mySide = (String) params.get("mySide");//本方
        String initValue = (String) params.get("initValue");//关键字
        if (StringUtil.isNotEmpty(dealType)) {
            item.setDealType(dealType);
        }
        if (StringUtil.isNotEmpty(counterId)) {
            item.setPartyId(counterId);
        } else if (StringUtil.isNotEmpty(counterId_Sel)) {
            item.setPartyId(counterId_Sel);
        } else if (StringUtil.isNotEmpty(mySide)) {
            item.setPartyId(mySide);
        }
        if (StringUtil.isNotEmpty(initValue)) {
            item.setSearch(initValue);
        }
        try {
            return joyinbusinessCommonService.queryMySideTraderOrCountTrader(item);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return null;
        }
    }

    @PostMapping("/gettotleTradeId")
    @ApiOperation(value = "交易登记业务编号列表查询", notes = "交易登记业务编号列表查询")
    @ResponseBody
    public Message<Page<SelectOptionDto>> gettotleTradeId(@RequestBody CfetsSearchInfoRequest entrustIdRequestDto) {
        try {
            Page<SelectOptionDto> tempList = joyinbusinessCommonService.gettotleTradeId(entrustIdRequestDto);
            return new Message<Page<SelectOptionDto>>().success().setData(tempList);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @PostMapping("/calPendingPeriod/{secId:.+}")
    @ApiOperation(value = "计算待偿期", notes = "计算待偿期")
    @ResponseBody
    public Message<String> calPendingPeriod(@PathVariable String secId) {
        try {
            String pendingPeriod = joyinbusinessCommonService.calPendingPeriod(secId);
            return new Message<String>().success().setData(pendingPeriod);
        } catch (Exception e) {
            logger.error("计算功能异常", e);
            return new Message<String>().fail().setMessage("计算查询异常");
        }
    }

    @PostMapping("/getPermissionsByUserId")
    @ApiOperation(value = "查询用户权限接口", notes = "查询用户权限接口")
    @ResponseBody
    public Message<Map<String, Object>> getPermissionsByUserId() {
        try {
            Map<String, Object> resultMap = new HashMap<>();
            List<String> list = new ArrayList<>();
            String userId = SecurityUtil.getLoginUser().getUserName();
            if (!SecurityUtil.getLoginUser().isAdmin() && StringUtils.isNotEmpty(userId)) {
                list = userService.getPermissions(userId);
            }
            resultMap.put("isAdmin",Boolean.TRUE);
            resultMap.put("PermissionLists",list);
            return new Message<Map<String, Object>>().success().setData(resultMap);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Map<String, Object>>().fail().setMessage("查询异常");
        }
    }

}
