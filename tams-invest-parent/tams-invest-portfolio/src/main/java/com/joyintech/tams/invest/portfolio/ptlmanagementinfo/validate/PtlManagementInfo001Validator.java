/*
 * 文件名：PtlManagementInfo001Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.portfolio.ptlmanagementinfo.validate;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.portfolio.service.SecuritiesAccountInfoService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.dto.PtlManagementInfo004Dto;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.portfolio.dto.FundAccountInfoDto;
import com.joyintech.tams.component.business.portfolio.dto.SecuritiesAccountInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.dto.PtlManagementInfoDto;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.service.PtlManagementInfo001Service;

/**
 * 组合信息验证
 * @author fanshuanghu
 * @version 1.0 2018年9月6日
 * @see PtlManagementInfo001Validator
 * @since 1.0
 */
@Transactional
@Component
public class PtlManagementInfo001Validator implements BeanValidator{
    /**
     * 组合service001
     */
    @Autowired
    private PtlManagementInfo001Service ptlManagementInfo001Service;

    @Autowired
    private SecuritiesAccountInfoService securitiesAccountInfoService;

	@Autowired
	private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired
    MstWorkflowCommonService mstWorkflowCommonService;

    @Override
    public List<ValidMessage> valid(Object value) {
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
        PtlManagementInfoDto dto = (PtlManagementInfoDto)value;
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
        	if(StringUtil.isNotEmpty(dto.getPortfolioId()) && dto.getPortfolioId().getBytes().length > 25){
        		errorList.add(new ValidMessage(getMessage("FW_E_0007", getMessage("PtlManagementInfo001.portfolioId"),"25")));
        	}
            //组合开始日必须小于组合结束日
            if(dto.getVdate() != null && dto.getMdate() != null && dto.getVdate().compareTo(dto.getMdate())>0){
                errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0003",
                    getMessage("PtlManagementInfo001.vDate")+DateUtil.format(dto.getVdate()),
                    getMessage("PtlManagementInfo001.mDate")+DateUtil.format(dto.getMdate()))));
            }
            //资金账户不能为空
//            if(dto.getFundAccountInfoDto() == null || dto.getFundAccountInfoDto().isEmpty()){
//                errorList.add(new ValidMessage("fundAccountInfoDto",getMessage("FW_E_0006",getMessage("PtlManagementInfo001.funInfo"))));
//            }
            //资金账户进行验证
            funDtoValid(dto,errorList);
            //证券账户验证
            secDtoValid(dto,errorList);
            //校验组合代码重复性
            if(StringUtil.isNotEmpty(dto.getPortfolioId())){
                if(ptlPortfolioMapper.selectByPrimaryKey(dto.getPortfolioId()) != null){
                    errorList.add(new ValidMessage("portfolioId",getMessage("PTL_COM_E_0009")));
                }
            }
            //验证是否分配用户
            if (null == dto.getUserId()){
                errorList.add(new ValidMessage(getMessage("PTL_COM_E_0011")));
            }else {
                // 银行模式  会默认选中组合审批人 也提示报错。
                if( dto.getUserId().size()==1){
                    // 如果仅有一条 并且为 组合审批人的话 依然提示需要分配用户
                    String institutionCode = null;
                    if(MultilevelDataAuthorityUtil.isEnabled()){
                        institutionCode = MultilevelDataAuthorityUtil.getOrganizationCode(dto.getCreateUser());
                    }
                    if(StringUtil.equals(dto.getUserId().get(0) , CacheUtil.getSpecialSysParam(BussinessConstant.PORTFOLIO_HANDLE_USERID,institutionCode))){
                        errorList.add(new ValidMessage(getMessage("PTL_COM_E_0040")));
                    }
                }
            }

        }else if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)){
            //组合开始日必须大于组合结束日（修改界面）
            if (StringUtil.isNotEmpty(dto.getMdate())){
                if(dto.getVdate() != null &&dto.getVdate().compareTo(dto.getMdate())>0){
                    errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0003",
                            getMessage("PtlManagementInfo001.vDate")+DateUtil.format(dto.getVdate()),
                            getMessage("PtlManagementInfo001.mDate")+DateUtil.format(dto.getMdate()))));
                }
            }
            //金融产品信息不能重复（修改界面）
