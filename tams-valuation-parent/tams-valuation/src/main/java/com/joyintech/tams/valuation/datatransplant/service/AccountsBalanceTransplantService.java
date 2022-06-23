package com.joyintech.tams.valuation.datatransplant.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.tams.valuation.common.batch.finproduct.entity.SubjectBalance;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.valuation.datatransplant.dao.AccountsBalanceTransplantMapper;
import com.joyintech.tams.valuation.datatransplant.dto.AccountsBalanceTransplantDto;
import com.joyintech.tams.valuation.datatransplant.dto.DataBooksetInfoDto;
import com.joyintech.tams.valuation.datatransplant.dto.TemplateCheckDto;
import com.joyintech.tams.common.base.dao.BokBalanceMapper;
import com.joyintech.tams.common.base.dao.BokBussDetsubMapper;
import com.joyintech.tams.common.base.dao.BokDetailIdMapper;
import com.joyintech.tams.common.base.entity.BokBalance;
import com.joyintech.tams.common.base.entity.BokBalanceKey;
import com.joyintech.tams.common.base.entity.BokBookdetailAll;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrent;
import com.joyintech.tams.common.base.entity.BokBussDetsub;
import com.joyintech.tams.common.base.entity.BokBussDetsubKey;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokDetailIdKey;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.common.base.entity.ValSubjectTableid;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import com.joyintech.tams.valuation.common.service.DataTransplantCommonService;
import com.joyintech.tams.valuation.common.utils.DBUtil;

@Service
@Transactional(rollbackFor = Exception.class)
public class AccountsBalanceTransplantService extends BaseService<SysBatchTask> {

	@Autowired
	private DataTransplantCommonService dataTransplantCommonService;

	@Autowired
	private BokDetailIdMapper bokDetailIdMapper;

	@Autowired
	private BokBussDetsubMapper bokBussDetsubMapper;

	@Autowired
	private DBUtil dBUtil;

	@Autowired
	private AccountsBalanceTransplantMapper accountsBalanceTransplantMapper;

	@Autowired
	private BookAccountCommonService bookAccountCommonService;

	@Autowired
	private BokBalanceMapper bokBalanceMapper;

	@Value("${tams.properties.maxSqlNum:500}")
	private int maxSqlNum;

	@SuppressWarnings("unchecked")
	public List<TemplateCheckDto> execute(List<List<String>> sheetDataList, String modelType) {
		List<AccountsBalanceTransplantDto> listAccountsBalanceTransplantDto = new ArrayList<AccountsBalanceTransplantDto>();
		// 数据转换
		// if (DictCodeListExt1.数据移植模版类型_账务余额模版.getValue().equals(modelType)) {
		// listAccountsBalanceTransplantDto = convertToDto(sheetDataList,
		// modelType);
		// }
		// else {
		listAccountsBalanceTransplantDto = convertToDtoOway(sheetDataList, modelType);
		// }
		// 数据校验
		// 存储校验信息
		List<TemplateCheckDto> checkList = new ArrayList<>();
		// 类型
		String typeError = "错误";
		// String typeWarn = "警告";
		// 错误条数
		int number = 0;
		// 账务余额模板校验
		Map<String, Object> map = checkTransplantTemple(modelType, listAccountsBalanceTransplantDto, checkList,
				typeError, number);
		checkList = (List<TemplateCheckDto>) map.get("checkList");
		number = (int) map.get("number");

		if (number == 0) {

			// 账务明细代码表集合
			Map<String, BokDetailId> mapBokDetailId = new HashMap<String, BokDetailId>();
			// 业务明细科目表集合
			Map<String, BokBussDetsub> mapBokBussDetsub = new HashMap<String, BokBussDetsub>();
			// 账务明细当前表集合
			List<BokBookdetailCurrent> listBokBookdetailCurrent = new ArrayList<BokBookdetailCurrent>();
			// 账务明细历史表集合
			List<BokBookdetailAll> listBokBookdetailAll = new ArrayList<BokBookdetailAll>();
			// 科目余额集合
			List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();

			// 取全量配置表
			List<ValSubjectTableid> tableIds = restTemplate.getForList(
					ServiceApplication.VALUATION_SERVICE_NAME+"/DataTransplant001/getTableIds", ValSubjectTableid.class);
					
			// 数据循环处理
			for (AccountsBalanceTransplantDto dto : listAccountsBalanceTransplantDto) {
				// 0.0 公用数据获取
				// 0.1 四级科目信息获取
				Map<String, String> detailSubjectNoInfo = dataTransplantCommonService.createDetailSubjectNoInfo(
						dto.getBooksetId(), dto.getBusiId(), dto.getBusiDetailType(), dto.getBranch());
				// 定义:变量-四级科目号
				String setailSubjectNo = detailSubjectNoInfo.get("DETAIL_SUBJECT_NO");
				// 定义:变量-四级科目号名称
//				String setailSubjectName = detailSubjectNoInfo.get("DETAIL_SUBJECT_NAME");
				// 定义:变量-四级科目尾号
				String detailSubjectNoTail = detailSubjectNoInfo.get("DETAIL_SUBJECT_NO_TAIL");
				// 定义:变量-四级科目尾号名称
				String detailSubnameTail = detailSubjectNoInfo.get("DETAIL_SUBNAME_TAIL");

				// 1.0 【账务明细代码表（BOK_DETAIL_ID）】数据移植
				// 调用数据移植-共通方法：获取账务明细代码
				String bokDetailId = dataTransplantCommonService.getBookDetailId(dto.getBusiDetailType(),
						dto.getBusiId(), dto.getBranch(), dto.getInvAim(),dto.getoCcy());
				String bokDetailIdKey = dto.getBooksetId() + bokDetailId;
				if (mapBokDetailId.get(bokDetailIdKey) == null) {
					// 没有重复数据则封装数据
					BokDetailId bokDetailIdDto = createBokDetailId(dto, detailSubjectNoTail, detailSubnameTail,
							bokDetailId);
					if (bokDetailIdDto != null) {
						mapBokDetailId.put(bokDetailIdKey, bokDetailIdDto);
					}
				}

				// 2.0 【业务明细科目（BOK_BUSS_DETSUB）】数据移植
				/** add by wujie on 20181212 start **/
				BokSubject bokSubject = dataTransplantCommonService.findSubjectInfo(dto.getBooksetId(),
						dto.getSubjectNo());
				// 科目有对应的四级科目号
				if (bokSubject != null && DictCodeList.是否_是.getValue().equals(bokSubject.getGenDetsubFlag())) {
					// 四级科目号
					String detailSubjectNo = dto.getSubjectNo()
							+ (detailSubjectNoTail == null ? "" : detailSubjectNoTail);
					// 四级科目名称
					String detailSubjectName = bokSubject.getSubjectName()
							+ (detailSubnameTail == null ? "" : detailSubnameTail);

					String bokBussDetsubKey = dto.getBooksetId() + detailSubjectNo;
					if (mapBokBussDetsub.get(bokBussDetsubKey) == null) {
						// 没有重复数据则封装数据
						BokBussDetsub bokBussDetsubDto = createBokBussDetsub(dto, detailSubjectNo, detailSubjectName,
								detailSubjectNoTail, detailSubnameTail);
						if (bokBussDetsubDto != null) {
							mapBokBussDetsub.put(bokBussDetsubKey, bokBussDetsubDto);
						}
					}
				}
				/** add by wujie on 201812112 end **/

				/**
				 * delete by wujie on 20181212 // 四级科目号不为空 if
				 * (StringUtils.isNotEmpty(setailSubjectNo)) { String
				 * bokBussDetsubKey = dto.getBooksetId() + setailSubjectNo; if
				 * (mapBokBussDetsub.get(bokBussDetsubKey) == null) { //
				 * 没有重复数据则封装数据 BokBussDetsub bokBussDetsubDto =
				 * createBokBussDetsub(dto, setailSubjectNo, setailSubjectName,
				 * detailSubjectNoTail, detailSubnameTail); if (bokBussDetsubDto
				 * != null) { mapBokBussDetsub.put(bokBussDetsubKey,
				 * bokBussDetsubDto); } } }
				 **/

				// 3.0 【账务明细(当前表)(BOK_BOOKDETAIL_CURRENT)】数据移植
				listBokBookdetailCurrent.add(createBokBookdetailCurrent(dto, setailSubjectNo, modelType, tableIds));

				// 4.0 【账务明细(历史表)（BOK_BOOKDETAIL_ALL）】数据移植
				listBokBookdetailAll.add(createBokBookdetailAll(dto, setailSubjectNo, modelType, tableIds));

				// 5.0 【科目余额（BOK_BALANCE）】数据移植
				listSubjectBalance.add(createSubjectBalance(dto, detailSubjectNoTail));

			}
			// 余额表数据向上汇总处理
			List<BokBalance> listBokBalance = null;
			if (listSubjectBalance.size() > 0) {
				listBokBalance = quaryBokBalance(listSubjectBalance, checkList);
			}

			if (checkList != null && checkList.size() > 0) {
				return checkList;
			}
			// 账务明细代码表数据入库
			insertBokDetailId(mapBokDetailId);

			// 业务明细科目表数据入库
			insertBokBussDetsub(mapBokBussDetsub);

			// 账务明细当前表数据入库
			insertBokBookdetailCurrent(listBokBookdetailCurrent);

			// 账务明细历史表数据入库
			insertBokBookdetailAll(listBokBookdetailAll);

			// 科目余额表数据入库
			insertBokBalance(listBokBalance);
		}

		return checkList;
	}

