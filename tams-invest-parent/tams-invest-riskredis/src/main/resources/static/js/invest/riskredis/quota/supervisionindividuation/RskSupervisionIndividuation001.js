/**
  * wangzehui
 */
(function(){
	var dataTable5_1;
	var dataTable5_2;
	var dataTable4;
	var dataTable3;
	var dataTable2;
	var dataTable1;
    var dataTable;
    //var index = 0;
    var flag = false;
    //定义删除的联合编号
    var deleteUniteId = '';
	
	/**
     * 获取查询参数
     */
	var getSearchParams = function() {
		var index = $('ul.layui-tab-title > li.layui-this').index();
		var indId = $('#indId').val()
		return {
			indId:indId,
			prtBagId: $('#prtBagId').val(),
			index:index,
			workflowId:$("#workflowId").val(),
			portfolioLevel:portfolioLevel
        }
    };
    
    /**
     * 定义数据表格 条件设置
     */
    var dataTable=$("#dataTable").joyintable({
    	ajax: {
            url: JoyinUtil.getRealPath('invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/findPageForCondition'),
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
    	//data:[],
        searchOnInit:true,
        height:'250',
        loadonce:true,
        //page:true,
        checkbox:false,
        cols:[
              {field: 'conditionId',visible: false}
            ,{field:'conditionItemStr', sortable:false,width:220 , title: 'RskSupervisionIndividuation001.ruleName'}//条件项
            ,{field:'controlTypeStr', sortable:false,width:220 , title: 'RskSupervisionIndividuation001.controlType'}//控制类型
            ,{field:'value', width:440 ,sortable:false, title: 'RskSupervisionIndividuation001.value',render: transferValue}//数值
            , {field: 'operation', width: 100, title: 'SysCommon.operation', type: 'action',
                buttons: ['delt','edit']
            }
        ]
    }); 
    /**
     * 定义数据表格 特殊阶段
     */
    var dataTable1=$("#dataTable1").joyintable({
    	ajax: {
    		url : JoyinUtil.getRealPath('invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/findPageForSpecial'),
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
    	//data:[],
        //addbtn:true,
        checkbox:false,
        searchOnInit:true,
        loadonce:true,
        height:'250',
        /*editCell : function(value, data, field, cell, column){
        	debugger;
        	if (field === "finprodId"){
        		var rowData = {finprodId:data.finprodId,delId:data.delId,startDate:data.startDate,endDate:data.endDate};
        		var str = $('#finprodId').select2('data')[0].object.split(',');
        		if(str&&str.length==1){
        			rowData.finprodName = str[0];
        		}
        		convertRow(rowData);
        	}
        	return;
        },*/
        cols:[
              {field: 'uniteId',visible: false}
              ,{field:'startDate', title: 'RskSupervisionIndividuation001.startDate', width:330,edit:false,type:'date', sortable: false}//开始时间
              ,{field:'endDate',title: 'RskSupervisionIndividuation001.endDate', width:320, edit:false,type:'date', sortable: false}//结束时间
             ,{field:'warnCond', width:200, title: 'RskSupervisionIndividuation001.warnCond', sortable: false,render: transferWarnFoul}//预警线
             ,{field:'approveCond', width:200, title: 'RskSupervisionIndividuation001.approveCond', sortable: false,render: transferWarnFoul}//审批线
             ,{field:'foulCond', width:200, title: 'RskSupervisionIndividuation001.foulCond', sortable: false,render: transferWarnFoul}//禁止线
            , {field: 'operation', width: 100, title: 'SysCommon.operation', type: 'action',
                buttons: ['delt','edit']
            }
        ]
    });
    
    var dataTable2 = $("#dataTable2").joyintable({
    	ajax: {
    		url : JoyinUtil.getRealPath('invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/findPageForSpecialAsset'),
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        checkbox:false,
        searchOnInit:true,
        loadonce:true,
        height:'250',
        
        cols:[
              {field: 'uniteId',visible: false}
              ,{field:'finprodId', width:200, title: 'RskSupervisionIndividuation001.finprodId', sortable: false}//资产代码
               ,{field:'finprodName', sortable:false,width:120 , title: 'RskSupervisionIndividuation001.finprodName'}//资产名称
              ,{field:'startDate', title: 'RskSupervisionIndividuation001.startDate', width:330,edit:false,type:'date', sortable: false}//开始时间
              ,{field:'endDate',title: 'RskSupervisionIndividuation001.endDate', width:320, edit:false,type:'date', sortable: false}//结束时间
             ,{field:'warnCond', width:200, title: 'RskSupervisionIndividuation001.warnCond', sortable: false,render: transferWarnFoul}//预警线
             ,{field:'approveCond', width:200, title: 'RskSupervisionIndividuation001.approveCond', sortable: false,render: transferWarnFoul}//审批线
             ,{field:'foulCond', width:200, title: 'RskSupervisionIndividuation001.foulCond', sortable: false,render: transferWarnFoul}//禁止线
            , {field: 'operation', width: 100, title: 'SysCommon.operation', type: 'action',
                buttons: ['delt','edit']
            }
        ]
    })
    /**
     * 定义数据表格 条件设置
     */
    var dataTable3=$("#dataTable3").joyintable({
    	ajax: {
    		url : JoyinUtil.getRealPath('invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/findPageForAssetDel'),
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
    	//data:[],
        addbtn:true,
        checkbox:false,
        searchOnInit:true,
        loadonce:true,
        height:'250',
        editCell : function(value, data, field, cell, column){
        	if (field === "finprodId"){
        		var rowData = {finprodId:value,delId:data.delId,startDate:data.startDate,endDate:data.endDate};
        		//var str = $('#finprodId').select2('data')[0].object.split(',');
        		/*if(str&&str.length==1){
        			rowData.finprodName = str[0];
        		}*/
        		var rows = dataTable3.getAllRows();
                var finprodIdMap = rows.map(function(i) {return i.finprodId;});
                for(var i=0;i<finprodIdMap.length;i++){
                	if(finprodIdMap[i]==value&&i!=data.LAY_TABLE_INDEX){
                		JoyinUtil.showMessage("资产不能重复选择", 'error', function() {
                            dataTable3.deleteRow(data.LAY_TABLE_INDEX);
                        });
                        return;
                	}
                }
        		var finprodName=$('td[data-field="finprodId"]').children().next().next().children().children().children().attr('title');
				// var finprodName = $("#finprodId").next().next().children().children().children().text();
        		//finprodName=finprodName.substring(1,finprodName.length);
        		rowData.finprodName = finprodName;
        		dataTable3.renderRow(data.LAY_TABLE_INDEX, rowData);
        	}
        	if (field === "endDate" || field === "startDate"){
          		var startDate = data.startDate;
          		var endDate = data.endDate;
          		if(startDate && endDate){
          			startDate = startDate.format('yyyy-mm-dd');
          			endDate = endDate.format('yyyy-mm-dd');
          			if(startDate>endDate){
          				JoyinUtil.showMessage(JoyinUtil.getMessage('RSKRDS_E_0060'), 'error', function() {
          					dataTable3.deleteRow(data.LAY_TABLE_INDEX);
                  		});
              			return;
          			}
          		}
          	}
        	return;
        },
        cols:[
              {field: 'delId',visible: false}
             ,{field:'finprodId', width:300, title: 'RskSupervisionIndividuation001.finprodId', sortable: false,edit:true,
              	editOption:{id:'finprodId',type:'select',showField:'id',formatoption:true,page:true,
              		url: JoyinUtil.getRealPath("invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/queryFinprodIdList?indId="+$("#indId").val()+"&prtBagId"+$('#prtBagId').val())
              	}
             }//资产代码
             ,{field:'finprodName', sortable:false,width:250 , title: 'RskSupervisionIndividuation001.finprodName'}//资产名称
             ,{field:'startDate', title: 'RskSupervisionIndividuation001.startDate', width:230,edit:true,type:'date', sortable: false}//开始时间
             ,{field:'endDate',title: 'RskSupervisionIndividuation001.endDate', width:220, edit:true,type:'date', sortable: false}//结束时间
             , {field: 'operation', width: 100, title: 'SysCommon.operation', type: 'action',
                buttons: ['delt']
            }
        ],
    });
    function transferWarnFoul(data, type, row, meta){
    	var lnkNo="";
    	var wg="";var detail = "";
    	if(data==''){
    		return wg;
    	}
    	var detailJson = JSON.parse(data);
		if(detailJson!=null){
			/* 限制型 */
			detail = html2Escape(data);
			if($("#indType").val().indexOf("LIMIT_")!=-1){
				if(detailJson.threshold_1.value!=null&&detailJson.threshold_1.value!=''){
					wg = detailJson.threshold_1.op+detailJson.threshold_1.value+valueConvertUnit(detailJson.threshold_1.unit);
				}
				
			}else{
				/* 禁止型 */
				wg = detailJson.threshold_1.value_cn;
				wg=wg.substring(0,wg.length-1);
			}
		}
		return wg;
    }
    /**
     * 单位转换
     */
    function valueConvertUnit(value){
    	if(value==''){
    		return value;
    	}
    	var retStr;
    	var supUnitArr = JSON.parse(SUP_UNIT);
    	$.each(supUnitArr,function(i,v){
    		if(v.id==value){
    			if(value==NONE){
    				retStr = '';
    			}else{
    				retStr=v.text;
    			}
    			return false;
    		}
    	})
    	return retStr;
    };
    function StringToChinese(id,type){
    	var arr=[];
    	var totalIndTypeEnum=JSON.parse(IND_TYPE_TOTAL_ENUM);
    	//var arrFobEnum=JSON.parse(relateIndType).filter(item => item.id===type);
    	var arrFobEnum=_.filter(JSON.parse(relateIndType),function(item){return item.id===type});
    	if(null!=totalIndTypeEnum&&null!=arrFobEnum){
    		//arr=totalIndTypeEnum.filter(item => item.id===id&&item.col===arrFobEnum[0].text)
    		arr=_.filter(totalIndTypeEnum,function(item){return item.id===id&&item.col===arrFobEnum[0].text})
    	}
    	if(arr==[]||arr.length==0)
    		return id;
    	else
    		return arr[0].text;
	};
    /* 普通字符转换成转意符 */
	var html2Escape = function(sHtml){
		return sHtml.replace(/[<>&"]/g,function(c){return {'<':'&lt;','>':'&gt;','&':'&amp;','"':'&quot;'}[c];});
	};
    /**
     * 定义数据表格 条件设置
     */
    var dataTable4=$("#dataTable4").joyintable({
    	ajax: {
    		url : JoyinUtil.getRealPath('invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/findPageForUnite'),
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
    	//data:[],
        addbtn:true,
        checkbox:false,
        searchOnInit:true,
        loadonce:true,
        height:'250',
        editCell: function(value, data, field, cell, column){
                if (field === "uniteIndId"){
                    var rows = dataTable4.getAllRows();
                    var finprodIdMap = rows.map(function(i) {return i.uniteIndId;});
                    for(var i=0;i<finprodIdMap.length;i++){
                    	if(finprodIdMap[i]==value&&i!=data.LAY_TABLE_INDEX){
                    		JoyinUtil.showMessage("联合设置不能重复选择", 'error', function() {
                                dataTable4.deleteRow(data.LAY_TABLE_INDEX);
                            });
                            return;
                    	}
                    }

                }
                return;
            },
        cols:[
              {field: 'uniteId',visible: false}
             ,{field:'uniteIndId', width:500, title: 'RskSupervisionIndividuation001.uniteType', sortable: false,edit:true,
              	editOption:{id:'uniteIndId',multiple:true,type:'select',formatoption:true,showField:'text',initValue:$('#uniteIndId').val(),
              		result:UNITE_SOURCE
              	}
             }
            ,{field:'remark', sortable:false,width:220 ,edit:true,  title: 'RskSupervisionIndividuation001.remark'}//备注
            , {field: 'operation', width: 100, title: 'SysCommon.operation', type: 'action',
                buttons: ['delt']
            }
        ]
    });

	var dataTable5_1 = $("#dataTable5_1").joyintable({
		ajax: {
			url : JoyinUtil.getRealPath('invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/findPageForCustomized'),
			type: 'POST',
			data: function() {
				return getSearchParams();
			}
		},
		checkbox:false,
		searchOnInit:true,
		loadonce:true,
		height:'250',
		cols:[
			{field: 'uniteId',visible: false}
			,{field:'conditionItem', width:200, title: 'RskSupervisionIndividuation001.conditionItem', sortable: false} //条件代码
			,{field:'conditionName', sortable:false,width:120 , title: 'RskSupervisionIndividuation001.conditionName'}//条件名称
			,{field:'conditionUnitStr', title: 'RskSupervisionIndividuation001.conditionUnit', width:330,sortable: false}//单位
			,{field:'conditionUnit', visible: false}//单位
			,{field:'conditionType',title: 'RskSupervisionIndividuation001.conditionType', width:320, sortable: false}//类型
			, {field: 'operation', width: 100, title: 'SysCommon.operation', type: 'action',buttons: ['delt','edit']}
		]
	})
	function isJSON(str) {
		if (typeof str == 'string') {
			try {
				JSON.parse(str);
				return true;
			} catch(e) {
				return false;
			}
		}
		return false;
	}
	function  parseValue (data, type, row, meta) {
		if (isJSON(data) && JSON.parse(data).threshold_1 !== undefined){
			return JSON.parse(data).threshold_1.value
		} else {
			return data
		}
	}
	var dataTable5_2 = $("#dataTable5_2").joyintable({
		ajax: {
			url : JoyinUtil.getRealPath('invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/findPageForCustomizedItem'),
			type: 'POST',
			data: function() {
				return getSearchParams();
			}
		},
		checkbox:false,
		searchOnInit:true,
		loadonce:true,
		addbtn:true,
		height:'250',
		cols:[
			{field: 'uniteId',visible: false}
			,{field:'conditionItem', width:200, title: 'RskSupervisionIndividuation001.ruleName', sortable: false,edit:true,
				editOption:{id:'conditionItem',type:'select',showField:'text',formatoption:true,page:true,
					url: JoyinUtil.getRealPath("invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/queryConditionList")
				}
			} //条件项
			,{field:'controlType', sortable:false,width:120 , title: 'RskSupervisionIndividuation001.controlType',edit:true
				,editOption:{type:'select',id:"controlType",formatoption:true,result: CONTROL_TYPE}}//控制类型
			,{field:'valueStr', title: 'RskSupervisionIndividuation001.valueStart', width:200,sortable: false,edit:true}//数值
			,{field:'valueEnd', title: 'RskSupervisionIndividuation001.valueEnd', width:150,sortable: false,edit:true}//数值
			,{field:'isBagCalc',title: 'RskSupervisionIndividuation001.isBagCalc', width:200, sortable: false,edit:true
				,editOption:{type:'select',id:"isBagCalc",formatoption:true,result: YES_OR_NO}}//是否计算组合包
			,{field:'warnCond', width:200, title: 'RskSupervisionIndividuation001.warnCond', type: 'number', sortable: false,edit:true,render: parseValue}//预警线
			,{field:'approveCond', width:200, title: 'RskSupervisionIndividuation001.approveCond', type: 'number',sortable: false,edit:true,render: parseValue}//审批线
			,{field:'foulCond', width:200, title: 'RskSupervisionIndividuation001.foulCond', type: 'number',sortable: false,edit:true,render: parseValue}//禁止线
			,{field: 'operation', width: 100, title: 'SysCommon.operation', type: 'action', fixed:true,
				buttons: ['delt']
			}
		]
	})
    
    /*var load = function(){
    	debugger;
    	
   	}*/
    
    /**
     * 绑定编辑事件
     */
    dataTable.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        data.indId = $('#indId').val();
        data.prtBagId = $("#prtBagId").val();
        data.VDATE=VDATE;
        if(data.valueEnd==null){
        	data.valueEnd="";
        }
        Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation002.edit"),
            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation002/init",
            "80%", "80%",data,function(result) {
        					if(result){
        						var row = {conditionItemStr:result.conditionItemStr,controlTypeStr:result.controlTypeStr,conditionId:result.conditionId,
										valueStr:result.valueStr,valueEnd:result.valueEnd,conditionItem:result.conditionItem,controlType:result.controlType,
										dateStr:result.dateStr,dateEnd:result.dateEnd,dateUnitStr:result.dateUnitStr,dateUnitEnd:result.dateUnitEnd,
										dateStrStr:result.dateStrStr,dateEndStr:result.dateEndStr,dateUnitStrStr:result.dateUnitStrStr,dateUnitEndStr:result.dateUnitEndStr};
								//var selArr = row];
								//setTimeout(function(){$('#dataTable').data("joyintable").addRow(selArr);},2000);
					        	dataTable.deleteRow(index);
					        	dataTable.addRow(row,index);
        					}
                            });
    });
    /**
     * 绑定编辑事件
     */
    dataTable1.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        data.indId = $('#indId').val();
        Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation003.edit"),
            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation003/init",
            "90%", "95%",data,function(result) {
        						if(result){
        							var row = {uniteId:result.uniteId,startDate:result.startDate,endDate:result.endDate,warnCond:result.warnCond,
                	    					approveCond:result.approveCond,foulCond:result.foulCond};
        							dataTable1.deleteRow(index);
    					        	dataTable1.addRow(row,index);
        						}
					        	
                            });
    });
    /**
     * 绑定编辑事件
     */
    dataTable2.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        data.indId = $('#indId').val();
        Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation004.edit"),
            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation004/init",
            "90%", "95%",data,function(result) {
        						if(result){
        							var row = {finprodId:result.finprodId,finprodName:result.finprodName,uniteId:result.uniteId,startDate:result.startDate,endDate:result.endDate,warnCond:result.warnCond,
                	    					approveCond:result.approveCond,foulCond:result.foulCond};
        							dataTable2.deleteRow(index);
    					        	dataTable2.addRow(row,index);
        						}
					        	
                            });
    });
    /**
     * 
     * 绑定删除事件
     */
    dataTable.bind('click', '.delt', function(data, index) {
		var processModel = data.processModel;
		if(processModel == PROCESSMODEL_RULE){
			Joyin.error(JoyinUtil.getMessage("RSKRDS_W_0079"));
			return;
		}else{
			dataTable.deleteRow(index);
		}
    });
    /**
     * 绑定删除事件
     */
    dataTable1.bind('click', '.delt', function(data, index) {
		var processModel = data.processModel;
		if(processModel == PROCESSMODEL_RULE){
			Joyin.error(JoyinUtil.getMessage("RSKRDS_W_0079"));
			return;
		}else{
			dataTable1.deleteRow(index);
		}
    });
    /**
     * 绑定删除事件
     */
    dataTable2.bind('click', '.delt', function(data, index) {
		var processModel = data.processModel;
		if(processModel == PROCESSMODEL_RULE){
			Joyin.error(JoyinUtil.getMessage("RSKRDS_W_0079"));
			return;
		}else{
			dataTable2.deleteRow(index);
		}
    });
    /**
     * 绑定删除事件
     */
    dataTable3.bind('click', '.delt', function(data, index) {
		var processModel = data.processModel;
		if(processModel == PROCESSMODEL_RULE){
			Joyin.error(JoyinUtil.getMessage("RSKRDS_W_0079"));
			return;
		}else{
			dataTable3.deleteRow(index);
		}
    });
    /**
     * 绑定删除事件
     */
    dataTable4.bind('click', '.delt', function(data, index) {
		var processModel = data.processModel;
		if(processModel == PROCESSMODEL_RULE){
			Joyin.error(JoyinUtil.getMessage("RSKRDS_W_0079"));
			return;
		}else{
			dataTable4.deleteRow(index);
		}
    });
	/**
	 * 绑定删除事件
	 */
	dataTable5_1.bind('click', '.delt', function(data, index) {
		data.operateType = 'delete';
		Joyin.ajaxJson(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation007/check', data, function(result) {
			Joyin.confirm(result.data, function() {
				Joyin.ajaxJson(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation007/save', data, function(result) {
					dataTable5_1.reload();
				});
			});
		});
	});
	/**
	 * 绑定删除事件
	 */
	dataTable5_1.bind('click', '.edit', function(data, index) {
		data.pageModel = PAGE_MODEL_EDIT;
		data.operateType = 'edit';
		Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation007"),
			basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation007/init",
			"90%", "95%", data, function (result) {
				flag = true;
			}, function() {
				dataTable5_1.reload();
			}
		);
		operateType = 'edit'

	});
	/**
	 * 绑定删除事件
	 */
	dataTable5_2.bind('click', '.delt', function(data, index) {
		var processModel = data.processModel;
		if(processModel == PROCESSMODEL_RULE){
			Joyin.error(JoyinUtil.getMessage("RSKRDS_W_0079"));
			return;
		}else{
			dataTable5_2.deleteRow(index);
		}
	});
    /**
     * 绑定编辑事件
     *//*
    dataTable3.bind('click', '.edit', function(data, index) {
        data.pageModel=PAGE_MODEL_EDIT;
        data.operateType='edit'
    	Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation003"),
	            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation003/init",
	            "90%", "95%", data, function (result) {
	            	flag = true;
	            	
	    			var row = {uniteId:result.uniteId,startDate:result.startDate,endDate:result.endDate,warnCond:result.warnCond,
	    					approveCond:result.approveCond,foulCond:result.foulCond};
	    			dataTable2.addRow(row);
	            });
        });
        
    *//**
     * 绑定编辑事件
     *//*
    dataTable4.bind('click', '.edit', function(data, index) {
        data.pageModel=PAGE_MODEL_EDIT;
        data.operateType='edit'
        	Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation004"),
    	            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation004/init",
    	            "90%", "95%", data, function (result) {
    	            	flag = true;
    	            	
    	    			var row = {finprodId:result.finprodId,finprodName:result.finprodName,uniteId:result.uniteId,startDate:result.startDate,endDate:result.endDate,warnCond:result.warnCond,
    	    					approveCond:result.approveCond,foulCond:result.foulCond};
    	    			//var selArr = row];
    	    			//setTimeout(function(){$('#dataTable').data("joyintable").addRow(selArr);},2000);
    	    			dataTable.addRow(row);
    	            	//table.reload({data:selArr});
    	            });
        
    });*/
    function transferValue(data, type, row, meta){
		//启用时间
		if(row.conditionItem==CONDITION_ITEM_04){
			var startStr = row.dateStrStr + "前" + row.valueStr + "个" + row.dateUnitStrStr;
			var endStr = row.dateEndStr + "后" + row.valueEnd + "个" + row.dateUnitEndStr

			if(row.valueStr && row.valueEnd){
				return startStr + "至" + endStr;
			}else{
				if(row.valueStr){
					return startStr + "开始启用";
				}else if(row.valueEnd){
					return "至" + endStr + "停止使用";
				}
			}
		}else{
			if(row.valueEnd){
				if(row.conditionItem==CONDITION_ITEM_03){
					var dStr = new Date(parseInt(row.valueStr));
					var dEnd = new Date(parseInt(row.valueEnd));
					return dStr.format("yyyy-MM-dd")+"-"+dEnd.format("yyyy-MM-dd")
				}else if(row.conditionItem==CONDITION_ITEM_02){
					return row.valueStr+"元"+"-"+row.valueEnd+"元";
				}
				return row.valueStr+"万元"+"-"+row.valueEnd+"万元";
			}else{
				if(row.conditionItem==CONDITION_ITEM_02){
					return row.valueStr+"元";
				}
				return row.valueStr+"万元";
			}
		}
		return "";
    }
	/*
      初始化单位与阈值方向
    */
	var initUnitOrDirection=function(){
		var warnDetail=$('#zdytjfzDiv').find('[id="oldWarnCond"]').val();
		var warnDetailJson = warnDetail==''?'':JSON.parse(warnDetail);
		var foulDetail=$('#zdytjfzDiv').find('[id="oldFoulCond"]').val();
		var foulDetailJson = foulDetail==''?'':JSON.parse(foulDetail);
		var approveDetail=$('#zdytjfzDiv').find('[id="oldApproveCond"]').val();
		var approveDetailJson = approveDetail==''?'':JSON.parse(approveDetail);
		var comparison = '';
		var unit = '';
		//预警
		if(warnDetailJson.threshold_1.value!=null&&warnDetailJson.threshold_1.value!=''){
			comparison = warnDetailJson.threshold_1.op;
			unit = warnDetailJson.threshold_1.unit;
		}
		//审批
		if(approveDetailJson.threshold_1.value!=null&&approveDetailJson.threshold_1.value!=''){
			comparison = approveDetailJson.threshold_1.op;
			unit =  approveDetailJson.threshold_1.unit;
		}
		//禁止
		if(foulDetailJson.threshold_1.value!=null&&foulDetailJson.threshold_1.value!=''){
			comparison = foulDetailJson.threshold_1.op;
			unit =  foulDetailJson.threshold_1.unit;
		}
		$('#zdytjfzDiv').find('[id="comparison"]').val(comparison).trigger('change').attr("disabled",true);
		$('#zdytjfzDiv').find('[id="unit"]').val(unit).trigger('change').attr("disabled",true);
	}
    var init = function(){
		initUnitOrDirection();
    	//debugger;
    	/*Joyin.ajax(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/findPage', getSearchParams(), function(result) {
    		if(result){
    			if(result.conditionList){
    				for(var i=0;i<result.conditionList.length;i++){
    					var condition = result.conditionList[i];
    					var row = {conditionItemStr:condition.conditionItemStr,controlTypeStr:condition.controlTypeStr,conditionId:condition.conditionId,
    	    					valueStr:condition.valueStr,valueEnd:condition.valueEnd,conditionItem:condition.conditionItem,controlType:condition.controlType};
    					
    					setTimeout(function(){dataTable.addRow(row);},1000);
    				}
    				
    			}
    			if(result.uniteList){
    				for(var i=0;i<result.uniteList.length;i++){
    					var unite = result.uniteList[i];
    					var row = {uniteId:unite.uniteId,uniteIndId:unite.indId,remark:unite.remark};
    					setTimeout(function(){dataTable4.addRow(row);},1000);
    				}
    				
    			}
    		}
    	})*/
    	//load();
    }
    /**
     * 保存或提交
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
    	/**
    	 * 获取列表参数
    	 */
    	var object = $.serializeToObject('#submitForm', true);
    	object.prtBagId = $("#prtBagId").val();
    	object.indId = $("#indId").val();
    	object.indType = $("#indType").val();
    	//条件设置
    	if(dataTable!=null && dataTable!=''){
            var selArr = dataTable.getAllRows();
        }
    	if(selArr && selArr.length!=0){
    		/*for(var i=0;i<selArr.length;i++){
    			if(selArr[i].conditionItem==CONDITION_ITEM_03){
    				var dStr = new Date(selArr[i].valueStr);
    				selArr[i].valueStr = dStr.getTime();
    				var dEnd = new Date(selArr[i].valueEnd);
    				selArr[i].valueEnd = dEnd.getTime();
    			}
    		}*/
    		object.conditionList = selArr;
    	}
    	object.pageId = "RskSupervisionIndividuation001";
        object.pageModel = pageModel;
    	//特殊阶段
    	if(dataTable1!=null && dataTable1!=''){
            var selArr1 = dataTable1.getAllRows();
        }
    	if(selArr1 && selArr1.length!=0){
    		object.specialStageList = selArr1;
    	}
    	
    	if(dataTable2!=null && dataTable2!=''){
            var selArr2 = dataTable2.getAllRows();
        }
    	if(selArr2 && selArr2.length!=0){
    		object.specialAssetList = selArr2;
    	}
    	
    	if(dataTable3!=null && dataTable3!=''){
            var selArr3 = dataTable3.getAllRows();
        }
    	if(selArr3 && selArr3.length!=0){
    		for(var i=0;i<selArr3.length;i++){
    			if(!selArr3[i].finprodId||selArr3[i].finprodId==""){
    				Joyin.error("剔除资产:证券代码不能为空!");
    				 return;
    			}
    			if(!selArr3[i].finprodName||selArr3[i].finprodName==""){
    				Joyin.error("剔除资产:证券名称不能为空!");
    				 return;
    			}
    			if(!selArr3[i].startDate||selArr3[i].startDate==""){
    				Joyin.error("剔除资产:开始时间不能为空!");
    				 return;
    			}
    			if(!selArr3[i].endDate||selArr3[i].endDate==""){
    				Joyin.error("剔除资产:结束时间不能为空!");
    				 return;
    			}
    			if(selArr3[i].endDate<selArr3[i].startDate){
    				Joyin.error("剔除资产:开始时间不能大于结束时间!");
    				return;
    			}
    		}
    		object.assetDelList = selArr3;
    	}
    	if(dataTable4!=null && dataTable4!=''){
            var selArr4 = dataTable4.getAllRows();
        }
    	if(selArr4 && selArr4.length!=0){
    		for(var i=0;i<selArr4.length;i++){
    			if(!selArr4[i].uniteIndId||selArr4[i].uniteIndId==""){
    				Joyin.error("联合设置:指标名称不能为空!");
    				 return;
    			}
    		}
    		/*for(var i=0;i<selArr4.length;i++){
    			selArr4[i].uniteIndId = selArr4[i].uniteIndId.split(',');;
    		}*/
    		object.uniteList = selArr4;
    	}
    	if (dataTable5_2 != null && dataTable5_2 !== ''){
			var selArr5 = dataTable5_2.getAllRows();
		}
		if(selArr5 && selArr5.length !== 0){
			var warnDetail=$('#zdytjfzDiv').find('[id="oldWarnCond"]').val();
			var warnDetailJson = warnDetail==''?'':JSON.parse(warnDetail);
			var foulDetail=$('#zdytjfzDiv').find('[id="oldFoulCond"]').val();
			var foulDetailJson = foulDetail==''?'':JSON.parse(foulDetail);
			var approveDetail=$('#zdytjfzDiv').find('[id="oldApproveCond"]').val();
			var approveDetailJson = approveDetail==''?'':JSON.parse(approveDetail);
			for(var i=0;i<selArr5.length;i++){
				if (isJSON(selArr5[i].warnCond) && JSON.parse(selArr5[i].warnCond).threshold_1 === undefined){
					warnDetailJson.threshold_1.value = selArr5[i].warnCond;
					selArr5[i].warnCond = JSON.stringify(warnDetailJson);
				}
				if (isJSON(selArr5[i].foulCond) && JSON.parse(selArr5[i].foulCond).threshold_1 === undefined){
					foulDetailJson.threshold_1.value = selArr5[i].foulCond;
					selArr5[i].foulCond = JSON.stringify(foulDetailJson);
				}
				if (isJSON(selArr5[i].approveCond) && JSON.parse(selArr5[i].approveCond).threshold_1 === undefined){
					approveDetailJson.threshold_1.value = selArr5[i].approveCond;
					selArr5[i].approveCond = JSON.stringify(approveDetailJson);
				}
			}
			object.conditionLines = selArr5;
		}
    	Joyin.ajaxJson(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/check', object, function(result) {
            Joyin.confirm(result.data, function() {
            	if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/save', object, "RskSupervisionIndividuation001_btn_save");
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/save', object, "RskSupervisionIndividuation001_btn_submit");
                }
            });
        });
    }
    $(function () {
    	
//    	//tab切换监听
//    	layui.use('element', function(){
//    		  var element = layui.element;
//    		  //一些事件监听
//    		  element.on('tab(docDemoTabBrief)', function(data){
//    			  index = data.index;
//    		  });
//    		});
    	init();
    	$("#RskSupervisionIndividuation001_btn_tjsz_add").click(function() {
    		Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation002"),
    	            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation002/init",
    	            "80%", "80%", {pageModel: PAGE_MODEL_NEW,indId: $('#indId').val(),prtBagId: $('#prtBagId').val(),VDATE:VDATE}, function (result) {
    	            	flag = true;
    	            	if(result){
    	            		var row = {conditionItemStr:result.conditionItemStr,controlTypeStr:result.controlTypeStr,conditionId:result.conditionId,
        	    					valueStr:result.valueStr,valueEnd:result.valueEnd,conditionItem:result.conditionItem,controlType:result.controlType,
									dateStr:result.dateStr,dateEnd:result.dateEnd,dateUnitStr:result.dateUnitStr,dateUnitEnd:result.dateUnitEnd,
									dateStrStr:result.dateStrStr,dateEndStr:result.dateEndStr,dateUnitStrStr:result.dateUnitStrStr,dateUnitEndStr:result.dateUnitEndStr};
        	    			//var selArr = row];
        	    			//setTimeout(function(){$('#dataTable').data("joyintable").addRow(selArr);},2000);
        	    			dataTable.addRow(row);
    	            	}
    	    			
    	            	//table.reload({data:selArr});
    	            });
        });
    	$("#RskSupervisionIndividuation001_btn_tsjd_add").click(function() {
    		Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation003"),
    	            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation003/init",
    	            "90%", "95%", {pageModel: PAGE_MODEL_NEW,indId: $('#indId').val(),prtBagId: $('#prtBagId').val()}, function (result) {
    	            	flag = true;
    	            	if(result){
    	            		var row = {uniteId:result.uniteId,startDate:result.startDate,endDate:result.endDate,warnCond:result.warnCond,
        	    					approveCond:result.approveCond,foulCond:result.foulCond};
    	            		dataTable1.addRow(row);
    	            	}
    	    			
    	            });
    	});
		$("#RskSupervisionIndividuation001_btn_zdytjfz_add").click(function() {
			Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation007"),
				basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation007/init",
				"90%", "95%", {pageModel: PAGE_MODEL_NEW,operateType:'save'}, function () {
					flag = true;
					dataTable5_1.reload();
				});
		});

    	$("#RskSupervisionIndividuation001_btn_tszqsz_add").click(function() {
    		if(IND_TYPE.indexOf("LIMIT_")!=-1){
    			Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation004"),
        	            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation004/init",
        	            "90%", "95%", {pageModel: PAGE_MODEL_NEW,indId: $('#indId').val(),prtBagId: $('#prtBagId').val()}, function (result) {
        	            	flag = true;
        	            	if(result){
        	            		var row = {finprodId:result.finprodId,finprodName:result.finprodName,uniteId:result.uniteId,startDate:result.startDate,endDate:result.endDate,warnCond:result.warnCond,
            	    					approveCond:result.approveCond,foulCond:result.foulCond};
            	    			//var selArr = row];
            	    			//setTimeout(function(){$('#dataTable').data("joyintable").addRow(selArr);},2000);
            	    			dataTable2.addRow(row);
        	            	}
        	    			
        	            	//table.reload({data:selArr});
        	            });
    		}else{
    			Joyin.warn(JoyinUtil.getMessage("RSKRDS_E_0061"));
                return;
    		}
    		
    	});
    	// 按钮事件
        $('#RskSupervisionIndividuation001_btn_save').click(function() {
            saveOrSubmit('save');
        });
     // 按钮事件
        $('#RskSupervisionIndividuation001_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
    });
}());