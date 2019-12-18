package com.static_analysis.Enum;
/**
 * http请求返回的code对应值
 * @author wangjiping
 *
 */
public enum ResultEnum {
	UNKONW_ERROR(-1, "未知错误"),
    SUCCESS(0, "成功"),
	LOGIN_NAME_NOEXSIT(100,"登陆名不存在"),
	LOGIN_PASSWORD_FAIL(101,"登陆密码错误"),
	MODIFY_PASSWORD_FAIL(102,"密码修改失败"),
	OLD_PASSWORD_ERROR(103,"原密码错误"),
	RESET_PASSWORD_FAIL(104,"重置密码失败"),
	DELE_USER_FAIL(105,"删除用户失败"),
	LOGIN_NAME_HASEXIST(106,"用户名已存在"),
	OverCidsLength(107,"客户id超过限制长度10,请重新选中客户"),
	DismissData(108,"没有此数据"),
	Updatafailed(109,"数据更新失败"), 
	ClientLoginFailure(110,"客户端登陆失效请重新登陆"), 
	EmptyData(111,"此条件下没有数据"), 
	AddFailure(112,"数据添加失败"), DeleFailure(113,"数据珊瑚失败"), FORBID_DELE_YOURSERLF(113,"禁止删除本人账号"), 
	UnqualifiedPhoto(114,"照片不合格"),
	ISNotOwnLogin(115,"可能不是本人登陆"),
	ISNotRegister(116,"此用户可能没有注册"), 
	HasNotFaceRegister(117,"没有人脸注册过"), 
	ISNOTfaceliveness(118,"非活体照片请重新拍摄"), 
	ISNOTRegisiter(119,"非注册刷脸用户，请注册后再使用此功能"),
	FaceRegisterFail(120,"人脸注册失败");
    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
