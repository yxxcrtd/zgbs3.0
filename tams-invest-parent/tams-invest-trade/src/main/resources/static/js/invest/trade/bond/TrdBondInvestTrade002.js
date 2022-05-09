//@ sourceURL=TrdBondInvestTrade002.js
var table;
var calculateField;
(function () {
    var unitNum;
	var initFlag = true;
    var entrustNum; // 委托时的券面总额
    var entrustPrice; // 委托时的净价
    var entrustYield; // 委托时的到期收益率
    var entrustExerYield; // 委托时的行权收益率
    var commissionAmt=0;// 佣金
    var fTradeIdFromCstp = fTradeId;
    var settleAmt=0; //结算费
    var exchangeAmt=0; //经手费
    var fTradeIdInitFlag = 0;//成交编号初始化标识 第一次调用时，不触发事件
   //初始计算要素内容
    var saveCalcParam;
    var isautobond = false;
    // add by wangguoxian 费用设置 20190807 start
    var isPayWithTrade = $("#isPayWithTrade").val()?$("#isPayWithTrade").val():YES_OR_NO_Y;//佣金是否随交易支付，默认为是
    $("#isPayWithTrade").val(isPayWithTrade);
    // add by wangguoxian 费用设置 20190807 end
    // add by wangpeng 费用设置 20190817 start
    var stampDutyIsPayWithTrade = $("#stampDutyIsPayWithTrade").val()?$("#stampDutyIsPayWithTrade").val():YES_OR_NO_Y;//印花是否随交易支付，默认为是
    var settleIsPayWithTrade = $("#settleIsPayWithTrade").val()?$("#settleIsPayWithTrade").val():YES_OR_NO_Y;//结算费是否随交易支付，默认为是
    var exchangeIsPayWithTrade = $("#exchangeIsPayWithTrade").val()?$("#exchangeIsPayWithTrade").val():YES_OR_NO_Y;//经手费是否随交易支付，默认为是
    $("#stampDutyIsPayWithTrade").val(stampDutyIsPayWithTrade);
    $("#settleIsPayWithTrade").val(settleIsPayWithTrade);
    $("#exchangeIsPayWithTrade").val(exchangeIsPayWithTrade);
    // add by wangpeng 费用设置 20190817 end
    var getriskdata = function () {
        var obj = {timePoint:MY_SUP_TIME_TYPE_02,valDate:$('#settleDate').val(),portfolioCode:$('#portfolioId').val(),tradeAmount:$('#fpriceAmt').val(),assetType:FINPROD_TYPE_F01,isEnabled:'01',assetCode:$('#finprodId').val(),tradeDirection:$('#ps').val()}
        if($('#windtypeone').val() && $('#windtypeone').val() != ''){
            obj.assetSecType = $('#windtypeone').val();
        }
        // add by zhangrui1 交易对手影响风险试算 start
        obj.counterId = $('#trdInvestPersonInfo_counterId').val();
        // add by zhangrui1 交易对手影响风险试算 end
        var object = {valDate:['settleDate','TrdPlanRateInvestTrade002.settleDate'],portfolioCode:['portfolioId','TrdBondInvestTrade002.portfolioId'],tradeAmount:['fpriceAmt','TrdBondInvestTrade002.fpriceAmt'],assetType:['windtypeone','TrdBondInvestTrade002.windtypeone'],tradeDirection:['ps','TrdLendInvestTrade002.ps'],data:obj}
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'TrdInvestPersonInfo.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        return object;
    }
    function saveOrSubmit(operateType) {
    	// 划款指令隐藏时，自动生成划款指令信息
    	generateOrders(function () {
	        if (integrateTradeAmtLeft && Number.parseFloat($("#integrateTradeAmt").val()) > Number.parseFloat(integrateTradeAmtLeft)) {
	            Joyin.error("融入券/买入购回券面总额不能大于委托剩余额度")
	            return;
	        }
	        if (ownTradeAmtLeft && Number.parseFloat($("#ownTradeAmt").val()) > Number.parseFloat(ownTradeAmtLeft)) {
	            Joyin.error("本方券券面总额不能大于委托剩余额度")
	            return;
	        }
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
	        // 检查计算元素是否发生变化
	        if (checkCalcParam()) {
	            //---update by jjpei 20190731---信息显示有误
	            Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
	                ["计算要素", "点击计算按钮"]));
	            //---update by jjpei 20190731---信息显示有误
	            return;
	        }
	
	        // 检查生成划款指令参数是否发生变化
	        if (window.BussTrdTransferOrder['bussTrdTransferOrder'].isCalcConditionChanged()) {
	            return;
	        }

			var tradeAmt_=$('#tradeAmt').val();
	
//	        if($('#fTradeId').val()&&$('#tradeAmt').val()&&(pageModel==PAGE_MODEL_NEW||pageModel==PAGE_MODEL_EDIT)){//新建
//	            var isTrue = false;
//	            var params = {};
//	            params.fTradeId = $('#fTradeId').val();
//
//	            if (tradeAmt_) {
//		             params.tradeAmt=tradeAmt_*10000;//万元转成元
//	            }
//
//	            params.workflowId = $('#workflowId').val();
//	            Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/isAllTradeAmtByFTradeId'), params, function (result) {
//	                if(result&&!result.data){
//	                    isTrue = true;
//	                    Joyin.error("成交编号获得的劵面总额不得大于已存在的劵面总额！");
//	                }
//	            },false);
//	            if(isTrue){
//	                return;
//	            }
//	        }
	        var object = $.serializeToObject('#submitForm', true);
	        if ($('#select2-finprodId-container').attr('title')){
	            object.finprodName = $('#select2-finprodId-container').attr('title');
	        }
			object.tradeAmt = JoyinMath.doMul( !$("#tradeAmt").val()?0:$("#tradeAmt").val(),unitNum,2,JoyinMath.ROUND_HALF_UP);
            object.ownTradeAmt = JoyinMath.doMul( !$("#ownTradeAmt").val()?0:$("#ownTradeAmt").val(),unitNum,2,JoyinMath.ROUND_HALF_UP);
            object.integrateTradeAmt = JoyinMath.doMul( !$("#integrateTradeAmt").val()?0:$("#integrateTradeAmt").val(),unitNum,2,JoyinMath.ROUND_HALF_UP);
            object.finprodId = $("#finprodId").val();
	        object.pageModel = pageModel;
	        object.operateType = operateType;
	        if(FileCommon) {
	            object.uploadFileList = FileCommon.getUploadFileInfo();
	        }
          object.tradeAmt = JoyinMath.doMul(!tradeAmt_?0:tradeAmt_,unitNum,2,JoyinMath.ROUND_HALF_UP);
	        object.settleAmt=$("#settleAmt").val();
	        object.exchangeAmt=$("#exchangeAmt").val();
	        object.commissionAmt=$("#commissionAmt").val();
            if($("#refundRate").val()){
                object.refundRate=$("#refundRate").val();
            }
            if($("#refundAmount").val()){
                object.refundAmount=$("#refundAmount").val();
            }
	        object.preTradeId = preTradeId;
//	        object.fTradeId = fTradeId;
	        object.entrustNum = entrustNum; // 指令时的券面总额
	        object.entrustPrice = entrustPrice; // 指令时的净价
	        object.entrustYield = entrustYield; // 指令时的到期收益率
	        object.entrustExerYield = entrustExerYield; // 指令时的行权收益率
            // 是否随交易支付赋值 fuyongsheng 20210906 add start
            object.isPayWithTrade = $("#isPayWithTrade").val()?$("#isPayWithTrade").val():YES_OR_NO_Y;//佣金是否随交易支付，默认为是
            // 是否随交易支付赋值 fuyongsheng 20210906 add end
            object.assetPosition = $("#bussTrdAvailableCashQuery_assetPosition").val(); // 资产可用持仓
	        object.bondFirstSign = $("#bondFirstSign").val(); //中标结果页面进入的标识 add by gejiaao 2019/8/9
	        object.deviation=  parseFloat($("#deviationDegreeLabel").text())/100;//偏离度获取界面值并将其转换为数值型by赵东东 2019.9.24
	        var data = $("#chlId").find("option:selected").data("data");
	        if (data && data.object) {
	            object.payType = data.object.payType;
	            object.collectType = data.object.collectType;
	        }
	        if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
	            object.counterType = $('#trdInvestPersonInfo_counterId').select2('data')[0].object;;
	        }
	        if(object.trdInvestPersonInfo && $("#fTradeId").val()){
	        	object.trdInvestPersonInfo.counterTrader = $('select#trdInvestPersonInfo_counterTrader').val();
	        }else{
	        	object.trdInvestPersonInfo.counterTrader = $('input#trdInvestPersonInfo_counterTrader').val();
	        }
            if (typeof shortSellingSupported != 'undefined' && shortSellingSupported == '02') {
                object.ownTradeAmt = JoyinMath.doMul(!tradeAmt_?0:tradeAmt_,unitNum,2,JoyinMath.ROUND_HALF_UP);
                object.ownFpriceAmt = object.fpriceAmt;
                object.ownIntAmt = object.intAmt;
            }
            object.calculateField = calculateField;
            if(parentPageId){
                object.parentPageId = parentPageId;
            }
	        Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/check', object, function(result1) {
                if(isautobond == true){
                    Joyin.alert("资产第一次接入系统，请确认并完善【债券标的】页面资产要素完整！",1,function () {
                        //如果是多组合指令，循环入库
                        if($("#zhilingtype").val()=="02"){

                            if(object.bussTrdTransferOrder.length){
                                var flag = true;
                                var msg = "";
                                for(var bussTrdTransferOrder in object.bussTrdTransferOrder){
                                    for(var bussTrdTransfenpeiOrder in object.bussTrdTransfenpeiDto){
                                        if((object.bussTrdTransferOrder[bussTrdTransferOrder].portfolioId == object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].busiId)&&
                                            (object.bussTrdTransferOrder[bussTrdTransferOrder].settleAmt== object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].fpriceAmt)){

                                            object.portfolioId = object.bussTrdTransferOrder[bussTrdTransferOrder].portfolioId;
                                            object.tradeAmt = JoyinMath.doMul(object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].tradeAmt?object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].tradeAmt:0,unitNum,2,JoyinMath.ROUND_HALF_UP);
                                            object.cpriceAmt = object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].cpriceAmt;
                                            object.fpriceAmt = object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].fpriceAmt;
                                            ajaxJsonSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/check', object, function(result) {
                                                if(result) {
                                                    if(msg.indexOf(result.data) == -1){
                                                        msg += result.data + "</br>";
                                                    }
                                                    if(result.statuCode !='200'){
                                                        flag = false;
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                if(flag){
                                    Joyin.confirm(msg, function() {
                                        object.riskCreditListCommonDto = result1.data;
                                        //授信占用额度
//	                        	Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {});
                                        if(operateType === 'save') {
                                            WorkFlow.doSaveList(basePath + 'invest/trade/bond/TrdBondInvestTrade002/save', object, "TrdBondInvestTrade002_btn_save");
                                        }
                                        else {
                                            WorkFlow.doSubmitList(basePath + 'invest/trade/bond/TrdBondInvestTrade002/save', object, "TrdBondInvestTrade002_btn_submit");
                                        }
                                    });
                                }
                            }else{
                                //验证划款指令必填
                                object.portfolioId=object.busiId;
                                Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade002/check', object, function(result) {

                                },false);
                            }
                        }else{
                            Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade002/check', object, function(result) {
                                //授信占用额度
//	                	Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {});
                                object.riskCreditListCommonDto = result1.data;
                                // if(result.data.indexOf("交易净价不满足使权不重大的购入价条件")!=-1){
                                //     $("#businessModel").val("Y2").trigger('change');
                                //     object.businessModel=="Y2";
                                //     object.invAim=="FVTPL";
                                // }


                                Joyin.confirm(result.data, function() {
                                    if(operateType === 'save') {
                                        WorkFlow.doSaveList(basePath + 'invest/trade/bond/TrdBondInvestTrade002/save', object, "TrdBondInvestTrade002_btn_save");
                                    }
                                    else {
                                        WorkFlow.doSubmitList(basePath + 'invest/trade/bond/TrdBondInvestTrade002/save', object, "TrdBondInvestTrade002_btn_submit");
                                    }
                                });
                            },false,function (result) {
                                var disabledFlag = $('#invAim').attr('disabled');
                                if(disabledFlag) return;
                                var msg = result.errorList;
                                var message ="";
                                for(var i=0;i<msg.length;i++){
                                    message = msg[i];
                                    if(message.field!=null && message.field=='invAim'){
                                        if(message.message!=null && message.message==JoyinUtil.getMessage("ETR_ERROR_0059")) {
                                            $('#invAim').textSelect('setValue', "FVTPL");
                                            break;
                                        }
                                    }
                                }

                            });
                        }
                    });
                }else{
                    //如果是多组合指令，循环入库
                    if($("#zhilingtype").val()=="02"){

                        if(object.bussTrdTransferOrder.length){
                            var flag = true;
                            var msg = "";
                            for(var bussTrdTransferOrder in object.bussTrdTransferOrder){
                                for(var bussTrdTransfenpeiOrder in object.bussTrdTransfenpeiDto){
                                    if((object.bussTrdTransferOrder[bussTrdTransferOrder].portfolioId == object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].busiId)&&
                                        (object.bussTrdTransferOrder[bussTrdTransferOrder].settleAmt== object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].fpriceAmt)){

                                        object.portfolioId = object.bussTrdTransferOrder[bussTrdTransferOrder].portfolioId;
                                        object.tradeAmt = object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].tradeAmt;
                                        object.cpriceAmt = object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].cpriceAmt;
                                        object.fpriceAmt = object.bussTrdTransfenpeiDto[bussTrdTransfenpeiOrder].fpriceAmt;
                                        ajaxJsonSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/check', object, function(result) {
                                            if(result) {
                                                if(msg.indexOf(result.data) == -1){
                                                    msg += result.data + "</br>";
                                                }
                                                if(result.statuCode !='200'){
                                                    flag = false;
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                            if(flag){
                                Joyin.confirm(msg, function() {
                                    object.riskCreditListCommonDto = result1.data;
                                    //授信占用额度
//	                        	Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {});
                                    if(operateType === 'save') {
                                        WorkFlow.doSaveList(basePath + 'invest/trade/bond/TrdBondInvestTrade002/save', object, "TrdBondInvestTrade002_btn_save");
                                    }
                                    else {
                                        WorkFlow.doSubmitList(basePath + 'invest/trade/bond/TrdBondInvestTrade002/save', object, "TrdBondInvestTrade002_btn_submit");
                                    }
                                });
                            }
                        }else{
                            //验证划款指令必填
                            object.portfolioId=object.busiId;
                            Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade002/check', object, function(result) {

                            },false);
                        }
                    }else{
                        Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade002/check', object, function(result) {
                            //授信占用额度
//	                	Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {});
                            object.riskCreditListCommonDto = result1.data;
                            // if(result.data.indexOf("交易净价不满足使权不重大的购入价条件")!=-1){
                            //     $("#businessModel").val("Y2").trigger('change');
                            //     object.businessModel=="Y2";
                            //     object.invAim=="FVTPL";
                            // }
                            Joyin.confirm(result.data, function() {
                                if(operateType === 'save') {
                                    WorkFlow.doSaveList(basePath + 'invest/trade/bond/TrdBondInvestTrade002/save', object, "TrdBondInvestTrade002_btn_save");
                                }
                                else {
                                    WorkFlow.doSubmitList(basePath + 'invest/trade/bond/TrdBondInvestTrade002/save', object, "TrdBondInvestTrade002_btn_submit");
                                }
                            });
                        },false,function (result) {
                            var disabledFlag = $('#invAim').attr('disabled');
                            if(disabledFlag) return;
                            var msg = result.errorList;
                            var message ="";
                            for(var i=0;i<msg.length;i++){
                                message = msg[i];
                                if(message.field!=null && message.field=='invAim'){
                                    if(message.message!=null && message.message==JoyinUtil.getMessage("ETR_ERROR_0059")) {
                                        $('#invAim').textSelect('setValue', "FVTPL");
                                        break;
                                    }
                                }
                            }

                        });
                    }
                }
	        });
    	});
	 };

    function checkCalcParam() {
        if (typeof saveCalcParam != 'undefined') {
            var currentCalcParam = getCalcParam();
            for ( var key in currentCalcParam) {
                //---update by jjpei 20190731---排除riskMathFlag----
                if (key == 'selectValue' || key == 'riskMathFlag' || key == 'remark' || key == 'isPayWithTrade'
                    || key == 'exchangeIsPayWithTrade' || key == 'settleIsPayWithTrade' || key == 'stampDutyIsPayWithTrade') {
                    continue;
                }
                //---update by jjpei 20190731---排除riskMathFlag----
                var currentDealObj = currentCalcParam[key];
                var backupDealObj = saveCalcParam[key];

                if (typeof currentDealObj != "object"
                    && currentDealObj != backupDealObj) {
                    return true;
                }
            }
        }
    }

    var checkAndAssign = function(el, value, editable){
        if(value != null && value != ''){
            $(el).val(value).trigger('change').trigger('blur');
            if(!editable){
                $(el).attr("disabled",true);
            }
        }
    };
    var checkNotEmpty = function () {
        for(var i = 0; i < arguments.length; i++){
            if(arguments[i] == null || arguments[i] == ''){
                return false;
            }
        }
        return true;
    }
    var etrEntrust;
    var etrEntrustAdd;
    /**
     * 计算交割日
     */
    var calcSettleDate = function() {
        var settleSpeed = $("#settleSpeed").val();
        if(settleSpeed){
            if (settleSpeed == 0) {
                $("#settleDate").val($("#tradeDate").val());
            }
            else{
                if ($('#finprodId').val()) {
                    // modify by xingxiaokang 2020-03-16 start T+1推算的交割日错误
                    //var tradeDate= DateUtil.parse($("#tradeDate").val()).DateAdd('d', settleSpeed).Format('yyyy-MM-dd')
                	var tradeDate=$("#tradeDate").val();
                	// modify by xingxiaokang 2020-03-16 end T+1推算的交割日错误                    
					
					Joyin.ajaxSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/getNextWorkDay', {
                        'calendarId': $("#calendarId").val(),
                        'tradeDate': tradeDate,
                        'settleSpeed': settleSpeed
                    }, function (date) {
                        $("#settleDate").val(date);
                    });
            }
            }
        }
        /*if (settleSpeed == ACCOUNT_SPEED_T0) {
            $("#settleDate").val($("#tradeDate").val());
        }
        else if(settleSpeed == ACCOUNT_SPEED_T1) {
        	if ($('#finprodId').val()) {
	            Joyin.ajaxSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/getNextWorkDay', {
	                'calendarId': $("#calendarId").val(),
	                'tradeDate': $("#tradeDate").val()
	            }, function (date) {
	                $("#settleDate").val(date);
	            });
        	}
        }else if(settleSpeed == ACCOUNT_SPEED_T2){
        	if ($('#finprodId').val()) {
        		var tradeDate= DateUtil.parse($("#tradeDate").val()).DateAdd('d', 1).Format('yyyy-MM-dd')
	            Joyin.ajaxSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/getNextWorkDay', {
	                'calendarId': $("#calendarId").val(),
	                'tradeDate': tradeDate
	            }, function (date) {
	                $("#settleDate").val(date);
	            });
        	}
        }*/
        else {
            $("#settleDate").val("");
        }
    };
    
    $.fn.updateChosenArr = function updateChosenArr(obj){
		var $this = $(this),arr = obj.dataResult || JSON.parse($this.attr('data-result') || '[]'),item = obj.item;
		$this.empty();
		$this.append($('<option value>'));
		for(var i in arr){
			if (!arr.hasOwnProperty(i))continue;
			if (obj.item && obj.item.length > 0){
				if ($.inArray(arr[i].id,item) >= 0)
					$('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this);
			} else
				$('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this)
		}
		if (obj.chosen)
			$this.val(obj.chosen);
		if (obj.disabled === true || obj.disabled === false)
			$this.attr('disabled', obj.disabled);
		$this.trigger("chosen:updated").trigger('change');
		if (obj.callback)
			obj.callback($this);
		return $this;
	}

    /**
     * 交易平台联动，增加输入框事件
     */
    $('#tradeMarket,#tradeMarketMode,#ps').on("change", function (e, isInit) {
        var tradeMarket = $('#tradeMarket').val();
        var tradeMarketMode = $('#tradeMarketMode').val();
        var $tradePlat = $('#tradePlat');
        var tradePlat = $('#tradePlat').val();
        var ps = $('#ps').val();
        if (pageModel != PAGE_MODEL_REFERENCE) {
            var key = tradeMarket + tradeMarketMode;
            switch (key) {
            	//如果是交易场所+交易市场 == 银行间+一级市场，就可以选择01和99
            	case TRADE_PLACE_01 + SHARES_TRADE_MARKET_01:
            	    $("#settleAmtDiv").hide().trigger("change");
            	    $("#exchangeAmtDiv").hide().trigger("change");
            	    if(initFlag){
            	    	$("#settleAmt").val('').trigger("change");
                	    $("#exchangeAmt").val('').trigger("change");
                	    $("#commissionAmt").val('').trigger("change");
                	    $("#tradeAmtAndFee").val('').trigger("change");
            	    }
            	    else{
            	    	$("#settleAmt").trigger("change");
                	    $("#exchangeAmt").trigger("change");
                	    $("#commissionAmt").trigger("change");
                	    $("#tradeAmtAndFee").trigger("change");
            	    }
            	    
            	    // $("#fpriceAmt").val('').trigger("change");

                    //$("#exchangeFeesAmtDiv").hide();
                    $tradePlat.updateChosenArr({
                        item: [SHARES_TRADE_PLATFORM_01,SHARES_TRADE_PLATFORM_99],
                        chosen: SHARES_TRADE_PLATFORM_01,
                        disabled: false
                    });
                    if($("#chlId").val()){ 
                        $("#commissionAmtDiv").hide();
                        }
                    break;
                case TRADE_PLACE_01 + SHARES_TRADE_MARKET_02:
                	//如果是交易场所+交易市场 == 银行间+二级市场，只能选择01且置灰
                	$("#settleAmtDiv").hide();
                	$("#exchangeAmtDiv").hide();
                	if(initFlag){
                    	$("#settleAmt").trigger("change");
                        $("#exchangeAmt").trigger("change");
                        $("#commissionAmt").trigger("change");
                        $("#tradeAmtAndFee").trigger("change");
                	}
                	else{
                       //	$("#settleAmt").val('').trigger("change");
                        //$("#exchangeAmt").val('').trigger("change");
                        //$("#commissionAmt").val('').trigger("change");
                        $("#tradeAmtAndFee").val('').trigger("change");
                	}

                    // $("#fpriceAmt").val('').trigger("change");
                    //$("#exchangeFeesAmtDiv").hide();
                    $tradePlat.updateChosenArr({
                        item: [SHARES_TRADE_PLATFORM_01],
                        chosen: SHARES_TRADE_PLATFORM_01,
                        disabled: true
                    });
                    if($("#chlId").val()){ 
                        $("#commissionAmtDiv").hide();
                        }
                    break;
                case TRADE_PLACE_02+SHARES_TRADE_MARKET_01:
                    //如果是交易场所+交易市场 == 上海证券交易所+一级市场;
                    $("#settleAmtDiv").hide();
                    $("#exchangeAmtDiv").hide();
                    if(!$("#chlId").val()){ 
                    $("#commissionAmtDiv").show();
                    }
                    if(initFlag){
                        $("#settleAmt").trigger("change");
                        $("#exchangeAmt").trigger("change");
                        $("#commissionAmt").trigger("change");
                        $("#tradeAmtAndFee").trigger("change");
                    }
                    else{
                       // $("#settleAmt").val('').trigger("change");
                        //$("#exchangeAmt").val('').trigger("change");
                       // $("#commissionAmt").val('').trigger("change");
                        $("#tradeAmtAndFee").val('').trigger("change");
                    }

                    // $("#fpriceAmt").val('').trigger("change");
                    //$("#exchangeFeesAmtDiv").hide();
                    $tradePlat.updateChosenArr({
                        disabled: true
                    });
                    if($("#chlId").val()){ 
                        $("#commissionAmtDiv").hide();
                        }
                    break;
                case TRADE_PLACE_02+SHARES_TRADE_MARKET_02:
                    //如果是交易场所+交易市场 == 上海证券交易所+二级市场; 显示经手费和结算费
                    //文本框事件              		
                	  /*  $("#settleAmtDiv").show();
                    $("#exchangeAmtDiv").show();*/
                    // TISHKS-1075:回售也展示佣金、结算费、经手费字段 modify by fuyongsheng 20220505 start
                    // if(ps =="T03"){
                	// 	$("#settleAmtDiv").hide();
                	// 	$("#exchangeAmtDiv").hide();
                	// 	$("#commissionAmtDiv").hide();
                	// }else if(ps !="T03"){
                		$("#settleAmtDiv").show();
                		$("#exchangeAmtDiv").show();
                		if(!$("#chlId").val()){ 
                		$("#commissionAmtDiv").show();
                		}
                	// }
                    // TISHKS-1075:回售也展示佣金、结算费、经手费字段 modify by fuyongsheng 20220505 end
                    if(initFlag){
                        $("#settleAmt").trigger("change");
                        $("#exchangeAmt").trigger("change");
                        $("#commissionAmt").trigger("change");
                        $("#tradeAmtAndFee").trigger("change");
                    }else{
                       // $("#settleAmt").val('').trigger("change");
                       // $("#exchangeAmt").val('').trigger("change");
                       // $("#commissionAmt").val('').trigger("change");
                        $("#tradeAmtAndFee").val('').trigger("change");	
                    }
                    // $("#fpriceAmt").val('').trigger("change");
                    //$("#exchangeFeesAmtDiv").show();
                    $tradePlat.updateChosenArr({
                        item: ['02','03','04'],
                        chosen: tradePlat,
                        disabled: false
                    });
                    if($("#chlId").val()){ 
                        $("#commissionAmtDiv").hide();
                        }
                    break;
                case TRADE_PLACE_03+SHARES_TRADE_MARKET_01:
                    //如果是交易场所+交易市场 == 深圳证券交易所+一级市场;
                    $("#settleAmtDiv").hide();
                    $("#exchangeAmtDiv").hide();
                    if(!$("#chlId").val()){ 
                    $("#commissionAmtDiv").show();
                    }
                    if(initFlag){
                        $("#settleAmt").trigger("change");
                        $("#exchangeAmt").trigger("change");
                        $("#commissionAmt").trigger("change");
                        $("#tradeAmtAndFee").trigger("change");
                    }
                    else{
                        //$("#settleAmt").val('').trigger("change");
                       // $("#exchangeAmt").val('').trigger("change");
                       // $("#commissionAmt").val('').trigger("change");
                        $("#tradeAmtAndFee").val('').trigger("change");	
                    }
                    // $("#fpriceAmt").val('').trigger("change");
                    //$("#exchangeFeesAmtDiv").hide();
                    $tradePlat.updateChosenArr({
                        disabled: true
                    });
                    if($("#chlId").val()){ 
                        $("#commissionAmtDiv").hide();
                        }
                    break;
                case TRADE_PLACE_03+SHARES_TRADE_MARKET_02:
                    //如果是交易场所+交易市场 == 深圳证券交易所+二级市场;
                  /*  $("#settleAmtDiv").show();
                    $("#exchangeAmtDiv").show();*/
                    // TISHKS-1075:回售也展示佣金、结算费、经手费字段 modify by fuyongsheng 20220505 start
                    // if(ps =="T03"){
                	// 	$("#settleAmtDiv").hide();
                	// 	$("#exchangeAmtDiv").hide();
                	// 	$("#commissionAmtDiv").hide();
                	// }else if(ps !="T03"){
                		$("#settleAmtDiv").show();
                		$("#exchangeAmtDiv").show();
                		if(!$("#chlId").val()){ 
                		$("#commissionAmtDiv").show();
                		}
                	// }
                    // TISHKS-1075:回售也展示佣金、结算费、经手费字段 modify by fuyongsheng 20220505 end
                    if(initFlag){
                        $("#settleAmt").trigger("change");
                        $("#exchangeAmt").trigger("change");
                        $("#commissionAmt").trigger("change");
                        $("#tradeAmtAndFee").trigger("change");	
                    }
                    else{
                        //$("#settleAmt").val('').trigger("change");
                      //  $("#exchangeAmt").val('').trigger("change");
                      //  $("#commissionAmt").val('').trigger("change");
                        $("#tradeAmtAndFee").val('').trigger("change");
                    }
                    // $("#fpriceAmt").val('').trigger("change");
                    //$("#exchangeFeesAmtDiv").show();
                    $tradePlat.updateChosenArr({
                        item: ['02','05'],
                        chosen: tradePlat,
                        disabled: false
                    });
                    if($("#chlId").val()){ 
                        $("#commissionAmtDiv").hide();
                        }
                    break;               
                default:
                    $("#settleAmtDiv").hide();
                    $("#exchangeAmtDiv").hide();                   
                    if(initFlag){
                        $("#settleAmt").trigger("change");
                        $("#exchangeAmt").trigger("change");
                        $("#commissionAmt").trigger("change");
                        $("#tradeAmtAndFee").trigger("change");
                    }
                    else{
                       // $("#settleAmt").val('').trigger("change");
                       // $("#exchangeAmt").val('').trigger("change");
                       // $("#commissionAmt").val('').trigger("change");
                        $("#tradeAmtAndFee").val('').trigger("change");	
                    }
                    // $("#fpriceAmt").val('').trigger("change");
                    //$("#exchangeFeesAmtDiv").hide();
                    if (isInit || pageModel == PAGE_MODEL_REFERENCE || pageModel ==PAGE_MODEL_EDIT) {
                        break;
                    }
                	if($("#entrustId").val() != ''){
                            $('#tradePlat').prop("disabled",true);
                	}else{
                		$tradePlat.updateChosenArr({
                			disabled: false
                		});
                	}
                    if($('#tradeMarket').val() === '05'){
                        $('#tradePlat').prop("disabled",true);
                        checkAndAssign("#tradePlat", SHARES_TRADE_PLATFORM_99, false);
                    }
                    break;
            }
        }
    });

    /**
     * 画面联动处理
     * 1.1交易场所
     */
    $('#tradeMarket').on("change", function () {
    	if($('#ps').val()=="T01" && ($('#tradeMarket').val() == "03" || $('#tradeMarket').val() == "04")){
        	$("#tradeMarketCash_div").show();
        }else{
        	$("#tradeMarketCash_div").hide();
        }
        //update by zhangnian 20200213  start 交易场所和币种联合联动费用
        controlTradeFee();
        //update by zhangnian 20200213  end 交易场所和币种联合联动费用
    });
    
    //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 begin
    var calcInvAim = function() {
        var portfolioId = $("#portfolioId").val();
        if ($('#portfolioId').val()) {
            Joyin.ajax(basePath + 'invest/trade/bond/TrdBondInvestTrade002/getProfitType', {
                'portfolioId': portfolioId     
            }, function (profitType) {
                if('03' == profitType){                   
                    $("#invAim").val('AC').trigger('change');
                }
                else{
                    $("#invAim").val("FVOCI").trigger('change');
                }
               
            });
        }
    }
  //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 end
    
    /**
     * 画面联动处理
     * 1.2交易市场
     */
    $("#tradeMarketMode").on("change", function () {
        var tradeMarketMode = $("#tradeMarketMode").val();
        if (tradeMarketMode == SHARES_TRADE_MARKET_01) {
            checkAndAssign("#ps", PS_T01);
            checkAndAssign("#unitInt", '0');
            checkAndAssign("#intAmt", '0');
            $("#refundRate_div").show();
            $("#refundAmount_div").show();
            /*var tradeMarket = $('#tradeMarket').val();
            if (tradeMarket == TRADE_PLACE_02 || tradeMarket == TRADE_PLACE_03) {
                $("#commissionAmt").prop("disabled", true).val(0).trigger('blur').trigger('change');
            }
            else {
                $("#commissionAmt").prop("disabled", false).val(commissionAmt).trigger('blur').trigger('change');
            }*/
        } else {
            // $("#commissionAmt").prop("disabled", false).val(commissionAmt).trigger('blur').trigger('change');
            $("#refundRate").val('');
            $("#refundAmount").val('');
            $("#refundRate_div").hide();
            $("#refundAmount_div").hide();            //编辑模式或者新建模式下交易市场为二级市场时影响资产代码，对应资产不能为发行标的
            if(pageModel == PAGE_MODEL_NEW || pageModel == PAGE_MODEL_EDIT){
                if ($('#finprodId').val != null && $('#finprodId').val != ''){
                    var data = $('#finprodId').select2('data')[0].object;
                    if (data && data.isIssueAsset == 'Y' && $("#tradeMarketMode").val() == '02'){
                        $('#finprodId').val('').trigger("change");
                    }
                }
            }
            if ($("#entrustId").val()) {
                $("#ps").attr("disabled", true);
            }else if ($("#fTradeId").val()){
                $("#ps").attr("disabled", true);
            } else {
            	if($("#zhilingtype").val() =="02"){
                	window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].hideCash(true);
                }
                $("#ps").attr("disabled", false);
            }
//            $("#unitInt").attr("disabled", false);
            $("#intAmt").attr("disabled", false);
        }
    });
    /**
     * 画面联动处理
     * 1.3交易方向
     */
    $("#ps").on("change", function () {
    	
    	if($('#ps').val()=="T01" && ($('#tradeMarket').val() == "03" || $('#tradeMarket').val() == "04")){
        	$("#tradeMarketCash_div").show();
        }else{
        	$("#tradeMarketCash_div").hide();
        }
        var ps = $("#ps").val();
        //交易方向发生变化，重新计算所有数据
        changeTradeAmtAndFee();

        // 一体化对应etliu start
//        if (ps == PS_T02 || ps == PS_T03) {
        $("#assetIntegrateSaleAvailablePosition_div").hide();
        $("#assetIntegrateAvailablePosition_div").hide();
        $("#assetOwnAvailablePosition_div").hide();
        $("#integrateTradeAmt").prop("disabled", false);
        if (ps == PS_T02 || ps == PS_T03 || ps == PS_T39) {
        // 一体化对应etliu end
            if (pageModel == '0') {
                // $("#yield").val("").trigger("change").attr("disabled", true);
                $("#exerYield").val("").trigger("change").attr("disabled", true);
            }
            // $("#calculateField_yield").attr("disabled", true);
            $("#calculateField_exerYield").attr("disabled", true);
            if($("#zhilingtype").val() =="02"){
            	window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].hidePosition(true);
            }else{
            	window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showPosition(true);
            }
            if (shortSellingSupported == '01') {
                $("#assetIntegrateSaleAvailablePosition_div").hide();
                // 该交易方向下：场内交易不展示融入券、展示本方券应计利息总额
                if(parentPageId && parentPageId == "TrdBondInvestTradeEx001"){
                    $("#assetIntegrateAvailablePosition_div").hide();
                    $("#ownIntAmt_div").show();
                }else{
                    $("#assetIntegrateAvailablePosition_div").show();
                }
                $("#assetOwnAvailablePosition_div").show();
                $(".ownNum_label").text('融入券券面总额');
                $(".ownNum_label1").text('融入券应计利息总额');
                $(".ownNum_label2").text('融入券全价总额');
            }
            if (ps == PS_T03 || ps == PS_T39) {
                $("#integrateTradeAmt").prop("disabled", true);
            }
        }
        else if (ps == PS_T01) {
            // $("#yield").attr("disabled", false);
            $("#exerYield").attr("disabled", false);
            // $("#calculateField_yield").attr("disabled", false);
            $("#calculateField_exerYield").attr("disabled", false);
            if($("#zhilingtype").val() =="02"){
            	window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].hideCash(true);
            }else{
            	window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showCash(true);
            }
            if (shortSellingSupported == '01') {
                // 该交易方向下：场内交易不展示融入券、本方券应计利息总额
                if(parentPageId && parentPageId == "TrdBondInvestTradeEx001"){
                    $("#assetIntegrateSaleAvailablePosition_div").hide();
                    $("#ownIntAmt_div").hide();
                }else{
                    $("#assetIntegrateSaleAvailablePosition_div").show();
                }
                $("#assetIntegrateAvailablePosition_div").hide();
                $("#assetOwnAvailablePosition_div").hide();
                $(".ownNum_label").text('卖空购回券面总额');
                $(".ownNum_label1").text('卖空购回应计利息总额');
                $(".ownNum_label2").text('卖空购回全价总额');
            }
        }
        else {
        	if($("#zhilingtype").val() =="02"){
            	window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].hideCash(true);
            }else{
            	window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showCash(true);
            }
        }

        if ((ps == PS_T01||ps == PS_T02|| ps == PS_T03)&&$("#chlId").val()){
        	$("#channelFee_div").show();
        }else{
        	$("#channelFee_div").hide();
        }
        checkChannelFee();
        changeTradeAmtAndFee();
	   //wangyang1 
        if (BANKCODE == '004') {
        	changeTurnInvAim();
	}	
    });

    var setParam = function(){
        return {portfolioId: $('#portfolioId').val(), finprodId: $('#finprodId').val(),
            invAim: $("#invAim").val(), secAcctId: $("#secManageAcctId").val(),
            tradeDate: $("#tradeDate").val(), ccy: $("#ccy").val(),
            chlId: $("#chlId").val()
        };
    }

    /**
     * 融入券券面总额
     */
    $('#integrateTradeAmt').change(function () {
        var tradeAmt = $("#tradeAmt").val();
        if (!tradeAmt) {
            tradeAmt = 0;
        }
        var integrateTradeAmt = $("#integrateTradeAmt").val();
        var entrustOwnNum = JoyinMath.doMinus(tradeAmt, integrateTradeAmt, 4, JoyinMath.ROUND_HALF_UP)
        if (entrustOwnNum < 0) {
            $("#ownTradeAmt").val(tradeAmt).trigger("blur");
            $("#integrateTradeAmt").val(0).trigger("blur");
        } else {
            $("#ownTradeAmt").val(entrustOwnNum).trigger("blur");
        }
    });

    // 绑定输入框失焦事件
    $('#refundRate').change(function () {
        var tradeAmt = $("#tradeAmt").val();
        var refundRate = $("#refundRate").val();
        var refundRateMulHundred = JoyinMath.doMul(refundRate, 100, 4, JoyinMath.ROUND_HALF_UP)
        var refundAmount = JoyinMath.doMul(tradeAmt, refundRateMulHundred, 2, JoyinMath.ROUND_HALF_UP)
        if(refundAmount < 0) {
            $("#refundRate").val(refundRate).trigger("blur");
            $("#refundAmount").val(0).trigger("blur");
        } else {
            $("#refundAmount").val(refundAmount).trigger("blur");
        }
    });

    /**

     * 本方券券面总额
     */
    $('#ownTradeAmt').change(function () {
        var tradeAmt = $("#tradeAmt").val();
        if (!tradeAmt) {
            tradeAmt = 0;
        }
        var ownTradeAmt = $("#ownTradeAmt").val();

        var integrateTradeAmt = JoyinMath.doMinus(tradeAmt, ownTradeAmt, 4, JoyinMath.ROUND_HALF_UP)
        if (integrateTradeAmt < 0) {
            $("#tradeAmt").val(ownTradeAmt).trigger("blur");
            $("#integrateTradeAmt").val(0).trigger("blur");
        } else {
            $("#integrateTradeAmt").val(integrateTradeAmt).trigger("blur");
        }
    });

    $('#tradeAmt').change(function () {
        var tradeAmt = $('#tradeAmt').val();
        var integrateTradeAmt = $("#integrateTradeAmt").val();
        if (!integrateTradeAmt) {
    //modify by yujiaping 2020-12-08 ZGBS-64【债券二级交易】卖出交易，交易对手选择内部产品时，计算要素未变更，也弹出提醒，请重新点击计算按钮start
            $("#integrateTradeAmt").val('0.00')
    //modify by yujiaping 2020-12-08 ZGBS-64【债券二级交易】卖出交易，交易对手选择内部产品时，计算要素未变更，也弹出提醒，请重新点击计算按钮end
            integrateTradeAmt = 0;
        }

        var ownTradeAmt = JoyinMath.doMinus(tradeAmt, integrateTradeAmt, 4, JoyinMath.ROUND_HALF_UP);
        if (ownTradeAmt < 0) {
            $("#ownTradeAmt").val(tradeAmt).trigger("blur");
            $("#integrateTradeAmt").val(0).trigger("blur");
        } else {
            $("#ownTradeAmt").val(ownTradeAmt).trigger("blur");
        }
    });

    /**

     * 内部交易买入显示通道费，外部交易买入置空通道费
     */
    var checkChannelFee = function(){
        var ps = $('#ps').val();
        if(ps==PS_T01&&$('#trdInvestPersonInfo_counterId').val()&&$("#trdInvestPersonInfo_counterId").select2('data')[0].object!='N'){
        	$("#channelFee").val("").attr('disabled',true);
        }else{
        	$("#channelFee").attr('disabled',false);
        }
    }
    /**

     * 1.4清算速度、交易日、交割日之间有联动关系
     */
    $("#settleSpeed").on("change", function () {
        // TISHKS-227:对清算速度进行数字校验 fuyongsheng 20211105 add start
        var settleSpeed = $("#settleSpeed").val();
        var reg = /^\d{1}$/;
        // 校验清算速度是否为数字
        if(settleSpeed && !reg.test(settleSpeed)){
            Joyin.error(JoyinUtil.getLabel("TrdBondInvestTrade002.settleSpreed")+ "数字格式不正确!");
            return;
        }
        // TISHKS-227:对清算速度进行数字校验 fuyongsheng 20211105 add end
        calcSettleDate();
    });
    /**
     * 交易日历变化，联动交割日
     */
    $("#calendarId").on("change", function () {
        calcSettleDate();
    });
    // by zhaodongdong 20200227 start
    /**
     * 上交所固收平台123代码段的公司债，资产支持证券和中小企业私募债，清算速度为T+0或T+1,其他债券的清算速度为T+0,不可改。
     */
    $('#finprodId,#tradePlat,#settleSpeed').change(function (){
        // by 赵东东
        var finprodId = $('#finprodId').val();
        var finprodId1;
        var finprodIdarr;
        var finprodIda;
        if(finprodId){
        var finprodtId = finprodId.split("_");
        var data = $('#finprodId').select2('data')[0].object;
        if (data && data.finprodId) {
            finprodId1 = finprodtId[1];
        }else{
            finprodId1 = finprodId;
        }
        finprodIdarr = finprodId1.split(".");
        finprodIda = finprodIdarr[0];
        }
        if(finprodId1 && finprodId1.length>3 && finprodIda.length==6){
        var fId = finprodId1.substring(0,3);
        }
        var tradeMarket = $('#tradeMarket').val();
        var tradePlat = $('#tradePlat').val();
        var type10 = $('#type10').val();
        var type11 = $('#type11').val();
        var settleSpeed = $('#settleSpeed').val();
        if(settleSpeed && fId == '123' && tradeMarket == '03' && tradePlat == '03' ) {
            if(type10 == '12' || type11== '012' || type11 =='014' ){
                if(!(settleSpeed == 0 || settleSpeed ==1)){
                	Joyin.alert(JoyinUtil.getMessage("ETR_BOND_1111"));
                	$('#settleSpeed').val('');
                }
            }else{
            	$('#settleSpeed').val('0').attr('disabled', true);
            }
        }else if(fId == '123' && tradeMarket == '03' && tradePlat == '03'){
        	if(!(type10 == '12' || type11== '012' || type11 =='014') ){
        		$('#settleSpeed').val('0').attr('disabled', true);
            }
        }
        else{
        	$('#settleSpeed').attr('disabled', false);
        }
    });
    // by zhaodongdong 20200227 end
