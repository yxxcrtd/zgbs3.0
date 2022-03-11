
(function () {

    $('#prodId').change(function(){
        var prodId = $('#prodId').val();
        if(prodId){
            $('#prodName').val($('#prodId').select2('data')[0].id).trigger('change');

        }else {
            $('#prodName').val('').trigger('change');
        }
    });

    $('#assetId').change(function(){
        var assetId = $('#assetId').val();
        if(assetId){
            var dataPort = $('#assetId').select2('data')[0].object;
            var profitType = dataPort.profitType;
            $('#assetAbbr').val($('#assetId').select2('data')[0].id).trigger('change');
            $('#profitType').val(profitType).trigger('change');
        }else {
            $('#assetAbbr').val('').trigger('change');
            $('#profitType').val('').trigger('change');
        }
    });

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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade003/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade003/save', object, "TrdPlanValuationTrade003_btn_save");
                }else{
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade003/save', object, "TrdPlanValuationTrade003_btn_submit");
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
        tradeAmtArriveInit();
        // init();
        //保存
        $('#TrdPlanValuationTrade003_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdPlanValuationTrade003_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());