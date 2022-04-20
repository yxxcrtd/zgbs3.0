
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

    //即期汇率 联动
    $('#unitCprice').change(function(){
        unitFpriceCalculate();
    });

    //远期点 联动
    $('#unitInt').change(function(){
        unitFpriceCalculate();
    });

    /**
     * 远期全价 = 即期汇率+远期点
     */
    var unitFpriceCalculate = function(){
        $('#unitFprice').val(JoyinMath.doAdd($('#unitCprice').val(), $('#unitInt').val(), 2));
    }


    /**
     * 差额收付金额 difPayAmt
     * 远期价格 unitFprice
     * 定价日即期汇率 pricedDateRate
     * 交易货币金额 dealCurAmt
     * 当清算模式（settMode）选择差额清算时允许编辑，差额收付金额=|远期价格-定价日即期汇率|*交易货币金额
     */
    $('#settMode').change(function(){
        if($('#settMode') && $('#settMode').val()=='DIF-SETT'){
            var price  = Math.abs(JoyinMath.doMinus($('#unitFprice').val(),$('#pricedDateRate').val(),2));
            $('#difPayAmt').val(JoyinMath.doMul(price,$('#dealCurAmt').val(),2));
        }
    });

    //远期点 联动
    $('#dealCurAmt').change(function(){
        corCcyAmtAmtCalculate();
    });
    //远期点 联动
    $('#unitFprice').change(function(){
        corCcyAmtAmtCalculate();
    });
    /**
     * 对应货币金额  计算  = 交易货币金额*远期全价
     *                     dealCurAmt  unitFprice
     */
    var corCcyAmtAmtCalculate = function(){
        //结算金额=全价总额+其他费用
        var mdateSettleAmt=JoyinMath.doMinus($('#dealCurAmt').val(), $('#unitFprice').val(), 2);
        $('#mdateSettleAmt').val(mdateSettleAmt);
    }


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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfxforwardvaluationtrade/TrdFxForwardValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                     WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdfxforwardvaluationtrade/TrdFxForwardValuationTrade002/save', object, "TrdFxForwardValuationTrade002_btn_save");
                }else{
                     WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdfxforwardvaluationtrade/TrdFxForwardValuationTrade002/save', object, "TrdFxForwardValuationTrade002_btn_submit");
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
        $('#TrdFxForwardValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdFxForwardValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());