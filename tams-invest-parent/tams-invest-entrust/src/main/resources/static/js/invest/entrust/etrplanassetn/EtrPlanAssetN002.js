/**
 * 计划类净值委托新建
 */
(function() {

    var getriskdata = function () {
        var entrustAmt = $('#entrustAmt').val();
        var entrustNum = $('#entrustNum').val();
        if(entrustAmt){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustAmt').val(),assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()};
            //---add by 裴家俊  2019/03/20  添加资产类型条件---start-----
            if($('#assetType2').val() && $('#assetType2').val() != ''){
                obj.assetSecType = $('#assetType2').val();
            }
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#etrInvestPersonInfo_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            //---add by 裴家俊  2019/03/20  添加资产类型条件---end-----
            var object = {tradeDirection:['ps','EtrPlanAssetN002.ps'],portfolioCode:['portfolioId','EtrPlanAssetN002.portfolioId'],tradeAmount:['entrustAmt','EtrPlanAssetN002.entrustAmt'],assetType:['assetType1','EtrPlanAssetN002.assetType1'],data:obj}
        }else if(entrustNum){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustNum').val(),assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val(),amtType:'1'}
            //---add by 裴家俊  2019/03/20  添加资产类型条件---start-----
            if($('#assetType2').val() && $('#assetType2').val() != ''){
                obj.assetSecType = $('#assetType2').val();
            }
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#etrInvestPersonInfo_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            //---add by 裴家俊  2019/03/20  添加资产类型条件---end-----
            var object = {tradeDirection:['ps','EtrPlanAssetN002.ps'],portfolioCode:['portfolioId','EtrPlanAssetN002.portfolioId'],tradeAmount:['entrustNum','EtrPlanAssetN002.entrustNum'],assetType:['assetType1','EtrPlanAssetN002.assetType1'],data:obj}
        }else{
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustAmt').val(),assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
            //---add by 裴家俊  2019/03/20  添加资产类型条件---start-----
            if($('#assetType2').val() && $('#assetType2').val() != ''){
                obj.assetSecType = $('#assetType2').val();
            }
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#etrInvestPersonInfo_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            //---add by 裴家俊  2019/03/20  添加资产类型条件---end-----
            // ZGZGST-2218:根据指令金额和指令份额必选情况分别展示提示语 fuyongsheng 20210907 edit start
            if($("#entrustAmt").attr("required")==="required"){
                var object = {tradeDirection:['ps','EtrPlanAssetN002.ps'],portfolioCode:['portfolioId','EtrPlanAssetN002.portfolioId'],tradeAmount:['entrustAmt','EtrPlanAssetN002.entrustAmt'],assetType:['assetType1','EtrPlanAssetN002.assetType1'],data:obj}
            }else{
                var object = {tradeDirection:['ps','EtrPlanAssetN002.ps'],portfolioCode:['portfolioId','EtrPlanAssetN002.portfolioId'],tradeAmount:['entrustNum','EtrPlanAssetN002.entrustNum'],assetType:['assetType1','EtrPlanAssetN002.assetType1'],data:obj}
            }
            // ZGZGST-2218:根据指令金额和指令份额必选情况分别展示提示语 fuyongsheng 20210907 edit end
        }
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
        if(BaseRiskMathComm) {
            s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
            if(!s && s != ''){
                return;
            }
            if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())){
                return;
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
        if ($('#etrInvestPersonInfo_counterId').select2('data') && $('#etrInvestPersonInfo_counterId').select2('data')[0]) {
            object.counterType = $('#etrInvestPersonInfo_counterId').select2('data')[0].object;;
        }
        if(object.etrInvestPersonInfo.trader != null && object.etrInvestPersonInfo.trader != ''){
            object.etrInvestPersonInfo.trader = object.etrInvestPersonInfo.trader.join(",");
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
            WorkFlow.doSaveList(basePath+'invest/entrust/etrplanassetn/EtrPlanAssetN002/save',object,"EtrPlanAssetN002_btn_save");
        }else{
            Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/check', object, function(result1) {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetn/EtrPlanAssetN002/checkAuthorization', object, function(result2) {
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetn/EtrPlanAssetN002/check',object,
                        function(result) {
                            Joyin.confirm(result.data,function() {
                                WorkFlow.doSubmitList(basePath+'invest/entrust/etrplanassetn/EtrPlanAssetN002/save',object,"EtrPlanAssetN002_btn_submit");
                            });
                        });
                });
            });
        }
    }
  //查询组合可用现金
    var getPtlCashPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),vdate:$('#vdate').val(),calId:$('#calendarId').val(), chlId: $("#chlId").val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetn/EtrPlanAssetN002/getProAvailableCash',data,
                function(result) {
            $('#proAvailableCash').val(result.cashT).trigger('blur');
            $('#proAvailableCash1').val(result.cashT1).trigger('blur');
            $('#accAvailableCash').val(result.cashTG).trigger('blur');
            $('#channelAvailableCash').val(result.cashTD).trigger('blur');
        });
    }  
    //查询组合可用持仓
    var getPtlSecPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),finprodId:$('#finprodIdhid').val(),ccy:$('#ccy').val(),secAcctId:$("#chlId").val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetn/EtrPlanAssetN002/getAssetAvailablePosition',data,
                function(result) {
            $('#assetAvailablePosition').val(result).trigger('blur');;
        });
    }
    var init = function(){
//      计划类净值型指令，交易方向为卖出，保存后在待办事项中查看，委托金额可输入不置灰，与新建界面不一致 add by yujinfa start
        var ps= $('#ps').val();
//        $('#assetType2').change();
        if($('#finprodId').val() != null && $('#finprodId').val() != ''){
        $('#finprodId').change();
        }
        $('#ps').change();
        if(!(pageModel === PAGE_MODEL_EDIT&&(ps == T06 || ps == T02))){
                $('#entrustAmt').change();
                $('#entrustNum').change();
        }
//      计划类净值型指令，交易方向为卖出，保存后在待办事项中查看，委托金额可输入不置灰，与新建界面不一致 add by yujinfa end
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
            var riskparam = getriskdata();
            if (pageModel==PAGE_MODEL_NEW){
                riskparam.data =null;
            }
            window.BaseRiskMathComm.getparam(riskparam);
        }
    }

    /**
     * 查看资产详情事件
     */
    $('#EtrPlanAssetN002_btn_search').click(function() {
        var finprodId =$('#finprodIdhid').val();
        var finprodIdhid =$('#finprodIdhid').val();
        var url = "/invest/asset/plan/planNet/FinPlanNetAsset002/init";
        if (finprodIdhid != ""){
            var params = {
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodIdhid
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
        $('#EtrPlanAssetN002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        /**
         * 提交
         */
        $('#EtrPlanAssetN002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        /**
         * 风险试算
         */
        $('#EtrPlanAssetN002_btn_riskcal').click(function(){
            // window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
            var object = $.serializeToObject('#searchForm');
            /* del by zhangrui1 start
            if($('#entrustNum').val()) {
                object.entrustAmt =$('#entrustNum').val();//把份额直接赋值给金额
            }
               del by zhangrui1 end */
            object.finprodId =$('#finprodIdhid').val();
            object.finmdate = $('#mdate').val();
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK002/F04',object,getriskdata());
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
                if(ps == T04 || ps == T01){
                    $('#proAvailableCash_div').show();
                    $('#div_channelCash').hide();
                    $('#proAvailableCash1_div').show();
                    //update by  zhangnian  2019115 start BSTOZ-1261
                    /*$('#accAvailableCash_div').show();*/
                    //update by  zhagnnain  20191115  end
                    $('#entrustAmt').attr('disabled',false);
                    $('#entrustAmt').attr('required',true);
                    $('#entrustNum').attr('disabled',true);
                    $('#entrustNum').attr('required',false);
                    $('#entrustNum').val("");
                    $('#assetAvailablePosition_div').hide();
                    if(ccy && portfolioId && vdate){
                        getPtlCashPositionInfo();
                    }
                }else if(ps == T06 || ps == T02){
                    $('#proAvailableCash_div').hide();
                    $('#div_channelCash').hide();
                    $('#proAvailableCash1_div').hide();
                    // $('#accAvailableCash_div').hide();
                    $('#entrustNum').attr('disabled',false);
                    $('#assetAvailablePosition_div').show();
                    $('#entrustAmt').change();
                    $('#entrustNum').change();
                    if($('#assetType2').val()==C01){
                        $('#entrustAmt').attr('disabled',true);
                        $('#entrustAmt').val("");
                        $('#entrustAmt').attr('required',false);
                    }else{
                        $('#entrustAmt').attr('disabled',false);
                        $('#entrustAmt').attr('required',true);
                    }
                    if(portfolioId && finprodId){
                        getPtlSecPositionInfo();
                    }
                }
             // 画星星
                star();
                $("#etrInvestPersonInfo_counterId").empty();
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
                /* add by zhangpuyu 2019/7/30 start*/
                $('#assetType10').val(data.assetType10).trigger('change');
                $('#assetType11').val(data.assetType11).trigger('change');
                /* add by zhangpuyu 2019/7/30 end*/
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
        $('#entrustAmt').change(function(){
            var ps= $('#ps').val();
            if((ps == T06 || ps == T02)){
                if($('#assetType2').val()==C02 || $('#assetType2').val()==C03|| $('#assetType2').val()==C04){
                    if($('#entrustAmt').val()){
                        $('#entrustNum').attr("disabled",true);
                        $('#entrustNum').val("");
                        $('#entrustNum').attr('required',false);
                    }else{
                        $('#entrustNum').attr("disabled",false);
                        $('#entrustNum').attr('required',true);
                    }
                }else{
                    $('#entrustAmt').attr("disabled",false);
                    $('#entrustAmt').attr('required',true);
                }
            }
         // 画星星
            star();
            if ($('#finprodId').val()) {
                if ((ps == T01 || ps == T04 || ps == T06 || ps == T02)) {
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetn/EtrPlanAssetN002/getValue', {finprodId: $('#finprodId').val()}, function (result) {
                        if (result && $('#entrustAmt').val()) {
                            $('#entrustNum').val($('#entrustAmt').val() / result);
                        }

                    });
                }
            }
        });
        /**
         * 委托份额
         */
        $('#entrustNum').change(function(){
            var ps= $('#ps').val();
            if((ps == T06 || ps == T02)){
                if($('#assetType2').val()==C02 || $('#assetType2').val()==C03|| $('#assetType2').val()==C04){
                    if($('#entrustNum').val()){
                        $('#entrustAmt').attr("disabled",true);
                        $('#entrustAmt').val("");
                        $('#entrustAmt').attr('required',false);
                    }else{
                        $('#entrustAmt').attr("disabled",false);
                        $('#entrustAmt').attr('required',true);
                    }
                }else{
                    $('#entrustNum').attr("disabled",false);
                    $('#entrustNum').attr('required',true);
                }
                if ($('#finprodId').val()) {
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetn/EtrPlanAssetN002/getValue', {finprodId: $('#finprodId').val()}, function (result) {
                        if (result && $('#entrustAmt').val()) {
                            $('#entrustNum').val($('#entrustAmt').val() / result);
                        }
                        if (result && $('#entrustNum').val()) {
                            $('#entrustAmt').val($('#entrustNum').val() * result);
                        }

                    });
                }
            }
         // 画星星
            star();
        });
        /**
         * 资产二级类型
         */
        $('#assetType2').change(function(){
            var ps= $('#ps').val();
            if($('#assetType2').val()==C01 && (ps == T06 || ps == T02)){
                $('#entrustAmt').attr("disabled",true);
                $('#entrustAmt').attr('required',false);
                $('#entrustAmt').val("");
                $('#entrustNum').attr("disabled",false);
                $('#entrustNum').attr('required',true);
            }else if((ps == T06 || ps == T02)){
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
            if((ps == T04 || ps == T01) && ccy && portfolioId && vdate){
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