	/**
	 * 将excel中的数据转换为实体类(在途债券)
	 * 
	 * @param sheetDataList
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<AccountsBalanceTransplantDto> convertToDtoOway(List<List<String>> sheetDataList, String modelType) {
		// 定义返回实体类
		List<AccountsBalanceTransplantDto> dtoList = new ArrayList<AccountsBalanceTransplantDto>();

		// 循环进行数据处理
		for (List<String> row : sheetDataList) {
			// 定义实体类
			AccountsBalanceTransplantDto dto = new AccountsBalanceTransplantDto();
			// 账套代码
			if (StringUtils.isNotEmpty(row.get(0))) {
				dto.setBooksetId(row.get(0).trim());
			}

			// 业务明细代码
			if (StringUtils.isNotEmpty(row.get(1))) {
				dto.setBusiId(row.get(1).trim());
			}

			// 业务明细类型
			if (StringUtils.isNotEmpty(row.get(2))) {
				dto.setBusiDetailType(row.get(2).trim().split(GlobalConstant.分号)[0]);
			}

			// 日期
			if (StringUtils.isNotEmpty(row.get(3))) {
				dto.setBalanceDate(row.get(3) == null ? null : DateUtil.parse(row.get(3).trim()));
			}

			// 投资目的
			if (StringUtils.isNotEmpty(row.get(4))) {
				dto.setInvAim(row.get(4).trim().split(GlobalConstant.分号)[0]);
			}

			// 场景代码
			if (StringUtils.isNotEmpty(row.get(5))) {
				dto.setTableId(row.get(5).trim().split(GlobalConstant.分号)[0]);
			}

			// 交易编号
			if (StringUtils.isNotEmpty(row.get(6))) {
				dto.setTradeId(row.get(6).trim());
			}

			// 在途日期
			if (StringUtils.isNotEmpty(row.get(7))) {
				dto.setOnwayDate(DateUtil.parse(row.get(7).trim()));
				dto.setNotOnwayDate(true);
			}

			// 科目号
			if (StringUtils.isNotEmpty(row.get(8))) {
				dto.setSubjectNo(row.get(8).trim());
			}

			// 分支序号
			if (StringUtils.isNotEmpty(row.get(9))) {
				dto.setBranch(Long.valueOf(row.get(9).trim()));
			}

			// 币种
			if (StringUtils.isNotEmpty(row.get(10))) {
				dto.setoCcy(row.get(10).trim().split(GlobalConstant.分号)[0]);
			}

			// 借贷方向
			if (StringUtils.isNotEmpty(row.get(11))) {
				dto.setCdFlag(row.get(11).trim().split(GlobalConstant.分号)[0]);
			}

			// 发生额
			if (StringUtils.isNotEmpty(row.get(12))) {
				dto.setoHappenAmt(new BigDecimal(row.get(12).trim()));
			}

			// 发生数量
			if (StringUtils.isNotEmpty(row.get(13))) {
				dto.setHappenNumber(new BigDecimal(row.get(13).trim()));
			}

			// 结息日
			if (StringUtils.isNotEmpty(row.get(14))) {
				dto.setMdate(DateUtil.parse(row.get(14).trim()));
			}

			// 余额方向
			if (StringUtils.isNotEmpty(row.get(15))) {
				dto.setBalFlagHappenDate(row.get(15).trim().split(GlobalConstant.分号)[0]);
			}

			// 原币余额
			if (StringUtils.isNotEmpty(row.get(16))) {
				dto.setoAmtHappenDate(new BigDecimal(row.get(16).trim()));
			}

			// 数量余额
			if (StringUtils.isNotEmpty(row.get(17))) {
				dto.setNumAmtHappenDate(new BigDecimal(row.get(17).trim()));
			}

			// 塞入list
			dtoList.add(dto);

		}

		return dtoList;
	}

//	/**
//	 * 将excel中的数据转换为实体类
//	 * 
//	 * @param sheetDataList
//	 * @return
//	 * @see
//	 * @since 1.0
//	 */
//	private List<AccountsBalanceTransplantDto> convertToDto(List<List<String>> sheetDataList, String modelType) {
//		// 定义返回实体类
//		List<AccountsBalanceTransplantDto> dtoList = new ArrayList<AccountsBalanceTransplantDto>();
//
//		// 循环进行数据处理
//		for (List<String> row : sheetDataList) {
//			// 定义实体类
//			AccountsBalanceTransplantDto dto = new AccountsBalanceTransplantDto();
//			// 账套代码
//			if (StringUtils.isNotEmpty(row.get(0))) {
//				dto.setBooksetId(row.get(0).trim());
//			}
//
//			// 业务明细代码
//			if (StringUtils.isNotEmpty(row.get(1))) {
//				dto.setBusiId(row.get(1).trim());
//			}
//
//			// 业务明细类型
//			if (StringUtils.isNotEmpty(row.get(2))) {
//				dto.setBusiDetailType(row.get(2).trim().split(GlobalConstant.分号)[0]);
//			}
//
//			// 日期
//			if (StringUtils.isNotEmpty(row.get(3))) {
//				dto.setBalanceDate(row.get(3) == null ? null : DateUtil.parse(row.get(3).trim()));
//			}
//
//			// 投资目的
//			if (StringUtils.isNotEmpty(row.get(4))) {
//				dto.setInvAim(row.get(4).trim().split(GlobalConstant.分号)[0]);
//			}
//
//			// 科目号
//			if (StringUtils.isNotEmpty(row.get(5))) {
//				dto.setSubjectNo(row.get(5).trim());
//			}
//
//			// 分支序号
//			if (StringUtils.isNotEmpty(row.get(6))) {
//				dto.setBranch(Long.valueOf(row.get(6).trim()));
//			}
//
//			// 币种
//			if (StringUtils.isNotEmpty(row.get(7))) {
//				dto.setoCcy(row.get(7).trim());
//			}
//
//			// 结息日
//			if (StringUtils.isNotEmpty(row.get(8))) {
//				dto.setMdate(DateUtil.parse(row.get(8).trim()));
//			}
//
//			// 余额方向
//			if (StringUtils.isNotEmpty(row.get(9))) {
//				dto.setBalFlagHappenDate(row.get(9).trim().split(GlobalConstant.分号)[0]);
//			}
//
//			// 原币余额
//			if (StringUtils.isNotEmpty(row.get(10))) {
//				dto.setoAmtHappenDate(new BigDecimal(row.get(10).trim()));
//			}
//
//			// 数量余额
//			if (StringUtils.isNotEmpty(row.get(11))) {
//				dto.setNumAmtHappenDate(new BigDecimal(row.get(11).trim()));
//			}
//			dto.setModelType(modelType);
//			// 塞入list
//			dtoList.add(dto);
//
//		}
//
//		return dtoList;
//	}

	/**
	 * 科目余额表数据入库<一句话功能简述> <功能详细描述>
	 * 
	 * @param listBokBalance
	 * @see
	 * @since 1.0
	 */
	private void insertBokBalance(List<BokBalance> listBokBalance) {

		// 入库
		if (CollectionUtils.isNotEmpty(listBokBalance)) {
			for (BokBalance bok : listBokBalance) {
				BokBalanceKey key = new BokBalanceKey();
				key.setBooksetId(bok.getBooksetId());
				key.setBalanceDate(bok.getBalanceDate());
				key.setSubjectNo(bok.getSubjectNo());
				BokBalance bal = bokBalanceMapper.selectByPrimaryKey(key);
				// 该记录已存在
				if (bal != null && StringUtils.isNotEmpty(bal.getBooksetId())) {
					// 更新余额
					bokBalanceMapper.deleteByPrimaryKey(key);
					bokBalanceMapper.insert(bok);
				} else {
					bokBalanceMapper.insert(bok);
				}
			}
		}
	}

	/**
	 * 账务明细历史表数据入库<一句话功能简述> <功能详细描述>
	 * 
	 * @param listBokBookdetailAll
	 * @see
	 * @since 1.0
	 */
	private void insertBokBookdetailAll(List<BokBookdetailAll> listBokBookdetailAll) {

		// 入库
		if (CollectionUtils.isNotEmpty(listBokBookdetailAll)) {
			// 定义一个固定大小插入存放集合
			List<BokBookdetailAll> listInsert = new ArrayList<BokBookdetailAll>();
			for (BokBookdetailAll e : listBokBookdetailAll) {
				// 小于最大值
				if (listInsert.size() < maxSqlNum) {
					listInsert.add(e);
				} else if (listInsert.size() >= maxSqlNum) {
					accountsBalanceTransplantMapper.insertBokBookdetailAll(listInsert);
					listInsert.clear();
					listInsert.add(e);
				}
			}
			// 如果循环结束，小于最大值
			if (CollectionUtils.isNotEmpty(listInsert)) {
				accountsBalanceTransplantMapper.insertBokBookdetailAll(listInsert);
			}
		}
	}

	/**
	 * 账务明细当前表数据入库<一句话功能简述> <功能详细描述>
	 * 
	 * @param listBokBookdetailCurrent
	 * @see
	 * @since 1.0
	 */
	private void insertBokBookdetailCurrent(List<BokBookdetailCurrent> listBokBookdetailCurrent) {

		// 入库
		if (CollectionUtils.isNotEmpty(listBokBookdetailCurrent)) {
			// 定义一个固定大小插入存放集合
			List<BokBookdetailCurrent> listInsert = new ArrayList<BokBookdetailCurrent>();
			for (BokBookdetailCurrent e : listBokBookdetailCurrent) {
				// 小于最大值
				if (listInsert.size() < maxSqlNum) {
					listInsert.add(e);
				} else if (listInsert.size() >= maxSqlNum) {
					accountsBalanceTransplantMapper.insertBokBookdetailCurrent(listInsert);
					listInsert.clear();
					listInsert.add(e);
				}
			}
			// 如果循环结束，小于最大值
			if (CollectionUtils.isNotEmpty(listInsert)) {
				accountsBalanceTransplantMapper.insertBokBookdetailCurrent(listInsert);
			}
		}
	}

