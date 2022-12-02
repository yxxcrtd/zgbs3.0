(function () {
    // 提交或保存
    function saveOrSubmit(operateType) {
        // 画面数据体
        var object = $.serializeToObject('#submitForm');

        // 券商代码
        object.customerId = $('#customerId').val()
        // 参数名称
        object.paraCode = $('#paraCode').val()
        if (object.paraCode === RECON_PARA_CODE_02) {
            // 对账维度
            object.paraValue = $('#paraValue2').val()
        } else if (object.paraCode === RECON_PARA_CODE_03) {
            // 佣金对账方式
            object.paraValue = $('#paraValue3').val()
        } else {
            // 误差值
            object.paraValue = $('#paraValue1').val()
        }
        // 配置日期
        object.configDate = $('#configDate').val()
        // 流程ID
        object.workflowId = $('#workflowId').val()

        // seqNo
        object.seqNo = $('#seqNo').val()

        // 操作类型
        object.operateType = operateType;
        object.pageModel = pageModel;
        if (pageModel === PAGE_MODEL_NEW) {
            object.operateType = 'new';
        } else if (pageModel === PAGE_MODEL_EDIT) {
            object.operateType = 'update';
        }

        Joyin.ajaxJson(basePath + 'invest/basicdata/stlsecreconpara/StlSecReconPara002/check',
            object, function(result) {
            Joyin.confirm(result.data, function() {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/basicdata/stlsecreconpara/StlSecReconPara002/save',
                        object, "StlSecReconPara002_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath  + 'invest/basicdata/stlsecreconpara/StlSecReconPara002/save',
                        object, "StlSecReconPara002_btn_submit");
                }
            });
        });
    }

    /**
     * 隐藏参数名称DIV
     */
    function hideParaValueDiv() {
        $('#DIV_paraValue01').hide();
        $('#DIV_paraValue02').hide();
        $('#DIV_paraValue03').hide();
    }

    // 画面初始化
    function init() {
        hideParaValueDiv();
        if ($('#paraCode').val() === RECON_PARA_CODE_02) {
            $('#DIV_paraValue02').show()
        } else if ($('#paraCode').val() === RECON_PARA_CODE_03) {
            $('#DIV_paraValue03').show()
        } else {
            $('#DIV_paraValue01').show()
        }
        // 编辑画面禁止修改参数名称和券商ID
        if (pageModel === PAGE_MODEL_EDIT) {
            $('#customerId').attr('disabled',true).trigger('change');
            $('#paraCode').attr('disabled',true).trigger('change');
        }
    }
    /**
     * 画面加载完成
     */
    $(function() {
        init();
        /**
         * 参数名称下拉框联动
         */
        $('#paraCode').change(function() {
            hideParaValueDiv();

            if ($('#paraCode').val() === RECON_PARA_CODE_02) {
                // 对账维度
                $('#DIV_paraValue02').show()
            } else if ($('#paraCode').val() === RECON_PARA_CODE_03) {
                // 佣金对账方式
                $('#DIV_paraValue03').show()
            } else {
                // 误差值
                $('#DIV_paraValue01').show()
            }
            
            $('#paraValue1').val('').trigger('change');
            $('#paraValue2').val('').trigger('change');
            $('#paraValue3').val('').trigger('change');
        });
    });

    // 绑定按钮事件
    $('#StlSecReconPara002_btn_save').click(function() {
        saveOrSubmit('save');
    });

    // 绑定按钮事件
    $('#StlSecReconPara002_btn_submit').click(function() {
        saveOrSubmit('submit');
    });
}())
