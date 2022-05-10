
/**
 * 基金类标的信息新建
 */
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
     * 保存或提交
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
    	/**
    	 * 获取列表参数
    	 */
        var object = $.serializeToObject('#submitForm', false);
        object.pageModel = pageModel;
        object.operateType = operateType;
        object.pageId = pageId;
        object.finprodType2 = finprodType2;
        if(object.bussFinIntPayDto != null){
        	object.bussFinIntPayDto.fundType = $('#fundType').val();
        }
        if(object.bussRepFundInfoDto != null){
        	object.bussRepFundInfoDto.finprodType2 = finprodType2;
        }
        //modify by mashukai 20201117 BS-2006 公募基金草稿箱提交时校验资产代码问题
        if(!draftoption){
            if((object.finprodMarketId==null || object.finprodMarketId=='' || object.finprodMarketId==undefined)){
                Joyin.error("资产代码必填,不能为空");
                return false;
            }
        }
        //modify by mashukai 20201117 BS-2006公募基金草稿箱提交时校验资产代码问题
		if (object.tradeMarketId != null) {
			object.finprodMarketId = object.finprodMarketId + object.tradeMarketId;
		}else {
			object.finprodMarketId = object.finprodMarketId + AssetCommon.setTradeMarketId(object.finprodMarketId);
		}
        if ($('#capiIncomeFeature').next().find("em").text() === '是') {
            object.capiIncomeFeature = 'Y';
        } else {
            object.capiIncomeFeature = 'N';
        }
        /*if(file != null){
        	object.fileName = file[0].srcName;
        	object.revealFileId = file[0].fileNo;
        	object.path = file[0].targetFile;
        }*/
     // 是否分层
