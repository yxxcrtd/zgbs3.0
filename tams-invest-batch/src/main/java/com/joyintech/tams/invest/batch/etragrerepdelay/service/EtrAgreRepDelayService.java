package com.joyintech.tams.invest.batch.etragrerepdelay.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.etragrerepdelay.dao.EtrAgreRepDelayMapper;

@Service
public class EtrAgreRepDelayService {
	
	@Autowired
	private EtrAgreRepDelayMapper etrAgreRepDelayMapper;
	public void run(String sysDate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("TARGET_BACK_TYPE_H01", DictCodeList.回购类型_质押式协议回购.getValue());
		map.put("BACK_LEND_DIRECTION_03", DictCodeList.回购拆借方向_正回购.getValue());
		map.put("BACK_LEND_DIRECTION_04", DictCodeList.回购拆借方向_逆回购.getValue());
		map.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
		map.put("PS_04", DictCodeListExt3.交易方向_正回购.getValue());
		map.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
		map.put("YES_OR_NO_N", DictCodeListExt1.是否_否.getValue());
		map.put("PS_T07", DictCodeList.交易方向_到期.getValue());
		map.put("PS_C20", DictCodeList.交易方向_提前终止.getValue());
		map.put("PS_T56", DictCodeListExt3.交易方向_到期续做.getValue());
		map.put("PS_T93", DictCodeListExt3.交易方向_解除质押.getValue());
		map.put("PS_T95", DictCodeListExt3.交易方向_到期购回.getValue());
		map.put("PS_T96", DictCodeListExt3.交易方向_提前购回.getValue());

		map.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
		map.put("sysDate", sysDate);
		//到期指令
		List<FinTradeProduct> finprodList = etrAgreRepDelayMapper.getExpireFinprodId(map);
		//到期结算日 = 跑批日 + 1
		Date mSettleDate = DateUtil.addDay(DateUtil.parse(sysDate), 1);
		if(!StringUtil.isEmptyList(finprodList)) {
			for(FinTradeProduct finTradeProduct : finprodList) {
				//实际占款天数 = 到期结算日 - 首次交付日
				int days = DateUtil.dateCompareTo(DateUtil.format(finTradeProduct.getVpayDate()), DateUtil.format(mSettleDate));
				//交易类金融产品表、金融产品交易表、金融产品临时交易表更新
				finTradeProduct.setMpayDate(mSettleDate);
				finTradeProduct.setActCapDays(Long.valueOf(days));
				etrAgreRepDelayMapper.updateFinTradeProduct(finTradeProduct);
				etrAgreRepDelayMapper.updateTrdProdctDeal(finTradeProduct);
				etrAgreRepDelayMapper.updateTrdTrdPrdTempDeal(finTradeProduct);
			}
		}
	}
}
