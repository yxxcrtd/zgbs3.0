package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.util.Date;

public class SysActivereminderInfo extends BaseEntity {
    /**
     * 主键
     */
    private String seqno;

    /**
     * 日期
     */
    private Date cdate;

    /**
     * 提醒类型
     */
    private String remType;

    /**
     * 产品/资产/交易等ID
     */
    private String finprodId;

    /**
     * 产品/资产/交易等名称
     */
    private String finprodName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否提醒标识Y提醒N关闭提醒
     */
    private String effflag;

    /**
     * 关闭类型1当天关闭0永久关闭
     */
    private String closeflag;

    /**
     * 费用ID
     */
    private String feeId;

    /**
     * 关闭标记日期
     */
    private Date mdate;

    /**
     * 机构代码
     */
    private String institutionCode;

    /**
     * 取得 主键
     * @return 主键
     */
    public String getSeqno() {
        return seqno;
    }

    /**
     * 设置 主键
     * @param seqno 主键
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno == null ? null : seqno.trim();
    }

    /**
     * 取得 日期
     * @return 日期
     */
    public Date getCdate() {
        return cdate;
    }

    /**
     * 设置 日期
     * @param cdate 日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    /**
     * 取得 提醒类型
     * @return 提醒类型
     */
    public String getRemType() {
        return remType;
    }

    /**
     * 设置 提醒类型
     * @param remType 提醒类型
     */
    public void setRemType(String remType) {
        this.remType = remType == null ? null : remType.trim();
    }

    /**
     * 取得 产品/资产/交易等ID
     * @return 产品/资产/交易等ID
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 产品/资产/交易等ID
     * @param finprodId 产品/资产/交易等ID
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 产品/资产/交易等名称
     * @return 产品/资产/交易等名称
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * 设置 产品/资产/交易等名称
     * @param finprodName 产品/资产/交易等名称
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName == null ? null : finprodName.trim();
    }

    /**
     * 取得 备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 取得 是否提醒标识Y提醒N关闭提醒
     * @return 是否提醒标识Y提醒N关闭提醒
     */
    public String getEffflag() {
        return effflag;
    }

    /**
     * 设置 是否提醒标识Y提醒N关闭提醒
     * @param effflag 是否提醒标识Y提醒N关闭提醒
     */
    public void setEffflag(String effflag) {
        this.effflag = effflag == null ? null : effflag.trim();
    }

    /**
     * 取得 关闭类型1当天关闭0永久关闭
     * @return 关闭类型1当天关闭0永久关闭
     */
    public String getCloseflag() {
        return closeflag;
    }

    /**
     * 设置 关闭类型1当天关闭0永久关闭
     * @param closeflag 关闭类型1当天关闭0永久关闭
     */
    public void setCloseflag(String closeflag) {
        this.closeflag = closeflag == null ? null : closeflag.trim();
    }

    /**
     * 取得 费用ID
     * @return 费用ID
     */
    public String getFeeId() {
        return feeId;
    }

    /**
     * 设置 费用ID
     * @param feeId 费用ID
     */
    public void setFeeId(String feeId) {
        this.feeId = feeId == null ? null : feeId.trim();
    }

    /**
     * 取得 关闭标记日期
     * @return 关闭标记日期
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 关闭标记日期
     * @param mdate 关闭标记日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }
}