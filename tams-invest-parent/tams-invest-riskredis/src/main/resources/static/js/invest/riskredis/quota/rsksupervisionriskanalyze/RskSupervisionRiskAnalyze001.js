/**
 * RskSupervisionRiskAnalyze001 指标参数管理
 */
var rsk = function() {
   var infFlag=false;
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var groupName = $("#groupName").val()?$("#groupName").val().join(","):'';
        var indName = $("#indName").val()?$("#indName").val().join(","):'';
        var supType = $('#supType').val();
        var supStat = $('#supStat').val();
        var starSupDate = $('#supDate').startDate();
        var endSupDate = $('#supDate').endDate();
        var infParam=infFlag?"NORMAL":null;
        var prodManager = $("#prodManager").val()?$("#prodManager").val().join(","):'';
        return {
            groupName : groupName,
            indName : indName,
            supType : supType,
            supStat : supStat,
            starSupDate : starSupDate,
            endSupDate : endSupDate,
            infParam : infParam,
            prodManager:prodManager
        }
    };
    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        cols:[
            {field:'portfolioId',visible :false}
            ,{field:'indId',visible :false}
            ,{field:'frequency',visible :false}
            ,{field:'supFrequency',visible :false}
            ,{field:'groupName', title: 'RskSupervisionRiskAnalyze001.groupNameStr',width:250}
            ,{field:'supTypeAbbr',sortField:"supType", title: 'RskSupervisionRiskAnalyze001.supType',width:100}
            ,{field:'supDate', title: 'RskSupervisionRiskAnalyze001.supDate',type:'date'}
            ,{field:'indName', title: 'RskSupervisionRiskAnalyze001.indName',width:200
                // add by zhangrui1 指标计算明细超链接 start
                ,render: function(data, type, row, meta) {
                    if (!row.mathProcess) {
                        return data;
                    }
                    return '<a href="javascript:;" onclick="javascript:showExprDetail(\''+ 
                    row.indId+'\',\'' + data +'\',\''+ mathToString(row.mathProcess) + '\')" style="cursor: pointer;">' + data + '</span></a>';
                }
                // add by zhangrui1 指标计算明细超链接 end
            }
            ,{field:'warnCond', width:150 ,title: 'RskSupervisionIndicator001.warnCond',render: RskQuotaCommon.transferWarnFoul}
            ,{field:'approveCond',width:150, title: 'RskSupervisionIndicator001.approveCond',render: RskQuotaCommon.transferWarnFoul}
             ,{field:'foulCond', width:150 ,title: 'RskSupervisionIndicator001.foulCond',render: RskQuotaCommon.transferWarnFoul}
            ,{field:'supVal', title: 'RskSupervisionRiskAnalyze001.threshol',width:150,render: RskQuotaCommon.transferSupVal}
            ,{field:'supStatAbbr',sortField:"supStat",width:100,title: 'RskSupervisionRiskAnalyze001.supStat',render:function(d,type, row, meta){
                 if(row.supStat == "ERROR"){
                 	 return   JoyinUtil.getLabel("RskSupervisionRiskAnalyze001.exception");
                  }
            	 if(d == normal){
                     if(row.mxCount==0){
                         return  '<a style="cursor: pointer;"><span style="color: green">'+d+'</span></a>';
                     }
                    else{
                         return  '<a  href="javascript:;" style="text-decoration:underline" onclick="javascript:showSupDetail(\''+ row.indId+'\',\''+row.portfolioId+'\',\''+new Date(row.supDate).format('yyyy-MM-dd')
                                        + '\',\''+ row.supFrequency+'\')" style="cursor: pointer;"><span style="color: green">'+d+'</span></a>';
                    }
                  }
                else if(d == warning){
                     if(row.mxCount==0){
                         return  '<a style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
                     }
                    else{
                        return  '<a  href="javascript:;" style="text-decoration:underline" onclick="javascript:showSupDetail(\''+ row.indId+'\',\''+row.portfolioId+'\',\''+new Date(row.supDate).format('yyyy-MM-dd')
                                            + '\',\''+ row.supFrequency+'\')" style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
                    }

 					}
				 else if(d == approve){
				            if(row.mxCount==0){
                              return  '<a style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
                          }
                         else{
                            return  '<a  href="javascript:;" style="text-decoration:underline" onclick="javascript:showSupDetail(\''+ row.indId+'\',\''+row.portfolioId+'\',\''+new Date(row.supDate).format('yyyy-MM-dd')
                                            + '\',\''+ row.supFrequency+'\')" style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
                         }

					}
                else if(d == violation){
                    if(row.mxCount==0){
                      return  '<a style="cursor: pointer;"><span style="color: red">'+d+'</span></a>';
                    }
                     else{
                        return  '<a  href="javascript:;" style="text-decoration:underline" onclick="javascript:showSupDetail(\''+ row.indId+'\',\''+row.portfolioId+'\',\''+new Date(row.supDate).format('yyyy-MM-dd')
                                        + '\',\''+ row.supFrequency+'\')" style="cursor: pointer;"><span style="color: red">'+d+'</span></a>';
                     }
				}
             }}
            ,{field:'supUniteStatAbbr',sortField:"supUniteStat",width:120,title: 'RskSupervisionRiskAnalyze001.supUniteStat',render:function(d,type, row, meta){
           	 if(d == normal)
            	 return  '<a  href="javascript:;" style="cursor: pointer;"><span style="color: green">'+d+'</span></a>';
            else if(d == warning)
            	 return  '<a  href="javascript:;"  style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
			 else if(d == approve)
				 return  '<a  href="javascript:;"  style="cursor: pointer;"><span style="color:  orange">'+d+'</span></a>';
            else if(d == violation)
                return  '<a  href="javascript:;" style="cursor: pointer;"><span style="color: red">'+d+'</span></a>';
            }}
            ,{field:'foulDays',width:120,title: 'RskSupervisionRiskAnalyze001.foulDays',render:function(d,type, row, meta){
              	 return  '<a  href="javascript:;" onclick="javascript:showSupDetail2(\''+ row.indId+'\',\''+row.portfolioId+'\','+d+',\''+new Date(row.supDate).format('yyyy-MM-dd')
					+ '\',\''+row.supFrequency+'\')" style="cursor: pointer;">'+d+'</a>';         
              }}
			,{field:'mathProcess', title: 'RskSupervisionRiskAnalyze001.mathProcess',width:200,visible :false}
            ,{field:'memo',width:200,title: 'RskSupervisionRiskAnalyze001.memo'}
        ]
    });

    // add by zhangrui1 指标计算明细 start
    var mathToString = function(mathProcess) {
        var object = JSON.parse(mathProcess);
        var str = JSON.stringify(object.PARAM);
        return str.replace(/\"/g,'#');
    }
    // add by zhangrui1 指标计算明细 end

	/**
	 * 绑定鼠标悬浮事件
	 */
	table.bind('mouseover', 'div.layui-table-main div.laytable-cell-2-groupName', function(data, index) {
		$('div[id*=xuanfu]:visible').each(function() {
			$(this).show();
		});
		$(this).after('<div id="xuanfu"'+index+' style="border-radius: 4px;text-align: center;display: block;z-index: 9999;border: 1px solid #ccc;height: 25px;width: 100px;position: absolute;top: 23px;background: #fff;left: 60px;">突破次数'+data.frequency+'次</div>')
	});
	
	table.bind('mouseleave', 'div.layui-table-main div.laytable-cell-2-groupName', function(data, index) {
		$('div[id*=xuanfu]:visible').each(function() {
			$(this).hide();
		});
	});

    /**
     * 绑定导出事件
     */
    var exportExcel = function() {
    	var getParams=getSearchParams();
        Joyin.exportExcel('export', getParams);
    };

    /**
     * 画面加载完成
     */
    $(function() {

        //资管跳转逻辑
        if(ssoUserName){
             infFlag=true;
             table.load();
         }
        else{
            infFlag=false;
            table.layOption.url="findPageInit";
            table.options.ajax.url="findPageInit";
            table.load();
        }
           // 绑定按钮事件
          $('#RskSupervisionRiskAnalyze001_btn_search').click(function() {
              infFlag=false;
              table.layOption.url="findPage";
              table.options.ajax.url="findPage";
              table.load();
          });
          $('#RskSupervisionRiskAnalyze001_btn_export').click(function() {
              exportExcel();
          });
    });
}();

