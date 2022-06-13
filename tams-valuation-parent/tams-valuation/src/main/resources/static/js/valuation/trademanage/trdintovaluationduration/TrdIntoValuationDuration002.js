$(function() {
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration002/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration002/save', object, "TrdIntoValuationDuration002_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration002/save', object, "TrdIntoValuationDuration002_btn_submit");
                }
            });
        })
    }

    /**
     * 画面加载完成
     */
    $(function() {
    	//初始化画面
        // init();
        // 绑定按钮事件
        $('#TrdIntoValuationDuration002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdIntoValuationDuration002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

    });

    //计算结算金额  结算金额=实收本金+实收利息
    var settleAmtCalculate = function(){
        if( $('#actCapi').val() != null && $('#actCapi').val() != ''
            && $('#actInt').val() != null && $('#actInt').val() != ''){
            var result1 = JoyinMath.doAdd($('#actCapi').val(), $('#actInt').val(),2);
            $('#settleAmt').val(result1);
        }else{
            $('#settleAmt').val('');
        }
    }

    //实收本金 联动
    $('#actCapi').change(function(){
        settleAmtCalculate(); //计算结算金额
    });

    //实收利息 联动
    $('#actInt').change(function(){
        settleAmtCalculate(); //计算结算金额
    });


    /**
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

    /**
     * 资产代码联动带出资产简称，交易场所
     */
    $('#secId').change(function(){
        var secId = $('#secId').val();
        if(secId){
            $('#secAbbr').val($('#secId').select2('data')[0].id).trigger('change');
        }else {
            $('#secAbbr').val('').trigger('change');
        }
    });

    //校验   计息结束日必须大于等于计息开始日
    var checkDate01=function () {
        var vdate = $('#vdate').val();
        var mdate = $('#mdate').val();
        if(vdate&&mdate){
            if((new Date(vdate).DateDiff('d',mdate))<0){
                Joyin.error("计息结束日必须大于等于计息开始日");
                $('#mdate').val("");
            }
        }
    }

    //计息开始日 联动
    $('#vdate').change(function () {
        checkDate01();
    });
    //计息结束日 联动
    $('#mdate').change(function () {
        checkDate01();
    });


}());