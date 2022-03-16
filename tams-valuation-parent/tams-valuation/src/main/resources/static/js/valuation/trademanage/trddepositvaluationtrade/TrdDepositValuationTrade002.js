(function(){

    /**
     * 获取表单参数
     */
    var informationform = function() {
        return $.serializeToObject('#informationForm');
    }


    /**
     * 产品代码联动带出产品名称，证券账户
     */

    $('#portfolioId').change(function(){
        var portfolioId = $('#portfolioId').val();
        if(portfolioId){
            $('#portfolioName').val($('#portfolioId').select2('data')[0].id).trigger('change');

        }else {
            $('#portfolioName').val('').trigger('change');
        }
    });

    /**
     * 资产代码联动带出资产简称，交易场所
     */

    $('#finprodMarketId').change(function(){
        var finprodMarketId = $('#finprodMarketId').val();
        if(finprodMarketId){
            $('#finprodName').val($('#finprodMarketId').select2('data')[0].id).trigger('change');

        }else {
            $('#finprodName').val('').trigger('change');
        }
    });


    // //根据产品名称联动出产品代码
    // $('#portfolioId').change(function(){
    //     var portfolioId = $('#portfolioId').val();
    //     if(portfolioId){
    //         var dataPort = $('#portfolioId').select2('data')[0].object;
    //         var finprodId = dataPort.finprodId;
    //         $('#portfolioName').val(finprodId).trigger('change');
    //
    //     }else {
    //         $('#portfolioName').val('').trigger('change');
    //     }
    // });
    //
    // //根据资产名称联动出资产代码
    // $('#finprodMarketId').change(function(){
    //     var finprodMarketId = $('#finprodMarketId').val();
    //     if(finprodMarketId){
    //         var dataPort = $('#finprodMarketId').select2('data')[0].object;
    //         var finprodId = dataPort.finprodId;
    //         $('#finprodName').val(finprodId).trigger('change');
    //
    //     }else {
    //         $('#finprodName').val('').trigger('change');
    //     }
    // });

    /**
     * 保存或提交
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#basicInfoForm');
        object.customerId= $('#customerId').val();
        object.operateType = operateType;
        object.pageModel = pageModel;
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade002/save', object, "TrdDepositValuationTrade002_btn_save");
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade002/save', object, "TrdDepositValuationTrade002_btn_submit");
                }
            });
        });
    }


    /**
     * 画面加载完成
     */
    $(function() {
        // init();
        $('#TrdDepositValuationTrade002_btn_save').click(function () {
            saveOrSubmit('save');
        });


        $('#TrdDepositValuationTrade002_btn_submit').click(function () {
            saveOrSubmit("submit");
        });

    });



    //计算结算金额  结算金额=交易本金+应计利息+其他费用
    var mdateSettleAmtCalculate = function(){

            if($('#prinAmt').val() && $('#intAmt').val() && $('#otherAmt').val()&& $('#otherAmt').val().replace(/(^s*)|(s*$)/g, "").length !==0
                &&$('#prinAmt').val() !=='' && $('#intAmt').val()!==''&& $('#otherAmt').val()!==''){
                if($('#ps').val()=="T01"){ //交易方向为买入时 结算金额=交易本金+应计利息+其他费用
                    var unitValue = JoyinMath.doAdd($('#prinAmt').val(), $('#intAmt').val(),2);
                    var settleAmt = JoyinMath.doAdd(unitValue, $('#otherAmt').val(),2);
                }else if($('#ps').val()=="T02"){ //交易方向为卖出时 结算金额=交易本金+应计利息-其他费用
                    var unitValue = JoyinMath.doAdd($('#prinAmt').val(), $('#intAmt').val(),2);
                    var settleAmt = JoyinMath.doMinus(unitValue, $('#otherAmt').val(),2);
                }
                $('#settleAmt').val(settleAmt);
                // $('#dealAmt').val(settleAmt);
            }else{
                $('#settleAmt').val('');
                // $('#dealAmt').val('');
            }
    }


    //交易本金 联动
    $('#ps').change(function(){
        mdateSettleAmtCalculate();
    });

    //交易本金 联动
    $('#prinAmt').change(function(){
        mdateSettleAmtCalculate();
    });

    //应记利息 联动
    $('#intAmt').change(function(){
        mdateSettleAmtCalculate();
    });

    //其他费用 联动
    $('#otherAmt').change(function(){
        mdateSettleAmtCalculate();
    });

    //校验   交割日必须大于等于交易日
    var checkDate=function () {
        var tradeDate = $('#tradeDate').val();
        var settDate = $('#settDate').val();
        if(tradeDate&&settDate){
            if((new Date(tradeDate).DateDiff('d',settDate))<0){
                    Joyin.error("交割日必须大于等于交易日");
                    $('#settDate').val("");
            }
        }
    }
    $("#tradeDate").change(function () {
        checkDate();
    })

    $("#settDate").change(function () {
        checkDate();
    })

}());
