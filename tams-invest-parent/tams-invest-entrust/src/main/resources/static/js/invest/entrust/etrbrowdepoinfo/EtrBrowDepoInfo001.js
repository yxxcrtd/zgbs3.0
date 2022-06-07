/**
 * 存款委托
 */
(function(){
    /**
     * 获取查询参数
     */
    var searchform =function(){
        var finprodType2;
        if($("#finprodType2").val() == null || $("#finprodType2").val() == ''){
            finprodType2 = $("#finprodType2").val();
        }
        else{
            finprodType2 = $("#finprodType2").val().join(',');
        }
        var portfolioId;
        if($("#portfolioId").val() == null || $("#portfolioId").val() == ''){
            portfolioId = $("#portfolioId").val();
        }
        else{
            portfolioId = $("#portfolioId").val().join(',');
        }
        //	查询条件换成资产二级分类多选项 -wangguiqi on 2019/8/6 -start
        var param = $.serializeToObject('#searchForm');
        if(param.type9){
            param.type9 = param.type9.join(",");
        }
        if(param.type10){
            param.type10 = param.type10.join(",");
        }
        if(param.type11){
            param.type11 = param.type11.join(",");
        }
        //	查询条件换成资产二级分类多选项 -wangguiqi on 2019/8/6 -end
        return {
            /* type9~11 added by wangguiqi on 2019/8/1 -start */
            type9: param.type9,
            type10: param.type10,
            type11: param.type11,
            /* type9~11 added by wangguiqi on 2019/8/1 -end */
            portfolioId:portfolioId,
            finprodType2:finprodType2,
            entrustStatus:$("#entrustStatus").val(),
            ccy:$("#ccy").val(),
            finprodId:$("#finprodId").val(),
            ps:$("#ps").val(),
            startDate:$("#entrustDate").startDate(),
            endDate:$("#entrustDate").endDate()
        };
    };

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
            data.pageModel=PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("EtrBrowDepoInfo002.detail"),
                JoyinUtil.getRealPath("invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo002/init"),
                "98%", "95%", data,
                function(){table.reload();});
        },
        cols:[
            {field:'entrustStatusStr', title: 'EtrBrowDepoInfo001.entrustStatus', width:'10%',sortField:"entrustStatus"}
            ,{field:'invManagerId', title: 'EtrBrowDepoInfo001.invManagerId', width:'10%',sortField:"invManagerId"}
//            ,{field:'entrustId', title: 'EtrBrowDepoInfo001.entrustId', width:180}
            ,{field:'portfolioId',
                title: 'EtrBrowDepoInfo001.portfolioId',
                render : function(data, type, row, meta) {
                    return "<a  class='pro-link portfolioIdLink' style='cursor: pointer;'>"+row.portfolioId+"</ a>";
                },
                width:'10%',sortField:"portfolioId"
            }
            ,{field:'portfolioName', title: 'EtrBrowDepoInfo001.portfolioName', width:'15%',sortField:"portfolioName"}
            ,{field:'finprodType2Str', title: 'EtrBrowDepoInfo001.finprodType2', width:'10%',sortField:"finprodType2", visible:false}//隐藏原资产类型
            //新增资产二、三、四级分类	王贵奇	2019/8/7	开始
            ,{field:'assetType1Str', title: 'EtrInvestBondInfo001.type9', width:'10%',sortField:"assetType1"}
            ,{field:'assetType10Str', title: 'EtrInvestBondInfo001.type10', width:'10%',sortField:"assetType10"}
            ,{field:'assetType11Str', title: 'EtrInvestBondInfo001.type11', width:'10%',sortField:"assetType11", visible:IS_SHOW_TYPE == '01'?true:false}
            //新增资产二、三、四级分类	王贵奇	2019/8/7	结束
            ,{field:'finprodMarketId',
                title: 'EtrBrowDepoInfo001.finprodId',
                render : function(data, type, row, meta) {
                    return "<a  class='pro-link finprodIdLink' style='cursor: pointer;'>"+row.finprodMarketId+"</ a>";
                },
                width:'15%',sortField:"finprodMarketId"
            }
            ,{field:'finprodId', visible:false}
            // add by zhangrui1 start
            ,{field:'counterId', visible:false}
            // add by zhangrui1 end
            ,{field:'finprodName', title: 'EtrBrowDepoInfo001.finprodName', width:'15%',sortField:"finprodName"}
            ,{field:'psStr', title: 'EtrBrowDepoInfo001.ps', width:'10%',sortField:"ps"}
            ,{field:'price',type:'number', title: 'EtrBrowDepoInfo001.price', width:'10%', type:'number',sortField:"price"}
            ,{field:'payFreqStr', title: 'EtrBrowDepoInfo001.payFreq', width:'10%',sortField:"payFreq"}
            ,{field:'ccyStr', title: 'EtrBrowDepoInfo001.ccy', width:'10%',sortField:"ccy"}
            ,{field:'vdateStr', title: 'EtrBrowDepoInfo001.entrustVDate', type:'date', width:'10%',sortField:"vdate"}
            ,{field:'mdateStr', title: 'EtrBrowDepoInfo001.entrustMDate', type:'date', width:'10%',sortField:"mdate"}
            ,{field:'entrustAmtStr',type:'number', title: 'EtrBrowDepoInfo001.entrustAmt', width:'10%', type:'money',sortField:"entrustAmt"}
            ,{field:'entrustAmt', visible:false}
            ,{field:'finishAmt',type:'number', title: 'EtrBrowDepoInfo001.finishAmt',  width:'15%', type:'money',sortField:"finishAmt"}
            ,{field:'transactioncom',
                title: 'EtrBrowDepoInfo001.transactioncom',
                render : function(data, type, row, meta){
                    return "<div class='layui-progress layui-progress-big' style='margin-top:5px;'><div class='layui-progress-bar layui-bg-green' style='width:"+row.transactioncom+"%'><span class='layui-progress-text'>"+row.transactioncom+"%</span></div></div>";
                },
                width:'10%',sortable:false
            }
            ,{field:'nextUser', title: 'EtrCommon.nextUser', width:'10%' ,sortable:false}
            ,{field:'action', title: '操作' , type:'action',buttons: ['edit'] , width:'10%'}
        ],
    });
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
        //在新建界面进行显示单行的详细信息
        Joyin.dialog(JoyinUtil.getLabel("FinDepositTarget002.title"),
            basePath + "invest/asset/depo/FinDepositTarget002/init",
            "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                finprodId:row.finprodId,  //金融产品代码
                //valDate:data.valDate,//估值日
                //finprodMarketId:data.finprodMarketId//市场代码
            });
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        //data.valueDate = new Date(data.valueDate).format();
        if(data.entrustStatusStr != "已退回" && data.entrustStatusStr != "已撤回"){
            Joyin.error("请选择委托状态为：已退回、已撤回的进行修改");
            return;
        }
        data.pageModel=PAGE_MODEL_EDIT;
        data.operateType='edit'
        //Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrLendSpecInfo002/check', data, function(result) {
        Joyin.dialog(JoyinUtil.getLabel("EtrBrowDepoInfo002.edit"),
            basePath + "invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo002/init",
            "98%", "95%",data,
            function(){table.reload();});
        //});

    });
    /**
     * 绑定删除事件
     */
    /*table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("BUSS_COM_C_0024",JoyinUtil.getLabel("SysCommon.delete")), function(){
            	Joyin.ajaxJson(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo001/delete', object, function(result){
            		table.reload();
            		
            	});
            });
        });	//画面提示message【BUSS_COM_C_0024：请确认是否要删除?】																	
    });*/

    /**
     * 导出
     */
    var exportExcel = function(){
        Joyin.exportExcel(basePath+"invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo001/export", searchform());
    };

    /**
     * 新建
     */
    function showDialog() {
        Joyin.dialog(JoyinUtil.getLabel('EtrBrowDepoInfo002'), basePath +'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo002/init', '98%', '95%');
    }

    /**
     * 编辑按钮事件
     */
    $(function () {

        /**
         * 查询按钮
         */
        $('#EtrBrowDepoInfo001_btn_search').click(function() {
            table.load();
        });

        /**
         * 新建
         */
        $('#EtrBrowDepoInfo001_btn_add').click(function(){
            showDialog();
            table.reload()
        });

        /**
         * 导出按钮
         */
        $('#EtrBrowDepoInfo001_btn_export').click(function() {
            exportExcel();
        });
        //add by gaopeng 20191231 导入按钮点击事件 start
        $('#EtrBrowDepoInfo001_btn_import').click(function() {
            Joyin.uploadFile({pageId : 'EtrBrowDepoInfo003MTM'}, function(fileName) {
                Joyin.ajax(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo003/checkImportData', {fileName: fileName}, function(checkResult) {
                    setTimeout( function () {
                        if(checkResult.data.errorMsg){
                            Joyin.error(checkResult.data.errorMsg);
                            return;
                        }
                        Joyin.confirm(checkResult.data.confirmMsg,function() {
                            var result = {};
                            result.tempList = checkResult.data.correctData;
                            Joyin.dialog(JoyinUtil.getLabel("EtrBrowDepoInfo003"),
                                JoyinUtil.getRealPath("/invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo003/init"),
                                "98%", "95%", result,
                                function(){table.reload();});
                        });
                    },1000);
                });
            },function() {
                table.reload();
            });
        });
        //add by gaopeng 20191231 导入按钮点击事件 end

        /**
         * 关闭委托
         */
        $('#EtrBrowDepoInfo001_btn_close').click(function(){
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            /*$.each(selArr,function(index,obj){
                if(obj.entrustStatusStr == '待接收'){
                    Joyin.error('不能对待接收的数据进行关闭操作');
                    return;
                }
                if(obj.entrustStatusStr == '已关闭'){
                    Joyin.error('已关闭状态的数据不允许再次关闭');
                    return;
                }
            })*/
            for(var i=0; i<selArr.length; i++){
                if(selArr[i].entrustStatusStr == '待接收'){
                    Joyin.error('不能对待接收的数据进行关闭操作');
                    return;
                }
                if(selArr[i].entrustStatusStr == '已关闭'){
                    Joyin.error('已关闭状态的数据不允许再次关闭');
                    return;
                }
                if(selArr[i].entrustStatusStr == '流程中'){
                    Joyin.error('不能对流程中的数据进行关闭操作');
                    return;
                }
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }//	画面提示message【ETR_C_0002：请确认是否要关闭委托？】
            var object = {selectedItem: selArr, operateType:"close"};
            Joyin.ajaxJson(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("ETR_C_0002",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo001/close', object, function(result){
                        table.reload();
                    });
                });
            });
        })

        /**
         * 撤销委托
         */
        $('#EtrBrowDepoInfo001_btn_revoke').click(function(){
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }//BUSS_COM_C_0032
            for(var index in selArr){
                if(selArr[index].entrustStatus == '08'){
                    Joyin.error("流程中的数据不允许撤回");
                    return ;
                }
            }
            var object = {selectedItem: selArr, operateType:"recall"};
            Joyin.ajaxJson(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("BUSS_COM_C_0032","撤销"), function(){
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo001/recall', object, function(result){

                        table.reload();
                    });
                });
            });
        })
        //是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
            $('#type11Div').show();
        }else{
            $('#type11Div').hide();
        }
    });
}());