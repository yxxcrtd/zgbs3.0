/**
 * 计划类（净值型）新建
 */
(function() {
    var getriskdata = function () {
        var obj = {timePoint:MY_SUP_TIME_TYPE_02,valDate:$('#tradeDate').val(),portfolioCode:$('#portfolioId').val(),tradeAmount:$('#tradeAmt').val(),assetType:FINPROD_TYPE_F17,isEnabled:'01',assetCode:$('#finprodId').val(),tradeDirection:$('#ps').val()}
        //---add by 裴家俊  2019/03/20  添加资产二级类型条件---start-----
        if($('#type7').val() && $('#type7').val() != ''){
            obj.assetSecType = $('#type7').val();
        }
        // add by zhangrui1 交易对手影响风险试算 start
        obj.counterId = $('#trdInvestPersonInfo_counterId').val();
        // add by zhangrui1 交易对手影响风险试算 end
        //---add by 裴家俊  2019/03/20  添加资产二级类型条件---end-----
        var object = {valDate:['tradeDate','TrdPlanNetInvestTrade002.tradeDate'],portfolioCode:['portfolioId','TrdPlanNetInvestTrade002.portfolioId'],tradeAmount:['tradeAmt','TrdPlanNetInvestTrade003.tradeAmt'],assetType:['type6','TrdPlanNetInvestTrade002.type6'],tradeDirection:['ps','TrdPlanNetInvestTrade001.ps'],data:obj}
        if($('#shareAmt').val() != null && $('#shareAmt').val() != ''){
            obj.tradeAmount = $('#shareAmt').val()
            obj.amtType = '1';
            object.tradeAmount = ['shareAmt','TrdPlanNetInvestTrade002.tradeSha']
        }
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'TrdInvestPersonInfo.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        return object;
    }

    function saveOrSubmit(operateType) {
    	// 划款指令隐藏时，自动生成划款指令信息
    	generateOrders(function () {
	        var s;
            if(typeof(BaseRiskMathComm) != 'undefined') {
	        if(BaseRiskMathComm&&$('#ps').val()!=ps_T06) {
	            s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
	            if(!s && s != ''){
	                return;
	            }
	            if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())){
	                return;
                    }
	            }
	        }
	        if (window.BussTrdTransferOrder['bussTrdTransferOrder'].isCalcConditionChanged()) {
	            return;
	        }
	        var object = $.serializeToObject('#submitForm', true);
	        if ($('#select2-finprodId-container').attr('title')){
	            object.finprodName = $('#select2-finprodId-container').attr('title');
	        }
	        object.pageModel = pageModel;
	        object.operateType = operateType;
	        object.tradeId = $('#tradeId').val();
	        object.prinAmt = $('#tradeAmtHid').val();
	        
	        if(FileCommon) {
	            object.fileArray = FileCommon.getUploadFileInfo();
	            if(object.fileArray && _.isArray(object.fileArray)){
	                $.each(object.fileArray, function(index,obj){
	                    obj.seqNo = obj.fileNo;
	                });
	            }
	        }
	        if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
	            object.counterType = $('#trdInvestPersonInfo_counterId').select2('data')[0].object;
	            object.counterId = $('#trdInvestPersonInfo_counterId').select2('data')[0].id;
	        }
	        object.inputValue1 = 'F04'; 
	        Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/check', object, function(result1) {
		        Joyin.ajaxJson(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade002/check', object, function(result) {
		            Joyin.confirm(result.data, function() {
		                if(operateType === 'save') {
		                    WorkFlow.doSaveList(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade002/save', object, "TrdPlanNetInvestTrade002_btn_save");
		                }
		                else {
		                    WorkFlow.doSubmitList(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade002/save', object, "TrdPlanNetInvestTrade002_btn_submit");
		                }
		            });
	            });
	        });
    	});
    };
  //查询组合可用现金
    var getPtlCashPositionInfo = function(){
        if ($('#portfolioId').val()) {
        param = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),vdate:$('#tradeDate').val(),calId:$('#calendarId').val(), chlId: $("#chlId").val()}
        Joyin.ajaxJson(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade002/getProAvailableCash',param,
                function(result) {
            $('#proAvailableCash').val(result.cashT).trigger('blur');
            $('#proAvailableCash1').val(result.cashT1).trigger('blur');
            $('#accAvailableCash').val(result.cashT2).trigger('blur');
            $('#channelAvailableCash').val(result.cashTD).trigger('blur');
        });
    }  
    }  
    //查询组合可用持仓
    var getPtlSecPositionInfo = function(){
        if ($('#portfolioId').val()) {
        param = {portfolioId:$('#portfolioId').val(),finprodId:$('#finprodId').val(),cdate:$('#tradeDate').val(), secAcctId: $("#chlId").val()}
        Joyin.ajaxJson(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade002/getPtlSecPosition',param,
                function(result) {
            $('#assetAvailablePosition').val(result).trigger('blur');
        });
    }
    }
  //查询资产管理人
    var selectManeger = function(){
        if($('#finprodId').val()){
            var data = $('#finprodId').select2('data')[0].object;
            Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/selectManeger',{issuer:data.manager},
                    function(result) {
                $("#trdInvestPersonInfo_counterId").textSelect('setValue',result.id);
            });
        }
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
     * 动态设置不可编辑
     */
    var checkAndAssign = function(el, value, editable){
        if(value != null && value != ''){
            $(el).val(value).trigger('change');
            if(!editable){
                $(el).attr("disabled",true);
            }
        }
    };
  //画面初始化
    var init = function(){
        
        var entrustId = $("#entrustId").val();
        
        $('#finprodId').on('change', function () {
            var finprodId = $('#finprodId').val();
            var ps = $('#ps').val();
            var portfolioId =  $('#portfolioId').val();
            var ccy = $('#ccy').val();
            if(finprodId){
                var data = $('#finprodId').select2('data')[0].object;
                $("#finprodName").val($('#finprodId').select2('data')[0].id);
                $("#calendarId").val(data.calendarId).trigger('change');
                $("#ccy").val(data.ccy).trigger('change');
                $("#type6").val(data.type6).trigger('change');
                /*添加资产二级-四级分类 zhangpuyu 2019/7/31 start*/
                $("#type9").val(data.type9).trigger('change');
                $("#type10").val(data.type10).trigger('change');
                $("#type11").val(data.type11).trigger('change');
                /*添加资产二级-四级分类 zhangpuyu 2019/7/31 end*/
                $("#type7").val(data.type7).trigger('change');
                $("#tradeMarket").val(data.tradeMarket).trigger('change');
                if ((ps == ps_T04 || ps == ps_T06) &&　!entrustId) {
                    $('#trdInvestPersonInfo_counterId option').remove();
//                $("#trdInvestPersonInfo_counterId").textSelect('setValue',data.issuer);
                    selectManeger();
                }
                //add  by zhangnian  20190917  start
                if($('#ps').val()==ps_T04){
                    $("#settleSpreed").val(data.purSpreed).change();
                }else if($('#ps').val()==ps_T06){
                    $("#settleSpreed").val(data.redSpreed).change();
                }
                //add  by zhangnian  20190917  end
                if (($('#ps').val() == ps_T04 || $('#ps').val() == ps_mr) && finprodId && portfolioId) {
                    getPtlCashPositionInfo();
                }
            }
            ChannelAssetCommon.setChannelSelectObject($("#chlId"), $('#finprodId').val(), $("#entrustId").val(), function () {

            });
            /*if($("#type7").val()== plansType01 && ps == ps_T06){
                $('#tradeAmt').val('').attr('disabled',true);
                $('#shareAmt').attr('disabled',false);
            }else{*/
            	$('#tradeAmt').attr('disabled',false);
            	$('#shareAmt').attr('disabled',false);
            // }
            $('#netDate').change();
            
            if ((ps == ps_T06 || ps == ps_mc) && finprodId && portfolioId) {
                getPtlSecPositionInfo();
            }
        });

        $("#chlId").change(function () {
            var finprodId = $('#finprodId').val();
            var ps = $('#ps').val();
            var portfolioId =  $('#portfolioId').val();
            var ccy = $('#ccy').val();
            if ((ps == ps_T04 || ps == ps_mr) && finprodId && portfolioId) {
                getPtlCashPositionInfo();
            }
            if ((ps == ps_T06 || ps == ps_mc) && finprodId && portfolioId) {
                getPtlSecPositionInfo();
            }
        })

        /**
         * 交易方向
         */
        $('#ps').on('change',function(){
            var ps = $('#ps').val();
            var finprodId = $('#finprodId').val();
            var portfolioId =  $('#portfolioId').val();
            var ccy = $('#ccy').val();
            //非查看模式下,交易方向不为买入和申购时，清空组合可用金额
            if (pageModel != PAGE_MODEL_REFERENCE && (ps != ps_T04 || ps != ps_mr)){
                $('#proAvailableCash').val('').trigger('change');;
                $('#proAvailableCash1').val('').trigger('change');
            }
            
            if(ps == ps_T04){
                $('#proAvailableCash_div').show();
                $('#proAvailableCash1_div').show();
                // $('#accAvailableCash_div').show();
                $('#div_channelCash').hide();
                $('#assetAvailablePosition_div').hide();
                $('.expectedArrivalDate_div').hide();
                $('#transferInformation').show();
                $('#tradeSha_div').hide();
                $('#shareAmt').val("");
                $('#tradeAmt_div').show();
                $('#tradeAmt').attr('disabled',false);
                $('#date_div').hide();
                $('#fee_div').hide();
                $('#settleSpreed_div').show();
                if(finprodId){
                    $('#trdInvestPersonInfo_counterId option').remove()
//                    $("#trdInvestPersonInfo_counterId").textSelect('setValue',$('#finprodId').select2('data')[0].object.issuer);
                    selectManeger();
                    var data = $('#finprodId').select2('data')[0].object;
                    $("#settleSpreed").val(data.purSpreed);
                }
                if(finprodId && portfolioId && ccy){
                    getPtlCashPositionInfo();
                }
            }else if(ps == ps_T06){
                $('#proAvailableCash_div').hide();
                $('#proAvailableCash1_div').hide();
                // $('#accAvailableCash_div').hide();
                $('#div_channelCash').hide();
                $('#assetAvailablePosition_div').show();
                $('.expectedArrivalDate_div').show();
                $('#transferInformation').hide();
                $('#tradeSha_div').show();
                $("#type7").change();
                $('#date_div').hide();
                $('#fee_div').hide();
                $('#settleSpreed_div').show();
                if(finprodId){
                    $('#trdInvestPersonInfo_counterId option').remove()
//                    $("#trdInvestPersonInfo_counterId").textSelect('setValue',$('#finprodId').select2('data')[0].object.issuer);
                    selectManeger();
                    var data = $('#finprodId').select2('data')[0].object;
                    $("#settleSpreed").val(data.redSpreed);
                }
//                $('#tradeAmt_div').hide();
//                $('#tradeAmt').val("");
                if(finprodId && portfolioId){
                    getPtlSecPositionInfo();
                }
                /*if(finprodId && $("#type7").val()== plansType01 && ps == ps_T06){
                    $('#tradeAmt').val('').attr('disabled',true);
                    $('#shareAmt').attr('disabled',false);
                }*/
                //-----add by 裴家俊 切换到赎回时清空风险试算列表 start ----
                if($("#baseRiskMathCommTable").data('joyintable')!=null) {
                    $("#riskTrialSpan").empty();
                    $("#baseRiskMathCommTable").data('joyintable').reload({data: []});
                    $("#riskMathFlag").val("01");
                }
                //-----add by 裴家俊 切换到赎回时清空风险试算列表 end ----
            }else if(ps == ps_mr){
                $('#date_div').show();
                $('#fee_div').show();
                $('#proAvailableCash_div').show();
                $('#proAvailableCash1_div').show();
                // $('#accAvailableCash_div').show();
                $('#div_channelCash').hide();
                $('#assetAvailablePosition_div').hide();
                $('.expectedArrivalDate_div').hide();
                $('#transferInformation').show();
                $('#tradeSha_div').show();
                $('#carryAmt_div').hide();
                $('#purchaseFee_div').show();
                $('#redemptionFee_div').hide();
                $('#tradeAmt').attr('disabled',false);
                $('#shareAmt').attr('disabled',false);
                $('#channelFee_div').hide();
                $('#hostingFee_div').hide();
                $('#settleSpreed_div').hide();
                if(finprodId && portfolioId){
                    getPtlCashPositionInfo();
                }
                $('#netDate').change();
            }else if(ps == ps_mc){
                $('#date_div').show();
                $('#fee_div').show();
                $('#proAvailableCash_div').hide();
                $('#proAvailableCash1_div').hide();
                // $('#accAvailableCash_div').hide();
                $('#div_channelCash').hide();
                $('#assetAvailablePosition_div').show();
                $('.expectedArrivalDate_div').show();
                $('#transferInformation').show();
                $('#purchaseFee_div').hide();
                $('#tradeSha_div').show();
                $('#redemptionFee_div').show();
                $('#tradeAmt').attr('disabled',false);
                $('#shareAmt').attr('disabled',false);
                $('#channelFee_div').show();
                $('#hostingFee_div').show();
                $('#settleSpreed_div').hide();
                if(finprodId && portfolioId){
                    getPtlSecPositionInfo();
                }
                $('#netDate').change();
            }
            // 交易方向变更 清空交易对手 yjm 2019/07/01 start
            if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
                var 交易对手 =  $('#trdInvestPersonInfo_counterId').select2('data')[0].object;
                if (typeof 交易对手 != 'undefined') {
                    if ((交易对手 != 'N' && (ps == ps_mc || ps == ps_mr)) || (ps && 交易对手 && 交易对手 != 'S' && ps != ps_mc && ps != ps_mr)) {
                        clearTrader();
                    }
                }
            }
            //交易方向变更 清空交易对手 yjm 2019/07/01 end
            
            //清算速度
            $('#settleSpreed').trigger('change');
        });

        var clearTrader = function () {
            $('#trdInvestPersonInfo_counterId').val('').change();
            $('#trdInvestPersonInfo_counterTrader').val('');
            $('#trdInvestPersonInfo_contactInfo').val('');
        }

        // zhangnian add 20190917 交易日期+清算速度 联动计算确认日期 start
        var calcCfmDate = function() {
            var tradedate = $('#tradeDate').val();
            var settleSpeed = $('#settleSpreed').val();
            var calendarId = $('#calendarId').val();
            if (!tradedate || !settleSpeed || !calendarId) {
                return;
            }
                Joyin.ajaxSync(basePath + 'invest/trade/TrdCommonController/getNextWorkDay', {
                    'calendarId': calendarId,
                    'tradeDate': tradedate,
                    'settleSpreed':settleSpeed
                }, function (result) {
                    $("#purCfmDate").val(result).trigger("change");
                    $('#expectedArrivalDate').val(result).trigger("change");
                });
        }
        // zhangnian add 20190917 交易日期+清算速度 联动计算确认日期 end
        /**
         * 基金类型
         */
