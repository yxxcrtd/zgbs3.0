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
     *//*
    function typeSelect() {
        var type = $('#type').val();
        if (type == publicOffPlan) {
            $(".bussRepAssetMngProdInfo_div").show();
            $(".bussRepOutInvestAgrInfo_div").hide();
        } else {
            $(".bussRepAssetMngProdInfo_div").hide();
            $(".bussRepOutInvestAgrInfo_div").show();
        }
    };*/
    //画面联动：输入出账编号，联动显示关联合同编号、BECIF号和发行金额
    /*var cmsIdChange = function() {
        var cmsId = $("#cmsId").val();
        var object = {'cmsId':cmsId};
        Joyin.ajax(getChange, object, function(data){
            if(data){
                $('#cmsContractId').val(data.cmsContractId);
                $('#cmsBecif').val(data.cmsBecif);
                $('#issueAmt').val(data.issueAmt);
            }
        });
    };*/

    //判断是提交或是保存
    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#submitForm');
        object.finprodAbbr = $("#finprodName").val();
        var assetProviderTemp = $('#assetProvider').val();
        var assetProvider= null;
        if(assetProviderTemp != null)
        	assetProvider=assetProviderTemp.join(",");
        object.assetProvider = assetProvider;
        object.pageModel = pageModel;
        object.operateType = operateType;
        // 增信措施(多选)
        object.creditEnhanceMeasure = $('#creditEnhanceMeasure').val()?$('#creditEnhanceMeasure').val().join(","):'';
        //update by zhangnian 20191102 start
        /*var repYieldFlag = $("#bussRepAssetMngProdInfo_repYieldFlag").prop("checked") ? 'Y' : 'N';
        object.bussRepAssetMngProdInfo.repYieldFlag = repYieldFlag;
        var repIsCtrlBottom = $("#bussRepAssetMngProdInfo_repIsCtrlBottom").prop("checked") ? 'Y' : 'N';
        object.bussRepAssetMngProdInfo.repIsCtrlBottom = repIsCtrlBottom;*/
        //update by zhangnian 20191102  end
        /*var repContainExpectedYield = $("#bussRepOutInvestAgrInfo_repContainExpectedYield").prop("checked") ? 'Y' : 'N';
        object.bussRepOutInvestAgrInfo.repContainExpectedYield = repContainExpectedYield;*/
        /*//确认是否验证
        object.bussRepAssetMngProdInfo.needValid = object.type == publicOffPlan;
        object.bussRepOutInvestAgrInfo.needValid = object.type != publicOffPlan;*/
        //文件信息
        if (FileCommon){
            object.fileCommonList = FileCommon.getUploadFileInfo();
        }

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

      //参与方信息
      //   if(BussParticipantInfo){
      //       object.bussParticipantInfoDto = {};
      //       object.bussParticipantInfoDto.type6 = $("#category").val();
      //       object.bussParticipantInfoDto.bussParticipantInfoDtoList = BussParticipantInfo.getAllRows();
      //   }
        //分红计划-货币型
        if (object.bussFinIntPayDto){
            object.bussFinIntPayDto.fundType = FUND_ASSET_TYPE_F01;
        }
        if (object.setFinprodType2){
            object.setFinprodType2 = FINPROD_TYPE_TG_F17;
        }

        //附件信息
        if(FileCommon) {
    		object.fileArray = FileCommon.getUploadFileInfo();
    		if(object.fileArray && _.isArray(object.fileArray)){
    			$.each(object.fileArray, function(index,obj){
    				obj.seqNo = obj.fileNo;
    			});
    		}
    	}
    	//补充回显画面提交序列化不完全数据 start add by yujinfa
        if (object.bussRepAssetMngProdInfo){
            if((!object.bussRepAssetMngProdInfo.repYieldFlag)&&(pageModel == 1)){
                object.bussRepAssetMngProdInfo.repYieldFlag = bussRepAssetMngProdInfo_repYieldFlag;
            }
            if((!object.bussRepAssetMngProdInfo.repIsCtrlBottom)&&(pageModel == 1)){
                object.bussRepAssetMngProdInfo.repIsCtrlBottom = bussRepAssetMngProdInfo_repIsCtrlBottom;
            }
        }

        //补充回显画面提交序列化不完全数据 end add by yujinfa

        Joyin.ajaxJson(basePath + 'invest/asset/plan/plancur/FinPlanAssetC002/check', object, function(result) {
            Joyin.ajaxJson(basePath + 'invest/asset/common/isHoliday', {
                mdate: object.mdate,
                calendarId: object.calendarId
            }, function (isHoliday) {
                if(isHoliday){
                    Joyin.confirm("终止日期为节假日，是否确认提交", function () {
                        if(operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/asset/plan/plancur/FinPlanAssetC002/save', object, "FinPlanAssetC002_btn_save");
                        }
                        else {
                            WorkFlow.doSubmitList(basePath + 'invest/asset/plan/plancur/FinPlanAssetC002/save', object, "FinPlanAssetC002_btn_submit");
                        }
                    })
                }else{
                    Joyin.confirm(result.data, function() {
                        if(operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/asset/plan/plancur/FinPlanAssetC002/save', object, "FinPlanAssetC002_btn_save");
                        }
                        else {
                            WorkFlow.doSubmitList(basePath + 'invest/asset/plan/plancur/FinPlanAssetC002/save', object, "FinPlanAssetC002_btn_submit");
                        }
                    });
                }
            })
        });
    };
    //控件加载完成后
    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
            $("#bussRepAssetMngProdInfo_repAssetType_div").show();
        }
    }

    var initOperationType = function (){
        if($('#isOpenEveryDay').val() == 'N'){
            $('#operationType').val('02').trigger('change');
        }else{
            $('#operationType').val('01').trigger('change');
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
    	if($("#category").val() == PLAN_ASSET_TYPE_N02){
    		$('#bankTrustCodeDiv').show();
    	}
    	else{
    		$('#bankTrustCodeDiv').hide();
    	}
    	if(ISChongQing != null && ISChongQing == 'Y'){
            $('#bankTrustCodeDiv').hide();
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
        //二级分类联动
        $('#type').change(function() {
            //$('#bussRepAssetMngProdInfo_repAssetType').repSelectArr(pageModel);
            //typeSelect();
        });
        /*category and type's automatic match added by lishuping on 2019/7/24 -start*/
        $("#type9,#type10,#type11,#type12").change(function(){
            window.BussAssetFiveType.doAssetFiveTypeChange(IS_SHOW_TYPE,'F09', 'type9', 'type10', 'type11', 'type12', 'category', 'type');
            /*if($('#type12').val()!=''){
                if($('#type9').val() == '16') {
                    $('#category').val('N01') ,
                    $('#type').val('02')
                }else {
                    window.BussAssetFiveType.doAssetFiveTypeChange('F09','type9','type10','type11','type12','category','type');
                    if($('#type').val() == '') {
                        $('#type').val('02')
                    }

                }
            }*/
        });
        /*category and type's automatic match added by lishuping on 2019/7/24 -end*/
        /*//出账编号联动
        $('#cmsId').change(function() {
            cmsIdChange();
        });*/
        // 绑定按钮事件
        $('#FinPlanAssetC002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#FinPlanAssetC002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
       /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
        $('#FinPlanAssetC002_btn_quickInput').click(function() {

            Joyin.ajax(basePath + 'invest/asset/plan/plancur/FinPlanAssetC002/init', {
                'finprodMarketId': $('#finprodMarketId').val(),
            }, function (data) {
                // update by caiqun 20200721 资产代码为本机构不存在的，快录时不直接删除填的的资产，做成提醒；  start
                if($(data).find('#finprodMarketId').val()!=""){
                     $("body").html(data);
                     $("#finprodId").val('');
                     $("#fjId").val('');
                }else{
                     Joyin.error("资产代码不存在！");
                }
                // update by caiqun 20200721 资产代码为本机构不存在的，快录时不直接删除填的的资产，做成提醒；  end
            });
        });
        /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
        //typeSelect();
        if(pageModel!=PAGE_MODEL_NEW) {
            $('#type').attr("disabled", true);
            $('#finprodMarketId').attr('disabled', true).trigger('change');
            $('.btn-save').hide();
        }
        $('#category').change(function() {
        	if($("#category").val() == PLAN_ASSET_TYPE_N02){
        		$('#bankTrustCodeDiv').show();
        	}
        	else{
        		$("#bankTrustCode").val('');
        		$('#bankTrustCodeDiv').hide().trigger('change');
        	}
        	if(ISChongQing != null && ISChongQing == 'Y'){
        	    $("#bankTrustCode").val('');
                $('#bankTrustCodeDiv').hide().trigger('change');
        	}
            //$('#bussRepAssetMngProdInfo_repAssetType').repSelectArr(pageModel);
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
      //是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
        	$('#type11Div').show();
        	$('#type12Div').show();
        }else{
        	$('#type11Div').hide();
        	$('#type12Div').hide();
        }

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

