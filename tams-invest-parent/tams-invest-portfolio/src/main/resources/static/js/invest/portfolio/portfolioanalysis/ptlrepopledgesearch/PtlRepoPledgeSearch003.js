/**
 * PtlRepoPledgeSearch002 质押券置换明细查询
 */
$(function() {
    /**
     * 质押券置换明细查询参数
     */
    var getSearchParams = function() {
        var tradeId = $('#tradeId').val();
        return {
            tradeId : tradeId
        }
    };

    /**
     * 定义质押券置换明细数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: basePath +"invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch001/getPledgeDetail",
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        cols:[
            {field:'seqNo',width:150,title: '流水号',sortField:'seq_No'}
            ,{field:'tradeId',width:150,title: '交易编号',sortField:'trade_id'}
            ,{field:'finprodId',width:150,title: '金融产品代码',sortField:'finprod_id'}
            ,{field:'branch',width:150,title: '分支编号'}
            ,{field:'tradeTypeName',width:150,title: '置换类型',sortField:'trade_type'}
            ,{field:'pledgeDisplaceAmt',width:150,title: '质押券面额(元)',sortField:'pledge_displace_amt', type: 'money'}
            ,{field:'scale',width:150,title: '折算比例%'}
            ,{field:'firstNetprice',width:150,title: '首次净价',sortField:'first_netprice'}

        ],
        searchOnInit: true,
        checkbox: true
    });
}());