	/**
	 * 账务明细代码表数据入库<一句话功能简述> <功能详细描述>
	 * 
	 * @param mapBokBussDetsub
	 * @see
	 * @since 1.0
	 */
	private void insertBokBussDetsub(Map<String, BokBussDetsub> mapBokBussDetsub) {
		List<BokBussDetsub> list = new ArrayList<BokBussDetsub>();
		for (String skey : mapBokBussDetsub.keySet()) {
			list.add(mapBokBussDetsub.get(skey));
		}
		// 入库
		if (CollectionUtils.isNotEmpty(list)) {
			// 定义一个固定大小插入存放集合
			List<BokBussDetsub> listInsert = new ArrayList<BokBussDetsub>();
			for (BokBussDetsub e : list) {
				// 小于最大值
				if (listInsert.size() < maxSqlNum) {
					listInsert.add(e);
				} else if (listInsert.size() >= maxSqlNum) {
					accountsBalanceTransplantMapper.insertBokBussDetsub(listInsert);
					listInsert.clear();
					listInsert.add(e);
				}
			}
			// 如果循环结束，小于最大值
			if (CollectionUtils.isNotEmpty(listInsert)) {
				accountsBalanceTransplantMapper.insertBokBussDetsub(listInsert);
			}
		}
	}

	/**
	 * 账务明细代码表数据入库<一句话功能简述> <功能详细描述>
	 * 
	 * @param mapBokDetailId
	 * @see
	 * @since 1.0
	 */
	private void insertBokDetailId(Map<String, BokDetailId> mapBokDetailId) {
		List<BokDetailId> listBokDetailId = new ArrayList<BokDetailId>();
		for (String skey : mapBokDetailId.keySet()) {
			listBokDetailId.add(mapBokDetailId.get(skey));
		}
		// 入库
		if (CollectionUtils.isNotEmpty(listBokDetailId)) {
			// 定义一个固定大小插入存放集合
			List<BokDetailId> listInsert = new ArrayList<BokDetailId>();
			for (BokDetailId e : listBokDetailId) {
				// 小于最大值
				if (listInsert.size() < maxSqlNum) {
					listInsert.add(e);
				} else if (listInsert.size() >= maxSqlNum) {
					accountsBalanceTransplantMapper.insertBokDetailId(listInsert);
					listInsert.clear();
					listInsert.add(e);
				}
			}
			// 如果循环结束，小于最大值
			if (CollectionUtils.isNotEmpty(listInsert)) {
				accountsBalanceTransplantMapper.insertBokDetailId(listInsert);
			}
		}
	}

