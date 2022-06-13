var etrAmt = '';
(function () {
    var fileArray =[];
    var entrustId = '';
    var entity = '';
    var isOpics = '';
    var isHoliday = '';
    var calendarId = '';
    // add ZGCPCN-1007 wsz 20220602 控制页面加载次数只加载一次 start
    var initFlag =false;
    // add ZGCPCN-1007 wsz 20220602 控制页面加载次数只加载一次 end
    var getriskdata = function(){
        var obj = {timePoint:MY_SUP_TIME_TYPE_02,valDate:$('#settleDate').val(),portfolioCode:$('#portfolioId').val(),tradeAmount:$('#tradeAmt').val(),assetType:FINPROD_TYPE_F17,isEnabled:'01',assetCode:$('#finprodMarketId').select2("data")[0].object,tradeDirection:$('#ps').val()}
        if($('#type7').val() && $('#type7').val() != ''){
            obj.assetSecType = $('#type7').val();
        }
        // add by zhangrui1 交易对手影响风险试算 start
        obj.counterId = $('#trdInvestPersonInfo002Dto_counterId').val();
        // add by zhangrui1 交易对手影响风险试算 end
        var object = {assetCode: ['finprodMarketId', 'TrdBondInvestTrade002.finprodId'],valDate:['settleDate','TrdPlanRateInvestTrade002.settleDate'],portfolioCode:['portfolioId','TrdDepositInvestTrade001.portfolioId'],tradeAmount:['tradeAmt','TrdPlanRateInvestTrade002.tradeAmt'],assetType:['finprodType','TrdDepositInvestTrade001.finprodType'],tradeDirection:['ps','TrdDepositInvestTrade001.ps'],data:obj}
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'TrdInvestPersonInfo002Dto.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        return object;
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
            if(bussTrdAvailableCashQuery && bussTrdAvailableCashQuery!=''){
            	window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].setPosition(bussTrdAvailableCashQuery);
            }
            if(window.BussTrdTransferOrder && window.BussTrdTransferOrder['bussTrdTransferOrder']) {
                window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                    if (!$("#tradeAmt").val()) {
                        Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038', "交易金额"));
                        return;
                    }
                    getEntity();
                    entity.chlId = $("#chlId").val();
                    return {
                        'isTotalTrade':YES_OR_NO_Y
                        ,'bankLendingType':null
                        ,'portfolioId':$("#portfolioId").val()
                        ,'deal':entity
                    };
                });
            }
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
            psChange();
            updateCash();
          //交易对手改变时  影响通道费金额，内部交易对手随交易支付，外部交易对手根据收取方式
            $('#trdInvestPersonInfo002Dto_counterId').on("change",function(){
            	checkChannelFee();
            	intAmtCalcu(true);
        /*        var counterId = $('#trdInvestPersonInfo002Dto_counterId');
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
            if($('#finprodMarketId').val()){
                $('#finprodMarketId').trigger('change');
            }
        }
    }


    function getEntity(){
        var object = $.serializeToObject('#basicInfoForm');
        if($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != ''){
            object.finprodId= $("#finprodMarketId").select2("data")[0].object;
        }else if(finprodId != null){
            object.finprodId = finprodId;
        }
        Joyin.ajaxSync(basePath + 'invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getEntity', object,function(result){
            result.createTime = null;
            result.updateTime = null;
            result.tradeId = null;
            result.totleTradeId = null;
            result.cpriceAmt = null;
            result.shareAmt = null;
            result.prinAmt = null;
            result.intAmt = null;
            result.fpriceAmt = null;
            // 输入备注等，点击提交，提示计息要素已变更 add by renping 20191018 start
            result.remark = null;
            result.counterContact = null;
            result.counterTrader = null;
            // 输入备注等，点击提交，提示计息要素已变更 add by renping 20191018 end
            entity = result;
        });
    }
    /**
     * 保存或提交
     * @param operateType 操作类型（save，submit）
     * @returns
     */
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
            /*if(!$('#invAim').val()){
                Joyin.error("请输入投资目的");
                return;
            }*/
	     // 检查生成划款指令参数是否发生变化
	        if (window.BussTrdTransferOrder['bussTrdTransferOrder'].isCalcConditionChanged()) {
	            return;
	        }
	        /**
	         * 获取列表参数
	         */
	        var object = $.serializeToObject('#basicInfoForm',true);
	        if ($('#select2-finprodMarketId-container').attr('title')){
	            object.finprodName = $('#select2-finprodMarketId-container').attr('title');
	        }
	        object.operateType = operateType;
	        if($("#finprodMarketId").val() != null && $("#finprodMarketId").val() != ''){
	            object.finprodId = $("#finprodMarketId").select2("data")[0].object;
	        }
	        object.pageModel = pageModel;
	        if(FileCommon) {
	            object.fileArray = FileCommon.getUploadFileInfo()
	        }
	
	        if ($('#trdInvestPersonInfo002Dto_counterId').select2('data') && $('#trdInvestPersonInfo002Dto_counterId').select2('data')[0]) {
	            object.counterType = $('#trdInvestPersonInfo002Dto_counterId').select2('data')[0].object;
	            object.counterId = $('#trdInvestPersonInfo002Dto_counterId').select2('data')[0].id;
	        }
	        var data = $("#chlId").find("option:selected").data("collecttype");
	        if (data ) {
	            object.collectType = data;
	        }
	        object.inputValue1 = 'F03'; 
	        Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/check', object, function(result1) {
	            Joyin.ajaxJson(basePath + 'invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/check', object, function(result) {
	                var message = '';
	                if (object.ps === PS_T01 && object.prinAmt && object.bussTrdAvailableCashQuery && object.bussTrdAvailableCashQuery.avalibleCash && Number(object.bussTrdAvailableCashQuery.avalibleCash) < Number(object.tradeAmt)){
	                    message = JoyinUtil.getMessage("TRD_C_0002");
	                }else if (object.ps === PS_T02 && object.prinAmt && object.bussTrdAvailableCashQuery && object.bussTrdAvailableCashQuery.assetPosition && Number(object.bussTrdAvailableCashQuery.assetPosition) < Number(object.prinAmt)){
                        var errorMessage = JoyinUtil.getMessage("TRD_C_0007_001");
                        Joyin.error(errorMessage);
                        return ;
                    }
	                else{
	                    message = JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.save"));
	                    if (operateType === 'submit') {
	                        message = JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.submit"));
	                    }
	                    if(isHoliday != '' && isHoliday !== YES_OR_NO_N){
	                        message = JoyinUtil.getMessage("BUSS_COM_E_0039",JoyinUtil.getLabel("TrdPlanRateInvestTrade002.dealdate"));
	                    }
	                }
	                if (message.length > 9 && result.data) {
	                    message += result.data;
	                }else if (result.data && result.data == JoyinUtil.getMessage("TRD_C_0005")) {
	                    message = result.data;
	                }
	                //授信占用额度
//	                Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {});
	                object.riskCreditListCommonDto = result1.data;
	                if(operateType === 'save') {
	                    Joyin.confirm(message, function() {
	                        WorkFlow.doSaveList(basePath + 'invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/save', object, "TrdPlanRateInvestTrade002_btn_save");
	                    });
	                }else{
	                    Joyin.confirm(message, function() {
	                        WorkFlow.doSubmitList(basePath + 'invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/save', object, "TrdPlanRateInvestTrade002_btn_sbumit");
	                    });
	                }
              },null,function (result) {
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
       });     
    }
    /**
     * 获取查询信息
     */
    var getParameters = function (){
        if($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != ''){
            finprodId = $("#finprodMarketId").select2("data")[0].object;
        }
        return{
            finprodId:finprodId,
            vdate:$('#vdate').val(),
            mdate:$('#mdate').val(),
            portfolioId:$('#portfolioId').val(),
            ps:$('#ps').val(),
            tradeDate:$('#tradeDate').val(),
            ccy:$('#ccy').val()
        };
    };
    
    /**
     * 利息总额计算
     */
    var intAmtCalcu = function(flag){
        object = {
//                portfolioId:$('#portfolioId').val(),
                finprodId:$('#finprodMarketId').val(),
                settleDate:$('#settleDate').val(),
//                investRate:$('#investRate').val().valueOf() / 100,
                prinAmt:$('#prinAmt').val(),
            ispayUnpaidint:$('#ispayUnpaidint').val(),
            payUnpaidintAmt:$('#payUnpaidintAmt').val()

        }

        if (pageModel != PAGE_MODEL_EDIT && pageModel != PAGE_MODEL_NEW){
        	return ;
        }
        if(object.finprodId != null && object.finprodId != null &&
                object.settleDate != '' && object.settleDate != '' && object.prinAmt != null && object.prinAmt != ''
                	){
        	object.finprodId=$('#finprodMarketId').select2("data")[0].object;
            Joyin.ajaxJson(basePath+ "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getIntAmt",object,function(result){
            	if(!$('#intAmt').val()){
                	$('#intAmt').val(result.data).trigger('blur');
                }
            	if(!flag){
            		$('#intAmt').val(result.data).trigger('blur');
            	}
            	var  intAmt= $('#intAmt').val();
                var tradeAmt = parseFloat(intAmt)+parseFloat($('#prinAmt').val().valueOf());
                if($('#ps').val()==PS_T02 && $('#ispayUnpaidint').val()!= null && $('#payUnpaidintAmt').val()!= null && $('#ispayUnpaidint').val()==YES_OR_NO_Y){
                    tradeAmt = JoyinMath.doAdd(tradeAmt, $("#payUnpaidintAmt").val())
                }
                //卖出时 通道存在 收取方式为回款前扣除 减去通道费 
              //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
//                var data1 = $("#chlId").find("option:selected").data("collecttype");
////                if($('#ps').val()&&$('#ps').val()==PS_T02 && data1 && data1.object && data1.object.collectType==collectType_01){
//                if(($("#trdInvestPersonInfo002Dto_counterId").val()&&$("#trdInvestPersonInfo002Dto_counterId").select2('data')[0].object=='N')
//           		 ||($("#trdInvestPersonInfo002Dto_counterId").val()&&$("#trdInvestPersonInfo002Dto_counterId").select2('data')[0].object!='N'
//           			&& $('#ps').val()&&$('#ps').val()==PS_T02 && data1 && data1==collectType_01)) {
//                	tradeAmt = JoyinMath.doMinus(tradeAmt, $("#channelFee").val())
//                }else if(!$("#trdInvestPersonInfo002Dto_counterId").val()&&$('#ps').val()&&$('#ps').val()==PS_T02 && data1  && data1==collectType_01){
//                	tradeAmt = JoyinMath.doMinus(tradeAmt, $("#channelFee").val())
//                }
                $('#tradeAmt').val(tradeAmt).trigger('blur');
            });
        }else{
            $('#intAmt').val('').trigger('blur');
            $('#tradeAmt').val('').trigger('blur');
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
            object.payDate != '' && object.payDate !=null &&
            object.portfolioId != '' && object.portfolioId !=null
        ){
            object.finprodId=$('#finprodMarketId').select2("data")[0].object;
            Joyin.ajaxJson(basePath+ "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getUnpayIntAmt",object,function(result){
                $('#payUnpaidintAmt').val(result.data).trigger('blur');
                $('#ispayUnpaidint').val(YES_OR_NO_Y).trigger('change');

            });
        }else{
            $('#payUnpaidintAmt').val('').trigger('blur');
            $('#ispayUnpaidint').val('').trigger('blur');

        }
    }
    
    /**
     * 利息总额计算
     */
    var intAmtCalculation = function(){
        if (pageModel != PAGE_MODEL_EDIT && pageModel != PAGE_MODEL_NEW){
            return ;
        }
    	var tradeAmt = parseFloat($("#intAmt").val().valueOf())+parseFloat($('#prinAmt').val().valueOf());

    	//2019-12-24 通道费改造 标的挂通道  通道费不参与计算
//    	var data1 = $("#chlId").find("option:selected").data("collecttype");
////      if($('#ps').val()&&$('#ps').val()==PS_T02 && data1 && data1.object && data1.object.collectType==collectType_01){
//      if(($("#trdInvestPersonInfo002Dto_counterId").val()&&$("#trdInvestPersonInfo002Dto_counterId").select2('data')[0].object=='N')
// 		 ||($("#trdInvestPersonInfo002Dto_counterId").val()&&$("#trdInvestPersonInfo002Dto_counterId").select2('data')[0].object!='N'
// 			&& $('#ps').val()&&$('#ps').val()==PS_T02 && data1 && data1==collectType_01)) {
//      	tradeAmt = JoyinMath.doMinus(tradeAmt, $("#channelFee").val())
//      }else if(!$("#trdInvestPersonInfo002Dto_counterId").val()&&$('#ps').val()&&$('#ps').val()==PS_T02 && data1  && data1==collectType_01){
//      	tradeAmt = JoyinMath.doMinus(tradeAmt, $("#channelFee").val())
//      }

      $('#tradeAmt').val(tradeAmt).trigger('blur')
    }
    
    /**
     * 界面初始化
     * @returns
     */
    function init(){
        if($('#entrustId').val()){
            $('#portfolioId').attr('disabled',true);
            $('#finprodMarketId').attr('disabled',true);
//            $('#prinAmt').attr('disabled',true);
            etrAmt = $('#prinAmt').val();
            $('#ps').prop('disabled',true).trigger('change');
            $('#chlId').prop('disabled',true);
        }
        if($('#finprodMarketId').val()){
            Joyin.ajaxJson(basePath+ "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getFinprodType",{finprodId:$('#finprodMarketId').select2('data')[0].object},function(result) {
                if (result != null && result != '') {
                    if (result.repayIntFlag === YES_OR_NO_N) {//是还本不带息场景，显示累计未付利息
                        $('#repayIntAmt_div').show();
                    } else {//不是则隐藏
                        $('#repayIntAmt_div').hide();
                    }
                }
            })
        }
        if(pageModel==PAGE_MODEL_NEW){
            Joyin.ajaxJson(basePath+'invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getTradeDate',{},function(result){
                $('#tradeDate').val(result.tradeDate);
                settleDateCalcu();
            });
        }
        if($("#chlId")){
        	var finprodId = null;
            if($("#finprodMarketId").val()){
            	finprodId=$("#finprodMarketId").select2("data")[0].object
            }
            ChannelAssetCommon.setChannelSelectObject($("#chlId"), finprodId, $("#entrustId").val(), function () {

            });
        }
        $('.hidden').attr('disabled',true);
    }
    /**
     * 通道费改变时，计算交易金额
     */
    $('#channelFee').change(function(){
    	intAmtCalcu();
    });
    /**
     * 通道改变时，计算交易金额
     */
    $('#chlId').on("change",function(){
    	updateCash()
    	intAmtCalculation();
    });
    /**
     * 交易方向修改联动显示持仓，和现金
     */
    $('#ps').change(function(){
        updateCash();
        psChange();
    });
    /**
     * 交易日改变
     */
    $('#tradeDate').on('change',function(){
        settleDateCalcu();
        updateCash();
    });
    /**
     * 清算速度改变
     */
    $('#settleSpeed').on('change',function(){
        settleDateCalcu();
    });

    /**
     * 币种改变
     */
   $('#ccy').on('change',function(){
       updateCash();
   })
   
   $('#prinAmt').on('change',function(){
       if($('#prinAmt').val() != null &&  $('#prinAmt').val() !='' && etrAmt != '' &&  Number($('#prinAmt').val()) > Number(etrAmt)){
           Joyin.error("交易本金不能大于委托金额",function(result){
               $('#prinAmt').val(etrAmt).trigger('change');
           });
       }else{
           intAmtCalcu();
       }
   });
   
    $('#intAmt').on('change',function(){
    	intAmtCalculation()
    });


    $('#payUnpaidintAmt').on('change',function(){
        intAmtCalculation()
    });

    $('#ispayUnpaidint').on('change',function(){
        intAmtCalculation()
    });

    $('#channelFee').on('change',function(){
    	intAmtCalculation()
    });
    /**
     * 资产代码改变联动
     */
    var finprodChange = function(){
        if($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != ''){
            finprodId = $("#finprodMarketId").select2("data")[0].object;

            Joyin.ajaxJson(basePath+ "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getFinprodType",{finprodId:$('#finprodMarketId').select2('data')[0].object},function(result){
                if(result != null && result != ''){
                    $('#ccy').textSelect('setValue',result.ccy);
                    $('#finprodType').textSelect('setValue',result.finprodType);
                    /*原因、zhuyongkang 2019/7/30  start */
                    $('#type9').textSelect('setValue',result.type9);
                    $('#type10').textSelect('setValue',result.type10);
                    $('#type11').textSelect('setValue',result.type11);
                    $("#type7").val(result.type7).trigger('change');
                    /*原因、zhuyongkang 2019/7/30  start */
                    $('#investRate').val(result.investRate);

                    intAmtCalcu();
                    if(result.repayIntFlag===YES_OR_NO_N){//是还本不带息场景，显示累计未付利息
                        $('#repayIntAmt_div').show();
                        $('#ispayUnpaidint').val(YES_OR_NO_Y).trigger('change');
                        unPayIntAmtCal();//初始化累计还本不带息金额
                    }else{//不是则隐藏
                        $('#repayIntAmt_div').hide();
                        $('#payUnpaidintAmt').val('').trigger('change');
                        $('#ispayUnpaidint').val('').trigger('change');
                    }
                }else{
                    $('#investRate').val('').trigger('change');
                    $('#ccy').val('').trigger('change');
                    $('#finprodType').val('').trigger('change');
                    /*原因、zhuyongkang 2019/7/30  start */
                    $('#type9').val('').trigger('change');
                    $('#type10').val('').trigger('change');
                    $('#type11').val('').trigger('change');
                    $('#payUnpaidintAmt').val('').trigger('change');
                    $('#ispayUnpaidint').val('').trigger('change');
                   /* 原因、zhuyongkang 2019/7/30   end*/
//                    $('#finprodType').textSelect('');
//                    $('#ccy').textSelect('setValue','');
//                    $('#finprodType').textSelect('setValue','');
                }
            })
            //资产代码关联出资产名称 by 赵东东 20191216 start
            $('#finprodName').val($('#finprodMarketId').select2('data')[0].id);
            //资产代码关联出资产名称 by 赵东东 20191216 end
        }else{
            finprodId = '';
            $('#investRate').val('').trigger('change');
            $('#ccy').val('').trigger('change');
            $('#finprodType').val('').trigger('change');
            /*原因、zhuyongkang 2019/7/30  start */
            $('#type9').val('').trigger('change');
            $('#type10').val('').trigger('change');
            $('#type11').val('').trigger('change');
            /* 原因、zhuyongkang 2019/7/30   end*/
//            $('#finprodType').textSelect('');
//            $('#ccy').textSelect('setValue','');
//            $('#finprodType').textSelect('setValue','');
            $('#finprodName').val('');
        }
    }
    /**
     * 交割日计算
     */
    var settleDateCalcu = function(){
        if($('#tradeDate').val() != null && $('#tradeDate').val() != ''
                && $('#finprodMarketId').val() != null && $('#finprodMarketId').val() != ''){
            Joyin.ajaxJson(basePath+ "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/settleDateCalcu",
                                {tradeDate:$('#tradeDate').val(),settleSpeed:$('#settleSpeed').val(),finprodId:$('#finprodMarketId').select2("data")[0].object},function(result){
                $('#settleDate').val(result.settleDate);
                calendarId = result.calendarId;
                isHoliday = result.isHoliday;
                intAmtCalcu();
                if(window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery']){
                    updateCash();
                }
            });
        }else{
            $('#settleDate').val('');
        }
        
    }
    
    
    var psChange = function(){
        var ps = $('#ps').val();
        if(ps == PS_T01){
            window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showCash(true);
        }else{
            window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showPosition(true);
        }
        //卖出时显示通道费
        if(ps&&(ps == PS_T02||ps == PS_T01)){
        	if(!ElementsControl.includes("channelFee")){
                $("#channelFee_div").show();
            }
        }else{
        	$("#channelFee_div").hide();
        	$("#channelFee").val("");
        }
        checkChannelFee();
        intAmtCalculation();
    }
    
    /**
     * 标的挂通道 内转交易 通道费置灰
     * 外部卖出显示可填  其他置灰
     */
    var checkChannelFee = function(){
        var ps = $('#ps').val();
        if(ps==PS_T02&&!$('#trdInvestPersonInfo002Dto_counterId').val()){
        	$("#channelFee").attr('disabled',false);
        }
        else if(ps==PS_T02&&$('#trdInvestPersonInfo002Dto_counterId').val()&&$("#trdInvestPersonInfo002Dto_counterId").select2('data')[0].object!='N'){
        	$("#channelFee").attr('disabled',false);
        }
        else{
        	$("#channelFee").val("").attr('disabled',true);
        }
    }
    
    $('#portfolioId').change(function(){
        updateCash();
        unPayintAmtCalByPl();
        intAmtCalcu();

    });

    //是否转乱联动
    $('#ispayUnpaidint').change(function(){
    prinAmtCalculation();
    });

    var unPayintAmtCalByPl = function() {
        if ($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != '') {
            finprodId = $("#finprodMarketId").select2("data")[0].object;

            Joyin.ajaxJson(basePath + "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getFinprodType", {finprodId: $('#finprodMarketId').select2('data')[0].object}, function (result) {
                if (result != null && result != '') {
                    if (result.repayIntFlag === YES_OR_NO_N) {//是还本不带息场景，显示累计未付利息
                        $('#repayIntAmt_div').show();
                        $('#ispayUnpaidint').val(YES_OR_NO_Y).trigger('change');
                        unPayIntAmtCal();//初始化累计还本不带息金额
                    } else {//不是则隐藏
                        $('#repayIntAmt_div').hide();
                        $('#payUnpaidintAmt').val('').trigger('change');
                        $('#ispayUnpaidint').val('').trigger('change');
                    }
                } else {
                    $('#ispayUnpaidint').val('').trigger('change');
                    $('#payUnpaidintAmt').val('').trigger('change');
                }
            })
        }
    }
    
//    /**
//     *opics 指令生成
//     */
//    $('#TrdPlanRateInvestTrade002_generate').click (function(){
//        $('#bussTrdTransferOrder_btn_generate').trigger('click');
//        var object = $.serializeToObject('#basicInfoForm');
//        if(object.loadRate != null){
//            object.loadRate = object.loadRate.valueOf()/100;
//        }
//        if($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != ''){
//            object.finprodId= $("#finprodMarketId").select2("data")[0].object;
//        }else if(finprodId != null){
//            object.finprodId = finprodId;
//        }
//        Joyin.ajaxJson(basePath+ "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getOpics",object,function(result){
//            var rows = table.getAllRows();
//            for(var i in rows){
//                table.removeRow(rows[i]);
//            }
//            table.addRow(result);
//        });
//    });
    
    /**
     * 1.3 查询组合T日可用现金
     * 1.4 查询组合T+1日可用现金
     */
    var updateCash = function(){
        if($("#tradeDate").val() != null && $("#tradeDate").val() != '' && calendarId != null && calendarId != '' && $("#finprodMarketId").val() != null && $("#finprodMarketId").val() != ''
             && $("#ccy").val() != null && $("#ccy").val() != '' && $("#portfolioId").val() != null && $("#portfolioId").val() != ''){
            window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].updateCash(function () {
                return {
                    'tradeDate': $("#tradeDate").val(),
                    'calendarId': calendarId,
                    'ccy': $("#ccy").val(),
                    'chlId': $("#chlId").val(),
                    'finprodId':$("#finprodMarketId").select2("data")[0].object,
                    'portfolioId': $("#portfolioId").val()
                }
            });

            window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].updateSecAcctBalance(function () {
                return {
                    'tradeDate': $("#tradeDate").val(),
                    'calendarId': calendarId,
                    'ccy': $("#ccy").val(),
                    // 通道交易 新增 强成西 2019/03/26 start
                    'chlId': $("#chlId").val(),
                    // 通道交易 新增 强成西 2019/03/26 end
                    'finprodId':$("#finprodMarketId").select2("data")[0].object,
                    'portfolioId': $("#portfolioId").val()
                }
            });
        }
    };
    
    /**
     * 1.5 查询托管户剩余现金
     */
    var updateSecAcctBalance = function(){
        window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].updateSecAcctBalance(function () {
            return {
                'ccy': $("#ccy").val(),
                'portfolioId': $("#portfolioId").val()
            }
        });
    };


    /**
     * 交易总额计算
     */
    var prinAmtCalculation = function(){
            intAmtCalculation();
    }

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
        init();
        // 是否显示划款指令
        if(GEN_PAY_ORDERS_AUTO=='01'){
        	$('#BussTrdTransferOrder').hide() ;
        }else{
        	$('#BussTrdTransferOrder').show() ;
        }
        $('#TrdPlanRateInvestTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //根据参数 选择显示投资目的或者业务模式
        // if(SHOW_INVAIM_OR_BUSSINSSMODEL=="02"){
        //    $("#invAimDiv").hide();
        //    $("#businessModelDiv").show();
        // }else{
        //    $("#invAimDiv").show();
        //    $("#businessModelDiv").hide();
        // }

        if($('#businessModel').val()) {
            $('#businessModel').attr("disabled",true);
        }
        /**
         * 资产改变
         */
        $('#finprodMarketId').on('change',function(){
            finprodChange();
            settleDateCalcu(); 
            var finprodId = null;
            if($("#finprodMarketId").val()){
                finprodId=$("#finprodMarketId").select2("data")[0].object
            }
            ChannelAssetCommon.setChannelSelectObject($("#chlId"), finprodId, $("#entrustId").val(), function () {

            }); 
        });
        //导出
        $('#TrdPlanRateInvestTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });
        
        $('#TrdPlanRateInvestTrade002_btn_001').click(function(){
            if($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != ''){
                finprodId = $("#finprodMarketId").select2("data")[0].object;
            }
            Joyin.dialog(JoyinUtil.getLabel("FinPlanProfitAsset002"),
                    basePath + "invest/asset/plan/planProfit/FinPlanProfitAsset002/init",
                    "95%", "90%",{pageModel:PAGE_MODEL_REFERENCE,
                    finprodId: finprodId});
        })
       $('#TrdPlanRateInvestTrade002_btn_riskMath').click(function(){
          // window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
           //-----------add by jjpei 2020-01-07 风险新版本 start----------
           var object = $.serializeToObject('#basicInfoForm', true);
           if($("#finprodMarketId").val() != null && $("#finprodMarketId").val() != ''){
               object.finprodId = $("#finprodMarketId").select2("data")[0].object;
           }
           window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/1/RSK002/F03', object,getriskdata());
           //-----------add by jjpei 2020-01-07 风险新版本 end----------
           window.BaseRiskMathComm.initBaseRiskMathCommPageCus(getriskdata());

       })
     //是否展示资产四五级分类
       if(IS_SHOW_TYPE == '01'){
       	$('#selector5').show();
       	}else{
       	$('#selector5').hide();
       	}
         initFlag = true;
    });
    
    /**
     * 资产代码
     */
    $('#finprodMarketId').on('change',function(){
        if(pageModel==PAGE_MODEL_REFERENCE){
          return;
        }
        selectManeger();
    });
    
    /**
     * 查询资产管理人
     */
    var selectManeger = function (){
        if(initFlag){
            return;
        }
        var finprodId = $("#finprodMarketId").select2("data")[0].object;
        if(finprodId){
            Joyin.ajaxJson(basePath + '/invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade003/selectFManeger', {finprodId:finprodId},
                    function (result) {
                        $("#trdInvestPersonInfo002Dto_counterId").textSelect('setValue', result.id);
                        $("#trdInvestPersonInfo002Dto_counterId").trigger('change');
                    });
        }    
    }
    
}());