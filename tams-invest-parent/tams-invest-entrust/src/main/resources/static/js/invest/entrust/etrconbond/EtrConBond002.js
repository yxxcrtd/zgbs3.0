/**
 * 可转债委托新建
 */
(function(){
    var getriskdata = function () {
        //配置单位 edit by yeming 20210721 start
        var purchBondTotal=$('#purchBondTotal').val();
        if (purchBondTotal) {
            if(entrustAmtUnit == '02' ){
                purchBondTotal=JoyinMath.doMul(purchBondTotal, 10000, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
            }
        }
        //配置单位 edit by yeming 20210721 end
        var obj = {tradeDirection:etrPs,timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:purchBondTotal,assetType:fintype,assetSecType:$('#assetType1').val(),isEnabled:'01',assetCode:$('#finprodIdhid').val()}
        // add by zhangrui1 交易对手影响风险试算 start
        obj.counterId = $('#etrInvestPersonInfo_counterId').val();
        // add by zhangrui1 交易对手影响风险试算 end
        var object = {tradeDirection:['ps','EtrConBond002.ps'],portfolioCode:['portfolioId','EtrConBond002.portfolioId'],tradeAmount:['purchBondTotal','EtrConBond001.purchBondTotal'],assetType:['assetType1','EtrConBond002.assetType1'],assetCode:['finprodId','EtrBondFirstLBidding002.finprodId'],data:obj}
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'EtrInvestPersonInfo.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        return object;
    }
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
     * 组合代码
     */
    $('#portfolioId').change(function(){
        var ccy = $('#ccy').val();
        var portfolioId = $('#portfolioId').val();
        if(portfolioId){
            var data = $('#portfolioId').select2('data')[0].object;
            $('#profitType').val(data).trigger('change');
        }
        if(ccy && portfolioId){
            getPtlCashPositionInfo();
        } 
    });

    
  //查询组合可用现金
    var getPtlCashPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),secAcctId:$('#secAcctId').val(),ccy:$('#ccy').val(),vdate:$('#entrustDate').val()}
        // modify by etliu 20211129 start
        // TISHKS-465: 组合T日可用现金、组合T+1日可用现金为交易可用，应为指令可用
        // Joyin.ajaxJson(basePath + 'invest/trade/conbond/TrdConBond002/getProAvailableCash',data,
        Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/getProAvailableCash',data,
                function(result) {
            $('#proAvailableCash').val(result.cashT).trigger('blur');
            // $('#tradeMarketCash').val(result.cashS).trigger('blur');
            $('#tradeMarketCash').val(result.cashT4).trigger('blur');
        });
        // modify by etliu 20211129 end
    }  
   
    
    /**
     * 保存或提交
     * 
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
    	//风险试算 start
    	 var s;
    	 if(typeof(BaseRiskMathComm) != 'undefined') {
			 if(BaseRiskMathComm) {
		         s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
		         if(!s && s != ''){
		             return;
		         }
		         if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())){
		             return;
		         }
		     }
    	 }
    	//风险试算 end
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#searchForm',true);
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.entrustId = $('#entrustId').val();
        object.entrustStatus = $('#entrustStatus').val();
        object.etrInvestPersonInfo.invManagerId = $('#etrInvestPersonInfo_invManagerId').val();
        if(object.etrInvestPersonInfo.trader != null && object.etrInvestPersonInfo.trader != ''){
            object.etrInvestPersonInfo.trader = object.etrInvestPersonInfo.trader;//.join(",");
        }
        //配置单位 edit by yeming 20210728 start
        if ($('#purchBondTotal').val()) {
            object.purchBondTotal=JoyinMath.doMul($('#purchBondTotal').val(), entrustAmtUnit == '02' ? 10000 : 1, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
        }
        //配置单位 edit by yeming 20210728 end
        if(FileCommon) {
            object.fileArray = FileCommon.getUploadFileInfo();
            if(object.fileArray && _.isArray(object.fileArray)){
                $.each(object.fileArray, function(index,obj){
                    obj.seqNo = obj.fileNo;
                });
            }
        }
        //保存跳过校验 edit by yeming 20210916 start
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath+'invest/entrust/etrconbond/EtrConBond002/save',object,"EtrConBond002_btn_save");
        } else {
            Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/check', object, function (result1) {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrconbond/EtrConBond002/checkAuthorization', object, function (result2) {
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrconbond/EtrConBond002/check', object,
                        function (result) {
                            Joyin.confirm(result.data, function () {
                                // if (operateType === 'save') {
                                //     WorkFlow.doSaveList(basePath + 'invest/entrust/etrconbond/EtrConBond002/save', object, "EtrConBond002_btn_save");
                                // } else {
                                    WorkFlow.doSubmitList(basePath + 'invest/entrust/etrconbond/EtrConBond002/save', object, "EtrConBond002_btn_submit");
                                // }
                            });
                        });
                });
            })
        }
        //保存跳过校验 edit by yeming 20210916 end
    }
    
    window.componentLoadCallback = function(isDone, pagePath, params){
        if (isDone) {
            if(typeof(BaseRiskMathComm) != 'undefined') {
            var riskparam = getriskdata();
            if (typeof riskparam != 'undefined') {
                if (pageModel==PAGE_MODEL_NEW){
                    riskparam.data =null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
                }
            }
        }
    	// $('#etrInvestPersonInfo_trader').attr('multiple',false);
    }

    /**
     * 将资产代码带出的值置灰不可修改
     */
    var init = function() {
        $("#invAim").trigger("change").attr("disabled", true);
        if(entrustAmtUnit == '01' ){
            $(".unitNum").text('元');
        }
        if (entity.finprodId){
            $("#finprodId").trigger("change").attr("disabled", true);
            $("#purchModle").trigger("change").attr("disabled", true);
            $("#assetType1").trigger("change").attr("disabled", true);
            $("#ccy").trigger("change").attr("disabled", true);
            $("#tradeMarket").trigger("change").attr("disabled", true);
            $("#entrustDate").trigger("change").attr("disabled", true);
        }
        if ($("#secAcctId").val()) {
            $("#chlId").val('').prop("disabled", true);
        }else {
            $("#chlId").prop("disabled", false);
        }
        if ($("#chlId").val()) {
            $("#secAcctId").val('').trigger("change").prop("disabled", true);
        } else {
            $("#secAcctId").prop("disabled", false);
        }
    }

    // ZGZGST-1822:可转债申购指令删除战略配售的申购类型 fuyongsheng 20210902 add start
    var deletePurchModle = function () {
        $.each($('#purchModle').find('option'),function (index,element) {
            // 03:战略配售
            if(element.value == '03'){
                element.remove();
            }
        });
    }
    // ZGZGST-1822:可转债申购指令删除战略配售的申购类型 fuyongsheng 20210902 add end

    $(function(){
        star();
        init();
        // ZGZGST-1822:可转债申购指令删除战略配售的申购类型 fuyongsheng 20210902 add start
        deletePurchModle();
        // ZGZGST-1822:可转债申购指令删除战略配售的申购类型 fuyongsheng 20210902 add end
	    /**
	     * 保存
	     */
	    $('#EtrConBond002_save').click(function() {
	        saveOrSubmit('save');
	    });
	    /**
	     * 提交
	     */
	    $('#EtrConBond002_btn_submit').click(function() {
	        saveOrSubmit('submit');
	    });
	   
	    
	 // 代办显示点击查看风险试算结果  Start
        /**
         * 代办点击查看风险试算结果
         */
        if(todoList==true){
            $("#riskOfTrialLink").show();
        }else{
            $("#riskOfTrialLink").hide();
        }
        $("#riskOfTrialLink").click(function() {
            Joyin.dialog(JoyinUtil.getLabel("BaseRiskMathComm.riskAndCredit"),
                basePath + "component/common/RiskMathAndCredit/init",
                "90%", "90%",{workflowId:workflowId,tradeId:tradeId,TodoList:todoList});
        });
        // 代办显示点击查看风险试算结果   End

        /**
	     * 风险试算
	     */
	    $('#EtrConBond002_btn_riskcal').click(function(){
        	/*var object = $.serializeToObject('#searchForm',true);
            object.entrustId = $('#entrustId').val();
            object.entrustStatus = $('#entrustStatus').val();
            object.finvdate = $('#finvdate').val();
            object.finmdate = $('#finmdate').val();
            object.etrInvestPersonInfo.invManagerId = $('#etrInvestPersonInfo_invManagerId').val();
            var initParam = getriskdata();
            
            Joyin.ajaxJson(basePath + 'component/common/BaseRiskMathComm/check', initParam, function(resultresult) {
            	//window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
        		});*/
            var object = $.serializeToObject('#searchForm',true);
            var purchBondTotal=$('#purchBondTotal').val();
            if (purchBondTotal) {
                if(entrustAmtUnit == '02' ){
                    purchBondTotal=JoyinMath.doMul(purchBondTotal, 10000, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
                }
            }
            object.purchBondTotal = purchBondTotal;
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK002/F01',object,getriskdata());
	    });

        $('#chlId').change(function(){
            if ($("#chlId").val()) {
                $("#secAcctId").val('').trigger("change").prop("disabled", true);
            } else {
                $("#secAcctId").prop("disabled", false);
            }
        })

        //联动通道可用现金
        $('#chlId').on("change", function () {
            if ($('#chlId').val()) {
                // modify by etliu 20211129 start
                // TISHKS-465: 组合T日可用现金、组合T+1日可用现金为交易可用，应为指令可用
                // var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),secAcctId:$('#secAcctId').val(),chlId:$('#chlId').val(),vdate:$('#entrustDate').val()}
                // Joyin.ajaxJson(basePath + 'invest/trade/conbond/TrdConBond002/getProAvailableCash',data,
                var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),chlFinprodId:$('#chlId').val()}
                Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/getProAvailableCash',data,
                    function(result) {
                        // $('#channelAvailableCash').val(result.cashC).trigger('blur');
                        $('#channelAvailableCash').val(result.cashT2).trigger('blur');
                    });
                // modify by etliu 20211129 end
            }else {
                $('#channelAvailableCash').val('').trigger('blur');
            }
        });

        /**
         * 资产代码
         */
        $('#finprodId').change(function() {

            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var finprodId = $('#finprodId').val();
            if(finprodId){
                var data = $('#finprodId').select2('data')[0].object;
                $('#finprodIdhid').val(data.finprodId).trigger('change');
                $('#finprodName').val(data.finprodName).trigger('change');
                $('#tradeMarket').val(data.tradeMarket).trigger('change').attr('disabled',true);
                $('#assetType1').val(data.assetType1).trigger('change').attr('disabled',true);
                $('#ccy').val(data.ccy).trigger('change').attr('disabled',true);
                if(ccy && portfolioId){
                    getPtlCashPositionInfo();
                }
            } else {
                $('#finprodIdhid').val('').trigger('change');
                $('#finprodName').val('').trigger('change');
                $('#tradeMarket').val('').trigger('change').attr('disabled',false);
                $('#ccy').val(ccy).trigger('change').attr('disabled',false);
                $('#assetType1').val('').trigger('change');
            }
            // 画星星
            star();
        });

        $('#secAcctId').change(function(){
            if ($("#secAcctId").val()) {
                $("#chlId").val('').prop("disabled", true);
            }else {
                $("#chlId").prop("disabled", false);
            }
            //add by mashukai ZGBSS-239 证券账户更改时交易所资金账户可用现金没有带出来 start
            if ($('#portfolioId').val() != null && $('#portfolioId').val() != ''){
                getPtlCashPositionInfo();
            }
            //add by mashukai ZGBSS-239 证券账户更改时交易所资金账户可用现金没有带出来 end
        });
	})

    $('#portfolioId').change(function(){
        if($('#secAcctId').val()){
            deleteSelect('secAcctId')
        }
        //add by mashukai ZGBSS-239 修正清除产品代码时交易所资金账户可用现金不清零  start
        if ($('#portfolioId').val() == null || $('#portfolioId').val() == ''){
            $('#tradeMarketCash').val('').trigger("change");
        }
        //add by mashukai ZGBSS-239 修正清除产品代码时交易所资金账户可用现金不清零  end
    });

    var deleteSelect = function (id) {
        $('#'+id).textSelect('setValue','');
        $.each($('#'+id).find('option'),function (index,element) {
            if( $('#'+id).find('option').eq(index).val()){
                $('#'+id).find('option').eq(index).remove();
            }
        });
    }
}());