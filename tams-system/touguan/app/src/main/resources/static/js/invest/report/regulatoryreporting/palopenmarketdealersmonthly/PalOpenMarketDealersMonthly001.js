$(function() {
	layui.use('laydate', function(){
		  var laydate = layui.laydate;
		  //执行一个laydate实例
		  laydate.render({
		    elem: '#statisticsMonth',//指定元素
		    type: 'month',
		    value: searchDataTime
		  });
		});
	
	/**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        	// 统计月份
        	statisticsMonth : $('#statisticsMonth').val() == null ? null : $('#statisticsMonth').val()
        }
    };
	
	var exportExcel = function() {
	    var paramObj = getSearchParams();
	    var statisticsMonth = paramObj.statisticsMonth;
		if (typeof (statisticsMonth) == "undefined" || statisticsMonth == null || statisticsMonth == "") {
			layer.alert('请选择统计月份！', {
				icon : 2,
				skin : 'layer-ext-moon'
			});
			return;
		}
	    Joyin.exportExcel('export', paramObj);
    };
	
	 var init = function(){
	    	//隐藏查询按钮
	    	$('#PalOpenMarketDealersMonthly001_btn_search').attr("style","display:none;");
	    }
	
	/**
     * 画面加载完成
     */
    $(function() {
    	init();
    	// 导出事件
    	$('#PalOpenMarketDealersMonthly001_btn_export').click(function() {
            exportExcel();
        });
    });
}());