/**
 * 债券投资委托查询
 */
(function(){
    /**
     * 获取查询参数
     */
    var searchform =function(){
        var object = {
            portfolioId: $('#portfolioId').val()
            ,type9: $('#type9').val()
            ,type10: $('#type10').val()
            ,type11: $('#type11').val()
            ,ccy: $('#ccy').val()
            ,finprodId: $('#finprodId').val()
            ,tradeMarket: $('#tradeMarket').val()
            ,tradeMarketMode: $('#tradeMarketMode').val()
            ,tradePlat: $('#tradePlat').val()
            ,ps: $('#ps').val()
            ,entrustStatus: $('#entrustStatus').val()
            ,startDate: $('#startDate').val()
            ,endDate: $('#endDate').val()
            ,priceModel: $('#priceModel').val()
        };
//        object.finprodId = $('#finprodId_hidden').val();
//        if(object.bondType){
//            object.bondType = object.bondType.join(',');
//        }
        //	新增债券二、三、四级分类	2019/7/26	王贵奇	开始
        if(object.type9){
            object.type9 = object.type9.join(',');
        }
        if(object.type10){
            object.type10 = object.type10.join(',');
        }
        if(object.type11){
            object.type11 = object.type11.join(',');
        }
        if(object.type12){
            object.type12 = object.type12.join(',');
        }
        //	新增债券二、三、四级分类	2019/7/26	王贵奇	结束
        if(object.portfolioId){
            object.portfolioId = object.portfolioId.join(',');
        }
        return object;
    };
    /**
     * 多组合指令
     */
    function showCmdDialog() {
        Joyin.dialog(JoyinUtil.getLabel('EtrInvestBondInfo003'), basePath +'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo003/init?parentPageId=' + pageId, '98%', '95%',function(){table.reload();});
    }
    // 导入按钮点击事件
    $('#'+pageId+'_btn_import').click(function() {
        Joyin.uploadFile({pageId : 'EtrInvestBondInfo004MTM'}, function(fileName) {
            Joyin.ajax(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo004/checkImportData', {fileName: fileName}, function(checkResult) {
                if (checkResult) {
                    setTimeout( function () {
                        if(checkResult.data.errorMsg){
                            Joyin.error(checkResult.data.errorMsg);
                            return;
                        }
                        Joyin.confirm(checkResult.data.confirmMsg,function() {
                            var result = {};
                            result.tempList = checkResult.data.correctData;
                            Joyin.dialog(JoyinUtil.getLabel("EtrInvestBondInfo004"),
                                JoyinUtil.getRealPath("/invest/entrust/etrinvestbondinfo/EtrInvestBondInfo004/init"),
                                "98%", "95%", result,
                                function(){table.reload();});
                        });
                    },1000);
                }
            });
        },function() {
            table.reload();
        });
    });

    /**
     * 新建
     */
    function showDialog() {
        Joyin.dialog(JoyinUtil.getLabel('EtrInvestBondInfo002'), basePath +'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/init', '98%', '95%',{parentPageId:pageId},function(){table.reload();});
    }
    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return searchform();
            }
        },
        dblclick:function(table, data) {
            data.operateType = "dblclick";
            data.pageModel=PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestBondInfo002.refrence"),
                    JoyinUtil.getRealPath("invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/init"),
                    "98%", "95%", data,
                    function(){table.reload();});
        },
        cols:[
            {field:'entrustStatus',visible:false}
            ,{field:'busEntrustStatus', title: 'EtrInvestBondInfo001.entrustStatus', width:'10%',sortField:"entrustStatus"}
            ,{field:'entrustId',visible:false}
            ,{field:'invManagerId', title: 'EtrInvestBondInfo001.invManagerId', width:'10%',sortField:"invManagerId"}
            ,{field:'portfolioId', title: 'EtrInvestBondInfo001.portfolioId', width:'10%',
                render : function(data, type, row, meta) {
                    return "<a  class='pro-link portfolioIdLink' style='cursor: pointer;'>"+row.portfolioId+"</ a>";
                },sortField:"portfolioId"
            }
            ,{field:'portfolioName', title: 'EtrInvestBondInfo001.portfolioName', width:'10%',sortField:"portfolioName"}
            ,{field:'finprodId', visible:false}
            ,{field:'finprodMarketId', title: 'EtrInvestBondInfo001.finprodId', width:'10%',
                render : function(data, type, row, meta) {
                    return "<a  class='pro-link finprodIdLink' style='cursor: pointer;'>"+data+"</ a>";
                },sortField:"finprodMarketId"
            }
            ,{field:'finprodAbbr', title: 'EtrInvestBondInfo001.finprodAbbr', width:'10%',sortField:"finprodAbbr"}
            // 新增资产二、三、四级分类 王贵奇2019/8/2 开始
            ,{field:'assetType1Name', title: 'EtrInvestBondInfo001.type9', width:'10%',sortField:"assetType1"}
            ,{field:'assetType10Name', title: 'EtrInvestBondInfo001.type10', width:'10%',sortField:"assetType10"}
            ,{field:'assetType11Name', title: 'EtrInvestBondInfo001.type11', width:'10%',sortField:"assetType11",visible:IS_SHOW_TYPE == '01'?true:false}
            // 新增资产二、三、四级分类 王贵奇2019/8/2 结束
            ,{field:'ps', visible:false}
            ,{field:'busPs', title: 'EtrInvestBondInfo001.ps', width:'10%',sortField:"ps"}
            ,{field:'tradeMarket', visible:false}
            ,{field:'busTradeMarket', title: 'EtrInvestBondInfo001.tradeMarket', width:'10%',sortField:"tradeMarket"}
            ,{field:'tradePlat',visible:false}
            ,{field:'busTradePlat', title: 'EtrInvestBondInfo001.tradePlat', width:'10%',sortField:"tradePlat"}
            ,{field:'entrustNum',type:'number', title: 'EtrInvestBondInfo001.entrustAmt1', width:'10%', digit:4 , sortField:"entrustNum"}
            ,{field:'entrustOwnNum',type:'number', title: 'EtrInvestBondInfo002.entrustOwnNum1', width:'10%', digit:4 , sortField:"entrustOwnNum"}
            ,{field:'entrustIntegrateNum',type:'number', title: 'EtrInvestBondInfo002.entrustIntegrateNum1', width:'10%', digit:4 , sortField:"entrustIntegrateNum"}
            ,{field:'ccy',visible:false}
            ,{field:'busCcy', title: 'EtrInvestBondInfo001.ccy', width:'10%',sortField:"ccy"}
            ,{field:'vdate', title: 'EtrInvestBondInfo001.vdate', width:'10%',type:'date',sortField:"vdate"}
            ,{field:'mdate', title: 'EtrInvestBondInfo001.mdate', width:'10%',type:'date',sortField:"mdate"}
            ,{field:'finishAmt',type:'number', title: 'EtrInvestBondInfo001.finishAmt', width:'10%',sortField:"finishAmt"}
            ,{field:'transactioncom', title: 'EtrInvestBondInfo001.transactioncom',render : function(data, type, row, meta){
                return "<div class='layui-progress layui-progress-big' style='margin-top:5px;'><div class='layui-progress-bar layui-bg-green' style='width:"+row.transactioncom+"%'><span class='layui-progress-text'>"+row.transactioncom+"%</span></div></div>";
            }, width:'10%',sortable:false}
            ,{field:'priceModelStr', title: 'EtrInvestBondInfo001.priceModel', width:'10%',sortField:"priceModel"}//新增价格模式 王贵奇 2019/10/10
            ,{field:'remark', title: 'EtrInvestBondInfo001.remark', width:'10%',sortable:false}
            ,{field:'invAim', visible:false}
            ,{field:'settleSpreed', visible:false}
            ,{field:'assetType1', visible:false}
            ,{field:'finprodMarketId', visible:false}
            ,{field:'action', title: '操作' , type:'action',buttons: ['edit'] , width:'10%'}
            ],
    });
    /**
     * 导出
     */
    var exportExcel = function(){
        Joyin.exportExcel(basePath+"invest/entrust/etrinvestbondinfo/EtrInvestBondInfo001/export", searchform());
    };

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.operateType = "edit";
        data.pageModel=PAGE_MODEL_EDIT;
        data.parentPageId=pageId;
        //走check会校验dto，所以抽出一个新的editCheck方法
        Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/checkEdit', data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestBondInfo002.edit"),
                    JoyinUtil.getRealPath("invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/init"),
                    "98%", "95%", data,
               function(){table.reload();});
        });
    });
    /**
     * 绑定删除事件
     */
