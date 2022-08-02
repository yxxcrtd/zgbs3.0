/**
 * 净值化产品存续期交易新建
 */
$(function() {

	//画面联动：输入产品代码，联动显示币种
    var finprodMarketIdChange = function() {
    	if (!$('#finprodMarketId').select2('data') || $('#finprodMarketId').select2('data').length == 0 || !$('#finprodMarketId').select2('data')[0]
		|| !$('#finprodMarketId').select2('data')[0].object) {
    		return;
		}
        var str = $('#finprodMarketId').select2('data')[0].object.split(',');
    	if(str != null && str.length==17){
    		$('#finprodId').val(str[2]);
    		$('#finccy').textSelect('setValue', str[5]); // 币种

            if(str[6]){
            	var vDate = Date.ofDate(str[6]).format('yyyy-MM-dd');
            	$('#vDate').val(vDate);
            }
            if(str[7]){
            	var mDate = Date.ofDate(str[7]).format('yyyy-MM-dd');
            	$('#mDate').val(mDate);
            }
            $('#isCycle').val(str[8]);	//

            $('#FinNetProductDuration_prodAbbr').val(str[0]);
            $('#FinNetProductDuration_prodName').val(str[1]);
            if(str[9]){
            	$('#operationType').val(str[9]);
            }
            if(str[10]){
            	$('#profitType').val(str[10]);
            }
            if(str[11]){
            	$('#portfolioId').val(str[11]);
            }
            //modify by wm 增加处理模式 2019-06-26 start
            if(str[12]){
            	$('#dealMode').val(str[12]);
            }
            //modify by wm 增加处理模式 2019-06-26 end
            if(str[13]){
            	$('#calendarId').val(str[13]);
            }
            //新增期限类型     周期型 净值型新增      2019-09-24
            if(str[14]){
            	$('#termType').val(str[14]);
            }
            //新增是否续投     周期型 净值型赎回新增      20191019
            if(str[15]){
            	$('#continueFlag').val(str[15]);
            }
    	}
    	//交易类型 下拉枚举值联动
    	dealTypeSelectInit();
    };
    /**
     * 显隐性要素联动	净值型周期型新增
     */
    var periodInfoList;		//查询到的期次信息列表
    var maneModelDivChange = function(){
    	//--------------------认申购      start----------------------
    	if($("#dealType").val()=="C1" || $("#dealType").val()=="C2"){
    		//净值型周期型 期次管理方式
        	if($("#profitType").val()=="01" && $("#isCycle").val()=="Y" && $("#maneWay").val()=="02"){
        		//01单一固定   02 多固定期限	投资周期展示
        		if($("#termType").val()=="01" || $("#termType").val()=="02"){
        			//新建
        	        if($("#pageModel").val() == PAGE_MODEL_NEW){
        	        	Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getTermDaysList',
	        	        	{'finprodId':$('#finprodId').val()},
	        	        	function(result) {
	        	        		$('#subTermDays').empty();
	        	        		$('#purTermDays').empty();
	        	        		if(result.length>1){
	        	        			$('#subTermDays').append("<option value=''></option>");
	        	                	$('#purTermDays').append("<option value=''></option>");
	        	        		}
	        	                $(result).each(function (i, n) {
	        	                	$('#subTermDays').append("<option value='"+n.id+"'>"+n.text+"</option>");
	        	                	$('#purTermDays').append("<option value='"+n.id+"'>"+n.text+"</option>");
	        	                });
                	        	termDaysChange();
	        	        	}
    	        		);
        	        }
            		$("#subTermDaysDiv").show();
            		$("#purTermDaysDiv").show();

        		}
        		//无固定期限
        		else{
        			$("#subTermDaysDiv").hide();
            		$("#purTermDaysDiv").hide();
            		//无固定期限	期次代码设置可点击
            		$("#subPeriodId").removeAttr("disabled");
            		$("#purPeriodId").removeAttr("disabled");
        		}

        	}
        	else{
        		$("#subTermDaysDiv").hide();
        		$("#purTermDaysDiv").hide();
        	}

        	//周期型且期次管理方式为“显性管理”		期次代码展示
        	if($("#profitType").val()=="01" && $("#isCycle").val()=="Y" && $("#maneWay").val()=="01"){
        		//新建
    	        if($("#pageModel").val() == PAGE_MODEL_NEW){
    	        	var vDate = $("#vDate").val();;
    	        	//认购去产品到期日
    	        	if($("#dealType").val()=="C1"){
    	        		vDate = $("#vDate").val();
	        		}else{	//申购取产品确认日
	        			vDate = $('#purConfirmDate').val();
	    			}
    	        	//获取期限信息
                	Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getPeriodsList',
        	        	{'finprodId':$('#finprodId').val(), "vDate":vDate},
        	        	function(result) {
        	        		$('#subPeriodId').empty();
        	        		$('#purPeriodId').empty();
        	        		periodInfoList = result;
    	        			$('#subPeriodId').append("<option value=''></option>");
    	                	$('#purPeriodId').append("<option value=''></option>");
        	                if($("#dealType").val()=="C1"){
        	                	$(result).each(function (i, n) {
        	                		$('#subPeriodId').append("<option object='"+n.object+"'>"+n.id+"</option>");
        	                	});
        			        	//期次代码可选择
        			        	$("#subPeriodId").removeAttr("disabled");
        			        	//期次结束日
        			        	$("#subCycleEndDate").removeAttr("disabled");
        	        		}else{
        	        			$(result).each(function (i, n) {
        	        				$('#purPeriodId').append("<option>"+n.id+"</option>");
        	        			});
        	        			//期次代码可选择
        	        			$("#purPeriodId").removeAttr("disabled");
        	        			//期次结束日
        	        			$("#purCycleEndDate").removeAttr("disabled");
        	    			}
        	        	}
        	        );
    	        }
        		$("#subPeriodIdDiv").show();
        		$("#purPeriodIdDiv").show();
        	}
        	else{
        		$("#subPeriodIdDiv").hide();
        		$("#purPeriodIdDiv").hide();
        	}

        	//净值型周期型
        	if($("#profitType").val()=="01" && $("#isCycle").val()=="Y"){
        		//期次结束日可见必填	期次结束日
        		$("#subCycleEndDateDiv").show();
        		$("#purCycleEndDateDiv").show();
        		//期次支付日可见必填	期次支付日
        		$("#subCyclePayDateDiv").show();
        		$("#purCyclePayDateDiv").show();
        	}
        	else{
        		$("#subCycleEndDateDiv").hide();
        		$("#purCycleEndDateDiv").hide();
        		$("#subCyclePayDateDiv").hide();
        		$("#purCyclePayDateDiv").hide();
        	}
    	}
    	//--------------------认申购      end----------------------

    	//期次管理方式“显性管理”		期次代码展示	----------赎回部分------------
    	if($("#maneWay").val()=="01"){
    		$("#redPeriodIdDiv").show();	//期次代码
    	}
    	else{
    		$("#redPeriodIdDiv").hide();	//期次代码
    	}
    }
    /**
     * 认申购期次代码联动，获取显性期限类型，期限
     */
    periodIdChange = function(){
    	//认购
    	if($("#dealType").val()=="C1"){
    		var termInfoObj;
    		for(var i in periodInfoList){
    			if(periodInfoList[i].id == $("#subPeriodId").val()){
    				termInfoObj = periodInfoList[i].object;
    			}
    		}
    		if(termInfoObj == null){
    			return;
    		}
    		var termInfo = termInfoObj.split(',');
    		if(termInfo != null && termInfo.length==4){
    			//期限类型
    			//使用产品ID带出的期限类型 yzg 20190106 start
    			if(termInfo[0]){
    				$("#termType").val(termInfo[0]);
    			}
    			//使用产品ID带出的期限类型 yzg 20190106 end
    			//期限
    			$('#subTermDays').empty();
    			$('#subTermDays').append("<option value='"+termInfo[1]+"'>"+termInfo[1]+"</option>");
    			$('#subTermDays').val(termInfo[1]);
    			//期次分支序号
    			if(termInfo[2]){
    				$("#subPeriodBranch").val(termInfo[2]);
    			}
    			//期次到期日
    			if(termInfo[3]){
    				$("#subCycleEndDate").val(termInfo[3]);
    				cycleEndDateChange();
    			}
    			//单一期限、多固定期限	期次结束日不可修改
    			if($("#termType").val()=="01" || $("#termType").val()=="02"){
    				$("#subCycleEndDate").attr("disabled","disabled");
    			}
    			else{
    				$("#subCycleEndDate").removeAttr("disabled");
    			}
    		}
		}else{		//申购
			var termInfoObj;
    		for(var i in periodInfoList){
    			if(periodInfoList[i].id = $("#purPeriodId").val()){
    				termInfoObj = periodInfoList[i].object;
    			}
    		}
    		if(termInfoObj == null){
    			return;
    		}
    		var termInfo = termInfoObj.split(',');
    		if(termInfo != null && termInfo.length==4){
    			$("#termType").val(termInfo[0]);
    			$('#purTermDays').empty();
    			$('#purTermDays').append("<option value='"+termInfo[1]+"'>"+termInfo[1]+"</option>");
    			$('#purTermDays').val(termInfo[1]);
    			//期次分支序号
    			if(termInfo[2]){
    				$("#purPeriodBranch").val(termInfo[2]);
    			}
    			//期次到期日
    			if(termInfo[3]){
    				$("#purCycleEndDate").val(termInfo[3]);
    				cycleEndDateChange();
    			}
    			//单一期限、多固定期限	期次结束日不可修改
    			if($("#termType").val()=="01" || $("#termType").val()=="02"){
    				$("#purCycleEndDate").attr("disabled","disabled");
    			}
    			else{
    				$("#purCycleEndDate").removeAttr("disabled");
    			}
    		}
		}
    }
    /**
     * 认申购期限结束日修改联动，期限结算日默认结束日下一天
     */
    var cycleEndDateChange = function(){
    	//认购
    	if($("#dealType").val()=="C1"){
    		//认购结束日下一天
    		var endDate = new Date($('#subCycleEndDate').val());
    		var payDateString = +endDate + 1000*60*60*24;
    		var payDateMonth = new Date(payDateString).getMonth()+1 < 10 ?
    				"0" + (new Date(payDateString).getMonth()+1) : (new Date(payDateString).getMonth()+1);
    		var payDateDate = new Date(payDateString).getDate() < 10 ?
    				"0" + (new Date(payDateString).getDate()) : (new Date(payDateString).getDate());
    		$("#subCyclePayDate").val(new Date(payDateString).getFullYear() +"-"+payDateMonth +"-"+payDateDate);
    	}
    	//申购
    	else{
    		//申购结束日下一天
    		//认购结束日下一天
    		var endDate = new Date($('#purCycleEndDate').val());
    		//支付日和期次结束日相等 modify by liupeilin 2020-01-15 begin
            var payDateString;
             if(BANK_CODE == '004'){
                payDateString = +endDate;
             }else{
                payDateString = +endDate + 1000*60*60*24;
             }
            //支付日和期次结束日相等 modify by liupeilin 2020-01-15 end
    		var payDateMonth = new Date(payDateString).getMonth()+1 < 10 ?
    				"0" + (new Date(payDateString).getMonth()+1) : (new Date(payDateString).getMonth()+1);
    		var payDateDate = new Date(payDateString).getDate() < 10 ?
    				"0" + (new Date(payDateString).getDate()) : (new Date(payDateString).getDate());
    		$("#purCyclePayDate").val(new Date(payDateString).getFullYear() +"-"+payDateMonth +"-"+payDateDate);
    	}
    }
    /**
     * 认申投资周期联动
     */
    var termDaysChange = function(){
    	//认购
    	if($("#dealType").val()=="C1"){
    		var confirmDate = new Date($('#subConfirmDate').val());
    		var endDateString = +confirmDate + 1000*60*60*24*($("#subTermDays").val());
    		var endDateMonth = new Date(endDateString).getMonth()+1 < 10 ?
    				"0" + (new Date(endDateString).getMonth()+1) : (new Date(endDateString).getMonth()+1);
    		var endDateDate = new Date(endDateString).getDate() < 10 ?
    				"0" + (new Date(endDateString).getDate()) : (new Date(endDateString).getDate());
    		$("#subCycleEndDate").val(new Date(endDateString).getFullYear() +"-"+endDateMonth +"-"+endDateDate);
    		cycleEndDateChange();
    	}else{
    		var confirmDate = new Date($('#purConfirmDate').val());
    		var endDateString = +confirmDate + 1000*60*60*24*($("#purTermDays").val());
    		var endDateMonth = new Date(endDateString).getMonth()+1 < 10 ?
    				"0" + (new Date(endDateString).getMonth()+1) : (new Date(endDateString).getMonth()+1);
    		var endDateDate = new Date(endDateString).getDate() < 10 ?
    				"0" + (new Date(endDateString).getDate()) : (new Date(endDateString).getDate());
    		$("#purCycleEndDate").val(new Date(endDateString).getFullYear() +"-"+endDateMonth +"-"+endDateDate);
    		cycleEndDateChange();
    	}
    }
    /**
     * 申购确认日修改联动    重新获取期次代码
     */
    var purConfirmDateChange = function(){
    	//周期型且期次管理方式为“显性管理”		期次代码展示
    	if($("#profitType").val()=="01" && $("#isCycle").val()=="Y" && $("#maneWay").val()=="01"){
    		//新建
	        if($("#pageModel").val() == PAGE_MODEL_NEW){
	        	//获取期限信息
            	Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getPeriodsList',
    	        	{'finprodId':$('#finprodId').val(), "vDate":$('#purConfirmDate').val()},
    	        	function(result) {
    	        		$('#purPeriodId').empty();
    	        		periodInfoList = result;
	                	$('#purPeriodId').append("<option value=''></option>");
	        			$(result).each(function (i, n) {
	        				$('#purPeriodId').append("<option>"+n.id+"</option>");
	        			});
    	        	}
    	        );
	        }
    	}
    }
    var finprodMarketIdDivChange = function(){
    	$('#subModel').hide();
    	$('#purModel').hide();
    	$('#redModel').hide();
    	$('#redCycleModel').hide();
    	$('#expireModel').hide();
    	$('#expireCycleModel').hide();
    	$('#addReduceModel').hide();
    	$('#addReduceCycleModel').hide();
    	$('#subModel03').hide();
    	$('#purModel03').hide();
    	$('#redModel03').hide();
    	$('#expireModel03').hide();
    	$('#addReduceModel03').hide();
    	$('#bonusCashModel03').hide();
    	$('#bonusShareModel03').hide();

    	$('#FinNetProductDuration_div_red_layering').prop('hidden', true);
    	$('#FinNetProductDuration_div_expire_layering').prop('hidden', true);
    	$('#FinNetProductDuration_div_reduce_layering').prop('hidden', true);
		$('#FinNetProductDuration_div_layering').prop('hidden', true);
		$('#FinNetProductDuration_div_pur_layering').prop('hidden', true);

    	$(".hkxx").find(".layui-table-view .layui-table-box .layui-table-body table tbody tr td").html("");
    	$(".hkxx").prop('hidden', false);
    	if(typeof pageModel !== 'undefined'
            && pageModel === PAGE_MODEL_NEW){
    		$("#subModel").find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
            $('#purModel').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#redModel').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#redCycleModel').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#expireModel').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#expireCycleModel').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#addReduceModel').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#addReduceCycleModel').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#subModel03').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#purModel03').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#redModel03').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#expireModel03').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#addReduceModel03').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#bonusCashModel03').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
        	$('#bonusShareModel03').find(".pro-module-item .layui-form-item .form-inline-wrap .input-inline input").val("");
    	}
    }
    //交易类型 下拉枚举值联动初始化
    var dealTypeSelectInit = function(){
    	//是否周期产品
    	var isCycle = $('#isCycle').val();
    	//运作方式，开放式01、封闭式02
    	var operationType = $('#operationType').val();
    	//净值产品还是货币产品
    	var profitType = $('#profitType').val();
    	var finprodId = $('#finprodId').val();
    	$('#dealType').empty();
    	Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getDealTypeList',
        	{'isCycle':isCycle,'operationType':operationType,'profitType':profitType,'finprodId':finprodId},
        	function(result) {
                $('#dealType').append("<option value=''>请选择...</option>");
                $(result).each(function (i, n) {
                	$('#dealType').append("<option value='"+n.id+"'>"+n.text+"</option>");
                });
        	}
        );
    };

    /**
	 * 分层金额明细-认购
	 */

	var layAmtNetTable = $('#FinNetProductDuration_layAmtNetTable').joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/product/productduration/netproductDuration/FinNetProductDuration/queryLayAmtDetailByFinprodId'),
            type: 'POST',
            data: function() {
                return {finprodId : $('#finprodId').val(),workflowId : workflowId, tradeId : $('#tradeId').val(), dealType : $("#dealType").val()};
            }
        },
        searchOnInit: false,
        height: 200,
        page: false,
        checkbox: false,
        loadonce: true,
        rowno: true,
        cols:[
				 {field:'prodType', title: 'FinNetProductDuration.prodType', sortable: false, width: 200}
				,{field:'subDealLayerAmt', title: 'FinNetProductDuration.subDealLayerAmt', width: 160, sortable: false, edit: true, type:'number' }
				,{field:'subLayerFee', title: 'FinNetProductDuration.subLayerFee',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'finprodId', visible: false}
				,{field:'finprodType', visible: false}
				,{field:'finprodType2', visible: false}
				,{field:'tradeId', visible: false}
	        ],
	    beforeEditCell : function(preVal, field, rowData){
	      return true;
	    },
	    editCell: function(value, data, field, cell, column){
	    	var rowData = {};
//	    	var lastAmt = 0;
//	    	var currentSub = 0;
//	    	var lastRed = 0;
//	    	var lastAmtSub = 0;
//	    	var accountingAmt = 0;
	    	var subDealLayerAmtSum = 0;
	    	var subLayerFeeSum = 0;
	    	rowData.subDealLayerAmtSum = data.subDealLayerAmt;
	    	rowData.accountingAmt = rowData.subLayerFee;
	    	layAmtNetTable.renderRow(data.LAY_TABLE_INDEX, rowData);
	    	var arr = layAmtNetTable.getAllRows();
	    	 for ( var i = 0; i <arr.length; i++){
	   		 if(arr[i].subDealLayerAmt==""||arr[i].subDealLayerAmt==null){
	   			 arr[i].subDealLayerAmt = 0;
	   		 }
	   		 if(arr[i].subLayerFee==""||arr[i].subLayerFee==null){
	   			 arr[i].subLayerFee = 0;
	   		 }

	   		 subDealLayerAmtSum = JoyinMath.doAdd(subDealLayerAmtSum, arr[i].subDealLayerAmt, 2, JoyinMath.ROUND_HALF_UP);//上期余额累计
	   		 subLayerFeeSum = JoyinMath.doAdd(subLayerFeeSum, arr[i].subLayerFee, 2, JoyinMath.ROUND_HALF_UP);//本期认购累计
	   		}
		   	 $('#subDealAmt').val(subDealLayerAmtSum).trigger("change");
//		   	 $('#subDealNum').val(subDealLayerAmtSum);
		   	 $('#subFee').val(subLayerFeeSum);
	    }
    });
	/**
	 * 分层金额明细-申购
	 */

	var layAmtPurNetTable = $('#FinNetProductDuration_layAmtPurNetTable').joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/product/productduration/netproductDuration/FinNetProductDuration/queryLayAmtDetailByFinprodId'),
            type: 'POST',
            data: function() {
            	var dealType = $("#dealType").val();
            	var dealDate = $("#purConfirmDate").val();
                return {finprodId : $('#finprodId').val(),workflowId : workflowId, tradeId : $('#tradeId').val(), dealDate : dealDate, dealType : dealType};
            }
        },
        searchOnInit: false,
        height: 200,
        page: false,
        checkbox: false,
        loadonce: true,
        rowno: true,
        cols:[
				 {field:'prodType', title: 'FinNetProductDuration.prodType', sortable: false, width: 200}
				,{field:'purNetValueLayerDate', title: 'FinNetProductDuration.purNetValueLayerDate', width: 160, sortable: false, edit: true, type:'date' }
				,{field:'purUnitNetLayerValue', title: 'FinNetProductDuration.purUnitNetLayerValue', width: 160,  sortable: false }
				,{field:'purDealLayerAmt', title: 'FinNetProductDuration.purDealLayerAmt', width: 160, edit: true, sortable: false, type:'number' }
				,{field:'purDealLayerNum', title: 'FinNetProductDuration.purDealLayerNum',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'purLayerFee', title: 'FinNetProductDuration.purLayerFee',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'finprodId', visible: false}
				,{field:'finprodType', visible: false}
				,{field:'finprodType2', visible: false}
				,{field:'tradeId', visible: false}
	        ],
	    beforeEditCell : function(preVal, field, rowData){

	      return true;
	    },
	    editCell: function(value, data, field, cell, column){

	    	var rowData = {};
//	    	var lastAmt = 0;
//	    	var currentSub = 0;
//	    	var lastRed = 0;
//	    	var lastAmtSub = 0;
//	    	var accountingAmt = 0;
	    	var purDealLayerAmtSum = 0;
	    	var purDealLayerNumSum = 0;
	    	var purLayerFeeSum = 0;

	    	if(data.purNetValueLayerDate&&field == 'purNetValueLayerDate'){
	    		data.purUnitNetLayerValue = '';
	    		getLayerNetValueInfo(data);
	    		rowData.purUnitNetLayerValue = data.purUnitNetLayerValue;
	    	}
	   		if(data.purUnitNetLayerValue && data.purDealLayerAmt && field == 'purDealLayerAmt'){
	   			rowData.purDealLayerNum = Number(JoyinMath.doDiv(data.purDealLayerAmt,data.purUnitNetLayerValue,2));
        	}
	    	layAmtPurNetTable.renderRow(data.LAY_TABLE_INDEX, rowData);
	    	var arr = layAmtPurNetTable.getAllRows();
	    	 for ( var i = 0; i <arr.length; i++){
	   		 if(arr[i].purDealLayerAmt==""||arr[i].purDealLayerAmt==null){
	   			 arr[i].purDealLayerAmt = 0;
	   		 }
	   		 if(arr[i].purDealLayerNum==""||arr[i].purDealLayerNum==null){
	   			 arr[i].purDealLayerNum = 0;
	   		 }
	   		if(arr[i].purLayerFee==""||arr[i].purLayerFee==null){
	   			 arr[i].purLayerFee = 0;
	   		 }
	   		 purDealLayerAmtSum = JoyinMath.doAdd(purDealLayerAmtSum, arr[i].purDealLayerAmt, 2, JoyinMath.ROUND_HALF_UP);//上期余额累计
	   		 purDealLayerNumSum = JoyinMath.doAdd(purDealLayerNumSum, arr[i].purDealLayerNum, 2, JoyinMath.ROUND_HALF_UP);//本期认购累计
	   		 purLayerFeeSum = JoyinMath.doAdd(purLayerFeeSum, arr[i].purLayerFee, 2, JoyinMath.ROUND_HALF_UP);//本期认购累计
	    	 }
		   	 $('#purDealAmt').val(purDealLayerAmtSum);
		   	 $('#purDealNum').val(purDealLayerNumSum);
		   	 $('#purFee').val(purLayerFeeSum);
	         reloadSettle();
	    }
    });
	/**
	 * 分层金额明细-赎回
	 */

	var layAmtRedNetTable = $('#FinNetProductDuration_layAmtRedNetTable').joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/product/productduration/netproductDuration/FinNetProductDuration/queryLayAmtDetailByFinprodId'),
            type: 'POST',
            data: function() {
            	var dealType = $("#dealType").val();
            	var dealDate = $("#redConfirmDate").val();
                return {finprodId : $('#finprodId').val(),workflowId : workflowId, tradeId : $('#tradeId').val(), dealDate : dealDate, dealType : dealType};
            }
        },
        searchOnInit: false,
        height: 200,
        page: false,
        checkbox: false,
        loadonce: true,
        cols:[
				 {field:'prodType', title: 'FinNetProductDuration.prodType', sortable: false, width: 200}
				,{field:'redNetValueLayerDate', title: 'FinNetProductDuration.redNetValueLayerDate', width: 160, sortable: false, edit: true, type:'date' }
				,{field:'redUnitNetLayerValue', title: 'FinNetProductDuration.redUnitNetLayerValue', width: 160,  sortable: false}
				,{field:'redDealLayerNum', title: 'FinNetProductDuration.redDealLayerNum', width: 160, edit: true, sortable: false, type:'number' }
				,{field:'redDealLayerAmt', title: 'FinNetProductDuration.redDealLayerAmt',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'redFeeLcLayer', title: 'FinNetProductDuration.redFeeLcLayer',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'redTotalNetLayerValue', title: 'FinNetProductDuration.redTotalNetLayerValue', width: 160,  sortable: false, type:'number' }
				,{field:'redLayerFee', title: 'FinNetProductDuration.redLayerFee', width: 160, edit: true, sortable: false, type:'number' }
				,{field:'redPurLayerFeeHd', title: 'FinNetProductDuration.redPurLayerFeeHd',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'redLayerProfit', title: 'FinNetProductDuration.redLayerProfit',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'redLayerCost', title: 'FinNetProductDuration.redLayerCost',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'finprodId', visible: false}
				,{field:'finprodType', visible: false}
				,{field:'finprodType2', visible: false}
				,{field:'tradeId', visible: false}
	        ],
	    beforeEditCell : function(preVal, field, rowData){
	      return true;
	    },
	    editCell: function(value, data, field, cell, column){
	    	var rowData = {};
	    	var redDealLayerNumSum = 0;
	    	var redDealLayerAmtSum = 0;
	    	var redFeeLcLayerSum = 0;
	    	var redTotalNetLayerValueSum = 0;
	    	var redLayerFeeSum = 0;
	    	var redPurLayerFeeHdSum = 0;
	    	var redLayerProfitSum = 0;
	    	var redLayerCostSum = 0;

	    	if(data.redNetValueLayerDate&&field == 'redNetValueLayerDate'){
	    		data.redUnitNetLayerValue =  '';
	    		getLayerNetValueInfo(data);
	    		rowData.redUnitNetLayerValue = data.redUnitNetLayerValue;
	    	}

	    	if(data.redUnitNetLayerValue && data.redDealLayerNum){
	   			rowData.redDealLayerAmt = Number(JoyinMath.doMul(data.redUnitNetLayerValue,data.redDealLayerNum,2));
        	}
	    	rowData.redTotalNetLayerValue = JoyinMath.doMinus(rowData.redDealLayerAmt,data.redFeeLcLayer,2);
	    	layAmtRedNetTable.renderRow(data.LAY_TABLE_INDEX, rowData);
	    	var arr = layAmtRedNetTable.getAllRows();
	    	for ( var i = 0; i <arr.length; i++){
	   		 if(arr[i].redDealLayerNum==""||arr[i].redDealLayerNum==null){
	   			 arr[i].redDealLayerNum = 0;
	   		 }
	   		 if(arr[i].redDealLayerAmt==""||arr[i].redDealLayerAmt==null){
	   			 arr[i].redDealLayerAmt = 0;
	   		 }
	   		 if(arr[i].redTotalNetLayerValue==""||arr[i].redTotalNetLayerValue==null){
	   			 arr[i].redTotalNetLayerValue = 0;
	   		 }
	   		 if(arr[i].redFeeLcLayer==""||arr[i].redFeeLcLayer==null){
	   			 arr[i].redFeeLcLayer = 0;
	   		 }
	   		 if(arr[i].redPurLayerFeeHd==""||arr[i].redPurLayerFeeHd==null){
	   			 arr[i].redPurLayerFeeHd = 0;
	   		 }
	   		 if(arr[i].redLayerFee==""||arr[i].redLayerFee==null){
	   			 arr[i].redLayerFee = 0;
	   		 }
	   		 if(arr[i].redLayerProfit==""||arr[i].redLayerProfit==null){
	   			 arr[i].redLayerProfit = 0;
	   		 }
	   		 if(arr[i].redLayerCost==""||arr[i].redLayerCost==null){
	   			 arr[i].redLayerCost = 0;
	   		 }
	   		 redDealLayerNumSum = JoyinMath.doAdd(redDealLayerNumSum, arr[i].redDealLayerNum, 2, JoyinMath.ROUND_HALF_UP);
	    	 redDealLayerAmtSum = JoyinMath.doAdd(redDealLayerAmtSum, arr[i].redDealLayerAmt, 2, JoyinMath.ROUND_HALF_UP);
	    	 redFeeLcLayerSum = JoyinMath.doAdd(redFeeLcLayerSum, arr[i].redFeeLcLayer, 2, JoyinMath.ROUND_HALF_UP);
	    	 redTotalNetLayerValueSum = JoyinMath.doAdd(redTotalNetLayerValueSum, arr[i].redTotalNetLayerValue, 2, JoyinMath.ROUND_HALF_UP);
	    	 redLayerFeeSum = JoyinMath.doAdd(redLayerFeeSum, arr[i].redLayerFee, 2, JoyinMath.ROUND_HALF_UP);
	    	 redPurLayerFeeHdSum = JoyinMath.doAdd(redPurLayerFeeHdSum, arr[i].redPurLayerFeeHd, 2, JoyinMath.ROUND_HALF_UP);
	    	 redLayerProfitSum = JoyinMath.doAdd(redLayerProfitSum, arr[i].redLayerProfit, 2, JoyinMath.ROUND_HALF_UP);
	    	 redLayerCostSum = JoyinMath.doAdd(redLayerCostSum, arr[i].redLayerCost, 2, JoyinMath.ROUND_HALF_UP);

	    	}
		   	$('#redDealNum').val(redDealLayerNumSum);
		   	$('#redFeeLc').val(redFeeLcLayerSum);
		   	$('#redDealAmt').val(redDealLayerAmtSum).trigger("change");
//		   	$('#redTotalNetValue').val(redTotalNetLayerValueSum);
		   	$('#redFee').val(redLayerFeeSum);
		   	$('#redPurFeeHd').val(redPurLayerFeeHdSum);
		   	$('#redProfit').val(redLayerProfitSum);
		   	$('#redCost').val(redLayerCostSum);
	    }
    });
	/**
	 * 分层金额明细-到期
	 */

	var layAmtExpireNetTable = $('#FinNetProductDuration_layAmtExpireNetTable').joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/product/productduration/netproductDuration/FinNetProductDuration/queryLayAmtDetailByFinprodId'),
            type: 'POST',
            data: function() {
            	var dealType = $("#dealType").val();
            	var dealDate = $("#expireConfirmDate").val();
                return {finprodId : $('#finprodId').val(),workflowId : workflowId, tradeId : $('#tradeId').val(), dealDate : dealDate, dealType : dealType};
            }
        },
        searchOnInit: false,
        height: 200,
        page: false,
        checkbox: false,
        loadonce: true,
        cols:[
				 {field:'prodType', title: 'FinNetProductDuration.prodType', sortable: false, width: 200}
				,{field:'expireNetValueLayerDate', title: 'FinNetProductDuration.expireNetValueLayerDate', width: 160, sortable: false, edit: true, type:'date' }
				,{field:'expireUnitNetLayerValue', title: 'FinNetProductDuration.expireUnitNetLayerValue', width: 160,  sortable: false }
				,{field:'expireDealLayerAmt', title: 'FinNetProductDuration.expireDealLayerAmt', width: 160, edit: true, sortable: false, type:'number' }
				,{field:'expireDealLayerNum', title: 'FinNetProductDuration.expireDealLayerNum', sortable: false, type:'number'}
				,{field:'expireLayerProfit', title: 'FinNetProductDuration.expireLayerProfit', width: 160, edit: true, sortable: false, type:'number' }
				,{field:'expireLayerCost', title: 'FinNetProductDuration.expireLayerCost',  width: 160, edit: true, sortable: false, type:'number'}
				,{field:'finprodId', visible: false}
				,{field:'finprodType', visible: false}
				,{field:'finprodType2', visible: false}
				,{field:'tradeId', visible: false}
	        ],
	    beforeEditCell : function(preVal, field, rowData){
	      return true;
	    },
	    editCell: function(value, data, field, cell, column){
	    	var rowData = {};
	    	var expireDealLayerAmtSum = 0;
	    	var expireDealLayerNumSum = 0;
	    	var expireLayerProfitSum = 0;
	    	var expireLayerCostValueSum = 0;

	    	if(data.expireNetValueLayerDate&&field == 'expireNetValueLayerDate'){
	    		data.expireUnitNetLayerValue =  '';
	    		getLayerNetValueInfo(data);
	    		rowData.expireUnitNetLayerValue = data.expireUnitNetLayerValue;
	    		if(data.expireDealLayerNum&&data.expireUnitNetLayerValue){
	    			rowData.expireDealLayerAmt = Number(JoyinMath.doMul(data.expireUnitNetLayerValue,data.expireDealLayerNum,2));
	    			rowData.expireDealLayerNum = data.expireDealLayerNum;
	    		}
	    		else{
	    			rowData.expireDealLayerAmt = 0;
	    			rowData.expireDealLayerNum = 0;
	    		}
	    	}
	    	layAmtExpireNetTable.renderRow(data.LAY_TABLE_INDEX, rowData);

	    	var arr = layAmtExpireNetTable.getAllRows();

	    	for ( var i = 0; i <arr.length; i++){
	   		 if(arr[i].expireDealLayerAmt==""||arr[i].expireDealLayerAmt==null){
	   			 arr[i].expireDealLayerAmt = 0;
	   		 }
	   		 if(arr[i].expireDealLayerNum==""||arr[i].expireDealLayerNum==null){
	   			 arr[i].expireDealLayerNum = 0;
	   		 }
	   		 if(arr[i].expireLayerProfit==""||arr[i].expireLayerProfit==null){
	   			 arr[i].expireLayerProfit = 0;
	   		 }
	   		 if(arr[i].expireLayerCost==""||arr[i].expireLayerCost==null){
	   			 arr[i].expireLayerCost = 0;
	   		 }
	   		 expireDealLayerAmtSum = JoyinMath.doAdd(expireDealLayerAmtSum, arr[i].expireDealLayerAmt, 2, JoyinMath.ROUND_HALF_UP);
	   		 expireDealLayerNumSum = JoyinMath.doAdd(expireDealLayerNumSum, arr[i].expireDealLayerNum, 2, JoyinMath.ROUND_HALF_UP);
	   		 expireLayerProfitSum = JoyinMath.doAdd(expireLayerProfitSum, arr[i].expireLayerProfit, 2, JoyinMath.ROUND_HALF_UP);
	   		 expireLayerCostValueSum = JoyinMath.doAdd(expireLayerCostValueSum, arr[i].expireLayerCost, 2, JoyinMath.ROUND_HALF_UP);
	    	}
		   	$('#expireDealNum').val(expireDealLayerNumSum);
		   	$('#expireDealAmt').val(expireDealLayerAmtSum).trigger("change");
//		   	$('#redDealAmt').val(expireLayerProfitSum).trigger("change");
//		   	$('#redTotalNetValue').val(redTotalNetLayerValueSum);
		   	$('#expireCost').val(expireLayerCostValueSum);
		   	$('#expireProfit').val(expireLayerProfitSum);
	    },
	    done: function(table, allData){
	    	var expireDealLayerAmtSum = 0;
	    	var expireDealLayerNumSum = 0;
	    	var expireLayerProfitSum = 0;
	    	var expireLayerCostValueSum = 0;
	    	for ( var i = 0; i <table.length; i++){
		   		 if(table[i].expireDealLayerAmt==""||table[i].expireDealLayerAmt==null){
		   			table[i].expireDealLayerAmt = 0;
		   		 }
		   		 if(table[i].expireDealLayerNum==""||table[i].expireDealLayerNum==null){
		   			table[i].expireDealLayerNum = 0;
		   		 }
		   		 if(table[i].expireLayerProfit==""||table[i].expireLayerProfit==null){
		   			table[i].expireLayerProfit = 0;
		   		 }
		   		 if(table[i].expireLayerCost==""||table[i].expireLayerCost==null){
		   			table[i].expireLayerCost = 0;
		   		 }
		   		 expireDealLayerAmtSum = JoyinMath.doAdd(expireDealLayerAmtSum, table[i].expireDealLayerAmt, 2, JoyinMath.ROUND_HALF_UP);
		   		 expireDealLayerNumSum = JoyinMath.doAdd(expireDealLayerNumSum, table[i].expireDealLayerNum, 2, JoyinMath.ROUND_HALF_UP);
		   		 expireLayerProfitSum = JoyinMath.doAdd(expireLayerProfitSum, table[i].expireLayerProfit, 2, JoyinMath.ROUND_HALF_UP);
		   		 expireLayerCostValueSum = JoyinMath.doAdd(expireLayerCostValueSum, table[i].expireLayerCost, 2, JoyinMath.ROUND_HALF_UP);
		    	}
			   	$('#expireDealAmt').val(expireDealLayerAmtSum).trigger('blur');
			   	$('#expireDealNum').val(expireDealLayerNumSum);
//			   	$('#redDealAmt').val(expireLayerProfitSum).trigger("change");
//			   	$('#redTotalNetValue').val(redTotalNetLayerValueSum);
			   	$('#expireCost').val(expireLayerCostValueSum);
			   	$('#expireProfit').val(expireLayerProfitSum);
	    }
    });

	/**
	 * 分层金额明细-强增/强减
	 */

	var layAmtReduceNetTable = $('#FinNetProductDuration_layAmtReduceNetTable').joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/product/productduration/netproductDuration/FinNetProductDuration/queryLayAmtDetailByFinprodId'),
            type: 'POST',
            data: function() {
            	var dealType = $("#dealType").val();
            	var dealDate = $("#addReduceConfirmDate").val();
                return {finprodId : $('#finprodId').val(),workflowId : workflowId, tradeId : $('#tradeId').val(), dealDate : dealDate, dealType : dealType};
            }
        },
        searchOnInit: false,
        height: 200,
        page: false,
        checkbox: false,
        loadonce: true,
        cols:[
				 {field:'prodType', title: 'FinNetProductDuration.prodType', sortable: false, width: 200}
				,{field:'addReduceNetValueLayerDate', title: 'FinNetProductDuration.addReduceNetValueLayerDate', width: 160, sortable: false, edit: true, type:'date' }
				,{field:'addReduceUnitNetLayerValue', title: 'FinNetProductDuration.addReduceUnitNetLayerValue', width: 160,  sortable: false}
				,{field:'addReduceDealLayerNum', title: 'FinNetProductDuration.addReduceDealLayerNum', sortable: false, edit: true, type:'number'}
				,{field:'addReduceDealLayerAmt', title: 'FinNetProductDuration.addReduceDealLayerAmt', width: 160, edit: true, sortable: false, type:'number' }
				,{field:'finprodId', visible: false}
				,{field:'finprodType', visible: false}
				,{field:'finprodType2', visible: false}
				,{field:'tradeId', visible: false}
	        ],
	    beforeEditCell : function(preVal, field, rowData){
	      return true;
	    },
	    editCell: function(value, data, field, cell, column){
	    	var rowData = {};
	    	var addReduceDealLayerAmtSum = 0;
	    	var addReduceDealLayerNumSum = 0;

	    	if(data.addReduceNetValueLayerDate&&field == 'addReduceNetValueLayerDate'){
	    		data.addReduceUnitNetLayerValue =  '';
	    		getLayerNetValueInfo(data);
	    		rowData.addReduceUnitNetLayerValue = data.addReduceUnitNetLayerValue;
	    	}
	    	if(data.addReduceUnitNetLayerValue && data.addReduceDealLayerNum && field == 'addReduceDealLayerNum'){
	   			rowData.addReduceDealLayerAmt = Number(JoyinMath.doMul(data.addReduceUnitNetLayerValue,data.addReduceDealLayerNum,2));
        	}

	    	layAmtReduceNetTable.renderRow(data.LAY_TABLE_INDEX, rowData);
	    	var arr = layAmtReduceNetTable.getAllRows();
	    	for ( var i = 0; i <arr.length; i++){
	   		 if(arr[i].addReduceDealLayerAmt==""||arr[i].addReduceDealLayerAmt==null){
	   			 arr[i].addReduceDealLayerAmt = 0;
	   		 }
	   		 if(arr[i].addReduceDealLayerNum==""||arr[i].addReduceDealLayerNum==null){
	   			 arr[i].addReduceDealLayerNum = 0;
	   		 }

	   		 addReduceDealLayerAmtSum = JoyinMath.doAdd(addReduceDealLayerAmtSum, arr[i].addReduceDealLayerAmt, 2, JoyinMath.ROUND_HALF_UP);
	   		 addReduceDealLayerNumSum = JoyinMath.doAdd(addReduceDealLayerNumSum, arr[i].addReduceDealLayerNum, 2, JoyinMath.ROUND_HALF_UP);
	    	}
		   	$('#addReduceDealAmt').val(addReduceDealLayerAmtSum).trigger("change");
		   	$('#addReduceDealNum').val(addReduceDealLayerNumSum);
//		   	$('#redDealAmt').val(expireLayerProfitSum).trigger("change");
	    }
    });
    /*查询货币产品的分红方式和分红规则，
     * 如果分红规则不是 不规则，则查询分红计划表，是否存在计息结束日等于平台日的分红计划，不存在则计息结束日字段为空，不可做分红交易
     * 如果是不规则分红规则，则计息结束日取平台日，不可修改
     */
    var queryBonusRule = function(){
    	var finprodId = $('#finprodId').val();
    	var dealType = $('#dealType').val();
    	//modify by wm 红利下发日默认平台日  start
    	if("C9"==dealType){
        	$('#bonusCashBonusDate03').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd'));
    	}
    	else{
    		$('#bonusShareConfirmDate03').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd'));
    	}
    	//modify by wm 红利下发日默认平台日  end
    	Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/queryBonusRule',
        	{'finprodId':finprodId,'dealType':dealType},
        	function(result) {
        		if("C9"==dealType){
        			if(result.payType=='02'){
        				JoyinUtil.showMessage("该产品只支持红利再投！");
        				return;
        			}
        			if(result.payCycle=='09' || (result.payCycle=='08' && !result.mdate)  || result.payCycle=='07'){
        				//modify by wm 取消计息结束日对红利下发日的联动 start
        				//$('#bonusCashEndDate03').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
        				$('#bonusCashEndDate03').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd'));
        				//modify by wm 取消计息结束日对红利下发日的联动 end
        			}else{
        				var mdate = result.mdate;
        				if(mdate){
        					//modify by wm 取消计息结束日对红利下发日的联动 start
        					//$('#bonusCashEndDate03').val(Date.ofDate(mdate).format('yyyy-MM-dd')).trigger("change");
        					$('#bonusCashEndDate03').val(Date.ofDate(mdate).format('yyyy-MM-dd'));
        					//modify by wm 取消计息结束日对红利下发日的联动end
        				}

        			}
        		}else if("C10"==dealType){
        			if(result.payType=='01'){
        				JoyinUtil.showMessage("该产品只支持现金分红！");
        				return;
        			}
        			if(result.payCycle=='09' || (result.payCycle=='08' && !result.mdate) || result.payCycle=='07'){
        				$('#bonusShareEndDate03').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
        			}else{
        				var mdate = result.mdate;
        				if(mdate){
        					//取消计息结束日联动再投确认日联动，并将平台日赋值再投确认日 start modify by wm
        					//$('#bonusShareEndDate03').val(Date.ofDate(mdate).format('yyyy-MM-dd')).trigger("change");
        					$('#bonusShareEndDate03').val(Date.ofDate(mdate).format('yyyy-MM-dd'));
        					//取消计息结束日联动再投确认日联动，并将平台日赋值再投确认日 end modify by wm
        				}
        			}
        			$('#bonusShareAfterShareNumBf').val(result.bonusShareAfterShareNumBf);
        			$('#bonusShareAmt03').val(result.bonusShareAmtT).trigger("change");
        		}
        	}
        );
    };

    /**
     * 交易类型选择，录单模块联动变化
     */
    var dealTypeChange = function(){
    	//是否周期产品
    	var isCycle = $('#isCycle').val();
    	//运作方式，开放式01、封闭式02
    	var operationType = $('#operationType').val();
		//系统状态
		var operateType= $('#operateType').val();
    	//净值产品还是货币产品
    	var profitType = $('#profitType').val();
    	//交易类型
    	var dealType = $('#dealType').val();
    	//产品处理模式 add by wm 分档判断条件
    	var dealMode = $('#dealMode').val();

//    	$('#subModel').hide();
//    	$('#purModel').hide();
//    	$('#redModel').hide();
//    	$('#redCycleModel').hide();
//    	$('#expireModel').hide();
//    	$('#expireCycleModel').hide();
//    	$('#addReduceModel').hide();
//    	$('#addReduceCycleModel').hide();
//    	$('#subModel03').hide();
//    	$('#purModel03').hide();
//    	$('#redModel03').hide();
//    	$('#expireModel03').hide();
//    	$('#addReduceModel03').hide();
//    	$('#bonusCashModel03').hide();
//    	$('#bonusShareModel03').hide();
//
//    	$('#FinNetProductDuration_div_red_layering').prop('hidden', true);
//    	$('#FinNetProductDuration_div_expire_layering').prop('hidden', true);
//    	$('#FinNetProductDuration_div_reduce_layering').prop('hidden', true);
    	finprodMarketIdDivChange();
    	if("N"==isCycle){
			$('#redManaFreeValDiv').hide();
			$('#expireManaFreeValDiv').hide();
		}
		else{
			$('#redManaFreeValDiv').show();
			$('#expireManaFreeValDiv').show();
		}
//    	$(".hkxx").find(".layui-table-view .layui-table-box .layui-table-body table tbody tr td").html("");
    	if("01"==profitType){
    		//净值产品
    		if("C1"==dealType){
        		//认购
            	if("N"==isCycle){
                	//非周期产品期次结束日、期次支付日隐藏，确认日取产品起息日，不可修改
                	$('#subCycleEndDateDiv').hide();
            		$('#subCyclePayDateDiv').hide();
					if($("#pageModel").val() != PAGE_MODEL_REFERENCE) {
						$('#subConfirmDate').val(Date.ofDate($('#vDate').val()).format('yyyy-MM-dd'));
					}
            		$('#subConfirmDate').attr("disabled",true);
                }else {
                	//周期产品期次结束日、期次支付日展示，确认日取系统平台日，可修改
                	$('#subCycleEndDateDiv').show();
            		$('#subCyclePayDateDiv').show();
					if($("#pageModel").val() != PAGE_MODEL_REFERENCE){
            		$('#subConfirmDate').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd'));}
            		$('#subConfirmDate').attr("disabled",false);
                }
            	if("11"==dealMode){
            		$('#FinNetProductDuration_div_layering').prop('hidden', false);
            		$('#subDealAmt').attr("disabled",true);
        			$('#subFee').attr("disabled",true);
        			layAmtNetTable.load();
            	}
            	else{
            		$('#subDealAmt').attr("disabled",false);
        			$('#subFee').attr("disabled",false);
            	}
            	maneModelDivChange();
        		$('#subModel').show();
        	}else if("C2"==dealType){
        		//申购
        		if("N"==isCycle){
                	//非周期产品期次结束日、期次支付日隐藏
            		$('#purCycleEndDateDiv').hide();
            		$('#purCyclePayDateDiv').hide();
                }else {
                	//周期产品期次结束日、期次支付日展示
            		$('#purCycleEndDateDiv').show();
            		$('#purCyclePayDateDiv').show();
                }
        		//确认日等于平台日期
        		if(!$('#purConfirmDate').val() && $("#pageModel").val() != PAGE_MODEL_REFERENCE){
        			$('#purConfirmDate').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
        		}
        		if("11"==dealMode){
        			$('#purNetValueDate').attr("disabled",true);
        			$('#purDealAmt').attr("disabled",true);
        			$('#purDealNum').attr("disabled",true);
        			$('#purFee').attr("disabled",true);
        			$('#FinNetProductDuration_div_pur_layering').prop('hidden', false);
                	layAmtPurNetTable.load();
            	}
        		else{
        			$('#FinNetProductDuration_div_pur_layering').prop('hidden', true);
        			$('#purNetValueDate').attr("disabled",false);
        			$('#purDealAmt').attr("disabled",false);
        			$('#purDealNum').attr("disabled",false);
        			$('#purFee').attr("disabled",false);
        		}
        		maneModelDivChange();
        		$('#purModel').show();

        	}else if("C4"==dealType){
        		//分档
        		if("11"==dealMode){
        			$('#FinNetProductDuration_div_red_layering').prop('hidden', false);
        			$('#redNetValueDate').attr("disabled",true);
        			$('#redDealNum').attr("disabled",true);
        			$('#redDealAmt').attr("disabled",true);
        			$('#redFeeLc').attr("disabled",true);
        			$('#redFee').attr("disabled",true);
        			$('#redPurFeeHd').attr("disabled",true);
        			$('#redProfit').attr("disabled",true);
        			$('#redCost').attr("disabled",true);
        			layAmtRedNetTable.load();
        		}
        		//普通
        		else{
        			//赎回，周期产品不支持部分赎回
            		if("N"==isCycle){
            			$('#redDealNum').attr("disabled",false);
            			$('#redDealAmt').attr("disabled",false);
            			$('#redFeeLc').attr("disabled",false);
//            			$('#redTotalNetValue').attr("disabled",false);
            			$('#redFee').attr("disabled",false);
            			$('#redPurFeeHd').attr("disabled",false);
            			$('#redCost').attr("disabled",false);
            			$('#redProfit').attr("disabled",false);
            		}else{
            			//期次模块展示，并初始化和加载数据
            			$('#redCycleModel').show();
            			redCycleDataInit();
            			//周期型产品交易份额不可修改，通过列表汇总赋值
            			$('#redNetValueDate').attr("disabled",true);
            			$('#redUnitNetValue').attr("disabled",true);
            			$('#redDealNum').attr("disabled",true);
            			$('#redDealAmt').attr("disabled",true);
            			$('#redFeeLc').attr("disabled",true);
            			$('#redTotalNetValue').attr("disabled",true);
            			$('#redFee').attr("disabled",true);
            			$('#redPurFeeHd').attr("disabled",true);
            			$('#redCost').attr("disabled",true);
            			$('#redProfit').attr("disabled",true);
            			$('#redManaFreeVal').attr("disabled",true);

            		}
        		}
        		$('#redTitle').text("赎回要素");
        		$('#divLiquidatedDamages').hide();
        		//赎回确认日等于平台日期
        		if(!$('#redConfirmDate').val()){
        			$('#redConfirmDate').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
        		}
        		$('#redModel').show();
        	}else if("C17"==dealType){
        		//提前赎回，周期产品不支持提前赎回
        		$('#redDealNum').attr("disabled",false);
    			$('#redDealAmt').attr("disabled",false);
    			//赎回确认日等于平台日期
    			if(!$('#redConfirmDate').val() && $("#pageModel").val() != PAGE_MODEL_REFERENCE){
    				$('#redConfirmDate').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
    			}
    			$('#redTitle').text("提前赎回要素");
    			$('#divLiquidatedDamages').hide();
    			if("11"==dealMode){
        			$('#FinNetProductDuration_div_red_layering').prop('hidden', false);
        			$('#redNetValueDate').attr("disabled",true);
        			$('#redDealNum').attr("disabled",true);
        			$('#redDealAmt').attr("disabled",true);
        			$('#redFeeLc').attr("disabled",true);
        			$('#redFee').attr("disabled",true);
        			$('#redPurFeeHd').attr("disabled",true);
        			$('#redProfit').attr("disabled",true);
        			$('#redCost').attr("disabled",true);
        			layAmtRedNetTable.load();
        		}
        		else{
//        			$('#redNetValueDate').attr("disabled",false);
//        			$('#redDealNum').attr("disabled",false);
//        			$('#redDealAmt').attr("disabled",false);
//        			$('#redFeeLc').attr("disabled",false);
//        			$('#redFee').attr("disabled",false);
//        			$('#redPurFeeHd').attr("disabled",false);
//        			$('#redProfit').attr("disabled",false);
//        			$('#redCost').attr("disabled",false);
        			//赎回，周期产品不支持部分赎回
            		if("N"==isCycle){
            			$('#redDealNum').attr("disabled",false);
            			$('#redDealAmt').attr("disabled",false);
            			$('#redFeeLc').attr("disabled",false);
//            			$('#redTotalNetValue').attr("disabled",false);
            			$('#redFee').attr("disabled",false);
            			$('#redPurFeeHd').attr("disabled",false);
            			$('#redCost').attr("disabled",false);
            			$('#redProfit').attr("disabled",false);
            		}else{
            			//周期型产品交易份额不可修改，通过列表汇总赋值
            			$('#redNetValueDate').attr("disabled",true);
            			$('#redUnitNetValue').attr("disabled",true);
            			$('#redDealNum').attr("disabled",true);
            			$('#redDealAmt').attr("disabled",true);
            			$('#redFeeLc').attr("disabled",true);
            			$('#redTotalNetValue').attr("disabled",true);
            			$('#redFee').attr("disabled",true);
            			$('#redPurFeeHd').attr("disabled",true);
            			$('#redCost').attr("disabled",true);
            			$('#redProfit').attr("disabled",true);
            			$('#redManaFreeVal').attr("disabled",true);
            		}
        		}
    			$('#redModel').show();
    			if("Y"==isCycle){
    				$('#redCycleModel').show();
    				redCycleDataInit();
    			}

        	}else if("C18"==dealType){
        		//违约赎回，周期产品不支持违约赎回
        		$('#redDealNum').attr("disabled",false);
    			$('#redDealAmt').attr("disabled",false);
    			$('#redTitle').text("违约赎回要素");
    			$('#divLiquidatedDamages').show();
    			//赎回确认日等于平台日期
    			if(!$('#redConfirmDate').val() && $("#pageModel").val() != PAGE_MODEL_REFERENCE){
    				$('#redConfirmDate').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
    			}
    			$('#redModel').show();
    			if("Y"==isCycle){
    				redCycleDataInit();
    			}
        	}else if("C5"==dealType){
        		//add by gcl start
        		$('#expireDealNum').attr("disabled",true);
        		//add by gcl end

        		$('#expireTitle').text("到期要素");
        		//确认日等于平台日期
        		if(!$('#expireConfirmDate').val() && $("#pageModel").val() != PAGE_MODEL_REFERENCE){
        			$('#expireConfirmDate').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
        		}
        		//modify by wm 净值型产品到期支付日修改为下一工作日 start 2019-06-20
//        		if(!$('#expirePayDate').val()){
//        			$('#expirePayDate').val(Date.ofDate($('#mDate').val()).format('yyyy-MM-dd')).trigger("change");
//        		}
        		if(pageModel === PAGE_MODEL_NEW){
//        		    getNextWorkDay($('#mDate').val());
        		    $('#expirePayDate').val($('#mDate').val()).trigger("change");
        		}
        		//modify by wm 净值型产品到期支付日修改为下一工作日 end 2019-06-20
        		if("11"==dealMode){
        			$('#FinNetProductDuration_div_expire_layering').prop('hidden', false);
        			$('#expireNetValueDate').attr("disabled",true);
        			$('#expireDealAmt').attr("disabled",true);
        			$('#expireProfit').attr("disabled",true);
        			$('#expireCost').attr("disabled",true);
        			$('#expireManaFreeVal').attr("disabled",true);
        			layAmtExpireNetTable.load();
        		}
        		else{
        			//到期
                	if("N"==isCycle){
                		$('#expireDealAmt').attr("disabled",false);
            			$('#expireProfit').attr("disabled",false);
            			$('#expireCost').attr("disabled",false);
            			$('#expireManaFreeVal').attr("disabled",false);
            			//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 start
            			$('#expireNetValueDate').attr("disabled",false);
            			//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 end
            		}else{
            			//期次模块展示，并初始化和加载数据
            			$('#expireCycleModel').show();
            			expireCycleDataInit();
            			//周期型产品交易金额、到期收益、到期成本不可修改，通过列表汇总赋值
            			$('#expireDealAmt').attr("disabled",true);
            			$('#expireProfit').attr("disabled",true);
            			$('#expireCost').attr("disabled",true);
            			$('#expireManaFreeVal').attr("disabled",true);
            			//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 start
            			$('#expireNetValueDate').attr("disabled",true);
            			//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 end
            		}
        			//$('#expireNetValueDate').attr("disabled",false);
        		}
        		$('#expireModel').show();
        	}else if("C20"==dealType){
        		//提前终止
        		$('#expireTitle').text("提前终止要素");
        		//确认日等于平台日期
        		if(!$('#expireConfirmDate').val()){
        			$('#expireConfirmDate').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
        		}
        		//modify by wm 净值型产品提前终止支付日修改为下一工作日 start 2019-06-20
//        		if(!$('#expirePayDate').val()){
//        			$('#expirePayDate').val(Date.ofDate($('#mDate').val()).format('yyyy-MM-dd')).trigger("change");
//        		}
        		getNextWorkDay($('#mDate').val());
        		//modify by wm 净值型产品提前终止支付日修改为下一工作日 end 2019-06-20
        		if("11"==dealMode){
        			$('#FinNetProductDuration_div_expire_layering').prop('hidden', false);
        			$('#expireNetValueDate').attr("disabled",true);
        			$('#expireDealAmt').attr("disabled",true);
        			$('#expireProfit').attr("disabled",true);
        			$('#expireCost').attr("disabled",true);
        			layAmtExpireNetTable.load();
        		}
        		else{
        			//到期
                	if("N"==isCycle){
                		$('#expireDealAmt').attr("disabled",false);
            			$('#expireProfit').attr("disabled",false);
            			$('#expireCost').attr("disabled",false);
            			$('#expireManaFreeVal').attr("disabled",false);
            			//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 start
            			$('#expireNetValueDate').attr("disabled",false);
            			//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 end
            		}else{
            			//期次模块展示，并初始化和加载数据
            			$('#expireCycleModel').show();
            			expireCycleDataInit();
            			//周期型产品交易金额、到期收益、到期成本不可修改，通过列表汇总赋值
            			$('#expireDealAmt').attr("disabled",true);
            			$('#expireProfit').attr("disabled",true);
            			$('#expireCost').attr("disabled",true);
            			$('#expireManaFreeVal').attr("disabled",true);
            			//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 start
            			$('#expireNetValueDate').attr("disabled",true);
            			//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 end
            		}
        			//$('#expireNetValueDate').attr("disabled",false);
        		}
        		$('#expireModel').show();
        	}else if("C3"==dealType || "C6"==dealType){
        		//份额增强
            	if("N"==isCycle){
        			$('#addReduceDealNum').attr("disabled",false);
        			$('#addReduceDealAmt').attr("disabled",false);
        		}else{
        			//期次模块展示，并初始化和加载数据
        			$('#addReduceCycleModel').show();
        			// modified by wangzhiang BSTOZ-887 2019-10-26 start
        			addReduceCycleDataInit();
                    // modified by wangzhiang BSTOZ-887 2019-10-26 end
        			//周期型产品交易份额不可修改，通过列表汇总赋值
        			$('#addReduceDealNum').attr("disabled",true);
        			$('#addReduceDealAmt').attr("disabled",true);
        		}
            	if("C3"==dealType){
            		$('#addReduceTitle').text("强增要素");
            	}else if("C6"==dealType){
            		$('#addReduceTitle').text("强减要素");
            	}

            	if(!$('#addReduceConfirmDate').val()){
	            	//赎回确认日等于平台日期
	        		$('#addReduceConfirmDate').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
            	}
            	if("11"==dealMode){
        			$('#FinNetProductDuration_div_reduce_layering').prop('hidden', false);
        			$('#addReduceNetValueDate').attr("disabled",true);
        			$('#addReduceDealAmt').attr("disabled",true);
        			$('#addReduceDealNum').attr("disabled",true);
        			layAmtReduceNetTable.load();
        		}
        		else{
        			$('#addReduceNetValueDate').attr("disabled",false);
        			$('#addReduceDealAmt').attr("disabled",false);
        			$('#addReduceDealNum').attr("disabled",false);
        		}
        		$('#addReduceModel').show();
        	}
    	}else if("03"==profitType){
    		//货币型产品
    		if("C1"==dealType){
    			//认购
    			$('#subConfirmDate03').val(Date.ofDate($('#vDate').val()).format('yyyy-MM-dd'));
        		$('#subConfirmDate03').attr("disabled",true);
        		$('#subModel03').show();
    		}else if("C2"==dealType){
    			//申购
    			if(!$('#purConfirmDate03').val()){
    				$('#purConfirmDate03').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd'));
    			}
    			$('#purModel03').show();
    		}else if("C9"==dealType){
        		//货币产品现金分红
    			//计息结束日赋值
    			if(!$('#bonusCashEndDate03').val()){
    				queryBonusRule();
    			}
    			$('#bonusCashModel03').show();
        	}else if("C10"==dealType){
        		//货币产品红利再投
        		//计息结束日赋值
        		if(!$('#bonusShareEndDate03').val()){
    				queryBonusRule();
    			}
            	$(".hkxx").prop('hidden', true);
        		$('#bonusShareModel03').show();
        	}else if("C4"==dealType || "C17"==dealType || "C18"==dealType){
    			//赎回
        		//初始化时 赎回确认日等于平台日期
        		if(!$('#redConfirmDate03').val()){
        			$('#redConfirmDate03').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd')).trigger("change");
        		}
        		if("C18"==dealType){
        			$('#divLiquidatedDamages03').show();
        		}else{
        			$('#divLiquidatedDamages03').hide();
        		}
        		$('#redModel03').show();
        		if("C4"==dealType || "C17"==dealType){
        			var redPrinAmt = $('#redPrinAmt03').val();
        			$('#redDealNum').val(redPrinAmt);
        		}
    		}else if("C5"==dealType || "C20"==dealType){
    			//到期、提前终止
    			//确认日等于产品到期日
    			if(!$('#expireConfirmDate03').val()){
    				$('#expireConfirmDate03').val(Date.ofDate($('#mDate').val()).format('yyyy-MM-dd')).trigger("change");
    			}
    			$('#expireModel03').show();
    		}else if("C3"==dealType || "C6"==dealType){
    			if("C3"==dealType){
            		$('#addReduceTitle').text("强增要素");
            	}else if("C6"==dealType){
            		$('#addReduceTitle').text("强减要素");
            	}
    			if(!$('#addReduceConfirmDate03').val()){
    				$('#addReduceConfirmDate03').val(Date.ofDate(iPlatformDate).format('yyyy-MM-dd'));
    			}
    			$('#addReduceModel03').show();
    		}
    	}

    };
    var preRedCycleDataTableList = [];
    /**
     * 定义赎回期次子组合列表
     */
    var redCycleDataTable = $("#redCycleDataTable").joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/product/productduration/netproductDuration/FinNetProductDuration/getPeriodInfoList'),
            type: 'POST',
            data: function() {
            		return getRedParams();
    			}
        },
        cols:[
            {field:'finprodId',visible :false}
            ,{field:'branch',visible :false}
            ,{field:'redPeriodId',title: 'FinNetProductDuration.periodId',sortable:false, width:120,visible :$("#maneWay").val() == "01" ? true:false}
            ,{field:'vDate',title: 'FinNetProductDuration.cycleStartDate', width:120,type:'date',sortable:false}
            ,{field:'mDate',title: 'FinNetProductDuration.cycleEndDate', width:120,type:'date',sortable:false}
            ,{field:'redNetValueDate', title: 'FinNetProductDuration.netValueDate',type:'date', width:110,sortable:false,edit:true}
            ,{field:'redUnitNetValue', title: 'FinNetProductDuration.unitNetValue',type:'number', digit:4, width:80,sortable:false,edit:true}
            ,{field:'redDealNum', title: 'FinNetProductDuration.dealNum',type:'number', width:110,sortable:false,edit:true}
            ,{field:'redDealAmt', title: 'FinNetProductDuration.dealAmt',type:'number', width:110,sortable:false,edit:true}
            ,{field:'redFeeLc', title: 'FinNetProductDuration.redFeeLc', width:110,type:'number',sortable:false,edit:true}
            ,{field:'redTotalNetValue', title: 'FinNetProductDuration.totalNetValue',type:'number', width:110,sortable:false}
            ,{field:'redFee', title: 'FinNetProductDuration.redFee', width:110,type:'number',sortable:false,edit:true}
            ,{field:'redPurFeeHd', title: 'FinNetProductDuration.purFeeHd', width:110,type:'number',sortable:false,edit:true}
            ,{field:'redCost', title: 'FinNetProductDuration.redCost', width:110,type:'number',sortable:false,edit:true}
            ,{field:'redProfit', title: 'FinNetProductDuration.redProfit', width:110,type:'number',sortable:false,edit:true}
            ,{field:'redManaFreeVal', title: 'FinNetProductDuration.manaFree', width:110,type:'number',sortable:false,edit:true}
            ,{field:'redRemark', title: 'FinNetProductDuration.remark', width:110,sortable:false,edit:true}
        ],
        /**
         * 是否分页
         */
        page:false,
        /**
         * 行号
         */
        rowno: true,
        /**
         * checkBox
         */
       checkbox:$("#pageModel").val()=='0'||$("#pageModel").val()=='1'?true : false,
        /**
         * check 选中事件
         */
        check:function(ele, row){
        	redCycleDataTableCheck();
        },
        /**
         * 加载完成回调
         */
        done: function(table, allData){
        	preRedCycleDataTableList = redCycleDataTable.getAllRows();;
        	var redManaFreeValTotal = 0;		//浮动管理费总和
        	for(var i in table){
        		if(table[i].redManaFreeVal){
    				redManaFreeValTotal = redManaFreeValTotal + parseFloat(table[i].redManaFreeVal);
    			}
        	}
        	$("#redManaFreeVal").val(redManaFreeValTotal);
        	if($("#pageModel").val() == PAGE_MODEL_NEW){
        		//清空赎回要素数据
        		$("#redDealAmt").val(0);
        		$("#redDealNum").val(0);
        		$("#redTotalNetValue").val(0);
        		$("#redFeeLc").val(0);
        		$("#redFee").val(0);
        		$("#redPurFeeHd").val(0);
        		$("#redCost").val(0);
        		$("#redProfit").val(0);
        		$("#redManaFreeVal").val(0);

            	for(var i in table){
            		redCycleDataTable.renderRow(i, {redDealAmt: table[i].redDealNum*table[i].redUnitNetValue});
            		redCycleDataTable.renderRow(i, {redFeeLc: '0'});
            		redCycleDataTable.renderRow(i, {redFee: '0'});
            		redCycleDataTable.renderRow(i, {redPurFeeHd: '0'});
            		redCycleDataTable.renderRow(i, {redCost: '0'});
            		redCycleDataTable.renderRow(i, {redProfit: '0'});
            		redCycleDataTable.renderRow(i, {redManaFreeVal: '0'});
            		redCycleDataTable.renderRow(i, {redTotalNetValue: parseFloat(table[i].redDealAmt)-parseFloat(table[i].redFeeLc)});	//净金额
            		//初始化设置全部选中
            		redCycleDataTable.checkRow(i);
            	}
        	}
        },
        /**
         * table编辑前联动事件
         */
        beforeEditCell: function(preVal, field, rowData){
        	if(field == "redDealNum" && $('#continueFlag').val() == 'N'){
        		return false;
        	}else{
        		return true;
        	}
    	},
        /**
         * table编辑后联动事件
         */
        editCell: function(value, data, field, cell, column){
        	//修改设置list取消选择☆
        	redCycleDataTable.uncheck();
        	var redCycleDataTableList = redCycleDataTable.getAllRows();
        	if(field == "redFeeLc"){
        		redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
            			{redTotalNetValue: parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)-value});	//净金额
        	}
        	//交易份额
        	if(field == "redDealNum"){
        		if(preRedCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum
        				&& parseFloat(value) > parseFloat(preRedCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum)){
        			//延迟加载，重置交易份额
            		setTimeout(function () {
            			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
    	        				{redDealNum: preRedCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum});
            			//修改交易金额     净值*交易份额
                		if(redCycleDataTableList[data.LAY_TABLE_INDEX].redUnitNetValue
                				*preRedCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum == 0){
                			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	        				{redDealAmt: "0"});
                		}else{
                			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	        				{redDealAmt: redCycleDataTableList[data.LAY_TABLE_INDEX].redUnitNetValue
                						*preRedCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum});
                		}
                		redCycleDataTableList = redCycleDataTable.getAllRows();
                		//修改净金额
                		if(parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
            					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc == 0){
                			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
                    				{redTotalNetValue: "0"});
                		}
            			else{
            				redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
            	    				{redTotalNetValue: parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
            	    					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc});
            			}
        			}, 100);
        		}
        		else{
        			//修改交易金额     净值*交易份额
            		if(redCycleDataTableList[data.LAY_TABLE_INDEX].redUnitNetValue*value == 0){
            			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
    	        				{redDealAmt: "0"});
            		}else{
            			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
    	        				{redDealAmt: redCycleDataTableList[data.LAY_TABLE_INDEX].redUnitNetValue*value});
            		}
            		redCycleDataTableList = redCycleDataTable.getAllRows();
            		//修改净金额
            		if(parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
        					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc == 0){
            			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
                				{redTotalNetValue: "0"});
            		}
        			else{
        				redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	    				{redTotalNetValue: parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
        	    					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc});
        			}
        		}
        	}
        	//交易金额
        	if(field == "redDealAmt"){
        		//修改净金额
        		if(parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
    					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc == 0){
        			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
            				{redTotalNetValue: "0"});
        		}
    			else{
    				redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
    	    				{redTotalNetValue: parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
    	    					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc});
    			}
        	}
        	//净值日期选择联动
    		if(field == "redNetValueDate"){

    		    			// add by wj 20200820
                if('C4' == $("#dealType").val() || 'C17' == $("#dealType").val()){
                    if($('#isCycle').val() == "Y"){
                        //新建
                        if($("#pageModel").val() == PAGE_MODEL_NEW){
                             if( DateUtil.parse(value).Format('yyyyMMdd') - dateLongFormatStr(data.mDate)   >0){
                                    Joyin.error("产品赎回时，期次信息中，净值日不能大于期次结束日");
                                    redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
                                                	    				{redNetValueDate: ''});
                                     return;
                             }
                        }
                    }
                }

    			Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getTrdValueByCdate',
    	        	{finprodId:$('#finprodId').val(),redNetValueDate:value,dealType:$("#dealType").val()},
    	        	function(result) {
    	        		//修改净值
    	        		redCycleDataTable.renderRow(data.LAY_TABLE_INDEX, {redUnitNetValue: result});
    	        		redCycleDataTableList = redCycleDataTable.getAllRows();
    	        		//修改交易金额     净值*交易份额
    	        		if(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum*result == 0){
    	        			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	        				{redDealAmt: "0"});
    	        		}else{
    	        			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	        				{redDealAmt: redCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum*result});
    	        		}
    	        		redCycleDataTableList = redCycleDataTable.getAllRows();
    	        		//修改净金额
    	        		if(parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
    	    					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc == 0){
    	        			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
    	            				{redTotalNetValue: "0"});
    	        		}
    	    			else{
    	    				redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
    	    	    				{redTotalNetValue: parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
    	    	    					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc});
    	    			}
    	        	}
        		);
    		}
    		//单位净值
    		if(field == "redUnitNetValue"){
        		//修改交易金额     净值*交易份额
        		if(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum*value == 0){
        			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{redDealAmt: "0"});
        		}else{
        			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{redDealAmt: redCycleDataTableList[data.LAY_TABLE_INDEX].redDealNum*value});
        		}
        		redCycleDataTableList = redCycleDataTable.getAllRows();
        		//修改净金额
        		if(parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
    					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc == 0){
        			redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
            				{redTotalNetValue: "0"});
        		}
    			else{
    				redCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
    	    				{redTotalNetValue: parseFloat(redCycleDataTableList[data.LAY_TABLE_INDEX].redDealAmt)
    	    					-redCycleDataTableList[data.LAY_TABLE_INDEX].redFeeLc});
    			}
    		}
        }
    });

    function dateLongFormatStr(longTypeDate){
         var dateTypeDate = "";
         var date = new Date();
         date.setTime(longTypeDate);
         dateTypeDate += date.getFullYear(); //年
         dateTypeDate +=  getMonth(date); //月
         dateTypeDate +=  getDay(date); //日
         return dateTypeDate;
    }

    function getMonth(date){
     var month = "";
     month = date.getMonth() + 1; //getMonth()得到的月份是0-11
     if(month<10){
      month = "0" + month;
     }
     return month;
    }

    //返回01-30的日期
    function getDay(date){
     var day = "";
     day = date.getDate();
     if(day<10){
      day = "0" + day;
     }
     return day;
    }

    /**
     * 设置赎回期次子组合列表请求参数
     */
    var getRedParams = function() {
        var pageParam = {
        		finprodId:$("#finprodId").val()
        		,dealType:$("#dealType").val()
        		,confirmDate:$("#redConfirmDate").val()
        		,netValueDate:$("#redNetValueDate").val()
        		,workflowId : workflowId
        		, tradeId : $('#tradeId').val()
				,dealMode : $('#dealMode').val()
			}
        return pageParam;
    };
    /**
     * 赎回期次子组合列表加载
     */
    var redCycleDataInit = function(){
		if(redCycleDataTable.table){
			redCycleDataTable.reload();
		}
		else{
			redCycleDataTable.load();
		}

    };
    /**
     * 赎回期次子产品列表check选中事件
     */
    var redCycleDataTableCheck = function() {

    	var redCycleDataTableList = redCycleDataTable.getAllCheckedRows();
    	var redDealNumTotal = 0;
    	var redDealAmtTotal = 0;
    	var redFeeLcTotal = 0;		//赎回费(产品留存)总和
    	var redFeeTotal = 0;		//赎回费总和
    	var redTotalNetValueTotal = 0;		//净金额总和
    	var redPurFeeHdTotal = 0;		//申购费(后端)总和
    	var redCostTotal = 0;		//赎回成本总和
    	var redProfitTotal = 0;		//赎回收益总和
    	var redManaFreeValTotal = 0;		//浮动管理费总和
    	for(var i in redCycleDataTableList){

    		if(redCycleDataTableList[i].redDealNum){
				redDealNumTotal = redDealNumTotal + parseFloat(redCycleDataTableList[i].redDealNum);
    		}
			if(redCycleDataTableList[i].redDealAmt){
    			redDealAmtTotal = redDealAmtTotal + parseFloat(redCycleDataTableList[i].redDealAmt);
    		}
			if(redCycleDataTableList[i].redFeeLc){
				redFeeLcTotal = redFeeLcTotal + parseFloat(redCycleDataTableList[i].redFeeLc);
			}
			if(redCycleDataTableList[i].redTotalNetValue){
				redTotalNetValueTotal = redTotalNetValueTotal + parseFloat(redCycleDataTableList[i].redTotalNetValue);
			}
			if(redCycleDataTableList[i].redFee){
				redFeeTotal = redFeeTotal + parseFloat(redCycleDataTableList[i].redFee);
			}
			if(redCycleDataTableList[i].totalNetValue){
				redTotalNetValueTotal = redTotalNetValueTotal + parseFloat(redCycleDataTableList[i].redTotalNetValue);
			}
			if(redCycleDataTableList[i].redPurFeeHd){
				redPurFeeHdTotal = redPurFeeHdTotal + parseFloat(redCycleDataTableList[i].redPurFeeHd);
			}
			if(redCycleDataTableList[i].redCost){
				redCostTotal = redCostTotal + parseFloat(redCycleDataTableList[i].redCost);
			}
			if(redCycleDataTableList[i].redProfit){
				redProfitTotal = redProfitTotal + parseFloat(redCycleDataTableList[i].redProfit);
			}
			if(redCycleDataTableList[i].redManaFreeVal){
				redManaFreeValTotal = redManaFreeValTotal + parseFloat(redCycleDataTableList[i].redManaFreeVal);
			}

    	}
    	//  add by jiangpeng on 20200731  start
		//  赎回，交易份额含小数位，会导致净金额 交易金额 小数位展示6位，应于非周期一致保留两位
		redTotalNetValueTotal = JoyinUtil.formatNumber(redTotalNetValueTotal,2,null,2);
		redDealAmtTotal = JoyinUtil.formatNumber(redDealAmtTotal,2,null,2);
		//  add by jiangpeng on 20200731 end
    	$("#redDealNum").val(redDealNumTotal);
    	$("#redDealAmt").val(redDealAmtTotal);
		$("#redFeeLc").val(redFeeLcTotal);
		$("#redFee").val(redFeeTotal);
		$("#redTotalNetValue").val(redTotalNetValueTotal);
		$("#redPurFeeHd").val(redPurFeeHdTotal);
		$("#redCost").val(redCostTotal);
		$("#redProfit").val(redProfitTotal);
		$("#redManaFreeVal").val(redManaFreeValTotal);
	};
    /**
     * 定义到期、中止子组合列表
     */
    var expireCycleDataTable = $("#expireCycleDataTable").joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/product/productduration/netproductDuration/FinNetProductDuration/getPeriodInfoList'),
            type: 'POST',
            data: function() {
                	return getExpireParams();
        		}
        },
        cols:[
        	{field:'finprodId',visible :false}
        	,{field:'branch',visible :false}
        	,{field:'redPeriodId',title: 'FinNetProductDuration.periodId',sortable:false, width:120,visible :$("#maneWay").val() == "01" ? true:false}
            ,{field:'vDate',title: 'FinNetProductDuration.cycleStartDate', width:120,type:'date',sortable:false}
            ,{field:'mDate',title: 'FinNetProductDuration.cycleEndDate', width:120,type:'date',sortable:false}
            ,{field:'expireNetValueDate', title: 'FinNetProductDuration.netValueDate',type:'date', width:100,sortable:false,edit:true}
            ,{field:'expireUnitNetValue', title: 'FinNetProductDuration.unitNetValue',type:'number', digit:4, width:100,sortable:false,edit:true}
            ,{field:'expireDealNum', title: 'FinNetProductDuration.dealNum',type:'number', width:100,sortable:false}
            ,{field:'expireDealAmt', title: 'FinNetProductDuration.dealAmt',type:'number', width:100,sortable:false,edit:true}
            ,{field:'expireCost', title: 'FinNetProductDuration.expireCost',type:'number', width:100,sortable:false,edit:true}
            ,{field:'expireProfit', title: 'FinNetProductDuration.expireProfit',type:'number', width:100,sortable:false,edit:true}
            ,{field:'expireManaFreeVal', title: 'FinNetProductDuration.manaFree',type:'number', width:100,sortable:false,edit:true}
        ],
        /**
         * 是否分页
         */
        page:false,
        /**
         * 行号
         */
        rowno: true,
        /**
         * checkBox
         */
        checkbox:$("#pageModel").val()=='0'?true : false,
        /**
         * check 选中事件
         */
        check:function(ele, row){
        	expireCycleDataTableCheck();
        },
        /**
         * 加载完成回调
         */
        done: function(table, allData){
        	var expireManaFreeValTotal = 0;		//浮动管理费总和
        	for(var i in table){
        		if(table[i].expireManaFreeVal){
        			expireManaFreeValTotal = expireManaFreeValTotal + parseFloat(table[i].expireManaFreeVal);
    			}
        	}
        	$("#expireManaFreeVal").val(expireManaFreeValTotal);
        	if($("#pageModel").val() == PAGE_MODEL_NEW){
        		$("#expireDealAmt").val(0);
        		$("#expireDealNum").val(0);
        		$("#expireCost").val(0);
        		$("#expireProfit").val(0);
        		$("#expireManaFreeVal").val(0);
            	for(var i in table){
            		expireCycleDataTable.renderRow(i, {expireDealAmt: table[i].expireDealNum*table[i].expireUnitNetValue});
            		expireCycleDataTable.renderRow(i, {expireCost: '0'});
            		expireCycleDataTable.renderRow(i, {expireProfit: '0'});
            		expireCycleDataTable.renderRow(i, {expireManaFreeVal: '0'});
            		//初始化设置全部选中
            		expireCycleDataTable.checkRow(i);
            	}
        	}
        },
        /**
         * table编辑后联动事件
         */
        editCell: function(value, data, field, cell, column){
        	//修改设置list取消选择☆
        	expireCycleDataTable.uncheck();
        	var expireCycleDataTableList = expireCycleDataTable.getAllRows();
        	//净值日期选择联动
    		if(field == "expireNetValueDate"){
    			Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getTrdValueByCdate',
    	        	{finprodId:$('#finprodId').val(),redNetValueDate:value,dealType:$("#dealType").val()},
    	        	function(result) {
    	        		//修改净值
    	        		expireCycleDataTable.renderRow(data.LAY_TABLE_INDEX, {expireUnitNetValue: result});
    	        		expireCycleDataTableList = expireCycleDataTable.getAllRows();
    	        		//修改交易金额     净值*交易份额
    	        		if(expireCycleDataTableList[data.LAY_TABLE_INDEX].expireDealNum*result == 0){
    	        			expireCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	        				{expireDealAmt: "0"});
    	        		}else{
    	        			expireCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	        				{expireDealAmt: expireCycleDataTableList[data.LAY_TABLE_INDEX].expireDealNum*result});
    	        		}
    	        	}
        		);
    		}
    		//净值修改联动
    		if(field == "expireUnitNetValue"){
        		//修改交易金额     净值*交易份额
        		if(expireCycleDataTableList[data.LAY_TABLE_INDEX].expireDealNum*value == 0){
        			expireCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{expireDealAmt: "0"});
        		}else{
        			expireCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{expireDealAmt: expireCycleDataTableList[data.LAY_TABLE_INDEX].expireDealNum*value});
        		}
    		}
    		//交易份额修改联动
    		if(field == "expireDealNum"){
        		//修改交易金额     净值*交易份额
        		if(expireCycleDataTableList[data.LAY_TABLE_INDEX].expireUnitNetValue*value == 0){
        			expireCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{expireDealAmt: "0"});
        		}else{
        			expireCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{expireDealAmt: expireCycleDataTableList[data.LAY_TABLE_INDEX].expireUnitNetValue*value});
        		}
    		}
        }
    });
    /**
     * 设置到期、中止子组合列表请求参数
     */
    var getExpireParams = function() {
        var pageParam = {
        		finprodId:$("#finprodId").val()
        		,dealType:$("#dealType").val()
        		,confirmDate:$("#expireConfirmDate").val()
        		,netValueDate:$("#expireNetValueDate").val()
        		,workflowId : workflowId
        		, tradeId : $('#tradeId').val()
			}
        return pageParam;
    };
    /**
     * 到期、中止子组合列表加载
     */
    var expireCycleDataInit = function(){
    	if(expireCycleDataTable.table){
    		expireCycleDataTable.reload();
    	}
    	else{
    		expireCycleDataTable.load();
    	}
    };
    /**
     * 到期 提前终止期次子产品列表check选中事件
     */
    var expireCycleDataTableCheck = function() {
    	var expireCycleDataTableList = expireCycleDataTable.getAllCheckedRows();
    	var expireDealNumTotal = 0;
    	var expireDealAmtTotal = 0;
    	var expireCostTotal = 0;		//赎回成本总和
    	var expireProfitTotal = 0;		//赎回收益总和
    	var expireManaFreeValTotal = 0;		//浮动管理费总和
    	for(var i in expireCycleDataTableList){
    		if(expireCycleDataTableList[i].expireDealNum){
				expireDealNumTotal = expireDealNumTotal + parseFloat(expireCycleDataTableList[i].expireDealNum);
    		}
			if(expireCycleDataTableList[i].expireDealAmt){
    			expireDealAmtTotal = expireDealAmtTotal + parseFloat(expireCycleDataTableList[i].expireDealAmt);
    		}
			if(expireCycleDataTableList[i].expireCost){
				expireCostTotal = expireCostTotal + parseFloat(expireCycleDataTableList[i].expireCost);
			}
			if(expireCycleDataTableList[i].expireProfit){
				expireProfitTotal = expireProfitTotal + parseFloat(expireCycleDataTableList[i].expireProfit);
			}
			if(expireCycleDataTableList[i].expireManaFreeVal){
				expireManaFreeValTotal = expireManaFreeValTotal + parseFloat(expireCycleDataTableList[i].expireManaFreeVal);
			}

    	}
    	$("#expireDealNum").val(expireDealNumTotal);
    	$("#expireDealAmt").val(expireDealAmtTotal);
		$("#expireCost").val(expireCostTotal);
		$("#expireProfit").val(expireProfitTotal);
		$("#expireManaFreeVal").val(expireManaFreeValTotal);
	};
    /**
     * 定义强增强减子组合列表
     */
    var addReduceCycleDataTable=$("#addReduceCycleDataTable").joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/product/productduration/netproductDuration/FinNetProductDuration/getPeriodInfoList'),
            type: 'POST',
            data:function() {
                return getAddReduceParams();
            }
        },
        cols:[
        	{field:'finprodId',visible :false}
        	,{field:'branch',visible :false}
        	,{field:'redPeriodId',title: 'FinNetProductDuration.periodId',sortable:false, width:120,visible :$("#maneWay").val() == "01" ? true:false}
            ,{field:'vDate',title: 'FinNetProductDuration.cycleStartDate', width:120,type:'date',sortable:false}
            ,{field:'mDate',title: 'FinNetProductDuration.cycleEndDate', width:120,type:'date',sortable:false}
            ,{field:'addReduceNetValueDate', title: 'FinNetProductDuration.netValueDate',type:'date', width:100,sortable:false,edit:true}
            ,{field:'addReduceUnitNetValue', title: 'FinNetProductDuration.unitNetValue',type:'number', digit:4, width:100,sortable:false,edit:true}
            ,{field:'addReduceDealNum', title: 'FinNetProductDuration.dealNum',type:'number', width:120,sortable:false,edit:true}
            ,{field:'addReduceDealAmt', title: 'FinNetProductDuration.dealAmt',type:'number', width:120,sortable:false,edit:true}
        ],
        /**
         * 是否分页
         */
        page:false,
        /**
         * 行号
         */
        rowno: true,
        /**
         * checkBox
         */
        checkbox:$("#pageModel").val()=='0'?true : false,
        /**
         * check 选中事件
         */
        check:function(ele, row){
        	addReduceCycleDataTableCheck();
        },
        /**
         * 加载完成回调
         */
        done: function(table, allData){
        	if($("#pageModel").val() == PAGE_MODEL_NEW){
            	for(var i in table){
            		addReduceCycleDataTable.renderRow(i, {addReduceDealAmt: table[i].addReduceDealNum*table[i].addReduceUnitNetValue});
            		//初始化设置全部选中
            		addReduceCycleDataTable.checkRow(i);
            	}
            	//交易份额
            	$("#addReduceDealNum").attr("disabled","disabled");
            	//交易金额
            	$("#addReduceDealAmt").attr("disabled","disabled");
        	}
        },
        /**
         * table编辑后联动事件
         */
        editCell: function(value, data, field, cell, column){
        	//修改设置list取消选择☆
        	addReduceCycleDataTable.uncheck();
        	var addReduceCycleDataTableList = addReduceCycleDataTable.getAllRows();
        	//净值日期选择联动
    		if(field == "addReduceNetValueDate"){
    			Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getTrdValueByCdate',
    	        	{finprodId:$('#finprodId').val(),redNetValueDate:value,dealType:$("#dealType").val()},
    	        	function(result) {
    	        		//修改净值
    	        		addReduceCycleDataTable.renderRow(data.LAY_TABLE_INDEX, {addReduceUnitNetValue: result});
    	        		addReduceCycleDataTableList = addReduceCycleDataTable.getAllRows();
    	        		//修改交易金额     净值*交易份额
    	        		if(addReduceCycleDataTableList[data.LAY_TABLE_INDEX].addReduceDealNum*result == 0){
    	        			addReduceCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	        				{addReduceDealAmt: "0"});
    	        		}else{
    	        			addReduceCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
        	        				{addReduceDealAmt: addReduceCycleDataTableList[data.LAY_TABLE_INDEX].addReduceDealNum*result});

    	        		}
    	        	}
        		);
    		}
    		//净值修改联动
    		if(field == "addReduceUnitNetValue"){
        		//修改交易金额     净值*交易份额
        		if(addReduceCycleDataTableList[data.LAY_TABLE_INDEX].addReduceDealNum*value == 0){
        			addReduceCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{addReduceDealAmt: "0"});
        		}else{
        			addReduceCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{addReduceDealAmt: addReduceCycleDataTableList[data.LAY_TABLE_INDEX].addReduceDealNum*value});
        		}
    		}
    		//交易份额修改联动
    		if(field == "addReduceDealNum"){
        		//修改交易金额     净值*交易份额
        		if(addReduceCycleDataTableList[data.LAY_TABLE_INDEX].addReduceUnitNetValue*value == 0){
        			addReduceCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{addReduceDealAmt: "0"});
        		}else{
        			addReduceCycleDataTable.renderRow(data.LAY_TABLE_INDEX,
	        				{addReduceDealAmt: addReduceCycleDataTableList[data.LAY_TABLE_INDEX].addReduceUnitNetValue*value});
        		}
    		}
        }
    });
    /**
     * 设置强增强减子组合列表请求参数
     */
    var getAddReduceParams = function() {
        var pageParam =  {finprodId:$("#finprodId").val()
        		,dealType:$("#dealType").val()
        		,confirmDate:$("#addReduceConfirmDate").val()
        		,netValueDate:$("#addReduceNetValueDate").val()
				,workflowId : workflowId
				, tradeId : $('#tradeId').val()}

        return pageParam;
    };
    /**
     * 强增强减子组合列表加载
     */
    var addReduceCycleDataInit = function(){
    	if(addReduceCycleDataTable.table){
    		addReduceCycleDataTable.reload();
    	}
    	else{
    		addReduceCycleDataTable.load();
    	}
    };
    /**
     * 强增强减期次子产品列表check选中事件
     */
    var addReduceCycleDataTableCheck = function() {
    	var addReduceCycleDataTableList = addReduceCycleDataTable.getAllCheckedRows();
    	var addReduceDealNumTotal = 0;
    	var addReduceDealAmtTotal = 0;
    	for(var i in addReduceCycleDataTableList){
    		if(addReduceCycleDataTableList[i].addReduceDealNum){
				addReduceDealNumTotal = addReduceDealNumTotal + parseFloat(addReduceCycleDataTableList[i].addReduceDealNum);
    		}
			if(addReduceCycleDataTableList[i].addReduceDealAmt){
    			addReduceDealAmtTotal = addReduceDealAmtTotal + parseFloat(addReduceCycleDataTableList[i].addReduceDealAmt);
    		}
    	}
    	$("#addReduceDealNum").val(addReduceDealNumTotal);
    	$("#addReduceDealAmt").val(addReduceDealAmtTotal);
	};

    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
            window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                var data = $.serializeToObject("#submitForm", true);
                data.counterId = '324';
                var dealType = $('#dealType').val();
                var profitType = $('#profitType').val();
				if (!$("#finprodMarketId").val()) {
					Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038',JoyinUtil.getLabel("FinNetProductDuration.finprodMarketId")))
					return;
				}
				if (!$("#dealType").val()) {
					Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038',JoyinUtil.getLabel("FinNetProductDuration.dealType")));
					return;
				}
                if("01"==profitType){
                	if("C1"==dealType){
                    	data.tradeAmt = $('#subDealAmt').val();
                    	data.tradeDate = $('#subConfirmDate').val();
                    }else if("C2"==dealType){
                    	data.tradeAmt = $('#purDealAmt').val();
                    	data.tradeDate = $('#purConfirmDate').val();
                    }else if("C4"==dealType || "C17"==dealType || "C18"==dealType){
                    	data.tradeAmt = $('#redTotalNetValue').val();
                    	data.tradeDate = $('#redPayDate').val();
                    }else if("C5"==dealType || "C20"==dealType){
                    	data.tradeAmt = $('#expireDealAmt').val();
                    	data.tradeDate = $('#expirePayDate').val();
                    }else if("C3"==dealType || "C6"==dealType){
                        var repPageId = 'FinNetProductDuration';
                    	data.tradeAmt = $('#addReduceDealAmt').val();
                    	data.tradeDate = $('#addReduceConfirmDate').val();
                    }
                }else if("03"==profitType){
                	if("C1"==dealType){
                    	data.tradeAmt = $('#subDealAmt03').val();
                    	data.tradeDate = $('#subConfirmDate03').val();
                    }else if("C2"==dealType){
                    	data.tradeAmt = $('#purDealAmt03').val();
                    	data.tradeDate = $('#purConfirmDate03').val();
                    }else if("C4"==dealType || "C17"==dealType || "C18"==dealType){
                    	data.tradeAmt = $('#redTotalNetValue03').val();
                    	data.tradeDate = $('#redPrinPayDate03').val();
                    }else if("C5"==dealType || "C20"==dealType){
                    	data.tradeAmt = $('#expireDealAmt03').val();
                    	data.tradeDate = $('#expirePrinPayDate03').val();
                    }else if("C3"==dealType || "C6"==dealType){
                    	data.tradeAmt = $('#addReduceDealAmt03').val();
                    	data.tradeDate = $('#addReduceConfirmDate03').val();
                    }else if("C9"==dealType){
                    	data.tradeAmt = $('#bonusCashBonusAmt03').val();
                    	data.tradeDate = $('#bonusCashBonusDate03').val();
                    }else if("C10"==dealType){
                    	data.tradeAmt = $('#bonusShareAmt03').val();
                    	data.tradeDate = $('#bonusShareConfirmDate03').val();
                    }
                }
                //是否轧差清算 yzg 20191108 start
                var str = $('#finprodMarketId').select2('data')[0].object.split(',');
                if(str != null && str.length==17){
                	data.liquidationYesno = str[16];
                }
                //是否轧差清算 yzg 20191108 end

                data.ps = dealType;
                data.finprodType2 = 'F16';

                return {
                    'isTotalTrade':'Y'
                    ,'bankLendingType':null
                    ,'portfolioId':$("#portfolioId").val()
                    ,'deal':data
                    ,'isLiquidation':true
                    , 'repPageId': repPageId
                };
            });
        }
    };

    /**
     * 界面数据变化重新生成划款指令
     */
    var reloadSettle = function(){
    	var dealType = $('#dealType').val();
    	var profitType = $('#profitType').val();
    	var portfolioId = $('#portfolioId').val();
        var ccy = $('#finccy').val();
        var tradeDate;
    	var tradeAmt;
    	if("01"==profitType){
	        if("C1"==dealType){
	        	tradeAmt = $('#subDealAmt').val();
	        	tradeDate = $('#subConfirmDate').val();
	        }else if("C2"==dealType){
	        	tradeAmt = $('#purDealAmt').val();
	        	tradeDate = $('#purConfirmDate').val();
	        }else if("C4"==dealType || "C17"==dealType || "C18"==dealType){
	        	tradeAmt = $('#redTotalNetValue').val();
	        	tradeDate = $('#redPayDate').val();
	        }else if("C5"==dealType || "C20"==dealType){
            	tradeAmt = $('#expireDealAmt').val();
            	tradeDate = $('#expirePayDate').val();
            }else if("C3"==dealType || "C6"==dealType){
            	tradeAmt = $('#addReduceDealAmt').val();
            	tradeDate = $('#addReduceConfirmDate').val();
            }
    	}else if("03"==profitType){
    		if("C1"==dealType){
	        	tradeAmt = $('#subDealAmt03').val();
	        	tradeDate = $('#subConfirmDate03').val();
	        }else if("C2"==dealType){
	        	tradeAmt = $('#purDealAmt03').val();
	        	tradeDate = $('#purConfirmDate03').val();
	        }else if("C4"==dealType || "C17"==dealType || "C18"==dealType){
	        	tradeAmt = $('#redTotalNetValue03').val();
	        	tradeDate = $('#redPrinPayDate03').val();
	        }else if("C5"==dealType || "C20"==dealType){
            	tradeAmt = $('#expireDealAmt03').val();
            	tradeDate = $('#expirePrinPayDate03').val();
            }else if("C3"==dealType || "C6"==dealType){
            	tradeAmt = $('#addReduceDealAmt03').val();
            	tradeDate = $('#addReduceConfirmDate03').val();
            }else if("C9"==dealType){
            	tradeAmt = $('#bonusCashBonusAmt03').val();
            	tradeDate = $('#bonusCashBonusDate03').val();
            }else if("C10"==dealType){
            	tradeAmt = $('#bonusShareAmt03').val();
            	tradeDate = $('#bonusShareConfirmDate03').val();
            }
    	}
    	if(tradeAmt && portfolioId && ccy && dealType && tradeDate){
            $('#bussTrdTransferOrder_btn_generate').click();
        }
    };

    /**
     * 认购确认日变动，如果是周期型产品，并且之前做过认购，自动带出期次信息
     */
    var subConfirmDateChange = function(){
    	var isCycle = $('#isCycle').val();
    	if("Y"==isCycle){
            var finprodId = $('#finprodId').val();
            var subConfirmDate = $('#subConfirmDate').val();
            Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getCycleInfo',
            	{'finprodId':finprodId,'subConfirmDate':subConfirmDate},
            	function(result) {
	                var r = result.data;
	                var subCycleEndDate = Date.ofDate(r.subCycleEndDate).format('yyyy-MM-dd');
	                var subCyclePayDate = Date.ofDate(r.subCyclePayDate).format('yyyy-MM-dd');

	                //赋值
	                $('#subCycleEndDate').val(subCycleEndDate).attr("disabled",true);
	                $('#subCyclePayDate').val(subCyclePayDate).attr("disabled",true);;
            	}
            );
    	}
    };
    var redDealAmtInit= function(){
    	var redDealNum = $('#redDealNum').val();
    	var redUnitNetValue = $('#redUnitNetValue').val();
    	var redDealAmt = 0;
    	if(redDealNum && redUnitNetValue){
    		redDealAmt = Number(JoyinMath.doMul(redDealNum,redUnitNetValue,2));
    	}
    	//赎回金额千分位显示
    	$('#redDealAmt').val(get_thousand_num(redDealAmt)).trigger("change");
    };

    var redTotalNetValueInit = function(){
    	var redDealAmt = $('#redDealAmt').val();
    	var redFeeLc = $('#redFeeLc').val();
    	if(redDealAmt){
    		if(!redFeeLc){
    			redFeeLc = Number(0);
    		}
    		//净金额千分位显示
    		$('#redTotalNetValue').val(get_thousand_num(JoyinMath.doMinus(Number(redDealAmt),redFeeLc)));

    	}
    };

    //实现金额数目千分位显示
    function get_thousand_num(num) {
             return num.toString().replace(/\d+/, function (n) {
             // 先提取整数部分
             return n.replace(/(\d)(?=(\d{3})+$)/g, function ($1) {
                   // 对整数部分添加分隔符
                   return $1 + ",";
                    });
               });
      }

    /**
     * 货币型计算赎回理论金额
     * 	= 剩余未分配利润*(交易本金/剩余本金)
     */
    var calRedProfit03 = function () {
    	var netValueDate = null;
    	var dealDate = null;
        var finprodId = $("#finprodId").val();
    	var dealType = $("#dealType").val();
    	var profitType = $("#profitType").val();
    	var isCycle = $("#isCycle").val();
    	if("03"==profitType){
    		if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
        		netValueDate = $("#redConfirmDate03").val();
        		var object = {'netValueDate':netValueDate,'dealDate':dealDate,'finprodId':finprodId};
                Joyin.ajaxSync(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getNetValueInfoForHB', object,
            		function (data) {
	                    if (data &&data.purDealNum!=0 &&  $('#redPrinAmt03').val() && $('#redPrinAmt03').val()!=0){
	            			$('#redProfit03').val(JoyinMath.doMul(data.purDealAmt,JoyinMath.doDiv($('#redPrinAmt03').val(), data.purDealNum,10),2));
	                    }else{
	            			$('#redProfit03').val(null);
	                    }

            		}
                )
        	}
    	}
    };

    /**
     * 获取净值信息：净值日、净值
     * type:
     * 1、根据确认日，查询最近的净值日净值信息 ；
     * 2、根据净值日，获取净值日的净值信息
     */
    var getNetValueInfo = function (type) {
    	var netValueDate = null;
    	var dealDate = null;
        var finprodId = $("#finprodId").val();
    	var dealType = $("#dealType").val();
    	var profitType = $("#profitType").val();
    	var isCycle = $("#isCycle").val();

    	var dealMode = $("#dealMode").val();

    	if("01"==profitType){
    		if(1==type){
        		if("C2"==dealType){
        			dealDate = $("#purConfirmDate").val();
            	}else if("C3"==dealType || "C6"==dealType){
        			dealDate = $("#addReduceConfirmDate").val();
            	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
            		dealDate = $("#redConfirmDate").val();
            	}else if("C5"==dealType || "C20"==dealType){
            		dealDate = $("#expireConfirmDate").val();
            	}
        	}else if(2==type){
        		if("C2"==dealType){
            		netValueDate = $("#purNetValueDate").val();
            	}else if("C3"==dealType || "C6"==dealType){
            		netValueDate = $("#addReduceNetValueDate").val();
            	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
            		netValueDate = $("#redNetValueDate").val();
            	}else if("C5"==dealType || "C20"==dealType){
            		netValueDate = $("#expireNetValueDate").val();
            	}
        	}else if(3==type){
        		if("C2"==dealType){
        			dealDate = $("#purNetValueDate").val();
            	}else if("C3"==dealType || "C6"==dealType){
            		dealDate = $("#addReduceNetValueDate").val();
            	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
            		dealDate = $("#redNetValueDate").val();
            	}else if("C5"==dealType || "C20"==dealType){
            		dealDate = $("#expireNetValueDate").val();
            	}
        	}
    	}else if("03"==profitType){
    		if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
        		netValueDate = $("#redConfirmDate03").val();
        	}else if("C5"==dealType || "C20"==dealType){
        		netValueDate = $("#expireConfirmDate03").val();
        	}
    	}

        var object = {'netValueDate':netValueDate,'dealDate':dealDate,'finprodId':finprodId};
        //净值型产品分档时，界面不展示相关内容，由分层汇总获得
        if(dealMode == '11'){
        	if("01"==profitType){
        		if(1==type){
            		if("C2"==dealType){
            			$('#purNetValueDate').val(null);
                    	$('#purUnitNetValue').val(null);
                	}else if("C3"==dealType || "C6"==dealType){
                		$('#addReduceNetValueDate').val(null);
                        $('#addReduceUnitNetValue').val(null);
                	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
                		$('#redNetValueDate').val(null);
                		$('#redUnitNetValue').val(null);
                	}else if("C5"==dealType || "C20"==dealType){
                		$('#expireNetValueDate').val(null);
                    	$('#expireUnitNetValue').val(null);
                	}
            	}else{
                	if("C2"==dealType){
                		$('#purUnitNetValue').val(null);
                	}else if("C3"==dealType || "C6"==dealType){
                        $('#addReduceUnitNetValue').val(null);
                	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
                		$('#redUnitNetValue').val(null);
                	}else if("C5"==dealType || "C20"==dealType){
                		$('#expireUnitNetValue').val(null);
                	}
            	}
        	}
        }
        else if(((1==type || 3==type) && dealDate) || (2==type && netValueDate)) {
            //获取净值参考日
            Joyin.ajax(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getNetValueInfo1', object, function (data) {
                if (data) {
                	if("01"==profitType){
                		if(1==type || 3==type){
                    		if("C2"==dealType){
                    			$('#purNetValueDate').val(Date.ofDate(data.purNetValueDate).format('yyyy-MM-dd'));
                                $('#purUnitNetValue').val(data.purUnitNetValue);
    	                	}else if("C3"==dealType || "C6"==dealType){
    	                		$('#addReduceNetValueDate').removeAttr("disabled");
    	                		$('#addReduceNetValueDate').val(Date.ofDate(data.purNetValueDate).format('yyyy-MM-dd'));
                                $('#addReduceUnitNetValue').val(data.purUnitNetValue);
                                if("Y"==isCycle){
                                	$('#addReduceNetValueDate').attr("disabled","disabled");
                                	$('#addReduceNetValueDate').val("");
                                    $('#addReduceUnitNetValue').val("");
                            		addReduceCycleDataInit();
                                }
    	                	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
    	                		$('#redNetValueDate').val(Date.ofDate(data.purNetValueDate).format('yyyy-MM-dd'));
                                $('#redUnitNetValue').val(data.purUnitNetValue);
                                if("Y"==isCycle){
                                	$('#redNetValueDate').val("");
                                    $('#redUnitNetValue').val("");
                                	redCycleDataInit();
                                }
    	                	}else if("C5"==dealType || "C20"==dealType){
    	                		$('#expireNetValueDate').removeAttr("disabled");
    	                		$('#expireNetValueDate').val(Date.ofDate(data.purNetValueDate).format('yyyy-MM-dd'));
    	                        $('#expireUnitNetValue').val(data.purUnitNetValue);
    	                        $('#expireDealNum').val(data.purDealNum);
    	                        $('#expireDealAmt').val(JoyinMath.doMul(data.purDealNum,data.purUnitNetValue,2)).trigger("change");
    	                        if("Y"==isCycle){
    	                        	$('#expireNetValueDate').val("");
        	                        $('#expireUnitNetValue').val("");
        	                        $('#expireNetValueDate').attr("disabled","disabled");
    	                        	expireCycleDataInit();
                                }
    	                	}
                    	}else{
                    		if("C2"==dealType){
                    			$('#purUnitNetValue').val(data.purUnitNetValue);
                        	}else if("C3"==dealType || "C6"==dealType){
                                $('#addReduceUnitNetValue').val(data.purUnitNetValue);
    	                	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
                        		$('#redUnitNetValue').val(data.purUnitNetValue);
                        	}else if("C5"==dealType || "C20"==dealType){
                        		$('#expireUnitNetValue').val(data.purUnitNetValue);
                        		$('#expireDealNum').val(data.purDealNum);
                        		$('#expireDealAmt').val(JoyinMath.doMul(data.purDealNum,data.purUnitNetValue,2)).trigger("change");
                        	}
                    	}
                	}else if("03"==profitType){
                		if("C4"==dealType || "C17"==dealType || "C18"==dealType){
                			$('#redPrinAmt03').val(data.purDealNum);
                			$('#redProfit03').val(data.purDealAmt);
                			$('#totalRedPrinAmt03').val(data.purDealNum);
                			$('#totalRedProfit03').val(data.purDealAmt);
                			$('#redDealAmt03').val(JoyinMath.doAdd(data.purDealNum,data.purDealAmt));
                			$('#redTotalNetValue03').val($('#redDealAmt03').val());
                    	}else if("C5"==dealType || "C20"==dealType){
                    		$('#expirePrinAmt03').val(data.purDealNum);
                			$('#expireProfit03').val(data.purDealAmt);
                			$('#totalRedPrinAmt03').val(data.purDealNum);
                			$('#totalRedProfit03').val(data.purDealAmt);
                			$('#expireDealAmt03').val(JoyinMath.doAdd(data.purDealNum,data.purDealAmt)).trigger("change");
                    	}
                	}

                }else{
                	if("01"==profitType){
                		if(1==type){
                    		if("C2"==dealType){
                    			$('#purNetValueDate').val(null);
    	                    	$('#purUnitNetValue').val(null);
    	                	}else if("C3"==dealType || "C6"==dealType){
    	                		$('#addReduceNetValueDate').val(null);
                                $('#addReduceUnitNetValue').val(null);
    	                	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
    	                		$('#redNetValueDate').val(null);
    	                		$('#redUnitNetValue').val(null);
    	                	}else if("C5"==dealType || "C20"==dealType){
    	                		$('#expireNetValueDate').val(null);
    	                    	$('#expireUnitNetValue').val(null);
    	                	}
                    	}else{
    	                	if("C2"==dealType){
    	                		$('#purUnitNetValue').val(null);
    	                	}else if("C3"==dealType || "C6"==dealType){
                                $('#addReduceUnitNetValue').val(null);
                                if("Y"==isCycle && $("#addReduceNetValueDate")!=""){
                                	addReduceCycleDataInit();
                                }
    	                	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
    	                		$('#redUnitNetValue').val(null);
    	                		 if("Y"==isCycle && $("#redNetValueDate")!=""){
                                 	redCycleDataInit();
                                 }
    	                	}else if("C5"==dealType || "C20"==dealType){
    	                		$('#expireUnitNetValue').val(null);
    	                		if("Y"==isCycle && $("#expireNetValueDate")!=""){
    	                			expireCycleDataInit();
                                 }
    	                	}
                    	}
                	}else if("03"==profitType){
                		if("C4"==dealType || "C17"==dealType || "C18"==dealType){
                			$('#redPrinAmt03').val(null);
                			$('#redProfit03').val(null);
                			$('#totalRedPrinAmt03').val(null);
                			$('#totalRedProfit03').val(null);
                			$('#redDealAmt03').val(null);
                			$('#redTotalNetValue03').val(null);
                    	}else if("C5"==dealType || "C20"==dealType){
                    		$('#expirePrinAmt03').val(null);
                			$('#expireProfit03').val(null);
                			$('#totalRedPrinAmt03').val(null);
                			$('#totalRedProfit03').val(null);
                			$('#expireDealAmt03').val(null);
                    	}
                	}

                }
               /* if("Y"==isCycle){
                	addReduceCycleDataInit();
                }*/
            })
        }
    };
    /**
     * 获取净值信息：净值日、净值
     * type:
     * 1、根据确认日，查询最近的净值日净值信息 ；
     * 2、根据净值日，获取净值日的净值信息
     */
    var getLayerNetValueInfo = function (dataBef) {
    	var netValueDate = null;
    	var dealDate = null;
        var finprodId = dataBef.finprodId;
    	var dealType = $("#dealType").val();
    	var profitType = $("#profitType").val();

    	if("01"==profitType){
    		if("C2"==dealType){
    			dealDate = dataBef.purNetValueLayerDate;
        	}else if("C3"==dealType || "C6"==dealType){
        		dealDate = dataBef.addReduceNetValueLayerDate;
        	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
        		dealDate = dataBef.redNetValueLayerDate;
        	}else if("C5"==dealType || "C20"==dealType){
        		dealDate = dataBef.expireNetValueLayerDate;
        	}
    	}
        var object = {'netValueDate':netValueDate,'dealDate':dealDate,'finprodId':finprodId};
        //获取净值参考日
        Joyin.ajaxSync(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/getNetValueInfo', object, function (data) {
            if (data) {
            	if("01"==profitType){
            		if("C2"==dealType){
            			dataBef.purUnitNetLayerValue = data.purUnitNetValue;
                	}else if("C3"==dealType || "C6"==dealType){
                		dataBef.addReduceUnitNetLayerValue = data.purUnitNetValue;
                	}else if("C4"==dealType || "C17"==dealType ||"C18"==dealType){
                		dataBef.redUnitNetLayerValue = data.purUnitNetValue;
                	}else if("C5"==dealType || "C20"==dealType){
                		dataBef.expireUnitNetLayerValue = data.purUnitNetValue;
                		dataBef.expireDealLayerNum = data.purDealNum;
                		dataBef.expireDealLayerAmt = JoyinMath.doMul(data.purDealNum,data.purUnitNetValue,2);
                	}
            	}
            }
        })
    };
    /**
     * 获取当前时间最近工作日
     * add by 20191230
     */
    var getNearWorkDay = function (confirmDate) {

    	var dealType = $("#dealType").val();
    	var calendarId = $("#calendarId").val();
    	var profitType = $("#profitType").val();
        var object = {'mdate':confirmDate,'calendarId':calendarId};
        if(confirmDate){
            //获取下一工作日
            Joyin.ajaxJson(basePath + 'invest/product/productelement/netvalueproduct/FinNetValueProductBuild002/getNearWorkDay', object, function (data) {
                if (data) {
                	if("01"==profitType){
                		if("C4"==dealType || "C17"==dealType){
                			$('#redPayDate').val(Date.ofDate(data).format('yyyy-MM-dd'));
                		}
                	}
                }
            })
        }
    };
    /**
     * 获取传入日期的下一工作日
     * add by wm 2019-06-20
     */
    var getNextWorkDay = function (confirmDate) {

    	var dealType = $("#dealType").val();
    	var calendarId = $("#calendarId").val();
 //   	var isCycle = $("#isCycle").val();
    	var profitType = $("#profitType").val();
        var object = {'mdate':confirmDate,'calendarId':calendarId};
        if(confirmDate){
            //获取下一工作日
            Joyin.ajaxJson(basePath + 'invest/product/productelement/netvalueproduct/FinNetValueProductBuild002/getNextWorkDay', object, function (data) {
                if (data) {
                	if("01"==profitType){
                		if("C4"==dealType || "C17"==dealType){
                			$('#redPayDate').val(Date.ofDate(data).format('yyyy-MM-dd'));
                		}
                		else if("C5"==dealType || "C20"==dealType){
                    		$('#expirePayDate').val(Date.ofDate(data).format('yyyy-MM-dd')).trigger("change");
                		}
                	}else if("03"==profitType){
                		if("C5"==dealType || "C20"==dealType){
//                    		$('#expirePrinPayDate03').val(Date.ofDate(data).format('yyyy-MM-dd'));
                        	$('#expireProfitPayDate03').val(Date.ofDate(data).format('yyyy-MM-dd'));

                        	//本金支付日默认为确认日
                        	$('#expirePrinPayDate03').val(Date.ofDate(confirmDate).format('yyyy-MM-dd'));
                    	}
                	}

                }
            })
        }
    };
    var addReduceDealAmtInit = function(){
    	var addReduceUnitNetValue = $('#addReduceUnitNetValue').val();
    	var addReduceDealNum = $('#addReduceDealNum').val();
    	//去除强增强减交易金额联动
    	/*if(addReduceDealNum && addReduceUnitNetValue){
    		$('#addReduceDealAmt').val(JoyinMath.doMul(addReduceUnitNetValue,addReduceDealNum,2));
    	}*/

    };

    var calExpireDealAmt03 = function(){
    	var expirePrinAmt = $('#expirePrinAmt03').val();
    	var expireProfit = $('#expireProfit03').val();
    	if(!expirePrinAmt){
    		expirePrinAmt = 0;
    	}
    	if(!expireProfit){
    		expireProfit = 0;
    	}
    	$('#expireDealAmt03').val(JoyinMath.doAdd(Number(expirePrinAmt),Number(expireProfit))).trigger("change");
    };

    var calRedDealAmt03 = function(){
    	var redPrinAmt = $('#redPrinAmt03').val();
    	var redProfit = $('#redProfit03').val();
    	if(!redPrinAmt){
    		redPrinAmt = 0;
    	}
    	if(!redProfit){
    		redProfit = 0;
    	}
    	if($('#profitType').val()=="03" && ("C4"==$('#dealType').val()||"C17"==$('#dealType').val())){
    		$('#redDealNum').val(redPrinAmt);
    	}
    	$('#redDealAmt03').val(JoyinMath.doAdd(Number(redPrinAmt),Number(redProfit)));
		calRedTotalNetValue03();
    };

    var calRedTotalNetValue03 = function(){
    	var redFee = $('#redFeeLc03').val();
    	var redDealAmt = $('#redDealAmt03').val();
    	if(redDealAmt){
    		if(!redFee){
    			redFee = 0;
    		}
    		$('#redTotalNetValue03').val(JoyinMath.doMinus(Number(redDealAmt),Number(redFee)));
    		reloadSettle();
    	}
    };



    //判断是提交或是保存
    function saveOrSubmit(operateType) {
    	//TISHKBS-1443 add by yujiaping 2020-11-19 开放式产品添加限制产品不能在到期日之后做申赎的存续期 start
        var mDate = $('#mDate').val();
		var confirmDate = $('#purConfirmDate').val();
		if(!confirmDate){confirmDate = $('#purConfirmDate03').val();}
        if(!confirmDate){confirmDate = $('#redConfirmDate').val();}
        if(!confirmDate){confirmDate = $('#redConfirmDate03').val();}
		if(mDate && confirmDate && confirmDate > mDate){
			Joyin.error("确认日不能大于产品到期日!");
			return;
		}
		//TISHKBS-1443 add by yujiaping 2020-11-19 开放式产品添加限制产品不能在到期日之后做申赎的存续期 end
        var object = $.serializeToObject('#submitForm', true);
        object.pageModel = pageModel;
        object.operateType = operateType;
        var dealType = object.dealType;
        //产品收益类型
    	var profitType = object.profitType;
    	if('01' == profitType ){
    		if('C1' == dealType ){
            	object.finNetProductDurationLayerList = layAmtNetTable.getAllRows();
            }
            else if('C2' == dealType ){
            	object.finNetProductDurationLayerList = layAmtPurNetTable.getAllRows();
            }
            else if('C4' == dealType || 'C17' == dealType){
            	if($('#isCycle').val() == "Y"){
            		//新建
        	        if($("#pageModel").val() == PAGE_MODEL_NEW){
        	        	object.redCycleFinNetProductDurationDtoList = redCycleDataTable.getAllCheckedRows();
        	        }else{
        	        	object.redCycleFinNetProductDurationDtoList = redCycleDataTable.getAllRows();
        	        }
            	}
            	object.finNetProductDurationLayerList = layAmtRedNetTable.getAllRows();
            }
            else if('C5' == dealType || 'C20' == dealType){
            	if($('#isCycle').val() == "Y"){
            		//新建
        	        if($("#pageModel").val() == PAGE_MODEL_NEW){
        	        	object.expireCycleFinNetProductDurationDtoList = expireCycleDataTable.getAllCheckedRows();
            		}else{
            			object.expireCycleFinNetProductDurationDtoList = expireCycleDataTable.getAllRows();
            		}
            	}
            	object.finNetProductDurationLayerList = layAmtExpireNetTable.getAllRows();
            }
            else if('C3' == dealType || 'C6' == dealType){
            	if($('#isCycle').val() == "Y"){
            		//新建
        	        if($("#pageModel").val() == PAGE_MODEL_NEW){
        	        	object.addReduceCycleFinNetProductDurationDtoList = addReduceCycleDataTable.getAllCheckedRows();
        	        }else{
        	        	object.addReduceCycleFinNetProductDurationDtoList = addReduceCycleDataTable.getAllRows();
        	        }
            	}
            	object.finNetProductDurationLayerList = layAmtReduceNetTable.getAllRows();
            	//当强增强减交易金额为空时，指令值为空
            	if(!object.addReduceDealAmt){
                	object.bussTrdTransferOrder = null;
    			}
            }
    	}
    	else{
    		if('C4' == dealType || 'C17' == dealType){
    			var redPrinAmt = $('#redPrinAmt03').val();
    			object.redDealNum = redPrinAmt;
    		}
    		else if(('C3' == dealType || 'C6' == dealType)){
    			if(!object.addReduceDealAmt03){
                	object.bussTrdTransferOrder = null;
    			}
            }
    		else if('C10' == dealType){
            	object.bussTrdTransferOrder = null;
            }
    	}
        if(operateType === 'revoke'){
            Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                    if(operateType === 'save') {
                        WorkFlow.doSaveList(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/save', object, "FinNetProductDuration_btn_save",null,
                        		function(){window.top.layui.tab.tabDelete('PRODUCT_SIDE_001_003_008','FinNetProductDuration');});

                    }else if(operateType === 'submit') {
                        WorkFlow.doSubmitList(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/save', object, "FinNetProductDuration_btn_submit",
                        		null,
                        		function(){
                        	window.top.layui.tab.tabDelete('PRODUCT_SIDE_001_003_008','FinNetProductDuration');
                        			}
                        );

                    }else if(operateType === 'revoke') {
                    	//update by gcl 2019/4/8 start
                    	WorkFlow.doRevokeList(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/doRevoke', object, "FinNetProductDuration_btn_revoke");
                    	//update by gcl 2019/4/8 end
                    }
                });
            });
        }else{
        Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/check', object, function(result) {
        	Joyin.ajaxJson(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/checkDuraAmt', object, function(result) {
        	//if(result1 != null){
        		//Joyin.confirm(result1.data, function(){
            Joyin.confirm(result.data, function() {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/save', object, "FinNetProductDuration_btn_save",null,
                    		function(){window.top.layui.tab.tabDelete('PRODUCT_SIDE_001_003_008','FinNetProductDuration');});

                }else if(operateType === 'submit') {
                    WorkFlow.doSubmitList(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/save', object, "FinNetProductDuration_btn_submit",
                    		null,
                    		function(){
                    	window.top.layui.tab.tabDelete('PRODUCT_SIDE_001_003_008','FinNetProductDuration');
                    			}
                    );

                }else if(operateType === 'revoke') {
                	WorkFlow.doRevokeList(basePath + 'invest/product/productduration/netproductDuration/FinNetProductDuration/doRevoke', object, "FinNetProductDuration_btn_revoke");
                }
            });
        		// });
        	//}
           });
        });

       		 //return;



        }
    };

    var init = function(){
    	dealTypeChange();
    	var operateType = $('#operateType').val();
    	if("revoke"==operateType || "delete"==operateType){
    		$('#FinNetProductDuration_btn_save').hide();
    		$('#FinNetProductDuration_btn_submit').hide();
    		$('#FinNetProductDuration_btn_revoke').show();
    		$('#FinNetProductDuration_btn_revoke').attr('disabled',false)

    		//layui-btn-disabled
    	}else{
    		$('#FinNetProductDuration_btn_revoke').hide();
    	}
    };

    /**
     * 画面加载完成
     */
    $(function() {

    	$('#namediv').hide();
    	init();
    	// 绑定按钮事件
        $('#FinNetProductDuration_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#FinNetProductDuration_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

        $('#FinNetProductDuration_btn_revoke').click(function() {
            saveOrSubmit('revoke');
        });

        $('#FinNetProductDuration_btn_cancel').click(function() {
			Joyin.confirm("是否关闭？", function() {
				window.top.layui.tab.tabDelete('PRODUCT_SIDE_001_003_008','FinNetProductDuration');
			});
		});

    	//产品代码联动
        $('#finprodMarketId').change(function() {
            finprodMarketIdChange();
        });
        //交易类型变动
        $('#dealType').change(function() {
        	dealTypeChange();
        });
        //认购期次代码联动
        $('#subPeriodId').change(function() {
        	periodIdChange();
        });
        //申购期次代码联动
        $('#purPeriodId').change(function() {
        	periodIdChange();
        });
        //认购投资周期联动
        $('#subTermDays').change(function() {
        	termDaysChange();
        });
        //申购投资周期联动
        $('#purTermDays').change(function() {
        	termDaysChange();
        });
        //认购交易金额联动,交易份额=交易金额
        $('#subDealAmt').change(function() {
        	$('#subDealNum').val(JoyinMath.doAdd(Number($('#subDealAmt').val()),Number(0))).trigger("change");
        	reloadSettle();
        });

        //认购确认日变动：如果周期型产品，已在确认日做过认购，则联动带出期次结束日和期次支付日
        $('#subConfirmDate').change(function() {
        	subConfirmDateChange();
        	reloadSettle();
        });

        //申购确认日变动：带出最近的净值日和净值
        $('#purConfirmDate').change(function() {
        	getNetValueInfo(1);
        	if($('#dealMode').val()=='11'){
        		if(layAmtPurNetTable.table){
        			layAmtPurNetTable.reload();
        		}
        		else{
        			layAmtPurNetTable.load();
        		}
        	}
        	reloadSettle();
        });
        //申购净值日变动：带出净值日对应的净值
        $('#purNetValueDate').change(function() {
        	//modify by wm 净值日修改为取净值日当前最近的净值，包括净值日当天 2019-07-03
//        	getNetValueInfo(2);
        	getNetValueInfo(3);
        });

        //申购交易金额联动,交易份额=交易金额/单位净值
        $('#purDealAmt').change(function() {
        	var purUnitNetValue = $('#purUnitNetValue').val();
        	var purDealAmt = $('#purDealAmt').val();
        	if(purUnitNetValue && purDealAmt){
        		var purDealNum = Number(JoyinMath.doDiv(purDealAmt,purUnitNetValue,2));
        		$('#purDealNum').val(purDealNum);
        	}
        	if(purUnitNetValue==0){
        		$('#purDealNum').val(0);
        	}
        	reloadSettle();
        });

        //赎回确认日变动：带出最近的净值日和净值
        $('#redConfirmDate').change(function() {
        	var confirmDate = $('#redConfirmDate').val();
        	//modify by wm 净值型产品赎回支付日修改为下一工作日 start 2019-06-20
        	var payDate = Date.ofDate(confirmDate).format('yyyy-MM-dd');
        	//$('#redPayDate').val(payDate);
        	getNearWorkDay(confirmDate);
        	//modify by wm 净值型产品赎回支付日修改为下一工作日 end 2019-06-20
        	getNetValueInfo(1);
        	if($('#dealMode').val()=='11'){
        		if(layAmtRedNetTable.table){
        			layAmtRedNetTable.reload();
        		}
        		else{
        			layAmtRedNetTable.load();
        		}
        	}
        });

        //赎回净值日变动：带出净值日对应的净值
        $('#redNetValueDate').change(function() {
        	//modify by wm 净值日修改为取净值日当前最近的净值，包括净值日当天 2019-07-03
//        	getNetValueInfo(2);
        	getNetValueInfo(3);
        });
        //赎回份额联动
        $('#redDealNum').change(function() {
        	redDealAmtInit();

        });
        $('#redUnitNetValue').change(function() {
        	redDealAmtInit();
        });

        $('#redDealAmt').change(function() {
        	redTotalNetValueInit();
        	reloadSettle();
        });

        $('#redFeeLc').change(function() {
        	redTotalNetValueInit();
        	reloadSettle();
        });

        //到期确认日变动：带出最近的净值日和净值
        $('#expireConfirmDate').change(function() {
        	getNetValueInfo(1);
        	if($('#dealMode').val()=='11'){
        		if(layAmtExpireNetTable.table){
                	layAmtExpireNetTable.reload();
        		}
        		else{
                	layAmtExpireNetTable.load();
        		}
        	}
        });

        //到期净值日变动：带出净值日对应的净值
        $('#expireNetValueDate').change(function() {
        	//modify by wm 净值日修改为取净值日当前最近的净值，包括净值日当天 2019-07-03
//        	getNetValueInfo(2);
        	getNetValueInfo(3);
        });

        $('#expireDealAmt').change(function() {
        	reloadSettle();
        });

        $('#expirePayDate').change(function() {
        	reloadSettle();
        });

        //强增确认日变动：带出最近的净值日和净值
        $('#addReduceConfirmDate').change(function() {
        	getNetValueInfo(1);
        	if($('#dealMode').val()=='11'){
        		if(layAmtReduceNetTable.table){
        			layAmtReduceNetTable.reload();
        		}
        		else{
        			layAmtReduceNetTable.load();
        		}
        	}
        });

        //强增净值日变动：带出净值日对应的净值
        $('#addReduceNetValueDate').change(function() {
        	//modify by wm 净值日修改为取净值日当前最近的净值，包括净值日当天 2019-07-03
//        	getNetValueInfo(2);
        	getNetValueInfo(3);
        });



        $('#addReduceDealNum').change(function() {
        	addReduceDealAmtInit();
        	reloadSettle();
        });

        $('#addReduceUnitNetValue').change(function() {
        	addReduceDealAmtInit();
        	reloadSettle();
        });
        $('#addReduceDealAmt').change(function() {
        	var addReduceDealAmt = $('#addReduceDealAmt').val();
        	if(addReduceDealAmt){
            	reloadSettle();
        	}else{
            	$(".hkxx").find(".layui-table-view .layui-table-box .layui-table-body table tbody tr td").html("");
        	}
        });



        $('#subDealAmt03').change(function() {
        	reloadSettle();
        });
        //强增强减金额改变事件
        $('#addReduceDealAmt03').change(function() {
        	var addReduceDealAmt03 = $('#addReduceDealAmt03').val();
//        	$('#addReduceDealNum03').val(addReduceDealAmt03);
        	if(addReduceDealAmt03){
            	reloadSettle();
        	}else{
            	$(".hkxx").find(".layui-table-view .layui-table-box .layui-table-body table tbody tr td").html("");
        	}
        });

        $('#purDealAmt03').change(function() {
        	reloadSettle();
        });

        $('#redConfirmDate03').change(function() {
        	var confirmDate = $('#redConfirmDate03').val();
        	//var payDate = Date.ofDate(confirmDate).DateAdd('d', 1).format('yyyy-MM-dd');
//        	$('#redPrinPayDate03').val(payDate);
        	//本金支付日默认为确认日
        	$('#redPrinPayDate03').val(Date.ofDate(confirmDate).format('yyyy-MM-dd'));
        	//modify by wm 收益支付日与本金支付已相同，既为确认日 start 2019-07-05
        	//$('#redProfitPayDate03').val(payDate);
        	$('#redProfitPayDate03').val(Date.ofDate(confirmDate).format('yyyy-MM-dd'));
        	//modify by wm 收益支付日与本金支付已相同，既为确认日 end 2019-07-05
        	//delete by wm 货币型产品赎回无需展示 start 2019-07-05
        	//查询交易本金和收益金额
        	//getNetValueInfo(2);
        	//delete by wm 货币型产品赎回无需展示 end 2019-07-05
        });
        //货币型产品本金支付日联动修改收益支付日
        $('#redPrinPayDate03').change(function() {
        	var payDate = $('#redPrinPayDate03').val();
        	$('#redProfitPayDate03').val(Date.ofDate(payDate).format('yyyy-MM-dd'));
        });
		$('#redPrinAmt03').change(function() {
/*			var totalRedPrinAmt03 = $('#totalRedPrinAmt03').val();
			var totalRedProfit03 = $('#totalRedProfit03').val();
			var redPrinAmt03 = $('#redPrinAmt03').val();*/
			// 计算理论收益
			calRedProfit03();

	/*		if(totalRedPrinAmt03 && totalRedProfit03 && redPrinAmt03){
				$('#redProfit03').val(JoyinMath.doMul(redPrinAmt03,JoyinMath.doDiv(totalRedProfit03,totalRedPrinAmt03),2));
			}	*/
			calRedDealAmt03();

		});
		$('#redProfit03').change(function() {
			calRedDealAmt03();
		});

		$('#expirePrinAmt03').change(function() {
			calExpireDealAmt03();

		});
		$('#expireProfit03').change(function() {
			calExpireDealAmt03();
		});

        $('#redFeeLc03').change(function() {
        	calRedTotalNetValue03();
        	reloadSettle();
        });

        $('#expireConfirmDate03').change(function() {
        	var confirmDate = $('#expireConfirmDate03').val();
        	//modify by wm 货币型产品到期/提前终止本金支付日和收益支付日修改为下一工作日 start 2019-06-20
        	var payDate = Date.ofDate(confirmDate).format('yyyy-MM-dd');
        	$('#expirePrinPayDate03').val(payDate);
        	$('#expireProfitPayDate03').val(payDate);
//        	getNextWorkDay(confirmDate);
        	//modify by wm 货币型产品到期/提前终止本金支付日和收益支付日修改为下一工作日 end 2019-06-20
        	//查询交易本金和收益金额
        	getNetValueInfo(2);
        });

        $('#expireDealAmt03').change(function() {
        	reloadSettle();
        });

        $('#bonusCashEndDate03').change(function() {
        	var bonusCashEndDate03 = $('#bonusCashEndDate03').val();
        	var d =Date.ofDate(bonusCashEndDate03).format('yyyy-MM-dd');
        	$('#bonusCashBonusDate03').val(d);
        });

        $('#bonusShareEndDate03').change(function() {
        	var bonusShareEndDate03 = $('#bonusShareEndDate03').val();
        	var d = Date.ofDate(bonusShareEndDate03).format('yyyy-MM-dd');
        	$('#bonusShareConfirmDate03').val(d);
        });
        $('#bonusShareAmt03').change(function() {
        	var bonusShareAmt03 = $('#bonusShareAmt03').val();
        	var bonusShareAfterShareNumBf = $('#bonusShareAfterShareNumBf').val();
        	var bonusShareAfterShareNum03 = JoyinMath.doAdd(bonusShareAmt03, bonusShareAfterShareNumBf);
        	$('#bonusShareAfterShareNum03').val(bonusShareAfterShareNum03);
        });

        // add by wm 增加现金分红的分红金额联动刷新指令信息
        $('#bonusCashBonusAmt03').change(function() {
        	reloadSettle();
        });
        $('#redNetValueDate').change(function() {
        	reloadSettle();
        });
        //认购期次结束日修改联动
        $('#subCycleEndDate').change(function() {
        	cycleEndDateChange();
        });
        //申购期次结束日修改联动
        $('#purCycleEndDate').change(function() {
        	cycleEndDateChange();
        });
        //申购确认日修改联动    期次代码修改
        $('#purConfirmDate').change(function() {
        	purConfirmDateChange();
        });
        //申购确认日修改联动   重新生成指令
        $('#purConfirmDate03').change(function() {
        	$('#bussTrdTransferOrder_btn_generate').click();
        });
        //净值日修改联动
        /*$('#addReduceNetValueDate').change(function() {
        	alert(6);
        	addReduceCycleDataInit();
        });*/
         if( remType =='R1S3' || remType =='R1S5' || remType == 'R1S2' ||remType=='R1S4'){
                finprodMarketIdChange();
         };
        //20191205 bug修复，红利再投撤销审批页面多撤销按钮
        if(workflowId != null && workflowId != ''){
        	$("#FinNetProductDuration_btn_revoke").hide();
        }
		if($("#pageModel").val()=='1'){
			if($('#isCycle').val() == "Y"){
			expireCycleDataTableCheck();
              }
		}
		//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 start
		if($("#dealType").val()=="C5" || $("#dealType").val()=="C20"){
			if($('#isCycle').val() == "Y"){
				$('#expireNetValueDate').attr("disabled",true);
	              }
		}
		//TISHKBS-2081 CLONE - 周期型产品到期、提前终止流程中修改 净值日没有置灰 end
    });
}());
