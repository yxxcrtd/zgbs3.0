$(function() {
    

    /**
     * 定义数据表格
     */
    var requiredTable=$("#requiredTable").joyintable({
        /**
         * 定义表头
         */
        searchOnInit: true,
        page:false,
        cols:[
            {field:'state',  title: '状态'},//状态
            {field:'face',  title: '券面（万元）'},//券面（万元）
            {field:'netPrice',  title: '净价（元）'},//净价（元）
            {field:'expire',  title: '到期（%）'},//到期（%）
            {field:'right',  title: '行权（%）'},//行权（%）
            {field:'settleType',  title: '清算类型'},//清算类型
            {field:'settleWay',  title: '结算方式'},//结算方式
            {field:'settleDay',  title: '结算日'},//结算日
            {field:'accruedInterest',  title: '应计利息（元）'}//应计利息（元）
        ],
        data:[
            {
                "state":"收到",
                "face":"800000",
                "netPrice":"99.2",
                "expire":"2.69",
                "right":"",
                "settleType":"净额清算",
                "settleWay":"券款对付",
                "settleDay":"2019-12-18",
                "accruedInterest":"63.50"
            },
            {
                "state":"收到",
                "face":"500000",
                "netPrice":"99.4",
                "expire":"2.62",
                "right":"",
                "settleType":"净额清算",
                "settleWay":"券款对付",
                "settleDay":"2019-12-18",
                "accruedInterest":"52.23"
            },
            {
                "state":"收到",
                "face":"800000",
                "netPrice":"99.5",
                "expire":"2.57",
                "right":"",
                "settleType":"净额清算",
                "settleWay":"券款对付",
                "settleDay":"2019-12-18",
                "accruedInterest":"63.50"
            },
            {
                "state":"收到",
                "face":"700000",
                "netPrice":"99.6",
                "expire":"2.55",
                "right":"",
                "settleType":"净额清算",
                "settleWay":"券款对付",
                "settleDay":"2019-12-18",
                "accruedInterest":"61.45"
            }
        ]
    });
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.dialog(JoyinUtil.getLabel("准入池新建"),
            basePath + "risk/admission/admissionassetpool/AdmissionAssetPool003/init",//打开新界面
            "75%", "45%",function(){
                table.reload();//回调函数，表格刷新
            });
    });
	    /**
	     * 画面加载完成
	     */
    	//绑定提交按钮
    	$(function() {
        	$("#Do_btn_success").click(function(){
        		Joyin.error("暂未开放上行接口");
        		return;
        	})
        });

}());