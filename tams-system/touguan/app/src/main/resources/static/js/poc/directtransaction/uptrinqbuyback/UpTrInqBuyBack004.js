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
    var table=$("#dataTable").joyintable({
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
        cols:[
            {field:'quotationNum',  title: '选择'}//选择
            ,{field:'quotationState',title:'债券代码'}//债券代码
            ,{field:'tradeState',title:'债券名称'}//债券名称
            ,{field:'proName',title:'券面总额（万元）'}//券面总额（万元）
            ,{field:'tradeDirection', title:'折算比例（%）'}//折算比例（%）
        ]
    });


    /**
     * 画面加载完成
     */
    $(function() {
        //添加质押券
        $('#UpTrInqBuyBack002_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("质押券添加"),
                basePath + "invest/trade/uptrinqbuyback/UpTrInqBuyBack005/init",
                "98%", "98%");
        });
        
        //绑定提交按钮
    	$("#UpTrInqBuyBack004_btn_suc").click(function(){
    		Joyin.error("暂未开放上行接口");
    		return;
    	})
        
    });
}());