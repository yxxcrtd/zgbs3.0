$(function() {
	
	/**
	 * 获取查询参数
	 */
	var getPageParams = function() {
		return $.serializeToObject('#searchForm');
	};
	
	var table=$("#dataTable").joyintable({
		ajax: {
			url: 'findPage',
			type: 'POST',
			data: function() {
				return getPageParams();
			}

		},
		checkbox: false,
		isMulti:true,
		loadonce:true,
		page: false,
        cols: [[ {
        		field : 'projName',
	            sortable : false,
	            title : '',
	            width : 200,
	            rowspan: 2
	        }, {
	        	field : 'sumAmt',
	            width : 200,
	            sortable : false,
	            title : '合计',
	            rowspan: 2,
	        }, {
	            width : 200,
	            sortable : false,
	            title : '未逾期未减值',
	            rowspan: 2,
	            field: 'notOverdueAmt'
	        }, {
	            sortable : false,
	            title : '逾期',
	            colspan: 3
	        }
	        ],[
				{
				    field : 'overdueAmt12',
				    width : 300,
				    type : "money",
				    sortable : false,
				    title : '1年至2年'
				},{
				    field : 'overdueAmt23',
				    width : 300,
				    type : "money",
				    sortable : false,
				    title : '2年至3年'
				},{
				    field : 'overdueAmt3x',
				    width : 300,
				    type : "money",
				    sortable : false,
				    title : '3年以上'
				}
	        ]
        ]
    });
    
    /**
     * 画面加载完成
     */
    $(function() {
    	
        $('#CreditRiskManagement001_btn_search').click(function() {
        	Joyin.ajaxJson(basePath + 'poc/creditrisk.riskmanagement/CreditRiskManagement001/check', getPageParams(), function(result) {
	        	if (!table.table) {
					table.load();
				} else {
					table.reload();
				}
        	});
        });

    });
}());