//    table.bind('click', '.delt', function(data, index) {
//        var object = {selectedItem: [data], operateType:"delete"};
//        Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo001/check', object, function(result) {
//            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
//               Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo001/delete', object, function() {
//                        table.reload();
//               });
//            });
//        });
//    });
    /**
     * 组合代码链接
     */
    table.bind('click','.portfolioIdLink', function(row) {
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
                "98%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                portfolioId:row.portfolioId});//主键组合代码
    });
    /**
     * 资产代码链接
     */
    table.bind('click','.finprodIdLink', function(row) {
        //在新建界面进行显示单行的详细信息                 // + "invest/asset/bond/FinBondTargetAsset002/init",
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                 basePath
                + "home.html?a=a&finprodMarketId=" + row.finprodId + "&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                                finprodId:row.finprodId,  //金融产品代码
                                //valDate:data.valDate,//估值日
                                //finprodMarketId:data.finprodMarketId//市场代码
                                });
    });
    /**
     * 编辑按钮事件
     */
    $(function () {
        /**
         * 查询按钮
         */
        $('#'+pageId+'_btn_search').click(function() {
            table.load();
            $('#EtrInvestBondInfo001_btn_close').show();
            $('#EtrInvestBondInfo001_btn_revoke').show();
        });
        /**
         * 导出按钮
         */
        $('#'+pageId+'_btn_export').click(function() {
            exportExcel();
        });
        /**
         * 多组合指令
         */
        $('#'+pageId+'_btn_cmd').click(function() {
            showCmdDialog();
        });
        /**
         * 新建
         */
        $('#'+pageId+'_btn_add').click(function() {
            showDialog();
        });
        /**
         * 关闭委托
         */
        $('#'+pageId+'_btn_close').click(function() {
            if (table != null && table != '') {
                var selArr = table.getAllCheckedRows();
             }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
             }else {
                var object = {selectedItem: selArr, operateType:"close"};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo001/check', object, function(result) {
                    Joyin.confirm(JoyinUtil.getMessage("ETR_C_0002"), function(){
                        Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo001/close', object, function(result) {
                            table.reload();
                        });
                    });
                });
             }
        });
        /**
         * 撤销
         */
        $('#'+pageId+'_btn_revoke').click(function() {
            if (table != null && table != '') {
                var selArr = table.getAllCheckedRows();
             }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
             }else {
                var object = {selectedItem: selArr, operateType:"recall"};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo001/check', object, function(result) {
                    Joyin.confirm(JoyinUtil.getMessage("BUSS_COM_C_0032","撤销"), function(){
                        Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo001/recall', object, function(result) {
                            table.reload();
                        });
                    });
                });
             }
        });
        /**
         * 交易场所切换
         */
        $('#tradeMarket').change(function(){
            var tradeMarket = $('#tradeMarket').val();
            if(tradeMarket == bankRoom){
                $('#tradePlat').val(bankPlat).trigger('change').attr('disabled',true);
            }else{
                $('#tradePlat').val('').trigger('change').attr('disabled',false);
            }
        });

      //是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
        	$('#type11Div').show();
        }else{
        	$('#type11Div').hide();
        }
    });
}());
