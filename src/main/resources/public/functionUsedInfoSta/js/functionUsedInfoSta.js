var TYZX = parent.window.TYZX;
TYZX.functionUsedInfoSta={};
var msg = TYZX.Utils.MsgBox;


TYZX.functionUsedInfoSta.init = function(){
	//创建客户树
	var ownCustomer = tyControls.ownCustomer;
	ownCustomer.createOwnCustomer($('#customer'));
	$('.showResult2').hide();
	$('.timeing').hide();
	$('.customer').hide();
	TYZX.functionUsedInfoSta.InitShowResult1();
};
TYZX.functionUsedInfoSta.bind = function(){
	$("#cusTerTree").tree({
		onCheck:function(node,checked){
			var nodes = $("#cusTerTree").tree("getChecked"),
				length = nodes.length,
				cutomers="";
			for (var i = 0; i < length-1; i++) {
				cutomers+=nodes[i].text+",";
			}
			cutomers+=nodes[i].text;
			$("#searchCust").val(cutomers);
			$("#searchCust").attr("title",cutomers);
		}
	});
	$('.appName,.timeing').data({appName:$(".appName select").val(),timeing:$(".timeing select").val()}).bind('change',function(){
		var appNamelast = $(".appName,.timeing").data("appName"),
			timeinglast = $(".appName,.timeing").data("timeing");
		$('.appName,.timeing').data({appName:$(".appName select").val(),timeing:$(".timeing select").val()});
		if($('.showResult1').css('display')!='none'){
			TYZX.functionUsedInfoSta.InitShowResult1();
		}else{
			TYZX.functionUsedInfoSta.InitShowResult2(appNamelast,timeinglast);
		}
	});
	$('.pushClickRate').bind('click',function(){
		TYZX.functionUsedInfoSta.InitShowResult1();
		$('.showResult2').hide();
		$('.showResult1').show();
		$('.timeing').hide();
		$('.customer').hide();
	});
	$('.pageParkTimes').bind('click',function(){
		if($("#cusTerTree").tree("getChecked").length==0){
			$("#cusTerTree").tree("check",$("#cusTerTree").tree("getRoot").target);
		}
		TYZX.functionUsedInfoSta.InitShowResult2();
		$('.showResult1').hide();
		$('.showResult2').show();
		$('.timeing').show();
		$('.customer').show();
	});
	$('#close').bind('click',function(){
		$('.warnDetals').hide();
	});
	$("#search").click(function(){
		if($('.showResult1').css('display')!='none'){
			TYZX.functionUsedInfoSta.InitShowResult1();
		}else{
			TYZX.functionUsedInfoSta.InitShowResult2();
		}
	});
};
/**
 * 初始化推送点击率
 */
TYZX.functionUsedInfoSta.InitShowResult1 = function(){
	var appName = $('.appName select').val(),
		timing = $('.timeing select').val();
 	$("#dg").datagrid({
		striped:true,
		singleSelect:true,
		url:"getPushClickRateInfoData?appName="+appName,
		loadMsg:"数据加载中请稍后",
		pagination:true,
		pageSize:10,
		columns:[[
		   {field:'formatetime',title:'日期',align:'left',width:280},
		   {field:'appname',title:'App名称',align:'left',width:280,
			   formatter:function(value,row,index){
				   if(value=="1"){
					   return "天易在线"
				   }else if(value=="2"){
					   return "天易派工"
				   }
			   }},
		   {field:'pushtotals',title:'推送数量',align:'left',width:280},
		   {field:'clicktotals',title:'点击数量',align:'left',width:280},
		   {field:'rate',title:'点击率',align:'left',width:280,
			   formatter:function(value,row,index){
				   return (Math.round(value*10000)/100).toFixed(2)+"%";
			   }},
		   {field:'detail',title:'详细',align:'left',width:280,
			   formatter:function(value,row,index){
				   if(row.appname==1&&row.clicktotals!=0){
					   return "<a href='#' onclick='TYZX.functionUsedInfoSta.showWarnDetals("+row.appname+",\""+row.formatetime+"\")'>报警详细</a>";
				   }
			   }}
		]]
	});
};
/**
 * 打开一天报警类型详细信息
 */
