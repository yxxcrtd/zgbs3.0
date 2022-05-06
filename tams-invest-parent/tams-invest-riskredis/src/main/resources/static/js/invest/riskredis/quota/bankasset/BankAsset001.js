/**
 * BankAsset001.js 商业银行净资产维护查询
 */
$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
    	var param = $.serializeToObject('#searchForm');
    	if(param.insitituteCode) {
        	param.insitituteCode = param.insitituteCode.join(",");
        }
        return param;
    };

    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({ 
        ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },
        checkbox:true,
        
        /**
         * 双击查看
         */
        dblclick: function (table, data) {
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/riskredis/quota/bankasset/BankAsset002/init",
                "60%", "75%", {
                    pageModel: PAGE_MODEL_REFERENCE,
                    customerId: data.customerId,
                    effDate: data.effDate,
                    valStatus : data.valStatus,
                    seqno : data.seqno
                });
        },
        /**
         * 定义表格名称
         */
        cols:[
             {field:'valStatusStr', title: 'BankAsset001.valStatus',sortField:'valStatus'}//有效状态
            ,{field:'customerId', title: 'BankAsset001.customerId'}//主体代码
             ,{field:'customerId', title: 'BankAsset001.customerId', visible: false}//主体代码
            ,{field:'customerName', title: 'BankAsset001.customerName'}//主体名称
            ,{field:'netValue',title: 'BankAsset001.netValue'}//净资产(亿元)
            ,{field:'effDateStr', title: 'BankAsset001.effDate'}//更新时间
            ,{field:'seqno', visible: false}
            ,{field:'status', visible: false}
            ,{field:'effDate', visible: false}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}//编辑和删除事件
        ]
    });
    
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function (data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/bankasset/BankAsset002/check', data, function (result) {
            Joyin.dialog("商业银行净资产维护编辑",
                basePath + "invest/riskredis/quota/bankasset/BankAsset002/init",
                "60%", "75%", {
                    pageModel: PAGE_MODEL_EDIT,
                    customerId: data.customerId,
                    effDate: data.effDate,
                    valStatus : data.valStatus,
                    seqno : data.seqno
                }, function () {
                    table.reload();
                });
        });
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function (data, index) {
        var object = {selectedItem: [data], operateType: "delete"};
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/bankasset/BankAsset001/check', object, function (result) {
            Joyin.confirm("请确认是否要删除?", function () {
                WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/bankasset/BankAsset001/delete', object,
                    "BankAsset002_btn_delete", "BankAsset002", function () {
                        table.reload();
                    });
            });
        });
    });
    
    /**
     * 绑定导出事件
     */
    var exportExcel = function () {
    	var paramObj = getSearchParams();//获取参数
        Joyin.exportExcel('export', paramObj);//导出
    };
    
    /**
     * 画面加载完成
     */
    $(function() {
        // 绑定按钮事件
        $('#BankAsset001_btn_search').click(function() {
        	var param = $.serializeToObject('#searchForm');
        	if( !param || !param.effDate){
        		Joyin.alert("请输入更新时间",2);
        		return;
        	}
        	table.load();
        });
        //绑定导出按钮
        $('#BankAsset001_btn_export').click(function() {
            exportExcel();//导出
        });
        //绑定新建按钮
        $('#BankAsset001_btn_add').click(function () {
            Joyin.dialog("商业银行净资产维护新建",
                basePath + "invest/riskredis/quota/bankasset/BankAsset002/init",
                "60%", "75%", {pageModel: PAGE_MODEL_NEW});
        });
        
        //绑定导入按钮
		$('#BankAsset001_btn_import').click(function() {
            Joyin.confirm(JoyinUtil.getLabel("请确认是否要导入数据？"), function() {
                Joyin.uploadFile(function(fileName) {
                    Joyin.ajax(basePath +"invest/riskredis/quota/bankasset/BankAsset001/importFile", {
                        fileName : fileName
                    },function(result){
                        if(!result || !($.isArray(result) && result.length > 0)) {
                            layui.layer.closeAll()
                            table.reload();
                        }
                    });
                });
            });
        });
    });
}());