	/**
	 * 余额表数据向上汇总处理<一句话功能简述> <功能详细描述>
	 * 
	 * @param listSubjectBalance
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<BokBalance> quaryBokBalance(List<SubjectBalance> listSubjectBalance,
			List<TemplateCheckDto> checkList) {

//		// 5.1.2去除重复科目，相同科目只使用最后一条的余额
//		Map<String, SubjectBalance> mapBal = new HashMap<String, SubjectBalance>();
//		for (SubjectBalance bal : listSubjectBalance) {
//			// 定义 key = 科目余额.账套代码+科目余额.余额日期+科目余额.科目号+科目余额.币种
//			String key = bal.getBooksetId() + bal.getBalanceDate() + bal.getOCcy() + bal.getSubjectNo();
//			mapBal.put(key, bal);
//		}
//		List<SubjectBalance> listData = new ArrayList<SubjectBalance>();
//		for (String skey : mapBal.keySet()) {
//			listData.add(mapBal.get(skey));
//		}

		// 账套集合
		Map<String, DataBooksetInfoDto> mapBooksetInfoDto = new HashMap<String, DataBooksetInfoDto>();

		for (SubjectBalance bal : listSubjectBalance) {
			String key = bal.getBooksetId() + bal.getOCcy();
			if (mapBooksetInfoDto.get(key) == null) {
				DataBooksetInfoDto info = new DataBooksetInfoDto();
				// 账套dto.账套代码 = 科目余额.账套代码
				info.setBooksetId(bal.getBooksetId());
				// 账套dto.开始日 = 科目余额.余额日期
				info.setStartDate(bal.getBalanceDate());
				// 账套dto.结束日 = 科目余额.余额日期
				info.setEndDate(bal.getBalanceDate());
				// 账套dto.币种 = 科目余额.原币
				info.setoCcy(bal.getOCcy());
				mapBooksetInfoDto.put(key, info);
			} else {
				DataBooksetInfoDto info = mapBooksetInfoDto.get(key);
				if (bal.getBalanceDate().compareTo(info.getStartDate()) < 0) {
					info.setStartDate(bal.getBalanceDate());
				}
				if (bal.getBalanceDate().compareTo(info.getEndDate()) > 0) {
					info.setEndDate(bal.getBalanceDate());
				}
				mapBooksetInfoDto.put(key, info);
			}
		}
		List<DataBooksetInfoDto> listDataBooksetInfoDto = new ArrayList<DataBooksetInfoDto>();
		for (String skey : mapBooksetInfoDto.keySet()) {
			listDataBooksetInfoDto.add(mapBooksetInfoDto.get(skey));
		}

		if (listDataBooksetInfoDto != null && listDataBooksetInfoDto.size() > 0) {
			List<SubjectBalance> listHuizong = new ArrayList<SubjectBalance>();
			// 分账套处理汇总信息
			for (DataBooksetInfoDto info : listDataBooksetInfoDto) {
				// 定义 入账日 = 账套dto.开始日
				Date bookDate = info.getStartDate();
				// 定义 昨日余额List
				// List<SubjectBalance> listLast = new
				// ArrayList<SubjectBalance>();
				do {
					// 定义当日余额List
					List<SubjectBalance> listTdy = new ArrayList<SubjectBalance>();
					// List<SubjectBalance> listRst = new
					// ArrayList<SubjectBalance>();

					// List<SubjectBalance> list_aready =
					// dataTransplantCommonService.queryTdayBalanceAready(info.getBooksetId(),
					// bookDate);
					// if (CollectionUtils.isNotEmpty(list_aready)) {
					// listTdy.addAll(list_aready);
					// }

					for (SubjectBalance dto : listSubjectBalance) {
						// if（科目余额.账套代码 = 账套dto.账套代码 && 科目余额.余额日期 = 入账日 &&
						// 科目余额.原币 = 账套dto.币种）
						if (dto.getBooksetId().equals(info.getBooksetId()) && dto.getBalanceDate().equals(bookDate)
								&& dto.getOCcy().equals(info.getoCcy())) {
							listTdy.add(dto);
						}
					}
					
					//查询是否当天已导入余额
					int count = accountsBalanceTransplantMapper.getTdyBanance(info.getBooksetId(),
							bookDate, info.getoCcy());
					if (count > 0) {
						TemplateCheckDto dto = new TemplateCheckDto();
						dto.setErrorLineNum(0);
						dto.setErrorMessage(info.getBooksetId() + "在" + DateUtil.format(bookDate) + "已导入余额，请删已导入的余额后再导入！");
						dto.setErrorType("错误");
						checkList.add(dto);
					}
					
					// 科目余额叶子节点向上汇总处理
					List<SubjectBalance> listRst = dataTransplantCommonService.subjectBalanceCollect(
							info.getBooksetId(), bookDate, new ArrayList<SubjectBalance>(), listTdy);
					// 日期设置
					// List<SubjectBalance> listLastCopy = new
					// ArrayList<SubjectBalance>();
					if (listRst != null && listRst.size() > 0) {
						for (SubjectBalance sub : listRst) {
							// SubjectBalance copy = new SubjectBalance();
							sub.setBookDate(bookDate);
							// BeanUtil.copyPropertiesIgnoreNull(copy, sub);
							// listLastCopy.add(copy);
						}
					}
					// 汇总余额List.add(上述方法返回值)
					listHuizong.addAll(listRst);
					// 昨日余额List = 上述方法返回值
					// listLast = listLastCopy;

					bookDate = DateUtil.addDays(bookDate, 1);
				} while (DateUtil.compareDate(info.getEndDate(), bookDate) >= 0);
			}

			// 余额表数据转换
			List<BokBalance> listBokBalance = quaryToBokBalance(listHuizong);
			return listBokBalance;
		}
		return null;
	}

	/**
	 * 余额表数据转换<一句话功能简述> <功能详细描述>
	 * 
	 * @param listHuizong
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<BokBalance> quaryToBokBalance(List<SubjectBalance> listHuizong) {
		List<BokBalance> list = new ArrayList<BokBalance>();
		if (listHuizong != null && listHuizong.size() > 0) {
			for (SubjectBalance bal : listHuizong) {
				BokBalance bok = new BokBalance();
				// 账套代码
				bok.setBooksetId(bal.getBooksetId());
				// 余额日期
				bok.setBalanceDate(bal.getBookDate());
				// 科目号
				bok.setSubjectNo(bal.getSubjectNo());
				// 父科目号
				bok.setFsubjectNo(bal.getFsubjectId());
				// 科目级别
				bok.setSubjectLevel(bal.getSubjectLevel());
				// 余额方向
				bok.setBalFlag(bal.getBalFlagHappenDate());
				// 结转余额方向
				bok.setBalCoFlag(bal.getBalCoFlag());
				// 原币
				bok.setoCcy(bal.getOCcy());
				// 原币余额
				bok.setoAmt(bal.getBalance() != null ? bal.getBalance() : BigDecimal.ZERO);
				// 原币余额贷方
				bok.setoCAmt(bal.getcAmt() != null ? bal.getcAmt() : BigDecimal.ZERO);
				// 原币余额借方
				bok.setoDAmt(bal.getdAmt() != null ? bal.getdAmt() : BigDecimal.ZERO);
				// 原币结转余额
				bok.setoCoAmt(bal.getoCcyKnotsAmt() != null ? bal.getoCcyKnotsAmt() : BigDecimal.ZERO);
				// 原币结转余额贷方
				bok.setoCoCAmt(bal.getoCcyKnotsAmtC() != null ? bal.getoCcyKnotsAmtC() : BigDecimal.ZERO);
				// 原币结转余额借方
				bok.setoCoDAmt(bal.getoCcyKnotsAmtD() != null ? bal.getoCcyKnotsAmtD() : BigDecimal.ZERO);
				// 本位币
				bok.setbCcy(bal.getbCcy());
				// 本位币余额
				bok.setbAmt(bal.getbAmt() != null ? bal.getbAmt() : BigDecimal.ZERO);
				// 本位币余额贷方
				bok.setbCAmt(bal.getbCAmt() != null ? bal.getbCAmt() : BigDecimal.ZERO);
				// 本位币结转余额
				bok.setbCoAmt(bal.getbCoAmt() != null ? bal.getbAmt() : BigDecimal.ZERO);
				// 本位币结转余额贷方
				bok.setbCoCAmt(bal.getbCoCAmt() != null ? bal.getbCoCAmt() : BigDecimal.ZERO);
				// 本位币结转余额借方
				bok.setbCoDAmt(bal.getbCoDAmt() != null ? bal.getbCoDAmt() : BigDecimal.ZERO);
				// 本位币余额借方
				bok.setbDAmt(bal.getbDAmt() != null ? bal.getbDAmt() : BigDecimal.ZERO);
				// 原币当日发生额
				bok.setTdyOAmt(bal.getTdyOAmt() != null ? bal.getTdyOAmt() : BigDecimal.ZERO);
				// 原币当日发生额贷方
				bok.setTdyOCAmt(bal.getTdyOCAmt() != null ? bal.getTdyOCAmt() : BigDecimal.ZERO);
				// 原币当日发生额借方
				bok.setTdyODAmt(bal.getTdyODAmt() != null ? bal.getTdyODAmt() : BigDecimal.ZERO);
				// 原币结转当日发生额
				bok.setTdyOCoAmt(bal.getTdyOCoAmt() != null ? bal.getTdyOCoAmt() : BigDecimal.ZERO);
				// 原币结转当日发生额贷方
				bok.setTdyOCoCAmt(bal.getTdyOCoCAmt() != null ? bal.getTdyOCoCAmt() : BigDecimal.ZERO);
				// 原币结转当日发生额借方
				bok.setTdyOCoDAmt(bal.getTdyOCoDAmt() != null ? bal.getTdyOCoDAmt() : BigDecimal.ZERO);
				// 本位币当日发生额
				bok.setTdyBAmt(bal.getTdyBAmt() != null ? bal.getTdyBAmt() : BigDecimal.ZERO);
				// 本位币当日发生额贷方
				bok.setTdyBCAmt(bal.getTdyBCAmt() != null ? bal.getTdyBCAmt() : BigDecimal.ZERO);
				// 本位币当日发生额借方
				bok.setTdyBDAmt(bal.getTdyBDAmt() != null ? bal.getTdyBDAmt() : BigDecimal.ZERO);
				// 本位币结转当日发生额
				bok.setTdyBCoAmt(bal.getTdyBCoAmt() != null ? bal.getTdyBCoAmt() : BigDecimal.ZERO);
				// 本位币结转当日发生额贷方
				bok.setTdyBCoCAmt(bal.getTdyBCoCAmt() != null ? bal.getTdyBCoCAmt() : BigDecimal.ZERO);
				// 本位币结转当日发生额借方
				bok.setTdyBCoDAmt(bal.getTdyBCoDAmt() != null ? bal.getTdyBCoDAmt() : BigDecimal.ZERO);
				// 是否叶子节点
				bok.setIsLeaf(bal.getIsleaf());
				// 数量余额
				bok.setNumAmt(bal.getNum());
				// 创建人
				bok.setCreateUser(GlobalConstant.数据移植账户);
				// 创建部门
				// 创建时间
				// 更新人
				bok.setUpdateUser(GlobalConstant.数据移植账户);
				// 更新时间
				// 当日发生额方向
				bok.setTdyAmtFlag(bal.getTdyAmtFlag());
				// 结转当日发生额方向
				bok.setTdyCoFlag(bal.getTdyCoFlag());
				// 原币申购未实现平准金借
				bok.setTdyPurODUlamt(bal.getTdyPurODUlamt() != null ? bal.getTdyPurODUlamt() : BigDecimal.ZERO);
				// 原币赎回未实现平准金借
				bok.setTdyRedODUlamt(bal.getTdyRedODUlamt() != null ? bal.getTdyRedODUlamt() : BigDecimal.ZERO);
				// 原币申购未实现平准金贷
				bok.setTdyPurOCUlamt(bal.getTdyPurOCUlamt() != null ? bal.getTdyPurOCUlamt() : BigDecimal.ZERO);
				// 原币赎回未实现平准金贷
				bok.setTdyRedOCUlamt(bal.getTdyRedOCUlamt() != null ? bal.getTdyRedOCUlamt() : BigDecimal.ZERO);
				// 本位币申购未实现平准金借
				bok.setTdyPurBDUlamt(bal.getTdyPurBDUlamt() != null ? bal.getTdyPurBDUlamt() : BigDecimal.ZERO);
				// 本位币赎回未实现平准金借
				bok.setTdyRedBDUlamt(bal.getTdyRedBDUlamt() != null ? bal.getTdyRedBDUlamt() : BigDecimal.ZERO);
				// 本位币申购未实现平准金贷
				bok.setTdyPurBCUlamt(bal.getTdyPurBCUlamt() != null ? bal.getTdyPurBCUlamt() : BigDecimal.ZERO);
				// 本位币赎回未实现平准金贷
				bok.setTdyRedBCUlamt(bal.getTdyRedBCUlamt() != null ? bal.getTdyRedBCUlamt() : BigDecimal.ZERO);
				list.add(bok);
			}
		}
		return list;
	}

	/**
	 * 生成【科目余额（BOK_BALANCE）】数据<一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @param detailSubjectNoTail
	 * @return
	 * @see
	 * @since 1.0
	 */
	private SubjectBalance createSubjectBalance(AccountsBalanceTransplantDto dto, String detailSubjectNoTail) {
		SubjectBalance subjectBalance = new SubjectBalance();
		// 科目余额.账套代码 = 导入数据-账套代码 ;
		subjectBalance.setBooksetId(dto.getBooksetId());
		// 科目余额.余额日期 = 导入数据-日期 ;
		subjectBalance.setBalanceDate(dto.getBalanceDate());
		// 调用数据移植-共通方法：获取科目信息
		BokSubject bokSubject = dataTransplantCommonService.findSubjectInfo(dto.getBooksetId(), dto.getSubjectNo());
		// IF(变量-四级科目尾号!=NULL)
		if (bokSubject != null && DictCodeList.是否_是.getValue().equals(bokSubject.getGenDetsubFlag())
				&& StringUtils.isNotEmpty(detailSubjectNoTail)) {
			// 科目余额.科目号 = 导入数据-科目号 + 变量-四级科目尾号;
			subjectBalance.setSubjectNo(dto.getSubjectNo() + detailSubjectNoTail);
			// 科目余额.科目级别 =99
			subjectBalance.setSubjectLevel(BookConstant.MAX_SUBJECT_LEVEL);
			// 科目余额.父科目号 = 科目号本身
			subjectBalance.setFsubjectNo(dto.getSubjectNo());
			subjectBalance.setFsubjectId(dto.getSubjectNo());
		} else {
			// 科目余额.科目号 = 导入数据-科目号 ;
			subjectBalance.setSubjectNo(dto.getSubjectNo());
			// 科目余额.科目级别
			subjectBalance.setSubjectLevel(bokSubject==null?0L:bokSubject.getSubjectLevel());
			// 科目余额.父科目号 = 返回值.上级科目号 ;
			subjectBalance.setFsubjectNo(bokSubject==null?null:bokSubject.getFsubjectId());
			subjectBalance.setFsubjectId(bokSubject==null?null:bokSubject.getFsubjectId());
		}

		// 科目余额.余额方向 = 导入数据-余额方向 ;
		subjectBalance.setBalFlagHappenDate(dto.getBalFlagHappenDate());
		// 科目余额.结转余额方向 = NULL ;
		// 科目余额.原币 = 导入数据-币种 ;
		subjectBalance.setOCcy(dto.getoCcy());
		// 科目余额.原币余额 = 导入数据-原币余额 ;
		subjectBalance.setBalance(dto.getoAmtHappenDate());
		// 科目余额.原币余额贷方 = CASE WHEN(导入数据-余额方向="C"(贷方)) THEN 【导入数据-原币余额】 ELSE 0
		// END ;
		subjectBalance.setcAmt(dto.getBalFlagHappenDate().equals(DictCodeList.余额方向_贷方.getValue())
				? dto.getoAmtHappenDate() : BigDecimal.ZERO);
		// 科目余额.原币余额借方 = CASE WHEN(导入数据-余额方向="D"(借方)) THEN 【导入数据-原币余额】 ELSE 0
		// END ;
		subjectBalance.setdAmt(dto.getBalFlagHappenDate().equals(DictCodeList.余额方向_借方.getValue())
				? dto.getoAmtHappenDate() : BigDecimal.ZERO);
		// 科目余额.原币结转余额 = NULL ;
		subjectBalance.setoCcyKnotsAmt(BigDecimal.ZERO);
		// 科目余额.原币结转余额贷方 = NULL ;
		subjectBalance.setoCcyKnotsAmtC(BigDecimal.ZERO);
		// 科目余额.原币结转余额借方 = NULL ;
		subjectBalance.setoCcyKnotsAmtD(BigDecimal.ZERO);
		// 科目余额.本位币 = NULL ;
		// 科目余额.本位币余额 = NULL ;
		// 科目余额.本位币余额贷方 = NULL ;
		// 科目余额.本位币结转余额 = NULL ;
		// 科目余额.本位币结转余额贷方 = NULL ;
		// 科目余额.本位币结转余额借方 = NULL ;
		// 科目余额.本位币余额借方 = NULL ;
		// 科目余额.原币当日发生额 = 0 ;
		subjectBalance.setTdyOAmt(BigDecimal.ZERO);
		// 科目余额.原币当日发生额贷方 = 0 ;
		subjectBalance.setTdyOCAmt(BigDecimal.ZERO);
		// 科目余额.原币当日发生额借方 = 0 ;
		subjectBalance.setTdyODAmt(BigDecimal.ZERO);
		// 科目余额.原币结转当日发生额 = 0 ;
		subjectBalance.setTdyOCoAmt(BigDecimal.ZERO);
		// 科目余额.原币结转当日发生额贷方 = 0 ;
		subjectBalance.setTdyOCoCAmt(BigDecimal.ZERO);
		// 科目余额.原币结转当日发生额借方 = 0 ;
		subjectBalance.setTdyOCoDAmt(BigDecimal.ZERO);
		// 科目余额.本位币当日发生额 = NULL ;
		// 科目余额.本位币当日发生额贷方 = NULL ;
		// 科目余额.本位币当日发生额借方 = NULL ;
		// 科目余额.本位币结转当日发生额 = NULL ;
		// 科目余额.本位币结转当日发生额贷方 = NULL ;
		// 科目余额.本位币结转当日发生额借方 = NULL ;
		// 科目余额.是否叶子节点 = Y(是) ;
		subjectBalance.setIsleaf(DictCodeList.是否_是.getValue());
		// 科目余额.数量余额 = 导入数据-数量余额 ;
		subjectBalance.setNum(dto.getNumAmtHappenDate());
		subjectBalance.setNumAmt(dto.getNumAmtHappenDate());
		// 科目余额.当日发生额方向 = 0 ;
		// 科目余额.结转当日发生额方向 = 0 ;
		// 科目余额.原币申购未实现平准金借 = 0 ;
		subjectBalance.setTdyPurODUlamt(BigDecimal.ZERO);
		// 科目余额.原币赎回未实现平准金借 = 0 ;
		subjectBalance.setTdyRedODUlamt(BigDecimal.ZERO);
		// 科目余额.原币申购未实现平准金贷 = 0 ;
		subjectBalance.setTdyPurOCUlamt(BigDecimal.ZERO);
		// 科目余额.原币赎回未实现平准金贷 = 0 ;
		subjectBalance.setTdyRedOCUlamt(BigDecimal.ZERO);
		// 科目余额.本位币申购未实现平准金借 = 0 ;
		subjectBalance.setTdyPurBDUlamt(BigDecimal.ZERO);
		// 科目余额.本位币赎回未实现平准金借 = 0 ;
		subjectBalance.setTdyRedBDUlamt(BigDecimal.ZERO);
		// 科目余额.本位币申购未实现平准金贷 = 0 ;
		subjectBalance.setTdyPurBCUlamt(BigDecimal.ZERO);
		// 科目余额.本位币赎回未实现平准金贷 = 0 ;
		subjectBalance.setTdyRedBCUlamt(BigDecimal.ZERO);
		return subjectBalance;
	}

