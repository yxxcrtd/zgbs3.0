
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
            // $("#tradeMarket").val("01").trigger("change");
            // $("#tradeMarket").attr("disabled", "disabled");
        }else {
            $('#finprodName').val('').trigger('change');
            $('#tradeMarket').val('').trigger('change');
        }
    });


    $('#ps').change(function(){
        if($('#ps').val() && $('#ps').val() != null && $('#ps').val() != ''){
            var psStr = $('#ps').val();
            //买入 T01、卖出T02、认购 T52、申购 T04、认购确认 T18、申购确认T05、赎回确认 T19、赎回到账 T24
            var jyps = "T01,T02";//交易信息
            var rgps = "T52";//认购信息
            var sqps = "T04";    //申请信息
            var rgqr = "T18";    //确认信息 认购确认
            var sgqr = "T05";    //确认信息 申购确认
            var shqr = "T19";    //确认信息 赎回确认
            if(jyps.indexOf(psStr) >-1){ //交易信息
                $('#value_div_jy').show();
                $('#value_div_rg').hide();
                $('#value_div_sq').hide();
                $('#value_div_rgqr').hide();
                $('#value_div_sgqr').hide();
                $('#value_div_shqr').hide();

                //交易日期、结算日期默认系统平台日
                $('#tradeDate').val(platDate);// 默认平台日
                $('#settleDate').val(platDate);// 默认平台日
            }else if(rgps.indexOf(psStr) >-1){ //认购信息
                $('#value_div_jy').hide();
                $('#value_div_rg').show();
                $('#value_div_sq').hide();
                $('#value_div_rgqr').hide();
                $('#value_div_sgqr').hide();
                $('#value_div_shqr').hide();

                //交易日期、结算日期默认系统平台日
                $('#tradeDate').val(platDate);// 默认平台日
                $('#settleDate').val(platDate);// 默认平台日
            }else if(sqps.indexOf(psStr) >-1){//申请信息
                $('#value_div_jy').hide();
                $('#value_div_rg').hide();
                $('#value_div_sq').show();
                $('#value_div_rgqr').hide();
                $('#value_div_sgqr').hide();
                $('#value_div_shqr').hide();

                //交易日期、结算日期默认系统平台日
                $('#tradeDate').val(platDate);// 默认平台日
                $('#settleDate').val(platDate);// 默认平台日
            }else if(rgqr.indexOf(psStr) >-1){//认购确认信息
                $('#value_div_jy').hide();
                $('#value_div_rg').hide();
                $('#value_div_sq').hide();
                $('#value_div_rgqr').show();
                $('#value_div_sgqr').hide();
                $('#value_div_shqr').hide();

                //确认日期默认系统平台日
                $('#tradeDate').val(platDate);// 默认平台日
            }else if(sgqr.indexOf(psStr) >-1){//申购确认信息
                $('#value_div_jy').hide();
                $('#value_div_rg').hide();
                $('#value_div_sq').hide();
                $('#value_div_rgqr').hide();
                $('#value_div_sgqr').show();
                $('#value_div_shqr').hide();

                //确认日期默认系统平台日
                $('#tradeDate').val(platDate);// 默认平台日
            }else if(shqr.indexOf(psStr) >-1){//赎回确认信息
                $('#value_div_jy').hide();
                $('#value_div_rg').hide();
                $('#value_div_sq').hide();
                $('#value_div_rgqr').hide();
                $('#value_div_sgqr').hide();
                $('#value_div_shqr').show();

                //交易日期、结算日期默认系统平台日
                $('#tradeDate').val(platDate);// 默认平台日
                $('#settleDate').val(platDate);// 默认平台日
            }else{//默认
                $('#value_div_jy').show();
                $('#value_div_rg').show();
                $('#value_div_sq').show();
                $('#value_div_rgqr').show();
                $('#value_div_sgqr').show();
                $('#value_div_shqr').show();
            }

        }

    });



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

        // var getSecDetailType = function() {
        //     return {
        //         secDetailType: $.trim($('#secDetailType').val()),
        //     }
        // };
        object.tradeAmt=$.trim($('#tradeAmt').val());
        object.operateType=operateType;
        object.pageId=pageId;
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002/save', object, "TrdFundValuationTrade002_btn_save");
                }else{
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002/save', object, "TrdFundValuationTrade002_btn_submit");
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
        $('#TrdFundValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdFundValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());