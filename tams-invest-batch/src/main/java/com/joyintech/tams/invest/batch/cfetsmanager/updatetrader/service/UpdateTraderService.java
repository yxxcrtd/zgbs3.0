package com.joyintech.tams.invest.batch.cfetsmanager.updatetrader.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.entity.MstCfetsCustomerInfo;
import com.joyintech.tams.common.base.entity.MstCounterParty;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.cfetsmanager.cfetstrader.service.CfetsTraderService;
import com.joyintech.tams.invest.batch.cfetsmanager.updatetrader.dao.UpdateTraderMapper;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;

/**
 * 系统交易对手表更新
 * @author Administrator
 *
 */
@Service	
@Transactional(rollbackFor = Exception.class)
@Scope("prototype")
public class UpdateTraderService {
	/**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(CfetsTraderService.class);
    
    @Autowired
    private UpdateTraderMapper updateTraderMapper;
    
    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;
    /**
     * 注入业务主键生产类
     */
    @Autowired(required = false)
    private BaseGetBussinessId baseGetBusinessId;
    /**
     * 系统交易对手表更新
     * @param batchDate  批处理执行时间
     * @throws Exception
     * @see
     * @since 1.0
     */
    public void updateTrader(Date batchDate) throws Exception {
    	logger.info("CfetsTraderService ---执行的操作选择的时间为：" + batchDate);
    	String date = DateUtil.format(batchDate, DateUtil.DEFAULT_OTHER_FORMAT);
    	//查询在交易对手中不存在的银行间机构
    	BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_交易对手管理);
    	List<MstCfetsCustomerInfo> mstCfetsCustomerList = updateTraderMapper.selectCustomerNew(date);
        List<MstCfetsCustomerInfo> mstCfetsCustomerInfos = Lists.newArrayList();

    	//过滤已经匹配的21位机构码
        List<String> partyIdList = updateTraderMapper.getAllPartyIdIsNotNull().stream()
                .filter(string -> !StringUtil.isBlank(string)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(partyIdList)) {
            mstCfetsCustomerInfos = mstCfetsCustomerList.stream()
                    .filter(s -> !partyIdList.contains(s.getPartyId())).collect(Collectors.toList());
        }else {
            mstCfetsCustomerInfos.addAll(mstCfetsCustomerList);
        }

