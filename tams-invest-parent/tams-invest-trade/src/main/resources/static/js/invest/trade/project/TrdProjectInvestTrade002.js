(function () {
        var entrustPrinAmt;

        var initFlag = true;
        var getriskdata = function () {
            var obj = {
                timePoint: MY_SUP_TIME_TYPE_02,
                valDate: $('#settleDate').val(),
                portfolioCode: $('#portfolioId').val(),
                tradeAmount: $('#tradeAmt').val()==0?null:$('#tradeAmt').val(),
                assetType: FINPROD_TYPE_F18,
                isEnabled: '01',
                assetCode:$('#finprodId').val(),
                tradeDirection:$('#ps').val()
            }
            //---add by 裴家俊  2019/03/20  添加资产类型条件---start-----
            if($('#projectType').val() && $('#projectType').val() != ''){
                obj.assetSecType = $('#projectType').val();
            }
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#trdInvestPersonInfo_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            //---add by 裴家俊  2019/03/20  添加资产类型条件---end-----
            var object = {
                    assetCode: ['finprodMarketId', 'TrdBondInvestTrade002.finprodId'],
                    valDate: ['settleDate', 'TrdPlanRateInvestTrade002.settleDate'],
                    portfolioCode: ['portfolioId', 'TrdBondInvestTrade002.portfolioId'],
                    tradeAmount: ['tradeAmt', 'TrdProjectInvestTrade002.fpriceAmt'],
                    assetType: ['projectType', 'TrdDepositInvestTrade001.finprodType'],
                    tradeDirection:['ps','TrdLendInvestTrade002.ps'],
                    data: obj
            }
            // add by zhangrui1 交易对手影响风险试算 start
            object.counterId = ['counterId', 'TrdInvestPersonInfo.counterId'];
            // add by zhangrui1 交易对手影响风险试算 end
            return object;
        }
        /**
         * 画面初始化
         */
        function init() {
            if ($("#entrustId").val()) {
                entrustPrinAmt = $("#prinAmt").val();
                $("#portfolioId").prop("disabled", true).trigger("change");
                $("#finprodMarketId").prop("disabled", true).trigger("change");
                setInvestRate($("#finprodId").val());
                $("#ps").prop("disabled", true).trigger("change");
                $("#settleSpeed").prop("disabled", true).trigger("change");
                $("#commissionRate").prop("disabled", false).trigger("change");
                //代志伟  2019-07-18 手续费根据手续费率带出，不可编辑
                $('#unitCprice').trigger('blur');
                $("#cpriceAmt").val(entrustPrinAmt).trigger("blur");
            }
            if (ps == T40) {
                $("#ps").prop("disabled", true).trigger("change");
                $("#ps").next().find("li:lt(2)").remove();
            }
            else {
                $("#ps").next().find("li:eq(2)").remove();
            }
            setInvestRate($("#finprodId").val());
            if($("#chlId")){
            	var finprodId = null;
                if($("#finprodMarketId").val()){
                	finprodId= $("#finprodMarketId").find("option:selected").data("data").object.finprodId
                }
                ChannelAssetCommon.setChannelSelectObject($("#chlId"), finprodId, $("#entrustId").val(), function () {

                });
            }
            initFlag =false;

            //根据参数 选择显示投资目的或者业务模式
            // if(SHOW_INVAIM_OR_BUSSINSSMODEL=="02"){
            //    $("#invAimDiv").hide();
            //    $("#businessModelDiv").show();
            // }else{
            //    $("#invAimDiv").show();
            //    $("#businessModelDiv").hide();
            // }
            setRepayFlag($("#finprodId").val());
        }

        /**
         * 获取持仓、现金余额、可用现金等
         * @param object json对象
         * @returns 获取持仓、现金余额、可用现金等
         */
        function available(){
            var object = getParameters();
            if(object.portfolioId != null && object.portfolioId != '' && object.ps != null && object.ps != '' && object.ccy != null && object.ccy != '' && object.tradeDate != null){
                Joyin.ajax(basePath+ "invest/trade/trddepositinvesttrade/TrdDepositInvestTrade002/available",object,function(result){
                    if(object.ps == T01){
                        $('#bussTrdAvailableCashQuery_avalibleCash').val(result[0].name).trigger('blur');
                        $('#bussTrdAvailableCashQuery_avalibleCashT1').val(result[1].name).trigger('blur');
                        window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].updateSecAcctBalance(function () {
                            return {
                                'ccy': $("#ccy").val(),
                                'portfolioId': $("#portfolioId").val(),
                                'chlId': $("#chlId").val(),
                                'tradeDate':$('#tradeDate').val()
                            }
                        });
                        $('#bussTrdAvailableCashQuery_channelAvailableCash').val(result[3].name).trigger('blur');
                        if(result[3].name == 'Y'){
                            isHoliday = result[3].name;
                        }
                    }else{
                        $('#bussTrdAvailableCashQuery_assetPosition').val(result[0].name).trigger('blur');
                        if(result[1].name == 'Y'){
                            isHoliday = result[1].name;
                        }
                    }
                });
            }
        }

        /**investRate
         * 利息总额计算
         */
        var intAmtCalcu = function () {
            if (pageModel == PAGE_MODEL_REFERENCE) {
                return;
            }
            $('#intAmt').val('');
            $("#unitIntAmt").val('');
            $("#unitFprice").val('');
            var object = {
                portfolioId: $('#portfolioId').val(),
                finprodId: $("#finprodId").val(),
                settleDate: $('#settleDate').val(),
                basis: $('#basis').val(),
                investRate: $('#investRate').val().valueOf() / 100,
                prinAmt: $('#prinAmt').val()
            }
            if (object.finprodId && object.investRate && object.settleDate && object.prinAmt) {
                Joyin.ajaxJson(basePath + "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getIntAmt", object, function (result) {
                    $('#intAmt').val(result.data).trigger("blur");
                    //加上精度计算，  yjm 20190725
                    var unitIntAmt = JoyinMath.doDiv(JoyinMath.doMul(result.data*1, 100, 6), object.prinAmt*1, 4);
                    $("#unitIntAmt").val(unitIntAmt).trigger('blur');
                    $("#unitFprice").val(JoyinMath.doAdd(unitIntAmt, $("#unitCprice").val(), 6)).trigger('blur');
                    //卖出时减去手续费 yjm start 20190903
                    var commissionAmt = $('#commissionAmt').val();
                    var ps = $('#ps').val();
                    if (ps && ps != T01) {
                        //卖出
                        commissionAmt = commissionAmt * -1;
                    }
                    var tradeAmt =JoyinMath.doAdd(JoyinMath.doAdd(result.data*1, object.prinAmt*1), commissionAmt, 6);
                    if(ps && ps != T01 && $("#payUnpaidintAmt").val() && $("#ispayUnpaidint").val() && $("#ispayUnpaidint").val()==YES_OR_NO_Y){
                        tradeAmt = JoyinMath.doAdd(tradeAmt, $("#payUnpaidintAmt").val())
                    }
                    //卖出时减去手续费 yjm start 20190903
                    $("#tradeAmt").val(tradeAmt).trigger("blur");
                });
            }
        }

        function portfolioIdChange() {
            available();
            unPayIntAmtCal();
        }

        function ccyChange() {
            available();
        }

        function projectAssetView() {
            var finprodId = $("#finprodId").val();
            Joyin.dialog(JoyinUtil.getLabel("FinProjectTargetAsset002.title"), basePath + "invest/asset/project/FinProjectTargetAsset002/init",
                "98%", "98%", {
                    pageModel: PAGE_MODEL_REFERENCE,
                    finprodId: finprodId
                });
        }

        function prinAmtChange() {
            var prinAmt = $("#prinAmt").val();
            $("#unitCprice").val(100);
            if (prinAmt) {
                $("#cpriceAmt").val(prinAmt).trigger("blur");
                intAmtCalcu();
                getCommissionAmt();
            }

        }

        $('#ispayUnpaidint').on('change',function(){
            intAmtCalcu()
        });

        $('#payUnpaidintAmt').on('change',function(){
           // intAmtCalcu()
        });
        /**
         * 清算速度变更
         */
        function settleSpeedChange() {
            var settleSpeed = $("#settleSpeed").val();
            var tradeDate = $("#tradeDate").val();
            var settleSpeedValue=0;
            if (settleSpeed == SETTLE_SPEED01) {
                $("#settleDate").val(tradeDate);
                //若清算速度由T+1改为T+0的时候也需要进行应计利息的计算 start yjm 20190724
                intAmtCalcu();
                //若清算速度由T+1改为T+0的时候也需要进行应计利息的计算 start end 20190724
            } else {
            	if (settleSpeed == SETTLE_SPEED02) {
                	settleSpeedValue=1;
                }
                else if (settleSpeed == SETTLE_SPEED03) {
                	settleSpeedValue=2;
                }
                var data = $("#finprodMarketId").find("option:selected").data("data");
                var calendarId = data.object ? data.object.calendarId || '' : ''
                if (calendarId && tradeDate) {
                    Joyin.ajax(basePath + "invest/trade/bond/TrdBondInvestTrade002/getNextWorkDay", {
                        tradeDate: tradeDate,
                        calendarId: calendarId,
                        settleSpeed: settleSpeedValue
                    }, function (data) {
                        $("#settleDate").val(data);
                        intAmtCalcu();
                    })
                }
            }
        }

        window.componentLoadCallback = function (isDone, pagePath, params) {
            if (isDone) {
                if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel==PAGE_MODEL_NEW){
                    riskparam.data =null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
                }
//                ChannelAssetCommon.setChannelSelect($("#chlId"), finprodId, $("#entrustId").val(), function () {
                    psChange();
                    init();
                    $("#bussTrdAvailableCashQuery_div_channelCash").hide();
                    window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                        if (!$("#tradeAmt").val()) {
                            Joyin.error("请输入实收本息总额")
                            return;
                        }
                        if (!$("#tradeDate").val()) {
                            Joyin.error("请输入资金到账日")
                            return;
                        }
                        if (!$("#ccy").val()) {
                            Joyin.error("请选择币种")
                            return;
                        }
                        if (!$("#finprodId").val()) {
                            Joyin.error("请选择资产")
                            return;
                        }
                        return {
                            'isTotalTrade': YES_OR_NO_Y
                            , 'bankLendingType': null
                            , 'portfolioId': $("#portfolioId").val()
                            , 'deal': {
                                finprodType2: F18,
                                ps: $("#ps").val(),
                                tradeDate: $("#settleDate").val(),
                                tradeAmt: $("#tradeAmt").val(),
                                finprodId: $("#finprodId").val(),
                                chlId: $("#chlId").val(),
                                counterId: $("#trdInvestPersonInfo_counterId").val(),
                                ccy: $("#ccy").val()
                            }
                        };
                    });
