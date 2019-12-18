package com.static_analysis.service.serviceImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.AppNameEnum;
import com.static_analysis.entity.AjaxJson;
import com.static_analysis.mapper.tyzx.MainMapper;
import com.static_analysis.service.MainService;
import com.static_analysis.util.FtpUtil;
import com.static_analysis.util.HttpUtil;
import com.static_analysis.util.RedisUtil;

@Service
public class MainServiceImpl implements MainService{
	@Autowired
	private MainMapper mainMapper;
	@Autowired
	RedisUtil redisUtil;
	@Autowired
	protected final static Logger log = LoggerFactory.getLogger(MainServiceImpl.class);
	@Value("${config.gaode.key}")
	private String gaodeKey; 
	private String GaodeApiGeocode = "";
	@Value("${ftpUrl}")
	private String ftpUrl;
	@Value("${ftpPort}")
	private Integer ftpPort;
	@Value("${ftpUserName}")
	private String ftpUserName;
	@Value("${ftpPwd}")
	private String ftpPwd;
	@Value("${errorFilesPath}")
	private String errorFilesPath;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public List<Map<String, Object>> getMenusInfo(String menus_ids) {
		return mainMapper.getMenusInfo(menus_ids);
	}

	@Override
	public AjaxJson postLoginInfo(String mphone_imei, String mphone_brand,String mphone_mode, String version, String longitude,
			String latitude,String exception_msg,  String cid,String cidName,String loginName, Integer appName, Integer operateSystem) {
		//处理位置信息得到province_adcode,city_adcode,district_adcode这里获得的adcode都是区的其他可以往上自己推
		GaodeApiGeocode = "http://restapi.amap.com/v3/geocode/regeo?key="+gaodeKey+"&poitype=&radius=&extensions=base&batch=false&roadlevel=&location=";
		AjaxJson aj = new AjaxJson();
		GaodeApiGeocode = GaodeApiGeocode+longitude+","+latitude;
		String province_adcode="";
		String city_adcode="";
		String district_adcode="";
		String result = HttpUtil.get(GaodeApiGeocode);
		JSONObject addressComponent = JSONObject.parseObject(result).getJSONObject("regeocode").getJSONObject("addressComponent");
		district_adcode = addressComponent.getString("adcode");
		if(district_adcode.equals("[]")||district_adcode.length()!=6){
			aj.setSuccess(false);
			aj.setMsg(longitude+","+latitude+":高德地图无法获得这条位置信息，此次信息收集失败！");
			return aj;
		}
		province_adcode = district_adcode.substring(0,2)+"0000";
		if(district_adcode.charAt(3)=='0'&&district_adcode.charAt(2)=='0'){//没有市上级直接就是省
			city_adcode = "";
		}else{
			city_adcode = district_adcode.substring(0,4)+"00";
		}
		Date date = new Date();
		Timestamp time = new Timestamp(date.getTime());
		//将最终处理好的字段存入数据库中
		try {
			Map<String, Object> mphoneInfo = mainMapper.hasMphoneInfoByImei(mphone_imei);
			if(mphoneInfo==null){
				mainMapper.insertLoginInfo(mphone_imei,mphone_brand,mphone_mode,version,province_adcode,city_adcode,district_adcode,cid,cidName,loginName,appName,operateSystem,time,time);
			}else{
				//这里注释后每次主要客户端一登陆已有设备就跟新跟新时间
				//每列相同就不操作
				//if(!mphoneInfo.get("mphone_mode").toString().equals(mphone_mode)||!mphoneInfo.get("version").toString().equals(version)||!mphoneInfo.get("province_adcode").equals(province_adcode)||!mphoneInfo.get("city_adcode").equals(city_adcode)||!mphoneInfo.get("district_adcode").equals(district_adcode)||!mphoneInfo.get("loginName").equals(loginName)||!mphoneInfo.get("appName").equals(appName)||!mphoneInfo.get("operateSystem").equals(operateSystem)){
					Integer id =Integer.parseInt(mphoneInfo.get("id").toString());
					mainMapper.updateLoginInfo(mphone_brand,mphone_mode,version,province_adcode,city_adcode,district_adcode,cid,cidName,loginName,appName,operateSystem,time,id);
				//}
			}
			aj.setSuccess(true);
			aj.setMsg("数据收集成功");
			return aj;
		} catch (Exception e) {
			log.error(e.toString());
			aj.setSuccess(false);
			aj.setMsg("数据收集失败");
			return aj;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public AjaxJson postPushMessageInfo(String appName, String warnType,String messageId, String reciptTime) {
		AjaxJson aj = new AjaxJson();
		aj.setSuccess(false);
		try {
			String date = sdf.format(new Date());
			String key = "";
			if(warnType==null||warnType==""){
				key = appName+"/"+date+"/";
			}else{
				key = appName+"/"+date+"/"+warnType;
			}
			//用来存放当天推送点击消息明细信息
			Map<String,Set<String>> map = new HashMap<>();
			if(!redisUtil.exists(key)){
				Set<String> set = new HashSet<>();
				set.add(messageId);
				map.put(reciptTime, set);
			}else{
				map = redisUtil.getMap(key);
				if(map.containsKey(reciptTime)){//包含这个接收时间
					Set<String> set = map.get(reciptTime);
					set.add(messageId);
					map.put(reciptTime, set);
				}else{//不包含这个接收时间
					Set<String> set = new HashSet<>();
					set.add(messageId);
					map.put(reciptTime, set);
				}
			}
			//将处理好的信息存入redis中
			redisUtil.setMap(key, map);
			log.info(key+":"+map.toString());
			aj.setMsg("数据收集成功");
			aj.setSuccess(true);
			return aj;
		} catch (Exception e) {
			log.error(e.toString());
			aj.setMsg("数据收集失败");
			return aj;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void doPushInfo(String time){
		try {
			for (int i = 0; i < AppNameEnum.values().length; i++) {
				String appName = AppNameEnum.values()[i].getShortName();
				String appCode = AppNameEnum.values()[i].getCode().toString();
				//获得昨天的推送数据
				String pushSumKey = "push"+appName+time;
				String pushSumValue ="0";
				if(redisUtil.exists(pushSumKey)){
					pushSumValue = redisUtil.get(pushSumKey);
					redisUtil.remove(pushSumKey);
				}
				//获得昨天的推送点击数据
				String clickSumKey = appCode+"/"+time+"/";
				String clickSumValue = "0";
				if(appName.equals("tyzx")){//天易在线
					List<String> warnTypes = mainMapper.getWarnTypes();
					for (String warnType : warnTypes) {//处理天易在线报警类型数据
						String key =appCode +"/"+time+"/"+warnType;
						if(redisUtil.exists(key)){
							Map<String,Set<String>> map = redisUtil.getMap(key);
							for (String reciptTime : map.keySet()){
								//天意在线报警信息不管是昨天还是之前的推送数据都存入数据库中
								Integer total = map.get(reciptTime).size();
								Map<String,Object> warnclickInfo = mainMapper.getWarnClickInfo(appCode,warnType,reciptTime);
								if(warnclickInfo==null){
									mainMapper.saveWarnClickInfo(appCode,warnType,total.toString(),reciptTime);
								}else{
									String clickSum = warnclickInfo.get("clickSum").toString();
									String id = warnclickInfo.get("id").toString();
									Integer clickSum1 = total+Integer.parseInt(clickSum);
									mainMapper.updataWarnClickInfo(clickSum1,id);
								}
								if(reciptTime.equals(time)){//昨天推送点击数据中接收推送消息的时间为昨天的
									Integer count = map.get(reciptTime).size();
									Integer sum = count+Integer.parseInt(clickSumValue);
									clickSumValue = sum.toString();
								}else{//前几天的....这里有待完善地方：不在每个报警类型更新数据而将各个有报警数据相加后再一次更新
									Map<String,Object> pushstatisticsInfo = mainMapper.getPushstatistics(appCode,reciptTime);
									String clicktotals = pushstatisticsInfo.get("clicktotals").toString();
									Integer totals = map.get(reciptTime).size()+Integer.parseInt(clicktotals);
									mainMapper.updataPushstatistics(totals,appCode,reciptTime);
								}
							}
							redisUtil.remove(key);
						}
					}
				}else{//其他软件
					if(redisUtil.exists(clickSumKey)){
						Map<String,Set<String>> map = redisUtil.getMap(clickSumKey);
						for (String reciptTime : map.keySet()){
							if(reciptTime.equals(time)){//昨天推送点击数据中接收推送消息的时间为昨天的
								Integer count = map.get(reciptTime).size();
								Integer sum = count+Integer.parseInt(clickSumValue);
								clickSumValue = sum.toString();
							}else{//前几天的
								Map<String,Object> pushstatisticsInfo = mainMapper.getPushstatistics(appCode,reciptTime);
								String clicktotals = pushstatisticsInfo.get("clicktotals").toString();
								Integer totals = map.get(reciptTime).size()+Integer.parseInt(clicktotals);
								mainMapper.updataPushstatistics(totals,appCode,reciptTime);
							}
						}
						redisUtil.remove(clickSumKey);
					}
				}
				mainMapper.savePushInfo(appCode,pushSumValue,clickSumValue,time);
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	@Override
	public AjaxJson postPageInfo(String pageCode,String mphone_imei, String appName, String loginName,String cid,String cidName) {
		AjaxJson result = new AjaxJson();
		result.setSuccess(false);
		String time =sdf1.format(new Date());
		//当appName,pageCode,cid都为""时，需要将其变为null才能成功插入数据库开始
		appName = (appName==""?null:appName);
		pageCode = (pageCode==""?null:pageCode);
		cid = (cid==""?null:cid);
		//当appName,pageCode,cid都为""时，需要将其变为null才能成功插入数据库结束
		try {
			Integer count =  mainMapper.savePageInfo(loginName,cid,cidName,mphone_imei,appName,pageCode,time);
			if(count>0){
				result.setSuccess(true);
				return result;
			}else{
				result.setMsg("appName:"+appName+","+"pageCode:"+pageCode+"time:"+time+"cid:"+cid+"cidName:"+cidName+"数据没有收集成功");
				return result;
			}
		} catch (Exception e) {
			log.error(e.toString());
			result.setMsg("操作失败！");
			return result;
		}
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void doPageParkingtime(String time) {
		try {
		Integer sum = 0;
		Map<String,Long> parktingtimeSum = new HashMap<String,Long>();//存停留时间汇总后数据
		//获得昨天所有数据
		List<Map<String,Object>> result = mainMapper.getParkingTimeDetail(time);
		for (int i = 0; i < result.size()-1; i++) {
			Map<String,Object> map1 = result.get(i);
			Map<String,Object> map2 = result.get(i+1);
			if(!map2.get("loginname").equals(map1.get("loginname"))||!map2.get("mphone_imei").equals(map1.get("mphone_imei"))||!map2.get("appname").equals(map1.get("appname"))){//下一个键与上一个不同此时不做处理
				continue;
			}else{
				if(!map1.get("pagecode").toString().equals("-1")&&!map1.get("pagecode").toString().equals("-2")){//正常使用软件
					Date date1= (Date) sdf1.parseObject(map1.get("time").toString());
					Date date2= (Date) sdf1.parseObject(map2.get("time").toString());
					long interval = (date2.getTime()-date1.getTime())/1000;
					String key = map1.get("loginname")+","+map1.get("appname")+","+(map1.get("cid")==null?"0":map1.get("cid"))+","+(map1.get("cidname")==null?"旧数据无客户名":map1.get("cidname"))+","+map1.get("mphone_imei")+","+map1.get("pagecode");
					if(parktingtimeSum.containsKey(key)){
						parktingtimeSum.put(key,interval+parktingtimeSum.get(key));
					}else{
						parktingtimeSum.put(key,interval);
					}
				}
			}
		}
		//将汇总数据存入库
		Iterator iter = parktingtimeSum.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String,Long> entry = (Map.Entry) iter.next();
			String key = entry.getKey().toString();
			String[] keys = key.split(",");
			String loginName = keys[0];
			String appName = keys[1];
			String cid = keys[2];
			String cidName = keys[3];
			String mphone_imei = keys[4];
			String pageCode = keys[5];
			Long value = Long.parseLong(entry.getValue().toString());
			Integer count = mainMapper.saveParkingtime(pageCode,value,time,mphone_imei,loginName,cid,cidName);
			if(count==0){
				log.error(loginName+","+appName+","+mphone_imei+","+pageCode+time+":数据没有插入成功");
				throw new RuntimeException();
			}
			sum+=count;
		}
		log.info("成功插入"+sum+"条数据");
		} catch (ParseException e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}

	@Override
	public AjaxJson postExceptionInfo(String mphone_imei, String appName,String type, String exception_msg, String exception_detailmsgString, MultipartFile exception_detailmsgFile) {
		AjaxJson aj = new AjaxJson();
		aj.setSuccess(false);
		String date = sdf.format(new Date());
		try {
			//将File存入指定文件夹，同时获得对应地址url
			String fileName = null;
			if(exception_detailmsgFile!=null){
				fileName = date+appName+mphone_imei+exception_detailmsgFile.getOriginalFilename();
				InputStream fileInput = exception_detailmsgFile.getInputStream();
				boolean blag = FtpUtil.uploadFile(ftpUrl, ftpPort, ftpUserName,ftpPwd,errorFilesPath, fileName, fileInput);
				if(blag){
					log.info(fileName+"移动端错误日志文件保存成功。");
				}
				
				/*String errorDir = getClass().getClassLoader().getResource("public/errorFiles").toString().substring(6);
				String target = errorDir+"/"+fileName;
				File dest = new File(target);
		        // 检测是否存在目录
		        if (!dest.getParentFile().exists()) {
		            dest.getParentFile().mkdirs();
		        }
				exception_detailmsgFile.transferTo(dest);*/
				//如果异常概述获取文件中前50字作为异常概述
				if(exception_msg.equals("")||exception_msg==null){
					InputStream inputStream =  exception_detailmsgFile.getInputStream();
					byte[] buff = new byte[250];
					inputStream.read(buff);
					exception_msg = new String(buff,"utf-8");
				}
			}
			//将信息存入数据库中
			Integer count = mainMapper.saveExceptionInfo(appName,mphone_imei,type,exception_msg,exception_detailmsgString,fileName,date);
			if(count==0){
				aj.setSuccess(false);
				aj.setMsg("数据存入失败！");
				return aj;
			}
			log.info("存入数量："+count);
			aj.setSuccess(true);
			return aj;
		} catch (IOException e) {
			log.error(e.toString());
			aj.setMsg("操作失败");
			return aj;
		}
	}

	@Override
	public void doErrorLog(String yesterday) {
		Integer sum = 0;
		StringBuffer sb = new StringBuffer();
		String errorString = "";
		String fileName = "app_errorlog."+yesterday+".log";
		BufferedReader br=null;
		//通过相对tomcat路径查找服务端错误日志文件
		String localLogsPath = System.getProperty("catalina.home")+"/logs/";
		File file = new File(localLogsPath+fileName);
		if(!file.exists()){//判断指定文件是否存在
			log.info(localLogsPath+fileName+"文件不存在，收集昨日服务端错误日志定时任务执行中断!");
			return;
		}
		try {
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			String buffer;
			while((buffer = br.readLine())!=null){
				sb.append(buffer);
			}
			errorString = sb.toString();
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String pattern = "(\\[ERROR\\])(\\[.*?\\])(\\[.*?\\])(\\[.*?\\])([\\s\\S]*?)(?=end\\$\\$)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(errorString);
		while(m.find()){
			String time = m.group(2).substring(1,m.group(2).length()-1);
			String appName = m.group(3).substring(1,m.group(3).length()-1);
			if(appName.equals("天易在线")){
				appName="4";
			}else if(appName.equals("天易派工")){
				appName="5";
			}else{
				appName="-1";
			};
			String exception_msg = m.group(4).substring(1,m.group(4).length()-1);
			String exception_msgDetail = m.group(5).substring(1,m.group(5).length()-1);
			//将信息存入数据库中
			Integer count = mainMapper.saveExceptionInfo(appName,null,null,exception_msg,exception_msgDetail,null,time);
			sum+=count;
			log.info("存入数量："+count);
		}
		//获得远程服务器上的对应错误日志文件内容
		log.info("成功插入"+yesterday+"app中台错误信息"+sum+"条");
	}
}
