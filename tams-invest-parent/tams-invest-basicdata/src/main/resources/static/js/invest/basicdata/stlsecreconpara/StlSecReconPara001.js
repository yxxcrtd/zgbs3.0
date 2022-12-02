(function (){
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        // 序列化form表单参数
        var object = $.serializeToObject('#searchForm',true);
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

        return object;
    };

    /**
     * 新建按钮
     */
    function showDialog() {
        Joyin.dialog(JoyinUtil.getLabel('StlSecReconPara002'), '../StlSecReconPara002/init', '98%', '98%')
    }

    /**
     * 定义数据表格
     */
    var table = $("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        dblclick:function(table, data) {
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/basicdata/stlsecreconpara/StlSecReconPara002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,
                    seqNo:data.seqNo});
        },
        cols:[
            {field:'seqNo',  visible:false}
            ,{field:'customerId', width:350,  title: 'StlSecReconPara001.customerId'}
            ,{field:'paraCode',  visible:false}
            ,{field:'paraCodeStr',width:350,  title: 'StlSecReconPara001.paraCode', sortField:"paraCode", sort: true}
            ,{field:'paraValue', visible:false}
            ,{field:'paraValueStr', width:350,  title: 'StlSecReconPara001.paraValue', sortField:"paraValue", sort: true}
           // ,{field:'configDate', width:200,  title: 'StlSecReconPara001.configDate', type:'date', sortField:"configDate", sort: true}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    });

    /**
     * 导出Excel
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();
        Joyin.exportExcel('export', paramObj);
    };

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.ajaxJson(basePath + 'invest/basicdata/stlsecreconpara/StlSecReconPara001/check', data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("StlSecReconPara002.edit"),
                basePath + "invest/basicdata/stlsecreconpara/StlSecReconPara002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_EDIT,
                    seqNo:data.seqNo},function() {
                    table.reload();
                });
        });
    });
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'invest/basicdata/stlsecreconpara/StlSecReconPara001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(basePath + 'invest/basicdata/stlsecreconpara/StlSecReconPara001/delete', object, "StlSecReconPara001_btn_delete", "StlSecReconPara002", function (){
                    table.reload();
                });
            });
        });
    });

    /**
     * 画面加载完成
     */
    $(function() {

        $('#StlSecReconPara001_btn_export').click(function() {
            exportExcel();
        });
        // 绑定按钮事件
        $('#StlSecReconPara001_btn_search').click(function() {
            // 加载表格
            table.load();
        });

        $('#StlSecReconPara001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'invest/basicdata/stlsecreconpara/StlSecReconPara001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(basePath + 'invest/basicdata/stlsecreconpara/StlSecReconPara001/delete', object, "StlSecReconPara001_btn_delete", "StlSecReconPara002", function () {
                        table.reload();
                    });
                });
            });
        });

        /**
         * 新建按钮
         */
        $('#StlSecReconPara001_btn_add').click(showDialog);

        /**
         * 参数名称下拉框联动
         */
        $('#paraCode').change(function() {
            $('#DIV_paraValue01').hide();
            $('#DIV_paraValue02').hide();
            $('#DIV_paraValue03').hide();
            if ($('#paraCode').val() === RECON_PARA_CODE_02) {
                $('#DIV_paraValue02').show();
            } else if ($('#paraCode').val() === RECON_PARA_CODE_03) {
                $('#DIV_paraValue03').show();
            } else {
                $('#DIV_paraValue01').show();
            }
            $('#paraValue1').val('').trigger('change');
            $('#paraValue2').val('').trigger('change');
            $('#paraValue3').val('').trigger('change');
        });
    });
}())