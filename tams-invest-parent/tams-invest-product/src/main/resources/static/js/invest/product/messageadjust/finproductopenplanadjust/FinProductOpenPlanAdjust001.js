/**
 * huwenbin
 * 信息调整管理_开放计划查询
 */
(function() {

    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
    	var finprodId = '';
    	var scheduleType = $('#scheduletype').val();
    	if($('#finprodId').val() == null || $('#finprodId').val() == ''){
    		finprodId = $('#finprodId').val();
    	}
    	else{
    		//finprodId = $('#finprodId').select2('data')[0].object.join(',');
    		$.each($('#finprodId').select2('data'),function(index,obj){
    			finprodId = finprodId+"," + obj.object;
    		})
    	}
        return {
            finprodId: finprodId,
            scheduleType: scheduleType,
            openDateStrStart: $('#openDateStr').startDate(),
            openDateStrEnd: $('#openDateStr').endDate()
            /*effDateStart: $('#FinProductOpenPlanAdjust001_effDate').startDate(),
            effDateEnd: $('#FinProductOpenPlanAdjust001_effDate').endDate()*/
        }
    };
    /**
     * check查询条件和计划类型
     */
    var checkAndLoadTable = function () {
        OPEN_TYPE = '';
        if($('#finprodId').val() != null && $('#finprodId').val() != ''){
            $.each($('#finprodId').select2('data'),function(index,obj){
                if(RANSOM_ISSUE_MODE_03 != obj.col){
                    OPEN_TYPE = '01';
                }
            })
        } else {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0009",JoyinUtil.getLabel("FinProductOpenPlanAdjust001.finprodId")));
            return;
        }
        //计划类型check 申赎不同频时必填
       if(OPEN_TYPE == '01' && ($('#scheduletype').val() == null || $('#scheduletype').val() == '')){
           // Joyin.error(JoyinUtil.getMessage("COMMON_E_0009",JoyinUtil.getLabel("FinProductOpenPlanAdjust001.scheduleType")));
           // return;
       } 
       table.load();
    }
    
    /**
     * 定义数据表格
     */
    var table = $("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function () {
                return getSearchParams();
            }
        },
        /*dblclick:function(table, data) {
            data.pageModel=PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("FinProductRateQuotation002"),
                    JoyinUtil.getRealPath("invest/product/messageadjust/finproductratequotation/FinProductRateQuotation002/init"),
                    "80%", "90%", data,
                    function(){table.reload();});
        },*/
//        rowno: true,
        cols: [
            {field: 'num', title: 'FinProductOpenPlanAdjust001.num',sortField:'NUM'}
            ,{field: 'finprodMarketId', title: 'FinProductOpenPlanAdjust001.finprodId', width:200, sortField:'FINPROD_MARKET_ID'}
            , {field: 'finprodAbbr', title: 'FinProductOpenPlanAdjust001.finprodAbbr', width:200, sortField:'FINPROD_ABBR'}
            , {field: 'openDateStr', title: 'FinProductOpenPlanAdjust001.openDateStr', type:'date', width:200,sortField:'OPEN_DATE_STR'}
            , {field: 'openDateEnd', title: 'FinProductOpenPlanAdjust001.openDateEnd', type:'date', width:200,sortField:'OPEN_DATE_END'}
            , {field: 'openVdate', title: 'FinProductOpenPlanAdjust001.openVdate', type:'date', width:200,sortField:'OPEN_VDATE'}
            , {field: 'openNdate', title: 'FinProductOpenPlanAdjust001.openNdate', type:'date', width:200,sortField:'OPEN_NDATE'}
            , {field: 'scheduleTypeStr', title: 'FinProductOpenPlanAdjust001.scheduleType', width:200,sortField:'OPEN_TYPE'}
            /*, {field: 'effDateStr', title: 'FinProductOpenPlanAdjust001.effDate', type:'date'}
            , {field: 'finprodId', visible:false}
            , {field: 'effDate', visible:false}
            , {field: 'cashId', visible:false}
            , {field: 'isInitial', visible:false}*/
            , {field:'operation', title: 'SysCommon.operation', visible:false,  type:'action', buttons: ['edit', 'delt']}//编辑和删除事件
        ]
    });
    
    /**
     * 明细查看
     */
    /*table.bind('click', '.edit', function(data, index) {
        Joyin.dialog(JoyinUtil.getLabel("FinPayInterestStdInfo.plan.title"),
                basePath + "invest/product/messageadjust/finproductratequotation/FinProductRateQuotation002/init",//打开新界面
                "80%", "90%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
        						finprodId:data.finprodId,
        						cashId:data.cashId,
        						effDateStr:data.effDateStr
        						},function(){
                                    table.reload();//回调函数，表格刷新
                                }); 
    });*/
    /**
     * 绑定删除事件
     */
    /*table.bind('click', '.delt', function(data, index) {
    	//发起流程
    	var object = {selectedItem: [data], operateType:"delete", finprodMarketId : data.finprodMarketId, effDate : data.effDate};
    	data.operateType = 'delete';
    	Joyin.ajaxJson(basePath + 'invest/product/messageadjust/finproductratequotation/FinProductOpenPlanAdjust001/check', object, function(result) {
	        Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
	        	WorkFlow.doDeleteList(basePath + 'invest/product/messageadjust/finproductratequotation/FinProductOpenPlanAdjust001/delete', object, "FinProductRateQuotation002_btn_delete", "FinProductOpenPlanAdjust001");
	        });
    	})
    });*/
    
    
    //导出
    var exportExcel = function () {
        Joyin.exportExcel('export', getSearchParams());
    };

    /**
     * 弹出新建画面
     * @returns
     */
    /*function showDialog() {
    	Joyin.dialog(JoyinUtil.getLabel("FinProductRateQuotation002"), basePath
                + "invest/product/messageadjust/finproductratequotation/FinProductRateQuotation002/init",
                "80%", "90%", {pageModel : PAGE_MODEL_NEW},function(){table.reload();});
    }*/
    /**
     * 画面加载完成
     */
    $(function() {
        // 绑定按钮事件
    	//查询
    	$('#FinProductOpenPlanAdjust001_btn_search').click(function(){
    	    checkAndLoadTable();
    	});
    	//
        /*$('#FinProductOpenPlanAdjust001_btn_add').click(function(){
        	showDialog();
        });*/
        //导出
        $('#FinProductOpenPlanAdjust001_btn_export').click(function() {
            exportExcel();
        });
        //批量删除
        /*$('#FinProductOpenPlanAdjust001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'invest/product/messageadjust/finproductratequotation/FinProductOpenPlanAdjust001/check', object, function(result) {														
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(basePath + 'invest/product/messageadjust/finproductratequotation/FinProductOpenPlanAdjust001/delete', object, "FinProductRateQuotation002_btn_delete", "FinProductOpenPlanAdjust001");
                });
            });
        });*/
    });
}());