/*
 * 文件名：TrdCommandDispatcher001Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：wangkang
 * 修改时间：2018年9月12日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.investtrade.commanddispatcher.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.trade.investtrade.commanddispatcher.dao.TrdCommandDispatcher001Mapper;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrCommandDispatcherTotalDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.PaEtrAutoDispenseTimeDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.TrdCommandDispatcher001Dto;
import com.joyintech.tams.common.base.dao.EtrAutoDispenseRuleDetailMapper;
import com.joyintech.tams.common.base.dao.EtrAutoDispenseRuleMapper;
import com.joyintech.tams.common.base.dao.EtrDistributeMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.PaEtrAutoDispenseTimeMapper;
import com.joyintech.tams.common.base.entity.EtrAutoDispenseRule;
import com.joyintech.tams.common.base.entity.EtrAutoDispenseRuleDetail;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrDistributeKey;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.PaEtrAutoDispenseTime;
import com.joyintech.tams.common.base.entity.User;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.constant.ServiceApplication;

/**
 * 微服务端交易查询service 〈功能详细描述〉
 * 
 * @author wangkang
 * @version 1.0 2018年9月12日
 * @see TrdCommandDispatcher001Service
 * @since 1.0
 */
@Service("backTrdCommandDispatcher001Service")
@Transactional(rollbackFor = Exception.class)
public class TrdCommandDispatcher001Service extends BaseService<TrdCommandDispatcher001Dto> {

    /**
     * 指令分发mapper
     */
    @Autowired
    private TrdCommandDispatcher001Mapper trdCommandDispatcher001Mapper;

    /**
     * 自动分发保存的mapper
     */
    @Autowired
    private PaEtrAutoDispenseTimeMapper paEtrAutoDispenseTimeMapper;

    /**
     * 委托指令分发信息保存的mapper
     */
    @Autowired
    private EtrDistributeMapper etrDistributeMapper;

