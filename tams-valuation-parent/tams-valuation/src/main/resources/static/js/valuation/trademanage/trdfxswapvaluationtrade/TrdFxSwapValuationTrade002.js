
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



    /**
     * 货币对 联动
     */
    $('#curPair').change(function(){
        if($('#curPair').val() == ""){
            $("#dealCur").val("");
            $("#corCcy").val("");
        }
        corCcyDeal();
    });

    /**
     * 交易货币、对应货币联动
     */
    $('#dealCur').change(function(){
        if($('#curPair').val() == ""){
            $("#corCcy").val("");
            return;
        }
        corCcyDeal();

    });

    /**
     * 对应货币联动展示
     */
    var corCcyDeal = function(){
        var index=document.getElementById("dealCur").selectedIndex;
        var length=document.getElementById("dealCur").length;
        var dealCur=$('#dealCur').val();
        if(dealCur == ""){
            $("#corCcy").empty();
        }
        var total=length;
        if(index != 0){
            $("#corCcy").empty();
            if(length > 2){
                var option = document.createElement("option");
                $(option).val(document.getElementById("dealCur").options[total-index].value);
                $(option).text(document.getElementById("dealCur").options[total-index].text);
                $('#corCcy').append(option);
                $("#corCcy option[value="+document.getElementById("dealCur").options[total-index].value+"]").prop("selected",true);
            }
        }
    }



    //即期汇率 联动
    // $('#unitCprice').change(function(){
    //     unitFpriceCalculate();
    // });

    //远期点 联动
    // $('#unitInt').change(function(){
    //     unitFpriceCalculate();
    // });

    /**
     * 远期全价 = 即期汇率+远期点
     */
    // var unitFpriceCalculate = function(){
    //     $('#unitFprice').val(JoyinMath.doAdd($('#unitCprice').val(), $('#unitInt').val(), 2));
    // }


    /**
     * 差额收付金额 difPayAmt
     * 远期价格 unitFprice
     * 定价日即期汇率 pricedDateRate
     * 交易货币金额 dealCurAmt
     * 当清算模式（settMode）选择差额清算时允许编辑，差额收付金额=|远期价格-定价日即期汇率|*交易货币金额
     */
    // $('#settMode').change(function(){
    //     if($('#settMode') && $('#settMode').val()=='DIF-SETT'){
    //         var price  = Math.abs(JoyinMath.doMinus($('#unitFprice').val(),$('#pricedDateRate').val(),2));
    //         $('#difPayAmt').val(JoyinMath.doMul(price,$('#dealCurAmt').val(),2));
    //     }
    // });

    //远期点 联动
    // $('#dealCurAmt').change(function(){
    //     corCcyAmtAmtCalculate();
    // });
    //远期点 联动
    // $('#unitFprice').change(function(){
    //     corCcyAmtAmtCalculate();
    // });
    /**
     * 对应货币金额  计算  = 交易货币金额*远期全价
     *                     dealCurAmt  unitFprice
     */
    // var corCcyAmtAmtCalculate = function(){
    //     //结算金额=全价总额+其他费用
    //     var mdateSettleAmt=JoyinMath.doMinus($('#dealCurAmt').val(), $('#unitFprice').val(), 2);
    //     $('#mdateSettleAmt').val(mdateSettleAmt);
    // }


    /**
     * 结算金额  计算
     */
    // var mdateSettleAmtCalculate = function(){
    //     //结算金额=全价总额+其他费用
    //     var mdateSettleAmt=JoyinMath.doAdd($('#fpriceAmt').val(), $('#feeAmtQT').val(), 2);
    //     $('#mdateSettleAmt').val(mdateSettleAmt);
    // }

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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfxswapvaluationtrade/TrdFxSwapValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdfxswapvaluationtrade/TrdFxSwapValuationTrade002/save', object, "TrdFxSwapValuationTrade002_btn_save");
                }else{
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdfxswapvaluationtrade/TrdFxSwapValuationTrade002/save', object, "TrdFxSwapValuationTrade002_btn_submit");
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
        $('#TrdFxSwapValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdFxSwapValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());