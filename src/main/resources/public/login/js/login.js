
$(function() {
	if ($.cookie('loginName')) {
		$("#loginName")[0].value = $.cookie('loginName');
	}
	if ($.cookie('password')) {
		$("#password")[0].value = $.cookie('password');
		$("#checkPwd").attr("checked", 'true');
	}
	document.onkeydown = function(event) {
		e = event ? event : (window.event ? window.event : null);
		if (e.keyCode == 13) {
			Login();
		}
	}
});
//刷脸登陆函数
function faceLogin() {
	var loginName = $("#loginName").val();
	if(loginName==undefined||loginName==""){
		alert("请输入用户名才能使用刷脸登陆验证身份");
		return;
	}
	//打开电脑摄像头
	navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;  
    window.URL = window.URL || window.webkitURL || window.mozURL || window.msURL;  
    if (navigator.getUserMedia) {  
        navigator.getUserMedia({video:true}, successFunc, errorFunc);   //success是获取成功的回调函数  
    }  
    else {  
        alert('Native device media streaming (getUserMedia) not supported in this browser.');  
    }  
};
/**
 * 将base64图片流数据变为formData数据的文件流
 * @param base64Data
 * @returns {Blob}
 */
function dataURItoBlob(base64Data) {
	var byteString;
	if (base64Data.split(',')[0].indexOf('base64') >= 0)
	byteString = atob(base64Data.split(',')[1]);
	else
	byteString = unescape(base64Data.split(',')[1]);
	var mimeString = base64Data.split(',')[0].split(':')[1].split(';')[0];
	var ia = new Uint8Array(byteString.length);
	for (var i = 0; i < byteString.length; i++) {
	ia[i] = byteString.charCodeAt(i);
	}
	return new Blob([ia], {type:mimeString});
}
//调用摄像成功的回调函数
function successFunc(stream) {
	console.log("调用摄像头成功");
	//将faceId源指定为摄像头里内容
	if($("#faceId").attr("mozSrcObject")!==undefined){
		$("#faceId").attr("mozSrcObject",stream);
	}else{
		let faceId = document.getElementById('faceId');
		//兼容webkit核心浏览器
		  let CompatibleURL = window.URL || window.webkitURL;
		  //video.src = CompatibleURL.createObjectURL(stream);
		  faceId.srcObject = stream;
		  faceId.play();
	    
	}
	$("#faceId").show();
	var flag = true;
	if(flag){
    	//获得质量高的照片
        var interval = setInterval(function(){
        	var canvas = document.createElement("canvas");
    	    var video = document.getElementById("faceId");
    	    canvas.width=330;
    	    canvas.height=300;
    		canvas.getContext("2d").drawImage(video,0,0,330,300);
        	var img = canvas.toDataURL();//这里video截图的base64转码后的数据
        	var blob = dataURItoBlob(img);
        	var loginName = $("#loginName").val();
        	var fd = new FormData(document.forms[0]);
        	fd.append("faceimg",blob,'image.png');
        	fd.append("name",loginName);
        	$.ajax({
        		type:'post',
        		url:'login.do?faceLogin',
        		data:fd,
        		async:false,
        		processData: false,     // 必须
    	      	contentType: false,     // 必须
        		error:function(){},
        		success:function(data){
        			if(data.code==0){
        				var flag = false;
        				$("#password").val(data.data);
        				Login();
        				clearInterval(interval);
        			}else if(data.code==119||data.code==117||data.code==100){
        				//关闭摄像头,刷新页面
        				alert(data.msg);
        				window.location.reload();
        			}else{
        				if(data.msg!="照片不合格:nopersons"){
        					alert(data.msg);
        				}
        			}
        		}
        	});
        }, 1000);
    }
	//停留3s
}
//调用摄像头失败的函数
function errorFunc() {
	alert("调用摄像头失败");
	flag = false;
}
/**
 * 停留时长
 * @param numberMillis
 */
function sleep(numberMillis) { 
	var now = new Date(); 
	var exitTime = now.getTime() + numberMillis; 
	while (true) { 
	now = new Date(); 
	if (now.getTime() > exitTime) 
	return; 
	} 
	}
//登录处理函数
function Login() {
	var loginName = $("#loginName")[0].value,
		password = $("#password")[0].value;
	if(!check(loginName,password)){
		return;
	}
	if ($("#checkPwd").is(':checked')) {
		$.cookie('loginName', loginName, {
			expires : 1000
		});
		$.cookie('password', password, {
			expires : 1000
		});
	} else {
		$.cookie('loginName', null);
		$.cookie('password', null);
	}
	
	var formData = {
		name:loginName,
		password:password
	}
	$.ajax({
		async : true,
		cache : false,
		type : 'POST',
		url : "login.do?login",
		data : formData,
		error : function() {
		},
		success : function(data) {
			if (data.code==0) {
				//存储条件系统参数
				localStorage.setItem("appNames", JSON.stringify(data.data.appNames));
				localStorage.setItem("operateSystems", JSON.stringify(data.data.operateSystems));
				localStorage.setItem("appVersions", JSON.stringify(data.data.appVersions));
				window.location.href= "main.do?toMain";
			} else {
				alert(data.msg);
			}
		}
	});
};
function check(name,password){
	if(name==null||name==''){
		$('#loginName').focus();
		alert("用户名不同为空");
		return false;
	}else if(password==null||password==''){
		$('#password').focus();
		alert("密码不能为空");
		return false;
	}
	return true;
}










