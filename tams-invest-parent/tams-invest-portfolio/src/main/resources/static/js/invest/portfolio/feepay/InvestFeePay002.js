/**
 * 费用明细
 */
function showFeeDetail(secAcctId, ptlId, calcStartDate, calcEndDate, feeType, confirmFlag){
	if(!ptlId || ptlId =='undefined'){
		ptlId = '';
	}
	Joyin.dialog(JoyinUtil.getLabel("InvestFeePay003"), JoyinUtil.getRealPath('invest/portfolio/feepay/InvestFeePay003/init'),
		"60%", "70%", {pageModel:PAGE_MODEL_REFERENCE, secAcctId : secAcctId, ptlId : ptlId, 
					   calcStartDate : calcStartDate, calcEndDate : calcEndDate, feeType: feeType, confirmFlag: confirmFlag, tradeId :$('#InvestFeePay002_hidden_tradeId').val(),org:$('#InvestFeePay002_regTrustOrg').val()})
}
/**
 * 画星星
 */
var star = function() {
	// 加红星星
	// 查找label
	var _findPrevLabel = function(element, level) {
		var $el = element.prev();
		if (!$el.is('label') && level > 0) {
			$el = $el.find('label');
			if ($el.length === 0) {
				return _findPrevLabel(element.parent(), level - 1);
			}
		}
		return $el;
	};

	// 非查看模式
	if (typeof pageModel !== 'undefined'
		&& (pageModel === PAGE_MODEL_EDIT || pageModel === PAGE_MODEL_NEW)) {
		$('.must').each(function() {
			$(this).remove();
		})
		$('*[required]').each(function() {
			var $el = $(this);
			var $target = _findPrevLabel($el, 3);
			if ($target.find('> .must').length === 0) {
				$target.append($('<i class="must">*</i>'));
			}
		});
	}
}
// 定义函数作用域(立即执行),防止全局变量污染
$(function() {
	var calculateFlag = false;//是否是按计算按钮，刷新组合信息（默认为否）
	var flag = false;//是否是修改列表列字段显示和隐藏，刷新组合信息（默认为否）
	var isNew = false;//是否是新建（没有预提规则的账户维护费），默认为不是
	var calcConditionObjBackUp;
	var cols1 = [{field : 'portFolioName',width:230,title : 'InvestFeePay002.feeTable.portFolioName'
		}, {field: 'ptlId',width:230, title : 'InvestFeePay002.feeTable.portFolioName',visible: false,edit:true,sortable:false,editOption:{type:'select',id:'booksetId',showField:'text',formatoption:true,
			url: JoyinUtil.getRealPath('/invest/portfolio/feepay/InvestFeePay002/getPortFolioName'),postData: function(){
				return {
					payDate: $("#InvestFeePay002_payDate").val(),
					secAcctId: $("#InvestFeePay002_secAcctId").val()
				}}}
		}, {field : 'payAmt',width:200,title : 'InvestFeePay002.feeTable.payAmt',type : 'number',textrange : '2',sortable:false
		}, {field : 'actPayAmt',width:200,title : 'InvestFeePay002.feeTable.actPayAmt',type : 'number',textrange : '2',edit:true,sortable:false
		}, {field : 'adjustAmt',width:120,title : 'InvestFeePay002.feeTable.adjustAmt',type : 'number',textrange : '2',sortable:false
		}, {field : 'status',width:100,title : 'InvestFeePay002.feeTable.showDetail',sortable:false,
			render : function(data, type, row, meta) {
					var secAcctId = row.secAcctId;
					if (!secAcctId || secAcctId == 'undefined') {
						secAcctId = '';
					}
					return "<a onclick=\"showFeeDetail('" + secAcctId +"','"+ row.ptlId +"','" + $('#InvestFeePay002_positionStartDate').val() +"','" + $('#InvestFeePay002_positionEndDate').val() +"','" + $('#InvestFeePay002_feeType').val()+"','" + $('#InvestFeePay002_hidden_operateType').val()+"')\" + style='cursor: pointer;'>"+JoyinUtil.getLabel("InvestFeePay002.feeTable.showDetail")+"</a>";
				}
		}, {field : 'secAcctId',visible : false
		}, {field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt'], visible: false
	}];

	/**
	 * 付费明细
	 */
	var feeInfoTable = $('#fee_dataTable').joyintable({
		ajax : {
			url : JoyinUtil.getRealPath('/invest/portfolio/feepay/InvestFeePay002/queryPayFeeInfoList'),
			type : 'POST',
			data : function() {
				return {
						workflowId : workflowId,
						secAcctId : $('#InvestFeePay002_secAcctId').val(),
						tradeId : $('#InvestFeePay002_hidden_tradeId').val(),
						calcStartDate : $('#InvestFeePay002_positionStartDate').val(),
						calcEndDate : $('#InvestFeePay002_positionEndDate').val(),
						payDate : $("#InvestFeePay002_payDate").val(),
						feeType : $('#InvestFeePay002_feeType').val(),
						operateType : $('#InvestFeePay002_hidden_operateType').val(),
						cashId:$('#InvestFeePay002_hidden_cashId').val(),
						chargeType : $('#InvestFeePay002_hidden_chargeType').val(),
						org : $('#InvestFeePay002_regTrustOrg').val(),
						confirmFlag : $('#InvestFeePay002_hidden_operateType').val(),
						flag : flag,
						calculateFlag : calculateFlag
						};
			}
		},
		loadonce : true,
		// searchOnInit: !($('#InvestFeePay002_hidden_feeType').val() === ''),
		searchOnInit: true,
		addbtn: true,
        height: 300,
        page: false,
        checkbox: false,
        rowno: true,
		cols : cols1,
        editCell : function(value, data, field, cell, column){
        	if (field === "actPayAmt"){
        		if (typeof value == "undefined" || value == null || value == "") {
        			data.adjustAmt = JoyinMath.doMinus(data.payAmt, data.actPayAmt, 2, JoyinMath.ROUND_HALF_UP);
        		} else {
        			data.adjustAmt = JoyinMath.doMinus(data.payAmt,value, 2, JoyinMath.ROUND_HALF_UP);
        		}
            	feeInfoTable.renderRow(data.LAY_TABLE_INDEX, data);
            		
        		var totalAmt = '0';
        		var allData = feeInfoTable.getAllRows();
            	if (allData && allData.length > 0){
            		$.each(allData, function(id, value){
    	        		totalAmt = JoyinMath.doAdd(totalAmt, value.actPayAmt, 2, JoyinMath.ROUND_HALF_UP);
    	        	});
            	}
            	$('#InvestFeePay002_hidden_tradeAmt').val(totalAmt);
            	//为一次性费用实付金额赋值
            	$('#InvestFeePay002_actPayAmt').val(totalAmt).trigger('change');
        	}
        },
		beforeEditCell : function(preVal, field, rowData) {
			if(field == 'portFolioName'){
				if(!isNew || $("#InvestFeePay002_feeType").val() != '08')return false;
			}
			return true;
		},
        done : function(allData, table){
			if(isNew && $("#InvestFeePay002_feeType").val() == '08')return;
        	var totalAmt = '0';
        	var actPayAmtCurSum = '0';
        	if (allData && allData.length > 0 && !isTodoList && $('#InvestFeePay002_hidden_operateType').val() === 'new'){
        		// 当前理论支付金额
        		var payAmtCur = $('#InvestFeePay002_payAmt').val();
        		// 当前理论实际支付金额
        		var actPayAmtCur = $('#InvestFeePay002_actPayAmt').val();
        		$.each(allData, function(id, value){
        			// 如果理论支付金额不等于理论实际支付金额，按比例计算实际支付金额，尾差计入最后一条数据
        			if (!(payAmtCur === actPayAmtCur)) {
        				var indexCur = value.LAY_TABLE_INDEX + 1;
        				if (indexCur === allData.length) {
        					value.actPayAmt = JoyinMath.doMinus(actPayAmtCur, actPayAmtCurSum, 2, JoyinMath.ROUND_HALF_UP);
        				} else {
        					value.actPayAmt = JoyinMath.doMul(actPayAmtCur,JoyinMath.doDiv(value.payAmt,payAmtCur,2, JoyinMath.ROUND_HALF_UP));
        					actPayAmtCurSum = JoyinMath.doAdd(actPayAmtCurSum,value.actPayAmt);
        				}
        			}
	        		totalAmt = JoyinMath.doAdd(totalAmt, value.actPayAmt, 2, JoyinMath.ROUND_HALF_UP);
    				var adjustAmt = JoyinMath.doMinus(value.payAmt,value.actPayAmt, 2, JoyinMath.ROUND_HALF_UP);
    				value.adjustAmt = adjustAmt;
    				feeInfoTable.renderRow(value.LAY_TABLE_INDEX, value);
        		});
        	}
        	$('#InvestFeePay002_hidden_tradeAmt').val(totalAmt);
        }
	});

	feeInfoTable.bind("click",".delt",function (data,index){
		feeInfoTable.removeRow(data);
		var totalAmt = '0';
		var allData = feeInfoTable.getAllRows();
		if (allData && allData.length > 0){
			$.each(allData, function(id, value){
				totalAmt = JoyinMath.doAdd(totalAmt, value.actPayAmt, 2, JoyinMath.ROUND_HALF_UP);
			});
		}
		$('#InvestFeePay002_hidden_tradeAmt').val(totalAmt);
		$('#InvestFeePay002_actPayAmt').val(totalAmt).trigger('change');
	})

	function feeTypeChangeAction(){
		var feeType = $('#InvestFeePay002_feeType').val();
		var feeName = $('#InvestFeePay002_feeType option:selected').text();
		if (feeType){
			$('#InvestFeePay002_hidden_feeType').val(feeType);
			$('#InvestFeePay002_hidden_feeName').val(feeName);
			//费用类型联动登记托管机构
			$('#InvestFeePay002_regTrustOrg').attr("required",true);
			if (feeType == '06'){
				$('#InvestFeePay002_regTrustOrg').val('').trigger('change');
				$("#InvestFeePay002_regTrustOrg").prop("disabled", true);
				$('#InvestFeePay002_regTrustOrg').attr("required",false);
			} else if (feeType == '07'||feeType == '08'){
				$("#InvestFeePay002_regTrustOrg").prop("disabled", false);
			}else{
				$('#InvestFeePay002_regTrustOrg').val('03').trigger('change');
				$("#InvestFeePay002_regTrustOrg").prop("disabled", true);
			}
			//费用类型联动证券账户   update by zoujiamin  2021/02/26  ZGBSS-639/ZGBSS-636
			if(feeType == '06'||feeType == '07'){
				$('#InvestFeePay002_secAcctId').val('').trigger('change');
				$("#InvestFeePay002_secAcctId").prop("disabled", true);
			}else{
			    $("#InvestFeePay002_secAcctId").prop("disabled", false);
			}
			//计算计费开始日
			if ($('#InvestFeePay002_hidden_operateType').val() === 'new'){
				secAcctIdFeeTypeChangeAction();
			}
		}else{
			$('#InvestFeePay002_hidden_feeType').val('');
		}
		payTradeAmtChangeAction();
		star();
	};
	/**
	 * 结算服务费联动带出默认费用（add by wm 增加交易手续费的查询）
	 */
	function payTradeAmtChangeAction(){
		var org = $('#InvestFeePay002_regTrustOrg').val();
		var secAcctId = $('#InvestFeePay002_secAcctId').val();
		var feeType = $('#InvestFeePay002_hidden_feeType').val();
		var cdate = $('#InvestFeePay002_positionEndDate').val();
		var startDate = $('#InvestFeePay002_positionStartDate').val();
		var confirmFlag = $('#InvestFeePay002_hidden_operateType').val()
		if((feeType=='06'|| (feeType=='07' && org)||(feeType=='08'||feeType=='14'||feeType=='16'||feeType=='17')&&secAcctId)&&startDate&&cdate){
			Joyin.ajax(JoyinUtil.getRealPath('/invest/portfolio/feepay/InvestFeePay002/queryPayTradeAmt'), {secAcctId : secAcctId, feeType : feeType, org : org, cdate : cdate, startDate : startDate, confirmFlag: confirmFlag}, function(result){
			   $('#InvestFeePay002_payAmt').val(result);
			   $('#InvestFeePay002_actPayAmt').val(result).trigger('change');
			   //add by wm 将值赋值给生成指令的值 start 2019-09-05
			   $('#InvestFeePay002_hidden_tradeAmt').val(result);
			   //add by wm 将值赋值给生成指令的值 end 2019-09-05
			})
		}else{
			$('#InvestFeePay002_payAmt').val('');
			$('#InvestFeePay002_actPayAmt').val('').trigger('change');
			$('#InvestFeePay002_hidden_tradeAmt').val('');
		}
	}
	/**
	 * 证券账户和费用类型下拉框联动处理
	 */
	function secAcctIdFeeTypeChangeAction(){
		var feeType = $('#InvestFeePay002_feeType').val();
		var secAcctId = $('#InvestFeePay002_secAcctId').val();
		var org = $('#InvestFeePay002_regTrustOrg').val();
		if (feeType){
			if(feeType == '06'|| (feeType == '07' && org) || (feeType == '14'||feeType == '16'||feeType == '17')&&secAcctId){
				// 查询费用上次计提结束日
				Joyin.ajax(JoyinUtil.getRealPath('/invest/portfolio/feepay/InvestFeePay002/queryLastPayDate'),
					{secAcctId : secAcctId, feeType : feeType,org :org, operateType:$('#InvestFeePay002_hidden_operateType').val()}, function(result){
						$('#InvestFeePay002_positionStartDate').val(result).trigger('change').prop("disabled",true);
						$('#InvestFeePay002_positionStartDate').prop("disabled", true);
						if (!result || result == 'undefined') {
							$('#InvestFeePay002_positionStartDate').val('').prop("disabled", false).trigger('change');
						}
						//var endDate = $('#InvestFeePay002_positionEndDateHide').val();
						//$('#InvestFeePay002_positionEndDate').val(endDate).trigger('change');
					});
			} else if(feeType == '08'){
				// 账户维护费 获取计提开始日
				if (secAcctId) {
					Joyin.ajaxSync(JoyinUtil.getRealPath('/invest/portfolio/feepay/InvestFeePay002/queryStartDate'),
						{secAcctId: secAcctId}, function (result) {
							if (!result || result == 'undefined') {
								isNew = true;
							}else{
								isNew = false;
							}
							$('#InvestFeePay002_positionStartDate').val(result).trigger('change');
							//var endDate = $('#InvestFeePay002_positionEndDateHide').val();
							//$('#InvestFeePay002_positionEndDate').val(endDate).trigger('change');
							$('#InvestFeePay002_positionStartDate').prop("disabled", true);
						});
				}else{
					isNew = false;
				}
			}
			// 当费用类型为佣金或结算服务费时，自动带出登记托管机构
			if (secAcctId) {
//				if(feeType == '07' || feeType == '14' || feeType == '08'){
//					Joyin.ajax(JoyinUtil.getRealPath('/invest/portfolio/feepay/InvestFeePay002/querySecAcctOrg'), {secAcctId : secAcctId}, function(result){
//						$('#InvestFeePay002_regTrustOrg').val(result).trigger('change');
//					});
//				}
				// TISHKBS-894 【晋商五期】【组合管理-投资费用支付】佣金的理论费用金额、实付费用金额未带出，无法提交
                //  add by wj 20200810 start 费用类型为佣金时 登记托管机构为空
                if(feeType == '07' || feeType == '08'){
					Joyin.ajax(JoyinUtil.getRealPath('/invest/portfolio/feepay/InvestFeePay002/querySecAcctOrg'), {secAcctId : secAcctId}, function(result){
						$('#InvestFeePay002_regTrustOrg').val(result).trigger('change');
						$('#InvestFeePay002_regTrustOrg').prop("disabled", true);
					});
				}
			}
		} else {
			$('#InvestFeePay002_positionStartDate').val('').trigger('change');
		}
		flag = true;
		if(isNew && feeType == '08'){
			$('#InvestFeePay002_div_positionStartDate').hide();
			$('#InvestFeePay002_div_positionEndDate').hide();
			$('#InvestFeePay002_div_payAmt').hide();
			$('#InvestFeePay002_div_adjustAmt').hide();
			$('#InvestFeePay002_div_calculate').hide();
			feeInfoTable.clear();
			feeInfoTable.showColumns([{field:'operation', visible:true}]);
			feeInfoTable.showColumns([{field:'ptlId', visible:true}]);
			feeInfoTable.showColumns([{field:'portFolioName', visible:false}]);
			feeInfoTable.showColumns([{field:'payAmt', visible:false}]);
			feeInfoTable.showColumns([{field:'adjustAmt', visible:false}]);
			feeInfoTable.showColumns([{field:'status', visible:false}]) ;
		}else{
			$('#InvestFeePay002_div_positionStartDate').show();
			$('#InvestFeePay002_div_positionEndDate').show();
			$('#InvestFeePay002_div_payAmt').show();
			$('#InvestFeePay002_div_adjustAmt').show();
			$('#InvestFeePay002_div_calculate').show();
			feeInfoTable.clear();
			feeInfoTable.showColumns([{field:'operation', visible:false}]);
			feeInfoTable.showColumns([{field:'ptlId', visible:false}]);
			feeInfoTable.showColumns([{field:'portFolioName', visible:true}]);
			feeInfoTable.showColumns([{field:'payAmt', visible:true}]);
			feeInfoTable.showColumns([{field:'adjustAmt', visible:true}]);
			feeInfoTable.showColumns([{field:'status', visible:true}]);
			if(feeType == '08'){
				feeInfoTable.showColumns([{field:'status', visible:false}]);
			}else{
				feeInfoTable.showColumns([{field:'status', visible:true}]);
			}
		}
		flag = false;
	}
	
	/**
	 * 计提结束日联动处理
	 */
	function endDateChangeAction(){
		var endDate = $('#InvestFeePay002_positionEndDate').val();
		if (endDate){
			var startDate = $('#InvestFeePay002_positionStartDate').val();
			if (startDate && endDate <= startDate){
				// 计提结束日不能小于等于计提开始日
				Joyin.error(JoyinUtil.getMessage('FIN_PROD_E_0015',[JoyinUtil.getLabel("InvestFeePay002.positionEndDate"), JoyinUtil.getLabel("InvestFeePay002.positionStartDate")]));
				$('#InvestFeePay002_positionEndDate').val('').trigger('change');
			}else{
				var secAcctId = $('#InvestFeePay002_secAcctId').val();
				var feeType = $('#InvestFeePay002_hidden_feeType').val();
				if (feeType){
//					reloadTable();
					payTradeAmtChangeAction();
				}
			}
		}else{
			$('#InvestFeePay002_payAmt').val('').trigger('change');
			$('#InvestFeePay002_actPayAmt').val('').trigger('change');
		}
	}
	
	function reloadTable(){
		$('#fee_div').toggle(true);
		if(feeInfoTable.table){
			feeInfoTable.reload();
		}else{
			if ($('#InvestFeePay002_hidden_feeType').val()) {
				feeInfoTable.load();
			}
		}
	}
	
	/**
	 * 支付日联动处理
	 */
	function payDateChangeAction(){
		endDateChangeAction();
		payTradeAmtChangeAction();
	}
	
	//add by yujiaping 20210303 ZGBSS-654 费用类型选择【账户维护费】在没有待支付的数据时修改支付日清空组合信息 start	
	function feeInfoListAction(){
		var feeType = $('#InvestFeePay002_feeType').val();
		var secAcctId = $('#InvestFeePay002_secAcctId').val();
		if (secAcctId) {
			Joyin.ajaxSync(JoyinUtil.getRealPath('/invest/portfolio/feepay/InvestFeePay002/queryStartDate'),
				{secAcctId: secAcctId}, function (result) {
					if (!result || result == 'undefined') {
						isNew = true;
					}else{
						isNew = false;
					}
				});
		}else{
			isNew = false;
		}
		if(isNew && feeType == '08'){
			feeInfoTable.clear();
		}		
	}
	//add by yujiaping 20210303 ZGBSS-654 费用类型选择【账户维护费】在没有待支付的数据时修改支付日清空组合信息 end
	/**
	 * 实付金额联动处理
	 */
	function actPayAmtChangeAction(){
		var actPayAmt = $('#InvestFeePay002_actPayAmt').val();
		var payAmt = $('#InvestFeePay002_payAmt').val();
		if (payAmt && actPayAmt){
			$('#InvestFeePay002_adjustAmt').val(JoyinMath.doMinus(payAmt, actPayAmt, 2, JoyinMath.ROUND_HALF_UP)).trigger('change');
		}else{
			$('#InvestFeePay002_adjustAmt').val('').trigger('change');		
		}
		$('#InvestFeePay002_hidden_tradeAmt').val($('#InvestFeePay002_actPayAmt').val());
		
	};
	
    /**
     * 计算按钮点击事件
     */
    $('#InvestFeePay002_btn_calculate').click(function() {
    	var feeType = $('#InvestFeePay002_feeType').val();
		var secAcctId = $('#InvestFeePay002_secAcctId').val();
		var regTrustOrg = $('#InvestFeePay002_regTrustOrg').val();
    	if (feeType == '14' || feeType == '08') {
    		if (!secAcctId) {
    			Joyin.error("当前费用类型下，证券账户必选！")
                return;
    		}
    	}else if(feeType == '07' && !regTrustOrg){
			Joyin.error("当前费用类型下，登记托管机构必选！")
			return;
		}

    	var startDate = $('#InvestFeePay002_positionStartDate').val();
    	var endDate = $('#InvestFeePay002_positionEndDate').val();
    	var payDate = $("#InvestFeePay002_payDate").val();
    	if (!startDate) {
    		Joyin.error("计费开始日不能为空！")
            return;
    	}
    	if (!endDate) {
    		Joyin.error("计费结束日不能为空！")
            return;
    	}
    	if (!payDate) {
    		Joyin.error("支付日不能为空！")
            return;
    	}
		calculateFlag = true;
    	reloadTable();
		calculateFlag = false;
    });
	
	/**
     * 划款指令定义数据表格
     *
     */
    var option = {
        searchOnInit: true,
        checkbox: false,
        cols: [
            {
                field: 'isNeedSend', width: 140, title: 'BussTrdTransferOrder.isNeedSend', edit: true, sortable:false,
                editOption: {type: 'select', multiple: false, searchable: false, showField: 'text', result: YES_NO}
            }
            , {field: 'payTypeText', width:130, title: 'BussTrdTransferOrder.payType', sortable:false, sortable:false}
            , {field: 'settleDateText', width:130, title: 'BussTrdTransferOrder.settleDate', sortable:false}
            , {field: 'settleAmt', width: 130, title: 'BussTrdTransferOrder.settleAmt', type: 'money', sortable:false}
            , {field: 'ccyText', width: 130, title: 'BussTrdTransferOrder.ccy', sortable:false}
            , {
            	field: 'payAcctNo', width: 130, title: 'BussTrdTransferOrder.payAcctNo', edit: true, sortable:false,
                editOption: {
                    type: 'select', multiple: false, searchable: true, showField: 'id', parentIds:'orderId_select,acctNoList',
                    url: JoyinUtil.getRealPath("/common/multiOrder/select"), id: 'payAcctNo', postData:function () {
                        return {selectValue: JSON.stringify(orderAcctMap), type: 'pay'};
                    }
                }
            }
            , {field: 'payAcctName', width: 150, title: 'BussTrdTransferOrder.payAcctName', sortable:false}
            , {
            	field: 'recAcctNo', width: 130, title: 'BussTrdTransferOrder.recAcctNo', edit: true, sortable:false,
                editOption: {
                    type: 'select', multiple: false, searchable: true, showField: 'id', parentIds:'orderId_select,acctNoList',
                    url: JoyinUtil.getRealPath("/common/multiOrder/select"),id: 'recAcctNo',  postData:function () {
                        return {selectValue: JSON.stringify(orderAcctMap), type: 'rec'};
                    }
                }
            }
            , {field: 'recAcctName', width: 150, title: 'BussTrdTransferOrder.recAcctName', sortable:false}
            , {field: 'purpose', width:130, title: 'BussTrdTransferOrder.purpose', sortable:false, edit:true}
            , {field: 'portfolioId', width: 130, title: 'BussTrdTransferOrder.portfolioId', sortable:false}
            , {field: 'portfolioName', width:150, title: 'BussTrdTransferOrder.portfolioName', sortable:false}
            , {field: 'orderId', visible: true, edit: true,width:0,
                editOption: {
                    type: 'select', id:'orderId_select', childrenIds:'recAcctNo,payAcctNo'
                }}
            , {field: 'ps', visible: false}
            , {field: 'settleStatus', visible: false}
            , {field: 'isTotalTrade', visible: false}
        ],
        done:function(){
            $("[data-field='orderId']").hide();
        },
        page: false,
        data: [],
        editCell: function (value, row, field, cell, column) {
            if (field == 'payAcctNo') {
                if (typeof value == "undefined" || value == null || value == "") {
                    row.payAcctName = "";
                }
                // 选择付款账户带出贷款账户名称
                if (typeof value != "undefined" && value) {
                    Joyin.ajax(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                        ccy: row.ccy,
                        acctNo: value
                    }, function (result) {
                        //add by wangmin 2019-0-06 begin
                        if (!result) {
                            Joyin.error("该币种下，无此账户。")
                            return;
                        }
                        row.payAcctName = result.acctName;
                        //add by wangmin 2019-0-06 end
                        table.renderRow(row.LAY_TABLE_INDEX, row);
                    })
                } else {
                    table.renderRow(row.LAY_TABLE_INDEX, row);
                }

            } else {
                if (typeof value == "undefined" || value == null || value == "") {
                    row.recAcctName = "";
                }
                if (typeof value != "undefined" && value) {
                    // 选择收款账户带出行内/行外、网点
                    Joyin.ajax(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                        ccy: row.ccy,
                        acctNo: value
                    }, function (result) {
                        // 网点
                        row.recAcctPoint = result.largePayno;
                        //add by wangmin 2019-0-06 begin
                        row.recAcctName = result.acctName;
                        //add by wangmin 2019-0-06 end
                        table.renderRow(row.LAY_TABLE_INDEX, row);
                    })
                } else {
                    table.renderRow(row.LAY_TABLE_INDEX, row);
                }
            }
        }
    }

    var tableInitData = function () {
        var feeInfos = feeInfoTable.getAllRows();
        var dtos = [];
        for (var i = 0; i < feeInfos.length; i++) {
            var dealReal = {};
            dealReal.finprodType2=$("#InvestFeePay002_hidden_feeType").val();
            dealReal.ps=ps;
            dealReal.tradeDate=$('#InvestFeePay002_payDate').val();
            dealReal.tradeAmt=feeInfos[i].actPayAmt;
            dealReal.ccy='CNY';
            dtos.push({
                'isTotalTrade': 'Y'
                , 'portfolioId': feeInfos[i].ptlId
                , 'deal':dealReal
            })
        }
        return dtos;
    }
    
    if (bussTrdTransferOrderDtos && bussTrdTransferOrderDtos.length > 0) {
        option.data = option.data.concat(bussTrdTransferOrderDtos);
    }
    var orderAcctMap = [];
    var table = $("#bussTrdTransferOrder_table").joyintable(option);
    // 生成指令参数保存
    var stlCommandInput = tableInitData;

    // 检查生成指令参数是否发生变化
    var isCalcConditionChanged = function() {
        // 参照模式，不做check
        if (pageModel === PAGE_MODEL_REFERENCE) {
            return false;
        }
        if (!$("#bussTrdTransferOrder_div").is(":visible")) {
            return false;
        }
        if (calcConditionObjBackUp == null) {
            return false;
        }

        if(stlCommandInput != null){
            var currentCalcConditionObj = stlCommandInput();
            // 影响计划生成的所有项目与计划生成时记录的值比较，值变更时
            for ( var key in currentCalcConditionObj) {
                for ( var key1 in currentCalcConditionObj[key]) {
                    if (typeof currentCalcConditionObj[key][key1] != "object"
                        && currentCalcConditionObj[key][key1] != calcConditionObjBackUp[key][key1]) {
                    	if(key1=='finprodType2'){
							var labelText = JoyinUtil.getLabel("InvestFeePay002.feeType");
							Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
								[labelText, "生成划款指令"]));
						}else{
							var labelText = JoyinUtil.getLabel("BussTrdTransferOrder." + key1);
							Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
								[labelText, "生成划款指令"]));
						}
                        return true;
                    }
                }
            }
            for ( var key in currentCalcConditionObj) {
                for ( var key1 in currentCalcConditionObj[key].deal) {
                    var currentDealObj = currentCalcConditionObj[key].deal;
                    var backupDealObj = calcConditionObjBackUp[key].deal;
                    if (typeof currentDealObj[key1] != "object"
                        && currentDealObj[key1] != backupDealObj[key1]) {
						if(key1=='finprodType2'){
							var labelText = JoyinUtil.getLabel("InvestFeePay002.feeType");
							Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
								[labelText, "生成划款指令"]));
						}else{
							var labelText = JoyinUtil.getLabel("BussTrdTransferOrder." + key1);
							Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
								[labelText, "生成划款指令"]));
						}
                        return true;
                    }
                }
            }
        }
    };

    $("#bussTrdTransferOrder_btn_generate").on("click", function () {
        if(stlCommandInput != null) {
            // var index = layer.open({
            //     type: 1,
            //     icon : 16,
            //     area: ['200px', '100px'],
            //     content: '数据加载中',closeBtn: 0
            // });
            var data = stlCommandInput();
            // 保存生成指令参数
            calcConditionObjBackUp = data;
            orderAcctMap = [];
            var result = [];
            //add by  meifaxin  20211109 单独判断费用类型为空,上面把费用类型赋值给金融资产类型，结果报错信息和实际为空字段对不上 start
			var tempfeeType=$("#InvestFeePay002_hidden_feeType").val();
			if(tempfeeType==null||tempfeeType==''){
				return Joyin.error('费用类型为空');
			}
			//add by  meifaxin  20211109 单独判断费用类型为空,上面把费用类型赋值给金融资产类型，结果报错信息和实际为空字段对不上 end
			Joyin.ajaxJson(JoyinUtil.getRealPath('/common/multiOrder/genOrderCheck'), data, function () {
                Joyin.ajaxJson(JoyinUtil.getRealPath('/common/multiOrder/generateOrder'), data, function (ret) {
                    if (data.length == ret.data.length) {
                        for (var key in ret.data) {
                            orderAcctMap[ret.data[key].orderId] = {
                                payAcctNoList: ret.data[key].payAcctNoList,
                                recAcctNoList: ret.data[key].recAcctNoList
                            }
                        }
                        table.clear(function () {
                            table.addRow(ret.data)
                            // layui.layer.close(index);
                        })
                    }
                });
            });
        }
    });
    
	function saveAction(){
		saveOrSubmitAction('save');
	};
	
	function submitAction(){
		saveOrSubmitAction('submit');
	};
	
	function saveOrSubmitAction(operateType){
		if($('#InvestFeePay002_hidden_operateType').val() === 'confirm'){
			var feeInfoTableList=feeInfoTable.getAllRows();
			var sum=0;
			for(var i=0;i<feeInfoTableList.length;i++){
				sum=sum+Number(feeInfoTableList[i].actPayAmt);
			}
			if(sum != 4500){
				Joyin.error("汇总金额不等于4500，请修改！");
				return false;
			}
		}
		// 检查生成划款指令参数是否发生变化
        if (isCalcConditionChanged()) {
            return;
        }
        
		var object = $.serializeToObject('#InvestFeePay002_Form', false);
		object.operateType = operateType;
        object.pageModel = pageModel;
        object.isClean = 'Y';
        
        if(FileCommon) {
            object.fileArray = FileCommon.getUploadFileInfo();
        }
        
        var actPayAmtSum = '0';
        var actPayAmt = parseFloat($('#InvestFeePay002_actPayAmt').val());
        $.each(object.feeInfoList, function(id, value){
        	actPayAmtSum = JoyinMath.doAdd(actPayAmtSum,value.actPayAmt);
        	value.isClean = 'Y';
		});
        if (actPayAmt != actPayAmtSum) {
        	Joyin.error("各组合的实付金额之和不等于费用信息中填写的实付费用金额！");
            return false;
        }
        
        //费用提交时校验收款账户是否为空
        if (object.bussTrdTransferOrder[0]) {
        	// 是否发送指令为是收付款账户均为必填，为否则付款账户必填，收款账户非必填
        	for (var i = 0; i < object.bussTrdTransferOrder.length; i++) {
        		if(object.bussTrdTransferOrder[i].isNeedSend == 'Y'
            			|| object.bussTrdTransferOrder[i].isNeedSend == 'Y'){
            		if (!object.bussTrdTransferOrder[i].recAcctNo){
                        Joyin.error("划款指令请选择收款账户！");
                        return false;
                    }
            		if (!object.bussTrdTransferOrder[i].payAcctNo){
                        Joyin.error("划款指令请选择付款账户！");
                        return false;
                    }
            	}else{
            		if (!object.bussTrdTransferOrder[i].payAcctNo){
                        Joyin.error("划款指令请选择付款账户！");
                        return false;
                    }
            	}
            }
        }

        if($('#InvestFeePay002_feeType').val()=='08' && !$('#InvestFeePay002_positionStartDate').val()){
			object.endDate = '';
		}
        Joyin.ajaxJson(basePath + 'invest/portfolio/feepay/InvestFeePay002/check',object, function(result) {
            Joyin.confirm("是否确认？", function() {
                    if(operateType === 'save') {
                        WorkFlow.doSaveList(basePath + 'invest/portfolio/feepay/InvestFeePay002/save', object, "InvestFeePay002_btn_save");
                    }
                    else {
                        WorkFlow.doSubmitList(basePath + 'invest/portfolio/feepay/InvestFeePay002/save', object, "InvestFeePay002_btn_submit");
                    }
                })
            });
	}
	
	// 所有dom对象加载完的处理
	$(document).ready(function() {
		$('#fee_div').toggle(false);
		var feeType = $('#InvestFeePay002_hidden_feeType').val();
		var chargeType = $('#InvestFeePay002_hidden_chargeType').val();
		if(feeType != null && feeType != 'undefined'){
			reloadTable();
		}
		if(feeType == '08'){
			$('#InvestFeePay002_secAcctId').prop("disabled", true);
			$('#InvestFeePay002_feeType').prop("disabled", true);
			$('#InvestFeePay002_regTrustOrg').prop("disabled", true);
			$('#InvestFeePay002_positionEndDate').prop("disabled", true);
			if(!$('#InvestFeePay002_positionStartDate').val()){
				$('#InvestFeePay002_div_positionStartDate').hide();
				$('#InvestFeePay002_div_positionEndDate').hide();
				$('#InvestFeePay002_div_payAmt').hide();
				$('#InvestFeePay002_div_adjustAmt').hide();
				$('#InvestFeePay002_div_calculate').hide();
				feeInfoTable.showColumns([{field:'operation', visible:true}]);
				feeInfoTable.showColumns([{field:'ptlId', visible:true}]);
				feeInfoTable.showColumns([{field:'portFolioName', visible:false}]);
				feeInfoTable.showColumns([{field:'payAmt', visible:false}]);
				feeInfoTable.showColumns([{field:'adjustAmt', visible:false}]);
				feeInfoTable.showColumns([{field:'status', visible:false}]) ;
			}
			feeInfoTable.showColumns([{field:'status', visible:false}]);
		}
		if(feeType == '06' || feeType == '07'){
			$('#InvestFeePay002_secAcctId').prop("disabled", true);
		}
		// 费用类型
		$('#InvestFeePay002_feeType').change(feeTypeChangeAction);
		// 证券账户
		$('#InvestFeePay002_secAcctId').change(secAcctIdFeeTypeChangeAction);
		//登记托管机构
		$('#InvestFeePay002_regTrustOrg').change(function (){
			if($(InvestFeePay002_feeType).val() == '06'||$(InvestFeePay002_feeType).val() == '07'){
				secAcctIdFeeTypeChangeAction();
			}
		});
		$('#InvestFeePay002_regTrustOrg').change(payTradeAmtChangeAction);
		//证券账户联动
		$('#InvestFeePay002_secAcctId').change(payTradeAmtChangeAction);
		//计提开始日
		$('#InvestFeePay002_positionStartDate').change(endDateChangeAction);
		// 计提结束日
		$('#InvestFeePay002_positionEndDate').change(endDateChangeAction);
		// 支付日
		// $('#InvestFeePay002_payDate').change(payDateChangeAction);
		$('#InvestFeePay002_payDate').change(feeInfoListAction);
		// 实付金额
		$('#InvestFeePay002_actPayAmt').change(actPayAmtChangeAction);
		// 保存按钮
		$('#InvestFeePay002_btn_save').click(saveAction);
		// 提交按钮
		$('#InvestFeePay002_btn_submit').click(submitAction);
		
		if($('#InvestFeePay002_hidden_operateType').val() === 'confirm'){
			$('#InvestFeePay002_div_adjustAmt').hide();
			$('#InvestFeePay002_div_calculate').hide();
		}
	});
}());