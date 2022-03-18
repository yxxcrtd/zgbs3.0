
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
            var tradeMarket = dataPort.tradeMarket;
            var assetType = dataPort.assetType;
            $('#assetAbbr').val($('#assetId').select2('data')[0].id).trigger('change');
            $('#tradeMarket').val(tradeMarket).trigger('change');
            $('#fundType').val(assetType).trigger('change');
        }else {
            $('#assetAbbr').val('').trigger('change');
            $('#tradeMarket').val('').trigger('change');
            $('#fundType').val('').trigger('change');
        }
    });


    $('#ps').change(function(){
        psChange();
    });


        var psChange = function () {
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

            }else{//默认
                $('#tradeInfo').hide();
                $('#subscriptionInfo').hide();
                $('#purchaseInfo').hide();
                $('#subConfirmInfo').hide();
                $('#purConfirmInfo').hide();
                $('#redeemInfo').hide();
            }
        };



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
        if(workflowId){
            psChange();
        }



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