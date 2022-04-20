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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade002/save', object, "TrdOutRightRepoValuationTrade002_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade002/save', object, "TrdOutRightRepoValuationTrade002_btn_submit");
                }
            });
        })
    }

    var init=function(){
        var result1 = JoyinMath.doAdd($('#fprice').val(), $('#dealFeeAmt').val(),2);
        var result2 = JoyinMath.doAdd(result1, $('#settFeeAmt').val(),2);
        $('#settleAmt').val(result2);
    }


    /**
     * 画面加载完成
     */
    $(function() {
    	//初始化画面
        init();
        // 绑定按钮事件
        $('#TrdOutRightRepoValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdOutRightRepoValuationTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

    });




    //计算结算金额  结算金额=首次全价总额+交易手续费+结算服务费
    var settleAmtCalculate = function(){
        if($('#fprice').val() != null && $('#fprice').val() != ''
            && $('#dealFeeAmt').val() != null && $('#dealFeeAmt').val() != ''
            && $('#settFeeAmt').val() != null && $('#settFeeAmt').val() != ''){
            var result1 = JoyinMath.doAdd($('#fprice').val(), $('#dealFeeAmt').val(),2);
            var result2 = JoyinMath.doAdd(result1, $('#settFeeAmt').val(),2);
            $('#settleAmt').val(result2);
        }else{
            $('#settleAmt').val('');
        }
    }

    //首次全价总额 联动
    $('#fprice').change(function(){
        settleAmtCalculate();  //计算结算金额
    });

    //交易手续费 联动
    $('#dealFeeAmt').change(function(){
        settleAmtCalculate(); //计算结算金额
    });

    //结算服务费 联动
    $('#settFeeAmt').change(function(){
        settleAmtCalculate(); //计算结算金额
    });




    /**
     * 产品代码联动带出产品名称，证券账户
     */

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

    //计算回购期限
    var termDate=function () {
        var vdate = $('#vdate').val();
        var mdate = $('#mdate').val();
        if(vdate&&mdate){
            var result1=(DateUtil.parse($('#mdate').val())-DateUtil.parse($('#vdate').val()))/1000/60/60/24;
            $('#term').val(result1);
        }
    }


    //校验   到期日必须大于等于起息日
    var checkDate01=function () {
        var vdate = $('#vdate').val();
        var mdate = $('#mdate').val();
        if(vdate&&mdate){
            if((new Date(vdate).DateDiff('d',mdate))<0){
                Joyin.error("到期日必须大于等于起息日");
                $('#mdate').val("");
            }
        }
    }

    $("#vdate").change(function () {
        checkDate01();     //校验起息日小于等于到期日
        termDate();
    });

    $("#mdate").change(function () {
        checkDate01(); //校验起息日小于等于到期日
        termDate();
    });



}());