//    /**
//     * 债券一级分类change事件
//     */
//    $('#windtypeone').on('change', function() {
//        if ($('#windtypeone').val()) {
//            if ($('#finprodId').val()) {
//                var data = $('#finprodId').select2('data')[0].object;
//                if(data.windtypeone && $('#windtypeone').val() == data.windtypeone) {
//                    // 已经选中的债券对应的债券一级分类和选中的债券一级分类一致，则不清空已选择资产代码
//                } else {
//                    $("#finprodId").val("").trigger('change');
//                }
//            }
//        }
//    });
    /**
     * 1.5资产代码选择时
     * 资产名称文本框根据资产代码选择自动显示对应名称;
     * 同时带出资产相关信息，包括起息日，到期日，发行日，交易场所，币种，交易日历，计息基础等;
     */
    $('#finprodId').on('change', function (e, isInit) {
        var finprodId = $('#finprodId').val();
        var data = $('#finprodId').select2('data')[0].object;
        if($('#finprodId').select2('data')[0].id){
        	if (data && data.finprodId) {// 资产名称文本框根据资产代码选择自动显示对应名称;
                $("#finprodName").val(data.finprodMarketId);
            // 同时带出资产相关信息，包括起息日，到期日，发行日，交易场所，币种，交易日历，计息基础等;
            $("#vdate").val(data.vdate).trigger('change');
            $("#mdate").val(data.mdate).trigger('change');
            $("#idate").val(data.idate).trigger('change');
            $("#type6").val(data.type6).trigger('change');
            if (data.tradeMarket) {
                $("#tradeMarket").val(data.tradeMarket);
                if(pageModel==PAGE_MODEL_NEW){
                    $("#tradeMarket").trigger('change')
                }
            }else{
                if(pageModel==PAGE_MODEL_REFERENCE){
                    $("#tradeMarket").trigger('change')
                }
            }
            $("#ccy").val(data.ccy).trigger('change');
            $("#calendarId").val(data.calendarId).trigger('change');
            $("#basis").val(data.basis).trigger('change');

            // 新增资产二、三、四级分类  王贵奇  2019/7/30 开始
            //延时加载
            setTimeout(function () {
                $("#type9").val(data.type9).trigger('change');
                $("#type10").val(data.type10).trigger('change');
                $("#type11").val(data.type11).trigger('change');
            },1000);
            // 新增资产二、三、四级分类  王贵奇  2019/7/30 结束
//            if(data.windtypeone && !$("#windtypeone").val()) {
//                $("#windtypeone").val(data.windtypeone).trigger('change');
//            }
                $("#windtypeone").val(data.windtypeone).trigger('change');

            /*// 是否含权债
            $("#isRight").val(data.isRight).trigger('change');*/
            // 控制显示行权收益率
            exerYieldDivContrl();
            } else {
	        	//自动加券 yjm
	        	var autoCoupon = AutoCoupon.autoBondTradeCoupon($('#finprodId').val());
	        	// TISHKS-532:自动加券债券风险试算报错 modify by fuyongsheng 20220509 start
                $('#finprodId').val(autoCoupon.finprodId).trigger('change');
                // TISHKS-532:自动加券债券风险试算报错 modify by fuyongsheng 20220509 end
                isautobond = true;
	            // 资产名称文本框根据资产代码选择自动显示对应名称;
	            $("#finprodName").val(autoCoupon.finprodMarketId).trigger('change');
	            // 同时带出资产相关信息，包括起息日，到期日，发行日，交易场所，币种，交易日历，计息基础等;
	            $("#vdate").val(autoCoupon.vdate).trigger('change');
	            $("#mdate").val(autoCoupon.mdate).trigger('change');
	            $("#idate").val(autoCoupon.idate).trigger('change');
	            if (autoCoupon.tradeMarket) {
	                $("#tradeMarket").val(autoCoupon.tradeMarket).trigger('change');
	            }
	            $("#ccy").val(autoCoupon.ccy).trigger('change');
	            $("#calendarId").val(autoCoupon.calendarId).trigger('change');
	            $("#basis").val(autoCoupon.basis).trigger('change');
	            $("#windtypeone").val(autoCoupon.windtypeone).trigger('change');
	            $("#finProd_id1").val(autoCoupon.finprodId).trigger('change');
	            $("#windtypeone").val(autoCoupon.type4).trigger('change');
	           // $('#finprodId').textSelect('setValue', autoCoupon.finprodId);
	            
                $("#type9").val(autoCoupon.type9).trigger('change');
                $("#type10").val(autoCoupon.type10).trigger('change');
                $("#type11").val(autoCoupon.type11).trigger('change');
			}
        	 updateCash();
             updateSecAcctBalance();

             /*// 是否含权债
             $("#isRight").val(data.isRight).trigger('change');*/
             // 控制显示行权收益率
             exerYieldDivContrl();
        } else {
            // 资产名称文本框根据资产代码选择自动显示对应名称;
            $("#finprodName").val("").trigger('change');
            // 同时带出资产相关信息，包括起息日，到期日，发行日，交易场所，币种，交易日历，计息基础等;
            $("#vdate").val("").trigger('change');
            $("#mdate").val("").trigger('change');
            $("#idate").val("").trigger('change');
            $("#ccy").val("").trigger('change');
            $("#calendarId").val("").trigger('change');
            $("#basis").val("").trigger('change');

            // add by etliu
            $("#windtypeone").val("").trigger('change');
        }
        // 1.6计算偏离度
        calcDeviationDegree();
        // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 start //
        if (IS_SHOW_CPRICE == 'Y') {
        	//changeUnitCprice();
        }
        // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 end //
        TradeCommon.getSecPositionForSaleAll(setParam(), function (result) {
            if(result){
                if(result.OWN_POSITION){
                    $('#assetOwnAvailablePosition').val(result.OWN_POSITION).trigger("blur");
                }
                if(result.INTEGRATE_POSITION){
                    $('#assetIntegrateAvailablePosition').val(result.INTEGRATE_POSITION).trigger("blur");
                }
                if(result.SHOWHANDS_POSITION){
                    $('#assetIntegrateSaleAvailablePosition').val(result.SHOWHANDS_POSITION).trigger("blur");
                }
            }
        });
        
        var select2Flag = $("#secManageAcctId").hasClass("select2-hidden-accessible");
        if(select2Flag){
        	$("#secManageAcctId").select2("val", " "); 
        }
        
    });
    /**
     * 资产名称查看
     * 若点击资产名称后面的【查看】按钮跳转到本资产的详情画面
     */
    $('#TrdBondInvestTrade002_btn_viewAsset').click(function(){
        if ($('#finprodId').val()) {
            Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002.title"), basePath
                    + "invest/asset/bond/FinBondTargetAsset002/init", "95%", "90%", {
                pageModel : PAGE_MODEL_REFERENCE,
                finprodId : $('#finprodId').val()
            });
        }
    })
    
    // 显示净价信息查看及回显  吉林银行产品化需求  2019.12.2 马琳琳 start //
	/**
	 * 净价信息查看
	 * 若点击净价后面的【查看】按钮显示本资产的净价详情
	 */
	$('#TrdBondInvestTrade002_btn_viewCprice').click(function(){
		if ($("#ps").val() == PS_T02) {
    		// 交易对手为内部交易时查询债权市值净价及摊销净价
    		if ($("#trdInvestPersonInfo_counterId").val()
    				&& $("#trdInvestPersonInfo_counterId").select2('data')[0].object=='N') {
    			if($("#portfolioId").val() && $("#finprodId").val() && $("#invAim").val()) {
    				var paramObj = getCalcParam();
    				paramObj.portfolioId = $("#portfolioId").val();
    				paramObj.finprodId = $("#finprodId").val();
    				paramObj.invAim = $("#invAim").val();
                    if($("#chlId").val()){
                        paramObj.chlId = $("#chlId").val();
                    }
    				paramObj.counterId = $("#trdInvestPersonInfo_counterId").val();
    				paramObj.tradeDate = $("#tradeDate").val();
    				Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/getCprice'), paramObj, function (result) {
    					if (result && result.data) {
    		                var data = result.data; 
    		                var netprice = "";
    		                var dscprice = "";
    		                if (data.netprice != null && data.netprice != 'undefined') {
    		                	netprice = data.netprice;
    		                }
    		                if (data.dscprice != null && data.dscprice != 'undefined') {
    		                	dscprice = data.dscprice;
    		                }
    		                Joyin.confirm("市值净价:"+netprice+"    摊销净价:"+dscprice, function() {});
    					} 
    				});
    			} 
    		} 
    	} 
	})
	
    $("#portfolioId").on("change", function (e, isInit) {
        // 债券投资 改变组合 需联动 证券账户 ,证券托管户应清空选择项
        if (!isInit && pageModel !=PAGE_MODEL_REFERENCE&&pageModel !=PAGE_MODEL_EDIT) {
        	if(!$("#fTradeId").val()){
        		$("#secManageAcctId").val('').trigger("change");
        	}
        }
        // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 start //
        if (IS_SHOW_CPRICE == 'Y') {
        	changeUnitCprice();
        }
        updateSecAcctBalance();
        updateCash();
        // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 end //
        //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 begin
        //if(BANKCODE == '004'){
        if($("#fTradeId").val()){
        	calcInvAim();
        	if($("#portfolioId").val()){
        		$("#invAim").attr('disabled',true);
    		}else{
    			$("#invAim").val('').trigger('change');
    			$("#invAim").attr('disabled',false);
    		}
        }
       //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 end
        TradeCommon.getSecPositionForSaleAll(setParam(), function (result) {
            if(result){
                if(result.OWN_POSITION){
                    $('#assetOwnAvailablePosition').val(result.OWN_POSITION).trigger("blur");
                }
                if(result.INTEGRATE_POSITION){
                    $('#assetIntegrateAvailablePosition').val(result.INTEGRATE_POSITION).trigger("blur");
                }
                if(result.SHOWHANDS_POSITION){
                    $('#assetIntegrateSaleAvailablePosition').val(result.SHOWHANDS_POSITION).trigger("blur");
                }
            }
        });
    });
    /**
     * 证券托管户变化更新资产可用持仓
     */
    $("#secManageAcctId").on("change", function () {
        if ($("#secManageAcctId").val()) {
            $("#chlId").val('').prop("disabled", true);
        }
        else {
            $("#chlId").prop("disabled", false);
        }
        updateCash();
        getTradeMarketCash();
        
        // 将甲类户对应的交易手续费、结算费、佣金设置为不可编辑
//        controllFeeItem();
        TradeCommon.getSecPositionForSaleAll(setParam(), function (result) {
            if(result){
                if(result.OWN_POSITION){
                    $('#assetOwnAvailablePosition').val(result.OWN_POSITION).trigger("blur");
                }
                if(result.INTEGRATE_POSITION){
                    $('#assetIntegrateAvailablePosition').val(result.INTEGRATE_POSITION).trigger("blur");
                }
                if(result.SHOWHANDS_POSITION){
                    $('#assetIntegrateSaleAvailablePosition').val(result.SHOWHANDS_POSITION).trigger("blur");
                }
            }
        });
    });
    //查询交易所资金账户可用现金
    var getTradeMarketCash = function(){
        var data = {portfolioId:$('#portfolioId').val(),secAcctId:$('#secManageAcctId').val(),ccy:$('#ccy').val(),tradeDate:$('#tradeDate').val()}
        if(data.portfolioId && data.secAcctId && data.ccy && data.tradeDate){
        Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade002/getTradeMarketCash',data,
                function(result) {           
            $('#tradeMarketCash').val(result.cashS).trigger('blur');
        });
        }
    };  

    $("#chlId").on("change", function () {
        if ($("#chlId").val()) {
        	$("#commissionAmtDiv").hide();
            $("#secManageAcctId").val('').prop("disabled", true);
            if($("#ps").val()&&($("#ps").val()==PS_T01||$("#ps").val()==PS_T02||$("#ps").val()==PS_T03)){
            	$("#channelFee_div").show();
            }else{
            	$("#channelFee_div").hide();
            }
        }
        else {
            $("#secManageAcctId").prop("disabled", false);
            $("#channelFee_div").hide();
            if(( $("#ps").val()=="T01" ||  $("#ps").val()=="T02")&&( $("#tradeMarket").val()=="03" || $("#tradeMarket").val()=="04")){            	
            	$("#commissionAmtDiv").show();
            }
        }
        updateCash();
        changeTradeAmtAndFee();
        // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 start //
        if (IS_SHOW_CPRICE == 'Y') {
        	changeUnitCprice();
        }
        // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 end //
        TradeCommon.getSecPositionForSaleAll(setParam(), function (result) {
            if(result){
                if(result.OWN_POSITION){
                    $('#assetOwnAvailablePosition').val(result.OWN_POSITION).trigger("blur");
                }
                if(result.INTEGRATE_POSITION){
                    $('#assetIntegrateAvailablePosition').val(result.INTEGRATE_POSITION).trigger("blur");
                }
                if(result.SHOWHANDS_POSITION){
                    $('#assetIntegrateSaleAvailablePosition').val(result.SHOWHANDS_POSITION).trigger("blur");
                }
            }
        });
    });

    $("#channelFee").on("change", function () {
        changeTradeAmtAndFee();
    });

    $("#fpriceAmt").on("change", function () {
        changeTradeAmtAndFee();
    });

    $("#settleAmt").on("change", function () {
        changeTradeAmtAndFee();
    });

    $("#exchangeAmt").on("change", function () {
        changeTradeAmtAndFee();
    });
    //更改计算规则
    var changeTradeAmtAndFee = function() {
        //在重新计算前将值置空，否则会重复进行计算
        $("#tradeAmtAndFee").val('0');
        if ($("#ps").val() == PS_T01) { //买入方向
            if($("#fpriceAmt").val()){  //判断全价金额有没有填写
                $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#fpriceAmt").val(), 0, 2)).trigger('blur');
            }
            /*if($("#procedureAmt").val()){
                $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#tradeAmtAndFee").val(), $("#procedureAmt").val(), 2)).trigger('blur');
            }
            if($("#settleAmt").val()){
                $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#tradeAmtAndFee").val(), $("#settleAmt").val(), 2)).trigger('blur');
            }*/
            if (stampDutyIsPayWithTrade == YES_OR_NO_Y) {
                $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#tradeAmtAndFee").val(), $("#stampDutyAmt").val(), 2)).trigger('blur');
            }

             if (isPayWithTrade == YES_OR_NO_Y) { //随交易支付
                 $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#tradeAmtAndFee").val(), $("#commissionAmt").val(), 2)).trigger('blur');
             }
             if(settleIsPayWithTrade == YES_OR_NO_Y){
                 //若结算费不为空
                 if($("#settleAmt").val()){
                     $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#tradeAmtAndFee").val(), $("#settleAmt").val(), 2)).trigger('blur');
                 }
             }
            if(exchangeIsPayWithTrade == YES_OR_NO_Y) {
                //若经手费不为空
                if ($("#exchangeAmt").val()) {
                    $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#tradeAmtAndFee").val(), $("#exchangeAmt").val(), 2)).trigger('blur');
                }
            }
             //内转买入 减去通道费
             if ($("#trdInvestPersonInfo_counterId").val()&&$("#trdInvestPersonInfo_counterId").select2('data')[0].object=='N') {
            	 $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#channelFee").val(), 2)).trigger('blur');
             }

        }
        else { //卖出方向
            if($("#fpriceAmt").val()){
                $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#fpriceAmt").val(), 0, 2)).trigger('blur');
            }
            /*if($("#procedureAmt").val()){
                $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#procedureAmt").val(), 2)).trigger('blur');
            }
            if($("#settleAmt").val()){
                $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#settleAmt").val(), 2)).trigger('blur');
            }*/
            // $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#fpriceAmt").val(), $("#channelFee").val(), 2)).trigger('blur');
            if (isPayWithTrade == YES_OR_NO_Y) {
                $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#commissionAmt").val(), 2)).trigger('blur');
            }
            if(settleIsPayWithTrade == YES_OR_NO_Y){
                //若结算费不为空
                if($("#settleAmt").val()){
                    $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#settleAmt").val(), 2)).trigger('blur');
                }
            }
            if(exchangeIsPayWithTrade == YES_OR_NO_Y) {
                //若经手费不为空
                if($("#exchangeAmt").val()){
                    $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#exchangeAmt").val(), 2)).trigger('blur');
                }
            }
             if (stampDutyIsPayWithTrade == YES_OR_NO_Y) {
                 $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#stampDutyAmt").val(), 2)).trigger('blur');
             }
             if (($("#trdInvestPersonInfo_counterId").val()&&$("#trdInvestPersonInfo_counterId").select2('data')[0].object=='N')
            		 ||($("#trdInvestPersonInfo_counterId").val()&&$("#trdInvestPersonInfo_counterId").select2('data')[0].object!='N'
            			 &&($("#ps").val() == PS_T02||$("#ps").val() == PS_T03)&&$("#chlId").val()&&$("#chlId").select2("data")[0].object.collectType==Collect_Type_01)) {
            	 $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#channelFee").val(), 2)).trigger('blur');
             }else if(!$("#trdInvestPersonInfo_counterId").val()&&($("#ps").val() == PS_T02||$("#ps").val() == PS_T03)&&$("#chlId").val()&&$("#chlId").select2("data")[0].object.collectType==Collect_Type_01){
				$("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#channelFee").val(), 2)).trigger('blur');
             }
        }
    }
    // 一体化对应etliu end
    
    /**
     * 投资目的变化更新资产可用持仓
     */
    $("#invAim").on("change", function () {
        updateCash();
        //priceInitByInCpy();
        // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 start //
        if (IS_SHOW_CPRICE == 'Y') {
        	changeUnitCprice();
        }
        // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 end //
        TradeCommon.getSecPositionForSaleAll(setParam(), function (result) {
            if(result){
                if(result.OWN_POSITION){
                    $('#assetOwnAvailablePosition').val(result.OWN_POSITION).trigger("blur");
                }
                if(result.INTEGRATE_POSITION){
                    $('#assetIntegrateAvailablePosition').val(result.INTEGRATE_POSITION).trigger("blur");
                }
                if(result.SHOWHANDS_POSITION){
                    $('#assetIntegrateSaleAvailablePosition').val(result.SHOWHANDS_POSITION).trigger("blur");
                }
            }
        });
    });
    // 一体化对应etliu end

    //缴款方式切换的事件
    $("#payType").on("change", function() {
        //修改费用清零条件
        if($("#tradeMarketMode").val()=="01"){
            //一级缴款,所有费用为0
            if($("#payType").val()=="02"){
                $("#commissionAmtDiv").hide();
                $("#settleAmtDiv").hide();
                $("#exchangeAmtDiv").hide();
                ClearNum();
            }

            if($("#tradeMarket").val()!="01"){
                //二级交易，若为交易所债券，显示佣金，经手费和结算费
                if($("#payType").val()=="03"){
                	if(!$("#chlId").val()){               		
                		$("#commissionAmtDiv").show();
                	}
                    $("#settleAmtDiv").show();
                    $("#exchangeAmtDiv").show();
                    ClearNum();
                }else if($("#payType").val()=="02"){
                    $("#commissionAmtDiv").hide();
                    $("#settleAmtDiv").hide();
                    $("#exchangeAmtDiv").hide();
                    ClearNum();
                }
                //一级分销，若是交易所，显示佣金
                if($("#payType").val()=="01"){
                	if(!$("#chlId").val()){ 
                    $("#commissionAmtDiv").show();
                	}
                    $("#settleAmtDiv").hide();
                    $("#exchangeAmtDiv").hide();
                    ClearNum();
                }
            }

        }
    });

    function ClearNum(){
        $("#commissionAmt").val('').trigger("blur");
        $("#settleAmt").val('').trigger("blur");
        $("#exchangeAmt").val('').trigger("blur");
        $("#stampDutyAmt").val('').trigger("blur");
        $("#procedureAmt").val('').trigger("blur");
        $("#settleServiceAmt").val('').trigger("blur");
        changeTradeAmtAndFee();
    }

    /**
     * 数值项目change后记录至隐藏值
     */
    $("#unitCprice").on("change", function() {
        $("#unitCprice_calc").val("");
    });
    $("#unitInt").on("change", function() {
        $("#unitInt_calc").val("");
    });
    $("#unitFprice").on("change", function() {
        $("#unitFprice_calc").val("");
    });
    $("#cpriceAmt").on("change", function() {
        $("#cpriceAmt_calc").val("");
    });
    $("#intAmt").on("change", function() {
        $("#intAmt_calc").val("");
    });
    $("#fpriceAmt").on("change", function() {
        $("#fpriceAmt_calc").val("");
        //add by 裴家俊----start----------
        // modified by wangguoxian 费用设置 20190808 start
        // relatedTradeRepFee($("#commissionAmt"),$(this));
        changeTradeAmtAndFee();
        // modified by wangguoxian 费用设置 20190808 end
        //add by 裴家俊----end----------
    });
    $("#yield").on("change", function() {
        $("#yield_calc").val("");
        if($('#ps').val()=='T01'){//买入
            if(entrustYield&&Number($(this).val())<Number(entrustYield)){
                Joyin.warn("买入时，到期收益率不能小于委托的到期收益率");
            }
        }else if($('#ps').val()=='T02'){//卖出
            if(entrustYield&&Number($(this).val())>Number(entrustYield)){
                Joyin.warn("卖出时，到期收益率不能大于委托的到期收益率");
            }
        }
    });
    $("#exerYield").on("change", function() {
        $("#exerYield_calc").val("");
        if($('#ps').val()=='T01'){//买入
            if(entrustExerYield&&Number($(this).val())<Number(entrustExerYield)){
                Joyin.warn("买入时，行权收益率不能小于委托的行权收益率");
            }
        }else if($('#ps').val()=='T02'){//卖出
            if(entrustExerYield&&Number($(this).val())>Number(entrustExerYield)){
                Joyin.warn("卖出时，行权收益率不能大于委托的行权收益率");
            }
        }
    });

    //----add by 裴家俊  ---start-
    $("#commissionAmt").on("change", function() {
        // modified by wangguoxian 费用设置 20190808 start
    	// relatedTradeRepFee($(this),$("#fpriceAmt"));
        changeTradeAmtAndFee();
        // modified by wangguoxian 费用设置 20190808 end
    });
    $("#stampDutyAmt").on("change", function() {
        changeTradeAmtAndFee();
    });
    // modified by wangguoxian 费用设置 20190808 start
    /*var relatedTradeRepFee=function(commissionAmt,fpriceAmt){
    	if($(commissionAmt).val()&&$(fpriceAmt).val()&&isPayWithTrade == YES_OR_NO_Y){
        	if($("#ps").val()==PS_T01){
				$("#tradeAmtAndFee").val(Number($(fpriceAmt).val())+Number($(commissionAmt).val())).trigger('blur');
			}
			if($("#ps").val()==PS_T02){
				$("#tradeAmtAndFee").val(Number($(fpriceAmt).val())-Number($(commissionAmt).val())).trigger('blur');
			}
        }
    	else{
    		if($(fpriceAmt).val()){
    			$("#tradeAmtAndFee").val(Number($(fpriceAmt).val())).trigger('blur');;
    		}
    		else{
    			$("#tradeAmtAndFee").val("").trigger('blur');
    		}
    	}
    }*/
    // modified by wangguoxian 费用设置 20190808 end
  //----add by 裴家俊  ---end------

    function getCalcParam(needCheck) {
        var calculateField = $("[name=calculateField]:radio").next(".layui-form-radioed").prev("input").val();
        if (needCheck && $("#bondFirstSign").val() && calculateField != 'yield') {
            return false;
        }
        var paramObj = $.serializeToObject('#submitForm', true);
        paramObj.calculateField = $("[name=calculateField]:checked").val();
        paramObj.calculateField = calculateField;
//        paramObj.settleAmt=$("#settleAmt").val();
//        paramObj.exchangeAmt=$("#exchangeAmt").val();
//        paramObj.commissionAmt=$("#commissionAmt").val();

        paramObj.tradeAmt = JoyinMath.doMul( $("#tradeAmt").val()?$("#tradeAmt").val():0,unitNum,2,JoyinMath.ROUND_HALF_UP);
        paramObj.unitCprice = $("#unitCprice").val();
        paramObj.unitInt = $("#unitInt").val();
        paramObj.unitFprice = $("#unitFprice").val();
        paramObj.cpriceAmt = $("#cpriceAmt").val();
        paramObj.intAmt = $("#intAmt").val();
        paramObj.fpriceAmt = $("#fpriceAmt").val();
        paramObj.exerYield = $("#exerYield").val();
        paramObj.ownTradeAmt = JoyinMath.doMul( $("#ownTradeAmt").val()?$("#ownTradeAmt").val():0,unitNum,2,JoyinMath.ROUND_HALF_UP);
        paramObj.integrateTradeAmt = JoyinMath.doMul( $("#integrateTradeAmt").val()?$("#integrateTradeAmt").val():0,unitNum,2,JoyinMath.ROUND_HALF_UP);
        paramObj.yield = $("#yield").val();

        // 隐藏域如果有值，使用隐藏域
        if ($("#unitCprice_calc").val()) {
            paramObj.unitCprice = $("#unitCprice_calc").val();
        }
        if ($("#unitInt_calc").val()) {
            paramObj.unitInt = $("#unitInt_calc").val();
        }
        if ($("#unitFprice_calc").val()) {
            paramObj.unitFprice = $("#unitFprice_calc").val();
        }
        if ($("#cpriceAmt_calc").val()) {
            paramObj.cpriceAmt = $("#cpriceAmt_calc").val();
        }
        if ($("#intAmt_calc").val()) {
            paramObj.intAmt = $("#intAmt_calc").val();
        }
        if ($("#fpriceAmt_calc").val()) {
            paramObj.fpriceAmt = $("#fpriceAmt_calc").val();
        }
        if ($("#exerYield_calc").val()) {
            paramObj.exerYield = $("#exerYield_calc").val();
        }
        if ($("#yield_calc").val()) {
            paramObj.yield = $("#yield_calc").val();
        }
        if($("#tradeMarketCash").val() == 0 ||$("#tradeMarketCash").val() == 0.00){
            paramObj.tradeMarketCash = null;
        }
        // 行权计算时用数据库中的理论行权日 add by renping 20191105 start
        if(paramObj.finprodId && ($("#ps").val() == PS_T03 || $("#ps").val() == PS_T39)) {
            Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/getExerciseDate'), paramObj, function (result) {
                if (result && result.data) {
                    paramObj.settleDate = result.data;
                }
            }, false);
            
        }
        // 行权计算时用数据库中的理论行权日 add by renping 20191105 end
        
        // 计算之前，先将应计利息清空
        var tradeMarketMode = $("#tradeMarketMode").val();
        if (tradeMarketMode != SHARES_TRADE_MARKET_01) {
            paramObj.unitInt = null;
        }
        // add by wangguoxian 费用设置 20190808 start
        // 交易手续费，结算费，佣金，通道费，交易金额不再计入计算要素
        paramObj.procedureAmt = null;
        paramObj.settleServiceAmt = null; //结算服务费
        paramObj.settleAmt = null;
        paramObj.commissionAmt = null;
        paramObj.channelFee = null;
        paramObj.tradeAmtAndFee = null;
        paramObj.exchangeAmt = null;
        if ($("#businessModel").val()){
           paramObj.invAim = $("#businessModel").val();
        }
        // add by wangguoxian 费用设置 20190808 end
        return paramObj;
    }

    /**
     * 净价change事件
     * 1.6计算偏离度
     */
    $("#unitCprice").on("change", function () {
        if($('#ps').val()=='T01'){//买入
            if(entrustPrice&&Number($(this).val())>Number(entrustPrice)){
                Joyin.warn("买入时，净价不能大于委托的净价");
            }
        }else if($('#ps').val()=='T02'){//卖出
            if(entrustPrice&&Number($(this).val())<Number(entrustPrice)){
                Joyin.warn("卖出时，净价不能小于委托的净价");
            }
        }
        calcDeviationDegree();
        //多组合指令类型净价改变影响净价总额
        if($("#zhilingtype").val()=="02"){
           updateBussTrdTransfenpeiTable();
        }
    });

    /**
     * 1.6计算偏离度
     */
    var calcDeviationDegree = function() {
        if ($("#finprodId").val() && $("#unitCprice").val()) {
            var paramObj = {};
            paramObj.finprodId = $('#finprodId').val();
            if($('#finprodId').select2('data')[0].object){
            paramObj.finprodMarketId = $('#finprodId').select2('data')[0].object.finprodMarketId;
            }
            paramObj.unitCprice = $("#unitCprice").val();
            Joyin.ajaxSync(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/calcDeviationDegree'), paramObj, function (data) {
                $("#deviationDegreeLabel").text(data);
            });
        } else {
            $("#deviationDegreeLabel").text("");
        }
    };

    //画面初始化
    var init = function() {
        /**
         * 若从委托场合点击交易录单
         */
        var entrustId = $("#entrustId").val();
        var bondFirstSign = $("#bondFirstSign").val();
        var winPrinAmt = $("#winPrinAmt").val();
        var secManageAcctId = $('#secManageAcctId').val();
        if(entrustAmtUnit == '01' ){
            $(".unitNum").text('元');
            unitNum = 1;
        }else{
            unitNum = 10000;
        }
        if(fTradeIdFromCstp){
            $('#fTradeId').textSelect('setValue',fTradeIdFromCstp);
            $('#fTradeId').attr('disabled',true);
        }
        if (shortSellingSupported == '02') {
            $(".shortSellingSupported").hide();
        }
        if(entrustId&&pageModel != PAGE_MODEL_REFERENCE){
            Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade002/getEntrustInfo', {'entrustId':entrustId,'bondFirstSign':bondFirstSign},function (data) {
                // 基本要素信息由投资委托带出，其中有：委托代码、组合代码、资产代码、清算速度、交易方向、交易场所、交易平台、交易市场、投资目的等，不可编辑
                // 金融要素信息由投资委托带出，其中有：币种、券面总额、净价、到期收益率、行权收益率等
                if(data != null && data.etrEntrust != null){
                    etrEntrust = data.etrEntrust;
                    etrEntrustAdd = data.etrEntrustAdd;
                    calculateField = etrEntrustAdd.calculateField;
                    if(etrEntrustAdd.calculateField == "price"){
                        calculateField = "unitCprice";
                    }
                    else if(etrEntrustAdd.calculateField == "matureYield"){
                        calculateField = "yield";
                    }
                    if(calculateField){
                        $("#calculateField_" + calculateField).attr("checked", true);
                        layui.form.render('radio');
                    }
                    checkAndAssign("#portfolioId", etrEntrust.portfolioId); // 组合代码、
                    $('#portfolioId').textSelect('setValue', etrEntrust.portfolioId);
                    checkAndAssign("#finprodId", etrEntrust.finprodId);
                    $('#finprodId').textSelect('setValue', etrEntrust.finprodId); // 资产代码
                    // 资产名称文本框根据资产代码选择自动显示对应名称;
                    $("#finprodName").val(data.finprodName).trigger('change');
                    
                    checkAndAssign("#tradeMarket", etrEntrust.tradeMarket); // 交易场所
                    checkAndAssign("#tradeMarketMode", etrEntrust.tradeMarketMode); // 交易市场
                    checkAndAssign("#tradePlat", etrEntrust.tradePlat); // 交易平台
                    checkAndAssign("#ps", etrEntrust.ps); // 交易方向
                    checkAndAssign("#invAim", etrEntrust.invAim); // 投资目的
                    // $("#businessModel").val(etrEntrust.businessModel).trigger('change');
                     checkAndAssign("#ccy", etrEntrust.ccy); // 币种
                    if ($("#tradeAmt").val()) {
                    } else {
                        //委托金额大于cstp的劵面总额
                        if(prinamt != null && etrEntrust.entrustNum>prinamt){
                            checkAndAssign("#tradeAmt",prinamt, true); // 券面总额
                        }else{
                            checkAndAssign("#tradeAmt", etrEntrust.entrustNum, true); // 券面总额
                        }
                    }
                    //中标结果查询页面进来 add by gejiaao start
                    if ($("#bondFirstSign").val()) {
                        checkAndAssign("#tradeAmt", winPrinAmt, true); // 券面总额
                    }
                    //中标结果查询页面进来 add by gejiaao end
                    if ($("#unitCprice").val()) {
                    } else {
                        checkAndAssign("#unitCprice", etrEntrust.price, true); // 净价
                    }

                    checkAndAssign("#calendarId", etrEntrust.calendarId); // 交易日历
                    
                    entrustNum = etrEntrust.entrustNum;
                    entrustPrice = etrEntrust.price;
                    checkAndAssign("#chlId", etrEntrust.chlId);
                    $("#chlId").textSelect('setValue', etrEntrust.chlId); 
                }
                if(data != null && data.etrEntrustAdd != null){
                    var etrEntrustAdd = data.etrEntrustAdd;
                    checkAndAssign("#windtypeone", etrEntrustAdd.assetType1); // 债券一级分类
                    if ($("#exerYield").val()) {
                    } else {
                        checkAndAssign("#exerYield", etrEntrustAdd.exerYield, true); // 行权收益率
                    }
                    if ($("#yield").val()) {
                    } else {
                        checkAndAssign("#yield", etrEntrustAdd.matureYield, true); // 到期收益率
                    }
                    checkAndAssign("#secManageAcctId", etrEntrustAdd.secAcctId); // 币种
                    secManageAcctId = etrEntrustAdd.secAcctId;
                    /*setTimeout(function () {
                        $('#secManageAcctId').textSelect('setValue', etrEntrustAdd.secAcctId);
                    },1000);*/
                    entrustYield = etrEntrustAdd.matureYield;
                    entrustExerYield = etrEntrustAdd.exerYield;

                    $("#integrateTradeAmt").val(etrEntrustAdd.lendinEntNum).trigger('blur');
//                    $("#ownTradeAmt").val(etrEntrustAdd.ourEntNum).trigger('blur');
                    integrateTradeAmtLeft = etrEntrustAdd.lendinEntNum;
                    ownTradeAmtLeft = $("#tradeAmt").val();
                    if (typeof etrEntrustAdd.settleSpreed != "undefined") {
                        $("#settleSpeed").val(etrEntrustAdd.settleSpreed).trigger('change').prop('disabled', true);
                    }
                }
                if (data != null) {
                    // 计算交割日
                    calcSettleDate();
                }
                var bondFirstSign = $("#bondFirstSign").val();
                if (bondFirstSign) {
                    $('#settleSpeed').attr("disabled", false);
                    $('#tradeAmt').attr("disabled", true);
                    checkAndAssign("#yield", yield, false); // 到期收益率
                    $('#calculateField_unitCprice').attr("disabled", false);
                    $('#calculateField_fpriceAmt').attr("disabled", false);
                }

            },false);
         }
         //延时加载，根据交易市场和缴款方式，初始化页面
         setTimeout( function(){
            if($("#tradeMarketMode").val()=="01"){
                //一级缴款,所有费用为0
                if($("#payType").val()=="02"){
                    $("#commissionAmtDiv").hide();
                    $("#settleAmtDiv").hide();
                    $("#exchangeAmtDiv").hide();
                    if(!isTodoList&&pageModel!=PAGE_MODEL_REFERENCE){
                        ClearNum();
                    }
                }

                if($("#tradeMarket").val()!="01"){
                //二级交易，若为交易所债券，显示佣金，经手费和结算费
                    if($("#payType").val()=="03"){
                    	if(!$("#chlId").val()){ 
                        $("#commissionAmtDiv").show();
                    	}
                        $("#settleAmtDiv").show();
                        $("#exchangeAmtDiv").show();
                        if(!isTodoList&&pageModel!=PAGE_MODEL_REFERENCE){
                            ClearNum();
                        }
                    }else if ($("#payType").val()=="02"){
                        $("#commissionAmtDiv").hide();
                        $("#settleAmtDiv").hide();
                        $("#exchangeAmtDiv").hide();
                        ClearNum();
                    }
                    //一级分销，若是交易所，显示佣金
                    if($("#payType").val()=="01"){
                    	if(!$("#chlId").val()){ 
                        $("#commissionAmtDiv").show();
                    	}
                        if(!isTodoList&&pageModel!=PAGE_MODEL_REFERENCE){
                            ClearNum();
                        }
                    }
                }

            }

         },1500);

         //延迟加载，最后给托管账户赋值
         setTimeout( function () {
             $('#secManageAcctId').textSelect('setValue', secManageAcctId);
             //初始计算要素内容
             saveCalcParam = getCalcParam();
             },4000);
        // <!-- 通道交易 新增 强成西 2019/03/26 start -->
        changeTradeAmtAndFee();
        exerYieldDivContrl();
        // <!-- 通道交易 新增 强成西 2019/03/26 start -->
        $('#bussTrdTransfenpei_div').hide();
        // if($('#tradeMarket').val()=='01'){
        //     $('#tradeAmtAndFeeForm').val('').hide();
        // }

    };

    $("#payType").change(function () {
        radioAndPayTypeChange();
    });


    layui.form.on('radio(calculateField)', function(data) {
        layui.form.render(); //更新
        radioAndPayTypeChange();
    });

    var radioAndPayTypeChange = function(){
        if ($("#bondFirstSign").val()) {
            if ($("#payType").val()=='01' || $("#payType").val()=='02'){
                $("#yield").attr('checked',true);
                $('#calculateField_unitCprice').attr("disabled", true);
                $('#calculateField_fpriceAmt').attr("disabled", true);
                $("#unitCprice").val('');
                $("#unitCprice").attr("disabled", true);
                $("#fpriceAmt").val('');
                checkAndAssign("#yield", yield, false); // 到期收益率
                $("#fpriceAmt").attr("disabled", true);
            }
            if ($("#payType").val()=='03'){
                var calculateField = $("[name=calculateField]:radio").next(".layui-form-radioed").prev("input").val();
                $('#calculateField_unitCprice').attr("disabled", false);
                $('#calculateField_fpriceAmt').attr("disabled", false);
                $("[name=calculateField]:radio").next().removeClass('layui-radio-disbaled layui-disabled');
                if (calculateField=='yield'){
                    $("#unitCprice").val('');
                    $("#unitCprice").attr("disabled", true);
                    $("#fpriceAmt").val('');
                    $("#fpriceAmt").attr("disabled", true);
                    checkAndAssign("#yield", yield, false); // 到期收益率
                }

                if (calculateField=='unitCprice'){
                    $("#yield").val('');
                    $("#yield").attr("disabled", true);
                    $("#fpriceAmt").val('');
                    $("#fpriceAmt").attr("disabled", true);
                    $("#unitCprice").val('');
                    $("#unitCprice").attr("disabled", false);
                }

                if (calculateField=='fpriceAmt'){
                    $("#yield").val('');
                    $("#yield").attr("disabled", true);
                    $("#fpriceAmt").val('');
                    $("#fpriceAmt").attr("disabled", false);
                    $("#unitCprice").val('');
                    $("#unitCprice").attr("disabled", true);
                }
            }
        }
    }
    
    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
        	if(typeof(BaseRiskMathComm) != 'undefined') {
            var riskparam = getriskdata();
            if (pageModel==PAGE_MODEL_NEW){
                riskparam.data =null;
            }
            window.BaseRiskMathComm.getparam(riskparam);
        	}
            window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
            	//全价总额不能为空
            	if (!$("#fpriceAmt").val() || typeof $("#fpriceAmt").val() == 'undefined'){
            		Joyin.error(JoyinUtil.getMessage('FW_E_0006',[JoyinUtil.getLabel("TrdBondInvestTrade002.fpriceAmt")]));
            		return null;
            	}
            	if (!$("#bondFirstSign").val() && (!$("#secManageAcctId").val() || typeof $("#secManageAcctId").val() == 'undefined')
               		 && (!$("#chlId").val() || typeof $("#chlId").val() == 'undefined') && !formTodoList){
               		Joyin.error(JoyinUtil.getMessage('FW_E_0006',['证券账户']));
               		return null;
               	}
                if ((!$("#secManageAcctId").val() || typeof $("#secManageAcctId").val() == 'undefined')
                    && (!$("#chlId").val() || typeof $("#chlId").val() == 'undefined') && !formTodoList){
                    Joyin.error(JoyinUtil.getMessage('FW_E_0006',['证券账户']));
                    return null;
                }
                var list;
                if($("#zhilingtype").val()=="02")
            	{
                	list = new Array();
	            	var tmpList = table.getAllRows();
	            	for(var dealObj in tmpList)
	        		{	
	            		var dealinfo = {};
	            		dealinfo.finprodType2 = $("#finprodType2").val();
	            		dealinfo.settleDate = $("#settleDate").val();
	            		dealinfo.tradeDate = $("#tradeDate").val();
	            		dealinfo.ps = $("#ps").val();
	            		dealinfo.ccy = $("#ccy").val();
	            		dealinfo.tradeAmt = tmpList[dealObj].fpriceAmt; // TODO 多组合有通道费
	            		dealinfo.portfolioId = tmpList[dealObj].busiId;
	            		dealinfo.counterId = $("#trdInvestPersonInfo_counterId").val();
	            		dealinfo.tradeMarket = $("#tradeMarket").val();
	            		dealinfo.tradePlat = $("#tradePlat").val();
	            		dealinfo.secManageAcctId = $("#secManageAcctId").val();
                        // 通道交易 新增 强成西 2019/03/26 start
                        if($("#chlId").val()) {
                            dealinfo.chlId = $("#chlId").val();
                        }
	            		// 通道交易 新增 强成西 2019/03/26 end
                        dealObj.tradeMarketMode = $("#tradeMarketMode").val();
	            		list.push(dealinfo);
	        		}
            	} else {
            		list = new Array();
            		var dealObj = {};
            		dealObj.finprodType2 = $("#finprodType2").val();
                    dealObj.settleDate = $("#settleDate").val();
                    dealObj.tradeDate = $("#tradeDate").val();
                    dealObj.ps = $("#ps").val();
                    dealObj.ccy = $("#ccy").val();
                    //-----modify by 裴家俊  2018/04/05----start-----
                    if (null!=$('#tradeMarket').val()&&($('#tradeMarket').val()==TRADE_PLACE_02||$('#tradeMarket').val()==TRADE_PLACE_03)) {
                        if($("#tradeAmtAndFee").val()){
                            dealObj.tradeAmt = $("#tradeAmtAndFee").val();
                        }
                        else{
                            dealObj.tradeAmt = $("#fpriceAmt").val();
                        }
                    }
                    else{
                        dealObj.tradeAmt = $("#fpriceAmt").val();
                    }
                    //-----modify by 裴家俊  2018/04/05----end-----
                    // 通道交易 新增 强成西 2019/03/26 start
                    dealObj.tradeAmt = $("#tradeAmtAndFee").val();
                    // 通道交易 新增 强成西 2019/03/26 end
                    dealObj.portfolioId = $("#portfolioId").val();
                    dealObj.counterId = $("#trdInvestPersonInfo_counterId").val();
                    dealObj.tradeMarket = $("#tradeMarket").val();
                    dealObj.tradePlat = $("#tradePlat").val();
                    if($("#secManageAcctId").val()){
                        dealObj.secManageAcctId = $("#secManageAcctId").val();
                    }else{
                        if(pageModel!=PAGE_MODEL_NEW){
                            dealObj.secManageAcctId = secManageAcctId;
                        }
                    }
                    // 通道交易 新增 强成西 2019/03/26 start
                    if($("#chlId").val()) {
                        dealObj.chlId = $("#chlId").val();
                    }
                    // 通道交易 新增 强成西 2019/03/26 end
                    dealObj.tradeMarketMode = $("#tradeMarketMode").val();
                    list.push(dealObj);
            	}
                
                return {
                    'isTotalTrade':YES_OR_NO_N
                    ,'bankLendingType':null
                    ,'portfolioId':$("#portfolioId").val()
                    ,'deal':list
                };
            }, true);
            if (formTodoList) {
                window.BussTrdTransferOrder && window.BussTrdTransferOrder['bussTrdTransferOrder'] && window.BussTrdTransferOrder['bussTrdTransferOrder'].saveCalcCondition();
            }
