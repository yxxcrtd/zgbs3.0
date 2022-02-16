
/**
 * 债券交易新建
 */
(function() {
    /**
     * 画星星
     */
    var star = function() {
        // 加红星星
        // 查找label
        var _findPrevLabel = function(element, level) {
            var $el = element.prev();
            if (!$el.is('label') && level > 0) {
                $el = $el.find('label');
                if ($el.length === 0) {
                    return _findPrevLabel(element.parent(), level - 1);
                }
            }
            return $el;
        };

        // 非查看模式
        if (typeof pageModel !== 'undefined'
            && (pageModel === PAGE_MODEL_EDIT || pageModel === PAGE_MODEL_NEW)) {
            $('.must').each(function() {
                $(this).remove();
            })
            $('*[required]').each(function() {
                var $el = $(this);
                var $target = _findPrevLabel($el, 3);
                if ($target.find('> .must').length === 0) {
                    $target.append($('<i class="must">*</i>'));
                }
            });
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
        var object = $.serializeToObject('#submitForm', false);
        object.pageModel = pageModel;
        object.operateType = operateType;
        object.pageId = pageId;

        Joyin.ajaxJson(basePath + 'valuation/trademanage/bond/TrdBondTrade002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/bond/TrdBondTrade002/save', object, "TrdBondTrade002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/bond/TrdBondTrade002/save', object, "TrdBondTrade002_btn_submit");
                        }
                    });
            })
    }

    /**
     * 产品代码联动带出产品名称，证券账户
     */
    var prodIdChange = function() {
        var prodId = $('#prodId').val();
        if (prodId == '' || prodId == undefined) {
            $('#prodName').val('');
        } else {
            $('#prodName').val($('#prodId').select2('data')[0].object).trigger('change');
        }
        Joyin.ajaxJson(basePath + 'valuation/trademanage/bond/TrdBondTrade002/selectInfoByProdId',
            {prodId: prodId}, function (data) {
                if (data) {
                    $('#secManageAcctId').val(data.secManageAcctId).trigger("change");
                }
            })
    }

    /**
     * 资产代码联动带出资产简称，交易场所
     */
    var secIdChange = function() {
        var secId =  $('#secId').val();
        Joyin.ajax(basePath + 'valuation/trademanage/bond/TrdBondTrade002/selectInfoBySecId',
            {secId:secId}, function(data){
                if(data) {
                    $('#secId').textSelect($('#secId').select2('data')[0].id);
                    $('#secAbbr').val(data.finprodAbbr).trigger("change");
                    $('#tradeMarket').textSelect('setValue',data.tradeMarket);
                }else{
                    $('#secId').text($('#secId').select2('data')[0].id);
                    $('#secAbbr').val("").trigger("change");
                    $('#tradeMarket').textSelect('setValue',"");
                }
            });
    }

    /*净价 = 净价总额/数量计算 */
    $('#dealNum').change(function () {
        var cpriceAmt = $('#cpriceAmt').val();
        var dealNum = $("#dealNum").val();
        if (cpriceAmt && dealNum ) {
            var cprice = JoyinMath.doDiv(cpriceAmt, dealNum, 4, JoyinMath.ROUND_HALF_UP);
            $("#cprice").val(cprice).trigger("blur");
        }
    });

    //申购时必填
    $('#ps').change(function () {
        var ps = $('#ps').val();
        if (ps == "T04"){
            $('#bondAmt').attr("required", true).trigger('change');
        }else{
            $('#bondAmt').attr("required", false).trigger('change');
        }
    });

    //结算金额  结算金额=全价总额+经手费+结算费+佣金+交易手续费+结算服务费+其他费用+保证金
    var settleAmtChange = function(){
        var fpriceAmt = $('#fpriceAmt').val();
        var brokerage = $('#brokerage').val();
        var stlAmt = $('#stlAmt').val();
        var commission = $('#commission').val();
        var dealFeeAmt = $('#dealFeeAmt').val();
        var stlFeeAmt = $('#stlFeeAmt').val();
        var otherAmt = $('#otherAmt').val();
        if(fpriceAmt != null && fpriceAmt != ''
            && brokerage != null && brokerage != ''
            && stlAmt != null && stlAmt != ''
            && commission != null && commission != ''
            && dealFeeAmt != null && dealFeeAmt != ''
            && stlFeeAmt != null && stlFeeAmt != ''
            && otherAmt != null && otherAmt != ''){
            var settleAmt1 = JoyinMath.doAdd(JoyinMath.doAdd(fpriceAmt, brokerage,2),stlAmt,2);
            var settleAmt2 = JoyinMath.doAdd(JoyinMath.doAdd(settleAmt1, commission,2),dealFeeAmt,2);
            var settleAmt3 = JoyinMath.doAdd(JoyinMath.doAdd(settleAmt2, stlFeeAmt,2),otherAmt,2);
            $('#settleAmt').val(settleAmt3);
            $('#bTradeAmt').val(settleAmt3);
        }else{
            $('#settleAmt').val('');
            $('#bTradeAmt').val('');
        }
    }


    /**
     * 画面加载完成
     */
    $(function() {

        //当前是否包含流程
        var workflowIdNow = $("#workflowId").val()

        // init();
        //产品代码联动事件
        $('#prodId').change(function(){
            prodIdChange();
        });

        //资产代码联动事件
        $('#secId').change(function(){
            secIdChange();
        });

        /*全价总额变动计算净价总额*/
        $('#fpriceAmt').change(function () {
            var fpriceAmt = $('#fpriceAmt').val();
            var intAmt = $("#intAmt").val();
            if (!intAmt) {
                $("#intAmt").val('0.00')
                intAmt = 0;
            }

            var cpriceAmt = JoyinMath.doMinus(fpriceAmt, intAmt, 4, JoyinMath.ROUND_HALF_UP);
            // if (cpriceAmt < 0) {
            //     $("#cpriceAmt").val(tradeAmt).trigger("blur");
            //     $("#intAmt").val(0).trigger("blur");
            // } else {
                $("#cpriceAmt").val(cpriceAmt).trigger("blur");
            //}
            var dealNum = $("#dealNum").val();
            if(cpriceAmt && dealNum ){
                var cprice = JoyinMath.doDiv(cpriceAmt, dealNum, 4, JoyinMath.ROUND_HALF_UP);
                $("#cprice").val(cprice).trigger("blur");
            }
            //结算金额
            settleAmtChange();
        });

        /*应收利息总额变动计算净价总额*/
        $('#intAmt').change(function () {
            var fpriceAmt = $('#fpriceAmt').val();
            var intAmt = $("#intAmt").val();
            if (!fpriceAmt) {
                $("#fpriceAmt").val('0.00')
                fpriceAmt = 0;
            }

            var cpriceAmt = JoyinMath.doMinus(fpriceAmt, intAmt, 4, JoyinMath.ROUND_HALF_UP);
            $("#cpriceAmt").val(cpriceAmt).trigger("blur");

            var dealNum = $("#dealNum").val();
            if(cpriceAmt && dealNum ){
                var cprice = JoyinMath.doDiv(cpriceAmt, dealNum, 4, JoyinMath.ROUND_HALF_UP);
                $("#cprice").val(cprice).trigger("blur");
            }
        });

        //交易类型联动
        $('#ps').change(function () {
            var ps = $('#ps').val();
            if(ps == 'T04'){
                $('#bondAmt').attr("required",true).trigger('change');
            }else{
                $('#bondAmt').attr("required",false).trigger('change');
            }
            star();
        });

        //结算金额联动事件
        //经手费
        $('#brokerage').change(function(){
            settleAmtChange();
        });
        //结算费
        $('#stlAmt').change(function(){
            settleAmtChange();
        });
        //佣金
        $('#commission').change(function(){
            settleAmtChange();
        });
        //交易手续费
        $('#dealFeeAmt').change(function(){
            settleAmtChange();
        });
        //结算手续费
        $('#stlFeeAmt').change(function(){
            settleAmtChange();
        });
        //其他费用
        $('#otherAmt').change(function(){
            settleAmtChange();
        });

        // 绑定按钮事件
        $('#TrdBondTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdBondTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
