/*
 * 文件名：MstSecuritiesAccountController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangjun
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstsecuritiesaccount.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstsecuritiesaccount.dto.MstSecuritiesAccountDto;
import com.joyintech.service.basicdata.mstsecuritiesaccount.service.MstSecuritiesAccountService;
import com.joyintech.tams.common.base.entity.StlSecAcctInfo;


/**
 * 证券账户管理微服务
 * 〈功能详细描述〉
 * @author zhangjun
 * @version 1.0 2018年9月11日
 * @see MstSecuritiesAccountController
 * @since 1.0
 */
@RestController("MstSecuritiesAccountInfoCtr_dtx")
@RequestMapping("/MstSecuritiesAccountInfo")
public class MstSecuritiesAccountController {
    /**
     * 证券管理微服务Service
     */
    @Autowired
    private MstSecuritiesAccountService mstSecuritiesAccountService;

    
    /**
     * 画面查询
     * 
     * @return Page
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<MstSecuritiesAccountDto> selectAll(@RequestBody Map<String, Object> param,
        @PathVariable("page")int page,@PathVariable("length")int length)
    {
        return mstSecuritiesAccountService.selectForPage(page,length,param);
    }
    
        /**
         * 根据证券账户账号进行查询
         * 
         * @return Page
         * @throws Exception Exception
         * @see
         * @since 1.0
         */
    @GetMapping("selectBySecAcctId")
        public StlSecAcctInfo selectBySecAcctId(String secAcctId)
        {
            return mstSecuritiesAccountService.selectBySecAcctId(secAcctId);
        }

    /**
     * 证券账户账号联想文本框 <功能详细描述>
     * 
     * @return List
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @GetMapping("getSecAcctId")
    public List<SuggestDto> getSecAcctId(String secAcctId)
    {
        List<SuggestDto> temp = mstSecuritiesAccountService.getSecAcctId(secAcctId);
        return temp;
    }
    
    /**
     * 组合代码联想文本框service层实现
     *
     * @param param Map<String, Object>
     * @return List<SuggestDto>
     */
    @PostMapping("/getPortfolioId")
    public List<SuggestDto> getPortfolioId(@RequestBody Map<String, Object> param) {
        return mstSecuritiesAccountService.getPortfolioId(param);
    }
    
    /**
     * 
     * @param companyNo
     * @return
     * @throws Exception
     */
    @GetMapping("getFuturesCompany")
    public List<SelectOptionDto> getFuturesCompany(String companyNo)
            throws Exception {
        List<SelectOptionDto> temp = mstSecuritiesAccountService.getFuturesCompany(companyNo);
        return temp;
    }
    
    /**
     * 回款账户以及资金结算账户联想文本框 <功能详细描述>
     * 
     * @return List
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @GetMapping("getBusiAcctNo")
    public List<SelectOptionDto> getBusiAcctNo(String busiAcctNo)
    {
        List<SelectOptionDto> temp = mstSecuritiesAccountService.getBusiAcctNo(busiAcctNo);
        return temp;
    }
    /**
     * 删除
     * 
     * @param dto
     *            页面选中的数据
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody StlSecAcctInfo dto)
    {
        return mstSecuritiesAccountService.delete(dto);
    }
    /**
     * 新建
     * 
     * @param dto
     *            页面选中的数据
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public int insert(@RequestBody MstSecuritiesAccountDto dto)
    {
        return mstSecuritiesAccountService.insert(dto);
    }
    /**
     * 修改
     * 
     * @param dto
     *            页面选中的数据
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public int update(@RequestBody MstSecuritiesAccountDto dto)
    {
        return mstSecuritiesAccountService.update(dto);
    }
    /**
     * 检查证券账户账号是否用过
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return List
     * @see
     * @since 1.0  
     */
    @PostMapping("checkBySecAcctId")
    public int checkBySecAcctId(@RequestBody MstSecuritiesAccountDto dto)
    {
        int count = mstSecuritiesAccountService.checkBySecAcctId(dto);
        return count;
    }
    /**
     * 检查证券账户名称是否用过
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return List
     * @see
     * @since 1.0  
     */
    @PostMapping("checkBySecAcctName")
    public int checkBySecAcctName(@RequestBody MstSecuritiesAccountDto dto)
    {
        int count = mstSecuritiesAccountService.checkBySecAcctName(dto);
        return count;
    }
    /**
     * 检查资金结算账户是否存在
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return List
     * @see
     * @since 1.0  
     */
    @PostMapping("checkByTrdAcctId")
    public int checkByTrdAcctId(@RequestBody MstSecuritiesAccountDto dto)
    {
        int count = mstSecuritiesAccountService.checkByTrdAcctId(dto);
        return count;
    }
    /**
     * 
     *删除时检查数据是否被使用过
     * <功能详细描述>
     * @param dto 业务实体
     * @return int
     * @see
     * @since 1.0
     */
    @PostMapping("checkCodeIsUse")
    public int checkCodeIsUse(@RequestBody MstSecuritiesAccountDto dto)
    {
        int count = mstSecuritiesAccountService.checkCodeIsUse(dto);
        return count;
    }
    /**
     * 
     *批量删除时检查数据是否被使用过
     * <功能详细描述>
     * @param item List<MstSecuritiesAccountDto>
     * @return int
     * @see
     * @since 1.0
     */
    @PostMapping("checkAllCodeIsUse")
    public List<MstSecuritiesAccountDto> checkAllCodeIsUse(@RequestBody List<MstSecuritiesAccountDto> item)
    {
        return mstSecuritiesAccountService.checkAllCodeIsUse(item);
    }
//        /**
//         * 
//         *查询插入的附件文件
//         * <功能详细描述>
//         * @param item List<MstSecuritiesAccountDto>
//         * @return int
//         * @see
//         * @since 1.0
//         */
//        @GetMapping("selectFileByBusinessId")
//        public List<FileDto> selectFileByBusinessId(String businessId)
//            throws Exception {
//            return mstSecuritiesAccountService.selectFileByBusinessId(businessId);
//        }
//        /**
//         * 
//         *插入附件文件
//         * <功能详细描述>
//         * @param item List<MstSecuritiesAccountDto>
//         * @return int
//         * @see
//         * @since 1.0
//         */
//        @PostMapping("insertIntoFile")
//        public int insertIntoFile(@RequestBody FileDto dto)
//            throws Exception {
//            return mstSecuritiesAccountService.insertIntoFile(dto);
//        } 
//        /**
//         * 
//         *删除附件文件
//         * <功能详细描述>
//         * @param item List<MstSecuritiesAccountDto>
//         * @return int
//         * @see
//         * @since 1.0
//         */
//        @GetMapping("deleteFileByBusinessId")
//        public int deleteFileByBusinessId(String businessId)
//            throws Exception {
//            return mstSecuritiesAccountService.deleteFileByBusinessId(businessId);
//        }  
    /**
     * 获取证券账户表
     * @param map
     * @return
     * @throws Exception
     */
	@PostMapping("getSecAcctInfo")
	public List<SelectOptionDto> getSecAcctInfo(@RequestBody Map<String, Object> map) throws Exception {
		return mstSecuritiesAccountService.getSecAcctInfo(map);
	}

