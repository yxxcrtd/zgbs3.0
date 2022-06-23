package com.joyintech.tams.mobileApproval.service;


import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.PasswordHelper;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.util.*;
import com.joyintech.system.constant.SystemDictCode;
import com.joyintech.system.main.dao.MainMapper;
import com.joyintech.system.main.dto.WorkFlowInfoDto;
import com.joyintech.system.main.service.MainService;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.product.productelement.build.dto.FinProductBuild002Dto;
import com.joyintech.tams.mobileApproval.dao.MobileApprovalMapper;
import com.joyintech.tams.mobileApproval.dto.*;
import com.joyintech.tams.mobileApproval.util.MobileUtils;
import com.joyintech.tams.mobileApproval.util.Office2PDFUtils;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.constant.Whether;
import com.joyintech.workflow.engine.controller.WorkFlowController;
import com.joyintech.workflow.engine.dto.WflStatusDto;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.entity.WorkflowEntity;
import com.joyintech.workflow.engine.exception.WflCode;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.service.BusinessService;
import com.joyintech.workflow.engine.service.ProcessService;
import com.joyintech.workflow.engine.service.WorkflowMethodInterceptor;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 移动审批
 *
 * @author houjifei
 */
@Service
public class MobileApprovalService extends CommonExportService<PreInvestApproveInfoDto> implements BaseProcessService<PreInvestApproveInfoDto> {

    @Autowired
    private MobileApprovalMapper mobileApprovalMapper;

    @Autowired
    private MainService mainService;
    
    @Autowired
    private MainMapper mainMapper;
    
    @Autowired
    private ProcessService processService;
    
    @Autowired
    private MobilePtlManagementInfoService mobilePtlManagementInfoService;
    
    @Autowired
    private MobileMstTradeOpponentService mobileMstTradeOpponentService;
    @Autowired
    private MobileTrdPlanRateInvestTradeService mobileTrdPlanRateInvestTradeService;
    @Autowired
    private MobileTrdPlanCcyInvestTradeService mobileTrdPlanCcyInvestTradeService;
    @Autowired
    private MobileTrdProjectInvestTradeService mobileTrdProjectInvestTradeService;
    @Autowired
    private MobileTrdOutRightRepoTradeService mobileTrdOutRightRepoTradeService;
    @Autowired
    private MobilePtlCashFlowService mobilePtlCashFlowService;
    @Autowired
    private MobileTrdBondDurationService mobileTrdBondDurationService;
    @Autowired
    private MobileTrdRepoDurationService mobileTrdRepoDurationService;
    @Autowired
    private MobileTrdDepoDurationService mobileTrdDepoDurationService;
    @Autowired
    private MobileTrdLendDurationService mobileTrdLendDurationService;
    @Autowired
    private MobileTrdPlanNetDurationService mobileTrdPlanNetDurationService;
    @Autowired
    private MobileTrdPlanInterestDurationService mobileTrdPlanInterestDurationService;
    @Autowired
    private MobileTrdPlanCurrDurationService mobileTrdPlanCurrDurationService;
    @Autowired
    private MobileTrdProjectDurationService mobileTrdProjectDurationService;
    @Autowired
    private MobileCeOrgService mobileCeOrgService;
    @Autowired
    private MobileRskAuthorityManageService mobileRskAuthorityManageService;
    @Autowired
    private MobileOrgNameManagementService mobileOrgNameManagementService;
    
    @Autowired
    private MobileMstBondQuotationInfoService mobileMstBondQuotationInfoService;
    
    @Autowired
    private MobileMstNetAssetQuotationInfoService mobileMstNetAssetQuotationInfoService;
    
    @Autowired
    private MobilePtlFundAllocationService mobilePtlFundAllocationService;
    
    @Autowired
    private MobilePtlDvpFundAllocationService mobilePtlDvpFundAllocationService;
    
    @Autowired
    private MobilePtlChlFundAllocationService mobilePtlChlFundAllocationService;
    
    @Autowired
    private MobileFinBondTargetAssetService mobileFinBondTargetAssetService;
    
    @Autowired
    private MobileFinDepositTargetService mobileFinDepositTargetService;
    
    @Autowired
    private MobileFinProjectTargetAssetService mobileFinProjectTargetAssetService;
    
    @Autowired
    private MobileEtrBondFirstLBiddingService mobileEtrBondFirstLBiddingService;
    
    @Autowired
    private MobileEtrInvestBondInfoService mobileEtrInvestBondInfoService;
    
    @Autowired
    private MobileEtrRepoService mobileEtrRepoService;
    
    @Autowired
    private MobileEtrBrowDepoInfoService mobileEtrBrowDepoInfoService;
    
    @Autowired
    private MobileEtrLendSpecInfoService mobileEtrLendSpecInfoService;
    
    @Autowired
    private MobileEtrFundService mobileEtrFundService;
    
    @Autowired
    private MobileEtrPlanAssetNService mobileEtrPlanAssetNService;
    
    @Autowired
    private MobileEtrPlanAssetRService mobileEtrPlanAssetRService;
    
    @Autowired
    private MobileEtrPlanAssetCService mobileEtrPlanAssetCService;
    
    @Autowired
    private MobileEtrProjectService mobileEtrProjectService;
    
    @Autowired
    private MobileEtrBoutRepoService mobileEtrBoutRepoService;
    
    @Autowired
    private MobileTrdBondInvestTradeService mobileTrdBondInvestTradeService;
    
    @Autowired
    private MobileTrdRepoTradeService mobileTrdRepoTradeService;
    
    @Autowired
    private MobileTrdDepositInvestTradeService mobileTrdDepositInvestTradeService;
    
    @Autowired
    private MobileTrdLendInvestTradeService mobileTrdLendInvestTradeService;
    
    @Autowired
    private MobileTrdFundInvestTradeService mobileTrdFundInvestTradeService;
    
