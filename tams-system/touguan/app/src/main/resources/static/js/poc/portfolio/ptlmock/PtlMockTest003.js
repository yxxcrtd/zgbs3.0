(function () { 
	/**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var objDiv= $("#PtlMockTest003_DIV");
        var param = $.serializeToObject('#submitForm');
        param.mdateStart = objDiv.find("input[name='mdatep']").startDate();
        param.mdateEnd = objDiv.find("input[name='mdatep']").endDate();
        param.mdateStartzc = objDiv.find("input[name='mdate']").startDate();
        param.mdateEndzc = objDiv.find("input[name='mdate']").endDate()
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
        /**
         * 定义表头
         */
        cols:[
	         {field:'portfolioId',title:'组合代码',width:150}
	        ,{field:'portfolioNamep',title:'组合名称',width:150}
	        ,{field:'profitTypeStr',title:'收益类型',sortField:'PROFIT_TYPE',width:150}
            ,{field:'mdatep',title:'组合到期日',type:'date',width:150}
            ,{field:'type8Str',title:'资产类型',sortField:'PINAN_ASSET_TYPE',width:150}
            ,{field:'finprodId',  title: '资产代码',sortField:'FINPROD_MARKET_ID',width:200}//资产代码
            ,{field:'finprodAbbr',  title: '资产名称',sortField:'FINPROD_ABBR',width:200}//资产简称
            ,{field:'mdate',title:'资产到期日',type:'date',width:150}//到期日
            ,{field:'invAimStr', title:'投资目的',sortField:'INV_AIM',width:150}
            ,{field:'amountp',title:'持仓数量',type:'number',width:150}
            ,{field:'shareAmt',title:'试算占用数量',type:'number',width:150}
            ,{field:'portfolioName',title:'试算占用组合',width:150}
            ,{field:'price',title:'净价',type:'number',width:150}
            ,{field:'tdyIntincexp',title:'应计利息',type:'number',width:150}
            ,{field:'yield',title:'到期收益率%',type:'number',width:150}
            ,{field:'exerYield',title:'行权收益率%',type:'number',width:150}
        ]
    });
    /**
     * 画面加载完成
     */
    $(function() {
        //初始化查询
        $('#PtlMockTest003_btn_search').click(function() {
            table.load();
        });
        $('#PtlMockTest003_btn_submit').click(function() {
            var selArr = null;
            if (table != null && table != '') {
                selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            top.returnData = selArr;
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });
        $('#PtlMockTest003_btn_filter').hide();
        $('#PtlMockTest003_btn_export').hide();
    });
}());