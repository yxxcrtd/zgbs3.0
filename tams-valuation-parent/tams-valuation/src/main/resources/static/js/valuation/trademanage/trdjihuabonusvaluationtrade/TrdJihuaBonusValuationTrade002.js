
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
            $('#assetAbbr').val($('#assetId').select2('data')[0].id).trigger('change');
            $('#tradeMarket').val(tradeMarket).trigger('change');

        }else {
            $('#assetAbbr').val('').trigger('change');
            $('#tradeMarket').val('').trigger('change');
        }
    });

    $('#ps').change(function(){
        psChange();
    });

    var psChange = function () {
        var ps = $('#ps').val();
        //现金分红 T11、红利再投 T12、份额强增 C3、份额强减 C6
        if(ps == 'T11' ){ //现金分红
            $('#xjfh').show();
            $('#hlzt').hide();
        }else if(ps == 'T12'){ //红利再投
            $('#xjfh').hide();
            $('#hlzt').show();
        }else{//默认
            $('#xjfh').hide();
            $('#hlzt').hide();
        }
    };

    //现金分红 结算金额 默认等于分红金额
    var settleAmt = function(){
        var bonusAmt = $('#bonusAmt').val();
        if(bonusAmt != null && bonusAmt != ''){
            $('#settleAmt').val(bonusAmt).trigger('blur');
        }else{
            $('#settleAmt').val('');
        }
    }

    //红利再投 单位分红净值=分红金额/分红份额
    var unitBonusUnitValueChange = function(){
        var bonusAmtReInvest = $('#bonusAmtReInvest').val();
        var dealNumReInvest = $('#dealNumReInvest').val();
        if(bonusAmtReInvest != null && bonusAmtReInvest != ''
            && dealNumReInvest != null && dealNumReInvest != ''){
            var unitBonusUnitValue =JoyinMath.doDiv(bonusAmtReInvest, dealNumReInvest,4,JoyinMath.ROUND_HALF_UP);
            $('#unitBonusUnitValue').val(unitBonusUnitValue);
        }else{
            $('#unitBonusUnitValue').val('');
        }
    }

    //红利再投 结算金额 默认等于分红金额
    var settleAmtReInvest = function(){
        var bonusAmtReInvest = $('#bonusAmtReInvest').val();
        if(bonusAmtReInvest != null && bonusAmtReInvest != ''){
            $('#settleAmtReInvest').val(bonusAmtReInvest).trigger('blur');
        }else{
            $('#settleAmtReInvest').val('');
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/check', object, function(result) {
            Joyin.confirm(result.data, function () {
                if(operateType === 'save') {
                     WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/save', object, "TrdJihuaBonusValuationTrade002_btn_save");
                }else{
                     WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/save', object, "TrdJihuaBonusValuationTrade002_btn_submit");
                }
            });

        });
    }

    /**
     * 画面加载完成
     */
    $(function() {
        if(workflowId){
            psChange();
        }

        $('#bonusAmt').change(function(){
            settleAmt();
        });

        //红利再投 单位分红净值=分红金额/分红份额
        $('#bonusAmtReInvest').change(function(){
            unitBonusUnitValueChange();
            settleAmtReInvest();
        });
        $('#dealNumReInvest').change(function(){
            unitBonusUnitValueChange();
        });

        //保存
        $('#TrdJihuaBonusValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });

        //提交
        $('#TrdJihuaBonusValuationTrade002_btn_submit').click(function() {
            saveOrSubmit("submit");
        });

    });
}());