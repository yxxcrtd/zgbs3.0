
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
            //现金分红 T11、红利再投 T12、份额强增 C3、份额强减 C6
            var fe = "C3,C6";    //清算信息-份额强增、份额强减
            var xjfh = "T11";    //清算信息-现金分红
            var hl = "T12";      //清算信息-红利再投
            if(xjfh.indexOf(psStr) >-1){ //清算信息-现金分红
                $('#value_div_xjfh').show();
                $('#value_div_hl').hide();
                $('#value_div_fe').hide();
            }else if(hl.indexOf(psStr) >-1){ //清算信息-红利再投
                $('#value_div_xjfh').hide();
                $('#value_div_hl').show();
                $('#value_div_fe').hide();
            }else if(fe.indexOf(psStr) >-1){//清算信息-份额强增、份额强减
                $('#value_div_xjfh').hide();
                $('#value_div_hl').hide();
                $('#value_div_fe').show();
            }else{//默认
                $('#value_div_xjfh').show();
                $('#value_div_hl').show();
                $('#value_div_fe').show();
            }

        }
    });



    //分红金额 联动
    $('#bonusAmt').change(function(){
        unitValueCalculate();
        //默认等于分红金额，可修改
        var bonusAmt = $('#bonusAmt').val();
        $('#mdateSettleAmt').val(bonusAmt);
    });

    //交易份额 联动
    $('#nums').change(function(){
        unitValueCalculate();
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
            $('#unitCprice').val('')
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfundbonusvaluationtrade/TrdFundBonusValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                        WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdfundbonusvaluationtrade/TrdFundBonusValuationTrade002/save', object, "TrdFundBonusValuationTrade002_btn_save");
                }else{
                        WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdfundbonusvaluationtrade/TrdFundBonusValuationTrade002/save', object, "TrdFundBonusValuationTrade002_btn_submit");
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
        $('#TrdFundBonusValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdFundBonusValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());