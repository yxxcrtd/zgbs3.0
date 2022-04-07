$(function () {
    var searchCheckLog = '';
    var status='';
    var detailName='--';
    var bookSetDate='';
    var bookMdate = '';
    var getPageParams = function() {
        return {
            booksetId:$.trim($('#booksetId').val()),
            startDate : $('#executeDate').val(),
            endDate : $('#executeDate').val(),
            taskStatus:status,
            detailName:detailName
        }
    };

    $('#typeDatatree').joyinMixTree({
        onTreeLoadSuccess: function() {
            $.fn.zTree.getZTreeObj('typeDatatree_tree').expandAll(true);
        }
    });

    function DisabledNode(treeObj, node) {
        treeObj.setChkDisabled(node, true);
        if(node.isParent) {
            var children = node.children;
            for(var i=0; i < children.length; i++) {
                DisabledNode(treeObj, children[i]);
            }
        }
    };

    function CheckNodeById(pid){
        var zTree = $.fn.zTree.getZTreeObj("typeDatatree_tree");
        var node = zTree.getNodeByParam("id",pid );
        zTree.cancelSelectedNode();//先取消所有的选中状态
        zTree.selectNode(node);//将指定ID的节点选中
        zTree.expandNode(node, true, false);//将指定ID节点展开
        zTree.checkNode(node);//将制定ID勾选】
    }

    /**
     * 日志页签
     */
    var mainTable = function(obj){
        $("#layui-tab-content").css('display','block');
        $("#layui-tab-check").css('display','none');
        var id = $(obj).attr("id");
        if(!id){
            id='log-title';
            status='';
        }if(id=='log-title'){
            status = '';
        }else if(id=='success-title'){
            status = 'END';
        }else if(id=='error-title'){
            status = 'EXP';
        }else if(id=='warning-title'){
            status = 'UNR';
        }else if(id=='eWarning-title'){
            status = 'RUN';
        }
        var table = id.replace('title','table');

        var docH= $(document).height();

        var tabTable=$('#'+table).joyintable({
            ajax: {
                url: 'findLogPage',
                type: 'POST',
                data:function(){
                    return getPageParams();
                }
            },
            done:function(data){
                if(data.length>0){
                    //如果结束时产品全部执行完毕,则取消轮询
                    var flag = 1;
                    var index = 0;
                    for(var i=0; i<data.length;i++){
                        var status = data[i].taskStatusCode;
                        if(status=='END'||status=='EXP'){
                            flag = 0;
                            index++;
                        }
                    }
                    if(index>=batchTaskNums){
                        stopInterval();
                        progress(1);
                        setTimeout(close(),3000);
                    }else{
                        progress(index/batchTaskNums);
                    }
                }
            },
            height:docH-160,
            checkbox: false,
            rowno:true,
            cols:
                [
                    {field: 'taskId', width: 150, title: '业务日期',visible:false}
                    ,{field: 'batchDate', width: 130, title: '业务日期',sort: true,
                    render: function (data, type, row, meta) {
                        return data.substring(0,10);
                    }
                }
                    , {field: 'detailName', width: 200, title: '任务名称',sort: true}
                    , {field: 'taskName', width: 180, title: '批处理类型',visible:false}
                    , {field: 'taskName', width: 220, title: '任务明细',sort: true,
                    render: function (data, type, row, meta) {
                        var html = '<a href="javascript:void(0);" class="taskName" data-seqno="' + row.seqNo + '">' + data + '</a>';
                        return html;
                    }
                }
                    , {field: 'taskStatus', width: 100, title: '执行状态',sortField:'taskStatusCode',sort: true,
                    render: function (data, type, row, meta) {
                        if(data=='正常结束'){
                            return '<span class="layui-badge-rim" style="width:55px;height:20px;background-color:#17c347;color:#fff">'+'成功'+'</span>';
                        }else if(data=='异常结束'){
                            return '<span class="layui-badge-rim" style="width:55px;height:20px;background-color:#ff2236;color:#fff">'+'失败'+'</span>';
                        }else if(data=='执行中'){
                            return '<span class="layui-badge-rim" style="width:55px;height:20px;background-color:#13a7edd1;color:#fff">'+'执行中'+'</span>';
                        }else if(data=='未执行'){
                            return '<span class="layui-badge-rim" style="width:55px;height:20px;">'+'未执行'+'</span>';
                        }
                    }
                }

                    , {field: 'startDateFmt',sorfiled:'batchStartTime', width: 130, title: '执行日期',sort: true}
                    , {field: 'useingTime', width: 100, title: '耗时(s)',sort: true}
                    , {field: 'startTimeFmt',sorfiled:'batchStartTime', width: 150, title: '开始时间',sort: true}
                    , {field: 'endTimeFmt',sorfiled:'batchEndTime', width: 150, title: '结束时间',sort: true}
                    , {field:'operation', title: '操作',sort: true,
                    render: function (data, type, row, meta) {
                        return '<a href="javascript:void(0);" onclick="openShow()" data-taskId="' + row.taskId + '">详细</a>';
                    }
                }
                ],
            loadonce:true,
            page:false
        });
        if(!tabTable.table) {
            tabTable.load();
        }else {
            tabTable.reload();
        }
    };

    var doInter = function (){

        var startDate = $('#executeDate').val();
        var endDate = $('#executeDate').val();
        var booksetId=$.trim($('#booksetId').val());
        if(booksetId==''){
            layer.alert('请选择账套！');
            return ;
        }
        if(startDate==''||endDate==''){
            layer.alert('请选择时间!');
            return ;
        }
        var treeObj=$.fn.zTree.getZTreeObj("typeDatatree_tree");
        //执行全部任务，不管有没有选中，都要执行(false)是未选中
        var nodes=treeObj.getCheckedNodes(false);
        var datas = "100";
//        for(var i=0;i<nodes.length;i++){
//            if(nodes[i].id!='00'){
//                datas+=nodes[i].id + ",";
//            }
//        }
//        nodes='';
//        //执行全部任务，不管有没有选中，都要执行(true)是已选中
//        nodes=treeObj.getCheckedNodes(true);
//        for(var i=0;i<nodes.length;i++){
//            if(nodes[i].id!='00'){
//                datas+=nodes[i].id + ",";
//            }
//        }
        executeBatch(datas,booksetId,startDate,endDate);
    };
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return {booksetId:$.trim($('#booksetId').val()),endDate:$('#executeDate').val(),prodGroupType:$("#booksets").val(),searchCheckLog: searchCheckLog};
            }
        },
        checkbox: false,
        searchOnInit: false,
        loadonce:true,
        page : true,
        cols: [
            {field:'booksetId', title: '账套代码',width:130, sort: true},
            {field:'bookPDate', title: '账套日期',width:130, sort: true},
            {field:'logInfo', title: '检查信息',width:480, sort: true},
            {field:'pageDate', title: '检查日期',width:170, sort: true}
        ]

    });
    var checkter=function(obj){
        $("#layui-tab-content").css('display','none');
        $("#layui-tab-check").css('display','block');

        var endDate = $('#executeDate').val();
        var booksetId=$.trim($('#booksetId').val());
        var id = $(obj).attr("id");
        if(!id){
            id='checking-title';
            status='';
        }else if(id=='checking-title'){
            status = '';
        }

        if (id == 'checking-title') {
            searchCheckLog = 'Y';
        } else {
            searchCheckLog = 'N';
        }
//      var distable=document.getElementById(layui-tab-content);
//      distable.style.display="none";

        table.load();
        $("#log-title").removeClass("layui-this");
        $("#checking-title").attr("class", "layui-this");
    };

    var executeBatch = function(selectedBatchGroup, selectedProductGroup, startDate, endDate) {
        var date= new Date();
        detailName=curUser.userName+'-'+startDate+'-'+date.format('yyyy-MM-dd HH:mm:ss');
        //Joyin.prompt("请为此次执行命名，以便在日志中查看", 0, function(result) {
        Joyin.confirm(JoyinUtil.getMessage('COMMON_C_0001', JoyinUtil
            .getLabel('SysCommon.execute')), function() {
            progress(0);
            doprocess();
            var batchGroupType = '1';
            var prodGroupType = $("#booksets").val();
            Joyin.ajax('executeBatch', {
                selectedBatchGroup : selectedBatchGroup,
                selectedProductGroup : selectedProductGroup,
                startDate : startDate,
                endDate : endDate,
                batchGroupType : batchGroupType,
                prodGroupType : prodGroupType,
                detailName : detailName
            }, function() {
                $("#setting_btn_save").attr("title", '5秒后才能再次执行').prop("disabled", true);
                setTimeout(function() {
                    startInterval();
                    $("#setting_btn_save").attr("title", '').prop("disabled", false);
                },5000)
            });
        });
        //}, detailName)

    }
    var stopInter=function(){
        Joyin.ajax('stopAs',
            {datas : datas,
                startDate:startDate,
                endDate:endDat
            },
            function() {
                layui.layer.alert("正在停止批处理操作");
            });
        /*      $.ajax({
                    type : "POST",
                    url : 'stopAs',
                    data : {datas : datas,
                        startDate:startDate,
                        endDate:endDat},

                    success : function(data) {
                        layui.layer.alert("正在停止批处理操作");
                    }
                });*/
    };


    var processIndex;

    var doprocess = function(){
        processIndex = layer.open({
            type: 1
            ,title: false //不显示标题栏
            ,closeBtn: false
            ,area: '300px;'
            ,shade: 0.6
            ,id: 'JOY_process' //设定一个id，防止重复弹出
            ,moveType: 1 //拖拽模式，0或者1
            ,content: '<div class="layui-progress layui-progress-big" lay-showpercent="true" lay-filter="progressBar" id="progressBarF"><div class="layui-progress-bar" lay-percent="0%">0%</div></div>'
            ,success: function(layero){
                layero.css('background', 'transparent');
                // 开始进度条
                progress(0);
            }
        });
    }

    function progress(percent){
        var percentText = parseInt(percent * 100) + '%';
        layui.element.progress('progressBar', percentText);
        $('#progressBar').find('.layui-progress-bar').text(percentText);
        $('#progressBarF').find('.layui-progress-bar').text(percentText);
    }

    function close(){
        layer.close(processIndex);
    }

    //轮询启动接口ID
    var interId;
    //轮询开始
    var startInterval = function(){
        //每5秒进行一次轮序
        interId = setInterval(mainTable,5000);
    };
    //轮询结束
    var stopInterval = function(){
        clearInterval(interId);
        gradeChange();
    }
    //初始化，开始之前不启动
    mainTable();
    var gradeChange=function(){
        var booksetId=$.trim($('#booksetId').val());
        if(booksetId==''||booksetId==null){
            return;
        }else{
            $.ajax({
                url : 'checkExecute',
                type : "POST",
                data : {booksetId:booksetId, prodGroupType:$("#booksets").val()},
                success: function(msg){
                    if(msg>0){
                        $('#booksetId').val('').trigger('change');
                        layer.alert('选择的账套存在正在进行登帐，请稍后再试!!');
                        return;
                    }else{
                        $.ajax({
                            url : 'checkBokkSetDate',
                            type : "POST",
                            data : {booksetId:booksetId, prodGroupType:$("#booksets").val()},
                            success: function(msg){
                                if(msg=='-1'){
                                    $('#booksetId').val('').trigger('change');
                                    layer.alert('所选账套的会计日期不一致，请分开执行!!');
                                }else{
                                    $('#executeDate').val(msg);
                                    bookSetDate=msg;
                                }
                            },
                            failure : function() {
                            }
                        });
                    }
                },
                failure : function() {
                }
            });
        }
    }
    var bookSetIdChange=function(){
        var booksetId=$.trim($('#booksetId').val());
        if(booksetId==''||booksetId==null){
            return;
        }else{
            $.ajax({
                url : 'checkExecute',
                type : "POST",
                data : {booksetId:booksetId,prodGroupType:$("#booksets").val()},
                success: function(msg){
                    if(msg>0){
                        $('#booksetId').val('').trigger('change');
                        layer.alert('选择的账套存在正在进行登帐，请稍后再试!!');
                        return;
                    }else{
                    }
                },
                failure : function() {
                }
            });
        }
    }
    var dateChange = function(){
        var date = $('#executeDate').val();
        if(new Date(date)<new Date(bookSetDate)){
            layer.alert('选择日期小于账套当前日期'+bookSetDate+'，若需要对历史日期重新登账，请先对账套日期进行切换，再进行登账批处理!');
            $('#executeDate').val('');
            return ;
        }else if(new Date(date)>new Date(bookSetDate)){
            layer.alert('选择日期必须等于账套当前日期'+bookSetDate);
            $('#executeDate').val('');
            return;
        }
    }
    var bokCheck = function(){


        //校验所选日期不能大于账套结束日------------yangjian 2019/06/20 begin
        if(checkDateForBookSetMdate()){
            return;
        }
        //校验所选日期不能大于账套结束日------------yangjian 2019/06/20 end


//      Joyin.confirm(JoyinUtil.getMessage('COMMON_I_0009', JoyinUtil
//              .getLabel('SysCommon.execute')), function(){
//
//       });
        var endDate = $('#executeDate').val();
        var booksetId=$.trim($('#booksetId').val());


        //添加逻辑，当



        Joyin.ajax(basePath+"valuation/dailyoperation/dailyaccountmake/valaccounthandle/ValAccountHandle001/geyCheckSysValue",null,function(res){
            //如果系统参数值为off
            if(res==0){
                //原先批处理
                bookSetIdChange();
                dateChange();
                doInter();
            }else{
                Joyin.ajax(basePath+"valuation/dailyoperation/dailyaccountmake/valaccounthandle/ValAccountHandle001/isCheckBefore",{booksetId:$.trim($('#booksetId').val()),endDate:$('#executeDate').val()},function(msg){
                    if(msg==0){
                        bookSetIdChange();
                        dateChange();
                        doInter();
                    }else{
                        Joyin.confirm(JoyinUtil.getMessage('COMMON_I_0009', JoyinUtil
                            .getLabel('SysCommon.execute')), function(){
                            bookSetIdChange();
                            dateChange();
                            doInter();
                        });
                    }

                });

            }

        });

    }

    /**
     * 校验所选日期不能大于账套结束日
     */
    var checkDateForBookSetMdate = function(){
        // 校验所选日期不能大于账套结束日
        var isMoreMdate = false;
        var date = $('#executeDate').val();
        var booksetId=$.trim($('#booksetId').val());
        if(booksetId==''||booksetId==null){
            return true;
        }
        $.ajax({
            url : 'getMdateForBookSet',
            type : "POST",
            data : {booksetId:booksetId, prodGroupType:$("#booksets").val()},
            async: false,
            success: function(msg){
                // 检验是否有账套的到期日小于所选日期的
                if(msg != null && msg.length > 0){
                    for(var index = 0 ; index < msg.length ; index++){
                        if(new Date(date)>new Date(msg[index])){
                            isMoreMdate = true;
                            break;
                        }
                    }
                }
            },
            failure : function() {
            }
        });

        //当前选择日期大于所选账套结束日
        if(isMoreMdate){
            layer.alert('选择日期不能大于账套结束日！');
            $('#executeDate').val('');
            return true;
        }
    }

    /**
     * 画面加载完成
     */
    $(function() {
        $("#batchBooksetType").change(function() {
            $("#booksetId").val('');
            var batchBooksetType = $("#batchBooksetType").val();
            $("#booksets").val(batchBooksetType);
            if ('0' == batchBooksetType) {
                $("#dynamicName").html("账套组");
            } else {
                $("#dynamicName").html("账套");
            }
        });
        // 绑定查询按钮事件
        $('#ValAccountHandle001_btn_doInterBtn').click(function() {
            bokCheck();
            //bookSetIdChange();
            // dateChange();
            //doInter();
        });
        $('#checkBtn').click(function() {
            checkter(this);
        });
        $('#executeDate').change(function(){
            dateChange();
        });
        $('#booksetId').change(function(){
            gradeChange();
        });
        // 绑定查询按钮事件
        $('#stopInterBtn').click(function() {
            stopInter();
        });
        $('#log-title').click(function(){
            mainTable(this);
            $('#progressBar').show();
        });
        $('#checking-title').click(function(){
            checkter(this);
        });
        $('#ValAccountHandle001_btn_logBtn').click(function(){
            Joyin.dialog(JoyinUtil.getLabel("批处理日志"),
                JoyinUtil.getRealPath("/valuation/system/log/ValBatchLog001/init"),
                '100%', '100%', {pageModel:PAGE_MODEL_EDIT},
                function(){
                    //bondTable.reload();
                });
        });
        $('#success-title').click(function(){
            mainTable(this);
            $('#progressBar').hide();
        });
        $('#error-title').click(function(){
            mainTable(this);
            $('#progressBar').hide();
        });
        $('#warning-title').click(function(){
            mainTable(this);
            $('#progressBar').hide();
        });
        $('#eWarning-title').click(function(){
            mainTable(this);
            $('#progressBar').hide();
        });
        var tree=$("#typeDatatree");
        var docH= $(document).height();
        var tabHeadUlH=100;
        tree.children('div').eq(1).height((docH-tabHeadUlH)+"px");
        tree.children('div').eq(1).css("overflow-x", "hidden");

        $("body").on("click", ".taskName", function () {
            var seqNo = $(this).data("seqno");
            var index = Joyin.dialog("任务详情",    JoyinUtil.getRealPath("/valuation/system/log/ValBatchLog001/ValBatchLog003"), "100%", "100%", {logSeqNo: seqNo}, function () {

            }, {
                "关闭": function () {
                    layer.close(index);
                }
            })
        })

    });

});

function searchByBatchName() {
    var batchName=$('#batchName').val();
    $('#typeDatatree').reloadTree({batchName:batchName});
}
var openShow=function(){



}