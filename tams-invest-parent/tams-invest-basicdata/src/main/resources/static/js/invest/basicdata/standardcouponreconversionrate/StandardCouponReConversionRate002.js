$(function() {
    /**
     * 保存或提交
     *
     * @param operateType
     *            操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#submitForm');

        object.operateType = operateType;
        object.pageModel = pageModel;
        object.seqNo = seqNo;
        if (pageModel == PAGE_MODEL_NEW) {
            object.operateType = 'new';
        } else if (pageModel == PAGE_MODEL_EDIT) {
            object.operateType = 'update';
        }
        Joyin.ajaxJson(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate002/check',
            object, function(result) {
                Joyin.confirm(result.data, function() {
                    if (operateType === 'save') {
                        WorkFlow.doSaveList(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate002/save',
                            object, "StandardCouponReConversionRate002_btn_save");
                    } else {
                        WorkFlow.doSubmitList(basePath  + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate002/save',
                            object, "StandardCouponReConversionRate002_btn_submit");
                    }
                });
            });
    }
    var init = function() {
        let finprodIdType = false
        let accountTypes = false
        $('#brokerId').change(function(){
            var borker = $("#brokerId").val();
            if(borker == "BROKER"){
                $('#accountType').textSelect('setValue', 'BROKER');
                $('#accountType').prop('disabled',true);
                console.log("2222")
            }else if(borker != ""){
                Joyin.ajaxJson(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate002/brokerIdChange?borker='+$("#brokerId").val(), null, function(result) {
                    if (borker != null || borker != "") {
                        if (result != null && result != '' && result.length > 0) {
                            var itemSH = [];
                            for (var m = 0; m < result.length; m++) {
                                itemSH.push(result[m].id);
                            }
                            $('#accountType').updateChosenArr({
                                item: itemSH,
                                disabled: false
                            });
                        } else {
                            $('#accountType').updateChosenArr({
                                item: [""],
                                disabled: false
                            });
                        }
                        setDisabled();
                    }
                });
            }

        });
        $("#finprodId").on("select2:select select2:unselecting", function(e) {
            this.finprodIdType = true
        });
        $("#accountType").on("select2:select select2:unselecting", function(e) {
            this.accountTypes = true
        });
        $("#finprodId").change(function(){
            if(this.finprodIdType){
                $("#accountType").attr("disabled", false);
            } else {
                $("#accountType").attr("disabled", "disabled");
            }
            this.finprodIdType = false
        })
        $("#accountType").change(function(){
            if(this.accountTypes){
                $("#finprodId").attr("disabled", false);
            } else {
                $("#finprodId").attr("disabled", "disabled");
            }
            this.accountTypes = false
        })
        if (pageModel == PAGE_MODEL_EDIT) {
            $("#brokerId").attr("disabled", "disabled");
            $("#finprodId").attr("disabled", "disabled");
            $("#accountType").attr("disabled", "disabled");
        }
    }

    function setDisabled (){
        $("#finprodId").val(null).trigger('change');
        $("#accountType").val(null).trigger('change');
        $("#finprodId").attr("disabled", false);
        $("#accountType").attr("disabled", false);
    }

    $.fn.updateChosenArr = function updateChosenArr(obj){
        var $this = $(this),arr = obj.dataResult || JSON.parse($this.attr('data-result') || '[]'),item = obj.item;
        $this.empty();
        $this.append($('<option value>'));
        for(var i in arr){
            if (!arr.hasOwnProperty(i))continue;
            if (obj.item && obj.item.length > 0){
                if ($.inArray(arr[i].id,item) >= 0)
                    $('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this);
            } else
                $('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this)
        }
        if (obj.chosen)
            $this.val(obj.chosen);
        if (obj.disabled === true || obj.disabled === false)
            $this.attr('disabled', obj.disabled);
        $this.trigger("chosen:updated").trigger('change');
        if (obj.callback)
            obj.callback($this);
        return $this;
    }


    /**
     *
     */
    if(pageModel != '2'){

    }

    /**
     * 画面加载完成
     */
    $(function() {
        init();
        // 绑定按钮事件
        $('#StandardCouponReConversionRate002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        // 绑定按钮事件
        $('#StandardCouponReConversionRate002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
    });
}());