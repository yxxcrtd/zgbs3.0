/**
 * 主动提醒详情页面
 */
$(function() {
    // 时间和分类切换标记
    var flag = true;
    
     
    /**
     * 处理按钮点击事件
     */
    function dealRemind(url, lable, id, remType, days, feeId, menuId, table,mdate,orgId,orgName,ddate){
        if(flag == true){

            /*
            data={
                    finprodId:id,
                    feeId:feeId,
                    remType:remType,
                    days:days,
                    quaryDate : $('#quaryDate').val(),
                    pageModel : PAGE_MODEL_NEW
                 };
             // 弹窗打开
            Joyin.dialog(JoyinUtil.getLabel(lable),
                    JoyinUtil.getRealPath(url),
                    "98%", "95%",data,
                 function(){
                        table.table? table.reload() : table.load();
                 }
            );
            */

            var urlPara = "";

            if (remType == 'R7S3') {
                // R7S3 将 查询时间去掉。查全部数据
                urlPara = "?finprodId=" + id + "&feeId=" + feeId + "&remType=" + remType + "&days=" + days
                    + "&pageModel=" + PAGE_MODEL_NEW + "&mdate=" + mdate
                    + "&orgId=" + orgId + "&orgName=" + orgName;
            } else if (remType == 'R3S5') {
                // R3S5 添加计息截止日作为跳转后查询条件
                urlPara = "?finprodId=" + id + "&feeId=" + feeId + "&remType=" + remType + "&days=" + days
                    + "&quaryDate=" + $('#quaryDate').val() + "&pageModel=" + PAGE_MODEL_NEW + "&mdate=" + mdate
                    + "&orgId=" + orgId + "&orgName=" + orgName + "&dDate=" + ddate;

            }else if (remType == 'R5S3') {
                urlPara = "?paraName=" + id + "&feeId=" + feeId + "&remType=" + remType + "&days=" + days
                    + "&quaryDate=" + $('#quaryDate').val() + "&pageModel=" + PAGE_MODEL_NEW + "&mdate=" + mdate
                    + "&orgId=" + orgId + "&orgName=" + orgName + "&dDate=" + ddate;

            }else if (remType == 'R11S1') {
                urlPara = "?finprodId=" + id + "&feeId=" + feeId + "&remType=" + remType + "&days=" + days
                    + "&quaryDate=" + $('#quaryDate').val() + "&pageModel=" + PAGE_MODEL_NEW + "&mdate=" + mdate
                    + "&orgId=" + orgId + "&orgName=" + orgName + "&dDate=" + ddate;

            }else {
                urlPara = "?finprodId=" + id + "&feeId=" + feeId + "&remType=" + remType + "&days=" + days
                    + "&quaryDate=" + $('#quaryDate').val() + "&pageModel=" + PAGE_MODEL_NEW + "&mdate=" + mdate
                    + "&orgId=" + orgId + "&orgName=" + orgName;
            }
            
            
            var urls = "/invest"+url+urlPara;
            if(remType=='R6S1'){
            	urls = url+urlPara;
            }
            //add by chenjiaxing 20210727 固定值参数更新提醒 start
            if(remType == 'R5S3'){
                urls = url+urlPara;
            }
            //add by chenjiaxing 20210727 固定值参数更新提醒 end
            //add by chenjiaxing 20210728 债券分销缴款提醒 start
            if(remType == 'R11S1'){
                urls = url+urlPara;
            }
            //add by chenjiaxing 20210728 债券分销缴款提醒 end
            // 新窗口打开
            var newTab = {
                pageId: lable,
                title: JoyinUtil.getLabel(lable),
                url:urls,
                id: menuId,
                icon: ''
            };
            top.tab.tabDelete(newTab.id);
            top.tab.tabAdd(newTab);

        }else{
            Joyin.alert(JoyinUtil.getMessage("FIN_E_0003"));
        }
    }

    function dealRemindR1(url, lable,prodId, finprodId,oneAttr, remType, days, feeId, menuId, table,mdate,orgId,orgName,ddate){
            if(flag == true){

                var urlPara = "";

                if (remType == 'R1S5' && oneAttr == '02') {
                    urlPara = "?finprodId=" + prodId + "&feeId=" + feeId + "&remType=" + remType + "&days=" + days
                        + "&quaryDate=" + $('#quaryDate').val() + "&pageModel=" + PAGE_MODEL_NEW + "&mdate=" + mdate
                        + "&orgId=" + orgId + "&orgName=" + orgName;
                }else{
                    urlPara = "?finprodId=" + finprodId + "&feeId=" + feeId + "&remType=" + remType + "&days=" + days
                                            + "&quaryDate=" + $('#quaryDate').val() + "&pageModel=" + PAGE_MODEL_NEW + "&mdate=" + mdate
                                            + "&orgId=" + orgId + "&orgName=" + orgName;
                }

                if(oneAttr == '01' || oneAttr == '03'){
                    url = '/product/productduration/netproductDuration/FinNetProductDuration/init';
                    lable = 'FinNetProductDuration';
                }

                var urls = "/invest";
//                if (remType == 'R1S3') {
//                    if(oneAttr == '02'){
//                        urls += url+urlPara;
//                    }else if(oneAttr == '03'){
//                        urls += '/product/productduration/netproductDuration/FinNetProductDuration/init'+urlPara;
//                        lable = 'FinNetProductDuration';
//                    }
//                }else if(remType == 'R1S5'){
//                    if(oneAttr == '02'){
//                        urls += url+urlPara;
//                    }else{
//                        // 净值型 或 收益型
//                        urls += '/product/productduration/netproductDuration/FinNetProductDuration/init'+urlPara;
//                        lable = 'FinNetProductDuration';
//                    }
//                }else{
//
//                        urls += url+urlPara;
//                }
                urls += url+urlPara;

                // 新窗口打开
                var newTab = {
                    pageId: lable,
                    title: JoyinUtil.getLabel(lable),
                    url:urls,
                    id: menuId,
                    icon: ''
                };
                top.tab.tabDelete(newTab.id);
                top.tab.tabAdd(newTab);

            }else{
                Joyin.alert(JoyinUtil.getMessage("FIN_E_0003"));
            }
        }

    /**
     * 关闭按钮点击事件
     */
    function closeRemind(remType, id, name, feeId, mdate, remark, table){
        if(flag == true){
            Joyin.confirm(JoyinUtil.getMessage('FIN_E_0001'), function(){
                Joyin.ajax(JoyinUtil.getRealPath('/invest/remind/PaActiveRemind002/closeRemind'),
                    {
                        cdate : $('#quaryDate').val(),
                        remType : remType,
                        finprodId : id,
                        feeId : feeId,
                        finprodName : name,
                        remark : remark,
                        mdate : mdate
                    },
                    function(result){
                        Joyin.alert(JoyinUtil.getMessage("FIN_E_0002"));
                        table.table? table.reload() : table.load();
                    })
            });
        }else{
            Joyin.alert(JoyinUtil.getMessage("FIN_E_0003"));
        }
    }

    /**
     * 获取查询参数
     */
    var searchform = function() {
        if ($('#quaryDate').val() == null || $('#quaryDate').val() == '') {
            layer.msg(JoyinUtil.getLabel("请选择日期"));
        } else {
            return {
                quaryDate : $('#quaryDate').val(),
                mainCategory : $('#mainCategory').val(),
                smallCategory : $('#smallCategory').val()
            }
        }
    };

    /**
     * 定义数据表格
     */
    var tableR1 = $("#dataTableR1").joyintable(
        {
            ajax : {
                url : 'selramind',
                type : 'POST',
                data : function() {
                    return searchform();
                }
            },
            cols : [
                {
                    field : 'remTypeStr',
                    title : 'PaActiveRemind002.remType',
                    width : 200
                },
                {
                    field : 'finprodId',
                    title : 'PaActiveRemind002.finprodId',
                    width : 200
                },
                {
                    field : 'finprodName',
                    title : 'PaActiveRemind002.finprodName',
                    width : 300
                },
                {
                    field : 'reminder',
                    title : 'PaActiveRemind002.reminder',
                    width : 300
                },
                {
                    field : 'prodId',
                    visible : false
                },
                {
                    field : 'feeId',
                    visible : false
                },
                {
                    field : 'remType',
                    visible : false
                },
                {
                    field : 'url',
                    visible : false
                },
                {
                    field : 'lable',
                    visible : false
                },
                {
                    field : 'dealF',
                    visible : false
                },
                {
                    field : 'closF',
                    visible : false
                },
                {
                    field : 'days',
                    visible : false
                },
                {
                    field : 'mdate',
                    visible : false
                },
                {
                    field : 'objType',
                    visible : false
                },
                {
                    field : 'oneAttr',
                    visible : false
                },
                {
                    field : 'orgId',
                    visible : false
                },
                {
                    field : 'orgName',
                    visible : false
                },
                {
                    field : 'mdateStr',
                    visible : false
                },
                {
                    field : 'odate',
                    visible : false
                },
                {
                    field : 'odateStr',
                    visible : false
                },
                {
                    field : 'menuId',
                    visible : false
                },
                /*OPEX-612【主动提醒】项目类和计划类利率型资产 315检查中的提醒出来2条一样的提醒 guyunfei start*/
                {
                    field : 'cashType',
                    visible : false
                },
                /*OPEX-612【主动提醒】项目类和计划类利率型资产 315检查中的提醒出来2条一样的提醒 guyunfei end*/
                {
                    field : 'operating',
                    title : 'PaActiveRemind002.operating',
                    title : 'PaActiveRemind002.operating',
                    align : 'center',
                    width : 200,
                    render : function(data, type, row, meta) {
                        var operating = ""
                        if(row.dealF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnDeal_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.deal")+"</a>"
                        };
                        if(row.closF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnClose_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.close")+"</a>"
                        };
                        return operating
                    },
                } ],
            loadonce:true,
            searchOnInit:false
        });

    /**
     * 定义数据表格
     */
    var tableR2 = $("#dataTableR2").joyintable(
        {
            ajax : {
                url : 'selramind',
                type : 'POST',
                data : function() {
                    return searchform();
                }
            },
            cols : [
                {
                    field : 'remTypeStr',
                    title : 'PaActiveRemind002.remType',
                    width : 200
                },
                {
                    field : 'appearMarketId',
                    visible : false
                },
                {
                    field : 'assetCode',
                    title : 'PaActiveRemind002.assetCode',
                    width : 200
                },
                {
                    field : 'assetName',
                    title : 'PaActiveRemind002.assetName',
                    width : 300
                },
                {
                    field : 'reminder',
                    title : 'PaActiveRemind002.reminder',
                    width : 300
                },
                {
                    field : 'remType',
                    visible : false
                },
                {
                    field : 'assetId',
                    visible : false
                },
                {
                    field : 'feeId',
                    visible : false
                },
                {
                    field : 'dealF',
                    visible : false
                },
                {
                    field : 'url',
                    visible : false
                },
                {
                    field : 'lable',
                    visible : false
                },
                {
                    field : 'dealF',
                    visible : false
                },
                {
                    field : 'closF',
                    visible : false
                },
                {
                    field : 'days',
                    visible : false
                },
                {
                    field : 'mdate',
                    visible : false
                },
                // add by jiangpeng on 20200728 债券存续期 计息截止日与支付日不同时 交易查询用 start
                {
                    field : 'dDate',
                    visible : false
                },
                {
                    field : 'dDateStr',
                    visible : false
                },
                // add by jiangpeng on 20200728 债券存续期 计息截止日与支付日不同时 交易查询用 end
                {
                    field : 'objType',
                    visible : false
                },
                {
                    field : 'orgId',
                    visible : false
                },
                {
                    field : 'orgName',
                    visible : false
                },
                {
                    field : 'mdateStr',
                    visible : false
                },
                {
                    field : 'menuId',
                    visible : false
                },
                /*OPEX-612【主动提醒】项目类和计划类利率型资产 315检查中的提醒出来2条一样的提醒 guyunfei start*/
                {
                    field : 'cashType',
                    visible : false
                },
                /*OPEX-612【主动提醒】项目类和计划类利率型资产 315检查中的提醒出来2条一样的提醒 guyunfei end*/
                {
                    field : 'operating',
                    title : 'PaActiveRemind002.operating',
                    align : 'center',
                    width : 200,
                    render : function(data, type, row, meta) {
                        var operating = ""
                        if(row.dealF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnDeal_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.deal")+"</a>"
                        };
                        if(row.closF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnClose_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.close")+"</a>"
                        };
                        return operating
                    },
                } ],
            loadonce:true,
            searchOnInit:false
        });

    /**
     * 定义数据表格
     */
    var tableR5 = $("#dataTableR5").joyintable(
        {
            ajax : {
                url : 'selramind',
                type : 'POST',
                data : function() {
                    return searchform();
                }
            },
            cols : [
                {
                    field : 'remTypeStr',
                    title : 'PaActiveRemind002.remType',
                    width : 200
                },
                {
                    field : 'assetCode',
                    title : '参数代码',
                    width : 200
                },
                {
                    field : 'assetName',
                    title : '参数名称',
                    width : 300
                },
                {
                    field : 'reminder',
                    title : 'PaActiveRemind002.reminder',
                    width : 300
                },
                {
                    field : 'remType',
                    visible : false
                },
                {
                    field : 'assetId',
                    visible : false
                },
                {
                    field : 'feeId',
                    visible : false
                },
                {
                    field : 'dealF',
                    visible : false
                },
                {
                    field : 'url',
                    visible : false
                },
                {
                    field : 'lable',
                    visible : false
                },
                {
                    field : 'dealF',
                    visible : false
                },
                {
                    field : 'closF',
                    visible : false
                },
                {
                    field : 'days',
                    visible : false
                },
                {
                    field : 'mdate',
                    visible : false
                },
                // add by jiangpeng on 20200728 债券存续期 计息截止日与支付日不同时 交易查询用 start
                {
                    field : 'dDate',
                    visible : false
                },
                {
                    field : 'dDateStr',
                    visible : false
                },
                // add by jiangpeng on 20200728 债券存续期 计息截止日与支付日不同时 交易查询用 end
                {
                    field : 'objType',
                    visible : false
                },
                {
                    field : 'orgId',
                    visible : false
                },
                {
                    field : 'orgName',
                    visible : false
                },
                {
                    field : 'mdateStr',
                    visible : false
                },
                {
                    field : 'menuId',
                    visible : false
                },
                /*OPEX-612【主动提醒】项目类和计划类利率型资产 315检查中的提醒出来2条一样的提醒 guyunfei start*/
                {
                    field : 'cashType',
                    visible : false
                },
                /*OPEX-612【主动提醒】项目类和计划类利率型资产 315检查中的提醒出来2条一样的提醒 guyunfei end*/
                {
                    field : 'operating',
                    title : 'PaActiveRemind002.operating',
                    align : 'center',
                    width : 200,
                    render : function(data, type, row, meta) {
                        var operating = ""
                        if(row.dealF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnDeal_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.deal")+"</a>"
                        };
                        if(row.closF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnClose_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.close")+"</a>"
                        };
                        return operating
                    },
                } ],
            loadonce:true,
            searchOnInit:false
        });

    /**
     * 定义数据表格
     */
    var tableR12 = $("#dataTableR12").joyintable(
        {
            ajax : {
                url : 'selramind',
                type : 'POST',
                data : function() {
                    return searchform();
                }
            },
            cols : [
                {
                    field : 'remTypeStr',
                    title : 'PaActiveRemind002.remType',
                    width : 200
                },
                {
                    field : 'finprodId',
                    title : '组合代码',
                    width : 200
                },
                {
                    field : 'finprodName',
                    title : '组合名称',
                    width : 300
                },
                {
                    field : 'trdAcctId',
                    visible : false
                },
                {
                    field : 'trdAcctName',
                    title : '资金账户',
                    width : 300
                },
                {
                    field : 'reminder',
                    title : 'PaActiveRemind002.reminder',
                    width : 300
                },
                {
                    field : 'dealF',
                    visible : false
                },
                {
                    field : 'closF',
                    visible : false
                },
                {
                    field : 'tradePlace',
                    visible : false
                },
                {
                    field : 'cdate',
                    visible : false
                },
                {
                    field : 'cdateStr',
                    visible : false
                },
                {
                    field : 'operating',
                    title : 'PaActiveRemind002.operating',
                    align : 'center',
                    width : 200,
                    render : function(data, type, row, meta) {
                        var operating = ""
                        if(row.dealF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnDeal_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.deal")+"</a>"
                        };
                        if(row.closF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnClose_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.close")+"</a>"
                        };
                        return operating
                    },
                } ],
            loadonce:true,
            searchOnInit:false
        });



    /**
     * 定义数据表格
     */
    var tableR3 = $("#dataTableR3").joyintable(
        {
            ajax : {
                url : 'selramind',
                type : 'POST',
                data : function() {
                    return searchform();
                }
            },
            cols:[
                {field:'indId',visible :false}
                ,{field:'frequency',visible :false}
                ,{field:'supFrequency',visible :false}
                ,{field :'remType', visible : false}
                ,{field:'portfolioId',title: 'PaActiveRemindss002.groupName',width:250}
                ,{field:'groupName', title: 'PaActiveRemindss002.groupNameStr',width:250}
                ,{field:'supTypeAbbr',sortField:"supType", title: 'PaActiveRemindss002.supType',width:100}
                ,{field:'supDate', title: 'PaActiveRemindss002.supDate',type:'date'}
                ,{field:'indName', title: 'PaActiveRemindss002.indName',width:200}
                ,{field:'warnCond', width:150 ,title: 'PaActiveRemindss002.warnCond',render: RskQuotaCommon.transferWarnFoul}
                ,{field:'approveCond',width:150, title: 'PaActiveRemindss002.approveCond',render: RskQuotaCommon.transferWarnFoul}
                 ,{field:'foulCond', width:150 ,title: 'PaActiveRemindss002.foulCond',render: RskQuotaCommon.transferWarnFoul}
                ,{field:'supVal', title: 'PaActiveRemindss002.threshol',width:150,render: RskQuotaCommon.transferSupVal}
                ,{field:'supStatAbbr',sortField:"supStat",width:100,title: 'PaActiveRemindss002.supStat',render:function(d,type, row, meta){
                     if(row.supStat == "ERROR"){
                     	 return   JoyinUtil.getLabel("PaActiveRemindss002.exception");
                      }
                	 if(d == normal){
                         if(row.mxCount==0){
                             return  '<a style="cursor: pointer;"><span style="color: green">'+d+'</span></a>';
                         }
                        else{
                            return  '<a style="cursor: pointer;"><span style="color: green">'+d+'</span></a>';
//                             return  '<a  href="javascript:;" style="text-decoration:underline" onclick="javascript:showSupDetail(\''+ row.indId+'\',\''+row.portfolioId+'\',\''+new Date(row.supDate).format('yyyy-MM-dd')
//                                            + '\',\''+ row.supFrequency+'\')" style="cursor: pointer;"><span style="color: green">'+d+'</span></a>';
                        }
                      }
                    else if(d == warning){
                         if(row.mxCount==0){
                             return  '<a style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
                         }
                        else{
                        	  return  '<a style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
//                            return  '<a  href="javascript:;" style="text-decoration:underline" onclick="javascript:showSupDetail(\''+ row.indId+'\',\''+row.portfolioId+'\',\''+new Date(row.supDate).format('yyyy-MM-dd')
//                                                + '\',\''+ row.supFrequency+'\')" style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
                        }

     					}
    				 else if(d == approve){
    				            if(row.mxCount==0){
                                  return  '<a style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
                              }
                             else{
                            	  return  '<a style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
//                                return  '<a  href="javascript:;" style="text-decoration:underline" onclick="javascript:showSupDetail(\''+ row.indId+'\',\''+row.portfolioId+'\',\''+new Date(row.supDate).format('yyyy-MM-dd')
//                                                + '\',\''+ row.supFrequency+'\')" style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
                             }

    					}
                    else if(d == violation){
                        if(row.mxCount==0){
                          return  '<a style="cursor: pointer;"><span style="color: red">'+d+'</span></a>';
                        }
                         else{
                        	 return  '<a style="cursor: pointer;"><span style="color: red">'+d+'</span></a>';
//                            return  '<a  href="javascript:;" style="text-decoration:underline" onclick="javascript:showSupDetail(\''+ row.indId+'\',\''+row.portfolioId+'\',\''+new Date(row.supDate).format('yyyy-MM-dd')
//                                            + '\',\''+ row.supFrequency+'\')" style="cursor: pointer;"><span style="color: red">'+d+'</span></a>';
                         }
    				}
                 }}
                ,{field:'supUniteStatAbbr',sortField:"supUniteStat",width:120,title: 'PaActiveRemindss002.supUniteStat',render:function(d,type, row, meta){
               	 if(d == normal)
                	 return  '<a  href="javascript:;" style="cursor: pointer;"><span style="color: green">'+d+'</span></a>';
                else if(d == warning)
                	 return  '<a  href="javascript:;"  style="cursor: pointer;"><span style="color: orange">'+d+'</span></a>';
    			 else if(d == approve)
    				 return  '<a  href="javascript:;"  style="cursor: pointer;"><span style="color:  orange">'+d+'</span></a>';
                else if(d == violation)
                    return  '<a  href="javascript:;" style="cursor: pointer;"><span style="color: red">'+d+'</span></a>';
                }}
                ,{field:'foulDays',width:120,title: 'PaActiveRemindss002.foulDays',
//                	render:function(d,type, row, meta){
//                  	 return  '<a  href="javascript:;" onclick="javascript:showSupDetail2(\''+ row.indId+'\',\''+row.portfolioId+'\','+d+',\''+new Date(row.supDate).format('yyyy-MM-dd')
//    					+ '\',\''+row.supFrequency+'\')" style="cursor: pointer;">'+d+'</a>';         
//                  }
                }
    			,{field:'mathProcess', title: 'PaActiveRemindss002.mathProcess',width:200,visible :false}
                ,{field:'memo',width:200,title: 'PaActiveRemindss002.memo'}
                ,{
                    field : 'operating',
                    title : 'PaActiveRemind002.operating',
                    align : 'center',
                    width : 200,
                    render : function(data, type, row, meta) {
                        var operating = ""
                        if(row.dealF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnDeal_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.deal")+"</a>"
                        };
                        if(row.closF == "Y"){
                            operating = operating+"<a id=\"PaActiveRemind002_btnClose_action\" style='color: #fff;' class=\"layui-btn layui-btn-sm\">"+JoyinUtil.getLabel("PaActiveRemind002.close")+"</a>"
                        };
                        return operating
                    },
                }
            ],
            loadonce:true,
            searchOnInit:false
        });

       /**
       * 定义数据表格--公共数据变更
       */
       var tableR9 = $("#dataTableR9").joyintable(
           {
               ajax : {
                   url : 'selramind',
                   type : 'POST',
                   data : function() {
                       return searchform();
                   }
               },
               cols:[
                   	{
                         field : 'remTypeStr',
                         title : 'PaActiveRemind002.remType',
                         width : 200
                    },
                    {
                         field : 'assetCode',
                         title : 'PaActiveRemind002.busiCode',
                         width : 200
                    },
                    {
                         field : 'assetName',
                         title : 'PaActiveRemind002.busiName',
                         width : 300
                    },
                    {
                         field : 'reminder',
                         title : 'PaActiveRemind002.reminder',
                         width : 500
                    }
                ],
                loadonce:true,
                searchOnInit:false
            }
       );

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

    /**
     * 绑定查询按钮事件
     */
    $('#PaActiveRemind002_btn_search').on('click', function() {
        if ($('#smallCategory').val() == null || $('#smallCategory').val() == '') {
            Joyin.error(JoyinUtil.getMessage("请选择提醒类型（小分类）"));
            return;
        }
        if($('#mainCategory') && ($('#mainCategory').val() === 'R1'||$('#mainCategory').val() === 'R6')){
            $('#dataTableR2_div').toggle(false);
            $('#dataTableR3_div').toggle(false);
            $('#dataTableR9_div').toggle(false);
            $('#dataTableR5_div').toggle(false);
            $('#dataTableR1_div').toggle(true);
            tableR1.table? tableR1.reload() : tableR1.load();
        }else if($('#mainCategory') && $('#mainCategory').val() === 'R5'){
            if($('#smallCategory') && $('#smallCategory').val() === 'R5S3'){
                $('#dataTableR1_div').toggle(false);
                $('#dataTableR2_div').toggle(false);
                $('#dataTableR3_div').toggle(false);
                $('#dataTableR5_div').toggle(true);
                $('#dataTableR9_div').toggle(false);
                tableR5.table? tableR5.reload() : tableR5.load();
            }else{
                $('#dataTableR1_div').toggle(false);
                $('#dataTableR3_div').toggle(true);
                $('#dataTableR2_div').toggle(false);
                $('#dataTableR5_div').toggle(false);
                $('#dataTableR9_div').toggle(false);
                tableR3.table? tableR3.reload() : tableR3.load();
            }
        }else if($('#mainCategory') && $('#mainCategory').val() === 'R9'){
         	$('#dataTableR1_div').toggle(false);
         	$('#dataTableR3_div').toggle(false);
         	$('#dataTableR2_div').toggle(false);
            $('#dataTableR5_div').toggle(false);
            $('#dataTableR9_div').toggle(true);
         	tableR9.table? tableR9.reload() : tableR9.load();
        }else if($('#mainCategory') && $('#mainCategory').val() === 'R12') {
            $('#dataTableR1_div').toggle(false);
            $('#dataTableR3_div').toggle(false);
            $('#dataTableR2_div').toggle(false);
            $('#dataTableR5_div').toggle(false);
            $('#dataTableR9_div').toggle(false);
            $('#dataTableR12_div').toggle(true);
            tableR12.table? tableR12.reload() : tableR12.load();
        }else{
            $('#dataTableR1_div').toggle(false);
            $('#dataTableR2_div').toggle(true);
            $('#dataTableR3_div').toggle(false);
            $('#dataTableR5_div').toggle(false);
            $('#dataTableR9_div').toggle(false);
            tableR2.table? tableR2.reload() : tableR2.load();
        }
        flag = true;
    });

    /**
     * 关闭按钮绑定事件
     */
    tableR1.bind('click','#PaActiveRemind002_btnClose_action', function(row) {
        closeRemind(row.remType, row.prodId, row.finprodName, row.feeId, row.mdateStr, null, tableR1);
    });

    /**
     * 处理按钮绑定事件
     */
    tableR1.bind('click','#PaActiveRemind002_btnDeal_action', function(row) {
        if(row.remType == 'R1S6'){
            dealRemind(row.url, row.lable, row.prodId, row.remType, row.days, row.feeId, row.menuId, tableR1,row.mdateStr,row.orgId,row.orgName);
        }else{
            dealRemindR1(row.url, row.lable,row.prodId, row.finprodId, row.oneAttr,row.remType, row.days, row.feeId, row.menuId, tableR1,row.mdateStr,row.orgId,row.orgName);
        }
    });

    /**
     * 标准券额度不足关闭按钮绑定事件
     */
    tableR12.bind('click','#PaActiveRemind002_btnClose_action', function(row) {
        closeRemindR12(row.remType, row.finprodId, row.finprodName, row.tradePlace, row.cdateStr, null, tableR12);
    });
    /**
     * 标准券额度不足关闭按钮点击事件
     */
    function closeRemindR12(remType, finprodId, finprodName, tradePlace, cdate, remark, table){
        if(flag == true){
            Joyin.confirm(JoyinUtil.getMessage('FIN_E_0001'), function(){
                Joyin.ajax(JoyinUtil.getRealPath('/invest/remind/PaActiveRemind002/closeRemind'),
                    {
                        cdate : $('#quaryDate').val(),
                        remType : remType,
                        finprodId : finprodId,
                        tradePlace : tradePlace,
                        finprodName : finprodName,
                        remark : remark,
                        cdate : cdate
                    },
                    function(result){
                        Joyin.alert(JoyinUtil.getMessage("FIN_E_0002"));
                        table.table? table.reload() : table.load();
                    })
            });
        }else{
            Joyin.alert(JoyinUtil.getMessage("FIN_E_0003"));
        }
    }

    /**
     * 处理按钮绑定事件
     */
    tableR12.bind('click','#PaActiveRemind002_btnDeal_action', function(row) {
        dealRemindR12(row.url, row.lable, row.finprodId, row.remType, row.tradePlace, row.feeId, row.menuId, tableR1,row.cdateStr,row.orgId,row.orgName);
    });

    function dealRemindR12(url, lable, id, remType, tradePlace, feeId, menuId, table,mdate,orgId,orgName,ddate){
        if(flag == true){
            var finprodId;
            var param = {
                portfolioId: id,
                cdate: mdate,
                tradePlace:tradePlace
            };
            Joyin.ajaxJson(basePath + 'invest/remind/PaActiveRemind001/searchFinprodId', param, function (result) {
                if (result.FINPROD_ID) {
                    finprodId = result.FINPROD_ID;
                }
            }, false);

            var  urlPara = "?finprodId=" + finprodId + "&portfolioId=" + id +"&ps=T68";
            url = 'home.html#/instructions/notradeorder?a=a';
            lable='\u975e\u4ea4\u6613\u4e49\u52a1\u6307\u4ee4';
            var urls = "";
            urls += url+urlPara;
            console.log(urls);
            var newTab = {
                pageId: lable,
                title: JoyinUtil.getLabel(lable),
                url:urls,
                id: menuId,
                icon: ''
            };
            top.tab.tabDelete(newTab.id);
            top.tab.tabAdd(newTab);
        }else{
            Joyin.alert(JoyinUtil.getMessage("FIN_E_0003"));
        }
    }



    /**
     * 关闭按钮绑定事件
     */
    tableR2.bind('click','#PaActiveRemind002_btnClose_action', function(row) {
        closeRemind(row.remType, row.assetCode, row.assetName, row.feeId, row.mdateStr, null, tableR2);
    });

    /**
     * 处理按钮绑定事件
     */
    tableR2.bind('click','#PaActiveRemind002_btnDeal_action', function(row) {
    	if(row.remType=='R7S1'){
            dealRemind(row.url, row.lable, row.assetCode, row.remType, row.days, row.feeId, row.menuId, tableR2,row.odateStr);
        }else if(row.remType=='R5S3'){
            dealRemind(row.url, row.lable, row.assetName, row.remType, row.days, row.feeId, row.menuId, tableR2,row.odateStr);
        }else if(row.remType=='R3S5'){
            dealRemind(row.url, row.lable, row.assetCode, row.remType, row.days, row.feeId, row.menuId, tableR2,row.mdateStr,row.orgId,row.orgName,row.dDateStr);
        }else if(row.remType=='R11S1'){
            dealRemind(row.url, row.lable, row.assetCode, row.remType, row.days, row.feeId, row.menuId, tableR2,row.mdateStr,row.orgId,row.orgName,row.dDateStr);
        }else if (row.remType == 'R10S1'){
                     //债券上市流通交易新建页面单独打开
                     var data = {};
                     data.pageModel=PAGE_MODEL_NEW;
                     data.finprodId = row.finprodId;
                     data.appearMarketId = row.appearMarketId;
                     Joyin.dialog(JoyinUtil.getLabel("TrdBondMarketTrade002"),
                         JoyinUtil.getRealPath("invest/trade/bondmarkettrade/TrdBondMarketTrade002/init"),
                         "99%", "99%", data,
                             function(){tableR2.reload();});
        }else{
            dealRemind(row.url, row.lable, row.assetCode, row.remType, row.days, row.feeId, row.menuId, tableR2,row.mdateStr,row.orgId,row.orgName);
        }
    });

    tableR5.bind('click','#PaActiveRemind002_btnDeal_action', function(row) {
       if(row.remType=='R5S3') {
           dealRemind(row.url, row.lable, row.assetName, row.remType, row.days, row.feeId, row.menuId, tableR2, row.odateStr);
       }
    });

    tableR5.bind('click','#PaActiveRemind002_btnClose_action', function(row) {
        closeRemind(row.remType, row.assetCode, row.assetName, row.feeId, row.mdateStr, row.remark, tableR5);
    });

    /**
     * 查询要素变更后需重新点击查询
     */
    function itemChange(){
        flag = false;
    }

    // 所有dom对象加载完的处理
    $(document).ready(function() {
        if($('#mainCategory') && ($('#mainCategory').val() === 'R1'||$('#mainCategory').val() === 'R6')){
            $('#dataTableR2_div').toggle(false);
            $('#dataTableR3_div').toggle(false);
            $('#dataTableR1_div').toggle(true);
            $('#dataTableR9_div').toggle(false);
            $('#dataTableR5_div').toggle(false);
            tableR1.table? tableR1.reload() : tableR1.load();
        }else if($('#mainCategory') && $('#mainCategory').val() === 'R5'){
            if($('#smallCategory') && $('#smallCategory').val() === 'R5S3'){
                $('#dataTableR1_div').toggle(false);
                $('#dataTableR2_div').toggle(false);
                $('#dataTableR3_div').toggle(false);
                $('#dataTableR9_div').toggle(false);
                $('#dataTableR5_div').toggle(true);
                tableR5.table? tableR5.reload() : tableR5.load();
            }else{
                $('#dataTableR1_div').toggle(false);
                $('#dataTableR3_div').toggle(true);
                $('#dataTableR2_div').toggle(false);
                $('#dataTableR9_div').toggle(false);
                $('#dataTableR5_div').toggle(false);
                tableR3.table? tableR3.reload() : tableR3.load();
            }
        }else if($('#mainCategory') && $('#mainCategory').val() === 'R9'){
            $('#dataTableR1_div').toggle(false);
            $('#dataTableR3_div').toggle(false);
            $('#dataTableR2_div').toggle(false);
            $('#dataTableR9_div').toggle(true);
            $('#dataTableR5_div').toggle(false);
            tableR9.table? tableR9.reload() : tableR9.load();
        }else if($('#mainCategory') && $('#mainCategory').val() === 'R12') {
            $('#dataTableR1_div').toggle(false);
            $('#dataTableR3_div').toggle(false);
            $('#dataTableR2_div').toggle(false);
            $('#dataTableR9_div').toggle(false);
            $('#dataTableR5_div').toggle(false);
            $('#dataTableR12_div').toggle(true);
            tableR12.table? tableR12.reload() : tableR12.load();
        }else{
            $('#dataTableR1_div').toggle(false);
            $('#dataTableR2_div').toggle(true);
            $('#dataTableR3_div').toggle(false);
            $('#dataTableR9_div').toggle(false);
            $('#dataTableR5_div').toggle(false);
            tableR2.table? tableR2.reload() : tableR2.load();
        }
        $('#mainCategory').change(itemChange);
        $('#smallCategory').change(itemChange);
        $('#quaryDate').change(itemChange);
    })
    /**
	 * 绑定鼠标悬浮事件
	 */
	tableR3.bind('mouseover', 'div.layui-table-main div.laytable-cell-2-groupName', function(data, index) {
		$('div[id*=xuanfu]:visible').each(function() {
			$(this).show();
		});
		$(this).after('<div id="xuanfu"'+index+' style="border-radius: 4px;text-align: center;display: block;z-index: 9999;border: 1px solid #ccc;height: 25px;width: 100px;position: absolute;top: 23px;background: #fff;left: 60px;">突破次数'+data.frequency+'次</div>')
	});
	
    tableR3.bind('mouseleave', 'div.layui-table-main div.laytable-cell-2-groupName', function(data, index) {
		$('div[id*=xuanfu]:visible').each(function() {
			$(this).hide();
		});
	});

    tableR3.bind('click','#PaActiveRemind002_btnClose_action', function(row) {
        closeRemind(row.remType, row.portfolioId, row.indId, row.feeId, row.mdateStr, row.supFrequency, tableR3);
    });
}());
