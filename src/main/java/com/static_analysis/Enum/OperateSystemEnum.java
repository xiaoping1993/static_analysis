package com.static_analysis.Enum;
/**
 * 操作系统enum
 * @author wangjiping
 *
 */
public enum OperateSystemEnum {
	
	ios("ios",1),
	android("android",2),
	wp("wp",3);
	
	OperateSystemEnum(String name,Integer code) {
        this.code = code;
        this.name = name;
    };
    private String name;
	private Integer code;
	public String getName(){
		return name;
	}
	public Integer getCode(){
		return code;
	}
}
