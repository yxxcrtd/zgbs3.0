/**
 * 证券账户管理
 */
$(function() {
    /**
     * 保存或提交
     * 
     */
    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#submitForm');
        object.remark = $('#remark').val();
        object.operateType=$('#operateType').val();
        object.recAcctId=$('#recAcctId').select2('data')[0].id;
        object.recAcctCcy=$('#recAcctId').select2('data')[0].col;
        object.trdAcctCcy=$('#trdAcctId').select2('data')[0].col;
        object.trdAcctId=$('#trdAcctId').select2('data')[0].id;
        object.pageModel = pageModel;
    	if ($("#registOrg").val() == "03") {
    		//暂不支持多选
    		//object.tradePlace=$("#tradePlace").val().join();
    		object.tradePlace=$("#tradePlace").val();
    	}else{
    		object.tradePlace=$("#tradePlace2").val();
    	}
    	//判断满足条件时券商和股东代码是否为空
    	if ($("#registOrg").val() == "03" && ($("#tradePlace").val() == "03" || $("#tradePlace").val() == "04")) {
    		if (object.bondId == null || $.trim(object.bondId) == "") {
    				Joyin.error("请输入股东代码。");
    				return;
    			}
    	}
    	if($("#registOrg").val() == "07" && $("#tradePlace2").val() == "12"){
    		if(object.tradeNo == null || $.trim(object.tradeNo) == ""){
    			Joyin.error("请输入交易编码。");
				return;
    		}
    	}
    	if(FileCommon) {
    		object.fileArray = FileCommon.getUploadFileInfo()
    	}
        Joyin.ajaxJson(basePath + 'invest/basicdata/mstsecuritiesaccount/MstSecuritiesAccount002/check',
                object, function(result) {
                    Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath
                                    + 'invest/basicdata/mstsecuritiesaccount/MstSecuritiesAccount002/save',
                                    object, "MstSecuritiesAccount002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath
                                    + 'invest/basicdata/mstsecuritiesaccount/MstSecuritiesAccount002/save',
                                    object, "MstSecuritiesAccount002_btn_submit");
                        }
                    });
                });
   
    };
    /*【证券账户管理】：增加逻辑,当托管机构选择“中国证券登记结算有限责任公司”，
     * 交易场所支持多选，多选选项限制为：上海证券交易所、深圳证券交易所*/
	    var registOrgChange = function() {
			if ($("#registOrg").val() == "03") {
				var op = document.getElementsByName('tradePlace')[0]
		    	.getElementsByTagName('option');
				for (var i = 0; i < op.length; i++) {
					if (op[i].value == "01") {
						op[i].remove(op[i]);
					}
				};
				var p = document.getElementById("tradePlaceChange");
				p.removeAttribute('style');
				
				//暂不支持多选
				var p = document.getElementById("tradePlace");
				p.removeAttribute('multiple');
				
				var p = document.getElementById("tradePlaceOld");
				p.setAttribute('style','display:none');
				
		} else {
			var p = document.getElementById("tradePlaceChange");
			p.setAttribute('style','display:none');
			
			var p = document.getElementById("tradePlaceOld");
			p.removeAttribute('style');
		}
		tradeMarketChange();
	};

    var pageLoad = function(){
        //双录且为第一个节点
        if(isDoubleRecord && isDoubleRecord == '01'
            && isFirstApprovalNode && isFirstApprovalNode == 'Y'){
            isDoubleInput = 'Y';

            //清空需要双录的字段
            $('#registOrg').val('').trigger("change");
            $('#registOrg').attr('disabled',false);
            $('#registOrg').attr('data-ignore',true);

            $('#recAcctId').val('').trigger("change");
            $('#recAcctId').attr('disabled',false);
            $('#recAcctId').attr('data-ignore',true);

            $('#trdAcctId').val('').trigger("change");
            $('#trdAcctId').attr('disabled',false);
            $('#trdAcctId').attr('data-ignore',true);


            $('#tradePlace').val('').trigger("change");
            $('#tradePlace').attr('disabled',false);
            $('#tradePlace').attr('data-ignore',true);
            $('#tradePlace2').val('').trigger("change");
            $('#tradePlace2').attr('disabled',false);
            $('#tradePlace2').attr('data-ignore',true);
        }
    };
	
    

	 var tradeMarketChange = function() {
		 if(($("#tradePlace").val() == "03" || $("#tradePlace").val() == "04" || $("#tradePlace2").val() == "03" || $("#tradePlace2").val() == "04")){
			 $('#yNHkstockc_div').show();
		 }else{
			 $('#yNHkstockc_div').hide();
		 }
		if ($("#registOrg").val()) {
			if ($("#registOrg").val() == "03" && ($("#tradePlace").val() == "03" || $("#tradePlace").val() == "04")) {
				$("#bondId_div").css('display', 'block');
				$('#yNHkstockc_div').show();
			} else {
				$("#bondId").val('').trigger('change');
				$("#bondId_div").css('display', 'none');
				$('#yNHkstockc_div').hide();
			}
			if($("#registOrg").val() == "07" && $("#tradePlace2").val() == "12"){
				$("#tradeNo_div").css('display', 'block');
			} else{
				$("#tradeNo").val('').trigger('change');
				$("#tradeNo_div").css('display', 'none');
			}
			if(($("#tradePlace").val() == "03" || $("#tradePlace").val() == "04" || $("#tradePlace2").val() == "03" || $("#tradePlace2").val() == "04")){
				$('#yNHkstockc_div').show();
			}else{
				$('#yNHkstockc_div').hide();
			}
		} else {
			$("#bondId").val('').trigger('change');
			$("#bondId_div").css('display', 'none');
			//$("#tradeNo").val('').trigger('change');
			$("#tradeNo_div").css('display', 'none');
		}
	}
	
	// $("#bondId").keyup(function(){
	// 	  $("#secAcctId").val($("#bondId").val());
	// });
    
    
    /**
	 * 画面加载完成
	 */
    $(function() {
        if (!$('#secAcctSource').val()) {
            $('#secAcctSource').val('01').trigger('change');
        }
        
        $('#registOrg').on('change',function(){
            if ($('#registOrg').val()=='07') {
                $('#futuresCompany_div').show();
                $("#tradePlace2").val('12').change().prop("disabled",true);
            }
            else {
                $("#tradePlace2").val('').change().prop("disabled",false);
                $('#futuresCompany_div').hide();
            }
            registOrgChange();
        });
        $('#tradePlace').on('change',function(){//深交所或者上交所
        	tradeMarketChange();
        })
        $('#tradePlace2').on('change',function(){//深交所或者上交所
        	tradeMarketChange();
        });
        if ($('#registOrg').val()=='03') {
        	registOrgChange();
        }
        if ($('#registOrg').val()=='07') {
            $('#futuresCompany_div').show();
            $("#tradePlace2").val('12').change().prop("disabled",true);
        }
        else {
            //$("#tradePlace2").val('').change().prop("disabled",false);
            $('#futuresCompany_div').hide();
        }
        if($('#pageModel').val()!=''){
        	pageModel=$('#pageModel').val();
        }
        //
        tradeMarketChange();
        //绑定init界面转换事件
    	if(pageModel==PAGE_MODEL_EDIT){
            $('#secAcctId').attr("disabled","disabled");
            // $('#bondId').attr("disabled","disabled");
        }
        // 绑定按钮事件
        $('#MstSecuritiesAccount002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#MstSecuritiesAccount002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

        pageLoad();

        // 双录校验
        if(isDoubleInput == 'Y'){
            JoyinWorkFlow.doCheckOnSubmit = function (object) {
                var param = $.serializeToObject('#submitForm', true);
                if ($("#registOrg").val() == "03") {
                    param.tradePlace=$("#tradePlace").val();
                }else{
                    param.tradePlace=$("#tradePlace2").val();
                }
                param.isDoubleInput = 'Y';
                param.pageModel = pageModel;
                var checkResult = false;

                Joyin.ajaxJson(basePath + 'invest/basicdata/mstsecuritiesaccount/MstSecuritiesAccount002/check', param, function(result) {
                    checkResult = true;
                },false);

                return checkResult;
            };
        }
    });
}());
