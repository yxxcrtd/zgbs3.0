/*
 * 文件名：FinProductBuild001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2018年9月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.interf.bankname.sale.job;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.interf.bankname.sale.service.SaleSystemInterfService;

/**
 * 接收E
 * @author 吴鼎
 * @version 1.0 2019年07月05日
 * @see SaleInterfJob
 * @since 1.0
 */
@Controller
public class SaleInterfJob{
    
	@Autowired 
    private SaleSystemInterfService saleSystemInterfService;
	
	// 平台日期
	@Autowired(required = false)
	private IPlatformDate iPlatformDate;
	
    
	/**
     * 行情下发
     * @return
     */
    @RequestMapping("sendMarketIssueToSale")
    public Map<String, String> sendMarketIssueToSale(){
    	
    	Map<String, String> res = new HashMap<>();
    	
    	String date = iPlatformDate.getSysDateStr();
        // Sonar Fix modify by huwei 220608
        Map<String, Object> result = saleSystemInterfService.sendMarketIssueToSale(date);
    	
    	//通知适配器生成文件
        boolean flag = (boolean) result.get("flag");
        
        if(flag){
        	res.put("status", DictCodeList.发送状态_发送成功.getValue());
        }else{
        	res.put("status", DictCodeList.发送状态_发送失败.getValue());
        }
    	
    	return res;
    }
    
    
    /**
     * 要素信息下发
     * @return
     */
    @RequestMapping("sendBaseInfoToSale")
    public Map<String, String> sendBaseInfoToSale(){
    	
    	Map<String, String> res = new HashMap<>();
    	
    	String date = iPlatformDate.getSysDateStr();

        Map<String, Object> result = saleSystemInterfService.sendProductInfoToSaleByDate(date);
    	
    	//通知适配器生成文件
        boolean flag = (boolean) result.get("flag");
        
        if(flag){
        	res.put("status", DictCodeList.发送状态_发送成功.getValue());
        }else{
        	res.put("status", DictCodeList.发送状态_发送失败.getValue());
        }
    	
    	return res;
    }
    
    /**
     * 分红方案下发
     * @return
     */
    @RequestMapping("sendBonusPlanToSale")
    public Map<String, String> sendBonusPlanToSale(){
    	
    	Map<String, String> res = new HashMap<>();
    	
    	String date = iPlatformDate.getSysDateStr();

        // Sonar Fix modify by huwei 220608
        Map<String, Object> result = saleSystemInterfService.sendBonusPlanToSaleByDate(date);
    	
    	//通知适配器生成文件
        boolean flag = (boolean) result.get("flag");
        
        if(flag){
        	res.put("status", DictCodeList.发送状态_发送成功.getValue());
        }else{
        	res.put("status", DictCodeList.发送状态_发送失败.getValue());
        }
    	
    	return res;
    }
    
    
    /**
     * 获取销售系统认申购数据
     * @return
     * @throws Exception 
     */
    @RequestMapping("receiveAlloFromSale")
    public void receiveAlloFromSale() {
    	
    	Map<String, String> res = new HashMap<>();
    	
    	String date = iPlatformDate.getSysDateStr();
    	
    	saleSystemInterfService.receiveAlloFromSale(date);
    	
    }
    
    /**
     * 获取销售系统赎回到期数据
     * @return
     * @throws Exception 
     */
    @RequestMapping("receiveRedeemFromSale")
    public void receiveRedeemFromSale() {
    	
    	Map<String, String> res = new HashMap<>();
    	
    	String date = iPlatformDate.getSysDateStr();
    	
    	saleSystemInterfService.receiveRedeemFromSale(date);
    	
    }
    
    /**
     * 获取销售系统份额强增强减数据
     * @return
     */
    @RequestMapping("receiveAddReduceSale")
    public void receiveAddReduceSale(){
    	
    	Map<String, String> res = new HashMap<>();
    	
    	String date = iPlatformDate.getSysDateStr();
    	
    	saleSystemInterfService.receiveAddReduceSale(date);
    	
    }
    
    /**
     * 获取销售系统分红汇总数据
     * @return
     * @throws Exception 
     */
    @RequestMapping("receiveBonusCollectSale")
    public void receiveBonusCollectSale() {
    	
    	Map<String, String> res = new HashMap<>();
    	
    	String date = iPlatformDate.getSysDateStr();
    	
    	saleSystemInterfService.receiveBonusCollectSale(date);
    	
    }
    
    
    /**
     * 获取销售系统产品日计提利息信息数据
     * @return
     */
    @RequestMapping("receiveProdAccrueSale")
    public void receiveProdAccrueSale(){
    	
    	Map<String, String> res = new HashMap<>();
    	
    	String date = iPlatformDate.getSysDateStr();
    	
    	saleSystemInterfService.receiveProdAccrueSale(date);
    	
    }
    
}
