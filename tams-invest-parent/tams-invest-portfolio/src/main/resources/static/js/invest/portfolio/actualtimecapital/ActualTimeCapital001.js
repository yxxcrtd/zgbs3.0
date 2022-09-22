$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        // update by caiqun  工单YHZGCPB20200806001实时资金头寸里不能同时查询多个产品的现金 20200813 start
        var object = $.serializeToObject('#searchForm');
        var acctNo = object.acctNo;
        if(null != acctNo && "" != acctNo){
            acctNo=object.acctNo.join(',');
        }
        var portfolioId = object.portfolioId;
        if(null != portfolioId && "" != portfolioId){
            portfolioId=object.portfolioId.join(',');
        }
        return {
        	portfolioId:portfolioId,//组合代码 // update by caiqun  工单YHZGCPB20200806001实时资金头寸里不能同时查询多个产品的现金 20200813 end
        	cdate : $('#cdate').val(),//日期
        	acctNo: acctNo//资金账户
        }
    };

    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({ 
        ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },
        /**
         * 定义表格名称
         */
        cols:[
             {field:'portfolioName', title: 'ActualTimeCapital001.portfolioName', width : 200}//组合名称
            ,{field:'portfolioId', visible:false}//组合编码
            ,{field:'cdate', visible:false, type:'date'}//
            ,{field:'acctNo', title: '账号',width : 150}//账户
            ,{field:'acctName', title: 'ActualTimeCapital001.acctName', width : 200}//账号名称
            ,{field:'acctTypeStr', title: '账号类型',width : 150}//账户类型
            ,{field:'ccy', title: 'ActualTimeCapital001.ccy', width : 150}//币种
            ,{field:'cashBalance', title: 'ActualTimeCapital001.cashBalance', type:'number', width : 200}//现金余额
            ,{field:'cashAvailable', title: 'ActualTimeCapital001.cashAvailable', type:'number', width : 200}//指令可用现金
            ,{field:'tradeCash', title: 'ActualTimeCapital001.tradeCash',type:'number', width : 200}//交易可用现金
            ,{field:'desirableCash', title: 'ActualTimeCapital001.desirableCash',type:'number', width : 200}//可取现金
            ,{field:'advanceCash', title: 'ActualTimeCapital001.advanceCash',type:'number', width : 200}//预增现金
            ,{field:'preReductionCash', title: 'ActualTimeCapital001.preReductionCash',type:'number', width : 200}//预减现金
            ,{field:'adjustmentAmount', title: 'ActualTimeCapital001.adjustmentAmount',type:'number', width : 200}//手工调整
        ]
    });
    
    //联动：组合代码联动效果
    var portfolioIdLinkShow = function() {
    	$('#acctNo').val('').trigger('change');
    }
    

    
    /**
     * 导出
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();//获取参数
        Joyin.exportExcel('export', paramObj);//导出
    };
    
    //返回 01-12 的月份值  
    var getMonth = function getMonth(date){ 
      var month = ""; 
      month = date.getMonth() + 1; //getMonth()得到的月份是0-11 
      if(month<10){ 
        month = "0" + month; 
      } 
      return month; 
    } ;
    //返回01-30的日期 
    var getDay = function getDay(date){ 
      var day = ""; 
      day = date.getDate(); 
      if(day<10){ 
        day = "0" + day; 
      } 
      return day; 
    };
    
    var dateFormat_1 = function dateFormat_1(longTypeDate){ 
    	  var dateType = ""; 
    	  var date = new Date(); 
    	  date.setTime(longTypeDate); 
    	  dateType += date.getFullYear();  //年 
    	  dateType += "-" + getMonth(date); //月  
    	  dateType += "-" + getDay(date);  //日 
    	  return dateType;
    };
    /**
     * 画面加载完成
     */
    $(function() {
        // 绑定查询按钮
        $('#ActualTimeCapital001_btn_search').click(function() {
        	var strError = '';
//add by yangwenjun 20201120 实时资金头寸查询-查询条件“组合代码”修改为非必填 start
//            if(!$('#portfolioId').val()){
//                strError = "请输入"+JoyinUtil.getLabel("ActualTimeCapital001.portfolioId")+"<br>";
//            }
//add by yangwenjun 20201120 实时资金头寸查询-查询条件“组合代码”修改为非必填 end
            if(!$('#cdate').val()){
                strError = strError + "请输入"+JoyinUtil.getLabel("ActualTimeCapital001.cdate");
            }
            else {
                if ($('#cdate').val() > sysDate) {
                    strError = strError + '日期必须小于等于平台日';
                }
            }
            if(strError != '' && strError != null){
                Joyin.error(strError);
                return;
            }
            table.load();//加载表格
        });
        //绑定到处按钮
        $('#ActualTimeCapital001_btn_export').click(function() {
            exportExcel();//导出
        });
        
        //联动：组合代码联动效果
        $("#portfolioId").change(function(){
        	portfolioIdLinkShow();
        });
        
      //绑定手工调整按钮
      $('#ActualTimeAsset001_btn_adjamt').click(function() {
    	  var selArr = [];//判断表格不为空，然后获取选择的数据
    	  if(table!=null && table!=''){
    		  selArr = table.getAllCheckedRows();//判断表格不为空，然后获取选择的数据
    	  }
    	  if (selArr.length != 1) {
    		  Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0046"));
      		  return;
    	  }
    	  var data = {};
    	  var obj  = selArr[0];
    	  data.portfolioId = obj.portfolioId;
    	  data.adjDate = dateFormat_1(obj.cdate);
    	  data.acctId = obj.acctNo;
    	  data.ccy = obj.ccy;
    	  /**
    	  data.portfolioId = 'YANYA01';
    	  data.adjDate = '2019-10-16';
    	  data.acctId = 'TEST';
    	  data.ccy = 'CNY';**/
            Joyin.dialog(JoyinUtil.getLabel("ActualTimeCapital002"),
    				JoyinUtil.getRealPath("invest/portfolio/actualtimecapital/ActualTimeCapital002/init"),
    				"75%", "75%",data,function(){table.reload();}
    			);
      });
        
    });
}());