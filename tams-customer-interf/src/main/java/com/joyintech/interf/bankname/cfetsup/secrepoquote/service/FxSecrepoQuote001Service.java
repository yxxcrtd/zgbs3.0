package com.joyintech.interf.bankname.cfetsup.secrepoquote.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.interf.bankname.cfetsup.outbound.CallReturn;
import com.joyintech.interf.bankname.cfetsup.outbound.service.ClientService;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dao.FxSecrepoQuoteDetailMapper;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dao.FxSecrepoQuoteMapper;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dto.FxSecrepoQuote001Dto;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dto.FxSecrepoQuote002Dto;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.entity.FxSecrepoQuote;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 回购对话报价业务层
 *
 * @author zcf
 * @version 1.0
 * @date 2020-01-08
 */
@Service
public class FxSecrepoQuote001Service extends BaseService<FxSecrepoQuote001Dto> implements BaseProcessService<FxSecrepoQuote001Dto> {

    @Autowired
    private FxSecrepoQuoteMapper fxSecrepoQuoteMapper;
    @Autowired
    private FxSecrepoQuoteDetailMapper fxSecrepoQuoteDetailMapper;
    @Autowired
    private ClientService clientService;

    @Override
    public void doComplete(FxSecrepoQuote001Dto fxSecrepoQuote001Dto, OperationType operationType) {
        fxSecrepoQuoteMapper.deleteByPrimaryKey(fxSecrepoQuote001Dto.getSeqno());
        fxSecrepoQuoteDetailMapper.deleteByPseqNo(fxSecrepoQuote001Dto.getPseqno());
    }

    /**
     * 分页查询
     * @param para
     * @return
     */
    public Page<FxSecrepoQuote001Dto> selectPageData(Map<String, Object> para) {
        para.put("ACCOUNTSPEED", DictCode.清算速度.getValue());
        return this.queryForPage(Integer.valueOf(para.get("page").toString()),Integer.valueOf(para.get("length").toString()),
                ()->fxSecrepoQuoteMapper.selectPageData(para));
    }

    /**
     * 查询组合代码
     * @param para
     * @return
     */
    public Page<SelectOptionDto> getPortfolioId(Map para){
        return new Page(fxSecrepoQuoteMapper.getPortfolioId(para));
    }

    /**
     * 查询组合代码
     * @param para
     * @return
     */
    public Page<SelectOptionDto> getPortfolioName(Map para){
        return new Page(fxSecrepoQuoteMapper.getPortfolioName(para));
    }

    /**
     * 查询交易员信息
     * @param para
     * @return
     */
    public Page<SelectOptionDto> getMenInfo(Map para){
        return new Page(fxSecrepoQuoteMapper.getMenInfo(para));
    }

    /**
     * 查询质押式回购表字段信息
     * @param para
     * @return
     */
    public List<SuggestDto> getSecrepoQuoteInfo(Map para){
        List<SuggestDto> list = fxSecrepoQuoteMapper.getSecrepoQuoteInfo(para);
        return list;
    }
    /**
     * 再次发送方法
     * @param dto
     * @return
     */
    public String  resend(FxSecrepoQuote001Dto dto){
        FxSecrepoQuote entity = new FxSecrepoQuote();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        String businessType = entity.getBusinessType();
        // Sonar Fix modify by huwei 220607
        if(StringUtils.isEmpty(businessType)) {
            return "未获取到接口业务类型！";
        }
        CallReturn callReturn= clientService.send(entity,businessType,businessType);
        boolean success = callReturn.isSuccess();
        if (success){
            return "发送成功！";
        }else{
            return callReturn.getMessage();
        }
    }
}
