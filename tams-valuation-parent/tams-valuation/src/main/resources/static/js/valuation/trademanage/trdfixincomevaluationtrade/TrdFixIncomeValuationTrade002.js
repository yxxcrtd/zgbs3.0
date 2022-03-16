
(function () {

    /*
    * 产品代码联动带出产品名称
    */
    $('#prodId').change(function(){
        var prodId = $('#prodId').val();
        if(prodId){
            $('#prodName').val($('#prodId').select2('data')[0].id).trigger('change');
        }else {
            $('#prodName').val('').trigger('change');
        }
    });

    /*
    * 资产代码联动带出资产名称
    */
    $('#assetId').change(function(){
        var assetId = $('#assetId').val();
        if(assetId){
            $('#assetName').val($('#assetId').select2('data')[0].id).trigger('change');
        }else {
            $('#assetName').val('').trigger('change');
        }
    });

    //净价=净价总额/本金金额
    var cpriceChange= function(){
        var capital = $('#capital').val();
        var cpriceAmt = $('#cpriceAmt').val();
        if (capital && cpriceAmt ) {
            var cprice = JoyinMath.doDiv(cpriceAmt, capital, 2, JoyinMath.ROUND_HALF_UP);
            $("#cprice").val(cprice).trigger("blur");
        }else {
            $("#cprice").val('').trigger("blur");
        }
    }

    //结算金额  结算金额=净价总额+应收利息总额+其他费用
    //卖出时：结算金额=净价总额+应收利息总额-其他费用
    var settleAmtChange = function(){
        var cpriceAmt = $('#cpriceAmt').val();
        var intAmt = $('#intAmt').val();
        var otherAmt = $('#otherAmt').val();
        var ps = $('#ps').val();
        if(cpriceAmt != null && cpriceAmt != ''
            && intAmt != null && intAmt != ''&& otherAmt != null && otherAmt != '' ){
            if(ps != '' && ps == 'T01'){
                var settleAmt1 = JoyinMath.doAdd(JoyinMath.doAdd(cpriceAmt, intAmt,2),otherAmt,2);
            }else if(ps != '' && ps == 'T02'){
                var settleAmt1 = JoyinMath.doMinus(JoyinMath.doAdd(cpriceAmt, intAmt,2),otherAmt,2);
            }
            $('#settleAmt').val(settleAmt1);
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
        object.pageId=pageId;
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002/save', object, "TrdFixIncomeValuationTrade002_btn_save");
                }else{
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002/save', object, "TrdFixIncomeValuationTrade002_btn_submit");
                }
            });

        });
    }

    /**
     * 画面加载完成
     */
    $(function() {
        // init();

        //本金金额
        $('#capital').change(function(){
            cpriceChange();
        });
        //净价总额
        $('#cpriceAmt').change(function(){
            cpriceChange();
        });

        //结算金额联动事件
        //净价总额
        $('#cpriceAmt').change(function(){
            settleAmtChange();
        });
        //应收利息总额
        $('#intAmt').change(function(){
            settleAmtChange();
        });
        //其他费用
        $('#otherAmt').change(function(){
            settleAmtChange();
        });

        //保存
        $('#TrdFixIncomeValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdFixIncomeValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());