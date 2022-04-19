package com.joyintech.tams.business.common.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.workflow.engine.controller.WorkFlowController;
import com.joyintech.workflow.engine.service.UserSelectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common/workflow")
@Api(tags = "工作流相关-兼容资管3.0框架")
public class JoyinTamsWorkFlowController  {
	
	private static final Logger logger = LoggerFactory.getLogger(JoyinTamsWorkFlowController.class);
	
	@Resource
	private UserSelectService userSelectService;
	@Resource
	private WorkFlowController workFlowController;
	@Autowired
	private HttpServletRequest request;
	 
	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = "查询审批人员列表", notes = "/common/workflow/listNextUser?pageId=CfetsEtrBondInfo001&elementId=CfetsEtrBondInfo001_btn_submit&searchType=user&searchValue=搜索用户")
	@PostMapping("/listNextUser")
	public Message<List<?>> listNextUser(){
		try {
			String pageId = request.getParameter("pageId");
			String elementId = request.getParameter("elementId");
			String searchValue = request.getParameter("searchValue");
			String searchType = request.getParameter("searchType");
			if (StringUtils.isEmpty(pageId)) {
				return new Message<List<?>>().fail().setMessage("pageId参数不能为null");
			}
			if (StringUtils.isEmpty(elementId)) {
				return new Message<List<?>>().fail().setMessage("elementId参数不能为null");
			}
			//请求:
			//  processInstanceId: 
			//  taskId: 
			//  pageId: EtrInvestBondInfo002
			//  elementId: EtrInvestBondInfo002_btn_submit
			//  businessInfo: {"finprodId":"F01_010007.IB","calendarId":"03","finvdate":"2001-07-31 00:00:00","finmdate":"2021-07-31 00:00:00","isRight":"N","profitType":"","portfolioId":"2222","finprodMarketId":"010007.IB","tradeMarketMode":"02","ps":"T01","invAim":"FVTPL","entrustNum":"1000000.00","entrustOwnNum":"1000000.00","entrustIntegrateNum":"0.00","priceModel":"PRM01","price":"100.0000","matureYield":"4.180306271706","minAmt":"","settleSpreed":"0","secAcctId":"","chlId":"","remark":"","riskMathFlag":"02","bankCode":"002","vdate":"2021-07-12","mdate":"2021-07-12","assetType1":"02","assetType10":"03","assetType11":"003","ccy":"CNY","finprodName":"01国债7","tradeMarket":"01","tradePlat":"01","availableNum":"100","fpriceAmt":"101.90640884","exerYield":"","proAvailableCash":"0.00","proAvailableCash1":"0.00","etrInvestPersonInfo":{"realName":"system","room":"资管部门","profitType":"","counterId":"","counterTrader":"","contactInfo":"","invManagerId":"system"},"riskTrialRetDto":[],"pageModel":"0","draftOption":"","operateType":"submit","entrustId":"","entrustStatus":"01","fileArray":[],"parentPageId":"EtrInvestBondInfoIb001"}
			//  elementIdForLog: EtrInvestBondInfo002_btn_submit
			//  pageIdForLog: EtrInvestBondInfo002
			//  isTodoList: false
			//响应:
			//  {"sortable":false,"statuCode":200,"errorList":[],
			//   "data":{"isSendUorg":"02","moduleId":"oneApproval","type":"role","nodeId":"sid-CE4E5CDC-B51F-4762-907F-5966276983D2",
			//           "nodeData":"{\"initUserIds\":\"null\",\"userNames\":\"null\"}"
			//          }
			//  }
			AjaxResult nextTask = workFlowController.getNextTask();
			Map<String, String> data = (Map<String, String>) nextTask.getData();
			String nodeId = data.get("nodeId");
			String isSendUorg = data.get("isSendUorg");
			String moduleId = data.get("moduleId");
			
			//请求:
			//  from: workflow
			//  searchType: user
			//  moduleId: oneApproval
			//  nodeId: sid-CE4E5CDC-B51F-4762-907F-5966276983D2
			//  isSendUorg: 02
			//  searchValue: admin
			//  pageId: EtrInvestBondInfo002
			//  showUserIds: 
			//  default: 
			//  pageIdForLog: EtrInvestBondInfo002
			//  elementIdForLog: btnSearch
			//响应:
			// [{"userName":"ai2","realName":"ai2","expired":false,"locked":false,"admin":false},
			//  {"userName":"JT01","realName":"JT01","expired":false,"locked":false,"admin":false}]
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("from", "workflow");
			params.put("searchType", searchType);
			params.put("moduleId", moduleId);
			params.put("nodeId", nodeId);
			params.put("isSendUorg", isSendUorg);
			params.put("searchValue", searchValue);
			params.put("pageId", pageId);
			List<?> list = userSelectService.findByLikeString(params);
            return new Message<List<?>>(list).success();
        } catch (Exception e) {
            logger.error("查询审批人员异常", e);
            return new Message<List<?>>().fail().setMessage("查询审批人员失败");
        }
	}
	
