package com.joyintech.tams.settle.batch.datacreate.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SzdJsmx;
import com.joyintech.tams.common.base.entity.SzdSjsjg;
import com.joyintech.tams.common.base.entity.SzdSjsmx;
import com.joyintech.tams.settle.batch.datacreate.constant.StlDataCreateConstant;
import com.joyintech.tams.settle.batch.datacreate.dao.StlDataCreateMapper;
import com.joyintech.tams.settle.batch.datacreate.dto.StlDataCreateDto;
import com.joyintech.tams.settle.batch.datacreate.dto.StlDataCreateWriteDto;
import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;

/**
 * @author 陈卓
 * @date 2020/11/24
 * @see StlDataCreateService
 * @since 1.0
 */
@Service
public class StlDataCreateService {

	@Autowired
    private StlDataCreateMapper stlDataCreateMapper;
	
    /**
     * 金融产品交易临时表.交易方向是买入、卖出、正回购、逆回购的相关数据检索
     * @param param 查询参数
     * @return 金融产品交易临时表.交易方向是买入、卖出、正回购、逆回购的检索结果DTO
     */
	public List<StlDataCreateDto> selectStlDownFileData(Map<String, Object> param) {
    	return stlDataCreateMapper.selectStlDownFileData(param);
	}
    
    /**
     * 查询结算下行数据自动生成对象组合
     * @param param Map<String, Object>
     * @return 结算下行数据自动生成对象组合
     */
    public String selectStlDataCreateTargetPortfolio(Map<String, Object> param) {
    	return stlDataCreateMapper.selectStlDataCreateTargetPortfolio(param);
    }
	
    /**
     * 生成结算文件
     * @param stlDataCreateWriteDto StlDataCreateWriteDto
     * @return 
     */
	public void writeData(StlDataCreateWriteDto stlDataCreateWriteDto) {
		File dir = Paths.get(CacheUtil.getSysParam("STL_DATA_DOWN_FILE_PATH"), stlDataCreateWriteDto.getTradeDate()).toFile();
    	if (!dir.exists()) {
    		dir.mkdir();
    	}
    	if (stlDataCreateWriteDto != null) {
    		// jsmx02SzdJsmxList
    		if (stlDataCreateWriteDto.getJsmx02SzdJsmxList() != null && stlDataCreateWriteDto.getJsmx02SzdJsmxList().size() > 0) {
    			createJsmxDbfFile(stlDataCreateWriteDto.getJsmx02SzdJsmxList(), StlDataCreateConstant.JSMX02_JS405_DBF, dir);
    		}
    		// jsmx03SzdJsmxList
    		if (stlDataCreateWriteDto.getJsmx03SzdJsmxList() != null && stlDataCreateWriteDto.getJsmx03SzdJsmxList().size() > 0) {
    			createJsmxDbfFile(stlDataCreateWriteDto.getJsmx03SzdJsmxList(), StlDataCreateConstant.JSMX03_JS405_DBF, dir);
    		}
    		// sjsmx0SzdSjsmxList
    		if (stlDataCreateWriteDto.getSjsmx0SzdSjsmxList() != null && stlDataCreateWriteDto.getSjsmx0SzdSjsmxList().size() > 0) {
    			createSjsmxDbfFile(stlDataCreateWriteDto.getSjsmx0SzdSjsmxList(), StlDataCreateConstant.SJSMX0_DBF, dir);
    		}
    		// sjsmx1SzdSjsmxList
    		if (stlDataCreateWriteDto.getSjsmx1SzdSjsmxList() != null && stlDataCreateWriteDto.getSjsmx1SzdSjsmxList().size() > 0) {
    			createSjsmxDbfFile(stlDataCreateWriteDto.getSjsmx1SzdSjsmxList(), StlDataCreateConstant.SJSMX1_DBF, dir);
    		}
    		// szdSjsjgList
    		if (stlDataCreateWriteDto.getSzdSjsjgList() != null && stlDataCreateWriteDto.getSzdSjsjgList().size() > 0) {
    			createSjsjgDbfFile(stlDataCreateWriteDto.getSzdSjsjgList(), StlDataCreateConstant.SJSJG_DBF, dir);
    		}
    	}
	}
	