	/**
	 * 生成【账务明细(历史表)（BOK_BOOKDETAIL_ALL）】数据<一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @param setailSubjectNo
	 * @return
	 * @see
	 * @since 1.0
	 */
	private BokBookdetailAll createBokBookdetailAll(AccountsBalanceTransplantDto dto, String setailSubjectNo,
			String modelType, List<ValSubjectTableid> tableIds) {
		BokBookdetailAll bokBookdetailAll = new BokBookdetailAll();
		// 账务明细(历史表).流水 =
		// 调用项目方法：DBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型) ;
		bokBookdetailAll.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
		// 账务明细(历史表).账套代码 = 导入数据-账套代码 ;
		bokBookdetailAll.setBooksetId(dto.getBooksetId());

		if (dto.isNotOnwayDate()) {
			// 账务明细(历史表).场景代码 = 导入数据-场景代码 ;
			bokBookdetailAll.setTableId(dto.getTableId());
		} else {
			/**
			 * modify by wujie on 20181210 start String subjectHead = null; if
			 * (dto.getSubjectNo().length() >= 4 && dto.getSubjectNo().length()
			 * < 6) { subjectHead = dto.getSubjectNo().substring(0, 4);
			 * bokBookdetailAll.setTableId(
			 * accountsBalanceTransplantMapper.getTableId(subjectHead)); } else
			 * if (dto.getBooksetId().length() >= 6) { subjectHead =
			 * dto.getSubjectNo().substring(0, 6); bokBookdetailAll.setTableId(
			 * accountsBalanceTransplantMapper.getTableId(subjectHead)); }
			 **/
			if (CollectionUtils.isNotEmpty(tableIds)) {
				String tableId = null;
				for (ValSubjectTableid obj : tableIds) {
					if (dto.getSubjectNo().startsWith(obj.getSubjectHead())) {
						if (tableId == null || tableId.length() < obj.getTableid().length()) {
							tableId = obj.getTableid();
						}
					}
				}
				bokBookdetailAll.setTableId(tableId);
			}
			/** modify by wujie on 20181210 end **/
		}

		// 账务明细(历史表).交易编号 = 导入数据-交易编号 ;
		bokBookdetailAll.setTradeId(dto.getTradeId());
		// 账务明细(历史表).业务明细代码 = 导入数据-业务明细代码 ;
		bokBookdetailAll.setBusiId(dto.getBusiId());
		// 账务明细(历史表).任务代码 = NULL ;
		// 账务明细(历史表).任务优先级 = NULL ;
		// 账务明细(历史表).发生日期 = 导入数据-日期 ;
		bokBookdetailAll.setHappenDate(dto.getBalanceDate());
		// 账务明细(历史表).入账日期 = 导入数据-日期 ;
		bokBookdetailAll.setBookDate(dto.getBalanceDate());
		// 账务明细(历史表).账套日期 = 导入数据-日期 ;
		bokBookdetailAll.setBooksetDate(dto.getBalanceDate());
		// 账务明细(历史表).在途日期 = 导入数据-在途日期 ;
		bokBookdetailAll.setOnwayDate(dto.getOnwayDate());
		// 调用数据移植-共通方法：获取科目信息
		BokSubject bokSubject = dataTransplantCommonService.findSubjectInfo(dto.getBooksetId(), dto.getSubjectNo());
		// 科目性质
		bokBookdetailAll.setBaseSubjectNature(bokSubject == null ? null : bokSubject.getBaseSubjectNature());
		// 调用数据移植-共通方法：获取账务明细代码
		String bokDetailId = dataTransplantCommonService.getBookDetailId(dto.getBusiDetailType(), dto.getBusiId(),
				dto.getBranch(), dto.getInvAim(),dto.getoCcy());
		bokBookdetailAll.setBokDetailId(bokDetailId);
		// 分支序号
		// IF(导入数据-科目号 == "其他应收款-本金:12219902")
		if (dto.getSubjectNo().equals(GlobalConstant.科目号_其他应收款_本金)) {
			// 账务明细(当前表).分支序号 = 导入数据-结息日 + "01：本金";
//			bokBookdetailAll.setBranch(new Long(DateUtil.format(dto.getMdate(), "yyyyMMdd") + "01"));
			String branch = String.valueOf(dto.getBranch());
			bokBookdetailAll.setBranch(new Long(branch+"01"));
		} // IF(导入数据-科目号 == "其他应收款-利息:12219903")
		else if (dto.getSubjectNo().equals(GlobalConstant.科目号_其他应收款_利息)) {
			// 账务明细(当前表).分支序号 = 导入数据-结息日 + "02：利息";
			bokBookdetailAll.setBranch(new Long(DateUtil.format(dto.getMdate(), "yyyyMMdd") + "02"));
			String branch = String.valueOf(dto.getBranch());
			bokBookdetailAll.setBranch(new Long(branch+"02"));
		} else {
			bokBookdetailAll.setBranch(BookConstant.DEFALUT_BRANCH);
		}
		// 账务明细(历史表).科目号 = 导入数据-科目号 ;
		bokBookdetailAll.setSubjectNo(dto.getSubjectNo());
		// 账务明细(历史表).四级科目号 = 变量-四级科目号 ;
		bokBookdetailAll.setDetailSubjectNo(setailSubjectNo);
		// 账务明细(历史表).借贷方向 = 导入数据-借贷方向 ;
		bokBookdetailAll.setCdFlag(dto.getCdFlag());
		// 账务明细(历史表).原币 = 导入数据-币种 ;
		bokBookdetailAll.setoCcy(dto.getoCcy());
		// 账务明细(历史表).原币发生额 = 导入数据-发生额 ;
		bokBookdetailAll.setoHappenAmt(dto.getoHappenAmt());
		// 账务明细(历史表).本位币 = NULL ;
		// 账务明细(历史表).本位币发生额 = NULL ;
		// 账务明细(历史表).发生数量 = 导入数据-发生数量 ;
		bokBookdetailAll.setHappenNumber(dto.getHappenNumber());
		// 账务明细(历史表).结息日 = 导入数据-结息日 ;
		bokBookdetailAll.setMdate(dto.getMdate());
		// 账务明细(历史表).凭证说明 = NULL ;
		// 账务明细(历史表).冲正标识 = 01(正常交易) ;
		bokBookdetailAll.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
		// 账务明细(历史表).冲正日期 = NULL ;
		// 账务明细(历史表).反向流水号 = NULL ;
		// 账务明细(历史表).凭证类型 = 01(日终) ;
		bokBookdetailAll.setBookType(DictCodeList.凭证类型_日终.getValue());
		// 账务明细(历史表).处理序列 =
		// 调用项目方法：DBUtil.getSequenceIndex(DataIndexSequenceType.处理序列) ;
		bokBookdetailAll.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
		// 账务明细(历史表).余额方向（发生日） = NULL ;
		// 账务明细(历史表).原币余额（发生日） = NULL ;
		// 账务明细(历史表).本位币余额（发生日） = NULL ;
		// 账务明细(历史表).数量余额（发生日） = NULL ;
		// 账务明细(历史表).余额方向（入账日） = 导入数据-余额方向 ;
		bokBookdetailAll.setBalFlagBookDate(dto.getBalFlagHappenDate());
		// 账务明细(历史表).原币余额（入账日） = 导入数据-原币余额 ;
		bokBookdetailAll.setoAmtBookDate(dto.getoAmtHappenDate());
		// 账务明细(历史表).本位币余额（入账日） = NULL ;
		// 账务明细(历史表).数量余额（入账日） = 导入数据-数量余额 ;
		bokBookdetailAll.setNumAmtBookDate(dto.getNumAmtHappenDate());
		// 账务明细(历史表).换汇日期 = NULL ;
		// 账务明细(历史表).折算汇率 = NULL ;

		// 只有在途债券时需要塞值
		// IF(导入数据-业务明细类型=="F01:债券" && 导入数据-在途日期 != NULL)
		if (DictCodeListExt1.金融产品类型_债券.getValue().equals(dto.getBusiId()) && dto.getOnwayDate() != null) {
			BokBookdetailCurrent bokBookdetailCurrent = new BokBookdetailCurrent();
			// 复制BEAN
			BeanUtil.copyPropertiesIgnoreNull(bokBookdetailCurrent, bokBookdetailAll);
			// 账务明细(当前表).凭证编号 =
			// 调用项目方法：BookAccountCommonService.genBookId(账务明细(当前表));//放在最后一个获取塞值
			bokBookdetailAll.setBookId(bookAccountCommonService.genBookId(bokBookdetailCurrent));
		}
		// 创建人
		bokBookdetailAll.setCreateUser(GlobalConstant.数据移植账户);
		// 更新人
		bokBookdetailAll.setUpdateUser(GlobalConstant.数据移植账户);

		return bokBookdetailAll;

	}

