
/**
 * 债券借贷交易新建
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/bondlend/TrdBondLendTrade002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/bondlend/TrdBondLendTrade002/save', object, "TrdBondLendTrade002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/bondlend/TrdBondLendTrade002/save', object, "TrdBondLendTrade002_btn_submit");
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
            $('#prodName').val($('#prodId').select2('data')[0].id).trigger('change');
        }
    }

    /**
     * 资产代码联动带出资产简称，交易场所
     */
    var secIdChange = function() {
        var secId =  $('#secId').val();
        if (secId == '' || secId == undefined) {
            $('#secAbbr').val('');
        } else {
            $('#secAbbr').val($('#secId').select2('data')[0].id).trigger('change');
        }
    }

    //借贷费用=券面总额（万元）×10000×借贷费率%×（到期日-起息日）/365
    var feeChange = function () {
        var vdate = $('#vdate').val();
        var mdate = $('#mdate').val();
        var prinAmt = $('#prinAmt').val();
        var feeRate = $('#feeRate').val();
        if(vdate != null && vdate != ''
            && mdate != null && mdate != ''
            && prinAmt != null && prinAmt != ''
            && feeRate != null && feeRate != ''){
            var days = (Date.ofDate(mdate) - Date.ofDate(vdate))/ (1000 * 60 * 60 * 24);
            var fee = JoyinMath.doMul(prinAmt,10000,2,JoyinMath.ROUND_HALF_UP);
            var fee1 = JoyinMath.doDiv(feeRate,100,2,JoyinMath.ROUND_HALF_UP);
            var fee2 = JoyinMath.doMul(fee,fee1,2,JoyinMath.ROUND_HALF_UP);
            var fee3 = JoyinMath.doDiv(JoyinMath.doMul(fee2,days,2,JoyinMath.ROUND_HALF_UP),365,2,JoyinMath.ROUND_HALF_UP);
            $('#fee').val(fee3).trigger('change');
        }else{
            $('#fee').val('').trigger('change');
        }
    }

    var calMdate = function(){
        var bondLendTerm = $('#term').val();
        if(bondLendTerm ){
            var vdate = $('#vdate').val();//首次结算日
            if(vdate){
                var d1 = new Date(vdate).getTime();
                var day = parseInt(bondLendTerm*24*60*60*1000);
                var mdate = new Date(d1+day);
                mdate=mdate.format("yyyy-MM-dd");
                $("#mdate").val(mdate).trigger("change");
                }else{
                    $("#mdate").val('').trigger('change');
                }
        }else{
            $("#mdate").val('').trigger('change');
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

        $('#term').change(function(){
            feeChange();
            calMdate();
        });


        //借贷费用联动事件
        //起息日
        $('#vdate').change(function(){
            feeChange();
        });
        //到期日
        $('#mdate').change(function(){
            feeChange();
        });
        //券面总额
        $('#prinAmt').change(function(){
            feeChange();
        });
        //借贷费率
        $('#feeRate').change(function(){
            feeChange();
        });

        // 绑定按钮事件
        $('#TrdBondLendTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdBondLendTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