    	if(!StringUtil.isEmptyList(mstCfetsCustomerInfos)) {
    		for(MstCfetsCustomerInfo mstCfetsCustomerInfo:mstCfetsCustomerInfos) {
    			/*update by chenjiaxing 银行间交易对手信息录入之后需要生成对应的交易对手信息，如果是非法人机构默认为资管产品；
    			如果是法人机构需要匹配机构信息是否已存在，不存在建立为市场机构交易对手，存在的话，则不再建立 20210730 start*/
/*    			MstCounterParty mstCounterParty = new MstCounterParty();
    			mstCounterParty.setCounterId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(
                    bussinessCodeParameter) : IDGenerator.generate());
    			mstCounterParty.setCounterType(DictCodeListExt1.交易对手类型_非法人机构.getValue());
    			mstCounterParty.setCounterName(mstCfetsCustomerInfo.getPartyName());
    			mstCounterPartyMapper.insertSelective(mstCounterParty);*/

                //按照交易成员全称匹配交易对手信息
                Map<String, Object> map = new HashMap<>();
                map.put("partyCustomerName",mstCfetsCustomerInfo.getPartyName());
                String counterId = updateTraderMapper.checkPartyNameOfParty(map);
                if (StringUtil.isNotEmpty(counterId)) {
                    MstCounterParty mstCounterParty = new MstCounterParty();
                    mstCounterParty.setCounterId(counterId);
                    mstCounterParty.setPartyId(mstCfetsCustomerInfo.getPartyId());

                    mstCounterPartyMapper.updateByPrimaryKeySelective(mstCounterParty);
                }else {
                    /*判断当前当前机构客户类型 >0 非法人*/
                    /*判别法人非法人逻辑调整，法人：MST_CFETS_CUSTOMER_INFO表中为主机构的机构
                     * 处理规则：与系统内的市场机构进行精确名称匹配：匹配上：生成市场机构时 数据生成的link_id为当前市场机构的市场机构代码，匹配不上link_id置为空
                     * 非法人：MST_CFETS_CUSTOMER_INFO表中为从机构的机构，管理人为主机构对应的交易对手数据的link_Id*/

                    map.put("partyId", mstCfetsCustomerInfo.getPartyId());
                    int temp01 = updateTraderMapper.selectCustomer01(map);
                    int temp02 = updateTraderMapper.selectCustomer02(map);
//                    String mpartyId = updateTraderMapper.selectMpartyId(map);
                    //法人（主机构）
                    if(temp01 > 0){
                        //法人相关处理
                        MstCounterParty mstCounterParty = new MstCounterParty();
                        mstCounterParty.setCounterId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(
                                bussinessCodeParameter) : IDGenerator.generate());
                        mstCounterParty.setCounterName(mstCfetsCustomerInfo.getPartyName());
                        mstCounterParty.setCounterType(DictCodeListExt1.交易对手类型_资管产品.getValue());
                        mstCounterParty.setPartyId(mstCfetsCustomerInfo.getPartyId());
                        //按名称进行匹配
                        map.put("partyCustomerName",mstCfetsCustomerInfo.getPartyName());
                        String linkId = updateTraderMapper.checkCustomerName(map);
                        if(StringUtil.isNotEmpty(linkId)){
                            mstCounterParty.setLinkId(linkId);
                            mstCounterPartyMapper.insertSelective(mstCounterParty);
                        }

                    }
                    //非法人（从机构）
                    if(temp02 > 0) {
                        //非法人相关处理
                        MstCounterParty mstCounterParty = new MstCounterParty();
                        mstCounterParty.setCounterName(mstCfetsCustomerInfo.getPartyName());
                        mstCounterParty.setCounterType(DictCodeListExt1.交易对手类型_资管产品.getValue());
                        mstCounterParty.setPartyId(mstCfetsCustomerInfo.getPartyId());

                        //获取主机构ID
                        map.put("partyId", mstCfetsCustomerInfo.getPartyId());
                        // 存在一个从机构对应多个主机构
                        List<String> mpartyIds = updateTraderMapper.selectMpartyId(map);
                        for (String mpartyId : mpartyIds) {
                            mstCounterParty.setCounterId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(
                                    bussinessCodeParameter) : IDGenerator.generate());
                            //获取主机构名称
                            //MstCfetsCustomerInfo temp = updateTraderMapper.selectInfo(mstCfetsCustomerInfo.getPartyId());
                            MstCfetsCustomerInfo temp = new MstCfetsCustomerInfo();
                            if (StringUtil.isNotEmpty(mpartyId)) {
                                temp = updateTraderMapper.selectMpartyInfo(mpartyId);
                            }
                            String linkId = "";
                            if (temp != null) {
                                //按名称进行匹配
                                map.put("partyCustomerName", temp.getPartyName());
                                //获取主机构交易对手的link_Id
                                linkId = updateTraderMapper.checkCustomerName(map);
                            }
                            if (StringUtil.isNotEmpty(linkId)) {
                                mstCounterParty.setManagerId(linkId);
                            }
                            mstCounterPartyMapper.insertSelective(mstCounterParty);
                        }
                    }
    			/*update by chenjiaxing 银行间交易对手信息录入之后需要生成对应的交易对手信息，如果是非法人机构默认为资管产品；
    			如果是法人机构需要匹配机构信息是否已存在，不存在建立为市场机构交易对手，存在的话，则不再建立 20210730 end*/
                }

    		}
    	}	
    	
    }
}
