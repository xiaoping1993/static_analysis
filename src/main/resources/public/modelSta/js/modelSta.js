var TYZX = parent.window.TYZX;
TYZX.modelSta={};

TYZX.modelSta.pieinit = function(source,$target,name,appNamelast,operateSystemlast){
	var msg = TYZX.Utils.MsgBox;
		appName = $(".appName select").val(),
		operateSystem = $(".operateSystem select").val(),
		result=true;
	$.ajax({
		async:false,
		cache:false,
		type:"post",
		url:"getModelStaData",
		data:{appName:appName,operateSystem:operateSystem,others:source},
		error:function(){
			result=false;
			msg.dialog("数据加载失败");
		},
		success:function(data){
			if(data.code==0){
				var serverData = data.data.sort(function(a,b){return b.y-a.y}),
					serverDataLength = serverData.length,
					serverDataOther = 0;
					serverData1=serverData.slice(0,20);
				if(serverDataLength>20){
					for (var i = 20; i < serverData.length; i++) {
						serverDataOther+=serverData[i].y;
					}
					serverData1.push(new Object({"name":"其他","y":Number(serverDataOther.toFixed(2))}));
				}
				var charts = new Highcharts.Chart($target, {
					title:{
						text:name+"统计",
						floating:true
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
							size:260
						}
					},
					series:[{
						type:'pie',
						innerSize:'60%',
						name:name+"统计",
						data:serverData1
					}],
					legend:{
						itemWidth: 200,
						y:-350,x:0,
						labelFormatter: function () {  
                            return this.name +"\t\t"+ this.y+'%';
                        } 
					}
				});
				var centerY = charts.series[0].center[1],
	            	titleHeight = parseInt(charts.title.styles.fontSize);
				charts.setTitle({
		            y:centerY + titleHeight/2
		        });
				//数据获得成功后将现有数据存入jquery对象
				$(".appName,.operateSystem").data({"appName":$(".appName select").val(),"operateSystem":$(".operateSystem select").val()})
			}else if(data.code==111){
				result=false;
				$(".appName select").val(appNamelast),
				$(".operateSystem select").val(operateSystemlast);
				msg.dialog(data.msg);
			}else{
				result=false;
				msg.dialog("数据加载出错");
			}
		}
	});
	return result;
};
/**
 * 初始化条件参数
 */
TYZX.modelSta.initParams = function(){
	var appNames = JSON.parse(localStorage.getItem("appNames"));
	for (var i = 0; i < appNames.length; i++) {
		$('.appName select').append('<option value="'+appNames[i].Code+'">'+appNames[i].Name+'</options>');
	}
	var operateSystems = JSON.parse(localStorage.getItem("operateSystems"));
	for (var i = 0; i < operateSystems.length; i++) {
		$('.operateSystem select').append('<option value="'+operateSystems[i].Code+'">'+operateSystems[i].Name+'</options>');
	}
};
TYZX.modelSta.init = function(){
	var msg = TYZX.Utils.MsgBox;
	TYZX.modelSta.pieinit("mphone_mode","modelStaPie1","机型");
	TYZX.modelSta.pieinit("mphone_brand","modelStaPie2","品牌");
};
TYZX.modelSta.bind = function(){
	$(".appName,.operateSystem").data({"appName":$(".appName select").val(),"operateSystem":$(".operateSystem select").val()}).bind('change',function(){
		var appNamelast = $(this).data("appName");
		var operateSystemlast = $(this).data("operateSystem");
		//存储改变之后的值便于下次改变找改变之前的值
		if(TYZX.modelSta.pieinit("mphone_mode","modelStaPie1","机型",appNamelast,operateSystemlast)){//机型统计能加载到数据品牌统计才去初始化
			TYZX.modelSta.pieinit("mphone_brand","modelStaPie2","品牌",appNamelast,operateSystemlast);
		}
	});
};
(function(){
	TYZX.modelSta.init();
	TYZX.modelSta.bind();
	TYZX.modelSta.initParams();
}());	
