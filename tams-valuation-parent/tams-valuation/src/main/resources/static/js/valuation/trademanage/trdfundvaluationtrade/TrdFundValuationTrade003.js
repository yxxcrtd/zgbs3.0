
(function () {

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

    var tradeAmtArriveInit = function(){
        var redConfirmAmt = $('#redConfirmAmt').val();
        var redFee = $('#redFee').val();
        if((redConfirmAmt != '' || redConfirmAmt != null)
            && (redFee != '' || redFee != null)){
            var tradeAmtArrive = JoyinMath.doMinus(redConfirmAmt, redFee,2);
            $('#tradeAmtArrive').val(tradeAmtArrive);
        }
    }

    /**
     * 画面加载完成
     */
    $(function() {
        tradeAmtArriveInit();

        //赎回费
        $('#redFee').change(function(){
            tradeAmtArriveInit();
        });
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