	/**
	 * 生成【账务明细(当前表)(BOK_BOOKDETAIL_CURRENT)】数据<一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @param setailSubjectNo
	 * @return
	 * @see
	 * @since 1.0
	 */
	private BokBookdetailCurrent createBokBookdetailCurrent(AccountsBalanceTransplantDto dto, String setailSubjectNo,
			String modelType, List<ValSubjectTableid> tableIds) {
		BokBookdetailCurrent bokBookdetailCurrent = new BokBookdetailCurrent();
		// 账务明细(当前表).流水 =
		// 调用项目方法：DBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型) ;
		bokBookdetailCurrent.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
		// 账务明细(当前表).账套代码 = 导入数据-账套代码 ;
		bokBookdetailCurrent.setBooksetId(dto.getBooksetId());

		if (dto.isNotOnwayDate()) {
			// 账务明细(当前表).场景代码 = 导入数据-场景代码 ;
			bokBookdetailCurrent.setTableId(dto.getTableId());
		} else {
			/**
			 * modify by wujie on 20181210 start String subjectHead = null; if
			 * (dto.getSubjectNo().length() >= 4 && dto.getSubjectNo().length()
			 * < 6) { subjectHead = dto.getSubjectNo().substring(0, 4);
			 * bokBookdetailCurrent.setTableId(
			 * accountsBalanceTransplantMapper.getTableId(subjectHead)); } else
			 * if (dto.getBooksetId().length() >= 6) { subjectHead =
			 * dto.getSubjectNo().substring(0, 6);
			 * bokBookdetailCurrent.setTableId(
			 * accountsBalanceTransplantMapper.getTableId(subjectHead)); }
			 **/
			if (CollectionUtils.isNotEmpty(tableIds)) {
				String tableId = null;
				for (ValSubjectTableid obj : tableIds) {
					if (dto.getSubjectNo().startsWith(obj.getSubjectHead())) {
						if (tableId == null || tableId.length() < obj.getTableid().length()) {
							tableId = obj.getTableid();
						}
					}
				}
				bokBookdetailCurrent.setTableId(tableId);
			}
			/** modify by wujie on 20181210 end **/

		}

		// 账务明细(当前表).交易编号 = 导入数据-交易编号 ;
		bokBookdetailCurrent.setTradeId(dto.getTradeId());
		// 账务明细(当前表).业务明细代码 = 导入数据-业务明细代码 ;
		bokBookdetailCurrent.setBusiId(dto.getBusiId());
		// 账务明细(当前表).任务代码 = NULL ;
		// 账务明细(当前表).任务优先级 = NULL ;
		// 账务明细(当前表).发生日期 = 导入数据-日期 ;
		bokBookdetailCurrent.setHappenDate(dto.getBalanceDate());
		// 账务明细(当前表).入账日期 = 导入数据-日期 ;
		bokBookdetailCurrent.setBookDate(dto.getBalanceDate());
		// 账务明细(当前表).账套日期 = 导入数据-日期 ;
		bokBookdetailCurrent.setBooksetDate(dto.getBalanceDate());
		// 账务明细(当前表).在途日期 = 导入数据-在途日期 ;
		bokBookdetailCurrent.setOnwayDate(dto.getOnwayDate());
		// 调用数据移植-共通方法：获取科目信息
		BokSubject bokSubject = dataTransplantCommonService.findSubjectInfo(dto.getBooksetId(), dto.getSubjectNo());
		bokBookdetailCurrent.setBaseSubjectNature(bokSubject == null ? null : bokSubject.getBaseSubjectNature());
		// 调用数据移植-共通方法：获取账务明细代码
		String bokDetailId = dataTransplantCommonService.getBookDetailId(dto.getBusiDetailType(), dto.getBusiId(),
				dto.getBranch(), dto.getInvAim(),dto.getoCcy());
		bokBookdetailCurrent.setBokDetailId(bokDetailId);
		// 分支序号
		// IF(导入数据-科目号 == "其他应收款-本金:12219902")
		if (dto.getSubjectNo().equals(GlobalConstant.科目号_其他应收款_本金)) {
			String branch = String.valueOf(dto.getBranch());
			// 账务明细(当前表).分支序号 = 导入数据-结息日 + "01：本金";
//			bokBookdetailCurrent.setBranch(new Long(DateUtil.format(dto.getMdate(), "yyyyMMdd") + "01"));
			bokBookdetailCurrent.setBranch(new Long(branch+"01"));
		} // IF(导入数据-科目号 == "其他应收款-利息:12219903")
		else if (dto.getSubjectNo().equals(GlobalConstant.科目号_其他应收款_利息)) {
			String branch = String.valueOf(dto.getBranch());
			// 账务明细(当前表).分支序号 = 导入数据-结息日 + "02：利息";
//			bokBookdetailCurrent.setBranch(new Long(DateUtil.format(dto.getMdate(), "yyyyMMdd") + "02"));
			bokBookdetailCurrent.setBranch(new Long(branch+"02"));
		} else {
			bokBookdetailCurrent.setBranch(BookConstant.DEFALUT_BRANCH);
		}
		// 账务明细(当前表).科目号 = 导入数据-科目号 ;
		bokBookdetailCurrent.setSubjectNo(dto.getSubjectNo());
		// 判断科目是否生成四级科目号
		if (bokSubject != null && (bokSubject.getGenDetsubFlag() == null || bokSubject.getGenDetsubFlag().equals(""))) {
			// 账务明细(当前表).四级科目号 = 变量-四级科目号 ;
			bokBookdetailCurrent.setDetailSubjectNo(null);
		} else {
			// 账务明细(当前表).四级科目号 = 变量-四级科目号 ;
			bokBookdetailCurrent
					.setDetailSubjectNo(setailSubjectNo == null ? "" : dto.getSubjectNo() + setailSubjectNo);
		}
		// 账务明细(当前表).借贷方向 = 导入数据-借贷方向 ;
		bokBookdetailCurrent.setCdFlag(dto.getCdFlag());
		// 账务明细(当前表).原币 = 导入数据-币种 ;
		bokBookdetailCurrent.setoCcy(dto.getoCcy());
		// 账务明细(当前表).原币发生额 = 导入数据-发生额 ;
		bokBookdetailCurrent.setoHappenAmt(dto.getoHappenAmt());
		// 账务明细(当前表).本位币 = NULL ;
		// 账务明细(当前表).本位币发生额 = NULL ;
		// 账务明细(当前表).发生数量 = 导入数据-发生数量 ;
		bokBookdetailCurrent.setHappenNumber(dto.getHappenNumber());
		// 账务明细(当前表).结息日 = 导入数据-结息日 ;
		bokBookdetailCurrent.setMdate(dto.getMdate());
		// 账务明细(当前表).凭证说明 = NULL ;
		// 账务明细(当前表).冲正标识 = 01(正常交易) ;
		bokBookdetailCurrent.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
		// 账务明细(当前表).冲正日期 = NULL ;
		// 账务明细(当前表).反向流水号 = NULL ;
		// 账务明细(当前表).凭证类型 = 01(日终) ;
		bokBookdetailCurrent.setBookType(DictCodeList.凭证类型_日终.getValue());
		// 账务明细(当前表).处理序列 =
		// 调用项目方法：DBUtil.getSequenceIndex(DataIndexSequenceType.处理序列) ;
		bokBookdetailCurrent.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
		// 账务明细(当前表).余额方向（发生日） = 导入数据-余额方向 ;
		bokBookdetailCurrent.setBalFlagHappenDate(dto.getBalFlagHappenDate());
		// 账务明细(当前表).原币余额（发生日） = 导入数据-原币余额 ;
		bokBookdetailCurrent.setoAmtHappenDate(dto.getoAmtHappenDate());
		// 账务明细(当前表).本位币余额（发生日） = NULL ;
		// 账务明细(当前表).数量余额（发生日） = 导入数据-数量余额 ;
		bokBookdetailCurrent.setNumAmtHappenDate(dto.getNumAmtHappenDate());
		// 账务明细(当前表).余额方向（入账日） = NULL ;
		// 账务明细(当前表).原币余额（入账日） = NULL ;
		// 账务明细(当前表).本位币余额（入账日） = NULL ;
		// 账务明细(当前表).数量余额（入账日） = NULL ;
		// 账务明细(当前表).换汇日期 = NULL ;
		// 账务明细(当前表).折算汇率 = NULL ;

		// 只有在途债券时需要塞值
		// IF(导入数据-业务明细类型=="F01:债券" && 导入数据-在途日期 != NULL)
		if (DictCodeListExt1.金融产品类型_债券.getValue().equals(dto.getBusiId()) && dto.getOnwayDate() != null) {
			// 账务明细(当前表).凭证编号 =
			// 调用项目方法：BookAccountCommonService.genBookId(账务明细(当前表));//放在最后一个获取塞值
			bokBookdetailCurrent.setBookId(bookAccountCommonService.genBookId(bokBookdetailCurrent));
		}
		// 创建人
		bokBookdetailCurrent.setCreateUser(GlobalConstant.数据移植账户);
		// 更新人
		bokBookdetailCurrent.setUpdateUser(GlobalConstant.数据移植账户);

		return bokBookdetailCurrent;

	}