TYZX.functionUsedInfoSta.showWarnDetals = function(appName,data){
	
	$('.warnDetals').show();
	$.ajax({
		async:true,
		cache:false,
		type:'post',
		url:'getwarnDetails',
		data:{appName:appName,data:data},
		error:function(){
			msg.dialog("数据加载失败");
		},
		success:function(data){
			if(data.code==0){
				var serverData = data.data;
				var charts = new Highcharts.Chart('warnPie', {
					title:{
						text:"报警详细信息饼状图",
					},
					credits: {
				        enabled: false
				    },
					tooltip:{
						headerFormat:'{series.name}<br>',
						pointFormat:'{point.name}:<b>{point.percentage:.1f}%</b>'
					},
					plotOptions:{
						pie:{
							allowPointSelect:true,
							cusor:'pointer',
							showInLegend:true
						}
					},
					series:[{
						type:'pie',
						name:'报警占比',
						data:serverData
					}]
				});
				
			}
		}
		
	});
};
/**
 * 初始化页面停留时长图表
 */
TYZX.functionUsedInfoSta.InitShowResult2 = function(appNamelast,timeinglast){
	TYZX.functionUsedInfoSta.getPageParktimeInfoData().then(function(data){//获取图表数据
		if(data.series.length==0){
			$(".appName select").val(appNamelast);
			$(".timeing select").val(timeinglast);
			msg.dialog("此条件下没有数据请重新选择");
			return;
		}
		//初始化图表
		$('#pageParking').highcharts({
	        chart: {
	            type: 'column'
	        },
	        title: {
	            text: '页面停留时长百分比堆叠柱形图'
	        },
	        xAxis: {
	            categories:data.categories 
	        },
	        yAxis: {
	            min: 0,
	            title:''
	        },
	        credits: {
		        enabled: false
		    },
	        tooltip: {
	        	pointFormatter: function() {
	        	    return '<span style="color: '+ this.series.color + '">\u25CF</span> '+
	        	           this.series.name+': <b>'+ TYZX.Utils.DateUtils.formatMssToDdhhmmss(this.y) +'</b><br/>'
	        	},
	            shared: true
	        },
	        plotOptions: {
	            column: {
	                stacking: 'percent',
	                pointWidth:50
	            }
	        },
	        series: data.series
	    });
	});
};
/**
 * 获得页面停留时间信息
 */
TYZX.functionUsedInfoSta.getPageParktimeInfoData = function(){
	var promise = new Promise(function(revole,reject){
		var appName = $('.appName select').val(),
			timing = $('.timeing select').val(),
			customerResult = tyControls.ownCustomer.getResult();
			if(customerResult.errorMsg!=null){
				msg.dialog(customerResult.errorMsg);
				return;
			}
		var cidArr = customerResult.cidArr.toString(),
			sonType = customerResult.sonType;
		$.ajax({
			async:true,
			type:'post',
			url:'getPageParktimeInfoData',
			data:{appName:appName,timing:timing,cidArr:cidArr,sonType:sonType},
			error:function(){},
			success:function(data){
				if(data.code==0){
					var serverData=data.data;
					revole(serverData);
				}else if(data.code==107){
					msg.dialog(data.msg);
				}else{
					msg.dialog("数据加载失败");
					return;
				}
			}
		});
	});
	return promise;
};
/**
 * 初始化条件参数
 */
TYZX.functionUsedInfoSta.initParams = function(){
	var appNames = JSON.parse(localStorage.getItem("appNames"));
	for (var i = 0; i < appNames.length; i++) {
		$('.appName select').append('<option value="'+appNames[i].Code+'">'+appNames[i].Name+'</options>');
	}
};
(function(){
	TYZX.functionUsedInfoSta.init();
	TYZX.functionUsedInfoSta.bind();
	TYZX.functionUsedInfoSta.initParams();
})();