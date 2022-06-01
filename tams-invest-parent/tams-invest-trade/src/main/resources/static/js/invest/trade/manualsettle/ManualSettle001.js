(function(){
    /**
     * 是否快速查询
     * @type {string}
     */
    var isQuickFlag = true;
    var settleStatus = '';
    var settleSeqNo = '';
    /**
     * 获取查询参数
     */
    var searchform =function(){
        if(isQuickFlag){
            return {"settleStatus":settleStatus};
        }else{
            var data = $.serializeToObject('#searchForm');
            if ($("#portfolioId").val()) {
                data.portfolioId = $("#portfolioId").val().join(",");
            } else {
                data.portfolioId = null;
            }
            data.tradeDateStart = $('#tradeDate').startDate();//开始日
            data.tradeDateEnd = $('#tradeDate').endDate();//结束日
            data.settleDateStart = $('#settleDate').startDate();//开始日
            data.settleDateEnd = $('#settleDate').endDate();//结束日
            return data;
        }
    };

    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        height: 300,
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return searchform();
            }
        },
        click:function(table, data) {
            data.operateType = "click";
            settleSeqNo = data.settleSeqNo;
            dataTableDetail.table == null ? dataTableDetail.load() : dataTableDetail.reload();
            // GNS-940:根据资产类型控制详情列展示 modify by fuyongsheng 20220223 start
            if(data && data.finprodType2 && data.finprodType2 === 'F01'){
                dataTableDetail.showColumns([{field:'settleNumStr',visible: true}]);
                dataTableDetail.showColumns([{field:'settleAmt',visible: true}]);
                dataTableDetail.showColumns([{field:'finprodMarketId',visible: true}]);
                dataTableDetail.showColumns([{field:'finprodName',visible: true}]);
                dataTableDetail.showColumns([{field:'prinStr',visible: false}]);
                dataTableDetail.showColumns([{field:'mTradeAmt',visible: false}]);
                dataTableDetail.showColumns([{field:'pledgeIdAbbr',visible: false}]);
                dataTableDetail.showColumns([{field:'pledgeName',visible: false}]);
                dataTableDetail.showColumns([{field:'pledgeNumberStr',visible: false}]);
                dataTableDetail.showColumns([{field:'bPledgeIdAbbr',visible: false}]);
            }else{
                dataTableDetail.showColumns([{field:'settleNumStr',visible: false}]);
                dataTableDetail.showColumns([{field:'settleAmt',visible: false}]);
                dataTableDetail.showColumns([{field:'finprodMarketId',visible: false}]);
                dataTableDetail.showColumns([{field:'finprodName',visible: false}]);
                dataTableDetail.showColumns([{field:'prinStr',visible: true}]);
                dataTableDetail.showColumns([{field:'mTradeAmt',visible: true}]);
                dataTableDetail.showColumns([{field:'pledgeIdAbbr',visible: true}]);
                dataTableDetail.showColumns([{field:'pledgeName',visible: true}]);
                dataTableDetail.showColumns([{field:'pledgeNumberStr',visible: true}]);
                dataTableDetail.showColumns([{field:'bPledgeIdAbbr',visible: true}]);
            }
            // GNS-940:根据资产类型控制详情列展示 modify by fuyongsheng 20220223 end
        },
        rowno:true,
        cols:[
            {field:'settleSeqNo',title: '待交割序号', width:'150', visible:false}
            ,{field:'settleStatus', visible:false}
            ,{field:'settleStatusStr', title: '交收状态', width:'100',sortField:'settleStatus'}
            ,{field:'entrustNo', title: '指令序号', width:'200'}
            ,{field:'orderId', title: '指令序号', width:'150', visible:false}
            // GNS-940:原业务编号场内交易为成交编号 modify by fuyongsheng 20220222 start
            // ,{field:'tradeId', title: '原业务编号', width:'150'}
            ,{field:'dealNo', title: '原业务编号', width:'150'}
            // GNS-940:原业务编号场内交易为成交编号 modify by fuyongsheng 20220222 end
            ,{field:'tradeDate', title: '交易日期', width:'150', type:'date'}
            ,{field:'settleDate', title: '交收日期', width:'150', type:'date'}
            ,{field:'portfolioId', title: '组合代码', width:'150'}
            ,{field:'portfolioName', title: '组合名称', width:'150'}
            ,{field:'acctNo', title: '资金账户', width:'150'}
            ,{field:'secAcctId', title: '证券账户', width:'150'}
            ,{field:'tradeMarket', visible:false}
            ,{field:'tradeMarketStr', title: '交易市场', width:'150',sortField:'tradeMarket'}
            ,{field:'tradePlat', visible:false}
            ,{field:'tradePlatStr', title: '交易平台', width:'150',sortField:'tradePlat', visible:false}
            ,{field:'finprodId',visible:false}
            ,{field:'finprodMarketId', title: 'EtrTransactionDetail001.finprodId', width:'150'}
            ,{field:'finprodName', title: 'EtrTransactionDetail001.finprodName', width:'150'}
            // GNS-940:修改字段展示名称 modify by fuyongsheng 20220222 start
            // ,{field:'settleNum',title: '交割数量', width:'150', type:'money'}
            // ,{field:'settleAmt', title: '交割金额', width:'150', type:'money'}
            ,{field:'settleNum',title: '交收数量', width:'150', type:'money', visible:false}
            ,{field:'settleNumStr',title: '交收数量', width:'150', type:'money'}
            ,{field:'settleAmt', title: '交收金额', width:'150', type:'money'}
            // GNS-940:修改字段展示名称 modify by fuyongsheng 20220222 end
            ,{field:'prin', title: '回购金额', width:'150', type:'money', visible:false}
            ,{field:'prinStr', title: '回购金额', width:'150', type:'money'}
            ,{field:'mTradeAmt', title: '到期结算金额', width:'150', type:'money'}
            // ,{field:'pledgeId', title: '质押券代码', width:'150'}
            ,{field:'pledgeId', title: '质押券代码', width:'150', visible:false}
            ,{field:'pledgeIdAbbr', title: '质押券代码', width:'150'}
            ,{field:'pledgeName', title: '质押券名称', width:'150'}
            ,{field:'pledgeNumber', title: '质押券数量', width:'150', visible:false}
            ,{field:'pledgeNumberStr', title: '质押券数量', width:'150'}
            ,{field:'bPledgeId', title: '原质押券代码', width:'150', visible:false}
            ,{field:'bPledgeIdAbbr', title: '原质押券代码', width:'150'}
            ,{field:'busiType', title: '业务类别', width:'150', visible:false}
            ,{field:'busiTypeStr', title: '业务类别', width:'150',sortField:'busiType'}
            ,{field:'counterId', title: '交易对手', width:'150', visible:false}
            ,{field:'counterName', title: '交易对手', width:'150'}
            ,{field:'dealNo',visible:false}
        ],
        done: function(res){
            $('#dataTable').next('.layui-table-view').find('.layui-table-header').eq(1).find('th[data-field="1"]').find('span').html("序号");
            if(dataTableDetail&&dataTableDetail.table){
                dataTableDetail.table?dataTableDetail.clear():'';
            }
        }
    });

    /**
     * 定义成交明细数据表格
     */
    var dataTableDetail = $("#dataTableDetail").joyintable({
        ajax: {
            url: 'queryForFlowPage',
            type: 'POST',
            data: function() {
                return {settleSeqNo:settleSeqNo};
            }
        },
        loadonce:true,
        checkbox:false,
        rowno:true,
        cols:[
            {field:'seqNo', title: '交收流水序号', width:'150', visible:false}
            ,{field:'settleDateStr', title: '交收日期', width:'150', type:'date'}
            ,{field:'settleTimeStr', title: '交收时间', width:'200'}
            ,{field:'finprodMarketId', title:'资产代码', width:'150'}
            ,{field:'finprodName', title:'资产名称', width:'150'}
            ,{field:'settleNum',title: '交收数量', width:'150', type:'money', visible:false}
            ,{field:'settleNumStr',title: '交收数量', width:'150', type:'money'}
            ,{field:'settleAmt',title:'交收金额', width:'150', type:'money'}
            ,{field:'prin', title: '回购金额', width:'150', type:'money', visible:false}
            ,{field:'prinStr', title: '回购金额', width:'150', type:'money'}
            ,{field:'mTradeAmt', title: '到期结算金额', width:'150', type:'money'}
            ,{field:'pledgeId', title: '质押券代码', width:'150', visible:false}
            ,{field:'pledgeIdAbbr', title: '质押券代码', width:'150'}
            ,{field:'pledgeName', title: '质押券名称', width:'150'}
            ,{field:'pledgeNumber', title: '质押券数量', width:'150', visible:false}
            ,{field:'pledgeNumberStr', title: '质押券数量', width:'150'}
            ,{field:'bPledgeId', title: '原质押券代码', width:'150', visible:false}
            ,{field:'bPledgeIdAbbr', title: '原质押券代码', width:'150'}
            ,{field:'operType', title:'操作类型', width:'200', visible:false}
            ,{field:'operTypeStr', title:'操作类型', width:'200'}
            ,{field:'operator',title:'操作员', width:'150'}
        ],
        done: function(res){
            $('#dataTableDetail').next('.layui-table-view').find('.layui-table-header').eq(1).find('th[data-field="0"]').find('span').html("序号");
        }
    });
    /**
     * 导出
     */
    var exportExcel = function(){
        //勾选导出
        var paramObj = searchform();
        Joyin.exportExcel(basePath + "invest/trade/manualsettle/ManualSettle001/export", paramObj);
    };

    var myrefresh = function(){
        table.table ? table.reload() : table.load();
    }


    /**
     * 操作
     */
    var settle = function(item){
        //存在异常数据
        var exceptionArr = [];
        if(table!=null && table!=''){
            var selArr = table.getAllCheckedRows();
        }
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }
        Joyin.ajax(basePath + 'invest/trade/manualsettle/ManualSettle001/getIPlatformDate', {}, function(data){
            // ZGTFCP-1461:交收、交收回溯、交收失败操作的交收日期必须等于平台日,否则调新头寸引擎会出错 modify by fuyongsheng 20220323 start
            if(item=="01"||item=="02"||item=="04"){
                var errorNum = 0;
                for(var i=0;i<selArr.length;i++){
                    if(Date.ofDate(selArr[i].settleDate).Format('yyyy-MM-dd') != data){
                        errorNum ++;
                    }
                }
                if(errorNum>0){
                    Joyin.error("交收日期须等于平台日");
                    return;
                }
            }
            // ZGTFCP-1461:交收、交收回溯、交收失败操作的交收日期必须等于平台日,否则调新头寸引擎会出错 modify by fuyongsheng 20220323 end
            for(var i=0;i<selArr.length;i++){
                if((item=="01"||item=="03"||item=="04")&&(selArr[i].settleStatus==DELIVERY_STATUS_02||selArr[i].settleStatus==DELIVERY_STATUS_04)){
                    exceptionArr.push(selArr[i]);
                }
                else if(item=="02"&&selArr[i].settleStatus!=DELIVERY_STATUS_02){
                    exceptionArr.push(selArr[i]);
                }
            }
            for(var i=0;i<selArr.length;i++){
                selArr[i].pageModel = item;
            }
            var object = {selectedItem: selArr, pageModel:item};
            var confirmMessage;
            if(exceptionArr.length>0) {
                if (item == "01") {
                    confirmMessage = JoyinUtil.getMessage("MANUAL_SETTLE_001");
                }
                if (item == "02") {
                    confirmMessage = JoyinUtil.getMessage("MANUAL_SETTLE_002");
                }
                if (item == "03") {
                    confirmMessage = JoyinUtil.getMessage("MANUAL_SETTLE_003");
                }
                if (item == "04") {
                    confirmMessage = JoyinUtil.getMessage("MANUAL_SETTLE_004");
                }
                //是:不操作  否:继续操作
                Joyin.confirm(confirmMessage, function () {
                    cancelCheck(exceptionArr);
                });
            }else{
                if(item == "04") {
                    Joyin.confirm(JoyinUtil.getMessage("MANUAL_SETTLE_005"), function () {
                        confirmMessage = JoyinUtil.getMessage("COMMON_C_0001","提交");
                        //正常提交
                        Joyin.confirm(confirmMessage,function(){
                            finalSubmit(object);
                        });
                    });
                } else {
                    confirmMessage = JoyinUtil.getMessage("COMMON_C_0001","提交");
                    //正常提交
                    Joyin.confirm(confirmMessage,function(){
                        finalSubmit(object);
                    });
                }
            }
        });
    }

    /**
     * 最终提交
     * @param object
     */
    var finalSubmit = function(object){
        WorkFlow.doDeleteList(basePath + 'invest/trade/manualsettle/ManualSettle001/settle', object, "ManualSettle001_btn_settle", "ManualSettle001", function(){
            table.reload();//回调函数
            // ZGTFCP-1345:禁止按钮置灰 modify by fuyongsheng 20220319 start
            JoyinUtil.disableButton("ManualSettle001_btn_settle", false);
            JoyinUtil.disableButton("ManualSettle001_btn_back", false);
            JoyinUtil.disableButton("ManualSettle001_btn_settledelay", false);
            JoyinUtil.disableButton("ManualSettle001_btn_settlefail", false);
            // ZGTFCP-1345:禁止按钮置灰 modify by fuyongsheng 20220319 end
        });
    }

    /**
     * 取消选中
     */
    var cancelCheck = function(exceptionArr){
        var tr = $('#dataTable').next('.layui-table-view').find('.layui-table-main').find('tr.table-row-select');
        for(var i=0;i<tr.length;i++){
            var index = tr.eq(i).data('index');
            var td = tr.eq(i).find('td[data-field="settleSeqNo"]').find('div').html();
            for(var j=0;j<exceptionArr.length;j++){
                if(td==exceptionArr[j].settleSeqNo){
                    var fixedCheckBox = $('#dataTable').next('.layui-table-view').find('.layui-table-fixed').find('tr[data-index="' + index + '"]').find(':checkbox[name="layTableCheckbox"]');
                    fixedCheckBox.next().trigger('click');
                    break;
                }
            }
        }
        //table.uncheck();
    }
    /**
     * 编辑按钮事件
     */
    $(function () {
        if(pageModel == PAGE_MODEL_NEW && isQuickFlag ){
            table.load();
        }


        /**
         * 筛选-隐藏显示查询条件
         */
        $('#ManualSettle001_btn_filter').click(function(){
            if($(".condition-area").css("display")=="none"){
                $(".condition-area").show();
            }else{
                $(".condition-area").hide();
            }
        });

        /**
         * 查询按钮
         */
        $('#ManualSettle001_btn_search').click(function() {
            isQuickFlag = false;
            table.load();
        });
        /**
         * 导出按钮
         */
        $('#ManualSettle001_btn_export').click(function() {
            exportExcel();
        });
        /**
         * 交收
         */
        $('#ManualSettle001_btn_settle').click(function () {
            settle("01");
        })
        /**
         * 回溯
         */
        $('#ManualSettle001_btn_back').click(function () {
            settle("02");
        })
        /**
         * 交收延期
         */
        $('#ManualSettle001_btn_settledelay').click(function () {
            settle("03");
        })
        /**
         * 交收失败
         */
        $('#ManualSettle001_btn_settlefail').click(function () {
            settle("04");
        })
        $('#settleStatus_div').find('input[name="settleStatus"]').click(function () {
            settleStatus = $(this).val();
            isQuickFlag = true;
            table.table == null ? table.load() : table.reload();
        })

        /**
         * 刷新列表
         */


        layui.form.on('radio(settleStatus)',function(data){
            myrefresh();
        })
    });
}());