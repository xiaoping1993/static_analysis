var TYZX = parent.window.TYZX;
TYZX.userMg = {};
var msg = TYZX.Utils.MsgBox;
/*
 * userMg中的常量标识
 */
TYZX.userMg.Identifications={};
//配置角色分配已有角色是否已选择标识
TYZX.userMg.Identifications.ybpeizhi=false;
TYZX.userMg.Identifications.successpz=false;
TYZX.userMg.Identifications.bgpeizhi=false;
TYZX.userMg.resetPwd = function(userId,userName) {
	msg.twoAffirm("请确认是否重置密码").then(function(data){
		if(data){
			$.ajax({
				async:true,
				cache:false,
				type:"post",
				url:"../userMg/resetPwd",
				data:{userId:userId,userName:userName},
				error:function(){
					msg.dialog("修改密码失败");
				},
				success:function(data){
					if(data.code==0){
						msg.light("修改密码成功新密码为：12345678",2);
					}else{
						msg.dialog("修改密码失败");
					}
				}
			});
		}
	});
};

TYZX.userMg.deleUser = function(userId,userName) {
	msg.twoAffirm("请确认是否删除").then(function(data){
		if(data){
			if(userId==undefined){
				var row = $('#userdg').datagrid('getSelected');
				if (row){
					var index = $('#userdg').datagrid('getRowIndex', row);
					$('#userdg').datagrid('deleteRow', index);
				}
				return;
			}
			$.ajax({
				async:true,
				cache:false,
				type:"post",
				url:"../userMg/deleUser",
				data:{userId:userId,userName:userName},
				error:function(){
					msg.dialog("用户删除失败");
				},
				success:function(data){
					if(data.code==0){
						msg.light("用户删除成功",2);
						$('#userdg').datagrid('reload');
					}else{
						msg.dialog(data.msg);
					}
				}
			});
		}
	});
};
TYZX.userMg.configRole = function(roleId,userId,loginName) {
	$.ajax({
		async:true,
		cache:false,
		type:"post",
		url:"../userMg/getRoleIds",
		data:{userId:userId},
		error:function(){
		},
		success:function(data){
			TYZX.userMg.Identifications.ybpeizhi=true;//异步数据已加载
			$('#role_ids').text(data);
			//选中用户已拥有角色
			//如果表加载完成执行这里
			var roleIds = new Array(),
				rows = $('#roleList').datagrid('getRows');
			roleIds = $('#role_ids').text().split(',');
			if(!TYZX.userMg.Identifications.bgpeizhi&&!TYZX.userMg.Identifications.successpz){
				for(var i = 0; i < rows.length; i++){
					if(roleIds.indexOf(''+rows[i].id+'')!=-1	){
						$('#roleList').datagrid('selectRow', i);
					}
				}
				TYZX.userMg.Identifications.successpz=true;
			}else{
				TYZX.userMg.Identifications.successpz=false;
			}
		}
	});
	$('#configRole').modal('show');
	$('#myModalLabel').text("为"+loginName+"配置角色");
	$('#user_id').text(userId);
	$('#role_id').text(roleId);
};
TYZX.userMg.init = function(){
	//加载用户表
	$('#userdg').datagrid({
		striped: true,
		singleSelect : true,
		url : "findAllUser",
		loadMsg:'数据加载中请稍后……',
		pagination : true,
		pageList:[10,20,30,40,50],
		pageSize:10,
		columns:[[  
	        {field:'username',title: "账号",align: 'left', width:280,editor:'text'},  
			{field:'rolename',title: "角色",align: 'left', width:280},
	        {field:'edit',title: "操作",align: 'left', width:430,
				formatter:function(value,row,index){
					return '<a onclick="TYZX.userMg.resetPwd('+row.userid+',\''+row.username+'\')">重置密码，</a>'+
						   '<a onclick="TYZX.userMg.deleUser('+row.userid+',\''+row.username+'\')">删除，</a>'+
						   '<a onclick="TYZX.userMg.configRole(\''+row.roleid+'\','+row.userid+',\''+row.username+'\')">配置角色，</a>'+
						   '<a onclick="TYZX.userMg.popupModifyPwd(\''+row.userid+'\',\''+row.username+'\')">修改密码，</a>'+
						   (row.hasfaceregister==1?'头像已上传':'<span>上传头像（300*400,2M,jpg）</span><input type="file" onchange="TYZX.userMg.RegisterByPhoto(event,'+row.userid+',\''+row.username+'\')" class="upload" style="display: inline-block;">');
				}
			}
	    ]]
	});
	//加载角色
	TYZX.userMg.getRoles();
	//加载权限
	$.ajax({
		async:true,
		cache:false,
		type:"post",
		url:"getAuthoritys",
		error:function(){
			msg.dialog("权限加载失败");
		},
		success:function(data){
			if(data.code==0){
				for(var i=0;i<data.data.length;i++){
					$('#authoritys').append("<label class='checkbox-inline'>" +
							"<input type='checkbox' id='inlineCheckbox2' value='"+data.data[i].id+"'> "+data.data[i].name+
							"</label>")
				}
			}
		}
	});
};
/**
 * 弹出修改密码
 */