//        $("#type7").change(function(){
//            var type7 = $('#type7').val();
//            var ps = $('#ps').val();
//            if(type7 == plansType01 && ps == ps_T06){
//                $('#tradeAmt').val('').attr('disabled',true);
//                $('#shareAmt').attr('disabled',false);
//            }else{
//                $('#tradeAmt').val('').attr('disabled',false);
//                $('#shareAmt').val('').attr('disabled',false);
//            }
//        });
        $('#tradeAmt').change(function(){
            var tradeAmt = $('#tradeAmt').val();
            var ps = $('#ps').val();
            if ((ps == ps_T04 || ps == ps_T06) && pageModel !== PAGE_MODEL_REFERENCE) {
                if(tradeAmt){
                    $('#shareAmt').val('').attr('disabled',true).attr('required',false);
                }else{
                    $('#shareAmt').attr('disabled',false).attr('required',true);
                }
                star();
            }
            if ((ps == ps_mr || ps == ps_mc) && pageModel !== PAGE_MODEL_REFERENCE) {
                var feeAmtPay = $('#feeAmtPay').val();
                var unitCprice = $('#unitCprice').val();
                if(unitCprice && $('#tradeAmt').val()){
                    $('#shareAmt').val($('#tradeAmt').val()/unitCprice).trigger('blur');
                }
            }
        });
        
        /**
         * 净值日期
         */
        $('#netDate').change(function(){
            var netDate = $('#netDate').val();
            var finprodId = $('#finprodId').val();
            var tradeAmt = $('#tradeAmt').val();
            if(netDate && finprodId){
                var data;
                if(pageModel==PAGE_MODEL_REFERENCE){
                	var tempTradeId = tradeId;
                	var tempWorkflowId = workflowId;
                	if (null == tempTradeId || 'undefined' == typeof(tempTradeId)) {
                		tempTradeId=""
                	}
                	if (null == tempWorkflowId || 'undefined' == typeof(tempWorkflowId)) {
                		tempWorkflowId=""
                	}
                	data =  {finprodId:finprodId,cdate:netDate,tradeId:tempTradeId,workflowId,tempWorkflowId};
                }
                else{
                	data =  {finprodId:finprodId,cdate:netDate};
                }
                $('#unitCprice').val('').trigger('blur').change();
                Joyin.ajaxSync(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade003/getLastPriceAndLastDate', data , function(result) {
                    if (result === '') {
                        console.log("当前资产查询单位净值为空！");
                        return;
                    }
                    $('#unitCprice').val(result.id).trigger('blur').change();
                    if (netDate !== result.text) {
                        $('#netDate').val(result.text).trigger('blur').change();
                    }
                });
            }
        });
        /**
         * 单位净价
         */
        $('#unitCprice').change(function(){
            if($('#tradeId').val()){
                return;
            }
            var unitCprice = $('#unitCprice').val();
            var tradeAmt = $('#tradeAmt').val();
            var ps = $('#ps').val();
            var shareAmt = $('#shareAmt').val();
//            if(ps == ps_mr || ps == ps_mc){
//                if(unitCprice != null && unitCprice != '' && unitCprice != 0){
//                    $('#shareAmt').val(tradeAmt/unitCprice).trigger('blur');
//                }
//            }else 
            if((ps == ps_mr || ps == ps_mc) && pageModel !== PAGE_MODEL_REFERENCE){
                if(unitCprice != null && unitCprice != '' && shareAmt){
                    $('#tradeAmt').val(shareAmt*unitCprice).trigger('blur').attr('disabled',true);
                }else if(unitCprice != null && unitCprice != '' && unitCprice != 0 && tradeAmt){
                    $('#shareAmt').val(tradeAmt/unitCprice).trigger('blur').attr('disabled',true);
                }
            }
        });
        $('#shareAmt').change(function(){
            var shareAmt = $('#shareAmt').val();
            var ps = $('#ps').val();
            if(ps == ps_T04 || ps == ps_T06){
                if(shareAmt){
                    $('#tradeAmt').val('').attr('disabled',true).attr('required',false);
                }else{
                    $('#tradeAmt').attr('disabled',false).attr('required',true);
                }
                star();
            }
            if(ps == ps_mr || ps == ps_mc){
                var unitCprice = $('#unitCprice').val();
                if(unitCprice && shareAmt){
                    $('#tradeAmt').val(unitCprice*shareAmt).trigger('blur');
                }
            }
        });
        /**
         * 组合代码
         */
        $('#portfolioId').change(function(){
           var portfolioId =  $('#portfolioId').val();
           var ps = $('#ps').val();
           var finprodId = $('#finprodId').val();
           var ccy = $('#ccy').val();
           if((ps == ps_T04 || ps == ps_mr) && finprodId && portfolioId){
               getPtlCashPositionInfo();
           }
           if((ps == ps_T06 || ps == ps_mc) && finprodId && portfolioId){
               getPtlSecPositionInfo();
           }
        });
        
        /**
         * 清算速度change事件
         */
        $('#settleSpreed').change(function(){
            var settleSpeed = $('#settleSpreed').val();
        	var tradeDate = $('#tradeDate').val();
        	if(pageModel==PAGE_MODEL_NEW){
                if (settleSpeed && settleSpeed != '' && tradeDate && tradeDate != ''){
                    /*var num = 0;
                    if (settleSpeed === settleSpreed_T1){
                        num = 1;
                    }else if(settleSpeed === settleSpreed_T2){
                        num = 2;
                    }*/
                   /* var num = settleSpeed;
                    $('#expectedArrivalDate').val(DateUtil.parse(tradeDate).DateAdd('d', num).Format('yyyy-MM-dd')).trigger('change');*/
                }
                if($("#settleSpreed_div").is(":hidden")){
                    var num = 0;
                    $('#expectedArrivalDate').val(DateUtil.parse(tradeDate).DateAdd('d', num).Format('yyyy-MM-dd')).trigger('change');
                }
                //计算确认日期
                calcCfmDate();
            }
        });

        $('#entrustId').change(function(){
            var entrustId = $('#entrustId').val();
            if(entrustId){
                $('#portfolioId').attr('disabled',true);
                $('#ps').attr('disabled',true);
                $('#finprodId').attr('disabled',true);
            }else{
                $('#portfolioId').attr('disabled',false);
                $('#ps').attr('disabled',false);
                $('#finprodId').attr('disabled',false);
            }
        });
        //add by yujiaping 2020-11-23 ZGBS-13新建赎回交易，修改确认日期，预计到账日根据确认日期联动：预计到账日=确认日 start
        $('#purCfmDate').change(function(){
        	var purCfmDate = $('#purCfmDate').val();
        	if(purCfmDate){       		
        		$('#expectedArrivalDate').val(purCfmDate).trigger('change');
        	}
        });
        //add by yujiaping 2020-11-23 ZGBS-13新建赎回交易，修改确认日期，预计到账日根据确认日期联动：预计到账日=确认日 end
        window.componentLoadCallback = function(isDone, pagePath, params){
            if(isDone){
                if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel==PAGE_MODEL_NEW){
                    riskparam.data =null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
                }
                ChannelAssetCommon.setChannelSelect($("#chlId"), $('#finprodId').val(), $("#entrustId").val(), function () {
                    window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                        var data = {};
                        data.counterId = $('#trdInvestPersonInfo_counterId').val();
                        data.tradeAmt = $('#tradeAmt').val();
                        data.profitType = $('#profitType').val();
                        data.ps = $('#ps').val();
                        data.finprodId = $('#finprodId').val();
                        data.ccy = $('#ccy').val();
                        data.chlId = $('#chlId').val();
                        data.tradeDate = $('#tradeDate').val();
                        data.finprodType2 = $('#finprodType2').val();
                        data.profitType = $('#profitType').val();
                        return {
                            'isTotalTrade':YES_OR_NO_N
                            ,'bankLendingType':null
                            ,'portfolioId':$("#portfolioId").val()
                            ,'chlId':$("#chlId").val()
                            ,'deal':data
                        };
                    });
                    if (TodoList) {
                        window.BussTrdTransferOrder['bussTrdTransferOrder'].saveCalcCondition();
                    }
                });
