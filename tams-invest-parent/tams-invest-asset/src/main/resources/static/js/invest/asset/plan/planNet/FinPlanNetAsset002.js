var QueryFunc=function(){
    return {
        serachKey:function(){
        }
    }
}();

(function() {
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


    /**
     * 中债报送信息DIV控制显示
     */
    function typeSelect() {
        var type = $('#type').val();
        /*if (!type ||type == publicOffPlan) {
            $(".bussRepAssetMngProdInfo_div").show();
            $(".bussRepOutInvestAgrInfo_div").hide();
        } else {
            $(".bussRepAssetMngProdInfo_div").hide();
            $(".bussRepOutInvestAgrInfo_div").show();
        }*/
    };

    //add by  zhangnian 20191021 start
    /**
     * 中债报送show/hide
     */
    var showReportInfo = function(){
        var repAssetType = $('#repAssetType').val();
        $("#bussRepOutInvestAgrInfo_div").hide();
        $("#bussRepAssetMngProdInfo_div").hide();
        $("#bussRepAssetMngProdInfo_repFinancialComissue").prop("required",false);
        if (repAssetType=="1701"||repAssetType=="1702"||repAssetType=="1703"||repAssetType=="1704"
            ||repAssetType=="1705"||repAssetType=="1706") {
            $("#bussRepAssetMngProdInfo_div").show();
            
          //add by wj
            if(repAssetType=="1705"){
            	$("#bussRepAssetMngProdInfo_repFinancialComissue").prop("required",true).trigger('change');
            	$("#bussRepAssetMngProdInfo_repFinancialComissue").attr("disabled",false).trigger('change');
            	$("#bussRepAssetMngProdInfo_repFinancialComissue_div").css('display','block');

            }else{
            	//默认显示否
                $("#bussRepAssetMngProdInfo_repFinancialComissue_div").css('display','none');
                $('#bussRepAssetMngProdInfo_repFinancialComissue').prop('checked', false);
            	$("#bussRepAssetMngProdInfo_repFinancialComissue").prop("required",false).trigger('change');
            	$("#bussRepAssetMngProdInfo_repFinancialComissue").attr("disabled",true).trigger('change');
            }
        }else if(repAssetType=="2801"||repAssetType=="2802"||repAssetType=="2803"||repAssetType=="2804"
            ||repAssetType=="2805"||repAssetType=="2806"||repAssetType=="2899"){
            $("#bussRepOutInvestAgrInfo_div").show();
        }
        star();
        layui.form.render();
    }
    //add by  zhangnian 20191021 end
    //画面联动：输入出账编号，联动显示关联合同编号、BECIF号和发行金额
    /*var cmsIdChange = function() {
    	if($("#cmsId").val() != null && $("#cmsId").val() != ''){
    		var cmsId = $("#cmsId").val();
            var object = {'cmsId':cmsId};
            
            Joyin.ajax(getChange, object, function(data){
                if(data){
                    $('#cmsContractId').val(data.cmsContractId);
                    $('#cmsBecif').val(data.cmsBecif);
                    $('#issueAmt').val(data.issueAmt);
                    $('#cmsContractId').attr("disabled",true);
                    $('#cmsBecif').attr("disabled",true);
                    $('#issueAmt').attr("disabled",true);
                }
            });
    	}
    	else{
    		$('#cmsContractId').val("");
            $('#cmsBecif').val("");
            $('#issueAmt').val("");
    	}
        
    };*/

    //判断是提交或是保存
    function saveOrSubmit(operateType) {
        if(!$('#type').val()){
            $('#type').val(publicOffPlan).trigger('change');
        }
        var object = $.serializeToObject('#submitForm', true);
        object.pageModel = pageModel;
        object.operateType = operateType;
        // 增信措施(多选)
        object.creditEnhanceMeasure = $('#creditEnhanceMeasure').val()?$('#creditEnhanceMeasure').val().join(","):'';
        var repAssetType = $('#repAssetType').val();
        //update by zhangnian 20191102 start
        /*var repYieldFlag = $("#bussRepAssetMngProdInfo_repYieldFlag").prop("checked") ? 'Y' : 'N';
        object.bussRepAssetMngProdInfo.repYieldFlag = repYieldFlag;
        var repIsCtrlBottom = $("#bussRepAssetMngProdInfo_repIsCtrlBottom").prop("checked") ? 'Y' : 'N';
        object.bussRepAssetMngProdInfo.repIsCtrlBottom = repIsCtrlBottom;
        var repContainExpectedYield = $("#bussRepOutInvestAgrInfo_repContainExpectedYield").prop("checked") ? 'Y' : 'N';
        object.bussRepOutInvestAgrInfo.repContainExpectedYield = repContainExpectedYield;*/
        //update by zhangnian 20191102 end
        //确认是否验证
        /*object.bussRepAssetMngProdInfo.needValid = object.type == publicOffPlan || !object.type;
        object.bussRepOutInvestAgrInfo.needValid = object.type && object.type != publicOffPlan;*/
        //update by zhangnian 20191031  start
        if ( object.bussRepAssetMngProdInfo){
            object.bussRepAssetMngProdInfo.needValid = repAssetType=="1701"||repAssetType=="1702"||repAssetType=="1703"||repAssetType=="1704"
                ||repAssetType=="1705"||repAssetType=="1706";
        }
       if (object.bussRepOutInvestAgrInfo){
           object.bussRepOutInvestAgrInfo.needValid = repAssetType=="2801"||repAssetType=="2802"||repAssetType=="2803"||repAssetType=="2804"
               ||repAssetType=="2805"||repAssetType=="2806"||repAssetType=="2899";
       }

        //update by zhangnian 20191031  end
        //文件信息
        object.fileCommonList = FileCommon.getUploadFileInfo();
        //增加快捷录入功能 by 赵东东 2019.10.12 start 
        var finprodIdOld = "F17_"+ $('#finprodMarketId').val();
        if(object.finprodId != finprodIdOld){
    	    object.finprodId = '';
    	    object.fjId = '';
          }
//        object.finprodId='';
//        object.fjId = '';
        //增加快捷录入功能 by 赵东东 2019.10.12 end
        //参与方信息
        if (object.bussParticipantInfoDto){
            object.bussParticipantInfoDto.type6 = object.category;
        }

        //add by  zhangnian 20191021 start
        if (object.bussRepAssetMngProdInfo){
            object.bussRepAssetMngProdInfo.repAssetType = repAssetType;

        }
        if (object.bussRepOutInvestAgrInfo){
            object.bussRepOutInvestAgrInfo.repAssetType = repAssetType;
        }

        //add by  zhangnian 20191021 end
        Joyin.ajaxJson(basePath + 'invest/asset/plan/planNet/FinPlanNetAsset002/check', object, function(result) {
            Joyin.ajaxJson(basePath + 'invest/asset/common/isHoliday', {
                mdate: object.mdate,
                calendarId: object.calendarId
            }, function (isHoliday) {
                if(isHoliday) {
                    Joyin.confirm("终止日期为节假日，是否确认提交", function () {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/asset/plan/planNet/FinPlanNetAsset002/save', object, "FinPlanNetAsset002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'invest/asset/plan/planNet/FinPlanNetAsset002/save', object, "FinPlanNetAsset002_btn_submit");
                        }
                    })
                }else{
                    Joyin.confirm(result.data, function() {
                        if(operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/asset/plan/planNet/FinPlanNetAsset002/save', object, "FinPlanNetAsset002_btn_save");
                        }
                        else {
                            WorkFlow.doSubmitList(basePath + 'invest/asset/plan/planNet/FinPlanNetAsset002/save', object, "FinPlanNetAsset002_btn_submit");
                        }
                    });
                }
        })
        });
    };
    //控件加载完成后
    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
            if(typeof pageModel !== 'undefined'
                && (pageModel === PAGE_MODEL_EDIT || pageModel === PAGE_MODEL_NEW)){
                $('.purSpeed').append('<i class="must">*</i>');
                $('.redSpeed').append('<i class="must">*</i>');
            }
            $('#repAssetType').trigger('change');
        }
    }
    var init = function(){
    	//如果授信模式=='02'，则显示批复号和实际授信主体
    	if(CE_MODEL == '02'){
    		$('#actualCreditProvidDiv').show();
    		$('#pflshDiv').show();
    	}
    	else{
    		$('#actualCreditProvidDiv').hide();
    		$('#pflshDiv').hide();
    	}
    	var hapCreditAmt = $("#hapCreditAmt").val();
    	if(hapCreditAmt && hapCreditAmt>0){
    		$("#pflsh").attr("disabled",true).trigger('change');
    	}
    	if($("#creditFlag").val() == 'Y'){
    		$("#actualCreditProvid").attr("required", true).trigger('change');
    	}
    	if($("#creditFlag").val() != 'Y'){
    		$("#actualCreditProvid").attr("required", false).trigger('change');
    	}
    	star();
    }
    /**
     * 画面加载完成
     *
     */
    $(function() {
    	init();
        if(pageModel!=PAGE_MODEL_NEW){
            $('#finprodMarketId').attr('disabled', true).trigger('change');
            $('.btn-save').hide();
        }
        //二级分类联动
        $('#type').change(function() {
            typeSelect();
            //$('#bussRepAssetMngProdInfo_repAssetType').repSelectArr(pageModel);

        });
        /*category and type's automatic match added by zhangpuyu on 2019/7/24 -start*/
        $("#type12").change(function(){
            if($('#type12').val()!='') {
                window.BussAssetFiveType.doAssetFiveTypeChange('F04', 'type9', 'type10', 'type11', 'type12', 'category', 'type');
            }
        });
//        // 对预警线和止损线保留六位小数 by 赵东东 2019.10.15 start
//        $("#warnLine").change(function(){
//        	$("#warnLine").val(parseFloat($("#warnLine").val()).toFixed(6));
//        });
//        $("#flatLine").change(function(){
//        	$("#flatLine").val(parseFloat($("#flatLine").val()).toFixed(6));
//        });
        // 对预警线和止损线保留六位小数 by 赵东东 2019.10.15 end
        /*category and type's automatic match added by zhangpuyu on 2019/7/24 -end*/
        //一二级联动显示中债资产负债类别
        $('#category').change(function () {
            //$('#bussRepAssetMngProdInfo_repAssetType').repSelectArr(pageModel);
        })
        
      //出账编号联动
        /*$('#cmsId').change(function() {
            cmsIdChange();
        });*/
        // 绑定按钮事件
        $('#FinPlanNetAsset002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#FinPlanNetAsset002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        //增加快捷录入功能 by 赵东东 2019.10.12 start 
        $('#FinPlanNetAsset002_btn_quickInput').click(function() {

            Joyin.ajax(basePath + 'invest/asset/plan/planNet/FinPlanNetAsset002/init', {
                'finprodMarketId': $('#finprodMarketId').val(),
//                'status':'quickinput'
                'pageModel': pageModel,'isQuickInput':true
            }, function (data) {
                // update by caiqun 20200721 资产代码为本机构不存在的，快录时不直接删除填的的资产，做成提醒；  start
                if($(data).find('#finprodMarketId').val()!=""){
                    $("body").html(data);
                }else{
                    Joyin.error("资产代码不存在！");
                }
                // update by caiqun 20200721 资产代码为本机构不存在的，快录时不直接删除填的的资产，做成提醒；  end
                // 修改保存提交异常 by 赵东东 start 
             // 修改保存提交异常 by 赵东东 end
            });

        });
      //增加快捷录入功能 by 赵东东 2019.10.12 end	
        
        $("#pflsh").change(function(){
        	var str = $('#pflsh').select2('data')[0].object.split(',');
        	if(str != null && str.length==3){
        		$("#seqno").val(str[0]);
        		$("#creditMstId").val(str[1]);
        		$("#creditFlag").val(str[2]);
        	}
        	if(str[2] == 'Y'){
        		$("#actualCreditProvid").attr("required", true).trigger('change');
        	}
        	if(str[2] != 'Y'){
        		$("#actualCreditProvid").attr("required", false).trigger('change');
        	}
        	star();
        });
        typeSelect();
        if(pageModel!=PAGE_MODEL_NEW) {
            $('#type').attr("disabled", true);
        };
        $('#mdate,#vdate').change(function(obj){
            obj.type = FINPROD_TYPE_TG_F17;
            AssetCommon.checkDate(obj);
        })
		/* 行内五级分类共通 zhangpuyu  2019-7-25 -start */
		$("#type9,#type10,#type11,#type12").change(function(){
			window.BussAssetFiveType.doAssetFiveTypeChange(IS_SHOW_TYPE,'F04','type9','type10','type11','type12','category','type');
		});
        
		/* 行内五级分类共通  zhangpuyu 2019-7-25 -end */
        //是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
        	$('#type11Div').show();
        	$('#type12Div').show();
        }else{
        	$('#type11Div').hide();
        	$('#type12Div').hide();
        }
        //负债类别change事件
        $('#repAssetType').change(function(){
            showReportInfo();
        });
    });
}());

