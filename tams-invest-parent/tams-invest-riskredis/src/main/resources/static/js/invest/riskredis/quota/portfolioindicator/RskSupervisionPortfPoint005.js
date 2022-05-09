(function() {
	/**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        	portfolioName: $('#portfolioName').val(),
        	investNature: $('#investNature').val(),
        	raiseType: $('#raiseType').val(),
        	openType: $('#issueMode').val(),
        	profitType: $('#profitType').val(),
        	portfolioType: $('#portfolioType').val(),
        	ptlBagId:ptlBagId,
            isRule:isRule
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
        
        cols:[
             {field:'portfolioId', width:120 , title: 'RskSupervisionPortfPoint005.portfolioId'}
            ,{field:'portfolioName', width:280 , title: 'RskSupervisionPortfPoint005.portfolioName'}
            ,{field:'vdate', width:120 ,  type:'date',title: 'RskSupervisionPortfPoint005.vdate'}
            ,{field:'mdate', width:120 ,  type:'date',title: 'RskSupervisionPortfPoint005.mdate'}
            ,{field:'profitTypeStr', width:110 ,sortField:"profitType",title: 'RskSupervisionPortfPoint005.profitType'}
            ,{field:'openTypeStr', width:110 ,sortField:"openType",title: 'RskSupervisionPortfPoint005.openType'}           
            ,{field:'raiseTypeStr', width:110 , sortField:"raiseType",title: 'RskSupervisionPortfPoint005.raiseType'}
            ,{field:'investNatureStr',  sortField:"investNature",title: 'RskSupervisionPortfPoint005.investNature'}
        ]
    });    
 
    /**
     * 绑定导出事件
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();
        Joyin.exportExcel('export', paramObj);
    };

    
    /**
     * 画面加载完成
     */
    $(function() {
    	// 绑定按钮事件
        $('#RskSupervisionPortfPoint005_btn_search').click(function() {
            table.load();
        });
        $('#RskSupervisionPortfPoint005_btn_export').click(function() {
            exportExcel();
        });
        $('#RskSupervisionPortfPoint005_btn_search').click();
    });
}());