//                });
                  //交易对手改变时  影响通道费金额，内部交易对手随交易支付，外部交易对手根据收取方式
                    $('#trdInvestPersonInfo_counterId').on("change",function(){
                    	checkChannelFee();
                    	commissionAmtChange();

                        var counterId = $('#trdInvestPersonInfo_counterId');
                        if (counterId.select2('data') && counterId.select2('data')[0]) {
                            var 交易对手 =  counterId.select2('data')[0].object;
                            var ps = $('#ps').val();
                            // 交易对手选择内部交易对手时
                            if (交易对手 == 'N' && ps == T01) {
                                changeTurnInvAim(true);
                            } else {
                                changeTurnInvAim(false);
                            }
                        }
               /*         var counterId = $('#trdInvestPersonInfo_counterId');
                        if (counterId.select2('data') && counterId.select2('data')[0]) {
                            var 交易对手 = counterId.select2('data')[0].object;
                            // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                            if (交易对手 == 'N') {
                                if (pageModel != PAGE_MODEL_REFERENCE) {
                                    $('#settleSpeed').val("01").prop("disabled", true).trigger("change");
                                }
                            } else {
                                if (pageModel != PAGE_MODEL_REFERENCE) {
                                    $('#settleSpeed').prop("disabled", false).trigger("change");
                                }
                            }
                        }*/
                    });
                if (workflowId && pageModel == '1'){
                    window.BussTrdTransferOrder['bussTrdTransferOrder'].saveCalcCondition();
                }
            }
        }
        
        function tradeAmtChange() {
            /*买入 全价总额 = 实付金额 - 费用 ； 卖出 全价总额 = 实付金额 + 费用*/
            //update by 张年  20190827 start
            var tradeAmt = $("#tradeAmt").val();
            var ps = $('#ps').val();
            var channelFee = $("#channelFee").val() || 0;
            //手续费
            var commissionAmt = $("#commissionAmt").val()||0;
           if (ps == T01) {
//                channelFee = channelFee * -1;
               commissionAmt = commissionAmt * -1;
           }
            //卖出时存在通道费 其他情况无通道费
//            if (ps == T02) {
//                channelFee = channelFee * -1;
//                commissionAmt = commissionAmt * -1;
//            }else{
//            	channelFee = 0;
//            }
          //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
            // channelFee = 0;
            //update by panbin 2020-3-27 start
            var data = $('#chlId').find('option:selected').data('collecttype');
            if(ps==T01){
                //买入时不计算通道费
                channelFee=0;
            }
            else if (ps==T02 && data!=collectType_01){
                //卖出时 无通道或通道的费用收取方式不是回款前扣除
                channelFee=0;
            }
            else if (ps==T02 && data && data==collectType_01) {
                channelFee = channelFee * -1;
            }
            //update by panbin 2020-3-27 end
            tradeAmt =JoyinMath.doAdd(JoyinMath.doAdd(tradeAmt, channelFee), commissionAmt);
            if(ps && ps != T01 && $("#payUnpaidintAmt").val() && $("#ispayUnpaidint").val() && $("#ispayUnpaidint").val()==YES_OR_NO_Y){
                tradeAmt = JoyinMath.doAdd(tradeAmt, $("#payUnpaidintAmt").val())
            }

            //update by 张年  20190827 end
            var intAmt = $("#intAmt").val();
            var prinAmt = $("#prinAmt").val();
            if (tradeAmt && intAmt) {
                var cpriceAmt = JoyinMath.doMinus(tradeAmt, intAmt);
                $("#cpriceAmt").val(cpriceAmt).trigger('blur');
                if (prinAmt) {
                    var oldUnitCprice = $("#unitCprice").val();
                    var unitCprice = JoyinMath.doMul(JoyinMath.doDiv(cpriceAmt, prinAmt, 8), 100, 6);
                    $("#unitCprice").val(unitCprice).trigger("blur");
                    var unitIntAmt = $("#unitIntAmt").val();
                    if (unitIntAmt) {
                        $("#unitFprice").val(JoyinMath.doAdd($("#unitIntAmt").val(), $("#unitCprice").val(), 6)).trigger('blur');
                    }
                }
            }                
        }

        var commissionAmtChange = function () {
            if (pageModel == PAGE_MODEL_REFERENCE) {
                return;
            }
            var ps = $('#ps').val();
            var commissionAmt = $("#commissionAmt").val();
//            var channelFee = $("#channelFee").val();
            var channelFee = 0;
            var prinAmt = $("#prinAmt").val();
            var intAmt = $("#intAmt").val();
            var tradeAmt = JoyinMath.doAdd(intAmt, prinAmt);            
            if(ps == T40&&initFlag){
            	return;
            }
            var data = $("#chlId").find("option:selected").data("collecttype");
           if(ps==T01){
           	//买入时不计算通道费
           	channelFee=0;
           }else if (($("#trdInvestPersonInfo_counterId").val()&&$("#trdInvestPersonInfo_counterId").select2('data')[0].object=='N')
          		 ||($("#trdInvestPersonInfo_counterId").val()&&$("#trdInvestPersonInfo_counterId").select2('data')[0].object!='N'
          			&& ps==T02 && data && data==collectType_01)){
           	//卖出时 且通道存在 通道收取方式为回款前扣除
           	channelFee = $("#channelFee").val();
           }else if(ps==T02 && data && data==collectType_01 && !$("#trdInvestPersonInfo_counterId").val()){
           	//卖出时 且通道存在 通道收取方式为回款前扣除
           	channelFee = $("#channelFee").val();
           }
          //2019-12-24 通道费改造 标的挂通道  通道费不参与计算    卖出 通道回款前扣除 通道费参与计算update bya panbin 2020-3-27
            if (ps == T01) {
                $("#tradeAmt").val(JoyinMath.doAdd(JoyinMath.doAdd(tradeAmt, commissionAmt), channelFee)).trigger("blur").trigger("change");
            }
            else {
                var tradeAmt=JoyinMath.doAdd(JoyinMath.doMinus(tradeAmt, commissionAmt), channelFee);
                if(ps && ps != T01 && $("#payUnpaidintAmt").val() && $("#ispayUnpaidint").val() && $("#ispayUnpaidint").val()==YES_OR_NO_Y){
                    tradeAmt = JoyinMath.doAdd(tradeAmt, $("#payUnpaidintAmt").val())
                }
                $("#tradeAmt").val(tradeAmt).trigger("blur").trigger("change");
            }
        }

        function psChange(isChannelChanged) {
            var ps = $('#ps').val();
            if (ps == T01) {
                window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showCash(true);
                $("#commissionRate").prop("disabled", false).trigger("change");
            } else {
                window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'] && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showPosition(true);
                //需求中要求卖出时手续费率可输入 yjm start 20190814
                //  $("#commissionRate").prop("disabled", true).val('').trigger("change");
                $("#commissionRate").prop("disabled", false).trigger("change");
                //需求中要求卖出时手续费率可输入 yjm end 20190814
                // $("#commissionAmt").prop("disabled", false).trigger("change");
            }
            //卖出时显示通道费
            if(ps&&(ps == T02)){
            	// $("#channelFee_div").show();
            }else{
            	$("#channelFee_div").hide();
            	$("#channelFee").val("");
            }
            commissionAmtChange();
            if (isChannelChanged !== 'true') {
                available();
            }
            checkChannelFee();
        }
        
        /**
	     * 标的挂通道 内转交易 通道费置灰
	     * 外部卖出显示可填  其他置灰
         */
        var checkChannelFee = function(){
            var ps = $('#ps').val();
            if(ps==T02&&!$('#trdInvestPersonInfo_counterId').val()){
            	$("#channelFee").attr('disabled',false);
            }
            else if(ps==T02&&$('#trdInvestPersonInfo_counterId').val()&&$("#trdInvestPersonInfo_counterId").select2('data')[0].object!='N'){
            	$("#channelFee").attr('disabled',false);
            }
            else{
            	$("#channelFee").val("").attr('disabled',true);
            }
        }

        /**
         * 获取查询信息
         */
        var getParameters = function (){
            if(!finprodId){
                var optionData = $("#finprodMarketId").find("option:selected").data("data");
                finprodId = optionData.object ? optionData.object.finprodId || '' : ''
            }
            if (!finprodId) {
                return {};
            }
            return{
                finprodId: finprodId,
                portfolioId:$('#portfolioId').val(),
                ps:$('#ps').val(),
                chlId:$('#chlId').val(),
                secAcctId:$('#chlId').val(),
                tradeDate:$('#tradeDate').val(),
                ccy:$('#ccy').val()
            };
        };

        function finprodMarketIdChange() {
            var optionData = $("#finprodMarketId").find("option:selected").data("data");
            if (optionData && optionData.object) {
                finprodId = optionData.object ? optionData.object.finprodId || '' : ''
            	$("#finprodId").val(finprodId);
                $("#finprodName").val(optionData.object.finprodId);
                $("#ccy").val(optionData.object.ccy).trigger("change");
                $('#finmdate').val(new Date(optionData.object.mdate));
                settleSpeedChange();
                var finprodType = optionData.object ? optionData.object.finprodType || '' : ''
                if (finprodType) {
                    $("#projectType").val(finprodType).trigger("change");
                }
                // 项目类资产五级分类改造 add by renping 2019-08-01 start
                var type9 = optionData.object ? optionData.object.type9 || '' : ''
                	if (type9) {
                		$("#type9").val(type9).trigger("change");
                	}
                var type10 = optionData.object ? optionData.object.type10 || '' : ''
                	if (type10) {
                		$("#type10").val(type10).trigger("change");
                	}
                var type11 = optionData.object ? optionData.object.type11 || '' : ''
                	if (type11) {
                		$("#type11").val(type11).trigger("change");
                	}
                // 项目类资产五级分类改造 add by renping 2019-08-01 end
                setInvestRate(finprodId, true);
                setRepayFlag(finprodId);
                ChannelAssetCommon.setChannelSelectObject($("#chlId"), finprodId, $("#entrustId").val(), function () {

                });
            } else {
                    $('#finprodId').val('');
                    $('#finprodName').val('');
                    $('#finmdate').val('');
                    $('#ccy').val('').trigger('change');
                    $('#projectType').val('').trigger('change');
                    $('#type9').val('').trigger('change');
                    $('#type10').val('').trigger('change');
                    $('#type11').val('').trigger('change');
             }
        }

        /**
         * 应计利息总额change事件
         */
        function intAmtChange(){
            psChange();
            $("#fpriceAmt").val(JoyinMath.doAdd($('#intAmt').val(), $("#unitCprice").val(), 6)).trigger('blur');
        }

        $("#chlId").change(available);

        /**
         * 获取投资收益率
         * @param finprodId 金融产品代码
         * @param calcInt 是否计算利息
         */
        function setInvestRate(finprodId, calcInt) {
            if (finprodId) {
                Joyin.ajax(basePath + "invest/trade/project/TrdProjectInvestTrade002/findInvestRate", {
                    finprodId: finprodId, settleDate: $("#settleDate").val()
                }, function (data) {
//                    $("#investRate").val(data.INVEST_RATE).trigger("blur");
                    //加上精度计算，否则会和资产端值不一样 yjm 20190725
                    $("#investRate").val(JoyinMath.doMul(data.INVEST_RATE, 100, 6)).trigger("blur");
                    $("#basis").val(data.BASIS);
                    if (calcInt) {
                        intAmtCalcu();
                    }
                })
            }
        }


        /**
         * 获取投资收益率
         * @param finprodId 金融产品代码
         */
        function setRepayFlag(finprodId) {
            if (finprodId) {
                var object = {
                    finprodId: finprodId,
                    settleDate: $('#settleDate').val()
                }
                Joyin.ajaxJson(basePath + "invest/trade/project/TrdProjectInvestTrade002/findRepayFlag", object, function (result) {
                    if(result.repayIntFlag===YES_OR_NO_N){//是还本不带息场景，显示累计未付利息
                        //$('#repayIntAmt_div').show();
                        unPayIntAmtCal();//初始化累计还本不带息金额
                        $('#ispayUnpaidint').val(YES_OR_NO_Y).trigger('change');
                    }else{//不是则隐藏
                       // $('#repayIntAmt_div').hide();
                        $('#payUnpaidintAmt').val('').trigger('blur');
                        $('#ispayUnpaidint').val('').trigger('blur');
                    }
                })
           }
        }

        function saveOrSubmit(operateType) {
        	// 划款指令隐藏时，自动生成划款指令信息
        	generateOrders(function () {
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
	            // 检查生成划款指令参数是否发生变化
	            if (window.BussTrdTransferOrder['bussTrdTransferOrder'].isCalcConditionChanged()) {
	                return;
	            }
                //校验转仓投资目的 add by yeming 20210823 start
                var counterId = $('#trdInvestPersonInfo_counterId');
                if(counterId.select2('data')[0].object == 'N' && $('#ps').val() == T01){
                    if (!$("#turnInvAim").val()) {
                        Joyin.error("请选择转仓投资目的")
                        return;
                    }
                }else{
                    if ($("#turnInvAim").val()) {
                        Joyin.error("转仓投资目的应置空")
                        return;
                    }
                }
                //校验转仓投资目的 add by yeming 20210823 end
	            /**
	             * 获取列表参数
	             */
	            var object = $.serializeToObject('#informationForm', true);
	            if ($('#select2-finprodMarketId-container').attr('title')&&$("#finprodName").val() != null && $("#finprodName").val() != ''){
	                object.finprodName = $('#select2-finprodMarketId-container').attr('title');
	            }
	            if (FileCommon) {
	                object.uploadFileList = FileCommon.getUploadFileInfo();
	            }
	
	            object.entrustPrinAmt = entrustPrinAmt; // 委托时的本金
	            object.operateType = operateType;
	            
	            // 设置资产可用持仓
	            if (object.bussTrdAvailableCashQuery) {
	                object.bussTrdAvailableCashQuery.assetPosition = $('#bussTrdAvailableCashQuery_assetPosition').val();
	            } else {
	                object.bussTrdAvailableCashQuery = {};
	                object.bussTrdAvailableCashQuery.assetPosition = $('#bussTrdAvailableCashQuery_assetPosition').val();
	            }
	            
	            // 设置交易日历
	            var finprodMarketIdData = $("#finprodMarketId").find("option:selected").data("data");
	            var calendarId = finprodMarketIdData.object ? finprodMarketIdData.object.calendarId || '' : '';
	            var finprodType = finprodMarketIdData.object ? finprodMarketIdData.object.finprodType || '' : '';
	            object.calendarId = calendarId;
	            object.projectType = finprodType;
	            //退回的流程重新提交的时候不用再进行金额校验 yjm  start 20190725
	            if (workflowId){
	                object.workflowId = workflowId;
	            }
	            //退回的流程重新提交的时候不用再进行金额校验 yjm end  20190725
	            if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
	                object.counterType = $('#trdInvestPersonInfo_counterId').select2('data')[0].object;
	                object.counterId=$('#trdInvestPersonInfo_counterId').select2('data')[0].id;
	            }
	            var data = $("#chlId").find("option:selected").data("collecttype");
	            if (data) {
	                object.collectType = data;
	            }
	            data = $("#chlId").find("option:selected").data("paytype");
	            if (data) {
	                object.payType = data;
	            }
	            object.finprodType2 = F18;
	            Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/check', object, function(result1) {
	                Joyin.ajaxJson(basePath + 'invest/trade/project/TrdProjectInvestTrade002/check', object, function (result) {
	                	//授信占用额度
//		                Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {});
	                    Joyin.confirm(result.data, function () {
	                        var elementId;
	                        object.riskCreditListCommonDto = result1.data;
	                        if (operateType === 'save') {
	                            elementId = "TrdProjectInvestTrade002_btn_save";
	                            if (ps == T40) {
	                                elementId = "TrdProjectInvestTrade002_btn_total"
	                            }
	                            WorkFlow.doSaveList(basePath + 'invest/trade/project/TrdProjectInvestTrade002/save', object, elementId);
	                        } else {
	                            elementId = "TrdProjectInvestTrade002_btn_submit";
	                            if (ps == T40) {
	                                elementId = "TrdProjectInvestTrade002_btn_total"
	                            }
	                            WorkFlow.doSubmitList(basePath + 'invest/trade/project/TrdProjectInvestTrade002/save', object, elementId);
	                        }
	                    });
	                },false,function (result) {
                        var disabledFlag = $('#businessModel').attr('disabled');
                        if(disabledFlag) return;
                        var msg = result.errorList;
                        var message ="";
                        for(var i=0;i<msg.length;i++){
                            message = msg[i];
                            if(message.field!=null && message.field=='invAim'){
                                if(message.message!=null && message.message==JoyinUtil.getMessage("ETR_ERROR_0059")) {
                                    $('#businessModel').textSelect('setValue', "FVTPL");
                                    break;
                                }
                            }
                        }

                    });
	              });   
	            })
	        }

        var getCommissionAmt = function(){
            if ($("#commissionRate").val() && $("#prinAmt").val()){
                var commissionRate = new BigNumber($("#commissionRate").val()).toFixed(2, JoyinMath.ROUND_HALF_UP);
                var prinAmt = new BigNumber($("#prinAmt").val()).toFixed(2, JoyinMath.ROUND_HALF_UP);
                $("#commissionAmt").val(JoyinMath.doDiv(JoyinMath.doMul(prinAmt,commissionRate),100)).trigger("change").trigger("blur");
            }
            else {
                $("#commissionAmt").val('').trigger("change");
            }
        }

        var unPayIntAmtCal = function(){
            object = {
                finprodId:$('#finprodMarketId').val(),
                payDate:$('#tradeDate').val(),
                cDate:$('#tradeDate').val(),
                portfolioId:$('#portfolioId').val(),
                secAcctId:$('#chlId').val()
            }

            if (pageModel != PAGE_MODEL_EDIT && pageModel != PAGE_MODEL_NEW){
                return ;
            }
            if(object.finprodId != null && object.finprodId != '' &&
                object.payDate != null && object.payDate != '' &&
                object.portfolioId!=null && object.portfolioId != ''
            ){
                object.finprodId=$('#finprodMarketId').select2("data")[0].object.finprodId;
                Joyin.ajaxJson(basePath+ "invest/trade/project/TrdProjectInvestTrade002/getUnpayIntAmt",object,function(result){
                   // $('#payUnpaidintAmt').val(result.data).trigger('blur');

                });
            }else{
                $('#payUnpaidintAmt').val('').trigger('blur');
            }
        }
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


        var changeTurnInvAim = function(isShow) {
            //校验转仓投资目的 edit by yeming 20210823 start
            if (isShow){
                $("#turnInvAim").prop("disabled", false).trigger("change");
                $("#turnInvAim").val('').trigger("change");
                if($('#invAim').val()){
                    $("#turnInvAim").val($('#invAim').val()).trigger("change");
                }
            } else{
                $("#turnInvAim").prop("disabled", true).trigger("change");
                $("#turnInvAim").val('').trigger("change");
            }
            //校验转仓投资目的 edit by yeming 20210823 end
        }


        /**
         * 画面加载完成
         */
        $(function () {
            $("#commissionRate").change(getCommissionAmt);
            $("#TrdProjectInvestTrade001_btn_view").click(projectAssetView)
            $("#prinAmt").change(prinAmtChange)
            $("#ccy").change(ccyChange)
            $("#ps").change(psChange)
            $("#commissionAmt").change(commissionAmtChange)
            $("#channelFee").change(commissionAmtChange);
            $("#chlId").change(commissionAmtChange);
            $("#portfolioId").change(portfolioIdChange)
            $("#tradeAmt").change(tradeAmtChange)
            $("#settleSpeed").change(settleSpeedChange)
            $("#finprodMarketId").change(finprodMarketIdChange)
            // 是否显示划款指令
            if(GEN_PAY_ORDERS_AUTO=='01'){
            	$('#bussTrdTransferOrder_div').hide() ;
            }else{
            	$('#bussTrdTransferOrder_div').show() ;
            }
            $('#TrdProjectInvestTrade002_btn_save').click(function () {
                saveOrSubmit('save');
            });
            $('#TrdProjectInvestTrade002_btn_submit').click(function () {
                saveOrSubmit('submit');
            });
            //风险试算
            $('#TrdProjectInvestTrade002_btn_risk').click(function () {
                //window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
                //-----------add by jjpei 2020-01-07 风险新版本 start----------
                var object = $.serializeToObject('#informationForm', true);
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/1/RSK002/F06', object,getriskdata());
                //-----------add by jjpei 2020-01-07 风险新版本 end----------
                window.BaseRiskMathComm.initBaseRiskMathCommPageCus(getriskdata());

            })
        });
      //是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
        	$('#selector5').show();
        	}else{
        	$('#selector5').hide();
        	}
        //应计利息change
        $('#intAmt').change(intAmtChange);

        // modify by etliu 20220521 start
        // 3.0.6场内 ZGCPCN-688:项目类资产交易新建画面，提交时提示【请输入投资目的】，但是画面上只有业务模式，并且业务模式只读
        // 修改为初始值有值的时候不可改，为空的时候可改
        if ($('#businessModel').val()) {
            //禁用选项
            $('#businessModel').attr("disabled",true);
        }
        // modify by etliu 20220521 end
    }
    ()
)
;

