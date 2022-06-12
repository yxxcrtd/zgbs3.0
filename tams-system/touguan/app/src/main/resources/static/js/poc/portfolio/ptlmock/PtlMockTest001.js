/**
 * 证券账户管理
 */
$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
            portfolioId : $('#portfolioId').val(),
            dataDateStart : $.trim($('#mockDateStr').startDate()),
            dataDateEnd : $.trim($('#mockDateStr').endDate()),
        };
    };
    /**
     * 定义数据表格
     */
    var table = $("#dataTable").joyintable(
            {
                ajax : {
                    url : 'findPage',
                    type : 'POST',
                    data : function() {
                        return getSearchParams();
                    }
                },
                dblclick : function(table, data) {
                    data.pageModel = PAGE_MODEL_REFERENCE;
                    Joyin.dialog(JoyinUtil.getLabel("PtlMockTest002"), JoyinUtil
                            .getRealPath("poc/portfolio/ptlmock/PtlMockTest002/init"), "99%",
                            "99%", data, function() {
                                table.reload();
                            });

                },
                cols : [ {
                    field : 'mockId',
                    visible : false
                }// 模拟编号
                , {
                    field : 'portfolioId',
                    title : 'PtlMockTest001.portfolioId',
                    width: 150,
                    sortField : 'portfolioId',
                    render : function(data, type, row, meta) {
                        return "<a class='portfolioId-link' style='cursor: pointer;'>" + row.portfolioId + "</a>";
                    }
                }// 组合代码
                , {
                    field : 'portfolioName',
                    title : 'PtlMockTest001.portfolioName',
                    width: 200,
                    sortField : 'portfolioName'
                }// 组合名称
                , {
                    field : 'mdate',
                    title : 'PtlMockTest001.mdate',
                    width: 150,
                    sortField : 'mdate',
                    type : 'date'
                }// 到期日
                , {
                    field : 'againInvestRate',
                    title : 'PtlMockTest001.againInvestRate',
                    width: 200,
                    type : 'number',
                    sortField : 'againInvestRate'
                }// 再投资收益率%
                , {
                    field : 'finanCost',
                    title : 'PtlMockTest001.finanCost',
                    width: 150,
                    type : 'number',
                    sortField : 'finanCost'
                }// 融资成本%
                , {
                    field : 'mockYield',
                    title : 'PtlMockTest001.mockYield',
                    width: 150,
                    type : 'number',
                    sortField : 'mockYield'
                }// 到期收益率
                , {
                    field : 'mockDate',
                    title : 'PtlMockTest001.mockDate',
                    width: 150,
                    type : 'datetime',
                    sortField : 'mockDate'
                } // 试算日期
                ]
            });

    /**
     * 点击组合代码链接跳转到该组合信息查看页面
     */
    table.bind('click','.portfolioId-link', function(row) {
        Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002"),
                basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
                "95%", "90%",
                { pageModel: PAGE_MODEL_REFERENCE,
                portfolioId: row.portfolioId});
    });

    /**
     * 画面加载完成
     */
    $(function() {
        // 绑定删除按钮
        $('#PtlMockTest001_btn_delete').click(
            function(data) {
                if (table != null && table != '') {
                    var selArr = table.getAllCheckedRows();
                }
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                var object = {
                    selectedItem : selArr,
                    operateType : "delete"
                };
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function() {
                    WorkFlow.doDeleteList(basePath + 'poc/portfolio/ptlmock/PtlMockTest001/delete', object,
                            "PtlMockTest001_btn_delete", "PtlMockTest001", function() {
                        table.reload();
                    });// 删除
                });
        });
        // 绑定查询按钮
        $('#PtlMockTest001_btn_search').click(function() {
            table.load();// 加载表格
        });
        // 绑定导出
        $('#PtlMockTest001_btn_export').click(function() {
            var paramObj = getSearchParams();
            Joyin.exportExcel('export', paramObj);
        });

        // 新建试算
        $('#PtlMockTest001_btn_add').click(
            function() {
                Joyin.dialog(JoyinUtil.getLabel("PtlMockTest002"), basePath
                        + "poc/portfolio/ptlmock/PtlMockTest002/init", "99%", "99%", {
                    pageModel : PAGE_MODEL_NEW
                }, function() {
                    table.reload();
                });
            });
    });
}());