	/**
	 * 生成JSMX系列的DBF文件
	 * @param szdJsmxList List<SzdJsmx>
	 * @param fileName 文件名
	 * @param dir base目录
	 */
	private void createJsmxDbfFile(List<SzdJsmx> szdJsmxList, String fileName, File dir) {
		// 判断文件是否存在，存在的话删除文件重新生成
		File file = new File(dir.getPath() + File.separator + fileName);
		if (file.exists()) {
			//file.delete();
			if (!file.delete()) {
				// file delete failed; take appropriate action
			}
		}
		OutputStream fos = null;
		try {
			// 定义DBF文件字段
			DBFField[] fields = new DBFField[48];
			// 市场代码
			fields[0] = new DBFField();
			fields[0].setName("SCDM");
			fields[0].setType(DBFDataType.CHARACTER);
			fields[0].setLength(2);
			//记录类型
			fields[1] = new DBFField();
			fields[1].setName("JLLX");
			fields[1].setType(DBFDataType.CHARACTER);
			fields[1].setLength(3);
			// 交易方式
			fields[2] = new DBFField();
			fields[2].setName("JYFS");
			fields[2].setType(DBFDataType.CHARACTER);
			fields[2].setLength(3);
			// 交收方式
			fields[3] = new DBFField();
			fields[3].setName("JSFS");
			fields[3].setType(DBFDataType.CHARACTER);
			fields[3].setLength(3);
			// 业务类型
			fields[4] = new DBFField();
			fields[4].setName("YWLX");
			fields[4].setType(DBFDataType.CHARACTER);
			fields[4].setLength(3);
			// 清算标志
			fields[5] = new DBFField();
			fields[5].setName("QSBZ");
			fields[5].setType(DBFDataType.CHARACTER);
			fields[5].setLength(3);
			// 过户类型
			fields[6] = new DBFField();
			fields[6].setName("GHLX");
			fields[6].setType(DBFDataType.CHARACTER);
			fields[6].setLength(3);
			// 交收编号
			fields[7] = new DBFField();
			fields[7].setName("JSBH");
			fields[7].setType(DBFDataType.CHARACTER);
			fields[7].setLength(16);
			// 成交编号
			fields[8] = new DBFField();
			fields[8].setName("CJBH");
			fields[8].setType(DBFDataType.CHARACTER);
			fields[8].setLength(16);
			// 申请编号
			fields[9] = new DBFField();
			fields[9].setName("SQBH");
			fields[9].setType(DBFDataType.CHARACTER);
			fields[9].setLength(16);
			// 委托编号
			fields[10] = new DBFField();
			fields[10].setName("WTBH");
			fields[10].setType(DBFDataType.CHARACTER);
			fields[10].setLength(16);
			// 交易日期
			fields[11] = new DBFField();
			fields[11].setName("JYRQ");
			fields[11].setType(DBFDataType.CHARACTER);
			fields[11].setLength(8);
			// 清算日期
			fields[12] = new DBFField();
			fields[12].setName("QSRQ");
			fields[12].setType(DBFDataType.CHARACTER);
			fields[12].setLength(8);
			// 交收日期
			fields[13] = new DBFField();
			fields[13].setName("JSRQ");
			fields[13].setType(DBFDataType.CHARACTER);
			fields[13].setLength(8);
			// 其它日期
			fields[14] = new DBFField();
			fields[14].setName("QTRQ");
			fields[14].setType(DBFDataType.CHARACTER);
			fields[14].setLength(8);
			// 委托时间
			fields[15] = new DBFField();
			fields[15].setName("WTSJ");
			fields[15].setType(DBFDataType.CHARACTER);
			fields[15].setLength(6);
			// 成交时间
			fields[16] = new DBFField();
			fields[16].setName("CJSJ");
			fields[16].setType(DBFDataType.CHARACTER);
			fields[16].setLength(6);
			// 业务单元1
			fields[17] = new DBFField();
			fields[17].setName("XWH1");
			fields[17].setType(DBFDataType.CHARACTER);
			fields[17].setLength(5);
			// 业务单元2
			fields[18] = new DBFField();
			fields[18].setName("XWH2");
			fields[18].setType(DBFDataType.CHARACTER);
			fields[18].setLength(5);
			// 业务单元1所属
			fields[19] = new DBFField();
			fields[19].setName("XWHY");
			fields[19].setType(DBFDataType.CHARACTER);
			fields[19].setLength(8);
			// 结算参与人的清算编号
			fields[20] = new DBFField();
			fields[20].setName("JSHY");
			fields[20].setType(DBFDataType.CHARACTER);
			fields[20].setLength(8);
			// 托管银行的清算编号
			fields[21] = new DBFField();
			fields[21].setName("TGHY");
			fields[21].setType(DBFDataType.CHARACTER);
			fields[21].setLength(8);
			// 证券账号
			fields[22] = new DBFField();
			fields[22].setName("ZQZH");
			fields[22].setType(DBFDataType.CHARACTER);
			fields[22].setLength(10);
			// 证券代码1
			fields[23] = new DBFField();
			fields[23].setName("ZQDM1");
			fields[23].setType(DBFDataType.CHARACTER);
			fields[23].setLength(6);
			// 证券代码2
			fields[24] = new DBFField();
			fields[24].setName("ZQDM2");
			fields[24].setType(DBFDataType.CHARACTER);
			fields[24].setLength(6);
			// 证券类别
			fields[25] = new DBFField();
			fields[25].setName("ZQLB");
			fields[25].setType(DBFDataType.CHARACTER);
			fields[25].setLength(2);
			// 流通类型
			fields[26] = new DBFField();
			fields[26].setName("LTLX");
			fields[26].setType(DBFDataType.CHARACTER);
			fields[26].setLength(1);
			// 权益类别
			fields[27] = new DBFField();
			fields[27].setName("QYLB");
			fields[27].setType(DBFDataType.CHARACTER);
			fields[27].setLength(2);
			// 挂牌年份
			fields[28] = new DBFField();
			fields[28].setName("GPNF");
			fields[28].setType(DBFDataType.CHARACTER);
			fields[28].setLength(4);
			// 买卖标志
			fields[29] = new DBFField();
			fields[29].setName("MMBZ");
			fields[29].setType(DBFDataType.CHARACTER);
			fields[29].setLength(1);
			// 交收数量
			fields[30] = new DBFField();
			fields[30].setName("SL");
			fields[30].setType(DBFDataType.CHARACTER);
			fields[30].setLength(16);
			// 成交数量
			fields[31] = new DBFField();
			fields[31].setName("CJSL");
			fields[31].setType(DBFDataType.CHARACTER);
			fields[31].setLength(16);
			// 资金账号
			fields[32] = new DBFField();
			fields[32].setName("ZJZH");
			fields[32].setType(DBFDataType.CHARACTER);
			fields[32].setLength(25);
			// 币种
			fields[33] = new DBFField();
			fields[33].setName("BZ");
			fields[33].setType(DBFDataType.CHARACTER);
			fields[33].setLength(3);
			// 价格1
			fields[34] = new DBFField();
			fields[34].setName("JG1");
			fields[34].setType(DBFDataType.CHARACTER);
			fields[34].setLength(17);
			// 价格2
			fields[35] = new DBFField();
			fields[35].setName("JG2");
			fields[35].setType(DBFDataType.CHARACTER);
			fields[35].setLength(17);
			// 清算金额
			fields[36] = new DBFField();
			fields[36].setName("QSJE");
			fields[36].setType(DBFDataType.CHARACTER);
			fields[36].setLength(19);
			// 印花税
			fields[37] = new DBFField();
			fields[37].setName("YHS");
			fields[37].setType(DBFDataType.CHARACTER);
			fields[37].setLength(17);
			// 经手费
			fields[38] = new DBFField();
			fields[38].setName("JSF");
			fields[38].setType(DBFDataType.CHARACTER);
			fields[38].setLength(17);
			// 过户费
			fields[39] = new DBFField();
			fields[39].setName("GHF");
			fields[39].setType(DBFDataType.CHARACTER);
			fields[39].setLength(17);
			// 证管费
			fields[40] = new DBFField();
			fields[40].setName("ZGF");
			fields[40].setType(DBFDataType.CHARACTER);
			fields[40].setLength(17);
			// 手续费
			fields[41] = new DBFField();
			fields[41].setName("SXF");
			fields[41].setType(DBFDataType.CHARACTER);
			fields[41].setLength(17);
			// 其它金额1
			fields[42] = new DBFField();
			fields[42].setName("QTJE1");
			fields[42].setType(DBFDataType.CHARACTER);
			fields[42].setLength(19);
			// 其它金额2
			fields[43] = new DBFField();
			fields[43].setName("QTJE2");
			fields[43].setType(DBFDataType.CHARACTER);
			fields[43].setLength(19);
			// 其它金额3
			fields[44] = new DBFField();
			fields[44].setName("QTJE3");
			fields[44].setType(DBFDataType.CHARACTER);
			fields[44].setLength(19);
			// 实际收付
			fields[45] = new DBFField();
			fields[45].setName("SJSF");
			fields[45].setType(DBFDataType.CHARACTER);
			fields[45].setLength(19);
			// 结果代码
			fields[46] = new DBFField();
			fields[46].setName("JGDM");
			fields[46].setType(DBFDataType.CHARACTER);
			fields[46].setLength(4);
			// 附加说明
			fields[47] = new DBFField();
			fields[47].setName("FJSM");
			fields[47].setType(DBFDataType.CHARACTER);
			fields[47].setLength(40);
			
			fos = new FileOutputStream(dir.getPath() + File.separator + fileName);
			DBFWriter writer = new DBFWriter();
			writer.setFields(fields);
			for (SzdJsmx szdJsmx : szdJsmxList) {
				Object[] rowData = new Object[48];
				// 市场代码
				rowData[0] = StringUtil.isNotEmpty(szdJsmx.getScdm()) ? szdJsmx.getScdm() : "";
				// 记录类型
				rowData[1] = StringUtil.isNotEmpty(szdJsmx.getJllx()) ? szdJsmx.getJllx() : "";
				// 交易方式
				rowData[2] = StringUtil.isNotEmpty(szdJsmx.getJyfs()) ? szdJsmx.getJyfs() : "";
				// 交收方式
				rowData[3] = StringUtil.isNotEmpty(szdJsmx.getJsfs()) ? szdJsmx.getJsfs() : "";
				// 业务类型
				rowData[4] = StringUtil.isNotEmpty(szdJsmx.getYwlx()) ? szdJsmx.getYwlx() : "";
				// 清算标志
				rowData[5] = StringUtil.isNotEmpty(szdJsmx.getQsbz()) ? szdJsmx.getQsbz() : "";
				// 过户类型
				rowData[6] = StringUtil.isNotEmpty(szdJsmx.getGhlx()) ? szdJsmx.getGhlx() : "";
				// 交收编号
				rowData[7] = StringUtil.isNotEmpty(szdJsmx.getJsbh()) ? szdJsmx.getJsbh() : "";
				// 成交编号
				rowData[8] = StringUtil.isNotEmpty(szdJsmx.getCjbh()) ? szdJsmx.getCjbh() : "";
				// 申请编号
				rowData[9] = StringUtil.isNotEmpty(szdJsmx.getSqbh()) ? szdJsmx.getSqbh() : "";
				// 委托编号
				rowData[10] = StringUtil.isNotEmpty(szdJsmx.getWtbh()) ? szdJsmx.getWtbh() : "";
				// 交易日期
				rowData[11] = StringUtil.isNotEmpty(szdJsmx.getJyrq()) ? szdJsmx.getJyrq() : "";
				// 清算日期
				rowData[12] = StringUtil.isNotEmpty(szdJsmx.getQsrq()) ? szdJsmx.getQsrq() : "";
				// 交收日期
				rowData[13] = StringUtil.isNotEmpty(szdJsmx.getJsrq()) ? szdJsmx.getJsrq() : "";
				// 其它日期
				rowData[14] = StringUtil.isNotEmpty(szdJsmx.getQtrq()) ? szdJsmx.getQtrq() : "";
				// 委托时间
				rowData[15] = StringUtil.isNotEmpty(szdJsmx.getWtsj()) ? szdJsmx.getWtsj() : "";
				// 成交时间
				rowData[16] = StringUtil.isNotEmpty(szdJsmx.getCjsj()) ? szdJsmx.getCjsj() : "";
				// 业务单元1
				rowData[17] = StringUtil.isNotEmpty(szdJsmx.getXwh1()) ? szdJsmx.getXwh1() : "";
				// 业务单元2
				rowData[18] = StringUtil.isNotEmpty(szdJsmx.getXwh2()) ? szdJsmx.getXwh2() : "";
				// 业务单元1所属
				rowData[19] = StringUtil.isNotEmpty(szdJsmx.getXwhy()) ? szdJsmx.getXwhy() : "";
				// 结算参与人的清算编号
				rowData[20] = StringUtil.isNotEmpty(szdJsmx.getJshy()) ? szdJsmx.getJshy() : "";
				// 托管银行的清算编号
				rowData[21] = StringUtil.isNotEmpty(szdJsmx.getTghy()) ? szdJsmx.getTghy() : "";
				// 证券账号
				rowData[22] = StringUtil.isNotEmpty(szdJsmx.getZqzh()) ? szdJsmx.getZqzh() : "";
				// 证券代码1
				rowData[23] = StringUtil.isNotEmpty(szdJsmx.getZqdm1()) ? szdJsmx.getZqdm1() : "";
				// 证券代码2
				rowData[24] = StringUtil.isNotEmpty(szdJsmx.getZqdm2()) ? szdJsmx.getZqdm2() : "";
				// 证券类别
				rowData[25] = StringUtil.isNotEmpty(szdJsmx.getZqlb()) ? szdJsmx.getZqlb() : "";
				// 流通类型
				rowData[26] = StringUtil.isNotEmpty(szdJsmx.getLtlx()) ? szdJsmx.getLtlx() : "";
				// 权益类别
				rowData[27] = StringUtil.isNotEmpty(szdJsmx.getQylb()) ? szdJsmx.getQylb() : "";
				// 挂牌年份
				rowData[28] = StringUtil.isNotEmpty(szdJsmx.getGpnf()) ? szdJsmx.getGpnf() : "";
				// 买卖标志
				rowData[29] = StringUtil.isNotEmpty(szdJsmx.getMmbz()) ? szdJsmx.getMmbz() : "";
				// 交收数量
				rowData[30] = StringUtil.isNotEmpty(szdJsmx.getSl()) ? szdJsmx.getSl() : "";
				// 成交数量
				rowData[31] = StringUtil.isNotEmpty(szdJsmx.getCjsl()) ? szdJsmx.getCjsl() : "";
				// 资金账号
				rowData[32] = StringUtil.isNotEmpty(szdJsmx.getZjzh()) ? szdJsmx.getZjzh() : "";
				// 币种
				rowData[33] = StringUtil.isNotEmpty(szdJsmx.getBz()) ? szdJsmx.getBz() : "";
				// 价格1
				rowData[34] = StringUtil.isNotEmpty(szdJsmx.getJg1()) ? szdJsmx.getJg1() : "";
				// 价格2
				rowData[35] = StringUtil.isNotEmpty(szdJsmx.getJg2()) ? szdJsmx.getJg2() : "";
				// 清算金额
				rowData[36] = StringUtil.isNotEmpty(szdJsmx.getQsje()) ? szdJsmx.getQsje() : "0.00";
				// 印花税
				rowData[37] = StringUtil.isNotEmpty(szdJsmx.getYhs()) ? szdJsmx.getYhs() : "0.00";
				// 经手费
				rowData[38] = StringUtil.isNotEmpty(szdJsmx.getJsf()) ? szdJsmx.getJsf() : "0.00";
				// 过户费
				rowData[39] = StringUtil.isNotEmpty(szdJsmx.getGhf()) ? szdJsmx.getGhf() : "0.00";
				// 证管费
				rowData[40] = StringUtil.isNotEmpty(szdJsmx.getZgf()) ? szdJsmx.getZgf() : "0.00";
				// 手续费
				rowData[41] = StringUtil.isNotEmpty(szdJsmx.getSxf()) ? szdJsmx.getSxf() : "0.00";
				// 其它金额1
				rowData[42] = StringUtil.isNotEmpty(szdJsmx.getQtje1()) ? szdJsmx.getQtje1() : "0.00";
				// 其它金额2
				rowData[43] = StringUtil.isNotEmpty(szdJsmx.getQtje2()) ? szdJsmx.getQtje2() : "0.00";
				// 其它金额3
				rowData[44] = StringUtil.isNotEmpty(szdJsmx.getQtje3()) ? szdJsmx.getQtje3() : "0.00";
				// 实际收付
				rowData[45] = StringUtil.isNotEmpty(szdJsmx.getSjsf()) ? szdJsmx.getSjsf() : "0.00";
				// 结果代码
				rowData[46] = StringUtil.isNotEmpty(szdJsmx.getJgdm()) ? szdJsmx.getJgdm() : "";
				// 附加说明
				rowData[47] = StringUtil.isNotEmpty(szdJsmx.getFjsm()) ? szdJsmx.getFjsm() : "";
				writer.addRecord(rowData);
			}
			writer.write(fos);
			writer.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (fos != null) {
					fos.close();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 生成SJSMX系列的DBF文件
	 * @param szdSjsmxList List<SzdSjsmx>
	 * @param fileName 文件名
	 * @param dir base目录
	 */
	private void createSjsmxDbfFile(List<SzdSjsmx> szdSjsmxList, String fileName, File dir) {
		// 判断文件是否存在，存在的话删除文件重新生成
		File file = new File(dir.getPath() + File.separator + fileName);
		if (file.exists()) {
			//file.delete();
			if (!file.delete()) {
				// file delete failed; take appropriate action
			}
		}
		OutputStream fos = null;
		try {
			// 定义DBF文件字段
			DBFField[] fields = new DBFField[48];
			// 结算账号
			fields[0] = new DBFField();
			fields[0].setName("MXJSZH");
			fields[0].setType(DBFDataType.CHARACTER);
			fields[0].setLength(6);
			// 备付金账户
			fields[1] = new DBFField();
			fields[1].setName("MXBFZH");
			fields[1].setType(DBFDataType.CHARACTER);
			fields[1].setLength(25);
			// 数据类型
			fields[2] = new DBFField();
			fields[2].setName("MXSJLX");
			fields[2].setType(DBFDataType.CHARACTER);
			fields[2].setLength(2);
			// 业务类别
			fields[3] = new DBFField();
			fields[3].setName("MXYWLB");
			fields[3].setType(DBFDataType.CHARACTER);
			fields[3].setLength(4);
			// 证券代码
			fields[4] = new DBFField();
			fields[4].setName("MXZQDM");
			fields[4].setType(DBFDataType.CHARACTER);
			fields[4].setLength(8);
			// 交易单元
			fields[5] = new DBFField();
			fields[5].setName("MXJYDY");
			fields[5].setType(DBFDataType.CHARACTER);
			fields[5].setLength(6);
			// 托管单元
			fields[6] = new DBFField();
			fields[6].setName("MXTGDY");
			fields[6].setType(DBFDataType.CHARACTER);
			fields[6].setLength(6);
			// 证券账户号码
			fields[7] = new DBFField();
			fields[7].setName("MXZQZH");
			fields[7].setType(DBFDataType.CHARACTER);
			fields[7].setLength(20);
			// 客户订单编号/申请单号
			fields[8] = new DBFField();
			fields[8].setName("MXDDBH");
			fields[8].setType(DBFDataType.CHARACTER);
			fields[8].setLength(24);
			// 营业部代码
			fields[9] = new DBFField();
			fields[9].setName("MXYYB");
			fields[9].setType(DBFDataType.CHARACTER);
			fields[9].setLength(4);
			// 执行编号
			fields[10] = new DBFField();
			fields[10].setName("MXZXBH");
			fields[10].setType(DBFDataType.CHARACTER);
			fields[10].setLength(16);
			// 业务流水号
			fields[11] = new DBFField();
			fields[11].setName("MXYWLSH");
			fields[11].setType(DBFDataType.CHARACTER);
			fields[11].setLength(16);
			// 成交数量
			fields[12] = new DBFField();
			fields[12].setName("MXCJSL");
			fields[12].setType(DBFDataType.NUMERIC);
			fields[12].setLength(15);
			fields[12].setDecimalCount(2);
			// 清算数量
			fields[13] = new DBFField();
			fields[13].setName("MXQSSL");
			fields[13].setType(DBFDataType.NUMERIC);
			fields[13].setLength(15);
			fields[13].setDecimalCount(2);
			// 成交价格
			fields[14] = new DBFField();
			fields[14].setName("MXCJJG");
			fields[14].setType(DBFDataType.NUMERIC);
			fields[14].setLength(13);
			fields[14].setDecimalCount(4);
			// 清算价格
			fields[15] = new DBFField();
			fields[15].setName("MXQSJG");
			fields[15].setType(DBFDataType.NUMERIC);
			fields[15].setLength(18);
			fields[15].setDecimalCount(9);
			// 信用交易标识
			fields[16] = new DBFField();
			fields[16].setName("MXXYJY");
			fields[16].setType(DBFDataType.CHARACTER);
			fields[16].setLength(1);
			// 平仓标识
			fields[17] = new DBFField();
			fields[17].setName("MXPCBS");
			fields[17].setType(DBFDataType.CHARACTER);
			fields[17].setLength(1);
			// 证券类别
			fields[18] = new DBFField();
			fields[18].setName("MXZQLB");
			fields[18].setType(DBFDataType.CHARACTER);
			fields[18].setLength(2);
			// 证券子类别
			fields[19] = new DBFField();
			fields[19].setName("MXZQZL");
			fields[19].setType(DBFDataType.CHARACTER);
			fields[19].setLength(2);
			// 股份性质
			fields[20] = new DBFField();
			fields[20].setName("MXGFXZ");
			fields[20].setType(DBFDataType.CHARACTER);
			fields[20].setLength(2);
			// 交收方式
			fields[21] = new DBFField();
			fields[21].setName("MXJSFS");
			fields[21].setType(DBFDataType.CHARACTER);
			fields[21].setLength(1);
			// 货币代号
			fields[22] = new DBFField();
			fields[22].setName("MXHBDH");
			fields[22].setType(DBFDataType.CHARACTER);
			fields[22].setLength(3);
			// 清算本金
			fields[23] = new DBFField();
			fields[23].setName("MXQSBJ");
			fields[23].setType(DBFDataType.NUMERIC);
			fields[23].setLength(17);
			fields[23].setDecimalCount(2);
			// 印花税
			fields[24] = new DBFField();
			fields[24].setName("MXYHS");
			fields[24].setType(DBFDataType.NUMERIC);
			fields[24].setLength(12);
			fields[24].setDecimalCount(2);
			// 交易经手费
			fields[25] = new DBFField();
			fields[25].setName("MXJYJSF");
			fields[25].setType(DBFDataType.NUMERIC);
			fields[25].setLength(12);
			fields[25].setDecimalCount(2);
			// 监管规费
			fields[26] = new DBFField();
			fields[26].setName("MXJGGF");
			fields[26].setType(DBFDataType.NUMERIC);
			fields[26].setLength(12);
			fields[26].setDecimalCount(2);
			// 过户费
			fields[27] = new DBFField();
			fields[27].setName("MXGHF");
			fields[27].setType(DBFDataType.NUMERIC);
			fields[27].setLength(12);
			fields[27].setDecimalCount(2);
			// 结算费
			fields[28] = new DBFField();
			fields[28].setName("MXJSF");
			fields[28].setType(DBFDataType.NUMERIC);
			fields[28].setLength(12);
			fields[28].setDecimalCount(2);
			// 手续费
			fields[29] = new DBFField();
			fields[29].setName("MXSXF");
			fields[29].setType(DBFDataType.NUMERIC);
			fields[29].setLength(12);
			fields[29].setDecimalCount(2);
			// 结算参与人佣金
			fields[30] = new DBFField();
			fields[30].setName("MXQSYJ");
			fields[30].setType(DBFDataType.NUMERIC);
			fields[30].setLength(12);
			fields[30].setDecimalCount(2);
			// 其他费用
			fields[31] = new DBFField();
			fields[31].setName("MXQTFY");
			fields[31].setType(DBFDataType.NUMERIC);
			fields[31].setLength(12);
			fields[31].setDecimalCount(2);
			// 资金金额
			fields[32] = new DBFField();
			fields[32].setName("MXZJJE");
			fields[32].setType(DBFDataType.NUMERIC);
			fields[32].setLength(17);
			fields[32].setDecimalCount(2);
			// 收付净额
			fields[33] = new DBFField();
			fields[33].setName("MXSFJE");
			fields[33].setType(DBFDataType.NUMERIC);
			fields[33].setLength(18);
			fields[33].setDecimalCount(2);
			// 成交日期
			fields[34] = new DBFField();
			fields[34].setName("MXCJRQ");
			fields[34].setType(DBFDataType.CHARACTER);
			fields[34].setLength(8);
			// 清算日期
			fields[35] = new DBFField();
			fields[35].setName("MXQSRQ");
			fields[35].setType(DBFDataType.CHARACTER);
			fields[35].setLength(8);
			// 交收日期
			fields[36] = new DBFField();
			fields[36].setName("MXJSRQ");
			fields[36].setType(DBFDataType.CHARACTER);
			fields[36].setLength(8);
			// 发送日期
			fields[37] = new DBFField();
			fields[37].setName("MXFSRQ");
			fields[37].setType(DBFDataType.CHARACTER);
			fields[37].setLength(8);
			// 其它日期
			fields[38] = new DBFField();
			fields[38].setName("MXQTRQ");
			fields[38].setType(DBFDataType.CHARACTER);
			fields[38].setLength(8);
			// 市场代码
			fields[39] = new DBFField();
			fields[39].setName("MXSCDM");
			fields[39].setType(DBFDataType.CHARACTER);
			fields[39].setLength(2);
			// 交易方式
			fields[40] = new DBFField();
			fields[40].setName("MXJYFS");
			fields[40].setType(DBFDataType.CHARACTER);
			fields[40].setLength(2);
			// 证券代码2
			fields[41] = new DBFField();
			fields[41].setName("MXZQDM2");
			fields[41].setType(DBFDataType.CHARACTER);
			fields[41].setLength(8);
			// 托管单元2
			fields[42] = new DBFField();
			fields[42].setName("MXTGDY2");
			fields[42].setType(DBFDataType.CHARACTER);
			fields[42].setLength(6);
			// 订单编号2
			fields[43] = new DBFField();
			fields[43].setName("MXDDBH2");
			fields[43].setType(DBFDataType.CHARACTER);
			fields[43].setLength(16);
			// 错误代号
			fields[44] = new DBFField();
			fields[44].setName("MXCWDH");
			fields[44].setType(DBFDataType.CHARACTER);
			fields[44].setLength(4);
			// 匹配号码
			fields[45] = new DBFField();
			fields[45].setName("MXPPHM");
			fields[45].setType(DBFDataType.CHARACTER);
			fields[45].setLength(10);
			// 附加说明
			fields[46] = new DBFField();
			fields[46].setName("MXFJSM");
			fields[46].setType(DBFDataType.CHARACTER);
			fields[46].setLength(30);
			// 备用标志
			fields[47] = new DBFField();
			fields[47].setName("MXBYBZ");
			fields[47].setType(DBFDataType.CHARACTER);
			fields[47].setLength(1);
			
			fos = new FileOutputStream(dir.getPath() + File.separator + fileName);
			DBFWriter writer = new DBFWriter();
			writer.setFields(fields);
			for (SzdSjsmx szdSjsmx : szdSjsmxList) {
				Object[] rowData = new Object[48];
				// 结算账号
				rowData[0] = StringUtil.isNotEmpty(szdSjsmx.getMxjszh()) ? szdSjsmx.getMxjszh() : "";
				// 备付金账户
				rowData[1] = StringUtil.isNotEmpty(szdSjsmx.getMxbfzh()) ? szdSjsmx.getMxbfzh() : "";
				// 数据类型
				rowData[2] = StringUtil.isNotEmpty(szdSjsmx.getMxsjlx()) ? szdSjsmx.getMxsjlx() : "";
				// 业务类别
				rowData[3] = StringUtil.isNotEmpty(szdSjsmx.getMxywlb()) ? szdSjsmx.getMxywlb() : "";
				// 证券代码
				rowData[4] = StringUtil.isNotEmpty(szdSjsmx.getMxzqdm()) ? szdSjsmx.getMxzqdm() : "";
				// 交易单元
				rowData[5] = StringUtil.isNotEmpty(szdSjsmx.getMxjydy()) ? szdSjsmx.getMxjydy() : "";
				// 托管单元
				rowData[6] = StringUtil.isNotEmpty(szdSjsmx.getMxtgdy()) ? szdSjsmx.getMxtgdy() : "";
				// 证券账户号码
				rowData[7] = StringUtil.isNotEmpty(szdSjsmx.getMxzqzh()) ? szdSjsmx.getMxzqzh() : "";
				// 客户订单编号/申请单号
				rowData[8] = StringUtil.isNotEmpty(szdSjsmx.getMxddbh()) ? szdSjsmx.getMxddbh() : "";
				// 营业部代码
				rowData[9] = StringUtil.isNotEmpty(szdSjsmx.getMxyyb()) ? szdSjsmx.getMxyyb() : "";
				// 执行编号
				rowData[10] = StringUtil.isNotEmpty(szdSjsmx.getMxzxbh()) ? szdSjsmx.getMxzxbh() : "";
				// 业务流水号
				rowData[11] = StringUtil.isNotEmpty(szdSjsmx.getMxywlsh()) ? szdSjsmx.getMxywlsh() : "";
				// 成交数量
				rowData[12] = szdSjsmx.getMxcjsl();
				// 清算数量
				rowData[13] = szdSjsmx.getMxqssl();
				// 成交价格
				rowData[14] = szdSjsmx.getMxcjjg();
				// 清算价格
				rowData[15] = szdSjsmx.getMxqsjg();
				// 信用交易标识
				rowData[16] = StringUtil.isNotEmpty(szdSjsmx.getMxxyjy()) ? szdSjsmx.getMxxyjy() : "";
				// 平仓标识
				rowData[17] = StringUtil.isNotEmpty(szdSjsmx.getMxpcbs()) ? szdSjsmx.getMxpcbs() : "";
				// 证券类别
				rowData[18] = StringUtil.isNotEmpty(szdSjsmx.getMxzqlb()) ? szdSjsmx.getMxzqlb() : "";
				// 证券子类别
				rowData[19] = StringUtil.isNotEmpty(szdSjsmx.getMxzqzl()) ? szdSjsmx.getMxzqzl() : "";
				// 股份性质
				rowData[20] = StringUtil.isNotEmpty(szdSjsmx.getMxgfxz()) ? szdSjsmx.getMxgfxz() : "";
				// 交收方式
				rowData[21] = StringUtil.isNotEmpty(szdSjsmx.getMxjsfs()) ? szdSjsmx.getMxjsfs() : "";
				// 货币代号
				rowData[22] = StringUtil.isNotEmpty(szdSjsmx.getMxhbdh()) ? szdSjsmx.getMxhbdh() : "";
				// 清算本金
				rowData[23] = szdSjsmx.getMxqsbj();
				// 印花税
				rowData[24] = szdSjsmx.getMxyhs() == null ? new BigDecimal("0.00") : szdSjsmx.getMxyhs();
				// 交易经手费
				rowData[25] = szdSjsmx.getMxjyjsf() == null ? new BigDecimal("0.00") : szdSjsmx.getMxjyjsf();
				// 监管规费
				rowData[26] = szdSjsmx.getMxjggf() == null ? new BigDecimal("0.00") : szdSjsmx.getMxjggf();
				// 过户费
				rowData[27] = szdSjsmx.getMxghf() == null ? new BigDecimal("0.00") : szdSjsmx.getMxghf();
				// 结算费
				rowData[28] = szdSjsmx.getMxjsf() == null ? new BigDecimal("0.00") : szdSjsmx.getMxjsf();
				// 手续费
				rowData[29] = szdSjsmx.getMxsxf() == null ? new BigDecimal("0.00") : szdSjsmx.getMxsxf();
				// 结算参与人佣金
				rowData[30] = szdSjsmx.getMxqsyj() == null ? new BigDecimal("0.00") : szdSjsmx.getMxqsyj();
				// 其他费用
				rowData[31] = szdSjsmx.getMxqtfy() == null ? new BigDecimal("0.00") : szdSjsmx.getMxqtfy();
				// 资金金额
				rowData[32] = szdSjsmx.getMxzjje() == null ? new BigDecimal("0.00") : szdSjsmx.getMxzjje();
				// 收付净额
				rowData[33] = szdSjsmx.getMxsfje() == null ? new BigDecimal("0.00") : szdSjsmx.getMxsfje();
				// 成交日期
				rowData[34] = StringUtil.isNotEmpty(szdSjsmx.getMxcjrq()) ? szdSjsmx.getMxcjrq() : "";
				// 清算日期
				rowData[35] = StringUtil.isNotEmpty(szdSjsmx.getMxqsrq()) ? szdSjsmx.getMxqsrq() : "";
				// 交收日期
				rowData[36] = StringUtil.isNotEmpty(szdSjsmx.getMxjsrq()) ? szdSjsmx.getMxjsrq() : "";
				// 发送日期
				rowData[37] = StringUtil.isNotEmpty(szdSjsmx.getMxfsrq()) ? szdSjsmx.getMxfsrq() : "";
				// 其它日期
				rowData[38] = StringUtil.isNotEmpty(szdSjsmx.getMxqtrq()) ? szdSjsmx.getMxqtrq() : "";
				// 市场代码
				rowData[39] = StringUtil.isNotEmpty(szdSjsmx.getMxscdm()) ? szdSjsmx.getMxscdm() : "";
				// 交易方式
				rowData[40] = StringUtil.isNotEmpty(szdSjsmx.getMxjyfs()) ? szdSjsmx.getMxjyfs() : "";
				// 证券代码2
				rowData[41] = StringUtil.isNotEmpty(szdSjsmx.getMxzqdm2()) ? szdSjsmx.getMxzqdm2() : "";
				// 托管单元2
				rowData[42] = StringUtil.isNotEmpty(szdSjsmx.getMxtgdy2()) ? szdSjsmx.getMxtgdy2() : "";
				// 订单编号2
				rowData[43] = StringUtil.isNotEmpty(szdSjsmx.getMxddbh2()) ? szdSjsmx.getMxddbh2() : "";
				// 错误代号
				rowData[44] = StringUtil.isNotEmpty(szdSjsmx.getMxcwdh()) ? szdSjsmx.getMxcwdh() : "";
				// 匹配号码
				rowData[45] = StringUtil.isNotEmpty(szdSjsmx.getMxpphm()) ? szdSjsmx.getMxpphm() : "";
				// 附加说明
				rowData[46] = StringUtil.isNotEmpty(szdSjsmx.getMxfjsm()) ? szdSjsmx.getMxfjsm() : "";
				// 备用标志
				rowData[47] = StringUtil.isNotEmpty(szdSjsmx.getMxbybz()) ? szdSjsmx.getMxbybz() : "";
				writer.addRecord(rowData);
			}
			writer.write(fos);
			writer.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (fos != null) {
					fos.close();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 生成SJSJG系列的DBF文件
	 * @param szdSjsjgList List<SzdSjsjg>
	 * @param fileName 文件名
	 * @param dir base目录
	 */
	private void createSjsjgDbfFile(List<SzdSjsjg> szdSjsjgList, String fileName, File dir) {
		// 判断文件是否存在，存在的话删除文件重新生成
		File file = new File(dir.getPath() + File.separator + fileName);
		if (file.exists()) {
			//file.delete();
			if (!file.delete()) {
				// file delete failed; take appropriate action
			}
		}
		OutputStream fos = null;
		try {
			// 定义DBF文件字段
			DBFField[] fields = new DBFField[50];
			// 结算账号
			fields[0] = new DBFField();
			fields[0].setName("JGJSZH");
			fields[0].setType(DBFDataType.CHARACTER);
			fields[0].setLength(6);
			// 备付金账户
			fields[1] = new DBFField();
			fields[1].setName("JGBFZH");
			fields[1].setType(DBFDataType.CHARACTER);
			fields[1].setLength(25);
			// 数据类型
			fields[2] = new DBFField();
			fields[2].setName("JGSJLX");
			fields[2].setType(DBFDataType.CHARACTER);
			fields[2].setLength(2);
			// 业务类别
			fields[3] = new DBFField();
			fields[3].setName("JGYWLB");
			fields[3].setType(DBFDataType.CHARACTER);
			fields[3].setLength(4);
			// 证券代码
			fields[4] = new DBFField();
			fields[4].setName("JGZQDM");
			fields[4].setType(DBFDataType.CHARACTER);
			fields[4].setLength(8);
			// 交易单元
			fields[5] = new DBFField();
			fields[5].setName("JGJYDY");
			fields[5].setType(DBFDataType.CHARACTER);
			fields[5].setLength(6);
			// 托管单元
			fields[6] = new DBFField();
			fields[6].setName("JGTGDY");
			fields[6].setType(DBFDataType.CHARACTER);
			fields[6].setLength(6);
			// 证券账户号码
			fields[7] = new DBFField();
			fields[7].setName("JGZQZH");
			fields[7].setType(DBFDataType.CHARACTER);
			fields[7].setLength(20);
			// 客户订单编号/申请单号
			fields[8] = new DBFField();
			fields[8].setName("JGDDBH");
			fields[8].setType(DBFDataType.CHARACTER);
			fields[8].setLength(24);
			// 营业部代码
			fields[9] = new DBFField();
			fields[9].setName("JGYYB");
			fields[9].setType(DBFDataType.CHARACTER);
			fields[9].setLength(4);
			// 执行编号
			fields[10] = new DBFField();
			fields[10].setName("JGZXBH");
			fields[10].setType(DBFDataType.CHARACTER);
			fields[10].setLength(16);
			// 业务流水号
			fields[11] = new DBFField();
			fields[11].setName("JGYWLSH");
			fields[11].setType(DBFDataType.CHARACTER);
			fields[11].setLength(16);
			// 成交数量
			fields[12] = new DBFField();
			fields[12].setName("JGCJSL");
			fields[12].setType(DBFDataType.NUMERIC);
			fields[12].setLength(15);
			fields[12].setDecimalCount(2);
			// 清算数量
			fields[13] = new DBFField();
			fields[13].setName("JGQSSL");
			fields[13].setType(DBFDataType.NUMERIC);
			fields[13].setLength(15);
			fields[13].setDecimalCount(2);
			// 交收数量
			fields[14] = new DBFField();
			fields[14].setName("JGJSSL");
			fields[14].setType(DBFDataType.NUMERIC);
			fields[14].setLength(15);
			fields[14].setDecimalCount(2);
			// 成交价格
			fields[15] = new DBFField();
			fields[15].setName("JGCJJG");
			fields[15].setType(DBFDataType.NUMERIC);
			fields[15].setLength(13);
			fields[15].setDecimalCount(4);
			// 清算价格
			fields[16] = new DBFField();
			fields[16].setName("JGQSJG");
			fields[16].setType(DBFDataType.NUMERIC);
			fields[16].setLength(18);
			fields[16].setDecimalCount(9);
			// 信用交易标识
			fields[17] = new DBFField();
			fields[17].setName("JGXYJY");
			fields[17].setType(DBFDataType.CHARACTER);
			fields[17].setLength(1);
			// 平仓标识
			fields[18] = new DBFField();
			fields[18].setName("JGPCBS");
			fields[18].setType(DBFDataType.CHARACTER);
			fields[18].setLength(1);
			// 证券类别
			fields[19] = new DBFField();
			fields[19].setName("JGZQLB");
			fields[19].setType(DBFDataType.CHARACTER);
			fields[19].setLength(2);
			// 证券子类别
			fields[20] = new DBFField();
			fields[20].setName("JGZQZL");
			fields[20].setType(DBFDataType.CHARACTER);
			fields[20].setLength(2);
			// 股份性质
			fields[21] = new DBFField();
			fields[21].setName("JGGFXZ");
			fields[21].setType(DBFDataType.CHARACTER);
			fields[21].setLength(2);
			// 流通类型
			fields[22] = new DBFField();
			fields[22].setName("JGLTLX");
			fields[22].setType(DBFDataType.CHARACTER);
			fields[22].setLength(1);
			// 交收方式
			fields[23] = new DBFField();
			fields[23].setName("JGJSFS");
			fields[23].setType(DBFDataType.CHARACTER);
			fields[23].setLength(1);
			// 货币代号
			fields[24] = new DBFField();
			fields[24].setName("JGHBDH");
			fields[24].setType(DBFDataType.CHARACTER);
			fields[24].setLength(3);
			// 清算本金
			fields[25] = new DBFField();
			fields[25].setName("JGQSBJ");
			fields[25].setType(DBFDataType.NUMERIC);
			fields[25].setLength(17);
			fields[25].setDecimalCount(2);
			// 印花税
			fields[26] = new DBFField();
			fields[26].setName("JGYHS");
			fields[26].setType(DBFDataType.NUMERIC);
			fields[26].setLength(12);
			fields[26].setDecimalCount(2);
			// 交易经手费
			fields[27] = new DBFField();
			fields[27].setName("JGJYJSF");
			fields[27].setType(DBFDataType.NUMERIC);
			fields[27].setLength(12);
			fields[27].setDecimalCount(2);
			// 监管规费
			fields[28] = new DBFField();
			fields[28].setName("JGJGGF");
			fields[28].setType(DBFDataType.NUMERIC);
			fields[28].setLength(12);
			fields[28].setDecimalCount(2);
			// 过户费
			fields[29] = new DBFField();
			fields[29].setName("JGGHF");
			fields[29].setType(DBFDataType.NUMERIC);
			fields[29].setLength(12);
			fields[29].setDecimalCount(2);
			// 结算费
			fields[30] = new DBFField();
			fields[30].setName("JGJSF");
			fields[30].setType(DBFDataType.NUMERIC);
			fields[30].setLength(12);
			fields[30].setDecimalCount(2);
			// 手续费
			fields[31] = new DBFField();
			fields[31].setName("JGSXF");
			fields[31].setType(DBFDataType.NUMERIC);
			fields[31].setLength(12);
			fields[31].setDecimalCount(2);
			// 结算参与人佣金
			fields[32] = new DBFField();
			fields[32].setName("JGQSYJ");
			fields[32].setType(DBFDataType.NUMERIC);
			fields[32].setLength(12);
			fields[32].setDecimalCount(2);
			// 其他费用
			fields[33] = new DBFField();
			fields[33].setName("JGQTFY");
			fields[33].setType(DBFDataType.NUMERIC);
			fields[33].setLength(12);
			fields[33].setDecimalCount(2);
			// 资金金额
			fields[34] = new DBFField();
			fields[34].setName("JGZJJE");
			fields[34].setType(DBFDataType.NUMERIC);
			fields[34].setLength(17);
			fields[34].setDecimalCount(2);
			// 收付净额
			fields[35] = new DBFField();
			fields[35].setName("JGSFJE");
			fields[35].setType(DBFDataType.NUMERIC);
			fields[35].setLength(18);
			fields[35].setDecimalCount(2);
			// 交收标志
			fields[36] = new DBFField();
			fields[36].setName("JGJSBZ");
			fields[36].setType(DBFDataType.CHARACTER);
			fields[36].setLength(1);
			// 摘要代号
			fields[37] = new DBFField();
			fields[37].setName("JGZYDH");
			fields[37].setType(DBFDataType.CHARACTER);
			fields[37].setLength(4);
			// 成交日期
			fields[38] = new DBFField();
			fields[38].setName("JGCJRQ");
			fields[38].setType(DBFDataType.CHARACTER);
			fields[38].setLength(8);
			// 清算日期
			fields[39] = new DBFField();
			fields[39].setName("JGQSRQ");
			fields[39].setType(DBFDataType.CHARACTER);
			fields[39].setLength(8);
			// 交收日期
			fields[40] = new DBFField();
			fields[40].setName("JGJSRQ");
			fields[40].setType(DBFDataType.CHARACTER);
			fields[40].setLength(8);
			// 发送日期
			fields[41] = new DBFField();
			fields[41].setName("JGFSRQ");
			fields[41].setType(DBFDataType.CHARACTER);
			fields[41].setLength(8);
			// 其它日期
			fields[42] = new DBFField();
			fields[42].setName("JGQTRQ");
			fields[42].setType(DBFDataType.CHARACTER);
			fields[42].setLength(8);
			// 市场代码
			fields[43] = new DBFField();
			fields[43].setName("JGSCDM");
			fields[43].setType(DBFDataType.CHARACTER);
			fields[43].setLength(2);
			// 交易方式
			fields[44] = new DBFField();
			fields[44].setName("JGJYFS");
			fields[44].setType(DBFDataType.CHARACTER);
			fields[44].setLength(2);
			// 证券代码2
			fields[45] = new DBFField();
			fields[45].setName("JGZQDM2");
			fields[45].setType(DBFDataType.CHARACTER);
			fields[45].setLength(8);
			// 托管单元2
			fields[46] = new DBFField();
			fields[46].setName("JGTGDY2");
			fields[46].setType(DBFDataType.CHARACTER);
			fields[46].setLength(6);
			// 订单编号2
			fields[47] = new DBFField();
			fields[47].setName("JGDDBH2");
			fields[47].setType(DBFDataType.CHARACTER);
			fields[47].setLength(16);
			// 附加说明
			fields[48] = new DBFField();
			fields[48].setName("JGFJSM");
			fields[48].setType(DBFDataType.CHARACTER);
			fields[48].setLength(30);
			// 备用标志
			fields[49] = new DBFField();
			fields[49].setName("JGBYBZ");
			fields[49].setType(DBFDataType.CHARACTER);
			fields[49].setLength(1);
			
			fos = new FileOutputStream(dir.getPath() + File.separator + fileName);
			DBFWriter writer = new DBFWriter();
			writer.setFields(fields);
			for (SzdSjsjg szdSjsjg : szdSjsjgList) {
				Object[] rowData = new Object[50];
				// 结算账号
				rowData[0] = StringUtil.isNotEmpty(szdSjsjg.getJgjszh()) ? szdSjsjg.getJgjszh() : "";
				// 备付金账户
				rowData[1] = StringUtil.isNotEmpty(szdSjsjg.getJgbfzh()) ? szdSjsjg.getJgbfzh() : "";
				// 数据类型
				rowData[2] = StringUtil.isNotEmpty(szdSjsjg.getJgsjlx()) ? szdSjsjg.getJgsjlx() : "";
				// 业务类别
				rowData[3] = StringUtil.isNotEmpty(szdSjsjg.getJgywlb()) ? szdSjsjg.getJgywlb() : "";
				// 证券代码
				rowData[4] = StringUtil.isNotEmpty(szdSjsjg.getJgzqdm()) ? szdSjsjg.getJgzqdm() : "";
				// 交易单元
				rowData[5] = StringUtil.isNotEmpty(szdSjsjg.getJgjydy()) ? szdSjsjg.getJgjydy() : "";
				// 托管单元
				rowData[6] = StringUtil.isNotEmpty(szdSjsjg.getJgtgdy()) ? szdSjsjg.getJgtgdy() : "";
				// 证券账户号码
				rowData[7] = StringUtil.isNotEmpty(szdSjsjg.getJgzqzh()) ? szdSjsjg.getJgzqzh() : "";
				// 客户订单编号/申请单号
				rowData[8] = StringUtil.isNotEmpty(szdSjsjg.getJgddbh()) ? szdSjsjg.getJgddbh() : "";
				// 营业部代码
				rowData[9] = StringUtil.isNotEmpty(szdSjsjg.getJgyyb()) ? szdSjsjg.getJgyyb() : "";
				// 执行编号
				rowData[10] = StringUtil.isNotEmpty(szdSjsjg.getJgzxbh()) ? szdSjsjg.getJgzxbh() : "";
				// 业务流水号
				rowData[11] = StringUtil.isNotEmpty(szdSjsjg.getJgywlsh()) ? szdSjsjg.getJgywlsh() : "";
				// 成交数量
				rowData[12] = szdSjsjg.getJgcjsl();
				// 清算数量
				rowData[13] = szdSjsjg.getJgqssl();
				// 交收数量
				rowData[14] = szdSjsjg.getJgjssl();
				// 成交价格
				rowData[15] = szdSjsjg.getJgcjjg();
				// 清算价格
				rowData[16] = szdSjsjg.getJgqsjg();
				// 信用交易标识
				rowData[17] = StringUtil.isNotEmpty(szdSjsjg.getJgxyjy()) ? szdSjsjg.getJgxyjy() : "";
				// 平仓标识
				rowData[18] = StringUtil.isNotEmpty(szdSjsjg.getJgpcbs()) ? szdSjsjg.getJgpcbs() : "";
				// 证券类别
				rowData[19] = StringUtil.isNotEmpty(szdSjsjg.getJgzqlb()) ? szdSjsjg.getJgzqlb() : "";
				// 证券子类别
				rowData[20] = StringUtil.isNotEmpty(szdSjsjg.getJgzqzl()) ? szdSjsjg.getJgzqzl() : "";
				// 股份性质
				rowData[21] = StringUtil.isNotEmpty(szdSjsjg.getJggfxz()) ? szdSjsjg.getJggfxz() : "";
				// 流通类型
				rowData[22] = StringUtil.isNotEmpty(szdSjsjg.getJgltlx()) ? szdSjsjg.getJgltlx() : "";
				// 交收方式
				rowData[23] = StringUtil.isNotEmpty(szdSjsjg.getJgjsfs()) ? szdSjsjg.getJgjsfs() : "";
				// 货币代号
				rowData[24] = StringUtil.isNotEmpty(szdSjsjg.getJghbdh()) ? szdSjsjg.getJghbdh() : "";
				// 清算本金
				rowData[25] = szdSjsjg.getJgqsbj();
				// 印花税
				rowData[26] = szdSjsjg.getJgyhs() == null ? new BigDecimal("0.00") : szdSjsjg.getJgyhs();
				// 交易经手费
				rowData[27] = szdSjsjg.getJgjyjsf() == null ? new BigDecimal("0.00") : szdSjsjg.getJgjyjsf();
				// 监管规费
				rowData[28] = szdSjsjg.getJgjggf() == null ? new BigDecimal("0.00") : szdSjsjg.getJgjggf();
				// 过户费
				rowData[29] = szdSjsjg.getJgghf() == null ? new BigDecimal("0.00") : szdSjsjg.getJgghf();
				// 结算费
				rowData[30] = szdSjsjg.getJgjsf() == null ? new BigDecimal("0.00") : szdSjsjg.getJgjsf();
				// 手续费
				rowData[31] = szdSjsjg.getJgsxf() == null ? new BigDecimal("0.00") : szdSjsjg.getJgsxf();
				// 结算参与人佣金
				rowData[32] = szdSjsjg.getJgqsyj() == null ? new BigDecimal("0.00") : szdSjsjg.getJgqsyj();
				// 其他费用
				rowData[33] = szdSjsjg.getJgqtfy() == null ? new BigDecimal("0.00") : szdSjsjg.getJgqtfy();
				// 资金金额
				rowData[34] = szdSjsjg.getJgzjje() == null ? new BigDecimal("0.00") : szdSjsjg.getJgzjje();
				// 收付净额
				rowData[35] = szdSjsjg.getJgsfje() == null ? new BigDecimal("0.00") : szdSjsjg.getJgsfje();
				// 交收标志
				rowData[36] = StringUtil.isNotEmpty(szdSjsjg.getJgjsbz()) ? szdSjsjg.getJgjsbz() : "";
				// 摘要代号
				rowData[37] = StringUtil.isNotEmpty(szdSjsjg.getJgzydh()) ? szdSjsjg.getJgzydh() : "";
				// 成交日期
				rowData[38] = StringUtil.isNotEmpty(szdSjsjg.getJgcjrq()) ? szdSjsjg.getJgcjrq() : "";
				// 清算日期
				rowData[39] = StringUtil.isNotEmpty(szdSjsjg.getJgqsrq()) ? szdSjsjg.getJgqsrq() : "";
				// 交收日期
				rowData[40] = StringUtil.isNotEmpty(szdSjsjg.getJgjsrq()) ? szdSjsjg.getJgjsrq() : "";
				// 发送日期
				rowData[41] = StringUtil.isNotEmpty(szdSjsjg.getJgfsrq()) ? szdSjsjg.getJgfsrq() : "";
				// 其它日期
				rowData[42] = StringUtil.isNotEmpty(szdSjsjg.getJgqtrq()) ? szdSjsjg.getJgqtrq() : "";
				// 市场代码
				rowData[43] = StringUtil.isNotEmpty(szdSjsjg.getJgscdm()) ? szdSjsjg.getJgscdm() : "";
				// 交易方式
				rowData[44] = StringUtil.isNotEmpty(szdSjsjg.getJgjyfs()) ? szdSjsjg.getJgjyfs() : "";
				// 证券代码2
				rowData[45] = StringUtil.isNotEmpty(szdSjsjg.getJgzqdm2()) ? szdSjsjg.getJgzqdm2() : "";
				// 托管单元2
				rowData[46] = StringUtil.isNotEmpty(szdSjsjg.getJgtgdy2()) ? szdSjsjg.getJgtgdy2() : "";
				// 订单编号2
				rowData[47] = StringUtil.isNotEmpty(szdSjsjg.getJgddbh2()) ? szdSjsjg.getJgddbh2() : "";
				// 附加说明
				rowData[48] = StringUtil.isNotEmpty(szdSjsjg.getJgfjsm()) ? szdSjsjg.getJgfjsm() : "";
				// 备用标志
				rowData[49] = StringUtil.isNotEmpty(szdSjsjg.getJgbybz()) ? szdSjsjg.getJgbybz() : "";
				writer.addRecord(rowData);
			}
			writer.write(fos);
			writer.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (fos != null) {
					fos.close();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
