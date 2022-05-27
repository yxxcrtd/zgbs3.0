package com.joyintech.service.basicdata.mstmarketmanagement.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.basicdata.mstmarketinstitutions.dto.MstMarketInstitutionsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.mstmarketmanagement.dto.MstMarketManageDto;
import com.joyintech.service.basicdata.mstmarketmanagement.service.MstMarketManagementService;

@RestController("MstMarketManagementCtr_dtx")
@RequestMapping("/MstMarketManagement")
public class MstMarketManagementController {
	@Autowired
         private MstMarketManagementService  mstMarketManagementService;
         
         /**
          * 画面查询方法
          * @param map map
          * @return Page<MstMarketManagementDto> 分页实体类结果
          * @throws Exception 错误
          * @see
          * @since 1.0
          */
         @PostMapping("/selectForPage/{page}/{length}")
         public Page<MstMarketManageDto> selectPage(@RequestBody Map<String, Object> pageParam,@PathVariable("page") int page, @PathVariable("length") int length)
         {
             Page<MstMarketManageDto> info=new Page<MstMarketManageDto>(); 
             Page<MstMarketManageDto> pageinfo=new Page<MstMarketManageDto>();
             info = mstMarketManagementService.selectForPage(page,length,pageParam);
             List<MstMarketManageDto> res=info.getRoot();
             Map<String, Object> map=new HashMap<String, Object>();
             for(MstMarketManageDto dto:res){
                 String gradeDateStr = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.addDay(DateUtil.parse(dto.getGradeDate()), -1));
                 map.put("gradeDateStr", gradeDateStr);
                 map.put("oCode", dto.getoCode());
                 // START modify by wj 20200417 查询前次评级时 加这2参数 会导致查不到。
                 //map.put("gradeOrgId", dto.getGradeOrgId());
                 //map.put("shortLongTerm", dto.getShortLongTerm());
                 map.put("oType", dto.getoType());
                 if(StringUtil.isNotEmpty(dto.getNoticeDate())){
                     dto.setNoticeDate(new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.parse(dto.getNoticeDate())));
                 }
                 pageinfo = mstMarketManagementService.selectForPage(page,length,map);
                 if(!StringUtil.isEmptyList(pageinfo.getRoot())){
                     dto.setPreviousgradeDate(pageinfo.getRoot().get(0).getGradeDate());
                     dto.setPreviousgradeResultDis(pageinfo.getRoot().get(0).getGradeResult());
                     dto.setPreviousgradeOrgName(pageinfo.getRoot().get(0).getGradeOrgName());
                 }
             }
             info.setRoot(res);
             return info;
         }
         /**
        * 联想文本框service层实现
          * 
          * @param param 检索参数
          * @return SuggestDto
        */
        @PostMapping("/selectFinprodName")
        public List<SuggestDto> selectForCode(@RequestBody Map<String, Object> param) {
            return mstMarketManagementService.selectFinprodName(param);
        }

    /**
     * 客户性质设置为“发行人、融资人、管理人、担保人、出质人、存放行、原始权益人”的全部市场机构名称
     *
     * @param param 过滤参数
     * @return 下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("getCodeName/{page}/{length}")
    public Page<SelectOptionDto> getFinancier(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page,
                                              @PathVariable("length") int length) {
        return mstMarketManagementService.getCodeName(page, length, param);
    }

    /**
     *
     * 工作流完成
     * <功能详细描述>
     * @param entity
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public void doComplete(@RequestBody MstMarketManageDto entity){
        mstMarketManagementService.doComplete(entity);
    }
    /**
     * 查询评级明细信息
     *
     * @param entity 检索参数
     * @return MstMarketManageDto
     */
    @PostMapping("/selectDetail")
    public MstMarketManageDto selectDetail(@RequestBody MstMarketManageDto entity) {
        return mstMarketManagementService.selectDetail(entity);
    }
}
