package com.static_analysis.service.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.mapper.tyzx.InfoMgMapper;
import com.static_analysis.mapper.tyzx.UserMgMapper;
import com.static_analysis.service.UserMgService;
import com.static_analysis.util.Base64Util;
import com.static_analysis.util.HttpUtil;
import com.static_analysis.util.MathUtil;
import com.static_analysis.util.PasswordUtil;
import com.static_analysis.util.RedisUtil;
import com.static_analysis.util.ResultUtil;

@Service
public class UserMgServiceImpl implements UserMgService{

	protected static final Logger log = LoggerFactory.getLogger(UserMgServiceImpl.class);
	@Autowired
	private UserMgMapper userMgMapper;
	@Autowired
	private InfoMgMapper infoMgMapper;
	@Autowired
	private RedisUtil redisUtil;
	@Value("${baiduai.FaceGroup_id}")
	private String group_id;
	@Override
	public List<Map<String, Object>> findAllUser(int start, int end) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> obj =  userMgMapper.findAllUser(start,end);
		return obj;
	}

	@Override
	public int getAllUserCounts() {
		return userMgMapper.getAllUserCounts();
	}

	@Override
	public int resetPwd(Integer userId,String userName) {
		String defaultPwd = PasswordUtil.encrypt("12345678",userName,  PasswordUtil.getStaticSalt());
		return userMgMapper.resetPwd(userId,defaultPwd);
	}

	@Override
	@Transactional
	public int deleUser(Integer userId) {
		try {
			//删除一个用户还需将用户角色表中对应的数据删除
			userMgMapper.deleUserRole(userId);
			//删除user的
			Integer count = userMgMapper.deleUser(userId);
			return count;
		} catch (Exception e) {
			log.error(e.toString());
			return 0;
		}
		
	}

	@Override
	public List<Map<String, Object>> getRoles() {
		return userMgMapper.getRoles();
	}

	@Override
	public List<Integer> getRoleIds(Integer userId) {
		return userMgMapper.getRoleIds(userId);
	}
	
	@Override
	public Integer insertUserRole(Integer id,Integer userId) {
		return userMgMapper.insertUserRole(id, userId);
	}
	
	@Override
	public Integer insertUserRoles(List<Integer> ids, Integer userId) {
		Integer count=0;
		for (Integer id : ids) {
			Integer count1 = userMgMapper.insertUserRole(id, userId);
			count+=count1;
		}
		log.debug("成功插入"+count+"条数据");
		return count;
	}

	@Override
	public String getUserIdByUserName(String userName) {
		Integer userId = userMgMapper.getUserIdByUserName(userName);
		return userId.toString();
	}

	@Override
	public List<Map<String,Object>> getAllUserNoRole() {
		List<Map<String,Object>> users = userMgMapper.getAllUserNoRole();
		return users;
	}

	@Override
	public List<Map<String,Object>> getAuthoritys() {
		return userMgMapper.getAuthoritys();
	}

	@Override
	public List<Map<String, Object>> getAuthoritysByRole(String roleId) {
		if (roleId.equals("")){
			return null;
		}
		return userMgMapper.getAuthoritysByRole(roleId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public boolean configRoleAuthoritys(String roleId, String[] authorityIds,HttpServletRequest request) {
		try {
			//删除角色所有的权限
			Integer count1 = userMgMapper.deleRoleAllAuthoritys(roleId);
			log.info("删除角色权限数："+count1);
			Integer count=0;
			//配置新的角色权限
			for (String authorityId : authorityIds) {
				count+=userMgMapper.insertRoleAuthoritys(roleId,authorityId);
			}
			//更新session中存储的menus_ids
			HttpSession session = request.getSession();
			String loginName = session.getAttribute("loginName").toString();
			String menus_ids = infoMgMapper.getMenus_idsByLoginName(loginName)+",";
			session.setAttribute("menus_ids", menus_ids);
			log.info("添加角色权限数："+count);
			return true;
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}

	@Override
	public Result configsRoles(String rows, String userId, String userName) {
		try {
			if(userId==null||userId==""&&userName!=null&&userName!=""){//这里是添加新用户,主要将添加过的用户userId替换下
				//判断账号名是否已存在
				if(userMgMapper.existUser(userName)!=0){
					return ResultUtil.error(ResultEnum.LOGIN_NAME_HASEXIST.getCode(), ResultEnum.LOGIN_NAME_HASEXIST.getMsg());
				}
				//添加账号到user表中
				String pwd = PasswordUtil.encrypt("12345678",userName, PasswordUtil.getStaticSalt());//默认密码12345678
				userId = (userMgMapper.addUser(userName,pwd)>0? userMgMapper.getUserIdByUserName(userName).toString():"-1");
				if(userId.equals("-1")){
					log.error("账户添加失败");
				}else{
					log.info("账户添加成功");
				}
			}
			JSONArray json = JSON.parseArray(rows);
			Iterator<Object> it = json.iterator();
			userMgMapper.deleUserRole(Integer.parseInt(userId));//删除之前的角色
			List<Integer> ids = new ArrayList<>();
			while(it.hasNext()){
				//将每个更新保存到表中
				JSONObject row = (JSONObject)it.next();
				Integer id = (Integer)row.get("id");
				ids.add(id);
			}
			insertUserRoles(ids,Integer.parseInt(userId));//添加最新的角色
			return ResultUtil.success();
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}

	@Override
	public Result addRole(String roleName,String roleDescribe) {
		Integer count = userMgMapper.addRole(roleName,roleDescribe);
		if(count==0){
			return ResultUtil.error(ResultEnum.AddFailure.getCode(), ResultEnum.AddFailure.getMsg());
		}else{
			return ResultUtil.success();
		}
		
	}

	@Override
	public Result deleRole(String roleId) {
		Integer count = userMgMapper.deleRole(roleId);
		if(count==0){
			return ResultUtil.error(ResultEnum.DeleFailure.getCode(), ResultEnum.DeleFailure.getMsg());
		}else{
			return ResultUtil.success();
		}
	}

	@Override
	public Result modifyPwd(String id, String pwd) {
		Integer count = userMgMapper.modifyPwd(id,pwd);
		if(count==0){
			return ResultUtil.error(ResultEnum.Updatafailed.getCode(), ResultEnum.Updatafailed.getMsg());
		}else{
			return ResultUtil.success();
		}
	}
	/**
	 * 根据MultipartFile类型数据获得base64编码方式的字符串
	 * @param img
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private String getBase64StringByMultipartFile(MultipartFile img) throws IOException, UnsupportedEncodingException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();//用来存放imgbyte值
		InputStream content = img.getInputStream();
		byte[] buffer = new byte[100];
		int rc = 0;
		while((rc=content.read(buffer,0,100))>0){
			swapStream.write(buffer,0,rc);
		}
		//获得二进制数
		byte[] in2b = swapStream.toByteArray();
		String imgStr = Base64Util.encode(in2b);
		String imgParam = URLEncoder.encode(imgStr,"UTF-8");
		return imgParam;
	}
	@Override
	public Result registerPhoto(MultipartFile faceimg, String userid,String username) {
		// 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v2/faceset/user/add";
		try {
			String imgParam = getBase64StringByMultipartFile(faceimg);
			String accessToken = redisUtil.get("baiduAIaccess_token");
			String baiduaiAccessToken = redisUtil.get("baiduAIaccess_token");
			String qualityurl = "https://aip.baidubce.com/rest/2.0/face/v1/detect";
			String qualityparam = "face_fields=pitch,roll,yaw,location,qualities&image="+imgParam;
			String msg = isQualityOK(baiduaiAccessToken, qualityurl, qualityparam);
			if(msg.equals("ok")){//照片质量合格后再进行人脸注册
				String param = "uid=" + userid + "&user_info=" + username + "&group_id=" + group_id + "&images=" + imgParam;
	            String result = HttpUtil.post(url, accessToken, param);
	            Object error_code = JSONObject.parseObject(result).get("error_code");
	            if(error_code==null){//注册成功
	            	//更新用户表hasfaceregister字段
	            	Integer count = userMgMapper.updateUserForhasfaceregister(userid,1);
	            	if(count>0){
	            		return ResultUtil.success();
	            	}else{
	            		return ResultUtil.error(ResultEnum.Updatafailed.getCode(),ResultEnum.Updatafailed.getMsg());
	            	}
	            }else{//失败
	            	return ResultUtil.error(ResultEnum.FaceRegisterFail.getCode(), ResultEnum.FaceRegisterFail.getMsg());
	            }
			}else{
				return ResultUtil.error(ResultEnum.UnqualifiedPhoto.getCode(), ResultEnum.UnqualifiedPhoto.getMsg()+":"+msg);
			}
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException();
        }
	}
	/**
	 * 检查照片质量是否合格
	 * @param baiduaiAccessToken
	 * @param qualityurl
	 * @param param
	 * @return 不合格描述，合格描述词
	 * @throws Exception
	 */
	private String isQualityOK(String baiduaiAccessToken, String qualityurl, String param) throws Exception {
		JSONObject quailties =JSONObject.parseObject(HttpUtil.post(qualityurl, baiduaiAccessToken, param));
		if(quailties.getInteger("result_num")==0){
			return "nopersons";
		}
		JSONObject quailtiesDete =  quailties.getJSONArray("result").getJSONObject(0);
		JSONObject location = quailtiesDete.getJSONObject("location");
		if(location.getInteger("height")<100){
			return "人脸高度小于100px";
		}
		if(MathUtil.AccuracyCalcDiv(location.getInteger("height"),330, 2)<0.33){
			return "用户活体检测时人脸高度与照片高度比例小了";
		}
		if(location.getInteger("width")<100){
			return "人脸宽度小于100px";
		}
		if(MathUtil.AccuracyCalcDiv(location.getInteger("width"),300, 2)<0.33){
			return "用户活体检测时人脸宽度与照片宽度比例小了";
		}
		if(Math.abs(quailtiesDete.getDouble("pitch"))>20){
			return "三维旋转之俯仰角度大于了20";
		}
		if(Math.abs(quailtiesDete.getDouble("roll"))>20){
			return "平面内旋转角度大于了20";
		}
		if(Math.abs(quailtiesDete.getDouble("yaw"))>20){
			return "三维旋转之左右旋转角大于了20";
		}
		JSONObject qualities = quailtiesDete.getJSONObject("qualities");
		if(qualities.getFloat("blur")>0.7){
			return "人脸模糊了";
		}
		/*if(qualities.getInteger("illumination")<40){
			return "人脸光照不好";
		}*/
		JSONObject occlusion = qualities.getJSONObject("occlusion");
		if(occlusion.getFloat("left_eye")>0.6){
			return "左眼被遮挡严重";
		}
		if(occlusion.getFloat("right_eye")>0.6){
			return "右眼被遮挡严重";
		}
		if(occlusion.getFloat("nose")>0.6){
			return "鼻子眼被遮挡严重";
		}
		if(occlusion.getFloat("mouth")>0.6){
			return "嘴巴被遮挡严重";
		}
		if(occlusion.getFloat("left_cheek")>0.6){
			return "左脸被遮挡严重";
		}
		if(occlusion.getFloat("right_cheek")>0.6){
			return "右脸被遮挡严重";
		}
		if(occlusion.getFloat("chin")>0.6){
			return "下巴被遮挡严重";
		}
		return "ok";
	}
}
