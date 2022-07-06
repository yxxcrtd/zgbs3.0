$(function () {
	/**
     * 明细查询获取查询参数
     */
    var getSearchParams = function () {
    	var result = $.serializeToObject('#searchForm');
    	//add by 张年  2019/08/09  start
        //获取查询日期
        if ($("#searchDate").val() != null && $("#searchDate").val() != '') {
            // if ($("#searchDate").endDate() > sysDate) {
            //     Joyin.error("日期必须小于等于平台日");
            //     return;
            // }
            result.searchDateStart = $("#searchDate").startDate();
            result.searchDateEnd = $("#searchDate").endDate();
        }else{
            Joyin.error("现金流发生日不能为空");
            return;
        }
        //add by 张年  2019/08/09  end
        var temp=result.sex;
        //获取查询资金账户代码
        if($("#portfolioId").val() == null || $("#portfolioId").val() == ''){
            result.portfolioId = $("#portfolioId").val();
        }
        else{
            result.portfolioId = $("#portfolioId").val().join(',');
        }
        //获取查询组合账户
        if($("#fundAccount").val() == null || $("#fundAccount").val() == ''){
            result.fundAccount = $("#fundAccount").val();
        }
        else{
            result.fundAccount = $("#fundAccount").val().join(',');
        }
        //获取金额显示精度
        if($("#displayAccuracy").val() != null || $("#displayAccuracy").val() != ''){
            result.displayAccuracy = $("#displayAccuracy").val();
        }
      //获取币种
        if($("#ccy").val() != null || $("#ccy").val() != ''){
            result.ccy = $("#ccy").val();
        }
        // add by wangyang1 【吉林银行】 增加 产品经理  2019年9月29日 start
        if($("#prodManager").val() == null || $("#prodManager").val() == ''){
            result.prodManager = $("#prodManager").val();
        }
        else{
            result.prodManager = $("#prodManager").val();
        }
        // add by wangyang1 【吉林银行】 增加 产品经理  end


        //获取现金流类型 20191231 Add by xkni begin
        if($("#cashType").val() == null || $("#cashType").val() == ''){
            result.cashType = $("#cashType").val();
        }
        else{
            result.cashType = $("#cashType").val().join(',');
        }
        //获取现金流类型 20191231 Add by xkni end
        if($("#ps").val() == null || $("#ps").val() == ''){
            result.ps = $("#ps").val();
        }
        else{
            result.ps = $("#ps").val().join(',');
        }
        //add by swwan [重庆银行] 组合系列 20190722 start
        if($("#portfolioSeries").val() == null || $("#portfolioSeries").val() == ''){
            result.portfolioSeries = $("#portfolioSeries").val();
        } else {
            result.portfolioSeries = $("#portfolioSeries").val().join(',');
        }
        //add by swwan [重庆银行] 组合系列 20190722 end
        return result;
    };
    
    
    /**
     * 汇总获取查询参数
     */
    var getSearchParams1 = function () {
    	var result = $.serializeToObject('#searchForm1');
    	//获取查询日期
        if ($("#searchDate1").val() != null && $("#searchDate1").val() != '') {
            result.searchDateStart1 = $("#searchDate1").startDate();
            result.searchDateEnd1 = $("#searchDate1").endDate();
        }else{
        	Joyin.error("现金流发生日不能为空");
        	return;
        } 
        var temp=result.sex;
        //获取查询组合账户代码
        if($("#portfolioId1").val() == null || $("#portfolioId1").val() == ''){
            result.portfolioId1 = $("#portfolioId1").val();
        }
        else{
            result.portfolioId1 = $("#portfolioId1").val().join(',');
        }
        //获取查询资金账户信息
        result.fundAccount1 = $("#fundAccount1").val();
        //获取金额显示精度
        if($("#displayAccuracy1").val() != null || $("#displayAccuracy1").val() != ''){
            result.displayAccuracy1 = $("#displayAccuracy1").val();
        }
      //获取币种
        if($("#ccy1").val() != null || $("#ccy1").val() != ''){
            result.ccy1 = $("#ccy1").val();
        }
        //获取汇总维度
        if($("#aggregDimension").val() != null || $("#aggregDimension").val() != ''){
            result.aggregDimension = $("#aggregDimension").val();
        }
        // add by wangyang1 【吉林银行】 增加 产品经理  2019年9月29日 start
        if($("#prodManager1").val() == null || $("#prodManager1").val() == ''){
            result.prodManager1 = $("#prodManager1").val();
        }
        else{
            result.prodManager1 = $("#prodManager1").val();
        }
        // add by wangyang1 【吉林银行】 增加 产品经理  end
        //add by swwan [重庆银行] 组合系列 20190722 start
        if($("#portfolioSeries1").val() == null || $("#portfolioSeries1").val() == ''){
            result.portfolioSeries1 = $("#portfolioSeries1").val();
        } else {
            result.portfolioSeries1 = $("#portfolioSeries1").val().join(',');
        }
        //add by swwan [重庆银行] 组合系列 20190722 end
        return result;
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
        cols: [
              {field: 'cDate',type:'date', title: '日期'}
            , {field: 'portfolioId', title: '组合代码'}
            , {field: 'fundAccount', title: '资金账户'}
            , {field: 'cashFlowTypeStr', title: '类型', sortField: 'cashFlowType'}
            , {field: 'psStr', title: '交易方向', sortField: 'cashType'}
            , {field: 'feetypeStr', title: '费用类型', sortField: 'feetype'}
            , {field: 'assetSign', title: '资产标的'}
            , {field: 'cashFlow', title: '现金流', width: 200, type: 'number'}
            , {field: 'ccyStr', title: '币种', sortField: 'cashFlow'}
            , {field: 'portfolioSeriesStr', title: '组合系列', sortField: 'portfolioSeries'}
        ]
    });
    

    var dataTable1=$("#dataTable1").joyintable({
         ajax: {
             url: 'findPage',
             type: 'POST',
             data: function() {
                 return getSearchParams1();
             }
         },
        checkbox: false,
        searchOnInit: false,
        cols:[
            {field:'aggregDimension',  title: '汇总维度', width:100}
            ,{field:'portfolioId',  title: '组合代码',  width:300}
            ,{field:'ccyStr',title:'币种',  sortField: 'ccy', width:150}
            ,{field:'fundAccount',title:'资金账户', width:300}
            ,{field:'inCashflow',title:'流入现金流', width:200, type: 'number'}
            ,{field:'outCashflow',title:'流出现金流', width:200, type: 'number'}
            ,{field:'netCashflow',title:'净现金流', width:200, type: 'number'}
            ,{field:'portfolioSeriesStr', title: '组合系列', sortField: 'portfolioSeries', width:300}
        ],
    });
    
    /**
     * 导出明细数据
     */
    var exportExcel = function(index) {
        var paramObj = getSearchParams();//获取参数
        if (paramObj){
            paramObj.tab = index;
            Joyin.exportExcel('export', paramObj);//导出
        }         
    };
    
    /**
     * 导出汇总数据
     */
    var exportExcel1 = function(index) {
        var paramObj = getSearchParams1();//获取参数
        if (paramObj){
            paramObj.tab = index;
            Joyin.exportExcel('export', paramObj);//导出
        }        
    };
    
    /**
     * 【吉林银行个性化代码】
     * 2019年10月10日
     * 导出明细数据
     */
    var detailExportExcel = function(index) {  
    	var paramObj = getSearchParams();//获取参数
        if (paramObj){
            paramObj.tab = index;
            Joyin.exportExcel( "detailExport",paramObj);  
        }        
    };
    
    
    $(function () {
        $(".detail-form-wrap .form-search-btns .btn-search").click(function () {
            /*$(".detail-table").css("display","block");
            $(".detail-table1").css("display","none");*/
        	dataTable.load();
        });
        $(".total-form-wrap .form-search-btns .btn-search").click(function () {
           /* $(".total-table").css("display","block");
            $(".total-table1").css("display","none");*/
        	dataTable1.load();
        }); 
        $(".export_total #PtlCashFlowGapAnalysis001_btn_export").click(function(){
        	var index = '1';
        	exportExcel1(index);
        }); 
        // add by wangyang1 【吉林银行】 增加 导出会总行  2019年9月29日 start 
		$(".export_detail #PtlCashFlowGapAnalysis001_btn_export").click(function(){ 
        	detailExportExcel('0');
        });  
//	    $(".export_detail #PtlCashFlowGapAnalysis001_btn_export").click(function(){
//        	var index = '0';
//        	exportExcel(index);
//        });  
		// add by wangyang1 【吉林银行】 增加 导出会总行  end 
		  
    })
});
