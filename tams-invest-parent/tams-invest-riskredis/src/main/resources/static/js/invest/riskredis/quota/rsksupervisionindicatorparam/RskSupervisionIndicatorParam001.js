/**
 * RskSupervisionIndicatorParam001 指标参数管理
 */
var rsk = function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var paraName = $('#paraName').val();
        var paraType = $('#paraType').val();
        return {
            paraName : paraName,
            paraType : paraType
        }
    };
    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        checkbox:false,
        searchOnInit:true,
        /**
         * 双击查看
         */
        dblclick:function(table, data) {
        	if(data.paraType==FIX){
        		Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                        basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam006/init",
                        "65%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                            paraCode: data.paraCode});
        	}else if(data.paraType==CUSTOM){
        		Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                        basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam005/init",
                        "70%", "90%", {pageModel:PAGE_MODEL_REFERENCE,
                            paraCode: data.paraCode});
        	}else{
        		Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                        basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam002/init",
                        "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,
                            paraCode: data.paraCode});
        	}
            
        },
        cols:[
              {field:'validDate',visible: false}
            ,{field:'paraName',title: 'RskSupervisionIndicatorParam001.paraName'}
            ,{field:'paraCode',title: 'RskSupervisionIndicatorParam001.paraCode'}
            ,{field:'paraTypeStr',sortField:"paraType",title: 'RskSupervisionIndicatorParam001.paraType'}
            ,{field:'content',title: 'RskSupervisionIndicatorParam001.content',type: "number"}
            ,{field:'updateTime', title: 'RskSupervisionIndicatorParam001.updateTime',type:'date'}
            ,{field:'operation',width:200,title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    });
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        if(data.paraType==FIX){
        	Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndicatorParam001.edit"),
                    basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam006/init",
                    "65%", "80%", {pageModel:PAGE_MODEL_EDIT,
                        paraCode: data.paraCode},function() {
                        table.reload();
                    });
        }else if(data.paraType==CUSTOM){
        	Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndicatorParam001.edit"),
                    basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam005/init",
                    "70%", "90%", {pageModel:PAGE_MODEL_EDIT,
                        paraCode: data.paraCode},function() {
                        table.reload();
                    });
        	
        }else{

            Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndicatorParam001.edit"),
                    basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam002/init",
                    "95%", "90%", {pageModel:PAGE_MODEL_EDIT,
                        paraCode: data.paraCode},function() {
                        table.reload();
                    });
        }
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        data.operateType="delete";
        if(data.paraType==FIX){
        	Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam001/check', data, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                	WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam006/delete', data,
                    		"RskSupervisionIndicatorParam001_btn_delete","RskSupervisionIndicatorParam006",function(result){
                        table.reload();
                    });
                });
            });
        }else if(data.paraType==CUSTOM){
        	Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam001/check', data, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                	WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam005/delete', data,
                    		"RskSupervisionIndicatorParam001_btn_delete","RskSupervisionIndicatorParam005",function(result){
                        table.reload();
                    });
                });
            });
        	
        }else{
        	Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam001/check', data, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                	WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam001/delete', data,
                    		"RskSupervisionIndicatorParam001_btn_delete","RskSupervisionIndicatorParam002",function(result){
                        table.reload();
                    });
                });
            });
        }
       /* Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam001/check', data, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
            	WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam001/delete', data,
                		"RskSupervisionIndicatorParam001_btn_delete","RskSupervisionIndicatorParam002",function(result){
                    table.reload();
                });
            });
        });*/
    });
    /**
     * 绑定导出事件
     */
    var exportExcel = function() {
    	  var paramObj = getSearchParams();//请求参数json格式
        Joyin.exportExcel('export', paramObj);
    };

    /**
     * 画面加载完成
     */
    $(function() {
        // 绑定按钮事件
        if(paraNameInit != '' && paraNameInit != null){
            $('#paraName').val(paraNameInit);
        }
        $('#RskSupervisionIndicatorParam001_btn_search').click(function() {
            table.load();
        });
        $('#RskSupervisionIndicatorParam001_btn_export').click(function() {
            exportExcel();
        });
        $('#RskSupervisionIndicatorParam001_btn_yycs_create').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndicatorParam002"),
                basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_NEW},function () {
                    table.reload();
                });
        });
        $('#RskSupervisionIndicatorParam001_btn_gdz_create').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndicatorParam003"),
                basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam003/init",
                "65%", "80%", {pageModel:PAGE_MODEL_NEW},function () {
                    table.reload();
                });
        });
        $('#RskSupervisionIndicatorParam001_btn_zdy_create').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndicatorParam005"),
                basePath + "invest/riskredis/quota/rsksupervisionindicatorparam/RskSupervisionIndicatorParam005/init",
                "70%", "90%", {pageModel:PAGE_MODEL_NEW},function () {
                    table.reload();
                });
        });

        //资管跳转逻辑
        if(ssoUserName){
             $('#RskSupervisionIndicatorParam001_btn_search').click();
          }
    });

}();
