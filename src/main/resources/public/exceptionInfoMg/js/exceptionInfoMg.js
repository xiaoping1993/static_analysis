var TYZX = parent.window.TYZX;
TYZX.exceptionInfoMg={};
var msg = TYZX.Utils.MsgBox;
var localPath =window.location.href.substring(0,window.location.href.indexOf(window.location.pathname))+"/statis_analysis";

TYZX.exceptionInfoMg.init = function(lastAppName,lastState,lastStart,lastEnd){
	var appName = $('.appName select').val();
		state = $('.state select').val(),
		start = ($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),
		end = ($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val();
		if(start==""){
			$("#start").focus();
			msg.dialog("请选择开始时间");
			return;
		}
		if(end==""){
			$("#end").focus();
			msg.dialog("请选择结束时间");
			return;
		}
		if(TYZX.Utils.DateUtils.compareDateForYyyymmdd(start,end)){
			TYZX.exceptionInfoMg.resetFilter(lastAppName,lastState,lastStart,lastEnd);
			msg.dialog("开始时间大于结束时间");
			return;
		}
	if($("#showResult1").css("display")!="none"){//错误类型显示
		$(".state").show();
		TYZX.exceptionInfoMg.getExceptionInfoByType(appName,state,start,end);
	}else if($("#showResult2").css("display")!="none"){//异常详细信息显示
		$(".state").show();
		TYZX.exceptionInfoMg.getExceptionInfo(appName,state,start,end,null);
	}else if($("#showResult3").css("display")!="none"){//奔溃机型显示
		$(".state").hide();
		TYZX.exceptionInfoMg.getMeltModelsInfo(appName,state,start,end);
	}
};
/**
 * 重置过滤条件为指定值
 * @param appName
 * @param state
 * @param start
 * @param end
 */
TYZX.exceptionInfoMg.resetFilter =function(appName,state,start,end){
	$("#appName select").val(appName);
	$("#state select").val(state);
	$("#start+span input:nth-child(2)").val(start);
	$("#end+span input:nth-child(2)").val(end);
}
/**
 * 展示奔溃机型柱状图
 */
TYZX.exceptionInfoMg.getMeltModelsInfo = function(appName,state,start,end){
	$.ajax({
		async:true,
		cache:false,
		type:"post",
		url:"getMeltModelsInfo",
		data:{appName:appName,state:state,start:start,end:end},
		error:function(){
			msg.dialog("数据没有加载成功");
		},
		success:function(data){
			if(data.code==0){
				$("#meltmodels").highcharts({
					chart:{
						type:'column'
					},
					title:{
						text:'崩溃机型统计'
					},
					xAxis:{
						categories:data.data.categories
					},
					yAxis:{
						title:{text:'总数'}
					},
					credits:{
						enabled:false
					},
					tooltip:{
						pointFormat: '机型总数：<b>{point.y}</b><br/>',
			            shared: true
					},
					series:[{
						data:data.data.series
					}],
					legend:{enabled:false},
					plotOptions: {
			            column: {
			                pointWidth:50
			            }
			        },
				});
			}else{
				msg.dialog(data.msg);
				return;
			}
		},
	});
};
/**
 * 展示具体详细错误页面
 * @param ids
 */
TYZX.exceptionInfoMg.showDetails = function(appName,state,start,end,ids){
	$("#showResult1").hide();
	$("#showResult2").show();
	$("#typebtn").removeClass("btn-act");
	$("#detailsbtn").addClass("btn-act");
	TYZX.exceptionInfoMg.getExceptionInfo(appName,state,start,end,ids);
}
/**
 * 加载错误类型统计
 * @param appName
 * @param state
 * @param start
 * @param end
 */
TYZX.exceptionInfoMg.getExceptionInfoByType = function(appName,state,start,end){
	$("#dgType").datagrid({
		singleSelect:true,
		url:"getExceptionInfoByType?appName="+appName+"&state="+state+"&start="+start+"&end="+end,
		loagMag:"数据加载中请稍后。。。。",
		columns:[[
		   {field:'type',title:"错误类型",align:'left',width:200,
			   formatter:function(value,row,index){
		    		 if(value==undefined){
		    			 value="";
		    		 }
		    		 return "<span title='"+value+"'>"+value+"</span>";
		    	 }},
		   {field:"count",title:"数量",align:'left',width:200},
		   {field:"ids",title:"详细",align:'left',width:200,
			   formatter:function(value,row,index){
				   return "<a href='#' onclick='TYZX.exceptionInfoMg.showDetails(\"all\",\"all\",null,null,\""+value+"\")'>详细</a>"
			   }}
		]]
	});
}

/**
 * 	加载错误详细信息
 * @param appName
 * @param state
 * @param start
 * @param end
 */
TYZX.exceptionInfoMg.getExceptionInfo = function(appName,state,start,end,ids){
	$('#dgDetails').datagrid({
		singleSelect:true,
		url:"getExceptionInfo?appName="+appName+"&state="+state+"&start="+start+"&end="+end+"&ids="+ids,
		loagMsg:"数据加载中请稍后。。。。",
		pagination:true,
		pageSize:10,
		columns:[[
		     {field:'date',title:'日期',align:'left',width:200},
		     {field:'appname',title:'应用名称',align:'left',width:200,
		    	 formatter:function(value,row,index){
		    		if(value=="1"){
		    			return "天易在线"
		    		}else if(value=="2"){
		    			return "天易派工"
		    		}else if(value="4"){
		    			return "天易在线服务端"
		    		}else if(value="5"){
		    			return "天易派工服务端"
		    		}else if(value="-1"){
		    			return "其他";
		    		}
		    	 }},
		     {field:'version',title:'版本号',align:'left',width:200},
		     {field:'exception_msg',title:'异常概述',align:'left',width:200,
		    	 formatter:function(value,row,index){
		    		 if(value==undefined){
		    			 value="";
		    		 }
		    		 return "<span title='"+value+"'>"+value+"</span>";
		    	 }},
		     {field:'mphone_mode',title:'手机型号',align:'left',width:200},
		     {field:'state',title:'状态',align:'left',width:200,
		    	 formatter:function(value,row,index){
		    		 if(value=="1"){
		    			 return "待解决"
		    		 }else if(value=="2"){
		    			 return "已解决"
		    		 }else{
		    			 return "挂起"
		    		 }
		    	 }},
		     {field:'operatesystem',title:'责任人',align:'left',width:200,
		    	 formatter:function(value,row,index){
		    		 if(value=="1"){
		    			 return "胡梅";
		    		 }else if(value=="2"){
		    			 return "谢鹏";
		    		 }else{
		    			 if(row.appname==4||row.appname==5){
		    				 return "朱俊斌";
		    			 }
		    		 }
		    	 }},
		    {field:'exception_detailmsgstring',title:'详细信息',align:'left',width:200,
		    		 formatter: function(value,row,index){
		    			 if(value==undefined){
		    				 value="";
		    			 }
	                     return "<span title='"+value+"'>"+value+"</span>";    
	                }
		    },
		    {field:'exception_detailmsgfile',title:'详细信息',align:'left',width:200,
	    		 formatter:function(value,row,index){
	    			 if(value==undefined){
	    				 return "";
	    			 }
			 		var url = localPath+"/exceptionInfoMg/downAppErrorFiles?fileName="+value;
					 return "<a href='"+url+"'>附件</a>";
				 }
		    }
		]]
	});
};

TYZX.exceptionInfoMg.bind = function(){
	$('.appName,.state').data({appName:$('.appName select').val(),state:$('.state select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()}).bind('change',function(){
		var lastAppName = $("#search").data("appName"),
			lastState = $("#search").data("state"),
			lastStart = $("#lastStart").data("start"),
			lastEnd = $("#lastEnd").data("end");
		TYZX.exceptionInfoMg.init(lastAppName,lastState,lastStart,lastEnd);
	});
	$("#returnType").click(function(){
		$("#showResult2").hide();
		$("#showResult1").show();
	});
	$("#cancel").bind("click",function(){
		$(".model").hide();
	});
	$("#resolve").bind("click",function(){
		
		var row = $("#dgDetails").datagrid("getSelected");
		if(row==null){
			msg.dialog("请选中列对象");
		}
		if(row.resolvemethod!=undefined){
			$("#method").val(row.resolvemethod);
		}else{
			$("#method").val("");
		}
		if(row.reasons!=undefined){
			$("#reasons").val(row.reasons)
		}else{
			$("#reasons").val("");
		}
		if(row.resolvepersons!=undefined){
			$("#resolvepersons").val(row.resolvepersons)
		}else{
			$("#resolvepersons").val("");
		}
		if(row.exception_detailmsgfile==undefined){
			$("#problemDescription").text("概述："+(row.exception_msg==undefined?"":row.exception_msg)).append("<br/>详情："+(row.exception_detailmsgstring==undefined?"":row.exception_detailmsgstring));
		}else{
			var url = localPath+"/exceptionInfoMg/downAppErrorFiles?fileName="+row.exception_detailmsgfile;
			$("#problemDescription").text("概述："+(row.exception_msg==undefined?"":row.exception_msg)).append("<br/>详情："+(row.exception_detailmsgstring==undefined?"":row.exception_detailmsgstring)).append("<br/><a href='"+url+"'>附件</a>");
		}
		$("#exceptionId").text(row.id);
		$(".model").show();
	});
	$("#resolved").bind("click",function(){
		msg.twoAffirm("请确认是否将此异常标记为解决").then(function(data){
			if(data){
				var reasons = $("#reasons").val(),
				method = $("#method").val(),
				resolvepersons=$("#resolvepersons").val(),
				id = $("#exceptionId").text();
				$.ajax({
					async:true,
					cache:false,
					type:"post",
					url:"doProblem",
					data:{id:id,state:2,reasons:reasons,resolvemethod:method,resolvePersons:resolvepersons},
					error:function(){},
					success:function(data){
						if(data.code==0){
							msg.light("问题已标记为解决！",2);
							$(".model").hide();
							$("#dgDetails").datagrid("reload");
						}else{
							msg.dialog(data.msg);
						}
					}
				});
			}
		});
	});
	$("#hangup").bind("click",function(){
		msg.twoAffirm("请确认是否将此问题标记为挂起").then(function(data){
			if(data){
				var reasons = $("#reasons").text(),
				method = $("#method").text(),
				resolvepersons=$("#resolvepersons").val(),
				id = $("#exceptionId").text();
				$.ajax({
					async:true,
					cache:false,
					type:"post",
					url:"doProblem",
					data:{id:id,state:3,reasons:reasons,resolvemethod:method},
					error:function(){},
					success:function(data){
						if(data.code==0){
							msg.light("问题已标记为挂起！",2);
							$(".model").hide();
							$("#dgDetails").datagrid("reload");
						}else{
							msg.dialog(data.msg);
						}
					}
				});
			}
		});
	});
	$("#typebtn").data({appName:$('.appName select').val(),state:$('.state select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()}).click(function(){//显示错误类型统计
		var lastAppName = $("#typebtn").data("appName"),
			lastState = $("#typebtn").data("state"),
			lastStart = $("#typebtn").data("start"),
			lastEnd = $("#typebtn").data("end");
		$("#typebtn").data({appName:$('.appName select').val(),state:$('.state select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()})
		$("#showResult2").hide();
		$("#showResult3").hide();
		$("#showResult1").show();
		TYZX.exceptionInfoMg.init(lastAppName,lastState,lastStart,lastEnd);
	});
	$("#detailsbtn").data({appName:$('.appName select').val(),state:$('.state select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()}).click(function(){//显示错误详细统计
		var lastAppName = $("#detailsbtn").data("appName"),
			lastState = $("#detailsbtn").data("state"),
			lastStart = $("#detailsbtn").data("start"),
			lastEnd = $("#detailsbtn").data("end");
		$("#detailsbtn").data({appName:$('.appName select').val(),state:$('.state select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()})
		$("#showResult1").hide();
		$("#showResult3").hide();
		$("#showResult2").show();
		$("#returnType").hide();
		TYZX.exceptionInfoMg.init(lastAppName,lastState,lastStart,lastEnd);
	});
	$("#meltmodelsbtn").data({appName:$('.appName select').val(),state:$('.state select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()}).click(function(){//显示奔溃机型统计
		var lastAppName = $("#meltmodelsbtn").data("appName"),
			lastState = $("#meltmodelsbtn").data("state"),
			lastStart = $("#meltmodelsbtn").data("start"),
			lastEnd = $("#meltmodelsbtn").data("end");
		$("#meltmodelsbtn").data({appName:$('.appName select').val(),state:$('.state select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()})
		$("#showResult1").hide();
		$("#showResult2").hide();
		$("#showResult3").show();
		TYZX.exceptionInfoMg.init(lastAppName,lastState,lastStart,lastEnd);
	});
	$("#search").data({appName:$('.appName select').val(),state:$('.state select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()}).click(function(){
		var lastAppName = $("#search").data("appName"),
			lastState = $("#search").data("state"),
			lastStart = $("#search").data("start"),
			lastEnd = $("#search").data("end");
		$("#search").data({appName:lastAppName,state:lastState,start:lastStart,end:lastEnd});
		TYZX.exceptionInfoMg.init(lastAppName,lastState,lastStart,lastEnd);
	});
};
/**
 * 初始化参数
 */
TYZX.exceptionInfoMg.initParam = function(){
	var formatDate = TYZX.Utils.DateUtils.formatDate;
	var today = new Date();
	$("#start").val("2016-01-01");
	$("#end").val(formatDate(today,"yyyy-mm-dd"));
};
(function(){
	TYZX.exceptionInfoMg.initParam();
	TYZX.exceptionInfoMg.init();
	TYZX.exceptionInfoMg.bind();
}())