    @Autowired
    private MobileTrdPlanNetInvestTradeService mobileTrdPlanNetInvestTradeService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowController.class);

    @Override
    public void doComplete(PreInvestApproveInfoDto dto, OperationType operationType) {

    }

    public String getNewTodoList( Map<String, Object> param) throws Exception {
        Map<String, Object> para = new HashMap<>();
        para.put("userName", param.get("userId")+"");
//        para.put("status", "A");
        //汇总出资产预审批的当前核审人
        List<ReturnTodoDto> dtoList = mobileApprovalMapper.queryTodoList(para);
        List<WorkFlowInfoDto> workFlowDto = new ArrayList<>();
        Map<String, Object> paramap = new HashMap<>();

        //业务类型
        List<String> list01 = new ArrayList<>();
        if(param.containsKey("auditType")){
            list01.add(param.get("auditType")+"");
        }
        paramap.put("businessType",list01);
        //查询起始日
//        paramap.put("startCrtDate",param.get("startCrtDate")+"");
        //查询结束日
//        paramap.put("endCrtDate",param.get("endCrtDate")+"");
        
//        for (ReturnTodoDto dto : dtoList) {
//            if (null != this.getCurrentInfo(dto.getWorkflowId(),paramap)) {
//                workFlowDto.add(this.getCurrentInfo(dto.getWorkflowId(),paramap));
//            }
//        }
        List<WorkFlowInfoDto> myWorks= mainService.getToDoListData((String) param.get("userId"), paramap);
        for (int i = 0; i < dtoList.size(); i++) {
			for (int j = 0; j < myWorks.size(); j++) {
                if(!"EtrStandardBond002".equals(dtoList.get(i).getPageId())) {
                    //计划类净值型，货币性，共募基金交易生成的交易确认在新建状态下不显示在app
                    if(!(("TrdPlanNetInvestTrade003".equals(myWorks.get(j).getPageId())&&myWorks.get(j).getProcessState().equals("NEW"))||
                            ("TrdFundInvestTrade003".equals(myWorks.get(j).getPageId())&&myWorks.get(j).getProcessState().equals("NEW"))||
                            ("TrdPlanCcyInvestTrade003".equals(myWorks.get(j).getPageId())&&myWorks.get(j).getProcessState().equals("NEW"))
                    ))
                        workFlowDto.add(myWorks.get(j));
                }
			}
		}
        
        String flag="false";
        if(null != workFlowDto){
            //进行分页处理
            if(param.containsKey("pageNo") && param.containsKey("pageSize")){
                int page = Integer.valueOf((String)param.get("pageNo"));
                int length = Integer.valueOf((String)param.get("pageSize"));
                int beginIndex = (page-1)*length;
                int endIndex = page*length;
                int count =workFlowDto.size();
                if(endIndex < count) {
                	flag="true";
                }
                
                if(beginIndex > count){
                    beginIndex = 0;
                }

                if(endIndex >count){
                    endIndex = count;
                }

                workFlowDto = workFlowDto.subList(beginIndex,endIndex);
            }
        }

        //按照接口规范进行传输数据
        List<Map> lst = new ArrayList<>();
        for(WorkFlowInfoDto vo : workFlowDto){
            Map map = new HashMap();
            //颜色
            map.put("color","");
            //副标题3
            map.put("subTitle3",vo.getTitle());
            //交易流水号
            map.put("processId",vo.getCurTaskId());
            //副标题2
            map.put("subTitle2",vo.getTitle());
            //副标题1
            map.put("subTitle1",vo.getBusinessCode());
            //图标
            map.put("icon","");
            //任务时间
            map.put("itemDate",vo.getCrtDate());
            
            int f=0;
          //标题
            
            map.put("title","("+vo.getActionType()+")"+vo.getBusinessType());
            

            //业务类型
            map.put("businessType",vo.getBusinessTypeValue());
            //任务id
            map.put("taskId",vo.getCurTaskId());
            //请求记录id
            map.put("reqId",vo.getWorkflowId());
            
            lst.add(map);

        }
        Map m=new HashMap<String, Object>();
        m.put("nextPage", flag);
        m.put("rows", lst);
        return MobileUtils.Objetc2Json("000",m,"");
//        return JsonUtils.object2Json(workFlowDto);
    }
    
    
    public String getDoneList( Map<String, Object> param) throws Exception {
        Map<String, Object> para = new HashMap<>();
        para.put("userId", param.get("userId")+"");
//        para.put("status", "A");
        //汇总出资产预审批的当前核审人
        List<WorkFlowInfoDto> workFlowDto = new ArrayList<>();
        Map<String, Object> paramap = new HashMap<>();

        //业务类型
        List<String> list01 = new ArrayList<>();
        if(param.containsKey("auditType")){
            list01.add(param.get("auditType")+"");
        }
        paramap.put("businessType",list01);
        paramap.put("userId",param.get("userId")+"");
        
        List<ReturnTodoDto> workflowIds = mobileApprovalMapper.getMyWorkflowId(para);
        List<WorkFlowInfoDto> myWorks=getMyWorkData(paramap);
        for (int i = 0; i < workflowIds.size(); i++) {
			for (int j = 0; j < myWorks.size(); j++) {
				if(workflowIds.get(i).getWorkflowId().equals(myWorks.get(j).getWorkflowId())) {
					WorkFlowInfoDto wf=myWorks.get(j);
					wf.setPageId(workflowIds.get(i).getPageId());
					if(!"EtrStandardBond002".equals(workflowIds.get(i).getPageId())) {
						workFlowDto.add(myWorks.get(j));
					}
				}
			}
		}
        String flag="false";
        if(null != workFlowDto){
            //进行分页处理
            if(param.containsKey("pageNo") && param.containsKey("pageSize")){
                int page = Integer.valueOf((String)param.get("pageNo"));
                int length = Integer.valueOf((String)param.get("pageSize"));
                int beginIndex = (page-1)*length;
                int endIndex = page*length;
                int count =workFlowDto.size();
                if(endIndex < count) {
                	flag="true";
                }

                if(beginIndex > count){
                    beginIndex = 0;
                }

                if(endIndex >count){
                    endIndex = count;
                }

                workFlowDto = workFlowDto.subList(beginIndex,endIndex);
            }
        }

        //按照接口规范进行传输数据
        List<Map> lst = new ArrayList<>();
        for(WorkFlowInfoDto vo : workFlowDto){
            Map map = new HashMap();
            //颜色
            map.put("color","");
            //副标题3
            map.put("subTitle3",vo.getTitle());
            //交易流水号
            map.put("processId",vo.getCurTaskId());
            //副标题2
            map.put("subTitle2",vo.getTitle());
            //副标题1
            map.put("subTitle1",vo.getBusinessCode());
            //图标
            map.put("icon","");
            //任务时间
            map.put("itemDate",vo.getCrtDate());
            
            //标题
            map.put("title","("+vo.getActionType()+")"+vo.getBusinessType());
            //业务类型
            map.put("businessType",vo.getBusinessTypeValue());
            //任务id
            map.put("taskId",vo.getCurTaskId());
            //请求记录id
            map.put("reqId",vo.getWorkflowId());
            
            for (int i = 0; i < workflowIds.size(); i++) {
            	if(vo.getWorkflowId().equals(workflowIds.get(i).getWorkflowId())) {
            		//审批时间
                    map.put("updateTime",workflowIds.get(i).getUpdateTime());
            	}
            }
            
            lst.add(map);

        }
        Map m=new HashMap<String, Object>();
        m.put("nextPage", flag);
        m.put("rows", lst);
        return MobileUtils.Objetc2Json("000",m,"");
//        return JsonUtils.object2Json(workFlowDto);
    }

    /**
     * <b>方法：</b>getAuditTypeList<br>
     * <b>描述：</b>获取审核类型任务数据量<br>
     * <b>作者：</b>zhuruqing<br>
     * <b>时间：</b>2020/5/6 11:12<br>
     * <b>参数：</b>[userName]<br>
     * <b>返回值：</b>java.lang.String<br>
     */
    public String getAuditTypeList(String userName) throws Exception {
        Map<String, Object> para = new HashMap<>();
        para.put("userName", userName);
//        para.put("status", "A");
        //汇总出资产预审批的当前核审人
        List<ReturnTodoDto> dtoList = mobileApprovalMapper.queryTodoList(para);
        List<WorkFlowInfoDto> workFlowDto = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
//        for (ReturnTodoDto dto : dtoList) {
//            if (null != this.getCurrentInfo(dto.getWorkflowId(),param)) {
//                workFlowDto.add(this.getCurrentInfo(dto.getWorkflowId(),param));
//            }
//        }
        List<WorkFlowInfoDto> myWorks= mainService.getToDoListData(userName, param);
        for (int i = 0; i < dtoList.size(); i++) {
			for (int j = 0; j < myWorks.size(); j++) {
				if(dtoList.get(i).getWorkflowId().equals(myWorks.get(j).getWorkflowId())) {
					WorkFlowInfoDto wf=myWorks.get(j);
					wf.setPageId(dtoList.get(i).getPageId());
                    if(!"EtrStandardBond002".equals(dtoList.get(i).getPageId())) {
                        if(!(("TrdPlanNetInvestTrade003".equals(myWorks.get(j).getPageId())&&myWorks.get(j).getProcessState().equals("NEW"))||
                                ("TrdFundInvestTrade003".equals(myWorks.get(j).getPageId())&&myWorks.get(j).getProcessState().equals("NEW"))||
                                ("TrdPlanCcyInvestTrade003".equals(myWorks.get(j).getPageId())&&myWorks.get(j).getProcessState().equals("NEW"))
                        ))
                            workFlowDto.add(myWorks.get(j));
                    }
				}
			}
		}
        Map<String,List<WorkFlowInfoDto>> typemap = new LinkedHashMap<String, List<WorkFlowInfoDto>>();
        List<OneMenuDto> oneMenuDtos = new ArrayList<>();
        int index=0;
        for(WorkFlowInfoDto vo : workFlowDto){
            //根据pageid查询各个模块所属的一级菜单
            String pageid = vo.getPageId();
            pageid = pageid.substring(0,pageid.length()-1)+"1";
            OneMenuDto mevo = mobileApprovalMapper.queryMenuInfoByPageId(pageid);
            if(null!=mevo){
                List<WorkFlowInfoDto> lst = typemap.get(mevo.getMenuId()+"&"+mevo.getMenuName());
                if(null != lst && lst.size()>0){
                    lst.add(vo);
                }else{
                    lst = new ArrayList<>();
                    lst.add(vo);
                    typemap.put(mevo.getMenuId()+"&"+mevo.getMenuName(),lst);
                }
            }

        }
        //处理叶子节点数据

        for(String vo : typemap.keySet()){
            OneMenuDto oneMenuDto = new OneMenuDto();
            List<WorkFlowInfoDto>  list1 = typemap.get(vo);
            Map<String,List<WorkFlowInfoDto>>  leafmap = new LinkedHashMap<String,List<WorkFlowInfoDto>>();
            for(WorkFlowInfoDto dto : list1){
                List<WorkFlowInfoDto> twolist = leafmap.get(dto.getBusinessTypeValue());
                if(null != twolist && twolist.size()>0){
                    twolist.add(dto);
                }else{
                    twolist = new ArrayList<>();
                    twolist.add(dto);
                    leafmap.put(dto.getBusinessTypeValue(),twolist);
                }
            }

            List<TwoMenuDto> twoMenuDtos = new ArrayList<>();
            for(String key:leafmap.keySet()){
                TwoMenuDto dto2 = new TwoMenuDto();
                dto2.setAuditType(key);
                dto2.setColor("");
                dto2.setIcon("");
                dto2.setMenuId(String.valueOf(index++));
                dto2.setTodoNum(leafmap.get(key).size());
                dto2.setMunuName(SysDataUtil.getNameFromDataDict(SystemDictCode.共通业务类型, leafmap.get(key).get(0).getBusinessTypeValue()));
                twoMenuDtos.add(dto2);
            }

            oneMenuDto.setMenuId(vo.split("&")[0]);
            oneMenuDto.setMenuName(vo.split("&")[1]);
            oneMenuDto.setBusinessType(oneMenuDto.getMenuName());
            oneMenuDto.setLeaf(twoMenuDtos);
            oneMenuDtos.add(oneMenuDto);
        }


        //组装数据
        Map map = new HashMap();
        map.put("rows", JSONObject.toJSON(oneMenuDtos));

        Map map1 = new HashMap();
        map1.put("code","000");
        map1.put("data",JSONObject.toJSON(map));
        map1.put("message","");
        return JSONObject.toJSONString(map1);


//        return MobileUtils.Objetc2Json("000",map,"");
    }
    
    
    /**
     * <b>方法：</b>getAuditTypeDoneList<br>
     * <b>描述：</b>获取已审数据<br>
     * <b>作者：</b>YH<br>
     * <b>时间：</b>2020/5/6 11:12<br>
     * <b>参数：</b>[userName]<br>
     * <b>返回值：</b>java.lang.String<br>
     */
    public String getAuditTypeDoneList(String userName) throws Exception {
        Map<String, Object> para = new HashMap<>();
        para.put("userId", userName);
        List<ReturnTodoDto> workflowIds = mobileApprovalMapper.getMyWorkflowId(para);
        List<WorkFlowInfoDto> workFlowDto = new ArrayList<>();
        List<WorkFlowInfoDto> myWorks=getMyWorkData(para);
        for (int i = 0; i < workflowIds.size(); i++) {
			for (int j = 0; j < myWorks.size(); j++) {
				if(workflowIds.get(i).getWorkflowId().equals(myWorks.get(j).getWorkflowId())) {
					WorkFlowInfoDto wf=myWorks.get(j);
					wf.setPageId(workflowIds.get(i).getPageId());
					if(!"EtrStandardBond002".equals(workflowIds.get(i).getPageId())) {
						workFlowDto.add(wf);
					}
				}
			}
		}
        Map<String,List<WorkFlowInfoDto>> typemap = new LinkedHashMap<String,List<WorkFlowInfoDto>>();
        List<OneMenuDto> oneMenuDtos = new ArrayList<>();
        int index=0;
        for(WorkFlowInfoDto vo : workFlowDto){
            //根据pageid查询各个模块所属的一级菜单
            String pageid = vo.getPageId();
            pageid = pageid.substring(0,pageid.length()-1)+"1";
            OneMenuDto mevo = mobileApprovalMapper.queryMenuInfoByPageId(pageid);
            if(null!=mevo){
                List<WorkFlowInfoDto> lst = typemap.get(mevo.getMenuId()+"&"+mevo.getMenuName());
                if(null != lst && lst.size()>0){
                    lst.add(vo);
                }else{
                    lst = new ArrayList<>();
                    lst.add(vo);
                    typemap.put(mevo.getMenuId()+"&"+mevo.getMenuName(),lst);
                }
            }

        }
        //处理叶子节点数据

        for(String vo : typemap.keySet()){
            OneMenuDto oneMenuDto = new OneMenuDto();
            List<WorkFlowInfoDto>  list1 = typemap.get(vo);
            Map<String,List<WorkFlowInfoDto>>  leafmap = new LinkedHashMap<String, List<WorkFlowInfoDto>>();
            for(WorkFlowInfoDto dto : list1){
                List<WorkFlowInfoDto> twolist = leafmap.get(dto.getBusinessTypeValue());
                if(null != twolist && twolist.size()>0){
                    twolist.add(dto);
                }else{
                    twolist = new ArrayList<>();
                    twolist.add(dto);
                    leafmap.put(dto.getBusinessTypeValue(),twolist);
                }
            }

            List<TwoMenuDto> twoMenuDtos = new ArrayList<>();
            for(String key:leafmap.keySet()){
                TwoMenuDto dto2 = new TwoMenuDto();
                dto2.setAuditType(key);
                dto2.setColor("");
                dto2.setIcon("");
                dto2.setMenuId(String.valueOf(index++));
                dto2.setTodoNum(leafmap.get(key).size());
                dto2.setMunuName(SysDataUtil.getNameFromDataDict(SystemDictCode.共通业务类型, leafmap.get(key).get(0).getBusinessTypeValue()));
                twoMenuDtos.add(dto2);
            }

            oneMenuDto.setMenuId(vo.split("&")[0]);
            oneMenuDto.setMenuName(vo.split("&")[1]);
            oneMenuDto.setBusinessType(oneMenuDto.getMenuName());
            oneMenuDto.setLeaf(twoMenuDtos);
            oneMenuDtos.add(oneMenuDto);
        }


        //组装数据
        Map map = new HashMap();
        map.put("rows", JSONObject.toJSON(oneMenuDtos));

        Map map1 = new HashMap();
        map1.put("code","000");
        map1.put("data",JSONObject.toJSON(map));
        map1.put("message","");
        return JSONObject.toJSONString(map1);


//        return MobileUtils.Objetc2Json("000",map,"");
    }
    

    public int getMarkNum(String userName) throws Exception {
        Map<String, Object> para = new HashMap<>();
        para.put("userName", userName);
        //汇总出资产预审批的当前核审人
        List<ReturnTodoDto> dtoList = mobileApprovalMapper.queryTodoList(para);
        for (int i = 0; i < dtoList.size(); i++) {
            if(!("EtrStandardBond002".equals(dtoList.get(i).getPageId())||
                    ("TrdPlanNetInvestTrade003".equals(dtoList.get(i).getPageId())&&dtoList.get(i).getTradeStatus().equals("NEW"))||
                    ("TrdFundInvestTrade003".equals(dtoList.get(i).getPageId())&&dtoList.get(i).getTradeStatus().equals("NEW"))||
                    ("TrdPlanCcyInvestTrade003".equals(dtoList.get(i).getPageId())&&dtoList.get(i).getTradeStatus().equals("NEW"))
            )){
                dtoList.remove(i);
            }
        }
        
        return dtoList.size();
    }


    public String getWorkflowInfo(Map<String, Object> param) throws Exception {
        //查询流程中数据实体
    	
        String str="";
        ProcessMark make =  WorkflowContext.getProcessMarkService().getBusinessInfo(param.get("workFlowId").toString());
        Map<String, Object> parammap = new HashMap<>();
//        WorkFlowInfoDto workFlowInfoDto = this.getCurrentInfo(make.getWorkflowId(),parammap);
//        if(workFlowInfoDto.getNextUser()!=null&&workFlowInfoDto.getNextUser().indexOf(",")!=-1) {
//          String taskId = workFlowInfoDto.getCurTaskId();
//          processService.doClaim((String) param.get("userId"), taskId);
//        }
        parammap.put("workflowId", make.getWorkflowId());
        List<ReturnTodoDto> l=mobileApprovalMapper.queryTodoList(parammap);
        
        if(l!=null && l.size()>0) {
        	FlowEntity flowEntity = processService.getRuntimeEntity(l.get(0).getProcessInstanceId());
        	if(flowEntity!=null && flowEntity.getNextUser()!=null && flowEntity.getNextUser().indexOf(",")!=-1) {
        		processService.doClaim((String) param.get("userId"), (String) param.get("taskId"));
        	}
        }
        
        Object o = new Object();
        if("FINPROD04".equals(param.get("auditType").toString())){//产品要素 FinProductBuild002
            FinProductBuild002Dto dto =  JsonUtils.json2Object(make.getBusinessData(), FinProductBuild002Dto.class);
            dto.setProfitFlag( SysDataUtil.getNameFromDataDict(DictCode.收益类型, dto.getProfitFlag()));
        }else if("PTL01".equals(param.get("auditType").toString())){//组合管理 PtlManagementInfo002
            str = mobilePtlManagementInfoService.getWorkFlowInfo(make.getBusinessData());
        }else if("MST15".equals(param.get("auditType").toString())){//交易对手管理 MstTradeOpponent002
            str = mobileMstTradeOpponentService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD07".equals(param.get("auditType").toString())){//计划类资产（利率型）交易 TrdPlanRateInvestTrade002
            str = mobileTrdPlanRateInvestTradeService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD47".equals(param.get("auditType").toString())){//计划类资产（货币型）交易 private List<SysUploadFileInfo> fileArray;
            str = mobileTrdPlanCcyInvestTradeService.getWorkFlowInfo(make.getBusinessData());
       }else if("TRD09".equals(param.get("auditType").toString())){//项目类资产交易 TrdProjectInvestTrade002
            str = mobileTrdProjectInvestTradeService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD48".equals(param.get("auditType").toString())){//买断式回购交易 TrdOutRightRepoTrade002
            str = mobileTrdOutRightRepoTradeService.getWorkFlowInfo(make.getBusinessData());
        }else if("PTL04".equals(param.get("auditType").toString())){//自定义现金流 PtlCashFlow002
            str = mobilePtlCashFlowService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD10".equals(param.get("auditType").toString())){//债券存续期 TrdBondDuration002
            str = mobileTrdBondDurationService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD16".equals(param.get("auditType").toString())){//质押式回购存续期 TrdRepoDuration002
            str = mobileTrdRepoDurationService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD12".equals(param.get("auditType").toString())){//存款类存续期 TrdDepoDuration002 TrdDepoDuration003
            str = mobileTrdDepoDurationService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD11".equals(param.get("auditType").toString())){//拆借存续期 TrdLendDuration002
            str = mobileTrdLendDurationService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD13".equals(param.get("auditType").toString())){//计划类净值型存续期 TrdPlanNetDuration002
            str = mobileTrdPlanNetDurationService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD15".equals(param.get("auditType").toString())){//计划类利率型存续期 TrdPlanInterestDuration002
            str = mobileTrdPlanInterestDurationService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD50".equals(param.get("auditType").toString())){//计划类货币型存续期 TrdPlanCurrDuration002
            str = mobileTrdPlanCurrDurationService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD17".equals(param.get("auditType").toString())){//项目类资产存续期 TrdProjectDuration002
            str = mobileTrdProjectDurationService.getWorkFlowInfo(make.getBusinessData());
        }else if("RSK16".equals(param.get("auditType").toString())){//机构授信 CeOrg002
            str = mobileCeOrgService.getWorkFlowInfo(make.getBusinessData());
        }else if("RSK02".equals(param.get("auditType").toString())){//授权管理 RskAuthorityManage002
            str = mobileRskAuthorityManageService.getWorkFlowInfo(make.getBusinessData());
        }else if("RSK24".equals(param.get("auditType").toString())){
        	/*机构名单管理 OrgNameManagement002 删除 OrgNameManagement003 手工  OrgNameManagement004 批量*/
            str = mobileOrgNameManagementService.getWorkFlowInfo(make.getBusinessData());
        }else if("MST02".equals(param.get("auditType").toString())){//债券估值行情信息 MstBondQuotationInfo002
            str = mobileMstBondQuotationInfoService.getWorkFlowInfo(make.getBusinessData());
        }else if("MST04".equals(param.get("auditType").toString())){// MstNetAssetQuotationInfo002
        	str = mobileMstNetAssetQuotationInfoService.getWorkFlowInfo(make.getBusinessData());
        }else if("PTL05".equals(param.get("auditType").toString())){//银证转账 PtlFundAllocation002
        	str = mobilePtlFundAllocationService.getWorkFlowInfo(make.getBusinessData());
        }else if("PTL09".equals(param.get("auditType").toString())){//DVP资金调拨 PtlDvpFundAllocation002
        	str = mobilePtlDvpFundAllocationService.getWorkFlowInfo(make.getBusinessData());
        }else if("PTL10".equals(param.get("auditType").toString())){//通道资金划拨管理 PtlChlFundAllocation002
        	str = mobilePtlChlFundAllocationService.getWorkFlowInfo(make.getBusinessData());
        }else if("FINASSET01".equals(param.get("auditType").toString())){//债券标的 FinBondTargetAsset002
        	str = mobileFinBondTargetAssetService.getWorkFlowInfo(make.getBusinessData());
        }else if("FINASSET02".equals(param.get("auditType").toString())){//存款类标的 FinDepositTarget002
        	str = mobileFinDepositTargetService.getWorkFlowInfo(make.getBusinessData());
        }else if("FINASSET06".equals(param.get("auditType").toString())){//项目类标的 FinProjectTargetAsset002
        	str = mobileFinProjectTargetAssetService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR18".equals(param.get("auditType").toString())){//债券一级指令 EtrBondFirstLBidding002
        	str = mobileEtrBondFirstLBiddingService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR02".equals(param.get("auditType").toString())){//债券二级指令 EtrInvestBondInfo001
        	str = mobileEtrInvestBondInfoService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR08".equals(param.get("auditType").toString())){//质押式回购指令 EtrRepo002
        	str = mobileEtrRepoService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR04".equals(param.get("auditType").toString())){//存款类指令  EtrBrowDepoInfo002
        	str = mobileEtrBrowDepoInfoService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR03".equals(param.get("auditType").toString())){//拆借指令  EtrLendSpecInfo002
        	str = mobileEtrLendSpecInfoService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR05".equals(param.get("auditType").toString())){//公募基金指令  EtrFund002
        	str = mobileEtrFundService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR06".equals(param.get("auditType").toString())){//计划类净值型指令  EtrPlanAssetN002
        	str = mobileEtrPlanAssetNService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR07".equals(param.get("auditType").toString())){//计划类利率型指令  EtrPlanAssetR002
        	str = mobileEtrPlanAssetRService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR10".equals(param.get("auditType").toString())){//计划类货币型指令  EtrPlanAssetC002
        	str = mobileEtrPlanAssetCService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR09".equals(param.get("auditType").toString())){//项目类资产指令  EtrProject002
        	str = mobileEtrProjectService.getWorkFlowInfo(make.getBusinessData());
        }else if("ETR11".equals(param.get("auditType").toString())){//买断式回购指令 EtrBoutRepo002
        	str = mobileEtrBoutRepoService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD02".equals(param.get("auditType").toString())){//二级债券交易 TrdBondInvestTrade002
        	str = mobileTrdBondInvestTradeService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD08".equals(param.get("auditType").toString())){//质押式回购交易 TrdRepoTrade002
        	str = mobileTrdRepoTradeService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD04".equals(param.get("auditType").toString())){//存款类交易  private List<UploadFile> fileArray;
        	str = mobileTrdDepositInvestTradeService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD03".equals(param.get("auditType").toString())){//拆借投资交易 TrdLendInvestTrade002
        	str = mobileTrdLendInvestTradeService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD05".equals(param.get("auditType").toString())){//公募基金交易 TrdFundInvestTrade002
        	str = mobileTrdFundInvestTradeService.getWorkFlowInfo(make.getBusinessData());
        }else if("TRD06".equals(param.get("auditType").toString())){//计划类净值型交易 TrdPlanNetInvestTrade002
        	str = mobileTrdPlanNetInvestTradeService.getWorkFlowInfo(make.getBusinessData());
        }
        return str;
    }

    @DTXTransaction
    @Transactional(rollbackFor = {Exception.class})
    public ReturnTodoDto getNextWorkFlowInfo(String workFlowId) throws Exception {
        //查询下一节点信息
        Map<String, Object> variables = null;
        Map<String, Object> param = new HashMap<>();
        WorkFlowInfoDto workFlowInfoDto = this.getCurrentInfo(workFlowId,param);
        String nextTaskId=null;
        ReturnTodoDto returnTodoDto = new ReturnTodoDto();
        if(workFlowInfoDto!=null) {
	        ProcessService processService = WorkflowContext.getProcessService();
	        FlowEntity flowEntity = processService.getRuntimeEntity(workFlowInfoDto.getProcessInstanceId());
	        String processKey = flowEntity.getProcessKey();
//	        if (WorkflowContext.getWorkFlowService().isConditionModule(processKey) && ActivitiType.排他网关.getValue().equals(processService.getNextActType(workFlowInfoDto.getProcessInstanceId()))) {
//	            variables = WorkflowContext.getBusinessVariables(flowEntity, workFlowInfoDto.getCurTaskId());
//	        }
	        nextTaskId = processService.getNextTaskDefinitionId(workFlowInfoDto.getCurTaskId(), variables);
	        
	        //ajax请求下一核审信息
	        //AjaxResult result = restTemplate.postForObject(ServiceApplication.PINGAN_SERVICE_NAME + "/workflow/nextTask",param,AjaxResult.class);
	        Map<String, String> data = new HashMap<>();
	        data.put("nodeId", nextTaskId);
	        data.put("moduleId", processKey);
	        data.put("searchType", WorkflowContext.getWorkFlowService().getSelectType(nextTaskId, processKey));
	        data.put("wflId", workFlowId);
            data.put("institutionCode", MultilevelDataAuthorityUtil.getOrganizationCode(SecurityUtil.getLoginUser().getUserName()));


	        //查询下一核审人
	        List<WflNextReviewerDto> candidateUser = new ArrayList<>();
	        if (nextTaskId != null) {
	            candidateUser = mobileApprovalMapper.queryCandidateUser(data);
	        }
	        //剔除当前用户
	        for (int i = 0; i < candidateUser.size(); i++) {
	        	if(SecurityUtil.getLoginUser().getUserName()!=null && SecurityUtil.getLoginUser().getUserName().equals(candidateUser.get(i).getUserId())){
	                candidateUser.remove(i);
	            }
	        	if(workFlowInfoDto.getNowUserId()!=null && workFlowInfoDto.getNowUserId().equals(candidateUser.get(i).getUserId())) {
	        		candidateUser.remove(i);
	        	}
			}
	        returnTodoDto.setCandidateUser(candidateUser);
	        
	        //构造操作类型
	        List<String> operation = new ArrayList<>();
	        
	        if("NEW".equals(workFlowInfoDto.getProcessState())
	        		||"BACK".equals(workFlowInfoDto.getProcessState())) {
	        	operation.add("saveComplete");
	            operation.add("cancelWorkFlow");
	        }else {
	        	operation.add("saveComplete");
	            operation.add("passWorkFlow");
	            operation.add("returnPrevious");
	        }
	        returnTodoDto.setOperationType(operation);
        }
       
        //获取审批意见
        List<WflBusinessNodeDto> opinions=mobileApprovalMapper.getOpinions(workFlowId);
        //审批意见时间修改
        // 1查询sys_wfl_business_list中的workFlowId
        String syswflbusinesslist_workFlowId = mobileApprovalMapper.selectworkFlowId(workFlowId);
        // 2查询操作时间
        List<WflStatusDto> opinionsTime = mobileApprovalMapper.listByWorkflowId(syswflbusinesslist_workFlowId);
        // 3格式化opinionsTime时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String firstUser="";
        if(opinions.size()>0) {
            firstUser=opinions.get(0).getUserId();
            opinions.get(0).setOpinions("新建");
          //  opinions.remove(opinions.size()-1);
            for(int i=0;i<opinions.size();i++){
                opinions.get(i).setCreateDate(simpleDateFormat.format(opinionsTime.get(i).getOperTime()));
            }
        }
        
//        是否有下一审核人
        if(nextTaskId!=null) {
        	returnTodoDto.setIsLastStep("false");
        }else {
        	returnTodoDto.setIsLastStep("true");
        }
        returnTodoDto.setFirstUser(firstUser);
        returnTodoDto.setOpinions(opinions);
        return  returnTodoDto;
    }



 
    public String dealMobileApproval(Map<String, Object> param) throws Exception {
        ResultDto resultDto = new ResultDto();
        //获取移动端操作类型
        String operationType = param.get("operationType").toString();
        //获取移动端工作流Id
        String workFlowId = param.get("workFlowId").toString();
        Map<String, Object> parammap = new HashMap<>();
        WorkFlowInfoDto workFlowInfoDto = this.getCurrentInfo(workFlowId,parammap);
        String comment = param.containsKey("comment") ? param.get("comment").toString() : "";
        if (workFlowInfoDto != null) {
            if (operationType.equals("saveComplete")) {
                resultDto = this.dosubmit(workFlowInfoDto, param.get("nextAccount").toString(), comment,param.get("userId").toString());
            } else if (operationType.equals("passWorkFlow")) {
                resultDto = this.doAbandon(workFlowInfoDto);
            } else if (operationType.equals("returnPrevious")) {
                resultDto = this.doBack(workFlowInfoDto, comment,param.get("userId").toString());
            } else if(operationType.contentEquals("cancelWorkFlow")) {
            	resultDto = this.doCancel(workFlowInfoDto);
            }
        } else {
            resultDto.setMsg("未找到该任务，该任务已被处理");
            resultDto.setStatus("false");
            resultDto.setCode("002");
        }
        return JsonUtils.object2Json(resultDto);
    }

    //根据工作流代码获取当前流程信息
    private WorkFlowInfoDto getCurrentInfo(String workFlowId,Map<String, Object> param) throws Exception {
        WorkFlowInfoDto workFlowInfoDto = null;
        //根据工作流代码查询当前处理人信息
        String curUser = mobileApprovalMapper.queryCurUser(workFlowId);
//        Map<String, Object> param = new HashMap<>();
        //查询该工作流ID对应的节点信息
        if(curUser!=null) {
        List<WorkFlowInfoDto> curWorkFlowDto = this.mainService.getToDoListData(curUser, param);
	        for (WorkFlowInfoDto flowDto : curWorkFlowDto) {
	            if (flowDto.getWorkflowId().equals(workFlowId)) {
	                workFlowInfoDto = flowDto;
	            }
	        }
        }
        return workFlowInfoDto;
    }
    
  //获取我的工作数据
    private List<WorkFlowInfoDto> getMyWorkData(Map<String, Object> param) throws Exception {
    	param.put("SING_N", Whether.N);
    	param.put("SING_Y", Whether.Y);
    	List<WorkFlowInfoDto> works=mainMapper.selectMyWorkData(param);
    	if (CollectionUtils.isNotEmpty(works)) {
            final String createMsg = getMessage("SysCommon.create");
            	works.forEach(item -> {
                if(StringUtil.isNotEmpty(item.getOperator())) {
                    String[] names = item.getOperator().split(GlobalConstant.COMMA);
                    List<String> realNames = new ArrayList<>(names.length);
                    for(String name : names) {
                        realNames.add(CacheUtil.getAppParamCache(GlobalConstant.SYS_USER_KEY, name));
                    }
                    item.setOperator(StringUtil.joinString(realNames, GlobalConstant.COMMA));
                }
                if(StringUtil.equals(SysCodeUtil.getSysCode(), item.getSystemCode())) {
                    item.setPathPrefix(null);
                }
                if(StringUtil.equalsIgnoreCase(OperationType.NEW.name(), item.getActionType())) {
                    item.setActionType(createMsg);
                }
                else {
                    item.setActionType(this.getMessage("SysCommon." + item.getActionType().toLowerCase()));
                }
            });
            works.sort((o1, o2) -> DateUtil.compareDate(o2.getCrtDate(), o1.getCrtDate()));
        }
        return works;
    }

    //提交流程


    private ResultDto dosubmit(WorkFlowInfoDto workFlowInfoDto, String user, String comment,String loginUserId) throws Exception {
        ResultDto resultDto = new ResultDto();
        String taskId = workFlowInfoDto.getCurTaskId();
        String processInstanceId = workFlowInfoDto.getProcessInstanceId();
        String curUser = mobileApprovalMapper.queryCurUser(workFlowInfoDto.getWorkflowId());
        ProcessService processService = WorkflowContext.getProcessService();
        ActivityImpl firstNode = processService.getFirstTaskDefinition(processInstanceId);
        Task currentTask = processService.getActiveTaskId(processInstanceId, curUser);
        FlowEntity flowEntity = WorkflowContext.getProcessService().getRuntimeEntity(processInstanceId);
        Boolean isEndEvent = user == null ? true : false;
        BusinessService businessService = (BusinessService) SpringContextUtil.getBean(flowEntity.getBusinessService());
        
        boolean isApprovalDataChange=false;
//        Class<?> clz = WorkFlowUtil.getClassByBusinessServiceName(
//                flowEntity.getBusinessService());
//        ProcessMark newEntity = WorkFlowUtil.getBusinessObjectFromRequest(clz, false);
//            if (null != newEntity) {
//                isApprovalDataChange = true;
//        }
        
        String message = businessService.checkForSubmit(flowEntity, false);
        if (null != message) {
            resultDto.setMsg(message);
            resultDto.setStatus("false");
            resultDto.setCode("002");
        } else {
//            if (null != currentTask && StringUtil.equals(firstNode.getId(), currentTask.getTaskDefinitionKey()) && !OperationType.DELETE.equals(flowEntity.getOperType()) && (isEndEvent == null || !isEndEvent)) {
//                flowEntity.setProcessType(ProcessType.SUBMIT);
//                this.doUpdate(flowEntity, !isApprovalDataChange);
//            }
            WorkFlowUtil.submitProcess(user, "", comment, taskId, processInstanceId, loginUserId);
            resultDto.setMsg("提交流程已启动");
            resultDto.setStatus("success");
            resultDto.setCode("000");
        }
        return resultDto;
    }

    //放弃任务
    private ResultDto doAbandon(WorkFlowInfoDto workFlowInfoDto) {
        ResultDto resultDto = new ResultDto();
        if (!workFlowInfoDto.getCurTaskId().isEmpty()) {
            WorkflowContext.getProcessService().doUnClaim(SecurityUtil.getLoginUser().getUserName(), workFlowInfoDto.getCurTaskId());
            resultDto.setMsg("流程已被挂起");
            resultDto.setStatus("success");
            resultDto.setCode("000");
        } else {
            resultDto.setMsg("未找到该任务信息");
            resultDto.setStatus("false");
            resultDto.setCode("002");
        }
        return resultDto;
    }

    //回退任务
    private ResultDto doBack(WorkFlowInfoDto workFlowInfoDto, String comment,String operatorUserId) throws Exception {
        ResultDto resultDto = new ResultDto();
        if (!workFlowInfoDto.getProcessInstanceId().isEmpty()) {
            WorkflowContext.getProcessService().rollbackProcess(workFlowInfoDto.getProcessInstanceId(), operatorUserId, comment, "", "");
            resultDto.setMsg("流程已回退");
            resultDto.setStatus("success");
            resultDto.setCode("000");
        } else {
            resultDto.setMsg("未找到该实例");
            resultDto.setStatus("false");
            resultDto.setCode("002");
        }
        return resultDto;

    }
    
    //作废任务
    private ResultDto doCancel(WorkFlowInfoDto workFlowInfoDto) throws Exception {
        ResultDto resultDto = new ResultDto();
        if(!workFlowInfoDto.getWorkflowId().isEmpty()) {
        	WorkFlowUtil.deleteProcess(workFlowInfoDto.getWorkflowId(), workFlowInfoDto.getProcessInstanceId());
            resultDto.setMsg("流程已作废");
            resultDto.setStatus("success");
            resultDto.setCode("000");
        }else {
            resultDto.setMsg("未找到该任务信息");
            resultDto.setStatus("false");
            resultDto.setCode("002");
        }
        return resultDto;
    }

    private void doUpdate(FlowEntity flowEntity, boolean flag) throws Exception {
        boolean skip = false;
        WorkflowMethodInterceptor interceptor = WorkflowContext.getInterceptor();
        if (null != interceptor) {
            skip = !interceptor.beforeDoUpdate(flowEntity);
        }

        if (!skip) {
            // 获取业务service
            BusinessService<WorkflowEntity> businessService = WorkflowContext.getBusinessService(flowEntity.getBusinessService());
            // 获取业务对象，如果有更新则为更新后的对象
            ProcessMark businessEntity = getBusinessObjectForUpdate(flowEntity, businessService);
            try{
                Object a = JsonUtils.OBJECT_MAPPER.readValue(
                    businessEntity.getBusinessData(), GenericsUtils.getSuperClassGenricType(
                        ProxyUtil.getTargetClass(businessService.getClass())));
                WorkflowEntity entity = (WorkflowEntity)a;
                //未获取到工作流id
                entity.setWorkflowId(businessEntity.getWorkflowId());
                // 流程标题
                String title = WorkFlowUtil.generateTitle(flowEntity.getBusinessService(), entity, flowEntity.getOperType());
                if(null != title) {
                    // SYS_WFL_BUSINESS_LIST的TITLE更新
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("title", title);
                    params.put("workFlowId", flowEntity.getWorkflowId());
                    params.put("updateUser", flowEntity.getCurrentUser());
                    params.put("updateTime", DateUtil.now());
                    WorkflowContext.getWorkFlowService().updateTitle(params);
                }
//                if (!flag) {
                    ProcessMark processMark = WorkflowContext.getBusinessEntity(flowEntity);
                    Object oldObject = JsonUtils.OBJECT_MAPPER.readValue(processMark.getBusinessData(),
                        GenericsUtils.getSuperClassGenricType(
                            ProxyUtil.getTargetClass(businessService.getClass())));
                    WorkflowEntity entityOld = (WorkflowEntity)oldObject;
                    BeanUtil.copyPropertiesIgnoreNull(entityOld, entity);
                    BeanUtil.copyPropertiesIgnoreNull(processMark, businessEntity);

                    businessService.doUpdate(entityOld);
                    // 将合并后的dto更新到clob字段 doUpdate可能会对entity操作
                    processMark.setBusinessData(JsonUtils.object2Json(entityOld));

                    // 更新业务数据
                    WorkflowContext.getProcessMarkService().insertIntoProcessMark(processMark);
//                }
//                else {
//                    //操作留痕表
//                    WorkflowContext.getProcessMarkService().insertIntoProcessMark(businessEntity);
//                    businessService.doUpdate(entity);
//                }
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
            finally {
                if(null != interceptor) {
                    interceptor.afterDoUpdate(flowEntity);
                }
            }
        }

    }

    private ProcessMark getBusinessObjectForUpdate(FlowEntity flowEntity, BusinessService<WorkflowEntity> businessService) throws Exception {
        ProcessMark businessEntity = WorkflowContext.getBusinessEntity(flowEntity);
        this.checkBusinessObject(businessEntity, businessService.getClass());
        Class<?> clz = WorkFlowUtil.getClassByBusinessServiceName(flowEntity.getBusinessService());
        ProcessMark newEntity = this.getBusinessObjectFromRequest(clz, true,flowEntity);
        String workflowId = flowEntity.getWorkflowId();
        newEntity.setWorkflowId(workflowId);
        newEntity.setStatus(businessEntity.getStatus());
        return newEntity;
    }
    
    
    public ProcessMark getBusinessObjectFromRequest(Class<?> businessEntityClass, boolean extendProp,FlowEntity flowEntity)
            throws Exception {
//            HttpServletRequest request = RequestUtil.getCurrentRequest();
//            if (null != request) {
//                Map<String, Object> params = RequestUtil.getParameters(request);
                // 业务数据的JSON字符串
//                String businessInfo = StringUtil.defaultString((String)params.get("businessInfo"));
//                if (StringUtil.isNotEmpty(businessInfo) && !StringUtil.equals(businessInfo, "{}")
//                    && !StringUtil.equals(businessInfo, "{\"_sub\":true}")
//                    && !StringUtil.equals(businessInfo, "{\"endEvent\":true}")) {
                    // request参数转为业务对象
    	ProcessMark make =  WorkflowContext.getProcessMarkService().getBusinessInfo(flowEntity.getWorkflowId());
                    WorkflowEntity newEntity = (WorkflowEntity)JsonUtils.OBJECT_MAPPER.readValue(
                    make.getBusinessData(), businessEntityClass);
                    if(extendProp) {
                        BeanUtil.setProperty(newEntity, GlobalConstant.STATUS,
                        		make.getStatus());
                        BeanUtil.setProperty(newEntity, GlobalConstant.WORKFLOW_ID,
                        		flowEntity.getWorkflowId());
                    }
                    // 获取新的PK
                    ProcessMark resultEntity = WorkflowContext.getProcessMarkService().getParameterByAnnotation(
                        newEntity);
                    if(extendProp) {
                        // 数据状态
                        BeanUtil.setProperty(resultEntity, GlobalConstant.STATUS,
                        		make.getStatus());
                        BeanUtil.setProperty(resultEntity, GlobalConstant.WORKFLOW_ID,
                        		flowEntity.getWorkflowId());
//                        if (StringUtil.isNotEmpty(params.get("pageIdForLog"))) {
//                            resultEntity.setPageId(params.get("pageIdForLog").toString());
//                        }
                        // 创建人
//                        if (StringUtil.isNotEmpty(params.get("createUser"))) {
	                    resultEntity.setCreateUser(flowEntity.getCreateUser());
	                    BeanUtil.setProperty(newEntity, "createUser", resultEntity.getCreateUser());
//                        }
                        // 创建时间
                        if (StringUtil.isNotEmpty(flowEntity.getCreateTime())) {
                            resultEntity.setCreateTime(flowEntity.getCreateTime());
                            BeanUtil.setProperty(newEntity, "createTime", resultEntity.getCreateTime());
                        }
                        // 修改时间
                        resultEntity.setUpdateTime(DateUtil.now());
                        BeanUtil.setProperty(newEntity, "updateTime", resultEntity.getUpdateTime());
                        // 修改人
                        resultEntity.setUpdateUser(flowEntity.getCreateUser());
                        BeanUtil.setProperty(newEntity, "updateUser", resultEntity.getUpdateUser());
                    }
                    resultEntity.setBusinessData(JsonUtils.object2Json(newEntity));
                    return resultEntity;
//                }
//            }
        }

    private void checkBusinessObject(ProcessMark businessEntity, Class<?> businessService) throws WorkFlowException {
        if (null == businessEntity) {
            throw new WorkFlowException(WflCode.WFL_E00022, WorkflowContext.getMessage(WflCode.WFL_E00013.name(), new Object[]{businessService}));
        } else {
            try {
                String status = BeanUtil.getProperty(businessEntity, "status");
                if (StringUtils.equals(DictCodeList.数据状态_修改.getValue(), status) || StringUtils.equals(DictCodeList.数据状态_新建.getValue(), status) || StringUtils.equals(DictCodeList.数据状态_删除.getValue(), status)) {
                    throw new WorkFlowException(WflCode.WFL_E00023, WorkflowContext.getMessage(WflCode.WFL_E00013.name(), new Object[]{businessService}));
                }
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var4) {
                LOGGER.error(var4.getMessage());
                throw new WorkFlowException(WflCode.WFL_E99999, WorkflowContext.getMessage(WflCode.WFL_E99999.name()));
            }
        }
    }


    public Object getFile(Map<String, Object> param) throws IOException {
        List<File> fileList = new ArrayList<>();
        SysUploadFileInfo fileInfo = mobileApprovalMapper.queryFileInfo(param.get("fileNo").toString());
        if (fileInfo != null) {
            File file = new File(fileInfo.getPath());
            //下载附件
            if (!file.exists()) {
//                throw new BusinessException("文件[" + fileInfo.getFileName() + "]不存在！");
//                return MobileUtils.Objetc2Json("",null,"文件不存在");
            } else {
            	if(!"pdf".equals(fileInfo.getFileName().substring(fileInfo.getFileName().indexOf(".")+1, fileInfo.getFileName().length()).toLowerCase())) {
            		file=Office2PDFUtils.officeToPdf(fileInfo.getPath());
            	}
                InputStream in = new FileInputStream(file);
                return ExportUtil.export(fileInfo.getFileName().substring(0,fileInfo.getFileName().indexOf("."))+".pdf", in);
            }
        }
        return MobileUtils.Objetc2Json("",null,"文件不存在");
    }
    
    public Object getFileList(Map<String, Object> param) throws IOException {
        List<File> fileList = new ArrayList<>();
        SysUploadFileInfo fileInfo = mobileApprovalMapper.queryFileInfo(param.get("fileNo").toString());
        if (fileInfo != null) {
            File file = new File(fileInfo.getPath());
            //下载附件
            if (!file.exists()) {
//                throw new BusinessException("文件[" + fileInfo.getFileName() + "]不存在！");
//                return MobileUtils.Objetc2Json("",null,"文件不存在");
            } else {
                InputStream in = new FileInputStream(file);
                return ExportUtil.export(fileInfo.getFileName(), in);
            }
        }
        return MobileUtils.Objetc2Json("",null,"文件不存在");
    }
    
    public Object getFileUrl(Map<String, Object> param) throws IOException {
        SysUploadFileInfo fileInfo = mobileApprovalMapper.queryFileInfo(param.get("fileNo").toString());
        if (fileInfo != null) {
            File file = new File(fileInfo.getPath());
            //下载附件
            if (!file.exists()) {
                throw new BusinessException("文件[" + fileInfo.getFileName() + "]不存在！");
            } else {
                InputStream in = new FileInputStream(file);
                return ExportUtil.export(fileInfo.getFileName(), in);
            }
        }
        return "文件名传入错误，未找到该文件";
    }

    //自动登陆
    public String autoLogin(HttpServletRequest request) {
        String msg = "";
        Map<String, Object> param = RequestUtil.getParameters(request);
        if (param.containsKey("userId")) {
            User userInfo = mobileApprovalMapper.findUserInfo(param.get("userId").toString());
            if (userInfo == null) {
                msg = "用户名传入错误，资管系统中不存在该用户";
                return msg;
            }

            //进行sso认证，通过移动端的用户名进行登陆注册
//            SecurityUtil.addUserToSession(userInfo);
//            AuthenticationToken token = new SSOUsernameToken(userInfo.getUserName(), userInfo.getPassword(), false, request.getRemoteHost());
//            Session session = SecurityUtils.getSubject().getSession(true);
//            SubjectContext context = new DefaultSubjectContext();
//            context.setAuthenticated(true);
//            context.setSessionId(session.getId());
//            context.setHost(session.getHost());
//            context.setSession(session);
//            Subject subject = SecurityUtils.getSecurityManager().createSubject(context);
//            Subject subject = SecurityUtils.getSubject();
//            subject.login(token);

            msg = "用户已成功注册";
            return msg;
        } else {
            msg = "请输入查询用户名";
            return msg;
        }
    }

    /**
     * <b>方法：</b>login<br>
     * <b>描述：</b>用户登录<br>
     * <b>作者：</b>zhuruqing<br>
     * <b>时间：</b>2020/5/1 15:12<br>
     * <b>参数：</b>[request]<br>
     * <b>返回值：</b>java.lang.String<br>
     */
    public String login(HttpServletRequest request) {

        String msg = "";
        String code = "";
        Map map = new HashMap();
        Map<String, Object> param = RequestUtil.getParameters(request);
        String phone = param.get("phone")+"";
        if (param.containsKey("userId")) {
            User userInfo = mobileApprovalMapper.findUserInfo((String) param.get("userId"));
            if (userInfo == null) {
                msg = "用户名传入错误，资管系统中不存在该用户";
                code = "001";
            } else {
                String passwordDefault = new SimpleHash(PasswordHelper.ALGORITHM_NAME, (String)param.get("password"),
                        userInfo.getSalt(), PasswordHelper.HASH_ITERATIONS).toHex();
                if (!passwordDefault.equals(userInfo.getPassword())) {
                    msg = "密码输入错误，请重新输入！";
                    code = "002";
                }else{
                    map.put("orgType","");
                    map.put("orgName","");
                    map.put("showReport","");
                    map.put("phone",phone);
                    map.put("userName",userInfo.getRealName());
                    map.put("userId",userInfo.getUserName());
                    
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                    map.put("token", uuid);
                    mobileApprovalMapper.delToken(userInfo.getUserName());
                    mobileApprovalMapper.createToken(userInfo.getUserName(), uuid);
                    msg = "登录成功！";
                    code = "000";       
                  map.put("ossUserId", request.getSession().getId());
                  request.getSession().getId();
                }
            }

        } else {
            msg = "请输入查询用户名";
            code = "003";
        }
        return MobileUtils.Objetc2Json(code,map,msg);
    }
    
    /**
     * <b>方法：</b>authentication<br>
     * <b>描述：</b>认证<br>
     * <b>作者：</b>YH<br>
     * <b>时间：</b>2020/5/1 15:12<br>
     * <b>参数：</b>[request]<br>
     * <b>返回值：</b>java.lang.String<br>
     */
    public String authentication(HttpServletRequest request) {

        String msg = "";
        String code = "";
        Map map = new HashMap();
        Map<String, Object> param = RequestUtil.getParameters(request);
        String phone = param.get("phone")+"";
        if (param.containsKey("userId")) {
            User userInfo = mobileApprovalMapper.findUserInfo((String) param.get("userId"));
            if (userInfo == null) {
                msg = "用户名不存在";
                code = "001";
            } else {  
                map.put("orgType","");
                map.put("orgName","");
                map.put("showReport","");
                map.put("phone",phone);
                map.put("userName",userInfo.getRealName());
                map.put("userId",userInfo.getUserName());
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                map.put("token", uuid);
                mobileApprovalMapper.delToken(userInfo.getUserName());
                mobileApprovalMapper.createToken(userInfo.getUserName(), uuid);
                msg = "认证成功！";
                code = "000";     
                map.put("ossUserId", request.getSession().getId());
                request.getSession().getId();
            }
        } else {
            msg = "请输入查询用户名";
            code = "003";
        }
        return MobileUtils.Objetc2Json(code,map,msg);
    }
    
    public List<TmAppAuditTabConfitDto> getAuditDetailConfig() {
    	return mobileApprovalMapper.getAuditDetailTabConfig();
    }
    
    public List<TmAppAuditTabDetailDto> getAuditDetailTabDetail(String tabConfigId) {
    	return mobileApprovalMapper.getAuditDetailTabDetail(tabConfigId);
    }
    
    public int createToken(String userId,String token) {
    	return mobileApprovalMapper.createToken(userId,token);
    }
    
    public int delToken(String userId) {
    	return mobileApprovalMapper.delToken(userId);
    }
    
    public String getToken(String userId) {
    	return mobileApprovalMapper.getToken(userId);
    }
}
