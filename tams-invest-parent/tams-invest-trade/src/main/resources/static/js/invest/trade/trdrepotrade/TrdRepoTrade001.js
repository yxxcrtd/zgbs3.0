(function () {
    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
        var pageParam = {
            // 委托代码
            entrustId: cstpEntrustId?cstpEntrustId:$('#entrustId').val()
            // 组合代码
            , portfolioId: $('#portfolioId').val()
            // 交易日期
            , tradeDateBegin: $('#tradeDate').startDate()
            , tradeDateEnd: $('#tradeDate').endDate()
            // 回购类型
            , repoType: $('#repoType').val()
            // 交易方向
            , ps: $('#ps').val()
            // 有效状态
            , validStatus: $('#validStatus').val()
            ,fTradeId:fTradeId ? fTradeId : $('#fTradeId').val()
            ,chlFinprodId: $('#chlFinprodId').val()
            ,finprodId: $('#finprodId').val()
        };

        if ($("#portfolioId").val()) {
            pageParam.portfolioId = $("#portfolioId").val().join(",");
        } else {
            pageParam.portfolioId = null;
        }

        if ($("#chlFinprodId").val()) {
            pageParam.chlFinprodId = $("#chlFinprodId").val().join(",");
        } else {
            pageParam.chlFinprodId = null;
        }
        if ($("#finprodId").val()) {
            pageParam.finprodId = $("#finprodId").val().join(",");
        } else {
            pageParam.finprodId = null;
        }

        return pageParam;
    };
    /**
     * 定义数据表格
     */

    var listTbl = $("#dataTable").joyintable({
        // ajax请求数据
        ajax: {
            // 请求路径
            url: 'selectForPage',
            // 请求类型
            type: 'POST',
            data: function () {
                // 获取请求参数
                return getSearchParams();
            }
        },
        dblclick: function (table, data) {
            data.pageModel = PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("TrdRepoTrade002.reference"),
                JoyinUtil.getRealPath("invest/trade/trdrepotrade/TrdRepoTrade002/init"),
                "99%", "99%", data
                , function(){table.reload();}
            );
        },
        /**
         * 定义表头
         */
        cols: [
            {field: 'validStatusText', width:120, sortField:'validStatus', title: 'TrdRepoTrade001.colModel.validStatus'}
            , {field: 'tradeId', width:180, sortField:'tradeId', title: 'TrdRepoTrade001.colModel.tradeId'}
            , {field: 'entrustId', width:200, sortField:'entrustId', title: 'TrdRepoTrade001.colModel.entrustId'}
            //,{field:'fTradeId',sortField:'fTradeId', width:180, title: 'TrdBondInvestTrade002.fTradeId'}
            // TISHKS-674:CLONE - 【交易管理】【场内交易】【场内质押式回购交易】系统中缺少成交编号字段，导出表格中多了合作资产字段 modify by huwei 2022/5/7 start
            , {field:'fTradeId',sortField:'fTradeId', width:180, title: '成交编号', visible:pageId == 'TrdRepoTradeEx001'}
            // TISHKS-674:CLONE - 【交易管理】【场内交易】【场内质押式回购交易】系统中缺少成交编号字段，导出表格中多了合作资产字段 modify by huwei 2022/5/7 end
            , {
                field: 'portfolioId', width:180, sortField:'portfolioId', title: 'TrdRepoTrade001.colModel.portfolioId',
                render: function (data, type, row, meta) {
                    return "<a class='portfolioId-link' style='cursor: pointer;'>" + row.portfolioId + "</a>";
                }
            }
            , {field: 'portfolioName', width:180, sortField:'portfolioName', title: 'TrdRepoTrade001.colModel.portfolioName'}
            // , {field: 'chlFinprodId', width:180, sortField:'chlFinprodId', title: 'TrdRepoTrade001.colModel.chlFinprodId'}
            , {field: 'tradeMarket', width:180, sortField:'tradeMarket', title: 'TrdRepoTrade001.colModel.tradeMarket'}
            , {field: 'bondName', width:180, sortable:false, title: 'TrdRepoTrade001.colModel.bondName'}
            , {field: 'repoTypeText', width:180, sortField:'repoType', title: 'TrdRepoTrade001.colModel.repoType'}
            , {field: 'termTypeReback', width:150, sortField:'termTypeReback', title: 'TrdRepoTrade001.colModel.termTypeReback'}
            , {field: 'psText', width:120, sortField:'ps', title: 'TrdRepoTrade001.colModel.ps'}
            , {field: 'tradeDate', width:120, sortField:'tradeDate', title: 'TrdRepoTrade001.colModel.tradeDate', type: 'date'}
            , {field: 'prinAmt', width:120, sortField:'prinAmt', title: 'TrdRepoTrade001.colModel.prinAmt', type: 'money'}
            , {field: 'ccyText', width:180, sortField:'ccy', title: 'TrdRepoTrade001.colModel.ccy'}
            , {field: 'remark', width:120, sortable:false, title: 'TrdRepoTrade001.colModel.remark'}
            , {field: 'ps', visible: false}
            , {field: 'repoType', visible: false}
            , {field: 'validStatus', visible: false}
            , {field: 'ccy', visible: false}
        ],
        initSort: {field: 'tradeDate',type: 'desc'}
    });
    var exportExcel = function () {
        Joyin.exportExcel('export', getSearchParams());
    };

    var exportMailModelExcel = function () {
        var selArr;
        if (listTbl != null && listTbl != '') {
            selArr = listTbl.getAllCheckedRows();
        }
        // 列表中未勾选
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }
        var tradeId = [];
        var isAllSellRepoFlag = true;
        for (var index = selArr.length - 1; index >= 0; index--) {
            if (!(selArr[index]["repoType"] == TARGET_BACK_TYPE_H01
                && selArr[index]["ps"] == BACK_LEND_DIRECTION_03)) {
                isAllSellRepoFlag = false;
            }
            tradeId.push(selArr[index]["tradeId"]);
        }
        // 如果所有选择行都是协议式正回购，则导出，否则提示 
        if (!isAllSellRepoFlag) {
        	//TRD_E_0011 修改为TRD_E_0017，提示语冲突，modify by zhujun@20190524
            Joyin.error(JoyinUtil.getMessage("TRD_E_0017"));
            return;
        }
        Joyin.exportExcel('exportMailModel', {tradeId: tradeId.join(",")});
    };

    /**
     * 导出pdf add by zengchengfa 20210803
     */
    var exportPdf = function() {
        if(listTbl!=null && listTbl!=''){
            var selArr = listTbl.getAllCheckedRows();
            // 列表中未勾选
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var paramObj = getSearchParams();
            //勾选导出
            var selectedItem = [];
            $.each(listTbl.getAllCheckedRows() ,function (i, item) {
                selectedItem.push(item.tradeId);
            });
            paramObj.selectedItem = selectedItem.join(',');
            Joyin.exportExcel('exportPdf', paramObj);
        }else{
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
        }
    };

    /**
     * 点击组合代码链接跳转到该组合信息查看页面
     */
    listTbl.bind('click', '.portfolioId-link', function (row) {
        Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002.see"),
            basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
            "95%", "90%",
            {
                pageModel: PAGE_MODEL_REFERENCE,
                portfolioId: row.portfolioId,
                portfolioName: row.portfolioName
            });
    });

    /**
     * 画面加载完成
     */
    $(function () {

        if (pageId == 'TrdRepoTradeEx001') {
            $('#fTradeIdDiv').hide();
        }

        if(fTradeId||cstpEntrustId){
            $('.form-button-area').hide();
            $('.curr-form').hide();
            listTbl.load();
        }
        // 初始化查询
        $('#'+pageId+'_btn_search').click(function () {
            listTbl.load();
            $('#'+pageId+'_btn_revoke').show();
        });
        $('#'+pageId+'_btn_export').click(function () {
            exportExcel();
        });
        $('#'+pageId+'_btn_export_mail_model').click(function () {
            exportMailModelExcel();
        });
        //add by zengchengfa 20210803 pdf导出
        $('#'+ pageId +'_btn_export_pdf').click(function() {
            exportPdf();
        });
        // 绑定新建按钮事件
        $('#'+pageId+'_btn_add').click(function () {
            Joyin.dialog(JoyinUtil.getLabel("TrdRepoTrade002.new"), basePath
                + "invest/trade/trdrepotrade/TrdRepoTrade002/init", "99%", "99%", {
                pageModel: PAGE_MODEL_NEW,parentPageId:pageId
            }, function () {
                listTbl.reload();
            });
        });
        // 绑定多组合新建按钮事件
        $('#TrdRepoTrade003_btn_add').click(function () {
            Joyin.dialog(JoyinUtil.getLabel("TrdRepoTrade003.new"), basePath
                + "invest/trade/trdrepotrade/TrdRepoTrade003/init", "99%", "99%", {
                pageModel: PAGE_MODEL_NEW,parentPageId:pageId
            }, function () {
                listTbl.reload();
            });
        });
        // 绑定质押券管理按钮事件
        $('#'+pageId+'_btn_manage').click(function () {
            Joyin.dialog(JoyinUtil.getLabel("TrdStandardBond001"), basePath
                + "invest/trade/trdstandardbond/TrdStandardBond001/init", "99%", "99%", {
                pageModel: PAGE_MODEL_NEW
            }, function () {
            });
        });
        // 质押券置换按钮事件
        $('#'+pageId+'_btn_substitution').click(function () {

            if (listTbl != null && listTbl != '') {
                var selArr = listTbl.getAllCheckedRows();
            }
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
            if(selArr[0]["validStatusText"]!='有效' || (selArr[0]["repoTypeText"]!='质押式协议回购' && selArr[0]["tradeMarket"]!='银行间')){
                Joyin.error(JoyinUtil.getMessage("请选择交易状态为有效、回购类型为质押式协议回购、直投的数据进行操作!"));
                return;
            }
            //update by yangwenjun 20201123 质押式回购-交易所协议回购-通道买入无法做质押券置换 start
            var param={
                tradeId:selArr[0]["tradeId"]
            }
            Joyin.ajaxJson(basePath+'invest/trade/trdrepotrade/TrdRepoTrade004/getChlId',param,function(result){
                var chlId=result.chlId;
                 //只有交易状态为有效，回购类型为质押式协议回购才允许进行质押券置换操作
                if(chlId&&chlId!=''){
                    Joyin.error(JoyinUtil.getMessage("请选择交易状态为有效、回购类型为质押式协议回购、直投的数据进行操作!"));
                    return;
                }else{
                    Joyin.dialog(JoyinUtil.getLabel("TrdStandardBond004"), basePath+ "invest/trade/trdrepotrade/TrdRepoTrade004/init", "99%", "99%", selArr[0],{pageModel: PAGE_MODEL_EDIT}, function () {
                         listTbl.reload();
                    });
                }
            });
            //update by yangwenjun 20201123 质押式回购-交易所协议回购-通道买入无法做质押券置换 end

        });

        /**
         * 撤销
         */
        $('#'+pageId+'_btn_revoke').click(function () {
            if (listTbl != null && listTbl != '') {
                var selArr = listTbl.getAllCheckedRows();
            }
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

            var checkObj = {};
            checkObj.tradeId = selArr[0].tradeId;
            checkObj.repoType = selArr[0].repoType;
            checkObj.operateType = "revoke";
            Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade001/checkRevoke', checkObj, function (result) {
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                    checkObj.revokeReason = result;
                    WorkFlow.doRevokeList(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade001/doRevoke', checkObj, "TrdRepoTrade001_btn_revoke", "TrdRepoTrade002");
                });
            });
        });

    });

    //回购类型在切换后事件
    $("#repoType").on("change", function () {
        if($("#repoType").val() == 'H02'){
            $("#chlFinprodId").val('').trigger("change");
            $("#finprodId").val('').trigger("change");
            $("#chlFinprodId").prop("disabled", true).trigger("change");
            $("#finprodId").prop("disabled", true).trigger("change");
        }else{
            $("#chlFinprodId").prop("disabled", false).trigger("change");
            $("#finprodId").prop("disabled", false).trigger("change");
        }
    });

}());