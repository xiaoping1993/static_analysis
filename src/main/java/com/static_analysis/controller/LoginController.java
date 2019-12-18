package com.static_analysis.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.service.LoginService;
import com.static_analysis.util.PasswordUtil;
import com.static_analysis.util.ResultUtil;

/**
 * 登陆
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {
	protected static final Logger log = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private LoginService loginService;

	/**
	 * 登陆
	 * @param request
	 * @param response
	 * @param name
	 * @param password
	 * @return
	 */
	@RequestMapping(params = "login")
	public Result checkLogin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "name") String name, @RequestParam(name = "password") String password) {
		return loginService.checkLogin(request, response, name, password);
	}
	/**
	 * 登陆内部调转
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(params = "toLogin")
	public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("/login/index");
	}
	/**
	 * 修改密码
	 * @param oldPw
	 * @param newPw
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "modifyPwd")
	public Result modifyPwd(String oldPw, String newPw, HttpServletRequest request) {
		String loginName = request.getSession().getAttribute("loginName").toString();
		try {
			String Pw = loginService.getUserPwByName(loginName);
			String oldPwd = PasswordUtil.encrypt(oldPw,loginName,PasswordUtil.getStaticSalt());// 加密得到旧密码
			String newPwd = PasswordUtil.encrypt(newPw,loginName,PasswordUtil.getStaticSalt());// 加密新密码
			if (!oldPwd.equals(Pw)) {
				return ResultUtil.error(ResultEnum.OLD_PASSWORD_ERROR.getCode(),
						ResultEnum.OLD_PASSWORD_ERROR.getMsg());
			} else {
				if (loginService.savePw(loginName, newPwd) != 0) {
					log.info("更新密码成功");
					return ResultUtil.success();
				} else {
					throw new RuntimeException();
				}
			}
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.MODIFY_PASSWORD_FAIL.getCode(),
					ResultEnum.MODIFY_PASSWORD_FAIL.getMsg());
		}
		
	}
	/**
	 * 刷脸登陆
	 * @param name
	 * @param faceimg
	 * @return
	 */
	@RequestMapping(params="faceLogin")
	public Result faceLogin(String name,MultipartFile faceimg){
		try {
			return loginService.faceLogin(name,faceimg);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
}
