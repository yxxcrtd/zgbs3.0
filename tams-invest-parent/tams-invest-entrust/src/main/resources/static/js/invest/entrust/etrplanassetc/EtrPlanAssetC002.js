/**
 * 计划类货币委托新建
 */
(function() {
    var getriskdata = function () {
        var entrustAmt=$('#entrustAmt').val();
        if($('#ps').val()==T02){
            if(!$('#entrustAmt2').val()&&$('#entrustNum2').val()) {
                entrustAmt =$('#entrustNum2').val();//把份额直接赋值给金额
            }else{
                entrustAmt =$('#entrustAmt2').val();//
            }
        }else {
            if (!$('#entrustAmt').val() && $('#entrustNum').val()) {
                entrustAmt = $('#entrustNum').val();//把份额直接赋值给金额
            }
        }
        var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:entrustAmt,assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
        // add by zhangrui1 交易对手影响风险试算 start
        obj.counterId = $('#etrInvestPersonInfo_counterId').val();
        // add by zhangrui1 交易对手影响风险试算 end
        var object = {tradeDirection:['ps','EtrPlanAssetC002.ps'],portfolioCode:['portfolioId','EtrPlanAssetC002.portfolioId'],tradeAmount:['entrustAmt','EtrPlanAssetC002.entrustAmt'],assetType:['assetType1','EtrPlanAssetC002.assetType1'],data:obj}
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'EtrInvestPersonInfo.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        return object;
    }
    /**
     * 保存或提交
     * 
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        var s;
        if(typeof(BaseRiskMathComm) != 'undefined') {
            if (BaseRiskMathComm) {
                s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
                if (!s && s != '') {
                    return;
                }
                if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())) {
                    return;
                }
            }
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#searchForm');
        if ($('#select2-finprodId-container').attr('title')){
            object.finprodName = $('#select2-finprodId-container').attr('title');
        }
        object.etrInvestPersonInfo.invManagerId = $('#etrInvestPersonInfo_invManagerId').val();
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.entrustId = $('#entrustId').val();
        object.finprodId =$('#finprodIdhid').val();
        object.finvdate = $('#finvdate').val();
        object.finmdate = $('#mdate').val();
        object.entrustStatus = $('#entrustStatus').val();
        if(object.etrInvestPersonInfo.trader != null && object.etrInvestPersonInfo.trader != ''){
            object.etrInvestPersonInfo.trader = object.etrInvestPersonInfo.trader.join(",");
        }
        if ($('#etrInvestPersonInfo_counterId').select2('data') && $('#etrInvestPersonInfo_counterId').select2('data')[0]) {
            object.counterType = $('#etrInvestPersonInfo_counterId').select2('data')[0].object;;
        }
        if(FileCommon) {
            object.fileArray = FileCommon.getUploadFileInfo();
            if(object.fileArray && _.isArray(object.fileArray)){
                $.each(object.fileArray, function(index,obj){
                    obj.seqNo = obj.fileNo;
                });
            }
        }

        //指令暂存，去除校验  add by xxg 20210802
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath+'invest/entrust/etrplanassetc/EtrPlanAssetC002/save',object,"EtrPlanAssetC002_btn_save");
        }else{
            Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetc/EtrPlanAssetC002/checkAuthorization', object, function(result2) {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetc/EtrPlanAssetC002/check',object,
                    function(result) {
                        Joyin.confirm(result.data,function() {
                            WorkFlow.doSubmitList(basePath+'invest/entrust/etrplanassetc/EtrPlanAssetC002/save',object,"EtrPlanAssetC002_btn_submit");
                        });
                    });
            });
        }
    }
  //查询组合可用现金
    var getPtlCashPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),vdate:$('#vdate').val(),calId:$('#calendarId').val(), chlId:$("#chlId").val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetc/EtrPlanAssetC002/getProAvailableCash',data,
                function(result) {
            $('#proAvailableCash').val(result.cashT).trigger('blur');;
            $('#proAvailableCash1').val(result.cashT1).trigger('blur');;
            $('#accAvailableCash').val(result.cashTG).trigger('blur');
            $('#channelAvailableCash').val(result.cashTD).trigger('blur');
        });
    }  
    //查询组合可用持仓
    var getPtlSecPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),finprodId:$('#finprodIdhid').val(),ccy:$('#ccy').val(), secAcctId:$("#chlId").val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetc/EtrPlanAssetC002/getAssetAvailablePosition',data,
                function(result) {
            $('#assetAvailablePosition').val(result).trigger('blur');;
        });
    }
    var init = function(){
        $('#ps').change();
//        $('#assetType2').change();
        $('#finprodId').change();
        $('#entrustAmt').change();
        $('#entrustNum').change();

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
    

    //添加风险试算验证
    window.componentLoadCallback = function(isDone, pagePath, params) {
        if (isDone) {
            if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel == PAGE_MODEL_NEW) {
                    riskparam.data = null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
            }
        }
    }

    /**
     * 查看资产详情事件
     */
    $('#EtrPlanAssetC002_btn_search').click(function() {
        var finprodId =$('#finprodIdhid').val();
        var url = "/invest/asset/plan/plancur/FinPlanAssetC002/init";
        if (finprodId != ""){
            var params = {
                'pageModel':PAGE_MODEL_REFERENCE,
                'finprodId':finprodId,
                'isQuickInput':true
            };
            EtrShowFinPlanAssetDetailInfo.searchDetail(params,url);
        } else {
            Joyin.error("资产名称不能为空")
        }
    });

    /**
     * 绑定事件
     */
    $(function() {

        /**
         * 保存
         */
        $('#EtrPlanAssetC002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        /**
         * 提交
         */
        $('#EtrPlanAssetC002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        /**
         * 风险试算
         */
        $('#EtrPlanAssetC002_btn_riskcal').click(function(){
                var objectForm = $.serializeToObject('#searchForm');
                //赎回提示份额不为空
                if($('#ps').val()==T06 && !$('#entrustNum').val()){
                	Joyin.error('请输入委托份额');
                	return;
                }
                if($('#ps').val()==T02){
                    if(!$('#entrustAmt2').val()&&$('#entrustNum2').val()) {
                        objectForm.entrustAmt =$('#entrustNum2').val();//把份额直接赋值给金额
                    }else{
                        objectForm.entrustAmt =$('#entrustAmt2').val();//
                    }
                }else {
                    if (!$('#entrustAmt').val() && $('#entrustNum').val()) {
                        objectForm.entrustAmt = $('#entrustNum').val();//把份额直接赋值给金额
                    }
                }
            objectForm.finprodId =$('#finprodIdhid').val();
            objectForm.finmdate = $('#mdate').val();
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK002/F09',objectForm,getriskdata());
        });
        /**
         * 交易方向
         */
        $('#ps').change(function() {
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodId = $('#finprodIdhid').val();
                if(ps == T04){
                    $('#proAvailableCash_div').show();
                    $('#proAvailableCash1_div').show();
                    // $('#accAvailableCash_div').show();
                    $('#expectedCashAvailable_div').show();
                    $('#div_channelCash').hide();
                    $('#entrustAmt_div').show();
                    $('#entrustAmt').attr("disabled",false);
                    $('#entrustNum_div').hide();
                    $('#entrustNum').val('');
                    $('#entrustAmt').attr('disabled',false);
                    $('#assetAvailablePosition_div').hide();
                    $('#carryAmt_div').hide();
                    $('#entrustAmt_div2').hide();
                    $('#entrustNum_div2').hide();
                    // ZGZGST-1428 交易对手必填 fuyongsheng 202108271501 edit start
                    $('#etrInvestPersonInfo_counterId').attr('required',true);
                    // ZGZGST-1428 交易对手必填 fuyongsheng 202108271501 edit end
                    $('#carryAmt').val('');
                }else if(ps == T06){
                    $('#proAvailableCash_div').hide();
                    $('#proAvailableCash1_div').hide();
                    // $('#accAvailableCash_div').hide();
                    $('#expectedCashAvailable_div').hide();
                    $('#div_channelCash').hide();
                    $('#entrustNum').attr('disabled',false);
                    $('#assetAvailablePosition_div').show();
                    $('#entrustAmt_div').hide();
                    $('#entrustAmt').val('');
                    $('#entrustNum_div').show();
                    $('#carryAmt_div').hide();
                    $('#entrustAmt_div2').hide();
                    $('#entrustNum_div2').hide();
                    // ZGZGST-1428 交易对手必填 fuyongsheng 202108271501 edit start
                    $('#etrInvestPersonInfo_counterId').attr('required',true);
                    // ZGZGST-1428 交易对手必填 fuyongsheng 202108271501 edit end
                    $('#carryAmt').val('');
                }else if(ps == T01){
                	$('#proAvailableCash_div').show();
                    $('#proAvailableCash1_div').show();
                    // $('#accAvailableCash_div').show();
                    $('#expectedCashAvailable_div').show();
                    $('#div_channelCash').hide();
                    /*隐藏买入的委托金额2和委托份额2 lishuping 2019/8/9 start*/
                    $('#entrustAmt_div2').hide();
                    $('#entrustAmt2').attr('disabled',true);
                    $('#entrustNum_div2').hide();
                    /*隐藏买入的委托金额2和委托份额2 lishuping 2019/8/9 end*/
                    $('#assetAvailablePosition_div').hide();
                    $('#carryAmt_div').show();
                    /*显示买入的委托金额和委托份额 lishuping 2019/8/9 start*/
                    $('#entrustAmt_div').show();
                    $('#entrustNum_div').show();
                    /*显示买入的委托金额和委托份额 lishuping 2019/8/9 end*/
                    $('#etrInvestPersonInfo_counterId').attr('required',true);
                    //ZGBSS-1198  货币类计划委托和需求不符
                    $('#entrustAmt').attr("disabled",true);
                }else if(ps == T02){
                	$('#proAvailableCash_div').hide();
                    $('#proAvailableCash1_div').hide();
                    // $('#accAvailableCash_div').hide();
                    $('#expectedCashAvailable_div').hide();
                    $('#div_channelCash').hide();
                    $('#entrustNum').attr('disabled',false);
                    $('#assetAvailablePosition_div').show();
                    $('#entrustAmt_div2').show();
                    $('#entrustAmt2').attr('disabled',true);
                    $('#entrustNum_div2').show();
                    $('#carryAmt_div').show();
                    $('#entrustAmt_div').hide();
                    $('#entrustNum_div').hide();
                    $('#etrInvestPersonInfo_counterId').attr('required',true);
                    //ZGBSS-1198  货币类计划委托和需求不符
                    $('#entrustAmt').attr("disabled",true);
                }
            $('#etrInvestPersonInfo_counterId').val('').trigger("change");
                if(ps == T04 || ps == T01){
                    if(ccy && portfolioId && vdate) {
                        getPtlCashPositionInfo();
                    }
                }
                else if(ps == T02 || ps == T06) {
                    if(portfolioId && finprodId) {
                        getPtlSecPositionInfo();
                    }
                }

             // 画星星
                star();
        });
        /**
         * 资产代码
         */
        $('#finprodId').on('change', function () {
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodIdhid = $('#finprodIdhid').val();
            var finprodId;
            $("#purSpeed").val('').trigger('change');
            $("#redSpeed").val('').trigger('change');
            if($('#finprodId').val() && $('#finprodId').select2('data') && $('#finprodId').select2('data')[0]){
                var data = $('#finprodId').select2('data')[0].object;
                Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/getSpeedByFinprodId', data,function (result) {
                    $("#purSpeed").val(result.PURSPEED).trigger('change');
                    $("#redSpeed").val(result.REDSPEED).trigger('change');
                });
                finprodId = data.finprodId;
                $('#finprodIdhid').val(data.finprodId).trigger('change');
                $('#finprodName').val($('#finprodId').val()).trigger('change');
                $('#calendarId').val(data.calendarId).trigger('change');
                $('#finvdate').val(new Date(data.finvdate)).trigger('change');
                $('#finmdate').val(new Date(data.finmdate)).trigger('change');
                $('#ccy').val(data.ccy).attr('disabled',true).trigger('change');
                $('#assetType1').val(data.assetType1).trigger('change');
                /*添加三四级分类 lishuping 2019/7/30 start*/
                $('#assetType10').val(data.assetType10).trigger('change');
                $('#assetType11').val(data.assetType11).trigger('change');
                /*添加三四级分类 lishuping 2019/7/30 start*/
                $('#assetType2').val(data.assetType2).trigger('change').change();
            }else{
            	$('#finprodIdhid').val('').trigger('change');
                $('#finprodName').val('').trigger('change');
                $('#calendarId').val('').trigger('change');
                $('#finvdate').val('').trigger('change');
                $('#finmdate').val('').trigger('change');
                $('#ccy').val('').attr('disabled',false).trigger('change');
                $('#assetType1').val('').trigger('change');
                $('#assetType2').val('').trigger('change').change();
            }
            ChannelAssetCommon.setChannelSelectObject($("#chlId"), finprodId, false, function () {

            });
        });
        $("#chlId").change(function () {
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            if((ps == T04 || ps == T01) && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
            if((ps == T06 || ps == T02) && portfolioId){
                getPtlSecPositionInfo();
            }
        })
        /**
         * 委托金额
         */
//        $('#entrustAmt').change(function(){
//            if($('#ps').val()==T06){
//                if($('#assetType2').val()==C02 || $('#assetType2').val()==C03){
//                    if($('#entrustAmt').val()){
//                        $('#entrustNum').attr("disabled",true);
//                        $('#entrustNum').val("");
//                        $('#entrustNum').attr('required',false);
//                    }else{
//                        $('#entrustNum').attr("disabled",false);
//                        $('#entrustNum').attr('required',true);
//                    }
//                }else{
//                    $('#entrustAmt').attr("disabled",false);
//                    $('#entrustAmt').attr('required',true);
//                }
//            }
//         // 画星星
//            star();
//        });
        /**
         * 委托份额
         */
        $('#entrustNum2').change(function(){
            if($('#ps').val()==T02 || $('#ps').val()==T01){
                $('#entrustAmt2').val($('#entrustNum2').val()).trigger('blur');
                $('#carryAmt').change();
            }
        });

        /**
         * 委托份额
         */
        $('#entrustNum').change(function(){
            if($('#ps').val()==T02 || $('#ps').val()==T01){
                var carryAmt = $('#carryAmt').val();
                var entrustNum = $('#entrustNum').val();
                if(!carryAmt){
                    carryAmt = 0;
                }
                if(!entrustNum){
                    entrustNum = 0;
                }
                $('#entrustAmt').val(Number(carryAmt)+Number(entrustNum)).trigger('blur');
            }
        });

        /**
         * 待结转份额
         */
        $('#carryAmt').change(function(){
        	var carryAmt = $('#carryAmt').val();
        	var entrustNum = $('#entrustNum2').val();
            var entrustNum2 = $('#entrustNum').val();
        	if(!carryAmt){
        		carryAmt = 0;
        	}
        	if(!entrustNum){
        		entrustNum = 0;
        	}
        	$('#entrustAmt2').val(Number(carryAmt)+Number(entrustNum)).trigger('blur');
        	//ZGBSS-1198  货币类计划委托和需求不符
            if($('#ps').val()==T02 || $('#ps').val()==T01) {
                $('#entrustAmt').val(Number(carryAmt) + Number(entrustNum2)).trigger('blur');
            }
        });
        /**
         * 资产二级类型
         */
        $('#assetType2').change(function(){
            if($('#assetType2').val()==C01 && ($('#ps').val()==T06 || $('#ps').val()==T02)){
                $('#entrustAmt').attr("disabled",true);
                $('#entrustAmt').attr('required',false);
                $('#entrustAmt').val("");
                $('#entrustNum').attr("disabled",false);
                $('#entrustNum').attr('required',true);
            }else if($('#ps').val()==T06 || $('#ps').val()==T02){
                $('#entrustAmt').attr("disabled",false);
                $('#entrustAmt').attr('required',true);
                $('#entrustNum').attr("disabled",false);
                $('#entrustNum').attr('required',true).change();
            }
         // 画星星
            star();
        });
        
        $('#ccy').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodId = $('#finprodIdhid').val();
            if((ps == T04 || ps == T01) && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
            if((ps == T06 || ps == T02) && ccy && portfolioId && finprodId){
                getPtlSecPositionInfo();
            }
        });
        
        $('#portfolioId').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodId = $('#finprodIdhid').val();
            if((ps == T04 || ps == T01) && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }else if((ps == T06 || ps == T02) && portfolioId && finprodId){
                getPtlSecPositionInfo();
            }
        });
        
        $('#vdate').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodId = $('#finprodIdhid').val();
            if(ps == T04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
        });
        //是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
        	$('#assetType11Div').show();
        }else{
        	$('#assetType11Div').hide();
        }
        init();
        // 画星星
        star();
    });
}());
