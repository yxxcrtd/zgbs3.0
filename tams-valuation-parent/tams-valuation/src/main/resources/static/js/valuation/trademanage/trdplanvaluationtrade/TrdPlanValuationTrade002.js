
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


    $('#ps').change(function(){
        psChange();
    });

    var psChange = function () {
        debugger;
        var ps = $('#ps').val();
        //买入 T01、卖出T02、认购 T52、申购 T04、认购确认 T18、申购确认T05、赎回确认 T19、赎回到账 T24
        if(ps == 'T01' || ps == 'T02'){ //交易信息
            $('#tradeInfo').show();
            $('#subscriptionInfo').hide();
            $('#purchaseInfo').hide();
            $('#subConfirmInfo').hide();
            $('#purConfirmInfo').hide();
            $('#redeemInfo').hide();
        }else if(ps == 'T52'){ //认购信息
            $('#tradeInfo').hide();
            $('#subscriptionInfo').show();
            $('#purchaseInfo').hide();
            $('#subConfirmInfo').hide();
            $('#purConfirmInfo').hide();
            $('#redeemInfo').hide();

        }else if(ps == 'T04'){//申请信息
            $('#tradeInfo').hide();
            $('#subscriptionInfo').hide();
            $('#purchaseInfo').show();
            $('#subConfirmInfo').hide();
            $('#purConfirmInfo').hide();
            $('#redeemInfo').hide();

        }else if(ps == 'T18'){//认购确认信息
            $('#tradeInfo').hide();
            $('#subscriptionInfo').hide();
            $('#purchaseInfo').hide();
            $('#subConfirmInfo').show();
            $('#purConfirmInfo').hide();
            $('#redeemInfo').hide();

        }else if(ps == 'T05'){//申购确认信息
            $('#tradeInfo').hide();
            $('#subscriptionInfo').hide();
            $('#purchaseInfo').hide();
            $('#subConfirmInfo').hide();
            $('#purConfirmInfo').show();
            $('#redeemInfo').hide();

        }else if(ps == 'T19'){//赎回确认信息
            $('#tradeInfo').hide();
            $('#subscriptionInfo').hide();
            $('#purchaseInfo').hide();
            $('#subConfirmInfo').hide();
            $('#purConfirmInfo').hide();
            $('#redeemInfo').show();

        }else if(ps == 'T24'){//赎回确认信息
            $('#tradeInfo').hide();
            $('#subscriptionInfo').hide();
            $('#purchaseInfo').hide();
            $('#subConfirmInfo').hide();
            $('#purConfirmInfo').hide();
            $('#redeemInfo').show();

        }else{//默认
            $('#tradeInfo').hide();
            $('#subscriptionInfo').hide();
            $('#purchaseInfo').hide();
            $('#subConfirmInfo').hide();
            $('#purConfirmInfo').hide();
            $('#redeemInfo').hide();
        }
    };

    //结算金额  买入时：结算金额=成交金额+投资顾问费+其他费用  卖出时：结算金额=成交金额-投资顾问费-其他费用
    var settleAmtChange = function(){
        var tradeAmt = $('#tradeAmt').val();
        //var advFeeOut = $('#advFeeOut').val();
        //var otherAmt = $('#otherAmt').val();
        var ps = $('#ps').val();
        if(tradeAmt != null && tradeAmt != ''
           // && advFeeOut != null && advFeeOut != ''
           // && otherAmt != null && otherAmt != ''
        ){
            if(ps == 'T01'){
                //var settleAmt = JoyinMath.doAdd(tradeAmt, advFeeOut,2);
                $('#settleAmt').val(tradeAmt);
            }else if(ps == 'T02'){
               // var  settleAmt = JoyinMath.doMinus(tradeAmt, advFeeOut,2);
                $('#settleAmt').val(tradeAmt);
            }
        }else{
            $('#settleAmt').val('');
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

        if(workflowId){
            psChange();
        }

        //结算金额联动事件
        //成交金额
        $('#tradeAmt').change(function(){
            settleAmtChange();
        });
        //投资顾问费
        $('#advFeeOut').change(function(){
            settleAmtChange();
        });
        // //其他费用
        // $('#otherAmt').change(function(){
        //     settleAmtChange();
        // });

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