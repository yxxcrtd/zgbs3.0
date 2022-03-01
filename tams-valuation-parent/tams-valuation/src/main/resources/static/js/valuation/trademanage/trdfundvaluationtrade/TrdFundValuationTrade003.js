
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


    $('#ps').change(function(){
        if($('#ps').val() && $('#ps').val() != null && $('#ps').val() != ''){
            var psStr = $('#ps').val();
            //买入 T01、卖出T02、认购 T52、申购 T04、认购确认 T18、申购确认T05、赎回确认 T19、赎回到账 T24
            var shdz = "T24";
            if(shdz.indexOf(psStr) >-1){ //赎回到账
                //到账日期默认系统平台日
                $('#shdzSettleDate').val(platDate);// 默认平台日
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

        object.tradeAmt=$.trim($('#tradeAmt').val());
        object.operateType=operateType;
        object.pageId=pageId;
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003/save', object, "TrdFundValuationTrade003_btn_save");
                }else{
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003/save', object, "TrdFundValuationTrade003_btn_submit");
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
        $('#TrdFundValuationTrade003_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdFundValuationTrade003_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());