//            updateSecAcctBalance();
//            updateCash();
            window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showCash();
            if(entrustId != ''){
                if(etrEntrust){
                    if(etrEntrust.counterId){
                        Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/TrdInvestPersonInfo002/selectCounterId?'+"initValue="+etrEntrust.counterId), {initValue:etrEntrust.counterId}, function (result) {
                            if(result&&result.root&&result.root[0]){
                                etrEntrust.innerTrade= result.root[0].object
                            }
                        },false)
                    }
                    window.TrdInvestPersonInfo002['trdInvestPersonInfo'].setEntrust(function () {
                        return etrEntrust;
                    });
                }
            }
            
            // 触发画面联动处理
            initFlag = true;
            $("#tradeMarket").trigger("change");
            $("#tradeMarketMode").trigger("change", true);
            initFlag = false;
            $("#ps").trigger("change");
            $("#portfolioId").trigger("change", true);
            $("#unitCprice").trigger("change");
            exerYieldDivContrl();
            
            // 设置默认选中净价
            var bondFirstSign = $("#bondFirstSign").val();
            if (!bondFirstSign) {
                $("#calculateField_unitCprice").attr("checked", true);
                layui.form.render('radio');
                $('#payType_div').hide();
            }else {
                $('#settleSpeed').attr("disabled", false);
                $('#tradeAmt').attr("disabled", true);
                checkAndAssign("#yield", yield, false); // 到期收益率
                $("#calculateField_yield").attr("checked", true);
                layui.form.render('radio');
                $('#yield').attr("disabled", true);
            }

            if(calculateField){
                $("#calculateField_" + calculateField).attr("checked", true);
                layui.form.render('radio');
            }
            
          //交易对手改变时  影响通道费金额，内部交易对手随交易支付，外部交易对手根据收取方式
            $('#trdInvestPersonInfo_counterId').on("change",function(){
            	counterIdChangeFee();
            	checkChannelFee();
            	changeTradeAmtAndFee();
                var counterId = $('#trdInvestPersonInfo_counterId');
                if (counterId.select2('data') && counterId.select2('data')[0]) {
                    var 交易对手 =  counterId.select2('data')[0].object;
                    // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                    if (交易对手 == 'N') {
                        if (pageModel != PAGE_MODEL_REFERENCE) {
                            $('#integrateTradeAmt').val(0).prop("disabled", true).trigger("change");
                            $('#ownTradeAmt').val($("#tradeAmt").val()).prop("disabled", true).trigger("change");
                            //$('#settleSpeed').val(0).prop("disabled", true);
                        }
                    } else {
                        if (pageModel != PAGE_MODEL_REFERENCE) {
                            if(isCstpSure){
                                $('#ownTradeAmt').prop("disabled", true);
                                $('#integrateTradeAmt').prop("disabled", true).trigger("change");
                            }else{
                                $('#ownTradeAmt').prop("disabled", false);
                                $('#integrateTradeAmt').prop("disabled", false).trigger("change");
                            }
                            //$('#settleSpeed').prop("disabled", false);
                        }
                    }
                }
            	// 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 start //
                if (IS_SHOW_CPRICE == 'Y') {
                	changeUnitCprice();
                }
                // 净价信息回显  吉林银行产品化需求  2019.12.2 马琳琳 end //
	       if (BANKCODE == '004') {
                	changeTurnInvAim();
                }
            });
            if(pageModel ==PAGE_MODEL_REFERENCE){
            //add by yujiaping 20210304 ZGBSS-592债券交易的交易场所为上交所/深交所，交易市场为一级市场时，审批人的待办事项和完成交易详情中不应展示结算费和经手费字段start
            	var tradeMarket = $('#tradeMarket').val();
                var tradeMarketMode = $('#tradeMarketMode').val();
                if((tradeMarket == "03" || tradeMarket == "04") && tradeMarketMode == "01"){                	
                	$("#settleAmtDiv").hide();
                	$("#exchangeAmtDiv").hide();
                }
            //add by yujiaping 20210304 ZGBSS-592债券交易的交易场所为上交所/深交所，交易市场为一级市场时，审批人的待办事项和完成交易详情中不应展示结算费和经手费字段end                
                if($('#ps').val()=="T01" && (tradeMarket == "03" || tradeMarket == "04")){
                	$("#tradeMarketCash_div").show();
                }else{
                	$("#tradeMarketCash_div").hide();
                }

                // TISHKS-1075:回售也展示佣金、结算费、经手费字段 modify by fuyongsheng 20220505 start
                // if($('#ps').val()=="T03"){
            	// 	$("#settleAmtDiv").hide();
            	// 	$("#exchangeAmtDiv").hide();
            	// 	$("#commissionAmtDiv").hide();
            	// }
                // TISHKS-1075:回售也展示佣金、结算费、经手费字段 modify by fuyongsheng 20220505 end
                if($('#chlId').val()){                		
            		$("#commissionAmtDiv").hide();
            	}
                if(settleSpeedStr=='01'){
                    $('#settleSpeed').val('0');
                }else  if(settleSpeedStr=='02'){
                    $('#settleSpeed').val('1');
                }else if(settleSpeedStr=='03'){
                    $('#settleSpeed').val('2');
                }else{
                    $('#settleSpeed').val(settleSpeedStr);
                }
                if(settleDateText){
                    $('#settleDate').val(settleDateText);
                }
            }
            //通过成交编号录入的交易  交易方向  资产代码置灰  不可编译
            var fTradeIdShow = $('#fTradeIdShow').val();
            var workflowId = $('#workflowId').val();
            if(fTradeIdShow&&workflowId){
                $('#ps').attr('disabled',true);
                $('#finprodMarketId').attr('disabled',true);
            }
        }

        TradeCommon.getSecPositionForSaleAll(setParam(), function (result) {
            if(result){
                if(result.OWN_POSITION){
                    $('#assetOwnAvailablePosition').val(result.OWN_POSITION).trigger("blur");
                }
                if(result.INTEGRATE_POSITION){
                    $('#assetIntegrateAvailablePosition').val(result.INTEGRATE_POSITION).trigger("blur");
                }
                if(result.SHOWHANDS_POSITION){
                    $('#assetIntegrateSaleAvailablePosition').val(result.SHOWHANDS_POSITION).trigger("blur");
                }
            }
        });

        $('#bussTrdAvailableCashQuery_div_channelCash').hide();
    };
    
    var counterIdChangeFee = function (){
    	if (pageModel != PAGE_MODEL_REFERENCE) {
            var counterId = $('#trdInvestPersonInfo_counterId');
            if (counterId.select2('data') && counterId.select2('data')[0]) {
                var 交易对手 =  counterId.select2('data')[0].object;
                // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                if (交易对手 == 'N') {
                        $("#settleServiceAmt").val("").prop("disabled", true).trigger("change");
                        $('#procedureAmt').val('').prop("disabled", true).trigger("change");
                        $('#settleAmt').val('').prop("disabled", true).trigger("change");
                        $('#exchangeAmt').val('').prop("disabled", true).trigger("change");
                        $('#commissionAmt').val('').prop("disabled", true).trigger("change");
                        $('#stampDutyAmt').val('').prop("disabled", true).trigger("change");
                } else {
                    $("#settleServiceAmt").prop("disabled", false).trigger("change");
                	$('#procedureAmt').prop("disabled", false).trigger("change");
                    $('#settleAmt').prop("disabled", false).trigger("change");
                    $('#exchangeAmt').prop("disabled", false).trigger("change");
                    $('#commissionAmt').prop("disabled", false).trigger("change");
                    $('#stampDutyAmt').prop("disabled", false).trigger("change");
                }
            }
            changeTradeAmtAndFee();
    	}
    }
    /**
     * 控制显示行权收益率
     */
    var exerYieldDivContrl = function() {
        // 11、  是否含权债为是，且未来有行权计划时，行权收益率显示，其余情况不显示；
//        if ($("#isRight").val() == YES_OR_NO_Y) {
        var paramObj_isExerYieldDivShow = {};
        paramObj_isExerYieldDivShow.finprodId = $('#finprodId').val();
        paramObj_isExerYieldDivShow.tradeDate = $('#tradeDate').val();
        Joyin.ajaxSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/isExerYieldDivShow', paramObj_isExerYieldDivShow, function (data) {
            if (data == YES_OR_NO_Y) {
                $("#exerYieldDiv").show();
            } else {
                $("#exerYieldDiv").hide();
            }
        });