	/**
	 * 封装【业务明细科目（BOK_BUSS_DETSUB）】数据<一句话功能简述> <功能详细描述> <功能详细描述>
	 * 
	 * @param dto
	 * @param setailSubjectNo
	 * @param setailSubjectName
	 * @param detailSubjectNoTail
	 * @param detailSubnameTail
	 * @return
	 * @see
	 * @since 1.0
	 */
	private BokBussDetsub createBokBussDetsub(AccountsBalanceTransplantDto dto, String setailSubjectNo,
			String setailSubjectName, String detailSubjectNoTail, String detailSubnameTail) {

		BokBussDetsub bokBussDetsubDto = new BokBussDetsub();
		// 查询数据库是否已存在业务明细科目
		BokBussDetsubKey bokBussDetsubKey = new BokBussDetsubKey();
		bokBussDetsubKey.setBooksetId(dto.getBooksetId());
		bokBussDetsubKey.setDetailSubjectNo(setailSubjectNo);
		BokBussDetsub same = bokBussDetsubMapper.selectByPrimaryKey(bokBussDetsubKey);
		if (same != null && StringUtils.isNotEmpty(same.getBooksetId())) {
			return null;
		}
		// 业务明细科目.账套代码 = 导入数据-账套代码 ；
		bokBussDetsubDto.setBooksetId(dto.getBooksetId());
		// 业务明细科目.四级科目号 = 变量-四级科目号 ；
		bokBussDetsubDto.setDetailSubjectNo(setailSubjectNo);
		// 业务明细科目.四级科目名称 = 变量-四级科目号名称 ；
		bokBussDetsubDto.setDetailSubjectName(setailSubjectName);

		/**
		 * modify by wujie on 20181212 start // 调用数据移植-共通方法：获取科目信息 BokSubject
		 * bokSubject =
		 * dataTransplantCommonService.findSubjectInfo(dto.getBooksetId(),
		 * dto.getSubjectNo()); // 业务明细科目.上级科目号 = 返回值.上级科目号 ;
		 * bokBussDetsubDto.setFsubjectId(bokSubject == null ? null :
		 * bokSubject.getFsubjectId());
		 **/
		bokBussDetsubDto.setFsubjectId(dto.getSubjectNo());
		/** modify by wujie on 20181212 end **/

		// 业务明细科目.四级科目尾号 = 变量-四级科目尾号 ；
		bokBussDetsubDto.setDetailSubjectNoTail(detailSubjectNoTail);
		// 业务明细科目.四级科目尾号名称 = 变量-四级科目尾号名称；
		bokBussDetsubDto.setDetailSubnameTail(detailSubnameTail);
		// 创建人
		bokBussDetsubDto.setCreateUser(GlobalConstant.数据移植账户);
		// 更新人
		bokBussDetsubDto.setUpdateUser(GlobalConstant.数据移植账户);
		//创建时间
		bokBussDetsubDto.setCreateTime(dto.getBalanceDate());
		//更新时间
		bokBussDetsubDto.setUpdateTime(dto.getBalanceDate());

		return bokBussDetsubDto;
	}

	/**
	 * 封装账务明细代码表（BOK_DETAIL_ID）数据<一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @param detailSubjectNoTail
	 * @param detailSubnameTail
	 * @param bokDetailId
	 * @return
	 * @see
	 * @since 1.0
	 */
	private BokDetailId createBokDetailId(AccountsBalanceTransplantDto dto, String detailSubjectNoTail,
			String detailSubnameTail, String bokDetailId) {

		BokDetailId bokDetailIdDto = new BokDetailId();

		// 查询数据库是否已存在该账务明细代码
		BokDetailIdKey bokDetailIdKey = new BokDetailIdKey();
		bokDetailIdKey.setBokDetailId(bokDetailId);
		bokDetailIdKey.setBooksetId(dto.getBooksetId());
		BokDetailId same = bokDetailIdMapper.selectByPrimaryKey(bokDetailIdKey);
		if (same != null && StringUtils.isNotEmpty(same.getBooksetId())) {
			return null;
		}

		// 账务明细代码表.账套代码 = 导入数据-账套代码 ;
		bokDetailIdDto.setBooksetId(dto.getBooksetId());
		// 账务明细代码表.账务明细代码 = 返回值 ;
		bokDetailIdDto.setBokDetailId(bokDetailId);
		// 账务明细代码表.业务明细代码 = 导入数据-业务明细代码 ;
		bokDetailIdDto.setBusiId(dto.getBusiId());
		// 账务明细代码表.业务明细类型 = 导入数据-业务明细类型 ;
		bokDetailIdDto.setBusiDetailType(dto.getBusiDetailType());
		// 账务明细代码表.投资目的 = 导入数据-投资目的 ;
		bokDetailIdDto.setInvAim(dto.getInvAim());
		// 账务明细代码表.证券管理户代码 = NULL ;
		// 账务明细代码表.交易编号 = NULL ;
		// 账务明细代码表.四级科目尾号 = 变量-四级科目尾号 ;
		bokDetailIdDto.setDetailSubjectNoTail(detailSubjectNoTail);
		// 账务明细代码表.四级科目尾号名称 = 变量-四级科目尾号名称 ;
		bokDetailIdDto.setDetailSubnameTail(detailSubnameTail);
		// 账务明细代码表.分支序号 = 导入数据-分支序号/周期型产品起始日 ;
		bokDetailIdDto.setBranch(dto.getBranch() == null ? BookConstant.DEFALUT_BRANCH : dto.getBranch());
		// 账务明细代码.币种 = 导入数据-币种
		bokDetailIdDto.setCcy(dto.getoCcy());
		// 创建人
		bokDetailIdDto.setCreateUser(GlobalConstant.数据移植账户);
		// 更新人
		bokDetailIdDto.setUpdateUser(GlobalConstant.数据移植账户);
		//创建时间
		bokDetailIdDto.setCreateTime(dto.getBalanceDate());
		//更新时间
		bokDetailIdDto.setUpdateTime(dto.getBalanceDate());

		return bokDetailIdDto;
	}

