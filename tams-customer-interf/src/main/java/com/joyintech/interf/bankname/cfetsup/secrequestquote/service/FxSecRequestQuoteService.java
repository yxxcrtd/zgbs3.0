package com.joyintech.interf.bankname.cfetsup.secrequestquote.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.interf.bankname.cfetsup.constant.FxDictCode;
import com.joyintech.interf.bankname.cfetsup.constant.FxDictCodeList;
import com.joyintech.interf.bankname.cfetsup.imixbasicinfo.imixaccountcfets.dto.FxImixAccountInfo001Dto;
import com.joyintech.interf.bankname.cfetsup.imixbasicinfo.imixtraderassociation.dto.FxTraderManagerDto;
import com.joyintech.interf.bankname.cfetsup.outbound.CallReturn;
import com.joyintech.interf.bankname.cfetsup.outbound.service.ClientService;
import com.joyintech.interf.bankname.cfetsup.secrequestquote.dao.FxSecRequestQuoteMapper;
import com.joyintech.interf.bankname.cfetsup.secrequestquote.dto.FxSecRequestQuoteCounterDto;
import com.joyintech.interf.bankname.cfetsup.secrequestquote.dto.FxSecRequestQuoteDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(value="fxSecRequestQuoteService")
public class FxSecRequestQuoteService
        extends BaseService<FxSecRequestQuoteDto> implements BaseProcessService<FxSecRequestQuoteDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(FxSecRequestQuoteService.class);

    @Autowired
    private FxSecRequestQuoteMapper fxSecRequestQuoteMapper;

    @Autowired
    private ClientService clientService;

    public Page<FxSecRequestQuoteDto> findPage(Map<String, Object> pageParam) {
        return this.queryForPage(Integer.valueOf(pageParam.get("page").toString()), Integer.valueOf(pageParam.get("length").toString()),
                () -> fxSecRequestQuoteMapper.findPage(pageParam));
    }


    public FxSecRequestQuoteDto findRequestQuoteDtoBySeqno(String seqno) {
        return fxSecRequestQuoteMapper.findRequestQuoteDtoBySeqno(seqno);
    }


    public List<FxSecRequestQuoteCounterDto> findCounterDtoByPseqno(String pseqno) {
        return fxSecRequestQuoteMapper.findCounterDtoByPseqno(pseqno);
    }

    public List<SelectOptionDto> getProductid(Map para){
        return fxSecRequestQuoteMapper.getProductid(para);
    }

    public List<SelectOptionDto> getSeccode(Map para){
        return fxSecRequestQuoteMapper.getSeccode(para);
    }

    /**
     * 获取系统日期
     * @return
     */
    // Sonar Fix modify by huwei 220607
    /*public String getSysDate() {
        // 加载平台日期
        Date dt = null;
              //  fxSecRequestQuoteMapper.getSysDate();
        if (dt != null) {
            return DateUtil.format(dt, DateUtil.DEFAULT_DATE_FORMAT_EN);
        }
        return StringUtil.EMPTY;
    }*/

    public AjaxResult save(FxSecRequestQuoteDto dto){
        ProcessParameter<FxSecRequestQuoteDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<FxSecRequestQuoteDto> params = new WorkFlowParameter<>();
        params.setPageId("FxSecRequestQuote002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getSeqno());
        params.setBusinessType(FxDictCodeList.共通业务类型_现券买卖请求报价.getItemCode());
        params.setComment(entity -> "现券买卖请求报价，报价编号" + "(" + entity.getNbquoteid()
                + ")-交易类型 ：" + FxDictCodeList.from(FxDictCode.交易方向, entity.getPs()).getItemValue());
        processParameter.setWorkFlowParameter(params);
        String msg="";
        try {
            msg =this.start(processParameter,dto);
        } catch (WorkFlowException e) {
            msg="操作失败";
            logger.error(e.getMessage(),e);
        }
        return new AjaxResult(msg);
    }


    public FxSecRequestQuoteDto setQuoteInfoFromTrust(String trustid) {
        return fxSecRequestQuoteMapper.setQuoteInfoFromTrust(trustid);
    }




    public List<SelectOptionDto> getDataDict(Map<String, Object> map) {
        return fxSecRequestQuoteMapper.getDataDict(map);
    }

    @Override
    public void doComplete(FxSecRequestQuoteDto fxSecRequestQuoteDto, OperationType operationType) {
            //更新流程状态
        fxSecRequestQuoteDto.setEffectflag("E");
        fxSecRequestQuoteDto.setLastmodifierid(LoginUserUtil.getUserName());
        fxSecRequestQuoteDto.setProcstatus("已生效");
        fxSecRequestQuoteDto.setSendstatus("sending");
        this.updateProcstatus(fxSecRequestQuoteDto);
        //发送接口
        if("add".equals(fxSecRequestQuoteDto.getBusinessType())){
           CallReturn callReturn= clientService.send(fxSecRequestQuoteDto,"add","addQuote");
//           System.out.println(callReturn);
           logger.info(callReturn.toString());
        }else if("confirm".equals(fxSecRequestQuoteDto.getBusinessType())){//确认成交
            CallReturn callReturn=clientService.send(fxSecRequestQuoteDto,"confirm","confirmQuote");
//            System.out.println(callReturn);
            logger.info(callReturn.toString());
        }
    }


    @Override
    public void doInsert(FxSecRequestQuoteDto dto, OperationType type) {
        if(dto.getOperateType().equals("confirm")){
            //删除原数据
            fxSecRequestQuoteMapper.deleteQuoteBySeqno(dto.getPseqno());
            fxSecRequestQuoteMapper.deleteQuoteCounterByPseqno(dto.getPseqno());
            dto.setBusinessType("confirm");
            dto.setTradestatus("dealing");
        }else if(dto.getOperateType().equals("add")){
            dto.setBusinessType("add");
            dto.setTradestatus("adding");
        }
        dto.setCreatorid(LoginUserUtil.getUserName());
        dto.setEffectflag("A");
        dto.setProcstatus("新建");
        //交易员 登录人关联
        //本方机构代码 产品关联
        //报价日期
        dto.setQuotedate(dto.getDealdate());
        //保存报价信息数据
        fxSecRequestQuoteMapper.saveQuote(dto);
        //存入报价历史留痕
        fxSecRequestQuoteMapper.insertQuoteHist(dto);
        List<FxSecRequestQuoteCounterDto> counterDtoList=new ArrayList<>();
        if(dto!=null&&dto.getCounterList()!=null&&dto.getCounterList().size()!=0){
            for (int i=0;i<dto.getCounterList().size();i++){
                FxSecRequestQuoteCounterDto conDto=dto.getCounterList().get(i);
                conDto.setSeqno(IDGenerator.generate());
                conDto.setPseqno(dto.getSeqno());
                dto.setEffectflag(dto.getEffectflag());
                conDto.setCreatorid(LoginUserUtil.getUserName());
                counterDtoList.add(conDto);
            }
            //保存交易对手信息
            fxSecRequestQuoteMapper.saveCounterList(counterDtoList);
            //保存对手报价历史留痕
            fxSecRequestQuoteMapper.insertQuoteCounterHist(counterDtoList);
        }
    }


    public String findSequence(){
        return fxSecRequestQuoteMapper.findSequence();
    }

    /**
     * 获取外汇交易中心交易员id
     * @param userName
     * @return
     */
    public FxTraderManagerDto findHomeTrader(String userName) {

        return fxSecRequestQuoteMapper.findHomeTrader(userName);
    }

    /**
     * 获取本币本方机构代码和机构名称
     * @param accountid
     * @return
     */
    public FxImixAccountInfo001Dto findHomeCstp(String accountid) {
        return fxSecRequestQuoteMapper.findHomeCstp(accountid);
    }

    /**
     * 更新交易状态
     * @param dto
     */
    public void updateStatus(FxSecRequestQuoteDto dto) {
        fxSecRequestQuoteMapper.updateStatus(dto);
    }

    /**
     * 更新流程状态
     * @param dto
     */
    public void updateProcstatus(FxSecRequestQuoteDto dto) {
        fxSecRequestQuoteMapper.updateProcstatus(dto);
    }
}
