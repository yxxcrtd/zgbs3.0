$(function () {
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        var result = $.serializeToObject('#submitForm');
        if($("#ptlId").val() == null || $("#ptlId").val() == ''){
            result.ptlId = $("#ptlId").val();
        }
        else{
            result.ptlId = $("#ptlId").val().join(',');
        }
        return result;
    };
    var dataTable=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getPageParams();
            }
        },
        checkbox:false,
        lazyload:true,
        searchOnInit: false,
        isMulti: true,
        cols: [[
            {field:'ptlId', title: '资管计划id',visible:false,sortable: false}//组合名称
            // ,{field:'valDate', title: '查询日期',visible:true,rowspan: 2,sortable: false,type: 'date'}//查询日期
            ,{field:'portfolioName', title: '资管计划名称',width:300, rowspan: 2,sortable: false}//组合名称
            ,{title: '总资产', colspan: 2,align:'center',sortable: false}//总资产
            ,{title: '净资产', colspan: 2,align:'center',sortable: false}//总资产
            ,{title: '单位净值', colspan: 2,align:'center',sortable: false}//总资产
            ,{title: '净值波动%', colspan: 3,align:'center',sortable: false}
            ,{field:'deviatingAmount',rowspan: 2, title: '偏离金额',sortable: false,type:'number'}
            ,{field:'degreeOfDeviate',rowspan: 2, title: '偏离度',sortable: false,type:'number'}
            ,{field:'operation', title: 'SysCommon.operation', type:'action',rowspan: 2,
                render:function (data, type, row, meta) {
                        return '<i class="btn iconfont config" title="查看">&#xe6ac;</i>';
                }
            }
        ],[
            {field:'totalAssestCost', title: '成本',sortable: false,type:'number'}
            ,{field:'totalAssestValue', title: '市值',sortable: false,type:'number'}
            ,{field:'netAssestCost', title: '成本',sortable: false,type:'number'}
            ,{field:'netAssestValue', title: '市值',sortable: false,type:'number'}
            ,{field:'unitNetWorthCost', title: '成本',sortable: false,type:'number'}
            ,{field:'unitNetWorthValue', title: '市值',sortable: false,type:'number'}
            ,{field:'comparedWithDay', title: '较上一日',sortable: false,type:'number'}
            ,{field:'comparedWithMonth', title: '较上月',sortable: false,type:'number'}
            ,{field:'comparedWithYear', title: '较年初',sortable: false,type:'number'}
        ]],
        done:function(){
            $(".laytable-cell-group").css({'text-align':'center'})
        }
    });

    /**
     * 明细查看
     */
    dataTable.bind('click', '.config', function(data, index) {
        var param = {};
        param.ptlId = data.ptlId;
        param.valDate = data.valDate;
        Joyin.dialog(JoyinUtil.getLabel("估值表明细"),
            basePath + "invest/risk/valtblimport/RskValTableQuery001/detailedInit",//打开新界面
            "95%", "70%", {paramJsonStr:JSON.stringify(param)
            },function(){
                dataTable.reload();
            });
    });

    /**
     * 导出
     */
    var exportExcel = function() {
        var paramObj = getPageParams();//获取参数
        if(!paramObj.valDate){
            Joyin.alert("请选择查询日期！",7);
            return;
        }
        Joyin.exportExcel('export', paramObj);//导出
    };
    $(function () {
        $("#RskValTableQuery001_btn_search").click(function () {
            var paramObj = getPageParams();//获取参数
            if(!paramObj.valDate){
                Joyin.alert("请选择查询日期！",7);
                return;
            }
            dataTable.load();
        });
        //绑定到处按钮
        $('#RskValTableQuery001_btn_export').click(function() {
            exportExcel();//导出
        });
    })
});
