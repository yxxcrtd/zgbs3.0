$(function() {
	
	layui.use('laydate', function(){
		  var laydate = layui.laydate;
		  //执行一个laydate实例
		  laydate.render({
		    elem: '#exportYear',//指定元素
		    type: 'year',
		    value: searchDataEnd
		  });
		});
	
	var init = function(){
    	//隐藏查询按钮
    	$('#InvestmentsInfo001_btn_search').attr("style","display:none;");
    }
	
	/**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        	// 年份
        	exportYear : $('#exportYear').val() == null ? null : $('#exportYear').val(),
        	// 季度
        	exportQuarter : $('#exportQuarter').val() == null ? null : $('#exportQuarter').val()
        }
    };
	
    
    var exportExcel = function() {
	    var paramObj = getSearchParams();
	    var exportYear = paramObj.exportYear;
	    var exportQuarter = paramObj.exportQuarter;
		if (typeof (exportYear) == "undefined" || exportYear == null || exportYear == ""
			|| typeof (exportQuarter) == "undefined" || exportQuarter == null || exportQuarter == "") {
			layer.alert('请选择统计年份和季度！', {
				icon : 2,
				skin : 'layer-ext-moon'
			});
			return;
		}
	    Joyin.exportExcel('export', paramObj);
    };
	/**
     * 画面加载完成
     */
    $(function() {
    	init();
    	// 导出事件
    	$('#InvestmentsInfo001_btn_export').click(function() {
            exportExcel();
        });
    });
	
    
    
}());