    /**
     * 指令推送信息查询service
     */
    @Autowired
    private EntrCommandDispatcherService entrCommandDispatcherService;
    /**
     * 投资委托委托组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoServiceImpl etrInvesInfo;
    @Autowired
    private EtrAutoDispenseRuleMapper etrAutoDispenseRuleMapper;
    @Autowired
    private EtrAutoDispenseRuleDetailMapper etrAutoDispenseRuleDetailMapper;
    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    
    /**
     * 查询 <功能详细描述>
     * 
     * @param pageParam 查询信息
     * @return 查询结果
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    public Page<TrdCommandDispatcher001Dto> selectForPage(int page, int length, Map<String, Object> pageParam)
    {
//    	setParams(pageParam);
        pageParam.put("finType_zq", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        //add by gaopeng 20191126 增加交易方向 start
        pageParam.put("ps_49", DictCodeListExt3.交易方向_债转股.getValue());
        //add by gaopeng 20191126 增加交易方向 end
        pageParam.put("ENTRUST_CLASS_02", DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());
        Page<TrdCommandDispatcher001Dto> pageLimit = super.queryForPage(page, length,
            () -> trdCommandDispatcher001Mapper.selectForPage(pageParam));
        // 将金融产品类型数据字典转换为平安资产类型字典
    /*    List<TrdCommandDispatcher001Dto> list = pageLimit.getRoot();
        if (!list.isEmpty()) {
            for (TrdCommandDispatcher001Dto dto : list) {
                String finType2 = dto.getFinprodType2();
                if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    dto.setFinprodType2(DictCodeList.平安资产类型_债券.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                    dto.setFinprodType2(DictCodeList.平安资产类型_基金.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_存款.getValue())) {
                    dto.setFinprodType2(DictCodeList.平安资产类型_存款.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                         || StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_拆出.getValue())) {
                    dto.setFinprodType2(DictCodeList.平安资产类型_拆借.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                         || StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())) {
                    dto.setFinprodType2(DictCodeList.平安资产类型_买断式回购.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                         || StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                    dto.setFinprodType2(DictCodeList.平安资产类型_质押式回购.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())) {
                    dto.setFinprodType2(DictCodeList.平安资产类型_项目类资产.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())) {
                    FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
                    if (StringUtil.isNotEmpty(finProduct)) {
                        if (StringUtil.equals(finProduct.getProfitType(), DictCodeList.收益类型_净值型.getValue())) {
                            dto.setFinprodType2(DictCodeList.平安资产类型_计划类资产_净值型.getValue());
                        }
                        else if (StringUtil.equals(finProduct.getProfitType(), DictCodeList.收益类型_收益型.getValue())) {
                            dto.setFinprodType2(DictCodeList.平安资产类型_计划类资产_利率型.getValue());
                        }
                        else if (StringUtil.equals(finProduct.getProfitType(), DictCodeList.收益类型_货币型.getValue())) {
                            dto.setFinprodType2(DictCodeList.平安资产类型_计划类资产_货币型.getValue());
                        }
                    }
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_即期.getValue())) {
                    dto.setFinprodType2(DictCodeListExt2.平安资产类型_外汇即期.getValue());
            }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_远期.getValue())) {
                    dto.setFinprodType2(DictCodeListExt2.平安资产类型_外汇远期.getValue());
        }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_掉期.getValue())) {
                    dto.setFinprodType2(DictCodeListExt2.平安资产类型_外汇掉期.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())) {
                    dto.setFinprodType2(DictCodeListExt2.平安资产类型_债券借贷.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())) {
                    dto.setFinprodType2(DictCodeListExt2.平安资产类型_债券借贷.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_利率互换.getValue())) {
                    dto.setFinprodType2(DictCodeListExt2.平安资产类型_利率互换.getValue());
                }
                else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                    dto.setFinprodType2(DictCodeListExt2.平安资产类型_股票.getValue());
                }
            }
        }*/
        return pageLimit;
    }
    
    /*private void setParams(Map<String, Object> pageParam) {
        pageParam.put("LEND_01", DictCodeList.回购拆借方向_拆入.getValue());
        pageParam.put("LEND_02", DictCodeList.回购拆借方向_拆出.getValue());
        pageParam.put("LEND_03", DictCodeList.回购拆借方向_正回购.getValue());
        pageParam.put("LEND_04", DictCodeList.回购拆借方向_逆回购.getValue());
        pageParam.put("LEND_05", DictCodeList.回购拆借方向_融入.getValue());
        pageParam.put("LEND_06", DictCodeList.回购拆借方向_融出.getValue());
        pageParam.put("F08", DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        pageParam.put("F09", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        pageParam.put("F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        pageParam.put("F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        pageParam.put("F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        pageParam.put("F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        pageParam.put("entrustAwait", DictCodeList.委托状态_待接收.getValue());
    }*/

    /**
     * 委托代码的联想文本框的查询方法
     * 
     * @param pageParam
     * @return List
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getEntrustId(Map<String, Object> pageParam) {
        return trdCommandDispatcher001Mapper.getEntrustId(pageParam);
    }

    /**
     * 组合代码的联想文本框的查询方法
     * 
     * @param param param
     * @return List
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getPortfolioIds(@RequestBody Map<String, Object> param) {
        return trdCommandDispatcher001Mapper.getPortfolioIds(param);
    }

    /**
     * 资产代码的联想文本框的查询方法
     * 
     * @param finprodId
     * @return List
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodIds(String finprodId) {
    	
    	List<SuggestDto> list1 =  trdCommandDispatcher001Mapper.getCommonFinprodIds(finprodId);
    	List<SuggestDto> list =  trdCommandDispatcher001Mapper.getFinprodIds(finprodId);
    	if(list1!=null && list1.size()>0) {
    		for(SuggestDto dto : list1) {
    			list.add(dto);
    		}
    	}
        return list;
    }

    /**
     * 获取radio交易员组的查询方法
     * 
     * @param pageParam param
     * @return List
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TrdCommandDispatcher001Dto> getRadios(Map<String, Object> pageParam) {
        return trdCommandDispatcher001Mapper.getRadios(pageParam);
    }

    /**
     * 获取联想文本框交易员的查询方法
     * 
     * @param pageParam param
     * @return List
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SelectOptionDto> getUsers(Map<String, Object> pageParam) {
        return trdCommandDispatcher001Mapper.getUsers(pageParam);
    }

    /**
     * 获取投资经理 <功能详细描述>
     * 
     * @param manager
     * @return
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SelectOptionDto> getManager(String manager) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("manager", manager);
        param.put("roleOne", DictCodeList.角色岗位_投资经理.getValue());
        param.put("roleTwo", DictCodeList.角色岗位_项目经理岗.getValue());
        //领导也属于投资经理范畴
        param.put("roleThree", DictCodeList.角色岗位_领导.getValue());
        return trdCommandDispatcher001Mapper.getManagers(param);
    }

    /**
     * 自动分发的保存
     */
    public int addAutoTime(PaEtrAutoDispenseTime record)
    {
        return paEtrAutoDispenseTimeMapper.insert(record);
    }

    /**
     * 得到自动分发的日期
     * 
     * @return
     */
    public List<TrdCommandDispatcher001Dto> selectEtrAutoTime() {
        return trdCommandDispatcher001Mapper.selectEtrAutoTime();
    }

    /**
     * 判断用户是否有相应资产类型的权限
     */
    public List<TrdCommandDispatcher001Dto> getAssetTypeUser(Map<String, Object> pageParam) {
        List<TrdCommandDispatcher001Dto> dealers = trdCommandDispatcher001Mapper.getAssetTypeUser(pageParam);
        return dealers;
    }

    /**
     * 删除并保存委托指令分发 <功能详细描述>
     * 
     * @param dto
     * @see
     * @since 1.0
     */
    public void delAndAddEtrDistribute(TrdCommandDispatcher001Dto dto) {
        String entrustId = dto.getEntrustId();
        String userName = dto.getDealers();
        List<String> entrustIds = StringUtil.splitToList(GlobalConstant.COMMA, entrustId);
        List<String> userNames = StringUtil.splitToList(GlobalConstant.COMMA, userName);
        EtrDistributeKey key = new EtrDistributeKey();
        // 将委托交易 状态为“已退回”的交易更新为“待接收” add by hpp 20200711 start
//        String entrustStatus = dto.getEntrustStatus();
//        List<String> entrustStatuss = StringUtil.splitToList(GlobalConstant.COMMA, entrustStatus);
//        for (int i = 0; i < entrustStatuss.size(); i++ ) {
//        	if(StringUtil.equals(DictCodeList.委托状态_已退回.getValue(), entrustStatuss.get(i))){
//        		// 修改委托指令表 委托状态
//        		EtrEntrust etrEntrust = new EtrEntrust();
//        		etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
//        		etrEntrust.setEntrustId(entrustIds.get(i));
//                etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
//        	}
//        }
        // 将委托交易 状态为“已退回”的交易更新为“待接收” add by hpp 20200711 end
        /*
         * for(int i=0;i<entrustIds.size();i++){ key.setEntrustId(entrustIds.get(i)); key.setTrader(trader); etrDistributeMapper.deleteByPrimaryKey(key); }
         */
        EtrDistribute record = new EtrDistribute();
        new EtrEntrust();
        // 删除原先分发过的所有交易员记录
        for (int j = 0; j < entrustIds.size(); j++ ) {
            key.setEntrustId(entrustIds.get(j));
            trdCommandDispatcher001Mapper.deleteEtrDistributeByPrimaryKey(key);
            //待分发转为待接收
            EtrEntrust etrEntrust = new EtrEntrust();
    		etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
    		etrEntrust.setEntrustId(entrustIds.get(j));
            etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
        }
        for (int i = 0; i < userNames.size(); i++ ) {
            record.setTrader(userNames.get(i));
            for (int j = 0; j < entrustIds.size(); j++ ) {
                record.setEntrustId(entrustIds.get(j));
                record.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                record.setCreateTime(dto.getCreateTime());
                record.setCreateUser(dto.getUpdateUser());
                record.setUpdateUser(dto.getUpdateUser());
                etrDistributeMapper.insert(record);
//                etrEntrust.setEntrustId(entrustIds.get(j));  已退回的不能再分发
//                etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
//                etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
            }
        }
        Map<String, Object> param = new HashMap<>();
		param.put("entrustId", dto.getEntrustId());
		param.put("isAutoReceive", dto.getIsAutoReceive());
		EntrCommandDispatcherTotalDto msgdto =  entrCommandDispatcherService.pushCommandMsg(param);
		
		// modify by etliu 20210203 可转债网下申购不发送交易端 start
		if (msgdto != null && !StringUtil.isEmptyList(msgdto.getEtr_entrust_list())) {
		    restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME
		        + "/EtrCommandDispatcher/pushCommandMsg",msgdto,String.class);
		}
        // modify by etliu 20210203 可转债网下申购不发送交易端 end
    }

    /**
     * 判断交易员是否有全部权限
     * 
     * @param userName
     * @param dealers
     * @return
     */
    public Map<String, Object> JudgingDealers(String otherFinprodType2, String userName,
                                              List<TrdCommandDispatcher001Dto> dealers) {
        List<String> otherFinprodTypeList = StringUtil.splitToList("otherFinprodType2", otherFinprodType2);
        // 循环删除重复元素
        for (int i = 0; i < otherFinprodTypeList.size() - 1; i++ ) {
            for (int j = otherFinprodTypeList.size() - 1; j > i; j-- ) {
                if (otherFinprodTypeList.get(j).equals(otherFinprodTypeList.get(i))) {
                    otherFinprodTypeList.remove(j);
                }
            }
        }
        List<String> userNames = StringUtil.splitToList(GlobalConstant.COMMA, userName);
        Map<String, Object> param = new HashMap<String, Object>();
        List<String> success = new ArrayList<String>();
        List<String> error = new ArrayList<String>();
        // 将根据资产类型查询到的用户和分发员比较判断
        for (int i = 0; i < userNames.size(); i++ ) {
            for (TrdCommandDispatcher001Dto dealer : dealers) {
                if (dealer.getDealers().equals(userNames.get(i).toString())) {
                    if (dealer.getCount() >= otherFinprodTypeList.size()) {
                        success.add(userNames.get(i).toString());
                        userNames.remove(userNames.get(i).toString());
                    }
                }
            }
        }
        param.put("success", success);
        // 判断所选分发员是否都有权限
        if (userNames.size() != 0) {
            for (String user : userNames) {
                error.add(user);
            }
        }
        param.put("error", error);
        return param;
    }

    /**
     * 自动分发的保存操作
     * 
     * @param dto
     */
    public void addAutoEtrDistribute(TrdCommandDispatcher001Dto dto) {
        Map<String, Object> param = new HashMap<String, Object>();
        List<String> otherFinprodTypeList = StringUtil.splitToList(GlobalConstant.COMMA, dto.getFinprodType2());
        List<String> entrustIds = StringUtil.splitToList(GlobalConstant.COMMA, dto.getEntrustId());
        // 循环删除重复元素
        for (int i = 0; i < otherFinprodTypeList.size() - 1; i++ ) {
            for (int j = otherFinprodTypeList.size() - 1; j > i; j-- ) {
                if (otherFinprodTypeList.get(j).equals(otherFinprodTypeList.get(i))) {
                    otherFinprodTypeList.remove(j);
                }
            }
        }
        param.put("finprodType2List", otherFinprodTypeList);
        List<TrdCommandDispatcher001Dto> dealers = trdCommandDispatcher001Mapper.getAssetTypeUser(param);
        EtrDistribute record = new EtrDistribute();
        for (TrdCommandDispatcher001Dto dealer : dealers) {
            for (String entrustId : entrustIds) {
                record.setEntrustId(entrustId);
                record.setTrader(dealer.getDealers());
                record.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                etrDistributeMapper.insert(record);
            }
        }
    }

    /**
     * 添加委托指令分发
     * 
     * @param dto
     * @return
     */
    public void addEtrDistribute(TrdCommandDispatcher001Dto dto) {
        Map<String, Object> param = new HashMap<String, Object>();
        List<String> otherFinprodTypeList = StringUtil.splitToList(GlobalConstant.COMMA, dto.getFinprodType2());
        List<String> entrustIds = StringUtil.splitToList(GlobalConstant.COMMA, dto.getEntrustId());
        // 循环删除重复元素
        for (int i = 0; i < otherFinprodTypeList.size() - 1; i++ ) {
            for (int j = otherFinprodTypeList.size() - 1; j > i; j-- ) {
                if (otherFinprodTypeList.get(j).equals(otherFinprodTypeList.get(i))) {
                    otherFinprodTypeList.remove(j);
                }
            }
        }
        // 得到相应权限的所有交易员
        param.put("finprodType2List", otherFinprodTypeList);
        List<TrdCommandDispatcher001Dto> dealers = getAssetTypeUser(param);
        EtrDistribute record = new EtrDistribute();
        for (TrdCommandDispatcher001Dto dealer : dealers) {
            record.setTrader(dealer.getDealers());
            for (String entrustId : entrustIds) {
                record.setEntrustId(entrustId);
                record.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                etrDistributeMapper.insert(record);
            }
        }
    }

    /**
     * 委托分发调用组件校验 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public List<ValidMessage> distributeCheck(TrdCommandDispatcher001Dto dto) {
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA, dto.getDealers());
        List<String> entrustIds = StringUtil.splitToList(GlobalConstant.COMMA, dto.getEntrustId());
        for (String entrustId : entrustIds) {
            errorList.addAll(etrInvesInfo.checkDelegateOperation(entrustId, DictCodeListExt1.委托操作类型_分发.getValue(), traderList,
                DictCodeListExt1.委托关闭方式_人工.getValue()));
        }
        return errorList;

    }

    /**
     * 将用户名转为真实姓名 <功能详细描述>
     * 
     * @param userName
     * @return
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User getRealNameByUserName(String userName) {
        return trdCommandDispatcher001Mapper.getRealNameByUserName(userName);
    }

    /**
     * 删除自动分发的日期
     * 
     * @return int
     */
    public void deleteEtrAutoTime() {
        trdCommandDispatcher001Mapper.deleteEtrAutoRule();
        trdCommandDispatcher001Mapper.deleteEtrAutoRuleDetail();
    }

    /**
     * 插入自动分发的日期
     * 
     * @param dto PaEtrAutoDispenseTime
     * @return int
     */
    public void insertEtrAutoTime(PaEtrAutoDispenseTimeDto dto) {
        String generate = IDGenerator.generate(32);
        EtrAutoDispenseRule rule = new EtrAutoDispenseRule();
        rule.setRuleId(generate);
        rule.setVdate(dto.getVdate());
        rule.setMdate(dto.getMdate());
        rule.setDispenseRule(dto.getDistributeRule());
        etrAutoDispenseRuleMapper.insertSelective(rule);
        if(StringUtil.isNotBlank(dto.getDistributeParameter())){
            String[] split = dto.getDistributeParameter().split(",");
            if(split.length>0){
                EtrAutoDispenseRuleDetail detail = new EtrAutoDispenseRuleDetail();
                for (String str:split) {
                    detail.setRuleId(generate);
                    detail.setDispensePara(str);
                    etrAutoDispenseRuleDetailMapper.insertSelective(detail);
                }
            }
        }
    }

    /**
     * 根据委托代码查询委托投标标位
     * @return
     */
    public TrdCommandDispatcher001Dto getEtrBidInfo(String entrustId){
        return trdCommandDispatcher001Mapper.getEtrBidInfo(entrustId);
    }
    /**
     * 根据投标日查询一投标债的交易人员的信息
     * @param dto
     * @return
     */
    public List<TrdCommandDispatcher001Dto>  getTraders(TrdCommandDispatcher001Dto dto) {
        return trdCommandDispatcher001Mapper.getTraders(dto);

    }

    public  List<TrdCommandDispatcher001Dto> selectEtrAutoRuleAndDetail(Date sysDate){
        return trdCommandDispatcher001Mapper.selectEtrAutoRuleAndDetail(DateUtil.format(sysDate));
    }

    /**
     * 查询交易员指令分发权限配置列表
     * @return page分页参数
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    public List<TrdCommandDispatcher001Dto> getTraderAuthList( ) {
        List<TrdCommandDispatcher001Dto> list =  trdCommandDispatcher001Mapper.getTraderAuthList();
        return list;
    }
    /**
     * 删除交易员指令分发的权限
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    public int deleteTraderAuthList() {
        return trdCommandDispatcher001Mapper.deleteTraderAuthList();
    }
    
    /**
     * 根据委托代码查询委托状态
     * add by hpp 20200710
     * @param map
     * @return
     */
    public List<TrdCommandDispatcher001Dto> getEntrustStatus(Map<String, Object> map){
    	return trdCommandDispatcher001Mapper.getEntrustStatus(map);
    }
}
