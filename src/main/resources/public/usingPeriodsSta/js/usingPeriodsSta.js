var TYZX = parent.window.TYZX;
TYZX.usingPeriodsSta={};
var msg = TYZX.Utils.MsgBox;



TYZX.usingPeriodsSta.init = function(lastAppName,lastTimeParticle,lastStart,lastEnd){
	TYZX.usingPeriodsSta.initShowResult(lastAppName,lastTimeParticle,lastStart,lastEnd);
};
/**
 * 用户使用时段初始化图表
 */
TYZX.usingPeriodsSta.initShowResult = function(lastAppName,lastTimeParticle,lastStart,lastEnd){
	TYZX.usingPeriodsSta.getUsingPeriodsData(lastAppName,lastTimeParticle,lastStart,lastEnd).then(function(data){//获取图片数据
		console.log(data);
		$('#usingPeriodsArea').highcharts({
	        chart: {
	            type: 'area'
	        },
	        title: {
	            text: '用户使用时段统计'
	        },
	        xAxis: {
	            type:'datetime',
	            maxZoom:24 * 3600 * 1000, // x轴总共显示的时间
	            dateTimeLabelFormats: {
	                minute: '%H:%M',
	                day: '%H:%M'
	            }
	        },
	        yAxis: {
	            title: {
	                text: '使用人数'
	            }
	        },
	        legend:{
	        	enabled:false
	        },
	        credits: {
		        enabled: false
		    },
	        tooltip: {
	        	xDateFormat: '%H:%M',
		        pointFormat: '{series.name}:<b>{point.y}</b>'
	        },
	        plotOptions: {
	            area: {
	                pointStart: 0,
	                marker: {
		                enabled: false,
		                symbol: 'circle',
		                radius: 2,
		                states: {
		                    hover: {
		                        enabled: true
		                    }
		                }
		            }
	            }
	        },
	        series: [{
	            name: '在线人数',
	            data: data.series,
	            pointInterval:data.timeParticle*60*1000//timeParticle min
	        }]
	    });
	});
};
/**
 * 初始化条件参数
 */
TYZX.usingPeriodsSta.initParams = function(){
	var appNames = JSON.parse(localStorage.getItem("appNames")),
		formatDate = TYZX.Utils.DateUtils.formatDate,
		yesterday = new Date();
		yesterday.setDate(yesterday.getDate()-1);
	var yesterdayFomart = formatDate(yesterday,"yyyy-mm-dd");
	for (var i = 0; i < appNames.length; i++) {
		$('.appName select').append('<option value="'+appNames[i].Code+'">'+appNames[i].Name+'</options>');
	}
	$('#start').val(yesterdayFomart);
	$('#end').val(yesterdayFomart);
};

/**
 * 加载使用使用时段数据
 * @returns {Promise}
 */
TYZX.usingPeriodsSta.getUsingPeriodsData = function(lastAppName,lastTimeParticle,lastStart,lastEnd){
	var appName = $('.appName select').val();
		timeParticle = $('.timeParticle select').val(),
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
			TYZX.usingPeriodsSta.resetFilter(lastAppName,lastTimeParticle,lastStart,lastEnd);
			msg.dialog("开始时间大于结束时间");
			return;
		}
	var promise = new Promise(function(revole,reject){
		$.ajax({
			async:true,
			cache:false,
			type:'post',
			data:{appName:appName,timeParticle:timeParticle,start:start,end:end},
			url:"getUsingPeriodsData",
			error:function(){
				msg.dialog("数据加载出错");
			},
			success:function(data){
				if(data.code==0){
					revole({series:data.data,timeParticle:timeParticle});
				}else{
					msg.dialog(data.msg);
				}
			}
		});
	});
	return promise;
};
/**
 * 重置过滤条件
 * @param lastAppName
 * @param lastState
 * @param lastStart
 * @param lastEnd
 */
TYZX.usingPeriodsSta.resetFilter = function(lastAppName,lastTimeParticle,lastStart,lastEnd){
	$(".appName select").val(lastAppName);
	$(".lastTimeParticle select").val(lastTimeParticle);
	$("input#start+span>input:nth-child(2)").val(lastStart);
	$("input#end+span>input:nth-child(2)").val(lastEnd);
};
TYZX.usingPeriodsSta.bind = function(){
	$('.appName,.timeParticle').data({appName:$('.appName select').val(),timeParticle:$('.timeParticle select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()}).bind('change',function(){
		var lastAppName = $('.appName,.timeParticle').data("appName"),
			lastTimeParticle = $('.appName,.timeParticle').data("timeParticle"),
			lastStart = $('.appName,.timeParticle').data("start"),
			lastEnd = $('.appName,.timeParticle').data("end");
		$('.appName,.timeParticle').data({appName:$('.appName select').val(),timeParticle:$('.timeParticle select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()});
		TYZX.usingPeriodsSta.init(lastAppName,lastTimeParticle,lastStart,lastEnd);
	});
	$('#search').data({appName:$('.appName select').val(),timeParticle:$('.timeParticle select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()}).click(function(){
		var lastAppName = $('#search').data("appName"),
			lastTimeParticle = $('#search').data("timeParticle"),
			lastStart = $('#search').data("start"),
			lastEnd = $('#search').data("end");
			$('#search').data({appName:$('.appName select').val(),timeParticle:$('.timeParticle select').val(),start:($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),end:($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val()});
		TYZX.usingPeriodsSta.init(lastAppName,lastTimeParticle,lastStart,lastEnd);
	});
};

(function(){
	TYZX.usingPeriodsSta.initParams();
	TYZX.usingPeriodsSta.init();
	TYZX.usingPeriodsSta.bind();
	//格式化日期显示格式
	$.fn.datebox.defaults.formatter = function(date){
		return TYZX.Utils.DateUtils.formatDate(date,"yyyy-mm-dd");
	}
}());