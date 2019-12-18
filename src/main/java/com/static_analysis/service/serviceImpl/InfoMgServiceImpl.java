package com.static_analysis.service.serviceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.static_analysis.entity.ComboTree;
import com.static_analysis.mapper.tyzx.InfoMgMapper;
import com.static_analysis.service.InfoMgService;

@Service
public class InfoMgServiceImpl implements InfoMgService {

	protected static final Logger log = LoggerFactory.getLogger(InfoMgServiceImpl.class);
	@Autowired
	private InfoMgMapper infoMgMapper;
	@Override
	public List<Map<String, Object>> getAllMenus(int start,int end) {
		List<Map<String, Object>> allMenus = infoMgMapper.getAllMenus(start,end);
		return allMenus;
	}
	@Override
	public int getAllMenusCount() {
		Integer count = infoMgMapper.getAllMenusCount();
		return count;
	}
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public boolean menusSave(HttpServletRequest request,Integer order,Integer id,Integer pid, String name, String url,Boolean isEdit) {
		Integer count;
		try {
			if(isEdit){//修改
				Integer firstOrder =Integer.parseInt(infoMgMapper.getMenusById(id).get("order").toString()) ;
				boolean isAsc =order > firstOrder;
				count = infoMgMapper.menusUpdate(id,pid,name,url,order);
				if(count>0){
					log.info("更新成功"+count+"条记录");
					//将表中order重新处理下
					resetMenusForOrder(isAsc);
					//重置session中的menus_ids
					resetSession(request);
					return true;
				}else{
					return false;
				}
			}else{//新增
				count = infoMgMapper.menusInsert(pid,name,url,order);
				if(count>0){
					log.info("新增成功"+count+"条记录");
					//将表中order重新排序处理下
					resetMenusForOrder(false);
					resetSession(request);
					return true;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			log.error(e.toString());
			return false;
		}
	}
	/**
	 * 重置session中的值（menus_ids）
	 * @param request
	 */
	private void resetSession(HttpServletRequest request){
		HttpSession session = request.getSession();
		String menus_ids = "";
		List<Map<String,Object>> allMenus =  infoMgMapper.getAllMenus(0, 100000000);
		for (Map<String, Object> map : allMenus) {
			menus_ids+=map.get("id").toString()+",";
		}
		session.setAttribute("menus_ids", menus_ids);
	}
	/**
	 * 将表中order重新排序处理下
	 */
	private void resetMenusForOrder(Boolean asc) {
		if(asc){
			List<Map<String,Object>> allMenus =  infoMgMapper.getAllMenus(0, 100000000);
			Integer length = allMenus.size();
			for(Integer i = 0;i<=length-1;i++ ){
				infoMgMapper.menusUpdate(Integer.parseInt(allMenus.get(i).get("id").toString()), Integer.parseInt(allMenus.get(i).get("pid").toString()),(allMenus.get(i).get("name")==null)?null:allMenus.get(i).get("name").toString(), (allMenus.get(i).get("url")==null)?null:allMenus.get(i).get("url").toString(), (i+1)*5);
			}
			log.info("顺序对menus表中order字段已做处理");
		}else{
			List<Map<String,Object>> allMenus =  infoMgMapper.getAllMenus(0, 100000000);
			Integer length = allMenus.size();
			for(Integer i = length-1;i>=0;i-- ){
				infoMgMapper.menusUpdate(Integer.parseInt(allMenus.get(i).get("id").toString()), Integer.parseInt(allMenus.get(i).get("pid").toString()),(allMenus.get(i).get("name")==null)?null:allMenus.get(i).get("name").toString(), (allMenus.get(i).get("url")==null)?null:allMenus.get(i).get("url").toString(), (i+1)*5);
			}
			log.info("逆序对menus表中order字段已做处理");
		}
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public boolean menusDele(Integer id,HttpServletRequest request) {
		try {
			Integer count = infoMgMapper.menusDele(id);
			if(count>0){
				log.info("成功删除记录："+count+"条");
				resetMenusForOrder(true);
				resetSession(request);
				return true;
			}else{return false;}
		} catch (Exception e) {
			log.error(e.toString());
			return false;
		}
	}
	@Override
	public List<ComboTree> getAllmenusJSONTree(String id) {
		List<ComboTree> result = new ArrayList<>();
		List<Map<String,Object>> list = new ArrayList<>();
		if(id==null){
			Map<String,Object> root = new HashMap<>();
			Integer rootChilds = infoMgMapper.getAllmenusJSONTree("0").size();
			root.put("childs", rootChilds);
			root.put("id", 0);
			root.put("name", "根菜单");
			root.put("order", 0);
			list.add(root);
		}else{
			list = infoMgMapper.getAllmenusJSONTree(id);
		}
		for (Map<String, Object> map : list) {
			ComboTree comboTreeTemp = new ComboTree();
			comboTreeTemp.setId(String.valueOf(map.get("id")));
			comboTreeTemp.setText(String.valueOf(map.get("name")));
			Map<String,Object> attribute = new HashMap<>();
			attribute.put("order",map.get("order"));
			comboTreeTemp.setAttributes(attribute);
			if("0".equals(String.valueOf(map.get("childs")))){
				comboTreeTemp.setState("open");
			}else{
				comboTreeTemp.setState("closed");
				List<ComboTree> children = getAllmenusJSONTree(map.get("id").toString());
				comboTreeTemp.setChildren(children);
			}
			result.add(comboTreeTemp);
		}
		return result;
	}
	
}
