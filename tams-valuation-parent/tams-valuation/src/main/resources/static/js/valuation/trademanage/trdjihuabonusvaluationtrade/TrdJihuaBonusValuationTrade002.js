
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
            var tradeMarket = dataPort.tradeMarket;
            $('#finprodName').val(finprodId).trigger('change');
            $('#tradeMarket').val(tradeMarket).trigger('change');
        }else {
            $('#finprodName').val('').trigger('change');
            $('#tradeMarket').val('').trigger('change');
        }
    });

    //分红金额 联动
    $('#bonusAmt').change(function(){
        //单位分红净值计算
        unitValueCalculate();
        //结算金额计算
        mdateSettleAmtCalculate();
    });

    //交易份额 联动
    $('#nums').change(function(){
        //单位分红净值计算
        unitValueCalculate();
    });

    //其他费用 联动
    $('#feeAmtQT').change(function(){
        //结算金额计算
        mdateSettleAmtCalculate();
    });

    /**
     * 单位分红净值计算
     */
    var unitValueCalculate = function(){
        if($('#bonusAmt').val() && $('#bonusAmt').val() != null && $('#bonusAmt').val() != ''
                && $('#nums').val() && $('#nums').val() != null && $('#nums').val() != '' && $('#confirmNums').val() !=0){
            var unitCprice = JoyinMath.doDiv($('#bonusAmt').val(), $('#nums').val(), 2);
            $('#unitCprice').val(unitCprice)
        }else{
            $('#unitValue').val('')
        }
    }

    //结算金额计算=分红金额+其他费用，可修改
    var mdateSettleAmtCalculate = function(){
        var mdateSettleAmtTotal = JoyinMath.doAdd($('#bonusAmt').val(),$('#feeAmtQT').val(),2);
        $('#mdateSettleAmt').val(mdateSettleAmtTotal);
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                    Joyin.confirm(message, function() {
                        WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/save', object, "TrdJihuaValuationTrade002_btn_save");
                    });
                }else{
                    Joyin.confirm(message, function() {
                        WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/save', object, "TrdJihuaValuationTrade002_btn_sbumit");
                    });
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
        $('#TrdJihuaBonusValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdJihuaBonusValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());