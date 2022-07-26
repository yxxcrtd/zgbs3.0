(function(){
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var object =  {
            portfolioId: $('#portfolioId').val(),//组合代码
            partyId:$('#partyId').val()//机构代码
        }
		if(object.portfolioId){
            object.portfolioId = object.portfolioId.join(',');
        }
        return object;
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
        dblclick:function(table, data) {
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela002/init",
                    "98%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    portfolioId:data.portfolioId,partyId:data.partyId,cfetsAcctType:data.cfetsAcctType});//主键组合代码
        },
        /**
         * 定义表格名称
         */
        cols:[
            {field:'portfolioId', title: 'CfetsPortfolioAcctRela001.portfolioId', width:'10%'}//组合代码
            ,{field:'portfolioName', title: 'CfetsPortfolioAcctRela001.portfolioName', width:'20%'}//组合名称
            ,{field:'partyCustomerId', title: 'CfetsPortfolioAcctRela001.partyCustomerId', width:'20%'}//6位机构码
            ,{field:'partyId', title: 'CfetsPortfolioAcctRela001.partyId2', width:'10%'}//21位机构码
            ,{field:'partyName', title: 'CfetsPortfolioAcctRela001.partyName', width:'20%'}//机构名称
            ,{field:'cfetsAcctTypeStr', title: 'CfetsPortfolioAcctRela001.cfetsAcctType', width:'10%',sortField:"cfetsAcctType"}//账户类型
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt'], width:'10'}//编辑和删除事件
            
        ]
    });
    
    /**
     * 绑定编辑事件
     *//*
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        data.seqno = 'object';
        //组合创建点击编辑报分配用户bug修改 sjj add 2019/03/30 start
        data.userId = [1];
        //组合创建点击编辑报分配用户bug修改 sjj add 2019/03/30 end
        Joyin.ajaxJson(basePath + 'invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela002/check', data, function(result) {//数据验证
            Joyin.dialog(JoyinUtil.getLabel("CfetsPortfolioAcctRela002.edit"),
                basePath +"invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela002/init",
                "98%", "90%", {pageModel:PAGE_MODEL_EDIT,//page_model:编辑
                                portfolioId:data.portfolioId,partyId:data.partyId,cfetsAcctType:data.cfetsAcctType});//主键组合代码
        });
    });*/
    
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        // var object = data;
        // object.operateType = "delete";
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela002/check', object, function(result) {//数据验证
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(basePath + 'invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela002/delete', object, "CfetsPortfolioAcctRela001_btn_delete", "CfetsPortfolioAcctRela002");//删除
            });
        });
    });
    /**
     * 导出
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();//获取参数
        Joyin.exportExcel('export', paramObj);//导出
    };
    /**
     * 画面加载完成
     */
    $(function() {
        // 绑定查询按钮
        $('#CfetsPortfolioAcctRela001_btn_search').click(function() {
            table.load();//加载表格
        });
        //绑定到处按钮
        $('#CfetsPortfolioAcctRela001_btn_export').click(function() {
            exportExcel();//导出
        });
        //绑定新建按钮
        $('#CfetsPortfolioAcctRela001_btn_add').click(function() {
        	Joyin.dialog(JoyinUtil.getLabel("CfetsPortfolioAcctRela002.new"),
                    JoyinUtil.getRealPath("invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela002/init"),
                    "98%", "90%",{pageModel : PAGE_MODEL_NEW},function(){table.reload();}
            );
        });
        //绑定删除按钮
        $('#CfetsPortfolioAcctRela001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();//判断表格不为空，然后获取选择的数据
            }
            if (!selArr || selArr.length == 0) {//判断是否选择，如果没有则报错COMMON_E_0026
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var portfolioIdList = '';
            for(var i = 0; i<selArr.length;i++){
                portfolioIdList +=' ' + selArr[i].portfolioId;
            }
            var object = {selectedItem: selArr, operateType:"delete"};//请求参数
            Joyin.ajaxJson(basePath + 'invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela001/check', object, function(result) {//数据验证
                Joyin.confirm(JoyinUtil.getMessage('COMMON_C_0001',JoyinUtil.getMessage(JoyinUtil.getLabel("SysCommon.delete"))+'['+portfolioIdList+']'), function(){
                    WorkFlow.doDeleteList(basePath + 'invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela002/delete', object, "CfetsPortfolioAcctRela001_btn_delete", "CfetsPortfolioAcctRela002");//删除
                });
            });
        });
    });
}());