TYZX.userMg.popupModifyPwd = function(id,name){
	$("#modifyPwd").data({id:id,name:name}).modal("show");
};
/**
 * 管理员在不知道原密码情况下修改对应用户密码
 * @param id
 * @param pwd
 */
TYZX.userMg.modifyPwd = function(id,name,pwd){
	$.ajax({
		async:true,
		cache:false,
		type:"post",
		url:"modifyPwd",
		data:{id:id,pwd:pwd,name:name},
		error:function(){
			msg.dialog("密码修改失败");
		},
		success:function(data){
			if(data.code==0){
				msg.light(name+"密码修改为："+pwd);
			}else{
				msg.dialog(name+"密码修改失败");
			}
		}
	});
};
/**
 * 加载所有角色
 */
TYZX.userMg.getRoles = function(){
	$("#roleMg tbody:first").empty();//加载所有角色之前需要现有子元素
	$.ajax({
		async:true,
		cache:false,
		type:"post",
		url:"getRoles",
		error:function(){
			msg.dialog("角色加载失败");
		},
		success:function(data){
			for(var i=0;i<data.rows.length;i++){
				$("#roleMg tbody:first").append("<tr><td value='"+data.rows[i].id+"'>"+data.rows[i].name+"</td><td><a onclick='TYZX.userMg.deleRole(\""+data.rows[i].id+"\",\""+data.rows[i].name+"\")'>删除</a></td></tr>")
			}
		}
	});
};
/**
 * 删除角色
 */
TYZX.userMg.deleRole = function(id,name){
	msg.twoAffirm("请确认是否删除").then(function(data){
		if(data){
			$.ajax({
				async:true,
				cache:false,
				type:"post",
				url:'deleRole',
				data:{id:id},
				error:function(){
					msg.dialog("删除角色:"+name+"失败");
				},
				success:function(data){
					if(data.code==0){
						TYZX.userMg.getRoles();
						$("#userdg").datagrid("reload");
						msg.light("删除角色:"+name+"成功",2);
					}else{
						msg.dialog("删除角色:"+name+"失败");
					}
				}
			});
		}
	});
};
/**
 * 人脸注册，拍照采集人脸照片
 */
TYZX.userMg.faceRegisterByCamera = function() {
	//先不做
}
/**
 * 人脸注册，图片上传采集图片
 */