//            if((StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit")) &&
//                (dto.getFundAccountInfoDto() == null || dto.getFundAccountInfoDto().isEmpty())){
//                errorList.add(new ValidMessage("fundAccountInfoDto",getMessage("FW_E_0006",getMessage("PtlManagementInfo001.funInfo"))));
//            }
            funDtoValid(dto,errorList);
            secDtoValid(dto,errorList);

            // TISHKBS-999  TISHKBS-986 编辑时  也校验
            //验证是否分配用户
            if (StringUtil.isEmptyList(dto.getUserId())){
                errorList.add(new ValidMessage(getMessage("PTL_COM_E_0011")));
            }else {
                // 银行模式  会默认选中组合审批人 也提示报错。
                if( dto.getUserId().size()==1){
                    // 如果仅有一条 并且为 组合审批人的话 依然提示需要分配用户
                    String institutionCode = null;
                    if(MultilevelDataAuthorityUtil.isEnabled()){
                        institutionCode = MultilevelDataAuthorityUtil.getOrganizationCode(dto.getCreateUser());
                    }
                    if(StringUtil.equals(dto.getUserId().get(0) , CacheUtil.getSpecialSysParam(BussinessConstant.PORTFOLIO_HANDLE_USERID,institutionCode))){
                        errorList.add(new ValidMessage(getMessage("PTL_COM_E_0040")));
                    }
                }
            }

        }
       else if(StringUtil.equals(dto.getOperateType(), "delete")){
           if(dto.getSelectedItem() != null ){
                boolean flg = ptlManagementInfo001Service.deleteCheck(dto);
                    if(!flg){
                        errorList.add(new ValidMessage("此组合存在交易不可删除"));
                    }
        		else{
        			funDtoValid(dto,errorList);
                    secDtoValid(dto,errorList);
                    addValid(dto,errorList);
        		}
           }
       }
    if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)||StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            //add by 马数凯 20210202 新增校验，组合新建页面资金账户不能是通道所使过的资金账户 start
            Map<String, Object> param = new HashMap<String, Object>();
            List<String> acctList = new ArrayList<String>();
            //资金账户账号
            for(FundAccountInfoDto fundDto : dto.getFundAccountInfoDto()){
                if(StringUtil.isNotEmpty(fundDto.getAcctNo())) {
                    acctList.add(fundDto.getAcctNo());
                }
            }
            //证券账户账号
            for(SecuritiesAccountInfoDto secDto : dto.getSecuritiesAccountInfoDto()){
                if(StringUtil.isNotEmpty(secDto.getTrdAcctId())) {
                    acctList.add(secDto.getTrdAcctId());
                }
            }
            if (!StringUtil.isEmptyList(acctList)) {
                param.put("busiAcctNoList", acctList);
                param.put("F28", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
                if(ptlManagementInfo001Service.getReAcct(param) > 0){
                    errorList.add(new ValidMessage("资金账户已被合作资产使用,请重新选择资金账户"));
                }
            }
            //add by 马数凯 20210202 新增校验，组合新建页面资金账户不能是通道所使过的资金账户 end
    }
      return errorList;
    }

    public void funDtoValid(PtlManagementInfoDto dto,ArrayList<ValidMessage> errorList){
        if(dto.getFundAccountInfoDto() == null || dto.getFundAccountInfoDto().isEmpty()){
            if(dto.getOperateType() != null && (StringUtil.equals(dto.getOperateType(),"save") ||
                    StringUtil.equals(dto.getOperateType(),"submit"))){
                // modify by jiangpeng on 20210422 start TISHKBS-1688 存在产品创建时未申请托管户的情况
//                errorList.add(new ValidMessage("fundAccountInfoDto",getMessage("FW_E_0006",getMessage("PtlManagementInfo001.funInfo"))));
                // modify by jiangpeng on 20210422 end
            }
        }else{
            boolean hasSecManageAcct = false;
            Iterator<FundAccountInfoDto> it = dto.getFundAccountInfoDto().iterator();
            Set<String> set = new HashSet<>();
            Set<String> acctTypeSet = new HashSet<>();
            while(it.hasNext()){
                FundAccountInfoDto nextDto = it.next();
                acctTypeSet.add(nextDto.getAcctType()+nextDto.getCcy());
                if(StringUtil.isNotEmpty(nextDto.getAcctType()) && StringUtil.equals(nextDto.getAcctType(), DictCodeListExt1.资金账户类型_产品托管户.getValue())){
                    hasSecManageAcct = true;
                }
                String nextId = nextDto.getAcctNo();
                set.add(nextId);
            }
            //判断资金账户类型是否重复 因为产品端新建可以添加多个券商资金账户，所以校验放开 yjm 20190926
            /*if(acctTypeSet.size() < dto.getFundAccountInfoDto().size()){
                errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0006",getMessage("FundAccountInfo.acctType"))));
            }*/
            //账户信息不能重复
            //update by leipeng 现状：产品要素界面挂资金账户，不同的账户类型挂了同一个资金账户，组合审批时，提示资金账户重复                                    期望：如果不同的账户类型挂了同一个资金账户，在建产品要素的时候提示资金账户重复，不要在组合审批的时候提示
            //针对募集户和兑付户，可能是同一个资金账户，放开限制
            if(set.size() < dto.getFundAccountInfoDto().size()){
                //针对募集户和兑付户，可能是同一个资金账户，这时,dto.getFundAccountInfoDto().size()数量需要减1
                int size=dto.getFundAccountInfoDto().size();
                List<String> list = new ArrayList<>();
                for(FundAccountInfoDto item:dto.getFundAccountInfoDto()){
                    if(StringUtil.isNotEmpty(item.getAcctType()) && (item.getAcctType().equals("B14") || item.getAcctType().equals("B15")) && StringUtil.isNotEmpty(item.getAcctNo())){
                        if(list.contains(item.getAcctNo())){
                            size=size-1;
                        }else{
                            list.add(item.getAcctNo());
                        }
                    }
                }
                if(set.size() <size){
                    //errorList.add(new ValidMessage("fundAccountInfoDto",getMessage("BUSS_COM_E_0006",getMessage("PtlManagementInfo001.funInfo"),getMessage("FundAccountInfo.acctNo"))));
                    errorList.add(new ValidMessage("资金账户信息中除募集户和兑付户资金账户账号之间允许重复外，其他资金账户账号数据不能重复。"));
                }
            }
            // modify by jiangpeng on 20210422 start TISHKBS-1688 存在产品创建时未申请托管户的情况,故取消组合创建时校验托管户
            //必须存在托管账户
//            if(!hasSecManageAcct){
//                errorList.add(new ValidMessage(getMessage("PtlManagementInfo001.selectAcct")));
//            }
            // modify by jiangpeng on 20210422 end
        }
    }

    public void secDtoValid(PtlManagementInfoDto dto,ArrayList<ValidMessage> errorList){
        if(dto.getFundAccountInfoDto() == null || dto.getFundAccountInfoDto().isEmpty()){
        }else{
            Iterator<SecuritiesAccountInfoDto> it = dto.getSecuritiesAccountInfoDto().iterator();
            Set<String> set = new HashSet<>();
            while(it.hasNext()){
                String nextId = it.next().getSecAcctId();
                set.add(nextId);
            }
            //证券账户不能重复
            if(set.size() < dto.getSecuritiesAccountInfoDto().size()){
                errorList.add(new ValidMessage("securitiesAccountInfoDto",getMessage("BUSS_COM_E_0006",getMessage("PtlManagementInfo001.secInfo"))));
            }

            //银行间or商业银行柜台同一登记托管机构只能最多绑定一个证券账户 add by chenkang 20220104 start
            List<SecuritiesAccountInfoDto> secLists = Lists.newArrayList();
            List<SecuritiesAccountInfoDto> secTwoLists = Lists.newArrayList();
            dto.getSecuritiesAccountInfoDto().stream().forEach(x -> {
                if (StringUtil.isNotEmpty(x.getSecAcctId())) {
                    SecuritiesAccountInfoDto secDto = securitiesAccountInfoService.getSecAcctInfo(x.getSecAcctId());
                    if (secDto != null && (DictCodeListExt1.交易场所_银行间.getValue().equals(secDto.getTradePlace()))) {
                        secLists.add(secDto);
                    }
                    if (secDto != null && DictCodeListExt1.交易场所_商业银行柜台.getValue().equals(secDto.getTradePlace())) {
                        secTwoLists.add(secDto);
                    }
                }
            });
            List<String> stringList = secLists.stream().map(SecuritiesAccountInfoDto::getRegistOrg)
                    .distinct().collect(Collectors.toList());
            List<String> stringTwoList = secTwoLists.stream().map(SecuritiesAccountInfoDto::getRegistOrg)
                    .distinct().collect(Collectors.toList());
            if (stringList.size() != secLists.size()) {
                errorList.add(new ValidMessage("银行间or商业银行柜台同一登记托管机构只能最多绑定一个证券账户！"));
            }
            if (stringTwoList.size() != stringTwoList.size()) {
                errorList.add(new ValidMessage("银行间or商业银行柜台同一登记托管机构只能最多绑定一个证券账户！"));
            }
            //银行间or商业银行柜台同一登记托管机构只能最多绑定一个证券账户 add by chenkang 20220104 end
        }
    }

    /**
     * 验证删除产品组合时是否有子组合，以及是否有新建流程
     * @param dto
     * @param errorList
     */
    public void addValid(PtlManagementInfoDto dto,ArrayList<ValidMessage> errorList){
        List<PtlManagementInfoDto> selectedItem = dto.getSelectedItem();
        for (PtlManagementInfoDto ptlManagementInfoDto : selectedItem) {
            if (StringUtil.equals(ptlManagementInfoDto.getPortfolioType(),DictCodeListExt1.组合类型_投资类组合.getValue())) {
                List<PtlManagementInfo004Dto> childPortfolios = ptlManagementInfo001Service.getChildPortfolios(ptlManagementInfoDto.getPortfolioId());
                if (!childPortfolios.isEmpty()){
                    errorList.add(new ValidMessage(getMessage("PTL_COM_E_0021")));
                }

                //判断流程中是否存在相同的数据
                WflValidInputParameter param = new WflValidInputParameter();
                param.setBusinessDataPk1(ptlManagementInfoDto.getPortfolioId());
                param.setPageId("PtlManagementInfo004");
                if (null != mstWorkflowCommonService &&
                        mstWorkflowCommonService.isInWflProcess(param)) {
                    errorList.add(new ValidMessage(this.getMessage("PTL_COM_E_0022")));
                }
            }else if (StringUtil.equals(ptlManagementInfoDto.getPortfolioType(),DictCodeListExt1.组合类型_子组合.getValue())){
                //判断流程中是否存在相同的数据
                WflValidInputParameter param = new WflValidInputParameter();
                param.setBusinessDataPk1(ptlManagementInfoDto.getPortfolioId());
                param.setPageId("PtlManagementInfo004");

                if (null != mstWorkflowCommonService &&
                        mstWorkflowCommonService.isInWflProcess(param)) {
                    errorList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0041")));
                }
            }

        }
    }
}