	/**
	 * 获取组合资金账户
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@PostMapping("getPtlSecAcctInfo")
	public List<SelectOptionDto> getPtlSecAcctInfo(@RequestBody Map<String, Object> map) throws Exception {
		return mstSecuritiesAccountService.getPtlSecAcctInfo(map);
	}
	
	/**
	 * 获取证券代码
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@PostMapping("getFinprodMarketInfo/{page}/{length}")
	public Page<SelectOptionDto> getFinprodMarketInfo(@RequestBody Map<String, Object> map,
			@PathVariable("page") int page, @PathVariable("length") int length) throws Exception {
		return mstSecuritiesAccountService.getFinprodMarketInfo(page, length, map);
	}

    /**
     * 场内指令 - 持仓页面-获取资产代码下拉框数据
     * @param map
     * @return
     * @throws Exception
     */
    @PostMapping("getFinprodMarketInfoPosition/{page}/{length}")
    public Page<SelectOptionDto> getFinprodMarketInfoPosition(@RequestBody Map<String, Object> map,
                                                      @PathVariable("page") int page, @PathVariable("length") int length) throws Exception {
        return mstSecuritiesAccountService.getFinprodMarketInfoPosition(page, length, map);
    }
	
	/**
	 * 获取标的信息
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@PostMapping("getFinprodId")
	public List<SelectOptionDto> getFinprodId(@RequestBody Map<String, Object> map) throws Exception {
		return mstSecuritiesAccountService.getFinprodId(map);
	}

    /**
     *
     * @param map
     * @return
     * @throws Exception
     */
    @PostMapping("getFinprodIdByMarketId")
    public List<SelectOptionDto> getFinprodIdByMarketId(@RequestBody Map<String, Object> map) throws Exception {
        return mstSecuritiesAccountService.getFinprodIdByMarketId(map);
    }

    /**
     *
     * @param map
     * @return
     * @throws Exception
     */
    @PostMapping("getFaceValueList")
    public List<Map<String, Object>> getFaceValueList(@RequestBody Map<String, Object> map) throws Exception {
        return mstSecuritiesAccountService.getFaceValueList(map);
    }

    /**
     * 判断资产是不是债券
     *
     * @return List
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @PostMapping("IsBondAsset")
    public String IsBondAsset(@RequestBody Map<String, Object> map)
    {
        return mstSecuritiesAccountService.IsBondAsset(map);

    }
    /**
     * 获取证券代码
     *
     * @param map
     * @return
     * @throws Exception
     */
    @PostMapping("getFinprodMarketInfoNewUi/{page}/{length}")
    public Page<SelectOptionDto> getFinprodMarketInfoNewUi(@RequestBody Map<String, Object> map,
                                                      @PathVariable("page") int page, @PathVariable("length") int length) throws Exception {
        Page<SelectOptionDto> asas=mstSecuritiesAccountService.getFinprodMarketInfoNewUi(page, length, map);
        return asas;
    }

    /**
     * 获取证券代码(债券 质押式回购)
     *
     * @param map
     * @return
     * @throws Exception
     */
    @PostMapping("getFinprodMarketInfoSecRepo/{page}/{length}")
    public Page<SelectOptionDto> getFinprodMarketInfoSecRepo(@RequestBody Map<String, Object> map,
                                                           @PathVariable("page") int page, @PathVariable("length") int length) throws Exception {
        Page<SelectOptionDto> asas=mstSecuritiesAccountService.getFinprodMarketInfoSecRepo(page, length, map);
        return asas;
    }

    /**
     * 现券列表查询获取资产代码
     * @param map
     * @param page
     * @param length
     * @return
     * @throws Exception
     */
    @PostMapping("getFinprodListSecRepo/{page}/{length}")
    public Page<SelectOptionDto> getFinprodListSecRepo(@RequestBody Map<String, Object> map,
                                                             @PathVariable("page") int page, @PathVariable("length") int length) throws Exception {
        Page<SelectOptionDto> asas=mstSecuritiesAccountService.getFinprodListSecRepo(page, length, map);
        return asas;
    }
}
