/**
 * 业务基准共通JS
 * @auth zhengyan
 */
(function(){
    
    var getElementById = function(id) {
        return $('#' + benchMarkLayeredInfoEntityName + '_' + id);
    };
    
    var getSearchParams = function(){
        var data = {};
         data.workflowId = getElementById('workflowId').val();
         data.finprodId = getElementById('finprodId').val();
         data.entityName = benchMarkLayeredInfoEntityName;
         data.isTodoList = isTodoList1;
        return data;
      }
    /**
     * 定义数据表格
     */
    var bussBenchMarkInfoTable = getElementById('markInfo').joyintable({
        ajax: {
            url: JoyinUtil.getRealPath('/component/prod/BussBenchMarkLayeredInfoCommon/queryBenchMarkInfoByFinprodId'),
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
       
        searchOnInit: true,
        loadonce: true,
        checkbox:false,
        page: false,
        height : 200,
        cols:[
            {field:'publishType',title: 'BussBenchMarkInfo.publishType', visible: false}
              ,{field:'prodStyle', width:200, title: 'BussBenchMarkInfo.prodStyle', visible: true,sortable: false, edit:true,editOption:{type:'select',formatoption:true,result: PROD_STYLE}}
              ,{field:'investmentCycle', width:200, title: 'BussBenchMarkInfo.investmentCycle', visible: true,sortable: false, edit:true,editOption:{type:'select',formatoption:true,result: PROD_STYLE}}
              ,{field:'rateType', width:150, title: 'BussBenchMarkInfo.rateType', sortable: false, edit:true,editOption:{type:'select',formatoption:true,result: INT_TYPE}}
              ,{field:'rate', width:150, title: 'BussBenchMarkInfo.rate', sortable: false, type:'number',digit:4}
              ,{field:'floatRate', sortable:false, width:150 ,title: 'BussBenchMarkInfo.indexId'}
              ,{field:'resetType', width:200,title: 'BussBenchMarkInfo.resetType', sortable: false, edit:true,editOption:{type:'select',formatoption:true,result: EFFECTIVE_RULE}}
              ,{field:'indexId',title: 'BussBenchMarkInfo.indexId', visible: false}
              ,{field:'coefficient',title: 'BussBenchMarkInfo.coefficient', visible: false}
              ,{field:'spreadRate',title: 'BussBenchMarkInfo.spreadRate', visible: false}
              ,{field:'rateLower', width:150,title: 'BussBenchMarkInfo.rateLower', visible: false}
              ,{field:'rateLimit', width:150,title: 'BussBenchMarkInfo.rateLimit', visible: false}
              ,{field:'lowLimitRate', width:200,title: 'FinProductBmQuotation001.lowLimitRate'}
              ,{field:'operation', title: 'SysCommon.operation',type:'action', buttons: ['delt']}
          ],
          done : function(table, allData){
              //设置都不可编辑
               var allrowData =  bussBenchMarkInfoTable.options.cols;
               for(var i in allrowData){
                     allrowData[i].edit = false;
               }
          }
    });
    
    // 新增按钮点击事件
    //点击“新增”按钮，在业绩基准信息列表中展示该条信息，并重置上方要素信息
	getElementById('btn_create').click(function() {
	    var tableArry = bussBenchMarkInfoTable.getAllRows();
	    var flag = false;
	    //TISHKS-1385   多固定期限周期型产品 1：字段不能为空，2：不能重复 start
        if($('#' + dealModeElement).val() === DISPOSE_PATTERN_10){
            if($('#' + profitType).val() === PROFIT_TYPE_01 && $('#' + periodElement).val() === TERM_TYPE_02){
                if((!getElementById('investmentCycle').val() || getElementById('investmentCycle').val() =='')){
                    Joyin.error(JoyinUtil.getMessage("多固定期限周期型产品, 投资周期不能为空！"));
                    return;
                }
                if(tableArry && tableArry.length > 0){
                    _.each(tableArry,function (data){
                        if(getElementById('investmentCycle').val() && getElementById('investmentCycle').val() == data.investmentCycle){
                            Joyin.error(JoyinUtil.getMessage("列表中存在相同投资周期,不允许保存"));
                            flag = true;
                            return;
                        }
                    })
                    if(flag){
                        return;
                    }
                }
            }
        }
        //TISHKS-1385   多固定期限周期型产品 1：字段不能为空，2：不能重复 end
	    if(tableArry && tableArry.length > 0){
	    	if($('#' + dealModeElement).val() === DISPOSE_PATTERN_11){
	    		if((!getElementById('prodStyle').val() || getElementById('prodStyle').val() =='')){
	                Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_005"));
	                return;
	            }
		        _.each(tableArry,function (data){
		            if(getElementById('prodStyle').val() && getElementById('prodStyle').val() == data.prodStyle){
		                Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_006"));
		                flag = true;
		                return;
		            }
		        })
	    	}
	    	else if($('#' + dealModeElement).val() === DISPOSE_PATTERN_10){

	    		if((!getElementById('investmentCycle').val() || getElementById('investmentCycle').val() =='')){
	                Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_003"));
	                return;
	            }
		        _.each(tableArry,function (data){
		            if(getElementById('investmentCycle').val() && getElementById('investmentCycle').val() == data.investmentCycle){
		                Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_003"));
		                flag = true;
		                return; 
		            }
		        })
	    	}
	        if(flag){
	            return;
	        }
	    }
	    if(!getElementById('publishType').val()){
	    	Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_004",JoyinUtil.getLabel("BussBenchMarkInfo.publishType")));
	    	return;
	    }
	    var lowLimitRate = '';
	    if((getElementById('publishType').val() === PUBLISH_TYPE_01)){
	    	lowLimitRate = getElementById('rateLower').val() + '%~' + getElementById('rateLimit').val() + '%';
	    }
	    var floatRate = '';
	    if(getElementById('publishType').val() === PUBLISH_TYPE_02 && getElementById('rateType').val() === '02') {
	    	if(!getElementById('rateType').val()){
	    		Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_004",JoyinUtil.getLabel("BussBenchMarkInfo.rateType")));
		    	return;
	    	}
	    	if(!getElementById('indexId').val()){
	    		Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_004",JoyinUtil.getLabel("BussBenchMarkInfo.indexId")));
		    	return;
	    	}
	    	if(!getElementById('coefficient').val()){
	    		Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_004",JoyinUtil.getLabel("BussBenchMarkInfo.coefficient")));
		    	return;
	    	}
	    	if(!getElementById('spreadRate').val()){
	    		Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_004",JoyinUtil.getLabel("BussBenchMarkInfo.spreadRate")));
		    	return;
	    	}
	    	if(!getElementById('resetType').val()){
	    		Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_004",JoyinUtil.getLabel("BussBenchMarkInfo.resetType")));
		    	return;
	    	}
		var row = {
				publishType: getElementById('publishType').val(),
				prodStyle: getElementById('prodStyle').val(),
				investmentCycle:getElementById('investmentCycle').val(),
				rateType: getElementById('rateType').val(),
				rate: getElementById('rate').val(),
				floatRate: floatRate,
				resetType: getElementById('resetType').val(),
				indexId: getElementById('indexId').val(),
				coefficient: getElementById('coefficient').val(),
				spreadRate: getElementById('spreadRate').val(),
				rateLower: getElementById('rateLower').val(),
				rateLimit: getElementById('rateLimit').val(),
				lowLimitRate: lowLimitRate,
				vdate: $('#FinNetValueProductBuild002_vdate').val()
    		};
	    Joyin.ajaxJson(basePath + 'component/prod/BussBenchMarkLayeredInfoCommon/getFloatRate', row, function(result) {
			floatRate = result;
			row = {
					publishType: getElementById('publishType').val(),
					prodStyle: getElementById('prodStyle').val(),
					investmentCycle:getElementById('investmentCycle').val(),
					rateType: getElementById('rateType').val(),
					rate: getElementById('rate').val(),
					floatRate: floatRate,
					resetType: getElementById('resetType').val(),
					indexId: getElementById('indexId').val(),
					coefficient: getElementById('coefficient').val(),
					spreadRate: getElementById('spreadRate').val(),
					rateLower: getElementById('rateLower').val(),
					rateLimit: getElementById('rateLimit').val(),
					lowLimitRate: lowLimitRate
	    		};
	        	bussBenchMarkInfoTable.addRow(row);  
	       
	    	getElementById('publishType').val('').trigger('change');
	        getElementById('prodStyle').val('').trigger('change');
	        getElementById('rateType').val('').trigger('change');
	        getElementById('rate').val('').trigger('change');
	        getElementById('indexId').val('').trigger('change');
	        getElementById('coefficient').val('').trigger('change');
	        getElementById('spreadRate').val('').trigger('change');
	        getElementById('resetType').val('').trigger('change');
	        getElementById('rateLower').val('').trigger('change');
	        getElementById('rateLimit').val('').trigger('change');
	        getElementById('investmentCycle').val('').trigger('change');
	        
		});
	    }
	    else{
	    	if(getElementById('publishType').val() === PUBLISH_TYPE_02){
		    	if(!getElementById('rateType').val()){
		    		Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_004",JoyinUtil.getLabel("BussBenchMarkInfo.rateType")));
			    	return;
		    	}
		    	if(!getElementById('rate').val()){
		    		Joyin.error(JoyinUtil.getMessage("FIN_PROD_M_004",JoyinUtil.getLabel("BussBenchMarkInfo.rate")));
			    	return;
		    	}
	    	}
			var row = {
					publishType: getElementById('publishType').val(),
					prodStyle: getElementById('prodStyle').val(),
					investmentCycle:getElementById('investmentCycle').val(),
					rateType: getElementById('rateType').val(),
					rate: getElementById('rate').val(),
					floatRate: floatRate,
					resetType: getElementById('resetType').val(),
					indexId: getElementById('indexId').val(),
					coefficient: getElementById('coefficient').val(),
					spreadRate: getElementById('spreadRate').val(),
					rateLower: getElementById('rateLower').val(),
					rateLimit: getElementById('rateLimit').val(),
					lowLimitRate: lowLimitRate
	    		};
        	bussBenchMarkInfoTable.addRow(row);  
 	       
	    	getElementById('publishType').val('').trigger('change');
	        getElementById('prodStyle').val('').trigger('change');
	        getElementById('rateType').val('').trigger('change');
	        getElementById('rate').val('').trigger('change');
	        getElementById('indexId').val('').trigger('change');
	        getElementById('coefficient').val('').trigger('change');
	        getElementById('spreadRate').val('').trigger('change');
	        getElementById('resetType').val('').trigger('change');
	        getElementById('rateLower').val('').trigger('change');
	        getElementById('rateLimit').val('').trigger('change');
	        getElementById('investmentCycle').val('').trigger('change');
	    }
	    
	})
	
	/**
	 * 绑定删除事件
	 */
	bussBenchMarkInfoTable.bind('click', '.delt', function(data, index) {
		bussBenchMarkInfoTable.removeRow(data);
	});
	
	 var benchMarkInitAction = function(input_finprodid){
	     
	     getElementById('finprodId').val(input_finprodid).trigger("change");
	     bussBenchMarkInfoTable.load();
	 }
	 
	 var benchMarkInitActionTmpl = function(bussBenchMarkInfoList) {
	    if(bussBenchMarkInfoTable) {
	    	_.each(bussBenchMarkInfoList, function(rowData) {
	    		bussBenchMarkInfoTable.addRow(rowData);
	    	});
	    }
	 }
	 window.BusslayerBenchMarkInfo = {
             benchMarkInitAction : benchMarkInitAction,
             benchMarkInitActionTmpl : benchMarkInitActionTmpl
     }
	/**
     * 产品处理模式联动事件
     */
    var dealModeChange = function (){
        //收益类型为净值型 且产品处理模式为分档 改界面显示  其他场景不显示
        if($('#' + profitType).val() === PROFIT_TYPE_01 && $('#' + dealModeElement).val() === DISPOSE_PATTERN_11){
            getElementById('div').show();
    	    getElementById('prodStyle').val("").parent().parent().show();
    	    getElementById('investmentCycle').val("").parent().parent().hide();
    	    if(bussBenchMarkInfoTable.table){
    	    	bussBenchMarkInfoTable.reload();
			}else{
				bussBenchMarkInfoTable.load();
			}
    	    bussBenchMarkInfoTable.showColumns([{field:'prodStyle', visible: true},{field:'investmentCycle', visible: false}]);
        } 
        else {
            getElementById('rateType').val('').trigger('change');
            getElementById('resetType').val('').trigger('change');
            getElementById('prodStyle').empty();
            bussBenchMarkInfoTable.reload({data:[],url:null});
            getElementById('div').hide();
        }
//		getElementById('btn_create').click(function() {btnClick()});
    }
	 /**
	     * 期限联动事件
	     */
	    var periodChange = function (){
	        //期限为多固定期限展示，其他情况不展示
	    	if($('#' + dealModeElement).val() === DISPOSE_PATTERN_10){
	    		if($('#' + profitType).val() === PROFIT_TYPE_01 && $('#' + periodElement).val() === TERM_TYPE_02){
		    	    getElementById('prodStyle').val("").parent().parent().hide();
		    	    getElementById('investmentCycle').val("").parent().parent().show();
		            getElementById('div').show();
		            if(bussBenchMarkInfoTable.table){
		            	bussBenchMarkInfoTable.reload();
					}else{
						bussBenchMarkInfoTable.load();
					}
		    	    bussBenchMarkInfoTable.showColumns([{field:'prodStyle', visible: false},{field:'investmentCycle', visible: true}]);
		        } 
		        else {
		            getElementById('rateType').val('').trigger('change');
		            getElementById('resetType').val('').trigger('change');
		            getElementById('prodStyle').empty();
		            bussBenchMarkInfoTable.reload({data:[],url:null});
		            getElementById('div').hide();
		        }
	    	}
	    }
    $(function() {
    	getElementById('publishType').change(function() {
    		if(getElementById('publishType').val() === PUBLISH_TYPE_01) {
                getElementById('rateType').val('').trigger('change').prop('disabled', true);
                getElementById('rate').val('').trigger('change').prop('disabled', true);
                getElementById('indexId').val('').trigger('change').prop('disabled', true);
                getElementById('coefficient').val('').trigger('change').prop('disabled', true);
                getElementById('spreadRate').val('').trigger('change').prop('disabled', true);
                getElementById('resetType').val('').trigger('change').prop('disabled', true);
                getElementById('rateLower').val('').trigger('change').prop('disabled', false);
                getElementById('rateLimit').val('').trigger('change').prop('disabled', false);
            }
    		else{
    			getElementById('rateType').val('').trigger('change').prop('disabled', false);
                getElementById('rate').val('').trigger('change').prop('disabled', false);
                getElementById('indexId').val('').trigger('change').prop('disabled', false);
                getElementById('coefficient').val('').trigger('change').prop('disabled', false);
                getElementById('spreadRate').val('').trigger('change').prop('disabled', false);
                getElementById('resetType').val('').trigger('change').prop('disabled', false);
                getElementById('rateLower').val('').trigger('change').prop('disabled', true);
                getElementById('rateLimit').val('').trigger('change').prop('disabled', true);
    		}
    	});
        // 利率类型改变时触发的事件
        getElementById('rateType').change(function() {
            if(getElementById('rateType').val() === '') {
                getElementById('rate').val('').trigger('change').prop('disabled', false);
                getElementById('indexId').val('').trigger('change').prop('disabled', false);
                getElementById('coefficient').val('').trigger('change').prop('disabled', false);
                getElementById('spreadRate').val('').trigger('change').prop('disabled', false);
                getElementById('resetType').val('').trigger('change').prop('disabled', false);
            } else if(getElementById('rateType').val() === INT_TYPE_01) {
                getElementById('rate').trigger('change').prop('disabled', false);
                getElementById('indexId').val('').trigger('change').prop('disabled', true);
                getElementById('coefficient').val('').trigger('change').prop('disabled', true);
                getElementById('spreadRate').val('').trigger('change').prop('disabled', true);
                getElementById('resetType').val('').trigger('change').prop('disabled', true);
            } else {
                getElementById('rate').val('').trigger('change').prop('disabled', true);
                getElementById('indexId').trigger('change').prop('disabled', false);
                getElementById('coefficient').val('1.00').trigger('change').prop('disabled', false);
                getElementById('spreadRate').trigger('change').prop('disabled', false);
                getElementById('resetType').val('').trigger('change').prop('disabled', false);
            }
        });

        //产品处理模式联动事件
        dealModeChange();
        $('#' + dealModeElement).change(dealModeChange);
        //期限联动时间
        periodChange();
        $('#' + periodElement).change(periodChange);
    });
}());