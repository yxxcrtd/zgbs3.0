(function () {
        /**
         * 获取查询参数
         */
        var getSearchParams = function () {
            var result = $.serializeToObject('#searchForm');
            if ($("#cashDate").val()) {
                result.cashDateStart = $("#cashDate").startDate();
                result.cashDateEnd = $("#cashDate").endDate();
            }
            if ($("#portfolioId").val()) {
                result.portfolioId = $("#portfolioId").val().join(",");
            } else {
                result.portfolioId = null;
            }
            if ($("#finprodId").val()) {
                result.finprodId = $("#finprodId").val().join(",");
            } else {
                result.finprodId = null;
            }
            // add by wangyang1 【吉林银行】 增加 产品经理  2019年9月29日 start 
			if ($("#prodManager").val()) {
                result.prodManager = $("#prodManager").val();
            } else {
                result.prodManager = null;
            }
			// add by wangyang1 【吉林银行】 增加 产品经理  end 
            return result;
        };

        /**
         * 定义数据表格
         */
        var table = $("#dataTable").joyintable({
            ajax: {
                url: 'findPage',
                type: 'POST',
                data: function () {
                    return getSearchParams();
                }
            },
            checkbox: false,
            isMulti: true,
            cols: [[{title: '系统流水', colspan: 10, align:'center'}, {title: '托管流水', colspan: 7, align:'center'}], [
                {field: 'groupId', title: '匹配状态',width:100, sortable: false}
                , {
                    field: 'sysTradeId', title: '业务编号', sortable: false, width:200, render: function (data, type, row, meta) {
                        if(row.sysBussType=='10'||row.sysBussType=='35'||row.sysBussType=='36'||row.sysBussType=='37'){
                            return data;
                        }
                        return "<a class='pro-link' style='cursor: pointer;' " +
                            "href='javascript:openOperateWindow(\""+STATUS_VALID_05+"\",\"" + row.sysBussType + "\",\"" + row.sysPortfolioId + "\",\""
                            + row.sysTradeId + "\",null,\"" + row.sysFinprodId + "\",\"" + row.ps + "\",null,null,\""+row.profitType+"\",\"" + row.finprodId+ "\",null,null,\"" +row.mdateStr+"\")'>" + data + "</a>";
                    }
                }
                , {field: 'sysCdate', title: '现金流日期', sortable: false, type: 'date'}
                , {field: 'sysBussTypeString', title: '业务类型',width:200, sortable: false}
                , {field: 'sysPortfolioId', title: '组合代码',width:200, sortable: false}
                , {field: 'sysPortfolioName', title: '组合名称',width:200, sortable: false}
                , {field: 'sysFinprodId', title: '产品/资产代码',width:150, sortable: false}
                , {field: 'sysFinprodName', title: '产品/资产名称', width:150, sortable: false}
                , {field: 'sysPrinamt', title: '金额', type: 'number',width:200, sortable: false}
                , {field: 'sysCcy', title: '币种',width:100, sortable: false}
                , {field: 'truAccount', title: '账号',width:200, sortable: false}
                , {field: 'truPsString', title: '借贷方向',width:80, sortable: false}
                , {field: 'truPrinamt', title: '金额', type: 'number', width:200, sortable: false}//金额
                , {field: 'truSeller', title: '对手方账户',width:200, sortable: false}//对手方账户
                , {field: 'truSellername', title: '对手方账户名称',width:200, sortable: false}//对手账户名称
                , {field: 'truUsage', title: '资金用途',width:200, sortable: false}//用途
                , {field: 'remark', title: '备注',width:200, sortable: false}//备注
            ]],
            done:function(){
                alarmTableRowSpan('groupId', 1);
                $(".layui-table-body .layui-table td[rowspan]").each(function(){
                    if ($(this).children().text().indexOf("GROUP_ID") >= 0) {
                        $(this).children().text('已匹配')
                    } else {
                        $(this).children().text('未匹配')
                    }
                })
                $(".laytable-cell-group").css({'text-align':'center'})
            },
            progressBar: true
        });

        var exportExcel = function () {
            Joyin.exportExcel('export', getSearchParams());
        };

        $(function () {
            $("#CashFlowBalanceManagement001_btn_search").click(function () {
                table.load();
            })
            $("#CashFlowBalanceManagement001_btn_export").click(function () {
                exportExcel();
            })  
        });
    }()
);

