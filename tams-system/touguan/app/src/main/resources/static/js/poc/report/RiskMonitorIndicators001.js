(function(){
	
	/**
     * 获取查询参数
     */
    var getSearchParams = function() {
    	var param = $.serializeToObject('#searchForm');	
    	param.searchDate = $('#RiskMonitorIndicators001_date_searchdate').val();
    	//产品ID
        if(param.prodId){
            param.prodId = param.prodId.join(",");
        }
    	//产品经理
        if(param.manager){
            param.manager = param.manager.join(",");
        }
    	return param;
    };
   
   
 
    /**
     * 导出
     */
    var exportExcel = function(){ 
    	 var param = $.serializeToObject('#searchForm');
     	 param.yesOrNo1=$('#RiskMonitorIndicators001_yesOrNo').val();
         if (!param.yesOrNo1) {
         	Joyin.alert("是否穿透不能为空");
 		}
        Joyin.exportExcel('export',getSearchParams());
    };
    
    $('#RiskMonitorIndicators001_manager').change(function(){
		if ($('#RiskMonitorIndicators001_manager').val()) {
			 $('#statisticalMethod_div').show();  
			 $("#RiskMonitorIndicators001_statisticalMethod").val("01").trigger("change"); 
		}else{
			 $('#statisticalMethod_div').hide();
			 $('#RiskMonitorIndicators001_statisticalMethod').val("").trigger("change");
		} 
	});

	$(function() {
	    $('#RiskMonitorIndicators001_btn_search').hide();
	    $('#RiskMonitorIndicators001_btn_reset').hide();
	    $('#statisticalMethod_div').hide();
        $('#RiskMonitorIndicators001_btn_export').click(function() {
        	exportExcel();
        });     
	});
}());