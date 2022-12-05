//@ sourceURL=TrdFundInvestTrade002.js
/**
 * 基金投资交易新建
 */
$(function () {
    // 场内基金改造 add by renping 20191117 start
    var initFlag = true;
    // 计算交易金额
    var calcTradeAmt = function () {
        if (pageModel === PAGE_MODEL_REFERENCE){
            return;
        }
        if (!initFlag && !isTradeMarketFundMrMcDeal()) {
            var unitCprice = $('#unitCprice').val();
            var ps = $('#ps').val();
            var shareAmt = $('#shareAmt').val();
            if (unitCprice && shareAmt && ps == ps_mc) {
                $('#tradeAmt').val(unitCprice * shareAmt).trigger('blur');
                changeTradeAmtAndFee();
                $('#tradeAmtHid').val(unitCprice * shareAmt);
                if ($('#type6').val() == type_01) {
                    $('#carryAmt').change();
                }
            }
        } else if (!initFlag) {
            var unitCprice = $('#unitCprice').val();
            var tradeAmt = $('#tradeAmt').val();
            var ps = $('#ps').val();
            var shareAmt = $('#shareAmt').val();
            var carryAmt = $('#carryAmt').val();
            if (!unitCprice || !shareAmt) {
                return;
            }
            if (!carryAmt) {
                carryAmt = 0;
            }
            var commissionFee = $('#commissionFee').val();
            if (ps && $('#finprodId').val()) {
                param = {
                    tradeMarket: $('#tradeMarket').val(),
                    ps: ps,
                    tradeDate: $('#tradeDate').val(),
                    type6: $("#type6").val(),
                    type7: $("#trmFundType").val(),
                    secManageAcctId: $("#secManageAcctId").val()
                }
                Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/getCommissionFeeInfo', param,
                    function (result) {
                        if (result && result.isPayWithTrade) {
                            if (result.isPayWithTrade == YES_OR_NO_N) {
                                commissionFee = 0;
                            }else{
                                commissionFee = result.feeAmt;
                            }
                            $("#isPayWithTrade").val(result.isPayWithTrade);
                        } else {
                            $("#isPayWithTrade").val('');
                        }
                    }, false);

            }
            $('#commissionFee').val(commissionFee)
            var chlFee = $('#channelAmt').val();
            if ($('#chlId').val()) {
                var data = $('#chlId').select2('data')[0].object;
                if (data && data.collectType) {
                    $('#collectType').val(data.collectType);
                    if (data.collectType != collectType_01) {
                        chlFee = 0;
                    }
                }
            }
            if ($("#ps").val() == ps_mr || $("#ps").val() == ps_T04 || $("#ps").val() == ps_T52) {
                $("#tradeAmt").val(JoyinMath.doAdd(JoyinMath.doAdd(Number(shareAmt) * Number(unitCprice), chlFee, 2), commissionFee, 2)).trigger('blur');
                $('#tradeAmtHid').val($('#tradeAmt').val());
            } else {
                $("#tradeAmt").val(JoyinMath.doMinus(JoyinMath.doMinus(Number(shareAmt) * Number(unitCprice), chlFee, 2), commissionFee, 2)).trigger('blur');
                $('#tradeAmtHid').val($('#tradeAmt').val());
            }
            changeTradeAmtAndFee();
            if ($('#type6').val() == type_01) {
                $('#carryAmt').change();
            }
        }
    };

    /**
     * 仅画面控制联动，不牵涉任何其他change事件，避免死循环新copy的方法
     */
    var pageItemDisplay = function () {
        var ps = $('#ps').val();
        var data = $('#finprodId').select2('data')[0].object;
        var portfolioId = $('#portfolioId').val();
        var ccy = $('#ccy').val();
        var type6 = $('#type6').val();
        if (ps == ps_T04 || ps == ps_T52) {
            $('#proAvailableCash_div').show();
            $('#proAvailableCash1_div').show();
            // $('#accAvailableCash_div').show();
            if ($("#secManageAcctId").val() && ($("#tradeMarket").val() == TRADE_PLACE_03 || $("#tradeMarket").val() == TRADE_PLACE_04)) {
                $('#tradeMarketAccAvailableCash_div').show();
            } else {
                $('#tradeMarketAccAvailableCash_div').hide();
            }
            $('#tradePrice_label').hide();
            $('.commissionFee_class').hide();
            $('.channel_cash').show();
            $('#accAvailableCash_c_div').hide();
            $('#assetAvailablePosition_div').hide();
            $('#expectedArrivalDate_div').hide();
            $('#transferInformation').show();
            if (ps == ps_T52) {
                $('#settleSpreed1_div').hide();
                $('#trdFundFTradeId').hide();
            } else {
                $('#settleSpreed1_div').show();
                $('#trdFundFTradeId').show();
            }

            $("#changeToFinprodMarketId_div").show();
            $('#changeToFinprodName_div').show();
            $('#settleSpreed2_div').show();
            $('#confirmDate').hide();
            $('#trdDate').show();
            $('#carryAmt_div').hide();
            $('#unitCprice_div').hide();
            $('#netDate_div').hide();
            $('#purchaseFee_div').hide();
            $('#tradeSha_div').hide();
            $('#tradeAmt_div').show();
            $('#tradeAmt').attr("disabled", false);
            $(".channelAmt_class").hide();
            if (!initFlag) {
                $('#shareAmt').val("");
                $("#channelAmt").val("");
            }
            if (data && data.finprodId && portfolioId && ccy) {
                getPtlCashPositionInfo();
            }
        } else if (ps == ps_T06 || $("#ps").val() == T65) {
            $('#proAvailableCash_div').hide();
            $('#proAvailableCash1_div').hide();
            // $('#accAvailableCash_div').hide();
            $('#tradeMarketAccAvailableCash_div').hide();
            $('#tradePrice_label').hide();
            $('.commissionFee_class').hide();
            $('.channel_cash').hide();
            $('#assetAvailablePosition_div').show();
            if($("#ps").val() == T65) {
                $('#expectedArrivalDate_div').hide();
                $("#changeToFinprodMarketId_div").show();
                $('#changeToFinprodName_div').show();
                $('#trdFundFTradeId').hide();
            }
            else {
                $('#expectedArrivalDate_div').show();
                $("#changeToFinprodMarketId_div").hide();
                $('#changeToFinprodName_div').hide();
                $('#trdFundFTradeId').show();
            }

            $('#transferInformation').hide();
            $('#settleSpreed1_div').show();
            $('#settleSpreed2_div').show();
            $('#confirmDate').hide();
            $('#trdDate').show();
            $('#carryAmt_div').hide();
            $('#unitCprice_div').hide();
            $('#netDate_div').hide();
            $('#purchaseFee_div').hide();
            $('#tradeSha_div').show();
            $('#tradeAmt_div').hide();
            $('#shareAmt').attr("disabled", false);
            $(".channelAmt_class").hide();
            if (ps == ps_T06) {
                $('#trdFundFTradeId').show();
            }
            else {
                $('#trdFundFTradeId').hide();
            }
            if (!initFlag) {
                $('#tradeAmt').val("");
                $("#channelAmt").val("");
            }
            if (data && data.finprodId && portfolioId) {
                getPtlSecPositionInfo();
            }
        } else if (ps == ps_mr) {
            $('#proAvailableCash_div').show();
            $('#proAvailableCash1_div').show();
            // $('#accAvailableCash_div').show();
            $('#unitCprice_div').show();
            if ($("#tradeMarket").val() == TRADE_PLACE_03 || $("#tradeMarket").val() == TRADE_PLACE_04) {
                $('#tradeMarketAccAvailableCash_div').show();
                $('#tradePrice_label').show();
                $('#unitCprice_label').hide();
                $('#unitCprice').prop("disabled", false);
                $('.commissionFee_class').show();
                $(".channelAmt_class").hide();
            } else {
                $('#tradeMarketAccAvailableCash_div').hide();
                $('#tradePrice_label').hide();
                $('#unitCprice_label').show();
                $('#unitCprice').prop("disabled", true);
                $('.commissionFee_class').hide();
                $(".channelAmt_class").hide();
            }
            //内部交易对手显示
            if($('#trdInvestPersonInfo_counterId')&&$('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0].object =='N'){
            	$(".channelAmt_class").show();
            }
            if (isTradeMarketDeal()) {
                $('#carryAmt').prop("disabled", true);
            } else {
                $('#carryAmt').prop("disabled", false);
            }
            $('.channel_cash').show();
            $('#accAvailableCash_c_div').hide();
            $('#assetAvailablePosition_div').hide();
            $('#expectedArrivalDate_div').hide();
            $('#transferInformation').show();
            $('#settleSpreed1_div').hide();
            $('#settleSpreed2_div').hide();
            $('#trdFundFTradeId').hide();
            $('#confirmDate').show();
            $('#trdDate').hide();
            if ($('#type6').val() == type_01) {
                $('#carryAmt_div').show();
                $('#netDate_div').hide();
            } else {
                $('#carryAmt_div').hide();
                $('#netDate_div').show();
            }
            $('#purchaseFee_div').show();
            $('#purchase').show();
            $('#redemp').hide();
            $('#tradeSha_div').show();
            if (isTradeMarketDeal()) {
                $('#shareAmt').attr("disabled", false);
                $('#tradeAmt').attr("disabled", false);
            } else {
                $('#shareAmt').attr("disabled", true);
                $('#tradeAmt').attr("disabled", false);
            }
            $('#tradeAmt_div').show();
            $('#changeToFinprodMarketId_div').hide();
            $('#changeToFinprodName_div').hide();
            if (data && data.finprodId && portfolioId && ccy) {
                getPtlCashPositionInfo();
            }
        } else if (ps == ps_mc) {
            $('#proAvailableCash_div').hide();
            $('#proAvailableCash1_div').hide();
            // $('#accAvailableCash_div').hide();
            $('#tradeMarketAccAvailableCash_div').hide();
            $('#unitCprice_div').show();
            if ($("#tradeMarket").val() == TRADE_PLACE_03 || $("#tradeMarket").val() == TRADE_PLACE_04) {
                $('#tradePrice_label').show();
                $('#unitCprice_label').hide();
                $('#unitCprice').prop("disabled", false);
                $('.commissionFee_class').show();
            } else {
                $('#tradePrice_label').hide();
                $('#unitCprice_label').show();
                $('#unitCprice').prop("disabled", true);
                $('.commissionFee_class').hide();
            }
            $('#carryAmt').prop("disabled", false);
            $('.channel_cash').hide();
            $('#assetAvailablePosition_div').show();
            $('#expectedArrivalDate_div').hide();
            $('#transferInformation').show();
            $('#settleSpreed1_div').hide();
            $('#settleSpreed2_div').hide();
            $('#trdFundFTradeId').hide();
            $('#confirmDate').show();
            $('#trdDate').hide();
            if ($('#type6').val() == type_01) {
                $('#carryAmt_div').show();
                $('#netDate_div').hide();
            } else {
                $('#carryAmt_div').hide();
                $('#netDate_div').show();
            }
            $('#purchaseFee_div').show();
            $('#purchase').hide();
            $('#redemp').show();
            $('#tradeSha_div').show();
            $('#shareAmt').attr("disabled", false);
            $('#tradeAmt_div').show();
            $('#tradeAmt').attr("disabled", false);
            if (data && data.finprodId && portfolioId) {
                getPtlSecPositionInfo();
            }
            $('#changeToFinprodMarketId_div').hide();
            $('#changeToFinprodName_div').hide();
            $(".channelAmt_class").show();
        }
        changeTradeAmtAndFee();
        if(ps == ps_T06){
            $('#TrdFundInvestTrade002_btn_risk').addClass("layui-btn-disabled");
        }else{
            $('#TrdFundInvestTrade002_btn_risk').removeClass("layui-btn-disabled");
        }
    };
    // 场内基金改造 add by renping 20191117 end
    var getriskdata = function () {
        var data = $('#finprodId').select2('data')[0].object;
        var finprodId;
        if (data && data.finprodId) {
            finprodId= data.finprodId;
        }
        var obj = {
            timePoint: MY_SUP_TIME_TYPE_02,
            valDate: $('#tradeDate').val(),
            portfolioCode: $('#portfolioId').val(),
            tradeAmount: $('#tradeAmt').val(),
            assetType: FINPROD_TYPE_F06,
            isEnabled: '01',
            assetCode: finprodId,
            tradeDirection: $('#ps').val()
        }
        // add by zhangrui1 交易对手影响风险试算 start
        obj.counterId = $('#trdInvestPersonInfo_counterId').val();
        // add by zhangrui1 交易对手影响风险试算 end
        var object = {
            valDate: ['tradeDate', 'TrdFundInvestTrade002.tradeDate'],
            portfolioCode: ['portfolioId', 'TrdFundInvestTrade002.portfolioId'],
            tradeAmount: ['tradeAmt', 'TrdFundInvestTrade002.tradeAmt'],
            assetType: ['type6', 'TrdFundInvestTrade002.type6'],
            tradeDirection: ['ps', 'TrdFundInvestTrade002.ps'],
            data: obj
        }
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'TrdInvestPersonInfo.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        if (ps == ps_T06 || $("#ps").val() == T65) {
            obj.tradeAmount = $('#shareAmt').val();
            obj.amtType = "1";
            object.tradeAmount = ["shareAmt", "TrdFundInvestTrade002.tradeSha"];
        }
        return object;
    }

    function saveOrSubmit(operateType) {
    	// 划款指令隐藏时，自动生成划款指令信息
    	generateOrders(function () {
	        var s;
            if(typeof(BaseRiskMathComm) != 'undefined') {
	        if (BaseRiskMathComm&&$("#ps").val()!=ps_T06 ) {
	            s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
	            if (!s && s != '') {
	                return;
	            }
	            if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())) {
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

	        var tradeMarket = $("#tradeMarket").val();
	        var ps = $("#ps").val();
	        if (ps == T65 && tradeMarket != TRADE_PLACE_06) {
	            Joyin.error("只有场外基金才能做转换。");
	            return;
	        }

	        if ($('#changeToFinprodMarketId').val() && $('#changeToFinprodMarketId').select2('data')[0].object) {
	            tradeMarket = $('#changeToFinprodMarketId').select2('data')[0].object.tradeMarket
	            if (ps == T65 && tradeMarket && tradeMarket != TRADE_PLACE_06) {
	                Joyin.error("转入基金是场外基金才能做转换。");
	                return;
	            }

	            var issuer1 = $('#finprodId').select2('data')[0].object.issuer;
	            var issuer = $('#changeToFinprodMarketId').select2('data')[0].object.issuer;
	            if (issuer1 && issuer && issuer1 != issuer) {
	                Joyin.error("转入基金和转出基金必须属于同一管理人。");
	                return;
	            }
	        }

	        if ($('#changeToFinprodMarketId').val() && $('#changeToFinprodMarketId').select2('data')[0].object && $('#changeToFinprodMarketId').select2('data')[0].object.finprodId) {
	            object.rFinprodId = $('#changeToFinprodMarketId').select2('data')[0].object.finprodId;
	        }

	        if (object.shareAmt && object.unitCprice) {
	            object.prinAmt = Number(object.shareAmt) * Number(object.unitCprice);
	        } else {
	            object.prinAmt = object.tradeAmt;
	        }
	        //交易金额赋值
	        if(object.tradeAmtAndFee){
	        	object.tradeAmt = object.tradeAmtAndFee;
	        }
	        var intAmt = 0;
	        if (object.intAmt) {
	            intAmt = object.intAmt;
	        }
	        object.fpriceAmt = object.prinAmt + Number(intAmt);
	        var data = $("#chlId").find("option:selected").data("data");
	        if (data && data.object) {
	            object.payType = data.object.payType;
	        }

	        if (FileCommon) {
	            object.fileArray = FileCommon.getUploadFileInfo();
	            if (object.fileArray && _.isArray(object.fileArray)) {
	                $.each(object.fileArray, function (index, obj) {
	                    obj.seqNo = obj.fileNo;
	                });
	            }
	        }
	        if(parentPageId){
	            object.parentPageId = parentPageId;
            }
	        if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
	            object.counterType = $('#trdInvestPersonInfo_counterId').select2('data')[0].object;
	            object.counterId=$('#trdInvestPersonInfo_counterId').select2('data')[0].id;
	        }
	        var data = $('#finprodId').select2('data')[0].object;
	        if (data && data.finprodId) {
	            object.finprodId = data.finprodId;
	            object.finprodMarketId = $('#finprodId').val();
	            Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/check', object, function(result1) {
	                Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/check', object, function (result) {
	                	 //授信占用额度
//		                Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {});
	                	object.riskCreditListCommonDto = result1.data;
	                	Joyin.confirm(result.data, function () {
	                        if (operateType === 'save') {
	                            WorkFlow.doSaveList(basePath + 'invest/trade/fund/TrdFundInvestTrade002/save', object, "TrdFundInvestTrade002_btn_save");
	                        } else {
	                            WorkFlow.doSubmitList(basePath + 'invest/trade/fund/TrdFundInvestTrade002/save', object, "TrdFundInvestTrade002_btn_submit");
	                        }
	                    });
	                });
	            });
	        } else {
	            Joyin.error("请输入资产代码。")
	        }
    	 });
	};

    // <!-- 通道交易 新增 强成西 2019/03/26 start -->
    $("#channelAmt,#commissionFee").on("change", function () {
        calcTradeAmt();
        changeTradeAmtAndFee();
    });

    $("#tradeAmt").on("change", function () {
        // add gl GLTZJY-197 交易金额联动带出交易份额 by xuhongfeng 20220531 start
        var tradeAmt = $('#tradeAmt').val();
        var ps = $('#ps').val();
        if(ps == ps_T04 || ps == ps_T06){
           if(tradeAmt){
                 $('#shareAmt').val('').attr('disabled',true).attr('required',false);
           }else{
                 $('#shareAmt').attr('disabled',false).attr('required',true);
           }
                  star();
           }
           if(ps == ps_mr || ps == ps_mc){
               var feeAmtPay = $('#feeAmtPay').val();
               var unitCprice = $('#unitCprice').val();
               if(unitCprice && $('#tradeAmt').val()){
               $('#shareAmt').val($('#tradeAmt').val()/unitCprice).trigger('blur');
           }
        }
        // add gl GLTZJY-197 交易金额联动带出交易份额 by xuhongfeng 20220531 end
        changeTradeAmtAndFee();
    });

    var changeTradeAmtAndFee = function() {
       /* if ($("#ps").val() == ps_mr || $("#ps").val() == ps_T04) {
            $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#tradeAmt").val(), $("#channelAmt").val(), 2)).trigger('blur');
        }
        else {
            $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmt").val(), $("#channelAmt").val(), 2)).trigger('blur');
        }*/
    	 if ($("#ps").val() == ps_mr || $("#ps").val() == ps_mc) {
    		 if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
    			 var 交易对手 =  $('#trdInvestPersonInfo_counterId').select2('data')[0].object;
    			 if (交易对手 == 'N') {
    				 $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmt").val(), $("#channelAmt").val(), 2)).trigger('blur');
    			 } else {
    				 //外部卖出
    				 if($("#ps").val() == ps_mc &&$("#chlId").val()&&$("#chlId").select2("data")[0].object.collectType==collectType_01){
    					 $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmt").val(), $("#channelAmt").val(), 2)).trigger('blur');
    				 }
    			 }
    		 }
         }
    	/*if ($("#ps").val() == PS_T02&&$("#chlId").val()&&$("#chlId").select2("data")[0].object.collectType==Collect_Type_01) {
          	 $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#channelFee").val(), 2)).trigger('blur');
    	}*/

    }
    $("#chlId").on("change", function () {
        if ($("#chlId").val()) {
            $("#secManageAcctId").textSelect('setValue', '');
            $("#secManageAcctId").prop("disabled", true);
            $("#secManageAcctId").trigger("change");
            $("#channelAmt").prop("disabled", false);
            $("#channelAmt").trigger("change");
        } else {
            $("#secManageAcctId").prop("disabled", false);
            $("#channelAmt").prop("disabled", true);
            $("#channelAmt").val('').trigger("change");
        }
        selectManeger();
        getPtlCashPositionInfo();
        getPtlSecPositionInfo();
    });

    // <!-- 通道交易 新增 强成西 2019/03/26 end -->
    $("#secManageAcctId").on("change", function () {
        if ($("#secManageAcctId").val()) {
            // 通道的change会计算组合现金数据
            $("#chlId").val('').trigger("change");
            $("#chlId").prop("disabled", true);
            $("#channelAmt").val("").trigger("blur");
            $("#channelAmt").trigger("change");
            $("#channelAmt").prop("disabled", true);
            //
        } else {
            $("#chlId").prop("disabled", false);
            $("#channelAmt").prop("disabled", false);

        }
        pageItemDisplay(); // 内部有剩余现金查询
    });

    //查询组合可用现金
    var getPtlCashPositionInfo = function () {
        if (!$('#portfolioId').val() || !$('#ccy').val() || !$('#tradeDate').val()) {
            return;
        }
        param = {
            portfolioId: $('#portfolioId').val(),
            ccy: $('#ccy').val(),
            vdate: $('#tradeDate').val(),
            calId: $('#calendarId').val(),
            chlId: $("#chlId").val(),
            secAcctId: $("#secManageAcctId").val()
        }
        Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/getProAvailableCash', param,
            function (result) {
                $('#proAvailableCash').val(result.cashT).trigger('blur');
                $('#proAvailableCash1').val(result.cashT1).trigger('blur');
                // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                $('#channelAvailableCash').val(result.T_2).trigger('blur');
                // <!-- 通道交易 新增 强成西 2019/03/26 end -->
                $('#accAvailableCash').val(result.cashT3).trigger('blur');

                // 场内基金改造 add by renping 20191117 start
                // 交易所时，证券托管户现金
                if ($("#tradeMarket").val() == TRADE_PLACE_03 || $("#tradeMarket").val() == TRADE_PLACE_04) {
                    $('#tradeMarketAccAvailableCash').val(result.cashS).trigger('blur');
                } else {
                    $('#tradeMarketAccAvailableCash').val('').trigger('blur');
                }
                // 场内基金改造 add by renping 20191117 end
            });
    }
    //查询组合可用持仓
    var getPtlSecPositionInfo = function () {
        var data = $('#finprodId').select2('data')[0].object;
        if (data && data.finprodId && $('#portfolioId').val()) {
            var finprodId = data.finprodId;
            var secAcctId = $("#secManageAcctId").val();
            if (!secAcctId) {
                secAcctId = $("#chlId").val();
            }
            param = {portfolioId: $('#portfolioId').val(), finprodId: finprodId, cdate: $('#tradeDate').val(), secAcctId: secAcctId}
            Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/getPtlSecPosition', param,
                function (result) {
                    $('#assetAvailablePosition').val(result).trigger('blur');
                });
        }
    }
    // 场内基金改造 add by renping 20191117 start
    // 判断是否为交易所交易
    var isTradeMarketDeal = function () {
        if (isTradeMarketFundMrMcDeal()
            && ($("#chlId").val() || $("#trdInvestPersonInfo_counterId").val() == COUNTER_PARTY_ID_SHANGHAI
                || $("#trdInvestPersonInfo_counterId").val() == COUNTER_PARTY_ID_SHENZHEN)) {
            return true;
        } else {
            return false;
        }
    }

    // 判断是否为交易所基金买入卖出交易
    var isTradeMarketFundMrMcDeal = function () {
        if (($('#ps').val() == ps_mr || $('#ps').val() == ps_mc)
            && ($("#tradeMarket").val() == TRADE_PLACE_03 || $("#tradeMarket").val() == TRADE_PLACE_04)) {
            return true;
        } else {
            return false;
        }
    }
    // 交易对手变更时联动处理
    var counterIdOnChange = function () {
        if (isTradeMarketDeal() && !$("#chlId").val()) {
            getPtlCashPositionInfo();
        }
        if (pageModel != PAGE_MODEL_REFERENCE) {
            var counterId = $('#trdInvestPersonInfo_counterId');
            if (counterId.select2('data') && counterId.select2('data')[0]) {
                var 交易对手 =  counterId.select2('data')[0].object;
                // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                if (交易对手 == 'N') {
                        $('#commissionFee').val('').prop("disabled", true).trigger("change");
                } else {
                    $('#commissionFee').prop("disabled", false).trigger("change");
                }
            }
//            //计算费用
//            calcTradeAmt();
    	}
        pageItemDisplay();
        if (!isTradeMarketDeal() && $("#ps").val() == ps_mr) {
            $('#unitCprice').change();
        }
    }
    // 交易场所变更时联动处理
    var tradeMarketChange = function () {
        if ($("#tradeMarket").val() == TRADE_PLACE_03 || $("#tradeMarket").val() == TRADE_PLACE_04) {
            if ($("#chlId").val()) {
                $("#chlId").prop("disabled", false);
                $("#secManageAcctId").prop("disabled", true);
            } else if ($("#secManageAcctId").val()) {
                $("#chlId").prop("disabled", true);
                $("#secManageAcctId").prop("disabled", false);
            } else {
                $("#chlId").prop("disabled", false);
                $("#secManageAcctId").prop("disabled", false);
            }
        } else {
            $("#chlId").prop("disabled", false);
            $("#secManageAcctId").prop("disabled", true);
        }
        pageItemDisplay();
    }
    // 场内基金改造 add by renping 20191117 end
    //查询资产管理人
    var selectManeger = function () {
        if (initFlag) {
            return;
        }
        var data = $('#finprodId').select2('data')[0].object;
        var ps = $("#ps").val()
        if (data && data.finprodId) {
            if ((ps == ps_mr || ps == ps_mc) && $("#tradeMarket").val() == TRADE_PLACE_03 && !$("#chlId").val()) {
                $("#trdInvestPersonInfo_counterId").textSelect('setValue', COUNTER_PARTY_ID_SHANGHAI)
                $("#trdInvestPersonInfo_counterId").trigger('change');
                $("#trdInvestPersonInfo_counterId").prop('disabled', false);
            } else if ((ps == ps_mr || ps == ps_mc) && $("#tradeMarket").val() == TRADE_PLACE_04 && !$("#chlId").val()) {
                $("#trdInvestPersonInfo_counterId").textSelect('setValue', COUNTER_PARTY_ID_SHENZHEN);
                $("#trdInvestPersonInfo_counterId").trigger('change');
                $("#trdInvestPersonInfo_counterId").prop('disabled', false);
            } else if ((ps == ps_mr || ps == ps_mc)
                && $('#trdInvestPersonInfo_counterId').val()
                && $('#trdInvestPersonInfo_counterId').select2('data')[0].object != 'N'
                && $("#tradeMarket").val() != TRADE_PLACE_03
                && $("#tradeMarket").val() != TRADE_PLACE_04) {
                $('#trdInvestPersonInfo_counterId').val('').trigger('change');
                $("#trdInvestPersonInfo_counterId").prop('disabled', false);
            } else if ((ps != ps_mr && ps != ps_mc) ) {
                Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/selectManeger', {issuer: data.issuer},
                    function (result) {
                        $("#trdInvestPersonInfo_counterId").textSelect('setValue', result.id);
                        $("#trdInvestPersonInfo_counterId").trigger('change');
                        $("#trdInvestPersonInfo_counterId").prop('disabled', true);
                    });
            } else if ($('#chlId').val() && $('#trdInvestPersonInfo_counterId').val()
                && ($('#trdInvestPersonInfo_counterId').select2('data')[0].object == 'N'
                    || $("#trdInvestPersonInfo_counterId").val() == COUNTER_PARTY_ID_SHANGHAI
                    || $("#trdInvestPersonInfo_counterId").val() == COUNTER_PARTY_ID_SHENZHEN)) {
                $('#trdInvestPersonInfo_counterId').val('').trigger('change');
            } else {
                $("#trdInvestPersonInfo_counterId").prop('disabled', false);
            }
        } else {
            $("#trdInvestPersonInfo_counterId").prop('disabled', false);
        }
    }
    /**
     * 动态设置不可编辑
     */
    var checkAndAssign = function (el, value, editable) {
        if (value != null && value != '') {
            $(el).val(value).trigger('change');
            if (!editable) {
                $(el).attr("disabled", true);
            }
        }
    };
    /**
     * 交易方向发生变化清空交易对手
     */
    var clearTrader = function () {
        $('#trdInvestPersonInfo_counterId').val('').trigger('change');
        $('#trdInvestPersonInfo_counterTrader').val('');
        $('#trdInvestPersonInfo_contactInfo').val('');
    }
    //画面初始化
    var init = function () {

        var entrustId = $("#entrustId").val();

        $('#finprodId').on('change', function () {
            var data = $('#finprodId').select2('data')[0].object;
            $("#settleSpreed").val('').change();
            if (data) {
                var finprodId = data.finprodId;
                var ps = $('#ps').val();
                var portfolioId = $('#portfolioId').val();
                var ccy = $('#ccy').val();
                if (finprodId) {
                    $("#finprodName").val(data.finprodId).trigger('change');
                    if (data.trmFundAbbr) {
                        $('#trmFundAbbr_div').show();
                        $("#trmFundAbbr").val(data.trmFundAbbr);
                    } else {
                        $("#trmFundAbbr").val("");
                        $('#trmFundAbbr_div').hide();
                    }
                    $("#trmFundType").val(data.trmFundType).trigger('change');
                    $("#calendarId").val(data.calendarId).trigger('change');
                    $("#ccy").val(data.ccy).trigger('change');
                    $("#tradeMarket").val(data.tradeMarket).trigger('change');
                    $("#type6").val(data.type6).trigger('change');
                    //新增资产二、三、四级分类	王贵奇	2019/7/30	开始
                    $("#type9").val(data.type9).trigger('change');
                    $("#type10").val(data.type10).trigger('change');
                    $("#type11").val(data.type11).trigger('change');
                    //新增资产二、三、四级分类	王贵奇	2019/7/30	结束
                    if ((ps == ps_T04 || (ps == ps_T06 || $("#ps").val() == T65) || ps == ps_T52) && !entrustId) {
                        $('#trdInvestPersonInfo_counterId option').remove()
//                $("#trdInvestPersonInfo_counterId").textSelect('setValue',data.issuer);
                        selectManeger();
                    } else {
                        selectManeger();
                    }
                    //add  by zhangnian  20190917  start
                    if ($('#ps').val() == ps_T04) {
                        $("#settleSpreed").val(data.purSpreed).change();
                    } else if ((ps == ps_T06 || $("#ps").val() == T65)) {
                        $("#settleSpreed").val(data.redSpreed).change();
                    }
                    //add  by zhangnian  20190917  end
                    Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/getSpeedByFinprodId', data,function (result) {
                        $("#purSpeed").val(result.PURSPEED).trigger('change');
                        $("#redSpeed").val(result.REDSPEED).trigger('change');
                    });
                } else {
                    //自动 加券
                    var autoCoupon = AutoCoupon.autoFundTradeCoupon($('#finprodId').select2('data')[0].text);
                    $('#finprodId').textSelect('setValue', autoCoupon.finprodMarketId);
                    $("#finprodName").val(autoCoupon.finprodName).trigger('change');
                    $("#calendarId").val(autoCoupon.calendarId).trigger('change');
                    $("#ccy").val(autoCoupon.ccy).trigger('change');
                    $("#type6").val(autoCoupon.type4).trigger('change');
                    $("#tradeMarket").val(autoCoupon.tradeMarket).trigger('change');
                    if ((ps == ps_T04 || (ps == ps_T06 || $("#ps").val() == T65) || ps == ps_T52) && !entrustId) {
                        $('#trdInvestPersonInfo_counterId option').remove()
                        selectManeger();
                    }
                    $("#purSpeed").val('').trigger('change');
                    $("#redSpeed").val('').trigger('change');
                }
            }
            if ((ps == ps_T04 || ps == ps_mr || ps == ps_T52) && finprodId && portfolioId) {
                getPtlCashPositionInfo();
            }
            if (((ps == ps_T06 || $("#ps").val() == T65) || ps == ps_mc) && finprodId && portfolioId) {
                getPtlSecPositionInfo();
            }
            $('#netDate').change();
        });

        // 场内基金改造 add by renping 20191117 start
        // 绑定按钮事件
        $('#tradeMarket').on('change', tradeMarketChange);
        // 场内基金改造 add by renping 20191117 end

        /**
         * 交易方向
         */
        $('#ps').on('change', function () {
            if (!initFlag) {
                $("#changeToFinprodMarketId").val('').trigger('change');
            }
            $('#changeToFinprodMarketId').prop('disabled', true);

            var ps = $('#ps').val();
            var data = $('#finprodId').select2('data')[0].object;
            var portfolioId = $('#portfolioId').val();
            var ccy = $('#ccy').val();
            var type6 = $('#type6').val();
            $("#settleSpreed").val('').change();
            if (ps == ps_T04 || ps == ps_T52) {
                $('#proAvailableCash_div').show();
                $('#proAvailableCash1_div').show();
                // $('#accAvailableCash_div').show();
                // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                $('.channel_cash').show();
                $('#accAvailableCash_c_div').hide();
                // <!-- 通道交易 新增 强成西 2019/03/26 end -->
                $('#assetAvailablePosition_div').hide();
                $('#expectedArrivalDate_div').hide();
                if(ps == ps_T52) {
                    $('#transferInformation').hide();
                    $('#trdFundFTradeId').hide();
                }
                else {
                    $('#transferInformation').show();
                    $('#trdFundFTradeId').show();
                }
                $("#changeToFinprodMarketId_div").hide();
                $("#changeToFinprodName_div").hide();
                $('#settleSpreed1_div').show();
                $('#settleSpreed2_div').show();
                $('#confirmDate').hide();
                $('#trdDate').show();
                $('#carryAmt_div').hide();
                $('#unitCprice_div').hide();
                $('#netDate_div').hide();
                $('#purchaseFee_div').hide();
                $('#tradeSha_div').hide();
//                $('#shareAmt').val("");
                $('#tradeAmt_div').show();
                $('#tradeAmt').attr("disabled", false);
                $(".channelAmt_class").hide();
//                $("#channelAmt").val("");
                if (data && data.finprodId) {
                    $('#trdInvestPersonInfo_counterId option').remove()
//                    $("#trdInvestPersonInfo_counterId").textSelect('setValue',$('#finprodId').select2('data')[0].object.issuer);
                    selectManeger();
                    $("#settleSpreed").val(data.purSpreed).change();
                }
                if (data && data.finprodId && portfolioId && ccy) {
                    getPtlCashPositionInfo();
                }
            } else if (ps == ps_T06 || $("#ps").val() == T65) {
                $('#proAvailableCash_div').hide();
                $('#proAvailableCash1_div').hide();
                // $('#accAvailableCash_div').hide();
                // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                $('.channel_cash').hide();
                // <!-- 通道交易 新增 强成西 2019/03/26 end -->
                $('#assetAvailablePosition_div').show();
                if ($("#ps").val() == T65) {
                    $('#expectedArrivalDate_div').hide();
                    $("#changeToFinprodMarketId_div").show();
                    $('#changeToFinprodName_div').show();
                    $('#trdFundFTradeId').hide();
                }
                else {
                    $('#expectedArrivalDate_div').show();
                    $("#changeToFinprodMarketId_div").hide();
                    $('#changeToFinprodName_div').hide();
                    $('#trdFundFTradeId').show();
                }

                $('#transferInformation').hide();
                $('#settleSpreed1_div').show();
                $('#settleSpreed2_div').show();
                $('#confirmDate').hide();
                $('#trdDate').show();
                $('#carryAmt_div').hide();
                $('#unitCprice_div').hide();
                $('#netDate_div').hide();
                $('#purchaseFee_div').hide();
                $('#tradeSha_div').show();
                $('#tradeAmt_div').hide();
//                $('#tradeAmt').val("");
                $('#shareAmt').attr("disabled", false);
                $(".channelAmt_class").hide();
//                $("#channelAmt").val("");
                if (data && data.finprodId) {
                    $('#trdInvestPersonInfo_counterId option').remove()
//                    $("#trdInvestPersonInfo_counterId").textSelect('setValue',$('#finprodId').select2('data')[0].object.issuer);
                    selectManeger();
                    $("#settleSpreed").val(data.redSpreed).change();
                }
                if (data && data.finprodId && portfolioId) {
                    getPtlSecPositionInfo();
                }
                if ($("#ps").val() == T65) {
                    $(".changein").show();
                    $('#changeToFinprodMarketId').prop('disabled', false);
                }
                //-----add by 裴家俊 切换到赎回时清空风险试算列表 start ----
                if ($("#ps").val() == ps_T06 && $("#baseRiskMathCommTable").data('joyintable')!=null) {
                    $("#riskTrialSpan").empty();
                    $("#baseRiskMathCommTable").data('joyintable').reload({data:[]});
                    $("#riskMathFlag").val("01");
                }
                //-----add by 裴家俊 切换到赎回时清空风险试算列表 end ----
            } else if (ps == ps_mr) {
                $('#proAvailableCash_div').show();
                $('#proAvailableCash1_div').show();
                // $('#accAvailableCash_div').show();
                // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                $('.channel_cash').show();
                $('#accAvailableCash_c_div').hide();
                // <!-- 通道交易 新增 强成西 2019/03/26 end -->
                $('#assetAvailablePosition_div').hide();
                $('#expectedArrivalDate_div').hide();
                $('#transferInformation').show();
                $('#settleSpreed1_div').hide();
                $('#settleSpreed2_div').hide();
                $('#confirmDate').show();
                $('#trdDate').hide();
                $('#unitCprice_div').show();
                if ($('#type6').val() == type_01) {
                    $('#carryAmt_div').show();
                    $('#netDate_div').hide();
                } else {
                    $('#netDate_div').show();
                    $('#netDate').change();
                }
                $('#purchaseFee_div').show();
                $('#purchase').show();
                $('#redemp').hide();
                $('#tradeSha_div').show();
                $('#shareAmt').attr("disabled", true);
                $('#tradeAmt_div').show();
                $('#tradeAmt').attr("disabled", false);
                if (data && data.finprodId && portfolioId && ccy) {
                    getPtlCashPositionInfo();
                }
                $(".channelAmt_class").show();
                $("#changeToFinprodMarketId_div").hide();
                $('#changeToFinprodName_div').hide();
                $('#netDate').change();
            } else if (ps == ps_mc) {
                $('#proAvailableCash_div').hide();
                $('#proAvailableCash1_div').hide();
                // $('#accAvailableCash_div').hide();
                // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                $('.channel_cash').hide();
                // <!-- 通道交易 新增 强成西 2019/03/26 end -->
                $('#assetAvailablePosition_div').show();
                $('#expectedArrivalDate_div').hide();
                $('#transferInformation').show();
                $('#settleSpreed1_div').hide();
                $('#settleSpreed2_div').hide();
                $('#confirmDate').show();
                $('#trdDate').hide();
                if ($('#type6').val() == type_01) {
                    $('#carryAmt_div').show();
                    $('#netDate_div').hide();
                } else {
                    $('#carryAmt_div').hide();
                    $('#netDate_div').show();
                    $('#netDate').change();
                }
                $('#unitCprice_div').show();
                $('#purchaseFee_div').show();
                $('#purchase').hide();
                $('#redemp').show();
                $('#tradeSha_div').show();
                $('#shareAmt').attr("disabled", false);
                $('#tradeAmt_div').show();
                $('#tradeAmt').attr("disabled", true);
                if (data && data.finprodId && portfolioId) {
                    getPtlSecPositionInfo();
                }
                $(".channelAmt_class").show();
                $("#changeToFinprodMarketId_div").hide();
                $('#changeToFinprodName_div').hide();
                $('#netDate').change();
            }
            <!-- 通道交易 新增 强成西 2019/03/26 start -->
            changeTradeAmtAndFee();
            <!-- 通道交易 新增 强成西 2019/03/26 end -->

            // 交易方向变更 清空交易对手 yjm 2019/07/01 start
            if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
                var 交易对手 = $('#trdInvestPersonInfo_counterId').select2('data')[0].object;
                if (typeof 交易对手 != 'undefined') {
                    if ((交易对手 != 'N' && (ps == ps_mc || ps == ps_mr) && !isTradeMarketDeal())
                        || (ps && 交易对手 && 交易对手 != 'S' && ps != ps_mc && ps != ps_mr)) {
                        clearTrader();
                    }
                }
            }
            selectManeger();
            //交易方向变更 清空交易对手 yjm 2019/07/01 end
            // 画面控制联动处理
            pageItemDisplay();
        });

        /**
         * 基金类型
         */
        $('#type6').on('change', function () {
            var ps = $('#ps').val();
            if ($('#type6').val() == type_01 && (ps == ps_mc || ps == ps_mr)) {
                $('#carryAmt_div').show();
            } else {
                $('#carryAmt_div').hide();
            }
            if ($('#type6').val() == type_01 && (ps == ps_mc || ps == ps_mr)) {
                if (!initFlag && !isTradeMarketFundMrMcDeal()) {
                    $('#unitCprice').val(1).change();
                } else if (!initFlag) {
                    $('#unitCprice').val('').change();
                }
                $('#unitCprice').trigger('blur');
                $('#netDate_div').hide();
            } else if (ps == ps_mc || ps == ps_mr) {
                if (!initFlag) {
                    $('#unitCprice').val('').change();
                }
                $('#netDate_div').show();
            }
        });

        /**
         * 组合代码
         */
        $('#portfolioId').change(function () {
            var portfolioId = $('#portfolioId').val();
            var ps = $('#ps').val();
            var data = $('#finprodId').select2('data')[0].object;
            var ccy = $('#ccy').val();
            if ((ps == ps_T04 || ps == ps_mr || ps == ps_T52) && data && data.finprodId && portfolioId) {
                getPtlCashPositionInfo();
            }
            if (((ps == ps_T06 || $("#ps").val() == T65) || ps == ps_mc) && data && data.finprodId && portfolioId) {
                getPtlSecPositionInfo();
            }
        });

        /**
         * 清算速度change事件
         */
        $('#settleSpreed').change(function () {
            var settleSpeed = $('#settleSpreed').val();
            var tradeDate = $('#tradeDate').val();
            if ($('#ps').val() == ps_T52) {

            } else if ($('#ps').val() == ps_T52 && (!settleSpeed || !tradeDate)) {
                return;
            }
            //计算确认日期
            calcCfmDate();
            if (pageModel == PAGE_MODEL_NEW) {
                /*if (settleSpeed && settleSpeed != '' && tradeDate && tradeDate != ''){
                    /!*var num = 0;
                    if (settleSpeed === settleSpreed_T1){
                        num = 1;
                    }else if(settleSpeed === settleSpreed_T2){
                        num = 2;
                    }*!/
                    var num = settleSpeed;
                    $('#expectedArrivalDate').val(DateUtil.parse(tradeDate).DateAdd('d', num).Format('yyyy-MM-dd')).trigger('change');
                }
                if($("#settleSpreed_div").is(":hidden")){
                    var num = 0;
                    $('#expectedArrivalDate').val(DateUtil.parse(tradeDate).DateAdd('d', num).Format('yyyy-MM-dd')).trigger('change');
                }*/
            }
        });

        /**
         * 净值日期
         */
        $('#netDate').change(function () {
            var netDate = $('#netDate').val();
            var data = $('#finprodId').select2('data')[0].object;
            var tradeAmt = $('#tradeAmt').val();
            var type6 = $('#type6').val();
            if (isTradeMarketFundMrMcDeal()) {
                return;
            }
            if ((!initFlag || $('#entrustId').val()) && netDate && data && data.finprodId && type6 == type_02) {
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
                	data =  {finprodId:data.finprodId,netDate:netDate,tradeId:tempTradeId,workflowId,tempWorkflowId};
                }
                else{
                	data =  {finprodId:data.finprodId,netDate:netDate};
                }
                Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade003/getPrice', data, function (result) {
                    $('#unitCprice').val(result).trigger('blur').change();
                });
            } else if ((!initFlag || $('#entrustId').val()) && type6 == type_01 && $('#ps').val() != ps_mr && $('#ps').val() != ps_mc) {
                if (data && data.etfType == ETF_TYPE_06) {
                    $('#unitCprice').val(100).trigger('blur');
                } else if (data && data.trmFundType == TRM_FUND_TYPE_05) {
                    $('#unitCprice').val(0.01).trigger('blur');
                } else {
                    $('#unitCprice').val(1).change();
                    $('#unitCprice').trigger('blur');
                }
            }
        });
        /**
         * 单位净价
         */
        $('#unitCprice').change(function () {
            var unitCprice = $('#unitCprice').val();
            var tradeAmt = $('#tradeAmt').val();
            var ps = $('#ps').val();
            var shareAmt = $('#shareAmt').val();
            if (!initFlag && !isTradeMarketDeal()) {
                if (ps == ps_mr) {
                    if (unitCprice != null && unitCprice != '' && unitCprice != 0 && tradeAmt) {
                        $('#shareAmt').val(tradeAmt / unitCprice).trigger('blur');

                    }
                } else if (ps == ps_mc) {
                    if (unitCprice != null && unitCprice != '' && shareAmt) {
                        $('#tradeAmt').val(shareAmt * unitCprice).trigger('blur');
                        $('#tradeAmtHid').val(shareAmt * unitCprice);
                    }
                }
            } else if (!initFlag) {
                var unitCprice = $('#unitCprice').val();
                var tradeAmt = $('#tradeAmt').val();
                var ps = $('#ps').val();
                var shareAmt = $('#shareAmt').val();
                var carryAmt = $('#carryAmt').val();
                if (!carryAmt) {
                    carryAmt = 0;
                }
                if (unitCprice && shareAmt) {
                    calcTradeAmt();
                }
            }
        });

        /**
         * 交易金额
         */
        $('#tradeAmt').change(function () {
            if (!initFlag && !isTradeMarketFundMrMcDeal()) {
                var feeAmtPay = $('#feeAmtPay').val();
                var tradeAmt = $('#tradeAmt').val();
                var ps = $('#ps').val();
                var unitCprice = $('#unitCprice').val();
                $('#tradeAmtHid').val(tradeAmt);
                if (unitCprice && tradeAmt && ps == ps_mr) {
                    var carryAmt = 0
                    if ($('#carryAmt').val()) {
                        carryAmt = $('#carryAmt').val();
                    }
                    $('#shareAmt').val($('#tradeAmt').val() / unitCprice - carryAmt).trigger('blur');
//                    if($('#type6').val() == type_01){
//                        $('#carryAmt').change();
//                    }
                }
            }
            changeTradeAmtAndFee();
        });
        /**
         * 交易份额
         */
        $('#shareAmt').change(function () {
            calcTradeAmt();
//            var unitCprice = $('#unitCprice').val();
//            var ps = $('#ps').val();
//            var shareAmt = $('#shareAmt').val();
//            if(unitCprice && shareAmt && ps == ps_mc){
//                $('#tradeAmt').val(unitCprice*shareAmt).trigger('blur');
//                changeTradeAmtAndFee();
//                $('#tradeAmtHid').val(unitCprice*shareAmt);
//                if($('#type6').val() == type_01){
//                    $('#carryAmt').change();
//                }
//            }
        });

        /**
         * 待结转份额
         */
        $('#carryAmt').change(function () {
            var carryAmt = $('#carryAmt').val();
            var tradeAmtHid = $('#tradeAmtHid').val();
            if (!carryAmt) {
                carryAmt = 0;
            }
            if (!initFlag) {
                $('#tradeAmt').val(parseFloat(tradeAmtHid) + parseFloat(carryAmt)).trigger('blur');
                changeTradeAmtAndFee();
            }
        });
        $('#entrustId').change(function () {
            var entrustId = $('#entrustId').val();
            if (entrustId) {
                $('#portfolioId').attr('disabled', true);
                $('#ps').attr('disabled', true);
                //add by 马数凯 20210126 ZGBSS-280 start
                $('#chlId').attr('disabled', true);
                //add by 马数凯 20210126 ZGBSS-280 end
                var data = $('#finprodId').select2('data')[0].object;
                if (data && data.finprodId) {
                    $('#finprodId').attr('disabled', true);
                } else {
                    $('#finprodId').attr('disabled', false);
                }
                $('#changeToFinprodMarketId').prop('disabled', true);
            } else {
                $('#portfolioId').attr('disabled', false);
                $('#ps').attr('disabled', false);
                $('#finprodId').attr('disabled', false);
                //add by 马数凯 20210126 ZGBSS-280 start
                $('#chlId').attr('disabled', false);
                //add by 马数凯 20210126 ZGBSS-280 end
            }
        });

        $("#changeToFinprodMarketId").change(function () {
            if ($("#changeToFinprodMarketId").val() && $('#changeToFinprodMarketId').select2('data')[0].text) {
                $("#changeToFinprodName").val($('#changeToFinprodMarketId').select2('data')[0].id);
            } else {
                $("#changeToFinprodName").val("")
            }
        })

        $("#purCfmDate").change(function () {
            $("#expectedArrivalDate").val( $("#purCfmDate").val()).trigger("change");
        })

        // zhangnian add 20190917 交易日期+清算速度 联动计算确认日期 start
        var calcCfmDate = function () {
            var selectData = $('#finprodId').select2('data')[0].object;
            if ($('#ps').val() == ps_T52) {
                if (selectData) {
                    if (pageModel == PAGE_MODEL_NEW) {
                        $("#purCfmDate").val(selectData.vdate).trigger("change");
                        return;
                    }
                }
            } else {
                if (isTradeMarketDeal() && $('#tradeDate').val()) {
                    if ($('#type6').val() == type_01 ||
                        (selectData.etfType == ETF_TYPE_03 || selectData.etfType == ETF_TYPE_04 || selectData.etfType == ETF_TYPE_05)) {
                        $('#settleSpreed').val("0");
                        if (pageModel == PAGE_MODEL_NEW) {
                            $("#purCfmDate").val($('#tradeDate').val()).trigger("change");
                        }
                        $("#expectedArrivalDate").val($('#tradeDate').val()).trigger("change");
                        return;
                    } else {
                        $('#settleSpreed').val("1");
                    }
                }
                var tradedate = $('#tradeDate').val();
                var settleSpeed = $('#settleSpreed').val();
                var calendarId = $('#calendarId').val();
                if (settleSpeed && tradeDate) {
                    Joyin.ajaxSync(basePath + 'invest/trade/TrdCommonController/getNextWorkDay', {
                        'calendarId': calendarId,
                        'tradeDate': tradedate,
                        'settleSpreed': settleSpeed
                    }, function (result) {
                        if (pageModel == PAGE_MODEL_NEW) {
                            $("#purCfmDate").val(result).trigger("change");
                            //update by zhangnian 20191102  start 预计到账日需要节假日推迟
                            $("#expectedArrivalDate").val(result).trigger("change");
                            //update by zhangnian 20191102  end 预计到账日需要节假日推迟
                        }
                    });
                }
            }
        }
        // zhangnian add 20190917 交易日期+清算速度 联动计算确认日期 end

        window.componentLoadCallback = function (isDone, pagePath, params) {
            if (isDone) {
                if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel == PAGE_MODEL_NEW) {
                    riskparam.data = null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
                }
                window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                    var selectData = $('#finprodId').select2('data')[0].object;
                    if (!selectData) {
                        Joyin.error("请选择资产代码。")
                        return;
                    }
                    var data = {};
                    data.counterId = $('#trdInvestPersonInfo_counterId').val();
                    if (($("#ps").val() == ps_mr || $("#ps").val() == ps_mc) && $("#tradeAmtAndFee").val()) {
                        data.tradeAmt = $("#tradeAmtAndFee").val();
                    } else {
                        data.tradeAmt = $("#tradeAmt").val();
                    }
                    data.profitType = $('#profitType').val();
                    data.ps = $('#ps').val();
                    data.finprodId = selectData.finprodId;
                    data.ccy = $('#ccy').val();
                    data.tradeDate = $('#tradeDate').val();
                    data.finprodType2 = $('#finprodType2').val();
                    data.profitType = $('#profitType').val();
                    // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                    data.chlId = $('#chlId').val();
                    // <!-- 通道交易 新增 强成西 2019/03/26 end -->
                    data.tradeMarket = $('#tradeMarket').val();
                    data.secManageAcctId = $('#secManageAcctId').val();
                    if (($("#tradeMarket").val() == TRADE_PLACE_03 || $("#tradeMarket").val() == TRADE_PLACE_04)
                        && !$('#chlId').val() && !$('#secManageAcctId').val()) {
                        Joyin.error("交易所基金交易时，" + JoyinUtil.getMessage('TRD_E_0014_1',JoyinUtil.getMessage('invest.channelId')))
                        return;
                    }
                    if (!($('#ps').val() == ps_mr || $('#ps').val() == ps_mc)
                        && ($("#tradeMarket").val() == TRADE_PLACE_03 || $("#tradeMarket").val() == TRADE_PLACE_04)
                        && !$('#chlId').val() && !$('#secManageAcctId').val()) {
                        Joyin.error("交易所基金交易时，" + JoyinUtil.getMessage('TRD_E_0014_1',JoyinUtil.getMessage('invest.channelId')))
                        return;
                    }
                    return {
                        'isTotalTrade': YES_OR_NO_N
                        , 'bankLendingType': null
                        , 'portfolioId': $("#portfolioId").val()
                        , 'deal': data
                    };
                });
                if (pageModel == PAGE_MODEL_EDIT) {
                    window.BussTrdTransferOrder['bussTrdTransferOrder'].saveCalcCondition()
                }
                $("#trdInvestPersonInfo_counterId").on("change", counterIdOnChange);
                pageItemDisplay();
                initFlag = false;
                calcTradeAmt()
                if(pageModel == PAGE_MODEL_NEW){
                    selectManeger();
                }
            }
        }

        $('#ps').change();
        $('#type6').change();
        $('#finprodId').change();
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
    $(function () {
        //初始化画面
        init();
        // 是否显示划款指令
        if(GEN_PAY_ORDERS_AUTO=='01'){
        	$('#transferInformation').hide() ;
        }else{
        	$('#transferInformation').show() ;
        }
        // 绑定按钮事件
        $('#TrdFundInvestTrade002_btn_save').click(function () {
            saveOrSubmit('save');
        });
        $('#TrdFundInvestTrade002_btn_submit').click(function () {
            saveOrSubmit('submit');
        });
        $('#TrdFundInvestTrade002_btn_view').click(function () {
            var data = $('#finprodId').select2('data')[0].object;
            if (data && data.finprodId) {
                var finprodId = data.finprodId;
                Joyin.dialog(JoyinUtil.getLabel("FinFundTarget002.detail"),
                    basePath + "invest/asset/fund/FinFundTarget002/init",
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        finprodId: finprodId
                    });
            }
        });
        //风险试算
        $('#TrdFundInvestTrade002_btn_risk').click(function(){
            //window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 start----------
            //---赎回申请不需要风险试算----
            if($("#ps").val()==ps_T06){
                return;
            }
            var object = $.serializeToObject('#submitForm', true);
            if(object.shareAmt && object.unitCprice && !object.tradeAmt){
                object.tradeAmt = Number(object.shareAmt)*Number(object.unitCprice);
            }
            var data = $('#finprodId').select2('data')[0].object;
            if (data && data.finprodId) {
                object.finprodId = data.finprodId;
                object.finprodMarketId = $('#finprodId').val();
            }
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/1/RSK002/F02', object,getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 end----------
            window.BaseRiskMathComm.initBaseRiskMathCommPageCus(getriskdata());

        })
        //是否展示资产四五级分类
        if (IS_SHOW_TYPE == '01') {
            $('#selector5').show();
        } else {
            $('#selector5').hide();
        }

        // add by etliu 20210501 银行间场内场外拆分 start
        // mody by meifaxin 20211105 银行间场内场外拆分,在html中判断 start
        // if (parentPageId) {
        //     if (parentPageId == 'TrdFundInvestTradeEx001' || parentPageId == 'TrdFundInvestTrade001') {
        //         $('#trmFundAbbr_div').show();
        //         $('#trmFundType_Div').show();
        //     } else {
        //         $('#trmFundAbbr_div').hide();
        //         $('#trmFundType_Div').hide();
        //     }
        // }
        // add by etliu 20210501 银行间场内场外拆分 end
        // mody by meifaxin 20211105 银行间场内场外拆分,在html中判断 end
    });

}());
