package com.static_analysis.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.service.ExceptionInfoMgService;
import com.static_analysis.util.ResultUtil;
/**
 * 异常信息管理
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("exceptionInfoMg")
public class ExceptionInfoMgController {
	@Autowired
	private ExceptionInfoMgService exceptionInfoService;
	/*
	 * @Value("${ftpUrl}") private String ftpUrl;
	 * 
	 * @Value("${ftpPort}") private Integer ftpPort;
	 * 
	 * @Value("${ftpPwd}") private String ftpPwd;
	 * 
	 * @Value("${ftpUserName}") private String ftpUserName;
	 * 
	 * @Value("${errorFilesPath}") private String errorFilesPath;
	 */
	protected final static Logger log = LoggerFactory.getLogger(ExceptionInfoMgController.class);
	/**
	 * 获得异常详细信息
	 * @param appName
	 * @param state
	 * @param start
	 * @param end
	 * @param ids
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("getExceptionInfo")
	public JSONObject getExceptionInfo(String appName,String state,String start,String end,String ids,int page,int rows){
		try {
			Integer limitstart = (page-1)*rows;
			Integer limitend = rows;
			//有ids就不管其他限制条件
			return exceptionInfoService.getExceptionInfo(appName,state,start,end,ids,limitstart,limitend);
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	/**
	 * 处理异常问题
	 * @param id
	 * @param state
	 * @param reasons
	 * @param resolvemethod
	 * @param resolvePersons
	 * @return
	 */
	@PostMapping("doProblem")
	public Result resolveProblem(String id,String state,String reasons,String resolvemethod,String resolvePersons){
		try {
			return exceptionInfoService.doProblem(id,state,reasons,resolvemethod,resolvePersons);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * 获得异常类型数据
	 * @param appName
	 * @param state
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("getExceptionInfoByType")
	public JSONObject getExceptionInfoByType(String appName,String state,String start,String end){
		try {
		    return exceptionInfoService.getExceptionInfoByType(appName,state,start,end);
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	/**
	 * 获得奔溃信息
	 * @param appName
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("getMeltModelsInfo")
	public Result getMeltModelsInfo(String appName,String start,String end){
		try {
			return exceptionInfoService.getMeltModelsInfo(appName,start,end);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * 下载app奔溃文件
	 * 
	 * @param fileName
	 * @param response
	 */
	/*
	 * @RequestMapping("downAppErrorFiles") public void downAppErrorFiles(String
	 * fileName,HttpServletResponse response){ response.setHeader("content-type",
	 * "application/octet-stream");
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
	 * BufferedInputStream bis = null; OutputStream os = null; byte[] buffer = new
	 * byte[1024]; Integer length = 0; try { //获得ftp上errorFiles的指定文件流 InputStream is
	 * =
	 * FtpUtil.downFileInputStream(ftpUrl,ftpPort,ftpUserName,ftpPwd,errorFilesPath,
	 * fileName); if(is!=null){ bis = new BufferedInputStream(is); os =
	 * response.getOutputStream(); while((length=bis.read(buffer))!=-1){
	 * os.write(buffer,0,length); os.flush(); } log.info("文件下载成功"); } } catch
	 * (Exception e) { log.error(e.toString()); }finally { if (bis != null) { try {
	 * bis.close(); } catch (IOException e) { e.printStackTrace(); } } } }
	 */
}
