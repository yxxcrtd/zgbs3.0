package com.joyintech.service.entrust.etrinstruction.service; /*
 * 文件名：
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：luocheng
 * 修改时间：2020/1/6
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.dao.EntrustCommonPersonSelectMapper;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etrfund.dao.EtrFundMapper;
import com.joyintech.service.entrust.etrfund.dto.EtrFundDto;
import com.joyintech.service.entrust.etrinstruction.dao.EtrInstructionMapper;
import com.joyintech.service.entrust.etrinstruction.dto.EtrInstructionDto;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("backEtrInstructionService")
public class EtrInstructionService extends BaseService<EtrInstructionDto> {


    @Autowired
    private EtrInstructionMapper instructionMapper;

    /**
     * 附件信息共通Service
     */
    @Autowired(required=false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 平台日期
     */
    @Autowired(required=false)
    private IPlatformDate platformDateImpl;

    /**
     * 委托自动分发
     */
    @Autowired(required=false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    /**
     * 委托指令共同
     */
    @Autowired(required=false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    /**
     * 委托人员信息
     */
    @Autowired
    private EntrustCommonPersonSelectMapper entrustCommonPersonSelectMapper;

    /**
     * 使用给定的检索分页数据。返回类型是Service的泛型。
     *
     * @param length int
     * @param page int
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<EtrInstructionDto> selectForPage(int page, int length,
                                          Map<String, Object> pageParam)
    {
        String assetTypeOne = (String)pageParam.get("assetType1");
        if(StringUtil.isNotEmpty(assetTypeOne)){
            pageParam.put("assetTypeOneList", StringUtil.splitToList(GlobalConstant.COMMA,assetTypeOne));
        }
        String assetTypeTwo = (String)pageParam.get("assetType10");
        if(StringUtil.isNotEmpty(assetTypeTwo)){
            pageParam.put("assetTypeTwoList", StringUtil.splitToList(GlobalConstant.COMMA,assetTypeTwo));
        }
        pageParam.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_立项指令.getValue());
        pageParam.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());
        return super.queryForPage(page, length, () -> instructionMapper.select(pageParam));
    }


    /**
     *
     * 资产代码联想文本框
     * <功能详细描述>
     * @param finprodId String
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodMarketId(String finprodId){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_立项指令.getValue());
        return instructionMapper.getFinprodMarketId(map);
    }

    /**
     *
     * 融资人/管理人 联想文本框
     * <功能详细描述>
     * @param repFinancier String
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getRepFinancierList(String repFinancier){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("repFinancier", repFinancier);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_立项指令.getValue());
        return instructionMapper.getRepFinancierList(map);
    }

    /**
     * 编辑、双击查看页面详情 <功能详细描述>
     *
     * @param entrustId String

     * @return EtrFundDto
     * @see
     * @since 1.0
     */
    public EtrInstructionDto selectByKey(String entrustId) {
        EtrInstructionDto etrInstructionDto = instructionMapper.selectByKey(entrustId);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("entrustId", entrustId);
        //获取委托人员
        List<String> traderList = entrustCommonPersonSelectMapper.getTraderList(map);
        String trader = "";
        for(int i=0; i<traderList.size(); i++){
            trader = trader +","+ traderList.get(i);
        }
        etrInstructionDto.getBussEtrInvestPersonInfoDto().setTrader(trader);
        return etrInstructionDto;
    }

    /**
     * 关闭、撤销、删除委托画面校验
     * <功能详细描述>
     * @param dto EtrFundDto
     * @return List<ValidMessage>
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrInstructionDto dto) {
        String entrustId = dto.getEntrustId();
        String delegateOperationType = dto.getEntrustOperationType();
        String delegateCloseWay = dto.getCloseWay();
        List<String> traderList = new ArrayList<>();
        traderList.add(dto.getTrader());
        return baseEtrInvestInfoService.checkDelegateOperation(entrustId, delegateOperationType,
            traderList, delegateCloseWay);
    }

    /**
     *
     * 撤销委托
     * 调用基础组件“关闭委托”
     * @param entrustId Stringg
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(String entrustId){
        //投资委托信息dto
        BaseEtrInvestInfoDto dto = new BaseEtrInvestInfoDto();
        //委托指令
        EtrEntrust etrEntrust = new EtrEntrust();
        etrEntrust.setEntrustId(entrustId);
        dto.setEtrEntrust(etrEntrust);
        if(baseEtrInvestInfoService != null){
            return baseEtrInvestInfoService.recallDelegate(dto);
        }
        else{
            return false;
        }
    }

    /**
     *
     * 关闭委托
     * 调用基础组件“关闭委托”
     * @param entrustId String
     * @param closeWay String
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId,String closeWay){
        if(baseEtrInvestInfoService != null){
            return baseEtrInvestInfoService.closeDelegate(entrustId, closeWay);
        }
        else{
            return false;
        }
    }


    /**
     *
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity EtrFundDto
     * @throws IllegalAccessException IllegalAccessException
     * @throws InvocationTargetException InvocationTargetException
     * @see
     * @since 1.0
     */
    public void doInsert(EtrInstructionDto dto) throws IllegalAccessException, InvocationTargetException {
        //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(dto.getFileArray());
        mstFileInputParameter.setBussinessId(dto.getWorkflowId());
        if(mstFileCommonService != null){
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }

        if (!StringUtil.equals("HTQS", dto.getOperateType()) && "submit".equals(dto.getOperateType())) {
            BaseEtrInvestInfoDto entity = initBaseDto(dto);
            if(StringUtil.isEmpty(entity.getWorkFlowId())){
                throw new RuntimeException("WorkFlowId初始化不能为空!");
            }
            WFlowCommonService.insertFlow(entity);
            if (!StringUtil.equals("HTQS", dto.getOperateType())
                    && !StringUtil.equals("revoke", dto.getOperateType())
                    && !StringUtil.equals("delete", dto.getOperateType())) {

                WFlowCommonService.deleteFlow(entity.getWorkFlowId());
                WFlowCommonService.insertFlow(entity);
            }
        }
    }

    public void doUpdate(EtrInstructionDto dto)
            throws IllegalAccessException,
            InvocationTargetException {
        //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(dto.getFileArray());
        mstFileInputParameter.setBussinessId(dto.getWorkflowId());
        if(mstFileCommonService != null){
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
        
        if (!StringUtil.equals("HTQS", dto.getOperateType()) 
                && !StringUtil.equals("revoke", dto.getOperateType()) 
                && !StringUtil.equals("delete", dto.getOperateType())) {
            BaseEtrInvestInfoDto entity = initBaseDto(dto);
            WFlowCommonService.deleteFlow(entity.getWorkFlowId());
            WFlowCommonService.insertFlow(entity);
            
            WFlowCommonService.deleteFlow(entity.getWorkFlowId());
            WFlowCommonService.insertFlow(entity);
        }
        
    }
    
    /**
     * 流程完成时的数据表单进程操作
     * @param entity
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void doComplete(EtrInstructionDto entity)
        throws IllegalAccessException,
        InvocationTargetException {
        // 投资委托信息dto
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        String type = entity.getOperateType();
        if (StringUtil.equals(type, "NEW")) {
            // 委托指令Entity
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, entity);
            BeanUtil.copyProperties(etrEntrust, entity.getBussEtrInvestPersonInfoDto());
            //设置委托状态
            etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_立项指令.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            etrEntrust.setFinprodId(entity.getFinprodMarketId());
            if(platformDateImpl != null){
                etrEntrust.setEntrustDate(platformDateImpl.getSysDate());
            }
            // 委托指令附属表Entity
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            etrEntrustAdd.setAssetType(DictCodeListExt3.共通业务类型_立项指令.getValue());
            etrEntrustAdd.setAssetType11(entity.getFinprodAbbr());//资产代码
            etrEntrustAdd.setSecAcctId(entity.getRepFinancier()); //融资人/管理人
            BeanUtil.copyProperties(etrEntrustAdd, entity);
            //设置资产一级类型：资产一级分类=资产类型
            //etrEntrustAdd.setAssetType1(entity.getAssetType1());
            //设置起息日到期日
            etrEntrustAdd.setVdate(entity.getvDate());
            etrEntrustAdd.setMdate(entity.getmDate());
            // List<委托指令回购质押品Entity>

            // List<委托完成状况表Entity>
            List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
           /* EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            BeanUtil.copyProperties(etrFinishStatus, entity);
            etrFinishStatusList.add(etrFinishStatus);*/

            baseEtrInvestInfoDto.setEtrEntrust(etrEntrust);
            baseEtrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);
            if(busiAutoEtrDispService != null){
            //自动分发
             baseEtrInvestInfoDto.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));

            }
            baseEtrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);

            // 新建委托
            if (baseEtrInvestInfoService != null) {
                baseEtrInvestInfoService.newDelegate(baseEtrInvestInfoDto);
            }
            //文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getEntrustId());
            if(mstFileCommonService != null){
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        }else if(StringUtil.equals(type, "HTQS")){

          instructionMapper.qshtbyId(entity);
          instructionMapper.qshtfinprodMarketIdbyId(entity);

        }else {
            // 委托指令Entity
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, entity);
            etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_立项指令.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            etrEntrust.setFinprodId(entity.getFinprodMarketId());
            if(platformDateImpl != null){
                etrEntrust.setEntrustDate(platformDateImpl.getSysDate());
            }
            // 委托指令附属表Entity
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            BeanUtil.copyProperties(etrEntrustAdd, entity);
            etrEntrustAdd.setAssetType11(entity.getFinprodAbbr());//资产代码
            etrEntrustAdd.setSecAcctId(entity.getRepFinancier()); //融资人/管理人
            etrEntrustAdd.setAssetType(DictCodeListExt3.共通业务类型_立项指令.getValue());
            if(null!=entity.getBussEtrInvestPersonInfoDto()) {
                BeanUtil.copyProperties(etrEntrust, entity.getBussEtrInvestPersonInfoDto());
            }
            //设置起息日到期日
            etrEntrustAdd.setVdate(entity.getvDate());
            etrEntrustAdd.setMdate(entity.getmDate());
            // List<委托指令回购质押品Entity>


            // List<委托完成状况表Entity>
            List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
            /*EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            BeanUtil.copyProperties(etrFinishStatus, entity);
            etrFinishStatusList.add(etrFinishStatus);*/

            baseEtrInvestInfoDto.setEtrEntrust(etrEntrust);
            baseEtrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);
            if(busiAutoEtrDispService != null){
            //自动分发
             baseEtrInvestInfoDto.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
            }
            baseEtrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);

            // 修改委托
            if (baseEtrInvestInfoService != null) {
                baseEtrInvestInfoService.updateDelegate(baseEtrInvestInfoDto);
            }

            //上传文件修改更新
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setBussinessId(entity.getEntrustId());
            mstFileInputParameter.setFileList(entity.getFileArray());
            if(mstFileCommonService != null){
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        }

//        if(baseEtrCashPositionCommService!=null && !StringUtil.equals(type, "HTQS")){
//            baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
//        }
    }

    public BaseEtrInvestInfoDto initBaseDto(EtrInstructionDto dto){
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();

        try {
            // etrEntrust
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getBussEtrInvestPersonInfoDto());
            //设置委托状态
            etrEntrust.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            if(platformDateImpl != null){
                etrEntrust.setEntrustDate(platformDateImpl.getSysDate());
            }
            entity.setEtrEntrust(etrEntrust);

            // etrEntrustAdd
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_基金.getValue());
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            //设置起息日到期日
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            entity.setEtrEntrustAdd(etrEntrustAdd);

            // 添加流程编号
            entity.setWorkFlowId(dto.getWorkflowId());
        } catch (IllegalAccessException | InvocationTargetException e) {
            // 对象转换赋值失败
            throw new RuntimeException("对象转换赋值失败", e);
        }
        return entity;
    }
}
