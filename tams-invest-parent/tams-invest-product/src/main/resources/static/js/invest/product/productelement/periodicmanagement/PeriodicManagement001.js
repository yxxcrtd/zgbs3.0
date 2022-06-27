$(function () {
    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
        var result = $.serializeToObject('#searchForm');
        if (result.finprodId) {
            result.finprodId = result.finprodId.join(",");
        }
        if ($("#beginDate").val()) {
            result.startDateBegin = $("#beginDate").startDate();
            result.startDateEnd = $("#beginDate").endDate();
        } else {
            result.startDateBegin = null;
            result.startDateEnd = null;
        }
        if ($("#endDate").val()) {
            result.endDateBegin = $("#endDate").startDate();
            result.endDateEnd = $("#endDate").endDate();
        } else {
            result.endDateBegin = null;
            result.endDateEnd = null;
        }
        return result;
    };

    /**
     * 定义数据表格
     */
    var table = $("#dataTable").joyintable({
        ajax: {
            url: 'selectForPage',
            type: 'POST',
            data: function () {
                return getSearchParams();
            }
        },
        /**
         * 双击查看
         */
        dblclick: function (table, data) {
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/product/productelement/periodicmanagement/PeriodicManagement002/init",
                "95%", "90%", {
                    pageModel: PAGE_MODEL_REFERENCE,
                    operateType: "look",
                    periodProdId: data.periodProdId,
                    periodId: data.periodId,
                    branch: data.branch,
                    finprodId: data.finprodId == undefined ? "" : data.finprodId,
                    startDate: data.startDate == undefined ? "" : (new Date(data.startDate)).format("yyyy-MM-dd"),
                    endDate: data.endDate == undefined ? "" : (new Date(data.endDate)).format("yyyy-MM-dd"),
                    investCycle: data.investCycle == undefined ? "" : data.investCycle
                });
        },
        checkbox: true,
        cellMinWidth: 100,
        cols: [
            {field: 'termType', visible: false},
            {field: 'periodProdId', visible: false},
            {field: 'branch', visible: false},
            // 2019-11-18 yangxiaofeng BSTOZ-1273
            {field: 'finprodId', visible: false},
            {field: 'finprodMarketId', title: '产品代码', sort: true, sortField: 'finprodMarketId', width: 150},
            // 2019-11-18 yangxiaofeng BSTOZ-1273
            {field: 'investCycle', visible: false},
            {field: 'publishType', visible: false},
            {field: 'rateType', visible: false},
            {field: 'rateLower', visible: false},
            {field: 'rateLimit', visible: false},
            {field: 'rate', visible: false},
            {field: 'indexId', visible: false},
            {field: 'coefficient', visible: false},
            {field: 'spreadRate', visible: false},
            {field: 'resetType', visible: false},
            {field: 'validStatus', title: '有效状态', sort: true, sortField: 'validStatus', width: 150},
            {field: 'periodId', title: '期次代码', sort: true, sortField: 'periodId', width: 150},
            {field: 'startDate', title: '期次开始日', type: 'date', sort: true, sortField: 'startDate', width: 150},
            {field: 'endDate', title: '期次结束日', type: 'date', sort: true, sortField: 'endDate', width: 150},
            {field: 'rateTypeStr', title: '利率类型', sort: true, sortField: 'rateType', width: 150},
            {field: 'baseRule', title: '业绩基准', sort: true, sortField: 'baseRule', width: 150},
            {field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['edit', 'delt']}//编辑和删除事件
        ],
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function (data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        data.operateType = 'edit';
        data.startDate = (new Date(data.startDate)).format("yyyy-MM-dd");
        data.endDate = (new Date(data.endDate)).format("yyyy-MM-dd");
        Joyin.ajaxJson(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement002/check', data, function (result) {
            Joyin.dialog(JoyinUtil.getLabel("PeriodicManagement002.edit"),
                basePath + "invest/product/productelement/periodicmanagement/PeriodicManagement002/init",
                "98%", "95%", data, function () {
                    table.reload();
                });
        })
    });
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function (data, index) {
        //发起流程
        var object = {selectedItem: [data], operateType: "delete"};
        data.operateType = "delete";
        Joyin.ajaxJson(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement001/check', data, function (result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                WorkFlow.doDeleteList(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement001/delete', data, "PeriodicManagement001_btn_delete");
            });
        });
    });


    /**
     * 导出
     */
    var exportExcel = function () {
        /*if (table != null && table != '') {
            var selArr = table.getAllCheckedRows();
            var datas = JSON.stringify(selArr);
        }
        if (!selArr || selArr.length == 0) {
            var paramObj = getSearchParams();
            Joyin.exportExcel('export', paramObj);
        } else {
            Joyin.exportExcel('export', {
                datas : datas
            });
        }*/
        Joyin.exportExcel('export', getSearchParams());
    };

    /**
     * 画面加载完成
     */
    $(function () {
        // 绑定按钮事件
        $('#PeriodicManagement001_btn_search').click(function () {
            table.load();
        });
        //  绑定导出事件
        $('#PeriodicManagement001_btn_export').click(function () {
            exportExcel();
        });
        // 绑定新建事件
        $('#PeriodicManagement001_btn_add').click(function () {
            Joyin.dialog(JoyinUtil.getLabel("期次管理新建（ " + "<span style='color:red'> * </span>" + "必填）"),
                basePath + "invest/product/productelement/periodicmanagement/PeriodicManagement002/init",
                "90%", "90%", {pageModel: PAGE_MODEL_NEW, operateType: "add"}, function () {
                    table.reload();
                });
        });

        // 绑定确认事件
        $('#PeriodicManagement001_btn_confrim').click(function () {
            var selArr = table.getAllCheckedRows();
            // 列表中未勾选
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            // 列表中勾选对象1条以上时
            if (selArr.length > 1) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                return;
            }

            if (selArr[0].validStatus != "待处理") {
                Joyin.error(JoyinUtil.getMessage("PeriodicManagement001_E_001"));
                return;
            }
            var object = {
                pageModel: PAGE_MODEL_NEW,
                operateType: "confrim",
                finprodId: selArr[0].finprodId,
                periodProdId: selArr[0].periodProdId,
                investCycle: selArr[0].investCycle,
                startDate: (new Date(selArr[0].startDate)).format("yyyy-MM-dd"),
                endDate: (new Date(selArr[0].endDate)).format("yyyy-MM-dd"),
                publishType: selArr[0].publishType,
                rateType: selArr[0].rateType,
                rateLower: selArr[0].rateLower,
                rateLimit: selArr[0].rateLimit,
                rate: selArr[0].rate,
                indexId: selArr[0].indexId,
                coefficient: selArr[0].coefficient,
                spreadRate: selArr[0].spreadRate,
                resetType: selArr[0].resetType,
                termType: selArr[0].termType
            };

            Joyin.ajaxJson(basePath + '/invest/product/productelement/periodicmanagement/PeriodicManagement001/check', object, function (result) {
                Joyin.dialog(JoyinUtil.getLabel("期次管理新建（ " + "<span style='color:red'> * </span>" + "必填）"),
                    basePath + "invest/product/productelement/periodicmanagement/PeriodicManagement002/init",
                    "90%", "90%", object, function () {
                        table.reload();
                    });
            })

            // Joyin.dialog(JoyinUtil.getLabel("期次管理新建（ "+ "<span style='color:red'> * </span>" + "必填）"),
            //         basePath + "invest/product/productelement/periodicmanagement/PeriodicManagement002/init",
            //         "90%", "90%", object, function(){
            //             table.reload();
            // });
        });
    });
});