//        } else {
//            $("#exerYieldDiv").hide();
//        }
    };
    
    var updateSecAcctBalance = function(){
        window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].updateSecAcctBalance(function () {
            return {
                'ccy': $("#ccy").val(),
                'portfolioId': $("#portfolioId").val(),
                'tradeDate':$('#tradeDate').val()
            }
        });
    };

    var updateCash = function(){
        window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].updateCash(function () {
            return {
                'tradeDate': $("#tradeDate").val(),
                'calendarId': $("#calendarId").val(),
                'ccy': $("#ccy").val(),
                'portfolioId': $("#portfolioId").val(),
                'finprodId': $("#finprodId").val(),
                'secAcctId': $("#secManageAcctId").val(),
                'multiParam' : 100
                // 一体化对应etliu start
                , 'invAim': $("#invAim").val()
                // 一体化对应etliu end
                // 通道交易 新增 强成西 2019/03/26 start
                , 'chlId': $("#chlId").val()
                // 通道交易 新增 强成西 2019/03/26 end
            }
        });
    };
    /**
     *
     * 若委托开始日和指令结束日均等于平台日期时：
     * a．若投资目的选择摊余成本类，净价默认带出该债券平台日期前一估值日的摊销净价，可修改；
     */
//    var priceInitByInCpy = function () {
//        // // 委托开始日
//        // var vdate = $("#vdate").val();
//        // // 委托终止日
//        // var mdate = $("#mdate").val();
//        // // 投资目的
//        var invAim = $("#invAim").val();
//        // // 委托开始日或指令结束日不等于平台日期时
//        // if (new Date(platDate).DateDiff('d', vdate) !== 0
//        //     || new Date(platDate).DateDiff('d', mdate) !== 0) {
//        //     return;
//        // }
//        var portfolioId = $('#portfolioId').val();
//        var finprodId = $('#finprodId').val();
//        if (portfolioId && finprodId) {
//            var data = {
//                sysdate: platDate,
//                portfolioId: portfolioId,
//                finprodId:finprodId
//            }
//            //投资目的选择摊余成本
//            if (invAim == 'AC') {
//                Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade002/getLastNetAmortizationPrice', data,
//                    function (result) {
//                        if(result.data){
//                            $('#unitCprice').val(result.data).trigger('blur').trigger('change');
//                        }
//                    });
//            }
//        }
//    }
    /**
     * 币种change事件
     */
    /**
     * 画面联动处理
     * 1.1交易场所
     */
    $('#ccy').on("change", function () {
        controlTradeFee();
    });
     //add by zhangnian 20200213 start
    //控制交易费用
    var controlTradeFee = function(){
        var ccy = $('#ccy').val();
        var tradeMarket = $('#tradeMarket').val();
        if (tradeMarket&&ccy) {
            if(ccy==CCY_CNY){
                $("#feeDiv").show();
                $("#feeAmtDiv").show();
                if (tradeMarket == TRADE_PLACE_01) {
                    // 选择【银行间】时，
                    // 1)交易平台默认为银行间且不可修改；
                    //checkAndAssign("#tradePlat", SHARES_TRADE_PLATFORM_01,true);
                    // 2)需输入银行间成交编号，相关交易要素根据成交编号作为输入参数调用接口自动带出;
                    if(!$("#fTradeId").val()){
                        $("#fTradeId").attr("disabled", false);
                    }
                    $("#fTradeId").associateInput();

                    //----add by 裴家俊  2019/03/28-----
                    $("#settleServiceAmtDiv").show();
                    $("#procedureAmtDiv").show();
                    $("#procedureAmtRateDiv").show();
                    $("#settleAmtDiv").hide();
                    $("#exchangeAmtDiv").hide();
                    $("#settleAmt").val('').trigger("change");
                    $("#exchangeAmt").val('').trigger("change");

                    $("#commissionAmtDiv").hide();
                    $("#stampDutyAmtDiv").hide();
                    $("#commissionAmtRateDiv").hide();

//                $("#tradeAmtAndFeeDiv").hide();
                } else if (tradeMarket == TRADE_PLACE_02 || tradeMarket == TRADE_PLACE_03) {
                    // 选择【上海证券交易所】或【深圳证券交易所】时，
                    // 1)交易平台可自主选择;
                    if(/*$("#entrustId").val() != ''*/$('#tradeMarketMode').val()===SHARES_TRADE_MARKET_01){
                        $("#tradePlat").attr("disabled", true);
                    }else{
                        $("#tradePlat").attr("disabled", false);
                    }
                    // 3)无交易成交单 此项不可填
                    $("#fTradeId").val("").attr("disabled", false);

                    //----add by 裴家俊  2019/03/28-----
                    $("#settleServiceAmtDiv").hide();
                    $("#procedureAmtDiv").hide();
                    $("#procedureAmtRateDiv").hide();

                    //若为二级市场，则显示
                    if($('#tradeMarketMode').val()==SHARES_TRADE_MARKET_02){
                        $("#settleAmtDiv").show();
                        $("#exchangeAmtDiv").show();
                    }
                    if(!$("#chlId").val()){ 
                    $("#commissionAmtDiv").show();
                    }
                    // TISHKS-1075:回售也展示佣金、结算费、经手费字段 modify by fuyongsheng 20220505 start
                    // if($('#ps').val()=="T03"){
                    // 	 $("#settleAmtDiv").hide();
                    //      $("#exchangeAmtDiv").hide();
                    //      $("#commissionAmtDiv").hide();
                    // }
                    // TISHKS-1075:回售也展示佣金、结算费、经手费字段 modify by fuyongsheng 20220505 end
                    $("#commissionAmtRateDiv").show();
                    if($("#type6").val()&&$("#type6").val()==WINDTYPEONE_B17){
                        $("#stampDutyAmtDiv").show();
                    }else{
                        $("#stampDutyAmtDiv").hide();
                    }
//                $("#tradeAmtAndFeeDiv").show();
                    /*var tradeMarketMode = $("#tradeMarketMode").val();
                    if (tradeMarketMode == SHARES_TRADE_MARKET_01) {
                        $("#commissionAmt").prop("disabled", true).val(0).trigger('change');
                    }
                    else {
                        $("#commissionAmt").prop("disabled", false).val(commissionAmt).trigger('change');
                    }*/
                } else {
                    // 1)交易平台可自主选择;
                    // 1)交易平台默认为其它，可改
                    checkAndAssign("#tradePlat", SHARES_TRADE_PLATFORM_99, false);
                    if($("#entrustId").val() != ''||tradeMarket === '05'){
                        $("#tradePlat").attr("disabled", true);
                    }else{
                        $("#tradePlat").attr("disabled", false);
                    }
                    // 3)无交易成交单 此项不可填
                    $("#fTradeId").val("").attr("disabled", true);

                    //----add by 裴家俊  2019/03/28-----
                    $("#settleServiceAmtDiv").show();
                    $("#procedureAmtDiv").show();
                    $("#procedureAmtRateDiv").show();
                    $("#settleAmtDiv").show();
                    $("#exchangeAmtDiv").show();
                    $("#settleAmt").val('').trigger("change");
                    $("#exchangeAmt").val('').trigger("change");

                    $("#commissionAmtDiv").hide();
                    $("#commissionAmtRateDiv").hide();
                    $("#stampDutyAmtDiv").hide();
//                $("#tradeAmtAndFeeDiv").hide();
                }
            }else{
                $("#feeDiv").hide();
                $("#feeAmtDiv").show();
            }
        }
    }
    //add by zhangnian 20200213 start

    //add by lizhi 2020-2-26 划款指令是否自动生成  start
    var generateOrders = function (callback) {
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
        //初始化画面
        init();
        // 在一级市场缴款时才展示，二级市场无需展示
        var tradeMarketMode = $("#tradeMarketMode").val();
        if(tradeMarketMode){
            if(tradeMarketMode==SHARES_TRADE_MARKET_01){
                $("#refundRate_div").show();
                $("#refundAmount_div").show();
                if(null!=parentPageId && parentPageId=='EtrBondBiddingResultEx001' ){
                    $("#integrateFpriceAmt").val('');
                    //mody by meifaxin 20211105 html中判断,这个地方判断导致计算按钮没有效果 start
                    //$("#integrateFpriceAmt_div").hide();
                    //mody by meifaxin 20211105 html中判断,这个地方判断导致计算按钮没有效果 end
                }
            }
            else if(tradeMarketMode==SHARES_TRADE_MARKET_02){
                $("#refundRate").val('');
                $("#refundAmount").val('');
                $("#refundRate_div").hide();
                $("#refundAmount_div").hide();
            }
        }
        // 是否显示划款指令
        if(GEN_PAY_ORDERS_AUTO=='01'){
        	$('#bussTrdTransferOrder_div').hide() ;
        }else{
        	$('#bussTrdTransferOrder_div').show() ;
        }
        // 绑定按钮事件
        $('#TrdBondInvestTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdBondInvestTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
//        $('#TrdBondInvestTrade002_btn_risk').click(function() {
//            Joyin.dialog(JoyinUtil.getLabel("BaseRiskMathComm"),
//                    basePath + "business/common/BaseRiskMathComm/init",
//                    "65%", "80%",
//                    { pageModel: PAGE_MODEL_REFERENCE });
//        });
      //风险试算
        $('#TrdBondInvestTrade002_btn_risk').click(function(){
            //window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 start----------
            var object = $.serializeToObject('#submitForm', true);
            object.tradeAmt = JoyinMath.doMul( $("#tradeAmt").val()?$("#tradeAmt").val():0,unitNum,2,JoyinMath.ROUND_HALF_UP);
         // add by etliu 20210129 start
            // 选择的成交编号存在对应的报价，则不影响风险
            if (object.fTradeId) {
                Joyin.ajaxSync(basePath + 'invest/trade/TrdCommonController/checkExecIdHasQuote', {'execId': object.fTradeId}, function (result) {
                    if (result == 'Y') {
                        object.isSkipRisk = 'Y';
                        object.isSkipRiskMsg = '选择的成交编号存在对应的报价，本次交易不影响风险';
                    }
                });
            }
         // add by etliu 20210129 end
            
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/1/RSK002/F01',object,getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 end----------
            // add by etliu 20210129 start
            if (object.isSkipRisk != 'Y') {
                window.BaseRiskMathComm.initBaseRiskMathCommPageCus(getriskdata());
            }
            // add by etliu 20210129 end

        })
        if($("#finprodId").val()){
        	$("#finprodId").trigger("change");
        }
        if(isCstp&&entrustId&&fTradeId){
            fTradeIdChangeAction();
        }
        if(isCstp&&preTradeId&&fTradeId){
            fTradeIdChangeAction();
        }
        /* edit by songyawei 理财资产管理系统BS2.1.3 BS-758 start */
//         if ($("#turnInvAim").val()) {
//                $("#turnInvAim_div").hide();
//             }
        /* edit by songyawei 理财资产管理系统BS2.1.3 BS-758 end */
         if(preTradeId){
             $('#baseRiskMathComm').hide();
             $('#TrdBondInvestTrade002_btn_risk').hide();
         }

        /**
         * 1.7计算按钮点击事件
         */
        $('#TrdBondInvestTrade002_btn_calculate').click(function() {
            var paramObj = getCalcParam();
            // if (SHOW_INVAIM_OR_BUSSINSSMODEL=='02'){
            //     //根据业务模式与投资目的对应关系对投资目的赋值，不改变原来计算逻辑
            //     if($('#businessModel').val()=="Y1"){
            //         paramObj.invAim = "AC";
            //     }else if($('#businessModel').val()=="Y2"){
            //         paramObj.invAim = "FVTPL"
            //     }else {
            //         paramObj.invAim = "";
            //     }
            // }


            Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/calculateData'), paramObj, function (result) {
                if (result && result.data) {
                    var data = result.data;
                    if(!$('#fTradeId').val()){
                        $("#tradeAmt").val(JoyinMath.doDiv(data.tradeAmt,unitNum,4,JoyinMath.ROUND_HALF_UP)).change().trigger("blur");
                    }
                    $("#unitCprice").val(data.unitCprice).trigger("blur");
                    $("#unitInt").val(data.unitInt).trigger("blur");
                    $("#unitFprice").val(data.unitFprice).trigger("blur");
                    $("#cpriceAmt").val(data.cpriceAmt).trigger("blur");
                    $("#integrateCpriceAmt").val(data.integrateCpriceAmt).trigger("blur");
                    $("#intAmt").val(data.intAmt).trigger("blur");
                    $("#integrateIntAmt").val(data.integrateIntAmt).trigger("blur");
                    $("#ownIntAmt").val(JoyinMath.doMinus(data.intAmt, data.integrateIntAmt, 2, JoyinMath.ROUND_HALF_UP)).trigger("blur");
                    if($("#zhilingtype").val()=="02"){
                        for(var i=0;i<table.getAllRows().length;i++){
                            if($("#unitFprice").val()){
                                //获取i行的券面总额
                                //全价总额
                                var fpriceAmtSum=Number(0);
                                for(var i=0;i<table.getAllRows().length;i++){
                                    //券面总额
                                    var tradeAmt=table.getAllRows()[i].tradeAmt;
                                    table.renderRow(i, {"fpriceAmt": Number($("#unitFprice").val())*Number(tradeAmt)/100});
                                    fpriceAmtSum=Number(fpriceAmtSum)+Number(Number($("#unitFprice").val())*Number(tradeAmt)/100);
                                }
                                $("#fpriceAmt").val(fpriceAmtSum).trigger('change');
                            }else{
                                table.renderRow(i, {"fpriceAmt": 0});
                            }

                            if($("#unitInt").val()){
                                //应计利息总额
                                var unitIntSum=Number(0);
                                for(var i=0;i<table.getAllRows().length;i++){
                                    //券面总额
                                    var tradeAmt=table.getAllRows()[i].tradeAmt;
                                    table.renderRow(i, {"intAmt": Number($("#unitInt").val())*Number(tradeAmt)/100});
                                    unitIntSum=Number(unitIntSum)+Number(Number($("#unitInt").val())*Number(tradeAmt)/100);
                                }
                            }else{
                                table.renderRow(i, {"intAmt": 0});
                            }

                        }
                    }else{
//                 	$("#fpriceAmt").val(Number($("#intAmt").val())+Number($("#cpriceAmt").val())).trigger("blur").trigger('change');
                        // $("#fpriceAmt").val(data.fpriceAmt).trigger("blur").trigger('change');
                        var calculateField =  $("[name=calculateField]:checked").val();
                        if(calculateField=='fpriceAmt'){
                            var intAmt = JoyinMath.doMinus(data.fpriceAmt,data.cpriceAmt,2);
                            $("#intAmt").val(intAmt).trigger("blur");
                            $("#fpriceAmt").val(data.fpriceAmt).trigger("blur").trigger('change');
                            var integrateIntAmt = JoyinMath.doMinus(data.integrateFpriceAmt,data.integrateCpriceAmt,2);
                            $("#integrateIntAmt").val(integrateIntAmt).trigger("blur");
                            $("#integrateFpriceAmt").val(data.integrateFpriceAmt).trigger("blur").trigger('change');

                            $("#ownIntAmt").val(JoyinMath.doMinus(intAmt, integrateIntAmt, 2, JoyinMath.ROUND_HALF_UP)).trigger("blur");
                            $("#ownFpriceAmt").val(JoyinMath.doMinus(data.fpriceAmt, data.integrateFpriceAmt, 2, JoyinMath.ROUND_HALF_UP)).trigger("blur");
                        }
                        else{
                            $("#fpriceAmt").val(Number($("#intAmt").val())+Number($("#cpriceAmt").val())).trigger("blur").trigger('change');
                            $("#integrateFpriceAmt").val(Number($("#integrateIntAmt").val())+Number($("#integrateCpriceAmt").val())).trigger("blur").trigger('change');
                            $("#ownFpriceAmt").val(JoyinMath.doMinus($("#fpriceAmt").val(), $("#integrateFpriceAmt").val(), 2, JoyinMath.ROUND_HALF_UP)).trigger("blur");
                        }
                    }
                    $("#exerYield").val(data.exerYield).trigger("blur");
                    $("#yield").val(data.yield).trigger("blur");
                    // 记录隐藏值
                    $("#unitCprice_calc").val(data.unitCprice);
                    $("#unitInt_calc").val(data.unitInt);
                    $("#unitIntCalc").val(data.unitInt);
                    $("#unitFprice_calc").val(data.unitFprice);
                    $("#cpriceAmt_calc").val(data.cpriceAmt);
                    $("#intAmt_calc").val(data.intAmt);
                    $("#fpriceAmt_calc").val(data.fpriceAmt);
                    $("#exerYield_calc").val(data.exerYield);
                    $("#yield_calc").val(data.yield);
                    saveCalcParam = getCalcParam();
                }

                //---add by 裴家俊  费用设置  2019/03/28----start--------
                //$("#tradeAmtAndFee").val('');//交易金额置空
                // $("#commissionAmt").val('').trigger("blur");
                // $("#procedureAmt").val('').trigger("blur");
                // $("#settleAmt").val('').trigger("blur");
                if(data.tradeFeeList&&data.tradeFeeList.length>0){
                    // 交易手续费条数
                    var cntP = 0;
                    // 结算服务费条数
                    var cntSS = 0;
                    // 结算费条数
                    var cntS = 0;
                    // 经手费条数
                    var cntM = 0;
                    // 佣金条数
                    var cntC = 0;
                    // 印花税条数
                    var cntST = 0;
                    $.each(data.tradeFeeList,function(i,v){
                        if(v.feeType==FEE_TYPE_05&&v.feeAmt){
                            cntC++;
                            isPayWithTrade = v.isPayWithTrade;
                            if(isPayWithTrade){
                                // add by wangguoxian 费用设置 20190808 start
                                $("#isPayWithTrade").val(isPayWithTrade);
                                // add by wangguoxian 费用设置 20190808 end
                            }
                            commissionAmt = v.feeAmt;
                            $("#commissionAmtRate").val(v.feeRate).trigger("blur");
                            var feeAmt = v.feeAmt;
                            var tradeMarket = $('#tradeMarket').val();
                            /*if (tradeMarket == TRADE_PLACE_02 || tradeMarket == TRADE_PLACE_03) {
                                var tradeMarketMode = $("#tradeMarketMode").val();
                                if (tradeMarketMode == SHARES_TRADE_MARKET_01) {
                                    $("#commissionAmt").val(0).prop('disabled', true).trigger("change");
                                    feeAmt = 0;
                                }
                            }*/
                            if (feeAmt) {
                                $("#commissionAmt").val(v.feeAmt).prop('disabled', false).trigger("blur").trigger("change");
                            }
                            // modified by wangguoxian 费用设置 20190808 start
                            changeTradeAmtAndFee();
                            /*if (isPayWithTrade == YES_OR_NO_Y) {

                                if($("#ps").val()==PS_T01){
                                    $("#tradeAmtAndFee").val(Number(data.fpriceAmt)+Number(feeAmt)).trigger("blur");
                                }
                                if($("#ps").val()==PS_T02){
                                    $("#tradeAmtAndFee").val(Number(data.fpriceAmt)-Number(feeAmt)).trigger("blur");
                                }
                            }else{
                                $("#tradeAmtAndFee").val(Number(data.fpriceAmt)).trigger("blur");
                            }*/
                            // modified by wangguoxian 费用设置 20190808 end
                        }
                        if(v.feeType==FEE_TYPE_02&&v.feeAmt){
                            cntST++;
                            stampDutyIsPayWithTrade = v.isPayWithTrade;
                            $("#stampDutyIsPayWithTrade").val(stampDutyIsPayWithTrade);
                            stampDutyAmt = v.feeAmt;
                            var feeAmt = v.feeAmt;
                            var tradeMarket = $('#tradeMarket').val();
                            if (feeAmt) {
                                $("#stampDutyAmt").val(v.feeAmt).prop('disabled', false).trigger("blur").trigger("change");
                            }
                            changeTradeAmtAndFee();
                        }

                        if(v.feeType==FEE_TYPE_06&&v.feeAmt){
                            cntP++;
                            $("#procedureAmt").val(v.feeAmt).trigger("blur");
                            if(procedureAmt_f){
                                $('#procedureAmt').val(procedureAmt_f);
                            }
                            $("#procedureAmtRate").val(v.feeRate).trigger("blur");
                        }

                        if(v.feeType=='B08' && v.feeAmt){
                            cntSS++;
                            $("#settleServiceAmt").val(v.feeAmt).trigger("blur");
                            if(settleAmt_f){
                                $('#settleServiceAmt').val(settleAmt_f);
                            }
                        }

                        //加入结算费经手费
                        if(v.feeType==FEE_TYPE_07&&v.feeAmt){
                            cntS++;
                            settleIsPayWithTrade = v.isPayWithTrade;
                            $("#settleIsPayWithTrade").val(settleIsPayWithTrade);
                            $("#settleAmt").val(v.feeAmt).trigger("blur");
                        }
                        if(v.feeType==FEE_TYPE_01&&v.feeAmt){
                            cntM++;
                            exchangeIsPayWithTrade = v.isPayWithTrade;
                            $("#exchangeIsPayWithTrade").val(exchangeIsPayWithTrade);
                            $("#exchangeAmt").val(v.feeAmt).trigger("blur");
                        }

                        /*if(v.feeType==FEE_TYPE_04&&v.feeAmt){
                            $("#transferAmt").val(v.feeAmt).trigger("blur");
                        }*/
                    });
                    // 按照最新费用规则计算相关费用无结果则清空相关费用
                    if(cntP == 0){
                        $("#procedureAmt").val("").trigger("blur");
                    }
                    if(cntSS == 0){
                        $("#settleServiceAmt").val("").trigger("blur");
                    }
                    if(cntS == 0){
                        //结算费置空
                        $("#settleAmt").val("").trigger("blur");
                    }
                    if(cntM == 0){
                        //经手费置空
                        $("#exchangeAmt").val("").trigger("blur");
                    }
                    if(cntC == 0){
                        $("#commissionAmt").val("").trigger("blur");
                    }
                    if(cntST == 0){
                        $("#stampDutyAmt").val("").trigger("blur");
                    }
                }
                else{
                    $("#exchangeAmt").val("").trigger("blur");
                    $("#commissionAmt").val("").trigger("change").trigger("blur");
                    $("#stampDutyAmt").val("").trigger("change").trigger("blur");
                    $("#procedureAmt").val("").trigger("blur");
                    $("#settleServiceAmt").val("").trigger("blur");
                    $("#settleAmt").val("").trigger("blur");
                    $("#procedureAmtRate").val("").trigger("blur");
                    $("#commissionAmtRate").val("").trigger("blur");
                    $("#transferAmt").val("").trigger("blur");
                }

                //一级缴款,所有费用为0
                if($("#payType").val()=="02"){
                    $("#commissionAmt").val('').trigger("blur");
                    $("#settleAmt").val('').trigger("blur");
                    $("#exchangeAmt").val('').trigger("blur");
                    $("#stampDutyAmt").val('').trigger("blur");
                    $("#procedureAmt").val('').trigger("blur");
                    $("#settleServiceAmt").val("").trigger("blur");
                }

                counterIdChangeFee();
                // 将甲类户对应的交易手续费、结算费、佣金设置为不可编辑
                // controllFeeItem();
                //---add by 裴家俊  费用设置  2019/03/28----end--------
            },false);
        });
    });

    var updateBussTrdTransfenpeiTable =function(){
    	for(var i=0;i<table.getAllRows().length;i++){
    		if($("#unitCprice").val()){
    			//获取i行的券面总额
        		var tradeAmt=table.getAllRows()[i].tradeAmt;
        		table.renderRow(i, {"cpriceAmt": Number($("#unitCprice").val())*Number(tradeAmt)/100});
    		}else{
    			table.renderRow(i, {"cpriceAmt": 0});
    		}
    	}
    };
    
    // 指令类型下拉框change事件处理
	$('#zhilingtype').change(function(){
		zhilingtypeChangeAction();
	});
	
	var zhilingtypeChangeAction = function(){
		
		$("#portfolioId").attr("disabled", false);
		$('#bussTrdAvailableCashQuery_div_cash').show();
		$("#tradeAmt").attr("disabled", false);
		$("#fpriceAmt").attr("disabled", false);
		
		if($("#zhilingtype").val()=="02"){
			//组合分配页面展示
			$('#bussTrdTransfenpei_div').show();
			//委托代码置灰
			$("#portfolioId").attr("disabled", true);
			$("#portfolioId ").empty();
			window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].hideCash(true);
			$("#tradeAmt").attr("disabled", true);
			//多组合时全价总额自动计算
			$("#fpriceAmt").attr("disabled", true);
			if(!$("#bussTrdTransfenpeiTable").data('joyintable')){
				table = $("#bussTrdTransfenpeiTable").joyintable({
			          data:[],
			          searchOnInit:true,
			          height:'225px',
			          cols:[
			                {field:'busiId', title: 'TrdInvestFenPeiInfo.busiId',sortable:false, edit:true, editOption:{type:'select',formatoption:true,parentIds:'secManageAcctId',url: JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/getPortfolioId')}}
			               ,{field:'investaim', title: 'TrdInvestFenPeiInfo.investaim',edit:true, sortable:false,editOption:{type:'select',formatoption:true,result: INV_AIM}}
			               ,{field:'tradeAmt', title: 'TrdInvestFenPeiInfo.tradeAmt',edit:true, sortable:false,type:'number'}
			               ,{field:'cpriceAmt', title: 'TrdInvestFenPeiInfo.cpriceAmt',sortable:false}
			               ,{field:'intAmt', title: 'TrdInvestFenPeiInfo.intAmt',sortable:false}
			               ,{field:'fpriceAmt', title: 'TrdInvestFenPeiInfo.fpriceAmt',sortable:false}
			               ,{field:'tgSurpluscash', title: 'TrdInvestFenPeiInfo.tgSurpluscash',sortable:false}
			               ,{field:'bussTrdAvailableCashQueryavalibleCash', title: 'TrdInvestFenPeiInfo.bussTrdAvailableCashQueryavalibleCash',sortable:false}
			               ,{field:'bussTrdAvailableCashQueryavalibleCash1', title: 'TrdInvestFenPeiInfo.bussTrdAvailableCashQueryavalibleCash1',sortable:false}
			               ,{field:'assetsavailablepositions', title: 'TrdInvestFenPeiInfo.assetsavailablepositions',sortable:false}
			               ,{field:'operation', title: 'SysCommon.operation',sortable:false, type:'action', buttons: ['delt']}
			           ],
			           addbtn:true,
			           done:function(){
			        	   this.bind('click', '.delt', function(data, index) {
			                   var object = {selectedItem: [data], operateType:"delete"};
			                   table.deleteRow(index);
			                   //券面总额
		                	   var tradeAmtSum=Number(0);
		                	   //全价总额
		                	   var fpriceAmtSum=Number(0);
			                   for(var i=0;i<table.getAllRows().length;i++){
			                	   var tradeAmt=table.getAllRows()[i].tradeAmt;
			                	   var fpriceAmt=table.getAllRows()[i].fpriceAmt;
				        		   tradeAmtSum=Number(tradeAmtSum)+Number(tradeAmt);
				        		   fpriceAmtSum=Number(fpriceAmtSum)+Number(fpriceAmt);
				        		   $("#tradeAmt").val(tradeAmtSum).change();
				        		   $("#fpriceAmt").val(fpriceAmtSum).trigger('change');
				        	   }
			               });
			           },
			           editCell: function(value, data, field, cell, column){

			        	   var params={};
        				   table.renderRow(data.LAY_TABLE_INDEX,  {"investaim": $("#invAim").val()});
			        	   if("busiId"==field && "T01"==$("#ps").val()){
			        		   table.renderRow(data.LAY_TABLE_INDEX, {"assetsavailablepositions":''})
			        		   if(value && value.trim() 
			                        && $("#calendarId").val() && $("#ccy").val() && $("#tradeDate").val()){
			            	   
			        			   params.portfolioId=value;
			        			   params.calendarId=$("#calendarId").val();
			        			   params.tradeDate=$("#tradeDate").val();
			        			   params.acctTypeList = ACCT_TYPE_A01 + "," + ACCT_TYPE_A07;
			        			   params.ccy=$("#ccy").val();
			        			   Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdAvailableCashQuery/getAvailableCash'), params, function (cashdata) {
			        				   table.renderRow(data.LAY_TABLE_INDEX, {"bussTrdAvailableCashQueryavalibleCash": cashdata.T_0});
			        				   table.renderRow(data.LAY_TABLE_INDEX, {"bussTrdAvailableCashQueryavalibleCash1": cashdata.T_1});
			        			   },false);
			        			   //托管户剩余现金
			        			   if($("#ccy").val() && value && value.trim()) {
			        				   params.portfolioId=value;
			        				   params.acctType = ACCT_TYPE_A01;
			        		            Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdAvailableCashQuery/getSecManageAvailableCash'), params, function (balance) {
			        		            	table.renderRow(data.LAY_TABLE_INDEX, {"tgSurpluscash": balance});
			        		            },false);
			        		        }
			        		   }
			        	   }else if("busiId"==field && "T01"!=$("#ps").val()){
			        		   table.renderRow(data.LAY_TABLE_INDEX, {"bussTrdAvailableCashQueryavalibleCash": ''});
			        		   table.renderRow(data.LAY_TABLE_INDEX, {"bussTrdAvailableCashQueryavalibleCash1": ''});
			        		   table.renderRow(data.LAY_TABLE_INDEX, {"tgSurpluscash": ''});
			        		   //交易方向为卖出时显示资产可用持仓
			        		   if(value && value.trim() 
				                        && $("#calendarId").val() && $("#ccy").val() && $("#tradeDate").val()){
			        			   		
			        			   		params.portfolioId=value;
			        			   		params.finprodId=$("#finprodId").val();
			        			   		params.calendarId=$("#calendarId").val();
			        			   		params.tradeDate=$("#tradeDate").val();
			        			   		params.acctTypeList = ACCT_TYPE_A01 + "," + ACCT_TYPE_A07;
			        			   		params.ccy=$("#ccy").val();
			        			   		Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdAvailableCashQuery/getAvailableCash'), params, function (cashdata) {
			        			   			table.renderRow(data.LAY_TABLE_INDEX, {"assetsavailablepositions": cashdata.position});
			        			   		},false);
			        		   }
			        	   }
			        	   
			        	   //只有券面总额发生变化才需要更新净价总额、应计利息总额
			        	   if("tradeAmt"==field){
			        		   //净价总额=券面总额*净价/100
				        	   if($("#unitCprice").val()){
				        		   table.renderRow(data.LAY_TABLE_INDEX, {"cpriceAmt": $("#unitCprice").val()*value/100});
				        	   }
				        	   //应计利息总额=券面总额*应计利息/100
				        	   if($("#unitInt").val()){
				        		   table.renderRow(data.LAY_TABLE_INDEX, {"intAmt": $("#unitInt").val()*value/100});
				        	   }
				        	   //全价总额=券面总额*全价/100
//				        	   if($("#unitFprice").val()){
//				        		   table.renderRow(data.LAY_TABLE_INDEX, {"fpriceAmt": Number($("#unitFprice").val())*value/100});
//				        	   }
			        		   //券面总额
			        		   var tradeAmtSum=Number(0);
			        		   for(var i=0;i<table.getAllRows().length;i++){
				        		   var tradeAmt=table.getAllRows()[i].tradeAmt;
				        		   tradeAmtSum=Number(tradeAmtSum)+Number(tradeAmt);
				        	   }
				        	   $("#tradeAmt").val(tradeAmtSum).change();
			        	   }
			           }
				});
			}
	  }else{
		  $('#bussTrdTransfenpei_div').hide();
	  }
	};


    $('#fTradeId').on("change",function(){
        if (pageModel==PAGE_MODEL_NEW&&!fTradeId) {
            fTradeIdChangeAction();
            // 中标结果维护画面跳转过来，不重新load画面
            if($("#fTradeId").val()=='' && $("#bondFirstSign").val() == '' &&fTradeIdInitFlag!=0){
                Joyin.ajaxSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/init', {entrustId:$('#entrustId').val(),portfolioId:$('#portfolioId').val()}, function (data) {
                    $("body").html(data);
                });
            }else{
            	fTradeIdInitFlag =1;
            }
        }
    });
    /**
     * 银行间成交编号输入时从cstp查询数据带出
     */
    var fTradeIdChangeAction = function(){
        var fTradeId = $('#fTradeId').val();

        $('#finprodId').attr('disabled', false);
        // $('#portfolioId').attr('disabled', false);
        $('#ps').attr('disabled', false);
        $("#settleSpeed").attr('disabled', false);
        $("#tradeMarketMode").attr('disabled', false);

        // 设置默认选中净价
        $("#calculateField_unitCprice").attr("checked", true);
        layui.form.render('radio');

        $('#unitCprice').attr('disabled', false);
        $('#cpriceAmt').attr('disabled', false);
        $('#intAmt').attr('disabled', false);
        $('#tradeAmt').attr('disabled', false);
        $('#fpriceAmt').attr('disabled', false);
        $('#yield').attr('disabled', false);
        $('#trdInvestPersonInfo_counterTrader').attr('disabled', false);
        $('#trdInvestPersonInfo_counterId').attr('disabled', false);

        if (fTradeId && fTradeId != ''){
            Joyin.ajaxSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/querySecCstpInfoByNo', {fedealno : fTradeId,entrustId:entrustId,preTradeId:preTradeId,isCstpSure:isCstpSure}, function(result){
                if (result){
                	//投资目的不可用
                	$("#invAim").attr('disabled', true);
                	//净价全价总额不可选
                	$("#calculateField_unitCprice").attr('disabled', true);
                	$("#calculateField_fpriceAmt").attr('disabled', true);
                	$("#calculateField_exerYield").attr('disabled', true);
                	$("#calculateField_yield").attr('disabled', true);
                	//是否有报价
                	
                	if(result.isOfferFlag && result.isOfferFlag != ''){
                		$('#isOfferFlag').val(result.isOfferFlag);
                	}else{
                		$('#isOfferFlag').val('');
                	}
                    if (result.secid && result.secid != ''){
                        deleteSelect('finprodId');
                        $('#finprodId').textSelect('setValue', result.secid);//债券代码;
                        if($('#finprodId').val()){
                            $('#finprodId').attr('disabled', true);
                        }
                        // 交易市场
                        $('#tradeMarketMode').val(SHARES_TRADE_MARKET_02).trigger('change');
                        if($('#tradeMarketMode').val()){
                            $('#tradeMarketMode').attr('disabled', true);
                        }
                    }else{
                        $('#finprodName').val('');
                        deleteSelect('tradeMarket');
                        deleteSelect('tradeMarketMode');
                        deleteSelect('tradePlat');
                        deleteSelect('type9');
                        deleteSelect('type10');
                        deleteSelect('type11');
                        deleteSelect('finprodId');
                        $('#finprodId').attr('disabled', false);
                        $('#tradeMarketMode').attr('disabled', false);
                        $('#tradePlat').attr('disabled', false);
                    }
                   if (result.profolioId && result.profolioId != ''){
                       $('#portfolioId').textSelect('setValue', result.profolioId);//组合代码
                       if($('#portfolioId').val()){
                           $('#portfolioId').attr('disabled', true);
                       }
                   }
                    if (result.ourdepositaccountnumber && result.ourdepositaccountnumber != ''){
                        $('#secManageAcctId').textSelect('setValue', result.ourdepositaccountnumber);//证券账户
                        if($('#secManageAcctId').val()){
                            $('#secManageAcctId').attr('disabled', true);
                        }
                    }else{
                        deleteSelect('secManageAcctId');
                        $('#secManageAcctId').attr('disabled', false);
                    }
                    if (result.ccy && result.ccy != ''){
                        $('#ccy').val(result.ccy).trigger('change');//币种代码
                        if($('#ccy').val()){
                            $('#ccy').attr('disabled', true);
                        }
                    }else{
                        $('#ccy').val('');
                        $('#ccy').attr('disabled', false);
                    }
                    // 投资类型
                    if (result.ps && result.ps != ''){
                        $('#ps').val(result.ps).trigger('change');// 买卖标识
                        if($('#ps').val()){
                            $('#ps').attr('disabled', true);
                        }
                    }else{
                        deleteSelect('ps');
                        $('#ps').attr('disabled', false);
                    }
                    if (result.price8 && result.price8 != ''){
                        $('#unitCprice').val(result.price8).trigger('change');// 价格
                        if($('#unitCprice').val()){
                            $('#unitCprice').attr('disabled', true);
                        }
                    }else{
                        $('#unitCprice').val('');
                        $('#unitCprice').attr('disabled', false);
                    }
                    if (result.yield8 && result.yield8 != ''){
                        var number =  result.yield8*100;
                        $('#yield').val(number).trigger('change');// 收益率
                        if($('#yield').val()){
                            $('#yield').attr('disabled', true);
                        }
                    }else{
                        $('#yield').val('');
                        $('#yield').attr('disabled', false);
                    }
                    // 交易对手号代码
                    // 票值
                    if (result.prinamt && result.prinamt != ''){
                        $('#tradeAmt').val(JoyinMath.doDiv(result.prinamt,unitNum,2,JoyinMath.ROUND_HALF_UP)).trigger('blur').trigger('change');// 本金金额(券面总额)
                        if($('#tradeAmt').val()){
                            $('#tradeAmt').attr('disabled', true); //成交编号以后，页面的券面总额被置灰，期望可以修改
                            if(isCstpSure){
                                $('#ownTradeAmt').attr('disabled', true);
                                $('#integrateTradeAmt').attr('disabled', true);
                            }
                        }
                    }else{
                        $('#tradeAmt').val('').change();
                        $('#tradeAmt').attr('disabled', false);
                    }
                    if($('#tradeAmt').val()){
                    	$("#TrdBondInvestTrade002_btn_calculate").click();
                    }
//                     if (result.settamt && result.settamt != ''){
//                         $('#fpriceAmt').val(result.settamt).trigger('blur').trigger('change');// 结算金额(全价总额)
//                         if($('#fpriceAmt').val()){
//                             $('#fpriceAmt').attr('disabled', true);
//                         }
//                     }else{
//                         $('#fpriceAmt').val('');
//                         $('#fpriceAmt').attr('disabled', false);
//                     }
//                     // 产品
//                     // 业务类型
//                     // 交易员
//                     if (result.convintamt && result.convintamt != ''){
//                         $('#intAmt').val(result.convintamt).trigger('blur').trigger('change');// 转换利息金额
//                         if($('#intAmt').val()){
//                             $('#intAmt').attr('disabled', true);
//                         }
//                     }else{
//                         $('#intAmt').val('');
//                         $('#intAmt').attr('disabled', false);
//                     }
                    if (result.invAim && result.invAim != ''){
                        $('#invAim').val(result.invAim).trigger('change');// 投资目的
                        if($('#invAim').val()){
                            $('#invAim').attr('disabled', true);
                        }
                    }
                    else{
                        $('#invAim').val('').trigger('change');
                        $('#invAim').attr('disabled', false);
                    }
                    // 有效状态
                    // 经纪人
                    // 录入日期
                    if (result.dealDateStr){
                        $('#tradeDate').val(result.dealDateStr).trigger('change');// 交易日期
                        if($('#tradeDate').val()){
                            $('#tradeDate').attr('disabled', true);
                        }
                    }else{
                        $('#tradeDate').val($('#formatDate').val());
                        $('#tradeDate').attr('disabled', true);
                    }

                    if (result.settDateStr){
                        $('#settleDate').val(result.settDateStr).trigger('change');// 交割日
                        if($('#settleDate').val()){
                            $('#settleDate').attr('disabled', true);
                        }
                    }else{
                        $('#settleDate').val('');
                        $('#settleDate').attr('disabled', true);
                    }
                    // 冲销日期
                    // 更新的次数
                    // 原始交易编号
                    if (result.dealtext && result.dealtext != ''){
                        $('textarea[name="remark"]').val(result.dealtext).trigger('change')
                        // $('#remark').val(result.dealtext).trigger('change');// 备注
                    }else{
                        $('textarea[name="remark"]').val('');
                    }
                    // 更新时间
                    setTimeout(function () {
                        if (result.cno && result.cno != ''){
                            $('#trdInvestPersonInfo_counterId').textSelect('setValue', result.cno);// 交易对手
                            if($('#trdInvestPersonInfo_counterId').val()){
                                $('#trdInvestPersonInfo_counterId').attr('disabled', true);
                            }
                        }else{
                            deleteSelect('trdInvestPersonInfo_counterId');
                        }
                        if (result.clienttrader && result.clienttrader != ''){
                            $('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').textSelect('setValue',result.clienttrader);// 对手方交易员
                            if($('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').val()){
                                $('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').attr('disabled', true);
                            }
                        }else{
                            $('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').val('');
                            $('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').attr('disabled', false);
                        }
                        if (result.countertelephone && result.countertelephone != ''){
                            $('#trdInvestPersonInfo_contactInfo').val(result.countertelephone);// 联系方式
                            if($('#trdInvestPersonInfo_contactInfo').val()){
                                $('#trdInvestPersonInfo_contactInfo').attr('disabled', true);
                            }
                        }else{
                            $('#trdInvestPersonInfo_contactInfo').val('');
                            $('#trdInvestPersonInfo_contactInfo').attr('disabled', false);
                        }
                        //本方交易员
                        if (result.trad && result.trad != '' && $('#isOfferFlag').val()=='01'){
                            $('#trdInvestPersonInfo_trader').textSelect('setValue',result.trad);
                        }else{
                            $('#trdInvestPersonInfo_trader').textSelect('setValue',userId);
                        }
                    },1000);
                    // 根据CSTP金额信息计算其他要素
                    // 清算速度
                    if (result.settleSpeed){
                        var cstpSettleSpeed='';
                        if(result.settleSpeed=='1'||result.settleSpeed=='01'){
                            cstpSettleSpeed='0';
                        }else if(result.settleSpeed=='2'||result.settleSpeed=='02'){
                            cstpSettleSpeed='1';
                        }else if(result.settleSpeed=='3'||result.settleSpeed=='03'){
                            cstpSettleSpeed='2';
                        }
                        $("#settleSpeed").val(cstpSettleSpeed).trigger('change');
                        $('#settleSpeed').attr('disabled', true);
                    }else{
                        deleteSelect('settleSpeed');
                        $('#settleSpeed').trigger('change');
                        $('#settleSpeed').attr('disabled', false);
                        $('#settleDate').val('');
                    }
                    // 单位应计利息
                    if (result.accruedinterestamt||result.unitInt){
                        if(result.accruedinterestamt){
                            $('#unitInt').val(result.accruedinterestamt).trigger('blur').trigger('change');
                        }
                        if(result.unitInt){
                            $('#unitInt').val(result.unitInt).trigger('blur').trigger('change');
                        }
                        if($('#unitInt').val()){
                            $('#unitInt').attr('disabled', true);
                        }
                    }else {
                        $('#unitInt').val('');
                        $('#unitInt').attr('disabled', false);
                    }
                    // 全价
                    if (result.dirtyprice && result.dirtyprice != ''){
                        $('#unitFprice').val(result.dirtyprice).trigger('blur').trigger('change');
                        if($('#unitFprice').val()){
                            $('#unitFprice').attr('disabled', true);
                        }
                    }else{
                        $('#unitFprice').val();
                        $('#unitFprice').attr('disabled', false);
                    }
                    // 净价总额 = 净价 * 券面总额 / 100
                    if (result.prinamt && result.prinamt != '' && result.price8 && result.price8 != ''){
                        $('#cpriceAmt').val(JoyinMath.doDiv(JoyinMath.doMul(result.price8, result.prinamt), 100, 2)).trigger('blur');
                        if($('#cpriceAmt').val()){
                            $('#cpriceAmt').attr('disabled', true);
                        }
                    }else{
                        $('#cpriceAmt').val('');
                        $('#cpriceAmt').attr('disabled', false);
                    }
                    // 行权收益率
                    if (result.strikeyield && result.strikeyield != ''){
                        $('#exerYield').val(Number(result.strikeyield)*Number(100)).trigger('blur');
                        if($('#exerYield').val()){
                            $('#exerYield').attr('disabled', true);
                        }
                    }else{
                        $('#exerYield').val('');
                        $('#exerYield').attr('disabled', false);
                    }
                    //成交编号带出的abs债券 行权收益率和到期收益率计算
                    if($('#yield').val()==''&&$('#exerYield').val()==''){
                        var paramObj = getCalcParam(false);
                        if (!paramObj) {
                            return;
                        }
                        Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/calculateData'), paramObj, function (result) {
                            if (result && result.data) {
                                var data = result.data;
                                $("#exerYield").val(data.exerYield).trigger("blur");
                                $("#yield").val(data.yield).trigger("blur");
                                $('#exerYield').attr('disabled', true);
                                $('#yield').attr('disabled', true);
                            }
                        },false);
                    }
                    //计算按钮隐藏
                    //$('#TrdBondInvestTrade002_btn_calculate').addClass('layui-btn-disabled').off();
                    // 银行间成交编号联动费用设置
                    calcFeeDataByFTradeId();
                }
            })
            if(procedureAmt_f){
                $('#procedureAmt').val(procedureAmt_f);
            }
            if(settleAmt_f){
                $('#settleServiceAmt').val(settleAmt_f);
            }
        }else{
            /*if ($('#finprodId').val()){
                $('#finprodName').val('');
                deleteSelect('finprodId');
                $('#finprodId').attr('disabled', false);
            }
            if($('#tradeMarket').val()){
                deleteSelect('tradeMarket');
            }
            if($('#tradeMarketMode').val()){
                deleteSelect('tradeMarketMode');
                $('#tradeMarketMode').attr('disabled', false);
            }
            if($('#tradePlat').val()){
                deleteSelect('tradePlat');
                $('#tradePlat').attr('disabled', false);
            }
            if($('#type9').val()){
                deleteSelect('type9');
            }
            if($('#type10').val()){
                deleteSelect('type10');
            }
            if($('#type11').val()){
                deleteSelect('type11');
            }
            // if ($('#portfolioId').val()){
            //     $('#bussTrdAvailableCashQuery_avalibleCash').val('');
            //     $('#bussTrdAvailableCashQuery_avalibleCashT1').val('');
            //     deleteSelect('portfolioId');
            //     $('#portfolioId').attr('disabled', false);
            // }


            if ($('#invAim').val()){
                deleteSelect('invAim')
                $('#invAim').attr('disabled', false);
            }
            if ($('#secManageAcctId').val()){
                deleteSelect('secManageAcctId');
                $('#secManageAcctId').attr('disabled', false);
            }
            if ($('#ccy').val()){
                $('#ccy').val('');
                $('#ccy').attr('disabled', false);
            }
            // 投资类型
            if ($('#ps').val()){
                deleteSelect('ps');
                $('#ps').attr('disabled', false);
            }
            if ($('#unitCprice').val()){
                $('#unitCprice').val('');
                $('#unitCprice').attr('disabled', false);
            }
            if ($('#yield').val()){
                $('#yield').val('');
                $('#yield').attr('disabled', false);
            }
            // 交易对手号代码
            // 票值
            if ($('#tradeAmt').val()){
                $('#tradeAmt').val('').change();
                $('#tradeAmt').attr('disabled', false);
            }
            if ($('#fpriceAmt').val()){
                $('#fpriceAmt').val('');
                $('#fpriceAmt').attr('disabled', false);
            }
            // 产品
            // 业务类型
            // 交易员
            if ($('#intAmt').val()){
                $('#intAmt').val('');
                $('#intAmt').attr('disabled', false);
            }
            // 有效状态
            // 经纪人
            // 录入日期
            if ($('#tradeDate').val()){
                $('#tradeDate').val($('#formatDate').val());
                $('#tradeDate').attr('disabled', true);
            }
            if ($('#settleDate').val()){
                $('#settleDate').val('');
                $('#settleDate').attr('disabled', true);
            }
            // 冲销日期
            // 更新的次数
            // 原始交易编号
            if ( $('#remark').val()){
                $('#remark').val('');
            }
            // 更新时间
            if ($('#trdInvestPersonInfo_counterId').val()){
                deleteSelect('trdInvestPersonInfo_counterId');
            }
            if ( $('#trdInvestPersonInfo_counterTrader').val()){
                $('#trdInvestPersonInfo_counterTrader').val('');
                $('#trdInvestPersonInfo_counterTrader').attr('disabled', false);
            }
            if ( $('#trdInvestPersonInfo_contactInfo').val()){
                $('#trdInvestPersonInfo_contactInfo').val('');
                $('#trdInvestPersonInfo_contactInfo').attr('disabled', false);
            }
            // 根据CSTP金额信息计算其他要素
            // 清算速度
            if ($('#settleSpeed').val()){
                deleteSelect('settleSpeed');
                $('#settleSpeed').trigger('change');
                $('#settleSpeed').attr('disabled', false);
                $('#settleDate').val('');
            }
            // 单位应计利息
            if ( $('#unitInt').val()){
                $('#unitInt').val('');
                $('#unitInt').attr('disabled', false);
            }
            // 全价
            if (  $('#unitFprice').val()){
                $('#unitFprice').val('');
                $('#unitFprice').attr('disabled', false);
            }
            // 净价总额 = 净价 * 券面总额 / 100
            if (  $('#cpriceAmt').val()){
                $('#cpriceAmt').val('');
                $('#cpriceAmt').attr('disabled', false);
            }
            // 行权收益率
            if (  $('#exerYield').val()){
                $('#exerYield').val('');
                $('#exerYield').attr('disabled', false);
            }
            if($('#bussTrdAvailableCashQuery_assetPosition').val()){
                $('#bussTrdAvailableCashQuery_assetPosition').val('');
                $('#bussTrdAvailableCashQuery_assetPosition').attr('disabled', false);
            }
            // 银行间成交编号联动费用设置
            calcFeeDataByFTradeId();*/
        }
    }

    var calcFeeDataByFTradeId = function () {
        var paramObj = $.serializeToObject('#submitForm', true);
        paramObj.calculateField = $("[name=calculateField]:checked").val();
        paramObj.tradeAmt = JoyinMath.doMul( $("#tradeAmt").val()?$("#tradeAmt").val():0,unitNum,2,JoyinMath.ROUND_HALF_UP);
        paramObj.unitCprice = $("#unitCprice").val();
        paramObj.unitInt = $("#unitInt").val();
        paramObj.unitFprice = $("#unitFprice").val();
        paramObj.cpriceAmt = $("#cpriceAmt").val();
        paramObj.intAmt = $("#intAmt").val();
        paramObj.fpriceAmt = $("#fpriceAmt").val();
        paramObj.exerYield = $("#exerYield").val();
        paramObj.yield = $("#yield").val();

        // 计算之前，先将应计利息清空
        var tradeMarketMode = $("#tradeMarketMode").val();
        if (tradeMarketMode != SHARES_TRADE_MARKET_01) {
            paramObj.unitInt = null;
        }

        Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/calculateFeeData'), paramObj, function (result) {
            if (result && result.data) {
                var data = result.data;
                if(data.tradeFeeList&&data.tradeFeeList.length>0){
                    $.each(data.tradeFeeList,function(i,v){
                        if(v.feeType==FEE_TYPE_05&&v.feeAmt){
                            commissionAmt = v.feeAmt;
                            $("#commissionAmtRate").val(v.feeRate).trigger("blur");
                            var feeAmt = v.feeAmt;
                            var tradeMarket = $('#tradeMarket').val();
                            if (tradeMarket == TRADE_PLACE_02 || tradeMarket == TRADE_PLACE_03) {
                                var tradeMarketMode = $("#tradeMarketMode").val();
                                if (tradeMarketMode == SHARES_TRADE_MARKET_01) {
                                    $("#commissionAmt").val(0).prop('disabled', true).trigger("change");
                                    feeAmt = 0;
                                }
                            }
                            if (feeAmt) {
                                $("#commissionAmt").val(v.feeAmt).prop('disabled', false).trigger("blur").trigger("change");
                            }

                            if($("#ps").val()==PS_T01){
                                $("#tradeRepAmt").val(Number(data.fpriceAmt)+Number(feeAmt)).trigger("blur");
                            }
                            if($("#ps").val()==PS_T02){
                                $("#tradeRepAmt").val(Number(data.fpriceAmt)-Number(feeAmt)).trigger("blur");
                            }
                        }
                        if(v.feeType==FEE_TYPE_02&&v.feeAmt){
                        	stampDutyAmt = v.feeAmt;
                            var feeAmt = v.feeAmt;
                            var tradeMarket = $('#tradeMarket').val();
                            if (tradeMarket == TRADE_PLACE_02 || tradeMarket == TRADE_PLACE_03) {
                                var tradeMarketMode = $("#tradeMarketMode").val();
                                if (tradeMarketMode == SHARES_TRADE_MARKET_01) {
                                    $("#stampDutyAmt").val(0).prop('disabled', true).trigger("change");
                                    feeAmt = 0;
                                }
                            }
                            if (feeAmt) {
                                $("#stampDutyAmt").val(v.feeAmt).prop('disabled', false).trigger("blur").trigger("change");
                            }

                        }
                        if(v.feeType==FEE_TYPE_06&&v.feeAmt){
                            $("#procedureAmt").val(v.feeAmt).trigger("blur");
                            if(procedureAmt_f){
                                $('#procedureAmt').val(procedureAmt_f);
                            }
                            $("#procedureAmtRate").val(v.feeRate).trigger("blur");
                        }
                        if(v.feeType == 'B08' && v.feeAmt){
                            $("#settleServiceAmt").val(v.feeAmt).trigger("blur");
                            if(settleAmt_f){
                                $('#settleServiceAmt').val(settleAmt_f);
                            }
                        }
                        /*if(v.feeType==FEE_TYPE_04&&v.feeAmt){
                            $("#transferAmt").val(v.feeAmt).trigger("blur");
                        }*/
                    });
                }
                else {
                    $("#commissionAmt").val("").trigger("change").trigger("blur");
                    $("#stampDutyAmt").val("").trigger("change").trigger("blur");
                    $("#procedureAmt").val("");
                    $("#settleServiceAmt").val("");
                    $("#settleAmt").val("");
                    $("#procedureAmtRate").val("");
                    $("#commissionAmtRate").val("");
                    $("#transferAmt").val("");
                }
            }
        },false);
    };

    var deleteSelect = function (id) {
        $('#'+id).val('').trigger('change');
    }
    var getDateByNow =  function (){
        var myDate = new Date;
        var year = myDate.getFullYear(); //获取当前年
        var mon = myDate.getMonth() + 1; //获取当前月
        var date = myDate.getDate();
        return year+'-'+mon+'-'+date;
    }
	
    /**
     * 参数是json字符串的ajax请求，必须是Post请求
     */
    var ajaxJsonSync = function (url, param, callback) {
        var elementIdForLog = Joyin.getElementId(url);
        if (url && url.indexOf("pageIdForLog") == -1) {
            if (url.indexOf("?") != -1) {
                if (pageId) {
                    url += "&pageIdForLog=" + pageId + "&elementIdForLog=" + elementIdForLog;
                }

            } else {
                if (pageId) {
                    url += "?pageIdForLog=" + pageId + "&elementIdForLog=" + elementIdForLog;
                }
            }
        }
        // 是否为待办事项标识
        if (param) {
            url += (url.indexOf("?") != -1 ? '&' : '?') + 'isTodoList=' + isTodoList;
        }
        param = JSON.stringify(param);
        if(url && url.indexOf(basePath) != 0 && url.indexOf('/') == 0) {
            url = JoyinUtil.getRealPath(url);
        }
        // “保存”、“提交”操作时，记录画面修改值
        if(elementIdForLog && (elementIdForLog.indexOf('_submit') != -1 || elementIdForLog.indexOf('_save') != -1)) {
            var data = JSON.parse(param);
            if (elementIdForLog.indexOf('_submit') != -1 && data.businessInfo) {
                JoyinUtil.setModifyFormData(JSON.parse(data.businessInfo));
            } else {
                JoyinUtil.setModifyFormData(data);
            }
        }
        $.ajax({
            type: "POST",
            url: url,
            dataType: "json",
            contentType: "application/json",
            data: param,
            async:false,
            success: function (result) {
                Joyin.processResult(result, callback);
            }
        });
    };
    //是否展示资产四五级分类
    if(IS_SHOW_TYPE == '01'){
    	$('#type11Div').show();
    }else{
    	$('#type11Div').hide();
    }
    
    // 内部交易则根据产品类型自动带出相应的净价
    var changeUnitCprice = function() {
    	if ($("#ps").val() == PS_T02 && $("#pageModel").val()=='0') {
    		// 交易对手为内部交易时查询债权市值净价及摊销净价
    		if ($("#trdInvestPersonInfo_counterId").val()
    				&& $("#trdInvestPersonInfo_counterId").select2('data')[0].object=='N') {
    			if($("#portfolioId").val() && $("#finprodId").val() && $("#invAim").val()) {
    				var paramObj = getCalcParam();
    				paramObj.portfolioId = $("#portfolioId").val();
    				paramObj.finprodId = $("#finprodId").val();
    				paramObj.invAim = $("#invAim").val();
                    if($("#chlId").val()) {
                        paramObj.chlId = $("#chlId").val();
                    }
    				paramObj.counterId = $("#trdInvestPersonInfo_counterId").val();
    				paramObj.tradeDate = $("#tradeDate").val();
    				Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/getCprice'), paramObj, function (result) {
    					if (result && result.data) {
    		                var data = result.data; 
    		                if (data.profitType == '02') {// 预期收益型
    		                	// 净价显示摊销净价
        		                $("#unitCprice").val(data.dscprice).trigger("change");
    		                } else {
    		                	// 净价显示市值净价
        		                $("#unitCprice").val(data.netprice).trigger("change");
    		                }
    					}
    				});
    			} 
    		} 
    	} 
    }
	// 显示净价信息查看及回显  吉林银行产品化需求  2019.12.2 马琳琳 end //
    
 // 查询交易对手是否为净值高频或者货币  2020年1月3日  wangyang1 start // 
    var changeTurnInvAim = function() {
        if ($("#ps").val() == PS_T02 && $("#trdInvestPersonInfo_counterId").val()){  
            //var paramObj = {};
            $("#turnInvAim_div").show();
           // BS-1763【债券二级交易】：转仓卖出流程中，转仓投资目的和提交时选的展示不一样； modify by wj 20200807
           // JHH: 投资目的按照选择的保存，不按照默认规则
//            paramObj.counterId = $("#trdInvestPersonInfo_counterId").val();
//            Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/getCounterId'), paramObj, function (result) {
//                if (result && result.data) {
//                   $("#turnInvAim").val(result.data).trigger("change");
//                }
//            });
        } else{
        	$("#turnInvAim").val('').trigger("change");
            $("#turnInvAim_div").hide(); 
        }
    }
    // 查询交易对手是否为净值高频或者货币  2020年1月3日  wangyang1 end // 
    initFlag = false;
}());
