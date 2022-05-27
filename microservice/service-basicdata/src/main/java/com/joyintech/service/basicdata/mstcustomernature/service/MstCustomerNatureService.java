/*
 * 文件名：MstCustomerNatureService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：huwei 修改时间：2018年8月15日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.basicdata.mstcustomernature.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.mstcustomernature.dao.MstCustomerNatureMapper;
import com.joyintech.service.basicdata.mstcustomernature.dto.MstCustomerNatureDto;
import com.joyintech.tams.common.base.dao.MstCustomerInfoAddMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoMapper;
import com.joyintech.tams.common.base.entity.MstCustomerInfo;
import com.joyintech.tams.common.base.entity.MstCustomerInfoAddWithBLOBs;
import com.joyintech.service.basicdata.msttradeopponent.dto.MstTradeOpponentDto;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.constant.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客服性质管理业务类
 *
 * @author huwei
 * @version 1.0 2018年8月15日
 * @see MstCustomerNatureService
 * @since 1.0
 */
@Service("backMstCustomerNatureService")
@Transactional
public class MstCustomerNatureService extends BaseService<MstCustomerNatureDto> {

    /**
     * 客服性质管理Mapper
     */
    @Autowired
    private MstCustomerNatureMapper mstCustomerNatureMapper;

    // 调用投管接口 add by renping 20200409 start
	@Autowired
	private MstCounterPartyMapper mstCounterPartyMapper;
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    /**
     * 客户信息表
     */
    @Autowired
    private MstCustomerInfoMapper mstCustomerInfoMapper;
    
