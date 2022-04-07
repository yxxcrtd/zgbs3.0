(function () {
    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
    	var taskGroupData = $("#taskGroup").data('selected');
    	var taskData = $('#task').data('selected');
    	var prdGroupData = $('#prdGroup').data('selected');
        return {
            taskGroup: taskGroupData ? taskGroupData.code : '',
            task: taskData ? taskData.code : '',
            prdGroup: prdGroupData ? prdGroupData.code : '',
            startDate: $('#businessDate').startDate(),
            endDate: $('#businessDate').endDate(),
            taskStatus: $('#taskStatus').val(),
            isWarn: $('#isWarn').next().hasClass('layui-form-onswitch') ? 'true' : 'false' 
        }
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
        progressBar: true,
        dblclick: function (table, data) {
            var index = Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                JoyinUtil.getRealPath("/valuation/system/log/ValBatchLog001/ValBatchLog003"),
                "98%", "95%", {
                    pageModel: PAGE_MODEL_REFERENCE,
                    logSeqNo: data.seqNo
                }, function () {
                }, {
                    "关闭": function () {
                        layer.close(index);
                    }
                });
        },
        cols: [
            {field: 'taskId', visible: false}
            , {field: 'taskGroupId', visible: false}
            , {field: 'prdGroupId', visible: false}
            , {field: 'seqNo', visible: false}
            , {field: 'batchExedetailSeqNo', visible: false}
            , {field: 'taskStatusString', visible: false}
            , {field: 'taskName', width: 200, title: 'ValBatchLog001.taskName'}
            , {field: 'taskGroupName', width: 200, title: 'ValBatchLog001.taskGroupName'}
            , {field: 'prdGroupName', width: 200, title: 'ValBatchLog001.prdGroupName'}
            , {field: 'businessStartDate', width: 150, title: 'ValBatchLog001.businessStartDate'}
            , {field: 'businessEndDate', width: 150, title: 'ValBatchLog001.businessEndDate'}
            , {field: 'batchDate', width: 150, title: 'ValBatchLog001.batchDate'}
            , {field: 'batchStartTime', width: 200, title: 'ValBatchLog001.batchStartTime'}
            , {field: 'batchEndTime', width: 200, title: 'ValBatchLog001.batchEndTime', render: function (data, type, row, meta) {
                if (row.taskStatusString == 'EXP' || row.taskStatusString == 'END' || row.taskStatusString == 'BRK') {
                    return data;
                }
            }}
            , {
                field: 'processTime', width: 200, title: '耗时(S)', render: function (data, type, row, meta) {
                    return data*1/1000;
                }
            }
            , {field: 'taskStatus', width: 150,sortField:'taskStatusString', title: 'ValBatchLog001.taskStatus'}
            , {field: 'startMode', width: 150, title: 'ValBatchLog001.startMode'}
            , {field: 'createUser', width: 150, title: 'ValBatchLog001.createUser'}
            ,{field: 'isWarn', title: 'ValBatchLog001.isWarn',width:100, sortable:false,render: function (data, type, row, meta) {
                    if(row.isWarn=='警告'){
                      var html = '<a href="javascript:void(0);" class="isWarn" data-seqno="' + row.batchExedetailSeqNo + '" data-taskid="' + row.taskId + '" >' + data + '</a>';
                      return html;
                      
                      }
                      else{
                          return row.isWarn;
                      }
                }}
            
        ],
        //loadonce:true,
        checkbox: false
    });
    
    var exportExcel = function () {
        Joyin.exportExcel('export', getSearchParams());
    };

    /**
     * 画面加载完成
     */
    $(function () {
        // 绑定按钮事件
        $('#ValBatchLog001_btn_search').click(function () {
            table.load();
        });

        $('#ValBatchLog001_btn_export').click(function () {
            exportExcel();
        });
        
        $("body").on("click", ".isWarn", function () {
            var batchExedetailSeqNo = $(this).data("seqno");
            var taskId = $(this).data("taskid");
            var index = Joyin.dialog("警告信息详细 ", "../ValBatchLog002/init", "85%", "90%", {batchExedetailSeqNo: batchExedetailSeqNo,taskId:taskId}, function () {
            })
        })
        
        
    });
}());