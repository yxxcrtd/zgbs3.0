
(function () {

    $('#portfolioId').change(function(){
        var portfolioId = $('#portfolioId').val();
        if(portfolioId){
            var dataPort = $('#portfolioId').select2('data')[0].object;
            var finprodId = dataPort.finprodId;
            var finprodType =dataPort.finprodType;
            var profitType =dataPort.profitType;
            $('#portfolioName').val(finprodId).trigger('change');

        }else {
            $('#portfolioName').val('').trigger('change');
        }
    });

    $('#finprodId').change(function(){
        var finprodId = $('#finprodId').val();
        if(finprodId){
            var dataPort = $('#finprodId').select2('data')[0].object;
            var finprodId = dataPort.finprodId;
            $('#finprodName').val(finprodId).trigger('change');

        }else {
            $('#finprodName').val('').trigger('change');
        }
    });


    $('#ps').change(function(){
        if($('#ps').val() && $('#ps').val() != null && $('#ps').val() != ''){
            var psStr = $('#ps').val();
            var sqps = "T01,T02,T52,T04";//申请信息  --买入 T01、卖出T02、认购 T52、申购 T04
            var qrps = "T18,T05,T19";    //确认信息  --认购确认 T18、申购确认T05、赎回确认 T19
            var dzps = "T24";               //到账信息  --赎回到账 T24
            if(sqps.indexOf(psStr) >-1){
                $('#value_div_sq').show();
                $('#value_div_qr').hide();
                $('#value_div_dz').hide();
                $('#confirmTradeAmt').val('');
                $('#settleAmt').val('');
            }else if(qrps.indexOf(psStr) >-1){
                $('#value_div_sq').hide();
                $('#value_div_qr').show();
                $('#value_div_dz').hide();
                $('#tradeAmt').val('');
                $('#settleAmt').val('');
            }else if(dzps.indexOf(psStr) >-1){
                $('#value_div_sq').hide();
                $('#value_div_qr').hide();
                $('#value_div_dz').show();
                $('#tradeAmt').val('')
                $('#confirmTradeAmt').val('')
            }else{
                $('#value_div_sq').show();
                $('#value_div_qr').show();
                $('#value_div_dz').show();
                $('#tradeAmt').val('');
                $('#confirmTradeAmt').val('');
                $('#settleAmt').val('');
            }

        }

    });

    //确认金额 联动
    $('#confirmTradeAmt').change(function(){
        unitValueCalculate();
    });

    //交易份额 联动
    $('#confirmNums').change(function(){
        unitValueCalculate();
    });

    /**
     * 单位净值计算
     */
    var unitValueCalculate = function(){
        if($('#confirmTradeAmt').val() && $('#confirmTradeAmt').val() != null && $('#confirmTradeAmt').val() != ''
                && $('#confirmNums').val() && $('#confirmNums').val() != null && $('#confirmNums').val() != '' && $('#confirmNums').val() !=0){
            var unitValue = JoyinMath.doDiv($('#confirmTradeAmt').val(), $('#confirmNums').val(), 2);
            $('#unitValue').val(unitValue)
        }else{
            $('#unitValue').val('')
        }
    }

    //交易金额 联动
    $('#tradeAmt').change(function(){
        mdateSettleAmtCalculate();
    });

    //交易金额 联动
    $('#confirmTradeAmt').change(function(){
        mdateSettleAmtCalculate();
    });

    //到账金额 联动
    $('#settleAmt').change(function(){
        mdateSettleAmtCalculate();
    });

    //交易费用 联动
    $('#feeAmt').change(function(){
        mdateSettleAmtCalculate();
    });
    //投资顾问费 联动
    $('#advFeeOut').change(function(){
        mdateSettleAmtCalculate();
    });
    //其他费用 联动
    $('#feeAmtQT').change(function(){
        mdateSettleAmtCalculate();
    });

    /**
     * 结算金额  计算
     */
    var mdateSettleAmtCalculate = function(){
        //总费用=交易费用+投资顾问费+其他费用
        var totalFee = JoyinMath.doAdd(JoyinMath.doAdd($('#feeAmt').val(), $('#advFeeOut').val(), 2),$('#feeAmtQT').val(),2);
        var mdateSettleAmt =0.00;
        //tradeAmt 交易金额 confirmTradeAmt 确认金额  settleAmt 到账金额
        if($('#tradeAmt').val() && $('#tradeAmt').val() != null && $('#tradeAmt').val() != ''){
            mdateSettleAmt=JoyinMath.doAdd($('#tradeAmt').val(), totalFee, 2);
        }else if($('#confirmTradeAmt').val() && $('#confirmTradeAmt').val() != null && $('#confirmTradeAmt').val() != ''){
            mdateSettleAmt=JoyinMath.doAdd($('#confirmTradeAmt').val(), totalFee, 2);
        }else if($('#settleAmt').val() && $('#settleAmt').val() != null && $('#settleAmt').val() != ''){
            mdateSettleAmt=JoyinMath.doAdd($('#settleAmt').val(), totalFee, 2);
        }else{
            mdateSettleAmt= totalFee;
        }
        $('#mdateSettleAmt').val(mdateSettleAmt);
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
        var object = $.serializeToObject('#basicInfoForm',true);
        object.operateType=operateType;
        object.pageId = pageId;
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade002/save', object, "TrdPlanValuationTrade002_btn_save");
                }else{
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade002/save', object, "TrdPlanValuationTrade002_btn_submit");
                }
            });

        });
    }
    /**
     * 界面初始化
     * @returns
     */
    function init(){
    }
    /**
     * 画面加载完成
     */
    $(function() {
        // init();
        //保存
        $('#TrdPlanValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdPlanValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());