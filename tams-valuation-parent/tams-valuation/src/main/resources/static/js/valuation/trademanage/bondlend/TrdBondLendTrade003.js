
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/bondlend/TrdBondLendTrade003/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/bondlend/TrdBondLendTrade003/save', object, "TrdBondLendTrade003_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/bondlend/TrdBondLendTrade003/save', object, "TrdBondLendTrade003_btn_submit");
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
    }

    /**
     * 资产代码联动带出资产简称，交易场所
     */
    var secIdChange = function() {
        var secId =  $('#secId').val();
        if (secId == '' || secId == undefined) {
            $('#secAbbr').val('');
        } else {
            $('#secAbbr').val($('#secId').select2('data')[0].object).trigger('change');
        }
    }

    //结算金额  结算金额=借贷费用+交易手续费+结算服务费+其他费用
    var settleAmtChange = function(){
        var fee = $('#fee').val();
        if(fee != null && fee != ''){
            $('#settleAmt').val(fee);
        }else{
            $('#settleAmt').val('');
        }
    }


    /**
     * 画面加载完成
     */
    $(function() {

        if($("#workflowId").val()=='' && $("#settleAmt").val()==''){
            settleAmtChange();
        }

        // init();
        //产品代码联动事件
        $('#prodId').change(function(){
            prodIdChange();
        });

        //资产代码联动事件
        $('#secId').change(function(){
            secIdChange();
        });

        //结算金额联动事件
        //借贷费用
        $('#fee').change(function(){
            settleAmtChange();
        });

        // 绑定按钮事件
        $('#TrdBondLendTrade003_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdBondLendTrade003_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
