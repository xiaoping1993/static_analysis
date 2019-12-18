var TYZX = parent.window.TYZX;
TYZX.versionSta={};

TYZX.versionSta.pieinit = function(target,appCode,appName,operateSystem){
	var msg = TYZX.Utils.MsgBox;
		startTime =TYZX.Utils.dataParse($("input#start+span>input:nth-child(2)").val());
		endTime = TYZX.Utils.dataParse($("input#end+span>input:nth-child(2)").val());
		if(startTime!=null||endTime!=null){
			if(startTime!=null&&endTime!=null){
				if(TYZX.Utils.compareDataParse(startTime,endTime)){msg.dialog("开始时间大于结束时间"); return;}
			}
		}
	$.ajax({
		async:false,
		cache:false,
		type:"post",
		url:"getVersionStaData",
		data:{appName:appCode,startTime:startTime,endTime:endTime},
		error:function(){
			msg.dialog("数据加载失败");
		},
		success:function(data){
			if(data.code==0||data.code==108){
				if(data.code==108){
					msg.dialog(appName+data.msg);
				};
				var serverData = data.data;
				var chart = new Highcharts.Chart({
				    chart: {
				        renderTo: target,
				        type:'pie',
				        height:480,
				    },
				    colors: ['#91c7ae', '#61a0a8', '#4B7D83','#305155'] ,
				    title:{
						text:appName,
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
							showInLegend:true,
							//size: 300
						}
					},
					
					legend:{
						itemWidth: 200,
						y:20,x:0,
					},
                   series:serverData
				});
			}else{
				msg.dialog("数据加载出错");
			}
		}
	});
};
TYZX.versionSta.init = function(){
	var msg = TYZX.Utils.MsgBox;
	TYZX.versionSta.pieinit("versionStaPie1",1,"天易在线");
	TYZX.versionSta.pieinit("versionStaPie2",2,"天易派工");
};
TYZX.versionSta.bind = function(){
	$("#search").bind('click',function(){
		TYZX.versionSta.pieinit("versionStaPie1",1,"天易在线");
		TYZX.versionSta.pieinit("versionStaPie2",2,"天易派工");
	});
};
(function(){
	TYZX.versionSta.init();
	TYZX.versionSta.bind();
}());
