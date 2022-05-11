$(function() {
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

    var finprodMarketIdFlag = true;
    var type1Flag = true;
    var finprodMarketIdChange = function () {
        finprodMarketIdFlag = false;
        var finprodMarketId = $('#finprodMarketId').val();
        $('#finprodId').val("").trigger('change');
        if(type1Flag){
            if(finprodMarketId){
                var data = $('#finprodMarketId').select2('data')[0].object;
                if (data) {
                    $('#finprodId').val(data.finprodId).trigger('change');
                    $('#assetCode').val(finprodMarketId);
                    // 20210812 fuyongsheng删除 start
                    // $('#type1').val(data.type9).prop("disabled",true).trigger('change');
                    // $('#type2').val(data.type10).prop("disabled",true).trigger('change');
                    // $('#type3').val(data.type11).prop("disabled",true).trigger('change');
                    // $('#type4').val(data.type12).prop("disabled",true).trigger('change');
                }
            }
            // else{
            //     $('#type1').prop("disabled",false);
            //     $('#type2').prop("disabled",false);
            //     $('#type3').prop("disabled",false);
            //     $('#type4').prop("disabled",false);
            // }
            // 20210812 fuyongsheng删除 start
        }
        finprodMarketIdFlag = true;
    }

    // 20210812 fuyongsheng删除 start
    // var type1Change = function () {
    //     type1Flag = false;
    //     if(finprodMarketIdFlag){
    //         var type1 = $('#type1').val();
    //         if(type1){
    //             $('#finprodMarketId').val("").prop("disabled",true).trigger('change');
    //         }
    //         else{
    //             $('#finprodMarketId').prop("disabled",false);
    //         }
    //     }
    //     type1Flag = true;
    // }
    // 20210812 fuyongsheng删除 end

    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#submitForm', true);
        object.operateType = operateType;
        object.pageModel = pageModel;
        Joyin.ajaxJson(basePath + 'invest/risk/quota/rskspecificassettype/RskSpecificAssetType002/check', object, function(
            result) {
            if (operateType === 'save') {
                WorkFlow.doSaveList(basePath + 'invest/risk/quota/rskspecificassettype/RskSpecificAssetType002/save',
                    object, "RskSpecificAssetType002_btn_save");
            } else {
                WorkFlow.doSubmitList(basePath + 'invest/risk/quota/rskspecificassettype/RskSpecificAssetType002/save',
                    object, "RskSpecificAssetType002_btn_submit");
            }
        })
    }

    $(function() {
        // 绑定按钮事件
        $('#RskSpecificAssetType002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#RskSpecificAssetType002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        $('#finprodMarketId').change(function () {
            finprodMarketIdChange();
        });

        // 20210812 fuyongsheng删除 start
        // $('#type1').change(function () {
        //     type1Change();
        // });
        if(pageModel !=PAGE_MODEL_NEW){
            $('#finprodMarketId').prop('disabled',true);
            if($('#finprodMarketId').val()){
                $('#finprodMarketId').trigger('change');
            }
            // else{
            //     $('#type1').trigger('change');
            // }
            // 20210812 fuyongsheng删除 end
        }
    });
});