    /**
     * 客户信息附加表
     */
    @Autowired
    private MstCustomerInfoAddMapper mstCustomerInfoAddMapper;
    // 调用投管接口 add by renping 20200409 end
    /**
     * 实现docomplement方法
     */
    public void doComplete(MstCustomerNatureDto entity, String type) {
        if (StringUtil.equals(type, "MODIFY")) {
            // List<MstCustomerNatureDto> list = entity.getSelectedItem();
            // MstCustomerNatureDto mdto = new MstCustomerNatureDto();
            // mdto.setCustomerId(entity.getCustomerId());
            // mstCustomerNatureMapper.deleteCustomerCharaterByPrimaryKey(mdto);
            // if (list.size() > 0) {
            // for (MstCustomerNatureDto dto : list) {
            // if (StringUtil.equals(DictCodeList.客户性质_发行人.getCodeName(),
            // dto.getCustomerCharater())) {
            // mdto.setIsIssuer("Y");
            // }
            // if (StringUtil.equals(DictCodeList.客户性质_管理人.getCodeName(),
            // dto.getCustomerCharater())) {
            // mdto.setIsAssteManager("Y");
            // }
            // if (StringUtil.equals(DictCodeList.客户性质_托管人.getCodeName(),
            // dto.getCustomerCharater())) {
            // mdto.setIsTruster("Y");
            // }
            // if (StringUtil.equals(DictCodeList.客户性质_融资人.getCodeName(),
            // dto.getCustomerCharater())) {
            // mdto.setIsFinancier("Y");
            // }
            // if (StringUtil.equals(DictCodeList.客户性质_担保人.getCodeName(),
            // dto.getCustomerCharater())) {
            // mdto.setIsGuarantee("Y");
            // }
            // }
            // mstCustomerNatureMapper.updateCustomerCharaterByPrimaryKey(mdto);
            // }
        	//先更新交易对手，再更新客户性质
        	updateCounter(entity);
            mstCustomerNatureMapper.updateByPrimaryKey(entity);
            // 调用投管接口 add by renping 20200409 start
            setInterf(entity.getCustomerId());
            // 调用投管接口 add by renping 20200409 end

        }
        else {
            // List<MstCustomerNatureDto> list = entity.getSelectedItem();
            // for (MstCustomerNatureDto dto : list) {
            Map<String, List<MstCustomerNatureDto>> dtoMap = entity.getSelectedItem().stream().collect(
                JoyinCollectors.groupingBy(x -> x.getCustomerId()));
            for (String key : dtoMap.keySet()) {
                MstCustomerNatureDto dto = new MstCustomerNatureDto();
                for (MstCustomerNatureDto temp : dtoMap.get(key)) {
                    if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_发行人.getValue())) {
                        dto.setIsIssuer("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_管理人.getValue())) {
                        dto.setIsAssteManager("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_托管人.getValue())) {
                        dto.setIsTruster("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_融资人.getValue())) {
                        dto.setIsFinancier("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_担保人.getValue())) {
                        dto.setIsGuarantee("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_承销商.getValue())) {
                        dto.setIsSaler("Y");;
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_存放行.getValue())) {
                        dto.setIsDepositBank("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_投资顾问.getValue())) {
                        dto.setIsInvestAdviser("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeList.客户性质_出质人.getValue())) {
                        dto.setIsPledgor("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeListExt3.客户性质_券商.getValue())) {
                        dto.setIsBroker("Y");
                    }
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeListExt3.客户性质_交易对手.getValue())) {
						dto.setIsCounter("Y");
						/****** 生成交易对手 ****/
						createCounter(temp);
						/*******************/
					}
                    else if (StringUtil.equals(temp.getCustomerCharater(), DictCodeListExt3.客户性质_原始权益人.getValue())) {
                        dto.setIsOriginalOwner("Y");
                    }
                }
                dto.setCustomerId(key);
                mstCustomerNatureMapper.updateByPrimaryKey(dto);
                // 调用投管接口 add by renping 20200409 start
                setInterf(key);
                // 调用投管接口 add by renping 20200409 end
            }
            // }
        }

    }
    /**
	 * 创建机构交易对手记录
	 */
	public void createCounter(MstCustomerNatureDto dto) {
        int count = mstCustomerNatureMapper.checkLinkId(dto.getCustomerId());
	    if (count == 0){
            MstTradeOpponentDto mstTradeOpponentDto = new MstTradeOpponentDto();
            mstTradeOpponentDto.setCounterId(dto.getCounterId());
            mstTradeOpponentDto.setCounterName(dto.getCustomerName());
            mstTradeOpponentDto.setLinkId(dto.getCustomerId());
            mstTradeOpponentDto.setCounterType(dto.getCounterType());
            mstTradeOpponentDto.setOperateType(dto.getOperateType());
            mstCounterPartyMapper.insertSelective(mstTradeOpponentDto);
            // 调用投管接口 add by chaowei
            mstCounterInterf(mstTradeOpponentDto);
            // 调用投管接口 add by chaowei
        }
	}
    // 调用投管接口 add by renping 20200409 start
	/**
	 * 更新机构交易对手记录 
	 */
	public void updateCounter(MstCustomerNatureDto dto) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerId", dto.getCustomerId());
		MstCustomerNatureDto entity = mstCustomerNatureMapper.selectByPrimaryKey(map);
		 //原来是交易对手,现在不是交易对手,删除交易对手记录；
		if ("Y".equals(entity.getIsCounter()) && dto.getIsCounter() == null) {
			mstCounterPartyMapper.deleteByPrimaryKey(dto.getCounterId());
            // 调用投管接口 add by daizhiwei
            MstTradeOpponentDto mstTradeOpponentDto = new MstTradeOpponentDto();
            mstTradeOpponentDto.setCounterId(dto.getCounterId());
            mstTradeOpponentDto.setCounterName(dto.getCustomerName());
            mstTradeOpponentDto.setLinkId(dto.getCustomerId());
            mstTradeOpponentDto.setCounterType(dto.getCounterType());
            mstTradeOpponentDto.setOperateType(OperationType.DELETE.toString());
            mstCounterInterf(mstTradeOpponentDto);
            // 调用投管接口 add by daizhiwei
		}//原来不是交易对手,现在是交易对手,新增交易对手记录；
		else if (entity.getIsCounter() == null && "Y".equals(dto.getIsCounter())) {
            dto.setOperateType(OperationType.NEW.toString());
			createCounter(dto);
		}
	}
    /**
     * 投管接口（客户性质通过市场机构接口发送）
     * @param customerId
     * @see
     * @since 1.0
     */
    private void setInterf(String customerId) {
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("MstCustomerNature002");
        MstCustomerInfo mstCustomerInfo = this.mstCustomerInfoMapper.selectByPrimaryKey(customerId);
        investInterfCommDto.setMstCustomerInfo(mstCustomerInfo);
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }
    /**
     * 调用投管接口--交易对手
     * @param entity 界面上信息
     */
    private void mstCounterInterf(MstTradeOpponentDto entity){
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("MstTradeOpponent002");
        investInterfCommDto.setMstCounterParty(entity);
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }
    /**
     * 客户名称的联想文本框的查询方法
     *
     * @param param map
     * @return List
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getCustomerName(Map<String, Object> param) {
        buileQueryMap(param);
        List<SuggestDto> resultDto = mstCustomerNatureMapper.getCustomerName(param);
        return resultDto;
    }

    /**
     * 客户的联想文本框的查询方法
     *
     * @param param map
     * @return List
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getCustomerId(Map<String, Object> param) {
        List<SuggestDto> resultDto = mstCustomerNatureMapper.getCustomerId(param);
        return resultDto;
    }

    /**
     * 客户名称的联想文本框的查询方法
     *
     * @param param map
     * @return List
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getName(Map<String, Object> param) {
        List<SuggestDto> resultDto = mstCustomerNatureMapper.getName(param);
        return resultDto;
    }

    /**
     * 客服性质配置查询按钮的查询方法 <一句话功能简述> <功能详细描述>
     * 
     * @param length 长度
     * @param page 画面页数
     * @param pageParam map
     * @return Page
     * @throws Exception 异常
     * @see
     * @since 1.0
     */

    public Page<MstCustomerNatureDto> searchMstCustomerNature(int page, int length, Map<String, Object> pageParam)
    {
        return super.queryForPage(page, length, () -> mstCustomerNatureMapper.searchMstCustomerNature(pageParam));
    }

    /**
     * 获取数据字典的方法
     *
     * @param param map
     * @see
     * @since 1.0
     */
    private void buileQueryMap(Map<String, Object> param) {
        param.put("CUSTOMER_TYPE_01", DictCodeList.客户类型_法人.getValue());
        param.put("CUSTOMER_TYPE_02", DictCodeList.客户类型_自然人.getValue());
        param.put("CUSTOMER_CHARATER_01", DictCodeList.客户性质_发行人.getValue());
        param.put("CUSTOMER_CHARATER_02", DictCodeList.客户性质_融资人.getValue());
        param.put("CUSTOMER_CHARATER_03", DictCodeList.客户性质_管理人.getValue());
        param.put("CUSTOMER_CHARATER_04", DictCodeList.客户性质_托管人.getValue());
        param.put("CUSTOMER_CHARATER_05", DictCodeList.客户性质_担保人.getValue());
        param.put("CUSTOMER_CHARATER_06", DictCodeList.客户性质_出质人.getValue());
        param.put("CUSTOMER_CHARATER_07", DictCodeList.客户性质_承销商.getValue());
        param.put("CUSTOMER_CHARATER_08", DictCodeList.客户性质_存放行.getValue());
        param.put("CUSTOMER_CHARATER_09", DictCodeList.客户性质_投资顾问.getValue());
        param.put("CUSTOMER_CHARATER_10", DictCodeListExt3.客户性质_券商.getValue());
        param.put("CUSTOMER_CHARATER_12", DictCodeListExt3.客户性质_交易对手.getValue());
        param.put("CUSTOMER_CHARATER_13", DictCodeListExt3.客户性质_原始权益人.getValue());

    }

    /**
     * 客服性质管理查询按钮的查询方法
     *
     * @param pageParam map
     * @return Page
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<MstCustomerNatureDto> selectCustomerInfo(int page, int length, Map<String, Object> pageParam)
    {
        buileQueryMap(pageParam);
        pageParam.put("flag_01", DictCodeList.客户性质_发行人.getCodeName());
        pageParam.put("flag_02", DictCodeList.客户性质_融资人.getCodeName());
        pageParam.put("flag_03", DictCodeList.客户性质_管理人.getCodeName());
        pageParam.put("flag_04", DictCodeList.客户性质_托管人.getCodeName());
        pageParam.put("flag_05", DictCodeList.客户性质_担保人.getCodeName());
        pageParam.put("flag_06", DictCodeList.客户性质_出质人.getCodeName());
        pageParam.put("flag_07", DictCodeList.客户性质_承销商.getCodeName());
        pageParam.put("flag_08", DictCodeList.客户性质_存放行.getCodeName());
        pageParam.put("flag_09", DictCodeList.客户性质_投资顾问.getCodeName());
        pageParam.put("flag_10", DictCodeListExt3.客户性质_券商.getCodeName());
        pageParam.put("flag_12", DictCodeListExt3.客户性质_交易对手.getCodeName());
        pageParam.put("flag_13", DictCodeListExt3.客户性质_原始权益人.getCodeName());

        Page<MstCustomerNatureDto> dto = super.queryForPage(page, length,
            () -> mstCustomerNatureMapper.selectCustomerInfo(pageParam));
        List<MstCustomerNatureDto> ldto = dto.getRoot();
        // 将后台传来的值转换成数据字典值
        for (MstCustomerNatureDto mdto : ldto) {
            List<String> slist = new ArrayList<String>();
            List<String> list = StringUtil.splitToList(GlobalConstant.COMMA, mdto.getCustomerCharater());
            for (int i = 0; i < list.size(); i++ ) {
                if (StringUtil.isNotEmpty(list.get(i))) {
                    slist.add(list.get(i));
                }
            }
            String customerCharater = StringUtil.joinString(slist, GlobalConstant.COMMA);
            mdto.setCustomerCharater(customerCharater);
        }
        return dto;
    }

    /**
     * 通过主键的查询方法
     *
     * @param param map
     * @return Page
     * @see
     * @since 1.0
     */
    public MstCustomerNatureDto selectByPrimaryKey(Map<String, Object> param) {
        return mstCustomerNatureMapper.selectByPrimaryKey(param);
    }

    public String getIssuer(String linkId1, String type) {
        // 入库
        String customerId = mstCustomerNatureMapper.getIssuer(linkId1);
        // 更新属性
        if (StringUtil.isNotEmpty(customerId)) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("customerId", customerId);
            MstCustomerNatureDto dto = mstCustomerNatureMapper.selectByPrimaryKey(param);
            if (StringUtil.isNotEmpty(dto)) {
                if (StringUtil.equals("IS_ISSUER", type)) {
                    // IS_ISSUER 发行人
                    dto.setIsIssuer(DictCodeList.是否_是.getValue());
                }
                else if (StringUtil.equals("IS_TRUSTER", type)) {
                    // IS_TRUSTER 托管人
                    dto.setIsTruster(DictCodeList.是否_是.getValue());
                }
                else if (StringUtil.equals("IS_ASSTE_MANAGER", type)) {
                    // IS_ASSTE_MANAGER 管理人
                    dto.setIsAssteManager(DictCodeList.是否_是.getValue());
                }
                mstCustomerNatureMapper.updateByPrimaryKey(dto);
            }
        }
        return customerId;
    }

    /**
     * check是否持有金融牌照
     *
     * @param  customerId
     * @return
     * @see
     * @since 1.0
     */
    public String checkIsFinLicenceStr(String customerId) {

        return mstCustomerNatureMapper.checkIsFinLicenceStr(customerId);
    }

    /**
     * check同一个客户是否已有在流程中的客户性质
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    public int checkwflstatus(Map<String, Object> param) {
        return mstCustomerNatureMapper.checkwflstatus(param);
    }

    /**
     * 统计
     * <功能详细描述>
     * @param counterId id
     * @return 统计结果
     * @see
     * @since 1.0
     */
    public int selectCounter(String linkId) {
        return mstCustomerNatureMapper.selectCounter(linkId);
    }
}
