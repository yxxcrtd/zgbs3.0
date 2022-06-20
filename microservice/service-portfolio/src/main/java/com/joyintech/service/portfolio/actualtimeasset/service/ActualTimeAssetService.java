package com.joyintech.service.portfolio.actualtimeasset.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.portfolio.actualtimeasset.dao.ActualTimeAssetMapper;
import com.joyintech.service.portfolio.actualtimeasset.dto.ActualTimeAssetDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @program: tams-invest-parent
 * @description: ActualTimeAssetService
 * @author: yanya
 * @create: 2019-10-15
 **/
@Service
public class ActualTimeAssetService extends BaseService<ActualTimeAssetDto> {
    /**
     * 实时资产头寸mapper
     */
    @Autowired
    private ActualTimeAssetMapper actualTimeAssetMapper;

    /**
     * 证券账户下拉框
     *
     * @param map 输入参数
     * @return 结果
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecAcct(Map<String,Object> map) {
        return actualTimeAssetMapper.getSecAcct(map);
    }

    /**
     * 资产代码下拉框
     *
     * @param map 输入参数
     * @return 结果
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getAssetsInfo(Map<String,Object> map) {
        //不显示的资产类型
        List<String> noShowType8List = Arrays.asList(DictCodeListExt2.平安资产类型_外汇即期.getValue(), DictCodeListExt2.平安资产类型_外汇掉期.getValue(),
            DictCodeListExt2.平安资产类型_债券借贷.getValue(), DictCodeListExt2.平安资产类型_外汇远期.getValue(),
            DictCodeListExt2.平安资产类型_一级市场债.getValue());
        map.put("noShowType8List", noShowType8List);
        // 多级法人改造 add by jingqiongqiong 20200615
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //当前用户所属机构代码
            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            //右上角选择框为空时 存放本级机构及有权限的下级机构 选择时 存放所选机构
            List<String> resourcesTreeInstCodeList =  LoginUserUtil.getTreeInstCode();
            map.put("institutionCode",createInst);
            map.put("resourcesTreeInstCodeList",resourcesTreeInstCodeList);
        }
        // 多级法人改造 add by jingqiongqiong 20200615
        return actualTimeAssetMapper.getAssetsInfo(map);
    }

    /**
     * 画面查询方法检索分页数据。返回类型是Service的泛型。
     * @param pageParam pageParam
     * @param page 页码
     * @param length 页面长度
     * @return Page类  Page
     * @throws Exception 异常信息
     */
    public Page<ActualTimeAssetDto> selectForPage(int page, int length, Map<String, Object> pageParam)
    {
        //组合持仓类型
        // modify by jiangpeng on 20210526 ZGBSSE-418 计划类净值型提交时没有可用持仓
        List<String> hodingTypeList = Arrays.asList(DictCodeList.持仓类型_实际.getValue(), DictCodeList.持仓类型_在途.getValue(),
                DictCodeList.持仓类型_融入.getValue(), DictCodeList.持仓类型_融出.getValue(),
            DictCodeList.持仓类型_质押.getValue(), DictCodeListExt1.持仓类型_预占用.getValue());
        pageParam.put("hodingTypeList", hodingTypeList);
        //组合持仓流水类型
        List<String> secFlowHodingTypeList =Arrays.asList(DictCodeList.持仓类型_可用.getValue(),DictCodeListExt1.持仓类型_借贷融出.getValue());
        pageParam.put("secFlowHodingTypeList", secFlowHodingTypeList);
        //指令持仓类型
        List<String> etrHodingTypeList = Arrays.asList(DictCodeListExt1.持仓类型_委托指令可用.getValue(), DictCodeList.持仓类型_质押.getValue());
        pageParam.put("etrHodingTypeList", etrHodingTypeList);
        //资产类型特殊处理分类
        List<String> type8List = Arrays.asList(DictCodeList.平安资产类型_质押式回购.getValue(), DictCodeList.平安资产类型_买断式回购.getValue(),
            DictCodeList.平安资产类型_拆借.getValue(), DictCodeListExt2.平安资产类型_利率互换.getValue(), DictCodeListExt2.平安资产类型_期权.getValue(),
                BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        pageParam.put("type8List", type8List);
        //不显示的资产类型
        List<String> noShowType8List = Arrays.asList(DictCodeListExt2.平安资产类型_外汇即期.getValue(), DictCodeListExt2.平安资产类型_外汇掉期.getValue(),
            DictCodeListExt2.平安资产类型_债券借贷.getValue(), DictCodeListExt2.平安资产类型_外汇远期.getValue(),
            DictCodeListExt2.平安资产类型_一级市场债.getValue());
        pageParam.put("noShowType8List", noShowType8List);

        pageParam.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
        pageParam.put("finprodType2_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("finprodType2_F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        pageParam.put("finprodType2_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        pageParam.put("finprodType2_F10", DictCodeList.平安资产类型_买断式回购.getValue());
        pageParam.put("finprodType2_F20", DictCodeList.平安资产类型_现金资产.getValue());
        pageParam.put("finprodType2_F32", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        //update zhangweiqi SNSTZJY-2370 按照实际持仓最大的优先扣减指令持仓  2020-12-15 begin
        Page<ActualTimeAssetDto> pageList = super.queryForPage(page, length, () -> actualTimeAssetMapper.findPage(pageParam));
		for (ActualTimeAssetDto dto : pageList.getRoot()) {
			if (StringUtil.equals(dto.getType8(), DictCodeList.平安资产类型_债券.getValue())
					) {
				pageParam.put("finprodId", dto.getFinprodId());
				pageParam.put("etrHodingTypeList", Arrays.asList(DictCodeListExt1.持仓类型_委托指令可用.getValue()));
                pageParam.put("portfolioIdList",Arrays.asList(dto.getPortfolioId()));
				//pageParam.put("secAcctId",dto.getSecAcctId());
				ActualTimeAssetDto maxPositionDto = actualTimeAssetMapper.getTradePosition(pageParam);
				if (StringUtil.equals(dto.getSecAcctId(), maxPositionDto.getSecAcctId())) {
					BigDecimal etrAmount = actualTimeAssetMapper.getEtrAmount(pageParam);
					BigDecimal tradeAmount = actualTimeAssetMapper.getTradeAmount(pageParam);
					etrAmount = etrAmount.multiply(new BigDecimal(100));
					tradeAmount = tradeAmount.multiply(new BigDecimal(100));
					dto.setInstructionsAmount(dto.getActualPositionAmount().add(etrAmount.subtract(tradeAmount)));// 差额出在实际持仓最大的上

				} else {
					dto.setInstructionsAmount(dto.getActualPositionAmount());
				}
				// ZGZGST-1408: 市值=当前持仓数量*最近估值全价；当前持仓数量=券面总额/100 fuyongsheng 20210830 add start
                dto.setMarketValue(null != dto.getMarketValue() ? dto.getMarketValue().divide(new BigDecimal(100),2, RoundingMode.HALF_UP) : null);
                // ZGZGST-1408: 市值=当前持仓数量*最近估值全价；当前持仓数量=券面总额/100 fuyongsheng 20210830 add end
			}
		}

        return pageList;
      //update zhangweiqi SNSTZJY-2370 按照实际持仓最大的优先扣减指令持仓  2020-12-15 end
    }
}