//    @Autowired(required=false)
//    private BaseGetBusinessId bussGetBussinessIdService;
//    @Autowired
//    private EtrInvestBondInfoService etrInvestBondInfoService;
//    
//	@ApiOperation(value = "保存测试", notes = "/common/workflow/save")
//	@PostMapping("save")
//	public AjaxResult save(@RequestBody EtrInvestBondInfoDto dto) throws Exception {
//		if(!StringUtil.isNotEmpty(dto.getEntrustId())){
//            BussinessCodeParameter param = new BussinessCodeParameter();
//            param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
//            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
//            dto.setEntrustId(code);
//        }
//        // ----------add by zhangrui1 20200703 预占功能 start----------
//        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
//            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
//                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
//                    DictCodeList.平安资产类型_债券.getValue());
//            if (StringUtil.isNotEmpty(ar)) {
//                return ar;
//            }
//        }
//        // ----------add by zhangrui1 20200703 预占功能 end----------
//        ProcessParameter<EtrInvestBondInfoDto> processParameter = new ProcessParameter<EtrInvestBondInfoDto>();
//        WorkFlowParameter<EtrInvestBondInfoDto> params = new WorkFlowParameter<EtrInvestBondInfoDto>();
//        params.setPageId("EtrInvestBondInfo002");
//        params.setElementId(dto.getElementId());
//        params.setBusinessCode(dto.getEntrustId());
//        params.setBusinessType(DictCodeList.共通业务类型_债券投资委托.getValue());
//        // 设置流程业务说明, entity即传入的dto
//        params.setComment(
//            entity -> this.getMessage("EtrInvestBondInfo001") + "(" + entity.getPortfolioId() + ")"
//                      + "-" + this.getMessage("EtrInvestBondInfo001.ps")+"("+SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs())+")" + "-" + "("
//                      + this.getMessage("EtrInvestBondInfo001.vdate") + ":" + DateUtil.format(entity.getVdate())
//                      + ")-(" + this.getMessage("EtrInvestBondInfo001.mdate") + ":"
//                      + DateUtil.format(entity.getMdate())+ ")-"
//                      + this.getMessage("EtrInvestBondInfo001.entrustAmt") + ":"
//                      + entity.getEntrustNum());
//        processParameter.setWorkFlowParameter(params);
//        dto.setWorkflowParameter("new|chumi,xuq,xwy,zhangjian,zhengfeng,duorou,killer,liuyuliang||1|EtrInvestBondInfo002|EtrInvestBondInfo002_btn_submit|false");
//        String msg = etrInvestBondInfoService.start(processParameter, dto);
//        return new AjaxResult(msg);
//	}
//	http://192.168.70.246:8080/tams/invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/save?operType=new&elementIdForLog=EtrInvestBondInfo002_btn_submit&pageIdForLog=EtrInvestBondInfo002&isTodoList=false
//	{"finprodId":"F01_010007.IB","calendarId":"03","finvdate":"2001-07-31 00:00:00","finmdate":"2021-07-31 00:00:00",
//	 "isRight":"N","profitType":"","portfolioId":"2222","finprodMarketId":"010007.IB","tradeMarketMode":"02","ps":"T01",
//	 "invAim":"FVTPL","entrustNum":"1000000.00","entrustOwnNum":"1000000.00","entrustIntegrateNum":"0.00","priceModel":"PRM01",
//	 "price":"100.0000","matureYield":"4.180306271706","minAmt":"","settleSpreed":"0","secAcctId":"","chlId":"","remark":"",
//	 "riskMathFlag":"02","bankCode":"002","vdate":"2021-07-12","mdate":"2021-07-12","assetType1":"02","assetType10":"03",
//	 "assetType11":"003","ccy":"CNY","finprodName":"01国债7","tradeMarket":"01","tradePlat":"01","availableNum":"100",
//	 "fpriceAmt":"101.90640884","exerYield":"","proAvailableCash":"0.00","proAvailableCash1":"0.00",
//	 "etrInvestPersonInfo":{"realName":"system","room":"资管部门","profitType":"","counterId":"","counterTrader":"","contactInfo":"","invManagerId":"system"},
//	 "riskTrialRetDto":[],"pageModel":"0","draftOption":"","operateType":"submit","entrustId":"","entrustStatus":"01","fileArray":[],
//	 "parentPageId":"EtrInvestBondInfoIb001","elementId":"EtrInvestBondInfo002_btn_submit","submit":"1","isTodoList":false,
//	 "workflowParameter":"new|chumi,xuq,xwy,zhangjian,zhengfeng,duorou,killer,liuyuliang||1|EtrInvestBondInfo002|EtrInvestBondInfo002_btn_submit|false",
//	 "todoListFlag":false,"operType":"new","pageId":"EtrInvestBondInfo002",
//	 "users":"chumi,xuq,xwy,zhangjian,zhengfeng,duorou,killer,liuyuliang","roles":""
//	}
	