//        if ($('#bussRepFundInfoDto_repIsGovInvest').next().find("em").text() === '是') {
//            object.bussRepFundInfoDto.repIsGovInvest = 'Y';
//        } else {
//            object.bussRepFundInfoDto.repIsGovInvest = 'N';
//        }
        if(FileCommon) {
    		object.fileArray = FileCommon.getUploadFileInfo();
    		if(object.fileArray && _.isArray(object.fileArray)){
    			$.each(object.fileArray, function(index,obj){
    				obj.seqNo = obj.fileNo;
    			});
    		}
    	}
        Joyin.ajaxJson(basePath + 'invest/asset/fund/FinFundTarget002/check', object, function(result) {
            Joyin.ajaxJson(basePath + 'invest/asset/common/isHoliday', {
                mdate: object.mdate,
                calendarId: object.calendarId
            }, function (isHoliday) {
                if (isHoliday) {
                    Joyin.confirm("到期日为节假日，是否确认提交", function () {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/asset/fund/FinFundTarget002/save', object, "FinFundTarget002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'invest/asset/fund/FinFundTarget002/save', object, "FinFundTarget002_btn_submit");
                        }
                    })
                } else {
                    Joyin.confirm(result.data, function () {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/asset/fund/FinFundTarget002/save', object, "FinFundTarget002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'invest/asset/fund/FinFundTarget002/save', object, "FinFundTarget002_btn_submit");
                        }
                    });
                }
            })
        })
    }

    var init = function(){
        //资产代码拆分
        var finprodMarketId =  $('#finprodMarketId').val();
        if (finprodMarketId){
            $('#finprodMarketId').val(finprodMarketId.split('.')[0]);
        }
    	var hapCreditAmt = $("#hapCreditAmt").val();
    	if(hapCreditAmt && hapCreditAmt>0){
    		$("#pflsh").attr("disabled",true).trigger('change');
    	}
    	if(pageModel != PAGE_MODEL_NEW){
    		$("#finprodMarketId").attr("disabled",true).trigger('change');
    	}
    	if(pageModel != PAGE_MODEL_REFERENCE){
    		var fundType = $("#fundType").val();
            if(fundType == F01){
            	$('#secondType').attr("disabled",true);
            	$('#secondType').val('').trigger('change');
            	$('#secondType').attr("required",false);
            	$('#isCompoundInt').attr("disabled",false).trigger('change');
            }else{
            	$('#secondType').attr("disabled",false);
            	$('#isCompoundInt').val('');
        		$('#isCompoundInt').attr("disabled",true).trigger('change');
            }
            if(isTransInAsset == 'Y'){
            	$('#idate').attr("disabled",true);
            	$('#vdate').attr("disabled",true);
            	$('#ccy').attr("disabled",true);
            }
            var openType = $("#openType").val();
            //封闭式
            if(openType == '01'){
                $('#isSevenDayCashable').attr("disabled",false).trigger('change');
                $('#isSevenDayCashable').attr("required",true);
            }else{
                $('#isSevenDayCashable').attr("disabled",true).trigger('change');
                $('#isSevenDayCashable').attr("required",false);
            }
        }
    	if($("#creditFlag").val() == 'Y'){
    		$("#actualCreditProvid").attr("required", true).trigger('change');
    	}
    	if($("#creditFlag").val() != 'Y'){
    		$("#actualCreditProvid").attr("required", false).trigger('change');
    	}
    	star();
    };
    var getInterfFundDto = function(){
    	if ($('#finprodMarketId').prop('disabled')) {
    		return;
		}
		var finprodMarketId = $('#finprodMarketId').val();
        //拆分市场代码
        var arr  = finprodMarketId.split('.');
        if (arr.length == 2){
            finprodMarketId = arr[0];
            $('#finprodMarketId').val(finprodMarketId);
            $('#tradeMarketId').val('.' + arr[1]);
        }
        if (finprodMarketId) {
             //add by  zhangnian 20191015 start
            Joyin.ajax(basePath + 'invest/asset/fund/FinFundTarget002/init', {
                'finprodMarketId': $('#finprodMarketId').val()+$('#tradeMarketId').val()
                ,'isQuickInput':true
            }, function (data) {
                if($(data).find('#finprodId').val()!=""){
                    $("body").html(data);
                }else{
                    //去接口表
                    Joyin.ajax(basePath + 'invest/asset/fund/FinFundTarget002/getInterfFundDto?finprodMarketId='
                        + finprodMarketId + '&tradeMarketId=' + $('#tradeMarketId').val(),'' , function(result){
                        if (result && result.data) {
                            if (result.data[0]) {
                                Joyin.error(result.data[0].message);
                            } else {
                                //$('#submitForm')[0].reset();
                                //清除重置下拉框
                                $('#type9').val(null).trigger('change');
                                $('#fundType').textSelect('setValue', null);
                                $('#secondType').textSelect('setValue', null);
                                $('#tradeMarket').textSelect('setValue', '09');
                                $('#ccy').textSelect('setValue', 'CNY');
                                $('#bussFinIntPayDto_payType').val('02').trigger('change');
                                window.componentLoadCallback(true);

                                $.each(result.data,function(id,value){
                                    if(result.data.finprodId){
                                        setData(id, value, '');
                                    }
                                });
                                //基金二级类型重新赋值
                                $('#secondType').textSelect('setValue', result.data.secondType);
                                $('#tradeMarket').textSelect('setValue', result.data.tradeMarket);
                            }
                        }
                    });
                }
                tradeMarketChange();
            });
            //add by  zhangnian 20101015 end
		}else{
			Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038',JoyinUtil.getLabel('FinFundTarget001.finprodMarketId')));
		}
	};

    var setData = function(id, value ,pageId) {
        if (id == 'bussFinIntPayDto') {
            $.each(value, function(id, value) {
                setData(id, value , 'bussFinIntPayDto');
            });
        }else if (id == 'repfund' || id == 'repAssetDept') {
            //中债报送 bussRepFundInfoDto_repAssetType
            $.each(value, function(id, value) {
                setData(id, value , 'bussRepFundInfoDto');
            });
        }else {
            // 基本信息
            var element;
            if(pageId != '' && pageId != undefined){
                id = pageId + '_' + id;
            }
            if($('#'+id).size() > 0){
                element = $('#'+id);
            }else{
                element = $('input[name=' + id + ']');
            }
            if(element.hasClass('input-dateSelect')){
                // 日期特殊处理
                if (element.hasClass('input-dateSelect')){
                    element.val(new Date(value).format('yyyy-MM-dd'));
                }
            }else if (element.hasClass('input-select')){
                //下拉框特殊处理
                if (id == 'tradeMarket') {
                    //给资产代码后缀赋值
                    AssetCommon.setTradeMarketId(value);
                    element.val(value).trigger('change');
                }else if (id == 'manager' ||   id == 'calendarId') {
                    //data-url 赋值要特殊处理
                    element.textSelect('setValue', value);
                }else {
                    element.val(value).trigger('change');
                }

            }else if(element.hasClass('input-checkbox')){
                //单选按钮特殊处理
                element.val(value).trigger('change');
            }else{
                element.val(value);
            }
		}
    }
    /**
     * 交易所基金类型 change
     */
    var trmFundTypeChange = function(){
        var fundType = $('#fundType').val();
        var trmFundType = $('#trmFundType_id').val();
        var secondType = $('#secondType').val();
        var etfType = $('#etfType_id').val();
        if(trmFundType == TRM_FUND_TYPE_03){
            $('#graFundType_div').show();
        }else{
            $('#graFundType').val("").trigger('change');
            $('#graFundType_div').hide();
        }
        if(trmFundType == TRM_FUND_TYPE_05){
            $('#faceValue_div').show();
            $('#faceValue').val("0.01");
            $('#faceValue').prop('disabled',true).trigger('change');
        }else if(etfType==ETF_TYPE_06){
            $('#faceValue_div').show();
            $('#faceValue').val("100");
            $('#faceValue').prop('disabled',true).trigger('change');
        }else{
            $('#faceValue_div').hide();
            $('#faceValue').val("");
        }
        if(fundType==F01){
            if($('#trmFundType_id').val()==TRM_FUND_TYPE_01){
                $('#etfType_id').val(ETF_TYPE_06);
                $('#etfType_id').prop('disabled',true).trigger('change');
            }else{
                $('#etfType_id').val("");
                $('#etfType_id').prop('disabled',true).trigger('change');
            }
        }
        if(trmFundType == TRM_FUND_TYPE_01){
            $('#purStartLow').val('');
            $('#redStartLow').val('');
            $('#purStartLow').prop('disabled',true).trigger('change');
            $('#redStartLow').prop('disabled',true).trigger('change');
        }else{
            $('#purStartLow').prop('disabled',false).trigger('change');
            $('#redStartLow').prop('disabled',false).trigger('change');
        }
        secondTypeChange();
    }

    /*
    * 二级分类change
    * */
    var secondTypeChange = function(){
        var fundType = $('#fundType').val();
        var secondType = $('#secondType').val();
        //交易所基金类型 交易所基金类型_ETF|TRM_FUND_TYPE_01
        var trmFundType = $("#trmFundType_id").val();
        // 交易所基金类型是否是ETF
        var isEtf = trmFundType === TRM_FUND_TYPE_01;

        //场外非货币基金ETF类型不要根据资产二级类型联动
        if($('#tradeMarket').val() != '09'){
            if(secondType == FUND_ASSET_SECOND_TYPE_01&&isEtf){
                $('#etfType_id').val(ETF_TYPE_04);
                $('#etfType_id').prop('disabled',true).trigger('change');
            }
            else if(secondType == FUND_ASSET_SECOND_TYPE_06&&isEtf){
                $('#etfType_id').val(ETF_TYPE_05);
                $('#etfType_id').prop('disabled',true).trigger('change');
            }
            else if(secondType == FUND_ASSET_SECOND_TYPE_03&&isEtf){
                $('#etfType_id').prop('disabled',false).trigger('change');
            }
            else if(secondType == FUND_ASSET_SECOND_TYPE_02&&$('#trmFundType_id').va()==TRM_FUND_TYPE_01&&isEtf){
                $('#etfType_id').val(ETF_TYPE_06);
                $('#etfType_id').prop('disabled',true).trigger('change');
            }
            else{
                if(fundType==F01&&$('#trmFundType_id').val()==TRM_FUND_TYPE_01&&isEtf){
                    $('#etfType_id').val(ETF_TYPE_06);
                    $('#etfType_id').prop('disabled',true).trigger('change');
                }
                else{
                    $('#etfType_id').val("");
                    $('#etfType_id').prop('disabled',true).trigger('change');
                }
            }
        }else{
            //场外非货币基金ETF类型不要根据资产二级类型联动
            var fundType = $('#fundType').val();
            if(fundType!=F01){
                $('#etfType_id').val("");
                $('#etfType_id').prop('disabled',true).trigger('change');
            }else{
                if($('#trmFundType_id').val()==TRM_FUND_TYPE_01){
                    $('#etfType_id').val(ETF_TYPE_06);
                    $('#etfType_id').prop('disabled',true).trigger('change');
                }else{
                    $('#etfType_id').val("");
                    $('#etfType_id').prop('disabled',true).trigger('change');
                }
            }
        }
    }

    var etfTypeChange = function(){
        var etfType = $('#etfType_id').val();
        var trmFundType = $('#trmFundType_id').val();
        if(etfType==ETF_TYPE_06){
            $('#faceValue_div').show();
            $('#faceValue').val("100");
            $('#faceValue').prop('disabled',true).trigger('change');
        }else if(trmFundType == TRM_FUND_TYPE_05){
            $('#faceValue_div').show();
            $('#faceValue').val("0.01");
            $('#faceValue').prop('disabled',true).trigger('change');
        }else {
            $('#faceValue_div').hide();
            $('#faceValue').val("");
        }
    }

    /**
     * 分级基金
     */
    var graFundTypeChange = function(){
        var graFundType = $('#graFundType').val();
        if(graFundType == GRA_FUND_TYPE_A||graFundType == GRA_FUND_TYPE_B){
            $('#pFinprodId_div').show();
        }else{
            $('#pFinprodId_div').hide();
        }
    }

    var tradeMarketChange = function(){
        AssetCommon.setTradeMarketId($('#tradeMarket').val());
        if($('#tradeMarket').val() == '09'){
            $('#trmFundType_id').val('').trigger('change');
            $('#trmFundType_id').prop('disabled',true);
            $('#trmFundAbbr').val('');
            $('#trmFundAbbr').prop('disabled',true);
        }else{
            $('#trmFundType_id').prop('disabled',false);
            $('#trmFundAbbr').prop('disabled',false);
        }
        secondTypeChange();
        trmFundTypeChange();
    }
    //控件加载完成后
    window.componentLoadCallback = function(isDone, pagePath, params){
    	if(isDone) {
    	    var tempTrmFundType = $('#tempTrmFundType').val();
    		var fundType = $("#fundType").val();
            if(fundType == F01 || fundType == ''){
            	$('#bussFinIntPayDto_div').show();
            	//双父级数据字典联动特殊处理
            	$('#trmFundType_id').val(tempTrmFundType).trigger('change');
            }else{
            	$('#bussFinIntPayDto_div').hide();
            }
			if ($('#finprodMarketId').prop('disabled')) {
				$("#magnifier").css({color: '#dbdbdb'});
			}
            // if(typeof pageModel !== 'undefined'
            //     && (pageModel === PAGE_MODEL_EDIT || pageModel === PAGE_MODEL_NEW)){
            //     $('.purSpeed').append('<i class="must">*</i>');
            //     $('.redSpeed').append('<i class="must">*</i>');
            // }
            $('#bussRepFundInfoDto_repAssetType_div').show();
            //add by zhangnain 20191120 start
            //双父级数据字典联动 重新触发change事件
            if($('#fundType').val()&&!$('#secondType').val()) {
                $('#fundType').trigger('change');
                if(TRM_FUND_TYPE){
                    $('#trmFundType_id').val(TRM_FUND_TYPE).trigger('change');
                }
            }else{
                $('#secondType').trigger('change');
                if(TRM_FUND_TYPE){
                    $('#trmFundType_id').val(TRM_FUND_TYPE).trigger('change');
                }
                //场外非货币基金ETF类型不要根据资产二级类型联动
                if($('#tradeMarket').val() != '09'){
                    if(ETF_TYPE){
                        $('#etfType_id').val(ETF_TYPE).trigger('change');
                    }
                }else{
                    $('#etfType_id').val("").trigger('change');
                }
            }
            //add by zhangnain 20191120 end
    	}
    }
    /**
     * 画面加载完成
     */
    $(function() {
    	init();
    	var draftInit = isDraft;
    	//修改和审批被驳回时，保存按钮隐藏
    	if(pageModel === PAGE_MODEL_EDIT){
    		$('#FinFundTarget002_btn_save').hide();
    		$('#btnSave_wfl').hide();
    	}
    	
        // 绑定按钮事件
        $('#FinFundTarget002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#FinFundTarget002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        $('#magnifier').click(function() {
			getInterfFundDto();
		});
		$('#tradeMarket').change(function(){
            tradeMarketChange();
		});
        $('#trmFundType_id').change(function(){
            trmFundTypeChange();
        });
        $('#fundType').change(function(){
        	if(pageModel != PAGE_MODEL_REFERENCE){
        		var fundType = $("#fundType").val();
            	if(fundType == F01){
            		$('#secondType').attr("disabled",true);
            		$('#secondType').val('').trigger('change');
            		$('#secondType').attr("required",false);
            		$('#bussFinIntPayDto_div').show();
            		//ZGBS-146 草稿箱回显时按日复利置灰 根据init方法中处理逻辑修改 start
            		if(draftInit){
            			var target = $("#bussFinIntPayDto_payCycle").val();
            			$('#isCompoundInt').attr("disabled",!(target=="02")).trigger('change');
            		}
            		else{
            			//按照之前代码回滚
            			$('#isCompoundInt').attr("disabled",true).trigger('change');
            		}
            		//ZGBS-146 草稿箱回显时按日复利置灰 根据init方法中处理逻辑修改 end
            	}else{
            		$('#secondType').attr("disabled",false);
            		$('#bussFinIntPayDto_div').hide();
            		$('#isCompoundInt').val('');
            		$('#isCompoundInt').attr("disabled",true).trigger('change');
            	}
        	}
        	star();
        });
        $('#openType').change(function(){
            if(pageModel != PAGE_MODEL_REFERENCE){
                var openType = $("#openType").val();
                //封闭式
                if(openType == '01'){
                    $('#isSevenDayCashable').attr("disabled",false);
                    $('#isSevenDayCashable').val('').trigger('change');
                    $('#isSevenDayCashable').attr("required",true);
                }else{
                    $('#isSevenDayCashable').attr("disabled",true);
                    $('#isSevenDayCashable').val('').trigger('change');
                    $('#isSevenDayCashable').attr("required",false);
                }
            }
            star();
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
        $('#tradeMarket').trigger('change');

        /*type4 and type5's automatic match added by wangguiqi on 2019/7/23 -start*/;
        $("#type9,#type10,#type11,#type12").change(function(){
        	window.BussAssetFiveType.doAssetFiveTypeChange(IS_SHOW_TYPE,'F02','type9','type10','type11','type12','fundType','secondType');
        });
        /*type4 and type5's automatic match added by wangguiqi on 2019/7/23 -end*/
    });
    $('#pMarketId').change(function () {
        var str = $('#pMarketId').select2('data')[0];
        if (str){
            $('#pFinprodId').val(str.object);
        }else{
            $('#pFinprodId').val("");
        }
    })
    $('#trmFundType_id').change(function () {
        trmFundTypeChange();
    }).trigger('change');
    $('#secondType').change(function () {
        setTimeout(secondTypeChange, 100);
    }).trigger('change');
    $('#etfType_id').change(function () {
        etfTypeChange();
    }).trigger('change');
    $('#graFundType').change(function () {
        graFundTypeChange();
    }).trigger('change');
    draftInit = false;
}());
//资产分类联动带入产品投资类型
$("#type11,#type10,#type12").on("change", function () {
    var three = $("#type10").val();
    var four = $("#type11").val();
    var five = $("#type12").val();
    if (three == '23' || four == '032'|| five == '032') {
        $('#investNature').val('01').trigger('change');
        /*var tradeMarket = $('#tradeMarket').val();
        if (tradeMarket == TRADE_PLACE_02 || tradeMarket == TRADE_PLACE_03) {
            $("#commissionAmt").prop("disabled", true).val(0).trigger('blur').trigger('change');
        }
        else {
            $("#commissionAmt").prop("disabled", false).val(commissionAmt).trigger('blur').trigger('change');
        }*/
    }
    if (three == '24' || four == '033'|| five == '033') {
        $('#investNature').val('01').trigger('change');
    }
    if (three == '25' || four == '034'|| five == '034') {
        $('#investNature').val('02').trigger('change');
    }
});
