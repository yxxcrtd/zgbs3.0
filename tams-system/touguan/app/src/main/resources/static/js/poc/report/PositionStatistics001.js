(function(){
	
	/**
     * 获取查询参数
     */
    var getSearchParams = function() {
    	var param = $.serializeToObject('#searchForm');	
    	param.searchDate = $('#PositionStatistics001_date_searchdate').val();
    	//产品经理
        if(param.manager){
            param.manager = param.manager.join(",");
        } 
    	return param;
    };
   
    var dataTable=$("#dataTable").joyintable({
		ajax: {
	         url: 'findPage',
	         type: 'POST',
	         data: function() {
	             return getSearchParams();
	         }
	     }, 
	   checkbox: false,
       searchOnInit: false,
       cellMinWidth:100 ,
       isMulti:true,
       cols: [[
            {field:'cdate', title: '统计日期',width:150, rowspan: 2,sortable: false}//统计日期
            ,{title: '现金流入',colspan: 9,sortable: false}//市值占比%
            ,{title: '现金流出',colspan: 7,sortable: false}//市值占比%
            ,{field:'cleancashflow', title: '净现金流', width: 80, rowspan: 2,sortable: false}//净现金流 //统计日期
            ],[
	            {field:'prodestablish', title: '产品成立',width:90,sortable: false}//产品成立
	           ,{field:'nhgexpire', title: '逆回购到期', width:120,sortable: false}//逆回购到期
	           ,{field:'zhgsq', title: '正回购首期', width:120,sortable: false}//正回购首期
	           ,{field:'ccexpire', title: '拆出到期', width:120,sortable: false}//拆出到期
	           ,{field:'crsq', title: '拆入首期', width:120,sortable: false}//拆入首期
	           ,{field:'assetsale', title: '资产卖出',width:90, sortable: false}//资产卖出
	           ,{field:'assetexpire',title: '资产到期',width:100, sortable: false}//资产到期
	           ,{field:'assetpayments', title: '资产中间收息', width:80,sortable: false}//资产中间收息
	           ,{field:'calclr', title: '小计',width:80, sortable: false}//小计 
             ,{field:'prodexpire',  title: '产品到期',width: 80, sortable: false}//数量
             ,{field:'nhgsq',title: '逆回购首期',  width: 80, sortable: false}//逆回购首期
             ,{field:'zhgexpire', title: '正回购到期',width: 80,  sortable: false}//正回购到期
             ,{field:'ccsq',  title: '拆出首期', width: 80,sortable: false}//拆出首期
             ,{field:'crexpire', title: '拆入到期', width: 80, sortable: false}//拆入到期
             ,{field:'assetbuy', title: '资产买入', width: 80, sortable: false}//资产买入
             ,{field:'calclc', title: '小计', width: 80, sortable: false}//小计 
          ]]
   });
 
    /**
     * 导出
     */
    var exportExcel = function(){
    	var param = $.serializeToObject('#searchForm');	
    	//获取查询日期
        if ($('#PositionStatistics001_date_searchdate').val() != null && $('#PositionStatistics001_date_searchdate').val() != '') {
        	var start = new Date($('#PositionStatistics001_date_searchdate').val().replace("-", "/").replace("-", "/"));
        	var end = new Date($('#platformDate').val().replace("-", "/").replace("-", "/")); 
        	if ( start < end ) {
        		param.searchdate = $("#searchdate").val(); 
			} else{
				 Joyin.error("查询日不能大于平台日");
				 return;
			}
        }else{
            Joyin.error("查询日不能为空");
            return;
        }
        Joyin.exportExcel('export',getSearchParams());
    };
    
   

	$(function() {
	    $('#PositionStatistics001_btn_search').click(function() { 
	    	dataTable.load();
        });
        $('#PositionStatistics001_btn_export').click(function() {
        	exportExcel();
        });     
	});
}());