/**
 *  name layuiRowspan
 * @param fieldName  要合并列的field属性值
 * @param index 要合并列的索引值 从1开始(如果要合并第一列，则index = 1;)
 * @desc 此方法适用于合并列冻结的单元格
 */
function layuiRowspan(fieldName, index) {
    // 左侧列为冻结的情况
    //  var tbodyNode = document.getElementsByClassName("layui-table-fixed-l")[index-1];
    // 左侧导航栏不冻结的情况
    var fixedNode = document.getElementsByClassName("layui-table-body")[index - 1];
    var child = tbodyNode.getElementsByTagName("td");
    var childFilterArr = [];
    // 获取data-field属性为fieldName的td
    for (var i = 0; i < child.length; i++) {
        if (child[i].getAttribute("data-field") == fieldName) {
            childFilterArr.push(child[i]);
        }
    }
    // 获取td的个数和种类
    var childFilterTextObj = {};
    for (var i = 0; i < childFilterArr.length; i++) {
        var childText = childFilterArr[i].textContent;
        if (childFilterTextObj[childText] == undefined) {
            childFilterTextObj[childText] = 1;
        } else {
            var num = childFilterTextObj[childText];
            childFilterTextObj[childText] = num * 1 + 1;
        }
    }
    // 给获取到的td设置合并单元格属性
    for (var key in childFilterTextObj) {
        var tdNum = childFilterTextObj[key];
        var canRowspan = true;
        for (var i = 0; i < childFilterArr.length; i++) {
            if (childFilterArr[i].getAttribute("data-content") == key) {
                if (canRowspan) {
                    childFilterArr[i].setAttribute("rowspan", tdNum);
                    canRowspan = false;
                } else {
                    childFilterArr[i].style.display = "none";
                }
            }
        }
    }
}

/**
 * name layui合并tbody中单元格的方法
 * @param fieldName  要合并列的field属性值
 * @param index 表格的索引值 从1开始
 * @desc 此方式适用于没有列冻结的单元格合并
 */
function alarmTableRowSpan(fieldName, index) {
    var fixedNode = document.getElementsByClassName("layui-table-body")[index - 1];
    if (!fixedNode) {
        return false;
    }
    var child = fixedNode.getElementsByTagName("td");
    var childFilterArr = [];
    // 获取data-field属性为fieldName的td
    for (var i = 0; i < child.length; i++) {
        if (child[i].getAttribute("data-field") == fieldName) {
            childFilterArr.push(child[i]);
        }
    }
    // 获取td的个数和种类
    var childFilterTextObj = {};
    for (var i = 0; i < childFilterArr.length; i++) {
        var childText = childFilterArr[i].textContent;
        if (childFilterTextObj[childText] == undefined) {
            childFilterTextObj[childText] = 1;
        } else {
            var num = childFilterTextObj[childText];
            childFilterTextObj[childText] = num * 1 + 1;
        }
    }
    // 给获取到的td设置合并单元格属性
    for (var key in childFilterTextObj) {
        var tdNum = childFilterTextObj[key];
        var canRowSpan = true;
        for (var i = 0; i < childFilterArr.length; i++) {
            if (childFilterArr[i].textContent == key) {
                if (canRowSpan) {
                    childFilterArr[i].setAttribute("rowspan", tdNum);
                    canRowSpan = false;
                } else {
                    childFilterArr[i].style.display = "none";
                }
            }
        }
    }
}
