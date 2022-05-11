/**
 * 条件新建
 */
$(function() {
	 
    //var editable = pageModel != PAGE_MODEL_REFERENCE;
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
        object.pageId = "RskSupervisionIndividuation002";
        object.pageModel = pageModel;
        var controlType = $("#controlType").val();
        var conditionItem = $("#conditionItem").val();
        if(conditionItem==CONDITION_ITEM_03){
        	var eStr = new Date($("#valueStr3").val());
        	object.valueStr = eStr.getTime();
        	var eEnd = new Date($("#valueEnd3").val())
        	object.valueEnd = eEnd.getTime();
        }else if(conditionItem==CONDITION_ITEM_04){
			object.valueStr = $("#valueStr4").val();
			object.valueEnd = $("#valueEnd4").val();
		}else{
        	if(controlType!=CONTROL_TYPE_05){
        		object.valueStr = $("#valueStr2").val();
        		object.valueEnd = null;
        	}
        }
    	
    	object.conditionItemStr = $("#conditionItem").select2('data')[0].text;
    	object.controlTypeStr = $("#controlType").select2('data')[0].text;
		object.dateStrStr = $("#dateStr").select2('data')[0].text;
		object.dateEndStr = $("#dateEnd").select2('data')[0].text;
		object.dateUnitStrStr = $("#dateUnitStr").select2('data')[0].text;
		object.dateUnitEndStr = $("#dateUnitEnd").select2('data')[0].text;
    	top.returnData=object;
    	var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
      /*//判断是新建界面还是编辑界面
        if(pageModel===PAGE_MODEL_NEW){
            Joyin.ajaxJson(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation002/check',object, function(result) {
                Joyin.confirm(result.data, function() {
                    Joyin.ajaxJson(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation002/insert',
                        object,function(result){
                            //点击保存后关闭窗口
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        })
                    })
                })
            }
        else {
            Joyin.ajaxJson(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation002/check',object,function(result) {
                Joyin.confirm(result.data, function() {
                    Joyin.ajaxJson(basePath + 'invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation002/update',
                        object,function(result){
                            //点击保存后关闭窗口
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        })
                })
            })
        }*/
    }
    
    var init = function(){
	var wfl_operType = $("#wfl_operType").val();
   	if(pageModel==PAGE_MODEL_EDIT){
   		/*if(wfl_operType == null || wfl_operType != 'NEW'){
   			$('#rolePos').attr("disabled",true);
   			$('#userName').attr("disabled",true);
   		}*/
   	}
   	var controlType = $("#controlType").val();
	if(controlType!=CONTROL_TYPE_05){
		$("#valueSingleDiv").show();
		$("#valueRangeDiv").hide();
	}else{
		$("#valueSingleDiv").hide();
		$("#valueRangeDiv").show();
	}
	setUnit();
	setDateStrAttr();
	setDateEndAttr();
	var conditionItem = $("#conditionItem").val();
	if(conditionItem==CONDITION_ITEM_03&&entity!=null&&entity.valueStr!=null&&entity.valueEnd!=null){
		var dStr = new Date(parseInt(entity.valueStr));
		var dEnd = new Date(parseInt(entity.valueEnd));
		$("#valueStr3").val(dStr.format("yyyy-MM-dd")).trigger('change');
		$("#valueEnd3").val(dEnd.format("yyyy-MM-dd")).trigger('change');
	}
   };
	var setDateStrAttr = function(){
		var conditionItem = $("#conditionItem").val();
		var dateStr = $("#dateStr").val();
		var dateEnd = $("#dateEnd").val();
		if(conditionItem==CONDITION_ITEM_04){
			if(!dateStr){
				$("#valueStr4").val('').prop("disabled", true);
				$("#dateUnitStr").val('').attr("disabled",true).trigger('change');
			}else{
				if(!$("#valueStr4").val()){
					$("#valueStr4").val('0')
				}
				if(!$("#dateUnitStr").val()){
					$("#dateUnitStr").val(DIS_DATE_TYPE_02);
				}
				$("#valueStr4").prop("disabled", false);
				$("#dateUnitStr").attr("disabled",false).trigger('change');
			}
		}

		$("#dateEnd").empty();
		$("#dateEnd").append( $("<option>").val("").text(""));
		switch (dateStr) {
			case "01":
				$("#dateEnd").append(OPTION1).append(OPTION2).append(OPTION3);
				break;
			case "02":
				$("#dateEnd").append(OPTION2).append(OPTION3);
				break;
			case "03":
				$("#dateEnd").append(OPTION3);
				break;
			case "04":
				$("#dateEnd").append(OPTION4).append(OPTION5).append(OPTION6);
				break;
			case "05":
				$("#dateEnd").append(OPTION5).append(OPTION6);
				break;
			case "06":
				$("#dateEnd").append(OPTION6);
				break;
			default:
				$("#dateEnd").append(OPTION1).append(OPTION2).append(OPTION3).append(OPTION4).append(OPTION5).append(OPTION6);
		}
		$("#dateEnd").val(dateEnd)
	}
	var setDateEndAttr = function(){
		var conditionItem = $("#conditionItem").val();
		var dateStr = $("#dateStr").val();
		var dateEnd = $("#dateEnd").val();
		if(conditionItem==CONDITION_ITEM_04){
			if(!dateEnd){
				$("#valueEnd4").val('').prop("disabled", true);
				$("#dateUnitEnd").val('').attr("disabled",true).trigger('change');
			}else{
				if(!$("#valueEnd4").val()){
					$("#valueEnd4").val('0')
				}
				if(!$("#dateUnitEnd").val()){
					$("#dateUnitEnd").val(DIS_DATE_TYPE_02);
				}
				$("#valueEnd4").prop("disabled", false);
				$("#dateUnitEnd").attr("disabled",false).trigger('change');
			}
		}

		$("#dateStr").empty();
		$("#dateStr").append( $("<option>").val("").text(""));
		switch (dateEnd) {
			case "01":
				$("#dateStr").append(OPTION1);
				break;
			case "02":
				$("#dateStr").append(OPTION1).append(OPTION2);
				break;
			case "03":
				$("#dateStr").append(OPTION1).append(OPTION2).append(OPTION3);
				break;
			case "04":
				$("#dateStr").append(OPTION4);
				break;
			case "05":
				$("#dateStr").append(OPTION4).append(OPTION5);
				break;
			case "06":
				$("#dateStr").append(OPTION4).append(OPTION5).append(OPTION6);
				break;
			default:
				$("#dateStr").append(OPTION1).append(OPTION2).append(OPTION3).append(OPTION4).append(OPTION5).append(OPTION6);
		}
		$("#dateStr").val(dateStr);
	}
   var setUnit = function(){
	   var conditionItem = $("#conditionItem").val();
	   var controlType = $("#controlType").val();
	   if(conditionItem==CONDITION_ITEM_03){
		   $("#controlType").attr("disabled",true).trigger('change');
		   $("#valueSingleDiv").hide();
		   $("#valueRangeDiv").hide();
		   $("#onDateRangeDiv").hide();
		   $("#dateRangeDiv").show();
		   if(VDATE){
			   $("#valueStr3").val(Date.ofDate(VDATE).format('yyyy-MM-dd')).trigger('change');
		   }
	   }else if(conditionItem==CONDITION_ITEM_04){
		   $("#controlType").attr("disabled",true).trigger('change');
		   $("#valueSingleDiv").hide();
		   $("#valueRangeDiv").hide();
		   $("#dateRangeDiv").hide();
		   $("#onDateRangeDiv").show();
	   }else{
		   $("#dateRangeDiv").hide();
		   $("#onDateRangeDiv").hide();
		   $("#controlType").attr("disabled",false).trigger('change');
		   if(controlType!=CONTROL_TYPE_05){
          		$("#valueSingleDiv").show();
          		$("#valueRangeDiv").hide();
          	}else{
          		$("#valueSingleDiv").hide();
          		$("#valueRangeDiv").show();
          	}
		   if(conditionItem==CONDITION_ITEM_02){
			   $("#unit1").val("元").trigger('change');
			   $("#unit2").val("元").trigger('change');
			   $("#unit3").val("元").trigger('change');
		   }else{
			   $("#unit1").val("万元").trigger('change');
			   $("#unit2").val("万元").trigger('change');
			   $("#unit3").val("万元").trigger('change');
		   }
	   }
   }
   function requiredW(){
	   //启用时间
	   if($("#conditionItem").val() == CONDITION_ITEM_04){
		   var dateStr = $("#dateStr").val();
		   var dateEnd = $("#dateEnd").val();
		   if(!dateStr && !dateEnd){
			   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"前置或后置项"));
			   return false;
		   }
		   if(dateStr){
			   if(!$("#valueStr4").val()){
				   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"前置数值"));
				   return false;
			   }
			   if(!$("#dateUnitStr").val()){
				   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"前置日期类型"));
				   return false;
			   }
		   }
		   if(dateEnd){
			   if(!$("#valueEnd4").val()){
				   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"后置数值"));
				   return false;
			   }
			   if(!$("#dateUnitEnd").val()){
				   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"后置日期类型"));
				   return false;
			   }
		   }
	   }else{
		   if(!$("#conditionItem").val()){
			   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"条件项"));
			   return false;
		   }
		   if($("#conditionItem").val()!=CONDITION_ITEM_03&&!$("#controlType").val()){
			   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"控制类型"));
			   return false;
		   }
		   if($("#conditionItem").val()==CONDITION_ITEM_03&&VDATE !=null && VDATE!="null"&&!$("#valueStr3").val()){
			   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"开始时间"));
			   return false;
		   }
		   if($("#conditionItem").val()==CONDITION_ITEM_03&&!$("#valueEnd3").val()){
			   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"结束时间"));
			   return false;
		   }
		   if($("#conditionItem").val()==CONDITION_ITEM_03&&$("#valueEnd3").val()<$("#valueStr3").val()){
			   Joyin.warn("开始时间不允许大于结束时间!");
			   return false;
		   }
		   if($("#conditionItem").val()!=CONDITION_ITEM_03&&$("#controlType").val()!=CONTROL_TYPE_05&&!$("#valueStr2").val()){
			   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"数值"));
			   return false;
		   }
		   if($("#conditionItem").val()!=CONDITION_ITEM_03&&$("#controlType").val()==CONTROL_TYPE_05&&!$("#valueStr").val()){
			   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"最小数值"));
			   return false;
		   }
		   if($("#conditionItem").val()!=CONDITION_ITEM_03&&$("#controlType").val()==CONTROL_TYPE_05&&!$("#valueEnd").val()){
			   Joyin.warn(JoyinUtil.getMessage('FW_E_0006',"最大数值"));
			   return false;
		   }
		   if($("#conditionItem").val()!=CONDITION_ITEM_03&&$("#controlType").val()==CONTROL_TYPE_05&&JoyinMath.doMinus($("#valueStr").val(),$("#valueEnd").val())>0){
			   Joyin.warn("前面数值不能大于后面数值");
			   return false;
		   }
	   }
	   return true;
	};
    $(function() {
    	init();
    	$("select[name='controlType']").closest("div").next().html("");//删除控制类型后面的小星星
        // 按钮事件
        $('#RskSupervisionIndividuation002_btn_save').click(function() {
        	if(!requiredW()){
    			return;
		 	}
            saveOrSubmit('save');
        });
        $("#controlType").change(function(){
        	var controlType = $("#controlType").val();
        	var conditionItem = $("#conditionItem").val();
        	if(conditionItem!=CONDITION_ITEM_03 && conditionItem!=CONDITION_ITEM_04){
        		if(controlType!==CONTROL_TYPE_05){
            		$("#valueSingleDiv").show();
            		$("#valueRangeDiv").hide();
            	}else{
            		$("#valueSingleDiv").hide();
            		$("#valueRangeDiv").show();
            	}
        	}
        	
        })
        $("#conditionItem").change(function(){
			var conditionItem = $("#conditionItem").val();
			if(conditionItem==CONDITION_ITEM_04){
				$("#controlType").val(CONTROL_TYPE_05);
			}else{
				$("#controlType").val('');
			}
        	setUnit();
			setDateStrAttr();
			setDateEndAttr();
        })
		$("#dateStr").change(function(){
			setDateStrAttr();
		})
		$("#dateEnd").change(function(){
			setDateEndAttr();
		})
		$("#valueStr4").change(function(){
			var valueStr4 = $("#valueStr4").val();
			if(valueStr4 < 0){
				$("#valueStr4").val(0);
			}
			if(valueStr4 > 365){
				$("#valueStr4").val(365);
			}
		})
		$("#valueEnd4").change(function(){
			var valueEnd4 = $("#valueEnd4").val();
			if(valueEnd4 < 0){
				$("#valueEnd4").val(0);
			}
			if(valueEnd4 > 365){
				$("#valueEnd4").val(365);
			}
		})
    });
}());