function showSupDetail(indId,portfId,supdate,supFrequency){
	var data={};
	data.indId=indId;
	data.portfId=portfId;
	data.supDate=supdate;
	data.supFrequency=supFrequency;
   Joyin.dialog(JoyinUtil.getLabel("RskSupervisionRiskAnalyze001.detail"),
           basePath + "invest/riskredis/quota/rsksupervisionriskanalyze/RskSupervisionRiskAnalyze002/init",
           "70%", "90%",data);
};

function showSupDetail2(indId,portfId,foulDays,supdate,supFrequency){
	var data={};
	data.indId=indId;
	data.portfId=portfId;
	data.supDate=supdate;
	data.foulDays=foulDays;
	data.supFrequency=supFrequency;
   Joyin.dialog(JoyinUtil.getLabel("RskSupervisionRiskAnalyze001.detail2"),
           basePath + "invest/riskredis/quota/rsksupervisionriskanalyze/RskSupervisionRiskAnalyze003/init",
           "70%", "90%",data);
};

// add by zhangrui1 指标计算明细 start
function showExprDetail(indId,indName,mathProcess){
    var data={};
    data.indId=indId;
    data.indName = indName;
    data.mathProcess = mathProcess.replace(/#/g,'"');
    Joyin.dialog(JoyinUtil.getLabel("RskIndExprDtl"),
           basePath + "invest/riskredis/quota/rsksupervisionriskanalyze/RskIndExprDtl/init",
           "70%", "90%",data);
}
// add by zhangrui1 指标计算明细 end
