
(function () {

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
                $('#tradeAmt').val('');
                $('#confirmTradeAmt').val('');
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

    //净价总额 联动
    $('#cpriceAmt').change(function(){
        netPriceCalculate();
    });

    //本金金额 联动
    $('#tradeAmt').change(function(){
        netPriceCalculate();
    });

    /**
     * 净价计算 净价总额/本金金额
     */
    var netPriceCalculate = function(){
        if($('#cpriceAmt').val() && $('#cpriceAmt').val() !==null && $('#cpriceAmt').val() !=''
        &&  $('#tradeAmt').val() && $('#tradeAmt').val() !=null && $('#tradeAmt').val() !='' && $('#tradeAmt').val()!=0){
            var netPrice = JoyinMath.doDiv($('#cpriceAmt').val(),$('#tradeAmt').val(),2);
            $('#netPrice').val(netPrice);
        }else{
            $('#netPrice').val('');
        }

    }

    //全价总额 联动
    $('#fpriceAmt').change(function(){
        cpriceAmtCalculate();
    });

    //应收利息总额 联动
    $('#intAmt').change(function(){
        cpriceAmtCalculate();
    });

    /**
     * 净价总额计算
     */
    var cpriceAmtCalculate = function(){
        if($('#fpriceAmt').val() && $('#fpriceAmt').val() != null && $('#fpriceAmt').val() != ''
                && $('#intAmt').val() && $('#intAmt').val() != null && $('#intAmt').val() != '' ){
            var cpriceAmt = JoyinMath.doMinus($('#fpriceAmt').val(), $('#intAmt').val(), 2);
            $('#cpriceAmt').val(cpriceAmt)
        }else{
            $('#cpriceAmt').val('')
        }
    }

    //全价总额 联动
    $('#fpriceAmt').change(function(){
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
        //结算金额=全价总额+其他费用
        var mdateSettleAmt=JoyinMath.doAdd($('#fpriceAmt').val(), $('#feeAmtQT').val(), 2);
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
        object.pageId=pageId;
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfxspotvaluationtrade/TrdFxSpotValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdfxspotvaluationtrade/TrdFxSpotValuationTrade002/save', object, "TrdFxSpotValuationTrade002_btn_save");
                }else{
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdfxspotvaluationtrade/TrdFxSpotValuationTrade002/save', object, "TrdFxSpotValuationTrade002_btn_submit");
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
        $('#TrdFxSpotValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdFxSpotValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());