	/**
	 * 账务余额模板校验
	 */
	private Map<String, Object> checkTransplantTemple(String modelType,
			List<AccountsBalanceTransplantDto> listAccountsBalanceTransplantDto, List<TemplateCheckDto> checkList,
			String typeError, int number) {
		Map<String, Object> map = new HashMap<>();
		// if (DictCodeListExt1.数据移植模版类型_账务余额模版.getValue().equals(modelType)) {
		//
		// for (int i = 0; i < listAccountsBalanceTransplantDto.size(); i++ ) {
		// // 判断是否有错误
		// boolean error = false;
		// // 校验账套表中是否存在该账套
		// if (StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getBooksetId())) {
		// boolean flag = dataTransplantCommonService.checkBooksetId(
		// listAccountsBalanceTransplantDto.get(i).getBooksetId());
		// if (!flag) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("账套表中不存在该账套");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// }
		// // 校验对应的标的是否存在
		// if
		// (StringUtil.isNotEmpty(listAccountsBalanceTransplantDto.get(i).getBusiId())
		// && StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getBusiDetailType())) {
		// Map<String,Object> params = new HashMap<String,Object>();
		// params.put("finprodId",
		// listAccountsBalanceTransplantDto.get(i).getBusiId());
		// params.put("finprodType",
		// listAccountsBalanceTransplantDto.get(i).getBusiDetailType());
		// params.put("booksetId",
		// listAccountsBalanceTransplantDto.get(i).getBooksetId());
		//
		// boolean flag = dataTransplantCommonService.checkUnderlying(params);
		// if (!flag) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("对应的标的不存在");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// }
		// if (!StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getBooksetId())) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("账套代码不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// if
		// (!StringUtil.isNotEmpty(listAccountsBalanceTransplantDto.get(i).getBusiId()))
		// {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("业务明细代码不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// if (!StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getBusiDetailType())) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("业务明细类型不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// if (listAccountsBalanceTransplantDto.get(i).getBalanceDate() == null)
		// {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("日期不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// if (!StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getSubjectNo())) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("科目号不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// if
		// (!StringUtil.isNotEmpty(listAccountsBalanceTransplantDto.get(i).getoCcy()))
		// {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("币种不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// if (!StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getBalFlagHappenDate())) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("余额方向不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// if (!StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getoAmtHappenDate())) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("原币余额不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// // 成本科目时做数量余额非空校验
		// if (!StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getNumAmtHappenDate())) {
		// boolean flag = dataTransplantCommonService.checkSubjectNature(
		// listAccountsBalanceTransplantDto.get(i).getSubjectNo());
		// // 成本类科目
		// if (flag) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("数量余额不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// }
		// // 校验非必须项
		// if (DictCodeListExt1.金融产品类型_债券.getValue().equals(
		// listAccountsBalanceTransplantDto.get(i).getBusiDetailType())) {
		// if (!StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getInvAim())) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage("业务明细类型为债券时，投资目的不能为空");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// }
		// // 校验枚举字段是否有效
		// if (StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getBusiDetailType())) {
		// DataDictItem from = SysDataUtil.getDataDictItem(DictCode.金融产品类型.getValue(),listAccountsBalanceTransplantDto.get(i).getBusiDetailType());
		// DataDictItem from2 = SysDataUtil.getDataDictItem(DictCode.资金账户类型.getValue(),listAccountsBalanceTransplantDto.get(i).getBusiDetailType());
		// if (!StringUtil.isNotEmpty(from) && !StringUtil.isNotEmpty(from2)) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage(
		// "业务明细类型枚举值：" + listAccountsBalanceTransplantDto.get(
		// i).getBusiDetailType() + "无效");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// }
		// if
		// (StringUtil.isNotEmpty(listAccountsBalanceTransplantDto.get(i).getInvAim()))
		// {
		// DataDictItem from = SysDataUtil.getDataDictItem(DictCode.投资目的.getValue(),listAccountsBalanceTransplantDto.get(i).getInvAim());
		// if (!StringUtil.isNotEmpty(from)) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage(
		// "投资目的枚举值: " + listAccountsBalanceTransplantDto.get(i).getInvAim()
		// + "无效");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// }
		// if (StringUtil.isNotEmpty(
		// listAccountsBalanceTransplantDto.get(i).getBalFlagHappenDate())) {
		// DataDictItem from = SysDataUtil.getDataDictItem(DictCode.余额方向.getValue(),listAccountsBalanceTransplantDto.get(i).getBalFlagHappenDate());
		// if (!StringUtil.isNotEmpty(from)) {
		// TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		// templateCheckDto.setErrorLineNum(i + 2);
		// templateCheckDto.setErrorType(typeError);
		// templateCheckDto.setErrorMessage(
		// "余额方向枚举值：" + listAccountsBalanceTransplantDto.get(
		// i).getBalFlagHappenDate() + "无效");
		// checkList.add(templateCheckDto);
		// error = true;
		// }
		// }
		// if (error) {
		// number++ ;
		// }
		//
		// }
		//
		// }
		// // 账务余额模板（在途债券）校验
		// else {

		for (int i = 0; i < listAccountsBalanceTransplantDto.size(); i++) {
			AccountsBalanceTransplantDto dto = listAccountsBalanceTransplantDto.get(i);
			// 判断是否有错误
			boolean error = false;
			// 校验账套表中是否存在该账套
			if (StringUtil.isNotEmpty(dto.getBooksetId())) {
				boolean flag = dataTransplantCommonService.checkBooksetId(dto.getBooksetId());
				if (!flag) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage("账套表中不存在该账套");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			// 校验对应的标的是否存在
			if (StringUtil.isNotEmpty(dto.getBusiId()) && StringUtil.isNotEmpty(dto.getBusiDetailType())) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("finprodId", dto.getBusiId());
				params.put("finprodType", dto.getBusiDetailType());
				params.put("booksetId", dto.getBooksetId());

				boolean flag = dataTransplantCommonService.checkUnderlying(params);

				if (!flag) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage("对应的标的不存在");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			if (!StringUtil.isNotEmpty(dto.getBooksetId())) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("账套代码不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getBusiId())) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("业务明细代码不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getBusiDetailType())) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("业务明细类型不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (dto.getBalanceDate() == null) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("日期不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getTableId()) && dto.isNotOnwayDate()) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("场景代码不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getTradeId()) && dto.isNotOnwayDate()) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("交易编号不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (dto.getOnwayDate() == null && dto.isNotOnwayDate()) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("在途日期不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getSubjectNo())) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("科目号不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getoCcy())) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("币种不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getCdFlag()) && dto.isNotOnwayDate()) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("借贷方向不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getoHappenAmt()) && dto.isNotOnwayDate()) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("发生额不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getHappenNumber()) && dto.isNotOnwayDate()) {
				boolean flag = dataTransplantCommonService.checkSubjectNature(dto.getSubjectNo());
				// 当时成本类科目时，非空判断
				if (flag) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage("发生数量不能为空");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			if (!StringUtil.isNotEmpty(dto.getBalFlagHappenDate())) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("余额方向不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			if (!StringUtil.isNotEmpty(dto.getoAmtHappenDate())) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("原币余额不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			// 校验非必须项
			if (DictCodeListExt1.金融产品类型_债券.getValue().equals(dto.getBusiDetailType())) {
				if (!StringUtil.isNotEmpty(dto.getInvAim())) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage("业务明细类型为债券时，投资目的不能为空");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			// 校验枚举字段是否有效
			if (StringUtil.isNotEmpty(dto.getBusiDetailType())) {
			    DataDictItem from = SysDataUtil.getDataDictItem(DictCode.金融产品类型.getValue(), dto.getBusiDetailType());
			    DataDictItem from2 = SysDataUtil.getDataDictItem(DictCode.资金账户类型.getValue(), dto.getBusiDetailType());
				if (!StringUtil.isNotEmpty(from) && !StringUtil.isNotEmpty(from2)) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage(
							"业务明细类型枚举值：" + listAccountsBalanceTransplantDto.get(i).getBusiDetailType() + "无效");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			if (StringUtil.isNotEmpty(dto.getInvAim())) {
			    DataDictItem from = SysDataUtil.getDataDictItem(DictCode.投资目的.getValue(), dto.getInvAim());
				if (!StringUtil.isNotEmpty(from)) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage("投资目的枚举值: " + dto.getInvAim() + "无效");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			if (StringUtil.isNotEmpty(dto.getTableId())) {
			    DataDictItem from = SysDataUtil.getDataDictItem(DictCode.会计场景代码.getValue(), dto.getTableId());
				if (!StringUtil.isNotEmpty(from)) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage("场景代码枚举值：" + dto.getTableId() + "无效");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			if (StringUtil.isNotEmpty(dto.getCdFlag())) {
			    DataDictItem from = SysDataUtil.getDataDictItem(DictCode.借贷方向.getValue(), dto.getCdFlag());
				if (!StringUtil.isNotEmpty(from)) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage("借贷方向枚举值：" + dto.getCdFlag() + "无效");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			if (StringUtil.isNotEmpty(dto.getBalFlagHappenDate())) {
			    DataDictItem from = SysDataUtil.getDataDictItem(DictCode.余额方向.getValue(), dto.getBalFlagHappenDate());
				if (!StringUtil.isNotEmpty(from)) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage(
							"余额方向枚举值：" + listAccountsBalanceTransplantDto.get(i).getBalFlagHappenDate() + "无效");
					checkList.add(templateCheckDto);
					error = true;
				}
			}
			if (error) {
				number++;
			}

		}

		// }

		map.put("checkList", checkList);
		map.put("number", number);

		return map;
	}
}