if(screen.width>820){
	treejsinit();
}


function treejsinit(){
	var SEPARATION = 100, AMOUNTX = 50, AMOUNTY = 50;

	var container;
	var camera, scene, renderer;

	var particles, particle, count = 0;

	var mouseX = -800, mouseY = -800;

	var windowHalfX = window.innerWidth / 2;
	var windowHalfY = window.innerHeight / 2;

	init();
	animate();

	function init() {

		container = document.createElement( 'div' );
		document.body.appendChild( container );

		camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 1, 10000 );
		camera.position.z = 1000;

		scene = new THREE.Scene();

		particles = new Array();

		var PI2 = Math.PI * 2;
		var material = new THREE.ParticleCanvasMaterial( {

			color: 0xffffff,
			program: function ( context ) {

				context.beginPath();
				context.arc( 0, 0, 1, 0, PI2, true );
				context.fill();

			}

		} );

		var i = 0;

		for ( var ix = 0; ix < AMOUNTX; ix ++ ) {

			for ( var iy = 0; iy < AMOUNTY; iy ++ ) {

				particle = particles[ i ++ ] = new THREE.Particle( material );
				particle.position.x = ix * SEPARATION - ( ( AMOUNTX * SEPARATION ) / 2 );
				particle.position.z = iy * SEPARATION - ( ( AMOUNTY * SEPARATION ) / 2 );
				scene.add( particle );

			}

		}

		renderer = new THREE.CanvasRenderer();
		renderer.setSize( window.innerWidth, window.innerHeight );
		container.appendChild( renderer.domElement );

		document.addEventListener( 'mousemove', onDocumentMouseMove, false );
		document.addEventListener( 'touchstart', onDocumentTouchStart, false );
		document.addEventListener( 'touchmove', onDocumentTouchMove, false );

		//

		window.addEventListener( 'resize', onWindowResize, false );

	}

	function onWindowResize() {

		windowHalfX = window.innerWidth / 2;
		windowHalfY = window.innerHeight / 2;

		camera.aspect = window.innerWidth / window.innerHeight;
		camera.updateProjectionMatrix();

		renderer.setSize( window.innerWidth, window.innerHeight );

	}

	//

	function onDocumentMouseMove( event ) {

		mouseX = 500 - windowHalfX;
		mouseY = -200 - windowHalfY;
		

		
		

	}

	function onDocumentTouchStart( event ) {

		if ( event.touches.length === 1 ) {
			// 判断默认行为是否可以被禁用
		    if (event.cancelable) {
		        // 判断默认行为是否已经被禁用
		        if (!event.defaultPrevented) {
		        	event.preventDefault();
		        }
		    }
			mouseX = event.touches[ 0 ].pageX - windowHalfX;
			mouseY = event.touches[ 0 ].pageY - windowHalfY;

		}

	}

	function onDocumentTouchMove( event ) {

		if ( event.touches.length === 1 ) {
			// 判断默认行为是否可以被禁用
		    if (event.cancelable) {
		        // 判断默认行为是否已经被禁用
		        if (!event.defaultPrevented) {
		        	event.preventDefault();
		        }
		    }
			mouseX = event.touches[ 0 ].pageX - windowHalfX;
			mouseY = event.touches[ 0 ].pageY - windowHalfY;

		}

	}

	//

	function animate() {

		requestAnimationFrame( animate );

		render();


	}

	function render() {

		camera.position.x += ( mouseX - camera.position.x ) * .05;
		camera.position.y += ( - mouseY - camera.position.y ) * .05;
		camera.lookAt( scene.position );

		var i = 0;

		for ( var ix = 0; ix < AMOUNTX; ix ++ ) {

			for ( var iy = 0; iy < AMOUNTY; iy ++ ) {

				particle = particles[ i++ ];
				particle.position.y = ( Math.sin( ( ix + count ) * 0.3 ) * 50 ) + ( Math.sin( ( iy + count ) * 0.5 ) * 50 );
				particle.scale.x = particle.scale.y = ( Math.sin( ( ix + count ) * 0.3 ) + 1 ) * 2 + ( Math.sin( ( iy + count ) * 0.5 ) + 1 ) * 2;

			}

		}

		renderer.render( scene, camera );

		count += 0.1;

	}
}


