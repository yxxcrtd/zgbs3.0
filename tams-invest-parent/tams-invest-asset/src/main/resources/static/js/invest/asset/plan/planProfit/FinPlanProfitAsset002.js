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
                && (pageModel === PAGE_MODEL_EDIT || pageModel === PAGE_MODEL_NEW || pageModel === PAGE_MODEL_REFERENCE)) {
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

    var getElementById = function(id) {
        return $('#' + id);
    };
    /**
     * 中债报送信息DIV控制显示
     */
    function typeSelect() {
        var type = $('#type').val();
       /* if (!type ||type == publicOffPlan) {
            $(".bussRepAssetMngProdInfo_div").show();
            $(".bussRepOutInvestAgrInfo_div").hide();
        } else {
            $(".bussRepAssetMngProdInfo_div").hide();
            $(".bussRepOutInvestAgrInfo_div").show();
        }*/
        if (!init) {
            if(type == PLAN_ASSET_SEC_TYPE_02){
                $("#investNature").val(INVEST_01).trigger('change');
            }
            else if(type == PLAN_ASSET_SEC_TYPE_03){
                $("#investNature").val(INVEST_02).trigger('change');
            }
            else {
                $("#investNature").val('').trigger('change');
            }
        }
    }
    //add by  zhangnian 20191021 start
    /**
     * 中债报送show/hide
     */
    var showReportInfo = function(){
    	
        var repAssetType = $('#repAssetType').val();
        $("#bussRepOutInvestAgrInfo_div").hide();
        $("#bussRepAssetMngProdInfo_div").hide();
        
//        $("#bussRepAssetMngProdInfo_repFinancialComissue").prop("required",false);
        
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
    // function categoryChange() {
    //     var category = $("#category").val();
    //     if (category) {
    //         var object = {'category':category};
    //         Joyin.ajax(basePath + 'invest/asset/plan/planProfit/FinPlanProfitAsset002/generateFinprodMarketId', object, function (data) {
    //             $('#finprodMarketId').val(data);
    //         });
    //     }
    // }
    //画面联动：输入出账编号，联动显示关联合同编号、BECIF号和发行金额
    var cmsIdChange = function() {
    	if($("#cmsId").val() != null && $("#cmsId").val() != ''){
	        var cmsId = $("#cmsId").val();
	        var object = {'cmsId':cmsId};
	        Joyin.ajax(getChange, object, function(data){
	            if(data){
	                $('#cmsContractId').val(data.cmsContractId);
	                $('#cmsBecif').val(data.cmsBecif);
	                $('#cmsCreditType').val(data.cmsCreditType);
	                $('#creditCustomerNo').val(data.creditCustomerNo);
	            }
	        });
    	}
    	else{
    		$('#cmsContractId').val("");
            $('#cmsBecif').val("");
            $('#cmsCreditType').val("");
            $('#creditCustomerNo').val("");
    	}
    };
    //判断是提交或是保存
    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#submitForm', true);
        //多选
        object.assetProvider = $('#assetProvider').val()?$('#assetProvider').val().join(","):'';
        // 增信措施(多选)
        object.creditEnhanceMeasure = $('#creditEnhanceMeasure').val()?$('#creditEnhanceMeasure').val().join(","):'';
        object.pageModel = pageModel;
        object.operateType = operateType;
        var repAssetType = $('#repAssetType').val();
        //update by zhangnian 20191102 start
        //中债报送信息是否控件显示
       /* var repYieldFlag = $("#bussRepAssetMngProdInfo_repYieldFlag").prop("checked") ? 'Y' : 'N';
        object.bussRepAssetMngProdInfo.repYieldFlag = repYieldFlag;
        var repIsCtrlBottom = $("#bussRepAssetMngProdInfo_repIsCtrlBottom").prop("checked") ? 'Y' : 'N';
        object.bussRepAssetMngProdInfo.repIsCtrlBottom = repIsCtrlBottom;
        var repContainExpectedYield = $("#bussRepOutInvestAgrInfo_repContainExpectedYield").prop("checked") ? 'Y' : 'N';
        object.bussRepOutInvestAgrInfo.repContainExpectedYield = repContainExpectedYield;*/
        //update by zhangnian 20191102 end
        //是否需要验证判断
        if (object.bussRepAssetMngProdInfo){
            object.bussRepAssetMngProdInfo.needValid = repAssetType=="1701"||repAssetType=="1702"||repAssetType=="1703"||repAssetType=="1704"
                ||repAssetType=="1705"||repAssetType=="1706";
        }
        if (object.bussRepOutInvestAgrInfo){
            object.bussRepOutInvestAgrInfo.needValid = repAssetType=="2801"||repAssetType=="2802"||repAssetType=="2803"||repAssetType=="2804"
                ||repAssetType=="2805"||repAssetType=="2806"||repAssetType=="2899";
        }

        
        //sx个性化 草稿箱bug修复 start
        if (draftoption && object.bussRepAssetMngProdInfo){
        	object.bussRepAssetMngProdInfo.needValid = true;
        }
		if (draftoption && object.bussRepOutInvestAgrInfo){
        	object.bussRepOutInvestAgrInfo.needValid = true;
        }
		//sx个性化 草稿箱bug修复 end
		
        //参与方信息
        if (object.bussParticipantInfoDto){
            object.bussParticipantInfoDto.type6 = object.category;
            object.bussParticipantInfoDto.finprodType2 = INT_ASSET_TYPE;
        }

        //文件信息
        if (FileCommon){
            object.fileCommonList = FileCommon.getUploadFileInfo();
        }
        // object.finProductAdd.calRateType = $('#finInterestFactorDto_calRateType').val();
        if ( object.calRateType){
            object.calRateType = $('#finInterestFactorDto_calRateType').val();
        }


        if(object.parentPlan == 'Y'){
         // 付息信息
            object.finprodType2 = ASSET_TYPE_PARENTPLAN;
            object.finInterestFactorDto.finprodType2 = ASSET_TYPE_PARENTPLAN;
            delete object.finPayInterestStdInfoDto;
            delete object.bussFinRepaymentDto;
        }else{
            object.finprodType2 = FINPROD_TYPE_TG_F17; 
            object.finPayInterestStdInfoDto.finCashPlanList = FinPayInterestStdInfo.getAllRows();
            object.bussFinRepaymentDto.finCashPlanList = BussFinRepayment.getAllRows();
        }
        //add by  zhangnian 20191021 start
        if (object.bussRepAssetMngProdInfo){
            object.bussRepAssetMngProdInfo.repAssetType = repAssetType;
        }
        if (object.bussRepOutInvestAgrInfo){
            object.bussRepOutInvestAgrInfo.repAssetType = repAssetType;
        }

        //add by  zhangnian 20191021 end

        //计划类资产添加开放计划 add by kongzhifang 2022-03-04 start
        // 开放计划
        if (object.bussFinProductOpenPlanDto){
            if (object.bussFinProductOpenPlanDto.dayOfWeek){
                object.bussFinProductOpenPlanDto.dayOfWeek = object.bussFinProductOpenPlanDto.dayOfWeek.join(',');
            }
            if (object.bussFinProductOpenPlanDto.dayOfMonth){
                object.bussFinProductOpenPlanDto.dayOfMonth = object.bussFinProductOpenPlanDto.dayOfMonth.join(',');
            }
            if (object.bussFinProductOpenPlanDto.workdayOfMonth){
                object.bussFinProductOpenPlanDto.workdayOfMonth = object.bussFinProductOpenPlanDto.workdayOfMonth.join(',');
            }
            if (!object.bussFinProductOpenPlanDto.hdayOpenFlag) {
                object.bussFinProductSaleParamDto.hdayOpenFlag = 'N';
            }
            if(object.operationType) {
                object.bussFinProductOpenPlanDto.operationType = object.operationType;//发行方式不为空时，将发行方式传递到开放计划DTO里
            }
            //add by wm 2019-11-26 开放计划月选第几周报错修复 start
            if (object.bussFinProductOpenPlanDto.weekOfMonth){
                object.bussFinProductOpenPlanDto.weekOfMonth = object.bussFinProductOpenPlanDto.weekOfMonth.join(',');
            }
            if (object.bussFinProductOpenPlanDto.workdayOfWeek){
                object.bussFinProductOpenPlanDto.workdayOfWeek = object.bussFinProductOpenPlanDto.workdayOfWeek.join(',');
            }
            //add by wm 2019-11-26 开放计划月选第几周报错修复 end
        }
        var openMode ="";
        if (object.bussFinProductOpenPlanDto){
            openMode =object.bussFinProductOpenPlanDto.openMode;//add by zlp 20190314 高频开发不校验开放计划
        }
        if(openMode!='02'&&!BussFinProductOpenPlan.chkOpenPlan()) return false;//add by zhangweiqi 开放计划元素调整后重新生成计划
        // 赎回开放计划 add by yejiejie
        if (object.bussFinProductOpenPlanRedDto){
            var openRedMode = object.bussFinProductOpenPlanRedDto.openMode;
            if(openMode!='02'&&object.dealMode != DISPOSE_PATTERN_10&&!BussFinProductOpenPlan.chkRedOpenPlan()){
                return false;
            }
            if (object.bussFinProductOpenPlanRedDto.dayOfWeek){
                object.bussFinProductOpenPlanRedDto.dayOfWeek = object.bussFinProductOpenPlanRedDto.dayOfWeek.join(',');
            }
            if (object.bussFinProductOpenPlanRedDto.dayOfMonth){
                object.bussFinProductOpenPlanRedDto.dayOfMonth = object.bussFinProductOpenPlanRedDto.dayOfMonth.join(',');
            }
            if (object.bussFinProductOpenPlanRedDto.workdayOfMonth){
                object.bussFinProductOpenPlanRedDto.workdayOfMonth = object.bussFinProductOpenPlanRedDto.workdayOfMonth.join(',');
            }
            if (!object.bussFinProductOpenPlanRedDto.hdayOpenFlag) {
                object.bussFinProductOpenPlanRedDto.hdayOpenFlag = 'N';
            }
            //add by wm 2019-11-26 开放计划月选第几周报错修复 start
            if (object.bussFinProductOpenPlanRedDto.weekOfMonth){
                object.bussFinProductOpenPlanRedDto.weekOfMonth = object.bussFinProductOpenPlanRedDto.weekOfMonth.join(',');
            }
            if (object.bussFinProductOpenPlanRedDto.workdayOfWeek){
                object.bussFinProductOpenPlanRedDto.workdayOfWeek = object.bussFinProductOpenPlanRedDto.workdayOfWeek.join(',');
            }
            //add by wm 2019-11-26 开放计划月选第几周报错修复 end
        }
        //计划类资产添加开放计划 add by kongzhifang 2022-03-04 end

        Joyin.ajaxJson(basePath + 'invest/asset/plan/planProfit/FinPlanProfitAsset002/check', object, function(result) {
            if (result.errorList.length > 0){
                return;
            }
            if(object.parentPlan == 'Y'){
                Joyin.ajaxJson(basePath + 'invest/asset/common/isHoliday', {
                    mdate: object.mdate,
                    calendarId: object.calendarId
                }, function (isHoliday) {
                    if (isHoliday) {
                        Joyin.confirm("终止日期为节假日，是否确认提交", function () {
                            finalSubmit(object,operateType);
                        })
                    }else{
                        finalSubmit(object,operateType);
                    }
                })
            }else{
                if (FinPayInterestStdInfo && FinPayInterestStdInfo.check() && FinPayInterestStdInfo.isSureCheck()
                    && FinPayInterestStdInfo.comfirmCheck() && BussFinRepayment.isComfirmCheck()) {
                    Joyin.ajaxJson(basePath + 'invest/asset/common/isHoliday', {
                        mdate: object.mdate,
                        calendarId: object.calendarId
                    }, function (isHoliday) {
                        if (isHoliday) {
                            Joyin.confirm("终止日期为节假日，是否确认提交", function () {
                                finalSubmit(object,operateType);
                            })
                        }else{
                            Joyin.confirm(result.data, function() {
                                finalSubmit(object,operateType);
                            });
                        }
                    })
                }
            }
        });
    };
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
    $('#FinPlanProfitAsset002_btn_quickInput').click(function() {

        Joyin.ajax(basePath + 'invest/asset/plan/planProfit/FinPlanProfitAsset002/init', {
            'finprodMarketId': $('#finprodMarketId').val(),
            'isQuickInput':true
        }, function (data) {
            // update by caiqun 20200721 资产代码为本机构不存在的，快录时不直接删除填的的资产，做成提醒；  start
            if($(data).find('#finprodMarketId').val()!=""){
                $("body").html(data);
            }else{
                Joyin.error("资产代码不存在！");
            }
            // update by caiqun 20200721 资产代码为本机构不存在的，快录时不直接删除填的的资产，做成提醒；  end
        });

    });
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
    var finalSubmit = function(object,operateType){
        if(operateType === 'save') {
            WorkFlow.doSaveList(basePath + 'invest/asset/plan/planProfit/FinPlanProfitAsset002/save', object, "FinPlanProfitAsset002_btn_save");
        }
        else {
            WorkFlow.doSubmitList(basePath + 'invest/asset/plan/planProfit/FinPlanProfitAsset002/save', object, "FinPlanProfitAsset002_btn_submit");
        }
    }
    /**
     * 资产来源联动
     */
    function assetSourceChange() {
        var assetSource = $('#assetSource').val();
        if (assetSource && assetSource == line) {
            getElementById('assetProvider').prop('disabled', false).trigger('change');
            getElementById('assetSourceUnit').prop('disabled', false).trigger('change');
            $('#bussFinRecommentDto_div').show();
        } else {
            getElementById('assetProvider').prop('disabled', true).trigger('change');
            getElementById('assetSourceUnit').prop('disabled', true).trigger('change');
            $('#bussFinRecommentDto_div').hide();
        }
    }
    
    /**
     * 是否母计划change事件
     */
    var parentPlanChange = function(){
        var parentPlanChange = $('#parentPlan')[0].checked;
        var pFinprodId = $('#pFinprodId').val();
        if(parentPlanChange){
            $('#referRate').val('');
            $('#issueAmt').val('').attr('disabled',true);
            // modify by wj 20200421 母计划时将红星去掉。
            $('#issueAmt').prop('required', false);
            $('#finInterestFactorDto_investRate').val('');
            $('#finInterestFactorDto_loadRate').val('').attr('disabled',true);
           // $('#finPayInterestStdInfoDto_div').hide();
            $("#label_buyOutRate").find('i').remove();
        }else{
            $('#issueAmt').attr('disabled',false);
            $('#finInterestFactorDto_loadRate').attr('disabled',false);
           // $('#finPayInterestStdInfoDto_div').show();
            $("#label_buyOutRate").append($('<i class="must">*</i>'));
            AssetCommon.loadRateChange();
        }
        //如果母代码不为空，则隐藏付息信息和附件信息 yjm start 20190830
        if (parentPlanChange || pFinprodId){            
            $('#finPayInterestStdInfoDto_div').hide();
            $('#bussFinRepaymentDto_div').hide();
            if (!parentPlanChange) {
                $('#FileCommon_DIV').hide();
            }else {
                $('#FileCommon_DIV').show();
            }           
        } else {
            $('#finPayInterestStdInfoDto_div').show();
            $('#bussFinRepaymentDto_div').show();
            $('#FileCommon_DIV').show();
        }
        //如果母代码不为空，则隐藏付息信息和附件信息 yjm end 20190830
    }
    //控件加载完成后
    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
            //该资产存在交易
            if(isTransInAsset){
        		// $('#vdate').attr('disabled',true).trigger('change');
        		$('#ccy').attr('disabled',true).trigger('change');
        		$('#blnArea').attr('disabled',true).trigger('change');
        		$('#finInterestFactorDto_loadRate').attr('disabled',true).trigger('change');
        		//置灰付息计划生成计划按钮
        		// $('#finPayInterestStdInfoDto_btn_generationPlan').off('click').prop('disabled', true).addClass('layui-btn-disabled');
            }
            // 增加推荐方与推荐费率 delete by renping 20191014 start
//            //买断利率change
//            $('#buyOutRate').change(FinInterestFactor.calreferRate).trigger("change");
            // 增加推荐方与推荐费率 delete by renping 20191014 end
            //计息要素-借款利率change
            $('#finInterestFactorDto_loadRate').change(AssetCommon.loadRateChange).trigger('change');
            parentPlanChange();
            $('#repAssetType').trigger('change');
            //update by  zhangnian 20191120  start
            // 增加推荐方与推荐费率 modify by renping 20191014 start
            $('#finInterestFactorDto_loadRate').change(function () {
                FinInterestFactor.calBuyOutRateByTable('loadRate')
            });
            $('#finInterestFactorDto_investRate').change(FinInterestFactor.calBuyOutRateByTable).trigger('change');
            // 增加推荐方与推荐费率 modify by renping 20191014 end
            //update by  zhangnian 20191120  start
        }
    }
    /**
     * 计算期限
     */
    var calTermDays = function(){
        var vdate = $('#vdate').val();
        var mdate = $('#mdate').val();
        if(vdate!=null && vdate!=''&&mdate!=null && mdate!='') {
            var termDays = Math.abs(parseInt(((Date.parse(Date.ofDate(mdate))) - (Date.parse(Date.ofDate(vdate)))) / 1000 / 3600 / 24));
            $('#termDays').val(termDays);
        }
        else{$('#termDays').val('');}
    }

    var initOperationType = function (){
        if($('#isOpenEveryDay').val() == 'N'){
            $('#operationType').val('02').trigger('change');
        }else{
            $('#operationType').val('01').trigger('change');
        }
    }

    var init = function(){
    	//融资金额不可修改生效太慢 yzg 20190912 start
    	var parentPlanChange = $('#parentPlan')[0].checked;
        if(parentPlanChange){
            $('#issueAmt').val('').attr('disabled',true);
        }
      //融资金额不可修改生效太慢 yzg 20190912 end
        
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
     */
    $(function() {
    	init();
    	if(pageModel!=PAGE_MODEL_NEW){
    	    //add by wm 20211112 GZYHTZJY-309 修改时 母计划按钮不可修改bug修复 start
            if(pageModel!=PAGE_MODEL_EDIT){
                $("#parentPlan").attr('disabled',true);
            }
            //add by wm 20211112 GZYHTZJY-309 修改时 母计划按钮不可修改bug修复 end
            $('#finprodMarketId').attr('disabled', true).trigger('change');
            $('.btn-save').hide();
        }
    	//一级分类联动人行信托代码 by 赵东东 20191018 start
         $('#category').change(function() {
             // $('#bussRepAssetMngProdInfo_repAssetType').repSelectArr(pageModel);
        	 if($('#category').val() == 'N02'){
        		 $('#bankTrustCodeDiv').show();
        	 }
        	 else{
        		 $('#bankTrustCodeDiv').hide();
        	 }
        	 if(ISChongQing != null && ISChongQing == 'Y'){
        	     $('#bankTrustCodeDiv').hide();
        	 }
         }).trigger('change');
       //一级分类联动人行信托代码 by 赵东东 20191018 end
        //二级分类联动显示资产负债类别


        $('#type').change(function() {
            typeSelect();
            // $('#bussRepAssetMngProdInfo_repAssetType').repSelectArr(pageModel);
        });
        //初始化界面二级联动 zhuyongkang   2019/8/5  start
        $('#type9').change(function() {
            cmsIdChange();
        }).trigger('change');
        // zhuyongkang   2019/8/5  end
        //调用资产五级分类联动处理、zhuyongkang、2019/7/23、start
        $("#type9,#type10,#type11,#type12").change(function(){
               /* if($('#type9').val()!='16'){*/
            window.BussAssetFiveType.doAssetFiveTypeChange(IS_SHOW_TYPE,'F03', 'type9', 'type10', 'type11', 'type12', 'category', 'type');
               /* }else{
                    $('#category').val('N03');
                    $('#category').trigger('change');
                    $('#type').val('03');
                    $('#type').trigger('change');
                }*/
        });
        //调用资产五级分类联动处理、zhuyongkang、2019/7/23、end
        //出账编号联动
        $('#cmsId').change(function() {
            cmsIdChange();
        }).trigger('change');
        //cmsIdChange();
        //资产来源联动
        $('#assetSource').change(function() {
            assetSourceChange();
        });
        // 绑定按钮事件
        $('#FinPlanProfitAsset002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#FinPlanProfitAsset002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        $('#vdate').change(function() {
            calTermDays();
        });
        $('#mdate').change(function() {
            calTermDays();
        });
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
        typeSelect(true);
        assetSourceChange();
        //当是编辑页面时，资产代码只读
        if(pageModel==PAGE_MODEL_EDIT) {
            $('#category').attr("disabled", true);
        }
        $('#mdate,#vdate').change(function(obj){
            obj.type = FINPROD_TYPE_TG_F17;
            AssetCommon.checkDate(obj);
        })
        layui.form.on('switch(parentPlan)',function(){
            parentPlanChange();
        });
        star();
        
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

        initOperationType();

        //添加是否每日开放申赎字段修改事件
        $('#isOpenEveryDay').change(function (){
            if($('#isOpenEveryDay').val() == 'N'){
                $('#operationType').val('02').trigger('change');
            }else{
                $('#operationType').val('01').trigger('change');
            }
        });

    });
}());

