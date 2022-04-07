(function () {
    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
        var seqNo = $("#seqNo").val();
        return {
            seqNo: seqNo,
        }
    };

    var table = $("#dataTable").joyintable({
        ajax: {
            url: 'findDetail',
            type: 'POST',
            data: function () {
                return getSearchParams();
            }
        },
        cols: [
              {field: 'remark', title: '属性名称',width:"50%"}
            , {field: 'value',  title: '属性值',width:"50%"}
        ],
        checkbox: false
    });




    $(function () {
        $("#logInfo").html($("#logInfoHidden").text());
    })

    $(function () {
        $("#resultInfo").html($("#resultInfoHidden").text());
    })

    $(function () {
        $("#detailEx").html($("#detailExHidden").text());
    })

}());