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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration002/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration002/save', object, "TrdFixIncomeValuDuration002_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration002/save', object, "TrdFixIncomeValuDuration002_btn_submit");
                }
            });
        })
    }

    var init = function(){
        if( $('#actCapi').val() != null && $('#actCapi').val() != ''
            && $('#actInt').val() != null && $('#actInt').val() != ''
            && $('#otherAmt').val() != null && $('#otherAmt').val() != ''){
            var result1 = JoyinMath.doAdd($('#actCapi').val(), $('#actInt').val(),2);
            var result2 = JoyinMath.doMinus(result1, $('#otherAmt').val(),2);
            $('#settleAmt').val(result2);
            $('#dealAmt').val(result2);
        }else{
            $('#settleAmt').val('');
            $('#dealAmt').val('');
        }
    }

    /**
     * 画面加载完成
     */
    $(function() {
    	//初始化画面
         init();
        // 绑定按钮事件
        $('#TrdFixIncomeValuDuration002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdFixIncomeValuDuration002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

    });



    //计算结算金额  结算金额=实收本金+实收利息+其他费用
    var settleAmtCalculate = function(){
        if( $('#actCapi').val() != null && $('#actCapi').val() != ''
            && $('#actInt').val() != null && $('#actInt').val() != ''
            && $('#otherAmt').val() != null && $('#otherAmt').val() != ''){
            var result1 = JoyinMath.doAdd($('#actCapi').val(), $('#actInt').val(),2);
            var result2 = JoyinMath.doMinus(result1, $('#otherAmt').val(),2);
            $('#settleAmt').val(result2);
            $('#dealAmt').val(result2);
        }else{
            $('#settleAmt').val('');
            $('#dealAmt').val('');
        }
    }

    //实收本金 联动
    $('#actCapi').change(function(){
        settleAmtCalculate(); //计算结算金额
    });

    //实收利息 联动
    $('#actInt').change(function(){
        settleAmtCalculate(); //计算结算金额
    });

    //其他费用 联动
    $('#otherAmt').change(function(){
        settleAmtCalculate();  //计算结算金额
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
    //
    // //资产代码联动事件
    // $('#finprodMarketId').change(function(){
    //     secIdChange();
    // });
    // /**
    //  * 资产代码联动带出资产简称，交易场所
    //  */
    // var secIdChange = function() {
    //     var finprodMarketId =  $('#finprodMarketId').val();
    //     if (finprodMarketId == '' || finprodMarketId == undefined) {
    //         $('#finprodName').val('');
    //     } else {
    //         $('#finprodName').val($('#finprodMarketId').select2('data')[0].object).trigger('change');
    //     }
    // }


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


    //根据资产名称联动出资产代码
    $('#finprodMarketId').change(function(){
        var finprodMarketId = $('#finprodMarketId').val();
        if(finprodMarketId){
            var dataPort = $('#finprodMarketId').select2('data')[0].object;
            var finprodId = dataPort.finprodId;
            $('#finprodName').val(finprodId).trigger('change');

        }else {
            $('#finprodName').val('').trigger('change');
        }
    });


    //校验   交割日必须大于等于交易日
    // var checkDate=function () {
    //     var tradeDate = $('#tradeDate').val();
    //     var settleDate = $('#settleDate').val();
    //     if(tradeDate&&settleDate){
    //         if((new Date(tradeDate).DateDiff('d',settleDate))<0){
    //             Joyin.error("交割日必须大于等于交易日");
    //             $('#settleDate').val("");
    //         }
    //     }
    // }
    // $("#tradeDate").change(function () {
    //     checkDate();   //校验交易日小于交割日
    // });
    //
    // $("#settleDate").change(function () {
    //     checkDate();  //校验交易日小于交割日
    // });




}());