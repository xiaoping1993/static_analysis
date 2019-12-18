package com.static_analysis.service.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.AppNameEnum;
import com.static_analysis.Enum.OperateSystemEnum;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.mapper.tyzx.LoginMapper;
import com.static_analysis.service.LoginService;
import com.static_analysis.util.Base64Util;
import com.static_analysis.util.HttpUtil;
import com.static_analysis.util.MathUtil;
import com.static_analysis.util.PasswordUtil;
import com.static_analysis.util.RedisUtil;
import com.static_analysis.util.ResultUtil;

@Service
public class LoginServiceImpl implements LoginService{
	@Autowired
	private LoginMapper loginMapper;
	protected static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);
	@Value("serverurl")
	private String serverurl;
	@Autowired
	private RedisUtil redisUtil;
	@Value("${baiduai.FaceGroup_id}")
	private String group_id;
	@Override
	public String getUserPwByName(String loginName) {
		return loginMapper.getUserPwByName(loginName);
	}

	@Override
	public Integer savePw(String loginName, String newPw) {
		return loginMapper.savePw(loginName,newPw);
	}

	@Override
	public Result checkLogin(HttpServletRequest request,HttpServletResponse response,String name, String password) {
		try {
			//1.判断用户是否存在
			if(loginMapper.checkUser(name)==0){
				return ResultUtil.error(ResultEnum.LOGIN_NAME_NOEXSIT.getCode(), ResultEnum.LOGIN_NAME_NOEXSIT.getMsg());
			}
			//2.存在的用户密码是否正确：正确返回
			String pwd = PasswordUtil.encrypt(password, name, PasswordUtil.getStaticSalt());//加密密码
			List<Map<String,Object>> userInfos1 = loginMapper.getLogin(name,pwd);
			if(userInfos1.isEmpty()){
				return ResultUtil.error(ResultEnum.LOGIN_PASSWORD_FAIL.getCode(),ResultEnum.LOGIN_PASSWORD_FAIL.getMsg());
			}
			//过滤userInfos1:将没有权限的去掉
			List<Map<String,Object>> userInfos2 = new ArrayList<>();
			String menus_ids = "";
			for (Map<String, Object> map : userInfos1) {
				if(map.get("menus_id")!=null){
					Map<String, Object> userInfo = new HashMap<String, Object>();
					userInfo.put("name", name);
					userInfo.put("role_id", map.get("role_id"));
					userInfo.put("menus", map.get("menus_id")+","+map.get("menus_pid"));//存了menus_id和其自身属性menus_pid
					userInfos2.add(userInfo);
					menus_ids += map.get("menus_id")+",";
				}
			}
			JSONArray userInfos = new JSONArray();
			userInfos.addAll(userInfos2);
			//将用户信息存入session中
			HttpSession session = request.getSession();//存用户信息
			session.setAttribute("loginName", name);
			session.setAttribute("menus_ids", menus_ids);
			JSONObject params =  getConditionParams();
			return ResultUtil.success(params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}

	@Override
	public Result faceLogin(String loginName, MultipartFile img) {
		//检测图片质量
		try {
			//先判断此用户是否已注册人脸登陆
			Map<String,Object> userinfo = loginMapper.getUserInfoByName(loginName);
			if(userinfo==null){
				return ResultUtil.error(ResultEnum.LOGIN_NAME_NOEXSIT.getCode(), ResultEnum.LOGIN_NAME_NOEXSIT.getMsg());
			}
			if(userinfo.get("hasfaceregister").toString().equals("0")){
				return ResultUtil.error(ResultEnum.HasNotFaceRegister.getCode(), loginName +ResultEnum.HasNotFaceRegister.getMsg()+"请注册过再使用人脸登陆功能");
			}
			String imgParam = getBase64StringByMultipartFile(img);
			//String baiduaiAccessToken = redisUtil.get("baiduAIaccess_token");
			String baiduaiAccessToken = loginMapper.getAccessToken("baiduAIAccessToken");
			String qualityurl = "https://aip.baidubce.com/rest/2.0/face/v1/detect";
			String qualityparam = "face_fields=pitch,roll,yaw,location,qualities&image="+imgParam;
			String msg = isQualityOK(baiduaiAccessToken, qualityurl, qualityparam);
			if(msg.equals("ok")){//照片质量合格后再进行人脸识别
				String faceidentifurl = "https://aip.baidubce.com/rest/2.0/face/v2/identify";
				String faceidentiparam = "group_id="+group_id+"&user_top_num=1&face_top_num=1&ext_fields=faceliveness&images="+imgParam;
				JSONObject faceidenti = JSONObject.parseObject(HttpUtil.post(faceidentifurl, baiduaiAccessToken, faceidentiparam));
				String error_msg = faceidenti.getString("error_msg");
				Integer error_code = faceidenti.getInteger("error_code");
				if(error_msg!=null&&error_code!=null){
					return ResultUtil.error(error_code, error_msg);
				}
				String faceliveness = faceidenti.getJSONObject("ext_info").getString("faceliveness");
				if(Double.parseDouble(faceliveness)<0.775454){
					return ResultUtil.error(ResultEnum.ISNOTfaceliveness.getCode(), ResultEnum.ISNOTfaceliveness.getMsg());
				}
				JSONObject user = faceidenti.getJSONArray("result").getJSONObject(0);
				//?还有对Score做处理，如果分数很低，返回此人没有注册的错误
				String score =user.getJSONArray("scores").getString(0);
				if(Double.parseDouble(score)<70){
					return ResultUtil.error(ResultEnum.ISNOTRegisiter.getCode(), ResultEnum.ISNOTRegisiter.getMsg());
				}
				String uuId = user.getString("uid");
				Map<String,Object> userInfo = loginMapper.getUserInfoByName(loginName);
				Integer userId = Integer.parseInt(userInfo.get("id").toString());
				if(uuId.equals(userId.toString())){
					String passwordE = userInfo.get("password").toString();
					String passwordY = PasswordUtil.decrypt(passwordE,loginName,PasswordUtil.getStaticSalt());
					return ResultUtil.success(passwordY);
				}else{
					return ResultUtil.error(ResultEnum.ISNotOwnLogin.getCode(), ResultEnum.ISNotOwnLogin.getMsg());
				}
			}else{
				return ResultUtil.error(ResultEnum.UnqualifiedPhoto.getCode(), ResultEnum.UnqualifiedPhoto.getMsg()+":"+msg); 
			}
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
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
	public JSONObject getConditionParams() {
		JSONObject result = new JSONObject();
		JSONArray appNames = new JSONArray();
		JSONArray operateSystems = new JSONArray();
		JSONArray appVersions = new JSONArray();
		//appName
		for(int i=0;i<AppNameEnum.values().length;i++){
			JSONObject appName = new JSONObject();
			appName.put("Code", AppNameEnum.values()[i].getCode());
			appName.put("Name", AppNameEnum.values()[i].getName());
			appName.put("ShortName", AppNameEnum.values()[i].getShortName());
			appNames.add(appName);
		}
		//systemOperate
		for(int i=0;i<OperateSystemEnum.values().length;i++){
			JSONObject operateSystem = new JSONObject();
			operateSystem.put("Code", OperateSystemEnum.values()[i].getCode());
			operateSystem.put("Name", OperateSystemEnum.values()[i].getName());
			operateSystems.add(operateSystem);
		}
		//appVersion
		//从数据库中实时查找
		List<String> versions = loginMapper.getAppVersions();
		for(int i=0;i<versions.size();i++){
			JSONObject appVersion = new JSONObject();
			appVersion.put("Code", versions.get(i));
			appVersion.put("Name", versions.get(i));
			appVersions.add(appVersion);
		}
		result.put("appNames", appNames);
		result.put("operateSystems", operateSystems);
		result.put("appVersions", appVersions);
		return result;
	}
}