//	{"finprodId":"F01_010007.IB","calendarId":"03","finvdate":"2001-07-31 00:00:00","finmdate":"2021-07-31 00:00:00","isRight":"N","profitType":"","portfolioId":"2222","finprodMarketId":"010007.IB","tradeMarketMode":"02","ps":"T01","invAim":"FVTPL","entrustNum":"10000.00","entrustOwnNum":"10000.00","entrustIntegrateNum":"0.00","priceModel":"PRM01","price":"100.0000","matureYield":"4.180306271706","minAmt":"","settleSpreed":"0","secAcctId":"","chlId":"","remark":"","riskMathFlag":"02","bankCode":"002","vdate":"2021-07-12","mdate":"2021-07-12","assetType1":"02","assetType10":"03","assetType11":"003","ccy":"CNY","finprodName":"01国债7","tradeMarket":"01","tradePlat":"01","availableNum":"100","fpriceAmt":"101.90640884","exerYield":"","proAvailableCash":"-1019064.09","proAvailableCash1":"-1019064.09","etrInvestPersonInfo":{"realName":"system","room":"资管部门","profitType":"","counterId":"","counterTrader":"","contactInfo":"","invManagerId":"system"},"riskTrialRetDto":[],"pageModel":"0","draftOption":"","operateType":"submit","entrustId":"","entrustStatus":"01","fileArray":[],"parentPageId":"EtrInvestBondInfoIb001","workflowParameter":"new|chumi,xuq,xwy,zhangjian,zhengfeng,duorou,killer,liuyuliang||1|EtrInvestBondInfo002|EtrInvestBondInfo002_btn_submit|false","todoListFlag":false,"operType":"new","users":"chumi,xuq,xwy,zhangjian,zhengfeng,duorou,killer,liuyuliang","roles":"","pageId":"EtrInvestBondInfo002","elementId":"EtrInvestBondInfo002_btn_submit","submit":"1","isTodoList":false}
}
