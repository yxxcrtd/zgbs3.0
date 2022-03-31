$(function () {

	/**
	 * 修改/新建
	 */
	var saveOrSubmit = function(operateType) {
		var object = $.serializeToObject('#dataForm');
		object.pageModel = pageModel;
		object.operateType = operateType;
		
//		var dto = $.serializeToObject('#dataForm');
//		dto.pageModel = pageModel;
//		dto.operateType = operateType;
//		var listdto=[dto];
//        var object = {};
//        object.listValuationMethodSchemeDto = listdto;
		
		Joyin.ajaxJson(basePath + 'valuation/valuationparametermanage/valuationmethodscheme/ValuationMethodScheme004/check', object, function(result) {
		    Joyin.confirm(result.data, function() {
		    	if(operateType === 'save'){
				    WorkFlow.doSaveList(basePath + 'valuation/valuationparametermanage/valuationmethodscheme/ValuationMethodScheme004/save', 
								object, "ValuationMethodScheme004_btn_save");
				}else{// submit
					WorkFlow.doSubmitList(basePath + 'valuation/valuationparametermanage/valuationmethodscheme/ValuationMethodScheme004/save', 
							object, "ValuationMethodScheme004_btn_submit");
				}
		    });
		});
	};
	
	/**
     * 绑定联动
     */
    var linkBind=function() {
//    	联动：估值第一市价不可和估值第二市价相同
    	$("#valuationPrice").change(function(){
        	var valPrice = $("#valuationPrice").val();
        	var num = $("#select2-valuationPrice-results").find("li").size();
        	if(num >0 && valPrice == valPrice2){
        		$("#valuationPrice").val("").trigger("change");// select清空
            	layer.msg("估值第一市价不可和估值第二市价相同！");
        	}
        });

        $("#quotaDigit").change(function(){
        	var quotaDigit = $('#quotaDigit').val();
            if(quotaDigit<0 || quotaDigit>12){
                layer.alert('[0,12]的整数！');
                $('#quotaDigit').val('');
                return ;
                }
        });
        
//        绑定联动：资产代码-资产名称-交易市场
        $("#finprodId").change(function(){
			  //var s_finprodId =  $("#select2-finprodId-container").attr("title").split("(")[0];
        	  // finprodId取原始的资产代码
			  var s_finprodId = $("#finprodId").select2('data')[0].object;
			  if(!(typeof(s_finprodId) == 'undefined')){
				  Joyin.ajax(
							basePath + 'valuation/valuationparametermanage/valuationmethodscheme/ValuationMethodScheme004/getFinprodInfo', 
							{finprodId : s_finprodId},
							function (data) {
								  $("#finprodName").val(data.finprodName);
								  $("#finprodType").val(data.finprodType).trigger("change");
								  $("#tradeMarket").val('').trigger("change");
								  $("#tradeMarket").textSelect('setValue',data.tradeMarket);
								  if (data.finprodType === 'F17'){
									  $("#div_tradeMarket").hide();
								  }else {
									  $("#div_tradeMarket").show();
								  }
					  });
			  }
			  $("#valuationPrice").val('').trigger("change");
        });
        
        
//        估值方法为市值法/市值成本孰低法 显示：估值第一市价、估值第二市价、行情日期、行情位数、行情位数处理
        $("#valuationMethod").change(function(){
        	valMethodLinkShow();
        });
        //交易场所
		$("#tradeMarket").change(function(){
			priceClassShow();
		});
 //     估值方法为市值法/市值成本孰低法且估值市价为收盘价、平均价、最高价、最低价、卖一价或者买一价时显示价格类型
        $("#valuationPrice").change(function(){
            priceClassShow();
        });
        $("#invAim").change(function(){
			invAimChange();
        });
    }

    var invAimChange = function () {
		if ($("#finprodType").val() === 'F17' && $("#invAim").val() === 'FVTPL') {
			$("#valuationMethod").val('01').trigger("change");
		}
		if ($("#finprodType").val() === 'F17' && $("#invAim").val() === 'FVOCI') {
			$("#valuationMethod").val('01').trigger("change");
		}
	};

    // 估值方法为市值法/市值成本孰低法 显示：估值第一市价、估值第二市价、行情日期、行情位数、行情位数处理
    var valMethodLinkShow = function (){
        var valMethod = $("#valuationMethod").val();
        if(valMethod == '01' || valMethod == '03'){
        	$("#c_valuationPrice").show();
        	$("#c_valuationDate").show();
        	$("#c_quotaDigit").show();
			$("#c_priceTailMode").show();
//    		$("#c_priceClass").show();
        }else{// 隐藏并且置空
        	$("#c_valuationPrice").hide();
        	$("#c_valuationDate").hide();
        	$("#c_quotaDigit").hide();
			$("#c_priceTailMode").hide();
    		$("#c_priceClass").hide();
        	
        	$("#valuationPrice").val("").trigger("change");
        	$("#valuationDate").next().find("li").removeClass("laytable-checkbox-selected");
        	$("#valuationDate").val("");
        	$("#quotaDigit").val("");
			$("#priceTailMode").val("");
			$("#c_priceClass").val("");
        }
		if ($("#finprodType").val()){
			if ($("#finprodType").val() === 'F17'){
				$("#div_tradeMarket").hide();
			}else {
				$("#div_tradeMarket").show();
			}
		}
    };
    
    // 01-债券,估值方法为市值法/市值成本孰低法且估值市价为收盘价、平均价、最高价、最低价、卖一价或者买一价时显示价格类型
    var priceClassShow = function (){
            var valMethod = $("#valuationMethod").val();
            var valuationPrice = $("#valuationPrice").val();
            var finprodId = $("#finprodId").val();
            var tradeMarket = $("#tradeMarket").val();
            var secType="";
            if(finprodId){
				var object = {finprodId:finprodId};
				//这里改为同步，防止后一步取不到值
				Joyin.ajaxSync(basePath + 'valuation/valuationparametermanage/valuationmethodscheme/ValuationMethodScheme004/getSecTypeByFinprodId', object, function(result) {
					secType=result.data;
				});
			}
			if(secType === '01' && valMethod == '01' && (tradeMarket=='03' || tradeMarket=='04')){
				if(['01','02','03','04','05','06','CBC','CSD','SCH'].indexOf(valuationPrice) > -1 ){
					$("#c_priceClass").show();
				}else{
					$("#c_priceClass").hide();
					$("#priceClass").val("");
				}
			}
    };
    
	/**
	 * 根据操作类型：显示按钮等
	 */
    function initShow() {
        var operateType = $('#operateType').val();
        if(operateType === 'edit') {
//        	 $("#ValuationMethodScheme004_btn_save").show();
//        	 $("#ValuationMethodScheme004_btn_submit").hide();
        }else if(operateType === 'add' || operateType === 'copy') {
//        	 $("#ValuationMethodScheme004_btn_save").hide();
//        	 $("#ValuationMethodScheme004_btn_submit").show();
        }else {
//        	 $("#ValuationMethodScheme004_btn_save").hide();
//        	 $("#ValuationMethodScheme004_btn_submit").show();
        	 
        	 $('#valuationDate').next().addClass('disabled').find('li').off('click');
        }
        
        valMethodLinkShow();//  估值方法为市值法/市值成本孰低法 显示：估值第一市价、估值第二市价、行情日期、行情位数、行情位数处理
        
        priceClassShow(); // 估值方法为市值法/市值成本孰低法且估值市价为收盘价、平均价、最高价、最低价、卖一价或者买一价时显示价格类型
    };
	

    $(function() {
        // 绑定按钮事件
        $('#ValuationMethodScheme004_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#ValuationMethodScheme004_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        

        initShow();// 根据操作类型：显示按钮等
        linkBind();//绑定联动
    	
    });
});