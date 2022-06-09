package com.joyintech.tams.invest.common.risktrialrds.util;

import java.text.SimpleDateFormat;

public class Const {

	private Const() {}
	
	public  SimpleDateFormat DateFmt = new SimpleDateFormat("yyyy-MM-dd");
	public  SimpleDateFormat DateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final long MillisOfDay = 24 * 60 * 60 * (long)1000;
	
	 /**
     * 成功时的状态码
     */
    public static final String CodeSucc = "0";
    /**
     * 失败时的状态码
     */
    public static final String CodeFail = "1";
    /**
     * 成功时提示信息
     */
    public static final String MsgSucc = "操作成功";
    /**
     * 失败时提示信息
     */
    public static final String MsgFail = "操作失败";
    
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    public static final String BrankLowName="BBB以下";
    public static final String NoLowName="无评级";
    public static final String[] BrankLowValue={"BB+","BB","BB-","B+","B","B-","CCC","CC","C","F"};
    
    public static final String parAstCode="XeAstCode01";
    public static final String parAstName="XeAstName01";

    public static final String keyValueCn="RVALUECN";
    public static final String keyValue="RVALUE";
    public static final String keyAstName="RKEYCN";
    public static final String keyAstCode="RKEY";
    
    public static final String tradefix="_XEJK";

    public static final String IND_SPE_STAGE="IndividSpecialStage";

    public static final String IND_SPE_STAGE_AST="IndividSpecialStageAst";

    public static final String IND_AST_DEL="IndividAssetDel";

    public static final String SUP_PARAM="SupervisionParam";

    public static final String SUP_FREQUENCY="SupFrequency";

    public static final String PARAM="PARAM";

    public static final String EXPR="EXPR";

    public static final String UNITE="UNITE";

    public static final String PROD_LIST="ProdList";

    /**
     * 金融产品代码
     */
    public static final String finprodId="finprodId";

    /**
     * 金融产品分类_投管
     */
    public static final String finprodType2="finprodType2";

    /**
     * 交易方向
     */
    public static final String ps="ps";

    /**
     * 币种
     */
    public static final String CCY="ccy";
    
}
