package com.static_analysis.Enum;
/**
 * app名称枚举
 * @author wangjiping
 *
 */
public enum AppNameEnum {
	
	tianyizx("天易在线",1,"tyzx"),
	tianyipg("天易派工",2,"typg"),
	weixinlc("微信小工具",3,"wxlc");
	
	AppNameEnum(String name,Integer code,String shortName) {
        this.code = code;
        this.name = name;
        this.shortName = shortName;
    };
    private String name;
	private Integer code;
	private String shortName;
	public String getName(){
		return name;
	}
	public Integer getCode(){
		return code;
	}
	public String getShortName(){
		return shortName;
	}
}
