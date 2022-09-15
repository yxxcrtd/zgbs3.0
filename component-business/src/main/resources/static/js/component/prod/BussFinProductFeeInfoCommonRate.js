/**
\ * 产品费用共通JS
 * @auth zhengyan
 */
(function(){

	var profitTypeElement1 = $('#' + getElementById('profitTypeElement').val());
	// 快速录入标志
	var quickFlag = false;

	// 预览按钮在产品模板新建时不显示
	//增加ignore 属性
	var buttons = [{ text: '&#xe614;',ignore:true, clas: 'preview', title: '预览' }, { text: '&#xe640;',ignore:true, clas: 'delt', title: '删除' }];
	if (getElementById('pageId').val() != 'FinProductBuild002' && getElementById('pageId').val() != 'FinNetValueProductBuild002'){
		buttons = ['delt'];
	}
	
    /**
     * 获取画面元素
     */
	function getElementById(id) {
		return $('#' + feeInfoEntityName1 + '_' + id);
	}
	
    /**
     * 转换表格数据
     */
    var convertRow = function(rowData) {
    	_.each(FEE_TYPE, function(v) {
    		if(v.id === rowData.feeType) {
				rowData.feeType = v.text;
    		}
    	});
    	_.each(JSON.parse(BILL_BASE_FEE_KIND), function(v) {
    		if(v.id === rowData.basicBill) {
    			rowData.basicBillStr = v.text;
    		}
    	});
//    	_.each(BASIS, function(v) {
//    		if(v.id === rowData.basis) {
//    			rowData.basis = v.text;
//    		}
//    	});
    }
    
    var getSearchParams = function(){
    	var data = {};
    	data.quickFlag = quickFlag;
    	if($('#FinProductBuild002_quickFlag') && $('#FinProductBuild002_quickFlag').val() === 'N'){
    		data.ptlId = $('#' + getElementById('ptlIdElement').val()).val();
    	}

		//add by mashukai 20201120  BS-1994 草稿箱反显时费用列表有时反显没有值问题 start
		if(isDraft && (workflowId==null || workflowId=="")){
			workflowId = $('#productFeeInfoDto_workflowId').val();
		}
		//add by mashukai 20201120  BS-1994 草稿箱反显时费用列表有时反显没有值问题 end
    	data.workflowId = workflowId;
        data.finprodId = finprodFeeFinprodIdCommon;
        data.entityName = feeInfoEntityName;
        data.isTodoList = isTodoList1;
    	return data;
    }

    /**
     * 定义数据表格
     */
    var bussFinProductFeeInfoTable = getElementById('feeInfo').joyintable({
    	ajax: {
    		url: JoyinUtil.getRealPath('/component/prod/BussFinProductFeeInfoCommonRate/getFeeInfo'),
    		type: 'POST',
    		data: function() {
                return getSearchParams();
            }
    	},
        searchOnInit: true,
        loadonce: true,
        rowno: true,
        page: false,
        checkbox:false,
        addbtn: true,
        height : 200,
        cols:[
            {field:'feeId', width:200, title: 'BussFinProductFeeInfo.feeName', sortable: false,
            	edit:true,
            	editOption:{type:'select',parentIds:getElementById('finprodMarketIdElement').val() + ',' +getElementById('profitTypeElement').val(), url: JoyinUtil.getRealPath('/component/prod/BussFinProductFeeInfo/queryFeeInfoList')}}
            ,{field:'feeType', width:200, title: 'BussFinProductFeeInfo.feeType', sortable: false,visible:false}//visible:false默认不展示
            ,{field:'prodStyle', width:100, title: 'BussFinProductFeeInfo.prodStyle', sortable: false, edit:true,visible : (getElementById('pageId').val() === 'FinNetValueProductBuild002' && $('#' + dealModeElement).val() ===DISPOSE_PATTERN_11) , editOption:{type:'select', formatoption : false, result: PROD_STYLE}}
            ,{field:'feeRate', width:100, title: 'BussFinProductFeeInfo.feeRate', sortable: false, edit:true}
            //,{field:'basicBill',visible : false}
            ,{field:'basicBillStr',sortField:'basicBill', width:150, title: 'BussFinProductFeeInfo.basicBill', sortable: false,
            	edit:false,
            	editOption:{type:'select',result:BILL_BASE_FEE_KIND}
            }
            ,{field:'basis', width:100, title: 'BussFinProductFeeInfo.basis', sortable: false,
            	edit:true,
            	editOption:{type:'select',result:BASIS_SELECT}
            }
            ,{field:'netAssetValueDay', width:150, title: 'BussFinProductFeeInfo.netAssetValueDay', sortable: false,
                visible : pageId === 'FinNetValueProductBuild002',
        		edit:true,
        		// 一体化对应etliu start
//        		editOption:{type:'select', result: AST_VLU_DATE_TYPE}
				editOption:{type:'select', result: BASE_DATE_TYPE}
				// 一体化对应etliu end
            }
            ,{field:'firstBaseType', width:100, title: 'BussFinProductFeeInfo.firstBaseType', sortable: false, 
            	edit:true,
            	editOption:{type:'select',result:BILL_BASE_FEE_KIND}
            }
            ,{field:'provisionFirstDay', width:150, title: 'BussFinProductFeeInfo.provisionFirstDay1', sortable: false,
            	visible:getElementById('pageId').val() === 'FinProductBuild002'||pageId === 'FinNetValueProductBuild002',
            	edit:true,
            	type:'date'
            }
            ,{field:'payFeeRule', width:150, title: 'BussFinProductFeeInfo.payFeeRule', sortable: false,
            	edit:true,
            	editOption:{type:'select', result: PAY_RULE}
            }
            ,{field:'calcFeeTradeRule', width:150, title: 'BussFinProductFeeInfo.calcFeeTradeRule', sortable: false,
            	edit:true, 
            	editOption:{type:'select',childrenIds : 'payFeeTradeRule',id: "calcFeeTradeRule",formatoption:true,result: TDAY_RULE}
            }
            ,{field:'payFeeTradeRule', width:150, title: 'BussFinProductFeeInfo.payFeeTradeRule', sortable: false,
            	edit:true,
            	editOption:{type:'select',parentIds:'calcFeeTradeRule',id :"payFeeTradeRule", formatoption:true,result: TDAY_RULE}
            }
            ,{field:'eAccrDate', width:150, title: 'BussFinProductFeeInfo.eAccrDate', sortable: false,
            	visible:getElementById('pageId').val() === 'FinProductBuild002'||pageId === 'FinNetValueProductBuild002',
            	edit:true,
            	type:'date'
            }
            ,{field:'firstCalEndDate', width:150, title: 'BussFinProductFeeInfo.firstCalEndDate.two', sortable: false,
            	visible:getElementById('pageId').val() === 'FinProductBuild002'||pageId === 'FinNetValueProductBuild002',
            	edit:true,
            	type:'date'
            }
            ,{field:'orgType', visible : false}
            ,{field:'orgType2', visible : false}
            ,{field:'chargeType', visible : false}
            ,{field:'remark', visible : false}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: buttons}
        ],
        beforeEditCell : function(preVal, field, rowData){
            
            if($('#' + getElementById('profitTypeElement').val()).val() === PROFIT_TYPE_01 && field === 'prodStyle'){
                bussFinProductFeeInfoTable._getColByField("prodStyle").editOption.result = PROD_STYLE;
                return true;
            }
        	var profitType = profitTypeElement1.val();   
            //费用收费类型
            if(rowData.chargeType === CHARGE_TYPE_02 && field != 'feeId'){
               return false;
            }
        	//付费规则为 不规则
        	if (rowData.payFeeRule ===PAY_RULE_08 && (field ==='calcFeeTradeRule' || field === 'payFeeTradeRule' || field === 'firstCalEndDate')){
        		return false;
        	}
        	else if (field === "feeRate" && rowData.feeType === FEE_TYPE_05NAME){
        		return false;
        	}
        	//当选择前一自然日/前一工作日资产规模，首次计提日选择为产品起息日，首次计费基数显示为“存续本金”置灰不允许修改， yzg 20191020 start
        	
        	if(field === "firstBaseType"){
        		return false;
        	}
        	//当选择前一自然日/前一工作日资产规模，首次计提日选择为产品起息日，首次计费基数显示为“存续本金”置灰不允许修改， yzg 20191020 end
        	return true;
        },
        editCell : function(value, data, field, cell, column){
        	if (field === "feeId"){
        		var rowData = {};
        		rowData.feeType = ""; // 费用类型
        		rowData.basicBill = ""; // 计费基数
        		rowData.basis = ""; // 计算基础 
        		if (value != null && value != ''){
        			//由于data.feeid为老的值，故取value的值
        			Joyin.ajax(JoyinUtil.getRealPath('component/prod/BussFinProductFeeInfoCommonRate/queryMstFeeinfoByFeeId'), {feeId : value, finprodMarketId : $('#' + getElementById('finprodMarketIdElement').val()).val()}, function(result){
        			    if (result){
        					// 差额中收时费率(%)清空，不可编辑
        					if (result.feeType === FEE_TYPE_05){
        						rowData.feeRate = '';
        					}
        					if(result.feeType){
        						rowData.feeType = result.feeType;
        					}
        					//固定费用的计提开始日默认等于产品起息日  联动
        					if(result.feeType == '03' || result.feeType == '01' ||result.chargeType != CHARGE_TYPE_02 ){
        					    //计提开始日=产品起息日
        					    if($('#' + getElementById('vdateElement').val()).val()){
        					        rowData.provisionFirstDay=$('#' + getElementById('vdateElement').val()).val();
        					    }
        					}
        					if(result.basicBill){
        						rowData.basicBill = result.basicBill;
        						//如果计费基础为存续本金  净值日类型默认为当日
        						if(result.basicBill === BASIC_BILL_04){
        						    rowData.netAssetValueDay = BASE_DATE_TYPE_01;
        						} else {
        						    rowData.netAssetValueDay = BASE_DATE_TYPE_02;
        						}
        					}
        					if(result.basis){
        						rowData.basis = result.basis;
        					} 
        	    			rowData.orgType = result.orgType;
        	    			rowData.orgType2 = result.orgType2;
        	    			rowData.chargeType = result.chargeType;
        	    			rowData.remark = result.remark;
        	    			//收费规则不为 不规则
        	    			if(result.chargeType != CHARGE_TYPE_02){
            	    			//计提结束日=到期日-1
            	        		if($('#' + mdate).val()){
            	        			rowData.eAccrDate=addDate($('#' + mdate).val(),-1);
            	        		}
            	        		if($("#"+vdate).val()){
            	        			rowData.provisionFirstDay=$('#' + getElementById('vdateElement').val()).val();
            	        		}
        	    			}
        	    			if (result.chargeType === CHARGE_TYPE_02) {
                                //add by jiangpeng on 20200729 不规则费用 清空
                                rowData.netAssetValueDay = '';
                                rowData.provisionFirstDay = '';
                                rowData.eAccrDate = '';
                                rowData.firstCalEndDate = '';
                                //add by jiangpeng on 20200729 不规则费用 清空
        	    				rowData.feeRate = '';
        	    				rowData.payFeeRule = '';
        	    				rowData.calcFeeTradeRule = '';
        	    				rowData.payFeeTradeRule = ''; 
							} 
        	    			convertRow(rowData); 
        	        		bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        	        		/*// 更新缓存值
        	        		var cacheData = bussFinProductFeeInfoTable.getRow(data.LAY_TABLE_INDEX, true);
        	        		cacheData.feeType = result.feeType;
        	        		cacheData.basicBill = result.basicBill;
        	        		cacheData.basis = result.basis;*/ 
        				}
        			})
        		}else{
        			bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        		}
        	}else if (field === "payFeeRule"){
        		var rowData = {};
        		//费用规则选择联动
        		payFeeRuleChange(value,rowData);
        		//add by yzg首次计费基数联动 20200313
        		var provisionFirstDay = Date.ofDate(data.provisionFirstDay).format('yyyy-MM-dd');
        	    var vdate = $('#' + getElementById('vdateElement').val()).val();
        		if((data.netAssetValueDay === BASE_DATE_TYPE_02 || data.netAssetValueDay === BASE_DATE_TYPE_03) && rowData.provisionFirstDay === vdate && data.basicBill === BILL_BASE_FEE_KIND_03){
        			rowData.firstBaseType = BASIC_BILL_04;
        		}else{
        			rowData.firstBaseType = '';
        		}
        		//更新表格
        		bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        	}else if (field === "netAssetValueDay"){
        	    if(data.basicBill === BILL_BASE_FEE_KIND_03 && data.netAssetValueDay === BASE_DATE_TYPE_01){
        	        Joyin.error(JoyinUtil.getMessage('FIN_PROD_E_0113') ,function(){
        	            var rowData = {};
        	            rowData.netAssetValueDay = "";
        	            bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
                    });
        	    }
        	  //首次计费基数联动 yzg 20191022 start
        	    var provisionFirstDay = Date.ofDate(data.provisionFirstDay).format('yyyy-MM-dd');
        	    var vdate = $('#' + getElementById('vdateElement').val()).val();
        		if((data.netAssetValueDay === BASE_DATE_TYPE_02 || data.netAssetValueDay === BASE_DATE_TYPE_03) && provisionFirstDay === vdate && data.basicBill === BILL_BASE_FEE_KIND_03){
        			var rowData = {};
        			rowData.firstBaseType = BASIC_BILL_04;
	        		bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        		}else{
        			var rowData = {};
        			rowData.firstBaseType = '';
	        		bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        		}
        	  //首次计费基数联动 yzg 20191022 end
        	} else if(field === 'calcFeeTradeRule'){
        	    var rowData = {};
        	    rowData.payFeeTradeRule='';
        	    //更新表格
                bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
                //首次计费基数联动 yzg 20191022 start
        	} else if(field === 'provisionFirstDay'){
        		var vdate = $('#' + getElementById('vdateElement').val()).val();
        		var provisionFirstDay = Date.ofDate(data.provisionFirstDay).format('yyyy-MM-dd');
        		if((data.netAssetValueDay === BASE_DATE_TYPE_02 || data.netAssetValueDay === BASE_DATE_TYPE_03) && provisionFirstDay === vdate && data.basicBill === BILL_BASE_FEE_KIND_03){
        			var rowData = {};
        			rowData.firstBaseType = BASIC_BILL_04;
	        		bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        		}else{
        			var rowData = {};
        			rowData.firstBaseType = '';
	        		bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        		}  
        	} else if (field === 'basicBill'){
        		var vdate = $('#' + getElementById('vdateElement').val()).val();
        		var provisionFirstDay = Date.ofDate(data.provisionFirstDay).format('yyyy-MM-dd');
        		if((data.netAssetValueDay === BASE_DATE_TYPE_02 || data.netAssetValueDay === BASE_DATE_TYPE_03) && provisionFirstDay === vdate && data.basicBill === BILL_BASE_FEE_KIND_03){
        			var rowData = {};
        			rowData.firstBaseType = BASIC_BILL_04;
	        		bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        		}else{
        			var rowData = {};
        			rowData.firstBaseType = '';
	        		bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
        		}
        	}else if(field === 'feeRate'){
				var rowData = {};
				if (data.feeRate!=null && data.feeRate!='') {
					rowData.feeRate = formatMoney(data.feeRate, 4);
				} 
				bussFinProductFeeInfoTable.renderRow(data.LAY_TABLE_INDEX, rowData);
			}else if(field === 'chargeType'){
				var rowData = {};
				if (data.chargeType == "02") { 
                    rowData.provisionFirstDay = ''; 
				} 
			}
        	//首次计费基数联动 yzg 20191022 end
        },
        done : function(table, allData){
        	if($('#FinProductBuild002_quickFlag')){
        		$('#FinProductBuild002_quickFlag').val('N');
        	} 
        }
    });

	/**
	 * 金额格式化: 框架数据表格 render 后
	 */
	var formatMoney = function (s, n)
	{
		n = n > 0 && n <= 20 ? n : 2;
		s = parseFloat((s + '').replace(/[^\d\.-]/g, '')).toFixed(n) + '';
		var l = s.split('.') [0].split('').reverse(),
			r = s.split('.') [1];
		var t = '';
		for (var i = 0; i < l.length; i++)
		{
			t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? ',' : '');
		}
		return t.split('').reverse().join('') + '.' + r;

	}
    /**
     * 费用规则选择联动
     */
    function payFeeRuleChange(value, rowData){
         //不规则
        if(value === PAY_RULE_08){
            rowData.firstCalEndDate = "";
            rowData.calcFeeTradeRule = "";
            rowData.payFeeTradeRule = "";
            
        } else if (value === PAY_RULE_07) {
        	rowData.firstCalEndDate = $('#' + mdate).val();
        }
        //起息日到期日必填
        if($('#' + getElementById('vdateElement').val()).val() && $('#' + mdate).val()){
            //非不规则
            if(value !=PAY_RULE_08) {
                //首次计费结束日先设置为到期前一天
                rowData.firstCalEndDate = $('#' + mdate).val();
            }
            //首次计提日
            rowData.provisionFirstDay = $('#' + getElementById('vdateElement').val()).val();
            //日期单位    
            var strInterval = '';
            //月
            if(value === PAY_RULE_03){
                strInterval = 'm'; 
            } 
            //季
            else if(value === PAY_RULE_04){
                strInterval = 'q'; 
            }
            //半年
            else if(value === PAY_RULE_06){
                strInterval = 'q'; 
            }
            //年
            else if(value === PAY_RULE_05){
                strInterval = 'y'; 
            } 
            if(strInterval != ''){
                //首次计费结束日
                var firstCalEndDate = Date.ofDate($('#' + getElementById('vdateElement').val()).val()).DateAdd(strInterval,1);
                
                // 半年
                if (value === PAY_RULE_06) {
                	var firstCalEndDate = firstCalEndDate.DateAdd(strInterval,1);
                }
                
                if(firstCalEndDate.DateDiff( "d" ,$('#' + mdate).val()) > 0){
                    rowData.firstCalEndDate= firstCalEndDate.Format('yyyy-MM-dd');
                }
            }
        }
    }



	//add by gcl 2019/3/29 start
	function eAccrDatechange(){
		var finAssetIntInfos = bussFinProductFeeInfoTable.getAllRows();
		if (finAssetIntInfos.length > 0) {
			var eAccrDateVal = addDate($('#' + mdate).val(), -1);
			$.each(finAssetIntInfos, function (index, obj) {
				var rowData = {};
				//费用规则选择联动
				payFeeRuleChange(finAssetIntInfos[index].payFeeRule, rowData);
				if (finAssetIntInfos[index].chargeType !== CHARGE_TYPE_02) {
					// bussFinProductFeeInfoTable.renderRow(index, {eAccrDate: eAccrDateVal});
					rowData.eAccrDate = eAccrDateVal;
				}
				bussFinProductFeeInfoTable.renderRow(index, rowData);
			});
			//到期日和首次付费日联动
			// var firstCalEndDateVal=$('#' + mdate).val();
			// $.each(finAssetIntInfos,function(index,obj){
			// if(finAssetIntInfos[index].chargeType != CHARGE_TYPE_02){
			// 	bussFinProductFeeInfoTable.renderRow(index, {firstCalEndDate: firstCalEndDateVal});
			// }
			// });
		}
	}

	//联动起息日和计提开始日
	function provisionFirstDaychange() {
		var finAssetIntInfos = bussFinProductFeeInfoTable.getAllRows();
		if (finAssetIntInfos.length > 0) {
			var provisionFirstDayVal = $('#' + vdate).val();
			$.each(finAssetIntInfos, function (index, obj) {
				var rowData = {};
				if (finAssetIntInfos[index].chargeType !== CHARGE_TYPE_02) {
					rowData.provisionFirstDay = provisionFirstDayVal;
				}
				//费用规则选择联动
				payFeeRuleChange(finAssetIntInfos[index].payFeeRule, rowData);
				var vdate = $('#' + getElementById('vdateElement').val()).val();
				if ((finAssetIntInfos[index].netAssetValueDay === BASE_DATE_TYPE_02 || finAssetIntInfos[index].netAssetValueDay === BASE_DATE_TYPE_03) &&
					rowData.provisionFirstDay === provisionFirstDayVal && finAssetIntInfos[index].basicBill === BILL_BASE_FEE_KIND_03) {
					rowData.firstBaseType = BASIC_BILL_04;
				} else {
					rowData.firstBaseType = '';
				}
				bussFinProductFeeInfoTable.renderRow(index, rowData);
			});
		}
	}
  //add by gcl 2019/3/29 end
    //add by yejiejie 2019-2-22 日期加减
    function addDate(date,days){
        var d=Date.ofDate(date); 
        d.setDate(d.getDate()+days); 
        var m=d.getMonth()+1; 
        if(m<10){
        	m='0'+m;
        }
        var day = d.getDate();
        if(day<10){
        	day = '0'+d.getDate();      	
        }
        return d.getFullYear()+'-'+m+'-'+day; 
    };
    //add by yejiejie 2019-2-22 日期加减
    /**
	 * 绑定删除事件
	 */
    bussFinProductFeeInfoTable.bind('click', '.delt', function(data, index) {
    	bussFinProductFeeInfoTable.removeRow(data);
	});
    
    bussFinProductFeeInfoTable.bind('click', '.preview', function(data, index) {
    	
    	data.dealCalId = $('#' + getElementById('dealCalIdElement').val()).val();
    	data.vdate = $('#' + getElementById('vdateElement').val()).val();
    	data.mdate = $('#' + getElementById('mdateElement').val()).val();
    	data.profitType = $('#' + getElementById('profitTypeElement').val()).val();
    	//add by gaochenglong 2019/3/11 start
    	data.firstCalEndDate = Date.ofDate(data.firstCalEndDate).format("yyyy-MM-dd");
        data.eAccrDate = Date.ofDate(data.eAccrDate).format("yyyy-MM-dd");
    	//add by gaochenglong 2019/3/11 end
    	//OPEX-921 产品要素新建时-挂钩不规则托管费-点击预览按钮提示信息有误
		if(data.chargeType != CHARGE_TYPE_02){
			Joyin.ajaxJson(JoyinUtil.getRealPath('component/prod/BussFinProductFeeInfoCommonRate/checkPreview'), data, function(result){
				if (result.data){
					Joyin.confirm(result.data, function() {
						// 生成付费计划
						Joyin.dialog(JoyinUtil.getLabel("FinProductFeePlanInfo"), JoyinUtil.getRealPath('component/prod/FinProductFeePlanInfo/init'),
								"45%", "50%", {pageModel:PAGE_MODEL_REFERENCE, feeInfo: JSON.stringify(data)})
					});
				}else{
					// 生成付费计划
					Joyin.dialog(JoyinUtil.getLabel("FinProductFeePlanInfo"), JoyinUtil.getRealPath('component/prod/FinProductFeePlanInfo/init'),
							"45%", "50%", {pageModel:PAGE_MODEL_REFERENCE, feeInfo: JSON.stringify(data)})
				}
			})
		}
	});
    
    function profitTypeChange() {
    	// 收益类型
    	 var selectedValue = $('#' + profitTypeElement).val();
         if (!selectedValue) {
             getElementById('div').hide();
         } else {
             getElementById('div').show();
         }
         /*
         alert($('#' + feeInfoEntityName1 + '_feeInfo').next().find("table tr").find('th:eq(5)').html());
          if(PROFIT_TYPE_01===selectedValue){
        	$('#' + feeInfoEntityName1 + '_feeInfo').next().find("table tr").find('td:eq(5)').show();
        	$('#' + feeInfoEntityName1 + '_feeInfo').next().find("table tr").find('th:eq(5)').show();
          }else{
        	$('#' + feeInfoEntityName1 + '_feeInfo').next().find("table tr").find('td:eq(5)').hide();
        	$('#' + feeInfoEntityName1 + '_feeInfo').next().find("table tr").find('th:eq(5)').hide();
          }*/
    }
    /**
     * 产品处理模式改变事件
     */
    function dealModeChange () {
        // 净值型产品新界面 切产品处理模式为分档
        if (getElementById('pageId').val() === 'FinNetValueProductBuild002' && $('#' + dealModeElement).val() ===DISPOSE_PATTERN_11 ) {
            //显示适用款型
            bussFinProductFeeInfoTable.showColumns([{field:'prodStyle', visible: true}]);
        } else {
            bussFinProductFeeInfoTable.showColumns([{field:'prodStyle', visible: false}]);
            var feeArr = bussFinProductFeeInfoTable.getAllRows();
            if(feeArr && feeArr.length > 0){
                _.each(feeArr, function(rowData) {
                    rowData.prodStyle = '';
                    bussFinProductFeeInfoTable.renderRow(rowData.LAY_TABLE_INDEX, rowData);
                });
            }    
        }
    }
    
    
    // 公开接口
    window.BussFinProductFeeInfo = {
        getAllRows:getAllRows,
        feeInfoInitAction:feeInfoInitAction,
        bussFinProductFeeInfoTable:bussFinProductFeeInfoTable,
        feeInfoInitActionByTmpl:feeInfoInitActionByTmpl
    };
    
    function feeInfoInitAction(input_finprodId){
    	finprodFeeFinprodId = input_finprodId;
    	finprodFeeFinprodIdCommon=input_finprodId;
    	quickFlag = true;
    	bussFinProductFeeInfoTable.table ? bussFinProductFeeInfoTable.reload() : bussFinProductFeeInfoTable.load();
    	//清空table内日期值 chenxiaoqiu 20191126 
    	if(isDisplayDate==NO){
        	var list =	bussFinProductFeeInfoTable.getAllRows();
            if(list && list.length > 0){
                _.each(list, function(rowData) {
            		rowData.provisionFirstDay = '';
            		rowData.firstCalEndDate	= '';
            		rowData.eAccrDate = '';
                    bussFinProductFeeInfoTable.renderRow(rowData.LAY_TABLE_INDEX, rowData);
                });
            }   
        }
    }
    
    function feeInfoInitActionByTmpl(bussFinProductFeeInfoDto) {
    	if(bussFinProductFeeInfoTable) {
    		if(bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList)
    		_.each(bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList, function(rowData) {
    			bussFinProductFeeInfoTable.addRow(rowData);
    		});
    	}
    }
    
    function getAllRows(){
        if(bussFinProductFeeInfoTable != ''){
            var rowDatas = [];
            _.each(bussFinProductFeeInfoTable.getAllRows(), function(rowData) {
                _.each(FEE_TYPE, function(v) {
                    if(v.text === rowData.feeType) {
                        rowData.feeType = v.id;
                    }
                });
                _.each(BILL_BASE_FEE_KIND, function(v) {
                    if(v.text === rowData.basicBill) {
                        rowData.basicBillStr = v.text;
                    }
                });
                _.each(BASIS, function(v) {
                    if(v.text === rowData.basis) {
                        rowData.basis = v.id;
                    }
                });
                rowDatas.push(rowData);
            });
            return rowDatas;
        }
    }
    
    /*
     * 当产品的收益类型为“净值型”和“货币型”时，选择“组合代码”时带出组合下产品的费用信息
     */
    function ptlIdChange(){
    	if($('#FinProductBuild002_quickFlag')){
    		if ($('#FinProductBuild002_quickFlag').val() === 'N'){
    			var ptlIdElement = getElementById('ptlIdElement').val();
    	    	if (ptlIdElement && ptlIdElement != ''){
    	    		var profitType = $('#' + getElementById('profitTypeElement').val()).val();
    	        	var ptlId = $('#' + ptlIdElement).val();
    	        	if ((profitType === PROFIT_TYPE_01 || profitType === PROFIT_TYPE_03) && ptlId){
    	        		if($('#FinProductBuild002_quickFlag') && $('#FinProductBuild002_quickFlag').val() === 'N'){
    	        			bussFinProductFeeInfoTable.table ? bussFinProductFeeInfoTable.reload() : bussFinProductFeeInfoTable.load();
    	        		}
    	        	}
    	    	}
    		}
    	}
    	
    }

    $(function() {
    	 $('#' + mdate).change(eAccrDatechange);
		$('#' + vdate).change(provisionFirstDaychange);
        // 父画面收益类型联动事件
    	profitTypeChange();
    	$('#' + getElementById('profitTypeElement').val()).on('change', function () {
            profitTypeChange();
        });
    	//产品处理模式
    	$('#' + dealModeElement).on('change', function () {
    	    dealModeChange();
    	});
    	// 收益类型切换事件
    	$('#' + getElementById('profitTypeElement').val()).change(function() {
    		var profitType = $(this).val();
    		if(profitType) {
    			// 设置表格列“资产净值日”是否显示
    			bussFinProductFeeInfoTable.showColumns([{field:'netAssetValueDay', visible: [PROFIT_TYPE_01, PROFIT_TYPE_03].indexOf(profitType) > -1}]);
    			// 清空表格(预期型产品强制不规则费用)
    			if (profitType === PROFIT_TYPE_02){
    				bussFinProductFeeInfoTable.clear();
    			}
    		}
    		if (getElementById('pageId').val() === 'FinNetValueProductBuild002'){
    			ptlIdChange();
    		}
    	});
        
        // 父画面组合代码联动事件
    	if (getElementById('pageId').val() === 'FinNetValueProductBuild002'){
    		$('#' + getElementById('ptlIdElement').val()).on('change', function () {
                ptlIdChange();
            });
    	}
        
    	// 新建按钮点击事件
    	getElementById('btn_create').click(function() {
    		var finprodMarketId = $('#' + getElementById('finprodMarketIdElement').val()).val();
    		if (finprodMarketId && finprodMarketId !== ''){
    			var feeList = JSON.stringify(bussFinProductFeeInfoTable.getAllRows());
    			var dialogIndex = Joyin.dialog(JoyinUtil.getLabel("MstFeeVarieties002"),
                        "/invest/basicdata/mstfeevarieties/MstFeeVarieties002/init",
                        "98%", "95%", {pageModel:PAGE_MODEL_NEW, applyType: 'prod', finprodMarketId : finprodMarketId, feeList : feeList, profitType : profitTypeElement1.val()}, function(data) {
                        	if(!data) return;
                        	// 新建费用品种后，在费用列表中，添加一行数据，费用名称，费用类型，计费基数，计算基础，默认带出上述添加的数据。
                        	var row = {
                    			feeId: data.feeId,
                    			feeName: data.feeName,
                    			feeType: data.feeType,
                    			basicBill: data.basicBill,
                    			basis: data.basis,
                    			orgType: data.orgType,
                    			orgType2: data.orgType2,
                    			chargeType: data.chargeType,
                    			remark : data.remark
                    		};
                        	if(row.basicBill){
                                //如果计费基础为存续本金  净值日类型默认为当日
                                if(row.basicBill === BASIC_BILL_04){
                                    row.netAssetValueDay = BASE_DATE_TYPE_01;
                                } else {
                                    row.netAssetValueDay = BASE_DATE_TYPE_02;
                                }
                            }
                        	var profitType = profitTypeElement1.val();
                        	if (row.chargeType != CHARGE_TYPE_02 && profitType === PROFIT_TYPE_02){
                        		row.payFeeRule = PAY_RULE_08;
                        	} 
                        	if(row.chargeType != CHARGE_TYPE_02 && $("#"+vdate).val()){
        	        			row.provisionFirstDay=$('#' + vdate).val();
        	        		}	 
                        	convertRow(row);
                        	bussFinProductFeeInfoTable.addRow(row);
                        });
    		}
//    		else{
//    			// 请输入产品代码
//    			if (getElementById('pageId').val() != 'FinProductBuild002'){
//    				return Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038', JoyinUtil.getLabel('FinProductModule002.templetName')));
//    			}else{
//    				return Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038', JoyinUtil.getLabel('FinProductBuild002.finprodMarketId')));
//    			}
//    			
//    		}
    	});
    	
    });
}());