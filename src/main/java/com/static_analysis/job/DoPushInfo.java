package com.static_analysis.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.static_analysis.mapper.tyzx.LoginMapper;
import com.static_analysis.service.DispatchStaService;
import com.static_analysis.service.MainService;
import com.static_analysis.service.UsingPeriodsService;
import com.static_analysis.util.HttpUtil;
import com.static_analysis.util.RedisUtil;

/**
 * 定时任务
 * @author wangjiping
 *
 */
@Component
public class DoPushInfo {
	@Autowired
	private MainService mainService;
	@Autowired
	private UsingPeriodsService usingPeriodsService;
	@Autowired
	private DispatchStaService dispatchService;
	private final Logger log = LoggerFactory.getLogger(DoPushInfo.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	@Value("${baiduai.APIKey}")
	private String baiduaiApiKey;
	@Value("${baiduai.SecretKey}")
	private String baiduaiSecretKey;
	@Value("${env}")
	private String env;
	@Autowired
	private LoginMapper loginMapper;
	/**
	 * 处理昨日推送消息（先不执行）
	 */
	//@Scheduled(cron="0 0 2 * * ?")
	//@Scheduled(cron="0/5 * * * * ? ")
	public void doPushInfo(){
		try {
			if(env.equals("prd1")){
				log.info("【pushInfo】【正式环境1不执行该任务】");
				return;
			}
			Date data = new Date(new Date().getTime()-24*60*60*1000);
			String yesterday =sdf.format(data);
			mainService.doPushInfo(yesterday);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
	/**
	 * 处理页面停留时长信息（先不执行）
	 */
	//@Scheduled(cron="0 0 2 * * ?")
	//@Scheduled(cron="0/5 * * * * ? ")
	public void doPageParkingtime(){
		try {
			if(env.equals("prd1")){
				log.info("【doPageParkingtime】【正式环境1不执行该任务】");
				return;
			}
			Date data = new Date(new Date().getTime()-24*60*60*1000);
			String yesterday =sdf.format(data);
			mainService.doPageParkingtime(yesterday);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
	/**
	 * 获得前一天的精确到10min的各个时段正在使用的用户数（这里的用户是指操作人loginname+mphoneimei）（先不执行）
	 */
	//@Scheduled(cron="0 0 2 * * ?")
	//@Scheduled(cron="0/5 * * * * ? ")
	public void doUserUsedtimestamp(){
		try {
			if(env.equals("prd1")){
				log.info("【doPageParkingtime】【正式环境1不执行该任务】");
				return;
			}
			Date data = new Date(new Date().getTime()-24*60*60*1000);
			String yesterday =sdf.format(data);
			usingPeriodsService.doUserUsedtimestamp(yesterday);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
	/**
	 * 获得昨日app中台错误日志信息（先不执行）
	 */
	//@Scheduled(cron="0 0 2 * * ?")
	//@Scheduled(cron="0/5 * * * * ? ")
	public void doErrorLog(){
		try {
			Date data = new Date(new Date().getTime()-24*60*60*1000);
			String yesterday =sdf.format(data);
			mainService.doErrorLog(yesterday);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
	/**
	 * 定制百度AIacessToken
	 */
	@Scheduled(cron="0 0 0 20 * ?")
	//@Scheduled(cron="0/5 * * * * ?")
	public void refreshbaiduaiAccesstoken(){
		if(env.equals("prd1")){
			log.info("【doPageParkingtime】【正式环境1不执行该任务】");
			return;
		}
		String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id="+baiduaiApiKey+"&client_secret="+baiduaiSecretKey;
		String baiduAIaccess_token =JSONObject.parseObject(HttpUtil.get(url)).getString("access_token") ;
		//不存redis了麻烦我直接写到数据库了
		//boolean flag = redisUtil.set("baiduAIaccess_token", baiduAIaccess_token);
		int flag = loginMapper.setAccessToken(baiduAIaccess_token,"baiduAIAccessToken");
		if(flag>0){
			log.info("新百度AIacessToken存储成功");
		}else{
			log.error("新百度AIacessToken存储失败");
		}
	}
	/**
	 * 统计拉车工具昨日一天的客户使用次数（先不执行）
	 */
	//@Scheduled(cron="0 0 2 * * ?")
	public void doLarryToolSta(){
		try {
			if(env.equals("prd1")){
				log.info("【doLarryToolSta】【正式环境1不执行该任务】");
				return;
			}
			Date data = new Date(new Date().getTime()-24*60*60*1000);
			String yesterday =sdf.format(data);
			dispatchService.doLarryToolSta(yesterday);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
}