//                if(entrustId != ''){
//                    if(etrEntrust){
//                        window.TrdInvestPersonInfo002['trdInvestPersonInfo'].setEntrust(function () {
//                            return etrEntrust;
//                        });
//                    }
//                }
            }
        }
        
        $('#ps').change();
//        $('#type6').change();
        
        $('#finprodId').change();
        $('#tradeAmt').change();
        $('#shareAmt').change();
        $('#entrustId').change();
    };
    
    //add by lizhi 2020-2-26 划款指令是否自动生成  start
    var generateOrders = function (callback) {
    	// 如果还款指令设置为隐藏，则自动生成划款指令
    	if(GEN_PAY_ORDERS_AUTO =='01'){
    		$('#bussTrdTransferOrder_btn_generate').click();
    	}
    	// 回调方法
	    if (callback && typeof callback === 'function') {
	        callback();
	    }
    }
    //add by lizhi 2020-2-26 划款指令是否自动生成  end
    /**
     * 画面加载完成
     */
    $(function() {
        // 是否显示划款指令
        if(GEN_PAY_ORDERS_AUTO=='01'){
            $('#transferInformation').hide() ;
        }else{
            $('#transferInformation').show() ;
        }
        //初始化画面
        init();
        // 绑定按钮事件
        $('#TrdPlanNetInvestTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdPlanNetInvestTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        $('#TrdPlanNetInvestTrade002_btn_view').click(function(){
            var finprodId = $('#finprodId').val();
            if(finprodId){
                Joyin.dialog(JoyinUtil.getLabel("FinPlanNetAsset002"),
                        basePath + "invest/asset/plan/planNet/FinPlanNetAsset002/init",
                        "95%", "90%",{pageModel:PAGE_MODEL_REFERENCE,
                        finprodId: finprodId});
            }
        });
      //风险试算
        $('#TrdPlanNetInvestTrade002_btn_risk').click(function(){
            //window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 start----------
            //---赎回申请不需要风险试算----
            if($("#ps").val()==ps_T06){
                return;
            }
            var object = $.serializeToObject('#submitForm', true);
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/1/RSK002/F04', object,getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 end----------
            window.BaseRiskMathComm.initBaseRiskMathCommPageCus(getriskdata());

        })
      //是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
        	$('#selector5').show();
        	}else{
        	$('#selector5').hide();
        	}
    });
}());