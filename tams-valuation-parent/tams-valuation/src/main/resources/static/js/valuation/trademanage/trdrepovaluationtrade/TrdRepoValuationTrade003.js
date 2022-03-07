$(function() {


    /**
     * 获取表单参数
     */
    var submitForm = function() {
        return $.serializeToObject('#submitForm');
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade003/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade003/save', object, "TrdRepoValuationTrade003_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade003/save', object, "TrdRepoValuationTrade003_btn_submit");
                }
            });
        })
    }
    //页面初始化
    var init=function () {
        $("#endate").hide();
        var mdate=$("#mdate").val();
        if(mdate){
            if(mdate>platDate){ //平台日小于到期日
                $('#ps').textSelect("setValue",'C20');
            }else if(mdate==platDate){
                $('#ps').textSelect("setValue",'T07');
            }else {
                $('#ps').textSelect("setValue",'T55');
            }
        }
    }

    /**
     * 画面加载完成
     */
    $(function() {
    	//初始化画面
        init();
        // 绑定按钮事件
        $('#TrdRepoValuationTrade003_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdRepoValuationTrade003_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

    });

    //计算实际占款天数
    var actpaydaysCalculate=function () {
        if($('#vdate').val() != null && $('#vdate').val() != '' && $('#mdate').val() != null && $('#mdate').val() != ''){
            var actpaydays=(DateUtil.parse($('#mdate').val())-DateUtil.parse($('#vdate').val()))/1000/60/60/24;   //计算 到期日-起息日
            $('#actpaydays').val(actpaydays);
        }else {
            $('#actpaydays').val('');
        }
    }

    //应计利息联动处理     应计利息=交易本金*回购利率*实际占款天数/365
    // var intAmtCalculate=function () {
    //     if($('#prinAmt').val() !=null && $('#prinAmt').val() != ''
    //         && $('#rebackPrice').val() != null && $('#rebackPrice').val() != ''
    //         && $('#actpaydays').val() != null && $('#actpaydays').val() != ''
    //         && $('#mdate').val() != null && $('#mdate').val() != ''
    //         && $('#vdate').val() != null && $('#vdate').val() != '' ){
    //         var result1=JoyinMath.doMul($('#rebackPrice').val(),0.01,4);
    //         var result2=JoyinMath.doMul($('#prinAmt').val(),result1,4);
    //         var result3=JoyinMath.doMul(result2,$('#actpaydays').val() ,4);
    //         var result4=JoyinMath.doDiv(result3,365 ,2);
    //         $('#intAmt').val(result4);
    //     }else {
    //         $('#intAmt').val('');
    //     }
    // }

    // 本金金额 联动
    $('#prinAmt').change(function(){
        // intAmtCalculate();  //计算应计利息

        settleAmtCalculate();  //计算结算金额
    });

    // 回购利率 联动
    $('#rebackPrice').change(function(){
        intAmtCalculate();  //计算应计利息
    });

    //实际占款天数
    $('#actpaydays').change(function(){
        intAmtCalculate();  //计算应计利息
    });




    //计算结算金额  结算金额=本金金额+应计利息+逾期利息金额+其他费用  overdueAmt
    var settleAmtCalculate = function(){
        if($('#prinAmt').val() != null && $('#prinAmt').val() != ''
            && $('#intAmt').val() != null && $('#intAmt').val() != ''
            && $('#otherAmt').val() != null && $('#otherAmt').val() != ''){
            var result1 = JoyinMath.doAdd($('#prinAmt').val(), $('#intAmt').val(),2);
            var result2 = JoyinMath.doAdd(result1, $('#overdueAmt').val(),2);
            var result3 = JoyinMath.doAdd(result2, $('#otherAmt').val(),2);
            $('#settleAmt').val(result3);
        }else{
            $('#settleAmt').val('');
        }
    }


    //逾期利息金额 联动
    $('#overdueAmt').change(function(){
        settleAmtCalculate();
    });


    //结算服务费 联动
    $('#settFeeAmt').change(function(){
        settleAmtCalculate();
    });

    //其他费用 联动
    $('#otherAmt').change(function(){
        settleAmtCalculate();
    });


    //根据产品名称联动出产品代码
    $('#portfolioId').change(function(){
        var portfolioId = $('#portfolioId').val();
        if(portfolioId){
            var dataPort = $('#portfolioId').select2('data')[0].object;
            var finprodId = dataPort.finprodId;
            $('#portfolioName').val(finprodId).trigger('change');

        }else {
            $('#portfolioName').val('').trigger('change');
        }
    });

    // /**
    //  * 产品代码联动带出产品名称，证券账户
    //  */
    // var prodIdChange = function() {
    //     var portfolioId = $('#portfolioId').val();
    //     if (portfolioId == '' || portfolioId == undefined) {
    //         $('#portfolioName').val('');
    //     } else {
    //         $('#portfolioName').val($('#portfolioId').select2('data')[0].object).trigger('change');
    //     }
    // }
    //
    // //产品代码联动事件
    // $('#portfolioId').change(function(){
    //     prodIdChange();
    // });


    //校验   交割日必须大于等于交易日
    var checkDate=function () {
        var tradeDate = $('#tradeDate').val();
        var settleDate = $('#settleDate').val();
        if(tradeDate&&settleDate){
            if((new Date(tradeDate).DateDiff('d',settleDate))<0){
                Joyin.error("交割日必须大于等于交易日");
                $('#settleDate').val("");
            }
        }
    }
    $("#tradeDate").change(function () {
        checkDate();   //校验交易日小于交割日
    });

    $("#settleDate").change(function () {
        checkDate();  //校验交易日小于交割日
    });






}());