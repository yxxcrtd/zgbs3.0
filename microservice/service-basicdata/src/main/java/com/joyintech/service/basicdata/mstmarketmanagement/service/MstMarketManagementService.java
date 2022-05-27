package com.joyintech.service.basicdata.mstmarketmanagement.service;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.dao.MstBondCreditRatingMapper;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.tams.common.base.entity.MstBondCreditRatingKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.mstmarketmanagement.dao.MstMarketManagementMapper;
import com.joyintech.service.basicdata.mstmarketmanagement.dto.MstMarketManageDto;
@Service
public class MstMarketManagementService  extends BaseService<MstMarketManageDto>{
	@Autowired
	private MstMarketManagementMapper mstMarketManagementMapper;
	/**
	 * 债券评级表
	 */
	@Autowired
	private MstBondCreditRatingMapper mstBondCreditRatingMapper;

	public Page<MstMarketManageDto> selectForPage(int page, int length, Map<String, Object> pageParam){
 	        return super.queryForPage(page,length,() -> mstMarketManagementMapper.queryForPage(pageParam));
	}
	  /**
     * 
     * 客户名称联想文本框service层实现
     * <功能详细描述>
     * @param map 联想文本框查询信息
     * @return 查询结果
     * @see
     * @since 1.0
     */
 
	public List<SuggestDto> selectFinprodName(Map<String, Object> param) {
 		return mstMarketManagementMapper.selectFinprodName(param);
	}

	public Page<SelectOptionDto> getCodeName(int page, int length, Map<String, Object> param) {
		return this.queryForPageForSelectReturn(page, length,
				() -> mstMarketManagementMapper.getCodeName(param));
	}

    public void doComplete(MstMarketManageDto entity) {
		// EDIT,先删除
		if (StringUtil.equals(entity.getPageModel(),"1")){
			MstBondCreditRatingKey mstBondCreditRating = new MstBondCreditRatingKey();
			mstBondCreditRating.setoType(entity.getoType());
			mstBondCreditRating.setoCode(entity.getoCode());
			// 取得修改前的评级机构
			mstBondCreditRating.setGradeOrgId(entity.getOldGradeOrgId());
			// 取得修改前的评级日期
			mstBondCreditRating.setGradeDate(DateUtil.parse(entity.getOldGradeDate()));
			mstBondCreditRatingMapper.deleteByPrimaryKey(mstBondCreditRating);
			// 取得评级机构
			mstBondCreditRating.setGradeOrgId(entity.getGradeOrgId());
			// 取得评级日期
			mstBondCreditRating.setGradeDate(DateUtil.parse(entity.getGradeDate()));
			mstBondCreditRatingMapper.deleteByPrimaryKey(mstBondCreditRating);

		}
		MstBondCreditRating mstBondCreditRating = new MstBondCreditRating();
		BeanUtil.copyPropertiesIgnoreNull(mstBondCreditRating,entity);
		mstBondCreditRating.setGradeDate(DateUtil.parse(entity.getGradeDate()));
		mstBondCreditRating.setNoticeDate(DateUtil.parse(entity.getNoticeDate()));
		mstBondCreditRatingMapper.insertSelective(mstBondCreditRating);
    }
	 
	public MstMarketManageDto selectDetail(MstMarketManageDto entity){
		MstBondCreditRatingKey mstBondCreditRatingKey = new MstBondCreditRatingKey();
		mstBondCreditRatingKey.setoType(entity.getoType());
		mstBondCreditRatingKey.setoCode(entity.getoCode());
		// 取得修改前的评级机构
		mstBondCreditRatingKey.setGradeOrgId(entity.getGradeOrgId());
		// 取得修改前的评级日期
		mstBondCreditRatingKey.setGradeDate(DateUtil.parse(entity.getGradeDate()));
		MstBondCreditRating mstBondCreditRating = mstBondCreditRatingMapper.selectByPrimaryKey(mstBondCreditRatingKey);
		if (mstBondCreditRating == null){
			return null;
		}
		BeanUtil.copyPropertiesIgnoreNull(entity,mstBondCreditRating);
		entity.setOldGradeDate(DateUtil.format(mstBondCreditRating.getGradeDate()));
		entity.setOldGradeOrgId(mstBondCreditRating.getGradeOrgId());
		entity.setGradeDate(DateUtil.format(mstBondCreditRating.getGradeDate()));
		entity.setNoticeDate(DateUtil.format(mstBondCreditRating.getNoticeDate()));
		return entity;
	}
	 
 
}