TYZX.userMg.RegisterByPhoto = function(e,id,username){
    for (var i = 0; i < e.target.files.length; i++) {  
      var file = e.target.files.item(i);            
      var freader = new FileReader();  
      freader.readAsDataURL(file);  
      freader.onload = function(e) {  
        var src = e.target.result;
        var blob = dataURItoBlob(src);
        var fd = new FormData();
    	fd.append("faceimg",blob,'image.png');
    	fd.append("userid",id);
    	fd.append("username",username);
    	$.ajax({
    		type:'post',
    		url:'../userMg/registerPhoto',
    		data:fd,
    		async:false,
    		processData: false,     // 必须
	      	contentType: false,     // 必须
    		error:function(){},
    		success:function(data){
    			if(data.code==0){
    				msg.light("上传成功");
    				$('#userdg').datagrid('reload');
    			}else{
    				msg.dialog(data.msg);
    			}
    		}
      });
      }
      }
}
TYZX.userMg.bind = function() {
	$('#configRole').on('shown.bs.modal', function () {
		//获得这个用户拥有的所有角色
		$('#roleList').datagrid({
			url : "getRoles",
			loadMsg:'数据加载中请稍后……',
			idField:"id",
			columns:[[  
				{field:"name",title: "角色", width:320},
		    ]],
		    onLoadSuccess:function(data){
		    	TYZX.userMg.Identifications.bgpeizhi=true;//表格已加载
		    	//解决easyui与bootstrap的兼容问题
				$('#roleList .datagrid-view').css('height','500px');
				$('#roleList .datagrid-header').css('height','25px');
				var rows = $('#roleList').datagrid('getRows'),
					roleIds = $('#role_ids').text().split(',');
				if(TYZX.userMg.Identifications.ybpeizhi&&!TYZX.userMg.Identifications.successpz){
					for(var i = 0; i < rows.length; i++){
						if(roleIds.indexOf(''+rows[i].id+'')!=-1	){
							$('#roleList').datagrid('selectRow', i);
						}
					}
					TYZX.userMg.Identifications.successpz=true;
				}
				
		    }
		});
	});
	$('#configRole').on('hidden.bs.modal',function(){
		//模态框关闭时一定要清除所有easyui表的已选项，不然下次还会出现
		$('#roleList').datagrid('clearSelections');
		TYZX.userMg.Identifications.bgpeizhi=false;
		TYZX.userMg.Identifications.successpz=false;
		TYZX.userMg.Identifications.ybpeizhi=false;
		$('#addUserRole').css('display','none');
		$('#user_id').text("");
		$('#role_id').text("");
		$('#role_ids').text("");
	});
	$('#configtj').click(function(){
			rows = JSON.stringify($('#roleList').datagrid('getSelections')),
			userId = $('#user_id').text();
			userName = $('#addUserRole').val().trim();
		$.ajax({
			async:true,
			cache:false,
			type:"post",
			url:"../userMg/configsRoles",
			data:{rows:rows,userId:userId,userName:userName},
			error:function(){
				msg.dialog("角色配置失败");
			},
			success:function(data){
				if(data.code==0){
					msg.light("角色配置成功",2);
					$('#userdg').datagrid('reload');
				}else{
					msg.dialog(data.msg);
				}
			}
		});
		$('#configRole').modal('hide');
	});
	$('#addUser').click(function(){
		$('#configRole').modal("show");
		$('#addUserRole').css('display','inline-block');
	});
	$('#addRole').click(function(){
		$('#addRoleModel').modal("show");
		$('#addRoleModel').css('display','inline-block');
	});
	$('#addRoleModelComplete').click(function(){
		var roleName = $("#roleName").val(),
			roleDescribe = $("#roleDescribe").val();
		if(roleName==""||roleName==undefined){
			msg.dialog("请输入新增角色名称");
			return;
		}
		$.ajax({
			async:true,
			cache:false,
			type:"post",
			url:"addRole",
			data:{roleName:roleName,roleDescribe:roleDescribe},
			error:function(){
				msg.dialog("角色新增失败");
			},
			success:function(data){
				if(data.code==0){
					TYZX.userMg.getRoles();//加载角色
					$("#addRoleModel").modal('hide');
					msg.light("角色新增成功",2);
				}else{
					msg.dialog("角色新增失败");
				}
			}
		});
	});
	$('#addRoleModelCancel').click(function(){
		$("#addRoleModel").modal('hide');
	});
	$("#roleMg").delegate('td', 'click', function(){
		$("#roleMg td").removeClass("active");
		$('#authoritys input').prop('checked', false);
		$(this).toggleClass("active");
		//右侧权限填充对应内容
		var role_id = $(this).attr("value");
		$.ajax({
			async:true,
			cache:false,
			type:"post",
			url:"getAuthoritysByRole",
			data:{roleId:role_id},
			error:function(){
				msg.dialog("权限加载失败");
			},
			success:function(data){
				if(data.code==0){
					$("#myRole_id").text(role_id);
					var length = $('#authoritys input').length
					for(var i=0;i<data.data.length;i++){
						for(var j=0;j<length;j++){
							if($($('#authoritys input')[j]).attr('value')==data.data[i].id){
								$($('#authoritys input')[j]).prop('checked', true);
							}
						}
					}
				}
			}
		});
	});
	//提交为角色配置的权限选项
	$("#configsubmit").click(function(){
		var length = $('#authoritys input:checked').length,
			authorityIds1=[],
			authoritys=[],
			authorityIds;
			role_id=$('#myRole_id').text();
		for(var i=0;i<length;i++){
			var authority =$($('#authoritys input:checked')[i]).attr('value'),
			authority_id = parseInt(authority.split(",")[0]);
			authority_pid = parseInt(authority.split(",")[1]);
			authoritys.push(authority_id+","+authority_pid);
			authorityIds1.push(authority_id);
		}
		authorityIds = JSON.stringify(authorityIds1);
		$.ajax({
			async:true,
			cache:false,
			type:"post",
			url:"configRoleAuthoritys",
			data:{roleId:role_id,authorityIds:authorityIds},
			error:function(){
				msg.dialog("角色权限配置失败");
			},
			success:function(data){
				if(data.code==0){
					msg.light("角色权限配置成功",2);
					localStorage.setItem("menuses", authoritys);
				}else{
					msg.dialog("角色权限配置失败");
				}
			}
		});
	});
	/**
	 * 修改密码
	 */
	$("#modifyPwdComplete").click(function(){
		//判断密码规则
		var newPwd = $("#newPwd").val(),
			pattern=/^(([a-z]+[0-9]+)|([0-9]+[a-z]+))[a-z0-9]*$/i;
			id = $("#modifyPwd").data("id"),
			name =$("#modifyPwd").data("name");
		if(newPwd==""||newPwd==undefined){
			$("#newPwd").focus();
			msg.dialog("请输入密码");
			return;
		}
		if(!pattern.test(newPwd)){
			msg.dialog("密码必须为数字和字母组合");
			return;
		}
		TYZX.userMg.modifyPwd(id,name,newPwd);
		$("#modifyPwd").modal("hide");
	});
	$("#modifyPwdCancel").click(function(){
		$("#modifyPwd").modal("hide");
	});
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
};
(function(){
	TYZX.userMg.init();
	TYZX.userMg.bind();
}());