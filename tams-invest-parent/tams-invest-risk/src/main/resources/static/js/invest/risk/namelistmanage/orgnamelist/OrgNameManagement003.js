(function () {
    /**
     * 定义历史名单列表（Detail）
     */
    var assetNameHistoryDetailTBL = $("#orgNameHistoryDetailList").joyintable({
        ajax: {
            url:JoyinUtil.getRealPath('/invest/risk/namelistmanage/orgnamelist/OrgNameManagement003/findPage'),
            type: 'POST',
            data: function () {
                var param = {};
                param.listId = $("#listId").val();
                param.listDetailId = $("#listDetailId").val();
                param.listDetailName = $("#listDetailName").val();
                param.rskAccessListLogDtoList =JSON.stringify(assetNameNewDetailTbl.getAllRows())
                return param;
            }
        },
        searchOnInit: true,
        checkbox:false,
        limit:10,
        height:200,
        cols: [
            {field:'oldlistDetailName',  title: 'OrgNameManagement003.listDetailName',visible:false} //金融产品代码
            // 资产名称
            ,{
                field: 'listDetailName',
                width: 525,
                title: 'OrgNameManagement003.listDetailName',
                sortable: false
            }
            // 资产代码
            , {
                field: 'listDetailId',
                width: 525,
                edit : true,
                title: 'OrgNameManagement003.listDetailId',
                sortable: false
            }
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: [ 'delt']}
        ],
        beforeEditCell: function (preVal, field, rowData) {
            /*if (field === "listDetailId"&&field === "listDetailName") {
                if(rowData.operatePs!='02'){
                    return false;
                }
            }
            return true;*/
        },
        editCell: function(value, data, field, cell, column){
            var assetNameNewAllRows = assetNameNewDetailTbl.getAllRows();
            // 倒序删除，正序会存在问题
            var isConfirm = false;
            var confirmMessageArray = new Array();
            for (var j in assetNameNewAllRows) {
                if (assetNameNewAllRows[j].listDetailName == data.listDetailName) {
                    isConfirm = true;
                    confirmMessageArray.push(data.listDetailName);
                    break;
                }
            }
            if (isConfirm) {
                Joyin.confirm(JoyinUtil.getMessage("RSK_C_0001", confirmMessageArray.join("，")), function () {
                    // 倒序删除，正序会存在问题
                    for (var j in assetNameNewAllRows) {
                        // 覆盖履历列表重复记录
                        if (assetNameNewAllRows[j].listDetailName == data.listDetailName) {
                            assetNameNewDetailTbl.removeRow(assetNameNewAllRows[j]);
                        }
                    }
                    data.operatePs = RSK_OPERATE_PS_03;
                    data.operateType = $('#operateStyle').val();
                    data.listDetailType = $('#listDetailType').val();
                    assetNameNewDetailTbl.addRow(data);
                });
            } else {
                data.operatePs = RSK_OPERATE_PS_03;
                data.operateType = $('#operateStyle').val();
                data.listDetailType = $('#listDetailType').val();
                assetNameNewDetailTbl.addRow(data);
            }
        },
        done:function () {
        }
    });

    var cols = [];
    if(pageModel === PAGE_MODEL_REFERENCE&&workflowId){
        cols = [
            {field:'oldlistDetailName',  title: 'OrgNameManagement003.listDetailName',visible:false} //金融产品代码
            // 资产代码
            ,{
                field: 'listDetailName',
                width: 350,
                edit : true,
                title: 'OrgNameManagement003.listDetailName',
                sortable: false
            }
            // 资产简称
            , {
                field: 'listDetailId',
                width: 350,
                edit : true,
                title: 'OrgNameManagement003.listDetailId',
                sortable: false
            }
            , {
                field: 'operatePs',
                width: 300,
                title: 'OrgNameManagement003.operatePs',
                sortable: false,
                render:function (data, type, row, meta) {
                    if (data == RSK_OPERATE_PS_01||data==RSK_OPERATE_PS_NAME_01)
                    {return RSK_OPERATE_PS_NAME_01;}
                    else if (data == RSK_OPERATE_PS_02||data==RSK_OPERATE_PS_NAME_02){
                        return RSK_OPERATE_PS_NAME_02;
                    }else if (data == RSK_OPERATE_PS_03||data==RSK_OPERATE_PS_NAME_03){
                        return RSK_OPERATE_PS_NAME_03;
                    }else{ return RSK_OPERATE_PS_NAME_99};
                }
            }
        ]
    }else{
        cols =  [
            {field:'oldlistDetailName',  title: 'OrgNameManagement003.listDetailName',visible:false} //金融产品代码
            // 资产代码
            ,{
            field: 'listDetailName',
            width: 300,
            edit : true,
            title: 'OrgNameManagement003.listDetailName',
            sortable: false
        }
            // 资产简称
            , {
            field: 'listDetailId',
            width: 300,
            edit : true,
            title: 'OrgNameManagement003.listDetailId',
            sortable: false
        }
            // 操作方式
            , {
                field: 'operateType',
                width: 300,
                edit : true,
                title: 'OrgNameManagement003.operateType',
                visible:false
            }
            //名单类型
            , {
                field: 'listDetailType',
                width: 300,
                edit : true,
                title: 'OrgNameManagement003.listDetailType',
                visible:false
            }
            , {
            field: 'operatePs',
            width: 300,
            title: 'OrgNameManagement003.operatePs',
            sortable: false,
            render:function (data, type, row, meta) {
                if (data == RSK_OPERATE_PS_01||data==RSK_OPERATE_PS_NAME_01)
                {return RSK_OPERATE_PS_NAME_01;}
                else if (data == RSK_OPERATE_PS_02||data==RSK_OPERATE_PS_NAME_02){
                    return RSK_OPERATE_PS_NAME_02;
                }else if (data == RSK_OPERATE_PS_03||data==RSK_OPERATE_PS_NAME_03){
                    return RSK_OPERATE_PS_NAME_03;
                }else{ return RSK_OPERATE_PS_NAME_99};
            }
        }
            ,{field:'operation', width:100 , title: 'SysCommon.operation',
            render:function (data, type, row, meta) {
                return '<a class="btn layui-btn del" title="撤销" style="color:white">撤销</a>';
            }
        }
        ]
    }
    /**
     * 名单明细变更列表（Detail）
     */
    var assetNameNewDetailTbl = $("#orgNameNewDetailList").joyintable({
        data: (entity && entity.rskAccessListDetailList) ? entity.rskAccessListDetailList : [],
        page: false,
        loadonce: true,
        addbtn:false,
        checkbox:false,
        searchOnInit: true,
        cols: cols,
        beforeEditCell: function (preVal, field, rowData) {
            /*if (field === "listDetailId"&&field === "listDetailName") {
                if(rowData.operatePs!='02'){
                    return false;
                }
            }
            return true;*/
        },
        editCell: function (value, data, field, cell, column) {

        },
        addclick: function(row) {
            row.operatePs = RSK_OPERATE_PS_02;
        },
        done:function () {
            var addCount=0;var updateCount=0;var delCount=0;var newSumCount = sumCount;
            var assetNameNewAllRows = assetNameNewDetailTbl.getAllRows();
            if (assetNameNewAllRows && assetNameNewAllRows.length != 0) {
                for (var j in assetNameNewAllRows) {
                    if (assetNameNewAllRows[j].operatePs ===RSK_OPERATE_PS_01) {
                        delCount++;
                        newSumCount--;
                    }
                    if (assetNameNewAllRows[j].operatePs === RSK_OPERATE_PS_02) {
                        addCount++;
                        newSumCount++;
                    }
                    if (assetNameNewAllRows[j].operatePs === RSK_OPERATE_PS_03) {
                        updateCount++;
                    }
                    $('#addCount').css('color','red');$('#updateCount').css('color','red');$('#delCount').css('color','red')
                    $('#sumCount').css('color','red');
                }
            }
            $('#addCount').html(addCount);
            $('#updateCount').html(updateCount);
            $('#delCount').html(delCount);
            $('#sumCount').html(newSumCount);
        }
    });

    /**
     * 变更记录删除
     */
    assetNameNewDetailTbl.bind('click', '.del', function(data, index) {
        assetNameNewDetailTbl.removeRow(data);
        if (data.operatePs == RSK_OPERATE_PS_01 || data.operatePs == RSK_OPERATE_PS_03){
            assetNameHistoryDetailTBL.reload();
        }
    });

    /**
     * 绑定删除事件
     */
    assetNameHistoryDetailTBL.bind('click', '.delt', function(data, index) {
        var assetNameNewAllRows = assetNameNewDetailTbl.getAllRows();
        // 倒序删除，正序会存在问题
        var isConfirm = false;
        var confirmMessageArray = new Array();
        for (var j in assetNameNewAllRows) {
            if (assetNameNewAllRows[j].listDetailName == data.listDetailName) {
                isConfirm = true;
                confirmMessageArray.push(data.listDetailName);
                break;
            }
        }
        if (isConfirm) {
            Joyin.confirm(JoyinUtil.getMessage("RSK_C_0001", confirmMessageArray.join("，")), function () {
                // 倒序删除，正序会存在问题
                for (var j in assetNameNewAllRows) {
                    // 覆盖履历列表重复记录
                    if (assetNameNewAllRows[j].listDetailName == data.listDetailName) {
                        assetNameNewDetailTbl.removeRow(assetNameNewAllRows[j]);
                    }
                }
                assetNameHistoryDetailTBL.removeRow(data);
                data.operatePs =  RSK_OPERATE_PS_01;
                data.operateType = $('#operateStyle').val();
                data.listDetailType = $('#listDetailType').val();
                assetNameNewDetailTbl.addRow(data);
            });
        } else {
            // 倒序删除，正序会存在问题
            assetNameHistoryDetailTBL.removeRow(data);
            data.operatePs =  RSK_OPERATE_PS_01;
            data.operateType = $('#operateStyle').val();
            data.listDetailType = $('#listDetailType').val();
            assetNameNewDetailTbl.addRow(data);
        }
        assetNameHistoryDetailTBL.reload();
    });

    /**
     * 绑定删除事件
     */
    assetNameNewDetailTbl.bind('click', '.delt', function(data, index) {
        assetNameNewDetailTbl.removeRow(data);
        if (data.operatePs == RSK_OPERATE_PS_01 || data.operatePs == RSK_OPERATE_PS_03){
            assetNameHistoryDetailTBL.reload();
        }
    });

    /**
     * 加入按钮按下
     * @param array
     * @returns {boolean}
     */
    var joyinProcess = function () {
        var assetNameHistorySelectedRows = assetNameHistoryDetailTBL.getAllCheckedRows();
        var assetNameNewAllRows = assetNameNewDetailTbl.getAllRows();
        if (!assetNameHistorySelectedRows || assetNameHistorySelectedRows.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }
        if (assetNameHistorySelectedRows != null && assetNameHistorySelectedRows.length > 0) {
            // 如果 选择的资产对象 在履历列表中存在  则 提示 是否覆盖  履历列表中债券
            var isConfirm = false;
            var confirmMessageArray = new Array();
            // 倒序删除，正序会存在问题
            for (var i = assetNameHistorySelectedRows.length - 1; i >= 0; i--) {
                for (var j in assetNameNewAllRows) {
                    if (assetNameNewAllRows[j].listDetailName == assetNameHistorySelectedRows[i].listDetailName) {
                        isConfirm = true;
                        confirmMessageArray.push(assetNameHistorySelectedRows[i].listDetailName);
                        break;
                    }
                }
            }
            if (isConfirm) {
                Joyin.confirm(JoyinUtil.getMessage("RSK_C_0001", confirmMessageArray.join("，")), function () {
                    // 倒序删除，正序会存在问题
                    for (var i = assetNameHistorySelectedRows.length - 1; i >= 0; i--) {
                        for (var j in assetNameNewAllRows) {
                            // 覆盖履历列表重复记录
                            if (assetNameNewAllRows[j].listDetailName == assetNameHistorySelectedRows[i].listDetailName) {
                                assetNameNewDetailTbl.removeRow(assetNameNewAllRows[j]);
                            }
                        }
                        assetNameHistoryDetailTBL.removeRow(avaSelectedRows[i]);
                        assetNameHistorySelectedRows[i].operatePs =  RSK_OPERATE_PS_01;
                        assetNameHistorySelectedRows[i].operateType = $('#operateStyle').val();
                        assetNameHistorySelectedRows[i].listDetailType = $('#listDetailType').val();
                        assetNameNewDetailTbl.addRow(assetNameHistorySelectedRows[i]);
                    }
                });
            } else {
                // 倒序删除，正序会存在问题
                for (var i = assetNameHistorySelectedRows.length - 1; i >= 0; i--) {
                    assetNameHistoryDetailTBL.removeRow(assetNameHistorySelectedRows[i]);
                    assetNameHistorySelectedRows[i].operatePs =  RSK_OPERATE_PS_01;
                    assetNameHistorySelectedRows[i].operateType = $('#operateStyle').val();
                    assetNameHistorySelectedRows[i].listDetailType = $('#listDetailType').val();
                    assetNameNewDetailTbl.addRow(assetNameHistorySelectedRows[i]);
                }
            }
        }
    };

    /**
     * 保存按钮按下
     *
     * @param array
     * @returns {boolean}
     */
    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#OrgNameManagement003_submitForm', true);
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.operateStyle = $('#operateStyle').val();
        object.rskAccessListDetailList = assetNameNewDetailTbl.getAllRows();
        Joyin.ajaxJson(basePath + 'invest/risk/namelistmanage/orgnamelist/OrgNameManagement003/check', object, function(
            result) {
            finalSave(object,operateType);
        });
    }

    /**
     * 最终提交
     */
    var finalSave = function(object,operateType){
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath + 'invest/risk/namelistmanage/orgnamelist/OrgNameManagement003/save',
                object, "OrgNameManagement003_btn_save");
        } else {
            WorkFlow.doSubmitList(basePath + 'invest/risk/namelistmanage/orgnamelist/OrgNameManagement003/save',
                object, "OrgNameManagement003_btn_submit");
        }
    }

    /**
     * 调整后预览结果
     */
    var showAdjPriview = function(){
        Joyin.dialog(JoyinUtil.getLabel('OrgNameManagement005'), basePath +'invest/risk/namelistmanage/orgnamelist/OrgNameManagement005/init', '80%', '80%',
            {listId:$("#listId").val()},function(){
            });
    }

    var init = function(){
        if(pageModel === PAGE_MODEL_REFERENCE&&workflowId){
            $('.condition-area').hide();
            $('#historyAssetList').hide();
            $('#btn_joyin').hide();
        }
    }
    var getAssetNameNewAllRows = function(){
        return assetNameNewDetailTbl.getAllRows();
    }

    /**
     * 弹出新建画面
     * @returns
     */
    function showDialog() {
        Joyin.dialog("新建",
            basePath + "invest/risk/namelistmanage/orgnamelist/OrgNameManagement006/init","40%", "40%",{},function(result){
                if(!result){
                    return;
                } else{
                    var row = {
                        listDetailId: result.listDetailId,
                        listDetailName: result.listDetailName,
                        operatePs : RSK_OPERATE_PS_02,
                        operateType:$('#operateStyle').val(),
                        listDetailType:$('#listDetailType').val(),
                        listId:$('#listId').val()
                    };
                    assetNameNewDetailTbl.addRow(row);
                }
            });
    }

    // 公开接口
    window.OrgNameManagement003 = {
        getAssetNameNewAllRows:getAssetNameNewAllRows
    };
    /**
     * 画面加载完成
     */
    $(function () {
        // 初始化时
        init();
        // 添加检索事件
        $("#OrgNameManagement003_btn_search").click(function () {
            // 检索历史名单明细列表
            assetNameHistoryDetailTBL.load();
        });
        $('#btn_joyin').click(joyinProcess);
        //调整后预览
        $('#OrgNameManagement003_btn_adj').click(showAdjPriview);
        // 绑定按钮事件
        $('#OrgNameManagement003_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#OrgNameManagement003_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        //资产新建
        $('#OrgNameManagement003_btn_add').click(showDialog);
    });
}());
