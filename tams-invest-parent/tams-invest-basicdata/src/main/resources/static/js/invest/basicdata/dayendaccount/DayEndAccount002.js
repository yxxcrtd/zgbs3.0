(function() {
    //初始化委托列表
    /**
     * 定义数据表格
     */
    var cols = [
        {field:'account',sortable: false, title: 'DayEndAccount001.busiAcctNo',width:120}
        ,{field:'settleDate', type:'date',sortable: false, title: 'DayEndAccount001.settleDate',width:120}
        ,{field:'finprodId',sortable: false, title: 'DayEndAccount001.finprodId',width:120}
        ,{field:'tradeMarketStr',sortable: false, title: 'DayEndAccount001.tradeMarket',width:120}
        ,{field:'finprodTypeStr',sortable: false, title: 'DayEndAccount001.finprodType',width:120}
        ,{field:'commissionAmt', type:'money',sortable: false, title: 'DayEndAccount001.commissionAmt',width:120,digit: 2,
            render: function (data, type, row, meta) {
                if (row.commissionType === 'QYJ')
                    return Number(row.commissionAmt).toFixed(2);
                else
                    return Number(row.netCommissionAmt).toFixed(2);
            }}
        ,{field:'jsfAmt', type:'money',sortable: false, title: 'DayEndAccount001.jsfAmt',width:120,digit: 2}
        ,{field:'zgfAmt', type:'money',sortable: false, title: 'DayEndAccount001.zgfAmt',width:120,digit: 2}
        ,{field:'stampFee', type:'money',sortable: false, title: 'DayEndAccount001.stampFee',width:120,digit: 2}
        ,{field:'settleFee', type:'money',sortable: false, title: 'DayEndAccount001.settleFee',width:120,digit: 2}
        ,{field:'ghfAmt', type:'money',sortable: false, title: 'DayEndAccount001.ghfAmt',width:120,digit: 2}
        ,{field:'tradeAmt', type:'money',sortable: false, title: 'DayEndAccount001.tradeAmt',width:120,digit: 2}
        ,{field:'tradeNum', type:'money',sortable: false, title: 'DayEndAccount001.tradeNum',width:120,digit: 2}
        ,{field: 'seqNo', width: 150, visible: false}
        ,{field: 'tradeMarket', width: 150, visible: false}
        ,{field: 'finprodType', width: 150, visible: false}
    ];
    var dataTable = $("#dataTable").joyintable({
        ajax: {
            url: JoyinUtil.getRealPath('invest/basicdata/dayendaccount/DayEndAccount002/findDataPage'),
            type: 'POST',
            data: function() {
                return {seqNo: $('#seqNo').val(),
                    workflowId: $('#workflowId').val()
                };
            }
        },
        cols: cols,
        height: 250,
        checkbox: false,
        filter: false,
        page: true,
        searchOnInit: true,
        addbtn:true
    });

    /**
     * 定义数据表格
     */
    var colsDetail = [
         {field:'cjbh',sortable: false, title: 'DayEndAccount001.tradeId',width:120}
        ,{field:'psStr',sortable: false, title: 'DayEndAccount001.ps',width:120}
        ,{field:'commissionAmt', type:'money',edit: true,sortable: false, title: 'DayEndAccount001.commissionAmt',width:120,digit: 2,
            render: function (data, type, row, meta) {
                if (row.commissionType === 'QYJ')
                    return Number(row.commissionAmt).toFixed(2);
                else
                    return Number(row.netCommissionAmt).toFixed(2);
            }}
        ,{field:'jsfAmt', type:'money',edit: true,sortable: false, title: 'DayEndAccount001.jsfAmt',width:120,digit: 2}
        ,{field:'zgfAmt', type:'money',edit: true,sortable: false, title: 'DayEndAccount001.zgfAmt',width:120,digit: 2}
        ,{field:'stampFee', type:'money',edit: true,sortable: false, title: 'DayEndAccount001.stampFee',width:120,digit: 2}
        ,{field:'settleFee', type:'money',edit: true,sortable: false, title: 'DayEndAccount001.settleFee',width:120,digit: 2}
        ,{field:'ghfAmt', type:'money',edit: true,sortable: false, title: 'DayEndAccount001.ghfAmt',width:120,digit: 2}
        ,{field:'tradeAmt', type:'money',edit: true,sortable: false, title: 'DayEndAccount001.tradeAmt',width:120,digit: 2}
        ,{field:'tradeNum', type:'money',sortable: false, title: 'DayEndAccount001.tradeNum',width:120,digit: 2}
        ,{field: 'commissionAmt2', type:'money', width: 150, visible: false}
        ,{field: 'jsfAmt2', type:'money', width: 150, visible: false}
        ,{field: 'zgfAmt2', type:'money', width: 150, visible: false}
        ,{field: 'stampFee2', type:'money', width: 150, visible: false}
        ,{field: 'settleFee2', type:'money', width: 150, visible: false}
        ,{field: 'ghfAmt2', type:'money', width: 150, visible: false}
        ,{field: 'tradeAmt2', type:'money', width: 150, visible: false}
        ,{field: 'commissionAmtFlg', type:'money', width: 150, visible: false}
        ,{field: 'jsfAmtFlg', type:'money', width: 150, visible: false}
        ,{field: 'zgfAmtFlg', type:'money', width: 150, visible: false}
        ,{field: 'stampFeeFlg', type:'money', width: 150, visible: false}
        ,{field: 'settleFeeFlg', type:'money', width: 150, visible: false}
        ,{field: 'ghfAmtFlg', type:'money', width: 150, visible: false}
        ,{field:'finprodId',sortable: false,width:120, visible: false}
        ,{field:'isPayWithTrade',sortable: false,width:120, visible: false}
    ];
    var dataDetailTable = $("#dataDetailTable").joyintable({
        ajax: {
            url: JoyinUtil.getRealPath('invest/basicdata/dayendaccount/DayEndAccount002/findDataDetailPage'),
            type: 'POST',
            data: entity
        },
        cols: colsDetail,
        height: 250,
        checkbox: false,
        filter: false,
        page: true,
        searchOnInit: true,
        addbtn:true,
        // GZYHTZJY-1127:回购时.成交金额不准许修改 modify fuyongsheng start
        beforeEditCell: function(preVal, field, rowData){
            if(field === "tradeAmt"
                && (rowData.finprodType2 === 'F02'
                    || rowData.finprodType2 === 'F03'
                    || rowData.finprodType2 === 'F04'
                    || rowData.finprodType2 === 'F05')){
                return false;
            } else {

                // IF 系统参数.是否可修改结算文件中的费用数据 = 是
                // 	场内清算数据的					经手费、证管费、印花税、结算费、过户费 可编辑
                //
                // 	1.调用【成交明细查询】，看佣金是否包含其他一次性费用
                // 			IF 返回.经手费 = 0 or 空
                // 				场内清算数据的 经手费不可编辑
                // 			IF 返回.证管费 = 0 or 空
                // 				场内清算数据的 证管费不可编辑
                // 			IF 返回.印花税 = 0 or 空
                // 				场内清算数据的 印花税不可编辑
                // 			IF 返回.过户费 = 0 or 空
                // 				场内清算数据的 结算费不可编辑
                // 			IF 返回.过户费 = 0 or 空
                // 				场内清算数据的 结算费不可编辑
                //
                // IF 系统参数.是否可修改结算文件中的费用数据 = 否
                // 	场内清算数据的					经手费、证管费、印花税、结算费、过户费 不可编辑
                if (stlModifyCommission === 'N'
                    && (
                        field === 'jsfAmt' || field === 'zgfAmt' || field === 'stampFee' || field === 'settleFee' || field === 'ghfAmt'
                    )) {
                    return false;
                }
                if (stlModifyCommission === 'Y' && (field === 'jsfAmt' || field === 'zgfAmt' || field === 'stampFee' || field === 'settleFee' || field === 'ghfAmt')) {
                    let ret = true;
                    ret = (field === 'jsfAmt' && rowData.jsfAmt2)
                        || (field === 'zgfAmt' && rowData.zgfAmt2)
                        || (field === 'stampFee' && rowData.stampFee2)
                        || (field === 'settleFee' && rowData.settleFee2)
                        || (field === 'ghfAmt' && rowData.ghfAmt2);
                    return ret;
                }
                return true;
            }
        },
        // GZYHTZJY-1127:回购时.成交金额不准许修改 modify fuyongsheng end
        editCell : function(value, data, field, cell, column){
            if(field=='commissionAmt') {
                var rowData = {};
                // var amtCe = JoyinMath.doMinus(data.commissionAmt,data.commissionAmt2);
                // amtCe = JoyinMath.doMul(amtCe, data.commissionAmtFlg);
                // rowData.tradeAmt2 = JoyinMath.doAdd(data.tradeAmt2, amtCe);
                // rowData.tradeAmt = JoyinMath.doAdd(data.tradeAmt, amtCe);
                dataDetailTable.renderRow(data.LAY_TABLE_INDEX, rowData);
            } else if (field=='jsfAmt') {
                var rowData = {};
                // var amtCe = JoyinMath.doMinus(data.jsfAmt,data.jsfAmt2);
                // amtCe = JoyinMath.doMul(amtCe, data.jsfAmtFlg);
                // rowData.tradeAmt2 = JoyinMath.doAdd(data.tradeAmt2, amtCe);
                // rowData.tradeAmt = JoyinMath.doAdd(data.tradeAmt, amtCe);
                rowData.jsfAmt2 = data.jsfAmt;
                dataDetailTable.renderRow(data.LAY_TABLE_INDEX, rowData);
            } else if (field=='zgfAmt') {
                var rowData = {};
                // var amtCe = JoyinMath.doMinus(data.zgfAmt,data.zgfAmt2);
                // amtCe = JoyinMath.doMul(amtCe, data.zgfAmtFlg);
                // rowData.tradeAmt2 = JoyinMath.doAdd(data.tradeAmt2, amtCe);
                // rowData.tradeAmt = JoyinMath.doAdd(data.tradeAmt, amtCe);
                rowData.zgfAmt2 = data.zgfAmt;
                dataDetailTable.renderRow(data.LAY_TABLE_INDEX, rowData);
            } else if (field=='stampFee') {
                var rowData = {};
                // var amtCe = JoyinMath.doMinus(data.stampFee,data.stampFee2);
                // amtCe = JoyinMath.doMul(amtCe, data.stampFeeFlg);
                // rowData.tradeAmt2 = JoyinMath.doAdd(data.tradeAmt2, amtCe);
                // rowData.tradeAmt = JoyinMath.doAdd(data.tradeAmt, amtCe);
                rowData.stampFee2 = data.stampFee;
                dataDetailTable.renderRow(data.LAY_TABLE_INDEX, rowData);
            } else if (field=='settleFee') {
                var rowData = {};
                // var amtCe = JoyinMath.doMinus(data.settleFee,data.settleFee2);
                // amtCe = JoyinMath.doMul(amtCe, data.settleFeeFlg);
                // rowData.tradeAmt2 = JoyinMath.doAdd(data.tradeAmt2, amtCe);
                // rowData.tradeAmt = JoyinMath.doAdd(data.tradeAmt, amtCe);
                rowData.settleFee2 = data.settleFee;
                dataDetailTable.renderRow(data.LAY_TABLE_INDEX, rowData);
            } else if (field=='ghfAmt') {
                var rowData = {};
                // var amtCe = JoyinMath.doMinus(data.ghfAmt,data.ghfAmt2);
                // amtCe = JoyinMath.doMul(amtCe, data.ghfAmtFlg);
                // rowData.tradeAmt2 = JoyinMath.doAdd(data.tradeAmt2, amtCe);
                // rowData.tradeAmt = JoyinMath.doAdd(data.tradeAmt, amtCe);
                rowData.ghfAmt2 = data.ghfAmt;
                dataDetailTable.renderRow(data.LAY_TABLE_INDEX, rowData);
            }
            if(data.commissionType === 'JYJ'){
                rowData.netCommissionAmt = data.commissionAmt;
            }
        }
    });
    /**
     * 保存或提交
     *
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#searchForm');
        object.reconDealInfoList = dataTable.getAllRows();
        object.dealDetailList = dataDetailTable.getAllRows();
        Joyin.ajaxJson(basePath + 'invest/basicdata/dayendaccount/DayEndAccount002/check',
            object, function(result) {
                Joyin.confirm(result.data, function() {
                    if (operateType === 'save') {
                        WorkFlow.doSaveList(basePath
                            + 'invest/basicdata/dayendaccount/DayEndAccount002/save',
                            object, "DayEndAccount002_btn_save");
                    } else {
                        WorkFlow.doSubmitList(basePath
                            + 'invest/basicdata/dayendaccount/DayEndAccount002/save',
                            object, "DayEndAccount002_btn_submit");
                    }
                });
            });
    }
    /**
     * 画面加载完成
     */
    $(function() {
        /**
         * 提交
         */
        $('#DayEndAccount002_btn_submit').click(function () {
            saveOrSubmit('submit');
        });
    });
}());
