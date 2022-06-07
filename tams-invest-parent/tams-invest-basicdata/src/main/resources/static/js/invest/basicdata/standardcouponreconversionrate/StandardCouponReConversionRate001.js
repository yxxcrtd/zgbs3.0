$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var object = $.serializeToObject('#searchForm',true);
        return object;
    };

    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        dblclick:function(table, data) {
            data.pageModel = PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                JoyinUtil.getRealPath("invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate002/init"),
                "75%", "75%", data,
                function(){
                    table.reload();
                });
        },
        cols:[{field:'brokerId',width:200,  title: '券商',visible:false}
            ,{field:'seqNo',width:200,  title: '序列号',visible:false}
            ,{field:'brokerName',width:200,  title: '券商'}
                       ,{field:'accountType',width:200,  title: '资金账号',visible:false}
                        ,{field:'accountTypeStr',width:200,  title: '资金账户'}
            ,{field:'finprodId',width:200,  title: '资产代码',visible:false}
            /**修改资产代码 排序 liuzilong 2021-11-09**/
            ,{field:'finprodIdStr',width:200,  title: '资产代码' ,visible:false}
            ,{field:'finprodMarketIdStr',width:200,  title: '资产代码',sortField:"finprodMarketId", sort: true}
            ,{field:'finprodAbbr',width:200,  title: '资产名称'}
            ,{field:'moreConvrate',width:200,typeapplication:'number', title: '标准券再折算率（%）'}
            ,{field:'startDate',width:200,  title: '开始使用日期'}
            ,{field:'endDate',width:200,  title: '结束使用日期'}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    });

    /**
     * 导出
     */
    var exportExcel = function(){
        var paramObj=getSearchParams();
        console.log(paramObj, "到处来数据的")
        Joyin.exportExcel('export', paramObj);
    };


    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.operateType = "edit";
        data.pageModel = PAGE_MODEL_EDIT;
        var object = data;
        object.operateType = "edit";
        Joyin.ajaxJson(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate001/check', object, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("标准券再折算率修改"),
                basePath + "invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate002/init",
                "75%", "75%", {
                    pageModel: PAGE_MODEL_EDIT,
                    brokerId: data.brokerId,
                    finprodId: data.finprodId,
                    accountType:data.accountType,
                    startDate: data.startDate,
                    seqNo:data.seqNo
                }, function () {
                    table.reload();
                })
        });
    });
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate001/delete', object, "StandardCouponReConversionRate001_btn_delete", "StandardCouponReConversionRate002");
            });
        });
    });

    // 新建
    function showDialog() {
        Joyin.dialog(JoyinUtil.getLabel('标准券再折算率新建'), '../StandardCouponReConversionRate002/init', '98%', '98%')
    }


    /**
     * 画面加载完成
     */
    $(function() {
        $('#StandardCouponReConversionRate001_btn_search').click(function() {
            table.load();
        });

        $('#StandardCouponReConversionRate001_btn_add').click(showDialog);

        $('#StandardCouponReConversionRate001_btn_export').click(function() {
            exportExcel();
        });

        $('#StandardCouponReConversionRate001_btn_import').click(function() {
            Joyin.confirm("请确认是否要导入数据？", function() {
                Joyin.uploadFile({pageId:'StandardCouponReConversionRate001MTM'},function(fileName) {
                    Joyin.ajax(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate001/checkConfirmMsgForImportData', {fileName: fileName}, function(result) {
                        var a = result;
                        Joyin.ajax(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate001/importFile', {fileName: fileName});
                    });
                },function() {
                    table.reload();
                });
            });
        });

        $('#StandardCouponReConversionRate001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(basePath + 'invest/basicdata/standardcouponreconversionrate/StandardCouponReConversionRate001/delete', object, "StandardCouponReConversionRate001_btn_delete", "StandardCouponReConversionRate002");
                });
            });
        });
    });
}());