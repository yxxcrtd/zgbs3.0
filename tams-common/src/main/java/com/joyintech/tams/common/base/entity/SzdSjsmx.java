package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class SzdSjsmx extends BaseEntity {
    /**
     * 流水号
     */
    private String seqno;

    /**
     * 导入日期
     */
    private Date importDate;

    /**
     * 数据文件
     */
    private String dataFile;

    /**
     * 是否本次数据清算对象
     */
    private String isSettleTarget;

    /**
     * 业务流水号
     */
    private String mxywlsh;

    /**
     * 结算账号
     */
    private String mxjszh;

    /**
     * 备付金账户
     */
    private String mxbfzh;

    /**
     * 数据类型
     */
    private String mxsjlx;

    /**
     * 业务类别
     */
    private String mxywlb;

    /**
     * 证券代码
     */
    private String mxzqdm;

    /**
     * 交易单元
     */
    private String mxjydy;

    /**
     * 托管单元
     */
    private String mxtgdy;

    /**
     * 证券账户号码
     */
    private String mxzqzh;

    /**
     * 客户订单编号
     */
    private String mxddbh;

    /**
     * 营业部代码
     */
    private String mxyyb;

    /**
     * 执行编号
     */
    private String mxzxbh;

    /**
     * 成交数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxcjsl;

    /**
     * 清算数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxqssl;

    /**
     * 成交价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxcjjg;

    /**
     * 清算价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxqsjg;

    /**
     * 信用交易标识
     */
    private String mxxyjy;

    /**
     * 平仓标识
     */
    private String mxpcbs;

    /**
     * 证券类别
     */
    private String mxzqlb;

    /**
     * 证券子类别
     */
    private String mxzqzl;

    /**
     * 股份性质
     */
    private String mxgfxz;

    /**
     * 交收方式
     */
    private String mxjsfs;

    /**
     * 交收结果
     */
    private String jgdm;

    /**
     * 货币代号
     */
    private String mxhbdh;

    /**
     * 清算本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxqsbj;

    /**
     * 印花税
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxyhs;

    /**
     * 交易经手费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxjyjsf;

    /**
     * 监管规费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxjggf;

    /**
     * 过户费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxghf;

    /**
     * 结算费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxjsf;

    /**
     * 手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxsxf;

    /**
     * 结算参与人佣金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxqsyj;

    /**
     * 其他费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxqtfy;

    /**
     * 资金金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxzjje;

    /**
     * 收付净额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxsfje;

    /**
     * 成交日期
     */
    private String mxcjrq;

    /**
     * 清算日期
     */
    private String mxqsrq;

    /**
     * 交收日期
     */
    private String mxjsrq;

    /**
     * 发送日期
     */
    private String mxfsrq;

    /**
     * 其它日期
     */
    private String mxqtrq;

    /**
     * 市场代码
     */
    private String mxscdm;

    /**
     * 交易方式
     */
    private String mxjyfs;

    /**
     * 证券代码2
     */
    private String mxzqdm2;

    /**
     * 托管单元2
     */
    private String mxtgdy2;

    /**
     * 订单编号2
     */
    private String mxddbh2;

    /**
     * 错误代号
     */
    private String mxcwdh;

    /**
     * 匹配号码
     */
    private String mxpphm;

    /**
     * 附加说明
     */
    private String mxfjsm;

    /**
     * 备用标志
     */
    private String mxbybz;

    /**
     * 取得 流水号
     * @return 流水号
     */
    public String getSeqno() {
        return seqno;
    }

    /**
     * 设置 流水号
     * @param seqno 流水号
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno == null ? null : seqno.trim();
    }

    /**
     * 取得 导入日期
     * @return 导入日期
     */
    public Date getImportDate() {
        return importDate;
    }

    /**
     * 设置 导入日期
     * @param importDate 导入日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    /**
     * 取得 数据文件
     * @return 数据文件
     */
    public String getDataFile() {
        return dataFile;
    }

    /**
     * 设置 数据文件
     * @param dataFile 数据文件
     */
    public void setDataFile(String dataFile) {
        this.dataFile = dataFile == null ? null : dataFile.trim();
    }

    /**
     * 取得 是否本次数据清算对象
     * @return 是否本次数据清算对象
     */
    public String getIsSettleTarget() {
        return isSettleTarget;
    }

    /**
     * 设置 是否本次数据清算对象
     * @param isSettleTarget 是否本次数据清算对象
     */
    public void setIsSettleTarget(String isSettleTarget) {
        this.isSettleTarget = isSettleTarget == null ? null : isSettleTarget.trim();
    }

    /**
     * 取得 业务流水号
     * @return 业务流水号
     */
    public String getMxywlsh() {
        return mxywlsh;
    }

    /**
     * 设置 业务流水号
     * @param mxywlsh 业务流水号
     */
    public void setMxywlsh(String mxywlsh) {
        this.mxywlsh = mxywlsh == null ? null : mxywlsh.trim();
    }

    /**
     * 取得 结算账号
     * @return 结算账号
     */
    public String getMxjszh() {
        return mxjszh;
    }

    /**
     * 设置 结算账号
     * @param mxjszh 结算账号
     */
    public void setMxjszh(String mxjszh) {
        this.mxjszh = mxjszh == null ? null : mxjszh.trim();
    }

    /**
     * 取得 备付金账户
     * @return 备付金账户
     */
    public String getMxbfzh() {
        return mxbfzh;
    }

    /**
     * 设置 备付金账户
     * @param mxbfzh 备付金账户
     */
    public void setMxbfzh(String mxbfzh) {
        this.mxbfzh = mxbfzh == null ? null : mxbfzh.trim();
    }

    /**
     * 取得 数据类型
     * @return 数据类型
     */
    public String getMxsjlx() {
        return mxsjlx;
    }

    /**
     * 设置 数据类型
     * @param mxsjlx 数据类型
     */
    public void setMxsjlx(String mxsjlx) {
        this.mxsjlx = mxsjlx == null ? null : mxsjlx.trim();
    }

    /**
     * 取得 业务类别
     * @return 业务类别
     */
    public String getMxywlb() {
        return mxywlb;
    }

    /**
     * 设置 业务类别
     * @param mxywlb 业务类别
     */
    public void setMxywlb(String mxywlb) {
        this.mxywlb = mxywlb == null ? null : mxywlb.trim();
    }

    /**
     * 取得 证券代码
     * @return 证券代码
     */
    public String getMxzqdm() {
        return mxzqdm;
    }

    /**
     * 设置 证券代码
     * @param mxzqdm 证券代码
     */
    public void setMxzqdm(String mxzqdm) {
        this.mxzqdm = mxzqdm == null ? null : mxzqdm.trim();
    }

    /**
     * 取得 交易单元
     * @return 交易单元
     */
    public String getMxjydy() {
        return mxjydy;
    }

    /**
     * 设置 交易单元
     * @param mxjydy 交易单元
     */
    public void setMxjydy(String mxjydy) {
        this.mxjydy = mxjydy == null ? null : mxjydy.trim();
    }

    /**
     * 取得 托管单元
     * @return 托管单元
     */
    public String getMxtgdy() {
        return mxtgdy;
    }

    /**
     * 设置 托管单元
     * @param mxtgdy 托管单元
     */
    public void setMxtgdy(String mxtgdy) {
        this.mxtgdy = mxtgdy == null ? null : mxtgdy.trim();
    }

    /**
     * 取得 证券账户号码
     * @return 证券账户号码
     */
    public String getMxzqzh() {
        return mxzqzh;
    }

    /**
     * 设置 证券账户号码
     * @param mxzqzh 证券账户号码
     */
    public void setMxzqzh(String mxzqzh) {
        this.mxzqzh = mxzqzh == null ? null : mxzqzh.trim();
    }

    /**
     * 取得 客户订单编号
     * @return 客户订单编号
     */
    public String getMxddbh() {
        return mxddbh;
    }

    /**
     * 设置 客户订单编号
     * @param mxddbh 客户订单编号
     */
    public void setMxddbh(String mxddbh) {
        this.mxddbh = mxddbh == null ? null : mxddbh.trim();
    }

    /**
     * 取得 营业部代码
     * @return 营业部代码
     */
    public String getMxyyb() {
        return mxyyb;
    }

    /**
     * 设置 营业部代码
     * @param mxyyb 营业部代码
     */
    public void setMxyyb(String mxyyb) {
        this.mxyyb = mxyyb == null ? null : mxyyb.trim();
    }

    /**
     * 取得 执行编号
     * @return 执行编号
     */
    public String getMxzxbh() {
        return mxzxbh;
    }

    /**
     * 设置 执行编号
     * @param mxzxbh 执行编号
     */
    public void setMxzxbh(String mxzxbh) {
        this.mxzxbh = mxzxbh == null ? null : mxzxbh.trim();
    }

    /**
     * 取得 成交数量
     * @return 成交数量
     */
    public BigDecimal getMxcjsl() {
        return mxcjsl;
    }

    /**
     * 设置 成交数量
     * @param mxcjsl 成交数量
     */
    public void setMxcjsl(BigDecimal mxcjsl) {
        this.mxcjsl = mxcjsl;
    }

    /**
     * 取得 清算数量
     * @return 清算数量
     */
    public BigDecimal getMxqssl() {
        return mxqssl;
    }

    /**
     * 设置 清算数量
     * @param mxqssl 清算数量
     */
    public void setMxqssl(BigDecimal mxqssl) {
        this.mxqssl = mxqssl;
    }

    /**
     * 取得 成交价格
     * @return 成交价格
     */
    public BigDecimal getMxcjjg() {
        return mxcjjg;
    }

    /**
     * 设置 成交价格
     * @param mxcjjg 成交价格
     */
    public void setMxcjjg(BigDecimal mxcjjg) {
        this.mxcjjg = mxcjjg;
    }

    /**
     * 取得 清算价格
     * @return 清算价格
     */
    public BigDecimal getMxqsjg() {
        return mxqsjg;
    }

    /**
     * 设置 清算价格
     * @param mxqsjg 清算价格
     */
    public void setMxqsjg(BigDecimal mxqsjg) {
        this.mxqsjg = mxqsjg;
    }

    /**
     * 取得 信用交易标识
     * @return 信用交易标识
     */
    public String getMxxyjy() {
        return mxxyjy;
    }

    /**
     * 设置 信用交易标识
     * @param mxxyjy 信用交易标识
     */
    public void setMxxyjy(String mxxyjy) {
        this.mxxyjy = mxxyjy == null ? null : mxxyjy.trim();
    }

    /**
     * 取得 平仓标识
     * @return 平仓标识
     */
    public String getMxpcbs() {
        return mxpcbs;
    }

    /**
     * 设置 平仓标识
     * @param mxpcbs 平仓标识
     */
    public void setMxpcbs(String mxpcbs) {
        this.mxpcbs = mxpcbs == null ? null : mxpcbs.trim();
    }

    /**
     * 取得 证券类别
     * @return 证券类别
     */
    public String getMxzqlb() {
        return mxzqlb;
    }

    /**
     * 设置 证券类别
     * @param mxzqlb 证券类别
     */
    public void setMxzqlb(String mxzqlb) {
        this.mxzqlb = mxzqlb == null ? null : mxzqlb.trim();
    }

    /**
     * 取得 证券子类别
     * @return 证券子类别
     */
    public String getMxzqzl() {
        return mxzqzl;
    }

    /**
     * 设置 证券子类别
     * @param mxzqzl 证券子类别
     */
    public void setMxzqzl(String mxzqzl) {
        this.mxzqzl = mxzqzl == null ? null : mxzqzl.trim();
    }

    /**
     * 取得 股份性质
     * @return 股份性质
     */
    public String getMxgfxz() {
        return mxgfxz;
    }

    /**
     * 设置 股份性质
     * @param mxgfxz 股份性质
     */
    public void setMxgfxz(String mxgfxz) {
        this.mxgfxz = mxgfxz == null ? null : mxgfxz.trim();
    }

    /**
     * 取得 交收方式
     * @return 交收方式
     */
    public String getMxjsfs() {
        return mxjsfs;
    }

    /**
     * 设置 交收方式
     * @param mxjsfs 交收方式
     */
    public void setMxjsfs(String mxjsfs) {
        this.mxjsfs = mxjsfs == null ? null : mxjsfs.trim();
    }

    public String getJgdm() {
        return jgdm;
    }

    public void setJgdm(String jgdm) {
        this.jgdm = jgdm;
    }

    /**
     * 取得 货币代号
     * @return 货币代号
     */
    public String getMxhbdh() {
        return mxhbdh;
    }

    /**
     * 设置 货币代号
     * @param mxhbdh 货币代号
     */
    public void setMxhbdh(String mxhbdh) {
        this.mxhbdh = mxhbdh == null ? null : mxhbdh.trim();
    }

    /**
     * 取得 清算本金
     * @return 清算本金
     */
    public BigDecimal getMxqsbj() {
        return mxqsbj;
    }

    /**
     * 设置 清算本金
     * @param mxqsbj 清算本金
     */
    public void setMxqsbj(BigDecimal mxqsbj) {
        this.mxqsbj = mxqsbj;
    }

    /**
     * 取得 印花税
     * @return 印花税
     */
    public BigDecimal getMxyhs() {
        return mxyhs;
    }

    /**
     * 设置 印花税
     * @param mxyhs 印花税
     */
    public void setMxyhs(BigDecimal mxyhs) {
        this.mxyhs = mxyhs;
    }

    /**
     * 取得 交易经手费
     * @return 交易经手费
     */
    public BigDecimal getMxjyjsf() {
        return mxjyjsf;
    }

    /**
     * 设置 交易经手费
     * @param mxjyjsf 交易经手费
     */
    public void setMxjyjsf(BigDecimal mxjyjsf) {
        this.mxjyjsf = mxjyjsf;
    }

    /**
     * 取得 监管规费
     * @return 监管规费
     */
    public BigDecimal getMxjggf() {
        return mxjggf;
    }

    /**
     * 设置 监管规费
     * @param mxjggf 监管规费
     */
    public void setMxjggf(BigDecimal mxjggf) {
        this.mxjggf = mxjggf;
    }

    /**
     * 取得 过户费
     * @return 过户费
     */
    public BigDecimal getMxghf() {
        return mxghf;
    }

    /**
     * 设置 过户费
     * @param mxghf 过户费
     */
    public void setMxghf(BigDecimal mxghf) {
        this.mxghf = mxghf;
    }

    /**
     * 取得 结算费
     * @return 结算费
     */
    public BigDecimal getMxjsf() {
        return mxjsf;
    }

    /**
     * 设置 结算费
     * @param mxjsf 结算费
     */
    public void setMxjsf(BigDecimal mxjsf) {
        this.mxjsf = mxjsf;
    }

    /**
     * 取得 手续费
     * @return 手续费
     */
    public BigDecimal getMxsxf() {
        return mxsxf;
    }

    /**
     * 设置 手续费
     * @param mxsxf 手续费
     */
    public void setMxsxf(BigDecimal mxsxf) {
        this.mxsxf = mxsxf;
    }

    /**
     * 取得 结算参与人佣金
     * @return 结算参与人佣金
     */
    public BigDecimal getMxqsyj() {
        return mxqsyj;
    }

    /**
     * 设置 结算参与人佣金
     * @param mxqsyj 结算参与人佣金
     */
    public void setMxqsyj(BigDecimal mxqsyj) {
        this.mxqsyj = mxqsyj;
    }

    /**
     * 取得 其他费用
     * @return 其他费用
     */
    public BigDecimal getMxqtfy() {
        return mxqtfy;
    }

    /**
     * 设置 其他费用
     * @param mxqtfy 其他费用
     */
    public void setMxqtfy(BigDecimal mxqtfy) {
        this.mxqtfy = mxqtfy;
    }

    /**
     * 取得 资金金额
     * @return 资金金额
     */
    public BigDecimal getMxzjje() {
        return mxzjje;
    }

    /**
     * 设置 资金金额
     * @param mxzjje 资金金额
     */
    public void setMxzjje(BigDecimal mxzjje) {
        this.mxzjje = mxzjje;
    }

    /**
     * 取得 收付净额
     * @return 收付净额
     */
    public BigDecimal getMxsfje() {
        return mxsfje;
    }

    /**
     * 设置 收付净额
     * @param mxsfje 收付净额
     */
    public void setMxsfje(BigDecimal mxsfje) {
        this.mxsfje = mxsfje;
    }

    /**
     * 取得 成交日期
     * @return 成交日期
     */
    public String getMxcjrq() {
        return mxcjrq;
    }

    /**
     * 设置 成交日期
     * @param mxcjrq 成交日期
     */
    public void setMxcjrq(String mxcjrq) {
        this.mxcjrq = mxcjrq == null ? null : mxcjrq.trim();
    }

    /**
     * 取得 清算日期
     * @return 清算日期
     */
    public String getMxqsrq() {
        return mxqsrq;
    }

    /**
     * 设置 清算日期
     * @param mxqsrq 清算日期
     */
    public void setMxqsrq(String mxqsrq) {
        this.mxqsrq = mxqsrq == null ? null : mxqsrq.trim();
    }

    /**
     * 取得 交收日期
     * @return 交收日期
     */
    public String getMxjsrq() {
        return mxjsrq;
    }

    /**
     * 设置 交收日期
     * @param mxjsrq 交收日期
     */
    public void setMxjsrq(String mxjsrq) {
        this.mxjsrq = mxjsrq == null ? null : mxjsrq.trim();
    }

    /**
     * 取得 发送日期
     * @return 发送日期
     */
    public String getMxfsrq() {
        return mxfsrq;
    }

    /**
     * 设置 发送日期
     * @param mxfsrq 发送日期
     */
    public void setMxfsrq(String mxfsrq) {
        this.mxfsrq = mxfsrq == null ? null : mxfsrq.trim();
    }

    /**
     * 取得 其它日期
     * @return 其它日期
     */
    public String getMxqtrq() {
        return mxqtrq;
    }

    /**
     * 设置 其它日期
     * @param mxqtrq 其它日期
     */
    public void setMxqtrq(String mxqtrq) {
        this.mxqtrq = mxqtrq == null ? null : mxqtrq.trim();
    }

    /**
     * 取得 市场代码
     * @return 市场代码
     */
    public String getMxscdm() {
        return mxscdm;
    }

    /**
     * 设置 市场代码
     * @param mxscdm 市场代码
     */
    public void setMxscdm(String mxscdm) {
        this.mxscdm = mxscdm == null ? null : mxscdm.trim();
    }

    /**
     * 取得 交易方式
     * @return 交易方式
     */
    public String getMxjyfs() {
        return mxjyfs;
    }

    /**
     * 设置 交易方式
     * @param mxjyfs 交易方式
     */
    public void setMxjyfs(String mxjyfs) {
        this.mxjyfs = mxjyfs == null ? null : mxjyfs.trim();
    }

    /**
     * 取得 证券代码2
     * @return 证券代码2
     */
    public String getMxzqdm2() {
        return mxzqdm2;
    }

    /**
     * 设置 证券代码2
     * @param mxzqdm2 证券代码2
     */
    public void setMxzqdm2(String mxzqdm2) {
        this.mxzqdm2 = mxzqdm2 == null ? null : mxzqdm2.trim();
    }

    /**
     * 取得 托管单元2
     * @return 托管单元2
     */
    public String getMxtgdy2() {
        return mxtgdy2;
    }

    /**
     * 设置 托管单元2
     * @param mxtgdy2 托管单元2
     */
    public void setMxtgdy2(String mxtgdy2) {
        this.mxtgdy2 = mxtgdy2 == null ? null : mxtgdy2.trim();
    }

    /**
     * 取得 订单编号2
     * @return 订单编号2
     */
    public String getMxddbh2() {
        return mxddbh2;
    }

    /**
     * 设置 订单编号2
     * @param mxddbh2 订单编号2
     */
    public void setMxddbh2(String mxddbh2) {
        this.mxddbh2 = mxddbh2 == null ? null : mxddbh2.trim();
    }

    /**
     * 取得 错误代号
     * @return 错误代号
     */
    public String getMxcwdh() {
        return mxcwdh;
    }

    /**
     * 设置 错误代号
     * @param mxcwdh 错误代号
     */
    public void setMxcwdh(String mxcwdh) {
        this.mxcwdh = mxcwdh == null ? null : mxcwdh.trim();
    }

    /**
     * 取得 匹配号码
     * @return 匹配号码
     */
    public String getMxpphm() {
        return mxpphm;
    }

    /**
     * 设置 匹配号码
     * @param mxpphm 匹配号码
     */
    public void setMxpphm(String mxpphm) {
        this.mxpphm = mxpphm == null ? null : mxpphm.trim();
    }

    /**
     * 取得 附加说明
     * @return 附加说明
     */
    public String getMxfjsm() {
        return mxfjsm;
    }

    /**
     * 设置 附加说明
     * @param mxfjsm 附加说明
     */
    public void setMxfjsm(String mxfjsm) {
        this.mxfjsm = mxfjsm == null ? null : mxfjsm.trim();
    }

    /**
     * 取得 备用标志
     * @return 备用标志
     */
    public String getMxbybz() {
        return mxbybz;
    }

    /**
     * 设置 备用标志
     * @param mxbybz 备用标志
     */
    public void setMxbybz(String mxbybz) {
        this.mxbybz = mxbybz == null ? null : mxbybz.trim();
    }
}