$(function() {


    /**
     * 获取表单参数
     */
    var submitForm = function() {
        return $.serializeToObject('#submitForm');
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
        var object = $.serializeToObject('#submitForm', false);
        object.pageModel = pageModel;
        object.operateType = operateType;
        object.pageId = pageId;

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade003/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade003/save', object, "TrdOutRightRepoValuationTrade003_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade003/save', object, "TrdOutRightRepoValuationTrade003_btn_submit");
                }
            });
        })
    }

    var init=function(){
        // $('#cpdm').hide();
        // $('#zcdm').hide();
        $('#endDate').hide(); //到期日隐藏
        var mdate=$('#mdate').val();
        $('#tradeDate').val(mdate);
        $('#settleDate').val(mdate);
        $('#ps').textSelect("setValue",'T07');
        var settleAmt=JoyinMath.doAdd($('#mfprice').val(), $('#otherAmt').val(), 2);
        $('#settleAmt').val(settleAmt);
    }
    /**
     * 画面加载完成
     */
    $(function() {
    	//初始化画面
        init();
        // 绑定按钮事件
        $('#TrdOutRightRepoValuationTrade003_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdOutRightRepoValuationTrade003_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

    });


    //计算结算金额  结算金额=到期全价总额+其他费用
    var settleAmtCalculate = function(){
        if($('#mfprice').val() != null && $('#mfprice').val() != ''
            && $('#otherAmt').val() != null && $('#otherAmt').val() != ''){
            var result1 = JoyinMath.doAdd($('#mfprice').val(), $('#otherAmt').val(),2);
            $('#settleAmt').val(result1);
        }else{
            $('#settleAmt').val('');
        }
    }

    //到期全价总额 联动
    $('#mfprice').change(function(){
        settleAmtCalculate();  //计算结算金额
    });

    //其他费用 联动
    $('#otherAmt').change(function(){
        settleAmtCalculate(); //计算结算金额
    });



    $('#portfolioId').change(function(){
        var portfolioId = $('#portfolioId').val();
        if(portfolioId){
            $('#portfolioName').val($('#portfolioId').select2('data')[0].id).trigger('change');

        }else {
            $('#portfolioName').val('').trigger('change');
        }
    });

    /**
     * 资产代码联动带出资产简称，交易场所
     */
    $('#finprodMarketId').change(function(){
        var finprodMarketId = $('#finprodMarketId').val();
        if(finprodMarketId){
            var dataPort = $('#finprodMarketId').select2('data')[0].object;
            // var term = dataPort.term;
            // var rebackPrice = JoyinMath.doMul(dataPort.rebackPrice, 100, 4);

            $('#finprodName').val($('#finprodMarketId').select2('data')[0].id).trigger('change');
            // $('#term').val(term).trigger('change');
            // $('#rebackPrice').val(rebackPrice).trigger('change');

        }else {
            $('#finprodName').val('').trigger('change');
            // $('#term').val('').trigger('change');
            // $('#rebackPrice').val('').trigger('change');
        }
    });


}());