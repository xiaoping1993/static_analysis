package com.static_analysis.service.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.static_analysis.Enum.AppNameEnum;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.mapper.tyzx.UsingPeriodsMapper;
import com.static_analysis.service.UsingPeriodsService;
import com.static_analysis.util.ResultUtil;

@Service
public class UsingPeriodsServiceImpl implements UsingPeriodsService{
	protected final static Logger log = LoggerFactory.getLogger(UsingPeriodsServiceImpl.class);
	@Autowired
	private UsingPeriodsMapper usingPeriodsMapper;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void doUserUsedtimestamp(String data) {
		try {
			List<String> group_ids = new ArrayList<>();
			for (Integer i = 0; i < 144; i++) {
				if(!group_ids.add(i.toString())){
					log.error("间隔时间没有插入成功");
				};
			}
			log.info("间隔数据插入完成");
			//获得usersums:一天中每隔10min使用软件用户数
			for (int i = 0; i < AppNameEnum.values().length; i++) {
				Integer appcode = AppNameEnum.values()[i].getCode();
				Map<String,Object> map = new HashMap<>();
				map.put("data", data);
				map.put("group_ids", group_ids);
				map.put("appname", appcode);
				String usersums = usingPeriodsMapper.getUsingPeriodsInfo10min(map);
				Integer count = usingPeriodsMapper.saveUsingPeriodsInfo(usersums,data,appcode);
				if(count>0){
					log.info("日期："+data+"，软件："+AppNameEnum.values()[i].getName()+"：用户使用时段信息成功插入"+count+"条记录");
				}else{
					log.error("日期："+data+"，软件："+AppNameEnum.values()[i].getName()+"用户使用时段信息数据插入失败");
				}
			}
			//查找插入全部appName
			Map<String,Object> map = new HashMap<>();
			map.put("data", data);
			map.put("group_ids", group_ids);
			map.put("appname", -1);
			String usersums = usingPeriodsMapper.getUsingPeriodsInfo10min(map);
			Integer count = usingPeriodsMapper.saveUsingPeriodsInfo(usersums,data,-1);
			if(count>0){
				log.info("日期："+data+"，所有软件用户使用时段信息成功插入"+count+"条记录");
			}else{
				log.error("日期："+data+"，所有软件用户使用时段信息数据插入失败");
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
	@Override
	public Result getUsingPeriodsInfo(String appName, String timeParticle, String start, String end) {
		try {
			//这里做的查询都是今天都是没有数据的
			start = (start.equals("null"))?sdf.format(new Date(new Date().getTime()-24*60*60*1000)):start;
			end = (end.equals("null"))?sdf.format(new Date(new Date().getTime()-24*60*60*1000)):end;
			appName = appName.equals("all")?"-1":appName;
			List<String> result = usingPeriodsMapper.getUsingPeriodsInfo(appName,start,end);
			//将指定app对应时间点用时长相加
			Integer length = result.size();
			List<Integer> resultReturn = new ArrayList<>();
			if(length==0){
				//没有数据
				log.info("此条件下没有数据");
				return ResultUtil.error(ResultEnum.EmptyData.getCode(), ResultEnum.EmptyData.getMsg());
			}else if(length==1){//只有一条数据
				resultReturn.clear();
				String[] string = result.get(0).split(",");
				for (String string2 : string) {
					resultReturn.add(Integer.parseInt(string2));
				}
			}else{//有多条数据
				resultReturn.clear();
				String[] string3 = result.get(0).split(",");
				for (String string2 : string3) {
					resultReturn.add(Integer.parseInt(string2));
				}
				for (int j = 1; j < length; j++) {
					List<Integer> resultOther = new ArrayList<>();
					String[] string = result.get(j).split(",");
					for (int k = 0; k < string.length; k++) {
						resultOther.add(Integer.parseInt(string[k])+resultReturn.get(k));
					}
					resultReturn = resultOther;
				}
			}
			return ResultUtil.success(listAddInside(resultReturn,Integer.parseInt(timeParticle)/10));
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * List集合内部相隔n位相加
	 * @param list
	 * @return
	 */
	private List<Integer> listAddInside(List<Integer> list,Integer n) {
        List<Integer> list2 = new LinkedList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            if (i % n == 0) {
                list2.add(list.get(i));
            } else {
                list2.set(i / n, list2.get(i / n) + list.get(i));
            }
        }
        return list2;
    }
}
