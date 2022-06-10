$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        };
    };

    /**
     * 定义数据表格
     */
    var requiredTable=$("#requiredTable").joyintable({
        /*ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },*/
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
                "face":"30000",
                "netPrice":"97.3389",
                "expire":"3.6500",
                "right":"",
                "settleType":"",
                "settleWay":"",
                "settleDay":"",
                "accruedInterest":""
            },
            {
                "state":"收到",
                "face":"20000",
                "netPrice":"97.3391",
                "expire":"3.5600",
                "right":"",
                "settleType":"",
                "settleWay":"",
                "settleDay":"",
                "accruedInterest":""
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
    $(function() {
    	//绑定提交按钮
    	$(function() {
        	$("#UpTrInqSec001_btn_submit").click(function(){
        		Joyin.error("暂未开放上行接口");
        		return;
        	})
        });
    });
}());