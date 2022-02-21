
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade002/save', object, "TrdBondChangeTrade002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade002/save', object, "TrdBondChangeTrade002_btn_submit");
                        }
                    });
            })
    }

    /**
     * 产品代码联动带出产品名称
     */
    var prodIdChange = function() {
        var prodId = $('#prodId').val();
        if (prodId == '' || prodId == undefined) {
            $('#prodName').val('');
        } else {
            $('#prodName').val($('#prodId').select2('data')[0].object).trigger('change');
        }
        // Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade002/selectInfoByProdId',
        //     {prodId: prodId}, function (data) {
        //         if (data) {
        //             $('#secManageAcctId').val(data.secManageAcctId).trigger("change");
        //         }
        //     })
    }

    /**
     * 转出证券代码联动带出转出证券名称
     */
    var secIdChange = function() {
        var oAssetId =  $('#oAssetId').val();
        var oAssetIdSec =  $('#oAssetIdSec').val();
        var oAssetIdFund =  $('#oAssetIdFund').val();
        if (oAssetId != '' || oAssetId != undefined
            && (oAssetIdSec == '' || oAssetIdSec == undefined)
            &&(oAssetIdFund == '' || oAssetIdFund == undefined)) {

            $('#oAssetAbbr').val($('#oAssetId').select2('data')[0].object).trigger('change');
        } else if(oAssetIdSec != '' || oAssetIdSec != undefined
            && (oAssetId == '' || oAssetId == undefined)
            &&(oAssetIdFund == '' || oAssetIdFund == undefined)){

            $('#oAssetAbbr').val($('#oAssetIdSec').select2('data')[0].object).trigger('change');
        } else if(oAssetIdFund != '' || oAssetIdFund != undefined
            && (oAssetIdSec == '' || oAssetIdSec == undefined)
            &&(oAssetId == '' || oAssetId == undefined)){

            $('#oAssetAbbr').val($('#oAssetIdFund').select2('data')[0].object).trigger('change');
        }
        else {
            $('#oAssetAbbr').val('');
        }
        // Joyin.ajax(basePath + 'valuation/trademanage/bond/TrdBondTrade002/selectInfoBySecId',
        //     {secId:secId}, function(data){
        //         if(data) {
        //             $('#secId').textSelect($('#secId').select2('data')[0].id);
        //             $('#secAbbr').val(data.finprodAbbr).trigger("change");
        //         }else{
        //             $('#secId').text($('#secId').select2('data')[0].id);
        //             $('#secAbbr').val("").trigger("change");
        //         }
        //     });
    }

    /**
     * 转出证券代码联动带出转出证券名称
     */
    var cAssetIdChange = function() {
        var cAssetId =  $('#cAssetId').val();
        var cAssetIdSec =  $('#cAssetIdSec').val();
        var cAssetIdFund =  $('#cAssetIdFund').val();
        if (cAssetId != '' || cAssetId != undefined
            && (cAssetIdSec == '' || cAssetIdSec == undefined)
            &&(cAssetIdFund == '' || cAssetIdFund == undefined)) {

            $('#cAssetAbbr').val($('#cAssetId').select2('data')[0].object).trigger('change');
        } else if(cAssetIdSec != '' || cAssetIdSec != undefined
            && (cAssetId == '' || cAssetId == undefined)
            &&(cAssetIdFund == '' || cAssetIdFund == undefined)){

            $('#cAssetAbbr').val($('#cAssetIdFund').select2('data')[0].object).trigger('change');
        } else if(cAssetIdFund != '' || cAssetIdFund != undefined
            && (cAssetIdSec == '' || cAssetIdSec == undefined)
            &&(cAssetId == '' || cAssetId == undefined)){

            $('#cAssetAbbr').val($('#cAssetIdFund').select2('data')[0].object).trigger('change');
        }
        else {
            $('#cAssetAbbr').val('');
        }
    }

    //交易方向联动转入、转出证券代码
    $('#ps').change(function(){
        if($('#ps').val() && $('#ps').val() != null && $('#ps').val() != ''){
            var ps= $('#ps').val();
            if(ps == 'T49'){//债转股
                $('#oAssetIdDiv1').hide();
                $('#oAssetIdDiv2').show();
                $('#oAssetIdDiv3').hide();
                $('#cAssetIdDiv1').hide();
                $('#cAssetIdDiv2').show();
                $('#cAssetIdDiv3').hide();
            }else if(ps == 'T41'){//上市流通
                // $('#oAssetIdDiv1').hide();
                // $('#oAssetIdDiv2').show();
                // $('#oAssetIdDiv3').hide();
                // $('#cAssetIdDiv1').hide();
                // $('#cAssetIdDiv2').show();
                // $('#cAssetIdDiv3').hide();
            }else if(ps == 'T64' || ps == 'T63'){//基金转换
                $('#oAssetIdDiv1').hide();
                $('#oAssetIdDiv2').hide();
                $('#oAssetIdDiv3').show();
                $('#cAssetIdDiv1').hide();
                $('#cAssetIdDiv2').hide();
                $('#cAssetIdDiv3').show();
            }else{
                $('#oAssetIdDiv1').show();
                $('#oAssetIdDiv2').hide();
                $('#oAssetIdDiv3').hide();
                $('#cAssetIdDiv1').show();
                $('#cAssetIdDiv2').hide();
                $('#cAssetIdDiv3').hide();
            }

        }

    });

    //结算金额  结算金额=其他费用+交易费用
    var settleAmtChange = function(){
        var tradeAmt = $('#tradeAmt').val();
        var otherAmt = $('#otherAmt').val();
        if(tradeAmt != null && tradeAmt != ''
            && otherAmt != null && otherAmt != ''){
            var settleAmt = JoyinMath.doAdd(tradeAmt, otherAmt,2);
            $('#settleAmt').val(settleAmt);
        }else{
            $('#settleAmt').val('');
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

        //转出证券代码联动事件
        $('#oAssetId').change(function(){
            secIdChange();
        });
        $('#oAssetIdSec').change(function(){
            secIdChange();
        });
        $('#oAssetIdFund').change(function(){
            secIdChange();
        });

        //转入证券代码联动事件
        $('#cAssetId').change(function(){
            cAssetIdChange();
        });
        $('#cAssetIdSec').change(function(){
            cAssetIdChange();
        });
        $('#cAssetIdFund').change(function(){
            cAssetIdChange();
        });

        //结算金额联动事件
        //经手费
        $('#tradeAmt').change(function(){
            settleAmtChange();
        });
        //其他费用
        $('#otherAmt').change(function(){
            settleAmtChange();
        });

        // 绑定按钮事件
        $('#TrdBondChangeTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdBondChangeTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
