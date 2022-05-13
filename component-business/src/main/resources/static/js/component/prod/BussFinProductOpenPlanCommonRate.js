(function(){
	var getElementById = function(id) {
		return $('#' + bussFinProductOpenPlanDto + '_' + id);
	};
	var getElementByRedId = function(id) {
		return $('#' + bussFinProductOpenPlanRedDto + '_' + id);
	};

	// add by mashukai 20201116 BS-1993,BS-1996 开放计划，赎回计划反显值不对 start
	if(isDraft && sameFlag1=='N'&& $('#FinNetValueProductBuild002_dealMode').val() != '10'){
		$("#red_div").show();
	}
	// add by mashukai 20201116 BS-1993,BS-1996 开放计划，赎回计划反显值不对 end
	/**
	 * 初始化加载tableFlag 
	 */
	var initFlag = true;
	var firstPayDate1=null;
	var payCycle1=null;
	var payCycleVal1=null;
	var payCycleUnit1=null;
	var endTdayRule1=null;
	var payTdayRule1=null;
	var vdate3=null;
	var mdate3=null;
	
	var getSearchParams = function(){
		var param = {};
		param.initFlag = initFlag;
		param.profitType = $("#"+profitType).val();
		if (!initFlag){
			param.finprodId = openPlanFinprodId;
			if(workflowId==null||workflowId==undefined){
				param.workflowId = $("#workflowIdBackUp").val();
			}
			else{
				param.workflowId = workflowId;
			}
			//为净值型产品修改
			if(getElementById('openType').val()&&getElementById('openType').val()!='03'){
				param.openType = getElementById('openType').val();
			}
			else{
				param.openType=getElementById('sameFLag').next().find("em").text()==="是"?"03":"01";
			}
			param.entityName = bussFinProductOpenPlanDto;
			
		}else{
			param.pageId = parentPageId;
			param.dealMode = $('#' + openPlanDealMode).val();
			param.vdate = $('#' + vdate1).val();
			param.mdate = $('#' + mdate1).val();
			param.tradCalender = $('#' + tradCalender).val();
			param.raiseStrDate = $('#' + raiseStrDate).val();
			param.raiseEndDate = $('#' + raiseEndDate).val();
			param.purConfDays =getElementById('purSpeed').val();
			if ($('#' + isCycle).next().find("em").text() === '是') {
				param.isCycle = 'Y';
	        } else {
	        	param.isCycle = 'N';
	        }
			param.closeTerm = $('#' + closeTerm).val();
			
			param.openMode = getElementById('openMode').val();
			param.openDays = getElementById('openDays').val();
			param.openCycle = getElementById('openCycle').val();
			param.openCycleUnit = getElementById('openCycleUnit').val();
			if (getElementById('dayOfWeek').val()){
				param.dayOfWeek = getElementById('dayOfWeek').val().join(",");
			}
			param.openDate = getElementById('radioDiv').find(':radio:checked').val();
			if (getElementById('dayOfMonth').val()){
				param.dayOfMonth = getElementById('dayOfMonth').val().join(",");
			}
			if (getElementById('workdayOfMonth').val()){
				param.workdayOfMonth = getElementById('workdayOfMonth').val().join(",");
			}
            if (getElementById('weekOfMonth').val()) {
                param.weekOfMonth = getElementById('weekOfMonth').val().join(",");
            }
            if (getElementById('workdayOfWeek').val()) {
                param.workdayOfWeek = getElementById('workdayOfWeek').val().join(",");
            }
			param.calcMethod = getElementById('calcMethod').val();
			param.firstOpenDate = getElementById('firstOpenDate').val();
			param.tradedayRule = getElementById('tradedayRule').val();
			param.calcRule = getElementById('calcRule').val();
			param.cpriceRule=getElementById('cpriceRule').val();
			param.openType=getElementById('sameFLag').next().find("em").text()==="是"?"03":"01";
			getElementByRedId('openType').val(param.openType);
			param.openDaysType = getElementById('openDaysType').val();
			if ($('#hdayOpenFlag').prop('checked')) {
				param.hdayOpenFlag = 'Y';
	        } else {
	        	param.hdayOpenFlag = 'N';
	        }
			param.operationType = $('#' + operationType).val();
		}
		return param;
	}
	
	var checkInputParam = function (){
		initFlag = true;
		$('#isClick').val("true");
		Joyin.ajaxJson(JoyinUtil.getRealPath('component/prod/BussFinProductOpenPlanCommonRate/checkInputParam'), getSearchParams(), function(result){
			if (result.data){
				Joyin.confirm(result.data, function(){
					table.table ? table.reload() : table.load();
					var tableList =  table.getAllRows();
					if(tableList != null && tableList.length > 0){
				 /*    for (var i in tableList) {
				            console.log(new Date(tableList[i].openNdate).format('yyyy-MM-dd'));
				        }*/
						if($('#' + profitType).val() === PROFIT_TYPE_01){
							 $('#openNdateStart').val(new Date(tableList[0].openNdate).format('yyyy-MM-dd'));
						     $('#openNdateEnd').val(new Date(tableList[tableList.length-1].openNdate).format('yyyy-MM-dd'));
						}
						else{
							 $('#openNdateStart').val(new Date(tableList[0].openDateEnd).format('yyyy-MM-dd'));
						     $('#openNdateEnd').val(new Date(tableList[tableList.length-1].openDateEnd).format('yyyy-MM-dd'));
						}
						
						ProductPayPlan.genePayPlan(tableList);

					}
	            });
			}else{
				table.table ? table.reload() : table.load();
				var tableList =  table.getAllRows();
				if($('#' + profitType).val() === PROFIT_TYPE_01){
					 $('#openNdateStart').val(new Date(tableList[0].openNdate).format('yyyy-MM-dd'));
				     $('#openNdateEnd').val(new Date(tableList[tableList.length-1].openNdate).format('yyyy-MM-dd'));
				}
				else{
					 $('#openNdateStart').val(new Date(tableList[0].openDateEnd).format('yyyy-MM-dd'));
				     $('#openNdateEnd').val(new Date(tableList[tableList.length-1].openDateEnd).format('yyyy-MM-dd'));
				}
				
				ProductPayPlan.genePayPlan(tableList);
			}
		})
	}
	
	var getSearchParamsRed = function(){
		var param = {};
		param.initFlag = initFlag;
		param.profitType = $("#"+profitType).val();
		if (!initFlag){
			param.finprodId = openPlanFinprodId;
			if(workflowId==null||workflowId==undefined){
				param.workflowId = $("#workflowIdBackUp").val();
			}
			else{
				param.workflowId = workflowId;
			}
			param.entityName = bussFinProductOpenPlanDto;
			param.openType="02";
		}else{
			param.pageId = parentPageId;
			param.dealMode = $('#' + openPlanDealMode).val();
			param.vdate = $('#' + vdate1).val();
			param.mdate = $('#' + mdate1).val();
			param.tradCalender = $('#' + tradCalender).val();
			param.raiseStrDate = $('#' + raiseStrDate).val();
			param.raiseEndDate = $('#' + raiseEndDate).val();
			param.purConfDays =getElementByRedId('redSpeed').val();
			if ($('#' + isCycle).next().find("em").text() === '是') {
				param.isCycle = 'Y';
	        } else {
	        	param.isCycle = 'N';
	        }
			param.closeTerm = $('#' + closeTerm).val();
			
			param.openMode = getElementByRedId('openMode').val();
			param.openDays = getElementByRedId('openDays').val();
			param.openCycle = getElementByRedId('openCycle').val();
			param.openCycleUnit = getElementByRedId('openCycleUnit').val();
			if (getElementByRedId('dayOfWeek').val()){
				param.dayOfWeek = getElementByRedId('dayOfWeek').val().join(",");
			}
			param.openDate = getElementByRedId('radioDiv').find(':radio:checked').val();
			if (getElementByRedId('dayOfMonth').val()){
				param.dayOfMonth = getElementByRedId('dayOfMonth').val().join(",");
			}
			if (getElementByRedId('workdayOfMonth').val()){
				param.workdayOfMonth = getElementByRedId('workdayOfMonth').val().join(",");
			}
			if (getElementByRedId('weekOfMonth').val()){
			    param.weekOfMonth = getElementByRedId('weekOfMonth').val().join(",");
			}
			if (getElementByRedId('workdayOfWeek').val()){
			    param.workdayOfWeek = getElementByRedId('workdayOfWeek').val().join(",");
			}
			param.calcMethod = getElementByRedId('calcMethod').val();
			param.firstOpenDate = getElementByRedId('firstOpenDate').val();
			param.tradedayRule = getElementByRedId('tradedayRule').val();
			param.calcRule = getElementByRedId('calcRule').val();
			param.cpriceRule=getElementByRedId('cpriceRule').val();
			param.openType="02";
			param.openDaysType = getElementByRedId('openDaysType').val();
			if ($('#hdayOpenFlag').prop('checked')) {
				param.hdayOpenFlag = 'Y';
	        } else {
	        	param.hdayOpenFlag = 'N';
	        }
			param.operationType = $('#' + operationType).val();
		}
		return param;
	}
	var checkInputParamRed = function (){
		$('#isClickRed').val("true");
		initFlag = true;
		Joyin.ajaxJson(JoyinUtil.getRealPath('component/prod/BussFinProductOpenPlanCommonRate/checkInputParam'), getSearchParamsRed(), function(result){
			if (result.data){
				Joyin.confirm(result.data, function(){
					tableRed.table ? tableRed.reload() : tableRed.load();
					var tableRedList =  tableRed.getAllRows();
					if(tableRedList != null && tableRedList.length > 0){
						if($('#' + profitType).val() === PROFIT_TYPE_01){
						     $('#openNdateRedStart').val(new Date(tableRedList[0].openNdate).format('yyyy-MM-dd'));
						     $('#openNdateRedEnd').val(new Date(tableRedList[tableRedList.length-1].openNdate).format('yyyy-MM-dd'));
						}
						else{
							 $('#openNdateRedStart').val(new Date(tableRedList[0].openDateEnd).format('yyyy-MM-dd'));
						     $('#openNdateRedEnd').val(new Date(tableRedList[tableRedList.length-1].openDateEnd).format('yyyy-MM-dd'));
						}
					}
	            });
			}else{
				tableRed.table ? tableRed.reload() : tableRed.load();
				var tableRedList =  tableRed.getAllRows();
				if(tableRedList != null && tableRedList.length > 0){
					if($('#' + profitType).val() === PROFIT_TYPE_01){
					     $('#openNdateRedStart').val(new Date(tableRedList[0].openNdate).format('yyyy-MM-dd'));
					     $('#openNdateRedEnd').val(new Date(tableRedList[tableRedList.length-1].openNdate).format('yyyy-MM-dd'));
					}
					else{
						 $('#openNdateRedStart').val(new Date(tableRedList[0].openDateEnd).format('yyyy-MM-dd'));
					     $('#openNdateRedEnd').val(new Date(tableRedList[tableRedList.length-1].openDateEnd).format('yyyy-MM-dd'));
					}
				}
			}
		})
		firstPayDate1 = getElementById('firstPayDate').val()
		payCycle1 = getElementById('payCycle').val()
		payCycleVal1 = getElementById('payCycleVal').val()
		payCycleUnit1 = getElementById('payCycleUnit').val()
		endTdayRule1 = getElementById('endTdayRule').val()
		payTdayRule1 = getElementById('payTdayRule').val()
		vdate3 = $('#' + vdate1).val()
		mdate3 = $('#' + mdate1).val()
	}

	var table;
	var tableRed;
	/**
	 * 定义数据表格
	 */
	table = getElementById('dataTable').joyintable({
		    	ajax: {
		            url: JoyinUtil.getRealPath('component/prod/BussFinProductOpenPlanCommonRate/previewList'),
		            type: 'POST',
		            data: function() {
		            	var param = getSearchParams();
		            	param.finprodIdCreatePlan = $("#finprodId").val();
		            	param.nextOpenDay = $("#nextOpenDay").val();
		                return param;
		            }
		        },
		        searchOnInit: true,
		        height: 300,
		        page: false,
		        checkbox: false,
		        loadonce: true,
		        rowno: true,
		        /**20200305 add by taoran 净值化产品，收益型产品，支持添加 删除开放区间 start*/
		        addbtn: parentPageId == 'FinNetValueProductBuild002' || parentPageId == 'FinProductBuild002' || parentPageId == 'FinPlanNetAsset002' || parentPageId == 'FinPlanProfitAsset002'|| parentPageId == 'FinPlanAssetC002',
		        /**20200305 add by taoran 净值化产品，收益型产品，支持添加 删除开放区间 end*/
		        done: function() {

		        	//update zhangweiqi 2019-3-11 begin
		        	 openMode1=getElementById('openMode').val();
		    		 openDays1=getElementById('openDays').val();
		    		 openCycle1= getElementById('openCycle').val();
		    		 openCycleUnit1=getElementById('openCycleUnit').val();
		    		 dayOfWeek1=getElementById('dayOfWeek').val();
		    		 dayOfMonth1=getElementById('dayOfMonth').val();
		    		 workdayOfMonth1=getElementById('workdayOfMonth').val();
		    		 calcMethod1=getElementById('calcMethod').val();
		    		 firstOpenDate1=getElementById('firstOpenDate').val();
		    		 tradedayRule1=getElementById('tradedayRule').val();
		    		 calcRule1=getElementById('calcRule').val();
		    		 hdayOpenFlag1=$('#hdayOpenFlag').prop('checked')?"Y":"N";
		    		 purSpeed1=getElementById('Speed').val();
		    		 cpriceRule1=getElementById('cpriceRule').val();
		    		 vdate2 = $('#' + vdate1).val();
		    		 mdate2 = $('#' + mdate1).val();
		    		 tradCalender1 = $('#' + tradCalender).val();
		    		 raiseStrDate1= $('#' + raiseStrDate).val();
		    		 raiseEndDate1=$('#' + raiseEndDate).val();
		        	//update zhangweiqi 2019-3-11 end
		    		 initFlag = false;
		    		 var tableList =  table.getAllRows();
		    		 if(tableList&&tableList.length>0){
		    			 if($('#' + profitType).val() === PROFIT_TYPE_01){
							 $('#openNdateStart').val(new Date(tableList[0].openNdate).format('yyyy-MM-dd'));
						     $('#openNdateEnd').val(new Date(tableList[tableList.length-1].openNdate).format('yyyy-MM-dd'));
						}
						else{
							 $('#openNdateStart').val(new Date(tableList[0].openDateEnd).format('yyyy-MM-dd'));
						     $('#openNdateEnd').val(new Date(tableList[tableList.length-1].openDateEnd).format('yyyy-MM-dd'));
						}
		    		 }
		        },
		        cols:[
					 {field:'openDateStr', title: 'FinProductOpenRule.table.openCycleStartDateEx',sortable:false, type:'date',width : 300,edit: true}
					,{field:'openDateEnd', title: 'FinProductOpenRule.table.openCycleEndDateEx',sortable:false, type:'date',width : 300,edit: true}
					,{field:'openVdate', title: 'FinProductOpenRule.table.openCycleVdateEx',sortable:false, type:'date',width : 300,edit: true}
					,{field:'openNdate', title: 'FinProductOpenRule.table.openNdateEx', sortable:false, type:'date',width : 300 , visible: $('#' + profitType).val() === PROFIT_TYPE_01, edit:true }
					/**20200305 add by taoran 净值化产品，收益型产品，支持添加 删除开放区间 start*/
					,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt'],visible:parentPageId == 'FinNetValueProductBuild002' || parentPageId == 'FinProductBuild002' || parentPageId == 'FinPlanNetAsset002' || parentPageId == 'FinPlanProfitAsset002'|| parentPageId == 'FinPlanAssetC002'}
					/**20200305 add by taoran 净值化产品，收益型产品，支持添加 删除开放区间 end*/
				]
		    });
		/**20200305 add by taoran 净值化产品，收益型产品，支持添加 删除开放区间 start*/
		/**
	     * 列表删除
	     */
	    table.bind('click', '.delt', function(data, index) {
	    	table.removeRow(data);
		});
	    /**20200305 add by taoran 净值化产品，收益型产品，支持添加 删除开放区间 end*/
		/**
	     * 定义数据表格
	     */
		tableRed = getElementByRedId('dataTable').joyintable({
		    	ajax: {
		            url: JoyinUtil.getRealPath('component/prod/BussFinProductOpenPlanCommonRate/previewList'),
		            type: 'POST',
		            data: function() {
		            	var paramRed = getSearchParamsRed();
		            	paramRed.finprodIdCreatePlan = $("#finprodId").val();
		            	paramRed.nextOpenDay = $("#nextOpenDay").val();
		                return paramRed;
		            }
		        },
		        searchOnInit: true,
		        height: 300,
		        page: false,
		        checkbox: false,
		        loadonce: true,
		        rowno: true,
		        cols:[
					 {field:'openDateStr', title: 'FinProductOpenRule.table.openCycleStartDateEx',sortable:false, type:'date',width : 300}
					,{field:'openDateEnd', title: 'FinProductOpenRule.table.openCycleEndDateEx',sortable:false, type:'date',width : 300}
					,{field:'openVdate', title: 'FinProductOpenRule.table.openCycleVdateEx',sortable:false, type:'date',width : 300}
					,{field:'openNdate', title: 'FinProductOpenRule.table.openNdateEx', sortable:false, type:'date',width : 300 , visible: $('#' + profitType).val() === PROFIT_TYPE_01 }
		        ],
                done: function() {
                	//update zhangweiqi 2019-3-11 begin
		        	 openModeRed1=getElementByRedId('openMode').val();
		    		 openDaysRed1=getElementByRedId('openDays').val();
		    		 openCycleRed1= getElementByRedId('openCycle').val();
		    		 openCycleUnitRed1=getElementByRedId('openCycleUnit').val();
		    		 dayOfWeekRed1=getElementByRedId('dayOfWeek').val();
		    		 dayOfMonthRed1=getElementByRedId('dayOfMonth').val();
		    		 workdayOfMonthRed1=getElementByRedId('workdayOfMonth').val();
		    		 calcMethodRed1=getElementByRedId('calcMethod').val();
		    		 firstOpenDateRed1=getElementByRedId('firstOpenDate').val();
		    		 tradedayRuleRed1=getElementByRedId('tradedayRule').val();
		    		 calcRuleRed1=getElementByRedId('calcRule').val();
		    		 hdayOpenFlagRed1=getElementByRedId('#hdayOpenFlag').prop('checked')?"Y":"N";
		    		 purSpeedRed1=getElementByRedId('redSpeed').val();
		    		 cpriceRuleRed1=getElementByRedId('cpriceRule').val();
//		    		 vdate2 = $('#' + vdate1).val();
//		    		 mdate2 = $('#' + mdate1).val();
//		    		 tradCalender1 = $('#' + tradCalender).val();
//		    		 raiseStrDate1= $('#' + raiseStrDate).val();
//		    		 raiseEndDate1=$('#' + raiseEndDate).val();
		        	//update zhangweiqi 2019-3-11 end
                    initFlag = false;
                }
		    });
	var openModeChange = function (){
		
		var selectedValue = getElementById('openMode').val();
		// 定期开放或者未勾选
		if (!selectedValue || selectedValue == OPENMODE_01){
			getElementById('openDays').prop('disabled', false);
			getElementById('openCycle').prop('disabled', false);
			getElementById('dayOfWeek').prop('disabled', false);
			getElementById('radioDiv').find(':radio').prop('disabled', false);
			getElementById('dayOfMonth').prop('disabled', false);
			getElementById('workdayOfMonth').prop('disabled', false);
			getElementById('openCycleUnit').trigger('change').prop('disabled', false);
			//getElementById('firstOpenDate').prop('disabled', false);
			getElementById('tradedayRule').trigger('change').prop('disabled', false);
			getElementById('calcRule').prop('disabled', false);
			if(parentPageId !== 'FinOpenPlanMaintenance002'){
				getElementById('calcMethod').prop('disabled', false);
			}
			getElementById('hdayOpenFlag').prop('disabled', false);
			getElementById('openDaysType').prop('disabled', false).val(OPEN_DAYS_TYPE_01).trigger('change');
//			getElementById('purSpeed').prop('disabled', false).val('');
			getElementById('tableDiv').show();
		}
		// 高频开放
		else if (selectedValue == OPENMODE_02){
			getElementById('openCycle').prop('disabled', true).val('').trigger('change');
			getElementById('openCycleUnit').prop('disabled', true).val('').trigger('change');
			getElementById('dayOfWeek').prop('disabled', true).val('').trigger('change');
			getElementById('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
			getElementById('dayOfMonth').prop('disabled', true).val('').trigger('change');
			getElementById('workdayOfMonth').prop('disabled', true).val('').trigger('change');
			getElementById('tradedayRule').prop('disabled', true).val('').trigger('change');
//			getElementById('calcRule').prop('disabled', true).val('').trigger('change');
			if(parentPageId !== 'FinOpenPlanMaintenance002'){
				getElementById('calcMethod').prop('disabled', false);
			}
			getElementById('openDaysType').prop('disabled', true).val('').trigger('change');
			getElementById('openDays').prop('disabled', true).val('').trigger('change');
//			getElementById('purSpeed').prop('disabled', true).val('').trigger('change');
			//getElementById('firstOpenDate').prop('disabled', false);
			if ($('#' + openPlanDealMode).val() === DISPOSE_PATTERN_07) {
			    getElementById('hdayOpenFlag').prop('checked', true);
			}
			table.reload({ajax:null, url: null, data:[]});
			getElementById('tableDiv').hide();
		}
		// 周期开放
		else if (selectedValue == OPENMODE_03){
			getElementById('openDays').prop('disabled', false);
			getElementById('openCycle').prop('disabled', false);
			if(pageModel === PAGE_MODEL_NEW){
				getElementById('openCycleUnit').val(PERIOD_UNIT_01).trigger('change');
				getElementById('tradedayRule').val(TDAY_RULE_02).trigger('change');
			}
			getElementById('openCycleUnit').prop('disabled', false);
			getElementById('tradedayRule').prop('disabled', false);
			getElementById('calcRule').prop('disabled', false);
			if(parentPageId !== 'FinOpenPlanMaintenance002'){
				getElementById('calcMethod').prop('disabled', false);
			}
			//getElementById('firstOpenDate').prop('disabled', false);
			getElementById('hdayOpenFlag').prop('disabled', false);
			getElementById('dayOfWeek').prop('disabled', true).val('').trigger('change');
			getElementById('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
			getElementById('dayOfMonth').prop('disabled', true).val('').trigger('change');
			getElementById('workdayOfMonth').prop('disabled', true).val('').trigger('change');
			getElementById('openDaysType').prop('disabled', false).val(OPEN_DAYS_TYPE_01).trigger('change');
//			getElementById('purSpeed').prop('disabled', false).val('');
			getElementById('tableDiv').show();
		}
	}
	
	var openCycleUnitChange = function (){
		var selectedValue = getElementById('openCycleUnit').val();
		var openMode = getElementById('openMode').val();
		//BS-1977 start
		if(selectedValue==null){
			return;
		}
		//BS-1977 end
		if (openMode && openMode === OPENMODE_01){
			if (!selectedValue || selectedValue == PERIOD_UNIT_01){
				getElementById('dayOfWeek').val('').prop('disabled', true).trigger('change');
				getElementById('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
				getElementById('dayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementById('workdayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true);
	            getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true);
			}
			else if (selectedValue == PERIOD_UNIT_02){
				getElementById('dayOfWeek').prop('disabled', false);
				getElementById('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
				getElementById('dayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementById('workdayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true);
	            getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true);
			}
			else if (selectedValue == PERIOD_UNIT_03){
				getElementById('dayOfWeek').val('').prop('disabled', true).trigger('change');
				getElementById('radioDiv').find(':radio').prop('disabled', false);
				if(pageModel === PAGE_MODEL_NEW || getElementById('radioDiv').find(':radio:checked').length === 0) {
					getElementById('radioDiv').find(':radio[value="0"]').prop('checked', true);
				} 
				getElementById('dayOfMonth').val('').prop('disabled', false).trigger('change');
				getElementById('workdayOfMonth').val('').prop('disabled', true).trigger('change');
			}
			else if (selectedValue == PERIOD_UNIT_04){
                getElementById('dayOfWeek').val('').prop('disabled', true).trigger('change');
                getElementById('radioDiv').find(':radio').prop('disabled', false);
                if(pageModel === PAGE_MODEL_NEW || getElementById('radioDiv').find(':radio:checked').length === 0) {
                    getElementById('radioDiv').find(':radio[value="0"]').prop('checked', true);
                    getElementById('radioDiv').find(':radio[value="2"]').prop('disabled', true);
                }
                getElementById('dayOfMonth').val('').prop('disabled', false).trigger('change');
                getElementById('workdayOfMonth').val('').prop('disabled', true).trigger('change');
                getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true);
                getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true);
            }
		}else{
			getElementById('dayOfWeek').val('').prop('disabled', true).trigger('change');
			getElementById('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
			getElementById('dayOfMonth').val('').prop('disabled', true).trigger('change');
			getElementById('workdayOfMonth').val('').prop('disabled', true).trigger('change');
			getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true);
            getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true);
		}
		layui.form.render('radio');
	}
	
	//add by yujiaping 20210312 ZGBS-168 控制开放周期、开放天数、确认天数只能输入整数 start
	var openCycleChange = function (){
		if(getElementById('openCycle').val()){
		var r =/^\d+$/;
		var flag=r.test(getElementById('openCycle').val());
		if(!flag){
			Joyin.error(JoyinUtil.getMessage("开放周期只能输入整数"));
			getElementById('openCycle').val('');
		    return false;
		}
		}
	}
    var openDaysChange = function (){
    	if(getElementById('openDays').val()){
    	var r =/^\d+$/;
		var flag=r.test(getElementById('openDays').val());
		if(!flag){
			Joyin.error(JoyinUtil.getMessage("开放天数只能输入整数"));
			getElementById('openDays').val('');
		    return false;
		}
    	}
	}
    var purSpeedChange = function (){
    	if(getElementById('purSpeed').val()){
    	var r =/^\d+$/;
		var flag=r.test(getElementById('purSpeed').val());
		if(!flag){
			Joyin.error(JoyinUtil.getMessage("确认天数只能输入正整数"));
			getElementById('purSpeed').val('');
		    return false;
		}
    	}
    }

    var purSpeedRedChange = function (){
        if(getElementByRedId('redSpeed').val()){
            var r =/^\d+$/;
            var flag=r.test(getElementByRedId('redSpeed').val());
            if(!flag){
                Joyin.error(JoyinUtil.getMessage("确认天数只能输入正整数"));
                getElementByRedId('redSpeed').val('');
                return false;
            }else{
                if(parentPageId == 'FinNetValueProductBuild002' && $('#FinNetValueProductBuild002_profitType').val()
                                == '01' && getElementByRedId('redSpeed').val() == '0'){
                            getElementByRedId('redSpeed').val('1');
                }
            }
        }
    }
	//add by yujiaping 20210312 ZGBS-168 控制开放周期、开放天数、确认天数只能输入整数 end
	
	var weekChange = function (){
		var selectedValue = getElementById('dayOfWeek').val();
		
		if (selectedValue){
			getElementById('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
			getElementById('dayOfMonth').val('').prop('disabled', true).trigger('change');
			getElementById('workdayOfMonth').val('').prop('disabled', true).trigger('change');
			getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true);
            getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true);
		}
		//layui.form.render('radio');
	}



	var calcMethodChange = function (){
		var selectedValue = getElementById('calcMethod').val();
		if (selectedValue){
			//if (selectedValue == CALC_RULE_01){
			// CALC_RULE_01 这个值不知道为啥被置为null了。CALC_RULE_03仍然是对的。没找到原因 暂时这么修复
			if (selectedValue == '01'){
				getElementById('firstOpenDate').val('').prop('disabled', true);
			}
			else if (selectedValue == CALC_RULE_03 && parentPageId!='FinOpenPlanMaintenance002'){
				if($('#issueStatus').val() == '01' && pageModel == PAGE_MODEL_EDIT)
					getElementById('firstOpenDate').prop('disabled', true);
				else
				getElementById('firstOpenDate').prop('disabled', false);
			}
		}
		else{
			if (!finprodId && !workflowId){
				getElementById('firstOpenDate').val('')
			}
			getElementById('firstOpenDate').prop('disabled', true);
		}
	}
	
	//add by yejiejie 2019-2-12  start 开放计划中当申赎不同频的时候，赎回模块也需要对应的联动
	var openModeRedChange = function (){
		
		var selectedValue = getElementByRedId('openMode').val();
		// 定期开放或者未勾选
		if (!selectedValue || selectedValue == OPENMODE_01){
			getElementByRedId('openDays').prop('disabled', false);
			getElementByRedId('openCycle').prop('disabled', false);
			getElementByRedId('dayOfWeek').prop('disabled', false);
			getElementByRedId('radioDiv').find(':radio').prop('disabled', false);
			getElementByRedId('dayOfMonth').prop('disabled', false);
			getElementByRedId('workdayOfMonth').prop('disabled', false);
			getElementByRedId('openCycleUnit').trigger('change').prop('disabled', false);
			if(parentPageId !== 'FinOpenPlanMaintenance002'){
				getElementByRedId('calcMethod').prop('disabled', false);
			}
			//getElementById('firstOpenDate').prop('disabled', false);
			getElementByRedId('tradedayRule').trigger('change').prop('disabled', false);
			getElementByRedId('calcRule').prop('disabled', false);
			getElementByRedId('hdayOpenFlag').prop('disabled', false);
			getElementByRedId('openDaysType').prop('disabled', false).val(OPEN_DAYS_TYPE_01).trigger('change');
			getElementByRedId('tableDiv').show();
		}
		// 高频开放
		else if (selectedValue == OPENMODE_02){
			getElementByRedId('openCycle').prop('disabled', true).val('').trigger('change');
			getElementByRedId('openCycleUnit').prop('disabled', true).val('').trigger('change');
			getElementByRedId('dayOfWeek').prop('disabled', true).val('').trigger('change');
			getElementByRedId('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
			getElementByRedId('dayOfMonth').prop('disabled', true).val('').trigger('change');
			getElementByRedId('workdayOfMonth').prop('disabled', true).val('').trigger('change');
			getElementByRedId('tradedayRule').prop('disabled', true).val('').trigger('change');
//			getElementByRedId('calcRule').prop('disabled', true).val('').trigger('change');
			if(parentPageId !== 'FinOpenPlanMaintenance002'){
				getElementByRedId('calcMethod').prop('disabled', false);
			}
			getElementByRedId('openDaysType').prop('disabled', true).val('').trigger('change');
			getElementByRedId('openDays').prop('disabled', true).val('').trigger('change');
			//getElementById('firstOpenDate').prop('disabled', false);
			if ($('#' + openPlanDealMode).val() === DISPOSE_PATTERN_07) {
			    getElementByRedId('hdayOpenFlag').prop('checked', true);
			}
			tableRed.reload({ajax:null, url: null, data:[]});
			getElementByRedId('tableDiv').hide();
		}
		// 周期开放
		else if (selectedValue == OPENMODE_03){
			getElementByRedId('openDays').prop('disabled', false);
			getElementByRedId('openCycle').prop('disabled', false);
			if(pageModel === PAGE_MODEL_NEW){
				getElementByRedId('openCycleUnit').val(PERIOD_UNIT_01).trigger('change');
				getElementByRedId('tradedayRule').val(TDAY_RULE_02).trigger('change');
			}
			getElementByRedId('openCycleUnit').prop('disabled', false);
			getElementByRedId('tradedayRule').prop('disabled', false);
			getElementByRedId('calcRule').prop('disabled', false);
			if(parentPageId !== 'FinOpenPlanMaintenance002'){
				getElementByRedId('calcMethod').prop('disabled', false);
			}
			//getElementById('firstOpenDate').prop('disabled', false);
			getElementByRedId('hdayOpenFlag').prop('disabled', false);
			getElementByRedId('dayOfWeek').prop('disabled', true).val('').trigger('change');
			getElementByRedId('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
			getElementByRedId('dayOfMonth').prop('disabled', true).val('').trigger('change');
			getElementByRedId('workdayOfMonth').prop('disabled', true).val('').trigger('change');
			getElementByRedId('openDaysType').prop('disabled', false).val(OPEN_DAYS_TYPE_01).trigger('change');
			getElementByRedId('tableDiv').show();
		}
	}
	
	var openCycleUnitRedChange = function (){
		var selectedValue = getElementByRedId('openCycleUnit').val();
		var openMode = getElementByRedId('openMode').val();
		//BS-1977 start
		if(selectedValue==null){
			return;
		}
		//BS-1977 end
		if (openMode && openMode === OPENMODE_01){
			if (!selectedValue || selectedValue == PERIOD_UNIT_01){
				getElementByRedId('dayOfWeek').val('').prop('disabled', true).trigger('change');
				getElementByRedId('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
				getElementByRedId('dayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementByRedId('workdayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true);
				getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true);
			}
			else if (selectedValue == PERIOD_UNIT_02){
				getElementByRedId('dayOfWeek').prop('disabled', false);
				getElementByRedId('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
				getElementByRedId('dayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementByRedId('workdayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true);
                getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true);
			}
			else if (selectedValue == PERIOD_UNIT_03){
				getElementByRedId('dayOfWeek').val('').prop('disabled', true).trigger('change');
				getElementByRedId('radioDiv').find(':radio').prop('disabled', false);
				if(pageModel === PAGE_MODEL_NEW || getElementByRedId('radioDiv').find(':radio:checked').length === 0) {
					getElementByRedId('radioDiv').find(':radio[value="0"]').prop('checked', true);
				}
				getElementByRedId('dayOfMonth').val('').prop('disabled', false).trigger('change');
				getElementByRedId('workdayOfMonth').val('').prop('disabled', true).trigger('change');
				getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true);
	            getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true);
			}
			else if (selectedValue == PERIOD_UNIT_04){
			    getElementByRedId('dayOfWeek').val('').prop('disabled', true).trigger('change');
			    getElementByRedId('radioDiv').find(':radio').prop('disabled', false);
			    if(pageModel === PAGE_MODEL_NEW || getElementByRedId('radioDiv').find(':radio:checked').length === 0) {
			        getElementByRedId('radioDiv').find(':radio[value="0"]').prop('checked', true);
			        getElementByRedId('radioDiv').find(':radio[value="2"]').prop('disabled', true);
			    }
			    getElementByRedId('dayOfMonth').val('').prop('disabled', false).trigger('change');
			    getElementByRedId('workdayOfMonth').val('').prop('disabled', true).trigger('change');
			    getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true);
			    getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true);
			}
		}else{
			getElementByRedId('dayOfWeek').val('').prop('disabled', true).trigger('change');
			getElementByRedId('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
			getElementByRedId('dayOfMonth').val('').prop('disabled', true).trigger('change');
			getElementByRedId('workdayOfMonth').val('').prop('disabled', true).trigger('change');
			getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true);
            getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true);
		}
		layui.form.render('radio');
	}
	
	var weekRedChange = function (){
		var selectedValue = getElementByRedId('dayOfWeek').val();
		
		if (selectedValue){
			getElementByRedId('radioDiv').find(':radio').prop('disabled', true).prop('checked', false);
			getElementByRedId('dayOfMonth').val('').prop('disabled', true).trigger('change');
			getElementByRedId('workdayOfMonth').val('').prop('disabled', true).trigger('change');
		}
		//layui.form.render('radio');
	}
	
	var calcMethodRedChange = function (){
		var selectedValue = getElementByRedId('calcMethod').val();
		
		if (selectedValue){
			if (selectedValue == CALC_RULE_01){
				getElementByRedId('firstOpenDate').val('').prop('disabled', true);
			}
			else if (selectedValue == CALC_RULE_03 && parentPageId!='FinOpenPlanMaintenance002'){
				if($('#issueStatus').val() == '01' && pageModel == PAGE_MODEL_EDIT)
					getElementByRedId('firstOpenDate').prop('disabled', true);
				else
				getElementByRedId('firstOpenDate').prop('disabled', false);
			}
		}
		else{
			if (!finprodId && !workflowId){
				getElementByRedId('firstOpenDate').val('')
			}
			getElementByRedId('firstOpenDate').prop('disabled', true);
		}
	}
	//add by yejiejie 2019-2-12  end 开放计划中当申赎不同频的时候，赎回模块也需要对应的联动
	
	var parentOpenModeChange = function (){
        var selectedValue = $('#' + operationType).val();
     
        if (selectedValue){
            if (selectedValue == ISSUE_MODE_02){
                getElementById('div').show();
				if (parentPageId == 'FinPlanNetAsset002' || parentPageId == 'FinPlanProfitAsset002'|| parentPageId == 'FinPlanAssetC002'){
					$('#div1').hide();
					$('#span1').html("赎回");
				}
            }
            else {
                getElementById('div').hide();
            }
        }
        else{
            getElementById('div').hide();
        }
        
        setTradedayRuleStatus();
	    setCalcMethodStatus();
    }	
	var parentDealModeChange = function () {
		var selectedValue = $('#' + openPlanDealMode).val();
		var operationTypeValue = $('#' + operationType).val();
		if (selectedValue) {			
		    first = false;
			// 非普通
			/*if (selectedValue == DISPOSE_PATTERN_01) {
				//getElementById('div').hide();
			}
			else{*/
			    if (operationTypeValue == ISSUE_MODE_02) {
    				getElementById('div').show();
    				if (selectedValue == DISPOSE_PATTERN_05 || selectedValue == DISPOSE_PATTERN_06 || selectedValue == DISPOSE_PATTERN_07){
    					getElementById('openMode').val(OPENMODE_02).trigger('change').prop('disabled', true);
    					if (selectedValue == DISPOSE_PATTERN_06){
    						getElementById('hdayOpenFlag').next().removeClass('layui-form-onswitch').trigger('change');
    	                    getElementById('hdayOpenFlag').next().find("em").text('否');
    	                    getElementById('hdayOpenFlag').prop('disabled', true);
    					}
    					if (selectedValue == DISPOSE_PATTERN_07){
    						getElementById('hdayOpenFlag').next().addClass('layui-form-onswitch').trigger('change');
    	                    getElementById('hdayOpenFlag').next().find("em").text('是');
    	                    getElementById('hdayOpenFlag').prop('disabled', true);
    					}
    				}else{
    	            	/*if (selectedValue == DISPOSE_PATTERN_03) {*/
    	            		getElementById('openMode').val(OPENMODE_01).trigger('change');
    	            	/*}*/
    	            	getElementById('openMode').prop('disabled', false);
    	            }
			    } else {
			        getElementById('div').hide();
			    }
			//}
		} else {
			getElementById('div').hide();
		}
	}
	
	var openPlanInitAction = function(input_finprodId){
		if (!table) {
			return;
		}
		Joyin.ajax(JoyinUtil.getRealPath('component/prod/BussFinProductOpenPlanCommonRate/queryOpenPlanInfoByFinprodId'), {finprodId : input_finprodId}, function(data){
			if(data.length==1){
			    FinProductCommon.initPageDataAction(bussFinProductOpenPlanDto, data[0]);
				// 当进行开放规则调整的时候置灰推算方法和首次开放日
				if (parentPageId === 'FinOpenPlanMaintenance002'){
			        getElementById('firstOpenDate').prop('disabled', true);
					getElementById('calcMethod').prop('disabled', true).val('03').trigger('change');
					
					getElementByRedId('firstOpenDate').prop('disabled', true);
					getElementByRedId('calcMethod').prop('disabled', true);
				}
    		}else if(data.length>1){
    			$("#red_div").show();
				getElementById('openType').val('01');
                if(getElementById('sameFLag').next().find("em").text()==="是"){
				    getElementById('sameFLag').next().trigger("click");
			    }
				layui.form.render('checkbox');
    			for(var i=0;i<data.length;i++){
    				if(data[i].openType=="01")FinProductCommon.initPageDataAction(bussFinProductOpenPlanDto, data[i]);
    				if(data[i].openType=="02")FinProductCommon.initPageDataAction(bussFinProductOpenPlanRedDto, data[i]);
    			}
				// 当进行开放规则调整的时候置灰推算方法和首次开放日
				if (parentPageId === 'FinOpenPlanMaintenance002'){
			        getElementById('firstOpenDate').prop('disabled', true);
					getElementById('calcMethod').prop('disabled', true).val('03').trigger('change');
					
					getElementByRedId('firstOpenDate').prop('disabled', true);
					getElementByRedId('calcMethod').prop('disabled', true).val('03').trigger('change');
				}
    		}
			if (parentPageId == 'FinProductBuild002'||parentPageId === 'FinNetValueProductBuild002'||parentPageId === 'FinOpenPlanMaintenance002' || parentPageId === 'FinPlanNetAsset002' || parentPageId === 'FinPlanProfitAsset002' || parentPageId === 'FinPlanAssetC002'){
		    		openPlanFinprodId = input_finprodId;
		    		table.table ? table.reload() : table.load();
		    		tableRed.table ? tableRed.reload() : tableRed.load();
					//清空日期值 chenxiaoqiu 20191126
					if(isDisplayDate == NO){
						tableRed.clear();
						table.clear();
					}
				}

	    	
    	})
    	
	}
	
	var openPlanInitActionByTmpl = function(bussFinProductOpenPlanDto){

    	FinProductCommon.initPageDataAction('bussFinProductOpenPlanDto', bussFinProductOpenPlanDto);
    	
    	if(bussFinProductOpenPlanDto.openType === '03') {
            if(getElementById('sameFLag').next().find("em").text()==="否"){
				getElementById('sameFLag').next().trigger("click");
			}
    	}
    	else {
            if(getElementById('sameFLag').next().find("em").text()==="是"){
				getElementById('sameFLag').next().trigger("click");
			}
    	}
	}
	
	var chkOpenPlan=function(){
		//开放式校验
		if($("#"+operationType).val()=="02"){
			if(getElementById('openMode').val()!='02'){
		if(openMode1&&openMode1!=getElementById('openMode').val())
		{	Joyin.error(JoyinUtil.getMessage("开放模式已经改变,请重新点击计划生成"));
		    return false;
		}
		if(openDays1&&openDays1!=getElementById('openDays').val()) {	
			Joyin.error(JoyinUtil.getMessage("开放天数已经改变,请重新点击计划生成"));
		    return false;
		}
		if(openCycle1&&openCycle1!=getElementById('openCycle').val()) {	
			Joyin.error(JoyinUtil.getMessage("开放周期已经改变,请重新点击计划生成"));
		    return false;
		}
		if(openCycleUnit1&&openCycleUnit1!=getElementById('openCycleUnit').val()){	
			Joyin.error(JoyinUtil.getMessage("开放周期单位已经改变,请重新点击计划生成"));
		    return false;
		}

		if(dayOfWeek1!=null&&dayOfWeek1.length>0 && getElementById('dayOfWeek').val()!=null && dayOfWeek1.toString() != getElementById('dayOfWeek').val().toString() ){
			Joyin.error(JoyinUtil.getMessage("周已经改变,请重新点击计划生成"));
		    return false;
		}
		
		if(dayOfMonth1!=null&&dayOfMonth1.length>0 &&  getElementById('dayOfMonth').val()!=null && dayOfMonth1.toString() !=getElementById('dayOfMonth').val().toString() ){
			Joyin.error(JoyinUtil.getMessage("几号已经改变,请重新点击计划生成"));
		    return false;
		}
		if(workdayOfMonth1!=null&&workdayOfMonth1.length>0 && getElementById('workdayOfMonth').val()!=null  &&workdayOfMonth1.toString()!=getElementById('workdayOfMonth').val().toString()){
			Joyin.error(JoyinUtil.getMessage("第几工作日已经改变,请重新点击计划生成"));
		    return false;
		}
		if(calcMethod1&&calcMethod1!=getElementById('calcMethod').val()){	
			Joyin.error(JoyinUtil.getMessage("推算方法已经改变,请重新点击计划生成"));
		    return false;
		}
		if(firstOpenDate1&&firstOpenDate1!=getElementById('firstOpenDate').val()){	
			Joyin.error(JoyinUtil.getMessage("首次开放日已经改变,请重新点击计划生成"));
		    return false;
		}
		if(tradedayRule1&&tradedayRule1!=getElementById('tradedayRule').val()){	
			Joyin.error(JoyinUtil.getMessage("营业日准则已经改变,请重新点击计划生成"));
		    return false;
		}
		if(calcRule1&&calcRule1!=getElementById('calcRule').val()){	
			Joyin.error(JoyinUtil.getMessage("推算规则已经改变,请重新点击计划生成"));
		    return false;
		}
		if(purSpeed1&&purSpeed1!=getElementById('Speed').val()){	
			Joyin.error(JoyinUtil.getMessage("申购确认天数已经改变,请重新点击计划生成"));
		    return false;
		}
		if(cpriceRule1&&cpriceRule1!=getElementById('cpriceRule').val()){	
			Joyin.error(JoyinUtil.getMessage("申购确认天数已经改变,请重新点击计划生成"));
		    return false;
		}
		if(vdate2&&vdate2!=$('#' + vdate1).val()){	
			Joyin.error(JoyinUtil.getMessage("起息日已经改变,请重新点击开放计划生成"));
		    return false;
		}
		if(mdate2&&mdate2!=$('#' + mdate1).val()){	
			Joyin.error(JoyinUtil.getMessage("到期日已经改变,请重新点击开放计划生成"));
		    return false;
		}
		if(tradCalender1&&tradCalender1!=$('#' + tradCalender).val()){	
			Joyin.error(JoyinUtil.getMessage("交易日历已经改变,请重新点击计划生成"));
		    return false;
		}
		if(raiseStrDate1&&raiseStrDate1!=$('#' + raiseStrDate).val()){	
			Joyin.error(JoyinUtil.getMessage("募集开始日期已经改变,请重新点击计划生成"));
		    return false;
		}
		if(raiseEndDate1&&raiseEndDate1!=$('#' + raiseEndDate).val()){	
			Joyin.error(JoyinUtil.getMessage("募集结束日期已经改变,请重新点击计划生成"));
		    return false;
		}
		}
		}
	    return true;	
	}
	var chkRedOpenPlan=function(){
		//开放式校验
		if($("#"+operationType).val()=="02"&&getElementById('sameFLag').next().find("em").text()!="是"){
			if(getElementByRedId('openMode').val()!='02'){
				if(openModeRed1&&openModeRed1!=getElementByRedId('openMode').val()){	
					Joyin.error(JoyinUtil.getMessage("赎回开放模式已经改变,请重新点击计划生成"));
					return false;
				}
				if(openDaysRed1&&openDaysRed1!=getElementByRedId('openDays').val()) {	
					Joyin.error(JoyinUtil.getMessage("赎回开放天数已经改变,请重新点击计划生成"));
					return false;
				}
				if(openCycleRed1&&openCycleRed1!=getElementByRedId('openCycle').val()) {	
					Joyin.error(JoyinUtil.getMessage("赎回开放周期已经改变,请重新点击计划生成"));
					return false;
				}
				if(openCycleUnitRed1&&openCycleUnitRed1!=getElementByRedId('openCycleUnit').val()){	
					Joyin.error(JoyinUtil.getMessage("赎回开放周期单位已经改变,请重新点击计划生成"));
					return false;
				}
				if(calcMethodRed1&&calcMethodRed1!=getElementByRedId('calcMethod').val()){	
					Joyin.error(JoyinUtil.getMessage("赎回推算方法已经改变,请重新点击计划生成"));
					return false;
				}
				if(firstOpenDateRed1&&firstOpenDateRed1!=getElementByRedId('firstOpenDate').val()){	
					Joyin.error(JoyinUtil.getMessage("赎回首次开放日已经改变,请重新点击计划生成"));
					return false;
				}
				if(tradedayRuleRed1&&tradedayRuleRed1!=getElementByRedId('tradedayRule').val()){	
					Joyin.error(JoyinUtil.getMessage("赎回营业日准则已经改变,请重新点击计划生成"));
					return false;
				}
				if(calcRuleRed1&&calcRuleRed1!=getElementByRedId('calcRule').val()){	
					Joyin.error(JoyinUtil.getMessage("赎回推算规则已经改变,请重新点击计划生成"));
					return false;
				}
				if(purSpeedRed1&&purSpeedRed1!=getElementByRedId('redSpeed').val()){	
					Joyin.error(JoyinUtil.getMessage("赎回确认天数已经改变,请重新点击计划生成"));
					return false;
				}
				if(cpriceRuleRed1&&cpriceRuleRed1!=getElementByRedId('cpriceRule').val()){	
					Joyin.error(JoyinUtil.getMessage("赎回净值日准则已经改变,请重新点击计划生成"));
					return false;
				}
				if(vdate2&&vdate2!=$('#' + vdate1).val()){	
					Joyin.error(JoyinUtil.getMessage("起息日已经改变,请重新点击开放计划生成"));
					return false;
				}
				if(mdate2&&mdate2!=$('#' + mdate1).val()){	
					Joyin.error(JoyinUtil.getMessage("到期日已经改变,请重新点击赎回开放计划生成"));
					return false;
				}
				if(tradCalender1&&tradCalender1!=$('#' + tradCalender).val()){	
					Joyin.error(JoyinUtil.getMessage("交易日历已经改变,请重新点击计划生成"));
					return false;
				}
				if(raiseStrDate1&&raiseStrDate1!=$('#' + raiseStrDate).val()){	
					Joyin.error(JoyinUtil.getMessage("募集开始日期已经改变,请重新点击计划生成"));
					return false;
				}
				if(raiseEndDate1&&raiseEndDate1!=$('#' + raiseEndDate).val()){	
					Joyin.error(JoyinUtil.getMessage("募集结束日期已经改变,请重新点击计划生成"));
					return false;
				}
				if(dayOfWeekRed1!=null&&dayOfWeekRed1.length>0 && getElementByRedId('dayOfWeek').val()!=null &&dayOfWeekRed1.toString()!=getElementByRedId('dayOfWeek').val().toString()){
					Joyin.error(JoyinUtil.getMessage("周已经改变,请重新点击计划生成"));
				    return false;
				}
				
				if(dayOfMonthRed1!=null&&dayOfMonthRed1.length>0 && getElementByRedId('dayOfMonth').val()!=null &&dayOfMonthRed1.toString()!=getElementByRedId('dayOfMonth').val().toString()){
					Joyin.error(JoyinUtil.getMessage("几号已经改变,请重新点击计划生成"));
				    return false;
				}
				if(workdayOfMonthRed1!=null&&workdayOfMonthRed1.length>0 && getElementByRedId('workdayOfMonth').val()!=null &&workdayOfMonthRed1.toString()!=getElementByRedId('workdayOfMonth').val().toString()){
					Joyin.error(JoyinUtil.getMessage("第几工作日已经改变,请重新点击计划生成"));
				    return false;
				}
			}
		}
	    return true;	
	}
	var chkClosePlan=function(){
		//封闭式校验
		if($("#"+operationType).val()=="01"){
			/*if(openMode1&&openMode1!=getElementById('openMode').val())
			{
				Joyin.error(JoyinUtil.getMessage("开放模式已经改变,请重新点击计划生成"));
			    return false;
			}
			if(openDays1&&openDays1!=getElementById('openDays').val()) {	
				Joyin.error(JoyinUtil.getMessage("开放天数已经改变,请重新点击计划生成"));
			    return false;
			}
			if(openCycle1&&openCycle1!=getElementById('openCycle').val()) {	
				Joyin.error(JoyinUtil.getMessage("开放周期已经改变,请重新点击计划生成"));
			    return false;
			}
			if(openCycleUnit1&&openCycleUnit1!=getElementById('openCycleUnit').val()){	
				Joyin.error(JoyinUtil.getMessage("开放周期单位已经改变,请重新点击计划生成"));
			    return false;
			}*/
			/*
			if(dayOfWeek1&&dayOfWeek1!=getElementById('dayOfWeek').val()){	
				Joyin.error(JoyinUtil.getMessage("周已经改变,请重新点击计划生成"));
			    return false;
			}
			
			if(dayOfMonth1&&dayOfMonth1!=getElementById('dayOfMonth').val()){	
				Joyin.error(JoyinUtil.getMessage("几号已经改变,请重新点击计划生成"));
			    return false;
			}
			if(workdayOfMonth1&&workdayOfMonth1!=getElementById('workdayOfMonth').val()){	
				Joyin.error(JoyinUtil.getMessage("第几工作日已经改变,请重新点击计划生成"));
			    return false;
			}*/
			/*if(calcMethod1&&calcMethod1!=getElementById('calcMethod').val()){	
				Joyin.error(JoyinUtil.getMessage("推算方法已经改变,请重新点击计划生成"));
			    return false;
			}
			if(firstOpenDate1&&firstOpenDate1!=getElementById('firstOpenDate').val()){	
				Joyin.error(JoyinUtil.getMessage("首次开放日已经改变,请重新点击计划生成"));
			    return false;
			}
			if(tradedayRule1&&tradedayRule1!=getElementById('tradedayRule').val()){	
				Joyin.error(JoyinUtil.getMessage("营业日准则已经改变,请重新点击计划生成"));
			    return false;
			}
			if(calcRule1&&calcRule1!=getElementById('calcRule').val()){	
				Joyin.error(JoyinUtil.getMessage("推算规则已经改变,请重新点击计划生成"));
			    return false;
			}
			if(purSpeed1&&purSpeed1!=getElementById('Speed').val()){	
				Joyin.error(JoyinUtil.getMessage("申购确认天数已经改变,请重新点击计划生成"));
			    return false;
			}
			if(cpriceRule1&&cpriceRule1!=getElementById('cpriceRule').val()){	
				Joyin.error(JoyinUtil.getMessage("申购确认天数已经改变,请重新点击计划生成"));
			    return false;
			}*/
			if(firstPayDate1&&firstPayDate1!=$("#bussFinProductPayPlanDto_firstPayDate").val()){	
				Joyin.error(JoyinUtil.getMessage("首次付息日已经改变,请重新点击付息计划生成"));
			    return false;
			}
			if(payCycle1&&payCycle1!= $("#bussFinProductPayPlanDto_payCycle").val()){	
				Joyin.error(JoyinUtil.getMessage("付息规则已经改变,请重新点击付息计划生成"));
			    return false;
			}
			if(payCycleVal1&&payCycleVal1!=$("#bussFinProductPayPlanDto_payCycleVal").val()){	
				Joyin.error(JoyinUtil.getMessage("付息周期已经改变,请重新点击付息计划生成"));
			    return false;
			}
		   if(payCycleUnit1&&payCycleUnit1!=$('#bussFinProductPayPlanDto_payCycleUnit').val()){	
				Joyin.error(JoyinUtil.getMessage("付息周期已经改变,请重新点击付息计划生成"));
			    return false;
			}
		/*   if(calcRule2&&calcRule2!=$('#bussFinProductPayPlanDto_calcRule').val()){	
				Joyin.error(JoyinUtil.getMessage("推算方法已经改变,请重新点击付息计划生成"));
			    return false;
			}*/
			if(endTdayRule1&&endTdayRule1!=$('#bussFinProductPayPlanDto_endTdayRule').val()){	
				Joyin.error(JoyinUtil.getMessage("计息营业日准则已经改变,请重新点击计划生成"));
			    return false;
			}
			if(payTdayRule1&&payTdayRule1!=$('#bussFinProductPayPlanDto_payTdayRule').val()){	
				Joyin.error(JoyinUtil.getMessage("付息营业日准则已经改变,请重新点击计划生成"));
			    return false;
			}
			if(vdate3&&vdate3!=$('#' + vdate1).val()){	
				Joyin.error(JoyinUtil.getMessage("起息日已经改变,请重新点击计划生成"));
			    return false;
			}
			if(mdate3&&mdate3!=$('#' + mdate1).val()){	
				Joyin.error(JoyinUtil.getMessage("到期日已经改变,请重新点击计划生成"));
			    return false;
			}
			/*if(tradCalender1&&tradCalender1!=$('#' + tradCalender).val()){	
				Joyin.error(JoyinUtil.getMessage("交易日历已经改变,请重新点击计划生成"));
			    return false;
			}
			if(raiseStrDate1&&raiseStrDate1!=$('#' + raiseStrDate).val()){	
				Joyin.error(JoyinUtil.getMessage("募集开始日期已经改变,请重新点击计划生成"));
			    return false;
			}
			if(raiseEndDate1&&raiseEndDate1!=$('#' + raiseEndDate).val()){	
				Joyin.error(JoyinUtil.getMessage("募集结束日期已经改变,请重新点击计划生成"));
			    return false;
			}*/
		
		}/*else if($("#"+operationType).val()=="01"){
			if(getElementById('openMode').val()!='02'){
			if(vdate2&&vdate2!=$('#' + vdate1).val()){	
				Joyin.error(JoyinUtil.getMessage("起息日已经改变,请重新点击计划生成"));
			    return false;
			}
			if(mdate2&&mdate2!=$('#' + mdate1).val()){	
				Joyin.error(JoyinUtil.getMessage("到期日已经改变,请重新点击计划生成"));
			    return false;
			}
		 }
		}*/
	    return true;	
	}
   //add by dingjunfeng begin
	var chkPayPlan=function(){
		//开放式校验
		if($("#"+operationType).val()=="02"){
			if(getElementById('openMode').val()!='02'){
		/*if(openMode1&&openMode1!=getElementById('openMode').val())
		{	Joyin.error(JoyinUtil.getMessage("开放模式已经改变,请重新点击计划生成"));
		    return false;
		}
		if(openDays1&&openDays1!=getElementById('openDays').val()) {	
			Joyin.error(JoyinUtil.getMessage("开放天数已经改变,请重新点击计划生成"));
		    return false;
		}
		if(openCycle1&&openCycle1!=getElementById('openCycle').val()) {	
			Joyin.error(JoyinUtil.getMessage("开放周期已经改变,请重新点击计划生成"));
		    return false;
		}
		if(openCycleUnit1&&openCycleUnit1!=getElementById('openCycleUnit').val()){	
			Joyin.error(JoyinUtil.getMessage("开放周期单位已经改变,请重新点击计划生成"));
		    return false;
		}*/
		
		if(firstPayDate1&&firstPayDate1!=$("#bussFinProductPayPlanDto_firstPayDate").val()){	
			Joyin.error(JoyinUtil.getMessage("首次付息日已经改变,请重新点击付息计划生成"));
		    return false;
		}
		if(payCycle1&&payCycle1!= $("#bussFinProductPayPlanDto_payCycle").val()){	
			Joyin.error(JoyinUtil.getMessage("付息规则已经改变,请重新点击付息计划生成"));
		    return false;
		}
		if(payCycleVal1&&payCycleVal1!=$("#bussFinProductPayPlanDto_payCycleVal").val()){	
			Joyin.error(JoyinUtil.getMessage("付息周期已经改变,请重新点击付息计划生成"));
		    return false;
		}
	   if(payCycleUnit1&&payCycleUnit1!=$('#bussFinProductPayPlanDto_payCycleUnit').val()){	
			Joyin.error(JoyinUtil.getMessage("付息周期已经改变,请重新点击付息计划生成"));
		    return false;
		}
	/*   if(calcRule2&&calcRule2!=$('#bussFinProductPayPlanDto_calcRule').val()){	
			Joyin.error(JoyinUtil.getMessage("推算方法已经改变,请重新点击付息计划生成"));
		    return false;
		}*/
		if(endTdayRule1&&endTdayRule1!=$('#bussFinProductPayPlanDto_endTdayRule').val()){	
			Joyin.error(JoyinUtil.getMessage("计息营业日准则已经改变,请重新点击计划生成"));
		    return false;
		}
		if(payTdayRule1&&payTdayRule1!=$('#bussFinProductPayPlanDto_payTdayRule').val()){	
			Joyin.error(JoyinUtil.getMessage("付息营业日准则已经改变,请重新点击计划生成"));
		    return false;
		}
		/*if(purSpeed1&&purSpeed1!=getElementById('Speed').val()){	
			Joyin.error(JoyinUtil.getMessage("申购确认天数已经改变,请重新点击计划生成"));
		    return false;
		}
		if(cpriceRule1&&cpriceRule1!=getElementById('cpriceRule').val()){	
			Joyin.error(JoyinUtil.getMessage("申购确认天数已经改变,请重新点击计划生成"));
		    return false;
		}*/
		if(vdate3&&vdate3!=$('#' + vdate1).val()){	
			Joyin.error(JoyinUtil.getMessage("起息日已经改变,请重新点击付息计划生成"));
		    return false;
		}
		if(mdate3&&mdate3!=$('#' + mdate1).val()){	
			Joyin.error(JoyinUtil.getMessage("到期日已经改变,请重新点击付息计划生成"));
		    return false;
		}
/*		if(tradCalender1&&tradCalender1!=$('#' + tradCalender).val()){	
			Joyin.error(JoyinUtil.getMessage("交易日历已经改变,请重新点击计划生成"));
		    return false;
		}
		if(raiseStrDate1&&raiseStrDate1!=$('#' + raiseStrDate).val()){	
			Joyin.error(JoyinUtil.getMessage("募集开始日期已经改变,请重新点击计划生成"));
		    return false;
		}
		if(raiseEndDate1&&raiseEndDate1!=$('#' + raiseEndDate).val()){	
			Joyin.error(JoyinUtil.getMessage("募集结束日期已经改变,请重新点击计划生成"));
		    return false;
		}*/
		}
		}
	    return true;	
	}
	
	var getOpenModeValue = function(){
		return getElementById('openMode').val();
	}
	
	window.BussFinProductOpenPlan = {
		openPlanInitAction : openPlanInitAction,
		openPlanInitActionByTmpl : openPlanInitActionByTmpl,
		chkOpenPlan:chkOpenPlan,
		chkClosePlan:chkClosePlan,
		chkPayPlan:chkPayPlan,
		chkRedOpenPlan:chkRedOpenPlan,
		getOpenModeValue:getOpenModeValue,
		getElementById:getElementById
	}
	//判断页面是否加载完成
	var isLoaded = function() {
    	var select2 = getElementById('calcRule').data('select2');
    	return  !!select2;
    };
	var periodChange = function (){
		if($('#FinNetValueProductBuild002_dealMode').val() === '10') {
        	$('#div1').hide();
        	if(isLoaded()){
            	$('#FinNetValueProductBuild002_period').val('').trigger('change').prop('disabled', false);
        	}
        	else{
            	$('#FinNetValueProductBuild002_period').trigger('change').prop('disabled', false);
        	}
        	//BS-2042  草稿箱功能，净值型产品产品处理模式为单一固定期限，选择开放模式为高频开放，使用草稿时，出现了赎回计划（错误）） start
			getElementById('openType').val('01');
			if(getElementById('sameFLag').next().find("em").text()==="是"){
				getElementById('sameFLag').next().trigger("click");
			}
			//BS-2042  草稿箱功能，净值型产品产品处理模式为单一固定期限，选择开放模式为高频开放，使用草稿时，出现了赎回计划（错误）） end
        } 
        else {
			if (parentPageId == 'FinPlanNetAsset002' || parentPageId == 'FinPlanProfitAsset002'|| parentPageId == 'FinPlanAssetC002'){
				$('#div1').hide();
			}else{
				$('#div1').show();
			}
        	$('#cfDiv').hide();
        	if(isLoaded()){
            	$('#FinNetValueProductBuild002_period').val('').trigger('change').prop('disabled', false);
        	}
        	else{
            	$('#FinNetValueProductBuild002_period').trigger('change').prop('disabled', false);
        	}
        	//BS-2042  草稿箱功能，净值型产品产品处理模式为单一固定期限，选择开放模式为高频开放，使用草稿时，出现了赎回计划（错误）） start
			getElementById('openType').val('03');
			if(getElementById('sameFLag').next().find("em").text()==="否"){
				getElementById('sameFLag').next().trigger("click");
			}
			//BS-2042  草稿箱功能，净值型产品产品处理模式为单一固定期限，选择开放模式为高频开放，使用草稿时，出现了赎回计划（错误）） end
        }
    }
	var profitTypeChangeEx = function () {
	    var selectedValueProfitType = $('#' + profitType).val();
        if (selectedValueProfitType == '02' || selectedValueProfitType == '03') {
            getElementById('cpriceRuleDiv').hide();// 隐藏净值日准则
            getElementByRedId('cpriceRuleDiv').hide();// 隐藏净值日准则
        } else {
            getElementById('cpriceRuleDiv').show();// 显示净值日准则
            getElementByRedId('cpriceRuleDiv').show();// 显示净值日准则
        }
        table.showColumns([{field:'openNdate', visible: PROFIT_TYPE_01 === selectedValueProfitType}]);
        tableRed.showColumns([{field:'openNdate', visible: PROFIT_TYPE_01 === selectedValueProfitType}]);
        
	    setTradedayRuleStatus();
	    setCalcMethodStatus();
    }
	 /*设置推营业日准则*/
	  var setTradedayRuleStatus = function() {
		var selectedValue = $('#' + operationType).val();
		if (selectedValue == '02' && $('#' + profitType).val() == '03') {// 开放式&&货币型
			getElementById('tradedayRule').val('02').prop('disabled', true).trigger('change');
			getElementByRedId('tradedayRule').val('02').prop('disabled', true).trigger('change');
		} else {
			getElementById('tradedayRule').prop('disabled', false);
			getElementByRedId('tradedayRule').prop('disabled', false);
		}
	  }
	  /*设置推算方法*/
	  var setCalcMethodStatus = function() {
		var selectedValue = $('#' + operationType).val();
		if (selectedValue == '02' && $('#' + profitType).val() == '03') {// 开放式&&货币型
			 getElementById('calcMethod').val(CALC_RULE_01).trigger('change');
			 getElementByRedId('calcMethod').val(CALC_RULE_01).trigger('change');
		} 
	  }
	var sameFlagChange=function(){
		/*
		if(pageModel == PAGE_MODEL_NEW ){
			getElementByRedId("openMode").val('');
			getElementByRedId("openDays").val('');
			getElementByRedId("openCycle").val('');
			getElementByRedId("calcMethod").val('');
			getElementByRedId("firstOpenDate").val('');
			getElementByRedId("cpriceRule").val('');
			getElementByRedId("tradedayRule").val('');
			getElementByRedId("redSpeed").val('');
			if(tableRed.table)
			tableRed.clear();
		}*/
	   //周期型产品不属于申赎同频，也不存在赎回计划
	   if(getElementById('sameFLag').next().find("em").text()==="是"||$('#FinNetValueProductBuild002_dealMode').val() === '10'){
			$("#red_div").hide();
			if(tableRed.table)
			tableRed.clear();
            if($('#FinNetValueProductBuild002_dealMode').val() != '10'){
    			getElementById('openType').val('03');
    			getElementByRedId("openMode").val('');
    			getElementByRedId("openDays").val('');
    			getElementByRedId("openCycle").val('');
    			getElementByRedId("calcMethod").val('');
    			getElementByRedId("firstOpenDate").val('');
    			getElementByRedId("cpriceRule").val('');
    			getElementByRedId("tradedayRule").val('');
    			getElementByRedId("redSpeed").val('');
                if(getElementById('sameFLag').next().find("em").text()==="否"){
    				getElementById('sameFLag').next().trigger("click");
    			}
            }
		}else{
			$("#red_div").show();
			getElementById('openType').val('01');
            if(getElementById('sameFLag').next().find("em").text()==="是"){
				getElementById('sameFLag').next().trigger("click");
			}
			
		}
		if(pageModel != PAGE_MODEL_NEW ){
			if($('#FinNetValueProductBuild002_dealMode').val() === '10'){
				$("#red_div").hide();
			}
			else if(getElementById('sameFLag').prop("checked")){
					
			$("#red_div").hide();
			getElementById('openType').val('03');
			
			}else{
				$("#red_div").show();
				getElementById('openType').val('01');
			}
		}
		
	}
	/**
	 * 开天数类型check
	 */
	var openDaysTypeChange =  function(){
	    if(getElementById('openDaysType').val() === OPEN_DAYS_TYPE_01){
	    	//BS-2045 高频开放开放天数置灰  start
	    	if(getElementById('openMode').val()!= OPENMODE_02){
	    		getElementById('openDays').trigger('change').prop('disabled', false);
	    	}
	    	//BS-2045 高频开放开放天数置灰  end
	    } else if(getElementById('openDaysType').val() === OPEN_DAYS_TYPE_02){
	        getElementById('openDays').val('').prop('disabled', true);
	        getElementById('calcRule').val('').trigger('change');
	    }
	}
	/**
	 * 开天数类型check
	 */
	var openDaysTypeRedChange =  function(){
	    if(getElementByRedId('openDaysType').val() === OPEN_DAYS_TYPE_01){
	    	//BS-2045 高频开放开放天数置灰  start
	    	if(getElementByRedId('openMode').val()!= OPENMODE_02){
	    		getElementByRedId('openDays').trigger('change').prop('disabled', false);
	    	}
	    	//BS-2045 高频开放开放天数置灰  end
        } else if(getElementByRedId('openDaysType').val() === OPEN_DAYS_TYPE_02){
            getElementByRedId('openDays').val('').prop('disabled', true);
            getElementByRedId('calcRule').val('').trigger('change');
        }
	}
	var calcRuleRedChange = function(){
	    if(getElementByRedId('openDaysType').val() === OPEN_DAYS_TYPE_02 && getElementByRedId('calcRule').val() === CALC_METHOD_02){
	        Joyin.error(JoyinUtil.getMessage("FIN_PROD_E_0126"));
	        getElementByRedId('calcRule').val('');
	        return;
	    }
		if (pageModel != PAGE_MODEL_REFERENCE) {
			if (getElementByRedId('calcRule').val() === CALC_METHOD_01) {
				getElementByRedId('tradedayRule').val(TDAY_RULE_02).trigger('change').prop('disabled', true);
			} else {
				//TISHKBS-1393 start
				getElementByRedId('tradedayRule').prop('disabled', false);
				//getElementByRedId('tradedayRule').val('').trigger('change').prop('disabled', false);
				//TISHKBS-1393 end
			}
		}
	}
	var calcRuleChange = function(){
	//debugger;
	    if(getElementById('openDaysType').val() === OPEN_DAYS_TYPE_02 && getElementById('calcRule').val() === CALC_METHOD_02){
	        Joyin.error(JoyinUtil.getMessage("FIN_PROD_E_0126"));
	        getElementById('calcRule').val('');
	        return;
	    }
		if (pageModel != PAGE_MODEL_REFERENCE) {
			if (getElementById('calcRule').val() === CALC_METHOD_01) {
				if (pageModel != PAGE_MODEL_REFERENCE) {
					getElementById('tradedayRule').val(TDAY_RULE_02).trigger('change').prop('disabled', true);
				}
			} else {
				//TISHKBS-1393 start
//        	getElementById('tradedayRule').val(TDAY_RULE_02).trigger('change').prop('disabled', false);
				getElementById('tradedayRule').prop('disabled', false);
				//TISHKBS-1393 end
			}
		}
	}
	
	var preview = function(){
		if(table!=null && table!=''){
			var selArr = table.getAllRows();
            if (!selArr.length == 0) {
           	var selectedItem = [];
                _.each(selArr,function (sel){
                	selectedItem[selectedItem.length] = {openDateStr: sel.openDateStr + '', openDateEnd : sel.openDateEnd + '', openVdate : sel.openVdate + '', openNdate : sel.openNdate + ''};
                });
                var items = JSON.stringify(selectedItem);
                var cycle;
                var period;
                if (parentPageId === 'FinOpenPlanMaintenance002'){
                	cycle = $('#FinOpenPlanMaintenance002_investmentCycle').val();
//                	period = $('#FinOpenPlanMaintenance002_termType').val();
                	period = '';
                }else{
                	cycle = $('#FinNetValueProductBuild002_investmentCycle').val();
                	period = $('#periods').val();
                }
                
                var object = {items: items, operateType:"preview", cycle:cycle, period:period};
                
        	Joyin.dialog(JoyinUtil.getLabel("FinNetValueProductBuild004"),
                    JoyinUtil.getRealPath("invest/product/productelement/netvalueproduct/FinNetValueProductBuild006/init"),
                    "50%", "50%",object,
                    function(){
                    	//table.reload();
                    }
            );
            }
        }
        
    }
	
	
    $(function() {
        initFlag = false;
    	//add by yejiejie 2019-1-1-26 start
    	if($('#'+profitType).val() != PROFIT_TYPE_01){
    	    //不等于型的时候
    	    getElementById('cpriceRuleDiv').hide();// 隐藏净值日准则
            getElementByRedId('cpriceRuleDiv').hide();// 隐藏净值日准则  
    	}
    	
		if (parentPageId === 'FinOpenPlanMaintenance002'){
			$('#hidden_div_1').attr("style","visibility:hidden;"); // 占空间隐藏
			$('#hidden_div_2').attr("style","visibility:hidden;"); // 占空间隐藏
			
	    	if ($("#FinOpenPlanMaintenance002_dealMode").val() === '10'
					&& ($("#FinOpenPlanMaintenance002_termType").val() === '01' || $(
							"#FinOpenPlanMaintenance002_termType").val() === '02')) {
				$('#bussOpenPlandiv').show();
			} else {
				$('#bussOpenPlandiv').hide();
			}
	    	
	    	if($('#FinOpenPlanMaintenance002_dealMode').val() === '10') {
	    		$('#div1').hide();
	    	}else{
	    		$('#div1').show();
	    	}
		}
    	
    	//add 收益型产品不显示预览 20191119 yzg start
    	if($('#'+profitType).val() === PROFIT_TYPE_02){
    		$('#bussOpenPlandiv').hide();  
    	}
    	//add 收益型产品不显示预览 20191119 yzg end
		if(parentPageId === 'FinPlanNetAsset002'||parentPageId === 'FinPlanProfitAsset002'||parentPageId === 'FinPlanAssetC002'){
			$('#bussOpenPlandiv').hide();
		}
    	
    	$('#bussOpenPlan_btn_preview').click(function() {
        	preview();
        });
    	
    	$('#FinNetValueProductBuild002_dealMode').change(function() {
    		periodChange();
        });
    	
    	$('#FinNetValueProductBuild002_period').change(function() {
    	    //debugger;
        	if($('#FinNetValueProductBuild002_period').val() === TERM_TYPE_01) {
        		$('#cfDiv').show();
        		$('#FinNetValueProductBuild002_continueFlag').trigger('change').prop('disabled', false);
        		$('#FinNetValueProductBuild002_investmentCycle').trigger('change').prop('disabled', false);
//        		$('#FinNetValueProductBuild002_investmentCycle').attr('required', true).trigger('change');
//                $('#FinNetValueProductBuild002_investmentCycle').append($('<i class="must">*</i>'));
        		if(pageModel == PAGE_MODEL_NEW || pageModel == PAGE_MODEL_EDIT ){
                $('#FinNetValueProductBuild002_investmentCycle').parent().parent().find('.layui-form-label');
        		}
        		$('#periodDiv').hide();
        		$('#bussOpenPlandiv').show();
        		if(parentPageId !== 'FinOpenPlanMaintenance002'){
        			BussFinProductInvestPeriod.cleanTable();
        		}
        	}
        	else if($('#FinNetValueProductBuild002_period').val() === TERM_TYPE_02) {


        		$('#cfDiv').show();
        		$('#FinNetValueProductBuild002_continueFlag').trigger('change').prop('disabled', false);
        		if(isLoaded()){
            		$('#FinNetValueProductBuild002_investmentCycle').val('').trigger('change').prop('disabled', true);
        		}
        		else{
            		$('#FinNetValueProductBuild002_investmentCycle').trigger('change').prop('disabled', true);
        		}
        		$('#FinNetValueProductBuild002_investmentCycle').attr('required', false).trigger('change');

        		$('#bussOpenPlandiv').show();
        		$('#periodDiv').show();

        		$('#FinNetValueProductBuild002_investmentCycle').parent().parent().find('.layui-form-label .must').remove();

        	}
        	else if($('#FinNetValueProductBuild002_period').val() === TERM_TYPE_03) {

        		$('#cfDiv').hide();
        		if(isLoaded()){
            		$('#FinNetValueProductBuild002_investmentCycle').val('').trigger('change').prop('disabled', true);
        		}
        		else{
            		$('#FinNetValueProductBuild002_investmentCycle').trigger('change').prop('disabled', true);
        		}
        		$('#periodDiv').hide();
        		if ($("#FinOpenPlanMaintenance002_dealMode").val() !== '10'){
        			$('#bussOpenPlandiv').hide();
        		}
        		if(parentPageId !== 'FinOpenPlanMaintenance002'){
        			BussFinProductInvestPeriod.cleanTable();
        		}

        	    $('#FinNetValueProductBuild002_investmentCycle').parent().parent().find('.layui-form-label .must').remove();

        	}
        	else{
        		$('#cfDiv').hide();
        		if(isLoaded()){
            		$('#FinNetValueProductBuild002_investmentCycle').val('').trigger('change').prop('disabled', false);
        		}
        		else{
            		$('#FinNetValueProductBuild002_investmentCycle').trigger('change').prop('disabled', false);
        		}
        		$('#periodDiv').hide();
        		if ($("#FinOpenPlanMaintenance002_dealMode").val() !== '10'){
        			$('#bussOpenPlandiv').hide();
        		}
                $('#FinNetValueProductBuild002_investmentCycle').parent().parent().find('.layui-form-label .must').remove();
        	}
             
        });
    	//add by yejiejie 2019-1-1-26 start
    	//add by yejiejie 2019-2-12  start 开放计划中当申赎不同频的时候，赎回模块也需要对应的联动
    	getElementByRedId('openMode').change(openModeRedChange);
    	// 画面开放模式联动事件
        openModeRedChange();    	
    	// 画面开放周期单位联动事件
    	getElementByRedId('openCycleUnit').change(openCycleUnitRedChange);
    	// 画面周几联动事件
    	getElementByRedId('dayOfWeek').change(weekRedChange);
    	// 画面推算方法联动事件
    	getElementByRedId('calcMethod').change(calcMethodRedChange);
    	//add by yejiejie 2019-2-12  end 开放计划中当申赎不同频的时候，赎回模块也需要对应的联动
	// 父画面开放类型联动事件
    	$('#' + operationType).change(parentOpenModeChange);
    	// 父画面处理模式联动事件
    	$('#' + openPlanDealMode).change(parentDealModeChange);
        parentOpenModeChange();
        periodChange();
        // 画面开放模式联动事件
        openModeChange();
    	getElementById('openMode').change(openModeChange);
    	// 画面开放周期单位联动事件
    	getElementById('openCycleUnit').change(openCycleUnitChange);
    	// 画面周几联动事件
    	getElementById('dayOfWeek').change(weekChange);
    	// 画面推算方法联动事件
    	getElementById('calcMethod').change(calcMethodChange);
//    	getElementById('firstPayDate').change(firstPayDateChange);
//    	getElementById('payCycle').change(payCycleChange);
//    	getElementById('payCycleVal').change(payCycleValChange);
//    	getElementById('payCycleUnit').change(payCycleUnitChange);
    	
    	//add by yujiaping 20210312 ZGBS-168 控制开放周期、开放天数、确认天数只能输入整数 start
    	getElementById('openCycle').change(openCycleChange);
    	getElementById('openDays').change(openDaysChange);
    	getElementById('purSpeed').change(purSpeedChange);
    	//add by yujiaping 20210312 ZGBS-168 控制开放周期、开放天数、确认天数只能输入整数 end

    	getElementByRedId('redSpeed').change(purSpeedRedChange);

    	// 几号、第几工作日勾选标识
    	layui.form.on('radio(openDateFilter)', function(data){
    		if(data) {
                if(data.value === '0') {
                	getElementById('dayOfMonth').prop('disabled', false).prop('readonly', false);
                	getElementById('workdayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                	getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                    getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true).prop('readonly', true);
                } else if(data.value === '1') {
                	getElementById('workdayOfMonth').prop('disabled', false).prop('readonly', false);
                	getElementById('dayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                	getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                    getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true).prop('readonly', true);
                } else {
                    getElementById('weekOfMonth').val('').trigger('change').prop('disabled', false).prop('readonly', false);
                    getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',false).prop('readonly', false);
                    getElementById('workdayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                    getElementById('dayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                }
            }
		});
    	// 几号、第几工作日勾选标识
    	layui.form.on('radio(openDateFilterRed)', function(data){
    		if(data) {
                if(data.value === '0') {                	
                	//add by yejiejie 2019-2-12 start
                	getElementByRedId('dayOfMonth').prop('disabled', false).prop('readonly', false);
                	getElementByRedId('workdayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                	//add by yejiejie 2019-2-12 end
                	getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                	getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true).prop('readonly', true);
                } else if(data.value === '1') {
                    getElementByRedId('workdayOfMonth').prop('disabled', false).prop('readonly', false);
                    getElementByRedId('dayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                    getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                    getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true).prop('readonly', true);
                } else {
                    getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', false).prop('readonly', false);
                    getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',false).prop('readonly', false);
                    getElementByRedId('workdayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                    getElementByRedId('dayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                }
            }
		});
    	// 预览按钮点击事件
		getElementById('btn_preview').click(checkInputParam);
		getElementByRedId('btn_preview').click(checkInputParamRed);
		layui.form.on('switch(isSameFLagFilter)', function(data){
			
			sameFlagChange();
        });
		 // 父画面收益类型联动事件
//    	profitTypeChange();
//        $('#' + profitType).change(profitTypeChange);
		if(pageModel != PAGE_MODEL_NEW ){
			
		sameFlagChange();
		}else{
			getElementById('sameFLag').prop("checked",true).trigger("change");
		}
		 // 父画面收益类型联动事件
		$('#' + profitType).change(profitTypeChangeEx);
		//开放天数类型
		getElementById('openDaysType').change(openDaysTypeChange);
        getElementByRedId('openDaysType').change(openDaysTypeRedChange);
        //推算规则
        getElementById('calcRule').change(calcRuleChange);
        getElementByRedId('calcRule').change(calcRuleRedChange);
        
        /**20200305 add by taoran 净值型产品，开放计划，确认天数不应支持T+0 start*/
        $('#bussFinProductOpenPlanDto_purSpeed').change(function(){
			if((parentPageId == 'FinNetValueProductBuild002'||parentPageId == 'FinPlanNetAsset002'||parentPageId == 'FinPlanProfitAsset002'||parentPageId == 'FinPlanAssetC002') && $('#FinNetValueProductBuild002_profitType').val()
					== '01' && $('#bussFinProductOpenPlanDto_purSpeed').val() == '0'){
				$('#bussFinProductOpenPlanDto_purSpeed').val('1');
			}
		});

		$('#bussFinProductOpenPlanRedDto_redSpeed').change(function(){
			if((parentPageId == 'FinNetValueProductBuild002'||parentPageId == 'FinPlanNetAsset002'||parentPageId == 'FinPlanProfitAsset002'||parentPageId == 'FinPlanAssetC002') && $('#FinNetValueProductBuild002_profitType').val()
				== '01' && $('#bussFinProductOpenPlanRedDto_redSpeed').val() == '0'){
				$('#bussFinProductOpenPlanRedDto_redSpeed').val('1');
			}
		});
        /**20200305 add by taoran 净值型产品，开放计划，确认天数不应支持T+0 end*/
//        debugger;

        var loadDartEnd = false, maxCount = 5;
        var _timmer = setInterval(function() {
              if(loadDartEnd && maxCount <= 0) {
                 clearInterval(_timmer);
                 _timmer = null;
                 return;
              }
              maxCount--;
              loadDartEnd = $("input[name='bussFinProductOpenPlanDto.openDateRadio']:first").next('.layui-form-radio').length > 0;
              if(isDraft && loadDartEnd){
                 if(openDateRadio  ){
     //                $("input[name='bussFinProductOpenPlanDto.openDateRadio'][value='"+openDateRadio+"']").attr('checked',true);
                     $("input[name='bussFinProductOpenPlanDto.openDateRadio'][value='"+openDateRadio+"']").prop('checked',true);

                     if(openDateRadio === '0') {
                        getElementById('dayOfMonth').prop('disabled', false).prop('readonly', false);
                        getElementById('workdayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                        getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                        getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true).prop('readonly', true);
                     } else if(openDateRadio === '1') {
                        getElementById('workdayOfMonth').prop('disabled', false).prop('readonly', false);
                        getElementById('dayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                        getElementById('weekOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                        getElementById('workdayOfWeek').val('').trigger('change').prop('disabled',true).prop('readonly', true);
                     } else {
                        getElementById('dayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                        getElementById('workdayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                        getElementById('weekOfMonth').trigger('change').prop('disabled', false).prop('readonly', false);
                        getElementById('workdayOfWeek').trigger('change').prop('disabled',false).prop('readonly', false);
                     }

                 }
                 if(openDateRadioRed){
                     $("input[name='bussFinProductOpenPlanRedDto.openDateRadio'][value='"+openDateRadioRed+"']").prop('checked',true).trigger('change');
                     if(openDateRadioRed === '0') {
                         getElementByRedId('dayOfMonth').prop('disabled', false).prop('readonly', false);
                         getElementByRedId('workdayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                         getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                         getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true).prop('readonly', true);
                      } else if(openDateRadioRed === '1') {
                         getElementByRedId('workdayOfMonth').prop('disabled', false).prop('readonly', false);
                         getElementByRedId('dayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                         getElementByRedId('weekOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                         getElementByRedId('workdayOfWeek').val('').trigger('change').prop('disabled',true).prop('readonly', true);
                      } else {
                         getElementByRedId('dayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                         getElementByRedId('workdayOfMonth').val('').trigger('change').prop('disabled', true).prop('readonly', true);
                         getElementByRedId('weekOfMonth').trigger('change').prop('disabled', false).prop('readonly', false);
                         getElementByRedId('workdayOfWeek').trigger('change').prop('disabled',false).prop('readonly', false);
                      }

                 }
                 layui.form.render('radio');

             }
         }, 1000);

    });
}());